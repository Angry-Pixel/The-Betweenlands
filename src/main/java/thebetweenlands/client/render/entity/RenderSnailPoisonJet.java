package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.projectiles.EntitySnailPoisonJet;

@SideOnly(Side.CLIENT)
public class RenderSnailPoisonJet extends Render<EntitySnailPoisonJet> {
	public RenderSnailPoisonJet(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(EntitySnailPoisonJet entity, double x, double y, double z, float entityYaw, float partialTicks) {
	}

	@Override
	protected ResourceLocation getEntityTexture(EntitySnailPoisonJet entity) {
		return null;
	}
}