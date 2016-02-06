package thebetweenlands.connection;

import thebetweenlands.tileentities.connection.Connection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import thebetweenlands.items.lanterns.LightVariant;
import thebetweenlands.utils.Catenary;
import thebetweenlands.utils.Segment;
import thebetweenlands.utils.mc.EnumDyeColor;
import thebetweenlands.utils.vectormath.Point3f;
import thebetweenlands.utils.vectormath.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class ConnectionLogicFairyLights extends ConnectionLogic {
	private Light[] lightPoints;

	private Light[] prevLightPoints;

	private List<PatternLightData> pattern;

	private boolean twinkle;

	private boolean tight;

	public ConnectionLogicFairyLights(Connection connection) {
		super(connection);
		pattern = new ArrayList<PatternLightData>();
	}

	public Light[] getLightPoints() {
		return lightPoints;
	}

	public Light[] getPrevLightPoints() {
		return prevLightPoints;
	}

	public List<PatternLightData> getPattern() {
		return pattern;
	}

	public boolean isTight() {
		return tight;
	}

	public void setPatern(List<PatternLightData> pattern) {
		this.pattern = pattern;
	}

	@Override
	public void onUpdate() {
		prevLightPoints = lightPoints;
	}

	@Override
	public void onUpdateEnd() {
		updateLights();
	}

	@Override
	public void onRecalculateCatenary() {
		updateLightVertices();
	}

	@Override
	public Catenary createCatenary(Point3f to) {
		return Catenary.from(new Vector3f(to), tight);
	}

	public void updateLights() {
		if (lightPoints != null) {
			for (int i = 0; i < lightPoints.length; i++) {
				Light light = lightPoints[i];
				if (pattern.size() > 0) {
					PatternLightData lightData = pattern.get(i % pattern.size());
					light.setVariant(lightData.getLightVariant());
					light.setColor(EnumDyeColor.byDyeDamage(lightData.getColor()).getMapColor().colorValue);
				}
			}
		}
	}

	private void updateLightVertices() {
		Catenary catenary = getConnection().getCatenary();
		if (catenary != null) {
			float spacing = 16;
			for (PatternLightData patternLightData : pattern) {
				float lightSpacing = patternLightData.getLightVariant().getSpacing();
				if (lightSpacing > spacing) {
					spacing = lightSpacing;
				}
			}
			float totalLength = catenary.getLength();
			// simplified version of t / 2 - ((int) (t / s) - 1) * s / 2
			float distance = (totalLength % spacing + spacing) / 2;
			Segment[] segments = catenary.getSegments();
			prevLightPoints = lightPoints;
			lightPoints = new Light[(int) (totalLength / spacing)];
			int lightIndex = 0;
			for (Segment segment : segments) {
				float length = segment.getLength();
				while (distance < length) {
					Light light = new Light(segment.pointAt(distance / length));
					light.setRotation(segment.getRotation());
					lightPoints[lightIndex++] = light;
					distance += spacing;
				}
				distance -= length;
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		NBTTagList tagList = new NBTTagList();
		for (PatternLightData b : pattern) {
			NBTTagCompound patternCompound = new NBTTagCompound();
			patternCompound.setInteger("light", b.getLightVariant().ordinal());
			patternCompound.setByte("color", b.getColor());
			tagList.appendTag(patternCompound);
		}
		compound.setTag("pattern", tagList);
		compound.setBoolean("twinkle", twinkle);
		compound.setBoolean("tight", tight);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		NBTTagList tagList = compound.getTagList("pattern", 10);
		pattern = new ArrayList<PatternLightData>();
		for (int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound lightCompound = tagList.getCompoundTagAt(i);
			LightVariant lightVariant = LightVariant.getLightVariant(lightCompound.getInteger("light"));
			byte color = Byte.valueOf(lightCompound.getByte("color"));
			pattern.add(new PatternLightData(lightVariant, color));
		}
		twinkle = compound.getBoolean("twinkle");
		tight = compound.getBoolean("tight");
	}
}
