package thebetweenlands.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.util.TexturePacker.ITexturePackable;
import thebetweenlands.util.TexturePacker.TextureQuad;
import thebetweenlands.util.TexturePacker.TextureQuadMap;

public class ModelConverter {
	//Holds the rotation matrix
	private static final RotationMatrix ROTATION_MATRIX = new RotationMatrix();

	public static final class Quad {
		private Vec3UV[] vertices = new Vec3UV[4];
		private Quad(Vec3UV v1, Vec3UV v2, Vec3UV v3, Vec3UV v4) {
			this.vertices[0] = v1;
			this.vertices[1] = v2;
			this.vertices[2] = v3;
			this.vertices[3] = v4;
		}

		/**
		 * Returns the vertices of this quad.
		 * @return
		 */
		public Vec3UV[] getVertices() {
			return this.vertices;
		}
	}

	public static final class AlignedQuad {
		public final Quad originalQuad;
		public final double x, y, z, width, height, rx, ry, rz;
		private AlignedQuad(Quad originalQuad) {
			this.originalQuad = originalQuad;
			Vec3UV vert1 = this.originalQuad.vertices[0];
			this.x = vert1.x;
			this.y = vert1.y;
			this.z = vert1.z;
			Vec3UV dirW = this.originalQuad.vertices[1].sub(this.originalQuad.vertices[0]);
			Vec3UV dirH = this.originalQuad.vertices[3].sub(this.originalQuad.vertices[0]);
			this.width = dirW.len();
			this.height = dirH.len();
			dirW = dirW.normalized();
			dirH = dirH.normalized();
			Vec3UV up = new Vec3UV(0, 1, 0);
			this.rx = Math.toDegrees(Math.acos(up.dot(new Vec3UV(0, dirH.y, dirH.z))));
			Vec3UV fwd = new Vec3UV(0, 0, 1);
			this.rz = Math.toDegrees(Math.acos(up.dot(new Vec3UV(dirH.x, dirH.y, 0))));
			this.ry = Math.toDegrees(Math.acos(fwd.dot(new Vec3UV(dirW.x, 0, dirW.z))));
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
		public Box rotate(float rotation, float x, float y, float z, Vec3UV center) {
			ROTATION_MATRIX.setRotations((float)Math.toRadians(x * rotation), (float)Math.toRadians(y * rotation), (float)Math.toRadians(z * rotation));
			for(Box box : this.childBoxes) {
				box.rotate(rotation, x, y, z, center);
			}
			for(Quad quad : this.quads) {
				for(int i = 0; i < 4; i++) {
					Vec3UV vec = quad.vertices[i];
					Vec3UV rotatedPoint = null;
					rotatedPoint = ROTATION_MATRIX.transformVec(vec, center);
					vec.x = rotatedPoint.x;
					vec.y = rotatedPoint.y;
					vec.z = rotatedPoint.z;
				}
			}
			return this;
		}

		/**
		 * Returns an array of axis aligned quads that contain the required rotation
		 * @return
		 */
		public AlignedQuad[] getAlignedQuads() {
			AlignedQuad[] alignedQuads = new AlignedQuad[4];
			for(int i = 0; i < 4; i++) {
				Quad quad = this.quads[i];
				alignedQuads[i] = new AlignedQuad(quad);
			}
			return alignedQuads;
		}
	}

	public static class Model {
		private final Vec3UV fwdVec;
		private final Vec3UV upVec;
		private List<Box> modelBoxes = new ArrayList<Box>();

