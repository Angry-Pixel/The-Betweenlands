package net.minecraft.world.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2DoubleArrayMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.BlockUtil;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.Nameable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.HoneyBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntityInLevelCallback;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListenerRegistrar;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import org.slf4j.Logger;

public abstract class Entity extends net.minecraftforge.common.capabilities.CapabilityProvider<Entity> implements Nameable, EntityAccess, CommandSource, net.minecraftforge.common.extensions.IForgeEntity {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final String ID_TAG = "id";
   public static final String PASSENGERS_TAG = "Passengers";
   protected static final AtomicInteger ENTITY_COUNTER = new AtomicInteger();
   private static final List<ItemStack> EMPTY_LIST = Collections.emptyList();
   public static final int BOARDING_COOLDOWN = 60;
   public static final int TOTAL_AIR_SUPPLY = 300;
   public static final int MAX_ENTITY_TAG_COUNT = 1024;
   public static final double DELTA_AFFECTED_BY_BLOCKS_BELOW = 0.5000001D;
   public static final float BREATHING_DISTANCE_BELOW_EYES = 0.11111111F;
   public static final int BASE_TICKS_REQUIRED_TO_FREEZE = 140;
   public static final int FREEZE_HURT_FREQUENCY = 40;
   private static final AABB INITIAL_AABB = new AABB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
   private static final double WATER_FLOW_SCALE = 0.014D;
   private static final double LAVA_FAST_FLOW_SCALE = 0.007D;
   private static final double LAVA_SLOW_FLOW_SCALE = 0.0023333333333333335D;
   public static final String UUID_TAG = "UUID";
   private static double viewScale = 1.0D;
   @Deprecated // Forge: Use the getter to allow overriding in mods
   private final EntityType<?> type;
   private int id = ENTITY_COUNTER.incrementAndGet();
   public boolean blocksBuilding;
   private ImmutableList<Entity> passengers = ImmutableList.of();
   protected int boardingCooldown;
   @Nullable
   private Entity vehicle;
   public Level level;
   public double xo;
   public double yo;
   public double zo;
   private Vec3 position;
   private BlockPos blockPosition;
   private ChunkPos chunkPosition;
   private Vec3 deltaMovement = Vec3.ZERO;
   private float yRot;
   private float xRot;
   public float yRotO;
   public float xRotO;
   private AABB bb = INITIAL_AABB;
   protected boolean onGround;
   public boolean horizontalCollision;
   public boolean verticalCollision;
   public boolean verticalCollisionBelow;
   public boolean minorHorizontalCollision;
   public boolean hurtMarked;
   protected Vec3 stuckSpeedMultiplier = Vec3.ZERO;
   @Nullable
   private Entity.RemovalReason removalReason;
   public static final float DEFAULT_BB_WIDTH = 0.6F;
   public static final float DEFAULT_BB_HEIGHT = 1.8F;
   public float walkDistO;
   public float walkDist;
   public float moveDist;
   public float flyDist;
   public float fallDistance;
   private float nextStep = 1.0F;
   public double xOld;
   public double yOld;
   public double zOld;
   @Deprecated // Forge - see IForgeEntity#getStepHeight
   public float maxUpStep;
   public boolean noPhysics;
   protected final Random random = new Random();
   public int tickCount;
   private int remainingFireTicks = -this.getFireImmuneTicks();
   protected boolean wasTouchingWater;
   protected Object2DoubleMap<TagKey<Fluid>> fluidHeight = new Object2DoubleArrayMap<>(2);
   protected boolean wasEyeInWater;
   private final Set<TagKey<Fluid>> fluidOnEyes = new HashSet<>();
   public int invulnerableTime;
   protected boolean firstTick = true;
   protected final SynchedEntityData entityData;
   protected static final EntityDataAccessor<Byte> DATA_SHARED_FLAGS_ID = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.BYTE);
   protected static final int FLAG_ONFIRE = 0;
   private static final int FLAG_SHIFT_KEY_DOWN = 1;
   private static final int FLAG_SPRINTING = 3;
   private static final int FLAG_SWIMMING = 4;
   private static final int FLAG_INVISIBLE = 5;
   protected static final int FLAG_GLOWING = 6;
   protected static final int FLAG_FALL_FLYING = 7;
   private static final EntityDataAccessor<Integer> DATA_AIR_SUPPLY_ID = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Optional<Component>> DATA_CUSTOM_NAME = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.OPTIONAL_COMPONENT);
   private static final EntityDataAccessor<Boolean> DATA_CUSTOM_NAME_VISIBLE = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> DATA_SILENT = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> DATA_NO_GRAVITY = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.BOOLEAN);
   protected static final EntityDataAccessor<Pose> DATA_POSE = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.POSE);
   private static final EntityDataAccessor<Integer> DATA_TICKS_FROZEN = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.INT);
   private EntityInLevelCallback levelCallback = EntityInLevelCallback.NULL;
   private Vec3 packetCoordinates;
   public boolean noCulling;
   public boolean hasImpulse;
   private int portalCooldown;
   protected boolean isInsidePortal;
   protected int portalTime;
   protected BlockPos portalEntrancePos;
   private boolean invulnerable;
   protected UUID uuid = Mth.createInsecureUUID(this.random);
   protected String stringUUID = this.uuid.toString();
   private boolean hasGlowingTag;
   private final Set<String> tags = Sets.newHashSet();
   private final double[] pistonDeltas = new double[]{0.0D, 0.0D, 0.0D};
   private long pistonDeltasGameTime;
   private EntityDimensions dimensions;
   private float eyeHeight;
   public boolean isInPowderSnow;
   public boolean wasInPowderSnow;
   public boolean wasOnFire;
   private float crystalSoundIntensity;
   private int lastCrystalSoundPlayTick;
   private boolean hasVisualFire;
   @Nullable
   private BlockState feetBlockState = null;

   public Entity(EntityType<?> p_19870_, Level p_19871_) {
      super(Entity.class);
      this.type = p_19870_;
      this.level = p_19871_;
      this.dimensions = p_19870_.getDimensions();
      this.position = Vec3.ZERO;
      this.blockPosition = BlockPos.ZERO;
      this.chunkPosition = ChunkPos.ZERO;
      this.packetCoordinates = Vec3.ZERO;
      this.entityData = new SynchedEntityData(this);
      this.entityData.define(DATA_SHARED_FLAGS_ID, (byte)0);
      this.entityData.define(DATA_AIR_SUPPLY_ID, this.getMaxAirSupply());
      this.entityData.define(DATA_CUSTOM_NAME_VISIBLE, false);
      this.entityData.define(DATA_CUSTOM_NAME, Optional.empty());
      this.entityData.define(DATA_SILENT, false);
      this.entityData.define(DATA_NO_GRAVITY, false);
      this.entityData.define(DATA_POSE, Pose.STANDING);
      this.entityData.define(DATA_TICKS_FROZEN, 0);
      this.defineSynchedData();
      this.setPos(0.0D, 0.0D, 0.0D);
      net.minecraftforge.event.entity.EntityEvent.Size sizeEvent = net.minecraftforge.event.ForgeEventFactory.getEntitySizeForge(this, Pose.STANDING, this.dimensions, this.getEyeHeight(Pose.STANDING, this.dimensions));
      this.dimensions = sizeEvent.getNewSize();
      this.eyeHeight = sizeEvent.getNewEyeHeight();
      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.EntityEvent.EntityConstructing(this));
      this.gatherCapabilities();
   }

   public boolean isColliding(BlockPos p_20040_, BlockState p_20041_) {
      VoxelShape voxelshape = p_20041_.getCollisionShape(this.level, p_20040_, CollisionContext.of(this));
      VoxelShape voxelshape1 = voxelshape.move((double)p_20040_.getX(), (double)p_20040_.getY(), (double)p_20040_.getZ());
      return Shapes.joinIsNotEmpty(voxelshape1, Shapes.create(this.getBoundingBox()), BooleanOp.AND);
   }

   public int getTeamColor() {
      Team team = this.getTeam();
      return team != null && team.getColor().getColor() != null ? team.getColor().getColor() : 16777215;
   }

   public boolean isSpectator() {
      return false;
   }

   public final void unRide() {
      if (this.isVehicle()) {
         this.ejectPassengers();
      }

      if (this.isPassenger()) {
         this.stopRiding();
      }

   }

   public void setPacketCoordinates(double p_20168_, double p_20169_, double p_20170_) {
      this.setPacketCoordinates(new Vec3(p_20168_, p_20169_, p_20170_));
   }

   public void setPacketCoordinates(Vec3 p_20014_) {
      this.packetCoordinates = p_20014_;
   }

   public Vec3 getPacketCoordinates() {
      return this.packetCoordinates;
   }

   public EntityType<?> getType() {
      return this.type;
   }

   public int getId() {
      return this.id;
   }

   public void setId(int p_20235_) {
      this.id = p_20235_;
   }

   public Set<String> getTags() {
      return this.tags;
   }

   public boolean addTag(String p_20050_) {
      return this.tags.size() >= 1024 ? false : this.tags.add(p_20050_);
   }

   public boolean removeTag(String p_20138_) {
      return this.tags.remove(p_20138_);
   }

   public void kill() {
      this.remove(Entity.RemovalReason.KILLED);
   }

   public final void discard() {
      this.remove(Entity.RemovalReason.DISCARDED);
   }

   protected abstract void defineSynchedData();

   public SynchedEntityData getEntityData() {
      return this.entityData;
   }

   public boolean equals(Object p_20245_) {
      if (p_20245_ instanceof Entity) {
         return ((Entity)p_20245_).id == this.id;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.id;
   }

   public void remove(Entity.RemovalReason p_146834_) {
      this.setRemoved(p_146834_);
      if (p_146834_ == Entity.RemovalReason.KILLED) {
         this.gameEvent(GameEvent.ENTITY_KILLED);
      }
      this.invalidateCaps();

   }

   public void onClientRemoval() {
   }

   public void setPose(Pose p_20125_) {
      this.entityData.set(DATA_POSE, p_20125_);
   }

   public Pose getPose() {
      return this.entityData.get(DATA_POSE);
   }

   public boolean closerThan(Entity p_19951_, double p_19952_) {
      double d0 = p_19951_.position.x - this.position.x;
      double d1 = p_19951_.position.y - this.position.y;
      double d2 = p_19951_.position.z - this.position.z;
      return d0 * d0 + d1 * d1 + d2 * d2 < p_19952_ * p_19952_;
   }

   protected void setRot(float p_19916_, float p_19917_) {
      this.setYRot(p_19916_ % 360.0F);
      this.setXRot(p_19917_ % 360.0F);
   }

   public final void setPos(Vec3 p_146885_) {
      this.setPos(p_146885_.x(), p_146885_.y(), p_146885_.z());
   }

   public void setPos(double p_20210_, double p_20211_, double p_20212_) {
      this.setPosRaw(p_20210_, p_20211_, p_20212_);
      this.setBoundingBox(this.makeBoundingBox());
   }

   protected AABB makeBoundingBox() {
      return this.dimensions.makeBoundingBox(this.position);
   }

   protected void reapplyPosition() {
      this.setPos(this.position.x, this.position.y, this.position.z);
   }

   public void turn(double p_19885_, double p_19886_) {
      float f = (float)p_19886_ * 0.15F;
      float f1 = (float)p_19885_ * 0.15F;
      this.setXRot(this.getXRot() + f);
      this.setYRot(this.getYRot() + f1);
      this.setXRot(Mth.clamp(this.getXRot(), -90.0F, 90.0F));
      this.xRotO += f;
      this.yRotO += f1;
      this.xRotO = Mth.clamp(this.xRotO, -90.0F, 90.0F);
      if (this.vehicle != null) {
         this.vehicle.onPassengerTurned(this);
      }

   }

   public void tick() {
      this.baseTick();
   }

   public void baseTick() {
      this.level.getProfiler().push("entityBaseTick");
      this.feetBlockState = null;
      if (this.isPassenger() && this.getVehicle().isRemoved()) {
         this.stopRiding();
      }

      if (this.boardingCooldown > 0) {
         --this.boardingCooldown;
      }

      this.walkDistO = this.walkDist;
      this.xRotO = this.getXRot();
      this.yRotO = this.getYRot();
      this.handleNetherPortal();
      if (this.canSpawnSprintParticle()) {
         this.spawnSprintParticle();
      }

      this.wasInPowderSnow = this.isInPowderSnow;
      this.isInPowderSnow = false;
      this.updateInWaterStateAndDoFluidPushing();
      this.updateFluidOnEyes();
      this.updateSwimming();
      if (this.level.isClientSide) {
         this.clearFire();
      } else if (this.remainingFireTicks > 0) {
         if (this.fireImmune()) {
            this.setRemainingFireTicks(this.remainingFireTicks - 4);
            if (this.remainingFireTicks < 0) {
               this.clearFire();
            }
         } else {
            if (this.remainingFireTicks % 20 == 0 && !this.isInLava()) {
               this.hurt(DamageSource.ON_FIRE, 1.0F);
            }

            this.setRemainingFireTicks(this.remainingFireTicks - 1);
         }

         if (this.getTicksFrozen() > 0) {
            this.setTicksFrozen(0);
            this.level.levelEvent((Player)null, 1009, this.blockPosition, 1);
         }
      }

      if (this.isInLava()) {
         this.lavaHurt();
         this.fallDistance *= 0.5F;
      }

      this.checkOutOfWorld();
      if (!this.level.isClientSide) {
         this.setSharedFlagOnFire(this.remainingFireTicks > 0);
      }

      this.firstTick = false;
      this.level.getProfiler().pop();
   }

   public void setSharedFlagOnFire(boolean p_146869_) {
      this.setSharedFlag(0, p_146869_ || this.hasVisualFire);
   }

   public void checkOutOfWorld() {
      if (this.getY() < (double)(this.level.getMinBuildHeight() - 64)) {
         this.outOfWorld();
      }

   }

   public void setPortalCooldown() {
      this.portalCooldown = this.getDimensionChangingDelay();
   }

   public boolean isOnPortalCooldown() {
      return this.portalCooldown > 0;
   }

   protected void processPortalCooldown() {
      if (this.isOnPortalCooldown()) {
         --this.portalCooldown;
      }

   }

   public int getPortalWaitTime() {
      return 0;
   }

   public void lavaHurt() {
      if (!this.fireImmune()) {
         this.setSecondsOnFire(15);
         if (this.hurt(DamageSource.LAVA, 4.0F)) {
            this.playSound(SoundEvents.GENERIC_BURN, 0.4F, 2.0F + this.random.nextFloat() * 0.4F);
         }

      }
   }

   public void setSecondsOnFire(int p_20255_) {
      int i = p_20255_ * 20;
      if (this instanceof LivingEntity) {
         i = ProtectionEnchantment.getFireAfterDampener((LivingEntity)this, i);
      }

      if (this.remainingFireTicks < i) {
         this.setRemainingFireTicks(i);
      }

   }

   public void setRemainingFireTicks(int p_20269_) {
      this.remainingFireTicks = p_20269_;
   }

   public int getRemainingFireTicks() {
      return this.remainingFireTicks;
   }

   public void clearFire() {
      this.setRemainingFireTicks(0);
   }

   protected void outOfWorld() {
      this.discard();
   }

   public boolean isFree(double p_20230_, double p_20231_, double p_20232_) {
      return this.isFree(this.getBoundingBox().move(p_20230_, p_20231_, p_20232_));
   }

   private boolean isFree(AABB p_20132_) {
      return this.level.noCollision(this, p_20132_) && !this.level.containsAnyLiquid(p_20132_);
   }

   public void setOnGround(boolean p_20181_) {
      this.onGround = p_20181_;
   }

   public boolean isOnGround() {
      return this.onGround;
   }

   public void move(MoverType p_19973_, Vec3 p_19974_) {
      if (this.noPhysics) {
         this.setPos(this.getX() + p_19974_.x, this.getY() + p_19974_.y, this.getZ() + p_19974_.z);
      } else {
         this.wasOnFire = this.isOnFire();
         if (p_19973_ == MoverType.PISTON) {
            p_19974_ = this.limitPistonMovement(p_19974_);
            if (p_19974_.equals(Vec3.ZERO)) {
               return;
            }
         }

         this.level.getProfiler().push("move");
         if (this.stuckSpeedMultiplier.lengthSqr() > 1.0E-7D) {
            p_19974_ = p_19974_.multiply(this.stuckSpeedMultiplier);
            this.stuckSpeedMultiplier = Vec3.ZERO;
            this.setDeltaMovement(Vec3.ZERO);
         }

         p_19974_ = this.maybeBackOffFromEdge(p_19974_, p_19973_);
         Vec3 vec3 = this.collide(p_19974_);
         double d0 = vec3.lengthSqr();
         if (d0 > 1.0E-7D) {
            if (this.fallDistance != 0.0F && d0 >= 1.0D) {
               BlockHitResult blockhitresult = this.level.clip(new ClipContext(this.position(), this.position().add(vec3), ClipContext.Block.FALLDAMAGE_RESETTING, ClipContext.Fluid.WATER, this));
               if (blockhitresult.getType() != HitResult.Type.MISS) {
                  this.resetFallDistance();
               }
            }

            this.setPos(this.getX() + vec3.x, this.getY() + vec3.y, this.getZ() + vec3.z);
         }

         this.level.getProfiler().pop();
         this.level.getProfiler().push("rest");
         boolean flag1 = !Mth.equal(p_19974_.x, vec3.x);
         boolean flag = !Mth.equal(p_19974_.z, vec3.z);
         this.horizontalCollision = flag1 || flag;
         this.verticalCollision = p_19974_.y != vec3.y;
         this.verticalCollisionBelow = this.verticalCollision && p_19974_.y < 0.0D;
         if (this.horizontalCollision) {
            this.minorHorizontalCollision = this.isHorizontalCollisionMinor(vec3);
         } else {
            this.minorHorizontalCollision = false;
         }

         this.onGround = this.verticalCollision && p_19974_.y < 0.0D;
         BlockPos blockpos = this.getOnPos();
         BlockState blockstate = this.level.getBlockState(blockpos);
         this.checkFallDamage(vec3.y, this.onGround, blockstate, blockpos);
         if (this.isRemoved()) {
            this.level.getProfiler().pop();
         } else {
            if (this.horizontalCollision) {
               Vec3 vec31 = this.getDeltaMovement();
               this.setDeltaMovement(flag1 ? 0.0D : vec31.x, vec31.y, flag ? 0.0D : vec31.z);
            }

            Block block = blockstate.getBlock();
            if (p_19974_.y != vec3.y) {
               block.updateEntityAfterFallOn(this.level, this);
            }

            if (this.onGround && !this.isSteppingCarefully()) {
               block.stepOn(this.level, blockpos, blockstate, this);
            }

            Entity.MovementEmission entity$movementemission = this.getMovementEmission();
            if (entity$movementemission.emitsAnything() && !this.isPassenger()) {
               double d1 = vec3.x;
               double d2 = vec3.y;
               double d3 = vec3.z;
               this.flyDist = (float) ((double) this.flyDist + vec3.length() * 0.6D);
               if (!blockstate.is(BlockTags.CLIMBABLE) && !blockstate.is(Blocks.POWDER_SNOW)) {
                  d2 = 0.0D;
               }

               this.walkDist += (float)vec3.horizontalDistance() * 0.6F;
               this.moveDist += (float)Math.sqrt(d1 * d1 + d2 * d2 + d3 * d3) * 0.6F;
               if (this.moveDist > this.nextStep && !blockstate.isAir()) {
                  this.nextStep = this.nextStep();
                  if (this.isInWater()) {
                     if (entity$movementemission.emitsSounds()) {
                        Entity entity = this.isVehicle() && this.getControllingPassenger() != null ? this.getControllingPassenger() : this;
                        float f = entity == this ? 0.35F : 0.4F;
                        Vec3 vec32 = entity.getDeltaMovement();
                        float f1 = Math.min(1.0F, (float)Math.sqrt(vec32.x * vec32.x * (double)0.2F + vec32.y * vec32.y + vec32.z * vec32.z * (double)0.2F) * f);
                        this.playSwimSound(f1);
                     }

                     if (entity$movementemission.emitsEvents()) {
                        this.gameEvent(GameEvent.SWIM);
                     }
                  } else {
                     if (entity$movementemission.emitsSounds()) {
                        this.playAmethystStepSound(blockstate);
                        this.playStepSound(blockpos, blockstate);
                     }

                     if (entity$movementemission.emitsEvents() && !blockstate.is(BlockTags.OCCLUDES_VIBRATION_SIGNALS)) {
                        this.gameEvent(GameEvent.STEP);
                     }
                  }
               } else if (blockstate.isAir()) {
                  this.processFlappingMovement();
               }
            }

            this.tryCheckInsideBlocks();
            float f2 = this.getBlockSpeedFactor();
            this.setDeltaMovement(this.getDeltaMovement().multiply((double) f2, 1.0D, (double) f2));
         }
         if (this.level.getBlockStatesIfLoaded(this.getBoundingBox().deflate(1.0E-6D)).noneMatch((p_20127_) -> p_20127_.is(BlockTags.FIRE) || p_20127_.is(Blocks.LAVA))) {
            if (this.remainingFireTicks <= 0) {
               this.setRemainingFireTicks(-this.getFireImmuneTicks());
            }

            if (this.wasOnFire && (this.isInPowderSnow || this.isInWaterRainOrBubble())) {
               this.playEntityOnFireExtinguishedSound();
            }
         }

         if (this.isOnFire() && (this.isInPowderSnow || this.isInWaterRainOrBubble())) {
            this.setRemainingFireTicks(-this.getFireImmuneTicks());
         }

         this.level.getProfiler().pop();
      }
   }

   protected boolean isHorizontalCollisionMinor(Vec3 p_196625_) {
      return false;
   }

   protected void tryCheckInsideBlocks() {
      try {
         this.checkInsideBlocks();
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.forThrowable(throwable, "Checking entity block collision");
         CrashReportCategory crashreportcategory = crashreport.addCategory("Entity being checked for collision");
         this.fillCrashReportCategory(crashreportcategory);
         throw new ReportedException(crashreport);
      }
   }

   protected void playEntityOnFireExtinguishedSound() {
      this.playSound(SoundEvents.GENERIC_EXTINGUISH_FIRE, 0.7F, 1.6F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
   }

   protected void processFlappingMovement() {
      if (this.isFlapping()) {
         this.onFlap();
         if (this.getMovementEmission().emitsEvents()) {
            this.gameEvent(GameEvent.FLAP);
         }
      }

   }

   public BlockPos getOnPos() {
      int i = Mth.floor(this.position.x);
      int j = Mth.floor(this.position.y - (double)0.2F);
      int k = Mth.floor(this.position.z);
      BlockPos blockpos = new BlockPos(i, j, k);
      if (this.level.isEmptyBlock(blockpos)) {
         BlockPos blockpos1 = blockpos.below();
         BlockState blockstate = this.level.getBlockState(blockpos1);
         if (blockstate.collisionExtendsVertically(this.level, blockpos1, this)) {
            return blockpos1;
         }
      }

      return blockpos;
   }

   protected float getBlockJumpFactor() {
      float f = this.level.getBlockState(this.blockPosition()).getBlock().getJumpFactor();
      float f1 = this.level.getBlockState(this.getBlockPosBelowThatAffectsMyMovement()).getBlock().getJumpFactor();
      return (double)f == 1.0D ? f1 : f;
   }

   protected float getBlockSpeedFactor() {
      BlockState blockstate = this.level.getBlockState(this.blockPosition());
      float f = blockstate.getBlock().getSpeedFactor();
      if (!blockstate.is(Blocks.WATER) && !blockstate.is(Blocks.BUBBLE_COLUMN)) {
         return (double)f == 1.0D ? this.level.getBlockState(this.getBlockPosBelowThatAffectsMyMovement()).getBlock().getSpeedFactor() : f;
      } else {
         return f;
      }
   }

   protected BlockPos getBlockPosBelowThatAffectsMyMovement() {
      return new BlockPos(this.position.x, this.getBoundingBox().minY - 0.5000001D, this.position.z);
   }

   protected Vec3 maybeBackOffFromEdge(Vec3 p_20019_, MoverType p_20020_) {
      return p_20019_;
   }

   protected Vec3 limitPistonMovement(Vec3 p_20134_) {
      if (p_20134_.lengthSqr() <= 1.0E-7D) {
         return p_20134_;
      } else {
         long i = this.level.getGameTime();
         if (i != this.pistonDeltasGameTime) {
            Arrays.fill(this.pistonDeltas, 0.0D);
            this.pistonDeltasGameTime = i;
         }

         if (p_20134_.x != 0.0D) {
            double d2 = this.applyPistonMovementRestriction(Direction.Axis.X, p_20134_.x);
            return Math.abs(d2) <= (double)1.0E-5F ? Vec3.ZERO : new Vec3(d2, 0.0D, 0.0D);
         } else if (p_20134_.y != 0.0D) {
            double d1 = this.applyPistonMovementRestriction(Direction.Axis.Y, p_20134_.y);
            return Math.abs(d1) <= (double)1.0E-5F ? Vec3.ZERO : new Vec3(0.0D, d1, 0.0D);
         } else if (p_20134_.z != 0.0D) {
            double d0 = this.applyPistonMovementRestriction(Direction.Axis.Z, p_20134_.z);
            return Math.abs(d0) <= (double)1.0E-5F ? Vec3.ZERO : new Vec3(0.0D, 0.0D, d0);
         } else {
            return Vec3.ZERO;
         }
      }
   }

   private double applyPistonMovementRestriction(Direction.Axis p_20043_, double p_20044_) {
      int i = p_20043_.ordinal();
      double d0 = Mth.clamp(p_20044_ + this.pistonDeltas[i], -0.51D, 0.51D);
      p_20044_ = d0 - this.pistonDeltas[i];
      this.pistonDeltas[i] = d0;
      return p_20044_;
   }

   private Vec3 collide(Vec3 p_20273_) {
      AABB aabb = this.getBoundingBox();
      List<VoxelShape> list = this.level.getEntityCollisions(this, aabb.expandTowards(p_20273_));
      Vec3 vec3 = p_20273_.lengthSqr() == 0.0D ? p_20273_ : collideBoundingBox(this, p_20273_, aabb, this.level, list);
      boolean flag = p_20273_.x != vec3.x;
      boolean flag1 = p_20273_.y != vec3.y;
      boolean flag2 = p_20273_.z != vec3.z;
      boolean flag3 = this.onGround || flag1 && p_20273_.y < 0.0D;
      float stepHeight = getStepHeight();
      if (stepHeight > 0.0F && flag3 && (flag || flag2)) {
         Vec3 vec31 = collideBoundingBox(this, new Vec3(p_20273_.x, (double)stepHeight, p_20273_.z), aabb, this.level, list);
         Vec3 vec32 = collideBoundingBox(this, new Vec3(0.0D, (double)stepHeight, 0.0D), aabb.expandTowards(p_20273_.x, 0.0D, p_20273_.z), this.level, list);
         if (vec32.y < (double)stepHeight) {
            Vec3 vec33 = collideBoundingBox(this, new Vec3(p_20273_.x, 0.0D, p_20273_.z), aabb.move(vec32), this.level, list).add(vec32);
            if (vec33.horizontalDistanceSqr() > vec31.horizontalDistanceSqr()) {
               vec31 = vec33;
            }
         }

         if (vec31.horizontalDistanceSqr() > vec3.horizontalDistanceSqr()) {
            return vec31.add(collideBoundingBox(this, new Vec3(0.0D, -vec31.y + p_20273_.y, 0.0D), aabb.move(vec31), this.level, list));
         }
      }

      return vec3;
   }

   public static Vec3 collideBoundingBox(@Nullable Entity p_198895_, Vec3 p_198896_, AABB p_198897_, Level p_198898_, List<VoxelShape> p_198899_) {
      Builder<VoxelShape> builder = ImmutableList.builderWithExpectedSize(p_198899_.size() + 1);
      if (!p_198899_.isEmpty()) {
         builder.addAll(p_198899_);
      }

      WorldBorder worldborder = p_198898_.getWorldBorder();
      boolean flag = p_198895_ != null && worldborder.isInsideCloseToBorder(p_198895_, p_198897_.expandTowards(p_198896_));
      if (flag) {
         builder.add(worldborder.getCollisionShape());
      }

      builder.addAll(p_198898_.getBlockCollisions(p_198895_, p_198897_.expandTowards(p_198896_)));
      return collideWithShapes(p_198896_, p_198897_, builder.build());
   }

   private static Vec3 collideWithShapes(Vec3 p_198901_, AABB p_198902_, List<VoxelShape> p_198903_) {
      if (p_198903_.isEmpty()) {
         return p_198901_;
      } else {
         double d0 = p_198901_.x;
         double d1 = p_198901_.y;
         double d2 = p_198901_.z;
         if (d1 != 0.0D) {
            d1 = Shapes.collide(Direction.Axis.Y, p_198902_, p_198903_, d1);
            if (d1 != 0.0D) {
               p_198902_ = p_198902_.move(0.0D, d1, 0.0D);
            }
         }

         boolean flag = Math.abs(d0) < Math.abs(d2);
         if (flag && d2 != 0.0D) {
            d2 = Shapes.collide(Direction.Axis.Z, p_198902_, p_198903_, d2);
            if (d2 != 0.0D) {
               p_198902_ = p_198902_.move(0.0D, 0.0D, d2);
            }
         }

         if (d0 != 0.0D) {
            d0 = Shapes.collide(Direction.Axis.X, p_198902_, p_198903_, d0);
            if (!flag && d0 != 0.0D) {
               p_198902_ = p_198902_.move(d0, 0.0D, 0.0D);
            }
         }

         if (!flag && d2 != 0.0D) {
            d2 = Shapes.collide(Direction.Axis.Z, p_198902_, p_198903_, d2);
         }

         return new Vec3(d0, d1, d2);
      }
   }

   protected float nextStep() {
      return (float)((int)this.moveDist + 1);
   }

   protected SoundEvent getSwimSound() {
      return SoundEvents.GENERIC_SWIM;
   }

   protected SoundEvent getSwimSplashSound() {
      return SoundEvents.GENERIC_SPLASH;
   }

   protected SoundEvent getSwimHighSpeedSplashSound() {
      return SoundEvents.GENERIC_SPLASH;
   }

   protected void checkInsideBlocks() {
      AABB aabb = this.getBoundingBox();
      BlockPos blockpos = new BlockPos(aabb.minX + 0.001D, aabb.minY + 0.001D, aabb.minZ + 0.001D);
      BlockPos blockpos1 = new BlockPos(aabb.maxX - 0.001D, aabb.maxY - 0.001D, aabb.maxZ - 0.001D);
      if (this.level.hasChunksAt(blockpos, blockpos1)) {
         BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

         for(int i = blockpos.getX(); i <= blockpos1.getX(); ++i) {
            for(int j = blockpos.getY(); j <= blockpos1.getY(); ++j) {
               for(int k = blockpos.getZ(); k <= blockpos1.getZ(); ++k) {
                  blockpos$mutableblockpos.set(i, j, k);
                  BlockState blockstate = this.level.getBlockState(blockpos$mutableblockpos);

                  try {
                     blockstate.entityInside(this.level, blockpos$mutableblockpos, this);
                     this.onInsideBlock(blockstate);
                  } catch (Throwable throwable) {
                     CrashReport crashreport = CrashReport.forThrowable(throwable, "Colliding entity with block");
                     CrashReportCategory crashreportcategory = crashreport.addCategory("Block being collided with");
                     CrashReportCategory.populateBlockDetails(crashreportcategory, this.level, blockpos$mutableblockpos, blockstate);
                     throw new ReportedException(crashreport);
                  }
               }
            }
         }
      }

   }

   protected void onInsideBlock(BlockState p_20005_) {
   }

   public void gameEvent(GameEvent p_146856_, @Nullable Entity p_146857_, BlockPos p_146858_) {
      this.level.gameEvent(p_146857_, p_146856_, p_146858_);
   }

   public void gameEvent(GameEvent p_146853_, @Nullable Entity p_146854_) {
      this.gameEvent(p_146853_, p_146854_, this.blockPosition);
   }

   public void gameEvent(GameEvent p_146860_, BlockPos p_146861_) {
      this.gameEvent(p_146860_, this, p_146861_);
   }

   public void gameEvent(GameEvent p_146851_) {
      this.gameEvent(p_146851_, this.blockPosition);
   }

   protected void playStepSound(BlockPos p_20135_, BlockState p_20136_) {
      if (!p_20136_.getMaterial().isLiquid()) {
         BlockState blockstate = this.level.getBlockState(p_20135_.above());
         SoundType soundtype = blockstate.is(Blocks.SNOW) ? blockstate.getSoundType(level, p_20135_, this) : p_20136_.getSoundType(level, p_20135_, this);
         this.playSound(soundtype.getStepSound(), soundtype.getVolume() * 0.15F, soundtype.getPitch());
      }
   }

   private void playAmethystStepSound(BlockState p_146883_) {
      if (p_146883_.is(BlockTags.CRYSTAL_SOUND_BLOCKS) && this.tickCount >= this.lastCrystalSoundPlayTick + 20) {
         this.crystalSoundIntensity *= (float)Math.pow(0.997D, (double)(this.tickCount - this.lastCrystalSoundPlayTick));
         this.crystalSoundIntensity = Math.min(1.0F, this.crystalSoundIntensity + 0.07F);
         float f = 0.5F + this.crystalSoundIntensity * this.random.nextFloat() * 1.2F;
         float f1 = 0.1F + this.crystalSoundIntensity * 1.2F;
         this.playSound(SoundEvents.AMETHYST_BLOCK_CHIME, f1, f);
         this.lastCrystalSoundPlayTick = this.tickCount;
      }

   }

   protected void playSwimSound(float p_20213_) {
      this.playSound(this.getSwimSound(), p_20213_, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
   }

   protected void onFlap() {
   }

   protected boolean isFlapping() {
      return false;
   }

   public void playSound(SoundEvent p_19938_, float p_19939_, float p_19940_) {
      if (!this.isSilent()) {
         this.level.playSound((Player)null, this.getX(), this.getY(), this.getZ(), p_19938_, this.getSoundSource(), p_19939_, p_19940_);
      }

   }

   public boolean isSilent() {
      return this.entityData.get(DATA_SILENT);
   }

   public void setSilent(boolean p_20226_) {
      this.entityData.set(DATA_SILENT, p_20226_);
   }

   public boolean isNoGravity() {
      return this.entityData.get(DATA_NO_GRAVITY);
   }

   public void setNoGravity(boolean p_20243_) {
      this.entityData.set(DATA_NO_GRAVITY, p_20243_);
   }

   protected Entity.MovementEmission getMovementEmission() {
      return Entity.MovementEmission.ALL;
   }

   public boolean occludesVibrations() {
      return false;
   }

   protected void checkFallDamage(double p_19911_, boolean p_19912_, BlockState p_19913_, BlockPos p_19914_) {
      if (p_19912_) {
         if (this.fallDistance > 0.0F) {
            p_19913_.getBlock().fallOn(this.level, p_19913_, p_19914_, this, this.fallDistance);
            if (!p_19913_.is(BlockTags.OCCLUDES_VIBRATION_SIGNALS)) {
               this.gameEvent(GameEvent.HIT_GROUND);
            }
         }

         this.resetFallDistance();
      } else if (p_19911_ < 0.0D) {
         this.fallDistance -= (float)p_19911_;
      }

   }

   public boolean fireImmune() {
      return this.getType().fireImmune();
   }

   public boolean causeFallDamage(float p_146828_, float p_146829_, DamageSource p_146830_) {
      if (this.isVehicle()) {
         for(Entity entity : this.getPassengers()) {
            entity.causeFallDamage(p_146828_, p_146829_, p_146830_);
         }
      }

      return false;
   }

   public boolean isInWater() {
      return this.wasTouchingWater;
   }

   private boolean isInRain() {
      BlockPos blockpos = this.blockPosition();
      return this.level.isRainingAt(blockpos) || this.level.isRainingAt(new BlockPos((double)blockpos.getX(), this.getBoundingBox().maxY, (double)blockpos.getZ()));
   }

   private boolean isInBubbleColumn() {
      return this.level.getBlockState(this.blockPosition()).is(Blocks.BUBBLE_COLUMN);
   }

   public boolean isInWaterOrRain() {
      return this.isInWater() || this.isInRain();
   }

   public boolean isInWaterRainOrBubble() {
      return this.isInWater() || this.isInRain() || this.isInBubbleColumn();
   }

   public boolean isInWaterOrBubble() {
      return this.isInWater() || this.isInBubbleColumn();
   }

   public boolean isUnderWater() {
      return this.wasEyeInWater && this.isInWater();
   }

   public void updateSwimming() {
      if (this.isSwimming()) {
         this.setSwimming(this.isSprinting() && this.isInWater() && !this.isPassenger());
      } else {
         this.setSwimming(this.isSprinting() && this.isUnderWater() && !this.isPassenger() && this.level.getFluidState(this.blockPosition).is(FluidTags.WATER));
      }

   }

   protected boolean updateInWaterStateAndDoFluidPushing() {
      this.fluidHeight.clear();
      this.updateInWaterStateAndDoWaterCurrentPushing();
      double d0 = this.level.dimensionType().ultraWarm() ? 0.007D : 0.0023333333333333335D;
      boolean flag = this.updateFluidHeightAndDoFluidPushing(FluidTags.LAVA, d0);
      return this.isInWater() || flag;
   }

   void updateInWaterStateAndDoWaterCurrentPushing() {
      if (this.getVehicle() instanceof Boat) {
         this.wasTouchingWater = false;
      } else if (this.updateFluidHeightAndDoFluidPushing(FluidTags.WATER, 0.014D)) {
         if (!this.wasTouchingWater && !this.firstTick) {
            this.doWaterSplashEffect();
         }

         this.resetFallDistance();
         this.wasTouchingWater = true;
         this.clearFire();
      } else {
         this.wasTouchingWater = false;
      }

   }

   private void updateFluidOnEyes() {
      this.wasEyeInWater = this.isEyeInFluid(FluidTags.WATER);
      this.fluidOnEyes.clear();
      double d0 = this.getEyeY() - (double)0.11111111F;
      Entity entity = this.getVehicle();
      if (entity instanceof Boat) {
         Boat boat = (Boat)entity;
         if (!boat.isUnderWater() && boat.getBoundingBox().maxY >= d0 && boat.getBoundingBox().minY <= d0) {
            return;
         }
      }

      BlockPos blockpos = new BlockPos(this.getX(), d0, this.getZ());
      FluidState fluidstate = this.level.getFluidState(blockpos);
      double d1 = (double)((float)blockpos.getY() + fluidstate.getHeight(this.level, blockpos));
      if (d1 > d0) {
         fluidstate.getTags().forEach(this.fluidOnEyes::add);
      }

   }

   protected void doWaterSplashEffect() {
      Entity entity = this.isVehicle() && this.getControllingPassenger() != null ? this.getControllingPassenger() : this;
      float f = entity == this ? 0.2F : 0.9F;
      Vec3 vec3 = entity.getDeltaMovement();
      float f1 = Math.min(1.0F, (float)Math.sqrt(vec3.x * vec3.x * (double)0.2F + vec3.y * vec3.y + vec3.z * vec3.z * (double)0.2F) * f);
      if (f1 < 0.25F) {
         this.playSound(this.getSwimSplashSound(), f1, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
      } else {
         this.playSound(this.getSwimHighSpeedSplashSound(), f1, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
      }

      float f2 = (float)Mth.floor(this.getY());

      for(int i = 0; (float)i < 1.0F + this.dimensions.width * 20.0F; ++i) {
         double d0 = (this.random.nextDouble() * 2.0D - 1.0D) * (double)this.dimensions.width;
         double d1 = (this.random.nextDouble() * 2.0D - 1.0D) * (double)this.dimensions.width;
         this.level.addParticle(ParticleTypes.BUBBLE, this.getX() + d0, (double)(f2 + 1.0F), this.getZ() + d1, vec3.x, vec3.y - this.random.nextDouble() * (double)0.2F, vec3.z);
      }

      for(int j = 0; (float)j < 1.0F + this.dimensions.width * 20.0F; ++j) {
         double d2 = (this.random.nextDouble() * 2.0D - 1.0D) * (double)this.dimensions.width;
         double d3 = (this.random.nextDouble() * 2.0D - 1.0D) * (double)this.dimensions.width;
         this.level.addParticle(ParticleTypes.SPLASH, this.getX() + d2, (double)(f2 + 1.0F), this.getZ() + d3, vec3.x, vec3.y, vec3.z);
      }

      this.gameEvent(GameEvent.SPLASH);
   }

   protected BlockState getBlockStateOn() {
      return this.level.getBlockState(this.getOnPos());
   }

   public boolean canSpawnSprintParticle() {
      return this.isSprinting() && !this.isInWater() && !this.isSpectator() && !this.isCrouching() && !this.isInLava() && this.isAlive();
   }

   protected void spawnSprintParticle() {
      int i = Mth.floor(this.getX());
      int j = Mth.floor(this.getY() - (double)0.2F);
      int k = Mth.floor(this.getZ());
      BlockPos blockpos = new BlockPos(i, j, k);
      BlockState blockstate = this.level.getBlockState(blockpos);
      if(!blockstate.addRunningEffects(level, blockpos, this))
      if (blockstate.getRenderShape() != RenderShape.INVISIBLE) {
         Vec3 vec3 = this.getDeltaMovement();
         this.level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockstate).setPos(blockpos), this.getX() + (this.random.nextDouble() - 0.5D) * (double)this.dimensions.width, this.getY() + 0.1D, this.getZ() + (this.random.nextDouble() - 0.5D) * (double)this.dimensions.width, vec3.x * -4.0D, 1.5D, vec3.z * -4.0D);
      }

   }

   public boolean isEyeInFluid(TagKey<Fluid> p_204030_) {
      return this.fluidOnEyes.contains(p_204030_);
   }

   public boolean isInLava() {
      return !this.firstTick && this.fluidHeight.getDouble(FluidTags.LAVA) > 0.0D;
   }

   public void moveRelative(float p_19921_, Vec3 p_19922_) {
      Vec3 vec3 = getInputVector(p_19922_, p_19921_, this.getYRot());
      this.setDeltaMovement(this.getDeltaMovement().add(vec3));
   }

   private static Vec3 getInputVector(Vec3 p_20016_, float p_20017_, float p_20018_) {
      double d0 = p_20016_.lengthSqr();
      if (d0 < 1.0E-7D) {
         return Vec3.ZERO;
      } else {
         Vec3 vec3 = (d0 > 1.0D ? p_20016_.normalize() : p_20016_).scale((double)p_20017_);
         float f = Mth.sin(p_20018_ * ((float)Math.PI / 180F));
         float f1 = Mth.cos(p_20018_ * ((float)Math.PI / 180F));
         return new Vec3(vec3.x * (double)f1 - vec3.z * (double)f, vec3.y, vec3.z * (double)f1 + vec3.x * (double)f);
      }
   }

   public float getBrightness() {
      return this.level.hasChunkAt(this.getBlockX(), this.getBlockZ()) ? this.level.getBrightness(new BlockPos(this.getX(), this.getEyeY(), this.getZ())) : 0.0F;
   }

   public void absMoveTo(double p_19891_, double p_19892_, double p_19893_, float p_19894_, float p_19895_) {
      this.absMoveTo(p_19891_, p_19892_, p_19893_);
      this.setYRot(p_19894_ % 360.0F);
      this.setXRot(Mth.clamp(p_19895_, -90.0F, 90.0F) % 360.0F);
      this.yRotO = this.getYRot();
      this.xRotO = this.getXRot();
   }

   public void absMoveTo(double p_20249_, double p_20250_, double p_20251_) {
      double d0 = Mth.clamp(p_20249_, -3.0E7D, 3.0E7D);
      double d1 = Mth.clamp(p_20251_, -3.0E7D, 3.0E7D);
      this.xo = d0;
      this.yo = p_20250_;
      this.zo = d1;
      this.setPos(d0, p_20250_, d1);
   }

   public void moveTo(Vec3 p_20220_) {
      this.moveTo(p_20220_.x, p_20220_.y, p_20220_.z);
   }

   public void moveTo(double p_20105_, double p_20106_, double p_20107_) {
      this.moveTo(p_20105_, p_20106_, p_20107_, this.getYRot(), this.getXRot());
   }

   public void moveTo(BlockPos p_20036_, float p_20037_, float p_20038_) {
      this.moveTo((double)p_20036_.getX() + 0.5D, (double)p_20036_.getY(), (double)p_20036_.getZ() + 0.5D, p_20037_, p_20038_);
   }

   public void moveTo(double p_20108_, double p_20109_, double p_20110_, float p_20111_, float p_20112_) {
      this.setPosRaw(p_20108_, p_20109_, p_20110_);
      this.setYRot(p_20111_);
      this.setXRot(p_20112_);
      this.setOldPosAndRot();
      this.reapplyPosition();
   }

   public final void setOldPosAndRot() {
      double d0 = this.getX();
      double d1 = this.getY();
      double d2 = this.getZ();
      this.xo = d0;
      this.yo = d1;
      this.zo = d2;
      this.xOld = d0;
      this.yOld = d1;
      this.zOld = d2;
      this.yRotO = this.getYRot();
      this.xRotO = this.getXRot();
   }

   public float distanceTo(Entity p_20271_) {
      float f = (float)(this.getX() - p_20271_.getX());
      float f1 = (float)(this.getY() - p_20271_.getY());
      float f2 = (float)(this.getZ() - p_20271_.getZ());
      return Mth.sqrt(f * f + f1 * f1 + f2 * f2);
   }

   public double distanceToSqr(double p_20276_, double p_20277_, double p_20278_) {
      double d0 = this.getX() - p_20276_;
      double d1 = this.getY() - p_20277_;
      double d2 = this.getZ() - p_20278_;
      return d0 * d0 + d1 * d1 + d2 * d2;
   }

   public double distanceToSqr(Entity p_20281_) {
      return this.distanceToSqr(p_20281_.position());
   }

   public double distanceToSqr(Vec3 p_20239_) {
      double d0 = this.getX() - p_20239_.x;
      double d1 = this.getY() - p_20239_.y;
      double d2 = this.getZ() - p_20239_.z;
      return d0 * d0 + d1 * d1 + d2 * d2;
   }

   public void playerTouch(Player p_20081_) {
   }

   public void push(Entity p_20293_) {
      if (!this.isPassengerOfSameVehicle(p_20293_)) {
         if (!p_20293_.noPhysics && !this.noPhysics) {
            double d0 = p_20293_.getX() - this.getX();
            double d1 = p_20293_.getZ() - this.getZ();
            double d2 = Mth.absMax(d0, d1);
            if (d2 >= (double)0.01F) {
               d2 = Math.sqrt(d2);
               d0 /= d2;
               d1 /= d2;
               double d3 = 1.0D / d2;
               if (d3 > 1.0D) {
                  d3 = 1.0D;
               }

               d0 *= d3;
               d1 *= d3;
               d0 *= (double)0.05F;
               d1 *= (double)0.05F;
               if (!this.isVehicle()) {
                  this.push(-d0, 0.0D, -d1);
               }

               if (!p_20293_.isVehicle()) {
                  p_20293_.push(d0, 0.0D, d1);
               }
            }

         }
      }
   }

   public void push(double p_20286_, double p_20287_, double p_20288_) {
      this.setDeltaMovement(this.getDeltaMovement().add(p_20286_, p_20287_, p_20288_));
      this.hasImpulse = true;
   }

   protected void markHurt() {
      this.hurtMarked = true;
   }

   public boolean hurt(DamageSource p_19946_, float p_19947_) {
      if (this.isInvulnerableTo(p_19946_)) {
         return false;
      } else {
         this.markHurt();
         return false;
      }
   }

   public final Vec3 getViewVector(float p_20253_) {
      return this.calculateViewVector(this.getViewXRot(p_20253_), this.getViewYRot(p_20253_));
   }

   public float getViewXRot(float p_20268_) {
      return p_20268_ == 1.0F ? this.getXRot() : Mth.lerp(p_20268_, this.xRotO, this.getXRot());
   }

   public float getViewYRot(float p_20279_) {
      return p_20279_ == 1.0F ? this.getYRot() : Mth.lerp(p_20279_, this.yRotO, this.getYRot());
   }

   protected final Vec3 calculateViewVector(float p_20172_, float p_20173_) {
      float f = p_20172_ * ((float)Math.PI / 180F);
      float f1 = -p_20173_ * ((float)Math.PI / 180F);
      float f2 = Mth.cos(f1);
      float f3 = Mth.sin(f1);
      float f4 = Mth.cos(f);
      float f5 = Mth.sin(f);
      return new Vec3((double)(f3 * f4), (double)(-f5), (double)(f2 * f4));
   }

   public final Vec3 getUpVector(float p_20290_) {
      return this.calculateUpVector(this.getViewXRot(p_20290_), this.getViewYRot(p_20290_));
   }

   protected final Vec3 calculateUpVector(float p_20215_, float p_20216_) {
      return this.calculateViewVector(p_20215_ - 90.0F, p_20216_);
   }

   public final Vec3 getEyePosition() {
      return new Vec3(this.getX(), this.getEyeY(), this.getZ());
   }

   public final Vec3 getEyePosition(float p_20300_) {
      double d0 = Mth.lerp((double)p_20300_, this.xo, this.getX());
      double d1 = Mth.lerp((double)p_20300_, this.yo, this.getY()) + (double)this.getEyeHeight();
      double d2 = Mth.lerp((double)p_20300_, this.zo, this.getZ());
      return new Vec3(d0, d1, d2);
   }

   public Vec3 getLightProbePosition(float p_20309_) {
      return this.getEyePosition(p_20309_);
   }

   public final Vec3 getPosition(float p_20319_) {
      double d0 = Mth.lerp((double)p_20319_, this.xo, this.getX());
      double d1 = Mth.lerp((double)p_20319_, this.yo, this.getY());
      double d2 = Mth.lerp((double)p_20319_, this.zo, this.getZ());
      return new Vec3(d0, d1, d2);
   }

   public HitResult pick(double p_19908_, float p_19909_, boolean p_19910_) {
      Vec3 vec3 = this.getEyePosition(p_19909_);
      Vec3 vec31 = this.getViewVector(p_19909_);
      Vec3 vec32 = vec3.add(vec31.x * p_19908_, vec31.y * p_19908_, vec31.z * p_19908_);
      return this.level.clip(new ClipContext(vec3, vec32, ClipContext.Block.OUTLINE, p_19910_ ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE, this));
   }

   public boolean isPickable() {
      return false;
   }

   public boolean isPushable() {
      return false;
   }

   public void awardKillScore(Entity p_19953_, int p_19954_, DamageSource p_19955_) {
      if (p_19953_ instanceof ServerPlayer) {
         CriteriaTriggers.ENTITY_KILLED_PLAYER.trigger((ServerPlayer)p_19953_, this, p_19955_);
      }

   }

   public boolean shouldRender(double p_20296_, double p_20297_, double p_20298_) {
      double d0 = this.getX() - p_20296_;
      double d1 = this.getY() - p_20297_;
      double d2 = this.getZ() - p_20298_;
      double d3 = d0 * d0 + d1 * d1 + d2 * d2;
      return this.shouldRenderAtSqrDistance(d3);
   }

   public boolean shouldRenderAtSqrDistance(double p_19883_) {
      double d0 = this.getBoundingBox().getSize();
      if (Double.isNaN(d0)) {
         d0 = 1.0D;
      }

      d0 *= 64.0D * viewScale;
      return p_19883_ < d0 * d0;
   }

   public boolean saveAsPassenger(CompoundTag p_20087_) {
      if (this.removalReason != null && !this.removalReason.shouldSave()) {
         return false;
      } else {
         String s = this.getEncodeId();
         if (s == null) {
            return false;
         } else {
            p_20087_.putString("id", s);
            this.saveWithoutId(p_20087_);
            return true;
         }
      }
   }

   public boolean save(CompoundTag p_20224_) {
      return this.isPassenger() ? false : this.saveAsPassenger(p_20224_);
   }

   public CompoundTag saveWithoutId(CompoundTag p_20241_) {
      try {
         if (this.vehicle != null) {
            p_20241_.put("Pos", this.newDoubleList(this.vehicle.getX(), this.getY(), this.vehicle.getZ()));
         } else {
            p_20241_.put("Pos", this.newDoubleList(this.getX(), this.getY(), this.getZ()));
         }

         Vec3 vec3 = this.getDeltaMovement();
         p_20241_.put("Motion", this.newDoubleList(vec3.x, vec3.y, vec3.z));
         p_20241_.put("Rotation", this.newFloatList(this.getYRot(), this.getXRot()));
         p_20241_.putFloat("FallDistance", this.fallDistance);
         p_20241_.putShort("Fire", (short)this.remainingFireTicks);
         p_20241_.putShort("Air", (short)this.getAirSupply());
         p_20241_.putBoolean("OnGround", this.onGround);
         p_20241_.putBoolean("Invulnerable", this.invulnerable);
         p_20241_.putInt("PortalCooldown", this.portalCooldown);
         p_20241_.putUUID("UUID", this.getUUID());
         Component component = this.getCustomName();
         if (component != null) {
            p_20241_.putString("CustomName", Component.Serializer.toJson(component));
         }

         if (this.isCustomNameVisible()) {
            p_20241_.putBoolean("CustomNameVisible", this.isCustomNameVisible());
         }

         if (this.isSilent()) {
            p_20241_.putBoolean("Silent", this.isSilent());
         }

         if (this.isNoGravity()) {
            p_20241_.putBoolean("NoGravity", this.isNoGravity());
         }

         if (this.hasGlowingTag) {
            p_20241_.putBoolean("Glowing", true);
         }

         int i = this.getTicksFrozen();
         if (i > 0) {
            p_20241_.putInt("TicksFrozen", this.getTicksFrozen());
         }

         if (this.hasVisualFire) {
            p_20241_.putBoolean("HasVisualFire", this.hasVisualFire);
         }
         p_20241_.putBoolean("CanUpdate", canUpdate);

         if (!this.tags.isEmpty()) {
            ListTag listtag = new ListTag();

            for(String s : this.tags) {
               listtag.add(StringTag.valueOf(s));
            }

            p_20241_.put("Tags", listtag);
         }

         CompoundTag caps = serializeCaps();
         if (caps != null) p_20241_.put("ForgeCaps", caps);
         if (persistentData != null) p_20241_.put("ForgeData", persistentData.copy());

         this.addAdditionalSaveData(p_20241_);
         if (this.isVehicle()) {
            ListTag listtag1 = new ListTag();

            for(Entity entity : this.getPassengers()) {
               CompoundTag compoundtag = new CompoundTag();
               if (entity.saveAsPassenger(compoundtag)) {
                  listtag1.add(compoundtag);
               }
            }

            if (!listtag1.isEmpty()) {
               p_20241_.put("Passengers", listtag1);
            }
         }

         return p_20241_;
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.forThrowable(throwable, "Saving entity NBT");
         CrashReportCategory crashreportcategory = crashreport.addCategory("Entity being saved");
         this.fillCrashReportCategory(crashreportcategory);
         throw new ReportedException(crashreport);
      }
   }

   public void load(CompoundTag p_20259_) {
      try {
         ListTag listtag = p_20259_.getList("Pos", 6);
         ListTag listtag1 = p_20259_.getList("Motion", 6);
         ListTag listtag2 = p_20259_.getList("Rotation", 5);
         double d0 = listtag1.getDouble(0);
         double d1 = listtag1.getDouble(1);
         double d2 = listtag1.getDouble(2);
         this.setDeltaMovement(Math.abs(d0) > 10.0D ? 0.0D : d0, Math.abs(d1) > 10.0D ? 0.0D : d1, Math.abs(d2) > 10.0D ? 0.0D : d2);
         this.setPosRaw(listtag.getDouble(0), Mth.clamp(listtag.getDouble(1), -2.0E7D, 2.0E7D), listtag.getDouble(2));
         this.setYRot(listtag2.getFloat(0));
         this.setXRot(listtag2.getFloat(1));
         this.setOldPosAndRot();
         this.setYHeadRot(this.getYRot());
         this.setYBodyRot(this.getYRot());
         this.fallDistance = p_20259_.getFloat("FallDistance");
         this.remainingFireTicks = p_20259_.getShort("Fire");
         if (p_20259_.contains("Air")) {
            this.setAirSupply(p_20259_.getShort("Air"));
         }

         this.onGround = p_20259_.getBoolean("OnGround");
         this.invulnerable = p_20259_.getBoolean("Invulnerable");
         this.portalCooldown = p_20259_.getInt("PortalCooldown");
         if (p_20259_.hasUUID("UUID")) {
            this.uuid = p_20259_.getUUID("UUID");
            this.stringUUID = this.uuid.toString();
         }

         if (Double.isFinite(this.getX()) && Double.isFinite(this.getY()) && Double.isFinite(this.getZ())) {
            if (Double.isFinite((double)this.getYRot()) && Double.isFinite((double)this.getXRot())) {
               this.reapplyPosition();
               this.setRot(this.getYRot(), this.getXRot());
               if (p_20259_.contains("CustomName", 8)) {
                  String s = p_20259_.getString("CustomName");

                  try {
                     this.setCustomName(Component.Serializer.fromJson(s));
                  } catch (Exception exception) {
                     LOGGER.warn("Failed to parse entity custom name {}", s, exception);
                  }
               }

               this.setCustomNameVisible(p_20259_.getBoolean("CustomNameVisible"));
               this.setSilent(p_20259_.getBoolean("Silent"));
               this.setNoGravity(p_20259_.getBoolean("NoGravity"));
               this.setGlowingTag(p_20259_.getBoolean("Glowing"));
               this.setTicksFrozen(p_20259_.getInt("TicksFrozen"));
               this.hasVisualFire = p_20259_.getBoolean("HasVisualFire");
               if (p_20259_.contains("ForgeData", 10)) persistentData = p_20259_.getCompound("ForgeData");
               if (p_20259_.contains("CanUpdate", 99)) this.canUpdate(p_20259_.getBoolean("CanUpdate"));
               if (p_20259_.contains("ForgeCaps", 10)) deserializeCaps(p_20259_.getCompound("ForgeCaps"));
               if (p_20259_.contains("Tags", 9)) {
                  this.tags.clear();
                  ListTag listtag3 = p_20259_.getList("Tags", 8);
                  int i = Math.min(listtag3.size(), 1024);

                  for(int j = 0; j < i; ++j) {
                     this.tags.add(listtag3.getString(j));
                  }
               }

               this.readAdditionalSaveData(p_20259_);
               if (this.repositionEntityAfterLoad()) {
                  this.reapplyPosition();
               }

            } else {
               throw new IllegalStateException("Entity has invalid rotation");
            }
         } else {
            throw new IllegalStateException("Entity has invalid position");
         }
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.forThrowable(throwable, "Loading entity NBT");
         CrashReportCategory crashreportcategory = crashreport.addCategory("Entity being loaded");
         this.fillCrashReportCategory(crashreportcategory);
         throw new ReportedException(crashreport);
      }
   }

   protected boolean repositionEntityAfterLoad() {
      return true;
   }

   @Nullable
   public final String getEncodeId() {
      EntityType<?> entitytype = this.getType();
      ResourceLocation resourcelocation = EntityType.getKey(entitytype);
      return entitytype.canSerialize() && resourcelocation != null ? resourcelocation.toString() : null;
   }

   protected abstract void readAdditionalSaveData(CompoundTag p_20052_);

   protected abstract void addAdditionalSaveData(CompoundTag p_20139_);

   protected ListTag newDoubleList(double... p_20064_) {
      ListTag listtag = new ListTag();

      for(double d0 : p_20064_) {
         listtag.add(DoubleTag.valueOf(d0));
      }

      return listtag;
   }

   protected ListTag newFloatList(float... p_20066_) {
      ListTag listtag = new ListTag();

      for(float f : p_20066_) {
         listtag.add(FloatTag.valueOf(f));
      }

      return listtag;
   }

   @Nullable
   public ItemEntity spawnAtLocation(ItemLike p_19999_) {
      return this.spawnAtLocation(p_19999_, 0);
   }

   @Nullable
   public ItemEntity spawnAtLocation(ItemLike p_20001_, int p_20002_) {
      return this.spawnAtLocation(new ItemStack(p_20001_), (float)p_20002_);
   }

   @Nullable
   public ItemEntity spawnAtLocation(ItemStack p_19984_) {
      return this.spawnAtLocation(p_19984_, 0.0F);
   }

   @Nullable
   public ItemEntity spawnAtLocation(ItemStack p_19985_, float p_19986_) {
      if (p_19985_.isEmpty()) {
         return null;
      } else if (this.level.isClientSide) {
         return null;
      } else {
         ItemEntity itementity = new ItemEntity(this.level, this.getX(), this.getY() + (double)p_19986_, this.getZ(), p_19985_);
         itementity.setDefaultPickUpDelay();
         if (captureDrops() != null) captureDrops().add(itementity);
         else
         this.level.addFreshEntity(itementity);
         return itementity;
      }
   }

   public boolean isAlive() {
      return !this.isRemoved();
   }

   public boolean isInWall() {
      if (this.noPhysics) {
         return false;
      } else {
         float f = this.dimensions.width * 0.8F;
         AABB aabb = AABB.ofSize(this.getEyePosition(), (double)f, 1.0E-6D, (double)f);
         return BlockPos.betweenClosedStream(aabb).anyMatch((p_201942_) -> {
            BlockState blockstate = this.level.getBlockState(p_201942_);
            return !blockstate.isAir() && blockstate.isSuffocating(this.level, p_201942_) && Shapes.joinIsNotEmpty(blockstate.getCollisionShape(this.level, p_201942_).move((double)p_201942_.getX(), (double)p_201942_.getY(), (double)p_201942_.getZ()), Shapes.create(aabb), BooleanOp.AND);
         });
      }
   }

   public InteractionResult interact(Player p_19978_, InteractionHand p_19979_) {
      return InteractionResult.PASS;
   }

   public boolean canCollideWith(Entity p_20303_) {
      return p_20303_.canBeCollidedWith() && !this.isPassengerOfSameVehicle(p_20303_);
   }

   public boolean canBeCollidedWith() {
      return false;
   }

   public void rideTick() {
      this.setDeltaMovement(Vec3.ZERO);
      if (canUpdate())
      this.tick();
      if (this.isPassenger()) {
         this.getVehicle().positionRider(this);
      }
   }

   public void positionRider(Entity p_20312_) {
      this.positionRider(p_20312_, Entity::setPos);
   }

   private void positionRider(Entity p_19957_, Entity.MoveFunction p_19958_) {
      if (this.hasPassenger(p_19957_)) {
         double d0 = this.getY() + this.getPassengersRidingOffset() + p_19957_.getMyRidingOffset();
         p_19958_.accept(p_19957_, this.getX(), d0, this.getZ());
      }
   }

   public void onPassengerTurned(Entity p_20320_) {
   }

   public double getMyRidingOffset() {
      return 0.0D;
   }

   public double getPassengersRidingOffset() {
      return (double)this.dimensions.height * 0.75D;
   }

   public boolean startRiding(Entity p_20330_) {
      return this.startRiding(p_20330_, false);
   }

   public boolean showVehicleHealth() {
      return this instanceof LivingEntity;
   }

   public boolean startRiding(Entity p_19966_, boolean p_19967_) {
      if (p_19966_ == this.vehicle) {
         return false;
      } else {
         for(Entity entity = p_19966_; entity.vehicle != null; entity = entity.vehicle) {
            if (entity.vehicle == this) {
               return false;
            }
         }

      if (!net.minecraftforge.event.ForgeEventFactory.canMountEntity(this, p_19966_, true)) return false;
         if (p_19967_ || this.canRide(p_19966_) && p_19966_.canAddPassenger(this)) {
            if (this.isPassenger()) {
               this.stopRiding();
            }

            this.setPose(Pose.STANDING);
            this.vehicle = p_19966_;
            this.vehicle.addPassenger(this);
            p_19966_.getIndirectPassengersStream().filter((p_185984_) -> {
               return p_185984_ instanceof ServerPlayer;
            }).forEach((p_185982_) -> {
               CriteriaTriggers.START_RIDING_TRIGGER.trigger((ServerPlayer)p_185982_);
            });
            return true;
         } else {
            return false;
         }
      }
   }

   protected boolean canRide(Entity p_20339_) {
      return !this.isShiftKeyDown() && this.boardingCooldown <= 0;
   }

   protected boolean canEnterPose(Pose p_20176_) {
      return this.level.noCollision(this, this.getBoundingBoxForPose(p_20176_).deflate(1.0E-7D));
   }

   public void ejectPassengers() {
      for(int i = this.passengers.size() - 1; i >= 0; --i) {
         this.passengers.get(i).stopRiding();
      }

   }

   public void removeVehicle() {
      if (this.vehicle != null) {
         Entity entity = this.vehicle;
         if (!net.minecraftforge.event.ForgeEventFactory.canMountEntity(this, entity, false)) return;
         this.vehicle = null;
         entity.removePassenger(this);
      }

   }

   public void stopRiding() {
      this.removeVehicle();
   }

   protected void addPassenger(Entity p_20349_) {
      if (p_20349_.getVehicle() != this) {
         throw new IllegalStateException("Use x.startRiding(y), not y.addPassenger(x)");
      } else {
         if (this.passengers.isEmpty()) {
            this.passengers = ImmutableList.of(p_20349_);
         } else {
            List<Entity> list = Lists.newArrayList(this.passengers);
            if (!this.level.isClientSide && p_20349_ instanceof Player && !(this.getControllingPassenger() instanceof Player)) {
               list.add(0, p_20349_);
            } else {
               list.add(p_20349_);
            }

            this.passengers = ImmutableList.copyOf(list);
         }

      }
   }

   protected void removePassenger(Entity p_20352_) {
      if (p_20352_.getVehicle() == this) {
         throw new IllegalStateException("Use x.stopRiding(y), not y.removePassenger(x)");
      } else {
         if (this.passengers.size() == 1 && this.passengers.get(0) == p_20352_) {
            this.passengers = ImmutableList.of();
         } else {
            this.passengers = this.passengers.stream().filter((p_185980_) -> {
               return p_185980_ != p_20352_;
            }).collect(ImmutableList.toImmutableList());
         }

         p_20352_.boardingCooldown = 60;
      }
   }

   protected boolean canAddPassenger(Entity p_20354_) {
      return this.passengers.isEmpty();
   }

   public void lerpTo(double p_19896_, double p_19897_, double p_19898_, float p_19899_, float p_19900_, int p_19901_, boolean p_19902_) {
      this.setPos(p_19896_, p_19897_, p_19898_);
      this.setRot(p_19899_, p_19900_);
   }

   public void lerpHeadTo(float p_19918_, int p_19919_) {
      this.setYHeadRot(p_19918_);
   }

   public float getPickRadius() {
      return 0.0F;
   }

   public Vec3 getLookAngle() {
      return this.calculateViewVector(this.getXRot(), this.getYRot());
   }

   public Vec3 getHandHoldingItemAngle(Item p_204035_) {
      if (!(this instanceof Player)) {
         return Vec3.ZERO;
      } else {
         Player player = (Player)this;
         boolean flag = player.getOffhandItem().is(p_204035_) && !player.getMainHandItem().is(p_204035_);
         HumanoidArm humanoidarm = flag ? player.getMainArm().getOpposite() : player.getMainArm();
         return this.calculateViewVector(0.0F, this.getYRot() + (float)(humanoidarm == HumanoidArm.RIGHT ? 80 : -80)).scale(0.5D);
      }
   }

   public Vec2 getRotationVector() {
      return new Vec2(this.getXRot(), this.getYRot());
   }

   public Vec3 getForward() {
      return Vec3.directionFromRotation(this.getRotationVector());
   }

   public void handleInsidePortal(BlockPos p_20222_) {
      if (this.isOnPortalCooldown()) {
         this.setPortalCooldown();
      } else {
         if (!this.level.isClientSide && !p_20222_.equals(this.portalEntrancePos)) {
            this.portalEntrancePos = p_20222_.immutable();
         }

         this.isInsidePortal = true;
      }
   }

   protected void handleNetherPortal() {
      if (this.level instanceof ServerLevel) {
         int i = this.getPortalWaitTime();
         ServerLevel serverlevel = (ServerLevel)this.level;
         if (this.isInsidePortal) {
            MinecraftServer minecraftserver = serverlevel.getServer();
            ResourceKey<Level> resourcekey = this.level.dimension() == Level.NETHER ? Level.OVERWORLD : Level.NETHER;
            ServerLevel serverlevel1 = minecraftserver.getLevel(resourcekey);
            if (serverlevel1 != null && minecraftserver.isNetherEnabled() && !this.isPassenger() && this.portalTime++ >= i) {
               this.level.getProfiler().push("portal");
               this.portalTime = i;
               this.setPortalCooldown();
               this.changeDimension(serverlevel1);
               this.level.getProfiler().pop();
            }

            this.isInsidePortal = false;
         } else {
            if (this.portalTime > 0) {
               this.portalTime -= 4;
            }

            if (this.portalTime < 0) {
               this.portalTime = 0;
            }
         }

         this.processPortalCooldown();
      }
   }

   public int getDimensionChangingDelay() {
      return 300;
   }

   public void lerpMotion(double p_20306_, double p_20307_, double p_20308_) {
      this.setDeltaMovement(p_20306_, p_20307_, p_20308_);
   }

   public void handleEntityEvent(byte p_19882_) {
      switch(p_19882_) {
      case 53:
         HoneyBlock.showSlideParticles(this);
      default:
      }
   }

   public void animateHurt() {
   }

   public Iterable<ItemStack> getHandSlots() {
      return EMPTY_LIST;
   }

   public Iterable<ItemStack> getArmorSlots() {
      return EMPTY_LIST;
   }

   public Iterable<ItemStack> getAllSlots() {
      return Iterables.concat(this.getHandSlots(), this.getArmorSlots());
   }

   public void setItemSlot(EquipmentSlot p_19968_, ItemStack p_19969_) {
   }

   public boolean isOnFire() {
      boolean flag = this.level != null && this.level.isClientSide;
      return !this.fireImmune() && (this.remainingFireTicks > 0 || flag && this.getSharedFlag(0));
   }

   public boolean isPassenger() {
      return this.getVehicle() != null;
   }

   public boolean isVehicle() {
      return !this.passengers.isEmpty();
   }

   @Deprecated //Forge: Use rider sensitive version
   public boolean rideableUnderWater() {
      return true;
   }

   public void setShiftKeyDown(boolean p_20261_) {
      this.setSharedFlag(1, p_20261_);
   }

   public boolean isShiftKeyDown() {
      return this.getSharedFlag(1);
   }

   public boolean isSteppingCarefully() {
      return this.isShiftKeyDown();
   }

   public boolean isSuppressingBounce() {
      return this.isShiftKeyDown();
   }

   public boolean isDiscrete() {
      return this.isShiftKeyDown();
   }

   public boolean isDescending() {
      return this.isShiftKeyDown();
   }

   public boolean isCrouching() {
      return this.getPose() == Pose.CROUCHING;
   }

   public boolean isSprinting() {
      return this.getSharedFlag(3);
   }

   public void setSprinting(boolean p_20274_) {
      this.setSharedFlag(3, p_20274_);
   }

   public boolean isSwimming() {
      return this.getSharedFlag(4);
   }

   public boolean isVisuallySwimming() {
      return this.getPose() == Pose.SWIMMING;
   }

   public boolean isVisuallyCrawling() {
      return this.isVisuallySwimming() && !this.isInWater();
   }

   public void setSwimming(boolean p_20283_) {
      this.setSharedFlag(4, p_20283_);
   }

   public final boolean hasGlowingTag() {
      return this.hasGlowingTag;
   }

   public final void setGlowingTag(boolean p_146916_) {
      this.hasGlowingTag = p_146916_;
      this.setSharedFlag(6, this.isCurrentlyGlowing());
   }

   public boolean isCurrentlyGlowing() {
      return this.level.isClientSide() ? this.getSharedFlag(6) : this.hasGlowingTag;
   }

   public boolean isInvisible() {
      return this.getSharedFlag(5);
   }

   public boolean isInvisibleTo(Player p_20178_) {
      if (p_20178_.isSpectator()) {
         return false;
      } else {
         Team team = this.getTeam();
         return team != null && p_20178_ != null && p_20178_.getTeam() == team && team.canSeeFriendlyInvisibles() ? false : this.isInvisible();
      }
   }

   @Nullable
   public GameEventListenerRegistrar getGameEventListenerRegistrar() {
      return null;
   }

   @Nullable
   public Team getTeam() {
      return this.level.getScoreboard().getPlayersTeam(this.getScoreboardName());
   }

   public boolean isAlliedTo(Entity p_20355_) {
      return this.isAlliedTo(p_20355_.getTeam());
   }

   public boolean isAlliedTo(Team p_20032_) {
      return this.getTeam() != null ? this.getTeam().isAlliedTo(p_20032_) : false;
   }

   public void setInvisible(boolean p_20304_) {
      this.setSharedFlag(5, p_20304_);
   }

   protected boolean getSharedFlag(int p_20292_) {
      return (this.entityData.get(DATA_SHARED_FLAGS_ID) & 1 << p_20292_) != 0;
   }

   protected void setSharedFlag(int p_20116_, boolean p_20117_) {
      byte b0 = this.entityData.get(DATA_SHARED_FLAGS_ID);
      if (p_20117_) {
         this.entityData.set(DATA_SHARED_FLAGS_ID, (byte)(b0 | 1 << p_20116_));
      } else {
         this.entityData.set(DATA_SHARED_FLAGS_ID, (byte)(b0 & ~(1 << p_20116_)));
      }

   }

   public int getMaxAirSupply() {
      return 300;
   }

   public int getAirSupply() {
      return this.entityData.get(DATA_AIR_SUPPLY_ID);
   }

   public void setAirSupply(int p_20302_) {
      this.entityData.set(DATA_AIR_SUPPLY_ID, p_20302_);
   }

   public int getTicksFrozen() {
      return this.entityData.get(DATA_TICKS_FROZEN);
   }

   public void setTicksFrozen(int p_146918_) {
      this.entityData.set(DATA_TICKS_FROZEN, p_146918_);
   }

   public float getPercentFrozen() {
      int i = this.getTicksRequiredToFreeze();
      return (float)Math.min(this.getTicksFrozen(), i) / (float)i;
   }

   public boolean isFullyFrozen() {
      return this.getTicksFrozen() >= this.getTicksRequiredToFreeze();
   }

   public int getTicksRequiredToFreeze() {
      return 140;
   }

   public void thunderHit(ServerLevel p_19927_, LightningBolt p_19928_) {
      this.setRemainingFireTicks(this.remainingFireTicks + 1);
      if (this.remainingFireTicks == 0) {
         this.setSecondsOnFire(8);
      }

      this.hurt(DamageSource.LIGHTNING_BOLT, p_19928_.getDamage());
   }

   public void onAboveBubbleCol(boolean p_20313_) {
      Vec3 vec3 = this.getDeltaMovement();
      double d0;
      if (p_20313_) {
         d0 = Math.max(-0.9D, vec3.y - 0.03D);
      } else {
         d0 = Math.min(1.8D, vec3.y + 0.1D);
      }

      this.setDeltaMovement(vec3.x, d0, vec3.z);
   }

   public void onInsideBubbleColumn(boolean p_20322_) {
      Vec3 vec3 = this.getDeltaMovement();
      double d0;
      if (p_20322_) {
         d0 = Math.max(-0.3D, vec3.y - 0.03D);
      } else {
         d0 = Math.min(0.7D, vec3.y + 0.06D);
      }

      this.setDeltaMovement(vec3.x, d0, vec3.z);
      this.resetFallDistance();
   }

   public void killed(ServerLevel p_19929_, LivingEntity p_19930_) {
   }

   public void resetFallDistance() {
      this.fallDistance = 0.0F;
   }

   protected void moveTowardsClosestSpace(double p_20315_, double p_20316_, double p_20317_) {
      BlockPos blockpos = new BlockPos(p_20315_, p_20316_, p_20317_);
      Vec3 vec3 = new Vec3(p_20315_ - (double)blockpos.getX(), p_20316_ - (double)blockpos.getY(), p_20317_ - (double)blockpos.getZ());
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
      Direction direction = Direction.UP;
      double d0 = Double.MAX_VALUE;

      for(Direction direction1 : new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, Direction.UP}) {
         blockpos$mutableblockpos.setWithOffset(blockpos, direction1);
         if (!this.level.getBlockState(blockpos$mutableblockpos).isCollisionShapeFullBlock(this.level, blockpos$mutableblockpos)) {
            double d1 = vec3.get(direction1.getAxis());
            double d2 = direction1.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1.0D - d1 : d1;
            if (d2 < d0) {
               d0 = d2;
               direction = direction1;
            }
         }
      }

      float f = this.random.nextFloat() * 0.2F + 0.1F;
      float f1 = (float)direction.getAxisDirection().getStep();
      Vec3 vec31 = this.getDeltaMovement().scale(0.75D);
      if (direction.getAxis() == Direction.Axis.X) {
         this.setDeltaMovement((double)(f1 * f), vec31.y, vec31.z);
      } else if (direction.getAxis() == Direction.Axis.Y) {
         this.setDeltaMovement(vec31.x, (double)(f1 * f), vec31.z);
      } else if (direction.getAxis() == Direction.Axis.Z) {
         this.setDeltaMovement(vec31.x, vec31.y, (double)(f1 * f));
      }

   }

   public void makeStuckInBlock(BlockState p_20006_, Vec3 p_20007_) {
      this.resetFallDistance();
      this.stuckSpeedMultiplier = p_20007_;
   }

   private static Component removeAction(Component p_20141_) {
      MutableComponent mutablecomponent = p_20141_.plainCopy().setStyle(p_20141_.getStyle().withClickEvent((ClickEvent)null));

      for(Component component : p_20141_.getSiblings()) {
         mutablecomponent.append(removeAction(component));
      }

      return mutablecomponent;
   }

   public Component getName() {
      Component component = this.getCustomName();
      return component != null ? removeAction(component) : this.getTypeName();
   }

   protected Component getTypeName() {
      return this.getType().getDescription(); // Forge: Use getter to allow overriding by mods
   }

   public boolean is(Entity p_20356_) {
      return this == p_20356_;
   }

   public float getYHeadRot() {
      return 0.0F;
   }

   public void setYHeadRot(float p_20328_) {
   }

   public void setYBodyRot(float p_20338_) {
   }

   public boolean isAttackable() {
      return true;
   }

   public boolean skipAttackInteraction(Entity p_20357_) {
      return false;
   }

   public String toString() {
      String s = this.level == null ? "~NULL~" : this.level.toString();
      return this.removalReason != null ? String.format(Locale.ROOT, "%s['%s'/%d, l='%s', x=%.2f, y=%.2f, z=%.2f, removed=%s]", this.getClass().getSimpleName(), this.getName().getString(), this.id, s, this.getX(), this.getY(), this.getZ(), this.removalReason) : String.format(Locale.ROOT, "%s['%s'/%d, l='%s', x=%.2f, y=%.2f, z=%.2f]", this.getClass().getSimpleName(), this.getName().getString(), this.id, s, this.getX(), this.getY(), this.getZ());
   }

   public boolean isInvulnerableTo(DamageSource p_20122_) {
      return this.isRemoved() || this.invulnerable && p_20122_ != DamageSource.OUT_OF_WORLD && !p_20122_.isCreativePlayer();
   }

   public boolean isInvulnerable() {
      return this.invulnerable;
   }

   public void setInvulnerable(boolean p_20332_) {
      this.invulnerable = p_20332_;
   }

   public void copyPosition(Entity p_20360_) {
      this.moveTo(p_20360_.getX(), p_20360_.getY(), p_20360_.getZ(), p_20360_.getYRot(), p_20360_.getXRot());
   }

   public void restoreFrom(Entity p_20362_) {
      CompoundTag compoundtag = p_20362_.saveWithoutId(new CompoundTag());
      compoundtag.remove("Dimension");
      this.load(compoundtag);
      this.portalCooldown = p_20362_.portalCooldown;
      this.portalEntrancePos = p_20362_.portalEntrancePos;
   }

   @Nullable
   public Entity changeDimension(ServerLevel p_20118_) {
      return this.changeDimension(p_20118_, p_20118_.getPortalForcer());
   }
   @Nullable
   public Entity changeDimension(ServerLevel p_20118_, net.minecraftforge.common.util.ITeleporter teleporter) {
      if (!net.minecraftforge.common.ForgeHooks.onTravelToDimension(this, p_20118_.dimension())) return null;
      if (this.level instanceof ServerLevel && !this.isRemoved()) {
         this.level.getProfiler().push("changeDimension");
         this.unRide();
         this.level.getProfiler().push("reposition");
         PortalInfo portalinfo = teleporter.getPortalInfo(this, p_20118_, this::findDimensionEntryPoint);
         if (portalinfo == null) {
            return null;
         } else {
            Entity transportedEntity = teleporter.placeEntity(this, (ServerLevel) this.level, p_20118_, this.yRot, spawnPortal -> { //Forge: Start vanilla logic
            this.level.getProfiler().popPush("reloading");
            Entity entity = this.getType().create(p_20118_);
            if (entity != null) {
               entity.restoreFrom(this);
               entity.moveTo(portalinfo.pos.x, portalinfo.pos.y, portalinfo.pos.z, portalinfo.yRot, entity.getXRot());
               entity.setDeltaMovement(portalinfo.speed);
               p_20118_.addDuringTeleport(entity);
               if (spawnPortal && p_20118_.dimension() == Level.END) {
                  ServerLevel.makeObsidianPlatform(p_20118_);
               }
            }
            return entity;
            }); //Forge: End vanilla logic

            this.removeAfterChangingDimensions();
            this.level.getProfiler().pop();
            ((ServerLevel)this.level).resetEmptyTime();
            p_20118_.resetEmptyTime();
            this.level.getProfiler().pop();
            return transportedEntity;
         }
      } else {
         return null;
      }
   }

   protected void removeAfterChangingDimensions() {
      this.setRemoved(Entity.RemovalReason.CHANGED_DIMENSION);
   }

   @Nullable
   protected PortalInfo findDimensionEntryPoint(ServerLevel p_19923_) {
      boolean flag = this.level.dimension() == Level.END && p_19923_.dimension() == Level.OVERWORLD;
      boolean flag1 = p_19923_.dimension() == Level.END;
      if (!flag && !flag1) {
         boolean flag2 = p_19923_.dimension() == Level.NETHER;
         if (this.level.dimension() != Level.NETHER && !flag2) {
            return null;
         } else {
            WorldBorder worldborder = p_19923_.getWorldBorder();
            double d0 = DimensionType.getTeleportationScale(this.level.dimensionType(), p_19923_.dimensionType());
            BlockPos blockpos1 = worldborder.clampToBounds(this.getX() * d0, this.getY(), this.getZ() * d0);
            return this.getExitPortal(p_19923_, blockpos1, flag2, worldborder).map((p_185941_) -> {
               BlockState blockstate = this.level.getBlockState(this.portalEntrancePos);
               Direction.Axis direction$axis;
               Vec3 vec3;
               if (blockstate.hasProperty(BlockStateProperties.HORIZONTAL_AXIS)) {
                  direction$axis = blockstate.getValue(BlockStateProperties.HORIZONTAL_AXIS);
                  BlockUtil.FoundRectangle blockutil$foundrectangle = BlockUtil.getLargestRectangleAround(this.portalEntrancePos, direction$axis, 21, Direction.Axis.Y, 21, (p_185959_) -> {
                     return this.level.getBlockState(p_185959_) == blockstate;
                  });
                  vec3 = this.getRelativePortalPosition(direction$axis, blockutil$foundrectangle);
               } else {
                  direction$axis = Direction.Axis.X;
                  vec3 = new Vec3(0.5D, 0.0D, 0.0D);
               }

               return PortalShape.createPortalInfo(p_19923_, p_185941_, direction$axis, vec3, this.getDimensions(this.getPose()), this.getDeltaMovement(), this.getYRot(), this.getXRot());
            }).orElse((PortalInfo)null);
         }
      } else {
         BlockPos blockpos;
         if (flag1) {
            blockpos = ServerLevel.END_SPAWN_POINT;
         } else {
            blockpos = p_19923_.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, p_19923_.getSharedSpawnPos());
         }

         return new PortalInfo(new Vec3((double)blockpos.getX() + 0.5D, (double)blockpos.getY(), (double)blockpos.getZ() + 0.5D), this.getDeltaMovement(), this.getYRot(), this.getXRot());
      }
   }

   protected Vec3 getRelativePortalPosition(Direction.Axis p_20045_, BlockUtil.FoundRectangle p_20046_) {
      return PortalShape.getRelativePosition(p_20046_, p_20045_, this.position(), this.getDimensions(this.getPose()));
   }

   protected Optional<BlockUtil.FoundRectangle> getExitPortal(ServerLevel p_185935_, BlockPos p_185936_, boolean p_185937_, WorldBorder p_185938_) {
      return p_185935_.getPortalForcer().findPortalAround(p_185936_, p_185937_, p_185938_);
   }

   public boolean canChangeDimensions() {
      return true;
   }

   public float getBlockExplosionResistance(Explosion p_19992_, BlockGetter p_19993_, BlockPos p_19994_, BlockState p_19995_, FluidState p_19996_, float p_19997_) {
      return p_19997_;
   }

   public boolean shouldBlockExplode(Explosion p_19987_, BlockGetter p_19988_, BlockPos p_19989_, BlockState p_19990_, float p_19991_) {
      return true;
   }

   public int getMaxFallDistance() {
      return 3;
   }

   public boolean isIgnoringBlockTriggers() {
      return false;
   }

   public void fillCrashReportCategory(CrashReportCategory p_20051_) {
      p_20051_.setDetail("Entity Type", () -> {
         return EntityType.getKey(this.getType()) + " (" + this.getClass().getCanonicalName() + ")";
      });
      p_20051_.setDetail("Entity ID", this.id);
      p_20051_.setDetail("Entity Name", () -> {
         return this.getName().getString();
      });
      p_20051_.setDetail("Entity's Exact location", String.format(Locale.ROOT, "%.2f, %.2f, %.2f", this.getX(), this.getY(), this.getZ()));
      p_20051_.setDetail("Entity's Block location", CrashReportCategory.formatLocation(this.level, Mth.floor(this.getX()), Mth.floor(this.getY()), Mth.floor(this.getZ())));
      Vec3 vec3 = this.getDeltaMovement();
      p_20051_.setDetail("Entity's Momentum", String.format(Locale.ROOT, "%.2f, %.2f, %.2f", vec3.x, vec3.y, vec3.z));
      p_20051_.setDetail("Entity's Passengers", () -> {
         return this.getPassengers().toString();
      });
      p_20051_.setDetail("Entity's Vehicle", () -> {
         return String.valueOf((Object)this.getVehicle());
      });
   }

   public boolean displayFireAnimation() {
      return this.isOnFire() && !this.isSpectator();
   }

   public void setUUID(UUID p_20085_) {
      this.uuid = p_20085_;
      this.stringUUID = this.uuid.toString();
   }

   public UUID getUUID() {
      return this.uuid;
   }

   public String getStringUUID() {
      return this.stringUUID;
   }

   public String getScoreboardName() {
      return this.stringUUID;
   }

   public boolean isPushedByFluid() {
      return true;
   }

   public static double getViewScale() {
      return viewScale;
   }

   public static void setViewScale(double p_20104_) {
      viewScale = p_20104_;
   }

   public Component getDisplayName() {
      return PlayerTeam.formatNameForTeam(this.getTeam(), this.getName()).withStyle((p_185975_) -> {
         return p_185975_.withHoverEvent(this.createHoverEvent()).withInsertion(this.getStringUUID());
      });
   }

   public void setCustomName(@Nullable Component p_20053_) {
      this.entityData.set(DATA_CUSTOM_NAME, Optional.ofNullable(p_20053_));
   }

   @Nullable
   public Component getCustomName() {
      return this.entityData.get(DATA_CUSTOM_NAME).orElse((Component)null);
   }

   public boolean hasCustomName() {
      return this.entityData.get(DATA_CUSTOM_NAME).isPresent();
   }

   public void setCustomNameVisible(boolean p_20341_) {
      this.entityData.set(DATA_CUSTOM_NAME_VISIBLE, p_20341_);
   }

   public boolean isCustomNameVisible() {
      return this.entityData.get(DATA_CUSTOM_NAME_VISIBLE);
   }

   public final void teleportToWithTicket(double p_20325_, double p_20326_, double p_20327_) {
      if (this.level instanceof ServerLevel) {
         ChunkPos chunkpos = new ChunkPos(new BlockPos(p_20325_, p_20326_, p_20327_));
         ((ServerLevel)this.level).getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, chunkpos, 0, this.getId());
         this.level.getChunk(chunkpos.x, chunkpos.z);
         this.teleportTo(p_20325_, p_20326_, p_20327_);
      }
   }

   public void dismountTo(double p_146825_, double p_146826_, double p_146827_) {
      this.teleportTo(p_146825_, p_146826_, p_146827_);
   }

   public void teleportTo(double p_19887_, double p_19888_, double p_19889_) {
      if (this.level instanceof ServerLevel) {
         this.moveTo(p_19887_, p_19888_, p_19889_, this.getYRot(), this.getXRot());
         this.getSelfAndPassengers().forEach((p_185977_) -> {
            for(Entity entity : p_185977_.passengers) {
               p_185977_.positionRider(entity, Entity::moveTo);
            }

         });
      }
   }

   public boolean shouldShowName() {
      return this.isCustomNameVisible();
   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> p_20059_) {
      if (DATA_POSE.equals(p_20059_)) {
         this.refreshDimensions();
      }

   }

   public void refreshDimensions() {
      EntityDimensions entitydimensions = this.dimensions;
      Pose pose = this.getPose();
      EntityDimensions entitydimensions1 = this.getDimensions(pose);
      net.minecraftforge.event.entity.EntityEvent.Size sizeEvent = net.minecraftforge.event.ForgeEventFactory.getEntitySizeForge(this, pose, entitydimensions, entitydimensions1, this.getEyeHeight(pose, entitydimensions1));
      entitydimensions1 = sizeEvent.getNewSize();
      this.dimensions = entitydimensions1;
      this.eyeHeight = sizeEvent.getNewEyeHeight();
      this.reapplyPosition();
      boolean flag = (double)entitydimensions1.width <= 4.0D && (double)entitydimensions1.height <= 4.0D;
      if (!this.level.isClientSide && !this.firstTick && !this.noPhysics && flag && (entitydimensions1.width > entitydimensions.width || entitydimensions1.height > entitydimensions.height) && !(this instanceof Player)) {
         Vec3 vec3 = this.position().add(0.0D, (double)entitydimensions.height / 2.0D, 0.0D);
         double d0 = (double)Math.max(0.0F, entitydimensions1.width - entitydimensions.width) + 1.0E-6D;
         double d1 = (double)Math.max(0.0F, entitydimensions1.height - entitydimensions.height) + 1.0E-6D;
         VoxelShape voxelshape = Shapes.create(AABB.ofSize(vec3, d0, d1, d0));
         EntityDimensions finalEntitydimensions = entitydimensions1;
         this.level.findFreePosition(this, voxelshape, vec3, (double)entitydimensions1.width, (double)entitydimensions1.height, (double)entitydimensions1.width).ifPresent((p_185956_) -> {
            this.setPos(p_185956_.add(0.0D, (double)(-finalEntitydimensions.height) / 2.0D, 0.0D));
         });
      }

   }

   public Direction getDirection() {
      return Direction.fromYRot((double)this.getYRot());
   }

   public Direction getMotionDirection() {
      return this.getDirection();
   }

   protected HoverEvent createHoverEvent() {
      return new HoverEvent(HoverEvent.Action.SHOW_ENTITY, new HoverEvent.EntityTooltipInfo(this.getType(), this.getUUID(), this.getName()));
   }

   public boolean broadcastToPlayer(ServerPlayer p_19937_) {
      return true;
   }

   public final AABB getBoundingBox() {
      return this.bb;
   }

   public AABB getBoundingBoxForCulling() {
      return this.getBoundingBox();
   }

   protected AABB getBoundingBoxForPose(Pose p_20218_) {
      EntityDimensions entitydimensions = this.getDimensions(p_20218_);
      float f = entitydimensions.width / 2.0F;
      Vec3 vec3 = new Vec3(this.getX() - (double)f, this.getY(), this.getZ() - (double)f);
      Vec3 vec31 = new Vec3(this.getX() + (double)f, this.getY() + (double)entitydimensions.height, this.getZ() + (double)f);
      return new AABB(vec3, vec31);
   }

   public final void setBoundingBox(AABB p_20012_) {
      this.bb = p_20012_;
   }

   protected float getEyeHeight(Pose p_19976_, EntityDimensions p_19977_) {
      return p_19977_.height * 0.85F;
   }

   public float getEyeHeight(Pose p_20237_) {
      return this.getEyeHeight(p_20237_, this.getDimensions(p_20237_));
   }

   public final float getEyeHeight() {
      return this.eyeHeight;
   }

   public Vec3 getLeashOffset() {
      return new Vec3(0.0D, (double)this.getEyeHeight(), (double)(this.getBbWidth() * 0.4F));
   }

   public SlotAccess getSlot(int p_146919_) {
      return SlotAccess.NULL;
   }

   public void sendMessage(Component p_20055_, UUID p_20056_) {
   }

   public Level getCommandSenderWorld() {
      return this.level;
   }

   @Nullable
   public MinecraftServer getServer() {
      return this.level.getServer();
   }

   public InteractionResult interactAt(Player p_19980_, Vec3 p_19981_, InteractionHand p_19982_) {
      return InteractionResult.PASS;
   }

   public boolean ignoreExplosion() {
      return false;
   }

   public void doEnchantDamageEffects(LivingEntity p_19971_, Entity p_19972_) {
      if (p_19972_ instanceof LivingEntity) {
         EnchantmentHelper.doPostHurtEffects((LivingEntity)p_19972_, p_19971_);
      }

      EnchantmentHelper.doPostDamageEffects(p_19971_, p_19972_);
   }

   public void startSeenByPlayer(ServerPlayer p_20119_) {
   }

   public void stopSeenByPlayer(ServerPlayer p_20174_) {
   }

   public float rotate(Rotation p_20004_) {
      float f = Mth.wrapDegrees(this.getYRot());
      switch(p_20004_) {
      case CLOCKWISE_180:
         return f + 180.0F;
      case COUNTERCLOCKWISE_90:
         return f + 270.0F;
      case CLOCKWISE_90:
         return f + 90.0F;
      default:
         return f;
      }
   }

   public float mirror(Mirror p_20003_) {
      float f = Mth.wrapDegrees(this.getYRot());
      switch(p_20003_) {
      case FRONT_BACK:
         return -f;
      case LEFT_RIGHT:
         return 180.0F - f;
      default:
         return f;
      }
   }

   public boolean onlyOpCanSetNbt() {
      return false;
   }

   @Nullable
   public Entity getControllingPassenger() {
      return null;
   }

   public final List<Entity> getPassengers() {
      return this.passengers;
   }

   @Nullable
   public Entity getFirstPassenger() {
      return this.passengers.isEmpty() ? null : this.passengers.get(0);
   }

   public boolean hasPassenger(Entity p_20364_) {
      return this.passengers.contains(p_20364_);
   }

   public boolean hasPassenger(Predicate<Entity> p_146863_) {
      for(Entity entity : this.passengers) {
         if (p_146863_.test(entity)) {
            return true;
         }
      }

      return false;
   }

   private Stream<Entity> getIndirectPassengersStream() {
      return this.passengers.stream().flatMap(Entity::getSelfAndPassengers);
   }

   public Stream<Entity> getSelfAndPassengers() {
      return Stream.concat(Stream.of(this), this.getIndirectPassengersStream());
   }

   public Stream<Entity> getPassengersAndSelf() {
      return Stream.concat(this.passengers.stream().flatMap(Entity::getPassengersAndSelf), Stream.of(this));
   }

   public Iterable<Entity> getIndirectPassengers() {
      return () -> {
         return this.getIndirectPassengersStream().iterator();
      };
   }

   public boolean hasExactlyOnePlayerPassenger() {
      return this.getIndirectPassengersStream().filter((p_185943_) -> {
         return p_185943_ instanceof Player;
      }).count() == 1L;
   }

   public Entity getRootVehicle() {
      Entity entity;
      for(entity = this; entity.isPassenger(); entity = entity.getVehicle()) {
      }

      return entity;
   }

   public boolean isPassengerOfSameVehicle(Entity p_20366_) {
      return this.getRootVehicle() == p_20366_.getRootVehicle();
   }

   public boolean hasIndirectPassenger(Entity p_20368_) {
      return this.getIndirectPassengersStream().anyMatch((p_185946_) -> {
         return p_185946_ == p_20368_;
      });
   }

   public boolean isControlledByLocalInstance() {
      Entity entity = this.getControllingPassenger();
      if (entity instanceof Player) {
         return ((Player)entity).isLocalPlayer();
      } else {
         return !this.level.isClientSide;
      }
   }

   protected static Vec3 getCollisionHorizontalEscapeVector(double p_19904_, double p_19905_, float p_19906_) {
      double d0 = (p_19904_ + p_19905_ + (double)1.0E-5F) / 2.0D;
      float f = -Mth.sin(p_19906_ * ((float)Math.PI / 180F));
      float f1 = Mth.cos(p_19906_ * ((float)Math.PI / 180F));
      float f2 = Math.max(Math.abs(f), Math.abs(f1));
      return new Vec3((double)f * d0 / (double)f2, 0.0D, (double)f1 * d0 / (double)f2);
   }

   public Vec3 getDismountLocationForPassenger(LivingEntity p_20123_) {
      return new Vec3(this.getX(), this.getBoundingBox().maxY, this.getZ());
   }

   @Nullable
   public Entity getVehicle() {
      return this.vehicle;
   }

   public PushReaction getPistonPushReaction() {
      return PushReaction.NORMAL;
   }

   public SoundSource getSoundSource() {
      return SoundSource.NEUTRAL;
   }

   protected int getFireImmuneTicks() {
      return 1;
   }

   public CommandSourceStack createCommandSourceStack() {
      return new CommandSourceStack(this, this.position(), this.getRotationVector(), this.level instanceof ServerLevel ? (ServerLevel)this.level : null, this.getPermissionLevel(), this.getName().getString(), this.getDisplayName(), this.level.getServer(), this);
   }

   protected int getPermissionLevel() {
      return 0;
   }

   public boolean hasPermissions(int p_20311_) {
      return this.getPermissionLevel() >= p_20311_;
   }

   public boolean acceptsSuccess() {
      return this.level.getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK);
   }

   public boolean acceptsFailure() {
      return true;
   }

   public boolean shouldInformAdmins() {
      return true;
   }

   public void lookAt(EntityAnchorArgument.Anchor p_20033_, Vec3 p_20034_) {
      Vec3 vec3 = p_20033_.apply(this);
      double d0 = p_20034_.x - vec3.x;
      double d1 = p_20034_.y - vec3.y;
      double d2 = p_20034_.z - vec3.z;
      double d3 = Math.sqrt(d0 * d0 + d2 * d2);
      this.setXRot(Mth.wrapDegrees((float)(-(Mth.atan2(d1, d3) * (double)(180F / (float)Math.PI)))));
      this.setYRot(Mth.wrapDegrees((float)(Mth.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F));
      this.setYHeadRot(this.getYRot());
      this.xRotO = this.getXRot();
      this.yRotO = this.getYRot();
   }

   public boolean updateFluidHeightAndDoFluidPushing(TagKey<Fluid> p_204032_, double p_204033_) {
      if (this.touchingUnloadedChunk()) {
         return false;
      } else {
         AABB aabb = this.getBoundingBox().deflate(0.001D);
         int i = Mth.floor(aabb.minX);
         int j = Mth.ceil(aabb.maxX);
         int k = Mth.floor(aabb.minY);
         int l = Mth.ceil(aabb.maxY);
         int i1 = Mth.floor(aabb.minZ);
         int j1 = Mth.ceil(aabb.maxZ);
         double d0 = 0.0D;
         boolean flag = this.isPushedByFluid();
         boolean flag1 = false;
         Vec3 vec3 = Vec3.ZERO;
         int k1 = 0;
         BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

         for(int l1 = i; l1 < j; ++l1) {
            for(int i2 = k; i2 < l; ++i2) {
               for(int j2 = i1; j2 < j1; ++j2) {
                  blockpos$mutableblockpos.set(l1, i2, j2);
                  FluidState fluidstate = this.level.getFluidState(blockpos$mutableblockpos);
                  if (fluidstate.is(p_204032_)) {
                     double d1 = (double)((float)i2 + fluidstate.getHeight(this.level, blockpos$mutableblockpos));
                     if (d1 >= aabb.minY) {
                        flag1 = true;
                        d0 = Math.max(d1 - aabb.minY, d0);
                        if (flag) {
                           Vec3 vec31 = fluidstate.getFlow(this.level, blockpos$mutableblockpos);
                           if (d0 < 0.4D) {
                              vec31 = vec31.scale(d0);
                           }

                           vec3 = vec3.add(vec31);
                           ++k1;
                        }
                     }
                  }
               }
            }
         }

         if (vec3.length() > 0.0D) {
            if (k1 > 0) {
               vec3 = vec3.scale(1.0D / (double)k1);
            }

            if (!(this instanceof Player)) {
               vec3 = vec3.normalize();
            }

            Vec3 vec32 = this.getDeltaMovement();
            vec3 = vec3.scale(p_204033_ * 1.0D);
            double d2 = 0.003D;
            if (Math.abs(vec32.x) < 0.003D && Math.abs(vec32.z) < 0.003D && vec3.length() < 0.0045000000000000005D) {
               vec3 = vec3.normalize().scale(0.0045000000000000005D);
            }

            this.setDeltaMovement(this.getDeltaMovement().add(vec3));
         }

         this.fluidHeight.put(p_204032_, d0);
         return flag1;
      }
   }

   public boolean touchingUnloadedChunk() {
      AABB aabb = this.getBoundingBox().inflate(1.0D);
      int i = Mth.floor(aabb.minX);
      int j = Mth.ceil(aabb.maxX);
      int k = Mth.floor(aabb.minZ);
      int l = Mth.ceil(aabb.maxZ);
      return !this.level.hasChunksAt(i, k, j, l);
   }

   public double getFluidHeight(TagKey<Fluid> p_204037_) {
      return this.fluidHeight.getDouble(p_204037_);
   }

   public double getFluidJumpThreshold() {
      return (double)this.getEyeHeight() < 0.4D ? 0.0D : 0.4D;
   }

   public final float getBbWidth() {
      return this.dimensions.width;
   }

   public final float getBbHeight() {
      return this.dimensions.height;
   }

   public abstract Packet<?> getAddEntityPacket();

   public EntityDimensions getDimensions(Pose p_19975_) {
      return this.type.getDimensions();
   }

   public Vec3 position() {
      return this.position;
   }

   public BlockPos blockPosition() {
      return this.blockPosition;
   }

   public BlockState getFeetBlockState() {
      if (this.feetBlockState == null) {
         this.feetBlockState = this.level.getBlockState(this.blockPosition());
      }

      return this.feetBlockState;
   }

   public BlockPos eyeBlockPosition() {
      return new BlockPos(this.getEyePosition(1.0F));
   }

   public ChunkPos chunkPosition() {
      return this.chunkPosition;
   }

   public Vec3 getDeltaMovement() {
      return this.deltaMovement;
   }

   public void setDeltaMovement(Vec3 p_20257_) {
      this.deltaMovement = p_20257_;
   }

   public void setDeltaMovement(double p_20335_, double p_20336_, double p_20337_) {
      this.setDeltaMovement(new Vec3(p_20335_, p_20336_, p_20337_));
   }

   public final int getBlockX() {
      return this.blockPosition.getX();
   }

   public final double getX() {
      return this.position.x;
   }

   public double getX(double p_20166_) {
      return this.position.x + (double)this.getBbWidth() * p_20166_;
   }

   public double getRandomX(double p_20209_) {
      return this.getX((2.0D * this.random.nextDouble() - 1.0D) * p_20209_);
   }

   public final int getBlockY() {
      return this.blockPosition.getY();
   }

   public final double getY() {
      return this.position.y;
   }

   public double getY(double p_20228_) {
      return this.position.y + (double)this.getBbHeight() * p_20228_;
   }

   public double getRandomY() {
      return this.getY(this.random.nextDouble());
   }

   public double getEyeY() {
      return this.position.y + (double)this.eyeHeight;
   }

   public final int getBlockZ() {
      return this.blockPosition.getZ();
   }

   public final double getZ() {
      return this.position.z;
   }

   public double getZ(double p_20247_) {
      return this.position.z + (double)this.getBbWidth() * p_20247_;
   }

   public double getRandomZ(double p_20263_) {
      return this.getZ((2.0D * this.random.nextDouble() - 1.0D) * p_20263_);
   }

   public final void setPosRaw(double p_20344_, double p_20345_, double p_20346_) {
      if (this.position.x != p_20344_ || this.position.y != p_20345_ || this.position.z != p_20346_) {
         this.position = new Vec3(p_20344_, p_20345_, p_20346_);
         int i = Mth.floor(p_20344_);
         int j = Mth.floor(p_20345_);
         int k = Mth.floor(p_20346_);
         if (i != this.blockPosition.getX() || j != this.blockPosition.getY() || k != this.blockPosition.getZ()) {
            this.blockPosition = new BlockPos(i, j, k);
            this.feetBlockState = null;
            if (SectionPos.blockToSectionCoord(i) != this.chunkPosition.x || SectionPos.blockToSectionCoord(k) != this.chunkPosition.z) {
               this.chunkPosition = new ChunkPos(this.blockPosition);
            }
         }

         this.levelCallback.onMove();
         GameEventListenerRegistrar gameeventlistenerregistrar = this.getGameEventListenerRegistrar();
         if (gameeventlistenerregistrar != null) {
            gameeventlistenerregistrar.onListenerMove(this.level);
         }
      }
      if (this.isAddedToWorld() && !this.level.isClientSide && !this.isRemoved()) this.level.getChunk((int) Math.floor(p_20344_) >> 4, (int) Math.floor(p_20346_) >> 4); // Forge - ensure target chunk is loaded.

   }

   public void checkDespawn() {
   }

   public Vec3 getRopeHoldPosition(float p_20347_) {
      return this.getPosition(p_20347_).add(0.0D, (double)this.eyeHeight * 0.7D, 0.0D);
   }

   public void recreateFromPacket(ClientboundAddEntityPacket p_146866_) {
      int i = p_146866_.getId();
      double d0 = p_146866_.getX();
      double d1 = p_146866_.getY();
      double d2 = p_146866_.getZ();
      this.setPacketCoordinates(d0, d1, d2);
      this.moveTo(d0, d1, d2);
      this.setXRot((float)(p_146866_.getxRot() * 360) / 256.0F);
      this.setYRot((float)(p_146866_.getyRot() * 360) / 256.0F);
      this.setId(i);
      this.setUUID(p_146866_.getUUID());
   }

   @Nullable
   public ItemStack getPickResult() {
      return null;
   }

   public void setIsInPowderSnow(boolean p_146925_) {
      this.isInPowderSnow = p_146925_;
   }

   public boolean canFreeze() {
      return !this.getType().is(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES);
   }

   public boolean isFreezing() {
      return (this.isInPowderSnow || this.wasInPowderSnow) && this.canFreeze();
   }

   public float getYRot() {
      return this.yRot;
   }

   public void setYRot(float p_146923_) {
      if (!Float.isFinite(p_146923_)) {
         Util.logAndPauseIfInIde("Invalid entity rotation: " + p_146923_ + ", discarding.");
      } else {
         this.yRot = p_146923_;
      }
   }

   public float getXRot() {
      return this.xRot;
   }

   public void setXRot(float p_146927_) {
      if (!Float.isFinite(p_146927_)) {
         Util.logAndPauseIfInIde("Invalid entity rotation: " + p_146927_ + ", discarding.");
      } else {
         this.xRot = p_146927_;
      }
   }

   public final boolean isRemoved() {
      return this.removalReason != null;
   }

   @Nullable
   public Entity.RemovalReason getRemovalReason() {
      return this.removalReason;
   }

   public final void setRemoved(Entity.RemovalReason p_146876_) {
      if (this.removalReason == null) {
         this.removalReason = p_146876_;
      }

      if (this.removalReason.shouldDestroy()) {
         this.stopRiding();
      }

      this.getPassengers().forEach(Entity::stopRiding);
      this.levelCallback.onRemove(p_146876_);
   }

   protected void unsetRemoved() {
      this.removalReason = null;
   }

   public void setLevelCallback(EntityInLevelCallback p_146849_) {
      this.levelCallback = p_146849_;
   }

   public boolean shouldBeSaved() {
      if (this.removalReason != null && !this.removalReason.shouldSave()) {
         return false;
      } else if (this.isPassenger()) {
         return false;
      } else {
         return !this.isVehicle() || !this.hasExactlyOnePlayerPassenger();
      }
   }

   public boolean isAlwaysTicking() {
      return false;
   }

   public boolean mayInteract(Level p_146843_, BlockPos p_146844_) {
      return true;
   }

   /* ================================== Forge Start =====================================*/

   private boolean canUpdate = true;
   @Override
   public void canUpdate(boolean value) {
      this.canUpdate = value;
   }
   @Override
   public boolean canUpdate() {
      return this.canUpdate;
   }
   private java.util.Collection<ItemEntity> captureDrops = null;
   @Override
   public java.util.Collection<ItemEntity> captureDrops() {
      return captureDrops;
   }
   @Override
   public java.util.Collection<ItemEntity> captureDrops(java.util.Collection<ItemEntity> value) {
      java.util.Collection<ItemEntity> ret = captureDrops;
      this.captureDrops = value;
      return ret;
   }
   private CompoundTag persistentData;
   @Override
   public CompoundTag getPersistentData() {
      if (persistentData == null)
         persistentData = new CompoundTag();
      return persistentData;
   }
   @Override
   public boolean canTrample(BlockState state, BlockPos pos, float fallDistance) {
      return level.random.nextFloat() < fallDistance - 0.5F
          && this instanceof LivingEntity
          && (this instanceof Player || net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(level, this))
          && this.getBbWidth() * this.getBbWidth() * this.getBbHeight() > 0.512F;
   }

   /**
    * Internal use for keeping track of entities that are tracked by a world, to
    * allow guarantees that entity position changes will force a chunk load, avoiding
    * potential issues with entity desyncing and bad chunk data.
    */
   private boolean isAddedToWorld;

   @Override
   public final boolean isAddedToWorld() { return this.isAddedToWorld; }

   @Override
   public void onAddedToWorld() { this.isAddedToWorld = true; }

   @Override
   public void onRemovedFromWorld() { this.isAddedToWorld = false; }

   @Override
   public void revive() {
      this.unsetRemoved();
      this.reviveCaps();
   }

   // no AT because of overrides
   /**
    * Accessor method for {@link #getEyeHeight(Pose, EntityDimensions)}
    */
   public float getEyeHeightAccess(Pose pose, EntityDimensions size) {
      return this.getEyeHeight(pose, size);
   }

   /* ================================== Forge End =====================================*/


   public Level getLevel() {
      return this.level;
   }

   @FunctionalInterface
   public interface MoveFunction {
      void accept(Entity p_20373_, double p_20374_, double p_20375_, double p_20376_);
   }

   public static enum MovementEmission {
      NONE(false, false),
      SOUNDS(true, false),
      EVENTS(false, true),
      ALL(true, true);

      final boolean sounds;
      final boolean events;

      private MovementEmission(boolean p_146942_, boolean p_146943_) {
         this.sounds = p_146942_;
         this.events = p_146943_;
      }

      public boolean emitsAnything() {
         return this.events || this.sounds;
      }

      public boolean emitsEvents() {
         return this.events;
      }

      public boolean emitsSounds() {
         return this.sounds;
      }
   }

   public static enum RemovalReason {
      KILLED(true, false),
      DISCARDED(true, false),
      UNLOADED_TO_CHUNK(false, true),
      UNLOADED_WITH_PLAYER(false, false),
      CHANGED_DIMENSION(false, false);

      private final boolean destroy;
      private final boolean save;

      private RemovalReason(boolean p_146963_, boolean p_146964_) {
         this.destroy = p_146963_;
         this.save = p_146964_;
      }

      public boolean shouldDestroy() {
         return this.destroy;
      }

      public boolean shouldSave() {
         return this.save;
      }
   }
}
