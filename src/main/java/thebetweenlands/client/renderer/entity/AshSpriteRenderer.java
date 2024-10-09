package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.AshSpriteModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.monster.AshSprite;

public class AshSpriteRenderer extends MobRenderer<AshSprite, AshSpriteModel> {
	private static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/ash_sprite.png");

	public AshSpriteRenderer(EntityRendererProvider.Context context) {
		super(context, new AshSpriteModel(context.bakeLayer(BLModelLayers.ASH_SPRITE)), 0.2F);
	}

	@Override
	protected void scale(AshSprite entity, PoseStack stack, float partialTick) {
		stack.translate(0.0F, 0.85F, 0.0F);
	}

	@Override
	public ResourceLocation getTextureLocation(AshSprite entity) {
		return TEXTURE;
	}
}
