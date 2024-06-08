package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class ClientboundEntityEventPacket implements Packet<ClientGamePacketListener> {
   private final int entityId;
   private final byte eventId;

   public ClientboundEntityEventPacket(Entity p_132092_, byte p_132093_) {
      this.entityId = p_132092_.getId();
      this.eventId = p_132093_;
   }

   public ClientboundEntityEventPacket(FriendlyByteBuf p_178843_) {
      this.entityId = p_178843_.readInt();
      this.eventId = p_178843_.readByte();
   }

   public void write(FriendlyByteBuf p_132104_) {
      p_132104_.writeInt(this.entityId);
      p_132104_.writeByte(this.eventId);
   }

   public void handle(ClientGamePacketListener p_132101_) {
      p_132101_.handleEntityEvent(this);
   }

   @Nullable
   public Entity getEntity(Level p_132095_) {
      return p_132095_.getEntity(this.entityId);
   }

   public byte getEventId() {
      return this.eventId;
   }
}