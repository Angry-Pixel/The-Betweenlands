package thebetweenlands.common.entity;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.item.misc.ItemGrapplingHook;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.util.PlayerUtil;

public class EntityGrapplingHookNode extends Entity implements IEntityAdditionalSpawnData {
	private static final DataParameter<Integer> DW_PREV_NODE = EntityDataManager.createKey(EntityGrapplingHookNode.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> DW_NEXT_NODE = EntityDataManager.createKey(EntityGrapplingHookNode.class, DataSerializers.VARINT);
	private static final DataParameter<Float> DW_CURRENT_ROPE_LENGTH = EntityDataManager.createKey(EntityGrapplingHookNode.class, DataSerializers.FLOAT);
	private static final DataParameter<Boolean> DW_ATTACHED = EntityDataManager.createKey(EntityGrapplingHookNode.class, DataSerializers.BOOLEAN);

	private UUID nextNodeUUID;
	private UUID prevNodeUUID;

	private int cachedPrevNodeDW;
	private int cachedNextNodeDW;

	private Entity cachedNextNodeEntity;
	private Entity cachedPrevNodeEntity;

	protected boolean isExtending = false;
	protected boolean climbing = false;

	protected double correctionX;
	protected double correctionY;
	protected double correctionZ;

	protected int pullCounter = 0;

	/**
	 * Number of nodes the grappling hook rope has.
	 * Only updated on mount node!
	 */
	protected int nodeCount = 0;
	
	/**
	 * Only set on mount node
	 */
	protected int maxNodeCount;

	protected Vec3d prevWeightPos;
	protected Vec3d weightPos;

	public EntityGrapplingHookNode(World world) {
		super(world);
		this.setSize(0.1F, 0.1F);
	}

	public EntityGrapplingHookNode(World world, int nodeCount, int maxNodeCount) {
		super(world);
		this.setSize(0.1F, 0.1F);
		this.nodeCount = nodeCount;
		this.maxNodeCount = maxNodeCount;
	}

	@Override
	protected void entityInit() {
		this.getDataManager().register(DW_PREV_NODE, -1);
		this.cachedPrevNodeDW = -1;
		this.getDataManager().register(DW_NEXT_NODE, -1);
		this.cachedNextNodeDW = -1;
		this.getDataManager().register(DW_CURRENT_ROPE_LENGTH, (float) this.getDefaultRopeLength());
		this.getDataManager().register(DW_ATTACHED, false);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		this.setNextNodeUUID(nbt.hasUniqueId("nextNodeUUID") ? nbt.getUniqueId("nextNodeUUID") : null);
		this.setPreviousNodeUUID(nbt.hasUniqueId("previousNodeUUID") ? nbt.getUniqueId("previousNodeUUID") : null);
		if(nbt.hasKey("ropeLength", Constants.NBT.TAG_FLOAT)) {
			this.setCurrentRopeLength(nbt.getFloat("ropeLength"));
		} else {
			this.setCurrentRopeLength((float) this.getDefaultRopeLength());
		}
		this.nodeCount = nbt.getInteger("nodeCount");
		this.maxNodeCount = nbt.getInteger("maxNodeCount");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		if(this.getNextNodeUUID() != null) {
			nbt.setUniqueId("nextNodeUUID", this.getNextNodeUUID());
		}
		if(this.getPreviousNodeUUID() != null) {
			nbt.setUniqueId("previousNodeUUID", this.getPreviousNodeUUID());
		}
		nbt.setFloat("ropeLength", this.getCurrentRopeLength());
		nbt.setInteger("nodeCount", this.nodeCount);
		nbt.setInteger("maxNodeCount", this.maxNodeCount);
	}

	@Override
	public double getMountedYOffset() {
		return 0.01D + (this.getControllingPassenger() != null ? -this.getControllingPassenger().getYOffset() : 0);
	}

	@Override
	public void onEntityUpdate() {
		if(this.ticksExisted < 2) {
			//Stupid EntityTrackerEntry is broken and desyncs server position.
			//Tracker updates server side position but *does not* send the change to the client
			//when tracker.updateCounter == 0, causing a desync until the next force teleport
			//packet.......
			//By not moving the entity until then it works.
			return;
		}

		if(this.isMountNode()) {
			this.setSize(0.6F, 1.8F);
		} else {
			this.setSize(0.1F, 0.1F);
		}

		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		boolean attached = this.isAttached();

		if(!this.world.isRemote) {
			this.getDataManager().set(DW_ATTACHED, attached);
		}

		Entity nextNode;
		Entity prevNode;

		if(!this.world.isRemote) {
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
			nextNode = this.getNextNodeClient();
			prevNode = this.getPreviousNodeClient();
		}

		if(!this.isMountNode() || prevNode == null) {
			this.setCurrentRopeLength((float) this.getDefaultRopeLength());
		}

		if(!this.world.isRemote) {
			if(nextNode != null) {

				if(nextNode instanceof EntityGrapplingHookNode && ((EntityGrapplingHookNode) nextNode).isMountNode()) {
					EntityGrapplingHookNode mountNode = ((EntityGrapplingHookNode) nextNode);

					if(mountNode.isExtending && nextNode.posY < this.posY && nextNode.getDistance(this.posX, this.posY, this.posZ) > this.getDefaultRopeLength() - 0.2D) {
						if(mountNode.nodeCount < mountNode.maxNodeCount) {
							Vec3d connection = this.getConnectionToNext();
							if(connection != null) {
								Vec3d newPos = mountNode.getPositionVector().add(connection.scale(-0.5D)).add(0, 0.1D, 0);

								RayTraceResult result = this.world.rayTraceBlocks(mountNode.getPositionVector().add(0, mountNode.height, 0), newPos, false);

								if(result != null && result.typeOfHit == Type.BLOCK && result.hitVec.squareDistanceTo(mountNode.getPositionVector().add(0, mountNode.height, 0)) < newPos.squareDistanceTo(mountNode.getPositionVector().add(0, mountNode.height, 0))) {
									newPos = result.hitVec.add(result.hitVec.subtract(this.getPositionVector().add(0, this.height, 0)).normalize().scale(0.1D));
								}

								EntityGrapplingHookNode newNode = this.extendRope(mountNode, newPos.x, newPos.y, newPos.z);

								if(newNode != null) {
									newNode.setCurrentRopeLength((float) connection.length() / 4);
									this.setCurrentRopeLength((float) connection.length() / 4);

									if(mountNode.getCurrentRopeLength() < this.getDefaultRopeLength() - 0.05F) {
										//TODO This should only happen when reeling in
										mountNode.setCurrentRopeLength(0.05F);
									}
								}
							}
						} else {
							Entity controller = mountNode.getControllingPassenger();
							if(controller instanceof EntityPlayer) {
								((EntityPlayer) controller).sendStatusMessage(new TextComponentTranslation("chat.grappling_hook.max_length"), true);
							}
						}
					}
				}

				if(nextNode.getDistance(this.posX, this.posY + this.height - nextNode.height, this.posZ) > this.getMaxRopeLength()) {
					EntityGrapplingHookNode mountNode = this.getMountNode();
					if(mountNode != null) {
						Entity controller = mountNode.getControllingPassenger();

						if(controller instanceof EntityPlayer) {
							((EntityPlayer) controller).sendStatusMessage(new TextComponentTranslation("chat.grappling_hook.disconnected"), true);
						}

						if(controller instanceof EntityLivingBase) {
							Iterator<ItemStack> it = ((EntityLivingBase) controller).getHeldEquipment().iterator();
							while(it.hasNext()) {
								ItemStack stack = it.next();
								if(!stack.isEmpty() && stack.getItem() instanceof ItemGrapplingHook) {
									((ItemGrapplingHook) stack.getItem()).onGrapplingHookRipped(stack, controller);
								}
							}
						}
					}

					this.setNextNode(null);
				}
			}
		}

		float friction = 1.0F;

		if(this.onGround || this.collidedHorizontally || this.collidedVertically) {
			friction = 0.5F;
		}

		this.motionY *= 0.98D * friction;
		this.motionX *= 0.98D * friction;
		this.motionZ *= 0.98D * friction;

		this.correctionX *= 0.5D * friction;
		this.correctionY *= 0.5D * friction;
		this.correctionZ *= 0.5D * friction;

		if(!attached) {
			this.handleWaterMovement();
			this.move(MoverType.SELF, this.motionX + this.correctionX, this.motionY + this.correctionY, this.motionZ + this.correctionZ);
			this.pushOutOfBlocks(this.posX, this.posY, this.posZ);

			//Check if it is now attached after move and should play sound
			if(this.isAttached()) {
				this.playSound(SoundRegistry.ROPE_GRAB, 0.6F, 0.8F + this.world.rand.nextFloat() * 0.3F);
			}
		}

		boolean isMovable = this.isMovable();

		if(isMovable && !this.climbing) {
			this.motionY -= 0.08D;
		}

		if(prevNode instanceof EntityGrapplingHookNode) {
			boolean isPullable = this.isPullable();

			if(isPullable && isMovable) {
				if(nextNode instanceof EntityGrapplingHookNode) {
					this.constrainMotion(prevNode, nextNode, 0.99D, 0.0D, 1.0D);
				}

				this.constrainMotion(prevNode, prevNode, 0.99D, -Double.MAX_VALUE, 0.1D);
			}

			Vec3d diff = prevNode.getPositionVector().add(0, prevNode.height, 0).subtract(this.getPositionVector().add(0, this.height, 0));

			if(diff.length() > this.getCurrentRopeLength()) {
				double correction = diff.length() - this.getCurrentRopeLength();

				Vec3d forceVec = diff.normalize().scale(correction * 0.5D);

				EntityGrapplingHookNode other = (EntityGrapplingHookNode) prevNode;

				boolean isThisCorrectable = isPullable && isMovable;
				boolean isOtherCorrectable = other.isPullable() && other.isMovable();

				float factor = !isThisCorrectable || !isOtherCorrectable ? 2.0f : 1.0f;

				if(isThisCorrectable) {
					this.correctionX += forceVec.x * factor;
					this.correctionY += forceVec.y * factor;
					this.correctionZ += forceVec.z * factor;
				}

				if(isOtherCorrectable) {
					other.correctionX += -forceVec.x * factor;
					other.correctionY += -forceVec.y * factor;
					other.correctionZ += -forceVec.z * factor;
				}
			}
		}

		this.velocityChanged = true;

		if(!this.isMovable()) {
			this.motionX = 0.0D;
			this.motionY = 0.0D;
			this.motionZ = 0.0D;
		}

		this.checkForEntityCollisions(prevNode, nextNode);

		if(this.world.isRemote && this.isMountNode()) {
			this.updateWeight();
		}

		this.climbing = false;

		Entity controller = this.getControllingPassenger();

		if(controller instanceof EntityLivingBase) {
			this.handleControllerMovement((EntityLivingBase) controller);
		}

		boolean hasValidUser = false;

		if(controller != null && this.isMountNode()) {
			Iterator<ItemStack> it = controller.getHeldEquipment().iterator();
			while(it.hasNext()) {
				ItemStack stack = it.next();
				if(!stack.isEmpty() && stack.getItem() instanceof ItemGrapplingHook && ((ItemGrapplingHook) stack.getItem()).canRideGrapplingHook(stack, controller)) {
					hasValidUser = true;
					break;
				}
			}
		}

		if(!this.world.isRemote && (nextNode == null || (this.isMountNode() && (!hasValidUser || prevNode == null)))) {
			this.onKillCommand();
		}

		this.firstUpdate = false;
	}

	protected void updateWeight() {
		final double weightRopeLength = 2D;

		Vec3d tether = this.getPositionVector().add(0, this.height, 0);

		if(this.weightPos == null) {
			this.prevWeightPos = this.weightPos = tether.add(0, -weightRopeLength, 0);
		}

		this.prevWeightPos = this.weightPos;

		this.weightPos = this.weightPos.add(0, -0.5D, 0);

		if(this.weightPos.distanceTo(tether) > weightRopeLength) {
			this.weightPos = tether.add(this.weightPos.subtract(tether).normalize().scale(weightRopeLength));
		}
	}

	protected void constrainMotion(Entity parentNode, Entity constraintNode, double ropeFriction, double constraintMin, double constraintDampening) {
		Vec3d nextPoint = new Vec3d(this.posX + this.motionX - parentNode.motionX, this.posY + this.height + this.motionY - parentNode.motionY, this.posZ + this.motionZ - parentNode.motionZ);

		Vec3d tetherPoint = new Vec3d(constraintNode.posX, constraintNode.posY + constraintNode.height, constraintNode.posZ);
		float currentRopeLength = this.getCurrentRopeLength();

		if(tetherPoint.distanceTo(nextPoint) >= currentRopeLength) {
			Vec3d constrainedPoint = nextPoint.subtract(tetherPoint).normalize();

			constrainedPoint = constrainedPoint.scale(currentRopeLength).add(tetherPoint.x, tetherPoint.y, tetherPoint.z);

			Vec3d fwd = tetherPoint.subtract(constrainedPoint).normalize();

			Vec3d relMotion = new Vec3d(this.motionX - parentNode.motionX, this.motionY - parentNode.motionY, this.motionZ - parentNode.motionZ);

			Vec3d side = fwd.crossProduct(new Vec3d(0, 1, 0)).normalize();
			Vec3d up = side.crossProduct(fwd).normalize();

			Vec3d newMotion = side.scale(side.dotProduct(relMotion) * 1F).add(up.scale(up.dotProduct(relMotion) * 1F)).add(fwd.scale(Math.max(constraintMin, fwd.dotProduct(relMotion) * constraintDampening)));

			this.motionX = (parentNode.motionX + newMotion.x) * ropeFriction;
			this.motionY = (parentNode.motionY + newMotion.y) * ropeFriction;
			this.motionZ = (parentNode.motionZ + newMotion.z) * ropeFriction;
		}
	}

	protected boolean isMovable() {
		return !this.isAttached() && !this.onGround && !this.inWater;
	}

	protected boolean isPullable() {
		return !this.isMountNode() || this.getCurrentRopeLength() < this.getDefaultRopeLength() - 0.05D;
	}

	@Override
	public void updatePassenger(Entity passenger) {
		super.updatePassenger(passenger);
		
		PlayerUtil.resetFloating(passenger);
	}
	
	protected void handleControllerMovement(EntityLivingBase controller) {
		this.isExtending = false;

		controller.fallDistance = 0;

		if(!this.world.isRemote) {
			if(controller.isJumping) {
				if(controller.moveForward > 0) {
					boolean canReelIn = false;

					//Only let player reel in once at least one node has attached.
					//To prevent the grappling hook from being abused for flight.
					Entity checkRopeNode = this.getPreviousNode();
					while(checkRopeNode instanceof EntityGrapplingHookNode) {
						if(((EntityGrapplingHookNode) checkRopeNode).isAttached()) {
							canReelIn = true;
							break;
						}

						checkRopeNode = ((EntityGrapplingHookNode) checkRopeNode).getPreviousNode();
					}

					if(canReelIn) {
						Entity prevNode = this.getPreviousNode();

						if(prevNode instanceof EntityGrapplingHookNode) {
							Vec3d dir = prevNode.getPositionVector().subtract(this.getPositionVector()).normalize();

							float prevStepHeight = this.stepHeight;
							this.stepHeight = 1.25f;

							//On ground required for step to work
							this.onGround = true;
							this.move(MoverType.SELF, dir.x * 0.25D, dir.y * 0.25D, dir.z * 0.52D);

							if(this.collidedHorizontally && dir.y > 0) {
								this.onGround = true;
								this.move(MoverType.SELF, 0, 0.2D, 0);
								this.climbing = true;
							}

							this.stepHeight = prevStepHeight;

							if(prevNode.getEntityBoundingBox().intersects(this.getEntityBoundingBox())) {
								((EntityGrapplingHookNode) prevNode).removeNode(this);
								this.setCurrentRopeLength((float) this.getDefaultRopeLength() - 0.1F);
							} else {
								this.setCurrentRopeLength(Math.min((float) this.getDefaultRopeLength() - 0.1F, (float) prevNode.getDistance(this.posX, this.posY + this.height - prevNode.height, this.posZ)));
							}

							if(this.pullCounter % 24 == 0) {
								this.world.playSound(null, controller.posX, controller.posY, controller.posZ, SoundRegistry.ROPE_PULL, SoundCategory.PLAYERS, 1.5F, 1);
							}

							this.pullCounter++;
						}
					}
				} else if(controller.moveForward < 0) {
					this.pullCounter = 0;

					this.setCurrentRopeLength(Math.min((float) this.getDefaultRopeLength() - 0.1F, this.getCurrentRopeLength() + 0.2F));
					this.isExtending = true;
				}
			} else {
				this.pullCounter = 0;

				if((Math.abs(controller.moveForward) > 0.05D || Math.abs(controller.moveStrafing) > 0.05D) && !this.onGround) {
					int count = 0;

					double swingX = 0;
					double swingZ = 0;

					if(controller.moveForward > 0) {
						swingX += Math.cos(Math.toRadians(controller.rotationYaw + 90));
						swingZ += Math.sin(Math.toRadians(controller.rotationYaw + 90));
						count++;
					}
					if(controller.moveForward < 0) {
						swingX += Math.cos(Math.toRadians(controller.rotationYaw - 90));
						swingZ += Math.sin(Math.toRadians(controller.rotationYaw - 90));
						count++;
					}
					if(controller.moveStrafing > 0) {
						swingX += Math.cos(Math.toRadians(controller.rotationYaw));
						swingZ += Math.sin(Math.toRadians(controller.rotationYaw));
						count++;
					} 
					if(controller.moveStrafing < 0){
						swingX += Math.cos(Math.toRadians(controller.rotationYaw + 180));
						swingZ += Math.sin(Math.toRadians(controller.rotationYaw + 180));
						count++;
					}

					swingX /= count;
					swingZ /= count;

					double swingStrength = 0.05D;

					this.motionX += swingX * swingStrength;
					this.motionZ += swingZ * swingStrength;

					int incr = 0;
					Entity prev = this.getPreviousNode();
					while(prev instanceof EntityGrapplingHookNode && !((EntityGrapplingHookNode) prev).isAttached()) {
						if(!prev.onGround) {
							prev.motionX += swingX * swingStrength / (1 + incr * 2);
							prev.motionZ += swingZ * swingStrength / (1 + incr * 2);
						}

						prev = ((EntityGrapplingHookNode) prev).getPreviousNode();

						incr++;
					}
				}
			}
		}
	}

	protected void checkForEntityCollisions(Entity prevNode, Entity nextNode) {
		if(!this.world.isRemote && prevNode != null) {
			double velocity = Math.sqrt((this.posX-this.prevPosX)*(this.posX-this.prevPosX) + (this.posY-this.prevPosY)*(this.posY-this.prevPosY) + (this.posZ-this.prevPosZ)*(this.posZ-this.prevPosZ));

			Entity mountNode = this.getMountNode();
			if(mountNode != null) {
				Entity user = mountNode.getControllingPassenger();

				if(user != null && velocity > 0.25D) {
					List<EntityLivingBase> entities = this.world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(this.posX, this.posY, this.posZ, prevNode.posX, prevNode.posY, prevNode.posZ));

					for(EntityLivingBase entity : entities) {
						if(entity != user) {
							RayTraceResult intersect = entity.getEntityBoundingBox().calculateIntercept(new Vec3d(this.posX, this.posY, this.posZ), new Vec3d(prevNode.posX, prevNode.posY, prevNode.posZ));

							if(intersect != null) {
								DamageSource source;

								if(user instanceof EntityPlayer) {
									source = new EntityDamageSourceIndirect("player", this, user);
								} else {
									source = new EntityDamageSourceIndirect("mob", this, user);
								}

								entity.attackEntityFrom(source, 3.0F + (float) Math.min((velocity - 0.25D) * 1.5D, 4));
							}
						}
					}
				}
			}
		}
	}

