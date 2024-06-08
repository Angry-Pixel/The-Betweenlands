package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.gameevent.vibrations.VibrationPath;

public class ClientboundAddVibrationSignalPacket implements Packet<ClientGamePacketListener> {
   private final VibrationPath vibrationPath;

   public ClientboundAddVibrationSignalPacket(VibrationPath p_178573_) {
      this.vibrationPath = p_178573_;
   }

   public ClientboundAddVibrationSignalPacket(FriendlyByteBuf p_178575_) {
      this.vibrationPath = VibrationPath.read(p_178575_);
   }

   public void write(FriendlyByteBuf p_178577_) {
      VibrationPath.write(p_178577_, this.vibrationPath);
   }

   public void handle(ClientGamePacketListener p_178581_) {
      p_178581_.handleAddVibrationSignal(this);
   }

   public VibrationPath getVibrationPath() {
      return this.vibrationPath;
   }
}