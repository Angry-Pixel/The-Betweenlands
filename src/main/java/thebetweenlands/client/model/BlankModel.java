package thebetweenlands.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

import java.util.List;
import java.util.Map;

public class BlankModel<T extends Entity> extends MowzieModelBase<T>{
	public BlankModel() {
		super(new ModelPart(List.of(), Map.of()));
	}
}
