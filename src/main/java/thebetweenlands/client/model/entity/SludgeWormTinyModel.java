package thebetweenlands.client.model.entity;
/*
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.mobs.EntityTinySludgeWorm;

@SideOnly(Side.CLIENT)
public class ModelTinySludgeWorm extends ModelBase {

	ModelRenderer head;
	ModelRenderer beak_right;
	ModelRenderer beak_left;
	ModelRenderer dat_detailed_hot_bod;
	ModelRenderer cute_lil_butt;
	ModelRenderer spoopy_stinger;

	public ModelTinySludgeWorm() {
		textureWidth = 32;
		textureHeight = 32;
		head = new ModelRenderer(this, 0, 0);
		head.setRotationPoint(0.0F, 22.5F, 0.0F);
		head.addBox(-1.5F, -1.5F, -1.5F, 3, 3, 3, 0.0F);
		beak_left = new ModelRenderer(this, 0, 14);
		beak_left.setRotationPoint(1.5F, 0.5F, -1.5F);
		beak_left.addBox(-2.0F, -2.0F, -2.0F, 2, 3, 3, 0.0F);
		setRotation(beak_left, 0.0F, -0.31869712141416456F, 0.0F);
		beak_right = new ModelRenderer(this, 0, 7);
		beak_right.setRotationPoint(-1.5F, 0.0F, -1.5F);
		beak_right.addBox(0.0F, -1.5F, -2.0F, 2, 3, 3, 0.0F);
		setRotation(beak_right, 0.0F, 0.31869712141416456F, 0.0F);

		dat_detailed_hot_bod = new ModelRenderer(this, 13, 0);
		dat_detailed_hot_bod.setRotationPoint(0.0F, 22.5F, 0.0F);
		dat_detailed_hot_bod.addBox(-1.5F, -1.5F, -1.5F, 3, 3, 3, 0.0F);

		cute_lil_butt = new ModelRenderer(this, 13, 7);
		cute_lil_butt.setRotationPoint(0.0F, 23.0F, 0.0F);
		cute_lil_butt.addBox(-1.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F);
		spoopy_stinger = new ModelRenderer(this, 13, 11);
		spoopy_stinger.setRotationPoint(0.0F, -1.3F, 1.0F);
		spoopy_stinger.addBox(-0.5F, 0.0F, 0.0F, 1, 2, 2, 0.0F);
		setRotation(spoopy_stinger, -0.18203784098300857F, 0.0F, 0.0F);

		head.addChild(beak_left);
		head.addChild(beak_right);
		cute_lil_butt.addChild(spoopy_stinger);
	}

	public void renderHead(EntityTinySludgeWorm worm, int frame, float wibbleStrength, float partialTicks) {
		float smoothedTicks = worm.ticksExisted + frame + (worm.ticksExisted + frame - (worm.ticksExisted + frame - 1)) * partialTicks;
		float wibble = MathHelper.sin(1F + (smoothedTicks) * 0.25F) * 0.125F * wibbleStrength;
		float jaw_wibble = MathHelper.sin(1F + (smoothedTicks) * 0.5F) * 0.5F;
		GlStateManager.translate(0F, -0.0625F - wibble * 0.5F, 0F + wibble * 2F);
		head.render(0.0625F);
		head.rotateAngleX = worm.rotationPitch / (180F / (float) Math.PI);
		beak_left.rotateAngleY = 0F - jaw_wibble;
		beak_right.rotateAngleY = 0F + jaw_wibble;
	}

	public void renderBody(EntityTinySludgeWorm worm, int frame, float wibbleStrength, float partialTicks) {
		float smoothedTicks = worm.ticksExisted + frame + (worm.ticksExisted + frame - (worm.ticksExisted + frame - 1)) * partialTicks;
		float wibble = MathHelper.sin(1F + (smoothedTicks) * 0.25F) * 0.125F * wibbleStrength;
		GlStateManager.translate(0F, -0.125F - wibble, 0F - wibble * 2F);
		GlStateManager.scale(1F + wibble * 2F, 1F + wibble, 1.25F - wibble * 1.5F);
		dat_detailed_hot_bod.render(0.0625F);
	}

	public void renderTail(EntityTinySludgeWorm worm, int frame, float wibbleStrength, float partialTicks) {
		float smoothedTicks = worm.ticksExisted + frame + (worm.ticksExisted + frame - (worm.ticksExisted + frame - 1)) * partialTicks;
		float wibble = MathHelper.sin(1F + (smoothedTicks) * 0.25F) * 0.125F * wibbleStrength;
		GlStateManager.translate(0F, -0.0625F - wibble * 0.5F, -0.0625F + wibble * 2F);
		cute_lil_butt.render(0.0625F);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
}*/