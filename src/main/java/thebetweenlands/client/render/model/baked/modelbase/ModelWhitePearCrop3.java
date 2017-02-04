package thebetweenlands.client.render.model.baked.modelbase;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * BLWhitePear3 - TripleHeadedSheep
 * Created using Tabula 4.1.1
 */
public class ModelWhitePearCrop3 extends ModelBase {
    public ModelRenderer stem1;
    public ModelRenderer stem2;
    public ModelRenderer stem3;
    public ModelRenderer leaf1a;
    public ModelRenderer leaf1d;
    public ModelRenderer leaf1f;
    public ModelRenderer leaf2a;
    public ModelRenderer leaf2d;
    public ModelRenderer stem4;
    public ModelRenderer leaf3a;
    public ModelRenderer leaf3d;
    public ModelRenderer leaf3e;
    public ModelRenderer stem5;
    public ModelRenderer leaf4a;
    public ModelRenderer leaf5a;
    public ModelRenderer leaf6a;
    public ModelRenderer leaf4b;
    public ModelRenderer leaf5b;
    public ModelRenderer leaf5c;
    public ModelRenderer leaf6b;
    public ModelRenderer leaf3b;
    public ModelRenderer leaf3c;
    public ModelRenderer leaf3f;
    public ModelRenderer leaf1b;
    public ModelRenderer leaf1c;
    public ModelRenderer leaf1e;
    public ModelRenderer leaf2b;
    public ModelRenderer leaf2c;
    public ModelRenderer leaf2e;

