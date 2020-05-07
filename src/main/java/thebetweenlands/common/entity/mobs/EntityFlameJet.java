package thebetweenlands.common.entity.mobs;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityFlameJet extends EntityLiving {
	public EntityLivingBase shootingEntity;
	public EntityFlameJet(World world) {
		super(world);
		setSize(1F, 2.5F);
		setEntityInvulnerable(true);
	}

	public EntityFlameJet(World world, EntityLivingBase shooter) {
		super(world);
		setSize(1F, 2.5F);
		setEntityInvulnerable(true);
		this.shootingEntity = shooter;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (!getEntityWorld().isRemote) {
			if (ticksExisted > 20)
				setDead();
		} else {
			if (ticksExisted == 1) {
				this.spawnFlameJetParticles();
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private void spawnFlameJetParticles() {
		for (double yy = this.posY; yy < this.posY + 2D; yy += 0.5D) {
			double d0 = this.posX - 0.075F;
			double d1 = yy;
			double d2 = this.posZ - 0.075F;
			double d3 = this.posX + 0.075F;
			double d4 = this.posZ + 0.075F;
			double d5 = this.posX;
			double d6 = yy + 0.25F;
			double d7 = this.posZ;
			this.world.spawnParticle(EnumParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.01D, 0.0D);
			this.world.spawnParticle(EnumParticleTypes.FLAME, d0, d1, d4, 0.0D, 0.01D, 0.0D);
			this.world.spawnParticle(EnumParticleTypes.FLAME, d3, d1, d2, 0.0D, 0.01D, 0.0D);
			this.world.spawnParticle(EnumParticleTypes.FLAME, d3, d1, d4, 0.0D, 0.01D, 0.0D);
			this.world.spawnParticle(EnumParticleTypes.FLAME, d5, d6, d7, 0.0D, 0.01D, 0.0D);
		}
	}

	@Override
	public boolean getIsInvulnerable() {
		return true;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		return false;
	}

	@Override
	protected void collideWithEntity(Entity entity) {
		if (!getEntityWorld().isRemote) {
			if (entity.getEntityBoundingBox().maxY >= getEntityBoundingBox().minY && entity.getEntityBoundingBox().minY <= getEntityBoundingBox().maxY)
				if (entity.getEntityBoundingBox().maxX >= getEntityBoundingBox().minX && entity.getEntityBoundingBox().minX <= getEntityBoundingBox().maxX)
					if (entity.getEntityBoundingBox().maxZ >= getEntityBoundingBox().minZ && entity.getEntityBoundingBox().minZ <= getEntityBoundingBox().maxZ)
						if (entity instanceof EntityLivingBase && !(entity instanceof EntityFlameJet))
							if (!entity.isImmuneToFire()) {
								boolean catch_fire = entity.attackEntityFrom(causeFlameJetDamage(this, shootingEntity), 5.0F);
								if (catch_fire)
									entity.setFire(5);
							}
							else {
								if (entity != shootingEntity)
									entity.attackEntityFrom(DamageSource.causeIndirectDamage(this, shootingEntity).setProjectile(), 2.0F);
							}
			setDead();
		}
	}

	public static DamageSource causeFlameJetDamage(EntityFlameJet flame_jet, @Nullable Entity indirectEntityIn) {
		return indirectEntityIn == null ? (new EntityDamageSourceIndirect("onFire", flame_jet, flame_jet)).setFireDamage().setProjectile() : (new EntityDamageSourceIndirect("flamejet", flame_jet, indirectEntityIn)).setFireDamage().setProjectile();
	}
}