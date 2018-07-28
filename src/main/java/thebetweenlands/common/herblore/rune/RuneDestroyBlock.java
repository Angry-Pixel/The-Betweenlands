package thebetweenlands.common.herblore.rune;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import thebetweenlands.api.rune.INodeComposition;
import thebetweenlands.api.rune.INodeConfiguration;
import thebetweenlands.api.rune.impl.AbstractRune;
import thebetweenlands.api.rune.impl.PortNodeConfiguration;
import thebetweenlands.api.rune.impl.RuneStats;
import thebetweenlands.api.rune.impl.PortNodeConfiguration.InputPort;
import thebetweenlands.api.rune.impl.RuneChain.RuneExecutionContext;
import thebetweenlands.common.registries.AspectRegistry;

public final class RuneDestroyBlock extends AbstractRune<RuneDestroyBlock> {

	public static final class Blueprint extends AbstractRune.Blueprint<RuneDestroyBlock> {
		public Blueprint() {
			super(RuneStats.builder()
					.aspect(AspectRegistry.ORDANIIS, 1)
					.duration(0.333f)
					.build());
		}

		private static final INodeConfiguration CONFIGURATION_1;

		private static final InputPort<Entity> IN_ENTITY;
		private static final InputPort<BlockPos> IN_POSITION;

		static {
			PortNodeConfiguration.Builder builder = PortNodeConfiguration.builder();

			IN_POSITION = builder.in(BlockPos.class);
			IN_ENTITY = builder.in(Entity.class);

			CONFIGURATION_1 = builder.build();
		}

		@Override
		public Set<INodeConfiguration> getConfigurations() {
			return ImmutableSet.of(CONFIGURATION_1);
		}

		@Override
		public RuneDestroyBlock create(INodeComposition<RuneExecutionContext> composition, INodeConfiguration configuration) {
			return new RuneDestroyBlock(this, composition, configuration);
		}

		@Override
		protected void activate(RuneDestroyBlock state, RuneExecutionContext context, INodeIO io) {

			if (state.getConfiguration() == CONFIGURATION_1) {
				BlockPos position = IN_POSITION.get(io);
				Entity entity = IN_ENTITY.get(io);

				entity.world.setBlockToAir(position);
			}

		}
	}

	private RuneDestroyBlock(Blueprint blueprint, INodeComposition<RuneExecutionContext> composition, INodeConfiguration configuration) {
		super(blueprint, composition, configuration);
	}
}
