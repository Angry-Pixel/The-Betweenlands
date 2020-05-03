package thebetweenlands.common.entity.mobs;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.base.Optional;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import thebetweenlands.api.entity.IBLBoss;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.api.entity.IEntityCameraOffset;
import thebetweenlands.api.entity.IEntityMusic;
import thebetweenlands.api.entity.IEntityScreenShake;
import thebetweenlands.client.audio.EntityMusicLayers;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.ai.IPathObstructionAwareEntity;
import thebetweenlands.common.entity.ai.ObstructionAwarePathNavigateGround;
import thebetweenlands.common.entity.projectiles.EntitySludgeBall;
import thebetweenlands.common.network.clientbound.MessageSummonPeatMummyParticles;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.sound.BLSoundEvent;

public class EntityDreadfulMummy extends EntityMob implements IEntityBL, IBLBoss, IEntityScreenShake, IEntityCameraOffset, IEntityMusic, IPathObstructionAwareEntity {

	private final BossInfoServer bossInfo = (BossInfoServer)(new BossInfoServer(this.getDisplayName(), BossInfo.Color.RED, BossInfo.Overlay.PROGRESS)).setDarkenSky(false);
	public static final IAttribute SPAWN_LENGTH_ATTRIB = (new RangedAttribute(null, "bl.spawnLength", 180.0D, 0.0D, Integer.MAX_VALUE)).setDescription("Spawning Length");
	public static final IAttribute SPAWN_OFFSET_ATTRIB = (new RangedAttribute(null, "bl.spawnOffset", 3.0D, -Integer.MAX_VALUE, Integer.MAX_VALUE)).setDescription("Spawning Y Offset");

