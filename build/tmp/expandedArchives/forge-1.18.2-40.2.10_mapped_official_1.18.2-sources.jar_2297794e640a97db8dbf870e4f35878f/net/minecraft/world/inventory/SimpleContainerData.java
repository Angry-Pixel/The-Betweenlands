package net.minecraft.world.inventory;

public class SimpleContainerData implements ContainerData {
   private final int[] ints;

   public SimpleContainerData(int p_40210_) {
      this.ints = new int[p_40210_];
   }

   public int get(int p_40213_) {
      return this.ints[p_40213_];
   }

   public void set(int p_40215_, int p_40216_) {
      this.ints[p_40215_] = p_40216_;
   }

   public int getCount() {
      return this.ints.length;
   }
}