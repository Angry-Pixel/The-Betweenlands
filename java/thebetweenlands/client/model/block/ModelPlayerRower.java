package thebetweenlands.client.model.block;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import thebetweenlands.client.model.ModelBipedLimb;
import thebetweenlands.client.model.ModelBoxCustomizable;
import thebetweenlands.utils.MathUtils;

public class ModelPlayerRower extends ModelBiped {
	private ModelBipedLimb leftArm, rightArm;

	private ModelRenderer leftForearm, rightForearm;

	public ModelPlayerRower(float expand) {
		super(expand, 0, 64, expand == 0 ? 64 : 32);
		if (expand == 0) {
			bipedLeftArm = leftArm = createReplacementLimb(bipedLeftArm, 40, 16);
			bipedRightArm = rightArm = createReplacementLimb(bipedRightArm, 32, 48);
			// bipedLeftLeg = createReplacementLimb(bipedLeftLeg, 0, 16);
			// bipedRightLeg = createReplacementLimb(bipedRightLeg, 16, 48);
		} else {
			bipedLeftArm = createExpandReplacementLimb(bipedLeftArm, 40, 16, expand);
			bipedRightArm = createExpandReplacementLimb(bipedRightArm, 40, 16, expand);
			// bipedLeftLeg = createExpandReplacementLimb(bipedLeftLeg, 0, 16, expand);
			// bipedRightLeg = createExpandReplacementLimb(bipedRightLeg, 0, 16, expand);
		}
	}

	private ModelBipedLimb createReplacementLimb(ModelRenderer oldLimb, int textureOffsetX, int textureOffsetY) {
		ModelBipedLimb limb = new ModelBipedLimb(this, textureOffsetX, textureOffsetY);
		// if legs are made special the z rotation point will need to be altered
		limb.setRotationPoint(Math.signum(oldLimb.rotationPointX) * 6, oldLimb.rotationPointY, oldLimb.rotationPointZ);
		ModelBox box = (ModelBox) oldLimb.cubeList.get(0);
		limb.offsetX = -2;
		limb.offsetY = box.posY1;
		limb.offsetZ = box.posZ1;
		return limb;
	}

	private ModelRenderer createExpandReplacementLimb(ModelRenderer oldLimb, int textureOffsetX, int textureOffsetY, float expand) {
		ModelRenderer limb = new ModelRenderer(this, textureOffsetX, textureOffsetY);
		limb.mirror = oldLimb.mirror;
		ModelBox box = (ModelBox) oldLimb.cubeList.get(0);
		ModelBoxCustomizable arm = new ModelBoxCustomizable(limb, textureOffsetX, textureOffsetY, -2, box.posY1, box.posZ1, 4, 6, 4, expand);
		arm.setVisibleSides(~ModelBoxCustomizable.SIDE_BOTTOM);
		limb.cubeList.add(arm);
		limb.setRotationPoint(Math.signum(oldLimb.rotationPointX) * 6, oldLimb.rotationPointY, oldLimb.rotationPointZ);
		ModelRenderer lowerLimb = new ModelRenderer(this, textureOffsetX, textureOffsetY - 6);
		if (bipedLeftArm == oldLimb) {
			leftForearm = lowerLimb;
		} else {
			rightForearm = lowerLimb;
		}
		lowerLimb.mirror = oldLimb.mirror;
		lowerLimb.setRotationPoint(-2 + 2, box.posY1 + 6, box.posZ1 + 2);
		ModelBoxCustomizable forearm = new ModelBoxCustomizable(lowerLimb, textureOffsetX, textureOffsetY + 6, -2, 0, -2, 4, 6, 4, expand * 0.75F, -6);
		forearm.setVisibleSides(~ModelBoxCustomizable.SIDE_TOP);
		lowerLimb.cubeList.add(forearm);
		limb.addChild(lowerLimb);
		return limb;
	}

	public void setLeftArmFlexionAngle(float flexionAngle) {
		if (leftArm == null) {
			leftForearm.rotateAngleX = flexionAngle * MathUtils.DEG_TO_RAD;
		} else {
			leftArm.setFlexionAngle(flexionAngle);
		}
	}

	public void setRightArmFlexionAngle(float flexionAngle) {
		if (rightArm == null) {
			rightForearm.rotateAngleX = flexionAngle * MathUtils.DEG_TO_RAD;
		} else {
			rightArm.setFlexionAngle(flexionAngle);
		}
	}

	@Override
	public void setRotationAngles(float speed, float swing, float ticksExisted, float yaw, float pitch, float scale, Entity entity) {
		bipedHead.rotateAngleY = yaw * MathUtils.DEG_TO_RAD;
		bipedHead.rotateAngleX = pitch * MathUtils.DEG_TO_RAD;
		bipedHead.rotationPointY = 0;
		bipedHeadwear.rotateAngleY = bipedHead.rotateAngleY;
		bipedHeadwear.rotateAngleX = bipedHead.rotateAngleX;
		bipedHeadwear.rotationPointY = 0;
		bipedRightLeg.rotateAngleX = -((float) Math.PI * 2F / 5F);
		bipedRightLeg.rotateAngleY = ((float) Math.PI / 10F);
		bipedRightLeg.rotationPointZ = 0.1F;
		bipedRightLeg.rotationPointY = 12;
		bipedLeftLeg.rotateAngleX = -((float) Math.PI * 2F / 5F);
		bipedLeftLeg.rotateAngleY = -((float) Math.PI / 10F);
		bipedLeftLeg.rotationPointZ = 0.1F;
		bipedLeftLeg.rotationPointY = 12;
	}
}
