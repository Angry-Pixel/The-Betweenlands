package thebetweenlands.common.registries;

import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.JukeboxSong;
import thebetweenlands.common.TheBetweenlands;

//TODO: Music for records are registered now. They also require a length in seconds and a comparator output
public class MusicRegistry {

	public static final ResourceKey<JukeboxSong> ASTATOS = ResourceKey.create(Registries.JUKEBOX_SONG, ResourceLocation.fromNamespaceAndPath(TheBetweenlands.ID, "astatos"));

	public static void bootstrap(BootstrapContext<JukeboxSong> context) {
		context.register(ASTATOS, new JukeboxSong(SoundRegistry.RECORD_ASTATOS, Component.translatable(Util.makeDescriptionId("jukebox_song", ASTATOS.location())), 366.0F, 1));
	}
}
