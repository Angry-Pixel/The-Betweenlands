package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.util.task.WorldCreationTask;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsCreateRealmScreen extends RealmsScreen {
   private static final Component NAME_LABEL = new TranslatableComponent("mco.configure.world.name");
   private static final Component DESCRIPTION_LABEL = new TranslatableComponent("mco.configure.world.description");
   private final RealmsServer server;
   private final RealmsMainScreen lastScreen;
   private EditBox nameBox;
   private EditBox descriptionBox;
   private Button createButton;

   public RealmsCreateRealmScreen(RealmsServer p_88574_, RealmsMainScreen p_88575_) {
      super(new TranslatableComponent("mco.selectServer.create"));
      this.server = p_88574_;
      this.lastScreen = p_88575_;
   }

   public void tick() {
      if (this.nameBox != null) {
         this.nameBox.tick();
      }

      if (this.descriptionBox != null) {
         this.descriptionBox.tick();
      }

   }

   public void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.createButton = this.addRenderableWidget(new Button(this.width / 2 - 100, this.height / 4 + 120 + 17, 97, 20, new TranslatableComponent("mco.create.world"), (p_88592_) -> {
         this.createWorld();
      }));
      this.addRenderableWidget(new Button(this.width / 2 + 5, this.height / 4 + 120 + 17, 95, 20, CommonComponents.GUI_CANCEL, (p_88589_) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
      this.createButton.active = false;
      this.nameBox = new EditBox(this.minecraft.font, this.width / 2 - 100, 65, 200, 20, (EditBox)null, new TranslatableComponent("mco.configure.world.name"));
      this.addWidget(this.nameBox);
      this.setInitialFocus(this.nameBox);
      this.descriptionBox = new EditBox(this.minecraft.font, this.width / 2 - 100, 115, 200, 20, (EditBox)null, new TranslatableComponent("mco.configure.world.description"));
      this.addWidget(this.descriptionBox);
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   public boolean charTyped(char p_88577_, int p_88578_) {
      boolean flag = super.charTyped(p_88577_, p_88578_);
      this.createButton.active = this.valid();
      return flag;
   }

   public boolean keyPressed(int p_88580_, int p_88581_, int p_88582_) {
      if (p_88580_ == 256) {
         this.minecraft.setScreen(this.lastScreen);
         return true;
      } else {
         boolean flag = super.keyPressed(p_88580_, p_88581_, p_88582_);
         this.createButton.active = this.valid();
         return flag;
      }
   }

   private void createWorld() {
      if (this.valid()) {
         RealmsResetWorldScreen realmsresetworldscreen = new RealmsResetWorldScreen(this.lastScreen, this.server, new TranslatableComponent("mco.selectServer.create"), new TranslatableComponent("mco.create.world.subtitle"), 10526880, new TranslatableComponent("mco.create.world.skip"), () -> {
            this.minecraft.execute(() -> {
               this.minecraft.setScreen(this.lastScreen.newScreen());
            });
         }, () -> {
            this.minecraft.setScreen(this.lastScreen.newScreen());
         });
         realmsresetworldscreen.setResetTitle(new TranslatableComponent("mco.create.world.reset.title"));
         this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, new WorldCreationTask(this.server.id, this.nameBox.getValue(), this.descriptionBox.getValue(), realmsresetworldscreen)));
      }

   }

   private boolean valid() {
      return !this.nameBox.getValue().trim().isEmpty();
   }

   public void render(PoseStack p_88584_, int p_88585_, int p_88586_, float p_88587_) {
      this.renderBackground(p_88584_);
      drawCenteredString(p_88584_, this.font, this.title, this.width / 2, 11, 16777215);
      this.font.draw(p_88584_, NAME_LABEL, (float)(this.width / 2 - 100), 52.0F, 10526880);
      this.font.draw(p_88584_, DESCRIPTION_LABEL, (float)(this.width / 2 - 100), 102.0F, 10526880);
      if (this.nameBox != null) {
         this.nameBox.render(p_88584_, p_88585_, p_88586_, p_88587_);
      }

      if (this.descriptionBox != null) {
         this.descriptionBox.render(p_88584_, p_88585_, p_88586_, p_88587_);
      }

      super.render(p_88584_, p_88585_, p_88586_, p_88587_);
   }
}