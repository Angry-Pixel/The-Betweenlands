package thebetweenlands.entities;

import java.util.List;
import java.util.UUID;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import thebetweenlands.items.BLItemRegistry;

public class EntityRopeNode extends Entity {
	public static final double ROPE_LENGTH = 4.0D;
	public static final double ROPE_LENGTH_MAX = 12.0D;

	private boolean canExtend = true;
	private boolean pickUp = false;
	private String prevNodeUUID = "";
	private String nextNodeUUID = "";
	private int despawnTimer = 0;

	public EntityRopeNode(World world) {
		super(world);
		this.setSize(0.1F, 0.1F);
		this.renderDistanceWeight = 20;
		this.ignoreFrustumCheck = true;
	}

	@Override
	protected void entityInit() {
		this.dataWatcher.addObject(19, -1);
		this.dataWatcher.addObject(20, -1);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		this.setNextNodeUUID(nbt.hasKey("nextNodeUUID") ? nbt.getString("nextNodeUUID") : null);
		this.setPreviousNodeUUID(nbt.hasKey("previousNodeUUID") ? nbt.getString("previousNodeUUID") : null);
		this.pickUp = nbt.getBoolean("pickUp");
		this.canExtend = nbt.getBoolean("canExtend");
		this.despawnTimer = nbt.getInteger("despawnTimer");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		nbt.setString("nextNodeUUID", this.getNextNodeUUID());
		nbt.setString("previousNodeUUID", this.getPreviousNodeUUID());
		nbt.setBoolean("pickUp", this.pickUp);
		nbt.setBoolean("canExtend", this.canExtend);
		nbt.setInteger("despawnTimer", this.despawnTimer);
	}

	@Override
	public void onEntityUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		if(!this.worldObj.isRemote) {
			this.handleWaterMovement();

			this.moveEntity(this.motionX, this.motionY, this.motionZ);

			if(this.getNextNodeByUUID() != null) {
				this.dataWatcher.updateObject(19, this.getNextNodeByUUID().getEntityId());
			} else {
				this.dataWatcher.updateObject(19, -1);
			}
			if(this.getPreviousNodeByUUID() != null) {
				this.dataWatcher.updateObject(20, this.getPreviousNodeByUUID().getEntityId());
			} else {
				this.dataWatcher.updateObject(20, -1);
			}

			Entity nextNode = this.getNextNode();
			Entity prevNode = this.getPreviousNode();

			if(nextNode != null && nextNode instanceof EntityPlayer) {
				if(nextNode.getDistanceToEntity(this) > 1.5D) {
					this.pickUp = true;
				}
				if(this.pickUp && nextNode.getDistanceToEntity(this) < 1.4D) {
					this.removeNode(nextNode);
					EntityItem itemEntity = new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, new ItemStack(BLItemRegistry.cavingRope, 1));
					itemEntity.delayBeforeCanPickup = 0;
					this.worldObj.spawnEntityInWorld(itemEntity);
				}
				if(nextNode.getDistanceToEntity(this) < ROPE_LENGTH - 1) {
					this.canExtend = true;
				}
				if(this.canExtend && nextNode.getDistanceToEntity(this) > ROPE_LENGTH + 1) {
					IInventory inventory = ((EntityPlayer)nextNode).inventory;
					int invSize = inventory.getSizeInventory();
					for(int i = 0; i < invSize; ++i) {
						ItemStack stack = inventory.getStackInSlot(i);
						if(stack != null && stack.getItem() == BLItemRegistry.cavingRope) {
							--stack.stackSize;
							inventory.setInventorySlotContents(i, stack.stackSize > 0 ? stack : null);
							Vec3 connection = this.getConnectionToNext();
							connection = connection.normalize();
							this.extendRope(nextNode, nextNode.posX, nextNode.posY + 0.1D, nextNode.posZ);
							break;
						}
					}
				}
				if(nextNode.getDistanceToEntity(this) > ROPE_LENGTH_MAX) {
					if(nextNode instanceof ICommandSender) {
						((ICommandSender) nextNode).addChatMessage(new ChatComponentTranslation("chat.rope.disconnected"));
					}
					this.setNextNode(null);
				}
			}

			this.motionY *= 0.88D;
			this.motionX *= 0.88D;
			this.motionZ *= 0.88D;

