package thebetweenlands.common.block.terrain;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import thebetweenlands.common.registries.BlockRegistry;

public class SpreadingRottenLogBlock extends SpreadingDeathBlock {
	public SpreadingRottenLogBlock(Properties properties) {
		super(properties);
	}

	@Override
	public boolean canSpreadInto(Level level, BlockPos pos, BlockState state, BlockPos offsetPos, BlockState offsetState) {
		return offsetState.is(BlockRegistry.SPIRIT_TREE_LOG);
	}

	@Override
	public void spreadInto(Level level, BlockPos pos, BlockState state, BlockPos offsetPos, BlockState offsetState) {
		level.setBlockAndUpdate(offsetPos, this.defaultBlockState());
	}

	@Override
	protected boolean shouldSpread(Level level, BlockPos pos, BlockState state) {
		return level.getRandom().nextInt(4) == 0;
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
		return new ItemStack(BlockRegistry.ROTTEN_BARK);
	}

	@Override
	protected void spawnAfterBreak(BlockState state, ServerLevel level, BlockPos pos, ItemStack stack, boolean dropExperience) {
		super.spawnAfterBreak(state, level, pos, stack, dropExperience);

		if (level.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS) && level.getDifficulty() != Difficulty.PEACEFUL) {
			if (level.getRandom().nextInt(6) == 0) {
				//TODO
//				Termite entity = new Termite(level);
//				entity.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0.0F, 0.0F);
//				if(!entity.isNotColliding()) {
//					entity.setSmall(true);
//				}
//				level.addFreshEntity(entity);
			}
		}
	}
}
