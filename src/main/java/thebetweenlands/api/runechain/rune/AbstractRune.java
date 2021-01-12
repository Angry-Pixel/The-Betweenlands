package thebetweenlands.api.runechain.rune;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nullable;

import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.api.aspect.AspectContainer;
import thebetweenlands.api.aspect.IAspectType;
import thebetweenlands.api.runechain.IRuneChainUser;
import thebetweenlands.api.runechain.base.IConfigurationLinkAccess;
import thebetweenlands.api.runechain.base.INode;
import thebetweenlands.api.runechain.base.INodeBlueprint;
import thebetweenlands.api.runechain.base.INodeComposition;
import thebetweenlands.api.runechain.base.INodeIO;
import thebetweenlands.api.runechain.base.INodeInput;
import thebetweenlands.api.runechain.base.INodeLink;
import thebetweenlands.api.runechain.chain.IRuneExecutionContext;
import thebetweenlands.api.runechain.initiation.InitiationPhase;
import thebetweenlands.api.runechain.initiation.InitiationState;
import thebetweenlands.api.runechain.io.InputSerializers;
import thebetweenlands.api.runechain.io.types.StaticBlockTarget;
import thebetweenlands.api.runechain.io.types.StaticVectorTarget;
import thebetweenlands.api.runechain.modifier.CompoundRuneEffectModifier;
import thebetweenlands.api.runechain.modifier.RuneEffectModifier;
import thebetweenlands.api.runechain.modifier.Subject;

public abstract class AbstractRune<T extends AbstractRune<T>> implements INode<T, IRuneExecutionContext> {

	public static abstract class Blueprint<T extends AbstractRune<T>> implements INodeBlueprint<T, IRuneExecutionContext> {
		private final RuneStats stats;

		private int recursiveRuneEffectModifierCount = 0;

		public Blueprint(RuneStats stats) {
			this.stats = stats;
		}

		public RuneStats getStats() {
			return this.stats;
		}

		protected void setRecursiveRuneEffectModifierCount(int count) {
			this.recursiveRuneEffectModifierCount = count;
		}

		@Override
		public abstract List<RuneConfiguration> getConfigurations(@Nullable IConfigurationLinkAccess linkAccess, boolean provisional);

		@Override
		public void run(T state, IRuneExecutionContext context, INodeIO io) {
			RuneStats stats = this.getStats();
			Aspect cost = stats.getAspect();

			if(this.updateFuelConsumption(state, context, io, stats, cost.type, cost.amount)) {
				Subject subject = this.activate(state, context, io);

				//Activate modifier
				state.initRuneEffectModifier();
				RuneEffectModifier effect = state.getRuneEffectModifier();
				if(effect != null && effect.activate(state, context.getUser(), subject)) {
					//Sync activation over network
					//TODO Batch activations together
					PacketBuffer inputsBuffer = new PacketBuffer(Unpooled.buffer());
					state.getConfiguration().serialize(context.getUser(), io, inputsBuffer);

					PacketBuffer subjectBuffer = new PacketBuffer(Unpooled.buffer());
					this.writeRuneEffectModifierSubject(subject, subjectBuffer);

					context.sendPacket(buffer -> {
						buffer.writeVarInt(0);
						synchronized(inputsBuffer) {
							buffer.writeVarInt(inputsBuffer.writerIndex());
							buffer.writeBytes(inputsBuffer, inputsBuffer.writerIndex());
							inputsBuffer.resetReaderIndex();
						}
						synchronized(subjectBuffer) {
							buffer.writeVarInt(subjectBuffer.writerIndex());
							buffer.writeBytes(subjectBuffer, subjectBuffer.writerIndex());
							subjectBuffer.resetReaderIndex();
						}
					}, null);
				}

				// On last parallel rune activation on last branch drain any left over fuel to be consumed
				if(context.getInputIndex() == context.getInputIndexCount() - 1 && context.getBranchIndex() == context.getBranchIndexCount() - 1) {
					this.drainLeftOverFuel(state, context);
				}

				// Schedule sleep task to wait out duration if no other task was scheduled
				if(io.scheduled() == null && ((io.failed() && stats.getFailDuration() > 0) || (!io.failed() && stats.getSuccessDuration() > 0))) {
					io.schedule(scheduler -> {
						scheduler.sleep(io.failed() ? stats.getFailDuration() : stats.getSuccessDuration());
						scheduler.terminate();
					});
				}
			} else {
				io.fail();
				io.terminate();
			}
		}

