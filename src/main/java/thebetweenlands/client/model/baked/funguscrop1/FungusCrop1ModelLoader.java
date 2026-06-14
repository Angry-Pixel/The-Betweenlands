package thebetweenlands.client.model.baked.funguscrop1;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

public class FungusCrop1ModelLoader implements IGeometryLoader<UnbakedFungusCrop1Model> {
	public static final FungusCrop1ModelLoader INSTANCE = new FungusCrop1ModelLoader();

	@Override
	public UnbakedFungusCrop1Model read(JsonObject object, JsonDeserializationContext context) throws JsonParseException {
		return new UnbakedFungusCrop1Model();
	}
}

