package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.DispenserMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DispenserScreen extends AbstractContainerScreen<DispenserMenu> {
   private static final ResourceLocation CONTAINER_LOCATION = new ResourceLocation("textures/gui/container/dispenser.png");

   public DispenserScreen(DispenserMenu p_98685_, Inventory p_98686_, Component p_98687_) {
      super(p_98685_, p_98686_, p_98687_);
   }

   protected void init() {
      super.init();
      this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
   }

   public void render(PoseStack p_98694_, int p_98695_, int p_98696_, float p_98697_) {
      this.renderBackground(p_98694_);
      super.render(p_98694_, p_98695_, p_98696_, p_98697_);
      this.renderTooltip(p_98694_, p_98695_, p_98696_);
   }

   protected void renderBg(PoseStack p_98689_, float p_98690_, int p_98691_, int p_98692_) {
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, CONTAINER_LOCATION);
      int i = (this.width - this.imageWidth) / 2;
      int j = (this.height - this.imageHeight) / 2;
      this.blit(p_98689_, i, j, 0, 0, this.imageWidth, this.imageHeight);
   }
}