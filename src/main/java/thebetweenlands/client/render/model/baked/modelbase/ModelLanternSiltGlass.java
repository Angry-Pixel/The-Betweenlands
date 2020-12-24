package thebetweenlands.client.render.model.baked.modelbase;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * BLLanternSiltGlass - TripleHeadedSheep
 * Created using Tabula 7.0.1
 */
public class ModelLanternSiltGlass extends ModelBase {
    public ModelRenderer lamp_base;
    public ModelRenderer bottom_mid;
    public ModelRenderer top_mid;
    public ModelRenderer bottom_left;
    public ModelRenderer bottom_right;
    public ModelRenderer top_left;
    public ModelRenderer top_right;
    public ModelRenderer handle;

    public ModelLanternSiltGlass() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.top_right = new ModelRenderer(this, 46, 17);
        this.top_right.setRotationPoint(-2.0F, -2.0F, 0.0F);
        this.top_right.addBox(-2.0F, 0.0F, -2.99F, 2, 2, 6, 0.0F);
        this.setRotateAngle(top_right, 0.0F, 0.0F, -0.31869712141416456F);
        this.bottom_left = new ModelRenderer(this, 0, 21);
        this.bottom_left.setRotationPoint(2.0F, 2.0F, 0.0F);
        this.bottom_left.addBox(0.0F, -2.0F, -2.99F, 2, 2, 6, 0.0F);
        this.setRotateAngle(bottom_left, 0.0F, 0.0F, -0.4553564018453205F);
        this.bottom_right = new ModelRenderer(this, 17, 21);
        this.bottom_right.setRotationPoint(-2.0F, 2.0F, 0.0F);
        this.bottom_right.addBox(-2.0F, -2.0F, -2.99F, 2, 2, 6, 0.0F);
        this.setRotateAngle(bottom_right, 0.0F, 0.0F, 0.4553564018453205F);
        this.handle = new ModelRenderer(this, 17, 0);
        this.handle.setRotationPoint(0.0F, -2.0F, 0.0F);
        this.handle.addBox(-2.0F, -3.0F, 0.0F, 4, 3, 0, 0.0F);
        this.setRotateAngle(handle, 0.8196066167365371F, 0.0F, 0.0F);
        this.top_mid = new ModelRenderer(this, 21, 12);
        this.top_mid.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.top_mid.addBox(-2.0F, -2.0F, -3.0F, 4, 2, 6, 0.0F);
        this.top_left = new ModelRenderer(this, 34, 21);
        this.top_left.setRotationPoint(2.0F, -2.0F, 0.0F);
        this.top_left.addBox(0.0F, 0.0F, -2.99F, 2, 2, 6, 0.0F);
        this.setRotateAngle(top_left, 0.0F, 0.0F, 0.31869712141416456F);
        this.bottom_mid = new ModelRenderer(this, 0, 12);
        this.bottom_mid.setRotationPoint(0.0F, 6.0F, 0.0F);
        this.bottom_mid.addBox(-2.0F, 0.0F, -3.0F, 4, 2, 6, 0.0F);
        this.lamp_base = new ModelRenderer(this, 0, 0);
        this.lamp_base.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.lamp_base.addBox(-2.5F, 0.0F, -2.5F, 5, 6, 5, 0.0F);
        this.top_mid.addChild(this.top_right);
        this.bottom_mid.addChild(this.bottom_left);
        this.bottom_mid.addChild(this.bottom_right);
        this.top_mid.addChild(this.handle);
        this.lamp_base.addChild(this.top_mid);
        this.top_mid.addChild(this.top_left);
        this.lamp_base.addChild(this.bottom_mid);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.lamp_base.render(f5);
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
