package thebetweenlands.common.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;
import org.apache.commons.lang3.ArrayUtils;
import thebetweenlands.common.block.entity.DecayPitControlBlockEntity;
import thebetweenlands.common.block.entity.DecayPitGroundChainBlockEntity;
import thebetweenlands.common.block.entity.DecayPitHangingChainBlockEntity;
import thebetweenlands.common.entity.boss.PrimordialMalevolence;
import thebetweenlands.common.entity.multipart.DecayPitTargetPart;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.util.RotationMatrix;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class DecayPitTarget extends Entity {
	private static final byte ATTACK_RAISE_COOLDOWN = 40;
	private static final byte EVENT_SHIELD_CRUMBLE = 82;

	private final RotationMatrix rotationMatrix = new RotationMatrix();

	public float animationTicksPrev = 0;
	public int animationTicksChain = 0;
	public int animationTicksChainPrev = 0;
	public final int MAX_PROGRESS = 768; // max distance of travel from origin so; 768 * 0.0078125F = 6 Blocks
	public final int MIN_PROGRESS = 0;
	public final float MOVE_UNIT = 0.0078125F; // unit of movement
	public final DecayPitTargetPart[] shields;
	public final DecayPitTargetPart[] parts;
	public final DecayPitTargetPart target_north;
	public final DecayPitTargetPart target_east;
	public final DecayPitTargetPart target_west;
	public final DecayPitTargetPart target_south;
	public final DecayPitTargetPart bottom;

	private static final EntityDataAccessor<Float> ANIMATION_TICKS = SynchedEntityData.defineId(DecayPitTarget.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Boolean> IS_RAISING = SynchedEntityData.defineId(DecayPitTarget.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> IS_MOVING = SynchedEntityData.defineId(DecayPitTarget.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> IS_SLOW = SynchedEntityData.defineId(DecayPitTarget.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Integer> PROGRESS = SynchedEntityData.defineId(DecayPitTarget.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Boolean> TARGET_N_ACTIVE = SynchedEntityData.defineId(DecayPitTarget.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> TARGET_E_ACTIVE = SynchedEntityData.defineId(DecayPitTarget.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> TARGET_S_ACTIVE = SynchedEntityData.defineId(DecayPitTarget.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> TARGET_W_ACTIVE = SynchedEntityData.defineId(DecayPitTarget.class, EntityDataSerializers.BOOLEAN);

	public int attackDamageTicks = 0;
	public final int[] beamTransparencyTicks = new int[4];

	public DecayPitTarget(EntityType<? extends Entity> type, Level level) {
		super(type, level);
		this.shields = new DecayPitTargetPart[]{
			new DecayPitTargetPart(this, 1F, 1F, true),
			new DecayPitTargetPart(this, 1F, 1F, true),
			new DecayPitTargetPart(this, 1F, 1F, true),
			new DecayPitTargetPart(this, 1F, 1F, true),
			new DecayPitTargetPart(this, 1F, 1F, true),
			new DecayPitTargetPart(this, 1F, 1F, true),
			new DecayPitTargetPart(this, 1F, 1F, true),
			new DecayPitTargetPart(this, 1F, 1F, true),
			new DecayPitTargetPart(this, 1F, 1F, true),
			new DecayPitTargetPart(this, 1F, 1F, true),
			new DecayPitTargetPart(this, 1F, 1F, true),
			new DecayPitTargetPart(this, 1F, 1F, true),
			new DecayPitTargetPart(this, 1F, 1F, true),
			new DecayPitTargetPart(this, 1F, 1F, true),
			new DecayPitTargetPart(this, 1F, 1F, true),
			new DecayPitTargetPart(this, 1F, 1F, true)
		};
		this.parts = ArrayUtils.addAll(this.shields,
			this.target_north = new DecayPitTargetPart(this, 2F, 2F, false),
			this.target_east = new DecayPitTargetPart(this, 2F, 2F, false),
			this.target_south = new DecayPitTargetPart(this, 2F, 2F, false),
			this.target_west = new DecayPitTargetPart(this, 2F, 2F, false),
			this.bottom = new DecayPitTargetPart(this, 3F, 1F, false));
		this.setId(ENTITY_COUNTER.getAndAdd(this.parts.length + 1) + 1);
	}

	@Override
	public void setId(int id) {
		super.setId(id);
		for (int i = 0; i < this.parts.length; i++)
			this.parts[i].setId(id + i + 1);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		builder.define(IS_RAISING, false);
		builder.define(IS_MOVING, false);
		builder.define(IS_SLOW, true);
		builder.define(PROGRESS, 0);
		builder.define(ANIMATION_TICKS, 0.0F);
		builder.define(TARGET_N_ACTIVE, true);
		builder.define(TARGET_E_ACTIVE, true);
		builder.define(TARGET_W_ACTIVE, true);
		builder.define(TARGET_S_ACTIVE, true);
	}

	@Override
	public void tick() {
		super.tick();
		if (this.attackDamageTicks > 0) {
			this.attackDamageTicks--;
		}

		boolean isMovingDown = this.isMoving() && !this.isSlow();

		if (this.getTargetEActive() && !isMovingDown) {
			this.beamTransparencyTicks[0] = Math.min(15, this.beamTransparencyTicks[0] + 1);
		} else {
			this.beamTransparencyTicks[0] = Math.max(0, this.beamTransparencyTicks[0] - 1);
		}

		if (this.getTargetWActive() && !isMovingDown) {
			this.beamTransparencyTicks[1] = Math.min(15, this.beamTransparencyTicks[1] + 1);
		} else {
			this.beamTransparencyTicks[1] = Math.max(0, this.beamTransparencyTicks[1] - 1);
		}

		if (this.getTargetSActive() && !isMovingDown) {
			this.beamTransparencyTicks[2] = Math.min(15, this.beamTransparencyTicks[2] + 1);
		} else {
			this.beamTransparencyTicks[2] = Math.max(0, this.beamTransparencyTicks[2] - 1);
		}

		if (this.getTargetNActive() && !isMovingDown) {
			this.beamTransparencyTicks[3] = Math.min(15, this.beamTransparencyTicks[3] + 1);
		} else {
			this.beamTransparencyTicks[3] = Math.max(0, this.beamTransparencyTicks[3] - 1);
		}

		float animationTicks = this.getEntityData().get(ANIMATION_TICKS);

		this.animationTicksPrev = animationTicks;
		this.animationTicksChainPrev = this.animationTicksChain;

		if (!this.level().isClientSide()) {
			if (animationTicks + 1 >= 360F) {
				this.getEntityData().set(ANIMATION_TICKS, 0.0F);
			} else {
				this.getEntityData().set(ANIMATION_TICKS, animationTicks + 1);
			}
		}

		while (animationTicks - this.animationTicksPrev < -180.0F) {
			this.animationTicksPrev -= 360.0F;
		}
		while (animationTicks - this.animationTicksPrev >= 180.0F) {
			this.animationTicksPrev += 360.0F;
		}

		//Set prev pos, rotation, etc.
		for (Entity entity : this.parts) {
			entity.setOldPosAndRot();
		}

		float angle = 0.0F;
		boolean oddShield = true;
		for (DecayPitTargetPart shield : this.shields) {
			float multiplier = oddShield ? -1.0F : 1.0F;
			this.setNewShieldHitboxPos(animationTicks * multiplier + angle, shield, oddShield);
			angle += 22.5F;
			oddShield = !oddShield;
		}

		this.target_north.setPos(this.getX(), this.getY() + 3D, this.getZ() - 0.75D);
		this.target_east.setPos(this.getX() + 0.75D, this.getY() + 3D, this.getZ());
		this.target_south.setPos(this.getX(), this.getY() + 3D, this.getZ() + 0.75D);
		this.target_west.setPos(this.getX() - 0.75D, this.getY() + 3D, this.getZ());
		this.bottom.setPos(this.getX(), this.getY(), this.getZ());

		if (this.isMoving()) {
			if (this.isSlow())
				this.animationTicksChain++;
			else
				this.animationTicksChain += 8;
			if (this.getHangingChain() != null)
				this.getHangingChain().setProgress(this.getProgress());

			if (!this.isRaising() && this.getProgress() < MAX_PROGRESS) {
				this.move(MoverType.SELF, new Vec3(0D, -MOVE_UNIT * 8D, 0D));
				this.setProgress(this.getProgress() + 8);

				if (this.getHangingChain() != null) {
					this.getHangingChain().setMoving(true);
					this.getHangingChain().setSlow(false);
				}

				for (DecayPitGroundChainBlockEntity chain : this.getGroundChains()) {
					chain.setRaising(true);
					chain.setMoving(true);
					chain.setSlow(false);
				}
			}

			if (this.isRaising() && this.getProgress() > MIN_PROGRESS) {
				this.move(MoverType.SELF, new Vec3(0D, MOVE_UNIT, 0D));
				this.setProgress(this.getProgress() - 1);

				if (this.getHangingChain() != null) {
					this.getHangingChain().setMoving(true);
					this.getHangingChain().setSlow(true);
				}

				for (DecayPitGroundChainBlockEntity chain : this.getGroundChains()) {
					chain.setRaising(false);
					chain.setMoving(true);
					chain.setSlow(true);
				}
			}

		}

		if (this.animationTicksChainPrev >= 128) {
			this.animationTicksChain = this.animationTicksChainPrev = 0;
			this.setMoving(false);
		}

		if (this.getProgress() > MAX_PROGRESS)
			this.setProgress(MAX_PROGRESS);

		if (this.getProgress() < MIN_PROGRESS)
			this.setProgress(MIN_PROGRESS);

		if (!this.level().isClientSide()) { // upsy-daisy
			if (this.getProgress() > MIN_PROGRESS && this.attackDamageTicks == 0)
				this.moveUp();

			DecayPitControlBlockEntity control = this.getControl();

			if (control != null && this.level().getGameTime() % 10 == 0) {
				if (this.getProgress() < 128)
					control.setSpawnType(0);
				if (this.getProgress() >= 128 && this.getProgress() < 256)
					control.setSpawnType(1);
				if (this.getProgress() >= 256 && this.getProgress() < 384)
					control.setSpawnType(2);
				if (this.getProgress() >= 384 && this.getProgress() < 512)
					control.setSpawnType(3);
				if (this.getProgress() >= 512 && this.getProgress() < 640)
					control.setSpawnType(4);
				if (this.getProgress() >= 640)
					control.setSpawnType(5);
			}

			if (control != null && control.getSpawnType() == 5) {
				DecayPitHangingChainBlockEntity hangingChains = this.getHangingChain();

				if (hangingChains != null) {
					hangingChains.setBroken(true);
					hangingChains.setMoving(true);
					hangingChains.setSlow(false);
					hangingChains.setChanged();
				}

				for (DecayPitGroundChainBlockEntity chain : this.getGroundChains()) {
					chain.setBroken(true);
					chain.setRaising(false);
					chain.setMoving(true);
					chain.setSlow(false);
					chain.setChanged();
				}

				this.kill();
			}
		}
	}

	protected void setNewShieldHitboxPos(float animationTicks, DecayPitTargetPart shield, boolean odd) {
		double a = Math.toRadians(animationTicks);
		double offSetX = -Math.sin(a) * 2.825D;
		double offSetZ = Math.cos(a) * 2.825D;
		float wobble;
		if (odd)
			wobble = Mth.sin(animationTicks * 0.14F) * 1.2F;
		else
			wobble = -Mth.sin(animationTicks * 0.14F) * 1.2F;
		float squarePoint = Math.signum(wobble);
		if (squarePoint == -1F)
			wobble = 0F;
		shield.setPos(this.getX() + offSetX, this.target_north.getY() + this.target_north.getBbHeight() / 2.0D - shield.getBbHeight() + wobble, this.getZ() + offSetZ);
		shield.setYRot(animationTicks + 180F);

		while (shield.getYRot() - shield.yRotO < -180.0F) {
			shield.yRotO -= 360.0F;
		}
		while (shield.getYRot() - shield.yRotO >= 180.0F) {
			shield.yRotO += 360.0F;
		}
	}

	@Override
	public void kill() {
		if (!this.level().isClientSide()) {
			this.level().broadcastEntityEvent(this, EVENT_SHIELD_CRUMBLE);
		}
		super.kill();
	}

	@Override
	public boolean isMultipartEntity() {
		return true;
	}

	@Override
	public PartEntity<?>[] getParts() {
		return this.parts;
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		return false;
	}

	public boolean attackEntityFromPart(DecayPitTargetPart part, DamageSource source, float amount) {
		boolean wasBlocked = false;

		if (source.is(DamageTypeTags.IS_EXPLOSION))
			return false;

		Entity sourceEntity = source.getEntity();
		Entity immediateEntity = source.getDirectEntity();

		Entity attackingEntity = immediateEntity != null ? immediateEntity : sourceEntity;

		if (attackingEntity == null) {
			wasBlocked = true;
		} else {
			Vec3 pos = attackingEntity.getEyePosition();

			Vec3 ray;
			if (attackingEntity instanceof LivingEntity) {
				ray = attackingEntity.getLookAngle();
			} else {
				ray = attackingEntity.getDeltaMovement().normalize();
			}

			DecayPitTargetPart hitShield = this.rayTraceShields(pos, ray);
			if (hitShield != null) {
				wasBlocked = true;
			}
		}


		if (part == this.target_north && !wasBlocked) {
			if (!this.level().isClientSide()) {
				if (this.getTargetNActive()) {
					this.setTargetNActive(false);
					this.playSound(SoundRegistry.BEAM_ACTIVATE.get(), 0.5F, 1F);
				}
				if (this.getAllTargetsHit())
					this.moveDown();
				this.attackDamageTicks += ATTACK_RAISE_COOLDOWN;
			}
			return true;
		} else if (part == this.target_east && !wasBlocked) {
			if (!this.level().isClientSide()) {
				if (this.getTargetEActive()) {
					this.setTargetEActive(false);
					this.playSound(SoundRegistry.BEAM_ACTIVATE.get(), 0.5F, 1F);
				}
				if (this.getAllTargetsHit())
					this.moveDown();
				this.attackDamageTicks += ATTACK_RAISE_COOLDOWN;
			}
			return true;
		} else if (part == this.target_south && !wasBlocked) {
			if (!this.level().isClientSide()) {
				if (this.getTargetSActive()) {
					this.setTargetSActive(false);
					this.playSound(SoundRegistry.BEAM_ACTIVATE.get(), 0.5F, 1F);
				}
				if (this.getAllTargetsHit())
					this.moveDown();
				this.attackDamageTicks += ATTACK_RAISE_COOLDOWN;
			}
			return true;
		} else if (part == this.target_west && !wasBlocked) {
			if (!this.level().isClientSide()) {
				if (this.getTargetWActive()) {
					this.setTargetWActive(false);
					this.playSound(SoundRegistry.BEAM_ACTIVATE.get(), 0.5F, 1F);
				}
				if (this.getAllTargetsHit())
					this.moveDown();
				this.attackDamageTicks += ATTACK_RAISE_COOLDOWN;
			}
			return true;
		} else if (wasBlocked) {
			if (!this.level().isClientSide()) {
				//TODO
//				if (source instanceof EntityDamageSourceIndirect) {
//					Entity sourceEntity = ((EntityDamageSourceIndirect) source).getTrueSource();
//					if (sourceEntity != null && !world.isAirBlock(sourceEntity.getPosition().down())) {
//						EntityRootGrabber grabber = new EntityRootGrabber(this.world, true);
//						grabber.setPosition(source.getTrueSource().getPosition().down(), 40);
//						getEntityWorld().spawnEntity(grabber);
//					}
//				}
				this.moveUp();
			}
			return false;
		}

		return false;
	}

	private boolean getAllTargetsHit() {
		return !getTargetNActive() && !getTargetEActive() && !getTargetSActive() && !getTargetWActive();
	}

	@Nullable
	public DecayPitTargetPart rayTraceShields(Vec3 pos, Vec3 dir) {
		Vec3 ray = dir.normalize().scale(3);

		float shieldSize = 0.6F;

		Vec3 v0 = new Vec3(-shieldSize, -shieldSize, 0);
		Vec3 v1 = new Vec3(shieldSize, -shieldSize, 0);
		Vec3 v2 = new Vec3(shieldSize, shieldSize, 0);
		Vec3 v3 = new Vec3(-shieldSize, shieldSize, 0);

		for (DecayPitTargetPart shieldPart : this.parts) {
			if (shieldPart.shield) {
				Vec3 center = shieldPart.position().add(0, shieldPart.getBbHeight() / 2, 0);
				this.rotationMatrix.setRotations(0, (float) Math.toRadians(shieldPart.getYRot()), 0);
				Vec3 relPos = this.rotationMatrix.transformVec(pos.subtract(center), Vec3.ZERO);
				Vec3 relRay = this.rotationMatrix.transformVec(ray, Vec3.ZERO);
				if (PrimordialMalevolence.rayTraceTriangle(relPos, relRay, v0, v1, v2) || PrimordialMalevolence.rayTraceTriangle(relPos, relRay, v2, v3, v0)) {
					return shieldPart;
				}
			}
		}

		return null;
	}

	@Override
	public void handleEntityEvent(byte id) {
		if (id == EVENT_SHIELD_CRUMBLE) {
			for (DecayPitTargetPart shield : this.shields) {
				for (int i = 0; i < 50; i++) {
					this.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, BlockRegistry.MUD_BRICKS.toStack()),
						shield.getBoundingBox().minX + (this.getRandom().nextFloat() * shield.getBbWidth()),
						shield.getBoundingBox().minY + (this.getRandom().nextFloat() * shield.getBbHeight()),
						shield.getBoundingBox().minZ + (this.getRandom().nextFloat() * shield.getBbWidth()),
						(this.getRandom().nextFloat() - 0.5F) * 0.1F, 0.3D, (this.getRandom().nextFloat() - 0.5F) * 0.1F);
				}
			}
		} else {
			super.handleEntityEvent(id);
		}
	}

	private void moveUp() {
		if (this.getProgress() > MIN_PROGRESS) {
			this.setRaising(true);
			this.setMoving(true);
			this.setSlow(true);
		}
	}

	private void moveDown() {
		if (this.getProgress() < MAX_PROGRESS) {
			this.setRaising(false);
			this.setMoving(true);
			this.setSlow(false);
			this.setTargetNActive(true);
			this.setTargetEActive(true);
			this.setTargetSActive(true);
			this.setTargetWActive(true);
		}

		if (this.getControl() != null) {
			this.attackDamageTicks = 400 - (this.getControl().getSpawnType() * 40);
		} else {
			this.attackDamageTicks = 200;
		}
		this.playSound(SoundRegistry.PLUG_HIT.get());
	}

	@Nullable
	public DecayPitHangingChainBlockEntity getHangingChain() {
		for (int x = -1; x < 1; x++) {
			for (int y = 0; y < 15; y++) {
				for (int z = -1; z < 1; z++) {
					if (this.level().getBlockEntity(this.blockPosition().offset(x, y, z)) instanceof DecayPitHangingChainBlockEntity chain) {
						chain.setProgress(this.getProgress());
						chain.setChanged();
						return chain;
					}
				}
			}
		}
		return null;
	}

	@Nullable
	public DecayPitControlBlockEntity getControl() {
		for (int x = -1; x < 1; x++) {
			for (int y = -15; y < 0; y++) {
				for (int z = -1; z < 1; z++) {
					if (this.level().getBlockEntity(this.blockPosition().offset(x, y, z)) instanceof DecayPitControlBlockEntity control) {
						return control;
					}
				}
			}
		}
		return null;
	}

	public List<DecayPitGroundChainBlockEntity> getGroundChains() {
		List<DecayPitGroundChainBlockEntity> chains = new ArrayList<>();
		BlockPos posEntity = this.blockPosition();
		Iterable<BlockPos> blocks = BlockPos.betweenClosed(posEntity.offset(-12, 3, -12), posEntity.offset(12, 9, 12));
		for (BlockPos pos : blocks)
			if (this.level().getBlockEntity(pos) instanceof DecayPitGroundChainBlockEntity chain) {
				chains.add(chain);
			}
		return chains;
	}

	public void setProgress(int progress) {
		this.getEntityData().set(PROGRESS, progress);
	}

	public int getProgress() {
		return this.getEntityData().get(PROGRESS);
	}

	public void setRaising(boolean raising) {
		this.getEntityData().set(IS_RAISING, raising);
	}

	public boolean isRaising() {
		return this.getEntityData().get(IS_RAISING);
	}

	public void setMoving(boolean moving) {
		this.getEntityData().set(IS_MOVING, moving);
	}

	public boolean isMoving() {
		return this.getEntityData().get(IS_MOVING);
	}

	public void setSlow(boolean slow) {
		this.getEntityData().set(IS_SLOW, slow);
	}

	public boolean isSlow() {
		return this.getEntityData().get(IS_SLOW);
	}

	public void setTargetNActive(boolean active) {
		this.getEntityData().set(TARGET_N_ACTIVE, active);
	}

	public boolean getTargetNActive() {
		return this.getEntityData().get(TARGET_N_ACTIVE);
	}

	public void setTargetEActive(boolean active) {
		this.getEntityData().set(TARGET_E_ACTIVE, active);
	}

	public boolean getTargetEActive() {
		return this.getEntityData().get(TARGET_E_ACTIVE);
	}

	public void setTargetWActive(boolean active) {
		this.getEntityData().set(TARGET_W_ACTIVE, active);
	}

	public boolean getTargetWActive() {
		return this.getEntityData().get(TARGET_W_ACTIVE);
	}

	public void setTargetSActive(boolean active) {
		this.getEntityData().set(TARGET_S_ACTIVE, active);
	}

	public boolean getTargetSActive() {
		return this.getEntityData().get(TARGET_S_ACTIVE);
	}

	public boolean hasLineOfSight(Entity entity) {
		if (entity.level() != this.level()) {
			return false;
		} else {
			Vec3 vec3 = new Vec3(this.getX(), this.getEyeY(), this.getZ());
			Vec3 vec31 = new Vec3(entity.getX(), entity.getEyeY(), entity.getZ());
			return !(vec31.distanceTo(vec3) > 128.0) && this.level().clip(new ClipContext(vec3, vec31, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() == HitResult.Type.MISS;
		}
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag tag) {
		this.setProgress(tag.getInt("progress"));
		this.setTargetNActive(tag.getBoolean("target_north"));
		this.setTargetEActive(tag.getBoolean("target_east"));
		this.setTargetWActive(tag.getBoolean("target_west"));
		this.setTargetSActive(tag.getBoolean("target_south"));
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag tag) {
		tag.putInt("progress", this.getProgress());
		tag.putBoolean("target_north", this.getTargetNActive());
		tag.putBoolean("target_east", this.getTargetEActive());
		tag.putBoolean("target_west", this.getTargetWActive());
		tag.putBoolean("target_south", this.getTargetSActive());
	}

	@Override
	public AABB getBoundingBoxForCulling() {
		return this.getBoundingBox().inflate(12.0D, 3.0D, 12.0D);
	}
}
