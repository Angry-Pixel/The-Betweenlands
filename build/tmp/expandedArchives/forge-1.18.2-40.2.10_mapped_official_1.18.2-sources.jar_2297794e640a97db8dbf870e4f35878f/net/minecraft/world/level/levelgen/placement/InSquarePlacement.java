package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;

public class InSquarePlacement extends PlacementModifier {
   private static final InSquarePlacement INSTANCE = new InSquarePlacement();
   public static final Codec<InSquarePlacement> CODEC = Codec.unit(() -> {
      return INSTANCE;
   });

   public static InSquarePlacement spread() {
      return INSTANCE;
   }

   public Stream<BlockPos> getPositions(PlacementContext p_191717_, Random p_191718_, BlockPos p_191719_) {
      int i = p_191718_.nextInt(16) + p_191719_.getX();
      int j = p_191718_.nextInt(16) + p_191719_.getZ();
      return Stream.of(new BlockPos(i, p_191719_.getY(), j));
   }

   public PlacementModifierType<?> type() {
      return PlacementModifierType.IN_SQUARE;
   }
}