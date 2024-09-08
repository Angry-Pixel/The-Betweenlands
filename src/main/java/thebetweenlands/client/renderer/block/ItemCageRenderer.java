package thebetweenlands.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.shader.LightSource;
import thebetweenlands.client.shader.ShaderHelper;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.entity.ItemCageBlockEntity;

public class ItemCageRenderer implements BlockEntityRenderer<ItemCageBlockEntity> {

	private static final RenderType TEXTURE = RenderType.entityCutout(TheBetweenlands.prefix("textures/entity/block/item_cage.png"));
	private static final ResourceLocation POWER_TEXTURE = TheBetweenlands.prefix("textures/entity/block/item_cage_power.png");

	private final ModelPart cage;
	private final ModelPart bars;

	public ItemCageRenderer(BlockEntityRendererProvider.Context context) {
		var root = context.bakeLayer(BLModelLayers.ITEM_CAGE);
		this.cage = root.getChild("base");
		this.bars = root.getChild("bars");
	}

	@Override
	public void render(ItemCageBlockEntity entity, float partialTick, PoseStack stack, MultiBufferSource source, int light, int overlay) {
		float ticks = (float) (720.0 * (System.currentTimeMillis() & 0x3FFFL) / 0x3FFFL);

		if(ShaderHelper.INSTANCE.isWorldShaderActive() && entity.getLevel() != null) {
			ShaderHelper.INSTANCE.require();
			ShaderHelper.INSTANCE.getWorldShader().addLight(new LightSource(entity.getBlockPos().getX() + 0.5D, entity.getBlockPos().getY() + 0.5D, entity.getBlockPos().getZ() + 0.5D,
				3.5f,
				5.0f / 255.0f * 16.0F,
				40.0f / 255.0f * 16.0F,
				60.0f / 255.0f * 16.0F));
		}
		stack.pushPose();
		stack.translate(0.5F, 0.0F, 0.5F);
		stack.scale(1.0F, -1.0F, -1.0F);
		this.cage.render(stack, source.getBuffer(TEXTURE), light, overlay);
		this.bars.render(stack, source.getBuffer(RenderType.energySwirl(POWER_TEXTURE, ticks * 0.0015F % 1.0F, ticks * 0.0015F % 1.0F)), light, overlay, FastColor.ARGB32.colorFromFloat(1.0F, 0.5F, 0.5F, 0.5F));
		stack.popPose();
	}
}
