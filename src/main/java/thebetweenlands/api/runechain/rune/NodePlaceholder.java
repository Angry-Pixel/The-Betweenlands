package thebetweenlands.api.runechain.rune;

import java.util.List;

import com.google.common.collect.ImmutableList;

import thebetweenlands.api.runechain.base.IConfigurationLinkAccess;
import thebetweenlands.api.runechain.base.INode;
import thebetweenlands.api.runechain.base.INodeBlueprint;
import thebetweenlands.api.runechain.base.INodeComposition;
import thebetweenlands.api.runechain.base.INodeCompositionBlueprint;
import thebetweenlands.api.runechain.base.INodeConfiguration;
import thebetweenlands.api.runechain.base.INodeIO;
import thebetweenlands.api.runechain.chain.IRuneExecutionContext;

public final class NodePlaceholder implements INode<NodePlaceholder, IRuneExecutionContext> {

	public static final class Blueprint implements INodeBlueprint<NodePlaceholder, IRuneExecutionContext> {
		public static final Blueprint INSTANCE = new Blueprint();

		public static final INodeConfiguration CONFIGURATION;

		static {
			RuneConfiguration.Builder builder = RuneConfiguration.create();
			CONFIGURATION = builder.build();
		}

		private Blueprint() {}

		@Override
		public List<INodeConfiguration> getConfigurations(IConfigurationLinkAccess linkAccess, boolean provisional) {
			return ImmutableList.of(CONFIGURATION);
		}

		@Override
		public NodePlaceholder create(int index, INodeComposition<IRuneExecutionContext> composition, INodeConfiguration configuration) {
			return new NodePlaceholder(this, index, composition, configuration);
		}

		@Override
		public void run(NodePlaceholder state, IRuneExecutionContext executor, INodeIO io) {
			io.fail();
		}

		@Override
		public void fail(NodePlaceholder state, IRuneExecutionContext executor) {

		}

		@Override
		public void terminate(NodePlaceholder state, IRuneExecutionContext context) {

		}
	}

	private final Blueprint blueprint;
	private final INodeComposition<IRuneExecutionContext> composition;
	private final INodeConfiguration configuration;
	private final int index;

	private NodePlaceholder(Blueprint blueprint, int index, INodeComposition<IRuneExecutionContext> composition, INodeConfiguration configuration) {
		this.blueprint = blueprint;
		this.composition = composition;
		this.configuration = configuration;
		this.index = index;
	}

	@Override
	public INodeBlueprint<NodePlaceholder, IRuneExecutionContext> getBlueprint() {
		return this.blueprint;
	}

	@Override
	public INodeConfiguration getConfiguration() {
		return this.configuration;
	}

	@Override
	public INodeComposition<IRuneExecutionContext> getComposition() {
		return this.composition;
	}

	@Override
	public int getIndex() {
		return this.index;
	}
}
