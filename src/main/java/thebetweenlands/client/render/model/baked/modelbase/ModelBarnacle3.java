package thebetweenlands.client.render.model.baked.modelbase;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelBarnacle3 extends ModelBase {
	public ModelRenderer bone;
	public ModelRenderer Bornacle7;
	public ModelRenderer bornacle7_r1;
	public ModelRenderer Bornacle6;
	public ModelRenderer bornacle6_r1;
	public ModelRenderer Bornacle5;
	public ModelRenderer Bornacle4;
	public ModelRenderer bornacle4_r1;
	public ModelRenderer Bornacle3;
	public ModelRenderer Bornacle2;
	public ModelRenderer Stalk2;
	public ModelRenderer Flesh2a;
	public ModelRenderer Flesh2b;
	public ModelRenderer Bornacle1;
	public ModelRenderer Flesh1a;
	public ModelRenderer flesh1a_r1;
	public ModelRenderer Flesh1b;
	public ModelRenderer Stalk1;

	public ModelBarnacle3() {
		textureWidth = 32;
		textureHeight = 32;

		bone = new ModelRenderer(this);
		bone.setRotationPoint(0.0F, 24.0F, 0.0F);
		

		Bornacle7 = new ModelRenderer(this);
		Bornacle7.setRotationPoint(4.5F, 0.0F, -0.5F);
		bone.addChild(Bornacle7);
		setRotationAngle(Bornacle7, 0.1745F, -2.0944F, 0.0F);
		

		bornacle7_r1 = new ModelRenderer(this, 0, 24);
		bornacle7_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
		Bornacle7.addChild(bornacle7_r1);
		setRotationAngle(bornacle7_r1, 0.0F, -0.1745F, -0.1745F);
		bornacle7_r1.addBox(-2.0F, -1.0F, -2.0F, 2, 1, 2, 0.0F);

		Bornacle6 = new ModelRenderer(this);
		Bornacle6.setRotationPoint(-0.5F, 0.0F, -4.0F);
		bone.addChild(Bornacle6);
		setRotationAngle(Bornacle6, -0.2182F, -1.5708F, 0.0F);
		

		bornacle6_r1 = new ModelRenderer(this, 18, 19);
		bornacle6_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
		Bornacle6.addChild(bornacle6_r1);
		setRotationAngle(bornacle6_r1, 0.0F, -0.1745F, -0.1745F);
		bornacle6_r1.addBox(-2.0F, -1.0F, 0.0F, 2, 1, 2, 0.0F);

		Bornacle5 = new ModelRenderer(this, 9, 19);
		Bornacle5.setRotationPoint(0.5F, 0.0F, 4.0F);
		bone.addChild(Bornacle5);
		setRotationAngle(Bornacle5, 0.3491F, -1.8762F, -0.4363F);
		Bornacle5.addBox(0.0F, -1.0F, 0.0F, 2, 1, 2, 0.0F);

		Bornacle4 = new ModelRenderer(this);
		Bornacle4.setRotationPoint(-1.0F, -1.0F, 2.0F);
		bone.addChild(Bornacle4);
		setRotationAngle(Bornacle4, 0.0873F, -2.0944F, -0.4363F);
		

		bornacle4_r1 = new ModelRenderer(this, 13, 6);
		bornacle4_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
		Bornacle4.addChild(bornacle4_r1);
		setRotationAngle(bornacle4_r1, 0.0873F, 0.0F, -0.0873F);
		bornacle4_r1.addBox(0.0F, -2.0F, 0.0F, 3, 2, 3, 0.0F);

		Bornacle3 = new ModelRenderer(this, 0, 6);
		Bornacle3.setRotationPoint(0.0F, 0.0F, -3.0F);
		bone.addChild(Bornacle3);
		setRotationAngle(Bornacle3, 0.0873F, 0.6545F, 0.2182F);
		Bornacle3.addBox(0.0F, -3.0F, 0.0F, 3, 2, 3, 0.0F);

		Bornacle2 = new ModelRenderer(this, 13, 0);
		Bornacle2.setRotationPoint(1.0F, -2.0F, 1.0F);
		bone.addChild(Bornacle2);
		setRotationAngle(Bornacle2, -0.0873F, 0.1309F, 0.1309F);
		Bornacle2.addBox(0.0F, -2.0F, 0.0F, 3, 2, 3, 0.0F);

		Stalk2 = new ModelRenderer(this, 0, 19);
		Stalk2.setRotationPoint(0.0F, 0.0F, 0.0F);
		Bornacle2.addChild(Stalk2);
		setRotationAngle(Stalk2, 0.0F, 0.0F, -0.1309F);
		Stalk2.addBox(0.0F, 0.0F, 0.01F, 2, 2, 2, 0.0F);

		Flesh2a = new ModelRenderer(this, 16, 12);
		Flesh2a.setRotationPoint(1.5F, -2.0F, 1.5F);
		Bornacle2.addChild(Flesh2a);
		setRotationAngle(Flesh2a, 0.0F, 0.0F, -0.1745F);
		Flesh2a.addBox(0.0F, -3.0F, -1.5F, 0, 3, 3, 0.0F);

		Flesh2b = new ModelRenderer(this, 16, 24);
		Flesh2b.setRotationPoint(1.5F, -2.0F, 1.5F);
		Bornacle2.addChild(Flesh2b);
		setRotationAngle(Flesh2b, 0.1745F, 0.0F, 0.0F);
		Flesh2b.addBox(-1.5F, -3.0F, 0.0F, 3, 3, 0, 0.0F);

		Bornacle1 = new ModelRenderer(this, 0, 0);
		Bornacle1.setRotationPoint(-1.0F, -2.0F, -1.0F);
		bone.addChild(Bornacle1);
		setRotationAngle(Bornacle1, 0.2182F, 0.2618F, 0.0F);
		Bornacle1.addBox(-3.0F, -2.0F, -3.0F, 3, 2, 3, 0.0F);

		Flesh1a = new ModelRenderer(this);
		Flesh1a.setRotationPoint(-1.5F, -2.0F, -1.5F);
		Bornacle1.addChild(Flesh1a);
		

		flesh1a_r1 = new ModelRenderer(this, 9, 24);
		flesh1a_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
		Flesh1a.addChild(flesh1a_r1);
		setRotationAngle(flesh1a_r1, -0.1309F, 0.0F, 0.0F);
		flesh1a_r1.addBox(-1.5F, -3.0F, 0.0F, 3, 3, 0, 0.0F);

		Flesh1b = new ModelRenderer(this, 9, 12);
		Flesh1b.setRotationPoint(-1.5F, -2.0F, -1.5F);
		Bornacle1.addChild(Flesh1b);
		setRotationAngle(Flesh1b, 0.0F, 0.0F, 0.1309F);
		Flesh1b.addBox(0.0F, -3.0F, -1.5F, 0, 3, 3, 0.0F);

		Stalk1 = new ModelRenderer(this, 0, 12);
		Stalk1.setRotationPoint(0.0F, 0.0F, 0.0F);
		Bornacle1.addChild(Stalk1);
		setRotationAngle(Stalk1, -0.1309F, 0.0F, 0.0F);
		Stalk1.addBox(-2.01F, 0.0F, -2.0F, 2, 2, 2, 0.0F);
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