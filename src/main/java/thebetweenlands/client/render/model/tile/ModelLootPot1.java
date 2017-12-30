package thebetweenlands.client.render.model.tile;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelLootPot1 extends ModelBase {
    ModelRenderer foot;
    ModelRenderer cupside1;
    ModelRenderer cupside2;
    ModelRenderer cupside3;
    ModelRenderer cupside4;
    ModelRenderer lid;
    ModelRenderer lidtop;

    public ModelLootPot1() {
        textureWidth = 128;
        textureHeight = 64;
        cupside2 = new ModelRenderer(this, 29, 13);
        cupside2.setRotationPoint(0.0F, -2.0F, -4.0F);
        cupside2.addBox(-4.0F, -10.0F, -2.0F, 8, 10, 2, 0.0F);
        lidtop = new ModelRenderer(this, 64, 0);
        lidtop.setRotationPoint(0.0F, -2.0F, 0.0F);
        lidtop.addBox(-2.0F, -2.0F, -2.0F, 4, 2, 4, 0.0F);
        foot = new ModelRenderer(this, 0, 0);
        foot.setRotationPoint(0.0F, 24.0F, 0.0F);
        foot.addBox(-4.0F, -4.0F, -4.0F, 8, 4, 8, 0.0F);
        cupside4 = new ModelRenderer(this, 29, 26);
        cupside4.setRotationPoint(0.0F, -2.0F, 4.0F);
        cupside4.addBox(-4.0F, -10.0F, 0.0F, 8, 10, 2, 0.0F);
        cupside3 = new ModelRenderer(this, 0, 36);
        cupside3.setRotationPoint(4.0F, -2.0F, 0.0F);
        cupside3.addBox(0.0F, -10.0F, -6.0F, 2, 10, 12, 0.0F);
        lid = new ModelRenderer(this, 33, 0);
        lid.setRotationPoint(0.0F, -12.0F, 0.0F);
        lid.addBox(-5.0F, -2.0F, -5.0F, 10, 2, 10, 0.0F);
        setRotateAngle(lid, 0.0F, 0.045553093477052F, 0.0F);
        cupside1 = new ModelRenderer(this, 0, 13);
        cupside1.setRotationPoint(-4.0F, -2.0F, 0.0F);
        cupside1.addBox(-2.0F, -10.0F, -6.0F, 2, 10, 12, 0.0F);
        foot.addChild(cupside2);
        lid.addChild(lidtop);
        foot.addChild(cupside4);
        foot.addChild(cupside3);
        foot.addChild(lid);
        foot.addChild(cupside1);
    }

    public void render() {
        foot.render(0.0625F);
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}