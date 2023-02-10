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
	private final ModelRenderer base;
	private final ModelRenderer roots;
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
	private final ModelRenderer base_tentacle1;
	private final ModelRenderer tentacle1a;
	private final ModelRenderer tentmyc1;
	private final ModelRenderer tentacle1b;
	private final ModelRenderer tentmyc2a;
	private final ModelRenderer tentacle1b2;
	private final ModelRenderer tentmy2b;
	private final ModelRenderer tentacle1c;
	private final ModelRenderer tentacle1c2;
	private final ModelRenderer tentmyc3b;
	private final ModelRenderer tentacle1d;
	private final ModelRenderer tentmyc4a;
	private final ModelRenderer tentacle1d2;
	private final ModelRenderer tentmyc4b;
	private final ModelRenderer tentacle1e;
	private final ModelRenderer tentacle1e2;
	private final ModelRenderer tentlet3a;
	private final ModelRenderer tentlet2a;
	private final ModelRenderer tentmyc3a;
	private final ModelRenderer tentlet1a;
	private final ModelRenderer mycotissue2;
	private final ModelRenderer mycotissue1_r1;
	private final ModelRenderer base_tentacle2;
	private final ModelRenderer tentacle1a2;
	private final ModelRenderer tentmyc2;
	private final ModelRenderer tentacle1b3;
	private final ModelRenderer mycotissue1;
	private final ModelRenderer mycotissue1_r2;
	private final ModelRenderer tentmyc2a2;
	private final ModelRenderer tentacle1b4;
	private final ModelRenderer tentmy2b2;
	private final ModelRenderer tentacle1c3;
	private final ModelRenderer tentacle1c4;
	private final ModelRenderer tentmyc3b2;
	private final ModelRenderer tentacle1d3;
	private final ModelRenderer tentmyc4a2;
	private final ModelRenderer tentacle1d4;
	private final ModelRenderer tentmyc4b2;
	private final ModelRenderer tentacle1e3;
	private final ModelRenderer tentacle1e4;
	private final ModelRenderer tentlet3a2;
	private final ModelRenderer tentlet2a2;
	private final ModelRenderer tentmyc3a2;
	private final ModelRenderer tentlet1a2;
	private final ModelRenderer base_tentacle3;
	private final ModelRenderer tentacle1a3;
	private final ModelRenderer tentmyc3;
	private final ModelRenderer tentacle1b5;
	private final ModelRenderer tentmyc2a3;
	private final ModelRenderer tentacle1b6;
	private final ModelRenderer tentmy2b3;
	private final ModelRenderer tentacle1c5;
	private final ModelRenderer tentacle1c6;
	private final ModelRenderer tentmyc3b3;
	private final ModelRenderer tentacle1d5;
	private final ModelRenderer tentmyc4a3;
	private final ModelRenderer tentacle1d6;
	private final ModelRenderer tentmyc4b3;
	private final ModelRenderer tentacle1e5;
	private final ModelRenderer tentacle1e6;
	private final ModelRenderer tentlet3a3;
	private final ModelRenderer tentlet2a3;
	private final ModelRenderer tentmyc3a3;
	private final ModelRenderer tentlet1a3;
	private final ModelRenderer mycotissue3;
	private final ModelRenderer mycotissue1_r3;
	private final ModelRenderer base_tentacle4;
	private final ModelRenderer tentacle1a4;
	private final ModelRenderer tentmyc4;
	private final ModelRenderer tentacle1b7;
	private final ModelRenderer mycotissue4;
	private final ModelRenderer mycotissue1_r4;
	private final ModelRenderer tentmyc2a4;
	private final ModelRenderer tentacle1b8;
	private final ModelRenderer tentmy2b4;
	private final ModelRenderer tentacle1c7;
	private final ModelRenderer tentacle1c8;
	private final ModelRenderer tentmyc3b4;
	private final ModelRenderer tentacle1d7;
	private final ModelRenderer tentmyc4a4;
	private final ModelRenderer tentacle1d8;
	private final ModelRenderer tentmyc4b4;
	private final ModelRenderer tentacle1e7;
	private final ModelRenderer tentacle1e8;
	private final ModelRenderer tentlet3a4;
	private final ModelRenderer tentlet2a4;
	private final ModelRenderer tentmyc3a4;
	private final ModelRenderer tentlet1a4;
	private final ModelRenderer base_tentacle5;
	private final ModelRenderer tentacle1a5;
	private final ModelRenderer tentmyc5;
	private final ModelRenderer tentacle1b9;
	private final ModelRenderer tentmyc2a5;
	private final ModelRenderer tentacle1b10;
	private final ModelRenderer tentmy2b5;
	private final ModelRenderer tentacle1c9;
	private final ModelRenderer tentacle1c10;
	private final ModelRenderer tentmyc3b5;
	private final ModelRenderer tentacle1d9;
	private final ModelRenderer tentmyc4a5;
	private final ModelRenderer tentacle1d10;
	private final ModelRenderer tentmyc4b5;
	private final ModelRenderer tentacle1e9;
	private final ModelRenderer tentacle1e10;
	private final ModelRenderer tentlet3a5;
	private final ModelRenderer tentlet2a5;
	private final ModelRenderer tentmyc3a5;
	private final ModelRenderer tentlet1a5;
	private final ModelRenderer mycotissue5;
	private final ModelRenderer mycotissue1_r5;
	private final ModelRenderer base_tentacle6;
	private final ModelRenderer tentacle1a6;
	private final ModelRenderer tentmyc6;
	private final ModelRenderer tentacle1b11;
	private final ModelRenderer mycotissue6;
	private final ModelRenderer mycotissue1_r6;
	private final ModelRenderer tentmyc2a6;
	private final ModelRenderer tentacle1b12;
	private final ModelRenderer tentmy2b6;
	private final ModelRenderer tentacle1c11;
	private final ModelRenderer tentacle1c12;
	private final ModelRenderer tentmyc3b6;
	private final ModelRenderer tentacle1d11;
	private final ModelRenderer tentmyc4a6;
	private final ModelRenderer tentacle1d12;
	private final ModelRenderer tentmyc4b6;
	private final ModelRenderer tentacle1e11;
	private final ModelRenderer tentacle1e12;
	private final ModelRenderer tentlet3a6;
	private final ModelRenderer tentlet2a6;
	private final ModelRenderer tentmyc3a6;
	private final ModelRenderer tentlet1a6;
	private final ModelRenderer base_tentacle7;
	private final ModelRenderer tentacle1a7;
	private final ModelRenderer tentmyc7;
	private final ModelRenderer tentacle1b13;
	private final ModelRenderer tentmyc2a7;
	private final ModelRenderer tentacle1b14;
	private final ModelRenderer tentmy2b7;
	private final ModelRenderer tentacle1c13;
	private final ModelRenderer tentacle1c14;
	private final ModelRenderer tentmyc3b7;
	private final ModelRenderer tentacle1d13;
	private final ModelRenderer tentmyc4a7;
	private final ModelRenderer tentacle1d14;
	private final ModelRenderer tentmyc4b7;
	private final ModelRenderer tentacle1e13;
	private final ModelRenderer tentacle1e14;
	private final ModelRenderer tentlet3a7;
	private final ModelRenderer tentlet2a7;
	private final ModelRenderer tentmyc3a7;
	private final ModelRenderer tentlet1a7;
	private final ModelRenderer mycotissue7;
	private final ModelRenderer mycotissue1_r7;
	private final ModelRenderer base_tentacle8;
	private final ModelRenderer tentacle1a8;
	private final ModelRenderer tentmyc8;
	private final ModelRenderer tentacle1b15;
	private final ModelRenderer mycotissue8;
	private final ModelRenderer mycotissue1_r8;
	private final ModelRenderer tentmyc2a8;
	private final ModelRenderer tentacle1b16;
	private final ModelRenderer tentmy2b8;
	private final ModelRenderer tentacle1c15;
	private final ModelRenderer tentacle1c16;
	private final ModelRenderer tentmyc3b8;
	private final ModelRenderer tentacle1d15;
	private final ModelRenderer tentmyc4a8;
	private final ModelRenderer tentacle1d16;
	private final ModelRenderer tentmyc4b8;
	private final ModelRenderer tentacle1e15;
	private final ModelRenderer tentacle1e16;
	private final ModelRenderer tentlet3a8;
	private final ModelRenderer tentlet2a8;
	private final ModelRenderer tentmyc3a8;
	private final ModelRenderer tentlet1a8;
	private final ModelRenderer spitty_part;
	private final ModelRenderer lip1;
	private final ModelRenderer lip2;
	private final ModelRenderer lip3;

	public ModelBigPuffshroom() {
		textureWidth = 256;
		textureHeight = 256;

		base = new ModelRenderer(this);
		base.setRotationPoint(0.0F, 24.0F, 0.0F);
		base.cubeList.add(new ModelBox(base, 0, 0, -5.0F, -26.0F, -5.0F, 10, 28, 10, 0.0F, false));

		roots = new ModelRenderer(this);
		roots.setRotationPoint(0.0F, 0.0F, 0.0F);
		base.addChild(roots);
		roots.cubeList.add(new ModelBox(roots, 41, 0, -4.0F, -1.0F, -4.0F, 8, 7, 8, 0.0F, false));

		core1a = new ModelRenderer(this);
		core1a.setRotationPoint(0.0F, -26.0F, -5.0F);
		base.addChild(core1a);
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
		core2a.setRotationPoint(0.0F, -26.0F, 5.0F);
		base.addChild(core2a);
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
		core3a.setRotationPoint(-5.0F, -26.0F, 0.0F);
		base.addChild(core3a);
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
		core4a.setRotationPoint(5.0F, -26.0F, 0.0F);
		base.addChild(core4a);
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

		base_tentacle1 = new ModelRenderer(this);
		base_tentacle1.setRotationPoint(0.0F, 0.0F, 0.0F);
		base.addChild(base_tentacle1);
		

		tentacle1a = new ModelRenderer(this);
		tentacle1a.setRotationPoint(-5.0F, 0.0F, 0.0F);
		base_tentacle1.addChild(tentacle1a);
		setRotationAngle(tentacle1a, 0.0F, 0.0F, 0.3927F);
		tentacle1a.cubeList.add(new ModelBox(tentacle1a, 144, 88, -5.0F, -4.0F, -3.0F, 5, 4, 6, 0.0F, false));

		tentmyc1 = new ModelRenderer(this);
		tentmyc1.setRotationPoint(0.0F, 0.0F, 0.0F);
		tentacle1a.addChild(tentmyc1);
		tentmyc1.cubeList.add(new ModelBox(tentmyc1, 85, 162, -5.0F, 0.0F, -1.5F, 5, 3, 3, 0.0F, false));

		tentacle1b = new ModelRenderer(this);
		tentacle1b.setRotationPoint(-5.0F, 0.0F, 0.0F);
		tentacle1a.addChild(tentacle1b);
		setRotationAngle(tentacle1b, 0.0F, 0.0F, 0.6109F);
		tentacle1b.cubeList.add(new ModelBox(tentacle1b, 38, 121, -3.0F, -4.0F, -3.001F, 3, 4, 6, 0.0F, false));

		tentmyc2a = new ModelRenderer(this);
		tentmyc2a.setRotationPoint(0.0F, 0.0F, 0.0F);
		tentacle1b.addChild(tentmyc2a);
		tentmyc2a.cubeList.add(new ModelBox(tentmyc2a, 143, 184, -3.0F, 0.0F, -1.0F, 4, 2, 2, 0.0F, false));

		tentacle1b2 = new ModelRenderer(this);
		tentacle1b2.setRotationPoint(-3.0F, 0.0F, 3.0F);
		tentacle1b.addChild(tentacle1b2);
		setRotationAngle(tentacle1b2, 0.0F, -0.0873F, 0.0F);
		tentacle1b2.cubeList.add(new ModelBox(tentacle1b2, 19, 121, -3.0F, -4.001F, -6.0F, 3, 4, 6, 0.0F, false));

		tentmy2b = new ModelRenderer(this);
		tentmy2b.setRotationPoint(0.0F, 0.0F, -3.0F);
		tentacle1b2.addChild(tentmy2b);
		tentmy2b.cubeList.add(new ModelBox(tentmy2b, 72, 190, -3.0F, 0.0F, -1.0F, 3, 2, 2, 0.0F, false));

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

		tentmyc3b = new ModelRenderer(this);
		tentmyc3b.setRotationPoint(0.0F, 0.0F, -2.5F);
		tentacle1c2.addChild(tentmyc3b);
		tentmyc3b.cubeList.add(new ModelBox(tentmyc3b, 44, 201, -3.0F, 0.0F, -0.5F, 3, 2, 1, 0.0F, false));

		tentacle1d = new ModelRenderer(this);
		tentacle1d.setRotationPoint(-3.0F, 0.0F, -2.0F);
		tentacle1c2.addChild(tentacle1d);
		setRotationAngle(tentacle1d, 0.0F, 0.0F, 0.2182F);
		tentacle1d.cubeList.add(new ModelBox(tentacle1d, 45, 170, -3.0F, -3.0F, -2.001F, 3, 3, 4, 0.0F, false));

		tentmyc4a = new ModelRenderer(this);
		tentmyc4a.setRotationPoint(0.0F, 0.0F, -0.25F);
		tentacle1d.addChild(tentmyc4a);
		tentmyc4a.cubeList.add(new ModelBox(tentmyc4a, 25, 207, -3.0F, 0.0F, 0.25F, 4, 2, 0, 0.0F, false));

		tentacle1d2 = new ModelRenderer(this);
		tentacle1d2.setRotationPoint(-3.0F, 0.0F, 2.0F);
		tentacle1d.addChild(tentacle1d2);
		setRotationAngle(tentacle1d2, 0.0F, -0.1309F, 0.0F);
		tentacle1d2.cubeList.add(new ModelBox(tentacle1d2, 30, 170, -3.0F, -3.001F, -4.001F, 3, 3, 4, 0.0F, false));

		tentmyc4b = new ModelRenderer(this);
		tentmyc4b.setRotationPoint(0.0F, 0.0F, -2.0F);
		tentacle1d2.addChild(tentmyc4b);
		tentmyc4b.cubeList.add(new ModelBox(tentmyc4b, 62, 207, -3.0F, 0.0F, 0.0F, 3, 2, 0, 0.0F, false));

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

		tentlet3a = new ModelRenderer(this);
		tentlet3a.setRotationPoint(-3.0F, 0.0F, -1.0F);
		tentacle1d2.addChild(tentlet3a);
		setRotationAngle(tentlet3a, 0.0F, 0.3491F, 0.0F);
		tentlet3a.cubeList.add(new ModelBox(tentlet3a, 34, 207, -2.0F, -3.0F, 0.0F, 2, 3, 1, 0.0F, false));

		tentlet2a = new ModelRenderer(this);
		tentlet2a.setRotationPoint(-3.0F, 0.0F, -4.0F);
		tentacle1c2.addChild(tentlet2a);
		setRotationAngle(tentlet2a, 0.0F, -0.3054F, 0.0F);
		tentlet2a.cubeList.add(new ModelBox(tentlet2a, 53, 201, -2.0F, -4.0F, -1.0F, 2, 4, 1, 0.0F, false));

		tentmyc3a = new ModelRenderer(this);
		tentmyc3a.setRotationPoint(0.0F, 0.0F, -0.5F);
		tentacle1c.addChild(tentmyc3a);
		tentmyc3a.cubeList.add(new ModelBox(tentmyc3a, 22, 195, -3.0F, 0.0F, -0.5F, 4, 2, 1, 0.0F, false));

		tentlet1a = new ModelRenderer(this);
		tentlet1a.setRotationPoint(-3.0F, 0.0F, -1.0F);
		tentacle1b2.addChild(tentlet1a);
		setRotationAngle(tentlet1a, 0.0F, 0.2618F, 0.0F);
		tentlet1a.cubeList.add(new ModelBox(tentlet1a, 60, 201, -2.0F, -4.0F, 0.0F, 2, 4, 1, 0.0F, false));

		mycotissue2 = new ModelRenderer(this);
		mycotissue2.setRotationPoint(-2.0F, -4.0F, 0.0F);
		tentacle1b.addChild(mycotissue2);
		setRotationAngle(mycotissue2, 0.0F, 0.0F, -0.2618F);
		

		mycotissue1_r1 = new ModelRenderer(this);
		mycotissue1_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
		mycotissue2.addChild(mycotissue1_r1);
		setRotationAngle(mycotissue1_r1, 0.0F, 0.0F, -0.829F);
		mycotissue1_r1.cubeList.add(new ModelBox(mycotissue1_r1, 108, 75, 0.0F, 0.0F, -1.5F, 10, 6, 3, 0.0F, false));

		base_tentacle2 = new ModelRenderer(this);
		base_tentacle2.setRotationPoint(0.0F, 0.0F, 0.0F);
		base.addChild(base_tentacle2);
		setRotationAngle(base_tentacle2, 0.0F, -0.7854F, 0.0F);
		

		tentacle1a2 = new ModelRenderer(this);
		tentacle1a2.setRotationPoint(-5.0F, 0.0F, 0.0F);
		base_tentacle2.addChild(tentacle1a2);
		setRotationAngle(tentacle1a2, 0.0F, 0.0F, 0.3927F);
		tentacle1a2.cubeList.add(new ModelBox(tentacle1a2, 50, 88, -6.0F, -4.0F, -3.0F, 6, 4, 6, 0.0F, false));

		tentmyc2 = new ModelRenderer(this);
		tentmyc2.setRotationPoint(0.0F, 0.0F, 0.0F);
		tentacle1a2.addChild(tentmyc2);
		tentmyc2.cubeList.add(new ModelBox(tentmyc2, 19, 153, -6.0F, 0.0F, -1.5F, 6, 3, 3, 0.0F, false));

		tentacle1b3 = new ModelRenderer(this);
		tentacle1b3.setRotationPoint(-6.0F, 0.0F, 0.0F);
		tentacle1a2.addChild(tentacle1b3);
		setRotationAngle(tentacle1b3, 0.0F, 0.0F, 0.6981F);
		tentacle1b3.cubeList.add(new ModelBox(tentacle1b3, 42, 110, -4.0F, -4.0F, -3.001F, 4, 4, 6, 0.0F, false));

		mycotissue1 = new ModelRenderer(this);
		mycotissue1.setRotationPoint(-4.0F, -4.0F, 0.0F);
		tentacle1b3.addChild(mycotissue1);
		

		mycotissue1_r2 = new ModelRenderer(this);
		mycotissue1_r2.setRotationPoint(0.0F, 0.0F, 0.0F);
		mycotissue1.addChild(mycotissue1_r2);
		setRotationAngle(mycotissue1_r2, 0.0F, 0.0F, -0.829F);
		mycotissue1_r2.cubeList.add(new ModelBox(mycotissue1_r2, 0, 59, 0.0F, 0.0F, -1.5F, 12, 8, 3, 0.0F, false));

		tentmyc2a2 = new ModelRenderer(this);
		tentmyc2a2.setRotationPoint(0.0F, 0.0F, 0.0F);
		tentacle1b3.addChild(tentmyc2a2);
		tentmyc2a2.cubeList.add(new ModelBox(tentmyc2a2, 0, 178, -4.0F, 0.0F, -1.0F, 5, 2, 2, 0.0F, false));

		tentacle1b4 = new ModelRenderer(this);
		tentacle1b4.setRotationPoint(-4.0F, 0.0F, 3.0F);
		tentacle1b3.addChild(tentacle1b4);
		setRotationAngle(tentacle1b4, 0.0F, -0.0873F, 0.0F);
		tentacle1b4.cubeList.add(new ModelBox(tentacle1b4, 21, 110, -4.0F, -4.001F, -6.0F, 4, 4, 6, 0.0F, false));

		tentmy2b2 = new ModelRenderer(this);
		tentmy2b2.setRotationPoint(0.0F, 0.0F, -3.0F);
		tentacle1b4.addChild(tentmy2b2);
		tentmy2b2.cubeList.add(new ModelBox(tentmy2b2, 130, 184, -4.0F, 0.0F, -1.0F, 4, 2, 2, 0.0F, false));

		tentacle1c3 = new ModelRenderer(this);
		tentacle1c3.setRotationPoint(-4.0F, 0.0F, -3.0F);
		tentacle1b4.addChild(tentacle1c3);
		setRotationAngle(tentacle1c3, 0.0F, 0.0F, 0.6545F);
		tentacle1c3.cubeList.add(new ModelBox(tentacle1c3, 38, 132, -4.0F, -4.0F, -2.999F, 4, 4, 5, 0.0F, false));

		tentacle1c4 = new ModelRenderer(this);
		tentacle1c4.setRotationPoint(-4.0F, 0.0F, 2.0F);
		tentacle1c3.addChild(tentacle1c4);
		setRotationAngle(tentacle1c4, 0.0F, -0.1309F, 0.0F);
		tentacle1c4.cubeList.add(new ModelBox(tentacle1c4, 19, 132, -4.0F, -4.001F, -5.0F, 4, 4, 5, 0.0F, false));

		tentmyc3b2 = new ModelRenderer(this);
		tentmyc3b2.setRotationPoint(0.0F, 0.0F, -2.5F);
		tentacle1c4.addChild(tentmyc3b2);
		tentmyc3b2.cubeList.add(new ModelBox(tentmyc3b2, 11, 195, -4.0F, 0.0F, -0.5F, 4, 2, 1, 0.0F, false));

		tentacle1d3 = new ModelRenderer(this);
		tentacle1d3.setRotationPoint(-4.0F, 0.0F, -2.0F);
		tentacle1c4.addChild(tentacle1d3);
		setRotationAngle(tentacle1d3, 0.0F, 0.0F, 0.2182F);
		tentacle1d3.cubeList.add(new ModelBox(tentacle1d3, 17, 162, -4.0F, -3.0F, -2.001F, 4, 3, 4, 0.0F, false));

		tentmyc4a2 = new ModelRenderer(this);
		tentmyc4a2.setRotationPoint(0.0F, 0.0F, -0.25F);
		tentacle1d3.addChild(tentmyc4a2);
		tentmyc4a2.cubeList.add(new ModelBox(tentmyc4a2, 44, 198, -4.0F, 0.0F, 0.25F, 5, 2, 0, 0.0F, false));

		tentacle1d4 = new ModelRenderer(this);
		tentacle1d4.setRotationPoint(-4.0F, 0.0F, 2.0F);
		tentacle1d3.addChild(tentacle1d4);
		setRotationAngle(tentacle1d4, 0.0F, -0.1745F, 0.0F);
		tentacle1d4.cubeList.add(new ModelBox(tentacle1d4, 0, 162, -4.0F, -3.001F, -4.001F, 4, 3, 4, 0.0F, false));

		tentmyc4b2 = new ModelRenderer(this);
		tentmyc4b2.setRotationPoint(0.0F, 0.0F, -2.0F);
		tentacle1d4.addChild(tentmyc4b2);
		tentmyc4b2.cubeList.add(new ModelBox(tentmyc4b2, 16, 207, -4.0F, 0.0F, 0.0F, 4, 2, 0, 0.0F, false));

		tentacle1e3 = new ModelRenderer(this);
		tentacle1e3.setRotationPoint(-4.0F, 0.0F, -2.0F);
		tentacle1d4.addChild(tentacle1e3);
		setRotationAngle(tentacle1e3, 0.0F, 0.0F, 0.1745F);
		tentacle1e3.cubeList.add(new ModelBox(tentacle1e3, 13, 184, -3.0F, -2.0F, -2.0F, 3, 2, 3, 0.0F, false));

		tentacle1e4 = new ModelRenderer(this);
		tentacle1e4.setRotationPoint(-3.0F, 0.0F, 1.0F);
		tentacle1e3.addChild(tentacle1e4);
		setRotationAngle(tentacle1e4, 0.0F, -0.1745F, 0.0F);
		tentacle1e4.cubeList.add(new ModelBox(tentacle1e4, 0, 184, -3.0F, -2.001F, -3.0F, 3, 2, 3, 0.0F, false));

		tentlet3a2 = new ModelRenderer(this);
		tentlet3a2.setRotationPoint(-4.0F, 0.0F, -1.0F);
		tentacle1d4.addChild(tentlet3a2);
		setRotationAngle(tentlet3a2, 0.0F, 0.3491F, 0.0F);
		tentlet3a2.cubeList.add(new ModelBox(tentlet3a2, 9, 207, -2.0F, -3.0F, 0.0F, 2, 3, 1, 0.0F, false));

		tentlet2a2 = new ModelRenderer(this);
		tentlet2a2.setRotationPoint(-4.0F, 0.0F, -4.0F);
		tentacle1c4.addChild(tentlet2a2);
		setRotationAngle(tentlet2a2, 0.0F, -0.3054F, 0.0F);
		tentlet2a2.cubeList.add(new ModelBox(tentlet2a2, 37, 201, -2.0F, -4.0F, -1.0F, 2, 4, 1, 0.0F, false));

		tentmyc3a2 = new ModelRenderer(this);
		tentmyc3a2.setRotationPoint(0.0F, 0.0F, -0.5F);
		tentacle1c3.addChild(tentmyc3a2);
		tentmyc3a2.cubeList.add(new ModelBox(tentmyc3a2, 26, 190, -4.0F, 0.0F, -0.5F, 5, 2, 1, 0.0F, false));

		tentlet1a2 = new ModelRenderer(this);
		tentlet1a2.setRotationPoint(-4.0F, 0.0F, -1.0F);
		tentacle1b4.addChild(tentlet1a2);
		setRotationAngle(tentlet1a2, 0.0F, 0.2618F, 0.0F);
		tentlet1a2.cubeList.add(new ModelBox(tentlet1a2, 30, 201, -2.0F, -4.0F, 0.0F, 2, 4, 1, 0.0F, false));

		base_tentacle3 = new ModelRenderer(this);
		base_tentacle3.setRotationPoint(0.0F, 0.0F, 0.0F);
		base.addChild(base_tentacle3);
		setRotationAngle(base_tentacle3, 0.0F, -1.5708F, 0.0F);
		

		tentacle1a3 = new ModelRenderer(this);
		tentacle1a3.setRotationPoint(-5.0F, 0.0F, 0.0F);
		base_tentacle3.addChild(tentacle1a3);
		setRotationAngle(tentacle1a3, 0.0F, 0.0F, 0.3927F);
		tentacle1a3.cubeList.add(new ModelBox(tentacle1a3, 121, 88, -5.0F, -4.0F, -3.0F, 5, 4, 6, 0.0F, false));

		tentmyc3 = new ModelRenderer(this);
		tentmyc3.setRotationPoint(0.0F, 0.0F, 0.0F);
		tentacle1a3.addChild(tentmyc3);
		tentmyc3.cubeList.add(new ModelBox(tentmyc3, 68, 162, -5.0F, 0.0F, -1.5F, 5, 3, 3, 0.0F, false));

		tentacle1b5 = new ModelRenderer(this);
		tentacle1b5.setRotationPoint(-5.0F, 0.0F, 0.0F);
		tentacle1a3.addChild(tentacle1b5);
		setRotationAngle(tentacle1b5, 0.0F, 0.0F, 0.6109F);
		tentacle1b5.cubeList.add(new ModelBox(tentacle1b5, 0, 121, -3.0F, -4.0F, -3.001F, 3, 4, 6, 0.0F, false));

		tentmyc2a3 = new ModelRenderer(this);
		tentmyc2a3.setRotationPoint(0.0F, 0.0F, 0.0F);
		tentacle1b5.addChild(tentmyc2a3);
		tentmyc2a3.cubeList.add(new ModelBox(tentmyc2a3, 117, 184, -3.0F, 0.0F, -1.0F, 4, 2, 2, 0.0F, false));

		tentacle1b6 = new ModelRenderer(this);
		tentacle1b6.setRotationPoint(-3.0F, 0.0F, 3.0F);
		tentacle1b5.addChild(tentacle1b6);
		setRotationAngle(tentacle1b6, 0.0F, -0.0873F, 0.0F);
		tentacle1b6.cubeList.add(new ModelBox(tentacle1b6, 139, 110, -3.0F, -4.001F, -6.0F, 3, 4, 6, 0.0F, false));

		tentmy2b3 = new ModelRenderer(this);
		tentmy2b3.setRotationPoint(0.0F, 0.0F, -3.0F);
		tentacle1b6.addChild(tentmy2b3);
		tentmy2b3.cubeList.add(new ModelBox(tentmy2b3, 61, 190, -3.0F, 0.0F, -1.0F, 3, 2, 2, 0.0F, false));

		tentacle1c5 = new ModelRenderer(this);
		tentacle1c5.setRotationPoint(-3.0F, 0.0F, -3.0F);
		tentacle1b6.addChild(tentacle1c5);
		setRotationAngle(tentacle1c5, 0.0F, 0.0F, 0.5672F);
		tentacle1c5.cubeList.add(new ModelBox(tentacle1c5, 68, 143, -3.0F, -4.0F, -2.999F, 3, 4, 5, 0.0F, false));

		tentacle1c6 = new ModelRenderer(this);
		tentacle1c6.setRotationPoint(-3.0F, 0.0F, 2.0F);
		tentacle1c5.addChild(tentacle1c6);
		setRotationAngle(tentacle1c6, 0.0F, -0.0873F, 0.0F);
		tentacle1c6.cubeList.add(new ModelBox(tentacle1c6, 51, 143, -3.0F, -4.001F, -5.0F, 3, 4, 5, 0.0F, false));

		tentmyc3b3 = new ModelRenderer(this);
		tentmyc3b3.setRotationPoint(0.0F, 0.0F, -2.5F);
		tentacle1c6.addChild(tentmyc3b3);
		tentmyc3b3.cubeList.add(new ModelBox(tentmyc3b3, 21, 201, -3.0F, 0.0F, -0.5F, 3, 2, 1, 0.0F, false));

		tentacle1d5 = new ModelRenderer(this);
		tentacle1d5.setRotationPoint(-3.0F, 0.0F, -2.0F);
		tentacle1c6.addChild(tentacle1d5);
		setRotationAngle(tentacle1d5, 0.0F, 0.0F, 0.2182F);
		tentacle1d5.cubeList.add(new ModelBox(tentacle1d5, 15, 170, -3.0F, -3.0F, -2.001F, 3, 3, 4, 0.0F, false));

		tentmyc4a3 = new ModelRenderer(this);
		tentmyc4a3.setRotationPoint(0.0F, 0.0F, -0.25F);
		tentacle1d5.addChild(tentmyc4a3);
		tentmyc4a3.cubeList.add(new ModelBox(tentmyc4a3, 0, 207, -3.0F, 0.0F, 0.25F, 4, 2, 0, 0.0F, false));

		tentacle1d6 = new ModelRenderer(this);
		tentacle1d6.setRotationPoint(-3.0F, 0.0F, 2.0F);
		tentacle1d5.addChild(tentacle1d6);
		setRotationAngle(tentacle1d6, 0.0F, -0.1309F, 0.0F);
		tentacle1d6.cubeList.add(new ModelBox(tentacle1d6, 0, 170, -3.0F, -3.001F, -4.001F, 3, 3, 4, 0.0F, false));

		tentmyc4b3 = new ModelRenderer(this);
		tentmyc4b3.setRotationPoint(0.0F, 0.0F, -2.0F);
		tentacle1d6.addChild(tentmyc4b3);
		tentmyc4b3.cubeList.add(new ModelBox(tentmyc4b3, 55, 207, -3.0F, 0.0F, 0.0F, 3, 2, 0, 0.0F, false));

		tentacle1e5 = new ModelRenderer(this);
		tentacle1e5.setRotationPoint(-3.0F, 0.0F, -2.0F);
		tentacle1d6.addChild(tentacle1e5);
		setRotationAngle(tentacle1e5, 0.0F, 0.0F, 0.1745F);
		tentacle1e5.cubeList.add(new ModelBox(tentacle1e5, 158, 178, -3.0F, -2.0F, -2.0F, 3, 2, 3, 0.0F, false));

		tentacle1e6 = new ModelRenderer(this);
		tentacle1e6.setRotationPoint(-3.0F, 0.0F, 1.0F);
		tentacle1e5.addChild(tentacle1e6);
		setRotationAngle(tentacle1e6, 0.0F, -0.1309F, 0.0F);
		tentacle1e6.cubeList.add(new ModelBox(tentacle1e6, 145, 178, -3.0F, -2.001F, -3.0F, 3, 2, 3, 0.0F, false));

		tentlet3a3 = new ModelRenderer(this);
		tentlet3a3.setRotationPoint(-3.0F, 0.0F, -1.0F);
		tentacle1d6.addChild(tentlet3a3);
		setRotationAngle(tentlet3a3, 0.0F, 0.3491F, 0.0F);
		tentlet3a3.cubeList.add(new ModelBox(tentlet3a3, 147, 201, -2.0F, -3.0F, 0.0F, 2, 3, 1, 0.0F, false));

		tentlet2a3 = new ModelRenderer(this);
		tentlet2a3.setRotationPoint(-3.0F, 0.0F, -4.0F);
		tentacle1c6.addChild(tentlet2a3);
		setRotationAngle(tentlet2a3, 0.0F, -0.3054F, 0.0F);
		tentlet2a3.cubeList.add(new ModelBox(tentlet2a3, 14, 201, -2.0F, -4.0F, -1.0F, 2, 4, 1, 0.0F, false));

		tentmyc3a3 = new ModelRenderer(this);
		tentmyc3a3.setRotationPoint(0.0F, 0.0F, -0.5F);
		tentacle1c5.addChild(tentmyc3a3);
		tentmyc3a3.cubeList.add(new ModelBox(tentmyc3a3, 0, 195, -3.0F, 0.0F, -0.5F, 4, 2, 1, 0.0F, false));

		tentlet1a3 = new ModelRenderer(this);
		tentlet1a3.setRotationPoint(-3.0F, 0.0F, -1.0F);
		tentacle1b6.addChild(tentlet1a3);
		setRotationAngle(tentlet1a3, 0.0F, 0.2618F, 0.0F);
		tentlet1a3.cubeList.add(new ModelBox(tentlet1a3, 7, 201, -2.0F, -4.0F, 0.0F, 2, 4, 1, 0.0F, false));

		mycotissue3 = new ModelRenderer(this);
		mycotissue3.setRotationPoint(-2.0F, -4.0F, 0.0F);
		tentacle1b5.addChild(mycotissue3);
		setRotationAngle(mycotissue3, 0.0F, 0.0F, -0.2618F);
		

		mycotissue1_r3 = new ModelRenderer(this);
		mycotissue1_r3.setRotationPoint(0.0F, 0.0F, 0.0F);
		mycotissue3.addChild(mycotissue1_r3);
		setRotationAngle(mycotissue1_r3, 0.0F, 0.0F, -0.829F);
		mycotissue1_r3.cubeList.add(new ModelBox(mycotissue1_r3, 81, 75, 0.0F, 0.0F, -1.5F, 10, 6, 3, 0.0F, false));

		base_tentacle4 = new ModelRenderer(this);
		base_tentacle4.setRotationPoint(0.0F, 0.0F, 0.0F);
		base.addChild(base_tentacle4);
		setRotationAngle(base_tentacle4, 0.0F, -2.3562F, 0.0F);
		

		tentacle1a4 = new ModelRenderer(this);
		tentacle1a4.setRotationPoint(-5.0F, 0.0F, 0.0F);
		base_tentacle4.addChild(tentacle1a4);
		setRotationAngle(tentacle1a4, 0.0F, 0.0F, 0.3927F);
		tentacle1a4.cubeList.add(new ModelBox(tentacle1a4, 25, 88, -6.0F, -4.0F, -3.0F, 6, 4, 6, 0.0F, false));

		tentmyc4 = new ModelRenderer(this);
		tentmyc4.setRotationPoint(0.0F, 0.0F, 0.0F);
		tentacle1a4.addChild(tentmyc4);
		tentmyc4.cubeList.add(new ModelBox(tentmyc4, 0, 153, -6.0F, 0.0F, -1.5F, 6, 3, 3, 0.0F, false));

		tentacle1b7 = new ModelRenderer(this);
		tentacle1b7.setRotationPoint(-6.0F, 0.0F, 0.0F);
		tentacle1a4.addChild(tentacle1b7);
		setRotationAngle(tentacle1b7, 0.0F, 0.0F, 0.6981F);
		tentacle1b7.cubeList.add(new ModelBox(tentacle1b7, 0, 110, -4.0F, -4.0F, -3.001F, 4, 4, 6, 0.0F, false));

		mycotissue4 = new ModelRenderer(this);
		mycotissue4.setRotationPoint(-4.0F, -4.0F, 0.0F);
		tentacle1b7.addChild(mycotissue4);
		

		mycotissue1_r4 = new ModelRenderer(this);
		mycotissue1_r4.setRotationPoint(0.0F, 0.0F, 0.0F);
		mycotissue4.addChild(mycotissue1_r4);
		setRotationAngle(mycotissue1_r4, 0.0F, 0.0F, -0.829F);
		mycotissue1_r4.cubeList.add(new ModelBox(mycotissue1_r4, 116, 39, 0.0F, 0.0F, -1.5F, 12, 8, 3, 0.0F, false));

		tentmyc2a4 = new ModelRenderer(this);
		tentmyc2a4.setRotationPoint(0.0F, 0.0F, 0.0F);
		tentacle1b7.addChild(tentmyc2a4);
		tentmyc2a4.cubeList.add(new ModelBox(tentmyc2a4, 124, 170, -4.0F, 0.0F, -1.0F, 5, 2, 2, 0.0F, false));

		tentacle1b8 = new ModelRenderer(this);
		tentacle1b8.setRotationPoint(-4.0F, 0.0F, 3.0F);
		tentacle1b7.addChild(tentacle1b8);
		setRotationAngle(tentacle1b8, 0.0F, -0.0873F, 0.0F);
		tentacle1b8.cubeList.add(new ModelBox(tentacle1b8, 134, 99, -4.0F, -4.001F, -6.0F, 4, 4, 6, 0.0F, false));

		tentmy2b4 = new ModelRenderer(this);
		tentmy2b4.setRotationPoint(0.0F, 0.0F, -3.0F);
		tentacle1b8.addChild(tentmy2b4);
		tentmy2b4.cubeList.add(new ModelBox(tentmy2b4, 104, 184, -4.0F, 0.0F, -1.0F, 4, 2, 2, 0.0F, false));

		tentacle1c7 = new ModelRenderer(this);
		tentacle1c7.setRotationPoint(-4.0F, 0.0F, -3.0F);
		tentacle1b8.addChild(tentacle1c7);
		setRotationAngle(tentacle1c7, 0.0F, 0.0F, 0.6545F);
		tentacle1c7.cubeList.add(new ModelBox(tentacle1c7, 0, 132, -4.0F, -4.0F, -2.999F, 4, 4, 5, 0.0F, false));

		tentacle1c8 = new ModelRenderer(this);
		tentacle1c8.setRotationPoint(-4.0F, 0.0F, 2.0F);
		tentacle1c7.addChild(tentacle1c8);
		setRotationAngle(tentacle1c8, 0.0F, -0.1309F, 0.0F);
		tentacle1c8.cubeList.add(new ModelBox(tentacle1c8, 133, 121, -4.0F, -4.001F, -5.0F, 4, 4, 5, 0.0F, false));

		tentmyc3b4 = new ModelRenderer(this);
		tentmyc3b4.setRotationPoint(0.0F, 0.0F, -2.5F);
		tentacle1c8.addChild(tentmyc3b4);
		tentmyc3b4.cubeList.add(new ModelBox(tentmyc3b4, 160, 190, -4.0F, 0.0F, -0.5F, 4, 2, 1, 0.0F, false));

		tentacle1d7 = new ModelRenderer(this);
		tentacle1d7.setRotationPoint(-4.0F, 0.0F, -2.0F);
		tentacle1c8.addChild(tentacle1d7);
		setRotationAngle(tentacle1d7, 0.0F, 0.0F, 0.2182F);
		tentacle1d7.cubeList.add(new ModelBox(tentacle1d7, 140, 153, -4.0F, -3.0F, -2.001F, 4, 3, 4, 0.0F, false));

		tentmyc4a4 = new ModelRenderer(this);
		tentmyc4a4.setRotationPoint(0.0F, 0.0F, -0.25F);
		tentacle1d7.addChild(tentmyc4a4);
		tentmyc4a4.cubeList.add(new ModelBox(tentmyc4a4, 44, 195, -4.0F, 0.0F, 0.25F, 5, 2, 0, 0.0F, false));

		tentacle1d8 = new ModelRenderer(this);
		tentacle1d8.setRotationPoint(-4.0F, 0.0F, 2.0F);
		tentacle1d7.addChild(tentacle1d8);
		setRotationAngle(tentacle1d8, 0.0F, -0.1745F, 0.0F);
		tentacle1d8.cubeList.add(new ModelBox(tentacle1d8, 123, 153, -4.0F, -3.001F, -4.001F, 4, 3, 4, 0.0F, false));

		tentmyc4b4 = new ModelRenderer(this);
		tentmyc4b4.setRotationPoint(0.0F, 0.0F, -2.0F);
		tentacle1d8.addChild(tentmyc4b4);
		tentmyc4b4.cubeList.add(new ModelBox(tentmyc4b4, 138, 201, -4.0F, 0.0F, 0.0F, 4, 2, 0, 0.0F, false));

		tentacle1e7 = new ModelRenderer(this);
		tentacle1e7.setRotationPoint(-4.0F, 0.0F, -2.0F);
		tentacle1d8.addChild(tentacle1e7);
		setRotationAngle(tentacle1e7, 0.0F, 0.0F, 0.1745F);
		tentacle1e7.cubeList.add(new ModelBox(tentacle1e7, 132, 178, -3.0F, -2.0F, -2.0F, 3, 2, 3, 0.0F, false));

		tentacle1e8 = new ModelRenderer(this);
		tentacle1e8.setRotationPoint(-3.0F, 0.0F, 1.0F);
		tentacle1e7.addChild(tentacle1e8);
		setRotationAngle(tentacle1e8, 0.0F, -0.1745F, 0.0F);
		tentacle1e8.cubeList.add(new ModelBox(tentacle1e8, 119, 178, -3.0F, -2.001F, -3.0F, 3, 2, 3, 0.0F, false));

		tentlet3a4 = new ModelRenderer(this);
		tentlet3a4.setRotationPoint(-4.0F, 0.0F, -1.0F);
		tentacle1d8.addChild(tentlet3a4);
		setRotationAngle(tentlet3a4, 0.0F, 0.3491F, 0.0F);
		tentlet3a4.cubeList.add(new ModelBox(tentlet3a4, 131, 201, -2.0F, -3.0F, 0.0F, 2, 3, 1, 0.0F, false));

		tentlet2a4 = new ModelRenderer(this);
		tentlet2a4.setRotationPoint(-4.0F, 0.0F, -4.0F);
		tentacle1c8.addChild(tentlet2a4);
		setRotationAngle(tentlet2a4, 0.0F, -0.3054F, 0.0F);
		tentlet2a4.cubeList.add(new ModelBox(tentlet2a4, 0, 201, -2.0F, -4.0F, -1.0F, 2, 4, 1, 0.0F, false));

		tentmyc3a4 = new ModelRenderer(this);
		tentmyc3a4.setRotationPoint(0.0F, 0.0F, -0.5F);
		tentacle1c7.addChild(tentmyc3a4);
		tentmyc3a4.cubeList.add(new ModelBox(tentmyc3a4, 13, 190, -4.0F, 0.0F, -0.5F, 5, 2, 1, 0.0F, false));

		tentlet1a4 = new ModelRenderer(this);
		tentlet1a4.setRotationPoint(-4.0F, 0.0F, -1.0F);
		tentacle1b8.addChild(tentlet1a4);
		setRotationAngle(tentlet1a4, 0.0F, 0.2618F, 0.0F);
		tentlet1a4.cubeList.add(new ModelBox(tentlet1a4, 129, 195, -2.0F, -4.0F, 0.0F, 2, 4, 1, 0.0F, false));

		base_tentacle5 = new ModelRenderer(this);
		base_tentacle5.setRotationPoint(0.0F, 0.0F, 0.0F);
		base.addChild(base_tentacle5);
		setRotationAngle(base_tentacle5, 0.0F, 3.1416F, 0.0F);
		

		tentacle1a5 = new ModelRenderer(this);
		tentacle1a5.setRotationPoint(-5.0F, 0.0F, 0.0F);
		base_tentacle5.addChild(tentacle1a5);
		setRotationAngle(tentacle1a5, 0.0F, 0.0F, 0.3927F);
		tentacle1a5.cubeList.add(new ModelBox(tentacle1a5, 98, 88, -5.0F, -4.0F, -3.0F, 5, 4, 6, 0.0F, false));

		tentmyc5 = new ModelRenderer(this);
		tentmyc5.setRotationPoint(0.0F, 0.0F, 0.0F);
		tentacle1a5.addChild(tentmyc5);
		tentmyc5.cubeList.add(new ModelBox(tentmyc5, 51, 162, -5.0F, 0.0F, -1.5F, 5, 3, 3, 0.0F, false));

		tentacle1b9 = new ModelRenderer(this);
		tentacle1b9.setRotationPoint(-5.0F, 0.0F, 0.0F);
		tentacle1a5.addChild(tentacle1b9);
		setRotationAngle(tentacle1b9, 0.0F, 0.0F, 0.6109F);
		tentacle1b9.cubeList.add(new ModelBox(tentacle1b9, 120, 110, -3.0F, -4.0F, -3.001F, 3, 4, 6, 0.0F, false));

		tentmyc2a5 = new ModelRenderer(this);
		tentmyc2a5.setRotationPoint(0.0F, 0.0F, 0.0F);
		tentacle1b9.addChild(tentmyc2a5);
		tentmyc2a5.cubeList.add(new ModelBox(tentmyc2a5, 91, 184, -3.0F, 0.0F, -1.0F, 4, 2, 2, 0.0F, false));

		tentacle1b10 = new ModelRenderer(this);
		tentacle1b10.setRotationPoint(-3.0F, 0.0F, 3.0F);
		tentacle1b9.addChild(tentacle1b10);
		setRotationAngle(tentacle1b10, 0.0F, -0.0873F, 0.0F);
		tentacle1b10.cubeList.add(new ModelBox(tentacle1b10, 101, 110, -3.0F, -4.001F, -6.0F, 3, 4, 6, 0.0F, false));

		tentmy2b5 = new ModelRenderer(this);
		tentmy2b5.setRotationPoint(0.0F, 0.0F, -3.0F);
		tentacle1b10.addChild(tentmy2b5);
		tentmy2b5.cubeList.add(new ModelBox(tentmy2b5, 50, 190, -3.0F, 0.0F, -1.0F, 3, 2, 2, 0.0F, false));

		tentacle1c9 = new ModelRenderer(this);
		tentacle1c9.setRotationPoint(-3.0F, 0.0F, -3.0F);
		tentacle1b10.addChild(tentacle1c9);
		setRotationAngle(tentacle1c9, 0.0F, 0.0F, 0.5672F);
		tentacle1c9.cubeList.add(new ModelBox(tentacle1c9, 34, 143, -3.0F, -4.0F, -2.999F, 3, 4, 5, 0.0F, false));

		tentacle1c10 = new ModelRenderer(this);
		tentacle1c10.setRotationPoint(-3.0F, 0.0F, 2.0F);
		tentacle1c9.addChild(tentacle1c10);
		setRotationAngle(tentacle1c10, 0.0F, -0.0873F, 0.0F);
		tentacle1c10.cubeList.add(new ModelBox(tentacle1c10, 17, 143, -3.0F, -4.001F, -5.0F, 3, 4, 5, 0.0F, false));

		tentmyc3b5 = new ModelRenderer(this);
		tentmyc3b5.setRotationPoint(0.0F, 0.0F, -2.5F);
		tentacle1c10.addChild(tentmyc3b5);
		tentmyc3b5.cubeList.add(new ModelBox(tentmyc3b5, 120, 195, -3.0F, 0.0F, -0.5F, 3, 2, 1, 0.0F, false));

		tentacle1d9 = new ModelRenderer(this);
		tentacle1d9.setRotationPoint(-3.0F, 0.0F, -2.0F);
		tentacle1c10.addChild(tentacle1d9);
		setRotationAngle(tentacle1d9, 0.0F, 0.0F, 0.2182F);
		tentacle1d9.cubeList.add(new ModelBox(tentacle1d9, 147, 162, -3.0F, -3.0F, -2.001F, 3, 3, 4, 0.0F, false));

		tentmyc4a5 = new ModelRenderer(this);
		tentmyc4a5.setRotationPoint(0.0F, 0.0F, -0.25F);
		tentacle1d9.addChild(tentmyc4a5);
		tentmyc4a5.cubeList.add(new ModelBox(tentmyc4a5, 122, 201, -3.0F, 0.0F, 0.25F, 4, 2, 0, 0.0F, false));

		tentacle1d10 = new ModelRenderer(this);
		tentacle1d10.setRotationPoint(-3.0F, 0.0F, 2.0F);
		tentacle1d9.addChild(tentacle1d10);
		setRotationAngle(tentacle1d10, 0.0F, -0.1309F, 0.0F);
		tentacle1d10.cubeList.add(new ModelBox(tentacle1d10, 132, 162, -3.0F, -3.001F, -4.001F, 3, 3, 4, 0.0F, false));

		tentmyc4b5 = new ModelRenderer(this);
		tentmyc4b5.setRotationPoint(0.0F, 0.0F, -2.0F);
		tentacle1d10.addChild(tentmyc4b5);
		tentmyc4b5.cubeList.add(new ModelBox(tentmyc4b5, 48, 207, -3.0F, 0.0F, 0.0F, 3, 2, 0, 0.0F, false));

		tentacle1e9 = new ModelRenderer(this);
		tentacle1e9.setRotationPoint(-3.0F, 0.0F, -2.0F);
		tentacle1d10.addChild(tentacle1e9);
		setRotationAngle(tentacle1e9, 0.0F, 0.0F, 0.1745F);
		tentacle1e9.cubeList.add(new ModelBox(tentacle1e9, 106, 178, -3.0F, -2.0F, -2.0F, 3, 2, 3, 0.0F, false));

		tentacle1e10 = new ModelRenderer(this);
		tentacle1e10.setRotationPoint(-3.0F, 0.0F, 1.0F);
		tentacle1e9.addChild(tentacle1e10);
		setRotationAngle(tentacle1e10, 0.0F, -0.1309F, 0.0F);
		tentacle1e10.cubeList.add(new ModelBox(tentacle1e10, 93, 178, -3.0F, -2.001F, -3.0F, 3, 2, 3, 0.0F, false));

		tentlet3a5 = new ModelRenderer(this);
		tentlet3a5.setRotationPoint(-3.0F, 0.0F, -1.0F);
		tentacle1d10.addChild(tentlet3a5);
		setRotationAngle(tentlet3a5, 0.0F, 0.3491F, 0.0F);
		tentlet3a5.cubeList.add(new ModelBox(tentlet3a5, 115, 201, -2.0F, -3.0F, 0.0F, 2, 3, 1, 0.0F, false));

		tentlet2a5 = new ModelRenderer(this);
		tentlet2a5.setRotationPoint(-3.0F, 0.0F, -4.0F);
		tentacle1c10.addChild(tentlet2a5);
		setRotationAngle(tentlet2a5, 0.0F, -0.3054F, 0.0F);
		tentlet2a5.cubeList.add(new ModelBox(tentlet2a5, 113, 195, -2.0F, -4.0F, -1.0F, 2, 4, 1, 0.0F, false));

		tentmyc3a5 = new ModelRenderer(this);
		tentmyc3a5.setRotationPoint(0.0F, 0.0F, -0.5F);
		tentacle1c9.addChild(tentmyc3a5);
		tentmyc3a5.cubeList.add(new ModelBox(tentmyc3a5, 149, 190, -3.0F, 0.0F, -0.5F, 4, 2, 1, 0.0F, false));

		tentlet1a5 = new ModelRenderer(this);
		tentlet1a5.setRotationPoint(-3.0F, 0.0F, -1.0F);
		tentacle1b10.addChild(tentlet1a5);
		setRotationAngle(tentlet1a5, 0.0F, 0.2618F, 0.0F);
		tentlet1a5.cubeList.add(new ModelBox(tentlet1a5, 106, 195, -2.0F, -4.0F, 0.0F, 2, 4, 1, 0.0F, false));

		mycotissue5 = new ModelRenderer(this);
		mycotissue5.setRotationPoint(-2.0F, -4.0F, 0.0F);
		tentacle1b9.addChild(mycotissue5);
		setRotationAngle(mycotissue5, 0.0F, 0.0F, -0.2618F);
		

		mycotissue1_r5 = new ModelRenderer(this);
		mycotissue1_r5.setRotationPoint(0.0F, 0.0F, 0.0F);
		mycotissue5.addChild(mycotissue1_r5);
		setRotationAngle(mycotissue1_r5, 0.0F, 0.0F, -0.829F);
		mycotissue1_r5.cubeList.add(new ModelBox(mycotissue1_r5, 54, 75, 0.0F, 0.0F, -1.5F, 10, 6, 3, 0.0F, false));

		base_tentacle6 = new ModelRenderer(this);
		base_tentacle6.setRotationPoint(0.0F, 0.0F, 0.0F);
		base.addChild(base_tentacle6);
		setRotationAngle(base_tentacle6, 0.0F, 2.3562F, 0.0F);
		

		tentacle1a6 = new ModelRenderer(this);
		tentacle1a6.setRotationPoint(-5.0F, 0.0F, 0.0F);
		base_tentacle6.addChild(tentacle1a6);
		tentacle1a6.cubeList.add(new ModelBox(tentacle1a6, 0, 88, -6.0F, -4.0F, -3.0F, 6, 4, 6, 0.0F, false));

		tentmyc6 = new ModelRenderer(this);
		tentmyc6.setRotationPoint(0.0F, 0.0F, 0.0F);
		tentacle1a6.addChild(tentmyc6);
		tentmyc6.cubeList.add(new ModelBox(tentmyc6, 138, 143, -6.0F, 0.0F, -1.5F, 6, 3, 3, 0.0F, false));

		tentacle1b11 = new ModelRenderer(this);
		tentacle1b11.setRotationPoint(-6.0F, 0.0F, 0.0F);
		tentacle1a6.addChild(tentacle1b11);
		tentacle1b11.cubeList.add(new ModelBox(tentacle1b11, 113, 99, -4.0F, -4.0F, -3.001F, 4, 4, 6, 0.0F, false));

		mycotissue6 = new ModelRenderer(this);
		mycotissue6.setRotationPoint(-4.0F, -4.0F, 0.0F);
		tentacle1b11.addChild(mycotissue6);
		

		mycotissue1_r6 = new ModelRenderer(this);
		mycotissue1_r6.setRotationPoint(0.0F, 0.0F, 0.0F);
		mycotissue6.addChild(mycotissue1_r6);
		setRotationAngle(mycotissue1_r6, 0.0F, 0.0F, -0.829F);
		mycotissue1_r6.cubeList.add(new ModelBox(mycotissue1_r6, 85, 39, 0.0F, 0.0F, -1.5F, 12, 8, 3, 0.0F, false));

		tentmyc2a6 = new ModelRenderer(this);
		tentmyc2a6.setRotationPoint(0.0F, 0.0F, 0.0F);
		tentacle1b11.addChild(tentmyc2a6);
		tentmyc2a6.cubeList.add(new ModelBox(tentmyc2a6, 109, 170, -4.0F, 0.0F, -1.0F, 5, 2, 2, 0.0F, false));

		tentacle1b12 = new ModelRenderer(this);
		tentacle1b12.setRotationPoint(-4.0F, 0.0F, 3.0F);
		tentacle1b11.addChild(tentacle1b12);
		setRotationAngle(tentacle1b12, 0.0F, -0.0873F, 0.0F);
		tentacle1b12.cubeList.add(new ModelBox(tentacle1b12, 92, 99, -4.0F, -4.001F, -6.0F, 4, 4, 6, 0.0F, false));

		tentmy2b6 = new ModelRenderer(this);
		tentmy2b6.setRotationPoint(0.0F, 0.0F, -3.0F);
		tentacle1b12.addChild(tentmy2b6);
		tentmy2b6.cubeList.add(new ModelBox(tentmy2b6, 78, 184, -4.0F, 0.0F, -1.0F, 4, 2, 2, 0.0F, false));

		tentacle1c11 = new ModelRenderer(this);
		tentacle1c11.setRotationPoint(-4.0F, 0.0F, -3.0F);
		tentacle1b12.addChild(tentacle1c11);
		setRotationAngle(tentacle1c11, 0.0F, 0.0F, 0.6545F);
		tentacle1c11.cubeList.add(new ModelBox(tentacle1c11, 114, 121, -4.0F, -4.0F, -2.999F, 4, 4, 5, 0.0F, false));

		tentacle1c12 = new ModelRenderer(this);
		tentacle1c12.setRotationPoint(-4.0F, 0.0F, 2.0F);
		tentacle1c11.addChild(tentacle1c12);
		setRotationAngle(tentacle1c12, 0.0F, -0.1309F, 0.0F);
		tentacle1c12.cubeList.add(new ModelBox(tentacle1c12, 95, 121, -4.0F, -4.001F, -5.0F, 4, 4, 5, 0.0F, false));

		tentmyc3b6 = new ModelRenderer(this);
		tentmyc3b6.setRotationPoint(0.0F, 0.0F, -2.5F);
		tentacle1c12.addChild(tentmyc3b6);
		tentmyc3b6.cubeList.add(new ModelBox(tentmyc3b6, 138, 190, -4.0F, 0.0F, -0.5F, 4, 2, 1, 0.0F, false));

		tentacle1d11 = new ModelRenderer(this);
		tentacle1d11.setRotationPoint(-4.0F, 0.0F, -2.0F);
		tentacle1c12.addChild(tentacle1d11);
		setRotationAngle(tentacle1d11, 0.0F, 0.0F, 0.2182F);
		tentacle1d11.cubeList.add(new ModelBox(tentacle1d11, 106, 153, -4.0F, -3.0F, -2.001F, 4, 3, 4, 0.0F, false));

		tentmyc4a6 = new ModelRenderer(this);
		tentmyc4a6.setRotationPoint(0.0F, 0.0F, -0.25F);
		tentacle1d11.addChild(tentmyc4a6);
		tentmyc4a6.cubeList.add(new ModelBox(tentmyc4a6, 33, 198, -4.0F, 0.0F, 0.25F, 5, 2, 0, 0.0F, false));

		tentacle1d12 = new ModelRenderer(this);
		tentacle1d12.setRotationPoint(-4.0F, 0.0F, 2.0F);
		tentacle1d11.addChild(tentacle1d12);
		setRotationAngle(tentacle1d12, 0.0F, -0.1745F, 0.0F);
		tentacle1d12.cubeList.add(new ModelBox(tentacle1d12, 89, 153, -4.0F, -3.001F, -4.001F, 4, 3, 4, 0.0F, false));

		tentmyc4b6 = new ModelRenderer(this);
		tentmyc4b6.setRotationPoint(0.0F, 0.0F, -2.0F);
		tentacle1d12.addChild(tentmyc4b6);
		tentmyc4b6.cubeList.add(new ModelBox(tentmyc4b6, 106, 201, -4.0F, 0.0F, 0.0F, 4, 2, 0, 0.0F, false));

		tentacle1e11 = new ModelRenderer(this);
		tentacle1e11.setRotationPoint(-4.0F, 0.0F, -2.0F);
		tentacle1d12.addChild(tentacle1e11);
		setRotationAngle(tentacle1e11, 0.0F, 0.0F, 0.1745F);
		tentacle1e11.cubeList.add(new ModelBox(tentacle1e11, 80, 178, -3.0F, -2.0F, -2.0F, 3, 2, 3, 0.0F, false));

		tentacle1e12 = new ModelRenderer(this);
		tentacle1e12.setRotationPoint(-3.0F, 0.0F, 1.0F);
		tentacle1e11.addChild(tentacle1e12);
		setRotationAngle(tentacle1e12, 0.0F, -0.1745F, 0.0F);
		tentacle1e12.cubeList.add(new ModelBox(tentacle1e12, 67, 178, -3.0F, -2.001F, -3.0F, 3, 2, 3, 0.0F, false));

		tentlet3a6 = new ModelRenderer(this);
		tentlet3a6.setRotationPoint(-4.0F, 0.0F, -1.0F);
		tentacle1d12.addChild(tentlet3a6);
		setRotationAngle(tentlet3a6, 0.0F, 0.3491F, 0.0F);
		tentlet3a6.cubeList.add(new ModelBox(tentlet3a6, 99, 201, -2.0F, -3.0F, 0.0F, 2, 3, 1, 0.0F, false));

		tentlet2a6 = new ModelRenderer(this);
		tentlet2a6.setRotationPoint(-4.0F, 0.0F, -4.0F);
		tentacle1c12.addChild(tentlet2a6);
		setRotationAngle(tentlet2a6, 0.0F, -0.3054F, 0.0F);
		tentlet2a6.cubeList.add(new ModelBox(tentlet2a6, 99, 195, -2.0F, -4.0F, -1.0F, 2, 4, 1, 0.0F, false));

		tentmyc3a6 = new ModelRenderer(this);
		tentmyc3a6.setRotationPoint(0.0F, 0.0F, -0.5F);
		tentacle1c11.addChild(tentmyc3a6);
		tentmyc3a6.cubeList.add(new ModelBox(tentmyc3a6, 0, 190, -4.0F, 0.0F, -0.5F, 5, 2, 1, 0.0F, false));

		tentlet1a6 = new ModelRenderer(this);
		tentlet1a6.setRotationPoint(-4.0F, 0.0F, -1.0F);
		tentacle1b12.addChild(tentlet1a6);
		setRotationAngle(tentlet1a6, 0.0F, 0.2618F, 0.0F);
		tentlet1a6.cubeList.add(new ModelBox(tentlet1a6, 92, 195, -2.0F, -4.0F, 0.0F, 2, 4, 1, 0.0F, false));

		base_tentacle7 = new ModelRenderer(this);
		base_tentacle7.setRotationPoint(0.0F, 0.0F, 0.0F);
		base.addChild(base_tentacle7);
		setRotationAngle(base_tentacle7, 0.0F, 1.5708F, 0.0F);
		

		tentacle1a7 = new ModelRenderer(this);
		tentacle1a7.setRotationPoint(-5.0F, 0.0F, 0.0F);
		base_tentacle7.addChild(tentacle1a7);
		tentacle1a7.cubeList.add(new ModelBox(tentacle1a7, 75, 88, -5.0F, -4.0F, -3.0F, 5, 4, 6, 0.0F, false));

		tentmyc7 = new ModelRenderer(this);
		tentmyc7.setRotationPoint(0.0F, 0.0F, 0.0F);
		tentacle1a7.addChild(tentmyc7);
		tentmyc7.cubeList.add(new ModelBox(tentmyc7, 34, 162, -5.0F, 0.0F, -1.5F, 5, 3, 3, 0.0F, false));

		tentacle1b13 = new ModelRenderer(this);
		tentacle1b13.setRotationPoint(-5.0F, 0.0F, 0.0F);
		tentacle1a7.addChild(tentacle1b13);
		tentacle1b13.cubeList.add(new ModelBox(tentacle1b13, 82, 110, -3.0F, -4.0F, -3.001F, 3, 4, 6, 0.0F, false));

		tentmyc2a7 = new ModelRenderer(this);
		tentmyc2a7.setRotationPoint(0.0F, 0.0F, 0.0F);
		tentacle1b13.addChild(tentmyc2a7);
		tentmyc2a7.cubeList.add(new ModelBox(tentmyc2a7, 65, 184, -3.0F, 0.0F, -1.0F, 4, 2, 2, 0.0F, false));

		tentacle1b14 = new ModelRenderer(this);
		tentacle1b14.setRotationPoint(-3.0F, 0.0F, 3.0F);
		tentacle1b13.addChild(tentacle1b14);
		setRotationAngle(tentacle1b14, 0.0F, -0.0873F, 0.0F);
		tentacle1b14.cubeList.add(new ModelBox(tentacle1b14, 63, 110, -3.0F, -4.001F, -6.0F, 3, 4, 6, 0.0F, false));

		tentmy2b7 = new ModelRenderer(this);
		tentmy2b7.setRotationPoint(0.0F, 0.0F, -3.0F);
		tentacle1b14.addChild(tentmy2b7);
		tentmy2b7.cubeList.add(new ModelBox(tentmy2b7, 39, 190, -3.0F, 0.0F, -1.0F, 3, 2, 2, 0.0F, false));

		tentacle1c13 = new ModelRenderer(this);
		tentacle1c13.setRotationPoint(-3.0F, 0.0F, -3.0F);
		tentacle1b14.addChild(tentacle1c13);
		setRotationAngle(tentacle1c13, 0.0F, 0.0F, 0.5672F);
		tentacle1c13.cubeList.add(new ModelBox(tentacle1c13, 0, 143, -3.0F, -4.0F, -2.999F, 3, 4, 5, 0.0F, false));

		tentacle1c14 = new ModelRenderer(this);
		tentacle1c14.setRotationPoint(-3.0F, 0.0F, 2.0F);
		tentacle1c13.addChild(tentacle1c14);
		setRotationAngle(tentacle1c14, 0.0F, -0.0873F, 0.0F);
		tentacle1c14.cubeList.add(new ModelBox(tentacle1c14, 125, 132, -3.0F, -4.001F, -5.0F, 3, 4, 5, 0.0F, false));

		tentmyc3b7 = new ModelRenderer(this);
		tentmyc3b7.setRotationPoint(0.0F, 0.0F, -2.5F);
		tentacle1c14.addChild(tentmyc3b7);
		tentmyc3b7.cubeList.add(new ModelBox(tentmyc3b7, 83, 195, -3.0F, 0.0F, -0.5F, 3, 2, 1, 0.0F, false));

		tentacle1d13 = new ModelRenderer(this);
		tentacle1d13.setRotationPoint(-3.0F, 0.0F, -2.0F);
		tentacle1c14.addChild(tentacle1d13);
		setRotationAngle(tentacle1d13, 0.0F, 0.0F, 0.2182F);
		tentacle1d13.cubeList.add(new ModelBox(tentacle1d13, 117, 162, -3.0F, -3.0F, -2.001F, 3, 3, 4, 0.0F, false));

		tentmyc4a7 = new ModelRenderer(this);
		tentmyc4a7.setRotationPoint(0.0F, 0.0F, -0.25F);
		tentacle1d13.addChild(tentmyc4a7);
		tentmyc4a7.cubeList.add(new ModelBox(tentmyc4a7, 90, 201, -3.0F, 0.0F, 0.25F, 4, 2, 0, 0.0F, false));

		tentacle1d14 = new ModelRenderer(this);
		tentacle1d14.setRotationPoint(-3.0F, 0.0F, 2.0F);
		tentacle1d13.addChild(tentacle1d14);
		setRotationAngle(tentacle1d14, 0.0F, -0.1309F, 0.0F);
		tentacle1d14.cubeList.add(new ModelBox(tentacle1d14, 102, 162, -3.0F, -3.001F, -4.001F, 3, 3, 4, 0.0F, false));

		tentmyc4b7 = new ModelRenderer(this);
		tentmyc4b7.setRotationPoint(0.0F, 0.0F, -2.0F);
		tentacle1d14.addChild(tentmyc4b7);
		tentmyc4b7.cubeList.add(new ModelBox(tentmyc4b7, 41, 207, -3.0F, 0.0F, 0.0F, 3, 2, 0, 0.0F, false));

		tentacle1e13 = new ModelRenderer(this);
		tentacle1e13.setRotationPoint(-3.0F, 0.0F, -2.0F);
		tentacle1d14.addChild(tentacle1e13);
		setRotationAngle(tentacle1e13, 0.0F, 0.0F, 0.1745F);
		tentacle1e13.cubeList.add(new ModelBox(tentacle1e13, 54, 178, -3.0F, -2.0F, -2.0F, 3, 2, 3, 0.0F, false));

		tentacle1e14 = new ModelRenderer(this);
		tentacle1e14.setRotationPoint(-3.0F, 0.0F, 1.0F);
		tentacle1e13.addChild(tentacle1e14);
		setRotationAngle(tentacle1e14, 0.0F, -0.1309F, 0.0F);
		tentacle1e14.cubeList.add(new ModelBox(tentacle1e14, 41, 178, -3.0F, -2.001F, -3.0F, 3, 2, 3, 0.0F, false));

		tentlet3a7 = new ModelRenderer(this);
		tentlet3a7.setRotationPoint(-3.0F, 0.0F, -1.0F);
		tentacle1d14.addChild(tentlet3a7);
		setRotationAngle(tentlet3a7, 0.0F, 0.3491F, 0.0F);
		tentlet3a7.cubeList.add(new ModelBox(tentlet3a7, 83, 201, -2.0F, -3.0F, 0.0F, 2, 3, 1, 0.0F, false));

		tentlet2a7 = new ModelRenderer(this);
		tentlet2a7.setRotationPoint(-3.0F, 0.0F, -4.0F);
		tentacle1c14.addChild(tentlet2a7);
		setRotationAngle(tentlet2a7, 0.0F, -0.3054F, 0.0F);
		tentlet2a7.cubeList.add(new ModelBox(tentlet2a7, 76, 195, -2.0F, -4.0F, -1.0F, 2, 4, 1, 0.0F, false));

		tentmyc3a7 = new ModelRenderer(this);
		tentmyc3a7.setRotationPoint(0.0F, 0.0F, -0.5F);
		tentacle1c13.addChild(tentmyc3a7);
		tentmyc3a7.cubeList.add(new ModelBox(tentmyc3a7, 127, 190, -3.0F, 0.0F, -0.5F, 4, 2, 1, 0.0F, false));

		tentlet1a7 = new ModelRenderer(this);
		tentlet1a7.setRotationPoint(-3.0F, 0.0F, -1.0F);
		tentacle1b14.addChild(tentlet1a7);
		setRotationAngle(tentlet1a7, 0.0F, 0.2618F, 0.0F);
		tentlet1a7.cubeList.add(new ModelBox(tentlet1a7, 69, 195, -2.0F, -4.0F, 0.0F, 2, 4, 1, 0.0F, false));

		mycotissue7 = new ModelRenderer(this);
		mycotissue7.setRotationPoint(-2.0F, -4.0F, 0.0F);
		tentacle1b13.addChild(mycotissue7);
		setRotationAngle(mycotissue7, 0.0F, 0.0F, -0.2618F);
		

		mycotissue1_r7 = new ModelRenderer(this);
		mycotissue1_r7.setRotationPoint(0.0F, 0.0F, 0.0F);
		mycotissue7.addChild(mycotissue1_r7);
		setRotationAngle(mycotissue1_r7, 0.0F, 0.0F, -0.829F);
		mycotissue1_r7.cubeList.add(new ModelBox(mycotissue1_r7, 27, 75, 0.0F, 0.0F, -1.5F, 10, 6, 3, 0.0F, false));

		base_tentacle8 = new ModelRenderer(this);
		base_tentacle8.setRotationPoint(0.0F, 0.0F, 0.0F);
		base.addChild(base_tentacle8);
		setRotationAngle(base_tentacle8, 0.0F, 0.7854F, 0.0F);
		

		tentacle1a8 = new ModelRenderer(this);
		tentacle1a8.setRotationPoint(-5.0F, 0.0F, 0.0F);
		base_tentacle8.addChild(tentacle1a8);
		setRotationAngle(tentacle1a8, 0.0F, 0.0F, 0.3927F);
		tentacle1a8.cubeList.add(new ModelBox(tentacle1a8, 135, 75, -6.0F, -4.0F, -3.0F, 6, 4, 6, 0.0F, false));

		tentmyc8 = new ModelRenderer(this);
		tentmyc8.setRotationPoint(0.0F, 0.0F, 0.0F);
		tentacle1a8.addChild(tentmyc8);
		tentmyc8.cubeList.add(new ModelBox(tentmyc8, 119, 143, -6.0F, 0.0F, -1.5F, 6, 3, 3, 0.0F, false));

		tentacle1b15 = new ModelRenderer(this);
		tentacle1b15.setRotationPoint(-6.0F, 0.0F, 0.0F);
		tentacle1a8.addChild(tentacle1b15);
		setRotationAngle(tentacle1b15, 0.0F, 0.0F, 0.6981F);
		tentacle1b15.cubeList.add(new ModelBox(tentacle1b15, 71, 99, -4.0F, -4.0F, -3.001F, 4, 4, 6, 0.0F, false));

		mycotissue8 = new ModelRenderer(this);
		mycotissue8.setRotationPoint(-4.0F, -4.0F, 0.0F);
		tentacle1b15.addChild(mycotissue8);
		

		mycotissue1_r8 = new ModelRenderer(this);
		mycotissue1_r8.setRotationPoint(0.0F, 0.0F, 0.0F);
		mycotissue8.addChild(mycotissue1_r8);
		setRotationAngle(mycotissue1_r8, 0.0F, 0.0F, -0.829F);
		mycotissue1_r8.cubeList.add(new ModelBox(mycotissue1_r8, 54, 39, 0.0F, 0.0F, -1.5F, 12, 8, 3, 0.0F, false));

		tentmyc2a8 = new ModelRenderer(this);
		tentmyc2a8.setRotationPoint(0.0F, 0.0F, 0.0F);
		tentacle1b15.addChild(tentmyc2a8);
		tentmyc2a8.cubeList.add(new ModelBox(tentmyc2a8, 94, 170, -4.0F, 0.0F, -1.0F, 5, 2, 2, 0.0F, false));

		tentacle1b16 = new ModelRenderer(this);
		tentacle1b16.setRotationPoint(-4.0F, 0.0F, 3.0F);
		tentacle1b15.addChild(tentacle1b16);
		setRotationAngle(tentacle1b16, 0.0F, -0.0873F, 0.0F);
		tentacle1b16.cubeList.add(new ModelBox(tentacle1b16, 50, 99, -4.0F, -4.001F, -6.0F, 4, 4, 6, 0.0F, false));

		tentmy2b8 = new ModelRenderer(this);
		tentmy2b8.setRotationPoint(0.0F, 0.0F, -3.0F);
		tentacle1b16.addChild(tentmy2b8);
		tentmy2b8.cubeList.add(new ModelBox(tentmy2b8, 52, 184, -4.0F, 0.0F, -1.0F, 4, 2, 2, 0.0F, false));

		tentacle1c15 = new ModelRenderer(this);
		tentacle1c15.setRotationPoint(-4.0F, 0.0F, -3.0F);
		tentacle1b16.addChild(tentacle1c15);
		setRotationAngle(tentacle1c15, 0.0F, 0.0F, 0.6545F);
		tentacle1c15.cubeList.add(new ModelBox(tentacle1c15, 76, 121, -4.0F, -4.0F, -2.999F, 4, 4, 5, 0.0F, false));

		tentacle1c16 = new ModelRenderer(this);
		tentacle1c16.setRotationPoint(-4.0F, 0.0F, 2.0F);
		tentacle1c15.addChild(tentacle1c16);
		setRotationAngle(tentacle1c16, 0.0F, -0.1309F, 0.0F);
		tentacle1c16.cubeList.add(new ModelBox(tentacle1c16, 57, 121, -4.0F, -4.001F, -5.0F, 4, 4, 5, 0.0F, false));

		tentmyc3b8 = new ModelRenderer(this);
		tentmyc3b8.setRotationPoint(0.0F, 0.0F, -2.5F);
		tentacle1c16.addChild(tentmyc3b8);
		tentmyc3b8.cubeList.add(new ModelBox(tentmyc3b8, 116, 190, -4.0F, 0.0F, -0.5F, 4, 2, 1, 0.0F, false));

		tentacle1d15 = new ModelRenderer(this);
		tentacle1d15.setRotationPoint(-4.0F, 0.0F, -2.0F);
		tentacle1c16.addChild(tentacle1d15);
		setRotationAngle(tentacle1d15, 0.0F, 0.0F, 0.2182F);
		tentacle1d15.cubeList.add(new ModelBox(tentacle1d15, 72, 153, -4.0F, -3.0F, -2.001F, 4, 3, 4, 0.0F, false));

		tentmyc4a8 = new ModelRenderer(this);
		tentmyc4a8.setRotationPoint(0.0F, 0.0F, -0.25F);
		tentacle1d15.addChild(tentmyc4a8);
		tentmyc4a8.cubeList.add(new ModelBox(tentmyc4a8, 33, 195, -4.0F, 0.0F, 0.25F, 5, 2, 0, 0.0F, false));

		tentacle1d16 = new ModelRenderer(this);
		tentacle1d16.setRotationPoint(-4.0F, 0.0F, 2.0F);
		tentacle1d15.addChild(tentacle1d16);
		setRotationAngle(tentacle1d16, 0.0F, -0.1745F, 0.0F);
		tentacle1d16.cubeList.add(new ModelBox(tentacle1d16, 55, 153, -4.0F, -3.001F, -4.001F, 4, 3, 4, 0.0F, false));

		tentmyc4b8 = new ModelRenderer(this);
		tentmyc4b8.setRotationPoint(0.0F, 0.0F, -2.0F);
		tentacle1d16.addChild(tentmyc4b8);
		tentmyc4b8.cubeList.add(new ModelBox(tentmyc4b8, 74, 201, -4.0F, 0.0F, 0.0F, 4, 2, 0, 0.0F, false));

		tentacle1e15 = new ModelRenderer(this);
		tentacle1e15.setRotationPoint(-4.0F, 0.0F, -2.0F);
		tentacle1d16.addChild(tentacle1e15);
		setRotationAngle(tentacle1e15, 0.0F, 0.0F, 0.1745F);
		tentacle1e15.cubeList.add(new ModelBox(tentacle1e15, 28, 178, -3.0F, -2.0F, -2.0F, 3, 2, 3, 0.0F, false));

		tentacle1e16 = new ModelRenderer(this);
		tentacle1e16.setRotationPoint(-3.0F, 0.0F, 1.0F);
		tentacle1e15.addChild(tentacle1e16);
		setRotationAngle(tentacle1e16, 0.0F, -0.1745F, 0.0F);
		tentacle1e16.cubeList.add(new ModelBox(tentacle1e16, 15, 178, -3.0F, -2.001F, -3.0F, 3, 2, 3, 0.0F, false));

		tentlet3a8 = new ModelRenderer(this);
		tentlet3a8.setRotationPoint(-4.0F, 0.0F, -1.0F);
		tentacle1d16.addChild(tentlet3a8);
		setRotationAngle(tentlet3a8, 0.0F, 0.3491F, 0.0F);
		tentlet3a8.cubeList.add(new ModelBox(tentlet3a8, 67, 201, -2.0F, -3.0F, 0.0F, 2, 3, 1, 0.0F, false));

		tentlet2a8 = new ModelRenderer(this);
		tentlet2a8.setRotationPoint(-4.0F, 0.0F, -4.0F);
		tentacle1c16.addChild(tentlet2a8);
		setRotationAngle(tentlet2a8, 0.0F, -0.3054F, 0.0F);
		tentlet2a8.cubeList.add(new ModelBox(tentlet2a8, 62, 195, -2.0F, -4.0F, -1.0F, 2, 4, 1, 0.0F, false));

		tentmyc3a8 = new ModelRenderer(this);
		tentmyc3a8.setRotationPoint(0.0F, 0.0F, -0.5F);
		tentacle1c15.addChild(tentmyc3a8);
		tentmyc3a8.cubeList.add(new ModelBox(tentmyc3a8, 156, 184, -4.0F, 0.0F, -0.5F, 5, 2, 1, 0.0F, false));

		tentlet1a8 = new ModelRenderer(this);
		tentlet1a8.setRotationPoint(-4.0F, 0.0F, -1.0F);
		tentacle1b16.addChild(tentlet1a8);
		setRotationAngle(tentlet1a8, 0.0F, 0.2618F, 0.0F);
		tentlet1a8.cubeList.add(new ModelBox(tentlet1a8, 55, 195, -2.0F, -4.0F, 0.0F, 2, 4, 1, 0.0F, false));

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
			GlStateManager.translate(0F, 0F - interAnimationTicks_1 * 0.0625F, 0F);
			GlStateManager.rotate(0F + interAnimationTicks_1 * 22.5F, 0F, 1F, 0F);
			base.render(0.0625F);
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			spitty_part.render(0.0625F);
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
			GlStateManager.translate(0F, 0F - interAnimationTicks_1 * 0.0625F - rise, 0F);
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
		float flap = MathHelper.sin((smoothedTicks) * 0.325F) * 0.125F;
		float flap2 = MathHelper.cos((smoothedTicks) * 0.325F) * 0.125F;

		if (bigPuffsroom.active_1) {
			flap = 0;
			flap2 = 0;
		}
		
		if (bigPuffsroom.getSlam()) {
			if ((bigPuffsroom.active_4 || bigPuffsroom.active_3) && bigPuffsroom.pause) {
				flap = interAnimationTicks_4 * 0.05F;
				flap2 = -interAnimationTicks_4 * 0.05F;
			}
		}

		/*	back_c_tent_1.rotateAngleX = -0.7853981633974483F + interAnimationTicks_2 / (180F / (float) Math.PI) * 11.25F;
			front_c_tent_1.rotateAngleX = -0.7853981633974483F + interAnimationTicks_2 / (180F / (float) Math.PI) * 11.25F;
			left_c_tent_1.rotateAngleX = -0.7853981633974483F + interAnimationTicks_2 / (180F / (float) Math.PI) * 11.25F;
			right_c_tent_1.rotateAngleX = -0.7853981633974483F + interAnimationTicks_2 / (180F / (float) Math.PI) * 11.25F;
			
			back_c_tent_2.rotateAngleX = 0F + interAnimationTicks_2 / (180F / (float) Math.PI);
			front_c_tent_2.rotateAngleX = 0F + interAnimationTicks_2 / (180F / (float) Math.PI);
			left_c_tent_2.rotateAngleX = 0F + interAnimationTicks_2 / (180F / (float) Math.PI);
			right_c_tent_2.rotateAngleX = 0F + interAnimationTicks_2 / (180F / (float) Math.PI);

			back_c_tent_3.rotateAngleX = 0F + interAnimationTicks_2 / (180F / (float) Math.PI);
			front_c_tent_3.rotateAngleX = 0F + interAnimationTicks_2 / (180F / (float) Math.PI);
			left_c_tent_3.rotateAngleX = 0F + interAnimationTicks_2 / (180F / (float) Math.PI);
			right_c_tent_3.rotateAngleX = 0F + interAnimationTicks_2 / (180F / (float) Math.PI);
		
		
		base_tentacle1.rotateAngleZ = 1F - interAnimationTicks_3 / (180F / (float) Math.PI) * 11.25F + flap;
		base_tentacle2.rotateAngleZ = 1F - interAnimationTicks_3 / (180F / (float) Math.PI) * 11.25F - flap2;
		base_tentacle3.rotateAngleZ = 1F - interAnimationTicks_3 / (180F / (float) Math.PI) * 11.25F - flap2;
		base_tentacle4.rotateAngleZ = 1F - interAnimationTicks_3 / (180F / (float) Math.PI) * 11.25F + flap;
		base_tentacle5.rotateAngleZ = 1F - interAnimationTicks_3 / (180F / (float) Math.PI) * 11.25F + flap;
		base_tentacle6.rotateAngleZ = 2F - interAnimationTicks_3 / (180F / (float) Math.PI) * 11.25F - flap2;//
		base_tentacle7.rotateAngleZ = 2F - interAnimationTicks_3 / (180F / (float) Math.PI) * 11.25F - flap2;//
		base_tentacle8.rotateAngleZ = 1F - interAnimationTicks_3 / (180F / (float) Math.PI) * 11.25F + flap;
		

			
			back_r_tent_2.rotateAngleX = 0F - interAnimationTicks_3 / (180F / (float) Math.PI) * 1.40625F + (!bigPuffsroom.getSlam() ? -flap * 2F :  flap);
			front_r_tent_2.rotateAngleX = 0F - interAnimationTicks_3 / (180F / (float) Math.PI) * 1.40625F + (!bigPuffsroom.getSlam() ? flap2 * 2F : - flap2);
			back_l_tent_2.rotateAngleX = 0F - interAnimationTicks_3 / (180F / (float) Math.PI) * 1.40625F + (!bigPuffsroom.getSlam() ? flap2 * 2F :  -flap2);
			front_l_tent_2.rotateAngleX = 0F - interAnimationTicks_3 / (180F / (float) Math.PI) * 1.40625F + (!bigPuffsroom.getSlam() ? -flap * 2F :  flap);
			
			back_r_tent_3.rotateAngleX = -0.7853981633974483F + (interAnimationTicks_3 - interAnimationTicks_4 * 0.5F) / (180F / (float) Math.PI) * 11.25F + flap * (!bigPuffsroom.getSlam() ? 4F : 2F);
			front_r_tent_3.rotateAngleX = -0.7853981633974483F + (interAnimationTicks_3 - interAnimationTicks_4 * 0.5F) / (180F / (float) Math.PI) * 11.25F - flap2 * (!bigPuffsroom.getSlam() ? 4F :2F);
			back_l_tent_3.rotateAngleX = -0.7853981633974483F + (interAnimationTicks_3 - interAnimationTicks_4 * 0.5F) / (180F / (float) Math.PI) * 11.25F - flap2 * (!bigPuffsroom.getSlam() ? 4F : 2F);
			front_l_tent_3.rotateAngleX = -0.7853981633974483F + (interAnimationTicks_3 - interAnimationTicks_4 * 0.5F) / (180F / (float) Math.PI) * 11.25F + flap * (!bigPuffsroom.getSlam() ? 4F : 2F);
		*/
		}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}