package thebetweenlands.client.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelFloatingFlamme extends ModelBase {
	ModelRenderer head;
	ModelRenderer jaw;
	ModelRenderer jawThings;

	public ModelFloatingFlamme() {
		textureWidth = 64;
		textureHeight = 64;

		head = new ModelRenderer(this, 0, 0);
		head.addBox(-4F, -14F, -4F, 8, 14, 8);
		head.setRotationPoint(0F, 10F, 0F);
		setRotation(head, -0.1F, 0F, 0F);
		jaw = new ModelRenderer(this, 0, 50);
		jaw.addBox(-4.5F, 4F, -8F, 9, 3, 8);
		jaw.setRotationPoint(0F, 5F, 3.5F);
		setRotation(jaw, 0.1F, 0F, 0F);
		jawThings = new ModelRenderer(this, 0, 28);
		jawThings.addBox(-4.5F, 0F, -8F, 9, 4, 8);
		jawThings.setRotationPoint(0F, 5F, 3.5F);
		setRotation(jawThings, 0.1F, 0F, 0F);
	}

	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel) {
		head.render(unitPixel);
		jaw.render(unitPixel);
		jawThings.render(unitPixel);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

}
