package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.util.task.GetServerDetailsTask;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.Util;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsTermsScreen extends RealmsScreen {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Component TITLE = new TranslatableComponent("mco.terms.title");
   private static final Component TERMS_STATIC_TEXT = new TranslatableComponent("mco.terms.sentence.1");
   private static final Component TERMS_LINK_TEXT = (new TextComponent(" ")).append((new TranslatableComponent("mco.terms.sentence.2")).withStyle(Style.EMPTY.withUnderlined(true)));
   private final Screen lastScreen;
   private final RealmsMainScreen mainScreen;
   private final RealmsServer realmsServer;
   private boolean onLink;
   private final String realmsToSUrl = "https://aka.ms/MinecraftRealmsTerms";

   public RealmsTermsScreen(Screen p_90033_, RealmsMainScreen p_90034_, RealmsServer p_90035_) {
      super(TITLE);
      this.lastScreen = p_90033_;
      this.mainScreen = p_90034_;
      this.realmsServer = p_90035_;
   }

   public void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      int i = this.width / 4 - 2;
      this.addRenderableWidget(new Button(this.width / 4, row(12), i, 20, new TranslatableComponent("mco.terms.buttons.agree"), (p_90054_) -> {
         this.agreedToTos();
      }));
      this.addRenderableWidget(new Button(this.width / 2 + 4, row(12), i, 20, new TranslatableComponent("mco.terms.buttons.disagree"), (p_90050_) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   public boolean keyPressed(int p_90041_, int p_90042_, int p_90043_) {
      if (p_90041_ == 256) {
         this.minecraft.setScreen(this.lastScreen);
         return true;
      } else {
         return super.keyPressed(p_90041_, p_90042_, p_90043_);
      }
   }

   private void agreedToTos() {
      RealmsClient realmsclient = RealmsClient.create();

      try {
         realmsclient.agreeToTos();
         this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, new GetServerDetailsTask(this.mainScreen, this.lastScreen, this.realmsServer, new ReentrantLock())));
      } catch (RealmsServiceException realmsserviceexception) {
         LOGGER.error("Couldn't agree to TOS");
      }

   }

   public boolean mouseClicked(double p_90037_, double p_90038_, int p_90039_) {
      if (this.onLink) {
         this.minecraft.keyboardHandler.setClipboard("https://aka.ms/MinecraftRealmsTerms");
         Util.getPlatform().openUri("https://aka.ms/MinecraftRealmsTerms");
         return true;
      } else {
         return super.mouseClicked(p_90037_, p_90038_, p_90039_);
      }
   }

   public Component getNarrationMessage() {
      return CommonComponents.joinForNarration(super.getNarrationMessage(), TERMS_STATIC_TEXT).append(" ").append(TERMS_LINK_TEXT);
   }

   public void render(PoseStack p_90045_, int p_90046_, int p_90047_, float p_90048_) {
      this.renderBackground(p_90045_);
      drawCenteredString(p_90045_, this.font, this.title, this.width / 2, 17, 16777215);
      this.font.draw(p_90045_, TERMS_STATIC_TEXT, (float)(this.width / 2 - 120), (float)row(5), 16777215);
      int i = this.font.width(TERMS_STATIC_TEXT);
      int j = this.width / 2 - 121 + i;
      int k = row(5);
      int l = j + this.font.width(TERMS_LINK_TEXT) + 1;
      int i1 = k + 1 + 9;
      this.onLink = j <= p_90046_ && p_90046_ <= l && k <= p_90047_ && p_90047_ <= i1;
      this.font.draw(p_90045_, TERMS_LINK_TEXT, (float)(this.width / 2 - 120 + i), (float)row(5), this.onLink ? 7107012 : 3368635);
      super.render(p_90045_, p_90046_, p_90047_, p_90048_);
   }
}