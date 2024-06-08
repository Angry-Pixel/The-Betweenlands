package net.minecraft.network.protocol.game;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.SynchedEntityData;

public class ClientboundSetEntityDataPacket implements Packet<ClientGamePacketListener> {
   private final int id;
   @Nullable
   private final List<SynchedEntityData.DataItem<?>> packedItems;

   public ClientboundSetEntityDataPacket(int p_133147_, SynchedEntityData p_133148_, boolean p_133149_) {
      this.id = p_133147_;
      if (p_133149_) {
         this.packedItems = p_133148_.getAll();
         p_133148_.clearDirty();
      } else {
         this.packedItems = p_133148_.packDirty();
      }

   }

   public ClientboundSetEntityDataPacket(FriendlyByteBuf p_179290_) {
      this.id = p_179290_.readVarInt();
      this.packedItems = SynchedEntityData.unpack(p_179290_);
   }

   public void write(FriendlyByteBuf p_133158_) {
      p_133158_.writeVarInt(this.id);
      SynchedEntityData.pack(this.packedItems, p_133158_);
   }

   public void handle(ClientGamePacketListener p_133155_) {
      p_133155_.handleSetEntityData(this);
   }

   @Nullable
   public List<SynchedEntityData.DataItem<?>> getUnpackedData() {
      return this.packedItems;
   }

   public int getId() {
      return this.id;
   }
}