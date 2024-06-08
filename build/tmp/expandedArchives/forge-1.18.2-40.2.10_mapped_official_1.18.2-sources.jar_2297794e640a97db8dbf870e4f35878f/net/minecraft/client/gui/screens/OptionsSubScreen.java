package net.minecraft.client.gui.screens;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.components.TooltipAccessor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OptionsSubScreen extends Screen {
   protected final Screen lastScreen;
   protected final Options options;

   public OptionsSubScreen(Screen p_96284_, Options p_96285_, Component p_96286_) {
      super(p_96286_);
      this.lastScreen = p_96284_;
      this.options = p_96285_;
   }

   public void removed() {
      this.minecraft.options.save();
   }

   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }

   public static List<FormattedCharSequence> tooltipAt(OptionsList p_96288_, int p_96289_, int p_96290_) {
      Optional<AbstractWidget> optional = p_96288_.getMouseOver((double)p_96289_, (double)p_96290_);
      return (List<FormattedCharSequence>)(optional.isPresent() && optional.get() instanceof TooltipAccessor ? ((TooltipAccessor)optional.get()).getTooltip() : ImmutableList.of());
   }
}