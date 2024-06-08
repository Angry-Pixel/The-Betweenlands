package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.realmsclient.util.LevelType;
import com.mojang.realmsclient.util.WorldGenerationInfo;
import java.util.function.Consumer;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsResetNormalWorldScreen extends RealmsScreen {
   private static final Component SEED_LABEL = new TranslatableComponent("mco.reset.world.seed");
   private final Consumer<WorldGenerationInfo> callback;
   private EditBox seedEdit;
   private LevelType levelType = LevelType.DEFAULT;
   private boolean generateStructures = true;
   private final Component buttonTitle;

   public RealmsResetNormalWorldScreen(Consumer<WorldGenerationInfo> p_167438_, Component p_167439_) {
      super(new TranslatableComponent("mco.reset.world.generate"));
      this.callback = p_167438_;
      this.buttonTitle = p_167439_;
   }

   public void tick() {
      this.seedEdit.tick();
      super.tick();
   }

   public void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.seedEdit = new EditBox(this.minecraft.font, this.width / 2 - 100, row(2), 200, 20, (EditBox)null, new TranslatableComponent("mco.reset.world.seed"));
      this.seedEdit.setMaxLength(32);
      this.addWidget(this.seedEdit);
      this.setInitialFocus(this.seedEdit);
      this.addRenderableWidget(CycleButton.builder(LevelType::getName).withValues(LevelType.values()).withInitialValue(this.levelType).create(this.width / 2 - 102, row(4), 205, 20, new TranslatableComponent("selectWorld.mapType"), (p_167441_, p_167442_) -> {
         this.levelType = p_167442_;
      }));
      this.addRenderableWidget(CycleButton.onOffBuilder(this.generateStructures).create(this.width / 2 - 102, row(6) - 2, 205, 20, new TranslatableComponent("selectWorld.mapFeatures"), (p_167444_, p_167445_) -> {
         this.generateStructures = p_167445_;
      }));
      this.addRenderableWidget(new Button(this.width / 2 - 102, row(12), 97, 20, this.buttonTitle, (p_89291_) -> {
         this.callback.accept(new WorldGenerationInfo(this.seedEdit.getValue(), this.levelType, this.generateStructures));
      }));
      this.addRenderableWidget(new Button(this.width / 2 + 8, row(12), 97, 20, CommonComponents.GUI_BACK, (p_89288_) -> {
         this.onClose();
      }));
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   public void onClose() {
      this.callback.accept((WorldGenerationInfo)null);
   }

   public void render(PoseStack p_89283_, int p_89284_, int p_89285_, float p_89286_) {
      this.renderBackground(p_89283_);
      drawCenteredString(p_89283_, this.font, this.title, this.width / 2, 17, 16777215);
      this.font.draw(p_89283_, SEED_LABEL, (float)(this.width / 2 - 100), (float)row(1), 10526880);
      this.seedEdit.render(p_89283_, p_89284_, p_89285_, p_89286_);
      super.render(p_89283_, p_89284_, p_89285_, p_89286_);
   }
}