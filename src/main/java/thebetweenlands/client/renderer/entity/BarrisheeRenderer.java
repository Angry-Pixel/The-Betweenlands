package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.BarrisheeModel;
import thebetweenlands.client.renderer.entity.layers.GenericEyesLayer;
import thebetweenlands.client.shader.LightSource;
import thebetweenlands.client.shader.ShaderHelper;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.boss.Barrishee;

public class BarrisheeRenderer extends MobRenderer<Barrishee, BarrisheeModel> {

	private static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/barrishee.png");
	//FIXME for some reason, when this is set to RenderType.eyes, the whole model appears fullbright. I have no idea why
	private static final RenderType EYES = RenderType.entityCutoutNoCull(TheBetweenlands.prefix("textures/entity/barrishee_face.png"));
	private int fudge = 0;

	public BarrisheeRenderer(EntityRendererProvider.Context context) {
		super(context, new BarrisheeModel(context.bakeLayer(BLModelLayers.BARRISHEE)), 1.0F);
		this.addLayer(new GenericEyesLayer<>(this, EYES));
	}

	@Override
	protected void scale(Barrishee entity, PoseStack stack, float partialTick) {
		if (entity.isAmbushSpawn() || entity.isSlamming()) {
			if (entity.isScreaming()) {
				stack.translate(0F, -0.5F + entity.standingAngle * 0.5F - this.getTimerFudge(entity) * 0.00625F - 0.0625F, 0F);
				this.lightUpStuff(entity, partialTick);
			} else {
				stack.translate(0F, -0.5F + entity.standingAngle * 0.5F, 0F);
			}
		} else {
			if (entity.isScreaming()) {
				stack.translate(0F, 0F - this.getTimerFudge(entity) * 0.00625F - 0.0625F, 0F);
				this.lightUpStuff(entity, partialTick);
			}
		}
	}

	public void lightUpStuff(Barrishee entity, float partialTick) {
		if (ShaderHelper.INSTANCE.isWorldShaderActive()) {
			ShaderHelper.INSTANCE.require();
			ShaderHelper.INSTANCE.getWorldShader().addLight(new LightSource(entity.getX(), entity.getY() + 1.25D, entity.getZ(), (this.fudge + partialTick) / 2F + 1F, 4.0F, 102f / 255.0f * 4.0F, 0f / 255.0f * 4.0F));
			ShaderHelper.INSTANCE.getWorldShader().addLight(new LightSource(entity.getX(), entity.getY() + 1.25D, entity.getZ(), (this.getTimerFudge(entity) + partialTick) / 2F + 0.6F, 105f / 255.0f * 4.0F, 26f / 255.0f * 4.0F, 0f / 255.0f * 4.0F));
		}
	}

	public int getTimerFudge(Barrishee entity) {
		if (entity.getScreamTimer() >= 20 && entity.getScreamTimer() <= 30)
			this.fudge = entity.getScreamTimer() - 20;
		if (entity.getScreamTimer() > 30 && entity.getScreamTimer() < 40)
			this.fudge = 10;
		if (entity.getScreamTimer() >= 40)
			this.fudge = -entity.getScreamTimer() + 50;
		return this.fudge;
	}

	@Override
	public ResourceLocation getTextureLocation(Barrishee entity) {
		return TEXTURE;
	}
}
