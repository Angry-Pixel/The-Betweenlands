package net.minecraft.network.protocol.game;

import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundLevelParticlesPacket implements Packet<ClientGamePacketListener> {
   private final double x;
   private final double y;
   private final double z;
   private final float xDist;
   private final float yDist;
   private final float zDist;
   private final float maxSpeed;
   private final int count;
   private final boolean overrideLimiter;
   private final ParticleOptions particle;

   public <T extends ParticleOptions> ClientboundLevelParticlesPacket(T p_132292_, boolean p_132293_, double p_132294_, double p_132295_, double p_132296_, float p_132297_, float p_132298_, float p_132299_, float p_132300_, int p_132301_) {
      this.particle = p_132292_;
      this.overrideLimiter = p_132293_;
      this.x = p_132294_;
      this.y = p_132295_;
      this.z = p_132296_;
      this.xDist = p_132297_;
      this.yDist = p_132298_;
      this.zDist = p_132299_;
      this.maxSpeed = p_132300_;
      this.count = p_132301_;
   }

   public ClientboundLevelParticlesPacket(FriendlyByteBuf p_178910_) {
      ParticleType<?> particletype = Registry.PARTICLE_TYPE.byId(p_178910_.readInt());
      this.overrideLimiter = p_178910_.readBoolean();
      this.x = p_178910_.readDouble();
      this.y = p_178910_.readDouble();
      this.z = p_178910_.readDouble();
      this.xDist = p_178910_.readFloat();
      this.yDist = p_178910_.readFloat();
      this.zDist = p_178910_.readFloat();
      this.maxSpeed = p_178910_.readFloat();
      this.count = p_178910_.readInt();
      this.particle = this.readParticle(p_178910_, particletype);
   }

   private <T extends ParticleOptions> T readParticle(FriendlyByteBuf p_132305_, ParticleType<T> p_132306_) {
      return p_132306_.getDeserializer().fromNetwork(p_132306_, p_132305_);
   }

   public void write(FriendlyByteBuf p_132313_) {
      p_132313_.writeInt(Registry.PARTICLE_TYPE.getId(this.particle.getType()));
      p_132313_.writeBoolean(this.overrideLimiter);
      p_132313_.writeDouble(this.x);
      p_132313_.writeDouble(this.y);
      p_132313_.writeDouble(this.z);
      p_132313_.writeFloat(this.xDist);
      p_132313_.writeFloat(this.yDist);
      p_132313_.writeFloat(this.zDist);
      p_132313_.writeFloat(this.maxSpeed);
      p_132313_.writeInt(this.count);
      this.particle.writeToNetwork(p_132313_);
   }

   public boolean isOverrideLimiter() {
      return this.overrideLimiter;
   }

   public double getX() {
      return this.x;
   }

   public double getY() {
      return this.y;
   }

   public double getZ() {
      return this.z;
   }

   public float getXDist() {
      return this.xDist;
   }

   public float getYDist() {
      return this.yDist;
   }

   public float getZDist() {
      return this.zDist;
   }

   public float getMaxSpeed() {
      return this.maxSpeed;
   }

   public int getCount() {
      return this.count;
   }

   public ParticleOptions getParticle() {
      return this.particle;
   }

   public void handle(ClientGamePacketListener p_132310_) {
      p_132310_.handleParticleEvent(this);
   }
}