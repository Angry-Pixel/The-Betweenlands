package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;

public class ClientboundSetSubtitleTextPacket implements Packet<ClientGamePacketListener> {
   private final Component text;

   public ClientboundSetSubtitleTextPacket(Component p_179378_) {
      this.text = p_179378_;
   }

   public ClientboundSetSubtitleTextPacket(FriendlyByteBuf p_179376_) {
      this.text = p_179376_.readComponent();
   }

   public void write(FriendlyByteBuf p_179380_) {
      p_179380_.writeComponent(this.text);
   }

   public void handle(ClientGamePacketListener p_179384_) {
      p_179384_.setSubtitleText(this);
   }

   public Component getText() {
      return this.text;
   }
}