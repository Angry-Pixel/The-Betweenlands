package net.minecraft.world.level.chunk;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.EuclideanGameEventDispatcher;
import net.minecraft.world.level.gameevent.GameEventDispatcher;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.levelgen.DebugLevelSource;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.ticks.LevelChunkTicks;
import net.minecraft.world.ticks.TickContainerAccess;
import org.slf4j.Logger;

public class LevelChunk extends ChunkAccess implements net.minecraftforge.common.capabilities.ICapabilityProviderImpl<LevelChunk> {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final TickingBlockEntity NULL_TICKER = new TickingBlockEntity() {
      public void tick() {
      }

      public boolean isRemoved() {
         return true;
      }

      public BlockPos getPos() {
         return BlockPos.ZERO;
      }

      public String getType() {
         return "<null>";
      }
   };
   private final Map<BlockPos, LevelChunk.RebindableTickingBlockEntityWrapper> tickersInLevel = Maps.newHashMap();
   private boolean loaded;
   private boolean clientLightReady = false;
   final Level level;
   @Nullable
   private Supplier<ChunkHolder.FullChunkStatus> fullStatus;
   @Nullable
   private LevelChunk.PostLoadProcessor postLoad;
   private final Int2ObjectMap<GameEventDispatcher> gameEventDispatcherSections;
   private final LevelChunkTicks<Block> blockTicks;
   private final LevelChunkTicks<Fluid> fluidTicks;

   public LevelChunk(Level p_187945_, ChunkPos p_187946_) {
      this(p_187945_, p_187946_, UpgradeData.EMPTY, new LevelChunkTicks<>(), new LevelChunkTicks<>(), 0L, (LevelChunkSection[])null, (LevelChunk.PostLoadProcessor)null, (BlendingData)null);
   }

   public LevelChunk(Level p_196854_, ChunkPos p_196855_, UpgradeData p_196856_, LevelChunkTicks<Block> p_196857_, LevelChunkTicks<Fluid> p_196858_, long p_196859_, @Nullable LevelChunkSection[] p_196860_, @Nullable LevelChunk.PostLoadProcessor p_196861_, @Nullable BlendingData p_196862_) {
      super(p_196855_, p_196856_, p_196854_, p_196854_.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY), p_196859_, p_196860_, p_196862_);
      this.level = p_196854_;
      this.gameEventDispatcherSections = new Int2ObjectOpenHashMap<>();

      for(Heightmap.Types heightmap$types : Heightmap.Types.values()) {
         if (ChunkStatus.FULL.heightmapsAfter().contains(heightmap$types)) {
            this.heightmaps.put(heightmap$types, new Heightmap(this, heightmap$types));
         }
      }

