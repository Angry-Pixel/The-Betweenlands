package thebetweenlands.compat.hwyla;

import mcp.mobius.waila.api.IWailaRegistrar;

import net.minecraft.entity.MultiPartEntityPart;
import thebetweenlands.common.block.plant.BlockPlantUnderwater;
import thebetweenlands.common.block.plant.BlockStackablePlantUnderwater;

public class HwylaProvider  {

    @SuppressWarnings("unused")
    public static void callbackRegister(IWailaRegistrar registrar) {
        MultiPartProvider partProvider = new MultiPartProvider();
        registrar.registerTailProvider(partProvider, MultiPartEntityPart.class);
        registrar.registerBodyProvider(partProvider, MultiPartEntityPart.class);
        registrar.registerHeadProvider(partProvider, MultiPartEntityPart.class);

        UnderwaterPlantProvider underwaterPlantProvider = new UnderwaterPlantProvider();
        registrar.registerStackProvider(underwaterPlantProvider, BlockStackablePlantUnderwater.class);
        registrar.registerHeadProvider(underwaterPlantProvider, BlockPlantUnderwater.class);
    }

}
