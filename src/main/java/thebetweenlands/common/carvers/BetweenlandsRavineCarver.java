package thebetweenlands.common.carvers;

import java.util.Random;
import java.util.function.Function;

import javax.annotation.Nullable;

import org.apache.commons.lang3.mutable.MutableBoolean;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.carver.CanyonCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CanyonWorldCarver;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.FluidRegistry;

public class BetweenlandsRavineCarver extends CanyonWorldCarver {

	// Ajusted carver
	public BetweenlandsRavineCarver(Codec<CanyonCarverConfiguration> p_64711_) {
		super(p_64711_);
		this.replaceableBlocks = ImmutableSet.of(BlockRegistry.BETWEENSTONE.get(), BlockRegistry.PITSTONE.get(), BlockRegistry.SWAMP_DIRT.get(), BlockRegistry.SWAMP_GRASS.get(), BlockRegistry.DEAD_SWAMP_GRASS.get(), BlockRegistry.MUD.get());
		this.liquids = ImmutableSet.of(FluidRegistry.SWAMP_WATER_STILL.get());
	}

	// Lava level is now swamp water level

	@Nullable
	private BlockState getCarveState(CarvingContext p_159419_, CarverConfiguration p_159420_, BlockPos p_159421_, Aquifer p_159422_) {
		if (p_159421_.getY() <= p_159420_.lavaLevel.resolveY(p_159419_)) {
			return FluidRegistry.SWAMP_WATER_STILL.get().defaultFluidState().createLegacyBlock();
		} else {
			//BlockState blockstate = p_159422_.computeSubstance(p_159421_.getX(), p_159421_.getY(), p_159421_.getZ(), 0.0D, 0.0D);

			return Blocks.AIR.defaultBlockState();
		}
	}

	public boolean carve(CarvingContext p_190611_, CanyonCarverConfiguration p_190612_, ChunkAccess p_190613_, Function<BlockPos, Holder<Biome>> p_190614_, Random p_190615_, Aquifer p_190616_, ChunkPos p_190617_, CarvingMask p_190618_) {
		int i = (this.getRange() * 2 - 1) * 16;
		double d0 = (double) p_190617_.getBlockX(p_190615_.nextInt(16));
		int j = p_190612_.y.sample(p_190615_, p_190611_);
		double d1 = (double) p_190617_.getBlockZ(p_190615_.nextInt(16));
		float f = p_190615_.nextFloat() * ((float) Math.PI * 2F);
		float f1 = p_190612_.verticalRotation.sample(p_190615_);
		double d2 = (double) p_190612_.yScale.sample(p_190615_);
		float f2 = p_190612_.shape.thickness.sample(p_190615_);
		int k = (int) ((float) i * p_190612_.shape.distanceFactor.sample(p_190615_));
		int l = 0;
		this.doCarve(p_190611_, p_190612_, p_190613_, p_190614_, p_190615_.nextLong(), p_190616_, d0, (double) j, d1, f2, f, f1, 0, k, d2, p_190618_);
		return true;
	}


	private double updateVerticalRadius(CanyonCarverConfiguration p_159026_, Random p_159027_, double p_159028_, float p_159029_, float p_159030_) {
		float f = 1.0F - Mth.abs(0.5F - p_159030_ / p_159029_) * 2.0F;
		float f1 = p_159026_.shape.verticalRadiusDefaultFactor + p_159026_.shape.verticalRadiusCenterFactor * f;
		return (double) f1 * p_159028_ * (double) Mth.randomBetween(p_159027_, 0.75F, 1.0F);
	}

	private float[] initWidthFactors(CarvingContext p_159061_, CanyonCarverConfiguration p_159062_, Random p_159063_) {
		int i = p_159061_.getGenDepth();
		float[] afloat = new float[i];
		float f = 1.0F;

		for (int j = 0; j < i; ++j) {
			if (j == 0 || p_159063_.nextInt(p_159062_.shape.widthSmoothness) == 0) {
				f = 1.0F + p_159063_.nextFloat() * p_159063_.nextFloat();
			}

			afloat[j] = f * f;
		}

		return afloat;
	}

