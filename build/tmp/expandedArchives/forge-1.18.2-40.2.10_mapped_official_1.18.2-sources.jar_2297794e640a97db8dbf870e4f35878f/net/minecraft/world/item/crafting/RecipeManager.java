package net.minecraft.world.item.crafting;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

public class RecipeManager extends SimpleJsonResourceReloadListener {
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
   private static final Logger LOGGER = LogUtils.getLogger();
   private Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> recipes = ImmutableMap.of();
   private Map<ResourceLocation, Recipe<?>> byName = ImmutableMap.of();
   private boolean hasErrors;
   private final net.minecraftforge.common.crafting.conditions.ICondition.IContext context; //Forge: add context

   /** @deprecated Forge: use {@linkplain RecipeManager#RecipeManager(net.minecraftforge.common.crafting.conditions.ICondition.IContext) constructor with context}. */
   @Deprecated
   public RecipeManager() {
      this(net.minecraftforge.common.crafting.conditions.ICondition.IContext.EMPTY);
   }

   public RecipeManager(net.minecraftforge.common.crafting.conditions.ICondition.IContext context) {
      super(GSON, "recipes");
      this.context = context;
   }

   protected void apply(Map<ResourceLocation, JsonElement> p_44037_, ResourceManager p_44038_, ProfilerFiller p_44039_) {
      this.hasErrors = false;
      Map<RecipeType<?>, Builder<ResourceLocation, Recipe<?>>> map = Maps.newHashMap();
      Builder<ResourceLocation, Recipe<?>> builder = ImmutableMap.builder();

      for(Entry<ResourceLocation, JsonElement> entry : p_44037_.entrySet()) {
         ResourceLocation resourcelocation = entry.getKey();
         if (resourcelocation.getPath().startsWith("_")) continue; //Forge: filter anything beginning with "_" as it's used for metadata.

         try {
            if (entry.getValue().isJsonObject() && !net.minecraftforge.common.crafting.CraftingHelper.processConditions(entry.getValue().getAsJsonObject(), "conditions", this.context)) {
               LOGGER.debug("Skipping loading recipe {} as it's conditions were not met", resourcelocation);
               continue;
            }
            Recipe<?> recipe = fromJson(resourcelocation, GsonHelper.convertToJsonObject(entry.getValue(), "top element"), this.context);
            if (recipe == null) {
               LOGGER.info("Skipping loading recipe {} as it's serializer returned null", resourcelocation);
               continue;
            }
            map.computeIfAbsent(recipe.getType(), (p_44075_) -> {
               return ImmutableMap.builder();
            }).put(resourcelocation, recipe);
            builder.put(resourcelocation, recipe);
         } catch (IllegalArgumentException | JsonParseException jsonparseexception) {
            LOGGER.error("Parsing error loading recipe {}", resourcelocation, jsonparseexception);
         }
      }

      this.recipes = map.entrySet().stream().collect(ImmutableMap.toImmutableMap(Entry::getKey, (p_44033_) -> {
         return p_44033_.getValue().build();
      }));
      this.byName = builder.build();
      LOGGER.info("Loaded {} recipes", (int)map.size());
   }

   public boolean hadErrorsLoading() {
      return this.hasErrors;
   }

   public <C extends Container, T extends Recipe<C>> Optional<T> getRecipeFor(RecipeType<T> p_44016_, C p_44017_, Level p_44018_) {
      return this.byType(p_44016_).values().stream().flatMap((p_44064_) -> {
         return p_44016_.tryMatch(p_44064_, p_44018_, p_44017_).stream();
      }).findFirst();
   }

   public <C extends Container, T extends Recipe<C>> List<T> getAllRecipesFor(RecipeType<T> p_44014_) {
      return this.byType(p_44014_).values().stream().map((p_44053_) -> {
         return (T)p_44053_;
      }).collect(Collectors.toList());
   }

