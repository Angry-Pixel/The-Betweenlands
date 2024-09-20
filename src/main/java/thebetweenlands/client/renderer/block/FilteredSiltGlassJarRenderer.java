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
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.entity.FilteredSiltGlassJarBlockEntity;
import thebetweenlands.util.RenderUtils;

public class FilteredSiltGlassJarRenderer implements BlockEntityRenderer<FilteredSiltGlassJarBlockEntity> {

	private static final RenderType TEXTURE = RenderType.entityTranslucent(TheBetweenlands.prefix("textures/entity/block/filtered_silt_glass_jar.png"));
	private final ModelPart jar;

	public FilteredSiltGlassJarRenderer(BlockEntityRendererProvider.Context context) {
		this.jar = context.bakeLayer(BLModelLayers.GLASS_JAR);
	}

	@Override
	public void render(FilteredSiltGlassJarBlockEntity entity, float partialTicks, PoseStack stack, MultiBufferSource source, int light, int overlay){
		if (entity.getLevel() != null) {
			float fluidLevel = entity.tank.getFluidAmount();
			if (fluidLevel > 0) {
				FluidStack fluidStack = entity.tank.getFluid();
				float height = (0.6875F / entity.tank.getCapacity()) * entity.tank.getFluidAmount();

				TextureAtlasSprite fluidStillSprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(IClientFluidTypeExtensions.of(fluidStack.getFluid()).getStillTexture());
				int fluidColor = IClientFluidTypeExtensions.of(fluidStack.getFluid()).getTintColor(fluidStack);
				RenderUtils.renderCuboid(stack.last(), source.getBuffer(RenderType.entityCutoutNoCull(fluidStillSprite.atlasLocation())), light, fluidColor, fluidStillSprite, 0.25F, 0.75F, 0.0625F, height, 0.25F, 0.75F);
			}
		}
		stack.pushPose();
		stack.translate(0.5F, 0.0F, 0.5F);
		stack.scale(1.0F, -1.0F, -1.0F);
		this.jar.render(stack, source.getBuffer(TEXTURE), light, overlay);
		stack.popPose();
	}
}
