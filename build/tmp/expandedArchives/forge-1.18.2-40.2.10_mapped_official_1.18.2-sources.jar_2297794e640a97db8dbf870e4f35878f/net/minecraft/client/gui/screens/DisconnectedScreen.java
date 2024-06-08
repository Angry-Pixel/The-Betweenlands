package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DisconnectedScreen extends Screen {
   private final Component reason;
   private MultiLineLabel message = MultiLineLabel.EMPTY;
   private final Screen parent;
   private int textHeight;

   public DisconnectedScreen(Screen p_95993_, Component p_95994_, Component p_95995_) {
      super(p_95994_);
      this.parent = p_95993_;
      this.reason = p_95995_;
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   protected void init() {
      this.message = MultiLineLabel.create(this.font, this.reason, this.width - 50);
      this.textHeight = this.message.getLineCount() * 9;
      this.addRenderableWidget(new Button(this.width / 2 - 100, Math.min(this.height / 2 + this.textHeight / 2 + 9, this.height - 30), 200, 20, new TranslatableComponent("gui.toMenu"), (p_96002_) -> {
         this.minecraft.setScreen(this.parent);
      }));
   }

   public void render(PoseStack p_95997_, int p_95998_, int p_95999_, float p_96000_) {
      this.renderBackground(p_95997_);
      drawCenteredString(p_95997_, this.font, this.title, this.width / 2, this.height / 2 - this.textHeight / 2 - 9 * 2, 11184810);
      this.message.renderCentered(p_95997_, this.width / 2, this.height / 2 - this.textHeight / 2);
      super.render(p_95997_, p_95998_, p_95999_, p_96000_);
   }
}