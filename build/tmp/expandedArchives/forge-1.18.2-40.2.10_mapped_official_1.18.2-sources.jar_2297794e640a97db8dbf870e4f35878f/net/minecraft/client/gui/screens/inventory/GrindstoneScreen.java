package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GrindstoneScreen extends AbstractContainerScreen<GrindstoneMenu> {
   private static final ResourceLocation GRINDSTONE_LOCATION = new ResourceLocation("textures/gui/container/grindstone.png");

   public GrindstoneScreen(GrindstoneMenu p_98782_, Inventory p_98783_, Component p_98784_) {
      super(p_98782_, p_98783_, p_98784_);
   }

   public void render(PoseStack p_98791_, int p_98792_, int p_98793_, float p_98794_) {
      this.renderBackground(p_98791_);
      this.renderBg(p_98791_, p_98794_, p_98792_, p_98793_);
      super.render(p_98791_, p_98792_, p_98793_, p_98794_);
      this.renderTooltip(p_98791_, p_98792_, p_98793_);
   }

   protected void renderBg(PoseStack p_98786_, float p_98787_, int p_98788_, int p_98789_) {
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, GRINDSTONE_LOCATION);
      int i = (this.width - this.imageWidth) / 2;
      int j = (this.height - this.imageHeight) / 2;
      this.blit(p_98786_, i, j, 0, 0, this.imageWidth, this.imageHeight);
      if ((this.menu.getSlot(0).hasItem() || this.menu.getSlot(1).hasItem()) && !this.menu.getSlot(2).hasItem()) {
         this.blit(p_98786_, i + 92, j + 31, this.imageWidth, 0, 28, 21);
      }

   }
}