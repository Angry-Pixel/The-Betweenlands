package thebetweenlands.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.CCGroundSpawner;

public class CCGroundSpawnerRenderer extends EntityRenderer<CCGroundSpawner> {
	private static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/cc_ground_spawner_shingles.png");
	private static final ResourceLocation HOLE_TEXTURE = TheBetweenlands.prefix("textures/entity/cc_ground_spawner_hole.png");
	private static final ResourceLocation GROUND_TEXTURE = TheBetweenlands.prefix("textures/entity/cc_ground_spawner_ground.png");

	public CCGroundSpawnerRenderer(Context context) {
		super(context);
	}

	@Override
	public ResourceLocation getTextureLocation(CCGroundSpawner entity) {
		//TODO temp
		return TEXTURE;
	}
}
