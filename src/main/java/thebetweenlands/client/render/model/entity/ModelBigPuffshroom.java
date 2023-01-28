package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.EntityBigPuffshroom;

@SideOnly(Side.CLIENT)

public class ModelBigPuffshroom extends ModelBase {
	ModelRenderer main_base;
	ModelRenderer main_head;
	ModelRenderer front_l_tent_origin;
	ModelRenderer front_r_tent_origin;
	ModelRenderer front_c_tent_origin;
	ModelRenderer back_c_tent_origin;
	ModelRenderer left_c_tent_origin;
	ModelRenderer right_c_tent_origin;
	ModelRenderer back_l_tent_origin;
	ModelRenderer back_r_tent_origin;
	ModelRenderer stalk_bottom;
	ModelRenderer front_l_tent_1;
	ModelRenderer front_l_tent_2;
	ModelRenderer front_l_tent_3;
	ModelRenderer front_r_tent_1;
	ModelRenderer front_r_tent_2;
	ModelRenderer front_r_tent_3;
	ModelRenderer front_c_tent_1;
	ModelRenderer front_c_tent_2;
	ModelRenderer front_c_tent_3;
	ModelRenderer back_c_tent_1;
	ModelRenderer back_c_tent_2;
	ModelRenderer back_c_tent_3;
	ModelRenderer left_c_tent_1;
	ModelRenderer left_c_tent_2;
	ModelRenderer left_c_tent_3;
	ModelRenderer right_c_tent_1;
	ModelRenderer right_c_tent_2;
	ModelRenderer right_c_tent_3;
	ModelRenderer back_l_tent_1;
	ModelRenderer back_l_tent_2;
	ModelRenderer back_l_tent_3;
	ModelRenderer back_r_tent_1;
	ModelRenderer back_r_tent_2;
	ModelRenderer back_r_tent_3;
	ModelRenderer funnel_front;
	ModelRenderer funnel_back;
	ModelRenderer funnel_left;
	ModelRenderer funnel;
	ModelRenderer stalk_top;

