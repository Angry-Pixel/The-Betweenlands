package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.player.Abilities;

public class ClientboundPlayerAbilitiesPacket implements Packet<ClientGamePacketListener> {
   private static final int FLAG_INVULNERABLE = 1;
   private static final int FLAG_FLYING = 2;
   private static final int FLAG_CAN_FLY = 4;
   private static final int FLAG_INSTABUILD = 8;
   private final boolean invulnerable;
   private final boolean isFlying;
   private final boolean canFly;
   private final boolean instabuild;
   private final float flyingSpeed;
   private final float walkingSpeed;

   public ClientboundPlayerAbilitiesPacket(Abilities p_132667_) {
      this.invulnerable = p_132667_.invulnerable;
      this.isFlying = p_132667_.flying;
      this.canFly = p_132667_.mayfly;
      this.instabuild = p_132667_.instabuild;
      this.flyingSpeed = p_132667_.getFlyingSpeed();
      this.walkingSpeed = p_132667_.getWalkingSpeed();
   }

   public ClientboundPlayerAbilitiesPacket(FriendlyByteBuf p_179033_) {
      byte b0 = p_179033_.readByte();
      this.invulnerable = (b0 & 1) != 0;
      this.isFlying = (b0 & 2) != 0;
      this.canFly = (b0 & 4) != 0;
      this.instabuild = (b0 & 8) != 0;
      this.flyingSpeed = p_179033_.readFloat();
      this.walkingSpeed = p_179033_.readFloat();
   }

   public void write(FriendlyByteBuf p_132676_) {
      byte b0 = 0;
      if (this.invulnerable) {
         b0 = (byte)(b0 | 1);
      }

      if (this.isFlying) {
         b0 = (byte)(b0 | 2);
      }

      if (this.canFly) {
         b0 = (byte)(b0 | 4);
      }

      if (this.instabuild) {
         b0 = (byte)(b0 | 8);
      }

      p_132676_.writeByte(b0);
      p_132676_.writeFloat(this.flyingSpeed);
      p_132676_.writeFloat(this.walkingSpeed);
   }

   public void handle(ClientGamePacketListener p_132673_) {
      p_132673_.handlePlayerAbilities(this);
   }

   public boolean isInvulnerable() {
      return this.invulnerable;
   }

   public boolean isFlying() {
      return this.isFlying;
   }

   public boolean canFly() {
      return this.canFly;
   }

   public boolean canInstabuild() {
      return this.instabuild;
   }

   public float getFlyingSpeed() {
      return this.flyingSpeed;
   }

   public float getWalkingSpeed() {
      return this.walkingSpeed;
   }
}