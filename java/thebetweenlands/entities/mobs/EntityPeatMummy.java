package thebetweenlands.entities.mobs;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.entities.entityAI.EntityAIApproachItem;
import thebetweenlands.items.BLItemRegistry;

public class EntityPeatMummy extends EntityMob implements IEntityBL {
	public static final IAttribute SPAWN_LENGTH_ATTRIB = (new RangedAttribute("bl.spawnLength", 100.0D, 0.0D, Integer.MAX_VALUE)).setDescription("Spawning Length");
	public static final IAttribute SPAWN_OFFSET_ATTRIB = (new RangedAttribute("bl.spawnOffset", 2.0D, -Integer.MAX_VALUE, Integer.MAX_VALUE)).setDescription("Spawning Y Offset");
	public static final IAttribute SPAWN_RANGE_ATTRIB = (new RangedAttribute("bl.spawnRange", 8.0D, 0, Double.MAX_VALUE)).setDescription("Spawning Range");

	public static final IAttribute CHARGING_COOLDOWN_ATTRIB = (new RangedAttribute("bl.chargingCooldown", 160.0D, 0, Integer.MAX_VALUE)).setDescription("Charging Cooldown");
	public static final IAttribute CHARGING_PREPARATION_SPEED_ATTRIB = (new RangedAttribute("bl.chargingPreparationSpeed", 60.0D, 0, Integer.MAX_VALUE)).setDescription("Charging Preparation Speed");
	public static final IAttribute CHARGING_TIME_ATTRIB = (new RangedAttribute("bl.chargingTime", 320.0D, 0, Integer.MAX_VALUE)).setDescription("Charging Time");
	public static final IAttribute CHARGING_SPEED_ATTRIB = (new RangedAttribute("bl.chargingSpeed", 0.55D, 0, Double.MAX_VALUE)).setDescription("Charging Movement Speed");
	public static final IAttribute CHARGING_DAMAGE_MULTIPLIER_ATTRIB = (new RangedAttribute("bl.chargingDamageMultiplier", 2.0D, 0, Double.MAX_VALUE)).setDescription("Charging Damage Multiplier");

	private static final int BREAK_COUNT = 5;

	public static final float BASE_SPEED = 0.2F;
	public static final float BASE_DAMAGE = 8.0F;

	private float prevYOffset;
	public static final int SPAWNING_STATE_DW = 20;

	private int chargingCooldown = (int) CHARGING_COOLDOWN_ATTRIB.getDefaultValue();
	private int chargingPreparation;
	private int chargingTime;
	public static final int CHARGING_STATE_DW = 21;

	//Scream timer is only used for the screen shake and is client side only.
	private int prevScreamTimer;
	private int screamTimer;
	private boolean screaming = false;
	private boolean wasScreaming = false;
	//Adjust to length of screaming sound
	private static final int SCREAMING_TIMER_MAX = 50;

	private boolean carryShimmerstone = false;

	private static final List<Block> SPAWN_BLOCKS = new ArrayList<Block>();
	static {
		SPAWN_BLOCKS.add(BLBlockRegistry.mud);
		SPAWN_BLOCKS.add(BLBlockRegistry.peat);
	}

	public EntityPeatMummy(World world) {
		super(world);
		setSize(1.2F, 1.2F);

		this.getNavigator().setCanSwim(true);

		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new EntityAIApproachItem(this, BLItemRegistry.shimmerStone, 80, 64, 1.9F, 1.5F) {
			@Override
			protected double getNearSpeed() {
				if(EntityPeatMummy.this.isCharging()) {
					return 1.0F;
				} else {
					return super.getNearSpeed();
				}
			}
			@Override
			protected double getFarSpeed() {
				if(EntityPeatMummy.this.isCharging()) {
					return 1.0F;
				} else {
					return super.getFarSpeed();
				}
			}
			@Override
			protected void onPickup() {
				EntityPeatMummy entity = EntityPeatMummy.this;
				if(entity.isCharging()) {
					entity.stopCharging();
				}
				entity.carryShimmerstone = true;
			}
		});
		this.tasks.addTask(2, new EntityAIWatchClosest(this, EntityPlayer.class, 16.0F));
		this.tasks.addTask(3, new EntityAIAttackOnCollide(this, EntityLivingBase.class, 1D, false));
		this.tasks.addTask(4, new EntityAIWander(this, 1D));
		this.tasks.addTask(5, new EntityAILookIdle(this));
		this.targetTasks.addTask(0, new EntityAIHurtByTarget(this, true));
		this.targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(BASE_SPEED);
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(110.0D);
		getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(BASE_DAMAGE);
		getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(80.0D);
		getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(1.0D);

