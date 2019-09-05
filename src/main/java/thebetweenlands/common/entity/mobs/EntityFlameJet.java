package thebetweenlands.common.entity.mobs;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.world.World;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.network.clientbound.PacketParticle;
import thebetweenlands.common.network.clientbound.PacketParticle.ParticleType;

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
			if (ticksExisted == 1) {
				TheBetweenlands.networkWrapper.sendToAll(new PacketParticle(ParticleType.FLAME_JET, (float) posX, (float) posY, (float) posZ, 0F));
			}
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