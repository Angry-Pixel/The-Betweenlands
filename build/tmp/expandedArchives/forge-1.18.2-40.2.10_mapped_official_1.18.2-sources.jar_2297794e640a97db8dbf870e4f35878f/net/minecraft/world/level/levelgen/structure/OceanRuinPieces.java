package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.OceanRuinConfiguration;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockRotProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class OceanRuinPieces {
   private static final ResourceLocation[] WARM_RUINS = new ResourceLocation[]{new ResourceLocation("underwater_ruin/warm_1"), new ResourceLocation("underwater_ruin/warm_2"), new ResourceLocation("underwater_ruin/warm_3"), new ResourceLocation("underwater_ruin/warm_4"), new ResourceLocation("underwater_ruin/warm_5"), new ResourceLocation("underwater_ruin/warm_6"), new ResourceLocation("underwater_ruin/warm_7"), new ResourceLocation("underwater_ruin/warm_8")};
   private static final ResourceLocation[] RUINS_BRICK = new ResourceLocation[]{new ResourceLocation("underwater_ruin/brick_1"), new ResourceLocation("underwater_ruin/brick_2"), new ResourceLocation("underwater_ruin/brick_3"), new ResourceLocation("underwater_ruin/brick_4"), new ResourceLocation("underwater_ruin/brick_5"), new ResourceLocation("underwater_ruin/brick_6"), new ResourceLocation("underwater_ruin/brick_7"), new ResourceLocation("underwater_ruin/brick_8")};
   private static final ResourceLocation[] RUINS_CRACKED = new ResourceLocation[]{new ResourceLocation("underwater_ruin/cracked_1"), new ResourceLocation("underwater_ruin/cracked_2"), new ResourceLocation("underwater_ruin/cracked_3"), new ResourceLocation("underwater_ruin/cracked_4"), new ResourceLocation("underwater_ruin/cracked_5"), new ResourceLocation("underwater_ruin/cracked_6"), new ResourceLocation("underwater_ruin/cracked_7"), new ResourceLocation("underwater_ruin/cracked_8")};
   private static final ResourceLocation[] RUINS_MOSSY = new ResourceLocation[]{new ResourceLocation("underwater_ruin/mossy_1"), new ResourceLocation("underwater_ruin/mossy_2"), new ResourceLocation("underwater_ruin/mossy_3"), new ResourceLocation("underwater_ruin/mossy_4"), new ResourceLocation("underwater_ruin/mossy_5"), new ResourceLocation("underwater_ruin/mossy_6"), new ResourceLocation("underwater_ruin/mossy_7"), new ResourceLocation("underwater_ruin/mossy_8")};
   private static final ResourceLocation[] BIG_RUINS_BRICK = new ResourceLocation[]{new ResourceLocation("underwater_ruin/big_brick_1"), new ResourceLocation("underwater_ruin/big_brick_2"), new ResourceLocation("underwater_ruin/big_brick_3"), new ResourceLocation("underwater_ruin/big_brick_8")};
   private static final ResourceLocation[] BIG_RUINS_MOSSY = new ResourceLocation[]{new ResourceLocation("underwater_ruin/big_mossy_1"), new ResourceLocation("underwater_ruin/big_mossy_2"), new ResourceLocation("underwater_ruin/big_mossy_3"), new ResourceLocation("underwater_ruin/big_mossy_8")};
   private static final ResourceLocation[] BIG_RUINS_CRACKED = new ResourceLocation[]{new ResourceLocation("underwater_ruin/big_cracked_1"), new ResourceLocation("underwater_ruin/big_cracked_2"), new ResourceLocation("underwater_ruin/big_cracked_3"), new ResourceLocation("underwater_ruin/big_cracked_8")};
   private static final ResourceLocation[] BIG_WARM_RUINS = new ResourceLocation[]{new ResourceLocation("underwater_ruin/big_warm_4"), new ResourceLocation("underwater_ruin/big_warm_5"), new ResourceLocation("underwater_ruin/big_warm_6"), new ResourceLocation("underwater_ruin/big_warm_7")};

   private static ResourceLocation getSmallWarmRuin(Random p_72552_) {
      return Util.getRandom(WARM_RUINS, p_72552_);
   }

   private static ResourceLocation getBigWarmRuin(Random p_72558_) {
      return Util.getRandom(BIG_WARM_RUINS, p_72558_);
   }

   public static void addPieces(StructureManager p_163079_, BlockPos p_163080_, Rotation p_163081_, StructurePieceAccessor p_163082_, Random p_163083_, OceanRuinConfiguration p_163084_) {
      boolean flag = p_163083_.nextFloat() <= p_163084_.largeProbability;
      float f = flag ? 0.9F : 0.8F;
      addPiece(p_163079_, p_163080_, p_163081_, p_163082_, p_163083_, p_163084_, flag, f);
      if (flag && p_163083_.nextFloat() <= p_163084_.clusterProbability) {
         addClusterRuins(p_163079_, p_163083_, p_163081_, p_163080_, p_163084_, p_163082_);
      }

   }

   private static void addClusterRuins(StructureManager p_163095_, Random p_163096_, Rotation p_163097_, BlockPos p_163098_, OceanRuinConfiguration p_163099_, StructurePieceAccessor p_163100_) {
      BlockPos blockpos = new BlockPos(p_163098_.getX(), 90, p_163098_.getZ());
      BlockPos blockpos1 = StructureTemplate.transform(new BlockPos(15, 0, 15), Mirror.NONE, p_163097_, BlockPos.ZERO).offset(blockpos);
      BoundingBox boundingbox = BoundingBox.fromCorners(blockpos, blockpos1);
      BlockPos blockpos2 = new BlockPos(Math.min(blockpos.getX(), blockpos1.getX()), blockpos.getY(), Math.min(blockpos.getZ(), blockpos1.getZ()));
      List<BlockPos> list = allPositions(p_163096_, blockpos2);
      int i = Mth.nextInt(p_163096_, 4, 8);

      for(int j = 0; j < i; ++j) {
         if (!list.isEmpty()) {
            int k = p_163096_.nextInt(list.size());
            BlockPos blockpos3 = list.remove(k);
            Rotation rotation = Rotation.getRandom(p_163096_);
            BlockPos blockpos4 = StructureTemplate.transform(new BlockPos(5, 0, 6), Mirror.NONE, rotation, BlockPos.ZERO).offset(blockpos3);
            BoundingBox boundingbox1 = BoundingBox.fromCorners(blockpos3, blockpos4);
            if (!boundingbox1.intersects(boundingbox)) {
               addPiece(p_163095_, blockpos3, rotation, p_163100_, p_163096_, p_163099_, false, 0.8F);
            }
         }
      }

   }

   private static List<BlockPos> allPositions(Random p_163102_, BlockPos p_163103_) {
      List<BlockPos> list = Lists.newArrayList();
      list.add(p_163103_.offset(-16 + Mth.nextInt(p_163102_, 1, 8), 0, 16 + Mth.nextInt(p_163102_, 1, 7)));
      list.add(p_163103_.offset(-16 + Mth.nextInt(p_163102_, 1, 8), 0, Mth.nextInt(p_163102_, 1, 7)));
      list.add(p_163103_.offset(-16 + Mth.nextInt(p_163102_, 1, 8), 0, -16 + Mth.nextInt(p_163102_, 4, 8)));
      list.add(p_163103_.offset(Mth.nextInt(p_163102_, 1, 7), 0, 16 + Mth.nextInt(p_163102_, 1, 7)));
      list.add(p_163103_.offset(Mth.nextInt(p_163102_, 1, 7), 0, -16 + Mth.nextInt(p_163102_, 4, 6)));
      list.add(p_163103_.offset(16 + Mth.nextInt(p_163102_, 1, 7), 0, 16 + Mth.nextInt(p_163102_, 3, 8)));
      list.add(p_163103_.offset(16 + Mth.nextInt(p_163102_, 1, 7), 0, Mth.nextInt(p_163102_, 1, 7)));
      list.add(p_163103_.offset(16 + Mth.nextInt(p_163102_, 1, 7), 0, -16 + Mth.nextInt(p_163102_, 4, 8)));
      return list;
   }

   private static void addPiece(StructureManager p_163086_, BlockPos p_163087_, Rotation p_163088_, StructurePieceAccessor p_163089_, Random p_163090_, OceanRuinConfiguration p_163091_, boolean p_163092_, float p_163093_) {
      switch(p_163091_.biomeTemp) {
      case WARM:
      default:
         ResourceLocation resourcelocation = p_163092_ ? getBigWarmRuin(p_163090_) : getSmallWarmRuin(p_163090_);
         p_163089_.addPiece(new OceanRuinPieces.OceanRuinPiece(p_163086_, resourcelocation, p_163087_, p_163088_, p_163093_, p_163091_.biomeTemp, p_163092_));
         break;
      case COLD:
         ResourceLocation[] aresourcelocation = p_163092_ ? BIG_RUINS_BRICK : RUINS_BRICK;
         ResourceLocation[] aresourcelocation1 = p_163092_ ? BIG_RUINS_CRACKED : RUINS_CRACKED;
         ResourceLocation[] aresourcelocation2 = p_163092_ ? BIG_RUINS_MOSSY : RUINS_MOSSY;
         int i = p_163090_.nextInt(aresourcelocation.length);
         p_163089_.addPiece(new OceanRuinPieces.OceanRuinPiece(p_163086_, aresourcelocation[i], p_163087_, p_163088_, p_163093_, p_163091_.biomeTemp, p_163092_));
         p_163089_.addPiece(new OceanRuinPieces.OceanRuinPiece(p_163086_, aresourcelocation1[i], p_163087_, p_163088_, 0.7F, p_163091_.biomeTemp, p_163092_));
         p_163089_.addPiece(new OceanRuinPieces.OceanRuinPiece(p_163086_, aresourcelocation2[i], p_163087_, p_163088_, 0.5F, p_163091_.biomeTemp, p_163092_));
      }

   }

   public static class OceanRuinPiece extends TemplateStructurePiece {
      private final OceanRuinFeature.Type biomeType;
      private final float integrity;
      private final boolean isLarge;

      public OceanRuinPiece(StructureManager p_72568_, ResourceLocation p_72569_, BlockPos p_72570_, Rotation p_72571_, float p_72572_, OceanRuinFeature.Type p_72573_, boolean p_72574_) {
         super(StructurePieceType.OCEAN_RUIN, 0, p_72568_, p_72569_, p_72569_.toString(), makeSettings(p_72571_), p_72570_);
         this.integrity = p_72572_;
         this.biomeType = p_72573_;
         this.isLarge = p_72574_;
      }

      public OceanRuinPiece(StructureManager p_192392_, CompoundTag p_192393_) {
         super(StructurePieceType.OCEAN_RUIN, p_192393_, p_192392_, (p_163116_) -> {
            return makeSettings(Rotation.valueOf(p_192393_.getString("Rot")));
         });
         this.integrity = p_192393_.getFloat("Integrity");
         this.biomeType = OceanRuinFeature.Type.valueOf(p_192393_.getString("BiomeType"));
         this.isLarge = p_192393_.getBoolean("IsLarge");
      }

      private static StructurePlaceSettings makeSettings(Rotation p_163113_) {
         return (new StructurePlaceSettings()).setRotation(p_163113_).setMirror(Mirror.NONE).addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR);
      }

      protected void addAdditionalSaveData(StructurePieceSerializationContext p_192403_, CompoundTag p_192404_) {
         super.addAdditionalSaveData(p_192403_, p_192404_);
         p_192404_.putString("Rot", this.placeSettings.getRotation().name());
         p_192404_.putFloat("Integrity", this.integrity);
         p_192404_.putString("BiomeType", this.biomeType.toString());
         p_192404_.putBoolean("IsLarge", this.isLarge);
      }

      protected void handleDataMarker(String p_72590_, BlockPos p_72591_, ServerLevelAccessor p_72592_, Random p_72593_, BoundingBox p_72594_) {
         if ("chest".equals(p_72590_)) {
            p_72592_.setBlock(p_72591_, Blocks.CHEST.defaultBlockState().setValue(ChestBlock.WATERLOGGED, Boolean.valueOf(p_72592_.getFluidState(p_72591_).is(FluidTags.WATER))), 2);
            BlockEntity blockentity = p_72592_.getBlockEntity(p_72591_);
            if (blockentity instanceof ChestBlockEntity) {
               ((ChestBlockEntity)blockentity).setLootTable(this.isLarge ? BuiltInLootTables.UNDERWATER_RUIN_BIG : BuiltInLootTables.UNDERWATER_RUIN_SMALL, p_72593_.nextLong());
            }
         } else if ("drowned".equals(p_72590_)) {
            Drowned drowned = EntityType.DROWNED.create(p_72592_.getLevel());
            drowned.setPersistenceRequired();
            drowned.moveTo(p_72591_, 0.0F, 0.0F);
            drowned.finalizeSpawn(p_72592_, p_72592_.getCurrentDifficultyAt(p_72591_), MobSpawnType.STRUCTURE, (SpawnGroupData)null, (CompoundTag)null);
            p_72592_.addFreshEntityWithPassengers(drowned);
            if (p_72591_.getY() > p_72592_.getSeaLevel()) {
               p_72592_.setBlock(p_72591_, Blocks.AIR.defaultBlockState(), 2);
            } else {
               p_72592_.setBlock(p_72591_, Blocks.WATER.defaultBlockState(), 2);
            }
         }

      }

      public void postProcess(WorldGenLevel p_192395_, StructureFeatureManager p_192396_, ChunkGenerator p_192397_, Random p_192398_, BoundingBox p_192399_, ChunkPos p_192400_, BlockPos p_192401_) {
         this.placeSettings.clearProcessors().addProcessor(new BlockRotProcessor(this.integrity)).addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR);
         int i = p_192395_.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, this.templatePosition.getX(), this.templatePosition.getZ());
         this.templatePosition = new BlockPos(this.templatePosition.getX(), i, this.templatePosition.getZ());
         BlockPos blockpos = StructureTemplate.transform(new BlockPos(this.template.getSize().getX() - 1, 0, this.template.getSize().getZ() - 1), Mirror.NONE, this.placeSettings.getRotation(), BlockPos.ZERO).offset(this.templatePosition);
         this.templatePosition = new BlockPos(this.templatePosition.getX(), this.getHeight(this.templatePosition, p_192395_, blockpos), this.templatePosition.getZ());
         super.postProcess(p_192395_, p_192396_, p_192397_, p_192398_, p_192399_, p_192400_, p_192401_);
      }

      private int getHeight(BlockPos p_72586_, BlockGetter p_72587_, BlockPos p_72588_) {
         int i = p_72586_.getY();
         int j = 512;
         int k = i - 1;
         int l = 0;

         for(BlockPos blockpos : BlockPos.betweenClosed(p_72586_, p_72588_)) {
            int i1 = blockpos.getX();
            int j1 = blockpos.getZ();
            int k1 = p_72586_.getY() - 1;
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(i1, k1, j1);
            BlockState blockstate = p_72587_.getBlockState(blockpos$mutableblockpos);

            for(FluidState fluidstate = p_72587_.getFluidState(blockpos$mutableblockpos); (blockstate.isAir() || fluidstate.is(FluidTags.WATER) || blockstate.is(BlockTags.ICE)) && k1 > p_72587_.getMinBuildHeight() + 1; fluidstate = p_72587_.getFluidState(blockpos$mutableblockpos)) {
               --k1;
               blockpos$mutableblockpos.set(i1, k1, j1);
               blockstate = p_72587_.getBlockState(blockpos$mutableblockpos);
            }

            j = Math.min(j, k1);
            if (k1 < k - 2) {
               ++l;
            }
         }

         int l1 = Math.abs(p_72586_.getX() - p_72588_.getX());
         if (k - j > 2 && l > l1 - 2) {
            i = j + 1;
         }

         return i;
      }
   }
}