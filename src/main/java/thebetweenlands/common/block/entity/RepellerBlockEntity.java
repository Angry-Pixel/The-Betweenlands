package thebetweenlands.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;
import thebetweenlands.common.entities.EntityWight;
import thebetweenlands.common.registries.BlockEntityRegistry;

import java.util.List;

public class RepellerBlockEntity extends SyncedBlockEntity {

	private static final float MAX_RADIUS = 18.0F;
	private static final int DEPLOY_TIME = 80;

	protected boolean hasShimmerstone = false;
	protected int fuel = 0;
	protected boolean running = false;

	private boolean prevRunning = false;
	private float lastRadius = 0.0F;
	private float radius = 0.0F;
	private int deployTicks = 0;
	private int radiusState = 0;
	private float accumulatedCost = 0.0F;

	public int renderTicks = 0;

	public RepellerBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityRegistry.REPELLER.get(), pos, state);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, RepellerBlockEntity entity) {
		if (!level.isClientSide()) {
			if (entity.fuel > 0) {
				if (entity.fuel <= 0) {
					entity.fuel = 0;
					entity.setChanged();
				}
			}
			if (entity.fuel > 0 && entity.hasShimmerstone) {
				if (!entity.running) {
					entity.running = true;
					level.sendBlockUpdated(pos, state, state, 2);
					entity.setChanged();
				}
			} else if (entity.fuel <= 0 || !entity.hasShimmerstone) {
				if (entity.running) {
					entity.running = false;
					level.sendBlockUpdated(pos, state, state, 2);
					entity.setChanged();
				}
			}
			if (entity.fuel < 0) {
				entity.fuel = 0;
				level.sendBlockUpdated(pos, state, state, 2);
				entity.setChanged();
			} else {
				float fuelCost = 0;
				double centerX = pos.getX() + 0.5F;
				double centerY = pos.getY() + 1.15F;
				double centerZ = pos.getZ() + 0.5F;
				AABB affectedBB = new AABB(pos).inflate(entity.radius + 5.0D);
				List<Entity> affectedEntities = level.getEntitiesOfClass(Entity.class, affectedBB);
				for (Entity selected : affectedEntities) {
					if (selected instanceof Enemy && !(selected instanceof EntityWight) && !selected.getType().is(Tags.EntityTypes.BOSSES)) {
						Vec3 closestPoint = entity.getClosestAABBCorner(selected.getBoundingBox(), centerX, centerY, centerZ);
						if (closestPoint.distanceToSqr(centerX, centerY, centerZ) < entity.radius * entity.radius) {
							double diffX = selected.getX() - centerX;
							double diffY = selected.getY() - centerY;
							double diffZ = selected.getZ() - centerZ;
							selected.move(MoverType.PISTON, new Vec3(diffX * 0.1F, 0.0F, diffZ * 0.1F));
							double len = Math.sqrt(diffX * diffX + diffY * diffY + diffZ * diffZ);
							double speed = (entity.radius - len) / entity.radius * 1.5F + 0.5F;
							selected.setDeltaMovement((float) (diffX / len) * speed, selected.getDeltaMovement().y(), (float) (diffZ / len) * speed);
							if (selected instanceof LivingEntity living) {
								living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10, 6));
							}
							if (!selected.horizontalCollision) {
								fuelCost += 0.00028F * (entity.radiusState / 1.5F + 1);
							}
						}
					}
					if (selected instanceof Projectile || selected instanceof Fireball) {
						Vec3 closestPoint = entity.getClosestAABBCorner(selected.getBoundingBox(), centerX, centerY, centerZ);
						if (closestPoint.distanceToSqr(centerX, centerY, centerZ) < entity.radius * entity.radius) {
							double velocity = Math.sqrt(selected.getDeltaMovement().x() * selected.getDeltaMovement().x() + selected.getDeltaMovement().y() * selected.getDeltaMovement().y() + selected.getDeltaMovement().z() * selected.getDeltaMovement().z());
							double diffX = selected.getX() - centerX;
							double diffY = selected.getY() - centerY;
							double diffZ = selected.getZ() - centerZ;
							double len = Math.sqrt(diffX * diffX + diffY * diffY + diffZ * diffZ);
							selected.move(MoverType.PISTON, new Vec3(diffX * 0.1F, 0.0F, diffZ * 0.1F));
							selected.setDeltaMovement((float) (diffX / len) * velocity, (float) (diffY / len) * velocity, (float) (diffZ / len) * velocity);
							if (selected instanceof Projectile projectile) {
								projectile.shoot(diffX / len, diffY / len, diffZ / len, 1.0F, 1.0F);
							}
							selected.hasImpulse = true;
							if (!selected.horizontalCollision && !selected.verticalCollision && !selected.onGround()) {
								fuelCost += 0.0004F * (entity.radiusState / 1.5F + 1);
							}
						}
					}
				}

				boolean fuelConsumed = false;

				//Limit fuel cost per tick
				entity.accumulatedCost += Math.min(fuelCost, 0.00125F) * 1000;
				while (entity.accumulatedCost > 1.0F) {
					entity.accumulatedCost -= 1.0F;
					entity.fuel--;
					fuelConsumed = true;
				}

				if (fuelConsumed) {
					level.sendBlockUpdated(pos, state, state, 2);
				}

				entity.setChanged();
			}
		} else {
			entity.renderTicks++;
		}

		if (entity.prevRunning != entity.running) {
			entity.deployTicks = 0;
			entity.setChanged();
		}
		entity.prevRunning = entity.running;

		float desiredRadius = MAX_RADIUS / 4.0F * (entity.radiusState + 1);
		entity.lastRadius = entity.radius;
		if (entity.running && entity.radius < desiredRadius && entity.deployTicks < DEPLOY_TIME) {
			entity.deployTicks++;
			entity.radius = (float) entity.easeInOut(entity.deployTicks, entity.radius, desiredRadius, DEPLOY_TIME);
			if (entity.radius > desiredRadius) {
				entity.radius = desiredRadius;
			}
			entity.setChanged();
		} else if ((!entity.running && entity.radius > 0.0F) || entity.radius > desiredRadius) {
			entity.deployTicks++;
			entity.radius = (float) entity.easeInOut(entity.deployTicks, !entity.running ? desiredRadius : entity.radius, -desiredRadius, DEPLOY_TIME);
			if (!entity.running && entity.radius < 0.0F) {
				entity.radius = 0.0F;
			} else if (entity.running && entity.radius < desiredRadius) {
				entity.radius = desiredRadius;
			}
			entity.setChanged();
		}
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		tag.putInt("fuel", this.fuel);
		tag.putBoolean("has_shimmerstone", this.hasShimmerstone);
		tag.putInt("deploy_ticks", this.deployTicks);
		tag.putFloat("radius", this.radius);
		tag.putBoolean("running", this.running);
		tag.putInt("radius_state", this.radiusState);
		tag.putFloat("accumulated_cost", this.accumulatedCost);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		this.fuel = tag.getInt("fuel");
		this.hasShimmerstone = tag.getBoolean("has_shimmerstone");
		this.deployTicks = tag.getInt("deploy_ticks");
		this.radius = tag.getFloat("radius");
		this.running = tag.getBoolean("running");
		this.prevRunning = this.running;
		this.radiusState = tag.getInt("radius_state");
		this.accumulatedCost = tag.getFloat("accumulated_cost");
	}

	public void setRadiusState(int state) {
		state = state % 4;
		if (state != this.radiusState) {
			if (this.running) {
				this.deployTicks = 0;
			}
			this.radiusState = state;
			this.setChanged();
			this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 2);
		}
	}

	public int getRadiusState() {
		return this.radiusState;
	}

	public void cycleRadiusState() {
		this.radiusState = (this.radiusState + 1) % 4;
		if (this.running)
			this.deployTicks = 0;
		this.setChanged();
		this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 2);
	}

	public void addShimmerstone() {
		if (!this.hasShimmerstone) {
			this.hasShimmerstone = true;
			this.setChanged();
			this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 2);
		}
	}

	public boolean hasShimmerstone() {
		return this.hasShimmerstone;
	}

	public void setShimmerstone(boolean shimmerstone) {
		this.hasShimmerstone = shimmerstone;
	}

	public void removeShimmerstone() {
		if (this.hasShimmerstone) {
			this.hasShimmerstone = false;
			this.setChanged();
			this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 2);
		}
	}

	public int getMaxFuel() {
		return 10000;
	}

	public int addFuel(int amount) {
		if (amount != 0) {
			int canAdd = this.getMaxFuel() - this.fuel;
			if (canAdd > 0) {
				int added = Math.min(canAdd, amount);
				this.fuel += added;
				this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 2);
				this.setChanged();
				return added;
			}
		}
		return 0;
	}

	public int removeFuel(int amount) {
		int removed = Math.min(this.fuel, amount);
		if (amount != 0) {
			this.fuel -= amount;
			this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 2);
			this.setChanged();
		}
		return removed;
	}

	public int getFuel() {
		return this.fuel;
	}

	public void setFuel(int fuel) {
		this.fuel = fuel;
	}

	public void emptyFuel() {
		this.fuel = 0;
	}

	public boolean isRunning() {
		return this.running || this.radius > 0.0F;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public float getRadius(float partialTicks) {
		return this.lastRadius + (this.radius - this.lastRadius) * partialTicks;
	}

	protected Vec3 getClosestAABBCorner(AABB bb, double centerX, double centerY, double centerZ) {
		Vec3 center = new Vec3(centerX, centerY, centerZ);
		Vec3 closest = null;
		for (int bcx = 0; bcx <= 1; bcx++) {
			for (int bcy = 0; bcy <= 1; bcy++) {
				for (int bcz = 0; bcz <= 1; bcz++) {
					double cx = bcx == 1 ? bb.maxX : bb.minX;
					double cy = bcy == 1 ? bb.maxY : bb.minY;
					double cz = bcz == 1 ? bb.maxZ : bb.minZ;
					Vec3 current = new Vec3(cx, cy, cz);
					if (closest == null || current.distanceTo(center) < closest.distanceTo(center)) {
						closest = current;
					}
				}
			}
		}
		return closest;
	}

	protected double easeInOut(float t, float b, float c, float d) {
		t /= d / 2;
		if (t < 1) return c / 2 * t * t * t * t * t + b;
		t -= 2;
		return c / 2 * (t * t * t * t * t + 2) + b;
	}
}
