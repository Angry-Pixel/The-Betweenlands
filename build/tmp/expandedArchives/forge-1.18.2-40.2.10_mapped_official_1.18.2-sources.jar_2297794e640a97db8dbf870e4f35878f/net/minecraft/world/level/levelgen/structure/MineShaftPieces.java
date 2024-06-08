package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.MinecartChest;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.RailBlock;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.MineshaftFeature;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import org.slf4j.Logger;

public class MineShaftPieces {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final int DEFAULT_SHAFT_WIDTH = 3;
   private static final int DEFAULT_SHAFT_HEIGHT = 3;
   private static final int DEFAULT_SHAFT_LENGTH = 5;
   private static final int MAX_PILLAR_HEIGHT = 20;
   private static final int MAX_CHAIN_HEIGHT = 50;
   private static final int MAX_DEPTH = 8;
   public static final int MAGIC_START_Y = 50;

   private static MineShaftPieces.MineShaftPiece createRandomShaftPiece(StructurePieceAccessor p_162481_, Random p_162482_, int p_162483_, int p_162484_, int p_162485_, @Nullable Direction p_162486_, int p_162487_, MineshaftFeature.Type p_162488_) {
      int i = p_162482_.nextInt(100);
      if (i >= 80) {
         BoundingBox boundingbox = MineShaftPieces.MineShaftCrossing.findCrossing(p_162481_, p_162482_, p_162483_, p_162484_, p_162485_, p_162486_);
         if (boundingbox != null) {
            return new MineShaftPieces.MineShaftCrossing(p_162487_, boundingbox, p_162486_, p_162488_);
         }
      } else if (i >= 70) {
         BoundingBox boundingbox1 = MineShaftPieces.MineShaftStairs.findStairs(p_162481_, p_162482_, p_162483_, p_162484_, p_162485_, p_162486_);
         if (boundingbox1 != null) {
            return new MineShaftPieces.MineShaftStairs(p_162487_, boundingbox1, p_162486_, p_162488_);
         }
      } else {
         BoundingBox boundingbox2 = MineShaftPieces.MineShaftCorridor.findCorridorSize(p_162481_, p_162482_, p_162483_, p_162484_, p_162485_, p_162486_);
         if (boundingbox2 != null) {
            return new MineShaftPieces.MineShaftCorridor(p_162487_, p_162482_, boundingbox2, p_162486_, p_162488_);
         }
      }

      return null;
   }

   static MineShaftPieces.MineShaftPiece generateAndAddPiece(StructurePiece p_162472_, StructurePieceAccessor p_162473_, Random p_162474_, int p_162475_, int p_162476_, int p_162477_, Direction p_162478_, int p_162479_) {
      if (p_162479_ > 8) {
         return null;
      } else if (Math.abs(p_162475_ - p_162472_.getBoundingBox().minX()) <= 80 && Math.abs(p_162477_ - p_162472_.getBoundingBox().minZ()) <= 80) {
         MineshaftFeature.Type mineshaftfeature$type = ((MineShaftPieces.MineShaftPiece)p_162472_).type;
         MineShaftPieces.MineShaftPiece mineshaftpieces$mineshaftpiece = createRandomShaftPiece(p_162473_, p_162474_, p_162475_, p_162476_, p_162477_, p_162478_, p_162479_ + 1, mineshaftfeature$type);
         if (mineshaftpieces$mineshaftpiece != null) {
            p_162473_.addPiece(mineshaftpieces$mineshaftpiece);
            mineshaftpieces$mineshaftpiece.addChildren(p_162472_, p_162473_, p_162474_);
         }

         return mineshaftpieces$mineshaftpiece;
      } else {
         return null;
      }
   }

   public static class MineShaftCorridor extends MineShaftPieces.MineShaftPiece {
      private final boolean hasRails;
      private final boolean spiderCorridor;
      private boolean hasPlacedSpider;
      private final int numSections;

      public MineShaftCorridor(CompoundTag p_192028_) {
         super(StructurePieceType.MINE_SHAFT_CORRIDOR, p_192028_);
         this.hasRails = p_192028_.getBoolean("hr");
         this.spiderCorridor = p_192028_.getBoolean("sc");
         this.hasPlacedSpider = p_192028_.getBoolean("hps");
         this.numSections = p_192028_.getInt("Num");
      }

      protected void addAdditionalSaveData(StructurePieceSerializationContext p_192038_, CompoundTag p_192039_) {
         super.addAdditionalSaveData(p_192038_, p_192039_);
         p_192039_.putBoolean("hr", this.hasRails);
         p_192039_.putBoolean("sc", this.spiderCorridor);
         p_192039_.putBoolean("hps", this.hasPlacedSpider);
         p_192039_.putInt("Num", this.numSections);
      }

      public MineShaftCorridor(int p_71373_, Random p_71374_, BoundingBox p_71375_, Direction p_71376_, MineshaftFeature.Type p_71377_) {
         super(StructurePieceType.MINE_SHAFT_CORRIDOR, p_71373_, p_71377_, p_71375_);
         this.setOrientation(p_71376_);
         this.hasRails = p_71374_.nextInt(3) == 0;
         this.spiderCorridor = !this.hasRails && p_71374_.nextInt(23) == 0;
         if (this.getOrientation().getAxis() == Direction.Axis.Z) {
            this.numSections = p_71375_.getZSpan() / 5;
         } else {
            this.numSections = p_71375_.getXSpan() / 5;
         }

      }

      @Nullable
      public static BoundingBox findCorridorSize(StructurePieceAccessor p_162538_, Random p_162539_, int p_162540_, int p_162541_, int p_162542_, Direction p_162543_) {
         for(int i = p_162539_.nextInt(3) + 2; i > 0; --i) {
            int j = i * 5;
            BoundingBox boundingbox;
            switch(p_162543_) {
            case NORTH:
            default:
               boundingbox = new BoundingBox(0, 0, -(j - 1), 2, 2, 0);
               break;
            case SOUTH:
               boundingbox = new BoundingBox(0, 0, 0, 2, 2, j - 1);
               break;
            case WEST:
               boundingbox = new BoundingBox(-(j - 1), 0, 0, 0, 2, 2);
               break;
            case EAST:
               boundingbox = new BoundingBox(0, 0, 0, j - 1, 2, 2);
            }

            boundingbox.move(p_162540_, p_162541_, p_162542_);
            if (p_162538_.findCollisionPiece(boundingbox) == null) {
               return boundingbox;
            }
         }

         return null;
      }

