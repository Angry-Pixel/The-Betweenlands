package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelRockSnot;
import thebetweenlands.common.entity.mobs.EntityRockSnot;

@SideOnly(Side.CLIENT)
public class RenderRockSnot extends RenderLiving<EntityRockSnot> {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/rock_snot.png");

	public RenderRockSnot(RenderManager rendermanagerIn) {
		super(rendermanagerIn, new ModelRockSnot(), 0.5F);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityRockSnot entity) {
		return TEXTURE;
	}

}
