package net.minecraft.client.gui.screens.recipebook;

import java.util.List;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface RecipeShownListener {
   void recipesShown(List<Recipe<?>> p_100518_);
}