package thebetweenlands.common.herblore.rune;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.api.rune.INodeComposition;
import thebetweenlands.api.rune.INodeConfiguration;
import thebetweenlands.api.rune.impl.AbstractRune;
import thebetweenlands.api.rune.impl.RuneChainComposition.RuneExecutionContext;
import thebetweenlands.api.rune.impl.RuneConfiguration;
import thebetweenlands.api.rune.impl.RuneConfiguration.OutputPort;
import thebetweenlands.api.rune.impl.RuneEffectModifier;
import thebetweenlands.api.rune.impl.RuneStats;
import thebetweenlands.api.rune.impl.RuneTokenDescriptors;
import thebetweenlands.common.block.misc.BlockBurntScrivenerMark;

public final class TokenRuneFormation extends AbstractRune<TokenRuneFormation> {

	public static final class Blueprint extends AbstractRune.Blueprint<TokenRuneFormation> {
		private final List<BlockPos> formation;

		public Blueprint(RuneStats stats, List<BlockPos> formation) {
			super(stats);
			this.formation = formation;
		}

		public static final RuneConfiguration CONFIGURATION;

		private static final OutputPort<Collection<BlockPos>> OUT_POSITIONS;

		static {
			RuneConfiguration.Builder builder = RuneConfiguration.builder();

			OUT_POSITIONS = builder.multiOut(RuneTokenDescriptors.BLOCK, BlockPos.class);

			CONFIGURATION = builder.build();
		}

		@Override
		public List<RuneConfiguration> getConfigurations(IConfigurationLinkAccess linkAccess, boolean provisional) {
			return ImmutableList.of(CONFIGURATION);
		}

		@Override
		public TokenRuneFormation create(int index, INodeComposition<RuneExecutionContext> composition, INodeConfiguration configuration) {
			return new TokenRuneFormation(this, index, composition, (RuneConfiguration) configuration);
		}

		@Override
		protected RuneEffectModifier.Subject activate(TokenRuneFormation state, RuneExecutionContext context, INodeIO io) {

			World world = context.getUser().getWorld();

			List<BlockPos> positions = new ArrayList<>(this.formation.size());

			IBlockState blockState;

			for(BlockPos block : this.formation) {
				if(world.isBlockLoaded(block, false)) {
					blockState = world.getBlockState(block);

					if(blockState.getBlock() instanceof BlockBurntScrivenerMark && blockState.getValue(BlockBurntScrivenerMark.LINKED)) {
						positions.add(block);
					}
				}
			}

			OUT_POSITIONS.set(io, positions);

			io.schedule(scheduler -> {
				scheduler.sleep(positions.size() * 0.1f);
				scheduler.terminate();
			});
			
			return null;
		}
	}

	private TokenRuneFormation(Blueprint blueprint, int index, INodeComposition<RuneExecutionContext> composition, RuneConfiguration configuration) {
		super(blueprint, index, composition, configuration);
	}
}
