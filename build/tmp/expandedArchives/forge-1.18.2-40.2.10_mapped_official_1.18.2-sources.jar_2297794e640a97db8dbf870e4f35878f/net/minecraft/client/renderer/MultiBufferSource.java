package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface MultiBufferSource {
   static MultiBufferSource.BufferSource immediate(BufferBuilder p_109899_) {
      return immediateWithBuffers(ImmutableMap.of(), p_109899_);
   }

   static MultiBufferSource.BufferSource immediateWithBuffers(Map<RenderType, BufferBuilder> p_109901_, BufferBuilder p_109902_) {
      return new MultiBufferSource.BufferSource(p_109902_, p_109901_);
   }

   VertexConsumer getBuffer(RenderType p_109903_);

   @OnlyIn(Dist.CLIENT)
   public static class BufferSource implements MultiBufferSource {
      protected final BufferBuilder builder;
      protected final Map<RenderType, BufferBuilder> fixedBuffers;
      protected Optional<RenderType> lastState = Optional.empty();
      protected final Set<BufferBuilder> startedBuffers = Sets.newHashSet();

      protected BufferSource(BufferBuilder p_109909_, Map<RenderType, BufferBuilder> p_109910_) {
         this.builder = p_109909_;
         this.fixedBuffers = p_109910_;
      }

      public VertexConsumer getBuffer(RenderType p_109919_) {
         Optional<RenderType> optional = p_109919_.asOptional();
         BufferBuilder bufferbuilder = this.getBuilderRaw(p_109919_);
         if (!Objects.equals(this.lastState, optional)) {
            if (this.lastState.isPresent()) {
               RenderType rendertype = this.lastState.get();
               if (!this.fixedBuffers.containsKey(rendertype)) {
                  this.endBatch(rendertype);
               }
            }

            if (this.startedBuffers.add(bufferbuilder)) {
               bufferbuilder.begin(p_109919_.mode(), p_109919_.format());
            }

            this.lastState = optional;
         }

         return bufferbuilder;
      }

      private BufferBuilder getBuilderRaw(RenderType p_109915_) {
         return this.fixedBuffers.getOrDefault(p_109915_, this.builder);
      }

      public void endLastBatch() {
         if (this.lastState.isPresent()) {
            RenderType rendertype = this.lastState.get();
            if (!this.fixedBuffers.containsKey(rendertype)) {
               this.endBatch(rendertype);
            }

            this.lastState = Optional.empty();
         }

      }

      public void endBatch() {
         this.lastState.ifPresent((p_109917_) -> {
            VertexConsumer vertexconsumer = this.getBuffer(p_109917_);
            if (vertexconsumer == this.builder) {
               this.endBatch(p_109917_);
            }

         });

         for(RenderType rendertype : this.fixedBuffers.keySet()) {
            this.endBatch(rendertype);
         }

      }

      public void endBatch(RenderType p_109913_) {
         BufferBuilder bufferbuilder = this.getBuilderRaw(p_109913_);
         boolean flag = Objects.equals(this.lastState, p_109913_.asOptional());
         if (flag || bufferbuilder != this.builder) {
            if (this.startedBuffers.remove(bufferbuilder)) {
               p_109913_.end(bufferbuilder, 0, 0, 0);
               if (flag) {
                  this.lastState = Optional.empty();
               }

            }
         }
      }
   }
}