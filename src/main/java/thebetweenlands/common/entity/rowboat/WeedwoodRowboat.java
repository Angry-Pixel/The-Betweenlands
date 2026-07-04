package thebetweenlands.common.entity.rowboat;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.network.PacketDistributor;
import thebetweenlands.client.BetweenlandsClient;
import thebetweenlands.common.network.serverbound.RowboatRowPacket;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.util.*;

import javax.annotation.Nullable;
import java.util.EnumMap;

/*
 * Useful links:
 * https://en.wikipedia.org/wiki/Glossary_of_rowing_terms
 * https://en.wikipedia.org/wiki/Glossary_of_nautical_terms
 * https://en.wikipedia.org/wiki/List_of_ship_directions
 * https://en.wikipedia.org/wiki/Anatomy_of_a_rowing_stroke
 */
public class WeedwoodRowboat extends Boat implements IEntityWithComplexSpawn {

	private static final CubicBezier DEVIATION_DRAG = new CubicBezier(0.9F, 0, 1, 0.6F);
	private static final CubicBezier SPEED_WAVE_POWER = new CubicBezier(0, 1, 0, 1);

	@SuppressWarnings("unchecked") //holy casting Batman
	private static final EnumMap<ShipSide, EntityDataAccessor<Float>> ROW_PROGRESS = ShipSide.newEnumMap((Class<EntityDataAccessor<Float>>) (Class<?>) EntityDataAccessor.class, SynchedEntityData.defineId(WeedwoodRowboat.class, EntityDataSerializers.FLOAT), SynchedEntityData.defineId(WeedwoodRowboat.class, EntityDataSerializers.FLOAT));
	private static final EntityDataAccessor<Boolean> IS_TARRED = SynchedEntityData.defineId(WeedwoodRowboat.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> HAS_LANTERN = SynchedEntityData.defineId(WeedwoodRowboat.class, EntityDataSerializers.BOOLEAN);

	public static final float OAR_ROTATION_SCALE = -28;
	public static final float ROW_PROGRESS_PERIOD = (2.0F * Mth.PI) / Math.abs(OAR_ROTATION_SCALE);
	private static final float OAR_LENGTH = 40F / 16;
	private static final float BLADE_LENGTH = 12F / 16;
	private static final float LOOM_LENGTH = OAR_LENGTH - BLADE_LENGTH;
	private static final float RESTING_ROW_PROGRESS = ROW_PROGRESS_PERIOD * 0.05F;
	private static final int FORCE_SETTLE_DURATION = 10;
	private static final Quat UP = Quat.fromAxisAngle(0, 1, 0, 0);
	private static final OpenSimplexNoise WAVE_RNG = new OpenSimplexNoise(6354); // 1486858338
	private static final EnumMap<ShipSide, SoundEvent> SOUND_ROW = ShipSide.newEnumMap(SoundEvent.class, SoundRegistry.ROWBOAT_ROW_STARBOARD.get(), SoundRegistry.ROWBOAT_ROW_PORT.get());
	private static final EnumMap<ShipSide, SoundEvent> SOUND_ROW_START = ShipSide.newEnumMap(SoundEvent.class, SoundRegistry.ROWBOAT_ROW_START_STARBOARD.get(), SoundRegistry.ROWBOAT_ROW_START_PORT.get());
	private final EnumMap<ShipSide, OarState> oars = ShipSide.newEnumMap(OarState.class, new OarState(), new OarState());

	public static class OarState {
		float rowForce = 0.0F;
		int rowTime = FORCE_SETTLE_DURATION;
		float prevRowProgress = RESTING_ROW_PROGRESS;
		float rowProgress = RESTING_ROW_PROGRESS;
		boolean oarState = false;
		boolean oarInAir = false;
		float prevOarXWavePull;
		float prevOarZWavePull;
		float oarXWavePull;
		float oarZWavePull;
	}

	private float drag;
	private float submergeTicks;
	private int inWaterTicks;
	private double rotationalVelocity;
	private boolean prevOarStrokeLeft;
	private boolean prevOarStrokeRight;
	private ShipSide synchronizer = ShipSide.STARBOARD;
	private Quat prevRotation = Quat.fromAxisAngle(0, 1, 0, 0);
	private final Quat rotation = new Quat(this.prevRotation);
	private double prevWaveHeight;
	private double waveHeight;
	private float prevPilotPower;
	private float pilotPower;

	@Nullable
	private RowboatLantern lantern;

	public WeedwoodRowboat(EntityType<? extends Boat> type, Level level) {
		super(type, level);
	}

	public WeedwoodRowboat(Level level, double x, double y, double z) {
		this(EntityRegistry.WEEDWOOD_ROWBOAT.get(), level);
		this.setPos(x, y, z);
		this.xo = x;
		this.yo = y;
		this.zo = z;
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(ROW_PROGRESS.get(ShipSide.STARBOARD), RESTING_ROW_PROGRESS);
		builder.define(ROW_PROGRESS.get(ShipSide.PORT), RESTING_ROW_PROGRESS);
		builder.define(IS_TARRED, false);
		builder.define(HAS_LANTERN, false);
	}

	@Override
	protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions dimensions, float partialTick) {
		Matrix mat = new Matrix();
		double pelvis = 0.75;
		mat.translate(0, pelvis - 1.05, 0);
		mat.rotate(this.rotation);
		mat.translate(0, 1.5, 0);
		mat.rotate(-this.getYRot() * Mth.DEG_TO_RAD, 0, 1, 0);
		mat.translate(0, this.getWaveHeight(1) - pelvis, 0.2625);
		return mat.transform(Vec3.ZERO);
	}

