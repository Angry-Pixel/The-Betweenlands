package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundSetChunkCacheCenterPacket implements Packet<ClientGamePacketListener> {
   private final int x;
   private final int z;

   public ClientboundSetChunkCacheCenterPacket(int p_133086_, int p_133087_) {
      this.x = p_133086_;
      this.z = p_133087_;
   }

   public ClientboundSetChunkCacheCenterPacket(FriendlyByteBuf p_179282_) {
      this.x = p_179282_.readVarInt();
      this.z = p_179282_.readVarInt();
   }

   public void write(FriendlyByteBuf p_133096_) {
      p_133096_.writeVarInt(this.x);
      p_133096_.writeVarInt(this.z);
   }

   public void handle(ClientGamePacketListener p_133093_) {
      p_133093_.handleSetChunkCacheCenter(this);
   }

   public int getX() {
      return this.x;
   }

   public int getZ() {
      return this.z;
   }
}