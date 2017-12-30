package thebetweenlands.client.render.model.baked.modelbase;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * BLCropFungus2 - TripleHeadedSheep
 * Created using Tabula 4.1.1
 */
public class ModelFungusCrop2 extends ModelBase {
    public ModelRenderer stalk1;
    public ModelRenderer stalk2;
    public ModelRenderer hat1;

    public ModelFungusCrop2() {
        this.textureWidth = 32;
        this.textureHeight = 16;
        this.hat1 = new ModelRenderer(this, 9, 0);
        this.hat1.setRotationPoint(0.0F, -2.8F, 1.0F);
        this.hat1.addBox(-1.5F, -2.0F, -1.5F, 3, 2, 3, 0.0F);
        this.setRotateAngle(hat1, -0.136659280431156F, 0.0F, 0.0F);
        this.stalk2 = new ModelRenderer(this, 0, 8);
        this.stalk2.setRotationPoint(0.0F, -3.0F, -1.0F);
        this.stalk2.addBox(-1.01F, -3.0F, 0.0F, 2, 3, 2, 0.0F);
        this.setRotateAngle(stalk2, -0.18203784098300857F, 0.0F, 0.0F);
        this.stalk1 = new ModelRenderer(this, 0, 0);
        this.stalk1.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.stalk1.addBox(-1.0F, -3.0F, -1.0F, 2, 5, 2, 0.0F);
        this.setRotateAngle(stalk1, 0.36425021489121656F, 0.31869712141416456F, 0.0F);
        this.stalk2.addChild(this.hat1);
        this.stalk1.addChild(this.stalk2);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.stalk1.render(f5);
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
