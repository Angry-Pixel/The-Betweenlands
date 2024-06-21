package thebetweenlands.common.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thebetweenlands.common.TheBetweenlands;

public class CreativeGroupRegistry {

	public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TheBetweenlands.ID);
	
	// Blocks tab
	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BETWEELANDS_BLOCKS = CREATIVE_TABS.register("thebetweenlands_block", () -> CreativeModeTab.builder()
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

	// Plants tab
	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BETWEELANDS_PLANTS = CREATIVE_TABS.register("betweelands_plants", () -> CreativeModeTab.builder()
			.title(Component.translatable("itemGroup.thebetweenlands.betweelands_plants"))
			.icon(() -> new ItemStack(BlockRegistry.WEEDWOOD_SAPLING.get()))
			.withTabsBefore(BETWEELANDS_BLOCKS.getId()) //TODO: Mire Coral
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

	public static final CreativeModeTab BETWEELANDS_SPECIAL = new CreativeModeTab("betweelands_special_tab") {

		@Override
		public ItemStack makeIcon() {
			return new ItemStack(ItemRegistry.RECORD_ASTATOS.get());
		}
	};
}
