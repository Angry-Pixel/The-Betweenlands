package net.minecraft.world.item.crafting;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public abstract class AbstractCookingRecipe implements Recipe<Container> {
   protected final RecipeType<?> type;
   protected final ResourceLocation id;
   protected final String group;
   protected final Ingredient ingredient;
   protected final ItemStack result;
   protected final float experience;
   protected final int cookingTime;

   public AbstractCookingRecipe(RecipeType<?> p_43734_, ResourceLocation p_43735_, String p_43736_, Ingredient p_43737_, ItemStack p_43738_, float p_43739_, int p_43740_) {
      this.type = p_43734_;
      this.id = p_43735_;
      this.group = p_43736_;
      this.ingredient = p_43737_;
      this.result = p_43738_;
      this.experience = p_43739_;
      this.cookingTime = p_43740_;
   }

   public boolean matches(Container p_43748_, Level p_43749_) {
      return this.ingredient.test(p_43748_.getItem(0));
   }

   public ItemStack assemble(Container p_43746_) {
      return this.result.copy();
   }

   public boolean canCraftInDimensions(int p_43743_, int p_43744_) {
      return true;
   }

   public NonNullList<Ingredient> getIngredients() {
      NonNullList<Ingredient> nonnulllist = NonNullList.create();
      nonnulllist.add(this.ingredient);
      return nonnulllist;
   }

   public float getExperience() {
      return this.experience;
   }

   public ItemStack getResultItem() {
      return this.result;
   }

   public String getGroup() {
      return this.group;
   }

   public int getCookingTime() {
      return this.cookingTime;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public RecipeType<?> getType() {
      return this.type;
   }
}