package thebetweenlands.client.model.baked.connectedtextures;

import com.google.gson.JsonObject;

import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public record ConnectedTexturesVertex(Vec3 pos, Vec2 uv) {

	public static ConnectedTexturesVertex deserialize(JsonObject jsonObject) {
		return new ConnectedTexturesVertex(
				jsonObject.get("x").getAsDouble(), jsonObject.get("y").getAsDouble(), jsonObject.get("z").getAsDouble(),
				jsonObject.get("u").getAsFloat(), jsonObject.get("v").getAsFloat()
			);
	}

	public ConnectedTexturesVertex(double x, double y, double z, float u, float v) {
		this(new Vec3(x, y, z), new Vec2(u, v));
	}

	public ConnectedTexturesVertex add(ConnectedTexturesVertex vertex) {
		return new ConnectedTexturesVertex(this.pos.x + vertex.pos.x, this.pos.y + vertex.pos.y, this.pos.z + vertex.pos.z, this.uv.x + vertex.uv.x, this.uv.y + vertex.uv.y);
	}

	public ConnectedTexturesVertex subtract(ConnectedTexturesVertex vertex) {
		return new ConnectedTexturesVertex(this.pos.x - vertex.pos.x, this.pos.y - vertex.pos.y, this.pos.z - vertex.pos.z, this.uv.x - vertex.uv.x, this.uv.y - vertex.uv.y);
	}

	public ConnectedTexturesVertex scale(float scale) {
		return new ConnectedTexturesVertex(this.pos.x * scale, this.pos.y * scale, this.pos.z * scale, this.uv.x * scale, this.uv.y * scale);
	}

	public ConnectedTexturesVertex scaleUVs(float u, float v) {
		return new ConnectedTexturesVertex(this.pos.x, this.pos.y, this.pos.z, this.uv.x * u, this.uv.y * v);
	}

}
