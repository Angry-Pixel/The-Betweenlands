package thebetweenlands.client.sound;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import thebetweenlands.common.lib.ModInfo;

public class BLSoundEvent extends SoundEvent {
    public SoundCategory soundCategory;
    public String soundName;

    public BLSoundEvent(String soundName) {
        super(new ResourceLocation(ModInfo.ID, soundName));
        setRegistryName(new ResourceLocation(ModInfo.ID, soundName));
        this.soundName = soundName;
    }

    //We might want do something with this?
    public BLSoundEvent(String soundName, SoundCategory soundCategory) {
        super(new ResourceLocation(ModInfo.ID, soundName));
        setRegistryName(new ResourceLocation(ModInfo.ID, soundName));
        this.soundCategory = soundCategory;
        this.soundName = soundName;
    }
}