      this.postLoad = p_196861_;
      this.blockTicks = p_196857_;
      this.fluidTicks = p_196858_;
      this.capProvider.initInternal();
   }

   public LevelChunk(ServerLevel p_196850_, ProtoChunk p_196851_, @Nullable LevelChunk.PostLoadProcessor p_196852_) {
      this(p_196850_, p_196851_.getPos(), p_196851_.getUpgradeData(), p_196851_.unpackBlockTicks(), p_196851_.unpackFluidTicks(), p_196851_.getInhabitedTime(), p_196851_.getSections(), p_196852_, p_196851_.getBlendingData());

      for(BlockEntity blockentity : p_196851_.getBlockEntities().values()) {
         this.setBlockEntity(blockentity);
      }

      this.pendingBlockEntities.putAll(p_196851_.getBlockEntityNbts());

      for(int i = 0; i < p_196851_.getPostProcessing().length; ++i) {
         this.postProcessing[i] = p_196851_.getPostProcessing()[i];
      }

      this.setAllStarts(p_196851_.getAllStarts());
      this.setAllReferences(p_196851_.getAllReferences());

      for(Entry<Heightmap.Types, Heightmap> entry : p_196851_.getHeightmaps()) {
         if (ChunkStatus.FULL.heightmapsAfter().contains(entry.getKey())) {
            this.setHeightmap(entry.getKey(), entry.getValue().getRawData());
         }
      }

      this.setLightCorrect(p_196851_.isLightCorrect());
      this.unsaved = true;
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

   public GameEventDispatcher getEventDispatcher(int p_156372_) {
      return this.gameEventDispatcherSections.computeIfAbsent(p_156372_, (p_156395_) -> {
         return new EuclideanGameEventDispatcher(this.level);
      });
   }

   public BlockState getBlockState(BlockPos p_62923_) {
      int i = p_62923_.getX();
      int j = p_62923_.getY();
      int k = p_62923_.getZ();
      if (this.level.isDebug()) {
         BlockState blockstate = null;
         if (j == 60) {
            blockstate = Blocks.BARRIER.defaultBlockState();
         }

         if (j == 70) {
            blockstate = DebugLevelSource.getBlockStateFor(i, k);
         }

         return blockstate == null ? Blocks.AIR.defaultBlockState() : blockstate;
      } else {
         try {
            int l = this.getSectionIndex(j);
            if (l >= 0 && l < this.sections.length) {
               LevelChunkSection levelchunksection = this.sections[l];
               if (!levelchunksection.hasOnlyAir()) {
                  return levelchunksection.getBlockState(i & 15, j & 15, k & 15);
               }
            }

            return Blocks.AIR.defaultBlockState();
         } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.forThrowable(throwable, "Getting block state");
            CrashReportCategory crashreportcategory = crashreport.addCategory("Block being got");
            crashreportcategory.setDetail("Location", () -> {
               return CrashReportCategory.formatLocation(this, i, j, k);
            });
            throw new ReportedException(crashreport);
         }
      }
   }

   public FluidState getFluidState(BlockPos p_62895_) {
      return this.getFluidState(p_62895_.getX(), p_62895_.getY(), p_62895_.getZ());
   }

   public FluidState getFluidState(int p_62815_, int p_62816_, int p_62817_) {
      try {
         int i = this.getSectionIndex(p_62816_);
         if (i >= 0 && i < this.sections.length) {
            LevelChunkSection levelchunksection = this.sections[i];
            if (!levelchunksection.hasOnlyAir()) {
               return levelchunksection.getFluidState(p_62815_ & 15, p_62816_ & 15, p_62817_ & 15);
            }
         }

         return Fluids.EMPTY.defaultFluidState();
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.forThrowable(throwable, "Getting fluid state");
         CrashReportCategory crashreportcategory = crashreport.addCategory("Block being got");
         crashreportcategory.setDetail("Location", () -> {
            return CrashReportCategory.formatLocation(this, p_62815_, p_62816_, p_62817_);
         });
         throw new ReportedException(crashreport);
      }
   }

   @Nullable
   public BlockState setBlockState(BlockPos p_62865_, BlockState p_62866_, boolean p_62867_) {
      int i = p_62865_.getY();
      LevelChunkSection levelchunksection = this.getSection(this.getSectionIndex(i));
      boolean flag = levelchunksection.hasOnlyAir();
      if (flag && p_62866_.isAir()) {
         return null;
      } else {
         int j = p_62865_.getX() & 15;
         int k = i & 15;
         int l = p_62865_.getZ() & 15;
         BlockState blockstate = levelchunksection.setBlockState(j, k, l, p_62866_);
         if (blockstate == p_62866_) {
            return null;
         } else {
            Block block = p_62866_.getBlock();
            this.heightmaps.get(Heightmap.Types.MOTION_BLOCKING).update(j, i, l, p_62866_);
            this.heightmaps.get(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES).update(j, i, l, p_62866_);
            this.heightmaps.get(Heightmap.Types.OCEAN_FLOOR).update(j, i, l, p_62866_);
            this.heightmaps.get(Heightmap.Types.WORLD_SURFACE).update(j, i, l, p_62866_);
            boolean flag1 = levelchunksection.hasOnlyAir();
            if (flag != flag1) {
               this.level.getChunkSource().getLightEngine().updateSectionStatus(p_62865_, flag1);
            }

            boolean flag2 = blockstate.hasBlockEntity();
            if (!this.level.isClientSide) {
               blockstate.onRemove(this.level, p_62865_, p_62866_, p_62867_);
         } else if ((!blockstate.is(block) || !p_62866_.hasBlockEntity()) && flag2) {
               this.removeBlockEntity(p_62865_);
            }

            if (!levelchunksection.getBlockState(j, k, l).is(block)) {
               return null;
            } else {
            if (!this.level.isClientSide && !this.level.captureBlockSnapshots) {
                  p_62866_.onPlace(this.level, p_62865_, blockstate, p_62867_);
               }

               if (p_62866_.hasBlockEntity()) {
                  BlockEntity blockentity = this.getBlockEntity(p_62865_, LevelChunk.EntityCreationType.CHECK);
                  if (blockentity == null) {
                     blockentity = ((EntityBlock)block).newBlockEntity(p_62865_, p_62866_);
                     if (blockentity != null) {
                        this.addAndRegisterBlockEntity(blockentity);
                     }
                  } else {
                     blockentity.setBlockState(p_62866_);
                     this.updateBlockEntityTicker(blockentity);
                  }
               }

               this.unsaved = true;
               return blockstate;
            }
         }
      }
   }

   /** @deprecated */
   @Deprecated
   public void addEntity(Entity p_62826_) {
   }

   @Nullable
   private BlockEntity createBlockEntity(BlockPos p_62935_) {
      BlockState blockstate = this.getBlockState(p_62935_);
      return !blockstate.hasBlockEntity() ? null : ((EntityBlock)blockstate.getBlock()).newBlockEntity(p_62935_, blockstate);
   }

   @Nullable
   public BlockEntity getBlockEntity(BlockPos p_62912_) {
      return this.getBlockEntity(p_62912_, LevelChunk.EntityCreationType.CHECK);
   }

   @Nullable
   public BlockEntity getBlockEntity(BlockPos p_62868_, LevelChunk.EntityCreationType p_62869_) {
      BlockEntity blockentity = this.blockEntities.get(p_62868_);
      if (blockentity != null && blockentity.isRemoved()) {
         blockEntities.remove(p_62868_);
         blockentity = null;
      }
      if (blockentity == null) {
         CompoundTag compoundtag = this.pendingBlockEntities.remove(p_62868_);
         if (compoundtag != null) {
            BlockEntity blockentity1 = this.promotePendingBlockEntity(p_62868_, compoundtag);
            if (blockentity1 != null) {
               return blockentity1;
            }
         }
      }

      if (blockentity == null) {
         if (p_62869_ == LevelChunk.EntityCreationType.IMMEDIATE) {
            blockentity = this.createBlockEntity(p_62868_);
            if (blockentity != null) {
               this.addAndRegisterBlockEntity(blockentity);
            }
         }
      }

      return blockentity;
   }

   public void addAndRegisterBlockEntity(BlockEntity p_156391_) {
      this.setBlockEntity(p_156391_);
      if (this.isInLevel()) {
         this.addGameEventListener(p_156391_);
         this.updateBlockEntityTicker(p_156391_);
         p_156391_.onLoad();
      }

   }

   private boolean isInLevel() {
      return this.loaded || this.level.isClientSide();
   }

   boolean isTicking(BlockPos p_156411_) {
      if (!this.level.getWorldBorder().isWithinBounds(p_156411_)) {
         return false;
      } else {
         Level level = this.level;
         if (!(level instanceof ServerLevel)) {
            return true;
         } else {
            ServerLevel serverlevel = (ServerLevel)level;
            return this.getFullStatus().isOrAfter(ChunkHolder.FullChunkStatus.TICKING) && serverlevel.areEntitiesLoaded(ChunkPos.asLong(p_156411_));
         }
      }
   }

   public void setBlockEntity(BlockEntity p_156374_) {
      BlockPos blockpos = p_156374_.getBlockPos();
      if (this.getBlockState(blockpos).hasBlockEntity()) {
         p_156374_.setLevel(this.level);
         p_156374_.clearRemoved();
         BlockEntity blockentity = this.blockEntities.put(blockpos.immutable(), p_156374_);
         if (blockentity != null && blockentity != p_156374_) {
            blockentity.setRemoved();
         }

      }
   }

   @Nullable
   public CompoundTag getBlockEntityNbtForSaving(BlockPos p_62932_) {
      BlockEntity blockentity = this.getBlockEntity(p_62932_);
      if (blockentity != null && !blockentity.isRemoved()) {
         try {
         CompoundTag compoundtag1 = blockentity.saveWithFullMetadata();
         compoundtag1.putBoolean("keepPacked", false);
         return compoundtag1;
         } catch (Exception e) {
            LOGGER.error("A BlockEntity type {} has thrown an exception trying to write state. It will not persist, Report this to the mod author", blockentity.getClass().getName(), e);
            return null;
         }
      } else {
         CompoundTag compoundtag = this.pendingBlockEntities.get(p_62932_);
         if (compoundtag != null) {
            compoundtag = compoundtag.copy();
            compoundtag.putBoolean("keepPacked", true);
         }

         return compoundtag;
      }
   }

   public void removeBlockEntity(BlockPos p_62919_) {
      if (this.isInLevel()) {
         BlockEntity blockentity = this.blockEntities.remove(p_62919_);
         if (blockentity != null) {
            this.removeGameEventListener(blockentity);
            blockentity.setRemoved();
         }
      }

      this.removeBlockEntityTicker(p_62919_);
   }

   private <T extends BlockEntity> void removeGameEventListener(T p_156397_) {
      if (!this.level.isClientSide) {
         Block block = p_156397_.getBlockState().getBlock();
         if (block instanceof EntityBlock) {
            GameEventListener gameeventlistener = ((EntityBlock)block).getListener(this.level, p_156397_);
            if (gameeventlistener != null) {
               int i = SectionPos.blockToSectionCoord(p_156397_.getBlockPos().getY());
               GameEventDispatcher gameeventdispatcher = this.getEventDispatcher(i);
               gameeventdispatcher.unregister(gameeventlistener);
               if (gameeventdispatcher.isEmpty()) {
                  this.gameEventDispatcherSections.remove(i);
               }
            }
         }

      }
   }

   private void removeBlockEntityTicker(BlockPos p_156413_) {
      LevelChunk.RebindableTickingBlockEntityWrapper levelchunk$rebindabletickingblockentitywrapper = this.tickersInLevel.remove(p_156413_);
      if (levelchunk$rebindabletickingblockentitywrapper != null) {
         levelchunk$rebindabletickingblockentitywrapper.rebind(NULL_TICKER);
      }

   }

   public void runPostLoad() {
      if (this.postLoad != null) {
         this.postLoad.run(this);
         this.postLoad = null;
      }

   }

   public boolean isEmpty() {
      return false;
   }

   public void replaceWithPacketData(FriendlyByteBuf p_187972_, CompoundTag p_187973_, Consumer<ClientboundLevelChunkPacketData.BlockEntityTagOutput> p_187974_) {
      this.clearAllBlockEntities();

      for(LevelChunkSection levelchunksection : this.sections) {
         levelchunksection.read(p_187972_);
      }

      for(Heightmap.Types heightmap$types : Heightmap.Types.values()) {
         String s = heightmap$types.getSerializationKey();
         if (p_187973_.contains(s, 12)) {
            this.setHeightmap(heightmap$types, p_187973_.getLongArray(s));
         }
      }

      p_187974_.accept((p_187968_, p_187969_, p_187970_) -> {
         BlockEntity blockentity = this.getBlockEntity(p_187968_, LevelChunk.EntityCreationType.IMMEDIATE);
         if (blockentity != null && p_187970_ != null && blockentity.getType() == p_187969_) {
            blockentity.handleUpdateTag(p_187970_);
         }

      });
   }

   public void setLoaded(boolean p_62914_) {
      this.loaded = p_62914_;
   }

   public Level getLevel() {
      return this.level;
   }

   public Map<BlockPos, BlockEntity> getBlockEntities() {
      return this.blockEntities;
   }

   public Stream<BlockPos> getLights() {
      return StreamSupport.stream(BlockPos.betweenClosed(this.chunkPos.getMinBlockX(), this.getMinBuildHeight(), this.chunkPos.getMinBlockZ(), this.chunkPos.getMaxBlockX(), this.getMaxBuildHeight() - 1, this.chunkPos.getMaxBlockZ()).spliterator(), false).filter((p_187990_) -> {
         return this.getBlockState(p_187990_).getLightEmission(getLevel(), p_187990_) != 0;
      });
   }

   public void postProcessGeneration() {
      ChunkPos chunkpos = this.getPos();

      for(int i = 0; i < this.postProcessing.length; ++i) {
         if (this.postProcessing[i] != null) {
            for(Short oshort : this.postProcessing[i]) {
               BlockPos blockpos = ProtoChunk.unpackOffsetCoordinates(oshort, this.getSectionYFromSectionIndex(i), chunkpos);
               BlockState blockstate = this.getBlockState(blockpos);
               FluidState fluidstate = blockstate.getFluidState();
               if (!fluidstate.isEmpty()) {
                  fluidstate.tick(this.level, blockpos);
               }

               if (!(blockstate.getBlock() instanceof LiquidBlock)) {
                  BlockState blockstate1 = Block.updateFromNeighbourShapes(blockstate, this.level, blockpos);
                  this.level.setBlock(blockpos, blockstate1, 20);
               }
            }

            this.postProcessing[i].clear();
         }
      }

      for(BlockPos blockpos1 : ImmutableList.copyOf(this.pendingBlockEntities.keySet())) {
         this.getBlockEntity(blockpos1);
      }

      this.pendingBlockEntities.clear();
      this.upgradeData.upgrade(this);
   }

   @Nullable
   private BlockEntity promotePendingBlockEntity(BlockPos p_62871_, CompoundTag p_62872_) {
      BlockState blockstate = this.getBlockState(p_62871_);
      BlockEntity blockentity;
      if ("DUMMY".equals(p_62872_.getString("id"))) {
         if (blockstate.hasBlockEntity()) {
            blockentity = ((EntityBlock)blockstate.getBlock()).newBlockEntity(p_62871_, blockstate);
         } else {
            blockentity = null;
            LOGGER.warn("Tried to load a DUMMY block entity @ {} but found not block entity block {} at location", p_62871_, blockstate);
         }
      } else {
         blockentity = BlockEntity.loadStatic(p_62871_, blockstate, p_62872_);
      }

      if (blockentity != null) {
         blockentity.setLevel(this.level);
         this.addAndRegisterBlockEntity(blockentity);
      } else {
         LOGGER.warn("Tried to load a block entity for block {} but failed at location {}", blockstate, p_62871_);
      }

      return blockentity;
   }

   public void unpackTicks(long p_187986_) {
      this.blockTicks.unpack(p_187986_);
      this.fluidTicks.unpack(p_187986_);
   }

   public void registerTickContainerInLevel(ServerLevel p_187959_) {
      p_187959_.getBlockTicks().addContainer(this.chunkPos, this.blockTicks);
      p_187959_.getFluidTicks().addContainer(this.chunkPos, this.fluidTicks);
   }

   public void unregisterTickContainerFromLevel(ServerLevel p_187980_) {
      p_187980_.getBlockTicks().removeContainer(this.chunkPos);
      p_187980_.getFluidTicks().removeContainer(this.chunkPos);
   }

   public ChunkStatus getStatus() {
      return ChunkStatus.FULL;
   }

   public ChunkHolder.FullChunkStatus getFullStatus() {
      return this.fullStatus == null ? ChunkHolder.FullChunkStatus.BORDER : this.fullStatus.get();
   }

   public void setFullStatus(Supplier<ChunkHolder.FullChunkStatus> p_62880_) {
      this.fullStatus = p_62880_;
   }

   public void clearAllBlockEntities() {
      this.blockEntities.values().forEach(BlockEntity::onChunkUnloaded);
      this.blockEntities.values().forEach(BlockEntity::setRemoved);
      this.blockEntities.clear();
      this.tickersInLevel.values().forEach((p_187966_) -> {
         p_187966_.rebind(NULL_TICKER);
      });
      this.tickersInLevel.clear();
   }

   public void registerAllBlockEntitiesAfterLevelLoad() {
      this.level.addFreshBlockEntities(this.blockEntities.values());
      this.blockEntities.values().forEach((p_187988_) -> {
         this.addGameEventListener(p_187988_);
         this.updateBlockEntityTicker(p_187988_);
      });
   }

   private <T extends BlockEntity> void addGameEventListener(T p_156405_) {
      if (!this.level.isClientSide) {
         Block block = p_156405_.getBlockState().getBlock();
         if (block instanceof EntityBlock) {
            GameEventListener gameeventlistener = ((EntityBlock)block).getListener(this.level, p_156405_);
            if (gameeventlistener != null) {
               GameEventDispatcher gameeventdispatcher = this.getEventDispatcher(SectionPos.blockToSectionCoord(p_156405_.getBlockPos().getY()));
               gameeventdispatcher.register(gameeventlistener);
            }
         }

      }
   }

   private <T extends BlockEntity> void updateBlockEntityTicker(T p_156407_) {
      BlockState blockstate = p_156407_.getBlockState();
      BlockEntityTicker<T> blockentityticker = blockstate.getTicker(this.level, (BlockEntityType<T>)p_156407_.getType());
      if (blockentityticker == null) {
         this.removeBlockEntityTicker(p_156407_.getBlockPos());
      } else {
         this.tickersInLevel.compute(p_156407_.getBlockPos(), (p_187963_, p_187964_) -> {
            TickingBlockEntity tickingblockentity = this.createTicker(p_156407_, blockentityticker);
            if (p_187964_ != null) {
               p_187964_.rebind(tickingblockentity);
               return p_187964_;
            } else if (this.isInLevel()) {
               LevelChunk.RebindableTickingBlockEntityWrapper levelchunk$rebindabletickingblockentitywrapper = new LevelChunk.RebindableTickingBlockEntityWrapper(tickingblockentity);
               this.level.addBlockEntityTicker(levelchunk$rebindabletickingblockentitywrapper);
               return levelchunk$rebindabletickingblockentitywrapper;
            } else {
               return null;
            }
         });
      }

   }

   private <T extends BlockEntity> TickingBlockEntity createTicker(T p_156376_, BlockEntityTicker<T> p_156377_) {
      return new LevelChunk.BoundTickingBlockEntity<>(p_156376_, p_156377_);
   }

   public boolean isClientLightReady() {
      return this.clientLightReady;
   }

   public void setClientLightReady(boolean p_196865_) {
      this.clientLightReady = p_196865_;
   }

   // FORGE START
   private final net.minecraftforge.common.capabilities.CapabilityProvider.AsField<LevelChunk> capProvider = new net.minecraftforge.common.capabilities.CapabilityProvider.AsField<>(LevelChunk.class, this);

   @org.jetbrains.annotations.NotNull
   @Override
   public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(@org.jetbrains.annotations.NotNull net.minecraftforge.common.capabilities.Capability<T> cap, @org.jetbrains.annotations.Nullable net.minecraft.core.Direction side)
   {
      return capProvider.getCapability(cap, side);
   }

   @Override
   public boolean areCapsCompatible(net.minecraftforge.common.capabilities.CapabilityProvider<LevelChunk> other)
   {
      return capProvider.areCapsCompatible(other);
   }

   @Override
   public boolean areCapsCompatible(@org.jetbrains.annotations.Nullable net.minecraftforge.common.capabilities.CapabilityDispatcher other)
   {
      return capProvider.areCapsCompatible(other);
   }

   @Override
   public void invalidateCaps()
   {
      capProvider.invalidateCaps();
   }

   @Override
   public void reviveCaps()
   {
      capProvider.reviveCaps();
   }
   // FORGE END

   class BoundTickingBlockEntity<T extends BlockEntity> implements TickingBlockEntity {
      private final T blockEntity;
      private final BlockEntityTicker<T> ticker;
      private boolean loggedInvalidBlockState;

      BoundTickingBlockEntity(T p_156433_, BlockEntityTicker<T> p_156434_) {
         this.blockEntity = p_156433_;
         this.ticker = p_156434_;
      }

      public void tick() {
         if (!this.blockEntity.isRemoved() && this.blockEntity.hasLevel()) {
            BlockPos blockpos = this.blockEntity.getBlockPos();
            if (LevelChunk.this.isTicking(blockpos)) {
               try {
                  ProfilerFiller profilerfiller = LevelChunk.this.level.getProfiler();
                  net.minecraftforge.server.timings.TimeTracker.BLOCK_ENTITY_UPDATE.trackStart(blockEntity);
                  profilerfiller.push(this::getType);
                  BlockState blockstate = LevelChunk.this.getBlockState(blockpos);
                  if (this.blockEntity.getType().isValid(blockstate)) {
                     this.ticker.tick(LevelChunk.this.level, this.blockEntity.getBlockPos(), blockstate, this.blockEntity);
                     this.loggedInvalidBlockState = false;
                  } else if (!this.loggedInvalidBlockState) {
                     this.loggedInvalidBlockState = true;
                     LevelChunk.LOGGER.warn("Block entity {} @ {} state {} invalid for ticking:", LogUtils.defer(this::getType), LogUtils.defer(this::getPos), blockstate);
                  }

                  profilerfiller.pop();
               } catch (Throwable throwable) {
                  CrashReport crashreport = CrashReport.forThrowable(throwable, "Ticking block entity");
                  CrashReportCategory crashreportcategory = crashreport.addCategory("Block entity being ticked");
                  this.blockEntity.fillCrashReportCategory(crashreportcategory);

                  if (net.minecraftforge.common.ForgeConfig.SERVER.removeErroringBlockEntities.get()) {
                     LOGGER.error("{}", crashreport.getFriendlyReport());
                     blockEntity.setRemoved();
                     LevelChunk.this.removeBlockEntity(blockEntity.getBlockPos());
                  } else
                  throw new ReportedException(crashreport);
               }
            }
         }

      }

      public boolean isRemoved() {
         return this.blockEntity.isRemoved();
      }

      public BlockPos getPos() {
         return this.blockEntity.getBlockPos();
      }

      public String getType() {
         return BlockEntityType.getKey(this.blockEntity.getType()).toString();
      }

      public String toString() {
         return "Level ticker for " + this.getType() + "@" + this.getPos();
      }
   }

   public static enum EntityCreationType {
      IMMEDIATE,
      QUEUED,
      CHECK;
   }


   /**
    * <strong>FOR INTERNAL USE ONLY</strong>
    * <p>
    * Only public for use in {@link net.minecraft.world.level.chunk.storage.ChunkSerializer}.
    */
   @java.lang.Deprecated
   @javax.annotation.Nullable
   public final CompoundTag writeCapsToNBT() {
      return capProvider.serializeInternal();
   }

   /**
    * <strong>FOR INTERNAL USE ONLY</strong>
    * <p>
    * Only public for use in {@link net.minecraft.world.level.chunk.storage.ChunkSerializer}.
    *
    */
   @java.lang.Deprecated
   public final void readCapsFromNBT(CompoundTag tag) {
      capProvider.deserializeInternal(tag);
   }

   @Override
   public Level getWorldForge() {
      return getLevel();
   }

   @FunctionalInterface
   public interface PostLoadProcessor {
      void run(LevelChunk p_196867_);
   }

   class RebindableTickingBlockEntityWrapper implements TickingBlockEntity {
      private TickingBlockEntity ticker;

      RebindableTickingBlockEntityWrapper(TickingBlockEntity p_156447_) {
         this.ticker = p_156447_;
      }

      void rebind(TickingBlockEntity p_156450_) {
         this.ticker = p_156450_;
      }

      public void tick() {
         this.ticker.tick();
      }

      public boolean isRemoved() {
         return this.ticker.isRemoved();
      }

      public BlockPos getPos() {
         return this.ticker.getPos();
      }

      public String getType() {
         return this.ticker.getType();
      }

      public String toString() {
         return this.ticker.toString() + " <wrapped>";
      }
   }
}
