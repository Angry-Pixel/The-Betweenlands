package net.minecraft.world.level.levelgen.structure;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

public class SwamplandHutPiece extends ScatteredFeaturePiece {
   private boolean spawnedWitch;
   private boolean spawnedCat;

   public SwamplandHutPiece(Random p_73640_, int p_73641_, int p_73642_) {
      super(StructurePieceType.SWAMPLAND_HUT, p_73641_, 64, p_73642_, 7, 7, 9, getRandomHorizontalDirection(p_73640_));
   }

   public SwamplandHutPiece(CompoundTag p_192664_) {
      super(StructurePieceType.SWAMPLAND_HUT, p_192664_);
      this.spawnedWitch = p_192664_.getBoolean("Witch");
      this.spawnedCat = p_192664_.getBoolean("Cat");
   }

   protected void addAdditionalSaveData(StructurePieceSerializationContext p_192674_, CompoundTag p_192675_) {
      super.addAdditionalSaveData(p_192674_, p_192675_);
      p_192675_.putBoolean("Witch", this.spawnedWitch);
      p_192675_.putBoolean("Cat", this.spawnedCat);
   }

   public void postProcess(WorldGenLevel p_192666_, StructureFeatureManager p_192667_, ChunkGenerator p_192668_, Random p_192669_, BoundingBox p_192670_, ChunkPos p_192671_, BlockPos p_192672_) {
      if (this.updateAverageGroundHeight(p_192666_, p_192670_, 0)) {
         this.generateBox(p_192666_, p_192670_, 1, 1, 1, 5, 1, 7, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
         this.generateBox(p_192666_, p_192670_, 1, 4, 2, 5, 4, 7, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
         this.generateBox(p_192666_, p_192670_, 2, 1, 0, 4, 1, 0, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
         this.generateBox(p_192666_, p_192670_, 2, 2, 2, 3, 3, 2, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
         this.generateBox(p_192666_, p_192670_, 1, 2, 3, 1, 3, 6, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
         this.generateBox(p_192666_, p_192670_, 5, 2, 3, 5, 3, 6, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
         this.generateBox(p_192666_, p_192670_, 2, 2, 7, 4, 3, 7, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
         this.generateBox(p_192666_, p_192670_, 1, 0, 2, 1, 3, 2, Blocks.OAK_LOG.defaultBlockState(), Blocks.OAK_LOG.defaultBlockState(), false);
         this.generateBox(p_192666_, p_192670_, 5, 0, 2, 5, 3, 2, Blocks.OAK_LOG.defaultBlockState(), Blocks.OAK_LOG.defaultBlockState(), false);
         this.generateBox(p_192666_, p_192670_, 1, 0, 7, 1, 3, 7, Blocks.OAK_LOG.defaultBlockState(), Blocks.OAK_LOG.defaultBlockState(), false);
         this.generateBox(p_192666_, p_192670_, 5, 0, 7, 5, 3, 7, Blocks.OAK_LOG.defaultBlockState(), Blocks.OAK_LOG.defaultBlockState(), false);
         this.placeBlock(p_192666_, Blocks.OAK_FENCE.defaultBlockState(), 2, 3, 2, p_192670_);
         this.placeBlock(p_192666_, Blocks.OAK_FENCE.defaultBlockState(), 3, 3, 7, p_192670_);
         this.placeBlock(p_192666_, Blocks.AIR.defaultBlockState(), 1, 3, 4, p_192670_);
         this.placeBlock(p_192666_, Blocks.AIR.defaultBlockState(), 5, 3, 4, p_192670_);
         this.placeBlock(p_192666_, Blocks.AIR.defaultBlockState(), 5, 3, 5, p_192670_);
         this.placeBlock(p_192666_, Blocks.POTTED_RED_MUSHROOM.defaultBlockState(), 1, 3, 5, p_192670_);
         this.placeBlock(p_192666_, Blocks.CRAFTING_TABLE.defaultBlockState(), 3, 2, 6, p_192670_);
         this.placeBlock(p_192666_, Blocks.CAULDRON.defaultBlockState(), 4, 2, 6, p_192670_);
         this.placeBlock(p_192666_, Blocks.OAK_FENCE.defaultBlockState(), 1, 2, 1, p_192670_);
         this.placeBlock(p_192666_, Blocks.OAK_FENCE.defaultBlockState(), 5, 2, 1, p_192670_);
         BlockState blockstate = Blocks.SPRUCE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.NORTH);
         BlockState blockstate1 = Blocks.SPRUCE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.EAST);
         BlockState blockstate2 = Blocks.SPRUCE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.WEST);
         BlockState blockstate3 = Blocks.SPRUCE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.SOUTH);
         this.generateBox(p_192666_, p_192670_, 0, 4, 1, 6, 4, 1, blockstate, blockstate, false);
         this.generateBox(p_192666_, p_192670_, 0, 4, 2, 0, 4, 7, blockstate1, blockstate1, false);
         this.generateBox(p_192666_, p_192670_, 6, 4, 2, 6, 4, 7, blockstate2, blockstate2, false);
         this.generateBox(p_192666_, p_192670_, 0, 4, 8, 6, 4, 8, blockstate3, blockstate3, false);
         this.placeBlock(p_192666_, blockstate.setValue(StairBlock.SHAPE, StairsShape.OUTER_RIGHT), 0, 4, 1, p_192670_);
         this.placeBlock(p_192666_, blockstate.setValue(StairBlock.SHAPE, StairsShape.OUTER_LEFT), 6, 4, 1, p_192670_);
         this.placeBlock(p_192666_, blockstate3.setValue(StairBlock.SHAPE, StairsShape.OUTER_LEFT), 0, 4, 8, p_192670_);
         this.placeBlock(p_192666_, blockstate3.setValue(StairBlock.SHAPE, StairsShape.OUTER_RIGHT), 6, 4, 8, p_192670_);

         for(int i = 2; i <= 7; i += 5) {
            for(int j = 1; j <= 5; j += 4) {
               this.fillColumnDown(p_192666_, Blocks.OAK_LOG.defaultBlockState(), j, -1, i, p_192670_);
            }
         }

         if (!this.spawnedWitch) {
            BlockPos blockpos = this.getWorldPos(2, 2, 5);
            if (p_192670_.isInside(blockpos)) {
               this.spawnedWitch = true;
               Witch witch = EntityType.WITCH.create(p_192666_.getLevel());
               witch.setPersistenceRequired();
               witch.moveTo((double)blockpos.getX() + 0.5D, (double)blockpos.getY(), (double)blockpos.getZ() + 0.5D, 0.0F, 0.0F);
               witch.finalizeSpawn(p_192666_, p_192666_.getCurrentDifficultyAt(blockpos), MobSpawnType.STRUCTURE, (SpawnGroupData)null, (CompoundTag)null);
               p_192666_.addFreshEntityWithPassengers(witch);
            }
         }

         this.spawnCat(p_192666_, p_192670_);
      }
   }

   private void spawnCat(ServerLevelAccessor p_73644_, BoundingBox p_73645_) {
      if (!this.spawnedCat) {
         BlockPos blockpos = this.getWorldPos(2, 2, 5);
         if (p_73645_.isInside(blockpos)) {
            this.spawnedCat = true;
            Cat cat = EntityType.CAT.create(p_73644_.getLevel());
            cat.setPersistenceRequired();
            cat.moveTo((double)blockpos.getX() + 0.5D, (double)blockpos.getY(), (double)blockpos.getZ() + 0.5D, 0.0F, 0.0F);
            cat.finalizeSpawn(p_73644_, p_73644_.getCurrentDifficultyAt(blockpos), MobSpawnType.STRUCTURE, (SpawnGroupData)null, (CompoundTag)null);
            p_73644_.addFreshEntityWithPassengers(cat);
         }
      }

   }
}