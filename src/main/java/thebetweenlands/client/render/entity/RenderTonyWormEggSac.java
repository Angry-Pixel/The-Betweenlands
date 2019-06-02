package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.render.model.entity.ModelTonyWormEggSac;
import thebetweenlands.common.entity.EntityTonyWormEggSac;
import thebetweenlands.common.lib.ModInfo;

public class RenderTonyWormEggSac extends RenderLiving<EntityTonyWormEggSac> {
	public static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.ID, "textures/entity/worm_egg_sac.png");
	private final ModelTonyWormEggSac EGG_SAC = new ModelTonyWormEggSac();

	public RenderTonyWormEggSac(RenderManager manager) {
		super(manager, new ModelTonyWormEggSac(), 0.5F);
	}
	
	@Override
	protected ResourceLocation getEntityTexture(EntityTonyWormEggSac entity) {
		return TEXTURE;
	}
}