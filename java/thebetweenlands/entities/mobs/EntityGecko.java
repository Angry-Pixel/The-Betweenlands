package thebetweenlands.entities.mobs;

import java.util.List;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.entities.WeedWoodBushUncollidableEntity;
import thebetweenlands.entities.entityAI.EntityAIBLAvoidEntityGecko;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.lib.ModInfo;
import thebetweenlands.network.packet.server.PacketWeedWoodBushRustle;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;

public class EntityGecko extends EntityCreature implements IEntityBL, WeedWoodBushUncollidableEntity {
	private static final int HIDING_ID = 20;

	private static final int MIN_HIDE_TIME = 20 * 60 * 2;

	private static final float UNHIDE_CHANCE = 0.1F;

	private static final int PLAYER_MIN_DISTANCE = 7;

	private int hidingBushX;

	private int hidingBushY;

	private int hidingBushZ;

	private int timeHiding;

	public EntityGecko(World worldObj) {
		super(worldObj);
		getNavigator().setAvoidsWater(true);
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new EntityAIPanic(this, 0.5));
		tasks.addTask(5, new EntityAIWander(this, 0.3));
		tasks.addTask(2, new EntityAIBLAvoidEntityGecko(this, EntityPlayer.class, PLAYER_MIN_DISTANCE, 0.3, 0.5));
		tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6));
		tasks.addTask(7, new EntityAILookIdle(this));
		setSize(0.5F, 0.5F);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataWatcher.addObject(HIDING_ID, (byte) 0);
	}

	public void setHidingBush(int x, int y, int z) {
		hidingBushX = x;
		hidingBushY = y;
		hidingBushZ = z;
	}

	@Override
	public boolean isInvisible() {
		return isHiding() ? true : super.isInvisible();
	}

	private void setHiding(boolean isHiding) {
		dataWatcher.updateObject(HIDING_ID, (byte) (isHiding ? 1 : 0));
	}

	public void startHiding() {
		setHiding(true);
		playSound(ModInfo.ID + ":geckoHide", 0.5F, rand.nextFloat() * 0.3F + 0.9F);
		sendRustleEffect(1.0F);
		setPosition(hidingBushX + 0.5, hidingBushY + 0.25, hidingBushZ + 0.5);
		timeHiding = 0;
	}

	public void stopHiding() {
		setHiding(false);
		playSound(ModInfo.ID + ":geckoHide", 0.25F, rand.nextFloat() * 0.3F + 0.9F);
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

	private boolean isWeedWoodBushAtHidingSpot() {
		return worldObj.getBlock(hidingBushX, hidingBushY, hidingBushZ) == BLBlockRegistry.weedwoodBush;
	}

	private void sendRustleEffect(float strength) {
		if (!isWeedWoodBushAtHidingSpot()) {
			return;
		}
		PacketWeedWoodBushRustle packet = new PacketWeedWoodBushRustle(hidingBushX, hidingBushY, hidingBushZ, strength);
		TargetPoint target = new TargetPoint(dimension, posX, posY, posZ, 16);
		TheBetweenlands.networkWrapper.sendToAllAround(TheBetweenlands.sidedPacketHandler.wrapPacket(packet), target);
	}

	public boolean isHiding() {
		return dataWatcher.getWatchableObjectByte(HIDING_ID) != 0;
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
				if (isWeedWoodBushAtHidingSpot()) {
					timeHiding++;
					if (rand.nextFloat() < 0.01F) {
						playSound(ModInfo.ID + ":geckoHide", rand.nextFloat() * 0.05F + 0.02F, rand.nextFloat() * 0.2F + 0.8F);
						if(rand.nextFloat() < 0.3F) sendRustleEffect((rand.nextFloat() + 0.2F) * 0.06F);
					}
					if (timeHiding > MIN_HIDE_TIME) {
						List<EntityPlayer> players = worldObj.getEntitiesWithinAABB(EntityPlayer.class, boundingBox.expand(PLAYER_MIN_DISTANCE, PLAYER_MIN_DISTANCE, PLAYER_MIN_DISTANCE));
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
	public boolean isAIEnabled() {
		return true;
	}

	@Override
	protected float getSoundVolume() {
		return isHiding() ? super.getSoundVolume() * 0.1F : super.getSoundVolume();
	}

	@Override
	protected String getLivingSound() {
		return ModInfo.ID + ":geckoLiving";
	}

	@Override
	protected String getHurtSound() {
		return ModInfo.ID + ":geckoHurt";
	}

	@Override
	protected String getDeathSound() {
		return ModInfo.ID + ":geckoDeath";
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setBoolean("isHiding", isHiding());
		if (isHiding()) {
			compound.setInteger("hidingBushX", hidingBushX);
			compound.setInteger("hidingBushY", hidingBushY);
			compound.setInteger("hidingBushZ", hidingBushZ);
		}
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		setHiding(compound.getBoolean("isHiding"));
		if (isHiding()) {
			setHidingBush(compound.getInteger("hidingBushX"), compound.getInteger("hidingBushY"), compound.getInteger("hidingBushZ"));
		}
	}
}
