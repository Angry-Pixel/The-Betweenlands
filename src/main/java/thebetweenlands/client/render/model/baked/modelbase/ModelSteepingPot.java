package thebetweenlands.client.render.model.baked.modelbase;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelSteepingPot extends ModelBase {
	private final ModelRenderer base;
	private final ModelRenderer frame_outer1;
	private final ModelRenderer frame_outer2;
	private final ModelRenderer frame_outer3;
	private final ModelRenderer frame_outer4;
	private final ModelRenderer frame_inner;
	private final ModelRenderer grate1;
	private final ModelRenderer grate2;
	private final ModelRenderer grate3;
	private final ModelRenderer grate4;
	private final ModelRenderer grate5;
	private final ModelRenderer grate6;
	private final ModelRenderer grate7;
	private final ModelRenderer grate8;
	private final ModelRenderer grate9;
	private final ModelRenderer legs;
	private final ModelRenderer legmid1;
	private final ModelRenderer leg1a;
	private final ModelRenderer leg1b;
	private final ModelRenderer leg1c;
	private final ModelRenderer leg1d;
	private final ModelRenderer legmid2;
	private final ModelRenderer leg2a;
	private final ModelRenderer leg2b;
	private final ModelRenderer leg2c;
	private final ModelRenderer leg2d;
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
	private final ModelRenderer rope2b;
	private final ModelRenderer rope2c;
	private final ModelRenderer rope2d;
	private final ModelRenderer rope2e;
	private final ModelRenderer rope2f;
	private final ModelRenderer rope2g;
	private final ModelRenderer pot_side3fillb;
	private final ModelRenderer pot_side3filla;
	private final ModelRenderer pot_side4a;
	private final ModelRenderer pot_side4b;
	private final ModelRenderer pot_side4fillb;
	private final ModelRenderer handle2a;
	private final ModelRenderer rope1a;
	private final ModelRenderer rope1b;
	private final ModelRenderer rope1c;
	private final ModelRenderer rope1d;
	private final ModelRenderer rope1e;
	private final ModelRenderer rope1f;
	private final ModelRenderer handle_wood;
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

	public ModelSteepingPot() {
		textureWidth = 128;
		textureHeight = 128;

		base = new ModelRenderer(this);
		base.setRotationPoint(0.0F, 24.0F, 0.0F);
		

		frame_outer1 = new ModelRenderer(this);
		frame_outer1.setRotationPoint(-6.0F, -3.0F, 0.0F);
		base.addChild(frame_outer1);
		setRotationAngle(frame_outer1, 0.0F, 0.0F, 0.2182F);
		frame_outer1.cubeList.add(new ModelBox(frame_outer1, 66, 0, -2.0F, -1.0F, -6.0F, 2, 1, 12, 0.0F, false));

		frame_outer2 = new ModelRenderer(this);
		frame_outer2.setRotationPoint(6.0F, -3.0F, 0.0F);
		base.addChild(frame_outer2);
		setRotationAngle(frame_outer2, 0.0F, 0.0F, -0.2182F);
		frame_outer2.cubeList.add(new ModelBox(frame_outer2, 37, 0, 0.0F, -1.0F, -6.0F, 2, 1, 12, 0.0F, false));

		frame_outer3 = new ModelRenderer(this);
		frame_outer3.setRotationPoint(0.0F, -4.0F, 0.0F);
		base.addChild(frame_outer3);
		frame_outer3.cubeList.add(new ModelBox(frame_outer3, 38, 41, -6.0F, 0.0F, -5.99F, 12, 1, 1, 0.0F, false));

		frame_outer4 = new ModelRenderer(this);
		frame_outer4.setRotationPoint(0.0F, -4.0F, 0.0F);
		base.addChild(frame_outer4);
		frame_outer4.cubeList.add(new ModelBox(frame_outer4, 38, 38, -6.0F, 0.0F, 4.999F, 12, 1, 1, 0.0F, false));

		frame_inner = new ModelRenderer(this);
		frame_inner.setRotationPoint(0.0F, -4.0F, 0.0F);
		base.addChild(frame_inner);
		

		grate1 = new ModelRenderer(this);
		grate1.setRotationPoint(4.5F, 0.0F, 0.0F);
		frame_inner.addChild(grate1);
		grate1.cubeList.add(new ModelBox(grate1, 0, 74, -0.5F, 0.0F, -5.0F, 1, 1, 10, 0.0F, false));

		grate2 = new ModelRenderer(this);
		grate2.setRotationPoint(1.5F, 0.0F, 0.0F);
		frame_inner.addChild(grate2);
		grate2.cubeList.add(new ModelBox(grate2, 0, 86, -0.5F, 0.0F, -5.0F, 1, 1, 10, 0.0F, false));

		grate3 = new ModelRenderer(this);
		grate3.setRotationPoint(-1.5F, 0.0F, 0.0F);
		frame_inner.addChild(grate3);
		grate3.cubeList.add(new ModelBox(grate3, 23, 74, -0.5F, 0.0F, -5.0F, 1, 1, 10, 0.0F, false));

		grate4 = new ModelRenderer(this);
		grate4.setRotationPoint(-4.5F, 0.0F, 0.0F);
		frame_inner.addChild(grate4);
		grate4.cubeList.add(new ModelBox(grate4, 23, 86, -0.5F, 0.0F, -5.0F, 1, 1, 10, 0.0F, false));

		grate5 = new ModelRenderer(this);
		grate5.setRotationPoint(0.0F, -0.1F, 4.0F);
		frame_inner.addChild(grate5);
		grate5.cubeList.add(new ModelBox(grate5, 23, 14, -6.5F, -0.025F, -0.5F, 13, 1, 1, 0.0F, false));

		grate6 = new ModelRenderer(this);
		grate6.setRotationPoint(0.0F, -0.1F, 2.0F);
		frame_inner.addChild(grate6);
		grate6.cubeList.add(new ModelBox(grate6, 23, 17, -6.5F, -0.025F, -0.5F, 13, 1, 1, 0.0F, false));

		grate7 = new ModelRenderer(this);
		grate7.setRotationPoint(0.0F, -0.1F, 0.0F);
		frame_inner.addChild(grate7);
		grate7.cubeList.add(new ModelBox(grate7, 23, 20, -6.5F, -0.025F, -0.5F, 13, 1, 1, 0.0F, false));

		grate8 = new ModelRenderer(this);
		grate8.setRotationPoint(0.0F, -0.1F, -2.0F);
		frame_inner.addChild(grate8);
		grate8.cubeList.add(new ModelBox(grate8, 17, 23, -6.5F, -0.025F, -0.5F, 13, 1, 1, 0.0F, false));

		grate9 = new ModelRenderer(this);
		grate9.setRotationPoint(0.0F, -0.1F, -4.0F);
		frame_inner.addChild(grate9);
		grate9.cubeList.add(new ModelBox(grate9, 0, 11, -6.5F, -0.025F, -0.5F, 13, 1, 1, 0.0F, false));

		legs = new ModelRenderer(this);
		legs.setRotationPoint(0.0F, -4.0F, 0.0F);
		base.addChild(legs);
		

		legmid1 = new ModelRenderer(this);
		legmid1.setRotationPoint(0.0F, 0.5F, -6.0F);
		legs.addChild(legmid1);
		legmid1.cubeList.add(new ModelBox(legmid1, 36, 60, -3.0F, 0.0F, -1.001F, 6, 1, 2, 0.0F, false));

		leg1a = new ModelRenderer(this);
		leg1a.setRotationPoint(-3.0F, 0.0F, 0.0F);
		legmid1.addChild(leg1a);
		setRotationAngle(leg1a, 0.0F, 0.0F, -0.3927F);
		leg1a.cubeList.add(new ModelBox(leg1a, 77, 67, -3.0F, 0.0F, -1.0F, 3, 1, 2, 0.0F, false));

		leg1b = new ModelRenderer(this);
		leg1b.setRotationPoint(-3.0F, 0.0F, 0.0F);
		leg1a.addChild(leg1b);
		setRotationAngle(leg1b, 0.0F, 0.0F, -0.3927F);
		leg1b.cubeList.add(new ModelBox(leg1b, 66, 67, -3.0F, 0.0F, -0.999F, 3, 1, 2, 0.0F, false));

		leg1c = new ModelRenderer(this);
		leg1c.setRotationPoint(3.0F, 0.0F, 0.0F);
		legmid1.addChild(leg1c);
		setRotationAngle(leg1c, 0.0F, 0.0F, 0.3927F);
		leg1c.cubeList.add(new ModelBox(leg1c, 55, 67, 0.0F, 0.0F, -1.0F, 3, 1, 2, 0.0F, false));

		leg1d = new ModelRenderer(this);
		leg1d.setRotationPoint(3.0F, 0.0F, 0.0F);
		leg1c.addChild(leg1d);
		setRotationAngle(leg1d, 0.0F, 0.0F, 0.3927F);
		leg1d.cubeList.add(new ModelBox(leg1d, 44, 67, 0.0F, 0.0F, -0.999F, 3, 1, 2, 0.0F, false));

		legmid2 = new ModelRenderer(this);
		legmid2.setRotationPoint(0.0F, 0.5F, 6.0F);
		legs.addChild(legmid2);
		legmid2.cubeList.add(new ModelBox(legmid2, 19, 60, -3.0F, 0.0F, -0.999F, 6, 1, 2, 0.0F, false));

		leg2a = new ModelRenderer(this);
		leg2a.setRotationPoint(-3.0F, 0.0F, 0.0F);
		legmid2.addChild(leg2a);
		setRotationAngle(leg2a, 0.0F, 0.0F, -0.3927F);
		leg2a.cubeList.add(new ModelBox(leg2a, 33, 67, -3.0F, 0.0F, -1.0F, 3, 1, 2, 0.0F, false));

		leg2b = new ModelRenderer(this);
		leg2b.setRotationPoint(-3.0F, 0.0F, 0.0F);
		leg2a.addChild(leg2b);
		setRotationAngle(leg2b, 0.0F, 0.0F, -0.3927F);
		leg2b.cubeList.add(new ModelBox(leg2b, 22, 67, -3.0F, 0.0F, -1.001F, 3, 1, 2, 0.0F, false));

		leg2c = new ModelRenderer(this);
		leg2c.setRotationPoint(3.0F, 0.0F, 0.0F);
		legmid2.addChild(leg2c);
		setRotationAngle(leg2c, 0.0F, 0.0F, 0.3927F);
		leg2c.cubeList.add(new ModelBox(leg2c, 11, 67, 0.0F, 0.0F, -1.0F, 3, 1, 2, 0.0F, false));

		leg2d = new ModelRenderer(this);
		leg2d.setRotationPoint(3.0F, 0.0F, 0.0F);
		leg2c.addChild(leg2d);
		setRotationAngle(leg2d, 0.0F, 0.0F, 0.3927F);
		leg2d.cubeList.add(new ModelBox(leg2d, 0, 67, 0.0F, 0.0F, -1.001F, 3, 1, 2, 0.0F, false));

		pot_base = new ModelRenderer(this);
		pot_base.setRotationPoint(0.0F, -4.1F, 0.0F);
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
		setRotationAngle(rope2a, -1.3963F, 0.0F, -0.1309F);
		rope2a.cubeList.add(new ModelBox(rope2a, 82, 0, -1.0F, 0.0F, 0.0F, 1, 0, 4, 0.0F, true));

		rope2b = new ModelRenderer(this);
		rope2b.setRotationPoint(0.0F, 0.0F, 4.0F);
		rope2a.addChild(rope2b);
		setRotationAngle(rope2b, 0.3927F, 0.0F, 0.0F);
		rope2b.cubeList.add(new ModelBox(rope2b, 85, 4, -1.0F, 0.0F, 0.0F, 1, 0, 1, 0.0F, true));

		rope2c = new ModelRenderer(this);
		rope2c.setRotationPoint(0.0F, 0.0F, 1.0F);
		rope2b.addChild(rope2c);
		setRotationAngle(rope2c, 0.0F, -0.2182F, 0.0F);
		rope2c.cubeList.add(new ModelBox(rope2c, 85, 5, -1.0F, 0.001F, 0.0F, 1, 0, 1, 0.0F, true));

		rope2d = new ModelRenderer(this);
		rope2d.setRotationPoint(0.0F, 0.0F, 1.0F);
		rope2c.addChild(rope2d);
		setRotationAngle(rope2d, 0.7854F, 0.0F, 0.0F);
		rope2d.cubeList.add(new ModelBox(rope2d, 85, 6, -1.0F, 0.0F, 0.0F, 1, 0, 1, 0.0F, true));

		rope2e = new ModelRenderer(this);
		rope2e.setRotationPoint(0.0F, 0.0F, 1.0F);
		rope2d.addChild(rope2e);
		setRotationAngle(rope2e, 0.0436F, -0.6545F, 0.0F);
		rope2e.cubeList.add(new ModelBox(rope2e, 84, 7, -1.0F, 0.001F, 0.0F, 1, 0, 2, 0.0F, true));

		rope2f = new ModelRenderer(this);
		rope2f.setRotationPoint(0.0F, 0.0F, 2.0F);
		rope2e.addChild(rope2f);
		setRotationAngle(rope2f, 0.0F, -0.6981F, 0.0F);
		rope2f.cubeList.add(new ModelBox(rope2f, 85, 9, -1.0F, 0.0F, 0.0F, 1, 0, 1, 0.0F, true));

		rope2g = new ModelRenderer(this);
		rope2g.setRotationPoint(0.0F, 0.0F, 1.0F);
		rope2f.addChild(rope2g);
		setRotationAngle(rope2g, 0.48F, 0.0F, 0.0F);
		rope2g.cubeList.add(new ModelBox(rope2g, 85, 10, -1.0F, 0.0F, 0.0F, 1, 0, 1, 0.0F, true));

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
		setRotationAngle(rope1a, -1.309F, 0.0F, 0.1309F);
		rope1a.cubeList.add(new ModelBox(rope1a, 80, 0, 0.0F, 0.0F, 0.0F, 1, 0, 3, 0.0F, false));

		rope1b = new ModelRenderer(this);
		rope1b.setRotationPoint(0.0F, 0.0F, 3.0F);
		rope1a.addChild(rope1b);
		setRotationAngle(rope1b, 0.3927F, 0.0F, 0.0F);
		rope1b.cubeList.add(new ModelBox(rope1b, 82, 3, 0.0F, 0.0F, 0.0F, 1, 0, 1, 0.0F, false));

		rope1c = new ModelRenderer(this);
		rope1c.setRotationPoint(0.0F, 0.0F, 1.0F);
		rope1b.addChild(rope1c);
		setRotationAngle(rope1c, 0.0F, 0.3491F, 0.0F);
		rope1c.cubeList.add(new ModelBox(rope1c, 81, 4, 0.0F, 0.001F, 0.0F, 1, 0, 2, 0.0F, false));

		rope1d = new ModelRenderer(this);
		rope1d.setRotationPoint(0.0F, 0.0F, 2.0F);
		rope1c.addChild(rope1d);
		setRotationAngle(rope1d, 0.5236F, 0.0F, 0.0F);
		rope1d.cubeList.add(new ModelBox(rope1d, 82, 6, 0.0F, 0.0F, 0.0F, 1, 0, 1, 0.0F, false));

		rope1e = new ModelRenderer(this);
		rope1e.setRotationPoint(0.0F, 0.0F, 1.0F);
		rope1d.addChild(rope1e);
		setRotationAngle(rope1e, 0.0F, 0.5236F, 0.0F);
		rope1e.cubeList.add(new ModelBox(rope1e, 81, 7, 0.0F, 0.001F, 0.0F, 1, 0, 2, 0.0F, false));

		rope1f = new ModelRenderer(this);
		rope1f.setRotationPoint(0.0F, 0.0F, 2.0F);
		rope1e.addChild(rope1f);
		setRotationAngle(rope1f, 0.0F, 0.7418F, 0.0F);
		rope1f.cubeList.add(new ModelBox(rope1f, 81, 9, 0.0F, 0.0F, 0.0F, 1, 0, 2, 0.0F, false));

		handle_wood = new ModelRenderer(this);
		handle_wood.setRotationPoint(0.5F, 0.0F, 2.0F);
		rope1f.addChild(handle_wood);
		setRotationAngle(handle_wood, -0.1139F, 0.1087F, -0.2225F);
		handle_wood.cubeList.add(new ModelBox(handle_wood, 58, 1, -1.0F, -0.5F, -0.5F, 2, 1, 6, 0.0F, false));

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