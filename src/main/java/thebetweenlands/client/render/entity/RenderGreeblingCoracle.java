package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelGreeblingCoracle;
import thebetweenlands.common.entity.mobs.EntityGreeblingCoracle;

@SideOnly(Side.CLIENT)
public class RenderGreeblingCoracle extends RenderLiving<EntityGreeblingCoracle> {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/greebling_coracle.png");

	public RenderGreeblingCoracle(RenderManager rendermanagerIn) {
		super(rendermanagerIn, new ModelGreeblingCoracle(), 0.2F);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityGreeblingCoracle entity) {
		return TEXTURE;
	}
}
