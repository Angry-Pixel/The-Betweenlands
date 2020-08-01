package thebetweenlands.common.herblore.rune;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.util.math.BlockPos;
import thebetweenlands.api.rune.INodeComposition;
import thebetweenlands.api.rune.INodeConfiguration;
import thebetweenlands.api.rune.impl.AbstractRune;
import thebetweenlands.api.rune.impl.InputSerializers;
import thebetweenlands.api.rune.impl.RuneChainComposition.RuneExecutionContext;
import thebetweenlands.api.rune.impl.RuneConfiguration;
import thebetweenlands.api.rune.impl.RuneConfiguration.InputPort;
import thebetweenlands.api.rune.impl.RuneConfiguration.OutputPort;
import thebetweenlands.api.rune.impl.RuneEffectModifier;
import thebetweenlands.api.rune.impl.RuneStats;
import thebetweenlands.api.rune.impl.RuneTokenDescriptors;

public final class RuneMarkPattern extends AbstractRune<RuneMarkPattern> {

	public static final class Blueprint extends AbstractRune.Blueprint<RuneMarkPattern> {
		private final List<BlockPos> pattern;

		public Blueprint(RuneStats stats, List<BlockPos> pattern) {
			super(stats);
			this.pattern = pattern;
		}

		public static final RuneConfiguration CONFIGURATION_1;
		public static final RuneConfiguration CONFIGURATION_2;

		private static final InputPort<BlockPos> IN_POSITION_2;
		private static final OutputPort<Collection<BlockPos>> OUT_POSITIONS_2;

		private static final OutputPort<Collection<BlockPos>> OUT_POSITIONS;

		static {
			RuneConfiguration.Builder builder = RuneConfiguration.builder();

			OUT_POSITIONS = builder.multiOut(RuneTokenDescriptors.BLOCK, BlockPos.class);

			CONFIGURATION_1 = builder.build();

			IN_POSITION_2 = builder.in(RuneTokenDescriptors.BLOCK, InputSerializers.BLOCK, BlockPos.class);
			OUT_POSITIONS_2 = builder.multiOut(RuneTokenDescriptors.BLOCK, BlockPos.class);

			CONFIGURATION_2 = builder.build();
		}

		@Override
		public List<RuneConfiguration> getConfigurations(IConfigurationLinkAccess linkAccess) {
			return ImmutableList.of(CONFIGURATION_1, CONFIGURATION_2);
		}

		@Override
		public RuneMarkPattern create(int index, INodeComposition<RuneExecutionContext> composition, INodeConfiguration configuration) {
			return new RuneMarkPattern(this, index, composition, (RuneConfiguration) configuration);
		}

		@Override
		protected RuneEffectModifier.Subject activate(RuneMarkPattern state, RuneExecutionContext context, INodeIO io) {

			BlockPos center;
			if(state.getConfiguration() == CONFIGURATION_1) {
				center = new BlockPos(context.getUser().getPosition());
			} else {
				center = IN_POSITION_2.get(io);
			}

			List<BlockPos> positions = new ArrayList<>(this.pattern.size());
			for(BlockPos block : this.pattern) {
				positions.add(block.add(center));
			}

			if(state.getConfiguration() == CONFIGURATION_1) {
				OUT_POSITIONS.set(io, positions);
			} else {
				OUT_POSITIONS_2.set(io, positions);
			}

			return null;
		}
	}

	private RuneMarkPattern(Blueprint blueprint, int index, INodeComposition<RuneExecutionContext> composition, RuneConfiguration configuration) {
		super(blueprint, index, composition, configuration);
	}
}