      public void addChildren(StructurePiece p_162534_, StructurePieceAccessor p_162535_, Random p_162536_) {
         int i = this.getGenDepth();
         int j = p_162536_.nextInt(4);
         Direction direction = this.getOrientation();
         if (direction != null) {
            switch(direction) {
            case NORTH:
            default:
               if (j <= 1) {
                  MineShaftPieces.generateAndAddPiece(p_162534_, p_162535_, p_162536_, this.boundingBox.minX(), this.boundingBox.minY() - 1 + p_162536_.nextInt(3), this.boundingBox.minZ() - 1, direction, i);
               } else if (j == 2) {
                  MineShaftPieces.generateAndAddPiece(p_162534_, p_162535_, p_162536_, this.boundingBox.minX() - 1, this.boundingBox.minY() - 1 + p_162536_.nextInt(3), this.boundingBox.minZ(), Direction.WEST, i);
               } else {
                  MineShaftPieces.generateAndAddPiece(p_162534_, p_162535_, p_162536_, this.boundingBox.maxX() + 1, this.boundingBox.minY() - 1 + p_162536_.nextInt(3), this.boundingBox.minZ(), Direction.EAST, i);
               }
               break;
            case SOUTH:
               if (j <= 1) {
                  MineShaftPieces.generateAndAddPiece(p_162534_, p_162535_, p_162536_, this.boundingBox.minX(), this.boundingBox.minY() - 1 + p_162536_.nextInt(3), this.boundingBox.maxZ() + 1, direction, i);
               } else if (j == 2) {
                  MineShaftPieces.generateAndAddPiece(p_162534_, p_162535_, p_162536_, this.boundingBox.minX() - 1, this.boundingBox.minY() - 1 + p_162536_.nextInt(3), this.boundingBox.maxZ() - 3, Direction.WEST, i);
               } else {
                  MineShaftPieces.generateAndAddPiece(p_162534_, p_162535_, p_162536_, this.boundingBox.maxX() + 1, this.boundingBox.minY() - 1 + p_162536_.nextInt(3), this.boundingBox.maxZ() - 3, Direction.EAST, i);
               }
               break;
            case WEST:
               if (j <= 1) {
                  MineShaftPieces.generateAndAddPiece(p_162534_, p_162535_, p_162536_, this.boundingBox.minX() - 1, this.boundingBox.minY() - 1 + p_162536_.nextInt(3), this.boundingBox.minZ(), direction, i);
               } else if (j == 2) {
                  MineShaftPieces.generateAndAddPiece(p_162534_, p_162535_, p_162536_, this.boundingBox.minX(), this.boundingBox.minY() - 1 + p_162536_.nextInt(3), this.boundingBox.minZ() - 1, Direction.NORTH, i);
               } else {
                  MineShaftPieces.generateAndAddPiece(p_162534_, p_162535_, p_162536_, this.boundingBox.minX(), this.boundingBox.minY() - 1 + p_162536_.nextInt(3), this.boundingBox.maxZ() + 1, Direction.SOUTH, i);
               }
               break;
            case EAST:
               if (j <= 1) {
                  MineShaftPieces.generateAndAddPiece(p_162534_, p_162535_, p_162536_, this.boundingBox.maxX() + 1, this.boundingBox.minY() - 1 + p_162536_.nextInt(3), this.boundingBox.minZ(), direction, i);
               } else if (j == 2) {
                  MineShaftPieces.generateAndAddPiece(p_162534_, p_162535_, p_162536_, this.boundingBox.maxX() - 3, this.boundingBox.minY() - 1 + p_162536_.nextInt(3), this.boundingBox.minZ() - 1, Direction.NORTH, i);
               } else {
                  MineShaftPieces.generateAndAddPiece(p_162534_, p_162535_, p_162536_, this.boundingBox.maxX() - 3, this.boundingBox.minY() - 1 + p_162536_.nextInt(3), this.boundingBox.maxZ() + 1, Direction.SOUTH, i);
               }
            }
         }

         if (i < 8) {
            if (direction != Direction.NORTH && direction != Direction.SOUTH) {
               for(int i1 = this.boundingBox.minX() + 3; i1 + 3 <= this.boundingBox.maxX(); i1 += 5) {
                  int j1 = p_162536_.nextInt(5);
                  if (j1 == 0) {
                     MineShaftPieces.generateAndAddPiece(p_162534_, p_162535_, p_162536_, i1, this.boundingBox.minY(), this.boundingBox.minZ() - 1, Direction.NORTH, i + 1);
                  } else if (j1 == 1) {
                     MineShaftPieces.generateAndAddPiece(p_162534_, p_162535_, p_162536_, i1, this.boundingBox.minY(), this.boundingBox.maxZ() + 1, Direction.SOUTH, i + 1);
                  }
               }
            } else {
               for(int k = this.boundingBox.minZ() + 3; k + 3 <= this.boundingBox.maxZ(); k += 5) {
                  int l = p_162536_.nextInt(5);
                  if (l == 0) {
                     MineShaftPieces.generateAndAddPiece(p_162534_, p_162535_, p_162536_, this.boundingBox.minX() - 1, this.boundingBox.minY(), k, Direction.WEST, i + 1);
                  } else if (l == 1) {
                     MineShaftPieces.generateAndAddPiece(p_162534_, p_162535_, p_162536_, this.boundingBox.maxX() + 1, this.boundingBox.minY(), k, Direction.EAST, i + 1);
                  }
               }
            }
         }

      }

