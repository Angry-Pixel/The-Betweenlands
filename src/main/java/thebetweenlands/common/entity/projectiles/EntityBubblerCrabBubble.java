package thebetweenlands.common.entity.projectiles;

import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.entity.mobs.EntityBubblerCrab;

public class EntityBubblerCrabBubble extends EntityThrowable {

	private static final byte EVENT_IMPACT = 106;
	private static final byte EVENT_EXPLODE = 107;
	
	private boolean impacted = false;
	
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
	public void move(MoverType type, double x, double y, double z) {
		if(this.ticksExisted < 2) {
			//Stupid EntityTrackerEntry is broken and desyncs server position.
			//Tracker updates server side position but *does not* send the change to the client
			//when tracker.updateCounter == 0, causing a desync until the next force teleport
			//packet.......
			//By not moving the entity until then it works.
			return;
		}
		
		super.move(type, x, y, z);
	}
	
	@Override
	public void onUpdate() {
		double prevPosX = this.posX;
		double prevPosY = this.posY;
		double prevPosZ = this.posZ;
		super.onUpdate();
		double newX = this.posX;
		double newY = this.posY;
		double newZ = this.posZ;
		this.setPosition(prevPosX, prevPosY, prevPosZ);
		this.move(MoverType.SELF, newX - prevPosX, newY - prevPosY, newZ - prevPosZ);
		this.lastTickPosX = this.prevPosX = prevPosX;
		this.lastTickPosY = this.prevPosY = prevPosY;
		this.lastTickPosZ = this.prevPosZ = prevPosZ;
		
		if(!getEntityWorld().isRemote) {
			if(ticksExisted >= 120) {
				explode();
				getEntityWorld().setEntityState(this, EVENT_EXPLODE);
				getEntityWorld().playSound((EntityPlayer) null, getPosition(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.HOSTILE, 1F, 0.25F);
				setDead();
			}
		} else {
			if(this.impacted) {
				this.spawnSwellingParticles();
				swell++;
				motionX = 0;
				motionY = 0;
				motionZ = 0;
			} else {
				this.spawnTrailParticles();
			}
		}
	}

	private void explode() {
		final double radius = 2.0D;
		
		AxisAlignedBB aoe = this.getEntityBoundingBox().grow(radius);
		
		if (!getEntityWorld().isRemote && getEntityWorld().getDifficulty() != EnumDifficulty.PEACEFUL) {
			List<EntityLivingBase> list = getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, aoe);
			
			for(EntityLivingBase entity : list) {
				if(entity instanceof EntityBubblerCrab == false && !entity.getIsInvulnerable() && entity.hurtResistantTime <= 0 && entity.getDistance(this) <= radius) {
					float attackDamage;
					
					if(this.thrower != null && this.thrower.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE) != null) {
						attackDamage = (float)this.thrower.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue() * 1.5f;
					} else {
						attackDamage = 2.0F;
					}
					
					if(entity.attackEntityFrom(new EntityDamageSourceIndirect("generic", this.thrower != null ? this.thrower : this , this), attackDamage)) {
						entity.motionX *= 0.25f;
						entity.motionZ *= 0.25f;
						entity.addVelocity(0, 0.1f, 0);
					}
				}
			}
		}
	}
	
	private float getBubbleRadius() {
		return 0.15f + Math.min(this.swell, 120) * 0.0065f;
	}

	@SideOnly(Side.CLIENT)
	private void spawnTrailParticles() {
		float radius = this.getBubbleRadius();
		BLParticles.FANCY_BUBBLE.spawn(getEntityWorld(), posX + (this.world.rand.nextFloat() - 0.5f) * radius, posY + (this.world.rand.nextFloat() - 0.5f) * radius, posZ + (this.world.rand.nextFloat() - 0.5f) * radius, ParticleArgs.get().withColor(0.44f, 0.46f, 0.42f, 0.95f).withScale(0.5f + this.world.rand.nextFloat() * 0.5f));
	}
	
	@SideOnly(Side.CLIENT)
	private void spawnSwellingParticles() {
		float radius = this.getBubbleRadius();
		
		if(this.world.rand.nextInt(3) == 0) {
			double ox = (rand.nextFloat() - 0.5f) * radius * 0.5f;
			double oy = (rand.nextFloat() - 0.5f) * radius * 0.5f;
			double oz = (rand.nextFloat() - 0.5f) * radius * 0.5f;
			double velX = ox * rand.nextFloat() * 0.25f;
			double velY = -0.1f;
			double velZ = oz * rand.nextFloat() * 0.25f;
			BLParticles.FANCY_DRIP.spawn(getEntityWorld(), this.posX + ox, this.posY + radius * 0.25f + oy, this.posZ + oz, ParticleArgs.get().withMotion(velX, velY, velZ).withScale(0.5f).withColor(0.44f, 0.46f, 0.42f, 0.8f));
		}
		
		if(this.world.rand.nextInt(10) == 0) {
			BLParticles.FANCY_BUBBLE.spawn(getEntityWorld(), posX + (this.world.rand.nextFloat() - 0.5f) * radius, posY + radius * 0.5f + (this.world.rand.nextFloat() - 0.5f) * radius * 0.5f, posZ + (this.world.rand.nextFloat() - 0.5f) * radius, 
					ParticleArgs.get().withMotion((this.world.rand.nextFloat() - 0.5f) * 0.01f, 0.05f, (this.world.rand.nextFloat() - 0.5f) * 0.01f)
					.withScale(0.25f + Math.max(0.0f, this.getBubbleRadius() - 0.2f) * 1.5f + this.world.rand.nextFloat() * 0.5f)
					.withData(true)
					.withColor(0.44f, 0.46f, 0.42f, 0.8f));
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void handleStatusUpdate(byte id) {
		super.handleStatusUpdate(id);

		if(id == EVENT_IMPACT) {
			float radius = this.getBubbleRadius();
			
			for(int i = 0; i < 8; ++i) {
				BLParticles.FANCY_BUBBLE.spawn(getEntityWorld(), posX + (this.world.rand.nextFloat() - 0.5f) * radius, posY + (this.world.rand.nextFloat() - 0.5f) * radius, posZ + (this.world.rand.nextFloat() - 0.5f) * radius, ParticleArgs.get().withColor(0.44f, 0.46f, 0.42f, 0.8f));
			}
			
			this.impacted = true;
		}
		
		if(id == EVENT_EXPLODE) {
			float radius = this.getBubbleRadius();
			
			for(int i = 0; i < 8; ++i) {
				BLParticles.FANCY_BUBBLE.spawn(getEntityWorld(), posX + (this.world.rand.nextFloat() - 0.5f) * radius, posY + (this.world.rand.nextFloat() - 0.5f) * radius, posZ + (this.world.rand.nextFloat() - 0.5f) * radius,
						ParticleArgs.get()
						.withColor(0.44f, 0.46f, 0.42f, 0.9f)
						.withScale(1.0f + this.world.rand.nextFloat()));
			}
			
			for(int j = 0; j < 25; ++j) {
				double ox = (rand.nextFloat() - 0.5f) * radius * 0.5f;
				double oy = (rand.nextFloat() - 0.5f) * radius * 0.5f;
				double oz = (rand.nextFloat() - 0.5f) * radius * 0.5f;
				double velX = ox * rand.nextFloat();
				double velY = 0.1f + rand.nextFloat() * 0.5f;
				double velZ = oz * rand.nextFloat();
				BLParticles.FANCY_DRIP.spawn(getEntityWorld(), this.posX + ox, this.posY + radius * 0.25f + oy, this.posZ + oz, ParticleArgs.get().withMotion(velX, velY, velZ).withScale(0.5f).withColor(0.44f, 0.46f, 0.42f, 1.0f));
			}
		}
	}

	@Override
	protected SoundEvent getSplashSound() {
		return SoundEvents.ENTITY_BOBBER_SPLASH;
	}

	@Override
	protected void onImpact(RayTraceResult mop) {
		if (!getEntityWorld().isRemote) {
			if (!impacted) {
				getEntityWorld().setEntityState(this, EVENT_IMPACT);
				getEntityWorld().playSound((EntityPlayer) null, getPosition(), getSplashSound(), SoundCategory.HOSTILE, 0.125F, 3.0F);
				impacted = true;
			}
			
			if (mop.typeOfHit != null && mop.typeOfHit == RayTraceResult.Type.BLOCK) {
				inGround = true;
				onGround = true;
				markVelocityChanged();
			}

			if (mop.entityHit != null) {
				if (mop.typeOfHit != null && mop.typeOfHit == RayTraceResult.Type.ENTITY && mop.entityHit != thrower && !(mop.entityHit instanceof EntityBubblerCrab)) {
					inGround = true;
					onGround = true;
					markVelocityChanged();
				}
			}
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