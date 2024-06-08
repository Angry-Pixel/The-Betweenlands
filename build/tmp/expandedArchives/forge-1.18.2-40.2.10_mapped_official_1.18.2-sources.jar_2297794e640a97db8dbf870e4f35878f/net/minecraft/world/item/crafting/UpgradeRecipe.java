package net.minecraft.world.item.crafting;

import com.google.gson.JsonObject;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class UpgradeRecipe implements Recipe<Container> {
   final Ingredient base;
   final Ingredient addition;
   final ItemStack result;
   private final ResourceLocation id;

   public UpgradeRecipe(ResourceLocation p_44523_, Ingredient p_44524_, Ingredient p_44525_, ItemStack p_44526_) {
      this.id = p_44523_;
      this.base = p_44524_;
      this.addition = p_44525_;
      this.result = p_44526_;
   }

   public boolean matches(Container p_44533_, Level p_44534_) {
      return this.base.test(p_44533_.getItem(0)) && this.addition.test(p_44533_.getItem(1));
   }

   public ItemStack assemble(Container p_44531_) {
      ItemStack itemstack = this.result.copy();
      CompoundTag compoundtag = p_44531_.getItem(0).getTag();
      if (compoundtag != null) {
         itemstack.setTag(compoundtag.copy());
      }

      return itemstack;
   }

   public boolean canCraftInDimensions(int p_44528_, int p_44529_) {
      return p_44528_ * p_44529_ >= 2;
   }

   public ItemStack getResultItem() {
      return this.result;
   }

   public boolean isAdditionIngredient(ItemStack p_44536_) {
      return this.addition.test(p_44536_);
   }

   public ItemStack getToastSymbol() {
      return new ItemStack(Blocks.SMITHING_TABLE);
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public RecipeSerializer<?> getSerializer() {
      return RecipeSerializer.SMITHING;
   }

   public RecipeType<?> getType() {
      return RecipeType.SMITHING;
   }

   public boolean isIncomplete() {
      return Stream.of(this.base, this.addition).anyMatch((p_151284_) -> {
         return net.minecraftforge.common.ForgeHooks.hasNoElements(p_151284_);
      });
   }

   public static class Serializer extends net.minecraftforge.registries.ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<UpgradeRecipe> {
      public UpgradeRecipe fromJson(ResourceLocation p_44562_, JsonObject p_44563_) {
         Ingredient ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(p_44563_, "base"));
         Ingredient ingredient1 = Ingredient.fromJson(GsonHelper.getAsJsonObject(p_44563_, "addition"));
         ItemStack itemstack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(p_44563_, "result"));
         return new UpgradeRecipe(p_44562_, ingredient, ingredient1, itemstack);
      }

      public UpgradeRecipe fromNetwork(ResourceLocation p_44565_, FriendlyByteBuf p_44566_) {
         Ingredient ingredient = Ingredient.fromNetwork(p_44566_);
         Ingredient ingredient1 = Ingredient.fromNetwork(p_44566_);
         ItemStack itemstack = p_44566_.readItem();
         return new UpgradeRecipe(p_44565_, ingredient, ingredient1, itemstack);
      }

      public void toNetwork(FriendlyByteBuf p_44553_, UpgradeRecipe p_44554_) {
         p_44554_.base.toNetwork(p_44553_);
         p_44554_.addition.toNetwork(p_44553_);
         p_44553_.writeItem(p_44554_.result);
      }
   }
}
