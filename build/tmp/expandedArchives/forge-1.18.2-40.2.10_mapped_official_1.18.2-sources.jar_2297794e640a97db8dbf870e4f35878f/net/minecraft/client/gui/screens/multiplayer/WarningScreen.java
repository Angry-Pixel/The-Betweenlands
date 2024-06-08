package net.minecraft.client.gui.screens.multiplayer;

import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.Nullable;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class WarningScreen extends Screen {
   private final Component titleComponent;
   private final Component content;
   private final Component check;
   private final Component narration;
   protected final Screen previous;
   @Nullable
   protected Checkbox stopShowing;
   private MultiLineLabel message = MultiLineLabel.EMPTY;

   protected WarningScreen(Component p_210917_, Component p_210918_, Component p_210919_, Component p_210920_, Screen p_210921_) {
      super(NarratorChatListener.NO_TITLE);
      this.titleComponent = p_210917_;
      this.content = p_210918_;
      this.check = p_210919_;
      this.narration = p_210920_;
      this.previous = p_210921_;
   }

   protected abstract void initButtons(int p_210922_);

   protected void init() {
      super.init();
      this.message = MultiLineLabel.create(this.font, this.content, this.width - 50);
      int i = (this.message.getLineCount() + 1) * 9 * 2;
      this.stopShowing = new Checkbox(this.width / 2 - 155 + 80, 76 + i, 150, 20, this.check, false);
      this.addRenderableWidget(this.stopShowing);
      this.initButtons(i);
   }

   public Component getNarrationMessage() {
      return this.narration;
   }

   public void render(PoseStack p_210924_, int p_210925_, int p_210926_, float p_210927_) {
      this.renderDirtBackground(0);
      drawString(p_210924_, this.font, this.titleComponent, 25, 30, 16777215);
      this.message.renderLeftAligned(p_210924_, 25, 70, 9 * 2, 16777215);
      super.render(p_210924_, p_210925_, p_210926_, p_210927_);
   }
}