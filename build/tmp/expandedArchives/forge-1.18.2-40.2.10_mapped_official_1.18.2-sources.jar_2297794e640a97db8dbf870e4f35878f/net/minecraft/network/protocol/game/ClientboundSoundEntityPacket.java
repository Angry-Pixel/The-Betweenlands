package net.minecraft.network.protocol.game;

import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import org.apache.commons.lang3.Validate;

public class ClientboundSoundEntityPacket implements Packet<ClientGamePacketListener> {
   private final SoundEvent sound;
   private final SoundSource source;
   private final int id;
   private final float volume;
   private final float pitch;

   public ClientboundSoundEntityPacket(SoundEvent p_133415_, SoundSource p_133416_, Entity p_133417_, float p_133418_, float p_133419_) {
      Validate.notNull(p_133415_, "sound");
      this.sound = p_133415_;
      this.source = p_133416_;
      this.id = p_133417_.getId();
      this.volume = p_133418_;
      this.pitch = p_133419_;
   }

   public ClientboundSoundEntityPacket(FriendlyByteBuf p_179419_) {
      this.sound = Registry.SOUND_EVENT.byId(p_179419_.readVarInt());
      this.source = p_179419_.readEnum(SoundSource.class);
      this.id = p_179419_.readVarInt();
      this.volume = p_179419_.readFloat();
      this.pitch = p_179419_.readFloat();
   }

   public void write(FriendlyByteBuf p_133428_) {
      p_133428_.writeVarInt(Registry.SOUND_EVENT.getId(this.sound));
      p_133428_.writeEnum(this.source);
      p_133428_.writeVarInt(this.id);
      p_133428_.writeFloat(this.volume);
      p_133428_.writeFloat(this.pitch);
   }

   public SoundEvent getSound() {
      return this.sound;
   }

   public SoundSource getSource() {
      return this.source;
   }

   public int getId() {
      return this.id;
   }

   public float getVolume() {
      return this.volume;
   }

   public float getPitch() {
      return this.pitch;
   }

   public void handle(ClientGamePacketListener p_133425_) {
      p_133425_.handleSoundEntityEvent(this);
   }
}