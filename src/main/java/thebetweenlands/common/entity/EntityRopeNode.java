package thebetweenlands.common.entity;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.registries.ItemRegistry;

public class EntityRopeNode extends Entity {
	private static final DataParameter<Integer> DW_PREV_NODE = EntityDataManager.createKey(EntityRopeNode.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> DW_NEXT_NODE = EntityDataManager.createKey(EntityRopeNode.class, DataSerializers.VARINT);

	public static final double ROPE_LENGTH = 4.0D;
	public static final double ROPE_LENGTH_MAX = 12.0D;

	private boolean canExtend = true;
	private boolean pickUp = false;
	private int despawnTimer = 0;

	private UUID nextNodeUUID;
	private UUID prevNodeUUID;

	private int cachedPrevNodeDW;
	private int cachedNextNodeDW;

	private Entity cachedNextNodeEntity;
	private Entity cachedPrevNodeEntity;

	public EntityRopeNode(World world) {
		super(world);
		this.setSize(0.1F, 0.1F);
	}

	@Override
	protected void entityInit() {
		this.getDataManager().register(DW_PREV_NODE, -1);
		this.cachedPrevNodeDW = -1;
		this.getDataManager().register(DW_NEXT_NODE, -1);
		this.cachedNextNodeDW = -1;
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		this.setNextNodeUUID(nbt.hasUniqueId("nextNodeUUID") ? nbt.getUniqueId("nextNodeUUID") : null);
		this.setPreviousNodeUUID(nbt.hasUniqueId("previousNodeUUID") ? nbt.getUniqueId("previousNodeUUID") : null);
		this.pickUp = nbt.getBoolean("pickUp");
		this.canExtend = nbt.getBoolean("canExtend");
		this.despawnTimer = nbt.getInteger("despawnTimer");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		if(this.getNextNodeUUID() != null) {
			nbt.setUniqueId("nextNodeUUID", this.getNextNodeUUID());
		}
		if(this.getPreviousNodeUUID() != null) {
			nbt.setUniqueId("previousNodeUUID", this.getPreviousNodeUUID());
		}
		nbt.setBoolean("pickUp", this.pickUp);
		nbt.setBoolean("canExtend", this.canExtend);
		nbt.setInteger("despawnTimer", this.despawnTimer);
	}

	@Override
	public void onEntityUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		boolean attached = this.isAttached();

		if(!attached) {
			this.handleWaterMovement();
			this.moveEntity(this.motionX, this.motionY, this.motionZ);
		}

		boolean prevAttached = attached;
		attached = this.isAttached();

		if(attached && !prevAttached) {
			this.worldObj.playSound((EntityPlayer)null, this.posX, this.posY, this.posZ, SoundEvents.BLOCK_METAL_STEP, SoundCategory.PLAYERS, 1, 1.5F);
		}

		Entity nextNode;
		Entity prevNode;

		if(!this.worldObj.isRemote) {
			nextNode = this.getNextNodeByUUID();
			prevNode = this.getPreviousNodeByUUID();

			if(nextNode != null && nextNode.getEntityId() != this.cachedNextNodeDW) {
				this.getDataManager().set(DW_NEXT_NODE, nextNode.getEntityId());
				this.cachedNextNodeDW = nextNode.getEntityId();
			} else if(nextNode == null && this.cachedNextNodeDW != -1) {
				this.getDataManager().set(DW_NEXT_NODE, -1);
				this.cachedNextNodeDW = -1;
			}

			if(prevNode != null && prevNode.getEntityId() != this.cachedPrevNodeDW) {
				this.getDataManager().set(DW_PREV_NODE, prevNode.getEntityId());
				this.cachedPrevNodeDW = prevNode.getEntityId();
			} else if(prevNode == null && this.cachedPrevNodeDW != -1) {
				this.getDataManager().set(DW_PREV_NODE, -1);
				this.cachedPrevNodeDW = -1;
			}
		} else {
			nextNode = this.getNextNode();
			prevNode = this.getPreviousNode();
		}

		if(!this.worldObj.isRemote) {
			if(nextNode instanceof EntityPlayer) {
				if(nextNode.getDistanceToEntity(this) > 1.5D) {
					this.pickUp = true;
				}
				if(this.pickUp && nextNode.getEntityBoundingBox().expand(0.4D, 0.4D, 0.4D).intersectsWith(this.getEntityBoundingBox())) {
					this.removeNode(nextNode);
					EntityPlayer player = (EntityPlayer) nextNode;
					if(player.inventory.addItemStackToInventory(new ItemStack(ItemRegistry.CAVING_ROPE, 1))) {
						this.worldObj.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
					} else {
						EntityItem itemEntity = new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, new ItemStack(ItemRegistry.CAVING_ROPE, 1));
						itemEntity.setPickupDelay(0);
						this.worldObj.spawnEntityInWorld(itemEntity);
					}
				}
				if(nextNode.getDistanceToEntity(this) < ROPE_LENGTH - 1) {
					this.canExtend = true;
				}
				if(this.canExtend && nextNode.getDistanceToEntity(this) > ROPE_LENGTH + 1) {
					IInventory inventory = ((EntityPlayer)nextNode).inventory;
					int invSize = inventory.getSizeInventory();
					for(int i = 0; i < invSize; ++i) {
						ItemStack stack = inventory.getStackInSlot(i);
						if(stack != null && stack.getItem() == ItemRegistry.CAVING_ROPE) {
							--stack.stackSize;
							inventory.setInventorySlotContents(i, stack.stackSize > 0 ? stack : null);
							Vec3d connection = this.getConnectionToNext();
							if(connection != null) {
								Vec3d newPos = nextNode.getPositionVector().add(connection.scale(-0.5D)).addVector(0, 0.1D, 0);
								RayTraceResult result = this.worldObj.rayTraceBlocks(nextNode.getPositionVector(), newPos, false);
								if(result != null && result.typeOfHit == Type.BLOCK && result.hitVec.squareDistanceTo(nextNode.getPositionVector()) < newPos.squareDistanceTo(nextNode.getPositionVector())) {
									newPos = result.hitVec.add(result.hitVec.subtract(this.getPositionVector()).normalize().scale(0.1D));
								}
								EntityRopeNode rope = this.extendRope(nextNode, newPos.xCoord, newPos.yCoord, newPos.zCoord);
								if(rope.isAttached()) {
									this.worldObj.playSound((EntityPlayer)null, this.posX, this.posY, this.posZ, SoundEvents.BLOCK_METAL_STEP, SoundCategory.PLAYERS, 1, 1.5F);
								}
								break;
							}
						}
					}
				}
				if(nextNode.getDistanceToEntity(this) > ROPE_LENGTH_MAX) {
					if(nextNode instanceof ICommandSender) {
						((ICommandSender) nextNode).addChatMessage(new TextComponentTranslation("chat.rope.disconnected"));
					}
					this.setNextNode(null);
				}
			}
		}

