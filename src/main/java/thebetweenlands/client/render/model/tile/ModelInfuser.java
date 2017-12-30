package thebetweenlands.client.render.model.tile;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelInfuser extends ModelBase {
	ModelRenderer cauldronpiece1;
	ModelRenderer cauldronpiece2;
	ModelRenderer cauldronpiece3;
	ModelRenderer cauldronpiece4;
	ModelRenderer cauldroncorner1;
	ModelRenderer cauldroncorner2;
	ModelRenderer cauldroncorner3;
	ModelRenderer cauldroncorner4;
	ModelRenderer cauldronbottom;
	ModelRenderer leg1;
	ModelRenderer leg2;
	ModelRenderer leg3;
	ModelRenderer leg4;
	ModelRenderer stand;
	ModelRenderer spoon_bottom;
	ModelRenderer spoon_handle;
	ModelRenderer spoon_end;

	public ModelInfuser() {
		textureWidth = 128;
		textureHeight = 64;
		cauldroncorner4 = new ModelRenderer(this, 27, 34);
		cauldroncorner4.setRotationPoint(0.0F, 20.0F, 0.0F);
		cauldroncorner4.addBox(-5.0F, -10.0F, -5.0F, 2, 9, 2, 0.0F);
		stand = new ModelRenderer(this, 65, 10);
		stand.setRotationPoint(0.0F, 20.0F, 0.0F);
		stand.addBox(-7.5F, -2.5F, -7.5F, 15, 3, 15, 0.0F);
		spoon_end = new ModelRenderer(this, 83, 29);
		spoon_end.setRotationPoint(0.0F, -7.0F, 0.0F);
		spoon_end.addBox(-1.5F, -2.0F, -2.0F, 3, 2, 3, 0.0F);
		spoon_handle = new ModelRenderer(this, 74, 29);
		spoon_handle.setRotationPoint(0.0F, 0.0F, 0.0F);
		spoon_handle.addBox(-1.0F, -7.0F, -1.5F, 2, 8, 2, 0.0F);
		setRotateAngle(spoon_handle, 0.18203784098300857F, 0.0F, 0.0F);
		cauldronbottom = new ModelRenderer(this, 0, 46);
		cauldronbottom.setRotationPoint(0.0F, 20.0F, 0.0F);
		cauldronbottom.addBox(-5.0F, -1.0F, -5.0F, 10, 3, 10, 0.0F);
		cauldroncorner1 = new ModelRenderer(this, 0, 34);
		cauldroncorner1.setRotationPoint(0.0F, 20.0F, 0.0F);
		cauldroncorner1.addBox(3.0F, -10.0F, -5.0F, 2, 9, 2, 0.0F);
		cauldronpiece2 = new ModelRenderer(this, 0, 13);
		cauldronpiece2.setRotationPoint(0.0F, 20.0F, 0.0F);
		cauldronpiece2.addBox(-7.0F, -10.0F, -5.0F, 2, 10, 10, 0.0F);
		leg2 = new ModelRenderer(this, 78, 0);
		leg2.setRotationPoint(6.5F, 20.0F, 6.5F);
		leg2.addBox(-3.0F, -1.5F, -3.0F, 3, 6, 3, 0.0F);
		setRotateAngle(leg2, 0.22759093446006054F, 0.022689280275926284F, -0.22759093446006054F);
		spoon_bottom = new ModelRenderer(this, 65, 29);
		spoon_bottom.setRotationPoint(-0.6F, 13.9F, 2.5F);
		spoon_bottom.addBox(-1.5F, 0.0F, -1.0F, 3, 4, 1, 0.0F);
		setRotateAngle(spoon_bottom, -0.5918411493512771F, -0.5009094953223726F, -0.136659280431156F);
		leg3 = new ModelRenderer(this, 91, 0);
		leg3.setRotationPoint(6.5F, 20.0F, -6.5F);
		leg3.addBox(-3.0F, -1.5F, 0.0F, 3, 6, 3, 0.0F);
		setRotateAngle(leg3, -0.22759093446006054F, 0.022689280275926284F, -0.22759093446006054F);
		cauldronpiece3 = new ModelRenderer(this, 25, 0);
		cauldronpiece3.setRotationPoint(0.0F, 20.0F, 0.0F);
		cauldronpiece3.addBox(-5.0F, -10.0F, 5.0F, 10, 10, 2, 0.0F);
		cauldronpiece1 = new ModelRenderer(this, 0, 0);
		cauldronpiece1.setRotationPoint(0.0F, 20.0F, 0.0F);
		cauldronpiece1.addBox(-5.0F, -10.0F, -7.0F, 10, 10, 2, 0.0F);
		leg4 = new ModelRenderer(this, 104, 0);
		leg4.setRotationPoint(-6.5F, 20.0F, -6.5F);
		leg4.addBox(0.0F, -1.5F, 0.0F, 3, 6, 3, 0.0F);
		setRotateAngle(leg4, -0.22759093446006054F, -0.022689280275926284F, 0.22759093446006054F);
		cauldroncorner2 = new ModelRenderer(this, 9, 34);
		cauldroncorner2.setRotationPoint(0.0F, 20.0F, 0.0F);
		cauldroncorner2.addBox(3.0F, -10.0F, 3.0F, 2, 9, 2, 0.0F);
		cauldronpiece4 = new ModelRenderer(this, 25, 13);
		cauldronpiece4.setRotationPoint(0.0F, 20.0F, 0.0F);
		cauldronpiece4.addBox(5.0F, -10.0F, -5.0F, 2, 10, 10, 0.0F);
		leg1 = new ModelRenderer(this, 65, 0);
		leg1.setRotationPoint(-6.5F, 20.0F, 6.5F);
		leg1.addBox(0.0F, -1.5F, -3.0F, 3, 6, 3, 0.0F);
		setRotateAngle(leg1, 0.22759093446006054F, -0.022689280275926284F, 0.22759093446006054F);
		cauldroncorner3 = new ModelRenderer(this, 18, 34);
		cauldroncorner3.setRotationPoint(0.0F, 20.0F, 0.0F);
		cauldroncorner3.addBox(-5.0F, -10.0F, 3.0F, 2, 9, 2, 0.0F);
		spoon_handle.addChild(spoon_end);
		spoon_bottom.addChild(spoon_handle);
	}

	public void render() { 
		cauldroncorner4.render(0.0625F);
		stand.render(0.0625F);
		cauldronbottom.render(0.0625F);
		cauldroncorner1.render(0.0625F);
		cauldronpiece2.render(0.0625F);
		leg2.render(0.0625F);
		leg3.render(0.0625F);
		cauldronpiece3.render(0.0625F);
		cauldronpiece1.render(0.0625F);
		leg4.render(0.0625F);
		cauldroncorner2.render(0.0625F);
		cauldronpiece4.render(0.0625F);
		leg1.render(0.0625F);
		cauldroncorner3.render(0.0625F);
	}

	public void renderSpoon() { 
		spoon_bottom.render(0.0625F);
	}

	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}
