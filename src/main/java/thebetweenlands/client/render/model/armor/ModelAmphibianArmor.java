package thebetweenlands.client.render.model.armor;

import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;

// Made with Blockbench 3.8.4
// Exported for Minecraft version 1.7 - 1.12
// Paste this class into your mod and generate all required imports


public class ModelAmphibianArmor extends ModelBodyAttachment {
	//private final ModelRenderer bipedHead;
	private final ModelRenderer Helmet_main;
	private final ModelRenderer Helmet_base;
	private final ModelRenderer Helmet_plate_b1a;
	private final ModelRenderer Helmet_front;
	private final ModelRenderer Helmet_nasalplate1a;
	private final ModelRenderer Whisker_l1a;
	private final ModelRenderer Whisker_l1b;
	private final ModelRenderer Whisker_l1c;
	private final ModelRenderer Whisker_l1d;
	private final ModelRenderer Whisker_l1a2;
	private final ModelRenderer Whisker_l1b2;
	private final ModelRenderer Whisker_l1c2;
	private final ModelRenderer Whisker_l1d2;
	private final ModelRenderer Helmet_plate_l1a;
	private final ModelRenderer Helmet_plate_l1b;
	private final ModelRenderer Helmet_plate_l1c;
	private final ModelRenderer Helmet_gill_l2a;
	private final ModelRenderer Helmet_gill_l2b;
	private final ModelRenderer Helmet_gill_l2c;
	private final ModelRenderer Helmet_gill_l1a;
	private final ModelRenderer Helmet_plate_l1a2;
	private final ModelRenderer Helmet_plate_l1b2;
	private final ModelRenderer Helmet_plate_l1c2;
	private final ModelRenderer Helmet_gill_l2a2;
	private final ModelRenderer Helmet_gill_l2b2;
	private final ModelRenderer Helmet_gill_l2c2;
	private final ModelRenderer Helmet_gill_l1a2;
	//private final ModelRenderer bipedBody;
	private final ModelRenderer Chestplate_main;
	private final ModelRenderer Chestplate_base;
	private final ModelRenderer Chestfin_mid1a;
	private final ModelRenderer Chestplate_chestpiece_left1a;
	private final ModelRenderer Chestfin_left1a;
	private final ModelRenderer Chestplate_chestpiece_right1a;
	private final ModelRenderer Chestfin_right1a;
	private final ModelRenderer Chestplate_back;
	private final ModelRenderer Chestfin_back1a;
	private final ModelRenderer Chestplate_torso;
	private final ModelRenderer Leggings_base;
	private final ModelRenderer Belt_left1a;
	private final ModelRenderer Beltfin_left1a;
	private final ModelRenderer Belt_right1a;
	private final ModelRenderer Beltfin_right1a;
	private final ModelRenderer Buckle_front1a;
	private final ModelRenderer Buckle_front1b;
	private final ModelRenderer Buckle_front1a2;
	private final ModelRenderer Buckle_front1b2;
	//private final ModelRenderer bipedRightArm;
	private final ModelRenderer Shoulderplate_rightmain;
	private final ModelRenderer Shoulderplate_right1a;
	private final ModelRenderer Shoulderplate_right1b;
	private final ModelRenderer shoulderplate_right1b_r1;
	//private final ModelRenderer bipedLeftArm;
	private final ModelRenderer Shoulderplate_leftmain;
	private final ModelRenderer Shoulderplate_left1a;
	private final ModelRenderer Shoulderplate_left1b;
	private final ModelRenderer shoulderplate_left1b_r1;
	//private final ModelRenderer bipedRightLeg;
	private final ModelRenderer Leggings_leg_right1a;
	private final ModelRenderer Boot_right_base;
	private final ModelRenderer Boot_right1a;
	private final ModelRenderer Bootfin_right1b;
	private final ModelRenderer Boot_right1b;
	private final ModelRenderer Boot_right1c;
	private final ModelRenderer Bootfin_right1a;
	private final ModelRenderer Bootfin_right1c;
	//private final ModelRenderer bipedLeftLeg;
	private final ModelRenderer Leggings_leg_left1a;
	private final ModelRenderer Boot_left_base;
	private final ModelRenderer Boot_left1a;
	private final ModelRenderer Bootfin_left1b;
	private final ModelRenderer Boot_left1b;
	private final ModelRenderer Boot_left1c;
	private final ModelRenderer Bootfin_left1a;
	private final ModelRenderer Bootfin_left1c;

