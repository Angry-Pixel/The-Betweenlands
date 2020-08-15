package thebetweenlands.common.herblore.rune;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.entity.Entity;
import thebetweenlands.api.rune.INodeComposition;
import thebetweenlands.api.rune.INodeConfiguration;
import thebetweenlands.api.rune.IRuneChainUser;
import thebetweenlands.api.rune.impl.AbstractRune;
import thebetweenlands.api.rune.impl.RuneChainComposition.RuneExecutionContext;
import thebetweenlands.api.rune.impl.RuneConfiguration;
import thebetweenlands.api.rune.impl.RuneConfiguration.OutputPort;
import thebetweenlands.api.rune.impl.RuneEffectModifier;
import thebetweenlands.api.rune.impl.RuneStats;
import thebetweenlands.api.rune.impl.RuneTokenDescriptors;
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
		public static final OutputPort<Entity> OUT_ENTITY_1;

		static {
			RuneConfiguration.Builder builder = RuneConfiguration.builder();

			OUT_ENTITY_1 = builder.out(RuneTokenDescriptors.ENTITY, Entity.class);
			CONFIGURATION_1 = builder.build();
		}

		@Override
		public List<RuneConfiguration> getConfigurations(IConfigurationLinkAccess linkAccess, boolean provisional) {
			return ImmutableList.of(CONFIGURATION_1);
		}

		@Override
		public InitiateRuneInteract create(int index, INodeComposition<RuneExecutionContext> composition, INodeConfiguration configuration) {
			return new InitiateRuneInteract(this, index, composition, (RuneConfiguration) configuration);
		}

		@Override
		protected RuneEffectModifier.Subject activate(InitiateRuneInteract state, RuneExecutionContext context, INodeIO io) {
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

	private InitiateRuneInteract(Blueprint blueprint, int index, INodeComposition<RuneExecutionContext> composition, RuneConfiguration configuration) {
		super(blueprint, index, composition, configuration);
	}
}
