package net.minecraft.client;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.logging.LogUtils;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.core.Registry;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ClientRecipeBook extends RecipeBook {
   private static final Logger LOGGER = LogUtils.getLogger();
   private Map<RecipeBookCategories, List<RecipeCollection>> collectionsByTab = ImmutableMap.of();
   private List<RecipeCollection> allCollections = ImmutableList.of();

   public void setupCollections(Iterable<Recipe<?>> p_90626_) {
      Map<RecipeBookCategories, List<List<Recipe<?>>>> map = categorizeAndGroupRecipes(p_90626_);
      Map<RecipeBookCategories, List<RecipeCollection>> map1 = Maps.newHashMap();
      Builder<RecipeCollection> builder = ImmutableList.builder();
      map.forEach((p_90630_, p_90631_) -> {
         map1.put(p_90630_, p_90631_.stream().map(RecipeCollection::new).peek(builder::add).collect(ImmutableList.toImmutableList()));
      });
      RecipeBookCategories.AGGREGATE_CATEGORIES.forEach((p_90637_, p_90638_) -> {
         map1.put(p_90637_, p_90638_.stream().flatMap((p_167706_) -> {
            return map1.getOrDefault(p_167706_, ImmutableList.of()).stream();
         }).collect(ImmutableList.toImmutableList()));
      });
      this.collectionsByTab = ImmutableMap.copyOf(map1);
      this.allCollections = builder.build();
   }

   private static Map<RecipeBookCategories, List<List<Recipe<?>>>> categorizeAndGroupRecipes(Iterable<Recipe<?>> p_90643_) {
      Map<RecipeBookCategories, List<List<Recipe<?>>>> map = Maps.newHashMap();
      Table<RecipeBookCategories, String, List<Recipe<?>>> table = HashBasedTable.create();

      for(Recipe<?> recipe : p_90643_) {
         if (!recipe.isSpecial() && !recipe.isIncomplete()) {
            RecipeBookCategories recipebookcategories = getCategory(recipe);
            String s = recipe.getGroup().isEmpty() ? recipe.getId().toString() : recipe.getGroup(); // FORGE: Group value defaults to the recipe's ID if the recipe's explicit group is empty.
            if (s.isEmpty()) {
               map.computeIfAbsent(recipebookcategories, (p_90645_) -> {
                  return Lists.newArrayList();
               }).add(ImmutableList.of(recipe));
            } else {
               List<Recipe<?>> list = table.get(recipebookcategories, s);
               if (list == null) {
                  list = Lists.newArrayList();
                  table.put(recipebookcategories, s, list);
                  map.computeIfAbsent(recipebookcategories, (p_90641_) -> {
                     return Lists.newArrayList();
                  }).add(list);
               }

               list.add(recipe);
            }
         }
      }

      return map;
   }

   private static RecipeBookCategories getCategory(Recipe<?> p_90647_) {
      RecipeType<?> recipetype = p_90647_.getType();
      if (recipetype == RecipeType.CRAFTING) {
         ItemStack itemstack = p_90647_.getResultItem();
         CreativeModeTab creativemodetab = itemstack.getItem().getItemCategory();
         if (creativemodetab == CreativeModeTab.TAB_BUILDING_BLOCKS) {
            return RecipeBookCategories.CRAFTING_BUILDING_BLOCKS;
         } else if (creativemodetab != CreativeModeTab.TAB_TOOLS && creativemodetab != CreativeModeTab.TAB_COMBAT) {
            return creativemodetab == CreativeModeTab.TAB_REDSTONE ? RecipeBookCategories.CRAFTING_REDSTONE : RecipeBookCategories.CRAFTING_MISC;
         } else {
            return RecipeBookCategories.CRAFTING_EQUIPMENT;
         }
      } else if (recipetype == RecipeType.SMELTING) {
         if (p_90647_.getResultItem().getItem().isEdible()) {
            return RecipeBookCategories.FURNACE_FOOD;
         } else {
            return p_90647_.getResultItem().getItem() instanceof BlockItem ? RecipeBookCategories.FURNACE_BLOCKS : RecipeBookCategories.FURNACE_MISC;
         }
      } else if (recipetype == RecipeType.BLASTING) {
         return p_90647_.getResultItem().getItem() instanceof BlockItem ? RecipeBookCategories.BLAST_FURNACE_BLOCKS : RecipeBookCategories.BLAST_FURNACE_MISC;
      } else if (recipetype == RecipeType.SMOKING) {
         return RecipeBookCategories.SMOKER_FOOD;
      } else if (recipetype == RecipeType.STONECUTTING) {
         return RecipeBookCategories.STONECUTTER;
      } else if (recipetype == RecipeType.CAMPFIRE_COOKING) {
         return RecipeBookCategories.CAMPFIRE;
      } else if (recipetype == RecipeType.SMITHING) {
         return RecipeBookCategories.SMITHING;
      } else {
         RecipeBookCategories categories = net.minecraftforge.client.RecipeBookRegistry.findCategories(recipetype, p_90647_);
         if (categories != null) return categories;
         LOGGER.warn("Unknown recipe category: {}/{}", LogUtils.defer(() -> {
            return Registry.RECIPE_TYPE.getKey(p_90647_.getType());
         }), LogUtils.defer(p_90647_::getId));
         return RecipeBookCategories.UNKNOWN;
      }
   }

   public List<RecipeCollection> getCollections() {
      return this.allCollections;
   }

   public List<RecipeCollection> getCollection(RecipeBookCategories p_90624_) {
      return this.collectionsByTab.getOrDefault(p_90624_, Collections.emptyList());
   }
}
