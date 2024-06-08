package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class ClientboundSetCameraPacket implements Packet<ClientGamePacketListener> {
   private final int cameraId;

   public ClientboundSetCameraPacket(Entity p_133058_) {
      this.cameraId = p_133058_.getId();
   }

   public ClientboundSetCameraPacket(FriendlyByteBuf p_179278_) {
      this.cameraId = p_179278_.readVarInt();
   }

   public void write(FriendlyByteBuf p_133068_) {
      p_133068_.writeVarInt(this.cameraId);
   }

   public void handle(ClientGamePacketListener p_133066_) {
      p_133066_.handleSetCamera(this);
   }

   @Nullable
   public Entity getEntity(Level p_133060_) {
      return p_133060_.getEntity(this.cameraId);
   }
}