package net.minecraft.core.particles;

public class ParticleGroup {
   private final int limit;
   public static final ParticleGroup SPORE_BLOSSOM = new ParticleGroup(1000);

   public ParticleGroup(int p_175818_) {
      this.limit = p_175818_;
   }

   public int getLimit() {
      return this.limit;
   }
}