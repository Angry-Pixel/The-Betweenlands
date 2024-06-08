package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class IglooPieces {
   public static final int GENERATION_HEIGHT = 90;
   static final ResourceLocation STRUCTURE_LOCATION_IGLOO = new ResourceLocation("igloo/top");
   private static final ResourceLocation STRUCTURE_LOCATION_LADDER = new ResourceLocation("igloo/middle");
   private static final ResourceLocation STRUCTURE_LOCATION_LABORATORY = new ResourceLocation("igloo/bottom");
   static final Map<ResourceLocation, BlockPos> PIVOTS = ImmutableMap.of(STRUCTURE_LOCATION_IGLOO, new BlockPos(3, 5, 5), STRUCTURE_LOCATION_LADDER, new BlockPos(1, 3, 1), STRUCTURE_LOCATION_LABORATORY, new BlockPos(3, 6, 7));
   static final Map<ResourceLocation, BlockPos> OFFSETS = ImmutableMap.of(STRUCTURE_LOCATION_IGLOO, BlockPos.ZERO, STRUCTURE_LOCATION_LADDER, new BlockPos(2, -3, 4), STRUCTURE_LOCATION_LABORATORY, new BlockPos(0, -3, -2));

   public static void addPieces(StructureManager p_162435_, BlockPos p_162436_, Rotation p_162437_, StructurePieceAccessor p_162438_, Random p_162439_) {
      if (p_162439_.nextDouble() < 0.5D) {
         int i = p_162439_.nextInt(8) + 4;
         p_162438_.addPiece(new IglooPieces.IglooPiece(p_162435_, STRUCTURE_LOCATION_LABORATORY, p_162436_, p_162437_, i * 3));

         for(int j = 0; j < i - 1; ++j) {
            p_162438_.addPiece(new IglooPieces.IglooPiece(p_162435_, STRUCTURE_LOCATION_LADDER, p_162436_, p_162437_, j * 3));
         }
      }

      p_162438_.addPiece(new IglooPieces.IglooPiece(p_162435_, STRUCTURE_LOCATION_IGLOO, p_162436_, p_162437_, 0));
   }

   public static class IglooPiece extends TemplateStructurePiece {
      public IglooPiece(StructureManager p_71244_, ResourceLocation p_71245_, BlockPos p_71246_, Rotation p_71247_, int p_71248_) {
         super(StructurePieceType.IGLOO, 0, p_71244_, p_71245_, p_71245_.toString(), makeSettings(p_71247_, p_71245_), makePosition(p_71245_, p_71246_, p_71248_));
      }

      public IglooPiece(StructureManager p_191998_, CompoundTag p_191999_) {
         super(StructurePieceType.IGLOO, p_191999_, p_191998_, (p_162451_) -> {
            return makeSettings(Rotation.valueOf(p_191999_.getString("Rot")), p_162451_);
         });
      }

      private static StructurePlaceSettings makeSettings(Rotation p_162447_, ResourceLocation p_162448_) {
         return (new StructurePlaceSettings()).setRotation(p_162447_).setMirror(Mirror.NONE).setRotationPivot(IglooPieces.PIVOTS.get(p_162448_)).addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK);
      }

      private static BlockPos makePosition(ResourceLocation p_162453_, BlockPos p_162454_, int p_162455_) {
         return p_162454_.offset(IglooPieces.OFFSETS.get(p_162453_)).below(p_162455_);
      }

      protected void addAdditionalSaveData(StructurePieceSerializationContext p_192009_, CompoundTag p_192010_) {
         super.addAdditionalSaveData(p_192009_, p_192010_);
         p_192010_.putString("Rot", this.placeSettings.getRotation().name());
      }

      protected void handleDataMarker(String p_71260_, BlockPos p_71261_, ServerLevelAccessor p_71262_, Random p_71263_, BoundingBox p_71264_) {
         if ("chest".equals(p_71260_)) {
            p_71262_.setBlock(p_71261_, Blocks.AIR.defaultBlockState(), 3);
            BlockEntity blockentity = p_71262_.getBlockEntity(p_71261_.below());
            if (blockentity instanceof ChestBlockEntity) {
               ((ChestBlockEntity)blockentity).setLootTable(BuiltInLootTables.IGLOO_CHEST, p_71263_.nextLong());
            }

         }
      }

      public void postProcess(WorldGenLevel p_192001_, StructureFeatureManager p_192002_, ChunkGenerator p_192003_, Random p_192004_, BoundingBox p_192005_, ChunkPos p_192006_, BlockPos p_192007_) {
         ResourceLocation resourcelocation = new ResourceLocation(this.templateName);
         StructurePlaceSettings structureplacesettings = makeSettings(this.placeSettings.getRotation(), resourcelocation);
         BlockPos blockpos = IglooPieces.OFFSETS.get(resourcelocation);
         BlockPos blockpos1 = this.templatePosition.offset(StructureTemplate.calculateRelativePosition(structureplacesettings, new BlockPos(3 - blockpos.getX(), 0, -blockpos.getZ())));
         int i = p_192001_.getHeight(Heightmap.Types.WORLD_SURFACE_WG, blockpos1.getX(), blockpos1.getZ());
         BlockPos blockpos2 = this.templatePosition;
         this.templatePosition = this.templatePosition.offset(0, i - 90 - 1, 0);
         super.postProcess(p_192001_, p_192002_, p_192003_, p_192004_, p_192005_, p_192006_, p_192007_);
         if (resourcelocation.equals(IglooPieces.STRUCTURE_LOCATION_IGLOO)) {
            BlockPos blockpos3 = this.templatePosition.offset(StructureTemplate.calculateRelativePosition(structureplacesettings, new BlockPos(3, 0, 5)));
            BlockState blockstate = p_192001_.getBlockState(blockpos3.below());
            if (!blockstate.isAir() && !blockstate.is(Blocks.LADDER)) {
               p_192001_.setBlock(blockpos3, Blocks.SNOW_BLOCK.defaultBlockState(), 3);
            }
         }

         this.templatePosition = blockpos2;
      }
   }
}