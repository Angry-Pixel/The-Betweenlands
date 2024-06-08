package com.mojang.blaze3d.audio;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;
import javax.sound.sampled.AudioFormat;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisAlloc;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

@OnlyIn(Dist.CLIENT)
public class OggAudioStream implements AudioStream {
   private static final int EXPECTED_MAX_FRAME_SIZE = 8192;
   private long handle;
   private final AudioFormat audioFormat;
   private final InputStream input;
   private ByteBuffer buffer = MemoryUtil.memAlloc(8192);

   public OggAudioStream(InputStream p_83751_) throws IOException {
      this.input = p_83751_;
      this.buffer.limit(0);
      MemoryStack memorystack = MemoryStack.stackPush();

      try {
         IntBuffer intbuffer = memorystack.mallocInt(1);
         IntBuffer intbuffer1 = memorystack.mallocInt(1);

         while(this.handle == 0L) {
            if (!this.refillFromStream()) {
               throw new IOException("Failed to find Ogg header");
            }

            int i = this.buffer.position();
            this.buffer.position(0);
            this.handle = STBVorbis.stb_vorbis_open_pushdata(this.buffer, intbuffer, intbuffer1, (STBVorbisAlloc)null);
            this.buffer.position(i);
            int j = intbuffer1.get(0);
            if (j == 1) {
               this.forwardBuffer();
            } else if (j != 0) {
               throw new IOException("Failed to read Ogg file " + j);
            }
         }

         this.buffer.position(this.buffer.position() + intbuffer.get(0));
         STBVorbisInfo stbvorbisinfo = STBVorbisInfo.mallocStack(memorystack);
         STBVorbis.stb_vorbis_get_info(this.handle, stbvorbisinfo);
         this.audioFormat = new AudioFormat((float)stbvorbisinfo.sample_rate(), 16, stbvorbisinfo.channels(), true, false);
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

   }

   private boolean refillFromStream() throws IOException {
      int i = this.buffer.limit();
      int j = this.buffer.capacity() - i;
      if (j == 0) {
         return true;
      } else {
         byte[] abyte = new byte[j];
         int k = this.input.read(abyte);
         if (k == -1) {
            return false;
         } else {
            int l = this.buffer.position();
            this.buffer.limit(i + k);
            this.buffer.position(i);
            this.buffer.put(abyte, 0, k);
            this.buffer.position(l);
            return true;
         }
      }
   }

   private void forwardBuffer() {
      boolean flag = this.buffer.position() == 0;
      boolean flag1 = this.buffer.position() == this.buffer.limit();
      if (flag1 && !flag) {
         this.buffer.position(0);
         this.buffer.limit(0);
      } else {
         ByteBuffer bytebuffer = MemoryUtil.memAlloc(flag ? 2 * this.buffer.capacity() : this.buffer.capacity());
         bytebuffer.put(this.buffer);
         MemoryUtil.memFree(this.buffer);
         bytebuffer.flip();
         this.buffer = bytebuffer;
      }

   }

   private boolean readFrame(OggAudioStream.OutputConcat p_83756_) throws IOException {
      if (this.handle == 0L) {
         return false;
      } else {
         MemoryStack memorystack = MemoryStack.stackPush();

         boolean flag1;
         label79: {
            boolean flag;
            label80: {
               try {
                  PointerBuffer pointerbuffer = memorystack.mallocPointer(1);
                  IntBuffer intbuffer = memorystack.mallocInt(1);
                  IntBuffer intbuffer1 = memorystack.mallocInt(1);

                  while(true) {
                     int l = STBVorbis.stb_vorbis_decode_frame_pushdata(this.handle, this.buffer, intbuffer, pointerbuffer, intbuffer1);
                     this.buffer.position(this.buffer.position() + l);
                     int i = STBVorbis.stb_vorbis_get_error(this.handle);
                     if (i == 1) {
                        this.forwardBuffer();
                        if (!this.refillFromStream()) {
                           flag1 = false;
                           break label79;
                        }
                     } else {
                        if (i != 0) {
                           throw new IOException("Failed to read Ogg file " + i);
                        }

                        int j = intbuffer1.get(0);
                        if (j != 0) {
                           int k = intbuffer.get(0);
                           PointerBuffer pointerbuffer1 = pointerbuffer.getPointerBuffer(k);
                           if (k == 1) {
                              this.convertMono(pointerbuffer1.getFloatBuffer(0, j), p_83756_);
                              flag = true;
                              break label80;
                           }

                           if (k != 2) {
                              throw new IllegalStateException("Invalid number of channels: " + k);
                           }

                           this.convertStereo(pointerbuffer1.getFloatBuffer(0, j), pointerbuffer1.getFloatBuffer(1, j), p_83756_);
                           flag = true;
                           break;
                        }
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

               return flag;
            }

            if (memorystack != null) {
               memorystack.close();
            }

            return flag;
         }

         if (memorystack != null) {
            memorystack.close();
         }

         return flag1;
      }
   }

   private void convertMono(FloatBuffer p_83758_, OggAudioStream.OutputConcat p_83759_) {
      while(p_83758_.hasRemaining()) {
         p_83759_.put(p_83758_.get());
      }

   }

   private void convertStereo(FloatBuffer p_83761_, FloatBuffer p_83762_, OggAudioStream.OutputConcat p_83763_) {
      while(p_83761_.hasRemaining() && p_83762_.hasRemaining()) {
         p_83763_.put(p_83761_.get());
         p_83763_.put(p_83762_.get());
      }

   }

   public void close() throws IOException {
      if (this.handle != 0L) {
         STBVorbis.stb_vorbis_close(this.handle);
         this.handle = 0L;
      }

      MemoryUtil.memFree(this.buffer);
      this.input.close();
   }

   public AudioFormat getFormat() {
      return this.audioFormat;
   }

   public ByteBuffer read(int p_83754_) throws IOException {
      OggAudioStream.OutputConcat oggaudiostream$outputconcat = new OggAudioStream.OutputConcat(p_83754_ + 8192);

      while(this.readFrame(oggaudiostream$outputconcat) && oggaudiostream$outputconcat.byteCount < p_83754_) {
      }

      return oggaudiostream$outputconcat.get();
   }

   public ByteBuffer readAll() throws IOException {
      OggAudioStream.OutputConcat oggaudiostream$outputconcat = new OggAudioStream.OutputConcat(16384);

      while(this.readFrame(oggaudiostream$outputconcat)) {
      }

      return oggaudiostream$outputconcat.get();
   }

   @OnlyIn(Dist.CLIENT)
   static class OutputConcat {
      private final List<ByteBuffer> buffers = Lists.newArrayList();
      private final int bufferSize;
      int byteCount;
      private ByteBuffer currentBuffer;

      public OutputConcat(int p_83773_) {
         this.bufferSize = p_83773_ + 1 & -2;
         this.createNewBuffer();
      }

      private void createNewBuffer() {
         this.currentBuffer = BufferUtils.createByteBuffer(this.bufferSize);
      }

      public void put(float p_83776_) {
         if (this.currentBuffer.remaining() == 0) {
            this.currentBuffer.flip();
            this.buffers.add(this.currentBuffer);
            this.createNewBuffer();
         }

         int i = Mth.clamp((int)(p_83776_ * 32767.5F - 0.5F), -32768, 32767);
         this.currentBuffer.putShort((short)i);
         this.byteCount += 2;
      }

      public ByteBuffer get() {
         this.currentBuffer.flip();
         if (this.buffers.isEmpty()) {
            return this.currentBuffer;
         } else {
            ByteBuffer bytebuffer = BufferUtils.createByteBuffer(this.byteCount);
            this.buffers.forEach(bytebuffer::put);
            bytebuffer.put(this.currentBuffer);
            bytebuffer.flip();
            return bytebuffer;
         }
      }
   }
}