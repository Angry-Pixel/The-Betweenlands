package thebetweenlands.util;

import javax.annotation.concurrent.NotThreadSafe;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

/**
 * Helper for Oblique Projection that can be reused.
 * Uses fixed memory, so no extra objects are allocated after initialization.
 */
@NotThreadSafe
public class ObliqueProjectionHelper {
	private final Matrix4f originalProjectionMatrix = new Matrix4f();
	private final Matrix4f backupProjectionMatrix = new Matrix4f(); // RenderSystem backup
	private final float[] matrix = new float[16];
	private final Vector3f planeNormal = new Vector3f();
	private final Vector3f planePoint  = new Vector3f();
	private final Vector4f clipPlane = new Vector4f();
	private final Vector4f q         = new Vector4f();
	
	// I benchmarked this against the previous code in {@link BeamOriginMirrorWorldRenderer},
	//     and this came out to like 2-3 times faster (because it doesn't need to allocate memory for each face)
	// Not that this was a bottleneck or anything. We're talking about an improvement of 0.01 ms --> 0.003 ms here.
	
	public ObliqueProjectionHelper(Matrix4f projectionMatrix) {
		this.originalProjectionMatrix.set(projectionMatrix);
	}
	
	/**
	 * Sets the internally stored projection matrix to a copy of the specified matrix
	 * @param source
	 */
	public void updateOriginalProjectionMatrix(Matrix4f source) {
		this.originalProjectionMatrix.set(source);
	}
	
	/**
	 * Sets the destination matrix to the internal original matrix
	 * @param dest
	 */
	public void loadOriginalProjectionMatrix(Matrix4f dest) {
		dest.set(this.originalProjectionMatrix);
	}

	/**
	 * Sets up an oblique projection matrix (as by {@link ObliqueProjectionHelper#computeObliqueProjectionWorld(com.mojang.blaze3d.vertex.PoseStack.Pose, Vector3f, Vector3f, boolean, Matrix4f, Matrix4f, Matrix4f)})
	 * and stores the result in {@code projectionMatrix}.
	 * <p>Additionally, updates the projection matrix in {@link RenderSystem} to hold the same values</p>
	 * @param pose
	 * @param planePoint
	 * @param planeNormal
	 * @param flipNormal
	 * @param modelViewMatrix
	 * @param projectionMatrix
	 */
	public void setupObliqueProjection(PoseStack.Pose pose, Vector3f planePoint, Vector3f planeNormal, boolean flipNormal, Matrix4f modelViewMatrix, Matrix4f projectionMatrix) {
		this.computeObliqueProjectionWorld(pose, planePoint, planeNormal, flipNormal, modelViewMatrix, projectionMatrix, projectionMatrix);
		
		Matrix4f renderMatrix4f = RenderSystem.getProjectionMatrix();
		this.backupProjectionMatrix.set(renderMatrix4f);
		renderMatrix4f.set(projectionMatrix);
	}

	/**
	 * Given a plane in world coordinates, computes a new oblique projection matrix with a
	 * near plane equal to that plane. The resulting projection matrix is stored in {@code dest}.
	 * 
	 * @param pose         the pose the plane points are in
	 * @param planePoint   any point on the new clip plane; world coordinates
	 * @param planeNormal  the normal vector of the plane;  world coordinates
	 * @param flipNormal   whether to negate the plane's normal vector
	 * @param modelViewMatrix  the model view matrix, to convert world coordinates to camera coordinates
	 * @param projectionMatrix the existing projection matrix
	 * @param dest             the target projection matrix
	 */
	public void computeObliqueProjectionWorld(PoseStack.Pose pose, Vector3f planePoint, Vector3f planeNormal, boolean flipNormal, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, Matrix4f dest) {
		final Vector3f _planePoint  = this.planePoint;
		final Vector3f _planeNormal = this.planeNormal;
		
		_planeNormal.set(planeNormal);
		if (flipNormal) {
			_planeNormal.negate();
		}
		
		// Transform the oblique plane by the pose it was rendered in
		pose.transformNormal(_planeNormal, _planeNormal);
		pose.pose().transformPosition(planePoint, _planePoint);
		
		// Convert our plane to camera space
		Vector3f planePointCameraSpace  = modelViewMatrix.transformPosition(_planePoint);
		Vector3f planeNormalCameraSpace = modelViewMatrix.transformPosition(_planeNormal);

		// Set clipPlane
		this.clipPlane.set(planeNormalCameraSpace, -planeNormalCameraSpace.dot(planePointCameraSpace));

		// Retrieve matrix data
		projectionMatrix.get(this.matrix);

		// Compute oblique projection and store in dest
		this.computeObliqueProjectionInternal(dest);
	}

