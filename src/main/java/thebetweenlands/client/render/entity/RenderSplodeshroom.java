package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.render.model.entity.ModelSplodeshroom;
import thebetweenlands.common.entity.EntitySplodeshroom;
import thebetweenlands.common.lib.ModInfo;

public class RenderSplodeshroom extends RenderLiving<EntitySplodeshroom> {
	public static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.ID, "textures/entity/splodeshroom.png");

	public RenderSplodeshroom(RenderManager manager) {
		super(manager, new ModelSplodeshroom(), 0F);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntitySplodeshroom entity) {
		return TEXTURE;
	}
}