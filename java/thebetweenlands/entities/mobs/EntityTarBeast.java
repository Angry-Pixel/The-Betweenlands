package thebetweenlands.entities.mobs;

import java.util.List;
import java.util.Random;

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
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.client.particle.BLParticle;
import thebetweenlands.items.misc.ItemGeneric;
import thebetweenlands.items.misc.ItemGeneric.EnumItemGeneric;
import thebetweenlands.items.tools.ItemAxeBL;
import thebetweenlands.items.tools.ItemPickaxeBL;
import thebetweenlands.items.tools.ItemSpadeBL;
import thebetweenlands.items.tools.ItemSwordBL;
import thebetweenlands.recipes.BLMaterials;
import thebetweenlands.utils.Mesh.Triangle.Vertex.Vector3D;

public class EntityTarBeast extends EntityMob implements IEntityBL {

	public static final IAttribute SHED_COOLDOWN_ATTRIB = (new RangedAttribute("bl.shedCooldown", 70.0D, 10.0D, Integer.MAX_VALUE)).setDescription("Shed Cooldown");
	public static final IAttribute SHED_SPEED_ATTRIB = (new RangedAttribute("bl.shedSpeed", 10.0D, 0.0D, Integer.MAX_VALUE)).setDescription("Shedding Speed");

	public static final IAttribute SUCK_COOLDOWN_ATTRIB = (new RangedAttribute("bl.suckCooldown", 400.0D, 0.0D, Integer.MAX_VALUE)).setDescription("Sucking Cooldown");
	public static final IAttribute SUCK_PREPARATION_SPEED_ATTRIB = (new RangedAttribute("bl.suckPreparationSpeed", 40.0D, 0.0D, Integer.MAX_VALUE)).setDescription("Sucking Preparation Speed");
	public static final IAttribute SUCK_LENGTH_ATTRIB = (new RangedAttribute("bl.suckLength", 130.0D, 0.0D, Integer.MAX_VALUE)).setDescription("Sucking Length");

	private int shedCooldown = (int) SHED_COOLDOWN_ATTRIB.getDefaultValue();
	private int sheddingProgress = 0;
	public static final int SHEDDING_STATE_DW = 20;

	private int suckingCooldown = (int) SUCK_COOLDOWN_ATTRIB.getDefaultValue();
	private int suckingPreparation = 0;
	private int suckingProgress = 0;
	public static final int SUCKING_STATE_DW = 21;

	public EntityTarBeast(World world) {
		super(world);
		setSize(1.25F, 2F);
		
		this.tasks.addTask(1, new EntityAIAttackOnCollide(this, EntityLivingBase.class, 1D, false));
		this.tasks.addTask(2, new EntityAIWander(this, 1D));
		this.tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(4, new EntityAILookIdle(this));
		this.targetTasks.addTask(0, new EntityAIHurtByTarget(this, true));
		this.targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(SHEDDING_STATE_DW, Byte.valueOf((byte) 0));
		this.dataWatcher.addObject(SUCKING_STATE_DW, Byte.valueOf((byte) 0));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.65D);
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(100.0D);
		getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(6.0D);
		getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(16.0D);
		getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(1.0D);

