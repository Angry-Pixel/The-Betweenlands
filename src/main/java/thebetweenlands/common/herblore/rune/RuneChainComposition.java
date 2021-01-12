package thebetweenlands.common.herblore.rune;

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
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.network.PacketBuffer;
import thebetweenlands.api.runechain.IAspectBuffer;
import thebetweenlands.api.runechain.IRuneChainUser;
import thebetweenlands.api.runechain.base.IConfigurationInput;
import thebetweenlands.api.runechain.base.IConfigurationOutput;
import thebetweenlands.api.runechain.base.INode;
import thebetweenlands.api.runechain.base.INodeBlueprint;
import thebetweenlands.api.runechain.base.INodeCompositionBlueprint;
import thebetweenlands.api.runechain.base.INodeConfiguration;
import thebetweenlands.api.runechain.base.INodeIO;
import thebetweenlands.api.runechain.base.INodeLink;
import thebetweenlands.api.runechain.base.IScheduler;
import thebetweenlands.api.runechain.base.ISchedulerTask;
import thebetweenlands.api.runechain.base.IType;
import thebetweenlands.api.runechain.chain.IRuneChain;
import thebetweenlands.api.runechain.chain.IRuneChainBlueprint;
import thebetweenlands.api.runechain.chain.IRuneExecutionContext;
import thebetweenlands.api.runechain.modifier.RuneEffectModifier;
import thebetweenlands.api.runechain.rune.AbstractRune;
import thebetweenlands.api.runechain.rune.NodePlaceholder;

public class RuneChainComposition implements IRuneChain {
	public static final class Blueprint implements IRuneChainBlueprint {
		private List<NodeSlot> slots = new ArrayList<>();

		private final class NodeSlot {
			private final INodeBlueprint<?, IRuneExecutionContext> blueprint;

			@Nullable
			private final INodeConfiguration configuration;

			private final SlotLink[] links;

			private int cachedIndex = -1;

