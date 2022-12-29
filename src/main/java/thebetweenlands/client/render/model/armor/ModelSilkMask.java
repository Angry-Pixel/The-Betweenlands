package thebetweenlands.client.render.model.armor;

import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelSilkMask extends ModelBodyAttachment {
	//private final ModelRenderer bipedHead;
	private final ModelRenderer mask_main;
	private final ModelRenderer mask_main2;
	private final ModelRenderer mask_main2_r1;
	private final ModelRenderer binder_low;
	private final ModelRenderer binder1a;
	private final ModelRenderer binder1b;
	private final ModelRenderer binder1c;
	private final ModelRenderer binder1d;
	private final ModelRenderer binder1e;
	private final ModelRenderer binder2a;
	private final ModelRenderer binder2b;
	private final ModelRenderer binder2c;
	private final ModelRenderer binder2d;
	private final ModelRenderer binder2e;
	private final ModelRenderer connecter;
	private final ModelRenderer connecter_r1;
	private final ModelRenderer binder_high;
	private final ModelRenderer binder3a;
	private final ModelRenderer binder3b;
	private final ModelRenderer binder3c;
	private final ModelRenderer binder3d;
	private final ModelRenderer binder4a;
	private final ModelRenderer binder4b;
	private final ModelRenderer binder4c;

	public ModelSilkMask() {
		textureWidth = 64;
		textureHeight = 64;

		//bipedHead = new ModelRenderer(this);
		//bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);

		mask_main = new ModelRenderer(this);
		mask_main.setRotationPoint(0.0F, -0.5F, -4.6F);
		bipedHead.addChild(mask_main);
		mask_main.cubeList.add(new ModelBox(mask_main, 0, 0, -4.0F, -3.0F, 0.0F, 8, 3, 0, 0.0F, false));

		mask_main2 = new ModelRenderer(this);
		mask_main2.setRotationPoint(0.0F, 0.0F, 0.0F);
		mask_main.addChild(mask_main2);

		mask_main2_r1 = new ModelRenderer(this);
		mask_main2_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
		mask_main2.addChild(mask_main2_r1);
		setRotationAngle(mask_main2_r1, 0.8727F, 0.0F, 0.0F);
		mask_main2_r1.cubeList.add(new ModelBox(mask_main2_r1, 0, 11, -3.0F, 0.0F, 0.0F, 6, 2, 0, 0.0F, false));

		binder_low = new ModelRenderer(this);
		binder_low.setRotationPoint(0.0F, 0.0F, 0.0F);
		mask_main.addChild(binder_low);

		binder1a = new ModelRenderer(this);
		binder1a.setRotationPoint(4.0F, 0.0F, 0.0F);
		binder_low.addChild(binder1a);
		setRotationAngle(binder1a, 0.0F, 0.0436F, 0.0F);
		binder1a.cubeList.add(new ModelBox(binder1a, 15, 24, 0.0F, -1.0F, 0.0F, 0, 1, 2, 0.0F, false));

		binder1b = new ModelRenderer(this);
		binder1b.setRotationPoint(0.0F, 0.0F, 2.0F);
		binder1a.addChild(binder1b);
		setRotationAngle(binder1b, 0.0F, -0.0436F, 0.0F);
		binder1b.cubeList.add(new ModelBox(binder1b, 10, 24, 0.0F, -1.0F, 0.0F, 0, 1, 2, 0.0F, false));

		binder1c = new ModelRenderer(this);
		binder1c.setRotationPoint(0.0F, 0.0F, 2.0F);
		binder1b.addChild(binder1c);
		setRotationAngle(binder1c, 0.1309F, 0.0F, 0.0F);
		binder1c.cubeList.add(new ModelBox(binder1c, 20, 11, 0.0F, -1.0F, 0.0F, 0, 1, 3, 0.0F, false));

		binder1d = new ModelRenderer(this);
		binder1d.setRotationPoint(0.0F, 0.0F, 3.0F);
		binder1c.addChild(binder1d);
		setRotationAngle(binder1d, 0.0F, -0.0436F, 0.0F);
		binder1d.cubeList.add(new ModelBox(binder1d, 5, 24, 0.0F, -1.0F, 0.0F, 0, 1, 2, 0.0F, false));

		binder1e = new ModelRenderer(this);
		binder1e.setRotationPoint(0.0F, 0.0F, 0.0F);
		binder1d.addChild(binder1e);
		binder1e.cubeList.add(new ModelBox(binder1e, 0, 18, -4.0F, -1.0F, 2.0F, 4, 1, 0, 0.0F, false));

		binder2a = new ModelRenderer(this);
		binder2a.setRotationPoint(-4.0F, 0.0F, 0.0F);
		binder_low.addChild(binder2a);
		setRotationAngle(binder2a, 0.0F, -0.0436F, 0.0F);
		binder2a.cubeList.add(new ModelBox(binder2a, 0, 24, 0.0F, -1.0F, 0.0F, 0, 1, 2, 0.0F, false));

		binder2b = new ModelRenderer(this);
		binder2b.setRotationPoint(0.0F, 0.0F, 2.0F);
		binder2a.addChild(binder2b);
		setRotationAngle(binder2b, 0.0F, 0.0436F, 0.0F);
		binder2b.cubeList.add(new ModelBox(binder2b, 20, 20, 0.0F, -1.0F, 0.0F, 0, 1, 2, 0.0F, false));

		binder2c = new ModelRenderer(this);
		binder2c.setRotationPoint(0.0F, 0.0F, 2.0F);
		binder2b.addChild(binder2c);
		setRotationAngle(binder2c, 0.1309F, 0.0F, 0.0F);
		binder2c.cubeList.add(new ModelBox(binder2c, 13, 11, 0.0F, -1.0F, 0.0F, 0, 1, 3, 0.0F, false));

		binder2d = new ModelRenderer(this);
		binder2d.setRotationPoint(0.0F, 0.0F, 3.0F);
		binder2c.addChild(binder2d);
		setRotationAngle(binder2d, 0.0F, 0.0436F, 0.0F);
		binder2d.cubeList.add(new ModelBox(binder2d, 15, 20, 0.0F, -1.0F, 0.0F, 0, 1, 2, 0.0F, false));

		binder2e = new ModelRenderer(this);
		binder2e.setRotationPoint(0.0F, 0.0F, 0.0F);
		binder2d.addChild(binder2e);
		binder2e.cubeList.add(new ModelBox(binder2e, 0, 16, 0.0F, -1.0F, 2.0F, 4, 1, 0, 0.0F, false));

		connecter = new ModelRenderer(this);
		connecter.setRotationPoint(0.0F, -0.5F, 8.25F);
		binder_low.addChild(connecter);

		connecter_r1 = new ModelRenderer(this);
		connecter_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
		connecter.addChild(connecter_r1);
		setRotationAngle(connecter_r1, -0.0873F, 0.0F, 0.0F);
		connecter_r1.cubeList.add(new ModelBox(connecter_r1, 10, 20, -0.5F, -2.0F, 0.0F, 1, 2, 1, 0.0F, false));

		binder_high = new ModelRenderer(this);
		binder_high.setRotationPoint(0.0F, -2.0F, 0.0F);
		mask_main.addChild(binder_high);

		binder3a = new ModelRenderer(this);
		binder3a.setRotationPoint(4.0F, 0.0F, 0.0F);
		binder_high.addChild(binder3a);
		setRotationAngle(binder3a, 0.0F, 0.0436F, 0.0F);
		binder3a.cubeList.add(new ModelBox(binder3a, 5, 20, 0.0F, -1.0F, 0.0F, 0, 1, 2, 0.0F, false));

		binder3b = new ModelRenderer(this);
		binder3b.setRotationPoint(0.0F, 0.0F, 2.0F);
		binder3a.addChild(binder3b);
		setRotationAngle(binder3b, 0.0F, -0.0436F, 0.0F);
		binder3b.cubeList.add(new ModelBox(binder3b, 11, 4, 0.0F, -1.0F, 0.0F, 0, 1, 5, 0.0F, false));

		binder3c = new ModelRenderer(this);
		binder3c.setRotationPoint(0.0F, 0.0F, 5.0F);
		binder3b.addChild(binder3c);
		setRotationAngle(binder3c, 0.0F, -0.0436F, 0.0F);
		binder3c.cubeList.add(new ModelBox(binder3c, 0, 20, 0.0F, -1.0F, 0.0F, 0, 1, 2, 0.0F, false));

		binder3d = new ModelRenderer(this);
		binder3d.setRotationPoint(0.0F, 0.0F, 2.0F);
		binder3c.addChild(binder3d);
		setRotationAngle(binder3d, 0.0F, 0.0436F, 0.0F);
		binder3d.cubeList.add(new ModelBox(binder3d, 17, 0, -8.0F, -1.0F, 0.0F, 8, 1, 0, 0.0F, false));

		binder4a = new ModelRenderer(this);
		binder4a.setRotationPoint(-4.0F, 0.0F, 0.0F);
		binder_high.addChild(binder4a);
		setRotationAngle(binder4a, 0.0F, -0.0436F, 0.0F);
		binder4a.cubeList.add(new ModelBox(binder4a, 14, 16, 0.0F, -1.0F, 0.0F, 0, 1, 2, 0.0F, false));

		binder4b = new ModelRenderer(this);
		binder4b.setRotationPoint(0.0F, 0.0F, 2.0F);
		binder4a.addChild(binder4b);
		setRotationAngle(binder4b, 0.0F, 0.0436F, 0.0F);
		binder4b.cubeList.add(new ModelBox(binder4b, 0, 4, 0.0F, -1.0F, 0.0F, 0, 1, 5, 0.0F, false));

		binder4c = new ModelRenderer(this);
		binder4c.setRotationPoint(0.0F, 0.0F, 5.0F);
		binder4b.addChild(binder4c);
		setRotationAngle(binder4c, 0.0F, 0.0436F, 0.0F);
		binder4c.cubeList.add(new ModelBox(binder4c, 9, 16, 0.0F, -1.0F, 0.0F, 0, 1, 2, 0.0F, false));
	}

	@Override
	public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}