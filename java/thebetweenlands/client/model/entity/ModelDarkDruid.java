package thebetweenlands.client.model.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import thebetweenlands.entities.mobs.EntityDarkDruid;

@SideOnly(Side.CLIENT)
public class ModelDarkDruid extends ModelBase {
	private final ModelRenderer beard;
	private final ModelRenderer rightarm;
	private final ModelRenderer rightcuff;
	private final ModelRenderer Body;
	private final ModelRenderer leftarm;
	private final ModelRenderer leftcuff;
	private final ModelRenderer head;
	private final ModelRenderer Hood_LT;
	private final ModelRenderer Hood_L;
	private final ModelRenderer Hood_RT;
	private final ModelRenderer Hood_R;
	private final ModelRenderer Liripipe_1;
	private final ModelRenderer Liripipe_2;
	private final ModelRenderer Liripipe_3;
	private final ModelRenderer Robe1;
	private final ModelRenderer Robe2;
	private final ModelRenderer Robe3;
	private final ModelRenderer Robe4;
	private final ModelRenderer Robe5;
	private final ModelRenderer Robe6;
	private final ModelRenderer Robe7;

	public ModelDarkDruid() {
		textureWidth = 64;
		textureHeight = 256;

		beard = new ModelRenderer(this, 22, 104);
		beard.addBox(-1.5F, -1F, -4.5F, 3, 2, 1);
		beard.setRotationPoint(0F, 0F, 0F);
		setRotation(beard, 0F, 0F, 0F);
		rightarm = new ModelRenderer(this, 0, 92);
		rightarm.addBox(-5F, -2F, -2F, 5, 12, 5);
		rightarm.setRotationPoint(-3F, 2F, 0F);
		setRotation(rightarm, 0F, 0F, 0F);
		rightcuff = new ModelRenderer(this, 0, 84);
		rightcuff.addBox(-5.5F, 10F, -2.5F, 6, 2, 6);
		rightcuff.setRotationPoint(-3F, 2F, 0F);
		setRotation(rightcuff, 0F, 0F, 0F);
		Body = new ModelRenderer(this, 32, 107);
		Body.addBox(-4F, 0F, -3F, 8, 13, 8);
		Body.setRotationPoint(0F, 0F, 0F);
		setRotation(Body, 0F, 0F, 0F);
		leftarm = new ModelRenderer(this, 0, 92);
		leftarm.addBox(0F, -2F, -2F, 5, 12, 5);
		leftarm.setRotationPoint(3F, 2F, 0F);
		setRotation(leftarm, 0F, 0F, 0F);
		leftcuff = new ModelRenderer(this, 0, 84);
		leftcuff.addBox(-0.5F, 10F, -2.5F, 6, 2, 6);
		leftcuff.setRotationPoint(3F, 2F, 0F);
		setRotation(leftcuff, 0F, 0F, 0F);
		head = new ModelRenderer(this, 30, 90);
		head.addBox(-4F, -8F, -4F, 8, 8, 9);
		head.setRotationPoint(0F, 0F, 0F);
		setRotation(head, 0F, 0F, 0F);
		Hood_LT = new ModelRenderer(this, 0, 54);
		Hood_LT.addBox(-4.5F, -15F, -4.5F, 4, 1, 9);
		Hood_LT.setRotationPoint(0F, 6F, 0F);
		setRotation(Hood_LT, 0F, 0F, 0.296706F);
		Hood_L = new ModelRenderer(this, 0, 64);
		Hood_L.addBox(4F, -8F, -4.5F, 1, 11, 9);
		Hood_L.setRotationPoint(0F, 0F, 0F);
		setRotation(Hood_L, 0F, 0F, -0.122173F);
		Hood_RT = new ModelRenderer(this, 0, 54);
		Hood_RT.addBox(0.5F, -15F, -4.5F, 4, 1, 9);
		Hood_RT.setRotationPoint(0F, 6F, 0F);
		setRotation(Hood_RT, 0F, 0F, -0.296706F);
		Hood_R = new ModelRenderer(this, 0, 64);
		Hood_R.addBox(-5F, -8F, -4.5F, 1, 11, 9);
		Hood_R.setRotationPoint(0F, 0F, 0F);
		setRotation(Hood_R, 0F, 0F, 0.122173F);
		Liripipe_1 = new ModelRenderer(this, 58, 76);
		Liripipe_1.addBox(-1F, -0.5F, -0.5F, 2, 6, 1);
		Liripipe_1.setRotationPoint(0F, 12F, 6F);
		setRotation(Liripipe_1, 0F, 0F, 0F);
		Liripipe_2 = new ModelRenderer(this, 54, 63);
		Liripipe_2.addBox(-2F, -0.5F, -0.5F, 4, 12, 1);
		Liripipe_2.setRotationPoint(0F, 0F, 6F);
		setRotation(Liripipe_2, 0F, 0F, 0F);
		Liripipe_3 = new ModelRenderer(this, 50, 54);
		Liripipe_3.addBox(-3F, -0.5F, -0.5F, 6, 8, 1);
		Liripipe_3.setRotationPoint(0F, -8F, 4.5F);
		setRotation(Liripipe_3, 0.1745329F, 0F, 0F);
		Robe1 = new ModelRenderer(this, 0, 0);
		Robe1.addBox(-4F, 0F, -3F, 8, 5, 8);
		Robe1.setRotationPoint(0F, 13F, 0F);
		setRotation(Robe1, 0F, 0F, 0F);
		Robe2 = new ModelRenderer(this, 0, 13);
		Robe2.addBox(-4.5F, 0F, -3.5F, 9, 2, 10);
		Robe2.setRotationPoint(0F, 18F, 0F);
		setRotation(Robe2, 0F, 0F, 0F);
		Robe3 = new ModelRenderer(this, 0, 35);
		Robe3.addBox(-5F, 0F, -4F, 10, 1, 15);
		Robe3.setRotationPoint(0F, 20F, 0F);
		setRotation(Robe3, 0F, 0F, 0F);
		Robe4 = new ModelRenderer(this, 0, 132);
		Robe4.addBox(-5.5F, 0F, -4.5F, 11, 1, 18);
		Robe4.setRotationPoint(0F, 21F, 0F);
		setRotation(Robe4, 0F, 0F, 0F);
		Robe5 = new ModelRenderer(this, 0, 13);
		Robe5.addBox(-3F, 1F, 6.5F, 6, 1, 2);
		Robe5.setRotationPoint(0F, 18F, 0F);
		setRotation(Robe5, 0F, 0F, 0F);
		Robe6 = new ModelRenderer(this, 0, 25);
		Robe6.addBox(-3F, 1F, 11F, 6, 1, 1);
		Robe6.setRotationPoint(0F, 19F, 0F);
		setRotation(Robe6, 0F, 0F, 0F);
		Robe7 = new ModelRenderer(this, 0, 28);
		Robe7.addBox(-4F, 0F, 13.5F, 8, 1, 2);
		Robe7.setRotationPoint(0F, 21F, 0F);
		setRotation(Robe7, 0F, 0F, 0F);
	}

	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel) {
		super.render(entity, limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel);
		setRotationAngles(limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel, entity);
		beard.render(unitPixel);
		rightarm.render(unitPixel);
		rightcuff.render(unitPixel);
		Body.render(unitPixel);
		leftarm.render(unitPixel);
		leftcuff.render(unitPixel);
		head.render(unitPixel);
		Hood_LT.render(unitPixel);
		Hood_L.render(unitPixel);
		Hood_RT.render(unitPixel);
		Hood_R.render(unitPixel);
		Liripipe_1.render(unitPixel);
		Liripipe_2.render(unitPixel);
		Liripipe_3.render(unitPixel);
		Robe1.render(unitPixel);
		Robe2.render(unitPixel);
		Robe3.render(unitPixel);
		Robe4.render(unitPixel);
		Robe5.render(unitPixel);
		Robe6.render(unitPixel);
		Robe7.render(unitPixel);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel, Entity entity) {
		super.setRotationAngles(limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel, entity);
		EntityDarkDruid druid = (EntityDarkDruid) entity;

		if (druid.getDataWatcher().getWatchableObjectByte(20) == 1) {
			rightarm.rotateAngleZ = 0.0F;
			rightarm.rotateAngleY = 0.0F;
			rightarm.rotateAngleX = -((float) Math.PI / 2F);

			rightcuff.rotateAngleX = rightarm.rotateAngleX;
			rightcuff.rotateAngleY = rightarm.rotateAngleY;
			rightcuff.rotateAngleZ = rightarm.rotateAngleZ;

			leftarm.rotateAngleZ = 0.0F;
			leftarm.rotateAngleY = 0.0F;
			leftarm.rotateAngleX = -((float) Math.PI / 2F);

			leftcuff.rotateAngleX = leftarm.rotateAngleX;
			leftcuff.rotateAngleY = leftarm.rotateAngleY;
			leftcuff.rotateAngleZ = leftarm.rotateAngleZ;
		} else {
			rightarm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 2.0F * limbSwingAngle * 0.5F;
			rightcuff.rotateAngleX = rightarm.rotateAngleX;

			leftarm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAngle * 0.5F;
			leftcuff.rotateAngleX = leftarm.rotateAngleX;
		}
	}
}
