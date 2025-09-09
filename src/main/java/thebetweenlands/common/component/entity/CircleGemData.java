package thebetweenlands.common.component.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import thebetweenlands.common.component.entity.circlegem.CircleGem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class CircleGemData {

	private final List<CircleGem> gems;

	public static final Codec<CircleGemData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		CircleGem.CODEC.listOf().fieldOf("gems").forGetter(o -> o.gems)
	).apply(instance, CircleGemData::new));

	public static final StreamCodec<FriendlyByteBuf, CircleGemData> STREAM_CODEC = StreamCodec.composite(
		CircleGem.STREAM_CODEC.apply(ByteBufCodecs.list()), o -> o.gems,
		CircleGemData::new
	);

	public CircleGemData() {
		this(new ArrayList<>());
	}

	private CircleGemData(List<CircleGem> gems) {
		this.gems = new ArrayList<>(gems);
	}

	public boolean canAdd(CircleGem gem) {
		return true;
	}

	public void addGem(CircleGem gem) {
		if (this.canAdd(gem)) {
			this.gems.add(gem);
		}
	}

	public boolean removeGem(CircleGem gem) {
		Iterator<CircleGem> gemIT = this.gems.iterator();
		while (gemIT.hasNext()) {
			CircleGem currentGem = gemIT.next();
			if (currentGem.gemType() == gem.gemType() && currentGem.combatType() == gem.combatType()) {
				gemIT.remove();
				return true;
			}
		}
		return false;
	}

	public List<CircleGem> getGems() {
		return Collections.unmodifiableList(this.gems);
	}

	public boolean removeAll() {
		boolean hadGems = !this.gems.isEmpty();
		this.gems.clear();
		return hadGems;
	}
}
