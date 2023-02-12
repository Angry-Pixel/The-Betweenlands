package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.mobs.EntityBigPuffshroom;

@SideOnly(Side.CLIENT)
public class ModelBigPuffshroom extends ModelBase {
	private final ModelRenderer core1a;
	private final ModelRenderer core1b;
	private final ModelRenderer core1c;
	private final ModelRenderer core1c_r1;
	private final ModelRenderer core_myco1a;
	private final ModelRenderer core_myco1b;
	private final ModelRenderer core2a;
	private final ModelRenderer core2b;
	private final ModelRenderer core2c;
	private final ModelRenderer core2c_r1;
	private final ModelRenderer core_myco2a;
	private final ModelRenderer core_myco2b;
	private final ModelRenderer core3a;
	private final ModelRenderer core3b;
	private final ModelRenderer core3c;
	private final ModelRenderer core3c_r1;
	private final ModelRenderer core_myco3a;
	private final ModelRenderer core_myco3b;
	private final ModelRenderer core4a;
	private final ModelRenderer core4b;
	private final ModelRenderer core4c;
	private final ModelRenderer core4c_r1;
	private final ModelRenderer core_myco4a;
	private final ModelRenderer core_myco4b;
	private final ModelRenderer base;
	private final ModelRenderer roots;
	private final ModelRenderer base_tentacle1;
	private final ModelRenderer tentacle1a;
	private final ModelRenderer tentmyc1_1;
	private final ModelRenderer tentacle1b;
	private final ModelRenderer tentmyc1_2a;
	private final ModelRenderer tentacle1b2;
	private final ModelRenderer tentmyc1_2b;
	private final ModelRenderer tentacle1c;
	private final ModelRenderer tentacle1c2;
	private final ModelRenderer tentmyc1_3b;
	private final ModelRenderer tentacle1d;
	private final ModelRenderer tentmyc1_4a;
	private final ModelRenderer tentacle1d2;
	private final ModelRenderer tentmyc1_4b;
	private final ModelRenderer tentacle1e;
	private final ModelRenderer tentacle1e2;
	private final ModelRenderer tentlet1_3a;
	private final ModelRenderer tentlet1_2a;
	private final ModelRenderer tentmyc1_3a;
	private final ModelRenderer tentlet1_1a;
	private final ModelRenderer mycotissue1;
	private final ModelRenderer mycotissue1_r1;
	private final ModelRenderer base_tentacle2;
	private final ModelRenderer tentacle2a;
	private final ModelRenderer tentmyc2_1;
	private final ModelRenderer tentacle2b;
	private final ModelRenderer mycotissue2;
	private final ModelRenderer mycotissue2_r1;
	private final ModelRenderer tentmyc2_2a;
	private final ModelRenderer tentacle2b2;
	private final ModelRenderer tentmyc2_2b;
	private final ModelRenderer tentacle2c;
	private final ModelRenderer tentacle2c2;
	private final ModelRenderer tentmyc2_3b;
	private final ModelRenderer tentacle2d;
	private final ModelRenderer tentmyc2_4a;
	private final ModelRenderer tentacle2d2;
	private final ModelRenderer tentmyc2_4b;
	private final ModelRenderer tentacle2e;
	private final ModelRenderer tentacle2e2;
	private final ModelRenderer tentlet2_3a;
	private final ModelRenderer tentlet2_2a;
	private final ModelRenderer tentmyc2_3a;
	private final ModelRenderer tentlet2_1a;
	private final ModelRenderer base_tentacle3;
	private final ModelRenderer tentacle3a;
	private final ModelRenderer tentmyc3_1;
	private final ModelRenderer tentacle3b;
	private final ModelRenderer tentmyc3_2a;
	private final ModelRenderer tentacle3b2;
	private final ModelRenderer tentmyc3_2b;
	private final ModelRenderer tentacle3c;
	private final ModelRenderer tentacle3c2;
	private final ModelRenderer tentmyc3_3b;
	private final ModelRenderer tentacle3d;
	private final ModelRenderer tentmyc3_4a;
	private final ModelRenderer tentacle3d2;
	private final ModelRenderer tentmyc3_4b;
	private final ModelRenderer tentacle3e;
	private final ModelRenderer tentacle3e2;
	private final ModelRenderer tentlet3_3a;
	private final ModelRenderer tentlet3_2a;
	private final ModelRenderer tentmyc3_3a;
	private final ModelRenderer tentlet3_1a;
	private final ModelRenderer mycotissue3;
	private final ModelRenderer mycotissue3_r1;
	private final ModelRenderer base_tentacle4;
	private final ModelRenderer tentacle4a;
	private final ModelRenderer tentmyc4_1;
	private final ModelRenderer tentacle4b;
	private final ModelRenderer mycotissue4;
	private final ModelRenderer mycotissue4_r1;
	private final ModelRenderer tentmyc4_2a;
	private final ModelRenderer tentacle4b2;
	private final ModelRenderer tentmyc4_2b;
	private final ModelRenderer tentacle4c;
	private final ModelRenderer tentacle4c2;
	private final ModelRenderer tentmyc4_3b;
	private final ModelRenderer tentacle4d;
	private final ModelRenderer tentmyc4_4a;
	private final ModelRenderer tentacle4d2;
	private final ModelRenderer tentmyc4_4b;
	private final ModelRenderer tentacle4e;
	private final ModelRenderer tentacle4e2;
	private final ModelRenderer tentlet4_3a;
	private final ModelRenderer tentlet4_2a;
	private final ModelRenderer tentmyc4_3a;
	private final ModelRenderer tentlet4_1a;
	private final ModelRenderer base_tentacle5;
	private final ModelRenderer tentacle5a;
	private final ModelRenderer tentmyc5_1;
	private final ModelRenderer tentacle5b;
	private final ModelRenderer tentmyc5_2a;
	private final ModelRenderer tentacle5b2;
	private final ModelRenderer tentmyc5_2b;
	private final ModelRenderer tentacle5c;
	private final ModelRenderer tentacle5c2;
	private final ModelRenderer tentmyc5_3b;
	private final ModelRenderer tentacle5d;
	private final ModelRenderer tentmyc5_4a;
	private final ModelRenderer tentacle5d2;
	private final ModelRenderer tentmyc5_4b;
	private final ModelRenderer tentacle5e;
	private final ModelRenderer tentacle5e2;
	private final ModelRenderer tentlet5_3a;
	private final ModelRenderer tentlet5_2a;
	private final ModelRenderer tentmyc5_3a;
	private final ModelRenderer tentlet5_1a;
	private final ModelRenderer mycotissue5;
	private final ModelRenderer mycotissue5_r1;
	private final ModelRenderer base_tentacle6;
	private final ModelRenderer tentacle6a;
	private final ModelRenderer tentmyc6_1;
	private final ModelRenderer tentacle6b;
	private final ModelRenderer mycotissue6;
	private final ModelRenderer mycotissue6_r1;
	private final ModelRenderer tentmyc6_2a;
	private final ModelRenderer tentacle6b2;
	private final ModelRenderer tentmyc6_2b;
	private final ModelRenderer tentacle6c;
	private final ModelRenderer tentacle6c2;
	private final ModelRenderer tentmyc6_3b;
	private final ModelRenderer tentacle6d;
	private final ModelRenderer tentmyc6_4a;
	private final ModelRenderer tentacle6d2;
	private final ModelRenderer tentmyc6_4b;
	private final ModelRenderer tentacle6e;
	private final ModelRenderer tentacle6e2;
	private final ModelRenderer tentlet6_3a;
	private final ModelRenderer tentlet6_2a;
	private final ModelRenderer tentmyc6_3a;
	private final ModelRenderer tentlet6_1a;
	private final ModelRenderer base_tentacle7;
	private final ModelRenderer tentacle7a;
	private final ModelRenderer tentmyc7_1;
	private final ModelRenderer tentacle7b;
	private final ModelRenderer tentmyc7_2a;
	private final ModelRenderer tentacle7b2;
	private final ModelRenderer tentmyc7_2b;
	private final ModelRenderer tentacle7c;
	private final ModelRenderer tentacle7c2;
	private final ModelRenderer tentmyc7_3b;
	private final ModelRenderer tentacle7d;
	private final ModelRenderer tentmyc7_4a;
	private final ModelRenderer tentacle7d2;
	private final ModelRenderer tentmyc_74b;
	private final ModelRenderer tentacle7e;
	private final ModelRenderer tentacle7e2;
	private final ModelRenderer tentlet7_3a;
	private final ModelRenderer tentlet7_2a;
	private final ModelRenderer tentmyc7_3a;
	private final ModelRenderer tentlet7_1a;
	private final ModelRenderer mycotissue7;
	private final ModelRenderer mycotissue7_r1;
	private final ModelRenderer base_tentacle8;
	private final ModelRenderer tentacle8a;
	private final ModelRenderer tentmyc8_1;
	private final ModelRenderer tentacle8b;
	private final ModelRenderer mycotissue8;
	private final ModelRenderer mycotissue8_r1;
	private final ModelRenderer tentmyc8_2a;
	private final ModelRenderer tentacle8b2;
	private final ModelRenderer tentmyc8_2b;
	private final ModelRenderer tentacle8c;
	private final ModelRenderer tentacle8c2;
	private final ModelRenderer tentmyc8_3b;
	private final ModelRenderer tentacle8d;
	private final ModelRenderer tentmyc8_4a;
	private final ModelRenderer tentacle8d2;
	private final ModelRenderer tentmyc8_4b;
	private final ModelRenderer tentacle8e;
	private final ModelRenderer tentacle8e2;
	private final ModelRenderer tentlet8_3a;
	private final ModelRenderer tentlet8_2a;
	private final ModelRenderer tentmyc8_3a;
	private final ModelRenderer tentlet8_1a;
	private final ModelRenderer spitty_part;
	private final ModelRenderer lip1;
	private final ModelRenderer lip2;
	private final ModelRenderer lip3;

