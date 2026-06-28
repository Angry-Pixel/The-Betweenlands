package thebetweenlands.client.model.baked.custom;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.math.Transformation;

import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.util.GsonHelper;
import net.neoforged.neoforge.client.model.IModelBuilder;
import net.neoforged.neoforge.client.model.IQuadTransformer;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.SimpleUnbakedGeometry;
import net.neoforged.neoforge.client.model.geometry.UnbakedGeometryHelper;

public class CustomElementsModel extends SimpleUnbakedGeometry<CustomElementsModel> {
    private final List<CustomBlockElement> elements;

    public CustomElementsModel(List<CustomBlockElement> elements) {
        this.elements = elements;
    }

	@Override
    protected void addQuads(IGeometryBakingContext context, IModelBuilder<?> modelBuilder, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState) {
        // If there is a root transform, undo the ModelState transform, apply it, then re-apply the ModelState transform.
        // This is necessary because of things like UV locking, which should only respond to the ModelState, and as such
        // that is the only transform that should be applied during face bake.
        var rootTransform = context.getRootTransform();
        if (!rootTransform.isIdentity()) {
            modelState = UnbakedGeometryHelper.composeRootTransformIntoModelState(modelState, rootTransform);
        }

        for (CustomBlockElement customElement : elements) {
        	// Get default (vanilla) model element
        	BlockElement element = customElement.baseBlockElement();
        	// Get custom rotation
        	CustomElementRotation customRotation = customElement.customRotation();
        	// If true, the current rotation is an identity rotation
        	boolean isIdentityRotation = customRotation.isIdentity();
        	// Rotation applier
        	IQuadTransformer transformer = isIdentityRotation ? null : getQuadTransformerForRotation(modelState, customRotation);
        	
            for (Direction direction : element.faces.keySet()) {
                var face = element.faces.get(direction);
                var sprite = spriteGetter.apply(context.getMaterial(face.texture()));
                var quad = BlockModel.bakeFace(element, face, sprite, direction, modelState);
                if (!isIdentityRotation) {
                	transformer.processInPlace(quad);
                }

                if (face.cullForDirection() == null)
                    modelBuilder.addUnculledFace(quad);
                else
                    modelBuilder.addCulledFace(modelState.getRotation().rotateTransform(face.cullForDirection()), quad);
            }
        }
	}
	
	public static IQuadTransformer getQuadTransformerForRotation(ModelState modelState, CustomElementRotation rotation) {
		Transformation transformation = rotation.getTransformation();
		
		IQuadTransformer transformer = UnbakedGeometryHelper.applyRootTransform(modelState, transformation);
		
		return transformer;
	}

	// Loader for this type of model
    public static final class Loader implements IGeometryLoader<CustomElementsModel> {
        public static final Loader INSTANCE = new Loader();

        private Loader() {}

        @Override
        public CustomElementsModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
            if (!jsonObject.has("elements"))
                throw new JsonParseException("An element model must have an \"elements\" member.");

            List<CustomBlockElement> elements = new ArrayList<>();
            for (JsonElement element : GsonHelper.getAsJsonArray(jsonObject, "elements")) {
            	// TODO register CustomBlockElement deserializer
                elements.add(deserializationContext.deserialize(element, CustomBlockElement.class));
            }

            return new CustomElementsModel(elements);
        }
    }
}
