package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.player.Abilities;

public class ServerboundPlayerAbilitiesPacket implements Packet<ServerGamePacketListener> {
   private static final int FLAG_FLYING = 2;
   private final boolean isFlying;

   public ServerboundPlayerAbilitiesPacket(Abilities p_134257_) {
      this.isFlying = p_134257_.flying;
   }

   public ServerboundPlayerAbilitiesPacket(FriendlyByteBuf p_179709_) {
      byte b0 = p_179709_.readByte();
      this.isFlying = (b0 & 2) != 0;
   }

   public void write(FriendlyByteBuf p_134266_) {
      byte b0 = 0;
      if (this.isFlying) {
         b0 = (byte)(b0 | 2);
      }

      p_134266_.writeByte(b0);
   }

   public void handle(ServerGamePacketListener p_134263_) {
      p_134263_.handlePlayerAbilities(this);
   }

   public boolean isFlying() {
      return this.isFlying;
   }
}