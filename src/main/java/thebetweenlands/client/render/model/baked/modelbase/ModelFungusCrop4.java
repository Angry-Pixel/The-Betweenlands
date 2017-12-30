package thebetweenlands.client.render.model.baked.modelbase;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * BLCropFungus4 - TripleHeadedSheep
 * Created using Tabula 4.1.1
 */
public class ModelFungusCrop4 extends ModelBase {
    public ModelRenderer stalk1;
    public ModelRenderer hat3;
    public ModelRenderer hat4;
    public ModelRenderer stalk2;
    public ModelRenderer stalkfluff1;
    public ModelRenderer stalk3;
    public ModelRenderer hat1;
    public ModelRenderer hat2;
    public ModelRenderer stalkfluff2;

    public ModelFungusCrop4() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.hat2 = new ModelRenderer(this, 13, 8);
        this.hat2.setRotationPoint(0.0F, -2.0F, 0.0F);
        this.hat2.addBox(-2.0F, -1.0F, -2.0F, 4, 1, 4, 0.0F);
        this.stalk2 = new ModelRenderer(this, 0, 10);
        this.stalk2.setRotationPoint(0.0F, -4.0F, -1.5F);
        this.stalk2.addBox(-1.51F, -5.0F, 0.0F, 3, 5, 3, 0.0F);
        this.setRotateAngle(stalk2, -0.18203784098300857F, 0.0F, 0.0F);
        this.hat1 = new ModelRenderer(this, 13, 0);
        this.hat1.setRotationPoint(0.0F, -4.8F, 1.5F);
        this.hat1.addBox(-2.5F, -2.0F, -2.5F, 5, 2, 5, 0.0F);
        this.setRotateAngle(hat1, -0.091106186954104F, 0.0F, 0.0F);
        this.stalk3 = new ModelRenderer(this, 0, 19);
        this.stalk3.setRotationPoint(0.0F, -5.0F, 0.0F);
        this.stalk3.addBox(-1.52F, -5.0F, 0.0F, 3, 5, 3, 0.0F);
        this.setRotateAngle(stalk3, -0.18203784098300857F, 0.0F, 0.0F);
        this.stalkfluff1 = new ModelRenderer(this, 34, 0);
        this.stalkfluff1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.stalkfluff1.addBox(-2.0F, -4.0F, -2.0F, 4, 6, 4, 0.0F);
        this.hat4 = new ModelRenderer(this, 22, 14);
        this.hat4.setRotationPoint(4.0F, 24.0F, -3.0F);
        this.hat4.addBox(-1.0F, -1.0F, -1.0F, 2, 3, 2, 0.0F);
        this.setRotateAngle(hat4, 0.18203784098300857F, 0.27314402793711257F, 0.136659280431156F);
        this.stalk1 = new ModelRenderer(this, 0, 0);
        this.stalk1.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.stalk1.addBox(-1.5F, -4.0F, -1.5F, 3, 6, 3, 0.0F);
        this.setRotateAngle(stalk1, 0.4553564018453205F, 0.31869712141416456F, 0.0F);
        this.stalkfluff2 = new ModelRenderer(this, 34, 11);
        this.stalkfluff2.setRotationPoint(0.0F, -4.0F, -2.0F);
        this.stalkfluff2.addBox(-2.01F, -3.0F, 0.0F, 4, 3, 4, 0.0F);
        this.setRotateAngle(stalkfluff2, -0.136659280431156F, 0.0F, 0.0F);
        this.hat3 = new ModelRenderer(this, 13, 14);
        this.hat3.setRotationPoint(5.0F, 24.0F, 1.0F);
        this.hat3.addBox(-1.0F, -2.0F, -1.0F, 2, 4, 2, 0.0F);
        this.setRotateAngle(hat3, 0.136659280431156F, -0.27314402793711257F, 0.136659280431156F);
        this.hat1.addChild(this.hat2);
        this.stalk1.addChild(this.stalk2);
        this.stalk3.addChild(this.hat1);
        this.stalk2.addChild(this.stalk3);
        this.stalk1.addChild(this.stalkfluff1);
        this.stalkfluff1.addChild(this.stalkfluff2);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.hat4.render(f5);
        this.stalk1.render(f5);
        this.hat3.render(f5);
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
