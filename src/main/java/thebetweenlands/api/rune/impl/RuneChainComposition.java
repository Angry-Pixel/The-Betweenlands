package thebetweenlands.api.rune.impl;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.network.PacketBuffer;
import thebetweenlands.api.aspect.AspectContainer;
import thebetweenlands.api.aspect.IAspectType;
import thebetweenlands.api.rune.INode;
import thebetweenlands.api.rune.INodeBlueprint;
import thebetweenlands.api.rune.INodeBlueprint.INodeIO;
import thebetweenlands.api.rune.INodeBlueprint.INodeIO.ISchedulerTask;
import thebetweenlands.api.rune.INodeComposition;
import thebetweenlands.api.rune.INodeCompositionBlueprint;
import thebetweenlands.api.rune.INodeCompositionBlueprint.INodeLink;
import thebetweenlands.api.rune.INodeConfiguration;
import thebetweenlands.api.rune.INodeConfiguration.IConfigurationInput;
import thebetweenlands.api.rune.INodeConfiguration.IConfigurationOutput;
import thebetweenlands.api.rune.INodeConfiguration.IType;
import thebetweenlands.api.rune.IRuneChainUser;
import thebetweenlands.api.rune.impl.RuneChainComposition.RuneExecutionContext;

public class RuneChainComposition implements INodeComposition<RuneExecutionContext> {
	public static final class Blueprint implements INodeCompositionBlueprint<RuneExecutionContext> {
		private List<NodeSlot> slots = new ArrayList<>();

		private final class NodeSlot {
			private final INodeBlueprint<?, RuneExecutionContext> blueprint;

			@Nullable
			private final INodeConfiguration configuration;

			private final SlotLink[] links;

			private int cachedIndex = -1;

			private NodeSlot(INodeBlueprint<?, RuneExecutionContext> blueprint, @Nullable INodeConfiguration configuration) {
				this.blueprint = blueprint;
				int maxSlots = 0;
				if(configuration != null) {
					maxSlots = configuration.getInputs().size();
				} else {
					for(INodeConfiguration c : blueprint.getConfigurations()) {
						maxSlots = Math.max(c.getInputs().size(), maxSlots);
					}
				}
				this.configuration = configuration;
				this.links = new SlotLink[maxSlots];
			}

			private int getIndex() {
				if(this.cachedIndex >= 0 && this.cachedIndex < slots.size() && slots.get(this.cachedIndex) == this) {
					return this.cachedIndex;
				}
				return this.cachedIndex = slots.indexOf(this);
			}
		}

		private final class SlotLink implements INodeLink {
			private final NodeSlot slot;
			private final int output;

			private SlotLink(NodeSlot slot, int output) {
				this.slot = slot;
				this.output = output;
			}

			@Override
			public int getNode() {
				return this.slot.getIndex();
			}

			@Override
			public int getOutput() {
				return this.output;
			}

			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result + getOuterType().hashCode();
				result = prime * result + this.output;
				result = prime * result + ((this.slot == null) ? 0 : this.slot.getIndex());
				return result;
			}

			@Override
			public boolean equals(Object obj) {
				if (this == obj)
					return true;
				if (obj == null)
					return false;
				if (getClass() != obj.getClass())
					return false;
				SlotLink other = (SlotLink) obj;
				if (!getOuterType().equals(other.getOuterType()))
					return false;
				if (this.output != other.output)
					return false;
				if (this.slot == null) {
					if (other.slot != null)
						return false;
				} else if (this.slot.getIndex() != other.slot.getIndex())
					return false;
				return true;
			}

			private Blueprint getOuterType() {
				return Blueprint.this;
			}
		}

		@Override
		public int getNodeBlueprints() {
			return this.slots.size();
		}

		@Override
		public INodeBlueprint<?, RuneExecutionContext> getNodeBlueprint(int node) {
			return this.slots.get(node).blueprint;
		}

		@Override
		public INodeConfiguration getNodeConfiguration(int node) {
			return this.slots.get(node).configuration;
		}

