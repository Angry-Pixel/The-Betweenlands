package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class NetherBridgePieces {
   private static final int MAX_DEPTH = 30;
   private static final int LOWEST_Y_POSITION = 10;
   public static final int MAGIC_START_Y = 64;
   static final NetherBridgePieces.PieceWeight[] BRIDGE_PIECE_WEIGHTS = new NetherBridgePieces.PieceWeight[]{new NetherBridgePieces.PieceWeight(NetherBridgePieces.BridgeStraight.class, 30, 0, true), new NetherBridgePieces.PieceWeight(NetherBridgePieces.BridgeCrossing.class, 10, 4), new NetherBridgePieces.PieceWeight(NetherBridgePieces.RoomCrossing.class, 10, 4), new NetherBridgePieces.PieceWeight(NetherBridgePieces.StairsRoom.class, 10, 3), new NetherBridgePieces.PieceWeight(NetherBridgePieces.MonsterThrone.class, 5, 2), new NetherBridgePieces.PieceWeight(NetherBridgePieces.CastleEntrance.class, 5, 1)};
   static final NetherBridgePieces.PieceWeight[] CASTLE_PIECE_WEIGHTS = new NetherBridgePieces.PieceWeight[]{new NetherBridgePieces.PieceWeight(NetherBridgePieces.CastleSmallCorridorPiece.class, 25, 0, true), new NetherBridgePieces.PieceWeight(NetherBridgePieces.CastleSmallCorridorCrossingPiece.class, 15, 5), new NetherBridgePieces.PieceWeight(NetherBridgePieces.CastleSmallCorridorRightTurnPiece.class, 5, 10), new NetherBridgePieces.PieceWeight(NetherBridgePieces.CastleSmallCorridorLeftTurnPiece.class, 5, 10), new NetherBridgePieces.PieceWeight(NetherBridgePieces.CastleCorridorStairsPiece.class, 10, 3, true), new NetherBridgePieces.PieceWeight(NetherBridgePieces.CastleCorridorTBalconyPiece.class, 7, 2), new NetherBridgePieces.PieceWeight(NetherBridgePieces.CastleStalkRoom.class, 5, 2)};

   static NetherBridgePieces.NetherBridgePiece findAndCreateBridgePieceFactory(NetherBridgePieces.PieceWeight p_162625_, StructurePieceAccessor p_162626_, Random p_162627_, int p_162628_, int p_162629_, int p_162630_, Direction p_162631_, int p_162632_) {
      Class<? extends NetherBridgePieces.NetherBridgePiece> oclass = p_162625_.pieceClass;
      NetherBridgePieces.NetherBridgePiece netherbridgepieces$netherbridgepiece = null;
      if (oclass == NetherBridgePieces.BridgeStraight.class) {
         netherbridgepieces$netherbridgepiece = NetherBridgePieces.BridgeStraight.createPiece(p_162626_, p_162627_, p_162628_, p_162629_, p_162630_, p_162631_, p_162632_);
      } else if (oclass == NetherBridgePieces.BridgeCrossing.class) {
         netherbridgepieces$netherbridgepiece = NetherBridgePieces.BridgeCrossing.createPiece(p_162626_, p_162628_, p_162629_, p_162630_, p_162631_, p_162632_);
      } else if (oclass == NetherBridgePieces.RoomCrossing.class) {
         netherbridgepieces$netherbridgepiece = NetherBridgePieces.RoomCrossing.createPiece(p_162626_, p_162628_, p_162629_, p_162630_, p_162631_, p_162632_);
      } else if (oclass == NetherBridgePieces.StairsRoom.class) {
         netherbridgepieces$netherbridgepiece = NetherBridgePieces.StairsRoom.createPiece(p_162626_, p_162628_, p_162629_, p_162630_, p_162632_, p_162631_);
      } else if (oclass == NetherBridgePieces.MonsterThrone.class) {
         netherbridgepieces$netherbridgepiece = NetherBridgePieces.MonsterThrone.createPiece(p_162626_, p_162628_, p_162629_, p_162630_, p_162632_, p_162631_);
      } else if (oclass == NetherBridgePieces.CastleEntrance.class) {
         netherbridgepieces$netherbridgepiece = NetherBridgePieces.CastleEntrance.createPiece(p_162626_, p_162627_, p_162628_, p_162629_, p_162630_, p_162631_, p_162632_);
      } else if (oclass == NetherBridgePieces.CastleSmallCorridorPiece.class) {
         netherbridgepieces$netherbridgepiece = NetherBridgePieces.CastleSmallCorridorPiece.createPiece(p_162626_, p_162628_, p_162629_, p_162630_, p_162631_, p_162632_);
      } else if (oclass == NetherBridgePieces.CastleSmallCorridorRightTurnPiece.class) {
         netherbridgepieces$netherbridgepiece = NetherBridgePieces.CastleSmallCorridorRightTurnPiece.createPiece(p_162626_, p_162627_, p_162628_, p_162629_, p_162630_, p_162631_, p_162632_);
      } else if (oclass == NetherBridgePieces.CastleSmallCorridorLeftTurnPiece.class) {
         netherbridgepieces$netherbridgepiece = NetherBridgePieces.CastleSmallCorridorLeftTurnPiece.createPiece(p_162626_, p_162627_, p_162628_, p_162629_, p_162630_, p_162631_, p_162632_);
      } else if (oclass == NetherBridgePieces.CastleCorridorStairsPiece.class) {
         netherbridgepieces$netherbridgepiece = NetherBridgePieces.CastleCorridorStairsPiece.createPiece(p_162626_, p_162628_, p_162629_, p_162630_, p_162631_, p_162632_);
      } else if (oclass == NetherBridgePieces.CastleCorridorTBalconyPiece.class) {
         netherbridgepieces$netherbridgepiece = NetherBridgePieces.CastleCorridorTBalconyPiece.createPiece(p_162626_, p_162628_, p_162629_, p_162630_, p_162631_, p_162632_);
      } else if (oclass == NetherBridgePieces.CastleSmallCorridorCrossingPiece.class) {
         netherbridgepieces$netherbridgepiece = NetherBridgePieces.CastleSmallCorridorCrossingPiece.createPiece(p_162626_, p_162628_, p_162629_, p_162630_, p_162631_, p_162632_);
      } else if (oclass == NetherBridgePieces.CastleStalkRoom.class) {
         netherbridgepieces$netherbridgepiece = NetherBridgePieces.CastleStalkRoom.createPiece(p_162626_, p_162628_, p_162629_, p_162630_, p_162631_, p_162632_);
      }

      return netherbridgepieces$netherbridgepiece;
   }

   public static class BridgeCrossing extends NetherBridgePieces.NetherBridgePiece {
      private static final int WIDTH = 19;
      private static final int HEIGHT = 10;
      private static final int DEPTH = 19;

      public BridgeCrossing(int p_71565_, BoundingBox p_71566_, Direction p_71567_) {
         super(StructurePieceType.NETHER_FORTRESS_BRIDGE_CROSSING, p_71565_, p_71566_);
         this.setOrientation(p_71567_);
      }

      protected BridgeCrossing(int p_162637_, int p_162638_, Direction p_162639_) {
         super(StructurePieceType.NETHER_FORTRESS_BRIDGE_CROSSING, 0, StructurePiece.makeBoundingBox(p_162637_, 64, p_162638_, p_162639_, 19, 10, 19));
         this.setOrientation(p_162639_);
      }

      protected BridgeCrossing(StructurePieceType p_209884_, CompoundTag p_209885_) {
         super(p_209884_, p_209885_);
      }

      public BridgeCrossing(CompoundTag p_192081_) {
         this(StructurePieceType.NETHER_FORTRESS_BRIDGE_CROSSING, p_192081_);
      }

      public void addChildren(StructurePiece p_162644_, StructurePieceAccessor p_162645_, Random p_162646_) {
         this.generateChildForward((NetherBridgePieces.StartPiece)p_162644_, p_162645_, p_162646_, 8, 3, false);
         this.generateChildLeft((NetherBridgePieces.StartPiece)p_162644_, p_162645_, p_162646_, 3, 8, false);
         this.generateChildRight((NetherBridgePieces.StartPiece)p_162644_, p_162645_, p_162646_, 3, 8, false);
      }

      public static NetherBridgePieces.BridgeCrossing createPiece(StructurePieceAccessor p_162648_, int p_162649_, int p_162650_, int p_162651_, Direction p_162652_, int p_162653_) {
         BoundingBox boundingbox = BoundingBox.orientBox(p_162649_, p_162650_, p_162651_, -8, -3, 0, 19, 10, 19, p_162652_);
         return isOkBox(boundingbox) && p_162648_.findCollisionPiece(boundingbox) == null ? new NetherBridgePieces.BridgeCrossing(p_162653_, boundingbox, p_162652_) : null;
      }

      public void postProcess(WorldGenLevel p_192083_, StructureFeatureManager p_192084_, ChunkGenerator p_192085_, Random p_192086_, BoundingBox p_192087_, ChunkPos p_192088_, BlockPos p_192089_) {
         this.generateBox(p_192083_, p_192087_, 7, 3, 0, 11, 4, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192083_, p_192087_, 0, 3, 7, 18, 4, 11, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192083_, p_192087_, 8, 5, 0, 10, 7, 18, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(p_192083_, p_192087_, 0, 5, 8, 18, 7, 10, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(p_192083_, p_192087_, 7, 5, 0, 7, 5, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192083_, p_192087_, 7, 5, 11, 7, 5, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192083_, p_192087_, 11, 5, 0, 11, 5, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192083_, p_192087_, 11, 5, 11, 11, 5, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192083_, p_192087_, 0, 5, 7, 7, 5, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192083_, p_192087_, 11, 5, 7, 18, 5, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192083_, p_192087_, 0, 5, 11, 7, 5, 11, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192083_, p_192087_, 11, 5, 11, 18, 5, 11, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192083_, p_192087_, 7, 2, 0, 11, 2, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192083_, p_192087_, 7, 2, 13, 11, 2, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192083_, p_192087_, 7, 0, 0, 11, 1, 3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192083_, p_192087_, 7, 0, 15, 11, 1, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);

         for(int i = 7; i <= 11; ++i) {
            for(int j = 0; j <= 2; ++j) {
               this.fillColumnDown(p_192083_, Blocks.NETHER_BRICKS.defaultBlockState(), i, -1, j, p_192087_);
               this.fillColumnDown(p_192083_, Blocks.NETHER_BRICKS.defaultBlockState(), i, -1, 18 - j, p_192087_);
            }
         }

         this.generateBox(p_192083_, p_192087_, 0, 2, 7, 5, 2, 11, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192083_, p_192087_, 13, 2, 7, 18, 2, 11, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192083_, p_192087_, 0, 0, 7, 3, 1, 11, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192083_, p_192087_, 15, 0, 7, 18, 1, 11, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);

         for(int k = 0; k <= 2; ++k) {
            for(int l = 7; l <= 11; ++l) {
               this.fillColumnDown(p_192083_, Blocks.NETHER_BRICKS.defaultBlockState(), k, -1, l, p_192087_);
               this.fillColumnDown(p_192083_, Blocks.NETHER_BRICKS.defaultBlockState(), 18 - k, -1, l, p_192087_);
            }
         }

      }
   }

   public static class BridgeEndFiller extends NetherBridgePieces.NetherBridgePiece {
      private static final int WIDTH = 5;
      private static final int HEIGHT = 10;
      private static final int DEPTH = 8;
      private final int selfSeed;

      public BridgeEndFiller(int p_71599_, Random p_71600_, BoundingBox p_71601_, Direction p_71602_) {
         super(StructurePieceType.NETHER_FORTRESS_BRIDGE_END_FILLER, p_71599_, p_71601_);
         this.setOrientation(p_71602_);
         this.selfSeed = p_71600_.nextInt();
      }

      public BridgeEndFiller(CompoundTag p_192091_) {
         super(StructurePieceType.NETHER_FORTRESS_BRIDGE_END_FILLER, p_192091_);
         this.selfSeed = p_192091_.getInt("Seed");
      }

      public static NetherBridgePieces.BridgeEndFiller createPiece(StructurePieceAccessor p_162664_, Random p_162665_, int p_162666_, int p_162667_, int p_162668_, Direction p_162669_, int p_162670_) {
         BoundingBox boundingbox = BoundingBox.orientBox(p_162666_, p_162667_, p_162668_, -1, -3, 0, 5, 10, 8, p_162669_);
         return isOkBox(boundingbox) && p_162664_.findCollisionPiece(boundingbox) == null ? new NetherBridgePieces.BridgeEndFiller(p_162670_, p_162665_, boundingbox, p_162669_) : null;
      }

      protected void addAdditionalSaveData(StructurePieceSerializationContext p_192101_, CompoundTag p_192102_) {
         super.addAdditionalSaveData(p_192101_, p_192102_);
         p_192102_.putInt("Seed", this.selfSeed);
      }

      public void postProcess(WorldGenLevel p_192093_, StructureFeatureManager p_192094_, ChunkGenerator p_192095_, Random p_192096_, BoundingBox p_192097_, ChunkPos p_192098_, BlockPos p_192099_) {
         Random random = new Random((long)this.selfSeed);

         for(int i = 0; i <= 4; ++i) {
            for(int j = 3; j <= 4; ++j) {
               int k = random.nextInt(8);
               this.generateBox(p_192093_, p_192097_, i, j, 0, i, j, k, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            }
         }

         int l = random.nextInt(8);
         this.generateBox(p_192093_, p_192097_, 0, 5, 0, 0, 5, l, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         l = random.nextInt(8);
         this.generateBox(p_192093_, p_192097_, 4, 5, 0, 4, 5, l, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);

         for(int i1 = 0; i1 <= 4; ++i1) {
            int k1 = random.nextInt(5);
            this.generateBox(p_192093_, p_192097_, i1, 2, 0, i1, 2, k1, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         }

         for(int j1 = 0; j1 <= 4; ++j1) {
            for(int l1 = 0; l1 <= 1; ++l1) {
               int i2 = random.nextInt(3);
               this.generateBox(p_192093_, p_192097_, j1, l1, 0, j1, l1, i2, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            }
         }

      }
   }

   public static class BridgeStraight extends NetherBridgePieces.NetherBridgePiece {
      private static final int WIDTH = 5;
      private static final int HEIGHT = 10;
      private static final int DEPTH = 19;

      public BridgeStraight(int p_71625_, Random p_71626_, BoundingBox p_71627_, Direction p_71628_) {
         super(StructurePieceType.NETHER_FORTRESS_BRIDGE_STRAIGHT, p_71625_, p_71627_);
         this.setOrientation(p_71628_);
      }

      public BridgeStraight(CompoundTag p_192104_) {
         super(StructurePieceType.NETHER_FORTRESS_BRIDGE_STRAIGHT, p_192104_);
      }

      public void addChildren(StructurePiece p_162678_, StructurePieceAccessor p_162679_, Random p_162680_) {
         this.generateChildForward((NetherBridgePieces.StartPiece)p_162678_, p_162679_, p_162680_, 1, 3, false);
      }

      public static NetherBridgePieces.BridgeStraight createPiece(StructurePieceAccessor p_162682_, Random p_162683_, int p_162684_, int p_162685_, int p_162686_, Direction p_162687_, int p_162688_) {
         BoundingBox boundingbox = BoundingBox.orientBox(p_162684_, p_162685_, p_162686_, -1, -3, 0, 5, 10, 19, p_162687_);
         return isOkBox(boundingbox) && p_162682_.findCollisionPiece(boundingbox) == null ? new NetherBridgePieces.BridgeStraight(p_162688_, p_162683_, boundingbox, p_162687_) : null;
      }

      public void postProcess(WorldGenLevel p_192106_, StructureFeatureManager p_192107_, ChunkGenerator p_192108_, Random p_192109_, BoundingBox p_192110_, ChunkPos p_192111_, BlockPos p_192112_) {
         this.generateBox(p_192106_, p_192110_, 0, 3, 0, 4, 4, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192106_, p_192110_, 1, 5, 0, 3, 7, 18, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(p_192106_, p_192110_, 0, 5, 0, 0, 5, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192106_, p_192110_, 4, 5, 0, 4, 5, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192106_, p_192110_, 0, 2, 0, 4, 2, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192106_, p_192110_, 0, 2, 13, 4, 2, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192106_, p_192110_, 0, 0, 0, 4, 1, 3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192106_, p_192110_, 0, 0, 15, 4, 1, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);

         for(int i = 0; i <= 4; ++i) {
            for(int j = 0; j <= 2; ++j) {
               this.fillColumnDown(p_192106_, Blocks.NETHER_BRICKS.defaultBlockState(), i, -1, j, p_192110_);
               this.fillColumnDown(p_192106_, Blocks.NETHER_BRICKS.defaultBlockState(), i, -1, 18 - j, p_192110_);
            }
         }

         BlockState blockstate1 = Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, Boolean.valueOf(true)).setValue(FenceBlock.SOUTH, Boolean.valueOf(true));
         BlockState blockstate2 = blockstate1.setValue(FenceBlock.EAST, Boolean.valueOf(true));
         BlockState blockstate = blockstate1.setValue(FenceBlock.WEST, Boolean.valueOf(true));
         this.generateBox(p_192106_, p_192110_, 0, 1, 1, 0, 4, 1, blockstate2, blockstate2, false);
         this.generateBox(p_192106_, p_192110_, 0, 3, 4, 0, 4, 4, blockstate2, blockstate2, false);
         this.generateBox(p_192106_, p_192110_, 0, 3, 14, 0, 4, 14, blockstate2, blockstate2, false);
         this.generateBox(p_192106_, p_192110_, 0, 1, 17, 0, 4, 17, blockstate2, blockstate2, false);
         this.generateBox(p_192106_, p_192110_, 4, 1, 1, 4, 4, 1, blockstate, blockstate, false);
         this.generateBox(p_192106_, p_192110_, 4, 3, 4, 4, 4, 4, blockstate, blockstate, false);
         this.generateBox(p_192106_, p_192110_, 4, 3, 14, 4, 4, 14, blockstate, blockstate, false);
         this.generateBox(p_192106_, p_192110_, 4, 1, 17, 4, 4, 17, blockstate, blockstate, false);
      }
   }

   public static class CastleCorridorStairsPiece extends NetherBridgePieces.NetherBridgePiece {
      private static final int WIDTH = 5;
      private static final int HEIGHT = 14;
      private static final int DEPTH = 10;

      public CastleCorridorStairsPiece(int p_71653_, BoundingBox p_71654_, Direction p_71655_) {
         super(StructurePieceType.NETHER_FORTRESS_CASTLE_CORRIDOR_STAIRS, p_71653_, p_71654_);
         this.setOrientation(p_71655_);
      }

      public CastleCorridorStairsPiece(CompoundTag p_192114_) {
         super(StructurePieceType.NETHER_FORTRESS_CASTLE_CORRIDOR_STAIRS, p_192114_);
      }

      public void addChildren(StructurePiece p_162696_, StructurePieceAccessor p_162697_, Random p_162698_) {
         this.generateChildForward((NetherBridgePieces.StartPiece)p_162696_, p_162697_, p_162698_, 1, 0, true);
      }

      public static NetherBridgePieces.CastleCorridorStairsPiece createPiece(StructurePieceAccessor p_162700_, int p_162701_, int p_162702_, int p_162703_, Direction p_162704_, int p_162705_) {
         BoundingBox boundingbox = BoundingBox.orientBox(p_162701_, p_162702_, p_162703_, -1, -7, 0, 5, 14, 10, p_162704_);
         return isOkBox(boundingbox) && p_162700_.findCollisionPiece(boundingbox) == null ? new NetherBridgePieces.CastleCorridorStairsPiece(p_162705_, boundingbox, p_162704_) : null;
      }

      public void postProcess(WorldGenLevel p_192116_, StructureFeatureManager p_192117_, ChunkGenerator p_192118_, Random p_192119_, BoundingBox p_192120_, ChunkPos p_192121_, BlockPos p_192122_) {
         BlockState blockstate = Blocks.NETHER_BRICK_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.SOUTH);
         BlockState blockstate1 = Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, Boolean.valueOf(true)).setValue(FenceBlock.SOUTH, Boolean.valueOf(true));

         for(int i = 0; i <= 9; ++i) {
            int j = Math.max(1, 7 - i);
            int k = Math.min(Math.max(j + 5, 14 - i), 13);
            int l = i;
            this.generateBox(p_192116_, p_192120_, 0, 0, i, 4, j, i, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(p_192116_, p_192120_, 1, j + 1, i, 3, k - 1, i, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            if (i <= 6) {
               this.placeBlock(p_192116_, blockstate, 1, j + 1, i, p_192120_);
               this.placeBlock(p_192116_, blockstate, 2, j + 1, i, p_192120_);
               this.placeBlock(p_192116_, blockstate, 3, j + 1, i, p_192120_);
            }

            this.generateBox(p_192116_, p_192120_, 0, k, i, 4, k, i, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(p_192116_, p_192120_, 0, j + 1, i, 0, k - 1, i, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(p_192116_, p_192120_, 4, j + 1, i, 4, k - 1, i, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            if ((i & 1) == 0) {
               this.generateBox(p_192116_, p_192120_, 0, j + 2, i, 0, j + 3, i, blockstate1, blockstate1, false);
               this.generateBox(p_192116_, p_192120_, 4, j + 2, i, 4, j + 3, i, blockstate1, blockstate1, false);
            }

            for(int i1 = 0; i1 <= 4; ++i1) {
               this.fillColumnDown(p_192116_, Blocks.NETHER_BRICKS.defaultBlockState(), i1, -1, l, p_192120_);
            }
         }

      }
   }

   public static class CastleCorridorTBalconyPiece extends NetherBridgePieces.NetherBridgePiece {
      private static final int WIDTH = 9;
      private static final int HEIGHT = 7;
      private static final int DEPTH = 9;

      public CastleCorridorTBalconyPiece(int p_71679_, BoundingBox p_71680_, Direction p_71681_) {
         super(StructurePieceType.NETHER_FORTRESS_CASTLE_CORRIDOR_T_BALCONY, p_71679_, p_71680_);
         this.setOrientation(p_71681_);
      }

      public CastleCorridorTBalconyPiece(CompoundTag p_192124_) {
         super(StructurePieceType.NETHER_FORTRESS_CASTLE_CORRIDOR_T_BALCONY, p_192124_);
      }

      public void addChildren(StructurePiece p_162713_, StructurePieceAccessor p_162714_, Random p_162715_) {
         int i = 1;
         Direction direction = this.getOrientation();
         if (direction == Direction.WEST || direction == Direction.NORTH) {
            i = 5;
         }

         this.generateChildLeft((NetherBridgePieces.StartPiece)p_162713_, p_162714_, p_162715_, 0, i, p_162715_.nextInt(8) > 0);
         this.generateChildRight((NetherBridgePieces.StartPiece)p_162713_, p_162714_, p_162715_, 0, i, p_162715_.nextInt(8) > 0);
      }

      public static NetherBridgePieces.CastleCorridorTBalconyPiece createPiece(StructurePieceAccessor p_162717_, int p_162718_, int p_162719_, int p_162720_, Direction p_162721_, int p_162722_) {
         BoundingBox boundingbox = BoundingBox.orientBox(p_162718_, p_162719_, p_162720_, -3, 0, 0, 9, 7, 9, p_162721_);
         return isOkBox(boundingbox) && p_162717_.findCollisionPiece(boundingbox) == null ? new NetherBridgePieces.CastleCorridorTBalconyPiece(p_162722_, boundingbox, p_162721_) : null;
      }

      public void postProcess(WorldGenLevel p_192126_, StructureFeatureManager p_192127_, ChunkGenerator p_192128_, Random p_192129_, BoundingBox p_192130_, ChunkPos p_192131_, BlockPos p_192132_) {
         BlockState blockstate = Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, Boolean.valueOf(true)).setValue(FenceBlock.SOUTH, Boolean.valueOf(true));
         BlockState blockstate1 = Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, Boolean.valueOf(true)).setValue(FenceBlock.EAST, Boolean.valueOf(true));
         this.generateBox(p_192126_, p_192130_, 0, 0, 0, 8, 1, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192126_, p_192130_, 0, 2, 0, 8, 5, 8, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(p_192126_, p_192130_, 0, 6, 0, 8, 6, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192126_, p_192130_, 0, 2, 0, 2, 5, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192126_, p_192130_, 6, 2, 0, 8, 5, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192126_, p_192130_, 1, 3, 0, 1, 4, 0, blockstate1, blockstate1, false);
         this.generateBox(p_192126_, p_192130_, 7, 3, 0, 7, 4, 0, blockstate1, blockstate1, false);
         this.generateBox(p_192126_, p_192130_, 0, 2, 4, 8, 2, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192126_, p_192130_, 1, 1, 4, 2, 2, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(p_192126_, p_192130_, 6, 1, 4, 7, 2, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(p_192126_, p_192130_, 1, 3, 8, 7, 3, 8, blockstate1, blockstate1, false);
         this.placeBlock(p_192126_, Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.EAST, Boolean.valueOf(true)).setValue(FenceBlock.SOUTH, Boolean.valueOf(true)), 0, 3, 8, p_192130_);
         this.placeBlock(p_192126_, Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, Boolean.valueOf(true)).setValue(FenceBlock.SOUTH, Boolean.valueOf(true)), 8, 3, 8, p_192130_);
         this.generateBox(p_192126_, p_192130_, 0, 3, 6, 0, 3, 7, blockstate, blockstate, false);
         this.generateBox(p_192126_, p_192130_, 8, 3, 6, 8, 3, 7, blockstate, blockstate, false);
         this.generateBox(p_192126_, p_192130_, 0, 3, 4, 0, 5, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192126_, p_192130_, 8, 3, 4, 8, 5, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192126_, p_192130_, 1, 3, 5, 2, 5, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192126_, p_192130_, 6, 3, 5, 7, 5, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192126_, p_192130_, 1, 4, 5, 1, 5, 5, blockstate1, blockstate1, false);
         this.generateBox(p_192126_, p_192130_, 7, 4, 5, 7, 5, 5, blockstate1, blockstate1, false);

         for(int i = 0; i <= 5; ++i) {
            for(int j = 0; j <= 8; ++j) {
               this.fillColumnDown(p_192126_, Blocks.NETHER_BRICKS.defaultBlockState(), j, -1, i, p_192130_);
            }
         }

      }
   }

   public static class CastleEntrance extends NetherBridgePieces.NetherBridgePiece {
      private static final int WIDTH = 13;
      private static final int HEIGHT = 14;
      private static final int DEPTH = 13;

      public CastleEntrance(int p_71705_, Random p_71706_, BoundingBox p_71707_, Direction p_71708_) {
         super(StructurePieceType.NETHER_FORTRESS_CASTLE_ENTRANCE, p_71705_, p_71707_);
         this.setOrientation(p_71708_);
      }

      public CastleEntrance(CompoundTag p_192134_) {
         super(StructurePieceType.NETHER_FORTRESS_CASTLE_ENTRANCE, p_192134_);
      }

      public void addChildren(StructurePiece p_162730_, StructurePieceAccessor p_162731_, Random p_162732_) {
         this.generateChildForward((NetherBridgePieces.StartPiece)p_162730_, p_162731_, p_162732_, 5, 3, true);
      }

      public static NetherBridgePieces.CastleEntrance createPiece(StructurePieceAccessor p_162734_, Random p_162735_, int p_162736_, int p_162737_, int p_162738_, Direction p_162739_, int p_162740_) {
         BoundingBox boundingbox = BoundingBox.orientBox(p_162736_, p_162737_, p_162738_, -5, -3, 0, 13, 14, 13, p_162739_);
         return isOkBox(boundingbox) && p_162734_.findCollisionPiece(boundingbox) == null ? new NetherBridgePieces.CastleEntrance(p_162740_, p_162735_, boundingbox, p_162739_) : null;
      }

      public void postProcess(WorldGenLevel p_192136_, StructureFeatureManager p_192137_, ChunkGenerator p_192138_, Random p_192139_, BoundingBox p_192140_, ChunkPos p_192141_, BlockPos p_192142_) {
         this.generateBox(p_192136_, p_192140_, 0, 3, 0, 12, 4, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192136_, p_192140_, 0, 5, 0, 12, 13, 12, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(p_192136_, p_192140_, 0, 5, 0, 1, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192136_, p_192140_, 11, 5, 0, 12, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192136_, p_192140_, 2, 5, 11, 4, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192136_, p_192140_, 8, 5, 11, 10, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192136_, p_192140_, 5, 9, 11, 7, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192136_, p_192140_, 2, 5, 0, 4, 12, 1, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192136_, p_192140_, 8, 5, 0, 10, 12, 1, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192136_, p_192140_, 5, 9, 0, 7, 12, 1, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192136_, p_192140_, 2, 11, 2, 10, 12, 10, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192136_, p_192140_, 5, 8, 0, 7, 8, 0, Blocks.NETHER_BRICK_FENCE.defaultBlockState(), Blocks.NETHER_BRICK_FENCE.defaultBlockState(), false);
         BlockState blockstate = Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, Boolean.valueOf(true)).setValue(FenceBlock.EAST, Boolean.valueOf(true));
         BlockState blockstate1 = Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, Boolean.valueOf(true)).setValue(FenceBlock.SOUTH, Boolean.valueOf(true));

         for(int i = 1; i <= 11; i += 2) {
            this.generateBox(p_192136_, p_192140_, i, 10, 0, i, 11, 0, blockstate, blockstate, false);
            this.generateBox(p_192136_, p_192140_, i, 10, 12, i, 11, 12, blockstate, blockstate, false);
            this.generateBox(p_192136_, p_192140_, 0, 10, i, 0, 11, i, blockstate1, blockstate1, false);
            this.generateBox(p_192136_, p_192140_, 12, 10, i, 12, 11, i, blockstate1, blockstate1, false);
            this.placeBlock(p_192136_, Blocks.NETHER_BRICKS.defaultBlockState(), i, 13, 0, p_192140_);
            this.placeBlock(p_192136_, Blocks.NETHER_BRICKS.defaultBlockState(), i, 13, 12, p_192140_);
            this.placeBlock(p_192136_, Blocks.NETHER_BRICKS.defaultBlockState(), 0, 13, i, p_192140_);
            this.placeBlock(p_192136_, Blocks.NETHER_BRICKS.defaultBlockState(), 12, 13, i, p_192140_);
            if (i != 11) {
               this.placeBlock(p_192136_, blockstate, i + 1, 13, 0, p_192140_);
               this.placeBlock(p_192136_, blockstate, i + 1, 13, 12, p_192140_);
               this.placeBlock(p_192136_, blockstate1, 0, 13, i + 1, p_192140_);
               this.placeBlock(p_192136_, blockstate1, 12, 13, i + 1, p_192140_);
            }
         }

         this.placeBlock(p_192136_, Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, Boolean.valueOf(true)).setValue(FenceBlock.EAST, Boolean.valueOf(true)), 0, 13, 0, p_192140_);
         this.placeBlock(p_192136_, Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.SOUTH, Boolean.valueOf(true)).setValue(FenceBlock.EAST, Boolean.valueOf(true)), 0, 13, 12, p_192140_);
         this.placeBlock(p_192136_, Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.SOUTH, Boolean.valueOf(true)).setValue(FenceBlock.WEST, Boolean.valueOf(true)), 12, 13, 12, p_192140_);
         this.placeBlock(p_192136_, Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, Boolean.valueOf(true)).setValue(FenceBlock.WEST, Boolean.valueOf(true)), 12, 13, 0, p_192140_);

         for(int k = 3; k <= 9; k += 2) {
            this.generateBox(p_192136_, p_192140_, 1, 7, k, 1, 8, k, blockstate1.setValue(FenceBlock.WEST, Boolean.valueOf(true)), blockstate1.setValue(FenceBlock.WEST, Boolean.valueOf(true)), false);
            this.generateBox(p_192136_, p_192140_, 11, 7, k, 11, 8, k, blockstate1.setValue(FenceBlock.EAST, Boolean.valueOf(true)), blockstate1.setValue(FenceBlock.EAST, Boolean.valueOf(true)), false);
         }

         this.generateBox(p_192136_, p_192140_, 4, 2, 0, 8, 2, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192136_, p_192140_, 0, 2, 4, 12, 2, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192136_, p_192140_, 4, 0, 0, 8, 1, 3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192136_, p_192140_, 4, 0, 9, 8, 1, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192136_, p_192140_, 0, 0, 4, 3, 1, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192136_, p_192140_, 9, 0, 4, 12, 1, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);

         for(int l = 4; l <= 8; ++l) {
            for(int j = 0; j <= 2; ++j) {
               this.fillColumnDown(p_192136_, Blocks.NETHER_BRICKS.defaultBlockState(), l, -1, j, p_192140_);
               this.fillColumnDown(p_192136_, Blocks.NETHER_BRICKS.defaultBlockState(), l, -1, 12 - j, p_192140_);
            }
         }

         for(int i1 = 0; i1 <= 2; ++i1) {
            for(int j1 = 4; j1 <= 8; ++j1) {
               this.fillColumnDown(p_192136_, Blocks.NETHER_BRICKS.defaultBlockState(), i1, -1, j1, p_192140_);
               this.fillColumnDown(p_192136_, Blocks.NETHER_BRICKS.defaultBlockState(), 12 - i1, -1, j1, p_192140_);
            }
         }

         this.generateBox(p_192136_, p_192140_, 5, 5, 5, 7, 5, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192136_, p_192140_, 6, 1, 6, 6, 4, 6, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.placeBlock(p_192136_, Blocks.NETHER_BRICKS.defaultBlockState(), 6, 0, 6, p_192140_);
         this.placeBlock(p_192136_, Blocks.LAVA.defaultBlockState(), 6, 5, 6, p_192140_);
         BlockPos blockpos = this.getWorldPos(6, 5, 6);
         if (p_192140_.isInside(blockpos)) {
            p_192136_.scheduleTick(blockpos, Fluids.LAVA, 0);
         }

      }
   }

   public static class CastleSmallCorridorCrossingPiece extends NetherBridgePieces.NetherBridgePiece {
      private static final int WIDTH = 5;
      private static final int HEIGHT = 7;
      private static final int DEPTH = 5;

      public CastleSmallCorridorCrossingPiece(int p_71733_, BoundingBox p_71734_, Direction p_71735_) {
         super(StructurePieceType.NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_CROSSING, p_71733_, p_71734_);
         this.setOrientation(p_71735_);
      }

      public CastleSmallCorridorCrossingPiece(CompoundTag p_192144_) {
         super(StructurePieceType.NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_CROSSING, p_192144_);
      }

      public void addChildren(StructurePiece p_162748_, StructurePieceAccessor p_162749_, Random p_162750_) {
         this.generateChildForward((NetherBridgePieces.StartPiece)p_162748_, p_162749_, p_162750_, 1, 0, true);
         this.generateChildLeft((NetherBridgePieces.StartPiece)p_162748_, p_162749_, p_162750_, 0, 1, true);
         this.generateChildRight((NetherBridgePieces.StartPiece)p_162748_, p_162749_, p_162750_, 0, 1, true);
      }

      public static NetherBridgePieces.CastleSmallCorridorCrossingPiece createPiece(StructurePieceAccessor p_162752_, int p_162753_, int p_162754_, int p_162755_, Direction p_162756_, int p_162757_) {
         BoundingBox boundingbox = BoundingBox.orientBox(p_162753_, p_162754_, p_162755_, -1, 0, 0, 5, 7, 5, p_162756_);
         return isOkBox(boundingbox) && p_162752_.findCollisionPiece(boundingbox) == null ? new NetherBridgePieces.CastleSmallCorridorCrossingPiece(p_162757_, boundingbox, p_162756_) : null;
      }

      public void postProcess(WorldGenLevel p_192146_, StructureFeatureManager p_192147_, ChunkGenerator p_192148_, Random p_192149_, BoundingBox p_192150_, ChunkPos p_192151_, BlockPos p_192152_) {
         this.generateBox(p_192146_, p_192150_, 0, 0, 0, 4, 1, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192146_, p_192150_, 0, 2, 0, 4, 5, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(p_192146_, p_192150_, 0, 2, 0, 0, 5, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192146_, p_192150_, 4, 2, 0, 4, 5, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192146_, p_192150_, 0, 2, 4, 0, 5, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192146_, p_192150_, 4, 2, 4, 4, 5, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192146_, p_192150_, 0, 6, 0, 4, 6, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);

         for(int i = 0; i <= 4; ++i) {
            for(int j = 0; j <= 4; ++j) {
               this.fillColumnDown(p_192146_, Blocks.NETHER_BRICKS.defaultBlockState(), i, -1, j, p_192150_);
            }
         }

      }
   }

   public static class CastleSmallCorridorLeftTurnPiece extends NetherBridgePieces.NetherBridgePiece {
      private static final int WIDTH = 5;
      private static final int HEIGHT = 7;
      private static final int DEPTH = 5;
      private boolean isNeedingChest;

      public CastleSmallCorridorLeftTurnPiece(int p_71760_, Random p_71761_, BoundingBox p_71762_, Direction p_71763_) {
         super(StructurePieceType.NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_LEFT_TURN, p_71760_, p_71762_);
         this.setOrientation(p_71763_);
         this.isNeedingChest = p_71761_.nextInt(3) == 0;
      }

      public CastleSmallCorridorLeftTurnPiece(CompoundTag p_192154_) {
         super(StructurePieceType.NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_LEFT_TURN, p_192154_);
         this.isNeedingChest = p_192154_.getBoolean("Chest");
      }

      protected void addAdditionalSaveData(StructurePieceSerializationContext p_192164_, CompoundTag p_192165_) {
         super.addAdditionalSaveData(p_192164_, p_192165_);
         p_192165_.putBoolean("Chest", this.isNeedingChest);
      }

      public void addChildren(StructurePiece p_162768_, StructurePieceAccessor p_162769_, Random p_162770_) {
         this.generateChildLeft((NetherBridgePieces.StartPiece)p_162768_, p_162769_, p_162770_, 0, 1, true);
      }

      public static NetherBridgePieces.CastleSmallCorridorLeftTurnPiece createPiece(StructurePieceAccessor p_162772_, Random p_162773_, int p_162774_, int p_162775_, int p_162776_, Direction p_162777_, int p_162778_) {
         BoundingBox boundingbox = BoundingBox.orientBox(p_162774_, p_162775_, p_162776_, -1, 0, 0, 5, 7, 5, p_162777_);
         return isOkBox(boundingbox) && p_162772_.findCollisionPiece(boundingbox) == null ? new NetherBridgePieces.CastleSmallCorridorLeftTurnPiece(p_162778_, p_162773_, boundingbox, p_162777_) : null;
      }

      public void postProcess(WorldGenLevel p_192156_, StructureFeatureManager p_192157_, ChunkGenerator p_192158_, Random p_192159_, BoundingBox p_192160_, ChunkPos p_192161_, BlockPos p_192162_) {
         this.generateBox(p_192156_, p_192160_, 0, 0, 0, 4, 1, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192156_, p_192160_, 0, 2, 0, 4, 5, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         BlockState blockstate = Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, Boolean.valueOf(true)).setValue(FenceBlock.EAST, Boolean.valueOf(true));
         BlockState blockstate1 = Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, Boolean.valueOf(true)).setValue(FenceBlock.SOUTH, Boolean.valueOf(true));
         this.generateBox(p_192156_, p_192160_, 4, 2, 0, 4, 5, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192156_, p_192160_, 4, 3, 1, 4, 4, 1, blockstate1, blockstate1, false);
         this.generateBox(p_192156_, p_192160_, 4, 3, 3, 4, 4, 3, blockstate1, blockstate1, false);
         this.generateBox(p_192156_, p_192160_, 0, 2, 0, 0, 5, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192156_, p_192160_, 0, 2, 4, 3, 5, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192156_, p_192160_, 1, 3, 4, 1, 4, 4, blockstate, blockstate, false);
         this.generateBox(p_192156_, p_192160_, 3, 3, 4, 3, 4, 4, blockstate, blockstate, false);
         if (this.isNeedingChest && p_192160_.isInside(this.getWorldPos(3, 2, 3))) {
            this.isNeedingChest = false;
            this.createChest(p_192156_, p_192160_, p_192159_, 3, 2, 3, BuiltInLootTables.NETHER_BRIDGE);
         }

         this.generateBox(p_192156_, p_192160_, 0, 6, 0, 4, 6, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);

         for(int i = 0; i <= 4; ++i) {
            for(int j = 0; j <= 4; ++j) {
               this.fillColumnDown(p_192156_, Blocks.NETHER_BRICKS.defaultBlockState(), i, -1, j, p_192160_);
            }
         }

      }
   }

   public static class CastleSmallCorridorPiece extends NetherBridgePieces.NetherBridgePiece {
      private static final int WIDTH = 5;
      private static final int HEIGHT = 7;
      private static final int DEPTH = 5;

      public CastleSmallCorridorPiece(int p_71790_, BoundingBox p_71791_, Direction p_71792_) {
         super(StructurePieceType.NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR, p_71790_, p_71791_);
         this.setOrientation(p_71792_);
      }

      public CastleSmallCorridorPiece(CompoundTag p_192167_) {
         super(StructurePieceType.NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR, p_192167_);
      }

      public void addChildren(StructurePiece p_162786_, StructurePieceAccessor p_162787_, Random p_162788_) {
         this.generateChildForward((NetherBridgePieces.StartPiece)p_162786_, p_162787_, p_162788_, 1, 0, true);
      }

      public static NetherBridgePieces.CastleSmallCorridorPiece createPiece(StructurePieceAccessor p_162790_, int p_162791_, int p_162792_, int p_162793_, Direction p_162794_, int p_162795_) {
         BoundingBox boundingbox = BoundingBox.orientBox(p_162791_, p_162792_, p_162793_, -1, 0, 0, 5, 7, 5, p_162794_);
         return isOkBox(boundingbox) && p_162790_.findCollisionPiece(boundingbox) == null ? new NetherBridgePieces.CastleSmallCorridorPiece(p_162795_, boundingbox, p_162794_) : null;
      }

      public void postProcess(WorldGenLevel p_192169_, StructureFeatureManager p_192170_, ChunkGenerator p_192171_, Random p_192172_, BoundingBox p_192173_, ChunkPos p_192174_, BlockPos p_192175_) {
         this.generateBox(p_192169_, p_192173_, 0, 0, 0, 4, 1, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192169_, p_192173_, 0, 2, 0, 4, 5, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         BlockState blockstate = Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, Boolean.valueOf(true)).setValue(FenceBlock.SOUTH, Boolean.valueOf(true));
         this.generateBox(p_192169_, p_192173_, 0, 2, 0, 0, 5, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192169_, p_192173_, 4, 2, 0, 4, 5, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192169_, p_192173_, 0, 3, 1, 0, 4, 1, blockstate, blockstate, false);
         this.generateBox(p_192169_, p_192173_, 0, 3, 3, 0, 4, 3, blockstate, blockstate, false);
         this.generateBox(p_192169_, p_192173_, 4, 3, 1, 4, 4, 1, blockstate, blockstate, false);
         this.generateBox(p_192169_, p_192173_, 4, 3, 3, 4, 4, 3, blockstate, blockstate, false);
         this.generateBox(p_192169_, p_192173_, 0, 6, 0, 4, 6, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);

         for(int i = 0; i <= 4; ++i) {
            for(int j = 0; j <= 4; ++j) {
               this.fillColumnDown(p_192169_, Blocks.NETHER_BRICKS.defaultBlockState(), i, -1, j, p_192173_);
            }
         }

      }
   }

   public static class CastleSmallCorridorRightTurnPiece extends NetherBridgePieces.NetherBridgePiece {
      private static final int WIDTH = 5;
      private static final int HEIGHT = 7;
      private static final int DEPTH = 5;
      private boolean isNeedingChest;

      public CastleSmallCorridorRightTurnPiece(int p_71817_, Random p_71818_, BoundingBox p_71819_, Direction p_71820_) {
         super(StructurePieceType.NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_RIGHT_TURN, p_71817_, p_71819_);
         this.setOrientation(p_71820_);
         this.isNeedingChest = p_71818_.nextInt(3) == 0;
      }

      public CastleSmallCorridorRightTurnPiece(CompoundTag p_192177_) {
         super(StructurePieceType.NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_RIGHT_TURN, p_192177_);
         this.isNeedingChest = p_192177_.getBoolean("Chest");
      }

      protected void addAdditionalSaveData(StructurePieceSerializationContext p_192187_, CompoundTag p_192188_) {
         super.addAdditionalSaveData(p_192187_, p_192188_);
         p_192188_.putBoolean("Chest", this.isNeedingChest);
      }

      public void addChildren(StructurePiece p_162806_, StructurePieceAccessor p_162807_, Random p_162808_) {
         this.generateChildRight((NetherBridgePieces.StartPiece)p_162806_, p_162807_, p_162808_, 0, 1, true);
      }

      public static NetherBridgePieces.CastleSmallCorridorRightTurnPiece createPiece(StructurePieceAccessor p_162810_, Random p_162811_, int p_162812_, int p_162813_, int p_162814_, Direction p_162815_, int p_162816_) {
         BoundingBox boundingbox = BoundingBox.orientBox(p_162812_, p_162813_, p_162814_, -1, 0, 0, 5, 7, 5, p_162815_);
         return isOkBox(boundingbox) && p_162810_.findCollisionPiece(boundingbox) == null ? new NetherBridgePieces.CastleSmallCorridorRightTurnPiece(p_162816_, p_162811_, boundingbox, p_162815_) : null;
      }

      public void postProcess(WorldGenLevel p_192179_, StructureFeatureManager p_192180_, ChunkGenerator p_192181_, Random p_192182_, BoundingBox p_192183_, ChunkPos p_192184_, BlockPos p_192185_) {
         this.generateBox(p_192179_, p_192183_, 0, 0, 0, 4, 1, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192179_, p_192183_, 0, 2, 0, 4, 5, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         BlockState blockstate = Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, Boolean.valueOf(true)).setValue(FenceBlock.EAST, Boolean.valueOf(true));
         BlockState blockstate1 = Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, Boolean.valueOf(true)).setValue(FenceBlock.SOUTH, Boolean.valueOf(true));
         this.generateBox(p_192179_, p_192183_, 0, 2, 0, 0, 5, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192179_, p_192183_, 0, 3, 1, 0, 4, 1, blockstate1, blockstate1, false);
         this.generateBox(p_192179_, p_192183_, 0, 3, 3, 0, 4, 3, blockstate1, blockstate1, false);
         this.generateBox(p_192179_, p_192183_, 4, 2, 0, 4, 5, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192179_, p_192183_, 1, 2, 4, 4, 5, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192179_, p_192183_, 1, 3, 4, 1, 4, 4, blockstate, blockstate, false);
         this.generateBox(p_192179_, p_192183_, 3, 3, 4, 3, 4, 4, blockstate, blockstate, false);
         if (this.isNeedingChest && p_192183_.isInside(this.getWorldPos(1, 2, 3))) {
            this.isNeedingChest = false;
            this.createChest(p_192179_, p_192183_, p_192182_, 1, 2, 3, BuiltInLootTables.NETHER_BRIDGE);
         }

         this.generateBox(p_192179_, p_192183_, 0, 6, 0, 4, 6, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);

         for(int i = 0; i <= 4; ++i) {
            for(int j = 0; j <= 4; ++j) {
               this.fillColumnDown(p_192179_, Blocks.NETHER_BRICKS.defaultBlockState(), i, -1, j, p_192183_);
            }
         }

      }
   }

   public static class CastleStalkRoom extends NetherBridgePieces.NetherBridgePiece {
      private static final int WIDTH = 13;
      private static final int HEIGHT = 14;
      private static final int DEPTH = 13;

      public CastleStalkRoom(int p_71847_, BoundingBox p_71848_, Direction p_71849_) {
         super(StructurePieceType.NETHER_FORTRESS_CASTLE_STALK_ROOM, p_71847_, p_71848_);
         this.setOrientation(p_71849_);
      }

      public CastleStalkRoom(CompoundTag p_192190_) {
         super(StructurePieceType.NETHER_FORTRESS_CASTLE_STALK_ROOM, p_192190_);
      }

      public void addChildren(StructurePiece p_162824_, StructurePieceAccessor p_162825_, Random p_162826_) {
         this.generateChildForward((NetherBridgePieces.StartPiece)p_162824_, p_162825_, p_162826_, 5, 3, true);
         this.generateChildForward((NetherBridgePieces.StartPiece)p_162824_, p_162825_, p_162826_, 5, 11, true);
      }

      public static NetherBridgePieces.CastleStalkRoom createPiece(StructurePieceAccessor p_162828_, int p_162829_, int p_162830_, int p_162831_, Direction p_162832_, int p_162833_) {
         BoundingBox boundingbox = BoundingBox.orientBox(p_162829_, p_162830_, p_162831_, -5, -3, 0, 13, 14, 13, p_162832_);
         return isOkBox(boundingbox) && p_162828_.findCollisionPiece(boundingbox) == null ? new NetherBridgePieces.CastleStalkRoom(p_162833_, boundingbox, p_162832_) : null;
      }

      public void postProcess(WorldGenLevel p_192192_, StructureFeatureManager p_192193_, ChunkGenerator p_192194_, Random p_192195_, BoundingBox p_192196_, ChunkPos p_192197_, BlockPos p_192198_) {
         this.generateBox(p_192192_, p_192196_, 0, 3, 0, 12, 4, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192192_, p_192196_, 0, 5, 0, 12, 13, 12, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(p_192192_, p_192196_, 0, 5, 0, 1, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192192_, p_192196_, 11, 5, 0, 12, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192192_, p_192196_, 2, 5, 11, 4, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192192_, p_192196_, 8, 5, 11, 10, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192192_, p_192196_, 5, 9, 11, 7, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192192_, p_192196_, 2, 5, 0, 4, 12, 1, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192192_, p_192196_, 8, 5, 0, 10, 12, 1, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192192_, p_192196_, 5, 9, 0, 7, 12, 1, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192192_, p_192196_, 2, 11, 2, 10, 12, 10, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         BlockState blockstate = Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, Boolean.valueOf(true)).setValue(FenceBlock.EAST, Boolean.valueOf(true));
         BlockState blockstate1 = Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, Boolean.valueOf(true)).setValue(FenceBlock.SOUTH, Boolean.valueOf(true));
         BlockState blockstate2 = blockstate1.setValue(FenceBlock.WEST, Boolean.valueOf(true));
         BlockState blockstate3 = blockstate1.setValue(FenceBlock.EAST, Boolean.valueOf(true));

         for(int i = 1; i <= 11; i += 2) {
            this.generateBox(p_192192_, p_192196_, i, 10, 0, i, 11, 0, blockstate, blockstate, false);
            this.generateBox(p_192192_, p_192196_, i, 10, 12, i, 11, 12, blockstate, blockstate, false);
            this.generateBox(p_192192_, p_192196_, 0, 10, i, 0, 11, i, blockstate1, blockstate1, false);
            this.generateBox(p_192192_, p_192196_, 12, 10, i, 12, 11, i, blockstate1, blockstate1, false);
            this.placeBlock(p_192192_, Blocks.NETHER_BRICKS.defaultBlockState(), i, 13, 0, p_192196_);
            this.placeBlock(p_192192_, Blocks.NETHER_BRICKS.defaultBlockState(), i, 13, 12, p_192196_);
            this.placeBlock(p_192192_, Blocks.NETHER_BRICKS.defaultBlockState(), 0, 13, i, p_192196_);
            this.placeBlock(p_192192_, Blocks.NETHER_BRICKS.defaultBlockState(), 12, 13, i, p_192196_);
            if (i != 11) {
               this.placeBlock(p_192192_, blockstate, i + 1, 13, 0, p_192196_);
               this.placeBlock(p_192192_, blockstate, i + 1, 13, 12, p_192196_);
               this.placeBlock(p_192192_, blockstate1, 0, 13, i + 1, p_192196_);
               this.placeBlock(p_192192_, blockstate1, 12, 13, i + 1, p_192196_);
            }
         }

         this.placeBlock(p_192192_, Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, Boolean.valueOf(true)).setValue(FenceBlock.EAST, Boolean.valueOf(true)), 0, 13, 0, p_192196_);
         this.placeBlock(p_192192_, Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.SOUTH, Boolean.valueOf(true)).setValue(FenceBlock.EAST, Boolean.valueOf(true)), 0, 13, 12, p_192196_);
         this.placeBlock(p_192192_, Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.SOUTH, Boolean.valueOf(true)).setValue(FenceBlock.WEST, Boolean.valueOf(true)), 12, 13, 12, p_192196_);
         this.placeBlock(p_192192_, Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, Boolean.valueOf(true)).setValue(FenceBlock.WEST, Boolean.valueOf(true)), 12, 13, 0, p_192196_);

         for(int j1 = 3; j1 <= 9; j1 += 2) {
            this.generateBox(p_192192_, p_192196_, 1, 7, j1, 1, 8, j1, blockstate2, blockstate2, false);
            this.generateBox(p_192192_, p_192196_, 11, 7, j1, 11, 8, j1, blockstate3, blockstate3, false);
         }

         BlockState blockstate4 = Blocks.NETHER_BRICK_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.NORTH);

         for(int j = 0; j <= 6; ++j) {
            int k = j + 4;

            for(int l = 5; l <= 7; ++l) {
               this.placeBlock(p_192192_, blockstate4, l, 5 + j, k, p_192196_);
            }

            if (k >= 5 && k <= 8) {
               this.generateBox(p_192192_, p_192196_, 5, 5, k, 7, j + 4, k, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            } else if (k >= 9 && k <= 10) {
               this.generateBox(p_192192_, p_192196_, 5, 8, k, 7, j + 4, k, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            }

            if (j >= 1) {
               this.generateBox(p_192192_, p_192196_, 5, 6 + j, k, 7, 9 + j, k, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            }
         }

         for(int k1 = 5; k1 <= 7; ++k1) {
            this.placeBlock(p_192192_, blockstate4, k1, 12, 11, p_192196_);
         }

         this.generateBox(p_192192_, p_192196_, 5, 6, 7, 5, 7, 7, blockstate3, blockstate3, false);
         this.generateBox(p_192192_, p_192196_, 7, 6, 7, 7, 7, 7, blockstate2, blockstate2, false);
         this.generateBox(p_192192_, p_192196_, 5, 13, 12, 7, 13, 12, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(p_192192_, p_192196_, 2, 5, 2, 3, 5, 3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192192_, p_192196_, 2, 5, 9, 3, 5, 10, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192192_, p_192196_, 2, 5, 4, 2, 5, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192192_, p_192196_, 9, 5, 2, 10, 5, 3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192192_, p_192196_, 9, 5, 9, 10, 5, 10, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192192_, p_192196_, 10, 5, 4, 10, 5, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         BlockState blockstate5 = blockstate4.setValue(StairBlock.FACING, Direction.EAST);
         BlockState blockstate6 = blockstate4.setValue(StairBlock.FACING, Direction.WEST);
         this.placeBlock(p_192192_, blockstate6, 4, 5, 2, p_192196_);
         this.placeBlock(p_192192_, blockstate6, 4, 5, 3, p_192196_);
         this.placeBlock(p_192192_, blockstate6, 4, 5, 9, p_192196_);
         this.placeBlock(p_192192_, blockstate6, 4, 5, 10, p_192196_);
         this.placeBlock(p_192192_, blockstate5, 8, 5, 2, p_192196_);
         this.placeBlock(p_192192_, blockstate5, 8, 5, 3, p_192196_);
         this.placeBlock(p_192192_, blockstate5, 8, 5, 9, p_192196_);
         this.placeBlock(p_192192_, blockstate5, 8, 5, 10, p_192196_);
         this.generateBox(p_192192_, p_192196_, 3, 4, 4, 4, 4, 8, Blocks.SOUL_SAND.defaultBlockState(), Blocks.SOUL_SAND.defaultBlockState(), false);
         this.generateBox(p_192192_, p_192196_, 8, 4, 4, 9, 4, 8, Blocks.SOUL_SAND.defaultBlockState(), Blocks.SOUL_SAND.defaultBlockState(), false);
         this.generateBox(p_192192_, p_192196_, 3, 5, 4, 4, 5, 8, Blocks.NETHER_WART.defaultBlockState(), Blocks.NETHER_WART.defaultBlockState(), false);
         this.generateBox(p_192192_, p_192196_, 8, 5, 4, 9, 5, 8, Blocks.NETHER_WART.defaultBlockState(), Blocks.NETHER_WART.defaultBlockState(), false);
         this.generateBox(p_192192_, p_192196_, 4, 2, 0, 8, 2, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192192_, p_192196_, 0, 2, 4, 12, 2, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192192_, p_192196_, 4, 0, 0, 8, 1, 3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192192_, p_192196_, 4, 0, 9, 8, 1, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192192_, p_192196_, 0, 0, 4, 3, 1, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192192_, p_192196_, 9, 0, 4, 12, 1, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);

         for(int l1 = 4; l1 <= 8; ++l1) {
            for(int i1 = 0; i1 <= 2; ++i1) {
               this.fillColumnDown(p_192192_, Blocks.NETHER_BRICKS.defaultBlockState(), l1, -1, i1, p_192196_);
               this.fillColumnDown(p_192192_, Blocks.NETHER_BRICKS.defaultBlockState(), l1, -1, 12 - i1, p_192196_);
            }
         }

         for(int i2 = 0; i2 <= 2; ++i2) {
            for(int j2 = 4; j2 <= 8; ++j2) {
               this.fillColumnDown(p_192192_, Blocks.NETHER_BRICKS.defaultBlockState(), i2, -1, j2, p_192196_);
               this.fillColumnDown(p_192192_, Blocks.NETHER_BRICKS.defaultBlockState(), 12 - i2, -1, j2, p_192196_);
            }
         }

      }
   }

   public static class MonsterThrone extends NetherBridgePieces.NetherBridgePiece {
      private static final int WIDTH = 7;
      private static final int HEIGHT = 8;
      private static final int DEPTH = 9;
      private boolean hasPlacedSpawner;

      public MonsterThrone(int p_71874_, BoundingBox p_71875_, Direction p_71876_) {
         super(StructurePieceType.NETHER_FORTRESS_MONSTER_THRONE, p_71874_, p_71875_);
         this.setOrientation(p_71876_);
      }

      public MonsterThrone(CompoundTag p_192200_) {
         super(StructurePieceType.NETHER_FORTRESS_MONSTER_THRONE, p_192200_);
         this.hasPlacedSpawner = p_192200_.getBoolean("Mob");
      }

      protected void addAdditionalSaveData(StructurePieceSerializationContext p_192210_, CompoundTag p_192211_) {
         super.addAdditionalSaveData(p_192210_, p_192211_);
         p_192211_.putBoolean("Mob", this.hasPlacedSpawner);
      }

      public static NetherBridgePieces.MonsterThrone createPiece(StructurePieceAccessor p_162844_, int p_162845_, int p_162846_, int p_162847_, int p_162848_, Direction p_162849_) {
         BoundingBox boundingbox = BoundingBox.orientBox(p_162845_, p_162846_, p_162847_, -2, 0, 0, 7, 8, 9, p_162849_);
         return isOkBox(boundingbox) && p_162844_.findCollisionPiece(boundingbox) == null ? new NetherBridgePieces.MonsterThrone(p_162848_, boundingbox, p_162849_) : null;
      }

      public void postProcess(WorldGenLevel p_192202_, StructureFeatureManager p_192203_, ChunkGenerator p_192204_, Random p_192205_, BoundingBox p_192206_, ChunkPos p_192207_, BlockPos p_192208_) {
         this.generateBox(p_192202_, p_192206_, 0, 2, 0, 6, 7, 7, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(p_192202_, p_192206_, 1, 0, 0, 5, 1, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192202_, p_192206_, 1, 2, 1, 5, 2, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192202_, p_192206_, 1, 3, 2, 5, 3, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192202_, p_192206_, 1, 4, 3, 5, 4, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192202_, p_192206_, 1, 2, 0, 1, 4, 2, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192202_, p_192206_, 5, 2, 0, 5, 4, 2, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192202_, p_192206_, 1, 5, 2, 1, 5, 3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192202_, p_192206_, 5, 5, 2, 5, 5, 3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192202_, p_192206_, 0, 5, 3, 0, 5, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192202_, p_192206_, 6, 5, 3, 6, 5, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192202_, p_192206_, 1, 5, 8, 5, 5, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         BlockState blockstate = Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, Boolean.valueOf(true)).setValue(FenceBlock.EAST, Boolean.valueOf(true));
         BlockState blockstate1 = Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, Boolean.valueOf(true)).setValue(FenceBlock.SOUTH, Boolean.valueOf(true));
         this.placeBlock(p_192202_, Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, Boolean.valueOf(true)), 1, 6, 3, p_192206_);
         this.placeBlock(p_192202_, Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.EAST, Boolean.valueOf(true)), 5, 6, 3, p_192206_);
         this.placeBlock(p_192202_, Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.EAST, Boolean.valueOf(true)).setValue(FenceBlock.NORTH, Boolean.valueOf(true)), 0, 6, 3, p_192206_);
         this.placeBlock(p_192202_, Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, Boolean.valueOf(true)).setValue(FenceBlock.NORTH, Boolean.valueOf(true)), 6, 6, 3, p_192206_);
         this.generateBox(p_192202_, p_192206_, 0, 6, 4, 0, 6, 7, blockstate1, blockstate1, false);
         this.generateBox(p_192202_, p_192206_, 6, 6, 4, 6, 6, 7, blockstate1, blockstate1, false);
         this.placeBlock(p_192202_, Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.EAST, Boolean.valueOf(true)).setValue(FenceBlock.SOUTH, Boolean.valueOf(true)), 0, 6, 8, p_192206_);
         this.placeBlock(p_192202_, Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, Boolean.valueOf(true)).setValue(FenceBlock.SOUTH, Boolean.valueOf(true)), 6, 6, 8, p_192206_);
         this.generateBox(p_192202_, p_192206_, 1, 6, 8, 5, 6, 8, blockstate, blockstate, false);
         this.placeBlock(p_192202_, Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.EAST, Boolean.valueOf(true)), 1, 7, 8, p_192206_);
         this.generateBox(p_192202_, p_192206_, 2, 7, 8, 4, 7, 8, blockstate, blockstate, false);
         this.placeBlock(p_192202_, Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, Boolean.valueOf(true)), 5, 7, 8, p_192206_);
         this.placeBlock(p_192202_, Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.EAST, Boolean.valueOf(true)), 2, 8, 8, p_192206_);
         this.placeBlock(p_192202_, blockstate, 3, 8, 8, p_192206_);
         this.placeBlock(p_192202_, Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, Boolean.valueOf(true)), 4, 8, 8, p_192206_);
         if (!this.hasPlacedSpawner) {
            BlockPos blockpos = this.getWorldPos(3, 5, 5);
            if (p_192206_.isInside(blockpos)) {
               this.hasPlacedSpawner = true;
               p_192202_.setBlock(blockpos, Blocks.SPAWNER.defaultBlockState(), 2);
               BlockEntity blockentity = p_192202_.getBlockEntity(blockpos);
               if (blockentity instanceof SpawnerBlockEntity) {
                  ((SpawnerBlockEntity)blockentity).getSpawner().setEntityId(EntityType.BLAZE);
               }
            }
         }

         for(int i = 0; i <= 6; ++i) {
            for(int j = 0; j <= 6; ++j) {
               this.fillColumnDown(p_192202_, Blocks.NETHER_BRICKS.defaultBlockState(), i, -1, j, p_192206_);
            }
         }

      }
   }

   abstract static class NetherBridgePiece extends StructurePiece {
      protected NetherBridgePiece(StructurePieceType p_209887_, int p_209888_, BoundingBox p_209889_) {
         super(p_209887_, p_209888_, p_209889_);
      }

      public NetherBridgePiece(StructurePieceType p_209891_, CompoundTag p_209892_) {
         super(p_209891_, p_209892_);
      }

      protected void addAdditionalSaveData(StructurePieceSerializationContext p_192213_, CompoundTag p_192214_) {
      }

      private int updatePieceWeight(List<NetherBridgePieces.PieceWeight> p_71933_) {
         boolean flag = false;
         int i = 0;

         for(NetherBridgePieces.PieceWeight netherbridgepieces$pieceweight : p_71933_) {
            if (netherbridgepieces$pieceweight.maxPlaceCount > 0 && netherbridgepieces$pieceweight.placeCount < netherbridgepieces$pieceweight.maxPlaceCount) {
               flag = true;
            }

            i += netherbridgepieces$pieceweight.weight;
         }

         return flag ? i : -1;
      }

      private NetherBridgePieces.NetherBridgePiece generatePiece(NetherBridgePieces.StartPiece p_162875_, List<NetherBridgePieces.PieceWeight> p_162876_, StructurePieceAccessor p_162877_, Random p_162878_, int p_162879_, int p_162880_, int p_162881_, Direction p_162882_, int p_162883_) {
         int i = this.updatePieceWeight(p_162876_);
         boolean flag = i > 0 && p_162883_ <= 30;
         int j = 0;

         while(j < 5 && flag) {
            ++j;
            int k = p_162878_.nextInt(i);

            for(NetherBridgePieces.PieceWeight netherbridgepieces$pieceweight : p_162876_) {
               k -= netherbridgepieces$pieceweight.weight;
               if (k < 0) {
                  if (!netherbridgepieces$pieceweight.doPlace(p_162883_) || netherbridgepieces$pieceweight == p_162875_.previousPiece && !netherbridgepieces$pieceweight.allowInRow) {
                     break;
                  }

                  NetherBridgePieces.NetherBridgePiece netherbridgepieces$netherbridgepiece = NetherBridgePieces.findAndCreateBridgePieceFactory(netherbridgepieces$pieceweight, p_162877_, p_162878_, p_162879_, p_162880_, p_162881_, p_162882_, p_162883_);
                  if (netherbridgepieces$netherbridgepiece != null) {
                     ++netherbridgepieces$pieceweight.placeCount;
                     p_162875_.previousPiece = netherbridgepieces$pieceweight;
                     if (!netherbridgepieces$pieceweight.isValid()) {
                        p_162876_.remove(netherbridgepieces$pieceweight);
                     }

                     return netherbridgepieces$netherbridgepiece;
                  }
               }
            }
         }

         return NetherBridgePieces.BridgeEndFiller.createPiece(p_162877_, p_162878_, p_162879_, p_162880_, p_162881_, p_162882_, p_162883_);
      }

      private StructurePiece generateAndAddPiece(NetherBridgePieces.StartPiece p_162858_, StructurePieceAccessor p_162859_, Random p_162860_, int p_162861_, int p_162862_, int p_162863_, @Nullable Direction p_162864_, int p_162865_, boolean p_162866_) {
         if (Math.abs(p_162861_ - p_162858_.getBoundingBox().minX()) <= 112 && Math.abs(p_162863_ - p_162858_.getBoundingBox().minZ()) <= 112) {
            List<NetherBridgePieces.PieceWeight> list = p_162858_.availableBridgePieces;
            if (p_162866_) {
               list = p_162858_.availableCastlePieces;
            }

            StructurePiece structurepiece = this.generatePiece(p_162858_, list, p_162859_, p_162860_, p_162861_, p_162862_, p_162863_, p_162864_, p_162865_ + 1);
            if (structurepiece != null) {
               p_162859_.addPiece(structurepiece);
               p_162858_.pendingChildren.add(structurepiece);
            }

            return structurepiece;
         } else {
            return NetherBridgePieces.BridgeEndFiller.createPiece(p_162859_, p_162860_, p_162861_, p_162862_, p_162863_, p_162864_, p_162865_);
         }
      }

      @Nullable
      protected StructurePiece generateChildForward(NetherBridgePieces.StartPiece p_162868_, StructurePieceAccessor p_162869_, Random p_162870_, int p_162871_, int p_162872_, boolean p_162873_) {
         Direction direction = this.getOrientation();
         if (direction != null) {
            switch(direction) {
            case NORTH:
               return this.generateAndAddPiece(p_162868_, p_162869_, p_162870_, this.boundingBox.minX() + p_162871_, this.boundingBox.minY() + p_162872_, this.boundingBox.minZ() - 1, direction, this.getGenDepth(), p_162873_);
            case SOUTH:
               return this.generateAndAddPiece(p_162868_, p_162869_, p_162870_, this.boundingBox.minX() + p_162871_, this.boundingBox.minY() + p_162872_, this.boundingBox.maxZ() + 1, direction, this.getGenDepth(), p_162873_);
            case WEST:
               return this.generateAndAddPiece(p_162868_, p_162869_, p_162870_, this.boundingBox.minX() - 1, this.boundingBox.minY() + p_162872_, this.boundingBox.minZ() + p_162871_, direction, this.getGenDepth(), p_162873_);
            case EAST:
               return this.generateAndAddPiece(p_162868_, p_162869_, p_162870_, this.boundingBox.maxX() + 1, this.boundingBox.minY() + p_162872_, this.boundingBox.minZ() + p_162871_, direction, this.getGenDepth(), p_162873_);
            }
         }

         return null;
      }

      @Nullable
      protected StructurePiece generateChildLeft(NetherBridgePieces.StartPiece p_162885_, StructurePieceAccessor p_162886_, Random p_162887_, int p_162888_, int p_162889_, boolean p_162890_) {
         Direction direction = this.getOrientation();
         if (direction != null) {
            switch(direction) {
            case NORTH:
               return this.generateAndAddPiece(p_162885_, p_162886_, p_162887_, this.boundingBox.minX() - 1, this.boundingBox.minY() + p_162888_, this.boundingBox.minZ() + p_162889_, Direction.WEST, this.getGenDepth(), p_162890_);
            case SOUTH:
               return this.generateAndAddPiece(p_162885_, p_162886_, p_162887_, this.boundingBox.minX() - 1, this.boundingBox.minY() + p_162888_, this.boundingBox.minZ() + p_162889_, Direction.WEST, this.getGenDepth(), p_162890_);
            case WEST:
               return this.generateAndAddPiece(p_162885_, p_162886_, p_162887_, this.boundingBox.minX() + p_162889_, this.boundingBox.minY() + p_162888_, this.boundingBox.minZ() - 1, Direction.NORTH, this.getGenDepth(), p_162890_);
            case EAST:
               return this.generateAndAddPiece(p_162885_, p_162886_, p_162887_, this.boundingBox.minX() + p_162889_, this.boundingBox.minY() + p_162888_, this.boundingBox.minZ() - 1, Direction.NORTH, this.getGenDepth(), p_162890_);
            }
         }

         return null;
      }

      @Nullable
      protected StructurePiece generateChildRight(NetherBridgePieces.StartPiece p_162892_, StructurePieceAccessor p_162893_, Random p_162894_, int p_162895_, int p_162896_, boolean p_162897_) {
         Direction direction = this.getOrientation();
         if (direction != null) {
            switch(direction) {
            case NORTH:
               return this.generateAndAddPiece(p_162892_, p_162893_, p_162894_, this.boundingBox.maxX() + 1, this.boundingBox.minY() + p_162895_, this.boundingBox.minZ() + p_162896_, Direction.EAST, this.getGenDepth(), p_162897_);
            case SOUTH:
               return this.generateAndAddPiece(p_162892_, p_162893_, p_162894_, this.boundingBox.maxX() + 1, this.boundingBox.minY() + p_162895_, this.boundingBox.minZ() + p_162896_, Direction.EAST, this.getGenDepth(), p_162897_);
            case WEST:
               return this.generateAndAddPiece(p_162892_, p_162893_, p_162894_, this.boundingBox.minX() + p_162896_, this.boundingBox.minY() + p_162895_, this.boundingBox.maxZ() + 1, Direction.SOUTH, this.getGenDepth(), p_162897_);
            case EAST:
               return this.generateAndAddPiece(p_162892_, p_162893_, p_162894_, this.boundingBox.minX() + p_162896_, this.boundingBox.minY() + p_162895_, this.boundingBox.maxZ() + 1, Direction.SOUTH, this.getGenDepth(), p_162897_);
            }
         }

         return null;
      }

      protected static boolean isOkBox(BoundingBox p_71904_) {
         return p_71904_ != null && p_71904_.minY() > 10;
      }
   }

   static class PieceWeight {
      public final Class<? extends NetherBridgePieces.NetherBridgePiece> pieceClass;
      public final int weight;
      public int placeCount;
      public final int maxPlaceCount;
      public final boolean allowInRow;

      public PieceWeight(Class<? extends NetherBridgePieces.NetherBridgePiece> p_71960_, int p_71961_, int p_71962_, boolean p_71963_) {
         this.pieceClass = p_71960_;
         this.weight = p_71961_;
         this.maxPlaceCount = p_71962_;
         this.allowInRow = p_71963_;
      }

      public PieceWeight(Class<? extends NetherBridgePieces.NetherBridgePiece> p_71956_, int p_71957_, int p_71958_) {
         this(p_71956_, p_71957_, p_71958_, false);
      }

      public boolean doPlace(int p_71966_) {
         return this.maxPlaceCount == 0 || this.placeCount < this.maxPlaceCount;
      }

      public boolean isValid() {
         return this.maxPlaceCount == 0 || this.placeCount < this.maxPlaceCount;
      }
   }

   public static class RoomCrossing extends NetherBridgePieces.NetherBridgePiece {
      private static final int WIDTH = 7;
      private static final int HEIGHT = 9;
      private static final int DEPTH = 7;

      public RoomCrossing(int p_71968_, BoundingBox p_71969_, Direction p_71970_) {
         super(StructurePieceType.NETHER_FORTRESS_ROOM_CROSSING, p_71968_, p_71969_);
         this.setOrientation(p_71970_);
      }

      public RoomCrossing(CompoundTag p_192216_) {
         super(StructurePieceType.NETHER_FORTRESS_ROOM_CROSSING, p_192216_);
      }

      public void addChildren(StructurePiece p_162905_, StructurePieceAccessor p_162906_, Random p_162907_) {
         this.generateChildForward((NetherBridgePieces.StartPiece)p_162905_, p_162906_, p_162907_, 2, 0, false);
         this.generateChildLeft((NetherBridgePieces.StartPiece)p_162905_, p_162906_, p_162907_, 0, 2, false);
         this.generateChildRight((NetherBridgePieces.StartPiece)p_162905_, p_162906_, p_162907_, 0, 2, false);
      }

      public static NetherBridgePieces.RoomCrossing createPiece(StructurePieceAccessor p_162909_, int p_162910_, int p_162911_, int p_162912_, Direction p_162913_, int p_162914_) {
         BoundingBox boundingbox = BoundingBox.orientBox(p_162910_, p_162911_, p_162912_, -2, 0, 0, 7, 9, 7, p_162913_);
         return isOkBox(boundingbox) && p_162909_.findCollisionPiece(boundingbox) == null ? new NetherBridgePieces.RoomCrossing(p_162914_, boundingbox, p_162913_) : null;
      }

      public void postProcess(WorldGenLevel p_192218_, StructureFeatureManager p_192219_, ChunkGenerator p_192220_, Random p_192221_, BoundingBox p_192222_, ChunkPos p_192223_, BlockPos p_192224_) {
         this.generateBox(p_192218_, p_192222_, 0, 0, 0, 6, 1, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192218_, p_192222_, 0, 2, 0, 6, 7, 6, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(p_192218_, p_192222_, 0, 2, 0, 1, 6, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192218_, p_192222_, 0, 2, 6, 1, 6, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192218_, p_192222_, 5, 2, 0, 6, 6, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192218_, p_192222_, 5, 2, 6, 6, 6, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192218_, p_192222_, 0, 2, 0, 0, 6, 1, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192218_, p_192222_, 0, 2, 5, 0, 6, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192218_, p_192222_, 6, 2, 0, 6, 6, 1, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192218_, p_192222_, 6, 2, 5, 6, 6, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         BlockState blockstate = Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, Boolean.valueOf(true)).setValue(FenceBlock.EAST, Boolean.valueOf(true));
         BlockState blockstate1 = Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, Boolean.valueOf(true)).setValue(FenceBlock.SOUTH, Boolean.valueOf(true));
         this.generateBox(p_192218_, p_192222_, 2, 6, 0, 4, 6, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192218_, p_192222_, 2, 5, 0, 4, 5, 0, blockstate, blockstate, false);
         this.generateBox(p_192218_, p_192222_, 2, 6, 6, 4, 6, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192218_, p_192222_, 2, 5, 6, 4, 5, 6, blockstate, blockstate, false);
         this.generateBox(p_192218_, p_192222_, 0, 6, 2, 0, 6, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192218_, p_192222_, 0, 5, 2, 0, 5, 4, blockstate1, blockstate1, false);
         this.generateBox(p_192218_, p_192222_, 6, 6, 2, 6, 6, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192218_, p_192222_, 6, 5, 2, 6, 5, 4, blockstate1, blockstate1, false);

         for(int i = 0; i <= 6; ++i) {
            for(int j = 0; j <= 6; ++j) {
               this.fillColumnDown(p_192218_, Blocks.NETHER_BRICKS.defaultBlockState(), i, -1, j, p_192222_);
            }
         }

      }
   }

   public static class StairsRoom extends NetherBridgePieces.NetherBridgePiece {
      private static final int WIDTH = 7;
      private static final int HEIGHT = 11;
      private static final int DEPTH = 7;

      public StairsRoom(int p_71994_, BoundingBox p_71995_, Direction p_71996_) {
         super(StructurePieceType.NETHER_FORTRESS_STAIRS_ROOM, p_71994_, p_71995_);
         this.setOrientation(p_71996_);
      }

      public StairsRoom(CompoundTag p_192226_) {
         super(StructurePieceType.NETHER_FORTRESS_STAIRS_ROOM, p_192226_);
      }

      public void addChildren(StructurePiece p_162922_, StructurePieceAccessor p_162923_, Random p_162924_) {
         this.generateChildRight((NetherBridgePieces.StartPiece)p_162922_, p_162923_, p_162924_, 6, 2, false);
      }

      public static NetherBridgePieces.StairsRoom createPiece(StructurePieceAccessor p_162926_, int p_162927_, int p_162928_, int p_162929_, int p_162930_, Direction p_162931_) {
         BoundingBox boundingbox = BoundingBox.orientBox(p_162927_, p_162928_, p_162929_, -2, 0, 0, 7, 11, 7, p_162931_);
         return isOkBox(boundingbox) && p_162926_.findCollisionPiece(boundingbox) == null ? new NetherBridgePieces.StairsRoom(p_162930_, boundingbox, p_162931_) : null;
      }

      public void postProcess(WorldGenLevel p_192228_, StructureFeatureManager p_192229_, ChunkGenerator p_192230_, Random p_192231_, BoundingBox p_192232_, ChunkPos p_192233_, BlockPos p_192234_) {
         this.generateBox(p_192228_, p_192232_, 0, 0, 0, 6, 1, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192228_, p_192232_, 0, 2, 0, 6, 10, 6, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(p_192228_, p_192232_, 0, 2, 0, 1, 8, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192228_, p_192232_, 5, 2, 0, 6, 8, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192228_, p_192232_, 0, 2, 1, 0, 8, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192228_, p_192232_, 6, 2, 1, 6, 8, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192228_, p_192232_, 1, 2, 6, 5, 8, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         BlockState blockstate = Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, Boolean.valueOf(true)).setValue(FenceBlock.EAST, Boolean.valueOf(true));
         BlockState blockstate1 = Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, Boolean.valueOf(true)).setValue(FenceBlock.SOUTH, Boolean.valueOf(true));
         this.generateBox(p_192228_, p_192232_, 0, 3, 2, 0, 5, 4, blockstate1, blockstate1, false);
         this.generateBox(p_192228_, p_192232_, 6, 3, 2, 6, 5, 2, blockstate1, blockstate1, false);
         this.generateBox(p_192228_, p_192232_, 6, 3, 4, 6, 5, 4, blockstate1, blockstate1, false);
         this.placeBlock(p_192228_, Blocks.NETHER_BRICKS.defaultBlockState(), 5, 2, 5, p_192232_);
         this.generateBox(p_192228_, p_192232_, 4, 2, 5, 4, 3, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192228_, p_192232_, 3, 2, 5, 3, 4, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192228_, p_192232_, 2, 2, 5, 2, 5, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192228_, p_192232_, 1, 2, 5, 1, 6, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192228_, p_192232_, 1, 7, 1, 5, 7, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192228_, p_192232_, 6, 8, 2, 6, 8, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(p_192228_, p_192232_, 2, 6, 0, 4, 8, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_192228_, p_192232_, 2, 5, 0, 4, 5, 0, blockstate, blockstate, false);

         for(int i = 0; i <= 6; ++i) {
            for(int j = 0; j <= 6; ++j) {
               this.fillColumnDown(p_192228_, Blocks.NETHER_BRICKS.defaultBlockState(), i, -1, j, p_192232_);
            }
         }

      }
   }

   public static class StartPiece extends NetherBridgePieces.BridgeCrossing {
      public NetherBridgePieces.PieceWeight previousPiece;
      public List<NetherBridgePieces.PieceWeight> availableBridgePieces;
      public List<NetherBridgePieces.PieceWeight> availableCastlePieces;
      public final List<StructurePiece> pendingChildren = Lists.newArrayList();

      public StartPiece(Random p_72027_, int p_72028_, int p_72029_) {
         super(p_72028_, p_72029_, getRandomHorizontalDirection(p_72027_));
         this.availableBridgePieces = Lists.newArrayList();

         for(NetherBridgePieces.PieceWeight netherbridgepieces$pieceweight : NetherBridgePieces.BRIDGE_PIECE_WEIGHTS) {
            netherbridgepieces$pieceweight.placeCount = 0;
            this.availableBridgePieces.add(netherbridgepieces$pieceweight);
         }

         this.availableCastlePieces = Lists.newArrayList();

         for(NetherBridgePieces.PieceWeight netherbridgepieces$pieceweight1 : NetherBridgePieces.CASTLE_PIECE_WEIGHTS) {
            netherbridgepieces$pieceweight1.placeCount = 0;
            this.availableCastlePieces.add(netherbridgepieces$pieceweight1);
         }

      }

      public StartPiece(CompoundTag p_192236_) {
         super(StructurePieceType.NETHER_FORTRESS_START, p_192236_);
      }
   }
}