package net.minecraft.world.entity.ai.memory;

import com.google.common.collect.Iterables;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.sensing.Sensor;

public class NearestVisibleLivingEntities {
   private static final NearestVisibleLivingEntities EMPTY = new NearestVisibleLivingEntities();
   private final List<LivingEntity> nearbyEntities;
   private final Predicate<LivingEntity> lineOfSightTest;

   private NearestVisibleLivingEntities() {
      this.nearbyEntities = List.of();
      this.lineOfSightTest = (p_186122_) -> {
         return false;
      };
   }

   public NearestVisibleLivingEntities(LivingEntity p_186104_, List<LivingEntity> p_186105_) {
      this.nearbyEntities = p_186105_;
      Object2BooleanOpenHashMap<LivingEntity> object2booleanopenhashmap = new Object2BooleanOpenHashMap<>(p_186105_.size());
      Predicate<LivingEntity> predicate = (p_186111_) -> {
         return Sensor.isEntityTargetable(p_186104_, p_186111_);
      };
      this.lineOfSightTest = (p_186115_) -> {
         return object2booleanopenhashmap.computeBooleanIfAbsent(p_186115_, predicate);
      };
   }

   public static NearestVisibleLivingEntities empty() {
      return EMPTY;
   }

   public Optional<LivingEntity> findClosest(Predicate<LivingEntity> p_186117_) {
      for(LivingEntity livingentity : this.nearbyEntities) {
         if (p_186117_.test(livingentity) && this.lineOfSightTest.test(livingentity)) {
            return Optional.of(livingentity);
         }
      }

      return Optional.empty();
   }

   public Iterable<LivingEntity> findAll(Predicate<LivingEntity> p_186124_) {
      return Iterables.filter(this.nearbyEntities, (p_186127_) -> {
         return p_186124_.test(p_186127_) && this.lineOfSightTest.test(p_186127_);
      });
   }

   public Stream<LivingEntity> find(Predicate<LivingEntity> p_186129_) {
      return this.nearbyEntities.stream().filter((p_186120_) -> {
         return p_186129_.test(p_186120_) && this.lineOfSightTest.test(p_186120_);
      });
   }

   public boolean contains(LivingEntity p_186108_) {
      return this.nearbyEntities.contains(p_186108_) && this.lineOfSightTest.test(p_186108_);
   }

   public boolean contains(Predicate<LivingEntity> p_186131_) {
      for(LivingEntity livingentity : this.nearbyEntities) {
         if (p_186131_.test(livingentity) && this.lineOfSightTest.test(livingentity)) {
            return true;
         }
      }

      return false;
   }
}