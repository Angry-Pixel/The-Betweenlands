package net.minecraft.world.item.enchantment;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableInt;

public class EnchantmentHelper {
   private static final String TAG_ENCH_ID = "id";
   private static final String TAG_ENCH_LEVEL = "lvl";

   public static CompoundTag storeEnchantment(@Nullable ResourceLocation p_182444_, int p_182445_) {
      CompoundTag compoundtag = new CompoundTag();
      compoundtag.putString("id", String.valueOf((Object)p_182444_));
      compoundtag.putShort("lvl", (short)p_182445_);
      return compoundtag;
   }

   public static void setEnchantmentLevel(CompoundTag p_182441_, int p_182442_) {
      p_182441_.putShort("lvl", (short)p_182442_);
   }

   public static int getEnchantmentLevel(CompoundTag p_182439_) {
      return Mth.clamp(p_182439_.getInt("lvl"), 0, 255);
   }

   @Nullable
   public static ResourceLocation getEnchantmentId(CompoundTag p_182447_) {
      return ResourceLocation.tryParse(p_182447_.getString("id"));
   }

   @Nullable
   public static ResourceLocation getEnchantmentId(Enchantment p_182433_) {
      return Registry.ENCHANTMENT.getKey(p_182433_);
   }

   public static int getItemEnchantmentLevel(Enchantment p_44844_, ItemStack p_44845_) {
      if (p_44845_.isEmpty()) {
         return 0;
      } else {
         ResourceLocation resourcelocation = getEnchantmentId(p_44844_);
         ListTag listtag = p_44845_.getEnchantmentTags();

         for(int i = 0; i < listtag.size(); ++i) {
            CompoundTag compoundtag = listtag.getCompound(i);
            ResourceLocation resourcelocation1 = getEnchantmentId(compoundtag);
            if (resourcelocation1 != null && resourcelocation1.equals(resourcelocation)) {
               return getEnchantmentLevel(compoundtag);
            }
         }

         return 0;
      }
   }

   public static Map<Enchantment, Integer> getEnchantments(ItemStack p_44832_) {
      ListTag listtag = p_44832_.is(Items.ENCHANTED_BOOK) ? EnchantedBookItem.getEnchantments(p_44832_) : p_44832_.getEnchantmentTags();
      return deserializeEnchantments(listtag);
   }

   public static Map<Enchantment, Integer> deserializeEnchantments(ListTag p_44883_) {
      Map<Enchantment, Integer> map = Maps.newLinkedHashMap();

      for(int i = 0; i < p_44883_.size(); ++i) {
         CompoundTag compoundtag = p_44883_.getCompound(i);
         Registry.ENCHANTMENT.getOptional(getEnchantmentId(compoundtag)).ifPresent((p_44871_) -> {
            map.put(p_44871_, getEnchantmentLevel(compoundtag));
         });
      }

      return map;
   }

   public static void setEnchantments(Map<Enchantment, Integer> p_44866_, ItemStack p_44867_) {
      ListTag listtag = new ListTag();

      for(Entry<Enchantment, Integer> entry : p_44866_.entrySet()) {
         Enchantment enchantment = entry.getKey();
         if (enchantment != null) {
            int i = entry.getValue();
            listtag.add(storeEnchantment(getEnchantmentId(enchantment), i));
            if (p_44867_.is(Items.ENCHANTED_BOOK)) {
               EnchantedBookItem.addEnchantment(p_44867_, new EnchantmentInstance(enchantment, i));
            }
         }
      }

      if (listtag.isEmpty()) {
         p_44867_.removeTagKey("Enchantments");
      } else if (!p_44867_.is(Items.ENCHANTED_BOOK)) {
         p_44867_.addTagElement("Enchantments", listtag);
      }

   }

