package net.minecraft.network.protocol.game;

import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ClientboundBlockEntityDataPacket implements Packet<ClientGamePacketListener> {
   private final BlockPos pos;
   private final BlockEntityType<?> type;
   @Nullable
   private final CompoundTag tag;

   public static ClientboundBlockEntityDataPacket create(BlockEntity p_195643_, Function<BlockEntity, CompoundTag> p_195644_) {
      return new ClientboundBlockEntityDataPacket(p_195643_.getBlockPos(), p_195643_.getType(), p_195644_.apply(p_195643_));
   }

   public static ClientboundBlockEntityDataPacket create(BlockEntity p_195641_) {
      return create(p_195641_, BlockEntity::getUpdateTag);
   }

   private ClientboundBlockEntityDataPacket(BlockPos p_195637_, BlockEntityType<?> p_195638_, CompoundTag p_195639_) {
      this.pos = p_195637_;
      this.type = p_195638_;
      this.tag = p_195639_.isEmpty() ? null : p_195639_;
   }

   public ClientboundBlockEntityDataPacket(FriendlyByteBuf p_178621_) {
      this.pos = p_178621_.readBlockPos();
      this.type = Registry.BLOCK_ENTITY_TYPE.byId(p_178621_.readVarInt());
      this.tag = p_178621_.readNbt();
   }

   public void write(FriendlyByteBuf p_131706_) {
      p_131706_.writeBlockPos(this.pos);
      p_131706_.writeVarInt(Registry.BLOCK_ENTITY_TYPE.getId(this.type));
      p_131706_.writeNbt(this.tag);
   }

   public void handle(ClientGamePacketListener p_131703_) {
      p_131703_.handleBlockEntityData(this);
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public BlockEntityType<?> getType() {
      return this.type;
   }

   @Nullable
   public CompoundTag getTag() {
      return this.tag;
   }
}