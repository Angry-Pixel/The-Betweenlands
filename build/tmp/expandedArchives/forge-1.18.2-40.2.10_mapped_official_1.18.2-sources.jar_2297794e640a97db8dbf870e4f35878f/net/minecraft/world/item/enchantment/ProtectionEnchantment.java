package net.minecraft.world.item.enchantment;

import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

public class ProtectionEnchantment extends Enchantment {
   public final ProtectionEnchantment.Type type;

   public ProtectionEnchantment(Enchantment.Rarity p_45126_, ProtectionEnchantment.Type p_45127_, EquipmentSlot... p_45128_) {
      super(p_45126_, p_45127_ == ProtectionEnchantment.Type.FALL ? EnchantmentCategory.ARMOR_FEET : EnchantmentCategory.ARMOR, p_45128_);
      this.type = p_45127_;
   }

   public int getMinCost(int p_45131_) {
      return this.type.getMinCost() + (p_45131_ - 1) * this.type.getLevelCost();
   }

   public int getMaxCost(int p_45144_) {
      return this.getMinCost(p_45144_) + this.type.getLevelCost();
   }

   public int getMaxLevel() {
      return 4;
   }

   public int getDamageProtection(int p_45133_, DamageSource p_45134_) {
      if (p_45134_.isBypassInvul()) {
         return 0;
      } else if (this.type == ProtectionEnchantment.Type.ALL) {
         return p_45133_;
      } else if (this.type == ProtectionEnchantment.Type.FIRE && p_45134_.isFire()) {
         return p_45133_ * 2;
      } else if (this.type == ProtectionEnchantment.Type.FALL && p_45134_.isFall()) {
         return p_45133_ * 3;
      } else if (this.type == ProtectionEnchantment.Type.EXPLOSION && p_45134_.isExplosion()) {
         return p_45133_ * 2;
      } else {
         return this.type == ProtectionEnchantment.Type.PROJECTILE && p_45134_.isProjectile() ? p_45133_ * 2 : 0;
      }
   }

   public boolean checkCompatibility(Enchantment p_45142_) {
      if (p_45142_ instanceof ProtectionEnchantment) {
         ProtectionEnchantment protectionenchantment = (ProtectionEnchantment)p_45142_;
         if (this.type == protectionenchantment.type) {
            return false;
         } else {
            return this.type == ProtectionEnchantment.Type.FALL || protectionenchantment.type == ProtectionEnchantment.Type.FALL;
         }
      } else {
         return super.checkCompatibility(p_45142_);
      }
   }

   public static int getFireAfterDampener(LivingEntity p_45139_, int p_45140_) {
      int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.FIRE_PROTECTION, p_45139_);
      if (i > 0) {
         p_45140_ -= Mth.floor((float)p_45140_ * (float)i * 0.15F);
      }

      return p_45140_;
   }

   public static double getExplosionKnockbackAfterDampener(LivingEntity p_45136_, double p_45137_) {
      int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.BLAST_PROTECTION, p_45136_);
      if (i > 0) {
         p_45137_ -= (double)Mth.floor(p_45137_ * (double)((float)i * 0.15F));
      }

      return p_45137_;
   }

   public static enum Type {
      ALL(1, 11),
      FIRE(10, 8),
      FALL(5, 6),
      EXPLOSION(5, 8),
      PROJECTILE(3, 6);

      private final int minCost;
      private final int levelCost;

      private Type(int p_151299_, int p_151300_) {
         this.minCost = p_151299_;
         this.levelCost = p_151300_;
      }

      public int getMinCost() {
         return this.minCost;
      }

      public int getLevelCost() {
         return this.levelCost;
      }
   }
}