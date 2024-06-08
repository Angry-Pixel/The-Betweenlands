package net.minecraft.client.renderer.block;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LiquidBlockRenderer {
   private static final float MAX_FLUID_HEIGHT = 0.8888889F;
   private final TextureAtlasSprite[] lavaIcons = new TextureAtlasSprite[2];
   private final TextureAtlasSprite[] waterIcons = new TextureAtlasSprite[2];
   private TextureAtlasSprite waterOverlay;

   protected void setupSprites() {
      this.lavaIcons[0] = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(Blocks.LAVA.defaultBlockState()).getParticleIcon();
      this.lavaIcons[1] = ModelBakery.LAVA_FLOW.sprite();
      this.waterIcons[0] = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(Blocks.WATER.defaultBlockState()).getParticleIcon();
      this.waterIcons[1] = ModelBakery.WATER_FLOW.sprite();
      this.waterOverlay = ModelBakery.WATER_OVERLAY.sprite();
   }

   private static boolean isNeighborSameFluid(FluidState p_203186_, FluidState p_203187_) {
      return p_203187_.getType().isSame(p_203186_.getType());
   }

   private static boolean isFaceOccludedByState(BlockGetter p_110979_, Direction p_110980_, float p_110981_, BlockPos p_110982_, BlockState p_110983_) {
      if (p_110983_.canOcclude()) {
         VoxelShape voxelshape = Shapes.box(0.0D, 0.0D, 0.0D, 1.0D, (double)p_110981_, 1.0D);
         VoxelShape voxelshape1 = p_110983_.getOcclusionShape(p_110979_, p_110982_);
         return Shapes.blockOccudes(voxelshape, voxelshape1, p_110980_);
      } else {
         return false;
      }
   }

   private static boolean isFaceOccludedByNeighbor(BlockGetter p_203180_, BlockPos p_203181_, Direction p_203182_, float p_203183_, BlockState p_203184_) {
      return isFaceOccludedByState(p_203180_, p_203182_, p_203183_, p_203181_.relative(p_203182_), p_203184_);
   }

   private static boolean isFaceOccludedBySelf(BlockGetter p_110960_, BlockPos p_110961_, BlockState p_110962_, Direction p_110963_) {
      return isFaceOccludedByState(p_110960_, p_110963_.getOpposite(), 1.0F, p_110961_, p_110962_);
   }

   public static boolean shouldRenderFace(BlockAndTintGetter p_203167_, BlockPos p_203168_, FluidState p_203169_, BlockState p_203170_, Direction p_203171_, FluidState p_203172_) {
      return !isFaceOccludedBySelf(p_203167_, p_203168_, p_203170_, p_203171_) && !isNeighborSameFluid(p_203169_, p_203172_);
   }

   public boolean tesselate(BlockAndTintGetter p_203174_, BlockPos p_203175_, VertexConsumer p_203176_, BlockState p_203177_, FluidState p_203178_) {
      boolean flag = p_203178_.is(FluidTags.LAVA);
      TextureAtlasSprite[] atextureatlassprite = net.minecraftforge.client.ForgeHooksClient.getFluidSprites(p_203174_, p_203175_, p_203178_);
      int i = p_203178_.getType().getAttributes().getColor(p_203174_, p_203175_);
      float alpha = (float)(i >> 24 & 255) / 255.0F;
      float f = (float)(i >> 16 & 255) / 255.0F;
      float f1 = (float)(i >> 8 & 255) / 255.0F;
      float f2 = (float)(i & 255) / 255.0F;
      BlockState blockstate = p_203174_.getBlockState(p_203175_.relative(Direction.DOWN));
      FluidState fluidstate = blockstate.getFluidState();
      BlockState blockstate1 = p_203174_.getBlockState(p_203175_.relative(Direction.UP));
      FluidState fluidstate1 = blockstate1.getFluidState();
      BlockState blockstate2 = p_203174_.getBlockState(p_203175_.relative(Direction.NORTH));
      FluidState fluidstate2 = blockstate2.getFluidState();
      BlockState blockstate3 = p_203174_.getBlockState(p_203175_.relative(Direction.SOUTH));
      FluidState fluidstate3 = blockstate3.getFluidState();
      BlockState blockstate4 = p_203174_.getBlockState(p_203175_.relative(Direction.WEST));
      FluidState fluidstate4 = blockstate4.getFluidState();
      BlockState blockstate5 = p_203174_.getBlockState(p_203175_.relative(Direction.EAST));
      FluidState fluidstate5 = blockstate5.getFluidState();
      boolean flag1 = !isNeighborSameFluid(p_203178_, fluidstate1);
      boolean flag2 = shouldRenderFace(p_203174_, p_203175_, p_203178_, p_203177_, Direction.DOWN, fluidstate) && !isFaceOccludedByNeighbor(p_203174_, p_203175_, Direction.DOWN, 0.8888889F, blockstate);
      boolean flag3 = shouldRenderFace(p_203174_, p_203175_, p_203178_, p_203177_, Direction.NORTH, fluidstate2);
      boolean flag4 = shouldRenderFace(p_203174_, p_203175_, p_203178_, p_203177_, Direction.SOUTH, fluidstate3);
      boolean flag5 = shouldRenderFace(p_203174_, p_203175_, p_203178_, p_203177_, Direction.WEST, fluidstate4);
      boolean flag6 = shouldRenderFace(p_203174_, p_203175_, p_203178_, p_203177_, Direction.EAST, fluidstate5);
      if (!flag1 && !flag2 && !flag6 && !flag5 && !flag3 && !flag4) {
         return false;
      } else {
         boolean flag7 = false;
         float f3 = p_203174_.getShade(Direction.DOWN, true);
         float f4 = p_203174_.getShade(Direction.UP, true);
         float f5 = p_203174_.getShade(Direction.NORTH, true);
         float f6 = p_203174_.getShade(Direction.WEST, true);
         Fluid fluid = p_203178_.getType();
         float f11 = this.getHeight(p_203174_, fluid, p_203175_, p_203177_, p_203178_);
         float f7;
         float f8;
         float f9;
         float f10;
         if (f11 >= 1.0F) {
            f7 = 1.0F;
            f8 = 1.0F;
            f9 = 1.0F;
            f10 = 1.0F;
         } else {
            float f12 = this.getHeight(p_203174_, fluid, p_203175_.north(), blockstate2, fluidstate2);
            float f13 = this.getHeight(p_203174_, fluid, p_203175_.south(), blockstate3, fluidstate3);
            float f14 = this.getHeight(p_203174_, fluid, p_203175_.east(), blockstate5, fluidstate5);
            float f15 = this.getHeight(p_203174_, fluid, p_203175_.west(), blockstate4, fluidstate4);
            f7 = this.calculateAverageHeight(p_203174_, fluid, f11, f12, f14, p_203175_.relative(Direction.NORTH).relative(Direction.EAST));
            f8 = this.calculateAverageHeight(p_203174_, fluid, f11, f12, f15, p_203175_.relative(Direction.NORTH).relative(Direction.WEST));
            f9 = this.calculateAverageHeight(p_203174_, fluid, f11, f13, f14, p_203175_.relative(Direction.SOUTH).relative(Direction.EAST));
            f10 = this.calculateAverageHeight(p_203174_, fluid, f11, f13, f15, p_203175_.relative(Direction.SOUTH).relative(Direction.WEST));
         }

         double d1 = (double)(p_203175_.getX() & 15);
         double d2 = (double)(p_203175_.getY() & 15);
         double d0 = (double)(p_203175_.getZ() & 15);
         float f16 = 0.001F;
         float f17 = flag2 ? 0.001F : 0.0F;
         if (flag1 && !isFaceOccludedByNeighbor(p_203174_, p_203175_, Direction.UP, Math.min(Math.min(f8, f10), Math.min(f9, f7)), blockstate1)) {
            flag7 = true;
            f8 -= 0.001F;
            f10 -= 0.001F;
            f9 -= 0.001F;
            f7 -= 0.001F;
            Vec3 vec3 = p_203178_.getFlow(p_203174_, p_203175_);
            float f18;
            float f19;
            float f20;
            float f21;
            float f22;
            float f23;
            float f24;
            float f25;
            if (vec3.x == 0.0D && vec3.z == 0.0D) {
               TextureAtlasSprite textureatlassprite1 = atextureatlassprite[0];
               f18 = textureatlassprite1.getU(0.0D);
               f22 = textureatlassprite1.getV(0.0D);
               f19 = f18;
               f23 = textureatlassprite1.getV(16.0D);
               f20 = textureatlassprite1.getU(16.0D);
               f24 = f23;
               f21 = f20;
               f25 = f22;
            } else {
               TextureAtlasSprite textureatlassprite = atextureatlassprite[1];
               float f26 = (float)Mth.atan2(vec3.z, vec3.x) - ((float)Math.PI / 2F);
               float f27 = Mth.sin(f26) * 0.25F;
               float f28 = Mth.cos(f26) * 0.25F;
               float f29 = 8.0F;
               f18 = textureatlassprite.getU((double)(8.0F + (-f28 - f27) * 16.0F));
               f22 = textureatlassprite.getV((double)(8.0F + (-f28 + f27) * 16.0F));
               f19 = textureatlassprite.getU((double)(8.0F + (-f28 + f27) * 16.0F));
               f23 = textureatlassprite.getV((double)(8.0F + (f28 + f27) * 16.0F));
               f20 = textureatlassprite.getU((double)(8.0F + (f28 + f27) * 16.0F));
               f24 = textureatlassprite.getV((double)(8.0F + (f28 - f27) * 16.0F));
               f21 = textureatlassprite.getU((double)(8.0F + (f28 - f27) * 16.0F));
               f25 = textureatlassprite.getV((double)(8.0F + (-f28 - f27) * 16.0F));
            }

            float f49 = (f18 + f19 + f20 + f21) / 4.0F;
            float f50 = (f22 + f23 + f24 + f25) / 4.0F;
            float f51 = (float)atextureatlassprite[0].getWidth() / (atextureatlassprite[0].getU1() - atextureatlassprite[0].getU0());
            float f52 = (float)atextureatlassprite[0].getHeight() / (atextureatlassprite[0].getV1() - atextureatlassprite[0].getV0());
            float f53 = 4.0F / Math.max(f52, f51);
            f18 = Mth.lerp(f53, f18, f49);
            f19 = Mth.lerp(f53, f19, f49);
            f20 = Mth.lerp(f53, f20, f49);
            f21 = Mth.lerp(f53, f21, f49);
            f22 = Mth.lerp(f53, f22, f50);
            f23 = Mth.lerp(f53, f23, f50);
            f24 = Mth.lerp(f53, f24, f50);
            f25 = Mth.lerp(f53, f25, f50);
            int j = this.getLightColor(p_203174_, p_203175_);
            float f30 = f4 * f;
            float f31 = f4 * f1;
            float f32 = f4 * f2;

            this.vertex(p_203176_, d1 + 0.0D, d2 + (double)f8, d0 + 0.0D, f30, f31, f32, alpha, f18, f22, j);
            this.vertex(p_203176_, d1 + 0.0D, d2 + (double)f10, d0 + 1.0D, f30, f31, f32, alpha, f19, f23, j);
            this.vertex(p_203176_, d1 + 1.0D, d2 + (double)f9, d0 + 1.0D, f30, f31, f32, alpha, f20, f24, j);
            this.vertex(p_203176_, d1 + 1.0D, d2 + (double)f7, d0 + 0.0D, f30, f31, f32, alpha, f21, f25, j);
            if (p_203178_.shouldRenderBackwardUpFace(p_203174_, p_203175_.above())) {
               this.vertex(p_203176_, d1 + 0.0D, d2 + (double)f8, d0 + 0.0D, f30, f31, f32, alpha, f18, f22, j);
               this.vertex(p_203176_, d1 + 1.0D, d2 + (double)f7, d0 + 0.0D, f30, f31, f32, alpha, f21, f25, j);
               this.vertex(p_203176_, d1 + 1.0D, d2 + (double)f9, d0 + 1.0D, f30, f31, f32, alpha, f20, f24, j);
               this.vertex(p_203176_, d1 + 0.0D, d2 + (double)f10, d0 + 1.0D, f30, f31, f32, alpha, f19, f23, j);
            }
         }

         if (flag2) {
            float f40 = atextureatlassprite[0].getU0();
            float f41 = atextureatlassprite[0].getU1();
            float f42 = atextureatlassprite[0].getV0();
            float f43 = atextureatlassprite[0].getV1();
            int l = this.getLightColor(p_203174_, p_203175_.below());
            float f46 = f3 * f;
            float f47 = f3 * f1;
            float f48 = f3 * f2;

            this.vertex(p_203176_, d1, d2 + (double)f17, d0 + 1.0D, f46, f47, f48, alpha, f40, f43, l);
            this.vertex(p_203176_, d1, d2 + (double)f17, d0, f46, f47, f48, alpha, f40, f42, l);
            this.vertex(p_203176_, d1 + 1.0D, d2 + (double)f17, d0, f46, f47, f48, alpha, f41, f42, l);
            this.vertex(p_203176_, d1 + 1.0D, d2 + (double)f17, d0 + 1.0D, f46, f47, f48, alpha, f41, f43, l);
            flag7 = true;
         }

         int k = this.getLightColor(p_203174_, p_203175_);

         for(Direction direction : Direction.Plane.HORIZONTAL) {
            float f44;
            float f45;
            double d3;
            double d4;
            double d5;
            double d6;
            boolean flag8;
            switch(direction) {
            case NORTH:
               f44 = f8;
               f45 = f7;
               d3 = d1;
               d5 = d1 + 1.0D;
               d4 = d0 + (double)0.001F;
               d6 = d0 + (double)0.001F;
               flag8 = flag3;
               break;
            case SOUTH:
               f44 = f9;
               f45 = f10;
               d3 = d1 + 1.0D;
               d5 = d1;
               d4 = d0 + 1.0D - (double)0.001F;
               d6 = d0 + 1.0D - (double)0.001F;
               flag8 = flag4;
               break;
            case WEST:
               f44 = f10;
               f45 = f8;
               d3 = d1 + (double)0.001F;
               d5 = d1 + (double)0.001F;
               d4 = d0 + 1.0D;
               d6 = d0;
               flag8 = flag5;
               break;
            default:
               f44 = f7;
               f45 = f9;
               d3 = d1 + 1.0D - (double)0.001F;
               d5 = d1 + 1.0D - (double)0.001F;
               d4 = d0;
               d6 = d0 + 1.0D;
               flag8 = flag6;
            }

            if (flag8 && !isFaceOccludedByNeighbor(p_203174_, p_203175_, direction, Math.max(f44, f45), p_203174_.getBlockState(p_203175_.relative(direction)))) {
               flag7 = true;
               BlockPos blockpos = p_203175_.relative(direction);
               TextureAtlasSprite textureatlassprite2 = atextureatlassprite[1];
               if (atextureatlassprite[2] != null) {
                  if (p_203174_.getBlockState(blockpos).shouldDisplayFluidOverlay(p_203174_, blockpos, p_203178_)) {
                     textureatlassprite2 = atextureatlassprite[2];
                  }
               }

               float f54 = textureatlassprite2.getU(0.0D);
               float f55 = textureatlassprite2.getU(8.0D);
               float f33 = textureatlassprite2.getV((double)((1.0F - f44) * 16.0F * 0.5F));
               float f34 = textureatlassprite2.getV((double)((1.0F - f45) * 16.0F * 0.5F));
               float f35 = textureatlassprite2.getV(8.0D);
               float f36 = direction.getAxis() == Direction.Axis.Z ? f5 : f6;
               float f37 = f4 * f36 * f;
               float f38 = f4 * f36 * f1;
               float f39 = f4 * f36 * f2;

               this.vertex(p_203176_, d3, d2 + (double)f44, d4, f37, f38, f39, alpha, f54, f33, k);
               this.vertex(p_203176_, d5, d2 + (double)f45, d6, f37, f38, f39, alpha, f55, f34, k);
               this.vertex(p_203176_, d5, d2 + (double)f17, d6, f37, f38, f39, alpha, f55, f35, k);
               this.vertex(p_203176_, d3, d2 + (double)f17, d4, f37, f38, f39, alpha, f54, f35, k);
               if (textureatlassprite2 != this.waterOverlay) {
                  this.vertex(p_203176_, d3, d2 + (double)f17, d4, f37, f38, f39, alpha, f54, f35, k);
                  this.vertex(p_203176_, d5, d2 + (double)f17, d6, f37, f38, f39, alpha, f55, f35, k);
                  this.vertex(p_203176_, d5, d2 + (double)f45, d6, f37, f38, f39, alpha, f55, f34, k);
                  this.vertex(p_203176_, d3, d2 + (double)f44, d4, f37, f38, f39, alpha, f54, f33, k);
               }
            }
         }

         return flag7;
      }
   }

   private float calculateAverageHeight(BlockAndTintGetter p_203150_, Fluid p_203151_, float p_203152_, float p_203153_, float p_203154_, BlockPos p_203155_) {
      if (!(p_203154_ >= 1.0F) && !(p_203153_ >= 1.0F)) {
         float[] afloat = new float[2];
         if (p_203154_ > 0.0F || p_203153_ > 0.0F) {
            float f = this.getHeight(p_203150_, p_203151_, p_203155_);
            if (f >= 1.0F) {
               return 1.0F;
            }

            this.addWeightedHeight(afloat, f);
         }

         this.addWeightedHeight(afloat, p_203152_);
         this.addWeightedHeight(afloat, p_203154_);
         this.addWeightedHeight(afloat, p_203153_);
         return afloat[0] / afloat[1];
      } else {
         return 1.0F;
      }
   }

   private void addWeightedHeight(float[] p_203189_, float p_203190_) {
      if (p_203190_ >= 0.8F) {
         p_203189_[0] += p_203190_ * 10.0F;
         p_203189_[1] += 10.0F;
      } else if (p_203190_ >= 0.0F) {
         p_203189_[0] += p_203190_;
         p_203189_[1] += 1.0F;
      }

   }

   private float getHeight(BlockAndTintGetter p_203157_, Fluid p_203158_, BlockPos p_203159_) {
      BlockState blockstate = p_203157_.getBlockState(p_203159_);
      return this.getHeight(p_203157_, p_203158_, p_203159_, blockstate, blockstate.getFluidState());
   }

   private float getHeight(BlockAndTintGetter p_203161_, Fluid p_203162_, BlockPos p_203163_, BlockState p_203164_, FluidState p_203165_) {
      if (p_203162_.isSame(p_203165_.getType())) {
         BlockState blockstate = p_203161_.getBlockState(p_203163_.above());
         return p_203162_.isSame(blockstate.getFluidState().getType()) ? 1.0F : p_203165_.getOwnHeight();
      } else {
         return !p_203164_.getMaterial().isSolid() ? 0.0F : -1.0F;
      }
   }

   private void vertex(VertexConsumer p_110985_, double p_110986_, double p_110987_, double p_110988_, float p_110989_, float p_110990_, float p_110991_, float alpha, float p_110992_, float p_110993_, int p_110994_) {
       p_110985_.vertex(p_110986_, p_110987_, p_110988_).color(p_110989_, p_110990_, p_110991_, alpha).uv(p_110992_, p_110993_).uv2(p_110994_).normal(0.0F, 1.0F, 0.0F).endVertex();
   }

   private int getLightColor(BlockAndTintGetter p_110946_, BlockPos p_110947_) {
      int i = LevelRenderer.getLightColor(p_110946_, p_110947_);
      int j = LevelRenderer.getLightColor(p_110946_, p_110947_.above());
      int k = i & 255;
      int l = j & 255;
      int i1 = i >> 16 & 255;
      int j1 = j >> 16 & 255;
      return (k > l ? k : l) | (i1 > j1 ? i1 : j1) << 16;
   }
}
