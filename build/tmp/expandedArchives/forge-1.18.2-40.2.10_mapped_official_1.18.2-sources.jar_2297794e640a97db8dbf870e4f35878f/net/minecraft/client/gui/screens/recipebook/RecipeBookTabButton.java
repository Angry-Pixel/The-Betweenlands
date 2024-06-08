package net.minecraft.client.gui.screens.recipebook;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RecipeBookTabButton extends StateSwitchingButton {
   private final RecipeBookCategories category;
   private static final float ANIMATION_TIME = 15.0F;
   private float animationTime;

   public RecipeBookTabButton(RecipeBookCategories p_100448_) {
      super(0, 0, 35, 27, false);
      this.category = p_100448_;
      this.initTextureValues(153, 2, 35, 0, RecipeBookComponent.RECIPE_BOOK_LOCATION);
   }

   public void startAnimation(Minecraft p_100452_) {
      ClientRecipeBook clientrecipebook = p_100452_.player.getRecipeBook();
      List<RecipeCollection> list = clientrecipebook.getCollection(this.category);
      if (p_100452_.player.containerMenu instanceof RecipeBookMenu) {
         for(RecipeCollection recipecollection : list) {
            for(Recipe<?> recipe : recipecollection.getRecipes(clientrecipebook.isFiltering((RecipeBookMenu)p_100452_.player.containerMenu))) {
               if (clientrecipebook.willHighlight(recipe)) {
                  this.animationTime = 15.0F;
                  return;
               }
            }
         }

      }
   }

   public void renderButton(PoseStack p_100457_, int p_100458_, int p_100459_, float p_100460_) {
      if (this.animationTime > 0.0F) {
         float f = 1.0F + 0.1F * (float)Math.sin((double)(this.animationTime / 15.0F * (float)Math.PI));
         p_100457_.pushPose();
         p_100457_.translate((double)(this.x + 8), (double)(this.y + 12), 0.0D);
         p_100457_.scale(1.0F, f, 1.0F);
         p_100457_.translate((double)(-(this.x + 8)), (double)(-(this.y + 12)), 0.0D);
      }

      Minecraft minecraft = Minecraft.getInstance();
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderTexture(0, this.resourceLocation);
      RenderSystem.disableDepthTest();
      int i = this.xTexStart;
      int j = this.yTexStart;
      if (this.isStateTriggered) {
         i += this.xDiffTex;
      }

      if (this.isHoveredOrFocused()) {
         j += this.yDiffTex;
      }

      int k = this.x;
      if (this.isStateTriggered) {
         k -= 2;
      }

      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      this.blit(p_100457_, k, this.y, i, j, this.width, this.height);
      RenderSystem.enableDepthTest();
      this.renderIcon(minecraft.getItemRenderer());
      if (this.animationTime > 0.0F) {
         p_100457_.popPose();
         this.animationTime -= p_100460_;
      }

   }

   private void renderIcon(ItemRenderer p_100454_) {
      List<ItemStack> list = this.category.getIconItems();
      int i = this.isStateTriggered ? -2 : 0;
      if (list.size() == 1) {
         p_100454_.renderAndDecorateFakeItem(list.get(0), this.x + 9 + i, this.y + 5);
      } else if (list.size() == 2) {
         p_100454_.renderAndDecorateFakeItem(list.get(0), this.x + 3 + i, this.y + 5);
         p_100454_.renderAndDecorateFakeItem(list.get(1), this.x + 14 + i, this.y + 5);
      }

   }

   public RecipeBookCategories getCategory() {
      return this.category;
   }

   public boolean updateVisibility(ClientRecipeBook p_100450_) {
      List<RecipeCollection> list = p_100450_.getCollection(this.category);
      this.visible = false;
      if (list != null) {
         for(RecipeCollection recipecollection : list) {
            if (recipecollection.hasKnownRecipes() && recipecollection.hasFitting()) {
               this.visible = true;
               break;
            }
         }
      }

      return this.visible;
   }
}