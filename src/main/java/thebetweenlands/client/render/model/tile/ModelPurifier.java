package thebetweenlands.client.render.model.tile;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelPurifier extends ModelBase {
    ModelRenderer base;
    ModelRenderer filter;
    ModelRenderer sidebeam1;
    ModelRenderer sidebeam2;
    ModelRenderer sidebeam3;
    ModelRenderer sidebeam4;
    ModelRenderer beam1;
    ModelRenderer beam2;
    ModelRenderer beam3;
    ModelRenderer fireplate;
    ModelRenderer side1;
    ModelRenderer side2;
    ModelRenderer side3;
    ModelRenderer side4;
    ModelRenderer corner1;
    ModelRenderer corner2;
    ModelRenderer corner3;
    ModelRenderer corner4;
    ModelRenderer tappiece1;
    ModelRenderer tappiece2;
    ModelRenderer tappiece3;
    ModelRenderer tappiece4;
    ModelRenderer shutter;
    ModelRenderer shutterpiece;
    ModelRenderer beam4b;
    ModelRenderer beam1b;
    ModelRenderer beam2b;
    ModelRenderer beam3b;

    public ModelPurifier() {
        textureWidth = 128;
        textureHeight = 64;
        beam4b = new ModelRenderer(this, 72, 58);
        beam4b.setRotationPoint(6.5F, -14.2F, 0.0F);
        beam4b.addBox(-5.0F, 0.0F, -1.49F, 10, 3, 1, 0.0F);
        setRotation(beam4b, 0.0F, 0.0F, -0.045553093477052F);
        fireplate = new ModelRenderer(this, 50, 11);
        fireplate.setRotationPoint(0.0F, 24.0F, 0.0F);
        fireplate.addBox(-5.0F, -0.1F, -5.0F, 10, 0, 10, 0.0F);
        shutterpiece = new ModelRenderer(this, 41, 51);
        shutterpiece.setRotationPoint(0.0F, -4.0F, 0.0F);
        shutterpiece.addBox(-1.5F, -1.0F, -1.0F, 3, 1, 1, 0.0F);
        beam3 = new ModelRenderer(this, 95, 26);
        beam3.setRotationPoint(0.0F, 24.0F, 6.5F);
        beam3.addBox(-5.0F, -5.0F, -1.51F, 10, 2, 3, 0.0F);
        base = new ModelRenderer(this, 0, 0);
        base.setRotationPoint(0.0F, 20.0F, 0.0F);
        base.addBox(-5.0F, -2.0F, -5.0F, 10, 2, 10, 0.0F);
        tappiece4 = new ModelRenderer(this, 27, 51);
        tappiece4.setRotationPoint(0.0F, 0.0F, -2.0F);
        tappiece4.addBox(-0.5F, -1.0F, -2.0F, 1, 1, 3, 0.0F);
        setRotation(tappiece4, 0.136659280431156F, 0.0F, 0.0F);
        corner2 = new ModelRenderer(this, 50, 13);
        corner2.setRotationPoint(-4.0F, -2.0F, 4.0F);
        corner2.addBox(-1.0F, -10.0F, -1.0F, 2, 10, 2, 0.0F);
        tappiece3 = new ModelRenderer(this, 18, 51);
        tappiece3.setRotationPoint(0.0F, 0.0F, -2.0F);
        tappiece3.addBox(0.5F, -3.0F, -2.0F, 1, 3, 3, 0.0F);
        setRotation(tappiece3, 0.136659280431156F, 0.0F, 0.0F);
        beam1 = new ModelRenderer(this, 95, 0);
        beam1.setRotationPoint(6.5F, 24.0F, 0.0F);
        beam1.addBox(-1.5F, -5.0F, -5.0F, 3, 2, 10, 0.0F);
        setRotation(beam1, 0.0F, 0.0F, -0.045553093477052F);
        tappiece1 = new ModelRenderer(this, 0, 51);
        tappiece1.setRotationPoint(0.0F, 0.0F, -2.0F);
        tappiece1.addBox(-1.5F, -3.0F, -2.0F, 1, 3, 3, 0.0F);
        setRotation(tappiece1, 0.136659280431156F, 0.0F, 0.0F);
        beam2b = new ModelRenderer(this, 95, 45);
        beam2b.setRotationPoint(0.0F, 0.0F, 0.0F);
        beam2b.addBox(-1.5F, -14.0F, -5.0F, 2, 2, 10, 0.0F);
        sidebeam1 = new ModelRenderer(this, 81, 0);
        sidebeam1.setRotationPoint(7.0F, 24.0F, -6.5F);
        sidebeam1.addBox(-2.0F, -14.0F, -1.5F, 3, 14, 3, 0.0F);
        setRotation(sidebeam1, 0.0F, 0.0F, -0.045553093477052F);
        filter = new ModelRenderer(this, 50, 0);
        filter.setRotationPoint(0.0F, 16.5F, 0.0F);
        filter.addBox(-5.0F, 0.0F, -5.0F, 10, 0, 10, 0.0F);
        shutter = new ModelRenderer(this, 36, 51);
        shutter.setRotationPoint(0.0F, 0.0F, -2.0F);
        shutter.addBox(-0.5F, -4.0F, -1.0F, 1, 3, 1, 0.0F);
        setRotation(shutter, 0.136659280431156F, 0.0F, 0.0F);
        side3 = new ModelRenderer(this, 25, 13);
        side3.setRotationPoint(0.0F, 0.0F, 5.0F);
        side3.addBox(-5.0F, -12.0F, 0.0F, 10, 12, 2, 0.0F);
        side1 = new ModelRenderer(this, 0, 13);
        side1.setRotationPoint(0.0F, 0.0F, -5.0F);
        side1.addBox(-5.0F, -12.0F, -2.0F, 10, 12, 2, 0.0F);
        beam2 = new ModelRenderer(this, 95, 13);
        beam2.setRotationPoint(-6.5F, 24.0F, 0.0F);
        beam2.addBox(-1.5F, -5.0F, -5.0F, 3, 2, 10, 0.0F);
        setRotation(beam2, 0.0F, 0.0F, 0.045553093477052F);
        side2 = new ModelRenderer(this, 0, 28);
        side2.setRotationPoint(-5.0F, 0.0F, 0.0F);
        side2.addBox(-2.0F, -12.0F, -5.0F, 2, 12, 10, 0.0F);
        corner4 = new ModelRenderer(this, 50, 39);
        corner4.setRotationPoint(4.0F, -2.0F, -4.0F);
        corner4.addBox(-1.0F, -10.0F, -1.0F, 2, 10, 2, 0.0F);
        beam3b = new ModelRenderer(this, 95, 58);
        beam3b.setRotationPoint(0.0F, 0.0F, 0.0F);
        beam3b.addBox(-5.0F, -13.9F, -0.51F, 10, 2, 2, 0.0F);
        corner1 = new ModelRenderer(this, 50, 0);
        corner1.setRotationPoint(-4.0F, -2.0F, -4.0F);
        corner1.addBox(-1.0F, -10.0F, -1.0F, 2, 10, 2, 0.0F);
        sidebeam4 = new ModelRenderer(this, 68, 36);
        sidebeam4.setRotationPoint(-7.0F, 24.0F, 6.5F);
        sidebeam4.addBox(-1.0F, -14.0F, -1.5F, 3, 14, 3, 0.0F);
        setRotation(sidebeam4, 0.0F, 0.0F, 0.045553093477052F);
        sidebeam2 = new ModelRenderer(this, 81, 18);
        sidebeam2.setRotationPoint(7.0F, 24.0F, 6.5F);
        sidebeam2.addBox(-2.0F, -14.0F, -1.5F, 3, 14, 3, 0.0F);
        setRotation(sidebeam2, 0.0F, 0.0F, -0.045553093477052F);
        corner3 = new ModelRenderer(this, 50, 26);
        corner3.setRotationPoint(4.0F, -2.0F, 4.0F);
        corner3.addBox(-1.0F, -10.0F, -1.0F, 2, 10, 2, 0.0F);
        beam1b = new ModelRenderer(this, 95, 32);
        beam1b.setRotationPoint(0.0F, 0.0F, 0.0F);
        beam1b.addBox(-0.5F, -14.0F, -5.0F, 2, 2, 10, 0.0F);
        side4 = new ModelRenderer(this, 25, 28);
        side4.setRotationPoint(5.0F, 0.0F, 0.0F);
        side4.addBox(0.0F, -12.0F, -5.0F, 2, 12, 10, 0.0F);
        tappiece2 = new ModelRenderer(this, 9, 51);
        tappiece2.setRotationPoint(0.0F, 0.0F, -2.0F);
        tappiece2.addBox(-0.5F, -3.0F, -2.0F, 1, 1, 3, 0.0F);
        setRotation(tappiece2, 0.136659280431156F, 0.0F, 0.0F);
        sidebeam3 = new ModelRenderer(this, 81, 36);
        sidebeam3.setRotationPoint(-7.0F, 24.0F, -6.5F);
        sidebeam3.addBox(-1.0F, -14.0F, -1.5F, 3, 14, 3, 0.0F);
        setRotation(sidebeam3, 0.0F, 0.0F, 0.045553093477052F);
        sidebeam3.addChild(beam4b);
        shutter.addChild(shutterpiece);
        side1.addChild(tappiece4);
        base.addChild(corner2);
        side1.addChild(tappiece3);
        side1.addChild(tappiece1);
        beam2.addChild(beam2b);
        side1.addChild(shutter);
        base.addChild(side3);
        base.addChild(side1);
        base.addChild(side2);
        base.addChild(corner4);
        beam3.addChild(beam3b);
        base.addChild(corner1);
        base.addChild(corner3);
        beam1.addChild(beam1b);
        base.addChild(side4);
        side1.addChild(tappiece2);
    }

	public void renderAll() {
		beam3.render(0.0625F);
		base.render(0.0625F);
		beam1.render(0.0625F);
		sidebeam1.render(0.0625F);
		filter.render(0.0625F);
		beam2.render(0.0625F);
		sidebeam4.render(0.0625F);
		sidebeam2.render(0.0625F);
		sidebeam3.render(0.0625F);
	}

	public void renderFirePlate() {
		fireplate.render(0.0625F);	
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
}
