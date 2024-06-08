package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

public class OceanMonumentPieces {
   private OceanMonumentPieces() {
   }

   static class FitDoubleXRoom implements OceanMonumentPieces.MonumentRoomFitter {
      public boolean fits(OceanMonumentPieces.RoomDefinition p_72097_) {
         return p_72097_.hasOpening[Direction.EAST.get3DDataValue()] && !p_72097_.connections[Direction.EAST.get3DDataValue()].claimed;
      }

      public OceanMonumentPieces.OceanMonumentPiece create(Direction p_72099_, OceanMonumentPieces.RoomDefinition p_72100_, Random p_72101_) {
         p_72100_.claimed = true;
         p_72100_.connections[Direction.EAST.get3DDataValue()].claimed = true;
         return new OceanMonumentPieces.OceanMonumentDoubleXRoom(p_72099_, p_72100_);
      }
   }

   static class FitDoubleXYRoom implements OceanMonumentPieces.MonumentRoomFitter {
      public boolean fits(OceanMonumentPieces.RoomDefinition p_72106_) {
         if (p_72106_.hasOpening[Direction.EAST.get3DDataValue()] && !p_72106_.connections[Direction.EAST.get3DDataValue()].claimed && p_72106_.hasOpening[Direction.UP.get3DDataValue()] && !p_72106_.connections[Direction.UP.get3DDataValue()].claimed) {
            OceanMonumentPieces.RoomDefinition oceanmonumentpieces$roomdefinition = p_72106_.connections[Direction.EAST.get3DDataValue()];
            return oceanmonumentpieces$roomdefinition.hasOpening[Direction.UP.get3DDataValue()] && !oceanmonumentpieces$roomdefinition.connections[Direction.UP.get3DDataValue()].claimed;
         } else {
            return false;
         }
      }

      public OceanMonumentPieces.OceanMonumentPiece create(Direction p_72108_, OceanMonumentPieces.RoomDefinition p_72109_, Random p_72110_) {
         p_72109_.claimed = true;
         p_72109_.connections[Direction.EAST.get3DDataValue()].claimed = true;
         p_72109_.connections[Direction.UP.get3DDataValue()].claimed = true;
         p_72109_.connections[Direction.EAST.get3DDataValue()].connections[Direction.UP.get3DDataValue()].claimed = true;
         return new OceanMonumentPieces.OceanMonumentDoubleXYRoom(p_72108_, p_72109_);
      }
   }

   static class FitDoubleYRoom implements OceanMonumentPieces.MonumentRoomFitter {
      public boolean fits(OceanMonumentPieces.RoomDefinition p_72115_) {
         return p_72115_.hasOpening[Direction.UP.get3DDataValue()] && !p_72115_.connections[Direction.UP.get3DDataValue()].claimed;
      }

      public OceanMonumentPieces.OceanMonumentPiece create(Direction p_72117_, OceanMonumentPieces.RoomDefinition p_72118_, Random p_72119_) {
         p_72118_.claimed = true;
         p_72118_.connections[Direction.UP.get3DDataValue()].claimed = true;
         return new OceanMonumentPieces.OceanMonumentDoubleYRoom(p_72117_, p_72118_);
      }
   }

   static class FitDoubleYZRoom implements OceanMonumentPieces.MonumentRoomFitter {
      public boolean fits(OceanMonumentPieces.RoomDefinition p_72124_) {
         if (p_72124_.hasOpening[Direction.NORTH.get3DDataValue()] && !p_72124_.connections[Direction.NORTH.get3DDataValue()].claimed && p_72124_.hasOpening[Direction.UP.get3DDataValue()] && !p_72124_.connections[Direction.UP.get3DDataValue()].claimed) {
            OceanMonumentPieces.RoomDefinition oceanmonumentpieces$roomdefinition = p_72124_.connections[Direction.NORTH.get3DDataValue()];
            return oceanmonumentpieces$roomdefinition.hasOpening[Direction.UP.get3DDataValue()] && !oceanmonumentpieces$roomdefinition.connections[Direction.UP.get3DDataValue()].claimed;
         } else {
            return false;
         }
      }

      public OceanMonumentPieces.OceanMonumentPiece create(Direction p_72126_, OceanMonumentPieces.RoomDefinition p_72127_, Random p_72128_) {
         p_72127_.claimed = true;
         p_72127_.connections[Direction.NORTH.get3DDataValue()].claimed = true;
         p_72127_.connections[Direction.UP.get3DDataValue()].claimed = true;
         p_72127_.connections[Direction.NORTH.get3DDataValue()].connections[Direction.UP.get3DDataValue()].claimed = true;
         return new OceanMonumentPieces.OceanMonumentDoubleYZRoom(p_72126_, p_72127_);
      }
   }

   static class FitDoubleZRoom implements OceanMonumentPieces.MonumentRoomFitter {
      public boolean fits(OceanMonumentPieces.RoomDefinition p_72133_) {
         return p_72133_.hasOpening[Direction.NORTH.get3DDataValue()] && !p_72133_.connections[Direction.NORTH.get3DDataValue()].claimed;
      }

      public OceanMonumentPieces.OceanMonumentPiece create(Direction p_72135_, OceanMonumentPieces.RoomDefinition p_72136_, Random p_72137_) {
         OceanMonumentPieces.RoomDefinition oceanmonumentpieces$roomdefinition = p_72136_;
         if (!p_72136_.hasOpening[Direction.NORTH.get3DDataValue()] || p_72136_.connections[Direction.NORTH.get3DDataValue()].claimed) {
            oceanmonumentpieces$roomdefinition = p_72136_.connections[Direction.SOUTH.get3DDataValue()];
         }

         oceanmonumentpieces$roomdefinition.claimed = true;
         oceanmonumentpieces$roomdefinition.connections[Direction.NORTH.get3DDataValue()].claimed = true;
         return new OceanMonumentPieces.OceanMonumentDoubleZRoom(p_72135_, oceanmonumentpieces$roomdefinition);
      }
   }

   static class FitSimpleRoom implements OceanMonumentPieces.MonumentRoomFitter {
      public boolean fits(OceanMonumentPieces.RoomDefinition p_72142_) {
         return true;
      }

      public OceanMonumentPieces.OceanMonumentPiece create(Direction p_72144_, OceanMonumentPieces.RoomDefinition p_72145_, Random p_72146_) {
         p_72145_.claimed = true;
         return new OceanMonumentPieces.OceanMonumentSimpleRoom(p_72144_, p_72145_, p_72146_);
      }
   }

   static class FitSimpleTopRoom implements OceanMonumentPieces.MonumentRoomFitter {
      public boolean fits(OceanMonumentPieces.RoomDefinition p_72151_) {
         return !p_72151_.hasOpening[Direction.WEST.get3DDataValue()] && !p_72151_.hasOpening[Direction.EAST.get3DDataValue()] && !p_72151_.hasOpening[Direction.NORTH.get3DDataValue()] && !p_72151_.hasOpening[Direction.SOUTH.get3DDataValue()] && !p_72151_.hasOpening[Direction.UP.get3DDataValue()];
      }

      public OceanMonumentPieces.OceanMonumentPiece create(Direction p_72153_, OceanMonumentPieces.RoomDefinition p_72154_, Random p_72155_) {
         p_72154_.claimed = true;
         return new OceanMonumentPieces.OceanMonumentSimpleTopRoom(p_72153_, p_72154_);
      }
   }

   public static class MonumentBuilding extends OceanMonumentPieces.OceanMonumentPiece {
      private static final int WIDTH = 58;
      private static final int HEIGHT = 22;
      private static final int DEPTH = 58;
      public static final int BIOME_RANGE_CHECK = 29;
      private static final int TOP_POSITION = 61;
      private OceanMonumentPieces.RoomDefinition sourceRoom;
      private OceanMonumentPieces.RoomDefinition coreRoom;
      private final List<OceanMonumentPieces.OceanMonumentPiece> childPieces = Lists.newArrayList();

      public MonumentBuilding(Random p_72163_, int p_72164_, int p_72165_, Direction p_72166_) {
         super(StructurePieceType.OCEAN_MONUMENT_BUILDING, p_72166_, 0, makeBoundingBox(p_72164_, 39, p_72165_, p_72166_, 58, 23, 58));
         this.setOrientation(p_72166_);
         List<OceanMonumentPieces.RoomDefinition> list = this.generateRoomGraph(p_72163_);
         this.sourceRoom.claimed = true;
         this.childPieces.add(new OceanMonumentPieces.OceanMonumentEntryRoom(p_72166_, this.sourceRoom));
         this.childPieces.add(new OceanMonumentPieces.OceanMonumentCoreRoom(p_72166_, this.coreRoom));
         List<OceanMonumentPieces.MonumentRoomFitter> list1 = Lists.newArrayList();
         list1.add(new OceanMonumentPieces.FitDoubleXYRoom());
         list1.add(new OceanMonumentPieces.FitDoubleYZRoom());
         list1.add(new OceanMonumentPieces.FitDoubleZRoom());
         list1.add(new OceanMonumentPieces.FitDoubleXRoom());
         list1.add(new OceanMonumentPieces.FitDoubleYRoom());
         list1.add(new OceanMonumentPieces.FitSimpleTopRoom());
         list1.add(new OceanMonumentPieces.FitSimpleRoom());

         for(OceanMonumentPieces.RoomDefinition oceanmonumentpieces$roomdefinition : list) {
            if (!oceanmonumentpieces$roomdefinition.claimed && !oceanmonumentpieces$roomdefinition.isSpecial()) {
               for(OceanMonumentPieces.MonumentRoomFitter oceanmonumentpieces$monumentroomfitter : list1) {
                  if (oceanmonumentpieces$monumentroomfitter.fits(oceanmonumentpieces$roomdefinition)) {
                     this.childPieces.add(oceanmonumentpieces$monumentroomfitter.create(p_72166_, oceanmonumentpieces$roomdefinition, p_72163_));
                     break;
                  }
               }
            }
         }

         BlockPos blockpos = this.getWorldPos(9, 0, 22);

         for(OceanMonumentPieces.OceanMonumentPiece oceanmonumentpieces$oceanmonumentpiece : this.childPieces) {
            oceanmonumentpieces$oceanmonumentpiece.getBoundingBox().move(blockpos);
         }

         BoundingBox boundingbox = BoundingBox.fromCorners(this.getWorldPos(1, 1, 1), this.getWorldPos(23, 8, 21));
         BoundingBox boundingbox1 = BoundingBox.fromCorners(this.getWorldPos(34, 1, 1), this.getWorldPos(56, 8, 21));
         BoundingBox boundingbox2 = BoundingBox.fromCorners(this.getWorldPos(22, 13, 22), this.getWorldPos(35, 17, 35));
         int i = p_72163_.nextInt();
         this.childPieces.add(new OceanMonumentPieces.OceanMonumentWingRoom(p_72166_, boundingbox, i++));
         this.childPieces.add(new OceanMonumentPieces.OceanMonumentWingRoom(p_72166_, boundingbox1, i++));
         this.childPieces.add(new OceanMonumentPieces.OceanMonumentPenthouse(p_72166_, boundingbox2));
      }

      public MonumentBuilding(CompoundTag p_192265_) {
         super(StructurePieceType.OCEAN_MONUMENT_BUILDING, p_192265_);
      }