		this.getAttributeMap().registerAttribute(SPAWN_LENGTH_ATTRIB);
		this.getAttributeMap().registerAttribute(SPAWN_OFFSET_ATTRIB);
		this.getAttributeMap().registerAttribute(SPAWN_RANGE_ATTRIB);
		this.getAttributeMap().registerAttribute(CHARGING_COOLDOWN_ATTRIB);
		this.getAttributeMap().registerAttribute(CHARGING_PREPARATION_SPEED_ATTRIB);
		this.getAttributeMap().registerAttribute(CHARGING_TIME_ATTRIB);
		this.getAttributeMap().registerAttribute(CHARGING_SPEED_ATTRIB);
		this.getAttributeMap().registerAttribute(CHARGING_DAMAGE_MULTIPLIER_ATTRIB);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataWatcher.addObject(SPAWNING_STATE_DW, (int) 0);
		dataWatcher.addObject(CHARGING_STATE_DW, (byte) 0);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		nbt.setInteger("mudSpawningState", this.getSpawningState());
		nbt.setDouble("initialPosY", this.posY);
		nbt.setFloat("previousYOffset", this.prevYOffset);
		nbt.setInteger("chargingCooldown", this.chargingCooldown);
		nbt.setInteger("chargingPreparation", this.chargingPreparation);
		nbt.setInteger("chargingTime", this.chargingTime);
		nbt.setByte("chargingState", this.dataWatcher.getWatchableObjectByte(CHARGING_STATE_DW));
		nbt.setBoolean("carryShimmerstone", this.carryShimmerstone);

		super.writeEntityToNBT(nbt);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		this.dataWatcher.updateObject(SPAWNING_STATE_DW, nbt.getInteger("mudSpawningState"));
		this.posY = nbt.getDouble("initialPosY");
		this.yOffset = nbt.getFloat("previousYOffset");
		this.chargingCooldown = nbt.getInteger("chargingCooldown");
		this.chargingPreparation = nbt.getInteger("chargingPreparation");
		this.chargingTime = nbt.getInteger("chargingTime");
		this.dataWatcher.updateObject(CHARGING_STATE_DW, nbt.getByte("chargingState"));
		this.carryShimmerstone = nbt.getBoolean("carryShimmerstone");

		super.readEntityFromNBT(nbt);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if(this.worldObj.isRemote) {
			if(this.getSpawningProgress() != 1.0F && this.getSpawningProgress() != 0.0F) {
				this.yOffset = this.getCurrentOffset();
				this.motionX = 0;
				this.motionY = 0;
				this.motionZ = 0;
				if(this.getSpawningState() == this.getSpawningLength() - 1) {
					this.setPosition(this.posX, this.posY + 0.22D, this.posZ);
				}
				int breakPoint = getSpawningLength() / BREAK_COUNT;
				if ((getSpawningState() - breakPoint / 2 - 1) % breakPoint == 0) {
					int x = MathHelper.floor_double(this.posX), y = MathHelper.floor_double(this.posY - this.yOffset), z = MathHelper.floor_double(this.posZ);
					Block block = this.worldObj.getBlock(x, y, z);
					int metadata = this.worldObj.getBlockMetadata(x, y, z);
					String particle = "blockdust_" + Block.getIdFromBlock(block) + "_" + metadata;
					double px = this.posX + this.rand.nextDouble() - 0.5F;
					double py = this.posY - this.yOffset + this.rand.nextDouble() * 0.2 + 0.075;
					double pz = this.posZ + this.rand.nextDouble() - 0.5F;
					this.worldObj.playSound(this.posX, this.posY, this.posZ, block.stepSound.getBreakSound(), this.rand.nextFloat() * 0.3F + 0.3F, this.rand.nextFloat() * 0.15F + 0.7F, false);
					for (int i = 0, amount = this.rand.nextInt(20) + 15; i < amount; i++) {
						double ox = this.rand.nextDouble() * 0.1F - 0.05F;
						double oz = this.rand.nextDouble() * 0.1F - 0.05F;
						double motionX = this.rand.nextDouble() * 0.2 - 0.1;
						double motionY = this.rand.nextDouble() * 0.25 + 0.1;
						double motionZ = this.rand.nextDouble() * 0.2 - 0.1;
						this.worldObj.spawnParticle(particle, px + ox, py, pz + oz, motionX, motionY, motionZ);
					}
				}
			} else {
				this.yOffset = 0;
			}
		} else {
			this.prevYOffset = this.yOffset;

			this.updateTarget();

			if(this.shouldUpdateState()) {
				if(this.getSpawningState() == 0) {
					this.playSound("thebetweenlands:peatMummyEmerge", 1.2F, 1.0F);
				}
				this.updateSpawningState();
			} else if(this.getSpawningProgress() != 0.0F) {
				this.setSpawningFinished();
			}

			if(this.isInValidSpawnPad() && this.getSpawningProgress() != 1.0F) {
				this.yOffset = this.getCurrentOffset();
				this.motionY = 0;
				this.motionX = 0;
				this.motionZ = 0;
				this.velocityChanged = true;
				if(this.getEntityToAttack() != null) this.faceEntity(this.getEntityToAttack(), 360, 360);
				if(this.getSpawningState() == this.getSpawningLength() - 1) {
					this.setPosition(this.posX, this.posY + 0.22D, this.posZ);
				}
			} else {
				this.setSpawningFinished();
				this.yOffset = 0;
			}
		}

