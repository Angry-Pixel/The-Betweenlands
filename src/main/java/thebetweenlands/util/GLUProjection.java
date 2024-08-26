package thebetweenlands.util;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.mojang.blaze3d.platform.Window;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;

import javax.annotation.Nullable;

/**
 * Helper class to project world space coordinates to screen space coordinates with {@link GLU#gluProject(float, float, float, FloatBuffer, FloatBuffer, IntBuffer, FloatBuffer)}
 *
 */
//FIXME GLU is :crab: in lwjgl 3, the 4 uses of it were commented out
public final class GLUProjection {
	public static class Line {
		public Vector3D sourcePoint = new Vector3D(0, 0, 0);
		public Vector3D direction = new Vector3D(0, 0, 0);
		public Line(double sx, double sy, double sz, double dx, double dy, double dz) {
			this.sourcePoint.x = sx;
			this.sourcePoint.y = sy;
			this.sourcePoint.z = sz;
			this.direction.x = dx;
			this.direction.y = dy;
			this.direction.z = dz;
		}
		public Vector3D intersect(Line line) {
			double a = this.sourcePoint.x;
			double b = this.direction.x;
			double c = line.sourcePoint.x;
			double d = line.direction.x;
			double e = this.sourcePoint.y;
			double f = this.direction.y;
			double g = line.sourcePoint.y;
			double h = line.direction.y;
			double te = -(a*h-c*h-d*(e-g));
			double be = b*h-d*f;
			if(be == 0) {
				return this.intersectXZ(line);
			}
			double t = te / be;
			Vector3D result = new Vector3D(0, 0, 0);
			result.x = this.sourcePoint.x + this.direction.x * t;
			result.y = this.sourcePoint.y + this.direction.y * t;
			result.z = this.sourcePoint.z + this.direction.z * t;
			return result;
		}
		private Vector3D intersectXZ(Line line) {
			double a = this.sourcePoint.x;
			double b = this.direction.x;
			double c = line.sourcePoint.x;
			double d = line.direction.x;
			double e = this.sourcePoint.z;
			double f = this.direction.z;
			double g = line.sourcePoint.z;
			double h = line.direction.z;
			double te = -(a*h-c*h-d*(e-g));
			double be = b*h-d*f;
			if(be == 0) {
				return this.intersectYZ(line);
			}
			double t = te / be;
			Vector3D result = new Vector3D(0, 0, 0);
			result.x = this.sourcePoint.x + this.direction.x * t;
			result.y = this.sourcePoint.y + this.direction.y * t;
			result.z = this.sourcePoint.z + this.direction.z * t;
			return result;
		}
		private Vector3D intersectYZ(Line line) {
			double a = this.sourcePoint.y;
			double b = this.direction.y;
			double c = line.sourcePoint.y;
			double d = line.direction.y;
			double e = this.sourcePoint.z;
			double f = this.direction.z;
			double g = line.sourcePoint.z;
			double h = line.direction.z;
			double te = -(a*h-c*h-d*(e-g));
			double be = b*h-d*f;
			if(be == 0) {
				return null;
			}
			double t = te / be;
			Vector3D result = new Vector3D(0, 0, 0);
			result.x = this.sourcePoint.x + this.direction.x * t;
			result.y = this.sourcePoint.y + this.direction.y * t;
			result.z = this.sourcePoint.z + this.direction.z * t;
			return result;
		}
		public Vector3D intersectPlane(Vector3D pointOnPlane, Vector3D planeNormal) {
			Vector3D result = new Vector3D(this.sourcePoint.x, this.sourcePoint.y, this.sourcePoint.z);
			double d = pointOnPlane.sub(this.sourcePoint).dot(planeNormal)/this.direction.dot(planeNormal);
			result.sadd(this.direction.mul(d));
			if(this.direction.dot(planeNormal) == 0.0D) {
				return null;
			}
			return result;
		}
	}
	public static class Vector3D {
		public double x, y, z;
		public Vector3D(double x, double y, double z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
		public Vector3D add(Vector3D v) {
			return new Vector3D(this.x + v.x, this.y + v.y, this.z + v.z);
		}
		public Vector3D add(double x, double y, double z) {
			return new Vector3D(this.x + x, this.y + y, this.z + z);
		}
		public Vector3D sub(Vector3D v) {
			return new Vector3D(this.x - v.x, this.y - v.y, this.z - v.z);
		}
		public Vector3D sub(double x, double y, double z) {
			return new Vector3D(this.x - x, this.y - y, this.z - z);
		}
		public Vector3D normalized() {
			double len = Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
			return new Vector3D(this.x / len, this.y / len, this.z / len);
		}
		public double dot(Vector3D v) {
			return this.x * v.x + this.y * v.y + this.z * v.z;
		}
		public Vector3D cross(Vector3D v) {
			return new Vector3D(this.y * v.z - this.z * v.y, this.z * v.x - this.x * v.z, this.x * v.y - this.y * v.x);
		}
		public Vector3D mul(double m) {
			return new Vector3D(this.x * m, this.y * m, this.z * m);
		}
		public Vector3D div(double d) {
			return new Vector3D(this.x / d, this.y / d, this.z / d);
		}
		public double length() {
			return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
		}
		public Vector3D sadd(Vector3D v) {
			this.x += v.x;
			this.y += v.y;
			this.z += v.z;
			return this;
		}
		public Vector3D sadd(double x, double y, double z) {
			this.x += x;
			this.y += y;
			this.z += z;
			return this;
		}
		public Vector3D ssub(Vector3D v) {
			this.x -= v.x;
			this.y -= v.y;
			this.z -= v.z;
			return this;
		}
		public Vector3D ssub(double x, double y, double z) {
			this.x -= x;
			this.y -= y;
			this.z -= z;
			return this;
		}
		public Vector3D snormalize() {
			double len = Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
			this.x /= len;
			this.y /= len;
			this.z /= len;
			return this;
		}
		public Vector3D scross(Vector3D v) {
			this.x = this.y * v.z - this.z * v.y;
			this.y = this.z * v.x - this.x * v.z;
			this.z = this.x * v.y - this.y * v.x;
			return this;
		}
		public Vector3D smul(double m) {
			this.x *= m;
			this.y *= m;
			this.z *= m;
			return this;
		}
		public Vector3D sdiv(double d) {
			this.x /= d;
			this.y /= d;
			this.z /= d;
			return this;
		}
		@Override
		public String toString() {
			return "(X: " + this.x + " Y: " + this.y + " Z: " + this.z + ")";
		}
	}
	public static class Projection {
		public enum Type {
			INSIDE, OUTSIDE, INVERTED, FAIL
		}
		private final double x;
		private final double y;
		private final Type t;
		public Projection(double x, double y, Type t) {
			this.x = x;
			this.y = y;
			this.t = t;
		}
		public double getX() {
			return this.x;
		}
		public double getY() {
			return this.y;
		}
		public Type getType() {
			return this.t;
		}
		public boolean isType(Type type) {
			return this.t == type;
		}
	}
	public enum ClampMode { ORTHOGONAL, DIRECT, NONE }

