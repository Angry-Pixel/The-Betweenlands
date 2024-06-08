package net.minecraft.client.renderer.debug;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.Locale;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PathfindingRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final Map<Integer, Path> pathMap = Maps.newHashMap();
   private final Map<Integer, Float> pathMaxDist = Maps.newHashMap();
   private final Map<Integer, Long> creationMap = Maps.newHashMap();
   private static final long TIMEOUT = 5000L;
   private static final float MAX_RENDER_DIST = 80.0F;
   private static final boolean SHOW_OPEN_CLOSED = true;
   private static final boolean SHOW_OPEN_CLOSED_COST_MALUS = false;
   private static final boolean SHOW_OPEN_CLOSED_NODE_TYPE_WITH_TEXT = false;
   private static final boolean SHOW_OPEN_CLOSED_NODE_TYPE_WITH_BOX = true;
   private static final boolean SHOW_GROUND_LABELS = true;
   private static final float TEXT_SCALE = 0.02F;

   public void addPath(int p_113612_, Path p_113613_, float p_113614_) {
      this.pathMap.put(p_113612_, p_113613_);
      this.creationMap.put(p_113612_, Util.getMillis());
      this.pathMaxDist.put(p_113612_, p_113614_);
   }

   public void render(PoseStack p_113629_, MultiBufferSource p_113630_, double p_113631_, double p_113632_, double p_113633_) {
      if (!this.pathMap.isEmpty()) {
         long i = Util.getMillis();

         for(Integer integer : this.pathMap.keySet()) {
            Path path = this.pathMap.get(integer);
            float f = this.pathMaxDist.get(integer);
            renderPath(path, f, true, true, p_113631_, p_113632_, p_113633_);
         }

         for(Integer integer1 : this.creationMap.keySet().toArray(new Integer[0])) {
            if (i - this.creationMap.get(integer1) > 5000L) {
               this.pathMap.remove(integer1);
               this.creationMap.remove(integer1);
            }
         }

      }
   }

   public static void renderPath(Path p_113621_, float p_113622_, boolean p_113623_, boolean p_113624_, double p_113625_, double p_113626_, double p_113627_) {
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.setShaderColor(0.0F, 1.0F, 0.0F, 0.75F);
      RenderSystem.disableTexture();
      RenderSystem.lineWidth(6.0F);
      doRenderPath(p_113621_, p_113622_, p_113623_, p_113624_, p_113625_, p_113626_, p_113627_);
      RenderSystem.enableTexture();
      RenderSystem.disableBlend();
   }

   private static void doRenderPath(Path p_113640_, float p_113641_, boolean p_113642_, boolean p_113643_, double p_113644_, double p_113645_, double p_113646_) {
      renderPathLine(p_113640_, p_113644_, p_113645_, p_113646_);
      BlockPos blockpos = p_113640_.getTarget();
      if (distanceToCamera(blockpos, p_113644_, p_113645_, p_113646_) <= 80.0F) {
         DebugRenderer.renderFilledBox((new AABB((double)((float)blockpos.getX() + 0.25F), (double)((float)blockpos.getY() + 0.25F), (double)blockpos.getZ() + 0.25D, (double)((float)blockpos.getX() + 0.75F), (double)((float)blockpos.getY() + 0.75F), (double)((float)blockpos.getZ() + 0.75F))).move(-p_113644_, -p_113645_, -p_113646_), 0.0F, 1.0F, 0.0F, 0.5F);

         for(int i = 0; i < p_113640_.getNodeCount(); ++i) {
            Node node = p_113640_.getNode(i);
            if (distanceToCamera(node.asBlockPos(), p_113644_, p_113645_, p_113646_) <= 80.0F) {
               float f = i == p_113640_.getNextNodeIndex() ? 1.0F : 0.0F;
               float f1 = i == p_113640_.getNextNodeIndex() ? 0.0F : 1.0F;
               DebugRenderer.renderFilledBox((new AABB((double)((float)node.x + 0.5F - p_113641_), (double)((float)node.y + 0.01F * (float)i), (double)((float)node.z + 0.5F - p_113641_), (double)((float)node.x + 0.5F + p_113641_), (double)((float)node.y + 0.25F + 0.01F * (float)i), (double)((float)node.z + 0.5F + p_113641_))).move(-p_113644_, -p_113645_, -p_113646_), f, 0.0F, f1, 0.5F);
            }
         }
      }

      if (p_113642_) {
         for(Node node2 : p_113640_.getClosedSet()) {
            if (distanceToCamera(node2.asBlockPos(), p_113644_, p_113645_, p_113646_) <= 80.0F) {
               DebugRenderer.renderFilledBox((new AABB((double)((float)node2.x + 0.5F - p_113641_ / 2.0F), (double)((float)node2.y + 0.01F), (double)((float)node2.z + 0.5F - p_113641_ / 2.0F), (double)((float)node2.x + 0.5F + p_113641_ / 2.0F), (double)node2.y + 0.1D, (double)((float)node2.z + 0.5F + p_113641_ / 2.0F))).move(-p_113644_, -p_113645_, -p_113646_), 1.0F, 0.8F, 0.8F, 0.5F);
            }
         }

         for(Node node3 : p_113640_.getOpenSet()) {
            if (distanceToCamera(node3.asBlockPos(), p_113644_, p_113645_, p_113646_) <= 80.0F) {
               DebugRenderer.renderFilledBox((new AABB((double)((float)node3.x + 0.5F - p_113641_ / 2.0F), (double)((float)node3.y + 0.01F), (double)((float)node3.z + 0.5F - p_113641_ / 2.0F), (double)((float)node3.x + 0.5F + p_113641_ / 2.0F), (double)node3.y + 0.1D, (double)((float)node3.z + 0.5F + p_113641_ / 2.0F))).move(-p_113644_, -p_113645_, -p_113646_), 0.8F, 1.0F, 1.0F, 0.5F);
            }
         }
      }

      if (p_113643_) {
         for(int j = 0; j < p_113640_.getNodeCount(); ++j) {
            Node node1 = p_113640_.getNode(j);
            if (distanceToCamera(node1.asBlockPos(), p_113644_, p_113645_, p_113646_) <= 80.0F) {
               DebugRenderer.renderFloatingText(String.format("%s", node1.type), (double)node1.x + 0.5D, (double)node1.y + 0.75D, (double)node1.z + 0.5D, -1, 0.02F, true, 0.0F, true);
               DebugRenderer.renderFloatingText(String.format(Locale.ROOT, "%.2f", node1.costMalus), (double)node1.x + 0.5D, (double)node1.y + 0.25D, (double)node1.z + 0.5D, -1, 0.02F, true, 0.0F, true);
            }
         }
      }

   }

   public static void renderPathLine(Path p_113616_, double p_113617_, double p_113618_, double p_113619_) {
      Tesselator tesselator = Tesselator.getInstance();
      BufferBuilder bufferbuilder = tesselator.getBuilder();
      RenderSystem.setShader(GameRenderer::getPositionColorShader);
      bufferbuilder.begin(VertexFormat.Mode.LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);

      for(int i = 0; i < p_113616_.getNodeCount(); ++i) {
         Node node = p_113616_.getNode(i);
         if (!(distanceToCamera(node.asBlockPos(), p_113617_, p_113618_, p_113619_) > 80.0F)) {
            float f = (float)i / (float)p_113616_.getNodeCount() * 0.33F;
            int j = i == 0 ? 0 : Mth.hsvToRgb(f, 0.9F, 0.9F);
            int k = j >> 16 & 255;
            int l = j >> 8 & 255;
            int i1 = j & 255;
            bufferbuilder.vertex((double)node.x - p_113617_ + 0.5D, (double)node.y - p_113618_ + 0.5D, (double)node.z - p_113619_ + 0.5D).color(k, l, i1, 255).endVertex();
         }
      }

      tesselator.end();
   }

   private static float distanceToCamera(BlockPos p_113635_, double p_113636_, double p_113637_, double p_113638_) {
      return (float)(Math.abs((double)p_113635_.getX() - p_113636_) + Math.abs((double)p_113635_.getY() - p_113637_) + Math.abs((double)p_113635_.getZ() - p_113638_));
   }
}