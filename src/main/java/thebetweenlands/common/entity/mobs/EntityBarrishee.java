package thebetweenlands.common.entity.mobs;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.api.entity.IEntityScreenShake;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.block.container.BlockLootUrn;
import thebetweenlands.common.block.container.BlockMudBrickAlcove;
import thebetweenlands.common.block.misc.BlockSulfurTorch;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.tile.TileEntityMudBrickAlcove;
import thebetweenlands.common.world.gen.feature.structure.utils.SludgeWormMazeBlockHelper;

//TODO Loot tables
public class EntityBarrishee extends EntityMob implements IEntityScreenShake, IEntityBL {

	private static final DataParameter<Boolean> AMBUSH_SPAWNED = EntityDataManager.createKey(EntityBarrishee.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> SCREAM = EntityDataManager.createKey(EntityBarrishee.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> SCREAM_TIMER = EntityDataManager.createKey(EntityBarrishee.class, DataSerializers.VARINT);
	public float standingAngle, prevStandingAngle;

	private SludgeWormMazeBlockHelper blockHelper = new SludgeWormMazeBlockHelper();
	public final Map<IBlockState, Boolean> BREAKABLE_BLOCKS = new HashMap<IBlockState, Boolean>();
	public final Map<IBlockState, Boolean> CANCEL_DROP_ITEMS = new HashMap<IBlockState, Boolean>();

	//Scream timer is only used for the screen shake and is client side only.
	private int prevScreamTimer;
	public int screamTimer;
	private boolean screaming;

	//Adjust to length of screaming sound
	private static final int SCREAMING_TIMER_MAX = 50;

	public EntityBarrishee(World world) {
		super(world);
		setSize(2.25F, 1.8F);
		initAOEBreakableBlockMap();
		initCancelBlockItemDropsMap(); // using map for this atm because I don't know if we'll add a load of stuff or not
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(AMBUSH_SPAWNED, false);
		dataManager.register(SCREAM, false);
		dataManager.register(SCREAM_TIMER, 50);
	}
	
	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.BARRISHEE;
	}

	public boolean isAmbushSpawn() {
		return dataManager.get(AMBUSH_SPAWNED);
	}

	public void setIsAmbushSpawn(boolean is_ambush) {
		dataManager.set(AMBUSH_SPAWNED, is_ambush);
	}

	public void setIsScreaming(boolean scream) {
		dataManager.set(SCREAM, scream);
	}

	public boolean isScreaming() {
		return dataManager.get(SCREAM);
	}

	public void setScreamTimer(int scream_timer) {
		dataManager.set(SCREAM_TIMER, scream_timer);
	}

	public int getScreamTimer() {
		return dataManager.get(SCREAM_TIMER);
	}

	@Override
	protected void initEntityAI() {
		tasks.addTask(1, new EntityAISwimming(this));
		tasks.addTask(2, new EntityBarrishee.AIBarrisheeAttack(this));
		tasks.addTask(3, new EntityAIWander(this, 0.4D, 20));
		//tasks.addTask(4, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		//tasks.addTask(5, new EntityAILookIdle(this));
		targetTasks.addTask(0, new EntityAINearestAttackableTarget<>(this, EntityZombie.class, 0, true, true, null));
		targetTasks.addTask(3, new EntityAIHurtByTarget(this, true, new Class[0]));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.6D);
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(200D);
		getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1.0D);
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D);
		getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.75D);
	}

	@Override
	public boolean getCanSpawnHere() {
		return super.getCanSpawnHere();
	}

	@Override
    public boolean isNotColliding() {
        return !getEntityWorld().containsAnyLiquid(getEntityBoundingBox()) && getEntityWorld().getCollisionBoxes(this, getEntityBoundingBox()).isEmpty() && getEntityWorld().checkNoEntityCollision(getEntityBoundingBox(), this);
    }

	@Override
	public int getMaxSpawnedInChunk() {
		return 1;
	}

    @SideOnly(Side.CLIENT)
    public float smoothedAngle(float partialTicks) {
        return prevStandingAngle + (standingAngle - prevStandingAngle) * partialTicks;
    }

	@Override
	public void onLivingUpdate() {
		//Test Scream remove once testing is over
		if(getEntityWorld().getTotalWorldTime()%200 == 0)
			setScreamTimer(0);

		if (getEntityWorld().isRemote) {
			prevStandingAngle = standingAngle;

			if (isAmbushSpawn() && standingAngle <= 0.1F)
				standingAngle += 0.01F;
			if (isAmbushSpawn() && standingAngle > 0.1F && standingAngle <= 1F)
				standingAngle += 0.1F;

			if (isAmbushSpawn() && standingAngle > 1F)
				standingAngle = 1F;
		}

		prevScreamTimer = getScreamTimer();
		if (!getEntityWorld().isRemote) {
			if (getScreamTimer() == 0) {
				setIsScreaming(true);
				setScreamTimer(1);
			}

			if (getScreamTimer() > 0 && getScreamTimer() <= SCREAMING_TIMER_MAX) {
				setScreamTimer(getScreamTimer() + 1);
			}

			if (getScreamTimer() >= SCREAMING_TIMER_MAX)
				setIsScreaming(false);
			else
				setIsScreaming(true);
		}

		//TODO Disabled particles until second scream is made for AI
		/*
		 if(this.world.isRemote && this.isScreaming() && getScreamTimer() <= 40)
			this.spawnScreamParticles();
		*/

		if(!this.world.isRemote && this.isScreaming() && getScreamTimer() >= 25)
			checkCollisionsForAOEScream(getAOEScreamBounds());
		
		super.onLivingUpdate();
	}
	
	private void checkCollisionsForAOEScream(AxisAlignedBB aoeScreamBounds) {
		int minX = MathHelper.floor(aoeScreamBounds.minX);
		int minY = MathHelper.floor(aoeScreamBounds.minY);
		int minZ = MathHelper.floor(aoeScreamBounds.minZ);
		int maxX = MathHelper.floor(aoeScreamBounds.maxX);
		int maxY = MathHelper.floor(aoeScreamBounds.maxY);
		int maxZ = MathHelper.floor(aoeScreamBounds.maxZ);

		for (int sizeX = minX; sizeX <= maxX; ++sizeX)
			for (int sizeZ = minZ; sizeZ <= maxZ; ++sizeZ)
				for (int sizeY = minY; sizeY <= maxY; ++sizeY) {
					BlockPos pos = new BlockPos(sizeX, sizeY, sizeZ);
					IBlockState state = getEntityWorld().getBlockState(pos);
					if (isAOEBreakableBlock(state)) {
						if (state.getBlock() instanceof BlockLootUrn)
							spawnAshSpriteMinion(getEntityWorld(), pos, state);
						if (state.getBlock() instanceof BlockMudBrickAlcove) {
							if (checkAlcoveForUrn(getEntityWorld(), state)) {
								setAlcoveUrnEmpty(getEntityWorld(), pos, state);
							}
						} else
							getEntityWorld().destroyBlock(pos, !dropItems(state));
					}
				}
	}

	@Nullable
	public static TileEntityMudBrickAlcove getTileEntity(IBlockAccess world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileEntityMudBrickAlcove) {
			return (TileEntityMudBrickAlcove) tile;
		}
		return null;
	}

	private void setAlcoveUrnEmpty(World world, BlockPos pos, IBlockState state) {
		BlockPos offsetPos = pos;
		TileEntityMudBrickAlcove tile = getTileEntity(world, pos);
		if (tile instanceof TileEntityMudBrickAlcove) {
			if (tile.has_urn) {
				IInventory tileInv = (IInventory) tile;
				if (tileInv != null) {
					EnumFacing facing = state.getValue(BlockMudBrickAlcove.FACING);
					offsetPos = pos.offset(facing);
					InventoryHelper.dropInventoryItems(world, offsetPos, tileInv);
				}
				spawnAshSpriteMinion(getEntityWorld(), pos, state);
				world.playEvent(null, 2001, pos, Block.getIdFromBlock(state.getBlock()));
				tile.has_urn = false;
				world.notifyBlockUpdate(pos, state, state, 2);
			}
		}
	}

	private void spawnAshSpriteMinion(World world, BlockPos pos, IBlockState state) {
		BlockPos offsetPos = pos;
		if (state instanceof BlockMudBrickAlcove) {
			EnumFacing facing = state.getValue(BlockMudBrickAlcove.FACING);
			offsetPos = pos.offset(facing);
		}
		EntityAshSprite entity = new EntityAshSprite(world);
		entity.setLocationAndAngles(offsetPos.getX() + 0.5D, offsetPos.getY(), offsetPos.getZ() + 0.5D, 0.0F, 0.0F);
		entity.setBoundOrigin(offsetPos);
		world.spawnEntity(entity);
	}

	private boolean checkAlcoveForUrn(World entityWorld, IBlockState state) {
		return state.getValue(BlockMudBrickAlcove.HAS_URN);	
	}

	public boolean isAOEBreakableBlock(IBlockState state) {
		return BREAKABLE_BLOCKS.get(state) != null;
	}

	public boolean dropItems(IBlockState state) {
		return CANCEL_DROP_ITEMS.get(state) != null;
	}

	@SideOnly(Side.CLIENT)
	protected void spawnScreamParticles() {
		Vec3d look = this.getLookVec();
		float speed = 0.6f;
		Particle particle = BLParticles.SONIC_SCREAM.create(this.world, this.posX, this.posY + (getScreamTimer() < 25 ? 0.8 + (getScreamTimer() * 0.0125F) : 1.25 - (25 - getScreamTimer()) * 0.025F), this.posZ, 
				ParticleArgs.get().withMotion(look.x * speed, look.y * speed, look.z * speed).withScale(10).withData(30, MathHelper.floor(this.ticksExisted * 3.3f))
				.withColor(1.0f, 0.9f, 0.8f, 1.0f));
		BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING, particle);
	}
	
	@Override
	protected boolean isMovementBlocked() {
		return super.isMovementBlocked() || isScreaming();// || true;
	}

	public float getScreamingProgress(float delta) {
		return 1.0F / SCREAMING_TIMER_MAX * (prevScreamTimer + (screamTimer - prevScreamTimer) * delta);
	}

	public AxisAlignedBB getAOEScreamBounds() {
		float boxsizeUnit = 0.0275F * getScreamTimer();
		AxisAlignedBB bounds = getEntityBoundingBox();
		return bounds.grow(boxsizeUnit, 0, boxsizeUnit);
	}

	@Override
	public float getShakeIntensity(Entity viewer, float partialTicks) {
		if(isScreaming()) {
			double dist = getDistance(viewer);
			float screamMult = (float) (1.0F - dist / 30.0F);
			if(dist >= 30.0F) {
				return 0.0F;
			}
			return (float) ((Math.sin(getScreamingProgress(partialTicks) * Math.PI) + 0.1F) * 0.15F * screamMult);
		} else {
			return 0.0F;
		}
	}

	static class AIBarrisheeAttack extends EntityAIAttackMelee {

		public AIBarrisheeAttack(EntityBarrishee barrishee) {
			super(barrishee, 0.4D, false);
		}

		@Override
		protected double getAttackReachSqr(EntityLivingBase attackTarget) {
			return (double) (4.0F + attackTarget.width);
		}
	}

	//TODO - may want to move to a config one day - add blockstates to break here.
	private void initAOEBreakableBlockMap() {
		if (BREAKABLE_BLOCKS.isEmpty()) {
			BREAKABLE_BLOCKS.put(blockHelper.ROOT, true);
			BREAKABLE_BLOCKS.put(blockHelper.MUD_BRICKS_ALCOVE_NORTH, true);
			BREAKABLE_BLOCKS.put(blockHelper.MUD_BRICKS_ALCOVE_EAST, true);
			BREAKABLE_BLOCKS.put(blockHelper.MUD_BRICKS_ALCOVE_SOUTH, true);
			BREAKABLE_BLOCKS.put(blockHelper.MUD_BRICKS_ALCOVE_WEST, true);
			BREAKABLE_BLOCKS.put(BlockRegistry.SULFUR_TORCH.getDefaultState(), true);
			for (EnumFacing facing : EnumFacing.HORIZONTALS) {
				BREAKABLE_BLOCKS.put(BlockRegistry.SULFUR_TORCH.getDefaultState().withProperty(BlockSulfurTorch.FACING, facing), true);
				BREAKABLE_BLOCKS.put(blockHelper.LOOT_URN_1.withProperty(BlockLootUrn.FACING, facing), true);
				BREAKABLE_BLOCKS.put(blockHelper.LOOT_URN_2.withProperty(BlockLootUrn.FACING, facing), true);
				BREAKABLE_BLOCKS.put(blockHelper.LOOT_URN_3.withProperty(BlockLootUrn.FACING, facing), true);
			}
		}
	}

	private void initCancelBlockItemDropsMap() {
		if (CANCEL_DROP_ITEMS.isEmpty()) {
			CANCEL_DROP_ITEMS.put(blockHelper.ROOT, true);
		}
	}

}