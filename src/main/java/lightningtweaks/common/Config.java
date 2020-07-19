package lightningtweaks.common;

import lightningtweaks.LightningTweaks;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig.Type;

public class Config {
	public static class Value<T> {
		private final String path, comment;
		private final T defaultValue;
		private ConfigValue<T> value;

		private Value(String path, T defaultValue, String comment) {
			this.path = path;
			this.defaultValue = defaultValue;
			this.comment = comment;
		}

		private void build(Builder builder) {
			value = builder.comment(comment).define(path, defaultValue);
		}

		public T get() {
			return value.get();
		}

		public T set(T value) {
			this.value.set(value);
			return value;
		}
	}

	public static final Value<Integer> EXTRA_IGNITIONS = new Value<>("Extra Ignitions", 4,
			"The number of fires that should be spawned around the lightning strike. Zero will only cause the struck block to be ignited. The vanilla value is four.");
	public static final Value<Boolean> REALISTIC_LIGHTNING = new Value<>("Realistic Lightning", true,
			"Whether lightning follows the path of least resistance. This is the main behavior of this mod."),
			SPAWN_FIRE = new Value<>("Spawn Fire", true,
					"Whether lightning spawns fire on the ground and damages and sets fire to entities. This doesn't change the behavior of lightning that would not have created fire otherwise.");

	public static void register() {
		LightningTweaks.log("Registering configuration file.");
		register(ModLoadingContext.get(), Type.COMMON, EXTRA_IGNITIONS, REALISTIC_LIGHTNING, SPAWN_FIRE);
	}

	private static void register(ModLoadingContext context, Type type, Value<?>... values) {
		Builder builder = new Builder();
		for (Value<?> value : values)
			value.build(builder);
		context.registerConfig(type, builder.build());
	}
}