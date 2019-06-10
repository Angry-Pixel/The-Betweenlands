package thebetweenlands.client.render.model.tile;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelLootUrn2 extends ModelBase {
    public ModelRenderer stand;
    public ModelRenderer base;
    public ModelRenderer leg1;
    public ModelRenderer leg2;
    public ModelRenderer leg3;
    public ModelRenderer leg4;
    public ModelRenderer midpiece;
    public ModelRenderer top;

    public ModelLootUrn2() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.leg2 = new ModelRenderer(this, 9, 11);
        this.leg2.setRotationPoint(-2.0F, 0.0F, -2.0F);
        this.leg2.addBox(-2.0F, 0.0F, -2.0F, 2, 5, 2, 0.0F);
        this.setRotateAngle(leg2, -0.18203784098300857F, 0.01815142422074103F, 0.18203784098300857F);
        this.leg3 = new ModelRenderer(this, 18, 11);
        this.leg3.setRotationPoint(-2.0F, 0.0F, 2.0F);
        this.leg3.addBox(-2.0F, 0.0F, 0.0F, 2, 5, 2, 0.0F);
        this.setRotateAngle(leg3, 0.18203784098300857F, -0.01815142422074103F, 0.18203784098300857F);
        this.base = new ModelRenderer(this, 0, 19);
        this.base.setRotationPoint(0.0F, 21.5F, 0.0F);
        this.base.addBox(-3.0F, -4.0F, -3.0F, 6, 4, 6, 0.0F);
        this.setRotateAngle(base, 0.0F, 0.091106186954104F, 0.045553093477052F);
        this.leg1 = new ModelRenderer(this, 0, 11);
        this.leg1.setRotationPoint(2.0F, 0.0F, -2.0F);
        this.leg1.addBox(0.0F, 0.0F, -2.0F, 2, 5, 2, 0.0F);
        this.setRotateAngle(leg1, -0.18203784098300857F, -0.01815142422074103F, -0.18203784098300857F);
        this.leg4 = new ModelRenderer(this, 27, 11);
        this.leg4.setRotationPoint(2.0F, 0.0F, 2.0F);
        this.leg4.addBox(0.0F, 0.0F, 0.0F, 2, 5, 2, 0.0F);
        this.setRotateAngle(leg4, 0.18203784098300857F, 0.01815142422074103F, -0.18203784098300857F);
        this.stand = new ModelRenderer(this, 0, 0);
        this.stand.setRotationPoint(0.0F, 20.0F, 0.0F);
        this.stand.addBox(-4.0F, -2.0F, -4.0F, 8, 2, 8, 0.0F);
        this.midpiece = new ModelRenderer(this, 28, 17);
        this.midpiece.setRotationPoint(0.0F, -4.0F, 0.0F);
        this.midpiece.addBox(-4.0F, -6.0F, -4.0F, 8, 6, 8, 0.0F);
        this.top = new ModelRenderer(this, 33, 0);
        this.top.setRotationPoint(0.0F, -10.0F, 0.0F);
        this.top.addBox(-3.0F, -2.0F, -3.0F, 6, 2, 6, 0.0F);
        this.stand.addChild(this.leg2);
        this.stand.addChild(this.leg3);
        this.stand.addChild(this.leg1);
        this.stand.addChild(this.leg4);
        this.base.addChild(this.midpiece);
        this.base.addChild(this.top);
    }

    public void render() { 
        this.base.render(0.0625F);
        this.stand.render(0.0625F);
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
