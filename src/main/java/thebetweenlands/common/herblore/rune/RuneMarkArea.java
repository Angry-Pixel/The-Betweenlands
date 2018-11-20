package thebetweenlands.common.herblore.rune;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import thebetweenlands.api.rune.INodeComposition;
import thebetweenlands.api.rune.INodeConfiguration;
import thebetweenlands.api.rune.impl.AbstractRune;
import thebetweenlands.api.rune.impl.RuneMarkDescriptors;
import thebetweenlands.api.rune.impl.PortNodeConfiguration;
import thebetweenlands.api.rune.impl.PortNodeConfiguration.OutputPort;
import thebetweenlands.api.rune.impl.RuneChainComposition.RuneExecutionContext;
import thebetweenlands.api.rune.impl.RuneStats;
import thebetweenlands.common.registries.AspectRegistry;

public final class RuneMarkArea extends AbstractRune<RuneMarkArea> {

	public static final class Blueprint extends AbstractRune.Blueprint<RuneMarkArea> {
		public Blueprint() {
			super(RuneStats.builder()
					.aspect(AspectRegistry.ORDANIIS, 1)
					.duration(5.0f)
					.build());
		}

		private static final INodeConfiguration CONFIGURATION_1;

		private static final OutputPort<Collection<BlockPos>> OUT_POSITIONS;
		private static final OutputPort<Entity> OUT_ENTITY;

		static {
			PortNodeConfiguration.Builder builder = PortNodeConfiguration.builder();

			OUT_POSITIONS = builder.multiOut(BlockPos.class, RuneMarkDescriptors.BLOCK);
			OUT_ENTITY = builder.out(Entity.class, RuneMarkDescriptors.ENTITY);

			CONFIGURATION_1 = builder.build();
		}

		@Override
		public Set<INodeConfiguration> getConfigurations() {
			return ImmutableSet.of(CONFIGURATION_1);
		}

		@Override
		public RuneMarkArea create(INodeComposition<RuneExecutionContext> composition, INodeConfiguration configuration) {
			return new RuneMarkArea(this, composition, configuration);
		}

		@Override
		protected void activate(RuneMarkArea state, RuneExecutionContext context, INodeIO io) {
			if (state.getConfiguration() == CONFIGURATION_1) {
				List<BlockPos> positions = new ArrayList<>();

				int range = 6;
				for(int xo = -range; xo <= range; xo++) {
					for(int yo = -range; yo <= range; yo++) {
						for(int zo = -range; zo <= range; zo++) {
							positions.add(new BlockPos(context.getUser().getPosition().addVector(xo, yo, zo)));
						}
					}
				}

				//System.out.println("Node 1: " + positions + " " + context.getEntity());

				OUT_POSITIONS.set(io, positions);
				OUT_ENTITY.set(io, context.getUser().getEntity());
			}			
		}
	}

	private RuneMarkArea(Blueprint blueprint, INodeComposition<RuneExecutionContext> composition, INodeConfiguration configuration) {
		super(blueprint, composition, configuration);
	}
}
