package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelTarminion extends ModelBase {
	ModelRenderer torso;
	ModelRenderer legleft1;
	ModelRenderer legright1;
	ModelRenderer legleft2;
	ModelRenderer legright2;
	ModelRenderer roots;
	ModelRenderer armleft;
	ModelRenderer armright;

	public ModelTarminion() {
		textureWidth = 64;
		textureHeight = 32;

		torso = new ModelRenderer(this, 0, 0);
		torso.addBox(-1.5F, 0F, -1.5F, 3, 3, 3);
		torso.setRotationPoint(0F, 18F, 0F);
		setRotation(torso, 0.1115358F, 0F, 0F);
		legleft1 = new ModelRenderer(this, 0, 7);
		legleft1.addBox(0.04F, 2.2F, -1F, 1, 2, 2);
		legleft1.setRotationPoint(0F, 18F, 0F);
		setRotation(legleft1, -0.0371705F, 0F, -0.1487144F);
		legright1 = new ModelRenderer(this, 7, 7);
		legright1.addBox(-1F, 2.2F, -1F, 1, 2, 2);
		legright1.setRotationPoint(0F, 18F, 0F);
		setRotation(legright1, -0.0371705F, 0F, 0.1487195F);
		legleft2 = new ModelRenderer(this, 0, 12);
		legleft2.addBox(-0.35F, -0.5F, -1F, 1, 3, 1);
		legleft2.setRotationPoint(1F, 22F, 0F);
		setRotation(legleft2, 0.2602503F, 0F, 0F);
		legright2 = new ModelRenderer(this, 5, 12);
		legright2.addBox(-0.65F, -0.5F, -1F, 1, 3, 1);
		legright2.setRotationPoint(-1F, 22F, 0F);
		setRotation(legright2, 0.2602503F, 0F, 0F);
		roots = new ModelRenderer(this, 0, 17);
		roots.addBox(-1.5F, 3F, -0.5F, 3, 2, 2);
		roots.setRotationPoint(0F, 18F, 0F);
		setRotation(roots, 0.0743654F, 0F, 0F);
		armleft = new ModelRenderer(this, 0, 22);
		armleft.addBox(0F, -0.5F, -0.5F, 1, 3, 1);
		armleft.setRotationPoint(1F, 20F, 0F);
		setRotation(armleft, -0.1487144F, -0.0743572F, -0.5948578F);
		armright = new ModelRenderer(this, 5, 22);
		armright.addBox(-1F, -0.5F, -0.5F, 1, 3, 1);
		armright.setRotationPoint(-1F, 20F, 0F);
		setRotation(armright, -0.1487144F, 0.074351F, 0.5948606F);
	}

	@Override
	public void render(Entity entity, float limbSwing, float prevLimbSwing, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel) {
		super.render(entity, limbSwing, prevLimbSwing, entityTickTime, rotationYaw, rotationPitch, unitPixel);
		setRotationAngles(limbSwing, prevLimbSwing, entityTickTime, rotationYaw, rotationPitch, unitPixel, entity);
		torso.render(unitPixel);
		legleft1.render(unitPixel);
		legright1.render(unitPixel);
		legleft2.render(unitPixel);
		legright2.render(unitPixel);
		roots.render(unitPixel);
		armleft.render(unitPixel);
		armright.render(unitPixel);
	}

	public void render() {
		torso.render(0.0625F);
		legleft1.render(0.0625F);
		legright1.render(0.0625F);
		legleft2.render(0.0625F);
		legright2.render(0.0625F);
		roots.render(0.0625F);
		armleft.render(0.0625F);
		armright.render(0.0625F);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	@Override
	public void setRotationAngles(float limbSwing, float prevLimbSwing, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel, Entity entity) {
		super.setRotationAngles(limbSwing, prevLimbSwing, entityTickTime, rotationYaw, rotationPitch, unitPixel, entity);
		armleft.rotateAngleX = MathHelper.cos(limbSwing * 1.5F + (float) Math.PI) * 2.0F * prevLimbSwing * 0.5F;
		armright.rotateAngleX = MathHelper.cos(limbSwing * 1.5F) * 2.0F * prevLimbSwing * 0.5F;

		legleft2.rotateAngleX = MathHelper.cos(limbSwing * 1.5F) * 1.4F * prevLimbSwing;
		legright2.rotateAngleX = MathHelper.cos(limbSwing * 1.5F + (float) Math.PI) * 1.4F * prevLimbSwing;
	}
}
