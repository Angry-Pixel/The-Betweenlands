package thebetweenlands.common.entities.fishing;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import thebetweenlands.common.entities.fishing.anadia.Anadia;
import thebetweenlands.common.items.BLFishingRodItem;
import thebetweenlands.common.registries.EntityRegistry;

public class BLFishHook extends FishingHook implements IEntityWithComplexSpawn {

	private static final EntityDataAccessor<Boolean> IS_BAITED = SynchedEntityData.defineId(BLFishHook.class, EntityDataSerializers.BOOLEAN);

	protected double interpTargetX;
	protected double interpTargetY;
	protected double interpTargetZ;
	protected int newPosRotationIncrements;

	public BLFishHook(EntityType<? extends FishingHook> type, Level level) {
		super(type, level);
		this.noCulling = true;
	}

	public BLFishHook(Player player, Level level) {
		this(EntityRegistry.FISH_HOOK.get(), level);
		this.setOwner(player);
		player.fishing = this;
		this.shoot(player);
	}

	public void shoot(Player player) {
		float f = player.getXRot();
		float f1 = player.getYRot();
		float f2 = Mth.cos(-f1 * Mth.DEG_TO_RAD - Mth.PI);
		float f3 = Mth.sin(-f1 * Mth.DEG_TO_RAD - Mth.PI);
		float f4 = -Mth.cos(-f * Mth.DEG_TO_RAD);
		float f5 = Mth.sin(-f * Mth.DEG_TO_RAD);
		double d0 = player.getX() - (double) f3 * 0.3D;
		double d1 = player.getEyeY();
		double d2 = player.getZ() - (double) f2 * 0.3D;
		this.moveTo(d0, d1, d2, f1, f);
		Vec3 vec3 = new Vec3(-f3, Mth.clamp(-(f5 / f4), -5.0F, 5.0F), -f2);
		double d3 = vec3.length();
		vec3 = vec3.multiply(0.6D / d3 + this.random.triangle(0.5D, 0.0103365D), 0.6D / d3 + this.random.triangle(0.5D, 0.0103365D), 0.6D / d3 + this.random.triangle(0.5D, 0.0103365D));
		this.setDeltaMovement(vec3);
		this.setYRot((float) (Mth.atan2(vec3.x, vec3.z) * Mth.RAD_TO_DEG));
		this.setXRot((float) (Mth.atan2(vec3.y, vec3.horizontalDistance()) * Mth.RAD_TO_DEG));
		this.yRotO = this.getYRot();
		this.xRotO = this.getXRot();
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(IS_BAITED, false);
	}

	@Override
	public void lerpTo(double x, double y, double z, float yRot, float xRot, int steps) {
		this.interpTargetX = x;
		this.interpTargetY = y;
		this.interpTargetZ = z;
		this.newPosRotationIncrements = steps;
	}

	@Override
	public void tick() {
		if (!this.level().isClientSide())
			this.setSharedFlag(6, this.isCurrentlyGlowing());

		if (this.tickCount < 2) {
			//Stupid EntityTrackerEntry is broken and desyncs server position.
			//Tracker updates server side position but *does not* send the change to the client
			//when tracker.updateCounter == 0, causing a desync until the next force teleport
			//packet.......
			//By not moving the entity until then it works.
			return;
		}

		this.baseTick();

		if (this.currentState != FishHookState.FLYING /*for smooth throwing*/ && this.newPosRotationIncrements > 0 && !this.isControlledByLocalInstance()) {
			double d0 = this.getX() + (this.interpTargetX - this.getX()) / (double) this.newPosRotationIncrements;
			double d1 = this.getY() + (this.interpTargetY - this.getY()) / (double) this.newPosRotationIncrements;
			double d2 = this.getZ() + (this.interpTargetZ - this.getZ()) / (double) this.newPosRotationIncrements;
			--this.newPosRotationIncrements;
			this.setPos(d0, d1, d2);
		}

		Player angler = this.getPlayerOwner();

		if (angler == null || angler.fishing != this) {
			this.discard();
		} else if (this.level().isClientSide() || !this.shouldStopFishing()) {
			if (this.onGround()) {
				this.life++;
				if (this.life >= 1200) {
					this.discard();
					return;
				}
			} else {
				this.life = 0;
			}

			float f = 0.0F;
			FluidState fluidstate = this.level().getFluidState(this.blockPosition());
			if (fluidstate.is(FluidTags.WATER)) {
				f = fluidstate.getHeight(this.level(), this.blockPosition());
			}

			if (this.currentState == FishHookState.FLYING) {
				if (this.getHookedIn() != null) {
					this.setDeltaMovement(Vec3.ZERO);
					this.currentState = FishHookState.HOOKED_IN_ENTITY;
					return;
				}

				if (f > 0.0F) {
					this.setDeltaMovement(this.getDeltaMovement().multiply(0.3D, 0.2D, 0.3D));
					this.currentState = FishHookState.BOBBING;
					return;
				}

				this.checkCollision();
			} else {
				if (this.currentState == FishHookState.HOOKED_IN_ENTITY) {
					if (this.getHookedIn() != null) {
						if (!this.getHookedIn().isRemoved() && this.getHookedIn().level().dimension() == this.level().dimension()) {
							this.setPos(this.getHookedIn().getX(), this.getHookedIn().getY(0.8), this.getHookedIn().getZ());
						} else {
							this.setHookedEntity(null);
							this.currentState = FishHookState.FLYING;
						}
					}
					return;
				}

				if (!this.level().isClientSide() && this.currentState == FishHookState.BOBBING) {
					//Bobbing is random so only do it on server side to stay in sync
					Vec3 vec3 = this.getDeltaMovement();
					double d0 = this.getY() + vec3.y - this.blockPosition().getY() - (double)f;
					if (Math.abs(d0) < 0.01) {
						d0 += Math.signum(d0) * 0.1;
					}

					this.setDeltaMovement(vec3.x() * 0.9D, vec3.y() - d0 * this.getRandom().nextFloat() * 0.2D, vec3.z() * 0.9D);

				}
			}

			if (!fluidstate.is(FluidTags.WATER)) {
				this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.03, 0.0));
			}

