package thebetweenlands.client.render.model.baked.modelbase;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * BLBucketFlow - TripleHeadedSheep
 * Created using Tabula 7.0.0
 */
public class ModelRubberTapPouring extends ModelBase {
    public ModelRenderer flow1;
    public ModelRenderer flow2;

    public ModelRubberTapPouring() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.flow1 = new ModelRenderer(this, 0, 0);
        this.flow1.setRotationPoint(0.0F, 8.0F, 12.0F);
        this.flow1.addBox(-0.5F, -1.0F, -6.0F, 1, 1, 6, 0.0F);
        this.setRotateAngle(flow1, 0.091106186954104F, 0.0F, 0.0F);
        this.flow2 = new ModelRenderer(this, 0, 8);
        this.flow2.setRotationPoint(0.0F, -1.0F, -6.0F);
        this.flow2.addBox(-0.51F, 0.0F, 0.0F, 1, 16, 1, 0.0F);
        this.setRotateAngle(flow2, -0.091106186954104F, 0.0F, 0.0F);
        this.flow1.addChild(this.flow2);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.flow1.render(f5);
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
