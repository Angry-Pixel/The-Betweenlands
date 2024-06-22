package thebetweenlands.client.rendering.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import org.lwjgl.opengl.GL30;

public class AdvancedModelPart {
	public ModelPart modelpart;
	public float scalex = 1;
	public float scaley = 1;
	public float scalez = 1;

	public AdvancedModelPart() {

	}

	public AdvancedModelPart(ModelPart modelpart) {
		this.modelpart = modelpart;
	}

	public void render(PoseStack posestack, VertexConsumer vertconsumer, int overlaycoords, int uv2) {
		this.render(posestack, vertconsumer, overlaycoords, uv2, 1.0f, 1.0f, 1.0f, 1.0f);
	}

	public void render(PoseStack posestack, VertexConsumer vertconsumer, int overlaycoords, int uv2, float R, float G, float B, float A) {
		// lets see if this works...
		GL30.glScalef(this.scalex, this.scaley, this.scalez);
		this.modelpart.render(posestack, vertconsumer, overlaycoords, uv2, R, G, B, A);
	}

}
