package thebetweenlands.client.render.model.baked.modelbase;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelBarnacle2  extends ModelBase {
	public ModelRenderer bone;
	public ModelRenderer Bornacle1;
	public ModelRenderer Bornacle2;
	public ModelRenderer Bornacle3;
	public ModelRenderer Bornacle4;

	public ModelBarnacle2() {
		textureWidth = 32;
		textureHeight = 32;

		bone = new ModelRenderer(this);
		bone.setRotationPoint(0.0F, 24.0F, 0.0F);
		

		Bornacle1 = new ModelRenderer(this,0, 0);
		Bornacle1.setRotationPoint(-1.0F, 0.0F, -1.0F);
		bone.addChild(Bornacle1);
		setRotationAngle(Bornacle1, 0.2182F, 0.2618F, 0.0F);
		Bornacle1.addBox(-3.0F, -2.0F, -3.0F, 3, 2, 3, 0.0F);

		Bornacle2 = new ModelRenderer(this, 13, 0);
		Bornacle2.setRotationPoint(1.0F, 0.0F, 1.0F);
		bone.addChild(Bornacle2);
		setRotationAngle(Bornacle2, -0.0873F, 0.1309F, 0.1309F);
		Bornacle2.addBox(0.0F, -2.0F, 0.0F, 2, 2, 2, 0.0F);

		Bornacle3 = new ModelRenderer(this, 9, 6);
		Bornacle3.setRotationPoint(1.0F, 0.0F, -2.0F);
		bone.addChild(Bornacle3);
		setRotationAngle(Bornacle3, 0.0873F, 0.6545F, 0.2182F);
		Bornacle3.addBox(0.0F, -1.0F, 0.0F, 2, 1, 2, 0.0F);

		Bornacle4 = new ModelRenderer(this, 0, 6);
		Bornacle4.setRotationPoint(-1.0F, 0.0F, 2.0F);
		bone.addChild(Bornacle4);
		setRotationAngle(Bornacle4, -0.0436F, -2.1817F, -0.0873F);
		Bornacle4.addBox(0.0F, -1.0F, 0.0F, 2, 1, 2, 0.0F);
	}

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
		bone.render(f5);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}