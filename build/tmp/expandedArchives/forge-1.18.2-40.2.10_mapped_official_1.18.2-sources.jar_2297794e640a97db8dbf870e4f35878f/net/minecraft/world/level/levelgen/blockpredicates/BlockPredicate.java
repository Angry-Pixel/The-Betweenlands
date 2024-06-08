package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.BiPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;

public interface BlockPredicate extends BiPredicate<WorldGenLevel, BlockPos> {
   Codec<BlockPredicate> CODEC = Registry.BLOCK_PREDICATE_TYPES.byNameCodec().dispatch(BlockPredicate::type, BlockPredicateType::codec);
   BlockPredicate ONLY_IN_AIR_PREDICATE = matchesBlock(Blocks.AIR, BlockPos.ZERO);
   BlockPredicate ONLY_IN_AIR_OR_WATER_PREDICATE = matchesBlocks(List.of(Blocks.AIR, Blocks.WATER), BlockPos.ZERO);

   BlockPredicateType<?> type();

   static BlockPredicate allOf(List<BlockPredicate> p_190413_) {
      return new AllOfPredicate(p_190413_);
   }

   static BlockPredicate allOf(BlockPredicate... p_190418_) {
      return allOf(List.of(p_190418_));
   }

   static BlockPredicate allOf(BlockPredicate p_190405_, BlockPredicate p_190406_) {
      return allOf(List.of(p_190405_, p_190406_));
   }

   static BlockPredicate anyOf(List<BlockPredicate> p_190426_) {
      return new AnyOfPredicate(p_190426_);
   }

   static BlockPredicate anyOf(BlockPredicate... p_190431_) {
      return anyOf(List.of(p_190431_));
   }

   static BlockPredicate anyOf(BlockPredicate p_190421_, BlockPredicate p_190422_) {
      return anyOf(List.of(p_190421_, p_190422_));
   }

   static BlockPredicate matchesBlocks(List<Block> p_190415_, Vec3i p_190416_) {
      return new MatchingBlocksPredicate(p_190416_, HolderSet.direct(Block::builtInRegistryHolder, p_190415_));
   }

   static BlockPredicate matchesBlocks(List<Block> p_198312_) {
      return matchesBlocks(p_198312_, Vec3i.ZERO);
   }

   static BlockPredicate matchesBlock(Block p_190397_, Vec3i p_190398_) {
      return matchesBlocks(List.of(p_190397_), p_190398_);
   }

   static BlockPredicate matchesTag(TagKey<Block> p_204680_, Vec3i p_204681_) {
      return new MatchingBlockTagPredicate(p_204681_, p_204680_);
   }

   static BlockPredicate matchesTag(TagKey<Block> p_204678_) {
      return matchesTag(p_204678_, Vec3i.ZERO);
   }

   static BlockPredicate matchesFluids(List<Fluid> p_190428_, Vec3i p_190429_) {
      return new MatchingFluidsPredicate(p_190429_, HolderSet.direct(Fluid::builtInRegistryHolder, p_190428_));
   }

   static BlockPredicate matchesFluid(Fluid p_190408_, Vec3i p_190409_) {
      return matchesFluids(List.of(p_190408_), p_190409_);
   }

   static BlockPredicate not(BlockPredicate p_190403_) {
      return new NotPredicate(p_190403_);
   }

   static BlockPredicate replaceable(Vec3i p_190411_) {
      return new ReplaceablePredicate(p_190411_);
   }

   static BlockPredicate replaceable() {
      return replaceable(Vec3i.ZERO);
   }

   static BlockPredicate wouldSurvive(BlockState p_190400_, Vec3i p_190401_) {
      return new WouldSurvivePredicate(p_190401_, p_190400_);
   }

   static BlockPredicate hasSturdyFace(Vec3i p_198309_, Direction p_198310_) {
      return new HasSturdyFacePredicate(p_198309_, p_198310_);
   }

   static BlockPredicate hasSturdyFace(Direction p_198914_) {
      return hasSturdyFace(Vec3i.ZERO, p_198914_);
   }

   static BlockPredicate solid(Vec3i p_190424_) {
      return new SolidPredicate(p_190424_);
   }

   static BlockPredicate solid() {
      return solid(Vec3i.ZERO);
   }

   static BlockPredicate insideWorld(Vec3i p_190434_) {
      return new InsideWorldBoundsPredicate(p_190434_);
   }

   static BlockPredicate alwaysTrue() {
      return TrueBlockPredicate.INSTANCE;
   }
}