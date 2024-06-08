package net.minecraft.util.profiling.jfr;

import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.SocketAddress;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import jdk.jfr.Configuration;
import jdk.jfr.Event;
import jdk.jfr.FlightRecorder;
import jdk.jfr.FlightRecorderListener;
import jdk.jfr.Recording;
import jdk.jfr.RecordingState;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.profiling.jfr.callback.ProfiledDuration;
import net.minecraft.util.profiling.jfr.event.ChunkGenerationEvent;
import net.minecraft.util.profiling.jfr.event.NetworkSummaryEvent;
import net.minecraft.util.profiling.jfr.event.PacketReceivedEvent;
import net.minecraft.util.profiling.jfr.event.PacketSentEvent;
import net.minecraft.util.profiling.jfr.event.ServerTickTimeEvent;
import net.minecraft.util.profiling.jfr.event.WorldLoadFinishedEvent;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

public class JfrProfiler implements JvmProfiler {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final String ROOT_CATEGORY = "Minecraft";
   public static final String WORLD_GEN_CATEGORY = "World Generation";
   public static final String TICK_CATEGORY = "Ticking";
   public static final String NETWORK_CATEGORY = "Network";
   private static final List<Class<? extends Event>> CUSTOM_EVENTS = List.of(ChunkGenerationEvent.class, PacketReceivedEvent.class, PacketSentEvent.class, NetworkSummaryEvent.class, ServerTickTimeEvent.class, WorldLoadFinishedEvent.class);
   private static final String FLIGHT_RECORDER_CONFIG = "/flightrecorder-config.jfc";
   private static final DateTimeFormatter DATE_TIME_FORMATTER = (new DateTimeFormatterBuilder()).appendPattern("yyyy-MM-dd-HHmmss").toFormatter().withZone(ZoneId.systemDefault());
   private static final JfrProfiler INSTANCE = new JfrProfiler();
   @Nullable
   Recording recording;
   private float currentAverageTickTime;
   private final Map<String, NetworkSummaryEvent.SumAggregation> networkTrafficByAddress = new ConcurrentHashMap<>();

   private JfrProfiler() {
      CUSTOM_EVENTS.forEach(FlightRecorder::register);
      FlightRecorder.addPeriodicEvent(ServerTickTimeEvent.class, () -> {
         (new ServerTickTimeEvent(this.currentAverageTickTime)).commit();
      });
      FlightRecorder.addPeriodicEvent(NetworkSummaryEvent.class, () -> {
         Iterator<NetworkSummaryEvent.SumAggregation> iterator = this.networkTrafficByAddress.values().iterator();

         while(iterator.hasNext()) {
            iterator.next().commitEvent();
            iterator.remove();
         }

      });
   }

   public static JfrProfiler getInstance() {
      return INSTANCE;
   }

