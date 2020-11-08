package thebetweenlands.client.render.model.baked.modelbase;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * BLDeepmanStatuette2 - TripleHeadedSheep
 * Created using Tabula 7.0.1
 */
public class ModelSimulacrumDeepman2 extends ModelBase {
    public ModelRenderer body_base;
    public ModelRenderer body_top;
    public ModelRenderer arms1a;
    public ModelRenderer rope1a;
    public ModelRenderer arms1b;
    public ModelRenderer rope1b;
    public ModelRenderer paper1;
    public ModelRenderer paper2;
    public ModelRenderer paper3;
    public ModelRenderer paper4;
    public ModelRenderer rope1c;
    public ModelRenderer paper5;
    public ModelRenderer paper6;
    public ModelRenderer paper7;
    public ModelRenderer paper8;

    public ModelSimulacrumDeepman2() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.paper2 = new ModelRenderer(this, 30, 1);
        this.paper2.setRotationPoint(-0.3F, 0.0F, 1.2F);
        this.paper2.addBox(0.0F, 0.0F, 0.0F, 0, 3, 2, 0.0F);
        this.setRotateAngle(paper2, -0.4553564018453205F, 0.27314402793711257F, 0.5009094953223726F);
        this.paper8 = new ModelRenderer(this, 30, 28);
        this.paper8.setRotationPoint(0.5F, -0.5F, 3.7F);
        this.paper8.addBox(0.0F, 0.0F, 0.0F, 2, 3, 0, 0.0F);
        this.setRotateAngle(paper8, 0.31869712141416456F, 0.0F, 1.0471975511965976F);
        this.paper4 = new ModelRenderer(this, 30, 12);
        this.paper4.setRotationPoint(2.0F, 0.0F, -3.7F);
        this.paper4.addBox(0.0F, 0.0F, 0.0F, 3, 2, 0, 0.0F);
        this.setRotateAngle(paper4, -0.4553564018453205F, 0.4553564018453205F, 1.0016444577195458F);
        this.rope1b = new ModelRenderer(this, 40, 10);
        this.rope1b.setRotationPoint(2.5F, 0.5F, 0.0F);
        this.rope1b.addBox(0.0F, -1.0F, -4.0F, 3, 1, 8, 0.0F);
        this.setRotateAngle(rope1b, 0.0F, 0.0F, -0.18203784098300857F);
        this.arms1a = new ModelRenderer(this, 0, 32);
        this.arms1a.setRotationPoint(0.0F, -2.0F, 4.0F);
        this.arms1a.addBox(-4.0F, 0.0F, 0.0F, 8, 4, 3, 0.0F);
        this.setRotateAngle(arms1a, -0.7285004297824331F, 0.0F, 0.0F);
        this.rope1a = new ModelRenderer(this, 40, 0);
        this.rope1a.setRotationPoint(-3.7F, 0.9F, 3.5F);
        this.rope1a.addBox(-0.5F, -0.5F, -4.0F, 3, 1, 8, 0.0F);
        this.setRotateAngle(rope1a, 0.0F, 0.0F, -0.18203784098300857F);
        this.paper1 = new ModelRenderer(this, 30, 0);
        this.paper1.setRotationPoint(1.5F, 0.0F, 3.7F);
        this.paper1.addBox(0.0F, 0.0F, 0.0F, 2, 3, 0, 0.0F);
        this.setRotateAngle(paper1, 0.18203784098300857F, -0.091106186954104F, 0.5918411493512771F);
        this.paper6 = new ModelRenderer(this, 30, 17);
        this.paper6.setRotationPoint(2.7F, -0.5F, -2.5F);
        this.paper6.addBox(0.0F, 0.0F, 0.0F, 0, 2, 3, 0.0F);
        this.setRotateAngle(paper6, -0.9560913642424937F, 0.0F, 0.36425021489121656F);
        this.arms1b = new ModelRenderer(this, 0, 40);
        this.arms1b.setRotationPoint(0.0F, 4.0F, 3.0F);
        this.arms1b.addBox(-4.01F, 0.0F, -3.0F, 8, 4, 3, 0.0F);
        this.setRotateAngle(arms1b, -0.36425021489121656F, 0.0F, 0.0F);
        this.rope1c = new ModelRenderer(this, 40, 20);
        this.rope1c.setRotationPoint(3.0F, 0.0F, 0.0F);
        this.rope1c.addBox(0.0F, -1.0F, -4.0F, 3, 1, 8, 0.0F);
        this.setRotateAngle(rope1c, 0.0F, 0.0F, -0.18203784098300857F);
        this.body_top = new ModelRenderer(this, 0, 16);
        this.body_top.setRotationPoint(0.0F, -8.0F, -3.5F);
        this.body_top.addBox(-3.5F, -8.0F, 0.0F, 7, 8, 7, 0.0F);
        this.setRotateAngle(body_top, -0.091106186954104F, 0.0F, 0.0F);
        this.body_base = new ModelRenderer(this, 0, 0);
        this.body_base.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.body_base.addBox(-3.5F, -8.0F, -3.5F, 7, 8, 7, 0.0F);
        this.paper7 = new ModelRenderer(this, 30, 21);
        this.paper7.setRotationPoint(2.7F, -0.5F, 2.0F);
        this.paper7.addBox(0.0F, 0.0F, 0.0F, 0, 2, 3, 0.0F);
        this.setRotateAngle(paper7, -0.9560913642424937F, -0.22759093446006054F, 0.27314402793711257F);
        this.paper3 = new ModelRenderer(this, 30, 5);
        this.paper3.setRotationPoint(-0.4F, 0.0F, -2.5F);
        this.paper3.addBox(0.0F, 0.0F, 0.0F, 0, 3, 2, 0.0F);
        this.setRotateAngle(paper3, -0.5462880558742251F, -0.31869712141416456F, 0.36425021489121656F);
        this.paper5 = new ModelRenderer(this, 30, 16);
        this.paper5.setRotationPoint(0.8F, -0.5F, -3.7F);
        this.paper5.addBox(0.0F, 0.0F, 0.0F, 3, 2, 0, 0.0F);
        this.setRotateAngle(paper5, -0.22759093446006054F, 0.31869712141416456F, 1.0927506446736497F);
        this.rope1a.addChild(this.paper2);
        this.rope1c.addChild(this.paper8);
        this.rope1a.addChild(this.paper4);
        this.rope1a.addChild(this.rope1b);
        this.body_top.addChild(this.arms1a);
        this.body_top.addChild(this.rope1a);
        this.rope1a.addChild(this.paper1);
        this.rope1c.addChild(this.paper6);
        this.arms1a.addChild(this.arms1b);
        this.rope1b.addChild(this.rope1c);
        this.body_base.addChild(this.body_top);
        this.rope1c.addChild(this.paper7);
        this.rope1a.addChild(this.paper3);
        this.rope1c.addChild(this.paper5);
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