	/**
	 * Given a plane in world coordinates, computes a new oblique projection matrix with a
	 * near plane equal to that plane. The resulting projection matrix is stored in {@code dest}.
	 * 
	 * @param planePoint   any point on the new clip plane; world coordinates
	 * @param planeNormal  the normal vector of the plane;  world coordinates
	 * @param flipNormal   whether to negate the plane's normal vector
	 * @param modelViewMatrix  the model view matrix, to convert world coordinates to camera coordinates
	 * @param projectionMatrix the existing projection matrix
	 * @param dest             the target projection matrix
	 */
	public void computeObliqueProjectionWorld(Vector3f planePoint, Vector3f planeNormal, boolean flipNormal, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, Matrix4f dest) {
		final Vector3f _planeNormal = this.planeNormal;
		_planeNormal.set(planeNormal);
		if (flipNormal) {
			_planeNormal.negate();
		}
		
		// Convert our plane to camera space
		Vector3f planePointCameraSpace  = modelViewMatrix.transformPosition(planePoint, this.planePoint);
		Vector3f planeNormalCameraSpace = modelViewMatrix.transformPosition(_planeNormal);

		// Set clipPlane
		this.clipPlane.set(planeNormalCameraSpace, -planeNormalCameraSpace.dot(planePointCameraSpace));

		// Retrieve matrix data
		projectionMatrix.get(this.matrix);

		// Compute oblique projection and store in dest
		this.computeObliqueProjectionInternal(dest);
	}

	/**
	 * Computes a new oblique projection matrix with a near clip plane equal
	 * to {@code nearClipPlane} in {@code projectionMatrix}, and stores the result in {@code dest}.
	 * 
	 * @param nearClipPlane    a vec4 representing the new near clip plane; camera coordinates
	 * @param projectionMatrix the current projection matrix
	 * @param dest             the matrix to store the new projection matrix in
	 */
	public void computeObliqueProjection(Vector4f nearClipPlane, Matrix4f projectionMatrix, Matrix4f dest) {
		// Set clipPlane
		this.clipPlane.set(nearClipPlane);
		
		// Retrieve matrix data
		projectionMatrix.get(this.matrix);

		// Compute oblique projection and store in dest
		this.computeObliqueProjectionInternal(dest);
	}

	/**
	 * Computes an oblique projection matrix from {@link #matrix} and {@link #clipPlane}
	 * and stores the result in {@code dest}.
	 * 
	 * @param dest
	 */
	private void computeObliqueProjectionInternal(Matrix4f dest) {
		final float[] matrix = this.matrix;
		final Vector4f clipPlane = this.clipPlane;
		final Vector4f q = this.q;
		
		// Taken from https://terathon.com/blog/oblique-clipping.html

		// Calculate the clip-space corner point opposite the clipping plane
		// as (sgn(clipPlane.x), sgn(clipPlane.y), 1, 1) and
		// transform it into camera space by multiplying it
		// by the inverse of the projection matrix
		
		q.x = (Math.signum(clipPlane.x) + matrix[8]) / matrix[0];
		q.y = (Math.signum(clipPlane.y) + matrix[9]) / matrix[5];
		q.z = -1.0F;
		q.w = (1.0F + matrix[10]) / matrix[14];
		
		// Calculate the scaled plane vector
		Vector4f c = clipPlane.mul(2.0F / clipPlane.dot(q));
		
		// Replace the third row of the projection matrix
		matrix[2] = c.x;
		matrix[6] = c.y;
		matrix[10] = c.z + 1.0F;
		matrix[14] = c.w;
		
		// Set matrix data
		dest.set(matrix);
	}

	/**
	 * Clears the oblique projection caused by {@link #setupObliqueProjection(PoseStack.Pose, Vector3f, Vector3f, boolean, Matrix4f, Matrix4f)}
	 * @param dest the projection matrix to restore the original projection matrix to
	 */
	public void clearObliqueProjection(Matrix4f dest) {
		// Restore the render system projection matrix
		RenderSystem.getProjectionMatrix().set(this.backupProjectionMatrix);

		// Reset projection matrix
		this.loadOriginalProjectionMatrix(dest);
	}
}
