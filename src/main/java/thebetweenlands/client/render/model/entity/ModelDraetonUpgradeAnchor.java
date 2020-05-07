package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * BLDraetonAddonAnchor - TripleHeadedSheep
 * Created using Tabula 7.0.1
 */
public class ModelDraetonUpgradeAnchor extends ModelBase {
    public ModelRenderer anchor_main;
    public ModelRenderer anchor_topsplit1;
    public ModelRenderer anchor_rope;
    public ModelRenderer anchor_hook1a;
    public ModelRenderer anchor_hook2a;
    public ModelRenderer anchor_hook3a;
    public ModelRenderer anchor_hook4a;
    public ModelRenderer anchor_hook1b;
    public ModelRenderer anchor_hook1c;
    public ModelRenderer anchor_hook1d;
    public ModelRenderer anchor_hook1e;
    public ModelRenderer anchor_hook2b;
    public ModelRenderer anchor_hook2c;
    public ModelRenderer anchor_hook2d;
    public ModelRenderer anchor_hook2e;
    public ModelRenderer anchor_hook3b;
    public ModelRenderer anchor_hook3c;
    public ModelRenderer anchor_hook3d;
    public ModelRenderer anchor_hook3e;
    public ModelRenderer anchor_hook4b;
    public ModelRenderer anchor_hook4c;
    public ModelRenderer anchor_hook4d;
    public ModelRenderer anchor_hook4e;

