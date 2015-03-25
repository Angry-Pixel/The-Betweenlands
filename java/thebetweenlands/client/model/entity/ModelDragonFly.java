package thebetweenlands.client.model.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import thebetweenlands.entities.mobs.EntityDragonFly;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelDragonFly extends ModelBase {
	ModelRenderer torso;
	ModelRenderer back;
	ModelRenderer tail1;
	ModelRenderer tail2;
	ModelRenderer tail3;
	ModelRenderer tail4;
	ModelRenderer stinger;
	ModelRenderer head1;
	ModelRenderer head2;
	ModelRenderer eyeleft;
	ModelRenderer eyeright;
	ModelRenderer jawleft;
	ModelRenderer jawright;
	ModelRenderer antennaleft;
	ModelRenderer antennaright;
	ModelRenderer wingright1;
	ModelRenderer wingleft1;
	ModelRenderer wingright2;
	ModelRenderer wingleft2;
	ModelRenderer leftfront1;
	ModelRenderer rightfront1;
	ModelRenderer leftmid1;
	ModelRenderer rightmid1;
	ModelRenderer leftback1;
	ModelRenderer rightback1;
	ModelRenderer leftfront2;
	ModelRenderer rightfront2;
	ModelRenderer leftmid2;
	ModelRenderer rightmid2;
	ModelRenderer leftback2;
	ModelRenderer rightback2;

	public ModelDragonFly() {
		textureWidth = 128;
		textureHeight = 64;

		torso = new ModelRenderer(this, 0, 0);
		torso.addBox(-2F, -2F, 0F, 4, 4, 6);
		torso.setRotationPoint(0F, 18F, -9F);
		setRotation(torso, -0.0743572F, 0F, 0F);
		back = new ModelRenderer(this, 0, 11);
		back.addBox(-1.5F, -1.5F, 0F, 3, 3, 2);
		back.setRotationPoint(0F, 18.2F, -3F);
		setRotation(back, -0.0371786F, 0F, 0F);
		tail1 = new ModelRenderer(this, 0, 17);
		tail1.addBox(-1F, -1F, 0F, 2, 2, 5);
		tail1.setRotationPoint(0F, 18F, -1F);
		setRotation(tail1, 0F, 0F, 0F);
		tail2 = new ModelRenderer(this, 0, 25);
		tail2.addBox(-1F, -1F, 0F, 2, 2, 4);
		tail2.setRotationPoint(0F, 18F, 4F);
		setRotation(tail2, 0F, 0F, 0F);
		tail3 = new ModelRenderer(this, 0, 32);
		tail3.addBox(-1F, -1F, 0F, 2, 2, 4);
		tail3.setRotationPoint(0F, 18F, 8F);
		setRotation(tail3, 0F, 0F, 0F);
		tail4 = new ModelRenderer(this, 0, 39);
		tail4.addBox(-1F, -1F, 0F, 2, 2, 4);
		tail4.setRotationPoint(0F, 18F, 12F);
		setRotation(tail4, 0F, 0F, 0F);
		stinger = new ModelRenderer(this, 0, 46);
		stinger.addBox(0F, 0F, 0F, 0, 2, 3);
		stinger.setRotationPoint(0F, 17F, 16F);
		setRotation(stinger, -0.4363323F, 0F, 0F);
		head1 = new ModelRenderer(this, 30, 0);
		head1.addBox(-2F, -2F, -3F, 4, 4, 3);
		head1.setRotationPoint(0F, 18F, -9F);
		setRotation(head1, 0.3717861F, 0F, 0F);
		head2 = new ModelRenderer(this, 30, 8);
		head2.addBox(-1.5F, -2.9F, -4F, 3, 3, 2);
		head2.setRotationPoint(0F, 18F, -9F);
		setRotation(head2, 0.7435722F, 0F, 0F);
		eyeleft = new ModelRenderer(this, 30, 14);
		eyeleft.addBox(2.5F, -0.5F, -2.5F, 1, 2, 2);
		eyeleft.setRotationPoint(0F, 18F, -9F);
		setRotation(eyeleft, 0.0743611F, 0.3892394F, -0.1858931F);
		eyeright = new ModelRenderer(this, 37, 14);
		eyeright.addBox(2.5F, -0.5F, 0.5F, 1, 2, 2);
		eyeright.setRotationPoint(0F, 18F, -9F);
		setRotation(eyeright, -0.0743533F, 2.769803F, -0.185895F);
		jawleft = new ModelRenderer(this, 30, 19);
		jawleft.addBox(0F, 0F, -4.5F, 1, 2, 1);
		jawleft.setRotationPoint(0F, 18F, -9F);
		setRotation(jawleft, 0.2230717F, 0F, -0.2230717F);
		jawright = new ModelRenderer(this, 35, 19);
		jawright.addBox(-1F, 0F, -4.5F, 1, 2, 1);
		jawright.setRotationPoint(0F, 18F, -9F);
		setRotation(jawright, 0.2230705F, 0F, 0.2230705F);
		antennaleft = new ModelRenderer(this, 30, 23);
		antennaleft.addBox(0F, -4F, -2F, 1, 2, 0);
		antennaleft.setRotationPoint(0F, 18F, -9F);
		setRotation(antennaleft, 0.4089647F, 0F, 0.5205006F);
		antennaright = new ModelRenderer(this, 33, 23);
		antennaright.addBox(-1F, -4F, -2F, 1, 2, 0);
		antennaright.setRotationPoint(0F, 18F, -9F);
		setRotation(antennaright, 0.4089647F, 0F, -0.5204921F);
		wingright1 = new ModelRenderer(this, 50, 0);
		wingright1.addBox(0F, -16F, 0F, 0, 16, 4);
		wingright1.setRotationPoint(-1F, 16.2F, -8F);
		setRotation(wingright1, 0F, -0.1745329F, -1.570796F);
		wingleft1 = new ModelRenderer(this, 59, 0);
		wingleft1.addBox(0F, -16F, 0F, 0, 16, 4);
		wingleft1.setRotationPoint(1F, 16.2F, -8F);
		setRotation(wingleft1, 0F, 0.1745329F, 1.570796F);
		wingright2 = new ModelRenderer(this, 50, 21);
		wingright2.addBox(0F, -12F, 0F, 0, 12, 4);
		wingright2.setRotationPoint(-1F, 16.4F, -6F);
		setRotation(wingright2, 0F, 0.1745329F, -1.570796F);
		wingleft2 = new ModelRenderer(this, 59, 21);
		wingleft2.addBox(0F, -12F, 0F, 0, 12, 4);
		wingleft2.setRotationPoint(1F, 16.4F, -6F);
		setRotation(wingleft2, 0F, -0.1745329F, 1.570796F);
		leftfront1 = new ModelRenderer(this, 50, 38);
		leftfront1.addBox(0F, -0.5F, -0.5F, 5, 1, 1);
		leftfront1.setRotationPoint(1F, 20F, -7F);
		setRotation(leftfront1, 0F, 0.4461433F, 0.1487144F);
		rightfront1 = new ModelRenderer(this, 63, 38);
		rightfront1.addBox(0F, -0.5F, -0.5F, 5, 1, 1);
		rightfront1.setRotationPoint(-1F, 20F, -7F);
		setRotation(rightfront1, 0F, 2.695445F, 0.1487144F);
		leftmid1 = new ModelRenderer(this, 50, 41);
		leftmid1.addBox(0F, -0.5F, -0.5F, 5, 1, 1);
		leftmid1.setRotationPoint(1F, 20F, -6F);
		setRotation(leftmid1, 0F, -0.2230717F, 0.0371786F);
		rightmid1 = new ModelRenderer(this, 63, 41);
		rightmid1.addBox(0F, -0.5F, -0.5F, 5, 1, 1);
		rightmid1.setRotationPoint(-1F, 20F, -6F);
		setRotation(rightmid1, 0F, -2.918522F, 0.0371786F);
		leftback1 = new ModelRenderer(this, 50, 44);
		leftback1.addBox(0F, -0.5F, -0.5F, 6, 1, 1);
		leftback1.setRotationPoint(1F, 20F, -5F);
		setRotation(leftback1, 0F, -0.8922867F, 0.1858931F);
		rightback1 = new ModelRenderer(this, 65, 44);
		rightback1.addBox(0F, -0.5F, -0.5F, 6, 1, 1);
		rightback1.setRotationPoint(-1F, 20F, -5F);
		setRotation(rightback1, 0F, -2.24931F, 0.1858931F);
		leftfront2 = new ModelRenderer(this, 50, 47);
		leftfront2.addBox(4F, 1.2F, -0.5F, 1, 4, 1);
		leftfront2.setRotationPoint(1F, 20F, -7F);
		setRotation(leftfront2, 0F, 0.4461411F, -0.1858931F);
		rightfront2 = new ModelRenderer(this, 55, 47);
		rightfront2.addBox(4F, 1.2F, -0.5F, 1, 4, 1);
		rightfront2.setRotationPoint(-1F, 20F, -7F);
		setRotation(rightfront2, 0F, 2.695451F, -0.1858931F);
		leftmid2 = new ModelRenderer(this, 50, 53);
		leftmid2.addBox(4F, 1.5F, -0.5F, 1, 4, 1);
		leftmid2.setRotationPoint(1F, 20F, -6F);
		setRotation(leftmid2, 0F, -0.2230705F, -0.3346075F);
		rightmid2 = new ModelRenderer(this, 55, 53);
		rightmid2.addBox(4F, 1.5F, -0.5F, 1, 4, 1);
		rightmid2.setRotationPoint(-1F, 20F, -6F);
		setRotation(rightmid2, 0F, -2.918522F, -0.3346075F);
		leftback2 = new ModelRenderer(this, 50, 59);
		leftback2.addBox(3.5F, 4.2F, -0.5F, 1, 4, 1);
		leftback2.setRotationPoint(1F, 20F, -5F);
		setRotation(leftback2, 0F, -0.8922821F, -0.6320364F);
		rightback2 = new ModelRenderer(this, 55, 59);
		rightback2.addBox(3.5F, 4.2F, -0.5F, 1, 4, 1);
		rightback2.setRotationPoint(-1F, 20F, -5F);
		setRotation(rightback2, 0F, -2.24931F, -0.6320364F);
	}

	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel) {
		super.render(entity, limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel);
		setRotationAngles(limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel, entity);
		torso.render(unitPixel);
		back.render(unitPixel);
		tail1.render(unitPixel);
		tail2.render(unitPixel);
		tail3.render(unitPixel);
		tail4.render(unitPixel);
		stinger.render(unitPixel);
		head1.render(unitPixel);
		head2.render(unitPixel);
		eyeleft.render(unitPixel);
		eyeright.render(unitPixel);
		jawleft.render(unitPixel);
		jawright.render(unitPixel);
		antennaleft.render(unitPixel);
		antennaright.render(unitPixel);
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		wingright1.render(unitPixel);
		wingleft1.render(unitPixel);
		wingright2.render(unitPixel);
		wingleft2.render(unitPixel);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
		leftfront1.render(unitPixel);
		rightfront1.render(unitPixel);
		leftmid1.render(unitPixel);
		rightmid1.render(unitPixel);
		leftback1.render(unitPixel);
		rightback1.render(unitPixel);
		leftfront2.render(unitPixel);
		rightfront2.render(unitPixel);
		leftmid2.render(unitPixel);
		rightmid2.render(unitPixel);
		leftback2.render(unitPixel);
		rightback2.render(unitPixel);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel, Entity entity) {
		//super.setRotationAngles(limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel, entity);
		EntityDragonFly dragonFly = (EntityDragonFly) entity;
		
		wingright1.rotateAngleX = 0.1745329F;
		wingleft1.rotateAngleX = 0.1745329F;
		wingright2.rotateAngleX = -0.1745329F;
		wingleft2.rotateAngleX = -0.1745329F;
		
		if (dragonFly.onGround) {
			wingright1.rotateAngleZ = -1.570796F;
			wingleft1.rotateAngleZ = 1.570796F;
			wingright2.rotateAngleZ = -1.570796F;
			wingleft2.rotateAngleZ = 1.570796F;
		}
		if (dragonFly.isFlying()) {
			wingright1.rotateAngleZ = -1.570796F - dragonFly.wingFloat;
			wingleft1.rotateAngleZ = 1.570796F + dragonFly.wingFloat;
			wingright2.rotateAngleZ = -1.570796F - dragonFly.wingFloat;
			wingleft2.rotateAngleZ = 1.570796F + dragonFly.wingFloat;
		}
	}

}
