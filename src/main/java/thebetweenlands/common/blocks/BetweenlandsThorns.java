package thebetweenlands.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BetweenlandsThorns extends BetweenlandsVineBlock {

	public BetweenlandsThorns(Properties properties) {
		super(properties);
	}


	@Override
	public void entityInside(BlockState p_60495_, Level level, BlockPos p_60497_, Entity p_60498_) {
		// Only hurt players and mobs ignoring items and others
		if (p_60498_ instanceof LivingEntity) {
			p_60498_.hurt(level.damageSources().sweetBerryBush(), 1);
		}
	}
}
