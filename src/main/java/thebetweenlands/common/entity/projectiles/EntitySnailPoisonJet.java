package thebetweenlands.common.entity.projectiles;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.common.entity.mobs.EntityBloodSnail;

public class EntitySnailPoisonJet extends EntityThrowable {

	public EntitySnailPoisonJet(World world) {
		super(world);
		setSize(0.25F, 0.25F);
	}

	public EntitySnailPoisonJet(World world, EntityLiving entity) {
		super(world, entity);
	}

	public EntitySnailPoisonJet(World world, double x, double y, double z) {
		super(world, x, y, z);
	}

	public EntitySnailPoisonJet(World world, EntityPlayer player) {
		super(world, player);
	}

	@Override
	public void tick() {
		super.tick();

		if (this.world.isRemote()) {
			this.trailParticles(this.world, this.posX, this.posY + this.height / 2.0D, this.posZ, this.rand);
		}

		if (this.ticksExisted > 140) {
			this.remove();
		}
	}

	@Override
	protected void onImpact(RayTraceResult result) {
		if (result.entityHit != null) {
			if (result.entityHit instanceof EntityLivingBase && !(result.entityHit instanceof EntityBloodSnail)) {
				if(result.entityHit.attackEntityFrom(getThrower() != null ? DamageSource.causeIndirectDamage(this, getThrower()).setProjectile() : DamageSource.causeThrownDamage(this, null), 1.0F)) {
					if (!world.isRemote()) {
						((EntityLivingBase) result.entityHit).addPotionEffect(new PotionEffect(MobEffects.POISON, 5 * 20, 0));
						this.remove();
					}
				} else {
					this.motionX *= -0.1D;
	                this.motionY *= -0.1D;
	                this.motionZ *= -0.1D;
	                this.rotationYaw += 180.0F;
	                this.prevRotationYaw += 180.0F;
				}
			}
		} else {
			if(result.typeOfHit == Type.BLOCK) {
				IBlockState blockState = this.world.getBlockState(result.getBlockPos());
				AxisAlignedBB collisionBox = blockState.getCollisionBoundingBox(this.world, result.getBlockPos());
				if(collisionBox != null && collisionBox.offset(result.getBlockPos()).intersects(this.getBoundingBox())) {
					this.remove();
				}
			} else {
				this.remove();
			}
		}
	}

	@Override
	protected float getGravityVelocity() {
		return 0.02F;
	}

	@Override
	public boolean canBeCollidedWith() {
		return false;
	}

	public boolean attackEntityFrom(DamageSource source, int amount) {
		return false;
	}

	@OnlyIn(Dist.CLIENT)
	public void trailParticles(World world, double x, double y, double z, Random rand) {
		for (int count = 0; count < 5; ++count) {
			BLParticles.SNAIL_POISON.spawn(world, x, y, z);
		}
	}
}
