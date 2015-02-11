package thebetweenlands.entities.mobs.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import thebetweenlands.entities.mobs.EntityDarkDruid;

public class ModelDarkDruid extends ModelBase {
	//fields
	ModelRenderer beard;
	ModelRenderer rightarm;
	ModelRenderer rightcuff;
	ModelRenderer Body;
	ModelRenderer leftarm;
	ModelRenderer leftcuff;
	ModelRenderer head;
	ModelRenderer Hood_LT;
	ModelRenderer Hood_L;
	ModelRenderer Hood_RT;
	ModelRenderer Hood_R;
	ModelRenderer Liripipe_1;
	ModelRenderer Liripipe_2;
	ModelRenderer Liripipe_3;
	ModelRenderer Robe1;
	ModelRenderer Robe2;
	ModelRenderer Robe3;
	ModelRenderer Robe4;
	ModelRenderer Robe5;
	ModelRenderer Robe6;
	ModelRenderer Robe7;

	public ModelDarkDruid() {
		textureWidth = 64;
		textureHeight = 256;

		beard = new ModelRenderer(this, 22, 104);
		beard.addBox(-1.5F, -1F, -4.5F, 3, 2, 1);
		beard.setRotationPoint(0F, 0F, 0F);
		beard.setTextureSize(64, 256);
		beard.mirror = true;
		setRotation(beard, 0F, 0F, 0F);
		rightarm = new ModelRenderer(this, 0, 92);
		rightarm.addBox(-5F, -2F, -2F, 5, 12, 5);
		rightarm.setRotationPoint(-3F, 2F, 0F);
		rightarm.setTextureSize(64, 256);
		rightarm.mirror = true;
		setRotation(rightarm, 0F, 0F, 0F);
		rightcuff = new ModelRenderer(this, 0, 84);
		rightcuff.addBox(-5.5F, 10F, -2.5F, 6, 2, 6);
		rightcuff.setRotationPoint(-3F, 2F, 0F);
		rightcuff.setTextureSize(64, 256);
		rightcuff.mirror = true;
		setRotation(rightcuff, 0F, 0F, 0F);
		Body = new ModelRenderer(this, 32, 107);
		Body.addBox(-4F, 0F, -3F, 8, 13, 8);
		Body.setRotationPoint(0F, 0F, 0F);
		Body.setTextureSize(64, 256);
		Body.mirror = true;
		setRotation(Body, 0F, 0F, 0F);
		leftarm = new ModelRenderer(this, 0, 92);
		leftarm.addBox(0F, -2F, -2F, 5, 12, 5);
		leftarm.setRotationPoint(3F, 2F, 0F);
		leftarm.setTextureSize(64, 256);
		leftarm.mirror = true;
		setRotation(leftarm, 0F, 0F, 0F);
		leftcuff = new ModelRenderer(this, 0, 84);
		leftcuff.addBox(-0.5F, 10F, -2.5F, 6, 2, 6);
		leftcuff.setRotationPoint(3F, 2F, 0F);
		leftcuff.setTextureSize(64, 256);
		leftcuff.mirror = true;
		setRotation(leftcuff, 0F, 0F, 0F);
		head = new ModelRenderer(this, 30, 90);
		head.addBox(-4F, -8F, -4F, 8, 8, 9);
		head.setRotationPoint(0F, 0F, 0F);
		head.setTextureSize(64, 256);
		head.mirror = true;
		setRotation(head, 0F, 0F, 0F);
		Hood_LT = new ModelRenderer(this, 0, 54);
		Hood_LT.addBox(-4.5F, -15F, -4.5F, 4, 1, 9);
		Hood_LT.setRotationPoint(0F, 6F, 0F);
		Hood_LT.setTextureSize(64, 256);
		Hood_LT.mirror = true;
		setRotation(Hood_LT, 0F, 0F, 0.296706F);
		Hood_L = new ModelRenderer(this, 0, 64);
		Hood_L.addBox(4F, -8F, -4.5F, 1, 11, 9);
		Hood_L.setRotationPoint(0F, 0F, 0F);
		Hood_L.setTextureSize(64, 256);
		Hood_L.mirror = true;
		setRotation(Hood_L, 0F, 0F, -0.122173F);
		Hood_RT = new ModelRenderer(this, 0, 54);
		Hood_RT.addBox(0.5F, -15F, -4.5F, 4, 1, 9);
		Hood_RT.setRotationPoint(0F, 6F, 0F);
		Hood_RT.setTextureSize(64, 256);
		Hood_RT.mirror = true;
		setRotation(Hood_RT, 0F, 0F, -0.296706F);
		Hood_R = new ModelRenderer(this, 0, 64);
		Hood_R.addBox(-5F, -8F, -4.5F, 1, 11, 9);
		Hood_R.setRotationPoint(0F, 0F, 0F);
		Hood_R.setTextureSize(64, 256);
		Hood_R.mirror = true;
		setRotation(Hood_R, 0F, 0F, 0.122173F);
		Liripipe_1 = new ModelRenderer(this, 58, 76);
		Liripipe_1.addBox(-1F, -0.5F, -0.5F, 2, 6, 1);
		Liripipe_1.setRotationPoint(0F, 12F, 6F);
		Liripipe_1.setTextureSize(64, 256);
		Liripipe_1.mirror = true;
		setRotation(Liripipe_1, 0F, 0F, 0F);
		Liripipe_2 = new ModelRenderer(this, 54, 63);
		Liripipe_2.addBox(-2F, -0.5F, -0.5F, 4, 12, 1);
		Liripipe_2.setRotationPoint(0F, 0F, 6F);
		Liripipe_2.setTextureSize(64, 256);
		Liripipe_2.mirror = true;
		setRotation(Liripipe_2, 0F, 0F, 0F);
		Liripipe_3 = new ModelRenderer(this, 50, 54);
		Liripipe_3.addBox(-3F, -0.5F, -0.5F, 6, 8, 1);
		Liripipe_3.setRotationPoint(0F, -8F, 4.5F);
		Liripipe_3.setTextureSize(64, 256);
		Liripipe_3.mirror = true;
		setRotation(Liripipe_3, 0.1745329F, 0F, 0F);
		Robe1 = new ModelRenderer(this, 0, 0);
		Robe1.addBox(-4F, 0F, -3F, 8, 5, 8);
		Robe1.setRotationPoint(0F, 13F, 0F);
		Robe1.setTextureSize(64, 256);
		Robe1.mirror = true;
		setRotation(Robe1, 0F, 0F, 0F);
		Robe2 = new ModelRenderer(this, 0, 13);
		Robe2.addBox(-4.5F, 0F, -3.5F, 9, 2, 10);
		Robe2.setRotationPoint(0F, 18F, 0F);
		Robe2.setTextureSize(64, 256);
		Robe2.mirror = true;
		setRotation(Robe2, 0F, 0F, 0F);
		Robe3 = new ModelRenderer(this, 0, 35);
		Robe3.addBox(-5F, 0F, -4F, 10, 1, 15);
		Robe3.setRotationPoint(0F, 20F, 0F);
		Robe3.setTextureSize(64, 256);
		Robe3.mirror = true;
		setRotation(Robe3, 0F, 0F, 0F);
		Robe4 = new ModelRenderer(this, 0, 132);
		Robe4.addBox(-5.5F, 0F, -4.5F, 11, 1, 18);
		Robe4.setRotationPoint(0F, 21F, 0F);
		Robe4.setTextureSize(64, 256);
		Robe4.mirror = true;
		setRotation(Robe4, 0F, 0F, 0F);
		Robe5 = new ModelRenderer(this, 0, 13);
		Robe5.addBox(-3F, 1F, 6.5F, 6, 1, 2);
		Robe5.setRotationPoint(0F, 18F, 0F);
		Robe5.setTextureSize(64, 256);
		Robe5.mirror = true;
		setRotation(Robe5, 0F, 0F, 0F);
		Robe6 = new ModelRenderer(this, 0, 25);
		Robe6.addBox(-3F, 1F, 11F, 6, 1, 1);
		Robe6.setRotationPoint(0F, 19F, 0F);
		Robe6.setTextureSize(64, 256);
		Robe6.mirror = true;
		setRotation(Robe6, 0F, 0F, 0F);
		Robe7 = new ModelRenderer(this, 0, 28);
		Robe7.addBox(-4F, 0F, 13.5F, 8, 1, 2);
		Robe7.setRotationPoint(0F, 21F, 0F);
		Robe7.setTextureSize(64, 256);
		Robe7.mirror = true;
		setRotation(Robe7, 0F, 0F, 0F);
	}

	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		beard.render(f5);
		rightarm.render(f5);
		rightcuff.render(f5);
		Body.render(f5);
		leftarm.render(f5);
		leftcuff.render(f5);
		head.render(f5);
		Hood_LT.render(f5);
		Hood_L.render(f5);
		Hood_RT.render(f5);
		Hood_R.render(f5);
		Liripipe_1.render(f5);
		Liripipe_2.render(f5);
		Liripipe_3.render(f5);
		Robe1.render(f5);
		Robe2.render(f5);
		Robe3.render(f5);
		Robe4.render(f5);
		Robe5.render(f5);
		Robe6.render(f5);
		Robe7.render(f5);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity e) {
		EntityDarkDruid entity = (EntityDarkDruid) e;
		if (entity.getDataWatcher().getWatchableObjectInt(EntityDarkDruid.dataWatcher_isCasting) == 1) {
			this.rightarm.rotateAngleZ = 0.0F;
			this.rightarm.rotateAngleY = 0.0F;
			this.rightarm.rotateAngleX = -((float)Math.PI / 2F);

			this.rightcuff.rotateAngleX = this.rightarm.rotateAngleX;
			this.rightcuff.rotateAngleY = this.rightarm.rotateAngleY;
			this.rightcuff.rotateAngleZ = this.rightarm.rotateAngleZ;

			this.leftarm.rotateAngleZ = 0.0F;
			this.leftarm.rotateAngleY = 0.0F;
			this.leftarm.rotateAngleX = -((float)Math.PI / 2F);

			this.leftcuff.rotateAngleX = this.leftarm.rotateAngleX;
			this.leftcuff.rotateAngleY = this.leftarm.rotateAngleY;
			this.leftcuff.rotateAngleZ = this.leftarm.rotateAngleZ;
		} else {
			this.rightarm.rotateAngleX = MathHelper.cos(par1 * 0.6662F + (float)Math.PI) * 2.0F * par2 * 0.5F;
			this.rightcuff.rotateAngleX = this.rightarm.rotateAngleX;

			this.leftarm.rotateAngleX = MathHelper.cos(par1 * 0.6662F) * 2.0F * par2 * 0.5F;
			this.leftcuff.rotateAngleX = this.leftarm.rotateAngleX;
		}
	}
}