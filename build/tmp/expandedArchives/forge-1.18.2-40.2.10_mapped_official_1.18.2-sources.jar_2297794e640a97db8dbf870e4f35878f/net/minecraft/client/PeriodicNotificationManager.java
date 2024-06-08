package net.minecraft.client;

import com.google.common.collect.ImmutableMap;
import com.google.common.math.LongMath;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2BooleanFunction;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class PeriodicNotificationManager extends SimplePreparableReloadListener<Map<String, List<PeriodicNotificationManager.Notification>>> implements AutoCloseable {
   private static final Codec<Map<String, List<PeriodicNotificationManager.Notification>>> CODEC = Codec.unboundedMap(Codec.STRING, RecordCodecBuilder.<PeriodicNotificationManager.Notification>create((p_205303_) -> {
      return p_205303_.group(Codec.LONG.optionalFieldOf("delay", Long.valueOf(0L)).forGetter(PeriodicNotificationManager.Notification::delay), Codec.LONG.fieldOf("period").forGetter(PeriodicNotificationManager.Notification::period), Codec.STRING.fieldOf("title").forGetter(PeriodicNotificationManager.Notification::title), Codec.STRING.fieldOf("message").forGetter(PeriodicNotificationManager.Notification::message)).apply(p_205303_, PeriodicNotificationManager.Notification::new);
   }).listOf());
   private static final Logger LOGGER = LogUtils.getLogger();
   private final ResourceLocation notifications;
   private final Object2BooleanFunction<String> selector;
   @Nullable
   private java.util.Timer timer;
   @Nullable
   private PeriodicNotificationManager.NotificationTask notificationTask;

   public PeriodicNotificationManager(ResourceLocation p_205293_, Object2BooleanFunction<String> p_205294_) {
      this.notifications = p_205293_;
      this.selector = p_205294_;
   }

   protected Map<String, List<PeriodicNotificationManager.Notification>> prepare(ResourceManager p_205300_, ProfilerFiller p_205301_) {
      try {
         Resource resource = p_205300_.getResource(this.notifications);

         Map map;
         try {
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));

            try {
               map = CODEC.parse(JsonOps.INSTANCE, JsonParser.parseReader(bufferedreader)).result().orElseThrow();
            } catch (Throwable throwable2) {
               try {
                  bufferedreader.close();
               } catch (Throwable throwable1) {
                  throwable2.addSuppressed(throwable1);
               }

               throw throwable2;
            }

            bufferedreader.close();
         } catch (Throwable throwable3) {
            if (resource != null) {
               try {
                  resource.close();
               } catch (Throwable throwable) {
                  throwable3.addSuppressed(throwable);
               }
            }

            throw throwable3;
         }

         if (resource != null) {
            resource.close();
         }

         return map;
      } catch (Exception exception) {
         LOGGER.warn("Failed to load {}", this.notifications, exception);
         return ImmutableMap.of();
      }
   }

   protected void apply(Map<String, List<PeriodicNotificationManager.Notification>> p_205318_, ResourceManager p_205319_, ProfilerFiller p_205320_) {
      List<PeriodicNotificationManager.Notification> list = p_205318_.entrySet().stream().filter((p_205316_) -> {
         return this.selector.apply(p_205316_.getKey());
      }).map(Entry::getValue).flatMap(Collection::stream).collect(Collectors.toList());
      if (list.isEmpty()) {
         this.stopTimer();
      } else if (list.stream().anyMatch((p_205326_) -> {
         return p_205326_.period == 0L;
      })) {
         Util.logAndPauseIfInIde("A periodic notification in " + this.notifications + " has a period of zero minutes");
         this.stopTimer();
      } else {
         long i = this.calculateInitialDelay(list);
         long j = this.calculateOptimalPeriod(list, i);
         if (this.timer == null) {
            this.timer = new java.util.Timer();
         }

         if (this.notificationTask == null) {
            this.notificationTask = new PeriodicNotificationManager.NotificationTask(list, i, j);
         } else {
            this.notificationTask = this.notificationTask.reset(list, j);
         }

         this.timer.scheduleAtFixedRate(this.notificationTask, TimeUnit.MINUTES.toMillis(i), TimeUnit.MINUTES.toMillis(j));
      }
   }

   public void close() {
      this.stopTimer();
   }

   private void stopTimer() {
      if (this.timer != null) {
         this.timer.cancel();
      }

   }

   private long calculateOptimalPeriod(List<PeriodicNotificationManager.Notification> p_205313_, long p_205314_) {
      return p_205313_.stream().mapToLong((p_205298_) -> {
         long i = p_205298_.delay - p_205314_;
         return LongMath.gcd(i, p_205298_.period);
      }).reduce(LongMath::gcd).orElseThrow(() -> {
         return new IllegalStateException("Empty notifications from: " + this.notifications);
      });
   }

   private long calculateInitialDelay(List<PeriodicNotificationManager.Notification> p_205311_) {
      return p_205311_.stream().mapToLong((p_205305_) -> {
         return p_205305_.delay;
      }).min().orElse(0L);
   }

   @OnlyIn(Dist.CLIENT)
   public static record Notification(long delay, long period, String title, String message) {
      public Notification(long delay, long period, String title, String message) {
         this.delay = delay != 0L ? delay : period;
         this.period = period;
         this.title = title;
         this.message = message;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class NotificationTask extends TimerTask {
      private final Minecraft minecraft = Minecraft.getInstance();
      private final List<PeriodicNotificationManager.Notification> notifications;
      private final long period;
      private final AtomicLong elapsed;

      public NotificationTask(List<PeriodicNotificationManager.Notification> p_205350_, long p_205351_, long p_205352_) {
         this.notifications = p_205350_;
         this.period = p_205352_;
         this.elapsed = new AtomicLong(p_205351_);
      }

      public PeriodicNotificationManager.NotificationTask reset(List<PeriodicNotificationManager.Notification> p_205357_, long p_205358_) {
         this.cancel();
         return new PeriodicNotificationManager.NotificationTask(p_205357_, this.elapsed.get(), p_205358_);
      }

      public void run() {
         long i = this.elapsed.getAndAdd(this.period);
         long j = this.elapsed.get();

         for(PeriodicNotificationManager.Notification periodicnotificationmanager$notification : this.notifications) {
            if (i >= periodicnotificationmanager$notification.delay) {
               long k = i / periodicnotificationmanager$notification.period;
               long l = j / periodicnotificationmanager$notification.period;
               if (k != l) {
                  this.minecraft.execute(() -> {
                     SystemToast.add(Minecraft.getInstance().getToasts(), SystemToast.SystemToastIds.PERIODIC_NOTIFICATION, new TranslatableComponent(periodicnotificationmanager$notification.title, k), new TranslatableComponent(periodicnotificationmanager$notification.message, k));
                  });
                  return;
               }
            }
         }

      }
   }
}