	public ModelAmphibianArmor() {
		textureWidth = 128;
		textureHeight = 128;

		//bipedHead = new ModelRenderer(this);
		//bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);


		Helmet_main = new ModelRenderer(this);
		Helmet_main.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedHead.addChild(Helmet_main);


		Helmet_base = new ModelRenderer(this);
		Helmet_base.setRotationPoint(0.0F, -3.0F, -0.25F);
		Helmet_main.addChild(Helmet_base);
		setRotationAngle(Helmet_base, -0.0873F, 0.0F, 0.0F);
		Helmet_base.cubeList.add(new ModelBox(Helmet_base, 58, 0, -4.55F, -6.0F, 0.0F, 9, 5, 5, 0.0F, false));

		Helmet_plate_b1a = new ModelRenderer(this);
		Helmet_plate_b1a.setRotationPoint(0.0F, -1.0F, 5.0F);
		Helmet_base.addChild(Helmet_plate_b1a);
		setRotationAngle(Helmet_plate_b1a, 0.1309F, 0.0F, 0.0F);
		Helmet_plate_b1a.cubeList.add(new ModelBox(Helmet_plate_b1a, 29, 0, -4.545F, -1.0F, -5.0F, 9, 5, 5, 0.0F, false));

		Helmet_front = new ModelRenderer(this);
		Helmet_front.setRotationPoint(0.0F, -6.0F, 0.0F);
		Helmet_base.addChild(Helmet_front);
		setRotationAngle(Helmet_front, 0.0873F, 0.0F, 0.0F);
		Helmet_front.cubeList.add(new ModelBox(Helmet_front, 0, 15, -4.555F, 0.0F, -5.0F, 9, 3, 5, 0.0F, false));

		Helmet_nasalplate1a = new ModelRenderer(this);
		Helmet_nasalplate1a.setRotationPoint(0.0F, 3.0F, -5.0F);
		Helmet_front.addChild(Helmet_nasalplate1a);
		setRotationAngle(Helmet_nasalplate1a, -0.0873F, 0.0F, 0.0F);
		Helmet_nasalplate1a.cubeList.add(new ModelBox(Helmet_nasalplate1a, 10, 97, -1.0F, -1.0F, 0.0F, 2, 4, 1, 0.0F, false));

		Whisker_l1a = new ModelRenderer(this);
		Whisker_l1a.setRotationPoint(1.0F, 3.0F, 0.0F);
		Helmet_nasalplate1a.addChild(Whisker_l1a);
		setRotationAngle(Whisker_l1a, -0.1309F, 0.2618F, 0.3927F);
		Whisker_l1a.cubeList.add(new ModelBox(Whisker_l1a, 38, 97, -1.0F, -1.0F, 0.0F, 3, 1, 0, 0.0F, false));

		Whisker_l1b = new ModelRenderer(this);
		Whisker_l1b.setRotationPoint(2.0F, 0.0F, 0.0F);
		Whisker_l1a.addChild(Whisker_l1b);
		setRotationAngle(Whisker_l1b, 0.0F, -0.3927F, 0.0F);
		Whisker_l1b.cubeList.add(new ModelBox(Whisker_l1b, 10, 105, 0.0F, -1.0F, 0.0F, 2, 1, 0, 0.0F, false));

		Whisker_l1c = new ModelRenderer(this);
		Whisker_l1c.setRotationPoint(2.0F, 0.0F, 0.0F);
		Whisker_l1b.addChild(Whisker_l1c);
		setRotationAngle(Whisker_l1c, 0.0F, -0.3927F, 0.0F);
		Whisker_l1c.cubeList.add(new ModelBox(Whisker_l1c, 5, 105, 0.0F, -1.0F, 0.0F, 2, 1, 0, 0.0F, false));

		Whisker_l1d = new ModelRenderer(this);
		Whisker_l1d.setRotationPoint(2.0F, 0.0F, 0.0F);
		Whisker_l1c.addChild(Whisker_l1d);
		setRotationAngle(Whisker_l1d, 0.0F, 0.0F, -0.3927F);
		Whisker_l1d.cubeList.add(new ModelBox(Whisker_l1d, 38, 102, 0.0F, -2.0F, -0.005F, 2, 2, 0, 0.0F, false));

		Whisker_l1a2 = new ModelRenderer(this);
		Whisker_l1a2.setRotationPoint(-1.0F, 3.0F, 0.0F);
		Helmet_nasalplate1a.addChild(Whisker_l1a2);
		setRotationAngle(Whisker_l1a2, -0.1309F, -0.2618F, -0.3927F);
		Whisker_l1a2.cubeList.add(new ModelBox(Whisker_l1a2, 31, 102, -2.0F, -1.0F, 0.0F, 3, 1, 0, 0.0F, false));

		Whisker_l1b2 = new ModelRenderer(this);
		Whisker_l1b2.setRotationPoint(-2.0F, 0.0F, 0.0F);
		Whisker_l1a2.addChild(Whisker_l1b2);
		setRotationAngle(Whisker_l1b2, 0.0F, 0.3927F, 0.0F);
		Whisker_l1b2.cubeList.add(new ModelBox(Whisker_l1b2, 0, 105, -2.0F, -1.0F, 0.0F, 2, 1, 0, 0.0F, false));

		Whisker_l1c2 = new ModelRenderer(this);
		Whisker_l1c2.setRotationPoint(-2.0F, 0.0F, 0.0F);
		Whisker_l1b2.addChild(Whisker_l1c2);
		setRotationAngle(Whisker_l1c2, 0.0F, 0.3927F, 0.0F);
		Whisker_l1c2.cubeList.add(new ModelBox(Whisker_l1c2, 45, 97, -2.0F, -1.0F, 0.0F, 2, 1, 0, 0.0F, false));

		Whisker_l1d2 = new ModelRenderer(this);
		Whisker_l1d2.setRotationPoint(-2.0F, 0.0F, 0.0F);
		Whisker_l1c2.addChild(Whisker_l1d2);
		setRotationAngle(Whisker_l1d2, 0.0F, 0.0F, 0.3927F);
		Whisker_l1d2.cubeList.add(new ModelBox(Whisker_l1d2, 38, 99, -2.0F, -2.0F, -0.005F, 2, 2, 0, 0.0F, false));

		Helmet_plate_l1a = new ModelRenderer(this);
		Helmet_plate_l1a.setRotationPoint(4.555F, 3.0F, -2.0F);
		Helmet_front.addChild(Helmet_plate_l1a);
		setRotationAngle(Helmet_plate_l1a, 0.0F, 0.0F, -0.1309F);
		Helmet_plate_l1a.cubeList.add(new ModelBox(Helmet_plate_l1a, 31, 81, -1.0F, -1.0F, -1.0F, 1, 3, 3, 0.0F, false));

		Helmet_plate_l1b = new ModelRenderer(this);
		Helmet_plate_l1b.setRotationPoint(0.0F, 2.0F, 2.0F);
		Helmet_plate_l1a.addChild(Helmet_plate_l1b);
		setRotationAngle(Helmet_plate_l1b, -0.2182F, 0.0F, 0.0F);
		Helmet_plate_l1b.cubeList.add(new ModelBox(Helmet_plate_l1b, 18, 89, -1.005F, 0.0F, -3.0F, 1, 1, 3, 0.0F, false));

		Helmet_plate_l1c = new ModelRenderer(this);
		Helmet_plate_l1c.setRotationPoint(-1.005F, 1.0F, -2.0F);
		Helmet_plate_l1b.addChild(Helmet_plate_l1c);
		setRotationAngle(Helmet_plate_l1c, 0.0F, 0.0F, -0.1745F);
		Helmet_plate_l1c.cubeList.add(new ModelBox(Helmet_plate_l1c, 39, 73, 0.0F, 0.0F, -1.99F, 1, 3, 4, 0.0F, false));

		Helmet_gill_l2a = new ModelRenderer(this);
		Helmet_gill_l2a.setRotationPoint(0.505F, 1.0F, 2.0F);
		Helmet_plate_l1c.addChild(Helmet_gill_l2a);
		setRotationAngle(Helmet_gill_l2a, 0.0F, 0.1309F, 0.0F);
		Helmet_gill_l2a.cubeList.add(new ModelBox(Helmet_gill_l2a, 5, 97, 0.0F, -3.0F, 0.0F, 0, 5, 2, 0.0F, false));

		Helmet_gill_l2b = new ModelRenderer(this);
		Helmet_gill_l2b.setRotationPoint(0.0F, 0.0F, 2.0F);
		Helmet_gill_l2a.addChild(Helmet_gill_l2b);
		setRotationAngle(Helmet_gill_l2b, 0.0F, 0.3491F, 0.0F);
		Helmet_gill_l2b.cubeList.add(new ModelBox(Helmet_gill_l2b, 0, 97, 0.0F, -3.0F, 0.0F, 0, 5, 2, 0.0F, false));

		Helmet_gill_l2c = new ModelRenderer(this);
		Helmet_gill_l2c.setRotationPoint(1.005F, 1.0F, 2.0F);
		Helmet_plate_l1c.addChild(Helmet_gill_l2c);
		setRotationAngle(Helmet_gill_l2c, 0.0F, 0.1745F, 0.0F);
		Helmet_gill_l2c.cubeList.add(new ModelBox(Helmet_gill_l2c, 51, 89, 0.0F, -3.0F, 0.0F, 0, 5, 2, 0.0F, false));

		Helmet_gill_l1a = new ModelRenderer(this);
		Helmet_gill_l1a.setRotationPoint(0.0F, 2.0F, 2.0F);
		Helmet_plate_l1a.addChild(Helmet_gill_l1a);
		setRotationAngle(Helmet_gill_l1a, 0.0F, 0.0436F, 0.0F);
		Helmet_gill_l1a.cubeList.add(new ModelBox(Helmet_gill_l1a, 31, 97, 0.0F, -2.0F, 0.0F, 0, 2, 2, 0.0F, false));

		Helmet_plate_l1a2 = new ModelRenderer(this);
		Helmet_plate_l1a2.setRotationPoint(-4.555F, 3.0F, -2.0F);
		Helmet_front.addChild(Helmet_plate_l1a2);
		setRotationAngle(Helmet_plate_l1a2, 0.0F, 0.0F, 0.1309F);
		Helmet_plate_l1a2.cubeList.add(new ModelBox(Helmet_plate_l1a2, 22, 81, 0.0F, -1.0F, -1.0F, 1, 3, 3, 0.0F, false));

		Helmet_plate_l1b2 = new ModelRenderer(this);
		Helmet_plate_l1b2.setRotationPoint(0.0F, 2.0F, 2.0F);
		Helmet_plate_l1a2.addChild(Helmet_plate_l1b2);
		setRotationAngle(Helmet_plate_l1b2, -0.2182F, 0.0F, 0.0F);
		Helmet_plate_l1b2.cubeList.add(new ModelBox(Helmet_plate_l1b2, 9, 89, 0.005F, 0.0F, -3.0F, 1, 1, 3, 0.0F, false));

		Helmet_plate_l1c2 = new ModelRenderer(this);
		Helmet_plate_l1c2.setRotationPoint(1.005F, 1.0F, -2.0F);
		Helmet_plate_l1b2.addChild(Helmet_plate_l1c2);
		setRotationAngle(Helmet_plate_l1c2, 0.0F, 0.0F, 0.1745F);
		Helmet_plate_l1c2.cubeList.add(new ModelBox(Helmet_plate_l1c2, 28, 73, -1.0F, 0.0F, -1.99F, 1, 3, 4, 0.0F, false));

		Helmet_gill_l2a2 = new ModelRenderer(this);
		Helmet_gill_l2a2.setRotationPoint(-0.505F, 1.0F, 2.0F);
		Helmet_plate_l1c2.addChild(Helmet_gill_l2a2);
		setRotationAngle(Helmet_gill_l2a2, 0.0F, -0.1309F, 0.0F);
		Helmet_gill_l2a2.cubeList.add(new ModelBox(Helmet_gill_l2a2, 46, 89, 0.0F, -3.0F, 0.0F, 0, 5, 2, 0.0F, false));

		Helmet_gill_l2b2 = new ModelRenderer(this);
		Helmet_gill_l2b2.setRotationPoint(0.0F, 0.0F, 2.0F);
		Helmet_gill_l2a2.addChild(Helmet_gill_l2b2);
		setRotationAngle(Helmet_gill_l2b2, 0.0F, -0.3491F, 0.0F);
		Helmet_gill_l2b2.cubeList.add(new ModelBox(Helmet_gill_l2b2, 41, 89, 0.0F, -3.0F, 0.0F, 0, 5, 2, 0.0F, false));

		Helmet_gill_l2c2 = new ModelRenderer(this);
		Helmet_gill_l2c2.setRotationPoint(-1.005F, 1.0F, 2.0F);
		Helmet_plate_l1c2.addChild(Helmet_gill_l2c2);
		setRotationAngle(Helmet_gill_l2c2, 0.0F, -0.1745F, 0.0F);
		Helmet_gill_l2c2.cubeList.add(new ModelBox(Helmet_gill_l2c2, 36, 89, 0.0F, -3.0F, 0.0F, 0, 5, 2, 0.0F, false));

		Helmet_gill_l1a2 = new ModelRenderer(this);
		Helmet_gill_l1a2.setRotationPoint(0.0F, 2.0F, 2.0F);
		Helmet_plate_l1a2.addChild(Helmet_gill_l1a2);
		setRotationAngle(Helmet_gill_l1a2, 0.0F, -0.0436F, 0.0F);
		Helmet_gill_l1a2.cubeList.add(new ModelBox(Helmet_gill_l1a2, 26, 97, 0.0F, -2.0F, 0.0F, 0, 2, 2, 0.0F, false));

		//bipedBody = new ModelRenderer(this);
		//bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);


		Chestplate_main = new ModelRenderer(this);
		Chestplate_main.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedBody.addChild(Chestplate_main);


		Chestplate_base = new ModelRenderer(this);
		Chestplate_base.setRotationPoint(0.0F, 2.5F, -2.5F);
		Chestplate_main.addChild(Chestplate_base);
		setRotationAngle(Chestplate_base, 0.0873F, 0.0F, 0.0F);
		Chestplate_base.cubeList.add(new ModelBox(Chestplate_base, 15, 73, -2.0F, -2.0F, -1.0F, 4, 4, 2, 0.0F, false));

		Chestfin_mid1a = new ModelRenderer(this);
		Chestfin_mid1a.setRotationPoint(0.0F, 2.0F, -1.0F);
		Chestplate_base.addChild(Chestfin_mid1a);
		setRotationAngle(Chestfin_mid1a, -0.1745F, 0.0F, 0.0F);
		Chestfin_mid1a.cubeList.add(new ModelBox(Chestfin_mid1a, 27, 93, -2.0F, 0.0F, 0.0F, 4, 3, 0, 0.0F, false));

		Chestplate_chestpiece_left1a = new ModelRenderer(this);
		Chestplate_chestpiece_left1a.setRotationPoint(2.0F, 2.0F, -1.0F);
		Chestplate_base.addChild(Chestplate_chestpiece_left1a);
		setRotationAngle(Chestplate_chestpiece_left1a, 0.0F, -0.1309F, -0.2182F);
		Chestplate_chestpiece_left1a.cubeList.add(new ModelBox(Chestplate_chestpiece_left1a, 21, 31, 0.0F, -4.0F, 0.0F, 4, 4, 6, 0.0F, false));

		Chestfin_left1a = new ModelRenderer(this);
		Chestfin_left1a.setRotationPoint(0.0F, 0.0F, 0.0F);
		Chestplate_chestpiece_left1a.addChild(Chestfin_left1a);
		setRotationAngle(Chestfin_left1a, -0.1309F, 0.0F, 0.0F);
		Chestfin_left1a.cubeList.add(new ModelBox(Chestfin_left1a, 17, 100, 0.0F, 0.0F, 0.0F, 4, 2, 0, 0.0F, false));

		Chestplate_chestpiece_right1a = new ModelRenderer(this);
		Chestplate_chestpiece_right1a.setRotationPoint(-2.0F, 2.0F, -1.0F);
		Chestplate_base.addChild(Chestplate_chestpiece_right1a);
		setRotationAngle(Chestplate_chestpiece_right1a, 0.0F, 0.1309F, 0.2182F);
		Chestplate_chestpiece_right1a.cubeList.add(new ModelBox(Chestplate_chestpiece_right1a, 0, 31, -4.0F, -4.0F, 0.0F, 4, 4, 6, 0.0F, false));

		Chestfin_right1a = new ModelRenderer(this);
		Chestfin_right1a.setRotationPoint(0.0F, 0.0F, 0.0F);
		Chestplate_chestpiece_right1a.addChild(Chestfin_right1a);
		setRotationAngle(Chestfin_right1a, -0.1309F, 0.0F, 0.0F);
		Chestfin_right1a.cubeList.add(new ModelBox(Chestfin_right1a, 17, 97, -4.0F, 0.0F, 0.0F, 4, 2, 0, 0.0F, false));

		Chestplate_back = new ModelRenderer(this);
		Chestplate_back.setRotationPoint(0.0F, 0.0F, 4.5F);
		Chestplate_base.addChild(Chestplate_back);
		setRotationAngle(Chestplate_back, -0.1309F, 0.0F, 0.0F);
		Chestplate_back.cubeList.add(new ModelBox(Chestplate_back, 68, 52, -3.0F, -2.0F, -1.0F, 6, 5, 2, 0.0F, false));

		Chestfin_back1a = new ModelRenderer(this);
		Chestfin_back1a.setRotationPoint(0.0F, 3.0F, 1.0F);
		Chestplate_back.addChild(Chestfin_back1a);
		setRotationAngle(Chestfin_back1a, 0.1745F, 0.0F, 0.0F);
		Chestfin_back1a.cubeList.add(new ModelBox(Chestfin_back1a, 9, 85, -3.0F, 0.0F, 0.0F, 6, 3, 0, 0.0F, false));

		Chestplate_torso = new ModelRenderer(this);
		Chestplate_torso.setRotationPoint(0.0F, 5.0F, -1.0F);
		Chestplate_main.addChild(Chestplate_torso);
		Chestplate_torso.cubeList.add(new ModelBox(Chestplate_torso, 0, 0, -4.46F, -2.0F, -1.5F, 9, 9, 5, 0.0F, false));

		Leggings_base = new ModelRenderer(this);
		Leggings_base.setRotationPoint(0.0F, 12.0F, 0.0F);
		bipedBody.addChild(Leggings_base);
		setRotationAngle(Leggings_base, 0.0873F, 0.0F, 0.0F);


		Belt_left1a = new ModelRenderer(this);
		Belt_left1a.setRotationPoint(1.0F, -1.5F, 0.0F);
		Leggings_base.addChild(Belt_left1a);
		setRotationAngle(Belt_left1a, 0.0F, 0.0F, 0.1309F);
		Belt_left1a.cubeList.add(new ModelBox(Belt_left1a, 63, 31, 0.0F, 0.0F, -3.0F, 4, 2, 6, 0.0F, false));

		Beltfin_left1a = new ModelRenderer(this);
		Beltfin_left1a.setRotationPoint(4.0F, 2.0F, 0.0F);
		Belt_left1a.addChild(Beltfin_left1a);
		setRotationAngle(Beltfin_left1a, 0.0F, 0.0F, -0.2618F);
		Beltfin_left1a.cubeList.add(new ModelBox(Beltfin_left1a, 13, 62, 0.0F, 0.0F, -3.0F, 0, 4, 6, 0.0F, false));

		Belt_right1a = new ModelRenderer(this);
		Belt_right1a.setRotationPoint(-1.0F, -1.5F, 0.0F);
		Leggings_base.addChild(Belt_right1a);
		setRotationAngle(Belt_right1a, 0.0F, 0.0F, -0.1309F);
		Belt_right1a.cubeList.add(new ModelBox(Belt_right1a, 42, 31, -4.0F, 0.0F, -3.0F, 4, 2, 6, 0.0F, false));

		Beltfin_right1a = new ModelRenderer(this);
		Beltfin_right1a.setRotationPoint(-4.0F, 2.0F, 0.0F);
		Belt_right1a.addChild(Beltfin_right1a);
		setRotationAngle(Beltfin_right1a, 0.0F, 0.0F, 0.2618F);
		Beltfin_right1a.cubeList.add(new ModelBox(Beltfin_right1a, 0, 62, 0.0F, 0.0F, -3.0F, 0, 4, 6, 0.0F, false));

		Buckle_front1a = new ModelRenderer(this);
		Buckle_front1a.setRotationPoint(0.0F, 0.0F, -2.5F);
		Leggings_base.addChild(Buckle_front1a);
		setRotationAngle(Buckle_front1a, -0.2182F, 0.0F, 0.0F);
		Buckle_front1a.cubeList.add(new ModelBox(Buckle_front1a, 0, 89, -1.005F, -2.0F, -1.0F, 2, 2, 2, 0.0F, false));

		Buckle_front1b = new ModelRenderer(this);
		Buckle_front1b.setRotationPoint(0.0F, 0.0F, -1.0F);
		Buckle_front1a.addChild(Buckle_front1b);
		setRotationAngle(Buckle_front1b, 0.2618F, 0.0F, 0.0F);
		Buckle_front1b.cubeList.add(new ModelBox(Buckle_front1b, 27, 89, -1.0F, 0.0F, 0.0F, 2, 1, 2, 0.0F, false));

		Buckle_front1a2 = new ModelRenderer(this);
		Buckle_front1a2.setRotationPoint(0.0F, -0.5F, 2.5F);
		Leggings_base.addChild(Buckle_front1a2);
		Buckle_front1a2.cubeList.add(new ModelBox(Buckle_front1a2, 9, 81, -2.005F, -1.0F, -1.0F, 4, 1, 2, 0.0F, false));

		Buckle_front1b2 = new ModelRenderer(this);
		Buckle_front1b2.setRotationPoint(0.0F, 0.0F, 1.0F);
		Buckle_front1a2.addChild(Buckle_front1b2);
		setRotationAngle(Buckle_front1b2, -0.2618F, 0.0F, 0.0F);
		Buckle_front1b2.cubeList.add(new ModelBox(Buckle_front1b2, 50, 73, -2.0F, 0.0F, -2.0F, 4, 2, 2, 0.0F, false));

		//bipedRightArm = new ModelRenderer(this);
		//bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);


		Shoulderplate_rightmain = new ModelRenderer(this);
		Shoulderplate_rightmain.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedRightArm.addChild(Shoulderplate_rightmain);


		Shoulderplate_right1a = new ModelRenderer(this);
		Shoulderplate_right1a.setRotationPoint(-1.0F, -2.0F, 0.0F);
		Shoulderplate_rightmain.addChild(Shoulderplate_right1a);
		setRotationAngle(Shoulderplate_right1a, 0.0F, 0.0F, -0.2618F);
		Shoulderplate_right1a.cubeList.add(new ModelBox(Shoulderplate_right1a, 21, 42, -3.0F, -1.0F, -2.505F, 5, 3, 5, 0.0F, false));

		Shoulderplate_right1b = new ModelRenderer(this);
		Shoulderplate_right1b.setRotationPoint(-3.0F, 2.0F, 0.0F);
		Shoulderplate_right1a.addChild(Shoulderplate_right1b);


		shoulderplate_right1b_r1 = new ModelRenderer(this);
		shoulderplate_right1b_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
		Shoulderplate_right1b.addChild(shoulderplate_right1b_r1);
		setRotationAngle(shoulderplate_right1b_r1, 0.0F, 0.0F, 0.3491F);
		shoulderplate_right1b_r1.cubeList.add(new ModelBox(shoulderplate_right1b_r1, 61, 42, 0.0F, -2.0F, -2.5F, 4, 4, 5, 0.0F, false));

		//bipedLeftArm = new ModelRenderer(this);
		//bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);


		Shoulderplate_leftmain = new ModelRenderer(this);
		Shoulderplate_leftmain.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedLeftArm.addChild(Shoulderplate_leftmain);


		Shoulderplate_left1a = new ModelRenderer(this);
		Shoulderplate_left1a.setRotationPoint(1.0F, -2.0F, 0.0F);
		Shoulderplate_leftmain.addChild(Shoulderplate_left1a);
		setRotationAngle(Shoulderplate_left1a, 0.0F, 0.0F, 0.2618F);
		Shoulderplate_left1a.cubeList.add(new ModelBox(Shoulderplate_left1a, 0, 42, -2.0F, -1.0F, -2.505F, 5, 3, 5, 0.0F, false));

		Shoulderplate_left1b = new ModelRenderer(this);
		Shoulderplate_left1b.setRotationPoint(3.0F, 2.0F, 0.0F);
		Shoulderplate_left1a.addChild(Shoulderplate_left1b);


		shoulderplate_left1b_r1 = new ModelRenderer(this);
		shoulderplate_left1b_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
		Shoulderplate_left1b.addChild(shoulderplate_left1b_r1);
		setRotationAngle(shoulderplate_left1b_r1, 0.0F, 0.0F, -0.3491F);
		shoulderplate_left1b_r1.cubeList.add(new ModelBox(shoulderplate_left1b_r1, 42, 42, -4.0F, -2.0F, -2.5F, 4, 4, 5, 0.0F, false));

		//bipedRightLeg = new ModelRenderer(this);
		//bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);


		Leggings_leg_right1a = new ModelRenderer(this);
		Leggings_leg_right1a.setRotationPoint(-0.1F, 0.0F, 0.0F);
		bipedRightLeg.addChild(Leggings_leg_right1a);
		Leggings_leg_right1a.cubeList.add(new ModelBox(Leggings_leg_right1a, 50, 15, -2.5F, 0.0F, -2.5F, 5, 10, 5, 0.0F, false));

		Boot_right_base = new ModelRenderer(this);
		Boot_right_base.setRotationPoint(-0.1F, 0.0F, 0.0F);
		bipedRightBoot.addChild(Boot_right_base);


		Boot_right1a = new ModelRenderer(this);
		Boot_right1a.setRotationPoint(0.0F, 10.5F, -0.5F);
		Boot_right_base.addChild(Boot_right1a);
		Boot_right1a.cubeList.add(new ModelBox(Boot_right1a, 0, 52, -2.51F, -2.0F, -0.99F, 5, 4, 4, 0.0F, false));

		Bootfin_right1b = new ModelRenderer(this);
		Bootfin_right1b.setRotationPoint(-2.51F, -2.0F, 0.0F);
		Boot_right1a.addChild(Bootfin_right1b);
		setRotationAngle(Bootfin_right1b, 0.0F, 0.0F, 0.1745F);
		Bootfin_right1b.cubeList.add(new ModelBox(Bootfin_right1b, 63, 73, 0.0F, 0.0F, -1.0F, 0, 3, 4, 0.0F, false));

		Boot_right1b = new ModelRenderer(this);
		Boot_right1b.setRotationPoint(0.0F, 2.0F, -0.99F);
		Boot_right1a.addChild(Boot_right1b);
		setRotationAngle(Boot_right1b, -0.0873F, 0.0F, 0.0F);
		Boot_right1b.cubeList.add(new ModelBox(Boot_right1b, 38, 52, -2.515F, -7.0F, -2.0F, 5, 7, 2, 0.0F, false));

		Boot_right1c = new ModelRenderer(this);
		Boot_right1c.setRotationPoint(0.0F, 0.0F, -2.0F);
		Boot_right1b.addChild(Boot_right1c);
		setRotationAngle(Boot_right1c, 0.3054F, 0.0F, 0.0F);
		Boot_right1c.cubeList.add(new ModelBox(Boot_right1c, 26, 66, -2.5F, -3.0F, -1.0F, 5, 3, 2, 0.0F, false));

		Bootfin_right1a = new ModelRenderer(this);
		Bootfin_right1a.setRotationPoint(0.0F, -3.0F, -1.0F);
		Boot_right1c.addChild(Bootfin_right1a);
		setRotationAngle(Bootfin_right1a, 0.3054F, 0.0F, 0.0F);
		Bootfin_right1a.cubeList.add(new ModelBox(Bootfin_right1a, 26, 62, -2.5F, 0.0F, -3.0F, 5, 0, 3, 0.0F, false));

		Bootfin_right1c = new ModelRenderer(this);
		Bootfin_right1c.setRotationPoint(0.0F, -2.0F, 3.01F);
		Boot_right1a.addChild(Bootfin_right1c);
		setRotationAngle(Bootfin_right1c, 0.1745F, 0.0F, 0.0F);
		Bootfin_right1c.cubeList.add(new ModelBox(Bootfin_right1c, 40, 81, -2.5F, 0.0F, 0.0F, 5, 3, 0, 0.0F, false));

		//bipedLeftLeg = new ModelRenderer(this);
		//bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);


		Leggings_leg_left1a = new ModelRenderer(this);
		Leggings_leg_left1a.setRotationPoint(0.1F, 0.0F, 0.0F);
		bipedLeftLeg.addChild(Leggings_leg_left1a);
		Leggings_leg_left1a.cubeList.add(new ModelBox(Leggings_leg_left1a, 29, 15, -2.5F, -0.01F, -2.51F, 5, 10, 5, 0.0F, false));

		Boot_left_base = new ModelRenderer(this);
		Boot_left_base.setRotationPoint(0.1F, 0.0F, 0.0F);
		bipedLeftBoot.addChild(Boot_left_base);


		Boot_left1a = new ModelRenderer(this);
		Boot_left1a.setRotationPoint(0.0F, 10.5F, -0.5F);
		Boot_left_base.addChild(Boot_left1a);
		Boot_left1a.cubeList.add(new ModelBox(Boot_left1a, 19, 52, -2.51F, -2.0F, -0.99F, 5, 4, 4, 0.0F, false));

		Bootfin_left1b = new ModelRenderer(this);
		Bootfin_left1b.setRotationPoint(2.51F, -2.0F, 0.0F);
		Boot_left1a.addChild(Bootfin_left1b);
		setRotationAngle(Bootfin_left1b, 0.0F, 0.0F, -0.1745F);
		Bootfin_left1b.cubeList.add(new ModelBox(Bootfin_left1b, 0, 81, 0.0F, 0.0F, -1.0F, 0, 3, 4, 0.0F, false));

		Boot_left1b = new ModelRenderer(this);
		Boot_left1b.setRotationPoint(0.0F, 2.0F, -0.99F);
		Boot_left1a.addChild(Boot_left1b);
		setRotationAngle(Boot_left1b, -0.0873F, 0.0F, 0.0F);
		Boot_left1b.cubeList.add(new ModelBox(Boot_left1b, 53, 52, -2.515F, -7.0F, -2.0F, 5, 7, 2, 0.0F, false));

		Boot_left1c = new ModelRenderer(this);
		Boot_left1c.setRotationPoint(0.0F, 0.0F, -2.0F);
		Boot_left1b.addChild(Boot_left1c);
		setRotationAngle(Boot_left1c, 0.3054F, 0.0F, 0.0F);
		Boot_left1c.cubeList.add(new ModelBox(Boot_left1c, 0, 73, -2.5F, -3.0F, -1.0F, 5, 3, 2, 0.0F, false));

		Bootfin_left1a = new ModelRenderer(this);
		Bootfin_left1a.setRotationPoint(0.0F, -3.0F, -1.0F);
		Boot_left1c.addChild(Bootfin_left1a);
		setRotationAngle(Bootfin_left1a, 0.3054F, 0.0F, 0.0F);
		Bootfin_left1a.cubeList.add(new ModelBox(Bootfin_left1a, 43, 62, -2.5F, 0.0F, -3.0F, 5, 0, 3, 0.0F, false));

		Bootfin_left1c = new ModelRenderer(this);
		Bootfin_left1c.setRotationPoint(0.0F, -2.0F, 3.01F);
		Boot_left1a.addChild(Bootfin_left1c);
		setRotationAngle(Bootfin_left1c, 0.1745F, 0.0F, 0.0F);
		Bootfin_left1c.cubeList.add(new ModelBox(Bootfin_left1c, 40, 85, -2.5F, 0.0F, 0.0F, 5, 3, 0, 0.0F, false));
	}

	@Override
	public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		GlStateManager.enableCull();
		super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
		GlStateManager.disableCull();
	}
	
	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}