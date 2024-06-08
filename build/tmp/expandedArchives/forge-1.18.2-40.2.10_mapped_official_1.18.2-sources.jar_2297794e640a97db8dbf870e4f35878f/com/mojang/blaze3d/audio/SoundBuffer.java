package com.mojang.blaze3d.audio;

import java.nio.ByteBuffer;
import java.util.OptionalInt;
import javax.annotation.Nullable;
import javax.sound.sampled.AudioFormat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.openal.AL10;

@OnlyIn(Dist.CLIENT)
public class SoundBuffer {
   @Nullable
   private ByteBuffer data;
   private final AudioFormat format;
   private boolean hasAlBuffer;
   private int alBuffer;

   public SoundBuffer(ByteBuffer p_83798_, AudioFormat p_83799_) {
      this.data = p_83798_;
      this.format = p_83799_;
   }

   OptionalInt getAlBuffer() {
      if (!this.hasAlBuffer) {
         if (this.data == null) {
            return OptionalInt.empty();
         }

         int i = OpenAlUtil.audioFormatToOpenAl(this.format);
         int[] aint = new int[1];
         AL10.alGenBuffers(aint);
         if (OpenAlUtil.checkALError("Creating buffer")) {
            return OptionalInt.empty();
         }

         AL10.alBufferData(aint[0], i, this.data, (int)this.format.getSampleRate());
         if (OpenAlUtil.checkALError("Assigning buffer data")) {
            return OptionalInt.empty();
         }

         this.alBuffer = aint[0];
         this.hasAlBuffer = true;
         this.data = null;
      }

      return OptionalInt.of(this.alBuffer);
   }

   public void discardAlBuffer() {
      if (this.hasAlBuffer) {
         AL10.alDeleteBuffers(new int[]{this.alBuffer});
         if (OpenAlUtil.checkALError("Deleting stream buffers")) {
            return;
         }
      }

      this.hasAlBuffer = false;
   }

   public OptionalInt releaseAlBuffer() {
      OptionalInt optionalint = this.getAlBuffer();
      this.hasAlBuffer = false;
      return optionalint;
   }
}