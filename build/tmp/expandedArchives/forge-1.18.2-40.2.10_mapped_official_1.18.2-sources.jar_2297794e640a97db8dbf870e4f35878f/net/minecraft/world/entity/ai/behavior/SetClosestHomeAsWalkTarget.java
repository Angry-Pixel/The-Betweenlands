package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.pathfinder.Path;

public class SetClosestHomeAsWalkTarget extends Behavior<LivingEntity> {
   private static final int CACHE_TIMEOUT = 40;
   private static final int BATCH_SIZE = 5;
   private static final int RATE = 20;
   private static final int OK_DISTANCE_SQR = 4;
   private final float speedModifier;
   private final Long2LongMap batchCache = new Long2LongOpenHashMap();
   private int triedCount;
   private long lastUpdate;

   public SetClosestHomeAsWalkTarget(float p_23877_) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.HOME, MemoryStatus.VALUE_ABSENT));
      this.speedModifier = p_23877_;
   }

   protected boolean checkExtraStartConditions(ServerLevel p_23879_, LivingEntity p_23880_) {
      if (p_23879_.getGameTime() - this.lastUpdate < 20L) {
         return false;
      } else {
         PathfinderMob pathfindermob = (PathfinderMob)p_23880_;
         PoiManager poimanager = p_23879_.getPoiManager();
         Optional<BlockPos> optional = poimanager.findClosest(PoiType.HOME.getPredicate(), p_23880_.blockPosition(), 48, PoiManager.Occupancy.ANY);
         return optional.isPresent() && !(optional.get().distSqr(pathfindermob.blockPosition()) <= 4.0D);
      }
   }

   protected void start(ServerLevel p_23882_, LivingEntity p_23883_, long p_23884_) {
      this.triedCount = 0;
      this.lastUpdate = p_23882_.getGameTime() + (long)p_23882_.getRandom().nextInt(20);
      PathfinderMob pathfindermob = (PathfinderMob)p_23883_;
      PoiManager poimanager = p_23882_.getPoiManager();
      Predicate<BlockPos> predicate = (p_23886_) -> {
         long i = p_23886_.asLong();
         if (this.batchCache.containsKey(i)) {
            return false;
         } else if (++this.triedCount >= 5) {
            return false;
         } else {
            this.batchCache.put(i, this.lastUpdate + 40L);
            return true;
         }
      };
      Stream<BlockPos> stream = poimanager.findAll(PoiType.HOME.getPredicate(), predicate, p_23883_.blockPosition(), 48, PoiManager.Occupancy.ANY);
      Path path = pathfindermob.getNavigation().createPath(stream, PoiType.HOME.getValidRange());
      if (path != null && path.canReach()) {
         BlockPos blockpos = path.getTarget();
         Optional<PoiType> optional = poimanager.getType(blockpos);
         if (optional.isPresent()) {
            p_23883_.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(blockpos, this.speedModifier, 1));
            DebugPackets.sendPoiTicketCountPacket(p_23882_, blockpos);
         }
      } else if (this.triedCount < 5) {
         this.batchCache.long2LongEntrySet().removeIf((p_23888_) -> {
            return p_23888_.getLongValue() < this.lastUpdate;
         });
      }

   }
}