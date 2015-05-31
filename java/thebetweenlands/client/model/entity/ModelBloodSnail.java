package thebetweenlands.client.model.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

@SideOnly(Side.CLIENT)
public class ModelBloodSnail extends ModelBase {
	ModelRenderer torso1;
	ModelRenderer torso2;
	ModelRenderer head;
	ModelRenderer sensor1;
	ModelRenderer sensor2;
	ModelRenderer sensor3;
	ModelRenderer sensor4;
	ModelRenderer shell1;
	ModelRenderer shell2;
	ModelRenderer shell3;
	ModelRenderer shell4;
	ModelRenderer shell5;

	public ModelBloodSnail() {
		textureWidth = 64;
		textureHeight = 32;

		torso1 = new ModelRenderer(this, 0, 0);
		torso1.addBox(-1.5F, 0F, -7F, 3, 2, 14);
		torso1.setRotationPoint(0F, 22F, 0F);
		setRotation(torso1, 0F, 0F, 0F);
		torso2 = new ModelRenderer(this, 0, 17);
		torso2.addBox(-2F, -1F, -5F, 4, 3, 9);
		torso2.setRotationPoint(0F, 22F, 0F);
		setRotation(torso2, 0F, 0F, 0F);
		head = new ModelRenderer(this, 26, 17);
		head.addBox(-1F, 4F, -5.7F, 2, 2, 2);
		head.setRotationPoint(0F, 22F, 0F);
		setRotation(head, -0.5948578F, 0F, 0F);
		sensor1 = new ModelRenderer(this, 21, 0);
		sensor1.addBox(-0.7F, -3F, -0.5F, 1, 4, 1);
		sensor1.setRotationPoint(-1F, 22F, -7F);
		setRotation(sensor1, 0.669215F, 0.4461433F, 0F);
		sensor2 = new ModelRenderer(this, 26, 0);
		sensor2.addBox(-0.3F, -3F, -0.5F, 1, 4, 1);
		sensor2.setRotationPoint(1F, 22F, -7F);
		setRotation(sensor2, 0.669215F, -0.4461411F, 0F);
		sensor3 = new ModelRenderer(this, 19, 6);
		sensor3.addBox(0F, 2F, -2F, 1, 0, 2);
		sensor3.setRotationPoint(0F, 22F, -7F);
		setRotation(sensor3, -0.2230717F, -0.669215F, 0F);
		sensor4 = new ModelRenderer(this, 24, 6);
		sensor4.addBox(-1F, 2F, -2F, 1, 0, 2);
		sensor4.setRotationPoint(0F, 22F, -7F);
		setRotation(sensor4, -0.2230717F, 0.6692116F, 0F);
		shell1 = new ModelRenderer(this, 35, 0);
		shell1.addBox(-2.5F, -6F, -4F, 5, 8, 5);
		shell1.setRotationPoint(0F, 22F, 0F);
		setRotation(shell1, -0.7063936F, 0F, 0F);
		shell2 = new ModelRenderer(this, 35, 14);
		shell2.addBox(-1.5F, -7F, -3F, 3, 1, 3);
		shell2.setRotationPoint(0F, 22F, 0F);
		setRotation(shell2, -0.7063871F, 0F, 0F);
		shell3 = new ModelRenderer(this, 27, 22);
		shell3.addBox(0F, 1.5F, -5F, 3, 1, 6);
		shell3.setRotationPoint(2F, 22F, 0F);
		setRotation(shell3, -0.7063871F, 0.6320364F, -0.0371786F);
		shell4 = new ModelRenderer(this, 46, 22);
		shell4.addBox(-3F, 1.5F, -5F, 3, 1, 6);
		shell4.setRotationPoint(-2F, 22F, 0F);
		setRotation(shell4, -0.7063871F, -0.6320361F, 0.0371755F);
		shell5 = new ModelRenderer(this, 48, 14);
		shell5.addBox(-0.5F, -1F, -4.5F, 1, 4, 1);
		shell5.setRotationPoint(0F, 22F, 0F);
		setRotation(shell5, -0.9666374F, 0F, 0F);
	}

	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel) {
		super.render(entity, limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel);
		setRotationAngles(limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel, entity);
		torso1.render(unitPixel);
		torso2.render(unitPixel);
		head.render(unitPixel);
		sensor1.render(unitPixel);
		sensor2.render(unitPixel);
		sensor3.render(unitPixel);
		sensor4.render(unitPixel);
		shell1.render(unitPixel);
		shell2.render(unitPixel);
		shell3.render(unitPixel);
		shell4.render(unitPixel);
		shell5.render(unitPixel);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel, Entity entity) {
		super.setRotationAngles(limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel, entity);
		sensor1.rotateAngleX = MathHelper.cos(limbSwing * 1F + (float) Math.PI) * 1.5F * limbSwingAngle + 0.5F;
		sensor2.rotateAngleX = MathHelper.cos(limbSwing * 1F) * 1.5F * limbSwingAngle + 0.5F;
		sensor1.rotateAngleY = MathHelper.cos(limbSwing * 1F + (float) Math.PI) * 1.5F * limbSwingAngle + 0.2F;
		sensor2.rotateAngleY = MathHelper.cos(limbSwing * 1F) * 1.5F * limbSwingAngle - 0.2F;
	}

}
