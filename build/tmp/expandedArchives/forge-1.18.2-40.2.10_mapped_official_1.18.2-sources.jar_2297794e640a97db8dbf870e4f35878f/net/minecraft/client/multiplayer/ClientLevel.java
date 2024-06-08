package net.minecraft.client.multiplayer;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintCache;
import net.minecraft.client.particle.FireworkParticles;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Cursor3D;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.entity.EntityTickList;
import net.minecraft.world.level.entity.LevelCallback;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.entity.TransientEntitySectionManager;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.ticks.BlackholeTickAccess;
import net.minecraft.world.ticks.LevelTickAccess;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientLevel extends Level {
   private static final double FLUID_PARTICLE_SPAWN_OFFSET = 0.05D;
   private static final int NORMAL_LIGHT_UPDATES_PER_FRAME = 10;
   private static final int LIGHT_UPDATE_QUEUE_SIZE_THRESHOLD = 1000;
   final EntityTickList tickingEntities = new EntityTickList();
   private final TransientEntitySectionManager<Entity> entityStorage = new TransientEntitySectionManager<>(Entity.class, new ClientLevel.EntityCallbacks());
   private final ClientPacketListener connection;
   private final LevelRenderer levelRenderer;
   private final ClientLevel.ClientLevelData clientLevelData;
   private final DimensionSpecialEffects effects;
   private final Minecraft minecraft = Minecraft.getInstance();
   final List<AbstractClientPlayer> players = Lists.newArrayList();
   private Scoreboard scoreboard = new Scoreboard();
   private final Map<String, MapItemSavedData> mapData = Maps.newHashMap();
   private static final long CLOUD_COLOR = 16777215L;
   private int skyFlashTime;
   private final Object2ObjectArrayMap<ColorResolver, BlockTintCache> tintCaches = Util.make(new Object2ObjectArrayMap<>(3), (p_194170_) -> {
      p_194170_.put(BiomeColors.GRASS_COLOR_RESOLVER, new BlockTintCache((p_194181_) -> {
         return this.calculateBlockTint(p_194181_, BiomeColors.GRASS_COLOR_RESOLVER);
      }));
      p_194170_.put(BiomeColors.FOLIAGE_COLOR_RESOLVER, new BlockTintCache((p_194177_) -> {
         return this.calculateBlockTint(p_194177_, BiomeColors.FOLIAGE_COLOR_RESOLVER);
      }));
      p_194170_.put(BiomeColors.WATER_COLOR_RESOLVER, new BlockTintCache((p_194168_) -> {
         return this.calculateBlockTint(p_194168_, BiomeColors.WATER_COLOR_RESOLVER);
      }));
   });
   public ClientChunkCache chunkSource;
   private final Deque<Runnable> lightUpdateQueue = Queues.newArrayDeque();
   private int serverSimulationDistance;
   private static final Set<Item> MARKER_PARTICLE_ITEMS = Set.of(Items.BARRIER, Items.LIGHT);
   private final it.unimi.dsi.fastutil.ints.Int2ObjectMap<net.minecraftforge.entity.PartEntity<?>> partEntities = new it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap<>();

   public ClientLevel(ClientPacketListener p_205505_, ClientLevel.ClientLevelData p_205506_, ResourceKey<Level> p_205507_, Holder<DimensionType> p_205508_, int p_205509_, int p_205510_, Supplier<ProfilerFiller> p_205511_, LevelRenderer p_205512_, boolean p_205513_, long p_205514_) {
      super(p_205506_, p_205507_, p_205508_, p_205511_, true, p_205513_, p_205514_);
      this.connection = p_205505_;
      this.chunkSource = new ClientChunkCache(this, p_205509_);
      this.clientLevelData = p_205506_;
      this.levelRenderer = p_205512_;
      this.effects = DimensionSpecialEffects.forType(p_205508_.value());
      this.setDefaultSpawnPos(new BlockPos(8, 64, 8), 0.0F);
      this.serverSimulationDistance = p_205510_;
      this.updateSkyBrightness();
      this.prepareWeather();
      this.gatherCapabilities();
      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.WorldEvent.Load(this));
   }

   public void queueLightUpdate(Runnable p_194172_) {
      this.lightUpdateQueue.add(p_194172_);
   }

   public void pollLightUpdates() {
      int i = this.lightUpdateQueue.size();
      int j = i < 1000 ? Math.max(10, i / 10) : i;

      for(int k = 0; k < j; ++k) {
         Runnable runnable = this.lightUpdateQueue.poll();
         if (runnable == null) {
            break;
         }

         runnable.run();
      }

   }

   public boolean isLightUpdateQueueEmpty() {
      return this.lightUpdateQueue.isEmpty();
   }

   public DimensionSpecialEffects effects() {
      return this.effects;
   }

   public void tick(BooleanSupplier p_104727_) {
      this.getWorldBorder().tick();
      this.tickTime();
      this.getProfiler().push("blocks");
      this.chunkSource.tick(p_104727_, true);
      this.getProfiler().pop();
   }

   private void tickTime() {
      this.setGameTime(this.levelData.getGameTime() + 1L);
      if (this.levelData.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) {
         this.setDayTime(this.levelData.getDayTime() + 1L);
      }

   }

   public void setGameTime(long p_104638_) {
      this.clientLevelData.setGameTime(p_104638_);
   }

   public void setDayTime(long p_104747_) {
      if (p_104747_ < 0L) {
         p_104747_ = -p_104747_;
         this.getGameRules().getRule(GameRules.RULE_DAYLIGHT).set(false, (MinecraftServer)null);
      } else {
         this.getGameRules().getRule(GameRules.RULE_DAYLIGHT).set(true, (MinecraftServer)null);
      }

      this.clientLevelData.setDayTime(p_104747_);
   }

   public Iterable<Entity> entitiesForRendering() {
      return this.getEntities().getAll();
   }

   public void tickEntities() {
      ProfilerFiller profilerfiller = this.getProfiler();
      profilerfiller.push("entities");
      this.tickingEntities.forEach((p_194183_) -> {
         if (!p_194183_.isRemoved() && !p_194183_.isPassenger()) {
            this.guardEntityTick(this::tickNonPassenger, p_194183_);
         }
      });
      profilerfiller.pop();
      this.tickBlockEntities();
   }

   public boolean shouldTickDeath(Entity p_194185_) {
      return p_194185_.chunkPosition().getChessboardDistance(this.minecraft.player.chunkPosition()) <= this.serverSimulationDistance;
   }

   public void tickNonPassenger(Entity p_104640_) {
      p_104640_.setOldPosAndRot();
      ++p_104640_.tickCount;
      this.getProfiler().push(() -> {
         return Registry.ENTITY_TYPE.getKey(p_104640_.getType()).toString();
      });
      if (p_104640_.canUpdate())
      p_104640_.tick();
      this.getProfiler().pop();

      for(Entity entity : p_104640_.getPassengers()) {
         this.tickPassenger(p_104640_, entity);
      }

   }

   private void tickPassenger(Entity p_104642_, Entity p_104643_) {
      if (!p_104643_.isRemoved() && p_104643_.getVehicle() == p_104642_) {
         if (p_104643_ instanceof Player || this.tickingEntities.contains(p_104643_)) {
            p_104643_.setOldPosAndRot();
            ++p_104643_.tickCount;
            p_104643_.rideTick();

            for(Entity entity : p_104643_.getPassengers()) {
               this.tickPassenger(p_104643_, entity);
            }

         }
      } else {
         p_104643_.stopRiding();
      }
   }

   public void unload(LevelChunk p_104666_) {
      p_104666_.clearAllBlockEntities();
      this.chunkSource.getLightEngine().enableLightSources(p_104666_.getPos(), false);
      this.entityStorage.stopTicking(p_104666_.getPos());
   }

   public void onChunkLoaded(ChunkPos p_171650_) {
      this.tintCaches.forEach((p_194154_, p_194155_) -> {
         p_194155_.invalidateForChunk(p_171650_.x, p_171650_.z);
      });
      this.entityStorage.startTicking(p_171650_);
   }

   public void clearTintCaches() {
      this.tintCaches.forEach((p_194157_, p_194158_) -> {
         p_194158_.invalidateAll();
      });
   }

   public boolean hasChunk(int p_104737_, int p_104738_) {
      return true;
   }

   public int getEntityCount() {
      return this.entityStorage.count();
   }

   public void addPlayer(int p_104631_, AbstractClientPlayer p_104632_) {
      this.addEntity(p_104631_, p_104632_);
   }

   public void putNonPlayerEntity(int p_104628_, Entity p_104629_) {
      this.addEntity(p_104628_, p_104629_);
   }

   private void addEntity(int p_104740_, Entity p_104741_) {
      if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.EntityJoinWorldEvent(p_104741_, this))) return;
      this.removeEntity(p_104740_, Entity.RemovalReason.DISCARDED);
      this.entityStorage.addEntity(p_104741_);
      p_104741_.onAddedToWorld();
   }

   public void removeEntity(int p_171643_, Entity.RemovalReason p_171644_) {
      Entity entity = this.getEntities().get(p_171643_);
      if (entity != null) {
         entity.setRemoved(p_171644_);
         entity.onClientRemoval();
      }

   }

   @Nullable
   public Entity getEntity(int p_104609_) {
      return this.getEntities().get(p_104609_);
   }

   public void setKnownState(BlockPos p_104756_, BlockState p_104757_) {
      this.setBlock(p_104756_, p_104757_, 19);
   }

   public void disconnect() {
      this.connection.getConnection().disconnect(new TranslatableComponent("multiplayer.status.quitting"));
   }

   public void animateTick(int p_104785_, int p_104786_, int p_104787_) {
      int i = 32;
      Random random = new Random();
      Block block = this.getMarkerParticleTarget();
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(int j = 0; j < 667; ++j) {
         this.doAnimateTick(p_104785_, p_104786_, p_104787_, 16, random, block, blockpos$mutableblockpos);
         this.doAnimateTick(p_104785_, p_104786_, p_104787_, 32, random, block, blockpos$mutableblockpos);
      }

   }

   @Nullable
   private Block getMarkerParticleTarget() {
      if (this.minecraft.gameMode.getPlayerMode() == GameType.CREATIVE) {
         ItemStack itemstack = this.minecraft.player.getMainHandItem();
         Item item = itemstack.getItem();
         if (MARKER_PARTICLE_ITEMS.contains(item) && item instanceof BlockItem) {
            BlockItem blockitem = (BlockItem)item;
            return blockitem.getBlock();
         }
      }

      return null;
   }

   public void doAnimateTick(int p_194143_, int p_194144_, int p_194145_, int p_194146_, Random p_194147_, @Nullable Block p_194148_, BlockPos.MutableBlockPos p_194149_) {
      int i = p_194143_ + this.random.nextInt(p_194146_) - this.random.nextInt(p_194146_);
      int j = p_194144_ + this.random.nextInt(p_194146_) - this.random.nextInt(p_194146_);
      int k = p_194145_ + this.random.nextInt(p_194146_) - this.random.nextInt(p_194146_);
      p_194149_.set(i, j, k);
      BlockState blockstate = this.getBlockState(p_194149_);
      blockstate.getBlock().animateTick(blockstate, this, p_194149_, p_194147_);
      FluidState fluidstate = this.getFluidState(p_194149_);
      if (!fluidstate.isEmpty()) {
         fluidstate.animateTick(this, p_194149_, p_194147_);
         ParticleOptions particleoptions = fluidstate.getDripParticle();
         if (particleoptions != null && this.random.nextInt(10) == 0) {
            boolean flag = blockstate.isFaceSturdy(this, p_194149_, Direction.DOWN);
            BlockPos blockpos = p_194149_.below();
            this.trySpawnDripParticles(blockpos, this.getBlockState(blockpos), particleoptions, flag);
         }
      }

      if (p_194148_ == blockstate.getBlock()) {
         this.addParticle(new BlockParticleOption(ParticleTypes.BLOCK_MARKER, blockstate), (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, 0.0D, 0.0D, 0.0D);
      }

      if (!blockstate.isCollisionShapeFullBlock(this, p_194149_)) {
         this.getBiome(p_194149_).value().getAmbientParticle().ifPresent((p_194166_) -> {
            if (p_194166_.canSpawn(this.random)) {
               this.addParticle(p_194166_.getOptions(), (double)p_194149_.getX() + this.random.nextDouble(), (double)p_194149_.getY() + this.random.nextDouble(), (double)p_194149_.getZ() + this.random.nextDouble(), 0.0D, 0.0D, 0.0D);
            }

         });
      }

   }

   private void trySpawnDripParticles(BlockPos p_104690_, BlockState p_104691_, ParticleOptions p_104692_, boolean p_104693_) {
      if (p_104691_.getFluidState().isEmpty()) {
         VoxelShape voxelshape = p_104691_.getCollisionShape(this, p_104690_);
         double d0 = voxelshape.max(Direction.Axis.Y);
         if (d0 < 1.0D) {
            if (p_104693_) {
               this.spawnFluidParticle((double)p_104690_.getX(), (double)(p_104690_.getX() + 1), (double)p_104690_.getZ(), (double)(p_104690_.getZ() + 1), (double)(p_104690_.getY() + 1) - 0.05D, p_104692_);
            }
         } else if (!p_104691_.is(BlockTags.IMPERMEABLE)) {
            double d1 = voxelshape.min(Direction.Axis.Y);
            if (d1 > 0.0D) {
               this.spawnParticle(p_104690_, p_104692_, voxelshape, (double)p_104690_.getY() + d1 - 0.05D);
            } else {
               BlockPos blockpos = p_104690_.below();
               BlockState blockstate = this.getBlockState(blockpos);
               VoxelShape voxelshape1 = blockstate.getCollisionShape(this, blockpos);
               double d2 = voxelshape1.max(Direction.Axis.Y);
               if (d2 < 1.0D && blockstate.getFluidState().isEmpty()) {
                  this.spawnParticle(p_104690_, p_104692_, voxelshape, (double)p_104690_.getY() - 0.05D);
               }
            }
         }

      }
   }

   private void spawnParticle(BlockPos p_104695_, ParticleOptions p_104696_, VoxelShape p_104697_, double p_104698_) {
      this.spawnFluidParticle((double)p_104695_.getX() + p_104697_.min(Direction.Axis.X), (double)p_104695_.getX() + p_104697_.max(Direction.Axis.X), (double)p_104695_.getZ() + p_104697_.min(Direction.Axis.Z), (double)p_104695_.getZ() + p_104697_.max(Direction.Axis.Z), p_104698_, p_104696_);
   }

   private void spawnFluidParticle(double p_104593_, double p_104594_, double p_104595_, double p_104596_, double p_104597_, ParticleOptions p_104598_) {
      this.addParticle(p_104598_, Mth.lerp(this.random.nextDouble(), p_104593_, p_104594_), p_104597_, Mth.lerp(this.random.nextDouble(), p_104595_, p_104596_), 0.0D, 0.0D, 0.0D);
   }

   public CrashReportCategory fillReportDetails(CrashReport p_104729_) {
      CrashReportCategory crashreportcategory = super.fillReportDetails(p_104729_);
      crashreportcategory.setDetail("Server brand", () -> {
         return this.minecraft.player.getServerBrand();
      });
      crashreportcategory.setDetail("Server type", () -> {
         return this.minecraft.getSingleplayerServer() == null ? "Non-integrated multiplayer server" : "Integrated singleplayer server";
      });
      return crashreportcategory;
   }

   public void playSound(@Nullable Player p_104645_, double p_104646_, double p_104647_, double p_104648_, SoundEvent p_104649_, SoundSource p_104650_, float p_104651_, float p_104652_) {
      net.minecraftforge.event.entity.PlaySoundAtEntityEvent event = net.minecraftforge.event.ForgeEventFactory.onPlaySoundAtEntity(p_104645_, p_104649_, p_104650_, p_104651_, p_104652_);
      if (event.isCanceled() || event.getSound() == null) return;
      p_104649_ = event.getSound();
      p_104650_ = event.getCategory();
      p_104651_ = event.getVolume();
      if (p_104645_ == this.minecraft.player) {
         this.playLocalSound(p_104646_, p_104647_, p_104648_, p_104649_, p_104650_, p_104651_, p_104652_, false);
      }

   }

   public void playSound(@Nullable Player p_104659_, Entity p_104660_, SoundEvent p_104661_, SoundSource p_104662_, float p_104663_, float p_104664_) {
      net.minecraftforge.event.entity.PlaySoundAtEntityEvent event = net.minecraftforge.event.ForgeEventFactory.onPlaySoundAtEntity(p_104659_, p_104661_, p_104662_, p_104663_, p_104664_);
      if (event.isCanceled() || event.getSound() == null) return;
      p_104661_ = event.getSound();
      p_104662_ = event.getCategory();
      p_104663_ = event.getVolume();
      if (p_104659_ == this.minecraft.player) {
         this.minecraft.getSoundManager().play(new EntityBoundSoundInstance(p_104661_, p_104662_, p_104663_, p_104664_, p_104660_));
      }

   }

   public void playLocalSound(BlockPos p_104678_, SoundEvent p_104679_, SoundSource p_104680_, float p_104681_, float p_104682_, boolean p_104683_) {
      this.playLocalSound((double)p_104678_.getX() + 0.5D, (double)p_104678_.getY() + 0.5D, (double)p_104678_.getZ() + 0.5D, p_104679_, p_104680_, p_104681_, p_104682_, p_104683_);
   }

   public void playLocalSound(double p_104600_, double p_104601_, double p_104602_, SoundEvent p_104603_, SoundSource p_104604_, float p_104605_, float p_104606_, boolean p_104607_) {
      double d0 = this.minecraft.gameRenderer.getMainCamera().getPosition().distanceToSqr(p_104600_, p_104601_, p_104602_);
      SimpleSoundInstance simplesoundinstance = new SimpleSoundInstance(p_104603_, p_104604_, p_104605_, p_104606_, p_104600_, p_104601_, p_104602_);
      if (p_104607_ && d0 > 100.0D) {
         double d1 = Math.sqrt(d0) / 40.0D;
         this.minecraft.getSoundManager().playDelayed(simplesoundinstance, (int)(d1 * 20.0D));
      } else {
         this.minecraft.getSoundManager().play(simplesoundinstance);
      }

   }

   public void createFireworks(double p_104585_, double p_104586_, double p_104587_, double p_104588_, double p_104589_, double p_104590_, @Nullable CompoundTag p_104591_) {
      this.minecraft.particleEngine.add(new FireworkParticles.Starter(this, p_104585_, p_104586_, p_104587_, p_104588_, p_104589_, p_104590_, this.minecraft.particleEngine, p_104591_));
   }

   public void sendPacketToServer(Packet<?> p_104734_) {
      this.connection.send(p_104734_);
   }

   public RecipeManager getRecipeManager() {
      return this.connection.getRecipeManager();
   }

   public void setScoreboard(Scoreboard p_104670_) {
      this.scoreboard = p_104670_;
   }

   public LevelTickAccess<Block> getBlockTicks() {
      return BlackholeTickAccess.emptyLevelList();
   }

   public LevelTickAccess<Fluid> getFluidTicks() {
      return BlackholeTickAccess.emptyLevelList();
   }

   public ClientChunkCache getChunkSource() {
      return this.chunkSource;
   }

   @Nullable
   public MapItemSavedData getMapData(String p_104725_) {
      return this.mapData.get(p_104725_);
   }

   public void setMapData(String p_171670_, MapItemSavedData p_171671_) {
      this.mapData.put(p_171670_, p_171671_);
   }

   public int getFreeMapId() {
      return 0;
   }

   public Scoreboard getScoreboard() {
      return this.scoreboard;
   }

   public RegistryAccess registryAccess() {
      return this.connection.registryAccess();
   }

   public void sendBlockUpdated(BlockPos p_104685_, BlockState p_104686_, BlockState p_104687_, int p_104688_) {
      this.levelRenderer.blockChanged(this, p_104685_, p_104686_, p_104687_, p_104688_);
   }

   public void setBlocksDirty(BlockPos p_104759_, BlockState p_104760_, BlockState p_104761_) {
      this.levelRenderer.setBlockDirty(p_104759_, p_104760_, p_104761_);
   }

   public void setSectionDirtyWithNeighbors(int p_104794_, int p_104795_, int p_104796_) {
      this.levelRenderer.setSectionDirtyWithNeighbors(p_104794_, p_104795_, p_104796_);
   }

   public void setLightReady(int p_197406_, int p_197407_) {
      LevelChunk levelchunk = this.chunkSource.getChunk(p_197406_, p_197407_, false);
      if (levelchunk != null) {
         levelchunk.setClientLightReady(true);
      }

   }

   public void destroyBlockProgress(int p_104634_, BlockPos p_104635_, int p_104636_) {
      this.levelRenderer.destroyBlockProgress(p_104634_, p_104635_, p_104636_);
   }

   public void globalLevelEvent(int p_104743_, BlockPos p_104744_, int p_104745_) {
      this.levelRenderer.globalLevelEvent(p_104743_, p_104744_, p_104745_);
   }

   public void levelEvent(@Nullable Player p_104654_, int p_104655_, BlockPos p_104656_, int p_104657_) {
      try {
         this.levelRenderer.levelEvent(p_104654_, p_104655_, p_104656_, p_104657_);
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.forThrowable(throwable, "Playing level event");
         CrashReportCategory crashreportcategory = crashreport.addCategory("Level event being played");
         crashreportcategory.setDetail("Block coordinates", CrashReportCategory.formatLocation(this, p_104656_));
         crashreportcategory.setDetail("Event source", p_104654_);
         crashreportcategory.setDetail("Event type", p_104655_);
         crashreportcategory.setDetail("Event data", p_104657_);
         throw new ReportedException(crashreport);
      }
   }

   public void addParticle(ParticleOptions p_104706_, double p_104707_, double p_104708_, double p_104709_, double p_104710_, double p_104711_, double p_104712_) {
      this.levelRenderer.addParticle(p_104706_, p_104706_.getType().getOverrideLimiter(), p_104707_, p_104708_, p_104709_, p_104710_, p_104711_, p_104712_);
   }

   public void addParticle(ParticleOptions p_104714_, boolean p_104715_, double p_104716_, double p_104717_, double p_104718_, double p_104719_, double p_104720_, double p_104721_) {
      this.levelRenderer.addParticle(p_104714_, p_104714_.getType().getOverrideLimiter() || p_104715_, p_104716_, p_104717_, p_104718_, p_104719_, p_104720_, p_104721_);
   }

   public void addAlwaysVisibleParticle(ParticleOptions p_104766_, double p_104767_, double p_104768_, double p_104769_, double p_104770_, double p_104771_, double p_104772_) {
      this.levelRenderer.addParticle(p_104766_, false, true, p_104767_, p_104768_, p_104769_, p_104770_, p_104771_, p_104772_);
   }

   public void addAlwaysVisibleParticle(ParticleOptions p_104774_, boolean p_104775_, double p_104776_, double p_104777_, double p_104778_, double p_104779_, double p_104780_, double p_104781_) {
      this.levelRenderer.addParticle(p_104774_, p_104774_.getType().getOverrideLimiter() || p_104775_, true, p_104776_, p_104777_, p_104778_, p_104779_, p_104780_, p_104781_);
   }

   public List<AbstractClientPlayer> players() {
      return this.players;
   }

   public Holder<Biome> getUncachedNoiseBiome(int p_205516_, int p_205517_, int p_205518_) {
      return this.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getHolderOrThrow(Biomes.PLAINS);
   }

   public float getSkyDarken(float p_104806_) {
      float f = this.getTimeOfDay(p_104806_);
      float f1 = 1.0F - (Mth.cos(f * ((float)Math.PI * 2F)) * 2.0F + 0.2F);
      f1 = Mth.clamp(f1, 0.0F, 1.0F);
      f1 = 1.0F - f1;
      f1 *= 1.0F - this.getRainLevel(p_104806_) * 5.0F / 16.0F;
      f1 *= 1.0F - this.getThunderLevel(p_104806_) * 5.0F / 16.0F;
      return f1 * 0.8F + 0.2F;
   }

   public Vec3 getSkyColor(Vec3 p_171661_, float p_171662_) {
      float f = this.getTimeOfDay(p_171662_);
      Vec3 vec3 = p_171661_.subtract(2.0D, 2.0D, 2.0D).scale(0.25D);
      BiomeManager biomemanager = this.getBiomeManager();
      Vec3 vec31 = CubicSampler.gaussianSampleVec3(vec3, (p_194161_, p_194162_, p_194163_) -> {
         return Vec3.fromRGB24(biomemanager.getNoiseBiomeAtQuart(p_194161_, p_194162_, p_194163_).value().getSkyColor());
      });
      float f1 = Mth.cos(f * ((float)Math.PI * 2F)) * 2.0F + 0.5F;
      f1 = Mth.clamp(f1, 0.0F, 1.0F);
      float f2 = (float)vec31.x * f1;
      float f3 = (float)vec31.y * f1;
      float f4 = (float)vec31.z * f1;
      float f5 = this.getRainLevel(p_171662_);
      if (f5 > 0.0F) {
         float f6 = (f2 * 0.3F + f3 * 0.59F + f4 * 0.11F) * 0.6F;
         float f7 = 1.0F - f5 * 0.75F;
         f2 = f2 * f7 + f6 * (1.0F - f7);
         f3 = f3 * f7 + f6 * (1.0F - f7);
         f4 = f4 * f7 + f6 * (1.0F - f7);
      }

      float f9 = this.getThunderLevel(p_171662_);
      if (f9 > 0.0F) {
         float f10 = (f2 * 0.3F + f3 * 0.59F + f4 * 0.11F) * 0.2F;
         float f8 = 1.0F - f9 * 0.75F;
         f2 = f2 * f8 + f10 * (1.0F - f8);
         f3 = f3 * f8 + f10 * (1.0F - f8);
         f4 = f4 * f8 + f10 * (1.0F - f8);
      }

      if (!this.minecraft.options.hideLightningFlashes && this.skyFlashTime > 0) {
         float f11 = (float)this.skyFlashTime - p_171662_;
         if (f11 > 1.0F) {
            f11 = 1.0F;
         }

         f11 *= 0.45F;
         f2 = f2 * (1.0F - f11) + 0.8F * f11;
         f3 = f3 * (1.0F - f11) + 0.8F * f11;
         f4 = f4 * (1.0F - f11) + 1.0F * f11;
      }

      return new Vec3((double)f2, (double)f3, (double)f4);
   }

   public Vec3 getCloudColor(float p_104809_) {
      float f = this.getTimeOfDay(p_104809_);
      float f1 = Mth.cos(f * ((float)Math.PI * 2F)) * 2.0F + 0.5F;
      f1 = Mth.clamp(f1, 0.0F, 1.0F);
      float f2 = 1.0F;
      float f3 = 1.0F;
      float f4 = 1.0F;
      float f5 = this.getRainLevel(p_104809_);
      if (f5 > 0.0F) {
         float f6 = (f2 * 0.3F + f3 * 0.59F + f4 * 0.11F) * 0.6F;
         float f7 = 1.0F - f5 * 0.95F;
         f2 = f2 * f7 + f6 * (1.0F - f7);
         f3 = f3 * f7 + f6 * (1.0F - f7);
         f4 = f4 * f7 + f6 * (1.0F - f7);
      }

      f2 *= f1 * 0.9F + 0.1F;
      f3 *= f1 * 0.9F + 0.1F;
      f4 *= f1 * 0.85F + 0.15F;
      float f9 = this.getThunderLevel(p_104809_);
      if (f9 > 0.0F) {
         float f10 = (f2 * 0.3F + f3 * 0.59F + f4 * 0.11F) * 0.2F;
         float f8 = 1.0F - f9 * 0.95F;
         f2 = f2 * f8 + f10 * (1.0F - f8);
         f3 = f3 * f8 + f10 * (1.0F - f8);
         f4 = f4 * f8 + f10 * (1.0F - f8);
      }

      return new Vec3((double)f2, (double)f3, (double)f4);
   }

   public float getStarBrightness(float p_104812_) {
      float f = this.getTimeOfDay(p_104812_);
      float f1 = 1.0F - (Mth.cos(f * ((float)Math.PI * 2F)) * 2.0F + 0.25F);
      f1 = Mth.clamp(f1, 0.0F, 1.0F);
      return f1 * f1 * 0.5F;
   }

   public int getSkyFlashTime() {
      return this.skyFlashTime;
   }

   public void setSkyFlashTime(int p_104783_) {
      this.skyFlashTime = p_104783_;
   }

   public float getShade(Direction p_104703_, boolean p_104704_) {
      boolean flag = this.effects().constantAmbientLight();
      if (!p_104704_) {
         return flag ? 0.9F : 1.0F;
      } else {
         switch(p_104703_) {
         case DOWN:
            return flag ? 0.9F : 0.5F;
         case UP:
            return flag ? 0.9F : 1.0F;
         case NORTH:
         case SOUTH:
            return 0.8F;
         case WEST:
         case EAST:
            return 0.6F;
         default:
            return 1.0F;
         }
      }
   }

   public int getBlockTint(BlockPos p_104700_, ColorResolver p_104701_) {
      BlockTintCache blocktintcache = this.tintCaches.get(p_104701_);
      return blocktintcache.getColor(p_104700_);
   }

   public int calculateBlockTint(BlockPos p_104763_, ColorResolver p_104764_) {
      int i = Minecraft.getInstance().options.biomeBlendRadius;
      if (i == 0) {
         return p_104764_.getColor(this.getBiome(p_104763_).value(), (double)p_104763_.getX(), (double)p_104763_.getZ());
      } else {
         int j = (i * 2 + 1) * (i * 2 + 1);
         int k = 0;
         int l = 0;
         int i1 = 0;
         Cursor3D cursor3d = new Cursor3D(p_104763_.getX() - i, p_104763_.getY(), p_104763_.getZ() - i, p_104763_.getX() + i, p_104763_.getY(), p_104763_.getZ() + i);

         int j1;
         for(BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(); cursor3d.advance(); i1 += j1 & 255) {
            blockpos$mutableblockpos.set(cursor3d.nextX(), cursor3d.nextY(), cursor3d.nextZ());
            j1 = p_104764_.getColor(this.getBiome(blockpos$mutableblockpos).value(), (double)blockpos$mutableblockpos.getX(), (double)blockpos$mutableblockpos.getZ());
            k += (j1 & 16711680) >> 16;
            l += (j1 & '\uff00') >> 8;
         }

         return (k / j & 255) << 16 | (l / j & 255) << 8 | i1 / j & 255;
      }
   }

   public BlockPos getSharedSpawnPos() {
      BlockPos blockpos = new BlockPos(this.levelData.getXSpawn(), this.levelData.getYSpawn(), this.levelData.getZSpawn());
      if (!this.getWorldBorder().isWithinBounds(blockpos)) {
         blockpos = this.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, new BlockPos(this.getWorldBorder().getCenterX(), 0.0D, this.getWorldBorder().getCenterZ()));
      }

      return blockpos;
   }

   public float getSharedSpawnAngle() {
      return this.levelData.getSpawnAngle();
   }

   public void setDefaultSpawnPos(BlockPos p_104753_, float p_104754_) {
      this.levelData.setSpawn(p_104753_, p_104754_);
   }

   public String toString() {
      return "ClientLevel";
   }

   public ClientLevel.ClientLevelData getLevelData() {
      return this.clientLevelData;
   }

   public void gameEvent(@Nullable Entity p_171646_, GameEvent p_171647_, BlockPos p_171648_) {
   }

   public Map<String, MapItemSavedData> getAllMapData() {
      return ImmutableMap.copyOf(this.mapData);
   }

   public void addMapData(Map<String, MapItemSavedData> p_171673_) {
      this.mapData.putAll(p_171673_);
   }

   protected LevelEntityGetter<Entity> getEntities() {
      return this.entityStorage.getEntityGetter();
   }

   public String gatherChunkSourceStats() {
      return "Chunks[C] W: " + this.chunkSource.gatherStats() + " E: " + this.entityStorage.gatherStats();
   }

   public void addDestroyBlockEffect(BlockPos p_171667_, BlockState p_171668_) {
      this.minecraft.particleEngine.destroy(p_171667_, p_171668_);
   }

   public void setServerSimulationDistance(int p_194175_) {
      this.serverSimulationDistance = p_194175_;
   }

   public int getServerSimulationDistance() {
      return this.serverSimulationDistance;
   }

   @OnlyIn(Dist.CLIENT)
   public static class ClientLevelData implements WritableLevelData {
      private final boolean hardcore;
      private final GameRules gameRules;
      private final boolean isFlat;
      private int xSpawn;
      private int ySpawn;
      private int zSpawn;
      private float spawnAngle;
      private long gameTime;
      private long dayTime;
      private boolean raining;
      private Difficulty difficulty;
      private boolean difficultyLocked;

      public ClientLevelData(Difficulty p_104843_, boolean p_104844_, boolean p_104845_) {
         this.difficulty = p_104843_;
         this.hardcore = p_104844_;
         this.isFlat = p_104845_;
         this.gameRules = new GameRules();
      }

      public int getXSpawn() {
         return this.xSpawn;
      }

      public int getYSpawn() {
         return this.ySpawn;
      }

      public int getZSpawn() {
         return this.zSpawn;
      }

      public float getSpawnAngle() {
         return this.spawnAngle;
      }

      public long getGameTime() {
         return this.gameTime;
      }

      public long getDayTime() {
         return this.dayTime;
      }

      public void setXSpawn(int p_104862_) {
         this.xSpawn = p_104862_;
      }

      public void setYSpawn(int p_104869_) {
         this.ySpawn = p_104869_;
      }

      public void setZSpawn(int p_104872_) {
         this.zSpawn = p_104872_;
      }

      public void setSpawnAngle(float p_104848_) {
         this.spawnAngle = p_104848_;
      }

      public void setGameTime(long p_104850_) {
         this.gameTime = p_104850_;
      }

      public void setDayTime(long p_104864_) {
         this.dayTime = p_104864_;
      }

      public void setSpawn(BlockPos p_104854_, float p_104855_) {
         this.xSpawn = p_104854_.getX();
         this.ySpawn = p_104854_.getY();
         this.zSpawn = p_104854_.getZ();
         this.spawnAngle = p_104855_;
      }

      public boolean isThundering() {
         return false;
      }

      public boolean isRaining() {
         return this.raining;
      }

      public void setRaining(boolean p_104866_) {
         this.raining = p_104866_;
      }

      public boolean isHardcore() {
         return this.hardcore;
      }

      public GameRules getGameRules() {
         return this.gameRules;
      }

      public Difficulty getDifficulty() {
         return this.difficulty;
      }

      public boolean isDifficultyLocked() {
         return this.difficultyLocked;
      }

      public void fillCrashReportCategory(CrashReportCategory p_171690_, LevelHeightAccessor p_171691_) {
         WritableLevelData.super.fillCrashReportCategory(p_171690_, p_171691_);
      }

      public void setDifficulty(Difficulty p_104852_) {
         net.minecraftforge.common.ForgeHooks.onDifficultyChange(p_104852_, this.difficulty);
         this.difficulty = p_104852_;
      }

      public void setDifficultyLocked(boolean p_104859_) {
         this.difficultyLocked = p_104859_;
      }

      public double getHorizonHeight(LevelHeightAccessor p_171688_) {
         return this.isFlat ? (double)p_171688_.getMinBuildHeight() : 63.0D;
      }

      public float getClearColorScale() {
         return this.isFlat ? 1.0F : 0.03125F;
      }
   }

   @OnlyIn(Dist.CLIENT)
   final class EntityCallbacks implements LevelCallback<Entity> {
      public void onCreated(Entity p_171696_) {
      }

      public void onDestroyed(Entity p_171700_) {
      }

      public void onTickingStart(Entity p_171704_) {
         ClientLevel.this.tickingEntities.add(p_171704_);
      }

      public void onTickingEnd(Entity p_171708_) {
         ClientLevel.this.tickingEntities.remove(p_171708_);
      }

      public void onTrackingStart(Entity p_171712_) {
         if (p_171712_ instanceof AbstractClientPlayer) {
            ClientLevel.this.players.add((AbstractClientPlayer)p_171712_);
         }

         if (p_171712_.isMultipartEntity()) {
            for (net.minecraftforge.entity.PartEntity<?> part : p_171712_.getParts()) {
               ClientLevel.this.partEntities.put(part.getId(), part);
            }
         }
      }

      public void onTrackingEnd(Entity p_171716_) {
         p_171716_.unRide();
         ClientLevel.this.players.remove(p_171716_);

          p_171716_.onRemovedFromWorld();
          net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.EntityLeaveWorldEvent(p_171716_, ClientLevel.this));

         if (p_171716_.isMultipartEntity()) {
            for (net.minecraftforge.entity.PartEntity<?> part : p_171716_.getParts()) {
               ClientLevel.this.partEntities.remove(part.getId());
            }
         }
      }
   }

   @Override
   public java.util.Collection<net.minecraftforge.entity.PartEntity<?>> getPartEntities() {
      return this.partEntities.values();
   }
}
