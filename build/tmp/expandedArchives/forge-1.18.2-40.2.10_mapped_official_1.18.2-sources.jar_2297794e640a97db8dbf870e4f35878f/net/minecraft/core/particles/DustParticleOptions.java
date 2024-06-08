package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.math.Vector3f;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

public class DustParticleOptions extends DustParticleOptionsBase {
   public static final Vector3f REDSTONE_PARTICLE_COLOR = new Vector3f(Vec3.fromRGB24(16711680));
   public static final DustParticleOptions REDSTONE = new DustParticleOptions(REDSTONE_PARTICLE_COLOR, 1.0F);
   public static final Codec<DustParticleOptions> CODEC = RecordCodecBuilder.create((p_175793_) -> {
      return p_175793_.group(Vector3f.CODEC.fieldOf("color").forGetter((p_175797_) -> {
         return p_175797_.color;
      }), Codec.FLOAT.fieldOf("scale").forGetter((p_175795_) -> {
         return p_175795_.scale;
      })).apply(p_175793_, DustParticleOptions::new);
   });
   public static final ParticleOptions.Deserializer<DustParticleOptions> DESERIALIZER = new ParticleOptions.Deserializer<DustParticleOptions>() {
      public DustParticleOptions fromCommand(ParticleType<DustParticleOptions> p_123689_, StringReader p_123690_) throws CommandSyntaxException {
         Vector3f vector3f = DustParticleOptionsBase.readVector3f(p_123690_);
         p_123690_.expect(' ');
         float f = p_123690_.readFloat();
         return new DustParticleOptions(vector3f, f);
      }

      public DustParticleOptions fromNetwork(ParticleType<DustParticleOptions> p_123692_, FriendlyByteBuf p_123693_) {
         return new DustParticleOptions(DustParticleOptionsBase.readVector3f(p_123693_), p_123693_.readFloat());
      }
   };

   public DustParticleOptions(Vector3f p_175790_, float p_175791_) {
      super(p_175790_, p_175791_);
   }

   public ParticleType<DustParticleOptions> getType() {
      return ParticleTypes.DUST;
   }
}