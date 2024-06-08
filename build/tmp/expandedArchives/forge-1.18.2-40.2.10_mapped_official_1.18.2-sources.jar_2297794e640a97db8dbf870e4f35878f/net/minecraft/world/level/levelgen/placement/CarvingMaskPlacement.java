package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.GenerationStep;

public class CarvingMaskPlacement extends PlacementModifier {
   public static final Codec<CarvingMaskPlacement> CODEC = GenerationStep.Carving.CODEC.fieldOf("step").xmap(CarvingMaskPlacement::new, (p_191593_) -> {
      return p_191593_.step;
   }).codec();
   private final GenerationStep.Carving step;

   private CarvingMaskPlacement(GenerationStep.Carving p_191589_) {
      this.step = p_191589_;
   }

   public static CarvingMaskPlacement forStep(GenerationStep.Carving p_191591_) {
      return new CarvingMaskPlacement(p_191591_);
   }

   public Stream<BlockPos> getPositions(PlacementContext p_191595_, Random p_191596_, BlockPos p_191597_) {
      ChunkPos chunkpos = new ChunkPos(p_191597_);
      return p_191595_.getCarvingMask(chunkpos, this.step).stream(chunkpos);
   }

   public PlacementModifierType<?> type() {
      return PlacementModifierType.CARVING_MASK_PLACEMENT;
   }
}