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
	    public ModelRenderer shaft_tail_1;
	    public ModelRenderer shaft_tail_2;
	    public ModelRenderer tail;

	    public ModelFishingSpear() {
	        this.textureWidth = 32;
	        this.textureHeight = 32;
	        this.fin_left = new ModelRenderer(this, 5, 17);
	        this.fin_left.setRotationPoint(0.0F, -6.0F, 0.0F);
	        this.fin_left.addBox(0.7F, 0.0F, 0.0F, 4, 4, 0, 0.0F);
	        this.setRotateAngle(fin_left, 0.0F, 0.7853981633974483F, 0.0F);
	        this.shaft = new ModelRenderer(this, 0, 0);
	        this.shaft.setRotationPoint(0.0F, 8.0F, 0.0F);
	        this.shaft.addBox(-0.5F, -12.0F, -0.5F, 1, 16, 1, 0.0F);
	        this.tail = new ModelRenderer(this, 5, 22);
	        this.tail.setRotationPoint(0.0F, 3.0F, 0.0F);
	        this.tail.addBox(0.0F, 0.0F, -2.5F, 0, 4, 5, 0.0F);
	        this.fin_right = new ModelRenderer(this, 5, 22);
	        this.fin_right.setRotationPoint(0.0F, -6.0F, 0.0F);
	        this.fin_right.addBox(-4.7F, 0.0F, 0.0F, 4, 4, 0, 0.0F);
	        this.setRotateAngle(fin_right, 0.0F, -0.7853981633974483F, 0.0F);
	        this.bone_tip = new ModelRenderer(this, 5, 4);
	        this.bone_tip.setRotationPoint(0.0F, 8.0F, 0.0F);
	        this.bone_tip.addBox(0.0F, -16.0F, -2.0F, 0, 8, 4, 0.0F);
	        this.shaft_tail_1 = new ModelRenderer(this, 0, 18);
	        this.shaft_tail_1.setRotationPoint(0.0F, 4.0F, 0.0F);
	        this.shaft_tail_1.addBox(-0.5F, 0.0F, -0.5F, 1, 5, 1, 0.0F);
	        this.shaft_tail_2 = new ModelRenderer(this, 0, 25);
	        this.shaft_tail_2.setRotationPoint(0.0F, 5.0F, 0.0F);
	        this.shaft_tail_2.addBox(-0.5F, 0.0F, -0.5F, 1, 4, 1, 0.0F);
	        this.tip_binding = new ModelRenderer(this, 5, 0);
	        this.tip_binding.setRotationPoint(0.0F, 0.0F, 0.0F);
	        this.tip_binding.addBox(-1.0F, -11.0F, -1.0F, 2, 5, 2, 0.0F);
	        this.urchin_tip = new ModelRenderer(this, 14, 4);
	        this.urchin_tip.setRotationPoint(0.0F, 8.0F, 0.0F);
	        this.urchin_tip.addBox(0.0F, -16.0F, -2.0F, 0, 8, 4, 0.0F);
	        this.shaft.addChild(this.fin_left);
	        this.shaft_tail_2.addChild(this.tail);
	        this.shaft.addChild(this.fin_right);
	        this.shaft.addChild(this.shaft_tail_1);
	        this.shaft_tail_1.addChild(this.shaft_tail_2);
	        this.shaft.addChild(this.tip_binding);
    }

	private void resetModel() {
		fin_left.isHidden = false;
		fin_right.isHidden = false;
		tail.isHidden = false;

		fin_right.rotateAngleY = -0.7853981633974483F;
		fin_left.rotateAngleY = 0.7853981633974483F;
		fin_right.rotateAngleZ = 0F;
		fin_left.rotateAngleZ = 0F;
		shaft_tail_1.rotateAngleZ = 0F;
		shaft_tail_2.rotateAngleZ = 0F;
		tail.rotateAngleZ = 0F;
	}

	public void renderBasic(EntityFishingSpear entity, float partialTicks) {
		resetModel();
		fin_left.isHidden = true;
		fin_right.isHidden = true;
		tail.isHidden = true;
		this.bone_tip.render(0.0625F);
		this.shaft.render(0.0625F);
	}

	public void renderAmphibious(EntityFishingSpear entity, float partialTicks) {
		resetModel();
		this.bone_tip.render(0.0625F);
		this.shaft.render(0.0625F);
	}

	public void renderRobustAmphibious(EntityFishingSpear entity, float partialTicks) {
		resetModel();

		EntityFishingSpear spear = (EntityFishingSpear) entity;
		float flap = MathHelper.sin((spear.ticksExisted + partialTicks) * 0.5F) * 0.6F;

		if (spear.getAnimated() && spear.returningTicks > 0) {
			fin_right.rotateAngleY = -0.7853981633974483F + flap * 0.5F;
			fin_left.rotateAngleY = 0.7853981633974483F - flap * 0.5F;
			fin_right.rotateAngleZ = 0F - flap * 0.5F;
			fin_left.rotateAngleZ = 0F + flap * 0.5F;
			shaft_tail_1.rotateAngleZ = 0F + flap * 0.25F;
			shaft_tail_2.rotateAngleZ = 0F + flap * 0.25F;
			tail.rotateAngleZ = 0F + flap * 0.25F;
		}
		this.urchin_tip.render(0.0625F);
		this.shaft.render(0.0625F);
	}

	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }

}
