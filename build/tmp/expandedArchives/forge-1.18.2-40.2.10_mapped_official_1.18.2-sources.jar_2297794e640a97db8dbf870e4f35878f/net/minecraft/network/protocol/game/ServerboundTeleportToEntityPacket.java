package net.minecraft.network.protocol.game;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

public class ServerboundTeleportToEntityPacket implements Packet<ServerGamePacketListener> {
   private final UUID uuid;

   public ServerboundTeleportToEntityPacket(UUID p_134680_) {
      this.uuid = p_134680_;
   }

   public ServerboundTeleportToEntityPacket(FriendlyByteBuf p_179794_) {
      this.uuid = p_179794_.readUUID();
   }

   public void write(FriendlyByteBuf p_134690_) {
      p_134690_.writeUUID(this.uuid);
   }

   public void handle(ServerGamePacketListener p_134688_) {
      p_134688_.handleTeleportToEntityPacket(this);
   }

   @Nullable
   public Entity getEntity(ServerLevel p_134682_) {
      return p_134682_.getEntity(this.uuid);
   }
}