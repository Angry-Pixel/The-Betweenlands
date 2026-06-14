package thebetweenlands.client.model.baked.funguscrop4;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

public class FungusCrop4ModelLoader implements IGeometryLoader<UnbakedFungusCrop4Model> {
	public static final FungusCrop4ModelLoader INSTANCE = new FungusCrop4ModelLoader();

	@Override
	public UnbakedFungusCrop4Model read(JsonObject object, JsonDeserializationContext context) throws JsonParseException {
		return new UnbakedFungusCrop4Model();
	}
}

