package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class TextureSheetParticle extends SingleQuadParticle {
   protected TextureAtlasSprite sprite;

   protected TextureSheetParticle(ClientLevel p_108323_, double p_108324_, double p_108325_, double p_108326_) {
      super(p_108323_, p_108324_, p_108325_, p_108326_);
   }

   protected TextureSheetParticle(ClientLevel p_108328_, double p_108329_, double p_108330_, double p_108331_, double p_108332_, double p_108333_, double p_108334_) {
      super(p_108328_, p_108329_, p_108330_, p_108331_, p_108332_, p_108333_, p_108334_);
   }

   protected void setSprite(TextureAtlasSprite p_108338_) {
      this.sprite = p_108338_;
   }

   protected float getU0() {
      return this.sprite.getU0();
   }

   protected float getU1() {
      return this.sprite.getU1();
   }

   protected float getV0() {
      return this.sprite.getV0();
   }

   protected float getV1() {
      return this.sprite.getV1();
   }

   public void pickSprite(SpriteSet p_108336_) {
      this.setSprite(p_108336_.get(this.random));
   }

   public void setSpriteFromAge(SpriteSet p_108340_) {
      if (!this.removed) {
         this.setSprite(p_108340_.get(this.age, this.lifetime));
      }

   }
}