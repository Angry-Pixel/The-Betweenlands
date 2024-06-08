package net.minecraft.client.gui.font.glyphs;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BakedGlyph {
   private final RenderType normalType;
   private final RenderType seeThroughType;
   private final RenderType polygonOffsetType;
   private final float u0;
   private final float u1;
   private final float v0;
   private final float v1;
   private final float left;
   private final float right;
   private final float up;
   private final float down;

   public BakedGlyph(RenderType p_181376_, RenderType p_181377_, RenderType p_181378_, float p_181379_, float p_181380_, float p_181381_, float p_181382_, float p_181383_, float p_181384_, float p_181385_, float p_181386_) {
      this.normalType = p_181376_;
      this.seeThroughType = p_181377_;
      this.polygonOffsetType = p_181378_;
      this.u0 = p_181379_;
      this.u1 = p_181380_;
      this.v0 = p_181381_;
      this.v1 = p_181382_;
      this.left = p_181383_;
      this.right = p_181384_;
      this.up = p_181385_;
      this.down = p_181386_;
   }

   public void render(boolean p_95227_, float p_95228_, float p_95229_, Matrix4f p_95230_, VertexConsumer p_95231_, float p_95232_, float p_95233_, float p_95234_, float p_95235_, int p_95236_) {
      int i = 3;
      float f = p_95228_ + this.left;
      float f1 = p_95228_ + this.right;
      float f2 = this.up - 3.0F;
      float f3 = this.down - 3.0F;
      float f4 = p_95229_ + f2;
      float f5 = p_95229_ + f3;
      float f6 = p_95227_ ? 1.0F - 0.25F * f2 : 0.0F;
      float f7 = p_95227_ ? 1.0F - 0.25F * f3 : 0.0F;
      p_95231_.vertex(p_95230_, f + f6, f4, 0.0F).color(p_95232_, p_95233_, p_95234_, p_95235_).uv(this.u0, this.v0).uv2(p_95236_).endVertex();
      p_95231_.vertex(p_95230_, f + f7, f5, 0.0F).color(p_95232_, p_95233_, p_95234_, p_95235_).uv(this.u0, this.v1).uv2(p_95236_).endVertex();
      p_95231_.vertex(p_95230_, f1 + f7, f5, 0.0F).color(p_95232_, p_95233_, p_95234_, p_95235_).uv(this.u1, this.v1).uv2(p_95236_).endVertex();
      p_95231_.vertex(p_95230_, f1 + f6, f4, 0.0F).color(p_95232_, p_95233_, p_95234_, p_95235_).uv(this.u1, this.v0).uv2(p_95236_).endVertex();
   }

   public void renderEffect(BakedGlyph.Effect p_95221_, Matrix4f p_95222_, VertexConsumer p_95223_, int p_95224_) {
      p_95223_.vertex(p_95222_, p_95221_.x0, p_95221_.y0, p_95221_.depth).color(p_95221_.r, p_95221_.g, p_95221_.b, p_95221_.a).uv(this.u0, this.v0).uv2(p_95224_).endVertex();
      p_95223_.vertex(p_95222_, p_95221_.x1, p_95221_.y0, p_95221_.depth).color(p_95221_.r, p_95221_.g, p_95221_.b, p_95221_.a).uv(this.u0, this.v1).uv2(p_95224_).endVertex();
      p_95223_.vertex(p_95222_, p_95221_.x1, p_95221_.y1, p_95221_.depth).color(p_95221_.r, p_95221_.g, p_95221_.b, p_95221_.a).uv(this.u1, this.v1).uv2(p_95224_).endVertex();
      p_95223_.vertex(p_95222_, p_95221_.x0, p_95221_.y1, p_95221_.depth).color(p_95221_.r, p_95221_.g, p_95221_.b, p_95221_.a).uv(this.u1, this.v0).uv2(p_95224_).endVertex();
   }

   public RenderType renderType(Font.DisplayMode p_181388_) {
      switch(p_181388_) {
      case NORMAL:
      default:
         return this.normalType;
      case SEE_THROUGH:
         return this.seeThroughType;
      case POLYGON_OFFSET:
         return this.polygonOffsetType;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Effect {
      protected final float x0;
      protected final float y0;
      protected final float x1;
      protected final float y1;
      protected final float depth;
      protected final float r;
      protected final float g;
      protected final float b;
      protected final float a;

      public Effect(float p_95247_, float p_95248_, float p_95249_, float p_95250_, float p_95251_, float p_95252_, float p_95253_, float p_95254_, float p_95255_) {
         this.x0 = p_95247_;
         this.y0 = p_95248_;
         this.x1 = p_95249_;
         this.y1 = p_95250_;
         this.depth = p_95251_;
         this.r = p_95252_;
         this.g = p_95253_;
         this.b = p_95254_;
         this.a = p_95255_;
      }
   }
}