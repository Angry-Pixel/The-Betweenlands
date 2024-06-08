package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundRenameItemPacket implements Packet<ServerGamePacketListener> {
   private final String name;

   public ServerboundRenameItemPacket(String p_134396_) {
      this.name = p_134396_;
   }

   public ServerboundRenameItemPacket(FriendlyByteBuf p_179738_) {
      this.name = p_179738_.readUtf();
   }

   public void write(FriendlyByteBuf p_134405_) {
      p_134405_.writeUtf(this.name);
   }

   public void handle(ServerGamePacketListener p_134402_) {
      p_134402_.handleRenameItem(this);
   }

   public String getName() {
      return this.name;
   }
}