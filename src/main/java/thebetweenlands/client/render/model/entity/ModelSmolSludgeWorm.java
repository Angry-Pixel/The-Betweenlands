package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.mobs.EntitySmolSludgeWorm;

@SideOnly(Side.CLIENT)
public class ModelSmolSludgeWorm extends ModelBase {

	public ModelRenderer head1;
	public ModelRenderer mouth_left;
	public ModelRenderer mouth_bottom;
	public ModelRenderer jaw_bottom_left;
	public ModelRenderer jaw_bottom_right;

	public ModelRenderer body1;

	public ModelSmolSludgeWorm() {
		textureWidth = 32;
		textureHeight = 32;
		jaw_bottom_right = new ModelRenderer(this, 11, 18);
		jaw_bottom_right.setRotationPoint(-1.5F, 1.0F, -2.5F);
		jaw_bottom_right.addBox(-0.5F, 0.0F, -3.5F, 1, 2, 4, 0.0F);
		setRotation(jaw_bottom_right, 0.136659280431156F, 0.0F, 0.7740535232594852F);
		mouth_left = new ModelRenderer(this, 0, 11);
		mouth_left.setRotationPoint(2.0F, -0.5F, -2.5F);
		mouth_left.addBox(-2.0F, -1.5F, -2.0F, 2, 3, 3, 0.0F);
		setRotation(mouth_left, 0.0F, -0.36425021489121656F, -0.22759093446006054F);
		jaw_bottom_left = new ModelRenderer(this, 0, 18);
		jaw_bottom_left.setRotationPoint(1.5F, 1.0F, -2.5F);
		jaw_bottom_left.addBox(-0.5F, 0.0F, -3.5F, 1, 2, 4, 0.0F);
		setRotation(jaw_bottom_left, 0.136659280431156F, 0.0F, -0.7740535232594852F);
		head1 = new ModelRenderer(this, 0, 0);
		head1.setRotationPoint(0.0F, 21.5F, 0.0F);
		head1.addBox(-2.5F, -2.5F, -2.5F, 5, 5, 5, 0.0F);
		mouth_bottom = new ModelRenderer(this, 13, 11);
		mouth_bottom.setRotationPoint(-2.0F, -0.5F, -2.5F);
		mouth_bottom.addBox(0.0F, -1.5F, -2.0F, 2, 3, 3, 0.0F);
		setRotation(mouth_bottom, 0.0F, 0.36425021489121656F, 0.22759093446006054F);
		head1.addChild(jaw_bottom_right);
		head1.addChild(mouth_left);
		head1.addChild(jaw_bottom_left);
		head1.addChild(mouth_bottom);

		body1 = new ModelRenderer(this, 0, 0);
		body1.setRotationPoint(0.0F, 21.5F, 0.0F);
		body1.addBox(-2.5F, -2.5F, -2.5F, 5, 5, 5, 0.0F);

	}

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel) {
        super.render(entity, limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel);
        setRotationAngles(limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel, entity);
        head1.render(unitPixel);
    }

	//public void renderHead() {
	//	head1.render(0.0625F);
	//}

	public void renderBody(EntitySmolSludgeWorm worm, int frame, float partialTicks) {
		float smoothedTicks = worm.ticksExisted + frame + (worm.ticksExisted + frame - (worm.ticksExisted + frame - 1)) * partialTicks;
		float wibble = MathHelper.sin(1F + (smoothedTicks) * 0.25F) * 0.125F;
		GlStateManager.translate(0F, 0F - wibble, 0F);
		GlStateManager.scale(1F + wibble * 2F, 1F + wibble, 1F);
		body1.render(0.0625F);
	}
	
	@Override
	public void setRotationAngles(float limbSwing, float prevLimbSwing, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel, Entity entity) {
		head1.rotateAngleY = rotationYaw / (180F / (float) Math.PI);
		head1.rotateAngleX = rotationPitch / (180F / (float) Math.PI);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

}
