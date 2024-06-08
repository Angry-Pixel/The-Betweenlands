package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EditServerScreen extends Screen {
   private static final Component NAME_LABEL = new TranslatableComponent("addServer.enterName");
   private static final Component IP_LABEL = new TranslatableComponent("addServer.enterIp");
   private Button addButton;
   private final BooleanConsumer callback;
   private final ServerData serverData;
   private EditBox ipEdit;
   private EditBox nameEdit;
   private final Screen lastScreen;

   public EditServerScreen(Screen p_96017_, BooleanConsumer p_96018_, ServerData p_96019_) {
      super(new TranslatableComponent("addServer.title"));
      this.lastScreen = p_96017_;
      this.callback = p_96018_;
      this.serverData = p_96019_;
   }

   public void tick() {
      this.nameEdit.tick();
      this.ipEdit.tick();
   }

   protected void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.nameEdit = new EditBox(this.font, this.width / 2 - 100, 66, 200, 20, new TranslatableComponent("addServer.enterName"));
      this.nameEdit.setFocus(true);
      this.nameEdit.setValue(this.serverData.name);
      this.nameEdit.setResponder((p_169304_) -> {
         this.updateAddButtonStatus();
      });
      this.addWidget(this.nameEdit);
      this.ipEdit = new EditBox(this.font, this.width / 2 - 100, 106, 200, 20, new TranslatableComponent("addServer.enterIp"));
      this.ipEdit.setMaxLength(128);
      this.ipEdit.setValue(this.serverData.ip);
      this.ipEdit.setResponder((p_169302_) -> {
         this.updateAddButtonStatus();
      });
      this.addWidget(this.ipEdit);
      this.addRenderableWidget(CycleButton.builder(ServerData.ServerPackStatus::getName).withValues(ServerData.ServerPackStatus.values()).withInitialValue(this.serverData.getResourcePackStatus()).create(this.width / 2 - 100, this.height / 4 + 72, 200, 20, new TranslatableComponent("addServer.resourcePack"), (p_169299_, p_169300_) -> {
         this.serverData.setResourcePackStatus(p_169300_);
      }));
      this.addButton = this.addRenderableWidget(new Button(this.width / 2 - 100, this.height / 4 + 96 + 18, 200, 20, new TranslatableComponent("addServer.add"), (p_96030_) -> {
         this.onAdd();
      }));
      this.addRenderableWidget(new Button(this.width / 2 - 100, this.height / 4 + 120 + 18, 200, 20, CommonComponents.GUI_CANCEL, (p_169297_) -> {
         this.callback.accept(false);
      }));
      this.updateAddButtonStatus();
   }

   public void resize(Minecraft p_96026_, int p_96027_, int p_96028_) {
      String s = this.ipEdit.getValue();
      String s1 = this.nameEdit.getValue();
      this.init(p_96026_, p_96027_, p_96028_);
      this.ipEdit.setValue(s);
      this.nameEdit.setValue(s1);
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   private void onAdd() {
      this.serverData.name = this.nameEdit.getValue();
      this.serverData.ip = this.ipEdit.getValue();
      this.callback.accept(true);
   }

   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }

   private void updateAddButtonStatus() {
      this.addButton.active = ServerAddress.isValidAddress(this.ipEdit.getValue()) && !this.nameEdit.getValue().isEmpty();
   }

   public void render(PoseStack p_96021_, int p_96022_, int p_96023_, float p_96024_) {
      this.renderBackground(p_96021_);
      drawCenteredString(p_96021_, this.font, this.title, this.width / 2, 17, 16777215);
      drawString(p_96021_, this.font, NAME_LABEL, this.width / 2 - 100, 53, 10526880);
      drawString(p_96021_, this.font, IP_LABEL, this.width / 2 - 100, 94, 10526880);
      this.nameEdit.render(p_96021_, p_96022_, p_96023_, p_96024_);
      this.ipEdit.render(p_96021_, p_96022_, p_96023_, p_96024_);
      super.render(p_96021_, p_96022_, p_96023_, p_96024_);
   }
}