package thebetweenlands.common.sound;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import thebetweenlands.common.lib.ModInfo;

public class BLSoundEvent extends SoundEvent {
    public final String name;

    public final SoundCategory category;

    public BLSoundEvent(String soundName) {
        this(soundName, SoundCategory.NEUTRAL);
    }

    // We might want do something with this?
    public BLSoundEvent(String name, SoundCategory category) {
        super(new ResourceLocation(ModInfo.ID, name));
        this.name = name;
        this.category = category;
        setRegistryName(new ResourceLocation(ModInfo.ID, name));
    }
}
