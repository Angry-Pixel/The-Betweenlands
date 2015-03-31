package thebetweenlands.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class ModelConverter {
	private static final RotationMatrix ROTATION_MATRIX = new RotationMatrix();

	public static class vec3 {
		public double x, y, z;
		public vec3(double x, double y, double z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}

	public static class vec3UV {
		public double x, y, z, u, v;
		public vec3UV(double x, double y, double z, double u, double v) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.u = u;
			this.v = v;
		}
	}

	public static class RotationMatrix {
		private float alpha;
		private float beta;
		private float gamma;
		private float matrix[];

		public void setRotations(float rotG, float rotB, float rotA) {
			this.alpha = rotA;
			this.beta = rotB;
			this.gamma = rotG;
			float sinAlpha = (float)Math.sin(this.alpha);
			float sinBeta = (float)Math.sin(this.beta);
			float sinGamma = (float)Math.sin(this.gamma);
			float cosAlpha = (float)Math.cos(this.alpha);
			float cosBeta = (float)Math.cos(this.beta);
			float cosGamma = (float)Math.cos(this.gamma);
			this.matrix = new float[] { cosAlpha * cosBeta, (cosAlpha * sinBeta * sinGamma) - (sinAlpha * cosGamma), (cosAlpha * sinBeta * cosGamma) + (sinAlpha * sinGamma),
					sinAlpha * cosBeta, (sinAlpha * sinBeta * sinGamma) + (cosAlpha * cosGamma), (sinAlpha * sinBeta * cosGamma) - (cosAlpha * sinGamma),
					-sinBeta,  cosBeta * sinGamma, cosBeta * cosGamma};
		}

		public vec3 transformVec(vec3 rotpoint, vec3 centerpoint) {
			vec3 result = new vec3(0, 0, 0);

			double px = rotpoint.x - centerpoint.x;
			double py = rotpoint.y - centerpoint.y;
			double pz = rotpoint.z - centerpoint.z;

			result.x = this.matrix[0]*px + this.matrix[1]*py + this.matrix[2]*pz;
			result.y = this.matrix[3]*px + this.matrix[4]*py + this.matrix[5]*pz;
			result.z = this.matrix[6]*px + this.matrix[7]*py + this.matrix[8]*pz;

			result.x += centerpoint.x;
			result.y += centerpoint.y;
			result.z += centerpoint.z;

			return result;
		}
	}

	private vec3 getBoxCorner(boolean xb, boolean yb, boolean zb, ModelBox modelBox, ModelRenderer modelRenderer, double modelScale, RotationMatrix rm) {
		double posX = ((!xb ? modelBox.posX1 : modelBox.posX2) + modelRenderer.offsetX);
		double posY = ((!yb ? modelBox.posY1 : modelBox.posY2) + modelRenderer.offsetY);
		double posZ = (!zb ? modelBox.posZ1 : modelBox.posZ2) + modelRenderer.offsetZ;
		vec3 scaledPos = new vec3(posX * modelScale, (posY) * modelScale, posZ * modelScale);
		vec3 scaledRotPos = new vec3(modelRenderer.rotationPointX * modelScale, modelRenderer.rotationPointY * modelScale, modelRenderer.rotationPointZ * modelScale);

		//Dirty fix to prevent some bugs when rotation == 0
		if(modelRenderer.rotateAngleX == 0.0F && modelRenderer.rotateAngleY == 0.0F && modelRenderer.rotateAngleZ == 0.0F) {
			modelRenderer.rotateAngleX = 0.0001F;
			modelRenderer.rotateAngleY = 0.0001F;
			modelRenderer.rotateAngleZ = 0.0001F;
		}

		scaledPos.x += scaledRotPos.x;
		scaledPos.y += scaledRotPos.y;
		scaledPos.z += scaledRotPos.z;

		rm.setRotations(modelRenderer.rotateAngleX, modelRenderer.rotateAngleY, modelRenderer.rotateAngleZ);
		scaledPos = rm.transformVec(scaledPos, scaledRotPos);

		rm.setRotations((float)Math.toRadians(-180 - this.rotationPitch), 0, 0);
		scaledPos = rm.transformVec(scaledPos, new vec3(0, 0, 0));

		rm.setRotations(0, (float)Math.toRadians(-this.rotationYaw), 0);
		return rm.transformVec(scaledPos, new vec3(0, 0, 0));
	}

	private final float rotationYaw;
	private final float rotationPitch;
	private final ArrayList<vec3UV> modelQuadList = new ArrayList<vec3UV>();

	public ModelConverter(ModelBase model, double scale, double textureWidth, double textureHeight, IIcon texture, boolean renderDoubleFace) {
		this.rotationYaw = 0.0F;
		this.rotationPitch = 0.0F;
		this.constructModel(model, scale, textureWidth, textureHeight, texture, renderDoubleFace);
	}
	
	public ModelConverter(ModelBase model, double scale, double textureWidth, double textureHeight, IIcon texture, boolean renderDoubleFace, float rotationYaw, float rotationPitch) {
		this.rotationYaw = rotationYaw;
		this.rotationPitch = rotationPitch;
		this.constructModel(model, scale, textureWidth, textureHeight, texture, renderDoubleFace);
	}

	private void constructModel(ModelBase modelBase, double modelScale, double actualWidth, double actualHeight, IIcon texture, boolean renderDoubleFace) {
		//Model texture width/height
		double modelWidth = modelBase.textureWidth;
		double modelHeight = modelBase.textureHeight;

		for(Object obj1 : modelBase.boxList) {
			ModelRenderer modelRenderer = (ModelRenderer) obj1;
			for(Object obj2 : modelRenderer.cubeList) {
				ModelBox modelBox = (ModelBox) obj2;

				//ModelBox transformed vertices
				vec3 o = this.getBoxCorner(false, false, false, modelBox, modelRenderer, modelScale, ROTATION_MATRIX);
				vec3 ox = this.getBoxCorner(true, false, false, modelBox, modelRenderer, modelScale, ROTATION_MATRIX);
				vec3 oy = this.getBoxCorner(false, true, false, modelBox, modelRenderer, modelScale, ROTATION_MATRIX);
				vec3 oz = this.getBoxCorner(false, false, true, modelBox, modelRenderer, modelScale, ROTATION_MATRIX);
				vec3 oxy = this.getBoxCorner(true, true, false, modelBox, modelRenderer, modelScale, ROTATION_MATRIX);
				vec3 oyz = this.getBoxCorner(false, true, true, modelBox, modelRenderer, modelScale, ROTATION_MATRIX);
				vec3 oxz = this.getBoxCorner(true, false, true, modelBox, modelRenderer, modelScale, ROTATION_MATRIX);
				vec3 oxyz = this.getBoxCorner(true, true, true, modelBox, modelRenderer, modelScale, ROTATION_MATRIX);

				//ModelBox quad list
				Field f_mbQuadList = ReflectionHelper.findField(ModelBox.class, "quadList", "field_78254_i");
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

				double umin = texture.getMinU();
				double vmin = texture.getMinV();
				double umax = texture.getMaxU();
				double vmax = texture.getMaxV();
				double uvWidth = (umax - umin) * modelWidth / actualWidth;
				double uvHeight = (vmax - vmin) * modelHeight / actualHeight;

				double u11 = umin + mbVertices5[0].texturePositionX * uvWidth;
				double v11 = vmin + mbVertices5[0].texturePositionY * uvHeight;
				double u14 = umin + mbVertices5[1].texturePositionX * uvWidth;
				double v14 = vmin + mbVertices5[1].texturePositionY * uvHeight;
				double u13 = umin + mbVertices5[2].texturePositionX * uvWidth;
				double v13 = vmin + mbVertices5[2].texturePositionY * uvHeight;
				double u12 = umin + mbVertices5[3].texturePositionX * uvWidth;
				double v12 = vmin + mbVertices5[3].texturePositionY * uvHeight;

				double u23 = umin + mbVertices6[0].texturePositionX * uvWidth;
				double v23 = vmin + mbVertices6[0].texturePositionY * uvHeight;
				double u24 = umin + mbVertices6[1].texturePositionX * uvWidth;
				double v24 = vmin + mbVertices6[1].texturePositionY * uvHeight;
				double u21 = umin + mbVertices6[2].texturePositionX * uvWidth;
				double v21 = vmin + mbVertices6[2].texturePositionY * uvHeight;
				double u22 = umin + mbVertices6[3].texturePositionX * uvWidth;
				double v22 = vmin + mbVertices6[3].texturePositionY * uvHeight;

				double u31 = umin + mbVertices4[0].texturePositionX * uvWidth;
				double v31 = vmin + mbVertices4[0].texturePositionY * uvHeight;
				double u34 = umin + mbVertices4[1].texturePositionX * uvWidth;
				double v34 = vmin + mbVertices4[1].texturePositionY * uvHeight;
				double u33 = umin + mbVertices4[2].texturePositionX * uvWidth;
				double v33 = vmin + mbVertices4[2].texturePositionY * uvHeight;
				double u32 = umin + mbVertices4[3].texturePositionX * uvWidth;
				double v32 = vmin + mbVertices4[3].texturePositionY * uvHeight;

				double u43 = umin + mbVertices3[0].texturePositionX * uvWidth;
				double v43 = vmin + mbVertices3[0].texturePositionY * uvHeight;
				double u44 = umin + mbVertices3[1].texturePositionX * uvWidth;
				double v44 = vmin + mbVertices3[1].texturePositionY * uvHeight;
				double u41 = umin + mbVertices3[2].texturePositionX * uvWidth;
				double v41 = vmin + mbVertices3[2].texturePositionY * uvHeight;
				double u42 = umin + mbVertices3[3].texturePositionX * uvWidth;
				double v42 = vmin + mbVertices3[3].texturePositionY * uvHeight;

				double u54 = umin + mbVertices1[0].texturePositionX * uvWidth;
				double v54 = vmin + mbVertices1[0].texturePositionY * uvHeight;
				double u51 = umin + mbVertices1[1].texturePositionX * uvWidth;
				double v51 = vmin + mbVertices1[1].texturePositionY * uvHeight;
				double u52 = umin + mbVertices1[2].texturePositionX * uvWidth;
				double v52 = vmin + mbVertices1[2].texturePositionY * uvHeight;
				double u53 = umin + mbVertices1[3].texturePositionX * uvWidth;
				double v53 = vmin + mbVertices1[3].texturePositionY * uvHeight;

				double u61 = umin + mbVertices2[0].texturePositionX * uvWidth;
				double v61 = vmin + mbVertices2[0].texturePositionY * uvHeight;
				double u62 = umin + mbVertices2[1].texturePositionX * uvWidth;
				double v62 = vmin + mbVertices2[1].texturePositionY * uvHeight;
				double u63 = umin + mbVertices2[2].texturePositionX * uvWidth;
				double v63 = vmin + mbVertices2[2].texturePositionY * uvHeight;
				double u64 = umin + mbVertices2[3].texturePositionX * uvWidth;
				double v64 = vmin + mbVertices2[3].texturePositionY * uvHeight;

				//1
				this.addVertexWithUV(o, u11, v11);
				this.addVertexWithUV(oy, u12, v12);
				this.addVertexWithUV(oxy, u13, v13);
				this.addVertexWithUV(ox, u14, v14);
				if(renderDoubleFace) {
					this.addVertexWithUV(o, u11, v11);
					this.addVertexWithUV(ox, u14, v14);
					this.addVertexWithUV(oxy, u13, v13);
					this.addVertexWithUV(oy, u12, v12);
				}

				//2
				this.addVertexWithUV(oz, u21, v21);
				this.addVertexWithUV(oxz, u22, v22);
				this.addVertexWithUV(oxyz, u23, v23);
				this.addVertexWithUV(oyz, u24, v24);
				if(renderDoubleFace) {
					this.addVertexWithUV(oz, u21, v21);
					this.addVertexWithUV(oyz, u24, v24);
					this.addVertexWithUV(oxyz, u23, v23);
					this.addVertexWithUV(oxz, u22, v22);
				}

				//3
				this.addVertexWithUV(oy, u31, v31);
				this.addVertexWithUV(oyz, u32, v32);
				this.addVertexWithUV(oxyz, u33, v33);
				this.addVertexWithUV(oxy, u34, v34);
				if(renderDoubleFace) {
					this.addVertexWithUV(oy, u31, v31);
					this.addVertexWithUV(oxy, u34, v34);
					this.addVertexWithUV(oxyz, u33, v33);
					this.addVertexWithUV(oyz, u32, v32);
				}

				//4
				this.addVertexWithUV(o, u41, v41);
				this.addVertexWithUV(ox, u42, v42);
				this.addVertexWithUV(oxz, u43, v43);
				this.addVertexWithUV(oz, u44, v44);
				if(renderDoubleFace) {
					this.addVertexWithUV(o, u41, v41);
					this.addVertexWithUV(oz, u44, v44);
					this.addVertexWithUV(oxz, u43, v43);
					this.addVertexWithUV(ox, u42, v42);
				}

				//5
				this.addVertexWithUV(ox, u51, v51);
				this.addVertexWithUV(oxy, u52, v52);
				this.addVertexWithUV(oxyz, u53, v53);
				this.addVertexWithUV(oxz, u54, v54);
				if(renderDoubleFace) {
					this.addVertexWithUV(ox, u51, v51);
					this.addVertexWithUV(oxz, u54, v54);
					this.addVertexWithUV(oxyz, u53, v53);
					this.addVertexWithUV(oxy, u52, v52);
				}

				//6
				this.addVertexWithUV(o, u61, v61);
				this.addVertexWithUV(oz, u62, v62);
				this.addVertexWithUV(oyz, u63, v63);
				this.addVertexWithUV(oy, u64, v64);
				if(renderDoubleFace) {
					this.addVertexWithUV(o, u61, v61);
					this.addVertexWithUV(oy, u64, v64);
					this.addVertexWithUV(oyz, u63, v63);
					this.addVertexWithUV(oz, u62, v62);
				}
			}
		}
	}

	private void addVertexWithUV(vec3 vert, double u, double v) {
		this.modelQuadList.add(new vec3UV(vert.x, vert.y, vert.z, u, v));
	}

	public ArrayList<vec3UV> getVertices() {
		return this.modelQuadList;
	}

	public void renderWithTessellator(Tessellator tessellator) {
		for(vec3UV vert : this.modelQuadList) {
			tessellator.addVertexWithUV(vert.x, vert.y, vert.z, vert.u, vert.v);
		}
	}
}
