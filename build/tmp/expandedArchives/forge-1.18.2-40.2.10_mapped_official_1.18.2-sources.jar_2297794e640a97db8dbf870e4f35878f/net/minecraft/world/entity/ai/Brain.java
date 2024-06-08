package net.minecraft.world.entity.ai;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.ExpirableValue;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import org.apache.commons.lang3.mutable.MutableObject;
import org.slf4j.Logger;

public class Brain<E extends LivingEntity> {
   static final Logger LOGGER = LogUtils.getLogger();
   private final Supplier<Codec<Brain<E>>> codec;
   private static final int SCHEDULE_UPDATE_DELAY = 20;
   private final Map<MemoryModuleType<?>, Optional<? extends ExpirableValue<?>>> memories = Maps.newHashMap();
   private final Map<SensorType<? extends Sensor<? super E>>, Sensor<? super E>> sensors = Maps.newLinkedHashMap();
   private final Map<Integer, Map<Activity, Set<Behavior<? super E>>>> availableBehaviorsByPriority = Maps.newTreeMap();
   private Schedule schedule = Schedule.EMPTY;
   private final Map<Activity, Set<Pair<MemoryModuleType<?>, MemoryStatus>>> activityRequirements = Maps.newHashMap();
   private final Map<Activity, Set<MemoryModuleType<?>>> activityMemoriesToEraseWhenStopped = Maps.newHashMap();
   private Set<Activity> coreActivities = Sets.newHashSet();
   private final Set<Activity> activeActivities = Sets.newHashSet();
   private Activity defaultActivity = Activity.IDLE;
   private long lastScheduleUpdate = -9999L;

   public static <E extends LivingEntity> Brain.Provider<E> provider(Collection<? extends MemoryModuleType<?>> p_21924_, Collection<? extends SensorType<? extends Sensor<? super E>>> p_21925_) {
      return new Brain.Provider<>(p_21924_, p_21925_);
   }

   public static <E extends LivingEntity> Codec<Brain<E>> codec(final Collection<? extends MemoryModuleType<?>> p_21947_, final Collection<? extends SensorType<? extends Sensor<? super E>>> p_21948_) {
      final MutableObject<Codec<Brain<E>>> mutableobject = new MutableObject<>();
      mutableobject.setValue((new MapCodec<Brain<E>>() {
         public <T> Stream<T> keys(DynamicOps<T> p_22029_) {
            return p_21947_.stream().flatMap((p_22020_) -> {
               return p_22020_.getCodec().map((p_147346_) -> {
                  return Registry.MEMORY_MODULE_TYPE.getKey(p_22020_);
               }).stream();
            }).map((p_22018_) -> {
               return p_22029_.createString(p_22018_.toString());
            });
         }

         public <T> DataResult<Brain<E>> decode(DynamicOps<T> p_22022_, MapLike<T> p_22023_) {
            MutableObject<DataResult<Builder<Brain.MemoryValue<?>>>> mutableobject1 = new MutableObject<>(DataResult.success(ImmutableList.builder()));
            p_22023_.entries().forEach((p_22015_) -> {
               DataResult<MemoryModuleType<?>> dataresult = Registry.MEMORY_MODULE_TYPE.byNameCodec().parse(p_22022_, p_22015_.getFirst());
               DataResult<? extends Brain.MemoryValue<?>> dataresult1 = dataresult.flatMap((p_147350_) -> {
                  return this.captureRead(p_147350_, p_22022_, (T)p_22015_.getSecond());
               });
               mutableobject1.setValue(mutableobject1.getValue().apply2(Builder::add, dataresult1));
            });
            ImmutableList<Brain.MemoryValue<?>> immutablelist = mutableobject1.getValue().resultOrPartial(Brain.LOGGER::error).map(Builder::build).orElseGet(ImmutableList::of);
            return DataResult.success(new Brain<>(p_21947_, p_21948_, immutablelist, mutableobject::getValue));
         }

         private <T, U> DataResult<Brain.MemoryValue<U>> captureRead(MemoryModuleType<U> p_21997_, DynamicOps<T> p_21998_, T p_21999_) {
            return p_21997_.getCodec().map(DataResult::success).orElseGet(() -> {
               return DataResult.error("No codec for memory: " + p_21997_);
            }).flatMap((p_22011_) -> {
               return p_22011_.parse(p_21998_, p_21999_);
            }).map((p_21992_) -> {
               return new Brain.MemoryValue<>(p_21997_, Optional.of(p_21992_));
            });
         }

         public <T> RecordBuilder<T> encode(Brain<E> p_21985_, DynamicOps<T> p_21986_, RecordBuilder<T> p_21987_) {
            p_21985_.memories().forEach((p_22007_) -> {
               p_22007_.serialize(p_21986_, p_21987_);
            });
            return p_21987_;
         }
      }).fieldOf("memories").codec());
      return mutableobject.getValue();
   }

