package thebetweenlands.client.render.model.baked.modelbase;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * BLLakeCavernStatuette3 - TripleHeadedSheep
 * Created using Tabula 7.0.1
 */
public class ModelSimulacrumLakeCavern3 extends ModelBase {
    public ModelRenderer base;
    public ModelRenderer stone_main;
    public ModelRenderer edge_front;
    public ModelRenderer edge_back;

    public ModelSimulacrumLakeCavern3() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.stone_main = new ModelRenderer(this, 0, 14);
        this.stone_main.setRotationPoint(0.0F, -3.0F, 0.0F);
        this.stone_main.addBox(-4.0F, -6.0F, -4.0F, 8, 6, 8, 0.0F);
        this.edge_front = new ModelRenderer(this, 0, 29);
        this.edge_front.setRotationPoint(0.0F, -5.9F, -2.0F);
        this.edge_front.addBox(-4.0F, -2.0F, -2.0F, 8, 2, 2, 0.0F);
        this.base = new ModelRenderer(this, 0, 0);
        this.base.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.base.addBox(-5.0F, -3.0F, -5.0F, 10, 3, 10, 0.0F);
        this.edge_back = new ModelRenderer(this, 21, 29);
        this.edge_back.setRotationPoint(0.0F, -6.0F, 2.0F);
        this.edge_back.addBox(-4.0F, -2.0F, 0.0F, 8, 2, 2, 0.0F);
        this.base.addChild(this.stone_main);
        this.stone_main.addChild(this.edge_front);
        this.stone_main.addChild(this.edge_back);
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
