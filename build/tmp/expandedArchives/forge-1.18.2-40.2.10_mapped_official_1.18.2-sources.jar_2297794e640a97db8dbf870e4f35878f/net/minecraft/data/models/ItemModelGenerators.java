package net.minecraft.data.models;

import com.google.gson.JsonElement;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class ItemModelGenerators {
   private final BiConsumer<ResourceLocation, Supplier<JsonElement>> output;

   public ItemModelGenerators(BiConsumer<ResourceLocation, Supplier<JsonElement>> p_125082_) {
      this.output = p_125082_;
   }

   private void generateFlatItem(Item p_125089_, ModelTemplate p_125090_) {
      p_125090_.create(ModelLocationUtils.getModelLocation(p_125089_), TextureMapping.layer0(p_125089_), this.output);
   }

   private void generateFlatItem(Item p_125092_, String p_125093_, ModelTemplate p_125094_) {
      p_125094_.create(ModelLocationUtils.getModelLocation(p_125092_, p_125093_), TextureMapping.layer0(TextureMapping.getItemTexture(p_125092_, p_125093_)), this.output);
   }

   private void generateFlatItem(Item p_125085_, Item p_125086_, ModelTemplate p_125087_) {
      p_125087_.create(ModelLocationUtils.getModelLocation(p_125085_), TextureMapping.layer0(p_125086_), this.output);
   }

   public void run() {
      this.generateFlatItem(Items.ACACIA_BOAT, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.AMETHYST_SHARD, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.APPLE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.ARMOR_STAND, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.ARROW, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.BAKED_POTATO, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.BAMBOO, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.BEEF, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.BEETROOT, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.BEETROOT_SOUP, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.BIRCH_BOAT, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.BLACK_DYE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.BLAZE_POWDER, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.BLAZE_ROD, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.BLUE_DYE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.BONE_MEAL, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.BOOK, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.BOWL, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.BREAD, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.BRICK, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.BROWN_DYE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.BUCKET, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.CARROT_ON_A_STICK, ModelTemplates.FLAT_HANDHELD_ROD_ITEM);
      this.generateFlatItem(Items.WARPED_FUNGUS_ON_A_STICK, ModelTemplates.FLAT_HANDHELD_ROD_ITEM);
      this.generateFlatItem(Items.CHAINMAIL_BOOTS, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.CHAINMAIL_CHESTPLATE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.CHAINMAIL_HELMET, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.CHAINMAIL_LEGGINGS, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.CHARCOAL, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.CHEST_MINECART, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.CHICKEN, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.CHORUS_FRUIT, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.CLAY_BALL, ModelTemplates.FLAT_ITEM);

      for(int i = 1; i < 64; ++i) {
         this.generateFlatItem(Items.CLOCK, String.format("_%02d", i), ModelTemplates.FLAT_ITEM);
      }

      this.generateFlatItem(Items.COAL, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.COD_BUCKET, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.COMMAND_BLOCK_MINECART, ModelTemplates.FLAT_ITEM);

      for(int j = 0; j < 32; ++j) {
         if (j != 16) {
            this.generateFlatItem(Items.COMPASS, String.format("_%02d", j), ModelTemplates.FLAT_ITEM);
         }
      }

      this.generateFlatItem(Items.COOKED_BEEF, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.COOKED_CHICKEN, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.COOKED_COD, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.COOKED_MUTTON, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.COOKED_PORKCHOP, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.COOKED_RABBIT, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.COOKED_SALMON, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.COOKIE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.RAW_COPPER, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.COPPER_INGOT, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.CREEPER_BANNER_PATTERN, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.CYAN_DYE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.DARK_OAK_BOAT, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.DIAMOND, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.DIAMOND_AXE, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.DIAMOND_BOOTS, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.DIAMOND_CHESTPLATE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.DIAMOND_HELMET, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.DIAMOND_HOE, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.DIAMOND_HORSE_ARMOR, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.DIAMOND_LEGGINGS, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.DIAMOND_PICKAXE, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.DIAMOND_SHOVEL, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.DIAMOND_SWORD, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.DRAGON_BREATH, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.DRIED_KELP, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.EGG, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.EMERALD, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.ENCHANTED_BOOK, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.ENDER_EYE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.ENDER_PEARL, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.END_CRYSTAL, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.EXPERIENCE_BOTTLE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.FERMENTED_SPIDER_EYE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.FIREWORK_ROCKET, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.FIRE_CHARGE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.FLINT, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.FLINT_AND_STEEL, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.FLOWER_BANNER_PATTERN, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.FURNACE_MINECART, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.GHAST_TEAR, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.GLASS_BOTTLE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.GLISTERING_MELON_SLICE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.GLOBE_BANNER_PATTERN, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.GLOW_BERRIES, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.GLOWSTONE_DUST, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.GLOW_INK_SAC, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.GLOW_ITEM_FRAME, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.RAW_GOLD, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.GOLDEN_APPLE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.GOLDEN_AXE, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.GOLDEN_BOOTS, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.GOLDEN_CARROT, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.GOLDEN_CHESTPLATE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.GOLDEN_HELMET, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.GOLDEN_HOE, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.GOLDEN_HORSE_ARMOR, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.GOLDEN_LEGGINGS, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.GOLDEN_PICKAXE, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.GOLDEN_SHOVEL, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.GOLDEN_SWORD, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.GOLD_INGOT, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.GOLD_NUGGET, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.GRAY_DYE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.GREEN_DYE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.GUNPOWDER, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.HEART_OF_THE_SEA, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.HONEYCOMB, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.HONEY_BOTTLE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.HOPPER_MINECART, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.INK_SAC, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.RAW_IRON, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.IRON_AXE, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.IRON_BOOTS, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.IRON_CHESTPLATE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.IRON_HELMET, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.IRON_HOE, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.IRON_HORSE_ARMOR, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.IRON_INGOT, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.IRON_LEGGINGS, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.IRON_NUGGET, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.IRON_PICKAXE, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.IRON_SHOVEL, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.IRON_SWORD, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.ITEM_FRAME, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.JUNGLE_BOAT, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.KNOWLEDGE_BOOK, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.LAPIS_LAZULI, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.LAVA_BUCKET, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.LEATHER, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.LEATHER_HORSE_ARMOR, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.LIGHT_BLUE_DYE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.LIGHT_GRAY_DYE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.LIME_DYE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.MAGENTA_DYE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.MAGMA_CREAM, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.MAP, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.MELON_SLICE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.MILK_BUCKET, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.MINECART, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.MOJANG_BANNER_PATTERN, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.MUSHROOM_STEW, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.MUSIC_DISC_11, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.MUSIC_DISC_13, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.MUSIC_DISC_BLOCKS, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.MUSIC_DISC_CAT, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.MUSIC_DISC_CHIRP, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.MUSIC_DISC_FAR, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.MUSIC_DISC_MALL, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.MUSIC_DISC_MELLOHI, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.MUSIC_DISC_PIGSTEP, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.MUSIC_DISC_STAL, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.MUSIC_DISC_STRAD, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.MUSIC_DISC_WAIT, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.MUSIC_DISC_WARD, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.MUSIC_DISC_OTHERSIDE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.MUTTON, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.NAME_TAG, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.NAUTILUS_SHELL, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.NETHERITE_AXE, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.NETHERITE_BOOTS, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.NETHERITE_CHESTPLATE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.NETHERITE_HELMET, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.NETHERITE_HOE, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.NETHERITE_INGOT, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.NETHERITE_LEGGINGS, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.NETHERITE_PICKAXE, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.NETHERITE_SCRAP, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.NETHERITE_SHOVEL, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.NETHERITE_SWORD, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.NETHER_BRICK, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.NETHER_STAR, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.OAK_BOAT, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.ORANGE_DYE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.PAINTING, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.PAPER, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.PHANTOM_MEMBRANE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.PIGLIN_BANNER_PATTERN, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.PINK_DYE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.POISONOUS_POTATO, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.POPPED_CHORUS_FRUIT, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.PORKCHOP, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.POWDER_SNOW_BUCKET, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.PRISMARINE_CRYSTALS, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.PRISMARINE_SHARD, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.PUFFERFISH, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.PUFFERFISH_BUCKET, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.PUMPKIN_PIE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.PURPLE_DYE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.QUARTZ, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.RABBIT, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.RABBIT_FOOT, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.RABBIT_HIDE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.RABBIT_STEW, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.RED_DYE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.ROTTEN_FLESH, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.SADDLE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.SALMON, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.SALMON_BUCKET, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.SCUTE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.SHEARS, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.SHULKER_SHELL, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.SKULL_BANNER_PATTERN, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.SLIME_BALL, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.SNOWBALL, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.SPECTRAL_ARROW, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.SPIDER_EYE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.SPRUCE_BOAT, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.SPYGLASS, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.STICK, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.STONE_AXE, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.STONE_HOE, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.STONE_PICKAXE, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.STONE_SHOVEL, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.STONE_SWORD, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.SUGAR, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.SUSPICIOUS_STEW, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.TNT_MINECART, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.TOTEM_OF_UNDYING, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.TRIDENT, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.TROPICAL_FISH, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.TROPICAL_FISH_BUCKET, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.AXOLOTL_BUCKET, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.TURTLE_HELMET, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.WATER_BUCKET, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.WHEAT, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.WHITE_DYE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.WOODEN_AXE, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.WOODEN_HOE, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.WOODEN_PICKAXE, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.WOODEN_SHOVEL, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.WOODEN_SWORD, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.WRITABLE_BOOK, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.WRITTEN_BOOK, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.YELLOW_DYE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.DEBUG_STICK, Items.STICK, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.ENCHANTED_GOLDEN_APPLE, Items.GOLDEN_APPLE, ModelTemplates.FLAT_ITEM);
   }
}