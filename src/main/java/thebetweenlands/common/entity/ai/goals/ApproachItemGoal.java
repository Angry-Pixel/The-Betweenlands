package thebetweenlands.common.entity.ai.goals;

import java.util.EnumSet;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public class ApproachItemGoal extends Goal {
	private final Mob entity;
	private final double farSpeed;
	private final double nearSpeed;
	@Nullable
	private ItemEntity targetEntity;
	@Nullable
	private Vec3 targetPos;
	private final float targetDistance;
	@Nullable
	private Path entityPathEntity;
	private final PathNavigation entityPathNavigate;
	private final ItemStack targetItem;
	private final boolean ignoreDamage;
	private final int distractionTime;
	private int distractionTicks;

	public ApproachItemGoal(Mob entity, Item item, int distractionTime, float targetDistance, double farSpeed, double nearSpeed) {
		this(entity, new ItemStack(item), distractionTime, targetDistance, farSpeed, nearSpeed, true);
	}

	public ApproachItemGoal(Mob entity, ItemStack item, int distractionTime, float targetDistance, double farSpeed, double nearSpeed, boolean ignoreDamage) {
		this.entity = entity;
		this.targetItem = item;
		this.targetDistance = targetDistance;
		this.farSpeed = farSpeed;
		this.nearSpeed = nearSpeed;
		this.entityPathNavigate = entity.getNavigation();
		this.ignoreDamage = ignoreDamage;
		this.distractionTime = distractionTime;
		this.setFlags(EnumSet.of(Flag.MOVE));
	}

	// Moved from a lambda predicate to a method due to IDE silliness
	public boolean isValidItemEntity(Entity maybeItemEntity) {
		return maybeItemEntity.isAlive() && maybeItemEntity.distanceTo(ApproachItemGoal.this.entity) <= ApproachItemGoal.this.targetDistance
				&& maybeItemEntity instanceof ItemEntity itemEntity &&
				(ApproachItemGoal.this.ignoreDamage ? ItemStack.isSameItem(itemEntity.getItem(), ApproachItemGoal.this.targetItem) : ItemStack.isSameItemSameComponents(itemEntity.getItem(), ApproachItemGoal.this.targetItem));
	}
	
	@Override
	public boolean canUse() {
		List<ItemEntity> list = this.entity.level().getEntitiesOfClass(ItemEntity.class, this.entity.getBoundingBox().inflate(this.targetDistance, 3, this.targetDistance), this::isValidItemEntity);
		if (list.isEmpty()) {
			return false;
		}
		this.targetEntity = list.getFirst();
		if(this.targetEntity == null || !this.targetEntity.isAlive()) {
			return false;
		}
		this.entityPathEntity = this.entityPathNavigate.createPath(this.targetEntity, 0);
		this.targetPos = this.targetEntity.position();
		return this.entityPathEntity != null;
	}

	@Override
	public boolean canContinueToUse() {
		return (this.entityPathEntity != null && this.targetEntity != null && this.targetEntity.isAlive()) || !this.entityPathNavigate.isDone();
	}

	@Override
	public void start() {
		this.entityPathNavigate.moveTo(this.entityPathEntity, this.farSpeed);
		this.distractionTicks = 0;
	}

	@Override
	public void stop() {
		this.targetEntity = null;
		this.entityPathNavigate.stop();
	}

	@Override
	public void tick() {
		if(this.targetEntity.distanceToSqr(this.targetPos) > 0.5) {
			this.entityPathEntity = this.entityPathNavigate.createPath(this.targetEntity, 0);
			this.entityPathNavigate.moveTo(this.entityPathEntity, this.farSpeed);
			this.targetPos = this.targetEntity.position();
		}
		if (this.entity.distanceTo(this.targetEntity) < 4.0D) {
			this.entity.getNavigation().setSpeedModifier(this.getNearSpeed());
			if(this.entity.distanceTo(this.targetEntity) < 2.0D) {
				this.distractionTicks++;
				if(this.distractionTicks > this.getDistractionTime()) {
					this.targetEntity.discard();
					this.onPickup();
					this.stop();
				}
			}
		} else {
			this.entity.getNavigation().setSpeedModifier(this.getFarSpeed());
		}
	}

	protected double getNearSpeed() {
		return this.nearSpeed;
	}

	protected double getFarSpeed() {
		return this.farSpeed;
	}

	protected int getDistractionTime() {
		return this.distractionTime;
	}

	protected void onPickup() {}
}
