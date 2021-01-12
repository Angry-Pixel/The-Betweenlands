package thebetweenlands.common.herblore.rune;

import java.util.List;

import com.google.common.collect.ImmutableList;

import thebetweenlands.api.runechain.base.IConfigurationLinkAccess;
import thebetweenlands.api.runechain.base.INodeComposition;
import thebetweenlands.api.runechain.base.INodeCompositionBlueprint;
import thebetweenlands.api.runechain.base.INodeConfiguration;
import thebetweenlands.api.runechain.base.INodeIO;
import thebetweenlands.api.runechain.chain.IRuneExecutionContext;
import thebetweenlands.api.runechain.io.types.RuneTokenDescriptors;
import thebetweenlands.api.runechain.modifier.Subject;
import thebetweenlands.api.runechain.rune.AbstractRune;
import thebetweenlands.api.runechain.rune.RuneConfiguration;
import thebetweenlands.api.runechain.rune.RuneStats;
import thebetweenlands.common.registries.AspectRegistry;

public final class RuneBranching extends AbstractRune<RuneBranching> {

	public static final class Blueprint extends AbstractRune.Blueprint<RuneBranching> {
		public Blueprint() {
			super(RuneStats.builder()
					.aspect(AspectRegistry.ORDANIIS, 1)
					.duration(0.005f)
					.build());
		}

		public static final RuneConfiguration CONFIGURATION_1;
		public static final RuneConfiguration CONFIGURATION_2;
		public static final RuneConfiguration CONFIGURATION_3;

		static {
			RuneConfiguration.Builder builder = RuneConfiguration.create(RuneTokenDescriptors.ANY);

			builder.in(RuneTokenDescriptors.ANY).type(Object.class).getter();
			CONFIGURATION_1 = builder.build();

			builder.in(RuneTokenDescriptors.ANY).type(Object.class).getter();
			builder.in(RuneTokenDescriptors.ANY).type(Object.class).getter();
			CONFIGURATION_2 = builder.build();

			builder.in(RuneTokenDescriptors.ANY).type(Object.class).getter();
			builder.in(RuneTokenDescriptors.ANY).type(Object.class).getter();
			builder.in(RuneTokenDescriptors.ANY).type(Object.class).getter();
			CONFIGURATION_3 = builder.build();
		}

		@Override
		public List<RuneConfiguration> getConfigurations(IConfigurationLinkAccess linkAccess, boolean provisional) {
			return ImmutableList.of(CONFIGURATION_1, CONFIGURATION_2, CONFIGURATION_3);
		}

		@Override
		public RuneBranching create(int index, INodeComposition<IRuneExecutionContext> composition, INodeConfiguration configuration) {
			return new RuneBranching(this, index, composition, (RuneConfiguration) configuration);
		}

		@Override
		protected Subject activate(RuneBranching state, IRuneExecutionContext context, INodeIO io) {
			io.branch();
			return null;
		}
	}

	private RuneBranching(Blueprint blueprint, int index, INodeComposition<IRuneExecutionContext> composition, RuneConfiguration configuration) {
		super(blueprint, index, composition, configuration);
	}
}
