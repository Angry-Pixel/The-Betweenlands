package thebetweenlands.util;

import java.util.Set;

import javax.annotation.Nullable;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;

import it.unimi.dsi.fastutil.objects.ReferenceArraySet;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * Parses item components from a string, leaves component values as a tag
 */
public class ItemComponentTagParser {

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
        
        <T> void visitComponent(DataComponentType<T> componentType, Tag value);
        <T> void visitRemovedComponent(DataComponentType<T> componentType);
    }
	
	// Note: brigadier StringReader, not java.io.StringReader
	private final StringReader reader;
	private final ComponentVisitor visitor;

	public ItemComponentTagParser(final String string, final ComponentVisitor visitor) {
		this(new StringReader(string), visitor);
	}
	
	public ItemComponentTagParser(final StringReader reader, final ComponentVisitor visitor) {
		this.reader = reader;
		this.visitor = visitor;
	}

	/**
	 * Parses components and passes them to the {@link ComponentVisitor}
	 * @throws CommandSyntaxException
	 */
	public void visitComponents() throws CommandSyntaxException {
		final StringReader reader = this.reader;
		final ComponentVisitor visitor = this.visitor;
		
		reader.expect('[');
		// Set to track which data components have already been used
        Set<DataComponentType<?>> usedComponents = new ReferenceArraySet<>();

        while (reader.canRead() && reader.peek() != ']') {
            reader.skipWhitespace();
            
            if(!reader.canRead()) continue;
            
            // Visit a component
            visitSingleComponent(reader, visitor, usedComponents);
            
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
		visitSingleComponent(this.reader, this.visitor, null);
	}
	
	public static void visitSingleComponent(final StringReader reader, final ComponentVisitor visitor, @Nullable final Set<DataComponentType<?>> usedComponents) throws CommandSyntaxException {
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
                visitor.visitComponent(componentType, tag);
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
