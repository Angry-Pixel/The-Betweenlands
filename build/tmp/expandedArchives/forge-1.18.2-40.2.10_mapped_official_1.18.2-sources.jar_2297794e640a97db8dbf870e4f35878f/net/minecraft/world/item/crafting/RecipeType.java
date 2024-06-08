package net.minecraft.world.item.crafting;

import java.util.Optional;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;

public interface RecipeType<T extends Recipe<?>> {
   RecipeType<CraftingRecipe> CRAFTING = register("crafting");
   RecipeType<SmeltingRecipe> SMELTING = register("smelting");
   RecipeType<BlastingRecipe> BLASTING = register("blasting");
   RecipeType<SmokingRecipe> SMOKING = register("smoking");
   RecipeType<CampfireCookingRecipe> CAMPFIRE_COOKING = register("campfire_cooking");
   RecipeType<StonecutterRecipe> STONECUTTING = register("stonecutting");
   RecipeType<UpgradeRecipe> SMITHING = register("smithing");

   static <T extends Recipe<?>> RecipeType<T> register(final String p_44120_) {
      return Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(p_44120_), new RecipeType<T>() {
         public String toString() {
            return p_44120_;
         }
      });
   }

   default <C extends Container> Optional<T> tryMatch(Recipe<C> p_44116_, Level p_44117_, C p_44118_) {
      return p_44116_.matches(p_44118_, p_44117_) ? Optional.of((T)p_44116_) : Optional.empty();
   }
}