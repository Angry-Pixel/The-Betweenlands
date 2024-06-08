package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;

public class RandomStroll extends Behavior<PathfinderMob> {
   private static final int MAX_XZ_DIST = 10;
   private static final int MAX_Y_DIST = 7;
   private final float speedModifier;
   protected final int maxHorizontalDistance;
   protected final int maxVerticalDistance;
   private final boolean mayStrollFromWater;

   public RandomStroll(float p_23744_) {
      this(p_23744_, true);
   }

   public RandomStroll(float p_182347_, boolean p_182348_) {
      this(p_182347_, 10, 7, p_182348_);
   }

   public RandomStroll(float p_23746_, int p_23747_, int p_23748_) {
      this(p_23746_, p_23747_, p_23748_, true);
   }

   public RandomStroll(float p_182342_, int p_182343_, int p_182344_, boolean p_182345_) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT));
      this.speedModifier = p_182342_;
      this.maxHorizontalDistance = p_182343_;
      this.maxVerticalDistance = p_182344_;
      this.mayStrollFromWater = p_182345_;
   }

   protected boolean checkExtraStartConditions(ServerLevel p_182353_, PathfinderMob p_182354_) {
      return this.mayStrollFromWater || !p_182354_.isInWaterOrBubble();
   }

   protected void start(ServerLevel p_23754_, PathfinderMob p_23755_, long p_23756_) {
      Optional<Vec3> optional = Optional.ofNullable(this.getTargetPos(p_23755_));
      p_23755_.getBrain().setMemory(MemoryModuleType.WALK_TARGET, optional.map((p_23758_) -> {
         return new WalkTarget(p_23758_, this.speedModifier, 0);
      }));
   }

   @Nullable
   protected Vec3 getTargetPos(PathfinderMob p_147851_) {
      return LandRandomPos.getPos(p_147851_, this.maxHorizontalDistance, this.maxVerticalDistance);
   }
}