		this.motionY *= 0.88D;
		this.motionX *= 0.88D;
		this.motionZ *= 0.88D;

		if(!attached && !this.onGround && !this.inWater) {
			boolean isFloating = false;

			if(nextNode != null && this.getDistanceToEntity(nextNode) >= ROPE_LENGTH) {
				Vec3d connection = this.getConnectionToNext();
				if(connection != null) {
					double mx = connection.xCoord * 0.02D;
					double my = connection.yCoord * 0.02D;
					double mz = connection.zCoord * 0.02D;
					double len = Math.sqrt(mx*mx + my*my + mz*mz);
					if(len > 0.5D) {
						mx /= len * 0.5D;
						my /= len * 0.5D;
						mz /= len * 0.5D;
					}
					if(prevNode != null && prevNode.getDistance(this.posX + mx, this.posY + my, this.posZ + mz) < ROPE_LENGTH + 1) {
						this.motionX += mx;
						this.motionZ += mz;
						this.motionY += my;
						isFloating = true;
					}
				}
			}

			if(!isFloating) {
				this.motionY -= 0.28D;
			}

			if(nextNode != null) {
				double mx = this.motionX;
				double my = this.motionY;
				double mz = this.motionZ;
				Vec3d nextPoint = new Vec3d(this.posX + mx, this.posY + my, this.posZ + mz);
				Vec3d tetherPoint = new Vec3d(nextNode.posX, nextNode.posY, nextNode.posZ);
				if(tetherPoint.distanceTo(nextPoint) >= ROPE_LENGTH) {
					Vec3d constrainedPoint = nextPoint.subtract(tetherPoint).normalize();
					constrainedPoint = new Vec3d(
							constrainedPoint.xCoord * ROPE_LENGTH, 
							constrainedPoint.yCoord * ROPE_LENGTH, 
							constrainedPoint.zCoord * ROPE_LENGTH).addVector(tetherPoint.xCoord, tetherPoint.yCoord, tetherPoint.zCoord);
					Vec3d diff = new Vec3d(this.posX, this.posY, this.posZ).subtract(constrainedPoint);
					this.motionX = -diff.xCoord;
					this.motionY = -diff.yCoord;
					this.motionZ = -diff.zCoord;
				}
			}

			if(prevNode != null) {
				double mx = this.motionX;
				double my = this.motionY;
				double mz = this.motionZ;
				Vec3d nextPoint = new Vec3d(this.posX + mx, this.posY + my, this.posZ + mz);
				Vec3d tetherPoint = new Vec3d(prevNode.posX, prevNode.posY, prevNode.posZ);
				if(tetherPoint.distanceTo(nextPoint) >= ROPE_LENGTH) {
					Vec3d constrainedPoint = nextPoint.subtract(tetherPoint).normalize();
					constrainedPoint = new Vec3d(
							constrainedPoint.xCoord * ROPE_LENGTH, 
							constrainedPoint.yCoord * ROPE_LENGTH, 
							constrainedPoint.zCoord * ROPE_LENGTH).addVector(tetherPoint.xCoord, tetherPoint.yCoord, tetherPoint.zCoord);
					Vec3d diff = new Vec3d(this.posX, this.posY, this.posZ).subtract(constrainedPoint).scale(0.8D);
					this.motionX = -diff.xCoord;
					this.motionY = -diff.yCoord;
					this.motionZ = -diff.zCoord;
				}
			}

			double speed = Math.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
			this.motionX *= Math.min(speed, 0.05D) / 0.05D;
			this.motionY *= Math.min(speed, 0.05D) / 0.05D;
			this.motionZ *= Math.min(speed, 0.05D) / 0.05D;
		} else {
			this.motionX = 0.0D;
			this.motionY = 0.0D;
			this.motionZ = 0.0D;
		}

