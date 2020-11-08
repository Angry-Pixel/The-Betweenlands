package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.ControlledAnimation;
import thebetweenlands.client.render.model.MowzieModelBase;
import thebetweenlands.client.render.model.MowzieModelRenderer;
import thebetweenlands.common.entity.mobs.EntityDarkLight;
import thebetweenlands.common.entity.mobs.EntitySludge;

@SideOnly(Side.CLIENT)
public class ModelDarkLightSkull extends MowzieModelBase {
	private MowzieModelRenderer head1;
	private MowzieModelRenderer head2;
	private MowzieModelRenderer jaw;
	private MowzieModelRenderer teeth;
	private float scale;

	public ModelDarkLightSkull() {
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
		this.teeth = new MowzieModelRenderer(this, 0, 32);
		this.teeth.setRotationPoint(0.0F, -0.07428254352663011F, 0.9972372354295702F);
		this.teeth.addBox(-4.0F, 0.0F, -8.0F, 8, 1, 5, 0.0F);
		this.head1 = new MowzieModelRenderer(this, 0, 0);
		this.head1.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.head1.addBox(-4.0F, -6.0F, -8.0F, 8, 6, 8, 0.0F);
		this.head2.addChild(this.jaw);
		this.head1.addChild(this.teeth);
		this.head2.addChild(this.head1);

		setInitPose();
	}

	public void render() {
		head2.render(0.0625F);
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
		scale = new ControlledAnimation(5).getAnimationProgressSinSqrt(partialTicks);
		float frame = entity.ticksExisted + partialTicks;
		float controller = (float) (0.5 * Math.sin(frame * 0.1f) * Math.sin(frame * 0.1f)) + 0.5f;
		head2.rotationPointY += 1.5f;
		walk(jaw, 0.5f, 0.3f * controller, false, 0, -0.2f * controller, frame, 1f);
		bob(head2, 0.5f, 1f * controller, false, frame, 1f);
		head2.rotationPointX += 2 * Math.sin(frame * 0.25) * controller;
		flap(head2, 0.25f, 0.2f * controller, false, 0, 0, frame, 1f);
	}
}