	private GLUProjection() { }
	@Nullable
	private static GLUProjection instance;
	public static GLUProjection getInstance() {
		if(instance == null) {
			instance = new GLUProjection();
		}
		return instance;
	}

	private IntBuffer viewport;
	private FloatBuffer modelview;
	private FloatBuffer projection;
	private final FloatBuffer coords = BufferUtils.createFloatBuffer(3);
	private Vector3D frustumPos;
	private Vector3D[] frustum;
	private Vector3D[] invFrustum;
	private Vector3D viewVec;
	private double displayWidth;
	private double displayHeight;
	private double widthScale;
	private double heightScale;
	private double bra, bla, tra, tla;
	private Line tb, bb, lb, rb;
	private float fovY;
	private float fovX;
	private Vector3D lookVec;

	/**
	 * Updates the matrices. Needed whenever the viewport or one of the matrices has changed.
	 * @param viewport 		Viewport
	 * @param modelview 	Modelview matrix
	 * @param projection 	Projection matrix
	 * @param widthScale 	(GUI Width) / (Display Width)
	 * @param heightScale	(GUI Height) / (Display Height)
	 */
	public void updateMatrices(IntBuffer viewport, FloatBuffer modelview, FloatBuffer projection, double widthScale, double heightScale) {
		this.viewport = viewport;
		this.modelview = modelview;
		this.projection = projection;
		this.widthScale = widthScale;
		this.heightScale = heightScale;

		//Get fov and display dimensions
		float fov = (float)Math.toDegrees(Math.atan(1.0D / this.projection.get(5)) * 2.0D);
		this.fovY = fov;
		this.displayWidth = this.viewport.get(2);
		this.displayHeight = this.viewport.get(3);
		this.fovX = (float) Math.toDegrees(2.0D * Math.atan((this.displayWidth / this.displayHeight) * Math.tan(Math.toRadians(this.fovY) / 2.0D)));
		//Getting modelview vectors
		Vector3D lv = new Vector3D(this.modelview.get(0), this.modelview.get(1), this.modelview.get(2));
		Vector3D uv = new Vector3D(this.modelview.get(4), this.modelview.get(5), this.modelview.get(6));
		Vector3D fv = new Vector3D(this.modelview.get(8), this.modelview.get(9), this.modelview.get(10));
		//Default axes
		Vector3D nuv = new Vector3D(0, 1.0D, 0);
		Vector3D nlv = new Vector3D(1.0D, 0, 0);
		//Calculate yaw and pitch from modelview
		double yaw = Math.toDegrees(Math.atan2(nlv.cross(lv).length(), nlv.dot(lv))) + 180.0D;
		if(fv.x < 0.0D) {
			yaw = 360.0D - yaw;
		}
		double pitch;
		if((-fv.y > 0.0D && yaw >= 90.0D && yaw < 270.0D) || (fv.y > 0.0D && !(yaw >= 90.0D && yaw < 270.0D))) {
			pitch = Math.toDegrees(Math.atan2(nuv.cross(uv).length(), nuv.dot(uv)));
		} else {
			pitch = -Math.toDegrees(Math.atan2(nuv.cross(uv).length(), nuv.dot(uv)));
		}
		this.lookVec = this.getRotationVector(yaw, pitch);
		//Get modelview matrix and invert it
		Matrix4f modelviewMatrix = new Matrix4f();
		modelviewMatrix.set(this.modelview.asReadOnlyBuffer());
		modelviewMatrix.invert();
		//Get frustum position
		this.frustumPos = new Vector3D(modelviewMatrix.m30(), modelviewMatrix.m31(), modelviewMatrix.m32());
		this.frustum = this.getFrustum(this.frustumPos.x, this.frustumPos.y, this.frustumPos.z, yaw, pitch, fov, 1.0F, displayWidth / displayHeight);
		this.invFrustum = this.getFrustum(this.frustumPos.x, this.frustumPos.y, this.frustumPos.z, yaw-180, -pitch, fov, 1.0F, displayWidth / displayHeight);
		//Set view vec
		this.viewVec = this.getRotationVector(yaw, pitch).normalized();
		//Calculate screen border angles
		this.bra = Math.toDegrees(Math.acos((displayHeight * heightScale)/Math.sqrt(displayWidth * widthScale * displayWidth * widthScale + displayHeight * heightScale * displayHeight * heightScale)));
		this.bla = 360 - this.bra;
		this.tra = this.bla - 180;
		this.tla = this.bra + 180;
		//Create screen border lines
		this.rb = new Line(this.displayWidth * this.widthScale, 0, 0, 0, 1, 0);
		this.tb = new Line(0, 0, 0, 1, 0, 0);
		this.lb = new Line(0, 0, 0, 0, 1, 0);
		this.bb = new Line(0, this.displayHeight * this.heightScale, 0, 1, 0, 0);
	}

