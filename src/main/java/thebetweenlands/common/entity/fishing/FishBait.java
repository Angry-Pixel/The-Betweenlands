package thebetweenlands.common.entity.fishing;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidType;
import thebetweenlands.common.component.item.FishBaitStats;
import thebetweenlands.common.entity.fishing.anadia.Anadia;
import thebetweenlands.common.registries.EntityRegistry;

import java.util.List;

public class FishBait extends ItemEntity {

	private static final EntityDataAccessor<Integer> SINK_SPEED = SynchedEntityData.defineId(FishBait.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> DISSOLVE_TIME = SynchedEntityData.defineId(FishBait.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> RANGE = SynchedEntityData.defineId(FishBait.class, EntityDataSerializers.INT);

	public FishBait(EntityType<? extends ItemEntity> type, Level level) {
		super(type, level);
	}

	public FishBait(Level level, double posX, double posY, double posZ, ItemStack stack) {
		this(level, posX, posY, posZ, stack, level.getRandom().nextDouble() * 0.2D - 0.1D, 0.2D, level.getRandom().nextDouble() * 0.2D - 0.1D);
	}

	public FishBait(Level level, double posX, double posY, double posZ, ItemStack stack, double deltaX, double deltaY, double deltaZ) {
		this(EntityRegistry.FISH_BAIT.get(), level);
		this.setPos(posX, posY, posZ);
		this.setDeltaMovement(deltaX, deltaY, deltaZ);
		this.setItem(stack);
		this.lifespan = stack.getEntityLifespan(level);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(SINK_SPEED, 3);
		builder.define(DISSOLVE_TIME, 200);
		builder.define(RANGE, 1);
	}

	@Override
	public void tick() {
		super.tick();

		if (!this.level().isClientSide())
			if (this.tickCount >= getBaitDissolveTime() && !this.hasPickUpDelay())
				this.discard();

		if (this.isInWater()) {
			this.setDeltaMovement(this.getDeltaMovement().multiply(0.9D, 1.0D, 0.9D).subtract(0.0D, this.getBaitSinkSpeed() * 0.02D, 0.0D));
			if (!this.level().isClientSide()) {
				if (!this.isNoGravity())
					this.setNoGravity(true);
				if (this.getBaitRange() >= 1)
					this.lureCloseFish();
			}
		} else {
			if (!this.level().isClientSide())
				if (this.isNoGravity())
					this.setNoGravity(false);
		}
	}

	public void lureCloseFish() {
		List<Anadia> list = this.level().getEntitiesOfClass(Anadia.class, new AABB(this.blockPosition()).inflate(0.125F).inflate(this.getBaitRange() * 0.5D), anadia -> anadia.isInWater() && anadia.hasLineOfSight(this));
		if (!list.isEmpty()) {
			Anadia foundFish = list.getFirst();
			if (foundFish.findBaitGoal != null) {
				foundFish.findBaitGoal.bait = this;
				foundFish.findBaitGoal.tick();
			}
		}
	}

	@Override
	public boolean isPushedByFluid(FluidType type) {
		return false;
	}

	public void setStats(FishBaitStats stats) {
		this.setBaitSinkSpeed(stats.sinkSpeed());
		this.setBaitDissolveTime(stats.dissolveTime());
		this.setBaitRange(stats.range());
		this.setGlowingTag(stats.glowing());
	}

	public int getBaitSinkSpeed() {
		return this.getEntityData().get(SINK_SPEED);
	}

	public void setBaitSinkSpeed(int weight) {
		this.getEntityData().set(SINK_SPEED, weight);
	}

	public int getBaitDissolveTime() {
		return this.getEntityData().get(DISSOLVE_TIME);
	}

	public void setBaitDissolveTime(int life) {
		this.getEntityData().set(DISSOLVE_TIME, life);
	}

	public int getBaitRange() {
		return this.getEntityData().get(RANGE);
	}

	public void setBaitRange(int range) {
		this.getEntityData().set(RANGE, range);
	}

	public Vec3 getMovementToShoot(double x, double y, double z, float velocity, float inaccuracy) {
		return new Vec3(x, y, z).normalize().add(
				this.getRandom().triangle(0.0D, 0.0172275D * inaccuracy),
				this.getRandom().triangle(0.0D, 0.0172275D * inaccuracy),
				this.getRandom().triangle(0.0D, 0.0172275D * inaccuracy)).scale(velocity);
	}

	public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
		Vec3 vec3 = this.getMovementToShoot(x, y, z, velocity, inaccuracy);
		this.setDeltaMovement(vec3);
		this.hasImpulse = true;
		double d0 = vec3.horizontalDistance();
		this.setYRot((float)(Mth.atan2(vec3.x, vec3.z) * Mth.RAD_TO_DEG));
		this.setXRot((float)(Mth.atan2(vec3.y, d0) * Mth.RAD_TO_DEG));
		this.yRotO = this.getYRot();
		this.xRotO = this.getXRot();
	}

	public void shootFromRotation(Entity shooter, float x, float y, float z, float velocity, float inaccuracy) {
		float f = -Mth.sin(y * Mth.DEG_TO_RAD) * Mth.cos(x * Mth.DEG_TO_RAD);
		float f1 = -Mth.sin((x + z) * Mth.DEG_TO_RAD);
		float f2 = Mth.cos(y * Mth.DEG_TO_RAD) * Mth.cos(x * Mth.DEG_TO_RAD);
		this.shoot(f, f1, f2, velocity, inaccuracy);
		Vec3 vec3 = shooter.getKnownMovement();
		this.setDeltaMovement(this.getDeltaMovement().add(vec3.x, shooter.onGround() ? 0.0 : vec3.y, vec3.z));
	}
}