		@Override
		public void terminate(T state, IRuneExecutionContext context) {
			this.drainLeftOverFuel(state, context);

			RuneEffectModifier effect = state.getRuneEffectModifier();
			if(effect != null) {
				effect.terminate();

				//Sync termination over network
				context.sendPacket(buffer -> {
					buffer.writeVarInt(1);
				}, null);
			}
		}

		/**
		 * Drains any left over buffered fuel to be consumed and if bufferCost < costRatio drains at least 1 more aspect.
		 * @param state - node instance created by create(INodeComposition, INodeConfiguration)
		 * @param context - context that is executing the node
		 */
		protected void drainLeftOverFuel(T state, IRuneExecutionContext context) {
			// If some buffered cost is left drain everything and if bufferCost < costRatio drain at least 1 more aspect
			if(state.bufferedCost > 0) {
				RuneStats stats = this.getStats();
				this.updateFuelConsumption(state, context, null, stats, stats.getAspect().type, state.bufferedCost < stats.getCostRatio() ? stats.getCostRatio() : 0);
			}
			state.bufferedCost = 0;
		}

		/**
		 * Updates the fuel consumption and drains aspect from the container if necessary.
		 * @param state - node instance created by create(INodeComposition, INodeConfiguration)
		 * @param context - context that is executing the node
		 * @param io - node input/output that allow reading input values and writing output values. May be null if no
		 * I/O is available while updating fuel consumption
		 * @param stats - rune stats of this blueprint
		 * @param type - aspect type to drain
		 * @param cost - amount of aspect to drain
		 * @return <i>true</i> if the cost was successfully drained, <i>false</i> otherwise
		 */
		protected boolean updateFuelConsumption(T state, IRuneExecutionContext context, @Nullable INodeIO io, RuneStats stats, IAspectType type, int cost) {
			AspectContainer container = context.getAspectBuffer().get(type);

			state.bufferedCost += cost;

			if(state.bufferedCost >= stats.getCostRatio()) {
				int drain = Math.floorDiv(state.bufferedCost, stats.getCostRatio());

				state.bufferedCost %= stats.getCostRatio();

				return container.drain(type, drain) >= drain;
			}

			return true;
		}

		/**
		 * Called when the rune is activated.
		 * @param state - node instance created by {@link #create(INodeComposition, INodeConfiguration)}
		 * @param context - context that is executing the node
		 * @param io - node input/output that allow reading input values and writing output values
		 * @return subject to be affected by the rune effect modifier to be applied to this rune ({@link AbstractRune#getRuneEffectModifier()}), e.g. a projectile or position
		 */
		@Nullable
		protected abstract Subject activate(T state, IRuneExecutionContext context, INodeIO io);

		/**
		 * Creates a rune effect modifier to be applied to a target rune (recursively) linked to this rune
		 * @param state - node instance created by {@link #create(INodeComposition, INodeConfiguration)}
		 * @param target target rune to apply the rune effect modifier to
		 * @param ioRune a rune that is linked to this rune or is delegating this rune
		 * @param ioIndex input of this rune that is linked to the passed in rune or the output of this rune that the passed in rune is linked to
		 * @return
		 */
		@Nullable
		protected RuneEffectModifier createRuneEffectModifier(T state, AbstractRune<?> target, AbstractRune<?> ioRune, int ioIndex) {
			return null;
		}

		/**
		 * Creates a rune effect modifier to be applied to only this rune itself
		 * @param state - node instance created by {@link #create(INodeComposition, INodeConfiguration)}
		 * @return
		 */
		@Nullable
		protected RuneEffectModifier createRuneEffectModifier(T state) {
			return null;
		}

		/**
		 * Returns whether this rune delegates the rune effect modifier of one of its runes it's linked to
		 * @param state - node instance created by {@link #create(INodeComposition, INodeConfiguration)}
		 * @param target target rune to apply the rune effect modifier to
		 * @param outputRune another rune this rune is linked to
		 * @param inputIndex input of this rune that is linked to the outputRune
		 * @return whether this rune delegates the rune effect modifier of one of its runes it's linked to
		 */
		protected boolean isDelegatingRuneEffectModifier(T state, AbstractRune<?> target, AbstractRune<?> outputRune, int inputIndex) {
			return false;
		}

