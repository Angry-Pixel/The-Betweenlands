package net.minecraft.network.protocol.game;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.decoration.Motive;
import net.minecraft.world.entity.decoration.Painting;

public class ClientboundAddPaintingPacket implements Packet<ClientGamePacketListener> {
   private final int id;
   private final UUID uuid;
   private final BlockPos pos;
   private final Direction direction;
   private final int motive;

   public ClientboundAddPaintingPacket(Painting p_131573_) {
      this.id = p_131573_.getId();
      this.uuid = p_131573_.getUUID();
      this.pos = p_131573_.getPos();
      this.direction = p_131573_.getDirection();
      this.motive = Registry.MOTIVE.getId(p_131573_.motive);
   }

   public ClientboundAddPaintingPacket(FriendlyByteBuf p_178568_) {
      this.id = p_178568_.readVarInt();
      this.uuid = p_178568_.readUUID();
      this.motive = p_178568_.readVarInt();
      this.pos = p_178568_.readBlockPos();
      this.direction = Direction.from2DDataValue(p_178568_.readUnsignedByte());
   }

   public void write(FriendlyByteBuf p_131582_) {
      p_131582_.writeVarInt(this.id);
      p_131582_.writeUUID(this.uuid);
      p_131582_.writeVarInt(this.motive);
      p_131582_.writeBlockPos(this.pos);
      p_131582_.writeByte(this.direction.get2DDataValue());
   }

   public void handle(ClientGamePacketListener p_131579_) {
      p_131579_.handleAddPainting(this);
   }

   public int getId() {
      return this.id;
   }

   public UUID getUUID() {
      return this.uuid;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public Direction getDirection() {
      return this.direction;
   }

   public Motive getMotive() {
      return Registry.MOTIVE.byId(this.motive);
   }
}