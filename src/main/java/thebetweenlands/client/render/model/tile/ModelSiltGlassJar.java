package thebetweenlands.client.render.model.tile;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelSiltGlassJar extends ModelBase {
    public ModelRenderer jar_base;
    public ModelRenderer neck;
    public ModelRenderer top;

    public ModelSiltGlassJar() {
        textureWidth = 64;
        textureHeight = 64;
        jar_base = new ModelRenderer(this, 0, 0);
        jar_base.setRotationPoint(0.0F, 24.0F, 0.0F);
        jar_base.addBox(-5.0F, -12.1F, -5.0F, 10, 12, 10, 0.0F);
        neck = new ModelRenderer(this, 0, 23);
        neck.setRotationPoint(0.0F, -12.0F, 0.0F);
        neck.addBox(-3.0F, -2.0F, -3.0F, 6, 2, 6, 0.0F);
        top = new ModelRenderer(this, 0, 32);
        top.setRotationPoint(0.0F, -2.0F, 0.0F);
        top.addBox(-4.0F, -2.0F, -4.0F, 8, 2, 8, 0.0F);
        jar_base.addChild(neck);
        neck.addChild(top);
    }

    public void render() { 
        jar_base.render(0.0625F);
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