	/**
	 * Uses {@link GLU#gluProject(float, float, float, FloatBuffer, FloatBuffer, IntBuffer, FloatBuffer)} to project world space coordinates to screen space coordinates.
	 * @param x						X position
	 * @param y						Y position
	 * @param z						Z position
	 * @param clampModeOutside		Clamp mode used when the point is outside
	 * 								the normal and inverted frustum
	 * @param extrudeInverted		If set to true this extrudes the projected point
	 * 								onto the screen borders if the point is inside
	 * 								the inverted frustum
	 * @return
	 */
	public Projection project(double x, double y, double z, ClampMode clampModeOutside, boolean extrudeInverted) {
		if(this.viewport != null && this.modelview != null && this.projection != null) {
			Vector3D posVec = new Vector3D(x, y, z);
			boolean[] frustum = this.doFrustumCheck(this.frustum, this.frustumPos, x, y, z);
			boolean outsideFrustum = frustum[0] || frustum[1] || frustum[2] || frustum[3];
			//Check if point is inside frustum
			if(outsideFrustum) {
				//Check if point is on opposite side of the near clip plane
				boolean opposite = posVec.sub(this.frustumPos).dot(this.viewVec) <= 0.0D;
				//Get inverted frustum check
				boolean[] invFrustum = this.doFrustumCheck(this.invFrustum, this.frustumPos, x, y, z);
				boolean outsideInvertedFrustum = invFrustum[0] || invFrustum[1] || invFrustum[2] || invFrustum[3];
				if((extrudeInverted && !outsideInvertedFrustum) || (outsideInvertedFrustum && clampModeOutside != ClampMode.NONE)) {
					if((extrudeInverted && !outsideInvertedFrustum) ||
							(clampModeOutside == ClampMode.DIRECT && outsideInvertedFrustum)) {
						//Point in inverted frustum, has to be clamped
						double vecX;
						double vecY;
						if(false/*GLU.gluProject((float) x, (float) y, (float) z, this.modelview, this.projection, this.viewport, this.coords)*/) {
							//Get projected coordinates
							if(opposite) {
								//Invert coordinates
								vecX = this.displayWidth * this.widthScale - (double)this.coords.get(0) * this.widthScale - this.displayWidth * this.widthScale / 2.0F;
								vecY = this.displayHeight * this.heightScale - (displayHeight - (double)this.coords.get(1)) * this.heightScale - this.displayHeight * this.heightScale / 2.0F;
							} else {
								vecX = (double)this.coords.get(0) * this.widthScale - this.displayWidth * this.widthScale / 2.0F;
								vecY = (this.displayHeight - (double)this.coords.get(1)) * this.heightScale - this.displayHeight * this.heightScale / 2.0F;
							}
						} else {
							return new Projection(0, 0, Projection.Type.FAIL);
						}
						//Normalize point direction vector
						Vector3D vec = new Vector3D(vecX, vecY, 0).snormalize();
						vecX = vec.x;
						vecY = vec.y;
						//Get vector line
						Line vectorLine = new Line(this.displayWidth * this.widthScale / 2.0F, this.displayHeight * this.heightScale / 2.0F, 0, vecX, vecY, 0);
						//Calculate angle of point on 2D plane relative to the screen center
						double angle = Math.toDegrees(Math.acos((vec.y)/Math.sqrt(vec.x * vec.x + vec.y * vec.y)));
						if(vecX < 0.0D) {
							angle = 360.0D - angle;
						}
						//Calculate screen border intersections
						new Vector3D(0, 0, 0);
						Vector3D intersect;
						//Check which screen border to intersect
						if(angle >= this.bra && angle < this.tra) {
							//Right
							intersect = this.rb.intersect(vectorLine);
						} else if(angle >= this.tra && angle < this.tla) {
							//Top
							intersect = this.tb.intersect(vectorLine);
						} else if(angle >= this.tla && angle < this.bla) {
							//Left
							intersect = this.lb.intersect(vectorLine);
						} else {
							//Bottom
							intersect = this.bb.intersect(vectorLine);
						}
						return new Projection(intersect.x, intersect.y, outsideInvertedFrustum ? Projection.Type.OUTSIDE : Projection.Type.INVERTED);
					} else if((clampModeOutside == ClampMode.ORTHOGONAL && outsideInvertedFrustum)) {
						if(false/*GLU.gluProject((float) x, (float) y, (float) z, this.modelview, this.projection, this.viewport, this.coords)*/) {
							//Get projected coordinates
							double guiX = (double)this.coords.get(0) * this.widthScale;
							double guiY = (this.displayHeight - (double)this.coords.get(1)) * this.heightScale;
							if(opposite) {
								//Invert coordinates
								guiX = this.displayWidth * this.widthScale - guiX;
								guiY = this.displayHeight * this.heightScale - guiY;
							}
							if(guiX < 0) {
								guiX = 0;
							} else if(guiX > this.displayWidth * this.widthScale) {
								guiX = this.displayWidth * this.widthScale;
							}
							if(guiY < 0) {
								guiY = 0;
							} else if(guiY > this.displayHeight * this.heightScale) {
								guiY = this.displayHeight * this.heightScale;
							}
							return new Projection(guiX, guiY, outsideInvertedFrustum ? Projection.Type.OUTSIDE : Projection.Type.INVERTED);
						} else {
							return new Projection(0, 0, Projection.Type.FAIL);
						}
					}
				} else {
					//Return point without clamping
					if(false/*GLU.gluProject((float) x, (float) y, (float) z, this.modelview, this.projection, this.viewport, this.coords)*/) {
						//Get projected coordinates
						double guiX = (double)this.coords.get(0) * this.widthScale;
						double guiY = (this.displayHeight - (double)this.coords.get(1)) * this.heightScale;
						if(opposite) {
							//Invert coordinates
							guiX = this.displayWidth * this.widthScale - guiX;
							guiY = this.displayHeight * this.heightScale - guiY;
						}
						return new Projection(guiX, guiY, outsideInvertedFrustum ? Projection.Type.OUTSIDE : Projection.Type.INVERTED);
					} else {
						return new Projection(0, 0, Projection.Type.FAIL);
					}
				}
			} else {
				//Point inside frustum, can be projected normally
				if(false/*GLU.gluProject((float) x, (float) y, (float) z, this.modelview, this.projection, this.viewport, this.coords)*/) {
					//Get projected coordinates
					double guiX = (double)this.coords.get(0) * this.widthScale;
					double guiY = (this.displayHeight - (double)this.coords.get(1)) * this.heightScale;
					return new Projection(guiX, guiY, Projection.Type.INSIDE);
				} else {
					return new Projection(0, 0, Projection.Type.FAIL);
				}
			}
		}
		return new Projection(0, 0, Projection.Type.FAIL);
	}

