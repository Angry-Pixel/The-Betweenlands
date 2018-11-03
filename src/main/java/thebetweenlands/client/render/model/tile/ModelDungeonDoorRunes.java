package thebetweenlands.client.render.model.tile;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.tile.TileEntityDungeonDoorRunes;

@SideOnly(Side.CLIENT)
public class ModelDungeonDoorRunes extends ModelBase {

    public ModelRenderer main;
    public ModelRenderer top;
    public ModelRenderer mid;
    public ModelRenderer bottom;


    public ModelDungeonDoorRunes() {
        textureWidth = 64;
        textureHeight = 64;
        main = new ModelRenderer(this, 0, 0);
        main.setRotationPoint(0.0F, 16.0F, 0.0F);
        main.addBox(-8.0F, -8.0F, -7.0F, 16, 16, 15, 0.0F);
        top = new ModelRenderer(this, 0, 32);
        top.setRotationPoint(0.0F, 11.5F, -5.5F);
        top.addBox(-7.0F, -2.5F, -2.5F, 14, 5, 5, 0.0F);
        mid = new ModelRenderer(this, 0, 43);
        mid.setRotationPoint(0.0F, 16.0F, -6.0F);
        mid.addBox(-7.0F, -2.0F, -2.0F, 14, 4, 4, 0.0F);
        bottom = new ModelRenderer(this, 0, 52);
        bottom.setRotationPoint(0.0F, 20.5F, -5.5F);
        bottom.addBox(-7.0F, -2.5F, -2.5F, 14, 5, 5, 0.0F);
    }

    public void render(TileEntityDungeonDoorRunes tile, float scale) { 
        main.render(scale);
        top.rotateAngleX = 0F + tile.top_rotate / (180F / (float) Math.PI);
        mid.rotateAngleX = 0F + tile.mid_rotate / (180F / (float) Math.PI);
        bottom.rotateAngleX = 0F + tile.bottom_rotate / (180F / (float) Math.PI);

        top.render(scale);
        mid.render(scale);
        bottom.render(scale);
    }

    public void renderItem(float scale) { 
        main.render(scale);
        mid.render(scale);
        top.render(scale);
        bottom.render(scale);
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
