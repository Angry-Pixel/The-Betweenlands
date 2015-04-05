package thebetweenlands.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class ModelConverter {
	//Holds the rotation matrix
	private static final RotationMatrix ROTATION_MATRIX = new RotationMatrix();

	//The field of ModelBox#quadList
	private static Field f_mbQuadList = null;

	public static class Vec3 {
		public double x, y, z, u, v;
		public Vec3(double x, double y, double z) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.u = 0.0D;
			this.v = 0.0D;
		}
		public Vec3(double x, double y, double z, double u, double v) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.u = u;
			this.v = v;
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

	public static class TextureMap {
		private final double umin, vmin, umax, vmax;
		private final int width, height;

		/**
		 * Creates a new TextureMap. Uses the default UVs of (0.0|0.0) and (1.0|1.0)
		 * @param width			Width of the texture (pixels)
		 * @param height		Height of the texture (pixels)
		 */
		public TextureMap(int width, int height) {
			this.width = width;
			this.height = height;
			this.umin = 0.0D;
			this.vmin = 0.0D;
			this.umax = 1.0D;
			this.vmax = 1.0D;
		}

		/**
		 * Creates a new TextureMap. Uses the UVs of the given minecraft IIcon
		 * @param width			Width of the texture (pixels)
		 * @param height		Height of the texture (pixels)
		 * @param icon			Minecraft IIcon
		 */
		public TextureMap(int width, int height, IIcon icon) {
			this.width = width;
			this.height = height;
			this.umin = icon.getMinU();
			this.vmin = icon.getMinV();
			this.umax = icon.getMaxU();
			this.vmax = icon.getMaxV();
		}

		/**
		 * Creates a new TextureMap.
		 * @param width			Width of the texture (pixels)
		 * @param height		Height of the texture (pixels)
		 * @param umin			Min. U
		 * @param vmin			Min. V
		 * @param umax			Max. U
		 * @param vmax			Max. V
		 */
		public TextureMap(int width, int height, double umin, double vmin, double umax, double vmax) {
			this.width = width;
			this.height = height;
			this.umin = umin;
			this.vmin = vmin;
			this.umax = umax;
			this.vmax = vmax;
		}
	}

	//Holds a list of vertices and UVs of this model
	private final ArrayList<Vec3> modelVertexList = new ArrayList<Vec3>();

	//Holds the parent component of each ModelRenderer of this model
	private final Map<ModelRenderer, ModelRenderer> childOfMap = new HashMap<ModelRenderer, ModelRenderer>();

	//Holds a list of all parent components and sub-parent components of each ModelRenderer of this model
	private final Map<ModelRenderer, List<ModelRenderer>> parentMap = new HashMap<ModelRenderer, List<ModelRenderer>>();

	private final Vec3 fwdVec = new Vec3(0, 0, 1);
	private final Vec3 upVec = new Vec3(0, -1, 0);

	/**
	 * Creates a new ModelConverter that converts a minecraft model to a list of vertices and UVs.
	 * @param model					The model
	 * @param scale					Scale of the model (usually 0.065)
	 * @param textureMap			TextureMap that holds the UVs and texture width/height
	 * @param renderDoubleFace		Set to true if the faces should be rendered in both directions
	 */
	public ModelConverter(ModelBase model, double scale, TextureMap textureMap, boolean renderDoubleFace) {
		this.constructModel(model, scale, textureMap, renderDoubleFace);
		this.rotate(1.0F, 180.0F, 0.0F, 0.0F, new Vec3(0, 0, 0));
	}

	/**
	 * Creates a new ModelConverter that converts a minecraft model to a list of vertices and UVs.
	 * @param model					The model
	 * @param scale					Scale of the model (usually 0.065)
	 * @param textureMap			TextureMap that holds the UVs and texture width/height
	 * @param renderDoubleFace		Set to true if the faces should be rendered in both directions
	 * @param rotationX				Rotation around X axis (degrees)
	 * @param rotationY				Rotation around Y axis (degrees)
	 * @param rotationZ				Rotation around Z axis (degrees)
	 */
	public ModelConverter(ModelBase model, double scale, TextureMap textureMap, boolean renderDoubleFace, float rotationX, float rotationY, float rotationZ) {
		this.constructModel(model, scale, textureMap, renderDoubleFace);
		this.rotate(1.0F, rotationX + 180.0F, rotationY, rotationZ, new Vec3(0, 0, 0));
	}

	/**
	 * Creates a new ModelConverter that converts a minecraft model to a list of vertices and UVs.
	 * @param model					The model
	 * @param scale					Scale of the model (usually 0.065)
	 * @param textureMap			TextureMap that holds the UVs and texture width/height
	 * @param renderDoubleFace		Set to true if the faces should be rendered in both directions
	 * @param rotationX				Rotation around X axis (degrees)
	 * @param rotationY				Rotation around Y axis (degrees)
	 * @param rotationZ				Rotation around Z axis (degrees)
	 * @param rotaitonCenter		Center of the rotation
	 */
	public ModelConverter(ModelBase model, double scale, TextureMap textureMap, boolean renderDoubleFace, float rotationX, float rotationY, float rotationZ, Vec3 rotationCenter) {
		this.constructModel(model, scale, textureMap, renderDoubleFace);
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
		rotationMatrix.setRotations(modelRenderer.rotateAngleX, modelRenderer.rotateAngleY, modelRenderer.rotateAngleZ);
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
			modelRenderer.rotateAngleX = 0.0001F;
			modelRenderer.rotateAngleY = 0.0001F;
			modelRenderer.rotateAngleZ = 0.0001F;
		}

		//Offset to rotation point
		scaledPos.x += scaledRotPos.x;
		scaledPos.y += scaledRotPos.y;
		scaledPos.z += scaledRotPos.z;

		//Apply own rotation and transformation
		rotationMatrix.setRotations(modelRenderer.rotateAngleX, modelRenderer.rotateAngleY, modelRenderer.rotateAngleZ);
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
	 * @param textureMap			TextureMap that holds the UVs and texture width/height
	 * @param renderDoubleFace		Set to true if the faces should be rendered in both directions
	 */
	private void constructModel(ModelBase modelBase, double modelScale, TextureMap textureMap, boolean renderDoubleFace) {
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

				//IIcon UVs
				double umin = textureMap.umin;
				double vmin = textureMap.vmin;
				double umax = textureMap.umax;
				double vmax = textureMap.vmax;
				double uvWidth = (umax - umin) * modelWidth / textureMap.width;
				double uvHeight = (vmax - vmin) * modelHeight / textureMap.height;

				//Face 1
				this.addVertexWithUV(o, umin + mbVertices5[0].texturePositionX * uvWidth, vmin + mbVertices5[0].texturePositionY * uvHeight);
				this.addVertexWithUV(oy, umin + mbVertices5[3].texturePositionX * uvWidth, vmin + mbVertices5[3].texturePositionY * uvHeight);
				this.addVertexWithUV(oxy, umin + mbVertices5[2].texturePositionX * uvWidth, vmin + mbVertices5[2].texturePositionY * uvHeight);
				this.addVertexWithUV(ox, umin + mbVertices5[1].texturePositionX * uvWidth, vmin + mbVertices5[1].texturePositionY * uvHeight);
				if(renderDoubleFace) {
					this.addVertexWithUV(o, umin + mbVertices5[0].texturePositionX * uvWidth, vmin + mbVertices5[0].texturePositionY * uvHeight);
					this.addVertexWithUV(ox, umin + mbVertices5[1].texturePositionX * uvWidth, vmin + mbVertices5[1].texturePositionY * uvHeight);
					this.addVertexWithUV(oxy, umin + mbVertices5[2].texturePositionX * uvWidth, vmin + mbVertices5[2].texturePositionY * uvHeight);
					this.addVertexWithUV(oy, umin + mbVertices5[3].texturePositionX * uvWidth, vmin + mbVertices5[3].texturePositionY * uvHeight);
				}

				//Face 2
				this.addVertexWithUV(oz, umin + mbVertices6[1].texturePositionX * uvWidth, vmin + mbVertices6[1].texturePositionY * uvHeight);
				this.addVertexWithUV(oxz, umin + mbVertices6[0].texturePositionX * uvWidth, vmin + mbVertices6[0].texturePositionY * uvHeight);
				this.addVertexWithUV(oxyz, umin + mbVertices6[3].texturePositionX * uvWidth, vmin + mbVertices6[3].texturePositionY * uvHeight);
				this.addVertexWithUV(oyz, umin + mbVertices6[2].texturePositionX * uvWidth, vmin + mbVertices6[2].texturePositionY * uvHeight);
				if(renderDoubleFace) {
					this.addVertexWithUV(oz, umin + mbVertices6[1].texturePositionX * uvWidth, vmin + mbVertices6[1].texturePositionY * uvHeight);
					this.addVertexWithUV(oyz, umin + mbVertices6[2].texturePositionX * uvWidth, vmin + mbVertices6[2].texturePositionY * uvHeight);
					this.addVertexWithUV(oxyz, umin + mbVertices6[3].texturePositionX * uvWidth, vmin + mbVertices6[3].texturePositionY * uvHeight);
					this.addVertexWithUV(oxz, umin + mbVertices6[0].texturePositionX * uvWidth, vmin + mbVertices6[0].texturePositionY * uvHeight);
				}

				//Face 3
				this.addVertexWithUV(oy, umin + mbVertices4[0].texturePositionX * uvWidth, vmin + mbVertices4[0].texturePositionY * uvHeight);
				this.addVertexWithUV(oyz, umin + mbVertices4[3].texturePositionX * uvWidth, vmin + mbVertices4[3].texturePositionY * uvHeight);
				this.addVertexWithUV(oxyz, umin + mbVertices4[2].texturePositionX * uvWidth, vmin + mbVertices4[2].texturePositionY * uvHeight);
				this.addVertexWithUV(oxy, umin + mbVertices4[1].texturePositionX * uvWidth, vmin + mbVertices4[1].texturePositionY * uvHeight);
				if(renderDoubleFace) {
					this.addVertexWithUV(oy, umin + mbVertices4[0].texturePositionX * uvWidth, vmin + mbVertices4[0].texturePositionY * uvHeight);
					this.addVertexWithUV(oxy, umin + mbVertices4[1].texturePositionX * uvWidth, vmin + mbVertices4[1].texturePositionY * uvHeight);
					this.addVertexWithUV(oxyz, umin + mbVertices4[2].texturePositionX * uvWidth, vmin + mbVertices4[2].texturePositionY * uvHeight);
					this.addVertexWithUV(oyz, umin + mbVertices4[3].texturePositionX * uvWidth, vmin + mbVertices4[3].texturePositionY * uvHeight);
				}

				//Face 4
				this.addVertexWithUV(o, umin + mbVertices3[2].texturePositionX * uvWidth, vmin + mbVertices3[2].texturePositionY * uvHeight);
				this.addVertexWithUV(ox, umin + mbVertices3[3].texturePositionX * uvWidth, vmin + mbVertices3[3].texturePositionY * uvHeight);
				this.addVertexWithUV(oxz, umin + mbVertices3[0].texturePositionX * uvWidth, vmin + mbVertices3[0].texturePositionY * uvHeight);
				this.addVertexWithUV(oz, umin + mbVertices3[1].texturePositionX * uvWidth, vmin + mbVertices3[1].texturePositionY * uvHeight);
				if(renderDoubleFace) {
					this.addVertexWithUV(o, umin + mbVertices3[2].texturePositionX * uvWidth, vmin + mbVertices3[2].texturePositionY * uvHeight);
					this.addVertexWithUV(oz, umin + mbVertices3[1].texturePositionX * uvWidth, vmin + mbVertices3[1].texturePositionY * uvHeight);
					this.addVertexWithUV(oxz, umin + mbVertices3[0].texturePositionX * uvWidth, vmin + mbVertices3[0].texturePositionY * uvHeight);
					this.addVertexWithUV(ox, umin + mbVertices3[3].texturePositionX * uvWidth, vmin + mbVertices3[3].texturePositionY * uvHeight);
				}

				//Face 5
				this.addVertexWithUV(ox, umin + mbVertices1[1].texturePositionX * uvWidth, vmin + mbVertices1[1].texturePositionY * uvHeight);
				this.addVertexWithUV(oxy, umin + mbVertices1[2].texturePositionX * uvWidth, vmin + mbVertices1[2].texturePositionY * uvHeight);
				this.addVertexWithUV(oxyz, umin + mbVertices1[3].texturePositionX * uvWidth, vmin + mbVertices1[3].texturePositionY * uvHeight);
				this.addVertexWithUV(oxz, umin + mbVertices1[0].texturePositionX * uvWidth, vmin + mbVertices1[0].texturePositionY * uvHeight);
				if(renderDoubleFace) {
					this.addVertexWithUV(ox, umin + mbVertices1[1].texturePositionX * uvWidth, vmin + mbVertices1[1].texturePositionY * uvHeight);
					this.addVertexWithUV(oxz, umin + mbVertices1[0].texturePositionX * uvWidth, vmin + mbVertices1[0].texturePositionY * uvHeight);
					this.addVertexWithUV(oxyz, umin + mbVertices1[3].texturePositionX * uvWidth, vmin + mbVertices1[3].texturePositionY * uvHeight);
					this.addVertexWithUV(oxy, umin + mbVertices1[2].texturePositionX * uvWidth, vmin + mbVertices1[2].texturePositionY * uvHeight);
				}

				//Face 6
				this.addVertexWithUV(o, umin + mbVertices2[0].texturePositionX * uvWidth, vmin + mbVertices2[0].texturePositionY * uvHeight);
				this.addVertexWithUV(oz, umin + mbVertices2[1].texturePositionX * uvWidth, vmin + mbVertices2[1].texturePositionY * uvHeight);
				this.addVertexWithUV(oyz, umin + mbVertices2[2].texturePositionX * uvWidth, vmin + mbVertices2[2].texturePositionY * uvHeight);
				this.addVertexWithUV(oy, umin + mbVertices2[3].texturePositionX * uvWidth, vmin + mbVertices2[3].texturePositionY * uvHeight);
				if(renderDoubleFace) {
					this.addVertexWithUV(o, umin + mbVertices2[0].texturePositionX * uvWidth, vmin + mbVertices2[0].texturePositionY * uvHeight);
					this.addVertexWithUV(oy, umin + mbVertices2[3].texturePositionX * uvWidth, vmin + mbVertices2[3].texturePositionY * uvHeight);
					this.addVertexWithUV(oyz, umin + mbVertices2[2].texturePositionX * uvWidth, vmin + mbVertices2[2].texturePositionY * uvHeight);
					this.addVertexWithUV(oz, umin + mbVertices2[1].texturePositionX * uvWidth, vmin + mbVertices2[1].texturePositionY * uvHeight);
				}
			}
		}
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
		for(Vec3 vec : this.modelVertexList) {
			Vec3 rotatedPoint = null;
			rotatedPoint = ROTATION_MATRIX.transformVec(vec, center);
			vec.x = rotatedPoint.x;
			vec.y = rotatedPoint.y;
			vec.z = rotatedPoint.z;
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
		for(Vec3 vec : this.modelVertexList) {
			vec.x += offset.x;
			vec.y += offset.y;
			vec.z += offset.z;
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
		for(Vec3 vec : this.modelVertexList) {
			Vec3 offsetVec = new Vec3(0, 0, 0);
			offsetVec.x += this.upVec.x * offset.y;
			offsetVec.y += this.upVec.y * offset.y;
			offsetVec.z += this.upVec.z * offset.y;
			offsetVec.x += this.fwdVec.x * offset.z;
			offsetVec.y += this.fwdVec.y * offset.z;
			offsetVec.z += this.fwdVec.z * offset.z;
			offsetVec.x += leftVec.x * offset.x;
			offsetVec.y += leftVec.y * offset.x;
			offsetVec.z += leftVec.z * offset.x;
			vec.x += offsetVec.x;
			vec.y += offsetVec.y;
			vec.z += offsetVec.z;
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
	 * Adds a vertex + UV to the model vertex list.
	 * @param vert		The vertex position
	 * @param u			Texture X coordinate
	 * @param v			Texture Y coordinate
	 */
	private void addVertexWithUV(Vec3 vert, double u, double v) {
		this.modelVertexList.add(new Vec3(vert.x, vert.y, vert.z, u, v));
	}

	/**
	 * Returns the list of the reconstructed vertices + UVs of this model.
	 * @return
	 */
	public List<Vec3> getVertices() {
		return this.modelVertexList;
	}

	/**
	 * Renders the model with the given tessellator. 
	 * The tessellator must already be drawing.
	 * @param tessellator
	 */
	public void renderWithTessellator(Tessellator tessellator) {
		for(Vec3 vert : this.modelVertexList) {
			tessellator.addVertexWithUV(vert.x, vert.y, vert.z, vert.u, vert.v);
		}
	}
}
