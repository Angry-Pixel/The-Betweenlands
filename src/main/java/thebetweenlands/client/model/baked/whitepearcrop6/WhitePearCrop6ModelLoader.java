package thebetweenlands.client.model.baked.whitepearcrop6;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

public class WhitePearCrop6ModelLoader implements IGeometryLoader<UnbakedWhitePearCrop6Model> {
	public static final WhitePearCrop6ModelLoader INSTANCE = new WhitePearCrop6ModelLoader();

	@Override
	public UnbakedWhitePearCrop6Model read(JsonObject object, JsonDeserializationContext context) throws JsonParseException {
		return new UnbakedWhitePearCrop6Model();
	}
}

