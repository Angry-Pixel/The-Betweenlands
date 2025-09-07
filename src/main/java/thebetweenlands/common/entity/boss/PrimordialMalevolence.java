package thebetweenlands.common.entity.boss;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.api.attachment.ProtectionShield;
import thebetweenlands.api.entity.MusicPlayer;
import thebetweenlands.api.entity.bossbar.BetweenlandsBossBar;
import thebetweenlands.api.entity.bossbar.BetweenlandsServerBossBar;
import thebetweenlands.client.audio.EntityMusicLayers;
import thebetweenlands.common.entity.BLEntity;
import thebetweenlands.common.entity.boss.malevolence.PrimordialMalevolenceBlockade;
import thebetweenlands.common.entity.boss.malevolence.PrimordialMalevolenceProjectile;
import thebetweenlands.common.entity.boss.malevolence.PrimordialMalevolenceSpawner;
import thebetweenlands.common.entity.boss.malevolence.PrimordialMalevolenceTurret;
import thebetweenlands.common.entity.monster.Wight;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.storage.location.EnumLocationType;
import thebetweenlands.common.world.storage.location.LocationStorage;
import thebetweenlands.util.RotationMatrix;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PrimordialMalevolence extends Monster implements BLEntity, BetweenlandsBossBar, MusicPlayer {
	private final BetweenlandsServerBossBar bossInfo = new BetweenlandsServerBossBar(this.getDisplayName(), BossType.NORMAL_BOSS);
	protected static final EntityDataAccessor<Integer> SHIELD_STATE = SynchedEntityData.defineId(PrimordialMalevolence.class, EntityDataSerializers.INT);
	protected static final EntityDataAccessor<Float> SHIELD_ROTATION = SynchedEntityData.defineId(PrimordialMalevolence.class, EntityDataSerializers.FLOAT);
	protected static final EntityDataAccessor<Boolean> FLOATING_STATE = SynchedEntityData.defineId(PrimordialMalevolence.class, EntityDataSerializers.BOOLEAN);
	protected static final EntityDataAccessor<Boolean> GROUND_ATTACK_STATE = SynchedEntityData.defineId(PrimordialMalevolence.class, EntityDataSerializers.BOOLEAN);
	protected static final EntityDataAccessor<BlockPos> ANCHOR = SynchedEntityData.defineId(PrimordialMalevolence.class, EntityDataSerializers.BLOCK_POS);
	protected static final EntityDataAccessor<Float> ANCHOR_RADIUS = SynchedEntityData.defineId(PrimordialMalevolence.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Optional<UUID>> BOSSINFO_ID = SynchedEntityData.defineId(PrimordialMalevolence.class, EntityDataSerializers.OPTIONAL_UUID);

	public static final double SHIELD_OFFSET_X = 0.0D;
	public static final double SHIELD_OFFSET_Y = 1D;
	public static final double SHIELD_OFFSET_Z = 0.0D;

	private static final double ICOSAHEDRON_X = 0.525731112119133606D;
	private static final double ICOSAHEDRON_Z = 0.850650808352039932D;
	public static final double[][] ICOSAHEDRON_VERTICES = new double[][]{{-ICOSAHEDRON_X, 0.0, ICOSAHEDRON_Z}, {ICOSAHEDRON_X, 0.0, ICOSAHEDRON_Z}, {-ICOSAHEDRON_X, 0.0, -ICOSAHEDRON_Z}, {ICOSAHEDRON_X, 0.0, -ICOSAHEDRON_Z},
		{0.0, ICOSAHEDRON_Z, ICOSAHEDRON_X}, {0.0, ICOSAHEDRON_Z, -ICOSAHEDRON_X}, {0.0, -ICOSAHEDRON_Z, ICOSAHEDRON_X}, {0.0, -ICOSAHEDRON_Z, -ICOSAHEDRON_X},
		{ICOSAHEDRON_Z, ICOSAHEDRON_X, 0.0}, {-ICOSAHEDRON_Z, ICOSAHEDRON_X, 0.0}, {ICOSAHEDRON_Z, -ICOSAHEDRON_X, 0.0}, {-ICOSAHEDRON_Z, -ICOSAHEDRON_X, 0.0}
	};
	public static final int[][] ICOSAHEDRON_INDICES = new int[][]{
		{0, 4, 1}, {0, 9, 4}, {9, 5, 4}, {4, 5, 8}, {4, 8, 1},
		{8, 10, 1}, {8, 3, 10}, {5, 3, 8}, {5, 2, 3}, {2, 7, 3},
		{7, 10, 3}, {7, 6, 10}, {7, 11, 6}, {11, 0, 6}, {0, 1, 6},
		{6, 1, 10}, {9, 0, 11}, {9, 11, 2}, {9, 2, 5}, {7, 2, 11}
	};

	public final ProtectionShield shield = new ProtectionShield();

	public final AABB coreBoundingBox;

	private BlockPos anchor = BlockPos.ZERO;
	private double anchorRadius;

	private float shieldRotationYaw, shieldRotationPitch, shieldRotationRoll, lastShieldRotationYaw, lastShieldRotationPitch, lastShieldRotationRoll, shieldExplosion, lastShieldExplosion;

	private int groundTicks = 0;

	private int turretTicks = -1;

	private int groundAttackTicks = -1;

	private int turretStreak = -1;
	private int turretStreakTicks = 0;

	private int wightSpawnTicks = -1;

	private int teleportTicks = -1;

	private int blockadeSpawnTicks = -1;

	private int deathTicks = 0;

	private final Object2IntMap<Entity> deflectionDamageCooldowns = new Object2IntOpenHashMap<>();

	public PrimordialMalevolence(EntityType<? extends Monster> type, Level level) {
		super(type, level);
		float coreWidth = 1.0F;
		float coreHeight = 1.0F;
		this.coreBoundingBox = new AABB(-coreWidth / 2.0F, 0F + type.getHeight() / 4.0F, -coreWidth / 2.0F, coreWidth / 2.0F, coreHeight + type.getHeight() / 4.0F, coreWidth / 2.0F);
		for (int i = 0; i < 20; i++) {
			this.shield.setActive(i, true);
		}
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Monster.createMonsterAttributes()
			.add(Attributes.MAX_HEALTH, 320.0D)
			.add(Attributes.ATTACK_DAMAGE, 6.0F);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(SHIELD_STATE, 0);
		builder.define(SHIELD_ROTATION, 0.0F);
		builder.define(FLOATING_STATE, true);
		builder.define(GROUND_ATTACK_STATE, false);
		builder.define(ANCHOR, BlockPos.ZERO);
		builder.define(ANCHOR_RADIUS, 0.0F);
		builder.define(BOSSINFO_ID, Optional.empty());
	}

	public float getShieldExplosion(float partialTicks) {
		return this.lastShieldExplosion + (this.shieldExplosion - this.lastShieldExplosion) * partialTicks;
	}

	public void setAnchor(BlockPos anchor, double radius) {
		this.anchor = anchor;
		this.anchorRadius = radius;
	}

	public Vec3 getAnchorCenter() {
		return new Vec3(this.anchor.getX() + 0.5D, this.anchor.getY() + 0.5D, this.anchor.getZ() + 0.5D);
	}

	public double getAnchorRadius() {
		return this.anchorRadius;
	}

	public int getGroundAttackTicks() {
		return this.groundAttackTicks;
	}

	public float getShieldRotationYaw(float partialTicks) {
		return this.lastShieldRotationYaw + (this.shieldRotationYaw - this.lastShieldRotationYaw) * partialTicks;
	}

	public float getShieldRotationPitch(float partialTicks) {
		return this.lastShieldRotationPitch + (this.shieldRotationPitch - this.lastShieldRotationPitch) * partialTicks;
	}

	public float getShieldRotationRoll(float partialTicks) {
		return this.lastShieldRotationRoll + (this.shieldRotationRoll - this.lastShieldRotationRoll) * partialTicks;
	}

	public static int rayTraceShield(ProtectionShield shield, Vec3 centerPos, float shieldRotationYaw, float shieldRotationPitch, float shieldRotationRoll, float shieldExplosion, Vec3 pos, Vec3 ray, boolean back) {
		int shieldIndex = -1;
		double centroidX = 0;
		double centroidY = 0;
		double centroidZ = 0;

		RotationMatrix rotationMatrix = new RotationMatrix();
		rotationMatrix.setRotations((float) Math.toRadians(-shieldRotationPitch), (float) Math.toRadians(-shieldRotationYaw), (float) Math.toRadians(-shieldRotationRoll));

		//Transform position and ray to local space
		pos = rotationMatrix.transformVec(pos, centerPos);
		ray = rotationMatrix.transformVec(ray, new Vec3(0, 0, 0));

		for (int i = 0; i <= 19; i++) {
			if (!shield.isActive(i)) {
				continue;
			}
			double v3[] = ICOSAHEDRON_VERTICES[ICOSAHEDRON_INDICES[i][0]];
			double v2[] = ICOSAHEDRON_VERTICES[ICOSAHEDRON_INDICES[i][1]];
			double v1[] = ICOSAHEDRON_VERTICES[ICOSAHEDRON_INDICES[i][2]];
			double centerX = (v1[0] + v2[0] + v3[0]) / 3;
			double centerY = (v1[1] + v2[1] + v3[1]) / 3;
			double centerZ = (v1[2] + v2[2] + v3[2]) / 3;
			double len = Math.sqrt(centerX * centerX + centerY * centerY + centerZ * centerZ);
			double a = len + shieldExplosion;
			Vec3 center = new Vec3(centerX, centerY, centerZ);
			centerX += centerPos.x;
			centerY += centerPos.y;
			centerZ += centerPos.z;
			Vec3 vert1Exploded = new Vec3(v1[0], v1[1], v1[2]);
			double b = vert1Exploded.dot(center);
			double d = a * Math.tan(b);
			double vertexExplode = Math.sqrt(a * a + d * d) - 1;
			Vec3 v1Normalized = new Vec3(v1[0], v1[1], v1[2]).normalize();
			Vec3 v2Normalized = new Vec3(v2[0], v2[1], v2[2]).normalize();
			Vec3 v3Normalized = new Vec3(v3[0], v3[1], v3[2]).normalize();
			Vec3 vert1 = new Vec3(v1[0] + v1Normalized.x * vertexExplode, v1[1] + v1Normalized.y * vertexExplode, v1[2] + v1Normalized.z * vertexExplode);
			Vec3 vert2 = new Vec3(v2[0] + v2Normalized.x * vertexExplode, v2[1] + v2Normalized.y * vertexExplode, v2[2] + v2Normalized.z * vertexExplode);
			Vec3 vert3 = new Vec3(v3[0] + v3Normalized.x * vertexExplode, v3[1] + v3Normalized.y * vertexExplode, v3[2] + v3Normalized.z * vertexExplode);
			vert1 = vert1.add(centerPos);
			vert2 = vert2.add(centerPos);
			vert3 = vert3.add(centerPos);
			Vec3 normal = vert2.subtract(vert1).cross(vert3.subtract(vert1));

			if (rayTraceTriangle(pos, ray, vert1, vert2, vert3) && (back || normal.normalize().dot(ray.normalize()) < Math.cos(Math.toRadians(90)))) {
				double dx = centerX - pos.x;
				double dy = centerY - pos.y;
				double dz = centerZ - pos.z;
				double pdx = centroidX - pos.x;
				double pdy = centroidY - pos.y;
				double pdz = centroidZ - pos.z;
				if (shieldIndex == -1 || (Math.sqrt(dx * dx + dy * dy + dz * dz) < Math.sqrt(pdx * pdx + pdy * pdy + pdz * pdz))) {
					shieldIndex = i;
					centroidX = centerX;
					centroidY = centerY;
					centroidZ = centerZ;
				}
			}
		}

		return shieldIndex;
	}

	public static boolean rayTraceTriangle(Vec3 pos, Vec3 ray, Vec3 v0, Vec3 v1, Vec3 v2) {
		final double epsilon = 0.00001;
		Vec3 diff1 = v1.subtract(v0);
		Vec3 diff2 = v2.subtract(v0);
		Vec3 rayCross = ray.cross(diff2);
		double angleDifference = diff1.dot(rayCross);
		if (angleDifference > -epsilon && angleDifference < epsilon)
			return false;
		double f = 1.0D / angleDifference;
		Vec3 s = pos.subtract(v0);
		double u = f * (s.dot(rayCross));
		if (u < 0.0 || u > 1.0) {
			return false;
		}
		Vec3 q = s.cross(diff1);
		double v = f * ray.dot(q);
		if (v < 0.0 || u + v > 1.0) {
			return false;
		}
		double t = f * diff2.dot(q);
		return 1 - t > epsilon;
	}

	public boolean isFloating() {
		return this.getEntityData().get(FLOATING_STATE);
	}

	public void setFloating(boolean floating) {
		this.getEntityData().set(FLOATING_STATE, floating);
		;
	}

	public record AttackShieldResult(boolean deflected, int shieldHit, @Nullable Vec3 pos, @Nullable Vec3 ray) {
	}

	public static AttackShieldResult attackShield(Level world, ProtectionShield shield, Vec3 centerPos, float shieldRotationYaw, float shieldRotationPitch, float shieldRotationRoll, float shieldExplosion, Object2IntMap<Entity> deflectionDamageCooldowns, DamageSource source, boolean checkOnly) {
		Entity sourceEntity = source.getEntity();
		Entity immediateEntity = source.getDirectEntity();

		Entity attackingEntity = immediateEntity != null ? immediateEntity : sourceEntity;

		int shieldHit = -1;
		boolean isDeflected = false;

		Vec3 ray = null;
		Vec3 pos = null;

		if (attackingEntity == null) {
			isDeflected = true;
		} else {
			if (attackingEntity instanceof LivingEntity) {
				ray = attackingEntity.getViewVector(0.0F);
			} else {
				ray = attackingEntity.getDeltaMovement().normalize();
			}
			ray = ray.scale(64.0D);

			pos = attackingEntity.getEyePosition();

			if (shield.hasShield() && (!(attackingEntity instanceof Player player) || !player.isCreative()) || attackingEntity.isShiftKeyDown()) {
				shieldHit = rayTraceShield(shield, centerPos, shieldRotationYaw, shieldRotationPitch, shieldRotationRoll, shieldExplosion, pos, ray, false);

				if (shieldHit >= 0) {
					if (!checkOnly) {
						if (!world.isClientSide() && attackingEntity.isShiftKeyDown() && ((Player)attackingEntity).isCreative()) {
							shield.setActive(shieldHit, false);
						}

						if (world.isClientSide()) {
							shield.setAnimationTicks(shieldHit, 20);
						}
					}

					isDeflected = true;
				}
			}
		}

		if (isDeflected) {
			if (!checkOnly && !world.isClientSide()) {
				boolean damaged = false;

				if (sourceEntity != null && !deflectionDamageCooldowns.containsKey(sourceEntity)) {
					double dx = sourceEntity.getX() - centerPos.x;
					double dy = sourceEntity.getY() - centerPos.y;
					double dz = sourceEntity.getZ() - centerPos.z;
					double len = Math.sqrt(dx * dx + dy * dy + dz * dz);
					sourceEntity.setDeltaMovement(dx / len * 0.8F, dy / len * 0.8F, dz / len * 0.8F);
					sourceEntity.hurtMarked = sourceEntity.hasImpulse = true;
					sourceEntity.hurt(world.damageSources().magic(), 2);

					deflectionDamageCooldowns.put(sourceEntity, 10);
					damaged = true;
				}

				if (immediateEntity != null && !deflectionDamageCooldowns.containsKey(immediateEntity)) {
					double dx = immediateEntity.getX() - centerPos.x;
					double dy = immediateEntity.getY() - centerPos.y;
					double dz = immediateEntity.getZ() - centerPos.z;
					double len = Math.sqrt(dx * dx + dy * dy + dz * dz);
					immediateEntity.setDeltaMovement(dx / len * 0.8F, dy / len * 0.8F, dz / len * 0.8F);
					immediateEntity.hurtMarked = immediateEntity.hasImpulse = true;
					immediateEntity.hurt(world.damageSources().magic(), 2);

					deflectionDamageCooldowns.put(immediateEntity, 10);
					damaged = true;
				}

				if (damaged) {
					world.playSound(null, centerPos.x, centerPos.y, centerPos.z, SoundRegistry.FORTRESS_BOSS_NOPE, SoundSource.HOSTILE, 1, 1);
				}
			}

			return new AttackShieldResult(true, shieldHit, pos, ray);
		} else {
			return new AttackShieldResult(false, shieldHit, pos, ray);
		}
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (!this.isAlive()) {
			return false;
		}

		AttackShieldResult result = attackShield(this.level(), this.shield, new Vec3(this.getX() + SHIELD_OFFSET_X, this.getY() + SHIELD_OFFSET_Y, this.getZ() + SHIELD_OFFSET_Z), this.shieldRotationYaw, this.shieldRotationPitch, this.shieldRotationRoll, this.shieldExplosion, this.deflectionDamageCooldowns, source, false);
		if (!result.deflected) {
			if (result.pos != null && result.ray != null && this.coreBoundingBox.move(this.position()).clip(result.pos, result.ray.add(result.pos.x, result.pos.y, result.pos.z)).isPresent()) {
				return super.hurt(source, amount);
			} else {
				return false;
			}
		}

		if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
			return super.hurt(source, amount);
		}

		return false;
	}

	@Override
	public boolean isPickable() {
		return !this.dead;
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("shields", this.shield.packActiveData());
		compound.putDouble("anchorX", this.anchor.getX());
		compound.putDouble("anchorY", this.anchor.getY());
		compound.putDouble("anchorZ", this.anchor.getZ());
		compound.putDouble("anchorRadius", this.anchorRadius);
		compound.putBoolean("floating", this.isFloating());
		compound.putInt("groundTicks", this.groundTicks);
		compound.putInt("turretTicks", this.turretTicks);
		compound.putInt("groundAttackTicks", this.groundAttackTicks);
		compound.putInt("turretStreak", this.turretStreak);
		compound.putInt("turretStreakTicks", this.turretStreakTicks);
		compound.putInt("wightSpawnTicks", this.wightSpawnTicks);
		compound.putInt("teleportTicks", this.teleportTicks);
		compound.putInt("blockadeSpawnTicks", this.blockadeSpawnTicks);
		compound.putInt("deathTicks", this.deathTicks);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("shields", Tag.TAG_INT)) {
			this.shield.unpackActiveData(compound.getInt("shields"));
		}
		this.anchor = BlockPos.containing(compound.getDouble("anchorX"), compound.getDouble("anchorY"), compound.getDouble("anchorZ"));
		this.anchorRadius = compound.getDouble("anchorRadius");
		this.setFloating(compound.getBoolean("floating"));
		this.groundTicks = compound.getInt("groundTicks");
		this.turretTicks = compound.getInt("turretTicks");
		this.groundAttackTicks = compound.getInt("groundAttackTicks");
		this.turretStreak = compound.getInt("turretStreak");
		this.turretStreakTicks = compound.getInt("turretStreakTicks");
		this.wightSpawnTicks = compound.getInt("wightSpawnTicks");
		this.teleportTicks = compound.getInt("teleportTicks");
		this.blockadeSpawnTicks = compound.getInt("blockadeSpawnTicks");
		this.deathTicks = compound.getInt("deathTicks");
		if (this.hasCustomName())
			this.bossInfo.setName(this.getDisplayName());
	}

	@Override
	public void setCustomName(@Nullable Component name) {
		super.setCustomName(name);
		this.bossInfo.setName(this.getDisplayName());
	}

	@Override
	public void startSeenByPlayer(ServerPlayer player) {
		super.startSeenByPlayer(player);
		this.bossInfo.addPlayer(player);
	}

	@Override
	public void stopSeenByPlayer(ServerPlayer player) {
		super.stopSeenByPlayer(player);
		this.bossInfo.removePlayer(player);
	}

	@Override
	public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
		spawnGroupData = super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
		this.anchor = this.blockPosition();
		this.anchorRadius = 10.0D;
		return spawnGroupData;
	}

	@Override
	public void knockback(double strength, double x, double z) {

	}

	@Override
	protected void customServerAiStep() {
		super.customServerAiStep();
		this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
		this.getEntityData().set(BOSSINFO_ID, Optional.of(this.bossInfo.getId()));
	}

	@Override
	public void aiStep() {
		super.aiStep();
		Iterator<Object2IntMap.Entry<Entity>> cooldownIt = this.deflectionDamageCooldowns.object2IntEntrySet().iterator();
		while (cooldownIt.hasNext()) {
			Object2IntMap.Entry<Entity> entry = cooldownIt.next();

			if (entry.getIntValue() > 0) {
				this.deflectionDamageCooldowns.put(entry.getKey(), entry.getIntValue() - 1);
			} else {
				cooldownIt.remove();
			}
		}

		Player closestPlayer = this.level().getNearestPlayer(this, 32.0D);
		if (closestPlayer != null) {
			this.lookAt(closestPlayer, 360.0F, 360.0F);
			this.setTarget(closestPlayer);
		}

		this.lastShieldRotationYaw = this.shieldRotationYaw;
		this.lastShieldRotationPitch = this.shieldRotationPitch;
		this.lastShieldRotationRoll = this.shieldRotationRoll;

		this.lastShieldExplosion = this.shieldExplosion;
		if (this.isAlive()) {
			this.shieldExplosion = 0.2F;
		} else {
			this.shieldExplosion = 0.2F + (this.deathTicks % 16) / 16.0F * (this.deathTicks / 60.0F);
		}

		float shieldRotation = 0.0F;
		if (this.level().isClientSide()) {
			shieldRotation = this.getEntityData().get(SHIELD_ROTATION);
			this.anchor = this.getEntityData().get(ANCHOR);
			this.anchorRadius = this.getEntityData().get(ANCHOR_RADIUS);
			this.shield.unpackActiveData(this.getEntityData().get(SHIELD_STATE));
		} else {
			if (this.isAlive()) {
				shieldRotation = this.tickCount;
				this.getEntityData().set(SHIELD_ROTATION, shieldRotation + 1);
			}
			this.getEntityData().set(ANCHOR, this.anchor);
			this.getEntityData().set(ANCHOR_RADIUS, (float) this.anchorRadius);
		}
		int activeShields = 0;
		for (int i = 0; i <= 19; i++) {
			if (this.shield.isActive(i))
				activeShields++;
		}
		if (this.isAlive()) {
			this.shieldRotationYaw = shieldRotation * (1.0F + 6.0F / 20.0F * (20 - activeShields));
			this.shieldRotationPitch = shieldRotation * (1.4F + 8.0F / 20.0F * (20 - activeShields));
			this.shieldRotationRoll = shieldRotation * (1.6F + 10.0F / 20.0F * (20 - activeShields));
		} else {
			this.shieldRotationYaw = shieldRotation * 2.0F;
			this.shieldRotationPitch = 0;
			this.shieldRotationRoll = 0;
		}

		if (this.isAlive()) {
			final Vec3 anchorCenter = this.getAnchorCenter();

			if (!this.level().isClientSide()) {
				if (this.isFloating() && this.getY() < anchorCenter.y) {
					this.setDeltaMovement(this.getDeltaMovement().x, 0.1F, this.getDeltaMovement().z);
				} else if (!this.isFloating()) {
					this.setDeltaMovement(this.getDeltaMovement().subtract(0.0F, 0.1F, 0.0F));
					this.groundTicks++;
					if (this.groundTicks > 180 && this.groundAttackTicks > 20) {
						this.groundTicks = 0;
						this.setFloating(true);
					}
				}

				if (this.isFloating() && (this.distanceToSqr(anchorCenter.x, this.getY(), anchorCenter.z) > this.anchorRadius || Math.abs(this.getY() - anchorCenter.y) > this.anchorRadius)) {
					this.level().playSound(null, this.blockPosition(), SoundRegistry.FORTRESS_BOSS_TELEPORT.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
					this.setPos(anchorCenter.x, anchorCenter.y, anchorCenter.z);
				}

				//Heal when no player is nearby
				if (this.tickCount % 12 == 0 && this.getHealth() < this.getMaxHealth()) {
					List<Player> currentlyTrackedEntities = this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(this.anchorRadius * 2, this.anchorRadius * 2, this.anchorRadius * 2), player -> player.distanceToSqr(anchorCenter.x, player.getY(), anchorCenter.z) > this.anchorRadius + 4 || Math.abs(player.getY() - anchorCenter.y) > this.anchorRadius);
					if (currentlyTrackedEntities.isEmpty()) {
						this.heal(1);
					}
				}
			}

			AABB checkArea = this.getBoundingBox().inflate(32, 16, 32);
			List<Player> players = this.level().getEntitiesOfClass(Player.class, checkArea);
			if (!players.isEmpty()) {
				if (!this.level().isClientSide()) {
					this.getEntityData().set(SHIELD_STATE, this.shield.packActiveData());

					if (this.isFloating() && this.getY() >= anchorCenter.y) {
						AABB checkAABB = this.getBoundingBox().inflate(16, 16, 16);
						List<Wight> wights = this.level().getEntitiesOfClass(Wight.class, checkAABB);
						List<PrimordialMalevolenceSpawner> spawners = this.level().getEntitiesOfClass(PrimordialMalevolenceSpawner.class, checkAABB);
						if (wights.isEmpty() && spawners.isEmpty()) {
							this.wightSpawnTicks--;
							if (this.wightSpawnTicks <= 0) {
								if (this.wightSpawnTicks == 0) {
									int spawnY = this.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, this.blockPosition()).getY();
									if (Math.abs(spawnY - this.getY()) < this.anchorRadius) {
										PrimordialMalevolenceSpawner spawner = new PrimordialMalevolenceSpawner(this.level(), this);
										spawner.moveTo(this.getX(), spawnY, this.getZ(), 0, 0);
										spawner.setOwner(this.getUUID());
										this.level().addFreshEntity(spawner);
									}
								}
								this.wightSpawnTicks = 160 + this.level().getRandom().nextInt(200);
							}
						}


						List<PrimordialMalevolenceBlockade> blockades = this.level().getEntitiesOfClass(PrimordialMalevolenceBlockade.class, checkAABB);
						if (blockades.isEmpty()) {
							this.blockadeSpawnTicks--;
							if (this.blockadeSpawnTicks <= 0) {
								if (this.blockadeSpawnTicks == 0) {
									int spawnY = this.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, this.blockPosition()).getY();
									if (Math.abs(spawnY - this.getY()) < this.anchorRadius) {
										PrimordialMalevolenceBlockade blockade = new PrimordialMalevolenceBlockade(this.level(), this);
										blockade.moveTo(this.getX(), spawnY, this.getZ(), 0, 0);
										blockade.setTriangleSize(1.2F + this.level().getRandom().nextFloat() * 1.6F);
										blockade.setOwner(this.getUUID());
										blockade.setMaxDespawnTicks(400);
										this.level().addFreshEntity(blockade);
									}
								}
								this.blockadeSpawnTicks = 190 + this.level().getRandom().nextInt(160);
							}
						}

						this.teleportTicks--;
						if (this.teleportTicks <= 0) {
							if (this.teleportTicks == 0) {
								this.level().playSound(null, this.blockPosition(), SoundRegistry.FORTRESS_BOSS_TELEPORT.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
								this.moveTo(anchorCenter.x + (this.level().getRandom().nextFloat() - 0.5F) * 2.0F * (this.anchorRadius - 1), anchorCenter.y, anchorCenter.z + (this.level().getRandom().nextFloat() - 0.5F) * 2.0F * (this.anchorRadius - 1), 0, 0);
							}
							this.teleportTicks = 140 + this.level().getRandom().nextInt(200);
						}

						if (this.turretStreak <= 0 && this.getHealth() < this.getMaxHealth() / 2.0F) {
							if (this.turretStreak == 0) {
								this.turretStreakTicks++;
								int turretFrequency = Mth.floor(15.0D - 14.0D / 300.0D * this.turretStreakTicks);
								if (this.turretStreakTicks % turretFrequency == 0) {
									double angle = Math.PI * 2.0D / 150.0D * this.turretStreakTicks;
									for (int d = 0; d < 2; d++) {
										Vec3 dir = new Vec3(Math.sin(angle) * (d == 0 ? 1 : -1), 0, Math.cos(angle) * (d == 0 ? 1 : -1));
										dir = dir.normalize().scale(this.anchorRadius);
										PrimordialMalevolenceTurret turret = new PrimordialMalevolenceTurret(this.level(), this);
										turret.moveTo(anchorCenter.x + dir.x, anchorCenter.y + dir.y, anchorCenter.z + dir.z, 0, 0);
										turret.setAttackDelay(turretFrequency);
										this.level().addFreshEntity(turret);
										this.level().playSound(null, this.blockPosition(), SoundRegistry.FORTRESS_BOSS_SUMMON_PROJECTILES.get(), SoundSource.HOSTILE, 0.25F, 0.3F + 0.7F / 300.0F * this.turretStreakTicks);
									}
								}
								if (this.turretStreakTicks >= 300) {
									double angle = Math.PI * 2.0D / 32.0D;
									for (int i = 0; i < 32; i++) {
										Vec3 dir = new Vec3(Math.sin(angle * i), 0, Math.cos(angle * i));
										dir = dir.normalize().scale(this.anchorRadius);
										PrimordialMalevolenceTurret turret = new PrimordialMalevolenceTurret(this.level(), this);
										turret.moveTo(anchorCenter.x + dir.x, anchorCenter.y + dir.y, anchorCenter.z + dir.z, 0, 0);
										turret.setAttackDelay(5 + i / 3);
										this.level().addFreshEntity(turret);
									}
									this.level().playSound(null, this.blockPosition(), SoundRegistry.FORTRESS_BOSS_SUMMON_PROJECTILES.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
									this.turretStreakTicks = 0;
									this.turretStreak = -1;
								}
							}
							if (this.turretStreak < 0) {
								this.turretStreak = 250 + this.level().getRandom().nextInt(200);
								this.turretStreakTicks = 0;
							}
						} else {
							if (this.turretTicks <= 0) {
								if (this.turretTicks == 0) {
									double angle = Math.PI * 2.0D / 9;
									for (int i = 0; i < 9; i++) {
										if (this.level().getRandom().nextInt(3) == 0) {
											Vec3 dir = new Vec3(Math.sin(angle * i), 0, Math.cos(angle * i));
											dir = dir.normalize().scale(8.0D);
											PrimordialMalevolenceTurret turret = new PrimordialMalevolenceTurret(this.level(), this);
											turret.moveTo(anchorCenter.x + dir.x, anchorCenter.y + dir.y, anchorCenter.z + dir.z, 0, 0);
											turret.setDeflectable(this.level().getRandom().nextInt(2) != 0);
											this.level().addFreshEntity(turret);
										}
									}
									this.level().playSound(null, this.blockPosition(), SoundRegistry.FORTRESS_BOSS_SUMMON_PROJECTILES.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
								}
								this.turretTicks = 100 + this.level().getRandom().nextInt(200);
							} else {
								this.turretTicks--;
								if (this.turretStreak > 0)
									this.turretStreak--;
							}
						}
					}

					if (!this.isFloating() && this.onGround()) {
						if (this.groundAttackTicks <= 0) {
							if (this.groundAttackTicks == 0) {
								double angle = Math.PI * 2.0D / 32;
								for (int i = 0; i < 32; i++) {
									Vec3 dir = new Vec3(Math.sin(angle * i), 0, Math.cos(angle * i));
									dir = dir.normalize();
									float speed = 0.8F;
									PrimordialMalevolenceProjectile bullet = new PrimordialMalevolenceProjectile(this.level(), this);
									bullet.moveTo(this.getX(), this.getY() + 0.5D, this.getZ(), 0, 0);
									bullet.shoot(dir.x, dir.y, dir.z, speed, 0.0F);
									this.level().addFreshEntity(bullet);
								}
							}
							this.groundAttackTicks = 40 + this.level().getRandom().nextInt(80);
							this.getEntityData().set(GROUND_ATTACK_STATE, false);
							this.turretStreak = -1;
							this.turretTicks = -1;
						} else {
							this.groundAttackTicks--;
							if (this.groundAttackTicks <= 20) {
								this.getEntityData().set(GROUND_ATTACK_STATE, true);
							}
						}
					} else {
						this.getEntityData().set(GROUND_ATTACK_STATE, false);
					}
				} else {
					for (int i = 0; i <= 19; i++) {
						if (this.shield.getAnimationTicks(i) == 0 && this.level().getRandom().nextInt(50) == 0)
							this.shield.setAnimationTicks(i, 40);
						if (this.shield.getAnimationTicks(i) > 0) {
							this.shield.setAnimationTicks(i, this.shield.getAnimationTicks(i) - 1);
							if (this.shield.getAnimationTicks(i) == 20)
								this.shield.setAnimationTicks(i, 0);
						}
					}
					if (this.getEntityData().get(GROUND_ATTACK_STATE)) {
						if (this.groundAttackTicks < 20)
							this.groundAttackTicks++;
					} else {
						this.groundAttackTicks = 0;
					}
				}
			}
		} else {
			this.setFloating(true);
		}
	}

	@Override
	public void travel(Vec3 travelVector) {
		if (this.isFloating()) {
			if (this.isInWater()) {
				this.moveRelative(0.02F, travelVector);
				this.move(MoverType.SELF, this.getDeltaMovement());
				this.setDeltaMovement(this.getDeltaMovement().multiply(0.800000011920929D, 0.800000011920929D, 0.800000011920929D));
			} else {
				float friction = 0.91F;

				if (this.onGround()) {
					friction = this.level().getBlockState(BlockPos.containing(this.getX(), this.getBoundingBox().minY - 1, this.getZ())).getBlock().getFriction() * 0.91F;
				}

				float groundFriction = 0.16277136F / (friction * friction * friction);
				this.moveRelative(this.onGround() ? 0.1F * groundFriction : 0.02F, travelVector);
				friction = 0.91F;

				if (this.onGround()) {
					friction = this.level().getBlockState(BlockPos.containing(this.getX(), this.getBoundingBox().minY - 1, this.getZ())).getBlock().getFriction() * 0.91F;
				}

				this.move(MoverType.SELF, this.getDeltaMovement());
				this.setDeltaMovement(this.getDeltaMovement().multiply(friction, friction, friction));
			}

			this.calculateEntityAnimation(false);
		} else {
			this.setJumping(false);
			super.travel(Vec3.ZERO);
		}
	}

	@Override
	protected void tickDeath() {
		this.bossInfo.setProgress(0.0F);
		if (this.deathTicks == 0) {
			if (!this.level().isClientSide()) {
				final Vec3 anchorCenter = this.getAnchorCenter();
				this.level().playSound(null, BlockPos.containing(anchorCenter), SoundRegistry.FORTRESS_BOSS_TELEPORT.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
				this.setPos(anchorCenter.x, anchorCenter.y, anchorCenter.z);
				List<Wight> trackedEntities = this.level().getEntitiesOfClass(Wight.class, this.getBoundingBox().inflate(this.anchorRadius * 2, 512, this.anchorRadius * 2), entity -> entity.distanceToSqr(anchorCenter.x, entity.getY(), anchorCenter.z) > this.anchorRadius || Math.abs(entity.getY() - anchorCenter.y) > this.anchorRadius);
				for (Entity entity : trackedEntities) {
					if (entity instanceof Wight || entity instanceof PrimordialMalevolenceSpawner || entity instanceof PrimordialMalevolenceProjectile
						|| entity instanceof PrimordialMalevolenceTurret || entity instanceof PrimordialMalevolenceBlockade) {
						entity.discard();
					}
				}
			}
		}

		++this.deathTicks;

		this.getEntityData().set(SHIELD_ROTATION, (this.deathTicks / 3.0F) * (this.deathTicks / 3.0F));
		for (int i = 0; i <= 19; i++) {
			this.shield.setActive(i, i * (130.0F / 19.0F) > this.deathTicks);
		}
		this.getEntityData().set(SHIELD_STATE, this.shield.packActiveData());
		if (!this.level().isClientSide()) {
			if (this.deathTicks > 100 && this.deathTicks % 5 == 0) {
				int xp = 800;
				while (xp > 0) {
					int dropXP = ExperienceOrb.getExperienceValue(xp);
					xp -= dropXP;
					this.level().addFreshEntity(new ExperienceOrb(this.level(), this.getX(), this.getY() + this.getBbHeight() / 2.0D, this.getZ(), dropXP));
				}
			}

			if (this.deathTicks > 130) {
				int xp = 3000;
				while (xp > 0) {
					int dropXP = ExperienceOrb.getExperienceValue(xp);
					xp -= dropXP;
					this.level().addFreshEntity(new ExperienceOrb(this.level(), this.getX(), this.getY() + this.getBbHeight() / 2.0D, this.getZ(), dropXP));
				}

				for (int c = 0; c < 4; c++) {
					double yawAngle = Math.PI * 2.0D / 6;
					for (int i = 0; i < 6; i++) {
						Vec3 dir = new Vec3(Math.sin(yawAngle * i), (c - 2) / 4.0D * 2.0D, Math.cos(yawAngle * i));
						dir = dir.normalize();
						float speed = 0.8F;
						PrimordialMalevolenceProjectile bullet = new PrimordialMalevolenceProjectile(this.level(), this);
						bullet.moveTo(this.getX(), this.getY() + 0.5D, this.getZ(), 0, 0);
						bullet.shoot(dir.x, dir.y, dir.z, speed, 0.0F);
						this.level().addFreshEntity(bullet);
					}
				}

				List<LocationStorage> locations = LocationStorage.getLocations(this.level(), this.position());
				for (LocationStorage location : locations) {
					if (location.getType() == EnumLocationType.WIGHT_TOWER) {
						if (location.getGuard() != null) {
							location.getGuard().clear(this.level());
							location.setDirty(true);
						}
					}
				}

				this.discard();
			}
		}
	}

	@Override
	public boolean canUsePortal(boolean allowPassengers) {
		return false;
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return false;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.FORTRESS_BOSS_LIVING.get();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundRegistry.FORTRESS_BOSS_HURT.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.FORTRESS_BOSS_DEATH.get();
	}

	@Override
	public @Nullable SoundEvent getMusicFile(Player listener) {
		return SoundRegistry.FORTRESS_BOSS_LOOP.get();
	}

	@Override
	public double getMusicRange(Player listener) {
		return 20.0D;
	}

	@Override
	public boolean isMusicActive(Player listener) {
		return this.isAlive();
	}

	@Override
	public int getMusicLayer(Player listener) {
		return EntityMusicLayers.BOSS;
	}

	@Override
	public BetweenlandsServerBossBar getBar() {
		return this.bossInfo;
	}
}
