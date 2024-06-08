package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.CartographyTableMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CartographyTableScreen extends AbstractContainerScreen<CartographyTableMenu> {
   private static final ResourceLocation BG_LOCATION = new ResourceLocation("textures/gui/container/cartography_table.png");

   public CartographyTableScreen(CartographyTableMenu p_98349_, Inventory p_98350_, Component p_98351_) {
      super(p_98349_, p_98350_, p_98351_);
      this.titleLabelY -= 2;
   }

   public void render(PoseStack p_98363_, int p_98364_, int p_98365_, float p_98366_) {
      super.render(p_98363_, p_98364_, p_98365_, p_98366_);
      this.renderTooltip(p_98363_, p_98364_, p_98365_);
   }

   protected void renderBg(PoseStack p_98358_, float p_98359_, int p_98360_, int p_98361_) {
      this.renderBackground(p_98358_);
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, BG_LOCATION);
      int i = this.leftPos;
      int j = this.topPos;
      this.blit(p_98358_, i, j, 0, 0, this.imageWidth, this.imageHeight);
      ItemStack itemstack = this.menu.getSlot(1).getItem();
      boolean flag = itemstack.is(Items.MAP);
      boolean flag1 = itemstack.is(Items.PAPER);
      boolean flag2 = itemstack.is(Items.GLASS_PANE);
      ItemStack itemstack1 = this.menu.getSlot(0).getItem();
      boolean flag3 = false;
      Integer integer;
      MapItemSavedData mapitemsaveddata;
      if (itemstack1.is(Items.FILLED_MAP)) {
         integer = MapItem.getMapId(itemstack1);
         mapitemsaveddata = MapItem.getSavedData(integer, this.minecraft.level);
         if (mapitemsaveddata != null) {
            if (mapitemsaveddata.locked) {
               flag3 = true;
               if (flag1 || flag2) {
                  this.blit(p_98358_, i + 35, j + 31, this.imageWidth + 50, 132, 28, 21);
               }
            }

            if (flag1 && mapitemsaveddata.scale >= 4) {
               flag3 = true;
               this.blit(p_98358_, i + 35, j + 31, this.imageWidth + 50, 132, 28, 21);
            }
         }
      } else {
         integer = null;
         mapitemsaveddata = null;
      }

      this.renderResultingMap(p_98358_, integer, mapitemsaveddata, flag, flag1, flag2, flag3);
   }

   private void renderResultingMap(PoseStack p_169711_, @Nullable Integer p_169712_, @Nullable MapItemSavedData p_169713_, boolean p_169714_, boolean p_169715_, boolean p_169716_, boolean p_169717_) {
      int i = this.leftPos;
      int j = this.topPos;
      if (p_169715_ && !p_169717_) {
         this.blit(p_169711_, i + 67, j + 13, this.imageWidth, 66, 66, 66);
         this.renderMap(p_169711_, p_169712_, p_169713_, i + 85, j + 31, 0.226F);
      } else if (p_169714_) {
         this.blit(p_169711_, i + 67 + 16, j + 13, this.imageWidth, 132, 50, 66);
         this.renderMap(p_169711_, p_169712_, p_169713_, i + 86, j + 16, 0.34F);
         RenderSystem.setShaderTexture(0, BG_LOCATION);
         p_169711_.pushPose();
         p_169711_.translate(0.0D, 0.0D, 1.0D);
         this.blit(p_169711_, i + 67, j + 13 + 16, this.imageWidth, 132, 50, 66);
         this.renderMap(p_169711_, p_169712_, p_169713_, i + 70, j + 32, 0.34F);
         p_169711_.popPose();
      } else if (p_169716_) {
         this.blit(p_169711_, i + 67, j + 13, this.imageWidth, 0, 66, 66);
         this.renderMap(p_169711_, p_169712_, p_169713_, i + 71, j + 17, 0.45F);
         RenderSystem.setShaderTexture(0, BG_LOCATION);
         p_169711_.pushPose();
         p_169711_.translate(0.0D, 0.0D, 1.0D);
         this.blit(p_169711_, i + 66, j + 12, 0, this.imageHeight, 66, 66);
         p_169711_.popPose();
      } else {
         this.blit(p_169711_, i + 67, j + 13, this.imageWidth, 0, 66, 66);
         this.renderMap(p_169711_, p_169712_, p_169713_, i + 71, j + 17, 0.45F);
      }

   }

   private void renderMap(PoseStack p_169704_, @Nullable Integer p_169705_, @Nullable MapItemSavedData p_169706_, int p_169707_, int p_169708_, float p_169709_) {
      if (p_169705_ != null && p_169706_ != null) {
         p_169704_.pushPose();
         p_169704_.translate((double)p_169707_, (double)p_169708_, 1.0D);
         p_169704_.scale(p_169709_, p_169709_, 1.0F);
         MultiBufferSource.BufferSource multibuffersource$buffersource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
         this.minecraft.gameRenderer.getMapRenderer().render(p_169704_, multibuffersource$buffersource, p_169705_, p_169706_, true, 15728880);
         multibuffersource$buffersource.endBatch();
         p_169704_.popPose();
      }

   }
}