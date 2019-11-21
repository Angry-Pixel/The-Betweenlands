package thebetweenlands.client.render.entity.layer;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import thebetweenlands.api.capability.IPuppetCapability;
import thebetweenlands.common.registries.CapabilityRegistry;

public class LayerPuppetOverlay extends LayerAnimatedOverlay<EntityLivingBase> {
	public static final ResourceLocation OVERLAY_TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/ring_of_recruitment_overlay.png");

	private final FloatBuffer buffer4f = GLAllocation.createDirectFloatBuffer(16);

	public LayerPuppetOverlay(RenderLivingBase<EntityLivingBase> renderer) {
		super(renderer, OVERLAY_TEXTURE);
	}

	private FloatBuffer getBuffer4f(float v1, float v2, float v3, float v4) {
		this.buffer4f.clear();
		this.buffer4f.put(v1).put(v2).put(v3).put(v4);
		this.buffer4f.flip();
		return this.buffer4f;
	}

	@Override
	public void doRenderLayer(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		IPuppetCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_PUPPET, null);
		if(cap != null && cap.hasPuppeteer()) {
			GlStateManager.pushMatrix();
			GlStateManager.enableBlend();
			GlStateManager.enableAlpha();
			GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0f);
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
			GlStateManager.depthMask(false);
			GlStateManager.enableCull();

			float animationTimer = (entity.ticksExisted + partialTicks);

			float colorSine = (MathHelper.cos(animationTimer * 0.1f) + 1) * 0.3f;

			float alpha = 0.1f * (MathHelper.sin(animationTimer * 0.0333333f) + 1) * 0.5f + 0.08f;

			GlStateManager.color(colorSine, 1.25f - colorSine, 2.0f, alpha);

			GlStateManager.enablePolygonOffset();
			GlStateManager.doPolygonOffset(-0.1F, -5.0F);


			this.renderer.bindTexture(OVERLAY_TEXTURE);

			//Set up UV generator
			GlStateManager.texGen(GlStateManager.TexGen.S, GL11.GL_OBJECT_LINEAR);
			GlStateManager.texGen(GlStateManager.TexGen.T, GL11.GL_OBJECT_LINEAR);
			GlStateManager.texGen(GlStateManager.TexGen.S, GL11.GL_OBJECT_PLANE, this.getBuffer4f(0.0F, 1.0F, 0.0F, 0.0F));
			GlStateManager.texGen(GlStateManager.TexGen.T, GL11.GL_OBJECT_PLANE, this.getBuffer4f(1.0F, 0.0F, 1.0F, 0.0F));
			GlStateManager.enableTexGenCoord(GlStateManager.TexGen.S);
			GlStateManager.enableTexGenCoord(GlStateManager.TexGen.T);

			GlStateManager.matrixMode(GL11.GL_TEXTURE);
			GlStateManager.pushMatrix();
			GlStateManager.loadIdentity();
			GlStateManager.translate(0.5F, 0.5F, 0.0F);

			GlStateManager.scale(0.25f, 0.25f, 1);

			GlStateManager.translate(animationTimer * 0.01f, -animationTimer * 0.015f, 0);
			GlStateManager.rotate(animationTimer * 0.25f, 0, 0, 1);

			GlStateManager.matrixMode(GL11.GL_MODELVIEW);

			//Render projection
			ModelBase mainModel = this.renderer.getMainModel();
			mainModel.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTicks);
			mainModel.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
			mainModel.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

			GlStateManager.matrixMode(GL11.GL_TEXTURE); //Make sure texture matrix is popped
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(GL11.GL_MODELVIEW);

			GlStateManager.disableTexGenCoord(GlStateManager.TexGen.S);
			GlStateManager.disableTexGenCoord(GlStateManager.TexGen.T);



			GlStateManager.matrixMode(GL11.GL_TEXTURE);
			GlStateManager.loadIdentity();
			GlStateManager.matrixMode(GL11.GL_MODELVIEW);


			GlStateManager.disablePolygonOffset();

			GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1f);
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.disableCull();
			GlStateManager.depthMask(true);
			GlStateManager.disableBlend();
			GlStateManager.popMatrix();
		}
	}
}
