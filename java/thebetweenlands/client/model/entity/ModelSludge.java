package thebetweenlands.client.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelSludge extends ModelBase {
	ModelRenderer head1;
	ModelRenderer head2;
	ModelRenderer jaw;
	ModelRenderer teeth;
	ModelRenderer spine;
	ModelRenderer spinepiece;
	ModelRenderer slime1;
	ModelRenderer slime2;
	ModelRenderer slime3;

	public ModelSludge() {
		textureWidth = 128;
		textureHeight = 64;

		head1 = new ModelRenderer(this, 0, 0);
		head1.addBox(-4F, -6F, -8F, 8, 6, 8);
		head1.setRotationPoint(0F, 15F, 3F);
		setRotation(head1, -0.0743572F, 0F, -0.1115358F);
		head2 = new ModelRenderer(this, 0, 16);
		head2.addBox(-3F, 0F, -3F, 6, 2, 3);
		head2.setRotationPoint(0F, 15F, 3F);
		setRotation(head2, -0.074351F, 0F, -0.111544F);
		jaw = new ModelRenderer(this, 0, 22);
		jaw.addBox(-4F, -1F, -8F, 8, 2, 7);
		jaw.setRotationPoint(0F, 15F, 3F);
		setRotation(jaw, 0.5205068F, 0F, 0.0371705F);
		teeth = new ModelRenderer(this, 0, 32);
		teeth.addBox(-4F, 0F, -8F, 8, 1, 5);
		teeth.setRotationPoint(0F, 15F, 4F);
		setRotation(teeth, -0.0743572F, 0F, -0.1115358F);
		spine = new ModelRenderer(this, 0, 39);
		spine.addBox(-1F, 0F, -2F, 2, 3, 2);
		spine.setRotationPoint(0F, 15F, 3F);
		setRotation(spine, 0.2602503F, 0F, 0F);
		spinepiece = new ModelRenderer(this, 0, 45);
		spinepiece.addBox(-1F, 0F, -1F, 2, 1, 2);
		spinepiece.setRotationPoint(0F, 19F, 3F);
		setRotation(spinepiece, 0F, 0F, 0.2230717F);
		slime1 = new ModelRenderer(this, 40, 0);
		slime1.addBox(-9F, -7F, -9F, 18, 14, 18);
		slime1.setRotationPoint(0F, 15F, 0F);
		setRotation(slime1, 0F, 0F, 0F);
		slime2 = new ModelRenderer(this, 40, 32);
		slime2.addBox(-7F, -9F, -7F, 14, 2, 14);
		slime2.setRotationPoint(0F, 15F, 0F);
		setRotation(slime2, 0F, 0F, 0F);
		slime3 = new ModelRenderer(this, 40, 48);
		slime3.addBox(-7F, 7F, -7F, 14, 2, 14);
		slime3.setRotationPoint(0F, 15F, 0F);
		setRotation(slime3, 0F, 0F, 0F);
	}

	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel) {
		super.render(entity, limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel);
		setRotationAngles(limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel, entity);
		head1.render(unitPixel);
		head2.render(unitPixel);
		jaw.render(unitPixel);
		teeth.render(unitPixel);
		spine.render(unitPixel);
		spinepiece.render(unitPixel);
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		slime1.render(unitPixel);
		slime2.render(unitPixel);
		slime3.render(unitPixel);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel, Entity entity) {
		super.setRotationAngles(limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel, entity);
	}

}
