package net.minecraft.network.protocol.game;

import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import org.apache.commons.lang3.Validate;

public class ClientboundSoundPacket implements Packet<ClientGamePacketListener> {
   public static final float LOCATION_ACCURACY = 8.0F;
   private final SoundEvent sound;
   private final SoundSource source;
   private final int x;
   private final int y;
   private final int z;
   private final float volume;
   private final float pitch;

   public ClientboundSoundPacket(SoundEvent p_133442_, SoundSource p_133443_, double p_133444_, double p_133445_, double p_133446_, float p_133447_, float p_133448_) {
      Validate.notNull(p_133442_, "sound");
      this.sound = p_133442_;
      this.source = p_133443_;
      this.x = (int)(p_133444_ * 8.0D);
      this.y = (int)(p_133445_ * 8.0D);
      this.z = (int)(p_133446_ * 8.0D);
      this.volume = p_133447_;
      this.pitch = p_133448_;
   }

   public ClientboundSoundPacket(FriendlyByteBuf p_179422_) {
      this.sound = Registry.SOUND_EVENT.byId(p_179422_.readVarInt());
      this.source = p_179422_.readEnum(SoundSource.class);
      this.x = p_179422_.readInt();
      this.y = p_179422_.readInt();
      this.z = p_179422_.readInt();
      this.volume = p_179422_.readFloat();
      this.pitch = p_179422_.readFloat();
   }

   public void write(FriendlyByteBuf p_133457_) {
      p_133457_.writeVarInt(Registry.SOUND_EVENT.getId(this.sound));
      p_133457_.writeEnum(this.source);
      p_133457_.writeInt(this.x);
      p_133457_.writeInt(this.y);
      p_133457_.writeInt(this.z);
      p_133457_.writeFloat(this.volume);
      p_133457_.writeFloat(this.pitch);
   }

   public SoundEvent getSound() {
      return this.sound;
   }

   public SoundSource getSource() {
      return this.source;
   }

   public double getX() {
      return (double)((float)this.x / 8.0F);
   }

   public double getY() {
      return (double)((float)this.y / 8.0F);
   }

   public double getZ() {
      return (double)((float)this.z / 8.0F);
   }

   public float getVolume() {
      return this.volume;
   }

   public float getPitch() {
      return this.pitch;
   }

   public void handle(ClientGamePacketListener p_133454_) {
      p_133454_.handleSoundEvent(this);
   }
}