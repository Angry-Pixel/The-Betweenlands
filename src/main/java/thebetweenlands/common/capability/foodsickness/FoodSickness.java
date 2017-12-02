package thebetweenlands.common.capability.foodsickness;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public enum FoodSickness {
	FINE(10 * 5),
	HALF(22 * 5),
	SICK(36 * 5);

	public final List<String> lines = new ArrayList<String>();
	public final int maxHatred;

	private FoodSickness(int maxHatred) {
		this.maxHatred = maxHatred;

		if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			this.updateLines();
		}
	}

	@SideOnly(Side.CLIENT)
	public void updateLines() {
		this.lines.clear();
		int index = 0;
		while (I18n.hasKey("chat.foodSickness." + name().toLowerCase() + "." + index)) {
			this.lines.add(I18n.format("chat.foodSickness." + name().toLowerCase() + "." + index));
			index++;
		}
	}

	@SideOnly(Side.CLIENT)
	public List<String> getLines() {
		return this.lines;
	}

	@SideOnly(Side.CLIENT)
	public String getRandomLine(Random rnd) {
		List<String> lines = this.getLines();
		if(lines.isEmpty()) {
			return "chat.foodSickness.nolines";
		}
		return lines.get(rnd.nextInt(lines.size()));
	}

	public static FoodSickness getSicknessForHatred(int hatred) {
		for (FoodSickness sickness : VALUES) {
			if (sickness.maxHatred > hatred) {
				return sickness;
			}
		}
		return VALUES[VALUES.length - 1];
	}

	public static final FoodSickness[] VALUES = values();

	public static class ResourceReloadListener implements IResourceManagerReloadListener {
		@Override
		public void onResourceManagerReload(IResourceManager resourceManager) {
			for(FoodSickness sickness : VALUES) {
				sickness.updateLines();
			}
		}
	}
}
