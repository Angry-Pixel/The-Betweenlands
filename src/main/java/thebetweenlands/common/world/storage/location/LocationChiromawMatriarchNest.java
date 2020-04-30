package thebetweenlands.common.world.storage.location;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.api.storage.IWorldStorage;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageID;

public class LocationChiromawMatriarchNest extends LocationGuarded {
	private BlockPos nest;

	public LocationChiromawMatriarchNest(IWorldStorage worldStorage, StorageID id, LocalRegion region) {
		super(worldStorage, id, region, "chiromaw_matriarch_nest", EnumLocationType.CHIROMAW_MATRIARCH_NEST);
	}

	public LocationChiromawMatriarchNest(IWorldStorage worldStorage, StorageID id, LocalRegion region, BlockPos nest) {
		super(worldStorage, id, region, "chiromaw_matriarch_nest", EnumLocationType.CHIROMAW_MATRIARCH_NEST);
		this.setNestPosition(nest);
	}

	public void setNestPosition(BlockPos nest) {
		this.nest = nest;
	}

	public BlockPos getNestPosition() {
		return this.nest;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt = super.writeToNBT(nbt);
		if(this.nest != null) {
			nbt.setInteger("NestX", this.nest.getX());
			nbt.setInteger("NestY", this.nest.getY());
			nbt.setInteger("NestZ", this.nest.getZ());
		}
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.nest = new BlockPos(nbt.getInteger("NestX"), nbt.getInteger("NestY"), nbt.getInteger("NestZ"));
	}

	@Override
	public void update() {
		super.update();

		World world = this.getWorldStorage().getWorld();
		if(!world.isRemote && this.nest != null && !this.getGuard().isClear(world) && !world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(this.nest), player -> !player.isCreative() && !player.isSpectator()).isEmpty()) {
			this.getGuard().clear(world);
		}
	}
}
