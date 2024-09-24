package thebetweenlands.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceKey;
import thebetweenlands.api.aspect.registry.AspectType;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.block.cage.CagedGeckoModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.container.GeckoCageBlock;
import thebetweenlands.common.block.entity.GeckoCageBlockEntity;
import thebetweenlands.common.registries.AspectTypeRegistry;

import java.util.HashMap;
import java.util.Map;

public class GeckoCageRenderer implements BlockEntityRenderer<GeckoCageBlockEntity> {

	private static final RenderType TEXTURE = RenderType.entityCutoutNoCull(TheBetweenlands.prefix("textures/entity/block/gecko_cage.png"));
	private static final RenderType NORMAL_GECKO_TEX = RenderType.entityCutoutNoCull(TheBetweenlands.prefix("textures/entity/gecko/gecko.png"));
	private static final RenderType CORRUPT_GECKO_TEX = RenderType.entityCutoutNoCull(TheBetweenlands.prefix("textures/entity/gecko/gecko_corrupted.png"));
	private static final RenderType RED_GECKO_TEX = RenderType.entityCutoutNoCull(TheBetweenlands.prefix("textures/entity/gecko/gecko_red.png"));
	private static final RenderType PALE_GECKO_TEX = RenderType.entityCutoutNoCull(TheBetweenlands.prefix("textures/entity/gecko/gecko_pale.png"));
	private static final RenderType TAN_GECKO_TEX = RenderType.entityCutoutNoCull(TheBetweenlands.prefix("textures/entity/gecko/gecko_tan.png"));
	private static final RenderType GREEN_GECKO_TEX = RenderType.entityCutoutNoCull(TheBetweenlands.prefix("textures/entity/gecko/gecko_green.png"));
	private final ModelPart cage;
	private final Map<ResourceKey<AspectType>, Pair<RenderType, CagedGeckoModel>> GECKOS = new HashMap<>();
	private final Pair<RenderType, CagedGeckoModel> defaultModel;

	public GeckoCageRenderer(BlockEntityRendererProvider.Context context) {
		this.cage = context.bakeLayer(BLModelLayers.GECKO_CAGE);
		GECKOS.put(AspectTypeRegistry.BYARIIS, Pair.of(CORRUPT_GECKO_TEX, new CagedGeckoModel(context.bakeLayer(BLModelLayers.CORRUPT_GECKO))));
		GECKOS.put(AspectTypeRegistry.FERGALAZ, Pair.of(TAN_GECKO_TEX, new CagedGeckoModel(context.bakeLayer(BLModelLayers.GECKO))));
		GECKOS.put(AspectTypeRegistry.FIRNALAZ, Pair.of(RED_GECKO_TEX, new CagedGeckoModel(context.bakeLayer(BLModelLayers.GECKO))));
		GECKOS.put(AspectTypeRegistry.GEOLIIRGAZ, Pair.of(PALE_GECKO_TEX, new CagedGeckoModel(context.bakeLayer(BLModelLayers.GECKO))));
		GECKOS.put(AspectTypeRegistry.YIHINREN, Pair.of(GREEN_GECKO_TEX, new CagedGeckoModel(context.bakeLayer(BLModelLayers.MUTATED_GECKO))));
		this.defaultModel = Pair.of(NORMAL_GECKO_TEX, new CagedGeckoModel(context.bakeLayer(BLModelLayers.GECKO)));
	}

	@Override
	public void render(GeckoCageBlockEntity entity, float partialTicks, PoseStack stack, MultiBufferSource source, int light, int overlay) {
		stack.pushPose();
		stack.translate(0.5F, 0.0F, 0.5F);
		stack.mulPose(Axis.YP.rotationDegrees(-entity.getBlockState().getValue(GeckoCageBlock.FACING).toYRot()));
		stack.scale(1.0F, -1.0F, -1.0F);
		stack.pushPose();
		this.cage.render(stack, source.getBuffer(TEXTURE), light, overlay);
		stack.popPose();

		if (entity.getLevel() != null && entity.hasGecko()) {
			stack.pushPose();
			stack.translate(0.0F, -1.175F, 0.0F);
			stack.scale(0.6F, 0.6F,0.6F);
			Pair<RenderType, CagedGeckoModel> model;
			if (entity.getAspectType() != null) {
				model = GECKOS.getOrDefault(entity.getAspectType().getKey(), this.defaultModel);
			} else {
				model = this.defaultModel;
			}
			model.getSecond().renderWithAnimations(stack, source.getBuffer(model.getFirst()), light, overlay, entity.getTicks(), partialTicks);
			stack.popPose();
		}
		stack.popPose();
	}
}
