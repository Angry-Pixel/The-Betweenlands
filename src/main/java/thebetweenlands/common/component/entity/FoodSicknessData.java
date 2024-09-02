package thebetweenlands.common.component.entity;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.network.PacketDistributor;
import thebetweenlands.common.handler.FoodSicknessHandler;
import thebetweenlands.common.network.clientbound.attachment.UpdateFoodSicknessPacket;
import thebetweenlands.util.FoodSickness;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class FoodSicknessData {

	private final HashMap<Item, Integer> hatredMap;
	private FoodSickness lastSickness;

	public static final Codec<FoodSicknessData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.unboundedMap(BuiltInRegistries.ITEM.byNameCodec(), Codec.INT).xmap(HashMap::new, Function.identity()).fieldOf("hatred").forGetter(o -> o.hatredMap),
		FoodSickness.CODEC.fieldOf("last_sickness").forGetter(o -> o.lastSickness)
	).apply(instance, FoodSicknessData::new));

	public FoodSicknessData() {
		this(new HashMap<>(), FoodSickness.FINE);
	}

	public FoodSicknessData(HashMap<Item, Integer> hatredMap, FoodSickness lastSickness) {
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

	public void decreaseHatredForAllExcept(Player player, Item food, int decrease) {
		if(decrease > 0) {
			Map<Item, Integer> newHatredMap = Maps.newHashMap();
			for (Item key : this.hatredMap.keySet()) {
				if (key != food) {
					newHatredMap.put(key, Math.max(this.hatredMap.get(key) - decrease, 0));
				}
			}
			if(!newHatredMap.isEmpty()) {
				this.hatredMap.putAll(newHatredMap);
				this.setChanged(player);
			}
		}
	}

	public void increaseFoodHatred(Player player, Item food, int amount, int decreaseForOthers) {
		if (!FoodSicknessHandler.isFoodSicknessEnabled(player.level()))
			return;
		int finalMaxHatred = FoodSickness.values()[Math.max(FoodSickness.values().length - 1, 0)].maxHatred;
		if (this.hatredMap.containsKey(food)) {
			this.hatredMap.compute(food, (k, currentAmount) -> Math.max(Math.min(currentAmount + amount, finalMaxHatred), 0));
		} else {
			this.hatredMap.put(food, Math.max(Math.min(amount, finalMaxHatred), 0));
		}
		this.decreaseHatredForAllExcept(player, food, decreaseForOthers);
		this.setChanged(player);
	}

	public int getFoodHatred(Item food) {
		if (this.hatredMap.containsKey(food)) {
			return this.hatredMap.get(food);
		}
		return 0;
	}

	public void setChanged(Player player) {
		if (player instanceof ServerPlayer sp) {
			PacketDistributor.sendToPlayer(sp, new UpdateFoodSicknessPacket(this.hatredMap, this.lastSickness));
		}
	}
}
