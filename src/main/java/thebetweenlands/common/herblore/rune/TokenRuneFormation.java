package thebetweenlands.common.herblore.rune;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.api.runechain.base.IConfigurationLinkAccess;
import thebetweenlands.api.runechain.base.INodeComposition;
import thebetweenlands.api.runechain.base.INodeConfiguration;
import thebetweenlands.api.runechain.base.INodeIO;
import thebetweenlands.api.runechain.chain.IRuneExecutionContext;
import thebetweenlands.api.runechain.io.ISetter;
import thebetweenlands.api.runechain.io.types.IBlockTarget;
import thebetweenlands.api.runechain.io.types.RuneTokenDescriptors;
import thebetweenlands.api.runechain.io.types.StaticBlockTarget;
import thebetweenlands.api.runechain.modifier.Subject;
import thebetweenlands.api.runechain.rune.AbstractRune;
import thebetweenlands.api.runechain.rune.RuneConfiguration;
import thebetweenlands.api.runechain.rune.RuneStats;
import thebetweenlands.common.block.misc.BlockBurntScrivenerMark;

public final class TokenRuneFormation extends AbstractRune<TokenRuneFormation> {

	public static final class Blueprint extends AbstractRune.Blueprint<TokenRuneFormation> {
		private final List<BlockPos> formation;

		public Blueprint(RuneStats stats, List<BlockPos> formation) {
			super(stats);
			this.formation = formation;
		}

		public static final RuneConfiguration CONFIGURATION;

		private static final ISetter<Collection<IBlockTarget>> OUT_POSITIONS;

		static {
			RuneConfiguration.Builder builder = RuneConfiguration.create();

			OUT_POSITIONS = builder.out(RuneTokenDescriptors.BLOCK).type(IBlockTarget.class).collection().setter();

			CONFIGURATION = builder.build();
		}

		@Override
		public List<RuneConfiguration> getConfigurations(IConfigurationLinkAccess linkAccess, boolean provisional) {
			return ImmutableList.of(CONFIGURATION);
		}

		@Override
		public TokenRuneFormation create(int index, INodeComposition<IRuneExecutionContext> composition, INodeConfiguration configuration) {
			return new TokenRuneFormation(this, index, composition, (RuneConfiguration) configuration);
		}

		@Override
		protected Subject activate(TokenRuneFormation state, IRuneExecutionContext context, INodeIO io) {

			World world = context.getUser().getWorld();

			List<IBlockTarget> positions = new ArrayList<>(this.formation.size());

			IBlockState blockState;

			for(BlockPos block : this.formation) {
				if(world.isBlockLoaded(block, false)) {
					blockState = world.getBlockState(block);

					if(blockState.getBlock() instanceof BlockBurntScrivenerMark && blockState.getValue(BlockBurntScrivenerMark.LINKED)) {
						positions.add(new StaticBlockTarget(block));
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

	private TokenRuneFormation(Blueprint blueprint, int index, INodeComposition<IRuneExecutionContext> composition, RuneConfiguration configuration) {
		super(blueprint, index, composition, configuration);
	}
}
