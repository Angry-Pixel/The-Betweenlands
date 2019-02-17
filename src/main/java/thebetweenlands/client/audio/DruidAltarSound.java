package thebetweenlands.client.audio;

import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.tile.TileEntityDruidAltar;

@OnlyIn(Dist.CLIENT)
public class DruidAltarSound extends TileEntitySound<TileEntityDruidAltar> {
	public DruidAltarSound(SoundEvent sound, SoundCategory category, TileEntityDruidAltar altar) {
		super(sound, category, altar, (tile) -> tile.craftingProgress > 0);
	}
}
