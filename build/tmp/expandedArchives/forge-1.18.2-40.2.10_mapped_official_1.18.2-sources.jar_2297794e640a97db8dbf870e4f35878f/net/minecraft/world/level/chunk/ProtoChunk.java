package net.minecraft.world.level.chunk;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.BelowZeroRetrogen;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.ticks.LevelChunkTicks;
import net.minecraft.world.ticks.ProtoChunkTicks;
import net.minecraft.world.ticks.TickContainerAccess;

public class ProtoChunk extends ChunkAccess {
   @Nullable
   private volatile LevelLightEngine lightEngine;
   private volatile ChunkStatus status = ChunkStatus.EMPTY;
   private final List<CompoundTag> entities = Lists.newArrayList();
   private final List<BlockPos> lights = Lists.newArrayList();
   private final Map<GenerationStep.Carving, CarvingMask> carvingMasks = new Object2ObjectArrayMap<>();
   @Nullable
   private BelowZeroRetrogen belowZeroRetrogen;
   private final ProtoChunkTicks<Block> blockTicks;
   private final ProtoChunkTicks<Fluid> fluidTicks;

   public ProtoChunk(ChunkPos p_188167_, UpgradeData p_188168_, LevelHeightAccessor p_188169_, Registry<Biome> p_188170_, @Nullable BlendingData p_188171_) {
      this(p_188167_, p_188168_, (LevelChunkSection[])null, new ProtoChunkTicks<>(), new ProtoChunkTicks<>(), p_188169_, p_188170_, p_188171_);
   }

   public ProtoChunk(ChunkPos p_188173_, UpgradeData p_188174_, @Nullable LevelChunkSection[] p_188175_, ProtoChunkTicks<Block> p_188176_, ProtoChunkTicks<Fluid> p_188177_, LevelHeightAccessor p_188178_, Registry<Biome> p_188179_, @Nullable BlendingData p_188180_) {
      super(p_188173_, p_188174_, p_188178_, p_188179_, 0L, p_188175_, p_188180_);
      this.blockTicks = p_188176_;
      this.fluidTicks = p_188177_;
   }

   public TickContainerAccess<Block> getBlockTicks() {
      return this.blockTicks;
   }

   public TickContainerAccess<Fluid> getFluidTicks() {
      return this.fluidTicks;
   }

   public ChunkAccess.TicksToSave getTicksForSerialization() {
      return new ChunkAccess.TicksToSave(this.blockTicks, this.fluidTicks);
   }

   public BlockState getBlockState(BlockPos p_63264_) {
      int i = p_63264_.getY();
      if (this.isOutsideBuildHeight(i)) {
         return Blocks.VOID_AIR.defaultBlockState();
      } else {
         LevelChunkSection levelchunksection = this.getSection(this.getSectionIndex(i));
         return levelchunksection.hasOnlyAir() ? Blocks.AIR.defaultBlockState() : levelchunksection.getBlockState(p_63264_.getX() & 15, i & 15, p_63264_.getZ() & 15);
      }
   }

   public FluidState getFluidState(BlockPos p_63239_) {
      int i = p_63239_.getY();
      if (this.isOutsideBuildHeight(i)) {
         return Fluids.EMPTY.defaultFluidState();
      } else {
         LevelChunkSection levelchunksection = this.getSection(this.getSectionIndex(i));
         return levelchunksection.hasOnlyAir() ? Fluids.EMPTY.defaultFluidState() : levelchunksection.getFluidState(p_63239_.getX() & 15, i & 15, p_63239_.getZ() & 15);
      }
   }

   public Stream<BlockPos> getLights() {
      return this.lights.stream();
   }

   public ShortList[] getPackedLights() {
      ShortList[] ashortlist = new ShortList[this.getSectionsCount()];

      for(BlockPos blockpos : this.lights) {
         ChunkAccess.getOrCreateOffsetList(ashortlist, this.getSectionIndex(blockpos.getY())).add(packOffsetCoordinates(blockpos));
      }

      return ashortlist;
   }

   public void addLight(short p_63245_, int p_63246_) {
      this.addLight(unpackOffsetCoordinates(p_63245_, this.getSectionYFromSectionIndex(p_63246_), this.chunkPos));
   }

