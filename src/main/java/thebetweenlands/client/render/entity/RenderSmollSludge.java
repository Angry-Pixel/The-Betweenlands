package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelSmollSludge;
import thebetweenlands.common.entity.mobs.EntitySludge;

@SideOnly(Side.CLIENT)
public class RenderSmollSludge extends RenderSludge {
	private static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/smoll_sludge.png");

	public RenderSmollSludge(RenderManager renderManager) {
		super(renderManager, new ModelSmollSludge());
	}

	@Override
	protected ResourceLocation getEntityTexture(EntitySludge entity) {
		return TEXTURE;
	}
}