   public boolean start(Environment p_185307_) {
      URL url = JfrProfiler.class.getResource("/flightrecorder-config.jfc");
      if (url == null) {
         LOGGER.warn("Could not find default flight recorder config at {}", (Object)"/flightrecorder-config.jfc");
         return false;
      } else {
         try {
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(url.openStream()));

            boolean flag;
            try {
               flag = this.start(bufferedreader, p_185307_);
            } catch (Throwable throwable1) {
               try {
                  bufferedreader.close();
               } catch (Throwable throwable) {
                  throwable1.addSuppressed(throwable);
               }

               throw throwable1;
            }

            bufferedreader.close();
            return flag;
         } catch (IOException ioexception) {
            LOGGER.warn("Failed to start flight recorder using configuration at {}", url, ioexception);
            return false;
         }
      }
   }

   public Path stop() {
      if (this.recording == null) {
         throw new IllegalStateException("Not currently profiling");
      } else {
         this.networkTrafficByAddress.clear();
         Path path = this.recording.getDestination();
         this.recording.stop();
         return path;
      }
   }

   public boolean isRunning() {
      return this.recording != null;
   }

   public boolean isAvailable() {
      return FlightRecorder.isAvailable();
   }

   private boolean start(Reader p_185317_, Environment p_185318_) {
      if (this.isRunning()) {
         LOGGER.warn("Profiling already in progress");
         return false;
      } else {
         try {
            Configuration configuration = Configuration.create(p_185317_);
            String s = DATE_TIME_FORMATTER.format(Instant.now());
            this.recording = Util.make(new Recording(configuration), (p_185311_) -> {
               CUSTOM_EVENTS.forEach(p_185311_::enable);
               p_185311_.setDumpOnExit(true);
               p_185311_.setToDisk(true);
               p_185311_.setName("%s-%s-%s".formatted(p_185318_.getDescription(), SharedConstants.getCurrentVersion().getName(), s));
            });
            Path path = Paths.get("debug/%s-%s.jfr".formatted(p_185318_.getDescription(), s));
            if (!Files.exists(path.getParent())) {
               Files.createDirectories(path.getParent());
            }

            this.recording.setDestination(path);
            this.recording.start();
            this.setupSummaryListener();
         } catch (ParseException | IOException ioexception) {
            LOGGER.warn("Failed to start jfr profiling", (Throwable)ioexception);
            return false;
         }

         LOGGER.info("Started flight recorder profiling id({}):name({}) - will dump to {} on exit or stop command", this.recording.getId(), this.recording.getName(), this.recording.getDestination());
         return true;
      }
   }

   private void setupSummaryListener() {
      FlightRecorder.addListener(new FlightRecorderListener() {
         final SummaryReporter summaryReporter = new SummaryReporter(() -> {
            JfrProfiler.this.recording = null;
         });

         public void recordingStateChanged(Recording p_185339_) {
            if (p_185339_ == JfrProfiler.this.recording && p_185339_.getState() == RecordingState.STOPPED) {
               this.summaryReporter.recordingStopped(p_185339_.getDestination());
               FlightRecorder.removeListener(this);
            }
         }
      });
   }

   public void onServerTick(float p_185300_) {
      if (ServerTickTimeEvent.TYPE.isEnabled()) {
         this.currentAverageTickTime = p_185300_;
      }

   }

   public void onPacketReceived(int p_185302_, int p_185303_, SocketAddress p_185304_, int p_185305_) {
      if (PacketReceivedEvent.TYPE.isEnabled()) {
         (new PacketReceivedEvent(p_185302_, p_185303_, p_185304_, p_185305_)).commit();
      }

      if (NetworkSummaryEvent.TYPE.isEnabled()) {
         this.networkStatFor(p_185304_).trackReceivedPacket(p_185305_);
      }

   }

   public void onPacketSent(int p_185323_, int p_185324_, SocketAddress p_185325_, int p_185326_) {
      if (PacketSentEvent.TYPE.isEnabled()) {
         (new PacketSentEvent(p_185323_, p_185324_, p_185325_, p_185326_)).commit();
      }

      if (NetworkSummaryEvent.TYPE.isEnabled()) {
         this.networkStatFor(p_185325_).trackSentPacket(p_185326_);
      }

   }

   private NetworkSummaryEvent.SumAggregation networkStatFor(SocketAddress p_185320_) {
      return this.networkTrafficByAddress.computeIfAbsent(p_185320_.toString(), NetworkSummaryEvent.SumAggregation::new);
   }

   @Nullable
   public ProfiledDuration onWorldLoadedStarted() {
      if (!WorldLoadFinishedEvent.TYPE.isEnabled()) {
         return null;
      } else {
         WorldLoadFinishedEvent worldloadfinishedevent = new WorldLoadFinishedEvent();
         worldloadfinishedevent.begin();
         return worldloadfinishedevent::commit;
      }
   }

   @Nullable
   public ProfiledDuration onChunkGenerate(ChunkPos p_185313_, ResourceKey<Level> p_185314_, String p_185315_) {
      if (!ChunkGenerationEvent.TYPE.isEnabled()) {
         return null;
      } else {
         ChunkGenerationEvent chunkgenerationevent = new ChunkGenerationEvent(p_185313_, p_185314_, p_185315_);
         chunkgenerationevent.begin();
         return chunkgenerationevent::commit;
      }
   }
}