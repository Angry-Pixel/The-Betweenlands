package thebetweenlands.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.WormGroundSpawner;

public class WormGroundSpawnerRenderer extends EntityRenderer<WormGroundSpawner> {
	private static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/cc_ground_spawner_shingles.png");
	private static final ResourceLocation HOLE_TEXTURE = TheBetweenlands.prefix("textures/entity/cc_ground_spawner_hole.png");
	private static final ResourceLocation GROUND_TEXTURE = TheBetweenlands.prefix("textures/entity/cc_ground_spawner_ground.png");

	public WormGroundSpawnerRenderer(Context context) {
		super(context);
	}

	@Override
	public ResourceLocation getTextureLocation(WormGroundSpawner entity) {
		//TODO temp
		return TEXTURE;
	}
}
