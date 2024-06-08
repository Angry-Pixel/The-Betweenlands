package net.minecraft.client.renderer.entity;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemRenderer implements ResourceManagerReloadListener {
   public static final ResourceLocation ENCHANT_GLINT_LOCATION = new ResourceLocation("textures/misc/enchanted_item_glint.png");
   private static final Set<Item> IGNORED = Sets.newHashSet(Items.AIR);
   private static final int GUI_SLOT_CENTER_X = 8;
   private static final int GUI_SLOT_CENTER_Y = 8;
   public static final int ITEM_COUNT_BLIT_OFFSET = 200;
   public static final float COMPASS_FOIL_UI_SCALE = 0.5F;
   public static final float COMPASS_FOIL_FIRST_PERSON_SCALE = 0.75F;
   public float blitOffset;
   private final ItemModelShaper itemModelShaper;
   private final TextureManager textureManager;
   private final ItemColors itemColors;
   private final BlockEntityWithoutLevelRenderer blockEntityRenderer;

   public ItemRenderer(TextureManager p_174225_, ModelManager p_174226_, ItemColors p_174227_, BlockEntityWithoutLevelRenderer p_174228_) {
      this.textureManager = p_174225_;
      this.blockEntityRenderer = p_174228_;
      this.itemModelShaper = new net.minecraftforge.client.ItemModelMesherForge(p_174226_);

      for(Item item : Registry.ITEM) {
         if (!IGNORED.contains(item)) {
            this.itemModelShaper.register(item, new ModelResourceLocation(Registry.ITEM.getKey(item), "inventory"));
         }
      }

      this.itemColors = p_174227_;
   }

   public ItemModelShaper getItemModelShaper() {
      return this.itemModelShaper;
   }

   public void renderModelLists(BakedModel p_115190_, ItemStack p_115191_, int p_115192_, int p_115193_, PoseStack p_115194_, VertexConsumer p_115195_) {
      Random random = new Random();
      long i = 42L;

      for(Direction direction : Direction.values()) {
         random.setSeed(42L);
         this.renderQuadList(p_115194_, p_115195_, p_115190_.getQuads((BlockState)null, direction, random), p_115191_, p_115192_, p_115193_);
      }

      random.setSeed(42L);
      this.renderQuadList(p_115194_, p_115195_, p_115190_.getQuads((BlockState)null, (Direction)null, random), p_115191_, p_115192_, p_115193_);
   }

   public void render(ItemStack p_115144_, ItemTransforms.TransformType p_115145_, boolean p_115146_, PoseStack p_115147_, MultiBufferSource p_115148_, int p_115149_, int p_115150_, BakedModel p_115151_) {
      if (!p_115144_.isEmpty()) {
         p_115147_.pushPose();
         boolean flag = p_115145_ == ItemTransforms.TransformType.GUI || p_115145_ == ItemTransforms.TransformType.GROUND || p_115145_ == ItemTransforms.TransformType.FIXED;
         if (flag) {
            if (p_115144_.is(Items.TRIDENT)) {
               p_115151_ = this.itemModelShaper.getModelManager().getModel(new ModelResourceLocation("minecraft:trident#inventory"));
            } else if (p_115144_.is(Items.SPYGLASS)) {
               p_115151_ = this.itemModelShaper.getModelManager().getModel(new ModelResourceLocation("minecraft:spyglass#inventory"));
            }
         }

         p_115151_ = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(p_115147_, p_115151_, p_115145_, p_115146_);
         p_115147_.translate(-0.5D, -0.5D, -0.5D);
         if (!p_115151_.isCustomRenderer() && (!p_115144_.is(Items.TRIDENT) || flag)) {
            boolean flag1;
            if (p_115145_ != ItemTransforms.TransformType.GUI && !p_115145_.firstPerson() && p_115144_.getItem() instanceof BlockItem) {
               Block block = ((BlockItem)p_115144_.getItem()).getBlock();
               flag1 = !(block instanceof HalfTransparentBlock) && !(block instanceof StainedGlassPaneBlock);
            } else {
               flag1 = true;
            }
            if (p_115151_.isLayered()) { net.minecraftforge.client.ForgeHooksClient.drawItemLayered(this, p_115151_, p_115144_, p_115147_, p_115148_, p_115149_, p_115150_, flag1); }
            else {
            RenderType rendertype = ItemBlockRenderTypes.getRenderType(p_115144_, flag1);
            VertexConsumer vertexconsumer;
            if (p_115144_.is(Items.COMPASS) && p_115144_.hasFoil()) {
               p_115147_.pushPose();
               PoseStack.Pose posestack$pose = p_115147_.last();
               if (p_115145_ == ItemTransforms.TransformType.GUI) {
                  posestack$pose.pose().multiply(0.5F);
               } else if (p_115145_.firstPerson()) {
                  posestack$pose.pose().multiply(0.75F);
               }

               if (flag1) {
                  vertexconsumer = getCompassFoilBufferDirect(p_115148_, rendertype, posestack$pose);
               } else {
                  vertexconsumer = getCompassFoilBuffer(p_115148_, rendertype, posestack$pose);
               }

               p_115147_.popPose();
            } else if (flag1) {
               vertexconsumer = getFoilBufferDirect(p_115148_, rendertype, true, p_115144_.hasFoil());
            } else {
               vertexconsumer = getFoilBuffer(p_115148_, rendertype, true, p_115144_.hasFoil());
            }

            this.renderModelLists(p_115151_, p_115144_, p_115149_, p_115150_, p_115147_, vertexconsumer);
            }
         } else {
            net.minecraftforge.client.RenderProperties.get(p_115144_).getItemStackRenderer().renderByItem(p_115144_, p_115145_, p_115147_, p_115148_, p_115149_, p_115150_);
         }

         p_115147_.popPose();
      }
   }

   public static VertexConsumer getArmorFoilBuffer(MultiBufferSource p_115185_, RenderType p_115186_, boolean p_115187_, boolean p_115188_) {
      return p_115188_ ? VertexMultiConsumer.create(p_115185_.getBuffer(p_115187_ ? RenderType.armorGlint() : RenderType.armorEntityGlint()), p_115185_.getBuffer(p_115186_)) : p_115185_.getBuffer(p_115186_);
   }

   public static VertexConsumer getCompassFoilBuffer(MultiBufferSource p_115181_, RenderType p_115182_, PoseStack.Pose p_115183_) {
      return VertexMultiConsumer.create(new SheetedDecalTextureGenerator(p_115181_.getBuffer(RenderType.glint()), p_115183_.pose(), p_115183_.normal()), p_115181_.getBuffer(p_115182_));
   }

   public static VertexConsumer getCompassFoilBufferDirect(MultiBufferSource p_115208_, RenderType p_115209_, PoseStack.Pose p_115210_) {
      return VertexMultiConsumer.create(new SheetedDecalTextureGenerator(p_115208_.getBuffer(RenderType.glintDirect()), p_115210_.pose(), p_115210_.normal()), p_115208_.getBuffer(p_115209_));
   }

   public static VertexConsumer getFoilBuffer(MultiBufferSource p_115212_, RenderType p_115213_, boolean p_115214_, boolean p_115215_) {
      if (p_115215_) {
         return Minecraft.useShaderTransparency() && p_115213_ == Sheets.translucentItemSheet() ? VertexMultiConsumer.create(p_115212_.getBuffer(RenderType.glintTranslucent()), p_115212_.getBuffer(p_115213_)) : VertexMultiConsumer.create(p_115212_.getBuffer(p_115214_ ? RenderType.glint() : RenderType.entityGlint()), p_115212_.getBuffer(p_115213_));
      } else {
         return p_115212_.getBuffer(p_115213_);
      }
   }

   public static VertexConsumer getFoilBufferDirect(MultiBufferSource p_115223_, RenderType p_115224_, boolean p_115225_, boolean p_115226_) {
      return p_115226_ ? VertexMultiConsumer.create(p_115223_.getBuffer(p_115225_ ? RenderType.glintDirect() : RenderType.entityGlintDirect()), p_115223_.getBuffer(p_115224_)) : p_115223_.getBuffer(p_115224_);
   }

   public void renderQuadList(PoseStack p_115163_, VertexConsumer p_115164_, List<BakedQuad> p_115165_, ItemStack p_115166_, int p_115167_, int p_115168_) {
      boolean flag = !p_115166_.isEmpty();
      PoseStack.Pose posestack$pose = p_115163_.last();

      for(BakedQuad bakedquad : p_115165_) {
         int i = -1;
         if (flag && bakedquad.isTinted()) {
            i = this.itemColors.getColor(p_115166_, bakedquad.getTintIndex());
         }

         float f = (float)(i >> 16 & 255) / 255.0F;
         float f1 = (float)(i >> 8 & 255) / 255.0F;
         float f2 = (float)(i & 255) / 255.0F;
         p_115164_.putBulkData(posestack$pose, bakedquad, f, f1, f2, p_115167_, p_115168_, true);
      }

   }

   public BakedModel getModel(ItemStack p_174265_, @Nullable Level p_174266_, @Nullable LivingEntity p_174267_, int p_174268_) {
      BakedModel bakedmodel;
      if (p_174265_.is(Items.TRIDENT)) {
         bakedmodel = this.itemModelShaper.getModelManager().getModel(new ModelResourceLocation("minecraft:trident_in_hand#inventory"));
      } else if (p_174265_.is(Items.SPYGLASS)) {
         bakedmodel = this.itemModelShaper.getModelManager().getModel(new ModelResourceLocation("minecraft:spyglass_in_hand#inventory"));
      } else {
         bakedmodel = this.itemModelShaper.getItemModel(p_174265_);
      }

      ClientLevel clientlevel = p_174266_ instanceof ClientLevel ? (ClientLevel)p_174266_ : null;
      BakedModel bakedmodel1 = bakedmodel.getOverrides().resolve(bakedmodel, p_174265_, clientlevel, p_174267_, p_174268_);
      return bakedmodel1 == null ? this.itemModelShaper.getModelManager().getMissingModel() : bakedmodel1;
   }

   public void renderStatic(ItemStack p_174270_, ItemTransforms.TransformType p_174271_, int p_174272_, int p_174273_, PoseStack p_174274_, MultiBufferSource p_174275_, int p_174276_) {
      this.renderStatic((LivingEntity)null, p_174270_, p_174271_, false, p_174274_, p_174275_, (Level)null, p_174272_, p_174273_, p_174276_);
   }

   public void renderStatic(@Nullable LivingEntity p_174243_, ItemStack p_174244_, ItemTransforms.TransformType p_174245_, boolean p_174246_, PoseStack p_174247_, MultiBufferSource p_174248_, @Nullable Level p_174249_, int p_174250_, int p_174251_, int p_174252_) {
      if (!p_174244_.isEmpty()) {
         BakedModel bakedmodel = this.getModel(p_174244_, p_174249_, p_174243_, p_174252_);
         this.render(p_174244_, p_174245_, p_174246_, p_174247_, p_174248_, p_174250_, p_174251_, bakedmodel);
      }
   }

   public void renderGuiItem(ItemStack p_115124_, int p_115125_, int p_115126_) {
      this.renderGuiItem(p_115124_, p_115125_, p_115126_, this.getModel(p_115124_, (Level)null, (LivingEntity)null, 0));
   }

   protected void renderGuiItem(ItemStack p_115128_, int p_115129_, int p_115130_, BakedModel p_115131_) {
      this.textureManager.getTexture(TextureAtlas.LOCATION_BLOCKS).setFilter(false, false);
      RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      PoseStack posestack = RenderSystem.getModelViewStack();
      posestack.pushPose();
      posestack.translate((double)p_115129_, (double)p_115130_, (double)(100.0F + this.blitOffset));
      posestack.translate(8.0D, 8.0D, 0.0D);
      posestack.scale(1.0F, -1.0F, 1.0F);
      posestack.scale(16.0F, 16.0F, 16.0F);
      RenderSystem.applyModelViewMatrix();
      PoseStack posestack1 = new PoseStack();
      MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
      boolean flag = !p_115131_.usesBlockLight();
      if (flag) {
         Lighting.setupForFlatItems();
      }

      this.render(p_115128_, ItemTransforms.TransformType.GUI, false, posestack1, multibuffersource$buffersource, 15728880, OverlayTexture.NO_OVERLAY, p_115131_);
      multibuffersource$buffersource.endBatch();
      RenderSystem.enableDepthTest();
      if (flag) {
         Lighting.setupFor3DItems();
      }

      posestack.popPose();
      RenderSystem.applyModelViewMatrix();
   }

   public void renderAndDecorateItem(ItemStack p_115204_, int p_115205_, int p_115206_) {
      this.tryRenderGuiItem(Minecraft.getInstance().player, p_115204_, p_115205_, p_115206_, 0);
   }

   public void renderAndDecorateItem(ItemStack p_174254_, int p_174255_, int p_174256_, int p_174257_) {
      this.tryRenderGuiItem(Minecraft.getInstance().player, p_174254_, p_174255_, p_174256_, p_174257_);
   }

   public void renderAndDecorateItem(ItemStack p_174259_, int p_174260_, int p_174261_, int p_174262_, int p_174263_) {
      this.tryRenderGuiItem(Minecraft.getInstance().player, p_174259_, p_174260_, p_174261_, p_174262_, p_174263_);
   }

   public void renderAndDecorateFakeItem(ItemStack p_115219_, int p_115220_, int p_115221_) {
      this.tryRenderGuiItem((LivingEntity)null, p_115219_, p_115220_, p_115221_, 0);
   }

   public void renderAndDecorateItem(LivingEntity p_174230_, ItemStack p_174231_, int p_174232_, int p_174233_, int p_174234_) {
      this.tryRenderGuiItem(p_174230_, p_174231_, p_174232_, p_174233_, p_174234_);
   }

   private void tryRenderGuiItem(@Nullable LivingEntity p_174278_, ItemStack p_174279_, int p_174280_, int p_174281_, int p_174282_) {
      this.tryRenderGuiItem(p_174278_, p_174279_, p_174280_, p_174281_, p_174282_, 0);
   }

   private void tryRenderGuiItem(@Nullable LivingEntity p_174236_, ItemStack p_174237_, int p_174238_, int p_174239_, int p_174240_, int p_174241_) {
      if (!p_174237_.isEmpty()) {
         BakedModel bakedmodel = this.getModel(p_174237_, (Level)null, p_174236_, p_174240_);
         this.blitOffset = bakedmodel.isGui3d() ? this.blitOffset + 50.0F + (float)p_174241_ : this.blitOffset + 50.0F;

         try {
            this.renderGuiItem(p_174237_, p_174238_, p_174239_, bakedmodel);
         } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.forThrowable(throwable, "Rendering item");
            CrashReportCategory crashreportcategory = crashreport.addCategory("Item being rendered");
            crashreportcategory.setDetail("Item Type", () -> {
               return String.valueOf((Object)p_174237_.getItem());
            });
            crashreportcategory.setDetail("Registry Name", () -> String.valueOf(p_174237_.getItem().getRegistryName()));
            crashreportcategory.setDetail("Item Damage", () -> {
               return String.valueOf(p_174237_.getDamageValue());
            });
            crashreportcategory.setDetail("Item NBT", () -> {
               return String.valueOf((Object)p_174237_.getTag());
            });
            crashreportcategory.setDetail("Item Foil", () -> {
               return String.valueOf(p_174237_.hasFoil());
            });
            throw new ReportedException(crashreport);
         }

         this.blitOffset = bakedmodel.isGui3d() ? this.blitOffset - 50.0F - (float)p_174241_ : this.blitOffset - 50.0F;
      }
   }

   public void renderGuiItemDecorations(Font p_115170_, ItemStack p_115171_, int p_115172_, int p_115173_) {
      this.renderGuiItemDecorations(p_115170_, p_115171_, p_115172_, p_115173_, (String)null);
   }

   public void renderGuiItemDecorations(Font p_115175_, ItemStack p_115176_, int p_115177_, int p_115178_, @Nullable String p_115179_) {
      if (!p_115176_.isEmpty()) {
         PoseStack posestack = new PoseStack();
         if (p_115176_.getCount() != 1 || p_115179_ != null) {
            String s = p_115179_ == null ? String.valueOf(p_115176_.getCount()) : p_115179_;
            posestack.translate(0.0D, 0.0D, (double)(this.blitOffset + 200.0F));
            MultiBufferSource.BufferSource multibuffersource$buffersource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
            p_115175_.drawInBatch(s, (float)(p_115177_ + 19 - 2 - p_115175_.width(s)), (float)(p_115178_ + 6 + 3), 16777215, true, posestack.last().pose(), multibuffersource$buffersource, false, 0, 15728880);
            multibuffersource$buffersource.endBatch();
         }

         if (p_115176_.isBarVisible()) {
            RenderSystem.disableDepthTest();
            RenderSystem.disableTexture();
            RenderSystem.disableBlend();
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder bufferbuilder = tesselator.getBuilder();
            int i = p_115176_.getBarWidth();
            int j = p_115176_.getBarColor();
            this.fillRect(bufferbuilder, p_115177_ + 2, p_115178_ + 13, 13, 2, 0, 0, 0, 255);
            this.fillRect(bufferbuilder, p_115177_ + 2, p_115178_ + 13, i, 1, j >> 16 & 255, j >> 8 & 255, j & 255, 255);
            RenderSystem.enableBlend();
            RenderSystem.enableTexture();
            RenderSystem.enableDepthTest();
         }

         LocalPlayer localplayer = Minecraft.getInstance().player;
         float f = localplayer == null ? 0.0F : localplayer.getCooldowns().getCooldownPercent(p_115176_.getItem(), Minecraft.getInstance().getFrameTime());
         if (f > 0.0F) {
            RenderSystem.disableDepthTest();
            RenderSystem.disableTexture();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            Tesselator tesselator1 = Tesselator.getInstance();
            BufferBuilder bufferbuilder1 = tesselator1.getBuilder();
            this.fillRect(bufferbuilder1, p_115177_, p_115178_ + Mth.floor(16.0F * (1.0F - f)), 16, Mth.ceil(16.0F * f), 255, 255, 255, 127);
            RenderSystem.enableTexture();
            RenderSystem.enableDepthTest();
         }

      }
   }

   private void fillRect(BufferBuilder p_115153_, int p_115154_, int p_115155_, int p_115156_, int p_115157_, int p_115158_, int p_115159_, int p_115160_, int p_115161_) {
      RenderSystem.setShader(GameRenderer::getPositionColorShader);
      p_115153_.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
      p_115153_.vertex((double)(p_115154_ + 0), (double)(p_115155_ + 0), 0.0D).color(p_115158_, p_115159_, p_115160_, p_115161_).endVertex();
      p_115153_.vertex((double)(p_115154_ + 0), (double)(p_115155_ + p_115157_), 0.0D).color(p_115158_, p_115159_, p_115160_, p_115161_).endVertex();
      p_115153_.vertex((double)(p_115154_ + p_115156_), (double)(p_115155_ + p_115157_), 0.0D).color(p_115158_, p_115159_, p_115160_, p_115161_).endVertex();
      p_115153_.vertex((double)(p_115154_ + p_115156_), (double)(p_115155_ + 0), 0.0D).color(p_115158_, p_115159_, p_115160_, p_115161_).endVertex();
      p_115153_.end();
      BufferUploader.end(p_115153_);
   }

   public void onResourceManagerReload(ResourceManager p_115105_) {
      this.itemModelShaper.rebuildCache();
   }

   public BlockEntityWithoutLevelRenderer getBlockEntityRenderer() {
       return blockEntityRenderer;
   }
}
