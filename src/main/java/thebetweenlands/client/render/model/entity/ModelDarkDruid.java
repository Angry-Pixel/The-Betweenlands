package thebetweenlands.client.render.model.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.mobs.EntityDarkDruid;
import thebetweenlands.util.MathUtils;

@SideOnly(Side.CLIENT)
public class ModelDarkDruid extends ModelBase {
	private final ModelRenderer beard;
	private final ModelRenderer rightArm;
	private final ModelRenderer rightCuff;
	private final ModelRenderer body;
	private final ModelRenderer leftArm;
	private final ModelRenderer leftCuff;
	private final ModelRenderer head;
	private final ModelRenderer hoodLeftTop;
	private final ModelRenderer hoodLeft;
	private final ModelRenderer hoodRightTop;
	private final ModelRenderer hoodRight;
	private final ModelRenderer liripipe1;
	private final ModelRenderer liripipe2;
	private final ModelRenderer liripipe3;
	private final ModelRenderer robe1;
	private final ModelRenderer robe2;
	private final ModelRenderer robe3;
	private final ModelRenderer robe4;
	private final ModelRenderer robe5;
	private final ModelRenderer robe6;
	private final ModelRenderer robe7;

	public ModelDarkDruid() {
		textureWidth = 64;
		textureHeight = 256;

		beard = new ModelRenderer(this, 22, 104);
		beard.addBox(-1.5F, -1F, -4.5F, 3, 2, 1);
		beard.setRotationPoint(0F, 0F, 0F);
		setRotation(beard, 0F, 0F, 0F);
		rightArm = new ModelRenderer(this, 0, 92);
		rightArm.addBox(-5F, -2F, -2F, 5, 12, 5);
		rightArm.setRotationPoint(-3F, 2F, 0F);
		setRotation(rightArm, 0F, 0F, 0F);
		rightCuff = new ModelRenderer(this, 0, 84);
		rightCuff.addBox(-5.5F, 10F, -2.5F, 6, 2, 6);
		rightCuff.setRotationPoint(-3F, 2F, 0F);
		setRotation(rightCuff, 0F, 0F, 0F);
		body = new ModelRenderer(this, 32, 107);
		body.addBox(-4F, 0F, -3F, 8, 13, 8);
		body.setRotationPoint(0F, 0F, 0F);
		setRotation(body, 0F, 0F, 0F);
		leftArm = new ModelRenderer(this, 0, 92);
		leftArm.addBox(0F, -2F, -2F, 5, 12, 5);
		leftArm.setRotationPoint(3F, 2F, 0F);
		setRotation(leftArm, 0F, 0F, 0F);
		leftCuff = new ModelRenderer(this, 0, 84);
		leftCuff.addBox(-0.5F, 10F, -2.5F, 6, 2, 6);
		leftCuff.setRotationPoint(3F, 2F, 0F);
		setRotation(leftCuff, 0F, 0F, 0F);
		head = new ModelRenderer(this, 30, 90);
		head.addBox(-4F, -8F, -4F, 8, 8, 9);
		head.setRotationPoint(0F, 0F, 0F);
		setRotation(head, 0F, 0F, 0F);
		hoodLeftTop = new ModelRenderer(this, 0, 54);
		hoodLeftTop.addBox(-4.5F, -15F, -4.5F, 4, 1, 9);
		hoodLeftTop.setRotationPoint(0F, 6F, 0F);
		setRotation(hoodLeftTop, 0F, 0F, 0.296706F);
		hoodLeft = new ModelRenderer(this, 0, 64);
		hoodLeft.addBox(4F, -8F, -4.5F, 1, 11, 9);
		hoodLeft.setRotationPoint(0F, 0F, 0F);
		setRotation(hoodLeft, 0F, 0F, -0.122173F);
		hoodRightTop = new ModelRenderer(this, 0, 54);
		hoodRightTop.addBox(0.5F, -15F, -4.5F, 4, 1, 9);
		hoodRightTop.setRotationPoint(0F, 6F, 0F);
		setRotation(hoodRightTop, 0F, 0F, -0.296706F);
		hoodRight = new ModelRenderer(this, 0, 64);
		hoodRight.addBox(-5F, -8F, -4.5F, 1, 11, 9);
		hoodRight.setRotationPoint(0F, 0F, 0F);
		setRotation(hoodRight, 0F, 0F, 0.122173F);
		liripipe1 = new ModelRenderer(this, 58, 76);
		liripipe1.addBox(-1F, -0.5F, -0.5F, 2, 6, 1);
		liripipe1.setRotationPoint(0F, 12F, 6F);
		setRotation(liripipe1, 0F, 0F, 0F);
		liripipe2 = new ModelRenderer(this, 54, 63);
		liripipe2.addBox(-2F, -0.5F, -0.5F, 4, 12, 1);
		liripipe2.setRotationPoint(0F, 0F, 6F);
		setRotation(liripipe2, 0F, 0F, 0F);
		liripipe3 = new ModelRenderer(this, 50, 54);
		liripipe3.addBox(-3F, -0.5F, -0.5F, 6, 8, 1);
		liripipe3.setRotationPoint(0F, -8F, 4.5F);
		setRotation(liripipe3, 0.1745329F, 0F, 0F);
		robe1 = new ModelRenderer(this, 0, 0);
		robe1.addBox(-4F, 0F, -3F, 8, 5, 8);
		robe1.setRotationPoint(0F, 13F, 0F);
		setRotation(robe1, 0F, 0F, 0F);
		robe2 = new ModelRenderer(this, 0, 13);
		robe2.addBox(-4.5F, 0F, -3.5F, 9, 2, 10);
		robe2.setRotationPoint(0F, 18F, 0F);
		setRotation(robe2, 0F, 0F, 0F);
		robe3 = new ModelRenderer(this, 0, 35);
		robe3.addBox(-5F, 0F, -4F, 10, 1, 15);
		robe3.setRotationPoint(0F, 20F, 0F);
		setRotation(robe3, 0F, 0F, 0F);
		robe4 = new ModelRenderer(this, 0, 132);
		robe4.addBox(-5.5F, 0F, -4.5F, 11, 1, 18);
		robe4.setRotationPoint(0F, 21F, 0F);
		setRotation(robe4, 0F, 0F, 0F);
		robe5 = new ModelRenderer(this, 0, 13);
		robe5.addBox(-3F, 1F, 6.5F, 6, 1, 2);
		robe5.setRotationPoint(0F, 18F, 0F);
		setRotation(robe5, 0F, 0F, 0F);
		robe6 = new ModelRenderer(this, 0, 25);
		robe6.addBox(-3F, 1F, 11F, 6, 1, 1);
		robe6.setRotationPoint(0F, 19F, 0F);
		setRotation(robe6, 0F, 0F, 0F);
		robe7 = new ModelRenderer(this, 0, 28);
		robe7.addBox(-4F, 0F, 13.5F, 8, 1, 2);
		robe7.setRotationPoint(0F, 21F, 0F);
		setRotation(robe7, 0F, 0F, 0F);
	}

	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAngle, float ticksExisted, float rotationYaw, float rotationPitch, float scale) {
		GL11.glPushMatrix();
		GL11.glTranslated(0, 0.15D, 0);
		setRotationAngles(limbSwing, limbSwingAngle, ticksExisted, rotationYaw, rotationPitch, scale, entity);
		beard.render(scale);
		rightArm.render(scale);
		rightCuff.render(scale);
		body.render(scale);
		leftArm.render(scale);
		leftCuff.render(scale);
		head.render(scale);
		hoodLeftTop.render(scale);
		hoodLeft.render(scale);
		hoodRightTop.render(scale);
		hoodRight.render(scale);
		liripipe1.render(scale);
		liripipe2.render(scale);
		liripipe3.render(scale);
		robe1.render(scale);
		robe2.render(scale);
		robe3.render(scale);
		robe4.render(scale);
		robe5.render(scale);
		robe6.render(scale);
		robe7.render(scale);
		GL11.glPopMatrix();
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	@Override
	public void setLivingAnimations(EntityLivingBase entity, float swing, float speed, float partialRenderTicks) {
		EntityDarkDruid druid = (EntityDarkDruid) entity;
		float attackAnimationTime = druid.getAttackAnimationTime(partialRenderTicks);
		float pitch = -MathUtils.PI / 2 + (entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialRenderTicks) * MathUtils.DEG_TO_RAD;
		float ticksExisted = entity.ticksExisted + partialRenderTicks;
		rightCuff.rotateAngleX = rightArm.rotateAngleX = pitch * attackAnimationTime + MathHelper.cos(swing * 0.6662F + MathUtils.PI) * 2 * speed * 0.5F * (1 - attackAnimationTime);
		leftCuff.rotateAngleX = leftArm.rotateAngleX = pitch * attackAnimationTime + MathHelper.cos(swing * 0.6662F) * 2.0F * speed * 0.5F * (1 - attackAnimationTime);
		rightCuff.rotateAngleX = rightArm.rotateAngleX += MathHelper.sin(ticksExisted * 0.4F) * 0.1F * attackAnimationTime;
		leftCuff.rotateAngleX = leftArm.rotateAngleX += MathHelper.cos(ticksExisted * 0.4F) * 0.1F * attackAnimationTime;
		rightCuff.rotateAngleZ = rightArm.rotateAngleZ = -MathHelper.sin(ticksExisted * 0.2F) * 0.1F * attackAnimationTime;
		leftCuff.rotateAngleZ = leftArm.rotateAngleZ = MathHelper.cos(ticksExisted * 0.2F) * 0.1F * attackAnimationTime;
	}
}
