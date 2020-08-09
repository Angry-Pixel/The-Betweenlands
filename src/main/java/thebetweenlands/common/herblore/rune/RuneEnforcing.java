package thebetweenlands.common.herblore.rune;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import thebetweenlands.api.rune.INodeComposition;
import thebetweenlands.api.rune.INodeConfiguration;
import thebetweenlands.api.rune.IRuneChainUser;
import thebetweenlands.api.rune.IRuneEffect;
import thebetweenlands.api.rune.impl.AbstractRune;
import thebetweenlands.api.rune.impl.InputSerializers;
import thebetweenlands.api.rune.impl.RuneChainComposition.RuneExecutionContext;
import thebetweenlands.api.rune.impl.RuneConfiguration;
import thebetweenlands.api.rune.impl.RuneConfiguration.InputPort;
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
		private static final InputPort<?> IN_ENTITY_1;
		private static final InputPort<IRuneEffect> IN_EFFECT_1;

		public static final RuneConfiguration CONFIGURATION_2;
		private static final InputPort<Vec3d> IN_POSITION_2;
		private static final InputPort<IRuneEffect> IN_EFFECT_2;

		public static final RuneConfiguration CONFIGURATION_3;
		private static final InputPort<BlockPos> IN_BLOCK_3;
		private static final InputPort<IRuneEffect> IN_EFFECT_3;

		static {
			RuneConfiguration.Builder builder = RuneConfiguration.builder();

			IN_ENTITY_1 = builder.in(RuneTokenDescriptors.ENTITY, InputSerializers.USER, Entity.class, IRuneChainUser.class); //TODO Needs custom serializer
			IN_EFFECT_1 = builder.in(RuneTokenDescriptors.EFFECT, null, IRuneEffect.class);
			CONFIGURATION_1 = builder.build();

			IN_POSITION_2 = builder.in(RuneTokenDescriptors.POSITION, InputSerializers.VECTOR, Vec3d.class);
			IN_EFFECT_2 = builder.in(RuneTokenDescriptors.EFFECT, null, IRuneEffect.class);
			CONFIGURATION_2 = builder.build();

			IN_BLOCK_3 = builder.in(RuneTokenDescriptors.BLOCK, InputSerializers.BLOCK, BlockPos.class);
			IN_EFFECT_3 = builder.in(RuneTokenDescriptors.EFFECT, null, IRuneEffect.class);
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
			} else if(state.getConfiguration() == CONFIGURATION_2) {
				IRuneEffect effect = IN_EFFECT_2.get(io);
				effect.apply(context.getUser().getWorld(), IN_POSITION_2.get(io));
			} else if(state.getConfiguration() == CONFIGURATION_3) {
				IRuneEffect effect = IN_EFFECT_3.get(io);
				effect.apply(context.getUser().getWorld(), IN_BLOCK_3.get(io));
			}

			return null;
		}
	}

	private RuneEnforcing(Blueprint blueprint, int index, INodeComposition<RuneExecutionContext> composition, RuneConfiguration configuration) {
		super(blueprint, index, composition, configuration);
	}
}
