package net.minecraft.client.renderer.debug;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Collections;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CollisionBoxRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final Minecraft minecraft;
   private double lastUpdateTime = Double.MIN_VALUE;
   private List<VoxelShape> shapes = Collections.emptyList();

   public CollisionBoxRenderer(Minecraft p_113404_) {
      this.minecraft = p_113404_;
   }

   public void render(PoseStack p_113408_, MultiBufferSource p_113409_, double p_113410_, double p_113411_, double p_113412_) {
      double d0 = (double)Util.getNanos();
      if (d0 - this.lastUpdateTime > 1.0E8D) {
         this.lastUpdateTime = d0;
         Entity entity = this.minecraft.gameRenderer.getMainCamera().getEntity();
         this.shapes = ImmutableList.copyOf(entity.level.getCollisions(entity, entity.getBoundingBox().inflate(6.0D)));
      }

      VertexConsumer vertexconsumer = p_113409_.getBuffer(RenderType.lines());

      for(VoxelShape voxelshape : this.shapes) {
         LevelRenderer.renderVoxelShape(p_113408_, vertexconsumer, voxelshape, -p_113410_, -p_113411_, -p_113412_, 1.0F, 1.0F, 1.0F, 1.0F);
      }

   }
}