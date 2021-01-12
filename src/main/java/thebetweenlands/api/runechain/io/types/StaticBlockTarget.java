package thebetweenlands.api.runechain.io.types;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class StaticBlockTarget extends StaticVectorTarget implements IBlockTarget {
	private BlockPos block = null;

	public StaticBlockTarget(double x, double y, double z) {
		super(x, y, z);
	}

	public StaticBlockTarget(Vec3d pos) {
		super(pos);
	}

	public StaticBlockTarget(BlockPos pos) {
		super(pos);
	}
	
	public StaticBlockTarget(IVectorTarget pos) {
		super(pos);
	}

	@Override
	public BlockPos block() {
		if(this.block == null) {
			this.block = new BlockPos(this.x(), this.y(), this.z());
		}
		return this.block;
	}
}
