package thebetweenlands.common.entity.mobs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.entity.EntityShockwaveBlock;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.world.gen.biome.decorator.SurfaceType;
import thebetweenlands.common.world.gen.feature.tree.WorldGenSpiritTreeStructure;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.LocationSpiritTree;

public class EntitySpiritTreeFaceLarge extends EntitySpiritTreeFace {
	public static final byte EVENT_ATTACKED = 2;
	public static final byte EVENT_BLOW_ATTACK = 40;

	private static final DataParameter<Integer> BLOW_STATE = EntityDataManager.createKey(EntitySpiritTreeFaceLarge.class, DataSerializers.VARINT);

	private int blowTicks = 0;

	private int waveTicks = 0;

	public EntitySpiritTreeFaceLarge(World world) {
		super(world);
		this.setSize(1.8F, 1.8F);
	}

	@Override
	protected void initEntityAI() {
		super.initEntityAI();

		this.tasks.addTask(0, new AITrackTarget(this));
		this.tasks.addTask(2, new AISpit(this));
		this.tasks.addTask(3, new AIBlowAttack(this));
		this.tasks.addTask(4, new AIWaveAttack(this));
		this.tasks.addTask(5, new AIRespawnSmallFaces(this));
	}

	@Override
	protected void entityInit() {
		super.entityInit();

		this.dataManager.register(BLOW_STATE, 0);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();

		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(8.0D);
		//this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(200.0D);
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.SPIRIT_TREE_FACE_LARGE;
	}

	@Override
	public List<BlockPos> findNearbyWoodBlocks() {
		List<LocationSpiritTree> locations = BetweenlandsWorldStorage.forWorld(this.world).getLocalStorageHandler().getLocalStorages(LocationSpiritTree.class, this.getEntityBoundingBox(), loc -> loc.isInside(this));
		if(!locations.isEmpty()) {
			List<BlockPos> positions = locations.get(0).getLargeFacePositions();
			if(!positions.isEmpty()) {
				return positions;
			}
		}
		return super.findNearbyWoodBlocks();
	}

	protected List<BlockPos> findSmallFacesBlocks() {
		List<LocationSpiritTree> locations = BetweenlandsWorldStorage.forWorld(this.world).getLocalStorageHandler().getLocalStorages(LocationSpiritTree.class, this.getEntityBoundingBox(), loc -> loc.isInside(this));
		if(!locations.isEmpty()) {
			List<BlockPos> positions = locations.get(0).getSmallFacePositions();
			if(!positions.isEmpty()) {
				return positions;
			}
		}
		return super.findNearbyWoodBlocks();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void handleStatusUpdate(byte id) {
		super.handleStatusUpdate(id);

		if(id == EVENT_ATTACKED) {
			Vec3d frontCenter = this.getFrontCenter();
			for(int i = 0; i < 16; i++) {
				float rx = this.world.rand.nextFloat() * 2.0F - 1.0F;
				float ry = this.world.rand.nextFloat() * 2.0F - 1.0F;
				float rz = this.world.rand.nextFloat() * 2.0F - 1.0F;
				Vec3d vec = new Vec3d(rx, ry, rz);
				vec = vec.normalize();
				this.world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, frontCenter.x + rx, frontCenter.y + ry, frontCenter.z + rz, vec.x * 1.5F, vec.y * 1.5F, vec.z * 1.5F, Block.getIdFromBlock(BlockRegistry.LOG_SPIRIT_TREE));
			}
		} else if(id == EVENT_BLOW_ATTACK) {
			Vec3d frontCenter = this.getFrontCenter();
			for(int i = 0; i < 64; i++) {
				Random rnd = world.rand;
				float rx = rnd.nextFloat() * 6.0F - 3.0F + this.getFacing().getFrontOffsetX() * 2;
				float ry = rnd.nextFloat() * 6.0F - 3.0F + this.getFacing().getFrontOffsetY() * 2;
				float rz = rnd.nextFloat() * 6.0F - 3.0F + this.getFacing().getFrontOffsetZ() * 2;
				Vec3d vec = new Vec3d(rx, ry, rz);
				vec = vec.normalize();
				BLParticles.ROOT_SPIKE.spawn(world, frontCenter.x, frontCenter.y - 0.25D, frontCenter.z, ParticleArgs.get().withMotion(vec.x * 0.45F, vec.y * 0.45F + 0.2F, vec.z * 0.45F));
			}
			frontCenter = this.getFrontCenter();
			for(int i = 0; i < 32; i++) {
				Random rnd = world.rand;
				float rx = rnd.nextFloat() - 0.5F + this.getFacing().getFrontOffsetX() * 0.5F;
				float ry = rnd.nextFloat() - 0.5F + this.getFacing().getFrontOffsetY() * 0.5F;
				float rz = rnd.nextFloat() - 0.5F + this.getFacing().getFrontOffsetZ() * 0.5F;
				Vec3d vec = new Vec3d(rx, ry, rz);
				vec = vec.normalize();
				BLParticles.MOTION_ITEM_BREAKING.spawn(world, frontCenter.x, frontCenter.y - 0.25D, frontCenter.z, ParticleArgs.get().withMotion(vec.x * 0.25F, vec.y * 0.25F, vec.z * 0.25F).withData(ItemRegistry.SAP_SPIT, 0));
			}
		}
	}

