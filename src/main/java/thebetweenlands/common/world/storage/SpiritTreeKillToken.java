package thebetweenlands.common.world.storage;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public class SpiritTreeKillToken {
	public final BlockPos pos;
	public final float strength;

	public SpiritTreeKillToken(BlockPos position, float strength) {
		this.pos = position;
		this.strength = strength;
	}

	public NBTTagCompound writeToNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setLong("pos", this.pos.toLong());
		nbt.setFloat("strength", this.strength);
		return nbt;
	}

	public static SpiritTreeKillToken readFromNBT(NBTTagCompound nbt) {
		return new SpiritTreeKillToken(BlockPos.fromLong(nbt.getLong("pos")), nbt.getFloat("strength"));
	}
}
