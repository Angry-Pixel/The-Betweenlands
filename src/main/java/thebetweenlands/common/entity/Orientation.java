package thebetweenlands.common.entity;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;

public record Orientation(Vec3 normal, Vec3 localZ, Vec3 localY, Vec3 localX, float componentZ, float componentY, float componentX, float yaw, float pitch) {

	public Vec3 getGlobal(Vec3 local) {
		return this.localX.scale(local.x()).add(this.localY().scale(local.y())).add(this.localZ().scale(local.z));
	}

	public Vec3 getGlobal(float yaw, float pitch) {
		float cy = Mth.cos(yaw * 0.017453292F);
		float sy = Mth.sin(yaw * 0.017453292F);
		float cp = -Mth.cos(-pitch * 0.017453292F);
		float sp = Mth.sin(-pitch * 0.017453292F);
		return this.localX().scale(sy * cp).add(this.localY().scale(sp)).add(this.localZ().scale(cy * cp));
	}

	public Vec3 getLocal(Vec3 global) {
		return new Vec3(this.localX().dot(global), this.localY().dot(global), this.localZ().dot(global));
	}

	public Pair<Float, Float> getLocalRotation(Vec3 global) {
		Vec3 local = this.getLocal(global);

		float yaw = (float) Math.toDegrees(Mth.atan2(local.x(), local.z())) + 180.0f;
		float pitch = (float) -Math.toDegrees(Mth.atan2(local.y(), Math.sqrt(local.x() * local.x() + local.z() * local.z())));

		return Pair.of(yaw, pitch);
	}
}
