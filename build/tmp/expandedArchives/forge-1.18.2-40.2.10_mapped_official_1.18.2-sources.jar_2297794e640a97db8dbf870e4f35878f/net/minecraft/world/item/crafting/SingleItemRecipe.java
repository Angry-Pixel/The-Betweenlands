package net.minecraft.world.item.crafting;

import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public abstract class SingleItemRecipe implements Recipe<Container> {
   protected final Ingredient ingredient;
   protected final ItemStack result;
   private final RecipeType<?> type;
   private final RecipeSerializer<?> serializer;
   protected final ResourceLocation id;
   protected final String group;

   public SingleItemRecipe(RecipeType<?> p_44416_, RecipeSerializer<?> p_44417_, ResourceLocation p_44418_, String p_44419_, Ingredient p_44420_, ItemStack p_44421_) {
      this.type = p_44416_;
      this.serializer = p_44417_;
      this.id = p_44418_;
      this.group = p_44419_;
      this.ingredient = p_44420_;
      this.result = p_44421_;
   }

   public RecipeType<?> getType() {
      return this.type;
   }

   public RecipeSerializer<?> getSerializer() {
      return this.serializer;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public String getGroup() {
      return this.group;
   }

   public ItemStack getResultItem() {
      return this.result;
   }

   public NonNullList<Ingredient> getIngredients() {
      NonNullList<Ingredient> nonnulllist = NonNullList.create();
      nonnulllist.add(this.ingredient);
      return nonnulllist;
   }

   public boolean canCraftInDimensions(int p_44424_, int p_44425_) {
      return true;
   }

   public ItemStack assemble(Container p_44427_) {
      return this.result.copy();
   }

   public static class Serializer<T extends SingleItemRecipe> extends net.minecraftforge.registries.ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<T> {
      final SingleItemRecipe.Serializer.SingleItemMaker<T> factory;

      protected Serializer(SingleItemRecipe.Serializer.SingleItemMaker<T> p_44435_) {
         this.factory = p_44435_;
      }

      public T fromJson(ResourceLocation p_44449_, JsonObject p_44450_) {
         String s = GsonHelper.getAsString(p_44450_, "group", "");
         Ingredient ingredient;
         if (GsonHelper.isArrayNode(p_44450_, "ingredient")) {
            ingredient = Ingredient.fromJson(GsonHelper.getAsJsonArray(p_44450_, "ingredient"));
         } else {
            ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(p_44450_, "ingredient"));
         }

         String s1 = GsonHelper.getAsString(p_44450_, "result");
         int i = GsonHelper.getAsInt(p_44450_, "count");
         ItemStack itemstack = new ItemStack(Registry.ITEM.get(new ResourceLocation(s1)), i);
         return this.factory.create(p_44449_, s, ingredient, itemstack);
      }

      public T fromNetwork(ResourceLocation p_44452_, FriendlyByteBuf p_44453_) {
         String s = p_44453_.readUtf();
         Ingredient ingredient = Ingredient.fromNetwork(p_44453_);
         ItemStack itemstack = p_44453_.readItem();
         return this.factory.create(p_44452_, s, ingredient, itemstack);
      }

      public void toNetwork(FriendlyByteBuf p_44440_, T p_44441_) {
         p_44440_.writeUtf(p_44441_.group);
         p_44441_.ingredient.toNetwork(p_44440_);
         p_44440_.writeItem(p_44441_.result);
      }

      interface SingleItemMaker<T extends SingleItemRecipe> {
         T create(ResourceLocation p_44455_, String p_44456_, Ingredient p_44457_, ItemStack p_44458_);
      }
   }
}
