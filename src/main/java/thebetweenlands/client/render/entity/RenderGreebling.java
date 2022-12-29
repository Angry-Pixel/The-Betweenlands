package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelGreebling;
import thebetweenlands.common.entity.mobs.EntityGreebling;

@SideOnly(Side.CLIENT)
public class RenderGreebling extends RenderLiving<EntityGreebling> {
	public static final ResourceLocation TEXTURE_0 = new ResourceLocation("thebetweenlands:textures/entity/greebling_0.png");
	public static final ResourceLocation TEXTURE_1 = new ResourceLocation("thebetweenlands:textures/entity/greebling_1.png");

	public RenderGreebling(RenderManager rendermanagerIn) {
		super(rendermanagerIn, new ModelGreebling(), 0.2F);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityGreebling entity) {
		if (entity.getType() == 0) {
			return TEXTURE_0;
		}
		else {
			return TEXTURE_1;
		}
	}
}
