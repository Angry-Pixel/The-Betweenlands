package thebetweenlands.client.render.model.baked.modelbase;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * BLLakeCavernStatuette2 - TripleHeadedSheep
 * Created using Tabula 7.0.1
 */
public class ModelSimulacrumLakeCavern2 extends ModelBase {
    public ModelRenderer base;
    public ModelRenderer stone_mid;
    public ModelRenderer stone_left;
    public ModelRenderer stone_right;
    public ModelRenderer top_left;
    public ModelRenderer top_right;

    public ModelSimulacrumLakeCavern2() {
        this.textureWidth = 32;
        this.textureHeight = 32;
        this.stone_right = new ModelRenderer(this, 18, 7);
        this.stone_right.setRotationPoint(-1.0F, 0.0F, -1.0F);
        this.stone_right.addBox(-2.0F, -14.0F, 0.0F, 2, 14, 2, 0.0F);
        this.setRotateAngle(stone_right, 0.0F, 0.22759093446006054F, 0.0F);
        this.stone_mid = new ModelRenderer(this, 0, 7);
        this.stone_mid.setRotationPoint(0.0F, -2.0F, -0.25F);
        this.stone_mid.addBox(-1.0F, -14.0F, -1.0F, 2, 14, 2, 0.0F);
        this.top_right = new ModelRenderer(this, 18, 24);
        this.top_right.setRotationPoint(-2.0F, -14.0F, 0.0F);
        this.top_right.addBox(-1.0F, 0.0F, 0.0F, 1, 4, 2, 0.0F);
        this.base = new ModelRenderer(this, 0, 0);
        this.base.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.base.addBox(-5.0F, -2.0F, -2.0F, 10, 2, 4, 0.0F);
        this.stone_left = new ModelRenderer(this, 9, 7);
        this.stone_left.setRotationPoint(1.0F, 0.0F, -1.0F);
        this.stone_left.addBox(0.0F, -14.0F, 0.0F, 2, 14, 2, 0.0F);
        this.setRotateAngle(stone_left, 0.0F, -0.22759093446006054F, 0.0F);
        this.top_left = new ModelRenderer(this, 9, 24);
        this.top_left.setRotationPoint(2.0F, -14.0F, 0.0F);
        this.top_left.addBox(0.0F, 0.0F, 0.0F, 1, 4, 2, 0.0F);
        this.stone_mid.addChild(this.stone_right);
        this.base.addChild(this.stone_mid);
        this.stone_right.addChild(this.top_right);
        this.stone_mid.addChild(this.stone_left);
        this.stone_left.addChild(this.top_left);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.base.render(f5);
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
