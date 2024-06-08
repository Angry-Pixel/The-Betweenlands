package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundJigsawGeneratePacket implements Packet<ServerGamePacketListener> {
   private final BlockPos pos;
   private final int levels;
   private final boolean keepJigsaws;

   public ServerboundJigsawGeneratePacket(BlockPos p_134078_, int p_134079_, boolean p_134080_) {
      this.pos = p_134078_;
      this.levels = p_134079_;
      this.keepJigsaws = p_134080_;
   }

   public ServerboundJigsawGeneratePacket(FriendlyByteBuf p_179669_) {
      this.pos = p_179669_.readBlockPos();
      this.levels = p_179669_.readVarInt();
      this.keepJigsaws = p_179669_.readBoolean();
   }

   public void write(FriendlyByteBuf p_134089_) {
      p_134089_.writeBlockPos(this.pos);
      p_134089_.writeVarInt(this.levels);
      p_134089_.writeBoolean(this.keepJigsaws);
   }

   public void handle(ServerGamePacketListener p_134086_) {
      p_134086_.handleJigsawGenerate(this);
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public int levels() {
      return this.levels;
   }

   public boolean keepJigsaws() {
      return this.keepJigsaws;
   }
}