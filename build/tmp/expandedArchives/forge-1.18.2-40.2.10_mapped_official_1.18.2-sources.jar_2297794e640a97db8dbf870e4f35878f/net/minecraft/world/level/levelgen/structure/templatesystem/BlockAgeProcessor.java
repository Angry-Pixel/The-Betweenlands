package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;

public class BlockAgeProcessor extends StructureProcessor {
   public static final Codec<BlockAgeProcessor> CODEC = Codec.FLOAT.fieldOf("mossiness").xmap(BlockAgeProcessor::new, (p_74023_) -> {
      return p_74023_.mossiness;
   }).codec();
   private static final float PROBABILITY_OF_REPLACING_FULL_BLOCK = 0.5F;
   private static final float PROBABILITY_OF_REPLACING_STAIRS = 0.5F;
   private static final float PROBABILITY_OF_REPLACING_OBSIDIAN = 0.15F;
   private static final BlockState[] NON_MOSSY_REPLACEMENTS = new BlockState[]{Blocks.STONE_SLAB.defaultBlockState(), Blocks.STONE_BRICK_SLAB.defaultBlockState()};
   private final float mossiness;

   public BlockAgeProcessor(float p_74013_) {
      this.mossiness = p_74013_;
   }

   @Nullable
   public StructureTemplate.StructureBlockInfo processBlock(LevelReader p_74016_, BlockPos p_74017_, BlockPos p_74018_, StructureTemplate.StructureBlockInfo p_74019_, StructureTemplate.StructureBlockInfo p_74020_, StructurePlaceSettings p_74021_) {
      Random random = p_74021_.getRandom(p_74020_.pos);
      BlockState blockstate = p_74020_.state;
      BlockPos blockpos = p_74020_.pos;
      BlockState blockstate1 = null;
      if (!blockstate.is(Blocks.STONE_BRICKS) && !blockstate.is(Blocks.STONE) && !blockstate.is(Blocks.CHISELED_STONE_BRICKS)) {
         if (blockstate.is(BlockTags.STAIRS)) {
            blockstate1 = this.maybeReplaceStairs(random, p_74020_.state);
         } else if (blockstate.is(BlockTags.SLABS)) {
            blockstate1 = this.maybeReplaceSlab(random);
         } else if (blockstate.is(BlockTags.WALLS)) {
            blockstate1 = this.maybeReplaceWall(random);
         } else if (blockstate.is(Blocks.OBSIDIAN)) {
            blockstate1 = this.maybeReplaceObsidian(random);
         }
      } else {
         blockstate1 = this.maybeReplaceFullStoneBlock(random);
      }

      return blockstate1 != null ? new StructureTemplate.StructureBlockInfo(blockpos, blockstate1, p_74020_.nbt) : p_74020_;
   }

   @Nullable
   private BlockState maybeReplaceFullStoneBlock(Random p_74025_) {
      if (p_74025_.nextFloat() >= 0.5F) {
         return null;
      } else {
         BlockState[] ablockstate = new BlockState[]{Blocks.CRACKED_STONE_BRICKS.defaultBlockState(), getRandomFacingStairs(p_74025_, Blocks.STONE_BRICK_STAIRS)};
         BlockState[] ablockstate1 = new BlockState[]{Blocks.MOSSY_STONE_BRICKS.defaultBlockState(), getRandomFacingStairs(p_74025_, Blocks.MOSSY_STONE_BRICK_STAIRS)};
         return this.getRandomBlock(p_74025_, ablockstate, ablockstate1);
      }
   }

   @Nullable
   private BlockState maybeReplaceStairs(Random p_74030_, BlockState p_74031_) {
      Direction direction = p_74031_.getValue(StairBlock.FACING);
      Half half = p_74031_.getValue(StairBlock.HALF);
      if (p_74030_.nextFloat() >= 0.5F) {
         return null;
      } else {
         BlockState[] ablockstate = new BlockState[]{Blocks.MOSSY_STONE_BRICK_STAIRS.defaultBlockState().setValue(StairBlock.FACING, direction).setValue(StairBlock.HALF, half), Blocks.MOSSY_STONE_BRICK_SLAB.defaultBlockState()};
         return this.getRandomBlock(p_74030_, NON_MOSSY_REPLACEMENTS, ablockstate);
      }
   }

   @Nullable
   private BlockState maybeReplaceSlab(Random p_74040_) {
      return p_74040_.nextFloat() < this.mossiness ? Blocks.MOSSY_STONE_BRICK_SLAB.defaultBlockState() : null;
   }

   @Nullable
   private BlockState maybeReplaceWall(Random p_74042_) {
      return p_74042_.nextFloat() < this.mossiness ? Blocks.MOSSY_STONE_BRICK_WALL.defaultBlockState() : null;
   }

   @Nullable
   private BlockState maybeReplaceObsidian(Random p_74044_) {
      return p_74044_.nextFloat() < 0.15F ? Blocks.CRYING_OBSIDIAN.defaultBlockState() : null;
   }

   private static BlockState getRandomFacingStairs(Random p_74027_, Block p_74028_) {
      return p_74028_.defaultBlockState().setValue(StairBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(p_74027_)).setValue(StairBlock.HALF, Half.values()[p_74027_.nextInt(Half.values().length)]);
   }

   private BlockState getRandomBlock(Random p_74036_, BlockState[] p_74037_, BlockState[] p_74038_) {
      return p_74036_.nextFloat() < this.mossiness ? getRandomBlock(p_74036_, p_74038_) : getRandomBlock(p_74036_, p_74037_);
   }

   private static BlockState getRandomBlock(Random p_74033_, BlockState[] p_74034_) {
      return p_74034_[p_74033_.nextInt(p_74034_.length)];
   }

   protected StructureProcessorType<?> getType() {
      return StructureProcessorType.BLOCK_AGE;
   }
}