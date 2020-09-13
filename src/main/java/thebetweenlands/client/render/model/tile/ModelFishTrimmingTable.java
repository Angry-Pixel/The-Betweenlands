package thebetweenlands.client.render.model.tile;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.tile.TileEntityFishTrimmingTable;

@SideOnly(Side.CLIENT)
public class ModelFishTrimmingTable extends ModelBase {
    ModelRenderer log_main;
    ModelRenderer cleaver_blade;
    ModelRenderer bloodsplat;
    ModelRenderer log_front;
    ModelRenderer log_sidepiece;
    ModelRenderer log_centre;
    ModelRenderer log_bloodslide;
    ModelRenderer cleaver_handle1a;
    ModelRenderer cleaver_handle1b;
    ModelRenderer cleaver_handle1c;
    ModelRenderer cleaver_handle1d;

    public ModelFishTrimmingTable() {
    	 textureWidth = 64;
         textureHeight = 64;
         cleaver_handle1b = new ModelRenderer(this, 40, 31);
         cleaver_handle1b.setRotationPoint(3.0F, 1.0F, 0.0F);
         cleaver_handle1b.addBox(0.0F, -1.0F, -0.5F, 2, 1, 1, 0.0F);
         setRotateAngle(cleaver_handle1b, 0.0F, 0.0F, -0.091106186954104F);
         log_main = new ModelRenderer(this, 0, 0);
         log_main.setRotationPoint(0.0F, 24.0F, 0.0F);
         log_main.addBox(-7.0F, -12.0F, -4.0F, 14, 12, 11, 0.0F);
         log_front = new ModelRenderer(this, 0, 24);
         log_front.setRotationPoint(0.0F, 0.0F, 0.0F);
         log_front.addBox(-7.0F, -12.0F, -7.0F, 14, 12, 2, 0.0F);
         cleaver_handle1d = new ModelRenderer(this, 40, 37);
         cleaver_handle1d.setRotationPoint(2.0F, -1.0F, 0.0F);
         cleaver_handle1d.addBox(0.0F, 0.0F, -0.5F, 3, 1, 1, 0.0F);
         setRotateAngle(cleaver_handle1d, 0.0F, 0.0F, 0.091106186954104F);
         log_bloodslide = new ModelRenderer(this, 0, 53);
         log_bloodslide.setRotationPoint(-5.0F, -12.0F, -5.0F);
         log_bloodslide.addBox(0.0F, 0.0F, 0.0F, 11, 1, 1, 0.0F);
         setRotateAngle(log_bloodslide, 0.0F, 0.0F, 0.091106186954104F);
         log_centre = new ModelRenderer(this, 7, 39);
         log_centre.setRotationPoint(0.0F, 0.0F, 0.0F);
         log_centre.addBox(-5.0F, -11.0F, -5.0F, 11, 11, 1, 0.0F);
         bloodsplat = new ModelRenderer(this, -5, 56);
         bloodsplat.setRotationPoint(6.0F, 23.98F, -4.5F);
         bloodsplat.addBox(0.0F, 0.0F, -2.4F, 5, 0, 5, 0.0F);
         cleaver_handle1a = new ModelRenderer(this, 40, 28);
         cleaver_handle1a.setRotationPoint(-2.0F, -3.5F, 0.0F);
         cleaver_handle1a.addBox(0.0F, 0.0F, -0.5F, 3, 1, 1, 0.0F);
         cleaver_handle1c = new ModelRenderer(this, 40, 34);
         cleaver_handle1c.setRotationPoint(2.0F, 0.0F, 0.0F);
         cleaver_handle1c.addBox(0.0F, -1.0F, -0.5F, 2, 1, 1, 0.0F);
         setRotateAngle(cleaver_handle1c, 0.0F, 0.0F, -0.091106186954104F);
         cleaver_blade = new ModelRenderer(this, 40, 24);
         cleaver_blade.setRotationPoint(5.5F, 12.0F, 1.9F);
         cleaver_blade.addBox(-2.0F, -3.0F, 0.0F, 5, 3, 0, 0.0F);
         setRotateAngle(cleaver_blade, 0.5918411493512771F, 1.7756979809790308F, 0.6373942428283291F);
         log_sidepiece = new ModelRenderer(this, 0, 39);
         log_sidepiece.setRotationPoint(0.0F, 0.0F, 0.0F);
         log_sidepiece.addBox(-7.0F, -12.0F, -5.0F, 2, 12, 1, 0.0F);
         cleaver_handle1a.addChild(cleaver_handle1b);
         log_main.addChild(log_front);
         cleaver_handle1c.addChild(cleaver_handle1d);
         log_sidepiece.addChild(log_bloodslide);
         log_main.addChild(log_centre);
         cleaver_blade.addChild(cleaver_handle1a);
         cleaver_handle1b.addChild(cleaver_handle1c);
         log_main.addChild(log_sidepiece);
    }

    public void render() { 
        log_main.render(0.0625F);
    }
    
    public void renderParts(TileEntityFishTrimmingTable table) { 
        cleaver_blade.render(0.0625F);
        bloodsplat.render(0.0625F);
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