		this.getAttributeMap().registerAttribute(SHED_COOLDOWN_ATTRIB);
		this.getAttributeMap().registerAttribute(SHED_SPEED_ATTRIB);
		this.getAttributeMap().registerAttribute(SUCK_COOLDOWN_ATTRIB);
		this.getAttributeMap().registerAttribute(SUCK_PREPARATION_SPEED_ATTRIB);
		this.getAttributeMap().registerAttribute(SUCK_LENGTH_ATTRIB);
	}

	@Override
	public boolean getCanSpawnHere() {
		boolean isDifficultyValid = this.worldObj.difficultySetting != EnumDifficulty.PEACEFUL;
		//Couldn't find any better way to make them spawn on liquid tar
		this.setLocationAndAngles(this.posX + 8 - this.rand.nextInt(16), this.posY + 8 - this.rand.nextInt(16), this.posZ + 8 - this.rand.nextInt(16), this.rotationYaw, this.rotationPitch);
		if(isDifficultyValid) {
			int bx = MathHelper.floor_double(posX);
			int by = MathHelper.floor_double(posY);
			int bz = MathHelper.floor_double(posZ);
			boolean isInTar = 
					worldObj.getBlock((int)bx, (int)by, (int)bz) == BLBlockRegistry.tarFluid &&
					worldObj.getBlock((int)bx - 1, (int)by, (int)bz) == BLBlockRegistry.tarFluid &&
					worldObj.getBlock((int)bx + 1, (int)by, (int)bz) == BLBlockRegistry.tarFluid &&
					worldObj.getBlock((int)bx, (int)by, (int)bz - 1) == BLBlockRegistry.tarFluid &&
					worldObj.getBlock((int)bx, (int)by, (int)bz + 1) == BLBlockRegistry.tarFluid;
			boolean canSpawn = worldObj.checkNoEntityCollision(boundingBox) && worldObj.getCollidingBoundingBoxes(this, boundingBox).isEmpty() && isInTar;
			return canSpawn;
		}
		return false;
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 3;
	}

	@Override
	protected String getLivingSound() {
		int randomSound = rand.nextInt(3) + 1;
		return "thebetweenlands:tarBeastLiving" + randomSound;
	}

	@Override
	protected String getHurtSound() {
		if (rand.nextBoolean())
			return "thebetweenlands:tarBeastHurt1";
		else
			return "thebetweenlands:tarBeastHurt2";
	}

	@Override
	protected String getDeathSound() {
		return "thebetweenlands:tarBeastDeath";
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		nbt.setInteger("shedCooldown", this.shedCooldown);
		nbt.setInteger("sheddingProgress", this.sheddingProgress);
		nbt.setByte("sheddingState", this.getDataWatcher().getWatchableObjectByte(SHEDDING_STATE_DW));

		nbt.setInteger("suckingCooldown", this.suckingCooldown);
		nbt.setInteger("suckingPreparation", this.suckingPreparation);
		nbt.setInteger("suckingProgress", this.suckingProgress);
		nbt.setByte("suckingState", this.getDataWatcher().getWatchableObjectByte(SUCKING_STATE_DW));

		super.writeEntityToNBT(nbt);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		this.shedCooldown = nbt.getInteger("shedCooldown");
		this.sheddingProgress = nbt.getInteger("sheddingProgress");
		this.getDataWatcher().updateObject(SHEDDING_STATE_DW, nbt.getByte("sheddingState"));

		this.suckingCooldown = nbt.getInteger("suckingCooldown");
		this.suckingPreparation = nbt.getInteger("suckingPreparation");
		this.suckingProgress = nbt.getInteger("suckingProgress");
		this.getDataWatcher().updateObject(SUCKING_STATE_DW, nbt.getByte("suckingState"));

		super.readEntityFromNBT(nbt);
	}

	@Override
	protected void func_145780_a(int x, int y, int z, Block block) { // playStepSound
		int randomSound = rand.nextInt(3) + 1;
		playSound("thebetweenlands:tarBeastStep" + randomSound, 1F, 1F);
	}

	@Override
	protected void dropFewItems(boolean recentlyHit, int looting) {
		entityDropItem(ItemGeneric.createStack(EnumItemGeneric.TAR_DRIP), 0F);
	}

	@Override
	protected void dropRareDrop(int looting) {
		entityDropItem(ItemGeneric.createStack(EnumItemGeneric.TAR_BEAST_HEART), 0F);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if (source.getSourceOfDamage() instanceof EntityPlayer) {
			EntityPlayer entityPlayer = (EntityPlayer) source.getSourceOfDamage();
			ItemStack heldItem = entityPlayer.getCurrentEquippedItem();
			if (heldItem != null)
				if (heldItem.getItem() instanceof ItemSwordBL || heldItem.getItem() instanceof ItemAxeBL || heldItem.getItem() instanceof ItemPickaxeBL || heldItem.getItem() instanceof ItemSpadeBL) {
					return super.attackEntityFrom(source, damage);
				} else {
					return super.attackEntityFrom(source, MathHelper.ceiling_float_int(damage * 0.5F));
				}
		}
		return super.attackEntityFrom(source, damage);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (worldObj.isRemote) {
			if(ticksExisted % 10 == 0) {
				renderParticles(worldObj, posX, posY, posZ, rand);
			}
			if(this.sheddingProgress > this.getSheddingSpeed()) {
				this.sheddingProgress = 0;

				for(int i = 0; i < 200; i++) {
					Random rnd = worldObj.rand;
					float rx = rnd.nextFloat() * 4.0F - 2.0F;
					float ry = rnd.nextFloat() * 4.0F - 2.0F;
					float rz = rnd.nextFloat() * 4.0F - 2.0F;
					Vector3D vec = new Vector3D(rx, ry, rz);
					vec = vec.normalized();
					BLParticle.SPLASH_TAR_BEAST.spawn(this.worldObj, this.posX + rx + 0.25F, this.posY + ry, this.posZ + rz + 0.25F, vec.x * 0.5F, vec.y * 0.5F, vec.z * 0.5F, 1);
				}
			} else if(this.isShedding() || this.sheddingProgress > 0) {
				this.sheddingProgress++;
			} else {
				this.sheddingProgress = 0;
			}

			if(this.isSucking()) {
				for(int i = 0; i < 5; i++) {
					Random rnd = worldObj.rand;
					float rx = rnd.nextFloat() * 8.0F - 4.0F;
					float ry = rnd.nextFloat() * 8.0F - 4.0F;
					float rz = rnd.nextFloat() * 8.0F - 4.0F;
					Vector3D vec = new Vector3D(rx, ry, rz);
					vec = vec.normalized();
					this.worldObj.spawnParticle("largesmoke", this.posX + rx + 0.25F, this.posY + ry, this.posZ + rz + 0.25F, -vec.x * 0.5F, -vec.y * 0.5F, -vec.z * 0.5F);
				}
			}
		}

		if(!worldObj.isRemote) {
			if(this.isInsideOfMaterial(BLMaterials.tar)) {
				this.stepHeight = 2.0F;
			} else {
				this.stepHeight = 0.75F;
			}
			
			if(this.shedCooldown > 0 && this.getEntityToAttack() != null) {
				this.shedCooldown--;
			}

			if(!this.isSucking() && !this.isPreparing()) {
				if(this.shedCooldown == 0 && this.getEntityToAttack() != null && this.getEntityToAttack().getDistanceToEntity(this) < 6.0D && this.canEntityBeSeen(this.getEntityToAttack())) {
					this.setShedding(true);
					this.shedCooldown = this.getSheddingCooldown() + this.worldObj.rand.nextInt(this.getSheddingCooldown() / 2);
				}

				if(this.sheddingProgress > this.getSheddingSpeed()) {
					this.playSound("thebetweenlands:tarBeastLiving" + (rand.nextInt(3) + 1), 1F, (this.rand.nextFloat() * 0.2F + 1.0F) * 0.6F);
					for(int i = 0; i < 8; i++) {
						this.playSound("thebetweenlands:tarBeastStep" + (rand.nextInt(3) + 1), 1F, (this.rand.nextFloat() * 0.4F + 0.8F) * 0.8F);
					}
					this.sheddingProgress = 0;
					this.setShedding(false);
					if(this.getEntityToAttack() != null) {
						List<EntityLivingBase> affectedEntities = (List<EntityLivingBase>)this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, this.boundingBox.expand(6.0F, 6.0F, 6.0F));
						for(EntityLivingBase e : affectedEntities) {
							if(e == this || e.getDistanceToEntity(this) > 6.0F || !e.canEntityBeSeen(this) || e instanceof EntityTarBeast) continue;
							if(e instanceof EntityPlayer) {
								if(((EntityPlayer)e).isBlocking()) continue;
							}
							double dst = e.getDistanceToEntity(this);
							float dmg = (float) (this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue() / dst * 7.0F);
							e.attackEntityFrom(DamageSource.causeMobDamage(this), dmg);
							e.addPotionEffect(new PotionEffect(Potion.moveSlowdown.getId(), (int)(20 + (1.0F - dst / 6.0F) * 150), 1, true));
						}
					}
				}

				if(this.isShedding()) {
					this.sheddingProgress++;
				} else {
					this.sheddingProgress = 0;
				}
			}

			if(this.suckingCooldown > 0 && this.getEntityToAttack() != null) {
				this.suckingCooldown--;
			}

			if(!this.isShedding()) {
				if(this.suckingCooldown == 0 && this.getEntityToAttack() != null && this.getEntityToAttack().getDistanceToEntity(this) <= 10.0D && this.canEntityBeSeen(this.getEntityToAttack())) {
					this.setPreparing();
				}

				if(this.isPreparing()) {
					this.suckingPreparation++;

					if(this.suckingPreparation > this.getEntityAttribute(SUCK_PREPARATION_SPEED_ATTRIB).getAttributeValue()) {
						this.suckingPreparation = 0;

						this.setSucking(true);
						this.suckingCooldown = this.getSuckingCooldown() + this.worldObj.rand.nextInt(this.getSuckingCooldown() / 2);
						this.playSound("thebetweenlands:tarBeastSuck", 1F, 1F);
					}
				}

				if(this.suckingProgress > (int)this.getEntityAttribute(SUCK_LENGTH_ATTRIB).getAttributeValue()) {
					this.setSucking(false);
					this.suckingProgress = 0;
				}

				if(this.isSucking()) {
					this.suckingProgress++;

					List<Entity> affectedEntities = (List<Entity>)this.worldObj.getEntitiesWithinAABB(Entity.class, this.boundingBox.expand(10.0F, 10.0F, 10.0F));
					for(Entity e : affectedEntities) {
						if(e == this || e.getDistanceToEntity(this) > 10.0F || !this.canEntityBeSeen(e) || e instanceof EntityTarBeast) continue;
						Vector3D vec = new Vector3D(this.posX - e.posX, this.posY - e.posY, this.posZ - e.posZ);
						vec = vec.normalized();
						float dst = e.getDistanceToEntity(this);
						float mod = (float) Math.pow(1.0F - dst / 13.0F, 1.2D);
						if(e instanceof EntityPlayer) {
							if(((EntityPlayer)e).isBlocking()) mod *= 0.18F;
						}
						if(dst < 1.0F && e instanceof EntityLivingBase) {
							((EntityLivingBase) e).addPotionEffect(new PotionEffect(Potion.weakness.getId(), 20, 3, true));
							((EntityLivingBase) e).addPotionEffect(new PotionEffect(Potion.moveSlowdown.getId(), 20, 3, true));
							e.motionX *= 0.008F;
							e.motionY *= 0.008F;
							e.motionZ *= 0.008F;
							if(e instanceof EntityPlayer) {
								((EntityPlayer)e).jumpMovementFactor = 0.0F;
							}
							if(this.ticksExisted % 12 == 0) {
								e.attackEntityFrom(DamageSource.drown, 1);
							}
						}
						e.motionX += vec.x * 0.18F * mod;
						e.motionY += vec.y * 0.18F * mod;
						e.motionZ += vec.z * 0.18F * mod;
						e.velocityChanged = true;
					}
					getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(0.75D);
				} else {
					this.suckingProgress = 0;
					getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(1.0D);
				}
			}
		}
	}

	@Override
	protected boolean isMovementBlocked() {
		return super.isMovementBlocked() || this.isSucking();
	}

	@SideOnly(Side.CLIENT)
	public void renderParticles(World world, double x, double y, double z, Random rand) {
		for (int count = 0; count < 3; ++count) {
			double velX = 0.0D;
			double velY = 0.0D;
			double velZ = 0.0D;
			int motionX = rand.nextInt(2) * 2 - 1;
			int motionZ = rand.nextInt(2) * 2 - 1;
			double a = Math.toRadians(renderYawOffset);
			double offSetX = -Math.sin(a) * 0.5D + rand.nextDouble() * 0.3D - rand.nextDouble() * 0.3D;
			double offSetZ = Math.cos(a) * 0.5D + rand.nextDouble() * 0.3D - rand.nextDouble() * 0.3D;
			velY = (rand.nextFloat() - 0.5D) * 0.125D;
			velZ = rand.nextFloat() * 0.5F * motionZ;
			velX = rand.nextFloat() * 0.5F * motionX;
			BLParticle.SPLASH_TAR_BEAST.spawn(world , x, y + rand.nextDouble() * 1.9D, z, velX * 0.15D, velY * 0.1D, velZ * 0.15D, 0);
			BLParticle.DRIP_TAR_BEAST.spawn(world , x + offSetX, y + 1.2D, z + offSetZ);
		}
	}

	@Override
	protected void collideWithEntity(Entity e) {
		if(!this.isSucking()) {
			e.applyEntityCollision(this);
		}
	}

	@Override
	public boolean canBePushed() {
		return super.canBePushed() && !this.isSucking();
	}

	public boolean isShedding() {
		return this.getDataWatcher().getWatchableObjectByte(SHEDDING_STATE_DW) == 1;
	}

	public void setShedding(boolean shedding) {
		this.getDataWatcher().updateObject(SHEDDING_STATE_DW, (byte)(shedding ? 1 : 0));
	}

	public int getSheddingProgress() {
		return this.sheddingProgress;
	}

	public int getSheddingCooldown() {
		return (int)this.getEntityAttribute(SHED_COOLDOWN_ATTRIB).getAttributeValue();
	}

	public int getSheddingSpeed() {
		return (int)this.getEntityAttribute(SHED_SPEED_ATTRIB).getAttributeValue();
	}

	public int getSuckingCooldown() {
		return (int)this.getEntityAttribute(SUCK_COOLDOWN_ATTRIB).getAttributeValue();
	}

	public boolean isSucking() {
		return this.getDataWatcher().getWatchableObjectByte(SUCKING_STATE_DW) == 1;
	}

	public boolean isPreparing() {
		return this.getDataWatcher().getWatchableObjectByte(SUCKING_STATE_DW) == 2;
	}

	public void setSucking(boolean sucking) {
		this.getDataWatcher().updateObject(SUCKING_STATE_DW, (byte)(sucking ? 1 : 0));
	}

	public void setPreparing() {
		this.getDataWatcher().updateObject(SUCKING_STATE_DW, (byte)2);
	}
}