		@Override
		public Collection<Integer> getLinkedSlots(int node) {
			Set<Integer> linkedSlots = new HashSet<>();
			NodeSlot slot = this.slots.get(node);
			for(int i = 0; i < slot.links.length; i++) {
				if(slot.links[i] != null) {
					linkedSlots.add(i);
				}
			}
			return linkedSlots;
		}

		@Override
		public INodeLink getLink(int node, int input) {
			if(input >= 0) {
				NodeSlot slot = this.slots.get(node);
				if(input < slot.links.length) {
					return slot.links[input];
				}
			}
			return null;
		}

		@Override
		public RuneChainComposition create() {
			return new RuneChainComposition(this);
		}

		/**
		 * Adds a new node blueprint to the rune chain
		 * @param blueprint - blueprint to add to the rune chain
		 */
		public void addNodeBlueprint(INodeBlueprint<?, RuneExecutionContext> blueprint) {
			this.addNodeBlueprint(this.slots.size(), blueprint);
		}

		/**
		 * Adds a new node blueprint at the specified index to the rune chain
		 * @param index - index where the blueprint should be inserted
		 * @param blueprint - blueprint to add to the rune chain
		 */
		public void addNodeBlueprint(int index, INodeBlueprint<?, RuneExecutionContext> blueprint) {
			this.slots.add(index, new NodeSlot(blueprint, null));
		}

		/**
		 * Adds a new node blueprint with the specified configuration to the rune chain
		 * @param blueprint - blueprint to add to the rune chain
		 * @param configuration - node configuration to be used
		 */
		public void addNodeBlueprint(INodeBlueprint<?, RuneExecutionContext> blueprint, INodeConfiguration configuration) {
			this.addNodeBlueprint(this.slots.size(), blueprint, configuration);
		}

		/**
		 * Adds a new node blueprint with the specified configuration at the specified index to the rune chain
		 * @param index - index where the blueprint should be inserted
		 * @param blueprint - blueprint to add to the rune chain
		 * @param configuration - node configuration to be used
		 */
		public void addNodeBlueprint(int index, INodeBlueprint<?, RuneExecutionContext> blueprint, INodeConfiguration configuration) {
			this.slots.add(index, new NodeSlot(blueprint, configuration));
		}

		/**
		 * Removes the node blueprint at the specified index
		 * @param index - index of the node blueprint to remove
		 * @return the removed node blueprint
		 */
		public INodeBlueprint<?, RuneExecutionContext> removeNodeBlueprint(int index) {
			return this.slots.remove(index).blueprint;
		}

		/**
		 * Tries to unlink the specified input
		 * @param inNodeIndex - index of the input node
		 * @param inputIndex - index of the input node's input
		 * @return <i>true</i> if the input was successfully unlinked, <i>false</i> otherwise
		 */
		public boolean unlink(int inNodeIndex, int inputIndex) {
			if(inputIndex < 0 || inNodeIndex < 0 || inNodeIndex >= this.getNodeBlueprints()) {
				return false;
			}

			NodeSlot inputSlot = this.slots.get(inNodeIndex);

			if(inputIndex >= inputSlot.links.length) {
				return false;
			}

			SlotLink link = inputSlot.links[inputIndex];

			if(link != null) {
				inputSlot.links[inputIndex] = null;
				return true;
			}

			return false;
		}

		/**
		 * Link the specified input to the specified output.
		 * No validation is done, if necessary see {@link #canLink(int, int, int, int)}.
		 * @param inNodeIndex - index of the input node
		 * @param inputIndex - index of the input node's input
		 * @param outNodeIndex - index of the output node
		 * @param outputIndex - index of the output node's output
		 * @return <i>true</i> if the input was successfully linked, <i>false</i> otherwise
		 */
		public boolean link(int inNodeIndex, int inputIndex, int outNodeIndex, int outputIndex) {
			if(outNodeIndex < 0 || outNodeIndex >= this.getNodeBlueprints() || inNodeIndex < 0 || inNodeIndex >= this.getNodeBlueprints() || inNodeIndex <= outNodeIndex) {
				return false;
			}
			NodeSlot outputSlot = this.slots.get(outNodeIndex);
			NodeSlot inputSlot = this.slots.get(inNodeIndex);
			inputSlot.links[inputIndex] = new SlotLink(outputSlot, outputIndex);
			return true;
		}

