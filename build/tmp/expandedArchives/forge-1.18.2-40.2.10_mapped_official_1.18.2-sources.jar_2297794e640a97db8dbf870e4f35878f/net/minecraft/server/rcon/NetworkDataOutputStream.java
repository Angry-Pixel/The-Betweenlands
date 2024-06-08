package net.minecraft.server.rcon;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class NetworkDataOutputStream {
   private final ByteArrayOutputStream outputStream;
   private final DataOutputStream dataOutputStream;

   public NetworkDataOutputStream(int p_11470_) {
      this.outputStream = new ByteArrayOutputStream(p_11470_);
      this.dataOutputStream = new DataOutputStream(this.outputStream);
   }

   public void writeBytes(byte[] p_11479_) throws IOException {
      this.dataOutputStream.write(p_11479_, 0, p_11479_.length);
   }

   public void writeString(String p_11475_) throws IOException {
      this.dataOutputStream.writeBytes(p_11475_);
      this.dataOutputStream.write(0);
   }

   public void write(int p_11473_) throws IOException {
      this.dataOutputStream.write(p_11473_);
   }

   public void writeShort(short p_11477_) throws IOException {
      this.dataOutputStream.writeShort(Short.reverseBytes(p_11477_));
   }

   public void writeInt(int p_144019_) throws IOException {
      this.dataOutputStream.writeInt(Integer.reverseBytes(p_144019_));
   }

   public void writeFloat(float p_144017_) throws IOException {
      this.dataOutputStream.writeInt(Integer.reverseBytes(Float.floatToIntBits(p_144017_)));
   }

   public byte[] toByteArray() {
      return this.outputStream.toByteArray();
   }

   public void reset() {
      this.outputStream.reset();
   }
}