package net.minecraft.client.sounds;

import java.io.BufferedInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import javax.sound.sampled.AudioFormat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LoopingAudioStream implements AudioStream {
   private final LoopingAudioStream.AudioStreamProvider provider;
   private AudioStream stream;
   private final BufferedInputStream bufferedInputStream;

   public LoopingAudioStream(LoopingAudioStream.AudioStreamProvider p_120163_, InputStream p_120164_) throws IOException {
      this.provider = p_120163_;
      this.bufferedInputStream = new BufferedInputStream(p_120164_);
      this.bufferedInputStream.mark(Integer.MAX_VALUE);
      this.stream = p_120163_.create(new LoopingAudioStream.NoCloseBuffer(this.bufferedInputStream));
   }

   public AudioFormat getFormat() {
      return this.stream.getFormat();
   }

   public ByteBuffer read(int p_120167_) throws IOException {
      ByteBuffer bytebuffer = this.stream.read(p_120167_);
      if (!bytebuffer.hasRemaining()) {
         this.stream.close();
         this.bufferedInputStream.reset();
         this.stream = this.provider.create(new LoopingAudioStream.NoCloseBuffer(this.bufferedInputStream));
         bytebuffer = this.stream.read(p_120167_);
      }

      return bytebuffer;
   }

   public void close() throws IOException {
      this.stream.close();
      this.bufferedInputStream.close();
   }

   @FunctionalInterface
   @OnlyIn(Dist.CLIENT)
   public interface AudioStreamProvider {
      AudioStream create(InputStream p_120170_) throws IOException;
   }

   @OnlyIn(Dist.CLIENT)
   static class NoCloseBuffer extends FilterInputStream {
      NoCloseBuffer(InputStream p_120172_) {
         super(p_120172_);
      }

      public void close() {
      }
   }
}