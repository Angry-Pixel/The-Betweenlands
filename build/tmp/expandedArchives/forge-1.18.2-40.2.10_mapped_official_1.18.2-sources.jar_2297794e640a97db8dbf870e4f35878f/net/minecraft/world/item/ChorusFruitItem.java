package net.minecraft.world.item;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ChorusFruitItem extends Item {
   public ChorusFruitItem(Item.Properties p_40710_) {
      super(p_40710_);
   }

   public ItemStack finishUsingItem(ItemStack p_40712_, Level p_40713_, LivingEntity p_40714_) {
      ItemStack itemstack = super.finishUsingItem(p_40712_, p_40713_, p_40714_);
      if (!p_40713_.isClientSide) {
         double d0 = p_40714_.getX();
         double d1 = p_40714_.getY();
         double d2 = p_40714_.getZ();

         for(int i = 0; i < 16; ++i) {
            double d3 = p_40714_.getX() + (p_40714_.getRandom().nextDouble() - 0.5D) * 16.0D;
            double d4 = Mth.clamp(p_40714_.getY() + (double)(p_40714_.getRandom().nextInt(16) - 8), (double)p_40713_.getMinBuildHeight(), (double)(p_40713_.getMinBuildHeight() + ((ServerLevel)p_40713_).getLogicalHeight() - 1));
            double d5 = p_40714_.getZ() + (p_40714_.getRandom().nextDouble() - 0.5D) * 16.0D;
            if (p_40714_.isPassenger()) {
               p_40714_.stopRiding();
            }

            net.minecraftforge.event.entity.EntityTeleportEvent.ChorusFruit event = net.minecraftforge.event.ForgeEventFactory.onChorusFruitTeleport(p_40714_, d3, d4, d5);
            if (event.isCanceled()) return itemstack;
            if (p_40714_.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true)) {
               SoundEvent soundevent = p_40714_ instanceof Fox ? SoundEvents.FOX_TELEPORT : SoundEvents.CHORUS_FRUIT_TELEPORT;
               p_40713_.playSound((Player)null, d0, d1, d2, soundevent, SoundSource.PLAYERS, 1.0F, 1.0F);
               p_40714_.playSound(soundevent, 1.0F, 1.0F);
               break;
            }
         }

         if (p_40714_ instanceof Player) {
            ((Player)p_40714_).getCooldowns().addCooldown(this, 20);
         }
      }

      return itemstack;
   }
}