   public void addLight(BlockPos p_63278_) {
      this.lights.add(p_63278_.immutable());
   }

   @Nullable
   public BlockState setBlockState(BlockPos p_63217_, BlockState p_63218_, boolean p_63219_) {
      int i = p_63217_.getX();
      int j = p_63217_.getY();
      int k = p_63217_.getZ();
      if (j >= this.getMinBuildHeight() && j < this.getMaxBuildHeight()) {
         int l = this.getSectionIndex(j);
         if (this.sections[l].hasOnlyAir() && p_63218_.is(Blocks.AIR)) {
            return p_63218_;
         } else {
            if (p_63218_.getLightEmission(this, p_63217_) > 0) {
               this.lights.add(new BlockPos((i & 15) + this.getPos().getMinBlockX(), j, (k & 15) + this.getPos().getMinBlockZ()));
            }

            LevelChunkSection levelchunksection = this.getSection(l);
            BlockState blockstate = levelchunksection.setBlockState(i & 15, j & 15, k & 15, p_63218_);
            if (this.status.isOrAfter(ChunkStatus.FEATURES) && p_63218_ != blockstate && (p_63218_.getLightBlock(this, p_63217_) != blockstate.getLightBlock(this, p_63217_) || p_63218_.getLightEmission(this, p_63217_) != blockstate.getLightEmission(this, p_63217_) || p_63218_.useShapeForLightOcclusion() || blockstate.useShapeForLightOcclusion())) {
               this.lightEngine.checkBlock(p_63217_);
            }

            EnumSet<Heightmap.Types> enumset = this.getStatus().heightmapsAfter();
            EnumSet<Heightmap.Types> enumset1 = null;

            for(Heightmap.Types heightmap$types : enumset) {
               Heightmap heightmap = this.heightmaps.get(heightmap$types);
               if (heightmap == null) {
                  if (enumset1 == null) {
                     enumset1 = EnumSet.noneOf(Heightmap.Types.class);
                  }

                  enumset1.add(heightmap$types);
               }
            }

            if (enumset1 != null) {
               Heightmap.primeHeightmaps(this, enumset1);
            }

            for(Heightmap.Types heightmap$types1 : enumset) {
               this.heightmaps.get(heightmap$types1).update(i & 15, j, k & 15, p_63218_);
            }

            return blockstate;
         }
      } else {
         return Blocks.VOID_AIR.defaultBlockState();
      }
   }

   public void setBlockEntity(BlockEntity p_156488_) {
      this.blockEntities.put(p_156488_.getBlockPos(), p_156488_);
   }

   @Nullable
   public BlockEntity getBlockEntity(BlockPos p_63257_) {
      return this.blockEntities.get(p_63257_);
   }

   public Map<BlockPos, BlockEntity> getBlockEntities() {
      return this.blockEntities;
   }

   public void addEntity(CompoundTag p_63243_) {
      this.entities.add(p_63243_);
   }

   public void addEntity(Entity p_63183_) {
      if (!p_63183_.isPassenger()) {
         CompoundTag compoundtag = new CompoundTag();
         p_63183_.save(compoundtag);
         this.addEntity(compoundtag);
      }
   }

   public void setStartForFeature(ConfiguredStructureFeature<?, ?> p_208116_, StructureStart p_208117_) {
      BelowZeroRetrogen belowzeroretrogen = this.getBelowZeroRetrogen();
      if (belowzeroretrogen != null && p_208117_.isValid()) {
         BoundingBox boundingbox = p_208117_.getBoundingBox();
         LevelHeightAccessor levelheightaccessor = this.getHeightAccessorForGeneration();
         if (boundingbox.minY() < levelheightaccessor.getMinBuildHeight() || boundingbox.maxY() >= levelheightaccessor.getMaxBuildHeight()) {
            return;
         }
      }

      super.setStartForFeature(p_208116_, p_208117_);
   }

   public List<CompoundTag> getEntities() {
      return this.entities;
   }

   public ChunkStatus getStatus() {
      return this.status;
   }

   public void setStatus(ChunkStatus p_63187_) {
      this.status = p_63187_;
      if (this.belowZeroRetrogen != null && p_63187_.isOrAfter(this.belowZeroRetrogen.targetStatus())) {
         this.setBelowZeroRetrogen((BelowZeroRetrogen)null);
      }

      this.setUnsaved(true);
   }

