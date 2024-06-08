package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.WorldGenLevel;

public class HasSturdyFacePredicate implements BlockPredicate {
   private final Vec3i offset;
   private final Direction direction;
   public static final Codec<HasSturdyFacePredicate> CODEC = RecordCodecBuilder.create((p_198327_) -> {
      return p_198327_.group(Vec3i.offsetCodec(16).optionalFieldOf("offset", Vec3i.ZERO).forGetter((p_198331_) -> {
         return p_198331_.offset;
      }), Direction.CODEC.fieldOf("direction").forGetter((p_198329_) -> {
         return p_198329_.direction;
      })).apply(p_198327_, HasSturdyFacePredicate::new);
   });

   public HasSturdyFacePredicate(Vec3i p_198320_, Direction p_198321_) {
      this.offset = p_198320_;
      this.direction = p_198321_;
   }

   public boolean test(WorldGenLevel p_198324_, BlockPos p_198325_) {
      BlockPos blockpos = p_198325_.offset(this.offset);
      return p_198324_.getBlockState(blockpos).isFaceSturdy(p_198324_, blockpos, this.direction);
   }

   public BlockPredicateType<?> type() {
      return BlockPredicateType.HAS_STURDY_FACE;
   }
}