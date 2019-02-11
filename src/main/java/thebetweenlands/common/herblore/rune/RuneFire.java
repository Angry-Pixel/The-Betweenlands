package thebetweenlands.common.herblore.rune;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import thebetweenlands.api.rune.INodeComposition;
import thebetweenlands.api.rune.INodeConfiguration;
import thebetweenlands.api.rune.impl.AbstractRune;
import thebetweenlands.api.rune.impl.PortNodeConfiguration;
import thebetweenlands.api.rune.impl.PortNodeConfiguration.InputPort;
import thebetweenlands.api.rune.impl.RuneChainComposition.RuneExecutionContext;
import thebetweenlands.api.rune.impl.RuneMarkDescriptors;
import thebetweenlands.api.rune.impl.RuneStats;
import thebetweenlands.common.registries.AspectRegistry;

public final class RuneFire extends AbstractRune<RuneFire> {

	public static final class Blueprint extends AbstractRune.Blueprint<RuneFire> {
		public Blueprint() {
			super(RuneStats.builder()
					.aspect(AspectRegistry.FERGALAZ, 1)
					.duration(0.333f)
					.build());
		}

		public static final INodeConfiguration CONFIGURATION_1;
		public static final INodeConfiguration CONFIGURATION_2;

		private static final InputPort<Entity> IN_ENTITY;
		private static final InputPort<BlockPos> IN_POSITION;

		static {
			PortNodeConfiguration.Builder builder = PortNodeConfiguration.builder();

			IN_POSITION = builder.in(RuneMarkDescriptors.BLOCK, BlockPos.class);
			CONFIGURATION_1 = builder.build();

			IN_ENTITY = builder.in(RuneMarkDescriptors.ENTITY, Entity.class);
			CONFIGURATION_2 = builder.build();
		}

		@Override
		public List<INodeConfiguration> getConfigurations() {
			return ImmutableList.of(CONFIGURATION_1, CONFIGURATION_2);
		}

		@Override
		public RuneFire create(INodeComposition<RuneExecutionContext> composition, INodeConfiguration configuration) {
			return new RuneFire(this, composition, configuration);
		}

		@Override
		protected void activate(RuneFire state, RuneExecutionContext context, INodeIO io) {

			if (state.getConfiguration() == CONFIGURATION_1) {
				BlockPos pos = IN_POSITION.get(io);

				if(context.getUser().getWorld().isAirBlock(pos.up())) {
					context.getUser().getWorld().setBlockState(pos.up(), Blocks.FIRE.getDefaultState());
				}
			}

			if (state.getConfiguration() == CONFIGURATION_2) {
				Entity entity = IN_ENTITY.get(io);

				entity.setFire(3);
			}

		}
	}

	private RuneFire(Blueprint blueprint, INodeComposition<RuneExecutionContext> composition, INodeConfiguration configuration) {
		super(blueprint, composition, configuration);
	}
}
