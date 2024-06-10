package thebetweenlands.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.RangeArgument;
import thebetweenlands.common.TheBetweenlands;

public class ShaderDebug {
	
	public ShaderDebug(CommandDispatcher<CommandSourceStack> dispatch) {
		dispatch.register(Commands.literal("shaderdebug").then(Commands.argument("index", IntegerArgumentType.integer(0, 2)).then(Commands.argument("value", FloatArgumentType.floatArg()).
				executes((command) -> {
					
					int index = IntegerArgumentType.getInteger(command, "index");
					float value = FloatArgumentType.getFloat(command, "value");
					
					TheBetweenlands.distmul[index] = value;
					
					return 0;
			}))));
	}
}
