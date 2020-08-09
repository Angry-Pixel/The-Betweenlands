package thebetweenlands.common.herblore.rune;

import java.util.List;

import com.google.common.collect.ImmutableList;

import thebetweenlands.api.rune.INodeComposition;
import thebetweenlands.api.rune.INodeCompositionBlueprint;
import thebetweenlands.api.rune.INodeConfiguration;
import thebetweenlands.api.rune.IRuneChainUser;
import thebetweenlands.api.rune.INodeBlueprint.IConfigurationLinkAccess;
import thebetweenlands.api.rune.impl.AbstractRune;
import thebetweenlands.api.rune.impl.RuneChainComposition.RuneExecutionContext;
import thebetweenlands.api.rune.impl.RuneConfiguration;
import thebetweenlands.api.rune.impl.RuneConfiguration.OutputPort;
import thebetweenlands.api.rune.impl.RuneEffectModifier;
import thebetweenlands.api.rune.impl.RuneStats;
import thebetweenlands.api.rune.impl.RuneTokenDescriptors;
import thebetweenlands.common.registries.AspectRegistry;

public final class RuneSelf extends AbstractRune<RuneSelf> {
	public static final class Blueprint extends AbstractRune.Blueprint<RuneSelf> {
		public Blueprint() {
			super(RuneStats.builder()
					.aspect(AspectRegistry.ORDANIIS, 1)
					.duration(5.0f)
					.build());
		}

		public static final RuneConfiguration CONFIGURATION_1;

		private static final OutputPort<IRuneChainUser> OUT_ENTITY;

		static {
			RuneConfiguration.Builder builder = RuneConfiguration.builder();

			OUT_ENTITY = builder.out(RuneTokenDescriptors.ENTITY, IRuneChainUser.class);

			CONFIGURATION_1 = builder.build();
		}

		@Override
		public List<RuneConfiguration> getConfigurations(IConfigurationLinkAccess linkAccess, boolean provisional) {
			return ImmutableList.of(CONFIGURATION_1);
		}

		@Override
		public RuneSelf create(int index, INodeComposition<RuneExecutionContext> composition, INodeConfiguration configuration) {
			return new RuneSelf(this, index, composition, (RuneConfiguration) configuration);
		}

		@Override
		protected RuneEffectModifier.Subject activate(RuneSelf state, RuneExecutionContext context, INodeIO io) {
			if(state.getConfiguration() == CONFIGURATION_1) {
				OUT_ENTITY.set(io, context.getUser());
			}
			
			return null;
		}
	}

	private RuneSelf(Blueprint blueprint, int index, INodeComposition<RuneExecutionContext> composition, RuneConfiguration configuration) {
		super(blueprint, index, composition, configuration);
	}
}
