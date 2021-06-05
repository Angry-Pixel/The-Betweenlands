package thebetweenlands.client.render.model.armor;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.inventory.EntityEquipmentSlot;

public class ModelBodyAttachment extends ModelBiped {
	public final ModelRenderer bipedRightBoot = new ModelRenderer(this, 0, 0);
	public final ModelRenderer bipedLeftBoot = new ModelRenderer(this, 0, 0);

	private final List<ModelRenderer> baseParts = ImmutableList.of(bipedHead, bipedHeadwear, bipedBody, bipedRightArm, bipedLeftArm, bipedRightLeg, bipedLeftLeg, bipedRightBoot, bipedLeftBoot);

	public ModelBodyAttachment() {
		bipedLeftLeg.addChild(bipedLeftBoot);
		bipedRightLeg.addChild(bipedRightBoot);
		clear(baseParts);
	}

	private void clear(List<ModelRenderer> renderers) {
		for (ModelRenderer renderer : renderers) {
			renderer.cubeList.clear();
		}
	}

	public void resetVisibilities() {
		for(ModelRenderer basePart : baseParts) {
			if(basePart.childModels != null) {
				for(ModelRenderer childPart : basePart.childModels) {
					if(childPart != bipedRightBoot && childPart != bipedLeftBoot) {
						childPart.showModel = false;
					}
				}
			}
		}
	}

	public void setVisibilities(EntityEquipmentSlot slot) {
		resetVisibilities();

		switch(slot) {
		case HEAD:
			if(bipedHead.childModels != null) {
				for(ModelRenderer childPart : bipedHead.childModels) {
					childPart.showModel = true;
				}
			}
			break;
		case CHEST:
			if(bipedBody.childModels != null) {
				for(ModelRenderer childPart : bipedBody.childModels) {
					childPart.showModel = true;
				}
			}
			if(bipedRightArm.childModels != null) {
				for(ModelRenderer childPart : bipedRightArm.childModels) {
					childPart.showModel = true;
				}
			}
			if(bipedLeftArm.childModels != null) {
				for(ModelRenderer childPart : bipedLeftArm.childModels) {
					childPart.showModel = true;
				}
			}
			break;
		case LEGS:
			if(bipedRightLeg.childModels != null) {
				for(ModelRenderer childPart : bipedRightLeg.childModels) {
					if(childPart != bipedRightBoot && childPart != bipedLeftBoot) {
						childPart.showModel = true;
					}
				}
			}
			if(bipedLeftLeg.childModels != null) {
				for(ModelRenderer childPart : bipedLeftLeg.childModels) {
					if(childPart != bipedRightBoot && childPart != bipedLeftBoot) {
						childPart.showModel = true;
					}
				}
			}
			break;
		case FEET:
			if(bipedRightBoot.childModels != null) {
				for(ModelRenderer childPart : bipedRightBoot.childModels) {
					childPart.showModel = true;
				}
			}
			if(bipedLeftBoot.childModels != null) {
				for(ModelRenderer childPart : bipedLeftBoot.childModels) {
					childPart.showModel = true;
				}
			}
			break;
		}
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
		if (entityIn instanceof EntityArmorStand) {
			// Disable idle animations when on an armor stand
			EntityArmorStand entityarmorstand = (EntityArmorStand)entityIn;
			this.bipedHead.rotateAngleX = 0.017453292F * entityarmorstand.getHeadRotation().getX();
			this.bipedHead.rotateAngleY = 0.017453292F * entityarmorstand.getHeadRotation().getY();
			this.bipedHead.rotateAngleZ = 0.017453292F * entityarmorstand.getHeadRotation().getZ();
			this.bipedHead.setRotationPoint(0.0F, 1.0F, 0.0F);
			this.bipedBody.rotateAngleX = 0.017453292F * entityarmorstand.getBodyRotation().getX();
			this.bipedBody.rotateAngleY = 0.017453292F * entityarmorstand.getBodyRotation().getY();
			this.bipedBody.rotateAngleZ = 0.017453292F * entityarmorstand.getBodyRotation().getZ();
			this.bipedLeftArm.rotateAngleX = 0.017453292F * entityarmorstand.getLeftArmRotation().getX();
			this.bipedLeftArm.rotateAngleY = 0.017453292F * entityarmorstand.getLeftArmRotation().getY();
			this.bipedLeftArm.rotateAngleZ = 0.017453292F * entityarmorstand.getLeftArmRotation().getZ();
			this.bipedRightArm.rotateAngleX = 0.017453292F * entityarmorstand.getRightArmRotation().getX();
			this.bipedRightArm.rotateAngleY = 0.017453292F * entityarmorstand.getRightArmRotation().getY();
			this.bipedRightArm.rotateAngleZ = 0.017453292F * entityarmorstand.getRightArmRotation().getZ();
			this.bipedLeftLeg.rotateAngleX = 0.017453292F * entityarmorstand.getLeftLegRotation().getX();
			this.bipedLeftLeg.rotateAngleY = 0.017453292F * entityarmorstand.getLeftLegRotation().getY();
			this.bipedLeftLeg.rotateAngleZ = 0.017453292F * entityarmorstand.getLeftLegRotation().getZ();
			this.bipedLeftLeg.setRotationPoint(1.9F, 11.0F, 0.0F);
			this.bipedRightLeg.rotateAngleX = 0.017453292F * entityarmorstand.getRightLegRotation().getX();
			this.bipedRightLeg.rotateAngleY = 0.017453292F * entityarmorstand.getRightLegRotation().getY();
			this.bipedRightLeg.rotateAngleZ = 0.017453292F * entityarmorstand.getRightLegRotation().getZ();
			this.bipedRightLeg.setRotationPoint(-1.9F, 11.0F, 0.0F);
			copyModelAngles(this.bipedHead, this.bipedHeadwear);
		} else {
			super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
		}
	}
}
