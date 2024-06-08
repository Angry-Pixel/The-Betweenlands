package net.minecraft.world.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;

public class TippedArrowItem extends ArrowItem {
   public TippedArrowItem(Item.Properties p_43354_) {
      super(p_43354_);
   }

   public ItemStack getDefaultInstance() {
      return PotionUtils.setPotion(super.getDefaultInstance(), Potions.POISON);
   }

   public void fillItemCategory(CreativeModeTab p_43356_, NonNullList<ItemStack> p_43357_) {
      if (this.allowdedIn(p_43356_)) {
         for(Potion potion : Registry.POTION) {
            if (!potion.getEffects().isEmpty()) {
               p_43357_.add(PotionUtils.setPotion(new ItemStack(this), potion));
            }
         }
      }

   }

   public void appendHoverText(ItemStack p_43359_, @Nullable Level p_43360_, List<Component> p_43361_, TooltipFlag p_43362_) {
      PotionUtils.addPotionTooltip(p_43359_, p_43361_, 0.125F);
   }

   public String getDescriptionId(ItemStack p_43364_) {
      return PotionUtils.getPotion(p_43364_).getName(this.getDescriptionId() + ".effect.");
   }
}