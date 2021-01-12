package thebetweenlands.common.herblore.rune;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.util.math.BlockPos;
import thebetweenlands.api.runechain.base.IConfigurationLinkAccess;
import thebetweenlands.api.runechain.base.INodeComposition;
import thebetweenlands.api.runechain.base.INodeConfiguration;
import thebetweenlands.api.runechain.base.INodeIO;
import thebetweenlands.api.runechain.chain.IRuneExecutionContext;
import thebetweenlands.api.runechain.io.IGetter;
import thebetweenlands.api.runechain.io.ISetter;
import thebetweenlands.api.runechain.io.InputSerializers;
import thebetweenlands.api.runechain.io.types.IBlockTarget;
import thebetweenlands.api.runechain.io.types.RuneTokenDescriptors;
import thebetweenlands.api.runechain.io.types.StaticBlockTarget;
import thebetweenlands.api.runechain.modifier.Subject;
import thebetweenlands.api.runechain.rune.AbstractRune;
import thebetweenlands.api.runechain.rune.RuneConfiguration;
import thebetweenlands.api.runechain.rune.RuneStats;
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
		public RuneMarkArea create(int index, INodeComposition<IRuneExecutionContext> composition, INodeConfiguration configuration) {
			return new RuneMarkArea(this, index, composition, (RuneConfiguration) configuration);
		}

		@Override
		protected Subject activate(RuneMarkArea state, IRuneExecutionContext context, INodeIO io) {
			
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

	private RuneMarkArea(Blueprint blueprint, int index, INodeComposition<IRuneExecutionContext> composition, RuneConfiguration configuration) {
		super(blueprint, index, composition, configuration);
	}
}
