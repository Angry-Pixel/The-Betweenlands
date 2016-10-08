package thebetweenlands.common.world.storage.chunk.storage.locationold;


import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import thebetweenlands.common.world.storage.chunk.ChunkDataBase;

public class GuardedLocationStorage extends LocationStorage {
	private boolean guarded = true;

	public GuardedLocationStorage(World world, ChunkDataBase data) {
		super(world, data);
	}

	public GuardedLocationStorage(World world, ChunkDataBase data, String name, AxisAlignedBB area, EnumLocationType type) {
		super(world, data, name, area, type);
	}

	public boolean isGuarded() {
		return this.guarded;
	}

	public GuardedLocationStorage setGuarded(boolean guarded) {
		this.guarded = guarded;
		return this;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.guarded = nbt.getBoolean("guarded");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setBoolean("guarded", this.guarded);
	}
}
