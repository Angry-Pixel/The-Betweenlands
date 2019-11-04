package thebetweenlands.client.render.model.baked.modelbase;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelDungeonWallCandle extends ModelBase {
    public ModelRenderer base1;
    public ModelRenderer base2;
    public ModelRenderer holder1;
    public ModelRenderer holder2;
    public ModelRenderer holder3;
    public ModelRenderer candle;
    public ModelRenderer holderside_left;
    public ModelRenderer holderside_right;
    public ModelRenderer wick;
    public ModelRenderer sidebottom_left;
    public ModelRenderer sidebottom_right;

    public ModelDungeonWallCandle() {
        this.textureWidth = 32;
        this.textureHeight = 32;
        this.holder1 = new ModelRenderer(this, 0, 14);
        this.holder1.setRotationPoint(0.0F, 4.0F, -2.0F);
        this.holder1.addBox(-1.0F, -3.0F, -2.0F, 2, 3, 2, 0.0F);
        this.setRotateAngle(holder1, -0.18203784098300857F, 0.0F, 0.0F);
        this.sidebottom_right = new ModelRenderer(this, 20, 14);
        this.sidebottom_right.setRotationPoint(-1.0F, 0.0F, 0.0F);
        this.sidebottom_right.addBox(0.0F, 0.0F, -2.0F, 1, 1, 4, 0.0F);
        this.setRotateAngle(sidebottom_right, 0.0F, 0.0F, -0.18203784098300857F);
        this.holder3 = new ModelRenderer(this, 11, 19);
        this.holder3.setRotationPoint(0.0F, 3.0F, -6.0F);
        this.holder3.addBox(-1.0F, -3.0F, -3.0F, 2, 3, 3, 0.0F);
        this.setRotateAngle(holder3, -0.6373942428283291F, 0.0F, 0.0F);
        this.holderside_left = new ModelRenderer(this, 22, 20);
        this.holderside_left.setRotationPoint(1.0F, 1.0F, -3.0F);
        this.holderside_left.addBox(0.0F, -1.0F, -2.0F, 1, 1, 4, 0.0F);
        this.setRotateAngle(holderside_left, 0.0F, 0.0F, -0.091106186954104F);
        this.holder2 = new ModelRenderer(this, 0, 20);
        this.holder2.setRotationPoint(0.0F, -3.0F, -2.0F);
        this.holder2.addBox(-1.0F, 0.0F, -6.0F, 2, 3, 6, 0.0F);
        this.setRotateAngle(holder2, 0.091106186954104F, 0.0F, 0.0F);
        this.wick = new ModelRenderer(this, 17, 10);
        this.wick.setRotationPoint(0.0F, -7.0F, 0.0F);
        this.wick.addBox(0.0F, -2.0F, -0.5F, 0, 3, 1, 0.0F);
        this.setRotateAngle(wick, -0.045553093477052F, -0.31869712141416456F, 0.22759093446006054F);
        this.holderside_right = new ModelRenderer(this, 9, 14);
        this.holderside_right.setRotationPoint(-1.0F, 1.0F, -3.0F);
        this.holderside_right.addBox(-1.0F, -1.0F, -2.0F, 1, 1, 4, 0.0F);
        this.setRotateAngle(holderside_right, 0.0F, 0.0F, 0.091106186954104F);
        this.sidebottom_left = new ModelRenderer(this, 22, 26);
        this.sidebottom_left.setRotationPoint(1.0F, 0.0F, 0.0F);
        this.sidebottom_left.addBox(-1.0F, 0.0F, -2.0F, 1, 1, 4, 0.0F);
        this.setRotateAngle(sidebottom_left, 0.0F, 0.0F, 0.18203784098300857F);
        this.base1 = new ModelRenderer(this, 0, 0);
        this.base1.setRotationPoint(0.0F, 18.0F, 8.0F);
        this.base1.addBox(-3.0F, 0.0F, -2.0F, 6, 5, 2, 0.0F);
        this.setRotateAngle(base1, 0.091106186954104F, 0.0F, 0.0F);
        this.base2 = new ModelRenderer(this, 0, 8);
        this.base2.setRotationPoint(0.0F, 0.0F, -2.0F);
        this.base2.addBox(-3.0F, -3.0F, 0.0F, 6, 3, 2, 0.0F);
        this.setRotateAngle(base2, -0.18203784098300857F, 0.0F, 0.0F);
        this.candle = new ModelRenderer(this, 17, 0);
        this.candle.setRotationPoint(0.0F, 0.0F, -3.0F);
        this.candle.addBox(-1.5F, -7.0F, -1.5F, 3, 7, 3, 0.0F);
        this.base1.addChild(this.holder1);
        this.holderside_right.addChild(this.sidebottom_right);
        this.holder2.addChild(this.holder3);
        this.holder2.addChild(this.holderside_left);
        this.holder1.addChild(this.holder2);
        this.candle.addChild(this.wick);
        this.holder2.addChild(this.holderside_right);
        this.holderside_left.addChild(this.sidebottom_left);
        this.base1.addChild(this.base2);
        this.holder2.addChild(this.candle);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.base1.render(f5);
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
