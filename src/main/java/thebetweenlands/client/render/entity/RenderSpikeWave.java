package thebetweenlands.client.render.entity;

import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.SpikeRenderer;
import thebetweenlands.common.entity.EntitySpikeWave;

@SideOnly(Side.CLIENT)
public class RenderSpikeWave extends Render<EntitySpikeWave> {
	public RenderSpikeWave(RenderManager rendermanagerIn) {
		super(rendermanagerIn);
	}

	@Override
	public void doRender(EntitySpikeWave entity, double x, double y, double z, float yaw, float partialTicks) {
		if(entity.posY != entity.origin.getY()) {
			renderSpikes(entity, x, y, z, yaw, partialTicks);
		}
	}

	public void renderSpikes(EntitySpikeWave entity, double x, double y, double z, float yaw, float partialTicks) {
		entity.initRootModels();

		if(entity.modelParts != null) {
			this.bindEntityTexture(entity);

			GlStateManager.pushMatrix();

			GlStateManager.enableBlend();
			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

			GlStateManager.translate(x, y, z);

			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder buffer = tessellator.getBuffer();

			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL);

			for(List<SpikeRenderer> renderers : entity.modelParts.values()) {
				for(SpikeRenderer renderer : renderers) {
					renderer.upload(buffer);
				}
			}

			tessellator.draw();

			GlStateManager.popMatrix();
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(EntitySpikeWave entity) {
		return TextureMap.LOCATION_BLOCKS_TEXTURE;
	}
}