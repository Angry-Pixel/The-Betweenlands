package thebetweenlands.common.component.entity;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import thebetweenlands.common.handler.FoodSicknessHandler;
import thebetweenlands.util.FoodSickness;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class FoodSicknessData {

	private final Object2IntOpenHashMap<Item> hatredMap;
	private FoodSickness lastSickness;

	public static final Codec<FoodSicknessData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.unboundedMap(BuiltInRegistries.ITEM.byNameCodec(), Codec.INT).xmap(Object2IntOpenHashMap::new, Function.identity()).fieldOf("hatred").forGetter(o -> o.hatredMap),
		FoodSickness.CODEC.fieldOf("last_sickness").forGetter(o -> o.lastSickness)
	).apply(instance, FoodSicknessData::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, FoodSicknessData> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.map(Object2IntOpenHashMap::new, ByteBufCodecs.registry(Registries.ITEM), ByteBufCodecs.VAR_INT), o -> o.hatredMap,
		FoodSickness.STREAM_CODEC, o -> o.lastSickness,
		FoodSicknessData::new
	);

	public FoodSicknessData() {
		this(new Object2IntOpenHashMap<>(), FoodSickness.FINE);
	}

	private FoodSicknessData(Object2IntOpenHashMap<Item> hatredMap, FoodSickness lastSickness) {
		this.hatredMap = hatredMap;
		this.lastSickness = lastSickness;
	}

	public FoodSickness getLastSickness() {
		return this.lastSickness;
	}

	public void setLastSickness(FoodSickness sickness) {
		this.lastSickness = sickness;
	}

	public FoodSickness getSickness(Item food) {
		return FoodSickness.getSicknessForHatred(this.getFoodHatred(food));
	}

	public void decreaseHatredForAllExcept(Item food, int decrease) {
		if(decrease > 0) {
			Map<Item, Integer> newHatredMap = Maps.newHashMap();
			for (Item key : this.hatredMap.keySet()) {
				if (key != food) {
					newHatredMap.put(key, Math.max(this.hatredMap.getInt(key) - decrease, 0));
				}
			}
			if(!newHatredMap.isEmpty()) {
				this.hatredMap.putAll(newHatredMap);
			}
		}
	}

	public void increaseFoodHatred(Player player, Item food, int amount, int decreaseForOthers) {
		if (!FoodSicknessHandler.isFoodSicknessEnabled(player.level()))
			return;
		int finalMaxHatred = FoodSickness.values()[Math.max(FoodSickness.values().length - 1, 0)].maxHatred;
		if (this.hatredMap.containsKey(food)) {
			this.hatredMap.computeIfPresent(food, (k, currentAmount) -> Math.max(Math.min(currentAmount + amount, finalMaxHatred), 0));
		} else {
			this.hatredMap.put(food, Math.max(Math.min(amount, finalMaxHatred), 0));
		}
		this.decreaseHatredForAllExcept(food, decreaseForOthers);
	}

	public int getFoodHatred(Item food) {
		if (this.hatredMap.containsKey(food)) {
			return this.hatredMap.getInt(food);
		}
		return 0;
	}
}
