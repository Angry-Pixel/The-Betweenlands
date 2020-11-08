package thebetweenlands.client.render.model.baked.modelbase;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * BLLakeCavernStatuette1 - TripleHeadedSheep
 * Created using Tabula 7.0.1
 */
public class ModelSimulacrumLakeCavern1 extends ModelBase {
    public ModelRenderer base;
    public ModelRenderer stone_main;
    public ModelRenderer stone_top;

    public ModelSimulacrumLakeCavern1() {
        this.textureWidth = 32;
        this.textureHeight = 32;
        this.stone_main = new ModelRenderer(this, 0, 8);
        this.stone_main.setRotationPoint(0.0F, -3.0F, 0.0F);
        this.stone_main.addBox(-2.0F, -12.0F, -1.0F, 4, 12, 2, 0.0F);
        this.stone_top = new ModelRenderer(this, 0, 23);
        this.stone_top.setRotationPoint(0.0F, -10.0F, 0.0F);
        this.stone_top.addBox(-2.0F, -3.0F, -1.0F, 3, 1, 2, 0.0F);
        this.base = new ModelRenderer(this, 0, 0);
        this.base.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.base.addBox(-3.0F, -3.0F, -2.0F, 6, 3, 4, 0.0F);
        this.base.addChild(this.stone_main);
        this.stone_main.addChild(this.stone_top);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.base.render(f5);
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
