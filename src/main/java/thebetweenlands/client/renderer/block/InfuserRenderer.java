package thebetweenlands.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Holder;
import net.minecraft.util.FastColor;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Matrix4f;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.InfuserBlock;
import thebetweenlands.common.block.entity.InfuserBlockEntity;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.herblore.elixir.ElixirRecipe;
import thebetweenlands.util.RenderUtils;

public class InfuserRenderer implements BlockEntityRenderer<InfuserBlockEntity> {

	private static final RenderType TEXTURE = RenderType.entityCutout(TheBetweenlands.prefix("textures/entity/block/infuser.png"));
	private final ModelPart infuser;
	private final ModelPart spoon;
	private final ItemRenderer itemRenderer;

	public InfuserRenderer(BlockEntityRendererProvider.Context context) {
		ModelPart root = context.bakeLayer(BLModelLayers.INFUSER);
		this.infuser = root.getChild("infuser");
		this.spoon = root.getChild("spoon");
		this.itemRenderer = context.getItemRenderer();
	}

	@Override
	public void render(InfuserBlockEntity entity, float partialTicks, PoseStack stack, MultiBufferSource source, int light, int overlay) {
		stack.pushPose();
		stack.translate(0.5F, 0.0F, 0.5F);
		stack.mulPose(Axis.YP.rotationDegrees(-entity.getBlockState().getValue(InfuserBlock.FACING).toYRot()));
		stack.scale(1.0F, -1.0F, -1.0F);
		this.infuser.render(stack, source.getBuffer(TEXTURE), light, overlay);
		if (entity.getLevel() != null) {
			stack.pushPose();
			stack.mulPose(Axis.YP.rotationDegrees(entity.getStirProgress() * 4));
			this.spoon.render(stack, source.getBuffer(TEXTURE), light, overlay);
			stack.popPose();
		}
		stack.popPose();

		if (BetweenlandsConfig.debug) {
			Holder<ElixirRecipe> recipe = entity.getInfusingRecipe();
			String elixirName = recipe != null ? recipe.getRegisteredName() : " N/A";
			String text = "Evap: " + entity.getEvaporation() + " Temp: " + entity.getTemperature() + " Time: " + entity.getInfusionTime() + " Recipe: " + elixirName;

			stack.pushPose();
			stack.translate(0.5D, 2.5D, 0.5D);
			stack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
			stack.scale(0.025F, -0.025F, 0.025F);
			Matrix4f matrix4f = stack.last().pose();
			Font font = Minecraft.getInstance().font;
			font.drawInBatch(text, -font.width(text) / 2.0F, 0, -1, false, matrix4f, source, Font.DisplayMode.NORMAL, 0, light);
			stack.popPose();
		}

		int amount = entity.tank.getFluidAmount();
		int capacity = entity.tank.getCapacity();
		float size = 1F / capacity * amount;

		int itemBob = entity.getItemBob();
		int stirProgress = entity.getStirProgress();
		float crystalRotation = entity.getCrystalRotation();
		double itemY = 0.3D + size * 0.5D;
		RandomSource rand = RandomSource.create(entity.getBlockPos().asLong());
		for (int i = 0; i <= InfuserBlockEntity.MAX_INGREDIENTS; i++) {
			if (!entity.getItem(i).isEmpty()) {
				float randRot = rand.nextFloat() * 360.0F;
				double xo = -0.2D + rand.nextFloat() * 0.4D;
				double zo = -0.2D + rand.nextFloat() * 0.4D;
				float rot = (stirProgress < 90 && amount >= 100 ? stirProgress * 4.0F + 45.0F : 45.0F) + randRot;
				stack.pushPose();
				stack.translate(0.5D, itemY, 0.5D);
				stack.mulPose(Axis.YP.rotationDegrees(-rot));
				stack.translate(xo, 0, zo);
				stack.pushPose();
				stack.scale(0.25F, 0.25F, 0.25F);
				stack.translate(0.0D, amount >= 100 ? (i % 2 == 0 ? (itemBob * 0.01D) : ((-itemBob + 20) * 0.01D)) : 0.0D, 0.0D);
				stack.mulPose(Axis.YP.rotationDegrees(-rot));
				this.itemRenderer.renderStatic(entity.getItem(i), ItemDisplayContext.FIXED, light, overlay, stack, source, null, 0);
				stack.popPose();
				stack.popPose();
			}
		}

		if (!entity.getItem(InfuserBlockEntity.MAX_INGREDIENTS + 1).isEmpty()) {
			stack.pushPose();
			stack.translate(0.5D, 1.43D, 0.5D);
			stack.scale(0.25F, 0.25F, 0.25F);
			stack.translate(0.0D, itemBob * 0.01D, 0.0D);
			stack.mulPose(Axis.YP.rotationDegrees(crystalRotation));
			this.itemRenderer.renderStatic(entity.getItem(InfuserBlockEntity.MAX_INGREDIENTS + 1), ItemDisplayContext.FIXED, light, overlay, stack, source, null, 0);
			stack.popPose();
		}

		if (amount >= 100) {
			FluidStack fluidStack = entity.tank.getFluid();
			TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(IClientFluidTypeExtensions.of(fluidStack.getFluid()).getStillTexture());
			float[] targetColor;
			if (entity.getInfusionColorGradientTicks() > 0) {
				targetColor = new float[]{
					entity.prevInfusionColor[0] + (entity.currentInfusionColor[0] - entity.prevInfusionColor[0]) / 30.0F * entity.getInfusionColorGradientTicks(),
					entity.prevInfusionColor[1] + (entity.currentInfusionColor[1] - entity.prevInfusionColor[1]) / 30.0F * entity.getInfusionColorGradientTicks(),
					entity.prevInfusionColor[2] + (entity.currentInfusionColor[2] - entity.prevInfusionColor[2]) / 30.0F * entity.getInfusionColorGradientTicks(),
					entity.prevInfusionColor[3] + (entity.currentInfusionColor[3] - entity.prevInfusionColor[3]) / 30.0F * entity.getInfusionColorGradientTicks()
				};
			} else {
				targetColor = entity.currentInfusionColor;
			}
			stack.pushPose();
			stack.translate(0.0F, 0.35F + size * 0.5F, 0.0F);
			RenderUtils.renderTopQuad(stack.last(), source.getBuffer(RenderType.entityTranslucent(sprite.atlasLocation())), light, FastColor.ARGB32.colorFromFloat(targetColor[3], targetColor[0], targetColor[1], targetColor[2]), 0.1875F, 0.8125F, 0.0F, 0.1875F, 0.8125F, sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1());
			stack.popPose();
		}
	}
}
