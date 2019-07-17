package thebetweenlands.common.world.storage.location;

import net.minecraft.nbt.NBTTagCompound;

public class LocationAmbience {
	public static enum EnumLocationAmbience {
		NONE("none"), WIGHT_TOWER("wightTower"), SLUDGE_WORM_DUNGEON("sludgeWormDungeon"), OTHER("other");

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

	private LocationStorage location;
	public final EnumLocationAmbience type;
	private float fogStart = -1, fogEnd = -1;
	private float fogRangeMultiplier = -1;
	private int[] fogColor = null;
	private int fogBrightness = -1;
	private float fogColorMultiplier = -1;

	public LocationAmbience(EnumLocationAmbience type) {
		this.type = type;
	}

	LocationAmbience setLocation(LocationStorage location) {
		this.location = location;
		return this;
	}

	public LocationStorage getLocation() {
		return this.location;
	}

	public LocationAmbience setFogRange(float start, float end) {
		this.fogStart = start;
		this.fogEnd = end;
		return this;
	}

	public boolean hasFogRange() {
		return this.fogStart >= 0 && this.fogEnd >= 0;
	}

	public float getFogStart() {
		return this.fogStart;
	}

	public float getFogEnd() {
		return this.fogEnd;
	}

	public LocationAmbience setFogRangeMultiplier(float multiplier) {
		this.fogRangeMultiplier = multiplier;
		return this;
	}

	public boolean hasFogRangeMultiplier() {
		return this.fogRangeMultiplier >= 0;
	}

	public float getFogRangeMultiplier() {
		return this.fogRangeMultiplier;
	}

	public LocationAmbience setFogColor(int[] color) {
		this.fogColor = color;
		return this;
	}

	public boolean hasFogColor() {
		return this.fogColor != null && this.fogColor.length == 3;
	}

	public int[] getFogColor() {
		return this.fogColor;
	}

	public LocationAmbience setFogBrightness(int brightness) {
		this.fogBrightness = brightness;
		return this;
	}

	public boolean hasFogBrightness() {
		return this.fogBrightness >= 0;
	}

	public int getFogBrightness() {
		return this.fogBrightness;
	}

	public LocationAmbience setFogColorMultiplier(float multiplier) {
		this.fogColorMultiplier = multiplier;
		return this;
	}

	public boolean hasFogColorMultiplier() {
		return this.fogColorMultiplier >= 0;
	}

	public float getFogColorMultiplier() {
		return this.fogColorMultiplier;
	}

	public static LocationAmbience readFromNBT(LocationStorage location, NBTTagCompound nbt) {
		EnumLocationAmbience type = EnumLocationAmbience.fromName(nbt.getString("type"));
		if(type != EnumLocationAmbience.NONE) {
			LocationAmbience ambience = new LocationAmbience(type);
			ambience.setLocation(location);
			if(nbt.hasKey("fogStart") && nbt.hasKey("fogEnd")) {
				ambience.setFogRange(nbt.getFloat("fogStart"), nbt.getFloat("fogEnd"));
			}
			if(nbt.hasKey("fogColor")) {
				ambience.setFogColor(nbt.getIntArray("fogColor"));
			}
			if(nbt.hasKey("fogBrightness")) {
				ambience.setFogBrightness(nbt.getInteger("fogBrightness"));
			}
			if(nbt.hasKey("fogColorMultiplier")) {
				ambience.setFogColorMultiplier(nbt.getFloat("fogColorMultiplier"));
			}
			if(nbt.hasKey("fogRangeMultiplier")) {
				ambience.setFogRangeMultiplier(nbt.getFloat("fogRangeMultiplier"));
			}
			return ambience;
		}
		return null;
	}

	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setString("type", this.type != null ? this.type.name : EnumLocationAmbience.NONE.name);
		if(this.hasFogRange()) {
			nbt.setFloat("fogStart", this.fogStart);
			nbt.setFloat("fogEnd", this.fogEnd);
		}
		if(this.hasFogColor()) {
			nbt.setIntArray("fogColor", this.fogColor);
		}
		if(this.hasFogBrightness()) {
			nbt.setInteger("fogBrightness", this.fogBrightness);
		}
		if(this.hasFogColorMultiplier()) {
			nbt.setFloat("fogColorMultiplier", this.fogColorMultiplier);
		}
		if(this.hasFogRangeMultiplier()) {
			nbt.setFloat("fogRangeMultiplier", this.fogRangeMultiplier);
		}
	}
}
