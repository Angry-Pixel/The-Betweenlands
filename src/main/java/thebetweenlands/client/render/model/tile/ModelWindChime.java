package thebetweenlands.client.render.model.tile;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * BLWindchime - TripleHeadedSheep
 * Created using Tabula 7.0.1
 */
public class ModelWindChime extends ModelBase {
	public ModelRenderer toprope;
	public ModelRenderer top;
	public ModelRenderer base;
	public ModelRenderer main_rotationpoint;
	public ModelRenderer edge_left;
	public ModelRenderer edge_right;
	public ModelRenderer string1;
	public ModelRenderer string2;
	public ModelRenderer string3;
	public ModelRenderer string4;
	public ModelRenderer string5;
	public ModelRenderer string6;
	public ModelRenderer midstring;
	public ModelRenderer rod1;
	public ModelRenderer rod2;
	public ModelRenderer rod3;
	public ModelRenderer rod4;
	public ModelRenderer rod5;
	public ModelRenderer rod6;
	public ModelRenderer thingy;

	public ModelWindChime() {
		this.textureWidth = 32;
		this.textureHeight = 32;
		this.string4 = new ModelRenderer(this, 22, 0);
		this.string4.setRotationPoint(2.0F, 1.0F, 0.0F);
		this.string4.addBox(0.0F, 0.0F, -0.5F, 0, 4, 1, 0.0F);
		this.string1 = new ModelRenderer(this, 13, 0);
		this.string1.setRotationPoint(-2.0F, 1.0F, 0.0F);
		this.string1.addBox(0.0F, 0.0F, -0.5F, 0, 4, 1, 0.0F);
		this.string3 = new ModelRenderer(this, 19, 0);
		this.string3.setRotationPoint(1.5F, 1.0F, -1.5F);
		this.string3.addBox(0.0F, 0.0F, -0.5F, 0, 5, 1, 0.0F);
		this.setRotateAngle(string3, 0.0F, 0.7853981633974483F, 0.0F);
		this.midstring = new ModelRenderer(this, 0, 23);
		this.midstring.setRotationPoint(0.0F, 1.0F, 0.0F);
		this.midstring.addBox(-0.5F, 0.0F, 0.0F, 1, 5, 0, 0.0F);
		this.setRotateAngle(midstring, 0.0F, 0.27314402793711257F, 0.0F);
		this.rod5 = new ModelRenderer(this, 20, 16);
		this.rod5.setRotationPoint(0.0F, 3.0F, 0.0F);
		this.rod5.addBox(-0.5F, 0.0F, -0.5F, 1, 5, 1, 0.0F);
		this.rod3 = new ModelRenderer(this, 10, 16);
		this.rod3.setRotationPoint(0.0F, 5.0F, 0.0F);
		this.rod3.addBox(-0.5F, 0.0F, -0.5F, 1, 5, 1, 0.0F);
		this.edge_right = new ModelRenderer(this, 20, 6);
		this.edge_right.setRotationPoint(-2.0F, 1.0F, 0.0F);
		this.edge_right.addBox(-1.0F, -1.0F, -2.0F, 1, 1, 4, 0.0F);
		this.setRotateAngle(edge_right, 0.0F, 0.0F, 0.136659280431156F);
		this.rod4 = new ModelRenderer(this, 15, 16);
		this.rod4.setRotationPoint(0.0F, 4.0F, 0.0F);
		this.rod4.addBox(-0.5F, 0.0F, -0.5F, 1, 5, 1, 0.0F);
		this.string5 = new ModelRenderer(this, 25, 0);
		this.string5.setRotationPoint(1.5F, 1.0F, 1.5F);
		this.string5.addBox(0.0F, 0.0F, -0.5F, 0, 3, 1, 0.0F);
		this.setRotateAngle(string5, 0.0F, -0.7853981633974483F, 0.0F);
		this.edge_left = new ModelRenderer(this, 13, 8);
		this.edge_left.setRotationPoint(2.0F, 1.0F, 0.0F);
		this.edge_left.addBox(0.0F, -1.0F, -2.0F, 1, 1, 4, 0.0F);
		this.setRotateAngle(edge_left, 0.0F, 0.0F, -0.136659280431156F);
		this.string6 = new ModelRenderer(this, 28, 0);
		this.string6.setRotationPoint(-1.5F, 1.0F, 1.5F);
		this.string6.addBox(0.0F, 0.0F, -0.5F, 0, 3, 1, 0.0F);
		this.setRotateAngle(string6, 0.0F, 0.7853981633974483F, 0.0F);
		this.toprope = new ModelRenderer(this, 0, 12);
		this.toprope.setRotationPoint(0.0F, 8.0F, 0.0F);
		this.toprope.addBox(-0.5F, 0.0F, -0.5F, 1, 2, 1, 0.0F);
		this.rod6 = new ModelRenderer(this, 25, 16);
		this.rod6.setRotationPoint(0.0F, 3.0F, 0.0F);
		this.rod6.addBox(-0.5F, 0.0F, -0.5F, 1, 5, 1, 0.0F);
		this.top = new ModelRenderer(this, 0, 0);
		this.top.setRotationPoint(0.0F, 2.0F, 0.0F);
		this.top.addBox(-1.5F, 0.0F, -1.5F, 3, 1, 3, 0.0F);
		this.main_rotationpoint = new ModelRenderer(this, 0, 0);
		this.main_rotationpoint.setRotationPoint(0.0F, 1.0F, 0.0F);
		this.main_rotationpoint.addBox(0.0F, 0.0F, 0.0F, 0, 0, 0, 0.0F);
		this.string2 = new ModelRenderer(this, 16, 0);
		this.string2.setRotationPoint(-1.5F, 1.0F, -1.5F);
		this.string2.addBox(0.0F, 0.0F, -0.5F, 0, 5, 1, 0.0F);
		this.setRotateAngle(string2, 0.0F, -0.7853981633974483F, 0.0F);
		this.rod1 = new ModelRenderer(this, 0, 16);
		this.rod1.setRotationPoint(0.0F, 4.0F, 0.0F);
		this.rod1.addBox(-0.5F, 0.0F, -0.5F, 1, 5, 1, 0.0F);
		this.rod2 = new ModelRenderer(this, 5, 16);
		this.rod2.setRotationPoint(0.0F, 5.0F, 0.0F);
		this.rod2.addBox(-0.5F, 0.0F, -0.5F, 1, 5, 1, 0.0F);
		this.base = new ModelRenderer(this, 0, 5);
		this.base.setRotationPoint(0.0F, 1.0F, 0.0F);
		this.base.addBox(-2.0F, 0.0F, -2.0F, 4, 2, 4, 0.0F);
		this.thingy = new ModelRenderer(this, 3, 23);
		this.thingy.setRotationPoint(0.0F, 5.0F, 0.0F);
		this.thingy.addBox(-1.0F, 0.0F, -1.0F, 2, 1, 2, 0.0F);
		this.main_rotationpoint.addChild(this.string4);
		this.main_rotationpoint.addChild(this.string1);
		this.main_rotationpoint.addChild(this.string3);
		this.main_rotationpoint.addChild(this.midstring);
		this.string5.addChild(this.rod5);
		this.string3.addChild(this.rod3);
		this.base.addChild(this.edge_right);
		this.string4.addChild(this.rod4);
		this.main_rotationpoint.addChild(this.string5);
		this.base.addChild(this.edge_left);
		this.main_rotationpoint.addChild(this.string6);
		this.string6.addChild(this.rod6);
		this.toprope.addChild(this.top);
		this.base.addChild(this.main_rotationpoint);
		this.main_rotationpoint.addChild(this.string2);
		this.string1.addChild(this.rod1);
		this.string2.addChild(this.rod2);
		this.top.addChild(this.base);
		this.midstring.addChild(this.thingy);
	}

	public void render() {
		this.toprope.render(0.0625f);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
		this.toprope.render(f5);
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
