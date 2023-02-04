package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.mobs.EntitySporeMinion;
@SideOnly(Side.CLIENT)
public class ModelSporeMinion extends ModelBase {
    public ModelRenderer main_base;
    public ModelRenderer front_l_tent_origin;
    public ModelRenderer front_r_tent_origin;
    public ModelRenderer front_c_tent_origin;
    public ModelRenderer back_c_tent_origin;
    public ModelRenderer left_c_tent_origin;
    public ModelRenderer right_c_tent_origin;
    public ModelRenderer back_l_tent_origin;
    public ModelRenderer back_r_tent_origin;
    public ModelRenderer spore_1;
    public ModelRenderer front_l_tent_1;
    public ModelRenderer front_l_tent_2;
    public ModelRenderer front_l_tent_3;
    public ModelRenderer front_r_tent_1;
    public ModelRenderer front_r_tent_2;
    public ModelRenderer front_r_tent_3;
    public ModelRenderer front_c_tent_1;
    public ModelRenderer front_c_tent_2;
    public ModelRenderer front_c_tent_3;
    public ModelRenderer back_c_tent_1;
    public ModelRenderer back_c_tent_2;
    public ModelRenderer back_c_tent_3;
    public ModelRenderer left_c_tent_1;
    public ModelRenderer left_c_tent_2;
    public ModelRenderer left_c_tent_3;
    public ModelRenderer right_c_tent_1;
    public ModelRenderer right_c_tent_2;
    public ModelRenderer right_c_tent_3;
    public ModelRenderer back_l_tent_1;
    public ModelRenderer back_l_tent_2;
    public ModelRenderer back_l_tent_3;
    public ModelRenderer back_r_tent_1;
    public ModelRenderer back_r_tent_2;
    public ModelRenderer back_r_tent_3;
    public ModelRenderer spore_2;
    public ModelRenderer spore_3;
    public ModelRenderer spore_5;
    public ModelRenderer spore_4;
    public ModelRenderer spore_6;