	public ModelBigPuffshroom() {
		textureWidth = 64;
		textureHeight = 64;
		back_c_tent_3 = new ModelRenderer(this, 49, 8);
		back_c_tent_3.setRotationPoint(0.0F, -1.0F, 0.0F);
		back_c_tent_3.addBox(-0.5F, -1.5F, -0.5F, 1, 2, 1, 0.0F);
		setRotateAngle(back_c_tent_3, -0.7853981633974483F, 0.0F, 0.0F);
		front_l_tent_3 = new ModelRenderer(this, 0, 0);
		front_l_tent_3.setRotationPoint(0.0F, -3.0F, 0.0F);
		front_l_tent_3.addBox(-0.5F, -2.5F, -0.5F, 1, 3, 1, 0.0F);
		setRotateAngle(front_l_tent_3, -0.7853981633974483F, 0.0F, 0.0F);
		back_r_tent_origin = new ModelRenderer(this, 32, 8);
		back_r_tent_origin.setRotationPoint(2.0F, -1.5F, 2.0F);
		back_r_tent_origin.addBox(-1.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F);
		setRotateAngle(back_r_tent_origin, 0.7853981633974483F, -2.356194490192345F, 0.0F);
		stalk_bottom = new ModelRenderer(this, 15, 0);
		stalk_bottom.setRotationPoint(0.0F, 0.0F, 0.0F);
		stalk_bottom.addBox(-1.0F, -3.5F, -1.0F, 2, 1, 2, 0.0F);
		left_c_tent_1 = new ModelRenderer(this, 46, 16);
		left_c_tent_1.setRotationPoint(0.0F, -1.0F, 0.0F);
		left_c_tent_1.addBox(-1.5F, -2.0F, -1.5F, 3, 2, 3, 0.0F);
		funnel_back = new ModelRenderer(this, 15, 3);
		funnel_back.setRotationPoint(0.0F, -8.5F, 0.0F);
		funnel_back.addBox(-0.5F, -1.0F, 0.2F, 1, 1, 1, 0.0F);
		setRotateAngle(funnel_back, -0.20943951023931953F, -0.5759586531581287F, 0.0F);
		back_l_tent_1 = new ModelRenderer(this, 35, 18);
		back_l_tent_1.setRotationPoint(0.0F, -1.0F, 0.0F);
		back_l_tent_1.addBox(-1.5F, -2.0F, -1.5F, 3, 2, 3, 0.0F);
		back_c_tent_origin = new ModelRenderer(this, 0, 8);
		back_c_tent_origin.setRotationPoint(0.0F, -1.5F, 2.2F);
		back_c_tent_origin.addBox(-1.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F);
		setRotateAngle(back_c_tent_origin, 0.6981317007977318F, 3.141592653589793F, 0.0F);
		front_r_tent_2 = new ModelRenderer(this, 12, 12);
		front_r_tent_2.setRotationPoint(0.0F, -2.0F, 0.0F);
		front_r_tent_2.addBox(-1.0F, -3.0F, -1.0F, 2, 4, 2, 0.0F);
		setRotateAngle(front_r_tent_2, -0.7853981633974483F, 0.0F, 0.0F);
		back_l_tent_origin = new ModelRenderer(this, 24, 8);
		back_l_tent_origin.setRotationPoint(-2.0F, -1.5F, 2.0F);
		back_l_tent_origin.addBox(-1.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F);
		setRotateAngle(back_l_tent_origin, 0.7853981633974483F, 2.356194490192345F, 0.0F);
		funnel_front = new ModelRenderer(this, 21, 0);
		funnel_front.setRotationPoint(0.0F, -8.5F, 0.0F);
		funnel_front.addBox(-0.5F, -1.0F, -1.2F, 1, 1, 1, 0.0F);
		setRotateAngle(funnel_front, 0.20943951023931953F, 0.5759586531581287F, 0.0F);
		right_c_tent_origin = new ModelRenderer(this, 16, 8);
		right_c_tent_origin.setRotationPoint(2.2F, -1.5F, 0.0F);
		right_c_tent_origin.addBox(-1.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F);
		setRotateAngle(right_c_tent_origin, 0.6981317007977318F, -1.5707963267948966F, 0.0F);
		front_r_tent_origin = new ModelRenderer(this, 48, 4);
		front_r_tent_origin.setRotationPoint(2.0F, -1.5F, -2.0F);
		front_r_tent_origin.addBox(-1.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F);
		setRotateAngle(front_r_tent_origin, 0.7853981633974483F, -0.7853981633974483F, 0.0F);
		stalk_top = new ModelRenderer(this, 47, 0);
		stalk_top.setRotationPoint(0.0F, 0.0F, 0.0F);
		stalk_top.addBox(-1.5F, -4.5F, -1.5F, 3, 1, 3, 0.0F);
		right_c_tent_3 = new ModelRenderer(this, 59, 15);
		right_c_tent_3.setRotationPoint(0.0F, -1.0F, 0.0F);
		right_c_tent_3.addBox(-0.5F, -1.5F, -0.5F, 1, 2, 1, 0.0F);
		setRotateAngle(right_c_tent_3, -0.7853981633974483F, 0.0F, 0.0F);
		front_c_tent_1 = new ModelRenderer(this, 20, 12);
		front_c_tent_1.setRotationPoint(0.0F, -1.0F, 0.0F);
		front_c_tent_1.addBox(-1.5F, -2.0F, -1.5F, 3, 2, 3, 0.0F);
		left_c_tent_3 = new ModelRenderer(this, 46, 13);
		left_c_tent_3.setRotationPoint(0.0F, -1.0F, 0.0F);
		left_c_tent_3.addBox(-0.5F, -1.5F, -0.5F, 1, 2, 1, 0.0F);
		setRotateAngle(left_c_tent_3, -0.7853981633974483F, 0.0F, 0.0F);
		front_r_tent_3 = new ModelRenderer(this, 59, 0);
		front_r_tent_3.setRotationPoint(0.0F, -3.0F, 0.0F);
		front_r_tent_3.addBox(-0.5F, -2.5F, -0.5F, 1, 3, 1, 0.0F);
		setRotateAngle(front_r_tent_3, -0.7853981633974483F, 0.0F, 0.0F);
		funnel_left = new ModelRenderer(this, 19, 3);
		funnel_left.setRotationPoint(0.0F, -8.5F, 0.0F);
		funnel_left.addBox(-0.5F, -1.0F, 0.1F, 1, 1, 1, 0.0F);
		setRotateAngle(funnel_left, -0.20943951023931953F, 1.5707963267948966F, 0.0F);
		back_r_tent_1 = new ModelRenderer(this, 44, 21);
		back_r_tent_1.setRotationPoint(0.0F, -1.0F, 0.0F);
		back_r_tent_1.addBox(-1.5F, -2.0F, -1.5F, 3, 2, 3, 0.0F);
		back_c_tent_1 = new ModelRenderer(this, 37, 13);
		back_c_tent_1.setRotationPoint(0.0F, -1.0F, 0.0F);
		back_c_tent_1.addBox(-1.5F, -2.0F, -1.5F, 3, 2, 3, 0.0F);
		back_l_tent_2 = new ModelRenderer(this, 27, 20);
		back_l_tent_2.setRotationPoint(0.0F, -2.0F, 0.0F);
		back_l_tent_2.addBox(-1.0F, -3.0F, -1.0F, 2, 4, 2, 0.0F);
		setRotateAngle(back_l_tent_2, -0.7853981633974483F, 0.0F, 0.0F);
		front_r_tent_1 = new ModelRenderer(this, 0, 12);
		front_r_tent_1.setRotationPoint(0.0F, -1.0F, 0.0F);
		front_r_tent_1.addBox(-1.5F, -2.0F, -1.5F, 3, 2, 3, 0.0F);
		front_c_tent_3 = new ModelRenderer(this, 44, 0);
		front_c_tent_3.setRotationPoint(0.0F, -1.0F, 0.0F);
		front_c_tent_3.addBox(-0.5F, -1.5F, -0.5F, 1, 2, 1, 0.0F);
		setRotateAngle(front_c_tent_3, -0.7853981633974483F, 0.0F, 0.0F);
		main_base = new ModelRenderer(this, 0, 0);
		main_base.setRotationPoint(0.0F, 24.0F, 0.0F);
		main_base.addBox(-2.5F, -2.51F, -2.5F, 5, 3, 5, 0.0F);
		main_head = new ModelRenderer(this, 20, 0);
		main_head.setRotationPoint(0.0F, 24.0F, 0.0F);
		main_head.addBox(-2.5F, -7.5F, -2.5F, 5, 3, 5, 0.0F);
		front_c_tent_origin = new ModelRenderer(this, 54, 6);
		front_c_tent_origin.setRotationPoint(0.0F, -1.5F, -2.2F);
		front_c_tent_origin.addBox(-1.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F);
		setRotateAngle(front_c_tent_origin, 0.6981317007977318F, 0.0F, 0.0F);
		left_c_tent_origin = new ModelRenderer(this, 8, 8);
		left_c_tent_origin.setRotationPoint(-2.2F, -1.5F, 0.0F);
		left_c_tent_origin.addBox(-1.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F);
		setRotateAngle(left_c_tent_origin, 0.6981317007977318F, 1.5707963267948966F, 0.0F);
		front_l_tent_1 = new ModelRenderer(this, 40, 8);
		front_l_tent_1.setRotationPoint(0.0F, -1.0F, 0.0F);
		front_l_tent_1.addBox(-1.5F, -2.0F, -1.5F, 3, 2, 3, 0.0F);
		back_c_tent_2 = new ModelRenderer(this, 30, 16);
		back_c_tent_2.setRotationPoint(0.0F, -2.0F, 0.0F);
		back_c_tent_2.addBox(-1.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F);
		setRotateAngle(back_c_tent_2, -0.7853981633974483F, 0.0F, 0.0F);
		back_r_tent_2 = new ModelRenderer(this, 0, 21);
		back_r_tent_2.setRotationPoint(0.0F, -2.0F, 0.0F);
		back_r_tent_2.addBox(-1.0F, -3.0F, -1.0F, 2, 4, 2, 0.0F);
		setRotateAngle(back_r_tent_2, -0.7853981633974483F, 0.0F, 0.0F);
		front_l_tent_2 = new ModelRenderer(this, 52, 10);
		front_l_tent_2.setRotationPoint(0.0F, -2.0F, 0.0F);
		front_l_tent_2.addBox(-1.0F, -3.0F, -1.0F, 2, 4, 2, 0.0F);
		setRotateAngle(front_l_tent_2, -0.7853981633974483F, 0.0F, 0.0F);
		right_c_tent_2 = new ModelRenderer(this, 8, 18);
		right_c_tent_2.setRotationPoint(0.0F, -2.0F, 0.0F);
		right_c_tent_2.addBox(-1.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F);
		setRotateAngle(right_c_tent_2, -0.7853981633974483F, 0.0F, 0.0F);
		back_r_tent_3 = new ModelRenderer(this, 8, 22);
		back_r_tent_3.setRotationPoint(0.0F, -3.0F, 0.0F);
		back_r_tent_3.addBox(-0.5F, -2.5F, -0.5F, 1, 3, 1, 0.0F);
		setRotateAngle(back_r_tent_3, -0.7853981633974483F, 0.0F, 0.0F);
		front_c_tent_2 = new ModelRenderer(this, 32, 12);
		front_c_tent_2.setRotationPoint(0.0F, -2.0F, 0.0F);
		front_c_tent_2.addBox(-1.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F);
		setRotateAngle(front_c_tent_2, -0.7853981633974483F, 0.0F, 0.0F);
		right_c_tent_1 = new ModelRenderer(this, 17, 17);
		right_c_tent_1.setRotationPoint(0.0F, -1.0F, 0.0F);
		right_c_tent_1.addBox(-1.5F, -2.0F, -1.5F, 3, 2, 3, 0.0F);
		left_c_tent_2 = new ModelRenderer(this, 0, 17);
		left_c_tent_2.setRotationPoint(0.0F, -2.0F, 0.0F);
		left_c_tent_2.addBox(-1.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F);
		setRotateAngle(left_c_tent_2, -0.7853981633974483F, 0.0F, 0.0F);
		back_l_tent_3 = new ModelRenderer(this, 58, 18);
		back_l_tent_3.setRotationPoint(0.0F, -3.0F, 0.0F);
		back_l_tent_3.addBox(-0.5F, -2.5F, -0.5F, 1, 3, 1, 0.0F);
		setRotateAngle(back_l_tent_3, -0.7853981633974483F, 0.0F, 0.0F);
		funnel = new ModelRenderer(this, 35, 0);
		funnel.setRotationPoint(0.0F, 0.0F, 0.0F);
		funnel.addBox(-1.5F, -8.5F, -1.5F, 3, 1, 3, 0.0F);
		front_l_tent_origin = new ModelRenderer(this, 40, 4);
		front_l_tent_origin.setRotationPoint(-2.0F, -1.5F, -2.0F);
		front_l_tent_origin.addBox(-1.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F);
		setRotateAngle(front_l_tent_origin, 0.7853981633974483F, 0.7853981633974483F, 0.0F);

		back_c_tent_2.addChild(back_c_tent_3);
		front_l_tent_2.addChild(front_l_tent_3);
		main_base.addChild(back_r_tent_origin);
		main_base.addChild(stalk_bottom);
		left_c_tent_origin.addChild(left_c_tent_1);
		main_head.addChild(funnel_back);
		back_l_tent_origin.addChild(back_l_tent_1);
		main_base.addChild(back_c_tent_origin);
		front_r_tent_1.addChild(front_r_tent_2);
		main_base.addChild(back_l_tent_origin);
		main_head.addChild(funnel_front);
		main_base.addChild(right_c_tent_origin);
		main_base.addChild(front_r_tent_origin);
		main_head.addChild(stalk_top);
		right_c_tent_2.addChild(right_c_tent_3);
		front_c_tent_origin.addChild(front_c_tent_1);
		left_c_tent_2.addChild(left_c_tent_3);
		front_r_tent_2.addChild(front_r_tent_3);
		main_head.addChild(funnel_left);
		back_r_tent_origin.addChild(back_r_tent_1);
		back_c_tent_origin.addChild(back_c_tent_1);
		back_l_tent_1.addChild(back_l_tent_2);
		front_r_tent_origin.addChild(front_r_tent_1);
		front_c_tent_2.addChild(front_c_tent_3);
		main_base.addChild(front_c_tent_origin);
		main_base.addChild(left_c_tent_origin);
		front_l_tent_origin.addChild(front_l_tent_1);
		back_c_tent_1.addChild(back_c_tent_2);
		back_r_tent_1.addChild(back_r_tent_2);
		front_l_tent_1.addChild(front_l_tent_2);
		right_c_tent_1.addChild(right_c_tent_2);
		back_r_tent_2.addChild(back_r_tent_3);
		front_c_tent_1.addChild(front_c_tent_2);
		right_c_tent_origin.addChild(right_c_tent_1);
		left_c_tent_1.addChild(left_c_tent_2);
		back_l_tent_2.addChild(back_l_tent_3);
		main_head.addChild(funnel);
		main_base.addChild(front_l_tent_origin);
	}

