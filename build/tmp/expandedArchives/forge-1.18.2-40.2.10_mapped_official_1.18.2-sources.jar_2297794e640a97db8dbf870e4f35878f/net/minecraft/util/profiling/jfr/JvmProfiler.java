package net.minecraft.util.profiling.jfr;

import com.mojang.logging.LogUtils;
import java.net.SocketAddress;
import java.nio.file.Path;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.profiling.jfr.callback.ProfiledDuration;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

public interface JvmProfiler {
   JvmProfiler INSTANCE = (JvmProfiler)(Runtime.class.getModule().getLayer().findModule("jdk.jfr").isPresent() ? JfrProfiler.getInstance() : new JvmProfiler.NoOpProfiler());

   boolean start(Environment p_185347_);

   Path stop();

   boolean isRunning();

   boolean isAvailable();

   void onServerTick(float p_185342_);

   void onPacketReceived(int p_185343_, int p_185344_, SocketAddress p_185345_, int p_185346_);

   void onPacketSent(int p_185351_, int p_185352_, SocketAddress p_185353_, int p_185354_);

   @Nullable
   ProfiledDuration onWorldLoadedStarted();

   @Nullable
   ProfiledDuration onChunkGenerate(ChunkPos p_185348_, ResourceKey<Level> p_185349_, String p_185350_);

   public static class NoOpProfiler implements JvmProfiler {
      private static final Logger LOGGER = LogUtils.getLogger();
      static final ProfiledDuration noOpCommit = () -> {
      };

      public boolean start(Environment p_185368_) {
         LOGGER.warn("Attempted to start Flight Recorder, but it's not supported on this JVM");
         return false;
      }

      public Path stop() {
         throw new IllegalStateException("Attempted to stop Flight Recorder, but it's not supported on this JVM");
      }

      public boolean isRunning() {
         return false;
      }

      public boolean isAvailable() {
         return false;
      }

      public void onPacketReceived(int p_185363_, int p_185364_, SocketAddress p_185365_, int p_185366_) {
      }

      public void onPacketSent(int p_185375_, int p_185376_, SocketAddress p_185377_, int p_185378_) {
      }

      public void onServerTick(float p_185361_) {
      }

      public ProfiledDuration onWorldLoadedStarted() {
         return noOpCommit;
      }

      @Nullable
      public ProfiledDuration onChunkGenerate(ChunkPos p_185370_, ResourceKey<Level> p_185371_, String p_185372_) {
         return null;
      }
   }
}