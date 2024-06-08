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
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.EndPortalFrameBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.NoiseEffect;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class StrongholdPieces {
   private static final int SMALL_DOOR_WIDTH = 3;
   private static final int SMALL_DOOR_HEIGHT = 3;
   private static final int MAX_DEPTH = 50;
   private static final int LOWEST_Y_POSITION = 10;
   private static final boolean CHECK_AIR = true;
   public static final int MAGIC_START_Y = 64;
   private static final StrongholdPieces.PieceWeight[] STRONGHOLD_PIECE_WEIGHTS = new StrongholdPieces.PieceWeight[]{new StrongholdPieces.PieceWeight(StrongholdPieces.Straight.class, 40, 0), new StrongholdPieces.PieceWeight(StrongholdPieces.PrisonHall.class, 5, 5), new StrongholdPieces.PieceWeight(StrongholdPieces.LeftTurn.class, 20, 0), new StrongholdPieces.PieceWeight(StrongholdPieces.RightTurn.class, 20, 0), new StrongholdPieces.PieceWeight(StrongholdPieces.RoomCrossing.class, 10, 6), new StrongholdPieces.PieceWeight(StrongholdPieces.StraightStairsDown.class, 5, 5), new StrongholdPieces.PieceWeight(StrongholdPieces.StairsDown.class, 5, 5), new StrongholdPieces.PieceWeight(StrongholdPieces.FiveCrossing.class, 5, 4), new StrongholdPieces.PieceWeight(StrongholdPieces.ChestCorridor.class, 5, 4), new StrongholdPieces.PieceWeight(StrongholdPieces.Library.class, 10, 2) {
      public boolean doPlace(int p_72903_) {
         return super.doPlace(p_72903_) && p_72903_ > 4;
      }
   }, new StrongholdPieces.PieceWeight(StrongholdPieces.PortalRoom.class, 20, 1) {
      public boolean doPlace(int p_72909_) {
         return super.doPlace(p_72909_) && p_72909_ > 5;
      }
   }};
   private static List<StrongholdPieces.PieceWeight> currentPieces;
   static Class<? extends StrongholdPieces.StrongholdPiece> imposedPiece;
   private static int totalWeight;
   static final StrongholdPieces.SmoothStoneSelector SMOOTH_STONE_SELECTOR = new StrongholdPieces.SmoothStoneSelector();

   public static void resetPieces() {
      currentPieces = Lists.newArrayList();

      for(StrongholdPieces.PieceWeight strongholdpieces$pieceweight : STRONGHOLD_PIECE_WEIGHTS) {
         strongholdpieces$pieceweight.placeCount = 0;
         currentPieces.add(strongholdpieces$pieceweight);
      }

      imposedPiece = null;
   }

   private static boolean updatePieceWeight() {
      boolean flag = false;
      totalWeight = 0;

      for(StrongholdPieces.PieceWeight strongholdpieces$pieceweight : currentPieces) {
         if (strongholdpieces$pieceweight.maxPlaceCount > 0 && strongholdpieces$pieceweight.placeCount < strongholdpieces$pieceweight.maxPlaceCount) {
            flag = true;
         }

         totalWeight += strongholdpieces$pieceweight.weight;
      }

      return flag;
   }

   private static StrongholdPieces.StrongholdPiece findAndCreatePieceFactory(Class<? extends StrongholdPieces.StrongholdPiece> p_163234_, StructurePieceAccessor p_163235_, Random p_163236_, int p_163237_, int p_163238_, int p_163239_, @Nullable Direction p_163240_, int p_163241_) {
      StrongholdPieces.StrongholdPiece strongholdpieces$strongholdpiece = null;
      if (p_163234_ == StrongholdPieces.Straight.class) {
         strongholdpieces$strongholdpiece = StrongholdPieces.Straight.createPiece(p_163235_, p_163236_, p_163237_, p_163238_, p_163239_, p_163240_, p_163241_);
      } else if (p_163234_ == StrongholdPieces.PrisonHall.class) {
         strongholdpieces$strongholdpiece = StrongholdPieces.PrisonHall.createPiece(p_163235_, p_163236_, p_163237_, p_163238_, p_163239_, p_163240_, p_163241_);
      } else if (p_163234_ == StrongholdPieces.LeftTurn.class) {
         strongholdpieces$strongholdpiece = StrongholdPieces.LeftTurn.createPiece(p_163235_, p_163236_, p_163237_, p_163238_, p_163239_, p_163240_, p_163241_);
      } else if (p_163234_ == StrongholdPieces.RightTurn.class) {
         strongholdpieces$strongholdpiece = StrongholdPieces.RightTurn.createPiece(p_163235_, p_163236_, p_163237_, p_163238_, p_163239_, p_163240_, p_163241_);
      } else if (p_163234_ == StrongholdPieces.RoomCrossing.class) {
         strongholdpieces$strongholdpiece = StrongholdPieces.RoomCrossing.createPiece(p_163235_, p_163236_, p_163237_, p_163238_, p_163239_, p_163240_, p_163241_);
      } else if (p_163234_ == StrongholdPieces.StraightStairsDown.class) {
         strongholdpieces$strongholdpiece = StrongholdPieces.StraightStairsDown.createPiece(p_163235_, p_163236_, p_163237_, p_163238_, p_163239_, p_163240_, p_163241_);
      } else if (p_163234_ == StrongholdPieces.StairsDown.class) {
         strongholdpieces$strongholdpiece = StrongholdPieces.StairsDown.createPiece(p_163235_, p_163236_, p_163237_, p_163238_, p_163239_, p_163240_, p_163241_);
      } else if (p_163234_ == StrongholdPieces.FiveCrossing.class) {
         strongholdpieces$strongholdpiece = StrongholdPieces.FiveCrossing.createPiece(p_163235_, p_163236_, p_163237_, p_163238_, p_163239_, p_163240_, p_163241_);
      } else if (p_163234_ == StrongholdPieces.ChestCorridor.class) {
         strongholdpieces$strongholdpiece = StrongholdPieces.ChestCorridor.createPiece(p_163235_, p_163236_, p_163237_, p_163238_, p_163239_, p_163240_, p_163241_);
      } else if (p_163234_ == StrongholdPieces.Library.class) {
         strongholdpieces$strongholdpiece = StrongholdPieces.Library.createPiece(p_163235_, p_163236_, p_163237_, p_163238_, p_163239_, p_163240_, p_163241_);
      } else if (p_163234_ == StrongholdPieces.PortalRoom.class) {
         strongholdpieces$strongholdpiece = StrongholdPieces.PortalRoom.createPiece(p_163235_, p_163237_, p_163238_, p_163239_, p_163240_, p_163241_);
      }

      return strongholdpieces$strongholdpiece;
   }

   private static StrongholdPieces.StrongholdPiece generatePieceFromSmallDoor(StrongholdPieces.StartPiece p_163225_, StructurePieceAccessor p_163226_, Random p_163227_, int p_163228_, int p_163229_, int p_163230_, Direction p_163231_, int p_163232_) {
      if (!updatePieceWeight()) {
         return null;
      } else {
         if (imposedPiece != null) {
            StrongholdPieces.StrongholdPiece strongholdpieces$strongholdpiece = findAndCreatePieceFactory(imposedPiece, p_163226_, p_163227_, p_163228_, p_163229_, p_163230_, p_163231_, p_163232_);
            imposedPiece = null;
            if (strongholdpieces$strongholdpiece != null) {
               return strongholdpieces$strongholdpiece;
            }
         }

         int j = 0;

         while(j < 5) {
            ++j;
            int i = p_163227_.nextInt(totalWeight);

            for(StrongholdPieces.PieceWeight strongholdpieces$pieceweight : currentPieces) {
               i -= strongholdpieces$pieceweight.weight;
               if (i < 0) {
                  if (!strongholdpieces$pieceweight.doPlace(p_163232_) || strongholdpieces$pieceweight == p_163225_.previousPiece) {
                     break;
                  }

                  StrongholdPieces.StrongholdPiece strongholdpieces$strongholdpiece1 = findAndCreatePieceFactory(strongholdpieces$pieceweight.pieceClass, p_163226_, p_163227_, p_163228_, p_163229_, p_163230_, p_163231_, p_163232_);
                  if (strongholdpieces$strongholdpiece1 != null) {
                     ++strongholdpieces$pieceweight.placeCount;
                     p_163225_.previousPiece = strongholdpieces$pieceweight;
                     if (!strongholdpieces$pieceweight.isValid()) {
                        currentPieces.remove(strongholdpieces$pieceweight);
                     }

                     return strongholdpieces$strongholdpiece1;
                  }
               }
            }
         }

         BoundingBox boundingbox = StrongholdPieces.FillerCorridor.findPieceBox(p_163226_, p_163227_, p_163228_, p_163229_, p_163230_, p_163231_);
         return boundingbox != null && boundingbox.minY() > 1 ? new StrongholdPieces.FillerCorridor(p_163232_, boundingbox, p_163231_) : null;
      }
   }

   static StructurePiece generateAndAddPiece(StrongholdPieces.StartPiece p_163243_, StructurePieceAccessor p_163244_, Random p_163245_, int p_163246_, int p_163247_, int p_163248_, @Nullable Direction p_163249_, int p_163250_) {
      if (p_163250_ > 50) {
         return null;
      } else if (Math.abs(p_163246_ - p_163243_.getBoundingBox().minX()) <= 112 && Math.abs(p_163248_ - p_163243_.getBoundingBox().minZ()) <= 112) {
         StructurePiece structurepiece = generatePieceFromSmallDoor(p_163243_, p_163244_, p_163245_, p_163246_, p_163247_, p_163248_, p_163249_, p_163250_ + 1);
         if (structurepiece != null) {
            p_163244_.addPiece(structurepiece);
            p_163243_.pendingChildren.add(structurepiece);
         }

         return structurepiece;
      } else {
         return null;
      }
   }

   public static class ChestCorridor extends StrongholdPieces.StrongholdPiece {
      private static final int WIDTH = 5;
      private static final int HEIGHT = 5;
      private static final int DEPTH = 7;
      private boolean hasPlacedChest;

      public ChestCorridor(int p_72915_, Random p_72916_, BoundingBox p_72917_, Direction p_72918_) {
         super(StructurePieceType.STRONGHOLD_CHEST_CORRIDOR, p_72915_, p_72917_);
         this.setOrientation(p_72918_);
         this.entryDoor = this.randomSmallDoor(p_72916_);
      }

      public ChestCorridor(CompoundTag p_192489_) {
         super(StructurePieceType.STRONGHOLD_CHEST_CORRIDOR, p_192489_);
         this.hasPlacedChest = p_192489_.getBoolean("Chest");
      }

      protected void addAdditionalSaveData(StructurePieceSerializationContext p_192499_, CompoundTag p_192500_) {
         super.addAdditionalSaveData(p_192499_, p_192500_);
         p_192500_.putBoolean("Chest", this.hasPlacedChest);
      }

      public void addChildren(StructurePiece p_163261_, StructurePieceAccessor p_163262_, Random p_163263_) {
         this.generateSmallDoorChildForward((StrongholdPieces.StartPiece)p_163261_, p_163262_, p_163263_, 1, 1);
      }

      public static StrongholdPieces.ChestCorridor createPiece(StructurePieceAccessor p_163265_, Random p_163266_, int p_163267_, int p_163268_, int p_163269_, Direction p_163270_, int p_163271_) {
         BoundingBox boundingbox = BoundingBox.orientBox(p_163267_, p_163268_, p_163269_, -1, -1, 0, 5, 5, 7, p_163270_);
         return isOkBox(boundingbox) && p_163265_.findCollisionPiece(boundingbox) == null ? new StrongholdPieces.ChestCorridor(p_163271_, p_163266_, boundingbox, p_163270_) : null;
      }

      public void postProcess(WorldGenLevel p_192491_, StructureFeatureManager p_192492_, ChunkGenerator p_192493_, Random p_192494_, BoundingBox p_192495_, ChunkPos p_192496_, BlockPos p_192497_) {
         this.generateBox(p_192491_, p_192495_, 0, 0, 0, 4, 4, 6, true, p_192494_, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateSmallDoor(p_192491_, p_192494_, p_192495_, this.entryDoor, 1, 1, 0);
         this.generateSmallDoor(p_192491_, p_192494_, p_192495_, StrongholdPieces.StrongholdPiece.SmallDoorType.OPENING, 1, 1, 6);
         this.generateBox(p_192491_, p_192495_, 3, 1, 2, 3, 1, 4, Blocks.STONE_BRICKS.defaultBlockState(), Blocks.STONE_BRICKS.defaultBlockState(), false);
         this.placeBlock(p_192491_, Blocks.STONE_BRICK_SLAB.defaultBlockState(), 3, 1, 1, p_192495_);
         this.placeBlock(p_192491_, Blocks.STONE_BRICK_SLAB.defaultBlockState(), 3, 1, 5, p_192495_);
         this.placeBlock(p_192491_, Blocks.STONE_BRICK_SLAB.defaultBlockState(), 3, 2, 2, p_192495_);
         this.placeBlock(p_192491_, Blocks.STONE_BRICK_SLAB.defaultBlockState(), 3, 2, 4, p_192495_);

         for(int i = 2; i <= 4; ++i) {
            this.placeBlock(p_192491_, Blocks.STONE_BRICK_SLAB.defaultBlockState(), 2, 1, i, p_192495_);
         }

         if (!this.hasPlacedChest && p_192495_.isInside(this.getWorldPos(3, 2, 3))) {
            this.hasPlacedChest = true;
            this.createChest(p_192491_, p_192495_, p_192494_, 3, 2, 3, BuiltInLootTables.STRONGHOLD_CORRIDOR);
         }

      }
   }

   public static class FillerCorridor extends StrongholdPieces.StrongholdPiece {
      private final int steps;

      public FillerCorridor(int p_72946_, BoundingBox p_72947_, Direction p_72948_) {
         super(StructurePieceType.STRONGHOLD_FILLER_CORRIDOR, p_72946_, p_72947_);
         this.setOrientation(p_72948_);
         this.steps = p_72948_ != Direction.NORTH && p_72948_ != Direction.SOUTH ? p_72947_.getXSpan() : p_72947_.getZSpan();
      }

      public FillerCorridor(CompoundTag p_192502_) {
         super(StructurePieceType.STRONGHOLD_FILLER_CORRIDOR, p_192502_);
         this.steps = p_192502_.getInt("Steps");
      }

      protected void addAdditionalSaveData(StructurePieceSerializationContext p_192512_, CompoundTag p_192513_) {
         super.addAdditionalSaveData(p_192512_, p_192513_);
         p_192513_.putInt("Steps", this.steps);
      }

      public static BoundingBox findPieceBox(StructurePieceAccessor p_163280_, Random p_163281_, int p_163282_, int p_163283_, int p_163284_, Direction p_163285_) {
         int i = 3;
         BoundingBox boundingbox = BoundingBox.orientBox(p_163282_, p_163283_, p_163284_, -1, -1, 0, 5, 5, 4, p_163285_);
         StructurePiece structurepiece = p_163280_.findCollisionPiece(boundingbox);
         if (structurepiece == null) {
            return null;
         } else {
            if (structurepiece.getBoundingBox().minY() == boundingbox.minY()) {
               for(int j = 2; j >= 1; --j) {
                  boundingbox = BoundingBox.orientBox(p_163282_, p_163283_, p_163284_, -1, -1, 0, 5, 5, j, p_163285_);
                  if (!structurepiece.getBoundingBox().intersects(boundingbox)) {
                     return BoundingBox.orientBox(p_163282_, p_163283_, p_163284_, -1, -1, 0, 5, 5, j + 1, p_163285_);
                  }
               }
            }

            return null;
         }
      }

      public void postProcess(WorldGenLevel p_192504_, StructureFeatureManager p_192505_, ChunkGenerator p_192506_, Random p_192507_, BoundingBox p_192508_, ChunkPos p_192509_, BlockPos p_192510_) {
         for(int i = 0; i < this.steps; ++i) {
            this.placeBlock(p_192504_, Blocks.STONE_BRICKS.defaultBlockState(), 0, 0, i, p_192508_);
            this.placeBlock(p_192504_, Blocks.STONE_BRICKS.defaultBlockState(), 1, 0, i, p_192508_);
            this.placeBlock(p_192504_, Blocks.STONE_BRICKS.defaultBlockState(), 2, 0, i, p_192508_);
            this.placeBlock(p_192504_, Blocks.STONE_BRICKS.defaultBlockState(), 3, 0, i, p_192508_);
            this.placeBlock(p_192504_, Blocks.STONE_BRICKS.defaultBlockState(), 4, 0, i, p_192508_);

            for(int j = 1; j <= 3; ++j) {
               this.placeBlock(p_192504_, Blocks.STONE_BRICKS.defaultBlockState(), 0, j, i, p_192508_);
               this.placeBlock(p_192504_, Blocks.CAVE_AIR.defaultBlockState(), 1, j, i, p_192508_);
               this.placeBlock(p_192504_, Blocks.CAVE_AIR.defaultBlockState(), 2, j, i, p_192508_);
               this.placeBlock(p_192504_, Blocks.CAVE_AIR.defaultBlockState(), 3, j, i, p_192508_);
               this.placeBlock(p_192504_, Blocks.STONE_BRICKS.defaultBlockState(), 4, j, i, p_192508_);
            }

            this.placeBlock(p_192504_, Blocks.STONE_BRICKS.defaultBlockState(), 0, 4, i, p_192508_);
            this.placeBlock(p_192504_, Blocks.STONE_BRICKS.defaultBlockState(), 1, 4, i, p_192508_);
            this.placeBlock(p_192504_, Blocks.STONE_BRICKS.defaultBlockState(), 2, 4, i, p_192508_);
            this.placeBlock(p_192504_, Blocks.STONE_BRICKS.defaultBlockState(), 3, 4, i, p_192508_);
            this.placeBlock(p_192504_, Blocks.STONE_BRICKS.defaultBlockState(), 4, 4, i, p_192508_);
         }

      }
   }

   public static class FiveCrossing extends StrongholdPieces.StrongholdPiece {
      protected static final int WIDTH = 10;
      protected static final int HEIGHT = 9;
      protected static final int DEPTH = 11;
      private final boolean leftLow;
      private final boolean leftHigh;
      private final boolean rightLow;
      private final boolean rightHigh;

      public FiveCrossing(int p_72974_, Random p_72975_, BoundingBox p_72976_, Direction p_72977_) {
         super(StructurePieceType.STRONGHOLD_FIVE_CROSSING, p_72974_, p_72976_);
         this.setOrientation(p_72977_);
         this.entryDoor = this.randomSmallDoor(p_72975_);
         this.leftLow = p_72975_.nextBoolean();
         this.leftHigh = p_72975_.nextBoolean();
         this.rightLow = p_72975_.nextBoolean();
         this.rightHigh = p_72975_.nextInt(3) > 0;
      }

      public FiveCrossing(CompoundTag p_192515_) {
         super(StructurePieceType.STRONGHOLD_FIVE_CROSSING, p_192515_);
         this.leftLow = p_192515_.getBoolean("leftLow");
         this.leftHigh = p_192515_.getBoolean("leftHigh");
         this.rightLow = p_192515_.getBoolean("rightLow");
         this.rightHigh = p_192515_.getBoolean("rightHigh");
      }

      protected void addAdditionalSaveData(StructurePieceSerializationContext p_192525_, CompoundTag p_192526_) {
         super.addAdditionalSaveData(p_192525_, p_192526_);
         p_192526_.putBoolean("leftLow", this.leftLow);
         p_192526_.putBoolean("leftHigh", this.leftHigh);
         p_192526_.putBoolean("rightLow", this.rightLow);
         p_192526_.putBoolean("rightHigh", this.rightHigh);
      }

      public void addChildren(StructurePiece p_163297_, StructurePieceAccessor p_163298_, Random p_163299_) {
         int i = 3;
         int j = 5;
         Direction direction = this.getOrientation();
         if (direction == Direction.WEST || direction == Direction.NORTH) {
            i = 8 - i;
            j = 8 - j;
         }

         this.generateSmallDoorChildForward((StrongholdPieces.StartPiece)p_163297_, p_163298_, p_163299_, 5, 1);
         if (this.leftLow) {
            this.generateSmallDoorChildLeft((StrongholdPieces.StartPiece)p_163297_, p_163298_, p_163299_, i, 1);
         }

         if (this.leftHigh) {
            this.generateSmallDoorChildLeft((StrongholdPieces.StartPiece)p_163297_, p_163298_, p_163299_, j, 7);
         }

         if (this.rightLow) {
            this.generateSmallDoorChildRight((StrongholdPieces.StartPiece)p_163297_, p_163298_, p_163299_, i, 1);
         }

         if (this.rightHigh) {
            this.generateSmallDoorChildRight((StrongholdPieces.StartPiece)p_163297_, p_163298_, p_163299_, j, 7);
         }

      }

      public static StrongholdPieces.FiveCrossing createPiece(StructurePieceAccessor p_163301_, Random p_163302_, int p_163303_, int p_163304_, int p_163305_, Direction p_163306_, int p_163307_) {
         BoundingBox boundingbox = BoundingBox.orientBox(p_163303_, p_163304_, p_163305_, -4, -3, 0, 10, 9, 11, p_163306_);
         return isOkBox(boundingbox) && p_163301_.findCollisionPiece(boundingbox) == null ? new StrongholdPieces.FiveCrossing(p_163307_, p_163302_, boundingbox, p_163306_) : null;
      }

      public void postProcess(WorldGenLevel p_192517_, StructureFeatureManager p_192518_, ChunkGenerator p_192519_, Random p_192520_, BoundingBox p_192521_, ChunkPos p_192522_, BlockPos p_192523_) {
         this.generateBox(p_192517_, p_192521_, 0, 0, 0, 9, 8, 10, true, p_192520_, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateSmallDoor(p_192517_, p_192520_, p_192521_, this.entryDoor, 4, 3, 0);
         if (this.leftLow) {
            this.generateBox(p_192517_, p_192521_, 0, 3, 1, 0, 5, 3, CAVE_AIR, CAVE_AIR, false);
         }

         if (this.rightLow) {
            this.generateBox(p_192517_, p_192521_, 9, 3, 1, 9, 5, 3, CAVE_AIR, CAVE_AIR, false);
         }

         if (this.leftHigh) {
            this.generateBox(p_192517_, p_192521_, 0, 5, 7, 0, 7, 9, CAVE_AIR, CAVE_AIR, false);
         }

         if (this.rightHigh) {
            this.generateBox(p_192517_, p_192521_, 9, 5, 7, 9, 7, 9, CAVE_AIR, CAVE_AIR, false);
         }

         this.generateBox(p_192517_, p_192521_, 5, 1, 10, 7, 3, 10, CAVE_AIR, CAVE_AIR, false);
         this.generateBox(p_192517_, p_192521_, 1, 2, 1, 8, 2, 6, false, p_192520_, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(p_192517_, p_192521_, 4, 1, 5, 4, 4, 9, false, p_192520_, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(p_192517_, p_192521_, 8, 1, 5, 8, 4, 9, false, p_192520_, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(p_192517_, p_192521_, 1, 4, 7, 3, 4, 9, false, p_192520_, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(p_192517_, p_192521_, 1, 3, 5, 3, 3, 6, false, p_192520_, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(p_192517_, p_192521_, 1, 3, 4, 3, 3, 4, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), false);
         this.generateBox(p_192517_, p_192521_, 1, 4, 6, 3, 4, 6, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), false);
         this.generateBox(p_192517_, p_192521_, 5, 1, 7, 7, 1, 8, false, p_192520_, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(p_192517_, p_192521_, 5, 1, 9, 7, 1, 9, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), false);
         this.generateBox(p_192517_, p_192521_, 5, 2, 7, 7, 2, 7, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), false);
         this.generateBox(p_192517_, p_192521_, 4, 5, 7, 4, 5, 9, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), false);
         this.generateBox(p_192517_, p_192521_, 8, 5, 7, 8, 5, 9, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), false);
         this.generateBox(p_192517_, p_192521_, 5, 5, 7, 7, 5, 9, Blocks.SMOOTH_STONE_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.DOUBLE), Blocks.SMOOTH_STONE_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.DOUBLE), false);
         this.placeBlock(p_192517_, Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.SOUTH), 6, 5, 6, p_192521_);
      }
   }

   public static class LeftTurn extends StrongholdPieces.Turn {
      public LeftTurn(int p_73004_, Random p_73005_, BoundingBox p_73006_, Direction p_73007_) {
         super(StructurePieceType.STRONGHOLD_LEFT_TURN, p_73004_, p_73006_);
         this.setOrientation(p_73007_);
         this.entryDoor = this.randomSmallDoor(p_73005_);
      }

      public LeftTurn(CompoundTag p_192528_) {
         super(StructurePieceType.STRONGHOLD_LEFT_TURN, p_192528_);
      }

      public void addChildren(StructurePiece p_163313_, StructurePieceAccessor p_163314_, Random p_163315_) {
         Direction direction = this.getOrientation();
         if (direction != Direction.NORTH && direction != Direction.EAST) {
            this.generateSmallDoorChildRight((StrongholdPieces.StartPiece)p_163313_, p_163314_, p_163315_, 1, 1);
         } else {
            this.generateSmallDoorChildLeft((StrongholdPieces.StartPiece)p_163313_, p_163314_, p_163315_, 1, 1);
         }

      }

      public static StrongholdPieces.LeftTurn createPiece(StructurePieceAccessor p_163317_, Random p_163318_, int p_163319_, int p_163320_, int p_163321_, Direction p_163322_, int p_163323_) {
         BoundingBox boundingbox = BoundingBox.orientBox(p_163319_, p_163320_, p_163321_, -1, -1, 0, 5, 5, 5, p_163322_);
         return isOkBox(boundingbox) && p_163317_.findCollisionPiece(boundingbox) == null ? new StrongholdPieces.LeftTurn(p_163323_, p_163318_, boundingbox, p_163322_) : null;
      }

      public void postProcess(WorldGenLevel p_192530_, StructureFeatureManager p_192531_, ChunkGenerator p_192532_, Random p_192533_, BoundingBox p_192534_, ChunkPos p_192535_, BlockPos p_192536_) {
         this.generateBox(p_192530_, p_192534_, 0, 0, 0, 4, 4, 4, true, p_192533_, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateSmallDoor(p_192530_, p_192533_, p_192534_, this.entryDoor, 1, 1, 0);
         Direction direction = this.getOrientation();
         if (direction != Direction.NORTH && direction != Direction.EAST) {
            this.generateBox(p_192530_, p_192534_, 4, 1, 1, 4, 3, 3, CAVE_AIR, CAVE_AIR, false);
         } else {
            this.generateBox(p_192530_, p_192534_, 0, 1, 1, 0, 3, 3, CAVE_AIR, CAVE_AIR, false);
         }

      }
   }

   public static class Library extends StrongholdPieces.StrongholdPiece {
      protected static final int WIDTH = 14;
      protected static final int HEIGHT = 6;
      protected static final int TALL_HEIGHT = 11;
      protected static final int DEPTH = 15;
      private final boolean isTall;

      public Library(int p_73033_, Random p_73034_, BoundingBox p_73035_, Direction p_73036_) {
         super(StructurePieceType.STRONGHOLD_LIBRARY, p_73033_, p_73035_);
         this.setOrientation(p_73036_);
         this.entryDoor = this.randomSmallDoor(p_73034_);
         this.isTall = p_73035_.getYSpan() > 6;
      }

      public Library(CompoundTag p_192538_) {
         super(StructurePieceType.STRONGHOLD_LIBRARY, p_192538_);
         this.isTall = p_192538_.getBoolean("Tall");
      }

      protected void addAdditionalSaveData(StructurePieceSerializationContext p_192548_, CompoundTag p_192549_) {
         super.addAdditionalSaveData(p_192548_, p_192549_);
         p_192549_.putBoolean("Tall", this.isTall);
      }

      public static StrongholdPieces.Library createPiece(StructurePieceAccessor p_163335_, Random p_163336_, int p_163337_, int p_163338_, int p_163339_, Direction p_163340_, int p_163341_) {
         BoundingBox boundingbox = BoundingBox.orientBox(p_163337_, p_163338_, p_163339_, -4, -1, 0, 14, 11, 15, p_163340_);
         if (!isOkBox(boundingbox) || p_163335_.findCollisionPiece(boundingbox) != null) {
            boundingbox = BoundingBox.orientBox(p_163337_, p_163338_, p_163339_, -4, -1, 0, 14, 6, 15, p_163340_);
            if (!isOkBox(boundingbox) || p_163335_.findCollisionPiece(boundingbox) != null) {
               return null;
            }
         }

         return new StrongholdPieces.Library(p_163341_, p_163336_, boundingbox, p_163340_);
      }

      public void postProcess(WorldGenLevel p_192540_, StructureFeatureManager p_192541_, ChunkGenerator p_192542_, Random p_192543_, BoundingBox p_192544_, ChunkPos p_192545_, BlockPos p_192546_) {
         int i = 11;
         if (!this.isTall) {
            i = 6;
         }

         this.generateBox(p_192540_, p_192544_, 0, 0, 0, 13, i - 1, 14, true, p_192543_, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateSmallDoor(p_192540_, p_192543_, p_192544_, this.entryDoor, 4, 1, 0);
         this.generateMaybeBox(p_192540_, p_192544_, p_192543_, 0.07F, 2, 1, 1, 11, 4, 13, Blocks.COBWEB.defaultBlockState(), Blocks.COBWEB.defaultBlockState(), false, false);
         int j = 1;
         int k = 12;

         for(int l = 1; l <= 13; ++l) {
            if ((l - 1) % 4 == 0) {
               this.generateBox(p_192540_, p_192544_, 1, 1, l, 1, 4, l, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
               this.generateBox(p_192540_, p_192544_, 12, 1, l, 12, 4, l, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
               this.placeBlock(p_192540_, Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.EAST), 2, 3, l, p_192544_);
               this.placeBlock(p_192540_, Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.WEST), 11, 3, l, p_192544_);
               if (this.isTall) {
                  this.generateBox(p_192540_, p_192544_, 1, 6, l, 1, 9, l, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
                  this.generateBox(p_192540_, p_192544_, 12, 6, l, 12, 9, l, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
               }
            } else {
               this.generateBox(p_192540_, p_192544_, 1, 1, l, 1, 4, l, Blocks.BOOKSHELF.defaultBlockState(), Blocks.BOOKSHELF.defaultBlockState(), false);
               this.generateBox(p_192540_, p_192544_, 12, 1, l, 12, 4, l, Blocks.BOOKSHELF.defaultBlockState(), Blocks.BOOKSHELF.defaultBlockState(), false);
               if (this.isTall) {
                  this.generateBox(p_192540_, p_192544_, 1, 6, l, 1, 9, l, Blocks.BOOKSHELF.defaultBlockState(), Blocks.BOOKSHELF.defaultBlockState(), false);
                  this.generateBox(p_192540_, p_192544_, 12, 6, l, 12, 9, l, Blocks.BOOKSHELF.defaultBlockState(), Blocks.BOOKSHELF.defaultBlockState(), false);
               }
            }
         }

         for(int l1 = 3; l1 < 12; l1 += 2) {
            this.generateBox(p_192540_, p_192544_, 3, 1, l1, 4, 3, l1, Blocks.BOOKSHELF.defaultBlockState(), Blocks.BOOKSHELF.defaultBlockState(), false);
            this.generateBox(p_192540_, p_192544_, 6, 1, l1, 7, 3, l1, Blocks.BOOKSHELF.defaultBlockState(), Blocks.BOOKSHELF.defaultBlockState(), false);
            this.generateBox(p_192540_, p_192544_, 9, 1, l1, 10, 3, l1, Blocks.BOOKSHELF.defaultBlockState(), Blocks.BOOKSHELF.defaultBlockState(), false);
         }

         if (this.isTall) {
            this.generateBox(p_192540_, p_192544_, 1, 5, 1, 3, 5, 13, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
            this.generateBox(p_192540_, p_192544_, 10, 5, 1, 12, 5, 13, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
            this.generateBox(p_192540_, p_192544_, 4, 5, 1, 9, 5, 2, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
            this.generateBox(p_192540_, p_192544_, 4, 5, 12, 9, 5, 13, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
            this.placeBlock(p_192540_, Blocks.OAK_PLANKS.defaultBlockState(), 9, 5, 11, p_192544_);
            this.placeBlock(p_192540_, Blocks.OAK_PLANKS.defaultBlockState(), 8, 5, 11, p_192544_);
            this.placeBlock(p_192540_, Blocks.OAK_PLANKS.defaultBlockState(), 9, 5, 10, p_192544_);
            BlockState blockstate5 = Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, Boolean.valueOf(true)).setValue(FenceBlock.EAST, Boolean.valueOf(true));
            BlockState blockstate = Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, Boolean.valueOf(true)).setValue(FenceBlock.SOUTH, Boolean.valueOf(true));
            this.generateBox(p_192540_, p_192544_, 3, 6, 3, 3, 6, 11, blockstate, blockstate, false);
            this.generateBox(p_192540_, p_192544_, 10, 6, 3, 10, 6, 9, blockstate, blockstate, false);
            this.generateBox(p_192540_, p_192544_, 4, 6, 2, 9, 6, 2, blockstate5, blockstate5, false);
            this.generateBox(p_192540_, p_192544_, 4, 6, 12, 7, 6, 12, blockstate5, blockstate5, false);
            this.placeBlock(p_192540_, Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, Boolean.valueOf(true)).setValue(FenceBlock.EAST, Boolean.valueOf(true)), 3, 6, 2, p_192544_);
            this.placeBlock(p_192540_, Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.SOUTH, Boolean.valueOf(true)).setValue(FenceBlock.EAST, Boolean.valueOf(true)), 3, 6, 12, p_192544_);
            this.placeBlock(p_192540_, Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, Boolean.valueOf(true)).setValue(FenceBlock.WEST, Boolean.valueOf(true)), 10, 6, 2, p_192544_);

            for(int i1 = 0; i1 <= 2; ++i1) {
               this.placeBlock(p_192540_, Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.SOUTH, Boolean.valueOf(true)).setValue(FenceBlock.WEST, Boolean.valueOf(true)), 8 + i1, 6, 12 - i1, p_192544_);
               if (i1 != 2) {
                  this.placeBlock(p_192540_, Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, Boolean.valueOf(true)).setValue(FenceBlock.EAST, Boolean.valueOf(true)), 8 + i1, 6, 11 - i1, p_192544_);
               }
            }

            BlockState blockstate6 = Blocks.LADDER.defaultBlockState().setValue(LadderBlock.FACING, Direction.SOUTH);
            this.placeBlock(p_192540_, blockstate6, 10, 1, 13, p_192544_);
            this.placeBlock(p_192540_, blockstate6, 10, 2, 13, p_192544_);
            this.placeBlock(p_192540_, blockstate6, 10, 3, 13, p_192544_);
            this.placeBlock(p_192540_, blockstate6, 10, 4, 13, p_192544_);
            this.placeBlock(p_192540_, blockstate6, 10, 5, 13, p_192544_);
            this.placeBlock(p_192540_, blockstate6, 10, 6, 13, p_192544_);
            this.placeBlock(p_192540_, blockstate6, 10, 7, 13, p_192544_);
            int j1 = 7;
            int k1 = 7;
            BlockState blockstate1 = Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.EAST, Boolean.valueOf(true));
            this.placeBlock(p_192540_, blockstate1, 6, 9, 7, p_192544_);
            BlockState blockstate2 = Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, Boolean.valueOf(true));
            this.placeBlock(p_192540_, blockstate2, 7, 9, 7, p_192544_);
            this.placeBlock(p_192540_, blockstate1, 6, 8, 7, p_192544_);
            this.placeBlock(p_192540_, blockstate2, 7, 8, 7, p_192544_);
            BlockState blockstate3 = blockstate.setValue(FenceBlock.WEST, Boolean.valueOf(true)).setValue(FenceBlock.EAST, Boolean.valueOf(true));
            this.placeBlock(p_192540_, blockstate3, 6, 7, 7, p_192544_);
            this.placeBlock(p_192540_, blockstate3, 7, 7, 7, p_192544_);
            this.placeBlock(p_192540_, blockstate1, 5, 7, 7, p_192544_);
            this.placeBlock(p_192540_, blockstate2, 8, 7, 7, p_192544_);
            this.placeBlock(p_192540_, blockstate1.setValue(FenceBlock.NORTH, Boolean.valueOf(true)), 6, 7, 6, p_192544_);
            this.placeBlock(p_192540_, blockstate1.setValue(FenceBlock.SOUTH, Boolean.valueOf(true)), 6, 7, 8, p_192544_);
            this.placeBlock(p_192540_, blockstate2.setValue(FenceBlock.NORTH, Boolean.valueOf(true)), 7, 7, 6, p_192544_);
            this.placeBlock(p_192540_, blockstate2.setValue(FenceBlock.SOUTH, Boolean.valueOf(true)), 7, 7, 8, p_192544_);
            BlockState blockstate4 = Blocks.TORCH.defaultBlockState();
            this.placeBlock(p_192540_, blockstate4, 5, 8, 7, p_192544_);
            this.placeBlock(p_192540_, blockstate4, 8, 8, 7, p_192544_);
            this.placeBlock(p_192540_, blockstate4, 6, 8, 6, p_192544_);
            this.placeBlock(p_192540_, blockstate4, 6, 8, 8, p_192544_);
            this.placeBlock(p_192540_, blockstate4, 7, 8, 6, p_192544_);
            this.placeBlock(p_192540_, blockstate4, 7, 8, 8, p_192544_);
         }

         this.createChest(p_192540_, p_192544_, p_192543_, 3, 3, 5, BuiltInLootTables.STRONGHOLD_LIBRARY);
         if (this.isTall) {
            this.placeBlock(p_192540_, CAVE_AIR, 12, 9, 1, p_192544_);
            this.createChest(p_192540_, p_192544_, p_192543_, 12, 8, 1, BuiltInLootTables.STRONGHOLD_LIBRARY);
         }

      }
   }

   static class PieceWeight {
      public final Class<? extends StrongholdPieces.StrongholdPiece> pieceClass;
      public final int weight;
      public int placeCount;
      public final int maxPlaceCount;

      public PieceWeight(Class<? extends StrongholdPieces.StrongholdPiece> p_73063_, int p_73064_, int p_73065_) {
         this.pieceClass = p_73063_;
         this.weight = p_73064_;
         this.maxPlaceCount = p_73065_;
      }

      public boolean doPlace(int p_73067_) {
         return this.maxPlaceCount == 0 || this.placeCount < this.maxPlaceCount;
      }

      public boolean isValid() {
         return this.maxPlaceCount == 0 || this.placeCount < this.maxPlaceCount;
      }
   }

   public static class PortalRoom extends StrongholdPieces.StrongholdPiece {
      protected static final int WIDTH = 11;
      protected static final int HEIGHT = 8;
      protected static final int DEPTH = 16;
      private boolean hasPlacedSpawner;

      public PortalRoom(int p_73070_, BoundingBox p_73071_, Direction p_73072_) {
         super(StructurePieceType.STRONGHOLD_PORTAL_ROOM, p_73070_, p_73071_);
         this.setOrientation(p_73072_);
      }

      public PortalRoom(CompoundTag p_192551_) {
         super(StructurePieceType.STRONGHOLD_PORTAL_ROOM, p_192551_);
         this.hasPlacedSpawner = p_192551_.getBoolean("Mob");
      }

      protected void addAdditionalSaveData(StructurePieceSerializationContext p_192561_, CompoundTag p_192562_) {
         super.addAdditionalSaveData(p_192561_, p_192562_);
         p_192562_.putBoolean("Mob", this.hasPlacedSpawner);
      }

      public void addChildren(StructurePiece p_163353_, StructurePieceAccessor p_163354_, Random p_163355_) {
         if (p_163353_ != null) {
            ((StrongholdPieces.StartPiece)p_163353_).portalRoomPiece = this;
         }

      }

      public static StrongholdPieces.PortalRoom createPiece(StructurePieceAccessor p_163357_, int p_163358_, int p_163359_, int p_163360_, Direction p_163361_, int p_163362_) {
         BoundingBox boundingbox = BoundingBox.orientBox(p_163358_, p_163359_, p_163360_, -4, -1, 0, 11, 8, 16, p_163361_);
         return isOkBox(boundingbox) && p_163357_.findCollisionPiece(boundingbox) == null ? new StrongholdPieces.PortalRoom(p_163362_, boundingbox, p_163361_) : null;
      }

      public void postProcess(WorldGenLevel p_192553_, StructureFeatureManager p_192554_, ChunkGenerator p_192555_, Random p_192556_, BoundingBox p_192557_, ChunkPos p_192558_, BlockPos p_192559_) {
         this.generateBox(p_192553_, p_192557_, 0, 0, 0, 10, 7, 15, false, p_192556_, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateSmallDoor(p_192553_, p_192556_, p_192557_, StrongholdPieces.StrongholdPiece.SmallDoorType.GRATES, 4, 1, 0);
         int i = 6;
         this.generateBox(p_192553_, p_192557_, 1, i, 1, 1, i, 14, false, p_192556_, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(p_192553_, p_192557_, 9, i, 1, 9, i, 14, false, p_192556_, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(p_192553_, p_192557_, 2, i, 1, 8, i, 2, false, p_192556_, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(p_192553_, p_192557_, 2, i, 14, 8, i, 14, false, p_192556_, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(p_192553_, p_192557_, 1, 1, 1, 2, 1, 4, false, p_192556_, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(p_192553_, p_192557_, 8, 1, 1, 9, 1, 4, false, p_192556_, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(p_192553_, p_192557_, 1, 1, 1, 1, 1, 3, Blocks.LAVA.defaultBlockState(), Blocks.LAVA.defaultBlockState(), false);
         this.generateBox(p_192553_, p_192557_, 9, 1, 1, 9, 1, 3, Blocks.LAVA.defaultBlockState(), Blocks.LAVA.defaultBlockState(), false);
         this.generateBox(p_192553_, p_192557_, 3, 1, 8, 7, 1, 12, false, p_192556_, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(p_192553_, p_192557_, 4, 1, 9, 6, 1, 11, Blocks.LAVA.defaultBlockState(), Blocks.LAVA.defaultBlockState(), false);
         BlockState blockstate = Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.NORTH, Boolean.valueOf(true)).setValue(IronBarsBlock.SOUTH, Boolean.valueOf(true));
         BlockState blockstate1 = Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.WEST, Boolean.valueOf(true)).setValue(IronBarsBlock.EAST, Boolean.valueOf(true));

         for(int j = 3; j < 14; j += 2) {
            this.generateBox(p_192553_, p_192557_, 0, 3, j, 0, 4, j, blockstate, blockstate, false);
            this.generateBox(p_192553_, p_192557_, 10, 3, j, 10, 4, j, blockstate, blockstate, false);
         }

         for(int i1 = 2; i1 < 9; i1 += 2) {
            this.generateBox(p_192553_, p_192557_, i1, 3, 15, i1, 4, 15, blockstate1, blockstate1, false);
         }

         BlockState blockstate5 = Blocks.STONE_BRICK_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.NORTH);
         this.generateBox(p_192553_, p_192557_, 4, 1, 5, 6, 1, 7, false, p_192556_, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(p_192553_, p_192557_, 4, 2, 6, 6, 2, 7, false, p_192556_, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(p_192553_, p_192557_, 4, 3, 7, 6, 3, 7, false, p_192556_, StrongholdPieces.SMOOTH_STONE_SELECTOR);

         for(int k = 4; k <= 6; ++k) {
            this.placeBlock(p_192553_, blockstate5, k, 1, 4, p_192557_);
            this.placeBlock(p_192553_, blockstate5, k, 2, 5, p_192557_);
            this.placeBlock(p_192553_, blockstate5, k, 3, 6, p_192557_);
         }

         BlockState blockstate6 = Blocks.END_PORTAL_FRAME.defaultBlockState().setValue(EndPortalFrameBlock.FACING, Direction.NORTH);
         BlockState blockstate2 = Blocks.END_PORTAL_FRAME.defaultBlockState().setValue(EndPortalFrameBlock.FACING, Direction.SOUTH);
         BlockState blockstate3 = Blocks.END_PORTAL_FRAME.defaultBlockState().setValue(EndPortalFrameBlock.FACING, Direction.EAST);
         BlockState blockstate4 = Blocks.END_PORTAL_FRAME.defaultBlockState().setValue(EndPortalFrameBlock.FACING, Direction.WEST);
         boolean flag = true;
         boolean[] aboolean = new boolean[12];

         for(int l = 0; l < aboolean.length; ++l) {
            aboolean[l] = p_192556_.nextFloat() > 0.9F;
            flag &= aboolean[l];
         }

         this.placeBlock(p_192553_, blockstate6.setValue(EndPortalFrameBlock.HAS_EYE, Boolean.valueOf(aboolean[0])), 4, 3, 8, p_192557_);
         this.placeBlock(p_192553_, blockstate6.setValue(EndPortalFrameBlock.HAS_EYE, Boolean.valueOf(aboolean[1])), 5, 3, 8, p_192557_);
         this.placeBlock(p_192553_, blockstate6.setValue(EndPortalFrameBlock.HAS_EYE, Boolean.valueOf(aboolean[2])), 6, 3, 8, p_192557_);
         this.placeBlock(p_192553_, blockstate2.setValue(EndPortalFrameBlock.HAS_EYE, Boolean.valueOf(aboolean[3])), 4, 3, 12, p_192557_);
         this.placeBlock(p_192553_, blockstate2.setValue(EndPortalFrameBlock.HAS_EYE, Boolean.valueOf(aboolean[4])), 5, 3, 12, p_192557_);
         this.placeBlock(p_192553_, blockstate2.setValue(EndPortalFrameBlock.HAS_EYE, Boolean.valueOf(aboolean[5])), 6, 3, 12, p_192557_);
         this.placeBlock(p_192553_, blockstate3.setValue(EndPortalFrameBlock.HAS_EYE, Boolean.valueOf(aboolean[6])), 3, 3, 9, p_192557_);
         this.placeBlock(p_192553_, blockstate3.setValue(EndPortalFrameBlock.HAS_EYE, Boolean.valueOf(aboolean[7])), 3, 3, 10, p_192557_);
         this.placeBlock(p_192553_, blockstate3.setValue(EndPortalFrameBlock.HAS_EYE, Boolean.valueOf(aboolean[8])), 3, 3, 11, p_192557_);
         this.placeBlock(p_192553_, blockstate4.setValue(EndPortalFrameBlock.HAS_EYE, Boolean.valueOf(aboolean[9])), 7, 3, 9, p_192557_);
         this.placeBlock(p_192553_, blockstate4.setValue(EndPortalFrameBlock.HAS_EYE, Boolean.valueOf(aboolean[10])), 7, 3, 10, p_192557_);
         this.placeBlock(p_192553_, blockstate4.setValue(EndPortalFrameBlock.HAS_EYE, Boolean.valueOf(aboolean[11])), 7, 3, 11, p_192557_);
         if (flag) {
            BlockState blockstate7 = Blocks.END_PORTAL.defaultBlockState();
            this.placeBlock(p_192553_, blockstate7, 4, 3, 9, p_192557_);
            this.placeBlock(p_192553_, blockstate7, 5, 3, 9, p_192557_);
            this.placeBlock(p_192553_, blockstate7, 6, 3, 9, p_192557_);
            this.placeBlock(p_192553_, blockstate7, 4, 3, 10, p_192557_);
            this.placeBlock(p_192553_, blockstate7, 5, 3, 10, p_192557_);
            this.placeBlock(p_192553_, blockstate7, 6, 3, 10, p_192557_);
            this.placeBlock(p_192553_, blockstate7, 4, 3, 11, p_192557_);
            this.placeBlock(p_192553_, blockstate7, 5, 3, 11, p_192557_);
            this.placeBlock(p_192553_, blockstate7, 6, 3, 11, p_192557_);
         }

         if (!this.hasPlacedSpawner) {
            BlockPos blockpos = this.getWorldPos(5, 3, 6);
            if (p_192557_.isInside(blockpos)) {
               this.hasPlacedSpawner = true;
               p_192553_.setBlock(blockpos, Blocks.SPAWNER.defaultBlockState(), 2);
               BlockEntity blockentity = p_192553_.getBlockEntity(blockpos);
               if (blockentity instanceof SpawnerBlockEntity) {
                  ((SpawnerBlockEntity)blockentity).getSpawner().setEntityId(EntityType.SILVERFISH);
               }
            }
         }

      }
   }

   public static class PrisonHall extends StrongholdPieces.StrongholdPiece {
      protected static final int WIDTH = 9;
      protected static final int HEIGHT = 5;
      protected static final int DEPTH = 11;

      public PrisonHall(int p_73098_, Random p_73099_, BoundingBox p_73100_, Direction p_73101_) {
         super(StructurePieceType.STRONGHOLD_PRISON_HALL, p_73098_, p_73100_);
         this.setOrientation(p_73101_);
         this.entryDoor = this.randomSmallDoor(p_73099_);
      }

      public PrisonHall(CompoundTag p_192564_) {
         super(StructurePieceType.STRONGHOLD_PRISON_HALL, p_192564_);
      }

      public void addChildren(StructurePiece p_163371_, StructurePieceAccessor p_163372_, Random p_163373_) {
         this.generateSmallDoorChildForward((StrongholdPieces.StartPiece)p_163371_, p_163372_, p_163373_, 1, 1);
      }

      public static StrongholdPieces.PrisonHall createPiece(StructurePieceAccessor p_163375_, Random p_163376_, int p_163377_, int p_163378_, int p_163379_, Direction p_163380_, int p_163381_) {
         BoundingBox boundingbox = BoundingBox.orientBox(p_163377_, p_163378_, p_163379_, -1, -1, 0, 9, 5, 11, p_163380_);
         return isOkBox(boundingbox) && p_163375_.findCollisionPiece(boundingbox) == null ? new StrongholdPieces.PrisonHall(p_163381_, p_163376_, boundingbox, p_163380_) : null;
      }

      public void postProcess(WorldGenLevel p_192566_, StructureFeatureManager p_192567_, ChunkGenerator p_192568_, Random p_192569_, BoundingBox p_192570_, ChunkPos p_192571_, BlockPos p_192572_) {
         this.generateBox(p_192566_, p_192570_, 0, 0, 0, 8, 4, 10, true, p_192569_, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateSmallDoor(p_192566_, p_192569_, p_192570_, this.entryDoor, 1, 1, 0);
         this.generateBox(p_192566_, p_192570_, 1, 1, 10, 3, 3, 10, CAVE_AIR, CAVE_AIR, false);
         this.generateBox(p_192566_, p_192570_, 4, 1, 1, 4, 3, 1, false, p_192569_, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(p_192566_, p_192570_, 4, 1, 3, 4, 3, 3, false, p_192569_, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(p_192566_, p_192570_, 4, 1, 7, 4, 3, 7, false, p_192569_, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(p_192566_, p_192570_, 4, 1, 9, 4, 3, 9, false, p_192569_, StrongholdPieces.SMOOTH_STONE_SELECTOR);

         for(int i = 1; i <= 3; ++i) {
            this.placeBlock(p_192566_, Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.NORTH, Boolean.valueOf(true)).setValue(IronBarsBlock.SOUTH, Boolean.valueOf(true)), 4, i, 4, p_192570_);
            this.placeBlock(p_192566_, Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.NORTH, Boolean.valueOf(true)).setValue(IronBarsBlock.SOUTH, Boolean.valueOf(true)).setValue(IronBarsBlock.EAST, Boolean.valueOf(true)), 4, i, 5, p_192570_);
            this.placeBlock(p_192566_, Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.NORTH, Boolean.valueOf(true)).setValue(IronBarsBlock.SOUTH, Boolean.valueOf(true)), 4, i, 6, p_192570_);
            this.placeBlock(p_192566_, Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.WEST, Boolean.valueOf(true)).setValue(IronBarsBlock.EAST, Boolean.valueOf(true)), 5, i, 5, p_192570_);
            this.placeBlock(p_192566_, Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.WEST, Boolean.valueOf(true)).setValue(IronBarsBlock.EAST, Boolean.valueOf(true)), 6, i, 5, p_192570_);
            this.placeBlock(p_192566_, Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.WEST, Boolean.valueOf(true)).setValue(IronBarsBlock.EAST, Boolean.valueOf(true)), 7, i, 5, p_192570_);
         }

         this.placeBlock(p_192566_, Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.NORTH, Boolean.valueOf(true)).setValue(IronBarsBlock.SOUTH, Boolean.valueOf(true)), 4, 3, 2, p_192570_);
         this.placeBlock(p_192566_, Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.NORTH, Boolean.valueOf(true)).setValue(IronBarsBlock.SOUTH, Boolean.valueOf(true)), 4, 3, 8, p_192570_);
         BlockState blockstate1 = Blocks.IRON_DOOR.defaultBlockState().setValue(DoorBlock.FACING, Direction.WEST);
         BlockState blockstate = Blocks.IRON_DOOR.defaultBlockState().setValue(DoorBlock.FACING, Direction.WEST).setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER);
         this.placeBlock(p_192566_, blockstate1, 4, 1, 2, p_192570_);
         this.placeBlock(p_192566_, blockstate, 4, 2, 2, p_192570_);
         this.placeBlock(p_192566_, blockstate1, 4, 1, 8, p_192570_);
         this.placeBlock(p_192566_, blockstate, 4, 2, 8, p_192570_);
      }
   }

   public static class RightTurn extends StrongholdPieces.Turn {
      public RightTurn(int p_73126_, Random p_73127_, BoundingBox p_73128_, Direction p_73129_) {
         super(StructurePieceType.STRONGHOLD_RIGHT_TURN, p_73126_, p_73128_);
         this.setOrientation(p_73129_);
         this.entryDoor = this.randomSmallDoor(p_73127_);
      }

      public RightTurn(CompoundTag p_192574_) {
         super(StructurePieceType.STRONGHOLD_RIGHT_TURN, p_192574_);
      }

      public void addChildren(StructurePiece p_163387_, StructurePieceAccessor p_163388_, Random p_163389_) {
         Direction direction = this.getOrientation();
         if (direction != Direction.NORTH && direction != Direction.EAST) {
            this.generateSmallDoorChildLeft((StrongholdPieces.StartPiece)p_163387_, p_163388_, p_163389_, 1, 1);
         } else {
            this.generateSmallDoorChildRight((StrongholdPieces.StartPiece)p_163387_, p_163388_, p_163389_, 1, 1);
         }

      }

      public static StrongholdPieces.RightTurn createPiece(StructurePieceAccessor p_163391_, Random p_163392_, int p_163393_, int p_163394_, int p_163395_, Direction p_163396_, int p_163397_) {
         BoundingBox boundingbox = BoundingBox.orientBox(p_163393_, p_163394_, p_163395_, -1, -1, 0, 5, 5, 5, p_163396_);
         return isOkBox(boundingbox) && p_163391_.findCollisionPiece(boundingbox) == null ? new StrongholdPieces.RightTurn(p_163397_, p_163392_, boundingbox, p_163396_) : null;
      }

      public void postProcess(WorldGenLevel p_192576_, StructureFeatureManager p_192577_, ChunkGenerator p_192578_, Random p_192579_, BoundingBox p_192580_, ChunkPos p_192581_, BlockPos p_192582_) {
         this.generateBox(p_192576_, p_192580_, 0, 0, 0, 4, 4, 4, true, p_192579_, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateSmallDoor(p_192576_, p_192579_, p_192580_, this.entryDoor, 1, 1, 0);
         Direction direction = this.getOrientation();
         if (direction != Direction.NORTH && direction != Direction.EAST) {
            this.generateBox(p_192576_, p_192580_, 0, 1, 1, 0, 3, 3, CAVE_AIR, CAVE_AIR, false);
         } else {
            this.generateBox(p_192576_, p_192580_, 4, 1, 1, 4, 3, 3, CAVE_AIR, CAVE_AIR, false);
         }

      }
   }

   public static class RoomCrossing extends StrongholdPieces.StrongholdPiece {
      protected static final int WIDTH = 11;
      protected static final int HEIGHT = 7;
      protected static final int DEPTH = 11;
      protected final int type;

      public RoomCrossing(int p_73155_, Random p_73156_, BoundingBox p_73157_, Direction p_73158_) {
         super(StructurePieceType.STRONGHOLD_ROOM_CROSSING, p_73155_, p_73157_);
         this.setOrientation(p_73158_);
         this.entryDoor = this.randomSmallDoor(p_73156_);
         this.type = p_73156_.nextInt(5);
      }

      public RoomCrossing(CompoundTag p_192584_) {
         super(StructurePieceType.STRONGHOLD_ROOM_CROSSING, p_192584_);
         this.type = p_192584_.getInt("Type");
      }

      protected void addAdditionalSaveData(StructurePieceSerializationContext p_192594_, CompoundTag p_192595_) {
         super.addAdditionalSaveData(p_192594_, p_192595_);
         p_192595_.putInt("Type", this.type);
      }

      public void addChildren(StructurePiece p_163408_, StructurePieceAccessor p_163409_, Random p_163410_) {
         this.generateSmallDoorChildForward((StrongholdPieces.StartPiece)p_163408_, p_163409_, p_163410_, 4, 1);
         this.generateSmallDoorChildLeft((StrongholdPieces.StartPiece)p_163408_, p_163409_, p_163410_, 1, 4);
         this.generateSmallDoorChildRight((StrongholdPieces.StartPiece)p_163408_, p_163409_, p_163410_, 1, 4);
      }

      public static StrongholdPieces.RoomCrossing createPiece(StructurePieceAccessor p_163412_, Random p_163413_, int p_163414_, int p_163415_, int p_163416_, Direction p_163417_, int p_163418_) {
         BoundingBox boundingbox = BoundingBox.orientBox(p_163414_, p_163415_, p_163416_, -4, -1, 0, 11, 7, 11, p_163417_);
         return isOkBox(boundingbox) && p_163412_.findCollisionPiece(boundingbox) == null ? new StrongholdPieces.RoomCrossing(p_163418_, p_163413_, boundingbox, p_163417_) : null;
      }

      public void postProcess(WorldGenLevel p_192586_, StructureFeatureManager p_192587_, ChunkGenerator p_192588_, Random p_192589_, BoundingBox p_192590_, ChunkPos p_192591_, BlockPos p_192592_) {
         this.generateBox(p_192586_, p_192590_, 0, 0, 0, 10, 6, 10, true, p_192589_, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateSmallDoor(p_192586_, p_192589_, p_192590_, this.entryDoor, 4, 1, 0);
         this.generateBox(p_192586_, p_192590_, 4, 1, 10, 6, 3, 10, CAVE_AIR, CAVE_AIR, false);
         this.generateBox(p_192586_, p_192590_, 0, 1, 4, 0, 3, 6, CAVE_AIR, CAVE_AIR, false);
         this.generateBox(p_192586_, p_192590_, 10, 1, 4, 10, 3, 6, CAVE_AIR, CAVE_AIR, false);
         switch(this.type) {
         case 0:
            this.placeBlock(p_192586_, Blocks.STONE_BRICKS.defaultBlockState(), 5, 1, 5, p_192590_);
            this.placeBlock(p_192586_, Blocks.STONE_BRICKS.defaultBlockState(), 5, 2, 5, p_192590_);
            this.placeBlock(p_192586_, Blocks.STONE_BRICKS.defaultBlockState(), 5, 3, 5, p_192590_);
            this.placeBlock(p_192586_, Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.WEST), 4, 3, 5, p_192590_);
            this.placeBlock(p_192586_, Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.EAST), 6, 3, 5, p_192590_);
            this.placeBlock(p_192586_, Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.SOUTH), 5, 3, 4, p_192590_);
            this.placeBlock(p_192586_, Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.NORTH), 5, 3, 6, p_192590_);
            this.placeBlock(p_192586_, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 4, 1, 4, p_192590_);
            this.placeBlock(p_192586_, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 4, 1, 5, p_192590_);
            this.placeBlock(p_192586_, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 4, 1, 6, p_192590_);
            this.placeBlock(p_192586_, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 6, 1, 4, p_192590_);
            this.placeBlock(p_192586_, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 6, 1, 5, p_192590_);
            this.placeBlock(p_192586_, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 6, 1, 6, p_192590_);
            this.placeBlock(p_192586_, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 5, 1, 4, p_192590_);
            this.placeBlock(p_192586_, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 5, 1, 6, p_192590_);
            break;
         case 1:
            for(int i1 = 0; i1 < 5; ++i1) {
               this.placeBlock(p_192586_, Blocks.STONE_BRICKS.defaultBlockState(), 3, 1, 3 + i1, p_192590_);
               this.placeBlock(p_192586_, Blocks.STONE_BRICKS.defaultBlockState(), 7, 1, 3 + i1, p_192590_);
               this.placeBlock(p_192586_, Blocks.STONE_BRICKS.defaultBlockState(), 3 + i1, 1, 3, p_192590_);
               this.placeBlock(p_192586_, Blocks.STONE_BRICKS.defaultBlockState(), 3 + i1, 1, 7, p_192590_);
            }

            this.placeBlock(p_192586_, Blocks.STONE_BRICKS.defaultBlockState(), 5, 1, 5, p_192590_);
            this.placeBlock(p_192586_, Blocks.STONE_BRICKS.defaultBlockState(), 5, 2, 5, p_192590_);
            this.placeBlock(p_192586_, Blocks.STONE_BRICKS.defaultBlockState(), 5, 3, 5, p_192590_);
            this.placeBlock(p_192586_, Blocks.WATER.defaultBlockState(), 5, 4, 5, p_192590_);
            break;
         case 2:
            for(int i = 1; i <= 9; ++i) {
               this.placeBlock(p_192586_, Blocks.COBBLESTONE.defaultBlockState(), 1, 3, i, p_192590_);
               this.placeBlock(p_192586_, Blocks.COBBLESTONE.defaultBlockState(), 9, 3, i, p_192590_);
            }

            for(int j = 1; j <= 9; ++j) {
               this.placeBlock(p_192586_, Blocks.COBBLESTONE.defaultBlockState(), j, 3, 1, p_192590_);
               this.placeBlock(p_192586_, Blocks.COBBLESTONE.defaultBlockState(), j, 3, 9, p_192590_);
            }

            this.placeBlock(p_192586_, Blocks.COBBLESTONE.defaultBlockState(), 5, 1, 4, p_192590_);
            this.placeBlock(p_192586_, Blocks.COBBLESTONE.defaultBlockState(), 5, 1, 6, p_192590_);
            this.placeBlock(p_192586_, Blocks.COBBLESTONE.defaultBlockState(), 5, 3, 4, p_192590_);
            this.placeBlock(p_192586_, Blocks.COBBLESTONE.defaultBlockState(), 5, 3, 6, p_192590_);
            this.placeBlock(p_192586_, Blocks.COBBLESTONE.defaultBlockState(), 4, 1, 5, p_192590_);
            this.placeBlock(p_192586_, Blocks.COBBLESTONE.defaultBlockState(), 6, 1, 5, p_192590_);
            this.placeBlock(p_192586_, Blocks.COBBLESTONE.defaultBlockState(), 4, 3, 5, p_192590_);
            this.placeBlock(p_192586_, Blocks.COBBLESTONE.defaultBlockState(), 6, 3, 5, p_192590_);

            for(int k = 1; k <= 3; ++k) {
               this.placeBlock(p_192586_, Blocks.COBBLESTONE.defaultBlockState(), 4, k, 4, p_192590_);
               this.placeBlock(p_192586_, Blocks.COBBLESTONE.defaultBlockState(), 6, k, 4, p_192590_);
               this.placeBlock(p_192586_, Blocks.COBBLESTONE.defaultBlockState(), 4, k, 6, p_192590_);
               this.placeBlock(p_192586_, Blocks.COBBLESTONE.defaultBlockState(), 6, k, 6, p_192590_);
            }

            this.placeBlock(p_192586_, Blocks.TORCH.defaultBlockState(), 5, 3, 5, p_192590_);

            for(int l = 2; l <= 8; ++l) {
               this.placeBlock(p_192586_, Blocks.OAK_PLANKS.defaultBlockState(), 2, 3, l, p_192590_);
               this.placeBlock(p_192586_, Blocks.OAK_PLANKS.defaultBlockState(), 3, 3, l, p_192590_);
               if (l <= 3 || l >= 7) {
                  this.placeBlock(p_192586_, Blocks.OAK_PLANKS.defaultBlockState(), 4, 3, l, p_192590_);
                  this.placeBlock(p_192586_, Blocks.OAK_PLANKS.defaultBlockState(), 5, 3, l, p_192590_);
                  this.placeBlock(p_192586_, Blocks.OAK_PLANKS.defaultBlockState(), 6, 3, l, p_192590_);
               }

               this.placeBlock(p_192586_, Blocks.OAK_PLANKS.defaultBlockState(), 7, 3, l, p_192590_);
               this.placeBlock(p_192586_, Blocks.OAK_PLANKS.defaultBlockState(), 8, 3, l, p_192590_);
            }

            BlockState blockstate = Blocks.LADDER.defaultBlockState().setValue(LadderBlock.FACING, Direction.WEST);
            this.placeBlock(p_192586_, blockstate, 9, 1, 3, p_192590_);
            this.placeBlock(p_192586_, blockstate, 9, 2, 3, p_192590_);
            this.placeBlock(p_192586_, blockstate, 9, 3, 3, p_192590_);
            this.createChest(p_192586_, p_192590_, p_192589_, 3, 4, 8, BuiltInLootTables.STRONGHOLD_CROSSING);
         }

      }
   }

   static class SmoothStoneSelector extends StructurePiece.BlockSelector {
      public void next(Random p_73188_, int p_73189_, int p_73190_, int p_73191_, boolean p_73192_) {
         if (p_73192_) {
            float f = p_73188_.nextFloat();
            if (f < 0.2F) {
               this.next = Blocks.CRACKED_STONE_BRICKS.defaultBlockState();
            } else if (f < 0.5F) {
               this.next = Blocks.MOSSY_STONE_BRICKS.defaultBlockState();
            } else if (f < 0.55F) {
               this.next = Blocks.INFESTED_STONE_BRICKS.defaultBlockState();
            } else {
               this.next = Blocks.STONE_BRICKS.defaultBlockState();
            }
         } else {
            this.next = Blocks.CAVE_AIR.defaultBlockState();
         }

      }
   }

   public static class StairsDown extends StrongholdPieces.StrongholdPiece {
      private static final int WIDTH = 5;
      private static final int HEIGHT = 11;
      private static final int DEPTH = 5;
      private final boolean isSource;

      public StairsDown(StructurePieceType p_209932_, int p_209933_, int p_209934_, int p_209935_, Direction p_209936_) {
         super(p_209932_, p_209933_, makeBoundingBox(p_209934_, 64, p_209935_, p_209936_, 5, 11, 5));
         this.isSource = true;
         this.setOrientation(p_209936_);
         this.entryDoor = StrongholdPieces.StrongholdPiece.SmallDoorType.OPENING;
      }

      public StairsDown(int p_73195_, Random p_73196_, BoundingBox p_73197_, Direction p_73198_) {
         super(StructurePieceType.STRONGHOLD_STAIRS_DOWN, p_73195_, p_73197_);
         this.isSource = false;
         this.setOrientation(p_73198_);
         this.entryDoor = this.randomSmallDoor(p_73196_);
      }

      public StairsDown(StructurePieceType p_209938_, CompoundTag p_209939_) {
         super(p_209938_, p_209939_);
         this.isSource = p_209939_.getBoolean("Source");
      }

      public StairsDown(CompoundTag p_192597_) {
         this(StructurePieceType.STRONGHOLD_STAIRS_DOWN, p_192597_);
      }

      protected void addAdditionalSaveData(StructurePieceSerializationContext p_192607_, CompoundTag p_192608_) {
         super.addAdditionalSaveData(p_192607_, p_192608_);
         p_192608_.putBoolean("Source", this.isSource);
      }

      public void addChildren(StructurePiece p_163436_, StructurePieceAccessor p_163437_, Random p_163438_) {
         if (this.isSource) {
            StrongholdPieces.imposedPiece = StrongholdPieces.FiveCrossing.class;
         }

         this.generateSmallDoorChildForward((StrongholdPieces.StartPiece)p_163436_, p_163437_, p_163438_, 1, 1);
      }

      public static StrongholdPieces.StairsDown createPiece(StructurePieceAccessor p_163440_, Random p_163441_, int p_163442_, int p_163443_, int p_163444_, Direction p_163445_, int p_163446_) {
         BoundingBox boundingbox = BoundingBox.orientBox(p_163442_, p_163443_, p_163444_, -1, -7, 0, 5, 11, 5, p_163445_);
         return isOkBox(boundingbox) && p_163440_.findCollisionPiece(boundingbox) == null ? new StrongholdPieces.StairsDown(p_163446_, p_163441_, boundingbox, p_163445_) : null;
      }

      public void postProcess(WorldGenLevel p_192599_, StructureFeatureManager p_192600_, ChunkGenerator p_192601_, Random p_192602_, BoundingBox p_192603_, ChunkPos p_192604_, BlockPos p_192605_) {
         this.generateBox(p_192599_, p_192603_, 0, 0, 0, 4, 10, 4, true, p_192602_, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateSmallDoor(p_192599_, p_192602_, p_192603_, this.entryDoor, 1, 7, 0);
         this.generateSmallDoor(p_192599_, p_192602_, p_192603_, StrongholdPieces.StrongholdPiece.SmallDoorType.OPENING, 1, 1, 4);
         this.placeBlock(p_192599_, Blocks.STONE_BRICKS.defaultBlockState(), 2, 6, 1, p_192603_);
         this.placeBlock(p_192599_, Blocks.STONE_BRICKS.defaultBlockState(), 1, 5, 1, p_192603_);
         this.placeBlock(p_192599_, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 1, 6, 1, p_192603_);
         this.placeBlock(p_192599_, Blocks.STONE_BRICKS.defaultBlockState(), 1, 5, 2, p_192603_);
         this.placeBlock(p_192599_, Blocks.STONE_BRICKS.defaultBlockState(), 1, 4, 3, p_192603_);
         this.placeBlock(p_192599_, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 1, 5, 3, p_192603_);
         this.placeBlock(p_192599_, Blocks.STONE_BRICKS.defaultBlockState(), 2, 4, 3, p_192603_);
         this.placeBlock(p_192599_, Blocks.STONE_BRICKS.defaultBlockState(), 3, 3, 3, p_192603_);
         this.placeBlock(p_192599_, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 3, 4, 3, p_192603_);
         this.placeBlock(p_192599_, Blocks.STONE_BRICKS.defaultBlockState(), 3, 3, 2, p_192603_);
         this.placeBlock(p_192599_, Blocks.STONE_BRICKS.defaultBlockState(), 3, 2, 1, p_192603_);
         this.placeBlock(p_192599_, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 3, 3, 1, p_192603_);
         this.placeBlock(p_192599_, Blocks.STONE_BRICKS.defaultBlockState(), 2, 2, 1, p_192603_);
         this.placeBlock(p_192599_, Blocks.STONE_BRICKS.defaultBlockState(), 1, 1, 1, p_192603_);
         this.placeBlock(p_192599_, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 1, 2, 1, p_192603_);
         this.placeBlock(p_192599_, Blocks.STONE_BRICKS.defaultBlockState(), 1, 1, 2, p_192603_);
         this.placeBlock(p_192599_, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 1, 1, 3, p_192603_);
      }
   }

   public static class StartPiece extends StrongholdPieces.StairsDown {
      public StrongholdPieces.PieceWeight previousPiece;
      @Nullable
      public StrongholdPieces.PortalRoom portalRoomPiece;
      public final List<StructurePiece> pendingChildren = Lists.newArrayList();

      public StartPiece(Random p_73240_, int p_73241_, int p_73242_) {
         super(StructurePieceType.STRONGHOLD_START, 0, p_73241_, p_73242_, getRandomHorizontalDirection(p_73240_));
      }

      public StartPiece(CompoundTag p_192610_) {
         super(StructurePieceType.STRONGHOLD_START, p_192610_);
      }

      public BlockPos getLocatorPosition() {
         return this.portalRoomPiece != null ? this.portalRoomPiece.getLocatorPosition() : super.getLocatorPosition();
      }
   }

   public static class Straight extends StrongholdPieces.StrongholdPiece {
      private static final int WIDTH = 5;
      private static final int HEIGHT = 5;
      private static final int DEPTH = 7;
      private final boolean leftChild;
      private final boolean rightChild;

      public Straight(int p_73246_, Random p_73247_, BoundingBox p_73248_, Direction p_73249_) {
         super(StructurePieceType.STRONGHOLD_STRAIGHT, p_73246_, p_73248_);
         this.setOrientation(p_73249_);
         this.entryDoor = this.randomSmallDoor(p_73247_);
         this.leftChild = p_73247_.nextInt(2) == 0;
         this.rightChild = p_73247_.nextInt(2) == 0;
      }

      public Straight(CompoundTag p_192612_) {
         super(StructurePieceType.STRONGHOLD_STRAIGHT, p_192612_);
         this.leftChild = p_192612_.getBoolean("Left");
         this.rightChild = p_192612_.getBoolean("Right");
      }

      protected void addAdditionalSaveData(StructurePieceSerializationContext p_192622_, CompoundTag p_192623_) {
         super.addAdditionalSaveData(p_192622_, p_192623_);
         p_192623_.putBoolean("Left", this.leftChild);
         p_192623_.putBoolean("Right", this.rightChild);
      }

      public void addChildren(StructurePiece p_163462_, StructurePieceAccessor p_163463_, Random p_163464_) {
         this.generateSmallDoorChildForward((StrongholdPieces.StartPiece)p_163462_, p_163463_, p_163464_, 1, 1);
         if (this.leftChild) {
            this.generateSmallDoorChildLeft((StrongholdPieces.StartPiece)p_163462_, p_163463_, p_163464_, 1, 2);
         }

         if (this.rightChild) {
            this.generateSmallDoorChildRight((StrongholdPieces.StartPiece)p_163462_, p_163463_, p_163464_, 1, 2);
         }

      }

      public static StrongholdPieces.Straight createPiece(StructurePieceAccessor p_163466_, Random p_163467_, int p_163468_, int p_163469_, int p_163470_, Direction p_163471_, int p_163472_) {
         BoundingBox boundingbox = BoundingBox.orientBox(p_163468_, p_163469_, p_163470_, -1, -1, 0, 5, 5, 7, p_163471_);
         return isOkBox(boundingbox) && p_163466_.findCollisionPiece(boundingbox) == null ? new StrongholdPieces.Straight(p_163472_, p_163467_, boundingbox, p_163471_) : null;
      }

      public void postProcess(WorldGenLevel p_192614_, StructureFeatureManager p_192615_, ChunkGenerator p_192616_, Random p_192617_, BoundingBox p_192618_, ChunkPos p_192619_, BlockPos p_192620_) {
         this.generateBox(p_192614_, p_192618_, 0, 0, 0, 4, 4, 6, true, p_192617_, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateSmallDoor(p_192614_, p_192617_, p_192618_, this.entryDoor, 1, 1, 0);
         this.generateSmallDoor(p_192614_, p_192617_, p_192618_, StrongholdPieces.StrongholdPiece.SmallDoorType.OPENING, 1, 1, 6);
         BlockState blockstate = Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.EAST);
         BlockState blockstate1 = Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.WEST);
         this.maybeGenerateBlock(p_192614_, p_192618_, p_192617_, 0.1F, 1, 2, 1, blockstate);
         this.maybeGenerateBlock(p_192614_, p_192618_, p_192617_, 0.1F, 3, 2, 1, blockstate1);
         this.maybeGenerateBlock(p_192614_, p_192618_, p_192617_, 0.1F, 1, 2, 5, blockstate);
         this.maybeGenerateBlock(p_192614_, p_192618_, p_192617_, 0.1F, 3, 2, 5, blockstate1);
         if (this.leftChild) {
            this.generateBox(p_192614_, p_192618_, 0, 1, 2, 0, 3, 4, CAVE_AIR, CAVE_AIR, false);
         }

         if (this.rightChild) {
            this.generateBox(p_192614_, p_192618_, 4, 1, 2, 4, 3, 4, CAVE_AIR, CAVE_AIR, false);
         }

      }
   }

   public static class StraightStairsDown extends StrongholdPieces.StrongholdPiece {
      private static final int WIDTH = 5;
      private static final int HEIGHT = 11;
      private static final int DEPTH = 8;

      public StraightStairsDown(int p_73276_, Random p_73277_, BoundingBox p_73278_, Direction p_73279_) {
         super(StructurePieceType.STRONGHOLD_STRAIGHT_STAIRS_DOWN, p_73276_, p_73278_);
         this.setOrientation(p_73279_);
         this.entryDoor = this.randomSmallDoor(p_73277_);
      }

      public StraightStairsDown(CompoundTag p_192625_) {
         super(StructurePieceType.STRONGHOLD_STRAIGHT_STAIRS_DOWN, p_192625_);
      }

      public void addChildren(StructurePiece p_163481_, StructurePieceAccessor p_163482_, Random p_163483_) {
         this.generateSmallDoorChildForward((StrongholdPieces.StartPiece)p_163481_, p_163482_, p_163483_, 1, 1);
      }

      public static StrongholdPieces.StraightStairsDown createPiece(StructurePieceAccessor p_163485_, Random p_163486_, int p_163487_, int p_163488_, int p_163489_, Direction p_163490_, int p_163491_) {
         BoundingBox boundingbox = BoundingBox.orientBox(p_163487_, p_163488_, p_163489_, -1, -7, 0, 5, 11, 8, p_163490_);
         return isOkBox(boundingbox) && p_163485_.findCollisionPiece(boundingbox) == null ? new StrongholdPieces.StraightStairsDown(p_163491_, p_163486_, boundingbox, p_163490_) : null;
      }

      public void postProcess(WorldGenLevel p_192627_, StructureFeatureManager p_192628_, ChunkGenerator p_192629_, Random p_192630_, BoundingBox p_192631_, ChunkPos p_192632_, BlockPos p_192633_) {
         this.generateBox(p_192627_, p_192631_, 0, 0, 0, 4, 10, 7, true, p_192630_, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateSmallDoor(p_192627_, p_192630_, p_192631_, this.entryDoor, 1, 7, 0);
         this.generateSmallDoor(p_192627_, p_192630_, p_192631_, StrongholdPieces.StrongholdPiece.SmallDoorType.OPENING, 1, 1, 7);
         BlockState blockstate = Blocks.COBBLESTONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.SOUTH);

         for(int i = 0; i < 6; ++i) {
            this.placeBlock(p_192627_, blockstate, 1, 6 - i, 1 + i, p_192631_);
            this.placeBlock(p_192627_, blockstate, 2, 6 - i, 1 + i, p_192631_);
            this.placeBlock(p_192627_, blockstate, 3, 6 - i, 1 + i, p_192631_);
            if (i < 5) {
               this.placeBlock(p_192627_, Blocks.STONE_BRICKS.defaultBlockState(), 1, 5 - i, 1 + i, p_192631_);
               this.placeBlock(p_192627_, Blocks.STONE_BRICKS.defaultBlockState(), 2, 5 - i, 1 + i, p_192631_);
               this.placeBlock(p_192627_, Blocks.STONE_BRICKS.defaultBlockState(), 3, 5 - i, 1 + i, p_192631_);
            }
         }

      }
   }

   abstract static class StrongholdPiece extends StructurePiece {
      protected StrongholdPieces.StrongholdPiece.SmallDoorType entryDoor = StrongholdPieces.StrongholdPiece.SmallDoorType.OPENING;

      protected StrongholdPiece(StructurePieceType p_209941_, int p_209942_, BoundingBox p_209943_) {
         super(p_209941_, p_209942_, p_209943_);
      }

      public StrongholdPiece(StructurePieceType p_209945_, CompoundTag p_209946_) {
         super(p_209945_, p_209946_);
         this.entryDoor = StrongholdPieces.StrongholdPiece.SmallDoorType.valueOf(p_209946_.getString("EntryDoor"));
      }

      public NoiseEffect getNoiseEffect() {
         return NoiseEffect.BURY;
      }

      protected void addAdditionalSaveData(StructurePieceSerializationContext p_192635_, CompoundTag p_192636_) {
         p_192636_.putString("EntryDoor", this.entryDoor.name());
      }

      protected void generateSmallDoor(WorldGenLevel p_73311_, Random p_73312_, BoundingBox p_73313_, StrongholdPieces.StrongholdPiece.SmallDoorType p_73314_, int p_73315_, int p_73316_, int p_73317_) {
         switch(p_73314_) {
         case OPENING:
            this.generateBox(p_73311_, p_73313_, p_73315_, p_73316_, p_73317_, p_73315_ + 3 - 1, p_73316_ + 3 - 1, p_73317_, CAVE_AIR, CAVE_AIR, false);
            break;
         case WOOD_DOOR:
            this.placeBlock(p_73311_, Blocks.STONE_BRICKS.defaultBlockState(), p_73315_, p_73316_, p_73317_, p_73313_);
            this.placeBlock(p_73311_, Blocks.STONE_BRICKS.defaultBlockState(), p_73315_, p_73316_ + 1, p_73317_, p_73313_);
            this.placeBlock(p_73311_, Blocks.STONE_BRICKS.defaultBlockState(), p_73315_, p_73316_ + 2, p_73317_, p_73313_);
            this.placeBlock(p_73311_, Blocks.STONE_BRICKS.defaultBlockState(), p_73315_ + 1, p_73316_ + 2, p_73317_, p_73313_);
            this.placeBlock(p_73311_, Blocks.STONE_BRICKS.defaultBlockState(), p_73315_ + 2, p_73316_ + 2, p_73317_, p_73313_);
            this.placeBlock(p_73311_, Blocks.STONE_BRICKS.defaultBlockState(), p_73315_ + 2, p_73316_ + 1, p_73317_, p_73313_);
            this.placeBlock(p_73311_, Blocks.STONE_BRICKS.defaultBlockState(), p_73315_ + 2, p_73316_, p_73317_, p_73313_);
            this.placeBlock(p_73311_, Blocks.OAK_DOOR.defaultBlockState(), p_73315_ + 1, p_73316_, p_73317_, p_73313_);
            this.placeBlock(p_73311_, Blocks.OAK_DOOR.defaultBlockState().setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER), p_73315_ + 1, p_73316_ + 1, p_73317_, p_73313_);
            break;
         case GRATES:
            this.placeBlock(p_73311_, Blocks.CAVE_AIR.defaultBlockState(), p_73315_ + 1, p_73316_, p_73317_, p_73313_);
            this.placeBlock(p_73311_, Blocks.CAVE_AIR.defaultBlockState(), p_73315_ + 1, p_73316_ + 1, p_73317_, p_73313_);
            this.placeBlock(p_73311_, Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.WEST, Boolean.valueOf(true)), p_73315_, p_73316_, p_73317_, p_73313_);
            this.placeBlock(p_73311_, Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.WEST, Boolean.valueOf(true)), p_73315_, p_73316_ + 1, p_73317_, p_73313_);
            this.placeBlock(p_73311_, Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.EAST, Boolean.valueOf(true)).setValue(IronBarsBlock.WEST, Boolean.valueOf(true)), p_73315_, p_73316_ + 2, p_73317_, p_73313_);
            this.placeBlock(p_73311_, Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.EAST, Boolean.valueOf(true)).setValue(IronBarsBlock.WEST, Boolean.valueOf(true)), p_73315_ + 1, p_73316_ + 2, p_73317_, p_73313_);
            this.placeBlock(p_73311_, Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.EAST, Boolean.valueOf(true)).setValue(IronBarsBlock.WEST, Boolean.valueOf(true)), p_73315_ + 2, p_73316_ + 2, p_73317_, p_73313_);
            this.placeBlock(p_73311_, Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.EAST, Boolean.valueOf(true)), p_73315_ + 2, p_73316_ + 1, p_73317_, p_73313_);
            this.placeBlock(p_73311_, Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.EAST, Boolean.valueOf(true)), p_73315_ + 2, p_73316_, p_73317_, p_73313_);
            break;
         case IRON_DOOR:
            this.placeBlock(p_73311_, Blocks.STONE_BRICKS.defaultBlockState(), p_73315_, p_73316_, p_73317_, p_73313_);
            this.placeBlock(p_73311_, Blocks.STONE_BRICKS.defaultBlockState(), p_73315_, p_73316_ + 1, p_73317_, p_73313_);
            this.placeBlock(p_73311_, Blocks.STONE_BRICKS.defaultBlockState(), p_73315_, p_73316_ + 2, p_73317_, p_73313_);
            this.placeBlock(p_73311_, Blocks.STONE_BRICKS.defaultBlockState(), p_73315_ + 1, p_73316_ + 2, p_73317_, p_73313_);
            this.placeBlock(p_73311_, Blocks.STONE_BRICKS.defaultBlockState(), p_73315_ + 2, p_73316_ + 2, p_73317_, p_73313_);
            this.placeBlock(p_73311_, Blocks.STONE_BRICKS.defaultBlockState(), p_73315_ + 2, p_73316_ + 1, p_73317_, p_73313_);
            this.placeBlock(p_73311_, Blocks.STONE_BRICKS.defaultBlockState(), p_73315_ + 2, p_73316_, p_73317_, p_73313_);
            this.placeBlock(p_73311_, Blocks.IRON_DOOR.defaultBlockState(), p_73315_ + 1, p_73316_, p_73317_, p_73313_);
            this.placeBlock(p_73311_, Blocks.IRON_DOOR.defaultBlockState().setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER), p_73315_ + 1, p_73316_ + 1, p_73317_, p_73313_);
            this.placeBlock(p_73311_, Blocks.STONE_BUTTON.defaultBlockState().setValue(ButtonBlock.FACING, Direction.NORTH), p_73315_ + 2, p_73316_ + 1, p_73317_ + 1, p_73313_);
            this.placeBlock(p_73311_, Blocks.STONE_BUTTON.defaultBlockState().setValue(ButtonBlock.FACING, Direction.SOUTH), p_73315_ + 2, p_73316_ + 1, p_73317_ - 1, p_73313_);
         }

      }

      protected StrongholdPieces.StrongholdPiece.SmallDoorType randomSmallDoor(Random p_73327_) {
         int i = p_73327_.nextInt(5);
         switch(i) {
         case 0:
         case 1:
         default:
            return StrongholdPieces.StrongholdPiece.SmallDoorType.OPENING;
         case 2:
            return StrongholdPieces.StrongholdPiece.SmallDoorType.WOOD_DOOR;
         case 3:
            return StrongholdPieces.StrongholdPiece.SmallDoorType.GRATES;
         case 4:
            return StrongholdPieces.StrongholdPiece.SmallDoorType.IRON_DOOR;
         }
      }

      @Nullable
      protected StructurePiece generateSmallDoorChildForward(StrongholdPieces.StartPiece p_163501_, StructurePieceAccessor p_163502_, Random p_163503_, int p_163504_, int p_163505_) {
         Direction direction = this.getOrientation();
         if (direction != null) {
            switch(direction) {
            case NORTH:
               return StrongholdPieces.generateAndAddPiece(p_163501_, p_163502_, p_163503_, this.boundingBox.minX() + p_163504_, this.boundingBox.minY() + p_163505_, this.boundingBox.minZ() - 1, direction, this.getGenDepth());
            case SOUTH:
               return StrongholdPieces.generateAndAddPiece(p_163501_, p_163502_, p_163503_, this.boundingBox.minX() + p_163504_, this.boundingBox.minY() + p_163505_, this.boundingBox.maxZ() + 1, direction, this.getGenDepth());
            case WEST:
               return StrongholdPieces.generateAndAddPiece(p_163501_, p_163502_, p_163503_, this.boundingBox.minX() - 1, this.boundingBox.minY() + p_163505_, this.boundingBox.minZ() + p_163504_, direction, this.getGenDepth());
            case EAST:
               return StrongholdPieces.generateAndAddPiece(p_163501_, p_163502_, p_163503_, this.boundingBox.maxX() + 1, this.boundingBox.minY() + p_163505_, this.boundingBox.minZ() + p_163504_, direction, this.getGenDepth());
            }
         }

         return null;
      }

      @Nullable
      protected StructurePiece generateSmallDoorChildLeft(StrongholdPieces.StartPiece p_163508_, StructurePieceAccessor p_163509_, Random p_163510_, int p_163511_, int p_163512_) {
         Direction direction = this.getOrientation();
         if (direction != null) {
            switch(direction) {
            case NORTH:
               return StrongholdPieces.generateAndAddPiece(p_163508_, p_163509_, p_163510_, this.boundingBox.minX() - 1, this.boundingBox.minY() + p_163511_, this.boundingBox.minZ() + p_163512_, Direction.WEST, this.getGenDepth());
            case SOUTH:
               return StrongholdPieces.generateAndAddPiece(p_163508_, p_163509_, p_163510_, this.boundingBox.minX() - 1, this.boundingBox.minY() + p_163511_, this.boundingBox.minZ() + p_163512_, Direction.WEST, this.getGenDepth());
            case WEST:
               return StrongholdPieces.generateAndAddPiece(p_163508_, p_163509_, p_163510_, this.boundingBox.minX() + p_163512_, this.boundingBox.minY() + p_163511_, this.boundingBox.minZ() - 1, Direction.NORTH, this.getGenDepth());
            case EAST:
               return StrongholdPieces.generateAndAddPiece(p_163508_, p_163509_, p_163510_, this.boundingBox.minX() + p_163512_, this.boundingBox.minY() + p_163511_, this.boundingBox.minZ() - 1, Direction.NORTH, this.getGenDepth());
            }
         }

         return null;
      }

      @Nullable
      protected StructurePiece generateSmallDoorChildRight(StrongholdPieces.StartPiece p_163514_, StructurePieceAccessor p_163515_, Random p_163516_, int p_163517_, int p_163518_) {
         Direction direction = this.getOrientation();
         if (direction != null) {
            switch(direction) {
            case NORTH:
               return StrongholdPieces.generateAndAddPiece(p_163514_, p_163515_, p_163516_, this.boundingBox.maxX() + 1, this.boundingBox.minY() + p_163517_, this.boundingBox.minZ() + p_163518_, Direction.EAST, this.getGenDepth());
            case SOUTH:
               return StrongholdPieces.generateAndAddPiece(p_163514_, p_163515_, p_163516_, this.boundingBox.maxX() + 1, this.boundingBox.minY() + p_163517_, this.boundingBox.minZ() + p_163518_, Direction.EAST, this.getGenDepth());
            case WEST:
               return StrongholdPieces.generateAndAddPiece(p_163514_, p_163515_, p_163516_, this.boundingBox.minX() + p_163518_, this.boundingBox.minY() + p_163517_, this.boundingBox.maxZ() + 1, Direction.SOUTH, this.getGenDepth());
            case EAST:
               return StrongholdPieces.generateAndAddPiece(p_163514_, p_163515_, p_163516_, this.boundingBox.minX() + p_163518_, this.boundingBox.minY() + p_163517_, this.boundingBox.maxZ() + 1, Direction.SOUTH, this.getGenDepth());
            }
         }

         return null;
      }

      protected static boolean isOkBox(BoundingBox p_73319_) {
         return p_73319_ != null && p_73319_.minY() > 10;
      }

      protected static enum SmallDoorType {
         OPENING,
         WOOD_DOOR,
         GRATES,
         IRON_DOOR;
      }
   }

   public abstract static class Turn extends StrongholdPieces.StrongholdPiece {
      protected static final int WIDTH = 5;
      protected static final int HEIGHT = 5;
      protected static final int DEPTH = 5;

      protected Turn(StructurePieceType p_209948_, int p_209949_, BoundingBox p_209950_) {
         super(p_209948_, p_209949_, p_209950_);
      }

      public Turn(StructurePieceType p_209952_, CompoundTag p_209953_) {
         super(p_209952_, p_209953_);
      }
   }
}