package thebetweenlands.client.render.model.armor;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.model.ModelBiped;
import net.minecraft.client.renderer.entity.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelBodyAttachment extends ModelBiped {
	protected final Set<ModelRenderer> toRender = new HashSet<>();

	protected void addAttachment(ModelRenderer base, ModelRenderer attachment) {
		base.addChild(attachment);
		this.toRender.add(attachment);
	}

	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);

		GlStateManager.pushMatrix();

		if (this.isChild) {
			GlStateManager.scalef(0.5F, 0.5F, 0.5F);
			GlStateManager.translatef(0.0F, 24.0F * scale, 0.0F);
		} else  {
			if(entity.isSneaking()) {
				GlStateManager.translatef(0.0F, 0.2F, 0.0F);
			}
		}

		this.renderAttachments(scale);

		GlStateManager.popMatrix();
	}

	protected void renderAttachments(float scale) {
		renderSpecific(this.bipedBody, scale, this.toRender);
		renderSpecific(this.bipedHead, scale, this.toRender);
		renderSpecific(this.bipedHeadwear, scale, this.toRender);
		renderSpecific(this.bipedLeftArm, scale, this.toRender);
		renderSpecific(this.bipedLeftLeg, scale, this.toRender);
		renderSpecific(this.bipedRightArm, scale, this.toRender);
		renderSpecific(this.bipedRightLeg, scale, this.toRender);
	}

	/**
	 * Only renders the model renderers in the specified set (or child models thereof) but applies all translations and rotations
	 * of the parent model renderers (without rendering them)
	 * @param base
	 * @param scale
	 * @param toRender
	 */
	public static void renderSpecific(ModelRenderer base, float scale, Set<ModelRenderer> toRender) {
		if(!base.isHidden) {
			if(base.showModel) {
				boolean shouldRender = toRender.contains(base);
				if(shouldRender) {
					base.renderWithRotation(scale);
				}

				GlStateManager.translatef(base.offsetX, base.offsetY, base.offsetZ);

				if(base.rotateAngleX == 0.0F && base.rotateAngleY == 0.0F && base.rotateAngleZ == 0.0F) {
					if(base.rotationPointX == 0.0F && base.rotationPointY == 0.0F && base.rotationPointZ == 0.0F) {
						if(base.childModels != null) {
							for(ModelRenderer childModel : base.childModels) {
								if(shouldRender) {
									childModel.render(scale);
								} else {
									renderSpecific(childModel, scale, toRender);
								}
							}
						}
					} else {
						GlStateManager.translatef(base.rotationPointX * scale, base.rotationPointY * scale, base.rotationPointZ * scale);

						if(base.childModels != null) {
							for(ModelRenderer childModel : base.childModels) {
								if(shouldRender) {
									childModel.render(scale);
								} else {
									renderSpecific(childModel, scale, toRender);
								}
							}
						}

						GlStateManager.translatef(-base.rotationPointX * scale, -base.rotationPointY * scale, -base.rotationPointZ * scale);
					}
				} else {
					GlStateManager.pushMatrix();
					GlStateManager.translatef(base.rotationPointX * scale, base.rotationPointY * scale, base.rotationPointZ * scale);

					if(base.rotateAngleZ != 0.0F) {
						GlStateManager.rotatef(base.rotateAngleZ * (180F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
					}

					if(base.rotateAngleY != 0.0F) {
						GlStateManager.rotatef(base.rotateAngleY * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
					}

					if(base.rotateAngleX != 0.0F) {
						GlStateManager.rotatef(base.rotateAngleX * (180F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
					}

					if(base.childModels != null) {
						for(ModelRenderer childModel : base.childModels) {
							if(shouldRender) {
								childModel.render(scale);
							} else {
								renderSpecific(childModel, scale, toRender);
							}
						}
					}

					GlStateManager.popMatrix();
				}

				GlStateManager.translatef(-base.offsetX, -base.offsetY, -base.offsetZ);
			}
		}
	}
}
