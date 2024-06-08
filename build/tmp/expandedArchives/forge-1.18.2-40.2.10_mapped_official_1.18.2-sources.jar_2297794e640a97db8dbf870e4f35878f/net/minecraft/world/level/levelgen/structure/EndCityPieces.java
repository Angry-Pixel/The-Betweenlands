package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class EndCityPieces {
   private static final int MAX_GEN_DEPTH = 8;
   static final EndCityPieces.SectionGenerator HOUSE_TOWER_GENERATOR = new EndCityPieces.SectionGenerator() {
      public void init() {
      }

      public boolean generate(StructureManager p_71161_, int p_71162_, EndCityPieces.EndCityPiece p_71163_, BlockPos p_71164_, List<StructurePiece> p_71165_, Random p_71166_) {
         if (p_71162_ > 8) {
            return false;
         } else {
            Rotation rotation = p_71163_.placeSettings.getRotation();
            EndCityPieces.EndCityPiece endcitypieces$endcitypiece = EndCityPieces.addHelper(p_71165_, EndCityPieces.addPiece(p_71161_, p_71163_, p_71164_, "base_floor", rotation, true));
            int i = p_71166_.nextInt(3);
            if (i == 0) {
               EndCityPieces.addHelper(p_71165_, EndCityPieces.addPiece(p_71161_, endcitypieces$endcitypiece, new BlockPos(-1, 4, -1), "base_roof", rotation, true));
            } else if (i == 1) {
               endcitypieces$endcitypiece = EndCityPieces.addHelper(p_71165_, EndCityPieces.addPiece(p_71161_, endcitypieces$endcitypiece, new BlockPos(-1, 0, -1), "second_floor_2", rotation, false));
               endcitypieces$endcitypiece = EndCityPieces.addHelper(p_71165_, EndCityPieces.addPiece(p_71161_, endcitypieces$endcitypiece, new BlockPos(-1, 8, -1), "second_roof", rotation, false));
               EndCityPieces.recursiveChildren(p_71161_, EndCityPieces.TOWER_GENERATOR, p_71162_ + 1, endcitypieces$endcitypiece, (BlockPos)null, p_71165_, p_71166_);
            } else if (i == 2) {
               endcitypieces$endcitypiece = EndCityPieces.addHelper(p_71165_, EndCityPieces.addPiece(p_71161_, endcitypieces$endcitypiece, new BlockPos(-1, 0, -1), "second_floor_2", rotation, false));
               endcitypieces$endcitypiece = EndCityPieces.addHelper(p_71165_, EndCityPieces.addPiece(p_71161_, endcitypieces$endcitypiece, new BlockPos(-1, 4, -1), "third_floor_2", rotation, false));
               endcitypieces$endcitypiece = EndCityPieces.addHelper(p_71165_, EndCityPieces.addPiece(p_71161_, endcitypieces$endcitypiece, new BlockPos(-1, 8, -1), "third_roof", rotation, true));
               EndCityPieces.recursiveChildren(p_71161_, EndCityPieces.TOWER_GENERATOR, p_71162_ + 1, endcitypieces$endcitypiece, (BlockPos)null, p_71165_, p_71166_);
            }

            return true;
         }
      }
   };
   static final List<Tuple<Rotation, BlockPos>> TOWER_BRIDGES = Lists.newArrayList(new Tuple<>(Rotation.NONE, new BlockPos(1, -1, 0)), new Tuple<>(Rotation.CLOCKWISE_90, new BlockPos(6, -1, 1)), new Tuple<>(Rotation.COUNTERCLOCKWISE_90, new BlockPos(0, -1, 5)), new Tuple<>(Rotation.CLOCKWISE_180, new BlockPos(5, -1, 6)));
   static final EndCityPieces.SectionGenerator TOWER_GENERATOR = new EndCityPieces.SectionGenerator() {
      public void init() {
      }

      public boolean generate(StructureManager p_71170_, int p_71171_, EndCityPieces.EndCityPiece p_71172_, BlockPos p_71173_, List<StructurePiece> p_71174_, Random p_71175_) {
         Rotation rotation = p_71172_.placeSettings.getRotation();
         EndCityPieces.EndCityPiece $$7 = EndCityPieces.addHelper(p_71174_, EndCityPieces.addPiece(p_71170_, p_71172_, new BlockPos(3 + p_71175_.nextInt(2), -3, 3 + p_71175_.nextInt(2)), "tower_base", rotation, true));
         $$7 = EndCityPieces.addHelper(p_71174_, EndCityPieces.addPiece(p_71170_, $$7, new BlockPos(0, 7, 0), "tower_piece", rotation, true));
         EndCityPieces.EndCityPiece endcitypieces$endcitypiece1 = p_71175_.nextInt(3) == 0 ? $$7 : null;
         int i = 1 + p_71175_.nextInt(3);

         for(int j = 0; j < i; ++j) {
            $$7 = EndCityPieces.addHelper(p_71174_, EndCityPieces.addPiece(p_71170_, $$7, new BlockPos(0, 4, 0), "tower_piece", rotation, true));
            if (j < i - 1 && p_71175_.nextBoolean()) {
               endcitypieces$endcitypiece1 = $$7;
            }
         }

         if (endcitypieces$endcitypiece1 != null) {
            for(Tuple<Rotation, BlockPos> tuple : EndCityPieces.TOWER_BRIDGES) {
               if (p_71175_.nextBoolean()) {
                  EndCityPieces.EndCityPiece endcitypieces$endcitypiece2 = EndCityPieces.addHelper(p_71174_, EndCityPieces.addPiece(p_71170_, endcitypieces$endcitypiece1, tuple.getB(), "bridge_end", rotation.getRotated(tuple.getA()), true));
                  EndCityPieces.recursiveChildren(p_71170_, EndCityPieces.TOWER_BRIDGE_GENERATOR, p_71171_ + 1, endcitypieces$endcitypiece2, (BlockPos)null, p_71174_, p_71175_);
               }
            }

            EndCityPieces.addHelper(p_71174_, EndCityPieces.addPiece(p_71170_, $$7, new BlockPos(-1, 4, -1), "tower_top", rotation, true));
         } else {
            if (p_71171_ != 7) {
               return EndCityPieces.recursiveChildren(p_71170_, EndCityPieces.FAT_TOWER_GENERATOR, p_71171_ + 1, $$7, (BlockPos)null, p_71174_, p_71175_);
            }

            EndCityPieces.addHelper(p_71174_, EndCityPieces.addPiece(p_71170_, $$7, new BlockPos(-1, 4, -1), "tower_top", rotation, true));
         }

         return true;
      }
   };
   static final EndCityPieces.SectionGenerator TOWER_BRIDGE_GENERATOR = new EndCityPieces.SectionGenerator() {
      public boolean shipCreated;

      public void init() {
         this.shipCreated = false;
      }

      public boolean generate(StructureManager p_71180_, int p_71181_, EndCityPieces.EndCityPiece p_71182_, BlockPos p_71183_, List<StructurePiece> p_71184_, Random p_71185_) {
         Rotation rotation = p_71182_.placeSettings.getRotation();
         int i = p_71185_.nextInt(4) + 1;
         EndCityPieces.EndCityPiece endcitypieces$endcitypiece = EndCityPieces.addHelper(p_71184_, EndCityPieces.addPiece(p_71180_, p_71182_, new BlockPos(0, 0, -4), "bridge_piece", rotation, true));
         endcitypieces$endcitypiece.genDepth = -1;
         int j = 0;

         for(int k = 0; k < i; ++k) {
            if (p_71185_.nextBoolean()) {
               endcitypieces$endcitypiece = EndCityPieces.addHelper(p_71184_, EndCityPieces.addPiece(p_71180_, endcitypieces$endcitypiece, new BlockPos(0, j, -4), "bridge_piece", rotation, true));
               j = 0;
            } else {
               if (p_71185_.nextBoolean()) {
                  endcitypieces$endcitypiece = EndCityPieces.addHelper(p_71184_, EndCityPieces.addPiece(p_71180_, endcitypieces$endcitypiece, new BlockPos(0, j, -4), "bridge_steep_stairs", rotation, true));
               } else {
                  endcitypieces$endcitypiece = EndCityPieces.addHelper(p_71184_, EndCityPieces.addPiece(p_71180_, endcitypieces$endcitypiece, new BlockPos(0, j, -8), "bridge_gentle_stairs", rotation, true));
               }

               j = 4;
            }
         }

         if (!this.shipCreated && p_71185_.nextInt(10 - p_71181_) == 0) {
            EndCityPieces.addHelper(p_71184_, EndCityPieces.addPiece(p_71180_, endcitypieces$endcitypiece, new BlockPos(-8 + p_71185_.nextInt(8), j, -70 + p_71185_.nextInt(10)), "ship", rotation, true));
            this.shipCreated = true;
         } else if (!EndCityPieces.recursiveChildren(p_71180_, EndCityPieces.HOUSE_TOWER_GENERATOR, p_71181_ + 1, endcitypieces$endcitypiece, new BlockPos(-3, j + 1, -11), p_71184_, p_71185_)) {
            return false;
         }

         endcitypieces$endcitypiece = EndCityPieces.addHelper(p_71184_, EndCityPieces.addPiece(p_71180_, endcitypieces$endcitypiece, new BlockPos(4, j, 0), "bridge_end", rotation.getRotated(Rotation.CLOCKWISE_180), true));
         endcitypieces$endcitypiece.genDepth = -1;
         return true;
      }
   };
   static final List<Tuple<Rotation, BlockPos>> FAT_TOWER_BRIDGES = Lists.newArrayList(new Tuple<>(Rotation.NONE, new BlockPos(4, -1, 0)), new Tuple<>(Rotation.CLOCKWISE_90, new BlockPos(12, -1, 4)), new Tuple<>(Rotation.COUNTERCLOCKWISE_90, new BlockPos(0, -1, 8)), new Tuple<>(Rotation.CLOCKWISE_180, new BlockPos(8, -1, 12)));
   static final EndCityPieces.SectionGenerator FAT_TOWER_GENERATOR = new EndCityPieces.SectionGenerator() {
      public void init() {
      }

      public boolean generate(StructureManager p_71189_, int p_71190_, EndCityPieces.EndCityPiece p_71191_, BlockPos p_71192_, List<StructurePiece> p_71193_, Random p_71194_) {
         Rotation rotation = p_71191_.placeSettings.getRotation();
         EndCityPieces.EndCityPiece endcitypieces$endcitypiece = EndCityPieces.addHelper(p_71193_, EndCityPieces.addPiece(p_71189_, p_71191_, new BlockPos(-3, 4, -3), "fat_tower_base", rotation, true));
         endcitypieces$endcitypiece = EndCityPieces.addHelper(p_71193_, EndCityPieces.addPiece(p_71189_, endcitypieces$endcitypiece, new BlockPos(0, 4, 0), "fat_tower_middle", rotation, true));

         for(int i = 0; i < 2 && p_71194_.nextInt(3) != 0; ++i) {
            endcitypieces$endcitypiece = EndCityPieces.addHelper(p_71193_, EndCityPieces.addPiece(p_71189_, endcitypieces$endcitypiece, new BlockPos(0, 8, 0), "fat_tower_middle", rotation, true));

            for(Tuple<Rotation, BlockPos> tuple : EndCityPieces.FAT_TOWER_BRIDGES) {
               if (p_71194_.nextBoolean()) {
                  EndCityPieces.EndCityPiece endcitypieces$endcitypiece1 = EndCityPieces.addHelper(p_71193_, EndCityPieces.addPiece(p_71189_, endcitypieces$endcitypiece, tuple.getB(), "bridge_end", rotation.getRotated(tuple.getA()), true));
                  EndCityPieces.recursiveChildren(p_71189_, EndCityPieces.TOWER_BRIDGE_GENERATOR, p_71190_ + 1, endcitypieces$endcitypiece1, (BlockPos)null, p_71193_, p_71194_);
               }
            }
         }

         EndCityPieces.addHelper(p_71193_, EndCityPieces.addPiece(p_71189_, endcitypieces$endcitypiece, new BlockPos(-2, 8, -2), "fat_tower_top", rotation, true));
         return true;
      }
   };

   static EndCityPieces.EndCityPiece addPiece(StructureManager p_71135_, EndCityPieces.EndCityPiece p_71136_, BlockPos p_71137_, String p_71138_, Rotation p_71139_, boolean p_71140_) {
      EndCityPieces.EndCityPiece endcitypieces$endcitypiece = new EndCityPieces.EndCityPiece(p_71135_, p_71138_, p_71136_.templatePosition, p_71139_, p_71140_);
      BlockPos blockpos = p_71136_.template.calculateConnectedPosition(p_71136_.placeSettings, p_71137_, endcitypieces$endcitypiece.placeSettings, BlockPos.ZERO);
      endcitypieces$endcitypiece.move(blockpos.getX(), blockpos.getY(), blockpos.getZ());
      return endcitypieces$endcitypiece;
   }

   public static void startHouseTower(StructureManager p_71125_, BlockPos p_71126_, Rotation p_71127_, List<StructurePiece> p_71128_, Random p_71129_) {
      FAT_TOWER_GENERATOR.init();
      HOUSE_TOWER_GENERATOR.init();
      TOWER_BRIDGE_GENERATOR.init();
      TOWER_GENERATOR.init();
      EndCityPieces.EndCityPiece endcitypieces$endcitypiece = addHelper(p_71128_, new EndCityPieces.EndCityPiece(p_71125_, "base_floor", p_71126_, p_71127_, true));
      endcitypieces$endcitypiece = addHelper(p_71128_, addPiece(p_71125_, endcitypieces$endcitypiece, new BlockPos(-1, 0, -1), "second_floor_1", p_71127_, false));
      endcitypieces$endcitypiece = addHelper(p_71128_, addPiece(p_71125_, endcitypieces$endcitypiece, new BlockPos(-1, 4, -1), "third_floor_1", p_71127_, false));
      endcitypieces$endcitypiece = addHelper(p_71128_, addPiece(p_71125_, endcitypieces$endcitypiece, new BlockPos(-1, 8, -1), "third_roof", p_71127_, true));
      recursiveChildren(p_71125_, TOWER_GENERATOR, 1, endcitypieces$endcitypiece, (BlockPos)null, p_71128_, p_71129_);
   }

   static EndCityPieces.EndCityPiece addHelper(List<StructurePiece> p_71150_, EndCityPieces.EndCityPiece p_71151_) {
      p_71150_.add(p_71151_);
      return p_71151_;
   }

   static boolean recursiveChildren(StructureManager p_71142_, EndCityPieces.SectionGenerator p_71143_, int p_71144_, EndCityPieces.EndCityPiece p_71145_, BlockPos p_71146_, List<StructurePiece> p_71147_, Random p_71148_) {
      if (p_71144_ > 8) {
         return false;
      } else {
         List<StructurePiece> list = Lists.newArrayList();
         if (p_71143_.generate(p_71142_, p_71144_, p_71145_, p_71146_, list, p_71148_)) {
            boolean flag = false;
            int i = p_71148_.nextInt();

            for(StructurePiece structurepiece : list) {
               structurepiece.genDepth = i;
               StructurePiece structurepiece1 = StructurePiece.findCollisionPiece(p_71147_, structurepiece.getBoundingBox());
               if (structurepiece1 != null && structurepiece1.genDepth != p_71145_.genDepth) {
                  flag = true;
                  break;
               }
            }

            if (!flag) {
               p_71147_.addAll(list);
               return true;
            }
         }

         return false;
      }
   }

   public static class EndCityPiece extends TemplateStructurePiece {
      public EndCityPiece(StructureManager p_71199_, String p_71200_, BlockPos p_71201_, Rotation p_71202_, boolean p_71203_) {
         super(StructurePieceType.END_CITY_PIECE, 0, p_71199_, makeResourceLocation(p_71200_), p_71200_, makeSettings(p_71203_, p_71202_), p_71201_);
      }

      public EndCityPiece(StructureManager p_191992_, CompoundTag p_191993_) {
         super(StructurePieceType.END_CITY_PIECE, p_191993_, p_191992_, (p_162428_) -> {
            return makeSettings(p_191993_.getBoolean("OW"), Rotation.valueOf(p_191993_.getString("Rot")));
         });
      }

      private static StructurePlaceSettings makeSettings(boolean p_162430_, Rotation p_162431_) {
         BlockIgnoreProcessor blockignoreprocessor = p_162430_ ? BlockIgnoreProcessor.STRUCTURE_BLOCK : BlockIgnoreProcessor.STRUCTURE_AND_AIR;
         return (new StructurePlaceSettings()).setIgnoreEntities(true).addProcessor(blockignoreprocessor).setRotation(p_162431_);
      }

      protected ResourceLocation makeTemplateLocation() {
         return makeResourceLocation(this.templateName);
      }

      private static ResourceLocation makeResourceLocation(String p_162425_) {
         return new ResourceLocation("end_city/" + p_162425_);
      }

      protected void addAdditionalSaveData(StructurePieceSerializationContext p_191995_, CompoundTag p_191996_) {
         super.addAdditionalSaveData(p_191995_, p_191996_);
         p_191996_.putString("Rot", this.placeSettings.getRotation().name());
         p_191996_.putBoolean("OW", this.placeSettings.getProcessors().get(0) == BlockIgnoreProcessor.STRUCTURE_BLOCK);
      }

      protected void handleDataMarker(String p_71210_, BlockPos p_71211_, ServerLevelAccessor p_71212_, Random p_71213_, BoundingBox p_71214_) {
         if (p_71210_.startsWith("Chest")) {
            BlockPos blockpos = p_71211_.below();
            if (p_71214_.isInside(blockpos)) {
               RandomizableContainerBlockEntity.setLootTable(p_71212_, p_71213_, blockpos, BuiltInLootTables.END_CITY_TREASURE);
            }
         } else if (p_71214_.isInside(p_71211_) && Level.isInSpawnableBounds(p_71211_)) {
            if (p_71210_.startsWith("Sentry")) {
               Shulker shulker = EntityType.SHULKER.create(p_71212_.getLevel());
               shulker.setPos((double)p_71211_.getX() + 0.5D, (double)p_71211_.getY(), (double)p_71211_.getZ() + 0.5D);
               p_71212_.addFreshEntity(shulker);
            } else if (p_71210_.startsWith("Elytra")) {
               ItemFrame itemframe = new ItemFrame(p_71212_.getLevel(), p_71211_, this.placeSettings.getRotation().rotate(Direction.SOUTH));
               itemframe.setItem(new ItemStack(Items.ELYTRA), false);
               p_71212_.addFreshEntity(itemframe);
            }
         }

      }
   }

   interface SectionGenerator {
      void init();

      boolean generate(StructureManager p_71217_, int p_71218_, EndCityPieces.EndCityPiece p_71219_, BlockPos p_71220_, List<StructurePiece> p_71221_, Random p_71222_);
   }
}