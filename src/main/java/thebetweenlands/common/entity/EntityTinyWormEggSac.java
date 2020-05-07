package thebetweenlands.common.entity;


import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleBreaking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.mobs.EntityTinySludgeWorm;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityTinyWormEggSac extends EntityProximitySpawner {
	private static final byte EVENT_GOOP_PARTICLES = 100;
	
	public EntityTinyWormEggSac(World world) {
		super(world);
		setSize(1F, 0.5F);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (!getEntityWorld().isRemote && getEntityWorld().getTotalWorldTime()%5 == 0)
			checkArea();
	}

	@Override
	protected boolean isMovementBlocked() {
		return true;
	}

	@Override
    public boolean canBePushed() {
        return false;
    }

	@Override
	public void addVelocity(double x, double y, double z) {
		motionX = 0;
		motionY += y;
		motionZ = 0;
	}

	@Override
	public boolean getIsInvulnerable() {
		return true;
	}

	@Override
	public void onKillCommand() {
		this.setDead();
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if(source instanceof EntityDamageSource) {
			Entity sourceEntity = ((EntityDamageSource) source).getTrueSource();
			if(sourceEntity instanceof EntityPlayer && ((EntityPlayer) sourceEntity).isCreative()) {
				this.setDead();
			}
		}
		return false;
	}

	@Override
	protected void performPostSpawnaction(Entity targetEntity, @Nullable Entity entitySpawned) {
		if(!this.world.isRemote) {
			this.world.setEntityState(this, EVENT_GOOP_PARTICLES);
		}
		getEntityWorld().playSound((EntityPlayer)null, getPosition(), getDeathSound(), SoundCategory.HOSTILE, 0.5F, 1.0F);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void handleStatusUpdate(byte id) {
		super.handleStatusUpdate(id);

		if(id == EVENT_GOOP_PARTICLES) {
			for (int count = 0; count <= 200; ++count) {
				Particle fx = new ParticleBreaking.SnowballFactory().createParticle(EnumParticleTypes.SNOWBALL.getParticleID(), world, this.posX + (world.rand.nextDouble() - 0.5D), this.posY + 0.25f + world.rand.nextDouble(), this.posZ + (world.rand.nextDouble() - 0.5D), 0, 0, 0, 0);
				fx.setRBGColorF(48F, 64F, 91F);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
		}
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.WORM_EGG_SAC_LIVING;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.WORM_EGG_SAC_SQUISH;
	}

	@Override
	protected float getProximityHorizontal() {
		return 3F;
	}

	@Override
	protected float getProximityVertical() {
		return 1F;
	}

	@Override
	protected boolean canSneakPast() {
		return true;
	}

	@Override
	protected boolean checkSight() {
		return true;
	}

	@Override
	protected Entity getEntitySpawned() {
		return new EntityTinySludgeWorm(getEntityWorld());
	}

	@Override
	protected int getEntitySpawnCount() {
		return 4;
	}

	@Override
	protected boolean isSingleUse() {
		return true;
	}

	@Override
	protected int maxUseCount() {
		return 0;
	}
}