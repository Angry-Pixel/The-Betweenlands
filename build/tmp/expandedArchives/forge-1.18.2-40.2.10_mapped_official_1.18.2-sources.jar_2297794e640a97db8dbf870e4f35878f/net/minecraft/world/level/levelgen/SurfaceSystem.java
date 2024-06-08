package net.minecraft.world.level.levelgen;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.BlockColumn;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.level.material.Material;

public class SurfaceSystem {
   private static final BlockState WHITE_TERRACOTTA = Blocks.WHITE_TERRACOTTA.defaultBlockState();
   private static final BlockState ORANGE_TERRACOTTA = Blocks.ORANGE_TERRACOTTA.defaultBlockState();
   private static final BlockState TERRACOTTA = Blocks.TERRACOTTA.defaultBlockState();
   private static final BlockState YELLOW_TERRACOTTA = Blocks.YELLOW_TERRACOTTA.defaultBlockState();
   private static final BlockState BROWN_TERRACOTTA = Blocks.BROWN_TERRACOTTA.defaultBlockState();
   private static final BlockState RED_TERRACOTTA = Blocks.RED_TERRACOTTA.defaultBlockState();
   private static final BlockState LIGHT_GRAY_TERRACOTTA = Blocks.LIGHT_GRAY_TERRACOTTA.defaultBlockState();
   private static final BlockState PACKED_ICE = Blocks.PACKED_ICE.defaultBlockState();
   private static final BlockState SNOW_BLOCK = Blocks.SNOW_BLOCK.defaultBlockState();
   private final BlockState defaultBlock;
   private final int seaLevel;
   private final BlockState[] clayBands;
   private final NormalNoise clayBandsOffsetNoise;
   private final NormalNoise badlandsPillarNoise;
   private final NormalNoise badlandsPillarRoofNoise;
   private final NormalNoise badlandsSurfaceNoise;
   private final NormalNoise icebergPillarNoise;
   private final NormalNoise icebergPillarRoofNoise;
   private final NormalNoise icebergSurfaceNoise;
   private final Registry<NormalNoise.NoiseParameters> noises;
   private final Map<ResourceKey<NormalNoise.NoiseParameters>, NormalNoise> noiseIntances = new ConcurrentHashMap<>();
   private final Map<ResourceLocation, PositionalRandomFactory> positionalRandoms = new ConcurrentHashMap<>();
   private final PositionalRandomFactory randomFactory;
   private final NormalNoise surfaceNoise;
   private final NormalNoise surfaceSecondaryNoise;

   public SurfaceSystem(Registry<NormalNoise.NoiseParameters> p_198285_, BlockState p_198286_, int p_198287_, long p_198288_, WorldgenRandom.Algorithm p_198289_) {
      this.noises = p_198285_;
      this.defaultBlock = p_198286_;
      this.seaLevel = p_198287_;
      this.randomFactory = p_198289_.newInstance(p_198288_).forkPositional();
      this.clayBandsOffsetNoise = Noises.instantiate(p_198285_, this.randomFactory, Noises.CLAY_BANDS_OFFSET);
      this.clayBands = generateBands(this.randomFactory.fromHashOf(new ResourceLocation("clay_bands")));
      this.surfaceNoise = Noises.instantiate(p_198285_, this.randomFactory, Noises.SURFACE);
      this.surfaceSecondaryNoise = Noises.instantiate(p_198285_, this.randomFactory, Noises.SURFACE_SECONDARY);
      this.badlandsPillarNoise = Noises.instantiate(p_198285_, this.randomFactory, Noises.BADLANDS_PILLAR);
      this.badlandsPillarRoofNoise = Noises.instantiate(p_198285_, this.randomFactory, Noises.BADLANDS_PILLAR_ROOF);
      this.badlandsSurfaceNoise = Noises.instantiate(p_198285_, this.randomFactory, Noises.BADLANDS_SURFACE);
      this.icebergPillarNoise = Noises.instantiate(p_198285_, this.randomFactory, Noises.ICEBERG_PILLAR);
      this.icebergPillarRoofNoise = Noises.instantiate(p_198285_, this.randomFactory, Noises.ICEBERG_PILLAR_ROOF);
      this.icebergSurfaceNoise = Noises.instantiate(p_198285_, this.randomFactory, Noises.ICEBERG_SURFACE);
   }

