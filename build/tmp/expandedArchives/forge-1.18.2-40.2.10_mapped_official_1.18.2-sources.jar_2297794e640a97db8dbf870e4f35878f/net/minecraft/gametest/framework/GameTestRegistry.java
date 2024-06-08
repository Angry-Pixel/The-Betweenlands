package net.minecraft.gametest.framework;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Rotation;

public class GameTestRegistry {
   private static final Collection<TestFunction> TEST_FUNCTIONS = Lists.newArrayList();
   private static final Set<String> TEST_CLASS_NAMES = Sets.newHashSet();
   private static final Map<String, Consumer<ServerLevel>> BEFORE_BATCH_FUNCTIONS = Maps.newHashMap();
   private static final Map<String, Consumer<ServerLevel>> AFTER_BATCH_FUNCTIONS = Maps.newHashMap();
   private static final Collection<TestFunction> LAST_FAILED_TESTS = Sets.newHashSet();

   /** @deprecated Forge: Use {@link net.minecraftforge.event.RegisterGameTestsEvent RegisterGameTestsEvent} to register game tests */
   @Deprecated
   public static void register(Class<?> p_177502_) {
      Arrays.stream(p_177502_.getDeclaredMethods()).forEach(GameTestRegistry::register);
   }

   /** @deprecated Forge: Use {@link net.minecraftforge.event.RegisterGameTestsEvent RegisterGameTestsEvent} to register game tests */
   @Deprecated
   public static void register(Method p_177504_) {
       register(p_177504_, java.util.Set.of());
   }
   /** @deprecated Forge: Use {@link net.minecraftforge.event.RegisterGameTestsEvent RegisterGameTestsEvent} to register game tests */
   @Deprecated
   public static void register(Method p_177504_, java.util.Set<String> allowedNamespaces) {
      String s = p_177504_.getDeclaringClass().getSimpleName();
      GameTest gametest = p_177504_.getAnnotation(GameTest.class);
      if (gametest != null && (allowedNamespaces.isEmpty() || allowedNamespaces.contains(net.minecraftforge.gametest.ForgeGameTestHooks.getTemplateNamespace(p_177504_)))) {
         TEST_FUNCTIONS.add(turnMethodIntoTestFunction(p_177504_));
         TEST_CLASS_NAMES.add(s);
      }

      GameTestGenerator gametestgenerator = p_177504_.getAnnotation(GameTestGenerator.class);
      if (gametestgenerator != null) {
         Collection<TestFunction> testFunctions = new java.util.ArrayList<>(useTestGeneratorMethod(p_177504_));
         if (!allowedNamespaces.isEmpty())
             testFunctions.removeIf(t -> !allowedNamespaces.contains(new net.minecraft.resources.ResourceLocation(t.getStructureName()).getNamespace()));
         TEST_FUNCTIONS.addAll(testFunctions);
         TEST_CLASS_NAMES.add(s);
      }

      registerBatchFunction(p_177504_, BeforeBatch.class, BeforeBatch::batch, BEFORE_BATCH_FUNCTIONS);
      registerBatchFunction(p_177504_, AfterBatch.class, AfterBatch::batch, AFTER_BATCH_FUNCTIONS);
   }

   private static <T extends Annotation> void registerBatchFunction(Method p_177506_, Class<T> p_177507_, Function<T, String> p_177508_, Map<String, Consumer<ServerLevel>> p_177509_) {
      T t = p_177506_.getAnnotation(p_177507_);
      if (t != null) {
         String s = p_177508_.apply(t);
         Consumer<ServerLevel> consumer = p_177509_.putIfAbsent(s, (Consumer<ServerLevel>)turnMethodIntoConsumer(p_177506_));
         if (consumer != null) {
            throw new RuntimeException("Hey, there should only be one " + p_177507_ + " method per batch. Batch '" + s + "' has more than one!");
         }
      }

   }

   public static Collection<TestFunction> getTestFunctionsForClassName(String p_127660_) {
      return TEST_FUNCTIONS.stream().filter((p_127674_) -> {
         return isTestFunctionPartOfClass(p_127674_, p_127660_);
      }).collect(Collectors.toList());
   }

   public static Collection<TestFunction> getAllTestFunctions() {
      return TEST_FUNCTIONS;
   }

