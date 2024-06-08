package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.HopperMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HopperScreen extends AbstractContainerScreen<HopperMenu> {
   private static final ResourceLocation HOPPER_LOCATION = new ResourceLocation("textures/gui/container/hopper.png");

   public HopperScreen(HopperMenu p_98798_, Inventory p_98799_, Component p_98800_) {
      super(p_98798_, p_98799_, p_98800_);
      this.passEvents = false;
      this.imageHeight = 133;
      this.inventoryLabelY = this.imageHeight - 94;
   }

   public void render(PoseStack p_98807_, int p_98808_, int p_98809_, float p_98810_) {
      this.renderBackground(p_98807_);
      super.render(p_98807_, p_98808_, p_98809_, p_98810_);
      this.renderTooltip(p_98807_, p_98808_, p_98809_);
   }

   protected void renderBg(PoseStack p_98802_, float p_98803_, int p_98804_, int p_98805_) {
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, HOPPER_LOCATION);
      int i = (this.width - this.imageWidth) / 2;
      int j = (this.height - this.imageHeight) / 2;
      this.blit(p_98802_, i, j, 0, 0, this.imageWidth, this.imageHeight);
   }
}