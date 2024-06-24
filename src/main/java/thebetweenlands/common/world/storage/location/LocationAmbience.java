package thebetweenlands.common.world.storage.location;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public class LocationAmbience {
	public enum EnumLocationAmbience {
		NONE("none"),
		WIGHT_TOWER("wightTower"),
		SLUDGE_WORM_DUNGEON("sludgeWormDungeon"),
		OTHER("other");

		public static final EnumLocationAmbience[] TYPES = EnumLocationAmbience.values();

		public final String name;

		EnumLocationAmbience(String name) {
			this.name = name;
		}

		public static EnumLocationAmbience fromName(String name) {
			for (EnumLocationAmbience type : TYPES) {
				if (type.name.equals(name))
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
	private boolean caveFog = true;

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

	public LocationAmbience setCaveFog(boolean caveFog) {
		this.caveFog = caveFog;
		return this;
	}

	public boolean hasCaveFog() {
		return this.caveFog;
	}

	public static LocationAmbience readFromNBT(LocationStorage location, CompoundTag tag) {
		EnumLocationAmbience type = EnumLocationAmbience.fromName(tag.getString("type"));
		if (type != EnumLocationAmbience.NONE) {
			LocationAmbience ambience = new LocationAmbience(type);
			ambience.setLocation(location);
			if (tag.contains("fogStart", Tag.TAG_FLOAT) && tag.contains("fogEnd", Tag.TAG_FLOAT)) {
				ambience.setFogRange(tag.getFloat("fogStart"), tag.getFloat("fogEnd"));
			}
			if (tag.contains("fogColor", Tag.TAG_INT_ARRAY)) {
				ambience.setFogColor(tag.getIntArray("fogColor"));
			}
			if (tag.contains("fogBrightness", Tag.TAG_INT)) {
				ambience.setFogBrightness(tag.getInt("fogBrightness"));
			}
			if (tag.contains("fogColorMultiplier", Tag.TAG_FLOAT)) {
				ambience.setFogColorMultiplier(tag.getFloat("fogColorMultiplier"));
			}
			if (tag.contains("fogRangeMultiplier", Tag.TAG_FLOAT)) {
				ambience.setFogRangeMultiplier(tag.getFloat("fogRangeMultiplier"));
			}
			if (tag.contains("caveFog", Tag.TAG_BYTE)) {
				ambience.setCaveFog(tag.getBoolean("caveFog"));
			} else {
				ambience.setCaveFog(true);
			}
			return ambience;
		}
		return null;
	}

	public void writeToNBT(CompoundTag tag) {
		tag.putString("type", this.type != null ? this.type.name : EnumLocationAmbience.NONE.name);
		if (this.hasFogRange()) {
			tag.putFloat("fogStart", this.fogStart);
			tag.putFloat("fogEnd", this.fogEnd);
		}
		if (this.hasFogColor()) {
			tag.putIntArray("fogColor", this.fogColor);
		}
		if (this.hasFogBrightness()) {
			tag.putInt("fogBrightness", this.fogBrightness);
		}
		if (this.hasFogColorMultiplier()) {
			tag.putFloat("fogColorMultiplier", this.fogColorMultiplier);
		}
		if (this.hasFogRangeMultiplier()) {
			tag.putFloat("fogRangeMultiplier", this.fogRangeMultiplier);
		}
		tag.putBoolean("caveFog", this.hasCaveFog());
	}
}
