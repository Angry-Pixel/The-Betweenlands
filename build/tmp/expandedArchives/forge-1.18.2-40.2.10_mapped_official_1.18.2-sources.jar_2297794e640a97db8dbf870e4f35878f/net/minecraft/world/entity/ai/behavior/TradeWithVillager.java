package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class TradeWithVillager extends Behavior<Villager> {
   private static final int INTERACT_DIST_SQR = 5;
   private static final float SPEED_MODIFIER = 0.5F;
   private Set<Item> trades = ImmutableSet.of();

   public TradeWithVillager() {
      super(ImmutableMap.of(MemoryModuleType.INTERACTION_TARGET, MemoryStatus.VALUE_PRESENT, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT));
   }

   protected boolean checkExtraStartConditions(ServerLevel p_24416_, Villager p_24417_) {
      return BehaviorUtils.targetIsValid(p_24417_.getBrain(), MemoryModuleType.INTERACTION_TARGET, EntityType.VILLAGER);
   }

   protected boolean canStillUse(ServerLevel p_24419_, Villager p_24420_, long p_24421_) {
      return this.checkExtraStartConditions(p_24419_, p_24420_);
   }

   protected void start(ServerLevel p_24437_, Villager p_24438_, long p_24439_) {
      Villager villager = (Villager)p_24438_.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET).get();
      BehaviorUtils.lockGazeAndWalkToEachOther(p_24438_, villager, 0.5F);
      this.trades = figureOutWhatIAmWillingToTrade(p_24438_, villager);
   }

   protected void tick(ServerLevel p_24445_, Villager p_24446_, long p_24447_) {
      Villager villager = (Villager)p_24446_.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET).get();
      if (!(p_24446_.distanceToSqr(villager) > 5.0D)) {
         BehaviorUtils.lockGazeAndWalkToEachOther(p_24446_, villager, 0.5F);
         p_24446_.gossip(p_24445_, villager, p_24447_);
         if (p_24446_.hasExcessFood() && (p_24446_.getVillagerData().getProfession() == VillagerProfession.FARMER || villager.wantsMoreFood())) {
            throwHalfStack(p_24446_, Villager.FOOD_POINTS.keySet(), villager);
         }

         if (villager.getVillagerData().getProfession() == VillagerProfession.FARMER && p_24446_.getInventory().countItem(Items.WHEAT) > Items.WHEAT.getMaxStackSize() / 2) {
            throwHalfStack(p_24446_, ImmutableSet.of(Items.WHEAT), villager);
         }

         if (!this.trades.isEmpty() && p_24446_.getInventory().hasAnyOf(this.trades)) {
            throwHalfStack(p_24446_, this.trades, villager);
         }

      }
   }

   protected void stop(ServerLevel p_24453_, Villager p_24454_, long p_24455_) {
      p_24454_.getBrain().eraseMemory(MemoryModuleType.INTERACTION_TARGET);
   }

   private static Set<Item> figureOutWhatIAmWillingToTrade(Villager p_24423_, Villager p_24424_) {
      ImmutableSet<Item> immutableset = p_24424_.getVillagerData().getProfession().getRequestedItems();
      ImmutableSet<Item> immutableset1 = p_24423_.getVillagerData().getProfession().getRequestedItems();
      return immutableset.stream().filter((p_24431_) -> {
         return !immutableset1.contains(p_24431_);
      }).collect(Collectors.toSet());
   }

   private static void throwHalfStack(Villager p_24426_, Set<Item> p_24427_, LivingEntity p_24428_) {
      SimpleContainer simplecontainer = p_24426_.getInventory();
      ItemStack itemstack = ItemStack.EMPTY;
      int i = 0;

      while(i < simplecontainer.getContainerSize()) {
         ItemStack itemstack1;
         Item item;
         int j;
         label28: {
            itemstack1 = simplecontainer.getItem(i);
            if (!itemstack1.isEmpty()) {
               item = itemstack1.getItem();
               if (p_24427_.contains(item)) {
                  if (itemstack1.getCount() > itemstack1.getMaxStackSize() / 2) {
                     j = itemstack1.getCount() / 2;
                     break label28;
                  }

                  if (itemstack1.getCount() > 24) {
                     j = itemstack1.getCount() - 24;
                     break label28;
                  }
               }
            }

            ++i;
            continue;
         }

         itemstack1.shrink(j);
         itemstack = new ItemStack(item, j);
         break;
      }

      if (!itemstack.isEmpty()) {
         BehaviorUtils.throwItem(p_24426_, itemstack, p_24428_.position());
      }

   }
}