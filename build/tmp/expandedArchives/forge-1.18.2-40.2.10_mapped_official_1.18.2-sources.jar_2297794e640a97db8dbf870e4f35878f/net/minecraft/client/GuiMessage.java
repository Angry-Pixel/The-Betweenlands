package net.minecraft.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiMessage<T> {
   private final int addedTime;
   private final T message;
   private final int id;

   public GuiMessage(int p_90790_, T p_90791_, int p_90792_) {
      this.message = p_90791_;
      this.addedTime = p_90790_;
      this.id = p_90792_;
   }

   public T getMessage() {
      return this.message;
   }

   public int getAddedTime() {
      return this.addedTime;
   }

   public int getId() {
      return this.id;
   }
}