package net.minecraft.network.protocol.game;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

public record ClientboundBlockBreakAckPacket(BlockPos pos, BlockState state, ServerboundPlayerActionPacket.Action action, boolean allGood) implements Packet<ClientGamePacketListener> {
   private static final Logger LOGGER = LogUtils.getLogger();

   public ClientboundBlockBreakAckPacket(BlockPos p_131654_, BlockState p_131655_, ServerboundPlayerActionPacket.Action p_131656_, boolean p_131657_, String p_131658_) {
      this(p_131654_, p_131655_, p_131656_, p_131657_);
   }

   public ClientboundBlockBreakAckPacket {
      pos = pos.immutable();
   }

   public ClientboundBlockBreakAckPacket(FriendlyByteBuf p_178604_) {
      this(p_178604_.readBlockPos(), Block.BLOCK_STATE_REGISTRY.byId(p_178604_.readVarInt()), p_178604_.readEnum(ServerboundPlayerActionPacket.Action.class), p_178604_.readBoolean());
   }

   public void write(FriendlyByteBuf p_131667_) {
      p_131667_.writeBlockPos(this.pos);
      p_131667_.writeVarInt(Block.getId(this.state));
      p_131667_.writeEnum(this.action);
      p_131667_.writeBoolean(this.allGood);
   }

   public void handle(ClientGamePacketListener p_131664_) {
      p_131664_.handleBlockBreakAck(this);
   }
}