package thebetweenlands.api.rune;

import net.minecraft.util.math.Vec3d;

@FunctionalInterface
public interface IVectorTarget {
	public Vec3d vec();

	public default double x() {
		return this.vec().x;
	}

	public default double y() {
		return this.vec().y;
	}

	public default double z() {
		return this.vec().z;
	}

	/**
	 * Whether the target is dynamic, i.e. can change its position over time
	 * @return
	 */
	public default boolean isDynamic() {
		return true;
	}
}
