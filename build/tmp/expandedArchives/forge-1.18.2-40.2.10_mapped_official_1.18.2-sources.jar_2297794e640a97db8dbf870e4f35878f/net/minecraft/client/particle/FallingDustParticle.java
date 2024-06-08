package net.minecraft.client.particle;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FallingDustParticle extends TextureSheetParticle {
   private final float rotSpeed;
   private final SpriteSet sprites;

   FallingDustParticle(ClientLevel p_106610_, double p_106611_, double p_106612_, double p_106613_, float p_106614_, float p_106615_, float p_106616_, SpriteSet p_106617_) {
      super(p_106610_, p_106611_, p_106612_, p_106613_);
      this.sprites = p_106617_;
      this.rCol = p_106614_;
      this.gCol = p_106615_;
      this.bCol = p_106616_;
      float f = 0.9F;
      this.quadSize *= 0.67499995F;
      int i = (int)(32.0D / (Math.random() * 0.8D + 0.2D));
      this.lifetime = (int)Math.max((float)i * 0.9F, 1.0F);
      this.setSpriteFromAge(p_106617_);
      this.rotSpeed = ((float)Math.random() - 0.5F) * 0.1F;
      this.roll = (float)Math.random() * ((float)Math.PI * 2F);
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public float getQuadSize(float p_106631_) {
      return this.quadSize * Mth.clamp(((float)this.age + p_106631_) / (float)this.lifetime * 32.0F, 0.0F, 1.0F);
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.setSpriteFromAge(this.sprites);
         this.oRoll = this.roll;
         this.roll += (float)Math.PI * this.rotSpeed * 2.0F;
         if (this.onGround) {
            this.oRoll = this.roll = 0.0F;
         }

         this.move(this.xd, this.yd, this.zd);
         this.yd -= (double)0.003F;
         this.yd = Math.max(this.yd, (double)-0.14F);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<BlockParticleOption> {
      private final SpriteSet sprite;

      public Provider(SpriteSet p_106634_) {
         this.sprite = p_106634_;
      }

      @Nullable
      public Particle createParticle(BlockParticleOption p_106636_, ClientLevel p_106637_, double p_106638_, double p_106639_, double p_106640_, double p_106641_, double p_106642_, double p_106643_) {
         BlockState blockstate = p_106636_.getState();
         if (!blockstate.isAir() && blockstate.getRenderShape() == RenderShape.INVISIBLE) {
            return null;
         } else {
            BlockPos blockpos = new BlockPos(p_106638_, p_106639_, p_106640_);
            int i = Minecraft.getInstance().getBlockColors().getColor(blockstate, p_106637_, blockpos);
            if (blockstate.getBlock() instanceof FallingBlock) {
               i = ((FallingBlock)blockstate.getBlock()).getDustColor(blockstate, p_106637_, blockpos);
            }

            float f = (float)(i >> 16 & 255) / 255.0F;
            float f1 = (float)(i >> 8 & 255) / 255.0F;
            float f2 = (float)(i & 255) / 255.0F;
            return new FallingDustParticle(p_106637_, p_106638_, p_106639_, p_106640_, f, f1, f2, this.sprite);
         }
      }
   }
}