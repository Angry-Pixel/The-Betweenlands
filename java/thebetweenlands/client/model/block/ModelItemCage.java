package thebetweenlands.client.model.block;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelItemCage extends ModelBase {

	ModelRenderer plate;
	ModelRenderer boss;
	ModelRenderer linkLeft;
	ModelRenderer linkRight;
	ModelRenderer cageBoss;
	ModelRenderer top1;
	ModelRenderer top2;
	ModelRenderer base;
	ModelRenderer barFrontLeft;
	ModelRenderer barFrontMidLeft;
	ModelRenderer barFrontMidRight;
	ModelRenderer barFrontRight;
	ModelRenderer barLeftMidFront;
	ModelRenderer barLeftMidBack;
	ModelRenderer barRightMidFront;
	ModelRenderer barRightMidBack;
	ModelRenderer barBackRight;
	ModelRenderer barLBack;
	ModelRenderer barBackMidLeft;
	ModelRenderer barBackMidRight;

	public ModelItemCage() {
		textureWidth = 64;
		textureHeight = 64;

		plate = new ModelRenderer(this, 22, 0);
		plate.addBox(-2.5F, -8F, -2.5F, 5, 1, 5);
		plate.setRotationPoint(0F, 16F, 0F);
		setRotation(plate, 0F, 0F, 0F);
		boss = new ModelRenderer(this, 26, 7);
		boss.addBox(-1.5F, -7F, -1.5F, 3, 1, 3);
		boss.setRotationPoint(0F, 16F, 0F);
		setRotation(boss, 0F, 0F, 0F);
		linkLeft = new ModelRenderer(this, 21, 7);
		linkLeft.addBox(1.5F, -6.5F, -0.5F, 1, 2, 1);
		linkLeft.setRotationPoint(0F, 16F, 0F);
		setRotation(linkLeft, 0F, 0F, 0F);
		linkRight = new ModelRenderer(this, 39, 7);
		linkRight.addBox(-2.5F, -6.5F, -0.5F, 1, 2, 1);
		linkRight.setRotationPoint(0F, 16F, 0F);
		setRotation(linkRight, 0F, 0F, 0F);
		cageBoss = new ModelRenderer(this, 26, 12);
		cageBoss.addBox(-1.5F, -5F, -1.5F, 3, 1, 3);
		cageBoss.setRotationPoint(0F, 16F, 0F);
		setRotation(cageBoss, 0F, 0F, 0F);
		top1 = new ModelRenderer(this, 12, 17);
		top1.addBox(-5F, -4F, -5F, 10, 1, 10);
		top1.setRotationPoint(0F, 16F, 0F);
		setRotation(top1, 0F, 0F, 0F);
		top2 = new ModelRenderer(this, 4, 29);
		top2.addBox(-7F, -3F, -7F, 14, 1, 14);
		top2.setRotationPoint(0F, 16F, 0F);
		setRotation(top2, 0F, 0F, 0F);
		base = new ModelRenderer(this, 4, 45);
		base.addBox(-7F, 7F, -7F, 14, 1, 14);
		base.setRotationPoint(0F, 16F, 0F);
		setRotation(base, 0F, 0F, 0F);
		barFrontLeft = new ModelRenderer(this, 0, 0);
		barFrontLeft.addBox(5F, -2F, -6F, 1, 9, 1);
		barFrontLeft.setRotationPoint(0F, 16F, 0F);
		setRotation(barFrontLeft, 0F, 0F, 0F);
		barFrontMidLeft = new ModelRenderer(this, 0, 0);
		barFrontMidLeft.addBox(1.25F, -2F, -6F, 1, 9, 1);
		barFrontMidLeft.setRotationPoint(0F, 16F, 0F);
		setRotation(barFrontMidLeft, 0F, 0F, 0F);
		barFrontMidRight = new ModelRenderer(this, 0, 0);
		barFrontMidRight.addBox(-2.25F, -2F, -6F, 1, 9, 1);
		barFrontMidRight.setRotationPoint(0F, 16F, 0F);
		setRotation(barFrontMidRight, 0F, 0F, 0F);
		barFrontRight = new ModelRenderer(this, 0, 0);
		barFrontRight.addBox(-6F, -2F, -6F, 1, 9, 1);
		barFrontRight.setRotationPoint(0F, 16F, 0F);
		setRotation(barFrontRight, 0F, 0F, 0F);
		barLeftMidFront = new ModelRenderer(this, 0, 0);
		barLeftMidFront.addBox(5F, -2F, -2.25F, 1, 9, 1);
		barLeftMidFront.setRotationPoint(0F, 16F, 0F);
		setRotation(barLeftMidFront, 0F, 0F, 0F);
		barLeftMidBack = new ModelRenderer(this, 0, 0);
		barLeftMidBack.addBox(5F, -2F, 1.25F, 1, 9, 1);
		barLeftMidBack.setRotationPoint(0F, 16F, 0F);
		setRotation(barLeftMidBack, 0F, 0F, 0F);
		barRightMidFront = new ModelRenderer(this, 0, 0);
		barRightMidFront.addBox(-6F, -2F, -2.25F, 1, 9, 1);
		barRightMidFront.setRotationPoint(0F, 16F, 0F);
		setRotation(barRightMidFront, 0F, 0F, 0F);
		barRightMidBack = new ModelRenderer(this, 0, 0);
		barRightMidBack.addBox(-6F, -2F, 1.25F, 1, 9, 1);
		barRightMidBack.setRotationPoint(0F, 16F, 0F);
		setRotation(barRightMidBack, 0F, 0F, 0F);
		barBackRight = new ModelRenderer(this, 0, 0);
		barBackRight.addBox(-6F, -2F, 5F, 1, 9, 1);
		barBackRight.setRotationPoint(0F, 16F, 0F);
		setRotation(barBackRight, 0F, 0F, 0F);
		barLBack = new ModelRenderer(this, 0, 0);
		barLBack.addBox(5F, -2F, 5F, 1, 9, 1);
		barLBack.setRotationPoint(0F, 16F, 0F);
		setRotation(barLBack, 0F, 0F, 0F);
		barBackMidLeft = new ModelRenderer(this, 0, 0);
		barBackMidLeft.addBox(1.25F, -2F, 5F, 1, 9, 1);
		barBackMidLeft.setRotationPoint(0F, 16F, 0F);
		setRotation(barBackMidLeft, 0F, 0F, 0F);
		barBackMidRight = new ModelRenderer(this, 0, 0);
		barBackMidRight.addBox(-2.25F, -2F, 5F, 1, 9, 1);
		barBackMidRight.setRotationPoint(0F, 16F, 0F);
		setRotation(barBackMidRight, 0F, 0F, 0F);
	}

	public void renderSolid() {
		plate.render(0.0625F);
		boss.render(0.0625F);
		linkLeft.render(0.0625F);
		linkRight.render(0.0625F);
		cageBoss.render(0.0625F);
		top1.render(0.0625F);
		top2.render(0.0625F);
		base.render(0.0625F);
	}

	public void renderBars() {
		barFrontLeft.render(0.0625F);
		barFrontMidLeft.render(0.0625F);
		barFrontMidRight.render(0.0625F);
		barFrontRight.render(0.0625F);
		barLeftMidFront.render(0.0625F);
		barLeftMidBack.render(0.0625F);
		barRightMidFront.render(0.0625F);
		barRightMidBack.render(0.0625F);
		barBackRight.render(0.0625F);
		barLBack.render(0.0625F);
		barBackMidLeft.render(0.0625F);
		barBackMidRight.render(0.0625F);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

}
