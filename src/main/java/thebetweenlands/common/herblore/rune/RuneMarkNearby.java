package thebetweenlands.common.herblore.rune;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import thebetweenlands.api.rune.INodeComposition;
import thebetweenlands.api.rune.INodeConfiguration;
import thebetweenlands.api.rune.impl.AbstractRune;
import thebetweenlands.api.rune.impl.PortNodeConfiguration;
import thebetweenlands.api.rune.impl.PortNodeConfiguration.InputPort;
import thebetweenlands.api.rune.impl.PortNodeConfiguration.OutputPort;
import thebetweenlands.api.rune.impl.RuneChainComposition.RuneExecutionContext;
import thebetweenlands.api.rune.impl.RuneTokenDescriptors;
import thebetweenlands.api.rune.impl.RuneStats;
import thebetweenlands.common.registries.AspectRegistry;

public final class RuneMarkNearby extends AbstractRune<RuneMarkNearby> {

	public static final class Blueprint extends AbstractRune.Blueprint<RuneMarkNearby> {
		public Blueprint() {
			super(RuneStats.builder()
					.aspect(AspectRegistry.ORDANIIS, 1)
					.duration(5.0f)
					.build());
		}

		public static final INodeConfiguration CONFIGURATION_1;
		public static final INodeConfiguration CONFIGURATION_2;

		private static final InputPort<BlockPos> IN_POSITION_2;
		private static final OutputPort<Collection<Entity>> OUT_ENTITIES_2;

		private static final OutputPort<Collection<Entity>> OUT_ENTITIES;

		static {
			PortNodeConfiguration.Builder builder = PortNodeConfiguration.builder();

			OUT_ENTITIES = builder.multiOut(RuneTokenDescriptors.ENTITY, Entity.class);

			CONFIGURATION_1 = builder.build();

			IN_POSITION_2 = builder.in(RuneTokenDescriptors.BLOCK, BlockPos.class);
			OUT_ENTITIES_2 = builder.multiOut(RuneTokenDescriptors.ENTITY, Entity.class);

			CONFIGURATION_2 = builder.build();
		}

		@Override
		public List<INodeConfiguration> getConfigurations() {
			return ImmutableList.of(CONFIGURATION_1, CONFIGURATION_2);
		}

		@Override
		public RuneMarkNearby create(INodeComposition<RuneExecutionContext> composition, INodeConfiguration configuration) {
			return new RuneMarkNearby(this, composition, configuration);
		}

		@Override
		protected void activate(RuneMarkNearby state, RuneExecutionContext context, INodeIO io) {

			if(state.getConfiguration() == CONFIGURATION_1) {
				int range = 6;
				AxisAlignedBB aabb = new AxisAlignedBB(new BlockPos(context.getUser().getPosition())).grow(range);
				
				OUT_ENTITIES.set(io, context.getUser().getWorld().getEntitiesWithinAABB(Entity.class, aabb));
			} else {
				BlockPos center = IN_POSITION_2.get(io);

				int range = 6;
				AxisAlignedBB aabb = new AxisAlignedBB(center).grow(range);

				OUT_ENTITIES_2.set(io, context.getUser().getWorld().getEntitiesWithinAABB(Entity.class, aabb));
			}
		}
	}

	private RuneMarkNearby(Blueprint blueprint, INodeComposition<RuneExecutionContext> composition, INodeConfiguration configuration) {
		super(blueprint, composition, configuration);
	}
}
