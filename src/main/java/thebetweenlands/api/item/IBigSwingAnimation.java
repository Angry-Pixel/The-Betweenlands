package thebetweenlands.api.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public interface IBigSwingAnimation {
	public boolean shouldUseBigSwingAnimation(ItemStack stack);
	
	public float getSwingSpeedMultiplier(EntityLivingBase entity, ItemStack stack);
}
