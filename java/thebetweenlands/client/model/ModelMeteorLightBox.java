package thebetweenlands.client.model;

import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.Tessellator;

public class ModelMeteorLightBox extends ModelBox {
	private PositionTextureVertex[] vertexPositions;

	private TexturedQuad[] quadList;

	private final float posX1;

	private final float posY1;

	private final float posZ1;

	private final float posX2;

	private final float posY2;

	private final float posZ2;

	private int type;

	public ModelMeteorLightBox(ModelRenderer renderer, int textureX, int textureY, float x, float y, float z, int width, int height, int depth, int type) {
		super(renderer, textureX, textureY, x, y, z, width, height, depth, 0);
		this.type = type;
		posX1 = x;
		posY1 = y;
		posZ1 = z;
		posX2 = x + width;
		posY2 = y + height;
		posZ2 = z + depth;
		vertexPositions = new PositionTextureVertex[8];
		quadList = new TexturedQuad[6];
		float x2 = x + width;
		float y2 = y + height;
		float z2 = z + depth;
		if (renderer.mirror) {
			float t = x2;
			x2 = x;
			x = t;
		}
		PositionTextureVertex positiontexturevertex7 = new PositionTextureVertex(x, y, z, 0.0F, 0.0F);
		PositionTextureVertex positiontexturevertex0 = new PositionTextureVertex(x2, y, z, 0.0F, 8.0F);
		PositionTextureVertex positiontexturevertex1 = new PositionTextureVertex(x2, y2, z, 8.0F, 8.0F);
		PositionTextureVertex positiontexturevertex2 = new PositionTextureVertex(x, y2, z, 8.0F, 0.0F);
		PositionTextureVertex positiontexturevertex3 = new PositionTextureVertex(x, y, z2, 0.0F, 0.0F);
		PositionTextureVertex positiontexturevertex4 = new PositionTextureVertex(x2, y, z2, 0.0F, 8.0F);
		PositionTextureVertex positiontexturevertex5 = new PositionTextureVertex(x2, y2, z2, 8.0F, 8.0F);
		PositionTextureVertex positiontexturevertex6 = new PositionTextureVertex(x, y2, z2, 8.0F, 0.0F);
		vertexPositions[0] = positiontexturevertex7;
		vertexPositions[1] = positiontexturevertex0;
		vertexPositions[2] = positiontexturevertex1;
		vertexPositions[3] = positiontexturevertex2;
		vertexPositions[4] = positiontexturevertex3;
		vertexPositions[5] = positiontexturevertex4;
		vertexPositions[6] = positiontexturevertex5;
		vertexPositions[7] = positiontexturevertex6;
		quadList[0] = new TexturedQuad(new PositionTextureVertex[]{positiontexturevertex4, positiontexturevertex0, positiontexturevertex1, positiontexturevertex5}, textureX + depth + width, textureY + depth, textureX + depth + width + depth, textureY + depth + height, renderer.textureWidth, renderer.textureHeight);
		quadList[1] = new TexturedQuad(new PositionTextureVertex[]{positiontexturevertex7, positiontexturevertex3, positiontexturevertex6, positiontexturevertex2}, textureX, textureY + depth, textureX + depth, textureY + depth + height, renderer.textureWidth, renderer.textureHeight);
		quadList[2] = new TexturedQuad(new PositionTextureVertex[]{positiontexturevertex4, positiontexturevertex3, positiontexturevertex7, positiontexturevertex0}, textureX + depth, textureY, textureX + depth + width, textureY + depth, renderer.textureWidth, renderer.textureHeight);
		quadList[3] = new TexturedQuad(new PositionTextureVertex[]{positiontexturevertex1, positiontexturevertex2, positiontexturevertex6, positiontexturevertex5}, textureX + depth + width, textureY + depth, textureX + depth + width + width, textureY, renderer.textureWidth, renderer.textureHeight);
		quadList[4] = new TexturedQuad(new PositionTextureVertex[]{positiontexturevertex0, positiontexturevertex7, positiontexturevertex2, positiontexturevertex1}, textureX + depth, textureY + depth, textureX + depth + width, textureY + depth + height, renderer.textureWidth, renderer.textureHeight);
		quadList[5] = new TexturedQuad(new PositionTextureVertex[]{positiontexturevertex3, positiontexturevertex4, positiontexturevertex5, positiontexturevertex6}, textureX + depth + width + depth, textureY + depth, textureX + depth + width + depth + width, textureY + depth + height, renderer.textureWidth, renderer.textureHeight);
		if (renderer.mirror) {
			for (int i = 0; i < quadList.length; i++) {
				quadList[i].flipFace();
			}
		}
	}

	@Override
	public void render(Tessellator renderer, float scale) {
		for (int i = 0; i < quadList.length; i++) {
			if (type != 1 && i == 2 || type != 0 && i == 3) {
				continue;
			}
			quadList[i].draw(renderer, scale);
		}
	}
}