    public ModelSporeMinion() {
    	this.textureWidth = 64;
        this.textureHeight = 64;
        this.spore_4 = new ModelRenderer(this, 0, 28);
        this.spore_4.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.spore_4.addBox(-2.5F, -2.5F, -2.5F, 5, 5, 5, 0.0F);
        this.setRotateAngle(spore_4, 0.0F, 0.7853981633974483F, 0.0F);
        this.back_r_tent_1 = new ModelRenderer(this, 44, 21);
        this.back_r_tent_1.setRotationPoint(0.0F, -1.0F, 0.0F);
        this.back_r_tent_1.addBox(-1.5F, -2.0F, -1.5F, 3, 2, 3, 0.0F);
        this.spore_2 = new ModelRenderer(this, 0, 28);
        this.spore_2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.spore_2.addBox(-2.5F, -2.5F, -2.5F, 5, 5, 5, 0.0F);
        this.setRotateAngle(spore_2, 0.0F, 0.7853981633974483F, 0.0F);
        this.back_c_tent_origin = new ModelRenderer(this, 0, 8);
        this.back_c_tent_origin.setRotationPoint(0.0F, -1.5F, 2.2F);
        this.back_c_tent_origin.addBox(-1.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F);
        this.setRotateAngle(back_c_tent_origin, 0.6981317007977318F, 3.141592653589793F, 0.0F);
        this.back_r_tent_3 = new ModelRenderer(this, 8, 22);
        this.back_r_tent_3.setRotationPoint(0.0F, -3.0F, 0.0F);
        this.back_r_tent_3.addBox(-0.5F, -2.5F, -0.5F, 1, 3, 1, 0.0F);
        this.setRotateAngle(back_r_tent_3, -0.7853981633974483F, 0.0F, 0.0F);
        this.left_c_tent_1 = new ModelRenderer(this, 46, 16);
        this.left_c_tent_1.setRotationPoint(0.0F, -1.0F, 0.0F);
        this.left_c_tent_1.addBox(-1.5F, -2.0F, -1.5F, 3, 2, 3, 0.0F);
        this.front_r_tent_2 = new ModelRenderer(this, 12, 12);
        this.front_r_tent_2.setRotationPoint(0.0F, -2.0F, 0.0F);
        this.front_r_tent_2.addBox(-1.0F, -3.0F, -1.0F, 2, 4, 2, 0.0F);
        this.setRotateAngle(front_r_tent_2, 0.7853981633974483F, 0.0F, 0.0F);
        this.front_l_tent_origin = new ModelRenderer(this, 40, 4);
        this.front_l_tent_origin.setRotationPoint(-2.0F, -1.5F, -2.0F);
        this.front_l_tent_origin.addBox(-1.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F);
        this.setRotateAngle(front_l_tent_origin, 3.141592653589793F, 0.7853981633974483F, 0.0F);
        this.back_l_tent_origin = new ModelRenderer(this, 24, 8);
        this.back_l_tent_origin.setRotationPoint(-2.0F, -1.5F, 2.0F);
        this.back_l_tent_origin.addBox(-1.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F);
        this.setRotateAngle(back_l_tent_origin, 3.141592653589793F, 2.356194490192345F, 0.0F);
        this.front_l_tent_2 = new ModelRenderer(this, 52, 10);
        this.front_l_tent_2.setRotationPoint(0.0F, -2.0F, 0.0F);
        this.front_l_tent_2.addBox(-1.0F, -3.0F, -1.0F, 2, 4, 2, 0.0F);
        this.setRotateAngle(front_l_tent_2, 0.7853981633974483F, 0.0F, 0.0F);
        this.front_l_tent_3 = new ModelRenderer(this, 0, 0);
        this.front_l_tent_3.setRotationPoint(0.0F, -3.0F, 0.0F);
        this.front_l_tent_3.addBox(-0.5F, -2.5F, -0.5F, 1, 3, 1, 0.0F);
        this.setRotateAngle(front_l_tent_3, -0.7853981633974483F, 0.0F, 0.0F);
        this.spore_5 = new ModelRenderer(this, 0, 28);
        this.spore_5.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.spore_5.addBox(-2.5F, -2.5F, -2.5F, 5, 5, 5, 0.0F);
        this.setRotateAngle(spore_5, -0.7853981633974483F, 0.0F, 0.0F);
        this.back_l_tent_1 = new ModelRenderer(this, 35, 18);
        this.back_l_tent_1.setRotationPoint(0.0F, -1.0F, 0.0F);
        this.back_l_tent_1.addBox(-1.5F, -2.0F, -1.5F, 3, 2, 3, 0.0F);
        this.front_c_tent_3 = new ModelRenderer(this, 44, 0);
        this.front_c_tent_3.setRotationPoint(0.0F, -1.0F, 0.0F);
        this.front_c_tent_3.addBox(-0.5F, -1.5F, -0.5F, 1, 2, 1, 0.0F);
        this.setRotateAngle(front_c_tent_3, -0.7853981633974483F, 0.0F, 0.0F);
        this.back_c_tent_2 = new ModelRenderer(this, 30, 16);
        this.back_c_tent_2.setRotationPoint(0.0F, -2.0F, 0.0F);
        this.back_c_tent_2.addBox(-1.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F);
        this.setRotateAngle(back_c_tent_2, -0.7853981633974483F, 0.0F, 0.0F);
        this.right_c_tent_1 = new ModelRenderer(this, 17, 17);
        this.right_c_tent_1.setRotationPoint(0.0F, -1.0F, 0.0F);
        this.right_c_tent_1.addBox(-1.5F, -2.0F, -1.5F, 3, 2, 3, 0.0F);
        this.left_c_tent_origin = new ModelRenderer(this, 8, 8);
        this.left_c_tent_origin.setRotationPoint(-2.2F, -1.5F, 0.0F);
        this.left_c_tent_origin.addBox(-1.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F);
        this.setRotateAngle(left_c_tent_origin, 0.6981317007977318F, 1.5707963267948966F, 0.0F);
        this.spore_6 = new ModelRenderer(this, 0, 28);
        this.spore_6.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.spore_6.addBox(-2.5F, -2.5F, -2.5F, 5, 5, 5, 0.0F);
        this.setRotateAngle(spore_6, 0.0F, 0.7853981633974483F, 0.0F);
        this.front_c_tent_origin = new ModelRenderer(this, 54, 6);
        this.front_c_tent_origin.setRotationPoint(0.0F, -1.5F, -2.2F);
        this.front_c_tent_origin.addBox(-1.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F);
        this.setRotateAngle(front_c_tent_origin, 0.6981317007977318F, 0.0F, 0.0F);
        this.back_c_tent_1 = new ModelRenderer(this, 37, 13);
        this.back_c_tent_1.setRotationPoint(0.0F, -1.0F, 0.0F);
        this.back_c_tent_1.addBox(-1.5F, -2.0F, -1.5F, 3, 2, 3, 0.0F);
        this.front_r_tent_origin = new ModelRenderer(this, 48, 4);
        this.front_r_tent_origin.setRotationPoint(2.0F, -1.5F, -2.0F);
        this.front_r_tent_origin.addBox(-1.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F);
        this.setRotateAngle(front_r_tent_origin, 3.141592653589793F, -0.7853981633974483F, 0.0F);
        this.right_c_tent_3 = new ModelRenderer(this, 59, 15);
        this.right_c_tent_3.setRotationPoint(0.0F, -1.0F, 0.0F);
        this.right_c_tent_3.addBox(-0.5F, -1.5F, -0.5F, 1, 2, 1, 0.0F);
        this.setRotateAngle(right_c_tent_3, -0.7853981633974483F, 0.0F, 0.0F);
        this.main_base = new ModelRenderer(this, 0, 0);
        this.main_base.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.main_base.addBox(-2.5F, -2.51F, -2.5F, 5, 3, 5, 0.0F);
        this.right_c_tent_origin = new ModelRenderer(this, 16, 8);
        this.right_c_tent_origin.setRotationPoint(2.2F, -1.5F, 0.0F);
        this.right_c_tent_origin.addBox(-1.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F);
        this.setRotateAngle(right_c_tent_origin, 0.6981317007977318F, -1.5707963267948966F, 0.0F);
        this.back_c_tent_3 = new ModelRenderer(this, 49, 8);
        this.back_c_tent_3.setRotationPoint(0.0F, -1.0F, 0.0F);
        this.back_c_tent_3.addBox(-0.5F, -1.5F, -0.5F, 1, 2, 1, 0.0F);
        this.setRotateAngle(back_c_tent_3, -0.7853981633974483F, 0.0F, 0.0F);
        this.spore_3 = new ModelRenderer(this, 0, 28);
        this.spore_3.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.spore_3.addBox(-2.5F, -2.5F, -2.5F, 5, 5, 5, 0.0F);
        this.setRotateAngle(spore_3, 0.7853981633974483F, 0.0F, 0.0F);
        this.right_c_tent_2 = new ModelRenderer(this, 8, 18);
        this.right_c_tent_2.setRotationPoint(0.0F, -2.0F, 0.0F);
        this.right_c_tent_2.addBox(-1.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F);
        this.setRotateAngle(right_c_tent_2, -0.7853981633974483F, 0.0F, 0.0F);
        this.front_r_tent_3 = new ModelRenderer(this, 59, 0);
        this.front_r_tent_3.setRotationPoint(0.0F, -3.0F, 0.0F);
        this.front_r_tent_3.addBox(-0.5F, -2.5F, -0.5F, 1, 3, 1, 0.0F);
        this.setRotateAngle(front_r_tent_3, -0.7853981633974483F, 0.0F, 0.0F);
        this.back_l_tent_3 = new ModelRenderer(this, 58, 18);
        this.back_l_tent_3.setRotationPoint(0.0F, -3.0F, 0.0F);
        this.back_l_tent_3.addBox(-0.5F, -2.5F, -0.5F, 1, 3, 1, 0.0F);
        this.setRotateAngle(back_l_tent_3, -0.7853981633974483F, 0.0F, 0.0F);
        this.front_l_tent_1 = new ModelRenderer(this, 40, 8);
        this.front_l_tent_1.setRotationPoint(0.0F, -1.0F, 0.0F);
        this.front_l_tent_1.addBox(-1.5F, -2.0F, -1.5F, 3, 2, 3, 0.0F);
        this.front_r_tent_1 = new ModelRenderer(this, 0, 12);
        this.front_r_tent_1.setRotationPoint(0.0F, -1.0F, 0.0F);
        this.front_r_tent_1.addBox(-1.5F, -2.0F, -1.5F, 3, 2, 3, 0.0F);
        this.left_c_tent_3 = new ModelRenderer(this, 46, 13);
        this.left_c_tent_3.setRotationPoint(0.0F, -1.0F, 0.0F);
        this.left_c_tent_3.addBox(-0.5F, -1.5F, -0.5F, 1, 2, 1, 0.0F);
        this.setRotateAngle(left_c_tent_3, -0.7853981633974483F, 0.0F, 0.0F);
        this.front_c_tent_1 = new ModelRenderer(this, 20, 12);
        this.front_c_tent_1.setRotationPoint(0.0F, -1.0F, 0.0F);
        this.front_c_tent_1.addBox(-1.5F, -2.0F, -1.5F, 3, 2, 3, 0.0F);
        this.left_c_tent_2 = new ModelRenderer(this, 0, 17);
        this.left_c_tent_2.setRotationPoint(0.0F, -2.0F, 0.0F);
        this.left_c_tent_2.addBox(-1.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F);
        this.setRotateAngle(left_c_tent_2, -0.7853981633974483F, 0.0F, 0.0F);
        this.spore_1 = new ModelRenderer(this, 0, 28);
        this.spore_1.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.spore_1.addBox(-2.5F, -2.5F, -2.5F, 5, 5, 5, 0.0F);
        this.back_r_tent_2 = new ModelRenderer(this, 0, 21);
        this.back_r_tent_2.setRotationPoint(0.0F, -2.0F, 0.0F);
        this.back_r_tent_2.addBox(-1.0F, -3.0F, -1.0F, 2, 4, 2, 0.0F);
        this.setRotateAngle(back_r_tent_2, 0.7853981633974483F, 0.0F, 0.0F);
        this.front_c_tent_2 = new ModelRenderer(this, 32, 12);
        this.front_c_tent_2.setRotationPoint(0.0F, -2.0F, 0.0F);
        this.front_c_tent_2.addBox(-1.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F);
        this.setRotateAngle(front_c_tent_2, -0.7853981633974483F, 0.0F, 0.0F);
        this.back_r_tent_origin = new ModelRenderer(this, 32, 8);
        this.back_r_tent_origin.setRotationPoint(2.0F, -1.5F, 2.0F);
        this.back_r_tent_origin.addBox(-1.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F);
        this.setRotateAngle(back_r_tent_origin, 3.141592653589793F, -2.356194490192345F, 0.0F);
        this.back_l_tent_2 = new ModelRenderer(this, 27, 20);
        this.back_l_tent_2.setRotationPoint(0.0F, -2.0F, 0.0F);
        this.back_l_tent_2.addBox(-1.0F, -3.0F, -1.0F, 2, 4, 2, 0.0F);
        this.setRotateAngle(back_l_tent_2, 0.7853981633974483F, 0.0F, 0.0F);
        this.spore_3.addChild(this.spore_4);
        this.back_r_tent_origin.addChild(this.back_r_tent_1);
        this.spore_1.addChild(this.spore_2);
        this.main_base.addChild(this.back_c_tent_origin);
        this.back_r_tent_2.addChild(this.back_r_tent_3);
        this.left_c_tent_origin.addChild(this.left_c_tent_1);
        this.front_r_tent_1.addChild(this.front_r_tent_2);
        this.main_base.addChild(this.front_l_tent_origin);
        this.main_base.addChild(this.back_l_tent_origin);
        this.front_l_tent_1.addChild(this.front_l_tent_2);
        this.front_l_tent_2.addChild(this.front_l_tent_3);
        this.spore_1.addChild(this.spore_5);
        this.back_l_tent_origin.addChild(this.back_l_tent_1);
        this.front_c_tent_2.addChild(this.front_c_tent_3);
        this.back_c_tent_1.addChild(this.back_c_tent_2);
        this.right_c_tent_origin.addChild(this.right_c_tent_1);
        this.main_base.addChild(this.left_c_tent_origin);
        this.spore_5.addChild(this.spore_6);
        this.main_base.addChild(this.front_c_tent_origin);
        this.back_c_tent_origin.addChild(this.back_c_tent_1);
        this.main_base.addChild(this.front_r_tent_origin);
        this.right_c_tent_2.addChild(this.right_c_tent_3);
        this.main_base.addChild(this.right_c_tent_origin);
        this.back_c_tent_2.addChild(this.back_c_tent_3);
        this.spore_1.addChild(this.spore_3);
        this.right_c_tent_1.addChild(this.right_c_tent_2);
        this.front_r_tent_2.addChild(this.front_r_tent_3);
        this.back_l_tent_2.addChild(this.back_l_tent_3);
        this.front_l_tent_origin.addChild(this.front_l_tent_1);
        this.front_r_tent_origin.addChild(this.front_r_tent_1);
        this.left_c_tent_2.addChild(this.left_c_tent_3);
        this.front_c_tent_origin.addChild(this.front_c_tent_1);
        this.left_c_tent_1.addChild(this.left_c_tent_2);
        this.back_r_tent_1.addChild(this.back_r_tent_2);
        this.front_c_tent_1.addChild(this.front_c_tent_2);
        this.main_base.addChild(this.back_r_tent_origin);
        this.back_l_tent_1.addChild(this.back_l_tent_2);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float scale) {
    //    this.main_base.render(scale);
    }
    
