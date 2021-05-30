package thebetweenlands.common.entity.projectiles;

import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.EnumDifficulty;
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
	public int swell = 0;

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
			if(!hasNoGravity())
				getEntityWorld().setEntityState(this, EVENT_TRAIL_PARTICLES);
			
			if(ticksExisted >= 120) {
				explode();
				getEntityWorld().setEntityState(this, EVENT_HIT_PARTICLES);
				getEntityWorld().playSound((EntityPlayer) null, getPosition(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.HOSTILE, 1F, 0.25F);
				setDead();
			}
		}

		if(getEntityWorld().isRemote)
			if(hasNoGravity()) {
				swell++;
				motionX = 0;
				motionY = 0;
				motionZ = 0;
			}
	}

	private void explode() {
		AxisAlignedBB aoe = new AxisAlignedBB(getPosition()).grow(1D);
		if (!getEntityWorld().isRemote && getEntityWorld().getDifficulty() != EnumDifficulty.PEACEFUL) {
			List<EntityLivingBase> list = getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, aoe);
			for (Iterator<EntityLivingBase> iterator = list.iterator(); iterator.hasNext();) {
				EntityLivingBase entity  = iterator.next();
				if (entity != null && (entity instanceof EntityBubblerCrab) || entity instanceof EntityPlayer && ((EntityPlayer) entity).isSpectator() || entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative())
					iterator.remove();
			}
			if (list.isEmpty()) {
				return;
			}
			if (!list.isEmpty()) {
				EntityLivingBase entity = list.get(0);
				if(entity.hurtResistantTime <= 0) {
					entity.attackEntityFrom(new EntityDamageSource("generic", thrower), 2F);
				}
			}
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
				BLParticles.BUBBLE_PURIFIER.spawn(getEntityWorld(), posX + (getEntityWorld().rand.nextDouble() - 0.5D), posY + getEntityWorld().rand.nextDouble(), posZ + (getEntityWorld().rand.nextDouble() - 0.5D), ParticleArgs.get().withColor(0xFFFFFFFF));
	}

	@Override
	protected SoundEvent getSplashSound() {
		return SoundEvents.ENTITY_BOBBER_SPLASH;
	}

	@Override
	protected void onImpact(RayTraceResult mop) {
		if (!getEntityWorld().isRemote) {
			if (!playedSound) {
				getEntityWorld().playSound((EntityPlayer) null, getPosition(), getSplashSound(), SoundCategory.HOSTILE, 0.125F, 3.0F);
				playedSound = true;
			}
			if (mop.typeOfHit != null && mop.typeOfHit == RayTraceResult.Type.BLOCK) {
				setPositionAndRotation(mop.getBlockPos().getX() + 0.5D, mop.getBlockPos().getY() + 1D + height * 0.5D, mop.getBlockPos().getZ() + 0.5D, 0F, 0F);
				setNoGravity(true);
				inGround = true;
				onGround = true;
				markVelocityChanged();
			}

			if (mop.entityHit != null) {
				if (mop.typeOfHit != null && mop.typeOfHit == RayTraceResult.Type.ENTITY && mop.entityHit != thrower && !(mop.entityHit instanceof EntityBubblerCrab)) {
					setPositionAndRotation(mop.entityHit.getPosition().getX() + 0.5D, mop.entityHit.getPosition().getY() + height * 0.5D, mop.entityHit.getPosition().getZ() + 0.5D, 0F, 0F);
					setNoGravity(true);
					inGround = true;
					onGround = true;
					markVelocityChanged();
				}
			}
			getEntityWorld().setEntityState(this, EVENT_HIT_PARTICLES);
		}
	}

	@Override
    @Nullable
    public AxisAlignedBB getCollisionBox(Entity entity) {
    	return entity.getEntityBoundingBox();
    }

	@Override
	@Nullable
	public AxisAlignedBB getCollisionBoundingBox() {
		return null;
	}

	@Override
	public boolean canBeCollidedWith() {
		return false;
	}

	public boolean attackEntityFrom(DamageSource source, int amount) {
		return false;
	}
}