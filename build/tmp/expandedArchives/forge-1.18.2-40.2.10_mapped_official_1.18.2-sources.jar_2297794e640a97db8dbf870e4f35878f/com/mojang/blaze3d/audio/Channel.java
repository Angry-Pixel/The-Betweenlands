package com.mojang.blaze3d.audio;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Nullable;
import javax.sound.sampled.AudioFormat;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.openal.AL10;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class Channel {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int QUEUED_BUFFER_COUNT = 4;
   public static final int BUFFER_DURATION_SECONDS = 1;
   private final int source;
   private final AtomicBoolean initialized = new AtomicBoolean(true);
   private int streamingBufferSize = 16384;
   @Nullable
   private AudioStream stream;

   @Nullable
   static Channel create() {
      int[] aint = new int[1];
      AL10.alGenSources(aint);
      return OpenAlUtil.checkALError("Allocate new source") ? null : new Channel(aint[0]);
   }

   private Channel(int p_83648_) {
      this.source = p_83648_;
   }

   public void destroy() {
      if (this.initialized.compareAndSet(true, false)) {
         AL10.alSourceStop(this.source);
         OpenAlUtil.checkALError("Stop");
         if (this.stream != null) {
            try {
               this.stream.close();
            } catch (IOException ioexception) {
               LOGGER.error("Failed to close audio stream", (Throwable)ioexception);
            }

            this.removeProcessedBuffers();
            this.stream = null;
         }

         AL10.alDeleteSources(new int[]{this.source});
         OpenAlUtil.checkALError("Cleanup");
      }

   }

   public void play() {
      AL10.alSourcePlay(this.source);
   }

   private int getState() {
      return !this.initialized.get() ? 4116 : AL10.alGetSourcei(this.source, 4112);
   }

   public void pause() {
      if (this.getState() == 4114) {
         AL10.alSourcePause(this.source);
      }

   }

   public void unpause() {
      if (this.getState() == 4115) {
         AL10.alSourcePlay(this.source);
      }

   }

   public void stop() {
      if (this.initialized.get()) {
         AL10.alSourceStop(this.source);
         OpenAlUtil.checkALError("Stop");
      }

   }

   public boolean playing() {
      return this.getState() == 4114;
   }

   public boolean stopped() {
      return this.getState() == 4116;
   }

   public void setSelfPosition(Vec3 p_83655_) {
      AL10.alSourcefv(this.source, 4100, new float[]{(float)p_83655_.x, (float)p_83655_.y, (float)p_83655_.z});
   }

   public void setPitch(float p_83651_) {
      AL10.alSourcef(this.source, 4099, p_83651_);
   }

   public void setLooping(boolean p_83664_) {
      AL10.alSourcei(this.source, 4103, p_83664_ ? 1 : 0);
   }

   public void setVolume(float p_83667_) {
      AL10.alSourcef(this.source, 4106, p_83667_);
   }

   public void disableAttenuation() {
      AL10.alSourcei(this.source, 53248, 0);
   }

   public void linearAttenuation(float p_83674_) {
      AL10.alSourcei(this.source, 53248, 53251);
      AL10.alSourcef(this.source, 4131, p_83674_);
      AL10.alSourcef(this.source, 4129, 1.0F);
      AL10.alSourcef(this.source, 4128, 0.0F);
   }

   public void setRelative(boolean p_83671_) {
      AL10.alSourcei(this.source, 514, p_83671_ ? 1 : 0);
   }

   public void attachStaticBuffer(SoundBuffer p_83657_) {
      p_83657_.getAlBuffer().ifPresent((p_83676_) -> {
         AL10.alSourcei(this.source, 4105, p_83676_);
      });
   }

   public void attachBufferStream(AudioStream p_83659_) {
      this.stream = p_83659_;
      AudioFormat audioformat = p_83659_.getFormat();
      this.streamingBufferSize = calculateBufferSize(audioformat, 1);
      this.pumpBuffers(4);
   }

   private static int calculateBufferSize(AudioFormat p_83661_, int p_83662_) {
      return (int)((float)(p_83662_ * p_83661_.getSampleSizeInBits()) / 8.0F * (float)p_83661_.getChannels() * p_83661_.getSampleRate());
   }

   private void pumpBuffers(int p_83653_) {
      if (this.stream != null) {
         try {
            for(int i = 0; i < p_83653_; ++i) {
               ByteBuffer bytebuffer = this.stream.read(this.streamingBufferSize);
               if (bytebuffer != null) {
                  (new SoundBuffer(bytebuffer, this.stream.getFormat())).releaseAlBuffer().ifPresent((p_83669_) -> {
                     AL10.alSourceQueueBuffers(this.source, new int[]{p_83669_});
                  });
               }
            }
         } catch (IOException ioexception) {
            LOGGER.error("Failed to read from audio stream", (Throwable)ioexception);
         }
      }

   }

   public void updateStream() {
      if (this.stream != null) {
         int i = this.removeProcessedBuffers();
         this.pumpBuffers(i);
      }

   }

   private int removeProcessedBuffers() {
      int i = AL10.alGetSourcei(this.source, 4118);
      if (i > 0) {
         int[] aint = new int[i];
         AL10.alSourceUnqueueBuffers(this.source, aint);
         OpenAlUtil.checkALError("Unqueue buffers");
         AL10.alDeleteBuffers(aint);
         OpenAlUtil.checkALError("Remove processed buffers");
      }

      return i;
   }
}