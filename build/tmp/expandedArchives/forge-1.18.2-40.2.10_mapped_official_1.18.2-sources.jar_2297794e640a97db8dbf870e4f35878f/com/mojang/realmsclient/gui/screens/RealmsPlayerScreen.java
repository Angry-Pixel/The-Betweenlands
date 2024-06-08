package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.Ops;
import com.mojang.realmsclient.dto.PlayerInfo;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.util.RealmsTextureManager;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsPlayerScreen extends RealmsScreen {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final ResourceLocation OP_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/op_icon.png");
   private static final ResourceLocation USER_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/user_icon.png");
   private static final ResourceLocation CROSS_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/cross_player_icon.png");
   private static final ResourceLocation OPTIONS_BACKGROUND = new ResourceLocation("minecraft", "textures/gui/options_background.png");
   private static final Component NORMAL_USER_TOOLTIP = new TranslatableComponent("mco.configure.world.invites.normal.tooltip");
   private static final Component OP_TOOLTIP = new TranslatableComponent("mco.configure.world.invites.ops.tooltip");
   private static final Component REMOVE_ENTRY_TOOLTIP = new TranslatableComponent("mco.configure.world.invites.remove.tooltip");
   private static final Component INVITED_LABEL = new TranslatableComponent("mco.configure.world.invited");
   @Nullable
   private Component toolTip;
   private final RealmsConfigureWorldScreen lastScreen;
   final RealmsServer serverData;
   private RealmsPlayerScreen.InvitedObjectSelectionList invitedObjectSelectionList;
   int column1X;
   int columnWidth;
   private int column2X;
   private Button removeButton;
   private Button opdeopButton;
   private int selectedInvitedIndex = -1;
   private String selectedInvited;
   int player = -1;
   private boolean stateChanged;
   RealmsPlayerScreen.UserAction hoveredUserAction = RealmsPlayerScreen.UserAction.NONE;

   public RealmsPlayerScreen(RealmsConfigureWorldScreen p_89089_, RealmsServer p_89090_) {
      super(new TranslatableComponent("mco.configure.world.players.title"));
      this.lastScreen = p_89089_;
      this.serverData = p_89090_;
   }

   public void init() {
      this.column1X = this.width / 2 - 160;
      this.columnWidth = 150;
      this.column2X = this.width / 2 + 12;
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.invitedObjectSelectionList = new RealmsPlayerScreen.InvitedObjectSelectionList();
      this.invitedObjectSelectionList.setLeftPos(this.column1X);
      this.addWidget(this.invitedObjectSelectionList);

      for(PlayerInfo playerinfo : this.serverData.players) {
         this.invitedObjectSelectionList.addEntry(playerinfo);
      }

      this.addRenderableWidget(new Button(this.column2X, row(1), this.columnWidth + 10, 20, new TranslatableComponent("mco.configure.world.buttons.invite"), (p_89176_) -> {
         this.minecraft.setScreen(new RealmsInviteScreen(this.lastScreen, this, this.serverData));
      }));
      this.removeButton = this.addRenderableWidget(new Button(this.column2X, row(7), this.columnWidth + 10, 20, new TranslatableComponent("mco.configure.world.invites.remove.tooltip"), (p_89161_) -> {
         this.uninvite(this.player);
      }));
      this.opdeopButton = this.addRenderableWidget(new Button(this.column2X, row(9), this.columnWidth + 10, 20, new TranslatableComponent("mco.configure.world.invites.ops.tooltip"), (p_89139_) -> {
         if (this.serverData.players.get(this.player).isOperator()) {
            this.deop(this.player);
         } else {
            this.op(this.player);
         }

      }));
      this.addRenderableWidget(new Button(this.column2X + this.columnWidth / 2 + 2, row(12), this.columnWidth / 2 + 10 - 2, 20, CommonComponents.GUI_BACK, (p_89122_) -> {
         this.backButtonClicked();
      }));
      this.updateButtonStates();
   }

   void updateButtonStates() {
      this.removeButton.visible = this.shouldRemoveAndOpdeopButtonBeVisible(this.player);
      this.opdeopButton.visible = this.shouldRemoveAndOpdeopButtonBeVisible(this.player);
   }

   private boolean shouldRemoveAndOpdeopButtonBeVisible(int p_89191_) {
      return p_89191_ != -1;
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   public boolean keyPressed(int p_89094_, int p_89095_, int p_89096_) {
      if (p_89094_ == 256) {
         this.backButtonClicked();
         return true;
      } else {
         return super.keyPressed(p_89094_, p_89095_, p_89096_);
      }
   }

   private void backButtonClicked() {
      if (this.stateChanged) {
         this.minecraft.setScreen(this.lastScreen.getNewScreen());
      } else {
         this.minecraft.setScreen(this.lastScreen);
      }

   }

   void op(int p_89193_) {
      this.updateButtonStates();
      RealmsClient realmsclient = RealmsClient.create();
      String s = this.serverData.players.get(p_89193_).getUuid();

      try {
         this.updateOps(realmsclient.op(this.serverData.id, s));
      } catch (RealmsServiceException realmsserviceexception) {
         LOGGER.error("Couldn't op the user");
      }

   }

   void deop(int p_89195_) {
      this.updateButtonStates();
      RealmsClient realmsclient = RealmsClient.create();
      String s = this.serverData.players.get(p_89195_).getUuid();

      try {
         this.updateOps(realmsclient.deop(this.serverData.id, s));
      } catch (RealmsServiceException realmsserviceexception) {
         LOGGER.error("Couldn't deop the user");
      }

   }

   private void updateOps(Ops p_89108_) {
      for(PlayerInfo playerinfo : this.serverData.players) {
         playerinfo.setOperator(p_89108_.ops.contains(playerinfo.getName()));
      }

   }

   void uninvite(int p_89197_) {
      this.updateButtonStates();
      if (p_89197_ >= 0 && p_89197_ < this.serverData.players.size()) {
         PlayerInfo playerinfo = this.serverData.players.get(p_89197_);
         this.selectedInvited = playerinfo.getUuid();
         this.selectedInvitedIndex = p_89197_;
         RealmsConfirmScreen realmsconfirmscreen = new RealmsConfirmScreen((p_89163_) -> {
            if (p_89163_) {
               RealmsClient realmsclient = RealmsClient.create();

               try {
                  realmsclient.uninvite(this.serverData.id, this.selectedInvited);
               } catch (RealmsServiceException realmsserviceexception) {
                  LOGGER.error("Couldn't uninvite user");
               }

               this.deleteFromInvitedList(this.selectedInvitedIndex);
               this.player = -1;
               this.updateButtonStates();
            }

            this.stateChanged = true;
            this.minecraft.setScreen(this);
         }, new TextComponent("Question"), (new TranslatableComponent("mco.configure.world.uninvite.question")).append(" '").append(playerinfo.getName()).append("' ?"));
         this.minecraft.setScreen(realmsconfirmscreen);
      }

   }

   private void deleteFromInvitedList(int p_89199_) {
      this.serverData.players.remove(p_89199_);
   }

   public void render(PoseStack p_89098_, int p_89099_, int p_89100_, float p_89101_) {
      this.toolTip = null;
      this.hoveredUserAction = RealmsPlayerScreen.UserAction.NONE;
      this.renderBackground(p_89098_);
      if (this.invitedObjectSelectionList != null) {
         this.invitedObjectSelectionList.render(p_89098_, p_89099_, p_89100_, p_89101_);
      }

      drawCenteredString(p_89098_, this.font, this.title, this.width / 2, 17, 16777215);
      int i = row(12) + 20;
      Tesselator tesselator = Tesselator.getInstance();
      BufferBuilder bufferbuilder = tesselator.getBuilder();
      RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
      RenderSystem.setShaderTexture(0, OPTIONS_BACKGROUND);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      float f = 32.0F;
      bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
      bufferbuilder.vertex(0.0D, (double)this.height, 0.0D).uv(0.0F, (float)(this.height - i) / 32.0F + 0.0F).color(64, 64, 64, 255).endVertex();
      bufferbuilder.vertex((double)this.width, (double)this.height, 0.0D).uv((float)this.width / 32.0F, (float)(this.height - i) / 32.0F + 0.0F).color(64, 64, 64, 255).endVertex();
      bufferbuilder.vertex((double)this.width, (double)i, 0.0D).uv((float)this.width / 32.0F, 0.0F).color(64, 64, 64, 255).endVertex();
      bufferbuilder.vertex(0.0D, (double)i, 0.0D).uv(0.0F, 0.0F).color(64, 64, 64, 255).endVertex();
      tesselator.end();
      if (this.serverData != null && this.serverData.players != null) {
         this.font.draw(p_89098_, (new TextComponent("")).append(INVITED_LABEL).append(" (").append(Integer.toString(this.serverData.players.size())).append(")"), (float)this.column1X, (float)row(0), 10526880);
      } else {
         this.font.draw(p_89098_, INVITED_LABEL, (float)this.column1X, (float)row(0), 10526880);
      }

      super.render(p_89098_, p_89099_, p_89100_, p_89101_);
      if (this.serverData != null) {
         this.renderMousehoverTooltip(p_89098_, this.toolTip, p_89099_, p_89100_);
      }
   }

   protected void renderMousehoverTooltip(PoseStack p_89103_, @Nullable Component p_89104_, int p_89105_, int p_89106_) {
      if (p_89104_ != null) {
         int i = p_89105_ + 12;
         int j = p_89106_ - 12;
         int k = this.font.width(p_89104_);
         this.fillGradient(p_89103_, i - 3, j - 3, i + k + 3, j + 8 + 3, -1073741824, -1073741824);
         this.font.drawShadow(p_89103_, p_89104_, (float)i, (float)j, 16777215);
      }
   }

   void drawRemoveIcon(PoseStack p_89143_, int p_89144_, int p_89145_, int p_89146_, int p_89147_) {
      boolean flag = p_89146_ >= p_89144_ && p_89146_ <= p_89144_ + 9 && p_89147_ >= p_89145_ && p_89147_ <= p_89145_ + 9 && p_89147_ < row(12) + 20 && p_89147_ > row(1);
      RenderSystem.setShaderTexture(0, CROSS_ICON_LOCATION);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      float f = flag ? 7.0F : 0.0F;
      GuiComponent.blit(p_89143_, p_89144_, p_89145_, 0.0F, f, 8, 7, 8, 14);
      if (flag) {
         this.toolTip = REMOVE_ENTRY_TOOLTIP;
         this.hoveredUserAction = RealmsPlayerScreen.UserAction.REMOVE;
      }

   }

   void drawOpped(PoseStack p_89165_, int p_89166_, int p_89167_, int p_89168_, int p_89169_) {
      boolean flag = p_89168_ >= p_89166_ && p_89168_ <= p_89166_ + 9 && p_89169_ >= p_89167_ && p_89169_ <= p_89167_ + 9 && p_89169_ < row(12) + 20 && p_89169_ > row(1);
      RenderSystem.setShaderTexture(0, OP_ICON_LOCATION);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      float f = flag ? 8.0F : 0.0F;
      GuiComponent.blit(p_89165_, p_89166_, p_89167_, 0.0F, f, 8, 8, 8, 16);
      if (flag) {
         this.toolTip = OP_TOOLTIP;
         this.hoveredUserAction = RealmsPlayerScreen.UserAction.TOGGLE_OP;
      }

   }

   void drawNormal(PoseStack p_89179_, int p_89180_, int p_89181_, int p_89182_, int p_89183_) {
      boolean flag = p_89182_ >= p_89180_ && p_89182_ <= p_89180_ + 9 && p_89183_ >= p_89181_ && p_89183_ <= p_89181_ + 9 && p_89183_ < row(12) + 20 && p_89183_ > row(1);
      RenderSystem.setShaderTexture(0, USER_ICON_LOCATION);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      float f = flag ? 8.0F : 0.0F;
      GuiComponent.blit(p_89179_, p_89180_, p_89181_, 0.0F, f, 8, 8, 8, 16);
      if (flag) {
         this.toolTip = NORMAL_USER_TOOLTIP;
         this.hoveredUserAction = RealmsPlayerScreen.UserAction.TOGGLE_OP;
      }

   }

   @OnlyIn(Dist.CLIENT)
   class Entry extends ObjectSelectionList.Entry<RealmsPlayerScreen.Entry> {
      private final PlayerInfo playerInfo;

      public Entry(PlayerInfo p_89204_) {
         this.playerInfo = p_89204_;
      }

      public void render(PoseStack p_89209_, int p_89210_, int p_89211_, int p_89212_, int p_89213_, int p_89214_, int p_89215_, int p_89216_, boolean p_89217_, float p_89218_) {
         this.renderInvitedItem(p_89209_, this.playerInfo, p_89212_, p_89211_, p_89215_, p_89216_);
      }

      private void renderInvitedItem(PoseStack p_89220_, PlayerInfo p_89221_, int p_89222_, int p_89223_, int p_89224_, int p_89225_) {
         int i;
         if (!p_89221_.getAccepted()) {
            i = 10526880;
         } else if (p_89221_.getOnline()) {
            i = 8388479;
         } else {
            i = 16777215;
         }

         RealmsPlayerScreen.this.font.draw(p_89220_, p_89221_.getName(), (float)(RealmsPlayerScreen.this.column1X + 3 + 12), (float)(p_89223_ + 1), i);
         if (p_89221_.isOperator()) {
            RealmsPlayerScreen.this.drawOpped(p_89220_, RealmsPlayerScreen.this.column1X + RealmsPlayerScreen.this.columnWidth - 10, p_89223_ + 1, p_89224_, p_89225_);
         } else {
            RealmsPlayerScreen.this.drawNormal(p_89220_, RealmsPlayerScreen.this.column1X + RealmsPlayerScreen.this.columnWidth - 10, p_89223_ + 1, p_89224_, p_89225_);
         }

         RealmsPlayerScreen.this.drawRemoveIcon(p_89220_, RealmsPlayerScreen.this.column1X + RealmsPlayerScreen.this.columnWidth - 22, p_89223_ + 2, p_89224_, p_89225_);
         RealmsTextureManager.withBoundFace(p_89221_.getUuid(), () -> {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            GuiComponent.blit(p_89220_, RealmsPlayerScreen.this.column1X + 2 + 2, p_89223_ + 1, 8, 8, 8.0F, 8.0F, 8, 8, 64, 64);
            GuiComponent.blit(p_89220_, RealmsPlayerScreen.this.column1X + 2 + 2, p_89223_ + 1, 8, 8, 40.0F, 8.0F, 8, 8, 64, 64);
         });
      }

      public Component getNarration() {
         return new TranslatableComponent("narrator.select", this.playerInfo.getName());
      }
   }

   @OnlyIn(Dist.CLIENT)
   class InvitedObjectSelectionList extends RealmsObjectSelectionList<RealmsPlayerScreen.Entry> {
      public InvitedObjectSelectionList() {
         super(RealmsPlayerScreen.this.columnWidth + 10, RealmsPlayerScreen.row(12) + 20, RealmsPlayerScreen.row(1), RealmsPlayerScreen.row(12) + 20, 13);
      }

      public void addEntry(PlayerInfo p_89244_) {
         this.addEntry(RealmsPlayerScreen.this.new Entry(p_89244_));
      }

      public int getRowWidth() {
         return (int)((double)this.width * 1.0D);
      }

      public boolean isFocused() {
         return RealmsPlayerScreen.this.getFocused() == this;
      }

      public boolean mouseClicked(double p_89230_, double p_89231_, int p_89232_) {
         if (p_89232_ == 0 && p_89230_ < (double)this.getScrollbarPosition() && p_89231_ >= (double)this.y0 && p_89231_ <= (double)this.y1) {
            int i = RealmsPlayerScreen.this.column1X;
            int j = RealmsPlayerScreen.this.column1X + RealmsPlayerScreen.this.columnWidth;
            int k = (int)Math.floor(p_89231_ - (double)this.y0) - this.headerHeight + (int)this.getScrollAmount() - 4;
            int l = k / this.itemHeight;
            if (p_89230_ >= (double)i && p_89230_ <= (double)j && l >= 0 && k >= 0 && l < this.getItemCount()) {
               this.selectItem(l);
               this.itemClicked(k, l, p_89230_, p_89231_, this.width);
            }

            return true;
         } else {
            return super.mouseClicked(p_89230_, p_89231_, p_89232_);
         }
      }

      public void itemClicked(int p_89236_, int p_89237_, double p_89238_, double p_89239_, int p_89240_) {
         if (p_89237_ >= 0 && p_89237_ <= RealmsPlayerScreen.this.serverData.players.size() && RealmsPlayerScreen.this.hoveredUserAction != RealmsPlayerScreen.UserAction.NONE) {
            if (RealmsPlayerScreen.this.hoveredUserAction == RealmsPlayerScreen.UserAction.TOGGLE_OP) {
               if (RealmsPlayerScreen.this.serverData.players.get(p_89237_).isOperator()) {
                  RealmsPlayerScreen.this.deop(p_89237_);
               } else {
                  RealmsPlayerScreen.this.op(p_89237_);
               }
            } else if (RealmsPlayerScreen.this.hoveredUserAction == RealmsPlayerScreen.UserAction.REMOVE) {
               RealmsPlayerScreen.this.uninvite(p_89237_);
            }

         }
      }

      public void selectItem(int p_89234_) {
         super.selectItem(p_89234_);
         this.selectInviteListItem(p_89234_);
      }

      public void selectInviteListItem(int p_89251_) {
         RealmsPlayerScreen.this.player = p_89251_;
         RealmsPlayerScreen.this.updateButtonStates();
      }

      public void setSelected(@Nullable RealmsPlayerScreen.Entry p_89246_) {
         super.setSelected(p_89246_);
         RealmsPlayerScreen.this.player = this.children().indexOf(p_89246_);
         RealmsPlayerScreen.this.updateButtonStates();
      }

      public void renderBackground(PoseStack p_89242_) {
         RealmsPlayerScreen.this.renderBackground(p_89242_);
      }

      public int getScrollbarPosition() {
         return RealmsPlayerScreen.this.column1X + this.width - 5;
      }

      public int getMaxPosition() {
         return this.getItemCount() * 13;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static enum UserAction {
      TOGGLE_OP,
      REMOVE,
      NONE;
   }
}