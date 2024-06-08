package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.BlockState;

public class SolidPredicate extends StateTestingPredicate {
   public static final Codec<SolidPredicate> CODEC = RecordCodecBuilder.create((p_190538_) -> {
      return stateTestingCodec(p_190538_).apply(p_190538_, SolidPredicate::new);
   });

   public SolidPredicate(Vec3i p_190533_) {
      super(p_190533_);
   }

   protected boolean test(BlockState p_190536_) {
      return p_190536_.getMaterial().isSolid();
   }

   public BlockPredicateType<?> type() {
      return BlockPredicateType.SOLID;
   }
}