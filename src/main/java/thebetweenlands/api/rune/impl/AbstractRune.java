package thebetweenlands.api.rune.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nullable;

import io.netty.buffer.Unpooled;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.api.aspect.AspectContainer;
import thebetweenlands.api.aspect.IAspectType;
import thebetweenlands.api.rune.INode;
import thebetweenlands.api.rune.INodeBlueprint;
import thebetweenlands.api.rune.INodeComposition;
import thebetweenlands.api.rune.INodeCompositionBlueprint.INodeLink;
import thebetweenlands.api.rune.INodeConfiguration;
import thebetweenlands.api.rune.IRuneChainUser;
import thebetweenlands.api.rune.impl.RuneChainComposition.RuneExecutionContext;

public abstract class AbstractRune<T extends AbstractRune<T>> implements INode<T, RuneExecutionContext> {

	public static abstract class Blueprint<T extends AbstractRune<T>> implements INodeBlueprint<T, RuneExecutionContext> {
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
		public abstract List<RuneConfiguration> getConfigurations();

		@Override
		public void run(T state, RuneExecutionContext context, INodeIO io) {
			RuneStats stats = this.getStats();
			Aspect cost = stats.getAspect();

			if(this.updateFuelConsumption(state, context, io, stats, cost.type, cost.amount)) {
				RuneEffectModifier.Subject subject = this.activate(state, context, io);

				//Activate modifier
				state.initRuneEffectModifier();
				RuneEffectModifier effect = state.getRuneEffectModifier();
				if(effect != null) {
					effect.activate(context.getUser(), subject);

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
				if(context.getInputIndex() == context.getInputIndexCount() - 1 && context.getBranch() == context.getBranchCount() - 1) {
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
		public void terminate(T state, RuneExecutionContext context) {
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
		protected void drainLeftOverFuel(T state, RuneExecutionContext context) {
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
		protected boolean updateFuelConsumption(T state, RuneExecutionContext context, @Nullable INodeIO io, RuneStats stats, IAspectType type, int cost) {
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
		protected abstract RuneEffectModifier.Subject activate(T state, RuneExecutionContext context, INodeIO io);

		/**
		 * Creates a rune effect modifier to be applied to a previous rune linked to this rune
		 * @param target previous rune to apply the rune effect modifier to
		 * @param output output index of the target rune
		 * @param input input index of this rune that the target rune is linked to
		 * @return
		 */
		@Nullable
		protected RuneEffectModifier createRuneEffectModifier(AbstractRune<?> target, int output, int input) {
			return null;
		}

		/**
		 * Creates a rune effect modifier to be applied to only this rune itself
		 * @return
		 */
		@Nullable
		protected RuneEffectModifier createRuneEffectModifier() {
			return null;
		}

		/**
		 * Writes a rune effect modifier subject to a packet to be synced over the network
		 * @param subject subject to write to the packet
		 * @param buffer packet buffer to write to
		 */
		protected void writeRuneEffectModifierSubject(@Nullable RuneEffectModifier.Subject subject, PacketBuffer buffer) {
			buffer.writeBoolean(subject != null);

			if(subject != null) {
				Entity entity = subject.getEntity();

				Vec3d vector = subject.getPosition();
				buffer.writeBoolean(entity == null && vector != null);
				if(entity == null && vector != null) {
					InputSerializers.VECTOR.write(vector, buffer);
				}

				BlockPos block = subject.getBlock();
				buffer.writeBoolean(block != null);
				if(block != null) {
					InputSerializers.BLOCK.write(block, buffer);
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
		 * @param input node input that allows reading input values that were present when the sender's {@link AbstractRune.Blueprint#activate(AbstractRune, RuneExecutionContext, thebetweenlands.api.rune.INodeBlueprint.INodeIO)} was called.
		 * Values may not always be serialized over the network and could thus be null even though they were present on the sender's side.
		 * @param buffer packet buffer to read from
		 * @return
		 * @throws IOException
		 */
		@Nullable
		protected RuneEffectModifier.Subject readRuneEffectModifierSubject(T state, IRuneChainUser user, INodeInput input, PacketBuffer buffer) throws IOException {
			if(buffer.readBoolean()) {
				Vec3d position = null;
				if(buffer.readBoolean()) {
					position = InputSerializers.VECTOR.read(user, buffer);
				}

				BlockPos block = null;
				if(buffer.readBoolean()) {
					block = InputSerializers.BLOCK.read(user, buffer);
				}

				Entity entity = null;
				if(buffer.readBoolean()) {
					entity = InputSerializers.ENTITY.read(user, buffer);
				}

				return new RuneEffectModifier.Subject(position, block, entity);
			}

			return null;
		}

		/**
		 * Processes a packet sent through {@link RuneExecutionContext#sendPacket(java.util.function.Consumer, net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint)}.
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
				state.getRuneEffectModifier().activate(user, this.readRuneEffectModifierSubject(state, user, new INodeInput() {
					@Override
					public Object get(int input) {
						return inputs.get(input);
					}
				}, subjectBuffer));

				break;
			case 1: //Terminate effect
				//TODO Terminate

			}
		}
	}

	private final Blueprint<T> blueprint;
	private final INodeComposition<RuneExecutionContext> composition;
	private final RuneConfiguration configuration;
	private final int index;

	private boolean runeEffectModifierSet = false;
	private RuneEffectModifier runeEffectModifier = null;

	protected int bufferedCost = 0;

	protected AbstractRune(Blueprint<T> blueprint, int index, INodeComposition<RuneExecutionContext> composition, RuneConfiguration configuration) {
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
	public final INodeComposition<RuneExecutionContext> getComposition() {
		return this.composition;
	}

	@Override
	public int getIndex() {
		return this.index;
	}

	/**
	 * Initializes the rune effect modifier applied to this rune. See {@link #getRuneEffectModifier()}.
	 */
	protected void initRuneEffectModifier() {
		if(!this.runeEffectModifierSet) {
			this.runeEffectModifierSet = true;

			List<RuneEffectModifier> effects = new ArrayList<>();

			Deque<Integer> queue = new LinkedList<>();

			queue.add(this.index);

			RuneEffectModifier effect = this.getBlueprint().createRuneEffectModifier();
			if(effect != null) {
				effects.add(effect);
			}

			if(this.blueprint.recursiveRuneEffectModifierCount > 0) {
				effectsLoop: while(!queue.isEmpty()) {
					int startIndex = queue.removeFirst();

					for(int runeIndex = startIndex + 1; runeIndex < this.composition.getBlueprint().getNodeBlueprints(); runeIndex++) {
						INode<?, RuneExecutionContext> node = this.composition.getNode(runeIndex);

						if(node instanceof AbstractRune) {
							AbstractRune<?> rune = (AbstractRune<?>) node;

							for(int linkIndex : this.composition.getBlueprint().getLinkedSlots(runeIndex)) {
								INodeLink link = this.composition.getBlueprint().getLink(runeIndex, linkIndex);

								if(link != null && link.getNode() == startIndex) {
									queue.add(runeIndex);

									effect = rune.getBlueprint().createRuneEffectModifier(this, link.getNode(), linkIndex);
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

			if(!effects.isEmpty()) {
				this.runeEffectModifier = new RuneEffectModifier() {
					@Override
					public void activate(IRuneChainUser user, RuneEffectModifier.Subject subject) {
						for(RuneEffectModifier effect : effects) {
							effect.activate(user, subject);
						}
					}

					@Override
					public void update(IRuneChainUser user) {
						for(RuneEffectModifier effect : effects) {
							effect.update(user);
						}
					}

					@Override
					public int getColorModifier() {
						int r = 0;
						int g = 0;
						int b = 0;
						int a = 0;

						int count = 0;

						for(RuneEffectModifier effect : effects) {
							if(effect.hasColorModifier()) {
								int color = effect.getColorModifier();

								r += (color >> 16) & 255;
								g += (color >> 8) & 255;
								b += color & 255;
								a += (color >> 24) & 255;

								count++;
							}
						}

						return count > 0 ? (((a / count) << 24) | ((r / count) << 16) | ((g / count) << 8) | (b / count)) : 0xFFFFFFFF;
					}

					@Override
					public int getColorModifier(int index) {
						if(index < 0 || index > effects.size()) {
							return 0xFFFFFFFF;
						}

						int currentIndex = 0;

						for(RuneEffectModifier effect : effects) {
							if(effect.hasColorModifier()) {
								if(currentIndex == index) {
									return effect.getColorModifier();
								}

								currentIndex++;
							}
						}

						return 0xFFFFFFFF;
					}

					@Override
					public boolean hasColorModifier() {
						for(RuneEffectModifier effect : effects) {
							if(effect.hasColorModifier()) {
								return true;
							}
						}
						return false;
					}
				};
			}
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
