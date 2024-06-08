package net.minecraft.world.item.crafting;

import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public interface RecipeSerializer<T extends Recipe<?>> extends net.minecraftforge.registries.IForgeRegistryEntry<RecipeSerializer<?>>, net.minecraftforge.common.extensions.IForgeRecipeSerializer<T> {
   RecipeSerializer<ShapedRecipe> SHAPED_RECIPE = register("crafting_shaped", new ShapedRecipe.Serializer());
   RecipeSerializer<ShapelessRecipe> SHAPELESS_RECIPE = register("crafting_shapeless", new ShapelessRecipe.Serializer());
   SimpleRecipeSerializer<ArmorDyeRecipe> ARMOR_DYE = register("crafting_special_armordye", new SimpleRecipeSerializer<>(ArmorDyeRecipe::new));
   SimpleRecipeSerializer<BookCloningRecipe> BOOK_CLONING = register("crafting_special_bookcloning", new SimpleRecipeSerializer<>(BookCloningRecipe::new));
   SimpleRecipeSerializer<MapCloningRecipe> MAP_CLONING = register("crafting_special_mapcloning", new SimpleRecipeSerializer<>(MapCloningRecipe::new));
   SimpleRecipeSerializer<MapExtendingRecipe> MAP_EXTENDING = register("crafting_special_mapextending", new SimpleRecipeSerializer<>(MapExtendingRecipe::new));
   SimpleRecipeSerializer<FireworkRocketRecipe> FIREWORK_ROCKET = register("crafting_special_firework_rocket", new SimpleRecipeSerializer<>(FireworkRocketRecipe::new));
   SimpleRecipeSerializer<FireworkStarRecipe> FIREWORK_STAR = register("crafting_special_firework_star", new SimpleRecipeSerializer<>(FireworkStarRecipe::new));
   SimpleRecipeSerializer<FireworkStarFadeRecipe> FIREWORK_STAR_FADE = register("crafting_special_firework_star_fade", new SimpleRecipeSerializer<>(FireworkStarFadeRecipe::new));
   SimpleRecipeSerializer<TippedArrowRecipe> TIPPED_ARROW = register("crafting_special_tippedarrow", new SimpleRecipeSerializer<>(TippedArrowRecipe::new));
   SimpleRecipeSerializer<BannerDuplicateRecipe> BANNER_DUPLICATE = register("crafting_special_bannerduplicate", new SimpleRecipeSerializer<>(BannerDuplicateRecipe::new));
   SimpleRecipeSerializer<ShieldDecorationRecipe> SHIELD_DECORATION = register("crafting_special_shielddecoration", new SimpleRecipeSerializer<>(ShieldDecorationRecipe::new));
   SimpleRecipeSerializer<ShulkerBoxColoring> SHULKER_BOX_COLORING = register("crafting_special_shulkerboxcoloring", new SimpleRecipeSerializer<>(ShulkerBoxColoring::new));
   SimpleRecipeSerializer<SuspiciousStewRecipe> SUSPICIOUS_STEW = register("crafting_special_suspiciousstew", new SimpleRecipeSerializer<>(SuspiciousStewRecipe::new));
   SimpleRecipeSerializer<RepairItemRecipe> REPAIR_ITEM = register("crafting_special_repairitem", new SimpleRecipeSerializer<>(RepairItemRecipe::new));
   SimpleCookingSerializer<SmeltingRecipe> SMELTING_RECIPE = register("smelting", new SimpleCookingSerializer<>(SmeltingRecipe::new, 200));
   SimpleCookingSerializer<BlastingRecipe> BLASTING_RECIPE = register("blasting", new SimpleCookingSerializer<>(BlastingRecipe::new, 100));
   SimpleCookingSerializer<SmokingRecipe> SMOKING_RECIPE = register("smoking", new SimpleCookingSerializer<>(SmokingRecipe::new, 100));
   SimpleCookingSerializer<CampfireCookingRecipe> CAMPFIRE_COOKING_RECIPE = register("campfire_cooking", new SimpleCookingSerializer<>(CampfireCookingRecipe::new, 100));
   RecipeSerializer<StonecutterRecipe> STONECUTTER = register("stonecutting", new SingleItemRecipe.Serializer<>(StonecutterRecipe::new));
   RecipeSerializer<UpgradeRecipe> SMITHING = register("smithing", new UpgradeRecipe.Serializer());

   //Forge: use fromJson with IContext if you need the context
   T fromJson(ResourceLocation p_44103_, JsonObject p_44104_);

   @javax.annotation.Nullable
   T fromNetwork(ResourceLocation p_44105_, FriendlyByteBuf p_44106_);

   void toNetwork(FriendlyByteBuf p_44101_, T p_44102_);

   static <S extends RecipeSerializer<T>, T extends Recipe<?>> S register(String p_44099_, S p_44100_) {
      return Registry.register(Registry.RECIPE_SERIALIZER, p_44099_, p_44100_);
   }
}
