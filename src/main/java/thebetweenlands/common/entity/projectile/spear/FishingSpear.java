package thebetweenlands.common.entity.projectile.spear;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.datagen.tags.BLEntityTagProvider;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;

import javax.annotation.Nullable;

public class FishingSpear extends AbstractArrow {

	private static final byte EVENT_DEAD = 111;

	protected boolean dealtDamage;

	public FishingSpear(EntityType<? extends AbstractArrow> type, Level level) {
		super(type, level);
	}

	public void onSpawn(Level level, LivingEntity owner, ItemStack stack, boolean crit) {
		this.setPos(owner.getEyePosition().subtract(0.0D, 0.1D, 0.0D));
		this.setOwner(owner);
		this.setPickupItemStack(stack.copyWithCount(1));
		if (owner.hasInfiniteMaterials()) {
			this.pickup = FishingSpear.Pickup.CREATIVE_ONLY;
		}
		this.setCritArrow(crit);
	}

	@Override
	public void tick() {
		if (this.inGroundTime > 4) {
			this.dealtDamage = true;
		}

		if (!this.level().isClientSide()) {
			if (this.getPickupItemStackOrigin().getDamageValue() >= this.getPickupItemStackOrigin().getMaxDamage()) {
				this.level().broadcastEntityEvent(this, EVENT_DEAD);
				this.kill();
				return;
			}
		}

		super.tick();
	}

	@Override
	protected ItemStack getDefaultPickupItem() {
		return ItemRegistry.FISHING_SPEAR.toStack();
	}

	@Override
	public void setPickupItemStack(ItemStack pickupItemStack) {
		super.setPickupItemStack(pickupItemStack);
	}

	@Nullable
	@Override
	protected EntityHitResult findHitEntity(Vec3 startVec, Vec3 endVec) {
		return this.dealtDamage ? null : super.findHitEntity(startVec, endVec);
	}

	@Override
	protected void onHitEntity(EntityHitResult result) {
		Entity entity = result.getEntity();
		float damage = (float) this.getBaseDamage();
		Entity entity1 = this.getOwner();
		DamageSource source = this.damageSources().thrown(this, entity1 == null ? this : entity1);

		if (entity instanceof LivingEntity living && entity.getType().is(BLEntityTagProvider.FISHING_SPEAR_ONE_SHOTS)) {
			damage = living.getMaxHealth();
		} else if (this.isCritArrow()) {
			damage += this.random.nextInt((int) (damage / 2 + 2));
		}

		if (this.level() instanceof ServerLevel serverlevel) {
			damage = EnchantmentHelper.modifyDamage(serverlevel, this.getWeaponItem(), entity, source, damage);
		}

		this.dealtDamage = true;
		if (entity.hurt(source, damage)) {
			if (entity.getType() == EntityType.ENDERMAN) {
				return;
			}

			if (this.level() instanceof ServerLevel serverlevel1) {
				EnchantmentHelper.doPostAttackEffectsWithItemSource(serverlevel1, entity, source, this.getWeaponItem());
			}

			if (entity instanceof LivingEntity livingentity) {
				this.doKnockback(livingentity, source);
				this.doPostHurtEffects(livingentity);
			}

			this.getPickupItemStackOrigin().setDamageValue(this.getPickupItemStackOrigin().getDamageValue() + 1);
		}

		this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01, -0.1, -0.01));
	}

	@Override
	protected void onHitBlock(BlockHitResult result) {
		super.onHitBlock(result);
		if (!this.dealtDamage && !this.isInWater()) {
			this.getPickupItemStackOrigin().setDamageValue(this.getPickupItemStackOrigin().getDamageValue() + 2);
		}
	}

	@Override
	protected void hitBlockEnchantmentEffects(ServerLevel level, BlockHitResult hitResult, ItemStack stack) {
		Vec3 vec3 = hitResult.getBlockPos().clampLocationWithin(hitResult.getLocation());
		EnchantmentHelper.onHitBlock(
			level,
			stack,
			this.getOwner() instanceof LivingEntity livingentity ? livingentity : null,
			this,
			null,
			vec3,
			level.getBlockState(hitResult.getBlockPos()),
			p_348680_ -> this.kill()
		);
	}

	@Override
	public void handleEntityEvent(byte id) {
		if (id == EVENT_DEAD) {
			for (int count = 0; count <= 10; ++count) {
				TheBetweenlands.createParticle(new ItemParticleOption(ParticleTypes.ITEM, this.getPickupItemStackOrigin()), this.level(), this.getX() + (this.getRandom().nextDouble() - 0.5D), this.getY() + (this.getRandom().nextDouble() - 0.5D), this.getZ() + (this.getRandom().nextDouble() - 0.5D));
			}
		} else {
			super.handleEntityEvent(id);
		}
	}

	@Override
	public ItemStack getWeaponItem() {
		return this.getPickupItemStackOrigin();
	}

	@Override
	protected boolean tryPickup(Player player) {
		return super.tryPickup(player) || this.isNoPhysics() && this.ownedBy(player) && player.getInventory().add(this.getPickupItem());
	}

	@Override
	protected SoundEvent getDefaultHitGroundSoundEvent() {
		return SoundRegistry.SPEAR_LAND.get();
	}

	@Override
	public void playerTouch(Player entity) {
		if (this.ownedBy(entity) || this.getOwner() == null) {
			super.playerTouch(entity);
		}
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		this.dealtDamage = compound.getBoolean("dealt_damage");
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putBoolean("dealt_damage", this.dealtDamage);
	}

	@Override
	public void tickDespawn() {
		if (this.pickup != AbstractArrow.Pickup.ALLOWED) {
			super.tickDespawn();
		}
	}

	@Override
	public boolean shouldRender(double x, double y, double z) {
		return true;
	}
}
