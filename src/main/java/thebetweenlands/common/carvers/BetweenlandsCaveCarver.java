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
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CaveWorldCarver;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.FluidRegistry;

// TODO: rework original MapGenBase carver to WorldCarver
public class BetweenlandsCaveCarver extends CaveWorldCarver {
	
	// Ajusted carver
	public BetweenlandsCaveCarver(Codec<CaveCarverConfiguration> p_64873_) {
		super(p_64873_);
		this.replaceableBlocks = ImmutableSet.of(BlockRegistry.BETWEENSTONE.get(), BlockRegistry.PITSTONE.get(), BlockRegistry.SWAMP_DIRT.get(), BlockRegistry.SWAMP_GRASS.get(), BlockRegistry.DEAD_SWAMP_GRASS.get(), BlockRegistry.MUD.get());
		this.liquids = ImmutableSet.of(FluidRegistry.SWAMP_WATER_STILL.get());
	}
	
	// Lava level is now swamp water level
	
	@Nullable
	public BlockState getCarveState(CarvingContext p_159419_, CarverConfiguration p_159420_, BlockPos p_159421_, Aquifer p_159422_) {
		if (p_159421_.getY() <= p_159420_.lavaLevel.resolveY(p_159419_)) {
			return FluidRegistry.SWAMP_WATER_STILL.get().defaultFluidState().createLegacyBlock();
		} else {
			//BlockState blockstate = p_159422_.computeSubstance(p_159421_.getX(), p_159421_.getY(), p_159421_.getZ(), 0.0D, 0.0D);

			return Blocks.AIR.defaultBlockState();
		}
	}
	
	
	@Override
	protected void createRoom(CarvingContext p_190691_, CaveCarverConfiguration p_190692_, ChunkAccess p_190693_, Function<BlockPos, Holder<Biome>> p_190694_, Aquifer p_190695_, double p_190696_, double p_190697_, double p_190698_, float p_190699_, double p_190700_, CarvingMask p_190701_, CarveSkipChecker p_190702_) {
	      double d0 = 1.5D + (double)(Mth.sin(((float)Math.PI / 2F)) * p_190699_);
	      double d1 = d0 * p_190700_;
	      this.carveBetweenlandsEllipsoid(p_190691_, p_190692_, p_190693_, p_190694_, p_190695_, p_190696_ + 1.0D, p_190697_, p_190698_, d0, d1, p_190701_, p_190702_);
	   }

	   protected void createTunnel(CarvingContext p_190671_, CaveCarverConfiguration p_190672_, ChunkAccess p_190673_, Function<BlockPos, Holder<Biome>> p_190674_, long p_190675_, Aquifer p_190676_, double p_190677_, double p_190678_, double p_190679_, double p_190680_, double p_190681_, float p_190682_, float p_190683_, float p_190684_, int p_190685_, int p_190686_, double p_190687_, CarvingMask p_190688_, CarveSkipChecker p_190689_) {
	      Random random = new Random(p_190675_);
	      int i = random.nextInt(p_190686_ / 2) + p_190686_ / 4;
	      boolean flag = random.nextInt(6) == 0;
	      float f = 0.0F;
	      float f1 = 0.0F;

	      for(int j = p_190685_; j < p_190686_; ++j) {
	         double d0 = 1.5D + (double)(Mth.sin((float)Math.PI * (float)j / (float)p_190686_) * p_190682_);
	         double d1 = d0 * p_190687_;
	         float f2 = Mth.cos(p_190684_);
	         p_190677_ += (double)(Mth.cos(p_190683_) * f2);
	         p_190678_ += (double)Mth.sin(p_190684_);
	         p_190679_ += (double)(Mth.sin(p_190683_) * f2);
	         p_190684_ *= flag ? 0.92F : 0.7F;
	         p_190684_ += f1 * 0.1F;
	         p_190683_ += f * 0.1F;
	         f1 *= 0.9F;
	         f *= 0.75F;
	         f1 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
	         f += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;
	         if (j == i && p_190682_ > 1.0F) {
	            this.createTunnel(p_190671_, p_190672_, p_190673_, p_190674_, random.nextLong(), p_190676_, p_190677_, p_190678_, p_190679_, p_190680_, p_190681_, random.nextFloat() * 0.5F + 0.5F, p_190683_ - ((float)Math.PI / 2F), p_190684_ / 3.0F, j, p_190686_, 1.0D, p_190688_, p_190689_);
	            this.createTunnel(p_190671_, p_190672_, p_190673_, p_190674_, random.nextLong(), p_190676_, p_190677_, p_190678_, p_190679_, p_190680_, p_190681_, random.nextFloat() * 0.5F + 0.5F, p_190683_ + ((float)Math.PI / 2F), p_190684_ / 3.0F, j, p_190686_, 1.0D, p_190688_, p_190689_);
	            return;
	         }

	         if (random.nextInt(4) != 0) {
	            if (!canReach(p_190673_.getPos(), p_190677_, p_190679_, j, p_190686_, p_190682_)) {
	               return;
	            }

	            this.carveBetweenlandsEllipsoid(p_190671_, p_190672_, p_190673_, p_190674_, p_190676_, p_190677_, p_190678_, p_190679_, d0 * p_190680_, d1 * p_190681_, p_190688_, p_190689_);
	         }
	      }

	   }
	
	public boolean carveBetweenlandsEllipsoid(CarvingContext p_190754_, CarverConfiguration p_190755_, ChunkAccess p_190756_, Function<BlockPos, Holder<Biome>> p_190757_, Aquifer p_190758_, double p_190759_, double p_190760_, double p_190761_, double p_190762_, double p_190763_, CarvingMask p_190764_, CarveSkipChecker p_190765_) {
	      ChunkPos chunkpos = p_190756_.getPos();
	      double d0 = (double)chunkpos.getMiddleBlockX();
	      double d1 = (double)chunkpos.getMiddleBlockZ();
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

	         for(int j2 = k; j2 <= l; ++j2) {
	            int k2 = chunkpos.getBlockX(j2);
	            double d3 = ((double)k2 + 0.5D - p_190759_) / p_190762_;

	            for(int l2 = l1; l2 <= i2; ++l2) {
	               int i3 = chunkpos.getBlockZ(l2);
	               double d4 = ((double)i3 + 0.5D - p_190761_) / p_190762_;
	               if (!(d3 * d3 + d4 * d4 >= 1.0D)) {
	                  MutableBoolean mutableboolean = new MutableBoolean(false);

	                  for(int j3 = k1; j3 > i1; --j3) {
	                     double d5 = ((double)j3 - 0.5D - p_190760_) / p_190763_;
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
	
	public boolean carveBetweenlandsBlock(CarvingContext p_190744_, CarverConfiguration p_190745_, ChunkAccess p_190746_, Function<BlockPos, Holder<Biome>> p_190757_, CarvingMask p_190748_, BlockPos.MutableBlockPos p_190749_, BlockPos.MutableBlockPos p_190750_, Aquifer p_190751_, MutableBoolean p_190752_) {
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
	                  p_190744_.topMaterial(p_190757_, p_190746_, p_190750_, !blockstate1.getFluidState().isEmpty()).ifPresent((p_190743_) -> {
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
