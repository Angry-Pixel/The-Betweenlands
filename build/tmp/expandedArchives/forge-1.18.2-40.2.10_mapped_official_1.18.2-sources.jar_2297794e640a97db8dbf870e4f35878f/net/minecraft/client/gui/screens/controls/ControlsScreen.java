package net.minecraft.client.gui.screens.controls;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.MouseSettingsScreen;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ControlsScreen extends OptionsSubScreen {
   private static final int ROW_SPACING = 24;

   public ControlsScreen(Screen p_97519_, Options p_97520_) {
      super(p_97519_, p_97520_, new TranslatableComponent("controls.title"));
   }

   protected void init() {
      super.init();
      int i = this.width / 2 - 155;
      int j = i + 160;
      int k = this.height / 6 - 12;
      this.addRenderableWidget(new Button(i, k, 150, 20, new TranslatableComponent("options.mouse_settings"), (p_97540_) -> {
         this.minecraft.setScreen(new MouseSettingsScreen(this, this.options));
      }));
      this.addRenderableWidget(new Button(j, k, 150, 20, new TranslatableComponent("controls.keybinds"), (p_97538_) -> {
         this.minecraft.setScreen(new KeyBindsScreen(this, this.options));
      }));
      k += 24;
      this.addRenderableWidget(Option.TOGGLE_CROUCH.createButton(this.options, i, k, 150));
      this.addRenderableWidget(Option.TOGGLE_SPRINT.createButton(this.options, j, k, 150));
      k += 24;
      this.addRenderableWidget(Option.AUTO_JUMP.createButton(this.options, i, k, 150));
      k += 24;
      this.addRenderableWidget(new Button(this.width / 2 - 100, k, 200, 20, CommonComponents.GUI_DONE, (p_97535_) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
   }

   public void render(PoseStack p_97530_, int p_97531_, int p_97532_, float p_97533_) {
      this.renderBackground(p_97530_);
      drawCenteredString(p_97530_, this.font, this.title, this.width / 2, 15, 16777215);
      super.render(p_97530_, p_97531_, p_97532_, p_97533_);
   }
}