	/**
	 * Performs a frustum check.
	 * @param frustumCorners		Frustum corners
	 * @param frustumPos			Frustum position
	 * @param x						X position
	 * @param y						Y position
	 * @param z						Z position
	 * @return
	 */
	public boolean[] doFrustumCheck(Vector3D[] frustumCorners, Vector3D frustumPos, double x, double y, double z) {
		Vector3D point = new Vector3D(x, y, z);
		boolean c1 = crossPlane(new Vector3D[]{frustumPos, frustumCorners[3], frustumCorners[0]}, point);
		boolean c2 = crossPlane(new Vector3D[]{frustumPos, frustumCorners[0], frustumCorners[1]}, point);
		boolean c3 = crossPlane(new Vector3D[]{frustumPos, frustumCorners[1], frustumCorners[2]}, point);
		boolean c4 = crossPlane(new Vector3D[]{frustumPos, frustumCorners[2], frustumCorners[3]}, point);
		return new boolean[]{c1, c2, c3, c4};
	}

	/**
	 * Returns true if the given plane has been crossed by the point.
	 * @param plane		Vector3D[] that describes the plane
	 * @param point		Vector3D that describes the point
	 * @return
	 */
	public boolean crossPlane(Vector3D[] plane, Vector3D point) {
		Vector3D z = new Vector3D(0.0D, 0.0D, 0.0D);
		Vector3D e0 = plane[1].sub(plane[0]);
		Vector3D e1 = plane[2].sub(plane[0]);
		Vector3D normal = e0.cross(e1).snormalize();
		double D = (z.sub(normal)).dot(plane[2]);
		double dist = normal.dot(point) + D;
		return dist >= 0.0D;
	}

