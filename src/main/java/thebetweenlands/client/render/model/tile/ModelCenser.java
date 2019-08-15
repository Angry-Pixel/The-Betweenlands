package thebetweenlands.client.render.model.tile;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * BLCenser - TripleHeadedSheep
 * Created using Tabula 7.0.1
 */
public class ModelCenser extends ModelBase {
    public ModelRenderer base;
    public ModelRenderer leg_front_left1;
    public ModelRenderer leg_front_right1;
    public ModelRenderer leg_back_left1;
    public ModelRenderer leg_back_right1;
    public ModelRenderer midpiece;
    public ModelRenderer leg_front_left2;
    public ModelRenderer leg_front_right2;
    public ModelRenderer leg_back_left2;
    public ModelRenderer leg_back_right2;
    public ModelRenderer toppiece;
    public ModelRenderer ornament_piece1;
    public ModelRenderer ornament_piece2;
    public ModelRenderer ornament_piece3;
    public ModelRenderer ornament_mouthpiece_upper;
    public ModelRenderer ornament_mouthpiece_lower;
    public ModelRenderer ornament_eyes;

    public ModelCenser() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.leg_front_left1 = new ModelRenderer(this, 0, 43);
        this.leg_front_left1.setRotationPoint(6.0F, 0.0F, -3.0F);
        this.leg_front_left1.addBox(-3.0F, -1.0F, -2.99F, 3, 2, 3, 0.0F);
        this.setRotateAngle(leg_front_left1, 0.0F, 0.0F, -0.18203784098300857F);
        this.ornament_piece1 = new ModelRenderer(this, 49, 0);
        this.ornament_piece1.setRotationPoint(3.0F, -2.0F, 0.0F);
        this.ornament_piece1.addBox(-2.0F, -1.0F, -1.5F, 2, 3, 3, 0.0F);
        this.setRotateAngle(ornament_piece1, 0.0F, 0.0F, 0.7285004297824331F);
        this.ornament_eyes = new ModelRenderer(this, 49, 31);
        this.ornament_eyes.setRotationPoint(-1.5F, 0.5F, 0.0F);
        this.ornament_eyes.addBox(0.0F, 0.0F, -2.0F, 1, 1, 4, 0.0F);
        this.ornament_piece2 = new ModelRenderer(this, 49, 7);
        this.ornament_piece2.setRotationPoint(-2.0F, -1.0F, -0.01F);
        this.ornament_piece2.addBox(-2.0F, 0.0F, -1.5F, 2, 3, 3, 0.0F);
        this.setRotateAngle(ornament_piece2, 0.0F, 0.0F, -0.36425021489121656F);
        this.toppiece = new ModelRenderer(this, 0, 32);
        this.toppiece.setRotationPoint(0.0F, -6.0F, 0.0F);
        this.toppiece.addBox(-4.0F, -2.0F, -4.0F, 8, 2, 8, 0.0F);
        this.setRotateAngle(toppiece, 0.0F, 0.045553093477052F, 0.0F);
        this.leg_back_left2 = new ModelRenderer(this, 13, 53);
        this.leg_back_left2.setRotationPoint(0.0F, 1.0F, 0.0F);
        this.leg_back_left2.addBox(-5.0F, 0.0F, 0.0F, 5, 2, 3, 0.0F);
        this.setRotateAngle(leg_back_left2, 0.0F, 0.0F, 0.18203784098300857F);
        this.ornament_mouthpiece_upper = new ModelRenderer(this, 49, 21);
        this.ornament_mouthpiece_upper.setRotationPoint(-2.0F, 1.0F, 0.0F);
        this.ornament_mouthpiece_upper.addBox(-2.0F, -1.0F, -1.5F, 2, 1, 3, 0.0F);
        this.ornament_piece3 = new ModelRenderer(this, 49, 14);
        this.ornament_piece3.setRotationPoint(-2.0F, 0.0F, -0.01F);
        this.ornament_piece3.addBox(-2.0F, 0.0F, -1.5F, 2, 3, 3, 0.0F);
        this.setRotateAngle(ornament_piece3, 0.0F, 0.0F, -0.36425021489121656F);
        this.ornament_mouthpiece_lower = new ModelRenderer(this, 49, 26);
        this.ornament_mouthpiece_lower.setRotationPoint(-2.0F, 3.0F, 0.0F);
        this.ornament_mouthpiece_lower.addBox(-2.0F, -1.0F, -1.5F, 2, 1, 3, 0.0F);
        this.setRotateAngle(ornament_mouthpiece_lower, 0.0F, 0.0F, 0.045553093477052F);
        this.leg_front_right2 = new ModelRenderer(this, 13, 48);
        this.leg_front_right2.setRotationPoint(0.0F, 1.0F, 0.0F);
        this.leg_front_right2.addBox(0.0F, 0.0F, -3.0F, 5, 2, 3, 0.0F);
        this.setRotateAngle(leg_front_right2, 0.0F, 0.0F, -0.18203784098300857F);
        this.base = new ModelRenderer(this, 0, 0);
        this.base.setRotationPoint(0.0F, 21.0F, 0.0F);
        this.base.addBox(-6.0F, -2.0F, -6.0F, 12, 2, 12, 0.0F);
        this.leg_front_left2 = new ModelRenderer(this, 13, 43);
        this.leg_front_left2.setRotationPoint(0.0F, 1.0F, 0.0F);
        this.leg_front_left2.addBox(-5.0F, 0.0F, -3.0F, 5, 2, 3, 0.0F);
        this.setRotateAngle(leg_front_left2, 0.0F, 0.0F, 0.18203784098300857F);
        this.leg_back_right2 = new ModelRenderer(this, 13, 58);
        this.leg_back_right2.setRotationPoint(0.0F, 1.0F, 0.0F);
        this.leg_back_right2.addBox(0.0F, 0.0F, 0.0F, 5, 2, 3, 0.0F);
        this.setRotateAngle(leg_back_right2, 0.0F, 0.0F, -0.18203784098300857F);
        this.leg_back_left1 = new ModelRenderer(this, 0, 53);
        this.leg_back_left1.setRotationPoint(6.0F, 0.0F, 3.0F);
        this.leg_back_left1.addBox(-3.0F, -1.0F, -0.01F, 3, 2, 3, 0.0F);
        this.setRotateAngle(leg_back_left1, 0.0F, 0.0F, -0.18203784098300857F);
        this.midpiece = new ModelRenderer(this, 0, 15);
        this.midpiece.setRotationPoint(0.0F, -2.0F, 0.0F);
        this.midpiece.addBox(-5.0F, -6.0F, -5.0F, 10, 6, 10, 0.0F);
        this.leg_back_right1 = new ModelRenderer(this, 0, 58);
        this.leg_back_right1.setRotationPoint(-6.0F, 0.0F, 3.0F);
        this.leg_back_right1.addBox(0.0F, -1.0F, -0.01F, 3, 2, 3, 0.0F);
        this.setRotateAngle(leg_back_right1, 0.0F, 0.0F, 0.18203784098300857F);
        this.leg_front_right1 = new ModelRenderer(this, 0, 48);
        this.leg_front_right1.setRotationPoint(-6.0F, 0.0F, -3.0F);
        this.leg_front_right1.addBox(0.0F, -1.0F, -2.99F, 3, 2, 3, 0.0F);
        this.setRotateAngle(leg_front_right1, 0.0F, 0.0F, 0.18203784098300857F);
        this.base.addChild(this.leg_front_left1);
        this.toppiece.addChild(this.ornament_piece1);
        this.ornament_piece3.addChild(this.ornament_eyes);
        this.ornament_piece1.addChild(this.ornament_piece2);
        this.midpiece.addChild(this.toppiece);
        this.leg_back_left1.addChild(this.leg_back_left2);
        this.ornament_piece3.addChild(this.ornament_mouthpiece_upper);
        this.ornament_piece2.addChild(this.ornament_piece3);
        this.ornament_piece3.addChild(this.ornament_mouthpiece_lower);
        this.leg_front_right1.addChild(this.leg_front_right2);
        this.leg_front_left1.addChild(this.leg_front_left2);
        this.leg_back_right1.addChild(this.leg_back_right2);
        this.base.addChild(this.leg_back_left1);
        this.base.addChild(this.midpiece);
        this.base.addChild(this.leg_back_right1);
        this.base.addChild(this.leg_front_right1);
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
