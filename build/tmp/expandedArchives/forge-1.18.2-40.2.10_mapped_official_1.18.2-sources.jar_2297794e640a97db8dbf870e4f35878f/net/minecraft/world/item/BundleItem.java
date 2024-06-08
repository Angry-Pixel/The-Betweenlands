package net.minecraft.world.item;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.level.Level;

public class BundleItem extends Item {
   private static final String TAG_ITEMS = "Items";
   public static final int MAX_WEIGHT = 64;
   private static final int BUNDLE_IN_BUNDLE_WEIGHT = 4;
   private static final int BAR_COLOR = Mth.color(0.4F, 0.4F, 1.0F);

   public BundleItem(Item.Properties p_150726_) {
      super(p_150726_);
   }

   public static float getFullnessDisplay(ItemStack p_150767_) {
      return (float)getContentWeight(p_150767_) / 64.0F;
   }

   public boolean overrideStackedOnOther(ItemStack p_150733_, Slot p_150734_, ClickAction p_150735_, Player p_150736_) {
      if (p_150733_.getCount() != 1 || p_150735_ != ClickAction.SECONDARY) {
         return false;
      } else {
         ItemStack itemstack = p_150734_.getItem();
         if (itemstack.isEmpty()) {
            this.playRemoveOneSound(p_150736_);
            removeOne(p_150733_).ifPresent((p_150740_) -> {
               add(p_150733_, p_150734_.safeInsert(p_150740_));
            });
         } else if (itemstack.getItem().canFitInsideContainerItems()) {
            int i = (64 - getContentWeight(p_150733_)) / getWeight(itemstack);
            int j = add(p_150733_, p_150734_.safeTake(itemstack.getCount(), i, p_150736_));
            if (j > 0) {
               this.playInsertSound(p_150736_);
            }
         }

         return true;
      }
   }