	/**
	 * Returns the frustum corner points
	 * 0 -------- 3
	 * |          |
	 * |          |
	 * 1 -------- 2
	 *
	 * @param x					X position
	 * @param y					Y position
	 * @param z					Z position
	 * @param rotationYaw		Yaw
	 * @param rotationPitch		Pitch
	 * @param fov				FOV
	 * @param farDistance		Far plane distance
	 * @param aspectRatio		(Display width) / (Display height)
	 * @return
	 */
	public Vector3D[] getFrustum(double x, double y, double z, double rotationYaw, double rotationPitch, double fov, double farDistance, double aspectRatio) {
		double hFar = 2D * Math.tan(Math.toRadians(fov / 2D)) * farDistance;
		double wFar = hFar * aspectRatio;
		Vector3D view = this.getRotationVector(rotationYaw, rotationPitch).snormalize();
		Vector3D up = this.getRotationVector(rotationYaw, rotationPitch - 90).snormalize();
		Vector3D right = this.getRotationVector(rotationYaw + 90, 0).snormalize();
		Vector3D camPos = new Vector3D(x, y, z);
		Vector3D view_camPos_product = view.add(camPos);
		Vector3D fc = new Vector3D(view_camPos_product.x * farDistance, view_camPos_product.y * farDistance, view_camPos_product.z * farDistance);
		Vector3D topLeftfrustum = new Vector3D(fc.x + (up.x * hFar / 2D) - (right.x * wFar / 2D), fc.y + (up.y * hFar / 2D) - (right.y * wFar / 2D), fc.z + (up.z * hFar / 2D) - (right.z * wFar / 2D));
		Vector3D downLeftfrustum = new Vector3D(fc.x - (up.x * hFar / 2D) - (right.x * wFar / 2D), fc.y - (up.y * hFar / 2D) - (right.y * wFar / 2D), fc.z - (up.z * hFar / 2D) - (right.z * wFar / 2D));
		Vector3D topRightfrustum = new Vector3D(fc.x + (up.x * hFar / 2D) + (right.x * wFar / 2D), fc.y + (up.y * hFar / 2D) + (right.y * wFar / 2D), fc.z + (up.z * hFar / 2D) + (right.z * wFar / 2D));
		Vector3D downRightfrustum = new Vector3D(fc.x - (up.x * hFar / 2D) + (right.x * wFar / 2D), fc.y - (up.y * hFar / 2D) + (right.y * wFar / 2D), fc.z - (up.z * hFar / 2D) + (right.z * wFar / 2D));
		return new Vector3D[]{topLeftfrustum, downLeftfrustum, downRightfrustum, topRightfrustum};
	}

