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

public final class RuneInteract extends AbstractRune<RuneInteract> {
	public static final class Blueprint extends AbstractRune.Blueprint<RuneInteract> {
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
		public RuneInteract create(int index, INodeComposition<RuneExecutionContext> composition, INodeConfiguration configuration) {
			return new RuneInteract(this, index, composition, (RuneConfiguration) configuration);
		}

		@Override
		protected RuneEffectModifier.Subject activate(RuneInteract state, RuneExecutionContext context, INodeIO io) {
			OUT_ENTITY_1.set(io, state.target);
			return null;
		}

		private static class EntityInitiationState extends InitiationState {
			private final Entity target;

			private EntityInitiationState(Entity target) {
				this.target = target;
				this.success = true;
			}
		}

		@Override
		public InitiationState checkInitiation(IRuneChainUser user, InitiationPhase phase, InitiationState state) {
			return phase instanceof InteractionInitiationPhase ? new EntityInitiationState(((InteractionInitiationPhase) phase).getTarget()) : null;
		}

		@Override
		public void initiate(IRuneChainUser user, InitiationState state, RuneInteract node) {
			node.target = ((EntityInitiationState) state).target;
		}
	}

	private Entity target;

	private RuneInteract(Blueprint blueprint, int index, INodeComposition<RuneExecutionContext> composition, RuneConfiguration configuration) {
		super(blueprint, index, composition, configuration);
	}
}
