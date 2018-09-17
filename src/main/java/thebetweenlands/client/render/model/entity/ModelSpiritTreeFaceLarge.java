package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * BLSpiritTree - TripleHeadedSheep
 * Created using Tabula 7.0.0
 */
public class ModelSpiritTreeFaceLarge extends ModelBase {
    public ModelRenderer headbase;
    public ModelRenderer nose1;
    public ModelRenderer head_lower;
    public ModelRenderer cheekthingy_left;
    public ModelRenderer cheekthingy_right;
    public ModelRenderer cheekthingy_leftupper;
    public ModelRenderer cheekthingy_rightupper;
    public ModelRenderer nose2;
    public ModelRenderer brow_left;
    public ModelRenderer brow_right;
    public ModelRenderer browpiece_mid;
    public ModelRenderer brow_leftpiece_a;
    public ModelRenderer brow_leftpiece_b;
    public ModelRenderer browpiece_left;
    public ModelRenderer sidepiece_left1;
    public ModelRenderer brow_leftpiece_c;
    public ModelRenderer sidepiece_left2;
    public ModelRenderer brow_rightpiece_a;
    public ModelRenderer brow_rightpiece_b;
    public ModelRenderer browpiece_right;
    public ModelRenderer sidepiece_right_1;
    public ModelRenderer brow_rightpiece_c;
    public ModelRenderer sidepiece_right_2;