	/**
	 * Returns the frustum that has been constructed with {@link GLUProjection#updateMatrices(IntBuffer, FloatBuffer, FloatBuffer, double, double)}
	 * 0 -------- 3
	 * |          |
	 * |          |
	 * 1 -------- 2
	 *
	 * @return
	 */
	public Vector3D[] getFrustum() {
		return this.frustum;
	}

	/**
	 * Returns the horizontal fov angle
	 * @return
	 */
	public float getFovX() {
		return this.fovX;
	}

	/**
	 * Returns the vertical fov angle
	 * @return
	 */
	public float getFovY() {
		return this.fovY;
	}

	/**
	 * Returns the normalized look vector
	 * @return
	 */
	public Vector3D getLookVector() {
		return this.lookVec;
	}

	/**
	 * Returns a rotated vector with the given yaw and pitch.
	 * @param rotYaw	Yaw
	 * @param rotPitch	Pitch
	 * @return
	 */
	public Vector3D getRotationVector(double rotYaw, double rotPitch) {
		double c = Math.cos(-rotYaw * 0.017453292F - Math.PI);
		double s = Math.sin(-rotYaw * 0.017453292F - Math.PI);
		double nc = -Math.cos(-rotPitch * 0.017453292F);
		double ns = Math.sin(-rotPitch * 0.017453292F);
		return new Vector3D(s * nc, ns, c * nc);
	}

	private static final IntBuffer VIEWPORT_BUFFER = BufferUtils.createIntBuffer(16);
	private static final FloatBuffer MODELVIEW_BUFFER = BufferUtils.createFloatBuffer(16);
	private static final FloatBuffer PROJECTION_BUFFER = BufferUtils.createFloatBuffer(16);

	public void renderWorld(RenderLevelStageEvent event) {
		if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SKY) {
			GL11.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, MODELVIEW_BUFFER);
			GL11.glGetFloatv(GL11.GL_PROJECTION_MATRIX, PROJECTION_BUFFER);
			GL11.glGetIntegerv(GL11.GL_VIEWPORT, VIEWPORT_BUFFER);
			Window window = Minecraft.getInstance().getWindow();
			GLUProjection.getInstance().updateMatrices(VIEWPORT_BUFFER, MODELVIEW_BUFFER, PROJECTION_BUFFER,
				(float) window.getWidth() / (float) window.getScreenWidth(),
				(float) window.getHeight() / (float) window.getScreenHeight());
		}
	}
}
