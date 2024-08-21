package thebetweenlands.client.renderer.block;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Supplier;

public class BLItemRenderer extends BlockEntityWithoutLevelRenderer {

	private static final Map<ResourceLocation, BlockEntity> cachedBEInstances = new WeakHashMap<>();
	public static final Supplier<BLItemRenderer> INSTANCE = Suppliers.memoize(BLItemRenderer::new);
	public static final IClientItemExtensions CLIENT_ITEM_EXTENSION = Util.make(() -> new IClientItemExtensions() {
		@Override
		public BlockEntityWithoutLevelRenderer getCustomRenderer() {
			return INSTANCE.get();
		}
	});

	private BLItemRenderer() {
		super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
	}

	@Override
	public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack pose, MultiBufferSource source, int light, int overlay) {
		Item item = stack.getItem();
		if (item instanceof BlockItem blockItem) {
			Block block = blockItem.getBlock();
			Minecraft minecraft = Minecraft.getInstance();
			if (block instanceof BaseEntityBlock entity) {
				cachedBEInstances.putIfAbsent(BuiltInRegistries.BLOCK.getKey(entity), entity.newBlockEntity(BlockPos.ZERO, entity.defaultBlockState()));
				minecraft.getBlockEntityRenderDispatcher().renderItem(cachedBEInstances.get(BuiltInRegistries.BLOCK.getKey(entity)), pose, source, light, overlay);
			}
		}
	}
}
