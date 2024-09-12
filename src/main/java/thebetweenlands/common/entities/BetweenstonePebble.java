package thebetweenlands.common.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.event.EventHooks;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;

import javax.annotation.Nullable;

public class BetweenstonePebble extends Projectile implements ItemSupplier {
	private static final EntityDataAccessor<Byte> FLAGS = SynchedEntityData.defineId(BetweenstonePebble.class, EntityDataSerializers.BYTE);
	private static final EntityDataAccessor<ItemStack> ITEM_STACK = SynchedEntityData.defineId(BetweenstonePebble.class, EntityDataSerializers.ITEM_STACK);
	private int life;
	private double baseDamage = 2.0;
	@Nullable
	private ItemStack firedFromWeapon = null;

	public BetweenstonePebble(EntityType<? extends Projectile> type, Level level) {
		super(type, level);
	}

	public BetweenstonePebble(double x, double y, double z, Level level, ItemStack stack, @Nullable ItemStack firedFromWeapon) {
		this(EntityRegistry.BETWEENSTONE_PEBBLE.get(), level);
		this.setItem(stack);
		this.setPos(x, y, z);
		if (firedFromWeapon != null && level instanceof ServerLevel) {
			if (firedFromWeapon.isEmpty()) {
				throw new IllegalArgumentException("Invalid weapon firing an arrow");
			}

			this.firedFromWeapon = firedFromWeapon.copy();
		}
	}

	public BetweenstonePebble(LivingEntity owner, Level level, ItemStack stack, @Nullable ItemStack firedFromWeapon) {
		this(owner.getX(), owner.getEyeY() - 0.1F, owner.getZ(), level, stack, firedFromWeapon);
		this.setOwner(owner);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		builder.define(FLAGS, (byte) 0);
		builder.define(ITEM_STACK, this.getDefaultItem());
	}

	public void setItem(ItemStack stack) {
		if (stack.isEmpty()) {
			this.getEntityData().set(ITEM_STACK, this.getDefaultItem());
		} else {
			this.getEntityData().set(ITEM_STACK, stack.copyWithCount(1));
		}
	}

	@Override
	public ItemStack getItem() {
		return this.getEntityData().get(ITEM_STACK);
	}

