package thebetweenlands.client.renderer.block;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.entity.SiltGlassJarBlockEntity;
import thebetweenlands.util.RenderUtils;

public class SiltGlassJarRenderer implements BlockEntityRenderer<SiltGlassJarBlockEntity> {

	private static final RenderType TEXTURE = RenderType.entityTranslucent(TheBetweenlands.prefix("textures/entity/block/silt_glass_jar.png"));
	private final ModelPart jar;

	public SiltGlassJarRenderer(BlockEntityRendererProvider.Context context) {
		this.jar = context.bakeLayer(BLModelLayers.GLASS_JAR);
	}

	@Override
	public void render(SiltGlassJarBlockEntity entity, float partialTicks, PoseStack stack, MultiBufferSource source, int light, int overlay){
		if (entity.getLevel() != null) {
			int wormLevel = entity.getItemCount();
			if (wormLevel >= 1) {
				TextureAtlasSprite wormSprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(TheBetweenlands.prefix("block/worm_wiggle"));
				RenderUtils.renderCuboid(stack.last(), source.getBuffer(RenderType.entityCutoutNoCull(wormSprite.atlasLocation())), light, -1, wormSprite, 0.25F, 0.75F, 0.0625F, (0.6875F / 8) * wormLevel, 0.25F, 0.75F);
			}
		}
		stack.pushPose();
		stack.translate(0.5F, 0.0F, 0.5F);
		stack.scale(1.0F, -1.0F, -1.0F);
		this.jar.render(stack, source.getBuffer(TEXTURE), light, overlay);
		stack.popPose();
	}
}