		/**
		 * Returns whether this rune's rune effect modifier should be delegated by runes that are linked to this rune
		 * @param state - node instance created by {@link #create(INodeComposition, INodeConfiguration)}
		 * @param target target rune to apply the rune effect modifier to
		 * @param inputRune another rune that is linked to this rune
		 * @param outputIndex output of this rune that the inputRune is linked to
		 * @return whether this rune delegates the rune effect modifier of one of its runes it's linked to
		 */
		protected boolean isDelegatedRuneEffectModifier(T state, AbstractRune<?> target, AbstractRune<?> inputRune, int outputIndex) {
			return false;
		}

		/**
		 * Writes a rune effect modifier subject to a packet to be synced over the network
		 * @param subject subject to write to the packet
		 * @param buffer packet buffer to write to
		 */
		protected void writeRuneEffectModifierSubject(@Nullable Subject subject, PacketBuffer buffer) {
			buffer.writeBoolean(subject != null);

			if(subject != null) {
				Entity entity = subject.getEntity();

				Vec3d vector = subject.getPosition();
				buffer.writeBoolean(entity == null && vector != null);
				if(entity == null && vector != null) {
					InputSerializers.VECTOR.write(new StaticVectorTarget(vector), buffer);
				}

				BlockPos block = subject.getBlock();
				buffer.writeBoolean(block != null);
				if(block != null) {
					InputSerializers.BLOCK.write(new StaticBlockTarget(block), buffer);
				}

				buffer.writeBoolean(entity != null);
				if(entity != null) {
					InputSerializers.ENTITY.write(entity, buffer);
				}
			}
		}

		/**
		 * Reads and creates the rune effect modifier subject from a packet synced over the network
		 * @param state node instance created by {@link #create(INodeComposition, INodeConfiguration)}
		 * @param user user that activated the rune chain
		 * @param input node input that allows reading input values that were present when the sender's {@link AbstractRune.Blueprint#activate(AbstractRune, IRuneExecutionContext, thebetweenlands.api.runechain.base.INodeBlueprint.INodeIO)} was called.
		 * Values may not always be serialized over the network and could thus be null even though they were present on the sender's side.
		 * @param buffer packet buffer to read from
		 * @return
		 * @throws IOException
		 */
		@Nullable
		protected Subject readRuneEffectModifierSubject(T state, IRuneChainUser user, INodeInput input, PacketBuffer buffer) throws IOException {
			if(buffer.readBoolean()) {
				Vec3d position = null;
				if(buffer.readBoolean()) {
					position = InputSerializers.VECTOR.read(user, buffer).vec();
				}

				BlockPos block = null;
				if(buffer.readBoolean()) {
					block = InputSerializers.BLOCK.read(user, buffer).block();
				}

				Entity entity = null;
				if(buffer.readBoolean()) {
					entity = InputSerializers.ENTITY.read(user, buffer);
				}

				return new Subject(position, block, entity);
			}

			return null;
		}

		/**
		 * Processes a packet sent through {@link IRuneExecutionContext#sendPacket(java.util.function.Consumer, net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint)}.
		 * @param state node instance created by {@link #create(INodeComposition, INodeConfiguration)}
		 * @param user user that activated the rune chain
		 * @param buffer packet buffer to read from
		 * @throws IOException
		 */
		public void processPacket(T state, IRuneChainUser user, PacketBuffer buffer) throws IOException {
			switch(buffer.readVarInt()) {
			case 0: //Activate effect
				PacketBuffer inputsBuffer = new PacketBuffer(Unpooled.buffer());
				buffer.readBytes(inputsBuffer, buffer.readVarInt());

				PacketBuffer subjectBuffer = new PacketBuffer(Unpooled.buffer());
				buffer.readBytes(subjectBuffer, buffer.readVarInt());

				List<Object> inputs = state.getConfiguration().deserialize(user, inputsBuffer);

				state.initRuneEffectModifier();
				state.getRuneEffectModifier().activate(state, user, this.readRuneEffectModifierSubject(state, user, input -> inputs.get(input), subjectBuffer));

				break;
			case 1: //Terminate effect
				RuneEffectModifier modifier = state.getRuneEffectModifier();
				if(modifier != null) {
					modifier.terminate();
				}
			}
		}

		@Nullable
		public InitiationState<T> checkInitiation(IRuneChainUser user, InitiationPhase initiationPhase, @Nullable InitiationState<T> initiationState) {
			return null;
		}
	}

	private final Blueprint<T> blueprint;
	private final INodeComposition<IRuneExecutionContext> composition;
	private final RuneConfiguration configuration;
	private final int index;

