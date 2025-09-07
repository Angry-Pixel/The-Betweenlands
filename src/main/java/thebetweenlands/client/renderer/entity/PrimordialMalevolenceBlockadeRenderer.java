package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.client.renderer.BLRenderTypes;
import thebetweenlands.common.entity.boss.malevolence.PrimordialMalevolenceBlockade;

public class PrimordialMalevolenceBlockadeRenderer extends EntityRenderer<PrimordialMalevolenceBlockade> {

	public PrimordialMalevolenceBlockadeRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void render(PrimordialMalevolenceBlockade entity, float entityYaw, float partialTicks, PoseStack stack, MultiBufferSource buffer, int light) {

		Vec3[] vertices = entity.getTriangleVertices(partialTicks);

		stack.pushPose();
		stack.translate(0.0D, 0.2D, 0.0D);

		float ticks = (float) entity.tickCount + partialTicks;
		PoseStack.Pose pose = stack.last();
		stack.pushPose();
		float uOffset = (ticks * 0.01F) % 1.0F;
		float vOffset = (ticks * 0.01F) % 1.0F;
		float textureScale = 4.0F;
		float cu = textureScale / 2.0F;
		float cv = textureScale * Mth.sqrt(2) / 2.0F;
		int layers = 8;
		for (int l = 0; l < layers; l++) {
			float cos = Mth.cos(2.0F * Mth.PI / layers * l);
			float sin = 1 + Mth.sin(2.0F * Mth.PI / layers * l);
			float tu1 = 0.0F - cu;
			float tv1 = 0.0F - cv;
			float tu2 = textureScale / 2.0F / layers * l - cu;
			float tv2 = textureScale / layers * l * Mth.sqrt(2) - cv;
			float tu3 = textureScale / layers * l - cu;
			float tv3 = 0.0F - cv;
			tu1 = cu + tu1 * sin;
			tv1 = cv + tv1 * cos;
			tu2 = cu + tu2 * sin;
			tv2 = cv + tv2 * cos;
			tu3 = cu + tu3 * sin;
			tv3 = cv + tv3 * cos;
			VertexConsumer consumer = buffer.getBuffer(BLRenderTypes.primordialShield(PrimordialMalevolenceRenderer.SHIELD_TEXTURE, uOffset, vOffset, true));
			consumer.addVertex(pose, (float) vertices[0].x, (float) vertices[0].y, (float) vertices[0].z).setUv(tu1, tv1).setColor(0.5F, 0.6F, 1F, 0.5F);
			consumer.addVertex(pose, (float) vertices[1].x, (float) vertices[1].y, (float) vertices[1].z).setUv(tu2, tv2).setColor(0.5F, 0.6F, 1F, 0.5F);
			consumer.addVertex(pose, (float) vertices[2].x, (float) vertices[2].y, (float) vertices[2].z).setUv(tu3, tv3).setColor(0.5F, 0.6F, 1F, 0.5F);
		}

		stack.popPose();

		VertexConsumer consumer = buffer.getBuffer(BLRenderTypes.primordialShieldFiller());
		consumer.addVertex(pose, (float) vertices[0].x, (float) vertices[0].y, (float) vertices[0].z).setColor(0.5F, 0.6F, 1F, 0.5F);
		consumer.addVertex(pose, (float) vertices[1].x, (float) vertices[1].y, (float) vertices[1].z).setColor(0.5F, 0.6F, 1F, 0.5F);
		consumer.addVertex(pose, (float) vertices[2].x, (float) vertices[2].y, (float) vertices[2].z).setColor(0.5F, 0.6F, 1F, 0.5F);

		consumer = buffer.getBuffer(RenderType.lines());
		consumer.addVertex(pose, (float) vertices[0].x, (float) vertices[0].y, (float) vertices[0].z).setColor(0.5F, 0.75F, 1F, 1.0F).setNormal(pose, 0.0F, 1.0F, 0.0F);
		consumer.addVertex(pose, (float) vertices[1].x, (float) vertices[1].y, (float) vertices[1].z).setColor(0.5F, 0.75F, 1F, 1.0F).setNormal(pose, 0.0F, 1.0F, 0.0F);
		consumer.addVertex(pose, (float) vertices[1].x, (float) vertices[1].y, (float) vertices[1].z).setColor(0.5F, 0.75F, 1F, 1.0F).setNormal(pose, 0.0F, 1.0F, 0.0F);
		consumer.addVertex(pose, (float) vertices[2].x, (float) vertices[2].y, (float) vertices[2].z).setColor(0.5F, 0.75F, 1F, 1.0F).setNormal(pose, 0.0F, 1.0F, 0.0F);
		consumer.addVertex(pose, (float) vertices[2].x, (float) vertices[2].y, (float) vertices[2].z).setColor(0.5F, 0.75F, 1F, 1.0F).setNormal(pose, 0.0F, 1.0F, 0.0F);
		consumer.addVertex(pose, (float) vertices[0].x, (float) vertices[0].y, (float) vertices[0].z).setColor(0.5F, 0.75F, 1F, 1.0F).setNormal(pose, 0.0F, 1.0F, 0.0F);

		stack.popPose();
	}

	@Override
	public ResourceLocation getTextureLocation(PrimordialMalevolenceBlockade entity) {
		return PrimordialMalevolenceRenderer.SHIELD_TEXTURE;
	}
}


