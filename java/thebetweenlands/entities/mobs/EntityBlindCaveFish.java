package thebetweenlands.entities.mobs;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.utils.AnimationMathHelper;

public class EntityBlindCaveFish extends EntityWaterMob implements IEntityBL, IMob {

    private ChunkCoordinates currentSwimTarget;
    AnimationMathHelper animation = new AnimationMathHelper();
    public float moveProgress;

	public EntityBlindCaveFish(World world) {
		super(world);
		setSize(0.3F, 0.2F);
		setAir(80);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(1D);
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(3.0D);
	}

	@Override
	protected String getHurtSound() {
		if (rand.nextBoolean())
			return "thebetweenlands:anglerAttack1";
		else
			return "thebetweenlands:anglerAttack2";
	}

	@Override
	protected String getDeathSound() {
		return "thebetweenlands:anglerDeath";
	}
	
	@Override
	protected void func_145780_a(int x, int y, int z, Block block) {
		if (rand.nextInt(10) == 0)
			playSound("game.hostile.swim", 0.1F, 2.0F);
	}

	@Override
	protected float getSoundVolume() {
		return 0.4F;
	}

	@Override
	public boolean getCanSpawnHere() {
		return worldObj.getBlock((int) posX, (int) posY, (int) posZ) == BLBlockRegistry.swampWater;
	}

	@Override
    public boolean isInWater() {
        return worldObj.handleMaterialAcceleration(boundingBox, Material.water, this) || worldObj.getBlock((int) posX, (int) posY, (int) posZ) == BLBlockRegistry.swampWater;
    }

	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (isInWater()) {
        	moveProgress = animation.swing(1.2F, 0.4F, false);
        	if (!worldObj.isRemote) {
    				swimAbout();
        	}
            renderYawOffset += (-((float)Math.atan2(motionX, motionZ)) * 180.0F / (float)Math.PI - renderYawOffset) * 0.1F;
            rotationYaw = renderYawOffset;
        }
    }

	@Override
	public void onEntityUpdate() {
		int air = getAir();
		super.onEntityUpdate();

		if (isEntityAlive() && !isInWater()) {
			--air;
			setAir(air);

			if (getAir() == -20) {
				setAir(0);
				attackEntityFrom(DamageSource.drown, 2.0F);
			}
		} else
			setAir(80);
	}

	public void swimAbout() {
		if (currentSwimTarget != null && (worldObj.getBlock(currentSwimTarget.posX, currentSwimTarget.posY, currentSwimTarget.posZ) != BLBlockRegistry.swampWater && worldObj.getBlock(currentSwimTarget.posX, currentSwimTarget.posY, currentSwimTarget.posZ) != Blocks.water || currentSwimTarget.posY < 1))
			currentSwimTarget = null;

		if (currentSwimTarget == null || rand.nextInt(30) == 0 || currentSwimTarget.getDistanceSquared((int) posX, (int) posY, (int) posZ) < 10.0F)
			currentSwimTarget = new ChunkCoordinates((int) posX + rand.nextInt(10) - rand.nextInt(10), (int) posY - rand.nextInt(3) + 1, (int) posZ + rand.nextInt(10) - rand.nextInt(10));

		swimToTarget();
	}

	protected void swimToTarget() {
		double targetX = currentSwimTarget.posX + 0.5D - posX;
		double targetY = currentSwimTarget.posY +0.5D - posY;
		double targetZ = currentSwimTarget.posZ + 0.5D - posZ;
		motionX += (Math.signum(targetX) * 0.3D - motionX) * 0.010000000149011612D;
		motionY += (Math.signum(targetY) * 0.3D - motionY) * 0.010000000149011612D;
		motionY -= 0.01D;
		motionZ += (Math.signum(targetZ) * 0.3D - motionZ) * 0.010000000149011612D;
		moveForward = 0.5F;

	}

}