	private boolean runeEffectModifierSet = false;
	private RuneEffectModifier runeEffectModifier = null;

	protected int bufferedCost = 0;

	protected AbstractRune(Blueprint<T> blueprint, int index, INodeComposition<IRuneExecutionContext> composition, RuneConfiguration configuration) {
		this.blueprint = blueprint;
		this.index = index;
		this.composition = composition;
		this.configuration = configuration;
	}

	@Override
	public final Blueprint<T> getBlueprint() {
		return this.blueprint;
	}

	@Override
	public final RuneConfiguration getConfiguration() {
		return this.configuration;
	}

	@Override
	public final INodeComposition<IRuneExecutionContext> getComposition() {
		return this.composition;
	}

	@Override
	public int getIndex() {
		return this.index;
	}

	/**
	 * Initializes the rune effect modifier applied to this rune. See {@link #getRuneEffectModifier()}.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void initRuneEffectModifier() {
		if(!this.runeEffectModifierSet) {
			this.runeEffectModifierSet = true;

			List<RuneEffectModifier> effects = new ArrayList<>();

			IntSet checked = new IntOpenHashSet();
			Deque<Integer> queue = new LinkedList<>();

			queue.add(this.index);

			RuneEffectModifier effect = ((AbstractRune.Blueprint) this.getBlueprint()).createRuneEffectModifier(this);
			if(effect != null) {
				effects.add(effect);
			}

			if(this.blueprint.recursiveRuneEffectModifierCount > 0) {
				effectsLoop: while(!queue.isEmpty()) {
					int startIndex = queue.removeFirst();
					AbstractRune<?> startRune = (AbstractRune<?>) this.composition.getNode(startIndex);

					for(int runeIndex = startIndex + 1; runeIndex < this.composition.getBlueprint().getNodeBlueprints(); runeIndex++) {
						if(!checked.contains(runeIndex)) {
							INode<?, IRuneExecutionContext> node = this.composition.getNode(runeIndex);

							if(node instanceof AbstractRune) {
								AbstractRune<?> rune = (AbstractRune<?>) node;

								for(int linkIndex : this.composition.getBlueprint().getLinkedSlots(runeIndex)) {
									INodeLink link = this.composition.getBlueprint().getLink(runeIndex, linkIndex);

									if(link != null && link.getNode() == startIndex) {
										queue.add(runeIndex);
										checked.add(runeIndex);

										effect = ((AbstractRune.Blueprint) rune.getBlueprint()).createRuneEffectModifier(rune, this, startRune, linkIndex);
										if(effect != null) {
											effects.add(effect);

											if(effects.size() >= this.blueprint.recursiveRuneEffectModifierCount) {
												break effectsLoop;
											}
										}
									}
								}
							}
						}
					}

					if(startIndex != this.index) {
						for(int linkIndex : this.composition.getBlueprint().getLinkedSlots(startIndex)) {
							INodeLink link = this.composition.getBlueprint().getLink(startIndex, linkIndex);

							if(link != null) {
								INode<?, IRuneExecutionContext> node = this.composition.getNode(link.getNode());

								if(node instanceof AbstractRune) {
									AbstractRune<?> rune = (AbstractRune<?>) node;

									if(checked.add(link.getNode()) && (((AbstractRune.Blueprint) rune.getBlueprint()).isDelegatedRuneEffectModifier(rune, this, startRune, link.getOutput()) || ((AbstractRune.Blueprint) startRune.getBlueprint()).isDelegatingRuneEffectModifier(startRune, this, rune, linkIndex))) {
										queue.add(link.getNode());

										effect = ((AbstractRune.Blueprint) rune.getBlueprint()).createRuneEffectModifier(rune, this, (AbstractRune<?>) this.composition.getNode(startIndex), link.getOutput());
										if(effect != null) {
											effects.add(effect);

											if(effects.size() >= this.blueprint.recursiveRuneEffectModifierCount) {
												break effectsLoop;
											}
										}
									}
								}
							}
						}
					}
				}
			}

			if(!effects.isEmpty()) {
				this.runeEffectModifier = new CompoundRuneEffectModifier(effects);
			};
		}
	}

	/**
	 * Returns the rune effect modifier to be applied to this rune. If multiple rune effect modifiers are available they are combined into
	 * one compound rune effect modifier.
	 * @return
	 */
	@Nullable
	public RuneEffectModifier getRuneEffectModifier() {
		return this.runeEffectModifier;
	}
}
