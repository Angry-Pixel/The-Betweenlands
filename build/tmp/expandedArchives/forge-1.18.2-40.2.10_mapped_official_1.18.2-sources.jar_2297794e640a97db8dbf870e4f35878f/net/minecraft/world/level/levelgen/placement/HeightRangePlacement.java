package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.heightproviders.TrapezoidHeight;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;

public class HeightRangePlacement extends PlacementModifier {
   public static final Codec<HeightRangePlacement> CODEC = RecordCodecBuilder.create((p_191679_) -> {
      return p_191679_.group(HeightProvider.CODEC.fieldOf("height").forGetter((p_191686_) -> {
         return p_191686_.height;
      })).apply(p_191679_, HeightRangePlacement::new);
   });
   private final HeightProvider height;

   private HeightRangePlacement(HeightProvider p_191677_) {
      this.height = p_191677_;
   }

   public static HeightRangePlacement of(HeightProvider p_191684_) {
      return new HeightRangePlacement(p_191684_);
   }

   public static HeightRangePlacement uniform(VerticalAnchor p_191681_, VerticalAnchor p_191682_) {
      return of(UniformHeight.of(p_191681_, p_191682_));
   }

   public static HeightRangePlacement triangle(VerticalAnchor p_191693_, VerticalAnchor p_191694_) {
      return of(TrapezoidHeight.of(p_191693_, p_191694_));
   }

   public Stream<BlockPos> getPositions(PlacementContext p_191688_, Random p_191689_, BlockPos p_191690_) {
      return Stream.of(p_191690_.atY(this.height.sample(p_191689_, p_191688_)));
   }

   public PlacementModifierType<?> type() {
      return PlacementModifierType.HEIGHT_RANGE;
   }
}