package thebetweenlands.entities.mobs;

import java.util.List;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import thebetweenlands.entities.properties.list.equipment.EnumEquipmentCategory;
import thebetweenlands.entities.properties.list.equipment.Equipment;
import thebetweenlands.entities.properties.list.equipment.EquipmentInventory;
import thebetweenlands.items.BLItemRegistry;

public class EntityMummyArm extends EntityCreature implements IEntityBL {
	public static final int OWNER_DW = 18;

	public int attackSwing = 0;

	private int spawnTicks = 0;

	private int despawnTicks = 0;

	private int deathTicks = 0;

	public EntityMummyArm(World world) {
		super(world);
		this.setSize(0.7F, 0.7F);
	}

	@Override
	public String pageName() {
		return "mummyArm";
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(OWNER_DW, "");
	}

	@Override
	public IEntityLivingData onSpawnWithEgg(IEntityLivingData data) {
		this.rotationYaw = this.worldObj.rand.nextFloat() * 360.0F;
		return data;
	}

	public void setOwner(String ownerUUID) {
		this.dataWatcher.updateObject(OWNER_DW, ownerUUID);
	}

	public String getOwnerUUID() {
		return this.dataWatcher.getWatchableObjectString(OWNER_DW);
	}

	public Entity getOwner() {
		try {
			UUID uuid = UUID.fromString(this.getOwnerUUID());
			return uuid == null ? null : this.getEntityByUUID(uuid);
		} catch (IllegalArgumentException illegalargumentexception) {
			return null;
		}
	}

	private Entity getEntityByUUID(UUID uuid) {
		for (int i = 0; i < this.worldObj.loadedEntityList.size(); ++i) {
			Entity entity = (Entity)this.worldObj.loadedEntityList.get(i);
			if (uuid.equals(entity.getUniqueID())) {
				return entity;
			}
		}
		return null;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if(!this.worldObj.isRemote) {
			int bx = MathHelper.floor_double(this.posX);
			int by = MathHelper.floor_double(this.posY - 0.5D);
			int bz = MathHelper.floor_double(this.posZ);
			Block blockBelow = this.worldObj.getBlock(bx, by, bz);
			if(blockBelow == Blocks.air || !this.worldObj.getBlock(bx, by, bz).isSideSolid(this.worldObj, bx, by, bz, ForgeDirection.UP)) {
				this.setDead();
			}

			Entity owner = this.getOwner();
			if(owner == null || owner.getDistanceToEntity(this) > 32.0D) {
				this.setHealth(0);
			} else if(owner instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) owner;
				if(!this.isUsingRing(player))
					this.setHealth(0);
			} else {
				this.setHealth(0);
			}

			if(this.despawnTicks >= 150) {
				this.setHealth(0);
			} else {
				if(this.spawnTicks >= 40)
					this.despawnTicks++;
			}
		}

		if(this.deathTicks > 0) {
			this.yOffset = -this.deathTicks / 40.0F;
		} else if(this.spawnTicks >= 40) {
			this.yOffset = 0.0F;
		}

		if(this.isEntityAlive()) {
			if(this.spawnTicks >= 4) {
				List<EntityLivingBase> targets = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, this.boundingBox);
				for(EntityLivingBase target : targets) {
					if(target != this && target != this.getOwner() && target instanceof EntityMob) {
						DamageSource damageSource;
						Entity owner = this.getOwner();
						if(owner != null) {
							damageSource = new EntityDamageSourceIndirect("mob", this, owner);
						} else {
							damageSource = DamageSource.causeMobDamage(this);
						}
						target.attackEntityFrom(damageSource, 3.0F);
						if(this.attackSwing <= 0)
							this.attackSwing = 20;
					}
				}
			}

			if(this.spawnTicks < 40) {
				this.spawnTicks++;
				this.yOffset = -1 + this.spawnTicks / 40.0F;
			} else {
				this.yOffset = 0.0F;
			}

			if(this.attackSwing > 0)
				this.attackSwing--;
		}

		if(this.worldObj.isRemote && this.rand.nextInt(this.yOffset < 0.0F ? 2 : 8) == 0) {
			int x = MathHelper.floor_double(this.posX);
			int y = MathHelper.floor_double(this.posY - 0.5D);
			int z = MathHelper.floor_double(this.posZ);
			Block block = this.worldObj.getBlock(x, y, z);
			if(block != Blocks.air) {
				int metadata = this.worldObj.getBlockMetadata(x, y, z);
				String particle = "blockdust_" + Block.getIdFromBlock(block) + "_" + metadata;
				double px = this.posX;
				double py = this.posY;
				double pz = this.posZ;
				for (int i = 0, amount = 2 + this.rand.nextInt(this.yOffset < 0.0F ? 8 : 3); i < amount; i++) {
					double ox = this.rand.nextDouble() * 0.1F - 0.05F;
					double oz = this.rand.nextDouble() * 0.1F - 0.05F;
					double motionX = this.rand.nextDouble() * 0.2 - 0.1;
					double motionY = this.rand.nextDouble() * 0.1 + 0.1;
					double motionZ = this.rand.nextDouble() * 0.2 - 0.1;
					this.worldObj.spawnParticle(particle, px + ox, py, pz + oz, motionX, motionY, motionZ);
				}
			}
		}
	}

	private boolean isUsingRing(EntityPlayer player) {
		EquipmentInventory inventory = EquipmentInventory.getEquipmentInventory(player);
		List<Equipment> rings = inventory.getEquipment(EnumEquipmentCategory.RING);
		for(Equipment equipment : rings) {
			if(equipment.item.getItem() == BLItemRegistry.ringOfSummoning) {
				if(equipment.item.getItemDamage() < equipment.item.getMaxDamage())
					return true;
			}
		}
		return false;
	}

	@Override
	public void moveEntityWithHeading(float strafe, float forward) { }

	@Override
	public void applyEntityCollision(Entity entity) { }

	@Override
	protected void collideWithEntity(Entity entity) { }

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	protected void onDeathUpdate() {
		this.deathTicks++;

		if(!this.worldObj.isRemote) {
			if(this.deathTicks >= 40)
				this.setDead();
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setString("ownerUUID", this.getOwnerUUID());
		nbt.setInteger("spawnTicks", this.spawnTicks);
		nbt.setInteger("despawnTicks", this.despawnTicks);
		nbt.setInteger("deathTicks", this.deathTicks);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		this.setOwner(nbt.getString("ownerUUID"));
		this.spawnTicks = nbt.getInteger("spawnTicks");
		this.despawnTicks = nbt.getInteger("despawnTicks");
		this.deathTicks = nbt.getInteger("deathTicks");
	}
}
