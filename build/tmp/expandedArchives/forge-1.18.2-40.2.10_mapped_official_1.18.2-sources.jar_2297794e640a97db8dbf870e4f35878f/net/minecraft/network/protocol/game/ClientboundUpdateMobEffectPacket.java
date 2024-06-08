package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

public class ClientboundUpdateMobEffectPacket implements Packet<ClientGamePacketListener> {
   private static final int FLAG_AMBIENT = 1;
   private static final int FLAG_VISIBLE = 2;
   private static final int FLAG_SHOW_ICON = 4;
   private final int entityId;
   private final int effectId;
   private final byte effectAmplifier;
   private final int effectDurationTicks;
   private final byte flags;

   public ClientboundUpdateMobEffectPacket(int p_133611_, MobEffectInstance p_133612_) {
      this.entityId = p_133611_;
      this.effectId = MobEffect.getId(p_133612_.getEffect());
      this.effectAmplifier = (byte)(p_133612_.getAmplifier() & 255);
      if (p_133612_.getDuration() > 32767) {
         this.effectDurationTicks = 32767;
      } else {
         this.effectDurationTicks = p_133612_.getDuration();
      }

      byte b0 = 0;
      if (p_133612_.isAmbient()) {
         b0 = (byte)(b0 | 1);
      }

      if (p_133612_.isVisible()) {
         b0 = (byte)(b0 | 2);
      }

      if (p_133612_.showIcon()) {
         b0 = (byte)(b0 | 4);
      }

      this.flags = b0;
   }

   public ClientboundUpdateMobEffectPacket(FriendlyByteBuf p_179466_) {
      this.entityId = p_179466_.readVarInt();
      this.effectId = p_179466_.readVarInt();
      this.effectAmplifier = p_179466_.readByte();
      this.effectDurationTicks = p_179466_.readVarInt();
      this.flags = p_179466_.readByte();
   }

   public void write(FriendlyByteBuf p_133621_) {
      p_133621_.writeVarInt(this.entityId);
      p_133621_.writeVarInt(this.effectId);
      p_133621_.writeByte(this.effectAmplifier);
      p_133621_.writeVarInt(this.effectDurationTicks);
      p_133621_.writeByte(this.flags);
   }

   public boolean isSuperLongDuration() {
      return this.effectDurationTicks == 32767;
   }

   public void handle(ClientGamePacketListener p_133618_) {
      p_133618_.handleUpdateMobEffect(this);
   }

   public int getEntityId() {
      return this.entityId;
   }

   public int getEffectId() {
      return this.effectId;
   }

   public byte getEffectAmplifier() {
      return this.effectAmplifier;
   }

   public int getEffectDurationTicks() {
      return this.effectDurationTicks;
   }

   public boolean isEffectVisible() {
      return (this.flags & 2) == 2;
   }

   public boolean isEffectAmbient() {
      return (this.flags & 1) == 1;
   }

   public boolean effectShowsIcon() {
      return (this.flags & 4) == 4;
   }
}