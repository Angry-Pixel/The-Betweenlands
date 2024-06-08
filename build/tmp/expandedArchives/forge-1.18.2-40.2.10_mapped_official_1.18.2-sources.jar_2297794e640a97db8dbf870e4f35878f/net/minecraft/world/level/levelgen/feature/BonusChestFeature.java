package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class BonusChestFeature extends Feature<NoneFeatureConfiguration> {
   public BonusChestFeature(Codec<NoneFeatureConfiguration> p_65299_) {
      super(p_65299_);
   }

   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> p_159477_) {
      Random random = p_159477_.random();
      WorldGenLevel worldgenlevel = p_159477_.level();
      ChunkPos chunkpos = new ChunkPos(p_159477_.origin());
      List<Integer> list = IntStream.rangeClosed(chunkpos.getMinBlockX(), chunkpos.getMaxBlockX()).boxed().collect(Collectors.toList());
      Collections.shuffle(list, random);
      List<Integer> list1 = IntStream.rangeClosed(chunkpos.getMinBlockZ(), chunkpos.getMaxBlockZ()).boxed().collect(Collectors.toList());
      Collections.shuffle(list1, random);
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(Integer integer : list) {
         for(Integer integer1 : list1) {
            blockpos$mutableblockpos.set(integer, 0, integer1);
            BlockPos blockpos = worldgenlevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockpos$mutableblockpos);
            if (worldgenlevel.isEmptyBlock(blockpos) || worldgenlevel.getBlockState(blockpos).getCollisionShape(worldgenlevel, blockpos).isEmpty()) {
               worldgenlevel.setBlock(blockpos, Blocks.CHEST.defaultBlockState(), 2);
               RandomizableContainerBlockEntity.setLootTable(worldgenlevel, random, blockpos, BuiltInLootTables.SPAWN_BONUS_CHEST);
               BlockState blockstate = Blocks.TORCH.defaultBlockState();

               for(Direction direction : Direction.Plane.HORIZONTAL) {
                  BlockPos blockpos1 = blockpos.relative(direction);
                  if (blockstate.canSurvive(worldgenlevel, blockpos1)) {
                     worldgenlevel.setBlock(blockpos1, blockstate, 2);
                  }
               }

               return true;
            }
         }
      }

      return false;
   }
}