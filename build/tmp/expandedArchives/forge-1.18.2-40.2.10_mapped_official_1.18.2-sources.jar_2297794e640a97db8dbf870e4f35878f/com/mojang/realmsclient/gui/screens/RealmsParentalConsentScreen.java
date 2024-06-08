package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsParentalConsentScreen extends RealmsScreen {
   private static final Component MESSAGE = new TranslatableComponent("mco.account.privacyinfo");
   private final Screen nextScreen;
   private MultiLineLabel messageLines = MultiLineLabel.EMPTY;

   public RealmsParentalConsentScreen(Screen p_88861_) {
      super(NarratorChatListener.NO_TITLE);
      this.nextScreen = p_88861_;
   }

   public void init() {
      Component component = new TranslatableComponent("mco.account.update");
      Component component1 = CommonComponents.GUI_BACK;
      int i = Math.max(this.font.width(component), this.font.width(component1)) + 30;
      Component component2 = new TranslatableComponent("mco.account.privacy.info");
      int j = (int)((double)this.font.width(component2) * 1.2D);
      this.addRenderableWidget(new Button(this.width / 2 - j / 2, row(11), j, 20, component2, (p_88873_) -> {
         Util.getPlatform().openUri("https://aka.ms/MinecraftGDPR");
      }));
      this.addRenderableWidget(new Button(this.width / 2 - (i + 5), row(13), i, 20, component, (p_88871_) -> {
         Util.getPlatform().openUri("https://aka.ms/UpdateMojangAccount");
      }));
      this.addRenderableWidget(new Button(this.width / 2 + 5, row(13), i, 20, component1, (p_88868_) -> {
         this.minecraft.setScreen(this.nextScreen);
      }));
      this.messageLines = MultiLineLabel.create(this.font, MESSAGE, (int)Math.round((double)this.width * 0.9D));
   }

   public Component getNarrationMessage() {
      return MESSAGE;
   }

   public void render(PoseStack p_88863_, int p_88864_, int p_88865_, float p_88866_) {
      this.renderBackground(p_88863_);
      this.messageLines.renderCentered(p_88863_, this.width / 2, 15, 15, 16777215);
      super.render(p_88863_, p_88864_, p_88865_, p_88866_);
   }
}