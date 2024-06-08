package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DatapackLoadFailureScreen extends Screen {
   private MultiLineLabel message = MultiLineLabel.EMPTY;
   private final Runnable callback;

   public DatapackLoadFailureScreen(Runnable p_95894_) {
      super(new TranslatableComponent("datapackFailure.title"));
      this.callback = p_95894_;
   }

   protected void init() {
      super.init();
      this.message = MultiLineLabel.create(this.font, this.getTitle(), this.width - 50);
      this.addRenderableWidget(new Button(this.width / 2 - 155, this.height / 6 + 96, 150, 20, new TranslatableComponent("datapackFailure.safeMode"), (p_95905_) -> {
         this.callback.run();
      }));
      this.addRenderableWidget(new Button(this.width / 2 - 155 + 160, this.height / 6 + 96, 150, 20, new TranslatableComponent("gui.toTitle"), (p_95901_) -> {
         this.minecraft.setScreen((Screen)null);
      }));
   }

   public void render(PoseStack p_95896_, int p_95897_, int p_95898_, float p_95899_) {
      this.renderBackground(p_95896_);
      this.message.renderCentered(p_95896_, this.width / 2, 70);
      super.render(p_95896_, p_95897_, p_95898_, p_95899_);
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }
}