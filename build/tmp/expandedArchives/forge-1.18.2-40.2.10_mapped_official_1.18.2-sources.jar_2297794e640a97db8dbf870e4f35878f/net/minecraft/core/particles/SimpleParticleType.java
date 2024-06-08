package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;

public class SimpleParticleType extends ParticleType<SimpleParticleType> implements ParticleOptions {
   private static final ParticleOptions.Deserializer<SimpleParticleType> DESERIALIZER = new ParticleOptions.Deserializer<SimpleParticleType>() {
      public SimpleParticleType fromCommand(ParticleType<SimpleParticleType> p_123846_, StringReader p_123847_) {
         return (SimpleParticleType)p_123846_;
      }

      public SimpleParticleType fromNetwork(ParticleType<SimpleParticleType> p_123849_, FriendlyByteBuf p_123850_) {
         return (SimpleParticleType)p_123849_;
      }
   };
   private final Codec<SimpleParticleType> codec = Codec.unit(this::getType);

   public SimpleParticleType(boolean p_123837_) {
      super(p_123837_, DESERIALIZER);
   }

   public SimpleParticleType getType() {
      return this;
   }

   public Codec<SimpleParticleType> codec() {
      return this.codec;
   }

   public void writeToNetwork(FriendlyByteBuf p_123840_) {
   }

   public String writeToString() {
      return Registry.PARTICLE_TYPE.getKey(this).toString();
   }
}