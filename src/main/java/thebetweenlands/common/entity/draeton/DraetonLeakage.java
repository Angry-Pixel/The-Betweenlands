package thebetweenlands.common.entity.draeton;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.Vec3d;

public class DraetonLeakage {
	public final Vec3d pos, dir;
	
	public int age;
	
	public DraetonLeakage(Vec3d pos, Vec3d dir, int age) {
		this.pos = pos;
		this.dir = dir;
		this.age = age;
	}
}
