package net.minecraft.world.level.levelgen.feature;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import org.slf4j.Logger;

public class MonsterRoomFeature extends Feature<NoneFeatureConfiguration> {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final EntityType<?>[] MOBS = new EntityType[]{EntityType.SKELETON, EntityType.ZOMBIE, EntityType.ZOMBIE, EntityType.SPIDER};
   private static final BlockState AIR = Blocks.CAVE_AIR.defaultBlockState();

   public MonsterRoomFeature(Codec<NoneFeatureConfiguration> p_66345_) {
      super(p_66345_);
   }

   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> p_160066_) {
      Predicate<BlockState> predicate = Feature.isReplaceable(BlockTags.FEATURES_CANNOT_REPLACE);
      BlockPos blockpos = p_160066_.origin();
      Random random = p_160066_.random();
      WorldGenLevel worldgenlevel = p_160066_.level();
      int i = 3;
      int j = random.nextInt(2) + 2;
      int k = -j - 1;
      int l = j + 1;
      int i1 = -1;
      int j1 = 4;
      int k1 = random.nextInt(2) + 2;
      int l1 = -k1 - 1;
      int i2 = k1 + 1;
      int j2 = 0;

      for(int k2 = k; k2 <= l; ++k2) {
         for(int l2 = -1; l2 <= 4; ++l2) {
            for(int i3 = l1; i3 <= i2; ++i3) {
               BlockPos blockpos1 = blockpos.offset(k2, l2, i3);
               Material material = worldgenlevel.getBlockState(blockpos1).getMaterial();
               boolean flag = material.isSolid();
               if (l2 == -1 && !flag) {
                  return false;
               }

               if (l2 == 4 && !flag) {
                  return false;
               }

               if ((k2 == k || k2 == l || i3 == l1 || i3 == i2) && l2 == 0 && worldgenlevel.isEmptyBlock(blockpos1) && worldgenlevel.isEmptyBlock(blockpos1.above())) {
                  ++j2;
               }
            }
         }
      }

      if (j2 >= 1 && j2 <= 5) {
         for(int k3 = k; k3 <= l; ++k3) {
            for(int i4 = 3; i4 >= -1; --i4) {
               for(int k4 = l1; k4 <= i2; ++k4) {
                  BlockPos blockpos2 = blockpos.offset(k3, i4, k4);
                  BlockState blockstate = worldgenlevel.getBlockState(blockpos2);
                  if (k3 != k && i4 != -1 && k4 != l1 && k3 != l && i4 != 4 && k4 != i2) {
                     if (!blockstate.is(Blocks.CHEST) && !blockstate.is(Blocks.SPAWNER)) {
                        this.safeSetBlock(worldgenlevel, blockpos2, AIR, predicate);
                     }
                  } else if (blockpos2.getY() >= worldgenlevel.getMinBuildHeight() && !worldgenlevel.getBlockState(blockpos2.below()).getMaterial().isSolid()) {
                     worldgenlevel.setBlock(blockpos2, AIR, 2);
                  } else if (blockstate.getMaterial().isSolid() && !blockstate.is(Blocks.CHEST)) {
                     if (i4 == -1 && random.nextInt(4) != 0) {
                        this.safeSetBlock(worldgenlevel, blockpos2, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), predicate);
                     } else {
                        this.safeSetBlock(worldgenlevel, blockpos2, Blocks.COBBLESTONE.defaultBlockState(), predicate);
                     }
                  }
               }
            }
         }

         for(int l3 = 0; l3 < 2; ++l3) {
            for(int j4 = 0; j4 < 3; ++j4) {
               int l4 = blockpos.getX() + random.nextInt(j * 2 + 1) - j;
               int i5 = blockpos.getY();
               int j5 = blockpos.getZ() + random.nextInt(k1 * 2 + 1) - k1;
               BlockPos blockpos3 = new BlockPos(l4, i5, j5);
               if (worldgenlevel.isEmptyBlock(blockpos3)) {
                  int j3 = 0;

                  for(Direction direction : Direction.Plane.HORIZONTAL) {
                     if (worldgenlevel.getBlockState(blockpos3.relative(direction)).getMaterial().isSolid()) {
                        ++j3;
                     }
                  }

                  if (j3 == 1) {
                     this.safeSetBlock(worldgenlevel, blockpos3, StructurePiece.reorient(worldgenlevel, blockpos3, Blocks.CHEST.defaultBlockState()), predicate);
                     RandomizableContainerBlockEntity.setLootTable(worldgenlevel, random, blockpos3, BuiltInLootTables.SIMPLE_DUNGEON);
                     break;
                  }
               }
            }
         }

         this.safeSetBlock(worldgenlevel, blockpos, Blocks.SPAWNER.defaultBlockState(), predicate);
         BlockEntity blockentity = worldgenlevel.getBlockEntity(blockpos);
         if (blockentity instanceof SpawnerBlockEntity) {
            ((SpawnerBlockEntity)blockentity).getSpawner().setEntityId(this.randomEntityId(random));
         } else {
            LOGGER.error("Failed to fetch mob spawner entity at ({}, {}, {})", blockpos.getX(), blockpos.getY(), blockpos.getZ());
         }

         return true;
      } else {
         return false;
      }
   }

   private EntityType<?> randomEntityId(Random p_66359_) {
      return net.minecraftforge.common.DungeonHooks.getRandomDungeonMob(p_66359_);
   }
}
