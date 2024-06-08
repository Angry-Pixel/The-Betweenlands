package net.minecraft.client.renderer.item;

import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/** @deprecated */
@Deprecated
@OnlyIn(Dist.CLIENT)
public interface ItemPropertyFunction {
   float call(ItemStack p_174676_, @Nullable ClientLevel p_174677_, @Nullable LivingEntity p_174678_, int p_174679_);
}