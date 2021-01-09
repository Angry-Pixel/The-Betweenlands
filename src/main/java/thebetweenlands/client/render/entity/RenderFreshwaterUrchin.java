package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelFreshwaterUrchin;
import thebetweenlands.common.entity.mobs.EntityFreshwaterUrchin;
import thebetweenlands.common.lib.ModInfo;

@SideOnly(Side.CLIENT)
public class RenderFreshwaterUrchin extends RenderLiving<EntityFreshwaterUrchin> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.ID, "textures/entity/freshwater_urchin.png");

	public RenderFreshwaterUrchin(RenderManager renderManager) {
		super(renderManager, new ModelFreshwaterUrchin(), 0.2F);
	}

	@Override
	protected void preRenderCallback(EntityFreshwaterUrchin entity, float partialTickTime) {
		// may need some GL scaling here
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityFreshwaterUrchin entity) {
		return TEXTURE;
	}
}
