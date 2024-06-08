package net.minecraft.client.renderer.item;

import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ClampedItemPropertyFunction extends ItemPropertyFunction {
   /** @deprecated */
   @Deprecated
   default float call(ItemStack p_174560_, @Nullable ClientLevel p_174561_, @Nullable LivingEntity p_174562_, int p_174563_) {
      return Mth.clamp(this.unclampedCall(p_174560_, p_174561_, p_174562_, p_174563_), 0.0F, 1.0F);
   }

   float unclampedCall(ItemStack p_174564_, @Nullable ClientLevel p_174565_, @Nullable LivingEntity p_174566_, int p_174567_);
}