		if(this.getSpawningProgress() != 1.0F) return;

		////// AI //////

		if(this.worldObj.isRemote) {
			if(this.isPreparing() && !this.wasScreaming && !this.screaming) {
				this.screaming = true;
				this.wasScreaming = true;
			} else if(!this.isPreparing()) {
				this.wasScreaming = false;
			}
			this.prevScreamTimer = this.screamTimer;
			if(this.screaming) {
				this.screamTimer++;
			} else if (this.screamTimer > 0) {
				this.screamTimer = 0;
			}
			if(this.screamTimer >= SCREAMING_TIMER_MAX) {
				this.screaming = false;
			}
		} else {
			if(!this.isPreparing() && !this.isCharging()) {
				if(this.chargingCooldown > 0 && this.getEntityToAttack() != null) {
					this.chargingCooldown--;
				}
				if(this.chargingCooldown == 0) {
					this.chargingCooldown = this.getChargingCooldown() + this.worldObj.rand.nextInt(this.getChargingCooldown() / 2);
					this.playSound("thebetweenlands:peatMummyCharge", 1.75F, (this.rand.nextFloat() * 0.4F + 0.8F) * 0.8F);
					this.setChargingState(1);
				}
			} else if(this.isPreparing()){
				this.chargingPreparation++;
				if(this.getPreparationProgress() == 1.0F) {
					this.chargingPreparation = 0;
					this.setChargingState(2);
				}
			} else if(this.isCharging()) {
				this.chargingTime++;
				if(this.chargingTime >= this.getEntityAttribute(CHARGING_TIME_ATTRIB).getAttributeValue()) {
					this.chargingTime = 0;
					this.setChargingState(0);
				}
			}

			if(this.isCharging()) {
				this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(BASE_DAMAGE * this.getEntityAttribute(CHARGING_DAMAGE_MULTIPLIER_ATTRIB).getAttributeValue());
				this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(this.getEntityAttribute(CHARGING_SPEED_ATTRIB).getAttributeValue());
			} else {
				this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(BASE_DAMAGE);
				this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(BASE_SPEED);
			}
		}
	}

	public boolean shouldUpdateState() {
		return this.getEntityToAttack() != null;
	}

	public boolean isInValidSpawnPad() {
		double initialPosY = this.posY - this.yOffset;
		int ebx = MathHelper.floor_double(this.posX);
		int eby = MathHelper.floor_double(initialPosY); //Ignore offset, want to check at the initial position
		int ebz = MathHelper.floor_double(this.posZ);
		return inMud(ebx, eby, ebz);
	}

	private boolean inMud(int ebx, int eby, int ebz) {
		for(int y = -MathHelper.ceiling_double_int(this.getSpawnOffset()); y < 0; y++) {
			for(int x = -1; x <= 1; x++) {
				for(int z = -1; z <= 1; z++) {
					Block cb = this.worldObj.getBlock(ebx + x, eby + y, ebz + z);
					if(!(y == -1 ? (SPAWN_BLOCKS.contains(cb)) : (cb.isOpaqueCube() || SPAWN_BLOCKS.contains(cb)))) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public void updateTarget() {
		EntityPlayer player = this.worldObj.getClosestVulnerablePlayerToEntity(this, this.getSpawningProgress() == 1.0F ? this.getEntityAttribute(SharedMonsterAttributes.followRange).getAttributeValue() : this.getSpawningRange());
		if(player != null && this.getEntityToAttack() == null) {
			double prevPosY = this.posY;
			this.posY = this.posY - this.yOffset;
			if(player.canEntityBeSeen(this)) {
				this.setTarget(player);
			}
			this.posY = prevPosY;
		} else if(this.getEntityToAttack() != null && player == null) {
			this.setTarget(null);
		}
	}

	@Override
	public void onCollideWithPlayer(EntityPlayer player) {
		if(this.getSpawningProgress() == 1.0F) {
			super.onCollideWithPlayer(player);
		}
	}

	@Override
	public boolean canBePushed() {
		return super.canBePushed() && this.getSpawningProgress() == 1.0F && this.getChargingState() == 0;
	}

	@Override
	protected boolean isMovementBlocked() {
		return super.isMovementBlocked() || this.getSpawningProgress() != 1.0F || this.getChargingState() == 1;
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 2;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if(source.equals(DamageSource.inWall)) return false;
		return super.attackEntityFrom(source, damage);
	}

	@Override
	public boolean getCanSpawnHere() {
		return super.getCanSpawnHere() && this.isInValidSpawnPad();
	}

	@Override
	protected boolean isAIEnabled() {
		return true;
	}

	@Override
	public boolean attackEntityAsMob(Entity entity) {
		if(this.isCharging()) {
			this.stopCharging();
		}
		return super.attackEntityAsMob(entity);
	}

	@Override
	public void playLivingSound() {
		if(this.getSpawningProgress() == 1.0F) {
			super.playLivingSound();
		}
	}

	public void stopCharging() {
		this.setChargingState(0);
		this.chargingCooldown = this.getChargingCooldown() + this.worldObj.rand.nextInt(this.getChargingCooldown() / 2);
		this.chargingPreparation = 0;
		this.chargingTime = 0;
	}

	public float getCurrentOffset() {
		return (float) ((-this.getSpawnOffset() + this.getSpawningProgress() * this.getSpawnOffset()));
	}

	public double getSpawnOffset() {
		return this.getEntityAttribute(SPAWN_OFFSET_ATTRIB).getAttributeValue();
	}

	public int getSpawningState() {
		return this.dataWatcher.getWatchableObjectInt(SPAWNING_STATE_DW);
	}

	public int getSpawningLength() {
		return (int) this.getEntityAttribute(SPAWN_LENGTH_ATTRIB).getAttributeValue();
	}

	public double getSpawningRange() {
		return this.getEntityAttribute(SPAWN_RANGE_ATTRIB).getAttributeValue();
	}

	public float getSpawningProgress() {
		if(this.getSpawningLength() == 0) {
			return 1.0F;
		}
		return 1.0F / this.getSpawningLength() * this.getSpawningState();
	}

	public float getSpawningProgress(float delta) {
		if(this.getSpawningLength() == 0) {
			return 1.0F;
		}
		return 1.0F / this.getSpawningLength() * (this.getSpawningState() + (this.getSpawningState() == this.getSpawningLength() ? 0 : delta));
	}

	public void updateSpawningState() {
		int spawningState = this.getSpawningState();
		if(spawningState < this.getSpawningLength()) {
			this.dataWatcher.updateObject(SPAWNING_STATE_DW, spawningState + 1);
		}
	}

	public void resetSpawningState() {
		this.dataWatcher.updateObject(SPAWNING_STATE_DW, 0);
	}

	public void setSpawningFinished() {
		if(this.getSpawningProgress() == 1.0F) return;
		this.dataWatcher.updateObject(SPAWNING_STATE_DW, this.getSpawningLength());
	}

	public int getChargingCooldown() {
		return (int) this.getEntityAttribute(CHARGING_COOLDOWN_ATTRIB).getAttributeValue();
	}

	public void setChargingState(int state) {
		this.dataWatcher.updateObject(CHARGING_STATE_DW, (byte) state);
	}

	public byte getChargingState() {
		return this.dataWatcher.getWatchableObjectByte(CHARGING_STATE_DW);
	}

	public boolean isPreparing() {
		return this.getChargingState() == 1;
	}

	public boolean isCharging() {
		return this.getChargingState() == 2;
	}

	public float getPreparationProgress() {
		return 1.0F / (int)this.getEntityAttribute(CHARGING_PREPARATION_SPEED_ATTRIB).getAttributeValue() * this.chargingPreparation;
	}

	@SideOnly(Side.CLIENT)
	public boolean isScreaming() {
		return this.screaming;
	}

	@SideOnly(Side.CLIENT)
	public float getScreamingProgress(float delta) {
		return 1.0F / SCREAMING_TIMER_MAX * (this.prevScreamTimer + (this.screamTimer - this.prevScreamTimer) * delta);
	}

	@Override
	protected String getLivingSound() {
		return "thebetweenlands:peatMummyLiving";
	}

	@Override
	protected String getHurtSound() {
		return "thebetweenlands:peatMummyHurt";
	}

	@Override
	protected String getDeathSound() {
		return "thebetweenlands:peatMummyDeath";
	}

	@Override
	protected void dropFewItems(boolean killedByPlayer, int looting) {
		if(this.carryShimmerstone) {
			if(this.worldObj.rand.nextInt(8) != 0) {
				this.dropItem(BLItemRegistry.shimmerStone, 1);
			}
		} else {
			if(this.worldObj.rand.nextInt(3) == 0) {
				this.dropItem(BLItemRegistry.shimmerStone, 1);
				if(this.worldObj.rand.nextInt(5) == 0) {
					this.dropItem(BLItemRegistry.shimmerStone, this.worldObj.rand.nextInt(5) + 1);
				}
			}
		}
	}

	@Override
	public String pageName() {
		return "peatMummy";
	}

}


