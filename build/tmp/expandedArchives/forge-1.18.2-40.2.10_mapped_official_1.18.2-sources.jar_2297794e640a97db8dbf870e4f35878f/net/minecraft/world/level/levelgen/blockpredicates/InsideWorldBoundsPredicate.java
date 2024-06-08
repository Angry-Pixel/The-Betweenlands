package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.WorldGenLevel;

public class InsideWorldBoundsPredicate implements BlockPredicate {
   public static final Codec<InsideWorldBoundsPredicate> CODEC = RecordCodecBuilder.create((p_190473_) -> {
      return p_190473_.group(Vec3i.offsetCodec(16).optionalFieldOf("offset", BlockPos.ZERO).forGetter((p_190475_) -> {
         return p_190475_.offset;
      })).apply(p_190473_, InsideWorldBoundsPredicate::new);
   });
   private final Vec3i offset;

   public InsideWorldBoundsPredicate(Vec3i p_190467_) {
      this.offset = p_190467_;
   }

   public boolean test(WorldGenLevel p_190470_, BlockPos p_190471_) {
      return !p_190470_.isOutsideBuildHeight(p_190471_.offset(this.offset));
   }

   public BlockPredicateType<?> type() {
      return BlockPredicateType.INSIDE_WORLD_BOUNDS;
   }
}