   public boolean overrideOtherStackedOnMe(ItemStack p_150742_, ItemStack p_150743_, Slot p_150744_, ClickAction p_150745_, Player p_150746_, SlotAccess p_150747_) {
      if (p_150742_.getCount() != 1) return false;
      if (p_150745_ == ClickAction.SECONDARY && p_150744_.allowModification(p_150746_)) {
         if (p_150743_.isEmpty()) {
            removeOne(p_150742_).ifPresent((p_186347_) -> {
               this.playRemoveOneSound(p_150746_);
               p_150747_.set(p_186347_);
            });
         } else {
            int i = add(p_150742_, p_150743_);
            if (i > 0) {
               this.playInsertSound(p_150746_);
               p_150743_.shrink(i);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public InteractionResultHolder<ItemStack> use(Level p_150760_, Player p_150761_, InteractionHand p_150762_) {
      ItemStack itemstack = p_150761_.getItemInHand(p_150762_);
      if (dropContents(itemstack, p_150761_)) {
         this.playDropContentsSound(p_150761_);
         p_150761_.awardStat(Stats.ITEM_USED.get(this));
         return InteractionResultHolder.sidedSuccess(itemstack, p_150760_.isClientSide());
      } else {
         return InteractionResultHolder.fail(itemstack);
      }
   }

   public boolean isBarVisible(ItemStack p_150769_) {
      return getContentWeight(p_150769_) > 0;
   }

   public int getBarWidth(ItemStack p_150771_) {
      return Math.min(1 + 12 * getContentWeight(p_150771_) / 64, 13);
   }

   public int getBarColor(ItemStack p_150773_) {
      return BAR_COLOR;
   }

   private static int add(ItemStack p_150764_, ItemStack p_150765_) {
      if (!p_150765_.isEmpty() && p_150765_.getItem().canFitInsideContainerItems()) {
         CompoundTag compoundtag = p_150764_.getOrCreateTag();
         if (!compoundtag.contains("Items")) {
            compoundtag.put("Items", new ListTag());
         }

         int i = getContentWeight(p_150764_);
         int j = getWeight(p_150765_);
         int k = Math.min(p_150765_.getCount(), (64 - i) / j);
         if (k == 0) {
            return 0;
         } else {
            ListTag listtag = compoundtag.getList("Items", 10);
            Optional<CompoundTag> optional = getMatchingItem(p_150765_, listtag);
            if (optional.isPresent()) {
               CompoundTag compoundtag1 = optional.get();
               ItemStack itemstack = ItemStack.of(compoundtag1);
               itemstack.grow(k);
               itemstack.save(compoundtag1);
               listtag.remove(compoundtag1);
               listtag.add(0, (Tag)compoundtag1);
            } else {
               ItemStack itemstack1 = p_150765_.copy();
               itemstack1.setCount(k);
               CompoundTag compoundtag2 = new CompoundTag();
               itemstack1.save(compoundtag2);
               listtag.add(0, (Tag)compoundtag2);
            }

            return k;
         }
      } else {
         return 0;
      }
   }

   private static Optional<CompoundTag> getMatchingItem(ItemStack p_150757_, ListTag p_150758_) {
      return p_150757_.is(Items.BUNDLE) ? Optional.empty() : p_150758_.stream().filter(CompoundTag.class::isInstance).map(CompoundTag.class::cast).filter((p_186350_) -> {
         return ItemStack.isSameItemSameTags(ItemStack.of(p_186350_), p_150757_);
      }).findFirst();
   }

   private static int getWeight(ItemStack p_150777_) {
      if (p_150777_.is(Items.BUNDLE)) {
         return 4 + getContentWeight(p_150777_);
      } else {
         if ((p_150777_.is(Items.BEEHIVE) || p_150777_.is(Items.BEE_NEST)) && p_150777_.hasTag()) {
            CompoundTag compoundtag = BlockItem.getBlockEntityData(p_150777_);
            if (compoundtag != null && !compoundtag.getList("Bees", 10).isEmpty()) {
               return 64;
            }
         }

         return 64 / p_150777_.getMaxStackSize();
      }
   }

   private static int getContentWeight(ItemStack p_150779_) {
      return getContents(p_150779_).mapToInt((p_186356_) -> {
         return getWeight(p_186356_) * p_186356_.getCount();
      }).sum();
   }

   private static Optional<ItemStack> removeOne(ItemStack p_150781_) {
      CompoundTag compoundtag = p_150781_.getOrCreateTag();
      if (!compoundtag.contains("Items")) {
         return Optional.empty();
      } else {
         ListTag listtag = compoundtag.getList("Items", 10);
         if (listtag.isEmpty()) {
            return Optional.empty();
         } else {
            int i = 0;
            CompoundTag compoundtag1 = listtag.getCompound(0);
            ItemStack itemstack = ItemStack.of(compoundtag1);
            listtag.remove(0);
            if (listtag.isEmpty()) {
               p_150781_.removeTagKey("Items");
            }

            return Optional.of(itemstack);
         }
      }
   }

   private static boolean dropContents(ItemStack p_150730_, Player p_150731_) {
      CompoundTag compoundtag = p_150730_.getOrCreateTag();
      if (!compoundtag.contains("Items")) {
         return false;
      } else {
         if (p_150731_ instanceof ServerPlayer) {
            ListTag listtag = compoundtag.getList("Items", 10);

            for(int i = 0; i < listtag.size(); ++i) {
               CompoundTag compoundtag1 = listtag.getCompound(i);
               ItemStack itemstack = ItemStack.of(compoundtag1);
               p_150731_.drop(itemstack, true);
            }
         }

         p_150730_.removeTagKey("Items");
         return true;
      }
   }

   private static Stream<ItemStack> getContents(ItemStack p_150783_) {
      CompoundTag compoundtag = p_150783_.getTag();
      if (compoundtag == null) {
         return Stream.empty();
      } else {
         ListTag listtag = compoundtag.getList("Items", 10);
         return listtag.stream().map(CompoundTag.class::cast).map(ItemStack::of);
      }
   }

   public Optional<TooltipComponent> getTooltipImage(ItemStack p_150775_) {
      NonNullList<ItemStack> nonnulllist = NonNullList.create();
      getContents(p_150775_).forEach(nonnulllist::add);
      return Optional.of(new BundleTooltip(nonnulllist, getContentWeight(p_150775_)));
   }

   public void appendHoverText(ItemStack p_150749_, Level p_150750_, List<Component> p_150751_, TooltipFlag p_150752_) {
      p_150751_.add((new TranslatableComponent("item.minecraft.bundle.fullness", getContentWeight(p_150749_), 64)).withStyle(ChatFormatting.GRAY));
   }

   public void onDestroyed(ItemEntity p_150728_) {
      ItemUtils.onContainerDestroyed(p_150728_, getContents(p_150728_.getItem()));
   }

   private void playRemoveOneSound(Entity p_186343_) {
      p_186343_.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + p_186343_.getLevel().getRandom().nextFloat() * 0.4F);
   }

   private void playInsertSound(Entity p_186352_) {
      p_186352_.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + p_186352_.getLevel().getRandom().nextFloat() * 0.4F);
   }

   private void playDropContentsSound(Entity p_186354_) {
      p_186354_.playSound(SoundEvents.BUNDLE_DROP_CONTENTS, 0.8F, 0.8F + p_186354_.getLevel().getRandom().nextFloat() * 0.4F);
   }
}
