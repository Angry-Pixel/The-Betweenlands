package thebetweenlands.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.monster.RockSnotTendril;

public class RockSnotTendrilRenderer extends EntityRenderer<RockSnotTendril> {
	private static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/rock_snot_grabber.png");
	private static final ResourceLocation VERTICAL_RING_TEXTURE = TheBetweenlands.prefix("textures/entity/decay_pit_vertical_ring.png");

	public RockSnotTendrilRenderer(Context context) {
		super(context);
	}

	@Override
	public ResourceLocation getTextureLocation(RockSnotTendril entity) {
		return TEXTURE;
	}
}
