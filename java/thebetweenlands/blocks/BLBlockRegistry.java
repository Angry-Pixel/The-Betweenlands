package thebetweenlands.blocks;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import cpw.mods.fml.common.registry.GameRegistry;

public class BLBlockRegistry {

	// LIST WITH ALL BLOCKS IN THIS CLASS
	public static final List<Block> BLOCKS = new LinkedList<Block>();

	// PORTAL

	// TERRAIN
	public static final Block betweenstone = new BlockBetweenstone();

	// ORES

	// WOOD
	
	// DOUBLE PLANTS
	

	// SMALL PLANTS

	// UNDERGROWTH
	
	// DECORATIONS AND UTILITIES
	
	// ALTARS

	// STAIRS, SLABS, WALLS
	
	// OTHER THINGS

	public static void init() {
		initBlocks();
		registerBlocks();
		registerProperties();
	}

	private static void initBlocks() {
		
	}

	private static void registerBlocks() {
		try {
			for (Field f : BLBlockRegistry.class.getDeclaredFields()) {
				Object obj = f.get(null);
				if (obj instanceof Block)
					registerBlock((Block) obj);
				else if (obj instanceof Block[])
					for (Block block : (Block[]) obj)
						registerBlock(block);
			}
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private static void registerBlock(Block block) {
		BLOCKS.add(block);
		String name = block.getUnlocalizedName();
		String[] strings = name.split("\\.");

		if (block instanceof ISubBlocksBlock)
			GameRegistry.registerBlock(block, ((ISubBlocksBlock) block).getItemBlockClass(), strings[strings.length - 1]);
		else
			GameRegistry.registerBlock(block, strings[strings.length - 1]);
	}

	private static void registerProperties() {
		//for fire etc
	}

	public static interface ISubBlocksBlock {
		Class<? extends ItemBlock> getItemBlockClass();
	}
}