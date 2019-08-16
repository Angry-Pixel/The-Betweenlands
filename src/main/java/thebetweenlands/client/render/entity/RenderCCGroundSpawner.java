package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.EntityCCGroundSpawner;

@SideOnly(Side.CLIENT)
public class RenderCCGroundSpawner extends Render<EntityCCGroundSpawner> {

	public RenderCCGroundSpawner(RenderManager renderManagerIn) {
		super(renderManagerIn);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityCCGroundSpawner entity) {
		return null;
	}
}
