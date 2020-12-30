package thebetweenlands.common.herblore.rune;

import java.util.List;

import com.google.common.collect.ImmutableList;

import thebetweenlands.api.runechain.IRuneChainUser;
import thebetweenlands.api.runechain.base.IConfigurationLinkAccess;
import thebetweenlands.api.runechain.base.INodeComposition;
import thebetweenlands.api.runechain.base.INodeConfiguration;
import thebetweenlands.api.runechain.base.INodeIO;
import thebetweenlands.api.runechain.chain.IRuneExecutionContext;
import thebetweenlands.api.runechain.io.ISetter;
import thebetweenlands.api.runechain.io.types.RuneTokenDescriptors;
import thebetweenlands.api.runechain.modifier.Subject;
import thebetweenlands.api.runechain.rune.AbstractRune;
import thebetweenlands.api.runechain.rune.RuneConfiguration;
import thebetweenlands.api.runechain.rune.RuneStats;
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

		private static final ISetter<IRuneChainUser> OUT_ENTITY;

		static {
			RuneConfiguration.Builder builder = RuneConfiguration.create();

			OUT_ENTITY = builder.out(RuneTokenDescriptors.ENTITY).type(IRuneChainUser.class).setter();

			CONFIGURATION_1 = builder.build();
		}

		@Override
		public List<RuneConfiguration> getConfigurations(IConfigurationLinkAccess linkAccess, boolean provisional) {
			return ImmutableList.of(CONFIGURATION_1);
		}

		@Override
		public RuneSelf create(int index, INodeComposition<IRuneExecutionContext> composition, INodeConfiguration configuration) {
			return new RuneSelf(this, index, composition, (RuneConfiguration) configuration);
		}

		@Override
		protected Subject activate(RuneSelf state, IRuneExecutionContext context, INodeIO io) {
			if(state.getConfiguration() == CONFIGURATION_1) {
				OUT_ENTITY.set(io, context.getUser());
			}
			
			return null;
		}
	}

	private RuneSelf(Blueprint blueprint, int index, INodeComposition<IRuneExecutionContext> composition, RuneConfiguration configuration) {
		super(blueprint, index, composition, configuration);
	}
}
