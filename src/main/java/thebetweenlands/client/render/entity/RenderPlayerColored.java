package thebetweenlands.client.render.entity;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import thebetweenlands.client.render.model.entity.ModelPlayerColored;

public class RenderPlayerColored extends RenderPlayer {
	private final ModelPlayerColored modelColored;

	public RenderPlayerColored(RenderManager renderManager, boolean useSmallArms) {
		super(renderManager, useSmallArms);
		this.mainModel = this.modelColored = new ModelPlayerColored(0.0F, useSmallArms);
		this.layerRenderers.clear();
	}

	@Override
	protected boolean canRenderName(AbstractClientPlayer entity) {
		return false;
	}

	@Override
	public void renderName(AbstractClientPlayer entity, double x, double y, double z) { }

	@Override
	protected boolean setBrightness(AbstractClientPlayer entitylivingbaseIn, float partialTicks, boolean combineTextures) {
		return false;
	}

	@Override
	protected void unsetBrightness() { }

	public void setColor(float r, float g, float b, float a) {
		this.modelColored.setColor(r, g, b, a);
	}
}
