package thebetweenlands.client.audio;

import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.tile.TileEntityDruidAltar;

@SideOnly(Side.CLIENT)
public class DruidAltarSound extends TileEntitySound<TileEntityDruidAltar> {
	public DruidAltarSound(SoundEvent sound, SoundCategory category, TileEntityDruidAltar altar) {
		super(sound, category, altar, (tile) -> tile.craftingProgress > 0);
	}
}
