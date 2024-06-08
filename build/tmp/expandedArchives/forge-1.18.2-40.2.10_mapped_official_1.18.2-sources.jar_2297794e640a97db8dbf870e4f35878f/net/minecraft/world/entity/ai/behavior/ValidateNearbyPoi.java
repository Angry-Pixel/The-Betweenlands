package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ValidateNearbyPoi extends Behavior<LivingEntity> {
   private static final int MAX_DISTANCE = 16;
   private final MemoryModuleType<GlobalPos> memoryType;
   private final Predicate<PoiType> poiPredicate;

   public ValidateNearbyPoi(PoiType p_24518_, MemoryModuleType<GlobalPos> p_24519_) {
      super(ImmutableMap.of(p_24519_, MemoryStatus.VALUE_PRESENT));
      this.poiPredicate = p_24518_.getPredicate();
      this.memoryType = p_24519_;
   }

   protected boolean checkExtraStartConditions(ServerLevel p_24521_, LivingEntity p_24522_) {
      GlobalPos globalpos = p_24522_.getBrain().getMemory(this.memoryType).get();
      return p_24521_.dimension() == globalpos.dimension() && globalpos.pos().closerToCenterThan(p_24522_.position(), 16.0D);
   }

   protected void start(ServerLevel p_24524_, LivingEntity p_24525_, long p_24526_) {
      Brain<?> brain = p_24525_.getBrain();
      GlobalPos globalpos = brain.getMemory(this.memoryType).get();
      BlockPos blockpos = globalpos.pos();
      ServerLevel serverlevel = p_24524_.getServer().getLevel(globalpos.dimension());
      if (serverlevel != null && !this.poiDoesntExist(serverlevel, blockpos)) {
         if (this.bedIsOccupied(serverlevel, blockpos, p_24525_)) {
            brain.eraseMemory(this.memoryType);
            p_24524_.getPoiManager().release(blockpos);
            DebugPackets.sendPoiTicketCountPacket(p_24524_, blockpos);
         }
      } else {
         brain.eraseMemory(this.memoryType);
      }

   }

   private boolean bedIsOccupied(ServerLevel p_24531_, BlockPos p_24532_, LivingEntity p_24533_) {
      BlockState blockstate = p_24531_.getBlockState(p_24532_);
      return blockstate.is(BlockTags.BEDS) && blockstate.getValue(BedBlock.OCCUPIED) && !p_24533_.isSleeping();
   }

   private boolean poiDoesntExist(ServerLevel p_24528_, BlockPos p_24529_) {
      return !p_24528_.getPoiManager().exists(p_24529_, this.poiPredicate);
   }
}