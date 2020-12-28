package thebetweenlands.common.herblore.rune;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.entity.Entity;
import thebetweenlands.api.rune.IBlockTarget;
import thebetweenlands.api.rune.INodeComposition;
import thebetweenlands.api.rune.INodeConfiguration;
import thebetweenlands.api.rune.IRuneChainUser;
import thebetweenlands.api.rune.IRuneEffect;
import thebetweenlands.api.rune.IVectorTarget;
import thebetweenlands.api.rune.impl.AbstractRune;
import thebetweenlands.api.rune.impl.IGetter;
import thebetweenlands.api.rune.impl.InputSerializers;
import thebetweenlands.api.rune.impl.RuneChainComposition.RuneExecutionContext;
import thebetweenlands.api.rune.impl.RuneConfiguration;
import thebetweenlands.api.rune.impl.RuneEffectModifier;
import thebetweenlands.api.rune.impl.RuneStats;
import thebetweenlands.api.rune.impl.RuneTokenDescriptors;
import thebetweenlands.common.registries.AspectRegistry;

public final class RuneEnforcing extends AbstractRune<RuneEnforcing> {

	public static final class Blueprint extends AbstractRune.Blueprint<RuneEnforcing> {
		public Blueprint() {
			super(RuneStats.builder()
					.aspect(AspectRegistry.ORDANIIS, 1)
					.duration(0.333f)
					.build());
		}

		public static final RuneConfiguration CONFIGURATION_1;
		private static final IGetter<?> IN_ENTITY_1;
		private static final IGetter<IRuneEffect> IN_EFFECT_1;

		public static final RuneConfiguration CONFIGURATION_2;
		private static final IGetter<IVectorTarget> IN_POSITION_2;
		private static final IGetter<IRuneEffect> IN_EFFECT_2;

		public static final RuneConfiguration CONFIGURATION_3;
		private static final IGetter<IBlockTarget> IN_BLOCK_3;
		private static final IGetter<IRuneEffect> IN_EFFECT_3;

		static {
			RuneConfiguration.Builder builder = RuneConfiguration.create();

			IN_ENTITY_1 = builder.in(RuneTokenDescriptors.ENTITY).type(Entity.class, IRuneChainUser.class).serializer(InputSerializers.USER).getter(); //TODO Needs custom serializer
			IN_EFFECT_1 = builder.in(RuneTokenDescriptors.EFFECT).type(IRuneEffect.class).getter();
			CONFIGURATION_1 = builder.build();

			IN_POSITION_2 = builder.in(RuneTokenDescriptors.POSITION).type(IVectorTarget.class).serializer(InputSerializers.VECTOR).getter();
			IN_EFFECT_2 = builder.in(RuneTokenDescriptors.EFFECT).type(IRuneEffect.class).getter();
			CONFIGURATION_2 = builder.build();

			IN_BLOCK_3 = builder.in(RuneTokenDescriptors.BLOCK).type(IBlockTarget.class).serializer(InputSerializers.BLOCK).getter();
			IN_EFFECT_3 = builder.in(RuneTokenDescriptors.EFFECT).type(IRuneEffect.class).getter();
			CONFIGURATION_3 = builder.build();
		}

		@Override
		public List<RuneConfiguration> getConfigurations(IConfigurationLinkAccess linkAccess, boolean provisional) {
			return ImmutableList.of(CONFIGURATION_1, CONFIGURATION_2, CONFIGURATION_3);
		}

		@Override
		public RuneEnforcing create(int index, INodeComposition<RuneExecutionContext> composition, INodeConfiguration configuration) {
			return new RuneEnforcing(this, index, composition, (RuneConfiguration) configuration);
		}

		@Override
		protected RuneEffectModifier.Subject activate(RuneEnforcing state, RuneExecutionContext context, INodeIO io) {

			if(state.getConfiguration() == CONFIGURATION_1) {
				IRuneEffect effect = IN_EFFECT_1.get(io);

				IN_ENTITY_1.run(io, Entity.class, entity -> effect.apply(entity.world, entity));

				IN_ENTITY_1.run(io, IRuneChainUser.class, user -> {
					if(!effect.apply(user.getWorld(), user)) {
						Entity entity = user.getEntity();

						if(entity != null) {
							effect.apply(user.getWorld(), entity);
						}
					}
				});
			} else if(state.getConfiguration() == CONFIGURATION_2 && IN_POSITION_2.get(io) != null) {
				IRuneEffect effect = IN_EFFECT_2.get(io);
				effect.apply(context.getUser().getWorld(), IN_POSITION_2.get(io).vec());
			} else if(state.getConfiguration() == CONFIGURATION_3 && IN_BLOCK_3.get(io) != null) {
				IRuneEffect effect = IN_EFFECT_3.get(io);
				effect.apply(context.getUser().getWorld(), IN_BLOCK_3.get(io).block());
			}

			return null;
		}
		
		@Override
		protected boolean isDelegatingRuneEffectModifier(RuneEnforcing state, AbstractRune<?> target, AbstractRune<?> outputRune, int inputIndex) {
			if(state.getConfiguration() == CONFIGURATION_1) {
				return inputIndex == IN_EFFECT_1.index();
			} else if(state.getConfiguration() == CONFIGURATION_2) {
				return inputIndex == IN_EFFECT_2.index();
			} else {
				return inputIndex == IN_EFFECT_3.index();
			}
		}
	}

	private RuneEnforcing(Blueprint blueprint, int index, INodeComposition<RuneExecutionContext> composition, RuneConfiguration configuration) {
		super(blueprint, index, composition, configuration);
	}
}
