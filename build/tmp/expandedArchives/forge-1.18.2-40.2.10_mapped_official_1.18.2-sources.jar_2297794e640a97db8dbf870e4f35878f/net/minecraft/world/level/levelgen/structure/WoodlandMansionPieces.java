package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class WoodlandMansionPieces {
   public static void generateMansion(StructureManager p_73692_, BlockPos p_73693_, Rotation p_73694_, List<WoodlandMansionPieces.WoodlandMansionPiece> p_73695_, Random p_73696_) {
      WoodlandMansionPieces.MansionGrid woodlandmansionpieces$mansiongrid = new WoodlandMansionPieces.MansionGrid(p_73696_);
      WoodlandMansionPieces.MansionPiecePlacer woodlandmansionpieces$mansionpieceplacer = new WoodlandMansionPieces.MansionPiecePlacer(p_73692_, p_73696_);
      woodlandmansionpieces$mansionpieceplacer.createMansion(p_73693_, p_73694_, p_73695_, woodlandmansionpieces$mansiongrid);
   }

   public static void main(String[] p_163677_) {
      Random random = new Random();
      long i = random.nextLong();
      System.out.println("Seed: " + i);
      random.setSeed(i);
      WoodlandMansionPieces.MansionGrid woodlandmansionpieces$mansiongrid = new WoodlandMansionPieces.MansionGrid(random);
      woodlandmansionpieces$mansiongrid.print();
   }

   static class FirstFloorRoomCollection extends WoodlandMansionPieces.FloorRoomCollection {
      public String get1x1(Random p_73701_) {
         return "1x1_a" + (p_73701_.nextInt(5) + 1);
      }

      public String get1x1Secret(Random p_73706_) {
         return "1x1_as" + (p_73706_.nextInt(4) + 1);
      }

      public String get1x2SideEntrance(Random p_73703_, boolean p_73704_) {
         return "1x2_a" + (p_73703_.nextInt(9) + 1);
      }

      public String get1x2FrontEntrance(Random p_73708_, boolean p_73709_) {
         return "1x2_b" + (p_73708_.nextInt(5) + 1);
      }

      public String get1x2Secret(Random p_73711_) {
         return "1x2_s" + (p_73711_.nextInt(2) + 1);
      }

      public String get2x2(Random p_73713_) {
         return "2x2_a" + (p_73713_.nextInt(4) + 1);
      }

      public String get2x2Secret(Random p_73715_) {
         return "2x2_s1";
      }
   }

   abstract static class FloorRoomCollection {
      public abstract String get1x1(Random p_73719_);

      public abstract String get1x1Secret(Random p_73722_);

      public abstract String get1x2SideEntrance(Random p_73720_, boolean p_73721_);

      public abstract String get1x2FrontEntrance(Random p_73723_, boolean p_73724_);

      public abstract String get1x2Secret(Random p_73725_);

      public abstract String get2x2(Random p_73726_);

      public abstract String get2x2Secret(Random p_73727_);
   }

   static class MansionGrid {
      private static final int DEFAULT_SIZE = 11;
      private static final int CLEAR = 0;
      private static final int CORRIDOR = 1;
      private static final int ROOM = 2;
      private static final int START_ROOM = 3;
      private static final int TEST_ROOM = 4;
      private static final int BLOCKED = 5;
      private static final int ROOM_1x1 = 65536;
      private static final int ROOM_1x2 = 131072;
      private static final int ROOM_2x2 = 262144;
      private static final int ROOM_ORIGIN_FLAG = 1048576;
      private static final int ROOM_DOOR_FLAG = 2097152;
      private static final int ROOM_STAIRS_FLAG = 4194304;
      private static final int ROOM_CORRIDOR_FLAG = 8388608;
      private static final int ROOM_TYPE_MASK = 983040;
      private static final int ROOM_ID_MASK = 65535;
      private final Random random;
      final WoodlandMansionPieces.SimpleGrid baseGrid;
      final WoodlandMansionPieces.SimpleGrid thirdFloorGrid;
      final WoodlandMansionPieces.SimpleGrid[] floorRooms;
      final int entranceX;
      final int entranceY;

      public MansionGrid(Random p_73735_) {
         this.random = p_73735_;
         int i = 11;
         this.entranceX = 7;
         this.entranceY = 4;
         this.baseGrid = new WoodlandMansionPieces.SimpleGrid(11, 11, 5);
         this.baseGrid.set(this.entranceX, this.entranceY, this.entranceX + 1, this.entranceY + 1, 3);
         this.baseGrid.set(this.entranceX - 1, this.entranceY, this.entranceX - 1, this.entranceY + 1, 2);
         this.baseGrid.set(this.entranceX + 2, this.entranceY - 2, this.entranceX + 3, this.entranceY + 3, 5);
         this.baseGrid.set(this.entranceX + 1, this.entranceY - 2, this.entranceX + 1, this.entranceY - 1, 1);
         this.baseGrid.set(this.entranceX + 1, this.entranceY + 2, this.entranceX + 1, this.entranceY + 3, 1);
         this.baseGrid.set(this.entranceX - 1, this.entranceY - 1, 1);
         this.baseGrid.set(this.entranceX - 1, this.entranceY + 2, 1);
         this.baseGrid.set(0, 0, 11, 1, 5);
         this.baseGrid.set(0, 9, 11, 11, 5);
         this.recursiveCorridor(this.baseGrid, this.entranceX, this.entranceY - 2, Direction.WEST, 6);
         this.recursiveCorridor(this.baseGrid, this.entranceX, this.entranceY + 3, Direction.WEST, 6);
         this.recursiveCorridor(this.baseGrid, this.entranceX - 2, this.entranceY - 1, Direction.WEST, 3);
         this.recursiveCorridor(this.baseGrid, this.entranceX - 2, this.entranceY + 2, Direction.WEST, 3);

         while(this.cleanEdges(this.baseGrid)) {
         }

         this.floorRooms = new WoodlandMansionPieces.SimpleGrid[3];
         this.floorRooms[0] = new WoodlandMansionPieces.SimpleGrid(11, 11, 5);
         this.floorRooms[1] = new WoodlandMansionPieces.SimpleGrid(11, 11, 5);
         this.floorRooms[2] = new WoodlandMansionPieces.SimpleGrid(11, 11, 5);
         this.identifyRooms(this.baseGrid, this.floorRooms[0]);
         this.identifyRooms(this.baseGrid, this.floorRooms[1]);
         this.floorRooms[0].set(this.entranceX + 1, this.entranceY, this.entranceX + 1, this.entranceY + 1, 8388608);
         this.floorRooms[1].set(this.entranceX + 1, this.entranceY, this.entranceX + 1, this.entranceY + 1, 8388608);
         this.thirdFloorGrid = new WoodlandMansionPieces.SimpleGrid(this.baseGrid.width, this.baseGrid.height, 5);
         this.setupThirdFloor();
         this.identifyRooms(this.thirdFloorGrid, this.floorRooms[2]);
      }

      public static boolean isHouse(WoodlandMansionPieces.SimpleGrid p_73741_, int p_73742_, int p_73743_) {
         int i = p_73741_.get(p_73742_, p_73743_);
         return i == 1 || i == 2 || i == 3 || i == 4;
      }

      public boolean isRoomId(WoodlandMansionPieces.SimpleGrid p_73745_, int p_73746_, int p_73747_, int p_73748_, int p_73749_) {
         return (this.floorRooms[p_73748_].get(p_73746_, p_73747_) & '\uffff') == p_73749_;
      }

      @Nullable
      public Direction get1x2RoomDirection(WoodlandMansionPieces.SimpleGrid p_73763_, int p_73764_, int p_73765_, int p_73766_, int p_73767_) {
         for(Direction direction : Direction.Plane.HORIZONTAL) {
            if (this.isRoomId(p_73763_, p_73764_ + direction.getStepX(), p_73765_ + direction.getStepZ(), p_73766_, p_73767_)) {
               return direction;
            }
         }

         return null;
      }

      private void recursiveCorridor(WoodlandMansionPieces.SimpleGrid p_73751_, int p_73752_, int p_73753_, Direction p_73754_, int p_73755_) {
         if (p_73755_ > 0) {
            p_73751_.set(p_73752_, p_73753_, 1);
            p_73751_.setif(p_73752_ + p_73754_.getStepX(), p_73753_ + p_73754_.getStepZ(), 0, 1);

            for(int i = 0; i < 8; ++i) {
               Direction direction = Direction.from2DDataValue(this.random.nextInt(4));
               if (direction != p_73754_.getOpposite() && (direction != Direction.EAST || !this.random.nextBoolean())) {
                  int j = p_73752_ + p_73754_.getStepX();
                  int k = p_73753_ + p_73754_.getStepZ();
                  if (p_73751_.get(j + direction.getStepX(), k + direction.getStepZ()) == 0 && p_73751_.get(j + direction.getStepX() * 2, k + direction.getStepZ() * 2) == 0) {
                     this.recursiveCorridor(p_73751_, p_73752_ + p_73754_.getStepX() + direction.getStepX(), p_73753_ + p_73754_.getStepZ() + direction.getStepZ(), direction, p_73755_ - 1);
                     break;
                  }
               }
            }

            Direction direction1 = p_73754_.getClockWise();
            Direction direction2 = p_73754_.getCounterClockWise();
            p_73751_.setif(p_73752_ + direction1.getStepX(), p_73753_ + direction1.getStepZ(), 0, 2);
            p_73751_.setif(p_73752_ + direction2.getStepX(), p_73753_ + direction2.getStepZ(), 0, 2);
            p_73751_.setif(p_73752_ + p_73754_.getStepX() + direction1.getStepX(), p_73753_ + p_73754_.getStepZ() + direction1.getStepZ(), 0, 2);
            p_73751_.setif(p_73752_ + p_73754_.getStepX() + direction2.getStepX(), p_73753_ + p_73754_.getStepZ() + direction2.getStepZ(), 0, 2);
            p_73751_.setif(p_73752_ + p_73754_.getStepX() * 2, p_73753_ + p_73754_.getStepZ() * 2, 0, 2);
            p_73751_.setif(p_73752_ + direction1.getStepX() * 2, p_73753_ + direction1.getStepZ() * 2, 0, 2);
            p_73751_.setif(p_73752_ + direction2.getStepX() * 2, p_73753_ + direction2.getStepZ() * 2, 0, 2);
         }
      }

      private boolean cleanEdges(WoodlandMansionPieces.SimpleGrid p_73739_) {
         boolean flag = false;

         for(int i = 0; i < p_73739_.height; ++i) {
            for(int j = 0; j < p_73739_.width; ++j) {
               if (p_73739_.get(j, i) == 0) {
                  int k = 0;
                  k += isHouse(p_73739_, j + 1, i) ? 1 : 0;
                  k += isHouse(p_73739_, j - 1, i) ? 1 : 0;
                  k += isHouse(p_73739_, j, i + 1) ? 1 : 0;
                  k += isHouse(p_73739_, j, i - 1) ? 1 : 0;
                  if (k >= 3) {
                     p_73739_.set(j, i, 2);
                     flag = true;
                  } else if (k == 2) {
                     int l = 0;
                     l += isHouse(p_73739_, j + 1, i + 1) ? 1 : 0;
                     l += isHouse(p_73739_, j - 1, i + 1) ? 1 : 0;
                     l += isHouse(p_73739_, j + 1, i - 1) ? 1 : 0;
                     l += isHouse(p_73739_, j - 1, i - 1) ? 1 : 0;
                     if (l <= 1) {
                        p_73739_.set(j, i, 2);
                        flag = true;
                     }
                  }
               }
            }
         }

         return flag;
      }

      private void setupThirdFloor() {
         List<Tuple<Integer, Integer>> list = Lists.newArrayList();
         WoodlandMansionPieces.SimpleGrid woodlandmansionpieces$simplegrid = this.floorRooms[1];

         for(int i = 0; i < this.thirdFloorGrid.height; ++i) {
            for(int j = 0; j < this.thirdFloorGrid.width; ++j) {
               int k = woodlandmansionpieces$simplegrid.get(j, i);
               int l = k & 983040;
               if (l == 131072 && (k & 2097152) == 2097152) {
                  list.add(new Tuple<>(j, i));
               }
            }
         }

         if (list.isEmpty()) {
            this.thirdFloorGrid.set(0, 0, this.thirdFloorGrid.width, this.thirdFloorGrid.height, 5);
         } else {
            Tuple<Integer, Integer> tuple = list.get(this.random.nextInt(list.size()));
            int l1 = woodlandmansionpieces$simplegrid.get(tuple.getA(), tuple.getB());
            woodlandmansionpieces$simplegrid.set(tuple.getA(), tuple.getB(), l1 | 4194304);
            Direction direction1 = this.get1x2RoomDirection(this.baseGrid, tuple.getA(), tuple.getB(), 1, l1 & '\uffff');
            int i2 = tuple.getA() + direction1.getStepX();
            int i1 = tuple.getB() + direction1.getStepZ();

            for(int j1 = 0; j1 < this.thirdFloorGrid.height; ++j1) {
               for(int k1 = 0; k1 < this.thirdFloorGrid.width; ++k1) {
                  if (!isHouse(this.baseGrid, k1, j1)) {
                     this.thirdFloorGrid.set(k1, j1, 5);
                  } else if (k1 == tuple.getA() && j1 == tuple.getB()) {
                     this.thirdFloorGrid.set(k1, j1, 3);
                  } else if (k1 == i2 && j1 == i1) {
                     this.thirdFloorGrid.set(k1, j1, 3);
                     this.floorRooms[2].set(k1, j1, 8388608);
                  }
               }
            }

            List<Direction> list1 = Lists.newArrayList();

            for(Direction direction : Direction.Plane.HORIZONTAL) {
               if (this.thirdFloorGrid.get(i2 + direction.getStepX(), i1 + direction.getStepZ()) == 0) {
                  list1.add(direction);
               }
            }

            if (list1.isEmpty()) {
               this.thirdFloorGrid.set(0, 0, this.thirdFloorGrid.width, this.thirdFloorGrid.height, 5);
               woodlandmansionpieces$simplegrid.set(tuple.getA(), tuple.getB(), l1);
            } else {
               Direction direction2 = list1.get(this.random.nextInt(list1.size()));
               this.recursiveCorridor(this.thirdFloorGrid, i2 + direction2.getStepX(), i1 + direction2.getStepZ(), direction2, 4);

               while(this.cleanEdges(this.thirdFloorGrid)) {
               }

            }
         }
      }

      private void identifyRooms(WoodlandMansionPieces.SimpleGrid p_73757_, WoodlandMansionPieces.SimpleGrid p_73758_) {
         List<Tuple<Integer, Integer>> list = Lists.newArrayList();

         for(int i = 0; i < p_73757_.height; ++i) {
            for(int j = 0; j < p_73757_.width; ++j) {
               if (p_73757_.get(j, i) == 2) {
                  list.add(new Tuple<>(j, i));
               }
            }
         }

         Collections.shuffle(list, this.random);
         int k3 = 10;

         for(Tuple<Integer, Integer> tuple : list) {
            int k = tuple.getA();
            int l = tuple.getB();
            if (p_73758_.get(k, l) == 0) {
               int i1 = k;
               int j1 = k;
               int k1 = l;
               int l1 = l;
               int i2 = 65536;
               if (p_73758_.get(k + 1, l) == 0 && p_73758_.get(k, l + 1) == 0 && p_73758_.get(k + 1, l + 1) == 0 && p_73757_.get(k + 1, l) == 2 && p_73757_.get(k, l + 1) == 2 && p_73757_.get(k + 1, l + 1) == 2) {
                  j1 = k + 1;
                  l1 = l + 1;
                  i2 = 262144;
               } else if (p_73758_.get(k - 1, l) == 0 && p_73758_.get(k, l + 1) == 0 && p_73758_.get(k - 1, l + 1) == 0 && p_73757_.get(k - 1, l) == 2 && p_73757_.get(k, l + 1) == 2 && p_73757_.get(k - 1, l + 1) == 2) {
                  i1 = k - 1;
                  l1 = l + 1;
                  i2 = 262144;
               } else if (p_73758_.get(k - 1, l) == 0 && p_73758_.get(k, l - 1) == 0 && p_73758_.get(k - 1, l - 1) == 0 && p_73757_.get(k - 1, l) == 2 && p_73757_.get(k, l - 1) == 2 && p_73757_.get(k - 1, l - 1) == 2) {
                  i1 = k - 1;
                  k1 = l - 1;
                  i2 = 262144;
               } else if (p_73758_.get(k + 1, l) == 0 && p_73757_.get(k + 1, l) == 2) {
                  j1 = k + 1;
                  i2 = 131072;
               } else if (p_73758_.get(k, l + 1) == 0 && p_73757_.get(k, l + 1) == 2) {
                  l1 = l + 1;
                  i2 = 131072;
               } else if (p_73758_.get(k - 1, l) == 0 && p_73757_.get(k - 1, l) == 2) {
                  i1 = k - 1;
                  i2 = 131072;
               } else if (p_73758_.get(k, l - 1) == 0 && p_73757_.get(k, l - 1) == 2) {
                  k1 = l - 1;
                  i2 = 131072;
               }

               int j2 = this.random.nextBoolean() ? i1 : j1;
               int k2 = this.random.nextBoolean() ? k1 : l1;
               int l2 = 2097152;
               if (!p_73757_.edgesTo(j2, k2, 1)) {
                  j2 = j2 == i1 ? j1 : i1;
                  k2 = k2 == k1 ? l1 : k1;
                  if (!p_73757_.edgesTo(j2, k2, 1)) {
                     k2 = k2 == k1 ? l1 : k1;
                     if (!p_73757_.edgesTo(j2, k2, 1)) {
                        j2 = j2 == i1 ? j1 : i1;
                        k2 = k2 == k1 ? l1 : k1;
                        if (!p_73757_.edgesTo(j2, k2, 1)) {
                           l2 = 0;
                           j2 = i1;
                           k2 = k1;
                        }
                     }
                  }
               }

               for(int i3 = k1; i3 <= l1; ++i3) {
                  for(int j3 = i1; j3 <= j1; ++j3) {
                     if (j3 == j2 && i3 == k2) {
                        p_73758_.set(j3, i3, 1048576 | l2 | i2 | k3);
                     } else {
                        p_73758_.set(j3, i3, i2 | k3);
                     }
                  }
               }

               ++k3;
            }
         }

      }

      public void print() {
         for(int i = 0; i < 2; ++i) {
            WoodlandMansionPieces.SimpleGrid woodlandmansionpieces$simplegrid = i == 0 ? this.baseGrid : this.thirdFloorGrid;

            for(int j = 0; j < woodlandmansionpieces$simplegrid.height; ++j) {
               for(int k = 0; k < woodlandmansionpieces$simplegrid.width; ++k) {
                  int l = woodlandmansionpieces$simplegrid.get(k, j);
                  if (l == 1) {
                     System.out.print("+");
                  } else if (l == 4) {
                     System.out.print("x");
                  } else if (l == 2) {
                     System.out.print("X");
                  } else if (l == 3) {
                     System.out.print("O");
                  } else if (l == 5) {
                     System.out.print("#");
                  } else {
                     System.out.print(" ");
                  }
               }

               System.out.println("");
            }

            System.out.println("");
         }

      }
   }

   static class MansionPiecePlacer {
      private final StructureManager structureManager;
      private final Random random;
      private int startX;
      private int startY;

      public MansionPiecePlacer(StructureManager p_73779_, Random p_73780_) {
         this.structureManager = p_73779_;
         this.random = p_73780_;
      }

      public void createMansion(BlockPos p_73782_, Rotation p_73783_, List<WoodlandMansionPieces.WoodlandMansionPiece> p_73784_, WoodlandMansionPieces.MansionGrid p_73785_) {
         WoodlandMansionPieces.PlacementData woodlandmansionpieces$placementdata = new WoodlandMansionPieces.PlacementData();
         woodlandmansionpieces$placementdata.position = p_73782_;
         woodlandmansionpieces$placementdata.rotation = p_73783_;
         woodlandmansionpieces$placementdata.wallType = "wall_flat";
         WoodlandMansionPieces.PlacementData woodlandmansionpieces$placementdata1 = new WoodlandMansionPieces.PlacementData();
         this.entrance(p_73784_, woodlandmansionpieces$placementdata);
         woodlandmansionpieces$placementdata1.position = woodlandmansionpieces$placementdata.position.above(8);
         woodlandmansionpieces$placementdata1.rotation = woodlandmansionpieces$placementdata.rotation;
         woodlandmansionpieces$placementdata1.wallType = "wall_window";
         if (!p_73784_.isEmpty()) {
         }

         WoodlandMansionPieces.SimpleGrid woodlandmansionpieces$simplegrid = p_73785_.baseGrid;
         WoodlandMansionPieces.SimpleGrid woodlandmansionpieces$simplegrid1 = p_73785_.thirdFloorGrid;
         this.startX = p_73785_.entranceX + 1;
         this.startY = p_73785_.entranceY + 1;
         int i = p_73785_.entranceX + 1;
         int j = p_73785_.entranceY;
         this.traverseOuterWalls(p_73784_, woodlandmansionpieces$placementdata, woodlandmansionpieces$simplegrid, Direction.SOUTH, this.startX, this.startY, i, j);
         this.traverseOuterWalls(p_73784_, woodlandmansionpieces$placementdata1, woodlandmansionpieces$simplegrid, Direction.SOUTH, this.startX, this.startY, i, j);
         WoodlandMansionPieces.PlacementData woodlandmansionpieces$placementdata2 = new WoodlandMansionPieces.PlacementData();
         woodlandmansionpieces$placementdata2.position = woodlandmansionpieces$placementdata.position.above(19);
         woodlandmansionpieces$placementdata2.rotation = woodlandmansionpieces$placementdata.rotation;
         woodlandmansionpieces$placementdata2.wallType = "wall_window";
         boolean flag = false;

         for(int k = 0; k < woodlandmansionpieces$simplegrid1.height && !flag; ++k) {
            for(int l = woodlandmansionpieces$simplegrid1.width - 1; l >= 0 && !flag; --l) {
               if (WoodlandMansionPieces.MansionGrid.isHouse(woodlandmansionpieces$simplegrid1, l, k)) {
                  woodlandmansionpieces$placementdata2.position = woodlandmansionpieces$placementdata2.position.relative(p_73783_.rotate(Direction.SOUTH), 8 + (k - this.startY) * 8);
                  woodlandmansionpieces$placementdata2.position = woodlandmansionpieces$placementdata2.position.relative(p_73783_.rotate(Direction.EAST), (l - this.startX) * 8);
                  this.traverseWallPiece(p_73784_, woodlandmansionpieces$placementdata2);
                  this.traverseOuterWalls(p_73784_, woodlandmansionpieces$placementdata2, woodlandmansionpieces$simplegrid1, Direction.SOUTH, l, k, l, k);
                  flag = true;
               }
            }
         }

         this.createRoof(p_73784_, p_73782_.above(16), p_73783_, woodlandmansionpieces$simplegrid, woodlandmansionpieces$simplegrid1);
         this.createRoof(p_73784_, p_73782_.above(27), p_73783_, woodlandmansionpieces$simplegrid1, (WoodlandMansionPieces.SimpleGrid)null);
         if (!p_73784_.isEmpty()) {
         }

         WoodlandMansionPieces.FloorRoomCollection[] awoodlandmansionpieces$floorroomcollection = new WoodlandMansionPieces.FloorRoomCollection[]{new WoodlandMansionPieces.FirstFloorRoomCollection(), new WoodlandMansionPieces.SecondFloorRoomCollection(), new WoodlandMansionPieces.ThirdFloorRoomCollection()};

         for(int l2 = 0; l2 < 3; ++l2) {
            BlockPos blockpos = p_73782_.above(8 * l2 + (l2 == 2 ? 3 : 0));
            WoodlandMansionPieces.SimpleGrid woodlandmansionpieces$simplegrid2 = p_73785_.floorRooms[l2];
            WoodlandMansionPieces.SimpleGrid woodlandmansionpieces$simplegrid3 = l2 == 2 ? woodlandmansionpieces$simplegrid1 : woodlandmansionpieces$simplegrid;
            String s = l2 == 0 ? "carpet_south_1" : "carpet_south_2";
            String s1 = l2 == 0 ? "carpet_west_1" : "carpet_west_2";

            for(int i1 = 0; i1 < woodlandmansionpieces$simplegrid3.height; ++i1) {
               for(int j1 = 0; j1 < woodlandmansionpieces$simplegrid3.width; ++j1) {
                  if (woodlandmansionpieces$simplegrid3.get(j1, i1) == 1) {
                     BlockPos blockpos1 = blockpos.relative(p_73783_.rotate(Direction.SOUTH), 8 + (i1 - this.startY) * 8);
                     blockpos1 = blockpos1.relative(p_73783_.rotate(Direction.EAST), (j1 - this.startX) * 8);
                     p_73784_.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "corridor_floor", blockpos1, p_73783_));
                     if (woodlandmansionpieces$simplegrid3.get(j1, i1 - 1) == 1 || (woodlandmansionpieces$simplegrid2.get(j1, i1 - 1) & 8388608) == 8388608) {
                        p_73784_.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "carpet_north", blockpos1.relative(p_73783_.rotate(Direction.EAST), 1).above(), p_73783_));
                     }

                     if (woodlandmansionpieces$simplegrid3.get(j1 + 1, i1) == 1 || (woodlandmansionpieces$simplegrid2.get(j1 + 1, i1) & 8388608) == 8388608) {
                        p_73784_.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "carpet_east", blockpos1.relative(p_73783_.rotate(Direction.SOUTH), 1).relative(p_73783_.rotate(Direction.EAST), 5).above(), p_73783_));
                     }

                     if (woodlandmansionpieces$simplegrid3.get(j1, i1 + 1) == 1 || (woodlandmansionpieces$simplegrid2.get(j1, i1 + 1) & 8388608) == 8388608) {
                        p_73784_.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, s, blockpos1.relative(p_73783_.rotate(Direction.SOUTH), 5).relative(p_73783_.rotate(Direction.WEST), 1), p_73783_));
                     }

                     if (woodlandmansionpieces$simplegrid3.get(j1 - 1, i1) == 1 || (woodlandmansionpieces$simplegrid2.get(j1 - 1, i1) & 8388608) == 8388608) {
                        p_73784_.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, s1, blockpos1.relative(p_73783_.rotate(Direction.WEST), 1).relative(p_73783_.rotate(Direction.NORTH), 1), p_73783_));
                     }
                  }
               }
            }

            String s2 = l2 == 0 ? "indoors_wall_1" : "indoors_wall_2";
            String s3 = l2 == 0 ? "indoors_door_1" : "indoors_door_2";
            List<Direction> list = Lists.newArrayList();

            for(int k1 = 0; k1 < woodlandmansionpieces$simplegrid3.height; ++k1) {
               for(int l1 = 0; l1 < woodlandmansionpieces$simplegrid3.width; ++l1) {
                  boolean flag1 = l2 == 2 && woodlandmansionpieces$simplegrid3.get(l1, k1) == 3;
                  if (woodlandmansionpieces$simplegrid3.get(l1, k1) == 2 || flag1) {
                     int i2 = woodlandmansionpieces$simplegrid2.get(l1, k1);
                     int j2 = i2 & 983040;
                     int k2 = i2 & '\uffff';
                     flag1 = flag1 && (i2 & 8388608) == 8388608;
                     list.clear();
                     if ((i2 & 2097152) == 2097152) {
                        for(Direction direction : Direction.Plane.HORIZONTAL) {
                           if (woodlandmansionpieces$simplegrid3.get(l1 + direction.getStepX(), k1 + direction.getStepZ()) == 1) {
                              list.add(direction);
                           }
                        }
                     }

                     Direction direction1 = null;
                     if (!list.isEmpty()) {
                        direction1 = list.get(this.random.nextInt(list.size()));
                     } else if ((i2 & 1048576) == 1048576) {
                        direction1 = Direction.UP;
                     }

                     BlockPos blockpos3 = blockpos.relative(p_73783_.rotate(Direction.SOUTH), 8 + (k1 - this.startY) * 8);
                     blockpos3 = blockpos3.relative(p_73783_.rotate(Direction.EAST), -1 + (l1 - this.startX) * 8);
                     if (WoodlandMansionPieces.MansionGrid.isHouse(woodlandmansionpieces$simplegrid3, l1 - 1, k1) && !p_73785_.isRoomId(woodlandmansionpieces$simplegrid3, l1 - 1, k1, l2, k2)) {
                        p_73784_.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, direction1 == Direction.WEST ? s3 : s2, blockpos3, p_73783_));
                     }

                     if (woodlandmansionpieces$simplegrid3.get(l1 + 1, k1) == 1 && !flag1) {
                        BlockPos blockpos2 = blockpos3.relative(p_73783_.rotate(Direction.EAST), 8);
                        p_73784_.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, direction1 == Direction.EAST ? s3 : s2, blockpos2, p_73783_));
                     }

                     if (WoodlandMansionPieces.MansionGrid.isHouse(woodlandmansionpieces$simplegrid3, l1, k1 + 1) && !p_73785_.isRoomId(woodlandmansionpieces$simplegrid3, l1, k1 + 1, l2, k2)) {
                        BlockPos blockpos4 = blockpos3.relative(p_73783_.rotate(Direction.SOUTH), 7);
                        blockpos4 = blockpos4.relative(p_73783_.rotate(Direction.EAST), 7);
                        p_73784_.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, direction1 == Direction.SOUTH ? s3 : s2, blockpos4, p_73783_.getRotated(Rotation.CLOCKWISE_90)));
                     }

                     if (woodlandmansionpieces$simplegrid3.get(l1, k1 - 1) == 1 && !flag1) {
                        BlockPos blockpos5 = blockpos3.relative(p_73783_.rotate(Direction.NORTH), 1);
                        blockpos5 = blockpos5.relative(p_73783_.rotate(Direction.EAST), 7);
                        p_73784_.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, direction1 == Direction.NORTH ? s3 : s2, blockpos5, p_73783_.getRotated(Rotation.CLOCKWISE_90)));
                     }

                     if (j2 == 65536) {
                        this.addRoom1x1(p_73784_, blockpos3, p_73783_, direction1, awoodlandmansionpieces$floorroomcollection[l2]);
                     } else if (j2 == 131072 && direction1 != null) {
                        Direction direction3 = p_73785_.get1x2RoomDirection(woodlandmansionpieces$simplegrid3, l1, k1, l2, k2);
                        boolean flag2 = (i2 & 4194304) == 4194304;
                        this.addRoom1x2(p_73784_, blockpos3, p_73783_, direction3, direction1, awoodlandmansionpieces$floorroomcollection[l2], flag2);
                     } else if (j2 == 262144 && direction1 != null && direction1 != Direction.UP) {
                        Direction direction2 = direction1.getClockWise();
                        if (!p_73785_.isRoomId(woodlandmansionpieces$simplegrid3, l1 + direction2.getStepX(), k1 + direction2.getStepZ(), l2, k2)) {
                           direction2 = direction2.getOpposite();
                        }

                        this.addRoom2x2(p_73784_, blockpos3, p_73783_, direction2, direction1, awoodlandmansionpieces$floorroomcollection[l2]);
                     } else if (j2 == 262144 && direction1 == Direction.UP) {
                        this.addRoom2x2Secret(p_73784_, blockpos3, p_73783_, awoodlandmansionpieces$floorroomcollection[l2]);
                     }
                  }
               }
            }
         }

      }

      private void traverseOuterWalls(List<WoodlandMansionPieces.WoodlandMansionPiece> p_73790_, WoodlandMansionPieces.PlacementData p_73791_, WoodlandMansionPieces.SimpleGrid p_73792_, Direction p_73793_, int p_73794_, int p_73795_, int p_73796_, int p_73797_) {
         int i = p_73794_;
         int j = p_73795_;
         Direction direction = p_73793_;

         do {
            if (!WoodlandMansionPieces.MansionGrid.isHouse(p_73792_, i + p_73793_.getStepX(), j + p_73793_.getStepZ())) {
               this.traverseTurn(p_73790_, p_73791_);
               p_73793_ = p_73793_.getClockWise();
               if (i != p_73796_ || j != p_73797_ || direction != p_73793_) {
                  this.traverseWallPiece(p_73790_, p_73791_);
               }
            } else if (WoodlandMansionPieces.MansionGrid.isHouse(p_73792_, i + p_73793_.getStepX(), j + p_73793_.getStepZ()) && WoodlandMansionPieces.MansionGrid.isHouse(p_73792_, i + p_73793_.getStepX() + p_73793_.getCounterClockWise().getStepX(), j + p_73793_.getStepZ() + p_73793_.getCounterClockWise().getStepZ())) {
               this.traverseInnerTurn(p_73790_, p_73791_);
               i += p_73793_.getStepX();
               j += p_73793_.getStepZ();
               p_73793_ = p_73793_.getCounterClockWise();
            } else {
               i += p_73793_.getStepX();
               j += p_73793_.getStepZ();
               if (i != p_73796_ || j != p_73797_ || direction != p_73793_) {
                  this.traverseWallPiece(p_73790_, p_73791_);
               }
            }
         } while(i != p_73796_ || j != p_73797_ || direction != p_73793_);

      }

      private void createRoof(List<WoodlandMansionPieces.WoodlandMansionPiece> p_73804_, BlockPos p_73805_, Rotation p_73806_, WoodlandMansionPieces.SimpleGrid p_73807_, @Nullable WoodlandMansionPieces.SimpleGrid p_73808_) {
         for(int i = 0; i < p_73807_.height; ++i) {
            for(int j = 0; j < p_73807_.width; ++j) {
               BlockPos $$27 = p_73805_.relative(p_73806_.rotate(Direction.SOUTH), 8 + (i - this.startY) * 8);
               $$27 = $$27.relative(p_73806_.rotate(Direction.EAST), (j - this.startX) * 8);
               boolean flag = p_73808_ != null && WoodlandMansionPieces.MansionGrid.isHouse(p_73808_, j, i);
               if (WoodlandMansionPieces.MansionGrid.isHouse(p_73807_, j, i) && !flag) {
                  p_73804_.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "roof", $$27.above(3), p_73806_));
                  if (!WoodlandMansionPieces.MansionGrid.isHouse(p_73807_, j + 1, i)) {
                     BlockPos blockpos1 = $$27.relative(p_73806_.rotate(Direction.EAST), 6);
                     p_73804_.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "roof_front", blockpos1, p_73806_));
                  }

                  if (!WoodlandMansionPieces.MansionGrid.isHouse(p_73807_, j - 1, i)) {
                     BlockPos blockpos5 = $$27.relative(p_73806_.rotate(Direction.EAST), 0);
                     blockpos5 = blockpos5.relative(p_73806_.rotate(Direction.SOUTH), 7);
                     p_73804_.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "roof_front", blockpos5, p_73806_.getRotated(Rotation.CLOCKWISE_180)));
                  }

                  if (!WoodlandMansionPieces.MansionGrid.isHouse(p_73807_, j, i - 1)) {
                     BlockPos blockpos6 = $$27.relative(p_73806_.rotate(Direction.WEST), 1);
                     p_73804_.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "roof_front", blockpos6, p_73806_.getRotated(Rotation.COUNTERCLOCKWISE_90)));
                  }

                  if (!WoodlandMansionPieces.MansionGrid.isHouse(p_73807_, j, i + 1)) {
                     BlockPos blockpos7 = $$27.relative(p_73806_.rotate(Direction.EAST), 6);
                     blockpos7 = blockpos7.relative(p_73806_.rotate(Direction.SOUTH), 6);
                     p_73804_.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "roof_front", blockpos7, p_73806_.getRotated(Rotation.CLOCKWISE_90)));
                  }
               }
            }
         }

         if (p_73808_ != null) {
            for(int k = 0; k < p_73807_.height; ++k) {
               for(int i1 = 0; i1 < p_73807_.width; ++i1) {
                  BlockPos blockpos3 = p_73805_.relative(p_73806_.rotate(Direction.SOUTH), 8 + (k - this.startY) * 8);
                  blockpos3 = blockpos3.relative(p_73806_.rotate(Direction.EAST), (i1 - this.startX) * 8);
                  boolean flag1 = WoodlandMansionPieces.MansionGrid.isHouse(p_73808_, i1, k);
                  if (WoodlandMansionPieces.MansionGrid.isHouse(p_73807_, i1, k) && flag1) {
                     if (!WoodlandMansionPieces.MansionGrid.isHouse(p_73807_, i1 + 1, k)) {
                        BlockPos blockpos8 = blockpos3.relative(p_73806_.rotate(Direction.EAST), 7);
                        p_73804_.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "small_wall", blockpos8, p_73806_));
                     }

                     if (!WoodlandMansionPieces.MansionGrid.isHouse(p_73807_, i1 - 1, k)) {
                        BlockPos blockpos9 = blockpos3.relative(p_73806_.rotate(Direction.WEST), 1);
                        blockpos9 = blockpos9.relative(p_73806_.rotate(Direction.SOUTH), 6);
                        p_73804_.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "small_wall", blockpos9, p_73806_.getRotated(Rotation.CLOCKWISE_180)));
                     }

                     if (!WoodlandMansionPieces.MansionGrid.isHouse(p_73807_, i1, k - 1)) {
                        BlockPos blockpos10 = blockpos3.relative(p_73806_.rotate(Direction.WEST), 0);
                        blockpos10 = blockpos10.relative(p_73806_.rotate(Direction.NORTH), 1);
                        p_73804_.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "small_wall", blockpos10, p_73806_.getRotated(Rotation.COUNTERCLOCKWISE_90)));
                     }

                     if (!WoodlandMansionPieces.MansionGrid.isHouse(p_73807_, i1, k + 1)) {
                        BlockPos blockpos11 = blockpos3.relative(p_73806_.rotate(Direction.EAST), 6);
                        blockpos11 = blockpos11.relative(p_73806_.rotate(Direction.SOUTH), 7);
                        p_73804_.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "small_wall", blockpos11, p_73806_.getRotated(Rotation.CLOCKWISE_90)));
                     }

                     if (!WoodlandMansionPieces.MansionGrid.isHouse(p_73807_, i1 + 1, k)) {
                        if (!WoodlandMansionPieces.MansionGrid.isHouse(p_73807_, i1, k - 1)) {
                           BlockPos blockpos12 = blockpos3.relative(p_73806_.rotate(Direction.EAST), 7);
                           blockpos12 = blockpos12.relative(p_73806_.rotate(Direction.NORTH), 2);
                           p_73804_.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "small_wall_corner", blockpos12, p_73806_));
                        }

                        if (!WoodlandMansionPieces.MansionGrid.isHouse(p_73807_, i1, k + 1)) {
                           BlockPos blockpos13 = blockpos3.relative(p_73806_.rotate(Direction.EAST), 8);
                           blockpos13 = blockpos13.relative(p_73806_.rotate(Direction.SOUTH), 7);
                           p_73804_.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "small_wall_corner", blockpos13, p_73806_.getRotated(Rotation.CLOCKWISE_90)));
                        }
                     }

                     if (!WoodlandMansionPieces.MansionGrid.isHouse(p_73807_, i1 - 1, k)) {
                        if (!WoodlandMansionPieces.MansionGrid.isHouse(p_73807_, i1, k - 1)) {
                           BlockPos blockpos14 = blockpos3.relative(p_73806_.rotate(Direction.WEST), 2);
                           blockpos14 = blockpos14.relative(p_73806_.rotate(Direction.NORTH), 1);
                           p_73804_.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "small_wall_corner", blockpos14, p_73806_.getRotated(Rotation.COUNTERCLOCKWISE_90)));
                        }

                        if (!WoodlandMansionPieces.MansionGrid.isHouse(p_73807_, i1, k + 1)) {
                           BlockPos blockpos15 = blockpos3.relative(p_73806_.rotate(Direction.WEST), 1);
                           blockpos15 = blockpos15.relative(p_73806_.rotate(Direction.SOUTH), 8);
                           p_73804_.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "small_wall_corner", blockpos15, p_73806_.getRotated(Rotation.CLOCKWISE_180)));
                        }
                     }
                  }
               }
            }
         }

         for(int l = 0; l < p_73807_.height; ++l) {
            for(int j1 = 0; j1 < p_73807_.width; ++j1) {
               BlockPos blockpos4 = p_73805_.relative(p_73806_.rotate(Direction.SOUTH), 8 + (l - this.startY) * 8);
               blockpos4 = blockpos4.relative(p_73806_.rotate(Direction.EAST), (j1 - this.startX) * 8);
               boolean flag2 = p_73808_ != null && WoodlandMansionPieces.MansionGrid.isHouse(p_73808_, j1, l);
               if (WoodlandMansionPieces.MansionGrid.isHouse(p_73807_, j1, l) && !flag2) {
                  if (!WoodlandMansionPieces.MansionGrid.isHouse(p_73807_, j1 + 1, l)) {
                     BlockPos blockpos16 = blockpos4.relative(p_73806_.rotate(Direction.EAST), 6);
                     if (!WoodlandMansionPieces.MansionGrid.isHouse(p_73807_, j1, l + 1)) {
                        BlockPos blockpos2 = blockpos16.relative(p_73806_.rotate(Direction.SOUTH), 6);
                        p_73804_.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "roof_corner", blockpos2, p_73806_));
                     } else if (WoodlandMansionPieces.MansionGrid.isHouse(p_73807_, j1 + 1, l + 1)) {
                        BlockPos blockpos18 = blockpos16.relative(p_73806_.rotate(Direction.SOUTH), 5);
                        p_73804_.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "roof_inner_corner", blockpos18, p_73806_));
                     }

                     if (!WoodlandMansionPieces.MansionGrid.isHouse(p_73807_, j1, l - 1)) {
                        p_73804_.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "roof_corner", blockpos16, p_73806_.getRotated(Rotation.COUNTERCLOCKWISE_90)));
                     } else if (WoodlandMansionPieces.MansionGrid.isHouse(p_73807_, j1 + 1, l - 1)) {
                        BlockPos blockpos19 = blockpos4.relative(p_73806_.rotate(Direction.EAST), 9);
                        blockpos19 = blockpos19.relative(p_73806_.rotate(Direction.NORTH), 2);
                        p_73804_.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "roof_inner_corner", blockpos19, p_73806_.getRotated(Rotation.CLOCKWISE_90)));
                     }
                  }

                  if (!WoodlandMansionPieces.MansionGrid.isHouse(p_73807_, j1 - 1, l)) {
                     BlockPos blockpos17 = blockpos4.relative(p_73806_.rotate(Direction.EAST), 0);
                     blockpos17 = blockpos17.relative(p_73806_.rotate(Direction.SOUTH), 0);
                     if (!WoodlandMansionPieces.MansionGrid.isHouse(p_73807_, j1, l + 1)) {
                        BlockPos blockpos20 = blockpos17.relative(p_73806_.rotate(Direction.SOUTH), 6);
                        p_73804_.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "roof_corner", blockpos20, p_73806_.getRotated(Rotation.CLOCKWISE_90)));
                     } else if (WoodlandMansionPieces.MansionGrid.isHouse(p_73807_, j1 - 1, l + 1)) {
                        BlockPos blockpos21 = blockpos17.relative(p_73806_.rotate(Direction.SOUTH), 8);
                        blockpos21 = blockpos21.relative(p_73806_.rotate(Direction.WEST), 3);
                        p_73804_.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "roof_inner_corner", blockpos21, p_73806_.getRotated(Rotation.COUNTERCLOCKWISE_90)));
                     }

                     if (!WoodlandMansionPieces.MansionGrid.isHouse(p_73807_, j1, l - 1)) {
                        p_73804_.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "roof_corner", blockpos17, p_73806_.getRotated(Rotation.CLOCKWISE_180)));
                     } else if (WoodlandMansionPieces.MansionGrid.isHouse(p_73807_, j1 - 1, l - 1)) {
                        BlockPos blockpos22 = blockpos17.relative(p_73806_.rotate(Direction.SOUTH), 1);
                        p_73804_.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "roof_inner_corner", blockpos22, p_73806_.getRotated(Rotation.CLOCKWISE_180)));
                     }
                  }
               }
            }
         }

      }

      private void entrance(List<WoodlandMansionPieces.WoodlandMansionPiece> p_73787_, WoodlandMansionPieces.PlacementData p_73788_) {
         Direction direction = p_73788_.rotation.rotate(Direction.WEST);
         p_73787_.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "entrance", p_73788_.position.relative(direction, 9), p_73788_.rotation));
         p_73788_.position = p_73788_.position.relative(p_73788_.rotation.rotate(Direction.SOUTH), 16);
      }

      private void traverseWallPiece(List<WoodlandMansionPieces.WoodlandMansionPiece> p_73831_, WoodlandMansionPieces.PlacementData p_73832_) {
         p_73831_.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, p_73832_.wallType, p_73832_.position.relative(p_73832_.rotation.rotate(Direction.EAST), 7), p_73832_.rotation));
         p_73832_.position = p_73832_.position.relative(p_73832_.rotation.rotate(Direction.SOUTH), 8);
      }

      private void traverseTurn(List<WoodlandMansionPieces.WoodlandMansionPiece> p_73834_, WoodlandMansionPieces.PlacementData p_73835_) {
         p_73835_.position = p_73835_.position.relative(p_73835_.rotation.rotate(Direction.SOUTH), -1);
         p_73834_.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "wall_corner", p_73835_.position, p_73835_.rotation));
         p_73835_.position = p_73835_.position.relative(p_73835_.rotation.rotate(Direction.SOUTH), -7);
         p_73835_.position = p_73835_.position.relative(p_73835_.rotation.rotate(Direction.WEST), -6);
         p_73835_.rotation = p_73835_.rotation.getRotated(Rotation.CLOCKWISE_90);
      }

      private void traverseInnerTurn(List<WoodlandMansionPieces.WoodlandMansionPiece> p_73837_, WoodlandMansionPieces.PlacementData p_73838_) {
         p_73838_.position = p_73838_.position.relative(p_73838_.rotation.rotate(Direction.SOUTH), 6);
         p_73838_.position = p_73838_.position.relative(p_73838_.rotation.rotate(Direction.EAST), 8);
         p_73838_.rotation = p_73838_.rotation.getRotated(Rotation.COUNTERCLOCKWISE_90);
      }

      private void addRoom1x1(List<WoodlandMansionPieces.WoodlandMansionPiece> p_73810_, BlockPos p_73811_, Rotation p_73812_, Direction p_73813_, WoodlandMansionPieces.FloorRoomCollection p_73814_) {
         Rotation rotation = Rotation.NONE;
         String s = p_73814_.get1x1(this.random);
         if (p_73813_ != Direction.EAST) {
            if (p_73813_ == Direction.NORTH) {
               rotation = rotation.getRotated(Rotation.COUNTERCLOCKWISE_90);
            } else if (p_73813_ == Direction.WEST) {
               rotation = rotation.getRotated(Rotation.CLOCKWISE_180);
            } else if (p_73813_ == Direction.SOUTH) {
               rotation = rotation.getRotated(Rotation.CLOCKWISE_90);
            } else {
               s = p_73814_.get1x1Secret(this.random);
            }
         }

         BlockPos blockpos = StructureTemplate.getZeroPositionWithTransform(new BlockPos(1, 0, 0), Mirror.NONE, rotation, 7, 7);
         rotation = rotation.getRotated(p_73812_);
         blockpos = blockpos.rotate(p_73812_);
         BlockPos blockpos1 = p_73811_.offset(blockpos.getX(), 0, blockpos.getZ());
         p_73810_.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, s, blockpos1, rotation));
      }

      private void addRoom1x2(List<WoodlandMansionPieces.WoodlandMansionPiece> p_73823_, BlockPos p_73824_, Rotation p_73825_, Direction p_73826_, Direction p_73827_, WoodlandMansionPieces.FloorRoomCollection p_73828_, boolean p_73829_) {
         if (p_73827_ == Direction.EAST && p_73826_ == Direction.SOUTH) {
            BlockPos blockpos13 = p_73824_.relative(p_73825_.rotate(Direction.EAST), 1);
            p_73823_.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, p_73828_.get1x2SideEntrance(this.random, p_73829_), blockpos13, p_73825_));
         } else if (p_73827_ == Direction.EAST && p_73826_ == Direction.NORTH) {
            BlockPos blockpos12 = p_73824_.relative(p_73825_.rotate(Direction.EAST), 1);
            blockpos12 = blockpos12.relative(p_73825_.rotate(Direction.SOUTH), 6);
            p_73823_.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, p_73828_.get1x2SideEntrance(this.random, p_73829_), blockpos12, p_73825_, Mirror.LEFT_RIGHT));
         } else if (p_73827_ == Direction.WEST && p_73826_ == Direction.NORTH) {
            BlockPos blockpos11 = p_73824_.relative(p_73825_.rotate(Direction.EAST), 7);
            blockpos11 = blockpos11.relative(p_73825_.rotate(Direction.SOUTH), 6);
            p_73823_.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, p_73828_.get1x2SideEntrance(this.random, p_73829_), blockpos11, p_73825_.getRotated(Rotation.CLOCKWISE_180)));
         } else if (p_73827_ == Direction.WEST && p_73826_ == Direction.SOUTH) {
            BlockPos blockpos10 = p_73824_.relative(p_73825_.rotate(Direction.EAST), 7);
            p_73823_.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, p_73828_.get1x2SideEntrance(this.random, p_73829_), blockpos10, p_73825_, Mirror.FRONT_BACK));
         } else if (p_73827_ == Direction.SOUTH && p_73826_ == Direction.EAST) {
            BlockPos blockpos9 = p_73824_.relative(p_73825_.rotate(Direction.EAST), 1);
            p_73823_.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, p_73828_.get1x2SideEntrance(this.random, p_73829_), blockpos9, p_73825_.getRotated(Rotation.CLOCKWISE_90), Mirror.LEFT_RIGHT));
         } else if (p_73827_ == Direction.SOUTH && p_73826_ == Direction.WEST) {
            BlockPos blockpos8 = p_73824_.relative(p_73825_.rotate(Direction.EAST), 7);
            p_73823_.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, p_73828_.get1x2SideEntrance(this.random, p_73829_), blockpos8, p_73825_.getRotated(Rotation.CLOCKWISE_90)));
         } else if (p_73827_ == Direction.NORTH && p_73826_ == Direction.WEST) {
            BlockPos blockpos7 = p_73824_.relative(p_73825_.rotate(Direction.EAST), 7);
            blockpos7 = blockpos7.relative(p_73825_.rotate(Direction.SOUTH), 6);
            p_73823_.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, p_73828_.get1x2SideEntrance(this.random, p_73829_), blockpos7, p_73825_.getRotated(Rotation.CLOCKWISE_90), Mirror.FRONT_BACK));
         } else if (p_73827_ == Direction.NORTH && p_73826_ == Direction.EAST) {
            BlockPos blockpos6 = p_73824_.relative(p_73825_.rotate(Direction.EAST), 1);
            blockpos6 = blockpos6.relative(p_73825_.rotate(Direction.SOUTH), 6);
            p_73823_.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, p_73828_.get1x2SideEntrance(this.random, p_73829_), blockpos6, p_73825_.getRotated(Rotation.COUNTERCLOCKWISE_90)));
         } else if (p_73827_ == Direction.SOUTH && p_73826_ == Direction.NORTH) {
            BlockPos blockpos5 = p_73824_.relative(p_73825_.rotate(Direction.EAST), 1);
            blockpos5 = blockpos5.relative(p_73825_.rotate(Direction.NORTH), 8);
            p_73823_.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, p_73828_.get1x2FrontEntrance(this.random, p_73829_), blockpos5, p_73825_));
         } else if (p_73827_ == Direction.NORTH && p_73826_ == Direction.SOUTH) {
            BlockPos blockpos4 = p_73824_.relative(p_73825_.rotate(Direction.EAST), 7);
            blockpos4 = blockpos4.relative(p_73825_.rotate(Direction.SOUTH), 14);
            p_73823_.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, p_73828_.get1x2FrontEntrance(this.random, p_73829_), blockpos4, p_73825_.getRotated(Rotation.CLOCKWISE_180)));
         } else if (p_73827_ == Direction.WEST && p_73826_ == Direction.EAST) {
            BlockPos blockpos3 = p_73824_.relative(p_73825_.rotate(Direction.EAST), 15);
            p_73823_.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, p_73828_.get1x2FrontEntrance(this.random, p_73829_), blockpos3, p_73825_.getRotated(Rotation.CLOCKWISE_90)));
         } else if (p_73827_ == Direction.EAST && p_73826_ == Direction.WEST) {
            BlockPos blockpos2 = p_73824_.relative(p_73825_.rotate(Direction.WEST), 7);
            blockpos2 = blockpos2.relative(p_73825_.rotate(Direction.SOUTH), 6);
            p_73823_.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, p_73828_.get1x2FrontEntrance(this.random, p_73829_), blockpos2, p_73825_.getRotated(Rotation.COUNTERCLOCKWISE_90)));
         } else if (p_73827_ == Direction.UP && p_73826_ == Direction.EAST) {
            BlockPos blockpos1 = p_73824_.relative(p_73825_.rotate(Direction.EAST), 15);
            p_73823_.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, p_73828_.get1x2Secret(this.random), blockpos1, p_73825_.getRotated(Rotation.CLOCKWISE_90)));
         } else if (p_73827_ == Direction.UP && p_73826_ == Direction.SOUTH) {
            BlockPos blockpos = p_73824_.relative(p_73825_.rotate(Direction.EAST), 1);
            blockpos = blockpos.relative(p_73825_.rotate(Direction.NORTH), 0);
            p_73823_.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, p_73828_.get1x2Secret(this.random), blockpos, p_73825_));
         }

      }

      private void addRoom2x2(List<WoodlandMansionPieces.WoodlandMansionPiece> p_73816_, BlockPos p_73817_, Rotation p_73818_, Direction p_73819_, Direction p_73820_, WoodlandMansionPieces.FloorRoomCollection p_73821_) {
         int i = 0;
         int j = 0;
         Rotation rotation = p_73818_;
         Mirror mirror = Mirror.NONE;
         if (p_73820_ == Direction.EAST && p_73819_ == Direction.SOUTH) {
            i = -7;
         } else if (p_73820_ == Direction.EAST && p_73819_ == Direction.NORTH) {
            i = -7;
            j = 6;
            mirror = Mirror.LEFT_RIGHT;
         } else if (p_73820_ == Direction.NORTH && p_73819_ == Direction.EAST) {
            i = 1;
            j = 14;
            rotation = p_73818_.getRotated(Rotation.COUNTERCLOCKWISE_90);
         } else if (p_73820_ == Direction.NORTH && p_73819_ == Direction.WEST) {
            i = 7;
            j = 14;
            rotation = p_73818_.getRotated(Rotation.COUNTERCLOCKWISE_90);
            mirror = Mirror.LEFT_RIGHT;
         } else if (p_73820_ == Direction.SOUTH && p_73819_ == Direction.WEST) {
            i = 7;
            j = -8;
            rotation = p_73818_.getRotated(Rotation.CLOCKWISE_90);
         } else if (p_73820_ == Direction.SOUTH && p_73819_ == Direction.EAST) {
            i = 1;
            j = -8;
            rotation = p_73818_.getRotated(Rotation.CLOCKWISE_90);
            mirror = Mirror.LEFT_RIGHT;
         } else if (p_73820_ == Direction.WEST && p_73819_ == Direction.NORTH) {
            i = 15;
            j = 6;
            rotation = p_73818_.getRotated(Rotation.CLOCKWISE_180);
         } else if (p_73820_ == Direction.WEST && p_73819_ == Direction.SOUTH) {
            i = 15;
            mirror = Mirror.FRONT_BACK;
         }

         BlockPos blockpos = p_73817_.relative(p_73818_.rotate(Direction.EAST), i);
         blockpos = blockpos.relative(p_73818_.rotate(Direction.SOUTH), j);
         p_73816_.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, p_73821_.get2x2(this.random), blockpos, rotation, mirror));
      }

      private void addRoom2x2Secret(List<WoodlandMansionPieces.WoodlandMansionPiece> p_73799_, BlockPos p_73800_, Rotation p_73801_, WoodlandMansionPieces.FloorRoomCollection p_73802_) {
         BlockPos blockpos = p_73800_.relative(p_73801_.rotate(Direction.EAST), 1);
         p_73799_.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, p_73802_.get2x2Secret(this.random), blockpos, p_73801_, Mirror.NONE));
      }
   }

   static class PlacementData {
      public Rotation rotation;
      public BlockPos position;
      public String wallType;
   }

   static class SecondFloorRoomCollection extends WoodlandMansionPieces.FloorRoomCollection {
      public String get1x1(Random p_73849_) {
         return "1x1_b" + (p_73849_.nextInt(4) + 1);
      }

      public String get1x1Secret(Random p_73854_) {
         return "1x1_as" + (p_73854_.nextInt(4) + 1);
      }

      public String get1x2SideEntrance(Random p_73851_, boolean p_73852_) {
         return p_73852_ ? "1x2_c_stairs" : "1x2_c" + (p_73851_.nextInt(4) + 1);
      }

      public String get1x2FrontEntrance(Random p_73856_, boolean p_73857_) {
         return p_73857_ ? "1x2_d_stairs" : "1x2_d" + (p_73856_.nextInt(5) + 1);
      }

      public String get1x2Secret(Random p_73859_) {
         return "1x2_se" + (p_73859_.nextInt(1) + 1);
      }

      public String get2x2(Random p_73861_) {
         return "2x2_b" + (p_73861_.nextInt(5) + 1);
      }

      public String get2x2Secret(Random p_73863_) {
         return "2x2_s1";
      }
   }

   static class SimpleGrid {
      private final int[][] grid;
      final int width;
      final int height;
      private final int valueIfOutside;

      public SimpleGrid(int p_73869_, int p_73870_, int p_73871_) {
         this.width = p_73869_;
         this.height = p_73870_;
         this.valueIfOutside = p_73871_;
         this.grid = new int[p_73869_][p_73870_];
      }

      public void set(int p_73876_, int p_73877_, int p_73878_) {
         if (p_73876_ >= 0 && p_73876_ < this.width && p_73877_ >= 0 && p_73877_ < this.height) {
            this.grid[p_73876_][p_73877_] = p_73878_;
         }

      }

      public void set(int p_73885_, int p_73886_, int p_73887_, int p_73888_, int p_73889_) {
         for(int i = p_73886_; i <= p_73888_; ++i) {
            for(int j = p_73885_; j <= p_73887_; ++j) {
               this.set(j, i, p_73889_);
            }
         }

      }

      public int get(int p_73873_, int p_73874_) {
         return p_73873_ >= 0 && p_73873_ < this.width && p_73874_ >= 0 && p_73874_ < this.height ? this.grid[p_73873_][p_73874_] : this.valueIfOutside;
      }

      public void setif(int p_73880_, int p_73881_, int p_73882_, int p_73883_) {
         if (this.get(p_73880_, p_73881_) == p_73882_) {
            this.set(p_73880_, p_73881_, p_73883_);
         }

      }

      public boolean edgesTo(int p_73893_, int p_73894_, int p_73895_) {
         return this.get(p_73893_ - 1, p_73894_) == p_73895_ || this.get(p_73893_ + 1, p_73894_) == p_73895_ || this.get(p_73893_, p_73894_ + 1) == p_73895_ || this.get(p_73893_, p_73894_ - 1) == p_73895_;
      }
   }

   static class ThirdFloorRoomCollection extends WoodlandMansionPieces.SecondFloorRoomCollection {
   }

   public static class WoodlandMansionPiece extends TemplateStructurePiece {
      public WoodlandMansionPiece(StructureManager p_73905_, String p_73906_, BlockPos p_73907_, Rotation p_73908_) {
         this(p_73905_, p_73906_, p_73907_, p_73908_, Mirror.NONE);
      }

      public WoodlandMansionPiece(StructureManager p_73910_, String p_73911_, BlockPos p_73912_, Rotation p_73913_, Mirror p_73914_) {
         super(StructurePieceType.WOODLAND_MANSION_PIECE, 0, p_73910_, makeLocation(p_73911_), p_73911_, makeSettings(p_73914_, p_73913_), p_73912_);
      }

      public WoodlandMansionPiece(StructureManager p_192693_, CompoundTag p_192694_) {
         super(StructurePieceType.WOODLAND_MANSION_PIECE, p_192694_, p_192693_, (p_163709_) -> {
            return makeSettings(Mirror.valueOf(p_192694_.getString("Mi")), Rotation.valueOf(p_192694_.getString("Rot")));
         });
      }

      protected ResourceLocation makeTemplateLocation() {
         return makeLocation(this.templateName);
      }

      private static ResourceLocation makeLocation(String p_163706_) {
         return new ResourceLocation("woodland_mansion/" + p_163706_);
      }

      private static StructurePlaceSettings makeSettings(Mirror p_163703_, Rotation p_163704_) {
         return (new StructurePlaceSettings()).setIgnoreEntities(true).setRotation(p_163704_).setMirror(p_163703_).addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK);
      }

      protected void addAdditionalSaveData(StructurePieceSerializationContext p_192696_, CompoundTag p_192697_) {
         super.addAdditionalSaveData(p_192696_, p_192697_);
         p_192697_.putString("Rot", this.placeSettings.getRotation().name());
         p_192697_.putString("Mi", this.placeSettings.getMirror().name());
      }

      protected void handleDataMarker(String p_73921_, BlockPos p_73922_, ServerLevelAccessor p_73923_, Random p_73924_, BoundingBox p_73925_) {
         if (p_73921_.startsWith("Chest")) {
            Rotation rotation = this.placeSettings.getRotation();
            BlockState blockstate = Blocks.CHEST.defaultBlockState();
            if ("ChestWest".equals(p_73921_)) {
               blockstate = blockstate.setValue(ChestBlock.FACING, rotation.rotate(Direction.WEST));
            } else if ("ChestEast".equals(p_73921_)) {
               blockstate = blockstate.setValue(ChestBlock.FACING, rotation.rotate(Direction.EAST));
            } else if ("ChestSouth".equals(p_73921_)) {
               blockstate = blockstate.setValue(ChestBlock.FACING, rotation.rotate(Direction.SOUTH));
            } else if ("ChestNorth".equals(p_73921_)) {
               blockstate = blockstate.setValue(ChestBlock.FACING, rotation.rotate(Direction.NORTH));
            }

            this.createChest(p_73923_, p_73925_, p_73924_, p_73922_, BuiltInLootTables.WOODLAND_MANSION, blockstate);
         } else {
            AbstractIllager abstractillager;
            switch(p_73921_) {
            case "Mage":
               abstractillager = EntityType.EVOKER.create(p_73923_.getLevel());
               break;
            case "Warrior":
               abstractillager = EntityType.VINDICATOR.create(p_73923_.getLevel());
               break;
            default:
               return;
            }

            abstractillager.setPersistenceRequired();
            abstractillager.moveTo(p_73922_, 0.0F, 0.0F);
            abstractillager.finalizeSpawn(p_73923_, p_73923_.getCurrentDifficultyAt(abstractillager.blockPosition()), MobSpawnType.STRUCTURE, (SpawnGroupData)null, (CompoundTag)null);
            p_73923_.addFreshEntityWithPassengers(abstractillager);
            p_73923_.setBlock(p_73922_, Blocks.AIR.defaultBlockState(), 2);
         }

      }
   }
}