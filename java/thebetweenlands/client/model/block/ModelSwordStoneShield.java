package thebetweenlands.client.model.block;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelSwordStoneShield extends ModelBase {

	ModelRenderer back;
	ModelRenderer right;
	ModelRenderer left;
	ModelRenderer front;

	public ModelSwordStoneShield() {
		textureWidth = 64;
		textureHeight = 32;

		back = new ModelRenderer(this, 0, 0);
		back.addBox(-8F, -8F, 8F, 16, 16, 0, 0.9F);
		back.setRotationPoint(0F, 16F, 0F);
		setRotation(back, 0F, 0F, 0F);
		right = new ModelRenderer(this, 0, 0);
		right.addBox(-8F, -8F, -8F, 0, 16, 16, 0.9F);
		right.setRotationPoint(0F, 16F, 0F);
		setRotation(right, 0F, 0F, 0F);
		left = new ModelRenderer(this, 0, 0);
		left.addBox(8F, -8F, -8F, 0, 16, 16, 0.9F);
		left.setRotationPoint(0F, 16F, 0F);
		setRotation(left, 0F, 0F, 0F);
		front = new ModelRenderer(this, 0, 0);
		front.addBox(-8F, -8F, -8F, 16, 16, 0, 0.9F);
		front.setRotationPoint(0F, 16F, 0F);
		setRotation(front, 0F, 0F, 0F);
	}

	public void render() {
		GL11.glPushMatrix();
		GL11.glScalef(0.9F, 0.9F, 0.9F);
		back.render(0.0625F);
		right.render(0.0625F);
		left.render(0.0625F);
		front.render(0.0625F);
		GL11.glPopMatrix();
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
}
