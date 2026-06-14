package thebetweenlands.client.model.baked.whitepearcrop3;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

public class WhitePearCrop3ModelLoader implements IGeometryLoader<UnbakedWhitePearCrop3Model> {
	public static final WhitePearCrop3ModelLoader INSTANCE = new WhitePearCrop3ModelLoader();

	@Override
	public UnbakedWhitePearCrop3Model read(JsonObject object, JsonDeserializationContext context) throws JsonParseException {
		return new UnbakedWhitePearCrop3Model();
	}
}

