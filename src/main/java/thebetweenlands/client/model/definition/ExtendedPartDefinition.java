package thebetweenlands.client.model.definition;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDefinition;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.PartDefinition;
import thebetweenlands.util.RotationOrder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//[VanillaCopy] of PartDefinition, with additional context for RotationOrder and cube type
public class ExtendedPartDefinition extends PartDefinition {

	private final Map<String, ExtendedPartDefinition> children = new HashMap<>();
	private final RotationOrder order;
	private final Type type;

	ExtendedPartDefinition(List<CubeDefinition> cubes, PartPose partPose, RotationOrder order, Type type) {
		super(cubes, partPose);
		this.order = order;
		this.type = type;
	}

	@Override
	public ExtendedPartDefinition addOrReplaceChild(String name, CubeListBuilder cubes, PartPose partPose) {
		return this.addOrReplaceChild(name, cubes, partPose, RotationOrder.ZYX);
	}

	public ExtendedPartDefinition addOrReplaceChild(String name, CubeListBuilder cubes, PartPose partPose, RotationOrder order) {
		return this.addOrReplaceChild(name, cubes, partPose, order, Type.VANILLA);
	}

	public ExtendedPartDefinition addOrReplaceChild(String name, CubeListBuilder cubes, PartPose partPose, Type type) {
		return this.addOrReplaceChild(name, cubes, partPose, RotationOrder.ZYX, type);
	}

	public ExtendedPartDefinition addOrReplaceChild(String name, CubeListBuilder cubes, PartPose partPose, RotationOrder order, Type type) {
		ExtendedPartDefinition partdefinition = new ExtendedPartDefinition(cubes.getCubes(), partPose, order, type);
		ExtendedPartDefinition partdefinition1 = this.children.put(name, partdefinition);
		if (partdefinition1 != null) {
			partdefinition.children.putAll(partdefinition1.children);
		}

		return partdefinition;
	}

	public ExtendedPartDefinition getChild(String name) {
		return this.children.get(name);
	}

	@Override
	public ExtendedModelPart bake(int texWidth, int texHeight) {
		Object2ObjectArrayMap<String, ModelPart> object2objectarraymap = this.children
			.entrySet()
			.stream()
			.collect(
				Collectors.toMap(
					Map.Entry::getKey,
					p_171593_ -> p_171593_.getValue().bake(texWidth, texHeight),
					(p_171595_, p_171596_) -> p_171595_,
					Object2ObjectArrayMap::new
				)
			);
		List<ModelPart.Cube> list = this.cubes.stream().map(p_171589_ -> p_171589_.bake(texWidth, texHeight)).collect(ImmutableList.toImmutableList());
		ExtendedModelPart modelpart = new ExtendedModelPart(list, object2objectarraymap, this.order, this.type);
		modelpart.setInitialPose(this.partPose);
		modelpart.loadPose(this.partPose);
		return modelpart;
	}

	public enum Type {
		VANILLA,
		ARM,
		THREE_DIMENSIONAL;
	}
}
