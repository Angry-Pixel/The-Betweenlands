package thebetweenlands.common.entity.mobs;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.PriorityQueue;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import thebetweenlands.common.entity.ai.ObstructionAwarePathNavigateGround;
import thebetweenlands.common.entity.ai.ObstructionAwareWalkNodeProcessor;

public class EntityClimber extends EntityClimberBase {
	protected int maxPathingTargetHeight = 0;

	protected boolean isStalking = true;

	protected float farAnglePathingPenalty = 2.0f;
	protected float farAngle = 90.0f;

	protected float nearAnglePathingPenalty = 4.0f;
	protected float nearAngle = 60.0f;

	protected float stalkingDistanceNear = 4.0f;
	protected float stalkingDistanceFar = 10.0f;
	protected float stalkingDistancePenalty = 4.0f;

	protected boolean isFleeingFromView;

	protected int inViewTimer = 0;
	
	public EntityClimber(World world) {
		super(world);
		this.maxPathingTargetHeight = 16;
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(0, new AIScurry(this));
		//this.tasks.addTask(1, new AIBreakLightSources(this));
		this.tasks.addTask(2, new EntityAIAttackMelee(this, 1.0D, false));
		this.targetTasks.addTask(1, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, 1, false, false, null));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(5D);
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(40.0D);
		this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
	}

	@Override
	public float getMovementSpeed() {
		return super.getMovementSpeed() + (this.isFleeingFromView ? 0.15f : 0.0f);
	}

	@Override
	public float getPathingMalus(EntityLiving entity, PathNodeType nodeType, BlockPos pos) {
		float priority = super.getPathPriority(nodeType);

		if(priority >= 0.0f && this.isStalking) {
			int height = 0;

			while(pos.getY() - height > 0) {
				height++;

				if(!this.world.isAirBlock(pos.offset(EnumFacing.DOWN, height))) {
					break;
				}
			}

			float penalty = Math.max(0, 6 - height) * 1f; 

			EntityLivingBase target = this.getAttackTarget();

			if(target != null) {
				Vec3d look = target.getLook(1);
				Vec3d dir = new Vec3d(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f).subtract(target.getPositionEyes(1).subtract(look.scale(3))).normalize();
				float dot = (float) look.dotProduct(dir);

				if(dot > 0) {
					float angle = (float) Math.toDegrees(Math.acos(dot));
					penalty += Math.max(0, this.nearAngle - angle) / this.nearAnglePathingPenalty * this.nearAnglePathingPenalty;
					penalty += Math.max(0, (this.farAngle - this.nearAngle) - (angle - this.nearAngle)) / (this.farAngle - this.nearAngle) * this.farAnglePathingPenalty;
				}

				double dst = new Vec3d(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f).subtract(target.getPositionEyes(1)).length();

				penalty += (1 - (MathHelper.clamp(dst, this.stalkingDistanceNear, this.stalkingDistanceFar) - this.stalkingDistanceNear) / (this.stalkingDistanceFar - this.stalkingDistanceNear)) * this.stalkingDistancePenalty;
			}


			return priority + penalty;
		}

		return priority;
	}

	protected boolean isPathNodeAllowed(int x, int y, int z) {
		if(this.isStalking) {
			EntityLivingBase target = this.getAttackTarget();

			if(target != null) {
				if(target.getDistance(x + 0.5f, y + 0.5f, z + 0.5f) > this.stalkingDistanceNear) {
					return true;
				}

				Vec3d look = target.getLook(1);
				Vec3d dir = this.getPositionVector().subtract(target.getPositionEyes(1).subtract(look.scale(3))).normalize();
				float dot = (float) look.dotProduct(dir);
				float angle = (float) Math.toDegrees(Math.acos(dot));
				if(dot > 0 && angle < this.nearAngle) {
					return new Vec3d(x + 0.5f, y + 0.5f, z + 0.5f).subtract(target.getPositionVector()).length() > this.getPositionVector().subtract(target.getPositionVector()).length();
				}

				return false;
			}
		}

		return true;
	}

	@Override
	protected PathNavigate createNavigator(World worldIn) {
		ObstructionAwarePathNavigateGround<EntityClimber> navigate = new ObstructionAwarePathNavigateGround<EntityClimber>(this, worldIn, false, true, true, false) {
			@Override
			public Path getPathToEntityLiving(Entity entityIn) {
				BlockPos pos = new BlockPos(entityIn);

				if(EntityClimber.this.isStalking && !EntityClimber.this.isFleeingFromView) {
					//Path to ceiling above target if possible
					for(int i = 1; i <= EntityClimber.this.maxPathingTargetHeight; i++) {
						if(this.getNodeProcessor().getPathNodeType(this.world, pos.getX(), pos.getY() + i, pos.getZ()) == PathNodeType.WALKABLE) {
							pos = pos.up(i);
							break;
						}
					}
				}

				return this.getPathToPos(pos);
			}

			@Override
			protected PathFinder getPathFinder() {
				this.nodeProcessor = new ObstructionAwareWalkNodeProcessor<EntityClimber>() {
					@Override
					public PathNodeType getPathNodeType(IBlockAccess blockaccessIn, int x, int y, int z) {
						if(!EntityClimber.this.isPathNodeAllowed(x, y, z)) {
							return PathNodeType.BLOCKED;
						}
						return super.getPathNodeType(blockaccessIn, x, y, z);
					}
				};
				this.nodeProcessor.setCanEnterDoors(true);
				return this.pathFinder = new PathFinder(this.nodeProcessor);
			}
		};
		navigate.setCanSwim(true);
		return navigate;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if(!this.world.isRemote) {
			this.isFleeingFromView = false;
			
			EntityLivingBase target = this.getAttackTarget();
			
			if(this.isStalking) {
				if(target != null) {
					Vec3d look = target.getLook(1);
					Vec3d dir = this.getPositionVector().subtract(target.getPositionEyes(1).subtract(look.scale(3))).normalize();
					float dot = (float) look.dotProduct(dir);
					float angle = (float) Math.toDegrees(Math.acos(dot));
	
					if(dot > 0 && angle < this.nearAngle) {
						this.isFleeingFromView = true;
	
						if(this.getNavigator().noPath()) {
							this.getNavigator().tryMoveToXYZ(target.posX + this.rand.nextFloat() * 8 - 4, target.posY + this.rand.nextFloat() * 8 - 4, target.posZ + this.rand.nextFloat() * 8 - 4, 1.0f);
						}
					}
				}
			} else if(target == null) {
				this.isStalking = true;
			}
			
			if(this.isFleeingFromView) {
				this.inViewTimer += 3;
				
				if(this.inViewTimer > 400) {
					this.isStalking = false;
				}
			} else {
				this.inViewTimer = Math.max(0, this.inViewTimer - 2);
			}
		}
	}

	public static class AIBreakLightSources extends EntityAIBase {
		private final EntityClimber entity;

		private BlockPos lightSourcePos = null;

		private int failCount = 0;

		private Path path;

		public AIBreakLightSources(EntityClimber entity) {
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

					if(this.entity.world.getLightFor(EnumSkyBlock.BLOCK, pos) > 0) {
						IBlockState state = this.entity.world.getBlockState(pos);
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

						if(this.entity.world.getLightFor(EnumSkyBlock.BLOCK, pos) > 0) {
							IBlockState offsetState = this.entity.world.getBlockState(pos);
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

		private boolean isBreakableLightSource(BlockPos pos) {
			IBlockState state = this.entity.world.getBlockState(pos);
			float hardness = state.getBlockHardness(this.entity.world, pos);
			return this.entity.world.getBlockState(pos).getLightValue(this.entity.world, pos) > 0 && hardness >= 0 && hardness <= 2.5F && state.getBlock().canEntityDestroy(state, this.entity.world, pos, this.entity);
		}

		@Override
		public boolean shouldExecute() {
			if(ForgeEventFactory.getMobGriefingEvent(this.entity.world, this.entity)) {
				float checkRange = 32.0f;

				Vec3d start = this.entity.getPositionEyes(1);
				Vec3d end = start.add(new Vec3d(this.entity.rand.nextFloat() - 0.5f, this.entity.rand.nextFloat() - 0.5f, this.entity.rand.nextFloat() - 0.5f).normalize().scale(checkRange));

				RayTraceResult result = this.rayTraceBlockLight(start, end);

				if(result != null) {
					BlockPos lightPos = this.findLightSource(result.getBlockPos(), 32);

					if(lightPos != null) {
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
			return this.lightSourcePos != null && this.isBreakableLightSource(this.lightSourcePos) && ForgeEventFactory.getMobGriefingEvent(this.entity.world, this.entity) && this.failCount < 100;
		}

		@Override
		public void resetTask() {
			this.lightSourcePos = null;
			this.failCount = 0;
		}

		@Override
		public void updateTask() {
			if(this.lightSourcePos != null) {
				if(!this.entity.getNavigator().tryMoveToXYZ(this.lightSourcePos.getX() + 0.5f, this.lightSourcePos.getY() + 0.5f, this.lightSourcePos.getZ() + 0.5f, 1)) {
					this.failCount++;
				} else {
					this.failCount = 0;
				}

				if(this.entity.getDistance(this.lightSourcePos.getX() + 0.5f, this.lightSourcePos.getY() + 0.5f - this.entity.height / 2.0f, this.lightSourcePos.getZ() + 0.5f) < 2.0f) {
					IBlockState state = this.entity.world.getBlockState(this.lightSourcePos);

					if(ForgeEventFactory.getMobGriefingEvent(this.entity.world, this.entity) && this.isBreakableLightSource(this.lightSourcePos) && ForgeEventFactory.onEntityDestroyBlock(this.entity, this.lightSourcePos, state)) {
						state.getBlock().dropBlockAsItemWithChance(this.entity.world, this.lightSourcePos, state, 1.0f, 0);
						this.entity.world.playEvent(2001, this.lightSourcePos, Block.getStateId(state));
						this.entity.world.setBlockToAir(this.lightSourcePos);
					} else {
						this.lightSourcePos = null;
					}
				}
			}
		}
	}

	public static class AIScurry extends EntityAIBase {

		private final EntityClimber entity;

		private int maxPathMemory = 32;
		private LinkedHashMap<Pair<Path, Integer>, BlockPos> pathMemory = new LinkedHashMap<>();

		private Path path;

		private Vec3d scurryingStart;
		private double lastDistance;
		private boolean isScurrying = false;

		private int scurryingCooldown = 0;

		public AIScurry(EntityClimber entity) {
			this.entity = entity;
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

				if(this.scurryingCooldown-- > 0) {
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
			this.scurryingCooldown = 40;

			this.isScurrying = true;

			PathPoint finalPoint = this.path != null ? this.path.getFinalPathPoint() : null;
			this.scurryingStart = this.entity.getAttackTarget() != null ? this.entity.getAttackTarget().getPositionVector() : finalPoint != null ? new Vec3d(finalPoint.x + 0.5f, finalPoint.y + 0.5f, finalPoint.z + 0.5f) : this.entity.getPositionVector();

			PathPoint[] pathPoints = new PathPoint[this.pathMemory.size()];

			int i = 0;
			for(BlockPos pos : this.pathMemory.values()) {
				pathPoints[this.pathMemory.size() - ++i] = new PathPoint(pos.getX(), pos.getY(), pos.getZ());
			}

			this.path = new Path(pathPoints);
		}

		@Override
		public void updateTask() {
			if(this.path != null) {
				PathPoint finalPoint = this.path.getFinalPathPoint();

				//Check if path still possible
				if(finalPoint == null || this.entity.getNavigator().getPathToPos(new BlockPos(finalPoint.x, finalPoint.y, finalPoint.z)) == null) {
					this.isScurrying = false;
				} else {
					this.entity.getNavigator().setPath(this.path, 1.75f);
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
					this.lastDistance = distance;
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
		}
	}
}
