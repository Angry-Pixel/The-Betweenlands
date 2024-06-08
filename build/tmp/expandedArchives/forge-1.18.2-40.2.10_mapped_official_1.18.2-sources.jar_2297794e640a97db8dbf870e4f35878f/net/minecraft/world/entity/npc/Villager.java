package net.minecraft.world.entity.npc;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ReputationEventHandler;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.VillagerGoalPackages;
import net.minecraft.world.entity.ai.gossip.GossipContainer;
import net.minecraft.world.entity.ai.gossip.GossipType;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.sensing.GolemSensor;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.village.ReputationEventType;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.slf4j.Logger;

public class Villager extends AbstractVillager implements ReputationEventHandler, VillagerDataHolder {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final EntityDataAccessor<VillagerData> DATA_VILLAGER_DATA = SynchedEntityData.defineId(Villager.class, EntityDataSerializers.VILLAGER_DATA);
   public static final int BREEDING_FOOD_THRESHOLD = 12;
   public static final Map<Item, Integer> FOOD_POINTS = ImmutableMap.of(Items.BREAD, 4, Items.POTATO, 1, Items.CARROT, 1, Items.BEETROOT, 1);
   private static final int TRADES_PER_LEVEL = 2;
   private static final Set<Item> WANTED_ITEMS = ImmutableSet.of(Items.BREAD, Items.POTATO, Items.CARROT, Items.WHEAT, Items.WHEAT_SEEDS, Items.BEETROOT, Items.BEETROOT_SEEDS);
   private static final int MAX_GOSSIP_TOPICS = 10;
   private static final int GOSSIP_COOLDOWN = 1200;
   private static final int GOSSIP_DECAY_INTERVAL = 24000;
   private static final int REPUTATION_CHANGE_PER_EVENT = 25;
   private static final int HOW_FAR_AWAY_TO_TALK_TO_OTHER_VILLAGERS_ABOUT_GOLEMS = 10;
   private static final int HOW_MANY_VILLAGERS_NEED_TO_AGREE_TO_SPAWN_A_GOLEM = 5;
   private static final long TIME_SINCE_SLEEPING_FOR_GOLEM_SPAWNING = 24000L;
   @VisibleForTesting
   public static final float SPEED_MODIFIER = 0.5F;
   private int updateMerchantTimer;
   private boolean increaseProfessionLevelOnUpdate;
   @Nullable
   private Player lastTradedPlayer;
   private boolean chasing;
   private int foodLevel;
   private final GossipContainer gossips = new GossipContainer();
   private long lastGossipTime;
   private long lastGossipDecayTime;
   private int villagerXp;
   private long lastRestockGameTime;
   private int numberOfRestocksToday;
   private long lastRestockCheckDayTime;
   private boolean assignProfessionWhenSpawned;
   private static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(MemoryModuleType.HOME, MemoryModuleType.JOB_SITE, MemoryModuleType.POTENTIAL_JOB_SITE, MemoryModuleType.MEETING_POINT, MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.VISIBLE_VILLAGER_BABIES, MemoryModuleType.NEAREST_PLAYERS, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryModuleType.WALK_TARGET, MemoryModuleType.LOOK_TARGET, MemoryModuleType.INTERACTION_TARGET, MemoryModuleType.BREED_TARGET, MemoryModuleType.PATH, MemoryModuleType.DOORS_TO_CLOSE, MemoryModuleType.NEAREST_BED, MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.NEAREST_HOSTILE, MemoryModuleType.SECONDARY_JOB_SITE, MemoryModuleType.HIDING_PLACE, MemoryModuleType.HEARD_BELL_TIME, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.LAST_SLEPT, MemoryModuleType.LAST_WOKEN, MemoryModuleType.LAST_WORKED_AT_POI, MemoryModuleType.GOLEM_DETECTED_RECENTLY);
   private static final ImmutableList<SensorType<? extends Sensor<? super Villager>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.NEAREST_ITEMS, SensorType.NEAREST_BED, SensorType.HURT_BY, SensorType.VILLAGER_HOSTILES, SensorType.VILLAGER_BABIES, SensorType.SECONDARY_POIS, SensorType.GOLEM_DETECTED);
   public static final Map<MemoryModuleType<GlobalPos>, BiPredicate<Villager, PoiType>> POI_MEMORIES = ImmutableMap.of(MemoryModuleType.HOME, (p_35493_, p_35494_) -> {
      return p_35494_ == PoiType.HOME;
   }, MemoryModuleType.JOB_SITE, (p_35486_, p_35487_) -> {
      return p_35486_.getVillagerData().getProfession().getJobPoiType() == p_35487_;
   }, MemoryModuleType.POTENTIAL_JOB_SITE, (p_35469_, p_35470_) -> {
      return PoiType.ALL_JOBS.test(p_35470_);
   }, MemoryModuleType.MEETING_POINT, (p_35434_, p_35435_) -> {
      return p_35435_ == PoiType.MEETING;
   });

   public Villager(EntityType<? extends Villager> p_35381_, Level p_35382_) {
      this(p_35381_, p_35382_, VillagerType.PLAINS);
   }

   public Villager(EntityType<? extends Villager> p_35384_, Level p_35385_, VillagerType p_35386_) {
      super(p_35384_, p_35385_);
      ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(true);
      this.getNavigation().setCanFloat(true);
      this.setCanPickUpLoot(true);
      this.setVillagerData(this.getVillagerData().setType(p_35386_).setProfession(VillagerProfession.NONE));
   }

   public Brain<Villager> getBrain() {
      return (Brain<Villager>)super.getBrain();
   }

   protected Brain.Provider<Villager> brainProvider() {
      return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
   }

   protected Brain<?> makeBrain(Dynamic<?> p_35445_) {
      Brain<Villager> brain = this.brainProvider().makeBrain(p_35445_);
      this.registerBrainGoals(brain);
      return brain;
   }

   public void refreshBrain(ServerLevel p_35484_) {
      Brain<Villager> brain = this.getBrain();
      brain.stopAll(p_35484_, this);
      this.brain = brain.copyWithoutBehaviors();
      this.registerBrainGoals(this.getBrain());
   }

   private void registerBrainGoals(Brain<Villager> p_35425_) {
      VillagerProfession villagerprofession = this.getVillagerData().getProfession();
      if (this.isBaby()) {
         p_35425_.setSchedule(Schedule.VILLAGER_BABY);
         p_35425_.addActivity(Activity.PLAY, VillagerGoalPackages.getPlayPackage(0.5F));
      } else {
         p_35425_.setSchedule(Schedule.VILLAGER_DEFAULT);
         p_35425_.addActivityWithConditions(Activity.WORK, VillagerGoalPackages.getWorkPackage(villagerprofession, 0.5F), ImmutableSet.of(Pair.of(MemoryModuleType.JOB_SITE, MemoryStatus.VALUE_PRESENT)));
      }

      p_35425_.addActivity(Activity.CORE, VillagerGoalPackages.getCorePackage(villagerprofession, 0.5F));
      p_35425_.addActivityWithConditions(Activity.MEET, VillagerGoalPackages.getMeetPackage(villagerprofession, 0.5F), ImmutableSet.of(Pair.of(MemoryModuleType.MEETING_POINT, MemoryStatus.VALUE_PRESENT)));
      p_35425_.addActivity(Activity.REST, VillagerGoalPackages.getRestPackage(villagerprofession, 0.5F));
      p_35425_.addActivity(Activity.IDLE, VillagerGoalPackages.getIdlePackage(villagerprofession, 0.5F));
      p_35425_.addActivity(Activity.PANIC, VillagerGoalPackages.getPanicPackage(villagerprofession, 0.5F));
      p_35425_.addActivity(Activity.PRE_RAID, VillagerGoalPackages.getPreRaidPackage(villagerprofession, 0.5F));
      p_35425_.addActivity(Activity.RAID, VillagerGoalPackages.getRaidPackage(villagerprofession, 0.5F));
      p_35425_.addActivity(Activity.HIDE, VillagerGoalPackages.getHidePackage(villagerprofession, 0.5F));
      p_35425_.setCoreActivities(ImmutableSet.of(Activity.CORE));
      p_35425_.setDefaultActivity(Activity.IDLE);
      p_35425_.setActiveActivityIfPossible(Activity.IDLE);
      p_35425_.updateActivityFromSchedule(this.level.getDayTime(), this.level.getGameTime());
   }

   protected void ageBoundaryReached() {
      super.ageBoundaryReached();
      if (this.level instanceof ServerLevel) {
         this.refreshBrain((ServerLevel)this.level);
      }

   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.5D).add(Attributes.FOLLOW_RANGE, 48.0D);
   }

   public boolean assignProfessionWhenSpawned() {
      return this.assignProfessionWhenSpawned;
   }

   protected void customServerAiStep() {
      this.level.getProfiler().push("villagerBrain");
      this.getBrain().tick((ServerLevel)this.level, this);
      this.level.getProfiler().pop();
      if (this.assignProfessionWhenSpawned) {
         this.assignProfessionWhenSpawned = false;
      }

      if (!this.isTrading() && this.updateMerchantTimer > 0) {
         --this.updateMerchantTimer;
         if (this.updateMerchantTimer <= 0) {
            if (this.increaseProfessionLevelOnUpdate) {
               this.increaseMerchantCareer();
               this.increaseProfessionLevelOnUpdate = false;
            }

            this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 0));
         }
      }

      if (this.lastTradedPlayer != null && this.level instanceof ServerLevel) {
         ((ServerLevel)this.level).onReputationEvent(ReputationEventType.TRADE, this.lastTradedPlayer, this);
         this.level.broadcastEntityEvent(this, (byte)14);
         this.lastTradedPlayer = null;
      }

      if (!this.isNoAi() && this.random.nextInt(100) == 0) {
         Raid raid = ((ServerLevel)this.level).getRaidAt(this.blockPosition());
         if (raid != null && raid.isActive() && !raid.isOver()) {
            this.level.broadcastEntityEvent(this, (byte)42);
         }
      }

      if (this.getVillagerData().getProfession() == VillagerProfession.NONE && this.isTrading()) {
         this.stopTrading();
      }

      super.customServerAiStep();
   }

   public void tick() {
      super.tick();
      if (this.getUnhappyCounter() > 0) {
         this.setUnhappyCounter(this.getUnhappyCounter() - 1);
      }

      this.maybeDecayGossip();
   }

   public InteractionResult mobInteract(Player p_35472_, InteractionHand p_35473_) {
      ItemStack itemstack = p_35472_.getItemInHand(p_35473_);
      if (itemstack.getItem() != Items.VILLAGER_SPAWN_EGG && this.isAlive() && !this.isTrading() && !this.isSleeping() && !p_35472_.isSecondaryUseActive()) {
         if (this.isBaby()) {
            this.setUnhappy();
            return InteractionResult.sidedSuccess(this.level.isClientSide);
         } else {
            boolean flag = this.getOffers().isEmpty();
            if (p_35473_ == InteractionHand.MAIN_HAND) {
               if (flag && !this.level.isClientSide) {
                  this.setUnhappy();
               }

               p_35472_.awardStat(Stats.TALKED_TO_VILLAGER);
            }

            if (flag) {
               return InteractionResult.sidedSuccess(this.level.isClientSide);
            } else {
               if (!this.level.isClientSide && !this.offers.isEmpty()) {
                  this.startTrading(p_35472_);
               }

               return InteractionResult.sidedSuccess(this.level.isClientSide);
            }
         }
      } else {
         return super.mobInteract(p_35472_, p_35473_);
      }
   }

   private void setUnhappy() {
      this.setUnhappyCounter(40);
      if (!this.level.isClientSide()) {
         this.playSound(SoundEvents.VILLAGER_NO, this.getSoundVolume(), this.getVoicePitch());
      }

   }

   private void startTrading(Player p_35537_) {
      this.updateSpecialPrices(p_35537_);
      this.setTradingPlayer(p_35537_);
      this.openTradingScreen(p_35537_, this.getDisplayName(), this.getVillagerData().getLevel());
   }

   public void setTradingPlayer(@Nullable Player p_35508_) {
      boolean flag = this.getTradingPlayer() != null && p_35508_ == null;
      super.setTradingPlayer(p_35508_);
      if (flag) {
         this.stopTrading();
      }

   }

   protected void stopTrading() {
      super.stopTrading();
      this.resetSpecialPrices();
   }

   private void resetSpecialPrices() {
      for(MerchantOffer merchantoffer : this.getOffers()) {
         merchantoffer.resetSpecialPriceDiff();
      }

   }

   public boolean canRestock() {
      return true;
   }

   public boolean isClientSide() {
      return this.getLevel().isClientSide;
   }

   public void restock() {
      this.updateDemand();

      for(MerchantOffer merchantoffer : this.getOffers()) {
         merchantoffer.resetUses();
      }

      this.lastRestockGameTime = this.level.getGameTime();
      ++this.numberOfRestocksToday;
   }

   private boolean needsToRestock() {
      for(MerchantOffer merchantoffer : this.getOffers()) {
         if (merchantoffer.needsRestock()) {
            return true;
         }
      }

      return false;
   }

   private boolean allowedToRestock() {
      return this.numberOfRestocksToday == 0 || this.numberOfRestocksToday < 2 && this.level.getGameTime() > this.lastRestockGameTime + 2400L;
   }

   public boolean shouldRestock() {
      long i = this.lastRestockGameTime + 12000L;
      long j = this.level.getGameTime();
      boolean flag = j > i;
      long k = this.level.getDayTime();
      if (this.lastRestockCheckDayTime > 0L) {
         long l = this.lastRestockCheckDayTime / 24000L;
         long i1 = k / 24000L;
         flag |= i1 > l;
      }

      this.lastRestockCheckDayTime = k;
      if (flag) {
         this.lastRestockGameTime = j;
         this.resetNumberOfRestocks();
      }

      return this.allowedToRestock() && this.needsToRestock();
   }

   private void catchUpDemand() {
      int i = 2 - this.numberOfRestocksToday;
      if (i > 0) {
         for(MerchantOffer merchantoffer : this.getOffers()) {
            merchantoffer.resetUses();
         }
      }

      for(int j = 0; j < i; ++j) {
         this.updateDemand();
      }

   }

   private void updateDemand() {
      for(MerchantOffer merchantoffer : this.getOffers()) {
         merchantoffer.updateDemand();
      }

   }

   private void updateSpecialPrices(Player p_35541_) {
      int i = this.getPlayerReputation(p_35541_);
      if (i != 0) {
         for(MerchantOffer merchantoffer : this.getOffers()) {
            merchantoffer.addToSpecialPriceDiff(-Mth.floor((float)i * merchantoffer.getPriceMultiplier()));
         }
      }

      if (p_35541_.hasEffect(MobEffects.HERO_OF_THE_VILLAGE)) {
         MobEffectInstance mobeffectinstance = p_35541_.getEffect(MobEffects.HERO_OF_THE_VILLAGE);
         int k = mobeffectinstance.getAmplifier();

         for(MerchantOffer merchantoffer1 : this.getOffers()) {
            double d0 = 0.3D + 0.0625D * (double)k;
            int j = (int)Math.floor(d0 * (double)merchantoffer1.getBaseCostA().getCount());
            merchantoffer1.addToSpecialPriceDiff(-Math.max(j, 1));
         }
      }

   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_VILLAGER_DATA, new VillagerData(VillagerType.PLAINS, VillagerProfession.NONE, 1));
   }

   public void addAdditionalSaveData(CompoundTag p_35481_) {
      super.addAdditionalSaveData(p_35481_);
      VillagerData.CODEC.encodeStart(NbtOps.INSTANCE, this.getVillagerData()).resultOrPartial(LOGGER::error).ifPresent((p_35454_) -> {
         p_35481_.put("VillagerData", p_35454_);
      });
      p_35481_.putByte("FoodLevel", (byte)this.foodLevel);
      p_35481_.put("Gossips", this.gossips.store(NbtOps.INSTANCE).getValue());
      p_35481_.putInt("Xp", this.villagerXp);
      p_35481_.putLong("LastRestock", this.lastRestockGameTime);
      p_35481_.putLong("LastGossipDecay", this.lastGossipDecayTime);
      p_35481_.putInt("RestocksToday", this.numberOfRestocksToday);
      if (this.assignProfessionWhenSpawned) {
         p_35481_.putBoolean("AssignProfessionWhenSpawned", true);
      }

   }

   public void readAdditionalSaveData(CompoundTag p_35451_) {
      super.readAdditionalSaveData(p_35451_);
      if (p_35451_.contains("VillagerData", 10)) {
         DataResult<VillagerData> dataresult = VillagerData.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, p_35451_.get("VillagerData")));
         dataresult.resultOrPartial(LOGGER::error).ifPresent(this::setVillagerData);
      }

      if (p_35451_.contains("Offers", 10)) {
         this.offers = new MerchantOffers(p_35451_.getCompound("Offers"));
      }

      if (p_35451_.contains("FoodLevel", 1)) {
         this.foodLevel = p_35451_.getByte("FoodLevel");
      }

      ListTag listtag = p_35451_.getList("Gossips", 10);
      this.gossips.update(new Dynamic<>(NbtOps.INSTANCE, listtag));
      if (p_35451_.contains("Xp", 3)) {
         this.villagerXp = p_35451_.getInt("Xp");
      }

      this.lastRestockGameTime = p_35451_.getLong("LastRestock");
      this.lastGossipDecayTime = p_35451_.getLong("LastGossipDecay");
      this.setCanPickUpLoot(true);
      if (this.level instanceof ServerLevel) {
         this.refreshBrain((ServerLevel)this.level);
      }

      this.numberOfRestocksToday = p_35451_.getInt("RestocksToday");
      if (p_35451_.contains("AssignProfessionWhenSpawned")) {
         this.assignProfessionWhenSpawned = p_35451_.getBoolean("AssignProfessionWhenSpawned");
      }

   }

   public boolean removeWhenFarAway(double p_35535_) {
      return false;
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      if (this.isSleeping()) {
         return null;
      } else {
         return this.isTrading() ? SoundEvents.VILLAGER_TRADE : SoundEvents.VILLAGER_AMBIENT;
      }
   }

   protected SoundEvent getHurtSound(DamageSource p_35498_) {
      return SoundEvents.VILLAGER_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.VILLAGER_DEATH;
   }

   public void playWorkSound() {
      SoundEvent soundevent = this.getVillagerData().getProfession().getWorkSound();
      if (soundevent != null) {
         this.playSound(soundevent, this.getSoundVolume(), this.getVoicePitch());
      }

   }

   public void setVillagerData(VillagerData p_35437_) {
      VillagerData villagerdata = this.getVillagerData();
      if (villagerdata.getProfession() != p_35437_.getProfession()) {
         this.offers = null;
      }

      this.entityData.set(DATA_VILLAGER_DATA, p_35437_);
   }

   public VillagerData getVillagerData() {
      return this.entityData.get(DATA_VILLAGER_DATA);
   }

   protected void rewardTradeXp(MerchantOffer p_35475_) {
      int i = 3 + this.random.nextInt(4);
      this.villagerXp += p_35475_.getXp();
      this.lastTradedPlayer = this.getTradingPlayer();
      if (this.shouldIncreaseLevel()) {
         this.updateMerchantTimer = 40;
         this.increaseProfessionLevelOnUpdate = true;
         i += 5;
      }

      if (p_35475_.shouldRewardExp()) {
         this.level.addFreshEntity(new ExperienceOrb(this.level, this.getX(), this.getY() + 0.5D, this.getZ(), i));
      }

   }

   public void setChasing(boolean p_150016_) {
      this.chasing = p_150016_;
   }

   public boolean isChasing() {
      return this.chasing;
   }

   public void setLastHurtByMob(@Nullable LivingEntity p_35423_) {
      if (p_35423_ != null && this.level instanceof ServerLevel) {
         ((ServerLevel)this.level).onReputationEvent(ReputationEventType.VILLAGER_HURT, p_35423_, this);
         if (this.isAlive() && p_35423_ instanceof Player) {
            this.level.broadcastEntityEvent(this, (byte)13);
         }
      }

      super.setLastHurtByMob(p_35423_);
   }

   public void die(DamageSource p_35419_) {
      LOGGER.info("Villager {} died, message: '{}'", this, p_35419_.getLocalizedDeathMessage(this).getString());
      Entity entity = p_35419_.getEntity();
      if (entity != null) {
         this.tellWitnessesThatIWasMurdered(entity);
      }

      this.releaseAllPois();
      super.die(p_35419_);
   }

   private void releaseAllPois() {
      this.releasePoi(MemoryModuleType.HOME);
      this.releasePoi(MemoryModuleType.JOB_SITE);
      this.releasePoi(MemoryModuleType.POTENTIAL_JOB_SITE);
      this.releasePoi(MemoryModuleType.MEETING_POINT);
   }

   private void tellWitnessesThatIWasMurdered(Entity p_35421_) {
      Level $$3 = this.level;
      if ($$3 instanceof ServerLevel) {
         ServerLevel serverlevel = (ServerLevel)$$3;
         Optional<NearestVisibleLivingEntities> optional = this.brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
         if (!optional.isEmpty()) {
            optional.get().findAll(ReputationEventHandler.class::isInstance).forEach((p_186297_) -> {
               serverlevel.onReputationEvent(ReputationEventType.VILLAGER_KILLED, p_35421_, (ReputationEventHandler)p_186297_);
            });
         }
      }
   }

   public void releasePoi(MemoryModuleType<GlobalPos> p_35429_) {
      if (this.level instanceof ServerLevel) {
         MinecraftServer minecraftserver = ((ServerLevel)this.level).getServer();
         this.brain.getMemory(p_35429_).ifPresent((p_186306_) -> {
            ServerLevel serverlevel = minecraftserver.getLevel(p_186306_.dimension());
            if (serverlevel != null) {
               PoiManager poimanager = serverlevel.getPoiManager();
               Optional<PoiType> optional = poimanager.getType(p_186306_.pos());
               BiPredicate<Villager, PoiType> bipredicate = POI_MEMORIES.get(p_35429_);
               if (optional.isPresent() && bipredicate.test(this, optional.get())) {
                  poimanager.release(p_186306_.pos());
                  DebugPackets.sendPoiTicketCountPacket(serverlevel, p_186306_.pos());
               }

            }
         });
      }
   }

   public boolean canBreed() {
      return this.foodLevel + this.countFoodPointsInInventory() >= 12 && this.getAge() == 0;
   }

   private boolean hungry() {
      return this.foodLevel < 12;
   }

   private void eatUntilFull() {
      if (this.hungry() && this.countFoodPointsInInventory() != 0) {
         for(int i = 0; i < this.getInventory().getContainerSize(); ++i) {
            ItemStack itemstack = this.getInventory().getItem(i);
            if (!itemstack.isEmpty()) {
               Integer integer = FOOD_POINTS.get(itemstack.getItem());
               if (integer != null) {
                  int j = itemstack.getCount();

                  for(int k = j; k > 0; --k) {
                     this.foodLevel += integer;
                     this.getInventory().removeItem(i, 1);
                     if (!this.hungry()) {
                        return;
                     }
                  }
               }
            }
         }

      }
   }

   public int getPlayerReputation(Player p_35533_) {
      return this.gossips.getReputation(p_35533_.getUUID(), (p_186302_) -> {
         return true;
      });
   }

   private void digestFood(int p_35549_) {
      this.foodLevel -= p_35549_;
   }

   public void eatAndDigestFood() {
      this.eatUntilFull();
      this.digestFood(12);
   }

   public void setOffers(MerchantOffers p_35477_) {
      this.offers = p_35477_;
   }

   private boolean shouldIncreaseLevel() {
      int i = this.getVillagerData().getLevel();
      return VillagerData.canLevelUp(i) && this.villagerXp >= VillagerData.getMaxXpPerLevel(i);
   }

   private void increaseMerchantCareer() {
      this.setVillagerData(this.getVillagerData().setLevel(this.getVillagerData().getLevel() + 1));
      this.updateTrades();
   }

   protected Component getTypeName() {
      net.minecraft.resources.ResourceLocation profName = this.getVillagerData().getProfession().getRegistryName();
      return new TranslatableComponent(this.getType().getDescriptionId() + '.' + (!"minecraft".equals(profName.getNamespace()) ? profName.getNamespace() + '.' : "") + profName.getPath());
   }

   public void handleEntityEvent(byte p_35391_) {
      if (p_35391_ == 12) {
         this.addParticlesAroundSelf(ParticleTypes.HEART);
      } else if (p_35391_ == 13) {
         this.addParticlesAroundSelf(ParticleTypes.ANGRY_VILLAGER);
      } else if (p_35391_ == 14) {
         this.addParticlesAroundSelf(ParticleTypes.HAPPY_VILLAGER);
      } else if (p_35391_ == 42) {
         this.addParticlesAroundSelf(ParticleTypes.SPLASH);
      } else {
         super.handleEntityEvent(p_35391_);
      }

   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_35439_, DifficultyInstance p_35440_, MobSpawnType p_35441_, @Nullable SpawnGroupData p_35442_, @Nullable CompoundTag p_35443_) {
      if (p_35441_ == MobSpawnType.BREEDING) {
         this.setVillagerData(this.getVillagerData().setProfession(VillagerProfession.NONE));
      }

      if (p_35441_ == MobSpawnType.COMMAND || p_35441_ == MobSpawnType.SPAWN_EGG || p_35441_ == MobSpawnType.SPAWNER || p_35441_ == MobSpawnType.DISPENSER) {
         this.setVillagerData(this.getVillagerData().setType(VillagerType.byBiome(p_35439_.getBiome(this.blockPosition()))));
      }

      if (p_35441_ == MobSpawnType.STRUCTURE) {
         this.assignProfessionWhenSpawned = true;
      }

      return super.finalizeSpawn(p_35439_, p_35440_, p_35441_, p_35442_, p_35443_);
   }

   public Villager getBreedOffspring(ServerLevel p_150012_, AgeableMob p_150013_) {
      double d0 = this.random.nextDouble();
      VillagerType villagertype;
      if (d0 < 0.5D) {
         villagertype = VillagerType.byBiome(p_150012_.getBiome(this.blockPosition()));
      } else if (d0 < 0.75D) {
         villagertype = this.getVillagerData().getType();
      } else {
         villagertype = ((Villager)p_150013_).getVillagerData().getType();
      }

      Villager villager = new Villager(EntityType.VILLAGER, p_150012_, villagertype);
      villager.finalizeSpawn(p_150012_, p_150012_.getCurrentDifficultyAt(villager.blockPosition()), MobSpawnType.BREEDING, (SpawnGroupData)null, (CompoundTag)null);
      return villager;
   }

   public void thunderHit(ServerLevel p_35409_, LightningBolt p_35410_) {
      if (p_35409_.getDifficulty() != Difficulty.PEACEFUL && net.minecraftforge.event.ForgeEventFactory.canLivingConvert(this, EntityType.WITCH, (timer) -> {})) {
         LOGGER.info("Villager {} was struck by lightning {}.", this, p_35410_);
         Witch witch = EntityType.WITCH.create(p_35409_);
         witch.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
         witch.finalizeSpawn(p_35409_, p_35409_.getCurrentDifficultyAt(witch.blockPosition()), MobSpawnType.CONVERSION, (SpawnGroupData)null, (CompoundTag)null);
         witch.setNoAi(this.isNoAi());
         if (this.hasCustomName()) {
            witch.setCustomName(this.getCustomName());
            witch.setCustomNameVisible(this.isCustomNameVisible());
         }

         witch.setPersistenceRequired();
         net.minecraftforge.event.ForgeEventFactory.onLivingConvert(this, witch);
         p_35409_.addFreshEntityWithPassengers(witch);
         this.releaseAllPois();
         this.discard();
      } else {
         super.thunderHit(p_35409_, p_35410_);
      }

   }

   protected void pickUpItem(ItemEntity p_35467_) {
      ItemStack itemstack = p_35467_.getItem();
      if (this.wantsToPickUp(itemstack)) {
         SimpleContainer simplecontainer = this.getInventory();
         boolean flag = simplecontainer.canAddItem(itemstack);
         if (!flag) {
            return;
         }

         this.onItemPickup(p_35467_);
         this.take(p_35467_, itemstack.getCount());
         ItemStack itemstack1 = simplecontainer.addItem(itemstack);
         if (itemstack1.isEmpty()) {
            p_35467_.discard();
         } else {
            itemstack.setCount(itemstack1.getCount());
         }
      }

   }

   public boolean wantsToPickUp(ItemStack p_35543_) {
      Item item = p_35543_.getItem();
      return (WANTED_ITEMS.contains(item) || this.getVillagerData().getProfession().getRequestedItems().contains(item)) && this.getInventory().canAddItem(p_35543_);
   }

   public boolean hasExcessFood() {
      return this.countFoodPointsInInventory() >= 24;
   }

   public boolean wantsMoreFood() {
      return this.countFoodPointsInInventory() < 12;
   }

   private int countFoodPointsInInventory() {
      SimpleContainer simplecontainer = this.getInventory();
      return FOOD_POINTS.entrySet().stream().mapToInt((p_186300_) -> {
         return simplecontainer.countItem(p_186300_.getKey()) * p_186300_.getValue();
      }).sum();
   }

   public boolean hasFarmSeeds() {
      return this.getInventory().hasAnyOf(ImmutableSet.of(Items.WHEAT_SEEDS, Items.POTATO, Items.CARROT, Items.BEETROOT_SEEDS));
   }

   protected void updateTrades() {
      VillagerData villagerdata = this.getVillagerData();
      Int2ObjectMap<VillagerTrades.ItemListing[]> int2objectmap = VillagerTrades.TRADES.get(villagerdata.getProfession());
      if (int2objectmap != null && !int2objectmap.isEmpty()) {
         VillagerTrades.ItemListing[] avillagertrades$itemlisting = int2objectmap.get(villagerdata.getLevel());
         if (avillagertrades$itemlisting != null) {
            MerchantOffers merchantoffers = this.getOffers();
            this.addOffersFromItemListings(merchantoffers, avillagertrades$itemlisting, 2);
         }
      }
   }

   public void gossip(ServerLevel p_35412_, Villager p_35413_, long p_35414_) {
      if ((p_35414_ < this.lastGossipTime || p_35414_ >= this.lastGossipTime + 1200L) && (p_35414_ < p_35413_.lastGossipTime || p_35414_ >= p_35413_.lastGossipTime + 1200L)) {
         this.gossips.transferFrom(p_35413_.gossips, this.random, 10);
         this.lastGossipTime = p_35414_;
         p_35413_.lastGossipTime = p_35414_;
         this.spawnGolemIfNeeded(p_35412_, p_35414_, 5);
      }
   }

   private void maybeDecayGossip() {
      long i = this.level.getGameTime();
      if (this.lastGossipDecayTime == 0L) {
         this.lastGossipDecayTime = i;
      } else if (i >= this.lastGossipDecayTime + 24000L) {
         this.gossips.decay();
         this.lastGossipDecayTime = i;
      }
   }

   public void spawnGolemIfNeeded(ServerLevel p_35398_, long p_35399_, int p_35400_) {
      if (this.wantsToSpawnGolem(p_35399_)) {
         AABB aabb = this.getBoundingBox().inflate(10.0D, 10.0D, 10.0D);
         List<Villager> list = p_35398_.getEntitiesOfClass(Villager.class, aabb);
         List<Villager> list1 = list.stream().filter((p_186293_) -> {
            return p_186293_.wantsToSpawnGolem(p_35399_);
         }).limit(5L).collect(Collectors.toList());
         if (list1.size() >= p_35400_) {
            IronGolem irongolem = this.trySpawnGolem(p_35398_);
            if (irongolem != null) {
               list.forEach(GolemSensor::golemDetected);
            }
         }
      }
   }

   public boolean wantsToSpawnGolem(long p_35393_) {
      if (!this.golemSpawnConditionsMet(this.level.getGameTime())) {
         return false;
      } else {
         return !this.brain.hasMemoryValue(MemoryModuleType.GOLEM_DETECTED_RECENTLY);
      }
   }

   @Nullable
   private IronGolem trySpawnGolem(ServerLevel p_35491_) {
      BlockPos blockpos = this.blockPosition();

      for(int i = 0; i < 10; ++i) {
         double d0 = (double)(p_35491_.random.nextInt(16) - 8);
         double d1 = (double)(p_35491_.random.nextInt(16) - 8);
         BlockPos blockpos1 = this.findSpawnPositionForGolemInColumn(blockpos, d0, d1);
         if (blockpos1 != null) {
            IronGolem irongolem = EntityType.IRON_GOLEM.create(p_35491_, (CompoundTag)null, (Component)null, (Player)null, blockpos1, MobSpawnType.MOB_SUMMONED, false, false);
            if (irongolem != null) {
               if (irongolem.checkSpawnRules(p_35491_, MobSpawnType.MOB_SUMMONED) && irongolem.checkSpawnObstruction(p_35491_)) {
                  p_35491_.addFreshEntityWithPassengers(irongolem);
                  return irongolem;
               }

               irongolem.discard();
            }
         }
      }

      return null;
   }

   @Nullable
   private BlockPos findSpawnPositionForGolemInColumn(BlockPos p_35447_, double p_35448_, double p_35449_) {
      int i = 6;
      BlockPos blockpos = p_35447_.offset(p_35448_, 6.0D, p_35449_);
      BlockState blockstate = this.level.getBlockState(blockpos);

      for(int j = 6; j >= -6; --j) {
         BlockPos blockpos1 = blockpos;
         BlockState blockstate1 = blockstate;
         blockpos = blockpos.below();
         blockstate = this.level.getBlockState(blockpos);
         if ((blockstate1.isAir() || blockstate1.getMaterial().isLiquid()) && blockstate.getMaterial().isSolidBlocking()) {
            return blockpos1;
         }
      }

      return null;
   }

   public void onReputationEventFrom(ReputationEventType p_35431_, Entity p_35432_) {
      if (p_35431_ == ReputationEventType.ZOMBIE_VILLAGER_CURED) {
         this.gossips.add(p_35432_.getUUID(), GossipType.MAJOR_POSITIVE, 20);
         this.gossips.add(p_35432_.getUUID(), GossipType.MINOR_POSITIVE, 25);
      } else if (p_35431_ == ReputationEventType.TRADE) {
         this.gossips.add(p_35432_.getUUID(), GossipType.TRADING, 2);
      } else if (p_35431_ == ReputationEventType.VILLAGER_HURT) {
         this.gossips.add(p_35432_.getUUID(), GossipType.MINOR_NEGATIVE, 25);
      } else if (p_35431_ == ReputationEventType.VILLAGER_KILLED) {
         this.gossips.add(p_35432_.getUUID(), GossipType.MAJOR_NEGATIVE, 25);
      }

   }

   public int getVillagerXp() {
      return this.villagerXp;
   }

   public void setVillagerXp(int p_35547_) {
      this.villagerXp = p_35547_;
   }

   private void resetNumberOfRestocks() {
      this.catchUpDemand();
      this.numberOfRestocksToday = 0;
   }

   public GossipContainer getGossips() {
      return this.gossips;
   }

   public void setGossips(Tag p_35456_) {
      this.gossips.update(new Dynamic<>(NbtOps.INSTANCE, p_35456_));
   }

   protected void sendDebugPackets() {
      super.sendDebugPackets();
      DebugPackets.sendEntityBrain(this);
   }

   public void startSleeping(BlockPos p_35479_) {
      super.startSleeping(p_35479_);
      this.brain.setMemory(MemoryModuleType.LAST_SLEPT, this.level.getGameTime());
      this.brain.eraseMemory(MemoryModuleType.WALK_TARGET);
      this.brain.eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
   }

   public void stopSleeping() {
      super.stopSleeping();
      this.brain.setMemory(MemoryModuleType.LAST_WOKEN, this.level.getGameTime());
   }

   private boolean golemSpawnConditionsMet(long p_35462_) {
      Optional<Long> optional = this.brain.getMemory(MemoryModuleType.LAST_SLEPT);
      if (optional.isPresent()) {
         return p_35462_ - optional.get() < 24000L;
      } else {
         return false;
      }
   }
}
