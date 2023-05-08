package thebetweenlands.common.entity.mobs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.block.plant.BlockMouldHornMushroom;
import thebetweenlands.common.entity.EntityFragSpore;
import thebetweenlands.common.entity.EntityShockwaveBlock;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityBigPuffshroom extends EntityLiving {

	public int cooldown = 0;
	public int prev_animation_1 = 0;
	public int prev_animation_2 = 0;
	public int prev_animation_3 = 0;
	public int prev_animation_4 = 0;
	public int renderTicks = 0;
	public int prev_renderTicks = 0;
	public int deathTicks;
	private static final byte SYNC_PREV_ANIMATION_DATA_MAX_1 = 101;
	private static final byte SYNC_PREV_ANIMATION_DATA_MAX_2 = 102;
	private static final byte SYNC_PREV_ANIMATION_DATA_MAX_3 = 103;
	private static final byte SYNC_PREV_ANIMATION_DATA_MAX_4 = 104;
	private static final byte SYNC_PREV_ANIMATION_DATA_MIN_1 = 105;
	private static final byte SYNC_PREV_ANIMATION_DATA_MIN_2 = 106;
	private static final byte SYNC_PREV_ANIMATION_DATA_MIN_3 = 107;
	private static final byte SYNC_PREV_ANIMATION_DATA_MIN_4 = 108;
	private static final DataParameter<Boolean> SLAM_ATTACK = EntityDataManager.createKey(EntityBigPuffshroom.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> MOVE = EntityDataManager.createKey(EntityBigPuffshroom.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> ACTIVE_1 = EntityDataManager.createKey(EntityBigPuffshroom.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> ACTIVE_2 = EntityDataManager.createKey(EntityBigPuffshroom.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> ACTIVE_3 = EntityDataManager.createKey(EntityBigPuffshroom.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> ACTIVE_4 = EntityDataManager.createKey(EntityBigPuffshroom.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> ACTIVE_5 = EntityDataManager.createKey(EntityBigPuffshroom.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> PAUSE = EntityDataManager.createKey(EntityBigPuffshroom.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> PAUSE_COUNT = EntityDataManager.createKey(EntityBigPuffshroom.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> ANIMATION_1 = EntityDataManager.createKey(EntityBigPuffshroom.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> ANIMATION_2 = EntityDataManager.createKey(EntityBigPuffshroom.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> ANIMATION_3 = EntityDataManager.createKey(EntityBigPuffshroom.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> ANIMATION_4 = EntityDataManager.createKey(EntityBigPuffshroom.class, DataSerializers.VARINT);


	public EntityBigPuffshroom(World world) {
		super(world);
		setSize(3F, 1.8F);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(SLAM_ATTACK, false);
		dataManager.register(MOVE, false);
		dataManager.register(ACTIVE_1, false);
		dataManager.register(ACTIVE_2, false);
		dataManager.register(ACTIVE_3, false);
		dataManager.register(ACTIVE_4, false);
		dataManager.register(ACTIVE_5, false);
		dataManager.register(PAUSE, true);
		dataManager.register(PAUSE_COUNT, 40);
		dataManager.register(ANIMATION_1, 0);
		dataManager.register(ANIMATION_2, 0);
		dataManager.register(ANIMATION_3, 0);
		dataManager.register(ANIMATION_4, 0);
	}

	public void setSlam(boolean state) {
		dataManager.set(SLAM_ATTACK, state);
	}

	public boolean getSlam() {
		return dataManager.get(SLAM_ATTACK);
	}

	public void setMove(boolean state) {
		dataManager.set(MOVE, state);
	}

	public boolean getMove() {
		return dataManager.get(MOVE);
	}

	public void setActive1(boolean state) {
		dataManager.set(ACTIVE_1, state);
	}

	public boolean getActive1() {
		return dataManager.get(ACTIVE_1);
	}

	public void setActive2(boolean state) {
		dataManager.set(ACTIVE_2, state);
	}

	public boolean getActive2() {
		return dataManager.get(ACTIVE_2);
	}

	public void setActive3(boolean state) {
		dataManager.set(ACTIVE_3, state);
	}

	public boolean getActive3() {
		return dataManager.get(ACTIVE_3);
	}

	public void setActive4(boolean state) {
		dataManager.set(ACTIVE_4, state);
	}

	public boolean getActive4() {
		return dataManager.get(ACTIVE_4);
	}

	public void setActive5(boolean state) {
		dataManager.set(ACTIVE_5, state);
	}

	public boolean getActive5() {
		return dataManager.get(ACTIVE_5);
	}

	public void setPause(boolean state) {
		dataManager.set(PAUSE, state);
	}

	public boolean getPause() {
		return dataManager.get(PAUSE);
	}

	public void setPauseCount(int count) {
		dataManager.set(PAUSE_COUNT, count);
	}

	public int getPauseCount() {
		return dataManager.get(PAUSE_COUNT);
	}

	public void setAnimation1(int count) {
		dataManager.set(ANIMATION_1, count);
	}

	public int getAnimation1() {
		return dataManager.get(ANIMATION_1);
	}

	public void setAnimation2(int count) {
		dataManager.set(ANIMATION_2, count);
	}

	public int getAnimation2() {
		return dataManager.get(ANIMATION_2);
	}

	public void setAnimation3(int count) {
		dataManager.set(ANIMATION_3, count);
	}

	public int getAnimation3() {
		return dataManager.get(ANIMATION_3);
	}

	public void setAnimation4(int count) {
		dataManager.set(ANIMATION_4, count);
	}

	public int getAnimation4() {
		return dataManager.get(ANIMATION_4);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0D);
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(50.0D);
		getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
	}

	@Override
	public void onUpdate() {
		if (getEntityWorld().isRemote) {
			prev_animation_1 = getAnimation1();
			prev_animation_2 = getAnimation2();
			prev_animation_3 = getAnimation3();
			prev_animation_4 = getAnimation4();
			prev_renderTicks = renderTicks;
		}

		if (!getEntityWorld().isRemote) {
			if (cooldown <= 0 && getEntityWorld().getTotalWorldTime() % 5 == 0)
				findEnemyToAttack();

			if (getActive1()) {
				if (getAnimation1() == 1) {
					breakShrooms(getPosition());
				}
				if (getAnimation1() <= 8)
					setAnimation1(getAnimation1() + 1);
				if (getAnimation1() > 8) {
					setAnimation1(8);
					getEntityWorld().setEntityState(this, SYNC_PREV_ANIMATION_DATA_MAX_1);
					setActive2(true);
					setActive1(false);
				}
			}

			if (getActive2()) {
				if (getAnimation2() <= 8)
					setAnimation2(getAnimation2() + 1);
				if (getAnimation2() == 8)
					setActive3(true);
				if (getAnimation2() > 8) {
					setAnimation2(8);
					getEntityWorld().setEntityState(this, SYNC_PREV_ANIMATION_DATA_MAX_2);
					setActive2(false);
				}
			}

			if (getActive3()) {
				if (getAnimation3() <= 8)
					setAnimation3(getAnimation3() + 1);
				if (getAnimation3() > 8) {
					setAnimation3(8);
					getEntityWorld().setEntityState(this, SYNC_PREV_ANIMATION_DATA_MAX_3);
					setActive3(false);
					setActive4(true);
				}
			}

			if (getActive4()) {
				if (getRangeToPlayer() > 5) {
					setSlam(false);
					if (getAnimation4() == 10) {
						if (rand.nextBoolean())
							spawnFragSpores();
						else
							spawnSporeMinions(world, getPosition());
						getEntityWorld().playSound(null, getPosition().getX() + 0.5D, getPosition().getY() + 1D, getPosition().getZ() + 0.5D, SoundRegistry.PUFF_SHROOM, SoundCategory.BLOCKS, 0.5F, 0.95F + getEntityWorld().rand.nextFloat() * 0.2F);
					}
				}

				if (getRangeToPlayer() <= 5) {
					setSlam(true);
					if (getAnimation4() == 10) {
						getEntityWorld().playSound(null, getPosition(), SoundRegistry.WALL_SLAM, SoundCategory.HOSTILE, 1F, 1F);
						slamAttack(world, getPosition());
					}
				}
				
				if (getAnimation4() <= 12)
					setAnimation4(getAnimation4() + 1);
				if (getAnimation4() > 12) {
					setAnimation4(12);
					getEntityWorld().setEntityState(this, SYNC_PREV_ANIMATION_DATA_MAX_4);
					setActive4(false);
				}
			}

			if (getPause()) {
				if (getAnimation4() >= 12) {
					if (getPauseCount() > 0)
						setPauseCount(getPauseCount() -1);
					if (getPauseCount() <= 0) {
						setPause(false);
						setPauseCount(40);
						setActive5(true);
					}
				}
			}

			if (getActive5() && getHealth() > 0) { // stop this if health <= 0
				setAnimation4(0);
				getEntityWorld().setEntityState(this, SYNC_PREV_ANIMATION_DATA_MIN_4);
				if (getAnimation1() >= 0)
					setAnimation3(getAnimation3() - 1);
				if (getAnimation3() <= 0)
					setAnimation2(getAnimation2() - 1);
				if (getAnimation2() <= 0)
					setAnimation1(getAnimation1() - 1);

				if (getAnimation3() <= 0) {
					setAnimation3(0);
					getEntityWorld().setEntityState(this, SYNC_PREV_ANIMATION_DATA_MIN_3);
				}
				if (getAnimation2() <= 0) {
					setAnimation2(0);
					getEntityWorld().setEntityState(this, SYNC_PREV_ANIMATION_DATA_MIN_2);
				}
				if (getAnimation1() <= 0) {
					setAnimation1(0);
					getEntityWorld().setEntityState(this, SYNC_PREV_ANIMATION_DATA_MIN_1);
					setActive5(false);
					setMove(true); 
					cooldown = 40;
				}
			}

			if (cooldown >= 0)
				cooldown--;
			if (cooldown < 0)
				cooldown = 0;

			if (getMove()) {
				BlockPos moveTo = new BlockPos(findNewSurfaceLocation(world, getPosition().down()));
				setPositionAndUpdate(moveTo.getX() + 0.5D, moveTo.getY() + 1, moveTo.getZ() + 0.5D);
				setMove(false);
			}
		}

		if (getEntityWorld().isRemote) {
			if (getActive4())
				if (getAnimation4() == 10)
					spawnSporeJetParticles();

			if (getActive1() || getActive5()) {
				if (getAnimation1() < 3) {
					double px = getPosition().getX() + 0.5D;
					double py = getPosition().getY() + 0.0625D;
					double pz = getPosition().getZ() + 0.5D;
					for (int i = 0, amount = 10 + getEntityWorld().rand.nextInt(4); i < amount; i++) {
						double ox = getEntityWorld().rand.nextDouble() * 2F - 1F;
						double oz = getEntityWorld().rand.nextDouble() * 2F - 1F;
						double motionX = getEntityWorld().rand.nextDouble() * 0.4F - 0.2F;
						double motionY = getEntityWorld().rand.nextDouble() * 0.3F + 0.075F;
						double motionZ = getEntityWorld().rand.nextDouble() * 0.4F - 0.2F;
						world.spawnParticle(EnumParticleTypes.BLOCK_DUST, px + ox, py, pz + oz, motionX, motionY, motionZ, Block.getStateId(BlockRegistry.MOULDY_SOIL.getDefaultState()));
					}
				}
			}
		}

		if (getEntityWorld().isRemote)
			renderTicks++;
		super.onUpdate();
	}

	@Override
	protected void onDeathUpdate() {
		// TODO add proper death animation
		++deathTicks;
		int i;
		int j;
		if (!getEntityWorld().isRemote) {
			setAnimation1(8);
			setAnimation2(8);
			setAnimation3(8);
			setAnimation4(12);
			if (deathTicks > 150 && deathTicks % 5 == 0) {
				i = 5;
				while (i > 0) {
					j = EntityXPOrb.getXPSplit(i);
					i -= j;
					getEntityWorld().spawnEntity(new EntityXPOrb(getEntityWorld(), posX, posY, posZ, j));
				}
			}

			// TODO REMOVE TEMP THING
			// just some random movement to show something is happening
			move(MoverType.SELF, rand.nextGaussian() * 0.02D - rand.nextGaussian() * 0.04D, rand.nextGaussian() * 0.32D, rand.nextGaussian() * 0.02D - rand.nextGaussian() * 0.04D);

			if (deathTicks == 200 && !getEntityWorld().isRemote) {
				i = 10;
				while (i > 0) {
					j = EntityXPOrb.getXPSplit(i);
					i -= j;
					getEntityWorld().spawnEntity(new EntityXPOrb(getEntityWorld(), posX, posY, posZ, j));
				}
				setDead();
			}
		}
	}

	private void breakShrooms(BlockPos pos) {
		for (int y = 6; y >= 0; y--) {
			for (int x = -1; x <= 1; x++) {
				for (int z = -1; z <= 1; z++) {
					if (getEntityWorld().getBlockState(pos.add(x, y, z)).getBlock() instanceof BlockMouldHornMushroom) {
						EntityFallingBlock falling_block = new EntityFallingBlock(world, pos.getX() + x + 0.5D, pos.getY() + y + 0.5D, pos.getZ() + z + 0.5D, getEntityWorld().getBlockState(pos.add(x, y, z)));
						falling_block.setPosition(pos.getX() + x + 0.5D, pos.getY() + y + 0.5D, pos.getZ() + z + 0.5D);
						double angle = Math.toRadians(y * 60D);
						double offSetX = Math.floor(-Math.sin(angle) * 3D);
						double offSetZ = Math.floor(Math.cos(angle) * 3D);
						Vec3d vector3d = new Vec3d(this.posX, this.posY, this.posZ);
						double velX = pos.getX() + offSetX * 3D - falling_block.posX;
						double velY = pos.getY() + 4D - falling_block.posY;
						double velZ = pos.getZ() + offSetZ * 3D - falling_block.posZ;
						double distanceSqRt = (double) MathHelper.sqrt(velX * velX + velY * velY + velZ * velZ);
						double accelerationX = velX / distanceSqRt * 0.3D + rand.nextDouble() * 0.2D;
						double accelerationY = velY / distanceSqRt * 0.5D + rand.nextDouble() * 0.5D;
						double accelerationZ = velZ / distanceSqRt * 0.3D + rand.nextDouble() * 0.2D;
						vector3d.add(accelerationX, accelerationY, accelerationZ).scale(0.95D);
						falling_block.addVelocity(accelerationX, accelerationY, accelerationZ);
						getEntityWorld().spawnEntity(falling_block);
						getEntityWorld().playEvent(null, 2001, pos.add(x, y, z), Block.getIdFromBlock(BlockRegistry.MOULD_HORN));
					}
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void handleStatusUpdate(byte id) {
		super.handleStatusUpdate(id);
		if (id == SYNC_PREV_ANIMATION_DATA_MAX_1)
			prev_animation_1 = 8;
		if (id == SYNC_PREV_ANIMATION_DATA_MAX_2)
			prev_animation_2 = 8;
		if (id == SYNC_PREV_ANIMATION_DATA_MAX_3)
			prev_animation_3 = 8;
		if (id == SYNC_PREV_ANIMATION_DATA_MAX_4)
			prev_animation_4 = 12;
		if (id == SYNC_PREV_ANIMATION_DATA_MIN_1)
			prev_animation_1 = 0;
		if (id == SYNC_PREV_ANIMATION_DATA_MIN_2)
			prev_animation_2 = 0;
		if (id == SYNC_PREV_ANIMATION_DATA_MIN_3)
			prev_animation_3 = 0;
		if (id == SYNC_PREV_ANIMATION_DATA_MIN_4)
			prev_animation_4 = 0;
	}

	private BlockPos findNewSurfaceLocation(World world, BlockPos pos) {
		List<BlockPos> allViableBlocks = new ArrayList<BlockPos>();
		List<BlockPos> posRandom = new ArrayList<BlockPos>();
		IBlockState mouldySoil = BlockRegistry.MOULDY_SOIL.getDefaultState();
		for (int x = -16; x <= 16; x++)
			for (int z = -16; z <= 16; z++)
				if (world.getBlockState(pos.add(x, 0, z)).getBlock() == mouldySoil.getBlock())
					allViableBlocks.add(pos.add(x, 0, z));

		if (!allViableBlocks.isEmpty())
			for (BlockPos goodBlocks : allViableBlocks)
				if (checkForAdjacentMouldyDirt(world, goodBlocks, 1, 1, mouldySoil))
					posRandom.add(goodBlocks);

		if (!posRandom.isEmpty()) {
			Collections.shuffle(posRandom);
			return posRandom.get(0);
		}
		return pos;
	}

	private boolean checkForAdjacentMouldyDirt(World world, BlockPos pos, int offSetXIn, int offSetZIn,
			IBlockState state) {
		int count = 0;
		for (int offsetX = -offSetXIn; offsetX <= offSetXIn; offsetX++)
			for (int offsetZ = -offSetZIn; offsetZ <= offSetZIn; offsetZ++)
				if (isMouldyDirtAdjacent(world.getBlockState(pos.add(offsetX, 0, offsetZ)), state)) {
					count++;
				}
		return count >= 8;
	}

	private boolean isMouldyDirtAdjacent(IBlockState posState, IBlockState stateChecked) {
		return posState.getBlock() == stateChecked.getBlock();
	}

	private void slamAttack(World world, BlockPos pos) {
		for (int x = 0; x < 12; x++) {
			for (int d = 0; d < 6; d++) {
				double angle = Math.toRadians(x * 30D);
				double offSetX = Math.floor(-Math.sin(angle) * (2D + d));
				double offSetZ = Math.floor(Math.cos(angle) * (2D + d));

				BlockPos origin = new BlockPos(pos.add(offSetX, -1, offSetZ));
				IBlockState block = world.getBlockState(origin);

				if (block.isNormalCube() && !block.getBlock().hasTileEntity(block) && block.getBlockHardness(world, origin) <= 5.0F && block.getBlockHardness(world, origin) >= 0.0F && world.getBlockState(origin).isOpaqueCube()) {
					EntityShockwaveBlock shockwaveBlock = new EntityShockwaveBlock(world);
					shockwaveBlock.setOrigin(origin, 5 + d * 2, origin.getX() + 0.5D, origin.getZ() + 0.5D, this);
					shockwaveBlock.setLocationAndAngles(origin.getX() + 0.5D, origin.getY(), origin.getZ() + 0.5D, 0.0F, 0.0F);
					shockwaveBlock.setBlock(Block.getBlockById(Block.getIdFromBlock(world.getBlockState(origin).getBlock())), world.getBlockState(origin).getBlock().getMetaFromState(world.getBlockState(origin)));
					world.spawnEntity(shockwaveBlock);
				}
			}
		}
	}

	private float getRangeToPlayer() {
		List<EntityPlayer> list = getEntityWorld().getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(getPosition()).grow(12D, 2D, 12D));
		for (EntityPlayer player : list)
			if (/* !player.isCreative() && */ !player.isSpectator())
				return getDistance(player);
		return 6F;
	}

	private void spawnSporeMinions(World world, BlockPos pos) {
		for (int x = 0; x < 4; x++) {
			double angle = Math.toRadians(x * 90D);
			double offSetX = Math.floor(-Math.sin(angle) * 1D);
			double offSetZ = Math.floor(Math.cos(angle) * 1D);

			if (getEntityWorld().isAirBlock(pos.add(offSetX, 1, offSetZ))) {
				EntitySporeMinion spore = new EntitySporeMinion(world);
				spore.setLocationAndAngles(posX + offSetX, posY + 1.8D, posZ + offSetZ, world.rand.nextFloat() * 360, 0);
				double velX = pos.getX() + offSetX * 4D - spore.posX;
				double velY = pos.getY() + 6D - spore.posY;
				double velZ = pos.getZ() + offSetZ * 4D - spore.posZ;
				double distanceSqRt = (double) MathHelper.sqrt(velX * velX + velY * velY + velZ * velZ);
				double accelerationX = velX / distanceSqRt * 0.3D + rand.nextDouble() * 0.2D;
				double accelerationY = velY / distanceSqRt * 0.5D + rand.nextDouble() * 0.5D;
				double accelerationZ = velZ / distanceSqRt * 0.3D + rand.nextDouble() * 0.2D;
				spore.addVelocity(accelerationX, accelerationY, accelerationZ);
				world.spawnEntity(spore);
				// spore.setType(1); //test
				spore.setOwnerId(this.getUniqueID());
			}
		}
	}

	protected void findEnemyToAttack() {
		if (!getActive1() && getAnimation1() == 0) {
			List<EntityPlayer> list = getEntityWorld().getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(getPosition()).grow(12D, 2D, 12D));
			for (EntityPlayer player : list) {
				if (/* !player.isCreative() && */ !player.isSpectator()) {
					setActive1(true);
					setPause(true);
				}
			}
		}
	}

	private void spawnFragSpores() {
		for (int x = 0; x < 6; x++) {
			double angle = Math.toRadians(x * 60D);
			double offSetX = Math.floor(-Math.sin(angle) * 1.5D);
			double offSetZ = Math.floor(Math.cos(angle) * 1.5D);
			EntityFragSpore fragSpore = new EntityFragSpore(getEntityWorld());
			fragSpore.setPosition(posX, posY + 2.5D, posZ);
			double velX = posX + offSetX - fragSpore.posX;
			double velY = posY + 6 - fragSpore.posY;
			double velZ = posZ + offSetZ - fragSpore.posZ;
			double distanceSqRt = (double) MathHelper.sqrt(velX * velX + velY * velY + velZ * velZ);
			double accelerationX = velX / distanceSqRt + rand.nextDouble() * 0.1D - rand.nextDouble() * 0.05D;
			double accelerationY = velY / distanceSqRt + rand.nextDouble() * 0.1D - rand.nextDouble() * 0.05D;
			double accelerationZ = velZ / distanceSqRt + rand.nextDouble() * 0.1D - rand.nextDouble() * 0.05D;
			fragSpore.addVelocity(accelerationX, accelerationY, accelerationZ);
			getEntityWorld().spawnEntity(fragSpore);
		}
	}

	public boolean isWearingSilkMask(EntityLivingBase entity) {
		if (entity instanceof EntityPlayer) {
			ItemStack helmet = ((EntityPlayer) entity).getItemStackFromSlot(EntityEquipmentSlot.HEAD);
			if (!helmet.isEmpty() && helmet.getItem() == ItemRegistry.SILK_MASK)
				return true;
		}
		return false;
	}

	@Override
	protected boolean isMovementBlocked() {
		return true;
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public void addVelocity(double x, double y, double z) {
		motionX = 0;
		motionY += y;
		motionZ = 0;
	}

	@Override
	public boolean getIsInvulnerable() {
		return false;
	}

	@Override
	public void onKillCommand() {
		this.setDead();
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if (source == DamageSource.OUT_OF_WORLD) // test using generic atm
			return super.attackEntityFrom(source, damage);

		if (source instanceof EntityDamageSource) {
			Entity sourceEntity = ((EntityDamageSource) source).getTrueSource();
			if(sourceEntity instanceof EntitySporeMinion) {
				EntitySporeMinion minion = (EntitySporeMinion) sourceEntity;
				return super.attackEntityFrom(minion.sporeminionDamage, damage);
			}
			if (sourceEntity instanceof EntityPlayer) {
				if (!isDead && cooldown <= 0 && canBeHit()) {
					if (!getEntityWorld().isRemote) {
						return super.attackEntityFrom(source, 20F);
					}
					
					if (!((getHealth() - damage) <= 0)) {
						setActive1(false);
						setActive2(false);
						setActive3(false);
						setActive4(false);
						setPause(false);
						setPauseCount(40);
						setActive5(true);
						cooldown = 40;
					}
				}
			}
		}
		return false;
	}

	private boolean canBeHit() {
		return getPause() || getActive1() || getActive2() || getActive3() || getActive4() || getActive5();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setBoolean("active_1", getActive1());
		nbt.setBoolean("active_2", getActive2());
		nbt.setBoolean("active_3", getActive3());
		nbt.setBoolean("active_4", getActive4());
		nbt.setBoolean("active_5", getActive5());
		nbt.setBoolean("pause", getPause());
		nbt.setInteger("pause_count", getPauseCount());
		nbt.setInteger("cooldown", cooldown);
		nbt.setInteger("animation_1", getAnimation1());
		nbt.setInteger("animation_2", getAnimation2());
		nbt.setInteger("animation_3", getAnimation3());
		nbt.setInteger("animation_4", getAnimation4());
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		setActive1(nbt.getBoolean("active_1"));
		setActive2(nbt.getBoolean("active_2"));
		setActive3(nbt.getBoolean("active_3"));
		setActive4(nbt.getBoolean("active_4"));
		setActive5(nbt.getBoolean("active_5"));
		setPause(nbt.getBoolean("pause"));
		setPauseCount(nbt.getInteger("pause_count"));
		cooldown = nbt.getInteger("cooldown");
		setAnimation1(nbt.getInteger("animation_1"));
		setAnimation2(nbt.getInteger("animation_2"));
		setAnimation3(nbt.getInteger("animation_3"));
		setAnimation4(nbt.getInteger("animation_4"));
	}

	@SideOnly(Side.CLIENT)
	private void spawnSporeJetParticles() {
		for (double yy = this.posY + 2.25F; yy < this.posY + 4.25D; yy += 0.5D) {
			double d0 = this.posX - 0.075F;
			double d1 = yy;
			double d2 = this.posZ - 0.075F;
			double d3 = this.posX + 0.075F;
			double d4 = this.posZ + 0.075F;
			double d5 = this.posX;
			double d6 = yy + 0.25F;
			double d7 = this.posZ;
			BatchedParticleRenderer.INSTANCE
					.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR,
							BLParticles.PUZZLE_BEAM.create(world, d0, d1, d2,
									ParticleArgs.get().withMotion(0.0125f * (rand.nextFloat() - 0.5f),
											0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f))
											.withColor(105F, 70F, 40F, 1F).withScale(1.5F).withData(100)));
			BatchedParticleRenderer.INSTANCE
					.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR,
							BLParticles.PUZZLE_BEAM.create(world, d0, d1, d4,
									ParticleArgs.get().withMotion(0.0125f * (rand.nextFloat() - 0.5f),
											0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f))
											.withColor(105F, 70F, 40F, 1F).withScale(1.5F).withData(100)));
			BatchedParticleRenderer.INSTANCE
					.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR,
							BLParticles.PUZZLE_BEAM.create(world, d3, d1, d2,
									ParticleArgs.get().withMotion(0.0125f * (rand.nextFloat() - 0.5f),
											0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f))
											.withColor(105F, 70F, 40F, 1F).withScale(1.5F).withData(100)));
			BatchedParticleRenderer.INSTANCE
					.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR,
							BLParticles.PUZZLE_BEAM.create(world, d3, d1, d4,
									ParticleArgs.get().withMotion(0.0125f * (rand.nextFloat() - 0.5f),
											0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f))
											.withColor(105F, 70F, 40F, 1F).withScale(1.5F).withData(100)));
			BatchedParticleRenderer.INSTANCE
					.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR,
							BLParticles.PUZZLE_BEAM.create(world, d0, d1, d2,
									ParticleArgs.get().withMotion(0.0125f * (rand.nextFloat() - 0.5f),
											0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f))
											.withColor(105F, 70F, 40F, 1F).withScale(1.5F).withData(100)));
			BatchedParticleRenderer.INSTANCE
					.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR,
							BLParticles.PUZZLE_BEAM.create(world, d5, d6, d7,
									ParticleArgs.get().withMotion(0.0125f * (rand.nextFloat() - 0.5f),
											0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f))
											.withColor(105F, 70F, 40F, 1F).withScale(1.5F).withData(100)));
			BatchedParticleRenderer.INSTANCE
					.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR,
							BLParticles.PUZZLE_BEAM.create(world, d0, d1, d2,
									ParticleArgs.get().withMotion(0.0125f * (rand.nextFloat() - 0.5f),
											0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f))
											.withColor(105F, 70F, 40F, 1F).withScale(1.5F).withData(100)));
		}
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 0;
	}
}