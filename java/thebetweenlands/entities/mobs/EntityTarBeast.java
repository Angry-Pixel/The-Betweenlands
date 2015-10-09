package thebetweenlands.entities.mobs;

import java.util.List;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import paulscode.sound.Vector3D;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.client.particle.BLParticle;
import thebetweenlands.items.AxeBL;
import thebetweenlands.items.ItemMaterialsBL;
import thebetweenlands.items.ItemMaterialsBL.EnumMaterialsBL;
import thebetweenlands.items.PickaxeBL;
import thebetweenlands.items.SpadeBL;
import thebetweenlands.items.SwordBL;

public class EntityTarBeast extends EntityMob implements IEntityBL {

	public static final IAttribute SHED_COOLDOWN_ATTRIB = (new RangedAttribute("bl.shedCooldown", 70.0D, 10.0D, Double.MAX_VALUE)).setDescription("Shed Cooldown");
	public static final IAttribute SHED_SPEED_ATTRIB = (new RangedAttribute("bl.shedSpeed", 10.0D, 0.0D, Double.MAX_VALUE)).setDescription("Shedding Speed");
	
	private int shedCooldown = 0;
	private int sheddingProgress = 0;
	public final static int SHEDDING_DW = 20;

	public EntityTarBeast(World world) {
		super(world);
		setSize(1.25F, 2F);
		stepHeight = 2.0F;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(SHEDDING_DW, Byte.valueOf((byte) 0));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.6D);
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(60.0D);
		getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(8.0D);
		getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(16.0D);
		getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(1.0D);
		
		this.getAttributeMap().registerAttribute(SHED_COOLDOWN_ATTRIB);
		this.getAttributeMap().registerAttribute(SHED_SPEED_ATTRIB);
	}

	@Override
	public boolean getCanSpawnHere() {
		return worldObj.checkNoEntityCollision(boundingBox) && worldObj.getCollidingBoundingBoxes(this, boundingBox).isEmpty() && worldObj.getBlock((int)posX, (int)posY, (int)posZ) == BLBlockRegistry.tarFluid;
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
		nbt.setByte("sheddingState", this.getDataWatcher().getWatchableObjectByte(SHEDDING_DW));
		super.writeEntityToNBT(nbt);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		this.shedCooldown = nbt.getInteger("shedCooldown");
		this.sheddingProgress = nbt.getInteger("sheddingProgress");
		this.getDataWatcher().updateObject(SHEDDING_DW, nbt.getByte("sheddingState"));
		super.readEntityFromNBT(nbt);
	}

	@Override
	protected void func_145780_a(int x, int y, int z, Block block) { // playStepSound
		int randomSound = rand.nextInt(3) + 1;
		playSound("thebetweenlands:tarBeastStep" + randomSound, 1F, 1F);
	}

	@Override
	protected void dropFewItems(boolean recentlyHit, int looting) {
		entityDropItem(ItemMaterialsBL.createStack(EnumMaterialsBL.TAR_DRIP), 0F);
	}

	@Override
	protected void dropRareDrop(int looting) {
		entityDropItem(ItemMaterialsBL.createStack(EnumMaterialsBL.TAR_BEAST_HEART), 0F);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if (source.getSourceOfDamage() instanceof EntityPlayer) {
			EntityPlayer entityPlayer = (EntityPlayer) source.getSourceOfDamage();
			ItemStack heldItem = entityPlayer.getCurrentEquippedItem();
			if (heldItem != null)
				if (heldItem.getItem() instanceof SwordBL || heldItem.getItem() instanceof AxeBL || heldItem.getItem() instanceof PickaxeBL || heldItem.getItem() instanceof SpadeBL) {
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

				for(int i = 0; i < 5; i++) {
					this.worldObj.playSoundAtEntity(this, "thebetweenlands:tarBeastStep", 1F, (this.rand.nextFloat() * 0.2F + 1.0F) * 0.8F);
				}

				for(int i = 0; i < 200; i++) {
					Random rnd = worldObj.rand;
					float rx = rnd.nextFloat() * 4.0F - 2.0F;
					float ry = rnd.nextFloat() * 4.0F - 2.0F;
					float rz = rnd.nextFloat() * 4.0F - 2.0F;
					Vector3D vec = new Vector3D(rx, ry, rz);
					vec.normalize();
					BLParticle.SPLASH_TAR_BEAST.spawn(this.worldObj, this.posX + rx + 0.5F, this.posY + ry, this.posZ + rz + 0.5F, vec.x * 0.5F, vec.y * 0.5F, vec.z * 0.5F, 1);
				}
			} else if(this.isShedding() || this.sheddingProgress > 0) {
				this.sheddingProgress++;
			} else {
				this.sheddingProgress = 0;
			}
		}

		if(!worldObj.isRemote) {
			if(this.shedCooldown > 0) {
				this.shedCooldown--;
			}

			if(this.shedCooldown == 0 && this.getEntityToAttack() != null && this.getEntityToAttack().getDistanceToEntity(this) < 6.0D && this.canEntityBeSeen(this.getEntityToAttack())) {
				this.setShedding(true);
				this.shedCooldown = this.getSheddingCooldown() + this.worldObj.rand.nextInt(this.getSheddingCooldown() / 2);
			}

			if(this.sheddingProgress > this.getSheddingSpeed()) {
				this.sheddingProgress = 0;
				this.setShedding(false);
				if(this.getEntityToAttack() != null) {
					List<EntityLivingBase> affectedEntities = (List<EntityLivingBase>)this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, this.boundingBox.expand(6.0F, 6.0F, 6.0F));
					for(EntityLivingBase e : affectedEntities) {
						if(e == this || e.getDistanceToEntity(this) > 6.0F || !e.canEntityBeSeen(this)) continue;
						if(e instanceof EntityPlayer) {
							if(((EntityPlayer)e).isBlocking()) continue;
						}
						double dst = e.getDistanceToEntity(this);
						float dmg = (float) (this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue() / dst * 7.0F);
						e.attackEntityFrom(DamageSource.causeMobDamage(this), dmg);
					}
				}
			}

			if(this.isShedding()) {
				this.sheddingProgress++;
			} else {
				this.sheddingProgress = 0;
			}
		}
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

	public boolean isShedding() {
		return this.getDataWatcher().getWatchableObjectByte(SHEDDING_DW) == 1;
	}

	public void setShedding(boolean shedding) {
		this.getDataWatcher().updateObject(SHEDDING_DW, (byte)(shedding ? 1 : 0));
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
}
