package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;

public class ShowTradesToPlayer extends Behavior<Villager> {
   private static final int MAX_LOOK_TIME = 900;
   private static final int STARTING_LOOK_TIME = 40;
   @Nullable
   private ItemStack playerItemStack;
   private final List<ItemStack> displayItems = Lists.newArrayList();
   private int cycleCounter;
   private int displayIndex;
   private int lookTime;

   public ShowTradesToPlayer(int p_24096_, int p_24097_) {
      super(ImmutableMap.of(MemoryModuleType.INTERACTION_TARGET, MemoryStatus.VALUE_PRESENT), p_24096_, p_24097_);
   }

   public boolean checkExtraStartConditions(ServerLevel p_24106_, Villager p_24107_) {
      Brain<?> brain = p_24107_.getBrain();
      if (!brain.getMemory(MemoryModuleType.INTERACTION_TARGET).isPresent()) {
         return false;
      } else {
         LivingEntity livingentity = brain.getMemory(MemoryModuleType.INTERACTION_TARGET).get();
         return livingentity.getType() == EntityType.PLAYER && p_24107_.isAlive() && livingentity.isAlive() && !p_24107_.isBaby() && p_24107_.distanceToSqr(livingentity) <= 17.0D;
      }
   }

   public boolean canStillUse(ServerLevel p_24109_, Villager p_24110_, long p_24111_) {
      return this.checkExtraStartConditions(p_24109_, p_24110_) && this.lookTime > 0 && p_24110_.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET).isPresent();
   }

   public void start(ServerLevel p_24124_, Villager p_24125_, long p_24126_) {
      super.start(p_24124_, p_24125_, p_24126_);
      this.lookAtTarget(p_24125_);
      this.cycleCounter = 0;
      this.displayIndex = 0;
      this.lookTime = 40;
   }

   public void tick(ServerLevel p_24134_, Villager p_24135_, long p_24136_) {
      LivingEntity livingentity = this.lookAtTarget(p_24135_);
      this.findItemsToDisplay(livingentity, p_24135_);
      if (!this.displayItems.isEmpty()) {
         this.displayCyclingItems(p_24135_);
      } else {
         clearHeldItem(p_24135_);
         this.lookTime = Math.min(this.lookTime, 40);
      }

      --this.lookTime;
   }

   public void stop(ServerLevel p_24144_, Villager p_24145_, long p_24146_) {
      super.stop(p_24144_, p_24145_, p_24146_);
      p_24145_.getBrain().eraseMemory(MemoryModuleType.INTERACTION_TARGET);
      clearHeldItem(p_24145_);
      this.playerItemStack = null;
   }

   private void findItemsToDisplay(LivingEntity p_24113_, Villager p_24114_) {
      boolean flag = false;
      ItemStack itemstack = p_24113_.getMainHandItem();
      if (this.playerItemStack == null || !ItemStack.isSame(this.playerItemStack, itemstack)) {
         this.playerItemStack = itemstack;
         flag = true;
         this.displayItems.clear();
      }

      if (flag && !this.playerItemStack.isEmpty()) {
         this.updateDisplayItems(p_24114_);
         if (!this.displayItems.isEmpty()) {
            this.lookTime = 900;
            this.displayFirstItem(p_24114_);
         }
      }

   }

   private void displayFirstItem(Villager p_24116_) {
      displayAsHeldItem(p_24116_, this.displayItems.get(0));
   }

   private void updateDisplayItems(Villager p_24128_) {
      for(MerchantOffer merchantoffer : p_24128_.getOffers()) {
         if (!merchantoffer.isOutOfStock() && this.playerItemStackMatchesCostOfOffer(merchantoffer)) {
            this.displayItems.add(merchantoffer.getResult());
         }
      }

   }

   private boolean playerItemStackMatchesCostOfOffer(MerchantOffer p_24118_) {
      return ItemStack.isSame(this.playerItemStack, p_24118_.getCostA()) || ItemStack.isSame(this.playerItemStack, p_24118_.getCostB());
   }

   private static void clearHeldItem(Villager p_182374_) {
      p_182374_.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
      p_182374_.setDropChance(EquipmentSlot.MAINHAND, 0.085F);
   }

   private static void displayAsHeldItem(Villager p_182371_, ItemStack p_182372_) {
      p_182371_.setItemSlot(EquipmentSlot.MAINHAND, p_182372_);
      p_182371_.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
   }

   private LivingEntity lookAtTarget(Villager p_24138_) {
      Brain<?> brain = p_24138_.getBrain();
      LivingEntity livingentity = brain.getMemory(MemoryModuleType.INTERACTION_TARGET).get();
      brain.setMemory(MemoryModuleType.LOOK_TARGET, new EntityTracker(livingentity, true));
      return livingentity;
   }

   private void displayCyclingItems(Villager p_24148_) {
      if (this.displayItems.size() >= 2 && ++this.cycleCounter >= 40) {
         ++this.displayIndex;
         this.cycleCounter = 0;
         if (this.displayIndex > this.displayItems.size() - 1) {
            this.displayIndex = 0;
         }

         displayAsHeldItem(p_24148_, this.displayItems.get(this.displayIndex));
      }

   }
}