package net.minecraft.data.recipes;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.critereon.EnterBlockTrigger;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.Registry;
import net.minecraft.data.BlockFamilies;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCookingSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.slf4j.Logger;

public class RecipeProvider implements DataProvider {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
   protected static final ImmutableList<ItemLike> COAL_SMELTABLES = ImmutableList.of(Items.COAL_ORE, Items.DEEPSLATE_COAL_ORE);
   protected static final ImmutableList<ItemLike> IRON_SMELTABLES = ImmutableList.of(Items.IRON_ORE, Items.DEEPSLATE_IRON_ORE, Items.RAW_IRON);
   protected static final ImmutableList<ItemLike> COPPER_SMELTABLES = ImmutableList.of(Items.COPPER_ORE, Items.DEEPSLATE_COPPER_ORE, Items.RAW_COPPER);
   protected static final ImmutableList<ItemLike> GOLD_SMELTABLES = ImmutableList.of(Items.GOLD_ORE, Items.DEEPSLATE_GOLD_ORE, Items.NETHER_GOLD_ORE, Items.RAW_GOLD);
   protected static final ImmutableList<ItemLike> DIAMOND_SMELTABLES = ImmutableList.of(Items.DIAMOND_ORE, Items.DEEPSLATE_DIAMOND_ORE);
   protected static final ImmutableList<ItemLike> LAPIS_SMELTABLES = ImmutableList.of(Items.LAPIS_ORE, Items.DEEPSLATE_LAPIS_ORE);
   protected static final ImmutableList<ItemLike> REDSTONE_SMELTABLES = ImmutableList.of(Items.REDSTONE_ORE, Items.DEEPSLATE_REDSTONE_ORE);
   protected static final ImmutableList<ItemLike> EMERALD_SMELTABLES = ImmutableList.of(Items.EMERALD_ORE, Items.DEEPSLATE_EMERALD_ORE);
   protected final DataGenerator generator;
   protected static final Map<BlockFamily.Variant, BiFunction<ItemLike, ItemLike, RecipeBuilder>> shapeBuilders = ImmutableMap.<BlockFamily.Variant, BiFunction<ItemLike, ItemLike, RecipeBuilder>>builder().put(BlockFamily.Variant.BUTTON, (p_176733_, p_176734_) -> {
      return buttonBuilder(p_176733_, Ingredient.of(p_176734_));
   }).put(BlockFamily.Variant.CHISELED, (p_176730_, p_176731_) -> {
      return chiseledBuilder(p_176730_, Ingredient.of(p_176731_));
   }).put(BlockFamily.Variant.CUT, (p_176724_, p_176725_) -> {
      return cutBuilder(p_176724_, Ingredient.of(p_176725_));
   }).put(BlockFamily.Variant.DOOR, (p_176714_, p_176715_) -> {
      return doorBuilder(p_176714_, Ingredient.of(p_176715_));
   }).put(BlockFamily.Variant.FENCE, (p_176708_, p_176709_) -> {
      return fenceBuilder(p_176708_, Ingredient.of(p_176709_));
   }).put(BlockFamily.Variant.FENCE_GATE, (p_176698_, p_176699_) -> {
      return fenceGateBuilder(p_176698_, Ingredient.of(p_176699_));
   }).put(BlockFamily.Variant.SIGN, (p_176688_, p_176689_) -> {
      return signBuilder(p_176688_, Ingredient.of(p_176689_));
   }).put(BlockFamily.Variant.SLAB, (p_176682_, p_176683_) -> {
      return slabBuilder(p_176682_, Ingredient.of(p_176683_));
   }).put(BlockFamily.Variant.STAIRS, (p_176674_, p_176675_) -> {
      return stairBuilder(p_176674_, Ingredient.of(p_176675_));
   }).put(BlockFamily.Variant.PRESSURE_PLATE, (p_176662_, p_176663_) -> {
      return pressurePlateBuilder(p_176662_, Ingredient.of(p_176663_));
   }).put(BlockFamily.Variant.POLISHED, (p_176650_, p_176651_) -> {
      return polishedBuilder(p_176650_, Ingredient.of(p_176651_));
   }).put(BlockFamily.Variant.TRAPDOOR, (p_176638_, p_176639_) -> {
      return trapdoorBuilder(p_176638_, Ingredient.of(p_176639_));
   }).put(BlockFamily.Variant.WALL, (p_176608_, p_176609_) -> {
      return wallBuilder(p_176608_, Ingredient.of(p_176609_));
   }).build();

   public RecipeProvider(DataGenerator p_125973_) {
      this.generator = p_125973_;
   }

   public void run(HashCache p_125982_) {
      Path path = this.generator.getOutputFolder();
      Set<ResourceLocation> set = Sets.newHashSet();
      buildCraftingRecipes((p_125991_) -> {
         if (!set.add(p_125991_.getId())) {
            throw new IllegalStateException("Duplicate recipe " + p_125991_.getId());
         } else {
            saveRecipe(p_125982_, p_125991_.serializeRecipe(), path.resolve("data/" + p_125991_.getId().getNamespace() + "/recipes/" + p_125991_.getId().getPath() + ".json"));
            JsonObject jsonobject = p_125991_.serializeAdvancement();
            if (jsonobject != null) {
               saveAdvancement(p_125982_, jsonobject, path.resolve("data/" + p_125991_.getId().getNamespace() + "/advancements/" + p_125991_.getAdvancementId().getPath() + ".json"));
            }

         }
      });
      if (this.getClass() == RecipeProvider.class) //Forge: Subclasses don't need this.
      saveAdvancement(p_125982_, Advancement.Builder.advancement().addCriterion("impossible", new ImpossibleTrigger.TriggerInstance()).serializeToJson(), path.resolve("data/minecraft/advancements/recipes/root.json"));
   }

   private static void saveRecipe(HashCache p_125984_, JsonObject p_125985_, Path p_125986_) {
      try {
         String s = GSON.toJson((JsonElement)p_125985_);
         String s1 = SHA1.hashUnencodedChars(s).toString();
         if (!Objects.equals(p_125984_.getHash(p_125986_), s1) || !Files.exists(p_125986_)) {
            Files.createDirectories(p_125986_.getParent());
            BufferedWriter bufferedwriter = Files.newBufferedWriter(p_125986_);

            try {
               bufferedwriter.write(s);
            } catch (Throwable throwable1) {
               if (bufferedwriter != null) {
                  try {
                     bufferedwriter.close();
                  } catch (Throwable throwable) {
                     throwable1.addSuppressed(throwable);
                  }
               }

               throw throwable1;
            }

            if (bufferedwriter != null) {
               bufferedwriter.close();
            }
         }

         p_125984_.putNew(p_125986_, s1);
      } catch (IOException ioexception) {
         LOGGER.error("Couldn't save recipe {}", p_125986_, ioexception);
      }

   }

   protected void saveAdvancement(HashCache p_126014_, JsonObject p_126015_, Path p_126016_) {
      try {
         String s = GSON.toJson((JsonElement)p_126015_);
         String s1 = SHA1.hashUnencodedChars(s).toString();
         if (!Objects.equals(p_126014_.getHash(p_126016_), s1) || !Files.exists(p_126016_)) {
            Files.createDirectories(p_126016_.getParent());
            BufferedWriter bufferedwriter = Files.newBufferedWriter(p_126016_);

            try {
               bufferedwriter.write(s);
            } catch (Throwable throwable1) {
               if (bufferedwriter != null) {
                  try {
                     bufferedwriter.close();
                  } catch (Throwable throwable) {
                     throwable1.addSuppressed(throwable);
                  }
               }

               throw throwable1;
            }

            if (bufferedwriter != null) {
               bufferedwriter.close();
            }
         }

         p_126014_.putNew(p_126016_, s1);
      } catch (IOException ioexception) {
         LOGGER.error("Couldn't save recipe advancement {}", p_126016_, ioexception);
      }

   }

