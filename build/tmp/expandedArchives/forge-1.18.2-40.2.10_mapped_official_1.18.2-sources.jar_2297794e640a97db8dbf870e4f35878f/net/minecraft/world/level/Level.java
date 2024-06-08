package net.minecraft.world.level;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.scores.Scoreboard;

public abstract class Level extends net.minecraftforge.common.capabilities.CapabilityProvider<Level> implements LevelAccessor, AutoCloseable, net.minecraftforge.common.extensions.IForgeLevel {
   public static final Codec<ResourceKey<Level>> RESOURCE_KEY_CODEC = ResourceLocation.CODEC.xmap(ResourceKey.elementKey(Registry.DIMENSION_REGISTRY), ResourceKey::location);
   public static final ResourceKey<Level> OVERWORLD = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation("overworld"));
   public static final ResourceKey<Level> NETHER = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation("the_nether"));
   public static final ResourceKey<Level> END = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation("the_end"));
   public static final int MAX_LEVEL_SIZE = 30000000;
   public static final int LONG_PARTICLE_CLIP_RANGE = 512;
   public static final int SHORT_PARTICLE_CLIP_RANGE = 32;
   private static final Direction[] DIRECTIONS = Direction.values();
   public static final int MAX_BRIGHTNESS = 15;
   public static final int TICKS_PER_DAY = 24000;
   public static final int MAX_ENTITY_SPAWN_Y = 20000000;
   public static final int MIN_ENTITY_SPAWN_Y = -20000000;
   protected final List<TickingBlockEntity> blockEntityTickers = Lists.newArrayList();
   private final List<TickingBlockEntity> pendingBlockEntityTickers = Lists.newArrayList();
   private boolean tickingBlockEntities;
   private final Thread thread;
   private final boolean isDebug;
   public int skyDarken;
   protected int randValue = (new Random()).nextInt();
   protected final int addend = 1013904223;
   public float oRainLevel;
   public float rainLevel;
   public float oThunderLevel;
   public float thunderLevel;
   public final Random random = new Random();
   final DimensionType dimensionType;
   private final Holder<DimensionType> dimensionTypeRegistration;
   protected final WritableLevelData levelData;
   private final Supplier<ProfilerFiller> profiler;
   public final boolean isClientSide;
   private final WorldBorder worldBorder;
   private final BiomeManager biomeManager;
   private final ResourceKey<Level> dimension;
   private long subTickCount;
   public boolean restoringBlockSnapshots = false;
   public boolean captureBlockSnapshots = false;
   public java.util.ArrayList<net.minecraftforge.common.util.BlockSnapshot> capturedBlockSnapshots = new java.util.ArrayList<>();
   private final java.util.ArrayList<BlockEntity> freshBlockEntities = new java.util.ArrayList<>();
   private final java.util.ArrayList<BlockEntity> pendingFreshBlockEntities = new java.util.ArrayList<>();

   protected Level(WritableLevelData p_204149_, ResourceKey<Level> p_204150_, Holder<DimensionType> p_204151_, Supplier<ProfilerFiller> p_204152_, boolean p_204153_, boolean p_204154_, long p_204155_) {
      super(Level.class);
      this.profiler = p_204152_;
      this.levelData = p_204149_;
      this.dimensionTypeRegistration = p_204151_;
      this.dimensionType = p_204151_.value();
      this.dimension = p_204150_;
      this.isClientSide = p_204153_;
      if (this.dimensionType.coordinateScale() != 1.0D) {
         this.worldBorder = new WorldBorder() {
            public double getCenterX() {
               return super.getCenterX() / Level.this.dimensionType.coordinateScale();
            }

            public double getCenterZ() {
               return super.getCenterZ() / Level.this.dimensionType.coordinateScale();
            }
         };
      } else {
         this.worldBorder = new WorldBorder();
      }

      this.thread = Thread.currentThread();
      this.biomeManager = new BiomeManager(this, p_204155_);
      this.isDebug = p_204154_;
   }

   public boolean isClientSide() {
      return this.isClientSide;
   }

   @Nullable
   public MinecraftServer getServer() {
      return null;
   }

   public boolean isInWorldBounds(BlockPos p_46740_) {
      return !this.isOutsideBuildHeight(p_46740_) && isInWorldBoundsHorizontal(p_46740_);
   }

   public static boolean isInSpawnableBounds(BlockPos p_46742_) {
      return !isOutsideSpawnableHeight(p_46742_.getY()) && isInWorldBoundsHorizontal(p_46742_);
   }

   private static boolean isInWorldBoundsHorizontal(BlockPos p_46458_) {
      return p_46458_.getX() >= -30000000 && p_46458_.getZ() >= -30000000 && p_46458_.getX() < 30000000 && p_46458_.getZ() < 30000000;
   }

   private static boolean isOutsideSpawnableHeight(int p_46725_) {
      return p_46725_ < -20000000 || p_46725_ >= 20000000;
   }

   public LevelChunk getChunkAt(BlockPos p_46746_) {
      return this.getChunk(SectionPos.blockToSectionCoord(p_46746_.getX()), SectionPos.blockToSectionCoord(p_46746_.getZ()));
   }

   public LevelChunk getChunk(int p_46727_, int p_46728_) {
      return (LevelChunk)this.getChunk(p_46727_, p_46728_, ChunkStatus.FULL);
   }

   @Nullable
   public ChunkAccess getChunk(int p_46502_, int p_46503_, ChunkStatus p_46504_, boolean p_46505_) {
      ChunkAccess chunkaccess = this.getChunkSource().getChunk(p_46502_, p_46503_, p_46504_, p_46505_);
      if (chunkaccess == null && p_46505_) {
         throw new IllegalStateException("Should always be able to create a chunk!");
      } else {
         return chunkaccess;
      }
   }

   public boolean setBlock(BlockPos p_46601_, BlockState p_46602_, int p_46603_) {
      return this.setBlock(p_46601_, p_46602_, p_46603_, 512);
   }

   public boolean setBlock(BlockPos p_46605_, BlockState p_46606_, int p_46607_, int p_46608_) {
      if (this.isOutsideBuildHeight(p_46605_)) {
         return false;
      } else if (!this.isClientSide && this.isDebug()) {
         return false;
      } else {
         LevelChunk levelchunk = this.getChunkAt(p_46605_);
         Block block = p_46606_.getBlock();

         p_46605_ = p_46605_.immutable(); // Forge - prevent mutable BlockPos leaks
         net.minecraftforge.common.util.BlockSnapshot blockSnapshot = null;
         if (this.captureBlockSnapshots && !this.isClientSide) {
             blockSnapshot = net.minecraftforge.common.util.BlockSnapshot.create(this.dimension, this, p_46605_, p_46607_);
             this.capturedBlockSnapshots.add(blockSnapshot);
         }

         BlockState old = getBlockState(p_46605_);
         int oldLight = old.getLightEmission(this, p_46605_);
         int oldOpacity = old.getLightBlock(this, p_46605_);

         BlockState blockstate = levelchunk.setBlockState(p_46605_, p_46606_, (p_46607_ & 64) != 0);
         if (blockstate == null) {
            if (blockSnapshot != null) this.capturedBlockSnapshots.remove(blockSnapshot);
            return false;
         } else {
            BlockState blockstate1 = this.getBlockState(p_46605_);
            if ((p_46607_ & 128) == 0 && blockstate1 != blockstate && (blockstate1.getLightBlock(this, p_46605_) != oldOpacity || blockstate1.getLightEmission(this, p_46605_) != oldLight || blockstate1.useShapeForLightOcclusion() || blockstate.useShapeForLightOcclusion())) {
               this.getProfiler().push("queueCheckLight");
               this.getChunkSource().getLightEngine().checkBlock(p_46605_);
               this.getProfiler().pop();
            }

            if (blockSnapshot == null) { // Don't notify clients or update physics while capturing blockstates
               this.markAndNotifyBlock(p_46605_, levelchunk, blockstate, p_46606_, p_46607_, p_46608_);
            }

            return true;
         }
      }
   }

   // Split off from original setBlockState(BlockPos, BlockState, int, int) method in order to directly send client and physic updates
   public void markAndNotifyBlock(BlockPos p_46605_, @Nullable LevelChunk levelchunk, BlockState blockstate, BlockState p_46606_, int p_46607_, int p_46608_) {
      Block block = p_46606_.getBlock();
      BlockState blockstate1 = getBlockState(p_46605_);
      {
         {
            if (blockstate1 == p_46606_) {
               if (blockstate != blockstate1) {
                  this.setBlocksDirty(p_46605_, blockstate, blockstate1);
               }

               if ((p_46607_ & 2) != 0 && (!this.isClientSide || (p_46607_ & 4) == 0) && (this.isClientSide || levelchunk.getFullStatus() != null && levelchunk.getFullStatus().isOrAfter(ChunkHolder.FullChunkStatus.TICKING))) {
                  this.sendBlockUpdated(p_46605_, blockstate, p_46606_, p_46607_);
               }

               if ((p_46607_ & 1) != 0) {
                  this.blockUpdated(p_46605_, blockstate.getBlock());
                  if (!this.isClientSide && p_46606_.hasAnalogOutputSignal()) {
                     this.updateNeighbourForOutputSignal(p_46605_, block);
                  }
               }

               if ((p_46607_ & 16) == 0 && p_46608_ > 0) {
                  int i = p_46607_ & -34;
                  blockstate.updateIndirectNeighbourShapes(this, p_46605_, i, p_46608_ - 1);
                  p_46606_.updateNeighbourShapes(this, p_46605_, i, p_46608_ - 1);
                  p_46606_.updateIndirectNeighbourShapes(this, p_46605_, i, p_46608_ - 1);
               }

               this.onBlockStateChange(p_46605_, blockstate, blockstate1);
               p_46606_.onBlockStateChange(this, p_46605_, blockstate);
            }
         }
      }
   }

   public void onBlockStateChange(BlockPos p_46609_, BlockState p_46610_, BlockState p_46611_) {
   }

   public boolean removeBlock(BlockPos p_46623_, boolean p_46624_) {
      FluidState fluidstate = this.getFluidState(p_46623_);
      return this.setBlock(p_46623_, fluidstate.createLegacyBlock(), 3 | (p_46624_ ? 64 : 0));
   }

   public boolean destroyBlock(BlockPos p_46626_, boolean p_46627_, @Nullable Entity p_46628_, int p_46629_) {
      BlockState blockstate = this.getBlockState(p_46626_);
      if (blockstate.isAir()) {
         return false;
      } else {
         FluidState fluidstate = this.getFluidState(p_46626_);
         if (!(blockstate.getBlock() instanceof BaseFireBlock)) {
            this.levelEvent(2001, p_46626_, Block.getId(blockstate));
         }

         if (p_46627_) {
            BlockEntity blockentity = blockstate.hasBlockEntity() ? this.getBlockEntity(p_46626_) : null;
            Block.dropResources(blockstate, this, p_46626_, blockentity, p_46628_, ItemStack.EMPTY);
         }

         boolean flag = this.setBlock(p_46626_, fluidstate.createLegacyBlock(), 3, p_46629_);
         if (flag) {
            this.gameEvent(p_46628_, GameEvent.BLOCK_DESTROY, p_46626_);
         }

         return flag;
      }
   }

   public void addDestroyBlockEffect(BlockPos p_151531_, BlockState p_151532_) {
   }

   public boolean setBlockAndUpdate(BlockPos p_46598_, BlockState p_46599_) {
      return this.setBlock(p_46598_, p_46599_, 3);
   }

   public abstract void sendBlockUpdated(BlockPos p_46612_, BlockState p_46613_, BlockState p_46614_, int p_46615_);

   public void setBlocksDirty(BlockPos p_46678_, BlockState p_46679_, BlockState p_46680_) {
   }

   public void updateNeighborsAt(BlockPos p_46673_, Block p_46674_) {
      if (net.minecraftforge.event.ForgeEventFactory.onNeighborNotify(this, p_46673_, this.getBlockState(p_46673_), java.util.EnumSet.allOf(Direction.class), false).isCanceled())
         return;
      this.neighborChanged(p_46673_.west(), p_46674_, p_46673_);
      this.neighborChanged(p_46673_.east(), p_46674_, p_46673_);
      this.neighborChanged(p_46673_.below(), p_46674_, p_46673_);
      this.neighborChanged(p_46673_.above(), p_46674_, p_46673_);
      this.neighborChanged(p_46673_.north(), p_46674_, p_46673_);
      this.neighborChanged(p_46673_.south(), p_46674_, p_46673_);
   }

   public void updateNeighborsAtExceptFromFacing(BlockPos p_46591_, Block p_46592_, Direction p_46593_) {
      java.util.EnumSet<Direction> directions = java.util.EnumSet.allOf(Direction.class);
      directions.remove(p_46593_);
      if (net.minecraftforge.event.ForgeEventFactory.onNeighborNotify(this, p_46591_, this.getBlockState(p_46591_), directions, false).isCanceled())
         return;

      if (p_46593_ != Direction.WEST) {
         this.neighborChanged(p_46591_.west(), p_46592_, p_46591_);
      }

      if (p_46593_ != Direction.EAST) {
         this.neighborChanged(p_46591_.east(), p_46592_, p_46591_);
      }

      if (p_46593_ != Direction.DOWN) {
         this.neighborChanged(p_46591_.below(), p_46592_, p_46591_);
      }

      if (p_46593_ != Direction.UP) {
         this.neighborChanged(p_46591_.above(), p_46592_, p_46591_);
      }

      if (p_46593_ != Direction.NORTH) {
         this.neighborChanged(p_46591_.north(), p_46592_, p_46591_);
      }

      if (p_46593_ != Direction.SOUTH) {
         this.neighborChanged(p_46591_.south(), p_46592_, p_46591_);
      }

   }

   public void neighborChanged(BlockPos p_46587_, Block p_46588_, BlockPos p_46589_) {
      if (!this.isClientSide) {
         BlockState blockstate = this.getBlockState(p_46587_);

         try {
            blockstate.neighborChanged(this, p_46587_, p_46588_, p_46589_, false);
         } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.forThrowable(throwable, "Exception while updating neighbours");
            CrashReportCategory crashreportcategory = crashreport.addCategory("Block being updated");
            crashreportcategory.setDetail("Source block type", () -> {
               try {
                  return String.format("ID #%s (%s // %s)", p_46588_.getRegistryName(), p_46588_.getDescriptionId(), p_46588_.getClass().getCanonicalName());
               } catch (Throwable throwable1) {
                  return "ID #" + p_46588_.getRegistryName();
               }
            });
            CrashReportCategory.populateBlockDetails(crashreportcategory, this, p_46587_, blockstate);
            throw new ReportedException(crashreport);
         }
      }
   }

   public int getHeight(Heightmap.Types p_46571_, int p_46572_, int p_46573_) {
      int i;
      if (p_46572_ >= -30000000 && p_46573_ >= -30000000 && p_46572_ < 30000000 && p_46573_ < 30000000) {
         if (this.hasChunk(SectionPos.blockToSectionCoord(p_46572_), SectionPos.blockToSectionCoord(p_46573_))) {
            i = this.getChunk(SectionPos.blockToSectionCoord(p_46572_), SectionPos.blockToSectionCoord(p_46573_)).getHeight(p_46571_, p_46572_ & 15, p_46573_ & 15) + 1;
         } else {
            i = this.getMinBuildHeight();
         }
      } else {
         i = this.getSeaLevel() + 1;
      }

      return i;
   }

   public LevelLightEngine getLightEngine() {
      return this.getChunkSource().getLightEngine();
   }

   public BlockState getBlockState(BlockPos p_46732_) {
      if (this.isOutsideBuildHeight(p_46732_)) {
         return Blocks.VOID_AIR.defaultBlockState();
      } else {
         LevelChunk levelchunk = this.getChunk(SectionPos.blockToSectionCoord(p_46732_.getX()), SectionPos.blockToSectionCoord(p_46732_.getZ()));
         return levelchunk.getBlockState(p_46732_);
      }
   }

   public FluidState getFluidState(BlockPos p_46671_) {
      if (this.isOutsideBuildHeight(p_46671_)) {
         return Fluids.EMPTY.defaultFluidState();
      } else {
         LevelChunk levelchunk = this.getChunkAt(p_46671_);
         return levelchunk.getFluidState(p_46671_);
      }
   }

   public boolean isDay() {
      return !this.dimensionType().hasFixedTime() && this.skyDarken < 4;
   }

   public boolean isNight() {
      return !this.dimensionType().hasFixedTime() && !this.isDay();
   }

   public void playSound(@Nullable Player p_46560_, BlockPos p_46561_, SoundEvent p_46562_, SoundSource p_46563_, float p_46564_, float p_46565_) {
      this.playSound(p_46560_, (double)p_46561_.getX() + 0.5D, (double)p_46561_.getY() + 0.5D, (double)p_46561_.getZ() + 0.5D, p_46562_, p_46563_, p_46564_, p_46565_);
   }

   public abstract void playSound(@Nullable Player p_46543_, double p_46544_, double p_46545_, double p_46546_, SoundEvent p_46547_, SoundSource p_46548_, float p_46549_, float p_46550_);

   public abstract void playSound(@Nullable Player p_46551_, Entity p_46552_, SoundEvent p_46553_, SoundSource p_46554_, float p_46555_, float p_46556_);

   public void playLocalSound(double p_46482_, double p_46483_, double p_46484_, SoundEvent p_46485_, SoundSource p_46486_, float p_46487_, float p_46488_, boolean p_46489_) {
   }

   public void addParticle(ParticleOptions p_46631_, double p_46632_, double p_46633_, double p_46634_, double p_46635_, double p_46636_, double p_46637_) {
   }

   public void addParticle(ParticleOptions p_46638_, boolean p_46639_, double p_46640_, double p_46641_, double p_46642_, double p_46643_, double p_46644_, double p_46645_) {
   }

   public void addAlwaysVisibleParticle(ParticleOptions p_46684_, double p_46685_, double p_46686_, double p_46687_, double p_46688_, double p_46689_, double p_46690_) {
   }

   public void addAlwaysVisibleParticle(ParticleOptions p_46691_, boolean p_46692_, double p_46693_, double p_46694_, double p_46695_, double p_46696_, double p_46697_, double p_46698_) {
   }

   public float getSunAngle(float p_46491_) {
      float f = this.getTimeOfDay(p_46491_);
      return f * ((float)Math.PI * 2F);
   }

   public void addBlockEntityTicker(TickingBlockEntity p_151526_) {
      (this.tickingBlockEntities ? this.pendingBlockEntityTickers : this.blockEntityTickers).add(p_151526_);
   }

   public void addFreshBlockEntities(java.util.Collection<BlockEntity> beList) {
      if (this.tickingBlockEntities) {
         this.pendingFreshBlockEntities.addAll(beList);
      } else {
         this.freshBlockEntities.addAll(beList);
      }
   }

   protected void tickBlockEntities() {
      ProfilerFiller profilerfiller = this.getProfiler();
      profilerfiller.push("blockEntities");
      if (!this.pendingFreshBlockEntities.isEmpty()) {
         this.freshBlockEntities.addAll(this.pendingFreshBlockEntities);
         this.pendingFreshBlockEntities.clear();
      }
      this.tickingBlockEntities = true;
      if (!this.freshBlockEntities.isEmpty()) {
         this.freshBlockEntities.forEach(BlockEntity::onLoad);
         this.freshBlockEntities.clear();
      }
      if (!this.pendingBlockEntityTickers.isEmpty()) {
         this.blockEntityTickers.addAll(this.pendingBlockEntityTickers);
         this.pendingBlockEntityTickers.clear();
      }

      Iterator<TickingBlockEntity> iterator = this.blockEntityTickers.iterator();

      while(iterator.hasNext()) {
         TickingBlockEntity tickingblockentity = iterator.next();
         if (tickingblockentity.isRemoved()) {
            iterator.remove();
         } else if (this.shouldTickBlocksAt(ChunkPos.asLong(tickingblockentity.getPos()))) {
            tickingblockentity.tick();
         }
      }

      this.tickingBlockEntities = false;
      profilerfiller.pop();
   }

   public <T extends Entity> void guardEntityTick(Consumer<T> p_46654_, T p_46655_) {
      try {
         net.minecraftforge.server.timings.TimeTracker.ENTITY_UPDATE.trackStart(p_46655_);
         p_46654_.accept(p_46655_);
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.forThrowable(throwable, "Ticking entity");
         CrashReportCategory crashreportcategory = crashreport.addCategory("Entity being ticked");
         p_46655_.fillCrashReportCategory(crashreportcategory);
         if (net.minecraftforge.common.ForgeConfig.SERVER.removeErroringEntities.get()) {
            com.mojang.logging.LogUtils.getLogger().error("{}", crashreport.getFriendlyReport());
            p_46655_.discard();
         } else
         throw new ReportedException(crashreport);
      } finally {
         net.minecraftforge.server.timings.TimeTracker.ENTITY_UPDATE.trackEnd(p_46655_);
      }
   }

   public boolean shouldTickDeath(Entity p_186458_) {
      return true;
   }

   public boolean shouldTickBlocksAt(long p_186456_) {
      return true;
   }

   public Explosion explode(@Nullable Entity p_46512_, double p_46513_, double p_46514_, double p_46515_, float p_46516_, Explosion.BlockInteraction p_46517_) {
      return this.explode(p_46512_, (DamageSource)null, (ExplosionDamageCalculator)null, p_46513_, p_46514_, p_46515_, p_46516_, false, p_46517_);
   }

   public Explosion explode(@Nullable Entity p_46519_, double p_46520_, double p_46521_, double p_46522_, float p_46523_, boolean p_46524_, Explosion.BlockInteraction p_46525_) {
      return this.explode(p_46519_, (DamageSource)null, (ExplosionDamageCalculator)null, p_46520_, p_46521_, p_46522_, p_46523_, p_46524_, p_46525_);
   }

   public Explosion explode(@Nullable Entity p_46526_, @Nullable DamageSource p_46527_, @Nullable ExplosionDamageCalculator p_46528_, double p_46529_, double p_46530_, double p_46531_, float p_46532_, boolean p_46533_, Explosion.BlockInteraction p_46534_) {
      Explosion explosion = new Explosion(this, p_46526_, p_46527_, p_46528_, p_46529_, p_46530_, p_46531_, p_46532_, p_46533_, p_46534_);
      if (net.minecraftforge.event.ForgeEventFactory.onExplosionStart(this, explosion)) return explosion;
      explosion.explode();
      explosion.finalizeExplosion(true);
      return explosion;
   }

   public abstract String gatherChunkSourceStats();

   @Nullable
   public BlockEntity getBlockEntity(BlockPos p_46716_) {
      if (this.isOutsideBuildHeight(p_46716_)) {
         return null;
      } else {
         return !this.isClientSide && Thread.currentThread() != this.thread ? null : this.getChunkAt(p_46716_).getBlockEntity(p_46716_, LevelChunk.EntityCreationType.IMMEDIATE);
      }
   }

   public void setBlockEntity(BlockEntity p_151524_) {
      BlockPos blockpos = p_151524_.getBlockPos();
      if (!this.isOutsideBuildHeight(blockpos)) {
         this.getChunkAt(blockpos).addAndRegisterBlockEntity(p_151524_);
      }
   }

   public void removeBlockEntity(BlockPos p_46748_) {
      if (!this.isOutsideBuildHeight(p_46748_)) {
         this.getChunkAt(p_46748_).removeBlockEntity(p_46748_);
      }
      this.updateNeighbourForOutputSignal(p_46748_, getBlockState(p_46748_).getBlock()); //Notify neighbors of changes
   }

   public boolean isLoaded(BlockPos p_46750_) {
      return this.isOutsideBuildHeight(p_46750_) ? false : this.getChunkSource().hasChunk(SectionPos.blockToSectionCoord(p_46750_.getX()), SectionPos.blockToSectionCoord(p_46750_.getZ()));
   }

   public boolean loadedAndEntityCanStandOnFace(BlockPos p_46579_, Entity p_46580_, Direction p_46581_) {
      if (this.isOutsideBuildHeight(p_46579_)) {
         return false;
      } else {
         ChunkAccess chunkaccess = this.getChunk(SectionPos.blockToSectionCoord(p_46579_.getX()), SectionPos.blockToSectionCoord(p_46579_.getZ()), ChunkStatus.FULL, false);
         return chunkaccess == null ? false : chunkaccess.getBlockState(p_46579_).entityCanStandOnFace(this, p_46579_, p_46580_, p_46581_);
      }
   }

   public boolean loadedAndEntityCanStandOn(BlockPos p_46576_, Entity p_46577_) {
      return this.loadedAndEntityCanStandOnFace(p_46576_, p_46577_, Direction.UP);
   }

   public void updateSkyBrightness() {
      double d0 = 1.0D - (double)(this.getRainLevel(1.0F) * 5.0F) / 16.0D;
      double d1 = 1.0D - (double)(this.getThunderLevel(1.0F) * 5.0F) / 16.0D;
      double d2 = 0.5D + 2.0D * Mth.clamp((double)Mth.cos(this.getTimeOfDay(1.0F) * ((float)Math.PI * 2F)), -0.25D, 0.25D);
      this.skyDarken = (int)((1.0D - d2 * d0 * d1) * 11.0D);
   }

   public void setSpawnSettings(boolean p_46704_, boolean p_46705_) {
      this.getChunkSource().setSpawnSettings(p_46704_, p_46705_);
   }

   protected void prepareWeather() {
      if (this.levelData.isRaining()) {
         this.rainLevel = 1.0F;
         if (this.levelData.isThundering()) {
            this.thunderLevel = 1.0F;
         }
      }

   }

   public void close() throws IOException {
      this.getChunkSource().close();
   }

   @Nullable
   public BlockGetter getChunkForCollisions(int p_46711_, int p_46712_) {
      return this.getChunk(p_46711_, p_46712_, ChunkStatus.FULL, false);
   }

   public List<Entity> getEntities(@Nullable Entity p_46536_, AABB p_46537_, Predicate<? super Entity> p_46538_) {
      this.getProfiler().incrementCounter("getEntities");
      List<Entity> list = Lists.newArrayList();
      this.getEntities().get(p_46537_, (p_151522_) -> {
         if (p_151522_ != p_46536_ && p_46538_.test(p_151522_)) {
            list.add(p_151522_);
         }

         if (false)
         if (p_151522_ instanceof EnderDragon) {
            for(EnderDragonPart enderdragonpart : ((EnderDragon)p_151522_).getSubEntities()) {
               if (p_151522_ != p_46536_ && p_46538_.test(enderdragonpart)) {
                  list.add(enderdragonpart);
               }
            }
         }

      });
      for (net.minecraftforge.entity.PartEntity<?> p : this.getPartEntities()) {
         if (p != p_46536_ && p.getBoundingBox().intersects(p_46537_) && p_46538_.test(p)) {
            list.add(p);
         }
      }
      return list;
   }

   public <T extends Entity> List<T> getEntities(EntityTypeTest<Entity, T> p_151528_, AABB p_151529_, Predicate<? super T> p_151530_) {
      this.getProfiler().incrementCounter("getEntities");
      List<T> list = Lists.newArrayList();
      this.getEntities().get(p_151528_, p_151529_, (p_151539_) -> {
         if (p_151530_.test(p_151539_)) {
            list.add(p_151539_);
         }


         if (false)
         if (p_151539_ instanceof EnderDragon) {
            EnderDragon enderdragon = (EnderDragon)p_151539_;

            for(EnderDragonPart enderdragonpart : enderdragon.getSubEntities()) {
               T t = p_151528_.tryCast(enderdragonpart);
               if (t != null && p_151530_.test(t)) {
                  list.add(t);
               }
            }
         }

      });
      for (net.minecraftforge.entity.PartEntity<?> p : this.getPartEntities()) {
         T t = p_151528_.tryCast(p);
         if (t != null && t.getBoundingBox().intersects(p_151529_) && p_151530_.test(t)) {
            list.add(t);
         }
      }
      return list;
   }

   @Nullable
   public abstract Entity getEntity(int p_46492_);

   public void blockEntityChangedWithoutNeighborUpdates(BlockPos p_151544_) {
      if (this.hasChunkAt(p_151544_)) {
         this.getChunkAt(p_151544_).setUnsaved(true);
      }
   }

   /** @deprecated Call {@link #blockEntityChangedWithoutNeighborUpdates(BlockPos)} instead. See <a href="https://github.com/MinecraftForge/MinecraftForge/pull/9235">#9235</a> for details. */
   @Deprecated
   public void blockEntityChanged(BlockPos p_151544_) {
      this.blockEntityChangedWithoutNeighborUpdates(p_151544_);
      this.updateNeighbourForOutputSignal(p_151544_, getBlockState(p_151544_).getBlock()); //Notify neighbors of changes
   }

   public int getSeaLevel() {
      return 63;
   }

   public int getDirectSignalTo(BlockPos p_46752_) {
      int i = 0;
      i = Math.max(i, this.getDirectSignal(p_46752_.below(), Direction.DOWN));
      if (i >= 15) {
         return i;
      } else {
         i = Math.max(i, this.getDirectSignal(p_46752_.above(), Direction.UP));
         if (i >= 15) {
            return i;
         } else {
            i = Math.max(i, this.getDirectSignal(p_46752_.north(), Direction.NORTH));
            if (i >= 15) {
               return i;
            } else {
               i = Math.max(i, this.getDirectSignal(p_46752_.south(), Direction.SOUTH));
               if (i >= 15) {
                  return i;
               } else {
                  i = Math.max(i, this.getDirectSignal(p_46752_.west(), Direction.WEST));
                  if (i >= 15) {
                     return i;
                  } else {
                     i = Math.max(i, this.getDirectSignal(p_46752_.east(), Direction.EAST));
                     return i >= 15 ? i : i;
                  }
               }
            }
         }
      }
   }

   public boolean hasSignal(BlockPos p_46617_, Direction p_46618_) {
      return this.getSignal(p_46617_, p_46618_) > 0;
   }

   public int getSignal(BlockPos p_46682_, Direction p_46683_) {
      BlockState blockstate = this.getBlockState(p_46682_);
      int i = blockstate.getSignal(this, p_46682_, p_46683_);
      return blockstate.shouldCheckWeakPower(this, p_46682_, p_46683_) ? Math.max(i, this.getDirectSignalTo(p_46682_)) : i;
   }

   public boolean hasNeighborSignal(BlockPos p_46754_) {
      if (this.getSignal(p_46754_.below(), Direction.DOWN) > 0) {
         return true;
      } else if (this.getSignal(p_46754_.above(), Direction.UP) > 0) {
         return true;
      } else if (this.getSignal(p_46754_.north(), Direction.NORTH) > 0) {
         return true;
      } else if (this.getSignal(p_46754_.south(), Direction.SOUTH) > 0) {
         return true;
      } else if (this.getSignal(p_46754_.west(), Direction.WEST) > 0) {
         return true;
      } else {
         return this.getSignal(p_46754_.east(), Direction.EAST) > 0;
      }
   }

   public int getBestNeighborSignal(BlockPos p_46756_) {
      int i = 0;

      for(Direction direction : DIRECTIONS) {
         int j = this.getSignal(p_46756_.relative(direction), direction);
         if (j >= 15) {
            return 15;
         }

         if (j > i) {
            i = j;
         }
      }

      return i;
   }

   public void disconnect() {
   }

   public long getGameTime() {
      return this.levelData.getGameTime();
   }

   public long getDayTime() {
      return this.levelData.getDayTime();
   }

   public boolean mayInteract(Player p_46557_, BlockPos p_46558_) {
      return true;
   }

   public void broadcastEntityEvent(Entity p_46509_, byte p_46510_) {
   }

   public void blockEvent(BlockPos p_46582_, Block p_46583_, int p_46584_, int p_46585_) {
      this.getBlockState(p_46582_).triggerEvent(this, p_46582_, p_46584_, p_46585_);
   }

   public LevelData getLevelData() {
      return this.levelData;
   }

   public GameRules getGameRules() {
      return this.levelData.getGameRules();
   }

   public float getThunderLevel(float p_46662_) {
      return Mth.lerp(p_46662_, this.oThunderLevel, this.thunderLevel) * this.getRainLevel(p_46662_);
   }

   public void setThunderLevel(float p_46708_) {
      float f = Mth.clamp(p_46708_, 0.0F, 1.0F);
      this.oThunderLevel = f;
      this.thunderLevel = f;
   }

   public float getRainLevel(float p_46723_) {
      return Mth.lerp(p_46723_, this.oRainLevel, this.rainLevel);
   }

   public void setRainLevel(float p_46735_) {
      float f = Mth.clamp(p_46735_, 0.0F, 1.0F);
      this.oRainLevel = f;
      this.rainLevel = f;
   }

   public boolean isThundering() {
      if (this.dimensionType().hasSkyLight() && !this.dimensionType().hasCeiling()) {
         return (double)this.getThunderLevel(1.0F) > 0.9D;
      } else {
         return false;
      }
   }

   public boolean isRaining() {
      return (double)this.getRainLevel(1.0F) > 0.2D;
   }

   public boolean isRainingAt(BlockPos p_46759_) {
      if (!this.isRaining()) {
         return false;
      } else if (!this.canSeeSky(p_46759_)) {
         return false;
      } else if (this.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, p_46759_).getY() > p_46759_.getY()) {
         return false;
      } else {
         Biome biome = this.getBiome(p_46759_).value();
         return biome.getPrecipitation() == Biome.Precipitation.RAIN && biome.warmEnoughToRain(p_46759_);
      }
   }

   public boolean isHumidAt(BlockPos p_46762_) {
      Biome biome = this.getBiome(p_46762_).value();
      return biome.isHumid();
   }

   @Nullable
   public abstract MapItemSavedData getMapData(String p_46650_);

   public abstract void setMapData(String p_151533_, MapItemSavedData p_151534_);

   public abstract int getFreeMapId();

   public void globalLevelEvent(int p_46665_, BlockPos p_46666_, int p_46667_) {
   }

   public CrashReportCategory fillReportDetails(CrashReport p_46656_) {
      CrashReportCategory crashreportcategory = p_46656_.addCategory("Affected level", 1);
      crashreportcategory.setDetail("All players", () -> {
         return this.players().size() + " total; " + this.players();
      });
      crashreportcategory.setDetail("Chunk stats", this.getChunkSource()::gatherStats);
      crashreportcategory.setDetail("Level dimension", () -> {
         return this.dimension().location().toString();
      });

      try {
         this.levelData.fillCrashReportCategory(crashreportcategory, this);
      } catch (Throwable throwable) {
         crashreportcategory.setDetailError("Level Data Unobtainable", throwable);
      }

      return crashreportcategory;
   }

   public abstract void destroyBlockProgress(int p_46506_, BlockPos p_46507_, int p_46508_);

   public void createFireworks(double p_46475_, double p_46476_, double p_46477_, double p_46478_, double p_46479_, double p_46480_, @Nullable CompoundTag p_46481_) {
   }

   public abstract Scoreboard getScoreboard();

   public void updateNeighbourForOutputSignal(BlockPos p_46718_, Block p_46719_) {
      for(Direction direction : Direction.values()) {
         BlockPos blockpos = p_46718_.relative(direction);
         if (this.hasChunkAt(blockpos)) {
            BlockState blockstate = this.getBlockState(blockpos);
            blockstate.onNeighborChange(this, blockpos, p_46718_);
            if (blockstate.isRedstoneConductor(this, blockpos)) {
               blockpos = blockpos.relative(direction);
               blockstate = this.getBlockState(blockpos);
               if (blockstate.getWeakChanges(this, blockpos)) {
                  blockstate.neighborChanged(this, blockpos, p_46719_, p_46718_, false);
               }
            }
         }
      }

   }

   public DifficultyInstance getCurrentDifficultyAt(BlockPos p_46730_) {
      long i = 0L;
      float f = 0.0F;
      if (this.hasChunkAt(p_46730_)) {
         f = this.getMoonBrightness();
         i = this.getChunkAt(p_46730_).getInhabitedTime();
      }

      return new DifficultyInstance(this.getDifficulty(), this.getDayTime(), i, f);
   }

   public int getSkyDarken() {
      return this.skyDarken;
   }

   public void setSkyFlashTime(int p_46709_) {
   }

   public WorldBorder getWorldBorder() {
      return this.worldBorder;
   }

   public void sendPacketToServer(Packet<?> p_46657_) {
      throw new UnsupportedOperationException("Can't send packets to server unless you're on the client.");
   }

   public DimensionType dimensionType() {
      return this.dimensionType;
   }

   public Holder<DimensionType> dimensionTypeRegistration() {
      return this.dimensionTypeRegistration;
   }

   public ResourceKey<Level> dimension() {
      return this.dimension;
   }

   public Random getRandom() {
      return this.random;
   }

   public boolean isStateAtPosition(BlockPos p_46620_, Predicate<BlockState> p_46621_) {
      return p_46621_.test(this.getBlockState(p_46620_));
   }

   public boolean isFluidAtPosition(BlockPos p_151541_, Predicate<FluidState> p_151542_) {
      return p_151542_.test(this.getFluidState(p_151541_));
   }

   public abstract RecipeManager getRecipeManager();

   public BlockPos getBlockRandomPos(int p_46497_, int p_46498_, int p_46499_, int p_46500_) {
      this.randValue = this.randValue * 3 + 1013904223;
      int i = this.randValue >> 2;
      return new BlockPos(p_46497_ + (i & 15), p_46498_ + (i >> 16 & p_46500_), p_46499_ + (i >> 8 & 15));
   }

   public boolean noSave() {
      return false;
   }

   public ProfilerFiller getProfiler() {
      return this.profiler.get();
   }

   public Supplier<ProfilerFiller> getProfilerSupplier() {
      return this.profiler;
   }

   public BiomeManager getBiomeManager() {
      return this.biomeManager;
   }

   private double maxEntityRadius = 2.0D;
   @Override
   public double getMaxEntityRadius() {
      return maxEntityRadius;
   }
   @Override
   public double increaseMaxEntityRadius(double value) {
      if (value > maxEntityRadius)
         maxEntityRadius = value;
      return maxEntityRadius;
   }

   public final boolean isDebug() {
      return this.isDebug;
   }

   protected abstract LevelEntityGetter<Entity> getEntities();

   protected void postGameEventInRadius(@Nullable Entity p_151514_, GameEvent p_151515_, BlockPos p_151516_, int p_151517_) {
      int i = SectionPos.blockToSectionCoord(p_151516_.getX() - p_151517_);
      int j = SectionPos.blockToSectionCoord(p_151516_.getZ() - p_151517_);
      int k = SectionPos.blockToSectionCoord(p_151516_.getX() + p_151517_);
      int l = SectionPos.blockToSectionCoord(p_151516_.getZ() + p_151517_);
      int i1 = SectionPos.blockToSectionCoord(p_151516_.getY() - p_151517_);
      int j1 = SectionPos.blockToSectionCoord(p_151516_.getY() + p_151517_);

      for(int k1 = i; k1 <= k; ++k1) {
         for(int l1 = j; l1 <= l; ++l1) {
            ChunkAccess chunkaccess = this.getChunkSource().getChunkNow(k1, l1);
            if (chunkaccess != null) {
               for(int i2 = i1; i2 <= j1; ++i2) {
                  chunkaccess.getEventDispatcher(i2).post(p_151515_, p_151514_, p_151516_);
               }
            }
         }
      }

   }

   public long nextSubTickCount() {
      return (long)(this.subTickCount++);
   }
}
