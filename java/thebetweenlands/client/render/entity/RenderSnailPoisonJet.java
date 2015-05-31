package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderSnailPoisonJet extends Render {

	@Override
	public void doRender(Entity entity, double x, double y, double z, float yaw, float tick) {
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return null;
	}
}