   protected NormalNoise getOrCreateNoise(ResourceKey<NormalNoise.NoiseParameters> p_189984_) {
      return this.noiseIntances.computeIfAbsent(p_189984_, (p_189987_) -> {
         return Noises.instantiate(this.noises, this.randomFactory, p_189984_);
      });
   }

   public PositionalRandomFactory getOrCreateRandomFactory(ResourceLocation p_189989_) {
      return this.positionalRandoms.computeIfAbsent(p_189989_, (p_189992_) -> {
         return this.randomFactory.fromHashOf(p_189989_).forkPositional();
      });
   }

   public void buildSurface(BiomeManager p_189945_, Registry<Biome> p_189946_, boolean p_189947_, WorldGenerationContext p_189948_, final ChunkAccess p_189949_, NoiseChunk p_189950_, SurfaceRules.RuleSource p_189951_) {
      final BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
      final ChunkPos chunkpos = p_189949_.getPos();
      int i = chunkpos.getMinBlockX();
      int j = chunkpos.getMinBlockZ();
      BlockColumn blockcolumn = new BlockColumn() {
         public BlockState getBlock(int p_190006_) {
            return p_189949_.getBlockState(blockpos$mutableblockpos.setY(p_190006_));
         }

         public void setBlock(int p_190008_, BlockState p_190009_) {
            LevelHeightAccessor levelheightaccessor = p_189949_.getHeightAccessorForGeneration();
            if (p_190008_ >= levelheightaccessor.getMinBuildHeight() && p_190008_ < levelheightaccessor.getMaxBuildHeight()) {
               p_189949_.setBlockState(blockpos$mutableblockpos.setY(p_190008_), p_190009_, false);
               if (!p_190009_.getFluidState().isEmpty()) {
                  p_189949_.markPosForPostprocessing(blockpos$mutableblockpos);
               }
            }

         }

         public String toString() {
            return "ChunkBlockColumn " + chunkpos;
         }
      };
      SurfaceRules.Context surfacerules$context = new SurfaceRules.Context(this, p_189949_, p_189950_, p_189945_::getBiome, p_189946_, p_189948_);
      SurfaceRules.SurfaceRule surfacerules$surfacerule = p_189951_.apply(surfacerules$context);
      BlockPos.MutableBlockPos blockpos$mutableblockpos1 = new BlockPos.MutableBlockPos();

      for(int k = 0; k < 16; ++k) {
         for(int l = 0; l < 16; ++l) {
            int i1 = i + k;
            int j1 = j + l;
            int k1 = p_189949_.getHeight(Heightmap.Types.WORLD_SURFACE_WG, k, l) + 1;
            blockpos$mutableblockpos.setX(i1).setZ(j1);
            Holder<Biome> holder = p_189945_.getBiome(blockpos$mutableblockpos1.set(i1, p_189947_ ? 0 : k1, j1));
            if (holder.is(Biomes.ERODED_BADLANDS)) {
               this.erodedBadlandsExtension(blockcolumn, i1, j1, k1, p_189949_);
            }

            int l1 = p_189949_.getHeight(Heightmap.Types.WORLD_SURFACE_WG, k, l) + 1;
            surfacerules$context.updateXZ(i1, j1);
            int i2 = 0;
            int j2 = Integer.MIN_VALUE;
            int k2 = Integer.MAX_VALUE;
            int l2 = p_189949_.getMinBuildHeight();

            for(int i3 = l1; i3 >= l2; --i3) {
               BlockState blockstate = blockcolumn.getBlock(i3);
               if (blockstate.isAir()) {
                  i2 = 0;
                  j2 = Integer.MIN_VALUE;
               } else if (!blockstate.getFluidState().isEmpty()) {
                  if (j2 == Integer.MIN_VALUE) {
                     j2 = i3 + 1;
                  }
               } else {
                  if (k2 >= i3) {
                     k2 = DimensionType.WAY_BELOW_MIN_Y;

                     for(int j3 = i3 - 1; j3 >= l2 - 1; --j3) {
                        BlockState blockstate1 = blockcolumn.getBlock(j3);
                        if (!this.isStone(blockstate1)) {
                           k2 = j3 + 1;
                           break;
                        }
                     }
                  }

                  ++i2;
                  int k3 = i3 - k2 + 1;
                  surfacerules$context.updateY(i2, k3, j2, i1, i3, j1);
                  if (blockstate == this.defaultBlock) {
                     BlockState blockstate2 = surfacerules$surfacerule.tryApply(i1, i3, j1);
                     if (blockstate2 != null) {
                        blockcolumn.setBlock(i3, blockstate2);
                     }
                  }
               }
            }

            if (holder.is(Biomes.FROZEN_OCEAN) || holder.is(Biomes.DEEP_FROZEN_OCEAN)) {
               this.frozenOceanExtension(surfacerules$context.getMinSurfaceLevel(), holder.value(), blockcolumn, blockpos$mutableblockpos1, i1, j1, k1);
            }
         }
      }

   }

