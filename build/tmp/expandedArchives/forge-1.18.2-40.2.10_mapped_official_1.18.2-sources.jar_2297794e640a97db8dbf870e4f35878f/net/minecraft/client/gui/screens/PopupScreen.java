package net.minecraft.client.gui.screens;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PopupScreen extends Screen {
   private static final int BUTTON_PADDING = 20;
   private static final int BUTTON_MARGIN = 5;
   private static final int BUTTON_HEIGHT = 20;
   private final Component narrationMessage;
   private final FormattedText message;
   private final ImmutableList<PopupScreen.ButtonOption> buttonOptions;
   private MultiLineLabel messageLines = MultiLineLabel.EMPTY;
   private int contentTop;
   private int buttonWidth;

   protected PopupScreen(Component p_96345_, List<Component> p_96346_, ImmutableList<PopupScreen.ButtonOption> p_96347_) {
      super(p_96345_);
      this.message = FormattedText.composite(p_96346_);
      this.narrationMessage = CommonComponents.joinForNarration(p_96345_, ComponentUtils.formatList(p_96346_, TextComponent.EMPTY));
      this.buttonOptions = p_96347_;
   }

   public Component getNarrationMessage() {
      return this.narrationMessage;
   }

   public void init() {
      for(PopupScreen.ButtonOption popupscreen$buttonoption : this.buttonOptions) {
         this.buttonWidth = Math.max(this.buttonWidth, 20 + this.font.width(popupscreen$buttonoption.message) + 20);
      }

      int l = 5 + this.buttonWidth + 5;
      int i1 = l * this.buttonOptions.size();
      this.messageLines = MultiLineLabel.create(this.font, this.message, i1);
      int i = this.messageLines.getLineCount() * 9;
      this.contentTop = (int)((double)this.height / 2.0D - (double)i / 2.0D);
      int j = this.contentTop + i + 9 * 2;
      int k = (int)((double)this.width / 2.0D - (double)i1 / 2.0D);

      for(PopupScreen.ButtonOption popupscreen$buttonoption1 : this.buttonOptions) {
         this.addRenderableWidget(new Button(k, j, this.buttonWidth, 20, popupscreen$buttonoption1.message, popupscreen$buttonoption1.onPress));
         k += l;
      }

   }

   public void render(PoseStack p_96349_, int p_96350_, int p_96351_, float p_96352_) {
      this.renderDirtBackground(0);
      drawCenteredString(p_96349_, this.font, this.title, this.width / 2, this.contentTop - 9 * 2, -1);
      this.messageLines.renderCentered(p_96349_, this.width / 2, this.contentTop);
      super.render(p_96349_, p_96350_, p_96351_, p_96352_);
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public static final class ButtonOption {
      final Component message;
      final Button.OnPress onPress;

      public ButtonOption(Component p_96362_, Button.OnPress p_96363_) {
         this.message = p_96362_;
         this.onPress = p_96363_;
      }
   }
}