   private static void runIterationOnItem(EnchantmentHelper.EnchantmentVisitor p_44851_, ItemStack p_44852_) {
      if (!p_44852_.isEmpty()) {
         ListTag listtag = p_44852_.getEnchantmentTags();

         for(int i = 0; i < listtag.size(); ++i) {
            CompoundTag compoundtag = listtag.getCompound(i);
            Registry.ENCHANTMENT.getOptional(getEnchantmentId(compoundtag)).ifPresent((p_182437_) -> {
               p_44851_.accept(p_182437_, getEnchantmentLevel(compoundtag));
            });
         }

      }
   }

   private static void runIterationOnInventory(EnchantmentHelper.EnchantmentVisitor p_44854_, Iterable<ItemStack> p_44855_) {
      for(ItemStack itemstack : p_44855_) {
         runIterationOnItem(p_44854_, itemstack);
      }

   }

   public static int getDamageProtection(Iterable<ItemStack> p_44857_, DamageSource p_44858_) {
      MutableInt mutableint = new MutableInt();
      runIterationOnInventory((p_44892_, p_44893_) -> {
         mutableint.add(p_44892_.getDamageProtection(p_44893_, p_44858_));
      }, p_44857_);
      return mutableint.intValue();
   }

   public static float getDamageBonus(ItemStack p_44834_, MobType p_44835_) {
      MutableFloat mutablefloat = new MutableFloat();
      runIterationOnItem((p_44887_, p_44888_) -> {
         mutablefloat.add(p_44887_.getDamageBonus(p_44888_, p_44835_, p_44834_));
      }, p_44834_);
      return mutablefloat.floatValue();
   }

   public static float getSweepingDamageRatio(LivingEntity p_44822_) {
      int i = getEnchantmentLevel(Enchantments.SWEEPING_EDGE, p_44822_);
      return i > 0 ? SweepingEdgeEnchantment.getSweepingDamageRatio(i) : 0.0F;
   }

   public static void doPostHurtEffects(LivingEntity p_44824_, Entity p_44825_) {
      EnchantmentHelper.EnchantmentVisitor enchantmenthelper$enchantmentvisitor = (p_44902_, p_44903_) -> {
         p_44902_.doPostHurt(p_44824_, p_44825_, p_44903_);
      };
      if (p_44824_ != null) {
         runIterationOnInventory(enchantmenthelper$enchantmentvisitor, p_44824_.getAllSlots());
      }

      if (p_44825_ instanceof Player) {
         runIterationOnItem(enchantmenthelper$enchantmentvisitor, p_44824_.getMainHandItem());
      }

   }

   public static void doPostDamageEffects(LivingEntity p_44897_, Entity p_44898_) {
      EnchantmentHelper.EnchantmentVisitor enchantmenthelper$enchantmentvisitor = (p_44829_, p_44830_) -> {
         p_44829_.doPostAttack(p_44897_, p_44898_, p_44830_);
      };
      if (p_44897_ != null) {
         runIterationOnInventory(enchantmenthelper$enchantmentvisitor, p_44897_.getAllSlots());
      }

      if (p_44897_ instanceof Player) {
         runIterationOnItem(enchantmenthelper$enchantmentvisitor, p_44897_.getMainHandItem());
      }

   }

   public static int getEnchantmentLevel(Enchantment p_44837_, LivingEntity p_44838_) {
      Iterable<ItemStack> iterable = p_44837_.getSlotItems(p_44838_).values();
      if (iterable == null) {
         return 0;
      } else {
         int i = 0;

         for(ItemStack itemstack : iterable) {
            int j = getItemEnchantmentLevel(p_44837_, itemstack);
            if (j > i) {
               i = j;
            }
         }

         return i;
      }
   }

   public static int getKnockbackBonus(LivingEntity p_44895_) {
      return getEnchantmentLevel(Enchantments.KNOCKBACK, p_44895_);
   }

   public static int getFireAspect(LivingEntity p_44915_) {
      return getEnchantmentLevel(Enchantments.FIRE_ASPECT, p_44915_);
   }

   public static int getRespiration(LivingEntity p_44919_) {
      return getEnchantmentLevel(Enchantments.RESPIRATION, p_44919_);
   }

