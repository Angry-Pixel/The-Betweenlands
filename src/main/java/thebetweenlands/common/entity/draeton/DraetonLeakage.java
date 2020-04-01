package thebetweenlands.common.entity.draeton;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.Vec3d;

public class DraetonLeakage {
	public final Vec3d pos, dir;
	
	public DraetonLeakage(Vec3d pos, Vec3d dir) {
		this.pos = pos;
		this.dir = dir;
	}
}
