package thebetweenlands.common.herblore.rune;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import thebetweenlands.api.rune.IBlockTarget;
import thebetweenlands.api.rune.INodeComposition;
import thebetweenlands.api.rune.INodeConfiguration;
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

public final class RuneMarkNearby extends AbstractRune<RuneMarkNearby> {

	public static final class Blueprint extends AbstractRune.Blueprint<RuneMarkNearby> {
		public Blueprint() {
			super(RuneStats.builder()
					.aspect(AspectRegistry.ORDANIIS, 1)
					.duration(5.0f)
					.build());
		}

		public static final RuneConfiguration CONFIGURATION_1;
		public static final RuneConfiguration CONFIGURATION_2;

		private static final IGetter<IBlockTarget> IN_POSITION_2;
		private static final ISetter<Collection<Entity>> OUT_ENTITIES_2;

		private static final ISetter<Collection<Entity>> OUT_ENTITIES;

		static {
			RuneConfiguration.Builder builder = RuneConfiguration.create();

			OUT_ENTITIES = builder.out(RuneTokenDescriptors.ENTITY).type(Entity.class).collection().setter();

			CONFIGURATION_1 = builder.build();

			IN_POSITION_2 = builder.in(RuneTokenDescriptors.BLOCK).type(IBlockTarget.class).serializer(InputSerializers.BLOCK).getter();
			OUT_ENTITIES_2 = builder.out(RuneTokenDescriptors.ENTITY).type(Entity.class).collection().setter();

			CONFIGURATION_2 = builder.build();
		}

		@Override
		public List<RuneConfiguration> getConfigurations(IConfigurationLinkAccess linkAccess, boolean provisional) {
			return ImmutableList.of(CONFIGURATION_1, CONFIGURATION_2);
		}

		@Override
		public RuneMarkNearby create(int index, INodeComposition<RuneExecutionContext> composition, INodeConfiguration configuration) {
			return new RuneMarkNearby(this, index, composition, (RuneConfiguration) configuration);
		}

		@Override
		protected RuneEffectModifier.Subject activate(RuneMarkNearby state, RuneExecutionContext context, INodeIO io) {

			if(state.getConfiguration() == CONFIGURATION_1) {
				int range = 6;
				AxisAlignedBB aabb = new AxisAlignedBB(new BlockPos(context.getUser().getPosition())).grow(range);
				
				OUT_ENTITIES.set(io, context.getUser().getWorld().getEntitiesWithinAABB(Entity.class, aabb));
			} else {
				BlockPos center = IN_POSITION_2.get(io).block();

				int range = 6;
				AxisAlignedBB aabb = new AxisAlignedBB(center).grow(range);

				OUT_ENTITIES_2.set(io, context.getUser().getWorld().getEntitiesWithinAABB(Entity.class, aabb));
			}
			
			return null;
		}
	}

	private RuneMarkNearby(Blueprint blueprint, int index, INodeComposition<RuneExecutionContext> composition, RuneConfiguration configuration) {
		super(blueprint, index, composition, configuration);
	}
}
