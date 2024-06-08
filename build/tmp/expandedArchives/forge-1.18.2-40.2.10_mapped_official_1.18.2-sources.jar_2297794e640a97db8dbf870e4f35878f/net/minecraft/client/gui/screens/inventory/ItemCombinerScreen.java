package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemCombinerScreen<T extends ItemCombinerMenu> extends AbstractContainerScreen<T> implements ContainerListener {
   private final ResourceLocation menuResource;

   public ItemCombinerScreen(T p_98901_, Inventory p_98902_, Component p_98903_, ResourceLocation p_98904_) {
      super(p_98901_, p_98902_, p_98903_);
      this.menuResource = p_98904_;
   }

   protected void subInit() {
   }

   protected void init() {
      super.init();
      this.subInit();
      this.menu.addSlotListener(this);
   }

   public void removed() {
      super.removed();
      this.menu.removeSlotListener(this);
   }

   public void render(PoseStack p_98922_, int p_98923_, int p_98924_, float p_98925_) {
      this.renderBackground(p_98922_);
      super.render(p_98922_, p_98923_, p_98924_, p_98925_);
      RenderSystem.disableBlend();
      this.renderFg(p_98922_, p_98923_, p_98924_, p_98925_);
      this.renderTooltip(p_98922_, p_98923_, p_98924_);
   }

   protected void renderFg(PoseStack p_98927_, int p_98928_, int p_98929_, float p_98930_) {
   }

   protected void renderBg(PoseStack p_98917_, float p_98918_, int p_98919_, int p_98920_) {
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, this.menuResource);
      int i = (this.width - this.imageWidth) / 2;
      int j = (this.height - this.imageHeight) / 2;
      this.blit(p_98917_, i, j, 0, 0, this.imageWidth, this.imageHeight);
      this.blit(p_98917_, i + 59, j + 20, 0, this.imageHeight + (this.menu.getSlot(0).hasItem() ? 0 : 16), 110, 16);
      if ((this.menu.getSlot(0).hasItem() || this.menu.getSlot(1).hasItem()) && !this.menu.getSlot(2).hasItem()) {
         this.blit(p_98917_, i + 99, j + 45, this.imageWidth, 0, 28, 21);
      }

   }

   public void dataChanged(AbstractContainerMenu p_169759_, int p_169760_, int p_169761_) {
   }

   public void slotChanged(AbstractContainerMenu p_98910_, int p_98911_, ItemStack p_98912_) {
   }
}