   public Brain(Collection<? extends MemoryModuleType<?>> p_21855_, Collection<? extends SensorType<? extends Sensor<? super E>>> p_21856_, ImmutableList<Brain.MemoryValue<?>> p_21857_, Supplier<Codec<Brain<E>>> p_21858_) {
      this.codec = p_21858_;

      for(MemoryModuleType<?> memorymoduletype : p_21855_) {
         this.memories.put(memorymoduletype, Optional.empty());
      }

      for(SensorType<? extends Sensor<? super E>> sensortype : p_21856_) {
         this.sensors.put(sensortype, sensortype.create());
      }

      for(Sensor<? super E> sensor : this.sensors.values()) {
         for(MemoryModuleType<?> memorymoduletype1 : sensor.requires()) {
            this.memories.put(memorymoduletype1, Optional.empty());
         }
      }

      for(Brain.MemoryValue<?> memoryvalue : p_21857_) {
         memoryvalue.setMemoryInternal(this);
      }

   }

   public <T> DataResult<T> serializeStart(DynamicOps<T> p_21915_) {
      return this.codec.get().encodeStart(p_21915_, this);
   }

   Stream<Brain.MemoryValue<?>> memories() {
      return this.memories.entrySet().stream().map((p_21929_) -> {
         return Brain.MemoryValue.createUnchecked(p_21929_.getKey(), p_21929_.getValue());
      });
   }

   public boolean hasMemoryValue(MemoryModuleType<?> p_21875_) {
      return this.checkMemory(p_21875_, MemoryStatus.VALUE_PRESENT);
   }

   public <U> void eraseMemory(MemoryModuleType<U> p_21937_) {
      this.setMemory(p_21937_, Optional.empty());
   }

   public <U> void setMemory(MemoryModuleType<U> p_21880_, @Nullable U p_21881_) {
      this.setMemory(p_21880_, Optional.ofNullable(p_21881_));
   }

   public <U> void setMemoryWithExpiry(MemoryModuleType<U> p_21883_, U p_21884_, long p_21885_) {
      this.setMemoryInternal(p_21883_, Optional.of(ExpirableValue.of(p_21884_, p_21885_)));
   }

   public <U> void setMemory(MemoryModuleType<U> p_21887_, Optional<? extends U> p_21888_) {
      this.setMemoryInternal(p_21887_, p_21888_.map(ExpirableValue::of));
   }

   <U> void setMemoryInternal(MemoryModuleType<U> p_21942_, Optional<? extends ExpirableValue<?>> p_21943_) {
      if (this.memories.containsKey(p_21942_)) {
         if (p_21943_.isPresent() && this.isEmptyCollection(p_21943_.get().getValue())) {
            this.eraseMemory(p_21942_);
         } else {
            this.memories.put(p_21942_, p_21943_);
         }
      }

   }

   public <U> Optional<U> getMemory(MemoryModuleType<U> p_21953_) {
      return (Optional<U>)this.memories.get(p_21953_).map(ExpirableValue::getValue);
   }

   public <U> long getTimeUntilExpiry(MemoryModuleType<U> p_147342_) {
      Optional<? extends ExpirableValue<?>> optional = this.memories.get(p_147342_);
      return optional.map(ExpirableValue::getTimeToLive).orElse(0L);
   }

   /** @deprecated */
   @Deprecated
   @VisibleForDebug
   public Map<MemoryModuleType<?>, Optional<? extends ExpirableValue<?>>> getMemories() {
      return this.memories;
   }

