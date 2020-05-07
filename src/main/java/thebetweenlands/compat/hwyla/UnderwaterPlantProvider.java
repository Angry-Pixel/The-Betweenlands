package thebetweenlands.compat.hwyla;

import com.google.common.base.Strings;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.config.FormattingConfig;
import mcp.mobius.waila.overlay.DisplayUtil;
import mcp.mobius.waila.utils.Constants;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.FluidRegistry;
import thebetweenlands.common.block.plant.BlockStackablePlantUnderwater;
import thebetweenlands.common.registries.ItemRegistry;

import javax.annotation.Nonnull;
import java.util.List;

public class UnderwaterPlantProvider implements IWailaDataProvider {

    @Nonnull
    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        Block block = accessor.getBlock();
        if (block instanceof BlockStackablePlantUnderwater) {
            List<ItemStack> stacks = ((BlockStackablePlantUnderwater) block).onSheared(new ItemStack(ItemRegistry.SYRMORITE_SHEARS), accessor.getWorld(), accessor.getPosition(), 0);
            if (!stacks.isEmpty()) {
                return stacks.get(0);
            }
            ItemStack pickBlock = block.getPickBlock(accessor.getBlockState(), accessor.getMOP(), accessor.getWorld(), accessor.getPosition(), accessor.getPlayer());
            if (!pickBlock.isEmpty()) {
                return pickBlock;
            }

            Item item = block.getItemDropped(accessor.getBlockState(), accessor.getWorld().rand, 0);
            if (item != null) {
                return new ItemStack(item);
            }
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
