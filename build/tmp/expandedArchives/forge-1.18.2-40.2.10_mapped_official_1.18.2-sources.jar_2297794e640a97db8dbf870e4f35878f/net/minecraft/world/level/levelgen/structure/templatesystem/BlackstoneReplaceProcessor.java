package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;

public class BlackstoneReplaceProcessor extends StructureProcessor {
   public static final Codec<BlackstoneReplaceProcessor> CODEC = Codec.unit(() -> {
      return BlackstoneReplaceProcessor.INSTANCE;
   });
   public static final BlackstoneReplaceProcessor INSTANCE = new BlackstoneReplaceProcessor();
   private final Map<Block, Block> replacements = Util.make(Maps.newHashMap(), (p_74007_) -> {
      p_74007_.put(Blocks.COBBLESTONE, Blocks.BLACKSTONE);
      p_74007_.put(Blocks.MOSSY_COBBLESTONE, Blocks.BLACKSTONE);
      p_74007_.put(Blocks.STONE, Blocks.POLISHED_BLACKSTONE);
      p_74007_.put(Blocks.STONE_BRICKS, Blocks.POLISHED_BLACKSTONE_BRICKS);
      p_74007_.put(Blocks.MOSSY_STONE_BRICKS, Blocks.POLISHED_BLACKSTONE_BRICKS);
      p_74007_.put(Blocks.COBBLESTONE_STAIRS, Blocks.BLACKSTONE_STAIRS);
      p_74007_.put(Blocks.MOSSY_COBBLESTONE_STAIRS, Blocks.BLACKSTONE_STAIRS);
      p_74007_.put(Blocks.STONE_STAIRS, Blocks.POLISHED_BLACKSTONE_STAIRS);
      p_74007_.put(Blocks.STONE_BRICK_STAIRS, Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS);
      p_74007_.put(Blocks.MOSSY_STONE_BRICK_STAIRS, Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS);
      p_74007_.put(Blocks.COBBLESTONE_SLAB, Blocks.BLACKSTONE_SLAB);
      p_74007_.put(Blocks.MOSSY_COBBLESTONE_SLAB, Blocks.BLACKSTONE_SLAB);
      p_74007_.put(Blocks.SMOOTH_STONE_SLAB, Blocks.POLISHED_BLACKSTONE_SLAB);
      p_74007_.put(Blocks.STONE_SLAB, Blocks.POLISHED_BLACKSTONE_SLAB);
      p_74007_.put(Blocks.STONE_BRICK_SLAB, Blocks.POLISHED_BLACKSTONE_BRICK_SLAB);
      p_74007_.put(Blocks.MOSSY_STONE_BRICK_SLAB, Blocks.POLISHED_BLACKSTONE_BRICK_SLAB);
      p_74007_.put(Blocks.STONE_BRICK_WALL, Blocks.POLISHED_BLACKSTONE_BRICK_WALL);
      p_74007_.put(Blocks.MOSSY_STONE_BRICK_WALL, Blocks.POLISHED_BLACKSTONE_BRICK_WALL);
      p_74007_.put(Blocks.COBBLESTONE_WALL, Blocks.BLACKSTONE_WALL);
      p_74007_.put(Blocks.MOSSY_COBBLESTONE_WALL, Blocks.BLACKSTONE_WALL);
      p_74007_.put(Blocks.CHISELED_STONE_BRICKS, Blocks.CHISELED_POLISHED_BLACKSTONE);
      p_74007_.put(Blocks.CRACKED_STONE_BRICKS, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS);
      p_74007_.put(Blocks.IRON_BARS, Blocks.CHAIN);
   });

   private BlackstoneReplaceProcessor() {
   }

   public StructureTemplate.StructureBlockInfo processBlock(LevelReader p_74000_, BlockPos p_74001_, BlockPos p_74002_, StructureTemplate.StructureBlockInfo p_74003_, StructureTemplate.StructureBlockInfo p_74004_, StructurePlaceSettings p_74005_) {
      Block block = this.replacements.get(p_74004_.state.getBlock());
      if (block == null) {
         return p_74004_;
      } else {
         BlockState blockstate = p_74004_.state;
         BlockState blockstate1 = block.defaultBlockState();
         if (blockstate.hasProperty(StairBlock.FACING)) {
            blockstate1 = blockstate1.setValue(StairBlock.FACING, blockstate.getValue(StairBlock.FACING));
         }

         if (blockstate.hasProperty(StairBlock.HALF)) {
            blockstate1 = blockstate1.setValue(StairBlock.HALF, blockstate.getValue(StairBlock.HALF));
         }

         if (blockstate.hasProperty(SlabBlock.TYPE)) {
            blockstate1 = blockstate1.setValue(SlabBlock.TYPE, blockstate.getValue(SlabBlock.TYPE));
         }

         return new StructureTemplate.StructureBlockInfo(p_74004_.pos, blockstate1, p_74004_.nbt);
      }
   }

   protected StructureProcessorType<?> getType() {
      return StructureProcessorType.BLACKSTONE_REPLACE;
   }
}