   public <U> boolean isMemoryValue(MemoryModuleType<U> p_21939_, U p_21940_) {
      return !this.hasMemoryValue(p_21939_) ? false : this.getMemory(p_21939_).filter((p_21922_) -> {
         return p_21922_.equals(p_21940_);
      }).isPresent();
   }

   public boolean checkMemory(MemoryModuleType<?> p_21877_, MemoryStatus p_21878_) {
      Optional<? extends ExpirableValue<?>> optional = this.memories.get(p_21877_);
      if (optional == null) {
         return false;
      } else {
         return p_21878_ == MemoryStatus.REGISTERED || p_21878_ == MemoryStatus.VALUE_PRESENT && optional.isPresent() || p_21878_ == MemoryStatus.VALUE_ABSENT && !optional.isPresent();
      }
   }

   public Schedule getSchedule() {
      return this.schedule;
   }

   public void setSchedule(Schedule p_21913_) {
      this.schedule = p_21913_;
   }

   public void setCoreActivities(Set<Activity> p_21931_) {
      this.coreActivities = p_21931_;
   }

   /** @deprecated */
   @Deprecated
   @VisibleForDebug
   public Set<Activity> getActiveActivities() {
      return this.activeActivities;
   }

   /** @deprecated */
   @Deprecated
   @VisibleForDebug
   public List<Behavior<? super E>> getRunningBehaviors() {
      List<Behavior<? super E>> list = new ObjectArrayList<>();

      for(Map<Activity, Set<Behavior<? super E>>> map : this.availableBehaviorsByPriority.values()) {
         for(Set<Behavior<? super E>> set : map.values()) {
            for(Behavior<? super E> behavior : set) {
               if (behavior.getStatus() == Behavior.Status.RUNNING) {
                  list.add(behavior);
               }
            }
         }
      }

      return list;
   }

   public void useDefaultActivity() {
      this.setActiveActivity(this.defaultActivity);
   }

   public Optional<Activity> getActiveNonCoreActivity() {
      for(Activity activity : this.activeActivities) {
         if (!this.coreActivities.contains(activity)) {
            return Optional.of(activity);
         }
      }

      return Optional.empty();
   }

   public void setActiveActivityIfPossible(Activity p_21890_) {
      if (this.activityRequirementsAreMet(p_21890_)) {
         this.setActiveActivity(p_21890_);
      } else {
         this.useDefaultActivity();
      }

   }

   private void setActiveActivity(Activity p_21961_) {
      if (!this.isActive(p_21961_)) {
         this.eraseMemoriesForOtherActivitesThan(p_21961_);
         this.activeActivities.clear();
         this.activeActivities.addAll(this.coreActivities);
         this.activeActivities.add(p_21961_);
      }
   }

   private void eraseMemoriesForOtherActivitesThan(Activity p_21967_) {
      for(Activity activity : this.activeActivities) {
         if (activity != p_21967_) {
            Set<MemoryModuleType<?>> set = this.activityMemoriesToEraseWhenStopped.get(activity);
            if (set != null) {
               for(MemoryModuleType<?> memorymoduletype : set) {
                  this.eraseMemory(memorymoduletype);
               }
            }
         }
      }

   }

   public void updateActivityFromSchedule(long p_21863_, long p_21864_) {
      if (p_21864_ - this.lastScheduleUpdate > 20L) {
         this.lastScheduleUpdate = p_21864_;
         Activity activity = this.getSchedule().getActivityAt((int)(p_21863_ % 24000L));
         if (!this.activeActivities.contains(activity)) {
            this.setActiveActivityIfPossible(activity);
         }
      }

   }

   public void setActiveActivityToFirstValid(List<Activity> p_21927_) {
      for(Activity activity : p_21927_) {
         if (this.activityRequirementsAreMet(activity)) {
            this.setActiveActivity(activity);
            break;
         }
      }

   }

   public void setDefaultActivity(Activity p_21945_) {
      this.defaultActivity = p_21945_;
   }

   public void addActivity(Activity p_21892_, int p_21893_, ImmutableList<? extends Behavior<? super E>> p_21894_) {
      this.addActivity(p_21892_, this.createPriorityPairs(p_21893_, p_21894_));
   }