		/**
		 * Returns whether the specified input can be linked to the specified output
		 * @param inNodeIndex - index of the input node
		 * @param inputIndex - index of the input node's input
		 * @param outNodeIndex - index of the output node
		 * @param outputIndex - index of the output node's output
		 * @return whether the specifeid input can be linked to the specified output
		 */
		public boolean canLink(int inNodeIndex, int inputIndex, int outNodeIndex, int outputIndex) {
			if(outNodeIndex < 0 || outNodeIndex >= this.getNodeBlueprints() || inNodeIndex < 0 || inNodeIndex >= this.getNodeBlueprints() || inNodeIndex <= outNodeIndex) {
				return false;
			}

			List<INodeConfiguration> outConfigurations = this.getValidConfigurations(outNodeIndex, false);
			List<INodeConfiguration> inConfigurations = this.getValidConfigurations(inNodeIndex, false);

			for(INodeConfiguration inConfiguration : inConfigurations) {
				List<? extends IConfigurationInput> inputs = inConfiguration.getInputs();

				if(inputIndex < inputs.size()) {
					IConfigurationInput input = inputs.get(inputIndex);

					for(INodeConfiguration outConfiguration : outConfigurations) {
						List<? extends IConfigurationOutput> outputs = outConfiguration.getOutputs();

						if(outputIndex < outputs.size()) {
							List<Entry<IConfigurationOutput, IType>> validOutputTypes = this.getValidOutputTypes(outNodeIndex, outputIndex);

							for(Entry<IConfigurationOutput, IType> outputType : validOutputTypes) {
								if(input.test(outputType.getKey(), outputType.getValue())) {
									return true;
								}
							}
						}
					}
				}
			}

			return false;
		}

		private List<Entry<IConfigurationOutput, IType>> getValidOutputTypes(int nodeIndex, int outputIndex) {
			List<Entry<IConfigurationOutput, IType>> validOutputTypes = new ArrayList<>();
			List<INodeConfiguration> configurations = this.getValidConfigurations(nodeIndex, false);
			Collection<Integer> linkedSlots = this.getLinkedSlots(nodeIndex);

			for(INodeConfiguration configuration : configurations) {
				List<? extends IConfigurationOutput> outputs = configuration.getOutputs();
				List<? extends IConfigurationInput> inputs = configuration.getInputs();

				if(outputIndex < outputs.size()) {
					IConfigurationOutput output = outputs.get(outputIndex);

					ImmutableList.Builder<IType> inputTypesBuilder = ImmutableList.builder();
					for(int i = 0; i < inputs.size(); i++) {
						if(linkedSlots.contains(i)) {
							INodeLink link = this.getLink(nodeIndex, i);
							inputTypesBuilder.addAll(this.getValidOutputTypes(link.getNode(), link.getOutput()).stream().map(entry -> entry.getValue()).collect(Collectors.toList()));
						} else {
							inputTypesBuilder.add((IType) null);
						}
					}

					List<IType> inputTypes = inputTypesBuilder.build();

					if(output.isEnabled(inputTypes)) {
						validOutputTypes.add(new AbstractMap.SimpleImmutableEntry<>(output, output.getType(inputTypes)));
					}
				}
			}
			return validOutputTypes;
		}

