package net.minecraft.commands;

import com.mojang.brigadier.exceptions.BuiltInExceptionProvider;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.network.chat.TranslatableComponent;

public class BrigadierExceptions implements BuiltInExceptionProvider {
   private static final Dynamic2CommandExceptionType DOUBLE_TOO_SMALL = new Dynamic2CommandExceptionType((p_77203_, p_77204_) -> {
      return new TranslatableComponent("argument.double.low", p_77204_, p_77203_);
   });
   private static final Dynamic2CommandExceptionType DOUBLE_TOO_BIG = new Dynamic2CommandExceptionType((p_77198_, p_77199_) -> {
      return new TranslatableComponent("argument.double.big", p_77199_, p_77198_);
   });
   private static final Dynamic2CommandExceptionType FLOAT_TOO_SMALL = new Dynamic2CommandExceptionType((p_77191_, p_77192_) -> {
      return new TranslatableComponent("argument.float.low", p_77192_, p_77191_);
   });
   private static final Dynamic2CommandExceptionType FLOAT_TOO_BIG = new Dynamic2CommandExceptionType((p_77186_, p_77187_) -> {
      return new TranslatableComponent("argument.float.big", p_77187_, p_77186_);
   });
   private static final Dynamic2CommandExceptionType INTEGER_TOO_SMALL = new Dynamic2CommandExceptionType((p_77175_, p_77176_) -> {
      return new TranslatableComponent("argument.integer.low", p_77176_, p_77175_);
   });
   private static final Dynamic2CommandExceptionType INTEGER_TOO_BIG = new Dynamic2CommandExceptionType((p_77170_, p_77171_) -> {
      return new TranslatableComponent("argument.integer.big", p_77171_, p_77170_);
   });
   private static final Dynamic2CommandExceptionType LONG_TOO_SMALL = new Dynamic2CommandExceptionType((p_77165_, p_77166_) -> {
      return new TranslatableComponent("argument.long.low", p_77166_, p_77165_);
   });
   private static final Dynamic2CommandExceptionType LONG_TOO_BIG = new Dynamic2CommandExceptionType((p_77160_, p_77161_) -> {
      return new TranslatableComponent("argument.long.big", p_77161_, p_77160_);
   });
   private static final DynamicCommandExceptionType LITERAL_INCORRECT = new DynamicCommandExceptionType((p_77206_) -> {
      return new TranslatableComponent("argument.literal.incorrect", p_77206_);
   });
   private static final SimpleCommandExceptionType READER_EXPECTED_START_OF_QUOTE = new SimpleCommandExceptionType(new TranslatableComponent("parsing.quote.expected.start"));
   private static final SimpleCommandExceptionType READER_EXPECTED_END_OF_QUOTE = new SimpleCommandExceptionType(new TranslatableComponent("parsing.quote.expected.end"));
   private static final DynamicCommandExceptionType READER_INVALID_ESCAPE = new DynamicCommandExceptionType((p_77201_) -> {
      return new TranslatableComponent("parsing.quote.escape", p_77201_);
   });
   private static final DynamicCommandExceptionType READER_INVALID_BOOL = new DynamicCommandExceptionType((p_77196_) -> {
      return new TranslatableComponent("parsing.bool.invalid", p_77196_);
   });
   private static final DynamicCommandExceptionType READER_INVALID_INT = new DynamicCommandExceptionType((p_77189_) -> {
      return new TranslatableComponent("parsing.int.invalid", p_77189_);
   });
   private static final SimpleCommandExceptionType READER_EXPECTED_INT = new SimpleCommandExceptionType(new TranslatableComponent("parsing.int.expected"));
   private static final DynamicCommandExceptionType READER_INVALID_LONG = new DynamicCommandExceptionType((p_77184_) -> {
      return new TranslatableComponent("parsing.long.invalid", p_77184_);
   });
   private static final SimpleCommandExceptionType READER_EXPECTED_LONG = new SimpleCommandExceptionType(new TranslatableComponent("parsing.long.expected"));
   private static final DynamicCommandExceptionType READER_INVALID_DOUBLE = new DynamicCommandExceptionType((p_77173_) -> {
      return new TranslatableComponent("parsing.double.invalid", p_77173_);
   });
   private static final SimpleCommandExceptionType READER_EXPECTED_DOUBLE = new SimpleCommandExceptionType(new TranslatableComponent("parsing.double.expected"));
   private static final DynamicCommandExceptionType READER_INVALID_FLOAT = new DynamicCommandExceptionType((p_77168_) -> {
      return new TranslatableComponent("parsing.float.invalid", p_77168_);
   });
   private static final SimpleCommandExceptionType READER_EXPECTED_FLOAT = new SimpleCommandExceptionType(new TranslatableComponent("parsing.float.expected"));
   private static final SimpleCommandExceptionType READER_EXPECTED_BOOL = new SimpleCommandExceptionType(new TranslatableComponent("parsing.bool.expected"));
   private static final DynamicCommandExceptionType READER_EXPECTED_SYMBOL = new DynamicCommandExceptionType((p_77163_) -> {
      return new TranslatableComponent("parsing.expected", p_77163_);
   });
   private static final SimpleCommandExceptionType DISPATCHER_UNKNOWN_COMMAND = new SimpleCommandExceptionType(new TranslatableComponent("command.unknown.command"));
   private static final SimpleCommandExceptionType DISPATCHER_UNKNOWN_ARGUMENT = new SimpleCommandExceptionType(new TranslatableComponent("command.unknown.argument"));
   private static final SimpleCommandExceptionType DISPATCHER_EXPECTED_ARGUMENT_SEPARATOR = new SimpleCommandExceptionType(new TranslatableComponent("command.expected.separator"));
   private static final DynamicCommandExceptionType DISPATCHER_PARSE_EXCEPTION = new DynamicCommandExceptionType((p_77158_) -> {
      return new TranslatableComponent("command.exception", p_77158_);
   });

