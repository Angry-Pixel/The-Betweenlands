package net.minecraft.client.gui.screens.inventory;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import java.util.List;
import java.util.Random;
import net.minecraft.ChatFormatting;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EnchantmentScreen extends AbstractContainerScreen<EnchantmentMenu> {
   private static final ResourceLocation ENCHANTING_TABLE_LOCATION = new ResourceLocation("textures/gui/container/enchanting_table.png");
   private static final ResourceLocation ENCHANTING_BOOK_LOCATION = new ResourceLocation("textures/entity/enchanting_table_book.png");
   private final Random random = new Random();
   private BookModel bookModel;
   public int time;
   public float flip;
   public float oFlip;
   public float flipT;
   public float flipA;
   public float open;
   public float oOpen;
   private ItemStack last = ItemStack.EMPTY;

   public EnchantmentScreen(EnchantmentMenu p_98754_, Inventory p_98755_, Component p_98756_) {
      super(p_98754_, p_98755_, p_98756_);
   }

   protected void init() {
      super.init();
      this.bookModel = new BookModel(this.minecraft.getEntityModels().bakeLayer(ModelLayers.BOOK));
   }

   public void containerTick() {
      super.containerTick();
      this.tickBook();
   }

   public boolean mouseClicked(double p_98758_, double p_98759_, int p_98760_) {
      int i = (this.width - this.imageWidth) / 2;
      int j = (this.height - this.imageHeight) / 2;

      for(int k = 0; k < 3; ++k) {
         double d0 = p_98758_ - (double)(i + 60);
         double d1 = p_98759_ - (double)(j + 14 + 19 * k);
         if (d0 >= 0.0D && d1 >= 0.0D && d0 < 108.0D && d1 < 19.0D && this.menu.clickMenuButton(this.minecraft.player, k)) {
            this.minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, k);
            return true;
         }
      }

      return super.mouseClicked(p_98758_, p_98759_, p_98760_);
   }

   protected void renderBg(PoseStack p_98762_, float p_98763_, int p_98764_, int p_98765_) {
      Lighting.setupForFlatItems();
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, ENCHANTING_TABLE_LOCATION);
      int i = (this.width - this.imageWidth) / 2;
      int j = (this.height - this.imageHeight) / 2;
      this.blit(p_98762_, i, j, 0, 0, this.imageWidth, this.imageHeight);
      int k = (int)this.minecraft.getWindow().getGuiScale();
      RenderSystem.viewport((this.width - 320) / 2 * k, (this.height - 240) / 2 * k, 320 * k, 240 * k);
      Matrix4f matrix4f = Matrix4f.createTranslateMatrix(-0.34F, 0.23F, 0.0F);
      matrix4f.multiply(Matrix4f.perspective(90.0D, 1.3333334F, 9.0F, 80.0F));
      RenderSystem.backupProjectionMatrix();
      RenderSystem.setProjectionMatrix(matrix4f);
      p_98762_.pushPose();
      PoseStack.Pose posestack$pose = p_98762_.last();
      posestack$pose.pose().setIdentity();
      posestack$pose.normal().setIdentity();
      p_98762_.translate(0.0D, (double)3.3F, 1984.0D);
      float f = 5.0F;
      p_98762_.scale(5.0F, 5.0F, 5.0F);
      p_98762_.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
      p_98762_.mulPose(Vector3f.XP.rotationDegrees(20.0F));
      float f1 = Mth.lerp(p_98763_, this.oOpen, this.open);
      p_98762_.translate((double)((1.0F - f1) * 0.2F), (double)((1.0F - f1) * 0.1F), (double)((1.0F - f1) * 0.25F));
      float f2 = -(1.0F - f1) * 90.0F - 90.0F;
      p_98762_.mulPose(Vector3f.YP.rotationDegrees(f2));
      p_98762_.mulPose(Vector3f.XP.rotationDegrees(180.0F));
      float f3 = Mth.lerp(p_98763_, this.oFlip, this.flip) + 0.25F;
      float f4 = Mth.lerp(p_98763_, this.oFlip, this.flip) + 0.75F;
      f3 = (f3 - (float)Mth.fastFloor((double)f3)) * 1.6F - 0.3F;
      f4 = (f4 - (float)Mth.fastFloor((double)f4)) * 1.6F - 0.3F;
      if (f3 < 0.0F) {
         f3 = 0.0F;
      }

      if (f4 < 0.0F) {
         f4 = 0.0F;
      }

      if (f3 > 1.0F) {
         f3 = 1.0F;
      }

      if (f4 > 1.0F) {
         f4 = 1.0F;
      }

      this.bookModel.setupAnim(0.0F, f3, f4, f1);
      MultiBufferSource.BufferSource multibuffersource$buffersource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
      VertexConsumer vertexconsumer = multibuffersource$buffersource.getBuffer(this.bookModel.renderType(ENCHANTING_BOOK_LOCATION));
      this.bookModel.renderToBuffer(p_98762_, vertexconsumer, 15728880, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
      multibuffersource$buffersource.endBatch();
      p_98762_.popPose();
      RenderSystem.viewport(0, 0, this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight());
      RenderSystem.restoreProjectionMatrix();
      Lighting.setupFor3DItems();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      EnchantmentNames.getInstance().initSeed((long)this.menu.getEnchantmentSeed());
      int l = this.menu.getGoldCount();

      for(int i1 = 0; i1 < 3; ++i1) {
         int j1 = i + 60;
         int k1 = j1 + 20;
         this.setBlitOffset(0);
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderTexture(0, ENCHANTING_TABLE_LOCATION);
         int l1 = (this.menu).costs[i1];
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         if (l1 == 0) {
            this.blit(p_98762_, j1, j + 14 + 19 * i1, 0, 185, 108, 19);
         } else {
            String s = "" + l1;
            int i2 = 86 - this.font.width(s);
            FormattedText formattedtext = EnchantmentNames.getInstance().getRandomName(this.font, i2);
            int j2 = 6839882;
            if (((l < i1 + 1 || this.minecraft.player.experienceLevel < l1) && !this.minecraft.player.getAbilities().instabuild) || this.menu.enchantClue[i1] == -1) { // Forge: render buttons as disabled when enchantable but enchantability not met on lower levels
               this.blit(p_98762_, j1, j + 14 + 19 * i1, 0, 185, 108, 19);
               this.blit(p_98762_, j1 + 1, j + 15 + 19 * i1, 16 * i1, 239, 16, 16);
               this.font.drawWordWrap(formattedtext, k1, j + 16 + 19 * i1, i2, (j2 & 16711422) >> 1);
               j2 = 4226832;
            } else {
               int k2 = p_98764_ - (i + 60);
               int l2 = p_98765_ - (j + 14 + 19 * i1);
               if (k2 >= 0 && l2 >= 0 && k2 < 108 && l2 < 19) {
                  this.blit(p_98762_, j1, j + 14 + 19 * i1, 0, 204, 108, 19);
                  j2 = 16777088;
               } else {
                  this.blit(p_98762_, j1, j + 14 + 19 * i1, 0, 166, 108, 19);
               }

               this.blit(p_98762_, j1 + 1, j + 15 + 19 * i1, 16 * i1, 223, 16, 16);
               this.font.drawWordWrap(formattedtext, k1, j + 16 + 19 * i1, i2, j2);
               j2 = 8453920;
            }

            this.font.drawShadow(p_98762_, s, (float)(k1 + 86 - this.font.width(s)), (float)(j + 16 + 19 * i1 + 7), j2);
         }
      }

   }

   public void render(PoseStack p_98767_, int p_98768_, int p_98769_, float p_98770_) {
      p_98770_ = this.minecraft.getFrameTime();
      this.renderBackground(p_98767_);
      super.render(p_98767_, p_98768_, p_98769_, p_98770_);
      this.renderTooltip(p_98767_, p_98768_, p_98769_);
      boolean flag = this.minecraft.player.getAbilities().instabuild;
      int i = this.menu.getGoldCount();

      for(int j = 0; j < 3; ++j) {
         int k = (this.menu).costs[j];
         Enchantment enchantment = Enchantment.byId((this.menu).enchantClue[j]);
         int l = (this.menu).levelClue[j];
         int i1 = j + 1;
         if (this.isHovering(60, 14 + 19 * j, 108, 17, (double)p_98768_, (double)p_98769_) && k > 0) {
            List<Component> list = Lists.newArrayList();
            list.add((new TranslatableComponent("container.enchant.clue", enchantment == null ? "" : enchantment.getFullname(l))).withStyle(ChatFormatting.WHITE));
            if (enchantment == null) {
               list.add(new TextComponent(""));
               list.add(new TranslatableComponent("forge.container.enchant.limitedEnchantability").withStyle(ChatFormatting.RED));
            } else if (!flag) {
               list.add(TextComponent.EMPTY);
               if (this.minecraft.player.experienceLevel < k) {
                  list.add((new TranslatableComponent("container.enchant.level.requirement", (this.menu).costs[j])).withStyle(ChatFormatting.RED));
               } else {
                  MutableComponent mutablecomponent;
                  if (i1 == 1) {
                     mutablecomponent = new TranslatableComponent("container.enchant.lapis.one");
                  } else {
                     mutablecomponent = new TranslatableComponent("container.enchant.lapis.many", i1);
                  }

                  list.add(mutablecomponent.withStyle(i >= i1 ? ChatFormatting.GRAY : ChatFormatting.RED));
                  MutableComponent mutablecomponent1;
                  if (i1 == 1) {
                     mutablecomponent1 = new TranslatableComponent("container.enchant.level.one");
                  } else {
                     mutablecomponent1 = new TranslatableComponent("container.enchant.level.many", i1);
                  }

                  list.add(mutablecomponent1.withStyle(ChatFormatting.GRAY));
               }
            }

            this.renderComponentTooltip(p_98767_, list, p_98768_, p_98769_);
            break;
         }
      }

   }

   public void tickBook() {
      ItemStack itemstack = this.menu.getSlot(0).getItem();
      if (!ItemStack.matches(itemstack, this.last)) {
         this.last = itemstack;

         do {
            this.flipT += (float)(this.random.nextInt(4) - this.random.nextInt(4));
         } while(this.flip <= this.flipT + 1.0F && this.flip >= this.flipT - 1.0F);
      }

      ++this.time;
      this.oFlip = this.flip;
      this.oOpen = this.open;
      boolean flag = false;

      for(int i = 0; i < 3; ++i) {
         if ((this.menu).costs[i] != 0) {
            flag = true;
         }
      }

      if (flag) {
         this.open += 0.2F;
      } else {
         this.open -= 0.2F;
      }

      this.open = Mth.clamp(this.open, 0.0F, 1.0F);
      float f1 = (this.flipT - this.flip) * 0.4F;
      float f = 0.2F;
      f1 = Mth.clamp(f1, -0.2F, 0.2F);
      this.flipA += (f1 - this.flipA) * 0.9F;
      this.flip += this.flipA;
   }
}
