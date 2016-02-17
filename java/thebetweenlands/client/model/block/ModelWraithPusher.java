package thebetweenlands.client.model.block;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

import org.lwjgl.opengl.GL11;

import thebetweenlands.tileentities.TileEntityWraithPusher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelWraithPusher extends ModelBase {
	ModelRenderer block;
	ModelRenderer hoodLT;
	ModelRenderer hoodRT;
	ModelRenderer hoodL;
	ModelRenderer hoodR;
	ModelRenderer head;
	ModelRenderer body;
	ModelRenderer leftArm;
	ModelRenderer leftCuff;
	ModelRenderer leftPalm;
	ModelRenderer leftOutFinger;
	ModelRenderer leftInFinger;
	ModelRenderer leftThumb;
	ModelRenderer rightArm;
	ModelRenderer rightCuff;
	ModelRenderer rightPalm;
	ModelRenderer rightOutFinger;
	ModelRenderer rightInFinger;
	ModelRenderer rightThumb;

	public ModelWraithPusher() {
		textureWidth = 64;
		textureHeight = 128;

		block = new ModelRenderer(this, 0, 0);
		block.addBox(-8F, -8F, -8F, 16, 16, 16);
		block.setRotationPoint(0F, 16F, 0F);
		setRotation(block, 0F, 0F, 0F);
		hoodLT = new ModelRenderer(this, 39, 33);
		hoodLT.addBox(-1.8F, -5.8F, -3.5F, 3, 1, 5);
		hoodLT.setRotationPoint(0F, 16F, 0F);
		setRotation(hoodLT, 0F, 0F, 0.296706F);
		hoodRT = new ModelRenderer(this, 9, 33);
		hoodRT.addBox(-1.2F, -5.8F, -3.5F, 3, 1, 5);
		hoodRT.setRotationPoint(0F, 16F, 0F);
		setRotation(hoodRT, 0F, 0F, -0.296706F);
		hoodL = new ModelRenderer(this, 41, 40);
		hoodL.addBox(2F, -5F, -3.5F, 1, 5, 5);
		hoodL.setRotationPoint(0F, 16F, 0F);
		setRotation(hoodL, 0F, 0F, -0.122173F);
		hoodR = new ModelRenderer(this, 11, 40);
		hoodR.addBox(-3F, -5F, -3.5F, 1, 5, 5);
		hoodR.setRotationPoint(0F, 16F, 0F);
		setRotation(hoodR, 0F, 0F, 0.122173F);
		head = new ModelRenderer(this, 24, 42);
		head.addBox(-2F, -5F, -2.5F, 4, 4, 4);
		head.setRotationPoint(0F, 16F, 0F);
		setRotation(head, 0F, 0F, 0F);
		body = new ModelRenderer(this, 19, 51);
		body.addBox(-3.5F, -1.5F, -2.5F, 7, 10, 6);
		body.setRotationPoint(0F, 16F, 0F);
		setRotation(body, 0.4363323F, 0F, 0F);
		leftArm = new ModelRenderer(this, 46, 51);
		leftArm.addBox(0.5F, -2F, -4F, 3, 4, 6);
		leftArm.setRotationPoint(3F, 15F, 0F);
		setRotation(leftArm, 0F, 0F, 0F);
		leftCuff = new ModelRenderer(this, 50, 62);
		leftCuff.addBox(0F, -2F, -5F, 4, 5, 1);
		leftCuff.setRotationPoint(3F, 15F, 0F);
		setRotation(leftCuff, 0F, 0F, 0F);
		leftPalm = new ModelRenderer(this, 51, 69);
		leftPalm.addBox(0.5F, -1F, -6F, 3, 3, 1);
		leftPalm.setRotationPoint(3F, 15F, 0F);
		setRotation(leftPalm, 0F, 0F, 0F);
		leftOutFinger = new ModelRenderer(this, 56, 74);
		leftOutFinger.addBox(2.5F, 0F, -8F, 1, 1, 3);
		leftOutFinger.setRotationPoint(3F, 15F, 0F);
		setRotation(leftOutFinger, -0.3490659F, 0F, 0F);
		leftInFinger = new ModelRenderer(this, 47, 74);
		leftInFinger.addBox(0.5F, 0F, -8F, 1, 1, 3);
		leftInFinger.setRotationPoint(3F, 15F, 0F);
		setRotation(leftInFinger, -0.3490659F, 0F, 0F);
		leftThumb = new ModelRenderer(this, 52, 79);
		leftThumb.addBox(1.966667F, 1F, -7F, 1, 1, 2);
		leftThumb.setRotationPoint(3F, 15F, 0F);
		setRotation(leftThumb, 0F, 0.3490659F, 0F);
		rightArm = new ModelRenderer(this, 0, 51);
		rightArm.addBox(-3.5F, -2F, -4F, 3, 4, 6);
		rightArm.setRotationPoint(-3F, 15F, 0F);
		setRotation(rightArm, 0F, 0F, 0F);
		rightCuff = new ModelRenderer(this, 4, 62);
		rightCuff.addBox(-4F, -2F, -5F, 4, 5, 1);
		rightCuff.setRotationPoint(-3F, 15F, 0F);
		setRotation(rightCuff, 0F, 0F, 0F);
		rightPalm = new ModelRenderer(this, 5, 69);
		rightPalm.addBox(-3.5F, -1F, -6F, 3, 3, 1);
		rightPalm.setRotationPoint(-3F, 15F, 0F);
		setRotation(rightPalm, 0F, 0F, 0F);
		rightOutFinger = new ModelRenderer(this, 0, 74);
		rightOutFinger.addBox(-3.5F, 0F, -8F, 1, 1, 3);
		rightOutFinger.setRotationPoint(-3F, 15F, 0F);
		setRotation(rightOutFinger, -0.3490659F, 0F, 0F);
		rightInFinger = new ModelRenderer(this, 9, 74);
		rightInFinger.addBox(-1.5F, 0F, -8F, 1, 1, 3);
		rightInFinger.setRotationPoint(-3F, 15F, 0F);
		setRotation(rightInFinger, -0.3490659F, 0F, 0F);
		rightThumb = new ModelRenderer(this, 5, 79);
		rightThumb.addBox(-3F, 1F, -7F, 1, 1, 2);
		rightThumb.setRotationPoint(-3F, 15F, 0F);
		setRotation(rightThumb, 0F, -0.3490659F, 0F);
	}

	public void render(TileEntityWraithPusher tile) {
		block.render(0.0625F);
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		if (tile.animationTicks <= 8)
			GL11.glTranslatef(0F, 0F, 0F - 1F / 8 * tile.animationTicks);
		if (tile.animationTicks > 8)
			GL11.glTranslatef(0F, 0F, 0F - 1F);
		if (tile.animationTicks > 1) {
			hoodLT.render(0.0625F);
			hoodRT.render(0.0625F);
			hoodL.render(0.0625F);
			hoodR.render(0.0625F);
			head.render(0.0625F);
			body.render(0.0625F);

			 GL11.glPushMatrix();
			 if(tile.animationTicks > 8)
				 GL11.glRotatef(0F - tile.animationTicks * 0.75F, 0, 1, 0);
			 else
				 GL11.glRotatef(-0, 0, 1, 0);
			 rightArm.render(0.0625F);
				rightCuff.render(0.0625F);
				rightPalm.render(0.0625F);
				rightOutFinger.render(0.0625F);
				rightInFinger.render(0.0625F);
				rightThumb.render(0.0625F);
			 GL11.glPopMatrix();

			 GL11.glPushMatrix();
			 if(tile.animationTicks > 8)
				 GL11.glRotatef(0F + tile.animationTicks * 0.75F, 0, 1, 0);
			 else
				 GL11.glRotatef(0, 0, 1, 0);
			 leftArm.render(0.0625F);
				leftCuff.render(0.0625F);
				leftPalm.render(0.0625F);
				leftOutFinger.render(0.0625F);
				leftInFinger.render(0.0625F);
				leftThumb.render(0.0625F);
			 GL11.glPopMatrix();

		}
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

}
