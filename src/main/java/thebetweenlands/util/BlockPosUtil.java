package thebetweenlands.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public class BlockPosUtil {

    public static Vec3 toVec3(BlockPos pos) {
        return new Vec3(pos.getX(),pos.getY(),pos.getZ());
    }
}