	@Override
	public void onDeath(DamageSource cause) {
		super.onDeath(cause);

		if(!this.world.isRemote) {
			List<LocationSpiritTree> locations = BetweenlandsWorldStorage.forWorld(this.world).getLocalStorageHandler().getLocalStorages(LocationSpiritTree.class, this.getEntityBoundingBox(), loc -> loc.isInside(this));
			if(!locations.isEmpty()) {
				LocationSpiritTree location = locations.get(0);

				List<EntitySpiritTreeFaceSmall> smallFaces = this.world.getEntitiesWithinAABB(EntitySpiritTreeFaceSmall.class, location.getEnclosingBounds());
				for(EntitySpiritTreeFaceSmall face : smallFaces) {
					face.setDropItemsWhenDead(false);
					face.onKillCommand();
				}

				List<BlockPos> positions = location.getLargeFacePositions();
				BlockPos lowest = null;
				for(BlockPos pos : positions) {
					if(lowest == null || lowest.getY() > pos.getY()) {
						lowest = pos;
					}
				}
				if(lowest != null) {
					int radius = 5;
					for(int xo = -radius; xo <= radius; xo++) {
						for(int yo = -radius; yo <= radius; yo++) {
							for(int zo = -radius; zo <= radius; zo++) {
								if(xo*xo + yo*yo + zo*zo <= radius*radius) {
									BlockPos pos = lowest.add(xo, yo, zo);
									IBlockState state = this.world.getBlockState(pos);

									if(SurfaceType.GRASS_AND_DIRT.matches(state) && !this.world.getBlockState(pos.up()).isNormalCube() && this.world.rand.nextInt(3) == 0) {
										this.world.setBlockState(pos, BlockRegistry.SPREADING_SLUDGY_DIRT.getDefaultState());
									}

									if(state.getBlock() == BlockRegistry.LOG_SPIRIT_TREE && this.world.rand.nextInt(5) == 0) {
										this.world.setBlockState(pos, BlockRegistry.LOG_SPREADING_ROTTEN_BARK.getDefaultState());
									}
								}
							}
						}
					}
				}

				for(BlockPos pos : location.getSmallFacePositions()) {
					IBlockState state = this.world.getBlockState(pos);
					if(state.getBlock() == BlockRegistry.LOG_SPIRIT_TREE && this.world.rand.nextInt(10) == 0) {
						this.world.setBlockState(pos, BlockRegistry.LOG_SPREADING_ROTTEN_BARK.getDefaultState());
					}
				}

				location.getGuard().clear(this.world);
				location.setVisible(false);
				location.setDirty(true);
			}
		}
	}

