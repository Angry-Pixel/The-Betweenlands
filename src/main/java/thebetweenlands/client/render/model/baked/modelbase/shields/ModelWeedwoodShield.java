package thebetweenlands.client.render.model.baked.modelbase.shields;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * BLShield_Weedwood - TripleHeadedSheep
 * Created using Tabula 4.1.1
 */
public class ModelWeedwoodShield extends ModelBase {
    public ModelRenderer handle;
    public ModelRenderer shield_main;
    public ModelRenderer shieldpiece1;
    public ModelRenderer shieldpiece5;
    public ModelRenderer leaf1;
    public ModelRenderer shieldpiece2;
    public ModelRenderer shieldpiece3;
    public ModelRenderer shieldpiece4;
    public ModelRenderer shieldpiece6;
    public ModelRenderer shieldpiece7;
    public ModelRenderer leaf2;
    public ModelRenderer leaf3;
    public ModelRenderer leaf4;
    public ModelRenderer leaf5;

    public ModelWeedwoodShield() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.shieldpiece2 = new ModelRenderer(this, 29, 0);
        this.shieldpiece2.setRotationPoint(0.0F, -6.0F, 0.0F);
        this.shieldpiece2.addBox(-5.0F, -2.0F, 0.0F, 10, 2, 2, 0.0F);
        this.setRotateAngle(shieldpiece2, -0.091106186954104F, 0.0F, 0.0F);
        this.shieldpiece5 = new ModelRenderer(this, 29, 13);
        this.shieldpiece5.setRotationPoint(-6.0F, 4.0F, -2.0F);
        this.shieldpiece5.addBox(0.0F, 0.0F, 0.0F, 3, 3, 2, 0.0F);
        this.shieldpiece1 = new ModelRenderer(this, 0, 24);
        this.shieldpiece1.setRotationPoint(0.0F, -4.0F, -2.0F);
        this.shieldpiece1.addBox(-6.0F, -6.0F, 0.0F, 12, 6, 2, 0.0F);
        this.setRotateAngle(shieldpiece1, -0.091106186954104F, 0.0F, 0.0F);
        this.leaf1 = new ModelRenderer(this, 42, 9);
        this.leaf1.setRotationPoint(0.0F, -4.0F, -2.0F);
        this.leaf1.addBox(-2.0F, 0.0F, 0.0F, 4, 2, 0, 0.0F);
        this.setRotateAngle(leaf1, -0.36425021489121656F, 0.0F, 0.0F);
        this.shieldpiece7 = new ModelRenderer(this, 29, 26);
        this.shieldpiece7.setRotationPoint(3.0F, 0.0F, 0.0F);
        this.shieldpiece7.addBox(0.0F, 0.0F, 0.0F, 3, 2, 2, 0.0F);
        this.leaf5 = new ModelRenderer(this, 42, 23);
        this.leaf5.setRotationPoint(0.0F, 2.0F, 0.0F);
        this.leaf5.addBox(-2.0F, 0.0F, 0.0F, 4, 2, 0, 0.0F);
        this.setRotateAngle(leaf5, -0.8651597102135892F, 0.0F, 0.0F);
        this.shieldpiece4 = new ModelRenderer(this, 29, 9);
        this.shieldpiece4.setRotationPoint(0.0F, -3.0F, 0.0F);
        this.shieldpiece4.addBox(-2.0F, -1.0F, 0.0F, 4, 1, 2, 0.0F);
        this.shieldpiece6 = new ModelRenderer(this, 29, 19);
        this.shieldpiece6.setRotationPoint(6.0F, 0.0F, 0.0F);
        this.shieldpiece6.addBox(0.0F, 0.0F, 0.0F, 4, 4, 2, 0.0F);
        this.leaf3 = new ModelRenderer(this, 42, 16);
        this.leaf3.setRotationPoint(0.0F, 3.0F, 0.0F);
        this.leaf3.addBox(-3.0F, 0.0F, 0.0F, 6, 3, 0, 0.0F);
        this.setRotateAngle(leaf3, 0.27314402793711257F, 0.0F, 0.0F);
        this.handle = new ModelRenderer(this, 0, 0);
        this.handle.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.handle.addBox(-1.0F, -3.0F, -1.0F, 2, 6, 6, 0.0F);
        this.leaf4 = new ModelRenderer(this, 42, 20);
        this.leaf4.setRotationPoint(0.0F, 3.0F, 0.0F);
        this.leaf4.addBox(-3.0F, 0.0F, 0.0F, 6, 2, 0, 0.0F);
        this.setRotateAngle(leaf4, -0.5009094953223726F, 0.0F, 0.0F);
        this.shieldpiece3 = new ModelRenderer(this, 29, 5);
        this.shieldpiece3.setRotationPoint(0.0F, -2.0F, 0.0F);
        this.shieldpiece3.addBox(-4.0F, -1.0F, 0.0F, 8, 1, 2, 0.0F);
        this.shield_main = new ModelRenderer(this, 0, 13);
        this.shield_main.setRotationPoint(0.0F, 2.0F, -1.0F);
        this.shield_main.addBox(-6.0F, -4.0F, -2.0F, 12, 8, 2, 0.0F);
        this.leaf2 = new ModelRenderer(this, 42, 12);
        this.leaf2.setRotationPoint(0.0F, 2.0F, 0.0F);
        this.leaf2.addBox(-3.0F, 0.0F, 0.0F, 6, 3, 0, 0.0F);
        this.setRotateAngle(leaf2, 0.27314402793711257F, 0.0F, 0.0F);
        this.shieldpiece1.addChild(this.shieldpiece2);
        this.shield_main.addChild(this.shieldpiece5);
        this.shield_main.addChild(this.shieldpiece1);
        this.shield_main.addChild(this.leaf1);
        this.shieldpiece5.addChild(this.shieldpiece7);
        this.leaf4.addChild(this.leaf5);
        this.shieldpiece2.addChild(this.shieldpiece4);
        this.shieldpiece5.addChild(this.shieldpiece6);
        this.leaf2.addChild(this.leaf3);
        this.leaf3.addChild(this.leaf4);
        this.shieldpiece2.addChild(this.shieldpiece3);
        this.handle.addChild(this.shield_main);
        this.leaf1.addChild(this.leaf2);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.handle.render(f5);
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
