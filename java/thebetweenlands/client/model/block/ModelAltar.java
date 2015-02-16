package thebetweenlands.client.model.block;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelAltar
        extends ModelBase
{
    private final ModelRenderer altar1;
    private final ModelRenderer altar2;
    private final ModelRenderer altar3;
    private final ModelRenderer altar4;
    private final ModelRenderer altar5;
    private final ModelRenderer altar6;
    private final ModelRenderer altar7;
    private final ModelRenderer altar8;
    private final ModelRenderer altar9;
    private final ModelRenderer altar10;
    private final ModelRenderer altar11;
    private final ModelRenderer altar12;
    private final ModelRenderer altar13;
    private final ModelRenderer altar14;
    private final ModelRenderer altar15;

    public ModelAltar() {
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
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    public void renderAll() {
        altar1.render(0.0625F);
        altar2.render(0.0625F);
        altar3.render(0.0625F);
        altar4.render(0.0625F);
        altar5.render(0.0625F);
        altar6.render(0.0625F);
        altar7.render(0.0625F);
        altar8.render(0.0625F);
        altar9.render(0.0625F);
        altar10.render(0.0625F);
        altar11.render(0.0625F);
        altar12.render(0.0625F);
        altar13.render(0.0625F);
        altar14.render(0.0625F);
        altar15.render(0.0625F);
    }
}