	@Override
	public Item getDropItem() {
		return ItemRegistry.WEEDWOOD_ROWBOAT.get();
	}

	@Override
	public ItemStack getPickedResult(HitResult target) {
		return this.getItem();
	}

	public ItemStack getItem() {
		ItemStack stack = new ItemStack(this.getDropItem());
		CompoundTag attrs = new CompoundTag();
		this.addAdditionalSaveData(attrs);
		if (!attrs.isEmpty()) {
			stack.set(DataComponents.CUSTOM_DATA, CustomData.of(attrs));
		}
		return stack;
	}

	public void setIsTarred(boolean isTarred) {
		this.getEntityData().set(IS_TARRED, isTarred);
	}

	public boolean isTarred() {
		return this.getEntityData().get(IS_TARRED);
	}

	@Override
	public void setInput(boolean starboard, boolean port, boolean forward, boolean backward) {
		this.oars.get(ShipSide.STARBOARD).oarState = starboard;
		this.oars.get(ShipSide.PORT).oarState = port;
	}

	public void setOarStates(boolean starboard, boolean port, float progressStarboard, float progressPort) {
		this.setPaddleState(port, starboard);
		this.setRowProgress(ShipSide.STARBOARD, progressStarboard);
		this.setRowProgress(ShipSide.PORT, progressPort);
	}

	@Override
	protected int getMaxPassengers() {
		return 1;
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (this.level().isClientSide() || this.isRemoved()) {
			return true;
		} else if (this.isInvulnerableTo(source)) {
			return false;
		} else {
			this.setHurtDir(-this.getHurtDir());
			this.setHurtTime(10);
			this.markHurt();
			this.setDamage(this.getDamage() + amount * 10.0F);
			this.gameEvent(GameEvent.ENTITY_DAMAGE, source.getEntity());
			boolean creative = source.isCreativePlayer();
			if (creative || this.getDamage() > 20.0F) {
				if (!creative && this.level().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
					this.spawnAtLocation(this.getItem(), 0);
				}
				this.kill();
			}

			return true;
		}
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
		if (HAS_LANTERN.equals(key)) {
			this.lantern = this.hasLantern() ? new RowboatLantern(1.2F, 0.2F) : null;
		}
		super.onSyncedDataUpdated(key);
	}

	@Override
	public InteractionResult interact(Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (stack.is(ItemRegistry.TAR_DRIP) && !this.isTarred()) {
			if (!this.level().isClientSide()) {
				this.setIsTarred(true);
				stack.consume(1, player);
				this.playSound(SoundRegistry.TAR_BEAST_STEP.get(), 0.9F + this.getRandom().nextFloat() * 0.1F, 0.6F + this.getRandom().nextFloat() * 0.15F);
			}
			return InteractionResult.sidedSuccess(this.level().isClientSide());
		} else if (!stack.isEmpty() && stack.is(ItemRegistry.WEEDWOOD_ROWBOAT_LANTERN_UPGRADE)) {
			if (!this.level().isClientSide()) {
				stack.consume(1, player);
				this.setHasLantern(true);
			}
			return InteractionResult.sidedSuccess(this.level().isClientSide());
		} else if (!player.isSecondaryUseActive()) {
			if (!this.level().isClientSide()) {
				return player.startRiding(this) ? InteractionResult.CONSUME : InteractionResult.PASS;
			} else {
				return InteractionResult.SUCCESS;
			}
		}
		return InteractionResult.PASS;
	}

	private void setHasLantern(boolean lantern) {
		this.getEntityData().set(HAS_LANTERN, lantern);
	}

	private boolean hasLantern() {
		return this.getEntityData().get(HAS_LANTERN);
	}

