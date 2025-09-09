package thebetweenlands.common.entity.boss.malevolence;

import java.util.*;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.component.entity.PuppetData;
import thebetweenlands.common.component.entity.PuppeteerData;
import thebetweenlands.common.entity.BLEntity;
import thebetweenlands.common.entity.boss.PrimordialMalevolence;
import thebetweenlands.common.registries.AttachmentRegistry;
import thebetweenlands.common.registries.EntityRegistry;

public class PrimordialMalevolenceBlockade extends Mob implements OwnableEntity, BLEntity, IEntityWithComplexSpawn {
	protected static final EntityDataAccessor<Optional<UUID>> OWNER = SynchedEntityData.defineId(PrimordialMalevolenceBlockade.class, EntityDataSerializers.OPTIONAL_UUID);
	protected static final EntityDataAccessor<Float> SIZE = SynchedEntityData.defineId(PrimordialMalevolenceBlockade.class, EntityDataSerializers.FLOAT);
	protected static final EntityDataAccessor<Float> ROTATION = SynchedEntityData.defineId(PrimordialMalevolenceBlockade.class, EntityDataSerializers.FLOAT);

	private float prevRotation = 0.0F;
	private float rotation = 0.0F;
	private int despawnTicks = 0;
	private int maxDespawnTicks = 160;

	public PrimordialMalevolenceBlockade(EntityType<? extends Mob> type, Level level) {
		super(type, level);
		this.prevRotation = this.rotation = level.getRandom().nextFloat() * 360.0f;
	}

