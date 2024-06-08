package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.Difficulty;

public class ServerboundChangeDifficultyPacket implements Packet<ServerGamePacketListener> {
   private final Difficulty difficulty;

   public ServerboundChangeDifficultyPacket(Difficulty p_133817_) {
      this.difficulty = p_133817_;
   }

   public void handle(ServerGamePacketListener p_133823_) {
      p_133823_.handleChangeDifficulty(this);
   }

   public ServerboundChangeDifficultyPacket(FriendlyByteBuf p_179542_) {
      this.difficulty = Difficulty.byId(p_179542_.readUnsignedByte());
   }

   public void write(FriendlyByteBuf p_133826_) {
      p_133826_.writeByte(this.difficulty.getId());
   }

   public Difficulty getDifficulty() {
      return this.difficulty;
   }
}