    public ModelDraetonUpgradeAnchor() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.anchor_hook1b = new ModelRenderer(this, 9, 13);
        this.anchor_hook1b.setRotationPoint(-0.02F, 3.0F, 0.0F);
        this.anchor_hook1b.addBox(-1.0F, 0.0F, 0.0F, 2, 3, 2, 0.0F);
        this.setRotateAngle(anchor_hook1b, 0.8651597102135892F, 0.0F, 0.0F);
        this.anchor_hook4e = new ModelRenderer(this, 45, 7);
        this.anchor_hook4e.setRotationPoint(0.0F, 3.0F, 2.0F);
        this.anchor_hook4e.addBox(-1.01F, 0.0F, -3.0F, 2, 2, 3, 0.0F);
        this.setRotateAngle(anchor_hook4e, -0.27314402793711257F, 0.0F, 0.0F);
        this.anchor_hook2e = new ModelRenderer(this, 36, 19);
        this.anchor_hook2e.setRotationPoint(0.0F, 3.0F, 2.0F);
        this.anchor_hook2e.addBox(-1.01F, 0.0F, -3.0F, 2, 2, 3, 0.0F);
        this.setRotateAngle(anchor_hook2e, -0.27314402793711257F, 0.0F, 0.0F);
        this.anchor_hook4d = new ModelRenderer(this, 36, 7);
        this.anchor_hook4d.setRotationPoint(0.01F, 3.0F, 0.0F);
        this.anchor_hook4d.addBox(-1.0F, 0.0F, 0.0F, 2, 3, 2, 0.0F);
        this.setRotateAngle(anchor_hook4d, 1.1383037381507017F, 0.0F, 0.0F);
        this.anchor_main = new ModelRenderer(this, 0, 0);
        this.anchor_main.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.anchor_main.addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
        this.anchor_hook3e = new ModelRenderer(this, 36, 25);
        this.anchor_hook3e.setRotationPoint(0.0F, 3.0F, 2.0F);
        this.anchor_hook3e.addBox(-1.01F, 0.0F, -3.0F, 2, 2, 3, 0.0F);
        this.setRotateAngle(anchor_hook3e, -0.27314402793711257F, 0.0F, 0.0F);
        this.anchor_hook2a = new ModelRenderer(this, 0, 19);
        this.anchor_hook2a.setRotationPoint(-1.0F, 8.0F, 0.0F);
        this.anchor_hook2a.addBox(-1.01F, 0.0F, 0.0F, 2, 3, 2, 0.0F);
        this.setRotateAngle(anchor_hook2a, 0.7285004297824331F, 1.5707963267948966F, 0.0F);
        this.anchor_hook2d = new ModelRenderer(this, 27, 19);
        this.anchor_hook2d.setRotationPoint(0.01F, 3.0F, 0.0F);
        this.anchor_hook2d.addBox(-1.0F, 0.0F, 0.0F, 2, 3, 2, 0.0F);
        this.setRotateAngle(anchor_hook2d, 1.1383037381507017F, 0.0F, 0.0F);
        this.anchor_hook1d = new ModelRenderer(this, 27, 13);
        this.anchor_hook1d.setRotationPoint(0.01F, 3.0F, 0.0F);
        this.anchor_hook1d.addBox(-1.0F, 0.0F, 0.0F, 2, 3, 2, 0.0F);
        this.setRotateAngle(anchor_hook1d, 1.1383037381507017F, 0.0F, 0.0F);
        this.anchor_hook2c = new ModelRenderer(this, 18, 19);
        this.anchor_hook2c.setRotationPoint(-0.01F, 3.0F, 0.0F);
        this.anchor_hook2c.addBox(-1.0F, 0.0F, 0.0F, 2, 3, 2, 0.0F);
        this.setRotateAngle(anchor_hook2c, 0.9105382707654417F, 0.0F, 0.0F);
        this.anchor_hook3a = new ModelRenderer(this, 0, 25);
        this.anchor_hook3a.setRotationPoint(0.0F, 8.0F, 1.0F);
        this.anchor_hook3a.addBox(-1.01F, 0.0F, 0.0F, 2, 3, 2, 0.0F);
        this.setRotateAngle(anchor_hook3a, 0.7285004297824331F, 3.141592653589793F, 0.0F);
        this.anchor_hook3c = new ModelRenderer(this, 18, 25);
        this.anchor_hook3c.setRotationPoint(-0.01F, 3.0F, 0.0F);
        this.anchor_hook3c.addBox(-1.0F, 0.0F, 0.0F, 2, 3, 2, 0.0F);
        this.setRotateAngle(anchor_hook3c, 0.9105382707654417F, 0.0F, 0.0F);
        this.anchor_hook4b = new ModelRenderer(this, 18, 7);
        this.anchor_hook4b.setRotationPoint(-0.02F, 3.0F, 0.0F);
        this.anchor_hook4b.addBox(-1.0F, 0.0F, 0.0F, 2, 3, 2, 0.0F);
        this.setRotateAngle(anchor_hook4b, 0.8651597102135892F, 0.0F, 0.0F);
        this.anchor_rope = new ModelRenderer(this, 26, 0);
        this.anchor_rope.setRotationPoint(0.0F, 1.0F, 0.0F);
        this.anchor_rope.addBox(-1.5F, -1.0F, -1.5F, 3, 2, 3, 0.0F);
        this.setRotateAngle(anchor_rope, -0.091106186954104F, 0.136659280431156F, 0.0F);
        this.anchor_hook4a = new ModelRenderer(this, 9, 7);
        this.anchor_hook4a.setRotationPoint(1.0F, 8.0F, 0.0F);
        this.anchor_hook4a.addBox(-1.01F, 0.0F, 0.0F, 2, 3, 2, 0.0F);
        this.setRotateAngle(anchor_hook4a, 0.7285004297824331F, -1.5707963267948966F, 0.0F);
        this.anchor_hook3b = new ModelRenderer(this, 9, 25);
        this.anchor_hook3b.setRotationPoint(-0.02F, 3.0F, 0.0F);
        this.anchor_hook3b.addBox(-1.0F, 0.0F, 0.0F, 2, 3, 2, 0.0F);
        this.setRotateAngle(anchor_hook3b, 0.8651597102135892F, 0.0F, 0.0F);
        this.anchor_hook3d = new ModelRenderer(this, 27, 25);
        this.anchor_hook3d.setRotationPoint(0.01F, 3.0F, 0.0F);
        this.anchor_hook3d.addBox(-1.0F, 0.0F, 0.0F, 2, 3, 2, 0.0F);
        this.setRotateAngle(anchor_hook3d, 1.1383037381507017F, 0.0F, 0.0F);
        this.anchor_hook1a = new ModelRenderer(this, 0, 13);
        this.anchor_hook1a.setRotationPoint(0.0F, 8.0F, -1.0F);
        this.anchor_hook1a.addBox(-1.01F, 0.0F, 0.0F, 2, 3, 2, 0.0F);
        this.setRotateAngle(anchor_hook1a, 0.7285004297824331F, 0.0F, 0.0F);
        this.anchor_hook1c = new ModelRenderer(this, 18, 13);
        this.anchor_hook1c.setRotationPoint(-0.01F, 3.0F, 0.0F);
        this.anchor_hook1c.addBox(-1.0F, 0.0F, 0.0F, 2, 3, 2, 0.0F);
        this.setRotateAngle(anchor_hook1c, 0.9105382707654417F, 0.0F, 0.0F);
        this.anchor_topsplit1 = new ModelRenderer(this, 9, 0);
        this.anchor_topsplit1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.anchor_topsplit1.addBox(-2.0F, -2.0F, -2.0F, 4, 2, 4, 0.0F);
        this.anchor_hook2b = new ModelRenderer(this, 9, 19);
        this.anchor_hook2b.setRotationPoint(-0.02F, 3.0F, 0.0F);
        this.anchor_hook2b.addBox(-1.0F, 0.0F, 0.0F, 2, 3, 2, 0.0F);
        this.setRotateAngle(anchor_hook2b, 0.8651597102135892F, 0.0F, 0.0F);
        this.anchor_hook1e = new ModelRenderer(this, 36, 13);
        this.anchor_hook1e.setRotationPoint(0.0F, 3.0F, 2.0F);
        this.anchor_hook1e.addBox(-1.01F, 0.0F, -3.0F, 2, 2, 3, 0.0F);
        this.setRotateAngle(anchor_hook1e, -0.27314402793711257F, 0.0F, 0.0F);
        this.anchor_hook4c = new ModelRenderer(this, 27, 7);
        this.anchor_hook4c.setRotationPoint(-0.01F, 3.0F, 0.0F);
        this.anchor_hook4c.addBox(-1.0F, 0.0F, 0.0F, 2, 3, 2, 0.0F);
        this.setRotateAngle(anchor_hook4c, 0.9105382707654417F, 0.0F, 0.0F);
        this.anchor_hook1a.addChild(this.anchor_hook1b);
        this.anchor_hook4d.addChild(this.anchor_hook4e);
        this.anchor_hook2d.addChild(this.anchor_hook2e);
        this.anchor_hook4c.addChild(this.anchor_hook4d);
        this.anchor_hook3d.addChild(this.anchor_hook3e);
        this.anchor_main.addChild(this.anchor_hook2a);
        this.anchor_hook2c.addChild(this.anchor_hook2d);
        this.anchor_hook1c.addChild(this.anchor_hook1d);
        this.anchor_hook2b.addChild(this.anchor_hook2c);
        this.anchor_main.addChild(this.anchor_hook3a);
        this.anchor_hook3b.addChild(this.anchor_hook3c);
        this.anchor_hook4a.addChild(this.anchor_hook4b);
        this.anchor_main.addChild(this.anchor_rope);
        this.anchor_main.addChild(this.anchor_hook4a);
        this.anchor_hook3a.addChild(this.anchor_hook3b);
        this.anchor_hook3c.addChild(this.anchor_hook3d);
        this.anchor_main.addChild(this.anchor_hook1a);
        this.anchor_hook1b.addChild(this.anchor_hook1c);
        this.anchor_main.addChild(this.anchor_topsplit1);
        this.anchor_hook2a.addChild(this.anchor_hook2b);
        this.anchor_hook1d.addChild(this.anchor_hook1e);
        this.anchor_hook4b.addChild(this.anchor_hook4c);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.anchor_main.render(f5);
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