		private List<INodeConfiguration> getValidConfigurations(int nodeIndex, boolean onlyFullyLinked) {
			if(nodeIndex < 0 || nodeIndex >= this.getNodeBlueprints()) {
				return Collections.emptyList();
			}

			INodeBlueprint<?, ?> node = this.getNodeBlueprint(nodeIndex);

			List<INodeConfiguration> validConfigurations = new ArrayList<>();

			List<INodeConfiguration> potentialConfigurations = new ArrayList<>();
			INodeConfiguration setConfiguration = this.getNodeConfiguration(nodeIndex);
			if(setConfiguration != null) {
				potentialConfigurations.add(setConfiguration);
			} else {
				potentialConfigurations.addAll(node.getConfigurations());
			}

			configurations: for(INodeConfiguration configuration : potentialConfigurations) {
				List<? extends IConfigurationInput> inputs = configuration.getInputs();

				Collection<Integer> linkedSlots = this.getLinkedSlots(nodeIndex);

				if(onlyFullyLinked && linkedSlots.size() != inputs.size()) {
					continue configurations;
				}

				for(int i : linkedSlots) {
					if(i < inputs.size()) {
						IConfigurationInput input = inputs.get(i);

						INodeLink link = this.getLink(nodeIndex, i);

						boolean validOutputConfiguration = false;

						for(INodeConfiguration outputConfiguration : this.getValidConfigurations(link.getNode(), false)) {
							List<? extends IConfigurationOutput> outputs = outputConfiguration.getOutputs();

							if(link.getOutput() < outputs.size()) {
								List<Entry<IConfigurationOutput, IType>> validOutputTypes = this.getValidOutputTypes(link.getNode(), link.getOutput());
								for(Entry<IConfigurationOutput, IType> outputType : validOutputTypes) {
									if(input.test(outputType.getKey(), outputType.getValue())) {
										validOutputConfiguration = true;
										break;
									}
								}
							}
						}

						if(!validOutputConfiguration) {
							continue configurations;
						}
					} else {
						continue configurations;
					}
				}

				validConfigurations.add(configuration);
			}

			return validConfigurations;
		}
	}

	private static final class Scheduler implements INodeIO.IScheduler {
		private boolean terminated;
		private float delay;
		private int updateCount;

		private Scheduler() {
			this.beginUpdateTask();
		}

		private void beginUpdateTask() {
			this.delay = 0.0F;
			this.terminated = false;
		}

		private void finishScheduledTask() {
			this.updateCount = 0;
		}

		@Override
		public void sleep(float delay) {
			if(delay > 0) {
				this.delay += delay;
			}
		}

		@Override
		public void terminate() {
			this.terminated = true;
		}

		@Override
		public int getUpdateCount() {
			return this.updateCount;
		}
	}

	private final class NodeIO implements INodeIO {
		private INode<?,RuneExecutionContext> node;
		private INodeBlueprint<INode<?, RuneExecutionContext>, RuneExecutionContext> blueprint;
		private Branch branch;

		private boolean branchingAllowed;
		private boolean branched;
		private boolean failed;
		private boolean terminated;
		private INodeIO.ISchedulerTask task;

		private NodeIO() {
			this.reset(null, null, null);
		}

		private void reset(Branch branch, INodeBlueprint<INode<?, RuneExecutionContext>, RuneExecutionContext> blueprint, INode<?, RuneExecutionContext> node) {
			this.branch = branch;
			this.node = node;
			this.blueprint = blueprint;
			this.branchingAllowed = true;
			this.branched = false;
			this.failed = false;
			this.terminated = false;
			this.task = null;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void set(int output, Object obj) {
			this.branchingAllowed = false;
			// TODO Type check?
			if(obj instanceof Collection) {
				RuneChainComposition.this.outputValues.get(output).addAll((Collection<Object>) obj);
			} else {
				RuneChainComposition.this.outputValues.get(output).add(obj);
			}
		}

		@Override
		public void fail() {
			this.branchingAllowed = false;
			if(!this.failed) {
				this.failed = true;
				this.blueprint.fail(this.node, RuneChainComposition.this.context);
			} else {
				this.failed = true;
			}
		}

		@Override
		public void branch() {
			if(!this.branchingAllowed) {
				throw new IllegalStateException("Cannot branch after calling set or fail");
			}
			if(!this.branched) {
				this.branch = new Branch(this.branch);
				this.branched = true;
			}
		}

		@Override
		public void terminate() {
			this.terminated = true;
		}

		@Override
		public void schedule(ISchedulerTask task) {
			this.task = task;
		}

		@Override
		public Object get(int input) {
			// TODO Type check?
			return RuneChainComposition.this.combination[input];
		}

		@Override
		public boolean branched() {
			return this.branched;
		}

		@Override
		public boolean failed() {
			return this.failed;
		}

		@Override
		public ISchedulerTask scheduled() {
			return this.task;
		}

		@Override
		public boolean terminated() {
			return this.terminated;
		}
	}

	private final class Branch {
		private final Branch parent;
		private Int2ObjectMap<List<Collection<Object>>> outputValues;

		private Branch(Branch parent) {
			this.parent = parent;
		}

		private void addOverrideOutputValues(int node, List<Collection<Object>> values) {
			if(this.outputValues == null) {
				this.outputValues = new Int2ObjectOpenHashMap<>();
			}

			this.outputValues.put(node, values);
		}

		private void addOverrideOutputValue(int node, int slot, Collection<Object> values) {
			if(this.outputValues == null) {
				this.outputValues = new Int2ObjectOpenHashMap<>();
			}

			List<Collection<Object>> nodeValues = this.outputValues.get(node);

			List<Collection<Object>> parentValues = this.getOutputValues(node);
			if(parentValues != null) {
				nodeValues = new ArrayList<>(parentValues.size());
				nodeValues.addAll(parentValues);
				this.outputValues.put(node, nodeValues);
			} else {
				int slots = RuneChainComposition.this.nodes.get(node).getConfiguration().getInputs().size();
				nodeValues = new ArrayList<>(slots);
				for(int i = 0; i < slots; i++) {
					nodeValues.add(new ArrayList<>());
				}
			}

			if(slot < nodeValues.size()) {
				nodeValues.set(slot, values);
			}
		}

		@Nullable
		private List<Collection<Object>> getOutputValues(int node) {
			Branch branch = this;
			do {
				if(branch.outputValues != null) {
					List<Collection<Object>> nodeValues = branch.outputValues.get(node);
					if(nodeValues != null) {
						return nodeValues;
					}
				}

				branch = branch.parent;
			} while(branch != null);

			return null;
		}
	}

	public final class RuneExecutionContext {
		private final IRuneChainUser user;
		private int inputIndexCount;
		private int inputIndex;
		private int branchCount;
		private int branch;

		private RuneExecutionContext(IRuneChainUser user) {
			this.user = user;
		}

		/**
		 * Returns the user that activated the rune chain
		 * @return the user that activated the rune chain
		 */
		public IRuneChainUser getUser() {
			return this.user;
		}

		/**
		 * Returns the aspect buffer that provides the runes with aspect.
		 * @return the aspect buffer that provides the runes with aspect
		 */
		public IAspectBuffer getAspectBuffer() {
			return RuneChainComposition.this.aspectBuffer;
		}

		/**
		 * Returns the number of currently active branches.
		 * @return the number of currently active branches
		 */
		public int getBranchCount() {
			return this.branchCount;
		}

		/**
		 * Returns the current active branch starting at 0.
		 * @return the current active branch starting at 0
		 */
		public int getBranch() {
			return this.branch;
		}

		/**
		 * Returns the number of current input combinations.
		 * @return the number of current input combinations
		 */
		public int getInputIndexCount() {
			return this.inputIndexCount;
		}

		/**
		 * Returns the current input combination index.
		 * @return the current input combination index
		 */
		public int getInputIndex() {
			return this.inputIndex;
		}

		/**
		 * Returns the currently activating rune index.
		 * @return the currently activating rune index
		 */
		public int getRune() {
			return RuneChainComposition.this.currentNode;
		}

		public void sendPacket(Consumer<PacketBuffer> serializer) {
			this.getUser().sendPacket(RuneChainComposition.this, buffer -> {
				buffer.writeVarInt(this.getRune());
				serializer.accept(buffer);
			});
		}
	}

	@FunctionalInterface
	public static interface IAspectBuffer {
		/**
		 * Gets an aspect container for the specified aspect type that
		 * can be modified. The container may be empty if no aspect container
		 * with the requested aspect type is available. All runes require and
		 * drain aspects from this container. This container could be used
		 * to provide an aspect buffer that is continuously refilled to
		 * restrict how often or how quickly the runes can be activated before
		 * they fail. Also keep in mind that this aspect container may be drained
		 * many times very quickly so it should be optimized for that (e.g. don't save
		 * on change but instead only after updating the rune chain).
		 * @param type - the requested type of the aspect
		 */
		public AspectContainer get(IAspectType type);
	}

	private final Blueprint blueprint;
	private final List<INode<?, RuneExecutionContext>> nodes;

	private final Scheduler scheduler = new Scheduler();
	private final NodeIO nodeIO = new NodeIO();

	private IAspectBuffer aspectBuffer;

	private boolean running = false;

	private int nextNode = 0;
	private Queue<Branch> branches;
	private Queue<Branch> newBranches;
	private int currentNode;
	private boolean sourceBranchAdded = false;
	private Branch sourceBranch;
	private float delay;
	private RuneExecutionContext context;
	private List<List<Object>> inputValues;
	private int currentCombination;
	private int combinations;
	private int[] itemCounts;
	private int[] divs;
	private List<Collection<Object>> outputValues = new ArrayList<>();
	private Object[] combination;

	private ISchedulerTask scheduledTask;

	private RuneChainComposition(Blueprint blueprint) {
		this.blueprint = blueprint;
		this.nodes = new ArrayList<>(this.blueprint.getNodeBlueprints());

		for(int i = 0; i < this.blueprint.getNodeBlueprints(); i++) {
			INodeConfiguration setConfiguration = this.blueprint.getNodeConfiguration(i);
			List<INodeConfiguration> validConfigurations = this.blueprint.getValidConfigurations(i, true);

			if(validConfigurations.isEmpty() || (setConfiguration != null && !validConfigurations.contains(setConfiguration))) {
				// Add dummy node that always fails instantly
				this.nodes.add(NodeDummy.Blueprint.INSTANCE.create(i, this, NodeDummy.Blueprint.CONFIGURATION));
			} else {
				INodeConfiguration configuration = setConfiguration != null ? setConfiguration : validConfigurations.get(0);
				INodeBlueprint<?, RuneExecutionContext> nodeBlueprint = this.blueprint.getNodeBlueprint(i);
				this.nodes.add(nodeBlueprint.create(i, this, configuration));
			}
		}
	}

	@Override
	public INodeCompositionBlueprint<RuneExecutionContext> getBlueprint() {
		return this.blueprint;
	}

	@Override
	public INode<?, RuneExecutionContext> getNode(int node) {
		return this.nodes.get(node);
	}

	@Override
	public boolean isInvalid(int node) {
		return this.nodes.get(node).getBlueprint() == NodeDummy.Blueprint.INSTANCE;
	}

	@Override
	public boolean isRunning() {
		return this.running;
	}

	@Override
	public void run(RuneExecutionContext context) {
		Preconditions.checkNotNull(this.aspectBuffer, "Aspect buffer must be set before running rune chain");

		this.nextNode = 0;
		this.context = context;
		this.running = true;
		this.delay = 0;
		this.branches = new LinkedList<>();
		this.branches.add(new Branch(null)); // Add root branch
		this.currentCombination = 0;
		this.inputValues = null;
		this.sourceBranchAdded = false;
		this.currentNode = 0;
		this.combinations = 1;
		this.divs = null;
		this.itemCounts = null;
		this.outputValues = null;
		this.combination = null;
		this.scheduledTask = null;
		this.newBranches = null;

		this.update();
	}

	/**
	 * Sets the aspect buffer that provides the runes with aspect. Must be set before calling
	 * {@link #run(IRuneChainUser)}.
	 * @param buffer - aspect buffer that provides the runes with aspect
	 */
	public void setAspectBuffer(IAspectBuffer buffer) {
		this.aspectBuffer = buffer;
	}

	/**
	 * Starts the execution of this rune chain. Requires an aspect buffer
	 * before running, see {@link #setAspectBuffer(IAspectBuffer)}!
	 * @param user - rune user that is executing this rune chain
	 */
	public void run(IRuneChainUser user) {
		this.run(new RuneExecutionContext(user));
	}

	/**
	 * Updates the rune chain execution if the rune
	 * chain is running
	 */
	public void update() {
		if(this.running) {
			boolean resumeSuspension = false;

			if(this.delay >= 1.0F) {
				this.delay -= 1.0F;

				if(this.scheduledTask != null && this.delay < 1.0F) {
					INode<?, RuneExecutionContext> node = this.nodes.get(this.currentNode);
					@SuppressWarnings("unchecked")
					INodeBlueprint<INode<?, RuneExecutionContext>, RuneExecutionContext> blueprint = (INodeBlueprint<INode<?, RuneExecutionContext>, RuneExecutionContext>) node.getBlueprint();

					if(this.updateTask(node, blueprint)) {
						// Delay has accumulated > 1.0, exit and continue next tick
						return;
					}
				}

				if(this.scheduledTask == null && this.delay < 1.0F) {
					resumeSuspension = true;
				}
			}

			if(this.delay < 1.0F) {
				while(this.nextNode < this.nodes.size() || resumeSuspension) {
					if(!resumeSuspension) {
						this.newBranches = new LinkedList<>();
					}

					if(!resumeSuspension) {
						this.currentNode = this.nextNode++;
						this.context.branchCount = this.branches.size();
						this.context.branch = 0;
					}

					INode<?, RuneExecutionContext> node = this.nodes.get(this.currentNode);
					@SuppressWarnings("unchecked")
					INodeBlueprint<INode<?, RuneExecutionContext>, RuneExecutionContext> blueprint = (INodeBlueprint<INode<?, RuneExecutionContext>, RuneExecutionContext>) node.getBlueprint();
					INodeConfiguration configuration = node.getConfiguration();
					List<? extends IConfigurationInput> inputs = configuration.getInputs();

					while(!this.branches.isEmpty() || resumeSuspension) {
						if(!resumeSuspension) {
							this.sourceBranchAdded = false;
							this.sourceBranch = this.branches.remove();

							this.inputValues = new ArrayList<>();

							// Collect input values
							for(int inputIndex = 0; inputIndex < inputs.size(); inputIndex++) {
								//TODO Is it possible to merge the collections without having to collect all values
								//and only get the values later on iteratively?
								List<Object> values = new ArrayList<>();
								INodeLink link = this.blueprint.getLink(this.currentNode, inputIndex);
								values.addAll(this.sourceBranch.getOutputValues(link.getNode()).get(link.getOutput()));
								this.inputValues.add(values);
							}

							//Prepare input combinations
							this.combinations = 1;
							this.itemCounts = new int[inputs.size()];
							this.divs = new int[inputs.size()];

							for(int inputIndex = 0; inputIndex < inputs.size(); inputIndex++) {
								IConfigurationInput input = inputs.get(inputIndex);

								if(input.isCollection()) {
									//If input is multi-input then treat collection as one value
									this.itemCounts[inputIndex] = 1;
								} else {
									this.combinations *= (this.itemCounts[inputIndex] = this.inputValues.get(inputIndex).size());
								}

								int div = 1;
								if(inputIndex > 0) {
									for(int divSlotIndex = inputIndex - 1; divSlotIndex < this.itemCounts.length - 1; divSlotIndex++) {
										div *= this.itemCounts[divSlotIndex];
									}
								}
								this.divs[inputIndex] = div;
							}

							this.outputValues = new ArrayList<>();
							for(int i = 0; i < configuration.getOutputs().size(); i++) {
								this.outputValues.add(new ArrayList<>());
							}

							this.combination = new Object[inputs.size()];
							this.currentCombination = 0;

							this.context.inputIndexCount = this.combinations;
						}

						//Execute node for each combination
						while(this.currentCombination < this.combinations) {
							this.context.inputIndex = this.currentCombination;

							// Get input value combination
							for(int inputIndex = 0; inputIndex < inputs.size(); inputIndex++) {
								IConfigurationInput input = inputs.get(inputIndex);
								if(input.isCollection()) {
									this.combination[inputIndex] = this.inputValues.get(inputIndex);
								} else {
									this.combination[inputIndex] = this.inputValues.get(inputIndex).get((this.currentCombination / this.divs[inputIndex]) % this.itemCounts[inputIndex]);
								}
							}

							// Reset I/O for new node execution
							this.nodeIO.reset(this.sourceBranch, blueprint, node);

							blueprint.run(node, this.context, this.nodeIO);

							// Increment before potentially suspending
							this.currentCombination++;

							// Already resumed don't try again next loop
							resumeSuspension = false;

							if(!this.nodeIO.failed && !this.nodeIO.terminated) {
								// Add output values to new branch
								this.nodeIO.branch.addOverrideOutputValues(this.currentNode, this.outputValues);

								if(this.nodeIO.branch != this.sourceBranch) {
									this.newBranches.add(this.nodeIO.branch);

									// Override values at nodes that produced the input values
									for(int inputIndex = 0; inputIndex < inputs.size(); inputIndex++) {
										INodeLink link = this.blueprint.getLink(this.currentNode, inputIndex);
										this.nodeIO.branch.addOverrideOutputValue(link.getNode(), link.getOutput(), Collections.singleton(this.combination[inputIndex]));
									}
								} else if(this.nodeIO.branch == this.sourceBranch && !this.sourceBranchAdded) {
									this.newBranches.add(this.sourceBranch);
									this.sourceBranchAdded = true;
								}
							} else if(this.nodeIO.terminated) {
								this.terminate();
								return;
							} else if(this.nodeIO.branch == this.sourceBranch) {
								this.newBranches.remove(this.sourceBranch);
								this.currentCombination = this.combinations; //Exit loop
							}

							if(!this.nodeIO.terminated && this.nodeIO.task != null) {
								this.scheduledTask = this.nodeIO.task;
								if(this.updateTask(node, blueprint)) {
									// Delay has accumulated > 1.0, exit and continue next tick
									return;
								}
							}
						}

						this.context.branch++;

						// Already resumed don't try again next loop
						resumeSuspension = false;
					}

					this.branches = this.newBranches;

					// Already resumed don't try again next loop
					resumeSuspension = false;
				}

				this.terminate();
			}
		}
	}

	private void terminate() {
		this.running = false;

		for(INode<?, RuneExecutionContext> node : this.nodes) {
			@SuppressWarnings("unchecked")
			INodeBlueprint<INode<?, RuneExecutionContext>, RuneExecutionContext> blueprint = (INodeBlueprint<INode<?, RuneExecutionContext>, RuneExecutionContext>) node.getBlueprint();
			blueprint.terminate(node, this.context);
		}
	}

	private boolean updateTask(INode<?, RuneExecutionContext> node, INodeBlueprint<INode<?, RuneExecutionContext>, RuneExecutionContext> blueprint) {
		this.scheduler.beginUpdateTask();

		boolean updated = false;

		while(this.delay < 1.0F) {
			this.scheduledTask.update(this.scheduler);
			updated = true;

			this.delay += this.scheduler.delay;

			this.scheduler.updateCount++;

			if(this.scheduler.terminated) {
				this.scheduler.finishScheduledTask();
				this.scheduledTask = null;
				break;
			}
		}

		if(updated && this.delay >= 1.0F && (this.currentCombination != this.combinations || !this.branches.isEmpty()) /*only suspend if execution of node isn't already finished for all inputs/branches*/) {
			blueprint.suspend(node, this.context);
		}

		return this.delay >= 1.0F;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void processPacket(IRuneChainUser user, PacketBuffer buffer) throws IOException {
		int runeIndex = buffer.readVarInt();

		if(runeIndex >= 0 && runeIndex < this.getBlueprint().getNodeBlueprints()) {
			INode<?, RuneExecutionContext> node = this.getNode(runeIndex);
			INodeBlueprint<INode<?, RuneExecutionContext>, RuneExecutionContext> blueprint = (INodeBlueprint<INode<?, RuneExecutionContext>, RuneExecutionContext>) node.getBlueprint();

			if(blueprint instanceof AbstractRune.Blueprint) {
				AbstractRune rune = (AbstractRune) node;
				AbstractRune.Blueprint runeBlueprint = (AbstractRune.Blueprint) blueprint;
				runeBlueprint.processPacket(rune, user, buffer);
			}
		}
	}
}
