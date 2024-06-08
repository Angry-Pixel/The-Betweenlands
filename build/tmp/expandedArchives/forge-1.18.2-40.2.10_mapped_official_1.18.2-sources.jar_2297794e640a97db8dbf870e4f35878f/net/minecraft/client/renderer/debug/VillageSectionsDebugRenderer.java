package net.minecraft.client.renderer.debug;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Set;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VillageSectionsDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   private static final int MAX_RENDER_DIST_FOR_VILLAGE_SECTIONS = 60;
   private final Set<SectionPos> villageSections = Sets.newHashSet();

   VillageSectionsDebugRenderer() {
   }

   public void clear() {
      this.villageSections.clear();
   }

   public void setVillageSection(SectionPos p_113710_) {
      this.villageSections.add(p_113710_);
   }

   public void setNotVillageSection(SectionPos p_113712_) {
      this.villageSections.remove(p_113712_);
   }

   public void render(PoseStack p_113701_, MultiBufferSource p_113702_, double p_113703_, double p_113704_, double p_113705_) {
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableTexture();
      this.doRender(p_113703_, p_113704_, p_113705_);
      RenderSystem.enableTexture();
      RenderSystem.disableBlend();
   }

   private void doRender(double p_113697_, double p_113698_, double p_113699_) {
      BlockPos blockpos = new BlockPos(p_113697_, p_113698_, p_113699_);
      this.villageSections.forEach((p_113708_) -> {
         if (blockpos.closerThan(p_113708_.center(), 60.0D)) {
            highlightVillageSection(p_113708_);
         }

      });
   }

   private static void highlightVillageSection(SectionPos p_113714_) {
      float f = 1.0F;
      BlockPos blockpos = p_113714_.center();
      BlockPos blockpos1 = blockpos.offset(-1.0D, -1.0D, -1.0D);
      BlockPos blockpos2 = blockpos.offset(1.0D, 1.0D, 1.0D);
      DebugRenderer.renderFilledBox(blockpos1, blockpos2, 0.2F, 1.0F, 0.2F, 0.15F);
   }
}