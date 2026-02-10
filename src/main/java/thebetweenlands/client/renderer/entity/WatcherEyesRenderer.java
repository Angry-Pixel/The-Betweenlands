package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.WatcherEyesModel;
import thebetweenlands.client.renderer.entity.layers.WatcherEyesLayer;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.WatcherEyes;

public class WatcherEyesRenderer extends MobRenderer<WatcherEyes, WatcherEyesModel> {
	public static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/watcher_eyes.png");

	public WatcherEyesRenderer(EntityRendererProvider.Context context) {
		super(context, new WatcherEyesModel(context.bakeLayer(BLModelLayers.WATCHER_EYES)), 0.0F);
		addLayer(new WatcherEyesLayer(this, context.getModelSet()));
	}

	@Override
	public void render(WatcherEyes entity, float entityYaw, float partialTicks, PoseStack stack, MultiBufferSource buffer, int packedLight) {
	    BlockPos offsetPos = entity.blockPosition().relative(entity.getDirection(), 1);
	    int customLight = LevelRenderer.getLightColor(entity.level(), offsetPos);
		super.render(entity, entityYaw, partialTicks, stack, buffer, customLight);
	}

	@SuppressWarnings("deprecation")
	@Override
	public ResourceLocation getTextureLocation(WatcherEyes entity) {
		BlockState state = entity.level().getBlockState(entity.blockPosition());
		String blockPath = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getParticleIcon(state).contents().name().toString();
		String modName = "minecraft";
		if (blockPath.contains(":")) {
			modName = blockPath.split(":")[0];
			blockPath = blockPath.split(":")[1];
		}
		return ResourceLocation.fromNamespaceAndPath(modName, "textures/" + blockPath + ".png");
	}

}
