package thebetweenlands.client.render.model.tile;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelDruidAltar extends ModelBase {
    ModelRenderer altar1;
    ModelRenderer altar2;
    ModelRenderer altar3;
    ModelRenderer altar4;
    ModelRenderer altar5;
    ModelRenderer altar6;
    ModelRenderer altar7;
    ModelRenderer altar8;
    ModelRenderer altar9;
    ModelRenderer altar10;
    ModelRenderer altar11;
    ModelRenderer altar12;
    ModelRenderer altar13;
    ModelRenderer altar14;
    ModelRenderer altar15;
    ModelRenderer grass;
    ModelRenderer shroom1;
    ModelRenderer shroom2;
    ModelRenderer shroom3;
    ModelRenderer shroom4;
    ModelRenderer shroom5;
    ModelRenderer shroom6;

    public ModelDruidAltar() {
        textureWidth = 256;
        textureHeight = 128;

        altar1 = new ModelRenderer(this, 0, 0);
        altar1.addBox(-16F, 0F, -16F, 32, 3, 32);
        altar1.setRotationPoint(0F, 21F, 0F);
        setRotation(altar1, 0F, 0F, 0F);
        altar2 = new ModelRenderer(this, 0, 35);
        altar2.addBox(-13F, -6F, -13F, 26, 6, 26);
        altar2.setRotationPoint(0F, 21F, 0F);
        setRotation(altar2, 0F, 0F, 0F);
        altar3 = new ModelRenderer(this, 0, 68);
        altar3.addBox(-15F, -9F, -15F, 30, 3, 30);
        altar3.setRotationPoint(0F, 21F, 0F);
        setRotation(altar3, 0F, 0F, 0F);
        altar4 = new ModelRenderer(this, 0, 102);
        altar4.addBox(-14F, -11F, -14F, 28, 2, 9);
        altar4.setRotationPoint(0F, 21F, 0F);
        setRotation(altar4, 0F, 0F, 0F);
        altar5 = new ModelRenderer(this, 0, 115);
        altar5.addBox(-14F, -11F, 5F, 28, 2, 9);
        altar5.setRotationPoint(0F, 21F, 0F);
        setRotation(altar5, 0F, 0F, 0F);
        altar6 = new ModelRenderer(this, 76, 102);
        altar6.addBox(5F, -11F, -5F, 9, 2, 10);
        altar6.setRotationPoint(0F, 21F, 0F);
        setRotation(altar6, 0F, 0F, 0F);
        altar7 = new ModelRenderer(this, 76, 116);
        altar7.addBox(-14F, -11F, -5F, 9, 2, 10);
        altar7.setRotationPoint(0F, 21F, 0F);
        setRotation(altar7, 0F, 0F, 0F);
        altar8 = new ModelRenderer(this, 129, 0);
        altar8.addBox(-17F, -1F, -17F, 6, 4, 6);
        altar8.setRotationPoint(0F, 21F, 0F);
        setRotation(altar8, 0F, 0F, 0F);
        altar9 = new ModelRenderer(this, 129, 11);
        altar9.addBox(11F, -1F, -17F, 6, 4, 6);
        altar9.setRotationPoint(0F, 21F, 0F);
        setRotation(altar9, 0F, 0F, 0F);
        altar10 = new ModelRenderer(this, 129, 22);
        altar10.addBox(11F, -1F, 11F, 6, 4, 6);
        altar10.setRotationPoint(0F, 21F, 0F);
        setRotation(altar10, 0F, 0F, 0F);
        altar11 = new ModelRenderer(this, 129, 34);
        altar11.addBox(-17F, -1F, 11F, 6, 4, 6);
        altar11.setRotationPoint(0F, 21F, 0F);
        setRotation(altar11, 0F, 0F, 0F);
        altar12 = new ModelRenderer(this, 129, 47);
        altar12.addBox(-8F, -12F, -8F, 16, 1, 3);
        altar12.setRotationPoint(0F, 21F, 0F);
        setRotation(altar12, 0F, 0F, 0F);
        altar13 = new ModelRenderer(this, 129, 53);
        altar13.addBox(-8F, -12F, 5F, 16, 1, 3);
        altar13.setRotationPoint(0F, 21F, 0F);
        setRotation(altar13, 0F, 0F, 0F);
        altar14 = new ModelRenderer(this, 129, 59);
        altar14.addBox(-8F, -12F, -5F, 3, 1, 10);
        altar14.setRotationPoint(0F, 21F, 0F);
        setRotation(altar14, 0F, 0F, 0F);
        altar15 = new ModelRenderer(this, 129, 72);
        altar15.addBox(5F, -12F, -5F, 3, 1, 10);
        altar15.setRotationPoint(0F, 21F, 0F);
        setRotation(altar15, 0F, 0F, 0F);
        grass = new ModelRenderer(this, 190, 0);
        grass.addBox(0F, 0F, -9F, 0, 4, 18);
        grass.setRotationPoint(15F, 15F, 3F);
        setRotation(grass, 0F, 0F, 0F);
        shroom1 = new ModelRenderer(this, 190, 0);
        shroom1.addBox(0F, -3F, 0F, 1, 3, 1);
        shroom1.setRotationPoint(8F, 22F, -14F);
        setRotation(shroom1, 0.3346075F, 0F, 0.2602503F);
        shroom2 = new ModelRenderer(this, 190, 6);
        shroom2.addBox(-0.5F, -4.5F, -2F, 3, 2, 3);
        shroom2.setRotationPoint(8F, 22F, -14F);
        setRotation(shroom2, 0.0743572F, 0F, 0F);
        shroom3 = new ModelRenderer(this, 196, 0);
        shroom3.addBox(0F, -2.5F, 0F, 1, 3, 1);
        shroom3.setRotationPoint(5F, 22F, -14F);
        setRotation(shroom3, 0.4833219F, 0.4089647F, 0F);
        shroom4 = new ModelRenderer(this, 203, 6);
        shroom4.addBox(-0.5F, -3F, -1.5F, 2, 1, 2);
        shroom4.setRotationPoint(5F, 22F, -14F);
        setRotation(shroom4, 0F, 0.4089656F, 0F);
        shroom5 = new ModelRenderer(this, 202, 0);
        shroom5.addBox(-1F, -2F, -1F, 1, 2, 1);
        shroom5.setRotationPoint(8F, 24F, -16F);
        setRotation(shroom5, 0.3346075F, -0.2230717F, 0F);
        shroom6 = new ModelRenderer(this, 203, 12);
        shroom6.addBox(-1.5F, -2F, -2F, 2, 1, 2);
        shroom6.setRotationPoint(8F, 24F, -16F);
        setRotation(shroom6, 0F, -0.2230705F, 0F);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    public void renderAll(float scale) {
        altar1.render(scale);
        altar2.render(scale);
        altar3.render(scale);
        altar4.render(scale);
        altar5.render(scale);
        altar6.render(scale);
        altar7.render(scale);
        altar8.render(scale);
        altar9.render(scale);
        altar10.render(scale);
        altar11.render(scale);
        altar12.render(scale);
        altar13.render(scale);
        altar14.render(scale);
        altar15.render(scale);
        grass.render(scale);
        shroom1.render(scale);
        shroom2.render(scale);
        shroom3.render(scale);
        shroom4.render(scale);
        shroom5.render(scale);
        shroom6.render(scale);
    }
}