    public void render(EntityBigPuffshroom entity, float partialTickTime) {
    	EntityBigPuffshroom bigPuffsroom = (EntityBigPuffshroom) entity;
		float interAnimationTicks_1 = bigPuffsroom.prev_animation_1 + (bigPuffsroom.animation_1 - bigPuffsroom.prev_animation_1) * partialTickTime;
		float interAnimationTicks_2 = bigPuffsroom.prev_animation_2 + (bigPuffsroom.animation_2 - bigPuffsroom.prev_animation_2) * partialTickTime;
		float interAnimationTicks_3 = bigPuffsroom.prev_animation_3 + (bigPuffsroom.animation_3 - bigPuffsroom.prev_animation_3) * partialTickTime;
		float interAnimationTicks_4 = bigPuffsroom.prev_animation_4 + (bigPuffsroom.animation_4 - bigPuffsroom.prev_animation_4) * partialTickTime;
		float smoothedTicks = bigPuffsroom.prev_renderTicks + (bigPuffsroom.renderTicks - bigPuffsroom.prev_renderTicks) * partialTickTime;
		float flap = MathHelper.sin((smoothedTicks) * 0.325F) * 0.125F;
		float flap2 = MathHelper.cos((smoothedTicks) * 0.325F) * 0.125F;
		float rise = 0F;

		if (bigPuffsroom.animation_1 < 8) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(0F, 0F - interAnimationTicks_1 * 0.0625F, 0F);
			GlStateManager.rotate(0F + interAnimationTicks_1 * 22.5F, 0F, 1F, 0F);
			main_base.render(0.0625F);
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			main_head.render(0.0625F);
			GlStateManager.disableBlend();
			GlStateManager.popMatrix();
		}
		else {
			GlStateManager.pushMatrix();
			if (bigPuffsroom.getSlam()) {
				if (bigPuffsroom.active_4 && bigPuffsroom.pause) {
					rise = interAnimationTicks_4 * 0.025F;
				}
			}
			GlStateManager.translate(0F, 0F - interAnimationTicks_1 * 0.0625F - rise, 0F);
			GlStateManager.rotate(0F + interAnimationTicks_1 * 22.55F, 0F, 1F, 0F);
			main_base.render(0.0625F);

			GlStateManager.pushMatrix();
			if (bigPuffsroom.animation_4 <= 8)
				GlStateManager.scale(1F + interAnimationTicks_4 * 0.125F, 1F, 1F + interAnimationTicks_4 * 0.125F);
			if (bigPuffsroom.animation_4 >= 10)
				GlStateManager.scale(1.75F - interAnimationTicks_4 * 0.0625F, 1F, 1.75F - interAnimationTicks_4 * 0.0625F);

			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			main_head.render(0.0625F);
			GlStateManager.disableBlend();
			GlStateManager.popMatrix();
			
			GlStateManager.popMatrix();
		}
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {	
	}
    