	@Override
	public void tick() {
		super.tick();
		boolean flag = this.isNoPhysics();
		Vec3 vec3 = this.getDeltaMovement();

		BlockPos blockpos = this.blockPosition();
		BlockState blockstate = this.level().getBlockState(blockpos);
		if (this.isInWaterOrRain() || blockstate.is(Blocks.POWDER_SNOW) || this.isInFluidType((fluidType, height) -> this.canFluidExtinguish(fluidType))) {
			this.clearFire();
		}

		Vec3 vec32 = this.position();
		Vec3 vec33 = vec32.add(vec3);
		HitResult hitresult = this.level().clip(new ClipContext(vec32, vec33, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
		if (hitresult.getType() != HitResult.Type.MISS) {
			vec33 = hitresult.getLocation();
		}

		while (!this.isRemoved()) {
			EntityHitResult entityhitresult = this.findHitEntity(vec32, vec33);
			if (entityhitresult != null) {
				hitresult = entityhitresult;
			}

			if (hitresult != null && hitresult.getType() == HitResult.Type.ENTITY) {
				Entity entity = ((EntityHitResult) hitresult).getEntity();
				Entity entity1 = this.getOwner();
				if (entity instanceof Player && entity1 instanceof Player && !((Player) entity1).canHarmPlayer((Player) entity)) {
					hitresult = null;
					entityhitresult = null;
				}
			}

			if (hitresult != null && hitresult.getType() != HitResult.Type.MISS && !flag) {
				if (EventHooks.onProjectileImpact(this, hitresult))
					break;
				ProjectileDeflection projectiledeflection = this.hitTargetOrDeflectSelf(hitresult);
				this.hasImpulse = true;
				if (projectiledeflection != ProjectileDeflection.NONE) {
					break;
				}
			}

			if (entityhitresult == null) {
				break;
			}

			hitresult = null;
		}

		vec3 = this.getDeltaMovement();
		double d5 = vec3.x;
		double d6 = vec3.y;
		double d1 = vec3.z;
		if (this.isCritArrow()) {
			for (int i = 0; i < 4; i++) {
				this.level().addParticle(ParticleTypes.CRIT,
					this.getX() + d5 * (double) i / 4.0,
					this.getY() + d6 * (double) i / 4.0,
					this.getZ() + d1 * (double) i / 4.0,
					-d5, -d6 + 0.2, -d1);
			}
		}

		double d7 = this.getX() + d5;
		double d2 = this.getY() + d6;
		double d3 = this.getZ() + d1;
		float f = 0.99F;
		if (this.isInWater()) {
			for (int j = 0; j < 4; j++) {
				this.level().addParticle(ParticleTypes.BUBBLE, d7 - d5 * 0.25, d2 - d6 * 0.25, d3 - d1 * 0.25, d5, d6, d1);
			}

			f = 0.6F;
		}

		if (!this.level().isClientSide()) {
			this.tickDespawn();
		}

		this.setDeltaMovement(vec3.scale(f));
		if (!flag) {
			this.applyGravity();
		}

		this.setPos(d7, d2, d3);
		this.checkInsideBlocks();
	}

	@Override
	protected double getDefaultGravity() {
		return 0.05;
	}

	protected void tickDespawn() {
		this.life++;
		if (this.life >= 1200) {
			this.discard();
		}
	}

	/**
	 * Called when the arrow hits an entity
	 */
	@Override
	protected void onHitEntity(EntityHitResult result) {
		super.onHitEntity(result);
		Entity entity = result.getEntity();
		float f = (float)this.getDeltaMovement().length();
		double d0 = this.baseDamage;
		Entity entity1 = this.getOwner();
		DamageSource damagesource = this.damageSources().mobProjectile(this, entity1 instanceof LivingEntity living ? living : null);
		if (this.getWeaponItem() != null && this.level() instanceof ServerLevel serverlevel) {
			d0 = EnchantmentHelper.modifyDamage(serverlevel, this.getWeaponItem(), entity, damagesource, (float)d0);
		}

		int j = Mth.ceil(Mth.clamp((double)f * d0, 0.0, 2.147483647E9));

		if (this.isCritArrow()) {
			long k = this.getRandom().nextInt(j / 2 + 2);
			j = (int)Math.min(k + (long)j, 2147483647L);
		}

		if (entity1 instanceof LivingEntity livingentity1) {
			livingentity1.setLastHurtMob(entity);
		}

		boolean flag = entity.getType() == EntityType.ENDERMAN;
		int i = entity.getRemainingFireTicks();
		if (this.isOnFire() && !flag) {
			entity.igniteForSeconds(5.0F);
		}

		if (entity.hurt(damagesource, (float)j)) {
			if (flag) {
				return;
			}

			if (entity instanceof LivingEntity livingentity) {
				this.doKnockback(livingentity, damagesource);
				if (this.level() instanceof ServerLevel serverlevel1) {
					EnchantmentHelper.doPostAttackEffectsWithItemSource(serverlevel1, livingentity, damagesource, this.getWeaponItem());
				}
			}

			this.playSound(SoundRegistry.SLINGSHOT_HIT.get(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
			this.discard();
		} else {
			entity.setRemainingFireTicks(i);
			this.deflect(ProjectileDeflection.REVERSE, entity, this.getOwner(), false);
			this.setDeltaMovement(this.getDeltaMovement().scale(0.2));
			if (!this.level().isClientSide() && this.getDeltaMovement().lengthSqr() < 1.0E-7) {
				this.discard();
			}
		}
	}

	protected void doKnockback(LivingEntity entity, DamageSource damageSource) {
		double d0 = this.firedFromWeapon != null && this.level() instanceof ServerLevel serverlevel ? EnchantmentHelper.modifyKnockback(serverlevel, this.firedFromWeapon, entity, damageSource, 0.0F) : 0.0F;
		if (d0 > 0.0) {
			double d1 = Math.max(0.0, 1.0 - entity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
			Vec3 vec3 = this.getDeltaMovement().multiply(1.0, 0.0, 1.0).normalize().scale(d0 * 0.6 * d1);
			if (vec3.lengthSqr() > 0.0) {
				entity.push(vec3.x, 0.1, vec3.z);
			}
		}
	}

	@Override
	protected void onHitBlock(BlockHitResult result) {
		super.onHitBlock(result);
		ItemStack itemstack = this.getWeaponItem();
		if (this.level() instanceof ServerLevel serverlevel && itemstack != null) {
			this.hitBlockEnchantmentEffects(serverlevel, result, itemstack);
		}
		this.discard();
	}

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
			p_348569_ -> this.firedFromWeapon = null
		);
	}

	@Override
	public ItemStack getWeaponItem() {
		return this.firedFromWeapon;
	}

	@Nullable
	protected EntityHitResult findHitEntity(Vec3 startVec, Vec3 endVec) {
		return ProjectileUtil.getEntityHitResult(this.level(), this, startVec, endVec, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0), this::canHitEntity);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		compound.put("item", this.getItem().save(this.registryAccess()));
		compound.putShort("life", (short)this.life);
		compound.putDouble("damage", this.baseDamage);
		compound.putBoolean("crit", this.isCritArrow());
		if (this.firedFromWeapon != null) {
			compound.put("weapon", this.firedFromWeapon.save(this.registryAccess(), new CompoundTag()));
		}
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		if (compound.contains("item", Tag.TAG_COMPOUND)) {
			this.setItem(ItemStack.parse(this.registryAccess(), compound.getCompound("item")).orElse(this.getDefaultItem()));
		} else {
			this.setItem(this.getDefaultItem());
		}
		this.life = compound.getShort("life");
		if (compound.contains("damage", Tag.TAG_ANY_NUMERIC)) {
			this.baseDamage = compound.getDouble("damage");
		}
		this.setCritArrow(compound.getBoolean("crit"));
		if (compound.contains("weapon", Tag.TAG_COMPOUND)) {
			this.firedFromWeapon = ItemStack.parse(this.registryAccess(), compound.getCompound("weapon")).orElse(null);
		} else {
			this.firedFromWeapon = null;
		}
	}

	public void setCritArrow(boolean critArrow) {
		this.setFlag(1, critArrow);
	}

	private void setFlag(int id, boolean value) {
		byte b0 = this.getEntityData().get(FLAGS);
		if (value) {
			this.getEntityData().set(FLAGS, (byte)(b0 | id));
		} else {
			this.getEntityData().set(FLAGS, (byte)(b0 & ~id));
		}
	}

	public boolean isCritArrow() {
		byte b0 = this.getEntityData().get(FLAGS);
		return (b0 & 1) != 0;
	}

	public void setNoPhysics(boolean noPhysics) {
		this.noPhysics = noPhysics;
		this.setFlag(2, noPhysics);
	}

	public boolean isNoPhysics() {
		return !this.level().isClientSide ? this.noPhysics : (this.entityData.get(FLAGS) & 2) != 0;
	}

	private ItemStack getDefaultItem() {
		return new ItemStack(ItemRegistry.BETWEENSTONE_PEBBLE.get());
	}
}
