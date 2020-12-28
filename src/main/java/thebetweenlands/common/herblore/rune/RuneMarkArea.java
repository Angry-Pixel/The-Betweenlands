package thebetweenlands.common.herblore.rune;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;

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
import thebetweenlands.api.rune.impl.StaticBlockTarget;
import thebetweenlands.common.registries.AspectRegistry;

public final class RuneMarkArea extends AbstractRune<RuneMarkArea> {

	public static final class Blueprint extends AbstractRune.Blueprint<RuneMarkArea> {
		public Blueprint() {
			super(RuneStats.builder()
					.aspect(AspectRegistry.ORDANIIS, 1)
					.duration(5.0f)
					.build());
		}

		public static final RuneConfiguration CONFIGURATION_1;
		public static final RuneConfiguration CONFIGURATION_2;

		private static final IGetter<IBlockTarget> IN_POSITION_2;
		private static final ISetter<Collection<IBlockTarget>> OUT_POSITIONS_2;
		
		private static final ISetter<Collection<IBlockTarget>> OUT_POSITIONS;

		static {
			RuneConfiguration.Builder builder = RuneConfiguration.create();

			OUT_POSITIONS = builder.out(RuneTokenDescriptors.BLOCK).type(IBlockTarget.class).collection().setter();
			CONFIGURATION_1 = builder.build();
			
			IN_POSITION_2 = builder.in(RuneTokenDescriptors.BLOCK).type(IBlockTarget.class).serializer(InputSerializers.BLOCK).getter();
			OUT_POSITIONS_2 = builder.out(RuneTokenDescriptors.BLOCK).type(IBlockTarget.class).collection().setter();
			CONFIGURATION_2 = builder.build();
		}

		@Override
		public List<RuneConfiguration> getConfigurations(IConfigurationLinkAccess linkAccess, boolean provisional) {
			return ImmutableList.of(CONFIGURATION_1, CONFIGURATION_2);
		}

		@Override
		public RuneMarkArea create(int index, INodeComposition<RuneExecutionContext> composition, INodeConfiguration configuration) {
			return new RuneMarkArea(this, index, composition, (RuneConfiguration) configuration);
		}

		@Override
		protected RuneEffectModifier.Subject activate(RuneMarkArea state, RuneExecutionContext context, INodeIO io) {
			
			if(state.getConfiguration() == CONFIGURATION_1) {
				List<IBlockTarget> positions = new ArrayList<>();

				int range = 6;
				for(int xo = -range; xo <= range; xo++) {
					for(int yo = -range; yo <= range; yo++) {
						for(int zo = -range; zo <= range; zo++) {
							positions.add(new StaticBlockTarget(new BlockPos(context.getUser().getPosition()).add(xo, yo, zo)));
						}
					}
				}

				//System.out.println("Node 1: " + positions + " " + context.getEntity());

				OUT_POSITIONS.set(io, positions);
			} else {
				BlockPos center = IN_POSITION_2.get(io).block();
				
				List<IBlockTarget> positions = new ArrayList<>();

				int range = 6;
				for(int xo = -range; xo <= range; xo++) {
					for(int yo = -range; yo <= range; yo++) {
						for(int zo = -range; zo <= range; zo++) {
							positions.add(new StaticBlockTarget(center.add(xo, yo, zo)));
						}
					}
				}

				//System.out.println("Node 1: " + positions + " " + context.getEntity());

				OUT_POSITIONS_2.set(io, positions);
			}
			
			return null;
		}
	}

	private RuneMarkArea(Blueprint blueprint, int index, INodeComposition<RuneExecutionContext> composition, RuneConfiguration configuration) {
		super(blueprint, index, composition, configuration);
	}
}
