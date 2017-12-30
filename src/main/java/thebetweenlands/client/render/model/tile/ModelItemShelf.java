package thebetweenlands.client.render.model.tile;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelItemShelf extends ModelBase {

	ModelRenderer support;
	ModelRenderer bottomShelf;
	ModelRenderer topShelf;

	public ModelItemShelf() {
		textureWidth = 64;
		textureHeight = 32;

		support = new ModelRenderer(this, 0, 0);
		support.addBox(-0.5F, -8F, 1F, 1, 16, 7);
		support.setRotationPoint(0F, 16F, 0F);
		setRotation(support, 0F, 0F, 0F);
		bottomShelf = new ModelRenderer(this, 0, 23);
		bottomShelf.addBox(-8F, 7F, 0F, 16, 1, 8);
		bottomShelf.setRotationPoint(0F, 16F, 0F);
		setRotation(bottomShelf, 0F, 0F, 0F);
		topShelf = new ModelRenderer(this, 0, 23);
		topShelf.addBox(-8F, -1F, 0F, 16, 1, 8);
		topShelf.setRotationPoint(0F, 16F, 0F);
		setRotation(topShelf, 0F, 0F, 0F);
	}

	public void render() {
		support.render(0.0625F);
		bottomShelf.render(0.0625F);
		topShelf.render(0.0625F);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

}
