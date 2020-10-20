package thebetweenlands.client.render.model.baked.modelbase;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * BLDeepmanStatuette1 - TripleHeadedSheep
 * Created using Tabula 7.0.1
 */
public class ModelSimulacrumDeepman1 extends ModelBase {
    public ModelRenderer body_base;
    public ModelRenderer body_face;
    public ModelRenderer shoulders;
    public ModelRenderer arms_back1a;
    public ModelRenderer arms_front1a;
    public ModelRenderer arms_back1b;
    public ModelRenderer arms_front1b;

    public ModelSimulacrumDeepman1() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.body_base = new ModelRenderer(this, 0, 0);
        this.body_base.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.body_base.addBox(-4.5F, -7.0F, -4.5F, 9, 7, 9, 0.0F);
        this.shoulders = new ModelRenderer(this, 0, 49);
        this.shoulders.setRotationPoint(0.0F, -2.5F, 0.0F);
        this.shoulders.addBox(-5.005F, -1.0F, -1.0F, 10, 1, 2, 0.0F);
        this.body_face = new ModelRenderer(this, 0, 17);
        this.body_face.setRotationPoint(0.0F, -7.0F, 0.0F);
        this.body_face.addBox(-4.0F, -7.0F, -4.0F, 8, 7, 8, 0.0F);
        this.arms_back1b = new ModelRenderer(this, 27, 41);
        this.arms_back1b.setRotationPoint(0.0F, 4.0F, -3.0F);
        this.arms_back1b.addBox(-5.0F, 0.0F, 0.0F, 10, 4, 3, 0.0F);
        this.setRotateAngle(arms_back1b, 0.5462880558742251F, 0.0F, 0.0F);
        this.arms_front1a = new ModelRenderer(this, 0, 33);
        this.arms_front1a.setRotationPoint(0.0F, -1.0F, -1.0F);
        this.arms_front1a.addBox(-5.01F, 0.0F, 0.0F, 10, 4, 3, 0.0F);
        this.setRotateAngle(arms_front1a, -0.6373942428283291F, 0.0F, 0.0F);
        this.arms_front1b = new ModelRenderer(this, 27, 33);
        this.arms_front1b.setRotationPoint(0.0F, 4.0F, 3.0F);
        this.arms_front1b.addBox(-5.0F, 0.0F, -3.0F, 10, 4, 3, 0.0F);
        this.setRotateAngle(arms_front1b, -0.5462880558742251F, 0.0F, 0.0F);
        this.arms_back1a = new ModelRenderer(this, 0, 41);
        this.arms_back1a.setRotationPoint(0.0F, -1.0F, 1.0F);
        this.arms_back1a.addBox(-5.0F, 0.0F, -3.0F, 10, 4, 3, 0.0F);
        this.setRotateAngle(arms_back1a, 0.6373942428283291F, 0.0F, 0.0F);
        this.body_face.addChild(this.shoulders);
        this.body_base.addChild(this.body_face);
        this.arms_back1a.addChild(this.arms_back1b);
        this.shoulders.addChild(this.arms_front1a);
        this.arms_front1a.addChild(this.arms_front1b);
        this.shoulders.addChild(this.arms_back1a);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.body_base.render(f5);
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
