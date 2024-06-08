package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.Vanishable;
import net.minecraft.world.item.Wearable;
import net.minecraft.world.level.block.Block;

public enum EnchantmentCategory implements net.minecraftforge.common.IExtensibleEnum {
   ARMOR {
      public boolean canEnchant(Item p_44751_) {
         return p_44751_ instanceof ArmorItem;
      }
   },
   ARMOR_FEET {
      public boolean canEnchant(Item p_44806_) {
         return p_44806_ instanceof ArmorItem && ((ArmorItem)p_44806_).getSlot() == EquipmentSlot.FEET;
      }
   },
   ARMOR_LEGS {
      public boolean canEnchant(Item p_44811_) {
         return p_44811_ instanceof ArmorItem && ((ArmorItem)p_44811_).getSlot() == EquipmentSlot.LEGS;
      }
   },
   ARMOR_CHEST {
      public boolean canEnchant(Item p_44816_) {
         return p_44816_ instanceof ArmorItem && ((ArmorItem)p_44816_).getSlot() == EquipmentSlot.CHEST;
      }
   },
   ARMOR_HEAD {
      public boolean canEnchant(Item p_44756_) {
         return p_44756_ instanceof ArmorItem && ((ArmorItem)p_44756_).getSlot() == EquipmentSlot.HEAD;
      }
   },
   WEAPON {
      public boolean canEnchant(Item p_44761_) {
         return p_44761_ instanceof SwordItem;
      }
   },
   DIGGER {
      public boolean canEnchant(Item p_44766_) {
         return p_44766_ instanceof DiggerItem;
      }
   },
   FISHING_ROD {
      public boolean canEnchant(Item p_44771_) {
         return p_44771_ instanceof FishingRodItem;
      }
   },
   TRIDENT {
      public boolean canEnchant(Item p_44776_) {
         return p_44776_ instanceof TridentItem;
      }
   },
   BREAKABLE {
      public boolean canEnchant(Item p_44781_) {
         return p_44781_.canBeDepleted();
      }
   },
   BOW {
      public boolean canEnchant(Item p_44786_) {
         return p_44786_ instanceof BowItem;
      }
   },
   WEARABLE {
      public boolean canEnchant(Item p_44791_) {
         return p_44791_ instanceof Wearable || Block.byItem(p_44791_) instanceof Wearable;
      }
   },
   CROSSBOW {
      public boolean canEnchant(Item p_44796_) {
         return p_44796_ instanceof CrossbowItem;
      }
   },
   VANISHABLE {
      public boolean canEnchant(Item p_44801_) {
         return p_44801_ instanceof Vanishable || Block.byItem(p_44801_) instanceof Vanishable || BREAKABLE.canEnchant(p_44801_);
      }
   };

   private java.util.function.Predicate<Item> delegate;

   private EnchantmentCategory() {}

   private EnchantmentCategory(java.util.function.Predicate<Item> delegate) {
      this.delegate = delegate;
   }

   public static EnchantmentCategory create(String name, java.util.function.Predicate<Item> delegate) {
      throw new IllegalStateException("Enum not extended");
   }

   public boolean canEnchant(Item p_44743_) {
      return this.delegate == null ? false : this.delegate.test(p_44743_);
   }
}
