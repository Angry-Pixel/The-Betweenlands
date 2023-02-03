package thebetweenlands.common.entity;


import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.block.plant.BlockMouldHornMushroom;
import thebetweenlands.common.block.plant.BlockMouldHornMushroom.EnumMouldHorn;

public class EntityPuffshroomBuilder extends EntityCreature implements IEntityBL {
	private static final DataParameter<Boolean> IS_MIDDLE = EntityDataManager.createKey(EntityPuffshroomBuilder.class, DataSerializers.BOOLEAN);

	public EntityPuffshroomBuilder (World world) {
		super(world);
		setSize(0.5F, 0.5F);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(IS_MIDDLE, false);
	}

	public void setIsMiddle(boolean state) {
		dataManager.set(IS_MIDDLE, state);
	}

	public boolean getIsMiddle() {
		return dataManager.get(IS_MIDDLE);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0D);
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(5.0D);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (world.getTotalWorldTime() % 20 == 0) {
			if (!world.isRemote) {
				checkForMiddle();
				if (getIsMiddle())
					if (checkForMouldhorns(world, getPosition())) {
						System.out.println("Mould horns all there");
						//set the soil patches, break the mould horns kill all the tendril entities and spawn the new entity.
						//easy right?
					}
			}
			if (world.isRemote) {
				if (getIsMiddle())
					if (checkForMouldhorns(world, getPosition())) {

					}
			}
		}
	}

	private boolean checkForMouldhorns(World world, BlockPos pos) {
		int count = 0;
		if(checkForCap(world, pos.add(6, 0, 0)))
			count++;
		if(checkForCap(world, pos.add(0, 0, 6)))
			count++;
		if(checkForCap(world, pos.add(-6, 0, 0)))
			count++;
		if(checkForCap(world, pos.add(0, 0, -6)))
			count++;
		if(checkForCap(world, pos.add(5, 0, 5)))
			count++;
		if(checkForCap(world, pos.add(5, 0, -5)))
			count++;
		if(checkForCap(world, pos.add(-5, 0, 5)))
			count++;
		if(checkForCap(world, pos.add(-5, 0, -5)))
			count++;
		System.out.println("Mould horns found:" + count);
		return count >=8;
	}

	private boolean checkForCap(World world, BlockPos pos) {
		for(int y = 0; y <= 6; y++)
			if (world.getBlockState(pos.add(0, y, 0)).getBlock() instanceof BlockMouldHornMushroom && world.getBlockState(pos.add(0, y, 0)).getValue(BlockMouldHornMushroom.MOULD_HORN_TYPE) == EnumMouldHorn.MOULD_HORN_CAP_FULL_WARTS) {
				if (world.isRemote) {
					spawnBrazierParticles(new Vec3d(pos.getX() - getPosition().getX(), y + 0.75D, pos.getZ() - getPosition().getZ()));
				}
				
				return true;
				}
		return false;
	}
	
	@SideOnly(Side.CLIENT)
	private void spawnBrazierParticles(Vec3d target) {
		BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.BEAM, BLParticles.PUZZLE_BEAM_2.create(world, this.posX + target.x, this.posY + target.y, this.posZ + target.z, ParticleArgs.get().withMotion(0, 0, 0).withColor(255F, 102F, 0F, 1F).withScale(1.5F).withData(30, target.scale(-1))));
		for(int i = 0; i < 2; i++) {
			float offsetLen = this.world.rand.nextFloat();
			Vec3d offset = new Vec3d(target.x * offsetLen + world.rand.nextFloat() * 0.2f - 0.1f, target.y * offsetLen + world.rand.nextFloat() * 0.2f - 0.1f, target.z * offsetLen + world.rand.nextFloat() * 0.2f - 0.1f);
			float vx = (world.rand.nextFloat() * 2f - 1) * 0.0025f;
			float vy = (world.rand.nextFloat() * 2f - 1) * 0.0025f + 0.008f;
			float vz = (world.rand.nextFloat() * 2f - 1) * 0.0025f;
			float scale = 0.5f + world.rand.nextFloat();
			BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR, BLParticles.PUZZLE_BEAM.create(world, this.posX + offset.x, this.posY + offset.y, this.posZ + offset.z, ParticleArgs.get().withMotion(vx, vy, vz).withColor(255F, 102F, 0F, 1F).withScale(scale).withData(100)));

		}
	}

	private void checkForMiddle() {
		List<Entity> list = getEntityWorld().getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox().grow(0.6D, 0D, 0.6D));
		if(list.stream().filter(e -> e instanceof EntityPuffshroomBuilder).count() == 8)
			setIsMiddle(true);
		else
			setIsMiddle(false);
	}

	@Override
	public void onLivingUpdate() {
		if(getIsMiddle() && world.isRemote) {
			BLParticles.REDSTONE_DUST.spawn(world, posX + (rand.nextDouble() - 0.5D) * width, posY + rand.nextDouble() * height, posZ + (rand.nextDouble() - 0.5D) * width, 
				ParticleArgs.get().withColor(0.5F + this.rand.nextFloat() * 0.5F, 0.5F + this.rand.nextFloat() * 0.5F, 0.5F + this.rand.nextFloat() * 0.5F, 1.0F));
		}
		super.onLivingUpdate();
	}

	@Override
	public boolean isNotColliding() {
		return true;
	}

	@Override
    public EnumPushReaction getPushReaction() {
        return EnumPushReaction.IGNORE;
    }

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
    public boolean canBePushed() {
        return false;
    }

	@Override
	public void addVelocity(double x, double y, double z) {
		motionX = 0;
		motionY = 0;
		motionZ = 0;
	}

	@Override
	public boolean getIsInvulnerable() {
		return true;
	}

    @Override
    public boolean getCanSpawnHere() {
        return true;
    }

	@Nullable
	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
		if (!getEntityWorld().isRemote)
			checkForMiddle();
		return livingdata;
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		setIsMiddle(nbt.getBoolean("isMiddle"));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setBoolean("isMiddle", getIsMiddle());
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
}
