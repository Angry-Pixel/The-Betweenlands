package thebetweenlands.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.SteepingPotBlock;
import thebetweenlands.common.block.entity.SteepingPotBlockEntity;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.common.registries.FluidTypeRegistry;
import thebetweenlands.util.RenderUtils;

public class SteepingPotRenderer implements BlockEntityRenderer<SteepingPotBlockEntity> {

	private static final RenderType GROUND_TEXTURE = RenderType.entityCutoutNoCull(TheBetweenlands.prefix("textures/entity/block/steeping_pot.png"));
	private static final RenderType HANGING_TEXTURE = RenderType.entityCutoutNoCull(TheBetweenlands.prefix("textures/entity/block/steeping_pot_hanging.png"));
	private final ModelPart groundPot;
	private final ModelPart hangingPot;

	public SteepingPotRenderer(BlockEntityRendererProvider.Context context) {
		this.groundPot = context.bakeLayer(BLModelLayers.STEEPING_POT);
		this.hangingPot = context.bakeLayer(BLModelLayers.HANGING_STEEPING_POT);
	}

	@Override
	public void render(SteepingPotBlockEntity entity, float partialTicks, PoseStack stack, MultiBufferSource source, int light, int overlay) {
		stack.pushPose();
		stack.translate(0.5F, 0.0F, 0.5F);
		stack.mulPose(Axis.YP.rotationDegrees(-entity.getBlockState().getValue(SteepingPotBlock.FACING).toYRot() + 180));
		stack.scale(1.0F, -1.0F, -1.0F);
		if (entity.getBlockState().getValue(SteepingPotBlock.HANGING)) {
			this.hangingPot.render(stack, source.getBuffer(HANGING_TEXTURE), light, overlay);
		} else {
			this.groundPot.render(stack, source.getBuffer(GROUND_TEXTURE), light, overlay);
		}
		stack.popPose();

		if (entity.getLevel() != null) {
			stack.pushPose();

			if(entity.getBlockState().getValue(SteepingPotBlock.HANGING)) {
				stack.translate(0.0F, -0.247F, 0.0F);
			}

			float fluidLevel = entity.tank.getFluidAmount();
			float height = 0.0625F;

			if (fluidLevel > 0) {
				FluidStack fluidStack = entity.tank.getFluid();
				height = (0.375F / entity.tank.getCapacity()) * entity.tank.getFluidAmount();
				TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(IClientFluidTypeExtensions.of(fluidStack.getFluid()).getStillTexture());
				if(entity.getHeatProgress() > 0  && entity.tank.getFluid().is(FluidTypeRegistry.DYE.get()) || entity.tank.getFluid().is(FluidTypeRegistry.BREW.get()))
					sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(IClientFluidTypeExtensions.of(fluidStack.getFluid()).getFlowingTexture());
				int fluidColor = IClientFluidTypeExtensions.of(fluidStack.getFluid()).getTintColor(fluidStack);
				int fluidColorTemp = entity.tempFluidColour;
				float fade = entity.getHeatProgress() > 50 && entity.hasBundle() ? (-50 + entity.getHeatProgress()) * 0.025F: 0F;

				stack.pushPose();
				stack.translate(0.0F, 0.25F, 0.0F);
				RenderUtils.renderTopQuad(stack.last(), source.getBuffer(RenderType.entityTranslucent(sprite.atlasLocation())), light, FastColor.ARGB32.color(FastColor.as8BitChannel(1.0F - fade), fluidColor), 0.25F, 0.75F, height, 0.25F, 0.75F, sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1());
				stack.popPose();

				stack.pushPose();
				stack.translate(0.0F, 0.25F, 0.0F);
				RenderUtils.renderTopQuad(stack.last(), source.getBuffer(RenderType.entityTranslucent(sprite.atlasLocation())), light, FastColor.ARGB32.color(FastColor.as8BitChannel(0.0F + fade), fluidColorTemp), 0.25F, 0.75F, height, 0.25F, 0.75F, sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1());
				stack.popPose();
			}

			if (!entity.getItem(0).isEmpty()) {
				int stirProgress = entity.itemRotate;
				int prevStirProgress = entity.prevItemRotate;
				float stirTicks = stirProgress + (stirProgress - prevStirProgress) * partialTicks;
				float bobTicks = Mth.sin((entity.itemBob + partialTicks) / 10.0F) * 0.1F + 0.1F;

				double itemY = 0.25D + height;
				RandomSource rand = RandomSource.create(entity.getBlockPos().asLong());
				float randRot = rand.nextFloat() * 360.0F;
				float xo = -0.1F + rand.nextFloat() * 0.2F;
				float zo = -0.1F + rand.nextFloat() * 0.2F;
				float rot = (stirTicks < 180 && fluidLevel > 0 ? stirTicks * 2.0F + 90.0F + randRot : 90.0F + randRot);
				stack.pushPose();
				stack.translate(0.5D, fluidLevel > 0 ? 0.0D : 0.15D, 0.5D);
				stack.mulPose(Axis.YP.rotationDegrees(-rot));
				stack.translate(xo, 0, zo);
				this.renderItem(entity, stack, source, light, overlay, itemY, fluidLevel > 0 ? bobTicks : 0.0F, -rot);
				stack.popPose();
			}

			stack.popPose();
		}
	}

	private void renderItem(SteepingPotBlockEntity entity, PoseStack stack, MultiBufferSource source, int light, int overlay, double y, float itemBob, float rotation) {
		stack.pushPose();
		stack.translate(0.0D, y, 0.0D);
		stack.scale(0.4F, 0.4F, 0.4F);
		stack.translate(0.0D, itemBob * 0.5 - 0.05D, 0.0D);
		stack.mulPose(Axis.YP.rotationDegrees(rotation));
		stack.mulPose(Axis.ZP.rotationDegrees(entity.getHeatProgress() < 20 ? 0.0F : itemBob * 30.0F));
		stack.mulPose(Axis.XP.rotationDegrees(entity.getHeatProgress() < 40 ? 0.0F : itemBob * 30.0F));
		stack.mulPose(Axis.YN.rotationDegrees(entity.getHeatProgress() < 60 ? 0.0F : itemBob * 60.0F));
		Minecraft.getInstance().getItemRenderer().renderStatic(entity.getItem(0), ItemDisplayContext.FIXED, light, overlay, stack, source, null, 0);
		stack.popPose();
	}
}