	public PrimordialMalevolenceBlockade(Level level, Entity source) {
		this(EntityRegistry.PRIMORDIAL_MALEVOLENCE_BLOCKADE.get(), level);
		this.setOwner(source.getUUID());
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.0D);
	}

	public void setOwner(@Nullable UUID uuid) {
		this.getEntityData().set(OWNER, Optional.ofNullable(uuid));
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(OWNER, Optional.empty());
		builder.define(SIZE, 1.0F);
		builder.define(ROTATION, this.rotation);
	}

	@Nullable
	@Override
	public UUID getOwnerUUID() {
		return this.getEntityData().get(OWNER).orElse(null);
	}

	public void setTriangleSize(float size) {
		this.getEntityData().set(SIZE, size);
		if (this.level().isClientSide()) {
			this.refreshDimensions();
		}
	}

	@Override
	protected EntityDimensions getDefaultDimensions(Pose pose) {
		return EntityDimensions.scalable(this.getEntityData().get(SIZE) * 2, this.getType().getHeight());
	}

	public float getTriangleSize() {
		return this.getEntityData().get(SIZE);
	}

	public void setMaxDespawnTicks(int ticks) {
		this.maxDespawnTicks = ticks;
	}

	public int getMaxDespawnTicks() {
		return this.maxDespawnTicks;
	}

	public int getDespawnTicks() {
		return this.despawnTicks;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putFloat("triangleSize", this.getTriangleSize());
		compound.putFloat("triangleRotation", this.getEntityData().get(ROTATION));
		compound.putInt("despawnTicks", this.despawnTicks);
		compound.putInt("maxDespawnTicks", this.maxDespawnTicks);
		if (this.getOwnerUUID() != null) {
			compound.putUUID("owner", this.getOwnerUUID());
		}
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		this.setTriangleSize(compound.getFloat("triangleSize"));
		this.getEntityData().set(ROTATION, compound.getFloat("triangleRotation"));
		this.despawnTicks = compound.getInt("despawnTicks");
		this.maxDespawnTicks = compound.getInt("maxDespawnTicks");
		if (compound.hasUUID("owner")) {
			this.getEntityData().set(OWNER, Optional.of(compound.getUUID("owner")));
		} else {
			this.getEntityData().set(OWNER, Optional.empty());
		}
	}

	protected boolean isPlayerControlled() {
		return this.getOwner() instanceof Player;
	}

	@Override
	public void tick() {
		if (!this.level().isClientSide() && (this.level().getDifficulty() == Difficulty.PEACEFUL || (this.getOwner() != null && !this.getOwner().isAlive()))) {
			this.discard();
			return;
		}

		this.setTriangleSize(this.getTriangleSize());

		this.setDeltaMovement(Vec3.ZERO);

		super.tick();

		if (!this.level().isClientSide()) {
			this.despawnTicks++;
			if (this.despawnTicks >= this.getMaxDespawnTicks()) {
				this.discard();
			}

			this.rotation += 1.0F;
			this.getEntityData().set(ROTATION, this.rotation);

			List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(this.getTriangleSize() * 2, 0, this.getTriangleSize() * 2));
			for (LivingEntity target : targets) {
				if (target != this.getOwner()) {
					Vec3[] vertices = this.getTriangleVertices(1);

					if (PrimordialMalevolence.rayTraceTriangle(new Vec3(target.getX() - this.getX(), 1, target.getZ() - this.getZ()), new Vec3(0, -2, 0), vertices[0], vertices[1], vertices[2])) {

						if (this.isPlayerControlled()) {
							Optional<PuppetData> cap = target.getExistingData(AttachmentRegistry.PUPPET);

							Entity owner = this.getOwner();

							if (cap.isPresent() && cap.get().getPuppeteer(target) == owner && owner instanceof Player player) {

								Optional<PuppeteerData> playerCap = player.getExistingData(AttachmentRegistry.PUPPETEER);
								if (playerCap.isPresent()) {
									float healthPercent = 0.3f;

									if (target.getHealth() * (1 - healthPercent) > 5.0f) {
										float healthCost = target.getHealth() * healthPercent;
										float prevHealth = target.getHealth();

										if (target.hurt(this.damageSources().magic(), healthCost) && (prevHealth - target.getHealth()) >= healthCost * 0.5f) {
											List<Integer> indices = new ArrayList<>();
											for (int i = 0; i < 20; i++) {
												indices.add(i);
											}
											Collections.shuffle(indices);

											for (int index : indices) {
												if (playerCap.get().checkAndActivateShield(index)) {
													break;
												}
											}

											this.maxDespawnTicks = this.despawnTicks + 8;
										}
									}
								}
							}
						} else if (target instanceof Player) {
							float damage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
							if (target.hurt(this.damageSources().magic(), damage) && this.getOwner() != null && this.getOwner() instanceof LivingEntity owner) {
								if (owner.getHealth() < owner.getMaxHealth() - damage) {
									owner.heal(damage * 3.0F);
								}
							}
						}

					}
				}
			}
		} else {
			this.prevRotation = this.rotation;
			this.rotation = this.getEntityData().get(ROTATION);

			for (int c = 0; c < 4; c++) {
				float r1 = this.level().getRandom().nextFloat();
				float r2 = this.level().getRandom().nextFloat();
				this.rotation += 15;
				Vec3[] vertices = this.getTriangleVertices(1);
				this.rotation -= 15;
				double xc = 0, zc = 0;
				for (int i = 0; i < 3; i++) {
					Vec3 vertex = vertices[i];
					switch (i) {
						default:
						case 0:
							xc += vertex.x * (1 - Math.sqrt(r1));
							zc += vertex.z * (1 - Math.sqrt(r1));
							break;
						case 1:
							xc += (Math.sqrt(r1) * (1 - r2)) * vertex.x;
							zc += (Math.sqrt(r1) * (1 - r2)) * vertex.z;
							break;
						case 2:
							xc += (Math.sqrt(r1) * r2) * vertex.x;
							zc += (Math.sqrt(r1) * r2) * vertex.z;
							break;
					}
				}
				Vec3 rp = new Vec3(xc, vertices[0].y, zc);

				double sx = this.getX() + rp.x;
				double sy = this.getY() + rp.y + 4;
				double sz = this.getZ() + rp.z;
				double ex = this.getX() + rp.x;
				double ey = this.getY() + rp.y;
				double ez = this.getZ() + rp.z;

				if (this.getOwner() != null) {
					sx = this.getOwner().getX();
					sy = this.getOwner().getBoundingBox().minY + (this.getOwner().getBoundingBox().maxY - this.getOwner().getBoundingBox().minY) / 2.0D;
					sz = this.getOwner().getZ();
				}

				if (!this.isPlayerControlled()) {
					this.level().addParticle(ParticleTypes.PORTAL, sx, sy, sz, ex - sx, ey - sy, ez - sz);
				}
			}
		}
	}

	@Override
	public void travel(Vec3 travelVector) {
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
	}

	public Vec3[] getTriangleVertices(float partialTicks) {
		Vec3[] vertices = new Vec3[3];
		double rot = Math.toRadians(this.prevRotation + (this.rotation - this.prevRotation) * partialTicks);
		double angle = Math.PI * 2.0D / 3.0D;
		for (int i = 0; i < 3; i++) {
			double sin = Math.sin(angle * i + rot);
			double cos = Math.cos(angle * i + rot);
			vertices[i] = new Vec3(sin * this.getTriangleSize(), 0, cos * this.getTriangleSize());
		}
		return vertices;
	}

	@Override
	public boolean isInvulnerableTo(DamageSource source) {
		//TODO
		return !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY) /*&& !(source.getDirectEntity() instanceof ShockwaveBlock)*/;
	}

	@Override
	public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
		buffer.writeFloat(this.rotation);
	}

	@Override
	public void readSpawnData(RegistryFriendlyByteBuf buffer) {
		this.prevRotation = this.rotation = buffer.readFloat();
		this.getEntityData().set(ROTATION, this.rotation);
	}
}
