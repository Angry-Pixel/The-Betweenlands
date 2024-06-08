package net.minecraft.world.item.crafting;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class ShapedRecipe implements CraftingRecipe, net.minecraftforge.common.crafting.IShapedRecipe<CraftingContainer> {
   static int MAX_WIDTH = 3;
   static int MAX_HEIGHT = 3;
   /**
    * Expand the max width and height allowed in the deserializer.
    * This should be called by modders who add custom crafting tables that are larger than the vanilla 3x3.
    * @param width your max recipe width
    * @param height your max recipe height
    */
   public static void setCraftingSize(int width, int height) {
      if (MAX_WIDTH < width) MAX_WIDTH = width;
      if (MAX_HEIGHT < height) MAX_HEIGHT = height;
   }

   final int width;
   final int height;
   final NonNullList<Ingredient> recipeItems;
   final ItemStack result;
   private final ResourceLocation id;
   final String group;

   public ShapedRecipe(ResourceLocation p_44153_, String p_44154_, int p_44155_, int p_44156_, NonNullList<Ingredient> p_44157_, ItemStack p_44158_) {
      this.id = p_44153_;
      this.group = p_44154_;
      this.width = p_44155_;
      this.height = p_44156_;
      this.recipeItems = p_44157_;
      this.result = p_44158_;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public RecipeSerializer<?> getSerializer() {
      return RecipeSerializer.SHAPED_RECIPE;
   }

   public String getGroup() {
      return this.group;
   }

   public ItemStack getResultItem() {
      return this.result;
   }

   public NonNullList<Ingredient> getIngredients() {
      return this.recipeItems;
   }

   public boolean canCraftInDimensions(int p_44161_, int p_44162_) {
      return p_44161_ >= this.width && p_44162_ >= this.height;
   }

   public boolean matches(CraftingContainer p_44176_, Level p_44177_) {
      for(int i = 0; i <= p_44176_.getWidth() - this.width; ++i) {
         for(int j = 0; j <= p_44176_.getHeight() - this.height; ++j) {
            if (this.matches(p_44176_, i, j, true)) {
               return true;
            }

            if (this.matches(p_44176_, i, j, false)) {
               return true;
            }
         }
      }

      return false;
   }

   private boolean matches(CraftingContainer p_44171_, int p_44172_, int p_44173_, boolean p_44174_) {
      for(int i = 0; i < p_44171_.getWidth(); ++i) {
         for(int j = 0; j < p_44171_.getHeight(); ++j) {
            int k = i - p_44172_;
            int l = j - p_44173_;
            Ingredient ingredient = Ingredient.EMPTY;
            if (k >= 0 && l >= 0 && k < this.width && l < this.height) {
               if (p_44174_) {
                  ingredient = this.recipeItems.get(this.width - k - 1 + l * this.width);
               } else {
                  ingredient = this.recipeItems.get(k + l * this.width);
               }
            }

            if (!ingredient.test(p_44171_.getItem(i + j * p_44171_.getWidth()))) {
               return false;
            }
         }
      }

      return true;
   }

   public ItemStack assemble(CraftingContainer p_44169_) {
      return this.getResultItem().copy();
   }

   public int getWidth() {
      return this.width;
   }

   @Override
   public int getRecipeWidth() {
      return getWidth();
   }

   public int getHeight() {
      return this.height;
   }

   @Override
   public int getRecipeHeight() {
      return getHeight();
   }

   static NonNullList<Ingredient> dissolvePattern(String[] p_44203_, Map<String, Ingredient> p_44204_, int p_44205_, int p_44206_) {
      NonNullList<Ingredient> nonnulllist = NonNullList.withSize(p_44205_ * p_44206_, Ingredient.EMPTY);
      Set<String> set = Sets.newHashSet(p_44204_.keySet());
      set.remove(" ");

      for(int i = 0; i < p_44203_.length; ++i) {
         for(int j = 0; j < p_44203_[i].length(); ++j) {
            String s = p_44203_[i].substring(j, j + 1);
            Ingredient ingredient = p_44204_.get(s);
            if (ingredient == null) {
               throw new JsonSyntaxException("Pattern references symbol '" + s + "' but it's not defined in the key");
            }

            set.remove(s);
            nonnulllist.set(j + p_44205_ * i, ingredient);
         }
      }

      if (!set.isEmpty()) {
         throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + set);
      } else {
         return nonnulllist;
      }
   }

   @VisibleForTesting
   static String[] shrink(String... p_44187_) {
      int i = Integer.MAX_VALUE;
      int j = 0;
      int k = 0;
      int l = 0;

      for(int i1 = 0; i1 < p_44187_.length; ++i1) {
         String s = p_44187_[i1];
         i = Math.min(i, firstNonSpace(s));
         int j1 = lastNonSpace(s);
         j = Math.max(j, j1);
         if (j1 < 0) {
            if (k == i1) {
               ++k;
            }

            ++l;
         } else {
            l = 0;
         }
      }

      if (p_44187_.length == l) {
         return new String[0];
      } else {
         String[] astring = new String[p_44187_.length - l - k];

         for(int k1 = 0; k1 < astring.length; ++k1) {
            astring[k1] = p_44187_[k1 + k].substring(i, j + 1);
         }

         return astring;
      }
   }

   public boolean isIncomplete() {
      NonNullList<Ingredient> nonnulllist = this.getIngredients();
      return nonnulllist.isEmpty() || nonnulllist.stream().filter((p_151277_) -> {
         return !p_151277_.isEmpty();
      }).anyMatch((p_151273_) -> {
         return net.minecraftforge.common.ForgeHooks.hasNoElements(p_151273_);
      });
   }

   private static int firstNonSpace(String p_44185_) {
      int i;
      for(i = 0; i < p_44185_.length() && p_44185_.charAt(i) == ' '; ++i) {
      }

      return i;
   }

   private static int lastNonSpace(String p_44201_) {
      int i;
      for(i = p_44201_.length() - 1; i >= 0 && p_44201_.charAt(i) == ' '; --i) {
      }

      return i;
   }

   static String[] patternFromJson(JsonArray p_44197_) {
      String[] astring = new String[p_44197_.size()];
      if (astring.length > MAX_HEIGHT) {
         throw new JsonSyntaxException("Invalid pattern: too many rows, " + MAX_HEIGHT + " is maximum");
      } else if (astring.length == 0) {
         throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
      } else {
         for(int i = 0; i < astring.length; ++i) {
            String s = GsonHelper.convertToString(p_44197_.get(i), "pattern[" + i + "]");
            if (s.length() > MAX_WIDTH) {
               throw new JsonSyntaxException("Invalid pattern: too many columns, " + MAX_WIDTH + " is maximum");
            }

            if (i > 0 && astring[0].length() != s.length()) {
               throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
            }

            astring[i] = s;
         }

         return astring;
      }
   }

   static Map<String, Ingredient> keyFromJson(JsonObject p_44211_) {
      Map<String, Ingredient> map = Maps.newHashMap();

      for(Entry<String, JsonElement> entry : p_44211_.entrySet()) {
         if (entry.getKey().length() != 1) {
            throw new JsonSyntaxException("Invalid key entry: '" + (String)entry.getKey() + "' is an invalid symbol (must be 1 character only).");
         }

         if (" ".equals(entry.getKey())) {
            throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
         }

         map.put(entry.getKey(), Ingredient.fromJson(entry.getValue()));
      }

      map.put(" ", Ingredient.EMPTY);
      return map;
   }

   public static ItemStack itemStackFromJson(JsonObject p_151275_) {
      return net.minecraftforge.common.crafting.CraftingHelper.getItemStack(p_151275_, true, true);
   }

   public static Item itemFromJson(JsonObject p_151279_) {
      String s = GsonHelper.getAsString(p_151279_, "item");
      Item item = Registry.ITEM.getOptional(new ResourceLocation(s)).orElseThrow(() -> {
         return new JsonSyntaxException("Unknown item '" + s + "'");
      });
      if (item == Items.AIR) {
         throw new JsonSyntaxException("Invalid item: " + s);
      } else {
         return item;
      }
   }

   public static class Serializer extends net.minecraftforge.registries.ForgeRegistryEntry<RecipeSerializer<?>>  implements RecipeSerializer<ShapedRecipe> {
      private static final ResourceLocation NAME = new ResourceLocation("minecraft", "crafting_shaped");
      public ShapedRecipe fromJson(ResourceLocation p_44236_, JsonObject p_44237_) {
         String s = GsonHelper.getAsString(p_44237_, "group", "");
         Map<String, Ingredient> map = ShapedRecipe.keyFromJson(GsonHelper.getAsJsonObject(p_44237_, "key"));
         String[] astring = ShapedRecipe.shrink(ShapedRecipe.patternFromJson(GsonHelper.getAsJsonArray(p_44237_, "pattern")));
         int i = astring[0].length();
         int j = astring.length;
         NonNullList<Ingredient> nonnulllist = ShapedRecipe.dissolvePattern(astring, map, i, j);
         ItemStack itemstack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(p_44237_, "result"));
         return new ShapedRecipe(p_44236_, s, i, j, nonnulllist, itemstack);
      }

      public ShapedRecipe fromNetwork(ResourceLocation p_44239_, FriendlyByteBuf p_44240_) {
         int i = p_44240_.readVarInt();
         int j = p_44240_.readVarInt();
         String s = p_44240_.readUtf();
         NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i * j, Ingredient.EMPTY);

         for(int k = 0; k < nonnulllist.size(); ++k) {
            nonnulllist.set(k, Ingredient.fromNetwork(p_44240_));
         }

         ItemStack itemstack = p_44240_.readItem();
         return new ShapedRecipe(p_44239_, s, i, j, nonnulllist, itemstack);
      }

      public void toNetwork(FriendlyByteBuf p_44227_, ShapedRecipe p_44228_) {
         p_44227_.writeVarInt(p_44228_.width);
         p_44227_.writeVarInt(p_44228_.height);
         p_44227_.writeUtf(p_44228_.group);

         for(Ingredient ingredient : p_44228_.recipeItems) {
            ingredient.toNetwork(p_44227_);
         }

         p_44227_.writeItem(p_44228_.result);
      }
   }
}
