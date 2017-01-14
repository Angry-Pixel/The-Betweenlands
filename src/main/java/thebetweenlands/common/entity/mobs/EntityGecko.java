package thebetweenlands.common.entity.mobs;

import java.util.List;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.WeedWoodBushUncollidableEntity;
import thebetweenlands.common.entity.ai.EntityAIBLAvoidEntityGecko;
import thebetweenlands.common.network.message.clientbound.MessageWeedwoodBushRustle;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityGecko extends EntityCreature implements IEntityBL, WeedWoodBushUncollidableEntity {
	private static final DataParameter<Boolean> HIDING = EntityDataManager.createKey(EntityGecko.class, DataSerializers.BOOLEAN);

	private static final int MIN_HIDE_TIME = 20 * 60 * 2;

	private static final float UNHIDE_CHANCE = 0.1F;

	private static final int PLAYER_MIN_DISTANCE = 7;

	private BlockPos hidingBush;

	private int timeHiding;

	public EntityGecko(World worldObj) {
		super(worldObj);
		this.setPathPriority(PathNodeType.WATER, 0.0F);
		this.setSize(0.75F, 0.35F);
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new EntityAIPanic(this, 0.5));
		this.tasks.addTask(2, new EntityAIBLAvoidEntityGecko(this, EntityPlayer.class, PLAYER_MIN_DISTANCE, 0.3, 0.5));
		this.tasks.addTask(3, new EntityAIWander(this, 0.3));
		this.tasks.addTask(4, new EntityAIWatchClosest(this, EntityPlayer.class, 6));
		this.tasks.addTask(5, new EntityAILookIdle(this));
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(HIDING, false);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(12.0D);
	}

	public void setHidingBush(BlockPos pos) {
		this.hidingBush = pos;
	}

	@Override
	public boolean isInvisible() {
		return this.isHiding() || super.isInvisible();
	}

	private void setHiding(boolean isHiding) {
		this.dataManager.set(HIDING, isHiding);
	}

	public void startHiding() {
		this.setHiding(true);
		this.playSound(SoundRegistry.GECKO_HIDE, 0.5F, rand.nextFloat() * 0.3F + 0.9F);
		this.sendRustleEffect(1.0F);
		this.setPosition(this.hidingBush.getX() + 0.5, this.hidingBush.getY(), this.hidingBush.getZ() + 0.5);
		this.timeHiding = 0;
	}

	public void stopHiding() {
		setHiding(false);
		playSound(SoundRegistry.GECKO_HIDE, 0.25F, rand.nextFloat() * 0.3F + 0.9F);
		timeHiding = 0;
		float x = rand.nextFloat() * 2 - 1;
		float y = rand.nextFloat() * 0.5F;
		float z = rand.nextFloat() * 2 - 1;
		float len = MathHelper.sqrt_float(x * x + y * y + z * z);
		float mag = 0.6F;
		motionX += x / len * mag;
		motionY += y / len * mag;
		motionZ += z / len * mag;
	}

	private boolean hasValidHiding() {
		return worldObj.getBlockState(this.hidingBush).getBlock() == BlockRegistry.WEEDWOOD_BUSH;
	}

	private void sendRustleEffect(float strength) {
		if (!hasValidHiding()) {
			return;
		}
		MessageWeedwoodBushRustle message = new MessageWeedwoodBushRustle(this.hidingBush, strength);
		NetworkRegistry.TargetPoint target = new NetworkRegistry.TargetPoint(dimension, posX, posY, posZ, 16);
		TheBetweenlands.networkWrapper.sendToAllAround(message, target);
	}

	public boolean isHiding() {
		return this.dataManager.get(HIDING);
	}

	@Override
	public void moveEntity(double motionX, double motionY, double motionZ) {
		if (!isHiding()) {
			super.moveEntity(motionX, motionY, motionZ);
		}
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (!worldObj.isRemote) {
			if (isHiding()) {
				if (hasValidHiding()) {
					timeHiding++;
					if (rand.nextFloat() < 0.01F) {
						playSound(SoundRegistry.GECKO_HIDE, rand.nextFloat() * 0.05F + 0.02F, rand.nextFloat() * 0.2F + 0.8F);
						if (rand.nextFloat() < 0.3F) sendRustleEffect((rand.nextFloat() + 0.2F) * 0.06F);
					}
					if (timeHiding > MIN_HIDE_TIME) {
						List<EntityPlayer> players = worldObj.getEntitiesWithinAABB(EntityPlayer.class, this.getEntityBoundingBox().expand(PLAYER_MIN_DISTANCE, PLAYER_MIN_DISTANCE, PLAYER_MIN_DISTANCE));
						if (players.size() < 1 && rand.nextFloat() < UNHIDE_CHANCE) {
							stopHiding();
						}
					}
				} else {
					stopHiding();
				}
			}
		}
	}

	@Override
	public boolean isAIDisabled() {
		return false;
	}

	@Override
	protected float getSoundVolume() {
		return isHiding() ? super.getSoundVolume() * 0.1F : super.getSoundVolume();
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.GECKO_LIVING;
	}

	@Override
	protected SoundEvent getHurtSound() {
		return SoundRegistry.GECKO_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.GECKO_DEATH;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setBoolean("isHiding", isHiding());
		if (isHiding()) {
			compound.setInteger("hidingBushX", hidingBush.getX());
			compound.setInteger("hidingBushY", hidingBush.getY());
			compound.setInteger("hidingBushZ", hidingBush.getZ());
		}
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		if(compound.hasKey("isHiding")) {
			setHiding(compound.getBoolean("isHiding"));
			if (isHiding()) {
				setHidingBush(new BlockPos(compound.getInteger("hidingBushX"), compound.getInteger("hidingBushY"), compound.getInteger("hidingBushZ")));
			}
		}
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}

	@Override
	public boolean canBeCollidedWith() {
		return !this.isHiding();
	}
}
