package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SkinCustomizationScreen extends OptionsSubScreen {
   public SkinCustomizationScreen(Screen p_96684_, Options p_96685_) {
      super(p_96684_, p_96685_, new TranslatableComponent("options.skinCustomisation.title"));
   }

   protected void init() {
      int i = 0;

      for(PlayerModelPart playermodelpart : PlayerModelPart.values()) {
         this.addRenderableWidget(CycleButton.onOffBuilder(this.options.isModelPartEnabled(playermodelpart)).create(this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20, playermodelpart.getName(), (p_169436_, p_169437_) -> {
            this.options.toggleModelPart(playermodelpart, p_169437_);
         }));
         ++i;
      }

      this.addRenderableWidget(Option.MAIN_HAND.createButton(this.options, this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150));
      ++i;
      if (i % 2 == 1) {
         ++i;
      }

      this.addRenderableWidget(new Button(this.width / 2 - 100, this.height / 6 + 24 * (i >> 1), 200, 20, CommonComponents.GUI_DONE, (p_96700_) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
   }

   public void render(PoseStack p_96692_, int p_96693_, int p_96694_, float p_96695_) {
      this.renderBackground(p_96692_);
      drawCenteredString(p_96692_, this.font, this.title, this.width / 2, 20, 16777215);
      super.render(p_96692_, p_96693_, p_96694_, p_96695_);
   }
}