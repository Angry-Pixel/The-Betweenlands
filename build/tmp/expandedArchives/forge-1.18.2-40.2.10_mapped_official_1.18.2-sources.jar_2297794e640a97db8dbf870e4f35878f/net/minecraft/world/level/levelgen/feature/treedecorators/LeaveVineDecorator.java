package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.levelgen.feature.Feature;

public class LeaveVineDecorator extends TreeDecorator {
   public static final Codec<LeaveVineDecorator> CODEC = Codec.unit(() -> {
      return LeaveVineDecorator.INSTANCE;
   });
   public static final LeaveVineDecorator INSTANCE = new LeaveVineDecorator();

   protected TreeDecoratorType<?> type() {
      return TreeDecoratorType.LEAVE_VINE;
   }

   public void place(LevelSimulatedReader p_161735_, BiConsumer<BlockPos, BlockState> p_161736_, Random p_161737_, List<BlockPos> p_161738_, List<BlockPos> p_161739_) {
      p_161739_.forEach((p_161744_) -> {
         if (p_161737_.nextInt(4) == 0) {
            BlockPos blockpos = p_161744_.west();
            if (Feature.isAir(p_161735_, blockpos)) {
               addHangingVine(p_161735_, blockpos, VineBlock.EAST, p_161736_);
            }
         }

         if (p_161737_.nextInt(4) == 0) {
            BlockPos blockpos1 = p_161744_.east();
            if (Feature.isAir(p_161735_, blockpos1)) {
               addHangingVine(p_161735_, blockpos1, VineBlock.WEST, p_161736_);
            }
         }

         if (p_161737_.nextInt(4) == 0) {
            BlockPos blockpos2 = p_161744_.north();
            if (Feature.isAir(p_161735_, blockpos2)) {
               addHangingVine(p_161735_, blockpos2, VineBlock.SOUTH, p_161736_);
            }
         }

         if (p_161737_.nextInt(4) == 0) {
            BlockPos blockpos3 = p_161744_.south();
            if (Feature.isAir(p_161735_, blockpos3)) {
               addHangingVine(p_161735_, blockpos3, VineBlock.NORTH, p_161736_);
            }
         }

      });
   }

   private static void addHangingVine(LevelSimulatedReader p_161730_, BlockPos p_161731_, BooleanProperty p_161732_, BiConsumer<BlockPos, BlockState> p_161733_) {
      placeVine(p_161733_, p_161731_, p_161732_);
      int i = 4;

      for(BlockPos blockpos = p_161731_.below(); Feature.isAir(p_161730_, blockpos) && i > 0; --i) {
         placeVine(p_161733_, blockpos, p_161732_);
         blockpos = blockpos.below();
      }

   }
}