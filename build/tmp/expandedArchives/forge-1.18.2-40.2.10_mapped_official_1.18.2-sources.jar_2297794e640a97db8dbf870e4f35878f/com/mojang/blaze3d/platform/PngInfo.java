package com.mojang.blaze3d.platform;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.stb.STBIEOFCallback;
import org.lwjgl.stb.STBIIOCallbacks;
import org.lwjgl.stb.STBIReadCallback;
import org.lwjgl.stb.STBISkipCallback;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

@OnlyIn(Dist.CLIENT)
public class PngInfo {
   public final int width;
   public final int height;

   public PngInfo(String p_85210_, InputStream p_85211_) throws IOException {
      MemoryStack memorystack = MemoryStack.stackPush();

      try {
         PngInfo.StbReader pnginfo$stbreader = createCallbacks(p_85211_);

         try {
            STBIReadCallback stbireadcallback = STBIReadCallback.create(pnginfo$stbreader::read);

            try {
               STBISkipCallback stbiskipcallback = STBISkipCallback.create(pnginfo$stbreader::skip);

               try {
                  STBIEOFCallback stbieofcallback = STBIEOFCallback.create(pnginfo$stbreader::eof);

                  try {
                     STBIIOCallbacks stbiiocallbacks = STBIIOCallbacks.mallocStack(memorystack);
                     stbiiocallbacks.read(stbireadcallback);
                     stbiiocallbacks.skip(stbiskipcallback);
                     stbiiocallbacks.eof(stbieofcallback);
                     IntBuffer intbuffer = memorystack.mallocInt(1);
                     IntBuffer intbuffer1 = memorystack.mallocInt(1);
                     IntBuffer intbuffer2 = memorystack.mallocInt(1);
                     if (!STBImage.stbi_info_from_callbacks(stbiiocallbacks, 0L, intbuffer, intbuffer1, intbuffer2)) {
                        throw new IOException("Could not read info from the PNG file " + p_85210_ + " " + STBImage.stbi_failure_reason());
                     }

                     this.width = intbuffer.get(0);
                     this.height = intbuffer1.get(0);
                  } catch (Throwable throwable5) {
                     if (stbieofcallback != null) {
                        try {
                           stbieofcallback.close();
                        } catch (Throwable throwable4) {
                           throwable5.addSuppressed(throwable4);
                        }
                     }

                     throw throwable5;
                  }

                  if (stbieofcallback != null) {
                     stbieofcallback.close();
                  }
               } catch (Throwable throwable6) {
                  if (stbiskipcallback != null) {
                     try {
                        stbiskipcallback.close();
                     } catch (Throwable throwable3) {
                        throwable6.addSuppressed(throwable3);
                     }
                  }

                  throw throwable6;
               }

               if (stbiskipcallback != null) {
                  stbiskipcallback.close();
               }
            } catch (Throwable throwable7) {
               if (stbireadcallback != null) {
                  try {
                     stbireadcallback.close();
                  } catch (Throwable throwable2) {
                     throwable7.addSuppressed(throwable2);
                  }
               }

               throw throwable7;
            }

            if (stbireadcallback != null) {
               stbireadcallback.close();
            }
         } catch (Throwable throwable8) {
            if (pnginfo$stbreader != null) {
               try {
                  pnginfo$stbreader.close();
               } catch (Throwable throwable1) {
                  throwable8.addSuppressed(throwable1);
               }
            }

            throw throwable8;
         }

         if (pnginfo$stbreader != null) {
            pnginfo$stbreader.close();
         }
      } catch (Throwable throwable9) {
         if (memorystack != null) {
            try {
               memorystack.close();
            } catch (Throwable throwable) {
               throwable9.addSuppressed(throwable);
            }
         }

         throw throwable9;
      }

      if (memorystack != null) {
         memorystack.close();
      }

   }

   private static PngInfo.StbReader createCallbacks(InputStream p_85213_) {
      return (PngInfo.StbReader)(p_85213_ instanceof FileInputStream ? new PngInfo.StbReaderSeekableByteChannel(((FileInputStream)p_85213_).getChannel()) : new PngInfo.StbReaderBufferedChannel(Channels.newChannel(p_85213_)));
   }

