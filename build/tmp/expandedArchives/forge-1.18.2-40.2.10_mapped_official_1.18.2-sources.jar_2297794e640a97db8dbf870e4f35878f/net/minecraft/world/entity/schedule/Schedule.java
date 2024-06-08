package net.minecraft.world.entity.schedule;

import com.google.common.collect.Maps;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import net.minecraft.core.Registry;

public class Schedule extends net.minecraftforge.registries.ForgeRegistryEntry<Schedule> {
   public static final int WORK_START_TIME = 2000;
   public static final int TOTAL_WORK_TIME = 7000;
   public static final Schedule EMPTY = register("empty").changeActivityAt(0, Activity.IDLE).build();
   public static final Schedule SIMPLE = register("simple").changeActivityAt(5000, Activity.WORK).changeActivityAt(11000, Activity.REST).build();
   public static final Schedule VILLAGER_BABY = register("villager_baby").changeActivityAt(10, Activity.IDLE).changeActivityAt(3000, Activity.PLAY).changeActivityAt(6000, Activity.IDLE).changeActivityAt(10000, Activity.PLAY).changeActivityAt(12000, Activity.REST).build();
   public static final Schedule VILLAGER_DEFAULT = register("villager_default").changeActivityAt(10, Activity.IDLE).changeActivityAt(2000, Activity.WORK).changeActivityAt(9000, Activity.MEET).changeActivityAt(11000, Activity.IDLE).changeActivityAt(12000, Activity.REST).build();
   private final Map<Activity, Timeline> timelines = Maps.newHashMap();

   protected static ScheduleBuilder register(String p_38030_) {
      Schedule schedule = Registry.register(Registry.SCHEDULE, p_38030_, new Schedule());
      return new ScheduleBuilder(schedule);
   }

   protected void ensureTimelineExistsFor(Activity p_38025_) {
      if (!this.timelines.containsKey(p_38025_)) {
         this.timelines.put(p_38025_, new Timeline());
      }

   }

   protected Timeline getTimelineFor(Activity p_38032_) {
      return this.timelines.get(p_38032_);
   }

   protected List<Timeline> getAllTimelinesExceptFor(Activity p_38034_) {
      return this.timelines.entrySet().stream().filter((p_38028_) -> {
         return p_38028_.getKey() != p_38034_;
      }).map(Entry::getValue).collect(Collectors.toList());
   }

   public Activity getActivityAt(int p_38020_) {
      return this.timelines.entrySet().stream().max(Comparator.comparingDouble((p_38023_) -> {
         return (double)p_38023_.getValue().getValueAt(p_38020_);
      })).map(Entry::getKey).orElse(Activity.IDLE);
   }
}
