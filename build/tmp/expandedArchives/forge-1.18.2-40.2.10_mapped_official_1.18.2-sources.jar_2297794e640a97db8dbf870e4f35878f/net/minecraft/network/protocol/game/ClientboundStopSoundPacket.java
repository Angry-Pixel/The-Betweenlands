package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;

public class ClientboundStopSoundPacket implements Packet<ClientGamePacketListener> {
   private static final int HAS_SOURCE = 1;
   private static final int HAS_SOUND = 2;
   @Nullable
   private final ResourceLocation name;
   @Nullable
   private final SoundSource source;

   public ClientboundStopSoundPacket(@Nullable ResourceLocation p_133468_, @Nullable SoundSource p_133469_) {
      this.name = p_133468_;
      this.source = p_133469_;
   }

   public ClientboundStopSoundPacket(FriendlyByteBuf p_179426_) {
      int i = p_179426_.readByte();
      if ((i & 1) > 0) {
         this.source = p_179426_.readEnum(SoundSource.class);
      } else {
         this.source = null;
      }

      if ((i & 2) > 0) {
         this.name = p_179426_.readResourceLocation();
      } else {
         this.name = null;
      }

   }

   public void write(FriendlyByteBuf p_133478_) {
      if (this.source != null) {
         if (this.name != null) {
            p_133478_.writeByte(3);
            p_133478_.writeEnum(this.source);
            p_133478_.writeResourceLocation(this.name);
         } else {
            p_133478_.writeByte(1);
            p_133478_.writeEnum(this.source);
         }
      } else if (this.name != null) {
         p_133478_.writeByte(2);
         p_133478_.writeResourceLocation(this.name);
      } else {
         p_133478_.writeByte(0);
      }

   }

   @Nullable
   public ResourceLocation getName() {
      return this.name;
   }

   @Nullable
   public SoundSource getSource() {
      return this.source;
   }

   public void handle(ClientGamePacketListener p_133475_) {
      p_133475_.handleStopSoundEvent(this);
   }
}