	public ModelBigPuffshroom() {
		textureWidth = 256;
		textureHeight = 256;

		core1a = new ModelRenderer(this);
		core1a.setRotationPoint(0.0F, -2.0F, -5.0F);
		setRotationAngle(core1a, -0.2182F, 0.0F, 0.0F);
		core1a.cubeList.add(new ModelBox(core1a, 108, 59, -5.0F, -1.0F, -1.0F, 10, 10, 3, 0.001F, false));

		core1b = new ModelRenderer(this);
		core1b.setRotationPoint(0.0F, 9.0F, -1.0F);
		core1a.addChild(core1b);
		setRotationAngle(core1b, 0.3491F, 0.0F, 0.0F);
		core1b.cubeList.add(new ModelBox(core1b, 0, 75, -5.0F, 0.0F, 0.0F, 10, 9, 3, 0.0F, false));

		core1c = new ModelRenderer(this);
		core1c.setRotationPoint(0.0F, 9.0F, 0.0F);
		core1b.addChild(core1c);
		

		core1c_r1 = new ModelRenderer(this);
		core1c_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
		core1c.addChild(core1c_r1);
		setRotationAngle(core1c_r1, -1.0036F, 0.0F, 0.0F);
		core1c_r1.cubeList.add(new ModelBox(core1c_r1, 25, 99, -5.0F, -2.0F, 0.0F, 10, 5, 2, -0.001F, false));

		core_myco1a = new ModelRenderer(this);
		core_myco1a.setRotationPoint(0.0F, -1.0F, 1.0F);
		core1a.addChild(core_myco1a);
		setRotationAngle(core_myco1a, 0.5672F, 0.0F, 0.0F);
		core_myco1a.cubeList.add(new ModelBox(core_myco1a, 77, 173, -4.0F, -2.0F, 0.0F, 8, 2, 0, 0.0F, false));

		core_myco1b = new ModelRenderer(this);
		core_myco1b.setRotationPoint(0.0F, -2.0F, 0.0F);
		core_myco1a.addChild(core_myco1b);
		setRotationAngle(core_myco1b, 0.5672F, 0.0F, 0.0F);
		core_myco1b.cubeList.add(new ModelBox(core_myco1b, 77, 170, -4.0F, -2.0F, 0.0F, 8, 2, 0, 0.0F, false));

		core2a = new ModelRenderer(this);
		core2a.setRotationPoint(0.0F, -2.0F, 5.0F);
		setRotationAngle(core2a, 0.2182F, 0.0F, 0.0F);
		core2a.cubeList.add(new ModelBox(core2a, 81, 59, -5.0F, -1.0F, -2.0F, 10, 10, 3, 0.001F, false));

		core2b = new ModelRenderer(this);
		core2b.setRotationPoint(0.0F, 9.0F, 1.0F);
		core2a.addChild(core2b);
		setRotationAngle(core2b, -0.3491F, 0.0F, 0.0F);
		core2b.cubeList.add(new ModelBox(core2b, 135, 59, -5.0F, 0.0F, -3.0F, 10, 9, 3, 0.0F, false));

		core2c = new ModelRenderer(this);
		core2c.setRotationPoint(0.0F, 9.0F, 0.0F);
		core2b.addChild(core2c);
		

		core2c_r1 = new ModelRenderer(this);
		core2c_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
		core2c.addChild(core2c_r1);
		setRotationAngle(core2c_r1, 1.0036F, 0.0F, 0.0F);
		core2c_r1.cubeList.add(new ModelBox(core2c_r1, 0, 99, -5.0F, -2.0F, -2.0F, 10, 5, 2, -0.001F, false));

		core_myco2a = new ModelRenderer(this);
		core_myco2a.setRotationPoint(0.0F, -1.0F, -1.0F);
		core2a.addChild(core_myco2a);
		setRotationAngle(core_myco2a, -0.5672F, 0.0F, 0.0F);
		core_myco2a.cubeList.add(new ModelBox(core_myco2a, 60, 173, -4.0F, -2.0F, 0.0F, 8, 2, 0, 0.0F, false));

		core_myco2b = new ModelRenderer(this);
		core_myco2b.setRotationPoint(0.0F, -2.0F, 0.0F);
		core_myco2a.addChild(core_myco2b);
		setRotationAngle(core_myco2b, -0.5672F, 0.0F, 0.0F);
		core_myco2b.cubeList.add(new ModelBox(core_myco2b, 60, 170, -4.0F, -2.0F, 0.0F, 8, 2, 0, 0.0F, false));

		core3a = new ModelRenderer(this);
		core3a.setRotationPoint(-5.0F, -2.0F, 0.0F);
		setRotationAngle(core3a, 0.0F, 0.0F, 0.2182F);
		core3a.cubeList.add(new ModelBox(core3a, 74, 0, -1.0F, -1.0F, -5.0F, 3, 10, 10, 0.001F, false));

		core3b = new ModelRenderer(this);
		core3b.setRotationPoint(-1.0F, 9.0F, 0.0F);
		core3a.addChild(core3b);
		setRotationAngle(core3b, 0.0F, 0.0F, -0.3491F);
		core3b.cubeList.add(new ModelBox(core3b, 27, 39, 0.0F, 0.0F, -5.0F, 3, 9, 10, 0.0F, false));

		core3c = new ModelRenderer(this);
		core3c.setRotationPoint(0.0F, 9.0F, 0.0F);
		core3b.addChild(core3c);
		

		core3c_r1 = new ModelRenderer(this);
		core3c_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
		core3c.addChild(core3c_r1);
		setRotationAngle(core3c_r1, 0.0F, 0.0F, 1.0036F);
		core3c_r1.cubeList.add(new ModelBox(core3c_r1, 56, 59, 0.0F, -2.0F, -5.0F, 2, 5, 10, -0.001F, false));

		core_myco3a = new ModelRenderer(this);
		core_myco3a.setRotationPoint(1.0F, -1.0F, 0.0F);
		core3a.addChild(core_myco3a);
		setRotationAngle(core_myco3a, 0.0F, 0.0F, -0.5672F);
		core_myco3a.cubeList.add(new ModelBox(core_myco3a, 108, 132, 0.0F, -2.0F, -4.0F, 0, 2, 8, 0.0F, false));

		core_myco3b = new ModelRenderer(this);
		core_myco3b.setRotationPoint(0.0F, -2.0F, 0.0F);
		core_myco3a.addChild(core_myco3b);
		setRotationAngle(core_myco3b, 0.0F, 0.0F, -0.5672F);
		core_myco3b.cubeList.add(new ModelBox(core_myco3b, 91, 132, 0.0F, -2.0F, -4.0F, 0, 2, 8, 0.0F, false));

		core4a = new ModelRenderer(this);
		core4a.setRotationPoint(5.0F, -2.0F, 0.0F);
		setRotationAngle(core4a, 0.0F, 0.0F, -0.2182F);
		core4a.cubeList.add(new ModelBox(core4a, 41, 16, -2.0F, -1.0F, -5.0F, 3, 10, 10, 0.001F, false));

		core4b = new ModelRenderer(this);
		core4b.setRotationPoint(1.0F, 9.0F, 0.0F);
		core4a.addChild(core4b);
		setRotationAngle(core4b, 0.0F, 0.0F, 0.3491F);
		core4b.cubeList.add(new ModelBox(core4b, 0, 39, -3.0F, 0.0F, -5.0F, 3, 9, 10, 0.0F, false));

		core4c = new ModelRenderer(this);
		core4c.setRotationPoint(0.0F, 9.0F, 0.0F);
		core4b.addChild(core4c);
		

		core4c_r1 = new ModelRenderer(this);
		core4c_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
		core4c.addChild(core4c_r1);
		setRotationAngle(core4c_r1, 0.0F, 0.0F, -1.0036F);
		core4c_r1.cubeList.add(new ModelBox(core4c_r1, 31, 59, -2.0F, -2.0F, -5.0F, 2, 5, 10, -0.001F, false));

		core_myco4a = new ModelRenderer(this);
		core_myco4a.setRotationPoint(-1.0F, -1.0F, 0.0F);
		core4a.addChild(core_myco4a);
		setRotationAngle(core_myco4a, 0.0F, 0.0F, 0.5672F);
		core_myco4a.cubeList.add(new ModelBox(core_myco4a, 74, 132, 0.0F, -2.0F, -4.0F, 0, 2, 8, 0.0F, false));

		core_myco4b = new ModelRenderer(this);
		core_myco4b.setRotationPoint(0.0F, -2.0F, 0.0F);
		core_myco4a.addChild(core_myco4b);
		setRotationAngle(core_myco4b, 0.0F, 0.0F, 0.5672F);
		core_myco4b.cubeList.add(new ModelBox(core_myco4b, 57, 132, 0.0F, -2.0F, -4.0F, 0, 2, 8, 0.0F, false));

		base = new ModelRenderer(this);
		base.setRotationPoint(0.0F, 24.0F, 0.0F);
		base.cubeList.add(new ModelBox(base, 0, 0, -5.0F, -26.0F, -5.0F, 10, 28, 10, 0.0F, false));

		roots = new ModelRenderer(this);
		roots.setRotationPoint(0.0F, 0.0F, 0.0F);
		base.addChild(roots);
		roots.cubeList.add(new ModelBox(roots, 41, 0, -4.0F, -1.0F, -4.0F, 8, 7, 8, 0.0F, false));

		base_tentacle1 = new ModelRenderer(this);
		base_tentacle1.setRotationPoint(0.0F, 0.0F, 0.0F);
		base.addChild(base_tentacle1);
		

		tentacle1a = new ModelRenderer(this);
		tentacle1a.setRotationPoint(-5.0F, 0.0F, 0.0F);
		base_tentacle1.addChild(tentacle1a);
		setRotationAngle(tentacle1a, 0.0F, 0.0F, 0.3927F);
		tentacle1a.cubeList.add(new ModelBox(tentacle1a, 144, 88, -5.0F, -4.0F, -3.0F, 5, 4, 6, 0.0F, false));

		tentmyc1_1 = new ModelRenderer(this);
		tentmyc1_1.setRotationPoint(0.0F, 0.0F, 0.0F);
		tentacle1a.addChild(tentmyc1_1);
		tentmyc1_1.cubeList.add(new ModelBox(tentmyc1_1, 85, 162, -5.0F, 0.0F, -1.5F, 5, 3, 3, 0.0F, false));

		tentacle1b = new ModelRenderer(this);
		tentacle1b.setRotationPoint(-5.0F, 0.0F, 0.0F);
		tentacle1a.addChild(tentacle1b);
		setRotationAngle(tentacle1b, 0.0F, 0.0F, 0.6109F);
		tentacle1b.cubeList.add(new ModelBox(tentacle1b, 38, 121, -3.0F, -4.0F, -3.001F, 3, 4, 6, 0.0F, false));

		tentmyc1_2a = new ModelRenderer(this);
		tentmyc1_2a.setRotationPoint(0.0F, 0.0F, 0.0F);
		tentacle1b.addChild(tentmyc1_2a);
		tentmyc1_2a.cubeList.add(new ModelBox(tentmyc1_2a, 143, 184, -3.0F, 0.0F, -1.0F, 4, 2, 2, 0.0F, false));

		tentacle1b2 = new ModelRenderer(this);
		tentacle1b2.setRotationPoint(-3.0F, 0.0F, 3.0F);
		tentacle1b.addChild(tentacle1b2);
		setRotationAngle(tentacle1b2, 0.0F, -0.0873F, 0.0F);
		tentacle1b2.cubeList.add(new ModelBox(tentacle1b2, 19, 121, -3.0F, -4.001F, -6.0F, 3, 4, 6, 0.0F, false));

		tentmyc1_2b = new ModelRenderer(this);
		tentmyc1_2b.setRotationPoint(0.0F, 0.0F, -3.0F);
		tentacle1b2.addChild(tentmyc1_2b);
		tentmyc1_2b.cubeList.add(new ModelBox(tentmyc1_2b, 72, 190, -3.0F, 0.0F, -1.0F, 3, 2, 2, 0.0F, false));

		tentacle1c = new ModelRenderer(this);
		tentacle1c.setRotationPoint(-3.0F, 0.0F, -3.0F);
		tentacle1b2.addChild(tentacle1c);
		setRotationAngle(tentacle1c, 0.0F, 0.0F, 0.5672F);
		tentacle1c.cubeList.add(new ModelBox(tentacle1c, 102, 143, -3.0F, -4.0F, -2.999F, 3, 4, 5, 0.0F, false));

		tentacle1c2 = new ModelRenderer(this);
		tentacle1c2.setRotationPoint(-3.0F, 0.0F, 2.0F);
		tentacle1c.addChild(tentacle1c2);
		setRotationAngle(tentacle1c2, 0.0F, -0.0873F, 0.0F);
		tentacle1c2.cubeList.add(new ModelBox(tentacle1c2, 85, 143, -3.0F, -4.001F, -5.0F, 3, 4, 5, 0.0F, false));

		tentmyc1_3b = new ModelRenderer(this);
		tentmyc1_3b.setRotationPoint(0.0F, 0.0F, -2.5F);
		tentacle1c2.addChild(tentmyc1_3b);
		tentmyc1_3b.cubeList.add(new ModelBox(tentmyc1_3b, 44, 201, -3.0F, 0.0F, -0.5F, 3, 2, 1, 0.0F, false));

		tentacle1d = new ModelRenderer(this);
		tentacle1d.setRotationPoint(-3.0F, 0.0F, -2.0F);
		tentacle1c2.addChild(tentacle1d);
		setRotationAngle(tentacle1d, 0.0F, 0.0F, 0.2182F);
		tentacle1d.cubeList.add(new ModelBox(tentacle1d, 45, 170, -3.0F, -3.0F, -2.001F, 3, 3, 4, 0.0F, false));

		tentmyc1_4a = new ModelRenderer(this);
		tentmyc1_4a.setRotationPoint(0.0F, 0.0F, -0.25F);
		tentacle1d.addChild(tentmyc1_4a);
		tentmyc1_4a.cubeList.add(new ModelBox(tentmyc1_4a, 25, 207, -3.0F, 0.0F, 0.25F, 4, 2, 0, 0.0F, false));

		tentacle1d2 = new ModelRenderer(this);
		tentacle1d2.setRotationPoint(-3.0F, 0.0F, 2.0F);
		tentacle1d.addChild(tentacle1d2);
		setRotationAngle(tentacle1d2, 0.0F, -0.1309F, 0.0F);
		tentacle1d2.cubeList.add(new ModelBox(tentacle1d2, 30, 170, -3.0F, -3.001F, -4.001F, 3, 3, 4, 0.0F, false));

		tentmyc1_4b = new ModelRenderer(this);
		tentmyc1_4b.setRotationPoint(0.0F, 0.0F, -2.0F);
		tentacle1d2.addChild(tentmyc1_4b);
		tentmyc1_4b.cubeList.add(new ModelBox(tentmyc1_4b, 62, 207, -3.0F, 0.0F, 0.0F, 3, 2, 0, 0.0F, false));

		tentacle1e = new ModelRenderer(this);
		tentacle1e.setRotationPoint(-3.0F, 0.0F, -2.0F);
		tentacle1d2.addChild(tentacle1e);
		setRotationAngle(tentacle1e, 0.0F, 0.0F, 0.1745F);
		tentacle1e.cubeList.add(new ModelBox(tentacle1e, 39, 184, -3.0F, -2.0F, -2.0F, 3, 2, 3, 0.0F, false));

		tentacle1e2 = new ModelRenderer(this);
		tentacle1e2.setRotationPoint(-3.0F, 0.0F, 1.0F);
		tentacle1e.addChild(tentacle1e2);
		setRotationAngle(tentacle1e2, 0.0F, -0.1309F, 0.0F);
		tentacle1e2.cubeList.add(new ModelBox(tentacle1e2, 26, 184, -3.0F, -2.001F, -3.0F, 3, 2, 3, 0.0F, false));

		tentlet1_3a = new ModelRenderer(this);
		tentlet1_3a.setRotationPoint(-3.0F, 0.0F, -1.0F);
		tentacle1d2.addChild(tentlet1_3a);
		setRotationAngle(tentlet1_3a, 0.0F, 0.3491F, 0.0F);
		tentlet1_3a.cubeList.add(new ModelBox(tentlet1_3a, 34, 207, -2.0F, -3.0F, 0.0F, 2, 3, 1, 0.0F, false));

		tentlet1_2a = new ModelRenderer(this);
		tentlet1_2a.setRotationPoint(-3.0F, 0.0F, -4.0F);
		tentacle1c2.addChild(tentlet1_2a);
		setRotationAngle(tentlet1_2a, 0.0F, -0.3054F, 0.0F);
		tentlet1_2a.cubeList.add(new ModelBox(tentlet1_2a, 53, 201, -2.0F, -4.0F, -1.0F, 2, 4, 1, 0.0F, false));

		tentmyc1_3a = new ModelRenderer(this);
		tentmyc1_3a.setRotationPoint(0.0F, 0.0F, -0.5F);
		tentacle1c.addChild(tentmyc1_3a);
		tentmyc1_3a.cubeList.add(new ModelBox(tentmyc1_3a, 22, 195, -3.0F, 0.0F, -0.5F, 4, 2, 1, 0.0F, false));

		tentlet1_1a = new ModelRenderer(this);
		tentlet1_1a.setRotationPoint(-3.0F, 0.0F, -1.0F);
		tentacle1b2.addChild(tentlet1_1a);
		setRotationAngle(tentlet1_1a, 0.0F, 0.2618F, 0.0F);
		tentlet1_1a.cubeList.add(new ModelBox(tentlet1_1a, 60, 201, -2.0F, -4.0F, 0.0F, 2, 4, 1, 0.0F, false));

		mycotissue1 = new ModelRenderer(this);
		mycotissue1.setRotationPoint(-2.0F, -4.0F, 0.0F);
		tentacle1b.addChild(mycotissue1);
		setRotationAngle(mycotissue1, 0.0F, 0.0F, -0.2618F);
		

		mycotissue1_r1 = new ModelRenderer(this);
		mycotissue1_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
		mycotissue1.addChild(mycotissue1_r1);
		setRotationAngle(mycotissue1_r1, 0.0F, 0.0F, -0.829F);
		mycotissue1_r1.cubeList.add(new ModelBox(mycotissue1_r1, 108, 75, 0.0F, 0.0F, -1.5F, 10, 6, 3, 0.0F, false));

		base_tentacle2 = new ModelRenderer(this);
		base_tentacle2.setRotationPoint(0.0F, 0.0F, 0.0F);
		base.addChild(base_tentacle2);
		setRotationAngle(base_tentacle2, 0.0F, -0.7854F, 0.0F);
		

		tentacle2a = new ModelRenderer(this);
		tentacle2a.setRotationPoint(-5.0F, 0.0F, 0.0F);
		base_tentacle2.addChild(tentacle2a);
		setRotationAngle(tentacle2a, 0.0F, 0.0F, 0.3927F);
		tentacle2a.cubeList.add(new ModelBox(tentacle2a, 50, 88, -6.0F, -4.0F, -3.0F, 6, 4, 6, 0.0F, false));

		tentmyc2_1 = new ModelRenderer(this);
		tentmyc2_1.setRotationPoint(0.0F, 0.0F, 0.0F);
		tentacle2a.addChild(tentmyc2_1);
		tentmyc2_1.cubeList.add(new ModelBox(tentmyc2_1, 19, 153, -6.0F, 0.0F, -1.5F, 6, 3, 3, 0.0F, false));

		tentacle2b = new ModelRenderer(this);
		tentacle2b.setRotationPoint(-6.0F, 0.0F, 0.0F);
		tentacle2a.addChild(tentacle2b);
		setRotationAngle(tentacle2b, 0.0F, 0.0F, 0.6981F);
		tentacle2b.cubeList.add(new ModelBox(tentacle2b, 42, 110, -4.0F, -4.0F, -3.001F, 4, 4, 6, 0.0F, false));

		mycotissue2 = new ModelRenderer(this);
		mycotissue2.setRotationPoint(-4.0F, -4.0F, 0.0F);
		tentacle2b.addChild(mycotissue2);
		

		mycotissue2_r1 = new ModelRenderer(this);
		mycotissue2_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
		mycotissue2.addChild(mycotissue2_r1);
		setRotationAngle(mycotissue2_r1, 0.0F, 0.0F, -0.829F);
		mycotissue2_r1.cubeList.add(new ModelBox(mycotissue2_r1, 0, 59, 0.0F, 0.0F, -1.5F, 12, 8, 3, 0.0F, false));

		tentmyc2_2a = new ModelRenderer(this);
		tentmyc2_2a.setRotationPoint(0.0F, 0.0F, 0.0F);
		tentacle2b.addChild(tentmyc2_2a);
		tentmyc2_2a.cubeList.add(new ModelBox(tentmyc2_2a, 0, 178, -4.0F, 0.0F, -1.0F, 5, 2, 2, 0.0F, false));

		tentacle2b2 = new ModelRenderer(this);
		tentacle2b2.setRotationPoint(-4.0F, 0.0F, 3.0F);
		tentacle2b.addChild(tentacle2b2);
		setRotationAngle(tentacle2b2, 0.0F, -0.0873F, 0.0F);
		tentacle2b2.cubeList.add(new ModelBox(tentacle2b2, 21, 110, -4.0F, -4.001F, -6.0F, 4, 4, 6, 0.0F, false));

		tentmyc2_2b = new ModelRenderer(this);
		tentmyc2_2b.setRotationPoint(0.0F, 0.0F, -3.0F);
		tentacle2b2.addChild(tentmyc2_2b);
		tentmyc2_2b.cubeList.add(new ModelBox(tentmyc2_2b, 130, 184, -4.0F, 0.0F, -1.0F, 4, 2, 2, 0.0F, false));

		tentacle2c = new ModelRenderer(this);
		tentacle2c.setRotationPoint(-4.0F, 0.0F, -3.0F);
		tentacle2b2.addChild(tentacle2c);
		setRotationAngle(tentacle2c, 0.0F, 0.0F, 0.6545F);
		tentacle2c.cubeList.add(new ModelBox(tentacle2c, 38, 132, -4.0F, -4.0F, -2.999F, 4, 4, 5, 0.0F, false));

		tentacle2c2 = new ModelRenderer(this);
		tentacle2c2.setRotationPoint(-4.0F, 0.0F, 2.0F);
		tentacle2c.addChild(tentacle2c2);
		setRotationAngle(tentacle2c2, 0.0F, -0.1309F, 0.0F);
		tentacle2c2.cubeList.add(new ModelBox(tentacle2c2, 19, 132, -4.0F, -4.001F, -5.0F, 4, 4, 5, 0.0F, false));

		tentmyc2_3b = new ModelRenderer(this);
		tentmyc2_3b.setRotationPoint(0.0F, 0.0F, -2.5F);
		tentacle2c2.addChild(tentmyc2_3b);
		tentmyc2_3b.cubeList.add(new ModelBox(tentmyc2_3b, 11, 195, -4.0F, 0.0F, -0.5F, 4, 2, 1, 0.0F, false));

		tentacle2d = new ModelRenderer(this);
		tentacle2d.setRotationPoint(-4.0F, 0.0F, -2.0F);
		tentacle2c2.addChild(tentacle2d);
		setRotationAngle(tentacle2d, 0.0F, 0.0F, 0.2182F);
		tentacle2d.cubeList.add(new ModelBox(tentacle2d, 17, 162, -4.0F, -3.0F, -2.001F, 4, 3, 4, 0.0F, false));

		tentmyc2_4a = new ModelRenderer(this);
		tentmyc2_4a.setRotationPoint(0.0F, 0.0F, -0.25F);
		tentacle2d.addChild(tentmyc2_4a);
		tentmyc2_4a.cubeList.add(new ModelBox(tentmyc2_4a, 44, 198, -4.0F, 0.0F, 0.25F, 5, 2, 0, 0.0F, false));

		tentacle2d2 = new ModelRenderer(this);
		tentacle2d2.setRotationPoint(-4.0F, 0.0F, 2.0F);
		tentacle2d.addChild(tentacle2d2);
		setRotationAngle(tentacle2d2, 0.0F, -0.1745F, 0.0F);
		tentacle2d2.cubeList.add(new ModelBox(tentacle2d2, 0, 162, -4.0F, -3.001F, -4.001F, 4, 3, 4, 0.0F, false));

		tentmyc2_4b = new ModelRenderer(this);
		tentmyc2_4b.setRotationPoint(0.0F, 0.0F, -2.0F);
		tentacle2d2.addChild(tentmyc2_4b);
		tentmyc2_4b.cubeList.add(new ModelBox(tentmyc2_4b, 16, 207, -4.0F, 0.0F, 0.0F, 4, 2, 0, 0.0F, false));

		tentacle2e = new ModelRenderer(this);
		tentacle2e.setRotationPoint(-4.0F, 0.0F, -2.0F);
		tentacle2d2.addChild(tentacle2e);
		setRotationAngle(tentacle2e, 0.0F, 0.0F, 0.1745F);
		tentacle2e.cubeList.add(new ModelBox(tentacle2e, 13, 184, -3.0F, -2.0F, -2.0F, 3, 2, 3, 0.0F, false));

		tentacle2e2 = new ModelRenderer(this);
		tentacle2e2.setRotationPoint(-3.0F, 0.0F, 1.0F);
		tentacle2e.addChild(tentacle2e2);
		setRotationAngle(tentacle2e2, 0.0F, -0.1745F, 0.0F);
		tentacle2e2.cubeList.add(new ModelBox(tentacle2e2, 0, 184, -3.0F, -2.001F, -3.0F, 3, 2, 3, 0.0F, false));

		tentlet2_3a = new ModelRenderer(this);
		tentlet2_3a.setRotationPoint(-4.0F, 0.0F, -1.0F);
		tentacle2d2.addChild(tentlet2_3a);
		setRotationAngle(tentlet2_3a, 0.0F, 0.3491F, 0.0F);
		tentlet2_3a.cubeList.add(new ModelBox(tentlet2_3a, 9, 207, -2.0F, -3.0F, 0.0F, 2, 3, 1, 0.0F, false));

		tentlet2_2a = new ModelRenderer(this);
		tentlet2_2a.setRotationPoint(-4.0F, 0.0F, -4.0F);
		tentacle2c2.addChild(tentlet2_2a);
		setRotationAngle(tentlet2_2a, 0.0F, -0.3054F, 0.0F);
		tentlet2_2a.cubeList.add(new ModelBox(tentlet2_2a, 37, 201, -2.0F, -4.0F, -1.0F, 2, 4, 1, 0.0F, false));

		tentmyc2_3a = new ModelRenderer(this);
		tentmyc2_3a.setRotationPoint(0.0F, 0.0F, -0.5F);
		tentacle2c.addChild(tentmyc2_3a);
		tentmyc2_3a.cubeList.add(new ModelBox(tentmyc2_3a, 26, 190, -4.0F, 0.0F, -0.5F, 5, 2, 1, 0.0F, false));

		tentlet2_1a = new ModelRenderer(this);
		tentlet2_1a.setRotationPoint(-4.0F, 0.0F, -1.0F);
		tentacle2b2.addChild(tentlet2_1a);
		setRotationAngle(tentlet2_1a, 0.0F, 0.2618F, 0.0F);
		tentlet2_1a.cubeList.add(new ModelBox(tentlet2_1a, 30, 201, -2.0F, -4.0F, 0.0F, 2, 4, 1, 0.0F, false));

		base_tentacle3 = new ModelRenderer(this);
		base_tentacle3.setRotationPoint(0.0F, 0.0F, 0.0F);
		base.addChild(base_tentacle3);
		setRotationAngle(base_tentacle3, 0.0F, -1.5708F, 0.0F);
		

		tentacle3a = new ModelRenderer(this);
		tentacle3a.setRotationPoint(-5.0F, 0.0F, 0.0F);
		base_tentacle3.addChild(tentacle3a);
		setRotationAngle(tentacle3a, 0.0F, 0.0F, 0.3927F);
		tentacle3a.cubeList.add(new ModelBox(tentacle3a, 121, 88, -5.0F, -4.0F, -3.0F, 5, 4, 6, 0.0F, false));

		tentmyc3_1 = new ModelRenderer(this);
		tentmyc3_1.setRotationPoint(0.0F, 0.0F, 0.0F);
		tentacle3a.addChild(tentmyc3_1);
		tentmyc3_1.cubeList.add(new ModelBox(tentmyc3_1, 68, 162, -5.0F, 0.0F, -1.5F, 5, 3, 3, 0.0F, false));

		tentacle3b = new ModelRenderer(this);
		tentacle3b.setRotationPoint(-5.0F, 0.0F, 0.0F);
		tentacle3a.addChild(tentacle3b);
		setRotationAngle(tentacle3b, 0.0F, 0.0F, 0.6109F);
		tentacle3b.cubeList.add(new ModelBox(tentacle3b, 0, 121, -3.0F, -4.0F, -3.001F, 3, 4, 6, 0.0F, false));

		tentmyc3_2a = new ModelRenderer(this);
		tentmyc3_2a.setRotationPoint(0.0F, 0.0F, 0.0F);
		tentacle3b.addChild(tentmyc3_2a);
		tentmyc3_2a.cubeList.add(new ModelBox(tentmyc3_2a, 117, 184, -3.0F, 0.0F, -1.0F, 4, 2, 2, 0.0F, false));

		tentacle3b2 = new ModelRenderer(this);
		tentacle3b2.setRotationPoint(-3.0F, 0.0F, 3.0F);
		tentacle3b.addChild(tentacle3b2);
		setRotationAngle(tentacle3b2, 0.0F, -0.0873F, 0.0F);
		tentacle3b2.cubeList.add(new ModelBox(tentacle3b2, 139, 110, -3.0F, -4.001F, -6.0F, 3, 4, 6, 0.0F, false));

		tentmyc3_2b = new ModelRenderer(this);
		tentmyc3_2b.setRotationPoint(0.0F, 0.0F, -3.0F);
		tentacle3b2.addChild(tentmyc3_2b);
		tentmyc3_2b.cubeList.add(new ModelBox(tentmyc3_2b, 61, 190, -3.0F, 0.0F, -1.0F, 3, 2, 2, 0.0F, false));

		tentacle3c = new ModelRenderer(this);
		tentacle3c.setRotationPoint(-3.0F, 0.0F, -3.0F);
		tentacle3b2.addChild(tentacle3c);
		setRotationAngle(tentacle3c, 0.0F, 0.0F, 0.5672F);
		tentacle3c.cubeList.add(new ModelBox(tentacle3c, 68, 143, -3.0F, -4.0F, -2.999F, 3, 4, 5, 0.0F, false));

		tentacle3c2 = new ModelRenderer(this);
		tentacle3c2.setRotationPoint(-3.0F, 0.0F, 2.0F);
		tentacle3c.addChild(tentacle3c2);
		setRotationAngle(tentacle3c2, 0.0F, -0.0873F, 0.0F);
		tentacle3c2.cubeList.add(new ModelBox(tentacle3c2, 51, 143, -3.0F, -4.001F, -5.0F, 3, 4, 5, 0.0F, false));

		tentmyc3_3b = new ModelRenderer(this);
		tentmyc3_3b.setRotationPoint(0.0F, 0.0F, -2.5F);
		tentacle3c2.addChild(tentmyc3_3b);
		tentmyc3_3b.cubeList.add(new ModelBox(tentmyc3_3b, 21, 201, -3.0F, 0.0F, -0.5F, 3, 2, 1, 0.0F, false));

		tentacle3d = new ModelRenderer(this);
		tentacle3d.setRotationPoint(-3.0F, 0.0F, -2.0F);
		tentacle3c2.addChild(tentacle3d);
		setRotationAngle(tentacle3d, 0.0F, 0.0F, 0.2182F);
		tentacle3d.cubeList.add(new ModelBox(tentacle3d, 15, 170, -3.0F, -3.0F, -2.001F, 3, 3, 4, 0.0F, false));

		tentmyc3_4a = new ModelRenderer(this);
		tentmyc3_4a.setRotationPoint(0.0F, 0.0F, -0.25F);
		tentacle3d.addChild(tentmyc3_4a);
		tentmyc3_4a.cubeList.add(new ModelBox(tentmyc3_4a, 0, 207, -3.0F, 0.0F, 0.25F, 4, 2, 0, 0.0F, false));

		tentacle3d2 = new ModelRenderer(this);
		tentacle3d2.setRotationPoint(-3.0F, 0.0F, 2.0F);
		tentacle3d.addChild(tentacle3d2);
		setRotationAngle(tentacle3d2, 0.0F, -0.1309F, 0.0F);
		tentacle3d2.cubeList.add(new ModelBox(tentacle3d2, 0, 170, -3.0F, -3.001F, -4.001F, 3, 3, 4, 0.0F, false));

		tentmyc3_4b = new ModelRenderer(this);
		tentmyc3_4b.setRotationPoint(0.0F, 0.0F, -2.0F);
		tentacle3d2.addChild(tentmyc3_4b);
		tentmyc3_4b.cubeList.add(new ModelBox(tentmyc3_4b, 55, 207, -3.0F, 0.0F, 0.0F, 3, 2, 0, 0.0F, false));

		tentacle3e = new ModelRenderer(this);
		tentacle3e.setRotationPoint(-3.0F, 0.0F, -2.0F);
		tentacle3d2.addChild(tentacle3e);
		setRotationAngle(tentacle3e, 0.0F, 0.0F, 0.1745F);
		tentacle3e.cubeList.add(new ModelBox(tentacle3e, 158, 178, -3.0F, -2.0F, -2.0F, 3, 2, 3, 0.0F, false));

		tentacle3e2 = new ModelRenderer(this);
		tentacle3e2.setRotationPoint(-3.0F, 0.0F, 1.0F);
		tentacle3e.addChild(tentacle3e2);
		setRotationAngle(tentacle3e2, 0.0F, -0.1309F, 0.0F);
		tentacle3e2.cubeList.add(new ModelBox(tentacle3e2, 145, 178, -3.0F, -2.001F, -3.0F, 3, 2, 3, 0.0F, false));

		tentlet3_3a = new ModelRenderer(this);
		tentlet3_3a.setRotationPoint(-3.0F, 0.0F, -1.0F);
		tentacle3d2.addChild(tentlet3_3a);
		setRotationAngle(tentlet3_3a, 0.0F, 0.3491F, 0.0F);
		tentlet3_3a.cubeList.add(new ModelBox(tentlet3_3a, 147, 201, -2.0F, -3.0F, 0.0F, 2, 3, 1, 0.0F, false));

		tentlet3_2a = new ModelRenderer(this);
		tentlet3_2a.setRotationPoint(-3.0F, 0.0F, -4.0F);
		tentacle3c2.addChild(tentlet3_2a);
		setRotationAngle(tentlet3_2a, 0.0F, -0.3054F, 0.0F);
		tentlet3_2a.cubeList.add(new ModelBox(tentlet3_2a, 14, 201, -2.0F, -4.0F, -1.0F, 2, 4, 1, 0.0F, false));

		tentmyc3_3a = new ModelRenderer(this);
		tentmyc3_3a.setRotationPoint(0.0F, 0.0F, -0.5F);
		tentacle3c.addChild(tentmyc3_3a);
		tentmyc3_3a.cubeList.add(new ModelBox(tentmyc3_3a, 0, 195, -3.0F, 0.0F, -0.5F, 4, 2, 1, 0.0F, false));

		tentlet3_1a = new ModelRenderer(this);
		tentlet3_1a.setRotationPoint(-3.0F, 0.0F, -1.0F);
		tentacle3b2.addChild(tentlet3_1a);
		setRotationAngle(tentlet3_1a, 0.0F, 0.2618F, 0.0F);
		tentlet3_1a.cubeList.add(new ModelBox(tentlet3_1a, 7, 201, -2.0F, -4.0F, 0.0F, 2, 4, 1, 0.0F, false));

		mycotissue3 = new ModelRenderer(this);
		mycotissue3.setRotationPoint(-2.0F, -4.0F, 0.0F);
		tentacle3b.addChild(mycotissue3);
		setRotationAngle(mycotissue3, 0.0F, 0.0F, -0.2618F);
		

		mycotissue3_r1 = new ModelRenderer(this);
		mycotissue3_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
		mycotissue3.addChild(mycotissue3_r1);
		setRotationAngle(mycotissue3_r1, 0.0F, 0.0F, -0.829F);
		mycotissue3_r1.cubeList.add(new ModelBox(mycotissue3_r1, 81, 75, 0.0F, 0.0F, -1.5F, 10, 6, 3, 0.0F, false));

		base_tentacle4 = new ModelRenderer(this);
		base_tentacle4.setRotationPoint(0.0F, 0.0F, 0.0F);
		base.addChild(base_tentacle4);
		setRotationAngle(base_tentacle4, 0.0F, -2.3562F, 0.0F);
		

		tentacle4a = new ModelRenderer(this);
		tentacle4a.setRotationPoint(-5.0F, 0.0F, 0.0F);
		base_tentacle4.addChild(tentacle4a);
		setRotationAngle(tentacle4a, 0.0F, 0.0F, 0.3927F);
		tentacle4a.cubeList.add(new ModelBox(tentacle4a, 25, 88, -6.0F, -4.0F, -3.0F, 6, 4, 6, 0.0F, false));

		tentmyc4_1 = new ModelRenderer(this);
		tentmyc4_1.setRotationPoint(0.0F, 0.0F, 0.0F);
		tentacle4a.addChild(tentmyc4_1);
		tentmyc4_1.cubeList.add(new ModelBox(tentmyc4_1, 0, 153, -6.0F, 0.0F, -1.5F, 6, 3, 3, 0.0F, false));

		tentacle4b = new ModelRenderer(this);
		tentacle4b.setRotationPoint(-6.0F, 0.0F, 0.0F);
		tentacle4a.addChild(tentacle4b);
		setRotationAngle(tentacle4b, 0.0F, 0.0F, 0.6981F);
		tentacle4b.cubeList.add(new ModelBox(tentacle4b, 0, 110, -4.0F, -4.0F, -3.001F, 4, 4, 6, 0.0F, false));

		mycotissue4 = new ModelRenderer(this);
		mycotissue4.setRotationPoint(-4.0F, -4.0F, 0.0F);
		tentacle4b.addChild(mycotissue4);
		

		mycotissue4_r1 = new ModelRenderer(this);
		mycotissue4_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
		mycotissue4.addChild(mycotissue4_r1);
		setRotationAngle(mycotissue4_r1, 0.0F, 0.0F, -0.829F);
		mycotissue4_r1.cubeList.add(new ModelBox(mycotissue4_r1, 116, 39, 0.0F, 0.0F, -1.5F, 12, 8, 3, 0.0F, false));

		tentmyc4_2a = new ModelRenderer(this);
		tentmyc4_2a.setRotationPoint(0.0F, 0.0F, 0.0F);
		tentacle4b.addChild(tentmyc4_2a);
		tentmyc4_2a.cubeList.add(new ModelBox(tentmyc4_2a, 124, 170, -4.0F, 0.0F, -1.0F, 5, 2, 2, 0.0F, false));

		tentacle4b2 = new ModelRenderer(this);
		tentacle4b2.setRotationPoint(-4.0F, 0.0F, 3.0F);
		tentacle4b.addChild(tentacle4b2);
		setRotationAngle(tentacle4b2, 0.0F, -0.0873F, 0.0F);
		tentacle4b2.cubeList.add(new ModelBox(tentacle4b2, 134, 99, -4.0F, -4.001F, -6.0F, 4, 4, 6, 0.0F, false));

		tentmyc4_2b = new ModelRenderer(this);
		tentmyc4_2b.setRotationPoint(0.0F, 0.0F, -3.0F);
		tentacle4b2.addChild(tentmyc4_2b);
		tentmyc4_2b.cubeList.add(new ModelBox(tentmyc4_2b, 104, 184, -4.0F, 0.0F, -1.0F, 4, 2, 2, 0.0F, false));

		tentacle4c = new ModelRenderer(this);
		tentacle4c.setRotationPoint(-4.0F, 0.0F, -3.0F);
		tentacle4b2.addChild(tentacle4c);
		setRotationAngle(tentacle4c, 0.0F, 0.0F, 0.6545F);
		tentacle4c.cubeList.add(new ModelBox(tentacle4c, 0, 132, -4.0F, -4.0F, -2.999F, 4, 4, 5, 0.0F, false));

		tentacle4c2 = new ModelRenderer(this);
		tentacle4c2.setRotationPoint(-4.0F, 0.0F, 2.0F);
		tentacle4c.addChild(tentacle4c2);
		setRotationAngle(tentacle4c2, 0.0F, -0.1309F, 0.0F);
		tentacle4c2.cubeList.add(new ModelBox(tentacle4c2, 133, 121, -4.0F, -4.001F, -5.0F, 4, 4, 5, 0.0F, false));

		tentmyc4_3b = new ModelRenderer(this);
		tentmyc4_3b.setRotationPoint(0.0F, 0.0F, -2.5F);
		tentacle4c2.addChild(tentmyc4_3b);
		tentmyc4_3b.cubeList.add(new ModelBox(tentmyc4_3b, 160, 190, -4.0F, 0.0F, -0.5F, 4, 2, 1, 0.0F, false));

		tentacle4d = new ModelRenderer(this);
		tentacle4d.setRotationPoint(-4.0F, 0.0F, -2.0F);
		tentacle4c2.addChild(tentacle4d);
		setRotationAngle(tentacle4d, 0.0F, 0.0F, 0.2182F);
		tentacle4d.cubeList.add(new ModelBox(tentacle4d, 140, 153, -4.0F, -3.0F, -2.001F, 4, 3, 4, 0.0F, false));

		tentmyc4_4a = new ModelRenderer(this);
		tentmyc4_4a.setRotationPoint(0.0F, 0.0F, -0.25F);
		tentacle4d.addChild(tentmyc4_4a);
		tentmyc4_4a.cubeList.add(new ModelBox(tentmyc4_4a, 44, 195, -4.0F, 0.0F, 0.25F, 5, 2, 0, 0.0F, false));

		tentacle4d2 = new ModelRenderer(this);
		tentacle4d2.setRotationPoint(-4.0F, 0.0F, 2.0F);
		tentacle4d.addChild(tentacle4d2);
		setRotationAngle(tentacle4d2, 0.0F, -0.1745F, 0.0F);
		tentacle4d2.cubeList.add(new ModelBox(tentacle4d2, 123, 153, -4.0F, -3.001F, -4.001F, 4, 3, 4, 0.0F, false));

		tentmyc4_4b = new ModelRenderer(this);
		tentmyc4_4b.setRotationPoint(0.0F, 0.0F, -2.0F);
		tentacle4d2.addChild(tentmyc4_4b);
		tentmyc4_4b.cubeList.add(new ModelBox(tentmyc4_4b, 138, 201, -4.0F, 0.0F, 0.0F, 4, 2, 0, 0.0F, false));

		tentacle4e = new ModelRenderer(this);
		tentacle4e.setRotationPoint(-4.0F, 0.0F, -2.0F);
		tentacle4d2.addChild(tentacle4e);
		setRotationAngle(tentacle4e, 0.0F, 0.0F, 0.1745F);
		tentacle4e.cubeList.add(new ModelBox(tentacle4e, 132, 178, -3.0F, -2.0F, -2.0F, 3, 2, 3, 0.0F, false));

		tentacle4e2 = new ModelRenderer(this);
		tentacle4e2.setRotationPoint(-3.0F, 0.0F, 1.0F);
		tentacle4e.addChild(tentacle4e2);
		setRotationAngle(tentacle4e2, 0.0F, -0.1745F, 0.0F);
		tentacle4e2.cubeList.add(new ModelBox(tentacle4e2, 119, 178, -3.0F, -2.001F, -3.0F, 3, 2, 3, 0.0F, false));

		tentlet4_3a = new ModelRenderer(this);
		tentlet4_3a.setRotationPoint(-4.0F, 0.0F, -1.0F);
		tentacle4d2.addChild(tentlet4_3a);
		setRotationAngle(tentlet4_3a, 0.0F, 0.3491F, 0.0F);
		tentlet4_3a.cubeList.add(new ModelBox(tentlet4_3a, 131, 201, -2.0F, -3.0F, 0.0F, 2, 3, 1, 0.0F, false));

		tentlet4_2a = new ModelRenderer(this);
		tentlet4_2a.setRotationPoint(-4.0F, 0.0F, -4.0F);
		tentacle4c2.addChild(tentlet4_2a);
		setRotationAngle(tentlet4_2a, 0.0F, -0.3054F, 0.0F);
		tentlet4_2a.cubeList.add(new ModelBox(tentlet4_2a, 0, 201, -2.0F, -4.0F, -1.0F, 2, 4, 1, 0.0F, false));

		tentmyc4_3a = new ModelRenderer(this);
		tentmyc4_3a.setRotationPoint(0.0F, 0.0F, -0.5F);
		tentacle4c.addChild(tentmyc4_3a);
		tentmyc4_3a.cubeList.add(new ModelBox(tentmyc4_3a, 13, 190, -4.0F, 0.0F, -0.5F, 5, 2, 1, 0.0F, false));

		tentlet4_1a = new ModelRenderer(this);
		tentlet4_1a.setRotationPoint(-4.0F, 0.0F, -1.0F);
		tentacle4b2.addChild(tentlet4_1a);
		setRotationAngle(tentlet4_1a, 0.0F, 0.2618F, 0.0F);
		tentlet4_1a.cubeList.add(new ModelBox(tentlet4_1a, 129, 195, -2.0F, -4.0F, 0.0F, 2, 4, 1, 0.0F, false));

		base_tentacle5 = new ModelRenderer(this);
		base_tentacle5.setRotationPoint(0.0F, 0.0F, 0.0F);
		base.addChild(base_tentacle5);
		setRotationAngle(base_tentacle5, 0.0F, 3.1416F, 0.0F);
		

		tentacle5a = new ModelRenderer(this);
		tentacle5a.setRotationPoint(-5.0F, 0.0F, 0.0F);
		base_tentacle5.addChild(tentacle5a);
		setRotationAngle(tentacle5a, 0.0F, 0.0F, 0.3927F);
		tentacle5a.cubeList.add(new ModelBox(tentacle5a, 98, 88, -5.0F, -4.0F, -3.0F, 5, 4, 6, 0.0F, false));

		tentmyc5_1 = new ModelRenderer(this);
		tentmyc5_1.setRotationPoint(0.0F, 0.0F, 0.0F);
		tentacle5a.addChild(tentmyc5_1);
		tentmyc5_1.cubeList.add(new ModelBox(tentmyc5_1, 51, 162, -5.0F, 0.0F, -1.5F, 5, 3, 3, 0.0F, false));

		tentacle5b = new ModelRenderer(this);
		tentacle5b.setRotationPoint(-5.0F, 0.0F, 0.0F);
		tentacle5a.addChild(tentacle5b);
		setRotationAngle(tentacle5b, 0.0F, 0.0F, 0.6109F);
		tentacle5b.cubeList.add(new ModelBox(tentacle5b, 120, 110, -3.0F, -4.0F, -3.001F, 3, 4, 6, 0.0F, false));

		tentmyc5_2a = new ModelRenderer(this);
		tentmyc5_2a.setRotationPoint(0.0F, 0.0F, 0.0F);
		tentacle5b.addChild(tentmyc5_2a);
		tentmyc5_2a.cubeList.add(new ModelBox(tentmyc5_2a, 91, 184, -3.0F, 0.0F, -1.0F, 4, 2, 2, 0.0F, false));

		tentacle5b2 = new ModelRenderer(this);
		tentacle5b2.setRotationPoint(-3.0F, 0.0F, 3.0F);
		tentacle5b.addChild(tentacle5b2);
		setRotationAngle(tentacle5b2, 0.0F, -0.0873F, 0.0F);
		tentacle5b2.cubeList.add(new ModelBox(tentacle5b2, 101, 110, -3.0F, -4.001F, -6.0F, 3, 4, 6, 0.0F, false));

		tentmyc5_2b = new ModelRenderer(this);
		tentmyc5_2b.setRotationPoint(0.0F, 0.0F, -3.0F);
		tentacle5b2.addChild(tentmyc5_2b);
		tentmyc5_2b.cubeList.add(new ModelBox(tentmyc5_2b, 50, 190, -3.0F, 0.0F, -1.0F, 3, 2, 2, 0.0F, false));

		tentacle5c = new ModelRenderer(this);
		tentacle5c.setRotationPoint(-3.0F, 0.0F, -3.0F);
		tentacle5b2.addChild(tentacle5c);
		setRotationAngle(tentacle5c, 0.0F, 0.0F, 0.5672F);
		tentacle5c.cubeList.add(new ModelBox(tentacle5c, 34, 143, -3.0F, -4.0F, -2.999F, 3, 4, 5, 0.0F, false));

		tentacle5c2 = new ModelRenderer(this);
		tentacle5c2.setRotationPoint(-3.0F, 0.0F, 2.0F);
		tentacle5c.addChild(tentacle5c2);
		setRotationAngle(tentacle5c2, 0.0F, -0.0873F, 0.0F);
		tentacle5c2.cubeList.add(new ModelBox(tentacle5c2, 17, 143, -3.0F, -4.001F, -5.0F, 3, 4, 5, 0.0F, false));

		tentmyc5_3b = new ModelRenderer(this);
		tentmyc5_3b.setRotationPoint(0.0F, 0.0F, -2.5F);
		tentacle5c2.addChild(tentmyc5_3b);
		tentmyc5_3b.cubeList.add(new ModelBox(tentmyc5_3b, 120, 195, -3.0F, 0.0F, -0.5F, 3, 2, 1, 0.0F, false));

		tentacle5d = new ModelRenderer(this);
		tentacle5d.setRotationPoint(-3.0F, 0.0F, -2.0F);
		tentacle5c2.addChild(tentacle5d);
		setRotationAngle(tentacle5d, 0.0F, 0.0F, 0.2182F);
		tentacle5d.cubeList.add(new ModelBox(tentacle5d, 147, 162, -3.0F, -3.0F, -2.001F, 3, 3, 4, 0.0F, false));

		tentmyc5_4a = new ModelRenderer(this);
		tentmyc5_4a.setRotationPoint(0.0F, 0.0F, -0.25F);
		tentacle5d.addChild(tentmyc5_4a);
		tentmyc5_4a.cubeList.add(new ModelBox(tentmyc5_4a, 122, 201, -3.0F, 0.0F, 0.25F, 4, 2, 0, 0.0F, false));

		tentacle5d2 = new ModelRenderer(this);
		tentacle5d2.setRotationPoint(-3.0F, 0.0F, 2.0F);
		tentacle5d.addChild(tentacle5d2);
		setRotationAngle(tentacle5d2, 0.0F, -0.1309F, 0.0F);
		tentacle5d2.cubeList.add(new ModelBox(tentacle5d2, 132, 162, -3.0F, -3.001F, -4.001F, 3, 3, 4, 0.0F, false));

		tentmyc5_4b = new ModelRenderer(this);
		tentmyc5_4b.setRotationPoint(0.0F, 0.0F, -2.0F);
		tentacle5d2.addChild(tentmyc5_4b);
		tentmyc5_4b.cubeList.add(new ModelBox(tentmyc5_4b, 48, 207, -3.0F, 0.0F, 0.0F, 3, 2, 0, 0.0F, false));

		tentacle5e = new ModelRenderer(this);
		tentacle5e.setRotationPoint(-3.0F, 0.0F, -2.0F);
		tentacle5d2.addChild(tentacle5e);
		setRotationAngle(tentacle5e, 0.0F, 0.0F, 0.1745F);
		tentacle5e.cubeList.add(new ModelBox(tentacle5e, 106, 178, -3.0F, -2.0F, -2.0F, 3, 2, 3, 0.0F, false));

		tentacle5e2 = new ModelRenderer(this);
		tentacle5e2.setRotationPoint(-3.0F, 0.0F, 1.0F);
		tentacle5e.addChild(tentacle5e2);
		setRotationAngle(tentacle5e2, 0.0F, -0.1309F, 0.0F);
		tentacle5e2.cubeList.add(new ModelBox(tentacle5e2, 93, 178, -3.0F, -2.001F, -3.0F, 3, 2, 3, 0.0F, false));

		tentlet5_3a = new ModelRenderer(this);
		tentlet5_3a.setRotationPoint(-3.0F, 0.0F, -1.0F);
		tentacle5d2.addChild(tentlet5_3a);
		setRotationAngle(tentlet5_3a, 0.0F, 0.3491F, 0.0F);
		tentlet5_3a.cubeList.add(new ModelBox(tentlet5_3a, 115, 201, -2.0F, -3.0F, 0.0F, 2, 3, 1, 0.0F, false));

		tentlet5_2a = new ModelRenderer(this);
		tentlet5_2a.setRotationPoint(-3.0F, 0.0F, -4.0F);
		tentacle5c2.addChild(tentlet5_2a);
		setRotationAngle(tentlet5_2a, 0.0F, -0.3054F, 0.0F);
		tentlet5_2a.cubeList.add(new ModelBox(tentlet5_2a, 113, 195, -2.0F, -4.0F, -1.0F, 2, 4, 1, 0.0F, false));

		tentmyc5_3a = new ModelRenderer(this);
		tentmyc5_3a.setRotationPoint(0.0F, 0.0F, -0.5F);
		tentacle5c.addChild(tentmyc5_3a);
		tentmyc5_3a.cubeList.add(new ModelBox(tentmyc5_3a, 149, 190, -3.0F, 0.0F, -0.5F, 4, 2, 1, 0.0F, false));

		tentlet5_1a = new ModelRenderer(this);
		tentlet5_1a.setRotationPoint(-3.0F, 0.0F, -1.0F);
		tentacle5b2.addChild(tentlet5_1a);
		setRotationAngle(tentlet5_1a, 0.0F, 0.2618F, 0.0F);
		tentlet5_1a.cubeList.add(new ModelBox(tentlet5_1a, 106, 195, -2.0F, -4.0F, 0.0F, 2, 4, 1, 0.0F, false));

		mycotissue5 = new ModelRenderer(this);
		mycotissue5.setRotationPoint(-2.0F, -4.0F, 0.0F);
		tentacle5b.addChild(mycotissue5);
		setRotationAngle(mycotissue5, 0.0F, 0.0F, -0.2618F);
		

		mycotissue5_r1 = new ModelRenderer(this);
		mycotissue5_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
		mycotissue5.addChild(mycotissue5_r1);
		setRotationAngle(mycotissue5_r1, 0.0F, 0.0F, -0.829F);
		mycotissue5_r1.cubeList.add(new ModelBox(mycotissue5_r1, 54, 75, 0.0F, 0.0F, -1.5F, 10, 6, 3, 0.0F, false));

		base_tentacle6 = new ModelRenderer(this);
		base_tentacle6.setRotationPoint(0.0F, 0.0F, 0.0F);
		base.addChild(base_tentacle6);
		setRotationAngle(base_tentacle6, 0.0F, 2.3562F, 0.0F);
		

		tentacle6a = new ModelRenderer(this);
		tentacle6a.setRotationPoint(-5.0F, 0.0F, 0.0F);
		base_tentacle6.addChild(tentacle6a);
		setRotationAngle(tentacle6a, 0.0F, 0.0F, 0.3927F);
		tentacle6a.cubeList.add(new ModelBox(tentacle6a, 0, 88, -6.0F, -4.0F, -3.0F, 6, 4, 6, 0.0F, false));

		tentmyc6_1 = new ModelRenderer(this);
		tentmyc6_1.setRotationPoint(0.0F, 0.0F, 0.0F);
		tentacle6a.addChild(tentmyc6_1);
		tentmyc6_1.cubeList.add(new ModelBox(tentmyc6_1, 138, 143, -6.0F, 0.0F, -1.5F, 6, 3, 3, 0.0F, false));

		tentacle6b = new ModelRenderer(this);
		tentacle6b.setRotationPoint(-6.0F, 0.0F, 0.0F);
		tentacle6a.addChild(tentacle6b);
		setRotationAngle(tentacle6b, 0.0F, 0.0F, 0.6981F);
		tentacle6b.cubeList.add(new ModelBox(tentacle6b, 113, 99, -4.0F, -4.0F, -3.001F, 4, 4, 6, 0.0F, false));

		mycotissue6 = new ModelRenderer(this);
		mycotissue6.setRotationPoint(-4.0F, -4.0F, 0.0F);
		tentacle6b.addChild(mycotissue6);
		

		mycotissue6_r1 = new ModelRenderer(this);
		mycotissue6_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
		mycotissue6.addChild(mycotissue6_r1);
		setRotationAngle(mycotissue6_r1, 0.0F, 0.0F, -0.829F);
		mycotissue6_r1.cubeList.add(new ModelBox(mycotissue6_r1, 85, 39, 0.0F, 0.0F, -1.5F, 12, 8, 3, 0.0F, false));

		tentmyc6_2a = new ModelRenderer(this);
		tentmyc6_2a.setRotationPoint(0.0F, 0.0F, 0.0F);
		tentacle6b.addChild(tentmyc6_2a);
		tentmyc6_2a.cubeList.add(new ModelBox(tentmyc6_2a, 109, 170, -4.0F, 0.0F, -1.0F, 5, 2, 2, 0.0F, false));

		tentacle6b2 = new ModelRenderer(this);
		tentacle6b2.setRotationPoint(-4.0F, 0.0F, 3.0F);
		tentacle6b.addChild(tentacle6b2);
		setRotationAngle(tentacle6b2, 0.0F, -0.0873F, 0.0F);
		tentacle6b2.cubeList.add(new ModelBox(tentacle6b2, 92, 99, -4.0F, -4.001F, -6.0F, 4, 4, 6, 0.0F, false));

		tentmyc6_2b = new ModelRenderer(this);
		tentmyc6_2b.setRotationPoint(0.0F, 0.0F, -3.0F);
		tentacle6b2.addChild(tentmyc6_2b);
		tentmyc6_2b.cubeList.add(new ModelBox(tentmyc6_2b, 78, 184, -4.0F, 0.0F, -1.0F, 4, 2, 2, 0.0F, false));

		tentacle6c = new ModelRenderer(this);
		tentacle6c.setRotationPoint(-4.0F, 0.0F, -3.0F);
		tentacle6b2.addChild(tentacle6c);
		setRotationAngle(tentacle6c, 0.0F, 0.0F, 0.6545F);
		tentacle6c.cubeList.add(new ModelBox(tentacle6c, 114, 121, -4.0F, -4.0F, -2.999F, 4, 4, 5, 0.0F, false));

		tentacle6c2 = new ModelRenderer(this);
		tentacle6c2.setRotationPoint(-4.0F, 0.0F, 2.0F);
		tentacle6c.addChild(tentacle6c2);
		setRotationAngle(tentacle6c2, 0.0F, -0.1309F, 0.0F);
		tentacle6c2.cubeList.add(new ModelBox(tentacle6c2, 95, 121, -4.0F, -4.001F, -5.0F, 4, 4, 5, 0.0F, false));

		tentmyc6_3b = new ModelRenderer(this);
		tentmyc6_3b.setRotationPoint(0.0F, 0.0F, -2.5F);
		tentacle6c2.addChild(tentmyc6_3b);
		tentmyc6_3b.cubeList.add(new ModelBox(tentmyc6_3b, 138, 190, -4.0F, 0.0F, -0.5F, 4, 2, 1, 0.0F, false));

		tentacle6d = new ModelRenderer(this);
		tentacle6d.setRotationPoint(-4.0F, 0.0F, -2.0F);
		tentacle6c2.addChild(tentacle6d);
		setRotationAngle(tentacle6d, 0.0F, 0.0F, 0.2182F);
		tentacle6d.cubeList.add(new ModelBox(tentacle6d, 106, 153, -4.0F, -3.0F, -2.001F, 4, 3, 4, 0.0F, false));

		tentmyc6_4a = new ModelRenderer(this);
		tentmyc6_4a.setRotationPoint(0.0F, 0.0F, -0.25F);
		tentacle6d.addChild(tentmyc6_4a);
		tentmyc6_4a.cubeList.add(new ModelBox(tentmyc6_4a, 33, 198, -4.0F, 0.0F, 0.25F, 5, 2, 0, 0.0F, false));

		tentacle6d2 = new ModelRenderer(this);
		tentacle6d2.setRotationPoint(-4.0F, 0.0F, 2.0F);
		tentacle6d.addChild(tentacle6d2);
		setRotationAngle(tentacle6d2, 0.0F, -0.1745F, 0.0F);
		tentacle6d2.cubeList.add(new ModelBox(tentacle6d2, 89, 153, -4.0F, -3.001F, -4.001F, 4, 3, 4, 0.0F, false));

		tentmyc6_4b = new ModelRenderer(this);
		tentmyc6_4b.setRotationPoint(0.0F, 0.0F, -2.0F);
		tentacle6d2.addChild(tentmyc6_4b);
		tentmyc6_4b.cubeList.add(new ModelBox(tentmyc6_4b, 106, 201, -4.0F, 0.0F, 0.0F, 4, 2, 0, 0.0F, false));

		tentacle6e = new ModelRenderer(this);
		tentacle6e.setRotationPoint(-4.0F, 0.0F, -2.0F);
		tentacle6d2.addChild(tentacle6e);
		setRotationAngle(tentacle6e, 0.0F, 0.0F, 0.1745F);
		tentacle6e.cubeList.add(new ModelBox(tentacle6e, 80, 178, -3.0F, -2.0F, -2.0F, 3, 2, 3, 0.0F, false));

		tentacle6e2 = new ModelRenderer(this);
		tentacle6e2.setRotationPoint(-3.0F, 0.0F, 1.0F);
		tentacle6e.addChild(tentacle6e2);
		setRotationAngle(tentacle6e2, 0.0F, -0.1745F, 0.0F);
		tentacle6e2.cubeList.add(new ModelBox(tentacle6e2, 67, 178, -3.0F, -2.001F, -3.0F, 3, 2, 3, 0.0F, false));

		tentlet6_3a = new ModelRenderer(this);
		tentlet6_3a.setRotationPoint(-4.0F, 0.0F, -1.0F);
		tentacle6d2.addChild(tentlet6_3a);
		setRotationAngle(tentlet6_3a, 0.0F, 0.3491F, 0.0F);
		tentlet6_3a.cubeList.add(new ModelBox(tentlet6_3a, 99, 201, -2.0F, -3.0F, 0.0F, 2, 3, 1, 0.0F, false));

		tentlet6_2a = new ModelRenderer(this);
		tentlet6_2a.setRotationPoint(-4.0F, 0.0F, -4.0F);
		tentacle6c2.addChild(tentlet6_2a);
		setRotationAngle(tentlet6_2a, 0.0F, -0.3054F, 0.0F);
		tentlet6_2a.cubeList.add(new ModelBox(tentlet6_2a, 99, 195, -2.0F, -4.0F, -1.0F, 2, 4, 1, 0.0F, false));

		tentmyc6_3a = new ModelRenderer(this);
		tentmyc6_3a.setRotationPoint(0.0F, 0.0F, -0.5F);
		tentacle6c.addChild(tentmyc6_3a);
		tentmyc6_3a.cubeList.add(new ModelBox(tentmyc6_3a, 0, 190, -4.0F, 0.0F, -0.5F, 5, 2, 1, 0.0F, false));

		tentlet6_1a = new ModelRenderer(this);
		tentlet6_1a.setRotationPoint(-4.0F, 0.0F, -1.0F);
		tentacle6b2.addChild(tentlet6_1a);
		setRotationAngle(tentlet6_1a, 0.0F, 0.2618F, 0.0F);
		tentlet6_1a.cubeList.add(new ModelBox(tentlet6_1a, 92, 195, -2.0F, -4.0F, 0.0F, 2, 4, 1, 0.0F, false));

		base_tentacle7 = new ModelRenderer(this);
		base_tentacle7.setRotationPoint(0.0F, 0.0F, 0.0F);
		base.addChild(base_tentacle7);
		setRotationAngle(base_tentacle7, 0.0F, 1.5708F, 0.0F);
		

		tentacle7a = new ModelRenderer(this);
		tentacle7a.setRotationPoint(-5.0F, 0.0F, 0.0F);
		base_tentacle7.addChild(tentacle7a);
		setRotationAngle(tentacle7a, 0.0F, 0.0F, 0.3927F);
		tentacle7a.cubeList.add(new ModelBox(tentacle7a, 75, 88, -5.0F, -4.0F, -3.0F, 5, 4, 6, 0.0F, false));

		tentmyc7_1 = new ModelRenderer(this);
		tentmyc7_1.setRotationPoint(0.0F, 0.0F, 0.0F);
		tentacle7a.addChild(tentmyc7_1);
		tentmyc7_1.cubeList.add(new ModelBox(tentmyc7_1, 34, 162, -5.0F, 0.0F, -1.5F, 5, 3, 3, 0.0F, false));

		tentacle7b = new ModelRenderer(this);
		tentacle7b.setRotationPoint(-5.0F, 0.0F, 0.0F);
		tentacle7a.addChild(tentacle7b);
		setRotationAngle(tentacle7b, 0.0F, 0.0F, 0.6109F);
		tentacle7b.cubeList.add(new ModelBox(tentacle7b, 82, 110, -3.0F, -4.0F, -3.001F, 3, 4, 6, 0.0F, false));

		tentmyc7_2a = new ModelRenderer(this);
		tentmyc7_2a.setRotationPoint(0.0F, 0.0F, 0.0F);
		tentacle7b.addChild(tentmyc7_2a);
		tentmyc7_2a.cubeList.add(new ModelBox(tentmyc7_2a, 65, 184, -3.0F, 0.0F, -1.0F, 4, 2, 2, 0.0F, false));

		tentacle7b2 = new ModelRenderer(this);
		tentacle7b2.setRotationPoint(-3.0F, 0.0F, 3.0F);
		tentacle7b.addChild(tentacle7b2);
		setRotationAngle(tentacle7b2, 0.0F, -0.0873F, 0.0F);
		tentacle7b2.cubeList.add(new ModelBox(tentacle7b2, 63, 110, -3.0F, -4.001F, -6.0F, 3, 4, 6, 0.0F, false));

		tentmyc7_2b = new ModelRenderer(this);
		tentmyc7_2b.setRotationPoint(0.0F, 0.0F, -3.0F);
		tentacle7b2.addChild(tentmyc7_2b);
		tentmyc7_2b.cubeList.add(new ModelBox(tentmyc7_2b, 39, 190, -3.0F, 0.0F, -1.0F, 3, 2, 2, 0.0F, false));

		tentacle7c = new ModelRenderer(this);
		tentacle7c.setRotationPoint(-3.0F, 0.0F, -3.0F);
		tentacle7b2.addChild(tentacle7c);
		setRotationAngle(tentacle7c, 0.0F, 0.0F, 0.5672F);
		tentacle7c.cubeList.add(new ModelBox(tentacle7c, 0, 143, -3.0F, -4.0F, -2.999F, 3, 4, 5, 0.0F, false));

		tentacle7c2 = new ModelRenderer(this);
		tentacle7c2.setRotationPoint(-3.0F, 0.0F, 2.0F);
		tentacle7c.addChild(tentacle7c2);
		setRotationAngle(tentacle7c2, 0.0F, -0.0873F, 0.0F);
		tentacle7c2.cubeList.add(new ModelBox(tentacle7c2, 125, 132, -3.0F, -4.001F, -5.0F, 3, 4, 5, 0.0F, false));

		tentmyc7_3b = new ModelRenderer(this);
		tentmyc7_3b.setRotationPoint(0.0F, 0.0F, -2.5F);
		tentacle7c2.addChild(tentmyc7_3b);
		tentmyc7_3b.cubeList.add(new ModelBox(tentmyc7_3b, 83, 195, -3.0F, 0.0F, -0.5F, 3, 2, 1, 0.0F, false));

		tentacle7d = new ModelRenderer(this);
		tentacle7d.setRotationPoint(-3.0F, 0.0F, -2.0F);
		tentacle7c2.addChild(tentacle7d);
		setRotationAngle(tentacle7d, 0.0F, 0.0F, 0.2182F);
		tentacle7d.cubeList.add(new ModelBox(tentacle7d, 117, 162, -3.0F, -3.0F, -2.001F, 3, 3, 4, 0.0F, false));

		tentmyc7_4a = new ModelRenderer(this);
		tentmyc7_4a.setRotationPoint(0.0F, 0.0F, -0.25F);
		tentacle7d.addChild(tentmyc7_4a);
		tentmyc7_4a.cubeList.add(new ModelBox(tentmyc7_4a, 90, 201, -3.0F, 0.0F, 0.25F, 4, 2, 0, 0.0F, false));

		tentacle7d2 = new ModelRenderer(this);
		tentacle7d2.setRotationPoint(-3.0F, 0.0F, 2.0F);
		tentacle7d.addChild(tentacle7d2);
		setRotationAngle(tentacle7d2, 0.0F, -0.1309F, 0.0F);
		tentacle7d2.cubeList.add(new ModelBox(tentacle7d2, 102, 162, -3.0F, -3.001F, -4.001F, 3, 3, 4, 0.0F, false));

		tentmyc_74b = new ModelRenderer(this);
		tentmyc_74b.setRotationPoint(0.0F, 0.0F, -2.0F);
		tentacle7d2.addChild(tentmyc_74b);
		tentmyc_74b.cubeList.add(new ModelBox(tentmyc_74b, 41, 207, -3.0F, 0.0F, 0.0F, 3, 2, 0, 0.0F, false));

		tentacle7e = new ModelRenderer(this);
		tentacle7e.setRotationPoint(-3.0F, 0.0F, -2.0F);
		tentacle7d2.addChild(tentacle7e);
		setRotationAngle(tentacle7e, 0.0F, 0.0F, 0.1745F);
		tentacle7e.cubeList.add(new ModelBox(tentacle7e, 54, 178, -3.0F, -2.0F, -2.0F, 3, 2, 3, 0.0F, false));

		tentacle7e2 = new ModelRenderer(this);
		tentacle7e2.setRotationPoint(-3.0F, 0.0F, 1.0F);
		tentacle7e.addChild(tentacle7e2);
		setRotationAngle(tentacle7e2, 0.0F, -0.1309F, 0.0F);
		tentacle7e2.cubeList.add(new ModelBox(tentacle7e2, 41, 178, -3.0F, -2.001F, -3.0F, 3, 2, 3, 0.0F, false));

		tentlet7_3a = new ModelRenderer(this);
		tentlet7_3a.setRotationPoint(-3.0F, 0.0F, -1.0F);
		tentacle7d2.addChild(tentlet7_3a);
		setRotationAngle(tentlet7_3a, 0.0F, 0.3491F, 0.0F);
		tentlet7_3a.cubeList.add(new ModelBox(tentlet7_3a, 83, 201, -2.0F, -3.0F, 0.0F, 2, 3, 1, 0.0F, false));

		tentlet7_2a = new ModelRenderer(this);
		tentlet7_2a.setRotationPoint(-3.0F, 0.0F, -4.0F);
		tentacle7c2.addChild(tentlet7_2a);
		setRotationAngle(tentlet7_2a, 0.0F, -0.3054F, 0.0F);
		tentlet7_2a.cubeList.add(new ModelBox(tentlet7_2a, 76, 195, -2.0F, -4.0F, -1.0F, 2, 4, 1, 0.0F, false));

		tentmyc7_3a = new ModelRenderer(this);
		tentmyc7_3a.setRotationPoint(0.0F, 0.0F, -0.5F);
		tentacle7c.addChild(tentmyc7_3a);
		tentmyc7_3a.cubeList.add(new ModelBox(tentmyc7_3a, 127, 190, -3.0F, 0.0F, -0.5F, 4, 2, 1, 0.0F, false));

		tentlet7_1a = new ModelRenderer(this);
		tentlet7_1a.setRotationPoint(-3.0F, 0.0F, -1.0F);
		tentacle7b2.addChild(tentlet7_1a);
		setRotationAngle(tentlet7_1a, 0.0F, 0.2618F, 0.0F);
		tentlet7_1a.cubeList.add(new ModelBox(tentlet7_1a, 69, 195, -2.0F, -4.0F, 0.0F, 2, 4, 1, 0.0F, false));

		mycotissue7 = new ModelRenderer(this);
		mycotissue7.setRotationPoint(-2.0F, -4.0F, 0.0F);
		tentacle7b.addChild(mycotissue7);
		setRotationAngle(mycotissue7, 0.0F, 0.0F, -0.2618F);
		

		mycotissue7_r1 = new ModelRenderer(this);
		mycotissue7_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
		mycotissue7.addChild(mycotissue7_r1);
		setRotationAngle(mycotissue7_r1, 0.0F, 0.0F, -0.829F);
		mycotissue7_r1.cubeList.add(new ModelBox(mycotissue7_r1, 27, 75, 0.0F, 0.0F, -1.5F, 10, 6, 3, 0.0F, false));

		base_tentacle8 = new ModelRenderer(this);
		base_tentacle8.setRotationPoint(0.0F, 0.0F, 0.0F);
		base.addChild(base_tentacle8);
		setRotationAngle(base_tentacle8, 0.0F, 0.7854F, 0.0F);
		

		tentacle8a = new ModelRenderer(this);
		tentacle8a.setRotationPoint(-5.0F, 0.0F, 0.0F);
		base_tentacle8.addChild(tentacle8a);
		setRotationAngle(tentacle8a, 0.0F, 0.0F, 0.3927F);
		tentacle8a.cubeList.add(new ModelBox(tentacle8a, 135, 75, -6.0F, -4.0F, -3.0F, 6, 4, 6, 0.0F, false));

		tentmyc8_1 = new ModelRenderer(this);
		tentmyc8_1.setRotationPoint(0.0F, 0.0F, 0.0F);
		tentacle8a.addChild(tentmyc8_1);
		tentmyc8_1.cubeList.add(new ModelBox(tentmyc8_1, 119, 143, -6.0F, 0.0F, -1.5F, 6, 3, 3, 0.0F, false));

		tentacle8b = new ModelRenderer(this);
		tentacle8b.setRotationPoint(-6.0F, 0.0F, 0.0F);
		tentacle8a.addChild(tentacle8b);
		setRotationAngle(tentacle8b, 0.0F, 0.0F, 0.6981F);
		tentacle8b.cubeList.add(new ModelBox(tentacle8b, 71, 99, -4.0F, -4.0F, -3.001F, 4, 4, 6, 0.0F, false));

		mycotissue8 = new ModelRenderer(this);
		mycotissue8.setRotationPoint(-4.0F, -4.0F, 0.0F);
		tentacle8b.addChild(mycotissue8);
		

		mycotissue8_r1 = new ModelRenderer(this);
		mycotissue8_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
		mycotissue8.addChild(mycotissue8_r1);
		setRotationAngle(mycotissue8_r1, 0.0F, 0.0F, -0.829F);
		mycotissue8_r1.cubeList.add(new ModelBox(mycotissue8_r1, 54, 39, 0.0F, 0.0F, -1.5F, 12, 8, 3, 0.0F, false));

		tentmyc8_2a = new ModelRenderer(this);
		tentmyc8_2a.setRotationPoint(0.0F, 0.0F, 0.0F);
		tentacle8b.addChild(tentmyc8_2a);
		tentmyc8_2a.cubeList.add(new ModelBox(tentmyc8_2a, 94, 170, -4.0F, 0.0F, -1.0F, 5, 2, 2, 0.0F, false));

		tentacle8b2 = new ModelRenderer(this);
		tentacle8b2.setRotationPoint(-4.0F, 0.0F, 3.0F);
		tentacle8b.addChild(tentacle8b2);
		setRotationAngle(tentacle8b2, 0.0F, -0.0873F, 0.0F);
		tentacle8b2.cubeList.add(new ModelBox(tentacle8b2, 50, 99, -4.0F, -4.001F, -6.0F, 4, 4, 6, 0.0F, false));

		tentmyc8_2b = new ModelRenderer(this);
		tentmyc8_2b.setRotationPoint(0.0F, 0.0F, -3.0F);
		tentacle8b2.addChild(tentmyc8_2b);
		tentmyc8_2b.cubeList.add(new ModelBox(tentmyc8_2b, 52, 184, -4.0F, 0.0F, -1.0F, 4, 2, 2, 0.0F, false));

		tentacle8c = new ModelRenderer(this);
		tentacle8c.setRotationPoint(-4.0F, 0.0F, -3.0F);
		tentacle8b2.addChild(tentacle8c);
		setRotationAngle(tentacle8c, 0.0F, 0.0F, 0.6545F);
		tentacle8c.cubeList.add(new ModelBox(tentacle8c, 76, 121, -4.0F, -4.0F, -2.999F, 4, 4, 5, 0.0F, false));

		tentacle8c2 = new ModelRenderer(this);
		tentacle8c2.setRotationPoint(-4.0F, 0.0F, 2.0F);
		tentacle8c.addChild(tentacle8c2);
		setRotationAngle(tentacle8c2, 0.0F, -0.1309F, 0.0F);
		tentacle8c2.cubeList.add(new ModelBox(tentacle8c2, 57, 121, -4.0F, -4.001F, -5.0F, 4, 4, 5, 0.0F, false));

		tentmyc8_3b = new ModelRenderer(this);
		tentmyc8_3b.setRotationPoint(0.0F, 0.0F, -2.5F);
		tentacle8c2.addChild(tentmyc8_3b);
		tentmyc8_3b.cubeList.add(new ModelBox(tentmyc8_3b, 116, 190, -4.0F, 0.0F, -0.5F, 4, 2, 1, 0.0F, false));

		tentacle8d = new ModelRenderer(this);
		tentacle8d.setRotationPoint(-4.0F, 0.0F, -2.0F);
		tentacle8c2.addChild(tentacle8d);
		setRotationAngle(tentacle8d, 0.0F, 0.0F, 0.2182F);
		tentacle8d.cubeList.add(new ModelBox(tentacle8d, 72, 153, -4.0F, -3.0F, -2.001F, 4, 3, 4, 0.0F, false));

		tentmyc8_4a = new ModelRenderer(this);
		tentmyc8_4a.setRotationPoint(0.0F, 0.0F, -0.25F);
		tentacle8d.addChild(tentmyc8_4a);
		tentmyc8_4a.cubeList.add(new ModelBox(tentmyc8_4a, 33, 195, -4.0F, 0.0F, 0.25F, 5, 2, 0, 0.0F, false));

		tentacle8d2 = new ModelRenderer(this);
		tentacle8d2.setRotationPoint(-4.0F, 0.0F, 2.0F);
		tentacle8d.addChild(tentacle8d2);
		setRotationAngle(tentacle8d2, 0.0F, -0.1745F, 0.0F);
		tentacle8d2.cubeList.add(new ModelBox(tentacle8d2, 55, 153, -4.0F, -3.001F, -4.001F, 4, 3, 4, 0.0F, false));

		tentmyc8_4b = new ModelRenderer(this);
		tentmyc8_4b.setRotationPoint(0.0F, 0.0F, -2.0F);
		tentacle8d2.addChild(tentmyc8_4b);
		tentmyc8_4b.cubeList.add(new ModelBox(tentmyc8_4b, 74, 201, -4.0F, 0.0F, 0.0F, 4, 2, 0, 0.0F, false));

		tentacle8e = new ModelRenderer(this);
		tentacle8e.setRotationPoint(-4.0F, 0.0F, -2.0F);
		tentacle8d2.addChild(tentacle8e);
		setRotationAngle(tentacle8e, 0.0F, 0.0F, 0.1745F);
		tentacle8e.cubeList.add(new ModelBox(tentacle8e, 28, 178, -3.0F, -2.0F, -2.0F, 3, 2, 3, 0.0F, false));

		tentacle8e2 = new ModelRenderer(this);
		tentacle8e2.setRotationPoint(-3.0F, 0.0F, 1.0F);
		tentacle8e.addChild(tentacle8e2);
		setRotationAngle(tentacle8e2, 0.0F, -0.1745F, 0.0F);
		tentacle8e2.cubeList.add(new ModelBox(tentacle8e2, 15, 178, -3.0F, -2.001F, -3.0F, 3, 2, 3, 0.0F, false));

		tentlet8_3a = new ModelRenderer(this);
		tentlet8_3a.setRotationPoint(-4.0F, 0.0F, -1.0F);
		tentacle8d2.addChild(tentlet8_3a);
		setRotationAngle(tentlet8_3a, 0.0F, 0.3491F, 0.0F);
		tentlet8_3a.cubeList.add(new ModelBox(tentlet8_3a, 67, 201, -2.0F, -3.0F, 0.0F, 2, 3, 1, 0.0F, false));

		tentlet8_2a = new ModelRenderer(this);
		tentlet8_2a.setRotationPoint(-4.0F, 0.0F, -4.0F);
		tentacle8c2.addChild(tentlet8_2a);
		setRotationAngle(tentlet8_2a, 0.0F, -0.3054F, 0.0F);
		tentlet8_2a.cubeList.add(new ModelBox(tentlet8_2a, 62, 195, -2.0F, -4.0F, -1.0F, 2, 4, 1, 0.0F, false));

		tentmyc8_3a = new ModelRenderer(this);
		tentmyc8_3a.setRotationPoint(0.0F, 0.0F, -0.5F);
		tentacle8c.addChild(tentmyc8_3a);
		tentmyc8_3a.cubeList.add(new ModelBox(tentmyc8_3a, 156, 184, -4.0F, 0.0F, -0.5F, 5, 2, 1, 0.0F, false));

		tentlet8_1a = new ModelRenderer(this);
		tentlet8_1a.setRotationPoint(-4.0F, 0.0F, -1.0F);
		tentacle8b2.addChild(tentlet8_1a);
		setRotationAngle(tentlet8_1a, 0.0F, 0.2618F, 0.0F);
		tentlet8_1a.cubeList.add(new ModelBox(tentlet8_1a, 55, 195, -2.0F, -4.0F, 0.0F, 2, 4, 1, 0.0F, false));

		spitty_part = new ModelRenderer(this);
		spitty_part.setRotationPoint(0.0F, -2.0F, 0.0F);
		spitty_part.cubeList.add(new ModelBox(spitty_part, 38, 153, -2.0F, -4.0F, -2.0F, 4, 4, 4, 0.0F, false));

		lip1 = new ModelRenderer(this);
		lip1.setRotationPoint(1.75F, -4.0F, -0.75F);
		spitty_part.addChild(lip1);
		setRotationAngle(lip1, -0.1309F, -1.0472F, 0.0F);
		lip1.cubeList.add(new ModelBox(lip1, 105, 190, -1.5F, -1.0F, -1.0F, 3, 2, 2, 0.0F, false));

		lip2 = new ModelRenderer(this);
		lip2.setRotationPoint(-1.75F, -4.0F, -0.75F);
		spitty_part.addChild(lip2);
		setRotationAngle(lip2, -0.1309F, 1.0472F, 0.0F);
		lip2.cubeList.add(new ModelBox(lip2, 94, 190, -1.5F, -1.0F, -1.0F, 3, 2, 2, 0.0F, false));

		lip3 = new ModelRenderer(this);
		lip3.setRotationPoint(0.0F, -4.0F, 2.25F);
		spitty_part.addChild(lip3);
		setRotationAngle(lip3, 0.1309F, 0.0F, 0.0F);
		lip3.cubeList.add(new ModelBox(lip3, 83, 190, -1.5F, -1.0F, -1.0F, 3, 2, 2, 0.0F, false));
	}

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {	
	}
	
