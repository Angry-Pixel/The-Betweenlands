package net.minecraft.world.item.enchantment;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.ItemStack;

public abstract class Enchantment extends net.minecraftforge.registries.ForgeRegistryEntry<Enchantment> implements net.minecraftforge.common.extensions.IForgeEnchantment {
   private final EquipmentSlot[] slots;
   private final Enchantment.Rarity rarity;
   public final EnchantmentCategory category;
   @Nullable
   protected String descriptionId;

   @Nullable
   public static Enchantment byId(int p_44698_) {
      return Registry.ENCHANTMENT.byId(p_44698_);
   }

   protected Enchantment(Enchantment.Rarity p_44676_, EnchantmentCategory p_44677_, EquipmentSlot[] p_44678_) {
      this.rarity = p_44676_;
      this.category = p_44677_;
      this.slots = p_44678_;
   }

   public Map<EquipmentSlot, ItemStack> getSlotItems(LivingEntity p_44685_) {
      Map<EquipmentSlot, ItemStack> map = Maps.newEnumMap(EquipmentSlot.class);

      for(EquipmentSlot equipmentslot : this.slots) {
         ItemStack itemstack = p_44685_.getItemBySlot(equipmentslot);
         if (!itemstack.isEmpty()) {
            map.put(equipmentslot, itemstack);
         }
      }

      return map;
   }

   public Enchantment.Rarity getRarity() {
      return this.rarity;
   }

   public int getMinLevel() {
      return 1;
   }

   public int getMaxLevel() {
      return 1;
   }

   public int getMinCost(int p_44679_) {
      return 1 + p_44679_ * 10;
   }

   public int getMaxCost(int p_44691_) {
      return this.getMinCost(p_44691_) + 5;
   }

   public int getDamageProtection(int p_44680_, DamageSource p_44681_) {
      return 0;
   }

   @Deprecated // Forge: Use ItemStack aware version in IForgeEnchantment
   public float getDamageBonus(int p_44682_, MobType p_44683_) {
      return 0.0F;
   }

   public final boolean isCompatibleWith(Enchantment p_44696_) {
      return this.checkCompatibility(p_44696_) && p_44696_.checkCompatibility(this);
   }

   protected boolean checkCompatibility(Enchantment p_44690_) {
      return this != p_44690_;
   }

   protected String getOrCreateDescriptionId() {
      if (this.descriptionId == null) {
         this.descriptionId = Util.makeDescriptionId("enchantment", Registry.ENCHANTMENT.getKey(this));
      }

      return this.descriptionId;
   }

   public String getDescriptionId() {
      return this.getOrCreateDescriptionId();
   }

   public Component getFullname(int p_44701_) {
      MutableComponent mutablecomponent = new TranslatableComponent(this.getDescriptionId());
      if (this.isCurse()) {
         mutablecomponent.withStyle(ChatFormatting.RED);
      } else {
         mutablecomponent.withStyle(ChatFormatting.GRAY);
      }

      if (p_44701_ != 1 || this.getMaxLevel() != 1) {
         mutablecomponent.append(" ").append(new TranslatableComponent("enchantment.level." + p_44701_));
      }

      return mutablecomponent;
   }

   public boolean canEnchant(ItemStack p_44689_) {
      return canApplyAtEnchantingTable(p_44689_);
   }

   public void doPostAttack(LivingEntity p_44686_, Entity p_44687_, int p_44688_) {
   }

   public void doPostHurt(LivingEntity p_44692_, Entity p_44693_, int p_44694_) {
   }

   public boolean isTreasureOnly() {
      return false;
   }

   public boolean isCurse() {
      return false;
   }

   public boolean isTradeable() {
      return true;
   }

   public boolean isDiscoverable() {
      return true;
   }

   /**
    * This applies specifically to applying at the enchanting table. The other method {@link #canEnchant(ItemStack)}
    * applies for <i>all possible</i> enchantments.
    * @param stack
    * @return
    */
   public boolean canApplyAtEnchantingTable(ItemStack stack) {
      return stack.canApplyAtEnchantingTable(this);
   }

   /**
    * Is this enchantment allowed to be enchanted on books via Enchantment Table
    * @return false to disable the vanilla feature
    */
   public boolean isAllowedOnBooks() {
      return true;
   }

   public static enum Rarity {
      COMMON(10),
      UNCOMMON(5),
      RARE(2),
      VERY_RARE(1);

      private final int weight;

      private Rarity(int p_44715_) {
         this.weight = p_44715_;
      }

      public int getWeight() {
         return this.weight;
      }
   }
}
