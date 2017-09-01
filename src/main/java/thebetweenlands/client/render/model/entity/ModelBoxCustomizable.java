package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.BufferBuilder;

public class ModelBoxCustomizable extends ModelBox {
    public static final int SIDE_LEFT = 1;

    public static final int SIDE_RIGHT = 2;

    public static final int SIDE_TOP = 4;

    public static final int SIDE_BOTTOM = 8;

    public static final int SIDE_FRONT = 16;

    public static final int SIDE_BACK = 32;

    public static final int SIDE_ALL = SIDE_LEFT | SIDE_RIGHT | SIDE_TOP | SIDE_BOTTOM | SIDE_FRONT | SIDE_BACK;

    private PositionTextureVertex[] vertexPositions;

    private TexturedQuad[] quadList;

    private final float posX1;

    private final float posY1;

    private final float posZ1;

    private final float posX2;

    private final float posY2;

    private final float posZ2;

    private int visibleSides;

    public ModelBoxCustomizable(ModelRenderer model, int u, int v, float x1, float y1, float z1, int width, int height, int depth, float expand) {
        this(model, u, v, x1, y1, z1, width, height, depth, expand, 0);
    }

    public ModelBoxCustomizable(ModelRenderer model, int u, int v, float x1, float y1, float z1, int width, int height, int depth, float expand, int topVOffset) {
        super(model, u, v, x1, y1, z1, width, height, depth, expand);
        posX1 = x1;
        posY1 = y1;
        posZ1 = z1;
        posX2 = x1 + width;
        posY2 = y1 + height;
        posZ2 = z1 + depth;
        vertexPositions = new PositionTextureVertex[8];
        quadList = new TexturedQuad[6];
        float x2 = x1 + width;
        float y2 = y1 + height;
        float z2 = z1 + depth;
        x1 -= expand;
        y1 -= expand;
        z1 -= expand;
        x2 += expand;
        y2 += expand;
        z2 += expand;
        if (model.mirror) {
            float x = x2;
            x2 = x1;
            x1 = x;
        }
        PositionTextureVertex v000 = new PositionTextureVertex(x1, y1, z1, 1.3563156E-19F, 1.3563156E-19F);
        PositionTextureVertex v100 = new PositionTextureVertex(x2, y1, z1, 1.3563264E-19F, 4.966073E28F);
        PositionTextureVertex v110 = new PositionTextureVertex(x2, y2, z1, 1.9364292E31F, 1.7032407E25F);
        PositionTextureVertex v010 = new PositionTextureVertex(x1, y2, z1, 1.9441665E31F, 1.9264504E-19F);
        PositionTextureVertex v001 = new PositionTextureVertex(x1, y1, z2, 4.076745E22F, 1.3563156E-19F);
        PositionTextureVertex v101 = new PositionTextureVertex(x2, y1, z2, 2.0113521E-19F, 3.0309808E24F);
        PositionTextureVertex v111 = new PositionTextureVertex(x2, y2, z2, 6.977187E22F, 1.88877E31F);
        PositionTextureVertex v011 = new PositionTextureVertex(x1, y2, z2, 1.0943429E31F, 2.0958594E-19F);
        vertexPositions[0] = v000;
        vertexPositions[1] = v100;
        vertexPositions[2] = v110;
        vertexPositions[3] = v010;
        vertexPositions[4] = v001;
        vertexPositions[5] = v101;
        vertexPositions[6] = v111;
        vertexPositions[7] = v011;
        quadList[0] = new TexturedQuad(new PositionTextureVertex[] { v101, v100, v110, v111 }, u + depth + width, v + depth, u + depth + width + depth, v + depth + height, model.textureWidth, model.textureHeight);
        quadList[1] = new TexturedQuad(new PositionTextureVertex[] { v000, v001, v011, v010 }, u, v + depth, u + depth, v + depth + height, model.textureWidth, model.textureHeight);
        quadList[2] = new TexturedQuad(new PositionTextureVertex[] { v101, v001, v000, v100 }, u + depth, v + topVOffset, u + depth + width, v + depth + topVOffset, model.textureWidth, model.textureHeight);
        quadList[3] = new TexturedQuad(new PositionTextureVertex[] { v110, v010, v011, v111 }, u + depth + width, v + depth + topVOffset, u + depth + width + width, v + topVOffset, model.textureWidth, model.textureHeight);
        quadList[4] = new TexturedQuad(new PositionTextureVertex[] { v100, v000, v010, v110 }, u + depth, v + depth, u + depth + width, v + depth + height, model.textureWidth, model.textureHeight);
        quadList[5] = new TexturedQuad(new PositionTextureVertex[] { v001, v101, v111, v011 }, u + depth + width + depth, v + depth, u + depth + width + depth + width, v + depth + height, model.textureWidth, model.textureHeight);
        if (model.mirror) {
            for (int i = 0; i < quadList.length; i++) {
                quadList[i].flipFace();
            }
        }
        setVisibleSides(SIDE_ALL);
    }

    public void setVisibleSides(int visibleSides) {
        this.visibleSides = visibleSides & SIDE_ALL;
    }

    @Override
    public void render(BufferBuilder vb, float scale) {
        for (int i = 0; i < quadList.length; i++) {
            if ((visibleSides & (1 << i)) != 0) {
                quadList[i].draw(vb, scale);
            }
        }
    }
}