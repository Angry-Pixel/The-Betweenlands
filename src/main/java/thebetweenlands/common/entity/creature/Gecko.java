package thebetweenlands.common.entity.creature;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import javax.annotation.Nullable;

import thebetweenlands.api.entity.WeedwoodBushPassableEntity;
import thebetweenlands.common.entity.BLEntity;
import thebetweenlands.common.entity.ai.goals.GeckoHideFromRainGoal;
import thebetweenlands.common.network.clientbound.WeedwoodBushRustlePacket;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;

import java.util.List;

public class Gecko extends Animal implements BLEntity, WeedwoodBushPassableEntity {

	private static final int MIN_HIDE_TIME = 20 * 60 * 2;
	private static final float UNHIDE_CHANCE = 0.1F;
	private static final int PLAYER_MIN_DISTANCE = 7;

	public static final EntityDataAccessor<Boolean> HIDING = SynchedEntityData.defineId(Gecko.class, EntityDataSerializers.BOOLEAN);
	@Nullable
	private BlockPos hidingBush;
	private int timeHiding;

	public Gecko(EntityType<? extends Animal> type, Level level) {
		super(type, level);
		this.setPathfindingMalus(PathType.WATER, 0.0F);
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Gecko.createMobAttributes()
			.add(Attributes.MAX_HEALTH, 12.0D)
			.add(Attributes.MOVEMENT_SPEED, 0.5D);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new PanicGoal(this, 1.0D));
		//TODO tempt tag
		this.goalSelector.addGoal(2, new TemptGoal(this, 0.5D, stack -> stack.is(ItemRegistry.SAP_SPIT), true));
		this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Player.class, PLAYER_MIN_DISTANCE, 0.65D, 1.0D));
		this.goalSelector.addGoal(4, new GeckoHideFromRainGoal(this, 0.65D));
		this.goalSelector.addGoal(6, new RandomStrollGoal(this, 0.6D));
		this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
		this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
	}

	@Nullable
	@Override
	public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
		return null;
	}

	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(HIDING, false);
	}

	public void setHidingBush(BlockPos pos) {
		this.hidingBush = pos;
	}

	private void setHiding(boolean isHiding) {
		this.getEntityData().set(HIDING, isHiding);
	}

	@Override
	public float getWalkTargetValue(BlockPos pos) {
		return this.level().isRainingAt(pos) ? -1.0F : 0.0F;
	}

	public void startHiding() {
		this.setHiding(true);
		this.playSound(SoundRegistry.GECKO_HIDE.get(), 0.5F, this.getRandom().nextFloat() * 0.3F + 0.9F);
		this.sendRustleEffect(1.0F);
		this.setPos(this.hidingBush.getX() + 0.5, this.hidingBush.getY(), this.hidingBush.getZ() + 0.5);
		this.timeHiding = 0;
	}

	public void stopHiding() {
		this.setHiding(false);
		this.playSound(SoundRegistry.GECKO_HIDE.get(), 0.25F, this.getRandom().nextFloat() * 0.3F + 0.9F);
		this.timeHiding = 0;
		float x = this.getRandom().nextFloat() * 2 - 1;
		float y = this.getRandom().nextFloat() * 0.5F;
		float z = this.getRandom().nextFloat() * 2 - 1;
		float len = Mth.sqrt(x * x + y * y + z * z);
		float mag = 0.6F;
		this.setDeltaMovement(this.getDeltaMovement().add(x / len * mag, y / len * mag, z / len * mag));
	}

	private boolean hasValidHiding() {
		return this.level().getBlockState(this.hidingBush).is(BlockRegistry.WEEDWOOD_BUSH);
	}

	private void sendRustleEffect(float strength) {
		if (this.hasValidHiding()) {
			PacketDistributor.sendToPlayersNear((ServerLevel) this.level(), null, this.getX(), this.getY(), this.getZ(), 16.0D, new WeedwoodBushRustlePacket(this.hidingBush, strength));
		}
	}

	public boolean isHiding() {
		return this.getEntityData().get(HIDING);
	}

	@Override
	public boolean isInvisible() {
		return this.isHiding() || super.isInvisible();
	}

	@Override
	public void move(MoverType type, Vec3 pos) {
		if (!this.isHiding()) {
			super.move(type, pos);
		}
	}

	@Override
	protected void customServerAiStep() {
		super.customServerAiStep();

		if (this.isHiding()) {
			if (this.hasValidHiding()) {
				this.timeHiding++;
				if (this.getRandom().nextFloat() < 0.01F) {
					this.playSound(SoundRegistry.GECKO_HIDE.get(), this.getRandom().nextFloat() * 0.05F + 0.02F, this.getRandom().nextFloat() * 0.2F + 0.8F);
					if (this.getRandom().nextFloat() < 0.3F) sendRustleEffect((this.getRandom().nextFloat() + 0.2F) * 0.06F);
				}
				if (this.timeHiding > MIN_HIDE_TIME) {
					List<Player> players = this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(PLAYER_MIN_DISTANCE, PLAYER_MIN_DISTANCE, PLAYER_MIN_DISTANCE));
					if (players.isEmpty() && this.getRandom().nextFloat() < UNHIDE_CHANCE) {
						this.stopHiding();
					}
				}
			} else {
				this.stopHiding();
			}
		}
	}

	@Override
	public boolean isFood(ItemStack stack) {
		return stack.is(ItemRegistry.SAP_SPIT);
	}

	@Override
	public boolean canFallInLove() {
		return false;
	}

	@Override
	protected float getSoundVolume() {
		return this.isHiding() ? super.getSoundVolume() * 0.1F : super.getSoundVolume();
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.GECKO_LIVING.get();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundRegistry.GECKO_HURT.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.GECKO_DEATH.get();
	}
}