   public void addActivityAndRemoveMemoryWhenStopped(Activity p_21896_, int p_21897_, ImmutableList<? extends Behavior<? super E>> p_21898_, MemoryModuleType<?> p_21899_) {
      Set<Pair<MemoryModuleType<?>, MemoryStatus>> set = ImmutableSet.of(Pair.of(p_21899_, MemoryStatus.VALUE_PRESENT));
      Set<MemoryModuleType<?>> set1 = ImmutableSet.of(p_21899_);
      this.addActivityAndRemoveMemoriesWhenStopped(p_21896_, this.createPriorityPairs(p_21897_, p_21898_), set, set1);
   }

   public void addActivity(Activity p_21901_, ImmutableList<? extends Pair<Integer, ? extends Behavior<? super E>>> p_21902_) {
      this.addActivityAndRemoveMemoriesWhenStopped(p_21901_, p_21902_, ImmutableSet.of(), Sets.newHashSet());
   }

   public void addActivityWithConditions(Activity p_21904_, ImmutableList<? extends Pair<Integer, ? extends Behavior<? super E>>> p_21905_, Set<Pair<MemoryModuleType<?>, MemoryStatus>> p_21906_) {
      this.addActivityAndRemoveMemoriesWhenStopped(p_21904_, p_21905_, p_21906_, Sets.newHashSet());
   }

   public void addActivityAndRemoveMemoriesWhenStopped(Activity p_21908_, ImmutableList<? extends Pair<Integer, ? extends Behavior<? super E>>> p_21909_, Set<Pair<MemoryModuleType<?>, MemoryStatus>> p_21910_, Set<MemoryModuleType<?>> p_21911_) {
      this.activityRequirements.put(p_21908_, p_21910_);
      if (!p_21911_.isEmpty()) {
         this.activityMemoriesToEraseWhenStopped.put(p_21908_, p_21911_);
      }

      for(Pair<Integer, ? extends Behavior<? super E>> pair : p_21909_) {
         this.availableBehaviorsByPriority.computeIfAbsent(pair.getFirst(), (p_21917_) -> {
            return Maps.newHashMap();
         }).computeIfAbsent(p_21908_, (p_21972_) -> {
            return Sets.newLinkedHashSet();
         }).add(pair.getSecond());
      }

   }

   @VisibleForTesting
   public void removeAllBehaviors() {
      this.availableBehaviorsByPriority.clear();
   }

   public boolean isActive(Activity p_21955_) {
      return this.activeActivities.contains(p_21955_);
   }

   public Brain<E> copyWithoutBehaviors() {
      Brain<E> brain = new Brain<>(this.memories.keySet(), this.sensors.keySet(), ImmutableList.of(), this.codec);

      for(Entry<MemoryModuleType<?>, Optional<? extends ExpirableValue<?>>> entry : this.memories.entrySet()) {
         MemoryModuleType<?> memorymoduletype = entry.getKey();
         if (entry.getValue().isPresent()) {
            brain.memories.put(memorymoduletype, entry.getValue());
         }
      }

      return brain;
   }

   public void tick(ServerLevel p_21866_, E p_21867_) {
      this.forgetOutdatedMemories();
      this.tickSensors(p_21866_, p_21867_);
      this.startEachNonRunningBehavior(p_21866_, p_21867_);
      this.tickEachRunningBehavior(p_21866_, p_21867_);
   }

   private void tickSensors(ServerLevel p_21950_, E p_21951_) {
      for(Sensor<? super E> sensor : this.sensors.values()) {
         sensor.tick(p_21950_, p_21951_);
      }

   }

   private void forgetOutdatedMemories() {
      for(Entry<MemoryModuleType<?>, Optional<? extends ExpirableValue<?>>> entry : this.memories.entrySet()) {
         if (entry.getValue().isPresent()) {
            ExpirableValue<?> expirablevalue = entry.getValue().get();
            expirablevalue.tick();
            if (expirablevalue.hasExpired()) {
               this.eraseMemory(entry.getKey());
            }
         }
      }

   }

   public void stopAll(ServerLevel p_21934_, E p_21935_) {
      long i = p_21935_.level.getGameTime();

      for(Behavior<? super E> behavior : this.getRunningBehaviors()) {
         behavior.doStop(p_21934_, p_21935_, i);
      }

   }

