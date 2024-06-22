package thebetweenlands.common.world.util;

import java.util.Arrays;

import javax.annotation.Nullable;

import org.apache.commons.lang3.mutable.MutableDouble;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.DensityFunction.FunctionContext;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.RandomSource;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public interface OpenAquifer extends Aquifer {
	static OpenAquifer create(NoiseChunk p_198193_, ChunkPos p_198194_, NormalNoise p_198195_, NormalNoise p_198196_, NormalNoise p_198197_, NormalNoise p_198198_, PositionalRandomFactory p_198199_, int p_198200_, int p_198201_, FluidPicker p_198202_) {
		return new NoiseBasedAquifer(p_198193_, p_198194_, p_198195_, p_198196_, p_198197_, p_198198_, p_198199_, p_198200_, p_198201_, p_198202_);
	}

	static OpenAquifer createDisabled(final FluidPicker p_188375_) {
		return new OpenAquifer() {
			@Nullable
			public BlockState computeSubstance(int p_188392_, int p_188393_, int p_188394_, double p_188395_, double p_188396_) {
				return p_188396_ > 0.0D ? null : p_188375_.computeFluid(p_188392_, p_188393_, p_188394_).at(p_188393_);
			}

			public boolean shouldScheduleFluidUpdate() {
				return false;
			}

			@Override
			public BlockState computeSubstance(FunctionContext p_208158_, double p_208159_) {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}

	@Nullable
	BlockState computeSubstance(int p_188369_, int p_188370_, int p_188371_, double p_188372_, double p_188373_);

	boolean shouldScheduleFluidUpdate();

	public interface FluidPicker {
		FluidStatus computeFluid(int p_188397_, int p_188398_, int p_188399_);
	}

	public static final class FluidStatus {
		final int fluidLevel;
		final BlockState fluidType;

		public FluidStatus(int p_188403_, BlockState p_188404_) {
			this.fluidLevel = p_188403_;
			this.fluidType = p_188404_;
		}

		public BlockState at(int p_188406_) {
			return p_188406_ < this.fluidLevel ? this.fluidType : Blocks.AIR.defaultBlockState();
		}
	}

	public static class NoiseBasedAquifer implements OpenAquifer, FluidPicker {
		private static final int X_RANGE = 10;
		private static final int Y_RANGE = 9;
		private static final int Z_RANGE = 10;
		private static final int X_SEPARATION = 6;
		private static final int Y_SEPARATION = 3;
		private static final int Z_SEPARATION = 6;
		private static final int X_SPACING = 16;
		private static final int Y_SPACING = 12;
		private static final int Z_SPACING = 16;
		private static final int MAX_REASONABLE_DISTANCE_TO_AQUIFER_CENTER = 11;
		private static final double FLOWING_UPDATE_SIMULARITY = similarity(Mth.square(10), Mth.square(12));
		private final NoiseChunk noiseChunk;
		protected final NormalNoise barrierNoise;
		private final NormalNoise fluidLevelFloodednessNoise;
		private final NormalNoise fluidLevelSpreadNoise;
		protected final NormalNoise lavaNoise;
		private final PositionalRandomFactory positionalRandomFactory;
		protected final OpenAquifer.FluidStatus[] aquiferCache;
		protected final long[] aquiferLocationCache;
		private final OpenAquifer.FluidPicker globalFluidPicker;
		protected boolean shouldScheduleFluidUpdate;
		protected final int minGridX;
		protected final int minGridY;
		protected final int minGridZ;
		protected final int gridSizeX;
		protected final int gridSizeZ;
		private static final int[][] SURFACE_SAMPLING_OFFSETS_IN_CHUNKS = new int[][]{{-2, -1}, {-1, -1}, {0, -1}, {1, -1}, {-3, 0}, {-2, 0}, {-1, 0}, {0, 0}, {1, 0}, {-2, 1}, {-1, 1}, {0, 1}, {1, 1}};

		NoiseBasedAquifer(NoiseChunk p_198204_, ChunkPos p_198205_, NormalNoise p_198206_, NormalNoise p_198207_, NormalNoise p_198208_, NormalNoise p_198209_, PositionalRandomFactory p_198210_, int p_198211_, int p_198212_, OpenAquifer.FluidPicker p_198213_) {
			this.noiseChunk = p_198204_;
			this.barrierNoise = p_198206_;
			this.fluidLevelFloodednessNoise = p_198207_;
			this.fluidLevelSpreadNoise = p_198208_;
			this.lavaNoise = p_198209_;
			this.positionalRandomFactory = p_198210_;
			this.minGridX = this.gridX(p_198205_.getMinBlockX()) - 1;
			this.globalFluidPicker = p_198213_;
			int i = this.gridX(p_198205_.getMaxBlockX()) + 1;
			this.gridSizeX = i - this.minGridX + 1;
			this.minGridY = this.gridY(p_198211_) - 1;
			int j = this.gridY(p_198211_ + p_198212_) + 1;
			int k = j - this.minGridY + 1;
			this.minGridZ = this.gridZ(p_198205_.getMinBlockZ()) - 1;
			int l = this.gridZ(p_198205_.getMaxBlockZ()) + 1;
			this.gridSizeZ = l - this.minGridZ + 1;
			int i1 = this.gridSizeX * k * this.gridSizeZ;
			this.aquiferCache = new OpenAquifer.FluidStatus[i1];
			this.aquiferLocationCache = new long[i1];
			Arrays.fill(this.aquiferLocationCache, Long.MAX_VALUE);
		}

		protected int getIndex(int p_158028_, int p_158029_, int p_158030_) {
			int i = p_158028_ - this.minGridX;
			int j = p_158029_ - this.minGridY;
			int k = p_158030_ - this.minGridZ;
			return (j * this.gridSizeZ + k) * this.gridSizeX + i;
		}

		@Nullable
		public BlockState computeSubstance(int p_188427_, int p_188428_, int p_188429_, double p_188430_, double p_188431_) {
			if (p_188430_ <= -64.0D) {
				return this.globalFluidPicker.computeFluid(p_188427_, p_188428_, p_188429_).at(p_188428_);
			} else {
				if (p_188431_ <= 0.0D) {
					OpenAquifer.FluidStatus aquifer$fluidstatus = this.globalFluidPicker.computeFluid(p_188427_, p_188428_, p_188429_);
					double d0;
					BlockState blockstate;
					boolean flag;
					if (aquifer$fluidstatus.at(p_188428_).is(Blocks.LAVA)) {
						blockstate = Blocks.LAVA.defaultBlockState();
						d0 = 0.0D;
						flag = false;
					} else {
						int i = Math.floorDiv(p_188427_ - 5, 16);
						int j = Math.floorDiv(p_188428_ + 1, 12);
						int k = Math.floorDiv(p_188429_ - 5, 16);
						int l = Integer.MAX_VALUE;
						int i1 = Integer.MAX_VALUE;
						int j1 = Integer.MAX_VALUE;
						long k1 = 0L;
						long l1 = 0L;
						long i2 = 0L;

						for (int j2 = 0; j2 <= 1; ++j2) {
							for (int k2 = -1; k2 <= 1; ++k2) {
								for (int l2 = 0; l2 <= 1; ++l2) {
									int i3 = i + j2;
									int j3 = j + k2;
									int k3 = k + l2;
									int l3 = this.getIndex(i3, j3, k3);
									long j4 = this.aquiferLocationCache[l3];
									long i4;
									if (j4 != Long.MAX_VALUE) {
										i4 = j4;
									} else {
										RandomSource randomsource = this.positionalRandomFactory.at(i3, j3, k3);
										i4 = BlockPos.asLong(i3 * 16 + randomsource.nextInt(10), j3 * 12 + randomsource.nextInt(9), k3 * 16 + randomsource.nextInt(10));
										this.aquiferLocationCache[l3] = i4;
									}

									int j5 = BlockPos.getX(i4) - p_188427_;
									int k4 = BlockPos.getY(i4) - p_188428_;
									int l4 = BlockPos.getZ(i4) - p_188429_;
									int i5 = j5 * j5 + k4 * k4 + l4 * l4;
									if (l >= i5) {
										i2 = l1;
										l1 = k1;
										k1 = i4;
										j1 = i1;
										i1 = l;
										l = i5;
									} else if (i1 >= i5) {
										i2 = l1;
										l1 = i4;
										j1 = i1;
										i1 = i5;
									} else if (j1 >= i5) {
										i2 = i4;
										j1 = i5;
									}
								}
							}
						}

						OpenAquifer.FluidStatus aquifer$fluidstatus1 = this.getAquiferStatus(k1);
						OpenAquifer.FluidStatus aquifer$fluidstatus2 = this.getAquiferStatus(l1);
						OpenAquifer.FluidStatus aquifer$fluidstatus3 = this.getAquiferStatus(i2);
						double d6 = similarity(l, i1);
						double d7 = similarity(l, j1);
						double d8 = similarity(i1, j1);
						flag = d6 >= FLOWING_UPDATE_SIMULARITY;
						if (aquifer$fluidstatus1.at(p_188428_).is(Blocks.WATER) && this.globalFluidPicker.computeFluid(p_188427_, p_188428_ - 1, p_188429_).at(p_188428_ - 1).is(Blocks.LAVA)) {
							d0 = 1.0D;
						} else if (d6 > -1.0D) {
							MutableDouble mutabledouble = new MutableDouble(Double.NaN);
							double d1 = this.calculatePressure(p_188427_, p_188428_, p_188429_, mutabledouble, aquifer$fluidstatus1, aquifer$fluidstatus2);
							double d9 = this.calculatePressure(p_188427_, p_188428_, p_188429_, mutabledouble, aquifer$fluidstatus1, aquifer$fluidstatus3);
							double d10 = this.calculatePressure(p_188427_, p_188428_, p_188429_, mutabledouble, aquifer$fluidstatus2, aquifer$fluidstatus3);
							double d2 = Math.max(0.0D, d6);
							double d3 = Math.max(0.0D, d7);
							double d4 = Math.max(0.0D, d8);
							double d5 = 2.0D * d2 * Math.max(d1, Math.max(d9 * d3, d10 * d4));
							d0 = Math.max(0.0D, d5);
						} else {
							d0 = 0.0D;
						}

						blockstate = aquifer$fluidstatus1.at(p_188428_);
					}

					if (p_188431_ + d0 <= 0.0D) {
						this.shouldScheduleFluidUpdate = flag;
						return blockstate;
					}
				}

				this.shouldScheduleFluidUpdate = false;
				return null;
			}
		}

		public boolean shouldScheduleFluidUpdate() {
			return this.shouldScheduleFluidUpdate;
		}

		protected static double similarity(int p_158025_, int p_158026_) {
			double d0 = 25.0D;
			return 1.0D - (double) Math.abs(p_158026_ - p_158025_) / 25.0D;
		}

		private double calculatePressure(int p_188439_, int p_188440_, int p_188441_, MutableDouble p_188442_, OpenAquifer.FluidStatus p_188443_, OpenAquifer.FluidStatus p_188444_) {
			BlockState blockstate = p_188443_.at(p_188440_);
			BlockState blockstate1 = p_188444_.at(p_188440_);
			if ((!blockstate.is(Blocks.LAVA) || !blockstate1.is(Blocks.WATER)) && (!blockstate.is(Blocks.WATER) || !blockstate1.is(Blocks.LAVA))) {
				int i = Math.abs(p_188443_.fluidLevel - p_188444_.fluidLevel);
				if (i == 0) {
					return 0.0D;
				} else {
					double d0 = 0.5D * (double) (p_188443_.fluidLevel + p_188444_.fluidLevel);
					double d1 = (double) p_188440_ + 0.5D - d0;
					double d2 = (double) i / 2.0D;
					double d3 = 0.0D;
					double d4 = 2.5D;
					double d5 = 1.5D;
					double d6 = 3.0D;
					double d7 = 10.0D;
					double d8 = 3.0D;
					double d9 = d2 - Math.abs(d1);
					double d10;
					if (d1 > 0.0D) {
						double d11 = 0.0D + d9;
						if (d11 > 0.0D) {
							d10 = d11 / 1.5D;
						} else {
							d10 = d11 / 2.5D;
						}
					} else {
						double d14 = 3.0D + d9;
						if (d14 > 0.0D) {
							d10 = d14 / 3.0D;
						} else {
							d10 = d14 / 10.0D;
						}
					}

					if (!(d10 < -2.0D) && !(d10 > 2.0D)) {
						double d15 = p_188442_.getValue();
						if (Double.isNaN(d15)) {
							double d12 = 0.5D;
							double d13 = this.barrierNoise.getValue((double) p_188439_, (double) p_188440_ * 0.5D, (double) p_188441_);
							p_188442_.setValue(d13);
							return d13 + d10;
						} else {
							return d15 + d10;
						}
					} else {
						return d10;
					}
				}
			} else {
				return 1.0D;
			}
		}

		protected int gridX(int p_158040_) {
			return Math.floorDiv(p_158040_, 16);
		}

		protected int gridY(int p_158046_) {
			return Math.floorDiv(p_158046_, 12);
		}

		protected int gridZ(int p_158048_) {
			return Math.floorDiv(p_158048_, 16);
		}

		private OpenAquifer.FluidStatus getAquiferStatus(long p_188446_) {
			int i = BlockPos.getX(p_188446_);
			int j = BlockPos.getY(p_188446_);
			int k = BlockPos.getZ(p_188446_);
			int l = this.gridX(i);
			int i1 = this.gridY(j);
			int j1 = this.gridZ(k);
			int k1 = this.getIndex(l, i1, j1);
			OpenAquifer.FluidStatus aquifer$fluidstatus = this.aquiferCache[k1];
			if (aquifer$fluidstatus != null) {
				return aquifer$fluidstatus;
			} else {
				OpenAquifer.FluidStatus aquifer$fluidstatus1 = this.computeFluid(i, j, k);
				this.aquiferCache[k1] = aquifer$fluidstatus1;
				return aquifer$fluidstatus1;
			}
		}

		public OpenAquifer.FluidStatus computeFluid(int p_188448_, int p_188449_, int p_188450_) {
			OpenAquifer.FluidStatus aquifer$fluidstatus = this.globalFluidPicker.computeFluid(p_188448_, p_188449_, p_188450_);
			int i = Integer.MAX_VALUE;
			int j = p_188449_ + 12;
			int k = p_188449_ - 12;
			boolean flag = false;

			for (int[] aint : SURFACE_SAMPLING_OFFSETS_IN_CHUNKS) {
				int l = p_188448_ + SectionPos.sectionToBlockCoord(aint[0]);
				int i1 = p_188450_ + SectionPos.sectionToBlockCoord(aint[1]);
				int j1 = this.noiseChunk.preliminarySurfaceLevel(l, i1);
				int k1 = j1 + 8;
				boolean flag1 = aint[0] == 0 && aint[1] == 0;
				if (flag1 && k > k1) {
					return aquifer$fluidstatus;
				}

				boolean flag2 = j > k1;
				if (flag2 || flag1) {
					OpenAquifer.FluidStatus aquifer$fluidstatus1 = this.globalFluidPicker.computeFluid(l, k1, i1);
					if (!aquifer$fluidstatus1.at(k1).isAir()) {
						if (flag1) {
							flag = true;
						}

						if (flag2) {
							return aquifer$fluidstatus1;
						}
					}
				}

				i = Math.min(i, j1);
			}

			int j4 = i + 8 - p_188449_;
			int k4 = 64;
			double d1 = flag ? Mth.clampedMap((double) j4, 0.0D, 64.0D, 1.0D, 0.0D) : 0.0D;
			double d2 = 0.67D;
			double d3 = Mth.clamp(this.fluidLevelFloodednessNoise.getValue((double) p_188448_, (double) p_188449_ * 0.67D, (double) p_188450_), -1.0D, 1.0D);
			double d4 = Mth.map(d1, 1.0D, 0.0D, -0.3D, 0.8D);
			if (d3 > d4) {
				return aquifer$fluidstatus;
			} else {
				double d5 = Mth.map(d1, 1.0D, 0.0D, -0.8D, 0.4D);
				if (d3 <= d5) {
					return new OpenAquifer.FluidStatus(DimensionType.WAY_BELOW_MIN_Y, aquifer$fluidstatus.fluidType);
				} else {
					int l1 = 16;
					int i2 = 40;
					int j2 = Math.floorDiv(p_188448_, 16);
					int k2 = Math.floorDiv(p_188449_, 40);
					int l2 = Math.floorDiv(p_188450_, 16);
					int i3 = k2 * 40 + 20;
					int j3 = 10;
					double d0 = this.fluidLevelSpreadNoise.getValue((double) j2, (double) k2 / 1.4D, (double) l2) * 10.0D;
					int k3 = Mth.quantize(d0, 3);
					int l3 = i3 + k3;
					int i4 = Math.min(i, l3);
					BlockState blockstate = this.getFluidType(p_188448_, p_188449_, p_188450_, aquifer$fluidstatus, l3);
					return new OpenAquifer.FluidStatus(i4, blockstate);
				}
			}
		}

		private BlockState getFluidType(int p_188433_, int p_188434_, int p_188435_, OpenAquifer.FluidStatus p_188436_, int p_188437_) {
			if (p_188437_ <= -10) {
				int i = 64;
				int j = 40;
				int k = Math.floorDiv(p_188433_, 64);
				int l = Math.floorDiv(p_188434_, 40);
				int i1 = Math.floorDiv(p_188435_, 64);
				double d0 = this.lavaNoise.getValue((double) k, (double) l, (double) i1);
				if (Math.abs(d0) > 0.3D) {
					return Blocks.LAVA.defaultBlockState();
				}
			}

			return p_188436_.fluidType;
		}

		@Override
		public BlockState computeSubstance(FunctionContext p_208158_, double p_208159_) {
			// TODO Auto-generated method stub
			return null;
		}
	}
}
