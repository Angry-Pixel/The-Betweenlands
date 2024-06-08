package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundLevelEventPacket implements Packet<ClientGamePacketListener> {
   private final int type;
   private final BlockPos pos;
   private final int data;
   private final boolean globalEvent;

   public ClientboundLevelEventPacket(int p_132264_, BlockPos p_132265_, int p_132266_, boolean p_132267_) {
      this.type = p_132264_;
      this.pos = p_132265_.immutable();
      this.data = p_132266_;
      this.globalEvent = p_132267_;
   }

   public ClientboundLevelEventPacket(FriendlyByteBuf p_178908_) {
      this.type = p_178908_.readInt();
      this.pos = p_178908_.readBlockPos();
      this.data = p_178908_.readInt();
      this.globalEvent = p_178908_.readBoolean();
   }

   public void write(FriendlyByteBuf p_132276_) {
      p_132276_.writeInt(this.type);
      p_132276_.writeBlockPos(this.pos);
      p_132276_.writeInt(this.data);
      p_132276_.writeBoolean(this.globalEvent);
   }

   public void handle(ClientGamePacketListener p_132273_) {
      p_132273_.handleLevelEvent(this);
   }

   public boolean isGlobalEvent() {
      return this.globalEvent;
   }

   public int getType() {
      return this.type;
   }

   public int getData() {
      return this.data;
   }

   public BlockPos getPos() {
      return this.pos;
   }
}