package thebetweenlands.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.IntFunction;

public enum FoodSickness implements StringRepresentable {
	FINE(10 * 5),
	HALF(22 * 5),
	SICK(36 * 5);

	public static final EnumCodec<FoodSickness> CODEC = StringRepresentable.fromEnum(FoodSickness::values);
	public static final IntFunction<FoodSickness> BY_ID = ByIdMap.continuous(FoodSickness::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
	public static final StreamCodec<ByteBuf, FoodSickness> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, FoodSickness::ordinal);
	public final List<Component> lines = new ArrayList<>();
	public final int maxHatred;

	FoodSickness(int maxHatred) {
		this.maxHatred = maxHatred;

		if (FMLLoader.getDist() == Dist.CLIENT) {
			this.updateLines();
		}
	}

	public void updateLines() {
		this.lines.clear();
		int index = 0;
		while (I18n.exists("chat.foodSickness." + name().toLowerCase() + "." + index)) {
			this.lines.add(Component.translatable("chat.foodSickness." + name().toLowerCase() + "." + index));
			index++;
		}
	}

	public List<Component> getLines() {
		return this.lines;
	}

	public Component getRandomLine(RandomSource rnd) {
		List<Component> lines = this.getLines();
		if (lines.isEmpty()) {
			return Component.translatable("chat.foodSickness.nolines");
		}
		return lines.get(rnd.nextInt(lines.size()));
	}

	public static FoodSickness getSicknessForHatred(int hatred) {
		for (FoodSickness sickness : values()) {
			if (sickness.maxHatred > hatred) {
				return sickness;
			}
		}
		return values()[values().length - 1];
	}

	@Override
	public String getSerializedName() {
		return this.name().toLowerCase(Locale.ROOT);
	}

	public static class ResourceReloadListener implements ResourceManagerReloadListener {
		@Override
		public void onResourceManagerReload(ResourceManager manager) {
			for (FoodSickness sickness : FoodSickness.values()) {
				sickness.updateLines();
			}
		}
	}
}
