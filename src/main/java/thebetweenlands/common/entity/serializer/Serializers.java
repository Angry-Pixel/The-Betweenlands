package thebetweenlands.common.entity.serializer;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.math.Vec3d;

public class Serializers {
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
			return null;
		}
	};

	public static void register() {
		DataSerializers.registerSerializer(VEC3D);
	}
}
