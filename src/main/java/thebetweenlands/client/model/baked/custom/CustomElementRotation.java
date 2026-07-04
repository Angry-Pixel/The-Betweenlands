package thebetweenlands.client.model.baked.custom;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.mojang.math.Transformation;

import net.minecraft.world.phys.Vec3;

public record CustomElementRotation(Vector3f origin, float xRot, float yRot, float zRot, boolean rescale) {
	public static final CustomElementRotation ZERO = new CustomElementRotation(0.0f, 0.0f, 0.0f);

	public CustomElementRotation(Vector3f rotation) {
		this(rotation.x, rotation.y, rotation.z);
	}
	
	public CustomElementRotation(Vec3 rotation) {
		this((float)rotation.x, (float)rotation.y, (float)rotation.z);
	}

	public CustomElementRotation(float xRot, float yRot, float zRot) {
		this(new Vector3f(), xRot, yRot, zRot, true);
	}

	public CustomElementRotation(Vector3f origin, float xRot, float yRot, float zRot, boolean rescale) {
		this.origin = origin;
		this.xRot = xRot;
		this.yRot = yRot;
		this.zRot = zRot;
		this.rescale = rescale;
	}
	
	public Transformation getTransformation() {
        Quaternionf leftRotation = new Quaternionf().rotationZYX(this.zRot * (float) (Math.PI / 180.0), this.yRot * (float) (Math.PI / 180.0), this.xRot * (float) (Math.PI / 180.0));
		
		// TODO rescaling
		
		Transformation transformation = new Transformation(null, leftRotation, null, null);
		return transformation.applyOrigin(new Vector3f(this.origin));
	}
	
	/**
	 * {@return whether this rotation has no effect}
	 */
	public boolean isIdentity() {
		return this == ZERO || (Math.abs(this.xRot) == 0.0f && Math.abs(this.yRot) == 0.0f && Math.abs(this.zRot) == 0.0f);
	}
}
