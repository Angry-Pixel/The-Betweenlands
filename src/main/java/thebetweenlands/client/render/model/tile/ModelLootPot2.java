package thebetweenlands.client.render.model.tile;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelLootPot2 extends ModelBase {
    ModelRenderer foot;
    ModelRenderer foot2;
    ModelRenderer cup1;
    ModelRenderer cup2;
    ModelRenderer cup3;
    ModelRenderer earright;
    ModelRenderer earleft;
    ModelRenderer rim1;
    ModelRenderer rim2;
    ModelRenderer rim3;
    ModelRenderer rim4;

    public ModelLootPot2() {
        textureWidth = 128;
        textureHeight = 64;
        rim3 = new ModelRenderer(this, 61, 11);
        rim3.setRotationPoint(0.0F, -2.0F, 3.0F);
        rim3.addBox(-5.0F, -2.0F, 0.0F, 8, 2, 2, 0.0F);
        rim2 = new ModelRenderer(this, 40, 16);
        rim2.setRotationPoint(3.0F, -2.0F, 0.0F);
        rim2.addBox(0.0F, -2.0F, -3.0F, 2, 2, 8, 0.0F);
        foot = new ModelRenderer(this, 0, 0);
        foot.setRotationPoint(0.0F, 24.0F, 0.0F);
        foot.addBox(-4.0F, -2.0F, -4.0F, 8, 2, 8, 0.0F);
        earleft = new ModelRenderer(this, 65, 27);
        earleft.setRotationPoint(6.0F, -12.0F, 0.0F);
        earleft.addBox(0.0F, -1.0F, -1.5F, 4, 8, 3, 0.0F);
        setRotateAngle(earleft, 0.0F, 0.0F, 0.27314402793711257F);
        rim4 = new ModelRenderer(this, 61, 16);
        rim4.setRotationPoint(-3.0F, -2.0F, 0.0F);
        rim4.addBox(-2.0F, -2.0F, -5.0F, 2, 2, 8, 0.0F);
        rim1 = new ModelRenderer(this, 40, 11);
        rim1.setRotationPoint(0.0F, -2.0F, -3.0F);
        rim1.addBox(-3.0F, -2.0F, -2.0F, 8, 2, 2, 0.0F);
        earright = new ModelRenderer(this, 50, 27);
        earright.setRotationPoint(-6.0F, -12.0F, 0.0F);
        earright.addBox(-4.0F, -1.0F, -1.5F, 4, 8, 3, 0.0F);
        setRotateAngle(earright, 0.0F, 0.0F, -0.27314402793711257F);
        cup1 = new ModelRenderer(this, 0, 20);
        cup1.setRotationPoint(0.0F, -4.0F, 0.0F);
        cup1.addBox(-5.0F, -2.0F, -5.0F, 10, 2, 10, 0.0F);
        cup3 = new ModelRenderer(this, 40, 0);
        cup3.setRotationPoint(0.0F, -12.0F, 0.0F);
        cup3.addBox(-4.0F, -2.0F, -4.0F, 8, 2, 8, 0.0F);
        cup2 = new ModelRenderer(this, 0, 33);
        cup2.setRotationPoint(0.0F, -6.0F, 0.0F);
        cup2.addBox(-6.0F, -6.0F, -6.0F, 12, 6, 12, 0.0F);
        foot2 = new ModelRenderer(this, 0, 11);
        foot2.setRotationPoint(0.0F, -2.0F, 0.0F);
        foot2.addBox(-3.0F, -2.0F, -3.0F, 6, 2, 6, 0.0F);
        cup3.addChild(rim3);
        cup3.addChild(rim2);
        foot.addChild(earleft);
        cup3.addChild(rim4);
        cup3.addChild(rim1);
        foot.addChild(earright);
        foot.addChild(cup1);
        foot.addChild(cup3);
        foot.addChild(cup2);
        foot.addChild(foot2);
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
