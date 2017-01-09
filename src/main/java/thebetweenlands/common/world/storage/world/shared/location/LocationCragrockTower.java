package thebetweenlands.common.world.storage.world.shared.location;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.common.block.terrain.BlockWisp;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.storage.world.global.WorldDataBase;

public class LocationCragrockTower extends LocationStorage implements ITickable {
	private List<BlockPos> glowingCragrockBlocks = new ArrayList<BlockPos>();
	private List<BlockPos> wisps = new ArrayList<BlockPos>();
	private List<BlockPos> inactiveWisps = new ArrayList<BlockPos>();
	private BlockPos structurePos;
	private boolean isTopReached = false;
	private boolean wasEntered = false;
	private int wispUpdateTicks = 0;

	public LocationCragrockTower(WorldDataBase<?> worldStorage, UUID uuid) {
		super(worldStorage, uuid, "translate:cragrockTower", EnumLocationType.DUNGEON);
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
	 * Updates the wisp blocks
	 */
	public void updateWispBlocks(@Nullable EntityPlayer player) {
		World world = this.getWorldStorage().getWorld();

		if(this.wasEntered() && !this.inactiveWisps.isEmpty()) {
			this.wispUpdateTicks++;

			if(this.wispUpdateTicks >= 10) {
				//Pick closest wisp to be placed, set block and play sound

				this.wispUpdateTicks = 0;

				Vec3i src = player != null ? player.getPosition() : this.structurePos;

				BlockPos closest = this.inactiveWisps.get(0);
				for(BlockPos pos : this.inactiveWisps) {
					if(pos.distanceSq(src) < closest.distanceSq(src)) {
						closest = pos;
					}
				}

				if(player == null || closest.getDistance(src.getX(), src.getY(), src.getZ()) < 28) {
					world.setBlockState(closest, BlockRegistry.WISP.getDefaultState().withProperty(BlockWisp.COLOR, world.rand.nextInt(4)));
					world.playSound(null, closest.getX(), closest.getY(), closest.getZ(), SoundRegistry.IGNITE, SoundCategory.AMBIENT, 1.6F + world.rand.nextFloat() * 0.45F, 1.0F + world.rand.nextFloat() * 0.4F);

					this.inactiveWisps.remove(closest);

					this.setDirty(true);
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

		if(!world.isRemote) {
			List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, this.getBounds().get(0));
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
		}
	}
}
