package thebetweenlands.common.world.storage.world.shared.location;

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
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.common.block.structure.BlockSlabBetweenlands;
import thebetweenlands.common.block.terrain.BlockWisp;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.storage.world.global.WorldDataBase;
import thebetweenlands.common.world.storage.world.shared.SharedRegion;

public class LocationCragrockTower extends LocationGuarded implements ITickable {
	private List<BlockPos> glowingCragrockBlocks = new ArrayList<BlockPos>();
	private List<BlockPos> wisps = new ArrayList<BlockPos>();
	private List<BlockPos> inactiveWisps = new ArrayList<BlockPos>();
	private BlockPos[][] levelBlockadeBlocks = new BlockPos[5][0];
	private boolean[] spawners = { true, true, true, true, true };
	private boolean[] blockades = { true, true, true, true, true };
	private BlockPos structurePos;
	private boolean isTopReached = false;
	private boolean wasEntered = false;
	private int wispUpdateTicks = 0;

	private final int[][] levelYBounds = { {-6, 9}, {10, 18}, {19, 27}, {28, 36}, {37, 45} };

	private boolean crumbling = false;
	private int crumblingTicks = 0;

	public LocationCragrockTower(WorldDataBase<?> worldStorage, String id, @Nullable SharedRegion region) {
		super(worldStorage, id, region, "translate:cragrockTower", EnumLocationType.DUNGEON);
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
	 * Returns whether the top was reached
	 * @return
	 */
	public boolean isTopReached() {
		return this.isTopReached;
	}

	/**
	 * Sets whether the top was reached and changes all glowing cragrock blocks accordingly
	 * @param reached
	 */
	public void setTopReached(boolean reached) {
		this.isTopReached = reached;

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
			inside = inside.expand(1, 1, 1); //Basement is wider
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
		if(y >= this.levelYBounds[0][0] && y <= this.levelYBounds[0][1]) {
			return 0;
		} else if(y >= this.levelYBounds[1][0] && y <= this.levelYBounds[1][1]) {
			return 1;
		} else if(y >= this.levelYBounds[2][0] && y <= this.levelYBounds[2][1]) {
			return 2;
		} else if(y >= this.levelYBounds[3][0] && y <= this.levelYBounds[3][1]) {
			return 3;
		} else if(y >= this.levelYBounds[4][0] && y <= this.levelYBounds[4][1]) {
			return 4;
		}
		return -1;
	}

	/**
	 * Returns whether the tower is crumbling
	 * @return
	 */
	public boolean isCrumbling() {
		return this.crumbling;
	}

	/**
	 * Sets whether the tower is crumbling
	 * @param crumbling
	 */
	public void setCrumbling(boolean crumbling) {
		this.crumbling = crumbling;
		this.setDirty(true);
	}

	/**
	 * Returns the crumbling ticks
	 * @return
	 */
	public int getCrumblingTicks() {
		return this.crumblingTicks;
	}

	/**
	 * Sets the crumbling ticks
	 * @param ticks
	 */
	public void setCrumblingTicks(int ticks) {
		this.crumblingTicks = ticks;
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
		this.crumbling = nbt.getBoolean("crumbling");
		this.crumblingTicks = nbt.getInteger("crumblingTicks");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setLong("structurePos", this.structurePos.toLong());
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
		nbt.setBoolean("crumbling", this.crumbling);
		nbt.setInteger("crumblingTicks", this.crumblingTicks);
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
			this.crumblingTicks++;

			if(!world.isRemote) {
				if(this.crumblingTicks > 1200) {
					this.crumblingTicks = -1;
					this.setCrumbling(false);
					this.destroyBlockade(4);
					this.getGuard().clear(world);
				} else {
					this.setDirty(true, world.getWorldTime() % 20 == 0 /*meh? dunno*/);
				}
			}
		}

		if(!world.isRemote) {
			List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, this.getEnclosingBounds(), player -> this.isInside(player) && !player.isCreative());
			if(!players.isEmpty()) {
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

			if(!this.isTopReached()) {
				for(int i = 0; i < 5; i++) {
					if(this.getBlockadeState(i) && !this.getSpawnerState(i)) {
						List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, this.getLevelBounds(i), entity -> entity instanceof EntityMob || entity instanceof IMob);
						if(entities.isEmpty()) {
							this.destroyBlockade(i);
						}
					}
				}
			}

			if(this.isCrumbling()) {
				for(int i = 0; i < Math.min(Math.pow(this.crumblingTicks / 400.0f, 4) * 30.0f, 30) + 1; i++) {
					BlockPos pos = this.getRandomPosInTower();
					IBlockState blockState = world.getBlockState(pos);

					if(blockState.getBlock() != Blocks.AIR && world.isAirBlock(pos.down()) && world.isAirBlock(pos.down(2))) {
						//TODO: Fix falling blocks and replace with falling smooth cragrock
						EntityFallingBlock fallingBlock = new EntityFallingBlock(world, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, BlockRegistry.WEAK_BETWEENSTONE_TILES.getDefaultState());
						fallingBlock.fallTime = -60;
						fallingBlock.shouldDropItem = false;
						fallingBlock.setHurtEntities(true);
						world.spawnEntityInWorld(fallingBlock);

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
		AxisAlignedBB innerBB = this.getInnerBoundingBox().expand(-1, -1, -1);

		int x = MathHelper.ceiling_double_int(innerBB.minX);
		int y = MathHelper.ceiling_double_int(innerBB.minY);
		int z = MathHelper.ceiling_double_int(innerBB.minZ);
		int width = MathHelper.floor_double(innerBB.maxX) - x;
		int height = MathHelper.floor_double(innerBB.maxY) - y;
		int depth = MathHelper.floor_double(innerBB.maxZ) - z;

		World world = this.getWorldStorage().getWorld();

		return new BlockPos(x + world.rand.nextInt(width + 1), y + world.rand.nextInt(height + 1), z + world.rand.nextInt(depth + 1));
	}
}
