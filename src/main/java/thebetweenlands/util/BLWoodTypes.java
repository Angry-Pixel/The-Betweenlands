package thebetweenlands.util;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import thebetweenlands.common.TheBetweenlands;

public class BLWoodTypes {

	public static final BlockSetType WEEDWOOD_BLOCK_SET = BlockSetType.register(new BlockSetType(TheBetweenlands.prefix("weedwood").toString()));
	public static final BlockSetType RUBBER_TREE_BLOCK_SET = BlockSetType.register(new BlockSetType(TheBetweenlands.prefix("rubber_tree").toString()));
	public static final BlockSetType GIANT_ROOT_BLOCK_SET = BlockSetType.register(new BlockSetType(TheBetweenlands.prefix("giant_root").toString()));
	public static final BlockSetType HEARTHGROVE_BLOCK_SET = BlockSetType.register(new BlockSetType(TheBetweenlands.prefix("hearthgrove").toString()));
	public static final BlockSetType NIBBLETWIG_BLOCK_SET = BlockSetType.register(new BlockSetType(TheBetweenlands.prefix("nibbletwig").toString()));
	public static final BlockSetType ROTTEN_BLOCK_SET = BlockSetType.register(new BlockSetType(TheBetweenlands.prefix("rotten").toString()));
	public static final BlockSetType BETWEENSTONE = BlockSetType.register(new BlockSetType(TheBetweenlands.prefix("betweenstone").toString(),
		true, true, false,
		BlockSetType.PressurePlateSensitivity.MOBS, SoundType.STONE,
		SoundEvents.IRON_DOOR_CLOSE, SoundEvents.IRON_DOOR_OPEN,
		SoundEvents.IRON_TRAPDOOR_CLOSE, SoundEvents.IRON_TRAPDOOR_OPEN,
		SoundEvents.STONE_PRESSURE_PLATE_CLICK_OFF, SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON,
		SoundEvents.STONE_BUTTON_CLICK_OFF, SoundEvents.STONE_BUTTON_CLICK_ON));
	public static final BlockSetType SYRMORITE = BlockSetType.register(new BlockSetType(TheBetweenlands.prefix("syrmorite").toString(),
		false, false, false,
		BlockSetType.PressurePlateSensitivity.EVERYTHING, SoundType.METAL,
		SoundEvents.IRON_DOOR_CLOSE, SoundEvents.IRON_DOOR_OPEN,
		SoundEvents.IRON_TRAPDOOR_CLOSE, SoundEvents.IRON_TRAPDOOR_OPEN,
		SoundEvents.METAL_PRESSURE_PLATE_CLICK_OFF, SoundEvents.METAL_PRESSURE_PLATE_CLICK_ON,
		SoundEvents.STONE_BUTTON_CLICK_OFF, SoundEvents.STONE_BUTTON_CLICK_ON));
	public static final BlockSetType SCABYST = BlockSetType.register(new BlockSetType(TheBetweenlands.prefix("scabyst").toString(),
		false, false, false,
		BlockSetType.PressurePlateSensitivity.EVERYTHING, SoundType.METAL,
		SoundEvents.IRON_DOOR_CLOSE, SoundEvents.IRON_DOOR_OPEN,
		SoundEvents.IRON_TRAPDOOR_CLOSE, SoundEvents.IRON_TRAPDOOR_OPEN,
		SoundEvents.METAL_PRESSURE_PLATE_CLICK_OFF, SoundEvents.METAL_PRESSURE_PLATE_CLICK_ON,
		SoundEvents.STONE_BUTTON_CLICK_OFF, SoundEvents.STONE_BUTTON_CLICK_ON));

	public static final WoodType WEEDWOOD_WOOD_TYPE = WoodType.register(new WoodType(TheBetweenlands.prefix("weedwood").toString(), WEEDWOOD_BLOCK_SET));
	public static final WoodType RUBBER_TREE_WOOD_TYPE = WoodType.register(new WoodType(TheBetweenlands.prefix("rubber_tree").toString(), RUBBER_TREE_BLOCK_SET));
	public static final WoodType GIANT_ROOT_WOOD_TYPE = WoodType.register(new WoodType(TheBetweenlands.prefix("giant_root").toString(), GIANT_ROOT_BLOCK_SET));
	public static final WoodType HEARTHGROVE_WOOD_TYPE = WoodType.register(new WoodType(TheBetweenlands.prefix("hearthgrove").toString(), HEARTHGROVE_BLOCK_SET));
	public static final WoodType NIBBLETWIG_WOOD_TYPE = WoodType.register(new WoodType(TheBetweenlands.prefix("nibbletwig").toString(), NIBBLETWIG_BLOCK_SET));
	public static final WoodType ROTTEN_WOOD_TYPE = WoodType.register(new WoodType(TheBetweenlands.prefix("rotten").toString(), ROTTEN_BLOCK_SET));

}
