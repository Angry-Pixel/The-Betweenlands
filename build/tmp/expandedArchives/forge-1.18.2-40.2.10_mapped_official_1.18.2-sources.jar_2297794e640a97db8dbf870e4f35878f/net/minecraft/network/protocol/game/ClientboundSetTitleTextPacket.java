package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;

public class ClientboundSetTitleTextPacket implements Packet<ClientGamePacketListener> {
   private final Component text;

   public ClientboundSetTitleTextPacket(Component p_179392_) {
      this.text = p_179392_;
   }

   public ClientboundSetTitleTextPacket(FriendlyByteBuf p_179390_) {
      this.text = p_179390_.readComponent();
   }

   public void write(FriendlyByteBuf p_179394_) {
      p_179394_.writeComponent(this.text);
   }

   public void handle(ClientGamePacketListener p_179398_) {
      p_179398_.setTitleText(this);
   }

   public Component getText() {
      return this.text;
   }
}