package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * BLSpiritTreeSmoll - TripleHeadedSheep
 * Created using Tabula 7.0.0
 */
public class ModelSpiritTreeFaceSmall1 extends ModelBase {
    public ModelRenderer head_base;
    public ModelRenderer head2;
    public ModelRenderer nose1;
    public ModelRenderer nose2;
    public ModelRenderer brow_left;
    public ModelRenderer brow_right;
    public ModelRenderer brow_leftpiece_1;
    public ModelRenderer sidethingy_1;
    public ModelRenderer brow_rightpiece_1;
    public ModelRenderer sidethingy_2;

    public ModelSpiritTreeFaceSmall1() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.nose1 = new ModelRenderer(this, 0, 18);
        this.nose1.setRotationPoint(0.0F, 3.0F, -1.0F);
        this.nose1.addBox(-1.5F, -3.0F, -2.0F, 3, 3, 2, 0.0F);
        this.setRotateAngle(nose1, 0.045553093477052F, 0.0F, 0.0F);
        this.sidethingy_2 = new ModelRenderer(this, 44, 14);
        this.sidethingy_2.setRotationPoint(-2.98F, 0.0F, 3.0F);
        this.sidethingy_2.addBox(0.0F, -3.0F, 0.0F, 3, 3, 6, 0.0F);
        this.setRotateAngle(sidethingy_2, 0.18203784098300857F, 0.0F, 0.0F);
        this.head_base = new ModelRenderer(this, 0, 0);
        this.head_base.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.head_base.addBox(-5.0F, -4.0F, -2.0F, 10, 8, 2, 0.0F);
        this.head2 = new ModelRenderer(this, 0, 11);
        this.head2.setRotationPoint(0.0F, 3.0F, -2.0F);
        this.head2.addBox(-3.0F, 0.0F, 0.0F, 6, 4, 2, 0.0F);
        this.setRotateAngle(head2, 0.091106186954104F, 0.0F, 0.0F);
        this.brow_leftpiece_1 = new ModelRenderer(this, 25, 7);
        this.brow_leftpiece_1.setRotationPoint(5.0F, 0.0F, -0.02F);
        this.brow_leftpiece_1.addBox(0.0F, -3.0F, 0.0F, 3, 3, 3, 0.0F);
        this.setRotateAngle(brow_leftpiece_1, 0.0F, 0.0F, -0.6373942428283291F);
        this.sidethingy_1 = new ModelRenderer(this, 25, 14);
        this.sidethingy_1.setRotationPoint(2.98F, 0.0F, 3.0F);
        this.sidethingy_1.addBox(-3.0F, -3.0F, 0.0F, 3, 3, 6, 0.0F);
        this.setRotateAngle(sidethingy_1, 0.18203784098300857F, 0.0F, 0.0F);
        this.nose2 = new ModelRenderer(this, 0, 24);
        this.nose2.setRotationPoint(0.0F, -3.0F, -2.0F);
        this.nose2.addBox(-1.5F, -6.0F, 0.0F, 3, 6, 3, 0.0F);
        this.setRotateAngle(nose2, -0.18203784098300857F, 0.0F, 0.0F);
        this.brow_left = new ModelRenderer(this, 25, 0);
        this.brow_left.setRotationPoint(1.5F, -3.0F, 0.02F);
        this.brow_left.addBox(0.0F, -3.0F, 0.0F, 5, 3, 3, 0.0F);
        this.setRotateAngle(brow_left, 0.0F, 0.0F, -0.091106186954104F);
        this.brow_right = new ModelRenderer(this, 42, 0);
        this.brow_right.setRotationPoint(-1.5F, -3.0F, 0.02F);
        this.brow_right.addBox(-5.0F, -3.0F, 0.0F, 5, 3, 3, 0.0F);
        this.setRotateAngle(brow_right, 0.0F, 0.0F, 0.091106186954104F);
        this.brow_rightpiece_1 = new ModelRenderer(this, 42, 7);
        this.brow_rightpiece_1.setRotationPoint(-5.0F, 0.0F, -0.02F);
        this.brow_rightpiece_1.addBox(-3.0F, -3.0F, 0.0F, 3, 3, 3, 0.0F);
        this.setRotateAngle(brow_rightpiece_1, 0.0F, 0.0F, 0.6373942428283291F);
        this.head_base.addChild(this.nose1);
        this.brow_rightpiece_1.addChild(this.sidethingy_2);
        this.head_base.addChild(this.head2);
        this.brow_left.addChild(this.brow_leftpiece_1);
        this.brow_leftpiece_1.addChild(this.sidethingy_1);
        this.nose1.addChild(this.nose2);
        this.nose2.addChild(this.brow_left);
        this.nose2.addChild(this.brow_right);
        this.brow_right.addChild(this.brow_rightpiece_1);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.head_base.render(f5);
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
