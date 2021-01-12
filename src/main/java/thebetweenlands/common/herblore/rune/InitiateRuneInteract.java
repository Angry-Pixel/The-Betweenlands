package thebetweenlands.common.herblore.rune;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.entity.Entity;
import thebetweenlands.api.runechain.IRuneChainUser;
import thebetweenlands.api.runechain.base.IConfigurationLinkAccess;
import thebetweenlands.api.runechain.base.INodeComposition;
import thebetweenlands.api.runechain.base.INodeConfiguration;
import thebetweenlands.api.runechain.base.INodeIO;
import thebetweenlands.api.runechain.chain.IRuneExecutionContext;
import thebetweenlands.api.runechain.initiation.InitiationPhase;
import thebetweenlands.api.runechain.initiation.InitiationState;
import thebetweenlands.api.runechain.initiation.InteractionInitiationPhase;
import thebetweenlands.api.runechain.io.ISetter;
import thebetweenlands.api.runechain.io.OutputKey;
import thebetweenlands.api.runechain.io.types.RuneTokenDescriptors;
import thebetweenlands.api.runechain.modifier.Subject;
import thebetweenlands.api.runechain.rune.AbstractRune;
import thebetweenlands.api.runechain.rune.RuneConfiguration;
import thebetweenlands.api.runechain.rune.RuneStats;
import thebetweenlands.common.registries.AspectRegistry;

public final class InitiateRuneInteract extends AbstractRune<InitiateRuneInteract> {
	public static final class Blueprint extends AbstractRune.Blueprint<InitiateRuneInteract> {
		public Blueprint() {
			super(RuneStats.builder()
					.aspect(AspectRegistry.ORDANIIS, 1)
					.duration(0.0f)
					.build());
		}

		public static final RuneConfiguration CONFIGURATION_1;
		public static final ISetter<Entity> OUT_ENTITY_1;

		static {
			RuneConfiguration.Builder builder = RuneConfiguration.create();

			OUT_ENTITY_1 = builder.out(RuneTokenDescriptors.ENTITY).type(Entity.class).setter();
			CONFIGURATION_1 = builder.build();
		}

		@Override
		public List<RuneConfiguration> getConfigurations(IConfigurationLinkAccess linkAccess, boolean provisional) {
			return ImmutableList.of(CONFIGURATION_1);
		}

		@Override
		public InitiateRuneInteract create(int index, INodeComposition<IRuneExecutionContext> composition, INodeConfiguration configuration) {
			return new InitiateRuneInteract(this, index, composition, (RuneConfiguration) configuration);
		}

		@Override
		protected Subject activate(InitiateRuneInteract state, IRuneExecutionContext context, INodeIO io) {
			OUT_ENTITY_1.set(io, state.target);
			return null;
		}

		@Override
		public InitiationState<InitiateRuneInteract> checkInitiation(IRuneChainUser user, InitiationPhase phase, InitiationState<InitiateRuneInteract> initiationState) {
			return phase instanceof InteractionInitiationPhase ? InitiationState.success(state -> {
				state.target = ((InteractionInitiationPhase) phase).getTarget();
			}) : null;
		}
	}

	private Entity target;

	private InitiateRuneInteract(Blueprint blueprint, int index, INodeComposition<IRuneExecutionContext> composition, RuneConfiguration configuration) {
		super(blueprint, index, composition, configuration);
	}
}
