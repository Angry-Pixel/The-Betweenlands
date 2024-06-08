package net.minecraft.client.renderer;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Rect2i {
   private int xPos;
   private int yPos;
   private int width;
   private int height;

   public Rect2i(int p_110081_, int p_110082_, int p_110083_, int p_110084_) {
      this.xPos = p_110081_;
      this.yPos = p_110082_;
      this.width = p_110083_;
      this.height = p_110084_;
   }

   public Rect2i intersect(Rect2i p_173053_) {
      int i = this.xPos;
      int j = this.yPos;
      int k = this.xPos + this.width;
      int l = this.yPos + this.height;
      int i1 = p_173053_.getX();
      int j1 = p_173053_.getY();
      int k1 = i1 + p_173053_.getWidth();
      int l1 = j1 + p_173053_.getHeight();
      this.xPos = Math.max(i, i1);
      this.yPos = Math.max(j, j1);
      this.width = Math.max(0, Math.min(k, k1) - this.xPos);
      this.height = Math.max(0, Math.min(l, l1) - this.yPos);
      return this;
   }

   public int getX() {
      return this.xPos;
   }

   public int getY() {
      return this.yPos;
   }

   public void setX(int p_173048_) {
      this.xPos = p_173048_;
   }

   public void setY(int p_173055_) {
      this.yPos = p_173055_;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public void setWidth(int p_173057_) {
      this.width = p_173057_;
   }

   public void setHeight(int p_173059_) {
      this.height = p_173059_;
   }

   public void setPosition(int p_173050_, int p_173051_) {
      this.xPos = p_173050_;
      this.yPos = p_173051_;
   }

   public boolean contains(int p_110088_, int p_110089_) {
      return p_110088_ >= this.xPos && p_110088_ <= this.xPos + this.width && p_110089_ >= this.yPos && p_110089_ <= this.yPos + this.height;
   }
}