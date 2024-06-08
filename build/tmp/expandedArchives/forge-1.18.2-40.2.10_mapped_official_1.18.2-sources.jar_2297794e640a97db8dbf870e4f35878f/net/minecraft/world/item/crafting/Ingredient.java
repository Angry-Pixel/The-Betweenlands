package net.minecraft.world.item.crafting;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class Ingredient implements Predicate<ItemStack> {
   //Because Mojang caches things... we need to invalidate them.. so... here we go..
   private static final java.util.concurrent.atomic.AtomicInteger INVALIDATION_COUNTER = new java.util.concurrent.atomic.AtomicInteger();
   public static void invalidateAll() {
      INVALIDATION_COUNTER.incrementAndGet();
   }

   public static final Ingredient EMPTY = new Ingredient(Stream.empty());
   private final Ingredient.Value[] values;
   @Nullable
   private ItemStack[] itemStacks;
   @Nullable
   private IntList stackingIds;
   private int invalidationCounter;

   protected Ingredient(Stream<? extends Ingredient.Value> p_43907_) {
      this.values = p_43907_.toArray((p_43933_) -> {
         return new Ingredient.Value[p_43933_];
      });
   }

   public ItemStack[] getItems() {
      this.dissolve();
      return this.itemStacks;
   }

   private void dissolve() {
      if (this.itemStacks == null) {
         this.itemStacks = Arrays.stream(this.values).flatMap((p_43916_) -> {
            return p_43916_.getItems().stream();
         }).distinct().toArray((p_43910_) -> {
            return new ItemStack[p_43910_];
         });
      }

   }

   public boolean test(@Nullable ItemStack p_43914_) {
      if (p_43914_ == null) {
         return false;
      } else {
         this.dissolve();
         if (this.itemStacks.length == 0) {
            return p_43914_.isEmpty();
         } else {
            for(ItemStack itemstack : this.itemStacks) {
               if (itemstack.is(p_43914_.getItem())) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   public IntList getStackingIds() {
      if (this.stackingIds == null || checkInvalidation()) {
         this.markValid();
         this.dissolve();
         this.stackingIds = new IntArrayList(this.itemStacks.length);

         for(ItemStack itemstack : this.itemStacks) {
            this.stackingIds.add(StackedContents.getStackingIndex(itemstack));
         }

         this.stackingIds.sort(IntComparators.NATURAL_COMPARATOR);
      }

      return this.stackingIds;
   }

   public final void toNetwork(FriendlyByteBuf p_43924_) {
      this.dissolve();
      if (!this.isVanilla()) {
         net.minecraftforge.common.crafting.CraftingHelper.write(p_43924_, this);
         return;
      }
      p_43924_.writeCollection(Arrays.asList(this.itemStacks), FriendlyByteBuf::writeItem);
   }

   public JsonElement toJson() {
      if (this.values.length == 1) {
         return this.values[0].serialize();
      } else {
         JsonArray jsonarray = new JsonArray();

         for(Ingredient.Value ingredient$value : this.values) {
            jsonarray.add(ingredient$value.serialize());
         }

         return jsonarray;
      }
   }

   public boolean isEmpty() {
      return this.values.length == 0 && (this.itemStacks == null || this.itemStacks.length == 0) && (this.stackingIds == null || this.stackingIds.isEmpty());
   }

   public final boolean checkInvalidation() {
      int currentInvalidationCounter = INVALIDATION_COUNTER.get();
      if (this.invalidationCounter != currentInvalidationCounter) {
         invalidate();
         return true;
      }
      return false;
   }

   protected final void markValid() {
      this.invalidationCounter = INVALIDATION_COUNTER.get();
   }

   protected void invalidate() {
      this.itemStacks = null;
      this.stackingIds = null;
   }

   public boolean isSimple() {
      return true;
   }

   private final boolean isVanilla = this.getClass() == Ingredient.class;
   public final boolean isVanilla() {
       return isVanilla;
   }

   public net.minecraftforge.common.crafting.IIngredientSerializer<? extends Ingredient> getSerializer() {
      if (!isVanilla()) throw new IllegalStateException("Modders must implement Ingredient.getSerializer in their custom Ingredients: " + this);
      return net.minecraftforge.common.crafting.VanillaIngredientSerializer.INSTANCE;
   }

   public static Ingredient fromValues(Stream<? extends Ingredient.Value> p_43939_) {
      Ingredient ingredient = new Ingredient(p_43939_);
      return ingredient.values.length == 0 ? EMPTY : ingredient;
   }

   public static Ingredient of() {
      return EMPTY;
   }

   public static Ingredient of(ItemLike... p_43930_) {
      return of(Arrays.stream(p_43930_).map(ItemStack::new));
   }

   public static Ingredient of(ItemStack... p_43928_) {
      return of(Arrays.stream(p_43928_));
   }

   public static Ingredient of(Stream<ItemStack> p_43922_) {
      return fromValues(p_43922_.filter((p_43944_) -> {
         return !p_43944_.isEmpty();
      }).map(Ingredient.ItemValue::new));
   }

   public static Ingredient of(TagKey<Item> p_204133_) {
      return fromValues(Stream.of(new Ingredient.TagValue(p_204133_)));
   }

   public static Ingredient fromNetwork(FriendlyByteBuf p_43941_) {
      var size = p_43941_.readVarInt();
      if (size == -1) return net.minecraftforge.common.crafting.CraftingHelper.getIngredient(p_43941_.readResourceLocation(), p_43941_);
      return fromValues(Stream.generate(() -> new Ingredient.ItemValue(p_43941_.readItem())).limit(size));
   }

   public static Ingredient fromJson(@Nullable JsonElement p_43918_) {
      if (p_43918_ != null && !p_43918_.isJsonNull()) {
         Ingredient ret = net.minecraftforge.common.crafting.CraftingHelper.getIngredient(p_43918_);
         if (ret != null) return ret;
         if (p_43918_.isJsonObject()) {
            return fromValues(Stream.of(valueFromJson(p_43918_.getAsJsonObject())));
         } else if (p_43918_.isJsonArray()) {
            JsonArray jsonarray = p_43918_.getAsJsonArray();
            if (jsonarray.size() == 0) {
               throw new JsonSyntaxException("Item array cannot be empty, at least one item must be defined");
            } else {
               return fromValues(StreamSupport.stream(jsonarray.spliterator(), false).map((p_151264_) -> {
                  return valueFromJson(GsonHelper.convertToJsonObject(p_151264_, "item"));
               }));
            }
         } else {
            throw new JsonSyntaxException("Expected item to be object or array of objects");
         }
      } else {
         throw new JsonSyntaxException("Item cannot be null");
      }
   }

   public static Ingredient.Value valueFromJson(JsonObject p_43920_) {
      if (p_43920_.has("item") && p_43920_.has("tag")) {
         throw new JsonParseException("An ingredient entry is either a tag or an item, not both");
      } else if (p_43920_.has("item")) {
         Item item = ShapedRecipe.itemFromJson(p_43920_);
         return new Ingredient.ItemValue(new ItemStack(item));
      } else if (p_43920_.has("tag")) {
         ResourceLocation resourcelocation = new ResourceLocation(GsonHelper.getAsString(p_43920_, "tag"));
         TagKey<Item> tagkey = TagKey.create(Registry.ITEM_REGISTRY, resourcelocation);
         return new Ingredient.TagValue(tagkey);
      } else {
         throw new JsonParseException("An ingredient entry needs either a tag or an item");
      }
   }

   //Merges several vanilla Ingredients together. As a quirk of how the json is structured, we can't tell if its a single Ingredient type or multiple so we split per item and re-merge here.
   //Only public for internal use, so we can access a private field in here.
   public static Ingredient merge(Collection<Ingredient> parts) {
      return fromValues(parts.stream().flatMap(i -> Arrays.stream(i.values)));
   }

   public static class ItemValue implements Ingredient.Value {
      private final ItemStack item;

      public ItemValue(ItemStack p_43953_) {
         this.item = p_43953_;
      }

      public Collection<ItemStack> getItems() {
         return Collections.singleton(this.item);
      }

      public JsonObject serialize() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.addProperty("item", Registry.ITEM.getKey(this.item.getItem()).toString());
         return jsonobject;
      }
   }

   public static class TagValue implements Ingredient.Value {
      private final TagKey<Item> tag;

      public TagValue(TagKey<Item> p_204135_) {
         this.tag = p_204135_;
      }

      public Collection<ItemStack> getItems() {
         List<ItemStack> list = Lists.newArrayList();

         for(Holder<Item> holder : Registry.ITEM.getTagOrEmpty(this.tag)) {
            list.add(new ItemStack(holder));
         }

         if (list.size() == 0 && !net.minecraftforge.common.ForgeConfig.SERVER.treatEmptyTagsAsAir.get()) {
            list.add(new ItemStack(net.minecraft.world.level.block.Blocks.BARRIER).setHoverName(new net.minecraft.network.chat.TextComponent("Empty Tag: " + this.tag.location())));
         }
         return list;
      }

      public JsonObject serialize() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.addProperty("tag", this.tag.location().toString());
         return jsonobject;
      }
   }

   public interface Value {
      Collection<ItemStack> getItems();

      JsonObject serialize();
   }
}