   public static int getDepthStrider(LivingEntity p_44923_) {
      return getEnchantmentLevel(Enchantments.DEPTH_STRIDER, p_44923_);
   }

   public static int getBlockEfficiency(LivingEntity p_44927_) {
      return getEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY, p_44927_);
   }

   public static int getFishingLuckBonus(ItemStack p_44905_) {
      return getItemEnchantmentLevel(Enchantments.FISHING_LUCK, p_44905_);
   }

   public static int getFishingSpeedBonus(ItemStack p_44917_) {
      return getItemEnchantmentLevel(Enchantments.FISHING_SPEED, p_44917_);
   }

   public static int getMobLooting(LivingEntity p_44931_) {
      return getEnchantmentLevel(Enchantments.MOB_LOOTING, p_44931_);
   }

   public static boolean hasAquaAffinity(LivingEntity p_44935_) {
      return getEnchantmentLevel(Enchantments.AQUA_AFFINITY, p_44935_) > 0;
   }

   public static boolean hasFrostWalker(LivingEntity p_44939_) {
      return getEnchantmentLevel(Enchantments.FROST_WALKER, p_44939_) > 0;
   }

   public static boolean hasSoulSpeed(LivingEntity p_44943_) {
      return getEnchantmentLevel(Enchantments.SOUL_SPEED, p_44943_) > 0;
   }

   public static boolean hasBindingCurse(ItemStack p_44921_) {
      return getItemEnchantmentLevel(Enchantments.BINDING_CURSE, p_44921_) > 0;
   }

   public static boolean hasVanishingCurse(ItemStack p_44925_) {
      return getItemEnchantmentLevel(Enchantments.VANISHING_CURSE, p_44925_) > 0;
   }

   public static int getLoyalty(ItemStack p_44929_) {
      return getItemEnchantmentLevel(Enchantments.LOYALTY, p_44929_);
   }

   public static int getRiptide(ItemStack p_44933_) {
      return getItemEnchantmentLevel(Enchantments.RIPTIDE, p_44933_);
   }

   public static boolean hasChanneling(ItemStack p_44937_) {
      return getItemEnchantmentLevel(Enchantments.CHANNELING, p_44937_) > 0;
   }

   @Nullable
   public static Entry<EquipmentSlot, ItemStack> getRandomItemWith(Enchantment p_44907_, LivingEntity p_44908_) {
      return getRandomItemWith(p_44907_, p_44908_, (p_44941_) -> {
         return true;
      });
   }

   @Nullable
   public static Entry<EquipmentSlot, ItemStack> getRandomItemWith(Enchantment p_44840_, LivingEntity p_44841_, Predicate<ItemStack> p_44842_) {
      Map<EquipmentSlot, ItemStack> map = p_44840_.getSlotItems(p_44841_);
      if (map.isEmpty()) {
         return null;
      } else {
         List<Entry<EquipmentSlot, ItemStack>> list = Lists.newArrayList();

         for(Entry<EquipmentSlot, ItemStack> entry : map.entrySet()) {
            ItemStack itemstack = entry.getValue();
            if (!itemstack.isEmpty() && getItemEnchantmentLevel(p_44840_, itemstack) > 0 && p_44842_.test(itemstack)) {
               list.add(entry);
            }
         }

         return list.isEmpty() ? null : list.get(p_44841_.getRandom().nextInt(list.size()));
      }
   }

   public static int getEnchantmentCost(Random p_44873_, int p_44874_, int p_44875_, ItemStack p_44876_) {
      Item item = p_44876_.getItem();
      int i = p_44876_.getItemEnchantability();
      if (i <= 0) {
         return 0;
      } else {
         if (p_44875_ > 15) {
            p_44875_ = 15;
         }

         int j = p_44873_.nextInt(8) + 1 + (p_44875_ >> 1) + p_44873_.nextInt(p_44875_ + 1);
         if (p_44874_ == 0) {
            return Math.max(j / 3, 1);
         } else {
            return p_44874_ == 1 ? j * 2 / 3 + 1 : Math.max(j, p_44875_ * 2);
         }
      }
   }

   public static ItemStack enchantItem(Random p_44878_, ItemStack p_44879_, int p_44880_, boolean p_44881_) {
      List<EnchantmentInstance> list = selectEnchantment(p_44878_, p_44879_, p_44880_, p_44881_);
      boolean flag = p_44879_.is(Items.BOOK);
      if (flag) {
         p_44879_ = new ItemStack(Items.ENCHANTED_BOOK);
      }

      for(EnchantmentInstance enchantmentinstance : list) {
         if (flag) {
            EnchantedBookItem.addEnchantment(p_44879_, enchantmentinstance);
         } else {
            p_44879_.enchant(enchantmentinstance.enchantment, enchantmentinstance.level);
         }
      }

      return p_44879_;
   }

   public static List<EnchantmentInstance> selectEnchantment(Random p_44910_, ItemStack p_44911_, int p_44912_, boolean p_44913_) {
      List<EnchantmentInstance> list = Lists.newArrayList();
      Item item = p_44911_.getItem();
      int i = p_44911_.getItemEnchantability();
      if (i <= 0) {
         return list;
      } else {
         p_44912_ += 1 + p_44910_.nextInt(i / 4 + 1) + p_44910_.nextInt(i / 4 + 1);
         float f = (p_44910_.nextFloat() + p_44910_.nextFloat() - 1.0F) * 0.15F;
         p_44912_ = Mth.clamp(Math.round((float)p_44912_ + (float)p_44912_ * f), 1, Integer.MAX_VALUE);
         List<EnchantmentInstance> list1 = getAvailableEnchantmentResults(p_44912_, p_44911_, p_44913_);
         if (!list1.isEmpty()) {
            WeightedRandom.getRandomItem(p_44910_, list1).ifPresent(list::add);

            while(p_44910_.nextInt(50) <= p_44912_) {
               if (!list.isEmpty()) {
                  filterCompatibleEnchantments(list1, Util.lastOf(list));
               }

               if (list1.isEmpty()) {
                  break;
               }

               WeightedRandom.getRandomItem(p_44910_, list1).ifPresent(list::add);
               p_44912_ /= 2;
            }
         }

         return list;
      }
   }

   public static void filterCompatibleEnchantments(List<EnchantmentInstance> p_44863_, EnchantmentInstance p_44864_) {
      Iterator<EnchantmentInstance> iterator = p_44863_.iterator();

      while(iterator.hasNext()) {
         if (!p_44864_.enchantment.isCompatibleWith((iterator.next()).enchantment)) {
            iterator.remove();
         }
      }

   }

   public static boolean isEnchantmentCompatible(Collection<Enchantment> p_44860_, Enchantment p_44861_) {
      for(Enchantment enchantment : p_44860_) {
         if (!enchantment.isCompatibleWith(p_44861_)) {
            return false;
         }
      }

      return true;
   }

   public static List<EnchantmentInstance> getAvailableEnchantmentResults(int p_44818_, ItemStack p_44819_, boolean p_44820_) {
      List<EnchantmentInstance> list = Lists.newArrayList();
      Item item = p_44819_.getItem();
      boolean flag = p_44819_.is(Items.BOOK);

      for(Enchantment enchantment : Registry.ENCHANTMENT) {
         if ((!enchantment.isTreasureOnly() || p_44820_) && enchantment.isDiscoverable() && (enchantment.canApplyAtEnchantingTable(p_44819_) || (flag && enchantment.isAllowedOnBooks()))) {
            for(int i = enchantment.getMaxLevel(); i > enchantment.getMinLevel() - 1; --i) {
               if (p_44818_ >= enchantment.getMinCost(i) && p_44818_ <= enchantment.getMaxCost(i)) {
                  list.add(new EnchantmentInstance(enchantment, i));
                  break;
               }
            }
         }
      }

      return list;
   }

   @FunctionalInterface
   interface EnchantmentVisitor {
      void accept(Enchantment p_44945_, int p_44946_);
   }
}
