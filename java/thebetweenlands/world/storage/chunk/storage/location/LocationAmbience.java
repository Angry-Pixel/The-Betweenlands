package thebetweenlands.world.storage.chunk.storage.location;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;

public class LocationAmbience {
	public static enum EnumLocationAmbience {
		NONE("none"), WIGHT_TOWER("wightTower");

		public static EnumLocationAmbience[] TYPES = EnumLocationAmbience.values();

		public final String name;
		private EnumLocationAmbience(String name) {
			this.name = name;
		}
		public static EnumLocationAmbience fromName(String name) {
			for(EnumLocationAmbience type : TYPES){
				if(type.name.equals(name))
					return type;
			}
			return NONE;
		}
	}

	public final EnumLocationAmbience type;

	public LocationAmbience(EnumLocationAmbience type) {
		this.type = type;
	}

	public static LocationAmbience readFromNBT(NBTTagCompound nbt) {
		EnumLocationAmbience type = EnumLocationAmbience.fromName(nbt.getString("type"));
		return new LocationAmbience(type);
	}

	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setString("type", this.type != null ? this.type.name : EnumLocationAmbience.NONE.name);
	}
}