	private static final DataParameter<Integer> SPAWNING_STATE_DW = EntityDataManager.createKey(EntityDreadfulMummy.class, DataSerializers.VARINT);
	private static final DataParameter<Boolean> SPEW = EntityDataManager.createKey(EntityDreadfulMummy.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> PREY = EntityDataManager.createKey(EntityDreadfulMummy.class, DataSerializers.VARINT);
	private static final DataParameter<Float> Y_OFFSET = EntityDataManager.createKey(EntityDreadfulMummy.class, DataSerializers.FLOAT);
	private static final DataParameter<Optional<UUID>> BOSSINFO_ID = EntityDataManager.createKey(EntityDreadfulMummy.class, DataSerializers.OPTIONAL_UNIQUE_ID);

	private int prevSpawningState;

	private int spewTicks = 0;

	private static final int BREAK_COUNT = 20;

	private static final int SPAWN_MUMMY_COOLDOWN = 350;
	private int untilSpawnMummy = 0;
	private static final int SPAWN_SLUDGE_COOLDOWN = 150;
	private int untilSpawnSludge = 0;
	private float prevYOffset;

	private int eatPreyTimer = 60;
	public EntityLivingBase currentEatPrey;

	public int deathTicks = 0;

	private int outOfRangeCounter = 0;
	private int outOfRangeCycleCounter = 0;
	private int obstructedCounter = 0;
	private boolean breakBlocksBelow = false;
	private int blockBreakCounter = 0;

	public EntityDreadfulMummy(World world) {
		super(world);
		this.isImmuneToFire = true;
		setSize(1.1F, 2.0F);

		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new EntityAIAttackMelee(this, 1.0D, false));
		tasks.addTask(2, new EntityAIMoveTowardsRestriction(this, 1.0D));
		tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 16.0F));
		tasks.addTask(4, new EntityAIWander(this, 1.0D));
		tasks.addTask(5, new EntityAILookIdle(this));
		targetTasks.addTask(0, new EntityAIHurtByTarget(this, true));
		targetTasks.addTask(1, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, false));
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(SPAWNING_STATE_DW, 0);
		dataManager.register(SPEW, false);
		dataManager.register(PREY, 0);
		dataManager.register(Y_OFFSET, 0F);
		this.getDataManager().register(BOSSINFO_ID, Optional.absent());
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(550.0D);
		getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(8);
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(40.0D);
		getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);

		getAttributeMap().registerAttribute(SPAWN_LENGTH_ATTRIB);
		getAttributeMap().registerAttribute(SPAWN_OFFSET_ATTRIB);
	}

	@Override
	protected PathNavigate createNavigator(World worldIn) {
		ObstructionAwarePathNavigateGround<EntityDreadfulMummy> navigate = new ObstructionAwarePathNavigateGround<>(this, worldIn);
		navigate.setCanSwim(true);
		return navigate;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.DREADFUL_PEAT_MUMMY_LIVING;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundRegistry.DREADFUL_PEAT_MUMMY_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.DREADFUL_PEAT_MUMMY_DEATH;
	}

	@Override
	public double getYOffset() {
		return dataManager.get(Y_OFFSET);
	}

	public void setYOffset(float yOffset) {
		dataManager.set(Y_OFFSET, yOffset);
	}

	public float getCurrentOffset() {
		return (float) ((-getSpawnOffset() + getSpawningProgress() * getSpawnOffset()));
	}

	public double getSpawnOffset() {
		return getEntityAttribute(SPAWN_OFFSET_ATTRIB).getAttributeValue();
	}

	public int getSpawningState() {
		return dataManager.get(SPAWNING_STATE_DW);
	}

	public int getSpawningLength() {
		return (int) getEntityAttribute(SPAWN_LENGTH_ATTRIB).getAttributeValue();
	}

	public float getSpawningProgress() {
		if(getSpawningLength() == 0) {
			return 1.0F;
		}
		return 1.0F / getSpawningLength() * getSpawningState();
	}

	public float getSpawningProgress(float delta) {
		if(getSpawningLength() == 0) {
			return 1.0F;
		}
		return 1.0F / getSpawningLength() * (this.prevSpawningState + (this.getSpawningState() - this.prevSpawningState) * delta);
	}

	/**
	 * Returns the interpolated relative spawning progress
	 * @param partialTicks
	 * @return
	 */
	public float getInterpolatedYOffsetProgress(float partialTicks) {
		return this.prevYOffset + (((float)this.getYOffset()) - this.prevYOffset) * partialTicks;
	}

	public void updateSpawningState() {
		int spawningState = getSpawningState();

		this.prevSpawningState = spawningState;

		if(this.dataManager.get(SPEW)) {
			int targetState = MathHelper.ceil(this.getSpawningLength() * 0.6f);

			if(spawningState > targetState) {
				dataManager.set(SPAWNING_STATE_DW, spawningState - 1);
			}
		} else if(spawningState < getSpawningLength()) {
			dataManager.set(SPAWNING_STATE_DW, spawningState + 1);
		}
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}

	@Override
	public boolean isPushedByWater() {
		return false;
	}

	@Override
	public void moveRelative(float strafe, float up, float forward, float friction) {
		//Can't use the SWIM_SPEED attribute for this because it also causes the mob to jump
		//way too high out of the water
		final float swimSpeedBoost = 10.0f;

		float f = strafe * strafe + up * up + forward * forward;
		if (f >= 1.0E-4F) {
			f = MathHelper.sqrt(f);
			if (f < 1.0F) f = 1.0F;
			f = friction / f;
			strafe = strafe * f;
			up = up * f;
			forward = forward * f;
			if(this.isInWater() || this.isInLava()) {
				strafe = strafe * (float)this.getEntityAttribute(SWIM_SPEED).getAttributeValue() * swimSpeedBoost;
				up = up * (float)this.getEntityAttribute(SWIM_SPEED).getAttributeValue();
				forward = forward * (float)this.getEntityAttribute(SWIM_SPEED).getAttributeValue() * swimSpeedBoost;
			}
			float f1 = MathHelper.sin(this.rotationYaw * 0.017453292F);
			float f2 = MathHelper.cos(this.rotationYaw * 0.017453292F);
			this.motionX += (double)(strafe * f2 - forward * f1);
			this.motionY += (double)up;
			this.motionZ += (double)(forward * f2 + strafe * f1);
		}
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if (!world.isRemote)
			dataManager.set(BOSSINFO_ID, Optional.of(bossInfo.getUniqueId()));
	}

	protected boolean isTargetOutOfAttackRange(EntityLivingBase target) {
		if(target.posY > this.getEntityBoundingBox().maxY - 0.25D) {
			Path path = this.getNavigator().getPath();

			//Check if there is a path to the target
			if(path != null) {
				PathPoint finalPoint = path.getFinalPathPoint();

				if(finalPoint != null) {
					PathNodeType type = this.getNavigator().getNodeProcessor().getPathNodeType(this.world, finalPoint.x, finalPoint.y - 1, finalPoint.z);
					boolean isEndpointInAir = type != PathNodeType.BLOCKED;
					return isEndpointInAir || finalPoint.y + this.height - 0.25D < target.posY;
				}
			}

			return true;
		}

		return this.getDistance(target) > 24;
	}

	protected boolean isTargetInCloseRange(EntityLivingBase target) {
		return !this.isTargetOutOfAttackRange(target) && target.getDistance(this) < 4;
	}

	protected boolean isTargetObstructed(EntityLivingBase target) {
		Path path = this.getNavigator().getPath();

		//Check if there is a path to the target
		if(path != null && path.getFinalPathPoint() != null) {
			PathPoint finalPoint = path.getFinalPathPoint();

			if(finalPoint != null && target.getDistance(finalPoint.x, finalPoint.y, finalPoint.z) < 0.9f) {
				PathNodeType type = this.getNavigator().getNodeProcessor().getPathNodeType(this.world, finalPoint.x, finalPoint.y, finalPoint.z);

				if(type == PathNodeType.OPEN || type == PathNodeType.WALKABLE) {
					return false;
				}
			}
		}

		MutableBlockPos checkPos = new MutableBlockPos();

		BlockPos center = new BlockPos(target);

		for(int yo = MathHelper.floor(target.height); yo < 3; yo++) {
			for(int xo = -1; xo <= 1; xo++) {
				for(int zo = -1; zo <= 1; zo++) {
					checkPos.setPos(center.getX() + xo, center.getY() + yo, center.getZ() + zo);

					if(!this.world.getCollisionBoxes(null, new AxisAlignedBB(checkPos)).isEmpty()) {
						return true;
					}
				}
			}
		}

		return false;
	}

	protected void placeBridge() {
		Path path = this.getNavigator().getPath();

		if(path != null && path.getCurrentPathIndex() < path.getCurrentPathLength()) {
			PathPoint nextPoint = path.getPathPointFromIndex(path.getCurrentPathIndex());

			if(nextPoint.y == MathHelper.floor(this.posY + 0.5f)) {
				BlockPos pos = new BlockPos(this);
				pos = pos.add(0, nextPoint.y - pos.getY() - 1, 0);

				if(this.getDistanceSq(pos) <= 2 && (this.world.isAirBlock(pos) || this.getNavigator().getNodeProcessor().getPathNodeType(this.world, pos.getX(), pos.getY(), pos.getZ()) != PathNodeType.BLOCKED)) {
					if(ForgeEventFactory.getMobGriefingEvent(this.world, this)) {

						int nx = -2;
						int nz = -2;

						if(path.getCurrentPathIndex() < path.getCurrentPathLength() - 1) {
							PathPoint secondNextPoint = path.getPathPointFromIndex(path.getCurrentPathIndex() + 1);
							nx = MathHelper.clamp(secondNextPoint.x - nextPoint.x, -1, 1);
							nz = MathHelper.clamp(secondNextPoint.z - nextPoint.z, -1, 1);
							if(nx == 0) nx = -2;
							if(nz == 0) nz = -2;
						}

						for(int xo = -1; xo <= 1; xo++) {
							for(int zo = -1; zo <= 1; zo++) {
								if(xo != nx && zo != nz) {
									BlockPos offsetPos = pos.add(xo, 0, zo);

									if((this.world.isAirBlock(offsetPos) || this.getNavigator().getNodeProcessor().getPathNodeType(this.world, offsetPos.getX(), offsetPos.getY(), offsetPos.getZ()) != PathNodeType.BLOCKED)) {

										boolean canReplace = false;

										if(this.world.isAirBlock(offsetPos)) {
											canReplace = true;
										} else {
											IBlockState hitState = this.world.getBlockState(offsetPos);
											float hardness = hitState.getBlockHardness(this.world, offsetPos);

											if(hardness >= 0
													&& hitState.getBlock().canEntityDestroy(hitState, this.world, offsetPos, this)
													&& ForgeEventFactory.onEntityDestroyBlock(this, offsetPos, hitState)) {

												canReplace = true;
												this.world.playEvent(2001, offsetPos, Block.getStateId(hitState));
												this.world.setBlockToAir(offsetPos);
											}
										}

										if(canReplace) {
											this.world.setBlockState(offsetPos, BlockRegistry.SLUDGY_DIRT.getDefaultState());
										}

									}
								}
							}
						}

					}
				}
			}
		}
	}

	@Override
	public float getPathingMalus(EntityLiving entity, PathNodeType nodeType, BlockPos pos) {
		if(nodeType == PathNodeType.BLOCKED) {
			return 10.0f;
		}
		return super.getPathPriority(nodeType);
	}

	@Override
	public void onPathingObstructed(EnumFacing facing) {
		if(this.getAttackTarget() != null) {
			this.breakBlocksBelow = facing == EnumFacing.DOWN;
			this.blockBreakCounter = 40;
		}
	}

	@Override
	public int getMaxFallHeight() {
		//Doesn't take fall damage
		return 128;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		this.prevYOffset = (float) getYOffset();

		if((getPrey() != null && this.dataManager.get(SPEW)) || !this.isEntityAlive()) {
			setPrey(null);
		}

		Entity prey = getPrey();
		if(prey instanceof EntityLivingBase) {
			currentEatPrey = (EntityLivingBase)prey;
			if(currentEatPrey != null) {
				updateEatPrey();
			}
		} else {
			currentEatPrey = null;
		}

		updateSpawningState();

		if(getEntityWorld().isRemote) {
			if(getSpawningProgress() < 1.0F) {
				setYOffset(getCurrentOffset());
				motionX = 0;
				motionY = 0;
				motionZ = 0;
				if(getSpawningState() == getSpawningLength() - 1) {
					setPosition(posX, posY, posZ);
				}
				int breakPoint = getSpawningLength() / BREAK_COUNT;
				if ((getSpawningState() - breakPoint / 2 - 1) % breakPoint == 0) {
					BlockPos pos = new BlockPos(this.posX, this.posY - 1, this.posZ);
					IBlockState state = getEntityWorld().getBlockState(pos);
					double px = posX + rand.nextDouble() - 0.5F;
					double py = posY + rand.nextDouble() * 0.2 + 0.075;
					double pz = posZ + rand.nextDouble() - 0.5F;
					for (int i = 0, amount = rand.nextInt(20) + 15; i < amount; i++) {
						double ox = rand.nextDouble() * 0.1F - 0.05F;
						double oz = rand.nextDouble() * 0.1F - 0.05F;
						double motionX = rand.nextDouble() * 0.2 - 0.1;
						double motionY = rand.nextDouble() * 0.25 + 0.1;
						double motionZ = rand.nextDouble() * 0.2 - 0.1;
						getEntityWorld().spawnParticle(EnumParticleTypes.BLOCK_DUST, px + ox, py, pz + oz, motionX, motionY, motionZ, Block.getStateId(state));
					}
				}
			} else if(this.deathTicks == 0) {
				setYOffset(0F);
			} else if(this.deathTicks > 60) {
				motionX = 0;
				motionY = 0;
				motionZ = 0;
				if (this.deathTicks % 5 == 0) {
					BlockPos pos = new BlockPos(this.posX, this.posY - 1, this.posZ);
					IBlockState state = getEntityWorld().getBlockState(pos);
					double px = posX + rand.nextDouble() - 0.5F;
					double py = posY + rand.nextDouble() * 0.2 + 0.075;
					double pz = posZ + rand.nextDouble() - 0.5F;
					for (int i = 0, amount = rand.nextInt(20) + 15; i < amount; i++) {
						double ox = rand.nextDouble() * 0.1F - 0.05F;
						double oz = rand.nextDouble() * 0.1F - 0.05F;
						double motionX = rand.nextDouble() * 0.2 - 0.1;
						double motionY = rand.nextDouble() * 0.25 + 0.1;
						double motionZ = rand.nextDouble() * 0.2 - 0.1;
						getEntityWorld().spawnParticle(EnumParticleTypes.BLOCK_DUST, px + ox, py, pz + oz, motionX, motionY, motionZ, Block.getStateId(state));
					}
				}
			}
		} else {
			EntityLivingBase target = this.getAttackTarget();

			if(target != null && (this.blockBreakCounter == 0 || !this.breakBlocksBelow)) {
				this.placeBridge();
			}

			float targetMotionX = 0.0f;
			float targetMotionY = 0.0f;
			float targetMotionZ = 0.0f;

			if(target instanceof EntityPlayer) {
				NBTTagCompound nbt = target.getEntityData();

				targetMotionX = (float)target.posX - nbt.getFloat("thebetweenlands.dpm.lastX");
				targetMotionY = (float)target.posY - nbt.getFloat("thebetweenlands.dpm.lastY");
				targetMotionZ = (float)target.posZ - nbt.getFloat("thebetweenlands.dpm.lastZ");

				nbt.setFloat("thebetweenlands.dpm.lastX", (float)target.posX);
				nbt.setFloat("thebetweenlands.dpm.lastY", (float)target.posY);
				nbt.setFloat("thebetweenlands.dpm.lastZ", (float)target.posZ);
			} else if(target != null) {
				targetMotionX = (float)target.motionX;
				targetMotionY = (float)target.motionY;
				targetMotionZ = (float)target.motionZ;
			}

			if(target != null && this.isTargetOutOfAttackRange(target)) {
				if(this.obstructedCounter > 0 && this.dataManager.get(SPEW)) {
					this.outOfRangeCycleCounter++;

					//Make DPM cycle between ranged and melee if both obstructed and out of range
					if(this.outOfRangeCycleCounter > 200) {
						this.outOfRangeCycleCounter = 0;
						this.outOfRangeCounter = 0;
						this.dataManager.set(SPEW, false);
					}
				} else {
					this.outOfRangeCounter += 2;
				}
			} else {
				if(target != null && this.isTargetInCloseRange(target)) {
					this.outOfRangeCounter = Math.max(0, this.outOfRangeCounter - 2);
				} else {
					this.outOfRangeCounter = Math.max(0, this.outOfRangeCounter - 1);
				}
			}

			if(this.ticksExisted % 10 == 0) {
				if(target != null && this.isTargetObstructed(target)) {
					this.obstructedCounter += 2;
				} else {
					this.obstructedCounter = Math.max(0, this.obstructedCounter - 1);
				}
			}

			if(this.obstructedCounter <= 0) {
				this.outOfRangeCycleCounter = Math.max(0, this.outOfRangeCycleCounter - 1);
			}

			if(this.obstructedCounter > 10) {
				this.obstructedCounter = 0;

				if(target != null) {
					BlockPos pos = new BlockPos(target);

					if(!this.world.isAirBlock(pos.down()) && this.world.getEntitiesWithinAABB(EntityMummyArm.class, new AxisAlignedBB(pos)).isEmpty()) {
						EntityMummyArm arm = new EntityMummyArm(this.world);
						arm.setLocationAndAngles(pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f, 0, 0);

						this.world.spawnEntity(arm);
					}

					this.breakBlocksBelow = false;
					this.blockBreakCounter = 40;
				}
			}

			if(this.blockBreakCounter > 0) {
				this.blockBreakCounter--;

				if(ForgeEventFactory.getMobGriefingEvent(this.world, this)) {
					boolean broken = false;

					for(int xo = -2; xo <= 2; xo++) {
						for(int yo = (this.breakBlocksBelow ? -2 : 0); yo <= 2; yo++) {
							for(int zo = -2; zo <= 2; zo++) {
								if(this.world.rand.nextInt(6) == 0) {
									Vec3d center = new Vec3d(this.posX + xo, this.posY + yo, this.posZ + zo);

									if(center.subtract(new Vec3d(this.posX, this.posY, this.posZ)).dotProduct(this.breakBlocksBelow ? new Vec3d(0, -1, 0) : this.getLookVec()) > 0.3) {
										BlockPos pos = new BlockPos(center);

										IBlockState hitState = this.world.getBlockState(pos);
										float hardness = hitState.getBlockHardness(this.world, pos);

										if(!hitState.getBlock().isAir(hitState, this.world, pos) && hardness >= 0
												&& hitState.getBlock().canEntityDestroy(hitState, this.world, pos, this)
												&& ForgeEventFactory.onEntityDestroyBlock(this, pos, hitState)) {
											this.world.playEvent(2001, pos, Block.getStateId(hitState));
											this.world.setBlockToAir(pos);
											broken = true;
										}
									}
								}
							}
						}
					}

					if(broken) {
						this.playSound(SoundRegistry.DREADFUL_PEAT_MUMMY_SWIPE, 1, 1);
						this.playSound(SoundRegistry.DREADFUL_PEAT_MUMMY_BITE, 1, 1);
					}
				}
			}

			if(this.outOfRangeCounter > 300) {
				this.outOfRangeCounter = 300;
				this.dataManager.set(SPEW, true);
			} else if(this.outOfRangeCounter <= 0) {
				this.dataManager.set(SPEW, false);
			}

			if(this.dataManager.get(SPEW)) {
				this.spewTicks++;

				if(this.spewTicks > 60 && (this.spewTicks / 5) % 5 == 0) {
					if(target != null) {
						int numBalls = 1 + this.rand.nextInt(3);
						for(int i = 1; i < numBalls; i++) {
							float g = -0.03f;

							float vy0 = 0.5f + this.rand.nextFloat() * 1.3f;

							float tmax = -vy0 / g;

							float s = vy0 * tmax + 0.5f * g * tmax * tmax;

							float h = (float)(target.posY - this.posY);

							float fall = h - s;

							if(fall < 0) {
								float tmin = MathHelper.sqrt(fall * 2 / g);

								float t = tmax + tmin;

								float dx = (float)(target.posX - this.posX) + targetMotionX * t / 3 + (this.rand.nextFloat() - 0.5f) * 2;
								float dz = (float)(target.posZ - this.posZ) + targetMotionZ * t / 3 + (this.rand.nextFloat() - 0.5f) * 2;

								float len = MathHelper.sqrt(dx*dx + dz*dz);

								dx /= len;
								dz /= len;

								float speed = Math.min(len / t * 3.0f /*constant adjustment for MC physics*/, 1.5f);

								this.spawnRangedSludge(dx * speed, vy0, dz * speed);
							}
						}

						if(this.spewTicks % 5 == 0) {
							this.world.playSound(null, getPosition(), SoundRegistry.DREADFUL_PEAT_MUMMY_RETCH, SoundCategory.HOSTILE, 1F, 0.7F + rand.nextFloat() * 0.6F);
							this.world.playSound(null, getPosition(), SoundRegistry.DREADFUL_PEAT_MUMMY_LICK, SoundCategory.HOSTILE, 1F, 1F);
						}
					}
				}
			} else {
				this.spewTicks = 0;
			}

			if(deathTicks > 60) {
				setYOffset(-(deathTicks - 60) * 0.05F);
			}

			if(getSpawningProgress() < 1.0F) {
				if(getSpawningState() == 0) {
					getEntityWorld().playSound(null, getPosition(), SoundRegistry.DREADFUL_PEAT_MUMMY_EMERGE, SoundCategory.HOSTILE, 1.2F, 1.0F);
				}

				setYOffset(getCurrentOffset());
				motionY = 0;
				motionX = 0;
				motionZ = 0;
				velocityChanged = true;

				int breakPoint = getSpawningLength() / BREAK_COUNT;
				if ((getSpawningState() - breakPoint / 2 - 1) % breakPoint == 0) {
					BlockPos pos = new BlockPos(this.posX, this.posY - 1, this.posZ);
					IBlockState blockState = this.world.getBlockState(pos);
					playSound(blockState.getBlock().getSoundType(blockState, this.world, pos, this).getBreakSound(), this.rand.nextFloat() * 0.3F + 0.3F, this.rand.nextFloat() * 0.15F + 0.7F);
				}

				if(getAttackTarget() != null) faceEntity(getAttackTarget(), 360, 360);
				if(getSpawningState() == getSpawningLength() - 1) {
					setPosition(posX, posY, posZ);
				}
			} else if(deathTicks < 60) {
				setYOffset(0);
				prevYOffset = 0;
			}
		}

		if(!getEntityWorld().isRemote && isEntityAlive() && this.getSpawningProgress() >= 1) {
			//Heal when no player is nearby
			if(this.ticksExisted % 12 == 0 && this.getHealth() < this.getMaxHealth() && this.world.getClosestPlayerToEntity(this, 32) == null) {
				this.heal(1);
			}

			if (getAttackTarget() != null) {
				AxisAlignedBB checkAABB = getEntityBoundingBox().expand(64, 64, 64);
				List<EntityPeatMummy> peatMummies = getEntityWorld().getEntitiesWithinAABB(EntityPeatMummy.class, checkAABB);
				int mummies = 0;
				for(EntityPeatMummy mummy : peatMummies) {
					if(mummy.getDistance(this) <= 64.0D && mummy.isSpawningFinished())
						mummies++;
				}
				//Max. 4 peat mummies
				if(mummies < 4 && untilSpawnMummy <= 0)
					spawnMummy();

				if(untilSpawnSludge <= 0)
					spawnSludge();
			}

			if(untilSpawnMummy > 0)
				untilSpawnMummy--;

			if(untilSpawnSludge > 0)
				untilSpawnSludge--;

			if(eatPreyTimer > 0 && currentEatPrey != null)
				eatPreyTimer--;

			if(eatPreyTimer <= 0) {
				setPrey(null);
				eatPreyTimer = 60;
			}
		}
	}

	@Override
	protected void updateAITasks() {
		super.updateAITasks();
		bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
	}

	private void spawnMummy() {
		EntityPeatMummy mummy = new EntityPeatMummy(getEntityWorld());
		mummy.setPosition(posX + (rand.nextInt(6) - 3), posY, posZ + (rand.nextInt(6) - 3));
		if(mummy.getEntityWorld().checkNoEntityCollision(mummy.getEntityBoundingBox()) && mummy.getEntityWorld().getCollisionBoxes(mummy, mummy.getEntityBoundingBox()).isEmpty()) {
			untilSpawnMummy = SPAWN_MUMMY_COOLDOWN;
			mummy.setAttackTarget((EntityLivingBase) getAttackTarget());
			mummy.setHealth(30);
			getEntityWorld().spawnEntity(mummy);
			mummy.setCarryShimmerstone(false);
			mummy.setBossMummy(true);
			getEntityWorld().playSound(null, getPosition(), SoundRegistry.DREADFUL_PEAT_MUMMY_SCREAM, SoundCategory.HOSTILE, 1F, 1.0F);
			TheBetweenlands.networkWrapper.sendToAllAround(new MessageSummonPeatMummyParticles(mummy), new TargetPoint(this.dimension, mummy.posX, mummy.posY, mummy.posZ, 64));
		} else {
			//Try again the next tick
			untilSpawnMummy = 1;
		}
	}

	private void spawnSludge() {
		untilSpawnSludge = SPAWN_SLUDGE_COOLDOWN;
		if(getAttackTarget() != null)
			faceEntity(getAttackTarget(), 360.0F, 360.0F);
		Vec3d look = getLookVec();
		double direction = Math.toRadians(renderYawOffset);
		EntitySludgeBall sludge = new EntitySludgeBall(getEntityWorld(), this, false);
		sludge.setPosition(posX - Math.sin(direction) * 3.5, posY + height, posZ + Math.cos(direction) * 3.5);
		sludge.motionX = look.x * 0.5D;
		sludge.motionY = look.y;
		sludge.motionZ = look.z * 0.5D;
		getEntityWorld().playSound(null, getPosition(), SoundRegistry.DREADFUL_PEAT_MUMMY_RETCH, SoundCategory.HOSTILE, 1F, 0.7F + rand.nextFloat() * 0.6F);
		getEntityWorld().spawnEntity(sludge);
	}

	private void spawnRangedSludge(float dx, float dy, float dz) {
		double direction = Math.toRadians(renderYawOffset);
		EntitySludgeBall sludge = new EntitySludgeBall(getEntityWorld(), this, true);
		sludge.setPosition(posX - Math.sin(direction) * 0.75, posY + 0.3f, posZ + Math.cos(direction) * 0.75);
		sludge.motionX = dx;
		sludge.motionY = dy;
		sludge.motionZ = dz;
		getEntityWorld().spawnEntity(sludge);
	}

	@Override
	public boolean attackEntityAsMob(Entity target) {
		if((getSpawningProgress() < 0.95F && !this.dataManager.get(SPEW)) || this.getHealth() <= 0.0F) {
			return false;
		}

		boolean attacked = super.attackEntityAsMob(target);
		if (attacked && this.isEntityAlive() && rand.nextInt(6) == 0 && target != currentEatPrey && target instanceof EntityLivingBase && !(target instanceof EntityPlayer && ((EntityPlayer)target).capabilities.isCreativeMode) && !getEntityWorld().isRemote && !this.dataManager.get(SPEW)) {
			setPrey((EntityLivingBase)target);
		}

		if(attacked) {
			getEntityWorld().playSound(null, getPosition(), SoundRegistry.DREADFUL_PEAT_MUMMY_SWIPE, SoundCategory.HOSTILE, 1F, 0.7F + rand.nextFloat() * 0.6F);
		}

		return attacked;
	}

	private void updateEatPrey() {
		double direction = Math.toRadians(renderYawOffset);
		currentEatPrey.setPositionAndRotation(posX - Math.sin(direction) * 1.7, posY + 0.25, posZ + Math.cos(direction) * 1.7, (float) (Math.toDegrees(direction) + 180), 0);
		currentEatPrey.rotationYawHead = currentEatPrey.prevRotationYawHead = currentEatPrey.rotationYaw = currentEatPrey.prevRotationYaw = ((float) (Math.toDegrees(direction) + 180));
		currentEatPrey.fallDistance = 0;
		if (ticksExisted % 10 == 0) {
			if(!getEntityWorld().isRemote) {
				currentEatPrey.attackEntityFrom(DamageSource.causeMobDamage(this), 3);
				getEntityWorld().playSound(null, getPosition(), SoundRegistry.DREADFUL_PEAT_MUMMY_BITE, SoundCategory.HOSTILE, 1F, 0.7F + rand.nextFloat() * 0.6F);
				getEntityWorld().playSound(null, getPosition(), SoundRegistry.DREADFUL_PEAT_MUMMY_LICK, SoundCategory.HOSTILE, 1F, 1F);
			}
		}
		if (!currentEatPrey.isEntityAlive() && !getEntityWorld().isRemote) 
			setPrey(null);
	}

	private void setPrey(EntityLivingBase prey) {
		if (prey == null) {
			dataManager.set(PREY, -1);
		} else {
			dataManager.set(PREY, prey.getEntityId());
		}
	}

	private EntityLivingBase getPrey() {
		int id = dataManager.get(PREY);
		Entity prey = id != -1 ? getEntityWorld().getEntityByID(id) : null;
		if(prey instanceof EntityLivingBase)
			return (EntityLivingBase) prey;
		return null;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if(source == DamageSource.OUT_OF_WORLD) {
			return super.attackEntityFrom(source, damage);
		}

		if(source == DamageSource.FALL || source == DamageSource.FALLING_BLOCK || source == DamageSource.IN_WALL || source == DamageSource.CRAMMING
				|| source == DamageSource.DROWN || source == DamageSource.HOT_FLOOR || source == DamageSource.IN_FIRE || source == DamageSource.ON_FIRE) {
			return false;
		}

		if (currentEatPrey != null && source.getTrueSource() == currentEatPrey)  {
			return false;
		}

		if(getSpawningProgress() < 0.95F && !this.dataManager.get(SPEW)) {
			return false;
		}

		if(super.attackEntityFrom(source, this.dataManager.get(SPEW) ? damage * 0.25f : damage)) {
			if(!this.world.isRemote && source instanceof EntityDamageSource) {
				Entity sourceEntity = ((EntityDamageSource) source).getTrueSource();

				if(sourceEntity instanceof EntityLivingBase) {
					if(this.isTargetOutOfAttackRange((EntityLivingBase) sourceEntity)) {
						this.outOfRangeCounter += 60;
					} else if(this.isTargetInCloseRange((EntityLivingBase) sourceEntity)) {
						this.outOfRangeCounter = Math.max(0, this.outOfRangeCounter - 40);
					}
				}
			}

			return true;
		}

		return false;
	}

	@Override
	public boolean isNonBoss() {
		return false;
	}

	@Override
	protected void onDeathUpdate() {
		bossInfo.setPercent(0);
		if(deathTicks == 0) {
			if(!getEntityWorld().isRemote) {
				getEntityWorld().playSound(null, getPosition(), SoundRegistry.DREADFUL_PEAT_MUMMY_DEATH, SoundCategory.HOSTILE, 1F, 1F);
			}
		}

		++deathTicks;

		if(!getEntityWorld().isRemote) {
			posX = lastTickPosX;
			posY = lastTickPosY;
			posZ = lastTickPosZ;
			motionX = 0;
			motionY = 0;
			motionZ = 0;

			if (deathTicks > 40 && deathTicks % 5 == 0) {
				int xp = 100;
				while (xp > 0) {
					int dropXP = EntityXPOrb.getXPSplit(xp);
					xp -= dropXP;
					getEntityWorld().spawnEntity(new EntityXPOrb(getEntityWorld(), posX, posY + height / 2.0D, posZ, dropXP));
				}
			}

			if(deathTicks == 80) {
				int xp = 1200;
				while (xp > 0) {
					int dropXP = EntityXPOrb.getXPSplit(xp);
					xp -= dropXP;
					getEntityWorld().spawnEntity(new EntityXPOrb(getEntityWorld(), posX, posY + height / 2.0D, posZ, dropXP));
				}
			}

			if(deathTicks > 120) {
				setDead();
			}
		}

		if(deathTicks > 80) {
			if(getEntityWorld().isRemote && deathTicks % 5 == 0) {
				for(int xo = -1; xo <= 1; xo++) {
					for(int zo = -1; zo <= 1; zo++) {
						int x = MathHelper.floor(posX) + xo, y = MathHelper.floor(posY - getYOffset() - 0.1D), z = MathHelper.floor(posZ) + zo;
						IBlockState state = getEntityWorld().getBlockState(new BlockPos(x, y, z));
						Block block = state.getBlock();
						if(block != Blocks.AIR) {
							double px = posX + rand.nextDouble() - 0.5F;
							double py = posY - getYOffset() + rand.nextDouble() * 0.2 + 0.075;
							double pz = posZ + rand.nextDouble() - 0.5F;
							getEntityWorld().playSound(null, getPosition(), block.getSoundType(state, getEntityWorld(), getPosition().down(), null).getBreakSound(), SoundCategory.BLOCKS, rand.nextFloat() * 0.3F + 0.3F, rand.nextFloat() * 0.15F + 0.7F);
							for (int i = 0, amount = rand.nextInt(20) + 10; i < amount; i++) {
								double ox = rand.nextDouble() * 0.1F - 0.05F;
								double oz = rand.nextDouble() * 0.1F - 0.05F;
								double motionX = rand.nextDouble() * 0.2 - 0.1;
								double motionY = rand.nextDouble() * 0.25 + 0.1;
								double motionZ = rand.nextDouble() * 0.2 - 0.1;
								getEntityWorld().spawnParticle(EnumParticleTypes.BLOCK_DUST, px + ox + xo, py, pz + oz + zo, motionX, motionY, motionZ, Block.getStateId(state));
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void onCollideWithPlayer(EntityPlayer player) {
		if(isEntityAlive()) {
			super.onCollideWithPlayer(player);
		}
	}

	@Override
	public boolean canBePushed() {
		return isEntityAlive() && this.getSpawningProgress() >= 1 && super.canBePushed();
	}

	@Override
	public boolean canBeCollidedWith() {
		return isEntityAlive();
	}

	@Override
	protected boolean isMovementBlocked() {
		return isEntityAlive() && super.isMovementBlocked();
	}

	@Override
	public float getShakeIntensity(Entity viewer, float partialTicks) {
		if(deathTicks > 0) {
			double dist = getDistance(viewer);
			float screamMult = (float) (1.0F - dist / 30.0F);
			if(dist >= 30.0F) {
				return 0.0F;
			}
			return (float) ((Math.sin(deathTicks / 120.0D * Math.PI) + 0.1F) * 0.15F * screamMult);
		} else {
			return 0.0F;
		}
	}

	@Override
	public boolean applyOffset(Entity view, float partialTicks) {
		if(currentEatPrey == view) {
			double direction = Math.toRadians(prevRenderYawOffset + (renderYawOffset - prevRenderYawOffset) * partialTicks);
			view.prevRotationYaw = view.rotationYaw = (float) (Math.toDegrees(direction) + 180);
			view.prevRotationPitch = view.rotationPitch = 0;
			view.setRotationYawHead((float) (Math.toDegrees(direction) + 180));
			return true;
		}
		return false;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setInteger("spawningState", getSpawningState());
		nbt.setInteger("deathTicks", deathTicks);
		nbt.setDouble("initialPosY", posY);
		nbt.setFloat("previousYOffset", prevYOffset);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		deathTicks = nbt.getInteger("deathTicks");
		posY = nbt.getDouble("initialPosY");
		setYOffset(nbt.getFloat("previousYOffset"));
		dataManager.set(SPAWNING_STATE_DW, nbt.getInteger("spawningState"));
		if(hasCustomName())
			bossInfo.setName(this.getDisplayName());
	}

	@Override
	public void setCustomNameTag(String name) {
		super.setCustomNameTag(name);
		bossInfo.setName(this.getDisplayName());
	}

	@Override
	public void addTrackingPlayer(EntityPlayerMP player) {
		super.addTrackingPlayer(player);
		bossInfo.addPlayer(player);
	}

	@Override
	public void removeTrackingPlayer(EntityPlayerMP player) {
		super.removeTrackingPlayer(player);
		bossInfo.removePlayer(player);
	}

	@Nullable
	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.DREADFUL_PEAT_MUMMY;
	}

	@Override
	public float getBlockPathWeight(BlockPos pos) {
		return 0.5F;
	}

	@Override
	protected boolean isValidLightLevel() {
		return true;
	}

	@Override
	public BLSoundEvent getMusicFile(EntityPlayer listener) {
		return SoundRegistry.DREADFUL_PEAT_MUMMY_LOOP;
	}

	@Override
	public double getMusicRange(EntityPlayer listener) {
		return 32.0D;
	}

	@Override
	public boolean isMusicActive(EntityPlayer listener) {
		return isEntityAlive();
	}

	@Override
	public int getMusicLayer(EntityPlayer listener) {
		return EntityMusicLayers.BOSS;
	}

	@Override
	public UUID getBossInfoUuid() {
		return dataManager.get(BOSSINFO_ID).or(new UUID(0, 0));
	}
}