	@Override
	public boolean isAttacking() {
		return super.isAttacking() || this.blowTicks > 0 || this.waveTicks > 0;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if(!this.world.isRemote) {
			if(this.blowTicks > 0) {
				if(this.blowTicks > 20) {
					this.dataManager.set(BLOW_STATE, 2);

					if((this.ticksExisted - 1) % 15 == 0) {
						this.doBlowAttack();
						this.world.setEntityState(this, EVENT_BLOW_ATTACK);
					}
				} else {
					this.dataManager.set(BLOW_STATE, 1);
				}

				if(this.blowTicks > 160) {
					this.dataManager.set(BLOW_STATE, 0);
					this.blowTicks = 0;
				} else {
					this.blowTicks++;
				}
			}

			if(this.waveTicks > 0) {
				if((this.waveTicks - 1) % 2 == 0) {
					double increment = Math.PI * 2 / 30;
					double a1 = this.waveTicks / 2 * increment;
					double a2 = (this.waveTicks / 2 + 1) * increment;

					List<BlockPos> blocks = this.getSegment(this.getAnchor(), a1, a2, WorldGenSpiritTreeStructure.RADIUS_INNER_CIRLCE, WorldGenSpiritTreeStructure.RADIUS_OUTER_CIRCLE + 1, false, new ArrayList<>());

					MutableBlockPos checkPos = new MutableBlockPos();

					for(BlockPos pos : blocks) {
						BlockPos spawnPos = null;

						for(int yo = 0; yo < 8; yo++) {
							checkPos.setPos(pos.getX(), pos.getY() - yo, pos.getZ());

							IBlockState state = this.world.getBlockState(checkPos);

							if(state.getCollisionBoundingBox(world, checkPos) != null) {
								if(state.isSideSolid(world, checkPos, EnumFacing.UP)) {
									spawnPos = checkPos.toImmutable();
								}
								break;
							}
						}

						if(spawnPos != null) {
							EntityShockwaveBlock shockwaveBlock = new EntityShockwaveBlock(world);
							shockwaveBlock.setOrigin(spawnPos, 1, pos.getX() + 0.5D, pos.getZ() + 0.5D, this);
							shockwaveBlock.setLocationAndAngles(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D, 0.0F, 0.0F);
							shockwaveBlock.setBlock(this.world.getBlockState(spawnPos).getBlock(), this.world.getBlockState(spawnPos).getBlock().getMetaFromState(this.world.getBlockState(spawnPos)));
							this.world.spawnEntity(shockwaveBlock);
						}
					}
				}

				if(this.waveTicks > 2 * 30 * 3) {
					this.waveTicks = 0;
				} else {
					this.waveTicks++;
				}
			}
		} else {
			int blowState = this.dataManager.get(BLOW_STATE);
			if(blowState == 1) {
				Vec3d frontCenter = this.getFrontCenter();
				for(int i = 0; i < 4; i++) {
					Random rnd = world.rand;
					float rx = rnd.nextFloat() * 4.0F - 2.0F + this.getFacing().getFrontOffsetX() * 4;
					float ry = rnd.nextFloat() * 4.0F - 2.0F + this.getFacing().getFrontOffsetY() * 4;
					float rz = rnd.nextFloat() * 4.0F - 2.0F + this.getFacing().getFrontOffsetZ() * 4;
					Vec3d vec = new Vec3d(rx, ry, rz);
					vec = vec.normalize();
					this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, frontCenter.x + rx, frontCenter.y - 0.75D + ry, frontCenter.z + rz, -vec.x * 0.5F, -vec.y * 0.5F, -vec.z * 0.5F);
				}
			}
		}
	}

	public boolean isTargetInBlowRange(EntityLivingBase target) {
		if(target.getDistanceSq(this) <= 5 * 5) {
			Vec3d center = this.getFrontCenter();
			Vec3d targetCenter = new Vec3d(target.posX, target.posY + target.getEyeHeight(), target.posZ);
			Vec3d dir = targetCenter.subtract(center).normalize();
			Vec3d facing = new Vec3d(this.getFacing().getFrontOffsetX(), this.getFacing().getFrontOffsetY(), this.getFacing().getFrontOffsetZ());
			float angle = (float)(Math.acos(facing.dotProduct(dir)) / 2 / Math.PI) * 360.0F;
			return angle >= 0.0F && angle <= 90.0F;
		}
		return false;
	}

	public void doBlowAttack() {
		Vec3d frontCenter = this.getFrontCenter();
		List<EntityLivingBase> targets = this.world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(frontCenter.x - 5, frontCenter.y - 5, frontCenter.z - 5, frontCenter.x + 5, frontCenter.y + 5, frontCenter.z + 5));
		for(EntityLivingBase target : targets) {
			if(target != this && this.isTargetInBlowRange(target)) {
				this.attackEntityAsMob(target);
			}
		}
	}

	public void startBlowAttack() {
		this.blowTicks = 1;
	}

	public boolean isTargetInWaveAttackRange(EntityLivingBase target) {
		double dx = this.posX - target.posX;
		double dz = this.posZ - target.posZ;
		double dstSq = dx*dx + dz*dz;
		int innerSq = WorldGenSpiritTreeStructure.RADIUS_INNER_CIRLCE * WorldGenSpiritTreeStructure.RADIUS_INNER_CIRLCE;
		int outerSq = WorldGenSpiritTreeStructure.RADIUS_OUTER_CIRCLE * WorldGenSpiritTreeStructure.RADIUS_OUTER_CIRCLE;
		return dstSq >= innerSq && dstSq <= outerSq;
	}

	public void startWaveAttack() {
		this.waveTicks = 1;
	}

	protected List<BlockPos> getSegment(BlockPos pos, double a1, double a2, double innerRadius, double outerRadius, boolean includeCenter, List<BlockPos> list) {
		final double twoPi = Math.PI * 2;
		final double halfPi = Math.PI / 2;

		a1 %= twoPi;
		if(a1 < 0) a1 += twoPi;

		a2 %= twoPi;
		if(a2 < 0) a2 += twoPi;

		int qa1 = MathHelper.floor(a1 / halfPi);

		a1 -= qa1 * halfPi;
		a2 -= qa1 * halfPi;

		double radiusSq = outerRadius * outerRadius;

		int rotation = (4 - qa1) % 4;
		int maxRot = MathHelper.floor(a2 / halfPi);

		for(int rot = 0; rot <= maxRot; rot++) {
			double ca1 = rot == 0 ? a1 : 0;
			double ca2 = rot == maxRot ? (a2 % halfPi) : halfPi;

			double cos1 = Math.cos(ca1);
			double tan1 = Math.tan(ca1);
			double tan2 = Math.tan(ca2);

			int minX = 0;
			int maxX = MathHelper.ceil(cos1 * outerRadius);

			for(int xo = minX; xo <= maxX; xo++) {
				double dxSq = (xo - 0.5D) * (xo - 0.5D);

				int minZ = MathHelper.floor(tan1 * xo);
				int maxZ = MathHelper.floor(Math.min(tan2 * xo, outerRadius));

				if(xo <= innerRadius) {
					minZ += MathHelper.ceil(innerRadius - Math.sqrt(xo * xo + minZ * minZ));
				}

				for(int zo = minZ; zo < maxZ; zo++) {
					double dstSq = dxSq + (zo + 0.5D) * (zo + 0.5D);

					if(dstSq >= radiusSq) {
						break;
					}

					if(dstSq <= innerRadius*innerRadius) {
						continue;
					}

					int nx;
					switch(rotation) {
					default:
					case 0:
						nx = xo - 1;
						break;
					case 1:
						nx = zo;
						break;
					case 2:
						nx = -xo + 1;
						break;
					case 3:
						nx = -zo;
						break;
					}

					int nz;
					switch(rotation) {
					default:
					case 0:
						nz = zo;
						break;
					case 1:
						nz = -xo + 1;
						break;
					case 2:
						nz = -zo;
						break;
					case 3:
						nz = xo - 1;
						break;
					}

					if(!includeCenter && nx == 0 && nz == 0) {
						continue;
					}

					list.add(pos.add(nx, 0, nz));
				}
			}

			rotation = rotation - 1;
			if(rotation < 0) {
				rotation += 4;
			}
		}

		return list;
	}

	public static class AIRespawnSmallFaces extends EntityAIBase {
		protected final EntitySpiritTreeFaceLarge entity;

		protected int executeCheckCooldown = 0;

		protected boolean shouldContinue = true;

		protected int spawnCooldown = 0;

		public AIRespawnSmallFaces(EntitySpiritTreeFaceLarge entity) {
			this.entity = entity;
		}

		protected int countSmallFaces() {
			return this.entity.world.getEntitiesWithinAABB(EntitySpiritTreeFaceSmall.class, this.entity.getEntityBoundingBox().grow(40.0D)).size();
		}

		protected boolean hasEnoughSmallFaces() {
			return this.countSmallFaces() >= 8;
		}

		@Override
		public boolean shouldExecute() {
			if(this.executeCheckCooldown <= 0) {
				this.executeCheckCooldown = 20 + this.entity.rand.nextInt(20);
				return !this.hasEnoughSmallFaces();
			}
			this.executeCheckCooldown--;
			return false;
		}

		@Override
		public void startExecuting() {
			this.shouldContinue = true;
		}

		@Override
		public void updateTask() {
			if(this.spawnCooldown <= 0) {
				this.spawnCooldown = 180 + this.entity.rand.nextInt(100);
				List<BlockPos> blocks = this.entity.findSmallFacesBlocks();

				if(!blocks.isEmpty()) {
					EntitySpiritTreeFaceSmall face = new EntitySpiritTreeFaceSmall(this.entity.world);

					spawnLoop: for(int i = 0; i < 16; i++) {
						BlockPos anchor = blocks.get(this.entity.rand.nextInt(blocks.size()));

						List<EnumFacing> facings = new ArrayList<>();
						facings.addAll(Arrays.asList(EnumFacing.VALUES));
						Collections.shuffle(facings, this.entity.rand);

						for(EnumFacing facing : facings) {
							EnumFacing facingUp = facing.getAxis().isVertical() ? EnumFacing.HORIZONTALS[this.entity.rand.nextInt(EnumFacing.HORIZONTALS.length)] : EnumFacing.UP;
							if(face.canAnchorAt(anchor, facing, facingUp)) {
								face.onInitialSpawn(this.entity.world.getDifficultyForLocation(anchor), null);
								face.setPositionToAnchor(anchor, facing, facingUp);
								this.entity.world.spawnEntity(face);
								break spawnLoop;
							}
						}
					}

					if(this.hasEnoughSmallFaces()) {
						this.shouldContinue = false;
					}
				}
			}
			this.spawnCooldown--;
		}

		@Override
		public boolean shouldContinueExecuting() {
			return this.shouldContinue;
		}
	}

	public static class AIBlowAttack extends EntityAIBase {
		protected final EntitySpiritTreeFaceLarge entity;

		protected int cooldown = 30;

		public AIBlowAttack(EntitySpiritTreeFaceLarge entity) {
			this.entity = entity;
		}

		@Override
		public boolean shouldExecute() {
			if(!this.entity.isAttacking() && this.entity.getAttackTarget() != null && this.entity.isTargetInBlowRange(this.entity.getAttackTarget())) {
				if(this.cooldown <= 0) {
					this.cooldown = 30 + this.entity.rand.nextInt(30);
					return true;
				}
				this.cooldown--;
			}
			return false;
		}

		@Override
		public void startExecuting() {
			this.entity.startBlowAttack();
		}

		@Override
		public boolean shouldContinueExecuting() {
			return false;
		}
	}

	public static class AIWaveAttack extends EntityAIBase {
		protected final EntitySpiritTreeFaceLarge entity;

		protected int cooldown = 30;

		public AIWaveAttack(EntitySpiritTreeFaceLarge entity) {
			this.entity = entity;
		}

		@Override
		public boolean shouldExecute() {
			if(!this.entity.isAttacking() && this.entity.getAttackTarget() != null && this.entity.isTargetInWaveAttackRange(this.entity.getAttackTarget())) {
				if(this.cooldown <= 0) {
					this.cooldown = 60 + this.entity.rand.nextInt(80);
					return true;
				}
				this.cooldown--;
			}
			return false;
		}

		@Override
		public void startExecuting() {
			this.entity.startWaveAttack();
		}

		@Override
		public boolean shouldContinueExecuting() {
			return false;
		}
	}
}
