package thebetweenlands.client.renderer.block;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.api.recipes.AnimatorRecipe;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.AnimatorBlock;
import thebetweenlands.common.block.entity.AnimatorBlockEntity;
import thebetweenlands.util.EntityCache;

public class AnimatorRenderer implements BlockEntityRenderer<AnimatorBlockEntity> {

	private static final RenderType TEXTURE = RenderType.entityCutoutNoCull(TheBetweenlands.prefix("textures/entity/block/animator.png"));
	private final ModelPart animator;

	public AnimatorRenderer(BlockEntityRendererProvider.Context context) {
		this.animator = context.bakeLayer(BLModelLayers.ANIMATOR);
	}

	@Override
	public void render(AnimatorBlockEntity entity, float partialTicks, PoseStack stack, MultiBufferSource source, int light, int overlay) {
		stack.pushPose();
		stack.translate(0.5F, 1.0F, 0.5F);
		stack.mulPose(Axis.XP.rotationDegrees(180.0F));
		stack.translate(0.0F, 1.0F, 0.0F);
		stack.mulPose(Axis.YP.rotationDegrees(entity.getBlockState().getValue(AnimatorBlock.FACING).toYRot()));
		stack.scale(-1.0F, 1.0F, 1.0F);
		this.animator.render(stack, source.getBuffer(TEXTURE), light, overlay);
		stack.popPose();

		stack.pushPose();
		stack.translate(0.5F, 0.0F, 0.5F);
		stack.mulPose(Axis.YP.rotationDegrees(entity.getBlockState().getValue(AnimatorBlock.FACING).toYRot()));
		if (entity.getLevel() != null) {
			RandomSource random = RandomSource.create(entity.getBlockPos().asLong());

			// Sulfur rendering
			if (!entity.getItem(2).isEmpty()) {
				int items = entity.getItem(2).getCount() / 4 + 1;
				for (int i = 0; i < items; i++) {
					stack.pushPose();
					stack.translate(random.nextDouble() / 3.0D - 1.0D / 6.0D, 0.27D, random.nextDouble() / 3.0D - 1.0D / 6.0D);
					stack.mulPose(Axis.XP.rotationDegrees(random.nextFloat() * 30.0F - 15.0F));
					stack.mulPose(Axis.ZP.rotationDegrees(random.nextFloat() * 30.0F - 15.0F));
					stack.scale(0.125F, 0.125F, 0.125F);
					stack.mulPose(Axis.XP.rotationDegrees(90.0F));
					stack.mulPose(Axis.ZP.rotationDegrees(random.nextFloat() * 360.0F));
					Minecraft.getInstance().getItemRenderer().renderStatic(entity.getItem(2), ItemDisplayContext.FIXED, light, overlay, stack, source, null, 0);
					stack.popPose();
				}
			}

			// Life crystal
			ItemStack crystal = entity.getItem(1);
			if (!crystal.isEmpty()) {
				stack.pushPose();
				stack.translate(0.0F, 0.43F, 0.0F);
				stack.scale(0.18F, 0.18F, 0.18F);
				stack.mulPose(Axis.YP.rotation(-this.setupRotation(entity, partialTicks)));
				Minecraft.getInstance().getItemRenderer().renderStatic(crystal, ItemDisplayContext.FIXED, light, overlay, stack, source, null, 0);
				stack.popPose();
			}

			// Item
			ItemStack inputStack = entity.getItem(0);
			if (!inputStack.isEmpty()) {
				stack.pushPose();
				stack.translate(0.0F, 1.43F, 0.0F);

				SingleRecipeInput input = new SingleRecipeInput(inputStack);
				RecipeHolder<AnimatorRecipe> recipe = entity.quickCheck.getRecipeFor(input, entity.getLevel()).orElse(null);

				if (recipe != null) {
					if (!(inputStack.getItem() instanceof SpawnEggItem) && recipe.value().getRenderEntity(input, entity.getLevel()) == null) {
						stack.scale(0.3F, 0.3F, 0.3F);
						stack.mulPose(Axis.YP.rotation(-this.setupRotation(entity, partialTicks)));
						Minecraft.getInstance().getItemRenderer().renderStatic(inputStack, ItemDisplayContext.FIXED, light, overlay, stack, source, null, 0);
					} else {
						RenderSystem.enableBlend();
						RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
						RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.65F);
						Entity renderEntity = this.fetchEntityForRendering(entity, recipe, input);
						if (renderEntity != null) {
							stack.translate(0.0D, renderEntity.getBbHeight() / 8.0D, 0.0D);
							stack.mulPose(Axis.YP.rotation(-this.setupRotation(entity, partialTicks) + Mth.PI));
							stack.scale(0.75F, 0.75F, 0.75F);
							renderEntity.setYRot(0.0F);
							renderEntity.setXRot(0.0F);
							renderEntity.tickCount = (int) entity.getLevel().getGameTime();
							EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
							boolean hitboxes = dispatcher.shouldRenderHitBoxes();
							dispatcher.setRenderShadow(false);
							dispatcher.setRenderHitBoxes(false);

							try {
								dispatcher.render(renderEntity, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks, stack, source, light);
							} catch (Exception e) {
								TheBetweenlands.LOGGER.error("Failed to render entity {} on the animator", renderEntity.getType().getDescriptionId(), e);
								EntityCache.addEntityToBlacklist(renderEntity.getType());
							}
							dispatcher.setRenderShadow(true);
							dispatcher.setRenderHitBoxes(hitboxes);
						}
						RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
					}
				}

				stack.popPose();
			}
		}
		stack.popPose();
	}

	@Nullable
	private Entity fetchEntityForRendering(AnimatorBlockEntity entity, RecipeHolder<AnimatorRecipe> holder, SingleRecipeInput input) {
		Entity renderEntity = null;
		if (holder != null && holder.value().getRenderEntity(input, entity.getLevel()) != null) {
			renderEntity = holder.value().getRenderEntity(input, entity.getLevel());
		} else if (entity.getItem(0).getItem() instanceof SpawnEggItem egg) {
			renderEntity = EntityCache.fetchEntity(egg.getType(entity.getItem(0)), entity.getLevel());
		}
		return renderEntity;
	}

	private float setupRotation(AnimatorBlockEntity entity, float partialTick) {
		float f1 = entity.rot - entity.oRot;

		while (f1 >= Mth.PI) {
			f1 -= Mth.PI * 2;
		}

		while (f1 < -Mth.PI) {
			f1 += Mth.PI * 2;
		}

		return entity.oRot + f1 * partialTick;
	}

	@Override
	public AABB getRenderBoundingBox(AnimatorBlockEntity entity) {
		if (entity.getLevel() != null) {
			SingleRecipeInput input = new SingleRecipeInput(entity.getItem(0));
			var recipe = entity.quickCheck.getRecipeFor(input, entity.getLevel()).orElse(null);
			Entity renderEntity = this.fetchEntityForRendering(entity, recipe, input);
			if (renderEntity != null) {
				return BlockEntityRenderer.super.getRenderBoundingBox(entity).expandTowards(renderEntity.getBbWidth() / 2, renderEntity.getBbHeight() + 1.0D, renderEntity.getBbWidth() / 2);
			}
		}
		return BlockEntityRenderer.super.getRenderBoundingBox(entity);
	}
}
