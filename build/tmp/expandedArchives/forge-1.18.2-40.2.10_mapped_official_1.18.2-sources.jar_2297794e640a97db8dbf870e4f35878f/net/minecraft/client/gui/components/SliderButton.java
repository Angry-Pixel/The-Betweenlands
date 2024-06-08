package net.minecraft.client.gui.components;

import java.util.List;
import net.minecraft.client.Options;
import net.minecraft.client.ProgressOption;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SliderButton extends AbstractOptionSliderButton implements TooltipAccessor {
   private final ProgressOption option;
   private final List<FormattedCharSequence> tooltip;

   public SliderButton(Options p_169060_, int p_169061_, int p_169062_, int p_169063_, int p_169064_, ProgressOption p_169065_, List<FormattedCharSequence> p_169066_) {
      super(p_169060_, p_169061_, p_169062_, p_169063_, p_169064_, (double)((float)p_169065_.toPct(p_169065_.get(p_169060_))));
      this.option = p_169065_;
      this.tooltip = p_169066_;
      this.updateMessage();
   }

   protected void applyValue() {
      this.option.set(this.options, this.option.toValue(this.value));
      this.options.save();
   }

   protected void updateMessage() {
      this.setMessage(this.option.getMessage(this.options));
   }

   public List<FormattedCharSequence> getTooltip() {
      return this.tooltip;
   }
}