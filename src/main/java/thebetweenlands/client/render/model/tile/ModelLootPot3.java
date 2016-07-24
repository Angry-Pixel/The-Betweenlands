package thebetweenlands.client.render.model.tile;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelLootPot3 extends ModelBase {
    ModelRenderer foot;
    ModelRenderer cup1;
    ModelRenderer cup2;
    ModelRenderer rim1;
    ModelRenderer rim2;
    ModelRenderer rim3;
    ModelRenderer rim4;

    public ModelLootPot3() {
        textureWidth = 128;
        textureHeight = 64;
        cup1 = new ModelRenderer(this, 0, 13);
        cup1.setRotationPoint(0.0F, -2.0F, 0.0F);
        cup1.addBox(-7.0F, -10.0F, -7.0F, 14, 10, 14, 0.0F);
        rim3 = new ModelRenderer(this, 71, 0);
        rim3.setRotationPoint(0.0F, -2.0F, 3.0F);
        rim3.addBox(-5.0F, -2.0F, 0.0F, 8, 2, 2, 0.0F);
        cup2 = new ModelRenderer(this, 0, 38);
        cup2.setRotationPoint(0.0F, -12.0F, 0.0F);
        cup2.addBox(-4.0F, -2.0F, -4.0F, 8, 2, 8, 0.0F);
        rim1 = new ModelRenderer(this, 50, 0);
        rim1.setRotationPoint(0.0F, -2.0F, -3.0F);
        rim1.addBox(-3.0F, -2.0F, -2.0F, 8, 2, 2, 0.0F);
        rim2 = new ModelRenderer(this, 50, 5);
        rim2.setRotationPoint(3.0F, -2.0F, 0.0F);
        rim2.addBox(0.0F, -2.0F, -3.0F, 2, 2, 8, 0.0F);
        rim4 = new ModelRenderer(this, 71, 5);
        rim4.setRotationPoint(-3.0F, -2.0F, 0.0F);
        rim4.addBox(-2.0F, -2.0F, -5.0F, 2, 2, 8, 0.0F);
        foot = new ModelRenderer(this, 0, 0);
        foot.setRotationPoint(0.0F, 24.0F, 0.0F);
        foot.addBox(-5.0F, -2.0F, -5.0F, 10, 2, 10, 0.0F);
        foot.addChild(cup1);
        cup2.addChild(rim3);
        foot.addChild(cup2);
        cup2.addChild(rim1);
        cup2.addChild(rim2);
        cup2.addChild(rim4);
    }

    public void render() {
        foot.render(0.0625F);
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y,
                               float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
