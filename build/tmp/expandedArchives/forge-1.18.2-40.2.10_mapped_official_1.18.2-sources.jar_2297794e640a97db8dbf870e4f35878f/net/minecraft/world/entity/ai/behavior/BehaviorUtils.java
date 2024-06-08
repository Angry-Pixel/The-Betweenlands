package net.minecraft.world.entity.ai.behavior;

import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;

public class BehaviorUtils {
   private BehaviorUtils() {
   }

   public static void lockGazeAndWalkToEachOther(LivingEntity p_22603_, LivingEntity p_22604_, float p_22605_) {
      lookAtEachOther(p_22603_, p_22604_);
      setWalkAndLookTargetMemoriesToEachOther(p_22603_, p_22604_, p_22605_);
   }

   public static boolean entityIsVisible(Brain<?> p_22637_, LivingEntity p_22638_) {
      Optional<NearestVisibleLivingEntities> optional = p_22637_.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
      return optional.isPresent() && optional.get().contains(p_22638_);
   }

   public static boolean targetIsValid(Brain<?> p_22640_, MemoryModuleType<? extends LivingEntity> p_22641_, EntityType<?> p_22642_) {
      return targetIsValid(p_22640_, p_22641_, (p_186022_) -> {
         return p_186022_.getType() == p_22642_;
      });
   }

   private static boolean targetIsValid(Brain<?> p_22644_, MemoryModuleType<? extends LivingEntity> p_22645_, Predicate<LivingEntity> p_22646_) {
      return p_22644_.getMemory(p_22645_).filter(p_22646_).filter(LivingEntity::isAlive).filter((p_186037_) -> {
         return entityIsVisible(p_22644_, p_186037_);
      }).isPresent();
   }

   private static void lookAtEachOther(LivingEntity p_22671_, LivingEntity p_22672_) {
      lookAtEntity(p_22671_, p_22672_);
      lookAtEntity(p_22672_, p_22671_);
   }

