package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.EntityMistBridge;

@SideOnly(Side.CLIENT)
public class RenderMistBridge extends Render<EntityMistBridge> {

	public RenderMistBridge(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(EntityMistBridge entity, double x, double y, double z, float yaw, float partialTicks) {
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityMistBridge entity) {
		return null;
	}
}
