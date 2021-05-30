package thebetweenlands.common.entity.projectiles;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.entity.mobs.EntityBubblerCrab;

public class EntityBubblerCrabBubble extends EntityThrowable {

	private boolean playedSound = false;
	private static final byte EVENT_TRAIL_PARTICLES = 105;
	private static final byte EVENT_HIT_PARTICLES = 106;

	public EntityBubblerCrabBubble(World world) {
		super(world);
		setSize(0.5F, 0.5F);
	}

	public EntityBubblerCrabBubble(World world, EntityLiving entity) {
		super(world, entity);
	}

	public EntityBubblerCrabBubble(World world, double x, double y, double z) {
		super(world, x, y, z);
	}

	public EntityBubblerCrabBubble(World world, EntityPlayer player) {
		super(world, player);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if(!getEntityWorld().isRemote) {
			//if(!inGround)
				//getEntityWorld().setEntityState(this, EVENT_TRAIL_PARTICLES);
			if(ticksExisted > 120)
				setDead();
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void handleStatusUpdate(byte id) {
		super.handleStatusUpdate(id);
		if(id == EVENT_TRAIL_PARTICLES) {
			for(int i = 0; i < 8; ++i) {
				double velX = 0.0D;
				double velY = 0.0D;
				double velZ = 0.0D;
				int motionX = rand.nextInt(2) * 2 - 1;
				int motionZ = rand.nextInt(2) * 2 - 1;
				velY = (rand.nextFloat() - 0.5D) * 0.125D;
				velZ = rand.nextFloat() * 0.1F * motionZ;
				velX = rand.nextFloat() * 0.1F * motionX;
				BLParticles.BUBBLE_PURIFIER.spawn(getEntityWorld(), posX, posY, posZ, ParticleArgs.get().withMotion(velX, velY, velZ).withColor(0xFFFFFFFF));
			}
		}

		if(id == EVENT_HIT_PARTICLES)
			for(int i = 0; i < 16; ++i)
				BLParticles.BUBBLE_PURIFIER.spawn(getEntityWorld(), posX + (getEntityWorld().rand.nextDouble() - 0.5D), posY + 2D + getEntityWorld().rand.nextDouble(), posZ + (getEntityWorld().rand.nextDouble() - 0.5D), ParticleArgs.get().withColor(0xFFFFFFFF));
	}

	@Override
	protected SoundEvent getSplashSound() {
		return SoundEvents.ENTITY_BOBBER_SPLASH;
	}

	@Override
	protected void onImpact(RayTraceResult mop) {
		if (!getEntityWorld().isRemote) {
			if (!playedSound) {
				getEntityWorld().playSound((EntityPlayer) null, getPosition(), getSplashSound(), SoundCategory.HOSTILE, 0.25F, 2.0F);
				playedSound = true;
			}
			if (mop.typeOfHit != null && mop.typeOfHit == RayTraceResult.Type.BLOCK) {
				setPositionAndRotation(mop.getBlockPos().getX() + 0.5D, mop.getBlockPos().getY() + 1D + height * 0.5D, mop.getBlockPos().getZ() + 0.5D, 0F, 0F);
				inGround = true;
			}

			if (mop.entityHit != null) {
				if (mop.typeOfHit != null && mop.typeOfHit == RayTraceResult.Type.ENTITY && mop.entityHit != thrower && !(mop.entityHit instanceof EntityBubblerCrab)) {
					setPositionAndRotation(mop.entityHit.getPosition().getX() + 0.5D, mop.entityHit.getPosition().getY() + height * 0.5D, mop.entityHit.getPosition().getZ() + 0.5D, 0F, 0F);
					inGround = true;
				}
				//getEntityWorld().setEntityState(this, EVENT_HIT_PARTICLES);
			}
		}
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	public boolean attackEntityFrom(DamageSource source, int amount) {
		return false;
	}
}