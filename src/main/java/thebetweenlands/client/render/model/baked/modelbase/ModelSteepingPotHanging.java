package thebetweenlands.client.render.model.baked.modelbase;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelSteepingPotHanging extends ModelBase {
	private final ModelRenderer base;
	private final ModelRenderer pot_base;
	private final ModelRenderer pot_side1a;
	private final ModelRenderer pot_side1b;
	private final ModelRenderer pot_side1fillb;
	private final ModelRenderer pot_side1filla;
	private final ModelRenderer pot_side2a;
	private final ModelRenderer pot_side2b;
	private final ModelRenderer pot_side2fillb;
	private final ModelRenderer pot_side2filla;
	private final ModelRenderer pot_side3a;
	private final ModelRenderer pot_side3b;
	private final ModelRenderer handle1a;
	private final ModelRenderer rope2a;
	private final ModelRenderer rope2c;
	private final ModelRenderer rope2c_r1;
	private final ModelRenderer rope2b;
	private final ModelRenderer pot_side3fillb;
	private final ModelRenderer pot_side3filla;
	private final ModelRenderer pot_side4a;
	private final ModelRenderer pot_side4b;
	private final ModelRenderer pot_side4fillb;
	private final ModelRenderer handle2a;
	private final ModelRenderer rope1a;
	private final ModelRenderer rope1c;
	private final ModelRenderer rope1c_r1;
	private final ModelRenderer rope1b;
	private final ModelRenderer pot_side4filla;
	private final ModelRenderer pot_base_side1;
	private final ModelRenderer pot_base_side2;
	private final ModelRenderer pot_top1;
	private final ModelRenderer pot_top_corner1;
	private final ModelRenderer pot_top2;
	private final ModelRenderer pot_top_corner2;
	private final ModelRenderer pot_top3;
	private final ModelRenderer pot_top_corner3;
	private final ModelRenderer pot_top4;
	private final ModelRenderer pot_top_corner4;
	private final ModelRenderer handle_wood;
	private final ModelRenderer connec;

	public ModelSteepingPotHanging() {
		textureWidth = 128;
		textureHeight = 128;

		base = new ModelRenderer(this);
		base.setRotationPoint(0.0F, 24.0F, 0.0F);


		pot_base = new ModelRenderer(this);
		pot_base.setRotationPoint(0.0F, -0.1F, 0.0F);
		base.addChild(pot_base);
		pot_base.cubeList.add(new ModelBox(pot_base, 0, 0, -5.0F, -1.005F, -4.001F, 10, 1, 8, 0.0F, false));

		pot_side1a = new ModelRenderer(this);
		pot_side1a.setRotationPoint(0.0F, -1.0F, 4.0F);
		pot_base.addChild(pot_side1a);
		setRotationAngle(pot_side1a, -0.0873F, 0.0F, 0.0F);
		pot_side1a.cubeList.add(new ModelBox(pot_side1a, 19, 55, -4.0F, -3.001F, 0.0F, 8, 3, 1, 0.0F, false));

		pot_side1b = new ModelRenderer(this);
		pot_side1b.setRotationPoint(0.0F, -3.0F, 1.0F);
		pot_side1a.addChild(pot_side1b);
		setRotationAngle(pot_side1b, 0.1745F, 0.0F, 0.0F);
		pot_side1b.cubeList.add(new ModelBox(pot_side1b, 95, 50, -3.999F, -3.001F, -1.0F, 8, 3, 1, 0.0F, false));

		pot_side1fillb = new ModelRenderer(this);
		pot_side1fillb.setRotationPoint(4.0F, 0.0F, -1.0F);
		pot_side1b.addChild(pot_side1fillb);
		pot_side1fillb.cubeList.add(new ModelBox(pot_side1fillb, 38, 50, -8.001F, -3.0F, -1.0F, 8, 3, 1, 0.0F, false));

		pot_side1filla = new ModelRenderer(this);
		pot_side1filla.setRotationPoint(4.0F, 0.0F, 0.0F);
		pot_side1a.addChild(pot_side1filla);
		pot_side1filla.cubeList.add(new ModelBox(pot_side1filla, 57, 50, -8.0F, -3.0F, -1.0F, 8, 3, 1, 0.0F, false));

		pot_side2a = new ModelRenderer(this);
		pot_side2a.setRotationPoint(0.0F, -1.0F, -4.0F);
		pot_base.addChild(pot_side2a);
		setRotationAngle(pot_side2a, 0.0873F, 0.0F, 0.0F);
		pot_side2a.cubeList.add(new ModelBox(pot_side2a, 0, 55, -4.0F, -3.001F, -1.0F, 8, 3, 1, 0.0F, false));

		pot_side2b = new ModelRenderer(this);
		pot_side2b.setRotationPoint(0.0F, -3.0F, -1.0F);
		pot_side2a.addChild(pot_side2b);
		setRotationAngle(pot_side2b, -0.1745F, 0.0F, 0.0F);
		pot_side2b.cubeList.add(new ModelBox(pot_side2b, 76, 50, -3.999F, -3.001F, 0.0F, 8, 3, 1, 0.0F, false));

		pot_side2fillb = new ModelRenderer(this);
		pot_side2fillb.setRotationPoint(4.0F, 0.0F, 2.0F);
		pot_side2b.addChild(pot_side2fillb);
		pot_side2fillb.cubeList.add(new ModelBox(pot_side2fillb, 0, 50, -8.001F, -3.0F, -1.0F, 8, 3, 1, 0.0F, false));

		pot_side2filla = new ModelRenderer(this);
		pot_side2filla.setRotationPoint(4.0F, 0.0F, 1.0F);
		pot_side2a.addChild(pot_side2filla);
		pot_side2filla.cubeList.add(new ModelBox(pot_side2filla, 19, 50, -8.0F, -3.0F, -1.0F, 8, 3, 1, 0.0F, false));

		pot_side3a = new ModelRenderer(this);
		pot_side3a.setRotationPoint(4.0F, -1.0F, 0.0F);
		pot_base.addChild(pot_side3a);
		setRotationAngle(pot_side3a, 0.0F, 0.0F, 0.0873F);
		pot_side3a.cubeList.add(new ModelBox(pot_side3a, 19, 38, 0.0F, -3.001F, -4.0F, 1, 3, 8, 0.0F, false));

		pot_side3b = new ModelRenderer(this);
		pot_side3b.setRotationPoint(1.0F, -3.0F, 0.0F);
		pot_side3a.addChild(pot_side3b);
		setRotationAngle(pot_side3b, 0.0F, 0.0F, -0.1745F);
		pot_side3b.cubeList.add(new ModelBox(pot_side3b, 57, 26, -1.0F, -3.001F, -4.001F, 1, 3, 8, 0.0F, false));

		handle1a = new ModelRenderer(this);
		handle1a.setRotationPoint(0.0F, -2.0F, 0.0F);
		pot_side3b.addChild(handle1a);
		setRotationAngle(handle1a, 0.0F, 0.0F, 0.1309F);
		handle1a.cubeList.add(new ModelBox(handle1a, 66, 60, 0.0F, 0.0F, -2.0F, 2, 2, 4, 0.0F, false));

		rope2a = new ModelRenderer(this);
		rope2a.setRotationPoint(1.0F, 0.0F, 2.0F);
		handle1a.addChild(rope2a);
		setRotationAngle(rope2a, 0.4102F, 0.0F, -0.3491F);
		rope2a.cubeList.add(new ModelBox(rope2a, 83, 6, -1.0F, -5.0F, 0.0F, 1, 5, 0, 0.0F, true));

		rope2c = new ModelRenderer(this);
		rope2c.setRotationPoint(0.0F, -5.0F, 0.0F);
		rope2a.addChild(rope2c);


		rope2c_r1 = new ModelRenderer(this);
		rope2c_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
		rope2c.addChild(rope2c_r1);
		setRotationAngle(rope2c_r1, -0.4102F, 0.0F, 0.0F);
		rope2c_r1.cubeList.add(new ModelBox(rope2c_r1, 88, 6, -1.0F, -6.0F, 0.0F, 1, 6, 0, 0.0F, true));

		rope2b = new ModelRenderer(this);
		rope2b.setRotationPoint(1.0F, 0.0F, -2.0F);
		handle1a.addChild(rope2b);
		setRotationAngle(rope2b, -0.4102F, 0.0F, -0.3491F);
		rope2b.cubeList.add(new ModelBox(rope2b, 83, 0, -1.0F, -5.0F, 0.0F, 1, 5, 0, 0.0F, true));

		pot_side3fillb = new ModelRenderer(this);
		pot_side3fillb.setRotationPoint(-1.0F, 0.0F, 4.0F);
		pot_side3b.addChild(pot_side3fillb);
		pot_side3fillb.cubeList.add(new ModelBox(pot_side3fillb, 19, 26, -1.0F, -3.0F, -7.99F, 1, 3, 8, 0.0F, false));

		pot_side3filla = new ModelRenderer(this);
		pot_side3filla.setRotationPoint(0.0F, 0.0F, 4.0F);
		pot_side3a.addChild(pot_side3filla);
		pot_side3filla.cubeList.add(new ModelBox(pot_side3filla, 0, 26, -1.0F, -3.0F, -8.0F, 1, 3, 8, 0.0F, false));

		pot_side4a = new ModelRenderer(this);
		pot_side4a.setRotationPoint(-4.0F, -1.0F, 0.0F);
		pot_base.addChild(pot_side4a);
		setRotationAngle(pot_side4a, 0.0F, 0.0F, -0.0873F);
		pot_side4a.cubeList.add(new ModelBox(pot_side4a, 0, 38, -1.0F, -3.001F, -4.0F, 1, 3, 8, 0.0F, false));

		pot_side4b = new ModelRenderer(this);
		pot_side4b.setRotationPoint(-1.0F, -3.0F, 0.0F);
		pot_side4a.addChild(pot_side4b);
		setRotationAngle(pot_side4b, 0.0F, 0.0F, 0.1745F);
		pot_side4b.cubeList.add(new ModelBox(pot_side4b, 38, 26, 0.0F, -3.001F, -4.001F, 1, 3, 8, 0.0F, false));

		pot_side4fillb = new ModelRenderer(this);
		pot_side4fillb.setRotationPoint(2.0F, 0.0F, 4.0F);
		pot_side4b.addChild(pot_side4fillb);
		pot_side4fillb.cubeList.add(new ModelBox(pot_side4fillb, 52, 14, -1.0F, -3.0F, -7.99F, 1, 3, 8, 0.0F, false));

		handle2a = new ModelRenderer(this);
		handle2a.setRotationPoint(0.0F, -2.0F, 0.0F);
		pot_side4b.addChild(handle2a);
		setRotationAngle(handle2a, 0.0F, 0.0F, -0.1309F);
		handle2a.cubeList.add(new ModelBox(handle2a, 53, 60, -2.0F, 0.0F, -2.0F, 2, 2, 4, 0.0F, false));

		rope1a = new ModelRenderer(this);
		rope1a.setRotationPoint(-1.0F, 0.0F, 2.0F);
		handle2a.addChild(rope1a);
		setRotationAngle(rope1a, 0.4102F, 0.0F, 0.3491F);
		rope1a.cubeList.add(new ModelBox(rope1a, 83, 6, 0.0F, -5.0F, 0.0F, 1, 5, 0, 0.0F, false));

		rope1c = new ModelRenderer(this);
		rope1c.setRotationPoint(0.0F, -5.0F, 0.0F);
		rope1a.addChild(rope1c);


		rope1c_r1 = new ModelRenderer(this);
		rope1c_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
		rope1c.addChild(rope1c_r1);
		setRotationAngle(rope1c_r1, -0.4102F, 0.0F, 0.0F);
		rope1c_r1.cubeList.add(new ModelBox(rope1c_r1, 88, 0, 0.0F, -6.0F, 0.0F, 1, 6, 0, 0.0F, false));

		rope1b = new ModelRenderer(this);
		rope1b.setRotationPoint(-1.0F, 0.0F, -2.0F);
		handle2a.addChild(rope1b);
		setRotationAngle(rope1b, -0.4102F, 0.0F, 0.3491F);
		rope1b.cubeList.add(new ModelBox(rope1b, 83, 0, 0.0F, -5.0F, 0.0F, 1, 5, 0, 0.0F, false));

		pot_side4filla = new ModelRenderer(this);
		pot_side4filla.setRotationPoint(1.0F, 0.0F, 4.0F);
		pot_side4a.addChild(pot_side4filla);
		pot_side4filla.cubeList.add(new ModelBox(pot_side4filla, 71, 14, -1.0F, -3.0F, -8.0F, 1, 3, 8, 0.0F, false));

		pot_base_side1 = new ModelRenderer(this);
		pot_base_side1.setRotationPoint(0.0F, 0.0F, 4.0F);
		pot_base.addChild(pot_base_side1);
		pot_base_side1.cubeList.add(new ModelBox(pot_base_side1, 0, 63, -3.999F, -1.005F, 0.0F, 8, 1, 1, 0.0F, false));

		pot_base_side2 = new ModelRenderer(this);
		pot_base_side2.setRotationPoint(0.0F, 0.0F, -4.0F);
		pot_base.addChild(pot_base_side2);
		pot_base_side2.cubeList.add(new ModelBox(pot_base_side2, 0, 60, -3.999F, -1.005F, -1.0F, 8, 1, 1, 0.0F, false));

		pot_top1 = new ModelRenderer(this);
		pot_top1.setRotationPoint(0.0F, -7.0F, 0.0F);
		pot_base.addChild(pot_top1);
		pot_top1.cubeList.add(new ModelBox(pot_top1, 95, 55, -4.0F, -0.8F, 4.0F, 8, 1, 1, 0.0F, false));

		pot_top_corner1 = new ModelRenderer(this);
		pot_top_corner1.setRotationPoint(0.0F, 0.0F, 0.0F);
		pot_top1.addChild(pot_top_corner1);
		pot_top_corner1.cubeList.add(new ModelBox(pot_top_corner1, 5, 71, 3.0F, -0.8F, 3.0F, 1, 1, 1, 0.0F, false));

		pot_top2 = new ModelRenderer(this);
		pot_top2.setRotationPoint(0.0F, -7.0F, 0.0F);
		pot_base.addChild(pot_top2);
		setRotationAngle(pot_top2, 0.0F, 1.5708F, 0.0F);
		pot_top2.cubeList.add(new ModelBox(pot_top2, 76, 55, -4.0F, -0.8F, 4.0F, 8, 1, 1, 0.0F, false));

		pot_top_corner2 = new ModelRenderer(this);
		pot_top_corner2.setRotationPoint(0.0F, 0.0F, 0.0F);
		pot_top2.addChild(pot_top_corner2);
		pot_top_corner2.cubeList.add(new ModelBox(pot_top_corner2, 0, 71, 3.0F, -0.8F, 3.0F, 1, 1, 1, 0.0F, false));

		pot_top3 = new ModelRenderer(this);
		pot_top3.setRotationPoint(0.0F, -7.0F, 0.0F);
		pot_base.addChild(pot_top3);
		setRotationAngle(pot_top3, 0.0F, 3.1416F, 0.0F);
		pot_top3.cubeList.add(new ModelBox(pot_top3, 57, 55, -4.0F, -0.8F, 4.0F, 8, 1, 1, 0.0F, false));

		pot_top_corner3 = new ModelRenderer(this);
		pot_top_corner3.setRotationPoint(0.0F, 0.0F, 0.0F);
		pot_top3.addChild(pot_top_corner3);
		pot_top_corner3.cubeList.add(new ModelBox(pot_top_corner3, 93, 67, 3.0F, -0.8F, 3.0F, 1, 1, 1, 0.0F, false));

		pot_top4 = new ModelRenderer(this);
		pot_top4.setRotationPoint(0.0F, -7.0F, 0.0F);
		pot_base.addChild(pot_top4);
		setRotationAngle(pot_top4, 0.0F, -1.5708F, 0.0F);
		pot_top4.cubeList.add(new ModelBox(pot_top4, 38, 55, -4.0F, -0.8F, 4.0F, 8, 1, 1, 0.0F, false));

		pot_top_corner4 = new ModelRenderer(this);
		pot_top_corner4.setRotationPoint(0.0F, 0.0F, 0.0F);
		pot_top4.addChild(pot_top_corner4);
		pot_top_corner4.cubeList.add(new ModelBox(pot_top_corner4, 88, 67, 3.0F, -0.8F, 3.0F, 1, 1, 1, 0.0F, false));

		handle_wood = new ModelRenderer(this);
		handle_wood.setRotationPoint(0.0F, -15.9F, 0.0F);
		pot_base.addChild(handle_wood);
		setRotationAngle(handle_wood, 0.0F, 1.5708F, 0.0F);
		handle_wood.cubeList.add(new ModelBox(handle_wood, 58, 1, -1.0F, 0.0F, -3.0F, 2, 1, 6, 0.0F, false));

		connec = new ModelRenderer(this);
		connec.setRotationPoint(0.0F, 0.0F, 0.0F);
		handle_wood.addChild(connec);
		connec.cubeList.add(new ModelBox(connec, 93, 0, -1.5F, -0.5F, -1.0F, 3, 2, 2, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		base.render(f5);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}