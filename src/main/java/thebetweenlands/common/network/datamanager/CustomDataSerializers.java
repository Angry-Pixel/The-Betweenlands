package thebetweenlands.common.network.datamanager;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

public class CustomDataSerializers {
	public static final DataSerializer<Vec3d> VEC3D = new DataSerializer<Vec3d>() {
		@Override
		public void write(PacketBuffer buf, Vec3d value) {
			buf.writeDouble(value.x);
			buf.writeDouble(value.y);
			buf.writeDouble(value.z);
		}

		@Override
		public Vec3d read(PacketBuffer buf) {
			return new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
		}

		@Override
		public DataParameter<Vec3d> createKey(int id) {
			return new DataParameter<Vec3d>(id, this);
		}

		@Override
		public Vec3d copyValue(Vec3d value) {
			return new Vec3d(value.x, value.y, value.z);
		}
	};

	public static final DataSerializer<AxisAlignedBB> AABB = new DataSerializer<AxisAlignedBB>() {
		@Override
		public void write(PacketBuffer buf, AxisAlignedBB value) {
			buf.writeDouble(value.minX);
			buf.writeDouble(value.minY);
			buf.writeDouble(value.minZ);
			buf.writeDouble(value.maxX);
			buf.writeDouble(value.maxY);
			buf.writeDouble(value.maxZ);
		}

		@Override
		public AxisAlignedBB read(PacketBuffer buf) {
			return new AxisAlignedBB(buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble());
		}

		@Override
		public DataParameter<AxisAlignedBB> createKey(int id) {
			return new DataParameter<AxisAlignedBB>(id, this);
		}

		@Override
		public AxisAlignedBB copyValue(AxisAlignedBB value) {
			return value;
		}
	};

	public static final DataSerializer<Short> SHORT = new DataSerializer<Short>() {
		@Override
		public void write(PacketBuffer buf, Short value) {
			buf.writeShort(value);
		}

		@Override
		public Short read(PacketBuffer buf) {
			return buf.readShort();
		}

		@Override
		public DataParameter<Short> createKey(int id) {
			return new DataParameter<Short>(id, this);
		}

		@Override
		public Short copyValue(Short value) {
			return value;
		}
	};

	public static void register() {
		DataSerializers.registerSerializer(VEC3D);
		DataSerializers.registerSerializer(SHORT);
		DataSerializers.registerSerializer(AABB);
	}
}
