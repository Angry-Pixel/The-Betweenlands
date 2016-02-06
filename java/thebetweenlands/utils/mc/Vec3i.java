package thebetweenlands.utils.mc;

import com.google.common.base.Objects;
import net.minecraft.util.MathHelper;

public class Vec3i implements Comparable {
	public static final Vec3i NULL_VECTOR = new Vec3i(0, 0, 0);

	private final int x;

	private final int y;

	private final int z;

	public Vec3i(int p_i46007_1_, int p_i46007_2_, int p_i46007_3_) {
		x = p_i46007_1_;
		y = p_i46007_2_;
		z = p_i46007_3_;
	}

	public Vec3i(double p_i46008_1_, double p_i46008_3_, double p_i46008_5_) {
		this(MathHelper.floor_double(p_i46008_1_), MathHelper.floor_double(p_i46008_3_), MathHelper.floor_double(p_i46008_5_));
	}

	@Override
	public boolean equals(Object p_equals_1_) {
		if (this == p_equals_1_) {
			return true;
		} else if (!(p_equals_1_ instanceof Vec3i)) {
			return false;
		} else {
			Vec3i vec3i = (Vec3i) p_equals_1_;
			return getX() != vec3i.getX() ? false : getY() != vec3i.getY() ? false : getZ() == vec3i.getZ();
		}
	}

	@Override
	public int hashCode() {
		return (getY() + getZ() * 31) * 31 + getX();
	}

	public int compareTo(Vec3i p_177953_1_) {
		return getY() == p_177953_1_.getY() ? getZ() == p_177953_1_.getZ() ? getX() - p_177953_1_.getX() : getZ() - p_177953_1_.getZ() : getY()
				- p_177953_1_.getY();
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public Vec3i crossProduct(Vec3i vec) {
		return new Vec3i(getY() * vec.getZ() - getZ() * vec.getY(), getZ() * vec.getX() - getX() * vec.getZ(), getX() * vec.getY() - getY() * vec.getX());
	}

	public double distanceSq(double toX, double toY, double toZ) {
		double d3 = getX() - toX;
		double d4 = getY() - toY;
		double d5 = getZ() - toZ;
		return d3 * d3 + d4 * d4 + d5 * d5;
	}

	public double distanceSqToCenter(double p_177957_1_, double p_177957_3_, double p_177957_5_) {
		double d3 = getX() + 0.5D - p_177957_1_;
		double d4 = getY() + 0.5D - p_177957_3_;
		double d5 = getZ() + 0.5D - p_177957_5_;
		return d3 * d3 + d4 * d4 + d5 * d5;
	}

	public double distanceSq(Vec3i to) {
		return this.distanceSq(to.getX(), to.getY(), to.getZ());
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("x", getX()).add("y", getY()).add("z", getZ()).toString();
	}

	@Override
	public int compareTo(Object p_compareTo_1_) {
		return this.compareTo((Vec3i) p_compareTo_1_);
	}
}
