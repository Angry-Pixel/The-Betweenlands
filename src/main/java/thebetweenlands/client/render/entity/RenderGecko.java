package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelGecko;
import thebetweenlands.common.entity.mobs.EntityGecko;
import thebetweenlands.common.lib.ModInfo;

@SideOnly(Side.CLIENT)
public class RenderGecko extends RenderLiving<EntityGecko> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.ID + ":textures/entity/gecko.png");

	public RenderGecko(RenderManager renderManager) {
		super(renderManager, new ModelGecko(), 0.5F);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityGecko entity) {
		return TEXTURE;
	}
}
