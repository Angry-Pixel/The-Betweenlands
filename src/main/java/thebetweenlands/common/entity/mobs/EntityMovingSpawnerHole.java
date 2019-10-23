package thebetweenlands.common.entity.mobs;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.registries.LootTableRegistry;

//TODO Loot tables
public class EntityMovingSpawnerHole extends EntityMovingWallFace implements IMob {
	@SideOnly(Side.CLIENT)
	private TextureAtlasSprite wallSprite;

	protected int spawnCount = 3;
	protected int spawnCooldown = 0;

	protected double maxTargetRange = 6;
	protected double countCheckRange = 8;
	protected int maxCount = 5;

	public EntityMovingSpawnerHole(World world) {
		super(world);
		this.lookMoveSpeedMultiplier = 4.0F;
		this.experienceValue = 5;
	}

	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
		this.spawnCount = 2 + this.rand.nextInt(3);
		return super.onInitialSpawn(difficulty, livingdata);
	}

	@Override
	protected void initEntityAI() {
		super.initEntityAI();

		this.targetTasks.addTask(0, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, 0, true, false, null).setUnseenMemoryTicks(120));

		this.tasks.addTask(0, new AITrackTarget<EntityMovingSpawnerHole>(this, true, 28.0D) {
			@Override
			protected boolean canMove() {
				return true;
			}
		});
		this.tasks.addTask(1, new AISpawnMob(this, 7, 18));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.08D);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if(!this.world.isRemote) {
			if(this.spawnCooldown > 0) {
				this.spawnCooldown--;
			}

			if(!this.isSpawningMob() && this.spawnCount <= 0) {
				this.setDead();
			}
		} else {
			this.updateWallSprite();
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setInteger("spawnCount", this.spawnCount);
		nbt.setInteger("spawnCooldown", this.spawnCooldown);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		if(nbt.hasKey("spawnCount", Constants.NBT.TAG_INT)) {
			this.spawnCount = nbt.getInteger("spawnCount");
		}
		if(nbt.hasKey("spawnCooldown", Constants.NBT.TAG_INT)) {
			this.spawnCooldown = nbt.getInteger("spawnCooldown");
		}
	}

	@SideOnly(Side.CLIENT)
	protected void updateWallSprite() {
		this.wallSprite = null;

		BlockPos pos = this.getPosition();

		IBlockState state = this.world.getBlockState(pos);
		state = state.getActualState(this.world, pos);

		if(state.isFullCube()) {
			this.wallSprite = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state);
		}
	}

	@Nullable
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite getWallSprite() {
		return this.wallSprite;
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.MOVING_SPAWNER_HOLE;
	}

	@Override
	public boolean canResideInBlock(BlockPos pos, EnumFacing facing, EnumFacing facingUp) {
		return this.isValidBlockForMovement(pos, this.world.getBlockState(pos)) && facing != EnumFacing.UP;
	}

	@Override
	protected boolean isValidBlockForMovement(BlockPos pos, IBlockState state) {
		return state.isOpaqueCube() && state.isNormalCube() && state.isFullCube() && state.getBlockHardness(this.world, pos) > 0 && state.getMaterial() == Material.ROCK;
	}

	@Override
	public Vec3d getOffset(float movementProgress) {
		return super.getOffset(1.0F);
	}

	public float getHoleDepthPercent(float partialTicks) {
		return this.getHalfMovementProgress(partialTicks);
	}

	protected Entity createEntity(Vec3d holeBottom, double frontOffset, EnumFacing facing) {
		Entity entity = new EntitySludgeWorm(this.world);
		entity.setLocationAndAngles(holeBottom.x, holeBottom.y, holeBottom.z, facing.getHorizontalAngle(), 0);
		entity.move(MoverType.SELF, facing.getXOffset() * 0.35D, facing == EnumFacing.UP ? 1 : 0, facing.getZOffset() * 0.35D);
		return entity;
	}

	protected Predicate<Entity> getNearbySpawnedEntitiesPredicate() {
		return entity -> entity instanceof EntitySludgeWorm;
	}

	public void startSpawningMob() {
		BlockPos pos = this.getPosition();
		Entity entity = this.createEntity(new Vec3d(pos.getX() + 0.5D, pos.getY() + 0.1D, pos.getZ() + 0.5D), 0.55D, this.getFacing());

		if(entity != null) {
			if(entity instanceof EntityLiving) {
				((EntityLiving) entity).onInitialSpawn(this.world.getDifficultyForLocation(new BlockPos(entity)), (IEntityLivingData) null);
			}
			this.world.spawnEntity(entity);
			this.world.playEvent(2004, this.getPosition(), 0);
		}

		this.spawnCount--;

		this.spawnCooldown = 40;
	}

	public boolean isSpawningMob() {
		return this.spawnCooldown > 0;
	}

	public boolean canSpawnMobs() {
		Entity target = this.getAttackTarget();
		if(target != null && !this.isSpawningMob() && this.getDistance(target) < this.maxTargetRange && this.canEntityBeSeen(target)) {
			Predicate<Entity> pred = this.getNearbySpawnedEntitiesPredicate();
			int others = this.world.getEntitiesWithinAABB(Entity.class, this.getEntityBoundingBox().grow(this.countCheckRange), pred).size();
			return others < this.maxCount;
		}
		return false;
	}

	protected static class AISpawnMob extends EntityAIBase {
		protected final EntityMovingSpawnerHole entity;

		protected int minSpawnCooldown, maxSpawnCooldown;
		protected int spawnCooldown;

		public AISpawnMob(EntityMovingSpawnerHole entity, int minSpawnCooldown, int maxSpawnCooldown) {
			this.entity = entity;
			this.minSpawnCooldown = minSpawnCooldown;
			this.maxSpawnCooldown = maxSpawnCooldown;
			this.spawnCooldown = minSpawnCooldown + entity.rand.nextInt(maxSpawnCooldown - minSpawnCooldown);
		}

		@Override
		public boolean shouldExecute() {
			if(--this.spawnCooldown <= 0) {
				this.spawnCooldown = this.minSpawnCooldown + this.entity.rand.nextInt(this.maxSpawnCooldown - this.minSpawnCooldown);
				return this.entity.isEntityAlive() && !this.entity.isMoving() && this.entity.isAnchored() && this.entity.canSpawnMobs();
			}
			return false;
		}

		@Override
		public void startExecuting() {
			this.entity.startSpawningMob();
		}

		@Override
		public boolean shouldContinueExecuting() {
			return false;
		}
	}
}
