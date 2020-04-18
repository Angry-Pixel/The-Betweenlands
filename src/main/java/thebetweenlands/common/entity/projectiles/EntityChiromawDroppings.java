package thebetweenlands.common.entity.projectiles;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.render.particle.entity.ParticleGasCloud;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.EntitySplodeshroom;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.network.clientbound.PacketParticle;
import thebetweenlands.common.network.clientbound.PacketParticle.ParticleType;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityChiromawDroppings extends Entity {

	private static final DataParameter<Boolean> HAS_EXPLODED = EntityDataManager.createKey(EntitySplodeshroom.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Float> AOE_SIZE_XZ = EntityDataManager.createKey(EntitySplodeshroom.class, DataSerializers.FLOAT);
	private static final DataParameter<Float> AOE_SIZE_Y = EntityDataManager.createKey(EntitySplodeshroom.class, DataSerializers.FLOAT);

	public float prevRotationTicks = 0;
	public float rotationTicks = 0;
	protected EntityLivingBase thrower;
    public Entity ignoreEntity;
    private int ignoreTime;

	public EntityChiromawDroppings(World world) {
		super(world);
		setSize(0.5F, 0.5F);
		setEntityInvulnerable(true);
	}

	public EntityChiromawDroppings(World world, EntityLivingBase throwerIn, double x, double y, double z) {
		super(world);
		setSize(0.5F, 0.5F);
		setPosition(x, y, z);
		setEntityInvulnerable(true);
		thrower = throwerIn;
	}

	@Override
	protected void entityInit() {
		dataManager.register(HAS_EXPLODED, false);
		dataManager.register(AOE_SIZE_XZ, 4F);
		dataManager.register(AOE_SIZE_Y, 0.5F);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		// TODO in case we want an animation grab smoothedAngle(float partialTicks) method for render class
		prevRotationTicks = rotationTicks;
		rotationTicks += 15;
		float wrap = MathHelper.wrapDegrees(rotationTicks) - rotationTicks;
		rotationTicks +=wrap;
		prevRotationTicks += wrap;

		if (!getEntityWorld().isRemote) {
			if(getHasExploded()) {
				if (getEntityWorld().getTotalWorldTime() % 5 == 0)
					checkAreaOfEffect();
				if (getAOESizeXZ() > 0.5F)
					setAOESizeXZ(getAOESizeXZ() - 0.01F);
				if (getAOESizeXZ() <= 0.5F)
					setDead();
			}
			if (!getHasExploded())
				if (getEntityWorld().getTotalWorldTime() % 4 == 0)
					TheBetweenlands.networkWrapper.sendToAll(new PacketParticle(ParticleType.CHIROMAW_DROPPINGS, (float) posX, (float)posY + (float) motionY, (float)posZ, 1F));
		}

		if (getHasExploded())
			setBoundingBoxSize();

		if (getEntityWorld().isRemote)
			if(getHasExploded())
				spawnCloudParticle();

		lastTickPosX = posX;
        lastTickPosY = posY;
        lastTickPosZ = posZ;

        Vec3d poopVector = new Vec3d(posX, posY, posZ);
        Vec3d poopMovementVector = new Vec3d(posX + motionX, posY + motionY, posZ + motionZ);
        RayTraceResult raytraceresult = world.rayTraceBlocks(poopVector, poopMovementVector);
        poopVector = new Vec3d(posX, posY, posZ);
        poopMovementVector = new Vec3d(posX + motionX, posY + motionY, posZ + motionZ);

        if (raytraceresult != null)
        	poopMovementVector = new Vec3d(raytraceresult.hitVec.x, raytraceresult.hitVec.y, raytraceresult.hitVec.z);

        Entity entityCollidedWith = null;
        List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox().expand(motionX, motionY, motionZ).grow(1.0D));
        double collisionDistance = 0D;
        boolean miss = false;

        for (int entityCount = 0; entityCount < list.size(); ++entityCount)  {
            Entity entityInList = list.get(entityCount);

            if (entityInList.canBeCollidedWith()) {
                if (entityInList == ignoreEntity)
                	miss = true;
                else if (thrower != null && ticksExisted < 2 && ignoreEntity == null) {
                    ignoreEntity = entityInList;
                    miss = true;
                }
                else {
                	miss = false;
                    AxisAlignedBB axisalignedbb = entityInList.getEntityBoundingBox().grow(0.30000001192092896D);
                    RayTraceResult raytraceresult1 = axisalignedbb.calculateIntercept(poopVector, poopMovementVector);

                    if (raytraceresult1 != null) {
                        double inpactDistance = poopVector.squareDistanceTo(raytraceresult1.hitVec);

                        if (inpactDistance < collisionDistance || collisionDistance == 0.0D) {
                        	entityCollidedWith = entityInList;
                            collisionDistance = inpactDistance;
                        }
                    }
                }
            }
        }

        if (ignoreEntity != null)
            if (miss)
                ignoreTime = 2;
            else if (ignoreTime-- <= 0)
                ignoreEntity = null;

        if (entityCollidedWith != null)
            raytraceresult = new RayTraceResult(entityCollidedWith);

        if (raytraceresult != null)
            if (raytraceresult.typeOfHit == RayTraceResult.Type.BLOCK && world.getBlockState(raytraceresult.getBlockPos()).getBlock() == Blocks.PORTAL)
                setPortal(raytraceresult.getBlockPos());
            else if (!net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult))
                onImpact(raytraceresult);

        posX += motionX;
        posY += motionY;
        posZ += motionZ;

        float fallAmount = 0.99F;
        float fallAmountWithGravity = getGravityVelocity();

        if (isInWater()) {
            for (int particleCount = 0; particleCount < 4; ++particleCount)
                world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, posX - motionX * 0.25D, posY - motionY * 0.25D, posZ - motionZ * 0.25D, motionX, motionY, motionZ);
            fallAmount = 0.8F;
        }

        if(getHasExploded())
        	fallAmount = 0.0F;

        motionX *= (double)fallAmount;
        motionY *= (double)fallAmount;
        motionZ *= (double)fallAmount;

        if (!hasNoGravity())
            motionY -= (double)fallAmountWithGravity;

        if (raytraceresult != null)
        	if(getHasExploded())
        		if(posY < raytraceresult.hitVec.y)
        			posY = raytraceresult.hitVec.y;

        setPosition(posX, posY, posZ);
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
						if(!player.isSpectator() && !player.isCreative()) {
							player.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 60));
							player.addPotionEffect(ElixirEffectRegistry.EFFECT_DECAY.createEffect(40, 1));
						}
					}
				}
			}
		return entity;
	}

    protected float getGravityVelocity() {
    	if(!getHasExploded())
    		return 0.03F;
    	return 0F;
    }

    @SideOnly(Side.CLIENT)
    public float smoothedAngle(float partialTicks) {
        return prevRotationTicks + (rotationTicks - prevRotationTicks) * partialTicks;
    }

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
    protected boolean canBeRidden(Entity entityIn) {
        return false;
    }

	protected void onImpact(RayTraceResult result) {
		if (!getHasExploded() && result.typeOfHit != null) {
			if(result.typeOfHit == result.typeOfHit.BLOCK || result.typeOfHit == result.typeOfHit.ENTITY && !(result.entityHit instanceof EntityChiromawDroppings) && result.entityHit != thrower)
			if (!getEntityWorld().isRemote) {
				setHasExploded(true);
				getEntityWorld().playSound(null, getPosition(), SoundRegistry.CHIROMAW_MATRIARCH_SPLAT, SoundCategory.HOSTILE, 1F, 1F + (getEntityWorld().rand.nextFloat() - getEntityWorld().rand.nextFloat()) * 0.8F);
			}
		}
	}

	protected void setBoundingBoxSize() {
		AxisAlignedBB axisalignedbb = new AxisAlignedBB(posX - getAOESizeXZ() * 0.5D, posY, posZ - getAOESizeXZ() * 0.5D, posX + getAOESizeXZ() * 0.5D, posY + getAOESizeY(), posZ + getAOESizeXZ() * 0.5D);
		setEntityBoundingBox(axisalignedbb);
	}

	@Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        if (AOE_SIZE_XZ.equals(key)) 
            setAOESizeXZ(getAOESizeXZ());
        if (AOE_SIZE_Y.equals(key)) 
            setAOESizeY(getAOESizeY());
        super.notifyDataManagerChange(key);
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

	@SideOnly(Side.CLIENT)
	private void spawnCloudParticle() {
		double x = posX + (world.rand.nextFloat() - 0.5F) / 2.0F;
		double y = posY + 0.1D;
		double z = posZ + (world.rand.nextFloat() - 0.5F) / 2.0F;
		double mx = (world.rand.nextFloat() - 0.5F) / 12.0F;
		double my = (world.rand.nextFloat() - 0.5F) / 16.0F * 0.1F;
		double mz = (world.rand.nextFloat() - 0.5F) / 12.0F;
		int[] color = {100, 100, 0, 255};

		ParticleGasCloud hazeParticle = (ParticleGasCloud) BLParticles.GAS_CLOUD
				.create(world, x, y, z, ParticleFactory.ParticleArgs.get()
						.withData(null)
						.withMotion(mx, my, mz)
						.withColor(color[0] / 255.0F, color[1] / 255.0F, color[2] / 255.0F, color[3] / 255.0F)
						.withScale(8f));
		
		BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.GAS_CLOUDS_HEAT_HAZE, hazeParticle);
		
		ParticleGasCloud particle = (ParticleGasCloud) BLParticles.GAS_CLOUD
				.create(world, x, y, z, ParticleFactory.ParticleArgs.get()
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

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
	}
}
