package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;

public class InteractWithDoor extends Behavior<LivingEntity> {
   private static final int COOLDOWN_BEFORE_RERUNNING_IN_SAME_NODE = 20;
   private static final double SKIP_CLOSING_DOOR_IF_FURTHER_AWAY_THAN = 2.0D;
   private static final double MAX_DISTANCE_TO_HOLD_DOOR_OPEN_FOR_OTHER_MOBS = 2.0D;
   @Nullable
   private Node lastCheckedNode;
   private int remainingCooldown;

   public InteractWithDoor() {
      super(ImmutableMap.of(MemoryModuleType.PATH, MemoryStatus.VALUE_PRESENT, MemoryModuleType.DOORS_TO_CLOSE, MemoryStatus.REGISTERED));
   }

   protected boolean checkExtraStartConditions(ServerLevel p_23292_, LivingEntity p_23293_) {
      Path path = p_23293_.getBrain().getMemory(MemoryModuleType.PATH).get();
      if (!path.notStarted() && !path.isDone()) {
         if (!Objects.equals(this.lastCheckedNode, path.getNextNode())) {
            this.remainingCooldown = 20;
            return true;
         } else {
            if (this.remainingCooldown > 0) {
               --this.remainingCooldown;
            }

            return this.remainingCooldown == 0;
         }
      } else {
         return false;
      }
   }

   protected void start(ServerLevel p_23295_, LivingEntity p_23296_, long p_23297_) {
      Path path = p_23296_.getBrain().getMemory(MemoryModuleType.PATH).get();
      this.lastCheckedNode = path.getNextNode();
      Node node = path.getPreviousNode();
      Node node1 = path.getNextNode();
      BlockPos blockpos = node.asBlockPos();
      BlockState blockstate = p_23295_.getBlockState(blockpos);
      if (blockstate.is(BlockTags.WOODEN_DOORS, (p_201959_) -> {
         return p_201959_.getBlock() instanceof DoorBlock;
      })) {
         DoorBlock doorblock = (DoorBlock)blockstate.getBlock();
         if (!doorblock.isOpen(blockstate)) {
            doorblock.setOpen(p_23296_, p_23295_, blockstate, blockpos, true);
         }

         this.rememberDoorToClose(p_23295_, p_23296_, blockpos);
      }

      BlockPos blockpos1 = node1.asBlockPos();
      BlockState blockstate1 = p_23295_.getBlockState(blockpos1);
      if (blockstate1.is(BlockTags.WOODEN_DOORS, (p_201957_) -> {
         return p_201957_.getBlock() instanceof DoorBlock;
      })) {
         DoorBlock doorblock1 = (DoorBlock)blockstate1.getBlock();
         if (!doorblock1.isOpen(blockstate1)) {
            doorblock1.setOpen(p_23296_, p_23295_, blockstate1, blockpos1, true);
            this.rememberDoorToClose(p_23295_, p_23296_, blockpos1);
         }
      }

      closeDoorsThatIHaveOpenedOrPassedThrough(p_23295_, p_23296_, node, node1);
   }

   public static void closeDoorsThatIHaveOpenedOrPassedThrough(ServerLevel p_23299_, LivingEntity p_23300_, @Nullable Node p_23301_, @Nullable Node p_23302_) {
      Brain<?> brain = p_23300_.getBrain();
      if (brain.hasMemoryValue(MemoryModuleType.DOORS_TO_CLOSE)) {
         Iterator<GlobalPos> iterator = brain.getMemory(MemoryModuleType.DOORS_TO_CLOSE).get().iterator();

         while(iterator.hasNext()) {
            GlobalPos globalpos = iterator.next();
            BlockPos blockpos = globalpos.pos();
            if ((p_23301_ == null || !p_23301_.asBlockPos().equals(blockpos)) && (p_23302_ == null || !p_23302_.asBlockPos().equals(blockpos))) {
               if (isDoorTooFarAway(p_23299_, p_23300_, globalpos)) {
                  iterator.remove();
               } else {
                  BlockState blockstate = p_23299_.getBlockState(blockpos);
                  if (!blockstate.is(BlockTags.WOODEN_DOORS, (p_201952_) -> {
                     return p_201952_.getBlock() instanceof DoorBlock;
                  })) {
                     iterator.remove();
                  } else {
                     DoorBlock doorblock = (DoorBlock)blockstate.getBlock();
                     if (!doorblock.isOpen(blockstate)) {
                        iterator.remove();
                     } else if (areOtherMobsComingThroughDoor(p_23299_, p_23300_, blockpos)) {
                        iterator.remove();
                     } else {
                        doorblock.setOpen(p_23300_, p_23299_, blockstate, blockpos, false);
                        iterator.remove();
                     }
                  }
               }
            }
         }
      }

   }

   private static boolean areOtherMobsComingThroughDoor(ServerLevel p_23304_, LivingEntity p_23305_, BlockPos p_23306_) {
      Brain<?> brain = p_23305_.getBrain();
      return !brain.hasMemoryValue(MemoryModuleType.NEAREST_LIVING_ENTITIES) ? false : brain.getMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES).get().stream().filter((p_201950_) -> {
         return p_201950_.getType() == p_23305_.getType();
      }).filter((p_201955_) -> {
         return p_23306_.closerToCenterThan(p_201955_.position(), 2.0D);
      }).anyMatch((p_201947_) -> {
         return isMobComingThroughDoor(p_23304_, p_201947_, p_23306_);
      });
   }

   private static boolean isMobComingThroughDoor(ServerLevel p_23322_, LivingEntity p_23323_, BlockPos p_23324_) {
      if (!p_23323_.getBrain().hasMemoryValue(MemoryModuleType.PATH)) {
         return false;
      } else {
         Path path = p_23323_.getBrain().getMemory(MemoryModuleType.PATH).get();
         if (path.isDone()) {
            return false;
         } else {
            Node node = path.getPreviousNode();
            if (node == null) {
               return false;
            } else {
               Node node1 = path.getNextNode();
               return p_23324_.equals(node.asBlockPos()) || p_23324_.equals(node1.asBlockPos());
            }
         }
      }
   }

   private static boolean isDoorTooFarAway(ServerLevel p_23308_, LivingEntity p_23309_, GlobalPos p_23310_) {
      return p_23310_.dimension() != p_23308_.dimension() || !p_23310_.pos().closerToCenterThan(p_23309_.position(), 2.0D);
   }

   private void rememberDoorToClose(ServerLevel p_23326_, LivingEntity p_23327_, BlockPos p_23328_) {
      Brain<?> brain = p_23327_.getBrain();
      GlobalPos globalpos = GlobalPos.of(p_23326_.dimension(), p_23328_);
      if (brain.getMemory(MemoryModuleType.DOORS_TO_CLOSE).isPresent()) {
         brain.getMemory(MemoryModuleType.DOORS_TO_CLOSE).get().add(globalpos);
      } else {
         brain.setMemory(MemoryModuleType.DOORS_TO_CLOSE, Sets.newHashSet(globalpos));
      }

   }
}