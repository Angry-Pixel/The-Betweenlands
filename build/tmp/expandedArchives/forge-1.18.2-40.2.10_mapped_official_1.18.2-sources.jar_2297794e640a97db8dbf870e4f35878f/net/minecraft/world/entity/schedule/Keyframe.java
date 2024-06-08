package net.minecraft.world.entity.schedule;

public class Keyframe {
   private final int timeStamp;
   private final float value;

   public Keyframe(int p_38008_, float p_38009_) {
      this.timeStamp = p_38008_;
      this.value = p_38009_;
   }

   public int getTimeStamp() {
      return this.timeStamp;
   }

   public float getValue() {
      return this.value;
   }
}