    public ModelSpiritTreeFaceLarge() {
        this.textureWidth = 128;
        this.textureHeight = 128;
        this.cheekthingy_right = new ModelRenderer(this, 9, 55);
        this.cheekthingy_right.setRotationPoint(-3.0F, 4.0F, -3.0F);
        this.cheekthingy_right.addBox(-2.0F, 0.0F, 0.0F, 2, 10, 2, 0.0F);
        this.brow_leftpiece_b = new ModelRenderer(this, 43, 22);
        this.brow_leftpiece_b.setRotationPoint(11.0F, 0.0F, 0.0F);
        this.brow_leftpiece_b.addBox(-4.0F, 0.0F, 0.0F, 5, 5, 5, 0.0F);
        this.setRotateAngle(brow_leftpiece_b, 0.36425021489121656F, 0.0F, 0.0F);
        this.sidepiece_right_1 = new ModelRenderer(this, 82, 55);
        this.sidepiece_right_1.setRotationPoint(-5.98F, 0.0F, 5.0F);
        this.sidepiece_right_1.addBox(0.0F, -4.0F, 0.0F, 4, 4, 15, 0.0F);
        this.setRotateAngle(sidepiece_right_1, 0.27314402793711257F, 0.0F, 0.0F);
        this.brow_leftpiece_a = new ModelRenderer(this, 43, 11);
        this.brow_leftpiece_a.setRotationPoint(12.0F, 0.0F, -0.02F);
        this.brow_leftpiece_a.addBox(0.0F, -5.0F, 0.0F, 6, 5, 5, 0.0F);
        this.setRotateAngle(brow_leftpiece_a, 0.0F, 0.0F, -0.5918411493512771F);
        this.browpiece_left = new ModelRenderer(this, 68, 45);
        this.browpiece_left.setRotationPoint(4.0F, -5.0F, 2.02F);
        this.browpiece_left.addBox(0.0F, -4.0F, -2.0F, 4, 4, 4, 0.0F);
        this.setRotateAngle(browpiece_left, 0.0F, 0.0F, 0.4553564018453205F);
        this.browpiece_mid = new ModelRenderer(this, 43, 45);
        this.browpiece_mid.setRotationPoint(0.0F, -10.0F, 2.0F);
        this.browpiece_mid.addBox(-4.0F, -5.0F, -2.0F, 8, 5, 4, 0.0F);
        this.sidepiece_left1 = new ModelRenderer(this, 43, 55);
        this.sidepiece_left1.setRotationPoint(5.98F, 0.0F, 5.0F);
        this.sidepiece_left1.addBox(-4.0F, -4.0F, 0.0F, 4, 4, 15, 0.0F);
        this.setRotateAngle(sidepiece_left1, 0.27314402793711257F, 0.0F, 0.0F);
        this.brow_leftpiece_c = new ModelRenderer(this, 43, 33);
        this.brow_leftpiece_c.setRotationPoint(-4.0F, 5.0F, -0.02F);
        this.brow_leftpiece_c.addBox(0.0F, 0.0F, 0.0F, 5, 6, 5, 0.0F);
        this.setRotateAngle(brow_leftpiece_c, 0.0F, 0.0F, -0.22759093446006054F);
        this.brow_rightpiece_a = new ModelRenderer(this, 78, 11);
        this.brow_rightpiece_a.setRotationPoint(-12.0F, 0.0F, -0.02F);
        this.brow_rightpiece_a.addBox(-6.0F, -5.0F, 0.0F, 6, 5, 5, 0.0F);
        this.setRotateAngle(brow_rightpiece_a, 0.0F, 0.0F, 0.5918411493512771F);
        this.brow_rightpiece_c = new ModelRenderer(this, 78, 33);
        this.brow_rightpiece_c.setRotationPoint(4.0F, 5.0F, -0.02F);
        this.brow_rightpiece_c.addBox(-5.0F, 0.0F, 0.0F, 5, 6, 5, 0.0F);
        this.setRotateAngle(brow_rightpiece_c, 0.0F, 0.0F, 0.22759093446006054F);
        this.brow_right = new ModelRenderer(this, 78, 0);
        this.brow_right.setRotationPoint(-3.0F, -5.0F, 0.02F);
        this.brow_right.addBox(-12.0F, -5.0F, 0.0F, 12, 5, 5, 0.0F);
        this.setRotateAngle(brow_right, 0.0F, 0.0F, 0.22759093446006054F);
        this.nose2 = new ModelRenderer(this, 0, 37);
        this.nose2.setRotationPoint(0.0F, -5.0F, -2.0F);
        this.nose2.addBox(-3.0F, -10.0F, 0.0F, 6, 10, 5, 0.0F);
        this.setRotateAngle(nose2, -0.18203784098300857F, 0.0F, 0.0F);
        this.sidepiece_left2 = new ModelRenderer(this, 43, 75);
        this.sidepiece_left2.setRotationPoint(5.0F, 5.98F, 5.0F);
        this.sidepiece_left2.addBox(-4.0F, -4.0F, 0.0F, 4, 4, 5, 0.0F);
        this.setRotateAngle(sidepiece_left2, 0.0F, -0.31869712141416456F, 0.0F);
        this.brow_left = new ModelRenderer(this, 43, 0);
        this.brow_left.setRotationPoint(3.0F, -5.0F, 0.02F);
        this.brow_left.addBox(0.0F, -5.0F, 0.0F, 12, 5, 5, 0.0F);
        this.setRotateAngle(brow_left, 0.0F, 0.0F, -0.22759093446006054F);
        this.headbase = new ModelRenderer(this, 0, 0);
        this.headbase.setRotationPoint(0.0F, 8.5F, 0.0F);
        this.headbase.addBox(-9.0F, -8.0F, -3.0F, 18, 12, 3, 0.0F);
        this.brow_rightpiece_b = new ModelRenderer(this, 78, 22);
        this.brow_rightpiece_b.setRotationPoint(-11.0F, 0.0F, 0.0F);
        this.brow_rightpiece_b.addBox(-1.0F, 0.0F, 0.0F, 5, 5, 5, 0.0F);
        this.setRotateAngle(brow_rightpiece_b, 0.36425021489121656F, 0.0F, 0.0F);
        this.nose1 = new ModelRenderer(this, 0, 28);
        this.nose1.setRotationPoint(0.0F, 4.0F, -3.0F);
        this.nose1.addBox(-3.0F, -5.0F, -2.0F, 6, 5, 3, 0.0F);
        this.setRotateAngle(nose1, 0.045553093477052F, 0.0F, 0.0F);
        this.browpiece_right = new ModelRenderer(this, 85, 45);
        this.browpiece_right.setRotationPoint(-4.0F, -5.0F, 2.02F);
        this.browpiece_right.addBox(-4.0F, -4.0F, -2.0F, 4, 4, 4, 0.0F);
        this.setRotateAngle(browpiece_right, 0.0F, 0.0F, -0.4553564018453205F);
        this.cheekthingy_rightupper = new ModelRenderer(this, 9, 68);
        this.cheekthingy_rightupper.setRotationPoint(-9.0F, -3.0F, -3.0F);
        this.cheekthingy_rightupper.addBox(-1.0F, 0.0F, 0.0F, 1, 2, 2, 0.0F);
        this.setRotateAngle(cheekthingy_rightupper, 0.0F, 0.091106186954104F, 0.0F);
        this.sidepiece_right_2 = new ModelRenderer(this, 82, 75);
        this.sidepiece_right_2.setRotationPoint(-5.0F, 5.98F, 5.0F);
        this.sidepiece_right_2.addBox(0.0F, -4.0F, 0.0F, 4, 4, 5, 0.0F);
        this.setRotateAngle(sidepiece_right_2, 0.0F, 0.31869712141416456F, 0.0F);
        this.cheekthingy_left = new ModelRenderer(this, 0, 55);
        this.cheekthingy_left.setRotationPoint(3.0F, 4.0F, -3.0F);
        this.cheekthingy_left.addBox(0.0F, 0.0F, 0.0F, 2, 10, 2, 0.0F);
        this.cheekthingy_leftupper = new ModelRenderer(this, 0, 68);
        this.cheekthingy_leftupper.setRotationPoint(9.0F, -3.0F, -3.0F);
        this.cheekthingy_leftupper.addBox(0.0F, 0.0F, 0.0F, 1, 2, 2, 0.0F);
        this.setRotateAngle(cheekthingy_leftupper, 0.0F, -0.091106186954104F, 0.0F);
        this.head_lower = new ModelRenderer(this, 0, 16);
        this.head_lower.setRotationPoint(0.0F, 4.0F, -3.0F);
        this.head_lower.addBox(-7.0F, 0.0F, 0.0F, 14, 8, 3, 0.0F);
        this.setRotateAngle(head_lower, 0.091106186954104F, 0.0F, 0.0F);
        this.headbase.addChild(this.cheekthingy_right);
        this.brow_left.addChild(this.brow_leftpiece_b);
        this.brow_rightpiece_a.addChild(this.sidepiece_right_1);
        this.brow_left.addChild(this.brow_leftpiece_a);
        this.brow_left.addChild(this.browpiece_left);
        this.nose2.addChild(this.browpiece_mid);
        this.brow_leftpiece_a.addChild(this.sidepiece_left1);
        this.brow_leftpiece_b.addChild(this.brow_leftpiece_c);
        this.brow_right.addChild(this.brow_rightpiece_a);
        this.brow_rightpiece_b.addChild(this.brow_rightpiece_c);
        this.nose2.addChild(this.brow_right);
        this.nose1.addChild(this.nose2);
        this.brow_leftpiece_c.addChild(this.sidepiece_left2);
        this.nose2.addChild(this.brow_left);
        this.brow_right.addChild(this.brow_rightpiece_b);
        this.headbase.addChild(this.nose1);
        this.brow_right.addChild(this.browpiece_right);
        this.headbase.addChild(this.cheekthingy_rightupper);
        this.brow_rightpiece_c.addChild(this.sidepiece_right_2);
        this.headbase.addChild(this.cheekthingy_left);
        this.headbase.addChild(this.cheekthingy_leftupper);
        this.headbase.addChild(this.head_lower);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.headbase.render(f5);
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