   private void startEachNonRunningBehavior(ServerLevel p_21958_, E p_21959_) {
      long i = p_21958_.getGameTime();

      for(Map<Activity, Set<Behavior<? super E>>> map : this.availableBehaviorsByPriority.values()) {
         for(Entry<Activity, Set<Behavior<? super E>>> entry : map.entrySet()) {
            Activity activity = entry.getKey();
            if (this.activeActivities.contains(activity)) {
               for(Behavior<? super E> behavior : entry.getValue()) {
                  if (behavior.getStatus() == Behavior.Status.STOPPED) {
                     behavior.tryStart(p_21958_, p_21959_, i);
                  }
               }
            }
         }
      }

   }

   private void tickEachRunningBehavior(ServerLevel p_21964_, E p_21965_) {
      long i = p_21964_.getGameTime();

      for(Behavior<? super E> behavior : this.getRunningBehaviors()) {
         behavior.tickOrStop(p_21964_, p_21965_, i);
      }

   }

   private boolean activityRequirementsAreMet(Activity p_21970_) {
      if (!this.activityRequirements.containsKey(p_21970_)) {
         return false;
      } else {
         for(Pair<MemoryModuleType<?>, MemoryStatus> pair : this.activityRequirements.get(p_21970_)) {
            MemoryModuleType<?> memorymoduletype = pair.getFirst();
            MemoryStatus memorystatus = pair.getSecond();
            if (!this.checkMemory(memorymoduletype, memorystatus)) {
               return false;
            }
         }

         return true;
      }
   }

   private boolean isEmptyCollection(Object p_21919_) {
      return p_21919_ instanceof Collection && ((Collection)p_21919_).isEmpty();
   }

   ImmutableList<? extends Pair<Integer, ? extends Behavior<? super E>>> createPriorityPairs(int p_21860_, ImmutableList<? extends Behavior<? super E>> p_21861_) {
      int i = p_21860_;
      Builder<Pair<Integer, ? extends Behavior<? super E>>> builder = ImmutableList.builder();

      for(Behavior<? super E> behavior : p_21861_) {
         builder.add(Pair.of(i++, behavior));
      }

      return builder.build();
   }

   static final class MemoryValue<U> {
      private final MemoryModuleType<U> type;
      private final Optional<? extends ExpirableValue<U>> value;

      static <U> Brain.MemoryValue<U> createUnchecked(MemoryModuleType<U> p_22060_, Optional<? extends ExpirableValue<?>> p_22061_) {
         return new Brain.MemoryValue<U>(p_22060_, (Optional<? extends ExpirableValue<U>>)p_22061_);
      }

      MemoryValue(MemoryModuleType<U> p_22033_, Optional<? extends ExpirableValue<U>> p_22034_) {
         this.type = p_22033_;
         this.value = p_22034_;
      }

      void setMemoryInternal(Brain<?> p_22043_) {
         p_22043_.setMemoryInternal(this.type, this.value);
      }

      public <T> void serialize(DynamicOps<T> p_22048_, RecordBuilder<T> p_22049_) {
         this.type.getCodec().ifPresent((p_22053_) -> {
            this.value.ifPresent((p_147355_) -> {
               p_22049_.add(Registry.MEMORY_MODULE_TYPE.byNameCodec().encodeStart(p_22048_, this.type), p_22053_.encodeStart(p_22048_, p_147355_));
            });
         });
      }
   }

   public static final class Provider<E extends LivingEntity> {
      private final Collection<? extends MemoryModuleType<?>> memoryTypes;
      private final Collection<? extends SensorType<? extends Sensor<? super E>>> sensorTypes;
      private final Codec<Brain<E>> codec;

      Provider(Collection<? extends MemoryModuleType<?>> p_22066_, Collection<? extends SensorType<? extends Sensor<? super E>>> p_22067_) {
         this.memoryTypes = p_22066_;
         this.sensorTypes = p_22067_;
         this.codec = Brain.codec(p_22066_, p_22067_);
      }

      public Brain<E> makeBrain(Dynamic<?> p_22074_) {
         return this.codec.parse(p_22074_).resultOrPartial(Brain.LOGGER::error).orElseGet(() -> {
            return new Brain<>(this.memoryTypes, this.sensorTypes, ImmutableList.of(), () -> {
               return this.codec;
            });
         });
      }
   }
}