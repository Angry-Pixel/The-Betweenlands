package net.minecraft.client.renderer.debug;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StructureRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final Minecraft minecraft;
   private final Map<DimensionType, Map<String, BoundingBox>> postMainBoxes = Maps.newIdentityHashMap();
   private final Map<DimensionType, Map<String, BoundingBox>> postPiecesBoxes = Maps.newIdentityHashMap();
   private final Map<DimensionType, Map<String, Boolean>> startPiecesMap = Maps.newIdentityHashMap();
   private static final int MAX_RENDER_DIST = 500;

   public StructureRenderer(Minecraft p_113680_) {
      this.minecraft = p_113680_;
   }

   public void render(PoseStack p_113688_, MultiBufferSource p_113689_, double p_113690_, double p_113691_, double p_113692_) {
      Camera camera = this.minecraft.gameRenderer.getMainCamera();
      LevelAccessor levelaccessor = this.minecraft.level;
      DimensionType dimensiontype = levelaccessor.dimensionType();
      BlockPos blockpos = new BlockPos(camera.getPosition().x, 0.0D, camera.getPosition().z);
      VertexConsumer vertexconsumer = p_113689_.getBuffer(RenderType.lines());
      if (this.postMainBoxes.containsKey(dimensiontype)) {
         for(BoundingBox boundingbox : this.postMainBoxes.get(dimensiontype).values()) {
            if (blockpos.closerThan(boundingbox.getCenter(), 500.0D)) {
               LevelRenderer.renderLineBox(p_113688_, vertexconsumer, (double)boundingbox.minX() - p_113690_, (double)boundingbox.minY() - p_113691_, (double)boundingbox.minZ() - p_113692_, (double)(boundingbox.maxX() + 1) - p_113690_, (double)(boundingbox.maxY() + 1) - p_113691_, (double)(boundingbox.maxZ() + 1) - p_113692_, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F);
            }
         }
      }

      if (this.postPiecesBoxes.containsKey(dimensiontype)) {
         for(Entry<String, BoundingBox> entry : this.postPiecesBoxes.get(dimensiontype).entrySet()) {
            String s = entry.getKey();
            BoundingBox boundingbox1 = entry.getValue();
            Boolean obool = this.startPiecesMap.get(dimensiontype).get(s);
            if (blockpos.closerThan(boundingbox1.getCenter(), 500.0D)) {
               if (obool) {
                  LevelRenderer.renderLineBox(p_113688_, vertexconsumer, (double)boundingbox1.minX() - p_113690_, (double)boundingbox1.minY() - p_113691_, (double)boundingbox1.minZ() - p_113692_, (double)(boundingbox1.maxX() + 1) - p_113690_, (double)(boundingbox1.maxY() + 1) - p_113691_, (double)(boundingbox1.maxZ() + 1) - p_113692_, 0.0F, 1.0F, 0.0F, 1.0F, 0.0F, 1.0F, 0.0F);
               } else {
                  LevelRenderer.renderLineBox(p_113688_, vertexconsumer, (double)boundingbox1.minX() - p_113690_, (double)boundingbox1.minY() - p_113691_, (double)boundingbox1.minZ() - p_113692_, (double)(boundingbox1.maxX() + 1) - p_113690_, (double)(boundingbox1.maxY() + 1) - p_113691_, (double)(boundingbox1.maxZ() + 1) - p_113692_, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F);
               }
            }
         }
      }

   }

   public void addBoundingBox(BoundingBox p_113683_, List<BoundingBox> p_113684_, List<Boolean> p_113685_, DimensionType p_113686_) {
      if (!this.postMainBoxes.containsKey(p_113686_)) {
         this.postMainBoxes.put(p_113686_, Maps.newHashMap());
      }

      if (!this.postPiecesBoxes.containsKey(p_113686_)) {
         this.postPiecesBoxes.put(p_113686_, Maps.newHashMap());
         this.startPiecesMap.put(p_113686_, Maps.newHashMap());
      }

      this.postMainBoxes.get(p_113686_).put(p_113683_.toString(), p_113683_);

      for(int i = 0; i < p_113684_.size(); ++i) {
         BoundingBox boundingbox = p_113684_.get(i);
         Boolean obool = p_113685_.get(i);
         this.postPiecesBoxes.get(p_113686_).put(boundingbox.toString(), boundingbox);
         this.startPiecesMap.get(p_113686_).put(boundingbox.toString(), obool);
      }

   }

   public void clear() {
      this.postMainBoxes.clear();
      this.postPiecesBoxes.clear();
      this.startPiecesMap.clear();
   }
}