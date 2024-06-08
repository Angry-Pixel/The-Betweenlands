package net.minecraft.data.recipes;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;

public class ShapelessRecipeBuilder implements RecipeBuilder {
   private final Item result;
   private final int count;
   private final List<Ingredient> ingredients = Lists.newArrayList();
   private final Advancement.Builder advancement = Advancement.Builder.advancement();
   @Nullable
   private String group;

   public ShapelessRecipeBuilder(ItemLike p_126180_, int p_126181_) {
      this.result = p_126180_.asItem();
      this.count = p_126181_;
   }

   public static ShapelessRecipeBuilder shapeless(ItemLike p_126190_) {
      return new ShapelessRecipeBuilder(p_126190_, 1);
   }

   public static ShapelessRecipeBuilder shapeless(ItemLike p_126192_, int p_126193_) {
      return new ShapelessRecipeBuilder(p_126192_, p_126193_);
   }

   public ShapelessRecipeBuilder requires(TagKey<Item> p_206420_) {
      return this.requires(Ingredient.of(p_206420_));
   }

   public ShapelessRecipeBuilder requires(ItemLike p_126210_) {
      return this.requires(p_126210_, 1);
   }

   public ShapelessRecipeBuilder requires(ItemLike p_126212_, int p_126213_) {
      for(int i = 0; i < p_126213_; ++i) {
         this.requires(Ingredient.of(p_126212_));
      }

      return this;
   }

   public ShapelessRecipeBuilder requires(Ingredient p_126185_) {
      return this.requires(p_126185_, 1);
   }

   public ShapelessRecipeBuilder requires(Ingredient p_126187_, int p_126188_) {
      for(int i = 0; i < p_126188_; ++i) {
         this.ingredients.add(p_126187_);
      }

      return this;
   }

   public ShapelessRecipeBuilder unlockedBy(String p_126197_, CriterionTriggerInstance p_126198_) {
      this.advancement.addCriterion(p_126197_, p_126198_);
      return this;
   }

   public ShapelessRecipeBuilder group(@Nullable String p_126195_) {
      this.group = p_126195_;
      return this;
   }

   public Item getResult() {
      return this.result;
   }

   public void save(Consumer<FinishedRecipe> p_126205_, ResourceLocation p_126206_) {
      this.ensureValid(p_126206_);
      this.advancement.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(p_126206_)).rewards(AdvancementRewards.Builder.recipe(p_126206_)).requirements(RequirementsStrategy.OR);
      p_126205_.accept(new ShapelessRecipeBuilder.Result(p_126206_, this.result, this.count, this.group == null ? "" : this.group, this.ingredients, this.advancement, new ResourceLocation(p_126206_.getNamespace(), "recipes/" + this.result.getItemCategory().getRecipeFolderName() + "/" + p_126206_.getPath())));
   }

   private void ensureValid(ResourceLocation p_126208_) {
      if (this.advancement.getCriteria().isEmpty()) {
         throw new IllegalStateException("No way of obtaining recipe " + p_126208_);
      }
   }

   public static class Result implements FinishedRecipe {
      private final ResourceLocation id;
      private final Item result;
      private final int count;
      private final String group;
      private final List<Ingredient> ingredients;
      private final Advancement.Builder advancement;
      private final ResourceLocation advancementId;

      public Result(ResourceLocation p_126222_, Item p_126223_, int p_126224_, String p_126225_, List<Ingredient> p_126226_, Advancement.Builder p_126227_, ResourceLocation p_126228_) {
         this.id = p_126222_;
         this.result = p_126223_;
         this.count = p_126224_;
         this.group = p_126225_;
         this.ingredients = p_126226_;
         this.advancement = p_126227_;
         this.advancementId = p_126228_;
      }

      public void serializeRecipeData(JsonObject p_126230_) {
         if (!this.group.isEmpty()) {
            p_126230_.addProperty("group", this.group);
         }

         JsonArray jsonarray = new JsonArray();

         for(Ingredient ingredient : this.ingredients) {
            jsonarray.add(ingredient.toJson());
         }

         p_126230_.add("ingredients", jsonarray);
         JsonObject jsonobject = new JsonObject();
         jsonobject.addProperty("item", Registry.ITEM.getKey(this.result).toString());
         if (this.count > 1) {
            jsonobject.addProperty("count", this.count);
         }

         p_126230_.add("result", jsonobject);
      }

      public RecipeSerializer<?> getType() {
         return RecipeSerializer.SHAPELESS_RECIPE;
      }

      public ResourceLocation getId() {
         return this.id;
      }

      @Nullable
      public JsonObject serializeAdvancement() {
         return this.advancement.serializeToJson();
      }

      @Nullable
      public ResourceLocation getAdvancementId() {
         return this.advancementId;
      }
   }
}