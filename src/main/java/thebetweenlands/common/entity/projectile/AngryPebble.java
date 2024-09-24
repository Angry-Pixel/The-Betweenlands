package thebetweenlands.common.entity.projectile;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class AngryPebble extends ThrowableItemProjectile {

	private final ItemStack stack;
	private final float explosionRadius;

	public AngryPebble(EntityType<? extends ThrowableItemProjectile> type, Level level) {
		super(type, level);
		this.stack = new ItemStack(ItemRegistry.ANGRY_PEBBLE.get());
		this.explosionRadius = 4.5F;
		this.setItem(this.stack);
	}

	public AngryPebble(LivingEntity shooter, Level level, ItemStack displayStack, float explosionRadius) {
		super(EntityRegistry.ANGRY_PEBBLE.get(), shooter, level);
		this.stack = displayStack;
		this.explosionRadius = explosionRadius;
		this.setItem(this.stack);
	}

	@Override
	public void tick() {
		super.tick();
		if(this.tickCount > 400) {
			this.discard();
		}
	}

	@Override
	protected void onHit(HitResult result) {
		if(result.getType() != HitResult.Type.MISS) {
			if(this.level().isClientSide()) {
				double particleX = Mth.floor(this.getX()) + this.getRandom().nextFloat();
				double particleY = Mth.floor(this.getY()) + this.getRandom().nextFloat();
				double particleZ = Mth.floor(this.getZ()) + this.getRandom().nextFloat();
				for (int count = 0; count < 10; count++) {
					TheBetweenlands.createParticle(ParticleTypes.FLAME, this.level(), particleX, particleY, particleZ);
				}
			} else {
				this.level().explode(this.getOwner() != null ? this.getOwner() : this, this.getX(), this.getY(), this.getZ(), this.explosionRadius, Level.ExplosionInteraction.BLOCK);
				this.discard();
			}
		}
	}

	@Override
	protected Item getDefaultItem() {
		return ItemRegistry.ANGRY_PEBBLE.get();
	}
}
