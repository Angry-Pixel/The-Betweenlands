package thebetweenlands.common.entity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public abstract class BasicProximitySpawner extends Mob implements ProximitySpawner {

	protected BasicProximitySpawner(EntityType<? extends Mob> entityType, Level level) {
		super(entityType, level);
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Mob.createMobAttributes()
			.add(Attributes.MAX_HEALTH, 5.0D)
			.add(Attributes.MOVEMENT_SPEED, 0.0D);
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.level().isClientSide() && this.level().getGameTime() % 5 == 0)
			this.checkArea(this, Player.class);
	}

	@Override
	protected boolean isImmobile() {
		return true;
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	public boolean canSneakPast() {
		return true;
	}

	@Override
	public boolean checkSight() {
		return true;
	}

	@Override
	public boolean isSingleUse() {
		return true;
	}

	@Override
	public void kill() {
		this.discard();
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (source.isCreativePlayer()) {
			this.discard();
		}
		return false;
	}

	@Override
	public boolean isInvulnerable() {
		return true;
	}
}
