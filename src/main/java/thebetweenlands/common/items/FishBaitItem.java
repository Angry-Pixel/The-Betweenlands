package thebetweenlands.common.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.component.item.FishBaitStats;
import thebetweenlands.common.entities.fishing.FishBait;
import thebetweenlands.common.registries.DataComponentRegistry;

import java.util.List;

public class FishBaitItem extends Item {

	public FishBaitItem(Properties properties) {
		super(properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		if (stack.has(DataComponentRegistry.FISH_BAIT)) {
			FishBaitStats stats = stack.get(DataComponentRegistry.FISH_BAIT);
			//	tooltip.add(TranslationHelper.translateToLocal("tooltip.bl.fish_bait.saturation", stack.getTagCompound().getInteger("saturation")));
			tooltip.add(Component.translatable("tooltip.bl.fish_bait.sink_speed", stats.sinkSpeed()));
			tooltip.add(Component.translatable("tooltip.bl.fish_bait.dissolve_time", stats.dissolveTime()));
			tooltip.add(Component.translatable("tooltip.bl.fish_bait.range", stats.range()));
			tooltip.add(Component.translatable("tooltip.bl.fish_bait.glowing", stats.glowing()));
		}
	}

	@Override
	public boolean hasCustomEntity(ItemStack stack) {
		return true;
	}

	@Override
	public @Nullable Entity createEntity(Level level, Entity location, ItemStack stack) {
		FishBait entity = new FishBait(level, location.getX(), location.getY() + ((double) location.getEyeHeight() * 0.75D - 0.1D), location.getZ(), stack);
		entity.setDeltaMovement(location.getDeltaMovement());
		entity.setPickUpDelay(10);
//		entity.setBaitSaturation(stack.getTagCompound().getInteger("saturation"));
		entity.setStats(stack.getOrDefault(DataComponentRegistry.FISH_BAIT, FishBaitStats.DEFAULT));
		return entity;
	}
}
