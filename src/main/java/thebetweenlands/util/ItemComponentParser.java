package thebetweenlands.util;

import java.util.Set;

import javax.annotation.Nullable;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import it.unimi.dsi.fastutil.objects.ReferenceArraySet;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ItemComponentParser {

    static final DynamicCommandExceptionType ERROR_DUPLICATE_COMPONENT = new DynamicCommandExceptionType(
        componentType -> Component.translatableEscape("arguments.item.component.repeated", componentType)
    );
    
    static final Dynamic2CommandExceptionType ERROR_MALFORMED_COMPONENT = new Dynamic2CommandExceptionType(
        (p_336012_, p_335885_) -> Component.translatableEscape("arguments.item.component.malformed", p_336012_, p_335885_)
    );
    
    static final DynamicCommandExceptionType ERROR_INVALID_COMPONENT = new DynamicCommandExceptionType(
        componentType -> Component.translatableEscape("arguments.item.component.unknown", componentType)
    );
    
    public static interface ComponentVisitor {
    	/**
    	 * Whether to simply ignore an invalid component type
    	 * @param componentType
    	 * @return true to ignore, false to throw an error
    	 */
        default boolean ignoreInvalidComponent(ResourceLocation componentType) {
        	return true;
        }
        
        <T> void visitComponent(DataComponentType<T> componentType, T value);
        <T> void visitRemovedComponent(DataComponentType<T> componentType);
    }
	
	// Note: brigadier StringReader, not java.io.StringReader
	private final StringReader reader;
	private final ComponentVisitor visitor;
	protected final DynamicOps<Tag> registryOps;

	public ItemComponentParser(final String string, final ComponentVisitor visitor, HolderLookup.Provider registries) {
		this(new StringReader(string), visitor, registries);
	}
	
	public ItemComponentParser(final StringReader reader, final ComponentVisitor visitor, HolderLookup.Provider registries) {
		this.reader = reader;
		this.visitor = visitor;
		this.registryOps = registries.createSerializationContext(NbtOps.INSTANCE);
	}

	/**
	 * Parses components and passes them to the {@link ComponentVisitor}
	 * @throws CommandSyntaxException
	 */
	public void visitComponents() throws CommandSyntaxException {
		final StringReader reader = this.reader;
		final ComponentVisitor visitor = this.visitor;
		final DynamicOps<Tag> registryOps = this.registryOps;
		
		reader.expect('[');
		// Set to track which data components have already been used
        Set<DataComponentType<?>> usedComponents = new ReferenceArraySet<>();

        while (reader.canRead() && reader.peek() != ']') {
            reader.skipWhitespace();
            
            if(!reader.canRead()) continue;
            
            // Visit a component
            visitSingleComponent(reader, visitor, registryOps, usedComponents);
            
            reader.skipWhitespace();
            
            // Check whether there's going to be a next component
            if (!reader.canRead() || reader.peek() != ',') {
                break;
            }
            
            // Skip the comma
            reader.skip();
        }
        
		reader.expect(']');
	}
	
	public void visitSingleComponent() throws CommandSyntaxException {
		visitSingleComponent(this.reader, this.visitor, this.registryOps, null);
	}
	
	// Here because generics
	protected static <T> void visitComponentContents(final StringReader reader, final ComponentVisitor visitor, final DynamicOps<Tag> registryOps, DataComponentType<T> componentType, Tag tag) throws CommandSyntaxException {
        reader.skipWhitespace();
        // isTransient(): does not have a codec
        if(componentType.isTransient()) return;
        
        DataResult<T> dataresult = componentType.codecOrThrow().parse(registryOps, tag);
        T componentData = dataresult.getOrThrow(cause -> {
            return ERROR_MALFORMED_COMPONENT.createWithContext(reader, componentType.toString(), cause);
        });
        visitor.visitComponent(componentType, componentData);
	}
	
	public static void visitSingleComponent(final StringReader reader, final ComponentVisitor visitor, final DynamicOps<Tag> registryOps, @Nullable final Set<DataComponentType<?>> usedComponents) throws CommandSyntaxException {
        // Is this a component exclusion?
        if (reader.peek() == '!') {
        	// Skip the ! before the name
        	reader.skip();
        	// Read the data component type
        	final DataComponentType<?> componentType = parseComponentOrNull(reader, visitor);
        	// Send removed component to visitor (assuming it isn't invalid)
        	if(componentType != null) {
            	// Don't allow duplicate component types (e.g. [!minecraft:food, minecraft:food={nutrition:5,saturation:5}])
            	if(usedComponents != null && !usedComponents.add(componentType))
            		throw ERROR_DUPLICATE_COMPONENT.create(componentType);
            	
        		visitor.visitRemovedComponent(componentType);
        	}
        } else { // This is a valid component
        	// Read the data component type
        	final DataComponentType<?> componentType = parseComponentOrNull(reader, visitor);
        	
        	// Don't allow duplicate component types (e.g. [!minecraft:food, minecraft:food={nutrition:5,saturation:5}])
        	if(componentType != null && usedComponents != null && !usedComponents.add(componentType))
        		throw ERROR_DUPLICATE_COMPONENT.create(componentType);

        	// Skip to the = sign
            reader.skipWhitespace();
            reader.expect('=');
            
            reader.skipWhitespace();
            
            // Read component contents
            Tag tag = new TagParser(reader).readValue();
            
            // Feed component to the visitor
            // If componentType is null, we'd still have had to consume its contents
            if(componentType != null) {
            	visitComponentContents(reader, visitor, registryOps, componentType, tag);
            }
        }
	}
	
	@Nullable
	public static DataComponentType<?> parseComponentOrNull(final StringReader reader, @Nullable final ComponentVisitor visitor) throws CommandSyntaxException {
		if (reader.canRead()) {
			int i = reader.getCursor();
            ResourceLocation resourcelocation = ResourceLocation.read(reader);
            DataComponentType<?> datacomponenttype = BuiltInRegistries.DATA_COMPONENT_TYPE.get(resourcelocation);
            if(datacomponenttype != null || visitor == null || visitor.ignoreInvalidComponent(resourcelocation)) {
                return datacomponenttype;
            } else {
            	reader.setCursor(i);
            	throw ERROR_INVALID_COMPONENT.createWithContext(reader, resourcelocation);
            }
        } else {
            return null;
        }
	}
	
}
