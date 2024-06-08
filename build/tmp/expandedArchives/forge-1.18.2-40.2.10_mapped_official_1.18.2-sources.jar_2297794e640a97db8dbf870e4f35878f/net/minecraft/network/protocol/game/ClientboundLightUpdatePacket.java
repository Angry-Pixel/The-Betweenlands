package net.minecraft.network.protocol.game;

import java.util.BitSet;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.lighting.LevelLightEngine;

public class ClientboundLightUpdatePacket implements Packet<ClientGamePacketListener> {
   private final int x;
   private final int z;
   private final ClientboundLightUpdatePacketData lightData;

   public ClientboundLightUpdatePacket(ChunkPos p_178912_, LevelLightEngine p_178913_, @Nullable BitSet p_178914_, @Nullable BitSet p_178915_, boolean p_178916_) {
      this.x = p_178912_.x;
      this.z = p_178912_.z;
      this.lightData = new ClientboundLightUpdatePacketData(p_178912_, p_178913_, p_178914_, p_178915_, p_178916_);
   }

   public ClientboundLightUpdatePacket(FriendlyByteBuf p_178918_) {
      this.x = p_178918_.readVarInt();
      this.z = p_178918_.readVarInt();
      this.lightData = new ClientboundLightUpdatePacketData(p_178918_, this.x, this.z);
   }

   public void write(FriendlyByteBuf p_132351_) {
      p_132351_.writeVarInt(this.x);
      p_132351_.writeVarInt(this.z);
      this.lightData.write(p_132351_);
   }

   public void handle(ClientGamePacketListener p_132348_) {
      p_132348_.handleLightUpdatePacket(this);
   }

   public int getX() {
      return this.x;
   }

   public int getZ() {
      return this.z;
   }

   public ClientboundLightUpdatePacketData getLightData() {
      return this.lightData;
   }
}