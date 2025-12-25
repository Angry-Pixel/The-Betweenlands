package thebetweenlands.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.monster.RockSnot;

public class RockSnotRenderer extends EntityRenderer<RockSnot> {
	private static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/rock_snot.png");

	public RockSnotRenderer(Context context) {
		super(context);
	}

	@Override
	public ResourceLocation getTextureLocation(RockSnot entity) {
		return TEXTURE;
	}
}