	@Override
	protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
		if (onGround) {
			if (this.fallDistance > 0) {
				state.getBlock().fallOn(this.level(), state, pos, this, this.fallDistance);
			}
			this.resetFallDistance();
		} else if (y < 0) {
			this.fallDistance -= (float) y;
		}
	}

	@Override
	protected void addPassenger(Entity passenger) {
		super.addPassenger(passenger);
		if (this.level().isClientSide() && this.getControllingPassenger() == passenger) {
			BetweenlandsClient.onPilotEnterWeedwoodRowboat(passenger);
		}
	}

	@Override
	protected void removePassenger(Entity passenger) {
		if (this.level().isClientSide() && this.getControllingPassenger() == passenger) {
			BetweenlandsClient.onPilotExitWeedwoodRowboat(this, passenger);
		}
		super.removePassenger(passenger);
	}

	@Override
	public void tick() {
		double pow = 1 - SPEED_WAVE_POWER.eval(Mth.sqrt((float) ((this.getX() - this.xo) * (this.getX() - this.xo) + (this.getZ() - this.zo) * (this.getZ() - this.zo))));

		if (this.getHurtTime() > 0) {
			this.setHurtTime(this.getHurtTime() - 1);
		}

		if (this.getDamage() > 0.0F) {
			this.setDamage(this.getDamage() - 1.0F);
		}

		this.baseTick();
		this.tickLerp();
		if (this.level().isClientSide()) {
			this.updateClientOarProgress(ShipSide.STARBOARD);
			this.updateClientOarProgress(ShipSide.PORT);
		}

		boolean left = this.getAppropriateOarState(ShipSide.STARBOARD);
		boolean right = this.getAppropriateOarState(ShipSide.PORT);
		this.updateRowForce(ShipSide.STARBOARD, left, prevOarStrokeLeft);
		this.updateRowForce(ShipSide.PORT, right, prevOarStrokeRight);
		this.updatePilotPull();
		this.prevOarStrokeLeft = left;
		this.prevOarStrokeRight = right;
		this.prevRotation = new Quat(this.rotation);
		this.prevWaveHeight = this.waveHeight;
		OarState oarStarboard = this.oars.get(ShipSide.STARBOARD);
		OarState oarPort = this.oars.get(ShipSide.PORT);
		oarStarboard.prevOarXWavePull = oarStarboard.oarXWavePull;
		oarPort.prevOarXWavePull = oarPort.oarXWavePull;
		oarStarboard.prevOarZWavePull = oarStarboard.oarZWavePull;
		oarPort.prevOarZWavePull = oarPort.oarZWavePull;
		if (this.isInWater()) {
			this.hitWaves(pow);
			this.inWaterTicks++;
		} else {
			this.rotation.interpolate(UP, 0.175);
			this.waveHeight -= this.waveHeight * 0.6F;
			if (this.waveHeight < 1e-3F) {
				this.waveHeight = 0;
			}
			this.inWaterTicks = 0;
		}
		if (this.isControlledByLocalInstance()) {
			if (!(this.getFirstPassenger() instanceof Player)) {
				this.setPaddleState(false, false);
			}
			this.applyForces();

			Vec3 motion = null;
			if (this.level().isClientSide()) {
				motion = this.applyRowForce();
			}
			this.setYRot((float) (this.getYRot() + this.rotationalVelocity));
			if (this.level().isClientSide()) {
				if (motion != null) {
					this.updateMotion(motion);
				}
				PacketDistributor.sendToServer(new RowboatRowPacket(oarStarboard.oarState, oarPort.oarState, oarStarboard.rowProgress, oarPort.rowProgress));
			}
			float rotationLeft = this.getAppropriateRowProgress(ShipSide.STARBOARD);
			float rotationRight = this.getAppropriateRowProgress(ShipSide.PORT);
			this.returnOarToResting(ShipSide.STARBOARD, rotationLeft);
			this.returnOarToResting(ShipSide.PORT, rotationRight);
			this.synchronizeOars();
			this.move(MoverType.SELF, this.getDeltaMovement());
		} else {
			this.setDeltaMovement(Vec3.ZERO);
		}
		this.checkInsideBlocks();
		if (this.isInWater()) {
			if (this.level().isClientSide()) {
				this.animateHullWaterInteraction();
				this.animateOars();
			} else {
				this.createSoundFX();
			}
		}
		if (this.lantern != null && this.level().isClientSide()) {
			this.lantern.tick(this.getLanternPosition(), this.getYRot());
		}
		if (!this.level().isClientSide()) {
			this.level().getEntities(this, this.getBoundingBox().inflate(0.2, 0.05, 0.2)).forEach(this::push);
		}
		this.setYRot(Mth.wrapDegrees(this.getYRot()));
		this.yRotO = MathUtils.adjustAngleForInterpolation(this.getYRot(), this.yRotO);
	}

	@Nullable
	public RowboatLantern getLantern() {
		return this.lantern;
	}

	private void hitWaves(double pow) {
		// TODO: custom smooth sync total world time (from 1.12)
		double t = this.level().getGameTime() * 0.03D;
		double roughness = 0.15D * pow * (this.inWaterTicks < 20 ? this.inWaterTicks / 20.0D : 1.0D);
		double scale = 0.5;
		double x = this.getX();
		double z = this.getZ();
		Matrix mat = new Matrix();
		mat.rotate(this.rotation);
		mat.rotate(-this.getYRot() * Mth.DEG_TO_RAD, 0, 1, 0);
		Vec3 posFront = mat.transform(new Vec3(0, 0, 0.75));
		Vec3 posStarboard = mat.transform(new Vec3(0.435, 0, -0.5));
		Vec3 posPort = mat.transform(new Vec3(-0.435, 0, 0.5));
		double sx0 = posFront.x;
		double sz0 = posFront.z;
		double sx1 = posStarboard.x;
		double sz1 = posStarboard.z;
		double sx2 = posPort.x;
		double sz2 = posPort.z;
		double sy0 = WAVE_RNG.eval((x + sx0) * scale, t, (z + sz0) * scale) * roughness;
		double sy1 = WAVE_RNG.eval((x + sx1) * scale, t, (z + sz1) * scale) * roughness;
		double sy2 = WAVE_RNG.eval((x + sx2) * scale, t, (z + sz2) * scale) * roughness;
		Vec3 s0 = new Vec3(sx0 * scale, sy0, sz0 * scale);
		Vec3 s1 = new Vec3(sx1 * scale, sy1, sz1 * scale);
		Vec3 s2 = new Vec3(sx2 * scale, sy2, sz2 * scale);
		Vec3 normal = s2.subtract(s1).cross(s0.subtract(s1)).normalize();
		Vec3 yAxis = new Vec3(0, 1, 0);
		double angle = Math.acos(Math.max(Math.min(normal.dot(yAxis), 1), -1));
		Vec3 axis = normal.cross(yAxis).normalize();
		Quat wave = Quat.fromAxisAngle(axis.x, axis.y, axis.z, -angle * 0.4);
		this.rotation.interpolate(wave, 0.2);
		Vec3 point = new Vec3(0, roughness, 0);
		this.waveHeight = point.subtract(normal.scale(point.subtract(s0).dot(normal))).y;
		this.pullOarByWave(ShipSide.STARBOARD, normal);
		this.pullOarByWave(ShipSide.PORT, normal);
		if (!this.isTarred() && this.isControlledByLocalInstance()) {
			double wx = normal.x;
			double wz = normal.z;
			double mag = Math.sqrt(wx * wx + wz * wz);
			double strength = mag / Math.min(((1 - normal.y) * 1.8 * roughness), 0.00125);
			if (strength > 0) {
				this.setDeltaMovement(this.getDeltaMovement().add(wx / strength, 0.0D, wz / strength));
				double dir = Math.atan2(wz, wx) * Mth.RAD_TO_DEG;
				this.rotationalVelocity += (float) (Math.signum(MathUtils.modularDelta(this.getYRot(), dir - 90, 360)) * Math.min((1 - normal.y) * 60 * roughness, roughness));
			}
		}
	}

	private void updatePilotPull() {
		this.prevPilotPower = this.pilotPower;
		OarState oarStarboard = this.oars.get(ShipSide.STARBOARD);
		OarState oarPort = this.oars.get(ShipSide.PORT);
		int timeStarboard = oarStarboard.rowTime;
		int timePort = oarPort.rowTime;
		if (timeStarboard > 20 && timePort > 20 && getAppropriateOarState(ShipSide.STARBOARD) && getAppropriateOarState(ShipSide.PORT) && getAppropriateRowProgress(ShipSide.STARBOARD) == getAppropriateRowProgress(ShipSide.PORT)) {
			if (this.pilotPower < 1) {
				this.pilotPower += 0.2F;
				if (this.pilotPower > 1) {
					this.pilotPower = 1;
				}
			}
		} else if (this.pilotPower > 0) {
			this.pilotPower -= 0.16F;
			if (this.pilotPower < 0) {
				this.pilotPower = 0;
			}
		}
	}

	private void pullOarByWave(ShipSide side, Vec3 normal) {
		Vec3 oar = this.getOarVector(side);
		Vec3 of = new Vec3(oar.x, 0, oar.z);
		Vec3 nf = new Vec3(normal.x, 0, normal.z);
		float angle = nf.length() < 1e-12 ? 0 : (float) Math.acos(Math.max(Math.min(nf.dot(of) / (nf.length() * of.length()), 1), -1));
		float align = MathUtils.linearTransformf(angle, 0, Mth.PI, 1, 0);
		float yaw = (float) Math.atan2(-normal.z, -normal.x) - (this.getYRot() - 90) * Mth.DEG_TO_RAD;
		float pitch = (float) Math.acos(Math.max(Math.min(normal.dot(of), 1), -1));
		OarState oarSide = oars.get(side);
		float x = oarSide.oarXWavePull;
		oarSide.oarXWavePull = x + (Mth.clamp(yaw * align * (float) nf.length() * 2, -0.3F, 0.3F) - x) * 0.7F * (float) nf.length();
		float z = oarSide.oarZWavePull;
		oarSide.oarZWavePull = z + ((pitch - Mth.HALF_PI) * (1 - align) * (getOarElevation(side) + 1) / 2 - z) * 0.4F;
	}

	private void updateClientOarProgress(ShipSide side) {
		OarState oarSide = oars.get(side);
		oarSide.prevRowProgress = oarSide.rowProgress;
		if (!isUserSteering()) {
			oarSide.rowProgress = getServerRowProgress(side);
		}
	}

	private void returnOarToResting(ShipSide side, float preApplyValue) {
		if (getRowForce(side) == 0) {
			float value = getAppropriateRowProgress(side);
			if (value != RESTING_ROW_PROGRESS) {
				float dist = RESTING_ROW_PROGRESS - value;
				if (dist < 0) {
					dist += ROW_PROGRESS_PERIOD;
				}
				if (dist < 1e-4 && preApplyValue < RESTING_ROW_PROGRESS) {
					value = RESTING_ROW_PROGRESS;
				} else {
					float increment = dist * 0.085F;
					if (increment > 0.005F) {
						increment = 0.005F;
					}
					value += increment;
				}
			}
			this.setRowProgress(side, value);
		}
	}

	private void synchronizeOars() {
		if (this.getRowForce(this.synchronizer) == 0) {
			return;
		}
		ShipSide desynced = this.synchronizer.getOpposite();
		if (this.getRowForce(desynced) == 0) {
			return;
		}
		float target = this.getAppropriateRowProgress(this.synchronizer);
		float value = this.getAppropriateRowProgress(desynced);
		if (Math.abs(target - value) < 1e-6F) {
			return;
		}
		if (target < value) {
			this.synchronizer = desynced;
			return;
		}
		value += 0.0045F;
		if (value > target) {
			value = target;
		}
		this.setRowProgress(desynced, value);
	}

	@Override
	public void positionRider(Entity passenger, Entity.MoveFunction callback) {
		super.positionRider(passenger, callback);
		if (this.hasPassenger(passenger)) {
			passenger.setYRot((float) (passenger.getYRot() + this.rotationalVelocity));
			passenger.setYHeadRot((float) (passenger.getYHeadRot() + this.rotationalVelocity));
			this.clampRotation(passenger);
		}
	}

	@Override
	protected void clampRotation(Entity entity) {
		entity.setYBodyRot(Mth.wrapDegrees(this.getYRot() - 180));
		float delta = Mth.wrapDegrees(entity.getYRot() - this.getYRot() - 180);
		float clamped = Mth.clamp(delta, -135, 135);
		entity.yRotO += clamped - delta;
		entity.setYRot(entity.getYRot() + clamped - delta);
		entity.setYHeadRot(entity.getYRot());
	}

	private void applyForces() {
		float buoyancy = 0;
		BlockPos pos = this.blockPosition();
		BlockState blockAt = this.level().getBlockState(pos);
		BlockState blockAbove = this.level().getBlockState(pos.above());
		if (isWater(blockAt) && !isWater(blockAbove)) {
			float y = (float) pos.getY() + getLiquidHeight(blockAt, this.level(), pos) + this.getBbHeight();
			buoyancy = (y - (float) this.getBoundingBox().minY - 0.55F) / this.getBbHeight();
			this.drag = 0.9875F;
			this.submergeTicks = 0;
		} else if (isWater(blockAt) && isWater(blockAbove)) {
			buoyancy = 1.25F;
			this.drag = 0.975F;
			this.submergeTicks++;
		} else if (blockAt.isAir()) {
			BlockState blockBellow = this.level().getBlockState(pos.below());
			if (isWater(blockBellow)) {
				this.drag = 0.95F;
			} else if (blockBellow.blocksMotion()) {
				this.drag = 0.35F;
			} else {
				this.drag = 1;
			}
		}
		float motionRawAngle = (float) Math.atan2(this.getDeltaMovement().z(), this.getDeltaMovement().x());
		float motionAngle = Mth.wrapDegrees(motionRawAngle * Mth.RAD_TO_DEG + 180);
		float deviation = Math.abs(Mth.wrapDegrees(this.getYRot() - 90 - motionAngle)) / 180;
		this.drag *= MathUtils.linearTransformf(DEVIATION_DRAG.eval(deviation), 0, 1, 1, 0.25F);
		this.setDeltaMovement(this.getDeltaMovement().subtract(0.0D, 0.04D, 0.0D).multiply(this.drag, 1.0D, this.drag));
		this.rotationalVelocity *= this.drag * 0.95F;
		if (buoyancy > 0) {
			this.setDeltaMovement(this.getDeltaMovement().add(0.0D, buoyancy * 0.06D, 0.0D).multiply(1.0D, 0.75D, 1.0D));
		}
	}

	@Nullable
	private Vec3 applyRowForce() {
		if (this.getControllingPassenger() == null || this.submergeTicks >= 25) {
			return null;
		}
		Vec3 rowForce = new Vec3(1, 0, 0);
		Vec3 motion = new Vec3(0, 0, 0);
		Vec3 rotation = new Vec3(0, 0, 0);
		float leftOarForce = this.getRowForce(ShipSide.STARBOARD);
		float rightOarForce = this.getRowForce(ShipSide.PORT);
		float forceFactor = 0.35F;
		if (leftOarForce > 0) {
			updateRowProgress(ShipSide.STARBOARD, leftOarForce * getOarWaterResistance(ShipSide.STARBOARD));
			if (canOarsApplyForce()) {
				leftOarForce *= getOarPeriodicForceApplyment(ShipSide.STARBOARD);
				Vec3 leftLever = new Vec3(0, 0, leftOarForce);
				motion = motion.add(0, 0, leftOarForce * forceFactor);
				Vec3 cross = rowForce.cross(leftLever);
				rotation = rotation.add(cross.x, cross.y, cross.z);
			}
		}
		if (rightOarForce > 0) {
			this.updateRowProgress(ShipSide.PORT, rightOarForce * this.getOarWaterResistance(ShipSide.PORT));
			if (canOarsApplyForce()) {
				rightOarForce *= this.getOarPeriodicForceApplyment(ShipSide.PORT);
				Vec3 rightLever = new Vec3(0, 0, rightOarForce);
				motion = motion.add(0, 0, rightOarForce * forceFactor);
				Vec3 cross = new Vec3(-rowForce.x, -rowForce.y, -rowForce.z).cross(rightLever);
				rotation = rotation.add(cross.x, cross.y, cross.z);
			}
		}
		Vec3 currentMotion = new Vec3(this.getDeltaMovement().x(), 0, this.getDeltaMovement().z());
		if (currentMotion.length() < 0.1 && rotation.x * rotation.x + rotation.y * rotation.y + rotation.z + rotation.z > 0) {
			motion = motion.scale(0.35);
			rotation = rotation.scale(1.6);
		}
		this.rotationalVelocity += (float) (rotation.y * 10.0F);
		return motion;
	}

	private void updateMotion(Vec3 motion) {
		motion = motion.yRot(-this.getYRot() * Mth.DEG_TO_RAD);
		this.setDeltaMovement(this.getDeltaMovement().add(motion));
		this.setPaddleState(this.oars.get(ShipSide.STARBOARD).oarState, this.oars.get(ShipSide.PORT).oarState);
	}

	private float getOarPeriodicForceApplyment(ShipSide side) {
		return MathUtils.linearTransformf(this.getOarElevation(side), -1, 1, 0, 2);
	}

	private float getOarWaterResistance(ShipSide side) {
		float weight = MathUtils.linearTransformf(this.getOarElevation(side), -1, 1, 1, 0.25F);
		float velocity = Mth.sqrt((float) (this.getDeltaMovement().x() * this.getDeltaMovement().x() + this.getDeltaMovement().z() * this.getDeltaMovement().z()));
		final float max = 0.5F;
		if (velocity > max) {
			velocity = max;
		}
		float t = velocity / max;
		return weight + (1 - weight) * t;
	}

	private float getOarElevation(ShipSide side) {
		return Mth.cos(this.getAppropriateRowProgress(side) * OAR_ROTATION_SCALE);
	}

	public boolean canOarsApplyForce() {
		return this.drag <= 1;
	}

	public float getRowForce(ShipSide side) {
		return 0.017F * this.oars.get(side).rowForce;
	}

	public void updateRowProgress(ShipSide side, float value) {
		this.setRowProgress(side, this.getAppropriateRowProgress(side) + value);
	}

	public float getPilotPower(float delta) {
		return Mth.lerp(delta, this.prevPilotPower, this.pilotPower);
	}

	public void updateRowForce(ShipSide side, boolean oarStroke, boolean prevOarStroke) {
		OarState oarSide = this.oars.get(side);
		float force = oarSide.rowForce;
		int time = oarSide.rowTime + 1;
		if (oarStroke || time < FORCE_SETTLE_DURATION) {
			if (!prevOarStroke && oarStroke && time >= FORCE_SETTLE_DURATION) {
				force = 1;
				time = 0;
			} else {
				force = Math.max(force - 0.05F, 0.55F);
			}
		} else {
			force = Math.max(force - 0.1F, 0);
		}
		oarSide.rowTime = time;
		oarSide.rowForce = force;
	}

	private void animateHullWaterInteraction() {
		double motionX = this.getX() - this.xo;
		double motionY = this.getY() - this.yo;
		double motionZ = this.getZ() - this.zo;
		double velocity = Math.sqrt(motionX * motionX + motionZ * motionZ);
		if (velocity > 0.2625) {
			double vecX = Math.cos((this.getYRot() - 90) * Mth.DEG_TO_RAD);
			double vecZ = Math.sin((this.getYRot() - 90) * Mth.DEG_TO_RAD);
			for (int p = 0; p < 1 + velocity * 60; p++) {
				double near = this.getRandom().nextFloat() * 2 - 1;
				double far = (this.getRandom().nextInt(2) * 2 - 1) * 0.7;
				double splashX, splashZ;
				if (this.getRandom().nextBoolean()) {
					splashX = this.getX() - vecX * near * 0.8 + vecZ * far;
					splashZ = this.getZ() - vecZ * near * 0.8 - vecX * far;
				} else {
					splashX = this.getX() + vecX + vecZ * near * 0.7;
					splashZ = this.getZ() + vecZ - vecX * near * 0.7;
				}
				this.level().addParticle(ParticleTypes.SPLASH, splashX, Math.ceil(this.getY()) - 0.125, splashZ, motionX, 0.01, motionZ);
			}
		}
	}

	private void animateOars() {
		double motionX = this.getX() - this.xo;
		double motionZ = this.getZ() - this.zo;
		double motion = Math.sqrt(motionX * motionX + motionZ * motionZ);
		this.animateOar(ShipSide.STARBOARD, motion);
		this.animateOar(ShipSide.PORT, motion);
	}

	private void animateOar(ShipSide side, double motion) {
		Vec3 oarlock = getOarlockPosition(side);
		Vec3 oarVector = getOarVector(side);
		Vec3 blade = oarlock.add(oarVector.x * OAR_LENGTH, oarVector.y * OAR_LENGTH, oarVector.z * OAR_LENGTH);
		//TODO verify
		HitResult raytrace = this.level().clip(new ClipContext(new Vec3(oarlock.x, oarlock.y, oarlock.z), blade, ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, this));
		boolean bladeInAir = true;
		float amountOfBladeInAir = BLADE_LENGTH;
		if (raytrace.getType() == HitResult.Type.BLOCK) {
			if (motion > 0.175) {
				for (int p = 0; p < motion; p++) {
					float x = MathUtils.linearTransformf(this.getRandom().nextFloat(), 0, 1, -0.2F, 0.2F);
					float y = MathUtils.linearTransformf(this.getRandom().nextFloat(), 0, 1, -0.2F, 0.2F);
					float z = MathUtils.linearTransformf(this.getRandom().nextFloat(), 0, 1, -0.2F, 0.2F);
					this.level().addParticle(ParticleTypes.SPLASH, raytrace.getLocation().x + x, raytrace.getLocation().y + y, raytrace.getLocation().z + z, this.getDeltaMovement().x(), 0.01, this.getDeltaMovement().z());
				}
			}
			float amountInAir = (float) oarlock.distanceTo(raytrace.getLocation());
			if (amountInAir < LOOM_LENGTH) {
				bladeInAir = false;
			} else {
				amountOfBladeInAir = OAR_LENGTH - amountInAir;
			}
		}
		if (bladeInAir && this.getRandom().nextFloat() < 0.4F) {
			for (int p = 0, count = (int) (1 + motion * 3); p < count; p++) {
				float point = LOOM_LENGTH + this.getRandom().nextFloat() * amountOfBladeInAir;
				float x = (float) (oarVector.x * point + MathUtils.linearTransformf(this.getRandom().nextFloat(), 0, 1, -0.1F, 0.1F));
				float y = (float) (oarVector.y * point + MathUtils.linearTransformf(this.getRandom().nextFloat(), 0, 1, -0.4F, -0.2F));
				float z = (float) (oarVector.z * point + MathUtils.linearTransformf(this.getRandom().nextFloat(), 0, 1, -0.1F, 0.1F));
				this.level().addParticle(ParticleTypes.SPLASH, oarlock.x + x, oarlock.y + y, oarlock.z + z, 0, 1e-8, 0);
			}
		}
	}

	public Vec3 getLanternPosition() {
		Matrix mat = new Matrix();
		mat.translate(this.getX(), this.getY() + this.waveHeight, this.getZ());
		return this.getLocalLanternPosition(mat, 1);
	}

	public Vec3 getLocalLanternPosition(float t) {
		return this.getLocalLanternPosition(new Matrix(), t);
	}

	private Vec3 getLocalLanternPosition(Matrix mat, float t) {
		mat.rotate(getRotation(t));
		mat.rotate(-(this.yRotO + (this.getYRot() - this.yRotO) * t) * Mth.DEG_TO_RAD, 0, 1, 0);
		float roll = getRoll(t);
		if (roll != 0) {
			mat.rotate(roll * Mth.DEG_TO_RAD, 0, 0, 1);
		}
		mat.scale(-1, -1, 1);
		return mat.transform(new Vec3(0.0, -0.9922452370881644, 1.6755452654813303));
	}

	private Vec3 getOarlockPosition(ShipSide side) {
		float dir = side == ShipSide.PORT ? 1 : -1;
		Matrix mat = new Matrix();
		mat.translate(this.getX(), this.getY() + this.waveHeight, this.getZ());
		mat.rotate(this.rotation);
		mat.rotate(-this.getYRot() * Mth.DEG_TO_RAD, 0, 1, 0);
		mat.translate(0.6 * dir, 1.15, -0.2);
		return mat.transform(Vec3.ZERO);
	}

	private Vec3 getOarVector(ShipSide side) {
		float dir = side == ShipSide.PORT ? -1 : 1;
		float progress = getAppropriateRowProgress(side);
		float yaw = getOarRotationX(side, progress, 1) * dir - (this.getYRot() - 90) * Mth.DEG_TO_RAD;
		float pitch = getOarRotationZ(side, progress, 1) - Mth.HALF_PI;
		float cosYaw = Mth.cos(-yaw);
		float sinYaw = Mth.sin(-yaw);
		float cosPitch = Mth.cos(-pitch);
		Mat4d mat = new Mat4d();
		mat.asQuaternion(this.rotation);
		return mat.transform(new Vec3(-sinYaw * cosPitch, Mth.sin(pitch), cosYaw * cosPitch));
	}

	private void createSoundFX() {
		createOarSoundFX(ShipSide.STARBOARD);
		createOarSoundFX(ShipSide.PORT);
	}

	private void createOarSoundFX(ShipSide side) {
		OarState oarSide = this.oars.get(side);
		Vec3 oarlock = this.getOarlockPosition(side);
		Vec3 oarVector = this.getOarVector(side);
		Vec3 blade = oarlock.add(oarVector.x * OAR_LENGTH, oarVector.y * OAR_LENGTH, oarVector.z * OAR_LENGTH);
		HitResult raytrace = this.level().clip(new ClipContext(new Vec3(oarlock.x, oarlock.y, oarlock.z), blade, ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, this));
		boolean bladeInAir = true;
		if (raytrace.getType() == HitResult.Type.BLOCK) {
			float amountInAir = (float) oarlock.distanceTo(raytrace.getLocation());
			if (amountInAir < LOOM_LENGTH) {
				bladeInAir = false;
				float force = oarSide.rowForce;
				boolean start = force == 1;
				if (oarSide.oarInAir || start) {
					float volume = force * 0.8F + 0.2F;
					SoundEvent sound = (start ? SOUND_ROW_START : SOUND_ROW).get(side);
					this.level().playSound(null, raytrace.getLocation().x, raytrace.getLocation().y, raytrace.getLocation().z, sound, SoundSource.NEUTRAL, volume, 0.8F + this.getRandom().nextFloat() * 0.3F);
				}
			}
		}
		oarSide.oarInAir = bladeInAir;
	}

	@Override
	public boolean isPushedByFluid(FluidType type) {
		return this.isControlledByLocalInstance();
	}

	@Override
	public void updateInWaterStateAndDoWaterCurrentPushing() {
		double mX = this.getDeltaMovement().x(), mZ = this.getDeltaMovement().z();
		if (this.updateFluidHeightAndDoFluidPushing(FluidTags.WATER, 0.014D)) {
			if (mX != this.getDeltaMovement().x() && mZ != this.getDeltaMovement().z() && this.isControlledByLocalInstance()) {
				double aX = this.getDeltaMovement().x() - mX, aZ = this.getDeltaMovement().z() - mZ;
				double dir = Math.atan2(aZ, aX) * Mth.RAD_TO_DEG;
				double speed = Math.sqrt(this.getDeltaMovement().x() * this.getDeltaMovement().x() + this.getDeltaMovement().z() * this.getDeltaMovement().z());
				this.rotationalVelocity += (Mth.clamp(MathUtils.modularDelta(this.getYRot(), dir - 90, 360) * Math.min(speed * 1.1, 0.3), -12, 12) - rotationalVelocity) * 0.75;
			}
			if (!this.wasTouchingWater && !this.firstTick) {
				float volume = Mth.sqrt((float) (this.getDeltaMovement().x() * this.getDeltaMovement().x() * 0.2 + this.getDeltaMovement().y() * this.getDeltaMovement().y() + this.getDeltaMovement().z() * this.getDeltaMovement().z() * 0.2)) * 0.2F;
				if (volume > 0.15) {
					if (volume > 1) {
						volume = 1;
					}
					this.playSound(this.getSwimSplashSound(), volume, 1 + (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.4F);
					float min = Mth.floor(this.getBoundingBox().minY);
					for (int i = 0; i < 1 + this.getBbWidth() * 20; i++) {
						float x = (this.getRandom().nextFloat() * 2 - 1) * this.getBbWidth();
						float z = (this.getRandom().nextFloat() * 2 - 1) * this.getBbWidth();
						this.level().addParticle(ParticleTypes.BUBBLE, this.getX() + x, min + 1, this.getZ() + z, this.getDeltaMovement().x(), this.getDeltaMovement().y() - this.getRandom().nextFloat() * 0.2F, this.getDeltaMovement().z());
					}
					for (int i = 0; i < 1 + this.getBbWidth() * 20; i++) {
						float x = (this.getRandom().nextFloat() * 2 - 1) * this.getBbWidth();
						float z = (this.getRandom().nextFloat() * 2 - 1) * this.getBbWidth();
						this.level().addParticle(ParticleTypes.SPLASH, this.getX() + x, min + 1, this.getZ() + z, this.getDeltaMovement().x(), this.getDeltaMovement().y(), this.getDeltaMovement().z());
					}
				}
			}
			this.resetFallDistance();
			this.wasTouchingWater = true;
			this.clearFire();
		} else {
			this.wasTouchingWater = false;
		}
	}

	public void setRowProgress(ShipSide side, float progress) {
		while (progress > ROW_PROGRESS_PERIOD) {
			progress -= ROW_PROGRESS_PERIOD;
		}
		while (progress < 0) {
			progress += ROW_PROGRESS_PERIOD;
		}
		if (this.isUserSteering()) {
			this.oars.get(side).rowProgress = progress;
		} else {
			this.getEntityData().set(ROW_PROGRESS.get(side), progress);
		}
	}

	public float getRowProgress(ShipSide side, float delta) {
		OarState oarSide = this.oars.get(side);
		float prevProgress = oarSide.prevRowProgress;
		float progress = oarSide.rowProgress;
		return delta * (MathUtils.mod(progress - prevProgress + ROW_PROGRESS_PERIOD / 2, ROW_PROGRESS_PERIOD) - ROW_PROGRESS_PERIOD / 2) + prevProgress;
	}

	public float getServerRowProgress(ShipSide side) {
		return this.getEntityData().get(ROW_PROGRESS.get(side));
	}

	public float getAppropriateRowProgress(ShipSide side) {
		return this.isUserSteering() ? this.oars.get(side).rowProgress : this.getServerRowProgress(side);
	}

	public boolean getAppropriateOarState(ShipSide side) {
		return this.isUserSteering() ? this.oars.get(side).oarState : this.getPaddleState(side.ordinal());
	}

	private boolean isUserSteering() {
		Entity entity = this.getControllingPassenger();
		return entity instanceof Player player && player.isLocalPlayer();
	}

	public float getOarRotationX(ShipSide side, float theta, float delta) {
		OarState oarSide = this.oars.get(side);
		return Mth.sin(theta * WeedwoodRowboat.OAR_ROTATION_SCALE) * 0.6F + oarSide.prevOarXWavePull + (oarSide.oarXWavePull - oarSide.prevOarXWavePull) * delta;
	}

	public float getOarRotationY(ShipSide side, float theta) {
		float angle = MathUtils.linearTransformf(Mth.sin(theta * WeedwoodRowboat.OAR_ROTATION_SCALE + Mth.HALF_PI), -1, 1, Mth.PI / 2, 0);
		if (side == ShipSide.PORT) {
			angle = Mth.PI - angle;
		}
		return angle;
	}

	public float getOarRotationZ(ShipSide side, float theta, float delta) {
		OarState oarSide = this.oars.get(side);
		float angle = Mth.cos(theta * WeedwoodRowboat.OAR_ROTATION_SCALE) * 0.45F - Mth.PI / 2.5F + oarSide.prevOarZWavePull + (oarSide.oarZWavePull - oarSide.prevOarZWavePull) * delta;
		if (side == ShipSide.PORT) {
			angle = -angle;
		}
		return angle;
	}

	public Quat getRotation(float delta) {
		Quat rot = new Quat(this.prevRotation);
		rot.interpolate(this.rotation, delta);
		return rot;
	}

	public float getRoll(float delta) {
		float timeSinceHit = this.getHurtTime() - delta;
		float damageTaken = Math.max(this.getDamage() - delta, 0.0F);
		if (timeSinceHit > 0) {
			return Mth.sin(timeSinceHit) * timeSinceHit * damageTaken / 10 * this.getHurtDir();
		}
		return 0;
	}

	public double getWaveHeight(float delta) {
		return delta == 1 ? this.waveHeight : Mth.lerp(delta, this.prevWaveHeight, this.waveHeight);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		compound.putBoolean("tarred", this.isTarred());
		compound.putBoolean("lantern", this.hasLantern());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		this.setIsTarred(compound.getBoolean("tarred"));
		this.setHasLantern(compound.getBoolean("lantern"));
	}

	@Override
	public void writeSpawnData(RegistryFriendlyByteBuf buf) {
	}

	@Override
	public void readSpawnData(RegistryFriendlyByteBuf buf) {
		this.yRotO = this.getYRot();
	}

	public static boolean isTarred(ItemStack stack) {
		CustomData tag = stack.get(DataComponents.CUSTOM_DATA);
		if (tag != null) {
			return tag.copyTag().getBoolean("tarred");
		}
		return false;
	}

	private static boolean isWater(BlockState state) {
		return state.getFluidState().is(FluidTags.WATER);
	}

	private static float getLiquidHeight(BlockState state, Level level, BlockPos pos) {
		if (!state.getFluidState().isEmpty()) {
			return state.getFluidState().getHeight(level, pos);
		}
		return 1.0F;
	}

	// Inherited methods not needed

	@Override
	public float getWaterLevelAbove() {
		return 0.0F;
	}

	@Override
	public float getGroundFriction() {
		return 0.0F;
	}

	@Override
	public void setVariant(Type boatType) {
	}

	@Override
	public Type getVariant() {
		return Type.OAK;
	}

	@Override
	public float getRowingTime(int oar, float limbSwing) {
		return 0.0F;
	}
}
