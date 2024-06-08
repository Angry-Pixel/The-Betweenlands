package net.minecraft.network.protocol.game;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundRemoveEntitiesPacket implements Packet<ClientGamePacketListener> {
   private final IntList entityIds;

   public ClientboundRemoveEntitiesPacket(IntList p_182719_) {
      this.entityIds = new IntArrayList(p_182719_);
   }

   public ClientboundRemoveEntitiesPacket(int... p_182723_) {
      this.entityIds = new IntArrayList(p_182723_);
   }

   public ClientboundRemoveEntitiesPacket(FriendlyByteBuf p_182721_) {
      this.entityIds = p_182721_.readIntIdList();
   }

   public void write(FriendlyByteBuf p_182725_) {
      p_182725_.writeIntIdList(this.entityIds);
   }

   public void handle(ClientGamePacketListener p_182729_) {
      p_182729_.handleRemoveEntities(this);
   }

   public IntList getEntityIds() {
      return this.entityIds;
   }
}