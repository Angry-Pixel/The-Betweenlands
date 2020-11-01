package thebetweenlands.common.entity.mobs;

import java.util.List;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.WeedWoodBushUncollidableEntity;
import thebetweenlands.common.entity.ai.EntityAISeekRainShelter;
import thebetweenlands.common.entity.ai.gecko.EntityAIAvoidEntityGecko;
import thebetweenlands.common.entity.ai.gecko.EntityAIGeckoHideFromRain;
import thebetweenlands.common.network.clientbound.MessageWeedwoodBushRustle;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
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
		this.tasks.addTask(1, new EntityAIPanic(this, 1.0D));
		this.tasks.addTask(2, new EntityAITempt(this, 0.5D, ItemRegistry.SAP_SPIT, true));
		this.tasks.addTask(3, new EntityAIAvoidEntityGecko(this, EntityPlayer.class, PLAYER_MIN_DISTANCE, 0.65, 1));
		this.tasks.addTask(4, new EntityAIGeckoHideFromRain(this, 0.65));
		this.tasks.addTask(5, new EntityAISeekRainShelter(this, 0.65));
		this.tasks.addTask(6, new EntityAIWander(this, 0.6D));
		this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6));
		this.tasks.addTask(8, new EntityAILookIdle(this));
	}

	@Override
	public float getBlockPathWeight(BlockPos pos) {
		return this.world.isRainingAt(pos) ? -1.0F : 0.0F;
	}
	
	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(HIDING, false);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
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
		float len = MathHelper.sqrt(x * x + y * y + z * z);
		float mag = 0.6F;
		motionX += x / len * mag;
		motionY += y / len * mag;
		motionZ += z / len * mag;
	}

	private boolean hasValidHiding() {
		return world.getBlockState(this.hidingBush).getBlock() == BlockRegistry.WEEDWOOD_BUSH;
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
	public void move(MoverType moverType, double motionX, double motionY, double motionZ) {
		if (!isHiding()) {
			super.move(moverType, motionX, motionY, motionZ);
		}
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (!world.isRemote) {
			if (isHiding()) {
				if (hasValidHiding()) {
					timeHiding++;
					if (rand.nextFloat() < 0.01F) {
						playSound(SoundRegistry.GECKO_HIDE, rand.nextFloat() * 0.05F + 0.02F, rand.nextFloat() * 0.2F + 0.8F);
						if (rand.nextFloat() < 0.3F) sendRustleEffect((rand.nextFloat() + 0.2F) * 0.06F);
					}
					if (timeHiding > MIN_HIDE_TIME) {
						List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, this.getEntityBoundingBox().grow(PLAYER_MIN_DISTANCE, PLAYER_MIN_DISTANCE, PLAYER_MIN_DISTANCE));
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
	protected boolean processInteract(EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if(!stack.isEmpty() && stack.getItem() == ItemRegistry.SAP_SPIT && this.getHealth() < this.getMaxHealth()) {
			if(!this.world.isRemote) {
				this.heal(this.getMaxHealth());
			} else {
				this.spawnHeartParticles();
			}
			player.swingArm(hand);
			return true;
		}
		return false;
	}

	@SideOnly(Side.CLIENT)
	protected void spawnHeartParticles() {
		for (int i = 0; i < 7; ++i) {
			double d0 = this.rand.nextGaussian() * 0.02D;
			double d1 = this.rand.nextGaussian() * 0.02D;
			double d2 = this.rand.nextGaussian() * 0.02D;
			this.world.spawnParticle(EnumParticleTypes.HEART, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2, new int[0]);
		}
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
	protected SoundEvent getHurtSound(DamageSource damageSource) {
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
	
	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.GECKO;
	}
	
	@Override
	protected int getExperiencePoints(EntityPlayer player) {
		return 1 + this.rand.nextInt(3);
	}
}