			if(!this.isAttached() && !this.onGround && !this.inWater) {
				boolean isFloating = false;
				if(nextNode != null && this.getDistanceToEntity(nextNode) >= ROPE_LENGTH) {
					Vec3 connection = this.getConnectionToNext();
					double mx = MathHelper.clamp_double(connection.xCoord * 0.02D, -0.5D, 0.5D);
					double my = MathHelper.clamp_double(connection.yCoord * 0.02D, -0.5D, 0.5D);
					double mz = MathHelper.clamp_double(connection.zCoord * 0.02D, -0.5D, 0.5D);
					if(prevNode != null && prevNode.getDistance(this.posX + mx, this.posY + my, this.posZ + mz) < ROPE_LENGTH + 1) {
						this.motionX += mx;
						this.motionZ += mz;
						this.motionY += my;
						isFloating = true;
					}
				}
				if(!isFloating) {
					this.motionY -= 0.1D;
				}

				if(nextNode != null) {
					double mx = MathHelper.clamp_double(this.motionX, -0.5D, 0.5D);
					double my = MathHelper.clamp_double(this.motionY, -0.5D, 0.5D);
					double mz = MathHelper.clamp_double(this.motionZ, -0.5D, 0.5D);
					Vec3 nextPoint = Vec3.createVectorHelper(this.posX + mx, this.posY + my, this.posZ + mz);
					Vec3 tetherPoint = Vec3.createVectorHelper(nextNode.posX, nextNode.posY, nextNode.posZ);
					if(tetherPoint.distanceTo(nextPoint) >= ROPE_LENGTH) {
						Vec3 constrainedPoint = nextPoint.subtract(tetherPoint).normalize();
						constrainedPoint = Vec3.createVectorHelper(
								constrainedPoint.xCoord * (-ROPE_LENGTH), 
								constrainedPoint.yCoord * (-ROPE_LENGTH), 
								constrainedPoint.zCoord * (-ROPE_LENGTH)).addVector(tetherPoint.xCoord, tetherPoint.yCoord, tetherPoint.zCoord);
						Vec3 diff = Vec3.createVectorHelper(this.posX, this.posY, this.posZ).subtract(constrainedPoint);
						this.motionX = diff.xCoord;
						this.motionY = diff.yCoord;
						this.motionZ = diff.zCoord;
					}
				}
				if(prevNode != null) {
					double mx = MathHelper.clamp_double(this.motionX, -0.5D, 0.5D);
					double my = MathHelper.clamp_double(this.motionY, -0.5D, 0.5D);
					double mz = MathHelper.clamp_double(this.motionZ, -0.5D, 0.5D);
					Vec3 nextPoint = Vec3.createVectorHelper(this.posX + mx, this.posY + my, this.posZ + mz);
					Vec3 tetherPoint = Vec3.createVectorHelper(prevNode.posX, prevNode.posY, prevNode.posZ);
					if(tetherPoint.distanceTo(nextPoint) >= ROPE_LENGTH) {
						Vec3 constrainedPoint = nextPoint.subtract(tetherPoint).normalize();
						constrainedPoint = Vec3.createVectorHelper(
								constrainedPoint.xCoord * (-ROPE_LENGTH), 
								constrainedPoint.yCoord * (-ROPE_LENGTH), 
								constrainedPoint.zCoord * (-ROPE_LENGTH)).addVector(tetherPoint.xCoord, tetherPoint.yCoord, tetherPoint.zCoord);
						Vec3 diff = Vec3.createVectorHelper(this.posX, this.posY, this.posZ).subtract(constrainedPoint);
						this.motionX = diff.xCoord;
						this.motionY = diff.yCoord;
						this.motionZ = diff.zCoord;
					}
				}
			} else {
				this.motionX = 0.0D;
				this.motionY = 0.0D;
				this.motionZ = 0.0D;
			}