   public Holder<Biome> getNoiseBiome(int p_204450_, int p_204451_, int p_204452_) {
      if (!this.getStatus().isOrAfter(ChunkStatus.BIOMES) && (this.belowZeroRetrogen == null || !this.belowZeroRetrogen.targetStatus().isOrAfter(ChunkStatus.BIOMES))) {
         throw new IllegalStateException("Asking for biomes before we have biomes");
      } else {
         return super.getNoiseBiome(p_204450_, p_204451_, p_204452_);
      }
   }

   public static short packOffsetCoordinates(BlockPos p_63281_) {
      int i = p_63281_.getX();
      int j = p_63281_.getY();
      int k = p_63281_.getZ();
      int l = i & 15;
      int i1 = j & 15;
      int j1 = k & 15;
      return (short)(l | i1 << 4 | j1 << 8);
   }

   public static BlockPos unpackOffsetCoordinates(short p_63228_, int p_63229_, ChunkPos p_63230_) {
      int i = SectionPos.sectionToBlockCoord(p_63230_.x, p_63228_ & 15);
      int j = SectionPos.sectionToBlockCoord(p_63229_, p_63228_ >>> 4 & 15);
      int k = SectionPos.sectionToBlockCoord(p_63230_.z, p_63228_ >>> 8 & 15);
      return new BlockPos(i, j, k);
   }

   public void markPosForPostprocessing(BlockPos p_63266_) {
      if (!this.isOutsideBuildHeight(p_63266_)) {
         ChunkAccess.getOrCreateOffsetList(this.postProcessing, this.getSectionIndex(p_63266_.getY())).add(packOffsetCoordinates(p_63266_));
      }

   }

   public void addPackedPostProcess(short p_63225_, int p_63226_) {
      ChunkAccess.getOrCreateOffsetList(this.postProcessing, p_63226_).add(p_63225_);
   }

   public Map<BlockPos, CompoundTag> getBlockEntityNbts() {
      return Collections.unmodifiableMap(this.pendingBlockEntities);
   }

   @Nullable
   public CompoundTag getBlockEntityNbtForSaving(BlockPos p_63275_) {
      BlockEntity blockentity = this.getBlockEntity(p_63275_);
      return blockentity != null ? blockentity.saveWithFullMetadata() : this.pendingBlockEntities.get(p_63275_);
   }

   public void removeBlockEntity(BlockPos p_63262_) {
      this.blockEntities.remove(p_63262_);
      this.pendingBlockEntities.remove(p_63262_);
   }

   @Nullable
   public CarvingMask getCarvingMask(GenerationStep.Carving p_188185_) {
      return this.carvingMasks.get(p_188185_);
   }

   public CarvingMask getOrCreateCarvingMask(GenerationStep.Carving p_188191_) {
      return this.carvingMasks.computeIfAbsent(p_188191_, (p_188193_) -> {
         return new CarvingMask(this.getHeight(), this.getMinBuildHeight());
      });
   }

   public void setCarvingMask(GenerationStep.Carving p_188187_, CarvingMask p_188188_) {
      this.carvingMasks.put(p_188187_, p_188188_);
   }

   public void setLightEngine(LevelLightEngine p_63210_) {
      this.lightEngine = p_63210_;
   }

   public void setBelowZeroRetrogen(@Nullable BelowZeroRetrogen p_188184_) {
      this.belowZeroRetrogen = p_188184_;
   }

   @Nullable
   public BelowZeroRetrogen getBelowZeroRetrogen() {
      return this.belowZeroRetrogen;
   }

   private static <T> LevelChunkTicks<T> unpackTicks(ProtoChunkTicks<T> p_188190_) {
      return new LevelChunkTicks<>(p_188190_.scheduledTicks());
   }

   public LevelChunkTicks<Block> unpackBlockTicks() {
      return unpackTicks(this.blockTicks);
   }

   public LevelChunkTicks<Fluid> unpackFluidTicks() {
      return unpackTicks(this.fluidTicks);
   }

   public LevelHeightAccessor getHeightAccessorForGeneration() {
      return (LevelHeightAccessor)(this.isUpgrading() ? BelowZeroRetrogen.UPGRADE_HEIGHT_ACCESSOR : this);
   }
}
