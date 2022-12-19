package thebetweenlands.client.render.model.tile;// Made with Blockbench 4.2.3
// Exported for Minecraft version 1.7 - 1.12
// Paste this class into your mod and generate all required imports


import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelMothHouse extends ModelBase {
	private final ModelRenderer base;
	private final ModelRenderer screen;
	private final ModelRenderer screen_r1;
	private final ModelRenderer base_l;
	private final ModelRenderer base_r;
	private final ModelRenderer base_f;
	private final ModelRenderer mid_l;
	private final ModelRenderer mid_r;
	private final ModelRenderer mid_b;
	private final ModelRenderer roof_base;
	private final ModelRenderer silk1;
	private final ModelRenderer silk1_r1;
	private final ModelRenderer roof_f1;
	private final ModelRenderer roof_f2;
	private final ModelRenderer roof_b1;
	private final ModelRenderer roof_b2;

	public ModelMothHouse() {
		textureWidth = 128;
		textureHeight = 128;

		base = new ModelRenderer(this);
		base.setRotationPoint(0.0F, 24.0F, 0.0F);
		base.cubeList.add(new ModelBox(base, 0, 0, -6.0F, -2.0F, -4.0F, 12, 2, 8, 0.0F, false));

		screen = new ModelRenderer(this);
		screen.setRotationPoint(0.0F, -13.0F, -1.0F);
		base.addChild(screen);
		

		screen_r1 = new ModelRenderer(this);
		screen_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
		screen.addChild(screen_r1);
		setRotationAngle(screen_r1, 0.0436F, 0.0F, 0.0F);
		screen_r1.cubeList.add(new ModelBox(screen_r1, 15, 34, -4.0F, 0.0F, 0.0F, 8, 9, 0, 0.0F, false));

		base_l = new ModelRenderer(this);
		base_l.setRotationPoint(4.0F, -2.0F, -1.0F);
		base.addChild(base_l);
		base_l.cubeList.add(new ModelBox(base_l, 0, 51, 0.0F, -1.0F, -3.0F, 2, 1, 3, 0.0F, false));

		base_r = new ModelRenderer(this);
		base_r.setRotationPoint(-4.0F, -2.0F, -1.0F);
		base.addChild(base_r);
		base_r.cubeList.add(new ModelBox(base_r, 36, 34, -2.0F, -1.0F, -3.0F, 2, 1, 3, 0.0F, false));

		base_f = new ModelRenderer(this);
		base_f.setRotationPoint(0.0F, -2.0F, -4.0F);
		base.addChild(base_f);
		base_f.cubeList.add(new ModelBox(base_f, 15, 44, -4.0F, -1.0F, 0.0F, 8, 1, 2, 0.0F, false));

		mid_l = new ModelRenderer(this);
		mid_l.setRotationPoint(5.0F, -2.0F, -1.0F);
		base.addChild(mid_l);
		mid_l.cubeList.add(new ModelBox(mid_l, 0, 34, -1.0F, -11.0F, 0.0F, 2, 11, 5, 0.0F, false));

		mid_r = new ModelRenderer(this);
		mid_r.setRotationPoint(-5.0F, -2.0F, -1.0F);
		base.addChild(mid_r);
		mid_r.cubeList.add(new ModelBox(mid_r, 56, 17, -1.0F, -11.0F, 0.0F, 2, 11, 5, 0.0F, false));

		mid_b = new ModelRenderer(this);
		mid_b.setRotationPoint(0.0F, -2.0F, 4.0F);
		base.addChild(mid_b);
		mid_b.cubeList.add(new ModelBox(mid_b, 33, 17, -4.0F, -11.0F, -3.0F, 8, 11, 3, 0.0F, false));

		roof_base = new ModelRenderer(this);
		roof_base.setRotationPoint(0.0F, -11.0F, -3.0F);
		mid_b.addChild(roof_base);
		roof_base.cubeList.add(new ModelBox(roof_base, 41, 0, -6.0F, -3.0F, -3.0F, 12, 3, 6, 0.0F, false));

		silk1 = new ModelRenderer(this);
		silk1.setRotationPoint(0.0F, 0.0F, -1.0F);
		roof_base.addChild(silk1);
		setRotationAngle(silk1, 0.0229F, 0.1289F, 0.176F);
		

		silk1_r1 = new ModelRenderer(this);
		silk1_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
		silk1.addChild(silk1_r1);
		setRotationAngle(silk1_r1, -0.0437F, -0.0436F, 0.0019F);
		silk1_r1.cubeList.add(new ModelBox(silk1_r1, 56, 34, -4.0F, -1.0F, 0.0F, 9, 12, 0, 0.0F, false));

		roof_f1 = new ModelRenderer(this);
		roof_f1.setRotationPoint(0.0F, -4.0F, 0.0F);
		roof_base.addChild(roof_f1);
		setRotationAngle(roof_f1, 0.3927F, 0.0F, 0.0F);
		roof_f1.cubeList.add(new ModelBox(roof_f1, 0, 17, -6.5F, 0.0F, -3.0F, 13, 2, 3, 0.0F, false));

		roof_f2 = new ModelRenderer(this);
		roof_f2.setRotationPoint(0.0F, 0.0F, -3.0F);
		roof_f1.addChild(roof_f2);
		setRotationAngle(roof_f2, 0.3927F, 0.0F, 0.0F);
		roof_f2.cubeList.add(new ModelBox(roof_f2, 66, 11, -6.5F, 0.0F, -3.0F, 13, 2, 3, 0.001F, false));

		roof_b1 = new ModelRenderer(this);
		roof_b1.setRotationPoint(0.0F, -4.0F, 0.0F);
		roof_base.addChild(roof_b1);
		setRotationAngle(roof_b1, -0.3927F, 0.0F, 0.0F);
		roof_b1.cubeList.add(new ModelBox(roof_b1, 33, 11, -6.495F, 0.0F, 0.0F, 13, 2, 3, 0.0F, false));

		roof_b2 = new ModelRenderer(this);
		roof_b2.setRotationPoint(0.0F, 0.0F, 3.0F);
		roof_b1.addChild(roof_b2);
		setRotationAngle(roof_b2, -0.3927F, 0.0F, 0.0F);
		roof_b2.cubeList.add(new ModelBox(roof_b2, 0, 11, -6.495F, 0.0F, 0.0F, 13, 2, 3, 0.001F, false));
	}

	public void render() {
		base.render(0.0625F);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}