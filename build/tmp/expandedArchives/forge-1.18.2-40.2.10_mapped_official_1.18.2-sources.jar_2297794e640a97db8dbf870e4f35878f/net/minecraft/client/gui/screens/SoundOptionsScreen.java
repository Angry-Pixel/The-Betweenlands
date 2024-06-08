package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.VolumeSlider;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SoundOptionsScreen extends OptionsSubScreen {
   public SoundOptionsScreen(Screen p_96702_, Options p_96703_) {
      super(p_96702_, p_96703_, new TranslatableComponent("options.sounds.title"));
   }

   protected void init() {
      int i = this.height / 6 - 12;
      int j = 22;
      int k = 0;
      this.addRenderableWidget(new VolumeSlider(this.minecraft, this.width / 2 - 155 + k % 2 * 160, i + 22 * (k >> 1), SoundSource.MASTER, 310));
      k += 2;

      for(SoundSource soundsource : SoundSource.values()) {
         if (soundsource != SoundSource.MASTER) {
            this.addRenderableWidget(new VolumeSlider(this.minecraft, this.width / 2 - 155 + k % 2 * 160, i + 22 * (k >> 1), soundsource, 150));
            ++k;
         }
      }

      if (k % 2 == 1) {
         ++k;
      }

      this.addRenderableWidget(Option.AUDIO_DEVICE.createButton(this.options, this.width / 2 - 155, i + 22 * (k >> 1), 310));
      k += 2;
      this.addRenderableWidget(Option.SHOW_SUBTITLES.createButton(this.options, this.width / 2 - 75, i + 22 * (k >> 1), 150));
      k += 2;
      this.addRenderableWidget(new Button(this.width / 2 - 100, i + 22 * (k >> 1), 200, 20, CommonComponents.GUI_DONE, (p_96713_) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
   }

   public void render(PoseStack p_96705_, int p_96706_, int p_96707_, float p_96708_) {
      this.renderBackground(p_96705_);
      drawCenteredString(p_96705_, this.font, this.title, this.width / 2, 15, 16777215);
      super.render(p_96705_, p_96706_, p_96707_, p_96708_);
   }
}