package thebetweenlands.common.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thebetweenlands.common.TheBetweenlands;

public class CreativeGroupRegistry {

	public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TheBetweenlands.ID);

	// Blocks tab
	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BETWEENLANDS_BLOCKS = CREATIVE_TABS.register("thebetweenlands_block", () -> CreativeModeTab.builder()
			.title(Component.translatable("itemGroup.thebetweenlands.betweenlands_block"))
			.icon(() -> new ItemStack(BlockRegistry.SWAMP_GRASS.get()))
			.displayItems((parameters, output) -> {
				//Landscape
				output.accept(BlockRegistry.MUD.get());
				output.accept(BlockRegistry.SILT.get());
				output.accept(BlockRegistry.PEAT.get());
				output.accept(BlockRegistry.SWAMP_DIRT.get());
				output.accept(BlockRegistry.DEAD_SWAMP_GRASS.get());
				output.accept(BlockRegistry.SWAMP_GRASS.get());
				output.accept(BlockRegistry.BETWEENSTONE.get());
				output.accept(BlockRegistry.PITSTONE.get());
				output.accept(BlockRegistry.LIMESTONE.get());
				output.accept(BlockRegistry.CRAGROCK.get());
				output.accept(BlockRegistry.BETWEENLANDS_BEDROCK.get());
				//Leaves
				output.accept(BlockRegistry.LEAVES_WEEDWOOD_TREE.get());
				output.accept(BlockRegistry.LEAVES_SAP_TREE.get());
				output.accept(BlockRegistry.LEAVES_RUBBER_TREE.get());
				output.accept(BlockRegistry.LEAVES_HEARTHGROVE_TREE.get());
				output.accept(BlockRegistry.LEAVES_NIBBLETWIG_TREE.get());
				//Woods
				output.accept(BlockRegistry.WEEDWOOD_BARK_LOG.get());
				output.accept(BlockRegistry.WEEDWOOD_LOG.get());
				output.accept(BlockRegistry.WEEDWOOD.get());
				output.accept(BlockRegistry.LOG_NIBBLETWIG.get());
				output.accept(BlockRegistry.SAP_BARK_LOG.get());
				output.accept(BlockRegistry.SAP_LOG.get());
				output.accept(BlockRegistry.RUBBER_LOG.get());
				output.accept(BlockRegistry.LOG_HEARTHGROVE.get());
				output.accept(BlockRegistry.GIANT_ROOT.get());
				//Plants
				output.accept(BlockRegistry.BULB_CAPPED_MUSHROOM_CAP.get());
				output.accept(BlockRegistry.BULB_CAPPED_MUSHROOM_STALK.get());
				//Ores
				output.accept(BlockRegistry.SULFUR_ORE.get());
				output.accept(BlockRegistry.SYRMORITE_ORE.get());
				output.accept(BlockRegistry.SLIMY_BONE_ORE.get());
				output.accept(BlockRegistry.OCTINE_ORE.get());
				output.accept(BlockRegistry.SCABYST_ORE.get());
				output.accept(BlockRegistry.VALONITE_ORE.get());
			}).build());

	//Special tab
	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BETWEENLANDS_SPECIAL = CREATIVE_TABS.register("betweenlands_special", () -> CreativeModeTab.builder()
			.title(Component.translatable("itemGroup.thebetweenlands.betweenlands_special"))
			.icon(() -> new ItemStack(ItemRegistry.RECORD_ASTATOS.get()))
			.withTabsBefore(BETWEENLANDS_BLOCKS.getId())
			.displayItems((parameters, output) -> {
				output.accept(ItemRegistry.PORTAL.get()); //TODO: temp
				output.accept(ItemRegistry.AMATE_MAP.get());
				output.accept(ItemRegistry.FILLED_AMATE_MAP.get()); //TODO: Remove?
				output.accept(ItemRegistry.RECORD_ASTATOS.get());
			}).build());

	// Plants tab
	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BETWEENLANDS_PLANTS = CREATIVE_TABS.register("betweenlands_plants", () -> CreativeModeTab.builder()
			.title(Component.translatable("itemGroup.thebetweenlands.betweenlands_plants"))
			.icon(() -> new ItemStack(BlockRegistry.WEEDWOOD_SAPLING.get())) //TODO: Mire Coral
			.withTabsBefore(BETWEENLANDS_SPECIAL.getId())
			.displayItems((parameters, output) -> {
				output.accept(BlockRegistry.WEEDWOOD_SAPLING.get());
				output.accept(BlockRegistry.SAP_SAPLING.get());
				output.accept(BlockRegistry.RUBBER_SAPLING.get());
				output.accept(BlockRegistry.NIBBLETWIG_SAPLING.get());
				output.accept(BlockRegistry.BULB_CAPED_MUSHROOM.get());
				output.accept(BlockRegistry.SHELF_FUNGUS.get());
				output.accept(BlockRegistry.MOSS.get());
				output.accept(BlockRegistry.THORNS.get());
				output.accept(BlockRegistry.POISON_IVY.get());
				output.accept(BlockRegistry.CAVE_MOSS.get());
				output.accept(BlockRegistry.SWAMP_REED.get());
				output.accept(BlockRegistry.HANGER.get());
				output.accept(BlockRegistry.SWAMP_TALLGRASS.get());
			}).build());

	public static void populateTabs(BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
			event.accept(ItemRegistry.SWAMP_HAG_SPAWN_EGG.get());
			event.accept(ItemRegistry.GECKO_SPAWN_EGG.get());
			event.accept(ItemRegistry.WIGHT_SPAWN_EGG.get());
		}
	}
}
