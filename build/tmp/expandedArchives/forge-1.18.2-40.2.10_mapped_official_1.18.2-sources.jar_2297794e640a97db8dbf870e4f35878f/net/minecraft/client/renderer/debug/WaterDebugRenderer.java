package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WaterDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final Minecraft minecraft;

   public WaterDebugRenderer(Minecraft p_113717_) {
      this.minecraft = p_113717_;
   }

   public void render(PoseStack p_113719_, MultiBufferSource p_113720_, double p_113721_, double p_113722_, double p_113723_) {
      BlockPos blockpos = this.minecraft.player.blockPosition();
      LevelReader levelreader = this.minecraft.player.level;
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.setShaderColor(0.0F, 1.0F, 0.0F, 0.75F);
      RenderSystem.disableTexture();
      RenderSystem.lineWidth(6.0F);

      for(BlockPos blockpos1 : BlockPos.betweenClosed(blockpos.offset(-10, -10, -10), blockpos.offset(10, 10, 10))) {
         FluidState fluidstate = levelreader.getFluidState(blockpos1);
         if (fluidstate.is(FluidTags.WATER)) {
            double d0 = (double)((float)blockpos1.getY() + fluidstate.getHeight(levelreader, blockpos1));
            DebugRenderer.renderFilledBox((new AABB((double)((float)blockpos1.getX() + 0.01F), (double)((float)blockpos1.getY() + 0.01F), (double)((float)blockpos1.getZ() + 0.01F), (double)((float)blockpos1.getX() + 0.99F), d0, (double)((float)blockpos1.getZ() + 0.99F))).move(-p_113721_, -p_113722_, -p_113723_), 1.0F, 1.0F, 1.0F, 0.2F);
         }
      }

      for(BlockPos blockpos2 : BlockPos.betweenClosed(blockpos.offset(-10, -10, -10), blockpos.offset(10, 10, 10))) {
         FluidState fluidstate1 = levelreader.getFluidState(blockpos2);
         if (fluidstate1.is(FluidTags.WATER)) {
            DebugRenderer.renderFloatingText(String.valueOf(fluidstate1.getAmount()), (double)blockpos2.getX() + 0.5D, (double)((float)blockpos2.getY() + fluidstate1.getHeight(levelreader, blockpos2)), (double)blockpos2.getZ() + 0.5D, -16777216);
         }
      }

      RenderSystem.enableTexture();
      RenderSystem.disableBlend();
   }
}