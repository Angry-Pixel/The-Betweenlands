package thebetweenlands.util;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import thebetweenlands.util.Mesh.Triangle.Vertex;
import thebetweenlands.util.Mesh.Triangle.Vertex.Vector3D;

public class Mesh {
	public record Triangle(Mesh.Triangle.Vertex v1, Mesh.Triangle.Vertex v2, Mesh.Triangle.Vertex v3) {

		public record Vertex(double x, double y, double z, Triangle.Vertex.Vector3D normal, int color) {

			public record Vector3D(double x, double y, double z) {

				public Vector3D normalized() {
					double length = Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
					return new Vector3D(this.x / length, this.y / length, this.z / length);
				}
			}
		}
	}

	private int vertexBufferID = -1;
	private int colorBufferID = -1;
	private int normalBufferID = -1;
	private int vertices = 0;

	public Mesh(List<Triangle> triangles) {
		if(triangles.isEmpty()) {
			return;
		}
		this.vertexBufferID = GL15.glGenBuffers();
		this.colorBufferID = GL15.glGenBuffers();
		this.normalBufferID = GL15.glGenBuffers();
		this.vertices = triangles.size() * 3;
		DoubleBuffer vertexBuffer = BufferUtils.createDoubleBuffer(this.vertices * 3);
		FloatBuffer colorBuffer = BufferUtils.createFloatBuffer(this.vertices * 4);
		FloatBuffer normalBuffer = BufferUtils.createFloatBuffer(this.vertices * 3);
		for(Triangle tri : triangles) {
			vertexBuffer.put(new double[]{tri.v1.x, tri.v1.y, tri.v1.z});
			vertexBuffer.put(new double[]{tri.v2.x, tri.v2.y, tri.v2.z});
			vertexBuffer.put(new double[]{tri.v3.x, tri.v3.y, tri.v3.z});
			colorBuffer.put(this.getRGBAFromHex(tri.v1.color));
			colorBuffer.put(this.getRGBAFromHex(tri.v2.color));
			colorBuffer.put(this.getRGBAFromHex(tri.v3.color));
			normalBuffer.put(new float[]{(float)tri.v1.normal.x, (float)tri.v1.normal.y, (float)tri.v1.normal.z});
			normalBuffer.put(new float[]{(float)tri.v2.normal.x, (float)tri.v2.normal.y, (float)tri.v2.normal.z});
			normalBuffer.put(new float[]{(float)tri.v3.normal.x, (float)tri.v3.normal.y, (float)tri.v3.normal.z});
		}
		this.createVertexBuffer(vertexBuffer.flip());
		this.createColorBuffer(colorBuffer.flip());
		this.createNormalBuffer(normalBuffer.flip());
		if(this.vertexBufferID == -1 || this.colorBufferID == -1 || this.normalBufferID == -1) {
			System.err.println("Invalid buffer IDs!");
		}
	}

	private void createVertexBuffer(DoubleBuffer buffer) {
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.vertexBufferID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	private void createColorBuffer(FloatBuffer buffer) {
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.colorBufferID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	private void createNormalBuffer(FloatBuffer buffer) {
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.normalBufferID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	private float[] getRGBAFromHex(int hex) {
		float[] rgba = new float[4];
		rgba[0] = (hex >> 16 & 0xFF) / 255.0F;
		rgba[1] = (hex >> 8 & 0xFF) / 255.0F;
		rgba[2] = (hex & 0xFF) / 255.0F;
		rgba[3] = (hex >> 24 & 0xFF) / 255.0F;
		return rgba;
	}

	public void render() {
		if(this.vertexBufferID == -1 || this.colorBufferID == -1 || this.normalBufferID == -1) {
			System.err.println("Invalid buffer IDs!");
			return;
		}
		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.vertexBufferID);
		GL11.glVertexPointer(3, GL11.GL_DOUBLE, 0, 0);

		GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.colorBufferID);
		GL11.glColorPointer(4, GL11.GL_FLOAT, 0, 0);

		GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.normalBufferID);
		GL11.glNormalPointer(GL11.GL_FLOAT, 0, 0);

		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, this.vertices);

		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
		GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	public void destroy() {
		if(this.vertexBufferID == -1 || this.colorBufferID == -1 || this.normalBufferID == -1) {
			System.err.println("Invalid buffer IDs!");
			return;
		}
		GL15.glDeleteBuffers(this.vertexBufferID);
		GL15.glDeleteBuffers(this.colorBufferID);
		GL15.glDeleteBuffers(this.normalBufferID);
	}