    public void render(EntityBigPuffshroom entity, float partialTickTime) {
    	EntityBigPuffshroom bigPuffsroom = (EntityBigPuffshroom) entity;
		float interAnimationTicks_1 = bigPuffsroom.prev_animation_1 + (bigPuffsroom.animation_1 - bigPuffsroom.prev_animation_1) * partialTickTime;
		float interAnimationTicks_2 = bigPuffsroom.prev_animation_2 + (bigPuffsroom.animation_2 - bigPuffsroom.prev_animation_2) * partialTickTime;
		float interAnimationTicks_3 = bigPuffsroom.prev_animation_3 + (bigPuffsroom.animation_3 - bigPuffsroom.prev_animation_3) * partialTickTime;
		float interAnimationTicks_4 = bigPuffsroom.prev_animation_4 + (bigPuffsroom.animation_4 - bigPuffsroom.prev_animation_4) * partialTickTime;
		float smoothedTicks = bigPuffsroom.prev_renderTicks + (bigPuffsroom.renderTicks - bigPuffsroom.prev_renderTicks) * partialTickTime;
		float flap = MathHelper.sin((smoothedTicks) * 0.325F) * 0.125F;
		float flap2 = MathHelper.cos((smoothedTicks) * 0.325F) * 0.125F;
		float rise = 0F;

		if (bigPuffsroom.animation_1 < 8) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(0F, 0F - interAnimationTicks_1 * 0.25F, 0F);
			GlStateManager.rotate(0F + interAnimationTicks_1 * 22.5F, 0F, 1F, 0F);
			base.render(0.0625F);
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			spitty_part.render(0.0625F);
			core1a.render(0.0625F);
			core2a.render(0.0625F);
			core3a.render(0.0625F);
			core4a.render(0.0625F);
			GlStateManager.disableBlend();
			GlStateManager.popMatrix();
		}
		else {
			GlStateManager.pushMatrix();
			if (bigPuffsroom.getSlam()) {
				if (bigPuffsroom.active_4 && bigPuffsroom.pause) {
					rise = interAnimationTicks_4 * 0.025F;
				}
			}
			GlStateManager.translate(0F, 0F - interAnimationTicks_1 * 0.25F - rise, 0F);
			GlStateManager.rotate(0F + interAnimationTicks_1 * 22.55F, 0F, 1F, 0F);
			base.render(0.0625F);

			GlStateManager.pushMatrix();
			if (bigPuffsroom.animation_4 <= 8)
				GlStateManager.scale(1F + interAnimationTicks_4 * 0.125F, 1F, 1F + interAnimationTicks_4 * 0.125F);
			if (bigPuffsroom.animation_4 >= 10)
				GlStateManager.scale(1.75F - interAnimationTicks_4 * 0.0625F, 1F, 1.75F - interAnimationTicks_4 * 0.0625F);

			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			spitty_part.render(0.0625F);
			core1a.render(0.0625F);
			core2a.render(0.0625F);
			core3a.render(0.0625F);
			core4a.render(0.0625F);
			GlStateManager.disableBlend();
			GlStateManager.popMatrix();
			
			GlStateManager.popMatrix();
		}
    }
    
    @Override
    public void setLivingAnimations(EntityLivingBase entity, float swing, float speed, float partialRenderTicks) {
    	EntityBigPuffshroom bigPuffsroom = (EntityBigPuffshroom) entity;
		float interAnimationTicks_1 = bigPuffsroom.prev_animation_1 + (bigPuffsroom.animation_1 - bigPuffsroom.prev_animation_1) * partialRenderTicks;
		float interAnimationTicks_2 = bigPuffsroom.prev_animation_2 + (bigPuffsroom.animation_2 - bigPuffsroom.prev_animation_2) * partialRenderTicks;
		float interAnimationTicks_3 = bigPuffsroom.prev_animation_3 + (bigPuffsroom.animation_3 - bigPuffsroom.prev_animation_3) * partialRenderTicks;
		float interAnimationTicks_4 = bigPuffsroom.prev_animation_4 + (bigPuffsroom.animation_4 - bigPuffsroom.prev_animation_4) * partialRenderTicks;
		float smoothedTicks = bigPuffsroom.prev_renderTicks + (bigPuffsroom.renderTicks - bigPuffsroom.prev_renderTicks)* partialRenderTicks;
		float flap = MathHelper.sin((smoothedTicks) * 0.325F) * 0.0625F;
		float flap2 = MathHelper.cos((smoothedTicks) * 0.325F) * 0.0625F;
		float flap3 = MathHelper.sin((smoothedTicks) * 0.325F) * 0.0625F;
		float flap4 = MathHelper.cos((smoothedTicks) * 0.325F) * 0.0625F;

		if (bigPuffsroom.active_1) {
			flap = 0;
			flap2 = 0;
			flap3 = 0;
			flap4 = 0;
		}

		if (bigPuffsroom.getSlam()) {
			if ((bigPuffsroom.active_4 || bigPuffsroom.active_3) && bigPuffsroom.pause) {
				flap = -interAnimationTicks_4 * 0.025F;
				flap2 = -interAnimationTicks_4 * 0.025F;
				flap3 = interAnimationTicks_4 * 0.025F;
				flap4 = interAnimationTicks_4 * 0.025F;
			}
		}

		tentacle1a.rotateAngleZ = 0.3927F - interAnimationTicks_2 / (180F / (float) Math.PI) * 5.625F + flap4 * 2F;
		tentacle2a.rotateAngleZ = 0.3927F - interAnimationTicks_3 / (180F / (float) Math.PI) * 5.625F - flap2;
		tentacle3a.rotateAngleZ = 0.3927F - interAnimationTicks_2 / (180F / (float) Math.PI) * 5.625F + flap3 * 2F;
		tentacle4a.rotateAngleZ = 0.3927F - interAnimationTicks_3 / (180F / (float) Math.PI) * 5.625F - flap;
		tentacle5a.rotateAngleZ = 0.3927F - interAnimationTicks_2 / (180F / (float) Math.PI) * 5.625F - flap2 * 2F;
		tentacle6a.rotateAngleZ = 0.3927F - interAnimationTicks_3 / (180F / (float) Math.PI) * 5.625F + flap4;
		tentacle7a.rotateAngleZ = 0.3927F - interAnimationTicks_2 / (180F / (float) Math.PI) * 5.625F - flap * 2F;
		tentacle8a.rotateAngleZ = 0.3927F - interAnimationTicks_3 / (180F / (float) Math.PI) * 5.625F + flap3;

		//long
		tentacle2c.rotateAngleZ = 0.6545F - interAnimationTicks_3 / (180F / (float) Math.PI) + (!bigPuffsroom.getSlam() ? -flap * 4F : flap);
		tentacle4c.rotateAngleZ = 0.6545F - interAnimationTicks_3 / (180F / (float) Math.PI)  + (!bigPuffsroom.getSlam() ? -flap2 * 4F : flap2);
		tentacle6c.rotateAngleZ = 0.6545F - interAnimationTicks_3 / (180F / (float) Math.PI)  + (!bigPuffsroom.getSlam() ? flap3 * 4F : -flap3);
		tentacle8c.rotateAngleZ = 0.6545F - interAnimationTicks_3 / (180F / (float) Math.PI)  + (!bigPuffsroom.getSlam() ? flap4 * 4F : -flap4);

		//short
		tentacle1d.rotateAngleZ = 0.2182F - (interAnimationTicks_3 - interAnimationTicks_4 * 0.5F) / (180F / (float) Math.PI) * 5.625F - flap * 2F;
		tentacle3d.rotateAngleZ = 0.2182F - (interAnimationTicks_3 - interAnimationTicks_4 * 0.5F) / (180F / (float) Math.PI) * 5.625F - flap2 * 2F;
		tentacle5d.rotateAngleZ = 0.2182F - (interAnimationTicks_3 - interAnimationTicks_4 * 0.5F) / (180F / (float) Math.PI) * 5.625F + flap3 * 2F;
		tentacle7d.rotateAngleZ = 0.2182F - (interAnimationTicks_3 - interAnimationTicks_4 * 0.5F) / (180F / (float) Math.PI) * 5.625F + flap4 * 2F;	

		//long
		tentacle2d.rotateAngleZ = 0.2182F - (interAnimationTicks_3 - interAnimationTicks_4 * 0.5F) / (180F / (float) Math.PI) * 5.625F + flap * 4F;
		tentacle4d.rotateAngleZ = 0.2182F - (interAnimationTicks_3 - interAnimationTicks_4 * 0.5F) / (180F / (float) Math.PI) * 5.625F + flap2 * 4F;
		tentacle6d.rotateAngleZ = 0.2182F - (interAnimationTicks_3 - interAnimationTicks_4 * 0.5F) / (180F / (float) Math.PI) * 5.625F - flap3 * 4F;
		tentacle8d.rotateAngleZ = 0.2182F - (interAnimationTicks_3 - interAnimationTicks_4 * 0.5F) / (180F / (float) Math.PI) * 5.625F - flap4 * 4F;

		//short	
		tentacle1e.rotateAngleZ = 0.1745F - (interAnimationTicks_3 - interAnimationTicks_4 * 0.5F) / (180F / (float) Math.PI) * 5.625F - flap * 2F;
		tentacle3e.rotateAngleZ = 0.1745F - (interAnimationTicks_3 - interAnimationTicks_4 * 0.5F) / (180F / (float) Math.PI) * 5.625F - flap2 * 2F;
		tentacle5e.rotateAngleZ = 0.1745F - (interAnimationTicks_3 - interAnimationTicks_4 * 0.5F) / (180F / (float) Math.PI) * 5.625F + flap3 * 2F;
		tentacle7e.rotateAngleZ = 0.1745F - (interAnimationTicks_3 - interAnimationTicks_4 * 0.5F) / (180F / (float) Math.PI) * 5.625F + flap4 * 2F;

		//long	
		tentacle2e.rotateAngleZ = 0.1745F - (interAnimationTicks_3 - interAnimationTicks_4 * 0.5F) / (180F / (float) Math.PI) * 5.625F + flap * 4F;
		tentacle4e.rotateAngleZ = 0.1745F - (interAnimationTicks_3 - interAnimationTicks_4 * 0.5F) / (180F / (float) Math.PI) * 5.625F + flap2 * 4F;
		tentacle6e.rotateAngleZ = 0.1745F - (interAnimationTicks_3 - interAnimationTicks_4 * 0.5F) / (180F / (float) Math.PI) * 5.625F - flap3 * 4F;
		tentacle8e.rotateAngleZ = 0.1745F - (interAnimationTicks_3 - interAnimationTicks_4 * 0.5F) / (180F / (float) Math.PI) * 5.625F - flap4 * 4F;
    }

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}