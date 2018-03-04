package thebetweenlands.common.world.storage.location;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.api.storage.IWorldStorage;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageID;
import thebetweenlands.common.config.BetweenlandsConfig;

public class LocationPortal extends LocationStorage {
	private BlockPos portalPos;
	private BlockPos otherPortalPos;
	private int otherPortalDimension;

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
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt = super.writeToNBT(nbt);
		nbt.setLong("PortalPos", this.portalPos.toLong());
		if(this.otherPortalPos != null) {
			nbt.setLong("OtherPortalPos", this.otherPortalPos.toLong());
		}
		nbt.setInteger("OtherPortalDimension", this.otherPortalDimension);
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
}