	public static List<Triangle> createBoxOccluded(double x, double y, double z, double x2, double y2, double z2, boolean front, boolean back, boolean bottom, boolean top, boolean right, boolean left, int colorFront, int colorBack, int colorBottom, int colorTop, int colorRight, int colorLeft) {
		double width = x2 - x;
		double height = y2 - y;
		double depth = z2 - z;
		List<Triangle> triangles = new ArrayList<>();
		if(front) {
			//Front face
			triangles.add(new Triangle(
					new Vertex(x + width, y + height, z, new Vector3D(0, 0, 1), colorFront),
					new Vertex(x + width, y, z, new Vector3D(0, 0, 1), colorFront),
					new Vertex(x, y, z, new Vector3D(0, 0, 1), colorFront)
					));
			triangles.add(new Triangle(
					new Vertex(x, y, z, new Vector3D(0, 0, 1), colorFront),
					new Vertex(x, y + height, z, new Vector3D(0, 0, 1), colorFront),
					new Vertex(x + width, y + height, z, new Vector3D(0, 0, 1), colorFront)
					));
		}
		if(back) {
			//Back face
			triangles.add(new Triangle(
					new Vertex(x, y, z + depth, new Vector3D(0, 0, -1), colorBack),
					new Vertex(x + width, y, z + depth, new Vector3D(0, 0, -1), colorBack),
					new Vertex(x + width, y + height, z + depth, new Vector3D(0, 0, -1), colorBack)));
			triangles.add(new Triangle(
					new Vertex(x + width, y + height, z + depth, new Vector3D(0, 0, -1), colorBack),
					new Vertex(x, y + height, z + depth, new Vector3D(0, 0, -1), colorBack),
					new Vertex(x, y, z + depth, new Vector3D(0, 0, -1), colorBack)));
		}
		if(bottom) {
			//Bottom face
			triangles.add(new Triangle(
					new Vertex(x, y, z, new Vector3D(0, -1, 0), colorBottom),
					new Vertex(x + width, y, z, new Vector3D(0, -1, 0), colorBottom),
					new Vertex(x + width, y, z + depth, new Vector3D(0, -1, 0), colorBottom)));
			triangles.add(new Triangle(
					new Vertex(x + width, y, z + depth, new Vector3D(0, -1, 0), colorBottom),
					new Vertex(x, y, z + depth, new Vector3D(0, -1, 0), colorBottom),
					new Vertex(x, y, z, new Vector3D(0, -1, 0), colorBottom)));
		}
		if(top) {
			//Top face
			triangles.add(new Triangle(
					new Vertex(x + width, y + height, z + depth, new Vector3D(0, 1, 0), colorTop),
					new Vertex(x + width, y + height, z, new Vector3D(0, 1, 0), colorTop),
					new Vertex(x, y + height, z, new Vector3D(0, 1, 0), colorTop)));
			triangles.add(new Triangle(
					new Vertex(x, y + height, z, new Vector3D(0, 1, 0), colorTop),
					new Vertex(x, y + height, z + depth, new Vector3D(0, 1, 0), colorTop),
					new Vertex(x + width, y + height, z + depth, new Vector3D(0, 1, 0), colorTop)));
		}
		if(right) {
			//Right face
			triangles.add(new Triangle(
					new Vertex(x, y, z, new Vector3D(-1, 0, 0), colorRight),
					new Vertex(x, y, z + depth, new Vector3D(-1, 0, 0), colorRight),
					new Vertex(x, y + height, z + depth, new Vector3D(-1, 0, 0), colorRight)));
			triangles.add(new Triangle(
					new Vertex(x, y + height, z + depth, new Vector3D(-1, 0, 0), colorRight),
					new Vertex(x, y + height, z, new Vector3D(-1, 0, 0), colorRight),
					new Vertex(x, y, z, new Vector3D(-1, 0, 0), colorRight)));
		}
		if(left) {
			//Left face
			triangles.add(new Triangle(
					new Vertex(x + width, y + height, z + depth, new Vector3D(1, 0, 0), colorLeft),
					new Vertex(x + width, y, z + depth, new Vector3D(1, 0, 0), colorLeft),
					new Vertex(x + width, y, z, new Vector3D(1, 0, 0), colorLeft)));
			triangles.add(new Triangle(
					new Vertex(x + width, y, z, new Vector3D(1, 0, 0), colorLeft),
					new Vertex(x + width, y + height, z, new Vector3D(1, 0, 0), colorLeft),
					new Vertex(x + width, y + height, z + depth, new Vector3D(1, 0, 0), colorLeft)));
		}
		return triangles;
	}
}