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
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.world.gen.biome.decorator.SurfaceType;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.LocationSpiritTree;

public class EntitySpiritTreeFaceLarge extends EntitySpiritTreeFace {
	private static final DataParameter<Integer> BLOW_STATE = EntityDataManager.createKey(EntitySpiritTreeFaceLarge.class, DataSerializers.VARINT);

	private int blowTicks = 0;

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
		this.tasks.addTask(4, new AIRespawnSmallFaces(this));
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

		if(id == 2) {
			Vec3d frontCenter = this.getFrontCenter();
			for(int i = 0; i < 16; i++) {
				float rx = this.world.rand.nextFloat() * 2.0F - 1.0F;
				float ry = this.world.rand.nextFloat() * 2.0F - 1.0F;
				float rz = this.world.rand.nextFloat() * 2.0F - 1.0F;
				Vec3d vec = new Vec3d(rx, ry, rz);
				vec = vec.normalize();
				this.world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, frontCenter.x + rx, frontCenter.y + ry, frontCenter.z + rz, vec.x * 1.5F, vec.y * 1.5F, vec.z * 1.5F, Block.getIdFromBlock(BlockRegistry.LOG_SPIRIT_TREE));
			}
		} else if(id == 40) {
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
		return super.isAttacking() || this.blowTicks > 0;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if(!this.world.isRemote) {
			if(this.blowTicks > 0) {
				this.dataManager.set(BLOW_STATE, 1);
				this.blowTicks++;

				if(this.blowTicks > 30) {
					this.dataManager.set(BLOW_STATE, 2);
					if(this.ticksExisted % 20 == 0) {
						this.doBlowAttack();
						this.world.setEntityState(this, (byte)40);
					}
				}

				if(this.blowTicks > 160) {
					this.dataManager.set(BLOW_STATE, 0);
					this.blowTicks = 0;
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
}
