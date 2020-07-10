package thebetweenlands.common.herblore.rune;

import java.util.List;

import com.google.common.collect.ImmutableList;

import thebetweenlands.api.rune.INodeComposition;
import thebetweenlands.api.rune.INodeConfiguration;
import thebetweenlands.api.rune.impl.AbstractRune;
import thebetweenlands.api.rune.impl.PortNodeConfiguration;
import thebetweenlands.api.rune.impl.RuneChainComposition.RuneExecutionContext;
import thebetweenlands.api.rune.impl.RuneStats;
import thebetweenlands.api.rune.impl.RuneTokenDescriptors;
import thebetweenlands.common.registries.AspectRegistry;

public final class RuneBranching extends AbstractRune<RuneBranching> {

	public static final class Blueprint extends AbstractRune.Blueprint<RuneBranching> {
		public Blueprint() {
			super(RuneStats.builder()
					.aspect(AspectRegistry.ORDANIIS, 1)
					.duration(0.005f)
					.build());
		}

		public static final INodeConfiguration CONFIGURATION_1;
		public static final INodeConfiguration CONFIGURATION_2;
		public static final INodeConfiguration CONFIGURATION_3;

		static {
			PortNodeConfiguration.Builder builder = PortNodeConfiguration.builder(RuneTokenDescriptors.ANY);

			builder.in(RuneTokenDescriptors.ANY, Object.class);
			CONFIGURATION_1 = builder.build();

			builder.in(RuneTokenDescriptors.ANY, Object.class);
			builder.in(RuneTokenDescriptors.ANY, Object.class);
			CONFIGURATION_2 = builder.build();

			builder.in(RuneTokenDescriptors.ANY, Object.class);
			builder.in(RuneTokenDescriptors.ANY, Object.class);
			builder.in(RuneTokenDescriptors.ANY, Object.class);
			CONFIGURATION_3 = builder.build();
		}

		@Override
		public List<INodeConfiguration> getConfigurations() {
			return ImmutableList.of(CONFIGURATION_1, CONFIGURATION_2, CONFIGURATION_3);
		}

		@Override
		public RuneBranching create(INodeComposition<RuneExecutionContext> composition, INodeConfiguration configuration) {
			return new RuneBranching(this, composition, configuration);
		}

		@Override
		protected void activate(RuneBranching state, RuneExecutionContext context, INodeIO io) {
			io.branch();
		}
	}

	private RuneBranching(Blueprint blueprint, INodeComposition<RuneExecutionContext> composition, INodeConfiguration configuration) {
		super(blueprint, composition, configuration);
	}
}
