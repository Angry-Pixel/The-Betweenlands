package com.mojang.blaze3d.audio;

import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import java.nio.IntBuffer;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.OptionalLong;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALC11;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.openal.ALUtil;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class Library {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final int NO_DEVICE = 0;
   private static final int DEFAULT_CHANNEL_COUNT = 30;
   private long currentDevice;
   private long context;
   private boolean supportsDisconnections;
   @Nullable
   private String defaultDeviceName;
   private static final Library.ChannelPool EMPTY = new Library.ChannelPool() {
      @Nullable
      public Channel acquire() {
         return null;
      }

      public boolean release(Channel p_83708_) {
         return false;
      }

      public void cleanup() {
      }

      public int getMaxCount() {
         return 0;
      }

      public int getUsedCount() {
         return 0;
      }
   };
   private Library.ChannelPool staticChannels = EMPTY;
   private Library.ChannelPool streamingChannels = EMPTY;
   private final Listener listener = new Listener();

   public Library() {
      this.defaultDeviceName = getDefaultDeviceName();
   }

   public void init(@Nullable String p_193470_) {
      this.currentDevice = openDeviceOrFallback(p_193470_);
      this.supportsDisconnections = ALC10.alcIsExtensionPresent(this.currentDevice, "ALC_EXT_disconnect");
      ALCCapabilities alccapabilities = ALC.createCapabilities(this.currentDevice);
      if (OpenAlUtil.checkALCError(this.currentDevice, "Get capabilities")) {
         throw new IllegalStateException("Failed to get OpenAL capabilities");
      } else if (!alccapabilities.OpenALC11) {
         throw new IllegalStateException("OpenAL 1.1 not supported");
      } else {
         this.context = ALC10.alcCreateContext(this.currentDevice, (IntBuffer)null);
         ALC10.alcMakeContextCurrent(this.context);
         int i = this.getChannelCount();
         int j = Mth.clamp((int)Mth.sqrt((float)i), 2, 8);
         int k = Mth.clamp(i - j, 8, 255);
         this.staticChannels = new Library.CountingChannelPool(k);
         this.streamingChannels = new Library.CountingChannelPool(j);
         ALCapabilities alcapabilities = AL.createCapabilities(alccapabilities);
         OpenAlUtil.checkALError("Initialization");
         if (!alcapabilities.AL_EXT_source_distance_model) {
            throw new IllegalStateException("AL_EXT_source_distance_model is not supported");
         } else {
            AL10.alEnable(512);
            if (!alcapabilities.AL_EXT_LINEAR_DISTANCE) {
               throw new IllegalStateException("AL_EXT_LINEAR_DISTANCE is not supported");
            } else {
               OpenAlUtil.checkALError("Enable per-source distance models");
               LOGGER.info("OpenAL initialized on device {}", (Object)this.getCurrentDeviceName());
            }
         }
      }
   }

   private int getChannelCount() {
      MemoryStack memorystack = MemoryStack.stackPush();

      int i1;
      label58: {
         try {
            int i = ALC10.alcGetInteger(this.currentDevice, 4098);
            if (OpenAlUtil.checkALCError(this.currentDevice, "Get attributes size")) {
               throw new IllegalStateException("Failed to get OpenAL attributes");
            }

            IntBuffer intbuffer = memorystack.mallocInt(i);
            ALC10.alcGetIntegerv(this.currentDevice, 4099, intbuffer);
            if (OpenAlUtil.checkALCError(this.currentDevice, "Get attributes")) {
               throw new IllegalStateException("Failed to get OpenAL attributes");
            }

            int j = 0;

            while(j < i) {
               int k = intbuffer.get(j++);
               if (k == 0) {
                  break;
               }

               int l = intbuffer.get(j++);
               if (k == 4112) {
                  i1 = l;
                  break label58;
               }
            }
         } catch (Throwable throwable1) {
            if (memorystack != null) {
               try {
                  memorystack.close();
               } catch (Throwable throwable) {
                  throwable1.addSuppressed(throwable);
               }
            }

            throw throwable1;
         }

         if (memorystack != null) {
            memorystack.close();
         }

         return 30;
      }

      if (memorystack != null) {
         memorystack.close();
      }

      return i1;
   }

   @Nullable
   public static String getDefaultDeviceName() {
      if (!ALC10.alcIsExtensionPresent(0L, "ALC_ENUMERATE_ALL_EXT")) {
         return null;
      } else {
         ALUtil.getStringList(0L, 4115);
         return ALC10.alcGetString(0L, 4114);
      }
   }

   public String getCurrentDeviceName() {
      String s = ALC10.alcGetString(this.currentDevice, 4115);
      if (s == null) {
         s = ALC10.alcGetString(this.currentDevice, 4101);
      }

      if (s == null) {
         s = "Unknown";
      }

      return s;
   }

   public synchronized boolean hasDefaultDeviceChanged() {
      String s = getDefaultDeviceName();
      if (Objects.equals(this.defaultDeviceName, s)) {
         return false;
      } else {
         this.defaultDeviceName = s;
         return true;
      }
   }

   private static long openDeviceOrFallback(@Nullable String p_193473_) {
      OptionalLong optionallong = OptionalLong.empty();
      if (p_193473_ != null) {
         optionallong = tryOpenDevice(p_193473_);
      }

      if (optionallong.isEmpty()) {
         optionallong = tryOpenDevice(getDefaultDeviceName());
      }

      if (optionallong.isEmpty()) {
         optionallong = tryOpenDevice((String)null);
      }

      if (optionallong.isEmpty()) {
         throw new IllegalStateException("Failed to open OpenAL device");
      } else {
         return optionallong.getAsLong();
      }
   }

   private static OptionalLong tryOpenDevice(@Nullable String p_193476_) {
      long i = ALC10.alcOpenDevice(p_193476_);
      return i != 0L && !OpenAlUtil.checkALCError(i, "Open device") ? OptionalLong.of(i) : OptionalLong.empty();
   }

   public void cleanup() {
      this.staticChannels.cleanup();
      this.streamingChannels.cleanup();
      ALC10.alcDestroyContext(this.context);
      if (this.currentDevice != 0L) {
         ALC10.alcCloseDevice(this.currentDevice);
      }

   }

   public Listener getListener() {
      return this.listener;
   }

   @Nullable
   public Channel acquireChannel(Library.Pool p_83698_) {
      return (p_83698_ == Library.Pool.STREAMING ? this.streamingChannels : this.staticChannels).acquire();
   }

   public void releaseChannel(Channel p_83696_) {
      if (!this.staticChannels.release(p_83696_) && !this.streamingChannels.release(p_83696_)) {
         throw new IllegalStateException("Tried to release unknown channel");
      }
   }

   public String getDebugString() {
      return String.format("Sounds: %d/%d + %d/%d", this.staticChannels.getUsedCount(), this.staticChannels.getMaxCount(), this.streamingChannels.getUsedCount(), this.streamingChannels.getMaxCount());
   }

   public List<String> getAvailableSoundDevices() {
      List<String> list = ALUtil.getStringList(0L, 4115);
      return list == null ? Collections.emptyList() : list;
   }

   public boolean isCurrentDeviceDisconnected() {
      return this.supportsDisconnections && ALC11.alcGetInteger(this.currentDevice, 787) == 0;
   }

   @OnlyIn(Dist.CLIENT)
   interface ChannelPool {
      @Nullable
      Channel acquire();

      boolean release(Channel p_83712_);

      void cleanup();

      int getMaxCount();

      int getUsedCount();
   }

   @OnlyIn(Dist.CLIENT)
   static class CountingChannelPool implements Library.ChannelPool {
      private final int limit;
      private final Set<Channel> activeChannels = Sets.newIdentityHashSet();

      public CountingChannelPool(int p_83716_) {
         this.limit = p_83716_;
      }

      @Nullable
      public Channel acquire() {
         if (this.activeChannels.size() >= this.limit) {
            if (SharedConstants.IS_RUNNING_IN_IDE) {
               Library.LOGGER.warn("Maximum sound pool size {} reached", (int)this.limit);
            }

            return null;
         } else {
            Channel channel = Channel.create();
            if (channel != null) {
               this.activeChannels.add(channel);
            }

            return channel;
         }
      }

      public boolean release(Channel p_83719_) {
         if (!this.activeChannels.remove(p_83719_)) {
            return false;
         } else {
            p_83719_.destroy();
            return true;
         }
      }

      public void cleanup() {
         this.activeChannels.forEach(Channel::destroy);
         this.activeChannels.clear();
      }

      public int getMaxCount() {
         return this.limit;
      }

      public int getUsedCount() {
         return this.activeChannels.size();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Pool {
      STATIC,
      STREAMING;
   }
}