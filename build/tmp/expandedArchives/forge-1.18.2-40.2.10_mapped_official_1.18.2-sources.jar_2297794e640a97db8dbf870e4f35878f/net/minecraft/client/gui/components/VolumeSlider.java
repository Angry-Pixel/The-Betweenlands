package net.minecraft.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VolumeSlider extends AbstractOptionSliderButton {
   private final SoundSource source;

   public VolumeSlider(Minecraft p_94662_, int p_94663_, int p_94664_, SoundSource p_94665_, int p_94666_) {
      super(p_94662_.options, p_94663_, p_94664_, p_94666_, 20, (double)p_94662_.options.getSoundSourceVolume(p_94665_));
      this.source = p_94665_;
      this.updateMessage();
   }

   protected void updateMessage() {
      Component component = (Component)((float)this.value == (float)this.getYImage(false) ? CommonComponents.OPTION_OFF : new TextComponent((int)(this.value * 100.0D) + "%"));
      this.setMessage((new TranslatableComponent("soundCategory." + this.source.getName())).append(": ").append(component));
   }

   protected void applyValue() {
      this.options.setSoundCategoryVolume(this.source, (float)this.value);
      this.options.save();
   }
}