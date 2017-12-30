package thebetweenlands.client.render.model.baked.modelbase.shields;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * BLShield_Syrmorite - TripleHeadedSheep
 * Created using Tabula 4.1.1
 */
public class ModelSyrmoriteShield extends ModelBase {
    public ModelRenderer handle1;
    public ModelRenderer handle2;
    public ModelRenderer shield_main;
    public ModelRenderer shieldplate1;
    public ModelRenderer rimplate1;
    public ModelRenderer rimplate2;
    public ModelRenderer midrim1;
    public ModelRenderer rimplate3;
    public ModelRenderer rimplate4;
    public ModelRenderer midrim2;
    public ModelRenderer midrim3;

    public ModelSyrmoriteShield() {
        this.textureWidth = 128;
        this.textureHeight = 64;
        this.shieldplate1 = new ModelRenderer(this, 21, 14);
        this.shieldplate1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.shieldplate1.addBox(-4.0F, -14.0F, -2.0F, 8, 14, 2, 0.0F);
        this.setRotateAngle(shieldplate1, -0.091106186954104F, 0.0F, 0.0F);
        this.rimplate2 = new ModelRenderer(this, 11, 31);
        this.rimplate2.setRotationPoint(-4.0F, 0.0F, -2.0F);
        this.rimplate2.addBox(-3.0F, 0.0F, 0.0F, 3, 15, 2, 0.0F);
        this.setRotateAngle(rimplate2, 0.0F, 0.27314402793711257F, 0.0F);
        this.handle1 = new ModelRenderer(this, 0, 0);
        this.handle1.setRotationPoint(-3.0F, 0.0F, 0.0F);
        this.handle1.addBox(-1.0F, -3.0F, -2.0F, 2, 6, 7, 0.0F);
        this.handle2 = new ModelRenderer(this, 19, 0);
        this.handle2.setRotationPoint(6.0F, 0.0F, 0.0F);
        this.handle2.addBox(-1.0F, -3.0F, -2.0F, 2, 6, 7, 0.0F);
        this.midrim1 = new ModelRenderer(this, 38, 0);
        this.midrim1.setRotationPoint(0.0F, 0.0F, -2.0F);
        this.midrim1.addBox(-4.0F, -1.0F, -1.0F, 8, 2, 2, 0.0F);
        this.setRotateAngle(midrim1, -0.045553093477052F, 0.0F, 0.0F);
        this.midrim3 = new ModelRenderer(this, 55, 5);
        this.midrim3.setRotationPoint(-4.0F, 0.0F, -1.0F);
        this.midrim3.addBox(-4.0F, -1.0F, 0.0F, 4, 2, 4, 0.0F);
        this.setRotateAngle(midrim3, 0.0F, 0.31869712141416456F, 0.0F);
        this.rimplate1 = new ModelRenderer(this, 0, 31);
        this.rimplate1.setRotationPoint(4.0F, 0.0F, -2.0F);
        this.rimplate1.addBox(0.0F, 0.0F, 0.0F, 3, 15, 2, 0.0F);
        this.setRotateAngle(rimplate1, 0.0F, -0.27314402793711257F, 0.0F);
        this.rimplate4 = new ModelRenderer(this, 33, 31);
        this.rimplate4.setRotationPoint(-4.0F, 0.0F, -2.0F);
        this.rimplate4.addBox(-3.0F, -15.0F, 0.0F, 3, 15, 2, 0.0F);
        this.setRotateAngle(rimplate4, 0.0F, 0.27314402793711257F, 0.0F);
        this.midrim2 = new ModelRenderer(this, 38, 5);
        this.midrim2.setRotationPoint(4.0F, 0.0F, -1.0F);
        this.midrim2.addBox(0.0F, -1.0F, 0.0F, 4, 2, 4, 0.0F);
        this.setRotateAngle(midrim2, 0.0F, -0.31869712141416456F, 0.0F);
        this.shield_main = new ModelRenderer(this, 0, 14);
        this.shield_main.setRotationPoint(3.0F, 0.0F, -2.0F);
        this.shield_main.addBox(-4.0F, 0.0F, -2.0F, 8, 14, 2, 0.0F);
        this.setRotateAngle(shield_main, 0.045553093477052F, 0.0F, 0.0F);
        this.rimplate3 = new ModelRenderer(this, 22, 31);
        this.rimplate3.setRotationPoint(4.0F, 0.0F, -2.0F);
        this.rimplate3.addBox(0.0F, -15.0F, 0.0F, 3, 15, 2, 0.0F);
        this.setRotateAngle(rimplate3, 0.0F, -0.27314402793711257F, 0.0F);
        this.shield_main.addChild(this.shieldplate1);
        this.shield_main.addChild(this.rimplate2);
        this.handle1.addChild(this.handle2);
        this.shield_main.addChild(this.midrim1);
        this.midrim1.addChild(this.midrim3);
        this.shield_main.addChild(this.rimplate1);
        this.shieldplate1.addChild(this.rimplate4);
        this.midrim1.addChild(this.midrim2);
        this.handle1.addChild(this.shield_main);
        this.shieldplate1.addChild(this.rimplate3);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.handle1.render(f5);
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
