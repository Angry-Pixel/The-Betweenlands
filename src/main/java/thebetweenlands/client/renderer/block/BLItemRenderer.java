package thebetweenlands.client.renderer.block;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import thebetweenlands.common.block.CenserBlock;
import thebetweenlands.common.block.SimulacrumBlock;
import thebetweenlands.common.block.entity.CenserBlockEntity;
import thebetweenlands.common.registries.BlockRegistry;

import java.util.function.Supplier;

public class BLItemRenderer extends BlockEntityWithoutLevelRenderer {

	private final CenserBlockEntity censer = new CenserBlockEntity(BlockPos.ZERO, BlockRegistry.CENSER.get().defaultBlockState());
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
			if (block instanceof SimulacrumBlock simulacrum) {
				minecraft.getBlockEntityRenderDispatcher().renderItem(simulacrum.newBlockEntity(BlockPos.ZERO, simulacrum.defaultBlockState()), pose, source, light, overlay);
			} else if (block instanceof CenserBlock) {
				minecraft.getBlockEntityRenderDispatcher().renderItem(this.censer, pose, source, light, overlay);
			}
		}
	}
}
