package net.minecraft.network.protocol.login;

import java.security.PublicKey;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.Crypt;
import net.minecraft.util.CryptException;

public class ClientboundHelloPacket implements Packet<ClientLoginPacketListener> {
   private final String serverId;
   private final byte[] publicKey;
   private final byte[] nonce;

   public ClientboundHelloPacket(String p_134782_, byte[] p_134783_, byte[] p_134784_) {
      this.serverId = p_134782_;
      this.publicKey = p_134783_;
      this.nonce = p_134784_;
   }

   public ClientboundHelloPacket(FriendlyByteBuf p_179816_) {
      this.serverId = p_179816_.readUtf(20);
      this.publicKey = p_179816_.readByteArray();
      this.nonce = p_179816_.readByteArray();
   }

   public void write(FriendlyByteBuf p_134793_) {
      p_134793_.writeUtf(this.serverId);
      p_134793_.writeByteArray(this.publicKey);
      p_134793_.writeByteArray(this.nonce);
   }

   public void handle(ClientLoginPacketListener p_134790_) {
      p_134790_.handleHello(this);
   }

   public String getServerId() {
      return this.serverId;
   }

   public PublicKey getPublicKey() throws CryptException {
      return Crypt.byteToPublicKey(this.publicKey);
   }

   public byte[] getNonce() {
      return this.nonce;
   }
}