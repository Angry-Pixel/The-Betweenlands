package thebetweenlands.common.herblore.rune;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thebetweenlands.api.rune.INodeComposition;
import thebetweenlands.api.rune.INodeConfiguration;
import thebetweenlands.api.rune.IRuneChainUser;
import thebetweenlands.api.rune.IRuneEffect;
import thebetweenlands.api.rune.impl.AbstractRune;
import thebetweenlands.api.rune.impl.IGetter;
import thebetweenlands.api.rune.impl.ISetter;
import thebetweenlands.api.rune.impl.InputSerializers;
import thebetweenlands.api.rune.impl.RuneChainComposition.RuneExecutionContext;
import thebetweenlands.api.rune.impl.RuneConfiguration;
import thebetweenlands.api.rune.impl.RuneEffectModifier;
import thebetweenlands.api.rune.impl.RuneStats;
import thebetweenlands.api.rune.impl.RuneTokenDescriptors;
import thebetweenlands.common.registries.AspectRegistry;

public final class RuneBounce extends AbstractRune<RuneBounce> {

	public static final class Blueprint extends AbstractRune.Blueprint<RuneBounce> {
		public Blueprint() {
			super(RuneStats.builder()
					.aspect(AspectRegistry.FERGALAZ, 1)
					.duration(0.333f)
					.build());
		}

		public static final RuneConfiguration CONFIGURATION_1;
		private static final IGetter<Entity> IN_ENTITY_1;

		public static final RuneConfiguration CONFIGURATION_2;
		private static final ISetter<IRuneEffect> OUT_EFFECT_2;

		private static final IRuneEffect BOUNCE_EFFECT = new IRuneEffect() {
			@Override
			public boolean apply(World world, IRuneChainUser user) {
				Entity entity = user.getEntity();
				if(entity != null) {
					entity.addVelocity(0, 1, 0);
					return true;
				}
				return true;
			}

			@Override
			public boolean apply(World world, Entity entity) {
				entity.addVelocity(0, 1, 0);
				return true;
			}

			@Override
			public boolean apply(World world, BlockPos pos) {
				return false;
			}

			@Override
			public boolean apply(World world, Vec3d position) {
				return false;
			}
		};

		static {
			RuneConfiguration.Builder builder = RuneConfiguration.create();

			IN_ENTITY_1 = builder.in(RuneTokenDescriptors.ENTITY).type(Entity.class).serializer(InputSerializers.ENTITY).getter();
			CONFIGURATION_1 = builder.build();

			OUT_EFFECT_2 = builder.out(RuneTokenDescriptors.EFFECT).type(IRuneEffect.class).setter();
			CONFIGURATION_2 = builder.build();
		}

		@Override
		public List<RuneConfiguration> getConfigurations(IConfigurationLinkAccess linkAccess, boolean provisional) {
			return ImmutableList.of(CONFIGURATION_1, CONFIGURATION_2);
		}

		@Override
		public RuneBounce create(int index, INodeComposition<RuneExecutionContext> composition, INodeConfiguration configuration) {
			return new RuneBounce(this, index, composition, (RuneConfiguration) configuration);
		}

		@Override
		protected RuneEffectModifier.Subject activate(RuneBounce state, RuneExecutionContext context, INodeIO io) {

			if(state.getConfiguration() == CONFIGURATION_1) {
				BOUNCE_EFFECT.apply(context.getUser().getWorld(), IN_ENTITY_1.get(io));
			} else if(state.getConfiguration() == CONFIGURATION_2) {
				OUT_EFFECT_2.set(io, BOUNCE_EFFECT);
			}

			return null;
		}
	}

	private RuneBounce(Blueprint blueprint, int index, INodeComposition<RuneExecutionContext> composition, RuneConfiguration configuration) {
		super(blueprint, index, composition, configuration);
	}
}
