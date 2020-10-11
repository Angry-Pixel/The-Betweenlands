package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelBubblerCrab;
import thebetweenlands.common.entity.mobs.EntityBubblerCrab;

@SideOnly(Side.CLIENT)
public class RenderBubblerCrab extends RenderLiving<EntityBubblerCrab> {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/bubbler_crab.png");

	public RenderBubblerCrab(RenderManager manager) {
		super(manager, new ModelBubblerCrab(), 0.5F);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityBubblerCrab entity) {
		return TEXTURE;
	}
}