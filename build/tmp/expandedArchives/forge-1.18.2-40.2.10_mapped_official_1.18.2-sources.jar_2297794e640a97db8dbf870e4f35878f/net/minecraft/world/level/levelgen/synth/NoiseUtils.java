package net.minecraft.world.level.levelgen.synth;

public class NoiseUtils {
   public static double biasTowardsExtreme(double p_164335_, double p_164336_) {
      return p_164335_ + Math.sin(Math.PI * p_164335_) * p_164336_ / Math.PI;
   }

   public static void parityNoiseOctaveConfigString(StringBuilder p_192826_, double p_192827_, double p_192828_, double p_192829_, byte[] p_192830_) {
      p_192826_.append(String.format("xo=%.3f, yo=%.3f, zo=%.3f, p0=%d, p255=%d", (float)p_192827_, (float)p_192828_, (float)p_192829_, p_192830_[0], p_192830_[255]));
   }

   public static void parityNoiseOctaveConfigString(StringBuilder p_192832_, double p_192833_, double p_192834_, double p_192835_, int[] p_192836_) {
      p_192832_.append(String.format("xo=%.3f, yo=%.3f, zo=%.3f, p0=%d, p255=%d", (float)p_192833_, (float)p_192834_, (float)p_192835_, p_192836_[0], p_192836_[255]));
   }
}