      private List<OceanMonumentPieces.RoomDefinition> generateRoomGraph(Random p_72180_) {
         OceanMonumentPieces.RoomDefinition[] aoceanmonumentpieces$roomdefinition = new OceanMonumentPieces.RoomDefinition[75];

         for(int i = 0; i < 5; ++i) {
            for(int j = 0; j < 4; ++j) {
               int k = 0;
               int l = getRoomIndex(i, 0, j);
               aoceanmonumentpieces$roomdefinition[l] = new OceanMonumentPieces.RoomDefinition(l);
            }
         }

         for(int i2 = 0; i2 < 5; ++i2) {
            for(int l2 = 0; l2 < 4; ++l2) {
               int k3 = 1;
               int j4 = getRoomIndex(i2, 1, l2);
               aoceanmonumentpieces$roomdefinition[j4] = new OceanMonumentPieces.RoomDefinition(j4);
            }
         }

         for(int j2 = 1; j2 < 4; ++j2) {
            for(int i3 = 0; i3 < 2; ++i3) {
               int l3 = 2;
               int k4 = getRoomIndex(j2, 2, i3);
               aoceanmonumentpieces$roomdefinition[k4] = new OceanMonumentPieces.RoomDefinition(k4);
            }
         }

         this.sourceRoom = aoceanmonumentpieces$roomdefinition[GRIDROOM_SOURCE_INDEX];

         for(int k2 = 0; k2 < 5; ++k2) {
            for(int j3 = 0; j3 < 5; ++j3) {
               for(int i4 = 0; i4 < 3; ++i4) {
                  int l4 = getRoomIndex(k2, i4, j3);
                  if (aoceanmonumentpieces$roomdefinition[l4] != null) {
                     for(Direction direction : Direction.values()) {
                        int i1 = k2 + direction.getStepX();
                        int j1 = i4 + direction.getStepY();
                        int k1 = j3 + direction.getStepZ();
                        if (i1 >= 0 && i1 < 5 && k1 >= 0 && k1 < 5 && j1 >= 0 && j1 < 3) {
                           int l1 = getRoomIndex(i1, j1, k1);
                           if (aoceanmonumentpieces$roomdefinition[l1] != null) {
                              if (k1 == j3) {
                                 aoceanmonumentpieces$roomdefinition[l4].setConnection(direction, aoceanmonumentpieces$roomdefinition[l1]);
                              } else {
                                 aoceanmonumentpieces$roomdefinition[l4].setConnection(direction.getOpposite(), aoceanmonumentpieces$roomdefinition[l1]);
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         OceanMonumentPieces.RoomDefinition oceanmonumentpieces$roomdefinition = new OceanMonumentPieces.RoomDefinition(1003);
         OceanMonumentPieces.RoomDefinition oceanmonumentpieces$roomdefinition1 = new OceanMonumentPieces.RoomDefinition(1001);
         OceanMonumentPieces.RoomDefinition oceanmonumentpieces$roomdefinition2 = new OceanMonumentPieces.RoomDefinition(1002);
         aoceanmonumentpieces$roomdefinition[GRIDROOM_TOP_CONNECT_INDEX].setConnection(Direction.UP, oceanmonumentpieces$roomdefinition);
         aoceanmonumentpieces$roomdefinition[GRIDROOM_LEFTWING_CONNECT_INDEX].setConnection(Direction.SOUTH, oceanmonumentpieces$roomdefinition1);
         aoceanmonumentpieces$roomdefinition[GRIDROOM_RIGHTWING_CONNECT_INDEX].setConnection(Direction.SOUTH, oceanmonumentpieces$roomdefinition2);
         oceanmonumentpieces$roomdefinition.claimed = true;
         oceanmonumentpieces$roomdefinition1.claimed = true;
         oceanmonumentpieces$roomdefinition2.claimed = true;
         this.sourceRoom.isSource = true;
         this.coreRoom = aoceanmonumentpieces$roomdefinition[getRoomIndex(p_72180_.nextInt(4), 0, 2)];
         this.coreRoom.claimed = true;
         this.coreRoom.connections[Direction.EAST.get3DDataValue()].claimed = true;
         this.coreRoom.connections[Direction.NORTH.get3DDataValue()].claimed = true;
         this.coreRoom.connections[Direction.EAST.get3DDataValue()].connections[Direction.NORTH.get3DDataValue()].claimed = true;
         this.coreRoom.connections[Direction.UP.get3DDataValue()].claimed = true;
         this.coreRoom.connections[Direction.EAST.get3DDataValue()].connections[Direction.UP.get3DDataValue()].claimed = true;
         this.coreRoom.connections[Direction.NORTH.get3DDataValue()].connections[Direction.UP.get3DDataValue()].claimed = true;
         this.coreRoom.connections[Direction.EAST.get3DDataValue()].connections[Direction.NORTH.get3DDataValue()].connections[Direction.UP.get3DDataValue()].claimed = true;
         List<OceanMonumentPieces.RoomDefinition> list = Lists.newArrayList();

         for(OceanMonumentPieces.RoomDefinition oceanmonumentpieces$roomdefinition4 : aoceanmonumentpieces$roomdefinition) {
            if (oceanmonumentpieces$roomdefinition4 != null) {
               oceanmonumentpieces$roomdefinition4.updateOpenings();
               list.add(oceanmonumentpieces$roomdefinition4);
            }
         }

         oceanmonumentpieces$roomdefinition.updateOpenings();
         Collections.shuffle(list, p_72180_);
         int i5 = 1;

         for(OceanMonumentPieces.RoomDefinition oceanmonumentpieces$roomdefinition3 : list) {
            int j5 = 0;
            int k5 = 0;

            while(j5 < 2 && k5 < 5) {
               ++k5;
               int l5 = p_72180_.nextInt(6);
               if (oceanmonumentpieces$roomdefinition3.hasOpening[l5]) {
                  int i6 = Direction.from3DDataValue(l5).getOpposite().get3DDataValue();
                  oceanmonumentpieces$roomdefinition3.hasOpening[l5] = false;
                  oceanmonumentpieces$roomdefinition3.connections[l5].hasOpening[i6] = false;
                  if (oceanmonumentpieces$roomdefinition3.findSource(i5++) && oceanmonumentpieces$roomdefinition3.connections[l5].findSource(i5++)) {
                     ++j5;
                  } else {
                     oceanmonumentpieces$roomdefinition3.hasOpening[l5] = true;
                     oceanmonumentpieces$roomdefinition3.connections[l5].hasOpening[i6] = true;
                  }
               }
            }
         }

         list.add(oceanmonumentpieces$roomdefinition);
         list.add(oceanmonumentpieces$roomdefinition1);
         list.add(oceanmonumentpieces$roomdefinition2);
         return list;
      }

      public void postProcess(WorldGenLevel p_192267_, StructureFeatureManager p_192268_, ChunkGenerator p_192269_, Random p_192270_, BoundingBox p_192271_, ChunkPos p_192272_, BlockPos p_192273_) {
         int i = Math.max(p_192267_.getSeaLevel(), 64) - this.boundingBox.minY();
         this.generateWaterBox(p_192267_, p_192271_, 0, 0, 0, 58, i, 58);
         this.generateWing(false, 0, p_192267_, p_192270_, p_192271_);
         this.generateWing(true, 33, p_192267_, p_192270_, p_192271_);
         this.generateEntranceArchs(p_192267_, p_192270_, p_192271_);
         this.generateEntranceWall(p_192267_, p_192270_, p_192271_);
         this.generateRoofPiece(p_192267_, p_192270_, p_192271_);
         this.generateLowerWall(p_192267_, p_192270_, p_192271_);
         this.generateMiddleWall(p_192267_, p_192270_, p_192271_);
         this.generateUpperWall(p_192267_, p_192270_, p_192271_);

         for(int j = 0; j < 7; ++j) {
            int k = 0;

            while(k < 7) {
               if (k == 0 && j == 3) {
                  k = 6;
               }

               int l = j * 9;
               int i1 = k * 9;

               for(int j1 = 0; j1 < 4; ++j1) {
                  for(int k1 = 0; k1 < 4; ++k1) {
                     this.placeBlock(p_192267_, BASE_LIGHT, l + j1, 0, i1 + k1, p_192271_);
                     this.fillColumnDown(p_192267_, BASE_LIGHT, l + j1, -1, i1 + k1, p_192271_);
                  }
               }

               if (j != 0 && j != 6) {
                  k += 6;
               } else {
                  ++k;
               }
            }
         }

         for(int l1 = 0; l1 < 5; ++l1) {
            this.generateWaterBox(p_192267_, p_192271_, -1 - l1, 0 + l1 * 2, -1 - l1, -1 - l1, 23, 58 + l1);
            this.generateWaterBox(p_192267_, p_192271_, 58 + l1, 0 + l1 * 2, -1 - l1, 58 + l1, 23, 58 + l1);
            this.generateWaterBox(p_192267_, p_192271_, 0 - l1, 0 + l1 * 2, -1 - l1, 57 + l1, 23, -1 - l1);
            this.generateWaterBox(p_192267_, p_192271_, 0 - l1, 0 + l1 * 2, 58 + l1, 57 + l1, 23, 58 + l1);
         }

         for(OceanMonumentPieces.OceanMonumentPiece oceanmonumentpieces$oceanmonumentpiece : this.childPieces) {
            if (oceanmonumentpieces$oceanmonumentpiece.getBoundingBox().intersects(p_192271_)) {
               oceanmonumentpieces$oceanmonumentpiece.postProcess(p_192267_, p_192268_, p_192269_, p_192270_, p_192271_, p_192272_, p_192273_);
            }
         }

      }

      private void generateWing(boolean p_72182_, int p_72183_, WorldGenLevel p_72184_, Random p_72185_, BoundingBox p_72186_) {
         int i = 24;
         if (this.chunkIntersects(p_72186_, p_72183_, 0, p_72183_ + 23, 20)) {
            this.generateBox(p_72184_, p_72186_, p_72183_ + 0, 0, 0, p_72183_ + 24, 0, 20, BASE_GRAY, BASE_GRAY, false);
            this.generateWaterBox(p_72184_, p_72186_, p_72183_ + 0, 1, 0, p_72183_ + 24, 10, 20);

            for(int j = 0; j < 4; ++j) {
               this.generateBox(p_72184_, p_72186_, p_72183_ + j, j + 1, j, p_72183_ + j, j + 1, 20, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_72184_, p_72186_, p_72183_ + j + 7, j + 5, j + 7, p_72183_ + j + 7, j + 5, 20, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_72184_, p_72186_, p_72183_ + 17 - j, j + 5, j + 7, p_72183_ + 17 - j, j + 5, 20, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_72184_, p_72186_, p_72183_ + 24 - j, j + 1, j, p_72183_ + 24 - j, j + 1, 20, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_72184_, p_72186_, p_72183_ + j + 1, j + 1, j, p_72183_ + 23 - j, j + 1, j, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_72184_, p_72186_, p_72183_ + j + 8, j + 5, j + 7, p_72183_ + 16 - j, j + 5, j + 7, BASE_LIGHT, BASE_LIGHT, false);
            }

            this.generateBox(p_72184_, p_72186_, p_72183_ + 4, 4, 4, p_72183_ + 6, 4, 20, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_72184_, p_72186_, p_72183_ + 7, 4, 4, p_72183_ + 17, 4, 6, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_72184_, p_72186_, p_72183_ + 18, 4, 4, p_72183_ + 20, 4, 20, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_72184_, p_72186_, p_72183_ + 11, 8, 11, p_72183_ + 13, 8, 20, BASE_GRAY, BASE_GRAY, false);
            this.placeBlock(p_72184_, DOT_DECO_DATA, p_72183_ + 12, 9, 12, p_72186_);
            this.placeBlock(p_72184_, DOT_DECO_DATA, p_72183_ + 12, 9, 15, p_72186_);
            this.placeBlock(p_72184_, DOT_DECO_DATA, p_72183_ + 12, 9, 18, p_72186_);
            int j1 = p_72183_ + (p_72182_ ? 19 : 5);
            int k = p_72183_ + (p_72182_ ? 5 : 19);

            for(int l = 20; l >= 5; l -= 3) {
               this.placeBlock(p_72184_, DOT_DECO_DATA, j1, 5, l, p_72186_);
            }

            for(int k1 = 19; k1 >= 7; k1 -= 3) {
               this.placeBlock(p_72184_, DOT_DECO_DATA, k, 5, k1, p_72186_);
            }

            for(int l1 = 0; l1 < 4; ++l1) {
               int i1 = p_72182_ ? p_72183_ + 24 - (17 - l1 * 3) : p_72183_ + 17 - l1 * 3;
               this.placeBlock(p_72184_, DOT_DECO_DATA, i1, 5, 5, p_72186_);
            }

            this.placeBlock(p_72184_, DOT_DECO_DATA, k, 5, 5, p_72186_);
            this.generateBox(p_72184_, p_72186_, p_72183_ + 11, 1, 12, p_72183_ + 13, 7, 12, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_72184_, p_72186_, p_72183_ + 12, 1, 11, p_72183_ + 12, 7, 13, BASE_GRAY, BASE_GRAY, false);
         }

      }

      private void generateEntranceArchs(WorldGenLevel p_72176_, Random p_72177_, BoundingBox p_72178_) {
         if (this.chunkIntersects(p_72178_, 22, 5, 35, 17)) {
            this.generateWaterBox(p_72176_, p_72178_, 25, 0, 0, 32, 8, 20);

            for(int i = 0; i < 4; ++i) {
               this.generateBox(p_72176_, p_72178_, 24, 2, 5 + i * 4, 24, 4, 5 + i * 4, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_72176_, p_72178_, 22, 4, 5 + i * 4, 23, 4, 5 + i * 4, BASE_LIGHT, BASE_LIGHT, false);
               this.placeBlock(p_72176_, BASE_LIGHT, 25, 5, 5 + i * 4, p_72178_);
               this.placeBlock(p_72176_, BASE_LIGHT, 26, 6, 5 + i * 4, p_72178_);
               this.placeBlock(p_72176_, LAMP_BLOCK, 26, 5, 5 + i * 4, p_72178_);
               this.generateBox(p_72176_, p_72178_, 33, 2, 5 + i * 4, 33, 4, 5 + i * 4, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_72176_, p_72178_, 34, 4, 5 + i * 4, 35, 4, 5 + i * 4, BASE_LIGHT, BASE_LIGHT, false);
               this.placeBlock(p_72176_, BASE_LIGHT, 32, 5, 5 + i * 4, p_72178_);
               this.placeBlock(p_72176_, BASE_LIGHT, 31, 6, 5 + i * 4, p_72178_);
               this.placeBlock(p_72176_, LAMP_BLOCK, 31, 5, 5 + i * 4, p_72178_);
               this.generateBox(p_72176_, p_72178_, 27, 6, 5 + i * 4, 30, 6, 5 + i * 4, BASE_GRAY, BASE_GRAY, false);
            }
         }

      }

      private void generateEntranceWall(WorldGenLevel p_72188_, Random p_72189_, BoundingBox p_72190_) {
         if (this.chunkIntersects(p_72190_, 15, 20, 42, 21)) {
            this.generateBox(p_72188_, p_72190_, 15, 0, 21, 42, 0, 21, BASE_GRAY, BASE_GRAY, false);
            this.generateWaterBox(p_72188_, p_72190_, 26, 1, 21, 31, 3, 21);
            this.generateBox(p_72188_, p_72190_, 21, 12, 21, 36, 12, 21, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_72188_, p_72190_, 17, 11, 21, 40, 11, 21, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_72188_, p_72190_, 16, 10, 21, 41, 10, 21, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_72188_, p_72190_, 15, 7, 21, 42, 9, 21, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_72188_, p_72190_, 16, 6, 21, 41, 6, 21, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_72188_, p_72190_, 17, 5, 21, 40, 5, 21, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_72188_, p_72190_, 21, 4, 21, 36, 4, 21, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_72188_, p_72190_, 22, 3, 21, 26, 3, 21, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_72188_, p_72190_, 31, 3, 21, 35, 3, 21, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_72188_, p_72190_, 23, 2, 21, 25, 2, 21, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_72188_, p_72190_, 32, 2, 21, 34, 2, 21, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_72188_, p_72190_, 28, 4, 20, 29, 4, 21, BASE_LIGHT, BASE_LIGHT, false);
            this.placeBlock(p_72188_, BASE_LIGHT, 27, 3, 21, p_72190_);
            this.placeBlock(p_72188_, BASE_LIGHT, 30, 3, 21, p_72190_);
            this.placeBlock(p_72188_, BASE_LIGHT, 26, 2, 21, p_72190_);
            this.placeBlock(p_72188_, BASE_LIGHT, 31, 2, 21, p_72190_);
            this.placeBlock(p_72188_, BASE_LIGHT, 25, 1, 21, p_72190_);
            this.placeBlock(p_72188_, BASE_LIGHT, 32, 1, 21, p_72190_);

            for(int i = 0; i < 7; ++i) {
               this.placeBlock(p_72188_, BASE_BLACK, 28 - i, 6 + i, 21, p_72190_);
               this.placeBlock(p_72188_, BASE_BLACK, 29 + i, 6 + i, 21, p_72190_);
            }

            for(int j = 0; j < 4; ++j) {
               this.placeBlock(p_72188_, BASE_BLACK, 28 - j, 9 + j, 21, p_72190_);
               this.placeBlock(p_72188_, BASE_BLACK, 29 + j, 9 + j, 21, p_72190_);
            }

            this.placeBlock(p_72188_, BASE_BLACK, 28, 12, 21, p_72190_);
            this.placeBlock(p_72188_, BASE_BLACK, 29, 12, 21, p_72190_);

            for(int k = 0; k < 3; ++k) {
               this.placeBlock(p_72188_, BASE_BLACK, 22 - k * 2, 8, 21, p_72190_);
               this.placeBlock(p_72188_, BASE_BLACK, 22 - k * 2, 9, 21, p_72190_);
               this.placeBlock(p_72188_, BASE_BLACK, 35 + k * 2, 8, 21, p_72190_);
               this.placeBlock(p_72188_, BASE_BLACK, 35 + k * 2, 9, 21, p_72190_);
            }

            this.generateWaterBox(p_72188_, p_72190_, 15, 13, 21, 42, 15, 21);
            this.generateWaterBox(p_72188_, p_72190_, 15, 1, 21, 15, 6, 21);
            this.generateWaterBox(p_72188_, p_72190_, 16, 1, 21, 16, 5, 21);
            this.generateWaterBox(p_72188_, p_72190_, 17, 1, 21, 20, 4, 21);
            this.generateWaterBox(p_72188_, p_72190_, 21, 1, 21, 21, 3, 21);
            this.generateWaterBox(p_72188_, p_72190_, 22, 1, 21, 22, 2, 21);
            this.generateWaterBox(p_72188_, p_72190_, 23, 1, 21, 24, 1, 21);
            this.generateWaterBox(p_72188_, p_72190_, 42, 1, 21, 42, 6, 21);
            this.generateWaterBox(p_72188_, p_72190_, 41, 1, 21, 41, 5, 21);
            this.generateWaterBox(p_72188_, p_72190_, 37, 1, 21, 40, 4, 21);
            this.generateWaterBox(p_72188_, p_72190_, 36, 1, 21, 36, 3, 21);
            this.generateWaterBox(p_72188_, p_72190_, 33, 1, 21, 34, 1, 21);
            this.generateWaterBox(p_72188_, p_72190_, 35, 1, 21, 35, 2, 21);
         }

      }

      private void generateRoofPiece(WorldGenLevel p_72192_, Random p_72193_, BoundingBox p_72194_) {
         if (this.chunkIntersects(p_72194_, 21, 21, 36, 36)) {
            this.generateBox(p_72192_, p_72194_, 21, 0, 22, 36, 0, 36, BASE_GRAY, BASE_GRAY, false);
            this.generateWaterBox(p_72192_, p_72194_, 21, 1, 22, 36, 23, 36);

            for(int i = 0; i < 4; ++i) {
               this.generateBox(p_72192_, p_72194_, 21 + i, 13 + i, 21 + i, 36 - i, 13 + i, 21 + i, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_72192_, p_72194_, 21 + i, 13 + i, 36 - i, 36 - i, 13 + i, 36 - i, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_72192_, p_72194_, 21 + i, 13 + i, 22 + i, 21 + i, 13 + i, 35 - i, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_72192_, p_72194_, 36 - i, 13 + i, 22 + i, 36 - i, 13 + i, 35 - i, BASE_LIGHT, BASE_LIGHT, false);
            }

            this.generateBox(p_72192_, p_72194_, 25, 16, 25, 32, 16, 32, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_72192_, p_72194_, 25, 17, 25, 25, 19, 25, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_72192_, p_72194_, 32, 17, 25, 32, 19, 25, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_72192_, p_72194_, 25, 17, 32, 25, 19, 32, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_72192_, p_72194_, 32, 17, 32, 32, 19, 32, BASE_LIGHT, BASE_LIGHT, false);
            this.placeBlock(p_72192_, BASE_LIGHT, 26, 20, 26, p_72194_);
            this.placeBlock(p_72192_, BASE_LIGHT, 27, 21, 27, p_72194_);
            this.placeBlock(p_72192_, LAMP_BLOCK, 27, 20, 27, p_72194_);
            this.placeBlock(p_72192_, BASE_LIGHT, 26, 20, 31, p_72194_);
            this.placeBlock(p_72192_, BASE_LIGHT, 27, 21, 30, p_72194_);
            this.placeBlock(p_72192_, LAMP_BLOCK, 27, 20, 30, p_72194_);
            this.placeBlock(p_72192_, BASE_LIGHT, 31, 20, 31, p_72194_);
            this.placeBlock(p_72192_, BASE_LIGHT, 30, 21, 30, p_72194_);
            this.placeBlock(p_72192_, LAMP_BLOCK, 30, 20, 30, p_72194_);
            this.placeBlock(p_72192_, BASE_LIGHT, 31, 20, 26, p_72194_);
            this.placeBlock(p_72192_, BASE_LIGHT, 30, 21, 27, p_72194_);
            this.placeBlock(p_72192_, LAMP_BLOCK, 30, 20, 27, p_72194_);
            this.generateBox(p_72192_, p_72194_, 28, 21, 27, 29, 21, 27, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_72192_, p_72194_, 27, 21, 28, 27, 21, 29, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_72192_, p_72194_, 28, 21, 30, 29, 21, 30, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_72192_, p_72194_, 30, 21, 28, 30, 21, 29, BASE_GRAY, BASE_GRAY, false);
         }

      }

      private void generateLowerWall(WorldGenLevel p_72196_, Random p_72197_, BoundingBox p_72198_) {
         if (this.chunkIntersects(p_72198_, 0, 21, 6, 58)) {
            this.generateBox(p_72196_, p_72198_, 0, 0, 21, 6, 0, 57, BASE_GRAY, BASE_GRAY, false);
            this.generateWaterBox(p_72196_, p_72198_, 0, 1, 21, 6, 7, 57);
            this.generateBox(p_72196_, p_72198_, 4, 4, 21, 6, 4, 53, BASE_GRAY, BASE_GRAY, false);

            for(int i = 0; i < 4; ++i) {
               this.generateBox(p_72196_, p_72198_, i, i + 1, 21, i, i + 1, 57 - i, BASE_LIGHT, BASE_LIGHT, false);
            }

            for(int j = 23; j < 53; j += 3) {
               this.placeBlock(p_72196_, DOT_DECO_DATA, 5, 5, j, p_72198_);
            }

            this.placeBlock(p_72196_, DOT_DECO_DATA, 5, 5, 52, p_72198_);

            for(int k = 0; k < 4; ++k) {
               this.generateBox(p_72196_, p_72198_, k, k + 1, 21, k, k + 1, 57 - k, BASE_LIGHT, BASE_LIGHT, false);
            }

            this.generateBox(p_72196_, p_72198_, 4, 1, 52, 6, 3, 52, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_72196_, p_72198_, 5, 1, 51, 5, 3, 53, BASE_GRAY, BASE_GRAY, false);
         }

         if (this.chunkIntersects(p_72198_, 51, 21, 58, 58)) {
            this.generateBox(p_72196_, p_72198_, 51, 0, 21, 57, 0, 57, BASE_GRAY, BASE_GRAY, false);
            this.generateWaterBox(p_72196_, p_72198_, 51, 1, 21, 57, 7, 57);
            this.generateBox(p_72196_, p_72198_, 51, 4, 21, 53, 4, 53, BASE_GRAY, BASE_GRAY, false);

            for(int l = 0; l < 4; ++l) {
               this.generateBox(p_72196_, p_72198_, 57 - l, l + 1, 21, 57 - l, l + 1, 57 - l, BASE_LIGHT, BASE_LIGHT, false);
            }

            for(int i1 = 23; i1 < 53; i1 += 3) {
               this.placeBlock(p_72196_, DOT_DECO_DATA, 52, 5, i1, p_72198_);
            }

            this.placeBlock(p_72196_, DOT_DECO_DATA, 52, 5, 52, p_72198_);
            this.generateBox(p_72196_, p_72198_, 51, 1, 52, 53, 3, 52, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_72196_, p_72198_, 52, 1, 51, 52, 3, 53, BASE_GRAY, BASE_GRAY, false);
         }

         if (this.chunkIntersects(p_72198_, 0, 51, 57, 57)) {
            this.generateBox(p_72196_, p_72198_, 7, 0, 51, 50, 0, 57, BASE_GRAY, BASE_GRAY, false);
            this.generateWaterBox(p_72196_, p_72198_, 7, 1, 51, 50, 10, 57);

            for(int j1 = 0; j1 < 4; ++j1) {
               this.generateBox(p_72196_, p_72198_, j1 + 1, j1 + 1, 57 - j1, 56 - j1, j1 + 1, 57 - j1, BASE_LIGHT, BASE_LIGHT, false);
            }
         }

      }

      private void generateMiddleWall(WorldGenLevel p_72200_, Random p_72201_, BoundingBox p_72202_) {
         if (this.chunkIntersects(p_72202_, 7, 21, 13, 50)) {
            this.generateBox(p_72200_, p_72202_, 7, 0, 21, 13, 0, 50, BASE_GRAY, BASE_GRAY, false);
            this.generateWaterBox(p_72200_, p_72202_, 7, 1, 21, 13, 10, 50);
            this.generateBox(p_72200_, p_72202_, 11, 8, 21, 13, 8, 53, BASE_GRAY, BASE_GRAY, false);

            for(int i = 0; i < 4; ++i) {
               this.generateBox(p_72200_, p_72202_, i + 7, i + 5, 21, i + 7, i + 5, 54, BASE_LIGHT, BASE_LIGHT, false);
            }

            for(int j = 21; j <= 45; j += 3) {
               this.placeBlock(p_72200_, DOT_DECO_DATA, 12, 9, j, p_72202_);
            }
         }

         if (this.chunkIntersects(p_72202_, 44, 21, 50, 54)) {
            this.generateBox(p_72200_, p_72202_, 44, 0, 21, 50, 0, 50, BASE_GRAY, BASE_GRAY, false);
            this.generateWaterBox(p_72200_, p_72202_, 44, 1, 21, 50, 10, 50);
            this.generateBox(p_72200_, p_72202_, 44, 8, 21, 46, 8, 53, BASE_GRAY, BASE_GRAY, false);

            for(int k = 0; k < 4; ++k) {
               this.generateBox(p_72200_, p_72202_, 50 - k, k + 5, 21, 50 - k, k + 5, 54, BASE_LIGHT, BASE_LIGHT, false);
            }

            for(int l = 21; l <= 45; l += 3) {
               this.placeBlock(p_72200_, DOT_DECO_DATA, 45, 9, l, p_72202_);
            }
         }

         if (this.chunkIntersects(p_72202_, 8, 44, 49, 54)) {
            this.generateBox(p_72200_, p_72202_, 14, 0, 44, 43, 0, 50, BASE_GRAY, BASE_GRAY, false);
            this.generateWaterBox(p_72200_, p_72202_, 14, 1, 44, 43, 10, 50);

            for(int i1 = 12; i1 <= 45; i1 += 3) {
               this.placeBlock(p_72200_, DOT_DECO_DATA, i1, 9, 45, p_72202_);
               this.placeBlock(p_72200_, DOT_DECO_DATA, i1, 9, 52, p_72202_);
               if (i1 == 12 || i1 == 18 || i1 == 24 || i1 == 33 || i1 == 39 || i1 == 45) {
                  this.placeBlock(p_72200_, DOT_DECO_DATA, i1, 9, 47, p_72202_);
                  this.placeBlock(p_72200_, DOT_DECO_DATA, i1, 9, 50, p_72202_);
                  this.placeBlock(p_72200_, DOT_DECO_DATA, i1, 10, 45, p_72202_);
                  this.placeBlock(p_72200_, DOT_DECO_DATA, i1, 10, 46, p_72202_);
                  this.placeBlock(p_72200_, DOT_DECO_DATA, i1, 10, 51, p_72202_);
                  this.placeBlock(p_72200_, DOT_DECO_DATA, i1, 10, 52, p_72202_);
                  this.placeBlock(p_72200_, DOT_DECO_DATA, i1, 11, 47, p_72202_);
                  this.placeBlock(p_72200_, DOT_DECO_DATA, i1, 11, 50, p_72202_);
                  this.placeBlock(p_72200_, DOT_DECO_DATA, i1, 12, 48, p_72202_);
                  this.placeBlock(p_72200_, DOT_DECO_DATA, i1, 12, 49, p_72202_);
               }
            }

            for(int j1 = 0; j1 < 3; ++j1) {
               this.generateBox(p_72200_, p_72202_, 8 + j1, 5 + j1, 54, 49 - j1, 5 + j1, 54, BASE_GRAY, BASE_GRAY, false);
            }

            this.generateBox(p_72200_, p_72202_, 11, 8, 54, 46, 8, 54, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_72200_, p_72202_, 14, 8, 44, 43, 8, 53, BASE_GRAY, BASE_GRAY, false);
         }

      }

      private void generateUpperWall(WorldGenLevel p_72204_, Random p_72205_, BoundingBox p_72206_) {
         if (this.chunkIntersects(p_72206_, 14, 21, 20, 43)) {
            this.generateBox(p_72204_, p_72206_, 14, 0, 21, 20, 0, 43, BASE_GRAY, BASE_GRAY, false);
            this.generateWaterBox(p_72204_, p_72206_, 14, 1, 22, 20, 14, 43);
            this.generateBox(p_72204_, p_72206_, 18, 12, 22, 20, 12, 39, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_72204_, p_72206_, 18, 12, 21, 20, 12, 21, BASE_LIGHT, BASE_LIGHT, false);

            for(int i = 0; i < 4; ++i) {
               this.generateBox(p_72204_, p_72206_, i + 14, i + 9, 21, i + 14, i + 9, 43 - i, BASE_LIGHT, BASE_LIGHT, false);
            }

            for(int j = 23; j <= 39; j += 3) {
               this.placeBlock(p_72204_, DOT_DECO_DATA, 19, 13, j, p_72206_);
            }
         }

         if (this.chunkIntersects(p_72206_, 37, 21, 43, 43)) {
            this.generateBox(p_72204_, p_72206_, 37, 0, 21, 43, 0, 43, BASE_GRAY, BASE_GRAY, false);
            this.generateWaterBox(p_72204_, p_72206_, 37, 1, 22, 43, 14, 43);
            this.generateBox(p_72204_, p_72206_, 37, 12, 22, 39, 12, 39, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_72204_, p_72206_, 37, 12, 21, 39, 12, 21, BASE_LIGHT, BASE_LIGHT, false);

            for(int k = 0; k < 4; ++k) {
               this.generateBox(p_72204_, p_72206_, 43 - k, k + 9, 21, 43 - k, k + 9, 43 - k, BASE_LIGHT, BASE_LIGHT, false);
            }

            for(int l = 23; l <= 39; l += 3) {
               this.placeBlock(p_72204_, DOT_DECO_DATA, 38, 13, l, p_72206_);
            }
         }

         if (this.chunkIntersects(p_72206_, 15, 37, 42, 43)) {
            this.generateBox(p_72204_, p_72206_, 21, 0, 37, 36, 0, 43, BASE_GRAY, BASE_GRAY, false);
            this.generateWaterBox(p_72204_, p_72206_, 21, 1, 37, 36, 14, 43);
            this.generateBox(p_72204_, p_72206_, 21, 12, 37, 36, 12, 39, BASE_GRAY, BASE_GRAY, false);

            for(int i1 = 0; i1 < 4; ++i1) {
               this.generateBox(p_72204_, p_72206_, 15 + i1, i1 + 9, 43 - i1, 42 - i1, i1 + 9, 43 - i1, BASE_LIGHT, BASE_LIGHT, false);
            }

            for(int j1 = 21; j1 <= 36; j1 += 3) {
               this.placeBlock(p_72204_, DOT_DECO_DATA, j1, 13, 38, p_72206_);
            }
         }

      }
   }

   interface MonumentRoomFitter {
      boolean fits(OceanMonumentPieces.RoomDefinition p_72207_);

      OceanMonumentPieces.OceanMonumentPiece create(Direction p_72208_, OceanMonumentPieces.RoomDefinition p_72209_, Random p_72210_);
   }

   public static class OceanMonumentCoreRoom extends OceanMonumentPieces.OceanMonumentPiece {
      public OceanMonumentCoreRoom(Direction p_72215_, OceanMonumentPieces.RoomDefinition p_72216_) {
         super(StructurePieceType.OCEAN_MONUMENT_CORE_ROOM, 1, p_72215_, p_72216_, 2, 2, 2);
      }

      public OceanMonumentCoreRoom(CompoundTag p_192275_) {
         super(StructurePieceType.OCEAN_MONUMENT_CORE_ROOM, p_192275_);
      }

      public void postProcess(WorldGenLevel p_192277_, StructureFeatureManager p_192278_, ChunkGenerator p_192279_, Random p_192280_, BoundingBox p_192281_, ChunkPos p_192282_, BlockPos p_192283_) {
         this.generateBoxOnFillOnly(p_192277_, p_192281_, 1, 8, 0, 14, 8, 14, BASE_GRAY);
         int i = 7;
         BlockState blockstate = BASE_LIGHT;
         this.generateBox(p_192277_, p_192281_, 0, 7, 0, 0, 7, 15, blockstate, blockstate, false);
         this.generateBox(p_192277_, p_192281_, 15, 7, 0, 15, 7, 15, blockstate, blockstate, false);
         this.generateBox(p_192277_, p_192281_, 1, 7, 0, 15, 7, 0, blockstate, blockstate, false);
         this.generateBox(p_192277_, p_192281_, 1, 7, 15, 14, 7, 15, blockstate, blockstate, false);

         for(int k = 1; k <= 6; ++k) {
            blockstate = BASE_LIGHT;
            if (k == 2 || k == 6) {
               blockstate = BASE_GRAY;
            }

            for(int j = 0; j <= 15; j += 15) {
               this.generateBox(p_192277_, p_192281_, j, k, 0, j, k, 1, blockstate, blockstate, false);
               this.generateBox(p_192277_, p_192281_, j, k, 6, j, k, 9, blockstate, blockstate, false);
               this.generateBox(p_192277_, p_192281_, j, k, 14, j, k, 15, blockstate, blockstate, false);
            }

            this.generateBox(p_192277_, p_192281_, 1, k, 0, 1, k, 0, blockstate, blockstate, false);
            this.generateBox(p_192277_, p_192281_, 6, k, 0, 9, k, 0, blockstate, blockstate, false);
            this.generateBox(p_192277_, p_192281_, 14, k, 0, 14, k, 0, blockstate, blockstate, false);
            this.generateBox(p_192277_, p_192281_, 1, k, 15, 14, k, 15, blockstate, blockstate, false);
         }

         this.generateBox(p_192277_, p_192281_, 6, 3, 6, 9, 6, 9, BASE_BLACK, BASE_BLACK, false);
         this.generateBox(p_192277_, p_192281_, 7, 4, 7, 8, 5, 8, Blocks.GOLD_BLOCK.defaultBlockState(), Blocks.GOLD_BLOCK.defaultBlockState(), false);

         for(int l = 3; l <= 6; l += 3) {
            for(int i1 = 6; i1 <= 9; i1 += 3) {
               this.placeBlock(p_192277_, LAMP_BLOCK, i1, l, 6, p_192281_);
               this.placeBlock(p_192277_, LAMP_BLOCK, i1, l, 9, p_192281_);
            }
         }

         this.generateBox(p_192277_, p_192281_, 5, 1, 6, 5, 2, 6, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192277_, p_192281_, 5, 1, 9, 5, 2, 9, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192277_, p_192281_, 10, 1, 6, 10, 2, 6, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192277_, p_192281_, 10, 1, 9, 10, 2, 9, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192277_, p_192281_, 6, 1, 5, 6, 2, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192277_, p_192281_, 9, 1, 5, 9, 2, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192277_, p_192281_, 6, 1, 10, 6, 2, 10, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192277_, p_192281_, 9, 1, 10, 9, 2, 10, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192277_, p_192281_, 5, 2, 5, 5, 6, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192277_, p_192281_, 5, 2, 10, 5, 6, 10, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192277_, p_192281_, 10, 2, 5, 10, 6, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192277_, p_192281_, 10, 2, 10, 10, 6, 10, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192277_, p_192281_, 5, 7, 1, 5, 7, 6, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192277_, p_192281_, 10, 7, 1, 10, 7, 6, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192277_, p_192281_, 5, 7, 9, 5, 7, 14, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192277_, p_192281_, 10, 7, 9, 10, 7, 14, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192277_, p_192281_, 1, 7, 5, 6, 7, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192277_, p_192281_, 1, 7, 10, 6, 7, 10, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192277_, p_192281_, 9, 7, 5, 14, 7, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192277_, p_192281_, 9, 7, 10, 14, 7, 10, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192277_, p_192281_, 2, 1, 2, 2, 1, 3, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192277_, p_192281_, 3, 1, 2, 3, 1, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192277_, p_192281_, 13, 1, 2, 13, 1, 3, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192277_, p_192281_, 12, 1, 2, 12, 1, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192277_, p_192281_, 2, 1, 12, 2, 1, 13, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192277_, p_192281_, 3, 1, 13, 3, 1, 13, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192277_, p_192281_, 13, 1, 12, 13, 1, 13, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192277_, p_192281_, 12, 1, 13, 12, 1, 13, BASE_LIGHT, BASE_LIGHT, false);
      }
   }

   public static class OceanMonumentDoubleXRoom extends OceanMonumentPieces.OceanMonumentPiece {
      public OceanMonumentDoubleXRoom(Direction p_72229_, OceanMonumentPieces.RoomDefinition p_72230_) {
         super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_X_ROOM, 1, p_72229_, p_72230_, 2, 1, 1);
      }

      public OceanMonumentDoubleXRoom(CompoundTag p_192285_) {
         super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_X_ROOM, p_192285_);
      }

      public void postProcess(WorldGenLevel p_192287_, StructureFeatureManager p_192288_, ChunkGenerator p_192289_, Random p_192290_, BoundingBox p_192291_, ChunkPos p_192292_, BlockPos p_192293_) {
         OceanMonumentPieces.RoomDefinition oceanmonumentpieces$roomdefinition = this.roomDefinition.connections[Direction.EAST.get3DDataValue()];
         OceanMonumentPieces.RoomDefinition oceanmonumentpieces$roomdefinition1 = this.roomDefinition;
         if (this.roomDefinition.index / 25 > 0) {
            this.generateDefaultFloor(p_192287_, p_192291_, 8, 0, oceanmonumentpieces$roomdefinition.hasOpening[Direction.DOWN.get3DDataValue()]);
            this.generateDefaultFloor(p_192287_, p_192291_, 0, 0, oceanmonumentpieces$roomdefinition1.hasOpening[Direction.DOWN.get3DDataValue()]);
         }

         if (oceanmonumentpieces$roomdefinition1.connections[Direction.UP.get3DDataValue()] == null) {
            this.generateBoxOnFillOnly(p_192287_, p_192291_, 1, 4, 1, 7, 4, 6, BASE_GRAY);
         }

         if (oceanmonumentpieces$roomdefinition.connections[Direction.UP.get3DDataValue()] == null) {
            this.generateBoxOnFillOnly(p_192287_, p_192291_, 8, 4, 1, 14, 4, 6, BASE_GRAY);
         }

         this.generateBox(p_192287_, p_192291_, 0, 3, 0, 0, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192287_, p_192291_, 15, 3, 0, 15, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192287_, p_192291_, 1, 3, 0, 15, 3, 0, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192287_, p_192291_, 1, 3, 7, 14, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192287_, p_192291_, 0, 2, 0, 0, 2, 7, BASE_GRAY, BASE_GRAY, false);
         this.generateBox(p_192287_, p_192291_, 15, 2, 0, 15, 2, 7, BASE_GRAY, BASE_GRAY, false);
         this.generateBox(p_192287_, p_192291_, 1, 2, 0, 15, 2, 0, BASE_GRAY, BASE_GRAY, false);
         this.generateBox(p_192287_, p_192291_, 1, 2, 7, 14, 2, 7, BASE_GRAY, BASE_GRAY, false);
         this.generateBox(p_192287_, p_192291_, 0, 1, 0, 0, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192287_, p_192291_, 15, 1, 0, 15, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192287_, p_192291_, 1, 1, 0, 15, 1, 0, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192287_, p_192291_, 1, 1, 7, 14, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192287_, p_192291_, 5, 1, 0, 10, 1, 4, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192287_, p_192291_, 6, 2, 0, 9, 2, 3, BASE_GRAY, BASE_GRAY, false);
         this.generateBox(p_192287_, p_192291_, 5, 3, 0, 10, 3, 4, BASE_LIGHT, BASE_LIGHT, false);
         this.placeBlock(p_192287_, LAMP_BLOCK, 6, 2, 3, p_192291_);
         this.placeBlock(p_192287_, LAMP_BLOCK, 9, 2, 3, p_192291_);
         if (oceanmonumentpieces$roomdefinition1.hasOpening[Direction.SOUTH.get3DDataValue()]) {
            this.generateWaterBox(p_192287_, p_192291_, 3, 1, 0, 4, 2, 0);
         }

         if (oceanmonumentpieces$roomdefinition1.hasOpening[Direction.NORTH.get3DDataValue()]) {
            this.generateWaterBox(p_192287_, p_192291_, 3, 1, 7, 4, 2, 7);
         }

         if (oceanmonumentpieces$roomdefinition1.hasOpening[Direction.WEST.get3DDataValue()]) {
            this.generateWaterBox(p_192287_, p_192291_, 0, 1, 3, 0, 2, 4);
         }

         if (oceanmonumentpieces$roomdefinition.hasOpening[Direction.SOUTH.get3DDataValue()]) {
            this.generateWaterBox(p_192287_, p_192291_, 11, 1, 0, 12, 2, 0);
         }

         if (oceanmonumentpieces$roomdefinition.hasOpening[Direction.NORTH.get3DDataValue()]) {
            this.generateWaterBox(p_192287_, p_192291_, 11, 1, 7, 12, 2, 7);
         }

         if (oceanmonumentpieces$roomdefinition.hasOpening[Direction.EAST.get3DDataValue()]) {
            this.generateWaterBox(p_192287_, p_192291_, 15, 1, 3, 15, 2, 4);
         }

      }
   }

   public static class OceanMonumentDoubleXYRoom extends OceanMonumentPieces.OceanMonumentPiece {
      public OceanMonumentDoubleXYRoom(Direction p_72243_, OceanMonumentPieces.RoomDefinition p_72244_) {
         super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_XY_ROOM, 1, p_72243_, p_72244_, 2, 2, 1);
      }

      public OceanMonumentDoubleXYRoom(CompoundTag p_192295_) {
         super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_XY_ROOM, p_192295_);
      }

      public void postProcess(WorldGenLevel p_192297_, StructureFeatureManager p_192298_, ChunkGenerator p_192299_, Random p_192300_, BoundingBox p_192301_, ChunkPos p_192302_, BlockPos p_192303_) {
         OceanMonumentPieces.RoomDefinition oceanmonumentpieces$roomdefinition = this.roomDefinition.connections[Direction.EAST.get3DDataValue()];
         OceanMonumentPieces.RoomDefinition oceanmonumentpieces$roomdefinition1 = this.roomDefinition;
         OceanMonumentPieces.RoomDefinition oceanmonumentpieces$roomdefinition2 = oceanmonumentpieces$roomdefinition1.connections[Direction.UP.get3DDataValue()];
         OceanMonumentPieces.RoomDefinition oceanmonumentpieces$roomdefinition3 = oceanmonumentpieces$roomdefinition.connections[Direction.UP.get3DDataValue()];
         if (this.roomDefinition.index / 25 > 0) {
            this.generateDefaultFloor(p_192297_, p_192301_, 8, 0, oceanmonumentpieces$roomdefinition.hasOpening[Direction.DOWN.get3DDataValue()]);
            this.generateDefaultFloor(p_192297_, p_192301_, 0, 0, oceanmonumentpieces$roomdefinition1.hasOpening[Direction.DOWN.get3DDataValue()]);
         }

         if (oceanmonumentpieces$roomdefinition2.connections[Direction.UP.get3DDataValue()] == null) {
            this.generateBoxOnFillOnly(p_192297_, p_192301_, 1, 8, 1, 7, 8, 6, BASE_GRAY);
         }

         if (oceanmonumentpieces$roomdefinition3.connections[Direction.UP.get3DDataValue()] == null) {
            this.generateBoxOnFillOnly(p_192297_, p_192301_, 8, 8, 1, 14, 8, 6, BASE_GRAY);
         }

         for(int i = 1; i <= 7; ++i) {
            BlockState blockstate = BASE_LIGHT;
            if (i == 2 || i == 6) {
               blockstate = BASE_GRAY;
            }

            this.generateBox(p_192297_, p_192301_, 0, i, 0, 0, i, 7, blockstate, blockstate, false);
            this.generateBox(p_192297_, p_192301_, 15, i, 0, 15, i, 7, blockstate, blockstate, false);
            this.generateBox(p_192297_, p_192301_, 1, i, 0, 15, i, 0, blockstate, blockstate, false);
            this.generateBox(p_192297_, p_192301_, 1, i, 7, 14, i, 7, blockstate, blockstate, false);
         }

         this.generateBox(p_192297_, p_192301_, 2, 1, 3, 2, 7, 4, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192297_, p_192301_, 3, 1, 2, 4, 7, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192297_, p_192301_, 3, 1, 5, 4, 7, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192297_, p_192301_, 13, 1, 3, 13, 7, 4, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192297_, p_192301_, 11, 1, 2, 12, 7, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192297_, p_192301_, 11, 1, 5, 12, 7, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192297_, p_192301_, 5, 1, 3, 5, 3, 4, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192297_, p_192301_, 10, 1, 3, 10, 3, 4, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192297_, p_192301_, 5, 7, 2, 10, 7, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192297_, p_192301_, 5, 5, 2, 5, 7, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192297_, p_192301_, 10, 5, 2, 10, 7, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192297_, p_192301_, 5, 5, 5, 5, 7, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192297_, p_192301_, 10, 5, 5, 10, 7, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.placeBlock(p_192297_, BASE_LIGHT, 6, 6, 2, p_192301_);
         this.placeBlock(p_192297_, BASE_LIGHT, 9, 6, 2, p_192301_);
         this.placeBlock(p_192297_, BASE_LIGHT, 6, 6, 5, p_192301_);
         this.placeBlock(p_192297_, BASE_LIGHT, 9, 6, 5, p_192301_);
         this.generateBox(p_192297_, p_192301_, 5, 4, 3, 6, 4, 4, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192297_, p_192301_, 9, 4, 3, 10, 4, 4, BASE_LIGHT, BASE_LIGHT, false);
         this.placeBlock(p_192297_, LAMP_BLOCK, 5, 4, 2, p_192301_);
         this.placeBlock(p_192297_, LAMP_BLOCK, 5, 4, 5, p_192301_);
         this.placeBlock(p_192297_, LAMP_BLOCK, 10, 4, 2, p_192301_);
         this.placeBlock(p_192297_, LAMP_BLOCK, 10, 4, 5, p_192301_);
         if (oceanmonumentpieces$roomdefinition1.hasOpening[Direction.SOUTH.get3DDataValue()]) {
            this.generateWaterBox(p_192297_, p_192301_, 3, 1, 0, 4, 2, 0);
         }

         if (oceanmonumentpieces$roomdefinition1.hasOpening[Direction.NORTH.get3DDataValue()]) {
            this.generateWaterBox(p_192297_, p_192301_, 3, 1, 7, 4, 2, 7);
         }

         if (oceanmonumentpieces$roomdefinition1.hasOpening[Direction.WEST.get3DDataValue()]) {
            this.generateWaterBox(p_192297_, p_192301_, 0, 1, 3, 0, 2, 4);
         }

         if (oceanmonumentpieces$roomdefinition.hasOpening[Direction.SOUTH.get3DDataValue()]) {
            this.generateWaterBox(p_192297_, p_192301_, 11, 1, 0, 12, 2, 0);
         }

         if (oceanmonumentpieces$roomdefinition.hasOpening[Direction.NORTH.get3DDataValue()]) {
            this.generateWaterBox(p_192297_, p_192301_, 11, 1, 7, 12, 2, 7);
         }

         if (oceanmonumentpieces$roomdefinition.hasOpening[Direction.EAST.get3DDataValue()]) {
            this.generateWaterBox(p_192297_, p_192301_, 15, 1, 3, 15, 2, 4);
         }

         if (oceanmonumentpieces$roomdefinition2.hasOpening[Direction.SOUTH.get3DDataValue()]) {
            this.generateWaterBox(p_192297_, p_192301_, 3, 5, 0, 4, 6, 0);
         }

         if (oceanmonumentpieces$roomdefinition2.hasOpening[Direction.NORTH.get3DDataValue()]) {
            this.generateWaterBox(p_192297_, p_192301_, 3, 5, 7, 4, 6, 7);
         }

         if (oceanmonumentpieces$roomdefinition2.hasOpening[Direction.WEST.get3DDataValue()]) {
            this.generateWaterBox(p_192297_, p_192301_, 0, 5, 3, 0, 6, 4);
         }

         if (oceanmonumentpieces$roomdefinition3.hasOpening[Direction.SOUTH.get3DDataValue()]) {
            this.generateWaterBox(p_192297_, p_192301_, 11, 5, 0, 12, 6, 0);
         }

         if (oceanmonumentpieces$roomdefinition3.hasOpening[Direction.NORTH.get3DDataValue()]) {
            this.generateWaterBox(p_192297_, p_192301_, 11, 5, 7, 12, 6, 7);
         }

         if (oceanmonumentpieces$roomdefinition3.hasOpening[Direction.EAST.get3DDataValue()]) {
            this.generateWaterBox(p_192297_, p_192301_, 15, 5, 3, 15, 6, 4);
         }

      }
   }

   public static class OceanMonumentDoubleYRoom extends OceanMonumentPieces.OceanMonumentPiece {
      public OceanMonumentDoubleYRoom(Direction p_72257_, OceanMonumentPieces.RoomDefinition p_72258_) {
         super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_Y_ROOM, 1, p_72257_, p_72258_, 1, 2, 1);
      }

      public OceanMonumentDoubleYRoom(CompoundTag p_192305_) {
         super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_Y_ROOM, p_192305_);
      }

      public void postProcess(WorldGenLevel p_192307_, StructureFeatureManager p_192308_, ChunkGenerator p_192309_, Random p_192310_, BoundingBox p_192311_, ChunkPos p_192312_, BlockPos p_192313_) {
         if (this.roomDefinition.index / 25 > 0) {
            this.generateDefaultFloor(p_192307_, p_192311_, 0, 0, this.roomDefinition.hasOpening[Direction.DOWN.get3DDataValue()]);
         }

         OceanMonumentPieces.RoomDefinition oceanmonumentpieces$roomdefinition = this.roomDefinition.connections[Direction.UP.get3DDataValue()];
         if (oceanmonumentpieces$roomdefinition.connections[Direction.UP.get3DDataValue()] == null) {
            this.generateBoxOnFillOnly(p_192307_, p_192311_, 1, 8, 1, 6, 8, 6, BASE_GRAY);
         }

         this.generateBox(p_192307_, p_192311_, 0, 4, 0, 0, 4, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192307_, p_192311_, 7, 4, 0, 7, 4, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192307_, p_192311_, 1, 4, 0, 6, 4, 0, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192307_, p_192311_, 1, 4, 7, 6, 4, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192307_, p_192311_, 2, 4, 1, 2, 4, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192307_, p_192311_, 1, 4, 2, 1, 4, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192307_, p_192311_, 5, 4, 1, 5, 4, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192307_, p_192311_, 6, 4, 2, 6, 4, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192307_, p_192311_, 2, 4, 5, 2, 4, 6, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192307_, p_192311_, 1, 4, 5, 1, 4, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192307_, p_192311_, 5, 4, 5, 5, 4, 6, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192307_, p_192311_, 6, 4, 5, 6, 4, 5, BASE_LIGHT, BASE_LIGHT, false);
         OceanMonumentPieces.RoomDefinition oceanmonumentpieces$roomdefinition1 = this.roomDefinition;

         for(int i = 1; i <= 5; i += 4) {
            int j = 0;
            if (oceanmonumentpieces$roomdefinition1.hasOpening[Direction.SOUTH.get3DDataValue()]) {
               this.generateBox(p_192307_, p_192311_, 2, i, j, 2, i + 2, j, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_192307_, p_192311_, 5, i, j, 5, i + 2, j, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_192307_, p_192311_, 3, i + 2, j, 4, i + 2, j, BASE_LIGHT, BASE_LIGHT, false);
            } else {
               this.generateBox(p_192307_, p_192311_, 0, i, j, 7, i + 2, j, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_192307_, p_192311_, 0, i + 1, j, 7, i + 1, j, BASE_GRAY, BASE_GRAY, false);
            }

            j = 7;
            if (oceanmonumentpieces$roomdefinition1.hasOpening[Direction.NORTH.get3DDataValue()]) {
               this.generateBox(p_192307_, p_192311_, 2, i, j, 2, i + 2, j, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_192307_, p_192311_, 5, i, j, 5, i + 2, j, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_192307_, p_192311_, 3, i + 2, j, 4, i + 2, j, BASE_LIGHT, BASE_LIGHT, false);
            } else {
               this.generateBox(p_192307_, p_192311_, 0, i, j, 7, i + 2, j, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_192307_, p_192311_, 0, i + 1, j, 7, i + 1, j, BASE_GRAY, BASE_GRAY, false);
            }

            int k = 0;
            if (oceanmonumentpieces$roomdefinition1.hasOpening[Direction.WEST.get3DDataValue()]) {
               this.generateBox(p_192307_, p_192311_, k, i, 2, k, i + 2, 2, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_192307_, p_192311_, k, i, 5, k, i + 2, 5, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_192307_, p_192311_, k, i + 2, 3, k, i + 2, 4, BASE_LIGHT, BASE_LIGHT, false);
            } else {
               this.generateBox(p_192307_, p_192311_, k, i, 0, k, i + 2, 7, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_192307_, p_192311_, k, i + 1, 0, k, i + 1, 7, BASE_GRAY, BASE_GRAY, false);
            }

            k = 7;
            if (oceanmonumentpieces$roomdefinition1.hasOpening[Direction.EAST.get3DDataValue()]) {
               this.generateBox(p_192307_, p_192311_, k, i, 2, k, i + 2, 2, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_192307_, p_192311_, k, i, 5, k, i + 2, 5, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_192307_, p_192311_, k, i + 2, 3, k, i + 2, 4, BASE_LIGHT, BASE_LIGHT, false);
            } else {
               this.generateBox(p_192307_, p_192311_, k, i, 0, k, i + 2, 7, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_192307_, p_192311_, k, i + 1, 0, k, i + 1, 7, BASE_GRAY, BASE_GRAY, false);
            }

            oceanmonumentpieces$roomdefinition1 = oceanmonumentpieces$roomdefinition;
         }

      }
   }

   public static class OceanMonumentDoubleYZRoom extends OceanMonumentPieces.OceanMonumentPiece {
      public OceanMonumentDoubleYZRoom(Direction p_72271_, OceanMonumentPieces.RoomDefinition p_72272_) {
         super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_YZ_ROOM, 1, p_72271_, p_72272_, 1, 2, 2);
      }

      public OceanMonumentDoubleYZRoom(CompoundTag p_192315_) {
         super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_YZ_ROOM, p_192315_);
      }

      public void postProcess(WorldGenLevel p_192317_, StructureFeatureManager p_192318_, ChunkGenerator p_192319_, Random p_192320_, BoundingBox p_192321_, ChunkPos p_192322_, BlockPos p_192323_) {
         OceanMonumentPieces.RoomDefinition oceanmonumentpieces$roomdefinition = this.roomDefinition.connections[Direction.NORTH.get3DDataValue()];
         OceanMonumentPieces.RoomDefinition oceanmonumentpieces$roomdefinition1 = this.roomDefinition;
         OceanMonumentPieces.RoomDefinition oceanmonumentpieces$roomdefinition2 = oceanmonumentpieces$roomdefinition.connections[Direction.UP.get3DDataValue()];
         OceanMonumentPieces.RoomDefinition oceanmonumentpieces$roomdefinition3 = oceanmonumentpieces$roomdefinition1.connections[Direction.UP.get3DDataValue()];
         if (this.roomDefinition.index / 25 > 0) {
            this.generateDefaultFloor(p_192317_, p_192321_, 0, 8, oceanmonumentpieces$roomdefinition.hasOpening[Direction.DOWN.get3DDataValue()]);
            this.generateDefaultFloor(p_192317_, p_192321_, 0, 0, oceanmonumentpieces$roomdefinition1.hasOpening[Direction.DOWN.get3DDataValue()]);
         }

         if (oceanmonumentpieces$roomdefinition3.connections[Direction.UP.get3DDataValue()] == null) {
            this.generateBoxOnFillOnly(p_192317_, p_192321_, 1, 8, 1, 6, 8, 7, BASE_GRAY);
         }

         if (oceanmonumentpieces$roomdefinition2.connections[Direction.UP.get3DDataValue()] == null) {
            this.generateBoxOnFillOnly(p_192317_, p_192321_, 1, 8, 8, 6, 8, 14, BASE_GRAY);
         }

         for(int i = 1; i <= 7; ++i) {
            BlockState blockstate = BASE_LIGHT;
            if (i == 2 || i == 6) {
               blockstate = BASE_GRAY;
            }

            this.generateBox(p_192317_, p_192321_, 0, i, 0, 0, i, 15, blockstate, blockstate, false);
            this.generateBox(p_192317_, p_192321_, 7, i, 0, 7, i, 15, blockstate, blockstate, false);
            this.generateBox(p_192317_, p_192321_, 1, i, 0, 6, i, 0, blockstate, blockstate, false);
            this.generateBox(p_192317_, p_192321_, 1, i, 15, 6, i, 15, blockstate, blockstate, false);
         }

         for(int j = 1; j <= 7; ++j) {
            BlockState blockstate1 = BASE_BLACK;
            if (j == 2 || j == 6) {
               blockstate1 = LAMP_BLOCK;
            }

            this.generateBox(p_192317_, p_192321_, 3, j, 7, 4, j, 8, blockstate1, blockstate1, false);
         }

         if (oceanmonumentpieces$roomdefinition1.hasOpening[Direction.SOUTH.get3DDataValue()]) {
            this.generateWaterBox(p_192317_, p_192321_, 3, 1, 0, 4, 2, 0);
         }

         if (oceanmonumentpieces$roomdefinition1.hasOpening[Direction.EAST.get3DDataValue()]) {
            this.generateWaterBox(p_192317_, p_192321_, 7, 1, 3, 7, 2, 4);
         }

         if (oceanmonumentpieces$roomdefinition1.hasOpening[Direction.WEST.get3DDataValue()]) {
            this.generateWaterBox(p_192317_, p_192321_, 0, 1, 3, 0, 2, 4);
         }

         if (oceanmonumentpieces$roomdefinition.hasOpening[Direction.NORTH.get3DDataValue()]) {
            this.generateWaterBox(p_192317_, p_192321_, 3, 1, 15, 4, 2, 15);
         }

         if (oceanmonumentpieces$roomdefinition.hasOpening[Direction.WEST.get3DDataValue()]) {
            this.generateWaterBox(p_192317_, p_192321_, 0, 1, 11, 0, 2, 12);
         }

         if (oceanmonumentpieces$roomdefinition.hasOpening[Direction.EAST.get3DDataValue()]) {
            this.generateWaterBox(p_192317_, p_192321_, 7, 1, 11, 7, 2, 12);
         }

         if (oceanmonumentpieces$roomdefinition3.hasOpening[Direction.SOUTH.get3DDataValue()]) {
            this.generateWaterBox(p_192317_, p_192321_, 3, 5, 0, 4, 6, 0);
         }

         if (oceanmonumentpieces$roomdefinition3.hasOpening[Direction.EAST.get3DDataValue()]) {
            this.generateWaterBox(p_192317_, p_192321_, 7, 5, 3, 7, 6, 4);
            this.generateBox(p_192317_, p_192321_, 5, 4, 2, 6, 4, 5, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_192317_, p_192321_, 6, 1, 2, 6, 3, 2, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_192317_, p_192321_, 6, 1, 5, 6, 3, 5, BASE_LIGHT, BASE_LIGHT, false);
         }

         if (oceanmonumentpieces$roomdefinition3.hasOpening[Direction.WEST.get3DDataValue()]) {
            this.generateWaterBox(p_192317_, p_192321_, 0, 5, 3, 0, 6, 4);
            this.generateBox(p_192317_, p_192321_, 1, 4, 2, 2, 4, 5, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_192317_, p_192321_, 1, 1, 2, 1, 3, 2, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_192317_, p_192321_, 1, 1, 5, 1, 3, 5, BASE_LIGHT, BASE_LIGHT, false);
         }

         if (oceanmonumentpieces$roomdefinition2.hasOpening[Direction.NORTH.get3DDataValue()]) {
            this.generateWaterBox(p_192317_, p_192321_, 3, 5, 15, 4, 6, 15);
         }

         if (oceanmonumentpieces$roomdefinition2.hasOpening[Direction.WEST.get3DDataValue()]) {
            this.generateWaterBox(p_192317_, p_192321_, 0, 5, 11, 0, 6, 12);
            this.generateBox(p_192317_, p_192321_, 1, 4, 10, 2, 4, 13, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_192317_, p_192321_, 1, 1, 10, 1, 3, 10, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_192317_, p_192321_, 1, 1, 13, 1, 3, 13, BASE_LIGHT, BASE_LIGHT, false);
         }

         if (oceanmonumentpieces$roomdefinition2.hasOpening[Direction.EAST.get3DDataValue()]) {
            this.generateWaterBox(p_192317_, p_192321_, 7, 5, 11, 7, 6, 12);
            this.generateBox(p_192317_, p_192321_, 5, 4, 10, 6, 4, 13, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_192317_, p_192321_, 6, 1, 10, 6, 3, 10, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_192317_, p_192321_, 6, 1, 13, 6, 3, 13, BASE_LIGHT, BASE_LIGHT, false);
         }

      }
   }