		private Model(List<Box> modelBoxList, Vec3UV fwdVec, Vec3UV upVec) {
			//Old, New
			Map<Box, Box> copyReference = new HashMap<Box, Box>();
			this.fwdVec = new Vec3UV(fwdVec);
			this.upVec = new Vec3UV(upVec);
			//Copy boxes
			for(Box box : modelBoxList) {
				Quad[] quads = new Quad[box.quads.length];
				for(int i = 0; i < box.quads.length; i++) {
					Quad quad = box.quads[i];
					quads[i] = new Quad(
							new Vec3UV(quad.vertices[0]),
							new Vec3UV(quad.vertices[1]),
							new Vec3UV(quad.vertices[2]),
							new Vec3UV(quad.vertices[3]));
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
		public Model offsetWS(Vec3UV offset) {
			for(Box box : this.modelBoxes) {
				for(Quad quad : box.quads) {
					for(int i = 0; i < 4; i++) {
						Vec3UV vec = quad.vertices[i];
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
		public Model offsetMS(Vec3UV offset) {
			Vec3UV leftVec = this.fwdVec.cross(this.upVec);
			for(Box box : this.modelBoxes) {
				for(Quad quad : box.quads) {
					for(int i = 0; i < 4; i++) {
						Vec3UV vec = quad.vertices[i];
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
		public Model rotate(float rotation, float x, float y, float z, Vec3UV center) {
			ROTATION_MATRIX.setRotations((float)Math.toRadians(x * rotation), (float)Math.toRadians(y * rotation), (float)Math.toRadians(z * rotation));
			for(Box box : this.modelBoxes) {
				for(Quad quad : box.quads) {
					for(int i = 0; i < 4; i++) {
						Vec3UV vec = quad.vertices[i];
						Vec3UV rotatedPoint = null;
						rotatedPoint = ROTATION_MATRIX.transformVec(vec, center);
						vec.x = rotatedPoint.x;
						vec.y = rotatedPoint.y;
						vec.z = rotatedPoint.z;
					}
				}
			}
			Vec3UV rotatedFwdVec = ROTATION_MATRIX.transformVec(this.fwdVec, center);
			this.fwdVec.x = rotatedFwdVec.x;
			this.fwdVec.y = rotatedFwdVec.y;
			this.fwdVec.z = rotatedFwdVec.z;
			Vec3UV rotatedUpVec = ROTATION_MATRIX.transformVec(this.upVec, center);
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
						Vec3UV vec = quad.vertices[i];
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
		 * Returns a copy of this model
		 * @return
		 */
		public Model copy() {
			return new Model(this.modelBoxes, this.fwdVec, this.upVec);
		}
	}

	//Holds a list of vertices and UVs of this model
	private final List<Box> modelBoxList = new ArrayList<Box>();

	//Holds the parent component of each ModelRenderer of this model
	private final Map<ModelRenderer, ModelRenderer> childOfMap = new HashMap<ModelRenderer, ModelRenderer>();

	//Holds a list of all parent components and sub-parent components of each ModelRenderer of this model
	private final Map<ModelRenderer, List<ModelRenderer>> parentMap = new HashMap<ModelRenderer, List<ModelRenderer>>();

	private final Map<ModelRenderer, List<Box>> modelRendererBoxMap = new HashMap<ModelRenderer, List<Box>>();

	private final Vec3UV fwdVec = new Vec3UV(0, 0, 1);
	private final Vec3UV upVec = new Vec3UV(0, -1, 0);

	//The constructed model
	private Model model;

	private final Packing packing;

	public static class Packing {
		public final ResourceLocation texture;
		public final TexturePacker packer;
		public final ITexturePackable owner;
		public final int width, height;

		public Packing(ResourceLocation texture, int width, int height, TexturePacker packer, ITexturePackable owner) {
			this.texture = texture;
			this.width = width;
			this.height = height;
			this.packer = packer;
			this.owner = owner;
		}
	}

	/**
	 * Creates a new ModelConverter that converts a minecraft model to a list of vertices and UVs.
	 * @param packing				The texture packing
	 * @param model					The model
	 * @param scale					Scale of the model (usually 0.065)
	 * @param renderDoubleFace		Set to true if the faces should be rendered in both directions
	 */
	public ModelConverter(@Nullable Packing packing, ModelBase model, double scale, boolean renderDoubleFace) {
		this(packing, model, scale, renderDoubleFace, 0, 0, 0);
	}

	/**
	 * Creates a new ModelConverter that converts a minecraft model to a list of vertices and UVs.
	 * @param packing				The texture packing
	 * @param model					The model
	 * @param scale					Scale of the model (usually 0.065)
	 * @param renderDoubleFace		Set to true if the faces should be rendered in both directions
	 * @param rotationX				Rotation around X axis (degrees)
	 * @param rotationY				Rotation around Y axis (degrees)
	 * @param rotationZ				Rotation around Z axis (degrees)
	 */
	public ModelConverter(@Nullable Packing packing, ModelBase model, double scale, boolean renderDoubleFace, float rotationX, float rotationY, float rotationZ) {
		this(packing, model, scale, renderDoubleFace, rotationX, rotationY, rotationZ, new Vec3UV(0, 0, 0));
	}

	/**
	 * Creates a new ModelConverter that converts a minecraft model to a list of vertices and UVs.
	 * @param packing				The texture packing
	 * @param model					The model
	 * @param scale					Scale of the model (usually 0.065)
	 * @param renderDoubleFace		Set to true if the faces should be rendered in both directions
	 * @param rotationX				Rotation around X axis (degrees)
	 * @param rotationY				Rotation around Y axis (degrees)
	 * @param rotationZ				Rotation around Z axis (degrees)
	 * @param rotationCenter		Center of the rotation
	 */
	public ModelConverter(@Nullable Packing packing, ModelBase model, double scale, boolean renderDoubleFace, float rotationX, float rotationY, float rotationZ, Vec3UV rotationCenter) {
		this.packing = packing;
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
	private Vec3UV transformPosition(Vec3UV scaledPos, ModelRenderer modelRenderer, RotationMatrix rotationMatrix, double modelScale) {
		Vec3UV scaledRotPos = new Vec3UV(modelRenderer.rotationPointX * modelScale, modelRenderer.rotationPointY * modelScale, modelRenderer.rotationPointZ * modelScale);

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
	private Vec3UV getBoxCorner(boolean xb, boolean yb, boolean zb, ModelBox modelBox, ModelRenderer modelRenderer, double modelScale, RotationMatrix rotationMatrix) {
		double posX = (!xb ? modelBox.posX1 : modelBox.posX2) + modelRenderer.offsetX;
		double posY = (!yb ? modelBox.posY1 : modelBox.posY2) + modelRenderer.offsetY;
		double posZ = (!zb ? modelBox.posZ1 : modelBox.posZ2) + modelRenderer.offsetZ;
		Vec3UV scaledPos = new Vec3UV(posX * modelScale, posY * modelScale, posZ * modelScale);
		Vec3UV scaledRotPos = new Vec3UV(modelRenderer.rotationPointX * modelScale, modelRenderer.rotationPointY * modelScale, modelRenderer.rotationPointZ * modelScale);

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
		this.modelBoxList.clear();

		List<TextureQuad> packerQuads = new ArrayList<>();

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
				Vec3UV o = this.getBoxCorner(false, false, false, modelBox, modelRenderer, modelScale, ROTATION_MATRIX);
				Vec3UV ox = this.getBoxCorner(true, false, false, modelBox, modelRenderer, modelScale, ROTATION_MATRIX);
				Vec3UV oy = this.getBoxCorner(false, true, false, modelBox, modelRenderer, modelScale, ROTATION_MATRIX);
				Vec3UV oz = this.getBoxCorner(false, false, true, modelBox, modelRenderer, modelScale, ROTATION_MATRIX);
				Vec3UV oxy = this.getBoxCorner(true, true, false, modelBox, modelRenderer, modelScale, ROTATION_MATRIX);
				Vec3UV oyz = this.getBoxCorner(false, true, true, modelBox, modelRenderer, modelScale, ROTATION_MATRIX);
				Vec3UV oxz = this.getBoxCorner(true, false, true, modelBox, modelRenderer, modelScale, ROTATION_MATRIX);
				Vec3UV oxyz = this.getBoxCorner(true, true, true, modelBox, modelRenderer, modelScale, ROTATION_MATRIX);

				TexturedQuad[] mbQuadList = null;
				try {
					mbQuadList = modelBox.quadList;
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
				this.addQuadFace(o, oy, oxy, ox, mbVertices5[0], mbVertices5[3], mbVertices5[2], mbVertices5[1], modelWidth, modelHeight, renderDoubleFace, quadList, packerQuads);
				/*Quad face1 = new Quad(
						new Vec3UV(o, mbVertices5[0].texturePositionX, mbVertices5[0].texturePositionY, modelWidth, modelHeight),
						new Vec3UV(oy, mbVertices5[3].texturePositionX, mbVertices5[3].texturePositionY, modelWidth, modelHeight),
						new Vec3UV(oxy, mbVertices5[2].texturePositionX, mbVertices5[2].texturePositionY, modelWidth, modelHeight),
						new Vec3UV(ox, mbVertices5[1].texturePositionX, mbVertices5[1].texturePositionY, modelWidth, modelHeight));
				quadList.add(face1);
				if(renderDoubleFace) {
					Quad face1d = new Quad(
							new Vec3UV(o, mbVertices5[0].texturePositionX, mbVertices5[0].texturePositionY, modelWidth, modelHeight),
							new Vec3UV(ox, mbVertices5[1].texturePositionX, mbVertices5[1].texturePositionY, modelWidth, modelHeight),
							new Vec3UV(oxy, mbVertices5[2].texturePositionX, mbVertices5[2].texturePositionY, modelWidth, modelHeight),
							new Vec3UV(oy, mbVertices5[3].texturePositionX, mbVertices5[3].texturePositionY, modelWidth, modelHeight));
					quadList.add(face1d);
				}*/

				//Face 2
				this.addQuadFace(oz, oxz, oxyz, oyz, mbVertices6[1], mbVertices6[0], mbVertices6[3], mbVertices6[2], modelWidth, modelHeight, renderDoubleFace, quadList, packerQuads);
				/*Quad face2 = new Quad(
						new Vec3UV(oz, mbVertices6[1].texturePositionX, mbVertices6[1].texturePositionY, modelWidth, modelHeight),
						new Vec3UV(oxz, mbVertices6[0].texturePositionX, mbVertices6[0].texturePositionY, modelWidth, modelHeight),
						new Vec3UV(oxyz, mbVertices6[3].texturePositionX, mbVertices6[3].texturePositionY, modelWidth, modelHeight),
						new Vec3UV(oyz, mbVertices6[2].texturePositionX, mbVertices6[2].texturePositionY, modelWidth, modelHeight));
				quadList.add(face2);
				if(renderDoubleFace) {
					Quad face2d = new Quad(
							new Vec3UV(oz, mbVertices6[1].texturePositionX, mbVertices6[1].texturePositionY, modelWidth, modelHeight),
							new Vec3UV(oyz, mbVertices6[2].texturePositionX, mbVertices6[2].texturePositionY, modelWidth, modelHeight),
							new Vec3UV(oxyz, mbVertices6[3].texturePositionX, mbVertices6[3].texturePositionY, modelWidth, modelHeight),
							new Vec3UV(oxz, mbVertices6[0].texturePositionX, mbVertices6[0].texturePositionY, modelWidth, modelHeight));
					quadList.add(face2d);
				}*/

				//Face 3
				this.addQuadFace(oy, oyz, oxyz, oxy, mbVertices4[1], mbVertices4[2], mbVertices4[3], mbVertices4[0], modelWidth, modelHeight, renderDoubleFace, quadList, packerQuads);
				/*Quad face3 = new Quad(
						new Vec3UV(oy, mbVertices4[1].texturePositionX, mbVertices4[1].texturePositionY, modelWidth, modelHeight),
						new Vec3UV(oyz, mbVertices4[2].texturePositionX, mbVertices4[2].texturePositionY, modelWidth, modelHeight),
						new Vec3UV(oxyz, mbVertices4[3].texturePositionX, mbVertices4[3].texturePositionY, modelWidth, modelHeight),
						new Vec3UV(oxy, mbVertices4[0].texturePositionX, mbVertices4[0].texturePositionY, modelWidth, modelHeight));
				quadList.add(face3);
				if(renderDoubleFace) {
					Quad face3d = new Quad(
							new Vec3UV(oy, mbVertices4[1].texturePositionX, mbVertices4[1].texturePositionY, modelWidth, modelHeight),
							new Vec3UV(oxy, mbVertices4[0].texturePositionX, mbVertices4[0].texturePositionY, modelWidth, modelHeight),
							new Vec3UV(oxyz, mbVertices4[3].texturePositionX, mbVertices4[3].texturePositionY, modelWidth, modelHeight),
							new Vec3UV(oyz, mbVertices4[2].texturePositionX, mbVertices4[2].texturePositionY, modelWidth, modelHeight));
					quadList.add(face3d);
				}*/

				//Face 4
				this.addQuadFace(o, ox, oxz, oz, mbVertices3[2], mbVertices3[3], mbVertices3[0], mbVertices3[1], modelWidth, modelHeight, renderDoubleFace, quadList, packerQuads);
				/*Quad face4 = new Quad(
						new Vec3UV(o, mbVertices3[2].texturePositionX, mbVertices3[2].texturePositionY, modelWidth, modelHeight),
						new Vec3UV(ox, mbVertices3[3].texturePositionX, mbVertices3[3].texturePositionY, modelWidth, modelHeight),
						new Vec3UV(oxz, mbVertices3[0].texturePositionX, mbVertices3[0].texturePositionY, modelWidth, modelHeight),
						new Vec3UV(oz, mbVertices3[1].texturePositionX, mbVertices3[1].texturePositionY, modelWidth, modelHeight));
				quadList.add(face4);
				if(renderDoubleFace) {
					Quad face4d = new Quad(
							new Vec3UV(o, mbVertices3[2].texturePositionX, mbVertices3[2].texturePositionY, modelWidth, modelHeight),
							new Vec3UV(oz, mbVertices3[1].texturePositionX, mbVertices3[1].texturePositionY, modelWidth, modelHeight),
							new Vec3UV(oxz, mbVertices3[0].texturePositionX, mbVertices3[0].texturePositionY, modelWidth, modelHeight),
							new Vec3UV(ox, mbVertices3[3].texturePositionX, mbVertices3[3].texturePositionY, modelWidth, modelHeight));
					quadList.add(face4d);
				}*/

				//Face 5
				this.addQuadFace(ox, oxy, oxyz, oxz, mbVertices1[1], mbVertices1[2], mbVertices1[3], mbVertices1[0], modelWidth, modelHeight, renderDoubleFace, quadList, packerQuads);
				/*Quad face5 = new Quad(
						new Vec3UV(ox, mbVertices1[1].texturePositionX, mbVertices1[1].texturePositionY, modelWidth, modelHeight),
						new Vec3UV(oxy, mbVertices1[2].texturePositionX, mbVertices1[2].texturePositionY, modelWidth, modelHeight),
						new Vec3UV(oxyz, mbVertices1[3].texturePositionX, mbVertices1[3].texturePositionY, modelWidth, modelHeight),
						new Vec3UV(oxz, mbVertices1[0].texturePositionX, mbVertices1[0].texturePositionY, modelWidth, modelHeight));
				quadList.add(face5);
				if(renderDoubleFace) {
					Quad face5d = new Quad(
							new Vec3UV(ox, mbVertices1[1].texturePositionX, mbVertices1[1].texturePositionY, modelWidth, modelHeight),
							new Vec3UV(oxz, mbVertices1[0].texturePositionX, mbVertices1[0].texturePositionY, modelWidth, modelHeight),
							new Vec3UV(oxyz, mbVertices1[3].texturePositionX, mbVertices1[3].texturePositionY, modelWidth, modelHeight),
							new Vec3UV(oxy, mbVertices1[2].texturePositionX, mbVertices1[2].texturePositionY, modelWidth, modelHeight));
					quadList.add(face5d);
				}*/

				//Face 6
				this.addQuadFace(o, oz, oyz, oy, mbVertices2[0], mbVertices2[1], mbVertices2[2], mbVertices2[3], modelWidth, modelHeight, renderDoubleFace, quadList, packerQuads);
				/*Quad face6 = new Quad(
						new Vec3UV(o, mbVertices2[0].texturePositionX, mbVertices2[0].texturePositionY, modelWidth, modelHeight),
						new Vec3UV(oz, mbVertices2[1].texturePositionX, mbVertices2[1].texturePositionY, modelWidth, modelHeight),
						new Vec3UV(oyz, mbVertices2[2].texturePositionX, mbVertices2[2].texturePositionY, modelWidth, modelHeight),
						new Vec3UV(oy, mbVertices2[3].texturePositionX, mbVertices2[3].texturePositionY, modelWidth, modelHeight));
				quadList.add(face6);
				if(renderDoubleFace) {
					Quad face6d = new Quad(
							new Vec3UV(o, mbVertices2[0].texturePositionX, mbVertices2[0].texturePositionY, modelWidth, modelHeight),
							new Vec3UV(oy, mbVertices2[3].texturePositionX, mbVertices2[3].texturePositionY, modelWidth, modelHeight),
							new Vec3UV(oyz, mbVertices2[2].texturePositionX, mbVertices2[2].texturePositionY, modelWidth, modelHeight),
							new Vec3UV(oz, mbVertices2[1].texturePositionX, mbVertices2[1].texturePositionY, modelWidth, modelHeight));
					quadList.add(face6d);
				}*/

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

		this.model = new Model(this.modelBoxList, this.fwdVec, this.upVec);

		if(this.packing != null) {
			this.packing.packer.addTextureMap(new TextureQuadMap(this.packing.texture, this.packing.width, this.packing.height, packerQuads, this.packing.owner));
		}
	}

	private void addQuadFace(Vec3UV v1, Vec3UV v2, Vec3UV v3, Vec3UV v4, 
			PositionTextureVertex tv1, PositionTextureVertex tv2, PositionTextureVertex tv3, PositionTextureVertex tv4,
			double modelWidth, double modelHeight, boolean renderDoubleFace, List<Quad> quadList, List<TextureQuad> packerQuads) {

		if(this.packing != null) {
			int minU = Integer.MAX_VALUE;
			int minV = Integer.MAX_VALUE;
			int maxU = 0;
			int maxV = 0;

			PositionTextureVertex[] verts = new PositionTextureVertex[] {tv1, tv2, tv3, tv4};

			int maxUVertex1 = 0;
			int maxUVertex2 = 0;
			int maxVVertex1 = 0;
			int maxVVertex2 = 0;

			for(int i = 0; i < verts.length; i++) {
				PositionTextureVertex vert = verts[i];

				if((int)Math.floor(vert.texturePositionX * modelWidth) >= maxU) {
					maxUVertex2 = maxUVertex1;
					maxUVertex1 = i;
				}
				if((int)Math.floor(vert.texturePositionY * modelHeight) >= maxV) {
					maxVVertex2 = maxVVertex1;
					maxVVertex1 = i;
				}

				minU = Math.min(minU, (int)Math.floor(vert.texturePositionX * modelWidth));
				minV = Math.min(minV, (int)Math.floor(vert.texturePositionY * modelHeight));
				maxU = Math.max(maxU, (int)Math.floor(vert.texturePositionX * modelWidth));
				maxV = Math.max(maxV, (int)Math.floor(vert.texturePositionY * modelHeight));
			}

			TextureQuad packerQuad = new TextureQuad(minU, minV, maxU - minU, maxV - minV);

			Quad face = new Quad(
					new Vec3UV(v1, packerQuad, maxUVertex1 == 0 || maxUVertex2 == 0, maxVVertex1 == 0 || maxVVertex2 == 0, modelWidth, modelHeight),
					new Vec3UV(v4, packerQuad, maxUVertex1 == 3 || maxUVertex2 == 3, maxVVertex1 == 3 || maxVVertex2 == 3, modelWidth, modelHeight),
					new Vec3UV(v3, packerQuad, maxUVertex1 == 2 || maxUVertex2 == 2, maxVVertex1 == 2 || maxVVertex2 == 2, modelWidth, modelHeight),
					new Vec3UV(v2, packerQuad, maxUVertex1 == 1 || maxUVertex2 == 1, maxVVertex1 == 1 || maxVVertex2 == 1, modelWidth, modelHeight) 
					);
			quadList.add(face);
			if(renderDoubleFace) {
				Quad doubleFace = new Quad(
						new Vec3UV(v1, packerQuad, maxUVertex1 == 0 || maxUVertex2 == 0, maxVVertex1 == 0 || maxVVertex2 == 0, modelWidth, modelHeight),
						new Vec3UV(v2, packerQuad, maxUVertex1 == 1 || maxUVertex2 == 1, maxVVertex1 == 1 || maxVVertex2 == 1, modelWidth, modelHeight),
						new Vec3UV(v3, packerQuad, maxUVertex1 == 2 || maxUVertex2 == 2, maxVVertex1 == 2 || maxVVertex2 == 2, modelWidth, modelHeight),
						new Vec3UV(v4, packerQuad, maxUVertex1 == 3 || maxUVertex2 == 3, maxVVertex1 == 3 || maxVVertex2 == 3, modelWidth, modelHeight) 
						);
				quadList.add(doubleFace);
			}

			packerQuads.add(packerQuad);
		} else {
			Quad face = new Quad(
					new Vec3UV(v1, tv1.texturePositionX, tv1.texturePositionY, modelWidth, modelHeight),
					new Vec3UV(v4, tv4.texturePositionX, tv4.texturePositionY, modelWidth, modelHeight),
					new Vec3UV(v3, tv3.texturePositionX, tv3.texturePositionY, modelWidth, modelHeight),
					new Vec3UV(v2, tv2.texturePositionX, tv2.texturePositionY, modelWidth, modelHeight)
					);
			quadList.add(face);
			if(renderDoubleFace) {
				Quad doubleFace = new Quad(
						new Vec3UV(v1, tv1.texturePositionX, tv1.texturePositionY, modelWidth, modelHeight),
						new Vec3UV(v2, tv2.texturePositionX, tv2.texturePositionY, modelWidth, modelHeight),
						new Vec3UV(v3, tv3.texturePositionX, tv3.texturePositionY, modelWidth, modelHeight),
						new Vec3UV(v4, tv4.texturePositionX, tv4.texturePositionY, modelWidth, modelHeight)
						);
				quadList.add(doubleFace);
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
	public ModelConverter rotate(float rotation, float x, float y, float z, Vec3UV center) {
		ROTATION_MATRIX.setRotations((float)Math.toRadians(x * rotation), (float)Math.toRadians(y * rotation), (float)Math.toRadians(z * rotation));
		for(Box box : this.modelBoxList) {
			for(Quad quad : box.quads) {
				for(int i = 0; i < 4; i++) {
					Vec3UV vec = quad.vertices[i];
					Vec3UV rotatedPoint = null;
					rotatedPoint = ROTATION_MATRIX.transformVec(vec, center);
					vec.x = rotatedPoint.x;
					vec.y = rotatedPoint.y;
					vec.z = rotatedPoint.z;
				}
			}
		}
		Vec3UV rotatedFwdVec = ROTATION_MATRIX.transformVec(this.fwdVec, center);
		this.fwdVec.x = rotatedFwdVec.x;
		this.fwdVec.y = rotatedFwdVec.y;
		this.fwdVec.z = rotatedFwdVec.z;
		Vec3UV rotatedUpVec = ROTATION_MATRIX.transformVec(this.upVec, center);
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
	public ModelConverter offsetWS(Vec3UV offset) {
		for(Box box : this.modelBoxList) {
			for(Quad quad : box.quads) {
				for(int i = 0; i < 4; i++) {
					Vec3UV vec = quad.vertices[i];
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
	public ModelConverter offsetMS(Vec3UV offset) {
		Vec3UV leftVec = this.fwdVec.cross(this.upVec);
		for(Box box : this.modelBoxList) {
			for(Quad quad : box.quads) {
				for(int i = 0; i < 4; i++) {
					Vec3UV vec = quad.vertices[i];
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
					Vec3UV vec = quad.vertices[i];
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
	 * Returns a copy of this model.
	 * @return
	 */
	public Model getModel() {
		return this.model;
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