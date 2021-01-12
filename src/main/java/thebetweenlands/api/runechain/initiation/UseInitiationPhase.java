package thebetweenlands.api.runechain.initiation;

import javax.annotation.Nullable;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class UseInitiationPhase extends InitiationPhase {
	private final BlockPos block;
	private final EnumFacing facing;
	private final Vec3d pos;

	public UseInitiationPhase(BlockPos block, EnumFacing facing, Vec3d pos) {
		this.block = block;
		this.facing = facing;
		this.pos = pos;
	}

	public UseInitiationPhase() {
		this(null, null, null);
	}

	@Nullable
	public BlockPos getBlock() {
		return this.block;
	}

	@Nullable
	public EnumFacing getFacing() {
		return this.facing;
	}

	@Nullable
	public Vec3d getPosition() {
		return this.pos;
	}
}