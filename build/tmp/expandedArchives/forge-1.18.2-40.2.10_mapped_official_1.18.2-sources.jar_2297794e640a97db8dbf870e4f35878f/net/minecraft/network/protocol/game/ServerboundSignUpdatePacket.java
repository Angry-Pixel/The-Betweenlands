package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundSignUpdatePacket implements Packet<ServerGamePacketListener> {
   private static final int MAX_STRING_LENGTH = 384;
   private final BlockPos pos;
   private final String[] lines;

   public ServerboundSignUpdatePacket(BlockPos p_134649_, String p_134650_, String p_134651_, String p_134652_, String p_134653_) {
      this.pos = p_134649_;
      this.lines = new String[]{p_134650_, p_134651_, p_134652_, p_134653_};
   }

   public ServerboundSignUpdatePacket(FriendlyByteBuf p_179790_) {
      this.pos = p_179790_.readBlockPos();
      this.lines = new String[4];

      for(int i = 0; i < 4; ++i) {
         this.lines[i] = p_179790_.readUtf(384);
      }

   }

   public void write(FriendlyByteBuf p_134662_) {
      p_134662_.writeBlockPos(this.pos);

      for(int i = 0; i < 4; ++i) {
         p_134662_.writeUtf(this.lines[i]);
      }

   }

   public void handle(ServerGamePacketListener p_134659_) {
      p_134659_.handleSignUpdate(this);
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public String[] getLines() {
      return this.lines;
   }
}