   public static Collection<String> getAllTestClassNames() {
      return TEST_CLASS_NAMES;
   }

   public static boolean isTestClass(String p_127671_) {
      return TEST_CLASS_NAMES.contains(p_127671_);
   }

   @Nullable
   public static Consumer<ServerLevel> getBeforeBatchFunction(String p_127677_) {
      return BEFORE_BATCH_FUNCTIONS.get(p_127677_);
   }

   @Nullable
   public static Consumer<ServerLevel> getAfterBatchFunction(String p_177518_) {
      return AFTER_BATCH_FUNCTIONS.get(p_177518_);
   }

   public static Optional<TestFunction> findTestFunction(String p_127680_) {
      return getAllTestFunctions().stream().filter((p_127663_) -> {
         return p_127663_.getTestName().equalsIgnoreCase(p_127680_);
      }).findFirst();
   }

   public static TestFunction getTestFunction(String p_127682_) {
      Optional<TestFunction> optional = findTestFunction(p_127682_);
      if (!optional.isPresent()) {
         throw new IllegalArgumentException("Can't find the test function for " + p_127682_);
      } else {
         return optional.get();
      }
   }

   private static Collection<TestFunction> useTestGeneratorMethod(Method p_177514_) {
      try {
         Object object = null;
         if (!java.lang.reflect.Modifier.isStatic(p_177514_.getModifiers()))
             object = p_177514_.getDeclaringClass().newInstance();
         return (Collection)p_177514_.invoke(object);
      } catch (ReflectiveOperationException reflectiveoperationexception) {
         throw new RuntimeException(reflectiveoperationexception);
      }
   }

   private static TestFunction turnMethodIntoTestFunction(Method p_177516_) {
      GameTest gametest = p_177516_.getAnnotation(GameTest.class);
      String s = p_177516_.getDeclaringClass().getSimpleName();
      String s1 = s.toLowerCase();
      boolean prefixGameTestTemplate = net.minecraftforge.gametest.ForgeGameTestHooks.prefixGameTestTemplate(p_177516_);
      String s2 = (prefixGameTestTemplate ? s1 + "." : "") + p_177516_.getName().toLowerCase();
      String s3 = net.minecraftforge.gametest.ForgeGameTestHooks.getTemplateNamespace(p_177516_) + ":" + (gametest.template().isEmpty() ? s2 : (prefixGameTestTemplate ? s1 + "." : "") + gametest.template());
      String s4 = gametest.batch();
      Rotation rotation = StructureUtils.getRotationForRotationSteps(gametest.rotationSteps());
      return new TestFunction(s4, s2, s3, rotation, gametest.timeoutTicks(), gametest.setupTicks(), gametest.required(), gametest.requiredSuccesses(), gametest.attempts(), (Consumer<net.minecraft.gametest.framework.GameTestHelper>)turnMethodIntoConsumer(p_177516_));
   }

   private static Consumer<?> turnMethodIntoConsumer(Method p_177520_) {
      return (p_177512_) -> {
         try {
            Object object = null;
            if (!java.lang.reflect.Modifier.isStatic(p_177520_.getModifiers()))
                object = p_177520_.getDeclaringClass().newInstance();
            p_177520_.invoke(object, p_177512_);
         } catch (InvocationTargetException invocationtargetexception) {
            if (invocationtargetexception.getCause() instanceof RuntimeException) {
               throw (RuntimeException)invocationtargetexception.getCause();
            } else {
               throw new RuntimeException(invocationtargetexception.getCause());
            }
         } catch (ReflectiveOperationException reflectiveoperationexception) {
            throw new RuntimeException(reflectiveoperationexception);
         }
      };
   }

   private static boolean isTestFunctionPartOfClass(TestFunction p_127667_, String p_127668_) {
      return p_127667_.getTestName().toLowerCase().startsWith(p_127668_.toLowerCase() + ".");
   }

   public static Collection<TestFunction> getLastFailedTests() {
      return LAST_FAILED_TESTS;
   }

   public static void rememberFailedTest(TestFunction p_127665_) {
      LAST_FAILED_TESTS.add(p_127665_);
   }

   public static void forgetFailedTests() {
      LAST_FAILED_TESTS.clear();
   }
}