			if(nextNode == null) {
				if(prevNode == null) {
					this.kill();
				} else {
					this.despawnTimer++;
					if(this.despawnTimer >= 12000) {
						if(prevNode != null && prevNode instanceof EntityRopeNode) {
							EntityRopeNode prevRopeNode = (EntityRopeNode) prevNode;
							prevRopeNode.setNextNode(null);
							prevRopeNode.despawn(); 
						}
						this.kill();
					}
				}
			} else {
				this.despawnTimer = 0;
			}
		}
	}

	@Override
	public boolean interactFirst(EntityPlayer player) {
		Entity nextNode = this.getNextNode();
		Entity prevNode = this.getPreviousNode();
		if(prevNode != null) {
			if(nextNode == null) {
				EntityRopeNode connectedRopeNode = null;
				for(Entity e : (List<Entity>) player.worldObj.loadedEntityList) {
					if(e instanceof EntityRopeNode) {
						EntityRopeNode ropeNode = (EntityRopeNode) e;
						if(ropeNode.getNextNode() == player) {
							connectedRopeNode = ropeNode;
							break;
						}
					}
				}
				if(connectedRopeNode != null) {
					if(player.worldObj.isRemote) {
						player.addChatMessage(new ChatComponentTranslation("chat.rope.already_connected"));
					}
					return false;
				}
				if(!player.worldObj.isRemote) {
					this.setNextNode(player);
				}
				return true;
			} else if(nextNode instanceof EntityRopeNode == false) {
				if(!player.worldObj.isRemote) this.setNextNode(null);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	public void removeNode(Entity nextConnectionNode) {
		Entity nextNode = this.getNextNode();
		Entity prevNode = this.getPreviousNode();
		if(prevNode != null && prevNode instanceof EntityRopeNode) {
			((EntityRopeNode)prevNode).setNextNode(nextConnectionNode);
			((EntityRopeNode)prevNode).canExtend = false;
		}
		this.kill();
	}

	public void despawn() {
		this.despawnTimer = 12000;
	}

	public boolean isAttached() {
		return !this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox.expand(0.1D, 0.1D, 0.1D)).isEmpty();
	}

	public EntityRopeNode extendRope(Entity entity, double x, double y, double z) {
		EntityRopeNode ropeNode = new EntityRopeNode(this.worldObj);
		ropeNode.setLocationAndAngles(x, y, z, 0, 0);
		ropeNode.setPreviousNode(this);
		ropeNode.setNextNode(entity);
		this.setNextNode(ropeNode);
		this.worldObj.spawnEntityInWorld(ropeNode);
		return ropeNode;
	}

	public Vec3 getConnectionToNext() {
		Entity nextNode = this.getNextNode();
		if(nextNode != null) {
			return Vec3.createVectorHelper(nextNode.posX - this.posX, nextNode.posY - this.posY, nextNode.posZ - this.posZ);
		}
		return null;
	}

	public void setNextNodeUUID(String uuid) {
		this.nextNodeUUID = uuid;
	}

	public String getNextNodeUUID() {
		return this.nextNodeUUID;
	}

	public void setNextNode(Entity entity) {
		this.setNextNodeUUID(entity == null ? "" : entity.getUniqueID().toString());
	}

	private Entity getNextNodeByUUID() {
		try {
			UUID uuid = UUID.fromString(this.getNextNodeUUID());
			return uuid == null ? null : this.getEntityByUUID(uuid);
		} catch (IllegalArgumentException illegalargumentexception) {
			return null;
		}
	}

	public void setPreviousNodeUUID(String uuid) {
		this.prevNodeUUID = uuid;;
	}

	public String getPreviousNodeUUID() {
		return this.prevNodeUUID;
	}

	public void setPreviousNode(Entity entity) {
		this.setPreviousNodeUUID(entity == null ? "" : entity.getUniqueID().toString());
	}

	private Entity getPreviousNodeByUUID() {
		try {
			UUID uuid = UUID.fromString(this.getPreviousNodeUUID());
			return uuid == null ? null : this.getEntityByUUID(uuid);
		} catch (IllegalArgumentException illegalargumentexception) {
			return null;
		}
	}

	public Entity getNextNode() {
		return this.worldObj.getEntityByID(this.dataWatcher.getWatchableObjectInt(19));
	}

	public Entity getPreviousNode() {
		return this.worldObj.getEntityByID(this.dataWatcher.getWatchableObjectInt(20));
	}

	private Entity getEntityByUUID(UUID uuid) {
		for(Entity entity : (List<Entity>) this.worldObj.loadedEntityList) {
			if (uuid.equals(entity.getUniqueID())) {
				return entity;
			}
		}
		return null;
	}
}
