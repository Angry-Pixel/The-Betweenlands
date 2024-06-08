package thebetweenlands.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BlockPoisonIvy extends BetweenlandsVineBlock {

	public BlockPoisonIvy(Properties properties) {
		super(properties);
	}

	@Override
	public void entityInside(BlockState p_60495_, Level p_60496_, BlockPos p_60497_, Entity p_60498_) {
		// Only hurt players and mobs ignoring items and others
		if (p_60498_ instanceof LivingEntity) {
			LivingEntity entity = (LivingEntity) p_60498_;
			entity.addEffect(new MobEffectInstance(MobEffects.POISON, 50));
		}
	}
}
