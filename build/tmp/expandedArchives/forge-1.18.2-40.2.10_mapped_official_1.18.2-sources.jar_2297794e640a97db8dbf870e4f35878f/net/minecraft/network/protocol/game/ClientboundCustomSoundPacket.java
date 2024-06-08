package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

public class ClientboundCustomSoundPacket implements Packet<ClientGamePacketListener> {
   public static final float LOCATION_ACCURACY = 8.0F;
   private final ResourceLocation name;
   private final SoundSource source;
   private final int x;
   private final int y;
   private final int z;
   private final float volume;
   private final float pitch;

   public ClientboundCustomSoundPacket(ResourceLocation p_132055_, SoundSource p_132056_, Vec3 p_132057_, float p_132058_, float p_132059_) {
      this.name = p_132055_;
      this.source = p_132056_;
      this.x = (int)(p_132057_.x * 8.0D);
      this.y = (int)(p_132057_.y * 8.0D);
      this.z = (int)(p_132057_.z * 8.0D);
      this.volume = p_132058_;
      this.pitch = p_132059_;
   }

   public ClientboundCustomSoundPacket(FriendlyByteBuf p_178839_) {
      this.name = p_178839_.readResourceLocation();
      this.source = p_178839_.readEnum(SoundSource.class);
      this.x = p_178839_.readInt();
      this.y = p_178839_.readInt();
      this.z = p_178839_.readInt();
      this.volume = p_178839_.readFloat();
      this.pitch = p_178839_.readFloat();
   }

   public void write(FriendlyByteBuf p_132068_) {
      p_132068_.writeResourceLocation(this.name);
      p_132068_.writeEnum(this.source);
      p_132068_.writeInt(this.x);
      p_132068_.writeInt(this.y);
      p_132068_.writeInt(this.z);
      p_132068_.writeFloat(this.volume);
      p_132068_.writeFloat(this.pitch);
   }

   public ResourceLocation getName() {
      return this.name;
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

   public void handle(ClientGamePacketListener p_132065_) {
      p_132065_.handleCustomSoundEvent(this);
   }
}