package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
@SideOnly(Side.CLIENT)
public class ModelShieldCog extends ModelBase {
    ModelRenderer cog2_1;
    ModelRenderer cog2_2;
    ModelRenderer cog2_fill;
    ModelRenderer cog2_3;
    ModelRenderer cog2_4;
    ModelRenderer cog2_5;
    ModelRenderer cog2_6;
    ModelRenderer cog2_7;
    ModelRenderer cog2_8;
    ModelRenderer cog2_9;
    ModelRenderer cog2_10;
    ModelRenderer cog2_11;
    ModelRenderer cog2_12;

    public ModelShieldCog() {
        textureWidth = 256;
        textureHeight = 256;
        cog2_10 = new ModelRenderer(this, 146, 121);
        cog2_10.setRotationPoint(-0.01F, 0.0F, -4.0F);
        cog2_10.addBox(0.0F, -3.0F, -4.0F, 3, 3, 4, 0.0F);
        setRotateAngle(cog2_10, -0.5235987755982988F, 0.0F, 0.0F);
        cog2_11 = new ModelRenderer(this, 146, 129);
        cog2_11.setRotationPoint(-0.01F, 0.0F, -4.0F);
        cog2_11.addBox(0.0F, -3.0F, -4.0F, 3, 4, 4, 0.0F);
        setRotateAngle(cog2_11, -0.5235987755982988F, 0.0F, 0.0F);
        cog2_12 = new ModelRenderer(this, 146, 138);
        cog2_12.setRotationPoint(-0.01F, 0.0F, -4.0F);
        cog2_12.addBox(0.0F, -3.0F, -4.0F, 3, 3, 4, 0.0F);
        setRotateAngle(cog2_12, -0.5235987755982988F, 0.0F, 0.0F);
        cog2_1 = new ModelRenderer(this, 146, 44);
        cog2_1.setRotationPoint(0.0F, 15.5F, 0.0F);
        cog2_1.addBox(-1.5F, 4.5F, -2.0F, 3, 4, 4, 0.0F);
        setRotateAngle(cog2_1, 0.0F, 1.5707963267948966F, 0.0F);
        cog2_6 = new ModelRenderer(this, 146, 87);
        cog2_6.setRotationPoint(0.01F, 0.0F, -4.0F);
        cog2_6.addBox(0.0F, -3.0F, -4.0F, 3, 3, 4, 0.0F);
        setRotateAngle(cog2_6, -0.5235987755982988F, 0.0F, 0.0F);
        cog2_9 = new ModelRenderer(this, 146, 112);
        cog2_9.setRotationPoint(-0.01F, 0.0F, -4.0F);
        cog2_9.addBox(0.0F, -3.0F, -4.0F, 3, 4, 4, 0.0F);
        setRotateAngle(cog2_9, -0.5235987755982988F, 0.0F, 0.0F);
        cog2_8 = new ModelRenderer(this, 146, 104);
        cog2_8.setRotationPoint(-0.01F, 0.0F, -4.0F);
        cog2_8.addBox(0.0F, -3.0F, -4.0F, 3, 3, 4, 0.0F);
        setRotateAngle(cog2_8, -0.5235987755982988F, 0.0F, 0.0F);
        cog2_7 = new ModelRenderer(this, 146, 95);
        cog2_7.setRotationPoint(0.01F, 0.0F, -4.0F);
        cog2_7.addBox(0.0F, -3.0F, -4.0F, 3, 4, 4, 0.0F);
        setRotateAngle(cog2_7, -0.5235987755982988F, 0.0F, 0.0F);
        cog2_fill = new ModelRenderer(this, 146, 146);
        cog2_fill.setRotationPoint(0.0F, 0.0F, 0.0F);
        cog2_fill.addBox(-1.0F, -4.5F, -4.5F, 2, 9, 9, 0.0F);
        cog2_4 = new ModelRenderer(this, 146, 70);
        cog2_4.setRotationPoint(0.01F, 0.0F, -4.0F);
        cog2_4.addBox(0.0F, -3.0F, -4.0F, 3, 3, 4, 0.0F);
        setRotateAngle(cog2_4, -0.5235987755982988F, 0.0F, 0.0F);
        cog2_5 = new ModelRenderer(this, 146, 78);
        cog2_5.setRotationPoint(0.01F, 0.0F, -4.0F);
        cog2_5.addBox(0.0F, -3.0F, -4.0F, 3, 4, 4, 0.0F);
        setRotateAngle(cog2_5, -0.5235987755982988F, 0.0F, 0.0F);
        cog2_2 = new ModelRenderer(this, 146, 53);
        cog2_2.setRotationPoint(-1.49F, 7.5F, -2.0F);
        cog2_2.addBox(0.0F, -3.0F, -4.0F, 3, 3, 4, 0.0F);
        setRotateAngle(cog2_2, -0.5235987755982988F, 0.0F, 0.0F);
        cog2_3 = new ModelRenderer(this, 146, 61);
        cog2_3.setRotationPoint(0.01F, 0.0F, -4.0F);
        cog2_3.addBox(0.0F, -3.0F, -4.0F, 3, 4, 4, 0.0F);
        setRotateAngle(cog2_3, -0.5235987755982988F, 0.0F, 0.0F);
        cog2_9.addChild(cog2_10);
        cog2_10.addChild(cog2_11);
        cog2_11.addChild(cog2_12);
        cog2_5.addChild(cog2_6);
        cog2_8.addChild(cog2_9);
        cog2_7.addChild(cog2_8);
        cog2_6.addChild(cog2_7);
        cog2_1.addChild(cog2_fill);
        cog2_3.addChild(cog2_4);
        cog2_4.addChild(cog2_5);
        cog2_1.addChild(cog2_2);
        cog2_2.addChild(cog2_3);
    }

    public void render(float scale) { 
        cog2_1.render(scale);
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
