package thebetweenlands.common.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import thebetweenlands.TheBetweenlands;

@Mod.EventBusSubscriber(modid = "thebetweenlands", bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();

        // Common
        gen.addProvider(new BetweenlandsBlockTagsProvider(gen, event.getExistingFileHelper()));

        // Client
        if (event.includeClient()) {
            gen.addProvider(new BetweenlandsBlockStateProvider(gen, event.getExistingFileHelper()));
        }

        // Server
        if (event.includeServer()) {
        }
    }
}
