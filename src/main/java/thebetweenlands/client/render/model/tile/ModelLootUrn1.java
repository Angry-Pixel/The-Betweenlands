package thebetweenlands.client.render.model.tile;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelLootUrn1 extends ModelBase {
    public ModelRenderer base;
    public ModelRenderer midpiece;
    public ModelRenderer top;
    public ModelRenderer thingy1;
    public ModelRenderer thingy2;

    public ModelLootUrn1() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.base = new ModelRenderer(this, 0, 0);
        this.base.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.base.addBox(-3.0F, -3.0F, -3.0F, 6, 3, 6, 0.0F);
        this.thingy1 = new ModelRenderer(this, 25, 9);
        this.thingy1.setRotationPoint(0.0F, -2.0F, 0.0F);
        this.thingy1.addBox(-3.0F, -1.0F, 1.0F, 6, 1, 2, 0.0F);
        this.top = new ModelRenderer(this, 25, 0);
        this.top.setRotationPoint(0.0F, 14.0F, 0.0F);
        this.top.addBox(-3.0F, -2.0F, -3.0F, 6, 2, 6, 0.0F);
        this.setRotateAngle(top, 0.0F, 0.091106186954104F, 0.0F);
        this.midpiece = new ModelRenderer(this, 0, 10);
        this.midpiece.setRotationPoint(0.0F, 21.0F, 0.0F);
        this.midpiece.addBox(-4.0F, -7.0F, -4.0F, 8, 7, 8, 0.0F);
        this.thingy2 = new ModelRenderer(this, 25, 13);
        this.thingy2.setRotationPoint(0.0F, -2.0F, 0.0F);
        this.thingy2.addBox(-3.0F, -1.0F, -3.0F, 6, 1, 2, 0.0F);
        this.top.addChild(this.thingy1);
        this.top.addChild(this.thingy2);
    }

    public void render() { 
        this.base.render(0.0625F);
        this.top.render(0.0625F);
        this.midpiece.render(0.0625F);
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
