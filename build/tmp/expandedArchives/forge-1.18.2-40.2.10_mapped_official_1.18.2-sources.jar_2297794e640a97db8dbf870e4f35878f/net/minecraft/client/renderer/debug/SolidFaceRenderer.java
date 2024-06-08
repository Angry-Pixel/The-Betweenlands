package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SolidFaceRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final Minecraft minecraft;

   public SolidFaceRenderer(Minecraft p_113668_) {
      this.minecraft = p_113668_;
   }

   public void render(PoseStack p_113670_, MultiBufferSource p_113671_, double p_113672_, double p_113673_, double p_113674_) {
      BlockGetter blockgetter = this.minecraft.player.level;
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.lineWidth(2.0F);
      RenderSystem.disableTexture();
      RenderSystem.depthMask(false);
      RenderSystem.setShader(GameRenderer::getPositionColorShader);
      BlockPos blockpos = new BlockPos(p_113672_, p_113673_, p_113674_);

      for(BlockPos blockpos1 : BlockPos.betweenClosed(blockpos.offset(-6, -6, -6), blockpos.offset(6, 6, 6))) {
         BlockState blockstate = blockgetter.getBlockState(blockpos1);
         if (!blockstate.is(Blocks.AIR)) {
            VoxelShape voxelshape = blockstate.getShape(blockgetter, blockpos1);

            for(AABB aabb : voxelshape.toAabbs()) {
               AABB aabb1 = aabb.move(blockpos1).inflate(0.002D).move(-p_113672_, -p_113673_, -p_113674_);
               double d0 = aabb1.minX;
               double d1 = aabb1.minY;
               double d2 = aabb1.minZ;
               double d3 = aabb1.maxX;
               double d4 = aabb1.maxY;
               double d5 = aabb1.maxZ;
               float f = 1.0F;
               float f1 = 0.0F;
               float f2 = 0.0F;
               float f3 = 0.5F;
               if (blockstate.isFaceSturdy(blockgetter, blockpos1, Direction.WEST)) {
                  Tesselator tesselator = Tesselator.getInstance();
                  BufferBuilder bufferbuilder = tesselator.getBuilder();
                  bufferbuilder.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
                  bufferbuilder.vertex(d0, d1, d2).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  bufferbuilder.vertex(d0, d1, d5).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  bufferbuilder.vertex(d0, d4, d2).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  bufferbuilder.vertex(d0, d4, d5).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  tesselator.end();
               }

               if (blockstate.isFaceSturdy(blockgetter, blockpos1, Direction.SOUTH)) {
                  Tesselator tesselator1 = Tesselator.getInstance();
                  BufferBuilder bufferbuilder1 = tesselator1.getBuilder();
                  bufferbuilder1.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
                  bufferbuilder1.vertex(d0, d4, d5).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  bufferbuilder1.vertex(d0, d1, d5).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  bufferbuilder1.vertex(d3, d4, d5).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  bufferbuilder1.vertex(d3, d1, d5).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  tesselator1.end();
               }

               if (blockstate.isFaceSturdy(blockgetter, blockpos1, Direction.EAST)) {
                  Tesselator tesselator2 = Tesselator.getInstance();
                  BufferBuilder bufferbuilder2 = tesselator2.getBuilder();
                  bufferbuilder2.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
                  bufferbuilder2.vertex(d3, d1, d5).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  bufferbuilder2.vertex(d3, d1, d2).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  bufferbuilder2.vertex(d3, d4, d5).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  bufferbuilder2.vertex(d3, d4, d2).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  tesselator2.end();
               }

               if (blockstate.isFaceSturdy(blockgetter, blockpos1, Direction.NORTH)) {
                  Tesselator tesselator3 = Tesselator.getInstance();
                  BufferBuilder bufferbuilder3 = tesselator3.getBuilder();
                  bufferbuilder3.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
                  bufferbuilder3.vertex(d3, d4, d2).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  bufferbuilder3.vertex(d3, d1, d2).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  bufferbuilder3.vertex(d0, d4, d2).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  bufferbuilder3.vertex(d0, d1, d2).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  tesselator3.end();
               }

               if (blockstate.isFaceSturdy(blockgetter, blockpos1, Direction.DOWN)) {
                  Tesselator tesselator4 = Tesselator.getInstance();
                  BufferBuilder bufferbuilder4 = tesselator4.getBuilder();
                  bufferbuilder4.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
                  bufferbuilder4.vertex(d0, d1, d2).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  bufferbuilder4.vertex(d3, d1, d2).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  bufferbuilder4.vertex(d0, d1, d5).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  bufferbuilder4.vertex(d3, d1, d5).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  tesselator4.end();
               }

               if (blockstate.isFaceSturdy(blockgetter, blockpos1, Direction.UP)) {
                  Tesselator tesselator5 = Tesselator.getInstance();
                  BufferBuilder bufferbuilder5 = tesselator5.getBuilder();
                  bufferbuilder5.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
                  bufferbuilder5.vertex(d0, d4, d2).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  bufferbuilder5.vertex(d0, d4, d5).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  bufferbuilder5.vertex(d3, d4, d2).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  bufferbuilder5.vertex(d3, d4, d5).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  tesselator5.end();
               }
            }
         }
      }

      RenderSystem.depthMask(true);
      RenderSystem.enableTexture();
      RenderSystem.disableBlend();
   }
}