   public Dynamic2CommandExceptionType doubleTooLow() {
      return DOUBLE_TOO_SMALL;
   }

   public Dynamic2CommandExceptionType doubleTooHigh() {
      return DOUBLE_TOO_BIG;
   }

   public Dynamic2CommandExceptionType floatTooLow() {
      return FLOAT_TOO_SMALL;
   }

   public Dynamic2CommandExceptionType floatTooHigh() {
      return FLOAT_TOO_BIG;
   }

   public Dynamic2CommandExceptionType integerTooLow() {
      return INTEGER_TOO_SMALL;
   }

   public Dynamic2CommandExceptionType integerTooHigh() {
      return INTEGER_TOO_BIG;
   }

   public Dynamic2CommandExceptionType longTooLow() {
      return LONG_TOO_SMALL;
   }

   public Dynamic2CommandExceptionType longTooHigh() {
      return LONG_TOO_BIG;
   }

   public DynamicCommandExceptionType literalIncorrect() {
      return LITERAL_INCORRECT;
   }

   public SimpleCommandExceptionType readerExpectedStartOfQuote() {
      return READER_EXPECTED_START_OF_QUOTE;
   }

   public SimpleCommandExceptionType readerExpectedEndOfQuote() {
      return READER_EXPECTED_END_OF_QUOTE;
   }

   public DynamicCommandExceptionType readerInvalidEscape() {
      return READER_INVALID_ESCAPE;
   }

   public DynamicCommandExceptionType readerInvalidBool() {
      return READER_INVALID_BOOL;
   }

   public DynamicCommandExceptionType readerInvalidInt() {
      return READER_INVALID_INT;
   }

   public SimpleCommandExceptionType readerExpectedInt() {
      return READER_EXPECTED_INT;
   }

   public DynamicCommandExceptionType readerInvalidLong() {
      return READER_INVALID_LONG;
   }

   public SimpleCommandExceptionType readerExpectedLong() {
      return READER_EXPECTED_LONG;
   }

   public DynamicCommandExceptionType readerInvalidDouble() {
      return READER_INVALID_DOUBLE;
   }

   public SimpleCommandExceptionType readerExpectedDouble() {
      return READER_EXPECTED_DOUBLE;
   }

   public DynamicCommandExceptionType readerInvalidFloat() {
      return READER_INVALID_FLOAT;
   }

   public SimpleCommandExceptionType readerExpectedFloat() {
      return READER_EXPECTED_FLOAT;
   }

   public SimpleCommandExceptionType readerExpectedBool() {
      return READER_EXPECTED_BOOL;
   }

   public DynamicCommandExceptionType readerExpectedSymbol() {
      return READER_EXPECTED_SYMBOL;
   }

   public SimpleCommandExceptionType dispatcherUnknownCommand() {
      return DISPATCHER_UNKNOWN_COMMAND;
   }

   public SimpleCommandExceptionType dispatcherUnknownArgument() {
      return DISPATCHER_UNKNOWN_ARGUMENT;
   }

   public SimpleCommandExceptionType dispatcherExpectedArgumentSeparator() {
      return DISPATCHER_EXPECTED_ARGUMENT_SEPARATOR;
   }

   public DynamicCommandExceptionType dispatcherParseException() {
      return DISPATCHER_PARSE_EXCEPTION;
   }
}