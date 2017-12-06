package thebetweenlands.client.render.model.baked.modelbase;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * Present - Undefined
 * Created using Tabula 7.0.0
 */
public class ModelPresent extends ModelBase {
	public ModelRenderer shape1;
	public ModelRenderer shape2;
	public ModelRenderer shape3;
	public ModelRenderer shape5;

	public ModelPresent() {
		this.textureWidth = 64;
		this.textureHeight = 64;
		this.shape5 = new ModelRenderer(this, 0, 27);
		this.shape5.setRotationPoint(0.0F, 1.0F, 0.0F);
		this.shape5.addBox(0.0F, 0.0F, -6.0F, 0, 10, 12, 0.0F);
		this.setRotateAngle(shape5, 0.0F, 0.7853981633974483F, 0.0F);
		this.shape1 = new ModelRenderer(this, 0, 0);
		this.shape1.setRotationPoint(-6.0F, 15.0F, -6.0F);
		this.shape1.addBox(0.0F, 0.0F, 0.0F, 12, 9, 12, 0.0F);
		this.shape3 = new ModelRenderer(this, 0, 27);
		this.shape3.setRotationPoint(0.0F, 1.0F, 0.0F);
		this.shape3.addBox(0.0F, 0.0F, -6.0F, 0, 10, 12, 0.0F);
		this.setRotateAngle(shape3, 0.0F, -0.7853981633974483F, 0.0F);
		this.shape2 = new ModelRenderer(this, 0, 21);
		this.shape2.setRotationPoint(-7.0F, 11.0F, -7.0F);
		this.shape2.addBox(0.0F, 0.0F, 0.0F, 14, 4, 14, 0.0F);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
		this.shape5.render(f5);
		this.shape1.render(f5);
		this.shape3.render(f5);
		this.shape2.render(f5);
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
