package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.math.Vector3f;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

public class DustColorTransitionOptions extends DustParticleOptionsBase {
   public static final Vector3f SCULK_PARTICLE_COLOR = new Vector3f(Vec3.fromRGB24(3790560));
   public static final DustColorTransitionOptions SCULK_TO_REDSTONE = new DustColorTransitionOptions(SCULK_PARTICLE_COLOR, DustParticleOptions.REDSTONE_PARTICLE_COLOR, 1.0F);
   public static final Codec<DustColorTransitionOptions> CODEC = RecordCodecBuilder.create((p_175763_) -> {
      return p_175763_.group(Vector3f.CODEC.fieldOf("fromColor").forGetter((p_175773_) -> {
         return p_175773_.color;
      }), Vector3f.CODEC.fieldOf("toColor").forGetter((p_175770_) -> {
         return p_175770_.toColor;
      }), Codec.FLOAT.fieldOf("scale").forGetter((p_175765_) -> {
         return p_175765_.scale;
      })).apply(p_175763_, DustColorTransitionOptions::new);
   });
   public static final ParticleOptions.Deserializer<DustColorTransitionOptions> DESERIALIZER = new ParticleOptions.Deserializer<DustColorTransitionOptions>() {
      public DustColorTransitionOptions fromCommand(ParticleType<DustColorTransitionOptions> p_175777_, StringReader p_175778_) throws CommandSyntaxException {
         Vector3f vector3f = DustParticleOptionsBase.readVector3f(p_175778_);
         p_175778_.expect(' ');
         float f = p_175778_.readFloat();
         Vector3f vector3f1 = DustParticleOptionsBase.readVector3f(p_175778_);
         return new DustColorTransitionOptions(vector3f, vector3f1, f);
      }

      public DustColorTransitionOptions fromNetwork(ParticleType<DustColorTransitionOptions> p_175780_, FriendlyByteBuf p_175781_) {
         Vector3f vector3f = DustParticleOptionsBase.readVector3f(p_175781_);
         float f = p_175781_.readFloat();
         Vector3f vector3f1 = DustParticleOptionsBase.readVector3f(p_175781_);
         return new DustColorTransitionOptions(vector3f, vector3f1, f);
      }
   };
   private final Vector3f toColor;

   public DustColorTransitionOptions(Vector3f p_175758_, Vector3f p_175759_, float p_175760_) {
      super(p_175758_, p_175760_);
      this.toColor = p_175759_;
   }

   public Vector3f getFromColor() {
      return this.color;
   }

   public Vector3f getToColor() {
      return this.toColor;
   }

   public void writeToNetwork(FriendlyByteBuf p_175767_) {
      super.writeToNetwork(p_175767_);
      p_175767_.writeFloat(this.toColor.x());
      p_175767_.writeFloat(this.toColor.y());
      p_175767_.writeFloat(this.toColor.z());
   }

   public String writeToString() {
      return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %.2f %.2f %.2f", Registry.PARTICLE_TYPE.getKey(this.getType()), this.color.x(), this.color.y(), this.color.z(), this.scale, this.toColor.x(), this.toColor.y(), this.toColor.z());
   }

   public ParticleType<DustColorTransitionOptions> getType() {
      return ParticleTypes.DUST_COLOR_TRANSITION;
   }
}