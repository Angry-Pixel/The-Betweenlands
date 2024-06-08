package net.minecraft.world.item.enchantment;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;

public class DamageEnchantment extends Enchantment {
   public static final int ALL = 0;
   public static final int UNDEAD = 1;
   public static final int ARTHROPODS = 2;
   private static final String[] NAMES = new String[]{"all", "undead", "arthropods"};
   private static final int[] MIN_COST = new int[]{1, 5, 5};
   private static final int[] LEVEL_COST = new int[]{11, 8, 8};
   private static final int[] LEVEL_COST_SPAN = new int[]{20, 20, 20};
   public final int type;

   public DamageEnchantment(Enchantment.Rarity p_44628_, int p_44629_, EquipmentSlot... p_44630_) {
      super(p_44628_, EnchantmentCategory.WEAPON, p_44630_);
      this.type = p_44629_;
   }

   public int getMinCost(int p_44633_) {
      return MIN_COST[this.type] + (p_44633_ - 1) * LEVEL_COST[this.type];
   }

   public int getMaxCost(int p_44646_) {
      return this.getMinCost(p_44646_) + LEVEL_COST_SPAN[this.type];
   }

   public int getMaxLevel() {
      return 5;
   }

   public float getDamageBonus(int p_44635_, MobType p_44636_) {
      if (this.type == 0) {
         return 1.0F + (float)Math.max(0, p_44635_ - 1) * 0.5F;
      } else if (this.type == 1 && p_44636_ == MobType.UNDEAD) {
         return (float)p_44635_ * 2.5F;
      } else {
         return this.type == 2 && p_44636_ == MobType.ARTHROPOD ? (float)p_44635_ * 2.5F : 0.0F;
      }
   }

   public boolean checkCompatibility(Enchantment p_44644_) {
      return !(p_44644_ instanceof DamageEnchantment);
   }

   public boolean canEnchant(ItemStack p_44642_) {
      return p_44642_.getItem() instanceof AxeItem ? true : super.canEnchant(p_44642_);
   }

   public void doPostAttack(LivingEntity p_44638_, Entity p_44639_, int p_44640_) {
      if (p_44639_ instanceof LivingEntity) {
         LivingEntity livingentity = (LivingEntity)p_44639_;
         if (this.type == 2 && p_44640_ > 0 && livingentity.getMobType() == MobType.ARTHROPOD) {
            int i = 20 + p_44638_.getRandom().nextInt(10 * p_44640_);
            livingentity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, i, 3));
         }
      }

   }
}