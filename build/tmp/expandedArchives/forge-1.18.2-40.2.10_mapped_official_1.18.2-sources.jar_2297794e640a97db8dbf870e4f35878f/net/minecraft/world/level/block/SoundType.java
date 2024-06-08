package net.minecraft.world.level.block;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

public class SoundType {
   public static final SoundType WOOD = new SoundType(1.0F, 1.0F, SoundEvents.WOOD_BREAK, SoundEvents.WOOD_STEP, SoundEvents.WOOD_PLACE, SoundEvents.WOOD_HIT, SoundEvents.WOOD_FALL);
   public static final SoundType GRAVEL = new SoundType(1.0F, 1.0F, SoundEvents.GRAVEL_BREAK, SoundEvents.GRAVEL_STEP, SoundEvents.GRAVEL_PLACE, SoundEvents.GRAVEL_HIT, SoundEvents.GRAVEL_FALL);
   public static final SoundType GRASS = new SoundType(1.0F, 1.0F, SoundEvents.GRASS_BREAK, SoundEvents.GRASS_STEP, SoundEvents.GRASS_PLACE, SoundEvents.GRASS_HIT, SoundEvents.GRASS_FALL);
   public static final SoundType LILY_PAD = new SoundType(1.0F, 1.0F, SoundEvents.GRASS_BREAK, SoundEvents.GRASS_STEP, SoundEvents.LILY_PAD_PLACE, SoundEvents.GRASS_HIT, SoundEvents.GRASS_FALL);
   public static final SoundType STONE = new SoundType(1.0F, 1.0F, SoundEvents.STONE_BREAK, SoundEvents.STONE_STEP, SoundEvents.STONE_PLACE, SoundEvents.STONE_HIT, SoundEvents.STONE_FALL);
   public static final SoundType METAL = new SoundType(1.0F, 1.5F, SoundEvents.METAL_BREAK, SoundEvents.METAL_STEP, SoundEvents.METAL_PLACE, SoundEvents.METAL_HIT, SoundEvents.METAL_FALL);
   public static final SoundType GLASS = new SoundType(1.0F, 1.0F, SoundEvents.GLASS_BREAK, SoundEvents.GLASS_STEP, SoundEvents.GLASS_PLACE, SoundEvents.GLASS_HIT, SoundEvents.GLASS_FALL);
   public static final SoundType WOOL = new SoundType(1.0F, 1.0F, SoundEvents.WOOL_BREAK, SoundEvents.WOOL_STEP, SoundEvents.WOOL_PLACE, SoundEvents.WOOL_HIT, SoundEvents.WOOL_FALL);
   public static final SoundType SAND = new SoundType(1.0F, 1.0F, SoundEvents.SAND_BREAK, SoundEvents.SAND_STEP, SoundEvents.SAND_PLACE, SoundEvents.SAND_HIT, SoundEvents.SAND_FALL);
   public static final SoundType SNOW = new SoundType(1.0F, 1.0F, SoundEvents.SNOW_BREAK, SoundEvents.SNOW_STEP, SoundEvents.SNOW_PLACE, SoundEvents.SNOW_HIT, SoundEvents.SNOW_FALL);
   public static final SoundType POWDER_SNOW = new SoundType(1.0F, 1.0F, SoundEvents.POWDER_SNOW_BREAK, SoundEvents.POWDER_SNOW_STEP, SoundEvents.POWDER_SNOW_PLACE, SoundEvents.POWDER_SNOW_HIT, SoundEvents.POWDER_SNOW_FALL);
   public static final SoundType LADDER = new SoundType(1.0F, 1.0F, SoundEvents.LADDER_BREAK, SoundEvents.LADDER_STEP, SoundEvents.LADDER_PLACE, SoundEvents.LADDER_HIT, SoundEvents.LADDER_FALL);
   public static final SoundType ANVIL = new SoundType(0.3F, 1.0F, SoundEvents.ANVIL_BREAK, SoundEvents.ANVIL_STEP, SoundEvents.ANVIL_PLACE, SoundEvents.ANVIL_HIT, SoundEvents.ANVIL_FALL);
   public static final SoundType SLIME_BLOCK = new SoundType(1.0F, 1.0F, SoundEvents.SLIME_BLOCK_BREAK, SoundEvents.SLIME_BLOCK_STEP, SoundEvents.SLIME_BLOCK_PLACE, SoundEvents.SLIME_BLOCK_HIT, SoundEvents.SLIME_BLOCK_FALL);
   public static final SoundType HONEY_BLOCK = new SoundType(1.0F, 1.0F, SoundEvents.HONEY_BLOCK_BREAK, SoundEvents.HONEY_BLOCK_STEP, SoundEvents.HONEY_BLOCK_PLACE, SoundEvents.HONEY_BLOCK_HIT, SoundEvents.HONEY_BLOCK_FALL);
   public static final SoundType WET_GRASS = new SoundType(1.0F, 1.0F, SoundEvents.WET_GRASS_BREAK, SoundEvents.WET_GRASS_STEP, SoundEvents.WET_GRASS_PLACE, SoundEvents.WET_GRASS_HIT, SoundEvents.WET_GRASS_FALL);
   public static final SoundType CORAL_BLOCK = new SoundType(1.0F, 1.0F, SoundEvents.CORAL_BLOCK_BREAK, SoundEvents.CORAL_BLOCK_STEP, SoundEvents.CORAL_BLOCK_PLACE, SoundEvents.CORAL_BLOCK_HIT, SoundEvents.CORAL_BLOCK_FALL);
   public static final SoundType BAMBOO = new SoundType(1.0F, 1.0F, SoundEvents.BAMBOO_BREAK, SoundEvents.BAMBOO_STEP, SoundEvents.BAMBOO_PLACE, SoundEvents.BAMBOO_HIT, SoundEvents.BAMBOO_FALL);
   public static final SoundType BAMBOO_SAPLING = new SoundType(1.0F, 1.0F, SoundEvents.BAMBOO_SAPLING_BREAK, SoundEvents.BAMBOO_STEP, SoundEvents.BAMBOO_SAPLING_PLACE, SoundEvents.BAMBOO_SAPLING_HIT, SoundEvents.BAMBOO_FALL);
   public static final SoundType SCAFFOLDING = new SoundType(1.0F, 1.0F, SoundEvents.SCAFFOLDING_BREAK, SoundEvents.SCAFFOLDING_STEP, SoundEvents.SCAFFOLDING_PLACE, SoundEvents.SCAFFOLDING_HIT, SoundEvents.SCAFFOLDING_FALL);
   public static final SoundType SWEET_BERRY_BUSH = new SoundType(1.0F, 1.0F, SoundEvents.SWEET_BERRY_BUSH_BREAK, SoundEvents.GRASS_STEP, SoundEvents.SWEET_BERRY_BUSH_PLACE, SoundEvents.GRASS_HIT, SoundEvents.GRASS_FALL);
   public static final SoundType CROP = new SoundType(1.0F, 1.0F, SoundEvents.CROP_BREAK, SoundEvents.GRASS_STEP, SoundEvents.CROP_PLANTED, SoundEvents.GRASS_HIT, SoundEvents.GRASS_FALL);
   public static final SoundType HARD_CROP = new SoundType(1.0F, 1.0F, SoundEvents.WOOD_BREAK, SoundEvents.WOOD_STEP, SoundEvents.CROP_PLANTED, SoundEvents.WOOD_HIT, SoundEvents.WOOD_FALL);
   public static final SoundType VINE = new SoundType(1.0F, 1.0F, SoundEvents.VINE_BREAK, SoundEvents.VINE_STEP, SoundEvents.VINE_PLACE, SoundEvents.VINE_HIT, SoundEvents.VINE_FALL);
   public static final SoundType NETHER_WART = new SoundType(1.0F, 1.0F, SoundEvents.NETHER_WART_BREAK, SoundEvents.STONE_STEP, SoundEvents.NETHER_WART_PLANTED, SoundEvents.STONE_HIT, SoundEvents.STONE_FALL);
   public static final SoundType LANTERN = new SoundType(1.0F, 1.0F, SoundEvents.LANTERN_BREAK, SoundEvents.LANTERN_STEP, SoundEvents.LANTERN_PLACE, SoundEvents.LANTERN_HIT, SoundEvents.LANTERN_FALL);
   public static final SoundType STEM = new SoundType(1.0F, 1.0F, SoundEvents.STEM_BREAK, SoundEvents.STEM_STEP, SoundEvents.STEM_PLACE, SoundEvents.STEM_HIT, SoundEvents.STEM_FALL);
   public static final SoundType NYLIUM = new SoundType(1.0F, 1.0F, SoundEvents.NYLIUM_BREAK, SoundEvents.NYLIUM_STEP, SoundEvents.NYLIUM_PLACE, SoundEvents.NYLIUM_HIT, SoundEvents.NYLIUM_FALL);
   public static final SoundType FUNGUS = new SoundType(1.0F, 1.0F, SoundEvents.FUNGUS_BREAK, SoundEvents.FUNGUS_STEP, SoundEvents.FUNGUS_PLACE, SoundEvents.FUNGUS_HIT, SoundEvents.FUNGUS_FALL);
   public static final SoundType ROOTS = new SoundType(1.0F, 1.0F, SoundEvents.ROOTS_BREAK, SoundEvents.ROOTS_STEP, SoundEvents.ROOTS_PLACE, SoundEvents.ROOTS_HIT, SoundEvents.ROOTS_FALL);
   public static final SoundType SHROOMLIGHT = new SoundType(1.0F, 1.0F, SoundEvents.SHROOMLIGHT_BREAK, SoundEvents.SHROOMLIGHT_STEP, SoundEvents.SHROOMLIGHT_PLACE, SoundEvents.SHROOMLIGHT_HIT, SoundEvents.SHROOMLIGHT_FALL);
   public static final SoundType WEEPING_VINES = new SoundType(1.0F, 1.0F, SoundEvents.WEEPING_VINES_BREAK, SoundEvents.WEEPING_VINES_STEP, SoundEvents.WEEPING_VINES_PLACE, SoundEvents.WEEPING_VINES_HIT, SoundEvents.WEEPING_VINES_FALL);
   public static final SoundType TWISTING_VINES = new SoundType(1.0F, 0.5F, SoundEvents.WEEPING_VINES_BREAK, SoundEvents.WEEPING_VINES_STEP, SoundEvents.WEEPING_VINES_PLACE, SoundEvents.WEEPING_VINES_HIT, SoundEvents.WEEPING_VINES_FALL);
   public static final SoundType SOUL_SAND = new SoundType(1.0F, 1.0F, SoundEvents.SOUL_SAND_BREAK, SoundEvents.SOUL_SAND_STEP, SoundEvents.SOUL_SAND_PLACE, SoundEvents.SOUL_SAND_HIT, SoundEvents.SOUL_SAND_FALL);
   public static final SoundType SOUL_SOIL = new SoundType(1.0F, 1.0F, SoundEvents.SOUL_SOIL_BREAK, SoundEvents.SOUL_SOIL_STEP, SoundEvents.SOUL_SOIL_PLACE, SoundEvents.SOUL_SOIL_HIT, SoundEvents.SOUL_SOIL_FALL);
   public static final SoundType BASALT = new SoundType(1.0F, 1.0F, SoundEvents.BASALT_BREAK, SoundEvents.BASALT_STEP, SoundEvents.BASALT_PLACE, SoundEvents.BASALT_HIT, SoundEvents.BASALT_FALL);
   public static final SoundType WART_BLOCK = new SoundType(1.0F, 1.0F, SoundEvents.WART_BLOCK_BREAK, SoundEvents.WART_BLOCK_STEP, SoundEvents.WART_BLOCK_PLACE, SoundEvents.WART_BLOCK_HIT, SoundEvents.WART_BLOCK_FALL);
   public static final SoundType NETHERRACK = new SoundType(1.0F, 1.0F, SoundEvents.NETHERRACK_BREAK, SoundEvents.NETHERRACK_STEP, SoundEvents.NETHERRACK_PLACE, SoundEvents.NETHERRACK_HIT, SoundEvents.NETHERRACK_FALL);
   public static final SoundType NETHER_BRICKS = new SoundType(1.0F, 1.0F, SoundEvents.NETHER_BRICKS_BREAK, SoundEvents.NETHER_BRICKS_STEP, SoundEvents.NETHER_BRICKS_PLACE, SoundEvents.NETHER_BRICKS_HIT, SoundEvents.NETHER_BRICKS_FALL);
   public static final SoundType NETHER_SPROUTS = new SoundType(1.0F, 1.0F, SoundEvents.NETHER_SPROUTS_BREAK, SoundEvents.NETHER_SPROUTS_STEP, SoundEvents.NETHER_SPROUTS_PLACE, SoundEvents.NETHER_SPROUTS_HIT, SoundEvents.NETHER_SPROUTS_FALL);
   public static final SoundType NETHER_ORE = new SoundType(1.0F, 1.0F, SoundEvents.NETHER_ORE_BREAK, SoundEvents.NETHER_ORE_STEP, SoundEvents.NETHER_ORE_PLACE, SoundEvents.NETHER_ORE_HIT, SoundEvents.NETHER_ORE_FALL);
   public static final SoundType BONE_BLOCK = new SoundType(1.0F, 1.0F, SoundEvents.BONE_BLOCK_BREAK, SoundEvents.BONE_BLOCK_STEP, SoundEvents.BONE_BLOCK_PLACE, SoundEvents.BONE_BLOCK_HIT, SoundEvents.BONE_BLOCK_FALL);
   public static final SoundType NETHERITE_BLOCK = new SoundType(1.0F, 1.0F, SoundEvents.NETHERITE_BLOCK_BREAK, SoundEvents.NETHERITE_BLOCK_STEP, SoundEvents.NETHERITE_BLOCK_PLACE, SoundEvents.NETHERITE_BLOCK_HIT, SoundEvents.NETHERITE_BLOCK_FALL);
   public static final SoundType ANCIENT_DEBRIS = new SoundType(1.0F, 1.0F, SoundEvents.ANCIENT_DEBRIS_BREAK, SoundEvents.ANCIENT_DEBRIS_STEP, SoundEvents.ANCIENT_DEBRIS_PLACE, SoundEvents.ANCIENT_DEBRIS_HIT, SoundEvents.ANCIENT_DEBRIS_FALL);
   public static final SoundType LODESTONE = new SoundType(1.0F, 1.0F, SoundEvents.LODESTONE_BREAK, SoundEvents.LODESTONE_STEP, SoundEvents.LODESTONE_PLACE, SoundEvents.LODESTONE_HIT, SoundEvents.LODESTONE_FALL);
   public static final SoundType CHAIN = new SoundType(1.0F, 1.0F, SoundEvents.CHAIN_BREAK, SoundEvents.CHAIN_STEP, SoundEvents.CHAIN_PLACE, SoundEvents.CHAIN_HIT, SoundEvents.CHAIN_FALL);
   public static final SoundType NETHER_GOLD_ORE = new SoundType(1.0F, 1.0F, SoundEvents.NETHER_GOLD_ORE_BREAK, SoundEvents.NETHER_GOLD_ORE_STEP, SoundEvents.NETHER_GOLD_ORE_PLACE, SoundEvents.NETHER_GOLD_ORE_HIT, SoundEvents.NETHER_GOLD_ORE_FALL);
   public static final SoundType GILDED_BLACKSTONE = new SoundType(1.0F, 1.0F, SoundEvents.GILDED_BLACKSTONE_BREAK, SoundEvents.GILDED_BLACKSTONE_STEP, SoundEvents.GILDED_BLACKSTONE_PLACE, SoundEvents.GILDED_BLACKSTONE_HIT, SoundEvents.GILDED_BLACKSTONE_FALL);
   public static final SoundType CANDLE = new SoundType(1.0F, 1.0F, SoundEvents.CANDLE_BREAK, SoundEvents.CANDLE_STEP, SoundEvents.CANDLE_PLACE, SoundEvents.CANDLE_HIT, SoundEvents.CANDLE_FALL);
   public static final SoundType AMETHYST = new SoundType(1.0F, 1.0F, SoundEvents.AMETHYST_BLOCK_BREAK, SoundEvents.AMETHYST_BLOCK_STEP, SoundEvents.AMETHYST_BLOCK_PLACE, SoundEvents.AMETHYST_BLOCK_HIT, SoundEvents.AMETHYST_BLOCK_FALL);
   public static final SoundType AMETHYST_CLUSTER = new SoundType(1.0F, 1.0F, SoundEvents.AMETHYST_CLUSTER_BREAK, SoundEvents.AMETHYST_CLUSTER_STEP, SoundEvents.AMETHYST_CLUSTER_PLACE, SoundEvents.AMETHYST_CLUSTER_HIT, SoundEvents.AMETHYST_CLUSTER_FALL);
   public static final SoundType SMALL_AMETHYST_BUD = new SoundType(1.0F, 1.0F, SoundEvents.SMALL_AMETHYST_BUD_BREAK, SoundEvents.AMETHYST_CLUSTER_STEP, SoundEvents.SMALL_AMETHYST_BUD_PLACE, SoundEvents.AMETHYST_CLUSTER_HIT, SoundEvents.AMETHYST_CLUSTER_FALL);
   public static final SoundType MEDIUM_AMETHYST_BUD = new SoundType(1.0F, 1.0F, SoundEvents.MEDIUM_AMETHYST_BUD_BREAK, SoundEvents.AMETHYST_CLUSTER_STEP, SoundEvents.MEDIUM_AMETHYST_BUD_PLACE, SoundEvents.AMETHYST_CLUSTER_HIT, SoundEvents.AMETHYST_CLUSTER_FALL);
   public static final SoundType LARGE_AMETHYST_BUD = new SoundType(1.0F, 1.0F, SoundEvents.LARGE_AMETHYST_BUD_BREAK, SoundEvents.AMETHYST_CLUSTER_STEP, SoundEvents.LARGE_AMETHYST_BUD_PLACE, SoundEvents.AMETHYST_CLUSTER_HIT, SoundEvents.AMETHYST_CLUSTER_FALL);
   public static final SoundType TUFF = new SoundType(1.0F, 1.0F, SoundEvents.TUFF_BREAK, SoundEvents.TUFF_STEP, SoundEvents.TUFF_PLACE, SoundEvents.TUFF_HIT, SoundEvents.TUFF_FALL);
   public static final SoundType CALCITE = new SoundType(1.0F, 1.0F, SoundEvents.CALCITE_BREAK, SoundEvents.CALCITE_STEP, SoundEvents.CALCITE_PLACE, SoundEvents.CALCITE_HIT, SoundEvents.CALCITE_FALL);
   public static final SoundType DRIPSTONE_BLOCK = new SoundType(1.0F, 1.0F, SoundEvents.DRIPSTONE_BLOCK_BREAK, SoundEvents.DRIPSTONE_BLOCK_STEP, SoundEvents.DRIPSTONE_BLOCK_PLACE, SoundEvents.DRIPSTONE_BLOCK_HIT, SoundEvents.DRIPSTONE_BLOCK_FALL);
   public static final SoundType POINTED_DRIPSTONE = new SoundType(1.0F, 1.0F, SoundEvents.POINTED_DRIPSTONE_BREAK, SoundEvents.POINTED_DRIPSTONE_STEP, SoundEvents.POINTED_DRIPSTONE_PLACE, SoundEvents.POINTED_DRIPSTONE_HIT, SoundEvents.POINTED_DRIPSTONE_FALL);
   public static final SoundType COPPER = new SoundType(1.0F, 1.0F, SoundEvents.COPPER_BREAK, SoundEvents.COPPER_STEP, SoundEvents.COPPER_PLACE, SoundEvents.COPPER_HIT, SoundEvents.COPPER_FALL);
   public static final SoundType CAVE_VINES = new SoundType(1.0F, 1.0F, SoundEvents.CAVE_VINES_BREAK, SoundEvents.CAVE_VINES_STEP, SoundEvents.CAVE_VINES_PLACE, SoundEvents.CAVE_VINES_HIT, SoundEvents.CAVE_VINES_FALL);
   public static final SoundType SPORE_BLOSSOM = new SoundType(1.0F, 1.0F, SoundEvents.SPORE_BLOSSOM_BREAK, SoundEvents.SPORE_BLOSSOM_STEP, SoundEvents.SPORE_BLOSSOM_PLACE, SoundEvents.SPORE_BLOSSOM_HIT, SoundEvents.SPORE_BLOSSOM_FALL);
   public static final SoundType AZALEA = new SoundType(1.0F, 1.0F, SoundEvents.AZALEA_BREAK, SoundEvents.AZALEA_STEP, SoundEvents.AZALEA_PLACE, SoundEvents.AZALEA_HIT, SoundEvents.AZALEA_FALL);
   public static final SoundType FLOWERING_AZALEA = new SoundType(1.0F, 1.0F, SoundEvents.FLOWERING_AZALEA_BREAK, SoundEvents.FLOWERING_AZALEA_STEP, SoundEvents.FLOWERING_AZALEA_PLACE, SoundEvents.FLOWERING_AZALEA_HIT, SoundEvents.FLOWERING_AZALEA_FALL);
   public static final SoundType MOSS_CARPET = new SoundType(1.0F, 1.0F, SoundEvents.MOSS_CARPET_BREAK, SoundEvents.MOSS_CARPET_STEP, SoundEvents.MOSS_CARPET_PLACE, SoundEvents.MOSS_CARPET_HIT, SoundEvents.MOSS_CARPET_FALL);
   public static final SoundType MOSS = new SoundType(1.0F, 1.0F, SoundEvents.MOSS_BREAK, SoundEvents.MOSS_STEP, SoundEvents.MOSS_PLACE, SoundEvents.MOSS_HIT, SoundEvents.MOSS_FALL);
   public static final SoundType BIG_DRIPLEAF = new SoundType(1.0F, 1.0F, SoundEvents.BIG_DRIPLEAF_BREAK, SoundEvents.BIG_DRIPLEAF_STEP, SoundEvents.BIG_DRIPLEAF_PLACE, SoundEvents.BIG_DRIPLEAF_HIT, SoundEvents.BIG_DRIPLEAF_FALL);
   public static final SoundType SMALL_DRIPLEAF = new SoundType(1.0F, 1.0F, SoundEvents.SMALL_DRIPLEAF_BREAK, SoundEvents.SMALL_DRIPLEAF_STEP, SoundEvents.SMALL_DRIPLEAF_PLACE, SoundEvents.SMALL_DRIPLEAF_HIT, SoundEvents.SMALL_DRIPLEAF_FALL);
   public static final SoundType ROOTED_DIRT = new SoundType(1.0F, 1.0F, SoundEvents.ROOTED_DIRT_BREAK, SoundEvents.ROOTED_DIRT_STEP, SoundEvents.ROOTED_DIRT_PLACE, SoundEvents.ROOTED_DIRT_HIT, SoundEvents.ROOTED_DIRT_FALL);
   public static final SoundType HANGING_ROOTS = new SoundType(1.0F, 1.0F, SoundEvents.HANGING_ROOTS_BREAK, SoundEvents.HANGING_ROOTS_STEP, SoundEvents.HANGING_ROOTS_PLACE, SoundEvents.HANGING_ROOTS_HIT, SoundEvents.HANGING_ROOTS_FALL);
   public static final SoundType AZALEA_LEAVES = new SoundType(1.0F, 1.0F, SoundEvents.AZALEA_LEAVES_BREAK, SoundEvents.AZALEA_LEAVES_STEP, SoundEvents.AZALEA_LEAVES_PLACE, SoundEvents.AZALEA_LEAVES_HIT, SoundEvents.AZALEA_LEAVES_FALL);
   public static final SoundType SCULK_SENSOR = new SoundType(1.0F, 1.0F, SoundEvents.SCULK_SENSOR_BREAK, SoundEvents.SCULK_SENSOR_STEP, SoundEvents.SCULK_SENSOR_PLACE, SoundEvents.SCULK_SENSOR_HIT, SoundEvents.SCULK_SENSOR_FALL);
   public static final SoundType GLOW_LICHEN = new SoundType(1.0F, 1.0F, SoundEvents.GRASS_BREAK, SoundEvents.VINE_STEP, SoundEvents.GRASS_PLACE, SoundEvents.GRASS_HIT, SoundEvents.GRASS_FALL);
   public static final SoundType DEEPSLATE = new SoundType(1.0F, 1.0F, SoundEvents.DEEPSLATE_BREAK, SoundEvents.DEEPSLATE_STEP, SoundEvents.DEEPSLATE_PLACE, SoundEvents.DEEPSLATE_HIT, SoundEvents.DEEPSLATE_FALL);
   public static final SoundType DEEPSLATE_BRICKS = new SoundType(1.0F, 1.0F, SoundEvents.DEEPSLATE_BRICKS_BREAK, SoundEvents.DEEPSLATE_BRICKS_STEP, SoundEvents.DEEPSLATE_BRICKS_PLACE, SoundEvents.DEEPSLATE_BRICKS_HIT, SoundEvents.DEEPSLATE_BRICKS_FALL);
   public static final SoundType DEEPSLATE_TILES = new SoundType(1.0F, 1.0F, SoundEvents.DEEPSLATE_TILES_BREAK, SoundEvents.DEEPSLATE_TILES_STEP, SoundEvents.DEEPSLATE_TILES_PLACE, SoundEvents.DEEPSLATE_TILES_HIT, SoundEvents.DEEPSLATE_TILES_FALL);
   public static final SoundType POLISHED_DEEPSLATE = new SoundType(1.0F, 1.0F, SoundEvents.POLISHED_DEEPSLATE_BREAK, SoundEvents.POLISHED_DEEPSLATE_STEP, SoundEvents.POLISHED_DEEPSLATE_PLACE, SoundEvents.POLISHED_DEEPSLATE_HIT, SoundEvents.POLISHED_DEEPSLATE_FALL);
   public final float volume;
   public final float pitch;
   private final SoundEvent breakSound;
   private final SoundEvent stepSound;
   private final SoundEvent placeSound;
   private final SoundEvent hitSound;
   private final SoundEvent fallSound;

   @Deprecated // Forge: Use {@link net.minecraftforge.common.util.ForgeSoundType} instead for suppliers
   public SoundType(float p_56766_, float p_56767_, SoundEvent p_56768_, SoundEvent p_56769_, SoundEvent p_56770_, SoundEvent p_56771_, SoundEvent p_56772_) {
      this.volume = p_56766_;
      this.pitch = p_56767_;
      this.breakSound = p_56768_;
      this.stepSound = p_56769_;
      this.placeSound = p_56770_;
      this.hitSound = p_56771_;
      this.fallSound = p_56772_;
   }

   public float getVolume() {
      return this.volume;
   }

   public float getPitch() {
      return this.pitch;
   }

   public SoundEvent getBreakSound() {
      return this.breakSound;
   }

   public SoundEvent getStepSound() {
      return this.stepSound;
   }

   public SoundEvent getPlaceSound() {
      return this.placeSound;
   }

   public SoundEvent getHitSound() {
      return this.hitSound;
   }

   public SoundEvent getFallSound() {
      return this.fallSound;
   }
}
