package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * ModelSmollSludge - TripleHeadedSheep
 * Created using Tabula 7.0.0
 */
public class ModelSmollSludge extends ModelBase {
    public ModelRenderer skullbase;
    public ModelRenderer sludge1;
    public ModelRenderer skull2;
    public ModelRenderer jaw;
    public ModelRenderer sludge2;
    public ModelRenderer sludge3;

    public ModelSmollSludge() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.sludge3 = new ModelRenderer(this, 36, 15);
        this.sludge3.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.sludge3.addBox(-3.5F, 3.5F, -3.5F, 7, 1, 7, 0.0F);
        this.sludge1 = new ModelRenderer(this, 0, 16);
        this.sludge1.setRotationPoint(0.0F, 19.5F, 0.0F);
        this.sludge1.addBox(-4.5F, -3.5F, -4.5F, 9, 7, 9, 0.0F);
        this.sludge2 = new ModelRenderer(this, 36, 24);
        this.sludge2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.sludge2.addBox(-3.5F, -4.5F, -3.5F, 7, 1, 7, 0.0F);
        this.skull2 = new ModelRenderer(this, 0, 8);
        this.skull2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.skull2.addBox(-1.5F, 0.0F, 0.0F, 3, 1, 1, 0.0F);
        this.skullbase = new ModelRenderer(this, 0, 0);
        this.skullbase.setRotationPoint(0.0F, 20.5F, 0.0F);
        this.skullbase.addBox(-2.0F, -3.0F, -3.0F, 4, 3, 4, 0.0F);
        this.setRotateAngle(skullbase, -0.5462880558742251F, 0.0F, -0.18203784098300857F);
        this.jaw = new ModelRenderer(this, 0, 11);
        this.jaw.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.jaw.addBox(-2.0F, 0.0F, -3.0F, 4, 1, 3, 0.0F);
        this.setRotateAngle(jaw, 0.40980330836826856F, 0.0F, 0.091106186954104F);
        this.sludge1.addChild(this.sludge3);
        this.sludge1.addChild(this.sludge2);
        this.skullbase.addChild(this.skull2);
        this.skull2.addChild(this.jaw);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.sludge1.render(f5);
        this.skullbase.render(f5);
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
