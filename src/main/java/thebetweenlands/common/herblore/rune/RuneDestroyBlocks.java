package thebetweenlands.common.herblore.rune;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import thebetweenlands.api.rune.INodeComposition;
import thebetweenlands.api.rune.INodeConfiguration;
import thebetweenlands.api.rune.impl.AbstractRune;
import thebetweenlands.api.rune.impl.RuneMarkDescriptors;
import thebetweenlands.api.rune.impl.PortNodeConfiguration;
import thebetweenlands.api.rune.impl.PortNodeConfiguration.InputPort;
import thebetweenlands.api.rune.impl.RuneChainComposition.RuneExecutionContext;
import thebetweenlands.api.rune.impl.RuneStats;
import thebetweenlands.common.registries.AspectRegistry;

public final class RuneDestroyBlocks extends AbstractRune<RuneDestroyBlocks> {

	public static final class Blueprint extends AbstractRune.Blueprint<RuneDestroyBlocks> {
		public Blueprint() {
			super(RuneStats.builder()
					.aspect(AspectRegistry.ORDANIIS, 1)
					.duration(0.333f)
					.build());
		}

		private static final INodeConfiguration CONFIGURATION_1;

		private static final InputPort<Entity> IN_ENTITY;
		private static final InputPort<Collection<BlockPos>> IN_POSITIONS;

		static {
			PortNodeConfiguration.Builder builder = PortNodeConfiguration.builder();

			IN_POSITIONS = builder.multiIn(BlockPos.class, RuneMarkDescriptors.BLOCK_POS);
			IN_ENTITY = builder.in(Entity.class, RuneMarkDescriptors.ENTITY);

			CONFIGURATION_1 = builder.build();
		}

		@Override
		public Set<INodeConfiguration> getConfigurations() {
			return ImmutableSet.of(CONFIGURATION_1);
		}

		@Override
		public RuneDestroyBlocks create(INodeComposition<RuneExecutionContext> composition, INodeConfiguration configuration) {
			return new RuneDestroyBlocks(this, composition, configuration);
		}

		@Override
		protected void activate(RuneDestroyBlocks state, RuneExecutionContext context, INodeIO io) {

			if (state.getConfiguration() == CONFIGURATION_1) {
				Collection<BlockPos> positions = IN_POSITIONS.get(io);
				Entity entity = IN_ENTITY.get(io);

				for(BlockPos pos : positions) {
					entity.world.setBlockToAir(pos);
				}
			}

		}
	}

	private RuneDestroyBlocks(Blueprint blueprint, INodeComposition<RuneExecutionContext> composition, INodeConfiguration configuration) {
		super(blueprint, composition, configuration);
	}
}
