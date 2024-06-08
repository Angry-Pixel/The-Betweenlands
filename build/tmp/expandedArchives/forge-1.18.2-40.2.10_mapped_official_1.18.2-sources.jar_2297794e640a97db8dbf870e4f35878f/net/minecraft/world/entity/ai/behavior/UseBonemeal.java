package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;

public class UseBonemeal extends Behavior<Villager> {
   private static final int BONEMEALING_DURATION = 80;
   private long nextWorkCycleTime;
   private long lastBonemealingSession;
   private int timeWorkedSoFar;
   private Optional<BlockPos> cropPos = Optional.empty();

   public UseBonemeal() {
      super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT));
   }

   protected boolean checkExtraStartConditions(ServerLevel p_24474_, Villager p_24475_) {
      if (p_24475_.tickCount % 10 == 0 && (this.lastBonemealingSession == 0L || this.lastBonemealingSession + 160L <= (long)p_24475_.tickCount)) {
         if (p_24475_.getInventory().countItem(Items.BONE_MEAL) <= 0) {
            return false;
         } else {
            this.cropPos = this.pickNextTarget(p_24474_, p_24475_);
            return this.cropPos.isPresent();
         }
      } else {
         return false;
      }
   }

   protected boolean canStillUse(ServerLevel p_24477_, Villager p_24478_, long p_24479_) {
      return this.timeWorkedSoFar < 80 && this.cropPos.isPresent();
   }

   private Optional<BlockPos> pickNextTarget(ServerLevel p_24493_, Villager p_24494_) {
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
      Optional<BlockPos> optional = Optional.empty();
      int i = 0;

      for(int j = -1; j <= 1; ++j) {
         for(int k = -1; k <= 1; ++k) {
            for(int l = -1; l <= 1; ++l) {
               blockpos$mutableblockpos.setWithOffset(p_24494_.blockPosition(), j, k, l);
               if (this.validPos(blockpos$mutableblockpos, p_24493_)) {
                  ++i;
                  if (p_24493_.random.nextInt(i) == 0) {
                     optional = Optional.of(blockpos$mutableblockpos.immutable());
                  }
               }
            }
         }
      }

      return optional;
   }

   private boolean validPos(BlockPos p_24486_, ServerLevel p_24487_) {
      BlockState blockstate = p_24487_.getBlockState(p_24486_);
      Block block = blockstate.getBlock();
      return block instanceof CropBlock && !((CropBlock)block).isMaxAge(blockstate);
   }

   protected void start(ServerLevel p_24496_, Villager p_24497_, long p_24498_) {
      this.setCurrentCropAsTarget(p_24497_);
      p_24497_.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BONE_MEAL));
      this.nextWorkCycleTime = p_24498_;
      this.timeWorkedSoFar = 0;
   }

   private void setCurrentCropAsTarget(Villager p_24481_) {
      this.cropPos.ifPresent((p_24484_) -> {
         BlockPosTracker blockpostracker = new BlockPosTracker(p_24484_);
         p_24481_.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, blockpostracker);
         p_24481_.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(blockpostracker, 0.5F, 1));
      });
   }

   protected void stop(ServerLevel p_24504_, Villager p_24505_, long p_24506_) {
      p_24505_.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
      this.lastBonemealingSession = (long)p_24505_.tickCount;
   }

   protected void tick(ServerLevel p_24512_, Villager p_24513_, long p_24514_) {
      BlockPos blockpos = this.cropPos.get();
      if (p_24514_ >= this.nextWorkCycleTime && blockpos.closerToCenterThan(p_24513_.position(), 1.0D)) {
         ItemStack itemstack = ItemStack.EMPTY;
         SimpleContainer simplecontainer = p_24513_.getInventory();
         int i = simplecontainer.getContainerSize();

         for(int j = 0; j < i; ++j) {
            ItemStack itemstack1 = simplecontainer.getItem(j);
            if (itemstack1.is(Items.BONE_MEAL)) {
               itemstack = itemstack1;
               break;
            }
         }

         if (!itemstack.isEmpty() && BoneMealItem.growCrop(itemstack, p_24512_, blockpos)) {
            p_24512_.levelEvent(1505, blockpos, 0);
            this.cropPos = this.pickNextTarget(p_24512_, p_24513_);
            this.setCurrentCropAsTarget(p_24513_);
            this.nextWorkCycleTime = p_24514_ + 40L;
         }

         ++this.timeWorkedSoFar;
      }
   }
}