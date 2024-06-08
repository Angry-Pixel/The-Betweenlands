package net.minecraft.realms;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DisconnectedRealmsScreen extends RealmsScreen {
   private final Component reason;
   private MultiLineLabel message = MultiLineLabel.EMPTY;
   private final Screen parent;
   private int textHeight;

   public DisconnectedRealmsScreen(Screen p_120653_, Component p_120654_, Component p_120655_) {
      super(p_120654_);
      this.parent = p_120653_;
      this.reason = p_120655_;
   }

   public void init() {
      Minecraft minecraft = Minecraft.getInstance();
      minecraft.setConnectedToRealms(false);
      minecraft.getClientPackSource().clearServerPack();
      this.message = MultiLineLabel.create(this.font, this.reason, this.width - 50);
      this.textHeight = this.message.getLineCount() * 9;
      this.addRenderableWidget(new Button(this.width / 2 - 100, this.height / 2 + this.textHeight / 2 + 9, 200, 20, CommonComponents.GUI_BACK, (p_120663_) -> {
         minecraft.setScreen(this.parent);
      }));
   }

   public Component getNarrationMessage() {
      return (new TextComponent("")).append(this.title).append(": ").append(this.reason);
   }

   public void onClose() {
      Minecraft.getInstance().setScreen(this.parent);
   }

   public void render(PoseStack p_120657_, int p_120658_, int p_120659_, float p_120660_) {
      this.renderBackground(p_120657_);
      drawCenteredString(p_120657_, this.font, this.title, this.width / 2, this.height / 2 - this.textHeight / 2 - 9 * 2, 11184810);
      this.message.renderCentered(p_120657_, this.width / 2, this.height / 2 - this.textHeight / 2);
      super.render(p_120657_, p_120658_, p_120659_, p_120660_);
   }
}