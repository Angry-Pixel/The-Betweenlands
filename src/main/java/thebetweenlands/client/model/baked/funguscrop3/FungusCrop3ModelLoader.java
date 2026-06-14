package thebetweenlands.client.model.baked.funguscrop3;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

public class FungusCrop3ModelLoader implements IGeometryLoader<UnbakedFungusCrop3Model> {
	public static final FungusCrop3ModelLoader INSTANCE = new FungusCrop3ModelLoader();

	@Override
	public UnbakedFungusCrop3Model read(JsonObject object, JsonDeserializationContext context) throws JsonParseException {
		return new UnbakedFungusCrop3Model();
	}
}