	private boolean shouldSkip(CarvingContext p_159074_, float[] p_159075_, double p_159076_, double p_159077_, double p_159078_, int p_159079_) {
		int i = p_159079_ - p_159074_.getMinGenY();
		return (p_159076_ * p_159076_ + p_159078_ * p_159078_) * (double) p_159075_[i - 1] + p_159077_ * p_159077_ / 6.0D >= 1.0D;
	}

	private void doCarve(CarvingContext p_190594_, CanyonCarverConfiguration p_190595_, ChunkAccess p_190596_, Function<BlockPos, Holder<Biome>> p_190597_, long p_190598_, Aquifer p_190599_, double p_190600_, double p_190601_, double p_190602_, float p_190603_, float p_190604_, float p_190605_, int p_190606_, int p_190607_, double p_190608_, CarvingMask p_190609_) {
		Random random = new Random(p_190598_);
		float[] afloat = this.initWidthFactors(p_190594_, p_190595_, random);
		float f = 0.0F;
		float f1 = 0.0F;

		for (int i = p_190606_; i < p_190607_; ++i) {
			double d0 = 1.5D + (double) (Mth.sin((float) i * (float) Math.PI / (float) p_190607_) * p_190603_);
			double d1 = d0 * p_190608_;
			d0 *= (double) p_190595_.shape.horizontalRadiusFactor.sample(random);
			d1 = this.updateVerticalRadius(p_190595_, random, d1, (float) p_190607_, (float) i);
			float f2 = Mth.cos(p_190605_);
			float f3 = Mth.sin(p_190605_);
			p_190600_ += (double) (Mth.cos(p_190604_) * f2);
			p_190601_ += (double) f3;
			p_190602_ += (double) (Mth.sin(p_190604_) * f2);
			p_190605_ *= 0.7F;
			p_190605_ += f1 * 0.05F;
			p_190604_ += f * 0.05F;
			f1 *= 0.8F;
			f *= 0.5F;
			f1 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
			f += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;
			if (random.nextInt(4) != 0) {
				if (!canReach(p_190596_.getPos(), p_190600_, p_190602_, i, p_190607_, p_190603_)) {
					return;
				}

				this.carveBetweenlandsEllipsoid(p_190594_, p_190595_, p_190596_, p_190597_, p_190599_, p_190600_, p_190601_, p_190602_, d0, d1, p_190609_, (p_159082_, p_159083_, p_159084_, p_159085_, p_159086_) -> {
					return this.shouldSkip(p_159082_, afloat, p_159083_, p_159084_, p_159085_, p_159086_);
				});
			}
		}

	}

