package thebetweenlands.common.world.storage.location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import thebetweenlands.api.storage.IWorldStorage;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageID;
import thebetweenlands.common.block.structure.BlockSlabBetweenlands;
import thebetweenlands.common.block.terrain.BlockWisp;
import thebetweenlands.common.entity.EntityTriggeredFallingBlock;
import thebetweenlands.common.network.datamanager.GenericDataManager;
import thebetweenlands.common.registries.AdvancementCriterionRegistry;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class LocationCragrockTower extends LocationGuarded {
	private List<BlockPos> glowingCragrockBlocks = new ArrayList<BlockPos>();
	private List<BlockPos> wisps = new ArrayList<BlockPos>();
	private List<BlockPos> inactiveWisps = new ArrayList<BlockPos>();
	private BlockPos[][] levelBlockadeBlocks = new BlockPos[5][0];
	private boolean[] spawners = { true, true, true, true, true };
	private boolean[] blockades = { true, true, true, true, true };
	private BlockPos structurePos;
	private boolean isTopConquered = false;
	private boolean isTopReached = false;
	private boolean wasEntered = false;
	private int wispUpdateTicks = 0;
	private int topSpawners = 2;

	private final int[][] levelYBounds = { {-5, 9}, {10, 18}, {19, 27}, {28, 36}, {37, 45}, {46, 56} };

	protected static final DataParameter<Boolean> CRUMBLING = GenericDataManager.createKey(LocationCragrockTower.class, DataSerializers.BOOLEAN);
	protected static final DataParameter<Integer> CRUMBLING_TICKS = GenericDataManager.createKey(LocationCragrockTower.class, DataSerializers.VARINT);

	public LocationCragrockTower(IWorldStorage worldStorage, StorageID id, @Nullable LocalRegion region) {
		super(worldStorage, id, region, "cragrock_tower", EnumLocationType.DUNGEON);
		this.dataManager.register(CRUMBLING, false);
		this.dataManager.register(CRUMBLING_TICKS, 20, 0);
	}

	/**
	 * Sets the structure entrance
	 * @param pos
	 */
	public void setStructurePos(BlockPos pos) {
		this.structurePos = pos;
		this.setDirty(true);
	}

	/**
	 * Returns the structure entrance
	 * @return
	 */
	public BlockPos getStructurePos() {
		return this.structurePos;
	}

	/**
	 * Returns the inner AABB
	 * @return
	 */
	public AxisAlignedBB getInnerBoundingBox() {
		return this.getBounds().size() > 1 ? this.getBounds().get(1) : new AxisAlignedBB(0, 0, 0, 0, 0, 0);
	}

	/**
	 * Returns whether the top was reached and all mobs and spawners are destroyed
	 * @return
	 */
	public boolean isTopConquered() {
		return this.isTopConquered;
	}

	/**
	 * Sets whether the top was reached and changes all glowing cragrock blocks accordingly
	 * @param reached
	 */
	public void setTopConquered(boolean reached) {
		this.isTopConquered = reached;

		World world = this.getWorldStorage().getWorld();
		for(BlockPos pos : this.glowingCragrockBlocks) {
			if(reached) {
				world.setBlockState(pos, BlockRegistry.GLOWING_SMOOTH_CRAGROCK.getDefaultState());
			} else {
				world.setBlockState(pos, BlockRegistry.INACTIVE_GLOWING_SMOOTH_CRAGROCK.getDefaultState());
			}
		}

		this.setDirty(true);
	}

	/**
	 * Adds an inactive glowing cragrock block that is activated once the top is reached
	 * @param pos
	 */
	public void addGlowingCragrock(BlockPos pos) {
		this.glowingCragrockBlocks.add(pos);
		this.setDirty(true);
	}

	/**
	 * Adds an inactive wisp that is activated once a player gets close to the tower
	 * @param pos
	 */
	public void addInactiveWisp(BlockPos pos) {
		this.wisps.add(pos);
		this.inactiveWisps.add(pos);
		this.setDirty(true);
	}

	/**
	 * Sets whether the tower was entered. Wisps are changed accordingly in the next tick
	 * @param entered
	 * @return
	 */
	public void setEntered(boolean entered) {
		this.wasEntered = entered;
		this.setDirty(true);
	}

	/**
	 * Returns whether the tower was entered
	 * @return
	 */
	public boolean wasEntered() {
		return this.wasEntered;
	}

	/**
	 * Sets the level blockade blocks
	 * @param level
	 * @param blocks
	 */
	public void setLevelBlockadeBlocks(int level, BlockPos[] blocks) {
		this.levelBlockadeBlocks[level] = blocks;
		this.setDirty(true);
	}

	/**
	 * Returns the level blockades
	 * @param level
	 * @return
	 */
	public BlockPos[] getLevelBlockadeBlocks(int level) {
		return this.levelBlockadeBlocks[level];
	}

	/**
	 * Destroys the blockade blocks for the specified level
	 * @param level
	 */
	public void destroyBlockade(int level) {
		BlockPos[] blocks = this.levelBlockadeBlocks[level];
		if(blocks != null && blocks.length != 0) {
			World world = this.getWorldStorage().getWorld();

			for(BlockPos pos : blocks) {
				world.playSound(null, pos, SoundRegistry.CRUMBLE, SoundCategory.BLOCKS, 0.2F, 1F);
				world.playEvent(null, 2001, pos.down(), Block.getIdFromBlock(world.getBlockState(pos).getBlock()));

				world.setBlockToAir(pos);
				this.getGuard().setGuarded(world, pos, false);
			}
		}

		this.blockades[level] = false;

		this.setDirty(true);
	}

	/**
	 * Restores the blockade blocks for the specified level
	 * @param level
	 */
	public void restoreBlockade(int level) {
		BlockPos[] blocks = this.levelBlockadeBlocks[level];
		if(blocks != null && blocks.length != 0) {
			World world = this.getWorldStorage().getWorld();

			for(BlockPos pos : blocks) {
				world.setBlockState(pos, BlockRegistry.SMOOTH_CRAGROCK_SLAB.getDefaultState().withProperty(BlockSlabBetweenlands.HALF, BlockSlabBetweenlands.EnumBlockHalfBL.TOP));
				this.getGuard().setGuarded(world, pos, true);
			}
		}

		this.blockades[level] = true;

		this.setDirty(true);
	}

	/**
	 * Returns whether the spawner is still active
	 * @param level
	 * @return
	 */
	public boolean getSpawnerState(int level) {
		return this.spawners[level];
	}

	/**
	 * Sets whether the spawner is still active
	 * @param level
	 * @param active
	 * @return
	 */
	public void setSpawnerState(int level, boolean active) {
		this.spawners[level] = active;
		this.setDirty(true);
	}

	/**
	 * Returns whether the blockade is still active
	 * @param level
	 * @return
	 */
	public boolean getBlockadeState(int level) {
		return this.blockades[level];
	}

	/**
	 * Returns the bounding box for the specified level
	 * @param level
	 * @return
	 */
	public AxisAlignedBB getLevelBounds(int level) {
		double minY = this.levelYBounds[level][0] + this.structurePos.getY();
		double maxY = this.levelYBounds[level][1] + this.structurePos.getY();
		AxisAlignedBB inside = this.getInnerBoundingBox();
		if(level == 0) {
			inside = inside.grow(1, 1, 1); //Basement is wider
		}
		return new AxisAlignedBB(inside.minX, minY, inside.minZ, inside.maxX, maxY, inside.maxZ);
	}

	/**
	 * Returns the level for the specified Y position
	 * @param y
	 * @return
	 */
	public int getLevel(int y) {
		y -= this.structurePos.getY(); //Relative position to structure
		for(int i = 0; i < this.levelYBounds.length; i++) {
			if(y >= this.levelYBounds[i][0] && y <= this.levelYBounds[i][1]) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Returns whether the tower is crumbling
	 * @return
	 */
	public boolean isCrumbling() {
		return this.dataManager.get(CRUMBLING);
	}

	/**
	 * Sets whether the tower is crumbling
	 * @param crumbling
	 */
	public void setCrumbling(boolean crumbling) {
		this.dataManager.set(CRUMBLING, crumbling);
		this.setDirty(true);
	}

	/**
	 * Returns the crumbling ticks
	 * @return
	 */
	public int getCrumblingTicks() {
		return this.dataManager.get(CRUMBLING_TICKS);
	}

	/**
	 * Sets the crumbling ticks
	 * @param ticks
	 */
	public void setCrumblingTicks(int ticks) {
		this.dataManager.set(CRUMBLING_TICKS, ticks);
		this.setDirty(true);
	}

	/**
	 * Updates the wisp blocks
	 * @param player Closest non-creative player
	 */
	public void updateWispBlocks(@Nullable EntityPlayer player) {
		World world = this.getWorldStorage().getWorld();

		if(this.wasEntered() && !this.inactiveWisps.isEmpty()) {
			if(player != null) {
				this.wispUpdateTicks++;

				if(this.wispUpdateTicks >= 10) {
					//Pick closest wisp to be placed, set block and play sound

					this.wispUpdateTicks = 0;

					for(int i = 0; i < 4; i++) {
						if(!this.inactiveWisps.isEmpty()) {
							Vec3i src = player != null ? player.getPosition() : this.structurePos;

							BlockPos closest = this.inactiveWisps.get(0);
							for(BlockPos pos : this.inactiveWisps) {
								if(pos.distanceSq(src) < closest.distanceSq(src)) {
									closest = pos;
								}
							}

							boolean canLightUp = false;

							if((closest.getY() - this.structurePos.getY() < 16 && player.posY - this.structurePos.getY() < 45) || 
									(closest.getY() - this.structurePos.getY() >= 16 && player.posY - this.structurePos.getY() >= 45)) {
								canLightUp = true;
							}

							if(canLightUp) {
								world.setBlockState(closest, BlockRegistry.WISP.getDefaultState().withProperty(BlockWisp.COLOR, world.rand.nextInt(4)));
								world.playSound(null, closest.getX(), closest.getY(), closest.getZ(), SoundRegistry.IGNITE, SoundCategory.AMBIENT, 1.6F + world.rand.nextFloat() * 0.45F, 1.0F + world.rand.nextFloat() * 0.4F);

								this.inactiveWisps.remove(closest);

								this.setDirty(true);
							}
						} else {
							break;
						}
					}
				}
			}
		} else if(!this.wasEntered() && this.inactiveWisps.size() != this.wisps.size()) {
			//Remove all wisps

			this.inactiveWisps.clear();
			this.inactiveWisps.addAll(this.wisps);

			for(BlockPos pos : this.wisps) {
				world.setBlockToAir(pos);
			}

			this.setDirty(true);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.structurePos = BlockPos.fromLong(nbt.getLong("structurePos"));
		this.isTopConquered = nbt.getBoolean("isTopConquered");
		this.isTopReached = nbt.getBoolean("isTopReached");
		this.wasEntered = nbt.getBoolean("wasEntered");
		this.readBlockList(nbt, "glowingBlocks", this.glowingCragrockBlocks);
		this.readBlockList(nbt, "wisps", this.wisps);
		this.readBlockList(nbt, "inactiveWisps", this.inactiveWisps);
		for(int i = 0; i < 5; i++) {
			NBTTagCompound blockadeNbt = nbt.getCompoundTag("blockade." + i);
			List<BlockPos> blocks = new ArrayList<>();
			this.readBlockList(blockadeNbt, "blocks", blocks);
			if(!blocks.isEmpty()) {
				this.levelBlockadeBlocks[i] = blocks.toArray(new BlockPos[0]);
			} else {
				this.levelBlockadeBlocks[i] = new BlockPos[0];
			}
			this.blockades[i] = blockadeNbt.getBoolean("broken");
		}
		for(int i = 0; i < 5; i++) {
			this.spawners[i] = nbt.getBoolean("spawner." + i);
		}
		this.setCrumbling(nbt.getBoolean("crumbling"));
		this.setCrumblingTicks(nbt.getInteger("crumblingTicks"));
		this.topSpawners = nbt.getInteger("topSpawners");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setLong("structurePos", this.structurePos.toLong());
		nbt.setBoolean("isTopConquered", this.isTopConquered);
		nbt.setBoolean("isTopReached", this.isTopReached);
		nbt.setBoolean("wasEntered", this.wasEntered);
		this.saveBlockList(nbt, "glowingBlocks", this.glowingCragrockBlocks);
		this.saveBlockList(nbt, "wisps", this.wisps);
		this.saveBlockList(nbt, "inactiveWisps", this.inactiveWisps);
		for(int i = 0; i < 5; i++) {
			NBTTagCompound blockadeNbt = new NBTTagCompound();
			BlockPos[] blocks = this.levelBlockadeBlocks[i];
			this.saveBlockList(blockadeNbt, "blocks", blocks != null && blocks.length != 0 ? Arrays.asList(blocks) : new ArrayList<>());
			blockadeNbt.setBoolean("broken", this.blockades[i]);
			nbt.setTag("blockade." + i, blockadeNbt);
		}
		for(int i = 0; i < 5; i++) {
			nbt.setBoolean("spawner." + i, this.spawners[i]);
		}
		nbt.setBoolean("crumbling", this.isCrumbling());
		nbt.setInteger("crumblingTicks", this.getCrumblingTicks());
		nbt.setInteger("topSpawners", this.topSpawners);
		return nbt;
	}

	protected void saveBlockList(NBTTagCompound nbt, String name, List<BlockPos> blocks) {
		NBTTagList blockList = new NBTTagList();
		for(BlockPos pos : blocks) {
			blockList.appendTag(new NBTTagLong(pos.toLong()));
		}
		nbt.setTag(name, blockList);
	}

	protected void readBlockList(NBTTagCompound nbt, String name, List<BlockPos> blocks) {
		blocks.clear();
		NBTTagList blockList = nbt.getTagList(name, Constants.NBT.TAG_LONG);
		for(int i = 0; i < blockList.tagCount(); i++) {
			NBTTagLong posNbt = (NBTTagLong) blockList.get(i);
			blocks.add(BlockPos.fromLong(posNbt.getLong()));
		}
	}

	@Override
	public void update() {
		super.update();

		World world = this.getWorldStorage().getWorld();

		if(this.isCrumbling()) {
			this.dataManager.set(CRUMBLING_TICKS, this.getCrumblingTicks() + 1);

			if(!world.isRemote) {
				if(this.getCrumblingTicks() > 1200) {
					this.dataManager.set(CRUMBLING_TICKS, -1).syncImmediately();
					this.setCrumbling(false);
					this.destroyBlockade(4);
					this.getGuard().clear(world);
				} else {
					this.setDirty(true);
				}
			}
		}

		if(!world.isRemote) {
			List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, this.getEnclosingBounds(), player -> this.isInside(player) && !player.isCreative() && !player.isSpectator());

			if(!players.isEmpty()) {
				if(this.isTopConquered() && !this.isCrumbling() && this.getCrumblingTicks() == 0) {
					List<EntityPlayer> topPlayers = world.getEntitiesWithinAABB(EntityPlayer.class, this.getLevelBounds(5).grow(5, 3, 5), player -> !player.isCreative() && !player.isSpectator());
					if (topPlayers.isEmpty()) {
						this.setCrumbling(true);
						this.restoreBlockade(4);
					}
				}

				for(EntityPlayer player : players) {
					BlockPos structurePos = this.getStructurePos();

					if (!this.wasEntered()) {
						this.setEntered(true);
					}

					if (this.getLevelBounds(5).contains(player.getPositionVector())) {
						this.isTopReached = true;
						this.setDirty(true);
					}

					if(this.isTopConquered() && player instanceof EntityPlayerMP && this.getLevel(MathHelper.floor(player.posY)) == 5) {
						AdvancementCriterionRegistry.CRAGROCK_TOP.trigger((EntityPlayerMP) player);
					} else if (!this.isTopReached && !this.getInnerBoundingBox().grow(0.5D, 0.5D, 0.5D).contains(player.getPositionVector()) && player.posY - structurePos.getY() > 12) {
						//Player trying to bypass tower, teleport to entrance

						player.dismountRidingEntity();
						if (player instanceof EntityPlayerMP) {
							EntityPlayerMP playerMP = (EntityPlayerMP) player;
							playerMP.connection.setPlayerLocation(structurePos.getX() + 0.5D, structurePos.getY(), structurePos.getZ() + 0.5D, player.rotationYaw, player.rotationPitch);
						} else {
							player.setLocationAndAngles(structurePos.getX() + 0.5D, structurePos.getY(), structurePos.getZ() + 0.5D, player.rotationYaw, player.rotationPitch);
						}
						player.fallDistance = 0.0F;
						player.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 60, 2));
						player.world.playSound(null, player.posX, player.posY, player.posZ, SoundRegistry.FORTRESS_BOSS_TELEPORT, SoundCategory.AMBIENT, 1, 1);
					}
				}

				EntityPlayer closest = players.get(0);
				for(EntityPlayer player : players) {
					if(player.getDistanceSq(this.structurePos) < closest.getDistanceSq(this.structurePos)) {
						closest = player;
					}
				}
				this.updateWispBlocks(closest);
			} else {
				this.updateWispBlocks(null);
			}

			if(!this.isTopConquered()) {
				for(int i = 0; i < 5; i++) {
					if(this.getBlockadeState(i) && !this.getSpawnerState(i)) {
						List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, this.getLevelBounds(i), entity -> entity instanceof EntityMob || entity instanceof IMob);
						if(entities.isEmpty()) {
							this.destroyBlockade(i);
						}
					}
				}

				if(this.topSpawners == 0) {
					List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, this.getLevelBounds(5).grow(3, 2, 3), entity -> entity instanceof EntityMob || entity instanceof IMob);
					if(entities.isEmpty()) {
						this.setTopConquered(true);

						for(EntityPlayer player : players) {
							player.addExperience(350);
						}
					}
				}
			}

			if(this.isCrumbling()) {
				for(int i = 0; i < Math.min(Math.pow(this.getCrumblingTicks() / 400.0f, 4) * 30.0f, 30) + 1; i++) {
					BlockPos pos = this.getRandomPosInTower();
					IBlockState blockState = world.getBlockState(pos);

					if(blockState.getBlock() != Blocks.AIR && world.isAirBlock(pos.up()) && world.isAirBlock(pos.down()) && world.isAirBlock(pos.down(2))) {
						EntityTriggeredFallingBlock falling_block = new EntityTriggeredFallingBlock(world);
						falling_block.setPosition(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
						falling_block.setHanging(true);
						world.spawnEntity(falling_block);

						/*
						EntityFallingBlock fallingBlock = new EntityFallingBlock(world, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, blockState);
						fallingBlock.fallTime = -60;
						fallingBlock.shouldDropItem = false;
						fallingBlock.setHurtEntities(true);
						world.spawnEntity(fallingBlock);
						 */

						world.playSound(null, pos, SoundRegistry.CRUMBLE, SoundCategory.BLOCKS, 0.2F, 0.5F + world.rand.nextFloat() * 0.4F);
					}
				}
			}
		}
	}

	/**
	 * Returns a random block pos inside the tower
	 * @return
	 */
	protected BlockPos getRandomPosInTower() {
		AxisAlignedBB innerBB = this.getInnerBoundingBox().grow(-1, -1, -1);

		int x = MathHelper.ceil(innerBB.minX);
		int y = MathHelper.ceil(innerBB.minY);
		int z = MathHelper.ceil(innerBB.minZ);
		int width = MathHelper.floor(innerBB.maxX) - x;
		int height = MathHelper.floor(innerBB.maxY) - y;
		int depth = MathHelper.floor(innerBB.maxZ) - z;

		World world = this.getWorldStorage().getWorld();

		return new BlockPos(x + world.rand.nextInt(width + 1), y + world.rand.nextInt(height + 1), z + world.rand.nextInt(depth + 1));
	}

	@Override
	public void onBreakBlock(BreakEvent event) {
		if(!event.getWorld().isRemote) {
			BlockPos pos = event.getPos();
			IBlockState blockState = event.getState();

			if(blockState.getBlock() == BlockRegistry.MOB_SPAWNER) {
				int level = this.getLevel(pos.getY());

				if(level != -1) {
					if(level == 5) {
						if(this.topSpawners > 0) {
							this.topSpawners--;
							this.setDirty(true);
						}
					} else {
						this.setSpawnerState(level, false);
					}
				}
			}
		}
	}
}
