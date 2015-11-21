package thebetweenlands.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;

public class ModelConverter {
	//Holds the rotation matrix
	private static final RotationMatrix ROTATION_MATRIX = new RotationMatrix();

	//The field of ModelBox#quadList
	private static Field f_mbQuadList = null;

	public static class Vec3 {
		public double x, y, z, u, v, uw, vw;
		public Vec3(double x, double y, double z) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.u = 0.0D;
			this.v = 0.0D;
			this.uw = 1.0D;
			this.vw = 1.0D;
		}
		public Vec3(double x, double y, double z, double u, double v, double uw, double vw) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.u = u;
			this.v = v;
			this.uw = uw;
			this.vw = vw;
		}
		public Vec3(Vec3 vec, double u, double v, double uw, double vw) {
			this.x = vec.x;
			this.y = vec.y;
			this.z = vec.z;
			this.u = u;
			this.v = v;
			this.uw = uw;
			this.vw = vw;
		}
		public Vec3(double u, double v, double uw, double vw) {
			this.x = 0.0D;
			this.y = 0.0D;
			this.z = 0.0D;
			this.u = u;
			this.v = v;
			this.uw = uw;
			this.vw = vw;
		}
		public Vec3(Vec3 vec) {
			this.x = vec.x;
			this.y = vec.y;
			this.z = vec.z;
			this.u = vec.u;
			this.v = vec.v;
			this.uw = vec.uw;
			this.vw = vec.vw;
		}
		public Vec3 cross(Vec3 vec) {
			Vec3 crossProduct = new Vec3(0, 0, 0);
			crossProduct.x = this.y * vec.z - vec.y * this.z;
			crossProduct.y = this.z * vec.x - vec.z * this.x;
			crossProduct.z = this.x * vec.y - vec.x * this.y;
			return crossProduct;
		}
		public Vec3 neg() {
			return new Vec3(-this.x, -this.y, -this.z);
		}
		public double getU(IIcon icon, int width) {
			double umin = icon.getMinU();
			double umax = icon.getMaxU();
			return umin + (umax - umin) * this.uw / (double)width * this.u;
		}
		public double getV(IIcon icon, int height) {
			double vmin = icon.getMinV();
			double vmax = icon.getMaxV();
			return vmin + (vmax - vmin) * this.vw / (double)height * this.v;
		}
	}

	public static class RotationMatrix {
		private float matrix[] = new float[9];

		private float rotG;
		private float rotB;
		private float rotA;

		/**
		 * Sets the matrix rotations.
		 * @param rotG		Rotation gamma (pitch)
		 * @param rotB		Rotation beta (yaw)
		 * @param rotA		Rotation alpha (roll)
		 */
		public void setRotations(float rotG, float rotB, float rotA) {
			if(this.rotA == rotA && this.rotB == rotB && this.rotG == rotG) {
				return;
			}
			this.rotA = rotA;
			this.rotB = rotB;
			this.rotG = rotG;
			float sinAlpha = (float)Math.sin(rotA);
			float sinBeta = (float)Math.sin(rotB);
			float sinGamma = (float)Math.sin(rotG);
			float cosAlpha = (float)Math.cos(rotA);
			float cosBeta = (float)Math.cos(rotB);
			float cosGamma = (float)Math.cos(rotG);
			this.matrix[0] = cosAlpha * cosBeta;
			this.matrix[1] = (cosAlpha * sinBeta * sinGamma) - (sinAlpha * cosGamma); 
			this.matrix[2] = (cosAlpha * sinBeta * cosGamma) + (sinAlpha * sinGamma);
			this.matrix[3] = sinAlpha * cosBeta;
			this.matrix[4] = (sinAlpha * sinBeta * sinGamma) + (cosAlpha * cosGamma);
			this.matrix[5] = (sinAlpha * sinBeta * cosGamma) - (cosAlpha * sinGamma);
			this.matrix[6] = -sinBeta;
			this.matrix[7] = cosBeta * sinGamma;
			this.matrix[8] = cosBeta * cosGamma;
		}

		/**
		 * Transforms/Rotates the given point around the given center and returns the result.
		 * @param point			Point to rotate
		 * @param centerPoint	Rotation center
		 * @return
		 */
		public Vec3 transformVec(Vec3 point, Vec3 centerPoint) {
			double px = point.x - centerPoint.x;
			double py = point.y - centerPoint.y;
			double pz = point.z - centerPoint.z;

			Vec3 result = new Vec3(0, 0, 0);

			result.x = this.matrix[0] * px + this.matrix[1] * py + this.matrix[2] * pz;
			result.y = this.matrix[3] * px + this.matrix[4] * py + this.matrix[5] * pz;
			result.z = this.matrix[6] * px + this.matrix[7] * py + this.matrix[8] * pz;

			result.x += centerPoint.x;
			result.y += centerPoint.y;
			result.z += centerPoint.z;

			return result;
		}
	}

	public static final class Quad {
		private Vec3[] vertices = new Vec3[4];
		private Quad(Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4) {
			this.vertices[0] = v1;
			this.vertices[1] = v2;
			this.vertices[2] = v3;
			this.vertices[3] = v4;
		}

		/**
		 * Returns the vertices of this quad.
		 * @return
		 */
		public Vec3[] getVertices() {
			return this.vertices;
		}
	}

	public static final class Box {
		private final Quad[] quads;
		private final ModelRenderer modelRenderer;
		private final ModelBox modelBox;
		private final List<Box> childBoxes = new ArrayList<Box>();

		private Box(Quad[] quads, ModelRenderer modelRenderer, ModelBox modelBox) {
			this.quads = quads;
			this.modelRenderer = modelRenderer;
			this.modelBox = modelBox;
		}

		/**
		 * Returns the quads of this box.
		 * @return
		 */
		public Quad[] getQuads() {
			return this.quads;
		}

		/**
		 * Returns the ModelRenderer box of this box.
		 * @return
		 */
		public ModelRenderer getModelRenderer() {
			return this.modelRenderer;
		}

		/**
		 * Returns the ModelBox of this box.
		 * @return
		 */
		public ModelBox getModelBox() {
			return this.modelBox;
		}

		/**
		 * Returns the list of all child boxes of this box.
		 * @return
		 */
		public List<Box> getChildBoxes() {
			return this.childBoxes;
		}

		/**
		 * Rotates the box and its child boxes.
		 * @param rotation		Rotation (degrees)
		 * @param x				X axis (pitch)
		 * @param y				Y axis (yaw)
		 * @param z				Z axis (roll)
		 * @param center		Rotation center
		 * @return
		 */
		public Box rotate(float rotation, float x, float y, float z, Vec3 center) {
			ROTATION_MATRIX.setRotations((float)Math.toRadians(x * rotation), (float)Math.toRadians(y * rotation), (float)Math.toRadians(z * rotation));
			for(Box box : this.childBoxes) {
				box.rotate(rotation, x, y, z, center);
			}
			for(Quad quad : this.quads) {
				for(int i = 0; i < 4; i++) {
					Vec3 vec = quad.vertices[i];
					Vec3 rotatedPoint = null;
					rotatedPoint = ROTATION_MATRIX.transformVec(vec, center);
					vec.x = rotatedPoint.x;
					vec.y = rotatedPoint.y;
					vec.z = rotatedPoint.z;
				}
			}
			return this;
		}
	}

	public static class Model {
		private final Vec3 fwdVec;
		private final Vec3 upVec;
		private List<Box> modelBoxes = new ArrayList<Box>();

		private Model(List<Box> modelBoxList, Vec3 fwdVec, Vec3 upVec) {
			//Old, New
			Map<Box, Box> copyReference = new HashMap<Box, Box>();
			this.fwdVec = new Vec3(fwdVec);
			this.upVec = new Vec3(upVec);
			//Copy boxes
			for(Box box : modelBoxList) {
				Quad[] quads = new Quad[box.quads.length];
				for(int i = 0; i < box.quads.length; i++) {
					Quad quad = box.quads[i];
					quads[i] = new Quad(
							new Vec3(quad.vertices[0]),
							new Vec3(quad.vertices[1]),
							new Vec3(quad.vertices[2]),
							new Vec3(quad.vertices[3]));
				}
				Box b = new Box(quads, box.modelRenderer, box.modelBox);
				this.modelBoxes.add(b);
				copyReference.put(box, b);
			}
			//Copy child boxes
			for(Box box : modelBoxList) {
				Box newBox = copyReference.get(box);
				for(Box childBox : box.childBoxes) {
					newBox.childBoxes.add(copyReference.get(childBox));
				}
			}
		}

		/**
		 * Offsets the model vertices in world space.
		 * @param offset	Offset
		 * @return
		 */
		public Model offsetWS(Vec3 offset) {
			for(Box box : this.modelBoxes) {
				for(Quad quad : box.quads) {
					for(int i = 0; i < 4; i++) {
						Vec3 vec = quad.vertices[i];
						vec.x += offset.x;
						vec.y += offset.y;
						vec.z += offset.z;
					}
				}
			}
			return this;
		}

		/**
		 * Offsets the model vertices in model space.
		 * @param offset
		 * @return
		 */
		public Model offsetMS(Vec3 offset) {
			Vec3 leftVec = this.fwdVec.cross(this.upVec);
			for(Box box : this.modelBoxes) {
				for(Quad quad : box.quads) {
					for(int i = 0; i < 4; i++) {
						Vec3 vec = quad.vertices[i];
						vec.x += this.upVec.x * offset.y + this.fwdVec.x * offset.z + leftVec.x * offset.x;
						vec.y += this.upVec.y * offset.y + this.fwdVec.y * offset.z + leftVec.y * offset.x;
						vec.z += this.upVec.z * offset.y + this.fwdVec.z * offset.z + leftVec.z * offset.x;
					}
				}
			}
			return this;
		}

		/**
		 * Rotates the model vertices.
		 * @param rotation		Rotation (degrees)
		 * @param x				X axis (pitch)
		 * @param y				Y axis (yaw)
		 * @param z				Z axis (roll)
		 * @param center		Rotation center
		 * @return
		 */
		public Model rotate(float rotation, float x, float y, float z, Vec3 center) {
			ROTATION_MATRIX.setRotations((float)Math.toRadians(x * rotation), (float)Math.toRadians(y * rotation), (float)Math.toRadians(z * rotation));
			for(Box box : this.modelBoxes) {
				for(Quad quad : box.quads) {
					for(int i = 0; i < 4; i++) {
						Vec3 vec = quad.vertices[i];
						Vec3 rotatedPoint = null;
						rotatedPoint = ROTATION_MATRIX.transformVec(vec, center);
						vec.x = rotatedPoint.x;
						vec.y = rotatedPoint.y;
						vec.z = rotatedPoint.z;
					}
				}
			}
			Vec3 rotatedFwdVec = ROTATION_MATRIX.transformVec(this.fwdVec, center);
			this.fwdVec.x = rotatedFwdVec.x;
			this.fwdVec.y = rotatedFwdVec.y;
			this.fwdVec.z = rotatedFwdVec.z;
			Vec3 rotatedUpVec = ROTATION_MATRIX.transformVec(this.upVec, center);
			this.upVec.x = rotatedUpVec.x;
			this.upVec.y = rotatedUpVec.y;
			this.upVec.z = rotatedUpVec.z;
			return this;
		}

		/**
		 * Scales the model vertices.
		 * @param x       X Scale
		 * @param y       Y Scale
		 * @param z       Z Scale
		 * @return
		 */
		public Model scale(double x, double y, double z) {
			for(Box box : this.modelBoxes) {
				for(Quad quad : box.quads) {
					for(int i = 0; i < 4; i++) {
						Vec3 vec = quad.vertices[i];
						vec.x *= x;
						vec.y *= y;
						vec.z *= z;
					}
				}
			}
			return this;
		}

		/**
		 * Returns the list of the reconstructed boxes of this model.
		 * @return
		 */
		public List<Box> getBoxes() {
			return this.modelBoxes;
		}

		/**
		 * Renders this model with the given tessellator. 
		 * The tessellator must already be drawing.
		 * @param tessellator
		 */
		public void renderWithTessellator(Tessellator tessellator, int width, int height, IIcon icon) {
			for(Box box : this.modelBoxes) {
				for(Quad quad : box.quads) {
					for(int i = 0; i < 4; i++) {
						Vec3 vec = quad.vertices[i];
						tessellator.addVertexWithUV(vec.x, vec.y, vec.z, vec.getU(icon, width), vec.getV(icon, height));
					}
				}
			}
		}
	}

	//Holds a list of vertices and UVs of this model
	private final List<Box> modelBoxList = new ArrayList<Box>();

	//Holds the parent component of each ModelRenderer of this model
	private final Map<ModelRenderer, ModelRenderer> childOfMap = new HashMap<ModelRenderer, ModelRenderer>();

	//Holds a list of all parent components and sub-parent components of each ModelRenderer of this model
	private final Map<ModelRenderer, List<ModelRenderer>> parentMap = new HashMap<ModelRenderer, List<ModelRenderer>>();

	private final Map<ModelRenderer, List<Box>> modelRendererBoxMap = new HashMap<ModelRenderer, List<Box>>();

	private final Vec3 fwdVec = new Vec3(0, 0, 1);
	private final Vec3 upVec = new Vec3(0, -1, 0);

	/**
	 * Creates a new ModelConverter that converts a minecraft model to a list of vertices and UVs.
	 * @param model					The model
	 * @param scale					Scale of the model (usually 0.065)
	 * @param renderDoubleFace		Set to true if the faces should be rendered in both directions
	 */
	public ModelConverter(ModelBase model, double scale, boolean renderDoubleFace) {
		this(model, scale, renderDoubleFace, 0, 0, 0);
	}

	/**
	 * Creates a new ModelConverter that converts a minecraft model to a list of vertices and UVs.
	 * @param model					The model
	 * @param scale					Scale of the model (usually 0.065)
	 * @param renderDoubleFace		Set to true if the faces should be rendered in both directions
	 * @param rotationX				Rotation around X axis (degrees)
	 * @param rotationY				Rotation around Y axis (degrees)
	 * @param rotationZ				Rotation around Z axis (degrees)
	 */
	public ModelConverter(ModelBase model, double scale, boolean renderDoubleFace, float rotationX, float rotationY, float rotationZ) {
		this(model, scale, renderDoubleFace, rotationX, rotationY, rotationZ, new Vec3(0, 0, 0));
	}

	/**
	 * Creates a new ModelConverter that converts a minecraft model to a list of vertices and UVs.
	 * @param model					The model
	 * @param scale					Scale of the model (usually 0.065)
	 * @param renderDoubleFace		Set to true if the faces should be rendered in both directions
	 * @param rotationX				Rotation around X axis (degrees)
	 * @param rotationY				Rotation around Y axis (degrees)
	 * @param rotationZ				Rotation around Z axis (degrees)
	 * @param rotaitonCenter		Center of the rotation
	 */
	public ModelConverter(ModelBase model, double scale, boolean renderDoubleFace, float rotationX, float rotationY, float rotationZ, Vec3 rotationCenter) {
		this.constructModel(model, scale, renderDoubleFace);
		this.rotate(1.0F, rotationX + 180.0F, rotationY, rotationZ, rotationCenter);
	}

	/**
	 * Transforms the given Vec3 with the given scale and ModelRenderer rotations.
	 * @param scaledPos			Scaled vertex position
	 * @param modelRenderer		Minecraft ModelRenderer
	 * @param rotationMatrix	Rotation matrix
	 * @param modelScale		Scale of the model (usually 0.065)
	 * @return
	 */
	private Vec3 transformPosition(Vec3 scaledPos, ModelRenderer modelRenderer, RotationMatrix rotationMatrix, double modelScale) {
		Vec3 scaledRotPos = new Vec3(modelRenderer.rotationPointX * modelScale, modelRenderer.rotationPointY * modelScale, modelRenderer.rotationPointZ * modelScale);

		//Dirty fix to prevent some bugs when rotation == 0
		if(modelRenderer.rotateAngleX == 0.0F && modelRenderer.rotateAngleY == 0.0F && modelRenderer.rotateAngleZ == 0.0F) {
			modelRenderer.rotateAngleX = 0.0001F;
			modelRenderer.rotateAngleY = 0.0001F;
			modelRenderer.rotateAngleZ = 0.0001F;
		}

		//Offset to rotation point
		scaledPos.x += scaledRotPos.x;
		scaledPos.y += scaledRotPos.y;
		scaledPos.z += scaledRotPos.z;

		//Apply own rotation and transformation
		this.applyRotation(modelRenderer, rotationMatrix);
		scaledPos = rotationMatrix.transformVec(scaledPos, scaledRotPos);

		return scaledPos;
	}

	/**
	 * Returns a corner of the box of the given ModelRenderer.
	 * @param xb				True for X + width
	 * @param yb				True for Y + height
	 * @param zb				True for Z + depth
	 * @param modelBox			Minecraft ModelBox
	 * @param modelRenderer		Minecraft ModelRenderer
	 * @param modelScale		Scale of the model (usually 0.065)
	 * @param rotationMatrix	Rotation matrix
	 * @return
	 */
	private Vec3 getBoxCorner(boolean xb, boolean yb, boolean zb, ModelBox modelBox, ModelRenderer modelRenderer, double modelScale, RotationMatrix rotationMatrix) {
		double posX = (!xb ? modelBox.posX1 : modelBox.posX2) + modelRenderer.offsetX;
		double posY = (!yb ? modelBox.posY1 : modelBox.posY2) + modelRenderer.offsetY;
		double posZ = (!zb ? modelBox.posZ1 : modelBox.posZ2) + modelRenderer.offsetZ;
		Vec3 scaledPos = new Vec3(posX * modelScale, posY * modelScale, posZ * modelScale);
		Vec3 scaledRotPos = new Vec3(modelRenderer.rotationPointX * modelScale, modelRenderer.rotationPointY * modelScale, modelRenderer.rotationPointZ * modelScale);

		//Dirty fix to prevent some bugs when rotation == 0
		if(modelRenderer.rotateAngleX == 0.0F && modelRenderer.rotateAngleY == 0.0F && modelRenderer.rotateAngleZ == 0.0F) {
			modelRenderer.rotateAngleX = 0.0000001F;
			modelRenderer.rotateAngleY = 0.0000001F;
			modelRenderer.rotateAngleZ = 0.0000001F;
		}

		//Offset to rotation point
		scaledPos.x += scaledRotPos.x;
		scaledPos.y += scaledRotPos.y;
		scaledPos.z += scaledRotPos.z;

		//Apply own rotation and transformation
		this.applyRotation(modelRenderer, rotationMatrix);
		scaledPos = rotationMatrix.transformVec(scaledPos, scaledRotPos);

		//Simulate parent rotation and transformation
		List<ModelRenderer> parents = this.parentMap.get(modelRenderer);
		if(parents != null && parents.size() > 0) {
			for(ModelRenderer parent : parents) {
				scaledPos = this.transformPosition(scaledPos, parent, ROTATION_MATRIX, modelScale);
			}
		}

		return scaledPos;
	}

	/**
	 * Reconstructs a list of vertices and UVs with the given data.
	 * @param modelBase				The model
	 * @param modelScale			Scale of the model (usually 0.065)
	 * @param renderDoubleFace		Set to true if the faces should be rendered in both directions
	 */
	private void constructModel(ModelBase modelBase, double modelScale, boolean renderDoubleFace) {
		//Model texture width/height
		double modelWidth = modelBase.textureWidth;
		double modelHeight = modelBase.textureHeight;

		//Create child map
		for(ModelRenderer modelRenderer : (List<ModelRenderer>) modelBase.boxList) {
			if(modelRenderer.childModels != null) {
				for(ModelRenderer childModelRenderer : (List<ModelRenderer>) modelRenderer.childModels) {
					this.childOfMap.put(childModelRenderer, modelRenderer);
				}
			}
		}

		//Create parent map
		for(ModelRenderer modelRenderer : (List<ModelRenderer>) modelBase.boxList) {
			this.parentMap.put(modelRenderer, this.getParentList(new ArrayList<ModelRenderer>(), modelRenderer));
		}

		//Iterate through ModelRenderers and boxes in the ModelBase
		for(ModelRenderer modelRenderer : (List<ModelRenderer>) modelBase.boxList) {
			for(ModelBox modelBox : (List<ModelBox>) modelRenderer.cubeList) {
				//ModelBox transformed vertices
				Vec3 o = this.getBoxCorner(false, false, false, modelBox, modelRenderer, modelScale, ROTATION_MATRIX);
				Vec3 ox = this.getBoxCorner(true, false, false, modelBox, modelRenderer, modelScale, ROTATION_MATRIX);
				Vec3 oy = this.getBoxCorner(false, true, false, modelBox, modelRenderer, modelScale, ROTATION_MATRIX);
				Vec3 oz = this.getBoxCorner(false, false, true, modelBox, modelRenderer, modelScale, ROTATION_MATRIX);
				Vec3 oxy = this.getBoxCorner(true, true, false, modelBox, modelRenderer, modelScale, ROTATION_MATRIX);
				Vec3 oyz = this.getBoxCorner(false, true, true, modelBox, modelRenderer, modelScale, ROTATION_MATRIX);
				Vec3 oxz = this.getBoxCorner(true, false, true, modelBox, modelRenderer, modelScale, ROTATION_MATRIX);
				Vec3 oxyz = this.getBoxCorner(true, true, true, modelBox, modelRenderer, modelScale, ROTATION_MATRIX);

				//ModelBox quad list
				if(f_mbQuadList == null) {
					f_mbQuadList = ReflectionHelper.findField(ModelBox.class, "quadList", "field_78254_i");
				}
				TexturedQuad[] mbQuadList = null;
				try {
					mbQuadList = (TexturedQuad[]) f_mbQuadList.get(modelBox);
				} catch(Exception ex) {
					ex.printStackTrace();
				}

				//ModelBox texture vertices
				PositionTextureVertex[] mbVertices1 = mbQuadList[0].vertexPositions;
				PositionTextureVertex[] mbVertices2 = mbQuadList[1].vertexPositions;
				PositionTextureVertex[] mbVertices3 = mbQuadList[2].vertexPositions;
				PositionTextureVertex[] mbVertices4 = mbQuadList[3].vertexPositions;
				PositionTextureVertex[] mbVertices5 = mbQuadList[4].vertexPositions;
				PositionTextureVertex[] mbVertices6 = mbQuadList[5].vertexPositions;

				ArrayList<Quad> quadList = new ArrayList<Quad>(12);

				//Face 1
				Quad face1 = new Quad(
						new Vec3(o, mbVertices5[0].texturePositionX, mbVertices5[0].texturePositionY, modelWidth, modelHeight),
						new Vec3(oy, mbVertices5[3].texturePositionX, mbVertices5[3].texturePositionY, modelWidth, modelHeight),
						new Vec3(oxy, mbVertices5[2].texturePositionX, mbVertices5[2].texturePositionY, modelWidth, modelHeight),
						new Vec3(ox, mbVertices5[1].texturePositionX, mbVertices5[1].texturePositionY, modelWidth, modelHeight));
				quadList.add(face1);
				if(renderDoubleFace) {
					Quad face1d = new Quad(
							new Vec3(o, mbVertices5[0].texturePositionX, mbVertices5[0].texturePositionY, modelWidth, modelHeight),
							new Vec3(ox, mbVertices5[1].texturePositionX, mbVertices5[1].texturePositionY, modelWidth, modelHeight),
							new Vec3(oxy, mbVertices5[2].texturePositionX, mbVertices5[2].texturePositionY, modelWidth, modelHeight),
							new Vec3(oy, mbVertices5[3].texturePositionX, mbVertices5[3].texturePositionY, modelWidth, modelHeight));
					quadList.add(face1d);
				}

				//Face 2
				Quad face2 = new Quad(
						new Vec3(oz, mbVertices6[1].texturePositionX, mbVertices6[1].texturePositionY, modelWidth, modelHeight),
						new Vec3(oxz, mbVertices6[0].texturePositionX, mbVertices6[0].texturePositionY, modelWidth, modelHeight),
						new Vec3(oxyz, mbVertices6[3].texturePositionX, mbVertices6[3].texturePositionY, modelWidth, modelHeight),
						new Vec3(oyz, mbVertices6[2].texturePositionX, mbVertices6[2].texturePositionY, modelWidth, modelHeight));
				quadList.add(face2);
				if(renderDoubleFace) {
					Quad face2d = new Quad(
							new Vec3(oz, mbVertices6[1].texturePositionX, mbVertices6[1].texturePositionY, modelWidth, modelHeight),
							new Vec3(oyz, mbVertices6[2].texturePositionX, mbVertices6[2].texturePositionY, modelWidth, modelHeight),
							new Vec3(oxyz, mbVertices6[3].texturePositionX, mbVertices6[3].texturePositionY, modelWidth, modelHeight),
							new Vec3(oxz, mbVertices6[0].texturePositionX, mbVertices6[0].texturePositionY, modelWidth, modelHeight));
					quadList.add(face2d);
				}

				//Face 3
				Quad face3 = new Quad(
						new Vec3(oy, mbVertices4[1].texturePositionX, mbVertices4[1].texturePositionY, modelWidth, modelHeight),
						new Vec3(oyz, mbVertices4[2].texturePositionX, mbVertices4[2].texturePositionY, modelWidth, modelHeight),
						new Vec3(oxyz, mbVertices4[3].texturePositionX, mbVertices4[3].texturePositionY, modelWidth, modelHeight),
						new Vec3(oxy, mbVertices4[0].texturePositionX, mbVertices4[0].texturePositionY, modelWidth, modelHeight));
				quadList.add(face3);
				if(renderDoubleFace) {
					Quad face3d = new Quad(
							new Vec3(oy, mbVertices4[1].texturePositionX, mbVertices4[1].texturePositionY, modelWidth, modelHeight),
							new Vec3(oxy, mbVertices4[0].texturePositionX, mbVertices4[0].texturePositionY, modelWidth, modelHeight),
							new Vec3(oxyz, mbVertices4[3].texturePositionX, mbVertices4[3].texturePositionY, modelWidth, modelHeight),
							new Vec3(oyz, mbVertices4[2].texturePositionX, mbVertices4[2].texturePositionY, modelWidth, modelHeight));
					quadList.add(face3d);
				}

				//Face 4
				Quad face4 = new Quad(
						new Vec3(o, mbVertices3[2].texturePositionX, mbVertices3[2].texturePositionY, modelWidth, modelHeight),
						new Vec3(ox, mbVertices3[3].texturePositionX, mbVertices3[3].texturePositionY, modelWidth, modelHeight),
						new Vec3(oxz, mbVertices3[0].texturePositionX, mbVertices3[0].texturePositionY, modelWidth, modelHeight),
						new Vec3(oz, mbVertices3[1].texturePositionX, mbVertices3[1].texturePositionY, modelWidth, modelHeight));
				quadList.add(face4);
				if(renderDoubleFace) {
					Quad face4d = new Quad(
							new Vec3(o, mbVertices3[2].texturePositionX, mbVertices3[2].texturePositionY, modelWidth, modelHeight),
							new Vec3(oz, mbVertices3[1].texturePositionX, mbVertices3[1].texturePositionY, modelWidth, modelHeight),
							new Vec3(oxz, mbVertices3[0].texturePositionX, mbVertices3[0].texturePositionY, modelWidth, modelHeight),
							new Vec3(ox, mbVertices3[3].texturePositionX, mbVertices3[3].texturePositionY, modelWidth, modelHeight));
					quadList.add(face4d);
				}

				//Face 5
				Quad face5 = new Quad(
						new Vec3(ox, mbVertices1[1].texturePositionX, mbVertices1[1].texturePositionY, modelWidth, modelHeight),
						new Vec3(oxy, mbVertices1[2].texturePositionX, mbVertices1[2].texturePositionY, modelWidth, modelHeight),
						new Vec3(oxyz, mbVertices1[3].texturePositionX, mbVertices1[3].texturePositionY, modelWidth, modelHeight),
						new Vec3(oxz, mbVertices1[0].texturePositionX, mbVertices1[0].texturePositionY, modelWidth, modelHeight));
				quadList.add(face5);
				if(renderDoubleFace) {
					Quad face5d = new Quad(
							new Vec3(ox, mbVertices1[1].texturePositionX, mbVertices1[1].texturePositionY, modelWidth, modelHeight),
							new Vec3(oxz, mbVertices1[0].texturePositionX, mbVertices1[0].texturePositionY, modelWidth, modelHeight),
							new Vec3(oxyz, mbVertices1[3].texturePositionX, mbVertices1[3].texturePositionY, modelWidth, modelHeight),
							new Vec3(oxy, mbVertices1[2].texturePositionX, mbVertices1[2].texturePositionY, modelWidth, modelHeight));
					quadList.add(face5d);
				}

				//Face 6
				Quad face6 = new Quad(
						new Vec3(o, mbVertices2[0].texturePositionX, mbVertices2[0].texturePositionY, modelWidth, modelHeight),
						new Vec3(oz, mbVertices2[1].texturePositionX, mbVertices2[1].texturePositionY, modelWidth, modelHeight),
						new Vec3(oyz, mbVertices2[2].texturePositionX, mbVertices2[2].texturePositionY, modelWidth, modelHeight),
						new Vec3(oy, mbVertices2[3].texturePositionX, mbVertices2[3].texturePositionY, modelWidth, modelHeight));
				quadList.add(face6);
				if(renderDoubleFace) {
					Quad face6d = new Quad(
							new Vec3(o, mbVertices2[0].texturePositionX, mbVertices2[0].texturePositionY, modelWidth, modelHeight),
							new Vec3(oy, mbVertices2[3].texturePositionX, mbVertices2[3].texturePositionY, modelWidth, modelHeight),
							new Vec3(oyz, mbVertices2[2].texturePositionX, mbVertices2[2].texturePositionY, modelWidth, modelHeight),
							new Vec3(oz, mbVertices2[1].texturePositionX, mbVertices2[1].texturePositionY, modelWidth, modelHeight));
					quadList.add(face6d);
				}

				Quad[] quads = quadList.toArray(new Quad[0]);
				this.addBox(new Box(quads, modelRenderer, modelBox));
			}
		}

		//Find the child models of each ModelRenderer
		Map<ModelRenderer, List<ModelRenderer>> childModelMap = new HashMap<ModelRenderer, List<ModelRenderer>>();
		for(Box box : this.modelBoxList) {
			ModelRenderer mr = box.modelRenderer;
			List<ModelRenderer> childList = childModelMap.get(mr);
			if(childList == null) {
				childList = new ArrayList<ModelRenderer>();
				childModelMap.put(mr, childList);
			}
			List<ModelRenderer> childModels = this.getChildModels(new ArrayList<ModelRenderer>(), mr);
			for(ModelRenderer childModel : childModels) {
				if(!childList.contains(childModel)) {
					childList.add(childModel);
				}
			}
		}

		//Add child boxes to each box
		for(Box box : this.modelBoxList) {
			List<ModelRenderer> childModels = childModelMap.get(box.modelRenderer);
			for(ModelRenderer childModel : childModels) {
				List<Box> childBoxes = this.modelRendererBoxMap.get(childModel);
				for(Box childBox : childBoxes) {
					if(!box.childBoxes.contains(childBox)) {
						box.childBoxes.add(childBox);
					}
				}
			}
		}
	}

	/**
	 * Returns a list of all child and sub-child models of the given ModelRenderer.
	 * @param list					List to be populated
	 * @param modelRenderer			Minecraft ModelRenderer
	 * @return
	 */
	private List<ModelRenderer> getChildModels(List<ModelRenderer> list, ModelRenderer modelRenderer) {
		if(modelRenderer.childModels != null && modelRenderer.childModels.size() > 0) {
			for(ModelRenderer childModel : (List<ModelRenderer>) modelRenderer.childModels) {
				list.add(childModel);
				if(!list.contains(childModel)) {
					for(ModelRenderer mr : this.getChildModels(list, childModel)) {
						if(!list.contains(mr)) {
							list.add(mr);
						}
					}
				}
			}
		}
		return list;
	}

	/**
	 * Rotates the model vertices.
	 * @param rotation		Rotation (degrees)
	 * @param x				X axis (pitch)
	 * @param y				Y axis (yaw)
	 * @param z				Z axis (roll)
	 * @param center		Rotation center
	 * @return
	 */
	public ModelConverter rotate(float rotation, float x, float y, float z, Vec3 center) {
		ROTATION_MATRIX.setRotations((float)Math.toRadians(x * rotation), (float)Math.toRadians(y * rotation), (float)Math.toRadians(z * rotation));
		for(Box box : this.modelBoxList) {
			for(Quad quad : box.quads) {
				for(int i = 0; i < 4; i++) {
					Vec3 vec = quad.vertices[i];
					Vec3 rotatedPoint = null;
					rotatedPoint = ROTATION_MATRIX.transformVec(vec, center);
					vec.x = rotatedPoint.x;
					vec.y = rotatedPoint.y;
					vec.z = rotatedPoint.z;
				}
			}
		}
		Vec3 rotatedFwdVec = ROTATION_MATRIX.transformVec(this.fwdVec, center);
		this.fwdVec.x = rotatedFwdVec.x;
		this.fwdVec.y = rotatedFwdVec.y;
		this.fwdVec.z = rotatedFwdVec.z;
		Vec3 rotatedUpVec = ROTATION_MATRIX.transformVec(this.upVec, center);
		this.upVec.x = rotatedUpVec.x;
		this.upVec.y = rotatedUpVec.y;
		this.upVec.z = rotatedUpVec.z;
		return this;
	}

	/**
	 * Offsets the model vertices in world space.
	 * @param offset	Offset
	 * @return
	 */
	public ModelConverter offsetWS(Vec3 offset) {
		for(Box box : this.modelBoxList) {
			for(Quad quad : box.quads) {
				for(int i = 0; i < 4; i++) {
					Vec3 vec = quad.vertices[i];
					vec.x += offset.x;
					vec.y += offset.y;
					vec.z += offset.z;
				}
			}
		}
		return this;
	}

	/**
	 * Offsets the model vertices in model space.
	 * @param offset
	 * @return
	 */
	public ModelConverter offsetMS(Vec3 offset) {
		Vec3 leftVec = this.fwdVec.cross(this.upVec);
		for(Box box : this.modelBoxList) {
			for(Quad quad : box.quads) {
				for(int i = 0; i < 4; i++) {
					Vec3 vec = quad.vertices[i];
					vec.x += this.upVec.x * offset.y + this.fwdVec.x * offset.z + leftVec.x * offset.x;
					vec.y += this.upVec.y * offset.y + this.fwdVec.y * offset.z + leftVec.y * offset.x;
					vec.z += this.upVec.z * offset.y + this.fwdVec.z * offset.z + leftVec.z * offset.x;
				}
			}
		}
		return this;
	}

	/**
	 * Scales the model vertices.
	 * @param x       X Scale
	 * @param y       Y Scale
	 * @param z       Z Scale
	 * @return
	 */
	public ModelConverter scale(double x, double y, double z) {
		for(Box box : this.modelBoxList) {
			for(Quad quad : box.quads) {
				for(int i = 0; i < 4; i++) {
					Vec3 vec = quad.vertices[i];
					vec.x *= x;
					vec.y *= y;
					vec.z *= z;
				}
			}
		}
		return this;
	}

	/**
	 * Recursively returns a list of all parents and sub-parents of the given ModelRenderer.
	 * Used to simulate previous rotations and transformations.
	 * @param parentList		List to be populated with parents
	 * @param modelRenderer		Minecraft ModelRenderer
	 * @return
	 */
	private List<ModelRenderer> getParentList(List<ModelRenderer> parentList, ModelRenderer modelRenderer) {
		if(this.childOfMap.containsKey(modelRenderer)) {
			ModelRenderer parent = this.childOfMap.get(modelRenderer);
			parentList.add(parent);
			this.getParentList(parentList, parent);
		}
		return parentList;
	}

	/**
	 * Adds a model box to the model box list.
	 * @param box	Box
	 */
	private void addBox(Box box) {
		this.modelBoxList.add(box);
		List<Box> boxList = this.modelRendererBoxMap.get(box.modelRenderer);
		if(boxList == null) {
			boxList = new ArrayList<Box>();
			this.modelRendererBoxMap.put(box.modelRenderer, boxList);
		}
		boxList.add(box);
	}

	/**
	 * Returns the list of the reconstructed boxes of this model.
	 * @return
	 */
	public List<Box> getBoxes() {
		return this.modelBoxList;
	}

	/**
	 * Renders the model with the given tessellator. 
	 * The tessellator must already be drawing.
	 * @param tessellator
	 */
	/*public void renderWithTessellator(Tessellator tessellator) {
		for(Box box : this.modelBoxList) {
			for(Quad quad : box.quads) {
				for(int i = 0; i < 4; i++) {
					Vec3 vec = quad.vertices[i];
					tessellator.addVertexWithUV(vec.x, vec.y, vec.z, vec.u, vec.v);
				}
			}
		}
	}*/

	/**
	 * Renders the model with the given tessellator. 
	 * The tessellator must already be drawing.
	 * @param tessellator
	 */
	public void renderWithTessellator(Tessellator tessellator, int width, int height, IIcon icon) {
		for(Box box : this.modelBoxList) {
			for(Quad quad : box.quads) {
				for(int i = 0; i < 4; i++) {
					Vec3 vec = quad.vertices[i];
					tessellator.addVertexWithUV(vec.x, vec.y, vec.z, vec.getU(icon, width), vec.getV(icon, height));
				}
			}
		}
	}

	/**
	 * Returns a copy of this model.
	 * @return
	 */
	public Model getModel() {
		return new Model(this.modelBoxList, this.fwdVec, this.upVec);
	}

	/**
	 * Applies the rotation of the ModelRenderer to the rotation matrix.
	 * @param modelRenderer			Minecraft ModelRenderer
	 * @param rotationMatrix		Rotation matrix
	 */
	protected void applyRotation(ModelRenderer modelRenderer, RotationMatrix rotationMatrix) {
		rotationMatrix.setRotations(modelRenderer.rotateAngleX, modelRenderer.rotateAngleY, modelRenderer.rotateAngleZ);
	}
}
