package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.projectiles.EntitySludgeWallJet;

@SideOnly(Side.CLIENT)
public class RenderSludgeWallJet extends Render<EntitySludgeWallJet> {

	public RenderSludgeWallJet(RenderManager renderManagerIn) {
		super(renderManagerIn);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntitySludgeWallJet entity) {
		return null;
	}
}
