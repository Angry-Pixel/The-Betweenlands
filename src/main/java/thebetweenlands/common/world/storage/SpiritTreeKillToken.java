package thebetweenlands.common.world.storage;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

public class SpiritTreeKillToken {
	public final BlockPos pos;
	public final float strength;

	public SpiritTreeKillToken(BlockPos position, float strength) {
		this.pos = position;
		this.strength = strength;
	}

	public CompoundTag writeToNBT() {
		CompoundTag nbt = new CompoundTag();
		nbt.putLong("pos", this.pos.asLong());
		nbt.putFloat("strength", this.strength);
		return nbt;
	}

	public static SpiritTreeKillToken readFromNBT(CompoundTag nbt) {
		return new SpiritTreeKillToken(BlockPos.of(nbt.getLong("pos")), nbt.getFloat("strength"));
	}
}
