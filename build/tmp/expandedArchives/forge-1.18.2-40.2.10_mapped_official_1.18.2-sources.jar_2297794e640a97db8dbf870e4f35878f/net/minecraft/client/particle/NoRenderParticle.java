package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NoRenderParticle extends Particle {
   protected NoRenderParticle(ClientLevel p_107149_, double p_107150_, double p_107151_, double p_107152_) {
      super(p_107149_, p_107150_, p_107151_, p_107152_);
   }

   protected NoRenderParticle(ClientLevel p_107154_, double p_107155_, double p_107156_, double p_107157_, double p_107158_, double p_107159_, double p_107160_) {
      super(p_107154_, p_107155_, p_107156_, p_107157_, p_107158_, p_107159_, p_107160_);
   }

   public final void render(VertexConsumer p_107162_, Camera p_107163_, float p_107164_) {
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.NO_RENDER;
   }
}