package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ServerboundSelectTradePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MerchantScreen extends AbstractContainerScreen<MerchantMenu> {
   private static final ResourceLocation VILLAGER_LOCATION = new ResourceLocation("textures/gui/container/villager2.png");
   private static final int TEXTURE_WIDTH = 512;
   private static final int TEXTURE_HEIGHT = 256;
   private static final int MERCHANT_MENU_PART_X = 99;
   private static final int PROGRESS_BAR_X = 136;
   private static final int PROGRESS_BAR_Y = 16;
   private static final int SELL_ITEM_1_X = 5;
   private static final int SELL_ITEM_2_X = 35;
   private static final int BUY_ITEM_X = 68;
   private static final int LABEL_Y = 6;
   private static final int NUMBER_OF_OFFER_BUTTONS = 7;
   private static final int TRADE_BUTTON_X = 5;
   private static final int TRADE_BUTTON_HEIGHT = 20;
   private static final int TRADE_BUTTON_WIDTH = 89;
   private static final int SCROLLER_HEIGHT = 27;
   private static final int SCROLLER_WIDTH = 6;
   private static final int SCROLL_BAR_HEIGHT = 139;
   private static final int SCROLL_BAR_TOP_POS_Y = 18;
   private static final int SCROLL_BAR_START_X = 94;
   private static final Component TRADES_LABEL = new TranslatableComponent("merchant.trades");
   private static final Component LEVEL_SEPARATOR = new TextComponent(" - ");
   private static final Component DEPRECATED_TOOLTIP = new TranslatableComponent("merchant.deprecated");
   private int shopItem;
   private final MerchantScreen.TradeOfferButton[] tradeOfferButtons = new MerchantScreen.TradeOfferButton[7];
   int scrollOff;
   private boolean isDragging;

   public MerchantScreen(MerchantMenu p_99123_, Inventory p_99124_, Component p_99125_) {
      super(p_99123_, p_99124_, p_99125_);
      this.imageWidth = 276;
      this.inventoryLabelX = 107;
   }

   private void postButtonClick() {
      this.menu.setSelectionHint(this.shopItem);
      this.menu.tryMoveItems(this.shopItem);
      this.minecraft.getConnection().send(new ServerboundSelectTradePacket(this.shopItem));
   }

   protected void init() {
      super.init();
      int i = (this.width - this.imageWidth) / 2;
      int j = (this.height - this.imageHeight) / 2;
      int k = j + 16 + 2;

      for(int l = 0; l < 7; ++l) {
         this.tradeOfferButtons[l] = this.addRenderableWidget(new MerchantScreen.TradeOfferButton(i + 5, k, l, (p_99174_) -> {
            if (p_99174_ instanceof MerchantScreen.TradeOfferButton) {
               this.shopItem = ((MerchantScreen.TradeOfferButton)p_99174_).getIndex() + this.scrollOff;
               this.postButtonClick();
            }

         }));
         k += 20;
      }

   }

   protected void renderLabels(PoseStack p_99185_, int p_99186_, int p_99187_) {
      int i = this.menu.getTraderLevel();
      if (i > 0 && i <= 5 && this.menu.showProgressBar()) {
         Component component = this.title.copy().append(LEVEL_SEPARATOR).append(new TranslatableComponent("merchant.level." + i));
         int j = this.font.width(component);
         int k = 49 + this.imageWidth / 2 - j / 2;
         this.font.draw(p_99185_, component, (float)k, 6.0F, 4210752);
      } else {
         this.font.draw(p_99185_, this.title, (float)(49 + this.imageWidth / 2 - this.font.width(this.title) / 2), 6.0F, 4210752);
      }

      this.font.draw(p_99185_, this.playerInventoryTitle, (float)this.inventoryLabelX, (float)this.inventoryLabelY, 4210752);
      int l = this.font.width(TRADES_LABEL);
      this.font.draw(p_99185_, TRADES_LABEL, (float)(5 - l / 2 + 48), 6.0F, 4210752);
   }

   protected void renderBg(PoseStack p_99143_, float p_99144_, int p_99145_, int p_99146_) {
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, VILLAGER_LOCATION);
      int i = (this.width - this.imageWidth) / 2;
      int j = (this.height - this.imageHeight) / 2;
      blit(p_99143_, i, j, this.getBlitOffset(), 0.0F, 0.0F, this.imageWidth, this.imageHeight, 512, 256);
      MerchantOffers merchantoffers = this.menu.getOffers();
      if (!merchantoffers.isEmpty()) {
         int k = this.shopItem;
         if (k < 0 || k >= merchantoffers.size()) {
            return;
         }

         MerchantOffer merchantoffer = merchantoffers.get(k);
         if (merchantoffer.isOutOfStock()) {
            RenderSystem.setShaderTexture(0, VILLAGER_LOCATION);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            blit(p_99143_, this.leftPos + 83 + 99, this.topPos + 35, this.getBlitOffset(), 311.0F, 0.0F, 28, 21, 512, 256);
         }
      }

   }

   private void renderProgressBar(PoseStack p_99153_, int p_99154_, int p_99155_, MerchantOffer p_99156_) {
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderTexture(0, VILLAGER_LOCATION);
      int i = this.menu.getTraderLevel();
      int j = this.menu.getTraderXp();
      if (i < 5) {
         blit(p_99153_, p_99154_ + 136, p_99155_ + 16, this.getBlitOffset(), 0.0F, 186.0F, 102, 5, 512, 256);
         int k = VillagerData.getMinXpPerLevel(i);
         if (j >= k && VillagerData.canLevelUp(i)) {
            int l = 100;
            float f = 100.0F / (float)(VillagerData.getMaxXpPerLevel(i) - k);
            int i1 = Math.min(Mth.floor(f * (float)(j - k)), 100);
            blit(p_99153_, p_99154_ + 136, p_99155_ + 16, this.getBlitOffset(), 0.0F, 191.0F, i1 + 1, 5, 512, 256);
            int j1 = this.menu.getFutureTraderXp();
            if (j1 > 0) {
               int k1 = Math.min(Mth.floor((float)j1 * f), 100 - i1);
               blit(p_99153_, p_99154_ + 136 + i1 + 1, p_99155_ + 16 + 1, this.getBlitOffset(), 2.0F, 182.0F, k1, 3, 512, 256);
            }

         }
      }
   }

   private void renderScroller(PoseStack p_99158_, int p_99159_, int p_99160_, MerchantOffers p_99161_) {
      int i = p_99161_.size() + 1 - 7;
      if (i > 1) {
         int j = 139 - (27 + (i - 1) * 139 / i);
         int k = 1 + j / i + 139 / i;
         int l = 113;
         int i1 = Math.min(113, this.scrollOff * k);
         if (this.scrollOff == i - 1) {
            i1 = 113;
         }

         blit(p_99158_, p_99159_ + 94, p_99160_ + 18 + i1, this.getBlitOffset(), 0.0F, 199.0F, 6, 27, 512, 256);
      } else {
         blit(p_99158_, p_99159_ + 94, p_99160_ + 18, this.getBlitOffset(), 6.0F, 199.0F, 6, 27, 512, 256);
      }

   }

   public void render(PoseStack p_99148_, int p_99149_, int p_99150_, float p_99151_) {
      this.renderBackground(p_99148_);
      super.render(p_99148_, p_99149_, p_99150_, p_99151_);
      MerchantOffers merchantoffers = this.menu.getOffers();
      if (!merchantoffers.isEmpty()) {
         int i = (this.width - this.imageWidth) / 2;
         int j = (this.height - this.imageHeight) / 2;
         int k = j + 16 + 1;
         int l = i + 5 + 5;
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderTexture(0, VILLAGER_LOCATION);
         this.renderScroller(p_99148_, i, j, merchantoffers);
         int i1 = 0;

         for(MerchantOffer merchantoffer : merchantoffers) {
            if (this.canScroll(merchantoffers.size()) && (i1 < this.scrollOff || i1 >= 7 + this.scrollOff)) {
               ++i1;
            } else {
               ItemStack itemstack = merchantoffer.getBaseCostA();
               ItemStack itemstack1 = merchantoffer.getCostA();
               ItemStack itemstack2 = merchantoffer.getCostB();
               ItemStack itemstack3 = merchantoffer.getResult();
               this.itemRenderer.blitOffset = 100.0F;
               int j1 = k + 2;
               this.renderAndDecorateCostA(p_99148_, itemstack1, itemstack, l, j1);
               if (!itemstack2.isEmpty()) {
                  this.itemRenderer.renderAndDecorateFakeItem(itemstack2, i + 5 + 35, j1);
                  this.itemRenderer.renderGuiItemDecorations(this.font, itemstack2, i + 5 + 35, j1);
               }

               this.renderButtonArrows(p_99148_, merchantoffer, i, j1);
               this.itemRenderer.renderAndDecorateFakeItem(itemstack3, i + 5 + 68, j1);
               this.itemRenderer.renderGuiItemDecorations(this.font, itemstack3, i + 5 + 68, j1);
               this.itemRenderer.blitOffset = 0.0F;
               k += 20;
               ++i1;
            }
         }

         int k1 = this.shopItem;
         MerchantOffer merchantoffer1 = merchantoffers.get(k1);
         if (this.menu.showProgressBar()) {
            this.renderProgressBar(p_99148_, i, j, merchantoffer1);
         }

         if (merchantoffer1.isOutOfStock() && this.isHovering(186, 35, 22, 21, (double)p_99149_, (double)p_99150_) && this.menu.canRestock()) {
            this.renderTooltip(p_99148_, DEPRECATED_TOOLTIP, p_99149_, p_99150_);
         }

         for(MerchantScreen.TradeOfferButton merchantscreen$tradeofferbutton : this.tradeOfferButtons) {
            if (merchantscreen$tradeofferbutton.isHoveredOrFocused()) {
               merchantscreen$tradeofferbutton.renderToolTip(p_99148_, p_99149_, p_99150_);
            }

            merchantscreen$tradeofferbutton.visible = merchantscreen$tradeofferbutton.index < this.menu.getOffers().size();
         }

         RenderSystem.enableDepthTest();
      }

      this.renderTooltip(p_99148_, p_99149_, p_99150_);
   }

   private void renderButtonArrows(PoseStack p_99169_, MerchantOffer p_99170_, int p_99171_, int p_99172_) {
      RenderSystem.enableBlend();
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderTexture(0, VILLAGER_LOCATION);
      if (p_99170_.isOutOfStock()) {
         blit(p_99169_, p_99171_ + 5 + 35 + 20, p_99172_ + 3, this.getBlitOffset(), 25.0F, 171.0F, 10, 9, 512, 256);
      } else {
         blit(p_99169_, p_99171_ + 5 + 35 + 20, p_99172_ + 3, this.getBlitOffset(), 15.0F, 171.0F, 10, 9, 512, 256);
      }

   }

   private void renderAndDecorateCostA(PoseStack p_99163_, ItemStack p_99164_, ItemStack p_99165_, int p_99166_, int p_99167_) {
      this.itemRenderer.renderAndDecorateFakeItem(p_99164_, p_99166_, p_99167_);
      if (p_99165_.getCount() == p_99164_.getCount()) {
         this.itemRenderer.renderGuiItemDecorations(this.font, p_99164_, p_99166_, p_99167_);
      } else {
         this.itemRenderer.renderGuiItemDecorations(this.font, p_99165_, p_99166_, p_99167_, p_99165_.getCount() == 1 ? "1" : null);
         this.itemRenderer.renderGuiItemDecorations(this.font, p_99164_, p_99166_ + 14, p_99167_, p_99164_.getCount() == 1 ? "1" : null);
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderTexture(0, VILLAGER_LOCATION);
         this.setBlitOffset(this.getBlitOffset() + 300);
         blit(p_99163_, p_99166_ + 7, p_99167_ + 12, this.getBlitOffset(), 0.0F, 176.0F, 9, 2, 512, 256);
         this.setBlitOffset(this.getBlitOffset() - 300);
      }

   }

   private boolean canScroll(int p_99141_) {
      return p_99141_ > 7;
   }

   public boolean mouseScrolled(double p_99127_, double p_99128_, double p_99129_) {
      int i = this.menu.getOffers().size();
      if (this.canScroll(i)) {
         int j = i - 7;
         this.scrollOff = Mth.clamp((int)((double)this.scrollOff - p_99129_), 0, j);
      }

      return true;
   }

   public boolean mouseDragged(double p_99135_, double p_99136_, int p_99137_, double p_99138_, double p_99139_) {
      int i = this.menu.getOffers().size();
      if (this.isDragging) {
         int j = this.topPos + 18;
         int k = j + 139;
         int l = i - 7;
         float f = ((float)p_99136_ - (float)j - 13.5F) / ((float)(k - j) - 27.0F);
         f = f * (float)l + 0.5F;
         this.scrollOff = Mth.clamp((int)f, 0, l);
         return true;
      } else {
         return super.mouseDragged(p_99135_, p_99136_, p_99137_, p_99138_, p_99139_);
      }
   }

   public boolean mouseClicked(double p_99131_, double p_99132_, int p_99133_) {
      this.isDragging = false;
      int i = (this.width - this.imageWidth) / 2;
      int j = (this.height - this.imageHeight) / 2;
      if (this.canScroll(this.menu.getOffers().size()) && p_99131_ > (double)(i + 94) && p_99131_ < (double)(i + 94 + 6) && p_99132_ > (double)(j + 18) && p_99132_ <= (double)(j + 18 + 139 + 1)) {
         this.isDragging = true;
      }

      return super.mouseClicked(p_99131_, p_99132_, p_99133_);
   }

   @OnlyIn(Dist.CLIENT)
   class TradeOfferButton extends Button {
      final int index;

      public TradeOfferButton(int p_99205_, int p_99206_, int p_99207_, Button.OnPress p_99208_) {
         super(p_99205_, p_99206_, 89, 20, TextComponent.EMPTY, p_99208_);
         this.index = p_99207_;
         this.visible = false;
      }

      public int getIndex() {
         return this.index;
      }

      public void renderToolTip(PoseStack p_99211_, int p_99212_, int p_99213_) {
         if (this.isHovered && MerchantScreen.this.menu.getOffers().size() > this.index + MerchantScreen.this.scrollOff) {
            if (p_99212_ < this.x + 20) {
               ItemStack itemstack = MerchantScreen.this.menu.getOffers().get(this.index + MerchantScreen.this.scrollOff).getCostA();
               MerchantScreen.this.renderTooltip(p_99211_, itemstack, p_99212_, p_99213_);
            } else if (p_99212_ < this.x + 50 && p_99212_ > this.x + 30) {
               ItemStack itemstack2 = MerchantScreen.this.menu.getOffers().get(this.index + MerchantScreen.this.scrollOff).getCostB();
               if (!itemstack2.isEmpty()) {
                  MerchantScreen.this.renderTooltip(p_99211_, itemstack2, p_99212_, p_99213_);
               }
            } else if (p_99212_ > this.x + 65) {
               ItemStack itemstack1 = MerchantScreen.this.menu.getOffers().get(this.index + MerchantScreen.this.scrollOff).getResult();
               MerchantScreen.this.renderTooltip(p_99211_, itemstack1, p_99212_, p_99213_);
            }
         }

      }
   }
}