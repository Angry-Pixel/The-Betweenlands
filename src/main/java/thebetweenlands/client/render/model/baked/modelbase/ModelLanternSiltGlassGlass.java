package thebetweenlands.client.render.model.baked.modelbase;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * BLLanternSiltGlass_inner - TripleHeadedSheep
 * Created using Tabula 7.0.1
 */
public class ModelLanternSiltGlassGlass extends ModelBase {
    public ModelRenderer lamp_base;

    public ModelLanternSiltGlassGlass() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.lamp_base = new ModelRenderer(this, 0, 0);
        this.lamp_base.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.lamp_base.addBox(-2.5F, 0.0F, -2.5F, 5, 6, 5, 0.0F);
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