      protected boolean createChest(WorldGenLevel p_71407_, BoundingBox p_71408_, Random p_71409_, int p_71410_, int p_71411_, int p_71412_, ResourceLocation p_71413_) {
         BlockPos blockpos = this.getWorldPos(p_71410_, p_71411_, p_71412_);
         if (p_71408_.isInside(blockpos) && p_71407_.getBlockState(blockpos).isAir() && !p_71407_.getBlockState(blockpos.below()).isAir()) {
            BlockState blockstate = Blocks.RAIL.defaultBlockState().setValue(RailBlock.SHAPE, p_71409_.nextBoolean() ? RailShape.NORTH_SOUTH : RailShape.EAST_WEST);
            this.placeBlock(p_71407_, blockstate, p_71410_, p_71411_, p_71412_, p_71408_);
            MinecartChest minecartchest = new MinecartChest(p_71407_.getLevel(), (double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 0.5D, (double)blockpos.getZ() + 0.5D);
            minecartchest.setLootTable(p_71413_, p_71409_.nextLong());
            p_71407_.addFreshEntity(minecartchest);
            return true;
         } else {
            return false;
         }
      }

      public void postProcess(WorldGenLevel p_192030_, StructureFeatureManager p_192031_, ChunkGenerator p_192032_, Random p_192033_, BoundingBox p_192034_, ChunkPos p_192035_, BlockPos p_192036_) {
         if (!this.edgesLiquid(p_192030_, p_192034_)) {
            int i = 0;
            int j = 2;
            int k = 0;
            int l = 2;
            int i1 = this.numSections * 5 - 1;
            BlockState blockstate = this.type.getPlanksState();
            this.generateBox(p_192030_, p_192034_, 0, 0, 0, 2, 1, i1, CAVE_AIR, CAVE_AIR, false);
            this.generateMaybeBox(p_192030_, p_192034_, p_192033_, 0.8F, 0, 2, 0, 2, 2, i1, CAVE_AIR, CAVE_AIR, false, false);
            if (this.spiderCorridor) {
               this.generateMaybeBox(p_192030_, p_192034_, p_192033_, 0.6F, 0, 0, 0, 2, 1, i1, Blocks.COBWEB.defaultBlockState(), CAVE_AIR, false, true);
            }

            for(int j1 = 0; j1 < this.numSections; ++j1) {
               int k1 = 2 + j1 * 5;
               this.placeSupport(p_192030_, p_192034_, 0, 0, k1, 2, 2, p_192033_);
               this.maybePlaceCobWeb(p_192030_, p_192034_, p_192033_, 0.1F, 0, 2, k1 - 1);
               this.maybePlaceCobWeb(p_192030_, p_192034_, p_192033_, 0.1F, 2, 2, k1 - 1);
               this.maybePlaceCobWeb(p_192030_, p_192034_, p_192033_, 0.1F, 0, 2, k1 + 1);
               this.maybePlaceCobWeb(p_192030_, p_192034_, p_192033_, 0.1F, 2, 2, k1 + 1);
               this.maybePlaceCobWeb(p_192030_, p_192034_, p_192033_, 0.05F, 0, 2, k1 - 2);
               this.maybePlaceCobWeb(p_192030_, p_192034_, p_192033_, 0.05F, 2, 2, k1 - 2);
               this.maybePlaceCobWeb(p_192030_, p_192034_, p_192033_, 0.05F, 0, 2, k1 + 2);
               this.maybePlaceCobWeb(p_192030_, p_192034_, p_192033_, 0.05F, 2, 2, k1 + 2);
               if (p_192033_.nextInt(100) == 0) {
                  this.createChest(p_192030_, p_192034_, p_192033_, 2, 0, k1 - 1, BuiltInLootTables.ABANDONED_MINESHAFT);
               }

               if (p_192033_.nextInt(100) == 0) {
                  this.createChest(p_192030_, p_192034_, p_192033_, 0, 0, k1 + 1, BuiltInLootTables.ABANDONED_MINESHAFT);
               }

               if (this.spiderCorridor && !this.hasPlacedSpider) {
                  int l1 = 1;
                  int i2 = k1 - 1 + p_192033_.nextInt(3);
                  BlockPos blockpos = this.getWorldPos(1, 0, i2);
                  if (p_192034_.isInside(blockpos) && this.isInterior(p_192030_, 1, 0, i2, p_192034_)) {
                     this.hasPlacedSpider = true;
                     p_192030_.setBlock(blockpos, Blocks.SPAWNER.defaultBlockState(), 2);
                     BlockEntity blockentity = p_192030_.getBlockEntity(blockpos);
                     if (blockentity instanceof SpawnerBlockEntity) {
                        ((SpawnerBlockEntity)blockentity).getSpawner().setEntityId(EntityType.CAVE_SPIDER);
                     }
                  }
               }
            }

            for(int j2 = 0; j2 <= 2; ++j2) {
               for(int l2 = 0; l2 <= i1; ++l2) {
                  this.setPlanksBlock(p_192030_, p_192034_, blockstate, j2, -1, l2);
               }
            }

            int k2 = 2;
            this.placeDoubleLowerOrUpperSupport(p_192030_, p_192034_, 0, -1, 2);
            if (this.numSections > 1) {
               int i3 = i1 - 2;
               this.placeDoubleLowerOrUpperSupport(p_192030_, p_192034_, 0, -1, i3);
            }

            if (this.hasRails) {
               BlockState blockstate1 = Blocks.RAIL.defaultBlockState().setValue(RailBlock.SHAPE, RailShape.NORTH_SOUTH);

               for(int j3 = 0; j3 <= i1; ++j3) {
                  BlockState blockstate2 = this.getBlock(p_192030_, 1, -1, j3, p_192034_);
                  if (!blockstate2.isAir() && blockstate2.isSolidRender(p_192030_, this.getWorldPos(1, -1, j3))) {
                     float f = this.isInterior(p_192030_, 1, 0, j3, p_192034_) ? 0.7F : 0.9F;
                     this.maybeGenerateBlock(p_192030_, p_192034_, p_192033_, f, 1, 0, j3, blockstate1);
                  }
               }
            }

         }
      }

      private void placeDoubleLowerOrUpperSupport(WorldGenLevel p_162513_, BoundingBox p_162514_, int p_162515_, int p_162516_, int p_162517_) {
         BlockState blockstate = this.type.getWoodState();
         BlockState blockstate1 = this.type.getPlanksState();
         if (this.getBlock(p_162513_, p_162515_, p_162516_, p_162517_, p_162514_).is(blockstate1.getBlock())) {
            this.fillPillarDownOrChainUp(p_162513_, blockstate, p_162515_, p_162516_, p_162517_, p_162514_);
         }

         if (this.getBlock(p_162513_, p_162515_ + 2, p_162516_, p_162517_, p_162514_).is(blockstate1.getBlock())) {
            this.fillPillarDownOrChainUp(p_162513_, blockstate, p_162515_ + 2, p_162516_, p_162517_, p_162514_);
         }

      }

      protected void fillColumnDown(WorldGenLevel p_162500_, BlockState p_162501_, int p_162502_, int p_162503_, int p_162504_, BoundingBox p_162505_) {
         BlockPos.MutableBlockPos blockpos$mutableblockpos = this.getWorldPos(p_162502_, p_162503_, p_162504_);
         if (p_162505_.isInside(blockpos$mutableblockpos)) {
            int i = blockpos$mutableblockpos.getY();

            while(this.isReplaceableByStructures(p_162500_.getBlockState(blockpos$mutableblockpos)) && blockpos$mutableblockpos.getY() > p_162500_.getMinBuildHeight() + 1) {
               blockpos$mutableblockpos.move(Direction.DOWN);
            }

            if (this.canPlaceColumnOnTopOf(p_162500_, blockpos$mutableblockpos, p_162500_.getBlockState(blockpos$mutableblockpos))) {
               while(blockpos$mutableblockpos.getY() < i) {
                  blockpos$mutableblockpos.move(Direction.UP);
                  p_162500_.setBlock(blockpos$mutableblockpos, p_162501_, 2);
               }

            }
         }
      }

      protected void fillPillarDownOrChainUp(WorldGenLevel p_162545_, BlockState p_162546_, int p_162547_, int p_162548_, int p_162549_, BoundingBox p_162550_) {
         BlockPos.MutableBlockPos blockpos$mutableblockpos = this.getWorldPos(p_162547_, p_162548_, p_162549_);
         if (p_162550_.isInside(blockpos$mutableblockpos)) {
            int i = blockpos$mutableblockpos.getY();
            int j = 1;
            boolean flag = true;

            for(boolean flag1 = true; flag || flag1; ++j) {
               if (flag) {
                  blockpos$mutableblockpos.setY(i - j);
                  BlockState blockstate = p_162545_.getBlockState(blockpos$mutableblockpos);
                  boolean flag2 = this.isReplaceableByStructures(blockstate) && !blockstate.is(Blocks.LAVA);
                  if (!flag2 && this.canPlaceColumnOnTopOf(p_162545_, blockpos$mutableblockpos, blockstate)) {
                     fillColumnBetween(p_162545_, p_162546_, blockpos$mutableblockpos, i - j + 1, i);
                     return;
                  }

                  flag = j <= 20 && flag2 && blockpos$mutableblockpos.getY() > p_162545_.getMinBuildHeight() + 1;
               }

               if (flag1) {
                  blockpos$mutableblockpos.setY(i + j);
                  BlockState blockstate1 = p_162545_.getBlockState(blockpos$mutableblockpos);
                  boolean flag3 = this.isReplaceableByStructures(blockstate1);
                  if (!flag3 && this.canHangChainBelow(p_162545_, blockpos$mutableblockpos, blockstate1)) {
                     p_162545_.setBlock(blockpos$mutableblockpos.setY(i + 1), this.type.getFenceState(), 2);
                     fillColumnBetween(p_162545_, Blocks.CHAIN.defaultBlockState(), blockpos$mutableblockpos, i + 2, i + j);
                     return;
                  }

                  flag1 = j <= 50 && flag3 && blockpos$mutableblockpos.getY() < p_162545_.getMaxBuildHeight() - 1;
               }
            }

         }
      }

      private static void fillColumnBetween(WorldGenLevel p_162507_, BlockState p_162508_, BlockPos.MutableBlockPos p_162509_, int p_162510_, int p_162511_) {
         for(int i = p_162510_; i < p_162511_; ++i) {
            p_162507_.setBlock(p_162509_.setY(i), p_162508_, 2);
         }

      }

      private boolean canPlaceColumnOnTopOf(LevelReader p_203133_, BlockPos p_203134_, BlockState p_203135_) {
         return p_203135_.isFaceSturdy(p_203133_, p_203134_, Direction.UP);
      }

      private boolean canHangChainBelow(LevelReader p_162496_, BlockPos p_162497_, BlockState p_162498_) {
         return Block.canSupportCenter(p_162496_, p_162497_, Direction.DOWN) && !(p_162498_.getBlock() instanceof FallingBlock);
      }

      private void placeSupport(WorldGenLevel p_71390_, BoundingBox p_71391_, int p_71392_, int p_71393_, int p_71394_, int p_71395_, int p_71396_, Random p_71397_) {
         if (this.isSupportingBox(p_71390_, p_71391_, p_71392_, p_71396_, p_71395_, p_71394_)) {
            BlockState blockstate = this.type.getPlanksState();
            BlockState blockstate1 = this.type.getFenceState();
            this.generateBox(p_71390_, p_71391_, p_71392_, p_71393_, p_71394_, p_71392_, p_71395_ - 1, p_71394_, blockstate1.setValue(FenceBlock.WEST, Boolean.valueOf(true)), CAVE_AIR, false);
            this.generateBox(p_71390_, p_71391_, p_71396_, p_71393_, p_71394_, p_71396_, p_71395_ - 1, p_71394_, blockstate1.setValue(FenceBlock.EAST, Boolean.valueOf(true)), CAVE_AIR, false);
            if (p_71397_.nextInt(4) == 0) {
               this.generateBox(p_71390_, p_71391_, p_71392_, p_71395_, p_71394_, p_71392_, p_71395_, p_71394_, blockstate, CAVE_AIR, false);
               this.generateBox(p_71390_, p_71391_, p_71396_, p_71395_, p_71394_, p_71396_, p_71395_, p_71394_, blockstate, CAVE_AIR, false);
            } else {
               this.generateBox(p_71390_, p_71391_, p_71392_, p_71395_, p_71394_, p_71396_, p_71395_, p_71394_, blockstate, CAVE_AIR, false);
               this.maybeGenerateBlock(p_71390_, p_71391_, p_71397_, 0.05F, p_71392_ + 1, p_71395_, p_71394_ - 1, Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.SOUTH));
               this.maybeGenerateBlock(p_71390_, p_71391_, p_71397_, 0.05F, p_71392_ + 1, p_71395_, p_71394_ + 1, Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.NORTH));
            }

         }
      }

