package com.mojang.blaze3d.vertex;

import java.util.function.Consumer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VertexMultiConsumer {
   public static VertexConsumer create() {
      throw new IllegalArgumentException();
   }

   public static VertexConsumer create(VertexConsumer p_167062_) {
      return p_167062_;
   }

   public static VertexConsumer create(VertexConsumer p_86169_, VertexConsumer p_86170_) {
      return new VertexMultiConsumer.Double(p_86169_, p_86170_);
   }

   public static VertexConsumer create(VertexConsumer... p_167064_) {
      return new VertexMultiConsumer.Multiple(p_167064_);
   }

   @OnlyIn(Dist.CLIENT)
   static class Double implements VertexConsumer {
      private final VertexConsumer first;
      private final VertexConsumer second;

      public Double(VertexConsumer p_86174_, VertexConsumer p_86175_) {
         if (p_86174_ == p_86175_) {
            throw new IllegalArgumentException("Duplicate delegates");
         } else {
            this.first = p_86174_;
            this.second = p_86175_;
         }
      }

      public VertexConsumer vertex(double p_86177_, double p_86178_, double p_86179_) {
         this.first.vertex(p_86177_, p_86178_, p_86179_);
         this.second.vertex(p_86177_, p_86178_, p_86179_);
         return this;
      }

      public VertexConsumer color(int p_86202_, int p_86203_, int p_86204_, int p_86205_) {
         this.first.color(p_86202_, p_86203_, p_86204_, p_86205_);
         this.second.color(p_86202_, p_86203_, p_86204_, p_86205_);
         return this;
      }

      public VertexConsumer uv(float p_86181_, float p_86182_) {
         this.first.uv(p_86181_, p_86182_);
         this.second.uv(p_86181_, p_86182_);
         return this;
      }

      public VertexConsumer overlayCoords(int p_86199_, int p_86200_) {
         this.first.overlayCoords(p_86199_, p_86200_);
         this.second.overlayCoords(p_86199_, p_86200_);
         return this;
      }

      public VertexConsumer uv2(int p_86211_, int p_86212_) {
         this.first.uv2(p_86211_, p_86212_);
         this.second.uv2(p_86211_, p_86212_);
         return this;
      }

      public VertexConsumer normal(float p_86207_, float p_86208_, float p_86209_) {
         this.first.normal(p_86207_, p_86208_, p_86209_);
         this.second.normal(p_86207_, p_86208_, p_86209_);
         return this;
      }

      public void vertex(float p_86184_, float p_86185_, float p_86186_, float p_86187_, float p_86188_, float p_86189_, float p_86190_, float p_86191_, float p_86192_, int p_86193_, int p_86194_, float p_86195_, float p_86196_, float p_86197_) {
         this.first.vertex(p_86184_, p_86185_, p_86186_, p_86187_, p_86188_, p_86189_, p_86190_, p_86191_, p_86192_, p_86193_, p_86194_, p_86195_, p_86196_, p_86197_);
         this.second.vertex(p_86184_, p_86185_, p_86186_, p_86187_, p_86188_, p_86189_, p_86190_, p_86191_, p_86192_, p_86193_, p_86194_, p_86195_, p_86196_, p_86197_);
      }

      public void endVertex() {
         this.first.endVertex();
         this.second.endVertex();
      }

      public void defaultColor(int p_167066_, int p_167067_, int p_167068_, int p_167069_) {
         this.first.defaultColor(p_167066_, p_167067_, p_167068_, p_167069_);
         this.second.defaultColor(p_167066_, p_167067_, p_167068_, p_167069_);
      }

      public void unsetDefaultColor() {
         this.first.unsetDefaultColor();
         this.second.unsetDefaultColor();
      }

      public VertexFormat getVertexFormat() { return first.getVertexFormat(); }
   }

   @OnlyIn(Dist.CLIENT)
   static class Multiple implements VertexConsumer {
      private final VertexConsumer[] delegates;

      public Multiple(VertexConsumer[] p_167073_) {
         for(int i = 0; i < p_167073_.length; ++i) {
            for(int j = i + 1; j < p_167073_.length; ++j) {
               if (p_167073_[i] == p_167073_[j]) {
                  throw new IllegalArgumentException("Duplicate delegates");
               }
            }
         }

         this.delegates = p_167073_;
      }

      private void forEach(Consumer<VertexConsumer> p_167145_) {
         for(VertexConsumer vertexconsumer : this.delegates) {
            p_167145_.accept(vertexconsumer);
         }

      }

      public VertexConsumer vertex(double p_167075_, double p_167076_, double p_167077_) {
         this.forEach((p_167082_) -> {
            p_167082_.vertex(p_167075_, p_167076_, p_167077_);
         });
         return this;
      }

      public VertexConsumer color(int p_167130_, int p_167131_, int p_167132_, int p_167133_) {
         this.forEach((p_167163_) -> {
            p_167163_.color(p_167130_, p_167131_, p_167132_, p_167133_);
         });
         return this;
      }

      public VertexConsumer uv(float p_167084_, float p_167085_) {
         this.forEach((p_167125_) -> {
            p_167125_.uv(p_167084_, p_167085_);
         });
         return this;
      }

      public VertexConsumer overlayCoords(int p_167127_, int p_167128_) {
         this.forEach((p_167167_) -> {
            p_167167_.overlayCoords(p_167127_, p_167128_);
         });
         return this;
      }

      public VertexConsumer uv2(int p_167151_, int p_167152_) {
         this.forEach((p_167143_) -> {
            p_167143_.uv2(p_167151_, p_167152_);
         });
         return this;
      }

      public VertexConsumer normal(float p_167147_, float p_167148_, float p_167149_) {
         this.forEach((p_167121_) -> {
            p_167121_.normal(p_167147_, p_167148_, p_167149_);
         });
         return this;
      }

      public void vertex(float p_167087_, float p_167088_, float p_167089_, float p_167090_, float p_167091_, float p_167092_, float p_167093_, float p_167094_, float p_167095_, int p_167096_, int p_167097_, float p_167098_, float p_167099_, float p_167100_) {
         this.forEach((p_167116_) -> {
            p_167116_.vertex(p_167087_, p_167088_, p_167089_, p_167090_, p_167091_, p_167092_, p_167093_, p_167094_, p_167095_, p_167096_, p_167097_, p_167098_, p_167099_, p_167100_);
         });
      }

      public void endVertex() {
         this.forEach(VertexConsumer::endVertex);
      }

      public void defaultColor(int p_167154_, int p_167155_, int p_167156_, int p_167157_) {
         this.forEach((p_167139_) -> {
            p_167139_.defaultColor(p_167154_, p_167155_, p_167156_, p_167157_);
         });
      }

      public void unsetDefaultColor() {
         this.forEach(VertexConsumer::unsetDefaultColor);
      }

      public VertexFormat getVertexFormat()
      {
         if (delegates.length > 0)
            return delegates[0].getVertexFormat();
         return DefaultVertexFormat.BLOCK;
      }
   }
}