	@Override
	public boolean canBeCollidedWith() {
		return !this.isMountNode();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isInRangeToRenderDist(double distance) {
		return distance < 4096.0D;
	}

	@Override
	@Nullable
	public Entity getControllingPassenger() {
		return this.getPassengers().isEmpty() ? null : (Entity)this.getPassengers().get(0);
	}

	@Override
	public boolean canPassengerSteer() {
		return false;
	}

	@Override
	public boolean shouldRiderSit() {
		return false;
	}

	@Override
	protected void removePassenger(Entity passenger) {
		super.removePassenger(passenger);

		passenger.motionX = this.motionX * 1.5D;
		passenger.motionY = this.motionY;
		passenger.motionZ = this.motionZ * 1.5D;
	}

	@Override
	public void fall(float distance, float damageMultiplier) {
		//No fall damage to node or rider
	}

	public boolean isAttached() {
		if(this.world.isRemote) {
			return this.getDataManager().get(DW_ATTACHED);
		}
		return !this.isMountNode() /*&& this.getPreviousNode() == null*/ && !this.world.getCollisionBoxes(this, this.getEntityBoundingBox().grow(0.1D, 0.1D, 0.1D)).isEmpty();
	}

	public EntityGrapplingHookNode extendRope(Entity entity, double x, double y, double z) {
		EntityGrapplingHookNode mountNode = this.getMountNode();

		if(mountNode != null && mountNode.nodeCount < mountNode.maxNodeCount) {
			EntityGrapplingHookNode ropeNode = new EntityGrapplingHookNode(this.world);
			ropeNode.setLocationAndAngles(x, y, z, 0, 0);

			ropeNode.setPreviousNode(this);
			this.setNextNode(ropeNode);

			ropeNode.setNextNode(entity);

			if(entity instanceof EntityGrapplingHookNode) {
				((EntityGrapplingHookNode) entity).setPreviousNode(ropeNode);
			}

			this.world.spawnEntity(ropeNode);

			mountNode.nodeCount++;

			return ropeNode;
		}

		return null;
	}

	public void removeNode(Entity nextConnectionNode) {
		Entity prevNode = this.getPreviousNodeByUUID();

		if(prevNode != null && prevNode instanceof EntityGrapplingHookNode) {
			((EntityGrapplingHookNode)prevNode).setNextNode(nextConnectionNode);

			if(nextConnectionNode instanceof EntityGrapplingHookNode) {
				((EntityGrapplingHookNode) nextConnectionNode).setPreviousNode(prevNode);
			}
		}

		EntityGrapplingHookNode mountNode = this.getMountNode();
		if(mountNode != null) {
			mountNode.nodeCount = Math.max(0, mountNode.nodeCount - 1);
		}

		this.onKillCommand();
	}

	public Vec3d getConnectionToNext() {
		Entity nextNode;
		if(this.world.isRemote) {
			nextNode = this.getNextNodeClient();
		} else {
			nextNode = this.getNextNodeByUUID();
		}
		if(nextNode != null) {
			return new Vec3d(nextNode.posX - this.posX, nextNode.posY + nextNode.height - (this.posY + this.height), nextNode.posZ - this.posZ);
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
		if(this.cachedPrevNodeEntity != null && this.cachedPrevNodeEntity.isEntityAlive() && this.cachedPrevNodeEntity.getUniqueID().equals(this.prevNodeUUID)) {
			return this.cachedPrevNodeEntity;
		} else {
			UUID uuid = this.prevNodeUUID;
			Entity entity = uuid == null ? null : this.getEntityByUUID(uuid);
			this.cachedPrevNodeEntity = entity;
			return entity;
		}
	}

	@SideOnly(Side.CLIENT)
	public Entity getNextNodeClient() {
		if(this.cachedNextNodeEntity == null || !this.cachedNextNodeEntity.isEntityAlive() || this.cachedNextNodeEntity.getEntityId() != this.getDataManager().get(DW_NEXT_NODE)) {
			Entity entity = this.world.getEntityByID(this.getDataManager().get(DW_NEXT_NODE));
			this.cachedNextNodeEntity = entity;
			return entity;
		}
		return this.cachedNextNodeEntity;
	}

	@SideOnly(Side.CLIENT)
	public Entity getPreviousNodeClient() {
		if(this.cachedPrevNodeEntity == null || !this.cachedPrevNodeEntity.isEntityAlive() || this.cachedPrevNodeEntity.getEntityId() != this.getDataManager().get(DW_PREV_NODE)) {
			Entity entity = this.world.getEntityByID(this.getDataManager().get(DW_PREV_NODE));
			this.cachedPrevNodeEntity = entity;
			return entity;
		}
		return this.cachedPrevNodeEntity;
	}

	public Entity getNextNode() {
		if(this.world.isRemote) {
			return this.getNextNodeClient();
		} else {
			return this.getNextNodeByUUID();
		}
	}

	public Entity getPreviousNode() {
		if(this.world.isRemote) {
			return this.getPreviousNodeClient();
		} else {
			return this.getPreviousNodeByUUID();
		}
	}

	private Entity getEntityByUUID(UUID uuid) {
		for(Entity entity : (List<Entity>) this.world.getEntitiesWithinAABB(Entity.class, this.getEntityBoundingBox().grow(24, 24, 24))) {
			if (uuid.equals(entity.getUniqueID())) {
				return entity;
			}
		}
		return null;
	}

	public boolean isMountNode() {
		return this.getNextNode() instanceof EntityLivingBase;
	}

	public float getCurrentRopeLength() {
		return this.dataManager.get(DW_CURRENT_ROPE_LENGTH);
	}

	public void setCurrentRopeLength(float length) {
		this.dataManager.set(DW_CURRENT_ROPE_LENGTH, length);
	}

	protected float getDefaultRopeLength() {
		return 2.0F;
	}

	protected float getMaxRopeLength() {
		return 12.0F;
	}

	//TODO Cache this somehow?
	public EntityGrapplingHookNode getMountNode() {
		Entity node = this;
		while(node instanceof EntityGrapplingHookNode) {
			EntityGrapplingHookNode hookNode = (EntityGrapplingHookNode) node;

			if(hookNode.isMountNode()) {
				return hookNode;
			}

			node = hookNode.getNextNode();
		}

		return null;
	}

	public Vec3d getWeightPos(float partialTicks) {
		if(this.weightPos == null) {
			return new Vec3d(this.prevPosX + (this.posX - this.prevPosX) * partialTicks, this.prevPosY + (this.posY - this.prevPosY) * partialTicks, this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks);
		} else {
			return this.prevWeightPos.add(this.weightPos.subtract(this.prevWeightPos).scale(partialTicks));
		}
	}

	@Override
	public void writeSpawnData(ByteBuf buf) {
		buf.writeInt(this.maxNodeCount);
	}

	@Override
	public void readSpawnData(ByteBuf buf) {
		this.maxNodeCount = buf.readInt();
	}
}
