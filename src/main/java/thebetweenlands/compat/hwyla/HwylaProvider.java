package thebetweenlands.compat.hwyla;

import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.entity.MultiPartEntityPart;
import thebetweenlands.common.block.container.BlockCrabPot;
import thebetweenlands.common.block.container.BlockCrabPotFilter;
import thebetweenlands.common.block.farming.BlockBarnacle_1_2;
import thebetweenlands.common.block.farming.BlockBarnacle_3_4;
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
        registrar.registerHeadProvider(underwaterPlantProvider, BlockCrabPot.class);
        registrar.registerHeadProvider(underwaterPlantProvider, BlockCrabPotFilter.class);
        
        UnderwaterBarnacleProvider underwaterBarnacleProvider = new UnderwaterBarnacleProvider();
        registrar.registerStackProvider(underwaterBarnacleProvider, BlockBarnacle_1_2.class);
        registrar.registerStackProvider(underwaterBarnacleProvider, BlockBarnacle_3_4.class);
        registrar.registerHeadProvider(underwaterBarnacleProvider, BlockBarnacle_1_2.class);
        registrar.registerHeadProvider(underwaterBarnacleProvider, BlockBarnacle_3_4.class);
    }

}
