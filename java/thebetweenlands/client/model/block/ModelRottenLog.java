package thebetweenlands.client.model.block;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * BLDeadLog - TripleHeadedSheep
 * Created using Tabula 4.1.1
 */
public class ModelRottenLog extends ModelBase {
    public ModelRenderer log1;
    public ModelRenderer log2;
    public ModelRenderer stalk1;
    public ModelRenderer stalk2;
    public ModelRenderer cap3;
    public ModelRenderer shelffungus1;
    public ModelRenderer shelffungus1b;
    public ModelRenderer shelffungus2;
    public ModelRenderer shelffungus3;
    public ModelRenderer shelffungus4;
    public ModelRenderer shelffungus5;
    public ModelRenderer shelffungus2b;
    public ModelRenderer shelffungus5b;
    public ModelRenderer cap1;
    public ModelRenderer cap2;

    public ModelRottenLog() {
        this.textureWidth = 128;
        this.textureHeight = 128;
        this.shelffungus5b = new ModelRenderer(this, 97, 7);
        this.shelffungus5b.setRotationPoint(0.0F, 0.0F, 3.5F);
        this.shelffungus5b.addBox(-2.0F, 0.0F, 0.0F, 4, 2, 1, 0.0F);
        this.shelffungus1b = new ModelRenderer(this, 70, 8);
        this.shelffungus1b.setRotationPoint(0.0F, 0.0F, 2.5F);
        this.shelffungus1b.addBox(-3.0F, 0.0F, 0.0F, 6, 2, 1, 0.0F);
        this.shelffungus3 = new ModelRenderer(this, 70, 23);
        this.shelffungus3.setRotationPoint(0.0F, -5.0F, 0.0F);
        this.shelffungus3.addBox(-2.0F, 0.0F, -1.0F, 4, 1, 3, 0.0F);
        this.setRotateAngle(shelffungus3, 0.0F, -0.091106186954104F, 0.0F);
        this.shelffungus5 = new ModelRenderer(this, 97, 0);
        this.shelffungus5.setRotationPoint(-5.5F, -4.0F, -3.0F);
        this.shelffungus5.addBox(-3.0F, 0.0F, -0.5F, 6, 2, 4, 0.0F);
        this.setRotateAngle(shelffungus5, 0.0F, -0.5462880558742251F, 0.0F);
        this.shelffungus1 = new ModelRenderer(this, 70, 0);
        this.shelffungus1.setRotationPoint(-8.0F, -8.0F, 29.5F);
        this.shelffungus1.addBox(-4.0F, 0.0F, -2.5F, 8, 2, 5, 0.0F);
        this.setRotateAngle(shelffungus1, 0.0F, -1.0927506446736497F, 0.0F);
        this.stalk1 = new ModelRenderer(this, 0, 0);
        this.stalk1.setRotationPoint(8.5F, 24.0F, -8.5F);
        this.stalk1.addBox(-1.0F, -3.5F, -1.0F, 2, 4, 2, 0.0F);
        this.setRotateAngle(stalk1, 0.0F, 0.27314402793711257F, 0.31869712141416456F);
        this.shelffungus4 = new ModelRenderer(this, 85, 23);
        this.shelffungus4.setRotationPoint(-3.0F, -7.0F, -2.0F);
        this.shelffungus4.addBox(-2.0F, 0.0F, 0.0F, 4, 1, 3, 0.0F);
        this.setRotateAngle(shelffungus4, 0.0F, -0.31869712141416456F, 0.0F);
        this.stalk2 = new ModelRenderer(this, 0, 14);
        this.stalk2.setRotationPoint(10.0F, 24.0F, -12.0F);
        this.stalk2.addBox(-0.5F, -1.5F, -0.5F, 1, 2, 1, 0.0F);
        this.setRotateAngle(stalk2, 0.22759093446006054F, 0.0F, 0.22759093446006054F);
        this.cap1 = new ModelRenderer(this, 0, 7);
        this.cap1.setRotationPoint(0.0F, -3.0F, 0.0F);
        this.cap1.addBox(-2.0F, -2.0F, -2.0F, 4, 2, 4, 0.0F);
        this.setRotateAngle(cap1, 0.136659280431156F, 0.0F, -0.27314402793711257F);
        this.shelffungus2b = new ModelRenderer(this, 70, 19);
        this.shelffungus2b.setRotationPoint(0.0F, 0.0F, 2.0F);
        this.shelffungus2b.addBox(-2.0F, 0.0F, 0.0F, 4, 2, 1, 0.0F);
        this.cap3 = new ModelRenderer(this, 0, 24);
        this.cap3.setRotationPoint(10.0F, 24.0F, -6.0F);
        this.cap3.addBox(-1.0F, -1.5F, -1.0F, 2, 2, 2, 0.0F);
        this.setRotateAngle(cap3, -0.091106186954104F, -0.22759093446006054F, 0.18203784098300857F);
        this.log2 = new ModelRenderer(this, 0, 49);
        this.log2.setRotationPoint(0.0F, 24.0F, -17.0F);
        this.log2.addBox(-8.0F, -13.0F, -8.0F, 16, 16, 16, 0.0F);
        this.setRotateAngle(log2, 0.091106186954104F, 0.091106186954104F, 0.091106186954104F);
        this.log1 = new ModelRenderer(this, 0, 0);
        this.log1.setRotationPoint(0.0F, 24.0F, -8.0F);
        this.log1.addBox(-8.0F, -16.0F, 0.0F, 16, 16, 32, 0.0F);
        this.setRotateAngle(log1, -0.045553093477052F, 0.0F, 0.0F);
        this.cap2 = new ModelRenderer(this, 0, 19);
        this.cap2.setRotationPoint(0.0F, -1.0F, 0.0F);
        this.cap2.addBox(-1.0F, -2.0F, -1.0F, 2, 2, 2, 0.0F);
        this.setRotateAngle(cap2, -0.136659280431156F, 0.0F, -0.091106186954104F);
        this.shelffungus2 = new ModelRenderer(this, 70, 12);
        this.shelffungus2.setRotationPoint(2.8F, -3.0F, -0.4F);
        this.shelffungus2.addBox(-3.0F, 0.0F, -1.8F, 6, 2, 4, 0.0F);
        this.setRotateAngle(shelffungus2, 0.0F, 0.5009094953223726F, 0.0F);
        this.shelffungus5.addChild(this.shelffungus5b);
        this.shelffungus1.addChild(this.shelffungus1b);
        this.shelffungus1.addChild(this.shelffungus3);
        this.shelffungus1.addChild(this.shelffungus5);
        this.log1.addChild(this.shelffungus1);
        this.shelffungus1.addChild(this.shelffungus4);
        this.stalk1.addChild(this.cap1);
        this.shelffungus2.addChild(this.shelffungus2b);
        this.stalk2.addChild(this.cap2);
        this.shelffungus1.addChild(this.shelffungus2);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.stalk1.render(f5);
        this.stalk2.render(f5);
        this.cap3.render(f5);
        this.log2.render(f5);
        this.log1.render(f5);
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
