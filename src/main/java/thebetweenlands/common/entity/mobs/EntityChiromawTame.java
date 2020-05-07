package thebetweenlands.common.entity.mobs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketSetPassengers;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.entity.IPullerEntity;
import thebetweenlands.api.entity.IPullerEntityProvider;
import thebetweenlands.api.entity.IRingOfGatheringMinion;
import thebetweenlands.api.item.IEquippable;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.EntityTameableBL;
import thebetweenlands.common.entity.ai.EntityAIFlyingWander;
import thebetweenlands.common.entity.ai.EntityAISitBL;
import thebetweenlands.common.entity.ai.PathNavigateFlyingBL;
import thebetweenlands.common.entity.draeton.DraetonPhysicsPart;
import thebetweenlands.common.entity.draeton.EntityDraeton;
import thebetweenlands.common.entity.draeton.EntityPullerChiromaw;
import thebetweenlands.common.entity.movement.FlightMoveHelper;
import thebetweenlands.common.entity.projectiles.EntityBLArrow;
import thebetweenlands.common.item.tools.bow.EnumArrowType;
import thebetweenlands.common.network.serverbound.MessageChiromawDoubleJump;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityChiromawTame extends EntityTameableBL implements IRingOfGatheringMinion, IPullerEntityProvider<EntityPullerChiromaw> {
	private static final byte EVENT_DOUBLE_JUMP = 80;

	private static final DataParameter<Boolean> ATTACKING = EntityDataManager.createKey(EntityChiromawTame.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> ELECTRIC = EntityDataManager.createKey(EntityChiromawTame.class, DataSerializers.BOOLEAN);

	public int doubleJumpTicks;
	public int prevWingFlapTicks, wingFlapTicks;
	public int prevRaiseWingTicks, raiseWingsTicks;

	public EntityChiromawTame(World world) {
		super(world);
		setSize(0.7F, 0.9F);
		moveHelper = new FlightMoveHelper(this);
	}

	@Override
	protected void initEntityAI() {
		tasks.addTask(0, new EntityAISwimming(this));
		this.aiSit = new EntityAISitBL(this);
		tasks.addTask(1, this.aiSit);
		tasks.addTask(2, new EntityChiromawTame.AIBarbAttack(this));
		tasks.addTask(3, new EntityChiromawTame.AIFollowOwner(this, 1.0D, 10.0F, 2.0F));
		tasks.addTask(4, new EntityAIAvoidEntity<>(this, EntityMob.class, 4.0F, 0.75D, 0.75D));
		tasks.addTask(5, new EntityAIAttackMelee(this, 0.5D, true));
		tasks.addTask(6, new EntityAIFlyingWander(this, 0.5D));
		tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		tasks.addTask(8, new EntityAILookIdle(this));
		targetTasks.addTask(1, new EntityChiromawTame.AIChiromawOwnerHurtByTarget(this));
		targetTasks.addTask(2, new EntityChiromawTame.AIChiromawOwnerHurtTarget(this));
		targetTasks.addTask(3, new EntityAIHurtByTarget(this, true, new Class[0]));
		targetTasks.addTask(4, new EntityAINearestAttackableTarget<>(this, EntityMob.class, true));
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(ATTACKING, false);
		dataManager.register(ELECTRIC, false);
	}

	@Override
	public String getName() {
		if (getElectricBoogaloo()) {
			return I18n.translateToLocal("entity.thebetweenlands.chiromaw_tame_lightning.name");
		}
		return super.getName();
	}

	@SideOnly(Side.CLIENT)
	public boolean isAttacking() {
		return dataManager.get(ATTACKING);
	}

	public void setAttacking(boolean attacking) {
		dataManager.set(ATTACKING, attacking);
	}

	public void setElectricBoogaloo(boolean electric) {
		dataManager.set(ELECTRIC, electric);
	}

    public boolean getElectricBoogaloo() {
        return dataManager.get(ELECTRIC);
    }

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setBoolean("Electric", getElectricBoogaloo());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		setElectricBoogaloo(nbt.getBoolean("Electric"));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(50.0D);
		getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2D);
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.FLYING_FIEND_LIVING;
	}

	@Nullable
	@Override
	protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
		return SoundRegistry.FLYING_FIEND_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.FLYING_FIEND_DEATH;
	}

	@Override
	public void onUpdate() {
		if(this.aiSit != null) {
			this.aiSit.setSitting(this.isSitting());
		}
		
		super.onUpdate();

		Entity riding = this.getRidingEntity();

		if(this.doubleJumpTicks > 1) {
			this.doubleJumpTicks--;
		}
		
		this.prevWingFlapTicks = this.wingFlapTicks;
		if(this.wingFlapTicks > 1) {
			this.wingFlapTicks--;
		}
		
		if(riding == null || riding.onGround) {
			this.doubleJumpTicks = 0;
		}
		
		if (riding == null) {
			if (isJumping && isInWater()) {
				getMoveHelper().setMoveTo(posX, posY + 1, posZ, 1.0D);
				this.setSitting(false);
			}

			if (isSitting()) {
				motionX = motionY = motionZ = 0.0D;
				if (!getEntityWorld().isRemote) {
					IBlockState state = this.world.getBlockState(this.getPosition().up());
					BlockFaceShape shape = state.getBlockFaceShape(this.world, this.getPosition().up(), EnumFacing.DOWN);
					
					if(shape != BlockFaceShape.SOLID && shape != BlockFaceShape.MIDDLE_POLE_THICK) {
						this.setSitting(false);
					}
				}
			} else if (getEntityWorld().getBlockState(getPosition().down()).isSideSolid(getEntityWorld(), getPosition().down(), EnumFacing.UP)) {
				getMoveHelper().setMoveTo(posX, posY + 1, posZ, 1.0D);
			}

			if (motionY < 0.0D && getAttackTarget() == null) {
				motionY *= 0.25D;
			}
			
			this.prevRaiseWingTicks = this.raiseWingsTicks;
			if(this.raiseWingsTicks > 0) {
				this.raiseWingsTicks--;
			}
		} else {
			this.prevRaiseWingTicks = this.raiseWingsTicks;

			if(!riding.onGround && this.raiseWingsTicks < 3) {
				this.raiseWingsTicks++;
			} else if(riding.onGround && this.raiseWingsTicks > 0) {
				this.raiseWingsTicks--;
				
				if(this.raiseWingsTicks == 0) {
					this.wingFlapTicks = 0;
				}
			}
		}
	}

	@Override
	public boolean shouldAttackEntity(EntityLivingBase entityTarget, EntityLivingBase entityTarget2) {
		if(this.isSitting()) {
			return false;
		}
		if (!(entityTarget instanceof EntityCreeper) && !(entityTarget instanceof EntityGhast)) {
			if (entityTarget instanceof EntityChiromawTame) {
				EntityChiromawTame chiromawIn = (EntityChiromawTame) entityTarget;

				if (chiromawIn.getOwner() == entityTarget2) {
					return false;
				}
			}
			return entityTarget instanceof EntityPlayer && entityTarget2 instanceof EntityPlayer && !((EntityPlayer) entityTarget2).canAttackPlayer((EntityPlayer) entityTarget) ? false : !(entityTarget instanceof EntityHorse) || !((EntityHorse) entityTarget).isTame();
		} else {
			return false;
		}
	}

	@Override
	public boolean attackEntityAsMob(Entity entity) { 
		boolean hasHit = entity.attackEntityFrom(DamageSource.causeMobDamage(this), (float) ((int) getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));
		if (hasHit)
			applyEnchantments(this, entity);
		return hasHit;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if (this.isRiding() && (source.equals(DamageSource.IN_WALL) || source.equals(DamageSource.DROWN)))
			return false;
		if (source instanceof EntityDamageSourceIndirect) {
			if (source.getTrueSource() == this)
				return false;
			if (source.getImmediateSource() instanceof EntityBLArrow)
				if (((EntityBLArrow) source.getImmediateSource()).getArrowType() == EnumArrowType.CHIROMAW_BARB)
					return false;
		}
		if(super.attackEntityFrom(source, damage)) {
			if(this.isSitting()) {
				this.setSitting(false);
			}
			return true;
		}
		return false;
	}

	@Override
	protected PathNavigate createNavigator(World world) {
		return new PathNavigateFlyingBL(this, world, 2);
	}

	@Override
	public void fall(float distance, float damageMultiplier) {
	}

	@Override
	protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos) {
	}

	@Override
	public void travel(float strafe, float vertical, float forward) {
		//Don't wander around when sitting
		if(this.isSitting() && !this.world.isRemote) {
			return;
		}
		
		if (isInWater()) {
			moveRelative(strafe, vertical, forward, 0.02F);
			move(MoverType.SELF, motionX, motionY, motionZ);
			motionX *= 0.800000011920929D;
			motionY *= 0.800000011920929D;
			motionZ *= 0.800000011920929D;
		} else if (isInLava()) {
			moveRelative(strafe, vertical, forward, 0.02F);
			move(MoverType.SELF, motionX, motionY, motionZ);
			motionX *= 0.5D;
			motionY *= 0.5D;
			motionZ *= 0.5D;
		} else {
			float f = 0.91F;

			if (onGround) {
				BlockPos underPos = new BlockPos(MathHelper.floor(posX), MathHelper.floor(getEntityBoundingBox().minY) - 1, MathHelper.floor(posZ));
				IBlockState underState = world.getBlockState(underPos);
				f = underState.getBlock().getSlipperiness(underState, world, underPos, this) * 0.91F;
			}

			float f1 = 0.16277136F / (f * f * f);
			moveRelative(strafe, vertical, forward, onGround ? 0.1F * f1 : 0.02F);
			f = 0.91F;

			if (onGround) {
				BlockPos underPos = new BlockPos(MathHelper.floor(posX), MathHelper.floor(getEntityBoundingBox().minY) - 1, MathHelper.floor(posZ));
				IBlockState underState = world.getBlockState(underPos);
				f = underState.getBlock().getSlipperiness(underState, world, underPos, this) * 0.91F;
			}

			move(MoverType.SELF, motionX, motionY, motionZ);
			motionX *= (double) f;
			motionY *= (double) f;
			motionZ *= (double) f;
		}

		prevLimbSwingAmount = limbSwingAmount;
		double d1 = posX - prevPosX;
		double d0 = posZ - prevPosZ;
		float f2 = MathHelper.sqrt(d1 * d1 + d0 * d0) * 4.0F;

		if (f2 > 1.0F) {
			f2 = 1.0F;
		}

		limbSwingAmount += (f2 - limbSwingAmount) * 0.4F;
		limbSwing += limbSwingAmount;
	}

	@Override
	public boolean isOnLadder() {
		return false;
	}

	@Override
	public void setSitting(boolean sitting) {
		super.setSitting(sitting);
		
		if(this.aiSit != null) {
			this.aiSit.setSitting(sitting);
		}
	}
	
	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		boolean holdsEquipment = hand == EnumHand.MAIN_HAND && !stack.isEmpty() && (stack.getItem() instanceof IEquippable || stack.getItem() == ItemRegistry.AMULET_SLOT);
		if (holdsEquipment)
			return true;
		if (!stack.isEmpty()) {
			if (stack.getItem() == ItemRegistry.SNAIL_FLESH_RAW) {
				if (getHealth() < getMaxHealth()) {
					if (!getEntityWorld().isRemote) {
						heal(5.0F);
						if (!player.capabilities.isCreativeMode) {
							stack.shrink(1);
							if (stack.getCount() <= 0)
								player.setHeldItem(hand, ItemStack.EMPTY);
						}
					} else {
						playTameEffect(true);
					}

					return true;
				}
			}
			if (stack.getItem() == ItemRegistry.NET)
				return stack.getItem().itemInteractionForEntity(stack, player, this, EnumHand.MAIN_HAND);
		}

		if (isOwner(player) && hand == EnumHand.MAIN_HAND && !(this instanceof IPullerEntity)) {
			rotationYaw = player.rotationYaw;
			
			if (!getEntityWorld().isRemote) {
				setAttackTarget((EntityLivingBase) null);

				Entity riding = getRidingEntity();

				if (riding == null) {
					if (!player.isBeingRidden()) { // stops multiple mounting you
						if(isSitting()) {
							this.setSitting(false);
						}
						getEntityWorld().playSound(null, getPosition(), SoundRegistry.CHIROMAW_MATRIARCH_LAND, SoundCategory.NEUTRAL, 0.25F, 1.5F);
						startRiding(player, true);
					}
				} else {
					boolean canUnmount = this.world.getCollisionBoxes(this, this.getEntityBoundingBox()).isEmpty();
					
					if(canUnmount) {
						getEntityWorld().playSound(null, getPosition(), SoundRegistry.CHIROMAW_MATRIARCH_RELEASE, SoundCategory.NEUTRAL, 0.5F, 1F);
						dismountRidingEntity();
					} else if(player instanceof EntityPlayerMP) {
						((EntityPlayerMP) player).sendStatusMessage(new TextComponentTranslation("chat.chiromaw_tame.obstructed"), true);
					}
					
					if(!this.isSitting()) {
						if(canUnmount) {
							List<BlockPos> sitPositions = new ArrayList<>();
							
							for(int yo = 2; yo <= 4; yo++) {
								for(int xo = -1; xo <= 1; xo++) {
									for(int zo = -1; zo <= 1; zo++) {
										BlockPos pos = new BlockPos(riding.getPosition().add(xo, yo, zo));
										
										if(this.world.isAirBlock(pos)) {
											IBlockState state = this.world.getBlockState(pos.up());
											BlockFaceShape shape = state.getBlockFaceShape(this.world, pos.up(), EnumFacing.DOWN);
											
											if(shape == BlockFaceShape.SOLID || shape == BlockFaceShape.MIDDLE_POLE_THICK) {
												sitPositions.add(pos);
											}
										}
									}
								}
							}
							
							if(!sitPositions.isEmpty()) {
								this.setSitting(true);
								this.isJumping = false;
								
								Collections.sort(sitPositions, Comparator.comparingDouble(pos -> this.getDistanceSq(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f)));
								
								BlockPos sitPosition = sitPositions.get(0);
								
								this.setPosition(sitPosition.getX() + 0.5f, sitPosition.getY() + 1 - this.height, sitPosition.getZ() + 0.5f);
								this.navigator.clearPath();
								this.motionX = this.motionY = this.motionZ = 0;
								this.velocityChanged = true;
								this.setMoveForward(0);
								this.setMoveStrafing(0);
								this.setMoveVertical(0);
							}
						}
					} else {
						this.setSitting(false);
					}
				}
			}
			return true;
		}

		return super.processInteract(player, hand);
	}

	@Override
	public boolean startRiding(Entity entity, boolean force) {
		boolean result = super.startRiding(entity, force);
		if(entity instanceof EntityPlayer && this.world instanceof WorldServer) {
			((WorldServer) this.world).getEntityTracker().sendToTracking(this, new SPacketSetPassengers(entity));
		}
		return result;
	}

	@Override
	public void dismountEntity(Entity entity) {
		super.dismountEntity(entity);
		if(entity instanceof EntityPlayer && this.world instanceof WorldServer) {
			((WorldServer) this.world).getEntityTracker().sendToTracking(this, new SPacketSetPassengers(entity));
		}
	}

	@Override
	public void updateRidden() {
		super.updateRidden();
	}

	@Override
	public double getYOffset() {
		if(getRidingEntity() !=null && getRidingEntity() instanceof EntityPlayer)
			return height * 0.5D;
		return 0.0D;
	}

	@Override
	public boolean canRiderInteract() {
		return true;
	}

	@Override
	public EntityAgeable createChild(EntityAgeable ageable) {
		return null;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void handleStatusUpdate(byte id) {
		super.handleStatusUpdate(id);

		if(id == EVENT_DOUBLE_JUMP) {
			this.prevWingFlapTicks = this.wingFlapTicks = this.doubleJumpTicks = 20;
		}
	}

	public void performDoubleJump(EntityPlayer player) {
		if(this.doubleJumpTicks == 0 && (!this.world.isRemote || !player.onGround)) {
			player.jump();
			player.fallDistance = -2;

			if(!this.world.isRemote) {
				this.prevWingFlapTicks = this.wingFlapTicks = this.doubleJumpTicks = 20;
				this.world.setEntityState(this, EVENT_DOUBLE_JUMP);
				this.world.playSound(null, this.posX, this.posY, this.posZ, SoundRegistry.CHIROMAW_MATRIARCH_FLAP, SoundCategory.NEUTRAL, 1, 1);
			} else {
				TheBetweenlands.networkWrapper.sendToServer(new MessageChiromawDoubleJump(this));
			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onInputUpdate(InputUpdateEvent event) {
		if(event.getMovementInput().jump && !event.getEntityPlayer().isJumping) {
			List<Entity> passengers = event.getEntityPlayer().getPassengers();
			for(Entity entity : passengers) {
				if(entity instanceof EntityChiromawTame) {
					((EntityChiromawTame) entity).performDoubleJump(event.getEntityPlayer());
					return;
				}
			}
		}
	}

	class AIFollowOwner extends EntityAIBase {
		private final EntityChiromawTame chiromaw;
		private EntityLivingBase owner;
		World world;
		private final double followSpeed;
		private final PathNavigate petPathfinder;
		private int timeToRecalcPath;
		float maxDist;
		float minDist;
		private float oldWaterCost;

		public AIFollowOwner(EntityChiromawTame chiromawIn, double followSpeedIn, float minDistIn, float maxDistIn) {
			chiromaw = chiromawIn;
			world = chiromawIn.getEntityWorld();
			followSpeed = followSpeedIn;
			petPathfinder = chiromawIn.getNavigator();
			minDist = minDistIn;
			maxDist = maxDistIn;
			setMutexBits(3);

			if (!(chiromawIn.getNavigator() instanceof PathNavigateFlyingBL)) {
				throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
			}
		}

		@Override
		public boolean shouldExecute() {
			if(chiromaw.isSitting()) {
				return false;
			}
			
			EntityLivingBase entitylivingbase = chiromaw.getOwner();

			if (entitylivingbase == null)
				return false;
			else if (entitylivingbase instanceof EntityPlayer && ((EntityPlayer) entitylivingbase).isSpectator())
				return false;
			else if (chiromaw.getDistanceSq(entitylivingbase) < (double) (minDist * minDist))
				return false;
			else {
				owner = entitylivingbase;
				return true;
			}
		}

		@Override
		public boolean shouldContinueExecuting() {
			return !petPathfinder.noPath() && chiromaw.getDistanceSq(owner) > (double) (maxDist * maxDist);
		}

		@Override
		public void startExecuting() {
			timeToRecalcPath = 0;
			oldWaterCost = chiromaw.getPathPriority(PathNodeType.WATER);
			chiromaw.setPathPriority(PathNodeType.WATER, 0.0F);
		}

		@Override
		public void resetTask() {
			owner = null;
			petPathfinder.clearPath();
			chiromaw.setPathPriority(PathNodeType.WATER, oldWaterCost);
		}

		private boolean isEmptyBlock(BlockPos pos) {
			IBlockState iblockstate = world.getBlockState(pos);
			return iblockstate.getMaterial() == Material.AIR ? true : !iblockstate.isFullCube();
		}

		@Override
		public void updateTask() {
			chiromaw.getLookHelper().setLookPositionWithEntity(owner, 10.0F, (float) chiromaw.getVerticalFaceSpeed());

			if (--timeToRecalcPath <= 0) {
				timeToRecalcPath = 10;

				if (!petPathfinder.tryMoveToEntityLiving(owner, followSpeed)) {
					if (!chiromaw.getLeashed()) {
						if (chiromaw.getDistanceSq(owner) >= 144.0D) {
							int i = MathHelper.floor(owner.posX) - 2;
							int j = MathHelper.floor(owner.posZ) - 2;
							int k = MathHelper.floor(owner.getEntityBoundingBox().minY);

							for (int l = 0; l <= 4; ++l) {
								for (int i1 = 0; i1 <= 4; ++i1) {
									if ((l < 1 || i1 < 1 || l > 3 || i1 > 3) && isTeleportFriendlyBlock(i, j, k, l, i1)) {
										chiromaw.setLocationAndAngles((double) ((float) (i + l) + 0.5F), (double) k, (double) ((float) (j + i1) + 0.5F), chiromaw.rotationYaw, chiromaw.rotationPitch);
										petPathfinder.clearPath();
										return;
									}
								}
							}
						}
					}
				}
			}
		}

		protected boolean isTeleportFriendlyBlock(int x, int z, int y, int xOffset, int zOffset) {
			BlockPos blockpos = new BlockPos(x + xOffset, y - 1, z + zOffset);
			IBlockState iblockstate = world.getBlockState(blockpos);
			return iblockstate.getBlockFaceShape(world, blockpos, EnumFacing.DOWN) == BlockFaceShape.SOLID && iblockstate.canEntitySpawn(chiromaw) && world.isAirBlock(blockpos.up()) && world.isAirBlock(blockpos.up(2));
		}
	}

	class AIChiromawOwnerHurtByTarget extends EntityAITarget {
		EntityChiromawTame chiromaw;
		EntityLivingBase attacker;
		private int timestamp;

		public AIChiromawOwnerHurtByTarget(EntityChiromawTame theDefendingChiromaw) {
			super(theDefendingChiromaw, false);
			chiromaw = theDefendingChiromaw;
			setMutexBits(1);
		}

		@Override
		public boolean shouldExecute() {
			EntityLivingBase entity = chiromaw.getOwner();
			if (entity == null)
				return false;
			else {
				attacker = entity.getRevengeTarget();
				int i = entity.getRevengeTimer();
				return i != timestamp && isSuitableTarget(attacker, false) && chiromaw.shouldAttackEntity(attacker, entity);
			}
		}

		@Override
		public void startExecuting() {
			taskOwner.setAttackTarget(attacker);
			EntityLivingBase entity = chiromaw.getOwner();
			if (entity != null)
				timestamp = entity.getRevengeTimer();
			super.startExecuting();
		}
	}

	class AIChiromawOwnerHurtTarget extends EntityAITarget {
		EntityChiromawTame chiromaw;
		EntityLivingBase target;
		private int timestamp;

		public AIChiromawOwnerHurtTarget(EntityChiromawTame chirowmawIn) {
			super(chirowmawIn, false);
			chiromaw = chirowmawIn;
			setMutexBits(1);
		}

		@Override
		public boolean shouldExecute() {
			EntityLivingBase entitylivingbase = chiromaw.getOwner();
			if (entitylivingbase == null)
				return false;
			else {
				target = entitylivingbase.getRevengeTarget();
				int i = entitylivingbase.getRevengeTimer();
				return i != timestamp && isSuitableTarget(target, false) && chiromaw.shouldAttackEntity(target, entitylivingbase);
			}
		}

		@Override
		public void startExecuting() {
			taskOwner.setAttackTarget(target);
			EntityLivingBase entitylivingbase = chiromaw.getOwner();
			if (entitylivingbase != null)
				timestamp = entitylivingbase.getRevengeTimer();
			super.startExecuting();
		}
	}

	class AIBarbAttack extends EntityAIBase {
		EntityChiromawTame chiromaw;
		public int attackTimer;

		public AIBarbAttack(EntityChiromawTame chirowmawIn) {
			chiromaw = chirowmawIn;
		}

		@Override
		public boolean shouldExecute() {
			return chiromaw.getAttackTarget() != null && !chiromaw.isSitting();
		}

		@Override
		public void startExecuting() {
			this.attackTimer = 0;
		}

		@Override
		public void resetTask() {
			chiromaw.setAttacking(false);
		}

		@Override
		public void updateTask() {
			EntityLivingBase entitylivingbase = chiromaw.getAttackTarget();
			if (!chiromaw.getEntityWorld().isRemote && entitylivingbase != null) {
				World world = chiromaw.getEntityWorld();
				if (entitylivingbase.getDistanceSq(chiromaw) < 576 && entitylivingbase.getDistanceSq(chiromaw) > 25 && chiromaw.canEntityBeSeen(entitylivingbase)) {
					++this.attackTimer;
					if (attackTimer == 20) {
						EntityBLArrow arrow = new EntityBLArrow(world, chiromaw);
						arrow.setType(chiromaw.getElectricBoogaloo()? EnumArrowType.CHIROMAW_SHOCK_BARB: EnumArrowType.CHIROMAW_BARB);
						double targetX = entitylivingbase.posX + entitylivingbase.motionX - chiromaw.posX;
						double targetY = entitylivingbase.posY + entitylivingbase.getEyeHeight() - chiromaw.posY + chiromaw.getEyeHeight();
						double targetZ = entitylivingbase.posZ + entitylivingbase.motionZ - chiromaw.posZ;
						arrow.shoot(targetX, targetY, targetZ, 1.2F, 0.0F);
						world.spawnEntity(arrow);
						chiromaw.getEntityWorld().playSound(null, chiromaw.getPosition(), SoundRegistry.CHIROMAW_MATRIARCH_BARB_FIRE, SoundCategory.NEUTRAL, 0.5F, 1F + (chiromaw.getEntityWorld().rand.nextFloat() - chiromaw.getEntityWorld().rand.nextFloat()) * 0.8F);
						attackTimer = -20;
					}

				} else if (this.attackTimer > 0) {
					--this.attackTimer;
				}
				chiromaw.setAttacking(this.attackTimer > 10);
			}
		}
	}

	@Override
	public NBTTagCompound returnToRing(UUID userId) {
		return this.writeToNBT(new NBTTagCompound());
	}

	@Override
	public boolean returnFromRing(Entity user, NBTTagCompound nbt) {
		double prevX = this.posX;
		double prevY = this.posY;
		double prevZ = this.posZ;
		float prevYaw = this.rotationYaw;
		float prevPitch = this.rotationPitch;
		this.readFromNBT(nbt);
		this.setLocationAndAngles(prevX, prevY, prevZ, prevYaw, prevPitch);
		if(!this.isEntityAlive()) {
			//Revivd by animator
			this.setHealth(this.getMaxHealth());
		}
		this.world.spawnEntity(this);
		return true;
	}

	@Override
	public boolean shouldReturnOnUnload(boolean isOwnerLoggedIn) {
		return IRingOfGatheringMinion.super.shouldReturnOnUnload(isOwnerLoggedIn) && !this.isSitting();
	}

	@Override
	public UUID getRingOwnerId() {
		return this.getOwnerId();
	}

	@Override
	public EntityPullerChiromaw createPuller(EntityDraeton draeton, DraetonPhysicsPart puller) {
		return new EntityPullerChiromaw(draeton.world, draeton, puller);
	}
}