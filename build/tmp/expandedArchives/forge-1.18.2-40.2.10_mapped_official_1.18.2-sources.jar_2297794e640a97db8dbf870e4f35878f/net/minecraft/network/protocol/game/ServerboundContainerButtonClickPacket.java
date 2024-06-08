package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundContainerButtonClickPacket implements Packet<ServerGamePacketListener> {
   private final int containerId;
   private final int buttonId;

   public ServerboundContainerButtonClickPacket(int p_133927_, int p_133928_) {
      this.containerId = p_133927_;
      this.buttonId = p_133928_;
   }

   public void handle(ServerGamePacketListener p_133934_) {
      p_133934_.handleContainerButtonClick(this);
   }

   public ServerboundContainerButtonClickPacket(FriendlyByteBuf p_179567_) {
      this.containerId = p_179567_.readByte();
      this.buttonId = p_179567_.readByte();
   }

   public void write(FriendlyByteBuf p_133937_) {
      p_133937_.writeByte(this.containerId);
      p_133937_.writeByte(this.buttonId);
   }

   public int getContainerId() {
      return this.containerId;
   }

   public int getButtonId() {
      return this.buttonId;
   }
}