   public static class OceanMonumentDoubleZRoom extends OceanMonumentPieces.OceanMonumentPiece {
      public OceanMonumentDoubleZRoom(Direction p_72285_, OceanMonumentPieces.RoomDefinition p_72286_) {
         super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_Z_ROOM, 1, p_72285_, p_72286_, 1, 1, 2);
      }

      public OceanMonumentDoubleZRoom(CompoundTag p_192325_) {
         super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_Z_ROOM, p_192325_);
      }

      public void postProcess(WorldGenLevel p_192327_, StructureFeatureManager p_192328_, ChunkGenerator p_192329_, Random p_192330_, BoundingBox p_192331_, ChunkPos p_192332_, BlockPos p_192333_) {
         OceanMonumentPieces.RoomDefinition oceanmonumentpieces$roomdefinition = this.roomDefinition.connections[Direction.NORTH.get3DDataValue()];
         OceanMonumentPieces.RoomDefinition oceanmonumentpieces$roomdefinition1 = this.roomDefinition;
         if (this.roomDefinition.index / 25 > 0) {
            this.generateDefaultFloor(p_192327_, p_192331_, 0, 8, oceanmonumentpieces$roomdefinition.hasOpening[Direction.DOWN.get3DDataValue()]);
            this.generateDefaultFloor(p_192327_, p_192331_, 0, 0, oceanmonumentpieces$roomdefinition1.hasOpening[Direction.DOWN.get3DDataValue()]);
         }

         if (oceanmonumentpieces$roomdefinition1.connections[Direction.UP.get3DDataValue()] == null) {
            this.generateBoxOnFillOnly(p_192327_, p_192331_, 1, 4, 1, 6, 4, 7, BASE_GRAY);
         }

         if (oceanmonumentpieces$roomdefinition.connections[Direction.UP.get3DDataValue()] == null) {
            this.generateBoxOnFillOnly(p_192327_, p_192331_, 1, 4, 8, 6, 4, 14, BASE_GRAY);
         }

         this.generateBox(p_192327_, p_192331_, 0, 3, 0, 0, 3, 15, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192327_, p_192331_, 7, 3, 0, 7, 3, 15, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192327_, p_192331_, 1, 3, 0, 7, 3, 0, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192327_, p_192331_, 1, 3, 15, 6, 3, 15, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192327_, p_192331_, 0, 2, 0, 0, 2, 15, BASE_GRAY, BASE_GRAY, false);
         this.generateBox(p_192327_, p_192331_, 7, 2, 0, 7, 2, 15, BASE_GRAY, BASE_GRAY, false);
         this.generateBox(p_192327_, p_192331_, 1, 2, 0, 7, 2, 0, BASE_GRAY, BASE_GRAY, false);
         this.generateBox(p_192327_, p_192331_, 1, 2, 15, 6, 2, 15, BASE_GRAY, BASE_GRAY, false);
         this.generateBox(p_192327_, p_192331_, 0, 1, 0, 0, 1, 15, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192327_, p_192331_, 7, 1, 0, 7, 1, 15, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192327_, p_192331_, 1, 1, 0, 7, 1, 0, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192327_, p_192331_, 1, 1, 15, 6, 1, 15, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192327_, p_192331_, 1, 1, 1, 1, 1, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192327_, p_192331_, 6, 1, 1, 6, 1, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192327_, p_192331_, 1, 3, 1, 1, 3, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192327_, p_192331_, 6, 3, 1, 6, 3, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192327_, p_192331_, 1, 1, 13, 1, 1, 14, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192327_, p_192331_, 6, 1, 13, 6, 1, 14, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192327_, p_192331_, 1, 3, 13, 1, 3, 14, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192327_, p_192331_, 6, 3, 13, 6, 3, 14, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192327_, p_192331_, 2, 1, 6, 2, 3, 6, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192327_, p_192331_, 5, 1, 6, 5, 3, 6, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192327_, p_192331_, 2, 1, 9, 2, 3, 9, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192327_, p_192331_, 5, 1, 9, 5, 3, 9, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192327_, p_192331_, 3, 2, 6, 4, 2, 6, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192327_, p_192331_, 3, 2, 9, 4, 2, 9, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192327_, p_192331_, 2, 2, 7, 2, 2, 8, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192327_, p_192331_, 5, 2, 7, 5, 2, 8, BASE_LIGHT, BASE_LIGHT, false);
         this.placeBlock(p_192327_, LAMP_BLOCK, 2, 2, 5, p_192331_);
         this.placeBlock(p_192327_, LAMP_BLOCK, 5, 2, 5, p_192331_);
         this.placeBlock(p_192327_, LAMP_BLOCK, 2, 2, 10, p_192331_);
         this.placeBlock(p_192327_, LAMP_BLOCK, 5, 2, 10, p_192331_);
         this.placeBlock(p_192327_, BASE_LIGHT, 2, 3, 5, p_192331_);
         this.placeBlock(p_192327_, BASE_LIGHT, 5, 3, 5, p_192331_);
         this.placeBlock(p_192327_, BASE_LIGHT, 2, 3, 10, p_192331_);
         this.placeBlock(p_192327_, BASE_LIGHT, 5, 3, 10, p_192331_);
         if (oceanmonumentpieces$roomdefinition1.hasOpening[Direction.SOUTH.get3DDataValue()]) {
            this.generateWaterBox(p_192327_, p_192331_, 3, 1, 0, 4, 2, 0);
         }

         if (oceanmonumentpieces$roomdefinition1.hasOpening[Direction.EAST.get3DDataValue()]) {
            this.generateWaterBox(p_192327_, p_192331_, 7, 1, 3, 7, 2, 4);
         }

         if (oceanmonumentpieces$roomdefinition1.hasOpening[Direction.WEST.get3DDataValue()]) {
            this.generateWaterBox(p_192327_, p_192331_, 0, 1, 3, 0, 2, 4);
         }

         if (oceanmonumentpieces$roomdefinition.hasOpening[Direction.NORTH.get3DDataValue()]) {
            this.generateWaterBox(p_192327_, p_192331_, 3, 1, 15, 4, 2, 15);
         }

         if (oceanmonumentpieces$roomdefinition.hasOpening[Direction.WEST.get3DDataValue()]) {
            this.generateWaterBox(p_192327_, p_192331_, 0, 1, 11, 0, 2, 12);
         }

         if (oceanmonumentpieces$roomdefinition.hasOpening[Direction.EAST.get3DDataValue()]) {
            this.generateWaterBox(p_192327_, p_192331_, 7, 1, 11, 7, 2, 12);
         }

      }
   }

   public static class OceanMonumentEntryRoom extends OceanMonumentPieces.OceanMonumentPiece {
      public OceanMonumentEntryRoom(Direction p_72299_, OceanMonumentPieces.RoomDefinition p_72300_) {
         super(StructurePieceType.OCEAN_MONUMENT_ENTRY_ROOM, 1, p_72299_, p_72300_, 1, 1, 1);
      }

      public OceanMonumentEntryRoom(CompoundTag p_192335_) {
         super(StructurePieceType.OCEAN_MONUMENT_ENTRY_ROOM, p_192335_);
      }

      public void postProcess(WorldGenLevel p_192337_, StructureFeatureManager p_192338_, ChunkGenerator p_192339_, Random p_192340_, BoundingBox p_192341_, ChunkPos p_192342_, BlockPos p_192343_) {
         this.generateBox(p_192337_, p_192341_, 0, 3, 0, 2, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192337_, p_192341_, 5, 3, 0, 7, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192337_, p_192341_, 0, 2, 0, 1, 2, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192337_, p_192341_, 6, 2, 0, 7, 2, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192337_, p_192341_, 0, 1, 0, 0, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192337_, p_192341_, 7, 1, 0, 7, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192337_, p_192341_, 0, 1, 7, 7, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192337_, p_192341_, 1, 1, 0, 2, 3, 0, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192337_, p_192341_, 5, 1, 0, 6, 3, 0, BASE_LIGHT, BASE_LIGHT, false);
         if (this.roomDefinition.hasOpening[Direction.NORTH.get3DDataValue()]) {
            this.generateWaterBox(p_192337_, p_192341_, 3, 1, 7, 4, 2, 7);
         }

         if (this.roomDefinition.hasOpening[Direction.WEST.get3DDataValue()]) {
            this.generateWaterBox(p_192337_, p_192341_, 0, 1, 3, 1, 2, 4);
         }

         if (this.roomDefinition.hasOpening[Direction.EAST.get3DDataValue()]) {
            this.generateWaterBox(p_192337_, p_192341_, 6, 1, 3, 7, 2, 4);
         }

      }
   }

   public static class OceanMonumentPenthouse extends OceanMonumentPieces.OceanMonumentPiece {
      public OceanMonumentPenthouse(Direction p_72313_, BoundingBox p_72314_) {
         super(StructurePieceType.OCEAN_MONUMENT_PENTHOUSE, p_72313_, 1, p_72314_);
      }

      public OceanMonumentPenthouse(CompoundTag p_192345_) {
         super(StructurePieceType.OCEAN_MONUMENT_PENTHOUSE, p_192345_);
      }

      public void postProcess(WorldGenLevel p_192347_, StructureFeatureManager p_192348_, ChunkGenerator p_192349_, Random p_192350_, BoundingBox p_192351_, ChunkPos p_192352_, BlockPos p_192353_) {
         this.generateBox(p_192347_, p_192351_, 2, -1, 2, 11, -1, 11, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192347_, p_192351_, 0, -1, 0, 1, -1, 11, BASE_GRAY, BASE_GRAY, false);
         this.generateBox(p_192347_, p_192351_, 12, -1, 0, 13, -1, 11, BASE_GRAY, BASE_GRAY, false);
         this.generateBox(p_192347_, p_192351_, 2, -1, 0, 11, -1, 1, BASE_GRAY, BASE_GRAY, false);
         this.generateBox(p_192347_, p_192351_, 2, -1, 12, 11, -1, 13, BASE_GRAY, BASE_GRAY, false);
         this.generateBox(p_192347_, p_192351_, 0, 0, 0, 0, 0, 13, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192347_, p_192351_, 13, 0, 0, 13, 0, 13, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192347_, p_192351_, 1, 0, 0, 12, 0, 0, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192347_, p_192351_, 1, 0, 13, 12, 0, 13, BASE_LIGHT, BASE_LIGHT, false);

         for(int i = 2; i <= 11; i += 3) {
            this.placeBlock(p_192347_, LAMP_BLOCK, 0, 0, i, p_192351_);
            this.placeBlock(p_192347_, LAMP_BLOCK, 13, 0, i, p_192351_);
            this.placeBlock(p_192347_, LAMP_BLOCK, i, 0, 0, p_192351_);
         }

         this.generateBox(p_192347_, p_192351_, 2, 0, 3, 4, 0, 9, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192347_, p_192351_, 9, 0, 3, 11, 0, 9, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192347_, p_192351_, 4, 0, 9, 9, 0, 11, BASE_LIGHT, BASE_LIGHT, false);
         this.placeBlock(p_192347_, BASE_LIGHT, 5, 0, 8, p_192351_);
         this.placeBlock(p_192347_, BASE_LIGHT, 8, 0, 8, p_192351_);
         this.placeBlock(p_192347_, BASE_LIGHT, 10, 0, 10, p_192351_);
         this.placeBlock(p_192347_, BASE_LIGHT, 3, 0, 10, p_192351_);
         this.generateBox(p_192347_, p_192351_, 3, 0, 3, 3, 0, 7, BASE_BLACK, BASE_BLACK, false);
         this.generateBox(p_192347_, p_192351_, 10, 0, 3, 10, 0, 7, BASE_BLACK, BASE_BLACK, false);
         this.generateBox(p_192347_, p_192351_, 6, 0, 10, 7, 0, 10, BASE_BLACK, BASE_BLACK, false);
         int l = 3;

         for(int j = 0; j < 2; ++j) {
            for(int k = 2; k <= 8; k += 3) {
               this.generateBox(p_192347_, p_192351_, l, 0, k, l, 2, k, BASE_LIGHT, BASE_LIGHT, false);
            }

            l = 10;
         }

         this.generateBox(p_192347_, p_192351_, 5, 0, 10, 5, 2, 10, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192347_, p_192351_, 8, 0, 10, 8, 2, 10, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192347_, p_192351_, 6, -1, 7, 7, -1, 8, BASE_BLACK, BASE_BLACK, false);
         this.generateWaterBox(p_192347_, p_192351_, 6, -1, 3, 7, -1, 4);
         this.spawnElder(p_192347_, p_192351_, 6, 1, 6);
      }
   }

   protected abstract static class OceanMonumentPiece extends StructurePiece {
      protected static final BlockState BASE_GRAY = Blocks.PRISMARINE.defaultBlockState();
      protected static final BlockState BASE_LIGHT = Blocks.PRISMARINE_BRICKS.defaultBlockState();
      protected static final BlockState BASE_BLACK = Blocks.DARK_PRISMARINE.defaultBlockState();
      protected static final BlockState DOT_DECO_DATA = BASE_LIGHT;
      protected static final BlockState LAMP_BLOCK = Blocks.SEA_LANTERN.defaultBlockState();
      protected static final boolean DO_FILL = true;
      protected static final BlockState FILL_BLOCK = Blocks.WATER.defaultBlockState();
      protected static final Set<Block> FILL_KEEP = ImmutableSet.<Block>builder().add(Blocks.ICE).add(Blocks.PACKED_ICE).add(Blocks.BLUE_ICE).add(FILL_BLOCK.getBlock()).build();
      protected static final int GRIDROOM_WIDTH = 8;
      protected static final int GRIDROOM_DEPTH = 8;
      protected static final int GRIDROOM_HEIGHT = 4;
      protected static final int GRID_WIDTH = 5;
      protected static final int GRID_DEPTH = 5;
      protected static final int GRID_HEIGHT = 3;
      protected static final int GRID_FLOOR_COUNT = 25;
      protected static final int GRID_SIZE = 75;
      protected static final int GRIDROOM_SOURCE_INDEX = getRoomIndex(2, 0, 0);
      protected static final int GRIDROOM_TOP_CONNECT_INDEX = getRoomIndex(2, 2, 0);
      protected static final int GRIDROOM_LEFTWING_CONNECT_INDEX = getRoomIndex(0, 1, 0);
      protected static final int GRIDROOM_RIGHTWING_CONNECT_INDEX = getRoomIndex(4, 1, 0);
      protected static final int LEFTWING_INDEX = 1001;
      protected static final int RIGHTWING_INDEX = 1002;
      protected static final int PENTHOUSE_INDEX = 1003;
      protected OceanMonumentPieces.RoomDefinition roomDefinition;

      protected static int getRoomIndex(int p_72394_, int p_72395_, int p_72396_) {
         return p_72395_ * 25 + p_72396_ * 5 + p_72394_;
      }

      public OceanMonumentPiece(StructurePieceType p_209902_, Direction p_209903_, int p_209904_, BoundingBox p_209905_) {
         super(p_209902_, p_209904_, p_209905_);
         this.setOrientation(p_209903_);
      }

      protected OceanMonumentPiece(StructurePieceType p_209894_, int p_209895_, Direction p_209896_, OceanMonumentPieces.RoomDefinition p_209897_, int p_209898_, int p_209899_, int p_209900_) {
         super(p_209894_, p_209895_, makeBoundingBox(p_209896_, p_209897_, p_209898_, p_209899_, p_209900_));
         this.setOrientation(p_209896_);
         this.roomDefinition = p_209897_;
      }

      private static BoundingBox makeBoundingBox(Direction p_163041_, OceanMonumentPieces.RoomDefinition p_163042_, int p_163043_, int p_163044_, int p_163045_) {
         int i = p_163042_.index;
         int j = i % 5;
         int k = i / 5 % 5;
         int l = i / 25;
         BoundingBox boundingbox = makeBoundingBox(0, 0, 0, p_163041_, p_163043_ * 8, p_163044_ * 4, p_163045_ * 8);
         switch(p_163041_) {
         case NORTH:
            boundingbox.move(j * 8, l * 4, -(k + p_163045_) * 8 + 1);
            break;
         case SOUTH:
            boundingbox.move(j * 8, l * 4, k * 8);
            break;
         case WEST:
            boundingbox.move(-(k + p_163045_) * 8 + 1, l * 4, j * 8);
            break;
         case EAST:
         default:
            boundingbox.move(k * 8, l * 4, j * 8);
         }

         return boundingbox;
      }

      public OceanMonumentPiece(StructurePieceType p_209907_, CompoundTag p_209908_) {
         super(p_209907_, p_209908_);
      }

      protected void addAdditionalSaveData(StructurePieceSerializationContext p_192355_, CompoundTag p_192356_) {
      }

      protected void generateWaterBox(WorldGenLevel p_72361_, BoundingBox p_72362_, int p_72363_, int p_72364_, int p_72365_, int p_72366_, int p_72367_, int p_72368_) {
         for(int i = p_72364_; i <= p_72367_; ++i) {
            for(int j = p_72363_; j <= p_72366_; ++j) {
               for(int k = p_72365_; k <= p_72368_; ++k) {
                  BlockState blockstate = this.getBlock(p_72361_, j, i, k, p_72362_);
                  if (!FILL_KEEP.contains(blockstate.getBlock())) {
                     if (this.getWorldY(i) >= p_72361_.getSeaLevel() && blockstate != FILL_BLOCK) {
                        this.placeBlock(p_72361_, Blocks.AIR.defaultBlockState(), j, i, k, p_72362_);
                     } else {
                        this.placeBlock(p_72361_, FILL_BLOCK, j, i, k, p_72362_);
                     }
                  }
               }
            }
         }

      }

      protected void generateDefaultFloor(WorldGenLevel p_72380_, BoundingBox p_72381_, int p_72382_, int p_72383_, boolean p_72384_) {
         if (p_72384_) {
            this.generateBox(p_72380_, p_72381_, p_72382_ + 0, 0, p_72383_ + 0, p_72382_ + 2, 0, p_72383_ + 8 - 1, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_72380_, p_72381_, p_72382_ + 5, 0, p_72383_ + 0, p_72382_ + 8 - 1, 0, p_72383_ + 8 - 1, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_72380_, p_72381_, p_72382_ + 3, 0, p_72383_ + 0, p_72382_ + 4, 0, p_72383_ + 2, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_72380_, p_72381_, p_72382_ + 3, 0, p_72383_ + 5, p_72382_ + 4, 0, p_72383_ + 8 - 1, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_72380_, p_72381_, p_72382_ + 3, 0, p_72383_ + 2, p_72382_ + 4, 0, p_72383_ + 2, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_72380_, p_72381_, p_72382_ + 3, 0, p_72383_ + 5, p_72382_ + 4, 0, p_72383_ + 5, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_72380_, p_72381_, p_72382_ + 2, 0, p_72383_ + 3, p_72382_ + 2, 0, p_72383_ + 4, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_72380_, p_72381_, p_72382_ + 5, 0, p_72383_ + 3, p_72382_ + 5, 0, p_72383_ + 4, BASE_LIGHT, BASE_LIGHT, false);
         } else {
            this.generateBox(p_72380_, p_72381_, p_72382_ + 0, 0, p_72383_ + 0, p_72382_ + 8 - 1, 0, p_72383_ + 8 - 1, BASE_GRAY, BASE_GRAY, false);
         }

      }

      protected void generateBoxOnFillOnly(WorldGenLevel p_72370_, BoundingBox p_72371_, int p_72372_, int p_72373_, int p_72374_, int p_72375_, int p_72376_, int p_72377_, BlockState p_72378_) {
         for(int i = p_72373_; i <= p_72376_; ++i) {
            for(int j = p_72372_; j <= p_72375_; ++j) {
               for(int k = p_72374_; k <= p_72377_; ++k) {
                  if (this.getBlock(p_72370_, j, i, k, p_72371_) == FILL_BLOCK) {
                     this.placeBlock(p_72370_, p_72378_, j, i, k, p_72371_);
                  }
               }
            }
         }

      }

      protected boolean chunkIntersects(BoundingBox p_72386_, int p_72387_, int p_72388_, int p_72389_, int p_72390_) {
         int i = this.getWorldX(p_72387_, p_72388_);
         int j = this.getWorldZ(p_72387_, p_72388_);
         int k = this.getWorldX(p_72389_, p_72390_);
         int l = this.getWorldZ(p_72389_, p_72390_);
         return p_72386_.intersects(Math.min(i, k), Math.min(j, l), Math.max(i, k), Math.max(j, l));
      }

      protected boolean spawnElder(WorldGenLevel p_72355_, BoundingBox p_72356_, int p_72357_, int p_72358_, int p_72359_) {
         BlockPos blockpos = this.getWorldPos(p_72357_, p_72358_, p_72359_);
         if (p_72356_.isInside(blockpos)) {
            ElderGuardian elderguardian = EntityType.ELDER_GUARDIAN.create(p_72355_.getLevel());
            elderguardian.heal(elderguardian.getMaxHealth());
            elderguardian.moveTo((double)blockpos.getX() + 0.5D, (double)blockpos.getY(), (double)blockpos.getZ() + 0.5D, 0.0F, 0.0F);
            elderguardian.finalizeSpawn(p_72355_, p_72355_.getCurrentDifficultyAt(elderguardian.blockPosition()), MobSpawnType.STRUCTURE, (SpawnGroupData)null, (CompoundTag)null);
            p_72355_.addFreshEntityWithPassengers(elderguardian);
            return true;
         } else {
            return false;
         }
      }
   }

   public static class OceanMonumentSimpleRoom extends OceanMonumentPieces.OceanMonumentPiece {
      private int mainDesign;

      public OceanMonumentSimpleRoom(Direction p_72402_, OceanMonumentPieces.RoomDefinition p_72403_, Random p_72404_) {
         super(StructurePieceType.OCEAN_MONUMENT_SIMPLE_ROOM, 1, p_72402_, p_72403_, 1, 1, 1);
         this.mainDesign = p_72404_.nextInt(3);
      }

      public OceanMonumentSimpleRoom(CompoundTag p_192358_) {
         super(StructurePieceType.OCEAN_MONUMENT_SIMPLE_ROOM, p_192358_);
      }

      public void postProcess(WorldGenLevel p_192360_, StructureFeatureManager p_192361_, ChunkGenerator p_192362_, Random p_192363_, BoundingBox p_192364_, ChunkPos p_192365_, BlockPos p_192366_) {
         if (this.roomDefinition.index / 25 > 0) {
            this.generateDefaultFloor(p_192360_, p_192364_, 0, 0, this.roomDefinition.hasOpening[Direction.DOWN.get3DDataValue()]);
         }

         if (this.roomDefinition.connections[Direction.UP.get3DDataValue()] == null) {
            this.generateBoxOnFillOnly(p_192360_, p_192364_, 1, 4, 1, 6, 4, 6, BASE_GRAY);
         }

         boolean flag = this.mainDesign != 0 && p_192363_.nextBoolean() && !this.roomDefinition.hasOpening[Direction.DOWN.get3DDataValue()] && !this.roomDefinition.hasOpening[Direction.UP.get3DDataValue()] && this.roomDefinition.countOpenings() > 1;
         if (this.mainDesign == 0) {
            this.generateBox(p_192360_, p_192364_, 0, 1, 0, 2, 1, 2, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_192360_, p_192364_, 0, 3, 0, 2, 3, 2, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_192360_, p_192364_, 0, 2, 0, 0, 2, 2, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_192360_, p_192364_, 1, 2, 0, 2, 2, 0, BASE_GRAY, BASE_GRAY, false);
            this.placeBlock(p_192360_, LAMP_BLOCK, 1, 2, 1, p_192364_);
            this.generateBox(p_192360_, p_192364_, 5, 1, 0, 7, 1, 2, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_192360_, p_192364_, 5, 3, 0, 7, 3, 2, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_192360_, p_192364_, 7, 2, 0, 7, 2, 2, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_192360_, p_192364_, 5, 2, 0, 6, 2, 0, BASE_GRAY, BASE_GRAY, false);
            this.placeBlock(p_192360_, LAMP_BLOCK, 6, 2, 1, p_192364_);
            this.generateBox(p_192360_, p_192364_, 0, 1, 5, 2, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_192360_, p_192364_, 0, 3, 5, 2, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_192360_, p_192364_, 0, 2, 5, 0, 2, 7, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_192360_, p_192364_, 1, 2, 7, 2, 2, 7, BASE_GRAY, BASE_GRAY, false);
            this.placeBlock(p_192360_, LAMP_BLOCK, 1, 2, 6, p_192364_);
            this.generateBox(p_192360_, p_192364_, 5, 1, 5, 7, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_192360_, p_192364_, 5, 3, 5, 7, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_192360_, p_192364_, 7, 2, 5, 7, 2, 7, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_192360_, p_192364_, 5, 2, 7, 6, 2, 7, BASE_GRAY, BASE_GRAY, false);
            this.placeBlock(p_192360_, LAMP_BLOCK, 6, 2, 6, p_192364_);
            if (this.roomDefinition.hasOpening[Direction.SOUTH.get3DDataValue()]) {
               this.generateBox(p_192360_, p_192364_, 3, 3, 0, 4, 3, 0, BASE_LIGHT, BASE_LIGHT, false);
            } else {
               this.generateBox(p_192360_, p_192364_, 3, 3, 0, 4, 3, 1, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_192360_, p_192364_, 3, 2, 0, 4, 2, 0, BASE_GRAY, BASE_GRAY, false);
               this.generateBox(p_192360_, p_192364_, 3, 1, 0, 4, 1, 1, BASE_LIGHT, BASE_LIGHT, false);
            }

            if (this.roomDefinition.hasOpening[Direction.NORTH.get3DDataValue()]) {
               this.generateBox(p_192360_, p_192364_, 3, 3, 7, 4, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
            } else {
               this.generateBox(p_192360_, p_192364_, 3, 3, 6, 4, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_192360_, p_192364_, 3, 2, 7, 4, 2, 7, BASE_GRAY, BASE_GRAY, false);
               this.generateBox(p_192360_, p_192364_, 3, 1, 6, 4, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
            }

            if (this.roomDefinition.hasOpening[Direction.WEST.get3DDataValue()]) {
               this.generateBox(p_192360_, p_192364_, 0, 3, 3, 0, 3, 4, BASE_LIGHT, BASE_LIGHT, false);
            } else {
               this.generateBox(p_192360_, p_192364_, 0, 3, 3, 1, 3, 4, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_192360_, p_192364_, 0, 2, 3, 0, 2, 4, BASE_GRAY, BASE_GRAY, false);
               this.generateBox(p_192360_, p_192364_, 0, 1, 3, 1, 1, 4, BASE_LIGHT, BASE_LIGHT, false);
            }

            if (this.roomDefinition.hasOpening[Direction.EAST.get3DDataValue()]) {
               this.generateBox(p_192360_, p_192364_, 7, 3, 3, 7, 3, 4, BASE_LIGHT, BASE_LIGHT, false);
            } else {
               this.generateBox(p_192360_, p_192364_, 6, 3, 3, 7, 3, 4, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_192360_, p_192364_, 7, 2, 3, 7, 2, 4, BASE_GRAY, BASE_GRAY, false);
               this.generateBox(p_192360_, p_192364_, 6, 1, 3, 7, 1, 4, BASE_LIGHT, BASE_LIGHT, false);
            }
         } else if (this.mainDesign == 1) {
            this.generateBox(p_192360_, p_192364_, 2, 1, 2, 2, 3, 2, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_192360_, p_192364_, 2, 1, 5, 2, 3, 5, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_192360_, p_192364_, 5, 1, 5, 5, 3, 5, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_192360_, p_192364_, 5, 1, 2, 5, 3, 2, BASE_LIGHT, BASE_LIGHT, false);
            this.placeBlock(p_192360_, LAMP_BLOCK, 2, 2, 2, p_192364_);
            this.placeBlock(p_192360_, LAMP_BLOCK, 2, 2, 5, p_192364_);
            this.placeBlock(p_192360_, LAMP_BLOCK, 5, 2, 5, p_192364_);
            this.placeBlock(p_192360_, LAMP_BLOCK, 5, 2, 2, p_192364_);
            this.generateBox(p_192360_, p_192364_, 0, 1, 0, 1, 3, 0, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_192360_, p_192364_, 0, 1, 1, 0, 3, 1, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_192360_, p_192364_, 0, 1, 7, 1, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_192360_, p_192364_, 0, 1, 6, 0, 3, 6, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_192360_, p_192364_, 6, 1, 7, 7, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_192360_, p_192364_, 7, 1, 6, 7, 3, 6, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_192360_, p_192364_, 6, 1, 0, 7, 3, 0, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_192360_, p_192364_, 7, 1, 1, 7, 3, 1, BASE_LIGHT, BASE_LIGHT, false);
            this.placeBlock(p_192360_, BASE_GRAY, 1, 2, 0, p_192364_);
            this.placeBlock(p_192360_, BASE_GRAY, 0, 2, 1, p_192364_);
            this.placeBlock(p_192360_, BASE_GRAY, 1, 2, 7, p_192364_);
            this.placeBlock(p_192360_, BASE_GRAY, 0, 2, 6, p_192364_);
            this.placeBlock(p_192360_, BASE_GRAY, 6, 2, 7, p_192364_);
            this.placeBlock(p_192360_, BASE_GRAY, 7, 2, 6, p_192364_);
            this.placeBlock(p_192360_, BASE_GRAY, 6, 2, 0, p_192364_);
            this.placeBlock(p_192360_, BASE_GRAY, 7, 2, 1, p_192364_);
            if (!this.roomDefinition.hasOpening[Direction.SOUTH.get3DDataValue()]) {
               this.generateBox(p_192360_, p_192364_, 1, 3, 0, 6, 3, 0, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_192360_, p_192364_, 1, 2, 0, 6, 2, 0, BASE_GRAY, BASE_GRAY, false);
               this.generateBox(p_192360_, p_192364_, 1, 1, 0, 6, 1, 0, BASE_LIGHT, BASE_LIGHT, false);
            }

            if (!this.roomDefinition.hasOpening[Direction.NORTH.get3DDataValue()]) {
               this.generateBox(p_192360_, p_192364_, 1, 3, 7, 6, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_192360_, p_192364_, 1, 2, 7, 6, 2, 7, BASE_GRAY, BASE_GRAY, false);
               this.generateBox(p_192360_, p_192364_, 1, 1, 7, 6, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
            }

            if (!this.roomDefinition.hasOpening[Direction.WEST.get3DDataValue()]) {
               this.generateBox(p_192360_, p_192364_, 0, 3, 1, 0, 3, 6, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_192360_, p_192364_, 0, 2, 1, 0, 2, 6, BASE_GRAY, BASE_GRAY, false);
               this.generateBox(p_192360_, p_192364_, 0, 1, 1, 0, 1, 6, BASE_LIGHT, BASE_LIGHT, false);
            }

            if (!this.roomDefinition.hasOpening[Direction.EAST.get3DDataValue()]) {
               this.generateBox(p_192360_, p_192364_, 7, 3, 1, 7, 3, 6, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_192360_, p_192364_, 7, 2, 1, 7, 2, 6, BASE_GRAY, BASE_GRAY, false);
               this.generateBox(p_192360_, p_192364_, 7, 1, 1, 7, 1, 6, BASE_LIGHT, BASE_LIGHT, false);
            }
         } else if (this.mainDesign == 2) {
            this.generateBox(p_192360_, p_192364_, 0, 1, 0, 0, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_192360_, p_192364_, 7, 1, 0, 7, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_192360_, p_192364_, 1, 1, 0, 6, 1, 0, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_192360_, p_192364_, 1, 1, 7, 6, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_192360_, p_192364_, 0, 2, 0, 0, 2, 7, BASE_BLACK, BASE_BLACK, false);
            this.generateBox(p_192360_, p_192364_, 7, 2, 0, 7, 2, 7, BASE_BLACK, BASE_BLACK, false);
            this.generateBox(p_192360_, p_192364_, 1, 2, 0, 6, 2, 0, BASE_BLACK, BASE_BLACK, false);
            this.generateBox(p_192360_, p_192364_, 1, 2, 7, 6, 2, 7, BASE_BLACK, BASE_BLACK, false);
            this.generateBox(p_192360_, p_192364_, 0, 3, 0, 0, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_192360_, p_192364_, 7, 3, 0, 7, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_192360_, p_192364_, 1, 3, 0, 6, 3, 0, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_192360_, p_192364_, 1, 3, 7, 6, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_192360_, p_192364_, 0, 1, 3, 0, 2, 4, BASE_BLACK, BASE_BLACK, false);
            this.generateBox(p_192360_, p_192364_, 7, 1, 3, 7, 2, 4, BASE_BLACK, BASE_BLACK, false);
            this.generateBox(p_192360_, p_192364_, 3, 1, 0, 4, 2, 0, BASE_BLACK, BASE_BLACK, false);
            this.generateBox(p_192360_, p_192364_, 3, 1, 7, 4, 2, 7, BASE_BLACK, BASE_BLACK, false);
            if (this.roomDefinition.hasOpening[Direction.SOUTH.get3DDataValue()]) {
               this.generateWaterBox(p_192360_, p_192364_, 3, 1, 0, 4, 2, 0);
            }

            if (this.roomDefinition.hasOpening[Direction.NORTH.get3DDataValue()]) {
               this.generateWaterBox(p_192360_, p_192364_, 3, 1, 7, 4, 2, 7);
            }

            if (this.roomDefinition.hasOpening[Direction.WEST.get3DDataValue()]) {
               this.generateWaterBox(p_192360_, p_192364_, 0, 1, 3, 0, 2, 4);
            }

            if (this.roomDefinition.hasOpening[Direction.EAST.get3DDataValue()]) {
               this.generateWaterBox(p_192360_, p_192364_, 7, 1, 3, 7, 2, 4);
            }
         }

         if (flag) {
            this.generateBox(p_192360_, p_192364_, 3, 1, 3, 4, 1, 4, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_192360_, p_192364_, 3, 2, 3, 4, 2, 4, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_192360_, p_192364_, 3, 3, 3, 4, 3, 4, BASE_LIGHT, BASE_LIGHT, false);
         }

      }
   }

   public static class OceanMonumentSimpleTopRoom extends OceanMonumentPieces.OceanMonumentPiece {
      public OceanMonumentSimpleTopRoom(Direction p_72417_, OceanMonumentPieces.RoomDefinition p_72418_) {
         super(StructurePieceType.OCEAN_MONUMENT_SIMPLE_TOP_ROOM, 1, p_72417_, p_72418_, 1, 1, 1);
      }

      public OceanMonumentSimpleTopRoom(CompoundTag p_192368_) {
         super(StructurePieceType.OCEAN_MONUMENT_SIMPLE_TOP_ROOM, p_192368_);
      }

      public void postProcess(WorldGenLevel p_192370_, StructureFeatureManager p_192371_, ChunkGenerator p_192372_, Random p_192373_, BoundingBox p_192374_, ChunkPos p_192375_, BlockPos p_192376_) {
         if (this.roomDefinition.index / 25 > 0) {
            this.generateDefaultFloor(p_192370_, p_192374_, 0, 0, this.roomDefinition.hasOpening[Direction.DOWN.get3DDataValue()]);
         }

         if (this.roomDefinition.connections[Direction.UP.get3DDataValue()] == null) {
            this.generateBoxOnFillOnly(p_192370_, p_192374_, 1, 4, 1, 6, 4, 6, BASE_GRAY);
         }

         for(int i = 1; i <= 6; ++i) {
            for(int j = 1; j <= 6; ++j) {
               if (p_192373_.nextInt(3) != 0) {
                  int k = 2 + (p_192373_.nextInt(4) == 0 ? 0 : 1);
                  BlockState blockstate = Blocks.WET_SPONGE.defaultBlockState();
                  this.generateBox(p_192370_, p_192374_, i, k, j, i, 3, j, blockstate, blockstate, false);
               }
            }
         }

         this.generateBox(p_192370_, p_192374_, 0, 1, 0, 0, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192370_, p_192374_, 7, 1, 0, 7, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192370_, p_192374_, 1, 1, 0, 6, 1, 0, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192370_, p_192374_, 1, 1, 7, 6, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192370_, p_192374_, 0, 2, 0, 0, 2, 7, BASE_BLACK, BASE_BLACK, false);
         this.generateBox(p_192370_, p_192374_, 7, 2, 0, 7, 2, 7, BASE_BLACK, BASE_BLACK, false);
         this.generateBox(p_192370_, p_192374_, 1, 2, 0, 6, 2, 0, BASE_BLACK, BASE_BLACK, false);
         this.generateBox(p_192370_, p_192374_, 1, 2, 7, 6, 2, 7, BASE_BLACK, BASE_BLACK, false);
         this.generateBox(p_192370_, p_192374_, 0, 3, 0, 0, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192370_, p_192374_, 7, 3, 0, 7, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192370_, p_192374_, 1, 3, 0, 6, 3, 0, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192370_, p_192374_, 1, 3, 7, 6, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_192370_, p_192374_, 0, 1, 3, 0, 2, 4, BASE_BLACK, BASE_BLACK, false);
         this.generateBox(p_192370_, p_192374_, 7, 1, 3, 7, 2, 4, BASE_BLACK, BASE_BLACK, false);
         this.generateBox(p_192370_, p_192374_, 3, 1, 0, 4, 2, 0, BASE_BLACK, BASE_BLACK, false);
         this.generateBox(p_192370_, p_192374_, 3, 1, 7, 4, 2, 7, BASE_BLACK, BASE_BLACK, false);
         if (this.roomDefinition.hasOpening[Direction.SOUTH.get3DDataValue()]) {
            this.generateWaterBox(p_192370_, p_192374_, 3, 1, 0, 4, 2, 0);
         }

      }
   }

   public static class OceanMonumentWingRoom extends OceanMonumentPieces.OceanMonumentPiece {
      private int mainDesign;

      public OceanMonumentWingRoom(Direction p_72432_, BoundingBox p_72433_, int p_72434_) {
         super(StructurePieceType.OCEAN_MONUMENT_WING_ROOM, p_72432_, 1, p_72433_);
         this.mainDesign = p_72434_ & 1;
      }

      public OceanMonumentWingRoom(CompoundTag p_192378_) {
         super(StructurePieceType.OCEAN_MONUMENT_WING_ROOM, p_192378_);
      }

      public void postProcess(WorldGenLevel p_192380_, StructureFeatureManager p_192381_, ChunkGenerator p_192382_, Random p_192383_, BoundingBox p_192384_, ChunkPos p_192385_, BlockPos p_192386_) {
         if (this.mainDesign == 0) {
            for(int i = 0; i < 4; ++i) {
               this.generateBox(p_192380_, p_192384_, 10 - i, 3 - i, 20 - i, 12 + i, 3 - i, 20, BASE_LIGHT, BASE_LIGHT, false);
            }

            this.generateBox(p_192380_, p_192384_, 7, 0, 6, 15, 0, 16, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_192380_, p_192384_, 6, 0, 6, 6, 3, 20, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_192380_, p_192384_, 16, 0, 6, 16, 3, 20, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_192380_, p_192384_, 7, 1, 7, 7, 1, 20, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_192380_, p_192384_, 15, 1, 7, 15, 1, 20, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_192380_, p_192384_, 7, 1, 6, 9, 3, 6, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_192380_, p_192384_, 13, 1, 6, 15, 3, 6, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_192380_, p_192384_, 8, 1, 7, 9, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_192380_, p_192384_, 13, 1, 7, 14, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_192380_, p_192384_, 9, 0, 5, 13, 0, 5, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_192380_, p_192384_, 10, 0, 7, 12, 0, 7, BASE_BLACK, BASE_BLACK, false);
            this.generateBox(p_192380_, p_192384_, 8, 0, 10, 8, 0, 12, BASE_BLACK, BASE_BLACK, false);
            this.generateBox(p_192380_, p_192384_, 14, 0, 10, 14, 0, 12, BASE_BLACK, BASE_BLACK, false);

            for(int i1 = 18; i1 >= 7; i1 -= 3) {
               this.placeBlock(p_192380_, LAMP_BLOCK, 6, 3, i1, p_192384_);
               this.placeBlock(p_192380_, LAMP_BLOCK, 16, 3, i1, p_192384_);
            }

            this.placeBlock(p_192380_, LAMP_BLOCK, 10, 0, 10, p_192384_);
            this.placeBlock(p_192380_, LAMP_BLOCK, 12, 0, 10, p_192384_);
            this.placeBlock(p_192380_, LAMP_BLOCK, 10, 0, 12, p_192384_);
            this.placeBlock(p_192380_, LAMP_BLOCK, 12, 0, 12, p_192384_);
            this.placeBlock(p_192380_, LAMP_BLOCK, 8, 3, 6, p_192384_);
            this.placeBlock(p_192380_, LAMP_BLOCK, 14, 3, 6, p_192384_);
            this.placeBlock(p_192380_, BASE_LIGHT, 4, 2, 4, p_192384_);
            this.placeBlock(p_192380_, LAMP_BLOCK, 4, 1, 4, p_192384_);
            this.placeBlock(p_192380_, BASE_LIGHT, 4, 0, 4, p_192384_);
            this.placeBlock(p_192380_, BASE_LIGHT, 18, 2, 4, p_192384_);
            this.placeBlock(p_192380_, LAMP_BLOCK, 18, 1, 4, p_192384_);
            this.placeBlock(p_192380_, BASE_LIGHT, 18, 0, 4, p_192384_);
            this.placeBlock(p_192380_, BASE_LIGHT, 4, 2, 18, p_192384_);
            this.placeBlock(p_192380_, LAMP_BLOCK, 4, 1, 18, p_192384_);
            this.placeBlock(p_192380_, BASE_LIGHT, 4, 0, 18, p_192384_);
            this.placeBlock(p_192380_, BASE_LIGHT, 18, 2, 18, p_192384_);
            this.placeBlock(p_192380_, LAMP_BLOCK, 18, 1, 18, p_192384_);
            this.placeBlock(p_192380_, BASE_LIGHT, 18, 0, 18, p_192384_);
            this.placeBlock(p_192380_, BASE_LIGHT, 9, 7, 20, p_192384_);
            this.placeBlock(p_192380_, BASE_LIGHT, 13, 7, 20, p_192384_);
            this.generateBox(p_192380_, p_192384_, 6, 0, 21, 7, 4, 21, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_192380_, p_192384_, 15, 0, 21, 16, 4, 21, BASE_LIGHT, BASE_LIGHT, false);
            this.spawnElder(p_192380_, p_192384_, 11, 2, 16);
         } else if (this.mainDesign == 1) {
            this.generateBox(p_192380_, p_192384_, 9, 3, 18, 13, 3, 20, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_192380_, p_192384_, 9, 0, 18, 9, 2, 18, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_192380_, p_192384_, 13, 0, 18, 13, 2, 18, BASE_LIGHT, BASE_LIGHT, false);
            int j1 = 9;
            int j = 20;
            int k = 5;

            for(int l = 0; l < 2; ++l) {
               this.placeBlock(p_192380_, BASE_LIGHT, j1, 6, 20, p_192384_);
               this.placeBlock(p_192380_, LAMP_BLOCK, j1, 5, 20, p_192384_);
               this.placeBlock(p_192380_, BASE_LIGHT, j1, 4, 20, p_192384_);
               j1 = 13;
            }

            this.generateBox(p_192380_, p_192384_, 7, 3, 7, 15, 3, 14, BASE_LIGHT, BASE_LIGHT, false);
            j1 = 10;

            for(int k1 = 0; k1 < 2; ++k1) {
               this.generateBox(p_192380_, p_192384_, j1, 0, 10, j1, 6, 10, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_192380_, p_192384_, j1, 0, 12, j1, 6, 12, BASE_LIGHT, BASE_LIGHT, false);
               this.placeBlock(p_192380_, LAMP_BLOCK, j1, 0, 10, p_192384_);
               this.placeBlock(p_192380_, LAMP_BLOCK, j1, 0, 12, p_192384_);
               this.placeBlock(p_192380_, LAMP_BLOCK, j1, 4, 10, p_192384_);
               this.placeBlock(p_192380_, LAMP_BLOCK, j1, 4, 12, p_192384_);
               j1 = 12;
            }

            j1 = 8;

            for(int l1 = 0; l1 < 2; ++l1) {
               this.generateBox(p_192380_, p_192384_, j1, 0, 7, j1, 2, 7, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_192380_, p_192384_, j1, 0, 14, j1, 2, 14, BASE_LIGHT, BASE_LIGHT, false);
               j1 = 14;
            }

            this.generateBox(p_192380_, p_192384_, 8, 3, 8, 8, 3, 13, BASE_BLACK, BASE_BLACK, false);
            this.generateBox(p_192380_, p_192384_, 14, 3, 8, 14, 3, 13, BASE_BLACK, BASE_BLACK, false);
            this.spawnElder(p_192380_, p_192384_, 11, 5, 13);
         }

      }
   }

   static class RoomDefinition {
      final int index;
      final OceanMonumentPieces.RoomDefinition[] connections = new OceanMonumentPieces.RoomDefinition[6];
      final boolean[] hasOpening = new boolean[6];
      boolean claimed;
      boolean isSource;
      private int scanIndex;

      public RoomDefinition(int p_72450_) {
         this.index = p_72450_;
      }

      public void setConnection(Direction p_72460_, OceanMonumentPieces.RoomDefinition p_72461_) {
         this.connections[p_72460_.get3DDataValue()] = p_72461_;
         p_72461_.connections[p_72460_.getOpposite().get3DDataValue()] = this;
      }

      public void updateOpenings() {
         for(int i = 0; i < 6; ++i) {
            this.hasOpening[i] = this.connections[i] != null;
         }

      }

      public boolean findSource(int p_72453_) {
         if (this.isSource) {
            return true;
         } else {
            this.scanIndex = p_72453_;

            for(int i = 0; i < 6; ++i) {
               if (this.connections[i] != null && this.hasOpening[i] && this.connections[i].scanIndex != p_72453_ && this.connections[i].findSource(p_72453_)) {
                  return true;
               }
            }

            return false;
         }
      }

      public boolean isSpecial() {
         return this.index >= 75;
      }

      public int countOpenings() {
         int i = 0;

         for(int j = 0; j < 6; ++j) {
            if (this.hasOpening[j]) {
               ++i;
            }
         }

         return i;
      }
   }
}