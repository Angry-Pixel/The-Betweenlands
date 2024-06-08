package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;

public class ClientboundTabListPacket implements Packet<ClientGamePacketListener> {
   private final Component header;
   private final Component footer;

   public ClientboundTabListPacket(Component p_179430_, Component p_179431_) {
      this.header = p_179430_;
      this.footer = p_179431_;
   }

   public ClientboundTabListPacket(FriendlyByteBuf p_179428_) {
      this.header = p_179428_.readComponent();
      this.footer = p_179428_.readComponent();
   }

   public void write(FriendlyByteBuf p_133491_) {
      p_133491_.writeComponent(this.header);
      p_133491_.writeComponent(this.footer);
   }

   public void handle(ClientGamePacketListener p_133488_) {
      p_133488_.handleTabListCustomisation(this);
   }

   public Component getHeader() {
      return this.header;
   }

   public Component getFooter() {
      return this.footer;
   }
}