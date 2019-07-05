package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.mobs.EntitySludgeWorm;

@SideOnly(Side.CLIENT)
public class ModelSmolSludgeWorm extends ModelBase {

	public ModelRenderer head1;
	public ModelRenderer mouth_left;
	public ModelRenderer mouth_bottom;
	public ModelRenderer jaw_bottom_left;
	public ModelRenderer jaw_bottom_right;
	public ModelRenderer butt;
    public ModelRenderer pincer_thingy_i_guess_a;
    public ModelRenderer pincer_thingy_i_guess_b;

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

		body1 = new ModelRenderer(this, 0, 15);
		body1.setRotationPoint(0.0F, 21.5F, 0.0F);
		body1.addBox(-2.5F, -2.5F, -2.5F, 5, 5, 5, 0.0F);

		pincer_thingy_i_guess_b = new ModelRenderer(this, 7, 9);
        pincer_thingy_i_guess_b.setRotationPoint(0.0F, 2.0F, 2.0F);
        pincer_thingy_i_guess_b.addBox(-0.5F, -2.0F, 0.0F, 1, 2, 3, 0.0F);
        setRotation(pincer_thingy_i_guess_b, 0.18203784098300857F, 0.0F, 0.0F);
        butt = new ModelRenderer(this, 0, 0);
        butt.setRotationPoint(0.0F, 21.5F, 0.0F);
        butt.addBox(-2.0F, -1.5F, -1.5F, 4, 4, 4, 0.0F);
        pincer_thingy_i_guess_a = new ModelRenderer(this, 0, 9);
        pincer_thingy_i_guess_a.setRotationPoint(0.0F, -0.2F, 2.5F);
        pincer_thingy_i_guess_a.addBox(-0.5F, 0.0F, 0.0F, 1, 2, 2, 0.0F);
        setRotation(pincer_thingy_i_guess_a, -0.22759093446006054F, 0.0F, 0.0F);
        pincer_thingy_i_guess_a.addChild(pincer_thingy_i_guess_b);
        butt.addChild(pincer_thingy_i_guess_a);
	}

	public void renderHead(EntitySludgeWorm worm, int frame, float wibbleStrength, float partialTicks) {
		float smoothedTicks = worm.ticksExisted + frame + (worm.ticksExisted + frame - (worm.ticksExisted + frame - 1)) * partialTicks;
		float wibble = MathHelper.sin(1F + (smoothedTicks) * 0.25F) * 0.125F * wibbleStrength;
		float jaw_wibble = MathHelper.sin(1F + (smoothedTicks) * 0.5F) * 0.5F;
		GlStateManager.translate(0F, - 0.0625F - wibble * 0.5F, 0F + wibble * 2F);
		head1.render(0.0625F);
		head1.rotateAngleX = worm.rotationPitch / (180F / (float) Math.PI);
	    jaw_bottom_left.rotateAngleY =  0F - jaw_wibble;
	    jaw_bottom_right.rotateAngleY = 0F + jaw_wibble;
	    mouth_bottom.rotateAngleY =  0F - jaw_wibble;
	    mouth_left.rotateAngleY = 0F + jaw_wibble;
	}

	public void renderBody(EntitySludgeWorm worm, int frame, float wibbleStrength, float partialTicks) {
		float smoothedTicks = worm.ticksExisted + frame + (worm.ticksExisted + frame - (worm.ticksExisted + frame - 1)) * partialTicks;
		float wibble = MathHelper.sin(1F + (smoothedTicks) * 0.25F) * 0.125F * wibbleStrength;
		GlStateManager.translate(0F, 0F - wibble, 0F - wibble * 2F);
		GlStateManager.scale(1F + wibble * 2F, 1F + wibble, 1.25F - wibble * 1.5F);
		body1.render(0.0625F);
	}
	
	public void renderTail(EntitySludgeWorm worm, int frame, float wibbleStrength, float partialTicks) {
		float smoothedTicks = worm.ticksExisted + frame + (worm.ticksExisted + frame - (worm.ticksExisted + frame - 1)) * partialTicks;
		float wibble = MathHelper.sin(1F + (smoothedTicks) * 0.25F) * 0.125F * wibbleStrength;
		GlStateManager.translate(0F, - 0.0625F - wibble * 0.5F, - 0.0625F + wibble * 2F);
		butt.render(0.0625F);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

}