	@Override
    public void setLivingAnimations(EntityLivingBase entity, float swing, float speed, float partialRenderTicks) {
    	EntityBigPuffshroom bigPuffsroom = (EntityBigPuffshroom) entity;
		float interAnimationTicks_1 = bigPuffsroom.prev_animation_1 + (bigPuffsroom.animation_1 - bigPuffsroom.prev_animation_1) * partialRenderTicks;
		float interAnimationTicks_2 = bigPuffsroom.prev_animation_2 + (bigPuffsroom.animation_2 - bigPuffsroom.prev_animation_2) * partialRenderTicks;
		float interAnimationTicks_3 = bigPuffsroom.prev_animation_3 + (bigPuffsroom.animation_3 - bigPuffsroom.prev_animation_3) * partialRenderTicks;
		float interAnimationTicks_4 = bigPuffsroom.prev_animation_4 + (bigPuffsroom.animation_4 - bigPuffsroom.prev_animation_4) * partialRenderTicks;
		float smoothedTicks = bigPuffsroom.prev_renderTicks + (bigPuffsroom.renderTicks - bigPuffsroom.prev_renderTicks)* partialRenderTicks;
		float flap = MathHelper.sin((smoothedTicks) * 0.325F) * 0.125F;
		float flap2 = MathHelper.cos((smoothedTicks) * 0.325F) * 0.125F;

		if (bigPuffsroom.active_1) {
			flap = 0;
			flap2 = 0;
		}
		
		if (bigPuffsroom.getSlam()) {
			if ((bigPuffsroom.active_4 || bigPuffsroom.active_3) && bigPuffsroom.pause) {
				flap = interAnimationTicks_4 * 0.05F;
				flap2 = -interAnimationTicks_4 * 0.05F;
			}
		}

			back_c_tent_1.rotateAngleX = -0.7853981633974483F + interAnimationTicks_2 / (180F / (float) Math.PI) * 11.25F;
			front_c_tent_1.rotateAngleX = -0.7853981633974483F + interAnimationTicks_2 / (180F / (float) Math.PI) * 11.25F;
			left_c_tent_1.rotateAngleX = -0.7853981633974483F + interAnimationTicks_2 / (180F / (float) Math.PI) * 11.25F;
			right_c_tent_1.rotateAngleX = -0.7853981633974483F + interAnimationTicks_2 / (180F / (float) Math.PI) * 11.25F;
			
			back_c_tent_2.rotateAngleX = 0F + interAnimationTicks_2 / (180F / (float) Math.PI);
			front_c_tent_2.rotateAngleX = 0F + interAnimationTicks_2 / (180F / (float) Math.PI);
			left_c_tent_2.rotateAngleX = 0F + interAnimationTicks_2 / (180F / (float) Math.PI);
			right_c_tent_2.rotateAngleX = 0F + interAnimationTicks_2 / (180F / (float) Math.PI);

			back_c_tent_3.rotateAngleX = 0F + interAnimationTicks_2 / (180F / (float) Math.PI);
			front_c_tent_3.rotateAngleX = 0F + interAnimationTicks_2 / (180F / (float) Math.PI);
			left_c_tent_3.rotateAngleX = 0F + interAnimationTicks_2 / (180F / (float) Math.PI);
			right_c_tent_3.rotateAngleX = 0F + interAnimationTicks_2 / (180F / (float) Math.PI);
		

			back_r_tent_1.rotateAngleX = -0.7853981633974483F + interAnimationTicks_3 / (180F / (float) Math.PI) * 11.25F + flap;
			front_r_tent_1.rotateAngleX = -0.7853981633974483F + interAnimationTicks_3 / (180F / (float) Math.PI) * 11.25F - flap2;
			back_l_tent_1.rotateAngleX = -0.7853981633974483F + interAnimationTicks_3 / (180F / (float) Math.PI) * 11.25F - flap2;
			front_l_tent_1.rotateAngleX = -0.7853981633974483F + interAnimationTicks_3 / (180F / (float) Math.PI) * 11.25F + flap;
			
			back_r_tent_2.rotateAngleX = 0F - interAnimationTicks_3 / (180F / (float) Math.PI) * 1.40625F + (!bigPuffsroom.getSlam() ? -flap * 2F :  flap);
			front_r_tent_2.rotateAngleX = 0F - interAnimationTicks_3 / (180F / (float) Math.PI) * 1.40625F + (!bigPuffsroom.getSlam() ? flap2 * 2F : - flap2);
			back_l_tent_2.rotateAngleX = 0F - interAnimationTicks_3 / (180F / (float) Math.PI) * 1.40625F + (!bigPuffsroom.getSlam() ? flap2 * 2F :  -flap2);
			front_l_tent_2.rotateAngleX = 0F - interAnimationTicks_3 / (180F / (float) Math.PI) * 1.40625F + (!bigPuffsroom.getSlam() ? -flap * 2F :  flap);
			
			back_r_tent_3.rotateAngleX = -0.7853981633974483F + (interAnimationTicks_3 - interAnimationTicks_4 * 0.5F) / (180F / (float) Math.PI) * 11.25F + flap * (!bigPuffsroom.getSlam() ? 4F : 2F);
			front_r_tent_3.rotateAngleX = -0.7853981633974483F + (interAnimationTicks_3 - interAnimationTicks_4 * 0.5F) / (180F / (float) Math.PI) * 11.25F - flap2 * (!bigPuffsroom.getSlam() ? 4F :2F);
			back_l_tent_3.rotateAngleX = -0.7853981633974483F + (interAnimationTicks_3 - interAnimationTicks_4 * 0.5F) / (180F / (float) Math.PI) * 11.25F - flap2 * (!bigPuffsroom.getSlam() ? 4F : 2F);
			front_l_tent_3.rotateAngleX = -0.7853981633974483F + (interAnimationTicks_3 - interAnimationTicks_4 * 0.5F) / (180F / (float) Math.PI) * 11.25F + flap * (!bigPuffsroom.getSlam() ? 4F : 2F);
		}

	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}