    public ModelWhitePearCrop3() {
        this.textureWidth = 128;
        this.textureHeight = 64;
        this.leaf1c = new ModelRenderer(this, 6, 8);
        this.leaf1c.setRotationPoint(0.0F, 0.0F, 3.0F);
        this.leaf1c.addBox(-2.0F, 0.0F, 0.0F, 4, 0, 3, 0.0F);
        this.setRotateAngle(leaf1c, -0.8196066167365371F, 0.0F, 0.0F);
        this.leaf2b = new ModelRenderer(this, 15, 4);
        this.leaf2b.setRotationPoint(0.0F, 0.0F, 3.0F);
        this.leaf2b.addBox(-2.0F, 0.0F, 0.0F, 4, 0, 3, 0.0F);
        this.setRotateAngle(leaf2b, -0.8196066167365371F, 0.0F, 0.0F);
        this.leaf5a = new ModelRenderer(this, 41, 0);
        this.leaf5a.setRotationPoint(1.5F, -4.0F, 0.0F);
        this.leaf5a.addBox(-2.0F, 0.0F, 0.0F, 4, 0, 3, 0.0F);
        this.setRotateAngle(leaf5a, 0.5918411493512771F, 1.9577358219620393F, -0.136659280431156F);
        this.stem4 = new ModelRenderer(this, 0, 21);
        this.stem4.setRotationPoint(-2.0F, -4.0F, 0.0F);
        this.stem4.addBox(0.0F, -4.0F, -1.03F, 2, 4, 2, 0.0F);
        this.setRotateAngle(stem4, 0.0F, 0.0F, 0.40980330836826856F);
        this.leaf2a = new ModelRenderer(this, 15, 0);
        this.leaf2a.setRotationPoint(-1.5F, -3.5F, -0.5F);
        this.leaf2a.addBox(-2.0F, 0.0F, 0.0F, 4, 0, 3, 0.0F);
        this.setRotateAngle(leaf2a, 0.4553564018453205F, -2.1399481958702475F, 0.0F);
        this.leaf3e = new ModelRenderer(this, 24, 16);
        this.leaf3e.setRotationPoint(-0.5F, -4.0F, -0.5F);
        this.leaf3e.addBox(-1.5F, 0.0F, 0.0F, 3, 0, 3, 0.0F);
        this.setRotateAngle(leaf3e, 0.5009094953223726F, 2.231054382824351F, 0.5009094953223726F);
        this.leaf3f = new ModelRenderer(this, 24, 20);
        this.leaf3f.setRotationPoint(0.0F, 0.0F, 3.0F);
        this.leaf3f.addBox(-1.5F, 0.0F, 0.0F, 3, 0, 3, 0.0F);
        this.setRotateAngle(leaf3f, -0.7740535232594852F, 0.0F, 0.0F);
        this.leaf6a = new ModelRenderer(this, 50, 0);
        this.leaf6a.setRotationPoint(0.5F, -4.0F, -0.5F);
        this.leaf6a.addBox(-1.5F, 0.0F, 0.0F, 3, 0, 3, 0.0F);
        this.setRotateAngle(leaf6a, 0.27314402793711257F, -2.321986036853256F, 0.0F);
        this.leaf3c = new ModelRenderer(this, 24, 8);
        this.leaf3c.setRotationPoint(0.0F, 0.0F, 3.0F);
        this.leaf3c.addBox(-2.0F, 0.0F, 0.0F, 4, 0, 3, 0.0F);
        this.setRotateAngle(leaf3c, -0.9105382707654417F, 0.0F, 0.0F);
        this.leaf3a = new ModelRenderer(this, 24, 0);
        this.leaf3a.setRotationPoint(-1.5F, -4.0F, 0.5F);
        this.leaf3a.addBox(-2.0F, 0.0F, 0.0F, 4, 0, 3, 0.0F);
        this.setRotateAngle(leaf3a, 0.4553564018453205F, -0.9105382707654417F, 0.4553564018453205F);
        this.leaf1e = new ModelRenderer(this, 6, 16);
        this.leaf1e.setRotationPoint(0.0F, 0.0F, 3.0F);
        this.leaf1e.addBox(-1.5F, 0.0F, 0.0F, 3, 0, 3, 0.0F);
        this.setRotateAngle(leaf1e, -0.7740535232594852F, 0.0F, 0.0F);
        this.stem2 = new ModelRenderer(this, 0, 7);
        this.stem2.setRotationPoint(1.0F, -4.0F, 0.0F);
        this.stem2.addBox(-2.0F, -4.0F, -1.01F, 2, 4, 2, 0.0F);
        this.setRotateAngle(stem2, 0.0F, 0.0F, -0.5462880558742251F);
        this.stem3 = new ModelRenderer(this, 0, 14);
        this.stem3.setRotationPoint(0.0F, -4.0F, 0.0F);
        this.stem3.addBox(-2.0F, -4.0F, -1.02F, 2, 4, 2, 0.0F);
        this.setRotateAngle(stem3, 0.0F, 0.0F, -0.5462880558742251F);
        this.leaf5c = new ModelRenderer(this, 41, 8);
        this.leaf5c.setRotationPoint(0.0F, 0.0F, 3.0F);
        this.leaf5c.addBox(-2.0F, 0.0F, 0.0F, 4, 0, 3, 0.0F);
        this.setRotateAngle(leaf5c, -0.8196066167365371F, 0.0F, 0.0F);
        this.leaf3b = new ModelRenderer(this, 24, 4);
        this.leaf3b.setRotationPoint(0.0F, 0.0F, 3.0F);
        this.leaf3b.addBox(-2.0F, 0.0F, 0.0F, 4, 0, 3, 0.0F);
        this.setRotateAngle(leaf3b, -0.7285004297824331F, 0.0F, 0.0F);
        this.leaf1f = new ModelRenderer(this, 6, 20);
        this.leaf1f.setRotationPoint(-1.0F, -4.0F, 0.5F);
        this.leaf1f.addBox(-1.5F, 0.0F, 0.0F, 3, 0, 3, 0.0F);
        this.setRotateAngle(leaf1f, 0.091106186954104F, 0.36425021489121656F, 0.0F);
        this.leaf2e = new ModelRenderer(this, 15, 16);
        this.leaf2e.setRotationPoint(0.0F, 0.0F, 3.0F);
        this.leaf2e.addBox(-1.5F, 0.0F, 0.0F, 3, 0, 3, 0.0F);
        this.setRotateAngle(leaf2e, -0.7285004297824331F, 0.0F, 0.0F);
        this.leaf6b = new ModelRenderer(this, 50, 4);
        this.leaf6b.setRotationPoint(0.0F, 0.0F, 3.0F);
        this.leaf6b.addBox(-1.5F, 0.0F, 0.0F, 3, 0, 3, 0.0F);
        this.setRotateAngle(leaf6b, -1.0016444577195458F, 0.0F, 0.0F);
        this.stem5 = new ModelRenderer(this, 0, 28);
        this.stem5.setRotationPoint(0.0F, -4.0F, 0.0F);
        this.stem5.addBox(0.0F, -4.0F, -1.04F, 2, 4, 2, 0.0F);
        this.setRotateAngle(stem5, 0.0F, 0.0F, 0.40980330836826856F);
        this.leaf1d = new ModelRenderer(this, 6, 12);
        this.leaf1d.setRotationPoint(-0.5F, -4.0F, 0.0F);
        this.leaf1d.addBox(-1.5F, 0.0F, 0.0F, 3, 0, 3, 0.0F);
        this.setRotateAngle(leaf1d, 0.22759093446006054F, 1.3658946726107624F, 0.0F);
        this.leaf4b = new ModelRenderer(this, 34, 4);
        this.leaf4b.setRotationPoint(0.0F, 0.0F, 3.0F);
        this.leaf4b.addBox(-1.5F, 0.0F, 0.0F, 3, 0, 3, 0.0F);
        this.setRotateAngle(leaf4b, -1.0927506446736497F, 0.0F, 0.0F);
        this.stem1 = new ModelRenderer(this, 0, 0);
        this.stem1.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.stem1.addBox(-1.0F, -4.0F, -1.0F, 2, 5, 2, 0.0F);
        this.setRotateAngle(stem1, 0.045553093477052F, 0.0F, 0.5918411493512771F);
        this.leaf3d = new ModelRenderer(this, 24, 12);
        this.leaf3d.setRotationPoint(-1.0F, -3.5F, 0.5F);
        this.leaf3d.addBox(-1.5F, 0.0F, 0.0F, 3, 0, 3, 0.0F);
        this.setRotateAngle(leaf3d, 0.22759093446006054F, -0.136659280431156F, 0.5009094953223726F);
        this.leaf1a = new ModelRenderer(this, 6, 0);
        this.leaf1a.setRotationPoint(-0.5F, -4.0F, 0.5F);
        this.leaf1a.addBox(-2.0F, 0.0F, 0.0F, 4, 0, 3, 0.0F);
        this.setRotateAngle(leaf1a, 0.5918411493512771F, 0.8196066167365371F, 0.0F);
        this.leaf2c = new ModelRenderer(this, 15, 8);
        this.leaf2c.setRotationPoint(0.0F, 0.0F, 3.0F);
        this.leaf2c.addBox(-2.0F, 0.0F, 0.0F, 4, 0, 3, 0.0F);
        this.setRotateAngle(leaf2c, -0.8196066167365371F, 0.0F, 0.0F);
        this.leaf4a = new ModelRenderer(this, 34, 0);
        this.leaf4a.setRotationPoint(0.5F, -4.0F, 0.5F);
        this.leaf4a.addBox(-1.5F, 0.0F, 0.0F, 3, 0, 3, 0.0F);
        this.setRotateAngle(leaf4a, 0.36425021489121656F, -0.27314402793711257F, -0.091106186954104F);
        this.leaf1b = new ModelRenderer(this, 6, 4);
        this.leaf1b.setRotationPoint(0.0F, 0.0F, 3.0F);
        this.leaf1b.addBox(-2.0F, 0.0F, 0.0F, 4, 0, 3, 0.0F);
        this.setRotateAngle(leaf1b, -0.7285004297824331F, 0.0F, 0.0F);
        this.leaf2d = new ModelRenderer(this, 15, 12);
        this.leaf2d.setRotationPoint(-1.5F, -3.5F, -0.2F);
        this.leaf2d.addBox(-1.5F, 0.0F, 0.0F, 3, 0, 3, 0.0F);
        this.setRotateAngle(leaf2d, 0.18203784098300857F, -1.6390387005478748F, 0.0F);
        this.leaf5b = new ModelRenderer(this, 41, 4);
        this.leaf5b.setRotationPoint(0.0F, 0.0F, 3.0F);
        this.leaf5b.addBox(-2.0F, 0.0F, 0.0F, 4, 0, 3, 0.0F);
        this.setRotateAngle(leaf5b, -0.8196066167365371F, 0.0F, 0.0F);
        this.leaf1b.addChild(this.leaf1c);
        this.leaf2a.addChild(this.leaf2b);
        this.stem5.addChild(this.leaf5a);
        this.stem3.addChild(this.stem4);
        this.stem2.addChild(this.leaf2a);
        this.stem3.addChild(this.leaf3e);
        this.leaf3e.addChild(this.leaf3f);
        this.stem5.addChild(this.leaf6a);
        this.leaf3b.addChild(this.leaf3c);
        this.stem3.addChild(this.leaf3a);
        this.leaf1d.addChild(this.leaf1e);
        this.stem1.addChild(this.stem2);
        this.stem2.addChild(this.stem3);
        this.leaf5b.addChild(this.leaf5c);
        this.leaf3a.addChild(this.leaf3b);
        this.stem2.addChild(this.leaf1f);
        this.leaf2d.addChild(this.leaf2e);
        this.leaf6a.addChild(this.leaf6b);
        this.stem4.addChild(this.stem5);
        this.stem2.addChild(this.leaf1d);
        this.leaf4a.addChild(this.leaf4b);
        this.stem3.addChild(this.leaf3d);
        this.stem2.addChild(this.leaf1a);
        this.leaf2b.addChild(this.leaf2c);
        this.stem5.addChild(this.leaf4a);
        this.leaf1a.addChild(this.leaf1b);
        this.stem2.addChild(this.leaf2d);
        this.leaf5a.addChild(this.leaf5b);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.stem1.render(f5);
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
