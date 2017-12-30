package thebetweenlands.client.render.model.baked.modelbase;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * BLCropFungus1 - TripleHeadedSheep
 * Created using Tabula 4.1.1
 */
public class ModelFungusCrop1 extends ModelBase {
    public ModelRenderer stalk1;
    public ModelRenderer hat1;

    public ModelFungusCrop1() {
        this.textureWidth = 32;
        this.textureHeight = 16;
        this.hat1 = new ModelRenderer(this, 0, 7);
        this.hat1.setRotationPoint(0.0F, -2.0F, -1.0F);
        this.hat1.addBox(-1.01F, -2.0F, 0.0F, 2, 2, 2, 0.0F);
        this.setRotateAngle(hat1, -0.18203784098300857F, 0.0F, 0.0F);
        this.stalk1 = new ModelRenderer(this, 0, 0);
        this.stalk1.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.stalk1.addBox(-1.0F, -2.0F, -1.0F, 2, 4, 2, 0.0F);
        this.setRotateAngle(stalk1, 0.27314402793711257F, 0.31869712141416456F, 0.0F);
        this.stalk1.addChild(this.hat1);
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
