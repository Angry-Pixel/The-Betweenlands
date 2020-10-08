package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thebetweenlands.client.render.model.MowzieModelBase;
import thebetweenlands.client.render.model.MowzieModelRenderer;
import thebetweenlands.common.entity.mobs.EntitySludge;

@SideOnly(Side.CLIENT)
public class ModelSludge extends MowzieModelBase {
	private MowzieModelRenderer head1;
	private MowzieModelRenderer head2;
	private MowzieModelRenderer jaw;
	private MowzieModelRenderer teeth;
	private MowzieModelRenderer spine;
	private MowzieModelRenderer spinepiece;
	private MowzieModelRenderer slime1;
	private MowzieModelRenderer slime2;
	private MowzieModelRenderer slime3;
	private float scale;

	public ModelSludge() {
		textureWidth = 128;
		textureHeight = 64;

		this.head2 = new MowzieModelRenderer(this, 0, 16);
		this.head2.setRotationPoint(0.0F, 15.0F, 3.0F);
		this.head2.addBox(-3.0F, 0.0F, -3.0F, 6, 2, 3, -0.01F);
		this.setRotation(head2, -0.07435102760791776F, 0.0F, -0.11154399067163465F);
		this.jaw = new MowzieModelRenderer(this, 0, 22);
		this.jaw.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.jaw.addBox(-4.0F, -1.0F, -8.0F, 8, 2, 7, 0.0F);
		this.setRotation(jaw, 0.5940400797409059F, -0.01100643542294784F, 0.1483095853034341F);
		this.slime2 = new MowzieModelRenderer(this, 40, 32);
		this.slime2.setRotationPoint(0.0F, 15.0F, 0.0F);
		this.slime2.addBox(-7.0F, -9.0F, -7.0F, 14, 2, 14, 0.0F);
		this.teeth = new MowzieModelRenderer(this, 0, 32);
		this.teeth.setRotationPoint(0.0F, -0.07428254352663011F, 0.9972372354295702F);
		this.teeth.addBox(-4.0F, 0.0F, -8.0F, 8, 1, 5, 0.0F);
		this.spine = new MowzieModelRenderer(this, 0, 39);
		this.spine.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.spine.addBox(-1.0F, 0.0F, -2.0F, 2, 3, 2, 0.0F);
		this.setRotation(spine, 0.33414092254743283F, -0.008268694238004389F, 0.11123836025835071F);
		this.slime3 = new MowzieModelRenderer(this, 40, 48);
		this.slime3.setRotationPoint(0.0F, 15.0F, 0.0F);
		this.slime3.addBox(-7.0F, 7.0F, -7.0F, 14, 2, 14, 0.0F);
		this.slime1 = new MowzieModelRenderer(this, 40, 0);
		this.slime1.setRotationPoint(0.0F, 15.0F, 0.0F);
		this.slime1.addBox(-9.0F, -7.0F, -9.0F, 18, 14, 18, 0.0F);
		this.spinepiece = new MowzieModelRenderer(this, 0, 45);
		this.spinepiece.setRotationPoint(0.0F, 3.865302432904631F, -1.0292896104505997F);
		this.spinepiece.addBox(-1.0F, 0.0F, -1.0F, 2, 1, 2, 0.0F);
		this.setRotation(spinepiece, -0.25407912408104716F, 0.056957253174300816F, 0.21579472540118327F);
		this.head1 = new MowzieModelRenderer(this, 0, 0);
		this.head1.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.head1.addBox(-4.0F, -6.0F, -8.0F, 8, 6, 8, 0.0F);
		this.head2.addChild(this.jaw);
		this.head1.addChild(this.teeth);
		this.head2.addChild(this.spine);
		this.spine.addChild(this.spinepiece);
		this.head2.addChild(this.head1);

		setInitPose();
	}

	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel) {
		super.render(entity, limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel);
		setRotationAngles(limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel, entity);
		GlStateManager.pushMatrix();
		GlStateManager.scale(scale, scale, scale);
		head2.render(unitPixel);
		GlStateManager.enableBlend();
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		slime1.render(unitPixel);
		slime2.render(unitPixel);
		slime3.render(unitPixel);
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel, Entity entity) {
		super.setRotationAngles(limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel, entity);
	}

	@Override
	public void setLivingAnimations(EntityLivingBase entity, float f, float f1, float partialTicks) {
		setToInitPose();
		scale = ((EntitySludge) entity).scale.getAnimationProgressSinSqrt(partialTicks);
		float frame = entity.ticksExisted + partialTicks;
		MowzieModelRenderer[] spineParts = new MowzieModelRenderer[]{spine, spinepiece};
		float controller = (float) (0.5 * Math.sin(frame * 0.1f) * Math.sin(frame * 0.1f)) + 0.5f;
		head2.rotationPointY += 1.5f;
		walk(jaw, 1f, 0.3f * controller, false, 0, -0.2f * controller, frame, 1f);
		bob(head2, 0.5f, 1f * controller, false, frame, 1f);
		chainWave(spineParts, 0.5f, 0.2f * controller, -2, frame, 1f);
		chainFlap(spineParts, 0.25f, 0.4f * controller, -2, frame, 1f);
		head2.rotationPointX += 2 * Math.sin(frame * 0.25) * controller;
		flap(head2, 0.25f, 0.2f * controller, false, 0, 0, frame, 1f);
	}
}
