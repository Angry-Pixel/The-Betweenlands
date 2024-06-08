package net.minecraft.world.item;

import java.util.stream.Stream;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ItemUtils {
   public static InteractionResultHolder<ItemStack> startUsingInstantly(Level p_150960_, Player p_150961_, InteractionHand p_150962_) {
      p_150961_.startUsingItem(p_150962_);
      return InteractionResultHolder.consume(p_150961_.getItemInHand(p_150962_));
   }

   public static ItemStack createFilledResult(ItemStack p_41818_, Player p_41819_, ItemStack p_41820_, boolean p_41821_) {
      boolean flag = p_41819_.getAbilities().instabuild;
      if (p_41821_ && flag) {
         if (!p_41819_.getInventory().contains(p_41820_)) {
            p_41819_.getInventory().add(p_41820_);
         }

         return p_41818_;
      } else {
         if (!flag) {
            p_41818_.shrink(1);
         }

         if (p_41818_.isEmpty()) {
            return p_41820_;
         } else {
            if (!p_41819_.getInventory().add(p_41820_)) {
               p_41819_.drop(p_41820_, false);
            }

            return p_41818_;
         }
      }
   }

   public static ItemStack createFilledResult(ItemStack p_41814_, Player p_41815_, ItemStack p_41816_) {
      return createFilledResult(p_41814_, p_41815_, p_41816_, true);
   }

   public static void onContainerDestroyed(ItemEntity p_150953_, Stream<ItemStack> p_150954_) {
      Level level = p_150953_.level;
      if (!level.isClientSide) {
         p_150954_.forEach((p_150958_) -> {
            level.addFreshEntity(new ItemEntity(level, p_150953_.getX(), p_150953_.getY(), p_150953_.getZ(), p_150958_));
         });
      }
   }
}