package thebetweenlands.client.render.model.armor;

import java.util.function.Function;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;

public class ModelRendererItemAttachment<T extends EntityLivingBase> extends ModelRenderer {
	private T entity;
	private Function<T, ItemStack> stack;
	private EnumHandSide side;
	private float scale;

	public ModelRendererItemAttachment(ModelBase model, Function<T, ItemStack> stack, EnumHandSide side, float scale) {
		super(model);
		this.stack = stack;
		this.side = side;
		this.scale = scale;
		this.isHidden = true;
	}

	public void setEntity(T entity) {
		this.entity = entity;
	}

	@Override
	public void render(float scale) {
		if (!this.isHidden)
		{
			if (this.showModel)
			{
				GlStateManager.translate(this.offsetX, this.offsetY, this.offsetZ);

				if (this.rotateAngleX == 0.0F && this.rotateAngleY == 0.0F && this.rotateAngleZ == 0.0F)
				{
					if (this.rotationPointX == 0.0F && this.rotationPointY == 0.0F && this.rotationPointZ == 0.0F)
					{
						this.renderItem(scale);

						if (this.childModels != null)
						{
							for (int k = 0; k < this.childModels.size(); ++k)
							{
								((ModelRenderer)this.childModels.get(k)).render(scale);
							}
						}
					}
					else
					{
						GlStateManager.translate(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);

						this.renderItem(scale);

						if (this.childModels != null)
						{
							for (int j = 0; j < this.childModels.size(); ++j)
							{
								((ModelRenderer)this.childModels.get(j)).render(scale);
							}
						}

						GlStateManager.translate(-this.rotationPointX * scale, -this.rotationPointY * scale, -this.rotationPointZ * scale);
					}
				}
				else
				{
					GlStateManager.pushMatrix();
					GlStateManager.translate(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);

					if (this.rotateAngleZ != 0.0F)
					{
						GlStateManager.rotate(this.rotateAngleZ * (180F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
					}

					if (this.rotateAngleY != 0.0F)
					{
						GlStateManager.rotate(this.rotateAngleY * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
					}

					if (this.rotateAngleX != 0.0F)
					{
						GlStateManager.rotate(this.rotateAngleX * (180F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
					}

					this.renderItem(scale);

					if (this.childModels != null)
					{
						for (int i = 0; i < this.childModels.size(); ++i)
						{
							((ModelRenderer)this.childModels.get(i)).render(scale);
						}
					}

					GlStateManager.popMatrix();
				}

				GlStateManager.translate(-this.offsetX, -this.offsetY, -this.offsetZ);
			}
		}
	}

	@Override
	public void renderWithRotation(float scale) {
		if (!this.isHidden)
		{
			if (this.showModel)
			{
				GlStateManager.pushMatrix();
				GlStateManager.translate(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);

				if (this.rotateAngleY != 0.0F)
				{
					GlStateManager.rotate(this.rotateAngleY * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
				}

				if (this.rotateAngleX != 0.0F)
				{
					GlStateManager.rotate(this.rotateAngleX * (180F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
				}

				if (this.rotateAngleZ != 0.0F)
				{
					GlStateManager.rotate(this.rotateAngleZ * (180F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
				}

				this.renderItem(scale);

				GlStateManager.popMatrix();
			}
		}
	}

	protected void renderItem(float modelScale) {
		if(this.entity != null && this.stack != null && this.side != null) {
			GlStateManager.pushMatrix();
			GlStateManager.rotate(-180.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.scale(this.scale, this.scale, this.scale);

			Minecraft.getMinecraft().getItemRenderer().renderItemSide(this.entity, this.stack.apply(this.entity),
					this.side == EnumHandSide.LEFT ? ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND : ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND,
							this.side == EnumHandSide.LEFT);

			GlStateManager.popMatrix();
		}
	}
}
