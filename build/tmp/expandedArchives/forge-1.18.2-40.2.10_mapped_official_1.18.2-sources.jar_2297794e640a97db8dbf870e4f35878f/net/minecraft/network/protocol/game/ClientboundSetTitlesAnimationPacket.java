package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundSetTitlesAnimationPacket implements Packet<ClientGamePacketListener> {
   private final int fadeIn;
   private final int stay;
   private final int fadeOut;

   public ClientboundSetTitlesAnimationPacket(int p_179404_, int p_179405_, int p_179406_) {
      this.fadeIn = p_179404_;
      this.stay = p_179405_;
      this.fadeOut = p_179406_;
   }

   public ClientboundSetTitlesAnimationPacket(FriendlyByteBuf p_179408_) {
      this.fadeIn = p_179408_.readInt();
      this.stay = p_179408_.readInt();
      this.fadeOut = p_179408_.readInt();
   }

   public void write(FriendlyByteBuf p_179410_) {
      p_179410_.writeInt(this.fadeIn);
      p_179410_.writeInt(this.stay);
      p_179410_.writeInt(this.fadeOut);
   }

   public void handle(ClientGamePacketListener p_179414_) {
      p_179414_.setTitlesAnimation(this);
   }

   public int getFadeIn() {
      return this.fadeIn;
   }

   public int getStay() {
      return this.stay;
   }

   public int getFadeOut() {
      return this.fadeOut;
   }
}