package thebetweenlands.client.render.model.baked.modelbase;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * BLBlueCappedMushroom - TripleHeadedSheep
 * Created using Tabula 4.1.1
 */
public class ModelBulbCappedMushroom extends ModelBase {
    public ModelRenderer stalk1;
    public ModelRenderer stalk2;
    public ModelRenderer stalk3;
    public ModelRenderer stalk4;
    public ModelRenderer stalk5;
    public ModelRenderer cap6;
    public ModelRenderer cap7;
    public ModelRenderer cap8;
    public ModelRenderer cap1;
    public ModelRenderer cap2;
    public ModelRenderer cap3;
    public ModelRenderer cap4;
    public ModelRenderer cap5;

    public ModelBulbCappedMushroom() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.cap1 = new ModelRenderer(this, 9, 0);
        this.cap1.setRotationPoint(0.0F, -3.5F, 0.0F);
        this.cap1.addBox(-1.5F, -3.0F, -1.5F, 3, 3, 3, 0.0F);
        this.setRotateAngle(cap1, 0.091106186954104F, 0.0F, -0.136659280431156F);
        this.cap5 = new ModelRenderer(this, 22, 14);
        this.cap5.setRotationPoint(0.0F, -2.5F, 0.0F);
        this.cap5.addBox(-1.5F, -3.0F, -1.5F, 3, 3, 3, 0.0F);
        this.setRotateAngle(cap5, 0.045553093477052F, 0.0F, 0.091106186954104F);
        this.stalk1 = new ModelRenderer(this, 0, 0);
        this.stalk1.setRotationPoint(4.0F, 24.0F, 4.0F);
        this.stalk1.addBox(-1.0F, -4.0F, -1.0F, 2, 6, 2, 0.0F);
        this.setRotateAngle(stalk1, -0.136659280431156F, -0.27314402793711257F, 0.18203784098300857F);
        this.cap4 = new ModelRenderer(this, 22, 7);
        this.cap4.setRotationPoint(0.0F, -1.5F, 0.0F);
        this.cap4.addBox(-1.5F, -3.0F, -1.5F, 3, 3, 3, 0.0F);
        this.setRotateAngle(cap4, -0.091106186954104F, 0.0F, -0.136659280431156F);
        this.stalk2 = new ModelRenderer(this, 0, 9);
        this.stalk2.setRotationPoint(-2.5F, 24.0F, -5.0F);
        this.stalk2.addBox(-1.0F, -2.0F, -1.0F, 2, 4, 2, 0.0F);
        this.setRotateAngle(stalk2, 0.136659280431156F, -0.045553093477052F, -0.091106186954104F);
        this.cap8 = new ModelRenderer(this, 35, 0);
        this.cap8.setRotationPoint(0.0F, 24.0F, 5.5F);
        this.cap8.addBox(-1.5F, -2.5F, -1.5F, 3, 3, 3, 0.0F);
        this.setRotateAngle(cap8, -0.136659280431156F, 0.31869712141416456F, -0.045553093477052F);
        this.cap6 = new ModelRenderer(this, 22, 21);
        this.cap6.setRotationPoint(-5.0F, 24.0F, 0.0F);
        this.cap6.addBox(-1.0F, -1.5F, -1.0F, 2, 2, 2, 0.0F);
        this.setRotateAngle(cap6, -0.18203784098300857F, -0.31869712141416456F, -0.136659280431156F);
        this.cap3 = new ModelRenderer(this, 22, 0);
        this.cap3.setRotationPoint(0.0F, -2.5F, 0.0F);
        this.cap3.addBox(-1.5F, -3.0F, -1.5F, 3, 3, 3, 0.0F);
        this.setRotateAngle(cap3, 0.136659280431156F, 0.0F, 0.091106186954104F);
        this.stalk4 = new ModelRenderer(this, 0, 24);
        this.stalk4.setRotationPoint(5.5F, 24.0F, -4.5F);
        this.stalk4.addBox(-1.0F, -2.0F, -1.0F, 2, 4, 2, 0.0F);
        this.setRotateAngle(stalk4, 0.27314402793711257F, 0.31869712141416456F, 0.18203784098300857F);
        this.cap7 = new ModelRenderer(this, 22, 26);
        this.cap7.setRotationPoint(4.5F, 24.0F, 0.5F);
        this.cap7.addBox(-1.0F, -1.5F, -1.0F, 2, 2, 2, 0.0F);
        this.setRotateAngle(cap7, -0.091106186954104F, 0.18203784098300857F, 0.18203784098300857F);
        this.stalk3 = new ModelRenderer(this, 0, 16);
        this.stalk3.setRotationPoint(-4.0F, 24.0F, 3.5F);
        this.stalk3.addBox(-1.0F, -3.0F, -1.0F, 2, 5, 2, 0.0F);
        this.setRotateAngle(stalk3, -0.136659280431156F, -0.136659280431156F, -0.091106186954104F);
        this.cap2 = new ModelRenderer(this, 9, 7);
        this.cap2.setRotationPoint(0.0F, -1.5F, 0.0F);
        this.cap2.addBox(-1.5F, -3.0F, -1.5F, 3, 3, 3, 0.0F);
        this.setRotateAngle(cap2, -0.091106186954104F, 0.0F, 0.091106186954104F);
        this.stalk5 = new ModelRenderer(this, 9, 16);
        this.stalk5.setRotationPoint(1.5F, 24.0F, -1.0F);
        this.stalk5.addBox(-1.0F, -3.0F, -1.0F, 2, 5, 2, 0.0F);
        this.setRotateAngle(stalk5, -0.045553093477052F, 0.0F, -0.136659280431156F);
        this.stalk1.addChild(this.cap1);
        this.stalk5.addChild(this.cap5);
        this.stalk4.addChild(this.cap4);
        this.stalk3.addChild(this.cap3);
        this.stalk2.addChild(this.cap2);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.stalk1.render(f5);
        this.stalk2.render(f5);
        this.cap8.render(f5);
        this.cap6.render(f5);
        this.stalk4.render(f5);
        this.cap7.render(f5);
        this.stalk3.render(f5);
        this.stalk5.render(f5);
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
