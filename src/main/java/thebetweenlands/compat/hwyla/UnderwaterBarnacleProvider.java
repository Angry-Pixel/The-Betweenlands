package thebetweenlands.compat.hwyla;

import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.base.Strings;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.config.FormattingConfig;
import mcp.mobius.waila.overlay.DisplayUtil;
import mcp.mobius.waila.utils.Constants;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.FluidRegistry;
import thebetweenlands.common.block.farming.BlockBarnacle_1_2;
import thebetweenlands.common.block.farming.BlockBarnacle_3_4;
import thebetweenlands.common.registries.BlockRegistry;

public class UnderwaterBarnacleProvider implements IWailaDataProvider {

    @Nonnull
    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        Block block = accessor.getBlock();
        IBlockState state = accessor.getBlockState();
        String name = new TextComponentTranslation("tile.thebetweenlands.barnacle_1_2.name").getFormattedText();

		if (block instanceof BlockBarnacle_1_2 || block instanceof BlockBarnacle_3_4) {

			if (block == BlockRegistry.BARNACLE_1_2) {
				if (block.getMetaFromState(state) <= 5)
					name = new TextComponentTranslation("tooltip.thebetweenlands.barnacle_stage_1.name").getFormattedText();
				if (block.getMetaFromState(state) > 5 && block.getMetaFromState(state) <= 11)
					name = new TextComponentTranslation("tooltip.thebetweenlands.barnacle_stage_2.name").getFormattedText();
			}

			if (block == BlockRegistry.BARNACLE_3_4) {
				if (block.getMetaFromState(state) <= 5)
					name = new TextComponentTranslation("tooltip.thebetweenlands.barnacle_stage_3.name").getFormattedText();
				if (block.getMetaFromState(state) > 5 && block.getMetaFromState(state) <= 11)
					name = new TextComponentTranslation("tooltip.thebetweenlands.barnacle_stage_4.name").getFormattedText();
			}

			ItemStack patchedStack = new ItemStack(Item.getItemFromBlock(BlockRegistry.BARNACLE_1_2)).setTranslatableName(name);
			return patchedStack;
		}
        return ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> tooltip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        tooltip.clear();

        // If universal bucket is enabled the bucket item is displayed with swamp water, leave like that then to have mismatched names
        // No real way of overriding the bucket
        if (!FluidRegistry.isUniversalBucketEnabled()) {
            String name = null;
            String displayName = DisplayUtil.itemDisplayNameShort(itemStack);
            if (displayName != null && !displayName.endsWith("Unnamed"))
                name = displayName;
            if (name != null)
                tooltip.add(name);

            if (ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_METADATA, true) && !Strings.isNullOrEmpty(FormattingConfig.metaFormat))
                tooltip.add("\u00a7r" + String.format(FormattingConfig.metaFormat, accessor.getBlock().getRegistryName().toString(), accessor.getMetadata()));
        }

        return tooltip;
    }
}
