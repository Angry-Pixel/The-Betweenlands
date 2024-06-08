package net.minecraft.world.inventory;

import java.util.Collections;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

public interface RecipeHolder {
   void setRecipeUsed(@Nullable Recipe<?> p_40134_);

   @Nullable
   Recipe<?> getRecipeUsed();

   default void awardUsedRecipes(Player p_40139_) {
      Recipe<?> recipe = this.getRecipeUsed();
      if (recipe != null && !recipe.isSpecial()) {
         p_40139_.awardRecipes(Collections.singleton(recipe));
         this.setRecipeUsed((Recipe<?>)null);
      }

   }

   default boolean setRecipeUsed(Level p_40136_, ServerPlayer p_40137_, Recipe<?> p_40138_) {
      if (!p_40138_.isSpecial() && p_40136_.getGameRules().getBoolean(GameRules.RULE_LIMITED_CRAFTING) && !p_40137_.getRecipeBook().contains(p_40138_)) {
         return false;
      } else {
         this.setRecipeUsed(p_40138_);
         return true;
      }
   }
}