package thebetweenlands.common.entity.mobs;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.network.clientbound.MessageMireSnailEggHatching;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.util.AnimationMathHelper;

public class EntityMireSnailEgg extends EntityAnimal implements IEntityBL {
	private static final DataParameter<Integer> HATCH_TICKS = EntityDataManager.createKey(EntityMireSnailEgg.class, DataSerializers.VARINT);
	public float pulseFloat;
	AnimationMathHelper pulse = new AnimationMathHelper();

	public EntityMireSnailEgg(World world) {
		super(world);
		setSize(0.35F, 0.35F);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(HATCH_TICKS, 0);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (getGrowingAge() < 0 || getGrowingAge() > 0) // stupid hack to stop entity scaling
		setGrowingAge(0);
		if (!worldObj.isRemote) {
			if (getHatchTime() < 24000)
				setHatchTime(getHatchTime() + 1);
			if (getHatchTime() >= 24000) //this should be 24000 = 1 day (20 mins)
				hatch();
		}
		pulseFloat = pulse.swing(0.3F, 0.2F, false);
		renderYawOffset = prevRenderYawOffset;
	}

	private void hatch() {
		EntityMireSnail snail = new EntityMireSnail(worldObj);
		snail.setPosition(posX, posY, posZ);
		if (snail.getCanSpawnHere()) {
			setDead();
			hatchParticlePacketTarget();
			snail.setHasMated(true);
			worldObj.spawnEntityInWorld(snail);
		}
	}

	private void hatchParticlePacketTarget() {
		TheBetweenlands.networkWrapper.sendToAllAround(new MessageMireSnailEggHatching(this), new TargetPoint(this.dimension, this.posX, this.posY, this.posZ, 32D));
	}

	@Override
	protected boolean isMovementBlocked() {
		return true;
	}

	@Override
	public EntityAgeable createChild(EntityAgeable entityAgeable) {
		return null;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0D);
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(5.0D);
	}

	public int getHatchTime() {
		return dataManager.get(HATCH_TICKS);
	}

	public void setHatchTime(int hatchTime) {
		dataManager.set(HATCH_TICKS, hatchTime);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setInteger("hatchTicks", getHatchTime());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		if(nbt.hasKey("hatchTicks")) {
			setHatchTime(nbt.getInteger("hatchTicks"));
		}
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.MIRE_SNAIL_EGG;
	}
}
