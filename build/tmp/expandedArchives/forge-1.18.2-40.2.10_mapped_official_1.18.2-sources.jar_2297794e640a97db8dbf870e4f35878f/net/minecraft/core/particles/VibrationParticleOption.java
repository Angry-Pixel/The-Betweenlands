package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.vibrations.VibrationPath;

public class VibrationParticleOption implements ParticleOptions {
   public static final Codec<VibrationParticleOption> CODEC = RecordCodecBuilder.create((p_175850_) -> {
      return p_175850_.group(VibrationPath.CODEC.fieldOf("vibration").forGetter((p_175852_) -> {
         return p_175852_.vibrationPath;
      })).apply(p_175850_, VibrationParticleOption::new);
   });
   public static final ParticleOptions.Deserializer<VibrationParticleOption> DESERIALIZER = new ParticleOptions.Deserializer<VibrationParticleOption>() {
      public VibrationParticleOption fromCommand(ParticleType<VibrationParticleOption> p_175859_, StringReader p_175860_) throws CommandSyntaxException {
         p_175860_.expect(' ');
         float f = (float)p_175860_.readDouble();
         p_175860_.expect(' ');
         float f1 = (float)p_175860_.readDouble();
         p_175860_.expect(' ');
         float f2 = (float)p_175860_.readDouble();
         p_175860_.expect(' ');
         float f3 = (float)p_175860_.readDouble();
         p_175860_.expect(' ');
         float f4 = (float)p_175860_.readDouble();
         p_175860_.expect(' ');
         float f5 = (float)p_175860_.readDouble();
         p_175860_.expect(' ');
         int i = p_175860_.readInt();
         BlockPos blockpos = new BlockPos((double)f, (double)f1, (double)f2);
         BlockPos blockpos1 = new BlockPos((double)f3, (double)f4, (double)f5);
         return new VibrationParticleOption(new VibrationPath(blockpos, new BlockPositionSource(blockpos1), i));
      }

      public VibrationParticleOption fromNetwork(ParticleType<VibrationParticleOption> p_175862_, FriendlyByteBuf p_175863_) {
         VibrationPath vibrationpath = VibrationPath.read(p_175863_);
         return new VibrationParticleOption(vibrationpath);
      }
   };
   private final VibrationPath vibrationPath;

   public VibrationParticleOption(VibrationPath p_175847_) {
      this.vibrationPath = p_175847_;
   }

   public void writeToNetwork(FriendlyByteBuf p_175854_) {
      VibrationPath.write(p_175854_, this.vibrationPath);
   }

   public String writeToString() {
      BlockPos blockpos = this.vibrationPath.getOrigin();
      double d0 = (double)blockpos.getX();
      double d1 = (double)blockpos.getY();
      double d2 = (double)blockpos.getZ();
      return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %.2f %.2f %d", Registry.PARTICLE_TYPE.getKey(this.getType()), d0, d1, d2, d0, d1, d2, this.vibrationPath.getArrivalInTicks());
   }

   public ParticleType<VibrationParticleOption> getType() {
      return ParticleTypes.VIBRATION;
   }

   public VibrationPath getVibrationPath() {
      return this.vibrationPath;
   }
}