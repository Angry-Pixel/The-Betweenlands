package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.projectiles.EntityFishingSpear;

@SideOnly(Side.CLIENT)
public class ModelFishingSpear extends ModelBase {
    public ModelRenderer shaft;
    public ModelRenderer bone_tip;
    public ModelRenderer urchin_tip;
    public ModelRenderer fin_left;
    public ModelRenderer tip_binding;
    public ModelRenderer fin_right;
    public ModelRenderer tail;

    public ModelFishingSpear() {
        this.textureWidth = 32;
        this.textureHeight = 32;
        this.bone_tip = new ModelRenderer(this, 5, 4);
        this.bone_tip.setRotationPoint(0.0F, 8.0F, 0.0F);
        this.bone_tip.addBox(0.0F, -16.0F, -2.0F, 0, 8, 4, 0.0F);
        this.tip_binding = new ModelRenderer(this, 5, 0);
        this.tip_binding.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.tip_binding.addBox(-1.0F, -11.0F, -1.0F, 2, 5, 2, 0.0F);
        this.shaft = new ModelRenderer(this, 0, 0);
        this.shaft.setRotationPoint(0.0F, 8.0F, 0.0F);
        this.shaft.addBox(-0.5F, -12.0F, -0.5F, 1, 26, 1, 0.0F);
        this.fin_right = new ModelRenderer(this, 5, 22);
        this.fin_right.setRotationPoint(0.0F, -6.0F, 0.0F);
        this.fin_right.addBox(-4.7F, 0.0F, 0.0F, 4, 4, 0, 0.0F);
        this.setRotateAngle(fin_right, 0.0F, -0.7853981633974483F, 0.0F);
        this.tail = new ModelRenderer(this, 5, 22);
        this.tail.setRotationPoint(0.0F, 12.0F, 0.0F);
        this.tail.addBox(0.0F, 0.0F, -2.5F, 0, 4, 5, 0.0F);
        this.fin_left = new ModelRenderer(this, 5, 17);
        this.fin_left.setRotationPoint(0.0F, -6.0F, 0.0F);
        this.fin_left.addBox(0.7F, 0.0F, 0.0F, 4, 4, 0, 0.0F);
        this.setRotateAngle(fin_left, 0.0F, 0.7853981633974483F, 0.0F);
        this.urchin_tip = new ModelRenderer(this, 14, 4);
        this.urchin_tip.setRotationPoint(0.0F, 8.0F, 0.0F);
        this.urchin_tip.addBox(0.0F, -16.0F, -2.0F, 0, 8, 4, 0.0F);
        this.shaft.addChild(this.tip_binding);
        this.shaft.addChild(this.fin_right);
        this.shaft.addChild(this.tail);
        this.shaft.addChild(this.fin_left);
    }

	public void render(EntityFishingSpear entity, float partialTicks) {
		EntityFishingSpear spear = (EntityFishingSpear) entity;
		float flap = MathHelper.sin((spear.ticksExisted + partialTicks) * 0.5F) * 0.6F;
		this.bone_tip.render(0.0625F);
		this.urchin_tip.render(0.0625F);
		this.shaft.render(0.0625F);

		fin_left.isHidden = false;
		fin_right.isHidden = false;
		tail.isHidden = false;
		urchin_tip.isHidden = false;
		bone_tip.isHidden = false;

		if (spear.getType() == 0) {
			fin_left.isHidden = true;
			fin_right.isHidden = true;
			tail.isHidden = true;
			urchin_tip.isHidden = true;
		}

		if (spear.getType() == 1) {
			urchin_tip.isHidden = true;
		}

		if (spear.getType() == 2) {
			bone_tip.isHidden = true;
			if (spear.getAnimated()) {
				fin_right.rotateAngleY = -0.7853981633974483F + flap;
				fin_left.rotateAngleY = 0.7853981633974483F - flap;
			}
		}

	}

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }



}
