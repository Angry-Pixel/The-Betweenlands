package net.minecraft.client.gui.screens.recipebook;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface RecipeUpdateListener {
   void recipesUpdated();

   RecipeBookComponent getRecipeBookComponent();
}