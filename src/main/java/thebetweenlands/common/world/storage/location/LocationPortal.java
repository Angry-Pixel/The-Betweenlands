package thebetweenlands.common.world.storage.location;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.api.storage.IWorldStorage;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageID;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.registries.BlockRegistry;

public class LocationPortal extends LocationStorage {
	private BlockPos portalPos;
	private BlockPos otherPortalPos;
	private int otherPortalDimension;
	private boolean targetDimensionSet;

	public LocationPortal(IWorldStorage worldStorage, StorageID id, @Nullable LocalRegion region) {
		super(worldStorage, id, region);
	}
	
	public LocationPortal(IWorldStorage worldStorage, StorageID id, @Nullable LocalRegion region, BlockPos pos) {
		super(worldStorage, id, region);
		this.portalPos = pos;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.portalPos = BlockPos.fromLong(nbt.getLong("PortalPos"));
		if(nbt.hasKey("OtherPortalPos", Constants.NBT.TAG_LONG)) {
			this.otherPortalPos = BlockPos.fromLong(nbt.getLong("OtherPortalPos"));
		} else {
			this.otherPortalPos = null;
		}
		if(nbt.hasKey("OtherPortalDimension", Constants.NBT.TAG_INT)) {
			this.otherPortalDimension = nbt.getInteger("OtherPortalDimension");
		} else {
			//Legacy code for old portals that didn't support other dimensions
			int currDim = this.getWorldStorage().getWorld().provider.getDimension();
			if(currDim == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId) {
				this.otherPortalDimension = 0;
			} else {
				this.otherPortalDimension = BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId;
			}
		}
		this.targetDimensionSet = nbt.getBoolean("TargetDimSet");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt = super.writeToNBT(nbt);
		nbt.setLong("PortalPos", this.portalPos.toLong());
		if(this.otherPortalPos != null) {
			nbt.setLong("OtherPortalPos", this.otherPortalPos.toLong());
		}
		nbt.setInteger("OtherPortalDimension", this.otherPortalDimension);
		nbt.setBoolean("TargetDimSet", this.targetDimensionSet);
		return nbt;
	}

	/**
	 * Returns the position of this portal
	 * @return
	 */
	public BlockPos getPortalPosition() {
		return this.portalPos;
	}

	/**
	 * Returns the position of the portal on the other side
	 * @return
	 */
	@Nullable
	public BlockPos getOtherPortalPosition() {
		return this.otherPortalPos;
	}
	
	/**
	 * Returns the dimension the other portal is in
	 * @return
	 */
	public int getOtherPortalDimension() {
		return this.otherPortalDimension;
	}

	/**
	 * Sets the position of the portal on the other side
	 * @param pos
	 */
	public void setOtherPortalPosition(int dim, @Nullable BlockPos pos) {
		this.otherPortalPos = pos;
		this.otherPortalDimension = dim;
		this.setDirty(true);
	}
	
	/**
	 * Sets the target dimension of this portal. A new portal
	 * will be generated on the other side when first entering the portal.
	 * The target dimension will be set in {@link #getOtherPortalDimension()},
	 * the link of this portal will be overwritten
	 * @param dim The target dimension
	 */
	public void setTargetDimension(int dim) {
		this.otherPortalPos = null;
		this.otherPortalDimension = dim;
		this.targetDimensionSet = true;
		this.setDirty(true);
	}
	
	/**
	 * Returns whether a target dimension was set. See {@link #setTargetDimension(int)}
	 * @return
	 */
	public boolean hasTargetDimension() {
		return this.targetDimensionSet;
	}
	
	/**
	 * Checks whether the portal is still valid and if not, removes the location 
	 * @return True if the portal was invalid and removed
	 */
	public boolean validateAndRemove() {
		World world = this.getWorldStorage().getWorld();
		AxisAlignedBB bounds = this.getBoundingBox();
		for(MutableBlockPos checkPos : MutableBlockPos.getAllInBoxMutable(new BlockPos(bounds.minX, bounds.minY, bounds.minZ), new BlockPos(bounds.maxX, bounds.maxY, bounds.maxZ))) {
			if(world.getBlockState(checkPos).getBlock() == BlockRegistry.TREE_PORTAL) {
				return false;
			}
		}
		return this.getWorldStorage().getLocalStorageHandler().removeLocalStorage(this);
	}
}