			private NodeSlot(INodeBlueprint<?, IRuneExecutionContext> blueprint, @Nullable INodeConfiguration configuration) {
				this.blueprint = blueprint;
				int maxSlots = 0;
				if(configuration != null) {
					maxSlots = configuration.getInputs().size();
				} else {
					for(INodeConfiguration c : blueprint.getConfigurations(Blueprint.this.createLinkAccess(this.getIndex()), false)) {
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
		public INodeBlueprint<?, IRuneExecutionContext> getNodeBlueprint(int node) {
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
		public IRuneChain create() {
			return new RuneChainComposition(this);
		}

		@Override
		public void addNodeBlueprint(INodeBlueprint<?, IRuneExecutionContext> blueprint) {
			this.addNodeBlueprint(this.slots.size(), blueprint);
		}

		@Override
		public void addNodeBlueprint(int index, INodeBlueprint<?, IRuneExecutionContext> blueprint) {
			this.slots.add(index, new NodeSlot(blueprint, null));
		}

		@Override
		public void addNodeBlueprint(INodeBlueprint<?, IRuneExecutionContext> blueprint, INodeConfiguration configuration) {
			this.addNodeBlueprint(this.slots.size(), blueprint, configuration);
		}

		@Override
		public void addNodeBlueprint(int index, INodeBlueprint<?, IRuneExecutionContext> blueprint, INodeConfiguration configuration) {
			this.slots.add(index, new NodeSlot(blueprint, configuration));
		}

		@Override
		public INodeBlueprint<?, IRuneExecutionContext> removeNodeBlueprint(int index) {
			return this.slots.remove(index).blueprint;
		}

		@Override
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

		@Override
		public boolean link(int inNodeIndex, int inputIndex, int outNodeIndex, int outputIndex) {
			if(outNodeIndex < 0 || outNodeIndex >= this.getNodeBlueprints() || inNodeIndex < 0 || inNodeIndex >= this.getNodeBlueprints() || inNodeIndex <= outNodeIndex) {
				return false;
			}
			NodeSlot outputSlot = this.slots.get(outNodeIndex);
			NodeSlot inputSlot = this.slots.get(inNodeIndex);
			inputSlot.links[inputIndex] = new SlotLink(outputSlot, outputIndex);
			return true;
		}

		@Override
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
								if(input.isType(outputType.getKey(), outputType.getValue())) {
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

					List<IType> inputTypes = new ArrayList<>();
					for(int i = 0; i < inputs.size(); i++) {
						if(linkedSlots.contains(i)) {
							INodeLink link = this.getLink(nodeIndex, i);
							inputTypes.addAll(this.getValidOutputTypes(link.getNode(), link.getOutput()).stream().map(entry -> entry.getValue()).collect(Collectors.toList()));
						} else {
							inputTypes.add((IType) null);
						}
					}

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

			INodeBlueprint<?, IRuneExecutionContext> node = this.getNodeBlueprint(nodeIndex);

			List<INodeConfiguration> validConfigurations = new ArrayList<>();

			List<INodeConfiguration> potentialConfigurations = new ArrayList<>();
			INodeConfiguration setConfiguration = this.getNodeConfiguration(nodeIndex);
			if(setConfiguration != null) {
				potentialConfigurations.add(setConfiguration);
			} else {
				potentialConfigurations.addAll(node.getConfigurations(this.createLinkAccess(nodeIndex), false));
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
									if(input.isType(outputType.getKey(), outputType.getValue())) {
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

	private static final class Scheduler implements IScheduler {
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
		private final INode<?,IRuneExecutionContext> node;
		private final INodeBlueprint<INode<?, IRuneExecutionContext>, IRuneExecutionContext> blueprint;
		private final List<Collection<Object>> outputValues;

		private Branch branch;

		private boolean branchingAllowed;
		private boolean branched;
		private boolean failed;
		private boolean terminated;
		private ISchedulerTask task;

		private NodeIO(Branch branch, List<Collection<Object>> outputValues, INodeBlueprint<INode<?, IRuneExecutionContext>, IRuneExecutionContext> blueprint, INode<?, IRuneExecutionContext> node) {
			this.branch = branch;
			this.outputValues = outputValues;
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
				this.outputValues.get(output).addAll((Collection<Object>) obj);
			} else {
				this.outputValues.get(output).add(obj);
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
				this.outputValues.put(node, nodeValues);
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

	private final Blueprint blueprint;
	private final List<INode<?, IRuneExecutionContext>> nodes;

	private final Scheduler scheduler = new Scheduler();
	IAspectBuffer aspectBuffer;

	private boolean running = false;

	private int nextNode = 0;
	private Queue<Branch> branches;
	private Queue<Branch> newBranches;
	private List<NodeIO> outputtingNodeIOs;
	int currentNode;
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
				this.nodes.add(NodePlaceholder.Blueprint.INSTANCE.create(i, this, NodePlaceholder.Blueprint.CONFIGURATION));
			} else {
				INodeConfiguration configuration = setConfiguration != null ? setConfiguration : validConfigurations.get(0);
				INodeBlueprint<?, IRuneExecutionContext> nodeBlueprint = this.blueprint.getNodeBlueprint(i);
				this.nodes.add(nodeBlueprint.create(i, this, configuration));
			}
		}
	}

	@Override
	public INodeCompositionBlueprint<IRuneExecutionContext> getBlueprint() {
		return this.blueprint;
	}

	@Override
	public INode<?, IRuneExecutionContext> getNode(int node) {
		return this.nodes.get(node);
	}

	@Override
	public boolean isInvalid(int node) {
		return this.nodes.get(node).getBlueprint() == NodePlaceholder.Blueprint.INSTANCE;
	}

	@Override
	public boolean isRunning() {
		return this.running;
	}

	@Override
	public void run(IRuneExecutionContext context) {
		Preconditions.checkNotNull(this.aspectBuffer, "Aspect buffer must be set before running rune chain");
		Preconditions.checkArgument(context instanceof RuneExecutionContext, "Context must be an instance of RuneExecutionContext");
		
		this.nextNode = 0;
		this.context = (RuneExecutionContext) context;
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

	@Override
	public void setAspectBuffer(IAspectBuffer buffer) {
		this.aspectBuffer = buffer;
	}

	@Override
	public IAspectBuffer getAspectBuffer() {
		return this.aspectBuffer;
	}

	@Override
	public void run(IRuneChainUser user) {
		this.run(new RuneExecutionContext(this, user));
	}

	/**
	 * Updates the rune chain execution if the rune
	 * chain is running
	 */
	@Override
	public void update() {
		if(this.running) {
			boolean resumeSuspension = false;

			if(this.delay >= 1.0F) {
				this.delay -= 1.0F;

				if(this.scheduledTask != null && this.delay < 1.0F) {
					INode<?, IRuneExecutionContext> node = this.nodes.get(this.currentNode);
					@SuppressWarnings("unchecked")
					INodeBlueprint<INode<?, IRuneExecutionContext>, IRuneExecutionContext> blueprint = (INodeBlueprint<INode<?, IRuneExecutionContext>, IRuneExecutionContext>) node.getBlueprint();

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
						this.outputtingNodeIOs = new ArrayList<>();
					}

					if(!resumeSuspension) {
						this.currentNode = this.nextNode++;
						this.context.branchCount = this.branches.size();
						this.context.branch = 0;
					}

					INode<?, IRuneExecutionContext> node = this.nodes.get(this.currentNode);
					@SuppressWarnings("unchecked")
					INodeBlueprint<INode<?, IRuneExecutionContext>, IRuneExecutionContext> blueprint = (INodeBlueprint<INode<?, IRuneExecutionContext>, IRuneExecutionContext>) node.getBlueprint();
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
							NodeIO nodeIO = new NodeIO(this.sourceBranch, this.outputValues, blueprint, node);

							blueprint.run(node, this.context, nodeIO);

							// Increment before potentially suspending
							this.currentCombination++;

							// Already resumed don't try again next loop
							resumeSuspension = false;

							if(!nodeIO.failed && !nodeIO.terminated) {
								// Queue I/O to output values at the end of the rune execution
								this.outputtingNodeIOs.add(nodeIO);

								if(nodeIO.branch != this.sourceBranch) {
									this.newBranches.add(nodeIO.branch);

									// Override values at nodes that produced the input values
									for(int inputIndex = 0; inputIndex < inputs.size(); inputIndex++) {
										INodeLink link = this.blueprint.getLink(this.currentNode, inputIndex);
										nodeIO.branch.addOverrideOutputValue(link.getNode(), link.getOutput(), Collections.singleton(this.combination[inputIndex]));
									}
								} else if(nodeIO.branch == this.sourceBranch && !this.sourceBranchAdded) {
									this.newBranches.add(this.sourceBranch);
									this.sourceBranchAdded = true;
								}
							} else if(nodeIO.terminated) {
								this.terminate();
								return;
							} else if(nodeIO.branch == this.sourceBranch) {
								this.newBranches.remove(this.sourceBranch);
								this.currentCombination = this.combinations; //Exit loop
							}

							if(!nodeIO.terminated && nodeIO.task != null) {
								this.scheduledTask = nodeIO.task;
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


					// Add rune output values to new branch
					for(NodeIO nodeIO : this.outputtingNodeIOs) {
						nodeIO.branch.addOverrideOutputValues(nodeIO.node.getIndex(), nodeIO.outputValues);
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

		for(INode<?, IRuneExecutionContext> node : this.nodes) {
			@SuppressWarnings("unchecked")
			INodeBlueprint<INode<?, IRuneExecutionContext>, IRuneExecutionContext> blueprint = (INodeBlueprint<INode<?, IRuneExecutionContext>, IRuneExecutionContext>) node.getBlueprint();
			blueprint.terminate(node, this.context);
		}
	}

	private boolean updateTask(INode<?, IRuneExecutionContext> node, INodeBlueprint<INode<?, IRuneExecutionContext>, IRuneExecutionContext> blueprint) {
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

	@Override
	public void updateRuneEffectModifiers() {
		for(int i = 0; i < this.getBlueprint().getNodeBlueprints(); i++) {
			INode<?, IRuneExecutionContext> node = this.getNode(i);

			//TODO
			if(node instanceof AbstractRune) {
				RuneEffectModifier modifier = ((AbstractRune<?>) node).getRuneEffectModifier();
				if(modifier != null) {
					modifier.update();
				}
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void processPacket(IRuneChainUser user, PacketBuffer buffer) throws IOException {
		int runeIndex = buffer.readVarInt();

		if(runeIndex >= 0 && runeIndex < this.getBlueprint().getNodeBlueprints()) {
			INode<?, IRuneExecutionContext> node = this.getNode(runeIndex);
			INodeBlueprint<INode<?, IRuneExecutionContext>, IRuneExecutionContext> blueprint = (INodeBlueprint<INode<?, IRuneExecutionContext>, IRuneExecutionContext>) node.getBlueprint();

			//TODO
			if(blueprint instanceof AbstractRune.Blueprint) {
				AbstractRune rune = (AbstractRune) node;
				AbstractRune.Blueprint runeBlueprint = (AbstractRune.Blueprint) blueprint;
				runeBlueprint.processPacket(rune, user, buffer);
			}
		}
	}

	public int getCurrentNode() {
		return this.currentNode;
	}
}
