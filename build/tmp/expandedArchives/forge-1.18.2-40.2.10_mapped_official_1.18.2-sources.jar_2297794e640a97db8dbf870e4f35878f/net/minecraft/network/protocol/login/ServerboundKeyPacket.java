package net.minecraft.network.protocol.login;

import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.SecretKey;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.Crypt;
import net.minecraft.util.CryptException;

public class ServerboundKeyPacket implements Packet<ServerLoginPacketListener> {
   private final byte[] keybytes;
   private final byte[] nonce;

   public ServerboundKeyPacket(SecretKey p_134856_, PublicKey p_134857_, byte[] p_134858_) throws CryptException {
      this.keybytes = Crypt.encryptUsingKey(p_134857_, p_134856_.getEncoded());
      this.nonce = Crypt.encryptUsingKey(p_134857_, p_134858_);
   }

   public ServerboundKeyPacket(FriendlyByteBuf p_179829_) {
      this.keybytes = p_179829_.readByteArray();
      this.nonce = p_179829_.readByteArray();
   }

   public void write(FriendlyByteBuf p_134870_) {
      p_134870_.writeByteArray(this.keybytes);
      p_134870_.writeByteArray(this.nonce);
   }

   public void handle(ServerLoginPacketListener p_134866_) {
      p_134866_.handleKey(this);
   }

   public SecretKey getSecretKey(PrivateKey p_134860_) throws CryptException {
      return Crypt.decryptByteToSecretKey(p_134860_, this.keybytes);
   }

   public byte[] getNonce(PrivateKey p_134868_) throws CryptException {
      return Crypt.decryptUsingKey(p_134868_, this.nonce);
   }
}