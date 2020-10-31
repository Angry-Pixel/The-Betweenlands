package thebetweenlands.common.entity.mobs;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.PriorityQueue;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableSet;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntitySenses;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.entity.ai.EntityAIAttackOnCollide;
import thebetweenlands.common.entity.movement.CustomPathFinder;
import thebetweenlands.common.entity.movement.ObstructionAwarePathNavigateClimber;
import thebetweenlands.common.entity.movement.ObstructionAwarePathNavigateGround;
import thebetweenlands.common.entity.movement.ObstructionAwareWalkNodeProcessor;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.WorldProviderBetweenlands;

public class EntityStalker extends EntityClimberBase implements IMob {
	public static final DataParameter<Boolean> SCREECHING = EntityDataManager.createKey(EntityStalker.class, DataSerializers.BOOLEAN);

	protected boolean restrictToPitstone = false;

	protected int maxPathingTargetHeight = 0;

	protected boolean isStalking = true;

	protected float farAnglePathingPenalty = 1.0f;
	protected float farAngle = 90.0f;

	protected float nearAnglePathingPenalty = 2.0f;
	protected float nearAngle = 65.0f;

	protected float stalkingDistanceNear = 4.0f;
	protected float stalkingDistanceFar = 8.0f;
	protected float stalkingDistancePenalty = 4.0f;

	protected boolean isFleeingFromView;

	protected int inViewTimer = 0;

	protected boolean drop = false;

	protected int checkSeenTimer = 20;
	protected boolean canStalkerBeSeen = false;

	public Vec3d eyeRotation;
	public Vec3d prevEyeRotation;
	public Vec3d eyeRotationTarget;
	private int nextEyeRotate;

	public int animationOffset;

	private boolean canCallAllies = true;

	public int screechingTicks = 0;
	public int prevScreechingTicks = 0;

	public EntityStalker(World world) {
		super(world);
		this.experienceValue = 30;
		this.maxPathingTargetHeight = 10;
		this.eyeRotation = new Vec3d(0, 0, 0);
		this.prevEyeRotation = new Vec3d(0, 0, 0);
		this.eyeRotationTarget = new Vec3d(0, 0, 0);
		this.animationOffset = rand.nextInt(200);
		this.nextEyeRotate = 1;
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new AIDropAttack(this));
		this.tasks.addTask(2, new AIScurry(this, 1.75f));
		this.tasks.addTask(3, new AIScreech(this, 80, 160, 1, 3));
		this.tasks.addTask(4, new AIBreakLightSources(this));
		this.tasks.addTask(5, new EntityAIAttackMelee(this, 1.1D, false));
		this.tasks.addTask(6, new EntityAIMoveTowardsRestriction(this, 1.0D));
		this.tasks.addTask(7, new EntityAIWander(this, 0.75D));
		this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(8, new EntityAILookIdle(this));

