package thebetweenlands.common.world.storage.location;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import thebetweenlands.api.storage.IWorldStorage;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageID;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.DimensionRegistries;

public class LocationPortal extends LocationStorage {
	private BlockPos portalPos;
	@Nullable
	private BlockPos otherPortalPos;
	private ResourceKey<Level> otherPortalDimension;
	private boolean targetDimensionSet;

	public LocationPortal(IWorldStorage worldStorage, StorageID id, @Nullable LocalRegion region) {
		super(worldStorage, id, region);
	}

	public LocationPortal(IWorldStorage worldStorage, StorageID id, @Nullable LocalRegion region, BlockPos pos) {
		super(worldStorage, id, region);
		this.portalPos = pos;
	}

	@Override
	public void readFromNBT(CompoundTag tag) {
		super.readFromNBT(tag);
		this.portalPos = BlockPos.of(tag.getLong("PortalPos"));
		if (tag.contains("OtherPortalPos", Tag.TAG_LONG)) {
			this.otherPortalPos = BlockPos.of(tag.getLong("OtherPortalPos"));
		} else {
			this.otherPortalPos = null;
		}
		if (tag.contains("OtherPortalDimension", Tag.TAG_INT)) {
			this.otherPortalDimension = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(tag.getString("OtherPortalDimension")));
		}
		this.targetDimensionSet = tag.getBoolean("TargetDimSet");
	}

	@Override
	public CompoundTag writeToNBT(CompoundTag tag) {
		tag = super.writeToNBT(tag);
		tag.putLong("PortalPos", this.portalPos.asLong());
		if (this.otherPortalPos != null) {
			tag.putLong("OtherPortalPos", this.otherPortalPos.asLong());
		}
		tag.putString("OtherPortalDimension", this.otherPortalDimension.location().toString());
		tag.putBoolean("TargetDimSet", this.targetDimensionSet);
		return tag;
	}

	/**
	 * Returns the position of this portal
	 *
	 * @return
	 */
	public BlockPos getPortalPosition() {
		return this.portalPos;
	}

	/**
	 * Returns the position of the portal on the other side
	 *
	 * @return
	 */
	@Nullable
	public BlockPos getOtherPortalPosition() {
		return this.otherPortalPos;
	}

	/**
	 * Returns the dimension the other portal is in
	 *
	 * @return
	 */
	public ResourceKey<Level> getOtherPortalDimension() {
		return this.otherPortalDimension;
	}

	/**
	 * Sets the position of the portal on the other side
	 *
	 * @param pos
	 */
	public void setOtherPortalPosition(ResourceKey<Level> dim, @Nullable BlockPos pos) {
		this.otherPortalPos = pos;
		this.otherPortalDimension = dim;
		this.setDirty(true);
	}

	/**
	 * Sets the target dimension of this portal. A new portal
	 * will be generated on the other side when first entering the portal.
	 * The target dimension will be set in {@link #getOtherPortalDimension()},
	 * the link of this portal will be overwritten
	 *
	 * @param dim The target dimension
	 */
	public void setTargetDimension(ResourceKey<Level> dim) {
		this.otherPortalPos = null;
		this.otherPortalDimension = dim;
		this.targetDimensionSet = true;
		this.setDirty(true);
	}

	/**
	 * Returns whether a target dimension was set. See {@link #setTargetDimension(ResourceKey)}
	 *
	 * @return
	 */
	public boolean hasTargetDimension() {
		return this.targetDimensionSet;
	}

	/**
	 * Checks whether the portal is still valid and if not, removes the location
	 *
	 * @return True if the portal was invalid and removed
	 */
	public boolean validateAndRemove(Level level) {
		AABB bounds = this.getBoundingBox();
		for (BlockPos checkPos : BlockPos.betweenClosed(BlockPos.containing(bounds.minX, bounds.minY, bounds.minZ), BlockPos.containing(bounds.maxX, bounds.maxY, bounds.maxZ))) {
			if (level.getBlockState(checkPos).is(BlockRegistry.PORTAL)) {
				return false;
			}
		}
		return this.getWorldStorage().getLocalStorageHandler().removeLocalStorage(this);
	}
}