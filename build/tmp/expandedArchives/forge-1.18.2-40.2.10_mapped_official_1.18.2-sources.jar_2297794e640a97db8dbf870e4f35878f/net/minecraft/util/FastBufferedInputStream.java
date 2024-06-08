package net.minecraft.util;

import java.io.IOException;
import java.io.InputStream;

public class FastBufferedInputStream extends InputStream {
   private static final int DEFAULT_BUFFER_SIZE = 8192;
   private final InputStream in;
   private final byte[] buffer;
   private int limit;
   private int position;

   public FastBufferedInputStream(InputStream p_196566_) {
      this(p_196566_, 8192);
   }

   public FastBufferedInputStream(InputStream p_196568_, int p_196569_) {
      this.in = p_196568_;
      this.buffer = new byte[p_196569_];
   }

   public int read() throws IOException {
      if (this.position >= this.limit) {
         this.fill();
         if (this.position >= this.limit) {
            return -1;
         }
      }

      return Byte.toUnsignedInt(this.buffer[this.position++]);
   }

   public int read(byte[] p_196576_, int p_196577_, int p_196578_) throws IOException {
      int i = this.bytesInBuffer();
      if (i <= 0) {
         if (p_196578_ >= this.buffer.length) {
            return this.in.read(p_196576_, p_196577_, p_196578_);
         }

         this.fill();
         i = this.bytesInBuffer();
         if (i <= 0) {
            return -1;
         }
      }

      if (p_196578_ > i) {
         p_196578_ = i;
      }

      System.arraycopy(this.buffer, this.position, p_196576_, p_196577_, p_196578_);
      this.position += p_196578_;
      return p_196578_;
   }

   public long skip(long p_196580_) throws IOException {
      if (p_196580_ <= 0L) {
         return 0L;
      } else {
         long i = (long)this.bytesInBuffer();
         if (i <= 0L) {
            return this.in.skip(p_196580_);
         } else {
            if (p_196580_ > i) {
               p_196580_ = i;
            }

            this.position = (int)((long)this.position + p_196580_);
            return p_196580_;
         }
      }
   }

   public int available() throws IOException {
      return this.bytesInBuffer() + this.in.available();
   }

   public void close() throws IOException {
      this.in.close();
   }

   private int bytesInBuffer() {
      return this.limit - this.position;
   }

   private void fill() throws IOException {
      this.limit = 0;
      this.position = 0;
      int i = this.in.read(this.buffer, 0, this.buffer.length);
      if (i > 0) {
         this.limit = i;
      }

   }
}