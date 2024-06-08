package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.HorseInventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HorseInventoryScreen extends AbstractContainerScreen<HorseInventoryMenu> {
   private static final ResourceLocation HORSE_INVENTORY_LOCATION = new ResourceLocation("textures/gui/container/horse.png");
   private final AbstractHorse horse;
   private float xMouse;
   private float yMouse;

   public HorseInventoryScreen(HorseInventoryMenu p_98817_, Inventory p_98818_, AbstractHorse p_98819_) {
      super(p_98817_, p_98818_, p_98819_.getDisplayName());
      this.horse = p_98819_;
      this.passEvents = false;
   }

   protected void renderBg(PoseStack p_98821_, float p_98822_, int p_98823_, int p_98824_) {
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, HORSE_INVENTORY_LOCATION);
      int i = (this.width - this.imageWidth) / 2;
      int j = (this.height - this.imageHeight) / 2;
      this.blit(p_98821_, i, j, 0, 0, this.imageWidth, this.imageHeight);
      if (this.horse instanceof AbstractChestedHorse) {
         AbstractChestedHorse abstractchestedhorse = (AbstractChestedHorse)this.horse;
         if (abstractchestedhorse.hasChest()) {
            this.blit(p_98821_, i + 79, j + 17, 0, this.imageHeight, abstractchestedhorse.getInventoryColumns() * 18, 54);
         }
      }

      if (this.horse.isSaddleable()) {
         this.blit(p_98821_, i + 7, j + 35 - 18, 18, this.imageHeight + 54, 18, 18);
      }

      if (this.horse.canWearArmor()) {
         if (this.horse instanceof Llama) {
            this.blit(p_98821_, i + 7, j + 35, 36, this.imageHeight + 54, 18, 18);
         } else {
            this.blit(p_98821_, i + 7, j + 35, 0, this.imageHeight + 54, 18, 18);
         }
      }

      InventoryScreen.renderEntityInInventory(i + 51, j + 60, 17, (float)(i + 51) - this.xMouse, (float)(j + 75 - 50) - this.yMouse, this.horse);
   }

   public void render(PoseStack p_98826_, int p_98827_, int p_98828_, float p_98829_) {
      this.renderBackground(p_98826_);
      this.xMouse = (float)p_98827_;
      this.yMouse = (float)p_98828_;
      super.render(p_98826_, p_98827_, p_98828_, p_98829_);
      this.renderTooltip(p_98826_, p_98827_, p_98828_);
   }
}