	public boolean carveBetweenlandsEllipsoid(CarvingContext p_190754_, CarverConfiguration p_190755_, ChunkAccess p_190756_, Function<BlockPos, Holder<Biome>> p_190757_, Aquifer p_190758_, double p_190759_, double p_190760_, double p_190761_, double p_190762_, double p_190763_, CarvingMask p_190764_, CarveSkipChecker p_190765_) {
		ChunkPos chunkpos = p_190756_.getPos();
		double d0 = (double) chunkpos.getMiddleBlockX();
		double d1 = (double) chunkpos.getMiddleBlockZ();
		double d2 = 16.0D + p_190762_ * 2.0D;
		if (!(Math.abs(p_190759_ - d0) > d2) && !(Math.abs(p_190761_ - d1) > d2)) {
			int i = chunkpos.getMinBlockX();
			int j = chunkpos.getMinBlockZ();
			int k = Math.max(Mth.floor(p_190759_ - p_190762_) - i - 1, 0);
			int l = Math.min(Mth.floor(p_190759_ + p_190762_) - i, 15);
			int i1 = Math.max(Mth.floor(p_190760_ - p_190763_) - 1, p_190754_.getMinGenY() + 1);
			int j1 = p_190756_.isUpgrading() ? 0 : 7;
			int k1 = Math.min(Mth.floor(p_190760_ + p_190763_) + 1, p_190754_.getMinGenY() + p_190754_.getGenDepth() - 1 - j1);
			int l1 = Math.max(Mth.floor(p_190761_ - p_190762_) - j - 1, 0);
			int i2 = Math.min(Mth.floor(p_190761_ + p_190762_) - j, 15);
			boolean flag = false;
			BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
			BlockPos.MutableBlockPos blockpos$mutableblockpos1 = new BlockPos.MutableBlockPos();

			for (int j2 = k; j2 <= l; ++j2) {
				int k2 = chunkpos.getBlockX(j2);
				double d3 = ((double) k2 + 0.5D - p_190759_) / p_190762_;

				for (int l2 = l1; l2 <= i2; ++l2) {
					int i3 = chunkpos.getBlockZ(l2);
					double d4 = ((double) i3 + 0.5D - p_190761_) / p_190762_;
					if (!(d3 * d3 + d4 * d4 >= 1.0D)) {
						MutableBoolean mutableboolean = new MutableBoolean(false);

						for (int j3 = k1; j3 > i1; --j3) {
							double d5 = ((double) j3 - 0.5D - p_190760_) / p_190763_;
							if (!p_190765_.shouldSkip(p_190754_, d3, d5, d4, j3) && (!p_190764_.get(j2, j3, l2) || isDebugEnabled(p_190755_))) {
								p_190764_.set(j2, j3, l2);
								blockpos$mutableblockpos.set(k2, j3, i3);
								flag |= this.carveBetweenlandsBlock(p_190754_, p_190755_, p_190756_, p_190757_, p_190764_, blockpos$mutableblockpos, blockpos$mutableblockpos1, p_190758_, mutableboolean);
							}
						}
					}
				}
			}

			return flag;
		} else {
			return false;
		}
	}

	public static boolean isDebugEnabled(CarverConfiguration p_159424_) {
		return p_159424_.debugSettings.isDebugMode();
	}

	public boolean carveBetweenlandsBlock(CarvingContext p_190744_, CarverConfiguration p_190745_, ChunkAccess p_190746_, Function<BlockPos, Holder<Biome>> p_190747_, CarvingMask p_190748_, BlockPos.MutableBlockPos p_190749_, BlockPos.MutableBlockPos p_190750_, Aquifer p_190751_, MutableBoolean p_190752_) {
		BlockState blockstate = p_190746_.getBlockState(p_190749_);
		if (blockstate.is(Blocks.GRASS_BLOCK) || blockstate.is(Blocks.MYCELIUM)) {
			p_190752_.setTrue();
		}

		if (!this.canReplaceBlock(blockstate) && !isDebugEnabled(p_190745_)) {
			return false;
		} else {
			BlockState blockstate1 = this.getCarveState(p_190744_, p_190745_, p_190749_, p_190751_);
			if (blockstate1 == null) {
				return false;
			} else {
				p_190746_.setBlockState(p_190749_, blockstate1, false);
				if (p_190751_.shouldScheduleFluidUpdate() && !blockstate1.getFluidState().isEmpty()) {
					p_190746_.markPosForPostprocessing(p_190749_);
				}

				if (p_190752_.isTrue()) {
					p_190750_.setWithOffset(p_190749_, Direction.DOWN);
					if (p_190746_.getBlockState(p_190750_).is(Blocks.DIRT)) {
						p_190744_.topMaterial(p_190747_, p_190746_, p_190750_, !blockstate1.getFluidState().isEmpty()).ifPresent((p_190743_) -> {
							p_190746_.setBlockState(p_190750_, p_190743_, false);
							if (!p_190743_.getFluidState().isEmpty()) {
								p_190746_.markPosForPostprocessing(p_190750_);
							}

						});
					}
				}

				return true;
			}
		}
	}
}