    public void renderSpore(float scale) {
    	this.spore_1.render(scale);
    }
    
    public void renderTendrils(float scale) {
    	 this.main_base.render(scale);
    }

    @Override
    public void setLivingAnimations(EntityLivingBase entity, float swing, float speed, float partialRenderTicks) {
    	EntitySporeMinion spore = (EntitySporeMinion) entity;
		float smoothed_1 = spore.animation_1 + (spore.animation_1 - spore.prev_animation_1) * partialRenderTicks;
		float smoothed_2 = spore.animation_2 + (spore.animation_2 - spore.prev_animation_2) * partialRenderTicks;
		//floating
		// origin from 180 to 80 = -1.74533 / 10
		// tent 2 from 45 to 0 = -0.785398/10
		// tent 3 from -45 to -30 = +0.261799 /10

		//walk from float
		// tent 2 from 0 to 45 = +0.785398/10
		// tent 3 from -30 to 55 = +1.48353 /10

		front_l_tent_origin.rotateAngleX = 3.14159F - smoothed_1 * 0.174533F;
		front_r_tent_origin.rotateAngleX = 3.14159F - smoothed_1 * 0.174533F;
		back_l_tent_origin.rotateAngleX = 3.14159F - smoothed_1 * 0.174533F;
		back_r_tent_origin.rotateAngleX = 3.14159F - smoothed_1 * 0.174533F;

		back_r_tent_2.rotateAngleX = 0.785398F - smoothed_1 * 0.0785398F + smoothed_2 * 0.0785398F;
		front_r_tent_2.rotateAngleX = 0.785398F - smoothed_1 *0.0785398F + smoothed_2 * 0.0785398F;
		back_l_tent_2.rotateAngleX = 0.785398F - smoothed_1 * 0.0785398F + smoothed_2 * 0.0785398F;
		front_l_tent_2.rotateAngleX = 0.785398F - smoothed_1 * 0.0785398F + smoothed_2 * 0.0785398F;

		back_r_tent_3.rotateAngleX = -0.785398F + smoothed_1 * 0.0261799F + smoothed_2 * 0.148353F;
		front_r_tent_3.rotateAngleX = -0.785398F + smoothed_1 * 0.0261799F + smoothed_2 * 0.148353F;
		back_l_tent_3.rotateAngleX = -0.785398F + smoothed_1 * 0.0261799F + smoothed_2 * 0.148353F;
		front_l_tent_3.rotateAngleX = -0.785398F + smoothed_1 * 0.0261799F + smoothed_2 * 0.148353F;

		float movementCos = MathHelper.cos(swing * 1.5F + (float) Math.PI) * 1.5F * speed *0.5F;
		float movementSin = MathHelper.sin(swing * 1.5F + (float) Math.PI) * 1.5F * speed *0.5F;

		front_l_tent_origin.rotateAngleZ = movementCos;
		front_r_tent_origin.rotateAngleZ = movementSin;
		back_l_tent_origin.rotateAngleZ = -movementCos;
		back_r_tent_origin.rotateAngleZ = movementSin;

		front_l_tent_origin.rotateAngleY = 0.7853981633974483F - movementSin;
		front_r_tent_origin.rotateAngleY = -0.7853981633974483F + movementCos;
		back_l_tent_origin.rotateAngleY = 2.356194490192345F - movementSin;
		back_r_tent_origin.rotateAngleY = -2.356194490192345F + movementCos;
}

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
