package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Vector3f;
import java.util.List;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.WallBannerBlock;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BannerRenderer implements BlockEntityRenderer<BannerBlockEntity> {
   private static final int BANNER_WIDTH = 20;
   private static final int BANNER_HEIGHT = 40;
   private static final int MAX_PATTERNS = 16;
   public static final String FLAG = "flag";
   private static final String POLE = "pole";
   private static final String BAR = "bar";
   private final ModelPart flag;
   private final ModelPart pole;
   private final ModelPart bar;

   public BannerRenderer(BlockEntityRendererProvider.Context p_173521_) {
      ModelPart modelpart = p_173521_.bakeLayer(ModelLayers.BANNER);
      this.flag = modelpart.getChild("flag");
      this.pole = modelpart.getChild("pole");
      this.bar = modelpart.getChild("bar");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      partdefinition.addOrReplaceChild("flag", CubeListBuilder.create().texOffs(0, 0).addBox(-10.0F, 0.0F, -2.0F, 20.0F, 40.0F, 1.0F), PartPose.ZERO);
      partdefinition.addOrReplaceChild("pole", CubeListBuilder.create().texOffs(44, 0).addBox(-1.0F, -30.0F, -1.0F, 2.0F, 42.0F, 2.0F), PartPose.ZERO);
      partdefinition.addOrReplaceChild("bar", CubeListBuilder.create().texOffs(0, 42).addBox(-10.0F, -32.0F, -1.0F, 20.0F, 2.0F, 2.0F), PartPose.ZERO);
      return LayerDefinition.create(meshdefinition, 64, 64);
   }

   public void render(BannerBlockEntity p_112052_, float p_112053_, PoseStack p_112054_, MultiBufferSource p_112055_, int p_112056_, int p_112057_) {
      List<Pair<BannerPattern, DyeColor>> list = p_112052_.getPatterns();
      float f = 0.6666667F;
      boolean flag = p_112052_.getLevel() == null;
      p_112054_.pushPose();
      long i;
      if (flag) {
         i = 0L;
         p_112054_.translate(0.5D, 0.5D, 0.5D);
         this.pole.visible = true;
      } else {
         i = p_112052_.getLevel().getGameTime();
         BlockState blockstate = p_112052_.getBlockState();
         if (blockstate.getBlock() instanceof BannerBlock) {
            p_112054_.translate(0.5D, 0.5D, 0.5D);
            float f1 = (float)(-blockstate.getValue(BannerBlock.ROTATION) * 360) / 16.0F;
            p_112054_.mulPose(Vector3f.YP.rotationDegrees(f1));
            this.pole.visible = true;
         } else {
            p_112054_.translate(0.5D, (double)-0.16666667F, 0.5D);
            float f3 = -blockstate.getValue(WallBannerBlock.FACING).toYRot();
            p_112054_.mulPose(Vector3f.YP.rotationDegrees(f3));
            p_112054_.translate(0.0D, -0.3125D, -0.4375D);
            this.pole.visible = false;
         }
      }

      p_112054_.pushPose();
      p_112054_.scale(0.6666667F, -0.6666667F, -0.6666667F);
      VertexConsumer vertexconsumer = ModelBakery.BANNER_BASE.buffer(p_112055_, RenderType::entitySolid);
      this.pole.render(p_112054_, vertexconsumer, p_112056_, p_112057_);
      this.bar.render(p_112054_, vertexconsumer, p_112056_, p_112057_);
      BlockPos blockpos = p_112052_.getBlockPos();
      float f2 = ((float)Math.floorMod((long)(blockpos.getX() * 7 + blockpos.getY() * 9 + blockpos.getZ() * 13) + i, 100L) + p_112053_) / 100.0F;
      this.flag.xRot = (-0.0125F + 0.01F * Mth.cos(((float)Math.PI * 2F) * f2)) * (float)Math.PI;
      this.flag.y = -32.0F;
      renderPatterns(p_112054_, p_112055_, p_112056_, p_112057_, this.flag, ModelBakery.BANNER_BASE, true, list);
      p_112054_.popPose();
      p_112054_.popPose();
   }

   public static void renderPatterns(PoseStack p_112066_, MultiBufferSource p_112067_, int p_112068_, int p_112069_, ModelPart p_112070_, Material p_112071_, boolean p_112072_, List<Pair<BannerPattern, DyeColor>> p_112073_) {
      renderPatterns(p_112066_, p_112067_, p_112068_, p_112069_, p_112070_, p_112071_, p_112072_, p_112073_, false);
   }

   public static void renderPatterns(PoseStack p_112075_, MultiBufferSource p_112076_, int p_112077_, int p_112078_, ModelPart p_112079_, Material p_112080_, boolean p_112081_, List<Pair<BannerPattern, DyeColor>> p_112082_, boolean p_112083_) {
      p_112079_.render(p_112075_, p_112080_.buffer(p_112076_, RenderType::entitySolid, p_112083_), p_112077_, p_112078_);

      for(int i = 0; i < 17 && i < p_112082_.size(); ++i) {
         Pair<BannerPattern, DyeColor> pair = p_112082_.get(i);
         float[] afloat = pair.getSecond().getTextureDiffuseColors();
         BannerPattern bannerpattern = pair.getFirst();
         Material material = p_112081_ ? Sheets.getBannerMaterial(bannerpattern) : Sheets.getShieldMaterial(bannerpattern);
         p_112079_.render(p_112075_, material.buffer(p_112076_, RenderType::entityNoOutline), p_112077_, p_112078_, afloat[0], afloat[1], afloat[2], 1.0F);
      }

   }
}