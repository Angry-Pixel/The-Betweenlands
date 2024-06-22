package thebetweenlands.common.blocks;

import net.minecraft.world.level.block.Block;

public class BetweenlandsBlock extends Block {

	public BetweenlandsBlock(Properties p_48756_) {
		super(p_48756_);
	}

	// Make all tools except for listed "betweenlands" tools in config inefective
	// (alows for more flexible mod ajustment for modpack developers)
	// todo: alow other mods to add to list for automation
	// optimisation: block must remeber last tool use on it to compair with tool curent
	// to stop unessesery regestery checks (thanks to fast crafting devs for idea)

	// Emit sheild particle if not a betweenlands tool
}