		if(!this.worldObj.isRemote) {
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
	public boolean processInitialInteract(EntityPlayer player, @Nullable ItemStack stack, EnumHand hand) {
		if(!this.worldObj.isRemote) {
			Entity prevNode = this.getPreviousNodeByUUID();
			Entity nextNode = this.getNextNodeByUUID();

			if(prevNode != null) {
				if(nextNode == null) {
					EntityRopeNode connectedRopeNode = null;
					for(Entity e : (List<Entity>) player.worldObj.loadedEntityList) {
						if(e instanceof EntityRopeNode) {
							EntityRopeNode ropeNode = (EntityRopeNode) e;
							if(ropeNode.getNextNodeByUUID() == player) {
								connectedRopeNode = ropeNode;
								break;
							}
						}
					}
					if(connectedRopeNode != null) {
						player.addChatMessage(new TextComponentTranslation("chat.rope.alreadyConnected"));
						return false;
					}

					this.setNextNode(player);

					return true;
				} else if(nextNode instanceof EntityRopeNode == false) {
					this.setNextNode(null);
					return true;
				}
			}

			if(nextNode instanceof EntityRopeNode) {
				EntityRopeNode endNode = (EntityRopeNode) nextNode;
				while(endNode.getNextNodeByUUID() instanceof EntityRopeNode && endNode.getNextNodeByUUID() != this) {
					endNode = (EntityRopeNode) endNode.getNextNodeByUUID();
				}
				if(endNode.getNextNodeByUUID() == null && endNode.getPreviousNodeByUUID() instanceof EntityRopeNode) {
					((EntityRopeNode) endNode.getPreviousNodeByUUID()).setNextNode(null);
					endNode.setDead();

					if(player.inventory.addItemStackToInventory(new ItemStack(ItemRegistry.CAVING_ROPE, 1))) {
						this.worldObj.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
					} else {
						EntityItem itemEntity = new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, new ItemStack(ItemRegistry.CAVING_ROPE, 1));
						itemEntity.setPickupDelay(0);
						this.worldObj.spawnEntityInWorld(itemEntity);
					}

					return true;
				}
			}
		} else {
			return true;
		}

		return false;
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isInRangeToRenderDist(double distance) {
		return distance < 1024.0D;
	}

	public void removeNode(Entity nextConnectionNode) {
		Entity prevNode = this.getPreviousNodeByUUID();
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
		return !this.worldObj.getCollisionBoxes(this.getEntityBoundingBox().expand(0.1D, 0.1D, 0.1D)).isEmpty();
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

	public Vec3d getConnectionToNext() {
		Entity nextNode;
		if(this.worldObj.isRemote) {
			nextNode = this.getNextNode();
		} else {
			nextNode = this.getNextNodeByUUID();
		}
		if(nextNode != null) {
			return new Vec3d(nextNode.posX - this.posX, nextNode.posY - this.posY, nextNode.posZ - this.posZ);
		}
		return null;
	}

	public void setNextNodeUUID(UUID uuid) {
		this.nextNodeUUID = uuid;
		if(this.cachedNextNodeEntity != null && !this.cachedNextNodeEntity.getUniqueID().equals(uuid)) {
			this.cachedNextNodeEntity = null;
		}
	}

	public UUID getNextNodeUUID() {
		return this.nextNodeUUID;
	}

	public void setNextNode(Entity entity) {
		this.cachedNextNodeEntity = entity;
		this.setNextNodeUUID(entity == null ? null : entity.getUniqueID());
	}

	public Entity getNextNodeByUUID() {
		if(this.cachedNextNodeEntity != null && this.cachedNextNodeEntity.isEntityAlive() && this.cachedNextNodeEntity.getUniqueID().equals(this.nextNodeUUID)) {
			return this.cachedNextNodeEntity;
		} else {
			UUID uuid = this.nextNodeUUID;
			Entity entity = uuid == null ? null : this.getEntityByUUID(uuid);
			this.cachedNextNodeEntity = entity;
			return entity;
		}
	}

	public void setPreviousNodeUUID(UUID uuid) {
		this.prevNodeUUID = uuid;
		if(this.cachedPrevNodeEntity != null && !this.cachedPrevNodeEntity.getUniqueID().equals(uuid)) {
			this.cachedPrevNodeEntity = null;
		}
	}

	public UUID getPreviousNodeUUID() {
		return this.prevNodeUUID;
	}

	public void setPreviousNode(Entity entity) {
		this.cachedPrevNodeEntity = entity;
		this.setPreviousNodeUUID(entity == null ? null : entity.getUniqueID());
	}

	public Entity getPreviousNodeByUUID() {
		if(this.cachedPrevNodeEntity != null && this.cachedPrevNodeEntity.isEntityAlive() && this.cachedPrevNodeEntity.getUniqueID().equals(this.cachedPrevNodeEntity)) {
			return this.cachedPrevNodeEntity;
		} else {
			UUID uuid = this.prevNodeUUID;
			Entity entity = uuid == null ? null : this.getEntityByUUID(uuid);
			this.cachedPrevNodeEntity = entity;
			return entity;
		}
	}

	@SideOnly(Side.CLIENT)
	public Entity getNextNode() {
		if(this.cachedNextNodeEntity == null || !this.cachedNextNodeEntity.isEntityAlive() || this.cachedNextNodeEntity.getEntityId() != this.getDataManager().get(DW_NEXT_NODE)) {
			Entity entity = this.worldObj.getEntityByID(this.getDataManager().get(DW_NEXT_NODE));
			this.cachedNextNodeEntity = entity;
			return entity;
		}
		return this.cachedNextNodeEntity;
	}

	@SideOnly(Side.CLIENT)
	public Entity getPreviousNode() {
		if(this.cachedPrevNodeEntity == null || !this.cachedPrevNodeEntity.isEntityAlive() || this.cachedPrevNodeEntity.getEntityId() != this.getDataManager().get(DW_PREV_NODE)) {
			Entity entity = this.worldObj.getEntityByID(this.getDataManager().get(DW_PREV_NODE));
			this.cachedPrevNodeEntity = entity;
			return entity;
		}
		return this.cachedPrevNodeEntity;
	}

	private Entity getEntityByUUID(UUID uuid) {
		for(Entity entity : (List<Entity>) this.worldObj.getEntitiesWithinAABB(Entity.class, this.getEntityBoundingBox().expand(24, 24, 24))) {
			if (uuid.equals(entity.getUniqueID())) {
				return entity;
			}
		}
		return null;
	}
}
