package thebetweenlands.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.WallLampreyModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.monster.wall.WallLamprey;

public class WallLampreyRenderer extends WallHoleRenderer<WallLamprey, WallLampreyModel> {
	private static final ResourceLocation MODEL_TEXTURE = TheBetweenlands.prefix("textures/entity/wall_lamprey.png");

	public WallLampreyRenderer(EntityRendererProvider.Context context) {
		super(context, new WallLampreyModel(context.bakeLayer(BLModelLayers.WALL_LAMPREY)), MODEL_TEXTURE);
	}

	@Nullable
	@Override
	protected TextureAtlasSprite getWallSprite(WallLamprey entity) {
		return entity.info.getWallSprite();
	}

	@Override
	protected float getHoleDepthPercent(WallLamprey entity, float partialTicks) {
		return entity.getHoleDepthPercent(partialTicks);
	}

	@Override
	protected float getMainModelVisibilityPercent(WallLamprey entity, float partialTicks) {
		return 1.0F - entity.getLampreyHiddenPercent(partialTicks);
	}
}
