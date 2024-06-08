package net.minecraft.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ParticleUtils {
   public static void spawnParticlesOnBlockFaces(Level p_144963_, BlockPos p_144964_, ParticleOptions p_144965_, UniformInt p_144966_) {
      for(Direction direction : Direction.values()) {
         int i = p_144966_.sample(p_144963_.random);

         for(int j = 0; j < i; ++j) {
            spawnParticleOnFace(p_144963_, p_144964_, direction, p_144965_);
         }
      }

   }

   public static void spawnParticlesAlongAxis(Direction.Axis p_144968_, Level p_144969_, BlockPos p_144970_, double p_144971_, ParticleOptions p_144972_, UniformInt p_144973_) {
      Vec3 vec3 = Vec3.atCenterOf(p_144970_);
      boolean flag = p_144968_ == Direction.Axis.X;
      boolean flag1 = p_144968_ == Direction.Axis.Y;
      boolean flag2 = p_144968_ == Direction.Axis.Z;
      int i = p_144973_.sample(p_144969_.random);

      for(int j = 0; j < i; ++j) {
         double d0 = vec3.x + Mth.nextDouble(p_144969_.random, -1.0D, 1.0D) * (flag ? 0.5D : p_144971_);
         double d1 = vec3.y + Mth.nextDouble(p_144969_.random, -1.0D, 1.0D) * (flag1 ? 0.5D : p_144971_);
         double d2 = vec3.z + Mth.nextDouble(p_144969_.random, -1.0D, 1.0D) * (flag2 ? 0.5D : p_144971_);
         double d3 = flag ? Mth.nextDouble(p_144969_.random, -1.0D, 1.0D) : 0.0D;
         double d4 = flag1 ? Mth.nextDouble(p_144969_.random, -1.0D, 1.0D) : 0.0D;
         double d5 = flag2 ? Mth.nextDouble(p_144969_.random, -1.0D, 1.0D) : 0.0D;
         p_144969_.addParticle(p_144972_, d0, d1, d2, d3, d4, d5);
      }

   }

   public static void spawnParticleOnFace(Level p_144958_, BlockPos p_144959_, Direction p_144960_, ParticleOptions p_144961_) {
      Vec3 vec3 = Vec3.atCenterOf(p_144959_);
      int i = p_144960_.getStepX();
      int j = p_144960_.getStepY();
      int k = p_144960_.getStepZ();
      double d0 = vec3.x + (i == 0 ? Mth.nextDouble(p_144958_.random, -0.5D, 0.5D) : (double)i * 0.55D);
      double d1 = vec3.y + (j == 0 ? Mth.nextDouble(p_144958_.random, -0.5D, 0.5D) : (double)j * 0.55D);
      double d2 = vec3.z + (k == 0 ? Mth.nextDouble(p_144958_.random, -0.5D, 0.5D) : (double)k * 0.55D);
      double d3 = i == 0 ? Mth.nextDouble(p_144958_.random, -1.0D, 1.0D) : 0.0D;
      double d4 = j == 0 ? Mth.nextDouble(p_144958_.random, -1.0D, 1.0D) : 0.0D;
      double d5 = k == 0 ? Mth.nextDouble(p_144958_.random, -1.0D, 1.0D) : 0.0D;
      p_144958_.addParticle(p_144961_, d0, d1, d2, d3, d4, d5);
   }
}