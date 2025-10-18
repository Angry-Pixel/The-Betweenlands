package thebetweenlands.common.item.misc;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import thebetweenlands.common.registries.SoundRegistry;

public class AnimatedSmallSpiritTreeMaskItem extends Item {
	public AnimatedSmallSpiritTreeMaskItem(Properties properties) {
		super(properties);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
		super.inventoryTick(stack, level, entity, slotId, isSelected);

		if (entity instanceof LivingEntity) {
			if (!level.isClientSide() && isSelected && level.getRandom().nextInt(60) == 0) {
				level.playSound(null, entity.blockPosition().above(), SoundRegistry.SPIRIT_TREE_FACE_SMALL_LIVING.get(), SoundSource.PLAYERS, 0.35F, 1.4F);
			}
		}
	}
}