   public static void lookAtEntity(LivingEntity p_22596_, LivingEntity p_22597_) {
      p_22596_.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new EntityTracker(p_22597_, true));
   }

   private static void setWalkAndLookTargetMemoriesToEachOther(LivingEntity p_22661_, LivingEntity p_22662_, float p_22663_) {
      int i = 2;
      setWalkAndLookTargetMemories(p_22661_, p_22662_, p_22663_, 2);
      setWalkAndLookTargetMemories(p_22662_, p_22661_, p_22663_, 2);
   }

   public static void setWalkAndLookTargetMemories(LivingEntity p_22591_, Entity p_22592_, float p_22593_, int p_22594_) {
      WalkTarget walktarget = new WalkTarget(new EntityTracker(p_22592_, false), p_22593_, p_22594_);
      p_22591_.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new EntityTracker(p_22592_, true));
      p_22591_.getBrain().setMemory(MemoryModuleType.WALK_TARGET, walktarget);
   }

   public static void setWalkAndLookTargetMemories(LivingEntity p_22618_, BlockPos p_22619_, float p_22620_, int p_22621_) {
      WalkTarget walktarget = new WalkTarget(new BlockPosTracker(p_22619_), p_22620_, p_22621_);
      p_22618_.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosTracker(p_22619_));
      p_22618_.getBrain().setMemory(MemoryModuleType.WALK_TARGET, walktarget);
   }

   public static void throwItem(LivingEntity p_22614_, ItemStack p_22615_, Vec3 p_22616_) {
      double d0 = p_22614_.getEyeY() - (double)0.3F;
      ItemEntity itementity = new ItemEntity(p_22614_.level, p_22614_.getX(), d0, p_22614_.getZ(), p_22615_);
      float f = 0.3F;
      Vec3 vec3 = p_22616_.subtract(p_22614_.position());
      vec3 = vec3.normalize().scale((double)0.3F);
      itementity.setDeltaMovement(vec3);
      itementity.setDefaultPickUpDelay();
      p_22614_.level.addFreshEntity(itementity);
   }

   public static SectionPos findSectionClosestToVillage(ServerLevel p_22582_, SectionPos p_22583_, int p_22584_) {
      int i = p_22582_.sectionsToVillage(p_22583_);
      return SectionPos.cube(p_22583_, p_22584_).filter((p_186017_) -> {
         return p_22582_.sectionsToVillage(p_186017_) < i;
      }).min(Comparator.comparingInt(p_22582_::sectionsToVillage)).orElse(p_22583_);
   }

   public static boolean isWithinAttackRange(Mob p_22633_, LivingEntity p_22634_, int p_22635_) {
      Item item = p_22633_.getMainHandItem().getItem();
      if (item instanceof ProjectileWeaponItem) {
         ProjectileWeaponItem projectileweaponitem = (ProjectileWeaponItem)item;
         if (p_22633_.canFireProjectileWeapon((ProjectileWeaponItem)item)) {
            int i = projectileweaponitem.getDefaultProjectileRange() - p_22635_;
            return p_22633_.closerThan(p_22634_, (double)i);
         }
      }

      return isWithinMeleeAttackRange(p_22633_, p_22634_);
   }

   public static boolean isWithinMeleeAttackRange(Mob p_147442_, LivingEntity p_147443_) {
      double d0 = p_147442_.distanceToSqr(p_147443_.getX(), p_147443_.getY(), p_147443_.getZ());
      return d0 <= p_147442_.getMeleeAttackRangeSqr(p_147443_);
   }

   public static boolean isOtherTargetMuchFurtherAwayThanCurrentAttackTarget(LivingEntity p_22599_, LivingEntity p_22600_, double p_22601_) {
      Optional<LivingEntity> optional = p_22599_.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET);
      if (optional.isEmpty()) {
         return false;
      } else {
         double d0 = p_22599_.distanceToSqr(optional.get().position());
         double d1 = p_22599_.distanceToSqr(p_22600_.position());
         return d1 > d0 + p_22601_ * p_22601_;
      }
   }

   public static boolean canSee(LivingEntity p_22668_, LivingEntity p_22669_) {
      Brain<?> brain = p_22668_.getBrain();
      return !brain.hasMemoryValue(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES) ? false : brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).get().contains(p_22669_);
   }

   public static LivingEntity getNearestTarget(LivingEntity p_22626_, Optional<LivingEntity> p_22627_, LivingEntity p_22628_) {
      return p_22627_.isEmpty() ? p_22628_ : getTargetNearestMe(p_22626_, p_22627_.get(), p_22628_);
   }

   public static LivingEntity getTargetNearestMe(LivingEntity p_22607_, LivingEntity p_22608_, LivingEntity p_22609_) {
      Vec3 vec3 = p_22608_.position();
      Vec3 vec31 = p_22609_.position();
      return p_22607_.distanceToSqr(vec3) < p_22607_.distanceToSqr(vec31) ? p_22608_ : p_22609_;
   }

   public static Optional<LivingEntity> getLivingEntityFromUUIDMemory(LivingEntity p_22611_, MemoryModuleType<UUID> p_22612_) {
      Optional<UUID> optional = p_22611_.getBrain().getMemory(p_22612_);
      return optional.map((p_186027_) -> {
         return ((ServerLevel)p_22611_.level).getEntity(p_186027_);
      }).map((p_186019_) -> {
         LivingEntity livingentity1;
         if (p_186019_ instanceof LivingEntity) {
            LivingEntity livingentity = (LivingEntity)p_186019_;
            livingentity1 = livingentity;
         } else {
            livingentity1 = null;
         }

         return livingentity1;
      });
   }

   public static Stream<Villager> getNearbyVillagersWithCondition(Villager p_22651_, Predicate<Villager> p_22652_) {
      return p_22651_.getBrain().getMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES).map((p_186034_) -> {
         return p_186034_.stream().filter((p_186030_) -> {
            return p_186030_ instanceof Villager && p_186030_ != p_22651_;
         }).map((p_186024_) -> {
            return (Villager)p_186024_;
         }).filter(LivingEntity::isAlive).filter(p_22652_);
      }).orElseGet(Stream::empty);
   }

   @Nullable
   public static Vec3 getRandomSwimmablePos(PathfinderMob p_147445_, int p_147446_, int p_147447_) {
      Vec3 vec3 = DefaultRandomPos.getPos(p_147445_, p_147446_, p_147447_);

      for(int i = 0; vec3 != null && !p_147445_.level.getBlockState(new BlockPos(vec3)).isPathfindable(p_147445_.level, new BlockPos(vec3), PathComputationType.WATER) && i++ < 10; vec3 = DefaultRandomPos.getPos(p_147445_, p_147446_, p_147447_)) {
      }

      return vec3;
   }
}