package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;

public class PlayTagWithOtherKids extends Behavior<PathfinderMob> {
   private static final int MAX_FLEE_XZ_DIST = 20;
   private static final int MAX_FLEE_Y_DIST = 8;
   private static final float FLEE_SPEED_MODIFIER = 0.6F;
   private static final float CHASE_SPEED_MODIFIER = 0.6F;
   private static final int MAX_CHASERS_PER_TARGET = 5;
   private static final int AVERAGE_WAIT_TIME_BETWEEN_RUNS = 10;

   public PlayTagWithOtherKids() {
      super(ImmutableMap.of(MemoryModuleType.VISIBLE_VILLAGER_BABIES, MemoryStatus.VALUE_PRESENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.INTERACTION_TARGET, MemoryStatus.REGISTERED));
   }

   protected boolean checkExtraStartConditions(ServerLevel p_23629_, PathfinderMob p_23630_) {
      return p_23629_.getRandom().nextInt(10) == 0 && this.hasFriendsNearby(p_23630_);
   }

   protected void start(ServerLevel p_23632_, PathfinderMob p_23633_, long p_23634_) {
      LivingEntity livingentity = this.seeIfSomeoneIsChasingMe(p_23633_);
      if (livingentity != null) {
         this.fleeFromChaser(p_23632_, p_23633_, livingentity);
      } else {
         Optional<LivingEntity> optional = this.findSomeoneBeingChased(p_23633_);
         if (optional.isPresent()) {
            chaseKid(p_23633_, optional.get());
         } else {
            this.findSomeoneToChase(p_23633_).ifPresent((p_23666_) -> {
               chaseKid(p_23633_, p_23666_);
            });
         }
      }
   }

   private void fleeFromChaser(ServerLevel p_23636_, PathfinderMob p_23637_, LivingEntity p_23638_) {
      for(int i = 0; i < 10; ++i) {
         Vec3 vec3 = LandRandomPos.getPos(p_23637_, 20, 8);
         if (vec3 != null && p_23636_.isVillage(new BlockPos(vec3))) {
            p_23637_.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(vec3, 0.6F, 0));
            return;
         }
      }

   }

   private static void chaseKid(PathfinderMob p_23650_, LivingEntity p_23651_) {
      Brain<?> brain = p_23650_.getBrain();
      brain.setMemory(MemoryModuleType.INTERACTION_TARGET, p_23651_);
      brain.setMemory(MemoryModuleType.LOOK_TARGET, new EntityTracker(p_23651_, true));
      brain.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new EntityTracker(p_23651_, false), 0.6F, 1));
   }

   private Optional<LivingEntity> findSomeoneToChase(PathfinderMob p_23648_) {
      return this.getFriendsNearby(p_23648_).stream().findAny();
   }

   private Optional<LivingEntity> findSomeoneBeingChased(PathfinderMob p_23663_) {
      Map<LivingEntity, Integer> map = this.checkHowManyChasersEachFriendHas(p_23663_);
      return map.entrySet().stream().sorted(Comparator.comparingInt(Entry::getValue)).filter((p_23653_) -> {
         return p_23653_.getValue() > 0 && p_23653_.getValue() <= 5;
      }).map(Entry::getKey).findFirst();
   }

   private Map<LivingEntity, Integer> checkHowManyChasersEachFriendHas(PathfinderMob p_23673_) {
      Map<LivingEntity, Integer> map = Maps.newHashMap();
      this.getFriendsNearby(p_23673_).stream().filter(this::isChasingSomeone).forEach((p_23656_) -> {
         map.compute(this.whoAreYouChasing(p_23656_), (p_147707_, p_147708_) -> {
            return p_147708_ == null ? 1 : p_147708_ + 1;
         });
      });
      return map;
   }

   private List<LivingEntity> getFriendsNearby(PathfinderMob p_23675_) {
      return p_23675_.getBrain().getMemory(MemoryModuleType.VISIBLE_VILLAGER_BABIES).get();
   }

   private LivingEntity whoAreYouChasing(LivingEntity p_23640_) {
      return p_23640_.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET).get();
   }

   @Nullable
   private LivingEntity seeIfSomeoneIsChasingMe(LivingEntity p_23658_) {
      return p_23658_.getBrain().getMemory(MemoryModuleType.VISIBLE_VILLAGER_BABIES).get().stream().filter((p_23671_) -> {
         return this.isFriendChasingMe(p_23658_, p_23671_);
      }).findAny().orElse((LivingEntity)null);
   }

   private boolean isChasingSomeone(LivingEntity p_23668_) {
      return p_23668_.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET).isPresent();
   }

   private boolean isFriendChasingMe(LivingEntity p_23642_, LivingEntity p_23643_) {
      return p_23643_.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET).filter((p_23661_) -> {
         return p_23661_ == p_23642_;
      }).isPresent();
   }

   private boolean hasFriendsNearby(PathfinderMob p_23677_) {
      return p_23677_.getBrain().hasMemoryValue(MemoryModuleType.VISIBLE_VILLAGER_BABIES);
   }
}