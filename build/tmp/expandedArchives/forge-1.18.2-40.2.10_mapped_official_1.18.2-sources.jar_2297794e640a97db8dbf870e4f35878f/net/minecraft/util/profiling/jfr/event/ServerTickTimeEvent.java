package net.minecraft.util.profiling.jfr.event;

import jdk.jfr.Category;
import jdk.jfr.Event;
import jdk.jfr.EventType;
import jdk.jfr.Label;
import jdk.jfr.Name;
import jdk.jfr.Period;
import jdk.jfr.StackTrace;
import jdk.jfr.Timespan;
import net.minecraft.obfuscate.DontObfuscate;

@Name("minecraft.ServerTickTime")
@Label("Server Tick Time")
@Category({"Minecraft", "Ticking"})
@StackTrace(false)
@Period("1 s")
@DontObfuscate
public class ServerTickTimeEvent extends Event {
   public static final String EVENT_NAME = "minecraft.ServerTickTime";
   public static final EventType TYPE = EventType.getEventType(ServerTickTimeEvent.class);
   @Name("averageTickDuration")
   @Label("Average Server Tick Duration")
   @Timespan
   public final long averageTickDurationNanos;

   public ServerTickTimeEvent(float p_195602_) {
      this.averageTickDurationNanos = (long)(1000000.0F * p_195602_);
   }

   public static class Fields {
      public static final String AVERAGE_TICK_DURATION = "averageTickDuration";

      private Fields() {
      }
   }
}