		this.targetTasks.addTask(1, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, 1, false, false, null));
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(SCREECHING, false);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();

		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);

		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(12.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40.0D);
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(40.0D);
		this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
	}

	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData data) {
		if(this.posY < WorldProviderBetweenlands.PITSTONE_HEIGHT + 3 && this.dimension == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId) {
			this.restrictToPitstone = true;
		}
		return super.onInitialSpawn(difficulty, data);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setBoolean("restrictToPitstone", this.restrictToPitstone);
		nbt.setBoolean("canCallAllies", this.canCallAllies);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		this.restrictToPitstone = nbt.getBoolean("restrictToPitstone");
		this.canCallAllies = nbt.getBoolean("canCallAllies");
	}

	public void setCanCallAllies(boolean canCall) {
		this.canCallAllies = canCall;
	}

	public boolean canCallAllies() {
		return this.canCallAllies;
	}

	public void setScreeching(boolean screeching) {
		this.dataManager.set(SCREECHING, screeching);
	}

	public boolean isScreeching() {
		return this.dataManager.get(SCREECHING);
	}

	@Override
	public float getMovementSpeed() {
		return super.getMovementSpeed() + (this.isStalking && this.isFleeingFromView ? 0.25f : 0.0f);
	}

	@Override
	public float getPathingMalus(EntityLiving entity, PathNodeType nodeType, BlockPos pos) {
		float priority = super.getPathPriority(nodeType);

		float penalty = 0;

		if(this.restrictToPitstone && this.posY > WorldProviderBetweenlands.PITSTONE_HEIGHT + 3 && pos.getY() >= this.posY) {
			penalty += Math.min((pos.getY() - this.posY) * 0.5f, 8);
		}

		if(priority >= 0.0f && this.isStalking) {
			int height = 0;

			while(pos.getY() - height > 0) {
				height++;

				if(!this.world.isAirBlock(pos.offset(EnumFacing.DOWN, height))) {
					break;
				}
			}

			penalty += Math.max(0, 6 - height) * 0.1f;

			EntityLivingBase target = this.getAttackTarget();

			if(target != null) {
				Vec3d look = target.getLook(1);
				Vec3d dir = new Vec3d(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f).subtract(target.getPositionEyes(1).subtract(look)).normalize();
				float dot = (float) look.dotProduct(dir);

				float angle = (float) Math.toDegrees(Math.acos(dot));

				penalty += Math.max(0, this.nearAngle - angle) / this.nearAngle * this.nearAnglePathingPenalty;
				penalty += Math.max(0, (this.farAngle - this.nearAngle) - (angle - this.nearAngle)) / (this.farAngle - this.nearAngle) * this.farAnglePathingPenalty;

				double dst = new Vec3d(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f).subtract(target.getPositionEyes(1)).length();

				penalty += (1 - (MathHelper.clamp(dst, this.stalkingDistanceNear, this.stalkingDistanceFar) - this.stalkingDistanceNear) / (this.stalkingDistanceFar - this.stalkingDistanceNear)) * this.stalkingDistancePenalty;
			}
		}

		return priority + penalty;
	}

	protected boolean isPathNodeAllowed(int x, int y, int z) {
		if(this.restrictToPitstone && this.posY <= WorldProviderBetweenlands.PITSTONE_HEIGHT + 3 && y >= WorldProviderBetweenlands.PITSTONE_HEIGHT + 2) {
			return false;
		}

		if(this.isStalking) {
			EntityLivingBase target = this.getAttackTarget();

			if(target != null) {
				if(target.getDistance(x + 0.5f, y + 0.5f, z + 0.5f) > this.stalkingDistanceNear) {
					return true;
				}

				Vec3d look = target.getLook(1);
				Vec3d dir = this.getPositionVector().subtract(target.getPositionEyes(1).subtract(look)).normalize();
				float dot = (float) look.dotProduct(dir);
				float angle = (float) Math.toDegrees(Math.acos(dot));
				if(angle < this.nearAngle) {
					return new Vec3d(x + 0.5f, y + 0.5f, z + 0.5f).subtract(target.getPositionVector()).length() > this.getPositionVector().subtract(target.getPositionVector()).length();
				}

				return false;
			}
		}

		return true;
	}

	@Override
	protected PathNavigate createNavigator(World worldIn) {
		ObstructionAwarePathNavigateGround<EntityStalker> navigate = new ObstructionAwarePathNavigateClimber<EntityStalker>(this, worldIn, false, true, true) {
			@Override
			public Path getPathToEntityLiving(Entity entityIn) {
				BlockPos pos = new BlockPos(entityIn);

				if(EntityStalker.this.isStalking && !EntityStalker.this.isFleeingFromView) {
					//Path to ceiling above target if possible
					for(int i = 1; i <= EntityStalker.this.maxPathingTargetHeight; i++) {
						if(this.getNodeProcessor().getPathNodeType(this.world, pos.getX(), pos.getY() + i, pos.getZ()) == PathNodeType.WALKABLE) {
							pos = pos.up(i);
							break;
						}
					}
				}

				return this.getPathToPos(pos);
			}

			@Override
			protected CustomPathFinder createPathFinder() {
				ObstructionAwareWalkNodeProcessor<EntityStalker> nodeProcessor = new ObstructionAwareWalkNodeProcessor<EntityStalker>() {
					@Override
					public PathNodeType getPathNodeType(IBlockAccess blockaccessIn, int x, int y, int z) {
						if(!EntityStalker.this.isPathNodeAllowed(x, y, z)) {
							return PathNodeType.BLOCKED;
						}
						return super.getPathNodeType(blockaccessIn, x, y, z);
					}
				};

				CustomPathFinder pathFinder = new CustomPathFinder(nodeProcessor);

				pathFinder.setMaxExpansions(400);

				pathFinder.setHeuristic((start, end, isTargetHeuristic) -> {
					if(isTargetHeuristic && EntityStalker.this.isFleeingFromView) {
						EntityLivingBase target = EntityStalker.this.getAttackTarget();

						if(target != null) {
							Vec3d look = target.getLook(1);
							Vec3d diff = new Vec3d(start.x + 0.5f - target.posX, start.y + 0.5f - target.posY, start.z + 0.5f - target.posZ);
							float dot = (float) look.dotProduct(diff);
							diff = diff.subtract(look.scale(dot));
							return 8.0f - (float)Math.abs(diff.x) - (float)Math.abs(diff.y) - (float)Math.abs(diff.z);
						}
					}

					return CustomPathFinder.DEFAULT_HEURISTIC.compute(start, end, isTargetHeuristic);
				});

				return pathFinder;
			}
		};
		navigate.setCanSwim(true);
		return navigate;
	}

	@Override
	public Vec3d getStickingForce(Pair<EnumFacing, Vec3d> walkingSide) {
		if(this.drop) {
			return new Vec3d(0, -0.08D, 0);
		}

		return super.getStickingForce(walkingSide);
	}

	private boolean canSeePosition(Vec3d start, Vec3d end) {
		return this.world.rayTraceBlocks(start, end, false, true, false) == null;
	}

	@Override
	public void onUpdate() {
		this.drop = false;

		super.onUpdate();

		this.prevScreechingTicks = this.screechingTicks;
		if(this.isScreeching()) {
			this.screechingTicks = Math.min(this.screechingTicks + 1, 100);
		} else {
			this.screechingTicks = Math.min(Math.max(this.screechingTicks - 5, 0), 40);
		}

		if(!this.world.isRemote) {
			boolean wasFleeingFromView = this.isFleeingFromView;
			this.isFleeingFromView = false;

			EntityLivingBase target = this.getAttackTarget();

			boolean isPotentiallySeen = false;

			if(this.isStalking) {
				if(target != null) {
					Vec3d look = target.getLook(1);
					Vec3d dir = this.getPositionVector().subtract(target.getPositionEyes(1).subtract(look)).normalize();
					float dot = (float) look.dotProduct(dir);
					float angle = (float) Math.toDegrees(Math.acos(dot));

					if(angle < this.nearAngle) {
						isPotentiallySeen = true;

						AxisAlignedBB aabb = this.getEntityBoundingBox();
						Vec3d center = new Vec3d((aabb.minX + aabb.maxX) * 0.5D, (aabb.minY + aabb.maxY) * 0.5D, (aabb.minZ + aabb.maxZ) * 0.5D);

						if(this.checkSeenTimer++ >= 20) {
							this.checkSeenTimer = 0;

							this.canStalkerBeSeen = false;

							if(this.canSeePosition(target.getPositionEyes(1), center)) {
								this.canStalkerBeSeen = true;
							} else {
								AxisAlignedBB checkAabb = this.getEntityBoundingBox().grow(1);

								for(int xo = 0; xo <= 1; xo++) {
									for(int yo = 0; yo <= 1; yo++) {
										for(int zo = 0; zo <= 1; zo++) {
											Vec3d checkPos = new Vec3d(
													(xo == 0 ? checkAabb.minX : checkAabb.maxX),
													(yo == 0 ? checkAabb.minY : checkAabb.maxY),
													(zo == 0 ? checkAabb.minZ : checkAabb.maxZ)
													);

											if(this.canSeePosition(target.getPositionEyes(1), checkPos)) {
												this.canStalkerBeSeen = true;
												break;
											}
										}
									}
								}
							}
						}

						if(this.canStalkerBeSeen) {
							this.isFleeingFromView = true;

							if(this.getNavigator().noPath() && this.ticksExisted % 10 == 0) {
								Vec3d right = look.crossProduct(new Vec3d(0, 1, 0));
								Vec3d up = right.crossProduct(look);

								float rot = this.rand.nextFloat() * (float)Math.PI * 2;
								float radius = 8.0f;

								Vec3d offset = right.scale(Math.cos(rot) * radius).add(up.scale(Math.sin(rot) * radius)).add(look.scale(-radius * 0.5f));

								RayTraceResult result = this.world.rayTraceBlocks(center, center.add(offset), false, true, false);
								if(result != null && result.hitVec != null) {
									offset = result.hitVec;
								}

								this.getNavigator().tryMoveToXYZ(this.posX + offset.x, this.posY + offset.y, this.posZ + offset.z, 1.0f);
							}
						}
					}
				}
			} else if(target == null) {
				this.isStalking = true;
			}

			if(!isPotentiallySeen) {
				this.checkSeenTimer = 20;
			}

			if(wasFleeingFromView && !this.isFleeingFromView) {
				this.getNavigator().clearPath();
			}

			if(this.isFleeingFromView && (target == null || this.getEntitySenses().canSee(target))) {
				this.inViewTimer++;

				if(this.inViewTimer > 200) {
					this.isStalking = false;

					if(target != null && this.getDistance(target) < 16.0f) {
						this.useParalysisAttack(target);
					}
				}
			} else {
				this.inViewTimer = Math.max(0, this.inViewTimer - 3);
			}

			if(this.world.getDifficulty() == EnumDifficulty.PEACEFUL) {
				this.setDead();
			}
		} else {
			if(this.screechingTicks > 40 && this.ticksExisted % 2 == 0) {
				this.spawnScreechingParticles();
			}
		}

		// Eye animation
		prevEyeRotation = eyeRotation;
		if ((ticksExisted + animationOffset) % nextEyeRotate == 0) {
			eyeRotationTarget = new Vec3d(rand.nextFloat() - 0.5, rand.nextFloat() - 0.5, rand.nextFloat() - 0.5);
			nextEyeRotate = rand.nextInt(15) + 20;
		}
		eyeRotation = eyeRotation.add(eyeRotationTarget.subtract(eyeRotation).scale(0.5));
	}
	
	@SideOnly(Side.CLIENT)
	protected void spawnScreechingParticles() {
		float angle = (float) Math.toRadians(-this.renderYawOffset);
		float ox = MathHelper.sin(angle) * 0.725f;
		float oz = MathHelper.cos(angle) * 0.725f;
		
		float screechingStrength = MathHelper.clamp((this.screechingTicks - 20) / 30.0f, 0, 1);
        float screechingHeadRotationStrength = MathHelper.sin(this.ticksExisted * 0.4f) * screechingStrength;
		float osx = MathHelper.sin(angle + (float)Math.PI * 0.5f) * 0.05f * screechingHeadRotationStrength;
		float osz = MathHelper.cos(angle + (float)Math.PI * 0.5f) * 0.05f * screechingHeadRotationStrength;
		
		Particle particle = BLParticles.SONIC_SCREAM.create(this.world, this.posX + ox, this.posY + 1.5f, this.posZ + oz, 
				ParticleArgs.get().withMotion(osx, 0.3f, osz).withScale(10).withData(30, MathHelper.floor(this.ticksExisted * 3.3f))
				.withColor(1.0f, 0.9f, 0.8f, 1.0f));
		
		BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING, particle);
	}

	@Override
	public void fall(float distance, float damageMultiplier) {
		//No fall damage
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if(super.attackEntityFrom(source, amount)) {
			if(this.getAttackTarget() != null && source.getTrueSource() == this.getAttackTarget()) {
				this.isStalking = false;
			}

			if(amount >= 6 && this.isScreeching()) {
				this.setScreeching(false);
			}

			return true;
		}

		return false;
	}

	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		return EntityAIAttackOnCollide.useStandardAttack(this, entityIn);
	}

	@Override
	public boolean getCanSpawnHere() {
		return this.world.getDifficulty() != EnumDifficulty.PEACEFUL && super.getCanSpawnHere();
	}

	@Override
	protected boolean canDropLoot() {
		return true;
	}

	public void useParalysisAttack(EntityLivingBase target) {
		target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 80, 10));
		target.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 80, -10));

		this.playSound(SoundRegistry.STALKER_SCREAM, 0.6f, 1.0f);
	}

	@Override
	protected void playStepSound(BlockPos pos, Block blockIn) {
		this.playSound(SoundRegistry.STALKER_STEP, 1.5f, 1.0f);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return !this.isStalking ? SoundRegistry.STALKER_LIVING : null;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundRegistry.STALKER_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.STALKER_DEATH;
	}

	public static class AIBreakLightSources extends EntityAIBase {
		private static final Set<Block> IGNORED_LIGHT_SOURCES = ImmutableSet.of(BlockRegistry.OCTINE_ORE, BlockRegistry.LIFE_CRYSTAL_STALACTITE, BlockRegistry.MOB_SPAWNER);

		private final EntityStalker entity;

		private BlockPos lightSourcePos = null;

		private int failCount = 0;

		private Path path;

		public AIBreakLightSources(EntityStalker entity) {
			this.entity = entity;
			this.setMutexBits(0b11);
		}

		@Nullable
		private RayTraceResult rayTraceBlockLight(Vec3d start, Vec3d end) {
			if(!Double.isNaN(start.x) && !Double.isNaN(start.y) && !Double.isNaN(start.z)) {
				if(!Double.isNaN(end.x) && !Double.isNaN(end.y) && !Double.isNaN(end.z)) {
					int ex = MathHelper.floor(end.x);
					int ey = MathHelper.floor(end.y);
					int ez = MathHelper.floor(end.z);
					int sx = MathHelper.floor(start.x);
					int sy = MathHelper.floor(start.y);
					int sz = MathHelper.floor(start.z);
					BlockPos pos = new BlockPos(sx, sy, sz);
					IBlockState state = this.entity.world.getBlockState(pos);

					if(this.entity.world.getLightFor(EnumSkyBlock.BLOCK, pos) > 0 || (state.getCollisionBoundingBox(this.entity.world, pos) != Block.NULL_AABB && state.getBlock().canCollideCheck(state, false))) {
						RayTraceResult result = state.collisionRayTrace(this.entity.world, pos, start, end);

						if(result != null) {
							return result;
						} else {
							return new RayTraceResult(new Vec3d(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f), EnumFacing.UP, pos);
						}
					}

					int steps = 200;

					while(steps-- >= 0) {
						if(Double.isNaN(start.x) || Double.isNaN(start.y) || Double.isNaN(start.z)) {
							return null;
						}

						if(sx == ex && sy == ey && sz == ez) {
							return null;
						}

						boolean xDiff = true;
						boolean yDiff = true;
						boolean zDiff = true;
						double newX = 999.0D;
						double newY = 999.0D;
						double newZ = 999.0D;

						if(ex > sx) {
							newX = (double)sx + 1.0D;
						} else if(ex < sx) {
							newX = (double)sx + 0.0D;
						} else {
							xDiff = false;
						}

						if(ey > sy) {
							newY = (double)sy + 1.0D;
						} else if(ey < sy) {
							newY = (double)sy + 0.0D;
						} else {
							yDiff = false;
						}

						if(ez > sz) {
							newZ = (double)sz + 1.0D;
						} else if(ez < sz) {
							newZ = (double)sz + 0.0D;
						} else {
							zDiff = false;
						}

						double offsetX = 999.0D;
						double offsetY = 999.0D;
						double offsetZ = 999.0D;
						double dx = end.x - start.x;
						double dy = end.y - start.y;
						double dz = end.z - start.z;

						if(xDiff) {
							offsetX = (newX - start.x) / dx;
						}

						if(yDiff) {
							offsetY = (newY - start.y) / dy;
						}

						if(zDiff) {
							offsetZ = (newZ - start.z) / dz;
						}

						if(offsetX == -0.0D) {
							offsetX = -1.0E-4D;
						}

						if(offsetY == -0.0D) {
							offsetY = -1.0E-4D;
						}

						if(offsetZ == -0.0D) {
							offsetZ = -1.0E-4D;
						}

						EnumFacing hitFacing;

						if(offsetX < offsetY && offsetX < offsetZ) {
							hitFacing = ex > sx ? EnumFacing.WEST : EnumFacing.EAST;
							start = new Vec3d(newX, start.y + dy * offsetX, start.z + dz * offsetX);
						} else if(offsetY < offsetZ) {
							hitFacing = ey > sy ? EnumFacing.DOWN : EnumFacing.UP;
							start = new Vec3d(start.x + dx * offsetY, newY, start.z + dz * offsetY);
						} else {
							hitFacing = ez > sz ? EnumFacing.NORTH : EnumFacing.SOUTH;
							start = new Vec3d(start.x + dx * offsetZ, start.y + dy * offsetZ, newZ);
						}

						sx = MathHelper.floor(start.x) - (hitFacing == EnumFacing.EAST ? 1 : 0);
						sy = MathHelper.floor(start.y) - (hitFacing == EnumFacing.UP ? 1 : 0);
						sz = MathHelper.floor(start.z) - (hitFacing == EnumFacing.SOUTH ? 1 : 0);
						pos = new BlockPos(sx, sy, sz);
						IBlockState offsetState = this.entity.world.getBlockState(pos);

						if(this.entity.world.getLightFor(EnumSkyBlock.BLOCK, pos) > 0 || (offsetState.getCollisionBoundingBox(this.entity.world, pos) != Block.NULL_AABB && offsetState.getBlock().canCollideCheck(state, false))) {
							RayTraceResult result = offsetState.collisionRayTrace(this.entity.world, pos, start, end);

							if(result != null) {
								return result;
							} else {
								return new RayTraceResult(new Vec3d(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f), EnumFacing.UP, pos);
							}
						}
					}

					return null;
				} else {
					return null;
				}
			} else {
				return null;
			}
		}

		@Nullable
		private BlockPos findLightSource(BlockPos pos, int maxChecks) {
			int lightValue = this.entity.world.getLightFor(EnumSkyBlock.BLOCK, pos);

			if(lightValue > 0) {
				Set<BlockPos> checked = new HashSet<>();
				PriorityQueue<Pair<BlockPos, Integer>> queue = new PriorityQueue<>(1, (p1, p2) -> -Integer.compare(p1.getRight(), p2.getRight()));

				queue.add(Pair.of(pos, lightValue));

				while(!queue.isEmpty() && maxChecks-- > 0) {
					Pair<BlockPos, Integer> queueEntry = queue.remove();
					BlockPos queuePos = queueEntry.getLeft();
					int queueLight = queueEntry.getRight();

					IBlockState state = this.entity.world.getBlockState(queuePos);
					if(state.getLightValue(this.entity.world, queuePos) > 0) {
						return queuePos;
					}

					for(EnumFacing offset : EnumFacing.VALUES) {
						BlockPos offsetPos = queuePos.offset(offset);

						if(this.entity.world.isBlockLoaded(offsetPos, false)) {
							lightValue = this.entity.world.getLightFor(EnumSkyBlock.BLOCK, offsetPos);

							if(lightValue > queueLight && checked.add(offsetPos)) {
								queue.add(Pair.of(offsetPos, lightValue));
							}
						}
					}
				}
			}

			return null;
		}

		private boolean isTargetLightSource(BlockPos pos) {
			EntityLivingBase target = this.entity.getAttackTarget();

			if(target != null) {
				if(target.getDistanceSq(pos) < this.entity.stalkingDistanceFar * this.entity.stalkingDistanceFar) {
					return false;
				}

				Vec3d look = target.getLook(1);
				Vec3d dir = new Vec3d(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f).subtract(target.getPositionEyes(1).subtract(look)).normalize();
				float dot = (float) look.dotProduct(dir);
				float angle = (float) Math.toDegrees(Math.acos(dot));

				if(angle < this.entity.farAngle) {
					return false;
				}
			}

			IBlockState state = this.entity.world.getBlockState(pos);

			if(!IGNORED_LIGHT_SOURCES.contains(state.getBlock())) {
				float hardness = state.getBlockHardness(this.entity.world, pos);
				return this.entity.world.getBlockState(pos).getLightValue(this.entity.world, pos) > 0 && hardness >= 0 && hardness <= 2.5F && state.getBlock().canEntityDestroy(state, this.entity.world, pos, this.entity);
			}

			return false;
		}

		@Override
		public boolean shouldExecute() {
			if(this.entity.isStalking && this.entity.ticksExisted % 10 == 0 && ForgeEventFactory.getMobGriefingEvent(this.entity.world, this.entity)) {
				float checkRange = 32.0f;

				Vec3d start = this.entity.getPositionEyes(1);
				Vec3d end = start.add(new Vec3d(this.entity.rand.nextFloat() - 0.5f, this.entity.rand.nextFloat() - 0.5f, this.entity.rand.nextFloat() - 0.5f).normalize().scale(checkRange));

				RayTraceResult result = this.rayTraceBlockLight(start, end);

				if(result != null) {
					BlockPos lightPos = this.findLightSource(result.getBlockPos(), 32);

					if(lightPos != null && this.isTargetLightSource(lightPos)) {
						if(this.entity.getNavigator().getPathToXYZ(lightPos.getX() + 0.5f, lightPos.getY() + 0.5f, lightPos.getZ() + 0.5f) != null) {
							this.lightSourcePos = lightPos;
						}
					}
				}

				return this.lightSourcePos != null;
			}

			return false;
		}

		@Override
		public boolean shouldContinueExecuting() {
			return this.entity.isStalking && this.lightSourcePos != null && this.isTargetLightSource(this.lightSourcePos) && ForgeEventFactory.getMobGriefingEvent(this.entity.world, this.entity) && this.failCount <= 5;
		}

		@Override
		public void resetTask() {
			this.lightSourcePos = null;
			this.failCount = 0;
			this.entity.getNavigator().clearPath();
		}

		@Override
		public void updateTask() {
			if(this.lightSourcePos != null) {
				if(this.failCount == 0 || this.entity.ticksExisted % 20 == 0) {
					if(!this.entity.getNavigator().tryMoveToXYZ(this.lightSourcePos.getX() + 0.5f, this.lightSourcePos.getY() + 0.5f, this.lightSourcePos.getZ() + 0.5f, 1)) {
						this.failCount++;
					} else {
						this.failCount = 1;
					}
				}

				if(this.entity.getDistance(this.lightSourcePos.getX() + 0.5f, this.lightSourcePos.getY() + 0.5f - this.entity.height / 2.0f, this.lightSourcePos.getZ() + 0.5f) < 2.0f) {
					IBlockState state = this.entity.world.getBlockState(this.lightSourcePos);

					if(ForgeEventFactory.getMobGriefingEvent(this.entity.world, this.entity) && this.isTargetLightSource(this.lightSourcePos) && ForgeEventFactory.onEntityDestroyBlock(this.entity, this.lightSourcePos, state)) {
						state.getBlock().dropBlockAsItemWithChance(this.entity.world, this.lightSourcePos, state, 1.0f, 0);
						this.entity.world.setBlockToAir(this.lightSourcePos);
					} else {
						this.lightSourcePos = null;
					}
				}
			}
		}
	}

	public static class AIScurry extends EntityAIBase {

		private final EntityStalker entity;

		private final float speed;

		private int maxPathMemory = 32;
		private LinkedHashMap<Pair<Path, Integer>, BlockPos> pathMemory = new LinkedHashMap<>();

		private Path path;

		private Vec3d scurryingStart;
		private double lastDistance;
		private boolean isScurrying = false;

		private int scurryingCooldown = 0;

		public AIScurry(EntityStalker entity, float speed) {
			this.entity = entity;
			this.speed = speed;
			this.setMutexBits(0b11);
		}

		@Override
		public boolean shouldExecute() {
			if(!this.isScurrying) {
				Path path = this.entity.getNavigator().getPath();
				if(path != null) {
					double smallestDst = Double.MAX_VALUE;
					PathPoint closestPathPoint = null;
					int closestPathPointIndex = 0;

					for(int i = 0; i < path.getCurrentPathIndex(); i++) {
						PathPoint point = path.getPathPointFromIndex(i);

						double dst = this.entity.getDistanceSq(point.x + 0.5f, point.y + 0.5f, point.z + 0.5f);

						if(dst <= smallestDst) {
							smallestDst = dst;
							closestPathPoint = point;
							closestPathPointIndex = i;
						}
					}

					if(closestPathPoint != null) {
						Pair<Path, Integer> key = Pair.of(path, closestPathPointIndex);

						if(!this.pathMemory.containsKey(key)) {
							if(this.pathMemory.size() > this.maxPathMemory) {
								this.pathMemory.remove(this.pathMemory.keySet().iterator().next());
							}

							this.pathMemory.put(key, new BlockPos(closestPathPoint.x, closestPathPoint.y, closestPathPoint.z));
						}
					}
				}

				if(this.entity.isStalking || this.scurryingCooldown++ < 40) {
					return false;
				}
			}

			EntityLivingBase target = this.entity.getAttackTarget();

			if(target != null) {
				Vec3d dir = this.entity.getPositionEyes(1).subtract(target.getPositionEyes(1)).normalize();
				float angle = (float) Math.toDegrees(Math.acos(dir.dotProduct(target.getLook(1))));
				return angle < 5;
			}

			return false;
		}

		@Override
		public void startExecuting() {
			this.scurryingCooldown = 0;

			this.isScurrying = true;

			PathPoint finalPoint = this.path != null ? this.path.getFinalPathPoint() : null;
			this.scurryingStart = this.entity.getAttackTarget() != null ? this.entity.getAttackTarget().getPositionVector() : finalPoint != null ? new Vec3d(finalPoint.x + 0.5f, finalPoint.y + 0.5f, finalPoint.z + 0.5f) : this.entity.getPositionVector();

			PathPoint[] pathPoints = new PathPoint[this.pathMemory.size()];

			double furthestPathPosDst = 0;
			BlockPos furthestPathPos = null;

			int i = 0;
			for(BlockPos pos : this.pathMemory.values()) {
				pathPoints[this.pathMemory.size() - ++i] = new PathPoint(pos.getX(), pos.getY(), pos.getZ());

				double dst = pos.getDistance((int)this.scurryingStart.x, (int)this.scurryingStart.y, (int)this.scurryingStart.z);
				if(dst > furthestPathPosDst) {
					furthestPathPosDst = dst;
					furthestPathPos = pos;
				}
			}

			if(furthestPathPos != null) {
				this.path = this.entity.getNavigator().getPathToPos(furthestPathPos);
			}

			if(this.path == null) {
				this.path = new Path(pathPoints);
			}

			for(int j = 0; (this.path == null || this.path.getCurrentPathLength() < 6 || this.path.getFinalPathPoint().distanceTo(new PathPoint((int)this.scurryingStart.x, (int)this.scurryingStart.y, (int)this.scurryingStart.z)) < 6) && j < 3; j++) {
				Vec3d dir = this.entity.getPositionVector().subtract(this.scurryingStart).add(this.entity.getLookVec().scale(-0.25f)).normalize();

				Vec3d right = dir.crossProduct(new Vec3d(0, 1, 0));
				Vec3d up = right.crossProduct(dir);

				Vec3d target = this.entity.getPositionVector().add(dir.scale(32.0f).add(right.scale((this.entity.rand.nextFloat() - 0.5f) * 16.0f)).add(up.scale((this.entity.rand.nextFloat() - 0.5f) * 16.0f)));

				this.path = this.entity.getNavigator().getPathToXYZ(target.x, target.y, target.z);
			}

			this.entity.playLivingSound();
		}

		@Override
		public void updateTask() {
			if(this.path != null) {
				PathPoint finalPoint = this.path.getFinalPathPoint();

				//Check if path still possible
				if(finalPoint == null || (this.entity.ticksExisted % 20 == 0 && this.entity.getNavigator().getPathToPos(new BlockPos(finalPoint.x, finalPoint.y, finalPoint.z)) == null)) {
					this.isScurrying = false;
				} else {
					this.entity.getNavigator().setPath(this.path, this.speed);
				}
			}
		}

		@Override
		public boolean shouldContinueExecuting() {
			if(this.scurryingStart != null) {
				double distance = this.scurryingStart.distanceTo(this.entity.getPositionVector());

				if(distance < this.lastDistance - 1) {
					return false;
				} else {
					this.lastDistance = Math.max(this.lastDistance, distance);
				}
			} else {
				return false;
			}

			return this.isScurrying && this.path != null && !this.path.isFinished();
		}

		@Override
		public void resetTask() {
			this.isScurrying = false;
			this.scurryingStart = null;
			this.lastDistance = 0;
			this.path = null;
			this.pathMemory.clear();
			this.entity.getNavigator().clearPath();
		}
	}

	public static class AIDropAttack extends EntityAIBase {

		private final EntityStalker entity;

		private float dropRadius = 5.0f;

		private int dropTimer = 0;

		private int waitingTimer = 20;

		private boolean attacked = false;

		public AIDropAttack(EntityStalker entity) {
			this.entity = entity;
			this.setMutexBits(0b11);
		}

		@Override
		public boolean shouldExecute() {
			EntityLivingBase target = this.entity.getAttackTarget();

			boolean inRange = false;

			if(target != null) {
				if(this.entity.getEntitySenses().canSee(target)) {
					Vec3d up = new Vec3d(0, 1, 0);
					Vec3d diff = target.getPositionVector().subtract(this.entity.getPositionVector());
					double diffY = up.dotProduct(diff);

					if(diffY <= -3.0D && diff.subtract(up.scale(diffY)).length() <= this.dropRadius) {
						inRange = true;

						if(this.dropTimer++ >= 40) {
							return true;
						}
					}
				}
			}

			if(!inRange) {
				this.dropTimer = Math.max(0, this.dropTimer - 1);
			}

			return false;
		}

		@Override
		public void updateTask() {
			this.entity.getNavigator().clearPath();
			this.entity.getMoveHelper().setMoveTo(this.entity.posX, this.entity.posY, this.entity.posZ, 1);
			this.entity.drop = true;
			this.entity.isStalking = false;

			EntityLivingBase target = this.entity.getAttackTarget();
			if(target != null) {
				this.entity.getLookHelper().setLookPositionWithEntity(target, 30, 30);

				if(this.waitingTimer > 30 && this.entity.onGround && !this.attacked) {
					this.attacked = true;
					this.entity.useParalysisAttack(target);
				}
			}

			this.waitingTimer++;
		}

		@Override
		public boolean shouldContinueExecuting() {
			return this.entity.getAttackTarget() != null && this.waitingTimer < 80;
		}

		@Override
		public void resetTask() {
			this.dropTimer = 0;
			this.waitingTimer = 0;
			this.attacked = false;
		}
	}

	public static class AIScreech extends EntityAIBase {
		private final EntityStalker entity;
		private final int minCooldown, maxCooldown;
		private final int minAllies, maxAllies;

		private int cooldown;
		
		private int allies;
		private boolean didSpawn;

		public AIScreech(EntityStalker entity, int minCooldown, int maxCooldown, int minAllies, int maxAllies) {
			this.entity = entity;
			this.minCooldown = minCooldown;
			this.maxCooldown = maxCooldown;
			this.minAllies = minAllies;
			this.maxAllies = maxAllies;
			this.setMutexBits(0b11);
		}

		@Override
		public boolean shouldExecute() {
			EntityLivingBase target = this.entity.getAttackTarget();
			return this.cooldown <= 0 && !this.entity.isStalking && /*TODO Testing this.entity.getHealth() <= this.entity.getHealth() * 0.5f && */target != null && this.entity.getDistance(target) > /*TODO Testing 8*/ 1 && this.entity.canCallAllies();
		}

		@Override
		public boolean shouldContinueExecuting() {
			EntityLivingBase target = this.entity.getAttackTarget();
			return !this.entity.isStalking && target != null && this.entity.canCallAllies() && this.entity.isScreeching() && this.allies > 0;
		}

		@Override
		public void startExecuting() {
			this.cooldown = this.minCooldown + this.entity.rand.nextInt(this.maxCooldown - this.minCooldown + 1);
			this.entity.setScreeching(true);
			this.allies = this.minAllies + this.entity.rand.nextInt(this.maxAllies - this.minAllies + 1);
		}

		@Override
		public void resetTask() {
			this.entity.setScreeching(false);
			if(this.didSpawn) {
				this.entity.setCanCallAllies(false);
			}
		}

		@Override
		public void updateTask() {
			if(this.shouldContinueExecuting() && this.entity.screechingTicks >= 100) {
				EntityLivingBase target = this.entity.getAttackTarget();
				EntitySenses targetSenses = target instanceof EntityLiving ? ((EntityLiving) target).getEntitySenses() : null;
				Vec3d targetLook = target.getLookVec();

				EntityStalker stalker = null;

				for(int i = 0; i < 8 && this.allies > 0; i++) {
					float dx = this.entity.rand.nextFloat() - 0.5f;
					float dy = this.entity.rand.nextFloat() - 0.5f;
					float dz = this.entity.rand.nextFloat() - 0.5f;

					float dst = 12 + this.entity.rand.nextFloat() * 12;

					float scale = dst / MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
					dx *= scale;
					dy *= scale;
					dz *= scale;

					BlockPos pos = new BlockPos(this.entity.posX + dx, this.entity.posY + dy, this.entity.posZ + dz);

					for(int j = 0; j < 8 && this.entity.world.isAirBlock(pos); j++) {
						pos = pos.down();
					}

					if(!this.entity.world.isAirBlock(pos) && this.entity.world.isAirBlock(pos.up())) {
						pos = pos.up();

						if(stalker == null) {
							stalker = new EntityStalker(this.entity.world);
						}

						boolean isBlockedFromSight = true;
						boolean isNotInView = true;

						AxisAlignedBB visibilityBox = new AxisAlignedBB(pos).grow(2);
						checks: for(int xo = 0; xo <= 1; xo++) {
							for(int yo = 0; yo <= 1; yo++) {
								for(int zo = 0; zo <= 1; zo++) {
									double cx = xo == 0 ? visibilityBox.minX : visibilityBox.maxX;
									double cy = yo == 0 ? visibilityBox.minY : visibilityBox.maxY;
									double cz = zo == 0 ? visibilityBox.minZ : visibilityBox.maxZ;

									Vec3d diff = new Vec3d(cx, cy, cz).subtract(target.getPositionEyes(1));

									if(diff.normalize().dotProduct(targetLook) > 0.1f) {
										isNotInView = false;
									}

									stalker.setLocationAndAngles(cx, cy + 0.1f - stalker.getEyeHeight() /*offset for canEntityBeSeen*/, cz, 0, 0);

									if((targetSenses != null && targetSenses.canSee(stalker)) || (targetSenses == null && target.canEntityBeSeen(stalker))) {
										isBlockedFromSight = false;
									}

									if(!isNotInView && !isBlockedFromSight) {
										break checks;
									}
								}
							}
						}

						if(isBlockedFromSight || isNotInView) {
							stalker.setLocationAndAngles(pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f, this.entity.rand.nextFloat() * 360.0f, 0);

							if(stalker.getCanSpawnHere() && stalker.isNotColliding()) {
								stalker.setCanCallAllies(false);
								stalker.setAttackTarget(this.entity.getAttackTarget());
								stalker.isStalking = false;

								this.entity.world.spawnEntity(stalker);

								stalker = null;

								this.allies--;
								this.didSpawn = true;
							}
						}
					}
				}

				if(stalker != null) {
					stalker.setDead();
				}
			}
		}
	}
}
