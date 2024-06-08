package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultedVertexConsumer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import java.util.Optional;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OutlineBufferSource implements MultiBufferSource {
   private final MultiBufferSource.BufferSource bufferSource;
   private final MultiBufferSource.BufferSource outlineBufferSource = MultiBufferSource.immediate(new BufferBuilder(256));
   private int teamR = 255;
   private int teamG = 255;
   private int teamB = 255;
   private int teamA = 255;

   public OutlineBufferSource(MultiBufferSource.BufferSource p_109927_) {
      this.bufferSource = p_109927_;
   }

   public VertexConsumer getBuffer(RenderType p_109935_) {
      if (p_109935_.isOutline()) {
         VertexConsumer vertexconsumer2 = this.outlineBufferSource.getBuffer(p_109935_);
         return new OutlineBufferSource.EntityOutlineGenerator(vertexconsumer2, this.teamR, this.teamG, this.teamB, this.teamA);
      } else {
         VertexConsumer vertexconsumer = this.bufferSource.getBuffer(p_109935_);
         Optional<RenderType> optional = p_109935_.outline();
         if (optional.isPresent()) {
            VertexConsumer vertexconsumer1 = this.outlineBufferSource.getBuffer(optional.get());
            OutlineBufferSource.EntityOutlineGenerator outlinebuffersource$entityoutlinegenerator = new OutlineBufferSource.EntityOutlineGenerator(vertexconsumer1, this.teamR, this.teamG, this.teamB, this.teamA);
            return VertexMultiConsumer.create(outlinebuffersource$entityoutlinegenerator, vertexconsumer);
         } else {
            return vertexconsumer;
         }
      }
   }

   public void setColor(int p_109930_, int p_109931_, int p_109932_, int p_109933_) {
      this.teamR = p_109930_;
      this.teamG = p_109931_;
      this.teamB = p_109932_;
      this.teamA = p_109933_;
   }

   public void endOutlineBatch() {
      this.outlineBufferSource.endBatch();
   }

   @OnlyIn(Dist.CLIENT)
   static class EntityOutlineGenerator extends DefaultedVertexConsumer {
      private final VertexConsumer delegate;
      private double x;
      private double y;
      private double z;
      private float u;
      private float v;

      EntityOutlineGenerator(VertexConsumer p_109943_, int p_109944_, int p_109945_, int p_109946_, int p_109947_) {
         this.delegate = p_109943_;
         super.defaultColor(p_109944_, p_109945_, p_109946_, p_109947_);
      }

      public void defaultColor(int p_109993_, int p_109994_, int p_109995_, int p_109996_) {
      }

      public void unsetDefaultColor() {
      }

      public VertexConsumer vertex(double p_109956_, double p_109957_, double p_109958_) {
         this.x = p_109956_;
         this.y = p_109957_;
         this.z = p_109958_;
         return this;
      }

      public VertexConsumer color(int p_109981_, int p_109982_, int p_109983_, int p_109984_) {
         return this;
      }

      public VertexConsumer uv(float p_109960_, float p_109961_) {
         this.u = p_109960_;
         this.v = p_109961_;
         return this;
      }

      public VertexConsumer overlayCoords(int p_109978_, int p_109979_) {
         return this;
      }

      public VertexConsumer uv2(int p_109990_, int p_109991_) {
         return this;
      }

      public VertexConsumer normal(float p_109986_, float p_109987_, float p_109988_) {
         return this;
      }

      public void vertex(float p_109963_, float p_109964_, float p_109965_, float p_109966_, float p_109967_, float p_109968_, float p_109969_, float p_109970_, float p_109971_, int p_109972_, int p_109973_, float p_109974_, float p_109975_, float p_109976_) {
         this.delegate.vertex((double)p_109963_, (double)p_109964_, (double)p_109965_).color(this.defaultR, this.defaultG, this.defaultB, this.defaultA).uv(p_109970_, p_109971_).endVertex();
      }

      public void endVertex() {
         this.delegate.vertex(this.x, this.y, this.z).color(this.defaultR, this.defaultG, this.defaultB, this.defaultA).uv(this.u, this.v).endVertex();
      }

      public com.mojang.blaze3d.vertex.VertexFormat getVertexFormat() { return delegate.getVertexFormat(); }
   }
}
