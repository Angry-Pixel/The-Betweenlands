package thebetweenlands.common.component.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import thebetweenlands.common.component.entity.circlegem.CircleGem;
import thebetweenlands.common.network.clientbound.UpdateGemsPacket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class CircleGemData {

	private final List<CircleGem> gems;

	public static final Codec<CircleGemData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		CircleGem.CODEC.listOf().fieldOf("gems").forGetter(o -> o.gems)
	).apply(instance, CircleGemData::new));

	public CircleGemData() {
		this(new ArrayList<>());
	}

	public CircleGemData(List<CircleGem> gems) {
		this.gems = new ArrayList<>(gems);
	}

	public boolean canAdd(CircleGem gem) {
		return true;
	}

	public void addGem(LivingEntity entity, CircleGem gem) {
		if(this.canAdd(gem)) {
			this.gems.add(gem);
			this.setChanged(entity);
		}
	}

	public boolean removeGem(LivingEntity entity, CircleGem gem) {
		Iterator<CircleGem> gemIT = this.gems.iterator();
		while(gemIT.hasNext()) {
			CircleGem currentGem = gemIT.next();
			if(currentGem.gemType() == gem.gemType() && currentGem.combatType() == gem.combatType()) {
				gemIT.remove();
				this.setChanged(entity);
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

	private void setChanged(LivingEntity entity) {
		PacketDistributor.sendToPlayersTrackingEntity(entity, new UpdateGemsPacket(this.gems));
	}
}
