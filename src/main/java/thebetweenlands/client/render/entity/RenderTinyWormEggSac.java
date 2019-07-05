package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.render.model.entity.ModelTinyWormEggSac;
import thebetweenlands.common.entity.EntityTinyWormEggSac;
import thebetweenlands.common.lib.ModInfo;

public class RenderTinyWormEggSac extends RenderLiving<EntityTinyWormEggSac> {
	public static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.ID, "textures/entity/worm_egg_sac.png");
	private final ModelTinyWormEggSac EGG_SAC = new ModelTinyWormEggSac();

	public RenderTinyWormEggSac(RenderManager manager) {
		super(manager, new ModelTinyWormEggSac(), 0.5F);
	}
	
	@Override
	protected ResourceLocation getEntityTexture(EntityTinyWormEggSac entity) {
		return TEXTURE;
	}
}