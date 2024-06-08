package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;

public class TrunkVineDecorator extends TreeDecorator {
   public static final Codec<TrunkVineDecorator> CODEC = Codec.unit(() -> {
      return TrunkVineDecorator.INSTANCE;
   });
   public static final TrunkVineDecorator INSTANCE = new TrunkVineDecorator();

   protected TreeDecoratorType<?> type() {
      return TreeDecoratorType.TRUNK_VINE;
   }

   public void place(LevelSimulatedReader p_161755_, BiConsumer<BlockPos, BlockState> p_161756_, Random p_161757_, List<BlockPos> p_161758_, List<BlockPos> p_161759_) {
      p_161758_.forEach((p_161764_) -> {
         if (p_161757_.nextInt(3) > 0) {
            BlockPos blockpos = p_161764_.west();
            if (Feature.isAir(p_161755_, blockpos)) {
               placeVine(p_161756_, blockpos, VineBlock.EAST);
            }
         }

         if (p_161757_.nextInt(3) > 0) {
            BlockPos blockpos1 = p_161764_.east();
            if (Feature.isAir(p_161755_, blockpos1)) {
               placeVine(p_161756_, blockpos1, VineBlock.WEST);
            }
         }

         if (p_161757_.nextInt(3) > 0) {
            BlockPos blockpos2 = p_161764_.north();
            if (Feature.isAir(p_161755_, blockpos2)) {
               placeVine(p_161756_, blockpos2, VineBlock.SOUTH);
            }
         }

         if (p_161757_.nextInt(3) > 0) {
            BlockPos blockpos3 = p_161764_.south();
            if (Feature.isAir(p_161755_, blockpos3)) {
               placeVine(p_161756_, blockpos3, VineBlock.NORTH);
            }
         }

      });
   }
}