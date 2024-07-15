package thebetweenlands.common.registries;

import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.JukeboxSong;
import thebetweenlands.common.TheBetweenlands;

public class MusicRegistry {

	public static final ResourceKey<JukeboxSong> ASTATOS = makeKey("astatos");
	public static final ResourceKey<JukeboxSong> BETWEEN_YOU_AND_ME = makeKey("between_you_and_me");
	public static final ResourceKey<JukeboxSong> CHRISTMAS_ON_THE_MARSH = makeKey("christmas_on_the_marsh");
	public static final ResourceKey<JukeboxSong> THE_EXPLORER = makeKey("the_explorer");
	public static final ResourceKey<JukeboxSong> HAG_DANCE = makeKey("hag_dance");
	public static final ResourceKey<JukeboxSong> LONELY_FIRE = makeKey("lonely_fire");
	public static final ResourceKey<JukeboxSong> MYSTERIOUS_RECORD = makeKey("mysterious_record");
	public static final ResourceKey<JukeboxSong> ANCIENT = makeKey("ancient");
	public static final ResourceKey<JukeboxSong> BENEATH_A_GREEN_SKY = makeKey("beneath_a_green_sky");
	public static final ResourceKey<JukeboxSong> DJ_WIGHTS_MIXTAPE = makeKey("dj_wights_mixtape");
	public static final ResourceKey<JukeboxSong> ONWARDS = makeKey("onwards");
	public static final ResourceKey<JukeboxSong> STUCK_IN_THE_MUD = makeKey("stuck_in_the_mud");
	public static final ResourceKey<JukeboxSong> WANDERING_WISPS = makeKey("wandering_wisps");
	public static final ResourceKey<JukeboxSong> WATERLOGGED = makeKey("waterlogged");
	public static final ResourceKey<JukeboxSong> DEEP_WATER_THEME = makeKey("deep_water_theme");

	private static ResourceKey<JukeboxSong> makeKey(String name) {
		return ResourceKey.create(Registries.JUKEBOX_SONG, TheBetweenlands.prefix(name));
	}

	public static void bootstrap(BootstrapContext<JukeboxSong> context) {
		register(context, ASTATOS, SoundRegistry.ASTATOS, 366.0F, 1);
		register(context, BETWEEN_YOU_AND_ME, SoundRegistry.BETWEEN_YOU_AND_ME, 298.0F, 2);
		register(context, CHRISTMAS_ON_THE_MARSH, SoundRegistry.CHRISTMAS_ON_THE_MARSH, 219.0F, 3);
		register(context, THE_EXPLORER, SoundRegistry.THE_EXPLORER, 400.0F, 4);
		register(context, HAG_DANCE, SoundRegistry.HAG_DANCE, 280.0F, 5);
		register(context, LONELY_FIRE, SoundRegistry.LONELY_FIRE, 227.0F, 6);
		register(context, MYSTERIOUS_RECORD, SoundRegistry._16612, 65.0F, 7);
		register(context, ANCIENT, SoundRegistry.ANCIENT, 181.0F, 8);
		register(context, BENEATH_A_GREEN_SKY, SoundRegistry.BENEATH_A_GREEN_SKY, 309.0F, 9);
		register(context, DJ_WIGHTS_MIXTAPE, SoundRegistry.DJ_WIGHTS_MIXTAPE, 226.0F, 10);
		register(context, ONWARDS, SoundRegistry.ONWARD, 210.0F, 11);
		register(context, STUCK_IN_THE_MUD, SoundRegistry.STUCK_IN_THE_MUD, 277.0F, 12);
		register(context, WANDERING_WISPS, SoundRegistry.WANDERING_WISPS, 204.0F, 13);
		register(context, WATERLOGGED, SoundRegistry.WATERLOGGED, 195.0F, 14);
		register(context, DEEP_WATER_THEME, SoundRegistry.DEEP_WATER_THEME, 161.0F, 15);
	}

	private static void register(BootstrapContext<JukeboxSong> context, ResourceKey<JukeboxSong> key, Holder<SoundEvent> sound, float length, int output) {
		context.register(key, new JukeboxSong(sound, Component.translatable(Util.makeDescriptionId("jukebox_song", key.location())), length, output));
	}
}
