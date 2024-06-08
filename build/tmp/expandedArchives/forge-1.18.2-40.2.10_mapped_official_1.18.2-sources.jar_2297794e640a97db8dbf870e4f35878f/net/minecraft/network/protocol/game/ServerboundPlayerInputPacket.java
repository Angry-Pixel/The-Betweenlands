package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundPlayerInputPacket implements Packet<ServerGamePacketListener> {
   private static final int FLAG_JUMPING = 1;
   private static final int FLAG_SHIFT_KEY_DOWN = 2;
   private final float xxa;
   private final float zza;
   private final boolean isJumping;
   private final boolean isShiftKeyDown;

   public ServerboundPlayerInputPacket(float p_134345_, float p_134346_, boolean p_134347_, boolean p_134348_) {
      this.xxa = p_134345_;
      this.zza = p_134346_;
      this.isJumping = p_134347_;
      this.isShiftKeyDown = p_134348_;
   }

   public ServerboundPlayerInputPacket(FriendlyByteBuf p_179720_) {
      this.xxa = p_179720_.readFloat();
      this.zza = p_179720_.readFloat();
      byte b0 = p_179720_.readByte();
      this.isJumping = (b0 & 1) > 0;
      this.isShiftKeyDown = (b0 & 2) > 0;
   }

   public void write(FriendlyByteBuf p_134357_) {
      p_134357_.writeFloat(this.xxa);
      p_134357_.writeFloat(this.zza);
      byte b0 = 0;
      if (this.isJumping) {
         b0 = (byte)(b0 | 1);
      }

      if (this.isShiftKeyDown) {
         b0 = (byte)(b0 | 2);
      }

      p_134357_.writeByte(b0);
   }

   public void handle(ServerGamePacketListener p_134354_) {
      p_134354_.handlePlayerInput(this);
   }

   public float getXxa() {
      return this.xxa;
   }

   public float getZza() {
      return this.zza;
   }

   public boolean isJumping() {
      return this.isJumping;
   }

   public boolean isShiftKeyDown() {
      return this.isShiftKeyDown;
   }
}