			this.move(MoverType.SELF, this.getDeltaMovement());
			this.updateRotation();
			this.setDeltaMovement(this.getDeltaMovement().scale(0.92));
			this.reapplyPosition();
		}
	}

	private boolean shouldStopFishing() {
		Player angler = this.getPlayerOwner();

		if(angler == null) {
			return true;
		}

		ItemStack stack = angler.getMainHandItem();
		ItemStack stack1 = angler.getOffhandItem();
		boolean mainHandHeld = stack.getItem() instanceof BLFishingRodItem;
		boolean offHandHeld = stack1.getItem() instanceof BLFishingRodItem;

		if (!angler.isRemoved() && angler.isAlive() && (mainHandHeld || offHandHeld) && !(this.distanceToSqr(angler) > 1024.0)) {
			return false;
		} else {
			this.discard();
			return true;
		}
	}

	public void setBaited(boolean hasBait) {
		this.getEntityData().set(IS_BAITED, hasBait);
	}

	public boolean getBaited() {
		return this.getEntityData().get(IS_BAITED);
	}

	@Override
	public int retrieve(ItemStack stack) {
		if (!this.level().isClientSide() && this.getPlayerOwner() != null) {
			int i = 1;

			if (this.getHookedIn() == null) {
				this.pullEntity(this.getHookedIn());
				this.level().broadcastEntityEvent(this, (byte)31);
			}

			if (this.getHookedIn() != null) {
				this.pullEntity(this.getHookedIn());
				this.level().broadcastEntityEvent(this, (byte)31);

				if(this.getHookedIn() instanceof Anadia anadia) {
					i = (int) Math.floor(anadia.getStrengthModifier() + 0.5D);
				}
			}

			if (this.onGround()) {
				i = 2;
			}

			return i;
		} else {
			return 0;
		}
	}

	@Override
	protected void pullEntity(Entity entity) {
		Player angler = this.getPlayerOwner();

		if (angler != null) {
			double d0 = angler.getX() - this.getX();
			double d1 = angler.getY() - this.getY();
			double d2 = angler.getZ() - this.getZ();

			if (this.getHookedIn() instanceof Anadia anadia) {
				if(anadia.getStaminaTicks() > 0) {
					if(!anadia.isObstructed()) {
						anadia.setStaminaTicks(anadia.getStaminaTicks() - 2);
					}

					if(anadia.isObstructed()) {
						anadia.setEscapeTicks(anadia.getEscapeTicks() -16);
					}

					if(anadia.isTreasureFish() && anadia.isObstructedTreasure()) {
						if(!anadia.getTreasureUnlocked()) {
							anadia.playTreasureCollectedSound(angler);
						}
						anadia.setAsLootFish(true); //does the loot table changes
					}

					if (anadia.getStaminaTicks() % 40 == 0) {
						// consumes a full shank of hunger every 2 seconds or so whilst the fish has stamina
						angler.getFoodData().addExhaustion(8);
					}
				}

				this.getHookedIn().setDeltaMovement(this.getHookedIn().getDeltaMovement().add(
					d0 * (0.045D - anadia.getStrengthModifier() * 0.005D),
					d1 * (0.045D - anadia.getStrengthModifier() * 0.005D),
					d2 * (0.045D - anadia.getStrengthModifier() * 0.005D)
				));
			} else if(this.getHookedIn() != null) {
				this.getHookedIn().setDeltaMovement(this.getHookedIn().getDeltaMovement().add(d0 * 0.02D, d1 * 0.02D, d2 * 0.02D));
			} else {
				this.setDeltaMovement(this.getDeltaMovement().add(d0 * 0.06D, d1 * 0.06D, d2 * 0.06D));
			}
		}
	}

	@Override
	public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
		Player angler = this.getPlayerOwner();
		buffer.writeBoolean(angler != null);
		if (angler != null) {
			buffer.writeInt(angler.getId());
		}
	}

	@Override
	public void readSpawnData(RegistryFriendlyByteBuf buf) {
		if (buf.readBoolean()) {
			int entityId = buf.readInt();
			Entity angler = this.level().getEntity(entityId);
			if (angler instanceof Player player) {
				player.fishing = this;
				this.setOwner(player);
			} else {
				this.setOwner(null);
			}
		}
	}
}
