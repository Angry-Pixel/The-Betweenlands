package thebetweenlands.util;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class AABBDerpHelper extends AxisAlignedBB {

	public AABBDerpHelper(BlockPos pos) {
		super(pos);
	}

	public AABBDerpHelper(BlockPos pos1, BlockPos pos2) {
		super(pos1, pos2);
	}

	public AABBDerpHelper(double x1, double y1, double z1, double x2, double y2, double z2) {
		super(x1, y1, z1, x2, y2, z2);
	}
	
    @SideOnly(Side.CLIENT)
	public AABBDerpHelper(Vec3d min, Vec3d max) {
		super(min, max);
	}
}
