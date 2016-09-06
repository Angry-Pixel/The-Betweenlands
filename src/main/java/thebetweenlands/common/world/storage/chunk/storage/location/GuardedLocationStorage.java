package thebetweenlands.common.world.storage.chunk.storage.location;


import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.chunk.Chunk;
import thebetweenlands.common.world.storage.chunk.BetweenlandsChunkData;

public class GuardedLocationStorage extends LocationStorage {
	private boolean guarded = true;

	public GuardedLocationStorage(Chunk chunk, BetweenlandsChunkData data) {
		super(chunk, data);
	}

	public GuardedLocationStorage(Chunk chunk, BetweenlandsChunkData data, String name, AxisAlignedBB area, EnumLocationType type) {
		super(chunk, data, name, area, type);
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
