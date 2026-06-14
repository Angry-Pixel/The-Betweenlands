package thebetweenlands.client.model.baked.funguscrop4decayed;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

public class FungusCrop4DecayedModelLoader implements IGeometryLoader<UnbakedFungusCrop4DecayedModel> {
	public static final FungusCrop4DecayedModelLoader INSTANCE = new FungusCrop4DecayedModelLoader();

	@Override
	public UnbakedFungusCrop4DecayedModel read(JsonObject object, JsonDeserializationContext context) throws JsonParseException {
		return new UnbakedFungusCrop4DecayedModel();
	}
}