   @OnlyIn(Dist.CLIENT)
   abstract static class StbReader implements AutoCloseable {
      protected boolean closed;

      int read(long p_85224_, long p_85225_, int p_85226_) {
         try {
            return this.read(p_85225_, p_85226_);
         } catch (IOException ioexception) {
            this.closed = true;
            return 0;
         }
      }

      void skip(long p_85221_, int p_85222_) {
         try {
            this.skip(p_85222_);
         } catch (IOException ioexception) {
            this.closed = true;
         }

      }

      int eof(long p_85219_) {
         return this.closed ? 1 : 0;
      }

      protected abstract int read(long p_85227_, int p_85228_) throws IOException;

      protected abstract void skip(int p_85218_) throws IOException;

      public abstract void close() throws IOException;
   }

   @OnlyIn(Dist.CLIENT)
   static class StbReaderBufferedChannel extends PngInfo.StbReader {
      private static final int START_BUFFER_SIZE = 128;
      private final ReadableByteChannel channel;
      private long readBufferAddress = MemoryUtil.nmemAlloc(128L);
      private int bufferSize = 128;
      private int read;
      private int consumed;

      StbReaderBufferedChannel(ReadableByteChannel p_85236_) {
         this.channel = p_85236_;
      }

      private void fillReadBuffer(int p_85243_) throws IOException {
         ByteBuffer bytebuffer = MemoryUtil.memByteBuffer(this.readBufferAddress, this.bufferSize);
         if (p_85243_ + this.consumed > this.bufferSize) {
            this.bufferSize = p_85243_ + this.consumed;
            bytebuffer = MemoryUtil.memRealloc(bytebuffer, this.bufferSize);
            this.readBufferAddress = MemoryUtil.memAddress(bytebuffer);
         }

         bytebuffer.position(this.read);

         while(p_85243_ + this.consumed > this.read) {
            try {
               int i = this.channel.read(bytebuffer);
               if (i == -1) {
                  break;
               }
            } finally {
               this.read = bytebuffer.position();
            }
         }

      }

      public int read(long p_85245_, int p_85246_) throws IOException {
         this.fillReadBuffer(p_85246_);
         if (p_85246_ + this.consumed > this.read) {
            p_85246_ = this.read - this.consumed;
         }

         MemoryUtil.memCopy(this.readBufferAddress + (long)this.consumed, p_85245_, (long)p_85246_);
         this.consumed += p_85246_;
         return p_85246_;
      }

      public void skip(int p_85241_) throws IOException {
         if (p_85241_ > 0) {
            this.fillReadBuffer(p_85241_);
            if (p_85241_ + this.consumed > this.read) {
               throw new EOFException("Can't skip past the EOF.");
            }
         }

         if (this.consumed + p_85241_ < 0) {
            throw new IOException("Can't seek before the beginning: " + (this.consumed + p_85241_));
         } else {
            this.consumed += p_85241_;
         }
      }

      public void close() throws IOException {
         MemoryUtil.nmemFree(this.readBufferAddress);
         this.channel.close();
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class StbReaderSeekableByteChannel extends PngInfo.StbReader {
      private final SeekableByteChannel channel;

      StbReaderSeekableByteChannel(SeekableByteChannel p_85250_) {
         this.channel = p_85250_;
      }

      public int read(long p_85259_, int p_85260_) throws IOException {
         ByteBuffer bytebuffer = MemoryUtil.memByteBuffer(p_85259_, p_85260_);
         return this.channel.read(bytebuffer);
      }

      public void skip(int p_85255_) throws IOException {
         this.channel.position(this.channel.position() + (long)p_85255_);
      }

      public int eof(long p_85257_) {
         return super.eof(p_85257_) != 0 && this.channel.isOpen() ? 1 : 0;
      }

      public void close() throws IOException {
         this.channel.close();
      }
   }
}