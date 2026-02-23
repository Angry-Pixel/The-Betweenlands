package thebetweenlands.common.capability;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.world.WorldlyContainer;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;

public class CompostBinWrapper extends SidedInvWrapper {

	public CompostBinWrapper(WorldlyContainer inv, @Nullable Direction side) {
		super(inv, side);
	}

}
