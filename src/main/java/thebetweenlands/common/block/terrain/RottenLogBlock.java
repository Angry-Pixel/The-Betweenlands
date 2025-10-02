package thebetweenlands.common.block.terrain;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.entity.monster.Termite;
import thebetweenlands.common.registries.EntityRegistry;

public class RottenLogBlock extends RotatedPillarBlock {
	public RottenLogBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected void spawnAfterBreak(BlockState state, ServerLevel level, BlockPos pos, ItemStack stack, boolean dropExperience) {
		super.spawnAfterBreak(state, level, pos, stack, dropExperience);

		if (level.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS) && level.getDifficulty() != Difficulty.PEACEFUL) {
			if (level.getRandom().nextInt(6) == 0) {
				Termite entity = new Termite(EntityRegistry.TERMITE.get(), level);
				entity.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0.0F, 0.0F);
				if (!entity.checkSpawnObstruction(level)) {
					entity.setSmall(true);
				}
				level.addFreshEntity(entity);
			}
		}
	}
}
