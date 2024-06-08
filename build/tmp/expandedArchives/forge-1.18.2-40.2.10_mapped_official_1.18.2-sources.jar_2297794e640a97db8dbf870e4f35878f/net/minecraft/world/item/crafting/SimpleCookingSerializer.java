package net.minecraft.world.item.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;

public class SimpleCookingSerializer<T extends AbstractCookingRecipe> extends net.minecraftforge.registries.ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<T> {
   private final int defaultCookingTime;
   private final SimpleCookingSerializer.CookieBaker<T> factory;

   public SimpleCookingSerializer(SimpleCookingSerializer.CookieBaker<T> p_44330_, int p_44331_) {
      this.defaultCookingTime = p_44331_;
      this.factory = p_44330_;
   }

   public T fromJson(ResourceLocation p_44347_, JsonObject p_44348_) {
      String s = GsonHelper.getAsString(p_44348_, "group", "");
      JsonElement jsonelement = (JsonElement)(GsonHelper.isArrayNode(p_44348_, "ingredient") ? GsonHelper.getAsJsonArray(p_44348_, "ingredient") : GsonHelper.getAsJsonObject(p_44348_, "ingredient"));
      Ingredient ingredient = Ingredient.fromJson(jsonelement);
      //Forge: Check if primitive string to keep vanilla or a object which can contain a count field.
      if (!p_44348_.has("result")) throw new com.google.gson.JsonSyntaxException("Missing result, expected to find a string or object");
      ItemStack itemstack;
      if (p_44348_.get("result").isJsonObject()) itemstack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(p_44348_, "result"));
      else {
      String s1 = GsonHelper.getAsString(p_44348_, "result");
      ResourceLocation resourcelocation = new ResourceLocation(s1);
      itemstack = new ItemStack(Registry.ITEM.getOptional(resourcelocation).orElseThrow(() -> {
         return new IllegalStateException("Item: " + s1 + " does not exist");
      }));
      }
      float f = GsonHelper.getAsFloat(p_44348_, "experience", 0.0F);
      int i = GsonHelper.getAsInt(p_44348_, "cookingtime", this.defaultCookingTime);
      return this.factory.create(p_44347_, s, ingredient, itemstack, f, i);
   }

   public T fromNetwork(ResourceLocation p_44350_, FriendlyByteBuf p_44351_) {
      String s = p_44351_.readUtf();
      Ingredient ingredient = Ingredient.fromNetwork(p_44351_);
      ItemStack itemstack = p_44351_.readItem();
      float f = p_44351_.readFloat();
      int i = p_44351_.readVarInt();
      return this.factory.create(p_44350_, s, ingredient, itemstack, f, i);
   }

   public void toNetwork(FriendlyByteBuf p_44335_, T p_44336_) {
      p_44335_.writeUtf(p_44336_.group);
      p_44336_.ingredient.toNetwork(p_44335_);
      p_44335_.writeItem(p_44336_.result);
      p_44335_.writeFloat(p_44336_.experience);
      p_44335_.writeVarInt(p_44336_.cookingTime);
   }

   interface CookieBaker<T extends AbstractCookingRecipe> {
      T create(ResourceLocation p_44353_, String p_44354_, Ingredient p_44355_, ItemStack p_44356_, float p_44357_, int p_44358_);
   }
}
