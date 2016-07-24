package thebetweenlands.client.render.model.baked.modelbase;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * BLRegularplant - TripleHeadedSheep
 * Created using Tabula 4.1.1
 */
public class ModelSwampPlant extends ModelBase {
    public ModelRenderer leaf1;
    public ModelRenderer leaf2;
    public ModelRenderer leaf3;
    public ModelRenderer leaf4;
    public ModelRenderer leaf5;
    public ModelRenderer leaf6;
    public ModelRenderer leaf7;
    public ModelRenderer leaf8;
    public ModelRenderer leaf9;
    public ModelRenderer leaf10;
    public ModelRenderer leaf11;
    public ModelRenderer leaf12;
    public ModelRenderer leafcentre;
    public ModelRenderer leaf1b;
    public ModelRenderer leaf2b;
    public ModelRenderer leaf3b;
    public ModelRenderer leaf4b;
    public ModelRenderer leaf5b;
    public ModelRenderer leaf6b;
    public ModelRenderer leaf7b;
    public ModelRenderer leaf8b;

    public ModelSwampPlant() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.leaf4b = new ModelRenderer(this, 21, 5);
        this.leaf4b.setRotationPoint(0.0F, 0.0F, -4.0F);
        this.leaf4b.addBox(-2.0F, 0.0F, -3.0F, 4, 0, 3, 0.0F);
        this.setRotateAngle(leaf4b, 0.6373942428283291F, 0.0F, 0.0F);
        this.leaf7 = new ModelRenderer(this, 35, 0);
        this.leaf7.setRotationPoint(2.0F, 24.0F, 1.0F);
        this.leaf7.addBox(-3.0F, 0.0F, -6.0F, 6, 0, 6, 0.0F);
        this.setRotateAngle(leaf7, -0.5462880558742251F, -2.41309222380736F, 0.0F);
        this.leaf6b = new ModelRenderer(this, 20, 27);
        this.leaf6b.setRotationPoint(0.0F, 0.0F, -5.0F);
        this.leaf6b.addBox(-2.5F, 0.0F, -4.0F, 5, 0, 4, 0.0F);
        this.setRotateAngle(leaf6b, 0.9560913642424937F, 0.0F, 0.0F);
        this.leaf1 = new ModelRenderer(this, 0, 0);
        this.leaf1.setRotationPoint(0.0F, 24.0F, -2.0F);
        this.leaf1.addBox(-3.0F, 0.0F, -6.0F, 6, 0, 6, 0.0F);
        this.setRotateAngle(leaf1, -0.40980330836826856F, 0.0F, 0.0F);
        this.leaf4 = new ModelRenderer(this, 20, 0);
        this.leaf4.setRotationPoint(-2.0F, 24.0F, 0.0F);
        this.leaf4.addBox(-2.0F, 0.0F, -4.0F, 4, 0, 4, 0.0F);
        this.setRotateAngle(leaf4, -0.5462880558742251F, 1.593485607070823F, 0.0F);
        this.leaf5 = new ModelRenderer(this, 18, 9);
        this.leaf5.setRotationPoint(-2.0F, 24.0F, 1.0F);
        this.leaf5.addBox(-3.0F, 0.0F, -6.0F, 6, 0, 6, 0.0F);
        this.setRotateAngle(leaf5, -0.4553564018453205F, 2.41309222380736F, 0.0F);
        this.leaf11 = new ModelRenderer(this, 45, 21);
        this.leaf11.setRotationPoint(-1.0F, 24.0F, 0.5F);
        this.leaf11.addBox(-2.5F, 0.0F, -5.0F, 5, 0, 5, 0.0F);
        this.setRotateAngle(leaf11, -0.7285004297824331F, 2.0488420089161434F, 0.0F);
        this.leaf9 = new ModelRenderer(this, 37, 21);
        this.leaf9.setRotationPoint(-1.0F, 24.0F, -1.0F);
        this.leaf9.addBox(-2.0F, 0.0F, -4.0F, 4, 0, 4, 0.0F);
        this.setRotateAngle(leaf9, -0.9560913642424937F, 0.5918411493512771F, 0.0F);
        this.leaf3 = new ModelRenderer(this, 2, 22);
        this.leaf3.setRotationPoint(2.0F, 24.0F, -1.0F);
        this.leaf3.addBox(-2.0F, 0.0F, -4.0F, 4, 0, 4, 0.0F);
        this.setRotateAngle(leaf3, -0.31869712141416456F, -0.5462880558742251F, 0.0F);
        this.leaf7b = new ModelRenderer(this, 37, 7);
        this.leaf7b.setRotationPoint(0.0F, 0.0F, -6.0F);
        this.leaf7b.addBox(-3.0F, 0.0F, -4.0F, 6, 0, 4, 0.0F);
        this.setRotateAngle(leaf7b, 0.5918411493512771F, 0.0F, 0.0F);
        this.leaf5b = new ModelRenderer(this, 20, 16);
        this.leaf5b.setRotationPoint(0.0F, 0.0F, -6.0F);
        this.leaf5b.addBox(-3.0F, 0.0F, -4.0F, 6, 0, 4, 0.0F);
        this.setRotateAngle(leaf5b, 0.9105382707654417F, 0.0F, 0.0F);
        this.leaf10 = new ModelRenderer(this, 37, 26);
        this.leaf10.setRotationPoint(1.0F, 24.0F, -0.5F);
        this.leaf10.addBox(-2.0F, 0.0F, -4.0F, 4, 0, 4, 0.0F);
        this.setRotateAngle(leaf10, -0.7740535232594852F, -1.1383037381507017F, 0.0F);
        this.leaf2b = new ModelRenderer(this, 3, 18);
        this.leaf2b.setRotationPoint(0.0F, 0.0F, -5.0F);
        this.leaf2b.addBox(-2.5F, 0.0F, -3.0F, 5, 0, 3, 0.0F);
        this.setRotateAngle(leaf2b, 0.7285004297824331F, 0.0F, 0.0F);
        this.leaf1b = new ModelRenderer(this, 2, 7);
        this.leaf1b.setRotationPoint(0.0F, 0.0F, -6.0F);
        this.leaf1b.addBox(-3.0F, 0.0F, -4.0F, 6, 0, 4, 0.0F);
        this.setRotateAngle(leaf1b, 0.7740535232594852F, 0.0F, 0.0F);
        this.leaf12 = new ModelRenderer(this, 46, 27);
        this.leaf12.setRotationPoint(1.0F, 24.0F, 1.0F);
        this.leaf12.addBox(-2.0F, 0.0F, -4.0F, 4, 0, 4, 0.0F);
        this.setRotateAngle(leaf12, -0.8196066167365371F, -2.6862362517444724F, 0.0F);
        this.leaf3b = new ModelRenderer(this, 3, 27);
        this.leaf3b.setRotationPoint(0.0F, 0.0F, -4.0F);
        this.leaf3b.addBox(-2.0F, 0.0F, -3.0F, 4, 0, 3, 0.0F);
        this.setRotateAngle(leaf3b, 0.5009094953223726F, 0.0F, 0.0F);
        this.leaf8b = new ModelRenderer(this, 38, 17);
        this.leaf8b.setRotationPoint(0.0F, 0.0F, -4.0F);
        this.leaf8b.addBox(-2.0F, 0.0F, -3.0F, 4, 0, 3, 0.0F);
        this.setRotateAngle(leaf8b, 1.2747884856566583F, 0.0F, 0.0F);
        this.leaf2 = new ModelRenderer(this, 1, 12);
        this.leaf2.setRotationPoint(-2.0F, 24.0F, -1.0F);
        this.leaf2.addBox(-2.5F, 0.0F, -5.0F, 5, 0, 5, 0.0F);
        this.setRotateAngle(leaf2, -0.5918411493512771F, 0.7285004297824331F, 0.0F);
        this.leafcentre = new ModelRenderer(this, 50, 0);
        this.leafcentre.setRotationPoint(0.0F, 23.9F, 0.0F);
        this.leafcentre.addBox(-2.0F, 0.0F, -2.0F, 4, 0, 4, 0.0F);
        this.leaf8 = new ModelRenderer(this, 37, 12);
        this.leaf8.setRotationPoint(2.0F, 24.0F, 0.0F);
        this.leaf8.addBox(-2.0F, 0.0F, -4.0F, 4, 0, 4, 0.0F);
        this.setRotateAngle(leaf8, -0.6373942428283291F, -1.5481070465189704F, 0.0F);
        this.leaf6 = new ModelRenderer(this, 19, 21);
        this.leaf6.setRotationPoint(0.0F, 24.0F, 2.0F);
        this.leaf6.addBox(-2.5F, 0.0F, -5.0F, 5, 0, 5, 0.0F);
        this.setRotateAngle(leaf6, -0.4553564018453205F, 3.141592653589793F, 0.0F);
        this.leaf4.addChild(this.leaf4b);
        this.leaf6.addChild(this.leaf6b);
        this.leaf7.addChild(this.leaf7b);
        this.leaf5.addChild(this.leaf5b);
        this.leaf2.addChild(this.leaf2b);
        this.leaf1.addChild(this.leaf1b);
        this.leaf3.addChild(this.leaf3b);
        this.leaf8.addChild(this.leaf8b);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.leaf7.render(f5);
        this.leaf1.render(f5);
        this.leaf4.render(f5);
        this.leaf5.render(f5);
        this.leaf11.render(f5);
        this.leaf9.render(f5);
        this.leaf3.render(f5);
        this.leaf10.render(f5);
        this.leaf12.render(f5);
        this.leaf2.render(f5);
        this.leafcentre.render(f5);
        this.leaf8.render(f5);
        this.leaf6.render(f5);
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
