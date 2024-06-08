package net.minecraft.world.level.levelgen.structure;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class DesertPyramidPiece extends ScatteredFeaturePiece {
   public static final int WIDTH = 21;
   public static final int DEPTH = 21;
   private final boolean[] hasPlacedChest = new boolean[4];

   public DesertPyramidPiece(Random p_71086_, int p_71087_, int p_71088_) {
      super(StructurePieceType.DESERT_PYRAMID_PIECE, p_71087_, 64, p_71088_, 21, 15, 21, getRandomHorizontalDirection(p_71086_));
   }

   public DesertPyramidPiece(CompoundTag p_191979_) {
      super(StructurePieceType.DESERT_PYRAMID_PIECE, p_191979_);
      this.hasPlacedChest[0] = p_191979_.getBoolean("hasPlacedChest0");
      this.hasPlacedChest[1] = p_191979_.getBoolean("hasPlacedChest1");
      this.hasPlacedChest[2] = p_191979_.getBoolean("hasPlacedChest2");
      this.hasPlacedChest[3] = p_191979_.getBoolean("hasPlacedChest3");
   }

   protected void addAdditionalSaveData(StructurePieceSerializationContext p_191989_, CompoundTag p_191990_) {
      super.addAdditionalSaveData(p_191989_, p_191990_);
      p_191990_.putBoolean("hasPlacedChest0", this.hasPlacedChest[0]);
      p_191990_.putBoolean("hasPlacedChest1", this.hasPlacedChest[1]);
      p_191990_.putBoolean("hasPlacedChest2", this.hasPlacedChest[2]);
      p_191990_.putBoolean("hasPlacedChest3", this.hasPlacedChest[3]);
   }

   public void postProcess(WorldGenLevel p_191981_, StructureFeatureManager p_191982_, ChunkGenerator p_191983_, Random p_191984_, BoundingBox p_191985_, ChunkPos p_191986_, BlockPos p_191987_) {
      if (this.updateHeightPositionToLowestGroundHeight(p_191981_, -p_191984_.nextInt(3))) {
         this.generateBox(p_191981_, p_191985_, 0, -4, 0, this.width - 1, 0, this.depth - 1, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);

         for(int i = 1; i <= 9; ++i) {
            this.generateBox(p_191981_, p_191985_, i, i, i, this.width - 1 - i, i, this.depth - 1 - i, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
            this.generateBox(p_191981_, p_191985_, i + 1, i, i + 1, this.width - 2 - i, i, this.depth - 2 - i, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         }

         for(int k1 = 0; k1 < this.width; ++k1) {
            for(int j = 0; j < this.depth; ++j) {
               int k = -5;
               this.fillColumnDown(p_191981_, Blocks.SANDSTONE.defaultBlockState(), k1, -5, j, p_191985_);
            }
         }

         BlockState blockstate1 = Blocks.SANDSTONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.NORTH);
         BlockState blockstate2 = Blocks.SANDSTONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.SOUTH);
         BlockState blockstate3 = Blocks.SANDSTONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.EAST);
         BlockState blockstate = Blocks.SANDSTONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.WEST);
         this.generateBox(p_191981_, p_191985_, 0, 0, 0, 4, 9, 4, Blocks.SANDSTONE.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(p_191981_, p_191985_, 1, 10, 1, 3, 10, 3, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
         this.placeBlock(p_191981_, blockstate1, 2, 10, 0, p_191985_);
         this.placeBlock(p_191981_, blockstate2, 2, 10, 4, p_191985_);
         this.placeBlock(p_191981_, blockstate3, 0, 10, 2, p_191985_);
         this.placeBlock(p_191981_, blockstate, 4, 10, 2, p_191985_);
         this.generateBox(p_191981_, p_191985_, this.width - 5, 0, 0, this.width - 1, 9, 4, Blocks.SANDSTONE.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(p_191981_, p_191985_, this.width - 4, 10, 1, this.width - 2, 10, 3, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
         this.placeBlock(p_191981_, blockstate1, this.width - 3, 10, 0, p_191985_);
         this.placeBlock(p_191981_, blockstate2, this.width - 3, 10, 4, p_191985_);
         this.placeBlock(p_191981_, blockstate3, this.width - 5, 10, 2, p_191985_);
         this.placeBlock(p_191981_, blockstate, this.width - 1, 10, 2, p_191985_);
         this.generateBox(p_191981_, p_191985_, 8, 0, 0, 12, 4, 4, Blocks.SANDSTONE.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(p_191981_, p_191985_, 9, 1, 0, 11, 3, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.placeBlock(p_191981_, Blocks.CUT_SANDSTONE.defaultBlockState(), 9, 1, 1, p_191985_);
         this.placeBlock(p_191981_, Blocks.CUT_SANDSTONE.defaultBlockState(), 9, 2, 1, p_191985_);
         this.placeBlock(p_191981_, Blocks.CUT_SANDSTONE.defaultBlockState(), 9, 3, 1, p_191985_);
         this.placeBlock(p_191981_, Blocks.CUT_SANDSTONE.defaultBlockState(), 10, 3, 1, p_191985_);
         this.placeBlock(p_191981_, Blocks.CUT_SANDSTONE.defaultBlockState(), 11, 3, 1, p_191985_);
         this.placeBlock(p_191981_, Blocks.CUT_SANDSTONE.defaultBlockState(), 11, 2, 1, p_191985_);
         this.placeBlock(p_191981_, Blocks.CUT_SANDSTONE.defaultBlockState(), 11, 1, 1, p_191985_);
         this.generateBox(p_191981_, p_191985_, 4, 1, 1, 8, 3, 3, Blocks.SANDSTONE.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(p_191981_, p_191985_, 4, 1, 2, 8, 2, 2, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(p_191981_, p_191985_, 12, 1, 1, 16, 3, 3, Blocks.SANDSTONE.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(p_191981_, p_191985_, 12, 1, 2, 16, 2, 2, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(p_191981_, p_191985_, 5, 4, 5, this.width - 6, 4, this.depth - 6, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
         this.generateBox(p_191981_, p_191985_, 9, 4, 9, 11, 4, 11, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(p_191981_, p_191985_, 8, 1, 8, 8, 3, 8, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
         this.generateBox(p_191981_, p_191985_, 12, 1, 8, 12, 3, 8, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
         this.generateBox(p_191981_, p_191985_, 8, 1, 12, 8, 3, 12, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
         this.generateBox(p_191981_, p_191985_, 12, 1, 12, 12, 3, 12, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
         this.generateBox(p_191981_, p_191985_, 1, 1, 5, 4, 4, 11, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
         this.generateBox(p_191981_, p_191985_, this.width - 5, 1, 5, this.width - 2, 4, 11, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
         this.generateBox(p_191981_, p_191985_, 6, 7, 9, 6, 7, 11, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
         this.generateBox(p_191981_, p_191985_, this.width - 7, 7, 9, this.width - 7, 7, 11, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
         this.generateBox(p_191981_, p_191985_, 5, 5, 9, 5, 7, 11, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
         this.generateBox(p_191981_, p_191985_, this.width - 6, 5, 9, this.width - 6, 7, 11, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
         this.placeBlock(p_191981_, Blocks.AIR.defaultBlockState(), 5, 5, 10, p_191985_);
         this.placeBlock(p_191981_, Blocks.AIR.defaultBlockState(), 5, 6, 10, p_191985_);
         this.placeBlock(p_191981_, Blocks.AIR.defaultBlockState(), 6, 6, 10, p_191985_);
         this.placeBlock(p_191981_, Blocks.AIR.defaultBlockState(), this.width - 6, 5, 10, p_191985_);
         this.placeBlock(p_191981_, Blocks.AIR.defaultBlockState(), this.width - 6, 6, 10, p_191985_);
         this.placeBlock(p_191981_, Blocks.AIR.defaultBlockState(), this.width - 7, 6, 10, p_191985_);
         this.generateBox(p_191981_, p_191985_, 2, 4, 4, 2, 6, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(p_191981_, p_191985_, this.width - 3, 4, 4, this.width - 3, 6, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.placeBlock(p_191981_, blockstate1, 2, 4, 5, p_191985_);
         this.placeBlock(p_191981_, blockstate1, 2, 3, 4, p_191985_);
         this.placeBlock(p_191981_, blockstate1, this.width - 3, 4, 5, p_191985_);
         this.placeBlock(p_191981_, blockstate1, this.width - 3, 3, 4, p_191985_);
         this.generateBox(p_191981_, p_191985_, 1, 1, 3, 2, 2, 3, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
         this.generateBox(p_191981_, p_191985_, this.width - 3, 1, 3, this.width - 2, 2, 3, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
         this.placeBlock(p_191981_, Blocks.SANDSTONE.defaultBlockState(), 1, 1, 2, p_191985_);
         this.placeBlock(p_191981_, Blocks.SANDSTONE.defaultBlockState(), this.width - 2, 1, 2, p_191985_);
         this.placeBlock(p_191981_, Blocks.SANDSTONE_SLAB.defaultBlockState(), 1, 2, 2, p_191985_);
         this.placeBlock(p_191981_, Blocks.SANDSTONE_SLAB.defaultBlockState(), this.width - 2, 2, 2, p_191985_);
         this.placeBlock(p_191981_, blockstate, 2, 1, 2, p_191985_);
         this.placeBlock(p_191981_, blockstate3, this.width - 3, 1, 2, p_191985_);
         this.generateBox(p_191981_, p_191985_, 4, 3, 5, 4, 3, 17, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
         this.generateBox(p_191981_, p_191985_, this.width - 5, 3, 5, this.width - 5, 3, 17, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
         this.generateBox(p_191981_, p_191985_, 3, 1, 5, 4, 2, 16, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(p_191981_, p_191985_, this.width - 6, 1, 5, this.width - 5, 2, 16, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);

         for(int l = 5; l <= 17; l += 2) {
            this.placeBlock(p_191981_, Blocks.CUT_SANDSTONE.defaultBlockState(), 4, 1, l, p_191985_);
            this.placeBlock(p_191981_, Blocks.CHISELED_SANDSTONE.defaultBlockState(), 4, 2, l, p_191985_);
            this.placeBlock(p_191981_, Blocks.CUT_SANDSTONE.defaultBlockState(), this.width - 5, 1, l, p_191985_);
            this.placeBlock(p_191981_, Blocks.CHISELED_SANDSTONE.defaultBlockState(), this.width - 5, 2, l, p_191985_);
         }

         this.placeBlock(p_191981_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 10, 0, 7, p_191985_);
         this.placeBlock(p_191981_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 10, 0, 8, p_191985_);
         this.placeBlock(p_191981_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 9, 0, 9, p_191985_);
         this.placeBlock(p_191981_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 11, 0, 9, p_191985_);
         this.placeBlock(p_191981_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 8, 0, 10, p_191985_);
         this.placeBlock(p_191981_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 12, 0, 10, p_191985_);
         this.placeBlock(p_191981_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 7, 0, 10, p_191985_);
         this.placeBlock(p_191981_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 13, 0, 10, p_191985_);
         this.placeBlock(p_191981_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 9, 0, 11, p_191985_);
         this.placeBlock(p_191981_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 11, 0, 11, p_191985_);
         this.placeBlock(p_191981_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 10, 0, 12, p_191985_);
         this.placeBlock(p_191981_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 10, 0, 13, p_191985_);
         this.placeBlock(p_191981_, Blocks.BLUE_TERRACOTTA.defaultBlockState(), 10, 0, 10, p_191985_);

         for(int l1 = 0; l1 <= this.width - 1; l1 += this.width - 1) {
            this.placeBlock(p_191981_, Blocks.CUT_SANDSTONE.defaultBlockState(), l1, 2, 1, p_191985_);
            this.placeBlock(p_191981_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), l1, 2, 2, p_191985_);
            this.placeBlock(p_191981_, Blocks.CUT_SANDSTONE.defaultBlockState(), l1, 2, 3, p_191985_);
            this.placeBlock(p_191981_, Blocks.CUT_SANDSTONE.defaultBlockState(), l1, 3, 1, p_191985_);
            this.placeBlock(p_191981_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), l1, 3, 2, p_191985_);
            this.placeBlock(p_191981_, Blocks.CUT_SANDSTONE.defaultBlockState(), l1, 3, 3, p_191985_);
            this.placeBlock(p_191981_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), l1, 4, 1, p_191985_);
            this.placeBlock(p_191981_, Blocks.CHISELED_SANDSTONE.defaultBlockState(), l1, 4, 2, p_191985_);
            this.placeBlock(p_191981_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), l1, 4, 3, p_191985_);
            this.placeBlock(p_191981_, Blocks.CUT_SANDSTONE.defaultBlockState(), l1, 5, 1, p_191985_);
            this.placeBlock(p_191981_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), l1, 5, 2, p_191985_);
            this.placeBlock(p_191981_, Blocks.CUT_SANDSTONE.defaultBlockState(), l1, 5, 3, p_191985_);
            this.placeBlock(p_191981_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), l1, 6, 1, p_191985_);
            this.placeBlock(p_191981_, Blocks.CHISELED_SANDSTONE.defaultBlockState(), l1, 6, 2, p_191985_);
            this.placeBlock(p_191981_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), l1, 6, 3, p_191985_);
            this.placeBlock(p_191981_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), l1, 7, 1, p_191985_);
            this.placeBlock(p_191981_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), l1, 7, 2, p_191985_);
            this.placeBlock(p_191981_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), l1, 7, 3, p_191985_);
            this.placeBlock(p_191981_, Blocks.CUT_SANDSTONE.defaultBlockState(), l1, 8, 1, p_191985_);
            this.placeBlock(p_191981_, Blocks.CUT_SANDSTONE.defaultBlockState(), l1, 8, 2, p_191985_);
            this.placeBlock(p_191981_, Blocks.CUT_SANDSTONE.defaultBlockState(), l1, 8, 3, p_191985_);
         }

         for(int i2 = 2; i2 <= this.width - 3; i2 += this.width - 3 - 2) {
            this.placeBlock(p_191981_, Blocks.CUT_SANDSTONE.defaultBlockState(), i2 - 1, 2, 0, p_191985_);
            this.placeBlock(p_191981_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), i2, 2, 0, p_191985_);
            this.placeBlock(p_191981_, Blocks.CUT_SANDSTONE.defaultBlockState(), i2 + 1, 2, 0, p_191985_);
            this.placeBlock(p_191981_, Blocks.CUT_SANDSTONE.defaultBlockState(), i2 - 1, 3, 0, p_191985_);
            this.placeBlock(p_191981_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), i2, 3, 0, p_191985_);
            this.placeBlock(p_191981_, Blocks.CUT_SANDSTONE.defaultBlockState(), i2 + 1, 3, 0, p_191985_);
            this.placeBlock(p_191981_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), i2 - 1, 4, 0, p_191985_);
            this.placeBlock(p_191981_, Blocks.CHISELED_SANDSTONE.defaultBlockState(), i2, 4, 0, p_191985_);
            this.placeBlock(p_191981_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), i2 + 1, 4, 0, p_191985_);
            this.placeBlock(p_191981_, Blocks.CUT_SANDSTONE.defaultBlockState(), i2 - 1, 5, 0, p_191985_);
            this.placeBlock(p_191981_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), i2, 5, 0, p_191985_);
            this.placeBlock(p_191981_, Blocks.CUT_SANDSTONE.defaultBlockState(), i2 + 1, 5, 0, p_191985_);
            this.placeBlock(p_191981_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), i2 - 1, 6, 0, p_191985_);
            this.placeBlock(p_191981_, Blocks.CHISELED_SANDSTONE.defaultBlockState(), i2, 6, 0, p_191985_);
            this.placeBlock(p_191981_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), i2 + 1, 6, 0, p_191985_);
            this.placeBlock(p_191981_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), i2 - 1, 7, 0, p_191985_);
            this.placeBlock(p_191981_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), i2, 7, 0, p_191985_);
            this.placeBlock(p_191981_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), i2 + 1, 7, 0, p_191985_);
            this.placeBlock(p_191981_, Blocks.CUT_SANDSTONE.defaultBlockState(), i2 - 1, 8, 0, p_191985_);
            this.placeBlock(p_191981_, Blocks.CUT_SANDSTONE.defaultBlockState(), i2, 8, 0, p_191985_);
            this.placeBlock(p_191981_, Blocks.CUT_SANDSTONE.defaultBlockState(), i2 + 1, 8, 0, p_191985_);
         }

         this.generateBox(p_191981_, p_191985_, 8, 4, 0, 12, 6, 0, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
         this.placeBlock(p_191981_, Blocks.AIR.defaultBlockState(), 8, 6, 0, p_191985_);
         this.placeBlock(p_191981_, Blocks.AIR.defaultBlockState(), 12, 6, 0, p_191985_);
         this.placeBlock(p_191981_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 9, 5, 0, p_191985_);
         this.placeBlock(p_191981_, Blocks.CHISELED_SANDSTONE.defaultBlockState(), 10, 5, 0, p_191985_);
         this.placeBlock(p_191981_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 11, 5, 0, p_191985_);
         this.generateBox(p_191981_, p_191985_, 8, -14, 8, 12, -11, 12, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
         this.generateBox(p_191981_, p_191985_, 8, -10, 8, 12, -10, 12, Blocks.CHISELED_SANDSTONE.defaultBlockState(), Blocks.CHISELED_SANDSTONE.defaultBlockState(), false);
         this.generateBox(p_191981_, p_191985_, 8, -9, 8, 12, -9, 12, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
         this.generateBox(p_191981_, p_191985_, 8, -8, 8, 12, -1, 12, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
         this.generateBox(p_191981_, p_191985_, 9, -11, 9, 11, -1, 11, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.placeBlock(p_191981_, Blocks.STONE_PRESSURE_PLATE.defaultBlockState(), 10, -11, 10, p_191985_);
         this.generateBox(p_191981_, p_191985_, 9, -13, 9, 11, -13, 11, Blocks.TNT.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.placeBlock(p_191981_, Blocks.AIR.defaultBlockState(), 8, -11, 10, p_191985_);
         this.placeBlock(p_191981_, Blocks.AIR.defaultBlockState(), 8, -10, 10, p_191985_);
         this.placeBlock(p_191981_, Blocks.CHISELED_SANDSTONE.defaultBlockState(), 7, -10, 10, p_191985_);
         this.placeBlock(p_191981_, Blocks.CUT_SANDSTONE.defaultBlockState(), 7, -11, 10, p_191985_);
         this.placeBlock(p_191981_, Blocks.AIR.defaultBlockState(), 12, -11, 10, p_191985_);
         this.placeBlock(p_191981_, Blocks.AIR.defaultBlockState(), 12, -10, 10, p_191985_);
         this.placeBlock(p_191981_, Blocks.CHISELED_SANDSTONE.defaultBlockState(), 13, -10, 10, p_191985_);
         this.placeBlock(p_191981_, Blocks.CUT_SANDSTONE.defaultBlockState(), 13, -11, 10, p_191985_);
         this.placeBlock(p_191981_, Blocks.AIR.defaultBlockState(), 10, -11, 8, p_191985_);
         this.placeBlock(p_191981_, Blocks.AIR.defaultBlockState(), 10, -10, 8, p_191985_);
         this.placeBlock(p_191981_, Blocks.CHISELED_SANDSTONE.defaultBlockState(), 10, -10, 7, p_191985_);
         this.placeBlock(p_191981_, Blocks.CUT_SANDSTONE.defaultBlockState(), 10, -11, 7, p_191985_);
         this.placeBlock(p_191981_, Blocks.AIR.defaultBlockState(), 10, -11, 12, p_191985_);
         this.placeBlock(p_191981_, Blocks.AIR.defaultBlockState(), 10, -10, 12, p_191985_);
         this.placeBlock(p_191981_, Blocks.CHISELED_SANDSTONE.defaultBlockState(), 10, -10, 13, p_191985_);
         this.placeBlock(p_191981_, Blocks.CUT_SANDSTONE.defaultBlockState(), 10, -11, 13, p_191985_);

         for(Direction direction : Direction.Plane.HORIZONTAL) {
            if (!this.hasPlacedChest[direction.get2DDataValue()]) {
               int i1 = direction.getStepX() * 2;
               int j1 = direction.getStepZ() * 2;
               this.hasPlacedChest[direction.get2DDataValue()] = this.createChest(p_191981_, p_191985_, p_191984_, 10 + i1, -11, 10 + j1, BuiltInLootTables.DESERT_PYRAMID);
            }
         }

      }
   }
}