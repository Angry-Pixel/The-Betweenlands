package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;

class MatchingFluidsPredicate extends StateTestingPredicate {
   private final HolderSet<Fluid> fluids;
   public static final Codec<MatchingFluidsPredicate> CODEC = RecordCodecBuilder.create((p_190504_) -> {
      return stateTestingCodec(p_190504_).and(RegistryCodecs.homogeneousList(Registry.FLUID_REGISTRY).fieldOf("fluids").forGetter((p_204698_) -> {
         return p_204698_.fluids;
      })).apply(p_190504_, MatchingFluidsPredicate::new);
   });

   public MatchingFluidsPredicate(Vec3i p_204695_, HolderSet<Fluid> p_204696_) {
      super(p_204695_);
      this.fluids = p_204696_;
   }

   protected boolean test(BlockState p_190500_) {
      return p_190500_.getFluidState().is(this.fluids);
   }

   public BlockPredicateType<?> type() {
      return BlockPredicateType.MATCHING_FLUIDS;
   }
}