   protected int getSurfaceDepth(int p_189928_, int p_189929_) {
      double d0 = this.surfaceNoise.getValue((double)p_189928_, 0.0D, (double)p_189929_);
      return (int)(d0 * 2.75D + 3.0D + this.randomFactory.at(p_189928_, 0, p_189929_).nextDouble() * 0.25D);
   }

   protected double getSurfaceSecondary(int p_202190_, int p_202191_) {
      return this.surfaceSecondaryNoise.getValue((double)p_202190_, 0.0D, (double)p_202191_);
   }

   private boolean isStone(BlockState p_189953_) {
      return !p_189953_.isAir() && p_189953_.getFluidState().isEmpty();
   }

   /** @deprecated */
   @Deprecated
   public Optional<BlockState> topMaterial(SurfaceRules.RuleSource p_189972_, CarvingContext p_189973_, Function<BlockPos, Holder<Biome>> p_189974_, ChunkAccess p_189975_, NoiseChunk p_189976_, BlockPos p_189977_, boolean p_189978_) {
      SurfaceRules.Context surfacerules$context = new SurfaceRules.Context(this, p_189975_, p_189976_, p_189974_, p_189973_.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY), p_189973_);
      SurfaceRules.SurfaceRule surfacerules$surfacerule = p_189972_.apply(surfacerules$context);
      int i = p_189977_.getX();
      int j = p_189977_.getY();
      int k = p_189977_.getZ();
      surfacerules$context.updateXZ(i, k);
      surfacerules$context.updateY(1, 1, p_189978_ ? j + 1 : Integer.MIN_VALUE, i, j, k);
      BlockState blockstate = surfacerules$surfacerule.tryApply(i, j, k);
      return Optional.ofNullable(blockstate);
   }

   private void erodedBadlandsExtension(BlockColumn p_189955_, int p_189956_, int p_189957_, int p_189958_, LevelHeightAccessor p_189959_) {
      double d0 = 0.2D;
      double d1 = Math.min(Math.abs(this.badlandsSurfaceNoise.getValue((double)p_189956_, 0.0D, (double)p_189957_) * 8.25D), this.badlandsPillarNoise.getValue((double)p_189956_ * 0.2D, 0.0D, (double)p_189957_ * 0.2D) * 15.0D);
      if (!(d1 <= 0.0D)) {
         double d2 = 0.75D;
         double d3 = 1.5D;
         double d4 = Math.abs(this.badlandsPillarRoofNoise.getValue((double)p_189956_ * 0.75D, 0.0D, (double)p_189957_ * 0.75D) * 1.5D);
         double d5 = 64.0D + Math.min(d1 * d1 * 2.5D, Math.ceil(d4 * 50.0D) + 24.0D);
         int i = Mth.floor(d5);
         if (p_189958_ <= i) {
            for(int j = i; j >= p_189959_.getMinBuildHeight(); --j) {
               BlockState blockstate = p_189955_.getBlock(j);
               if (blockstate.is(this.defaultBlock.getBlock())) {
                  break;
               }

               if (blockstate.is(Blocks.WATER)) {
                  return;
               }
            }

            for(int k = i; k >= p_189959_.getMinBuildHeight() && p_189955_.getBlock(k).isAir(); --k) {
               p_189955_.setBlock(k, this.defaultBlock);
            }

         }
      }
   }

   private void frozenOceanExtension(int p_189935_, Biome p_189936_, BlockColumn p_189937_, BlockPos.MutableBlockPos p_189938_, int p_189939_, int p_189940_, int p_189941_) {
      double d0 = 1.28D;
      double d1 = Math.min(Math.abs(this.icebergSurfaceNoise.getValue((double)p_189939_, 0.0D, (double)p_189940_) * 8.25D), this.icebergPillarNoise.getValue((double)p_189939_ * 1.28D, 0.0D, (double)p_189940_ * 1.28D) * 15.0D);
      if (!(d1 <= 1.8D)) {
         double d3 = 1.17D;
         double d4 = 1.5D;
         double d5 = Math.abs(this.icebergPillarRoofNoise.getValue((double)p_189939_ * 1.17D, 0.0D, (double)p_189940_ * 1.17D) * 1.5D);
         double d6 = Math.min(d1 * d1 * 1.2D, Math.ceil(d5 * 40.0D) + 14.0D);
         if (p_189936_.shouldMeltFrozenOceanIcebergSlightly(p_189938_.set(p_189939_, 63, p_189940_))) {
            d6 -= 2.0D;
         }

         double d2;
         if (d6 > 2.0D) {
            d2 = (double)this.seaLevel - d6 - 7.0D;
            d6 += (double)this.seaLevel;
         } else {
            d6 = 0.0D;
            d2 = 0.0D;
         }

         double d7 = d6;
         RandomSource randomsource = this.randomFactory.at(p_189939_, 0, p_189940_);
         int i = 2 + randomsource.nextInt(4);
         int j = this.seaLevel + 18 + randomsource.nextInt(10);
         int k = 0;

         for(int l = Math.max(p_189941_, (int)d6 + 1); l >= p_189935_; --l) {
            if (p_189937_.getBlock(l).isAir() && l < (int)d7 && randomsource.nextDouble() > 0.01D || p_189937_.getBlock(l).getMaterial() == Material.WATER && l > (int)d2 && l < this.seaLevel && d2 != 0.0D && randomsource.nextDouble() > 0.15D) {
               if (k <= i && l > j) {
                  p_189937_.setBlock(l, SNOW_BLOCK);
                  ++k;
               } else {
                  p_189937_.setBlock(l, PACKED_ICE);
               }
            }
         }

      }
   }

   private static BlockState[] generateBands(RandomSource p_189965_) {
      BlockState[] ablockstate = new BlockState[192];
      Arrays.fill(ablockstate, TERRACOTTA);

      for(int k = 0; k < ablockstate.length; ++k) {
         k += p_189965_.nextInt(5) + 1;
         if (k < ablockstate.length) {
            ablockstate[k] = ORANGE_TERRACOTTA;
         }
      }

      makeBands(p_189965_, ablockstate, 1, YELLOW_TERRACOTTA);
      makeBands(p_189965_, ablockstate, 2, BROWN_TERRACOTTA);
      makeBands(p_189965_, ablockstate, 1, RED_TERRACOTTA);
      int l = p_189965_.nextIntBetweenInclusive(9, 15);
      int i = 0;

      for(int j = 0; i < l && j < ablockstate.length; j += p_189965_.nextInt(16) + 4) {
         ablockstate[j] = WHITE_TERRACOTTA;
         if (j - 1 > 0 && p_189965_.nextBoolean()) {
            ablockstate[j - 1] = LIGHT_GRAY_TERRACOTTA;
         }

         if (j + 1 < ablockstate.length && p_189965_.nextBoolean()) {
            ablockstate[j + 1] = LIGHT_GRAY_TERRACOTTA;
         }

         ++i;
      }

      return ablockstate;
   }

   private static void makeBands(RandomSource p_189967_, BlockState[] p_189968_, int p_189969_, BlockState p_189970_) {
      int i = p_189967_.nextIntBetweenInclusive(6, 15);

      for(int j = 0; j < i; ++j) {
         int k = p_189969_ + p_189967_.nextInt(3);
         int l = p_189967_.nextInt(p_189968_.length);

         for(int i1 = 0; l + i1 < p_189968_.length && i1 < k; ++i1) {
            p_189968_[l + i1] = p_189970_;
         }
      }

   }

   protected BlockState getBand(int p_189931_, int p_189932_, int p_189933_) {
      int i = (int)Math.round(this.clayBandsOffsetNoise.getValue((double)p_189931_, 0.0D, (double)p_189933_) * 4.0D);
      return this.clayBands[(p_189932_ + i + this.clayBands.length) % this.clayBands.length];
   }
}