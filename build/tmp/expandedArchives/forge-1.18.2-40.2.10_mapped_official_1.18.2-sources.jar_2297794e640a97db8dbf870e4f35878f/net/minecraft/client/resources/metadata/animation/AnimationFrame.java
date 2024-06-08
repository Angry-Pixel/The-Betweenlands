package net.minecraft.client.resources.metadata.animation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AnimationFrame {
   public static final int UNKNOWN_FRAME_TIME = -1;
   private final int index;
   private final int time;

   public AnimationFrame(int p_119004_) {
      this(p_119004_, -1);
   }

   public AnimationFrame(int p_119006_, int p_119007_) {
      this.index = p_119006_;
      this.time = p_119007_;
   }

   public int getTime(int p_174857_) {
      return this.time == -1 ? p_174857_ : this.time;
   }

   public int getIndex() {
      return this.index;
   }
}