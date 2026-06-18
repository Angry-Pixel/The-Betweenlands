package thebetweenlands.client.model.baked.funguscrop;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

public class FungusCropModelLoader implements IGeometryLoader<UnbakedFungusCropModel> {
	public static final FungusCropModelLoader INSTANCE = new FungusCropModelLoader();

	@Override
	public UnbakedFungusCropModel read(JsonObject object, JsonDeserializationContext context) throws JsonParseException {
		return new UnbakedFungusCropModel();
	}
}

