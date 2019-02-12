package thebetweenlands.common.herblore.rune;

import java.util.List;

import com.google.common.collect.ImmutableList;

import thebetweenlands.api.rune.INodeComposition;
import thebetweenlands.api.rune.INodeConfiguration;
import thebetweenlands.api.rune.impl.AbstractRune;
import thebetweenlands.api.rune.impl.PortNodeConfiguration;
import thebetweenlands.api.rune.impl.PortNodeConfiguration.InputPort;
import thebetweenlands.api.rune.impl.RuneChainComposition.RuneExecutionContext;
import thebetweenlands.api.rune.impl.RuneMarkDescriptors;
import thebetweenlands.api.rune.impl.RuneStats;
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

		private static final InputPort<?> IN;

		static {
			PortNodeConfiguration.Builder builder = PortNodeConfiguration.builder(RuneMarkDescriptors.ANY);

			IN = builder.in(RuneMarkDescriptors.ANY, Object.class);

			CONFIGURATION_1 = builder.build();
		}

		@Override
		public List<INodeConfiguration> getConfigurations() {
			return ImmutableList.of(CONFIGURATION_1);
		}

		@Override
		public RuneBranching create(INodeComposition<RuneExecutionContext> composition, INodeConfiguration configuration) {
			return new RuneBranching(this, composition, configuration);
		}

		@Override
		protected void activate(RuneBranching state, RuneExecutionContext context, INodeIO io) {
			if(state.getConfiguration() == CONFIGURATION_1) {
				io.branch();
			}
		}
	}

	private RuneBranching(Blueprint blueprint, INodeComposition<RuneExecutionContext> composition, INodeConfiguration configuration) {
		super(blueprint, composition, configuration);
	}
}