      private void maybePlaceCobWeb(WorldGenLevel p_162526_, BoundingBox p_162527_, Random p_162528_, float p_162529_, int p_162530_, int p_162531_, int p_162532_) {
         if (this.isInterior(p_162526_, p_162530_, p_162531_, p_162532_, p_162527_) && p_162528_.nextFloat() < p_162529_ && this.hasSturdyNeighbours(p_162526_, p_162527_, p_162530_, p_162531_, p_162532_, 2)) {
            this.placeBlock(p_162526_, Blocks.COBWEB.defaultBlockState(), p_162530_, p_162531_, p_162532_, p_162527_);
         }

      }

      private boolean hasSturdyNeighbours(WorldGenLevel p_162519_, BoundingBox p_162520_, int p_162521_, int p_162522_, int p_162523_, int p_162524_) {
         BlockPos.MutableBlockPos blockpos$mutableblockpos = this.getWorldPos(p_162521_, p_162522_, p_162523_);
         int i = 0;

         for(Direction direction : Direction.values()) {
            blockpos$mutableblockpos.move(direction);
            if (p_162520_.isInside(blockpos$mutableblockpos) && p_162519_.getBlockState(blockpos$mutableblockpos).isFaceSturdy(p_162519_, blockpos$mutableblockpos, direction.getOpposite())) {
               ++i;
               if (i >= p_162524_) {
                  return true;
               }
            }

            blockpos$mutableblockpos.move(direction.getOpposite());
         }

         return false;
      }
   }

   public static class MineShaftCrossing extends MineShaftPieces.MineShaftPiece {
      private final Direction direction;
      private final boolean isTwoFloored;

      public MineShaftCrossing(CompoundTag p_192041_) {
         super(StructurePieceType.MINE_SHAFT_CROSSING, p_192041_);
         this.isTwoFloored = p_192041_.getBoolean("tf");
         this.direction = Direction.from2DDataValue(p_192041_.getInt("D"));
      }

      protected void addAdditionalSaveData(StructurePieceSerializationContext p_192051_, CompoundTag p_192052_) {
         super.addAdditionalSaveData(p_192051_, p_192052_);
         p_192052_.putBoolean("tf", this.isTwoFloored);
         p_192052_.putInt("D", this.direction.get2DDataValue());
      }

      public MineShaftCrossing(int p_71430_, BoundingBox p_71431_, @Nullable Direction p_71432_, MineshaftFeature.Type p_71433_) {
         super(StructurePieceType.MINE_SHAFT_CROSSING, p_71430_, p_71433_, p_71431_);
         this.direction = p_71432_;
         this.isTwoFloored = p_71431_.getYSpan() > 3;
      }

      @Nullable
      public static BoundingBox findCrossing(StructurePieceAccessor p_162564_, Random p_162565_, int p_162566_, int p_162567_, int p_162568_, Direction p_162569_) {
         int i;
         if (p_162565_.nextInt(4) == 0) {
            i = 6;
         } else {
            i = 2;
         }

         BoundingBox boundingbox;
         switch(p_162569_) {
         case NORTH:
         default:
            boundingbox = new BoundingBox(-1, 0, -4, 3, i, 0);
            break;
         case SOUTH:
            boundingbox = new BoundingBox(-1, 0, 0, 3, i, 4);
            break;
         case WEST:
            boundingbox = new BoundingBox(-4, 0, -1, 0, i, 3);
            break;
         case EAST:
            boundingbox = new BoundingBox(0, 0, -1, 4, i, 3);
         }

         boundingbox.move(p_162566_, p_162567_, p_162568_);
         return p_162564_.findCollisionPiece(boundingbox) != null ? null : boundingbox;
      }

      public void addChildren(StructurePiece p_162560_, StructurePieceAccessor p_162561_, Random p_162562_) {
         int i = this.getGenDepth();
         switch(this.direction) {
         case NORTH:
         default:
            MineShaftPieces.generateAndAddPiece(p_162560_, p_162561_, p_162562_, this.boundingBox.minX() + 1, this.boundingBox.minY(), this.boundingBox.minZ() - 1, Direction.NORTH, i);
            MineShaftPieces.generateAndAddPiece(p_162560_, p_162561_, p_162562_, this.boundingBox.minX() - 1, this.boundingBox.minY(), this.boundingBox.minZ() + 1, Direction.WEST, i);
            MineShaftPieces.generateAndAddPiece(p_162560_, p_162561_, p_162562_, this.boundingBox.maxX() + 1, this.boundingBox.minY(), this.boundingBox.minZ() + 1, Direction.EAST, i);
            break;
         case SOUTH:
            MineShaftPieces.generateAndAddPiece(p_162560_, p_162561_, p_162562_, this.boundingBox.minX() + 1, this.boundingBox.minY(), this.boundingBox.maxZ() + 1, Direction.SOUTH, i);
            MineShaftPieces.generateAndAddPiece(p_162560_, p_162561_, p_162562_, this.boundingBox.minX() - 1, this.boundingBox.minY(), this.boundingBox.minZ() + 1, Direction.WEST, i);
            MineShaftPieces.generateAndAddPiece(p_162560_, p_162561_, p_162562_, this.boundingBox.maxX() + 1, this.boundingBox.minY(), this.boundingBox.minZ() + 1, Direction.EAST, i);
            break;
         case WEST:
            MineShaftPieces.generateAndAddPiece(p_162560_, p_162561_, p_162562_, this.boundingBox.minX() + 1, this.boundingBox.minY(), this.boundingBox.minZ() - 1, Direction.NORTH, i);
            MineShaftPieces.generateAndAddPiece(p_162560_, p_162561_, p_162562_, this.boundingBox.minX() + 1, this.boundingBox.minY(), this.boundingBox.maxZ() + 1, Direction.SOUTH, i);
            MineShaftPieces.generateAndAddPiece(p_162560_, p_162561_, p_162562_, this.boundingBox.minX() - 1, this.boundingBox.minY(), this.boundingBox.minZ() + 1, Direction.WEST, i);
            break;
         case EAST:
            MineShaftPieces.generateAndAddPiece(p_162560_, p_162561_, p_162562_, this.boundingBox.minX() + 1, this.boundingBox.minY(), this.boundingBox.minZ() - 1, Direction.NORTH, i);
            MineShaftPieces.generateAndAddPiece(p_162560_, p_162561_, p_162562_, this.boundingBox.minX() + 1, this.boundingBox.minY(), this.boundingBox.maxZ() + 1, Direction.SOUTH, i);
            MineShaftPieces.generateAndAddPiece(p_162560_, p_162561_, p_162562_, this.boundingBox.maxX() + 1, this.boundingBox.minY(), this.boundingBox.minZ() + 1, Direction.EAST, i);
         }

         if (this.isTwoFloored) {
            if (p_162562_.nextBoolean()) {
               MineShaftPieces.generateAndAddPiece(p_162560_, p_162561_, p_162562_, this.boundingBox.minX() + 1, this.boundingBox.minY() + 3 + 1, this.boundingBox.minZ() - 1, Direction.NORTH, i);
            }

            if (p_162562_.nextBoolean()) {
               MineShaftPieces.generateAndAddPiece(p_162560_, p_162561_, p_162562_, this.boundingBox.minX() - 1, this.boundingBox.minY() + 3 + 1, this.boundingBox.minZ() + 1, Direction.WEST, i);
            }

            if (p_162562_.nextBoolean()) {
               MineShaftPieces.generateAndAddPiece(p_162560_, p_162561_, p_162562_, this.boundingBox.maxX() + 1, this.boundingBox.minY() + 3 + 1, this.boundingBox.minZ() + 1, Direction.EAST, i);
            }

            if (p_162562_.nextBoolean()) {
               MineShaftPieces.generateAndAddPiece(p_162560_, p_162561_, p_162562_, this.boundingBox.minX() + 1, this.boundingBox.minY() + 3 + 1, this.boundingBox.maxZ() + 1, Direction.SOUTH, i);
            }
         }

      }

      public void postProcess(WorldGenLevel p_192043_, StructureFeatureManager p_192044_, ChunkGenerator p_192045_, Random p_192046_, BoundingBox p_192047_, ChunkPos p_192048_, BlockPos p_192049_) {
         if (!this.edgesLiquid(p_192043_, p_192047_)) {
            BlockState blockstate = this.type.getPlanksState();
            if (this.isTwoFloored) {
               this.generateBox(p_192043_, p_192047_, this.boundingBox.minX() + 1, this.boundingBox.minY(), this.boundingBox.minZ(), this.boundingBox.maxX() - 1, this.boundingBox.minY() + 3 - 1, this.boundingBox.maxZ(), CAVE_AIR, CAVE_AIR, false);
               this.generateBox(p_192043_, p_192047_, this.boundingBox.minX(), this.boundingBox.minY(), this.boundingBox.minZ() + 1, this.boundingBox.maxX(), this.boundingBox.minY() + 3 - 1, this.boundingBox.maxZ() - 1, CAVE_AIR, CAVE_AIR, false);
               this.generateBox(p_192043_, p_192047_, this.boundingBox.minX() + 1, this.boundingBox.maxY() - 2, this.boundingBox.minZ(), this.boundingBox.maxX() - 1, this.boundingBox.maxY(), this.boundingBox.maxZ(), CAVE_AIR, CAVE_AIR, false);
               this.generateBox(p_192043_, p_192047_, this.boundingBox.minX(), this.boundingBox.maxY() - 2, this.boundingBox.minZ() + 1, this.boundingBox.maxX(), this.boundingBox.maxY(), this.boundingBox.maxZ() - 1, CAVE_AIR, CAVE_AIR, false);
               this.generateBox(p_192043_, p_192047_, this.boundingBox.minX() + 1, this.boundingBox.minY() + 3, this.boundingBox.minZ() + 1, this.boundingBox.maxX() - 1, this.boundingBox.minY() + 3, this.boundingBox.maxZ() - 1, CAVE_AIR, CAVE_AIR, false);
            } else {
               this.generateBox(p_192043_, p_192047_, this.boundingBox.minX() + 1, this.boundingBox.minY(), this.boundingBox.minZ(), this.boundingBox.maxX() - 1, this.boundingBox.maxY(), this.boundingBox.maxZ(), CAVE_AIR, CAVE_AIR, false);
               this.generateBox(p_192043_, p_192047_, this.boundingBox.minX(), this.boundingBox.minY(), this.boundingBox.minZ() + 1, this.boundingBox.maxX(), this.boundingBox.maxY(), this.boundingBox.maxZ() - 1, CAVE_AIR, CAVE_AIR, false);
            }

            this.placeSupportPillar(p_192043_, p_192047_, this.boundingBox.minX() + 1, this.boundingBox.minY(), this.boundingBox.minZ() + 1, this.boundingBox.maxY());
            this.placeSupportPillar(p_192043_, p_192047_, this.boundingBox.minX() + 1, this.boundingBox.minY(), this.boundingBox.maxZ() - 1, this.boundingBox.maxY());
            this.placeSupportPillar(p_192043_, p_192047_, this.boundingBox.maxX() - 1, this.boundingBox.minY(), this.boundingBox.minZ() + 1, this.boundingBox.maxY());
            this.placeSupportPillar(p_192043_, p_192047_, this.boundingBox.maxX() - 1, this.boundingBox.minY(), this.boundingBox.maxZ() - 1, this.boundingBox.maxY());
            int i = this.boundingBox.minY() - 1;

            for(int j = this.boundingBox.minX(); j <= this.boundingBox.maxX(); ++j) {
               for(int k = this.boundingBox.minZ(); k <= this.boundingBox.maxZ(); ++k) {
                  this.setPlanksBlock(p_192043_, p_192047_, blockstate, j, i, k);
               }
            }

         }
      }

      private void placeSupportPillar(WorldGenLevel p_71446_, BoundingBox p_71447_, int p_71448_, int p_71449_, int p_71450_, int p_71451_) {
         if (!this.getBlock(p_71446_, p_71448_, p_71451_ + 1, p_71450_, p_71447_).isAir()) {
            this.generateBox(p_71446_, p_71447_, p_71448_, p_71449_, p_71450_, p_71448_, p_71451_, p_71450_, this.type.getPlanksState(), CAVE_AIR, false);
         }

      }
   }

   abstract static class MineShaftPiece extends StructurePiece {
      protected MineshaftFeature.Type type;

      public MineShaftPiece(StructurePieceType p_209876_, int p_209877_, MineshaftFeature.Type p_209878_, BoundingBox p_209879_) {
         super(p_209876_, p_209877_, p_209879_);
         this.type = p_209878_;
      }

      public MineShaftPiece(StructurePieceType p_209881_, CompoundTag p_209882_) {
         super(p_209881_, p_209882_);
         this.type = MineshaftFeature.Type.byId(p_209882_.getInt("MST"));
      }

      protected boolean canBeReplaced(LevelReader p_162582_, int p_162583_, int p_162584_, int p_162585_, BoundingBox p_162586_) {
         BlockState blockstate = this.getBlock(p_162582_, p_162583_, p_162584_, p_162585_, p_162586_);
         return !blockstate.is(this.type.getPlanksState().getBlock()) && !blockstate.is(this.type.getWoodState().getBlock()) && !blockstate.is(this.type.getFenceState().getBlock()) && !blockstate.is(Blocks.CHAIN);
      }

      protected void addAdditionalSaveData(StructurePieceSerializationContext p_192054_, CompoundTag p_192055_) {
         p_192055_.putInt("MST", this.type.ordinal());
      }

      protected boolean isSupportingBox(BlockGetter p_71475_, BoundingBox p_71476_, int p_71477_, int p_71478_, int p_71479_, int p_71480_) {
         for(int i = p_71477_; i <= p_71478_; ++i) {
            if (this.getBlock(p_71475_, i, p_71479_ + 1, p_71480_, p_71476_).isAir()) {
               return false;
            }
         }

         return true;
      }

      protected boolean edgesLiquid(BlockGetter p_162579_, BoundingBox p_162580_) {
         int i = Math.max(this.boundingBox.minX() - 1, p_162580_.minX());
         int j = Math.max(this.boundingBox.minY() - 1, p_162580_.minY());
         int k = Math.max(this.boundingBox.minZ() - 1, p_162580_.minZ());
         int l = Math.min(this.boundingBox.maxX() + 1, p_162580_.maxX());
         int i1 = Math.min(this.boundingBox.maxY() + 1, p_162580_.maxY());
         int j1 = Math.min(this.boundingBox.maxZ() + 1, p_162580_.maxZ());
         BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

         for(int k1 = i; k1 <= l; ++k1) {
            for(int l1 = k; l1 <= j1; ++l1) {
               if (p_162579_.getBlockState(blockpos$mutableblockpos.set(k1, j, l1)).getMaterial().isLiquid()) {
                  return true;
               }

               if (p_162579_.getBlockState(blockpos$mutableblockpos.set(k1, i1, l1)).getMaterial().isLiquid()) {
                  return true;
               }
            }
         }

         for(int i2 = i; i2 <= l; ++i2) {
            for(int k2 = j; k2 <= i1; ++k2) {
               if (p_162579_.getBlockState(blockpos$mutableblockpos.set(i2, k2, k)).getMaterial().isLiquid()) {
                  return true;
               }

               if (p_162579_.getBlockState(blockpos$mutableblockpos.set(i2, k2, j1)).getMaterial().isLiquid()) {
                  return true;
               }
            }
         }

         for(int j2 = k; j2 <= j1; ++j2) {
            for(int l2 = j; l2 <= i1; ++l2) {
               if (p_162579_.getBlockState(blockpos$mutableblockpos.set(i, l2, j2)).getMaterial().isLiquid()) {
                  return true;
               }

               if (p_162579_.getBlockState(blockpos$mutableblockpos.set(l, l2, j2)).getMaterial().isLiquid()) {
                  return true;
               }
            }
         }

         return false;
      }

      protected void setPlanksBlock(WorldGenLevel p_162588_, BoundingBox p_162589_, BlockState p_162590_, int p_162591_, int p_162592_, int p_162593_) {
         if (this.isInterior(p_162588_, p_162591_, p_162592_, p_162593_, p_162589_)) {
            BlockPos blockpos = this.getWorldPos(p_162591_, p_162592_, p_162593_);
            BlockState blockstate = p_162588_.getBlockState(blockpos);
            if (!blockstate.isFaceSturdy(p_162588_, blockpos, Direction.UP)) {
               p_162588_.setBlock(blockpos, p_162590_, 2);
            }

         }
      }
   }

   public static class MineShaftRoom extends MineShaftPieces.MineShaftPiece {
      private final List<BoundingBox> childEntranceBoxes = Lists.newLinkedList();

      public MineShaftRoom(int p_71486_, Random p_71487_, int p_71488_, int p_71489_, MineshaftFeature.Type p_71490_) {
         super(StructurePieceType.MINE_SHAFT_ROOM, p_71486_, p_71490_, new BoundingBox(p_71488_, 50, p_71489_, p_71488_ + 7 + p_71487_.nextInt(6), 54 + p_71487_.nextInt(6), p_71489_ + 7 + p_71487_.nextInt(6)));
         this.type = p_71490_;
      }

      public MineShaftRoom(CompoundTag p_192057_) {
         super(StructurePieceType.MINE_SHAFT_ROOM, p_192057_);
         BoundingBox.CODEC.listOf().parse(NbtOps.INSTANCE, p_192057_.getList("Entrances", 11)).resultOrPartial(MineShaftPieces.LOGGER::error).ifPresent(this.childEntranceBoxes::addAll);
      }

      public void addChildren(StructurePiece p_162601_, StructurePieceAccessor p_162602_, Random p_162603_) {
         int i = this.getGenDepth();
         int j = this.boundingBox.getYSpan() - 3 - 1;
         if (j <= 0) {
            j = 1;
         }

         int k;
         for(k = 0; k < this.boundingBox.getXSpan(); k += 4) {
            k += p_162603_.nextInt(this.boundingBox.getXSpan());
            if (k + 3 > this.boundingBox.getXSpan()) {
               break;
            }

            MineShaftPieces.MineShaftPiece mineshaftpieces$mineshaftpiece = MineShaftPieces.generateAndAddPiece(p_162601_, p_162602_, p_162603_, this.boundingBox.minX() + k, this.boundingBox.minY() + p_162603_.nextInt(j) + 1, this.boundingBox.minZ() - 1, Direction.NORTH, i);
            if (mineshaftpieces$mineshaftpiece != null) {
               BoundingBox boundingbox = mineshaftpieces$mineshaftpiece.getBoundingBox();
               this.childEntranceBoxes.add(new BoundingBox(boundingbox.minX(), boundingbox.minY(), this.boundingBox.minZ(), boundingbox.maxX(), boundingbox.maxY(), this.boundingBox.minZ() + 1));
            }
         }

         for(k = 0; k < this.boundingBox.getXSpan(); k += 4) {
            k += p_162603_.nextInt(this.boundingBox.getXSpan());
            if (k + 3 > this.boundingBox.getXSpan()) {
               break;
            }

            MineShaftPieces.MineShaftPiece mineshaftpieces$mineshaftpiece1 = MineShaftPieces.generateAndAddPiece(p_162601_, p_162602_, p_162603_, this.boundingBox.minX() + k, this.boundingBox.minY() + p_162603_.nextInt(j) + 1, this.boundingBox.maxZ() + 1, Direction.SOUTH, i);
            if (mineshaftpieces$mineshaftpiece1 != null) {
               BoundingBox boundingbox1 = mineshaftpieces$mineshaftpiece1.getBoundingBox();
               this.childEntranceBoxes.add(new BoundingBox(boundingbox1.minX(), boundingbox1.minY(), this.boundingBox.maxZ() - 1, boundingbox1.maxX(), boundingbox1.maxY(), this.boundingBox.maxZ()));
            }
         }

         for(k = 0; k < this.boundingBox.getZSpan(); k += 4) {
            k += p_162603_.nextInt(this.boundingBox.getZSpan());
            if (k + 3 > this.boundingBox.getZSpan()) {
               break;
            }

            MineShaftPieces.MineShaftPiece mineshaftpieces$mineshaftpiece2 = MineShaftPieces.generateAndAddPiece(p_162601_, p_162602_, p_162603_, this.boundingBox.minX() - 1, this.boundingBox.minY() + p_162603_.nextInt(j) + 1, this.boundingBox.minZ() + k, Direction.WEST, i);
            if (mineshaftpieces$mineshaftpiece2 != null) {
               BoundingBox boundingbox2 = mineshaftpieces$mineshaftpiece2.getBoundingBox();
               this.childEntranceBoxes.add(new BoundingBox(this.boundingBox.minX(), boundingbox2.minY(), boundingbox2.minZ(), this.boundingBox.minX() + 1, boundingbox2.maxY(), boundingbox2.maxZ()));
            }
         }

         for(k = 0; k < this.boundingBox.getZSpan(); k += 4) {
            k += p_162603_.nextInt(this.boundingBox.getZSpan());
            if (k + 3 > this.boundingBox.getZSpan()) {
               break;
            }

            StructurePiece structurepiece = MineShaftPieces.generateAndAddPiece(p_162601_, p_162602_, p_162603_, this.boundingBox.maxX() + 1, this.boundingBox.minY() + p_162603_.nextInt(j) + 1, this.boundingBox.minZ() + k, Direction.EAST, i);
            if (structurepiece != null) {
               BoundingBox boundingbox3 = structurepiece.getBoundingBox();
               this.childEntranceBoxes.add(new BoundingBox(this.boundingBox.maxX() - 1, boundingbox3.minY(), boundingbox3.minZ(), this.boundingBox.maxX(), boundingbox3.maxY(), boundingbox3.maxZ()));
            }
         }

      }

      public void postProcess(WorldGenLevel p_192059_, StructureFeatureManager p_192060_, ChunkGenerator p_192061_, Random p_192062_, BoundingBox p_192063_, ChunkPos p_192064_, BlockPos p_192065_) {
         if (!this.edgesLiquid(p_192059_, p_192063_)) {
            this.generateBox(p_192059_, p_192063_, this.boundingBox.minX(), this.boundingBox.minY() + 1, this.boundingBox.minZ(), this.boundingBox.maxX(), Math.min(this.boundingBox.minY() + 3, this.boundingBox.maxY()), this.boundingBox.maxZ(), CAVE_AIR, CAVE_AIR, false);

            for(BoundingBox boundingbox : this.childEntranceBoxes) {
               this.generateBox(p_192059_, p_192063_, boundingbox.minX(), boundingbox.maxY() - 2, boundingbox.minZ(), boundingbox.maxX(), boundingbox.maxY(), boundingbox.maxZ(), CAVE_AIR, CAVE_AIR, false);
            }

            this.generateUpperHalfSphere(p_192059_, p_192063_, this.boundingBox.minX(), this.boundingBox.minY() + 4, this.boundingBox.minZ(), this.boundingBox.maxX(), this.boundingBox.maxY(), this.boundingBox.maxZ(), CAVE_AIR, false);
         }
      }

      public void move(int p_71495_, int p_71496_, int p_71497_) {
         super.move(p_71495_, p_71496_, p_71497_);

         for(BoundingBox boundingbox : this.childEntranceBoxes) {
            boundingbox.move(p_71495_, p_71496_, p_71497_);
         }

      }

      protected void addAdditionalSaveData(StructurePieceSerializationContext p_192067_, CompoundTag p_192068_) {
         super.addAdditionalSaveData(p_192067_, p_192068_);
         BoundingBox.CODEC.listOf().encodeStart(NbtOps.INSTANCE, this.childEntranceBoxes).resultOrPartial(MineShaftPieces.LOGGER::error).ifPresent((p_162606_) -> {
            p_192068_.put("Entrances", p_162606_);
         });
      }
   }

   public static class MineShaftStairs extends MineShaftPieces.MineShaftPiece {
      public MineShaftStairs(int p_71513_, BoundingBox p_71514_, Direction p_71515_, MineshaftFeature.Type p_71516_) {
         super(StructurePieceType.MINE_SHAFT_STAIRS, p_71513_, p_71516_, p_71514_);
         this.setOrientation(p_71515_);
      }

      public MineShaftStairs(CompoundTag p_192070_) {
         super(StructurePieceType.MINE_SHAFT_STAIRS, p_192070_);
      }

      @Nullable
      public static BoundingBox findStairs(StructurePieceAccessor p_162615_, Random p_162616_, int p_162617_, int p_162618_, int p_162619_, Direction p_162620_) {
         BoundingBox boundingbox;
         switch(p_162620_) {
         case NORTH:
         default:
            boundingbox = new BoundingBox(0, -5, -8, 2, 2, 0);
            break;
         case SOUTH:
            boundingbox = new BoundingBox(0, -5, 0, 2, 2, 8);
            break;
         case WEST:
            boundingbox = new BoundingBox(-8, -5, 0, 0, 2, 2);
            break;
         case EAST:
            boundingbox = new BoundingBox(0, -5, 0, 8, 2, 2);
         }

         boundingbox.move(p_162617_, p_162618_, p_162619_);
         return p_162615_.findCollisionPiece(boundingbox) != null ? null : boundingbox;
      }

      public void addChildren(StructurePiece p_162611_, StructurePieceAccessor p_162612_, Random p_162613_) {
         int i = this.getGenDepth();
         Direction direction = this.getOrientation();
         if (direction != null) {
            switch(direction) {
            case NORTH:
            default:
               MineShaftPieces.generateAndAddPiece(p_162611_, p_162612_, p_162613_, this.boundingBox.minX(), this.boundingBox.minY(), this.boundingBox.minZ() - 1, Direction.NORTH, i);
               break;
            case SOUTH:
               MineShaftPieces.generateAndAddPiece(p_162611_, p_162612_, p_162613_, this.boundingBox.minX(), this.boundingBox.minY(), this.boundingBox.maxZ() + 1, Direction.SOUTH, i);
               break;
            case WEST:
               MineShaftPieces.generateAndAddPiece(p_162611_, p_162612_, p_162613_, this.boundingBox.minX() - 1, this.boundingBox.minY(), this.boundingBox.minZ(), Direction.WEST, i);
               break;
            case EAST:
               MineShaftPieces.generateAndAddPiece(p_162611_, p_162612_, p_162613_, this.boundingBox.maxX() + 1, this.boundingBox.minY(), this.boundingBox.minZ(), Direction.EAST, i);
            }
         }

      }

      public void postProcess(WorldGenLevel p_192072_, StructureFeatureManager p_192073_, ChunkGenerator p_192074_, Random p_192075_, BoundingBox p_192076_, ChunkPos p_192077_, BlockPos p_192078_) {
         if (!this.edgesLiquid(p_192072_, p_192076_)) {
            this.generateBox(p_192072_, p_192076_, 0, 5, 0, 2, 7, 1, CAVE_AIR, CAVE_AIR, false);
            this.generateBox(p_192072_, p_192076_, 0, 0, 7, 2, 2, 8, CAVE_AIR, CAVE_AIR, false);

            for(int i = 0; i < 5; ++i) {
               this.generateBox(p_192072_, p_192076_, 0, 5 - i - (i < 4 ? 1 : 0), 2 + i, 2, 7 - i, 2 + i, CAVE_AIR, CAVE_AIR, false);
            }

         }
      }
   }
}