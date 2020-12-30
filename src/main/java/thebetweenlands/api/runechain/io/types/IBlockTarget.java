package thebetweenlands.api.runechain.io.types;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

@FunctionalInterface
public interface IBlockTarget extends IVectorTarget {
	public BlockPos block();

	@Override
	public default Vec3d vec() {
		BlockPos block = this.block();
		return new Vec3d(block.getX() + 0.5f, block.getY() + 0.5f, block.getZ() + 0.5f);
	}

	@Override
	public default double x() {
		return this.block().getX() + 0.5f;
	}

	@Override
	public default double y() {
		return this.block().getY() + 0.5f;
	}

	@Override
	public default double z() {
		return this.block().getZ() + 0.5f;
	}

	public default int bx() {
		return this.block().getX();
	}

	public default int by() {
		return this.block().getY();
	}

	public default int bz() {
		return this.block().getZ();
	}
}
