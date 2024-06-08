package net.minecraft.client.renderer;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemBlockRenderTypes {
   @Deprecated
   private static final Map<Block, RenderType> TYPE_BY_BLOCK = Util.make(Maps.newHashMap(), (p_109296_) -> {
      RenderType rendertype = RenderType.tripwire();
      p_109296_.put(Blocks.TRIPWIRE, rendertype);
      RenderType rendertype1 = RenderType.cutoutMipped();
      p_109296_.put(Blocks.GRASS_BLOCK, rendertype1);
      p_109296_.put(Blocks.IRON_BARS, rendertype1);
      p_109296_.put(Blocks.GLASS_PANE, rendertype1);
      p_109296_.put(Blocks.TRIPWIRE_HOOK, rendertype1);
      p_109296_.put(Blocks.HOPPER, rendertype1);
      p_109296_.put(Blocks.CHAIN, rendertype1);
      p_109296_.put(Blocks.JUNGLE_LEAVES, rendertype1);
      p_109296_.put(Blocks.OAK_LEAVES, rendertype1);
      p_109296_.put(Blocks.SPRUCE_LEAVES, rendertype1);
      p_109296_.put(Blocks.ACACIA_LEAVES, rendertype1);
      p_109296_.put(Blocks.BIRCH_LEAVES, rendertype1);
      p_109296_.put(Blocks.DARK_OAK_LEAVES, rendertype1);
      p_109296_.put(Blocks.AZALEA_LEAVES, rendertype1);
      p_109296_.put(Blocks.FLOWERING_AZALEA_LEAVES, rendertype1);
      RenderType rendertype2 = RenderType.cutout();
      p_109296_.put(Blocks.OAK_SAPLING, rendertype2);
      p_109296_.put(Blocks.SPRUCE_SAPLING, rendertype2);
      p_109296_.put(Blocks.BIRCH_SAPLING, rendertype2);
      p_109296_.put(Blocks.JUNGLE_SAPLING, rendertype2);
      p_109296_.put(Blocks.ACACIA_SAPLING, rendertype2);
      p_109296_.put(Blocks.DARK_OAK_SAPLING, rendertype2);
      p_109296_.put(Blocks.GLASS, rendertype2);
      p_109296_.put(Blocks.WHITE_BED, rendertype2);
      p_109296_.put(Blocks.ORANGE_BED, rendertype2);
      p_109296_.put(Blocks.MAGENTA_BED, rendertype2);
      p_109296_.put(Blocks.LIGHT_BLUE_BED, rendertype2);
      p_109296_.put(Blocks.YELLOW_BED, rendertype2);
      p_109296_.put(Blocks.LIME_BED, rendertype2);
      p_109296_.put(Blocks.PINK_BED, rendertype2);
      p_109296_.put(Blocks.GRAY_BED, rendertype2);
      p_109296_.put(Blocks.LIGHT_GRAY_BED, rendertype2);
      p_109296_.put(Blocks.CYAN_BED, rendertype2);
      p_109296_.put(Blocks.PURPLE_BED, rendertype2);
      p_109296_.put(Blocks.BLUE_BED, rendertype2);
      p_109296_.put(Blocks.BROWN_BED, rendertype2);
      p_109296_.put(Blocks.GREEN_BED, rendertype2);
      p_109296_.put(Blocks.RED_BED, rendertype2);
      p_109296_.put(Blocks.BLACK_BED, rendertype2);
      p_109296_.put(Blocks.POWERED_RAIL, rendertype2);
      p_109296_.put(Blocks.DETECTOR_RAIL, rendertype2);
      p_109296_.put(Blocks.COBWEB, rendertype2);
      p_109296_.put(Blocks.GRASS, rendertype2);
      p_109296_.put(Blocks.FERN, rendertype2);
      p_109296_.put(Blocks.DEAD_BUSH, rendertype2);
      p_109296_.put(Blocks.SEAGRASS, rendertype2);
      p_109296_.put(Blocks.TALL_SEAGRASS, rendertype2);
      p_109296_.put(Blocks.DANDELION, rendertype2);
      p_109296_.put(Blocks.POPPY, rendertype2);
      p_109296_.put(Blocks.BLUE_ORCHID, rendertype2);
      p_109296_.put(Blocks.ALLIUM, rendertype2);
      p_109296_.put(Blocks.AZURE_BLUET, rendertype2);
      p_109296_.put(Blocks.RED_TULIP, rendertype2);
      p_109296_.put(Blocks.ORANGE_TULIP, rendertype2);
      p_109296_.put(Blocks.WHITE_TULIP, rendertype2);
      p_109296_.put(Blocks.PINK_TULIP, rendertype2);
      p_109296_.put(Blocks.OXEYE_DAISY, rendertype2);
      p_109296_.put(Blocks.CORNFLOWER, rendertype2);
      p_109296_.put(Blocks.WITHER_ROSE, rendertype2);
      p_109296_.put(Blocks.LILY_OF_THE_VALLEY, rendertype2);
      p_109296_.put(Blocks.BROWN_MUSHROOM, rendertype2);
      p_109296_.put(Blocks.RED_MUSHROOM, rendertype2);
      p_109296_.put(Blocks.TORCH, rendertype2);
      p_109296_.put(Blocks.WALL_TORCH, rendertype2);
      p_109296_.put(Blocks.SOUL_TORCH, rendertype2);
      p_109296_.put(Blocks.SOUL_WALL_TORCH, rendertype2);
      p_109296_.put(Blocks.FIRE, rendertype2);
      p_109296_.put(Blocks.SOUL_FIRE, rendertype2);
      p_109296_.put(Blocks.SPAWNER, rendertype2);
      p_109296_.put(Blocks.REDSTONE_WIRE, rendertype2);
      p_109296_.put(Blocks.WHEAT, rendertype2);
      p_109296_.put(Blocks.OAK_DOOR, rendertype2);
      p_109296_.put(Blocks.LADDER, rendertype2);
      p_109296_.put(Blocks.RAIL, rendertype2);
      p_109296_.put(Blocks.IRON_DOOR, rendertype2);
      p_109296_.put(Blocks.REDSTONE_TORCH, rendertype2);
      p_109296_.put(Blocks.REDSTONE_WALL_TORCH, rendertype2);
      p_109296_.put(Blocks.CACTUS, rendertype2);
      p_109296_.put(Blocks.SUGAR_CANE, rendertype2);
      p_109296_.put(Blocks.REPEATER, rendertype2);
      p_109296_.put(Blocks.OAK_TRAPDOOR, rendertype2);
      p_109296_.put(Blocks.SPRUCE_TRAPDOOR, rendertype2);
      p_109296_.put(Blocks.BIRCH_TRAPDOOR, rendertype2);
      p_109296_.put(Blocks.JUNGLE_TRAPDOOR, rendertype2);
      p_109296_.put(Blocks.ACACIA_TRAPDOOR, rendertype2);
      p_109296_.put(Blocks.DARK_OAK_TRAPDOOR, rendertype2);
      p_109296_.put(Blocks.CRIMSON_TRAPDOOR, rendertype2);
      p_109296_.put(Blocks.WARPED_TRAPDOOR, rendertype2);
      p_109296_.put(Blocks.ATTACHED_PUMPKIN_STEM, rendertype2);
      p_109296_.put(Blocks.ATTACHED_MELON_STEM, rendertype2);
      p_109296_.put(Blocks.PUMPKIN_STEM, rendertype2);
      p_109296_.put(Blocks.MELON_STEM, rendertype2);
      p_109296_.put(Blocks.VINE, rendertype2);
      p_109296_.put(Blocks.GLOW_LICHEN, rendertype2);
      p_109296_.put(Blocks.LILY_PAD, rendertype2);
      p_109296_.put(Blocks.NETHER_WART, rendertype2);
      p_109296_.put(Blocks.BREWING_STAND, rendertype2);
      p_109296_.put(Blocks.COCOA, rendertype2);
      p_109296_.put(Blocks.BEACON, rendertype2);
      p_109296_.put(Blocks.FLOWER_POT, rendertype2);
      p_109296_.put(Blocks.POTTED_OAK_SAPLING, rendertype2);
      p_109296_.put(Blocks.POTTED_SPRUCE_SAPLING, rendertype2);
      p_109296_.put(Blocks.POTTED_BIRCH_SAPLING, rendertype2);
      p_109296_.put(Blocks.POTTED_JUNGLE_SAPLING, rendertype2);
      p_109296_.put(Blocks.POTTED_ACACIA_SAPLING, rendertype2);
      p_109296_.put(Blocks.POTTED_DARK_OAK_SAPLING, rendertype2);
      p_109296_.put(Blocks.POTTED_FERN, rendertype2);
      p_109296_.put(Blocks.POTTED_DANDELION, rendertype2);
      p_109296_.put(Blocks.POTTED_POPPY, rendertype2);
      p_109296_.put(Blocks.POTTED_BLUE_ORCHID, rendertype2);
      p_109296_.put(Blocks.POTTED_ALLIUM, rendertype2);
      p_109296_.put(Blocks.POTTED_AZURE_BLUET, rendertype2);
      p_109296_.put(Blocks.POTTED_RED_TULIP, rendertype2);
      p_109296_.put(Blocks.POTTED_ORANGE_TULIP, rendertype2);
      p_109296_.put(Blocks.POTTED_WHITE_TULIP, rendertype2);
      p_109296_.put(Blocks.POTTED_PINK_TULIP, rendertype2);
      p_109296_.put(Blocks.POTTED_OXEYE_DAISY, rendertype2);
      p_109296_.put(Blocks.POTTED_CORNFLOWER, rendertype2);
      p_109296_.put(Blocks.POTTED_LILY_OF_THE_VALLEY, rendertype2);
      p_109296_.put(Blocks.POTTED_WITHER_ROSE, rendertype2);
      p_109296_.put(Blocks.POTTED_RED_MUSHROOM, rendertype2);
      p_109296_.put(Blocks.POTTED_BROWN_MUSHROOM, rendertype2);
      p_109296_.put(Blocks.POTTED_DEAD_BUSH, rendertype2);
      p_109296_.put(Blocks.POTTED_CACTUS, rendertype2);
      p_109296_.put(Blocks.POTTED_AZALEA, rendertype2);
      p_109296_.put(Blocks.POTTED_FLOWERING_AZALEA, rendertype2);
      p_109296_.put(Blocks.CARROTS, rendertype2);
      p_109296_.put(Blocks.POTATOES, rendertype2);
      p_109296_.put(Blocks.COMPARATOR, rendertype2);
      p_109296_.put(Blocks.ACTIVATOR_RAIL, rendertype2);
      p_109296_.put(Blocks.IRON_TRAPDOOR, rendertype2);
      p_109296_.put(Blocks.SUNFLOWER, rendertype2);
      p_109296_.put(Blocks.LILAC, rendertype2);
      p_109296_.put(Blocks.ROSE_BUSH, rendertype2);
      p_109296_.put(Blocks.PEONY, rendertype2);
      p_109296_.put(Blocks.TALL_GRASS, rendertype2);
      p_109296_.put(Blocks.LARGE_FERN, rendertype2);
      p_109296_.put(Blocks.SPRUCE_DOOR, rendertype2);
      p_109296_.put(Blocks.BIRCH_DOOR, rendertype2);
      p_109296_.put(Blocks.JUNGLE_DOOR, rendertype2);
      p_109296_.put(Blocks.ACACIA_DOOR, rendertype2);
      p_109296_.put(Blocks.DARK_OAK_DOOR, rendertype2);
      p_109296_.put(Blocks.END_ROD, rendertype2);
      p_109296_.put(Blocks.CHORUS_PLANT, rendertype2);
      p_109296_.put(Blocks.CHORUS_FLOWER, rendertype2);
      p_109296_.put(Blocks.BEETROOTS, rendertype2);
      p_109296_.put(Blocks.KELP, rendertype2);
      p_109296_.put(Blocks.KELP_PLANT, rendertype2);
      p_109296_.put(Blocks.TURTLE_EGG, rendertype2);
      p_109296_.put(Blocks.DEAD_TUBE_CORAL, rendertype2);
      p_109296_.put(Blocks.DEAD_BRAIN_CORAL, rendertype2);
      p_109296_.put(Blocks.DEAD_BUBBLE_CORAL, rendertype2);
      p_109296_.put(Blocks.DEAD_FIRE_CORAL, rendertype2);
      p_109296_.put(Blocks.DEAD_HORN_CORAL, rendertype2);
      p_109296_.put(Blocks.TUBE_CORAL, rendertype2);
      p_109296_.put(Blocks.BRAIN_CORAL, rendertype2);
      p_109296_.put(Blocks.BUBBLE_CORAL, rendertype2);
      p_109296_.put(Blocks.FIRE_CORAL, rendertype2);
      p_109296_.put(Blocks.HORN_CORAL, rendertype2);
      p_109296_.put(Blocks.DEAD_TUBE_CORAL_FAN, rendertype2);
      p_109296_.put(Blocks.DEAD_BRAIN_CORAL_FAN, rendertype2);
      p_109296_.put(Blocks.DEAD_BUBBLE_CORAL_FAN, rendertype2);
      p_109296_.put(Blocks.DEAD_FIRE_CORAL_FAN, rendertype2);
      p_109296_.put(Blocks.DEAD_HORN_CORAL_FAN, rendertype2);
      p_109296_.put(Blocks.TUBE_CORAL_FAN, rendertype2);
      p_109296_.put(Blocks.BRAIN_CORAL_FAN, rendertype2);
      p_109296_.put(Blocks.BUBBLE_CORAL_FAN, rendertype2);
      p_109296_.put(Blocks.FIRE_CORAL_FAN, rendertype2);
      p_109296_.put(Blocks.HORN_CORAL_FAN, rendertype2);
      p_109296_.put(Blocks.DEAD_TUBE_CORAL_WALL_FAN, rendertype2);
      p_109296_.put(Blocks.DEAD_BRAIN_CORAL_WALL_FAN, rendertype2);
      p_109296_.put(Blocks.DEAD_BUBBLE_CORAL_WALL_FAN, rendertype2);
      p_109296_.put(Blocks.DEAD_FIRE_CORAL_WALL_FAN, rendertype2);
      p_109296_.put(Blocks.DEAD_HORN_CORAL_WALL_FAN, rendertype2);
      p_109296_.put(Blocks.TUBE_CORAL_WALL_FAN, rendertype2);
      p_109296_.put(Blocks.BRAIN_CORAL_WALL_FAN, rendertype2);
      p_109296_.put(Blocks.BUBBLE_CORAL_WALL_FAN, rendertype2);
      p_109296_.put(Blocks.FIRE_CORAL_WALL_FAN, rendertype2);
      p_109296_.put(Blocks.HORN_CORAL_WALL_FAN, rendertype2);
      p_109296_.put(Blocks.SEA_PICKLE, rendertype2);
      p_109296_.put(Blocks.CONDUIT, rendertype2);
      p_109296_.put(Blocks.BAMBOO_SAPLING, rendertype2);
      p_109296_.put(Blocks.BAMBOO, rendertype2);
      p_109296_.put(Blocks.POTTED_BAMBOO, rendertype2);
      p_109296_.put(Blocks.SCAFFOLDING, rendertype2);
      p_109296_.put(Blocks.STONECUTTER, rendertype2);
      p_109296_.put(Blocks.LANTERN, rendertype2);
      p_109296_.put(Blocks.SOUL_LANTERN, rendertype2);
      p_109296_.put(Blocks.CAMPFIRE, rendertype2);
      p_109296_.put(Blocks.SOUL_CAMPFIRE, rendertype2);
      p_109296_.put(Blocks.SWEET_BERRY_BUSH, rendertype2);
      p_109296_.put(Blocks.WEEPING_VINES, rendertype2);
      p_109296_.put(Blocks.WEEPING_VINES_PLANT, rendertype2);
      p_109296_.put(Blocks.TWISTING_VINES, rendertype2);
      p_109296_.put(Blocks.TWISTING_VINES_PLANT, rendertype2);
      p_109296_.put(Blocks.NETHER_SPROUTS, rendertype2);
      p_109296_.put(Blocks.CRIMSON_FUNGUS, rendertype2);
      p_109296_.put(Blocks.WARPED_FUNGUS, rendertype2);
      p_109296_.put(Blocks.CRIMSON_ROOTS, rendertype2);
      p_109296_.put(Blocks.WARPED_ROOTS, rendertype2);
      p_109296_.put(Blocks.POTTED_CRIMSON_FUNGUS, rendertype2);
      p_109296_.put(Blocks.POTTED_WARPED_FUNGUS, rendertype2);
      p_109296_.put(Blocks.POTTED_CRIMSON_ROOTS, rendertype2);
      p_109296_.put(Blocks.POTTED_WARPED_ROOTS, rendertype2);
      p_109296_.put(Blocks.CRIMSON_DOOR, rendertype2);
      p_109296_.put(Blocks.WARPED_DOOR, rendertype2);
      p_109296_.put(Blocks.POINTED_DRIPSTONE, rendertype2);
      p_109296_.put(Blocks.SMALL_AMETHYST_BUD, rendertype2);
      p_109296_.put(Blocks.MEDIUM_AMETHYST_BUD, rendertype2);
      p_109296_.put(Blocks.LARGE_AMETHYST_BUD, rendertype2);
      p_109296_.put(Blocks.AMETHYST_CLUSTER, rendertype2);
      p_109296_.put(Blocks.LIGHTNING_ROD, rendertype2);
      p_109296_.put(Blocks.CAVE_VINES, rendertype2);
      p_109296_.put(Blocks.CAVE_VINES_PLANT, rendertype2);
      p_109296_.put(Blocks.SPORE_BLOSSOM, rendertype2);
      p_109296_.put(Blocks.FLOWERING_AZALEA, rendertype2);
      p_109296_.put(Blocks.AZALEA, rendertype2);
      p_109296_.put(Blocks.MOSS_CARPET, rendertype2);
      p_109296_.put(Blocks.BIG_DRIPLEAF, rendertype2);
      p_109296_.put(Blocks.BIG_DRIPLEAF_STEM, rendertype2);
      p_109296_.put(Blocks.SMALL_DRIPLEAF, rendertype2);
      p_109296_.put(Blocks.HANGING_ROOTS, rendertype2);
      p_109296_.put(Blocks.SCULK_SENSOR, rendertype2);
      RenderType rendertype3 = RenderType.translucent();
      p_109296_.put(Blocks.ICE, rendertype3);
      p_109296_.put(Blocks.NETHER_PORTAL, rendertype3);
      p_109296_.put(Blocks.WHITE_STAINED_GLASS, rendertype3);
      p_109296_.put(Blocks.ORANGE_STAINED_GLASS, rendertype3);
      p_109296_.put(Blocks.MAGENTA_STAINED_GLASS, rendertype3);
      p_109296_.put(Blocks.LIGHT_BLUE_STAINED_GLASS, rendertype3);
      p_109296_.put(Blocks.YELLOW_STAINED_GLASS, rendertype3);
      p_109296_.put(Blocks.LIME_STAINED_GLASS, rendertype3);
      p_109296_.put(Blocks.PINK_STAINED_GLASS, rendertype3);
      p_109296_.put(Blocks.GRAY_STAINED_GLASS, rendertype3);
      p_109296_.put(Blocks.LIGHT_GRAY_STAINED_GLASS, rendertype3);
      p_109296_.put(Blocks.CYAN_STAINED_GLASS, rendertype3);
      p_109296_.put(Blocks.PURPLE_STAINED_GLASS, rendertype3);
      p_109296_.put(Blocks.BLUE_STAINED_GLASS, rendertype3);
      p_109296_.put(Blocks.BROWN_STAINED_GLASS, rendertype3);
      p_109296_.put(Blocks.GREEN_STAINED_GLASS, rendertype3);
      p_109296_.put(Blocks.RED_STAINED_GLASS, rendertype3);
      p_109296_.put(Blocks.BLACK_STAINED_GLASS, rendertype3);
      p_109296_.put(Blocks.WHITE_STAINED_GLASS_PANE, rendertype3);
      p_109296_.put(Blocks.ORANGE_STAINED_GLASS_PANE, rendertype3);
      p_109296_.put(Blocks.MAGENTA_STAINED_GLASS_PANE, rendertype3);
      p_109296_.put(Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, rendertype3);
      p_109296_.put(Blocks.YELLOW_STAINED_GLASS_PANE, rendertype3);
      p_109296_.put(Blocks.LIME_STAINED_GLASS_PANE, rendertype3);
      p_109296_.put(Blocks.PINK_STAINED_GLASS_PANE, rendertype3);
      p_109296_.put(Blocks.GRAY_STAINED_GLASS_PANE, rendertype3);
      p_109296_.put(Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, rendertype3);
      p_109296_.put(Blocks.CYAN_STAINED_GLASS_PANE, rendertype3);
      p_109296_.put(Blocks.PURPLE_STAINED_GLASS_PANE, rendertype3);
      p_109296_.put(Blocks.BLUE_STAINED_GLASS_PANE, rendertype3);
      p_109296_.put(Blocks.BROWN_STAINED_GLASS_PANE, rendertype3);
      p_109296_.put(Blocks.GREEN_STAINED_GLASS_PANE, rendertype3);
      p_109296_.put(Blocks.RED_STAINED_GLASS_PANE, rendertype3);
      p_109296_.put(Blocks.BLACK_STAINED_GLASS_PANE, rendertype3);
      p_109296_.put(Blocks.SLIME_BLOCK, rendertype3);
      p_109296_.put(Blocks.HONEY_BLOCK, rendertype3);
      p_109296_.put(Blocks.FROSTED_ICE, rendertype3);
      p_109296_.put(Blocks.BUBBLE_COLUMN, rendertype3);
      p_109296_.put(Blocks.TINTED_GLASS, rendertype3);
   });
   @Deprecated
   private static final Map<Fluid, RenderType> TYPE_BY_FLUID = Util.make(Maps.newHashMap(), (p_109290_) -> {
      RenderType rendertype = RenderType.translucent();
      p_109290_.put(Fluids.FLOWING_WATER, rendertype);
      p_109290_.put(Fluids.WATER, rendertype);
   });
   private static boolean renderCutout;

   @Deprecated // FORGE: Use canRenderInLayer
   public static RenderType getChunkRenderType(BlockState p_109283_) {
      Block block = p_109283_.getBlock();
      if (block instanceof LeavesBlock) {
         return renderCutout ? RenderType.cutoutMipped() : RenderType.solid();
      } else {
         RenderType rendertype = TYPE_BY_BLOCK.get(block);
         return rendertype != null ? rendertype : RenderType.solid();
      }
   }

   @Deprecated // FORGE: Use canRenderInLayer
   public static RenderType getMovingBlockRenderType(BlockState p_109294_) {
      Block block = p_109294_.getBlock();
      if (block instanceof LeavesBlock) {
         return renderCutout ? RenderType.cutoutMipped() : RenderType.solid();
      } else {
         RenderType rendertype = TYPE_BY_BLOCK.get(block);
         if (rendertype != null) {
            return rendertype == RenderType.translucent() ? RenderType.translucentMovingBlock() : rendertype;
         } else {
            return RenderType.solid();
         }
      }
   }

   public static RenderType getRenderType(BlockState p_109285_, boolean p_109286_) {
      if (canRenderInLayer(p_109285_, RenderType.translucent())) {
         if (!Minecraft.useShaderTransparency()) {
            return Sheets.translucentCullBlockSheet();
         } else {
            return p_109286_ ? Sheets.translucentCullBlockSheet() : Sheets.translucentItemSheet();
         }
      } else {
         return Sheets.cutoutBlockSheet();
      }
   }

   public static RenderType getRenderType(ItemStack p_109280_, boolean p_109281_) {
      Item item = p_109280_.getItem();
      if (item instanceof BlockItem) {
         Block block = ((BlockItem)item).getBlock();
         return getRenderType(block.defaultBlockState(), p_109281_);
      } else {
         return p_109281_ ? Sheets.translucentCullBlockSheet() : Sheets.translucentItemSheet();
      }
   }

   @Deprecated // FORGE: Use canRenderInLayer
   public static RenderType getRenderLayer(FluidState p_109288_) {
      RenderType rendertype = TYPE_BY_FLUID.get(p_109288_.getType());
      return rendertype != null ? rendertype : RenderType.solid();
   }

   // FORGE START

   private static final java.util.function.Predicate<RenderType> SOLID_PREDICATE = type -> type == RenderType.solid();
   // Access to the two following editable maps is guarded by synchronization and they are lazily copied to the "readonly" maps on first read after modification
   private static final Map<net.minecraftforge.registries.IRegistryDelegate<Block>, java.util.function.Predicate<RenderType>> blockRenderChecks = createRenderCheckMap(TYPE_BY_BLOCK);
   private static final Map<net.minecraftforge.registries.IRegistryDelegate<Fluid>, java.util.function.Predicate<RenderType>> fluidRenderChecks = createRenderCheckMap(TYPE_BY_FLUID);
   @org.jetbrains.annotations.Nullable private static volatile Map<net.minecraftforge.registries.IRegistryDelegate<Block>, java.util.function.Predicate<RenderType>> blockRenderChecksReadOnly = null;
   @org.jetbrains.annotations.Nullable private static volatile Map<net.minecraftforge.registries.IRegistryDelegate<Fluid>, java.util.function.Predicate<RenderType>> fluidRenderChecksReadOnly = null;

   private static <T extends net.minecraftforge.registries.ForgeRegistryEntry<T>> Map<net.minecraftforge.registries.IRegistryDelegate<T>, java.util.function.Predicate<RenderType>> createRenderCheckMap(
           Map<T, RenderType> vanillaMap
   ) {
      return Util.make(new it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap<>(vanillaMap.size(), 0.5F), map -> {
         map.defaultReturnValue(SOLID_PREDICATE);
         for (Map.Entry<T, RenderType> entry : vanillaMap.entrySet()) {
            map.put(entry.getKey().delegate, createMatchingLayerPredicate(entry.getValue()));
         }
      });
   }

   public static boolean canRenderInLayer(BlockState state, RenderType type) {
      Block block = state.getBlock();
      if (block instanceof LeavesBlock) {
         return renderCutout ? type == RenderType.cutoutMipped() : type == RenderType.solid();
      } else {
         return getBlockLayerPredicates().get(block.delegate).test(type);
      }
   }

   public static boolean canRenderInLayer(FluidState fluid, RenderType type) {
      return getFluidLayerPredicates().get(fluid.getType().delegate).test(type);
   }

   public static void setRenderLayer(Block block, RenderType type) {
      setRenderLayer(block, createMatchingLayerPredicate(type));
   }

   public static void setRenderLayer(Block block, java.util.function.Predicate<RenderType> predicate) {
      synchronized (blockRenderChecks) {
         blockRenderChecks.put(block.delegate, predicate);
         blockRenderChecksReadOnly = null;
      }
   }

   public static void setRenderLayer(Fluid fluid, RenderType type) {
      setRenderLayer(fluid, createMatchingLayerPredicate(type));
   }

   public static void setRenderLayer(Fluid fluid, java.util.function.Predicate<RenderType> predicate) {
      synchronized (fluidRenderChecks) {
         fluidRenderChecks.put(fluid.delegate, predicate);
         fluidRenderChecksReadOnly = null;
      }
   }

   public static Map<net.minecraftforge.registries.IRegistryDelegate<Block>, java.util.function.Predicate<RenderType>> getBlockLayerPredicatesView() {
      return java.util.Collections.unmodifiableMap(getBlockLayerPredicates());
   }

   private static Map<net.minecraftforge.registries.IRegistryDelegate<Block>, java.util.function.Predicate<RenderType>> getBlockLayerPredicates() {
      Map<net.minecraftforge.registries.IRegistryDelegate<Block>, java.util.function.Predicate<RenderType>> map = blockRenderChecksReadOnly;
      if (map == null) {
         synchronized (blockRenderChecks) {
            map = blockRenderChecksReadOnly;
            if (map == null) {
               blockRenderChecksReadOnly = map = copy(blockRenderChecks);
            }
         }
      }
      return map;
   }


   public static Map<net.minecraftforge.registries.IRegistryDelegate<Fluid>, java.util.function.Predicate<RenderType>> getFluidLayerPredicatesView() {
      return java.util.Collections.unmodifiableMap(getFluidLayerPredicates());
   }

   private static Map<net.minecraftforge.registries.IRegistryDelegate<Fluid>, java.util.function.Predicate<RenderType>> getFluidLayerPredicates() {
      Map<net.minecraftforge.registries.IRegistryDelegate<Fluid>, java.util.function.Predicate<RenderType>> map = fluidRenderChecksReadOnly;
      if (map == null) {
         synchronized (fluidRenderChecks) {
            map = fluidRenderChecksReadOnly;
            if (map == null) {
               fluidRenderChecksReadOnly = map = copy(fluidRenderChecks);
            }
         }
      }
      return map;
   }

   private static <T> Map<net.minecraftforge.registries.IRegistryDelegate<T>, java.util.function.Predicate<RenderType>> copy(Map<net.minecraftforge.registries.IRegistryDelegate<T>, java.util.function.Predicate<RenderType>> map) {
      var newMap = new it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap<>(map);
      newMap.defaultReturnValue(SOLID_PREDICATE);
      return newMap;
   }

   private static java.util.function.Predicate<RenderType> createMatchingLayerPredicate(RenderType type) {
      java.util.Objects.requireNonNull(type);
      return type::equals;
   }

   public static void setFancy(boolean p_109292_) {
      renderCutout = p_109292_;
   }
}
