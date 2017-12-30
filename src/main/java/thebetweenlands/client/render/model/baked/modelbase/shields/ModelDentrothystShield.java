package thebetweenlands.client.render.model.baked.modelbase.shields;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * BLShield_Dentrothyst - TripleHeadedSheep
 * Created using Tabula 4.1.1
 */
public class ModelDentrothystShield extends ModelBase {
	public ModelRenderer handle;
	public ModelRenderer shield_main;
	public ModelRenderer plate1;
	public ModelRenderer plate2;
	public ModelRenderer beam1;
	public ModelRenderer beam2;
	public ModelRenderer beam5;
	public ModelRenderer leaf1;
	public ModelRenderer beam3;
	public ModelRenderer beam4;
	public ModelRenderer beam6;
	public ModelRenderer leaf2;
	public ModelRenderer leaf3;
	public ModelRenderer leaf4;

	public ModelDentrothystShield() {
		this.textureWidth = 128;
		this.textureHeight = 64;
		this.beam3 = new ModelRenderer(this, 27, 25);
		this.beam3.setRotationPoint(0.0F, -6.0F, 0.0F);
		this.beam3.addBox(-3.0F, -7.0F, -1.48F, 3, 7, 3, 0.0F);
		this.setRotateAngle(beam3, 0.0F, 0.0F, -0.9105382707654417F);
		this.leaf1 = new ModelRenderer(this, 53, -4);
		this.leaf1.setRotationPoint(3.0F, 0.0F, 0.0F);
		this.leaf1.addBox(0.0F, -2.0F, -2.5F, 0, 2, 4, 0.0F);
		this.setRotateAngle(leaf1, 0.0F, 0.0F, 0.5009094953223726F);
		this.beam1 = new ModelRenderer(this, 27, 0);
		this.beam1.setRotationPoint(3.5F, 0.0F, -1.0F);
		this.beam1.addBox(0.0F, -5.0F, -1.5F, 3, 11, 3, 0.0F);
		this.setRotateAngle(beam1, 0.0F, 0.0F, 0.27314402793711257F);
		this.shield_main = new ModelRenderer(this, 0, 13);
		this.shield_main.setRotationPoint(0.0F, 1.0F, -1.0F);
		this.shield_main.addBox(-6.0F, -6.0F, -2.0F, 11, 12, 2, 0.0F);
		this.setRotateAngle(shield_main, 0.0F, 0.0F, -0.045553093477052F);
		this.plate2 = new ModelRenderer(this, 0, 34);
		this.plate2.setRotationPoint(0.0F, -6.0F, -2.0F);
		this.plate2.addBox(-4.0F, -2.0F, 0.0F, 8, 2, 2, 0.0F);
		this.setRotateAngle(plate2, -0.136659280431156F, 0.0F, 0.0F);
		this.leaf3 = new ModelRenderer(this, 53, 2);
		this.leaf3.setRotationPoint(0.0F, -2.0F, 0.0F);
		this.leaf3.addBox(0.0F, -2.0F, -2.5F, 0, 2, 4, 0.0F);
		this.setRotateAngle(leaf3, 0.0F, 0.0F, 0.7285004297824331F);
		this.beam6 = new ModelRenderer(this, 40, 7);
		this.beam6.setRotationPoint(0.0F, 3.0F, 0.0F);
		this.beam6.addBox(0.0F, 0.0F, -1.52F, 3, 3, 3, 0.0F);
		this.setRotateAngle(beam6, 0.0F, 0.0F, -0.5918411493512771F);
		this.beam2 = new ModelRenderer(this, 27, 15);
		this.beam2.setRotationPoint(3.0F, -5.0F, 0.0F);
		this.beam2.addBox(-3.0F, -6.0F, -1.49F, 3, 6, 3, 0.0F);
		this.setRotateAngle(beam2, 0.0F, 0.0F, -0.7285004297824331F);
		this.beam4 = new ModelRenderer(this, 27, 36);
		this.beam4.setRotationPoint(-3.0F, -7.0F, 0.0F);
		this.beam4.addBox(0.0F, -3.0F, -1.47F, 3, 3, 3, 0.0F);
		this.setRotateAngle(beam4, 0.0F, 0.0F, 0.27314402793711257F);
		this.handle = new ModelRenderer(this, 0, 0);
		this.handle.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.handle.addBox(-1.0F, -3.0F, -1.0F, 2, 6, 6, 0.0F);
		this.beam5 = new ModelRenderer(this, 40, 0);
		this.beam5.setRotationPoint(0.0F, 6.0F, 0.0F);
		this.beam5.addBox(0.0F, 0.0F, -1.51F, 3, 3, 3, 0.0F);
		this.setRotateAngle(beam5, 0.0F, 0.0F, -0.5918411493512771F);
		this.leaf2 = new ModelRenderer(this, 53, -1);
		this.leaf2.setRotationPoint(0.0F, -2.0F, 0.0F);
		this.leaf2.addBox(0.0F, -2.0F, -2.5F, 0, 2, 4, 0.0F);
		this.setRotateAngle(leaf2, 0.0F, 0.0F, 0.9105382707654417F);
		this.leaf4 = new ModelRenderer(this, 53, 5);
		this.leaf4.setRotationPoint(0.0F, -2.0F, 0.0F);
		this.leaf4.addBox(0.0F, -2.0F, -2.5F, 0, 2, 4, 0.0F);
		this.setRotateAngle(leaf4, 0.0F, 0.0F, -1.1383037381507017F);
		this.plate1 = new ModelRenderer(this, 0, 28);
		this.plate1.setRotationPoint(0.0F, 6.0F, -2.0F);
		this.plate1.addBox(-4.0F, 0.0F, 0.0F, 9, 3, 2, 0.0F);
		this.setRotateAngle(plate1, 0.136659280431156F, 0.0F, 0.0F);
		this.beam2.addChild(this.beam3);
		this.beam1.addChild(this.leaf1);
		this.shield_main.addChild(this.beam1);
		this.handle.addChild(this.shield_main);
		this.shield_main.addChild(this.plate2);
		this.leaf2.addChild(this.leaf3);
		this.beam5.addChild(this.beam6);
		this.beam1.addChild(this.beam2);
		this.beam3.addChild(this.beam4);
		this.beam1.addChild(this.beam5);
		this.leaf1.addChild(this.leaf2);
		this.leaf3.addChild(this.leaf4);
		this.shield_main.addChild(this.plate1);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
		this.handle.render(f5);
	}

	/**
	 * This is a helper function from Tabula to set the rotation of model parts
	 */
	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}
