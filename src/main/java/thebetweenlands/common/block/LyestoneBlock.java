package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.registries.DataComponentRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class LyestoneBlock extends Block {

	public LyestoneBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
		if (!level.isClientSide() && level.getGameTime() % 40 == 0) {
			level.playSound(null, pos, SoundRegistry.LYESTONE_FIZZ.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
		}

		if (entity instanceof Player player) {
			if (!player.getItemBySlot(EquipmentSlot.FEET).isEmpty()) {
				player.getItemBySlot(EquipmentSlot.FEET).set(DataComponentRegistry.CORROSIVE, true);
			} else {
				this.causeDamage(level, pos, entity);
			}
		} else {
			this.causeDamage(level, pos, entity);
		}
	}

	public void causeDamage(Level level, BlockPos pos, Entity entity) {
		if (!entity.fireImmune() && entity instanceof LivingEntity)
			entity.hurt(level.damageSources().hotFloor(), 1.0F);
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		double posX = pos.getX();
		double posY = pos.getY();
		double posZ = pos.getZ();

		if (random.nextInt(10) == 0) {
			boolean stateBelow = level.isEmptyBlock(pos.below());
			if (stateBelow) {
				double x = posX + (double) random.nextFloat();
				double y = posY - 0.05D;
				double z = posZ + (double) random.nextFloat();
//				BLParticles.CAVE_WATER_DRIP.spawn(level, x, y, z).setRBGColorF(0.4118F, 0.2745F, 0.1568F);
			}
		}

//		BLParticles.FANCY_BUBBLE.spawn(level, posX + 0.5D, posY + 0.5D, posZ + 0.5D);
	}
}
