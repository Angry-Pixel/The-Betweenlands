package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TextureOffset;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModelWallLamprey extends ModelBase {
	public static class BlockTexturedModelRenderer extends ModelRenderer {
		private int textureOffsetX;
		private int textureOffsetY;
		private final ModelBase baseModel;
		private final boolean root;

		public BlockTexturedModelRenderer(ModelBase model, boolean root) {
			super(model);
			this.baseModel = model;
			this.root = root;
		}

		public BlockTexturedModelRenderer(ModelBase model, int texOffX, int texOffY, boolean root) {
			super(model, texOffX, texOffY);
			this.baseModel = model;
			this.root = root;
		}

		public BlockTexturedModelRenderer(ModelBase model, String boxNameIn, boolean root) {
			super(model, boxNameIn);
			this.baseModel = model;
			this.root = root;
		}

		@Override
		public ModelRenderer setTextureOffset(int x, int y) {
			this.textureOffsetX = x;
			this.textureOffsetY = y;
			return super.setTextureOffset(x, y);
		}

		@Override
		public ModelRenderer addBox(String partName, float offX, float offY, float offZ, int width, int height, int depth) {
			partName = this.boxName + "." + partName;
			TextureOffset textureoffset = this.baseModel.getTextureOffset(partName);
			this.setTextureOffset(textureoffset.textureOffsetX, textureoffset.textureOffsetY);
			this.cubeList.add((new BlockTexturedModelBox(this, this.textureOffsetX, this.textureOffsetY, offX, offY, offZ, width, height, depth, 0.0F)).setBoxName(partName));
			return this;
		}

		@Override
		public ModelRenderer addBox(float offX, float offY, float offZ, int width, int height, int depth) {
			this.cubeList.add(new BlockTexturedModelBox(this, this.textureOffsetX, this.textureOffsetY, offX, offY, offZ, width, height, depth, 0.0F));
			return this;
		}

		@Override
		public ModelRenderer addBox(float offX, float offY, float offZ, int width, int height, int depth, boolean mirrored) {
			this.cubeList.add(new BlockTexturedModelBox(this, this.textureOffsetX, this.textureOffsetY, offX, offY, offZ, width, height, depth, 0.0F, mirrored));
			return this;
		}

		@Override
		public void addBox(float offX, float offY, float offZ, int width, int height, int depth, float scaleFactor) {
			this.cubeList.add(new BlockTexturedModelBox(this, this.textureOffsetX, this.textureOffsetY, offX, offY, offZ, width, height, depth, scaleFactor));
		}
	}

	public static class BlockTexturedModelBox extends ModelBox {
		private static enum BlockPlane {
			XY, YX,
			XZ, ZX,
			YZ, ZY
		}

		private final PositionTextureVertex[] vertexPositions;
		public final TexturedQuad[] quadList;
		public final float posX1;
		public final float posY1;
		public final float posZ1;
		public final float posX2;
		public final float posY2;
		public final float posZ2;
		public String boxName;

		public BlockTexturedModelBox(BlockTexturedModelRenderer renderer, int texU, int texV, float x, float y, float z, int dx, int dy, int dz, float delta) {
			this(renderer, texU, texV, x, y, z, dx, dy, dz, delta, renderer.mirror);
		}

		public BlockTexturedModelBox(BlockTexturedModelRenderer renderer, int texU, int texV, float x, float y, float z, int dx, int dy, int dz, float delta, boolean mirror) {
			super(renderer, texU, texV, x, y, z, dx, dy, dz, delta, mirror);

			this.posX1 = x;
			this.posY1 = y;
			this.posZ1 = z;
			this.posX2 = x + (float)dx;
			this.posY2 = y + (float)dy;
			this.posZ2 = z + (float)dz;
			this.vertexPositions = new PositionTextureVertex[8];
			this.quadList = new TexturedQuad[6];

			float x2 = x + (float)dx;
			float y2 = y + (float)dy;
			float z2 = z + (float)dz;
			x = x - delta;
			y = y - delta;
			z = z - delta;
			x2 = x2 + delta;
			y2 = y2 + delta;
			z2 = z2 + delta;

			if(mirror) {
				float f3 = x2;
				x2 = x;
				x = f3;
			}

			PositionTextureVertex v7 = new PositionTextureVertex(x, y, z, 0.0F, 0.0F);
			PositionTextureVertex v0 = new PositionTextureVertex(x2, y, z, 0.0F, 8.0F);
			PositionTextureVertex v1 = new PositionTextureVertex(x2, y2, z, 8.0F, 8.0F);
			PositionTextureVertex v2 = new PositionTextureVertex(x, y2, z, 8.0F, 0.0F);
			PositionTextureVertex v3 = new PositionTextureVertex(x, y, z2, 0.0F, 0.0F);
			PositionTextureVertex v4 = new PositionTextureVertex(x2, y, z2, 0.0F, 8.0F);
			PositionTextureVertex v5 = new PositionTextureVertex(x2, y2, z2, 8.0F, 8.0F);
			PositionTextureVertex v6 = new PositionTextureVertex(x, y2, z2, 8.0F, 0.0F);

			this.vertexPositions[0] = v7;
			this.vertexPositions[1] = v0;
			this.vertexPositions[2] = v1;
			this.vertexPositions[3] = v2;
			this.vertexPositions[4] = v3;
			this.vertexPositions[5] = v4;
			this.vertexPositions[6] = v5;
			this.vertexPositions[7] = v6;

			this.quadList[0] = new TexturedQuad(new PositionTextureVertex[] {new PositionTextureVertex(v4, 0, 0), new PositionTextureVertex(v0, 0, 0), new PositionTextureVertex(v1, 0, 0), new PositionTextureVertex(v5, 0, 0)}, 0, 0, 0, 0, renderer.textureWidth, renderer.textureHeight);
			this.quadList[1] = new TexturedQuad(new PositionTextureVertex[] {new PositionTextureVertex(v7, 0, 0), new PositionTextureVertex(v3, 0, 0), new PositionTextureVertex(v6, 0, 0), new PositionTextureVertex(v2, 0, 0)}, 0, 0, 0, 0, renderer.textureWidth, renderer.textureHeight);
			this.quadList[2] = new TexturedQuad(new PositionTextureVertex[] {new PositionTextureVertex(v4, 0, 0), new PositionTextureVertex(v3, 0, 0), new PositionTextureVertex(v7, 0, 0), new PositionTextureVertex(v0, 0, 0)}, 0, 0, 0, 0, renderer.textureWidth, renderer.textureHeight);
			this.quadList[3] = new TexturedQuad(new PositionTextureVertex[] {new PositionTextureVertex(v1, 0, 0), new PositionTextureVertex(v2, 0, 0), new PositionTextureVertex(v6, 0, 0), new PositionTextureVertex(v5, 0, 0)}, 0, 0, 0, 0, renderer.textureWidth, renderer.textureHeight);
			this.quadList[4] = new TexturedQuad(new PositionTextureVertex[] {new PositionTextureVertex(v0, 0, 0), new PositionTextureVertex(v7, 0, 0), new PositionTextureVertex(v2, 0, 0), new PositionTextureVertex(v1, 0, 0)}, 0, 0, 0, 0, renderer.textureWidth, renderer.textureHeight);
			this.quadList[5] = new TexturedQuad(new PositionTextureVertex[] {new PositionTextureVertex(v3, 0, 0), new PositionTextureVertex(v4, 0, 0), new PositionTextureVertex(v5, 0, 0), new PositionTextureVertex(v6, 0, 0)}, 0, 0, 0, 0, renderer.textureWidth, renderer.textureHeight);

			this.setVertexBlockUVs(this.quadList[0], renderer, BlockPlane.ZY);
			this.setVertexBlockUVs(this.quadList[1], renderer, BlockPlane.ZY);
			this.setVertexBlockUVs(this.quadList[2], renderer, BlockPlane.XZ);
			this.setVertexBlockUVs(this.quadList[3], renderer, BlockPlane.XZ);
			this.setVertexBlockUVs(this.quadList[4], renderer, BlockPlane.XY);
			this.setVertexBlockUVs(this.quadList[5], renderer, BlockPlane.XY);

			if(mirror) {
				for(TexturedQuad texturedquad : this.quadList) {
					texturedquad.flipFace();
				}
			}
		}

		private void setVertexBlockUVs(TexturedQuad quad, BlockTexturedModelRenderer renderer, BlockPlane plane) {
			for(int i = 0; i < quad.vertexPositions.length; i++) {
				PositionTextureVertex vertex = quad.vertexPositions[i];
				double up;
				double vp;
				switch(plane) {
				default:
				case XY:
					up = (!renderer.root ? renderer.rotationPointX : 0) + vertex.vector3D.x;
					vp = (!renderer.root ? renderer.rotationPointY : 0) + vertex.vector3D.y;
					break;
				case YX:
					up = (!renderer.root ? renderer.rotationPointY : 0) + vertex.vector3D.y;
					vp = (!renderer.root ? renderer.rotationPointX : 0) + vertex.vector3D.x;
					break;
				case XZ:
					up = (!renderer.root ? renderer.rotationPointX : 0) + vertex.vector3D.x;
					vp = (!renderer.root ? renderer.rotationPointZ : 0) + vertex.vector3D.z;
					break;
				case ZX:
					up = (!renderer.root ? renderer.rotationPointZ : 0) + vertex.vector3D.z;
					vp = (!renderer.root ? renderer.rotationPointX : 0) + vertex.vector3D.x;
					break;
				case YZ:
					up = (!renderer.root ? renderer.rotationPointY : 0) + vertex.vector3D.y;
					vp = (!renderer.root ? renderer.rotationPointZ : 0) + vertex.vector3D.z;
					break;
				case ZY:
					up = (!renderer.root ? renderer.rotationPointZ : 0) + vertex.vector3D.z;
					vp = (!renderer.root ? renderer.rotationPointY : 0) + vertex.vector3D.y;
					break;
				}
				quad.vertexPositions[i] = vertex.setTexturePosition((float)up / renderer.textureWidth, (float)vp / renderer.textureHeight);
			}
		}

		@Override
		@SideOnly(Side.CLIENT)
		public void render(BufferBuilder renderer, float scale) {
			for(TexturedQuad texturedquad : this.quadList) {
				texturedquad.draw(renderer, scale);
			}
		}

		@Override
		public ModelBox setBoxName(String name) {
			this.boxName = name;
			return this;
		}
	}

	public ModelRenderer frontPiece1;
	public ModelRenderer frontPiece2;
	public ModelRenderer frontPiece3;
	public ModelRenderer frontPiece4;
	public ModelRenderer frontPiece5;
	public ModelRenderer frontPiece6;
	public ModelRenderer frontPiece7;
	public ModelRenderer frontPiece8;
	public ModelRenderer frontPiece9;
	public ModelRenderer frontPiece10;
	public ModelRenderer frontPiece11;
	public ModelRenderer frontPiece12;
	public ModelRenderer frontPiece13;
	public ModelRenderer frontPiece14;
	public ModelRenderer frontPiece15;
	public ModelRenderer frontPiece16;
	public ModelRenderer frontPiece17;
	public ModelRenderer left;
	public ModelRenderer right;
	public ModelRenderer bottom;
	public ModelRenderer top;
	public ModelRenderer back;
	public ModelRenderer window;

	public ModelWallLamprey() {
		this.textureWidth = 16;
		this.textureHeight = 16;
		this.frontPiece17 = new BlockTexturedModelRenderer(this, 3, 14, false);
		this.frontPiece17.setRotationPoint(3.0F, 14.0F, 0.0F);
		this.frontPiece17.addBox(0.0F, 0.0F, 0.0F, 1, 2, 1, 0.0F);
		this.frontPiece12 = new BlockTexturedModelRenderer(this, 14, 9, false);
		this.frontPiece12.setRotationPoint(14.0F, 9.0F, 0.0F);
		this.frontPiece12.addBox(0.0F, 0.0F, 0.0F, 1, 2, 1, 0.0F);
		this.frontPiece2 = new BlockTexturedModelRenderer(this, 0, 2, false);
		this.frontPiece2.setRotationPoint(0.0F, 2.0F, 0.0F);
		this.frontPiece2.addBox(0.0F, 0.0F, 0.0F, 2, 1, 1, 0.0F);
		this.left = new BlockTexturedModelRenderer(this, 6, 0, false);
		this.left.setRotationPoint(0.0F, 0.0F, 1.0F);
		this.left.addBox(0.0F, 0.0F, 0.0F, 1, 16, 9, 0.0F);
		this.frontPiece9 = new BlockTexturedModelRenderer(this, 13, 2, false);
		this.frontPiece9.setRotationPoint(13.0F, 2.0F, 0.0F);
		this.frontPiece9.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
		this.frontPiece16 = new BlockTexturedModelRenderer(this, 4, 15, false);
		this.frontPiece16.setRotationPoint(4.0F, 15.0F, 0.0F);
		this.frontPiece16.addBox(0.0F, 0.0F, 0.0F, 6, 1, 1, 0.0F);
		this.frontPiece5 = new BlockTexturedModelRenderer(this, 1, 12, false);
		this.frontPiece5.setRotationPoint(1.0F, 12.0F, 0.0F);
		this.frontPiece5.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
		this.top = new BlockTexturedModelRenderer(this, 1, 6, false);
		this.top.setRotationPoint(1.0F, 0.0F, 1.0F);
		this.top.addBox(0.0F, 0.0F, 0.0F, 14, 1, 9, 0.0F);
		this.frontPiece7 = new BlockTexturedModelRenderer(this, 4, 2, false);
		this.frontPiece7.setRotationPoint(4.0F, 2.0F, 0.0F);
		this.frontPiece7.addBox(0.0F, 0.0F, 0.0F, 8, 1, 1, 0.0F);
		this.frontPiece10 = new BlockTexturedModelRenderer(this, 14, 2, false);
		this.frontPiece10.setRotationPoint(14.0F, 2.0F, 0.0F);
		this.frontPiece10.addBox(0.0F, 0.0F, 0.0F, 2, 2, 1, 0.0F);
		this.frontPiece15 = new BlockTexturedModelRenderer(this, 9, 14, false);
		this.frontPiece15.setRotationPoint(9.0F, 14.0F, 0.0F);
		this.frontPiece15.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
		this.bottom = new BlockTexturedModelRenderer(this, 1, 1, false);
		this.bottom.setRotationPoint(1.0F, 15.0F, 1.0F);
		this.bottom.addBox(0.0F, 0.0F, 0.0F, 14, 1, 9, 0.0F);
		this.right = new BlockTexturedModelRenderer(this, 1, 0, false);
		this.right.setRotationPoint(15.0F, 0.0F, 1.0F);
		this.right.addBox(0.0F, 0.0F, 0.0F, 1, 16, 9, 0.0F);
		this.frontPiece4 = new BlockTexturedModelRenderer(this, 1, 6, false);
		this.frontPiece4.setRotationPoint(1.0F, 6.0F, 0.0F);
		this.frontPiece4.addBox(0.0F, 0.0F, 0.0F, 1, 4, 1, 0.0F);
		this.frontPiece1 = new BlockTexturedModelRenderer(this, 0, 0, true);
		this.frontPiece1.setRotationPoint(-8.0F, 0.0F, -8.0F);
		this.frontPiece1.addBox(0.0F, 0.0F, 0.0F, 16, 2, 1, 0.0F);
		this.frontPiece3 = new BlockTexturedModelRenderer(this, 0, 3, false);
		this.frontPiece3.setRotationPoint(0.0F, 3.0F, 0.0F);
		this.frontPiece3.addBox(0.0F, 0.0F, 0.0F, 1, 13, 1, 0.0F);
		this.frontPiece11 = new BlockTexturedModelRenderer(this, 15, 4, false);
		this.frontPiece11.setRotationPoint(15.0F, 4.0F, 0.0F);
		this.frontPiece11.addBox(0.0F, 0.0F, 0.0F, 1, 12, 1, 0.0F);
		this.frontPiece8 = new BlockTexturedModelRenderer(this, 5, 3, false);
		this.frontPiece8.setRotationPoint(5.0F, 3.0F, 0.0F);
		this.frontPiece8.addBox(0.0F, 0.0F, 0.0F, 4, 1, 1, 0.0F);
		this.frontPiece13 = new BlockTexturedModelRenderer(this, 12, 12, false);
		this.frontPiece13.setRotationPoint(12.0F, 12.0F, 0.0F);
		this.frontPiece13.addBox(0.0F, 0.0F, 0.0F, 3, 1, 1, 0.0F);
		this.frontPiece6 = new BlockTexturedModelRenderer(this, 1, 13, false);
		this.frontPiece6.setRotationPoint(1.0F, 13.0F, 0.0F);
		this.frontPiece6.addBox(0.0F, 0.0F, 0.0F, 2, 3, 1, 0.0F);
		this.frontPiece14 = new BlockTexturedModelRenderer(this, 10, 13, false);
		this.frontPiece14.setRotationPoint(10.0F, 13.0F, 0.0F);
		this.frontPiece14.addBox(0.0F, 0.0F, 0.0F, 5, 3, 1, 0.0F);
		this.back = new BlockTexturedModelRenderer(this, 1, 1, false);
		this.back.setRotationPoint(1.0F, 1.0F, 9.0F);
		this.back.addBox(0.0F, 0.0F, 0.0F, 14, 14, 1, 0.0F);
		this.window = new BlockTexturedModelRenderer(this, 0, 0, false);
		this.window.setRotationPoint(-8.0F, 0.0F, -8.0F);
		this.window.addBox(0.0F, 0.0F, 0.0F, 16, 16, 0, 0.0F);
		this.frontPiece1.addChild(this.frontPiece17);
		this.frontPiece1.addChild(this.frontPiece12);
		this.frontPiece1.addChild(this.frontPiece2);
		this.frontPiece1.addChild(this.left);
		this.frontPiece1.addChild(this.frontPiece9);
		this.frontPiece1.addChild(this.frontPiece16);
		this.frontPiece1.addChild(this.frontPiece5);
		this.frontPiece1.addChild(this.top);
		this.frontPiece1.addChild(this.frontPiece7);
		this.frontPiece1.addChild(this.frontPiece10);
		this.frontPiece1.addChild(this.frontPiece15);
		this.frontPiece1.addChild(this.bottom);
		this.frontPiece1.addChild(this.right);
		this.frontPiece1.addChild(this.frontPiece4);
		this.frontPiece1.addChild(this.frontPiece3);
		this.frontPiece1.addChild(this.frontPiece11);
		this.frontPiece1.addChild(this.frontPiece8);
		this.frontPiece1.addChild(this.frontPiece13);
		this.frontPiece1.addChild(this.frontPiece6);
		this.frontPiece1.addChild(this.frontPiece14);
		this.frontPiece1.addChild(this.back);

		//Don't render window normally
		this.window.showModel = false;
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
		this.frontPiece1.render(f5);
		this.window.render(f5);
	}

	/**
	 * This is a helper function from Tabula to set the rotation of model parts
	 */
	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}