   public <C extends Container, T extends Recipe<C>> List<T> getRecipesFor(RecipeType<T> p_44057_, C p_44058_, Level p_44059_) {
      return this.byType(p_44057_).values().stream().flatMap((p_44023_) -> {
         return p_44057_.tryMatch(p_44023_, p_44059_, p_44058_).stream();
      }).sorted(Comparator.comparing((p_44012_) -> {
         return p_44012_.getResultItem().getDescriptionId();
      })).collect(Collectors.toList());
   }

   private <C extends Container, T extends Recipe<C>> Map<ResourceLocation, Recipe<C>> byType(RecipeType<T> p_44055_) {
      return (Map<ResourceLocation, Recipe<C>>)(Map<ResourceLocation, T>)this.recipes.getOrDefault(p_44055_, Collections.emptyMap());
   }

   public <C extends Container, T extends Recipe<C>> NonNullList<ItemStack> getRemainingItemsFor(RecipeType<T> p_44070_, C p_44071_, Level p_44072_) {
      Optional<T> optional = this.getRecipeFor(p_44070_, p_44071_, p_44072_);
      if (optional.isPresent()) {
         return optional.get().getRemainingItems(p_44071_);
      } else {
         NonNullList<ItemStack> nonnulllist = NonNullList.withSize(p_44071_.getContainerSize(), ItemStack.EMPTY);

         for(int i = 0; i < nonnulllist.size(); ++i) {
            nonnulllist.set(i, p_44071_.getItem(i));
         }

         return nonnulllist;
      }
   }

   public Optional<? extends Recipe<?>> byKey(ResourceLocation p_44044_) {
      return Optional.ofNullable(this.byName.get(p_44044_));
   }

   public Collection<Recipe<?>> getRecipes() {
      return this.recipes.values().stream().flatMap((p_199910_) -> {
         return p_199910_.values().stream();
      }).collect(Collectors.toSet());
   }

   public Stream<ResourceLocation> getRecipeIds() {
      return this.recipes.values().stream().flatMap((p_199904_) -> {
         return p_199904_.keySet().stream();
      });
   }

   /** @deprecated Forge: use {@linkplain #fromJson(ResourceLocation, JsonObject, net.minecraftforge.common.crafting.conditions.ICondition.IContext) overload with context}. */
   @Deprecated
   public static Recipe<?> fromJson(ResourceLocation p_44046_, JsonObject p_44047_) {
      return fromJson(p_44046_, p_44047_, net.minecraftforge.common.crafting.conditions.ICondition.IContext.EMPTY);
   }

   public static Recipe<?> fromJson(ResourceLocation p_44046_, JsonObject p_44047_, net.minecraftforge.common.crafting.conditions.ICondition.IContext context) {
      String s = GsonHelper.getAsString(p_44047_, "type");
      return Registry.RECIPE_SERIALIZER.getOptional(new ResourceLocation(s)).orElseThrow(() -> {
         return new JsonSyntaxException("Invalid or unsupported recipe type '" + s + "'");
      }).fromJson(p_44046_, p_44047_, context);
   }

   public void replaceRecipes(Iterable<Recipe<?>> p_44025_) {
      this.hasErrors = false;
      Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> map = Maps.newHashMap();
      Builder<ResourceLocation, Recipe<?>> builder = ImmutableMap.builder();
      p_44025_.forEach((p_199908_) -> {
         Map<ResourceLocation, Recipe<?>> map1 = map.computeIfAbsent(p_199908_.getType(), (p_199912_) -> {
            return Maps.newHashMap();
         });
         ResourceLocation resourcelocation = p_199908_.getId();
         Recipe<?> recipe = map1.put(resourcelocation, p_199908_);
         builder.put(resourcelocation, p_199908_);
         if (recipe != null) {
            throw new IllegalStateException("Duplicate recipe ignored with ID " + resourcelocation);
         }
      });
      this.recipes = ImmutableMap.copyOf(map);
      this.byName = builder.build();
   }
}
