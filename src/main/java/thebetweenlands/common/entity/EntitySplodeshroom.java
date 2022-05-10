package thebetweenlands.common.entity;


import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleBreaking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.render.particle.entity.ParticleGasCloud;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntitySplodeshroom extends EntityProximitySpawner {
	private static final byte EVENT_EXPLODE_PARTICLES = 100;
	
	public int MAX_SWELL = 40;
	public int MIN_SWELL = 0;
	private static final DataParameter<Boolean> IS_SWELLING = EntityDataManager.createKey(EntitySplodeshroom.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> SWELL_COUNT = EntityDataManager.createKey(EntitySplodeshroom.class, DataSerializers.VARINT);
	private static final DataParameter<Boolean> HAS_EXPLODED = EntityDataManager.createKey(EntitySplodeshroom.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Float> AOE_SIZE_XZ = EntityDataManager.createKey(EntitySplodeshroom.class, DataSerializers.FLOAT);
	private static final DataParameter<Float> AOE_SIZE_Y = EntityDataManager.createKey(EntitySplodeshroom.class, DataSerializers.FLOAT);

	public EntitySplodeshroom(World world) {
		super(world);
		setSize(0.5F, 1F);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(IS_SWELLING, false);
		dataManager.register(SWELL_COUNT, 0);
		dataManager.register(HAS_EXPLODED, false);
		dataManager.register(AOE_SIZE_XZ, 4F);
		dataManager.register(AOE_SIZE_Y, 0.5F);
	}

	@Override
	public void onUpdate() {
		// super.onUpdate();
		if (!getEntityWorld().isRemote && getEntityWorld().getTotalWorldTime() % 5 == 0) {
			if (!getHasExploded())
				checkArea();
			if (getHasExploded())
				checkAreaOfEffect();
		}

		if (!getEntityWorld().isRemote) {
			if (!getSwelling() && getSwellCount() > MIN_SWELL)
				setSwellCount(getSwellCount() - 1);

			if (getSwelling() && getSwellCount() < MAX_SWELL)
				setSwellCount(getSwellCount() + 1);
			
			if(getHasExploded()) {
				if (getSwellCount() < MAX_SWELL)
					setSwellCount(MAX_SWELL);
				if (getAOESizeXZ() > 0.5F)
					setAOESizeXZ(getAOESizeXZ() - 0.01F);
				if (getAOESizeXZ() <= 0.5F)
					setDead();
			}
		}

		if (getHasExploded())
			setBoundingBoxSize();
		
		if (getEntityWorld().isRemote)
			if(getHasExploded())
				spawnCloudParticle();
	}

	@Override
	@Nullable
	protected Entity checkArea() {
		Entity entity = null;
		if (!getEntityWorld().isRemote && getEntityWorld().getDifficulty() != EnumDifficulty.PEACEFUL) {
			List<EntityPlayer> list = getEntityWorld().getEntitiesWithinAABB(EntityPlayer.class, proximityBox());
			for (int entityCount = 0; entityCount < list.size(); entityCount++) {
				entity = list.get(entityCount);

				if (entity != null) {
					if (entity instanceof EntityPlayer && !((EntityPlayer) entity).isSpectator() && !((EntityPlayer) entity).isCreative()) {
						if (canSneakPast() && entity.isSneaking())
							return null;
						else if (checkSight() && !canEntityBeSeen(entity))
							return null;
						else {
							if(!getSwelling())
								setSwelling(true);
						}
						if (!isDead && isSingleUse() && getSwellCount() >= MAX_SWELL) {
							explode();
						}
					}
				}
			}
			if (entity == null) {
				if (getSwelling())
					setSwelling(false);
			}
		}
		return entity;
	}

	@Nullable
	protected Entity checkAreaOfEffect() {
		Entity entity = null;
		if (!getEntityWorld().isRemote && getEntityWorld().getDifficulty() != EnumDifficulty.PEACEFUL) {
			List<EntityPlayer> list = getEntityWorld().getEntitiesWithinAABB(EntityPlayer.class, getEntityBoundingBox());
			for (int entityCount = 0; entityCount < list.size(); entityCount++) {
				entity = list.get(entityCount);
				if (entity != null)
					if (entity instanceof EntityPlayer) {
						EntityPlayer player = (EntityPlayer) entity;
						if(!isWearingSilkMask(player)) {
							if(!player.isSpectator() && !player.isCreative()) {
								player.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 60));
								player.addPotionEffect(ElixirEffectRegistry.EFFECT_DECAY.createEffect(40, 1));
							}
						}
					}
				}
			}
		return entity;
	}

    public boolean isWearingSilkMask(EntityLivingBase entity) {
    	if(entity instanceof EntityPlayer) {
        	ItemStack helmet = ((EntityPlayer)entity).getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        	if(!helmet.isEmpty() && helmet.getItem() == ItemRegistry.SILK_MASK) {
        		return true;
        	}
        }
    	return false;
    }

	private void explode() {
		this.world.setEntityState(this, EVENT_EXPLODE_PARTICLES);
		getEntityWorld().playSound((EntityPlayer)null, getPosition(), SoundRegistry.SPLODESHROOM_POP, SoundCategory.HOSTILE, 0.5F, 1F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
		setHasExploded(true);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void handleStatusUpdate(byte id) {
		super.handleStatusUpdate(id);

		if(id == EVENT_EXPLODE_PARTICLES) {
			for (int count = 0; count <= 200; ++count) {
				Particle fx = new ParticleBreaking.SnowballFactory().createParticle(EnumParticleTypes.SNOWBALL.getParticleID(), world, this.posX + (world.rand.nextDouble() - 0.5D), this.posY + 0.25f + world.rand.nextDouble(), this.posZ + (world.rand.nextDouble() - 0.5D), 0, 0, 0, 0);
				fx.setRBGColorF(128F, 203F, 175F);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
		}
	}
	
	@Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        if (AOE_SIZE_XZ.equals(key)) 
            setAOESizeXZ(getAOESizeXZ());
        if (AOE_SIZE_Y.equals(key)) 
            setAOESizeY(getAOESizeY());
        super.notifyDataManagerChange(key);
    }

	protected void setBoundingBoxSize() {
		AxisAlignedBB axisalignedbb = new AxisAlignedBB(posX - getAOESizeXZ() * 0.5D, posY, posZ - getAOESizeXZ() * 0.5D, posX + getAOESizeXZ() * 0.5D, posY + getAOESizeY(), posZ + getAOESizeXZ() * 0.5D);
		setEntityBoundingBox(axisalignedbb);
	}

	private void setSwelling(boolean swell) {
		dataManager.set(IS_SWELLING, swell);
		//probably doesn't work
		if(swell)
			getEntityWorld().playSound((EntityPlayer)null, getPosition(), SoundRegistry.SPLODESHROOM_WINDUP, SoundCategory.HOSTILE, 0.5F, 1F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
		else
			getEntityWorld().playSound((EntityPlayer)null, getPosition(), SoundRegistry.SPLODESHROOM_WINDDOWN, SoundCategory.HOSTILE, 0.5F, 1F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
	}

    public boolean getSwelling() {
        return dataManager.get(IS_SWELLING);
    }

	private void setSwellCount(int swellCountIn) {
		dataManager.set(SWELL_COUNT, swellCountIn);
	}

	public int getSwellCount() {
		return dataManager.get(SWELL_COUNT);
	}

	private void setHasExploded(boolean hasExploded) {
		dataManager.set(HAS_EXPLODED, hasExploded);
	}

    public boolean getHasExploded() {
        return dataManager.get(HAS_EXPLODED);
    }

	private void setAOESizeXZ(float aoeSizeXZ) {
		dataManager.set(AOE_SIZE_XZ, aoeSizeXZ);
	}

	public float getAOESizeXZ() {
		return dataManager.get(AOE_SIZE_XZ);
	}

	private void setAOESizeY(float aoeSizeY) {
		dataManager.set(AOE_SIZE_Y, aoeSizeY);
	}

	public float getAOESizeY() {
		return dataManager.get(AOE_SIZE_Y);
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
    public boolean canBeCollidedWith() {
        return !this.getHasExploded();
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
		if(source == DamageSource.OUT_OF_WORLD) {
			return true;
		}
		if (source instanceof EntityDamageSource) {
			Entity sourceEntity = ((EntityDamageSource) source).getTrueSource();
			if (sourceEntity instanceof EntityPlayer) {
				if (!getEntityWorld().isRemote) {
					if(!getHasExploded())
						explode();
				}
			}
			return true;
		}
		return false;
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
		return null;
	}

	@Override
	protected int getEntitySpawnCount() {
		return 1;
	}

	@Override
	protected boolean isSingleUse() {
		return true;
	}

	@Override
	protected int maxUseCount() {
		return 0;
	}
	
	@SideOnly(Side.CLIENT)
	private void spawnCloudParticle() {
		double x = this.posX + (this.world.rand.nextFloat() - 0.5F) / 2.0F;
		double y = this.posY + 0.1D;
		double z = this.posZ + (this.world.rand.nextFloat() - 0.5F) / 2.0F;
		double mx = (this.world.rand.nextFloat() - 0.5F) / 12.0F;
		double my = (this.world.rand.nextFloat() - 0.5F) / 16.0F * 0.1F;
		double mz = (this.world.rand.nextFloat() - 0.5F) / 12.0F;
		int[] color = {100, 100, 0, 255};

		ParticleGasCloud hazeParticle = (ParticleGasCloud) BLParticles.GAS_CLOUD
				.create(this.world, x, y, z, ParticleFactory.ParticleArgs.get()
						.withData(null)
						.withMotion(mx, my, mz)
						.withColor(color[0] / 255.0F, color[1] / 255.0F, color[2] / 255.0F, color[3] / 255.0F)
						.withScale(8f));
		
		BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.GAS_CLOUDS_HEAT_HAZE, hazeParticle);
		
		ParticleGasCloud particle = (ParticleGasCloud) BLParticles.GAS_CLOUD
				.create(this.world, x, y, z, ParticleFactory.ParticleArgs.get()
						.withData(null)
						.withMotion(mx, my, mz)
						.withColor(color[0] / 255.0F, color[1] / 255.0F, color[2] / 255.0F, color[3] / 255.0F)
						.withScale(4f));

		BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.GAS_CLOUDS_TEXTURED, particle);
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 1;
	}
}