   protected void buildCraftingRecipes(Consumer<FinishedRecipe> p_176532_) {
      BlockFamilies.getAllFamilies().filter(BlockFamily::shouldGenerateRecipe).forEach((p_176624_) -> {
         generateRecipes(p_176532_, p_176624_);
      });
      planksFromLog(p_176532_, Blocks.ACACIA_PLANKS, ItemTags.ACACIA_LOGS);
      planksFromLogs(p_176532_, Blocks.BIRCH_PLANKS, ItemTags.BIRCH_LOGS);
      planksFromLogs(p_176532_, Blocks.CRIMSON_PLANKS, ItemTags.CRIMSON_STEMS);
      planksFromLog(p_176532_, Blocks.DARK_OAK_PLANKS, ItemTags.DARK_OAK_LOGS);
      planksFromLogs(p_176532_, Blocks.JUNGLE_PLANKS, ItemTags.JUNGLE_LOGS);
      planksFromLogs(p_176532_, Blocks.OAK_PLANKS, ItemTags.OAK_LOGS);
      planksFromLogs(p_176532_, Blocks.SPRUCE_PLANKS, ItemTags.SPRUCE_LOGS);
      planksFromLogs(p_176532_, Blocks.WARPED_PLANKS, ItemTags.WARPED_STEMS);
      woodFromLogs(p_176532_, Blocks.ACACIA_WOOD, Blocks.ACACIA_LOG);
      woodFromLogs(p_176532_, Blocks.BIRCH_WOOD, Blocks.BIRCH_LOG);
      woodFromLogs(p_176532_, Blocks.DARK_OAK_WOOD, Blocks.DARK_OAK_LOG);
      woodFromLogs(p_176532_, Blocks.JUNGLE_WOOD, Blocks.JUNGLE_LOG);
      woodFromLogs(p_176532_, Blocks.OAK_WOOD, Blocks.OAK_LOG);
      woodFromLogs(p_176532_, Blocks.SPRUCE_WOOD, Blocks.SPRUCE_LOG);
      woodFromLogs(p_176532_, Blocks.CRIMSON_HYPHAE, Blocks.CRIMSON_STEM);
      woodFromLogs(p_176532_, Blocks.WARPED_HYPHAE, Blocks.WARPED_STEM);
      woodFromLogs(p_176532_, Blocks.STRIPPED_ACACIA_WOOD, Blocks.STRIPPED_ACACIA_LOG);
      woodFromLogs(p_176532_, Blocks.STRIPPED_BIRCH_WOOD, Blocks.STRIPPED_BIRCH_LOG);
      woodFromLogs(p_176532_, Blocks.STRIPPED_DARK_OAK_WOOD, Blocks.STRIPPED_DARK_OAK_LOG);
      woodFromLogs(p_176532_, Blocks.STRIPPED_JUNGLE_WOOD, Blocks.STRIPPED_JUNGLE_LOG);
      woodFromLogs(p_176532_, Blocks.STRIPPED_OAK_WOOD, Blocks.STRIPPED_OAK_LOG);
      woodFromLogs(p_176532_, Blocks.STRIPPED_SPRUCE_WOOD, Blocks.STRIPPED_SPRUCE_LOG);
      woodFromLogs(p_176532_, Blocks.STRIPPED_CRIMSON_HYPHAE, Blocks.STRIPPED_CRIMSON_STEM);
      woodFromLogs(p_176532_, Blocks.STRIPPED_WARPED_HYPHAE, Blocks.STRIPPED_WARPED_STEM);
      woodenBoat(p_176532_, Items.ACACIA_BOAT, Blocks.ACACIA_PLANKS);
      woodenBoat(p_176532_, Items.BIRCH_BOAT, Blocks.BIRCH_PLANKS);
      woodenBoat(p_176532_, Items.DARK_OAK_BOAT, Blocks.DARK_OAK_PLANKS);
      woodenBoat(p_176532_, Items.JUNGLE_BOAT, Blocks.JUNGLE_PLANKS);
      woodenBoat(p_176532_, Items.OAK_BOAT, Blocks.OAK_PLANKS);
      woodenBoat(p_176532_, Items.SPRUCE_BOAT, Blocks.SPRUCE_PLANKS);
      coloredWoolFromWhiteWoolAndDye(p_176532_, Blocks.BLACK_WOOL, Items.BLACK_DYE);
      carpet(p_176532_, Blocks.BLACK_CARPET, Blocks.BLACK_WOOL);
      coloredCarpetFromWhiteCarpetAndDye(p_176532_, Blocks.BLACK_CARPET, Items.BLACK_DYE);
      bedFromPlanksAndWool(p_176532_, Items.BLACK_BED, Blocks.BLACK_WOOL);
      bedFromWhiteBedAndDye(p_176532_, Items.BLACK_BED, Items.BLACK_DYE);
      banner(p_176532_, Items.BLACK_BANNER, Blocks.BLACK_WOOL);
      coloredWoolFromWhiteWoolAndDye(p_176532_, Blocks.BLUE_WOOL, Items.BLUE_DYE);
      carpet(p_176532_, Blocks.BLUE_CARPET, Blocks.BLUE_WOOL);
      coloredCarpetFromWhiteCarpetAndDye(p_176532_, Blocks.BLUE_CARPET, Items.BLUE_DYE);
      bedFromPlanksAndWool(p_176532_, Items.BLUE_BED, Blocks.BLUE_WOOL);
      bedFromWhiteBedAndDye(p_176532_, Items.BLUE_BED, Items.BLUE_DYE);
      banner(p_176532_, Items.BLUE_BANNER, Blocks.BLUE_WOOL);
      coloredWoolFromWhiteWoolAndDye(p_176532_, Blocks.BROWN_WOOL, Items.BROWN_DYE);
      carpet(p_176532_, Blocks.BROWN_CARPET, Blocks.BROWN_WOOL);
      coloredCarpetFromWhiteCarpetAndDye(p_176532_, Blocks.BROWN_CARPET, Items.BROWN_DYE);
      bedFromPlanksAndWool(p_176532_, Items.BROWN_BED, Blocks.BROWN_WOOL);
      bedFromWhiteBedAndDye(p_176532_, Items.BROWN_BED, Items.BROWN_DYE);
      banner(p_176532_, Items.BROWN_BANNER, Blocks.BROWN_WOOL);
      coloredWoolFromWhiteWoolAndDye(p_176532_, Blocks.CYAN_WOOL, Items.CYAN_DYE);
      carpet(p_176532_, Blocks.CYAN_CARPET, Blocks.CYAN_WOOL);
      coloredCarpetFromWhiteCarpetAndDye(p_176532_, Blocks.CYAN_CARPET, Items.CYAN_DYE);
      bedFromPlanksAndWool(p_176532_, Items.CYAN_BED, Blocks.CYAN_WOOL);
      bedFromWhiteBedAndDye(p_176532_, Items.CYAN_BED, Items.CYAN_DYE);
      banner(p_176532_, Items.CYAN_BANNER, Blocks.CYAN_WOOL);
      coloredWoolFromWhiteWoolAndDye(p_176532_, Blocks.GRAY_WOOL, Items.GRAY_DYE);
      carpet(p_176532_, Blocks.GRAY_CARPET, Blocks.GRAY_WOOL);
      coloredCarpetFromWhiteCarpetAndDye(p_176532_, Blocks.GRAY_CARPET, Items.GRAY_DYE);
      bedFromPlanksAndWool(p_176532_, Items.GRAY_BED, Blocks.GRAY_WOOL);
      bedFromWhiteBedAndDye(p_176532_, Items.GRAY_BED, Items.GRAY_DYE);
      banner(p_176532_, Items.GRAY_BANNER, Blocks.GRAY_WOOL);
      coloredWoolFromWhiteWoolAndDye(p_176532_, Blocks.GREEN_WOOL, Items.GREEN_DYE);
      carpet(p_176532_, Blocks.GREEN_CARPET, Blocks.GREEN_WOOL);
      coloredCarpetFromWhiteCarpetAndDye(p_176532_, Blocks.GREEN_CARPET, Items.GREEN_DYE);
      bedFromPlanksAndWool(p_176532_, Items.GREEN_BED, Blocks.GREEN_WOOL);
      bedFromWhiteBedAndDye(p_176532_, Items.GREEN_BED, Items.GREEN_DYE);
      banner(p_176532_, Items.GREEN_BANNER, Blocks.GREEN_WOOL);
      coloredWoolFromWhiteWoolAndDye(p_176532_, Blocks.LIGHT_BLUE_WOOL, Items.LIGHT_BLUE_DYE);
      carpet(p_176532_, Blocks.LIGHT_BLUE_CARPET, Blocks.LIGHT_BLUE_WOOL);
      coloredCarpetFromWhiteCarpetAndDye(p_176532_, Blocks.LIGHT_BLUE_CARPET, Items.LIGHT_BLUE_DYE);
      bedFromPlanksAndWool(p_176532_, Items.LIGHT_BLUE_BED, Blocks.LIGHT_BLUE_WOOL);
      bedFromWhiteBedAndDye(p_176532_, Items.LIGHT_BLUE_BED, Items.LIGHT_BLUE_DYE);
      banner(p_176532_, Items.LIGHT_BLUE_BANNER, Blocks.LIGHT_BLUE_WOOL);
      coloredWoolFromWhiteWoolAndDye(p_176532_, Blocks.LIGHT_GRAY_WOOL, Items.LIGHT_GRAY_DYE);
      carpet(p_176532_, Blocks.LIGHT_GRAY_CARPET, Blocks.LIGHT_GRAY_WOOL);
      coloredCarpetFromWhiteCarpetAndDye(p_176532_, Blocks.LIGHT_GRAY_CARPET, Items.LIGHT_GRAY_DYE);
      bedFromPlanksAndWool(p_176532_, Items.LIGHT_GRAY_BED, Blocks.LIGHT_GRAY_WOOL);
      bedFromWhiteBedAndDye(p_176532_, Items.LIGHT_GRAY_BED, Items.LIGHT_GRAY_DYE);
      banner(p_176532_, Items.LIGHT_GRAY_BANNER, Blocks.LIGHT_GRAY_WOOL);
      coloredWoolFromWhiteWoolAndDye(p_176532_, Blocks.LIME_WOOL, Items.LIME_DYE);
      carpet(p_176532_, Blocks.LIME_CARPET, Blocks.LIME_WOOL);
      coloredCarpetFromWhiteCarpetAndDye(p_176532_, Blocks.LIME_CARPET, Items.LIME_DYE);
      bedFromPlanksAndWool(p_176532_, Items.LIME_BED, Blocks.LIME_WOOL);
      bedFromWhiteBedAndDye(p_176532_, Items.LIME_BED, Items.LIME_DYE);
      banner(p_176532_, Items.LIME_BANNER, Blocks.LIME_WOOL);
      coloredWoolFromWhiteWoolAndDye(p_176532_, Blocks.MAGENTA_WOOL, Items.MAGENTA_DYE);
      carpet(p_176532_, Blocks.MAGENTA_CARPET, Blocks.MAGENTA_WOOL);
      coloredCarpetFromWhiteCarpetAndDye(p_176532_, Blocks.MAGENTA_CARPET, Items.MAGENTA_DYE);
      bedFromPlanksAndWool(p_176532_, Items.MAGENTA_BED, Blocks.MAGENTA_WOOL);
      bedFromWhiteBedAndDye(p_176532_, Items.MAGENTA_BED, Items.MAGENTA_DYE);
      banner(p_176532_, Items.MAGENTA_BANNER, Blocks.MAGENTA_WOOL);
      coloredWoolFromWhiteWoolAndDye(p_176532_, Blocks.ORANGE_WOOL, Items.ORANGE_DYE);
      carpet(p_176532_, Blocks.ORANGE_CARPET, Blocks.ORANGE_WOOL);
      coloredCarpetFromWhiteCarpetAndDye(p_176532_, Blocks.ORANGE_CARPET, Items.ORANGE_DYE);
      bedFromPlanksAndWool(p_176532_, Items.ORANGE_BED, Blocks.ORANGE_WOOL);
      bedFromWhiteBedAndDye(p_176532_, Items.ORANGE_BED, Items.ORANGE_DYE);
      banner(p_176532_, Items.ORANGE_BANNER, Blocks.ORANGE_WOOL);
      coloredWoolFromWhiteWoolAndDye(p_176532_, Blocks.PINK_WOOL, Items.PINK_DYE);
      carpet(p_176532_, Blocks.PINK_CARPET, Blocks.PINK_WOOL);
      coloredCarpetFromWhiteCarpetAndDye(p_176532_, Blocks.PINK_CARPET, Items.PINK_DYE);
      bedFromPlanksAndWool(p_176532_, Items.PINK_BED, Blocks.PINK_WOOL);
      bedFromWhiteBedAndDye(p_176532_, Items.PINK_BED, Items.PINK_DYE);
      banner(p_176532_, Items.PINK_BANNER, Blocks.PINK_WOOL);
      coloredWoolFromWhiteWoolAndDye(p_176532_, Blocks.PURPLE_WOOL, Items.PURPLE_DYE);
      carpet(p_176532_, Blocks.PURPLE_CARPET, Blocks.PURPLE_WOOL);
      coloredCarpetFromWhiteCarpetAndDye(p_176532_, Blocks.PURPLE_CARPET, Items.PURPLE_DYE);
      bedFromPlanksAndWool(p_176532_, Items.PURPLE_BED, Blocks.PURPLE_WOOL);
      bedFromWhiteBedAndDye(p_176532_, Items.PURPLE_BED, Items.PURPLE_DYE);
      banner(p_176532_, Items.PURPLE_BANNER, Blocks.PURPLE_WOOL);
      coloredWoolFromWhiteWoolAndDye(p_176532_, Blocks.RED_WOOL, Items.RED_DYE);
      carpet(p_176532_, Blocks.RED_CARPET, Blocks.RED_WOOL);
      coloredCarpetFromWhiteCarpetAndDye(p_176532_, Blocks.RED_CARPET, Items.RED_DYE);
      bedFromPlanksAndWool(p_176532_, Items.RED_BED, Blocks.RED_WOOL);
      bedFromWhiteBedAndDye(p_176532_, Items.RED_BED, Items.RED_DYE);
      banner(p_176532_, Items.RED_BANNER, Blocks.RED_WOOL);
      carpet(p_176532_, Blocks.WHITE_CARPET, Blocks.WHITE_WOOL);
      bedFromPlanksAndWool(p_176532_, Items.WHITE_BED, Blocks.WHITE_WOOL);
      banner(p_176532_, Items.WHITE_BANNER, Blocks.WHITE_WOOL);
      coloredWoolFromWhiteWoolAndDye(p_176532_, Blocks.YELLOW_WOOL, Items.YELLOW_DYE);
      carpet(p_176532_, Blocks.YELLOW_CARPET, Blocks.YELLOW_WOOL);
      coloredCarpetFromWhiteCarpetAndDye(p_176532_, Blocks.YELLOW_CARPET, Items.YELLOW_DYE);
      bedFromPlanksAndWool(p_176532_, Items.YELLOW_BED, Blocks.YELLOW_WOOL);
      bedFromWhiteBedAndDye(p_176532_, Items.YELLOW_BED, Items.YELLOW_DYE);
      banner(p_176532_, Items.YELLOW_BANNER, Blocks.YELLOW_WOOL);
      carpet(p_176532_, Blocks.MOSS_CARPET, Blocks.MOSS_BLOCK);
      stainedGlassFromGlassAndDye(p_176532_, Blocks.BLACK_STAINED_GLASS, Items.BLACK_DYE);
      stainedGlassPaneFromStainedGlass(p_176532_, Blocks.BLACK_STAINED_GLASS_PANE, Blocks.BLACK_STAINED_GLASS);
      stainedGlassPaneFromGlassPaneAndDye(p_176532_, Blocks.BLACK_STAINED_GLASS_PANE, Items.BLACK_DYE);
      stainedGlassFromGlassAndDye(p_176532_, Blocks.BLUE_STAINED_GLASS, Items.BLUE_DYE);
      stainedGlassPaneFromStainedGlass(p_176532_, Blocks.BLUE_STAINED_GLASS_PANE, Blocks.BLUE_STAINED_GLASS);
      stainedGlassPaneFromGlassPaneAndDye(p_176532_, Blocks.BLUE_STAINED_GLASS_PANE, Items.BLUE_DYE);
      stainedGlassFromGlassAndDye(p_176532_, Blocks.BROWN_STAINED_GLASS, Items.BROWN_DYE);
      stainedGlassPaneFromStainedGlass(p_176532_, Blocks.BROWN_STAINED_GLASS_PANE, Blocks.BROWN_STAINED_GLASS);
      stainedGlassPaneFromGlassPaneAndDye(p_176532_, Blocks.BROWN_STAINED_GLASS_PANE, Items.BROWN_DYE);
      stainedGlassFromGlassAndDye(p_176532_, Blocks.CYAN_STAINED_GLASS, Items.CYAN_DYE);
      stainedGlassPaneFromStainedGlass(p_176532_, Blocks.CYAN_STAINED_GLASS_PANE, Blocks.CYAN_STAINED_GLASS);
      stainedGlassPaneFromGlassPaneAndDye(p_176532_, Blocks.CYAN_STAINED_GLASS_PANE, Items.CYAN_DYE);
      stainedGlassFromGlassAndDye(p_176532_, Blocks.GRAY_STAINED_GLASS, Items.GRAY_DYE);
      stainedGlassPaneFromStainedGlass(p_176532_, Blocks.GRAY_STAINED_GLASS_PANE, Blocks.GRAY_STAINED_GLASS);
      stainedGlassPaneFromGlassPaneAndDye(p_176532_, Blocks.GRAY_STAINED_GLASS_PANE, Items.GRAY_DYE);
      stainedGlassFromGlassAndDye(p_176532_, Blocks.GREEN_STAINED_GLASS, Items.GREEN_DYE);
      stainedGlassPaneFromStainedGlass(p_176532_, Blocks.GREEN_STAINED_GLASS_PANE, Blocks.GREEN_STAINED_GLASS);
      stainedGlassPaneFromGlassPaneAndDye(p_176532_, Blocks.GREEN_STAINED_GLASS_PANE, Items.GREEN_DYE);
      stainedGlassFromGlassAndDye(p_176532_, Blocks.LIGHT_BLUE_STAINED_GLASS, Items.LIGHT_BLUE_DYE);
      stainedGlassPaneFromStainedGlass(p_176532_, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, Blocks.LIGHT_BLUE_STAINED_GLASS);
      stainedGlassPaneFromGlassPaneAndDye(p_176532_, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, Items.LIGHT_BLUE_DYE);
      stainedGlassFromGlassAndDye(p_176532_, Blocks.LIGHT_GRAY_STAINED_GLASS, Items.LIGHT_GRAY_DYE);
      stainedGlassPaneFromStainedGlass(p_176532_, Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, Blocks.LIGHT_GRAY_STAINED_GLASS);
      stainedGlassPaneFromGlassPaneAndDye(p_176532_, Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, Items.LIGHT_GRAY_DYE);
      stainedGlassFromGlassAndDye(p_176532_, Blocks.LIME_STAINED_GLASS, Items.LIME_DYE);
      stainedGlassPaneFromStainedGlass(p_176532_, Blocks.LIME_STAINED_GLASS_PANE, Blocks.LIME_STAINED_GLASS);
      stainedGlassPaneFromGlassPaneAndDye(p_176532_, Blocks.LIME_STAINED_GLASS_PANE, Items.LIME_DYE);
      stainedGlassFromGlassAndDye(p_176532_, Blocks.MAGENTA_STAINED_GLASS, Items.MAGENTA_DYE);
      stainedGlassPaneFromStainedGlass(p_176532_, Blocks.MAGENTA_STAINED_GLASS_PANE, Blocks.MAGENTA_STAINED_GLASS);
      stainedGlassPaneFromGlassPaneAndDye(p_176532_, Blocks.MAGENTA_STAINED_GLASS_PANE, Items.MAGENTA_DYE);
      stainedGlassFromGlassAndDye(p_176532_, Blocks.ORANGE_STAINED_GLASS, Items.ORANGE_DYE);
      stainedGlassPaneFromStainedGlass(p_176532_, Blocks.ORANGE_STAINED_GLASS_PANE, Blocks.ORANGE_STAINED_GLASS);
      stainedGlassPaneFromGlassPaneAndDye(p_176532_, Blocks.ORANGE_STAINED_GLASS_PANE, Items.ORANGE_DYE);
      stainedGlassFromGlassAndDye(p_176532_, Blocks.PINK_STAINED_GLASS, Items.PINK_DYE);
      stainedGlassPaneFromStainedGlass(p_176532_, Blocks.PINK_STAINED_GLASS_PANE, Blocks.PINK_STAINED_GLASS);
      stainedGlassPaneFromGlassPaneAndDye(p_176532_, Blocks.PINK_STAINED_GLASS_PANE, Items.PINK_DYE);
      stainedGlassFromGlassAndDye(p_176532_, Blocks.PURPLE_STAINED_GLASS, Items.PURPLE_DYE);
      stainedGlassPaneFromStainedGlass(p_176532_, Blocks.PURPLE_STAINED_GLASS_PANE, Blocks.PURPLE_STAINED_GLASS);
      stainedGlassPaneFromGlassPaneAndDye(p_176532_, Blocks.PURPLE_STAINED_GLASS_PANE, Items.PURPLE_DYE);
      stainedGlassFromGlassAndDye(p_176532_, Blocks.RED_STAINED_GLASS, Items.RED_DYE);
      stainedGlassPaneFromStainedGlass(p_176532_, Blocks.RED_STAINED_GLASS_PANE, Blocks.RED_STAINED_GLASS);
      stainedGlassPaneFromGlassPaneAndDye(p_176532_, Blocks.RED_STAINED_GLASS_PANE, Items.RED_DYE);
      stainedGlassFromGlassAndDye(p_176532_, Blocks.WHITE_STAINED_GLASS, Items.WHITE_DYE);
      stainedGlassPaneFromStainedGlass(p_176532_, Blocks.WHITE_STAINED_GLASS_PANE, Blocks.WHITE_STAINED_GLASS);
      stainedGlassPaneFromGlassPaneAndDye(p_176532_, Blocks.WHITE_STAINED_GLASS_PANE, Items.WHITE_DYE);
      stainedGlassFromGlassAndDye(p_176532_, Blocks.YELLOW_STAINED_GLASS, Items.YELLOW_DYE);
      stainedGlassPaneFromStainedGlass(p_176532_, Blocks.YELLOW_STAINED_GLASS_PANE, Blocks.YELLOW_STAINED_GLASS);
      stainedGlassPaneFromGlassPaneAndDye(p_176532_, Blocks.YELLOW_STAINED_GLASS_PANE, Items.YELLOW_DYE);
      coloredTerracottaFromTerracottaAndDye(p_176532_, Blocks.BLACK_TERRACOTTA, Items.BLACK_DYE);
      coloredTerracottaFromTerracottaAndDye(p_176532_, Blocks.BLUE_TERRACOTTA, Items.BLUE_DYE);
      coloredTerracottaFromTerracottaAndDye(p_176532_, Blocks.BROWN_TERRACOTTA, Items.BROWN_DYE);
      coloredTerracottaFromTerracottaAndDye(p_176532_, Blocks.CYAN_TERRACOTTA, Items.CYAN_DYE);
      coloredTerracottaFromTerracottaAndDye(p_176532_, Blocks.GRAY_TERRACOTTA, Items.GRAY_DYE);
      coloredTerracottaFromTerracottaAndDye(p_176532_, Blocks.GREEN_TERRACOTTA, Items.GREEN_DYE);
      coloredTerracottaFromTerracottaAndDye(p_176532_, Blocks.LIGHT_BLUE_TERRACOTTA, Items.LIGHT_BLUE_DYE);
      coloredTerracottaFromTerracottaAndDye(p_176532_, Blocks.LIGHT_GRAY_TERRACOTTA, Items.LIGHT_GRAY_DYE);
      coloredTerracottaFromTerracottaAndDye(p_176532_, Blocks.LIME_TERRACOTTA, Items.LIME_DYE);
      coloredTerracottaFromTerracottaAndDye(p_176532_, Blocks.MAGENTA_TERRACOTTA, Items.MAGENTA_DYE);
      coloredTerracottaFromTerracottaAndDye(p_176532_, Blocks.ORANGE_TERRACOTTA, Items.ORANGE_DYE);
      coloredTerracottaFromTerracottaAndDye(p_176532_, Blocks.PINK_TERRACOTTA, Items.PINK_DYE);
      coloredTerracottaFromTerracottaAndDye(p_176532_, Blocks.PURPLE_TERRACOTTA, Items.PURPLE_DYE);
      coloredTerracottaFromTerracottaAndDye(p_176532_, Blocks.RED_TERRACOTTA, Items.RED_DYE);
      coloredTerracottaFromTerracottaAndDye(p_176532_, Blocks.WHITE_TERRACOTTA, Items.WHITE_DYE);
      coloredTerracottaFromTerracottaAndDye(p_176532_, Blocks.YELLOW_TERRACOTTA, Items.YELLOW_DYE);
      concretePowder(p_176532_, Blocks.BLACK_CONCRETE_POWDER, Items.BLACK_DYE);
      concretePowder(p_176532_, Blocks.BLUE_CONCRETE_POWDER, Items.BLUE_DYE);
      concretePowder(p_176532_, Blocks.BROWN_CONCRETE_POWDER, Items.BROWN_DYE);
      concretePowder(p_176532_, Blocks.CYAN_CONCRETE_POWDER, Items.CYAN_DYE);
      concretePowder(p_176532_, Blocks.GRAY_CONCRETE_POWDER, Items.GRAY_DYE);
      concretePowder(p_176532_, Blocks.GREEN_CONCRETE_POWDER, Items.GREEN_DYE);
      concretePowder(p_176532_, Blocks.LIGHT_BLUE_CONCRETE_POWDER, Items.LIGHT_BLUE_DYE);
      concretePowder(p_176532_, Blocks.LIGHT_GRAY_CONCRETE_POWDER, Items.LIGHT_GRAY_DYE);
      concretePowder(p_176532_, Blocks.LIME_CONCRETE_POWDER, Items.LIME_DYE);
      concretePowder(p_176532_, Blocks.MAGENTA_CONCRETE_POWDER, Items.MAGENTA_DYE);
      concretePowder(p_176532_, Blocks.ORANGE_CONCRETE_POWDER, Items.ORANGE_DYE);
      concretePowder(p_176532_, Blocks.PINK_CONCRETE_POWDER, Items.PINK_DYE);
      concretePowder(p_176532_, Blocks.PURPLE_CONCRETE_POWDER, Items.PURPLE_DYE);
      concretePowder(p_176532_, Blocks.RED_CONCRETE_POWDER, Items.RED_DYE);
      concretePowder(p_176532_, Blocks.WHITE_CONCRETE_POWDER, Items.WHITE_DYE);
      concretePowder(p_176532_, Blocks.YELLOW_CONCRETE_POWDER, Items.YELLOW_DYE);
      ShapedRecipeBuilder.shaped(Items.CANDLE).define('S', Items.STRING).define('H', Items.HONEYCOMB).pattern("S").pattern("H").unlockedBy("has_string", has(Items.STRING)).unlockedBy("has_honeycomb", has(Items.HONEYCOMB)).save(p_176532_);
      candle(p_176532_, Blocks.BLACK_CANDLE, Items.BLACK_DYE);
      candle(p_176532_, Blocks.BLUE_CANDLE, Items.BLUE_DYE);
      candle(p_176532_, Blocks.BROWN_CANDLE, Items.BROWN_DYE);
      candle(p_176532_, Blocks.CYAN_CANDLE, Items.CYAN_DYE);
      candle(p_176532_, Blocks.GRAY_CANDLE, Items.GRAY_DYE);
      candle(p_176532_, Blocks.GREEN_CANDLE, Items.GREEN_DYE);
      candle(p_176532_, Blocks.LIGHT_BLUE_CANDLE, Items.LIGHT_BLUE_DYE);
      candle(p_176532_, Blocks.LIGHT_GRAY_CANDLE, Items.LIGHT_GRAY_DYE);
      candle(p_176532_, Blocks.LIME_CANDLE, Items.LIME_DYE);
      candle(p_176532_, Blocks.MAGENTA_CANDLE, Items.MAGENTA_DYE);
      candle(p_176532_, Blocks.ORANGE_CANDLE, Items.ORANGE_DYE);
      candle(p_176532_, Blocks.PINK_CANDLE, Items.PINK_DYE);
      candle(p_176532_, Blocks.PURPLE_CANDLE, Items.PURPLE_DYE);
      candle(p_176532_, Blocks.RED_CANDLE, Items.RED_DYE);
      candle(p_176532_, Blocks.WHITE_CANDLE, Items.WHITE_DYE);
      candle(p_176532_, Blocks.YELLOW_CANDLE, Items.YELLOW_DYE);
      ShapedRecipeBuilder.shaped(Blocks.ACTIVATOR_RAIL, 6).define('#', Blocks.REDSTONE_TORCH).define('S', Items.STICK).define('X', Items.IRON_INGOT).pattern("XSX").pattern("X#X").pattern("XSX").unlockedBy("has_rail", has(Blocks.RAIL)).save(p_176532_);
      ShapelessRecipeBuilder.shapeless(Blocks.ANDESITE, 2).requires(Blocks.DIORITE).requires(Blocks.COBBLESTONE).unlockedBy("has_stone", has(Blocks.DIORITE)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.ANVIL).define('I', Blocks.IRON_BLOCK).define('i', Items.IRON_INGOT).pattern("III").pattern(" i ").pattern("iii").unlockedBy("has_iron_block", has(Blocks.IRON_BLOCK)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.ARMOR_STAND).define('/', Items.STICK).define('_', Blocks.SMOOTH_STONE_SLAB).pattern("///").pattern(" / ").pattern("/_/").unlockedBy("has_stone_slab", has(Blocks.SMOOTH_STONE_SLAB)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.ARROW, 4).define('#', Items.STICK).define('X', Items.FLINT).define('Y', Items.FEATHER).pattern("X").pattern("#").pattern("Y").unlockedBy("has_feather", has(Items.FEATHER)).unlockedBy("has_flint", has(Items.FLINT)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.BARREL, 1).define('P', ItemTags.PLANKS).define('S', ItemTags.WOODEN_SLABS).pattern("PSP").pattern("P P").pattern("PSP").unlockedBy("has_planks", has(ItemTags.PLANKS)).unlockedBy("has_wood_slab", has(ItemTags.WOODEN_SLABS)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.BEACON).define('S', Items.NETHER_STAR).define('G', Blocks.GLASS).define('O', Blocks.OBSIDIAN).pattern("GGG").pattern("GSG").pattern("OOO").unlockedBy("has_nether_star", has(Items.NETHER_STAR)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.BEEHIVE).define('P', ItemTags.PLANKS).define('H', Items.HONEYCOMB).pattern("PPP").pattern("HHH").pattern("PPP").unlockedBy("has_honeycomb", has(Items.HONEYCOMB)).save(p_176532_);
      ShapelessRecipeBuilder.shapeless(Items.BEETROOT_SOUP).requires(Items.BOWL).requires(Items.BEETROOT, 6).unlockedBy("has_beetroot", has(Items.BEETROOT)).save(p_176532_);
      ShapelessRecipeBuilder.shapeless(Items.BLACK_DYE).requires(Items.INK_SAC).group("black_dye").unlockedBy("has_ink_sac", has(Items.INK_SAC)).save(p_176532_);
      oneToOneConversionRecipe(p_176532_, Items.BLACK_DYE, Blocks.WITHER_ROSE, "black_dye");
      ShapelessRecipeBuilder.shapeless(Items.BLAZE_POWDER, 2).requires(Items.BLAZE_ROD).unlockedBy("has_blaze_rod", has(Items.BLAZE_ROD)).save(p_176532_);
      ShapelessRecipeBuilder.shapeless(Items.BLUE_DYE).requires(Items.LAPIS_LAZULI).group("blue_dye").unlockedBy("has_lapis_lazuli", has(Items.LAPIS_LAZULI)).save(p_176532_);
      oneToOneConversionRecipe(p_176532_, Items.BLUE_DYE, Blocks.CORNFLOWER, "blue_dye");
      ShapedRecipeBuilder.shaped(Blocks.BLUE_ICE).define('#', Blocks.PACKED_ICE).pattern("###").pattern("###").pattern("###").unlockedBy("has_packed_ice", has(Blocks.PACKED_ICE)).save(p_176532_);
      ShapelessRecipeBuilder.shapeless(Items.BONE_MEAL, 3).requires(Items.BONE).group("bonemeal").unlockedBy("has_bone", has(Items.BONE)).save(p_176532_);
      nineBlockStorageRecipesRecipesWithCustomUnpacking(p_176532_, Items.BONE_MEAL, Items.BONE_BLOCK, "bone_meal_from_bone_block", "bonemeal");
      ShapelessRecipeBuilder.shapeless(Items.BOOK).requires(Items.PAPER, 3).requires(Items.LEATHER).unlockedBy("has_paper", has(Items.PAPER)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.BOOKSHELF).define('#', ItemTags.PLANKS).define('X', Items.BOOK).pattern("###").pattern("XXX").pattern("###").unlockedBy("has_book", has(Items.BOOK)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.BOW).define('#', Items.STICK).define('X', Items.STRING).pattern(" #X").pattern("# X").pattern(" #X").unlockedBy("has_string", has(Items.STRING)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.BOWL, 4).define('#', ItemTags.PLANKS).pattern("# #").pattern(" # ").unlockedBy("has_brown_mushroom", has(Blocks.BROWN_MUSHROOM)).unlockedBy("has_red_mushroom", has(Blocks.RED_MUSHROOM)).unlockedBy("has_mushroom_stew", has(Items.MUSHROOM_STEW)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.BREAD).define('#', Items.WHEAT).pattern("###").unlockedBy("has_wheat", has(Items.WHEAT)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.BREWING_STAND).define('B', Items.BLAZE_ROD).define('#', ItemTags.STONE_CRAFTING_MATERIALS).pattern(" B ").pattern("###").unlockedBy("has_blaze_rod", has(Items.BLAZE_ROD)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.BRICKS).define('#', Items.BRICK).pattern("##").pattern("##").unlockedBy("has_brick", has(Items.BRICK)).save(p_176532_);
      ShapelessRecipeBuilder.shapeless(Items.BROWN_DYE).requires(Items.COCOA_BEANS).group("brown_dye").unlockedBy("has_cocoa_beans", has(Items.COCOA_BEANS)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.BUCKET).define('#', Items.IRON_INGOT).pattern("# #").pattern(" # ").unlockedBy("has_iron_ingot", has(Items.IRON_INGOT)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.CAKE).define('A', Items.MILK_BUCKET).define('B', Items.SUGAR).define('C', Items.WHEAT).define('E', Items.EGG).pattern("AAA").pattern("BEB").pattern("CCC").unlockedBy("has_egg", has(Items.EGG)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.CAMPFIRE).define('L', ItemTags.LOGS).define('S', Items.STICK).define('C', ItemTags.COALS).pattern(" S ").pattern("SCS").pattern("LLL").unlockedBy("has_stick", has(Items.STICK)).unlockedBy("has_coal", has(ItemTags.COALS)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.CARROT_ON_A_STICK).define('#', Items.FISHING_ROD).define('X', Items.CARROT).pattern("# ").pattern(" X").unlockedBy("has_carrot", has(Items.CARROT)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.WARPED_FUNGUS_ON_A_STICK).define('#', Items.FISHING_ROD).define('X', Items.WARPED_FUNGUS).pattern("# ").pattern(" X").unlockedBy("has_warped_fungus", has(Items.WARPED_FUNGUS)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.CAULDRON).define('#', Items.IRON_INGOT).pattern("# #").pattern("# #").pattern("###").unlockedBy("has_water_bucket", has(Items.WATER_BUCKET)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.COMPOSTER).define('#', ItemTags.WOODEN_SLABS).pattern("# #").pattern("# #").pattern("###").unlockedBy("has_wood_slab", has(ItemTags.WOODEN_SLABS)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.CHEST).define('#', ItemTags.PLANKS).pattern("###").pattern("# #").pattern("###").unlockedBy("has_lots_of_items", new InventoryChangeTrigger.TriggerInstance(EntityPredicate.Composite.ANY, MinMaxBounds.Ints.atLeast(10), MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, new ItemPredicate[0])).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.CHEST_MINECART).define('A', Blocks.CHEST).define('B', Items.MINECART).pattern("A").pattern("B").unlockedBy("has_minecart", has(Items.MINECART)).save(p_176532_);
      chiseledBuilder(Blocks.CHISELED_QUARTZ_BLOCK, Ingredient.of(Blocks.QUARTZ_SLAB)).unlockedBy("has_chiseled_quartz_block", has(Blocks.CHISELED_QUARTZ_BLOCK)).unlockedBy("has_quartz_block", has(Blocks.QUARTZ_BLOCK)).unlockedBy("has_quartz_pillar", has(Blocks.QUARTZ_PILLAR)).save(p_176532_);
      chiseledBuilder(Blocks.CHISELED_STONE_BRICKS, Ingredient.of(Blocks.STONE_BRICK_SLAB)).unlockedBy("has_tag", has(ItemTags.STONE_BRICKS)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.CLAY).define('#', Items.CLAY_BALL).pattern("##").pattern("##").unlockedBy("has_clay_ball", has(Items.CLAY_BALL)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.CLOCK).define('#', Items.GOLD_INGOT).define('X', Items.REDSTONE).pattern(" # ").pattern("#X#").pattern(" # ").unlockedBy("has_redstone", has(Items.REDSTONE)).save(p_176532_);
      nineBlockStorageRecipes(p_176532_, Items.COAL, Items.COAL_BLOCK);
      ShapedRecipeBuilder.shaped(Blocks.COARSE_DIRT, 4).define('D', Blocks.DIRT).define('G', Blocks.GRAVEL).pattern("DG").pattern("GD").unlockedBy("has_gravel", has(Blocks.GRAVEL)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.COMPARATOR).define('#', Blocks.REDSTONE_TORCH).define('X', Items.QUARTZ).define('I', Blocks.STONE).pattern(" # ").pattern("#X#").pattern("III").unlockedBy("has_quartz", has(Items.QUARTZ)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.COMPASS).define('#', Items.IRON_INGOT).define('X', Items.REDSTONE).pattern(" # ").pattern("#X#").pattern(" # ").unlockedBy("has_redstone", has(Items.REDSTONE)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.COOKIE, 8).define('#', Items.WHEAT).define('X', Items.COCOA_BEANS).pattern("#X#").unlockedBy("has_cocoa", has(Items.COCOA_BEANS)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.CRAFTING_TABLE).define('#', ItemTags.PLANKS).pattern("##").pattern("##").unlockedBy("has_planks", has(ItemTags.PLANKS)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.CROSSBOW).define('~', Items.STRING).define('#', Items.STICK).define('&', Items.IRON_INGOT).define('$', Blocks.TRIPWIRE_HOOK).pattern("#&#").pattern("~$~").pattern(" # ").unlockedBy("has_string", has(Items.STRING)).unlockedBy("has_stick", has(Items.STICK)).unlockedBy("has_iron_ingot", has(Items.IRON_INGOT)).unlockedBy("has_tripwire_hook", has(Blocks.TRIPWIRE_HOOK)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.LOOM).define('#', ItemTags.PLANKS).define('@', Items.STRING).pattern("@@").pattern("##").unlockedBy("has_string", has(Items.STRING)).save(p_176532_);
      chiseledBuilder(Blocks.CHISELED_RED_SANDSTONE, Ingredient.of(Blocks.RED_SANDSTONE_SLAB)).unlockedBy("has_red_sandstone", has(Blocks.RED_SANDSTONE)).unlockedBy("has_chiseled_red_sandstone", has(Blocks.CHISELED_RED_SANDSTONE)).unlockedBy("has_cut_red_sandstone", has(Blocks.CUT_RED_SANDSTONE)).save(p_176532_);
      chiseled(p_176532_, Blocks.CHISELED_SANDSTONE, Blocks.SANDSTONE_SLAB);
      nineBlockStorageRecipesRecipesWithCustomUnpacking(p_176532_, Items.COPPER_INGOT, Items.COPPER_BLOCK, getSimpleRecipeName(Items.COPPER_INGOT), getItemName(Items.COPPER_INGOT));
      ShapelessRecipeBuilder.shapeless(Items.COPPER_INGOT, 9).requires(Blocks.WAXED_COPPER_BLOCK).group(getItemName(Items.COPPER_INGOT)).unlockedBy(getHasName(Blocks.WAXED_COPPER_BLOCK), has(Blocks.WAXED_COPPER_BLOCK)).save(p_176532_, getConversionRecipeName(Items.COPPER_INGOT, Blocks.WAXED_COPPER_BLOCK));
      waxRecipes(p_176532_);
      ShapelessRecipeBuilder.shapeless(Items.CYAN_DYE, 2).requires(Items.BLUE_DYE).requires(Items.GREEN_DYE).unlockedBy("has_green_dye", has(Items.GREEN_DYE)).unlockedBy("has_blue_dye", has(Items.BLUE_DYE)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.DARK_PRISMARINE).define('S', Items.PRISMARINE_SHARD).define('I', Items.BLACK_DYE).pattern("SSS").pattern("SIS").pattern("SSS").unlockedBy("has_prismarine_shard", has(Items.PRISMARINE_SHARD)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.DAYLIGHT_DETECTOR).define('Q', Items.QUARTZ).define('G', Blocks.GLASS).define('W', Ingredient.of(ItemTags.WOODEN_SLABS)).pattern("GGG").pattern("QQQ").pattern("WWW").unlockedBy("has_quartz", has(Items.QUARTZ)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.DEEPSLATE_BRICKS, 4).define('S', Blocks.POLISHED_DEEPSLATE).pattern("SS").pattern("SS").unlockedBy("has_polished_deepslate", has(Blocks.POLISHED_DEEPSLATE)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.DEEPSLATE_TILES, 4).define('S', Blocks.DEEPSLATE_BRICKS).pattern("SS").pattern("SS").unlockedBy("has_deepslate_bricks", has(Blocks.DEEPSLATE_BRICKS)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.DETECTOR_RAIL, 6).define('R', Items.REDSTONE).define('#', Blocks.STONE_PRESSURE_PLATE).define('X', Items.IRON_INGOT).pattern("X X").pattern("X#X").pattern("XRX").unlockedBy("has_rail", has(Blocks.RAIL)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.DIAMOND_AXE).define('#', Items.STICK).define('X', Items.DIAMOND).pattern("XX").pattern("X#").pattern(" #").unlockedBy("has_diamond", has(Items.DIAMOND)).save(p_176532_);
      nineBlockStorageRecipes(p_176532_, Items.DIAMOND, Items.DIAMOND_BLOCK);
      ShapedRecipeBuilder.shaped(Items.DIAMOND_BOOTS).define('X', Items.DIAMOND).pattern("X X").pattern("X X").unlockedBy("has_diamond", has(Items.DIAMOND)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.DIAMOND_CHESTPLATE).define('X', Items.DIAMOND).pattern("X X").pattern("XXX").pattern("XXX").unlockedBy("has_diamond", has(Items.DIAMOND)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.DIAMOND_HELMET).define('X', Items.DIAMOND).pattern("XXX").pattern("X X").unlockedBy("has_diamond", has(Items.DIAMOND)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.DIAMOND_HOE).define('#', Items.STICK).define('X', Items.DIAMOND).pattern("XX").pattern(" #").pattern(" #").unlockedBy("has_diamond", has(Items.DIAMOND)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.DIAMOND_LEGGINGS).define('X', Items.DIAMOND).pattern("XXX").pattern("X X").pattern("X X").unlockedBy("has_diamond", has(Items.DIAMOND)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.DIAMOND_PICKAXE).define('#', Items.STICK).define('X', Items.DIAMOND).pattern("XXX").pattern(" # ").pattern(" # ").unlockedBy("has_diamond", has(Items.DIAMOND)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.DIAMOND_SHOVEL).define('#', Items.STICK).define('X', Items.DIAMOND).pattern("X").pattern("#").pattern("#").unlockedBy("has_diamond", has(Items.DIAMOND)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.DIAMOND_SWORD).define('#', Items.STICK).define('X', Items.DIAMOND).pattern("X").pattern("X").pattern("#").unlockedBy("has_diamond", has(Items.DIAMOND)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.DIORITE, 2).define('Q', Items.QUARTZ).define('C', Blocks.COBBLESTONE).pattern("CQ").pattern("QC").unlockedBy("has_quartz", has(Items.QUARTZ)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.DISPENSER).define('R', Items.REDSTONE).define('#', Blocks.COBBLESTONE).define('X', Items.BOW).pattern("###").pattern("#X#").pattern("#R#").unlockedBy("has_bow", has(Items.BOW)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.DRIPSTONE_BLOCK).define('#', Items.POINTED_DRIPSTONE).pattern("##").pattern("##").group("pointed_dripstone").unlockedBy("has_pointed_dripstone", has(Items.POINTED_DRIPSTONE)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.DROPPER).define('R', Items.REDSTONE).define('#', Blocks.COBBLESTONE).pattern("###").pattern("# #").pattern("#R#").unlockedBy("has_redstone", has(Items.REDSTONE)).save(p_176532_);
      nineBlockStorageRecipes(p_176532_, Items.EMERALD, Items.EMERALD_BLOCK);
      ShapedRecipeBuilder.shaped(Blocks.ENCHANTING_TABLE).define('B', Items.BOOK).define('#', Blocks.OBSIDIAN).define('D', Items.DIAMOND).pattern(" B ").pattern("D#D").pattern("###").unlockedBy("has_obsidian", has(Blocks.OBSIDIAN)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.ENDER_CHEST).define('#', Blocks.OBSIDIAN).define('E', Items.ENDER_EYE).pattern("###").pattern("#E#").pattern("###").unlockedBy("has_ender_eye", has(Items.ENDER_EYE)).save(p_176532_);
      ShapelessRecipeBuilder.shapeless(Items.ENDER_EYE).requires(Items.ENDER_PEARL).requires(Items.BLAZE_POWDER).unlockedBy("has_blaze_powder", has(Items.BLAZE_POWDER)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.END_STONE_BRICKS, 4).define('#', Blocks.END_STONE).pattern("##").pattern("##").unlockedBy("has_end_stone", has(Blocks.END_STONE)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.END_CRYSTAL).define('T', Items.GHAST_TEAR).define('E', Items.ENDER_EYE).define('G', Blocks.GLASS).pattern("GGG").pattern("GEG").pattern("GTG").unlockedBy("has_ender_eye", has(Items.ENDER_EYE)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.END_ROD, 4).define('#', Items.POPPED_CHORUS_FRUIT).define('/', Items.BLAZE_ROD).pattern("/").pattern("#").unlockedBy("has_chorus_fruit_popped", has(Items.POPPED_CHORUS_FRUIT)).save(p_176532_);
      ShapelessRecipeBuilder.shapeless(Items.FERMENTED_SPIDER_EYE).requires(Items.SPIDER_EYE).requires(Blocks.BROWN_MUSHROOM).requires(Items.SUGAR).unlockedBy("has_spider_eye", has(Items.SPIDER_EYE)).save(p_176532_);
      ShapelessRecipeBuilder.shapeless(Items.FIRE_CHARGE, 3).requires(Items.GUNPOWDER).requires(Items.BLAZE_POWDER).requires(Ingredient.of(Items.COAL, Items.CHARCOAL)).unlockedBy("has_blaze_powder", has(Items.BLAZE_POWDER)).save(p_176532_);
      ShapelessRecipeBuilder.shapeless(Items.FIREWORK_ROCKET, 3).requires(Items.GUNPOWDER).requires(Items.PAPER).unlockedBy("has_gunpowder", has(Items.GUNPOWDER)).save(p_176532_, "firework_rocket_simple");
      ShapedRecipeBuilder.shaped(Items.FISHING_ROD).define('#', Items.STICK).define('X', Items.STRING).pattern("  #").pattern(" #X").pattern("# X").unlockedBy("has_string", has(Items.STRING)).save(p_176532_);
      ShapelessRecipeBuilder.shapeless(Items.FLINT_AND_STEEL).requires(Items.IRON_INGOT).requires(Items.FLINT).unlockedBy("has_flint", has(Items.FLINT)).unlockedBy("has_obsidian", has(Blocks.OBSIDIAN)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.FLOWER_POT).define('#', Items.BRICK).pattern("# #").pattern(" # ").unlockedBy("has_brick", has(Items.BRICK)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.FURNACE).define('#', ItemTags.STONE_CRAFTING_MATERIALS).pattern("###").pattern("# #").pattern("###").unlockedBy("has_cobblestone", has(ItemTags.STONE_CRAFTING_MATERIALS)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.FURNACE_MINECART).define('A', Blocks.FURNACE).define('B', Items.MINECART).pattern("A").pattern("B").unlockedBy("has_minecart", has(Items.MINECART)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.GLASS_BOTTLE, 3).define('#', Blocks.GLASS).pattern("# #").pattern(" # ").unlockedBy("has_glass", has(Blocks.GLASS)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.GLASS_PANE, 16).define('#', Blocks.GLASS).pattern("###").pattern("###").unlockedBy("has_glass", has(Blocks.GLASS)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.GLOWSTONE).define('#', Items.GLOWSTONE_DUST).pattern("##").pattern("##").unlockedBy("has_glowstone_dust", has(Items.GLOWSTONE_DUST)).save(p_176532_);
      ShapelessRecipeBuilder.shapeless(Items.GLOW_ITEM_FRAME).requires(Items.ITEM_FRAME).requires(Items.GLOW_INK_SAC).unlockedBy("has_item_frame", has(Items.ITEM_FRAME)).unlockedBy("has_glow_ink_sac", has(Items.GLOW_INK_SAC)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.GOLDEN_APPLE).define('#', Items.GOLD_INGOT).define('X', Items.APPLE).pattern("###").pattern("#X#").pattern("###").unlockedBy("has_gold_ingot", has(Items.GOLD_INGOT)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.GOLDEN_AXE).define('#', Items.STICK).define('X', Items.GOLD_INGOT).pattern("XX").pattern("X#").pattern(" #").unlockedBy("has_gold_ingot", has(Items.GOLD_INGOT)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.GOLDEN_BOOTS).define('X', Items.GOLD_INGOT).pattern("X X").pattern("X X").unlockedBy("has_gold_ingot", has(Items.GOLD_INGOT)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.GOLDEN_CARROT).define('#', Items.GOLD_NUGGET).define('X', Items.CARROT).pattern("###").pattern("#X#").pattern("###").unlockedBy("has_gold_nugget", has(Items.GOLD_NUGGET)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.GOLDEN_CHESTPLATE).define('X', Items.GOLD_INGOT).pattern("X X").pattern("XXX").pattern("XXX").unlockedBy("has_gold_ingot", has(Items.GOLD_INGOT)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.GOLDEN_HELMET).define('X', Items.GOLD_INGOT).pattern("XXX").pattern("X X").unlockedBy("has_gold_ingot", has(Items.GOLD_INGOT)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.GOLDEN_HOE).define('#', Items.STICK).define('X', Items.GOLD_INGOT).pattern("XX").pattern(" #").pattern(" #").unlockedBy("has_gold_ingot", has(Items.GOLD_INGOT)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.GOLDEN_LEGGINGS).define('X', Items.GOLD_INGOT).pattern("XXX").pattern("X X").pattern("X X").unlockedBy("has_gold_ingot", has(Items.GOLD_INGOT)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.GOLDEN_PICKAXE).define('#', Items.STICK).define('X', Items.GOLD_INGOT).pattern("XXX").pattern(" # ").pattern(" # ").unlockedBy("has_gold_ingot", has(Items.GOLD_INGOT)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.POWERED_RAIL, 6).define('R', Items.REDSTONE).define('#', Items.STICK).define('X', Items.GOLD_INGOT).pattern("X X").pattern("X#X").pattern("XRX").unlockedBy("has_rail", has(Blocks.RAIL)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.GOLDEN_SHOVEL).define('#', Items.STICK).define('X', Items.GOLD_INGOT).pattern("X").pattern("#").pattern("#").unlockedBy("has_gold_ingot", has(Items.GOLD_INGOT)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.GOLDEN_SWORD).define('#', Items.STICK).define('X', Items.GOLD_INGOT).pattern("X").pattern("X").pattern("#").unlockedBy("has_gold_ingot", has(Items.GOLD_INGOT)).save(p_176532_);
      nineBlockStorageRecipesRecipesWithCustomUnpacking(p_176532_, Items.GOLD_INGOT, Items.GOLD_BLOCK, "gold_ingot_from_gold_block", "gold_ingot");
      nineBlockStorageRecipesWithCustomPacking(p_176532_, Items.GOLD_NUGGET, Items.GOLD_INGOT, "gold_ingot_from_nuggets", "gold_ingot");
      ShapelessRecipeBuilder.shapeless(Blocks.GRANITE).requires(Blocks.DIORITE).requires(Items.QUARTZ).unlockedBy("has_quartz", has(Items.QUARTZ)).save(p_176532_);
      ShapelessRecipeBuilder.shapeless(Items.GRAY_DYE, 2).requires(Items.BLACK_DYE).requires(Items.WHITE_DYE).unlockedBy("has_white_dye", has(Items.WHITE_DYE)).unlockedBy("has_black_dye", has(Items.BLACK_DYE)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.HAY_BLOCK).define('#', Items.WHEAT).pattern("###").pattern("###").pattern("###").unlockedBy("has_wheat", has(Items.WHEAT)).save(p_176532_);
      pressurePlate(p_176532_, Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, Items.IRON_INGOT);
      ShapelessRecipeBuilder.shapeless(Items.HONEY_BOTTLE, 4).requires(Items.HONEY_BLOCK).requires(Items.GLASS_BOTTLE, 4).unlockedBy("has_honey_block", has(Blocks.HONEY_BLOCK)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.HONEY_BLOCK, 1).define('S', Items.HONEY_BOTTLE).pattern("SS").pattern("SS").unlockedBy("has_honey_bottle", has(Items.HONEY_BOTTLE)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.HONEYCOMB_BLOCK).define('H', Items.HONEYCOMB).pattern("HH").pattern("HH").unlockedBy("has_honeycomb", has(Items.HONEYCOMB)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.HOPPER).define('C', Blocks.CHEST).define('I', Items.IRON_INGOT).pattern("I I").pattern("ICI").pattern(" I ").unlockedBy("has_iron_ingot", has(Items.IRON_INGOT)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.HOPPER_MINECART).define('A', Blocks.HOPPER).define('B', Items.MINECART).pattern("A").pattern("B").unlockedBy("has_minecart", has(Items.MINECART)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.IRON_AXE).define('#', Items.STICK).define('X', Items.IRON_INGOT).pattern("XX").pattern("X#").pattern(" #").unlockedBy("has_iron_ingot", has(Items.IRON_INGOT)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.IRON_BARS, 16).define('#', Items.IRON_INGOT).pattern("###").pattern("###").unlockedBy("has_iron_ingot", has(Items.IRON_INGOT)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.IRON_BOOTS).define('X', Items.IRON_INGOT).pattern("X X").pattern("X X").unlockedBy("has_iron_ingot", has(Items.IRON_INGOT)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.IRON_CHESTPLATE).define('X', Items.IRON_INGOT).pattern("X X").pattern("XXX").pattern("XXX").unlockedBy("has_iron_ingot", has(Items.IRON_INGOT)).save(p_176532_);
      doorBuilder(Blocks.IRON_DOOR, Ingredient.of(Items.IRON_INGOT)).unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.IRON_HELMET).define('X', Items.IRON_INGOT).pattern("XXX").pattern("X X").unlockedBy("has_iron_ingot", has(Items.IRON_INGOT)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.IRON_HOE).define('#', Items.STICK).define('X', Items.IRON_INGOT).pattern("XX").pattern(" #").pattern(" #").unlockedBy("has_iron_ingot", has(Items.IRON_INGOT)).save(p_176532_);
      nineBlockStorageRecipesRecipesWithCustomUnpacking(p_176532_, Items.IRON_INGOT, Items.IRON_BLOCK, "iron_ingot_from_iron_block", "iron_ingot");
      nineBlockStorageRecipesWithCustomPacking(p_176532_, Items.IRON_NUGGET, Items.IRON_INGOT, "iron_ingot_from_nuggets", "iron_ingot");
      ShapedRecipeBuilder.shaped(Items.IRON_LEGGINGS).define('X', Items.IRON_INGOT).pattern("XXX").pattern("X X").pattern("X X").unlockedBy("has_iron_ingot", has(Items.IRON_INGOT)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.IRON_PICKAXE).define('#', Items.STICK).define('X', Items.IRON_INGOT).pattern("XXX").pattern(" # ").pattern(" # ").unlockedBy("has_iron_ingot", has(Items.IRON_INGOT)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.IRON_SHOVEL).define('#', Items.STICK).define('X', Items.IRON_INGOT).pattern("X").pattern("#").pattern("#").unlockedBy("has_iron_ingot", has(Items.IRON_INGOT)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.IRON_SWORD).define('#', Items.STICK).define('X', Items.IRON_INGOT).pattern("X").pattern("X").pattern("#").unlockedBy("has_iron_ingot", has(Items.IRON_INGOT)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.IRON_TRAPDOOR).define('#', Items.IRON_INGOT).pattern("##").pattern("##").unlockedBy("has_iron_ingot", has(Items.IRON_INGOT)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.ITEM_FRAME).define('#', Items.STICK).define('X', Items.LEATHER).pattern("###").pattern("#X#").pattern("###").unlockedBy("has_leather", has(Items.LEATHER)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.JUKEBOX).define('#', ItemTags.PLANKS).define('X', Items.DIAMOND).pattern("###").pattern("#X#").pattern("###").unlockedBy("has_diamond", has(Items.DIAMOND)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.LADDER, 3).define('#', Items.STICK).pattern("# #").pattern("###").pattern("# #").unlockedBy("has_stick", has(Items.STICK)).save(p_176532_);
      nineBlockStorageRecipes(p_176532_, Items.LAPIS_LAZULI, Items.LAPIS_BLOCK);
      ShapedRecipeBuilder.shaped(Items.LEAD, 2).define('~', Items.STRING).define('O', Items.SLIME_BALL).pattern("~~ ").pattern("~O ").pattern("  ~").unlockedBy("has_slime_ball", has(Items.SLIME_BALL)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.LEATHER).define('#', Items.RABBIT_HIDE).pattern("##").pattern("##").unlockedBy("has_rabbit_hide", has(Items.RABBIT_HIDE)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.LEATHER_BOOTS).define('X', Items.LEATHER).pattern("X X").pattern("X X").unlockedBy("has_leather", has(Items.LEATHER)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.LEATHER_CHESTPLATE).define('X', Items.LEATHER).pattern("X X").pattern("XXX").pattern("XXX").unlockedBy("has_leather", has(Items.LEATHER)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.LEATHER_HELMET).define('X', Items.LEATHER).pattern("XXX").pattern("X X").unlockedBy("has_leather", has(Items.LEATHER)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.LEATHER_LEGGINGS).define('X', Items.LEATHER).pattern("XXX").pattern("X X").pattern("X X").unlockedBy("has_leather", has(Items.LEATHER)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.LEATHER_HORSE_ARMOR).define('X', Items.LEATHER).pattern("X X").pattern("XXX").pattern("X X").unlockedBy("has_leather", has(Items.LEATHER)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.LECTERN).define('S', ItemTags.WOODEN_SLABS).define('B', Blocks.BOOKSHELF).pattern("SSS").pattern(" B ").pattern(" S ").unlockedBy("has_book", has(Items.BOOK)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.LEVER).define('#', Blocks.COBBLESTONE).define('X', Items.STICK).pattern("X").pattern("#").unlockedBy("has_cobblestone", has(Blocks.COBBLESTONE)).save(p_176532_);
      oneToOneConversionRecipe(p_176532_, Items.LIGHT_BLUE_DYE, Blocks.BLUE_ORCHID, "light_blue_dye");
      ShapelessRecipeBuilder.shapeless(Items.LIGHT_BLUE_DYE, 2).requires(Items.BLUE_DYE).requires(Items.WHITE_DYE).group("light_blue_dye").unlockedBy("has_blue_dye", has(Items.BLUE_DYE)).unlockedBy("has_white_dye", has(Items.WHITE_DYE)).save(p_176532_, "light_blue_dye_from_blue_white_dye");
      oneToOneConversionRecipe(p_176532_, Items.LIGHT_GRAY_DYE, Blocks.AZURE_BLUET, "light_gray_dye");
      ShapelessRecipeBuilder.shapeless(Items.LIGHT_GRAY_DYE, 2).requires(Items.GRAY_DYE).requires(Items.WHITE_DYE).group("light_gray_dye").unlockedBy("has_gray_dye", has(Items.GRAY_DYE)).unlockedBy("has_white_dye", has(Items.WHITE_DYE)).save(p_176532_, "light_gray_dye_from_gray_white_dye");
      ShapelessRecipeBuilder.shapeless(Items.LIGHT_GRAY_DYE, 3).requires(Items.BLACK_DYE).requires(Items.WHITE_DYE, 2).group("light_gray_dye").unlockedBy("has_white_dye", has(Items.WHITE_DYE)).unlockedBy("has_black_dye", has(Items.BLACK_DYE)).save(p_176532_, "light_gray_dye_from_black_white_dye");
      oneToOneConversionRecipe(p_176532_, Items.LIGHT_GRAY_DYE, Blocks.OXEYE_DAISY, "light_gray_dye");
      oneToOneConversionRecipe(p_176532_, Items.LIGHT_GRAY_DYE, Blocks.WHITE_TULIP, "light_gray_dye");
      pressurePlate(p_176532_, Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, Items.GOLD_INGOT);
      ShapedRecipeBuilder.shaped(Blocks.LIGHTNING_ROD).define('#', Items.COPPER_INGOT).pattern("#").pattern("#").pattern("#").unlockedBy("has_copper_ingot", has(Items.COPPER_INGOT)).save(p_176532_);
      ShapelessRecipeBuilder.shapeless(Items.LIME_DYE, 2).requires(Items.GREEN_DYE).requires(Items.WHITE_DYE).unlockedBy("has_green_dye", has(Items.GREEN_DYE)).unlockedBy("has_white_dye", has(Items.WHITE_DYE)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.JACK_O_LANTERN).define('A', Blocks.CARVED_PUMPKIN).define('B', Blocks.TORCH).pattern("A").pattern("B").unlockedBy("has_carved_pumpkin", has(Blocks.CARVED_PUMPKIN)).save(p_176532_);
      oneToOneConversionRecipe(p_176532_, Items.MAGENTA_DYE, Blocks.ALLIUM, "magenta_dye");
      ShapelessRecipeBuilder.shapeless(Items.MAGENTA_DYE, 4).requires(Items.BLUE_DYE).requires(Items.RED_DYE, 2).requires(Items.WHITE_DYE).group("magenta_dye").unlockedBy("has_blue_dye", has(Items.BLUE_DYE)).unlockedBy("has_rose_red", has(Items.RED_DYE)).unlockedBy("has_white_dye", has(Items.WHITE_DYE)).save(p_176532_, "magenta_dye_from_blue_red_white_dye");
      ShapelessRecipeBuilder.shapeless(Items.MAGENTA_DYE, 3).requires(Items.BLUE_DYE).requires(Items.RED_DYE).requires(Items.PINK_DYE).group("magenta_dye").unlockedBy("has_pink_dye", has(Items.PINK_DYE)).unlockedBy("has_blue_dye", has(Items.BLUE_DYE)).unlockedBy("has_red_dye", has(Items.RED_DYE)).save(p_176532_, "magenta_dye_from_blue_red_pink");
      oneToOneConversionRecipe(p_176532_, Items.MAGENTA_DYE, Blocks.LILAC, "magenta_dye", 2);
      ShapelessRecipeBuilder.shapeless(Items.MAGENTA_DYE, 2).requires(Items.PURPLE_DYE).requires(Items.PINK_DYE).group("magenta_dye").unlockedBy("has_pink_dye", has(Items.PINK_DYE)).unlockedBy("has_purple_dye", has(Items.PURPLE_DYE)).save(p_176532_, "magenta_dye_from_purple_and_pink");
      ShapedRecipeBuilder.shaped(Blocks.MAGMA_BLOCK).define('#', Items.MAGMA_CREAM).pattern("##").pattern("##").unlockedBy("has_magma_cream", has(Items.MAGMA_CREAM)).save(p_176532_);
      ShapelessRecipeBuilder.shapeless(Items.MAGMA_CREAM).requires(Items.BLAZE_POWDER).requires(Items.SLIME_BALL).unlockedBy("has_blaze_powder", has(Items.BLAZE_POWDER)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.MAP).define('#', Items.PAPER).define('X', Items.COMPASS).pattern("###").pattern("#X#").pattern("###").unlockedBy("has_compass", has(Items.COMPASS)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.MELON).define('M', Items.MELON_SLICE).pattern("MMM").pattern("MMM").pattern("MMM").unlockedBy("has_melon", has(Items.MELON_SLICE)).save(p_176532_);
      ShapelessRecipeBuilder.shapeless(Items.MELON_SEEDS).requires(Items.MELON_SLICE).unlockedBy("has_melon", has(Items.MELON_SLICE)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.MINECART).define('#', Items.IRON_INGOT).pattern("# #").pattern("###").unlockedBy("has_iron_ingot", has(Items.IRON_INGOT)).save(p_176532_);
      ShapelessRecipeBuilder.shapeless(Blocks.MOSSY_COBBLESTONE).requires(Blocks.COBBLESTONE).requires(Blocks.VINE).group("mossy_cobblestone").unlockedBy("has_vine", has(Blocks.VINE)).save(p_176532_, getConversionRecipeName(Blocks.MOSSY_COBBLESTONE, Blocks.VINE));
      ShapelessRecipeBuilder.shapeless(Blocks.MOSSY_STONE_BRICKS).requires(Blocks.STONE_BRICKS).requires(Blocks.VINE).group("mossy_stone_bricks").unlockedBy("has_vine", has(Blocks.VINE)).save(p_176532_, getConversionRecipeName(Blocks.MOSSY_STONE_BRICKS, Blocks.VINE));
      ShapelessRecipeBuilder.shapeless(Blocks.MOSSY_COBBLESTONE).requires(Blocks.COBBLESTONE).requires(Blocks.MOSS_BLOCK).group("mossy_cobblestone").unlockedBy("has_moss_block", has(Blocks.MOSS_BLOCK)).save(p_176532_, getConversionRecipeName(Blocks.MOSSY_COBBLESTONE, Blocks.MOSS_BLOCK));
      ShapelessRecipeBuilder.shapeless(Blocks.MOSSY_STONE_BRICKS).requires(Blocks.STONE_BRICKS).requires(Blocks.MOSS_BLOCK).group("mossy_stone_bricks").unlockedBy("has_moss_block", has(Blocks.MOSS_BLOCK)).save(p_176532_, getConversionRecipeName(Blocks.MOSSY_STONE_BRICKS, Blocks.MOSS_BLOCK));
      ShapelessRecipeBuilder.shapeless(Items.MUSHROOM_STEW).requires(Blocks.BROWN_MUSHROOM).requires(Blocks.RED_MUSHROOM).requires(Items.BOWL).unlockedBy("has_mushroom_stew", has(Items.MUSHROOM_STEW)).unlockedBy("has_bowl", has(Items.BOWL)).unlockedBy("has_brown_mushroom", has(Blocks.BROWN_MUSHROOM)).unlockedBy("has_red_mushroom", has(Blocks.RED_MUSHROOM)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.NETHER_BRICKS).define('N', Items.NETHER_BRICK).pattern("NN").pattern("NN").unlockedBy("has_netherbrick", has(Items.NETHER_BRICK)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.NETHER_WART_BLOCK).define('#', Items.NETHER_WART).pattern("###").pattern("###").pattern("###").unlockedBy("has_nether_wart", has(Items.NETHER_WART)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.NOTE_BLOCK).define('#', ItemTags.PLANKS).define('X', Items.REDSTONE).pattern("###").pattern("#X#").pattern("###").unlockedBy("has_redstone", has(Items.REDSTONE)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.OBSERVER).define('Q', Items.QUARTZ).define('R', Items.REDSTONE).define('#', Blocks.COBBLESTONE).pattern("###").pattern("RRQ").pattern("###").unlockedBy("has_quartz", has(Items.QUARTZ)).save(p_176532_);
      oneToOneConversionRecipe(p_176532_, Items.ORANGE_DYE, Blocks.ORANGE_TULIP, "orange_dye");
      ShapelessRecipeBuilder.shapeless(Items.ORANGE_DYE, 2).requires(Items.RED_DYE).requires(Items.YELLOW_DYE).group("orange_dye").unlockedBy("has_red_dye", has(Items.RED_DYE)).unlockedBy("has_yellow_dye", has(Items.YELLOW_DYE)).save(p_176532_, "orange_dye_from_red_yellow");
      ShapedRecipeBuilder.shaped(Items.PAINTING).define('#', Items.STICK).define('X', Ingredient.of(ItemTags.WOOL)).pattern("###").pattern("#X#").pattern("###").unlockedBy("has_wool", has(ItemTags.WOOL)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.PAPER, 3).define('#', Blocks.SUGAR_CANE).pattern("###").unlockedBy("has_reeds", has(Blocks.SUGAR_CANE)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.QUARTZ_PILLAR, 2).define('#', Blocks.QUARTZ_BLOCK).pattern("#").pattern("#").unlockedBy("has_chiseled_quartz_block", has(Blocks.CHISELED_QUARTZ_BLOCK)).unlockedBy("has_quartz_block", has(Blocks.QUARTZ_BLOCK)).unlockedBy("has_quartz_pillar", has(Blocks.QUARTZ_PILLAR)).save(p_176532_);
      ShapelessRecipeBuilder.shapeless(Blocks.PACKED_ICE).requires(Blocks.ICE, 9).unlockedBy("has_ice", has(Blocks.ICE)).save(p_176532_);
      oneToOneConversionRecipe(p_176532_, Items.PINK_DYE, Blocks.PEONY, "pink_dye", 2);
      oneToOneConversionRecipe(p_176532_, Items.PINK_DYE, Blocks.PINK_TULIP, "pink_dye");
      ShapelessRecipeBuilder.shapeless(Items.PINK_DYE, 2).requires(Items.RED_DYE).requires(Items.WHITE_DYE).group("pink_dye").unlockedBy("has_white_dye", has(Items.WHITE_DYE)).unlockedBy("has_red_dye", has(Items.RED_DYE)).save(p_176532_, "pink_dye_from_red_white_dye");
      ShapedRecipeBuilder.shaped(Blocks.PISTON).define('R', Items.REDSTONE).define('#', Blocks.COBBLESTONE).define('T', ItemTags.PLANKS).define('X', Items.IRON_INGOT).pattern("TTT").pattern("#X#").pattern("#R#").unlockedBy("has_redstone", has(Items.REDSTONE)).save(p_176532_);
      polished(p_176532_, Blocks.POLISHED_BASALT, Blocks.BASALT);
      ShapedRecipeBuilder.shaped(Blocks.PRISMARINE).define('S', Items.PRISMARINE_SHARD).pattern("SS").pattern("SS").unlockedBy("has_prismarine_shard", has(Items.PRISMARINE_SHARD)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.PRISMARINE_BRICKS).define('S', Items.PRISMARINE_SHARD).pattern("SSS").pattern("SSS").pattern("SSS").unlockedBy("has_prismarine_shard", has(Items.PRISMARINE_SHARD)).save(p_176532_);
      ShapelessRecipeBuilder.shapeless(Items.PUMPKIN_PIE).requires(Blocks.PUMPKIN).requires(Items.SUGAR).requires(Items.EGG).unlockedBy("has_carved_pumpkin", has(Blocks.CARVED_PUMPKIN)).unlockedBy("has_pumpkin", has(Blocks.PUMPKIN)).save(p_176532_);
      ShapelessRecipeBuilder.shapeless(Items.PUMPKIN_SEEDS, 4).requires(Blocks.PUMPKIN).unlockedBy("has_pumpkin", has(Blocks.PUMPKIN)).save(p_176532_);
      ShapelessRecipeBuilder.shapeless(Items.PURPLE_DYE, 2).requires(Items.BLUE_DYE).requires(Items.RED_DYE).unlockedBy("has_blue_dye", has(Items.BLUE_DYE)).unlockedBy("has_red_dye", has(Items.RED_DYE)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.SHULKER_BOX).define('#', Blocks.CHEST).define('-', Items.SHULKER_SHELL).pattern("-").pattern("#").pattern("-").unlockedBy("has_shulker_shell", has(Items.SHULKER_SHELL)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.PURPUR_BLOCK, 4).define('F', Items.POPPED_CHORUS_FRUIT).pattern("FF").pattern("FF").unlockedBy("has_chorus_fruit_popped", has(Items.POPPED_CHORUS_FRUIT)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.PURPUR_PILLAR).define('#', Blocks.PURPUR_SLAB).pattern("#").pattern("#").unlockedBy("has_purpur_block", has(Blocks.PURPUR_BLOCK)).save(p_176532_);
      slabBuilder(Blocks.PURPUR_SLAB, Ingredient.of(Blocks.PURPUR_BLOCK, Blocks.PURPUR_PILLAR)).unlockedBy("has_purpur_block", has(Blocks.PURPUR_BLOCK)).save(p_176532_);
      stairBuilder(Blocks.PURPUR_STAIRS, Ingredient.of(Blocks.PURPUR_BLOCK, Blocks.PURPUR_PILLAR)).unlockedBy("has_purpur_block", has(Blocks.PURPUR_BLOCK)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.QUARTZ_BLOCK).define('#', Items.QUARTZ).pattern("##").pattern("##").unlockedBy("has_quartz", has(Items.QUARTZ)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.QUARTZ_BRICKS, 4).define('#', Blocks.QUARTZ_BLOCK).pattern("##").pattern("##").unlockedBy("has_quartz_block", has(Blocks.QUARTZ_BLOCK)).save(p_176532_);
      slabBuilder(Blocks.QUARTZ_SLAB, Ingredient.of(Blocks.CHISELED_QUARTZ_BLOCK, Blocks.QUARTZ_BLOCK, Blocks.QUARTZ_PILLAR)).unlockedBy("has_chiseled_quartz_block", has(Blocks.CHISELED_QUARTZ_BLOCK)).unlockedBy("has_quartz_block", has(Blocks.QUARTZ_BLOCK)).unlockedBy("has_quartz_pillar", has(Blocks.QUARTZ_PILLAR)).save(p_176532_);
      stairBuilder(Blocks.QUARTZ_STAIRS, Ingredient.of(Blocks.CHISELED_QUARTZ_BLOCK, Blocks.QUARTZ_BLOCK, Blocks.QUARTZ_PILLAR)).unlockedBy("has_chiseled_quartz_block", has(Blocks.CHISELED_QUARTZ_BLOCK)).unlockedBy("has_quartz_block", has(Blocks.QUARTZ_BLOCK)).unlockedBy("has_quartz_pillar", has(Blocks.QUARTZ_PILLAR)).save(p_176532_);
      ShapelessRecipeBuilder.shapeless(Items.RABBIT_STEW).requires(Items.BAKED_POTATO).requires(Items.COOKED_RABBIT).requires(Items.BOWL).requires(Items.CARROT).requires(Blocks.BROWN_MUSHROOM).group("rabbit_stew").unlockedBy("has_cooked_rabbit", has(Items.COOKED_RABBIT)).save(p_176532_, getConversionRecipeName(Items.RABBIT_STEW, Items.BROWN_MUSHROOM));
      ShapelessRecipeBuilder.shapeless(Items.RABBIT_STEW).requires(Items.BAKED_POTATO).requires(Items.COOKED_RABBIT).requires(Items.BOWL).requires(Items.CARROT).requires(Blocks.RED_MUSHROOM).group("rabbit_stew").unlockedBy("has_cooked_rabbit", has(Items.COOKED_RABBIT)).save(p_176532_, getConversionRecipeName(Items.RABBIT_STEW, Items.RED_MUSHROOM));
      ShapedRecipeBuilder.shaped(Blocks.RAIL, 16).define('#', Items.STICK).define('X', Items.IRON_INGOT).pattern("X X").pattern("X#X").pattern("X X").unlockedBy("has_minecart", has(Items.MINECART)).save(p_176532_);
      nineBlockStorageRecipes(p_176532_, Items.REDSTONE, Items.REDSTONE_BLOCK);
      ShapedRecipeBuilder.shaped(Blocks.REDSTONE_LAMP).define('R', Items.REDSTONE).define('G', Blocks.GLOWSTONE).pattern(" R ").pattern("RGR").pattern(" R ").unlockedBy("has_glowstone", has(Blocks.GLOWSTONE)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.REDSTONE_TORCH).define('#', Items.STICK).define('X', Items.REDSTONE).pattern("X").pattern("#").unlockedBy("has_redstone", has(Items.REDSTONE)).save(p_176532_);
      oneToOneConversionRecipe(p_176532_, Items.RED_DYE, Items.BEETROOT, "red_dye");
      oneToOneConversionRecipe(p_176532_, Items.RED_DYE, Blocks.POPPY, "red_dye");
      oneToOneConversionRecipe(p_176532_, Items.RED_DYE, Blocks.ROSE_BUSH, "red_dye", 2);
      ShapelessRecipeBuilder.shapeless(Items.RED_DYE).requires(Blocks.RED_TULIP).group("red_dye").unlockedBy("has_red_flower", has(Blocks.RED_TULIP)).save(p_176532_, "red_dye_from_tulip");
      ShapedRecipeBuilder.shaped(Blocks.RED_NETHER_BRICKS).define('W', Items.NETHER_WART).define('N', Items.NETHER_BRICK).pattern("NW").pattern("WN").unlockedBy("has_nether_wart", has(Items.NETHER_WART)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.RED_SANDSTONE).define('#', Blocks.RED_SAND).pattern("##").pattern("##").unlockedBy("has_sand", has(Blocks.RED_SAND)).save(p_176532_);
      slabBuilder(Blocks.RED_SANDSTONE_SLAB, Ingredient.of(Blocks.RED_SANDSTONE, Blocks.CHISELED_RED_SANDSTONE)).unlockedBy("has_red_sandstone", has(Blocks.RED_SANDSTONE)).unlockedBy("has_chiseled_red_sandstone", has(Blocks.CHISELED_RED_SANDSTONE)).save(p_176532_);
      stairBuilder(Blocks.RED_SANDSTONE_STAIRS, Ingredient.of(Blocks.RED_SANDSTONE, Blocks.CHISELED_RED_SANDSTONE, Blocks.CUT_RED_SANDSTONE)).unlockedBy("has_red_sandstone", has(Blocks.RED_SANDSTONE)).unlockedBy("has_chiseled_red_sandstone", has(Blocks.CHISELED_RED_SANDSTONE)).unlockedBy("has_cut_red_sandstone", has(Blocks.CUT_RED_SANDSTONE)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.REPEATER).define('#', Blocks.REDSTONE_TORCH).define('X', Items.REDSTONE).define('I', Blocks.STONE).pattern("#X#").pattern("III").unlockedBy("has_redstone_torch", has(Blocks.REDSTONE_TORCH)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.SANDSTONE).define('#', Blocks.SAND).pattern("##").pattern("##").unlockedBy("has_sand", has(Blocks.SAND)).save(p_176532_);
      slabBuilder(Blocks.SANDSTONE_SLAB, Ingredient.of(Blocks.SANDSTONE, Blocks.CHISELED_SANDSTONE)).unlockedBy("has_sandstone", has(Blocks.SANDSTONE)).unlockedBy("has_chiseled_sandstone", has(Blocks.CHISELED_SANDSTONE)).save(p_176532_);
      stairBuilder(Blocks.SANDSTONE_STAIRS, Ingredient.of(Blocks.SANDSTONE, Blocks.CHISELED_SANDSTONE, Blocks.CUT_SANDSTONE)).unlockedBy("has_sandstone", has(Blocks.SANDSTONE)).unlockedBy("has_chiseled_sandstone", has(Blocks.CHISELED_SANDSTONE)).unlockedBy("has_cut_sandstone", has(Blocks.CUT_SANDSTONE)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.SEA_LANTERN).define('S', Items.PRISMARINE_SHARD).define('C', Items.PRISMARINE_CRYSTALS).pattern("SCS").pattern("CCC").pattern("SCS").unlockedBy("has_prismarine_crystals", has(Items.PRISMARINE_CRYSTALS)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.SHEARS).define('#', Items.IRON_INGOT).pattern(" #").pattern("# ").unlockedBy("has_iron_ingot", has(Items.IRON_INGOT)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.SHIELD).define('W', ItemTags.PLANKS).define('o', Items.IRON_INGOT).pattern("WoW").pattern("WWW").pattern(" W ").unlockedBy("has_iron_ingot", has(Items.IRON_INGOT)).save(p_176532_);
      nineBlockStorageRecipes(p_176532_, Items.SLIME_BALL, Items.SLIME_BLOCK);
      cut(p_176532_, Blocks.CUT_RED_SANDSTONE, Blocks.RED_SANDSTONE);
      cut(p_176532_, Blocks.CUT_SANDSTONE, Blocks.SANDSTONE);
      ShapedRecipeBuilder.shaped(Blocks.SNOW_BLOCK).define('#', Items.SNOWBALL).pattern("##").pattern("##").unlockedBy("has_snowball", has(Items.SNOWBALL)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.SNOW, 6).define('#', Blocks.SNOW_BLOCK).pattern("###").unlockedBy("has_snowball", has(Items.SNOWBALL)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.SOUL_CAMPFIRE).define('L', ItemTags.LOGS).define('S', Items.STICK).define('#', ItemTags.SOUL_FIRE_BASE_BLOCKS).pattern(" S ").pattern("S#S").pattern("LLL").unlockedBy("has_stick", has(Items.STICK)).unlockedBy("has_soul_sand", has(ItemTags.SOUL_FIRE_BASE_BLOCKS)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.GLISTERING_MELON_SLICE).define('#', Items.GOLD_NUGGET).define('X', Items.MELON_SLICE).pattern("###").pattern("#X#").pattern("###").unlockedBy("has_melon", has(Items.MELON_SLICE)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.SPECTRAL_ARROW, 2).define('#', Items.GLOWSTONE_DUST).define('X', Items.ARROW).pattern(" # ").pattern("#X#").pattern(" # ").unlockedBy("has_glowstone_dust", has(Items.GLOWSTONE_DUST)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.SPYGLASS).define('#', Items.AMETHYST_SHARD).define('X', Items.COPPER_INGOT).pattern(" # ").pattern(" X ").pattern(" X ").unlockedBy("has_amethyst_shard", has(Items.AMETHYST_SHARD)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.STICK, 4).define('#', ItemTags.PLANKS).pattern("#").pattern("#").group("sticks").unlockedBy("has_planks", has(ItemTags.PLANKS)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.STICK, 1).define('#', Blocks.BAMBOO).pattern("#").pattern("#").group("sticks").unlockedBy("has_bamboo", has(Blocks.BAMBOO)).save(p_176532_, "stick_from_bamboo_item");
      ShapedRecipeBuilder.shaped(Blocks.STICKY_PISTON).define('P', Blocks.PISTON).define('S', Items.SLIME_BALL).pattern("S").pattern("P").unlockedBy("has_slime_ball", has(Items.SLIME_BALL)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.STONE_BRICKS, 4).define('#', Blocks.STONE).pattern("##").pattern("##").unlockedBy("has_stone", has(Blocks.STONE)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.STONE_AXE).define('#', Items.STICK).define('X', ItemTags.STONE_TOOL_MATERIALS).pattern("XX").pattern("X#").pattern(" #").unlockedBy("has_cobblestone", has(ItemTags.STONE_TOOL_MATERIALS)).save(p_176532_);
      slabBuilder(Blocks.STONE_BRICK_SLAB, Ingredient.of(Blocks.STONE_BRICKS)).unlockedBy("has_stone_bricks", has(ItemTags.STONE_BRICKS)).save(p_176532_);
      stairBuilder(Blocks.STONE_BRICK_STAIRS, Ingredient.of(Blocks.STONE_BRICKS)).unlockedBy("has_stone_bricks", has(ItemTags.STONE_BRICKS)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.STONE_HOE).define('#', Items.STICK).define('X', ItemTags.STONE_TOOL_MATERIALS).pattern("XX").pattern(" #").pattern(" #").unlockedBy("has_cobblestone", has(ItemTags.STONE_TOOL_MATERIALS)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.STONE_PICKAXE).define('#', Items.STICK).define('X', ItemTags.STONE_TOOL_MATERIALS).pattern("XXX").pattern(" # ").pattern(" # ").unlockedBy("has_cobblestone", has(ItemTags.STONE_TOOL_MATERIALS)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.STONE_SHOVEL).define('#', Items.STICK).define('X', ItemTags.STONE_TOOL_MATERIALS).pattern("X").pattern("#").pattern("#").unlockedBy("has_cobblestone", has(ItemTags.STONE_TOOL_MATERIALS)).save(p_176532_);
      slab(p_176532_, Blocks.SMOOTH_STONE_SLAB, Blocks.SMOOTH_STONE);
      ShapedRecipeBuilder.shaped(Items.STONE_SWORD).define('#', Items.STICK).define('X', ItemTags.STONE_TOOL_MATERIALS).pattern("X").pattern("X").pattern("#").unlockedBy("has_cobblestone", has(ItemTags.STONE_TOOL_MATERIALS)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.WHITE_WOOL).define('#', Items.STRING).pattern("##").pattern("##").unlockedBy("has_string", has(Items.STRING)).save(p_176532_, getConversionRecipeName(Blocks.WHITE_WOOL, Items.STRING));
      oneToOneConversionRecipe(p_176532_, Items.SUGAR, Blocks.SUGAR_CANE, "sugar");
      ShapelessRecipeBuilder.shapeless(Items.SUGAR, 3).requires(Items.HONEY_BOTTLE).group("sugar").unlockedBy("has_honey_bottle", has(Items.HONEY_BOTTLE)).save(p_176532_, getConversionRecipeName(Items.SUGAR, Items.HONEY_BOTTLE));
      ShapedRecipeBuilder.shaped(Blocks.TARGET).define('H', Items.HAY_BLOCK).define('R', Items.REDSTONE).pattern(" R ").pattern("RHR").pattern(" R ").unlockedBy("has_redstone", has(Items.REDSTONE)).unlockedBy("has_hay_block", has(Blocks.HAY_BLOCK)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.TNT).define('#', Ingredient.of(Blocks.SAND, Blocks.RED_SAND)).define('X', Items.GUNPOWDER).pattern("X#X").pattern("#X#").pattern("X#X").unlockedBy("has_gunpowder", has(Items.GUNPOWDER)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.TNT_MINECART).define('A', Blocks.TNT).define('B', Items.MINECART).pattern("A").pattern("B").unlockedBy("has_minecart", has(Items.MINECART)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.TORCH, 4).define('#', Items.STICK).define('X', Ingredient.of(Items.COAL, Items.CHARCOAL)).pattern("X").pattern("#").unlockedBy("has_stone_pickaxe", has(Items.STONE_PICKAXE)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.SOUL_TORCH, 4).define('X', Ingredient.of(Items.COAL, Items.CHARCOAL)).define('#', Items.STICK).define('S', ItemTags.SOUL_FIRE_BASE_BLOCKS).pattern("X").pattern("#").pattern("S").unlockedBy("has_soul_sand", has(ItemTags.SOUL_FIRE_BASE_BLOCKS)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.LANTERN).define('#', Items.TORCH).define('X', Items.IRON_NUGGET).pattern("XXX").pattern("X#X").pattern("XXX").unlockedBy("has_iron_nugget", has(Items.IRON_NUGGET)).unlockedBy("has_iron_ingot", has(Items.IRON_INGOT)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.SOUL_LANTERN).define('#', Items.SOUL_TORCH).define('X', Items.IRON_NUGGET).pattern("XXX").pattern("X#X").pattern("XXX").unlockedBy("has_soul_torch", has(Items.SOUL_TORCH)).save(p_176532_);
      ShapelessRecipeBuilder.shapeless(Blocks.TRAPPED_CHEST).requires(Blocks.CHEST).requires(Blocks.TRIPWIRE_HOOK).unlockedBy("has_tripwire_hook", has(Blocks.TRIPWIRE_HOOK)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.TRIPWIRE_HOOK, 2).define('#', ItemTags.PLANKS).define('S', Items.STICK).define('I', Items.IRON_INGOT).pattern("I").pattern("S").pattern("#").unlockedBy("has_string", has(Items.STRING)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.TURTLE_HELMET).define('X', Items.SCUTE).pattern("XXX").pattern("X X").unlockedBy("has_scute", has(Items.SCUTE)).save(p_176532_);
      ShapelessRecipeBuilder.shapeless(Items.WHEAT, 9).requires(Blocks.HAY_BLOCK).unlockedBy("has_hay_block", has(Blocks.HAY_BLOCK)).save(p_176532_);
      ShapelessRecipeBuilder.shapeless(Items.WHITE_DYE).requires(Items.BONE_MEAL).group("white_dye").unlockedBy("has_bone_meal", has(Items.BONE_MEAL)).save(p_176532_);
      oneToOneConversionRecipe(p_176532_, Items.WHITE_DYE, Blocks.LILY_OF_THE_VALLEY, "white_dye");
      ShapedRecipeBuilder.shaped(Items.WOODEN_AXE).define('#', Items.STICK).define('X', ItemTags.PLANKS).pattern("XX").pattern("X#").pattern(" #").unlockedBy("has_stick", has(Items.STICK)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.WOODEN_HOE).define('#', Items.STICK).define('X', ItemTags.PLANKS).pattern("XX").pattern(" #").pattern(" #").unlockedBy("has_stick", has(Items.STICK)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.WOODEN_PICKAXE).define('#', Items.STICK).define('X', ItemTags.PLANKS).pattern("XXX").pattern(" # ").pattern(" # ").unlockedBy("has_stick", has(Items.STICK)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.WOODEN_SHOVEL).define('#', Items.STICK).define('X', ItemTags.PLANKS).pattern("X").pattern("#").pattern("#").unlockedBy("has_stick", has(Items.STICK)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Items.WOODEN_SWORD).define('#', Items.STICK).define('X', ItemTags.PLANKS).pattern("X").pattern("X").pattern("#").unlockedBy("has_stick", has(Items.STICK)).save(p_176532_);
      ShapelessRecipeBuilder.shapeless(Items.WRITABLE_BOOK).requires(Items.BOOK).requires(Items.INK_SAC).requires(Items.FEATHER).unlockedBy("has_book", has(Items.BOOK)).save(p_176532_);
      oneToOneConversionRecipe(p_176532_, Items.YELLOW_DYE, Blocks.DANDELION, "yellow_dye");
      oneToOneConversionRecipe(p_176532_, Items.YELLOW_DYE, Blocks.SUNFLOWER, "yellow_dye", 2);
      nineBlockStorageRecipes(p_176532_, Items.DRIED_KELP, Items.DRIED_KELP_BLOCK);
      ShapedRecipeBuilder.shaped(Blocks.CONDUIT).define('#', Items.NAUTILUS_SHELL).define('X', Items.HEART_OF_THE_SEA).pattern("###").pattern("#X#").pattern("###").unlockedBy("has_nautilus_core", has(Items.HEART_OF_THE_SEA)).unlockedBy("has_nautilus_shell", has(Items.NAUTILUS_SHELL)).save(p_176532_);
      wall(p_176532_, Blocks.RED_SANDSTONE_WALL, Blocks.RED_SANDSTONE);
      wall(p_176532_, Blocks.STONE_BRICK_WALL, Blocks.STONE_BRICKS);
      wall(p_176532_, Blocks.SANDSTONE_WALL, Blocks.SANDSTONE);
      ShapelessRecipeBuilder.shapeless(Items.CREEPER_BANNER_PATTERN).requires(Items.PAPER).requires(Items.CREEPER_HEAD).unlockedBy("has_creeper_head", has(Items.CREEPER_HEAD)).save(p_176532_);
      ShapelessRecipeBuilder.shapeless(Items.SKULL_BANNER_PATTERN).requires(Items.PAPER).requires(Items.WITHER_SKELETON_SKULL).unlockedBy("has_wither_skeleton_skull", has(Items.WITHER_SKELETON_SKULL)).save(p_176532_);
      ShapelessRecipeBuilder.shapeless(Items.FLOWER_BANNER_PATTERN).requires(Items.PAPER).requires(Blocks.OXEYE_DAISY).unlockedBy("has_oxeye_daisy", has(Blocks.OXEYE_DAISY)).save(p_176532_);
      ShapelessRecipeBuilder.shapeless(Items.MOJANG_BANNER_PATTERN).requires(Items.PAPER).requires(Items.ENCHANTED_GOLDEN_APPLE).unlockedBy("has_enchanted_golden_apple", has(Items.ENCHANTED_GOLDEN_APPLE)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.SCAFFOLDING, 6).define('~', Items.STRING).define('I', Blocks.BAMBOO).pattern("I~I").pattern("I I").pattern("I I").unlockedBy("has_bamboo", has(Blocks.BAMBOO)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.GRINDSTONE).define('I', Items.STICK).define('-', Blocks.STONE_SLAB).define('#', ItemTags.PLANKS).pattern("I-I").pattern("# #").unlockedBy("has_stone_slab", has(Blocks.STONE_SLAB)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.BLAST_FURNACE).define('#', Blocks.SMOOTH_STONE).define('X', Blocks.FURNACE).define('I', Items.IRON_INGOT).pattern("III").pattern("IXI").pattern("###").unlockedBy("has_smooth_stone", has(Blocks.SMOOTH_STONE)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.SMOKER).define('#', ItemTags.LOGS).define('X', Blocks.FURNACE).pattern(" # ").pattern("#X#").pattern(" # ").unlockedBy("has_furnace", has(Blocks.FURNACE)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.CARTOGRAPHY_TABLE).define('#', ItemTags.PLANKS).define('@', Items.PAPER).pattern("@@").pattern("##").pattern("##").unlockedBy("has_paper", has(Items.PAPER)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.SMITHING_TABLE).define('#', ItemTags.PLANKS).define('@', Items.IRON_INGOT).pattern("@@").pattern("##").pattern("##").unlockedBy("has_iron_ingot", has(Items.IRON_INGOT)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.FLETCHING_TABLE).define('#', ItemTags.PLANKS).define('@', Items.FLINT).pattern("@@").pattern("##").pattern("##").unlockedBy("has_flint", has(Items.FLINT)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.STONECUTTER).define('I', Items.IRON_INGOT).define('#', Blocks.STONE).pattern(" I ").pattern("###").unlockedBy("has_stone", has(Blocks.STONE)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.LODESTONE).define('S', Items.CHISELED_STONE_BRICKS).define('#', Items.NETHERITE_INGOT).pattern("SSS").pattern("S#S").pattern("SSS").unlockedBy("has_netherite_ingot", has(Items.NETHERITE_INGOT)).save(p_176532_);
      nineBlockStorageRecipesRecipesWithCustomUnpacking(p_176532_, Items.NETHERITE_INGOT, Items.NETHERITE_BLOCK, "netherite_ingot_from_netherite_block", "netherite_ingot");
      ShapelessRecipeBuilder.shapeless(Items.NETHERITE_INGOT).requires(Items.NETHERITE_SCRAP, 4).requires(Items.GOLD_INGOT, 4).group("netherite_ingot").unlockedBy("has_netherite_scrap", has(Items.NETHERITE_SCRAP)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.RESPAWN_ANCHOR).define('O', Blocks.CRYING_OBSIDIAN).define('G', Blocks.GLOWSTONE).pattern("OOO").pattern("GGG").pattern("OOO").unlockedBy("has_obsidian", has(Blocks.CRYING_OBSIDIAN)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.CHAIN).define('I', Items.IRON_INGOT).define('N', Items.IRON_NUGGET).pattern("N").pattern("I").pattern("N").unlockedBy("has_iron_nugget", has(Items.IRON_NUGGET)).unlockedBy("has_iron_ingot", has(Items.IRON_INGOT)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.TINTED_GLASS, 2).define('G', Blocks.GLASS).define('S', Items.AMETHYST_SHARD).pattern(" S ").pattern("SGS").pattern(" S ").unlockedBy("has_amethyst_shard", has(Items.AMETHYST_SHARD)).save(p_176532_);
      ShapedRecipeBuilder.shaped(Blocks.AMETHYST_BLOCK).define('S', Items.AMETHYST_SHARD).pattern("SS").pattern("SS").unlockedBy("has_amethyst_shard", has(Items.AMETHYST_SHARD)).save(p_176532_);
      SpecialRecipeBuilder.special(RecipeSerializer.ARMOR_DYE).save(p_176532_, "armor_dye");
      SpecialRecipeBuilder.special(RecipeSerializer.BANNER_DUPLICATE).save(p_176532_, "banner_duplicate");
      SpecialRecipeBuilder.special(RecipeSerializer.BOOK_CLONING).save(p_176532_, "book_cloning");
      SpecialRecipeBuilder.special(RecipeSerializer.FIREWORK_ROCKET).save(p_176532_, "firework_rocket");
      SpecialRecipeBuilder.special(RecipeSerializer.FIREWORK_STAR).save(p_176532_, "firework_star");
      SpecialRecipeBuilder.special(RecipeSerializer.FIREWORK_STAR_FADE).save(p_176532_, "firework_star_fade");
      SpecialRecipeBuilder.special(RecipeSerializer.MAP_CLONING).save(p_176532_, "map_cloning");
      SpecialRecipeBuilder.special(RecipeSerializer.MAP_EXTENDING).save(p_176532_, "map_extending");
      SpecialRecipeBuilder.special(RecipeSerializer.REPAIR_ITEM).save(p_176532_, "repair_item");
      SpecialRecipeBuilder.special(RecipeSerializer.SHIELD_DECORATION).save(p_176532_, "shield_decoration");
      SpecialRecipeBuilder.special(RecipeSerializer.SHULKER_BOX_COLORING).save(p_176532_, "shulker_box_coloring");
      SpecialRecipeBuilder.special(RecipeSerializer.TIPPED_ARROW).save(p_176532_, "tipped_arrow");
      SpecialRecipeBuilder.special(RecipeSerializer.SUSPICIOUS_STEW).save(p_176532_, "suspicious_stew");
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.POTATO), Items.BAKED_POTATO, 0.35F, 200).unlockedBy("has_potato", has(Items.POTATO)).save(p_176532_);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.CLAY_BALL), Items.BRICK, 0.3F, 200).unlockedBy("has_clay_ball", has(Items.CLAY_BALL)).save(p_176532_);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(ItemTags.LOGS_THAT_BURN), Items.CHARCOAL, 0.15F, 200).unlockedBy("has_log", has(ItemTags.LOGS_THAT_BURN)).save(p_176532_);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.CHORUS_FRUIT), Items.POPPED_CHORUS_FRUIT, 0.1F, 200).unlockedBy("has_chorus_fruit", has(Items.CHORUS_FRUIT)).save(p_176532_);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.BEEF), Items.COOKED_BEEF, 0.35F, 200).unlockedBy("has_beef", has(Items.BEEF)).save(p_176532_);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.CHICKEN), Items.COOKED_CHICKEN, 0.35F, 200).unlockedBy("has_chicken", has(Items.CHICKEN)).save(p_176532_);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.COD), Items.COOKED_COD, 0.35F, 200).unlockedBy("has_cod", has(Items.COD)).save(p_176532_);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.KELP), Items.DRIED_KELP, 0.1F, 200).unlockedBy("has_kelp", has(Blocks.KELP)).save(p_176532_, getSmeltingRecipeName(Items.DRIED_KELP));
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.SALMON), Items.COOKED_SALMON, 0.35F, 200).unlockedBy("has_salmon", has(Items.SALMON)).save(p_176532_);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.MUTTON), Items.COOKED_MUTTON, 0.35F, 200).unlockedBy("has_mutton", has(Items.MUTTON)).save(p_176532_);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.PORKCHOP), Items.COOKED_PORKCHOP, 0.35F, 200).unlockedBy("has_porkchop", has(Items.PORKCHOP)).save(p_176532_);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.RABBIT), Items.COOKED_RABBIT, 0.35F, 200).unlockedBy("has_rabbit", has(Items.RABBIT)).save(p_176532_);
      oreSmelting(p_176532_, COAL_SMELTABLES, Items.COAL, 0.1F, 200, "coal");
      oreSmelting(p_176532_, IRON_SMELTABLES, Items.IRON_INGOT, 0.7F, 200, "iron_ingot");
      oreSmelting(p_176532_, COPPER_SMELTABLES, Items.COPPER_INGOT, 0.7F, 200, "copper_ingot");
      oreSmelting(p_176532_, GOLD_SMELTABLES, Items.GOLD_INGOT, 1.0F, 200, "gold_ingot");
      oreSmelting(p_176532_, DIAMOND_SMELTABLES, Items.DIAMOND, 1.0F, 200, "diamond");
      oreSmelting(p_176532_, LAPIS_SMELTABLES, Items.LAPIS_LAZULI, 0.2F, 200, "lapis_lazuli");
      oreSmelting(p_176532_, REDSTONE_SMELTABLES, Items.REDSTONE, 0.7F, 200, "redstone");
      oreSmelting(p_176532_, EMERALD_SMELTABLES, Items.EMERALD, 1.0F, 200, "emerald");
      nineBlockStorageRecipes(p_176532_, Items.RAW_IRON, Items.RAW_IRON_BLOCK);
      nineBlockStorageRecipes(p_176532_, Items.RAW_COPPER, Items.RAW_COPPER_BLOCK);
      nineBlockStorageRecipes(p_176532_, Items.RAW_GOLD, Items.RAW_GOLD_BLOCK);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(ItemTags.SAND), Blocks.GLASS.asItem(), 0.1F, 200).unlockedBy("has_sand", has(ItemTags.SAND)).save(p_176532_);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.SEA_PICKLE), Items.LIME_DYE, 0.1F, 200).unlockedBy("has_sea_pickle", has(Blocks.SEA_PICKLE)).save(p_176532_, getSmeltingRecipeName(Items.LIME_DYE));
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.CACTUS.asItem()), Items.GREEN_DYE, 1.0F, 200).unlockedBy("has_cactus", has(Blocks.CACTUS)).save(p_176532_);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.GOLDEN_PICKAXE, Items.GOLDEN_SHOVEL, Items.GOLDEN_AXE, Items.GOLDEN_HOE, Items.GOLDEN_SWORD, Items.GOLDEN_HELMET, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS, Items.GOLDEN_HORSE_ARMOR), Items.GOLD_NUGGET, 0.1F, 200).unlockedBy("has_golden_pickaxe", has(Items.GOLDEN_PICKAXE)).unlockedBy("has_golden_shovel", has(Items.GOLDEN_SHOVEL)).unlockedBy("has_golden_axe", has(Items.GOLDEN_AXE)).unlockedBy("has_golden_hoe", has(Items.GOLDEN_HOE)).unlockedBy("has_golden_sword", has(Items.GOLDEN_SWORD)).unlockedBy("has_golden_helmet", has(Items.GOLDEN_HELMET)).unlockedBy("has_golden_chestplate", has(Items.GOLDEN_CHESTPLATE)).unlockedBy("has_golden_leggings", has(Items.GOLDEN_LEGGINGS)).unlockedBy("has_golden_boots", has(Items.GOLDEN_BOOTS)).unlockedBy("has_golden_horse_armor", has(Items.GOLDEN_HORSE_ARMOR)).save(p_176532_, getSmeltingRecipeName(Items.GOLD_NUGGET));
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.IRON_PICKAXE, Items.IRON_SHOVEL, Items.IRON_AXE, Items.IRON_HOE, Items.IRON_SWORD, Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS, Items.IRON_HORSE_ARMOR, Items.CHAINMAIL_HELMET, Items.CHAINMAIL_CHESTPLATE, Items.CHAINMAIL_LEGGINGS, Items.CHAINMAIL_BOOTS), Items.IRON_NUGGET, 0.1F, 200).unlockedBy("has_iron_pickaxe", has(Items.IRON_PICKAXE)).unlockedBy("has_iron_shovel", has(Items.IRON_SHOVEL)).unlockedBy("has_iron_axe", has(Items.IRON_AXE)).unlockedBy("has_iron_hoe", has(Items.IRON_HOE)).unlockedBy("has_iron_sword", has(Items.IRON_SWORD)).unlockedBy("has_iron_helmet", has(Items.IRON_HELMET)).unlockedBy("has_iron_chestplate", has(Items.IRON_CHESTPLATE)).unlockedBy("has_iron_leggings", has(Items.IRON_LEGGINGS)).unlockedBy("has_iron_boots", has(Items.IRON_BOOTS)).unlockedBy("has_iron_horse_armor", has(Items.IRON_HORSE_ARMOR)).unlockedBy("has_chainmail_helmet", has(Items.CHAINMAIL_HELMET)).unlockedBy("has_chainmail_chestplate", has(Items.CHAINMAIL_CHESTPLATE)).unlockedBy("has_chainmail_leggings", has(Items.CHAINMAIL_LEGGINGS)).unlockedBy("has_chainmail_boots", has(Items.CHAINMAIL_BOOTS)).save(p_176532_, getSmeltingRecipeName(Items.IRON_NUGGET));
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.CLAY), Blocks.TERRACOTTA.asItem(), 0.35F, 200).unlockedBy("has_clay_block", has(Blocks.CLAY)).save(p_176532_);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.NETHERRACK), Items.NETHER_BRICK, 0.1F, 200).unlockedBy("has_netherrack", has(Blocks.NETHERRACK)).save(p_176532_);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.NETHER_QUARTZ_ORE), Items.QUARTZ, 0.2F, 200).unlockedBy("has_nether_quartz_ore", has(Blocks.NETHER_QUARTZ_ORE)).save(p_176532_);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.WET_SPONGE), Blocks.SPONGE.asItem(), 0.15F, 200).unlockedBy("has_wet_sponge", has(Blocks.WET_SPONGE)).save(p_176532_);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.COBBLESTONE), Blocks.STONE.asItem(), 0.1F, 200).unlockedBy("has_cobblestone", has(Blocks.COBBLESTONE)).save(p_176532_);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.STONE), Blocks.SMOOTH_STONE.asItem(), 0.1F, 200).unlockedBy("has_stone", has(Blocks.STONE)).save(p_176532_);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.SANDSTONE), Blocks.SMOOTH_SANDSTONE.asItem(), 0.1F, 200).unlockedBy("has_sandstone", has(Blocks.SANDSTONE)).save(p_176532_);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.RED_SANDSTONE), Blocks.SMOOTH_RED_SANDSTONE.asItem(), 0.1F, 200).unlockedBy("has_red_sandstone", has(Blocks.RED_SANDSTONE)).save(p_176532_);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.QUARTZ_BLOCK), Blocks.SMOOTH_QUARTZ.asItem(), 0.1F, 200).unlockedBy("has_quartz_block", has(Blocks.QUARTZ_BLOCK)).save(p_176532_);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.STONE_BRICKS), Blocks.CRACKED_STONE_BRICKS.asItem(), 0.1F, 200).unlockedBy("has_stone_bricks", has(Blocks.STONE_BRICKS)).save(p_176532_);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.BLACK_TERRACOTTA), Blocks.BLACK_GLAZED_TERRACOTTA.asItem(), 0.1F, 200).unlockedBy("has_black_terracotta", has(Blocks.BLACK_TERRACOTTA)).save(p_176532_);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.BLUE_TERRACOTTA), Blocks.BLUE_GLAZED_TERRACOTTA.asItem(), 0.1F, 200).unlockedBy("has_blue_terracotta", has(Blocks.BLUE_TERRACOTTA)).save(p_176532_);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.BROWN_TERRACOTTA), Blocks.BROWN_GLAZED_TERRACOTTA.asItem(), 0.1F, 200).unlockedBy("has_brown_terracotta", has(Blocks.BROWN_TERRACOTTA)).save(p_176532_);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.CYAN_TERRACOTTA), Blocks.CYAN_GLAZED_TERRACOTTA.asItem(), 0.1F, 200).unlockedBy("has_cyan_terracotta", has(Blocks.CYAN_TERRACOTTA)).save(p_176532_);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.GRAY_TERRACOTTA), Blocks.GRAY_GLAZED_TERRACOTTA.asItem(), 0.1F, 200).unlockedBy("has_gray_terracotta", has(Blocks.GRAY_TERRACOTTA)).save(p_176532_);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.GREEN_TERRACOTTA), Blocks.GREEN_GLAZED_TERRACOTTA.asItem(), 0.1F, 200).unlockedBy("has_green_terracotta", has(Blocks.GREEN_TERRACOTTA)).save(p_176532_);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.LIGHT_BLUE_TERRACOTTA), Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA.asItem(), 0.1F, 200).unlockedBy("has_light_blue_terracotta", has(Blocks.LIGHT_BLUE_TERRACOTTA)).save(p_176532_);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.LIGHT_GRAY_TERRACOTTA), Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA.asItem(), 0.1F, 200).unlockedBy("has_light_gray_terracotta", has(Blocks.LIGHT_GRAY_TERRACOTTA)).save(p_176532_);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.LIME_TERRACOTTA), Blocks.LIME_GLAZED_TERRACOTTA.asItem(), 0.1F, 200).unlockedBy("has_lime_terracotta", has(Blocks.LIME_TERRACOTTA)).save(p_176532_);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.MAGENTA_TERRACOTTA), Blocks.MAGENTA_GLAZED_TERRACOTTA.asItem(), 0.1F, 200).unlockedBy("has_magenta_terracotta", has(Blocks.MAGENTA_TERRACOTTA)).save(p_176532_);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.ORANGE_TERRACOTTA), Blocks.ORANGE_GLAZED_TERRACOTTA.asItem(), 0.1F, 200).unlockedBy("has_orange_terracotta", has(Blocks.ORANGE_TERRACOTTA)).save(p_176532_);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.PINK_TERRACOTTA), Blocks.PINK_GLAZED_TERRACOTTA.asItem(), 0.1F, 200).unlockedBy("has_pink_terracotta", has(Blocks.PINK_TERRACOTTA)).save(p_176532_);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.PURPLE_TERRACOTTA), Blocks.PURPLE_GLAZED_TERRACOTTA.asItem(), 0.1F, 200).unlockedBy("has_purple_terracotta", has(Blocks.PURPLE_TERRACOTTA)).save(p_176532_);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.RED_TERRACOTTA), Blocks.RED_GLAZED_TERRACOTTA.asItem(), 0.1F, 200).unlockedBy("has_red_terracotta", has(Blocks.RED_TERRACOTTA)).save(p_176532_);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.WHITE_TERRACOTTA), Blocks.WHITE_GLAZED_TERRACOTTA.asItem(), 0.1F, 200).unlockedBy("has_white_terracotta", has(Blocks.WHITE_TERRACOTTA)).save(p_176532_);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.YELLOW_TERRACOTTA), Blocks.YELLOW_GLAZED_TERRACOTTA.asItem(), 0.1F, 200).unlockedBy("has_yellow_terracotta", has(Blocks.YELLOW_TERRACOTTA)).save(p_176532_);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.ANCIENT_DEBRIS), Items.NETHERITE_SCRAP, 2.0F, 200).unlockedBy("has_ancient_debris", has(Blocks.ANCIENT_DEBRIS)).save(p_176532_);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.BASALT), Blocks.SMOOTH_BASALT, 0.1F, 200).unlockedBy("has_basalt", has(Blocks.BASALT)).save(p_176532_);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.COBBLED_DEEPSLATE), Blocks.DEEPSLATE, 0.1F, 200).unlockedBy("has_cobbled_deepslate", has(Blocks.COBBLED_DEEPSLATE)).save(p_176532_);
      oreBlasting(p_176532_, COAL_SMELTABLES, Items.COAL, 0.1F, 100, "coal");
      oreBlasting(p_176532_, IRON_SMELTABLES, Items.IRON_INGOT, 0.7F, 100, "iron_ingot");
      oreBlasting(p_176532_, COPPER_SMELTABLES, Items.COPPER_INGOT, 0.7F, 100, "copper_ingot");
      oreBlasting(p_176532_, GOLD_SMELTABLES, Items.GOLD_INGOT, 1.0F, 100, "gold_ingot");
      oreBlasting(p_176532_, DIAMOND_SMELTABLES, Items.DIAMOND, 1.0F, 100, "diamond");
      oreBlasting(p_176532_, LAPIS_SMELTABLES, Items.LAPIS_LAZULI, 0.2F, 100, "lapis_lazuli");
      oreBlasting(p_176532_, REDSTONE_SMELTABLES, Items.REDSTONE, 0.7F, 100, "redstone");
      oreBlasting(p_176532_, EMERALD_SMELTABLES, Items.EMERALD, 1.0F, 100, "emerald");
      SimpleCookingRecipeBuilder.blasting(Ingredient.of(Blocks.NETHER_QUARTZ_ORE), Items.QUARTZ, 0.2F, 100).unlockedBy("has_nether_quartz_ore", has(Blocks.NETHER_QUARTZ_ORE)).save(p_176532_, getBlastingRecipeName(Items.QUARTZ));
      SimpleCookingRecipeBuilder.blasting(Ingredient.of(Items.GOLDEN_PICKAXE, Items.GOLDEN_SHOVEL, Items.GOLDEN_AXE, Items.GOLDEN_HOE, Items.GOLDEN_SWORD, Items.GOLDEN_HELMET, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS, Items.GOLDEN_HORSE_ARMOR), Items.GOLD_NUGGET, 0.1F, 100).unlockedBy("has_golden_pickaxe", has(Items.GOLDEN_PICKAXE)).unlockedBy("has_golden_shovel", has(Items.GOLDEN_SHOVEL)).unlockedBy("has_golden_axe", has(Items.GOLDEN_AXE)).unlockedBy("has_golden_hoe", has(Items.GOLDEN_HOE)).unlockedBy("has_golden_sword", has(Items.GOLDEN_SWORD)).unlockedBy("has_golden_helmet", has(Items.GOLDEN_HELMET)).unlockedBy("has_golden_chestplate", has(Items.GOLDEN_CHESTPLATE)).unlockedBy("has_golden_leggings", has(Items.GOLDEN_LEGGINGS)).unlockedBy("has_golden_boots", has(Items.GOLDEN_BOOTS)).unlockedBy("has_golden_horse_armor", has(Items.GOLDEN_HORSE_ARMOR)).save(p_176532_, getBlastingRecipeName(Items.GOLD_NUGGET));
      SimpleCookingRecipeBuilder.blasting(Ingredient.of(Items.IRON_PICKAXE, Items.IRON_SHOVEL, Items.IRON_AXE, Items.IRON_HOE, Items.IRON_SWORD, Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS, Items.IRON_HORSE_ARMOR, Items.CHAINMAIL_HELMET, Items.CHAINMAIL_CHESTPLATE, Items.CHAINMAIL_LEGGINGS, Items.CHAINMAIL_BOOTS), Items.IRON_NUGGET, 0.1F, 100).unlockedBy("has_iron_pickaxe", has(Items.IRON_PICKAXE)).unlockedBy("has_iron_shovel", has(Items.IRON_SHOVEL)).unlockedBy("has_iron_axe", has(Items.IRON_AXE)).unlockedBy("has_iron_hoe", has(Items.IRON_HOE)).unlockedBy("has_iron_sword", has(Items.IRON_SWORD)).unlockedBy("has_iron_helmet", has(Items.IRON_HELMET)).unlockedBy("has_iron_chestplate", has(Items.IRON_CHESTPLATE)).unlockedBy("has_iron_leggings", has(Items.IRON_LEGGINGS)).unlockedBy("has_iron_boots", has(Items.IRON_BOOTS)).unlockedBy("has_iron_horse_armor", has(Items.IRON_HORSE_ARMOR)).unlockedBy("has_chainmail_helmet", has(Items.CHAINMAIL_HELMET)).unlockedBy("has_chainmail_chestplate", has(Items.CHAINMAIL_CHESTPLATE)).unlockedBy("has_chainmail_leggings", has(Items.CHAINMAIL_LEGGINGS)).unlockedBy("has_chainmail_boots", has(Items.CHAINMAIL_BOOTS)).save(p_176532_, getBlastingRecipeName(Items.IRON_NUGGET));
      SimpleCookingRecipeBuilder.blasting(Ingredient.of(Blocks.ANCIENT_DEBRIS), Items.NETHERITE_SCRAP, 2.0F, 100).unlockedBy("has_ancient_debris", has(Blocks.ANCIENT_DEBRIS)).save(p_176532_, getBlastingRecipeName(Items.NETHERITE_SCRAP));
      cookRecipes(p_176532_, "smoking", RecipeSerializer.SMOKING_RECIPE, 100);
      cookRecipes(p_176532_, "campfire_cooking", RecipeSerializer.CAMPFIRE_COOKING_RECIPE, 600);
      stonecutterResultFromBase(p_176532_, Blocks.STONE_SLAB, Blocks.STONE, 2);
      stonecutterResultFromBase(p_176532_, Blocks.STONE_STAIRS, Blocks.STONE);
      stonecutterResultFromBase(p_176532_, Blocks.STONE_BRICKS, Blocks.STONE);
      stonecutterResultFromBase(p_176532_, Blocks.STONE_BRICK_SLAB, Blocks.STONE, 2);
      stonecutterResultFromBase(p_176532_, Blocks.STONE_BRICK_STAIRS, Blocks.STONE);
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.STONE), Blocks.CHISELED_STONE_BRICKS).unlockedBy("has_stone", has(Blocks.STONE)).save(p_176532_, "chiseled_stone_bricks_stone_from_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.STONE), Blocks.STONE_BRICK_WALL).unlockedBy("has_stone", has(Blocks.STONE)).save(p_176532_, "stone_brick_walls_from_stone_stonecutting");
      stonecutterResultFromBase(p_176532_, Blocks.CUT_SANDSTONE, Blocks.SANDSTONE);
      stonecutterResultFromBase(p_176532_, Blocks.SANDSTONE_SLAB, Blocks.SANDSTONE, 2);
      stonecutterResultFromBase(p_176532_, Blocks.CUT_SANDSTONE_SLAB, Blocks.SANDSTONE, 2);
      stonecutterResultFromBase(p_176532_, Blocks.CUT_SANDSTONE_SLAB, Blocks.CUT_SANDSTONE, 2);
      stonecutterResultFromBase(p_176532_, Blocks.SANDSTONE_STAIRS, Blocks.SANDSTONE);
      stonecutterResultFromBase(p_176532_, Blocks.SANDSTONE_WALL, Blocks.SANDSTONE);
      stonecutterResultFromBase(p_176532_, Blocks.CHISELED_SANDSTONE, Blocks.SANDSTONE);
      stonecutterResultFromBase(p_176532_, Blocks.CUT_RED_SANDSTONE, Blocks.RED_SANDSTONE);
      stonecutterResultFromBase(p_176532_, Blocks.RED_SANDSTONE_SLAB, Blocks.RED_SANDSTONE, 2);
      stonecutterResultFromBase(p_176532_, Blocks.CUT_RED_SANDSTONE_SLAB, Blocks.RED_SANDSTONE, 2);
      stonecutterResultFromBase(p_176532_, Blocks.CUT_RED_SANDSTONE_SLAB, Blocks.CUT_RED_SANDSTONE, 2);
      stonecutterResultFromBase(p_176532_, Blocks.RED_SANDSTONE_STAIRS, Blocks.RED_SANDSTONE);
      stonecutterResultFromBase(p_176532_, Blocks.RED_SANDSTONE_WALL, Blocks.RED_SANDSTONE);
      stonecutterResultFromBase(p_176532_, Blocks.CHISELED_RED_SANDSTONE, Blocks.RED_SANDSTONE);
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.QUARTZ_BLOCK), Blocks.QUARTZ_SLAB, 2).unlockedBy("has_quartz_block", has(Blocks.QUARTZ_BLOCK)).save(p_176532_, "quartz_slab_from_stonecutting");
      stonecutterResultFromBase(p_176532_, Blocks.QUARTZ_STAIRS, Blocks.QUARTZ_BLOCK);
      stonecutterResultFromBase(p_176532_, Blocks.QUARTZ_PILLAR, Blocks.QUARTZ_BLOCK);
      stonecutterResultFromBase(p_176532_, Blocks.CHISELED_QUARTZ_BLOCK, Blocks.QUARTZ_BLOCK);
      stonecutterResultFromBase(p_176532_, Blocks.QUARTZ_BRICKS, Blocks.QUARTZ_BLOCK);
      stonecutterResultFromBase(p_176532_, Blocks.COBBLESTONE_STAIRS, Blocks.COBBLESTONE);
      stonecutterResultFromBase(p_176532_, Blocks.COBBLESTONE_SLAB, Blocks.COBBLESTONE, 2);
      stonecutterResultFromBase(p_176532_, Blocks.COBBLESTONE_WALL, Blocks.COBBLESTONE);
      stonecutterResultFromBase(p_176532_, Blocks.STONE_BRICK_SLAB, Blocks.STONE_BRICKS, 2);
      stonecutterResultFromBase(p_176532_, Blocks.STONE_BRICK_STAIRS, Blocks.STONE_BRICKS);
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.STONE_BRICKS), Blocks.STONE_BRICK_WALL).unlockedBy("has_stone_bricks", has(Blocks.STONE_BRICKS)).save(p_176532_, "stone_brick_wall_from_stone_bricks_stonecutting");
      stonecutterResultFromBase(p_176532_, Blocks.CHISELED_STONE_BRICKS, Blocks.STONE_BRICKS);
      stonecutterResultFromBase(p_176532_, Blocks.BRICK_SLAB, Blocks.BRICKS, 2);
      stonecutterResultFromBase(p_176532_, Blocks.BRICK_STAIRS, Blocks.BRICKS);
      stonecutterResultFromBase(p_176532_, Blocks.BRICK_WALL, Blocks.BRICKS);
      stonecutterResultFromBase(p_176532_, Blocks.NETHER_BRICK_SLAB, Blocks.NETHER_BRICKS, 2);
      stonecutterResultFromBase(p_176532_, Blocks.NETHER_BRICK_STAIRS, Blocks.NETHER_BRICKS);
      stonecutterResultFromBase(p_176532_, Blocks.NETHER_BRICK_WALL, Blocks.NETHER_BRICKS);
      stonecutterResultFromBase(p_176532_, Blocks.CHISELED_NETHER_BRICKS, Blocks.NETHER_BRICKS);
      stonecutterResultFromBase(p_176532_, Blocks.RED_NETHER_BRICK_SLAB, Blocks.RED_NETHER_BRICKS, 2);
      stonecutterResultFromBase(p_176532_, Blocks.RED_NETHER_BRICK_STAIRS, Blocks.RED_NETHER_BRICKS);
      stonecutterResultFromBase(p_176532_, Blocks.RED_NETHER_BRICK_WALL, Blocks.RED_NETHER_BRICKS);
      stonecutterResultFromBase(p_176532_, Blocks.PURPUR_SLAB, Blocks.PURPUR_BLOCK, 2);
      stonecutterResultFromBase(p_176532_, Blocks.PURPUR_STAIRS, Blocks.PURPUR_BLOCK);
      stonecutterResultFromBase(p_176532_, Blocks.PURPUR_PILLAR, Blocks.PURPUR_BLOCK);
      stonecutterResultFromBase(p_176532_, Blocks.PRISMARINE_SLAB, Blocks.PRISMARINE, 2);
      stonecutterResultFromBase(p_176532_, Blocks.PRISMARINE_STAIRS, Blocks.PRISMARINE);
      stonecutterResultFromBase(p_176532_, Blocks.PRISMARINE_WALL, Blocks.PRISMARINE);
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.PRISMARINE_BRICKS), Blocks.PRISMARINE_BRICK_SLAB, 2).unlockedBy("has_prismarine_brick", has(Blocks.PRISMARINE_BRICKS)).save(p_176532_, "prismarine_brick_slab_from_prismarine_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.PRISMARINE_BRICKS), Blocks.PRISMARINE_BRICK_STAIRS).unlockedBy("has_prismarine_brick", has(Blocks.PRISMARINE_BRICKS)).save(p_176532_, "prismarine_brick_stairs_from_prismarine_stonecutting");
      stonecutterResultFromBase(p_176532_, Blocks.DARK_PRISMARINE_SLAB, Blocks.DARK_PRISMARINE, 2);
      stonecutterResultFromBase(p_176532_, Blocks.DARK_PRISMARINE_STAIRS, Blocks.DARK_PRISMARINE);
      stonecutterResultFromBase(p_176532_, Blocks.ANDESITE_SLAB, Blocks.ANDESITE, 2);
      stonecutterResultFromBase(p_176532_, Blocks.ANDESITE_STAIRS, Blocks.ANDESITE);
      stonecutterResultFromBase(p_176532_, Blocks.ANDESITE_WALL, Blocks.ANDESITE);
      stonecutterResultFromBase(p_176532_, Blocks.POLISHED_ANDESITE, Blocks.ANDESITE);
      stonecutterResultFromBase(p_176532_, Blocks.POLISHED_ANDESITE_SLAB, Blocks.ANDESITE, 2);
      stonecutterResultFromBase(p_176532_, Blocks.POLISHED_ANDESITE_STAIRS, Blocks.ANDESITE);
      stonecutterResultFromBase(p_176532_, Blocks.POLISHED_ANDESITE_SLAB, Blocks.POLISHED_ANDESITE, 2);
      stonecutterResultFromBase(p_176532_, Blocks.POLISHED_ANDESITE_STAIRS, Blocks.POLISHED_ANDESITE);
      stonecutterResultFromBase(p_176532_, Blocks.POLISHED_BASALT, Blocks.BASALT);
      stonecutterResultFromBase(p_176532_, Blocks.GRANITE_SLAB, Blocks.GRANITE, 2);
      stonecutterResultFromBase(p_176532_, Blocks.GRANITE_STAIRS, Blocks.GRANITE);
      stonecutterResultFromBase(p_176532_, Blocks.GRANITE_WALL, Blocks.GRANITE);
      stonecutterResultFromBase(p_176532_, Blocks.POLISHED_GRANITE, Blocks.GRANITE);
      stonecutterResultFromBase(p_176532_, Blocks.POLISHED_GRANITE_SLAB, Blocks.GRANITE, 2);
      stonecutterResultFromBase(p_176532_, Blocks.POLISHED_GRANITE_STAIRS, Blocks.GRANITE);
      stonecutterResultFromBase(p_176532_, Blocks.POLISHED_GRANITE_SLAB, Blocks.POLISHED_GRANITE, 2);
      stonecutterResultFromBase(p_176532_, Blocks.POLISHED_GRANITE_STAIRS, Blocks.POLISHED_GRANITE);
      stonecutterResultFromBase(p_176532_, Blocks.DIORITE_SLAB, Blocks.DIORITE, 2);
      stonecutterResultFromBase(p_176532_, Blocks.DIORITE_STAIRS, Blocks.DIORITE);
      stonecutterResultFromBase(p_176532_, Blocks.DIORITE_WALL, Blocks.DIORITE);
      stonecutterResultFromBase(p_176532_, Blocks.POLISHED_DIORITE, Blocks.DIORITE);
      stonecutterResultFromBase(p_176532_, Blocks.POLISHED_DIORITE_SLAB, Blocks.DIORITE, 2);
      stonecutterResultFromBase(p_176532_, Blocks.POLISHED_DIORITE_STAIRS, Blocks.DIORITE);
      stonecutterResultFromBase(p_176532_, Blocks.POLISHED_DIORITE_SLAB, Blocks.POLISHED_DIORITE, 2);
      stonecutterResultFromBase(p_176532_, Blocks.POLISHED_DIORITE_STAIRS, Blocks.POLISHED_DIORITE);
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.MOSSY_STONE_BRICKS), Blocks.MOSSY_STONE_BRICK_SLAB, 2).unlockedBy("has_mossy_stone_bricks", has(Blocks.MOSSY_STONE_BRICKS)).save(p_176532_, "mossy_stone_brick_slab_from_mossy_stone_brick_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.MOSSY_STONE_BRICKS), Blocks.MOSSY_STONE_BRICK_STAIRS).unlockedBy("has_mossy_stone_bricks", has(Blocks.MOSSY_STONE_BRICKS)).save(p_176532_, "mossy_stone_brick_stairs_from_mossy_stone_brick_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.MOSSY_STONE_BRICKS), Blocks.MOSSY_STONE_BRICK_WALL).unlockedBy("has_mossy_stone_bricks", has(Blocks.MOSSY_STONE_BRICKS)).save(p_176532_, "mossy_stone_brick_wall_from_mossy_stone_brick_stonecutting");
      stonecutterResultFromBase(p_176532_, Blocks.MOSSY_COBBLESTONE_SLAB, Blocks.MOSSY_COBBLESTONE, 2);
      stonecutterResultFromBase(p_176532_, Blocks.MOSSY_COBBLESTONE_STAIRS, Blocks.MOSSY_COBBLESTONE);
      stonecutterResultFromBase(p_176532_, Blocks.MOSSY_COBBLESTONE_WALL, Blocks.MOSSY_COBBLESTONE);
      stonecutterResultFromBase(p_176532_, Blocks.SMOOTH_SANDSTONE_SLAB, Blocks.SMOOTH_SANDSTONE, 2);
      stonecutterResultFromBase(p_176532_, Blocks.SMOOTH_SANDSTONE_STAIRS, Blocks.SMOOTH_SANDSTONE);
      stonecutterResultFromBase(p_176532_, Blocks.SMOOTH_RED_SANDSTONE_SLAB, Blocks.SMOOTH_RED_SANDSTONE, 2);
      stonecutterResultFromBase(p_176532_, Blocks.SMOOTH_RED_SANDSTONE_STAIRS, Blocks.SMOOTH_RED_SANDSTONE);
      stonecutterResultFromBase(p_176532_, Blocks.SMOOTH_QUARTZ_SLAB, Blocks.SMOOTH_QUARTZ, 2);
      stonecutterResultFromBase(p_176532_, Blocks.SMOOTH_QUARTZ_STAIRS, Blocks.SMOOTH_QUARTZ);
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.END_STONE_BRICKS), Blocks.END_STONE_BRICK_SLAB, 2).unlockedBy("has_end_stone_brick", has(Blocks.END_STONE_BRICKS)).save(p_176532_, "end_stone_brick_slab_from_end_stone_brick_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.END_STONE_BRICKS), Blocks.END_STONE_BRICK_STAIRS).unlockedBy("has_end_stone_brick", has(Blocks.END_STONE_BRICKS)).save(p_176532_, "end_stone_brick_stairs_from_end_stone_brick_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.END_STONE_BRICKS), Blocks.END_STONE_BRICK_WALL).unlockedBy("has_end_stone_brick", has(Blocks.END_STONE_BRICKS)).save(p_176532_, "end_stone_brick_wall_from_end_stone_brick_stonecutting");
      stonecutterResultFromBase(p_176532_, Blocks.END_STONE_BRICKS, Blocks.END_STONE);
      stonecutterResultFromBase(p_176532_, Blocks.END_STONE_BRICK_SLAB, Blocks.END_STONE, 2);
      stonecutterResultFromBase(p_176532_, Blocks.END_STONE_BRICK_STAIRS, Blocks.END_STONE);
      stonecutterResultFromBase(p_176532_, Blocks.END_STONE_BRICK_WALL, Blocks.END_STONE);
      stonecutterResultFromBase(p_176532_, Blocks.SMOOTH_STONE_SLAB, Blocks.SMOOTH_STONE, 2);
      stonecutterResultFromBase(p_176532_, Blocks.BLACKSTONE_SLAB, Blocks.BLACKSTONE, 2);
      stonecutterResultFromBase(p_176532_, Blocks.BLACKSTONE_STAIRS, Blocks.BLACKSTONE);
      stonecutterResultFromBase(p_176532_, Blocks.BLACKSTONE_WALL, Blocks.BLACKSTONE);
      stonecutterResultFromBase(p_176532_, Blocks.POLISHED_BLACKSTONE, Blocks.BLACKSTONE);
      stonecutterResultFromBase(p_176532_, Blocks.POLISHED_BLACKSTONE_WALL, Blocks.BLACKSTONE);
      stonecutterResultFromBase(p_176532_, Blocks.POLISHED_BLACKSTONE_SLAB, Blocks.BLACKSTONE, 2);
      stonecutterResultFromBase(p_176532_, Blocks.POLISHED_BLACKSTONE_STAIRS, Blocks.BLACKSTONE);
      stonecutterResultFromBase(p_176532_, Blocks.CHISELED_POLISHED_BLACKSTONE, Blocks.BLACKSTONE);
      stonecutterResultFromBase(p_176532_, Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.BLACKSTONE);
      stonecutterResultFromBase(p_176532_, Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, Blocks.BLACKSTONE, 2);
      stonecutterResultFromBase(p_176532_, Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, Blocks.BLACKSTONE);
      stonecutterResultFromBase(p_176532_, Blocks.POLISHED_BLACKSTONE_BRICK_WALL, Blocks.BLACKSTONE);
      stonecutterResultFromBase(p_176532_, Blocks.POLISHED_BLACKSTONE_SLAB, Blocks.POLISHED_BLACKSTONE, 2);
      stonecutterResultFromBase(p_176532_, Blocks.POLISHED_BLACKSTONE_STAIRS, Blocks.POLISHED_BLACKSTONE);
      stonecutterResultFromBase(p_176532_, Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.POLISHED_BLACKSTONE);
      stonecutterResultFromBase(p_176532_, Blocks.POLISHED_BLACKSTONE_WALL, Blocks.POLISHED_BLACKSTONE);
      stonecutterResultFromBase(p_176532_, Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, Blocks.POLISHED_BLACKSTONE, 2);
      stonecutterResultFromBase(p_176532_, Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, Blocks.POLISHED_BLACKSTONE);
      stonecutterResultFromBase(p_176532_, Blocks.POLISHED_BLACKSTONE_BRICK_WALL, Blocks.POLISHED_BLACKSTONE);
      stonecutterResultFromBase(p_176532_, Blocks.CHISELED_POLISHED_BLACKSTONE, Blocks.POLISHED_BLACKSTONE);
      stonecutterResultFromBase(p_176532_, Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, Blocks.POLISHED_BLACKSTONE_BRICKS, 2);
      stonecutterResultFromBase(p_176532_, Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, Blocks.POLISHED_BLACKSTONE_BRICKS);
      stonecutterResultFromBase(p_176532_, Blocks.POLISHED_BLACKSTONE_BRICK_WALL, Blocks.POLISHED_BLACKSTONE_BRICKS);
      stonecutterResultFromBase(p_176532_, Blocks.CUT_COPPER_SLAB, Blocks.CUT_COPPER, 2);
      stonecutterResultFromBase(p_176532_, Blocks.CUT_COPPER_STAIRS, Blocks.CUT_COPPER);
      stonecutterResultFromBase(p_176532_, Blocks.EXPOSED_CUT_COPPER_SLAB, Blocks.EXPOSED_CUT_COPPER, 2);
      stonecutterResultFromBase(p_176532_, Blocks.EXPOSED_CUT_COPPER_STAIRS, Blocks.EXPOSED_CUT_COPPER);
      stonecutterResultFromBase(p_176532_, Blocks.WEATHERED_CUT_COPPER_SLAB, Blocks.WEATHERED_CUT_COPPER, 2);
      stonecutterResultFromBase(p_176532_, Blocks.WEATHERED_CUT_COPPER_STAIRS, Blocks.WEATHERED_CUT_COPPER);
      stonecutterResultFromBase(p_176532_, Blocks.OXIDIZED_CUT_COPPER_SLAB, Blocks.OXIDIZED_CUT_COPPER, 2);
      stonecutterResultFromBase(p_176532_, Blocks.OXIDIZED_CUT_COPPER_STAIRS, Blocks.OXIDIZED_CUT_COPPER);
      stonecutterResultFromBase(p_176532_, Blocks.WAXED_CUT_COPPER_SLAB, Blocks.WAXED_CUT_COPPER, 2);
      stonecutterResultFromBase(p_176532_, Blocks.WAXED_CUT_COPPER_STAIRS, Blocks.WAXED_CUT_COPPER);
      stonecutterResultFromBase(p_176532_, Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB, Blocks.WAXED_EXPOSED_CUT_COPPER, 2);
      stonecutterResultFromBase(p_176532_, Blocks.WAXED_EXPOSED_CUT_COPPER_STAIRS, Blocks.WAXED_EXPOSED_CUT_COPPER);
      stonecutterResultFromBase(p_176532_, Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB, Blocks.WAXED_WEATHERED_CUT_COPPER, 2);
      stonecutterResultFromBase(p_176532_, Blocks.WAXED_WEATHERED_CUT_COPPER_STAIRS, Blocks.WAXED_WEATHERED_CUT_COPPER);
      stonecutterResultFromBase(p_176532_, Blocks.WAXED_OXIDIZED_CUT_COPPER_SLAB, Blocks.WAXED_OXIDIZED_CUT_COPPER, 2);
      stonecutterResultFromBase(p_176532_, Blocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS, Blocks.WAXED_OXIDIZED_CUT_COPPER);
      stonecutterResultFromBase(p_176532_, Blocks.CUT_COPPER, Blocks.COPPER_BLOCK, 4);
      stonecutterResultFromBase(p_176532_, Blocks.CUT_COPPER_STAIRS, Blocks.COPPER_BLOCK, 4);
      stonecutterResultFromBase(p_176532_, Blocks.CUT_COPPER_SLAB, Blocks.COPPER_BLOCK, 8);
      stonecutterResultFromBase(p_176532_, Blocks.EXPOSED_CUT_COPPER, Blocks.EXPOSED_COPPER, 4);
      stonecutterResultFromBase(p_176532_, Blocks.EXPOSED_CUT_COPPER_STAIRS, Blocks.EXPOSED_COPPER, 4);
      stonecutterResultFromBase(p_176532_, Blocks.EXPOSED_CUT_COPPER_SLAB, Blocks.EXPOSED_COPPER, 8);
      stonecutterResultFromBase(p_176532_, Blocks.WEATHERED_CUT_COPPER, Blocks.WEATHERED_COPPER, 4);
      stonecutterResultFromBase(p_176532_, Blocks.WEATHERED_CUT_COPPER_STAIRS, Blocks.WEATHERED_COPPER, 4);
      stonecutterResultFromBase(p_176532_, Blocks.WEATHERED_CUT_COPPER_SLAB, Blocks.WEATHERED_COPPER, 8);
      stonecutterResultFromBase(p_176532_, Blocks.OXIDIZED_CUT_COPPER, Blocks.OXIDIZED_COPPER, 4);
      stonecutterResultFromBase(p_176532_, Blocks.OXIDIZED_CUT_COPPER_STAIRS, Blocks.OXIDIZED_COPPER, 4);
      stonecutterResultFromBase(p_176532_, Blocks.OXIDIZED_CUT_COPPER_SLAB, Blocks.OXIDIZED_COPPER, 8);
      stonecutterResultFromBase(p_176532_, Blocks.WAXED_CUT_COPPER, Blocks.WAXED_COPPER_BLOCK, 4);
      stonecutterResultFromBase(p_176532_, Blocks.WAXED_CUT_COPPER_STAIRS, Blocks.WAXED_COPPER_BLOCK, 4);
      stonecutterResultFromBase(p_176532_, Blocks.WAXED_CUT_COPPER_SLAB, Blocks.WAXED_COPPER_BLOCK, 8);
      stonecutterResultFromBase(p_176532_, Blocks.WAXED_EXPOSED_CUT_COPPER, Blocks.WAXED_EXPOSED_COPPER, 4);
      stonecutterResultFromBase(p_176532_, Blocks.WAXED_EXPOSED_CUT_COPPER_STAIRS, Blocks.WAXED_EXPOSED_COPPER, 4);
      stonecutterResultFromBase(p_176532_, Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB, Blocks.WAXED_EXPOSED_COPPER, 8);
      stonecutterResultFromBase(p_176532_, Blocks.WAXED_WEATHERED_CUT_COPPER, Blocks.WAXED_WEATHERED_COPPER, 4);
      stonecutterResultFromBase(p_176532_, Blocks.WAXED_WEATHERED_CUT_COPPER_STAIRS, Blocks.WAXED_WEATHERED_COPPER, 4);
      stonecutterResultFromBase(p_176532_, Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB, Blocks.WAXED_WEATHERED_COPPER, 8);
      stonecutterResultFromBase(p_176532_, Blocks.WAXED_OXIDIZED_CUT_COPPER, Blocks.WAXED_OXIDIZED_COPPER, 4);
      stonecutterResultFromBase(p_176532_, Blocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS, Blocks.WAXED_OXIDIZED_COPPER, 4);
      stonecutterResultFromBase(p_176532_, Blocks.WAXED_OXIDIZED_CUT_COPPER_SLAB, Blocks.WAXED_OXIDIZED_COPPER, 8);
      stonecutterResultFromBase(p_176532_, Blocks.COBBLED_DEEPSLATE_SLAB, Blocks.COBBLED_DEEPSLATE, 2);
      stonecutterResultFromBase(p_176532_, Blocks.COBBLED_DEEPSLATE_STAIRS, Blocks.COBBLED_DEEPSLATE);
      stonecutterResultFromBase(p_176532_, Blocks.COBBLED_DEEPSLATE_WALL, Blocks.COBBLED_DEEPSLATE);
      stonecutterResultFromBase(p_176532_, Blocks.CHISELED_DEEPSLATE, Blocks.COBBLED_DEEPSLATE);
      stonecutterResultFromBase(p_176532_, Blocks.POLISHED_DEEPSLATE, Blocks.COBBLED_DEEPSLATE);
      stonecutterResultFromBase(p_176532_, Blocks.POLISHED_DEEPSLATE_SLAB, Blocks.COBBLED_DEEPSLATE, 2);
      stonecutterResultFromBase(p_176532_, Blocks.POLISHED_DEEPSLATE_STAIRS, Blocks.COBBLED_DEEPSLATE);
      stonecutterResultFromBase(p_176532_, Blocks.POLISHED_DEEPSLATE_WALL, Blocks.COBBLED_DEEPSLATE);
      stonecutterResultFromBase(p_176532_, Blocks.DEEPSLATE_BRICKS, Blocks.COBBLED_DEEPSLATE);
      stonecutterResultFromBase(p_176532_, Blocks.DEEPSLATE_BRICK_SLAB, Blocks.COBBLED_DEEPSLATE, 2);
      stonecutterResultFromBase(p_176532_, Blocks.DEEPSLATE_BRICK_STAIRS, Blocks.COBBLED_DEEPSLATE);
      stonecutterResultFromBase(p_176532_, Blocks.DEEPSLATE_BRICK_WALL, Blocks.COBBLED_DEEPSLATE);
      stonecutterResultFromBase(p_176532_, Blocks.DEEPSLATE_TILES, Blocks.COBBLED_DEEPSLATE);
      stonecutterResultFromBase(p_176532_, Blocks.DEEPSLATE_TILE_SLAB, Blocks.COBBLED_DEEPSLATE, 2);
      stonecutterResultFromBase(p_176532_, Blocks.DEEPSLATE_TILE_STAIRS, Blocks.COBBLED_DEEPSLATE);
      stonecutterResultFromBase(p_176532_, Blocks.DEEPSLATE_TILE_WALL, Blocks.COBBLED_DEEPSLATE);
      stonecutterResultFromBase(p_176532_, Blocks.POLISHED_DEEPSLATE_SLAB, Blocks.POLISHED_DEEPSLATE, 2);
      stonecutterResultFromBase(p_176532_, Blocks.POLISHED_DEEPSLATE_STAIRS, Blocks.POLISHED_DEEPSLATE);
      stonecutterResultFromBase(p_176532_, Blocks.POLISHED_DEEPSLATE_WALL, Blocks.POLISHED_DEEPSLATE);
      stonecutterResultFromBase(p_176532_, Blocks.DEEPSLATE_BRICKS, Blocks.POLISHED_DEEPSLATE);
      stonecutterResultFromBase(p_176532_, Blocks.DEEPSLATE_BRICK_SLAB, Blocks.POLISHED_DEEPSLATE, 2);
      stonecutterResultFromBase(p_176532_, Blocks.DEEPSLATE_BRICK_STAIRS, Blocks.POLISHED_DEEPSLATE);
      stonecutterResultFromBase(p_176532_, Blocks.DEEPSLATE_BRICK_WALL, Blocks.POLISHED_DEEPSLATE);
      stonecutterResultFromBase(p_176532_, Blocks.DEEPSLATE_TILES, Blocks.POLISHED_DEEPSLATE);
      stonecutterResultFromBase(p_176532_, Blocks.DEEPSLATE_TILE_SLAB, Blocks.POLISHED_DEEPSLATE, 2);
      stonecutterResultFromBase(p_176532_, Blocks.DEEPSLATE_TILE_STAIRS, Blocks.POLISHED_DEEPSLATE);
      stonecutterResultFromBase(p_176532_, Blocks.DEEPSLATE_TILE_WALL, Blocks.POLISHED_DEEPSLATE);
      stonecutterResultFromBase(p_176532_, Blocks.DEEPSLATE_BRICK_SLAB, Blocks.DEEPSLATE_BRICKS, 2);
      stonecutterResultFromBase(p_176532_, Blocks.DEEPSLATE_BRICK_STAIRS, Blocks.DEEPSLATE_BRICKS);
      stonecutterResultFromBase(p_176532_, Blocks.DEEPSLATE_BRICK_WALL, Blocks.DEEPSLATE_BRICKS);
      stonecutterResultFromBase(p_176532_, Blocks.DEEPSLATE_TILES, Blocks.DEEPSLATE_BRICKS);
      stonecutterResultFromBase(p_176532_, Blocks.DEEPSLATE_TILE_SLAB, Blocks.DEEPSLATE_BRICKS, 2);
      stonecutterResultFromBase(p_176532_, Blocks.DEEPSLATE_TILE_STAIRS, Blocks.DEEPSLATE_BRICKS);
      stonecutterResultFromBase(p_176532_, Blocks.DEEPSLATE_TILE_WALL, Blocks.DEEPSLATE_BRICKS);
      stonecutterResultFromBase(p_176532_, Blocks.DEEPSLATE_TILE_SLAB, Blocks.DEEPSLATE_TILES, 2);
      stonecutterResultFromBase(p_176532_, Blocks.DEEPSLATE_TILE_STAIRS, Blocks.DEEPSLATE_TILES);
      stonecutterResultFromBase(p_176532_, Blocks.DEEPSLATE_TILE_WALL, Blocks.DEEPSLATE_TILES);
      netheriteSmithing(p_176532_, Items.DIAMOND_CHESTPLATE, Items.NETHERITE_CHESTPLATE);
      netheriteSmithing(p_176532_, Items.DIAMOND_LEGGINGS, Items.NETHERITE_LEGGINGS);
      netheriteSmithing(p_176532_, Items.DIAMOND_HELMET, Items.NETHERITE_HELMET);
      netheriteSmithing(p_176532_, Items.DIAMOND_BOOTS, Items.NETHERITE_BOOTS);
      netheriteSmithing(p_176532_, Items.DIAMOND_SWORD, Items.NETHERITE_SWORD);
      netheriteSmithing(p_176532_, Items.DIAMOND_AXE, Items.NETHERITE_AXE);
      netheriteSmithing(p_176532_, Items.DIAMOND_PICKAXE, Items.NETHERITE_PICKAXE);
      netheriteSmithing(p_176532_, Items.DIAMOND_HOE, Items.NETHERITE_HOE);
      netheriteSmithing(p_176532_, Items.DIAMOND_SHOVEL, Items.NETHERITE_SHOVEL);
   }

   protected static void oneToOneConversionRecipe(Consumer<FinishedRecipe> p_176552_, ItemLike p_176553_, ItemLike p_176554_, @Nullable String p_176555_) {
      oneToOneConversionRecipe(p_176552_, p_176553_, p_176554_, p_176555_, 1);
   }

   protected static void oneToOneConversionRecipe(Consumer<FinishedRecipe> p_176557_, ItemLike p_176558_, ItemLike p_176559_, @Nullable String p_176560_, int p_176561_) {
      ShapelessRecipeBuilder.shapeless(p_176558_, p_176561_).requires(p_176559_).group(p_176560_).unlockedBy(getHasName(p_176559_), has(p_176559_)).save(p_176557_, getConversionRecipeName(p_176558_, p_176559_));
   }

   protected static void oreSmelting(Consumer<FinishedRecipe> p_176592_, List<ItemLike> p_176593_, ItemLike p_176594_, float p_176595_, int p_176596_, String p_176597_) {
      oreCooking(p_176592_, RecipeSerializer.SMELTING_RECIPE, p_176593_, p_176594_, p_176595_, p_176596_, p_176597_, "_from_smelting");
   }

   protected static void oreBlasting(Consumer<FinishedRecipe> p_176626_, List<ItemLike> p_176627_, ItemLike p_176628_, float p_176629_, int p_176630_, String p_176631_) {
      oreCooking(p_176626_, RecipeSerializer.BLASTING_RECIPE, p_176627_, p_176628_, p_176629_, p_176630_, p_176631_, "_from_blasting");
   }

   protected static void oreCooking(Consumer<FinishedRecipe> p_176534_, SimpleCookingSerializer<?> p_176535_, List<ItemLike> p_176536_, ItemLike p_176537_, float p_176538_, int p_176539_, String p_176540_, String p_176541_) {
      for(ItemLike itemlike : p_176536_) {
         SimpleCookingRecipeBuilder.cooking(Ingredient.of(itemlike), p_176537_, p_176538_, p_176539_, p_176535_).group(p_176540_).unlockedBy(getHasName(itemlike), has(itemlike)).save(p_176534_, getItemName(p_176537_) + p_176541_ + "_" + getItemName(itemlike));
      }

   }

   protected static void netheriteSmithing(Consumer<FinishedRecipe> p_125995_, Item p_125996_, Item p_125997_) {
      UpgradeRecipeBuilder.smithing(Ingredient.of(p_125996_), Ingredient.of(Items.NETHERITE_INGOT), p_125997_).unlocks("has_netherite_ingot", has(Items.NETHERITE_INGOT)).save(p_125995_, getItemName(p_125997_) + "_smithing");
   }

   protected static void planksFromLog(Consumer<FinishedRecipe> p_206409_, ItemLike p_206410_, TagKey<Item> p_206411_) {
      ShapelessRecipeBuilder.shapeless(p_206410_, 4).requires(p_206411_).group("planks").unlockedBy("has_log", has(p_206411_)).save(p_206409_);
   }

   protected static void planksFromLogs(Consumer<FinishedRecipe> p_206413_, ItemLike p_206414_, TagKey<Item> p_206415_) {
      ShapelessRecipeBuilder.shapeless(p_206414_, 4).requires(p_206415_).group("planks").unlockedBy("has_logs", has(p_206415_)).save(p_206413_);
   }

   protected static void woodFromLogs(Consumer<FinishedRecipe> p_126003_, ItemLike p_126004_, ItemLike p_126005_) {
      ShapedRecipeBuilder.shaped(p_126004_, 3).define('#', p_126005_).pattern("##").pattern("##").group("bark").unlockedBy("has_log", has(p_126005_)).save(p_126003_);
   }

   protected static void woodenBoat(Consumer<FinishedRecipe> p_126022_, ItemLike p_126023_, ItemLike p_126024_) {
      ShapedRecipeBuilder.shaped(p_126023_).define('#', p_126024_).pattern("# #").pattern("###").group("boat").unlockedBy("in_water", insideOf(Blocks.WATER)).save(p_126022_);
   }

   protected static RecipeBuilder buttonBuilder(ItemLike p_176659_, Ingredient p_176660_) {
      return ShapelessRecipeBuilder.shapeless(p_176659_).requires(p_176660_);
   }

   protected static RecipeBuilder doorBuilder(ItemLike p_176671_, Ingredient p_176672_) {
      return ShapedRecipeBuilder.shaped(p_176671_, 3).define('#', p_176672_).pattern("##").pattern("##").pattern("##");
   }

   protected static RecipeBuilder fenceBuilder(ItemLike p_176679_, Ingredient p_176680_) {
      int i = p_176679_ == Blocks.NETHER_BRICK_FENCE ? 6 : 3;
      Item item = p_176679_ == Blocks.NETHER_BRICK_FENCE ? Items.NETHER_BRICK : Items.STICK;
      return ShapedRecipeBuilder.shaped(p_176679_, i).define('W', p_176680_).define('#', item).pattern("W#W").pattern("W#W");
   }

   protected static RecipeBuilder fenceGateBuilder(ItemLike p_176685_, Ingredient p_176686_) {
      return ShapedRecipeBuilder.shaped(p_176685_).define('#', Items.STICK).define('W', p_176686_).pattern("#W#").pattern("#W#");
   }

   protected static void pressurePlate(Consumer<FinishedRecipe> p_176691_, ItemLike p_176692_, ItemLike p_176693_) {
      pressurePlateBuilder(p_176692_, Ingredient.of(p_176693_)).unlockedBy(getHasName(p_176693_), has(p_176693_)).save(p_176691_);
   }

   protected static RecipeBuilder pressurePlateBuilder(ItemLike p_176695_, Ingredient p_176696_) {
      return ShapedRecipeBuilder.shaped(p_176695_).define('#', p_176696_).pattern("##");
   }

   protected static void slab(Consumer<FinishedRecipe> p_176701_, ItemLike p_176702_, ItemLike p_176703_) {
      slabBuilder(p_176702_, Ingredient.of(p_176703_)).unlockedBy(getHasName(p_176703_), has(p_176703_)).save(p_176701_);
   }

   protected static RecipeBuilder slabBuilder(ItemLike p_176705_, Ingredient p_176706_) {
      return ShapedRecipeBuilder.shaped(p_176705_, 6).define('#', p_176706_).pattern("###");
   }

   protected static RecipeBuilder stairBuilder(ItemLike p_176711_, Ingredient p_176712_) {
      return ShapedRecipeBuilder.shaped(p_176711_, 4).define('#', p_176712_).pattern("#  ").pattern("## ").pattern("###");
   }

   protected static RecipeBuilder trapdoorBuilder(ItemLike p_176721_, Ingredient p_176722_) {
      return ShapedRecipeBuilder.shaped(p_176721_, 2).define('#', p_176722_).pattern("###").pattern("###");
   }

   protected static RecipeBuilder signBuilder(ItemLike p_176727_, Ingredient p_176728_) {
      return ShapedRecipeBuilder.shaped(p_176727_, 3).group("sign").define('#', p_176728_).define('X', Items.STICK).pattern("###").pattern("###").pattern(" X ");
   }

   protected static void coloredWoolFromWhiteWoolAndDye(Consumer<FinishedRecipe> p_126062_, ItemLike p_126063_, ItemLike p_126064_) {
      ShapelessRecipeBuilder.shapeless(p_126063_).requires(p_126064_).requires(Blocks.WHITE_WOOL).group("wool").unlockedBy("has_white_wool", has(Blocks.WHITE_WOOL)).save(p_126062_);
   }

   protected static void carpet(Consumer<FinishedRecipe> p_176717_, ItemLike p_176718_, ItemLike p_176719_) {
      ShapedRecipeBuilder.shaped(p_176718_, 3).define('#', p_176719_).pattern("##").group("carpet").unlockedBy(getHasName(p_176719_), has(p_176719_)).save(p_176717_);
   }

   protected static void coloredCarpetFromWhiteCarpetAndDye(Consumer<FinishedRecipe> p_126070_, ItemLike p_126071_, ItemLike p_126072_) {
      ShapedRecipeBuilder.shaped(p_126071_, 8).define('#', Blocks.WHITE_CARPET).define('$', p_126072_).pattern("###").pattern("#$#").pattern("###").group("carpet").unlockedBy("has_white_carpet", has(Blocks.WHITE_CARPET)).unlockedBy(getHasName(p_126072_), has(p_126072_)).save(p_126070_, getConversionRecipeName(p_126071_, Blocks.WHITE_CARPET));
   }

   protected static void bedFromPlanksAndWool(Consumer<FinishedRecipe> p_126074_, ItemLike p_126075_, ItemLike p_126076_) {
      ShapedRecipeBuilder.shaped(p_126075_).define('#', p_126076_).define('X', ItemTags.PLANKS).pattern("###").pattern("XXX").group("bed").unlockedBy(getHasName(p_126076_), has(p_126076_)).save(p_126074_);
   }

   protected static void bedFromWhiteBedAndDye(Consumer<FinishedRecipe> p_126078_, ItemLike p_126079_, ItemLike p_126080_) {
      ShapelessRecipeBuilder.shapeless(p_126079_).requires(Items.WHITE_BED).requires(p_126080_).group("dyed_bed").unlockedBy("has_bed", has(Items.WHITE_BED)).save(p_126078_, getConversionRecipeName(p_126079_, Items.WHITE_BED));
   }

   protected static void banner(Consumer<FinishedRecipe> p_126082_, ItemLike p_126083_, ItemLike p_126084_) {
      ShapedRecipeBuilder.shaped(p_126083_).define('#', p_126084_).define('|', Items.STICK).pattern("###").pattern("###").pattern(" | ").group("banner").unlockedBy(getHasName(p_126084_), has(p_126084_)).save(p_126082_);
   }

   protected static void stainedGlassFromGlassAndDye(Consumer<FinishedRecipe> p_126086_, ItemLike p_126087_, ItemLike p_126088_) {
      ShapedRecipeBuilder.shaped(p_126087_, 8).define('#', Blocks.GLASS).define('X', p_126088_).pattern("###").pattern("#X#").pattern("###").group("stained_glass").unlockedBy("has_glass", has(Blocks.GLASS)).save(p_126086_);
   }

   protected static void stainedGlassPaneFromStainedGlass(Consumer<FinishedRecipe> p_126090_, ItemLike p_126091_, ItemLike p_126092_) {
      ShapedRecipeBuilder.shaped(p_126091_, 16).define('#', p_126092_).pattern("###").pattern("###").group("stained_glass_pane").unlockedBy("has_glass", has(p_126092_)).save(p_126090_);
   }

   protected static void stainedGlassPaneFromGlassPaneAndDye(Consumer<FinishedRecipe> p_126094_, ItemLike p_126095_, ItemLike p_126096_) {
      ShapedRecipeBuilder.shaped(p_126095_, 8).define('#', Blocks.GLASS_PANE).define('$', p_126096_).pattern("###").pattern("#$#").pattern("###").group("stained_glass_pane").unlockedBy("has_glass_pane", has(Blocks.GLASS_PANE)).unlockedBy(getHasName(p_126096_), has(p_126096_)).save(p_126094_, getConversionRecipeName(p_126095_, Blocks.GLASS_PANE));
   }

   protected static void coloredTerracottaFromTerracottaAndDye(Consumer<FinishedRecipe> p_126098_, ItemLike p_126099_, ItemLike p_126100_) {
      ShapedRecipeBuilder.shaped(p_126099_, 8).define('#', Blocks.TERRACOTTA).define('X', p_126100_).pattern("###").pattern("#X#").pattern("###").group("stained_terracotta").unlockedBy("has_terracotta", has(Blocks.TERRACOTTA)).save(p_126098_);
   }

   protected static void concretePowder(Consumer<FinishedRecipe> p_126102_, ItemLike p_126103_, ItemLike p_126104_) {
      ShapelessRecipeBuilder.shapeless(p_126103_, 8).requires(p_126104_).requires(Blocks.SAND, 4).requires(Blocks.GRAVEL, 4).group("concrete_powder").unlockedBy("has_sand", has(Blocks.SAND)).unlockedBy("has_gravel", has(Blocks.GRAVEL)).save(p_126102_);
   }

   public static void candle(Consumer<FinishedRecipe> p_176543_, ItemLike p_176544_, ItemLike p_176545_) {
      ShapelessRecipeBuilder.shapeless(p_176544_).requires(Blocks.CANDLE).requires(p_176545_).group("dyed_candle").unlockedBy(getHasName(p_176545_), has(p_176545_)).save(p_176543_);
   }

   public static void wall(Consumer<FinishedRecipe> p_176613_, ItemLike p_176614_, ItemLike p_176615_) {
      wallBuilder(p_176614_, Ingredient.of(p_176615_)).unlockedBy(getHasName(p_176615_), has(p_176615_)).save(p_176613_);
   }

   public static RecipeBuilder wallBuilder(ItemLike p_176515_, Ingredient p_176516_) {
      return ShapedRecipeBuilder.shaped(p_176515_, 6).define('#', p_176516_).pattern("###").pattern("###");
   }

   public static void polished(Consumer<FinishedRecipe> p_176641_, ItemLike p_176642_, ItemLike p_176643_) {
      polishedBuilder(p_176642_, Ingredient.of(p_176643_)).unlockedBy(getHasName(p_176643_), has(p_176643_)).save(p_176641_);
   }

   public static RecipeBuilder polishedBuilder(ItemLike p_176605_, Ingredient p_176606_) {
      return ShapedRecipeBuilder.shaped(p_176605_, 4).define('S', p_176606_).pattern("SS").pattern("SS");
   }

   public static void cut(Consumer<FinishedRecipe> p_176653_, ItemLike p_176654_, ItemLike p_176655_) {
      cutBuilder(p_176654_, Ingredient.of(p_176655_)).unlockedBy(getHasName(p_176655_), has(p_176655_)).save(p_176653_);
   }

   public static ShapedRecipeBuilder cutBuilder(ItemLike p_176635_, Ingredient p_176636_) {
      return ShapedRecipeBuilder.shaped(p_176635_, 4).define('#', p_176636_).pattern("##").pattern("##");
   }

   public static void chiseled(Consumer<FinishedRecipe> p_176665_, ItemLike p_176666_, ItemLike p_176667_) {
      chiseledBuilder(p_176666_, Ingredient.of(p_176667_)).unlockedBy(getHasName(p_176667_), has(p_176667_)).save(p_176665_);
   }

   public static ShapedRecipeBuilder chiseledBuilder(ItemLike p_176647_, Ingredient p_176648_) {
      return ShapedRecipeBuilder.shaped(p_176647_).define('#', p_176648_).pattern("#").pattern("#");
   }

   protected static void stonecutterResultFromBase(Consumer<FinishedRecipe> p_176736_, ItemLike p_176737_, ItemLike p_176738_) {
      stonecutterResultFromBase(p_176736_, p_176737_, p_176738_, 1);
   }

   protected static void stonecutterResultFromBase(Consumer<FinishedRecipe> p_176547_, ItemLike p_176548_, ItemLike p_176549_, int p_176550_) {
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(p_176549_), p_176548_, p_176550_).unlockedBy(getHasName(p_176549_), has(p_176549_)).save(p_176547_, getConversionRecipeName(p_176548_, p_176549_) + "_stonecutting");
   }

   protected static void smeltingResultFromBase(Consumer<FinishedRecipe> p_176740_, ItemLike p_176741_, ItemLike p_176742_) {
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(p_176742_), p_176741_, 0.1F, 200).unlockedBy(getHasName(p_176742_), has(p_176742_)).save(p_176740_);
   }

   protected static void nineBlockStorageRecipes(Consumer<FinishedRecipe> p_176744_, ItemLike p_176745_, ItemLike p_176746_) {
      nineBlockStorageRecipes(p_176744_, p_176745_, p_176746_, getSimpleRecipeName(p_176746_), (String)null, getSimpleRecipeName(p_176745_), (String)null);
   }

   protected static void nineBlockStorageRecipesWithCustomPacking(Consumer<FinishedRecipe> p_176563_, ItemLike p_176564_, ItemLike p_176565_, String p_176566_, String p_176567_) {
      nineBlockStorageRecipes(p_176563_, p_176564_, p_176565_, p_176566_, p_176567_, getSimpleRecipeName(p_176564_), (String)null);
   }

   protected static void nineBlockStorageRecipesRecipesWithCustomUnpacking(Consumer<FinishedRecipe> p_176617_, ItemLike p_176618_, ItemLike p_176619_, String p_176620_, String p_176621_) {
      nineBlockStorageRecipes(p_176617_, p_176618_, p_176619_, getSimpleRecipeName(p_176619_), (String)null, p_176620_, p_176621_);
   }

   protected static void nineBlockStorageRecipes(Consumer<FinishedRecipe> p_176569_, ItemLike p_176570_, ItemLike p_176571_, String p_176572_, @Nullable String p_176573_, String p_176574_, @Nullable String p_176575_) {
      ShapelessRecipeBuilder.shapeless(p_176570_, 9).requires(p_176571_).group(p_176575_).unlockedBy(getHasName(p_176571_), has(p_176571_)).save(p_176569_, new ResourceLocation(p_176574_));
      ShapedRecipeBuilder.shaped(p_176571_).define('#', p_176570_).pattern("###").pattern("###").pattern("###").group(p_176573_).unlockedBy(getHasName(p_176570_), has(p_176570_)).save(p_176569_, new ResourceLocation(p_176572_));
   }

   protected static void cookRecipes(Consumer<FinishedRecipe> p_126007_, String p_126008_, SimpleCookingSerializer<?> p_126009_, int p_126010_) {
      simpleCookingRecipe(p_126007_, p_126008_, p_126009_, p_126010_, Items.BEEF, Items.COOKED_BEEF, 0.35F);
      simpleCookingRecipe(p_126007_, p_126008_, p_126009_, p_126010_, Items.CHICKEN, Items.COOKED_CHICKEN, 0.35F);
      simpleCookingRecipe(p_126007_, p_126008_, p_126009_, p_126010_, Items.COD, Items.COOKED_COD, 0.35F);
      simpleCookingRecipe(p_126007_, p_126008_, p_126009_, p_126010_, Items.KELP, Items.DRIED_KELP, 0.1F);
      simpleCookingRecipe(p_126007_, p_126008_, p_126009_, p_126010_, Items.SALMON, Items.COOKED_SALMON, 0.35F);
      simpleCookingRecipe(p_126007_, p_126008_, p_126009_, p_126010_, Items.MUTTON, Items.COOKED_MUTTON, 0.35F);
      simpleCookingRecipe(p_126007_, p_126008_, p_126009_, p_126010_, Items.PORKCHOP, Items.COOKED_PORKCHOP, 0.35F);
      simpleCookingRecipe(p_126007_, p_126008_, p_126009_, p_126010_, Items.POTATO, Items.BAKED_POTATO, 0.35F);
      simpleCookingRecipe(p_126007_, p_126008_, p_126009_, p_126010_, Items.RABBIT, Items.COOKED_RABBIT, 0.35F);
   }

   protected static void simpleCookingRecipe(Consumer<FinishedRecipe> p_176584_, String p_176585_, SimpleCookingSerializer<?> p_176586_, int p_176587_, ItemLike p_176588_, ItemLike p_176589_, float p_176590_) {
      SimpleCookingRecipeBuilder.cooking(Ingredient.of(p_176588_), p_176589_, p_176590_, p_176587_, p_176586_).unlockedBy(getHasName(p_176588_), has(p_176588_)).save(p_176584_, getItemName(p_176589_) + "_from_" + p_176585_);
   }

   protected static void waxRecipes(Consumer<FinishedRecipe> p_176611_) {
      HoneycombItem.WAXABLES.get().forEach((p_176578_, p_176579_) -> {
         ShapelessRecipeBuilder.shapeless(p_176579_).requires(p_176578_).requires(Items.HONEYCOMB).group(getItemName(p_176579_)).unlockedBy(getHasName(p_176578_), has(p_176578_)).save(p_176611_, getConversionRecipeName(p_176579_, Items.HONEYCOMB));
      });
   }

   protected static void generateRecipes(Consumer<FinishedRecipe> p_176581_, BlockFamily p_176582_) {
      p_176582_.getVariants().forEach((p_176529_, p_176530_) -> {
         BiFunction<ItemLike, ItemLike, RecipeBuilder> bifunction = shapeBuilders.get(p_176529_);
         ItemLike itemlike = getBaseBlock(p_176582_, p_176529_);
         if (bifunction != null) {
            RecipeBuilder recipebuilder = bifunction.apply(p_176530_, itemlike);
            p_176582_.getRecipeGroupPrefix().ifPresent((p_176601_) -> {
               recipebuilder.group(p_176601_ + (p_176529_ == BlockFamily.Variant.CUT ? "" : "_" + p_176529_.getName()));
            });
            recipebuilder.unlockedBy(p_176582_.getRecipeUnlockedBy().orElseGet(() -> {
               return getHasName(itemlike);
            }), has(itemlike));
            recipebuilder.save(p_176581_);
         }

         if (p_176529_ == BlockFamily.Variant.CRACKED) {
            smeltingResultFromBase(p_176581_, p_176530_, itemlike);
         }

      });
   }

   protected static Block getBaseBlock(BlockFamily p_176524_, BlockFamily.Variant p_176525_) {
      if (p_176525_ == BlockFamily.Variant.CHISELED) {
         if (!p_176524_.getVariants().containsKey(BlockFamily.Variant.SLAB)) {
            throw new IllegalStateException("Slab is not defined for the family.");
         } else {
            return p_176524_.get(BlockFamily.Variant.SLAB);
         }
      } else {
         return p_176524_.getBaseBlock();
      }
   }

   protected static EnterBlockTrigger.TriggerInstance insideOf(Block p_125980_) {
      return new EnterBlockTrigger.TriggerInstance(EntityPredicate.Composite.ANY, p_125980_, StatePropertiesPredicate.ANY);
   }

   protected static InventoryChangeTrigger.TriggerInstance has(MinMaxBounds.Ints p_176521_, ItemLike p_176522_) {
      return inventoryTrigger(ItemPredicate.Builder.item().of(p_176522_).withCount(p_176521_).build());
   }

   protected static InventoryChangeTrigger.TriggerInstance has(ItemLike p_125978_) {
      return inventoryTrigger(ItemPredicate.Builder.item().of(p_125978_).build());
   }

   protected static InventoryChangeTrigger.TriggerInstance has(TagKey<Item> p_206407_) {
      return inventoryTrigger(ItemPredicate.Builder.item().of(p_206407_).build());
   }

   protected static InventoryChangeTrigger.TriggerInstance inventoryTrigger(ItemPredicate... p_126012_) {
      return new InventoryChangeTrigger.TriggerInstance(EntityPredicate.Composite.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, p_126012_);
   }

   protected static String getHasName(ItemLike p_176603_) {
      return "has_" + getItemName(p_176603_);
   }

   protected static String getItemName(ItemLike p_176633_) {
      return Registry.ITEM.getKey(p_176633_.asItem()).getPath();
   }

   protected static String getSimpleRecipeName(ItemLike p_176645_) {
      return getItemName(p_176645_);
   }

   protected static String getConversionRecipeName(ItemLike p_176518_, ItemLike p_176519_) {
      return getItemName(p_176518_) + "_from_" + getItemName(p_176519_);
   }

   protected static String getSmeltingRecipeName(ItemLike p_176657_) {
      return getItemName(p_176657_) + "_from_smelting";
   }

   protected static String getBlastingRecipeName(ItemLike p_176669_) {
      return getItemName(p_176669_) + "_from_blasting";
   }

   public String getName() {
      return "Recipes";
   }
}
