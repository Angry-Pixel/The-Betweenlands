package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelChiromawHatchling;
import thebetweenlands.common.entity.mobs.EntityChiromawHatchling;
import thebetweenlands.common.lib.ModInfo;

@SideOnly(Side.CLIENT)
public class RenderChiromawHatchling extends RenderLiving<EntityChiromawHatchling> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.ID, "textures/entity/chiromaw_hatchling.png");

	public RenderChiromawHatchling(RenderManager renderManager) {
		super(renderManager, new ModelChiromawHatchling(), 0.2F);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityChiromawHatchling entity) {
		return TEXTURE;
	}
}
