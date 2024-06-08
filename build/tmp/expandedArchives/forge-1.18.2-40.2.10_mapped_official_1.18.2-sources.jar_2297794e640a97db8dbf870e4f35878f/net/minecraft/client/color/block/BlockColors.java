package net.minecraft.client.color.block;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.IdMapper;
import net.minecraft.core.Registry;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlockColors {
   private static final int DEFAULT = -1;
   // FORGE: Use RegistryDelegates as non-Vanilla block ids are not constant
   private final java.util.Map<net.minecraftforge.registries.IRegistryDelegate<Block>, BlockColor> blockColors = new java.util.HashMap<>();
   private final Map<Block, Set<Property<?>>> coloringStates = Maps.newHashMap();

   public static BlockColors createDefault() {
      BlockColors blockcolors = new BlockColors();
      blockcolors.register((p_92646_, p_92647_, p_92648_, p_92649_) -> {
         return p_92647_ != null && p_92648_ != null ? BiomeColors.getAverageGrassColor(p_92647_, p_92646_.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER ? p_92648_.below() : p_92648_) : -1;
      }, Blocks.LARGE_FERN, Blocks.TALL_GRASS);
      blockcolors.addColoringState(DoublePlantBlock.HALF, Blocks.LARGE_FERN, Blocks.TALL_GRASS);
      blockcolors.register((p_92641_, p_92642_, p_92643_, p_92644_) -> {
         return p_92642_ != null && p_92643_ != null ? BiomeColors.getAverageGrassColor(p_92642_, p_92643_) : GrassColor.get(0.5D, 1.0D);
      }, Blocks.GRASS_BLOCK, Blocks.FERN, Blocks.GRASS, Blocks.POTTED_FERN);
      blockcolors.register((p_92636_, p_92637_, p_92638_, p_92639_) -> {
         return FoliageColor.getEvergreenColor();
      }, Blocks.SPRUCE_LEAVES);
      blockcolors.register((p_92631_, p_92632_, p_92633_, p_92634_) -> {
         return FoliageColor.getBirchColor();
      }, Blocks.BIRCH_LEAVES);
      blockcolors.register((p_92626_, p_92627_, p_92628_, p_92629_) -> {
         return p_92627_ != null && p_92628_ != null ? BiomeColors.getAverageFoliageColor(p_92627_, p_92628_) : FoliageColor.getDefaultColor();
      }, Blocks.OAK_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.ACACIA_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.VINE);
      blockcolors.register((p_92621_, p_92622_, p_92623_, p_92624_) -> {
         return p_92622_ != null && p_92623_ != null ? BiomeColors.getAverageWaterColor(p_92622_, p_92623_) : -1;
      }, Blocks.WATER, Blocks.BUBBLE_COLUMN, Blocks.WATER_CAULDRON);
      blockcolors.register((p_92616_, p_92617_, p_92618_, p_92619_) -> {
         return RedStoneWireBlock.getColorForPower(p_92616_.getValue(RedStoneWireBlock.POWER));
      }, Blocks.REDSTONE_WIRE);
      blockcolors.addColoringState(RedStoneWireBlock.POWER, Blocks.REDSTONE_WIRE);
      blockcolors.register((p_92611_, p_92612_, p_92613_, p_92614_) -> {
         return p_92612_ != null && p_92613_ != null ? BiomeColors.getAverageGrassColor(p_92612_, p_92613_) : -1;
      }, Blocks.SUGAR_CANE);
      blockcolors.register((p_92606_, p_92607_, p_92608_, p_92609_) -> {
         return 14731036;
      }, Blocks.ATTACHED_MELON_STEM, Blocks.ATTACHED_PUMPKIN_STEM);
      blockcolors.register((p_92601_, p_92602_, p_92603_, p_92604_) -> {
         int i = p_92601_.getValue(StemBlock.AGE);
         int j = i * 32;
         int k = 255 - i * 8;
         int l = i * 4;
         return j << 16 | k << 8 | l;
      }, Blocks.MELON_STEM, Blocks.PUMPKIN_STEM);
      blockcolors.addColoringState(StemBlock.AGE, Blocks.MELON_STEM, Blocks.PUMPKIN_STEM);
      blockcolors.register((p_92596_, p_92597_, p_92598_, p_92599_) -> {
         return p_92597_ != null && p_92598_ != null ? 2129968 : 7455580;
      }, Blocks.LILY_PAD);
      net.minecraftforge.client.ForgeHooksClient.onBlockColorsInit(blockcolors);
      return blockcolors;
   }

   public int getColor(BlockState p_92583_, Level p_92584_, BlockPos p_92585_) {
      BlockColor blockcolor = this.blockColors.get(p_92583_.getBlock().delegate);
      if (blockcolor != null) {
         return blockcolor.getColor(p_92583_, (BlockAndTintGetter)null, (BlockPos)null, 0);
      } else {
         MaterialColor materialcolor = p_92583_.getMapColor(p_92584_, p_92585_);
         return materialcolor != null ? materialcolor.col : -1;
      }
   }

   public int getColor(BlockState p_92578_, @Nullable BlockAndTintGetter p_92579_, @Nullable BlockPos p_92580_, int p_92581_) {
      BlockColor blockcolor = this.blockColors.get(p_92578_.getBlock().delegate);
      return blockcolor == null ? -1 : blockcolor.getColor(p_92578_, p_92579_, p_92580_, p_92581_);
   }

   public void register(BlockColor p_92590_, Block... p_92591_) {
      for(Block block : p_92591_) {
         this.blockColors.put(block.delegate, p_92590_);
      }

   }

   private void addColoringStates(Set<Property<?>> p_92593_, Block... p_92594_) {
      for(Block block : p_92594_) {
         this.coloringStates.put(block, p_92593_);
      }

   }

   private void addColoringState(Property<?> p_92587_, Block... p_92588_) {
      this.addColoringStates(ImmutableSet.of(p_92587_), p_92588_);
   }

   public Set<Property<?>> getColoringProperties(Block p_92576_) {
      return this.coloringStates.getOrDefault(p_92576_, ImmutableSet.of());
   }
}
