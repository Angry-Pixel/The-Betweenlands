package thebetweenlands.common.herblore.rune;

import java.util.List;

import com.google.common.collect.ImmutableList;

import thebetweenlands.api.rune.INodeComposition;
import thebetweenlands.api.rune.INodeConfiguration;
import thebetweenlands.api.rune.IRuneChainUser;
import thebetweenlands.api.rune.impl.AbstractRune;
import thebetweenlands.api.rune.impl.PortNodeConfiguration;
import thebetweenlands.api.rune.impl.PortNodeConfiguration.OutputPort;
import thebetweenlands.api.rune.impl.RuneChainComposition.RuneExecutionContext;
import thebetweenlands.api.rune.impl.RuneMarkDescriptors;
import thebetweenlands.api.rune.impl.RuneStats;
import thebetweenlands.common.registries.AspectRegistry;

public final class RuneSelf extends AbstractRune<RuneSelf> {
	public static final class Blueprint extends AbstractRune.Blueprint<RuneSelf> {
		public Blueprint() {
			super(RuneStats.builder()
					.aspect(AspectRegistry.ORDANIIS, 1)
					.duration(5.0f)
					.build());
		}

		public static final INodeConfiguration CONFIGURATION_1;

		private static final OutputPort<IRuneChainUser> OUT_ENTITY;

		static {
			PortNodeConfiguration.Builder builder = PortNodeConfiguration.builder();

			OUT_ENTITY = builder.out(RuneMarkDescriptors.ENTITY, IRuneChainUser.class);

			CONFIGURATION_1 = builder.build();
		}

		@Override
		public List<INodeConfiguration> getConfigurations() {
			return ImmutableList.of(CONFIGURATION_1);
		}

		@Override
		public RuneSelf create(INodeComposition<RuneExecutionContext> composition, INodeConfiguration configuration) {
			return new RuneSelf(this, composition, configuration);
		}

		@Override
		protected void activate(RuneSelf state, RuneExecutionContext context, INodeIO io) {
			if(state.getConfiguration() == CONFIGURATION_1) {
				OUT_ENTITY.set(io, context.getUser());
			} 
		}
	}

	private RuneSelf(Blueprint blueprint, INodeComposition<RuneExecutionContext> composition, INodeConfiguration configuration) {
		super(blueprint, composition, configuration);
	}
}
