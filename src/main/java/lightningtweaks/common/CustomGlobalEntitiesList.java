package lightningtweaks.common;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;

import org.apache.commons.lang3.mutable.MutableDouble;

import lightningtweaks.LightningTweaks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.passive.horse.SkeletonHorseEntity;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

/**
 * Modified version of an {@link ArrayList} of type {@link Entity}. The only
 * behavior that is changed is {@link #add(Entity)} to catch the given
 * {@link Entity} and apply this mod's logic to it.
 */
public class CustomGlobalEntitiesList extends ArrayList<Entity> {
	public static final SplittableRandom RANDOM = new SplittableRandom();

	public static final Field CASTER = ObfuscationReflectionHelper.findField(LightningBoltEntity.class,
			"field_204810_e"),
			EFFECT_ONLY = ObfuscationReflectionHelper.findField(LightningBoltEntity.class, "field_184529_d");
	public static final Method IGNITE_FIRE = ObfuscationReflectionHelper.findMethod(LightningBoltEntity.class,
			"func_195053_a", int.class);

	private final World world;

	/**
	 * Constructs an instance of {@link CustomGlobalEntitiesList} on an instance of
	 * {@link World}. The given {@link World} should be the same instance of
	 * {@link World} that this {@link CustomGlobalEntitiesList} is applied to.
	 * <hr>
	 * <b>Basically, the following statements should be true:</b>
	 * <ul>
	 * <li><code>world instanceof {@link ServerWorld}</code></li>
	 * <li><code>(({@link ServerWorld}) world).{@link ServerWorld#globalEntities globalEntities} == {@link CustomGlobalEntitiesList this}</code></li>
	 * </ul>
	 *
	 * @param world the {@link World} that this {@link CustomGlobalEntitiesList} is
	 *              applied to
	 */
	public CustomGlobalEntitiesList(World world) {
		this.world = world;
	}

	@Override
	public boolean add(Entity entity) {
		try {
			if (entity.getType() == EntityType.LIGHTNING_BOLT) {
				removeFire(entity);

				if (Config.REALISTIC_LIGHTNING.get() && CASTER.get(entity) == null && world
						.getLoadedEntitiesWithinAABB(SkeletonHorseEntity.class, entity.getBoundingBox().grow(0, 1, 0))
						.isEmpty())
					moveLightning(entity);

				if (Config.SPAWN_FIRE.get()) {
					Difficulty difficulty = world.getDifficulty();
					if (difficulty == Difficulty.NORMAL || difficulty == Difficulty.HARD)
						addFire(entity);
				} else
					disableFire(entity);
			}
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return super.add(entity);
	}

	private void addFire(Entity entity) throws IllegalAccessException, InvocationTargetException {
		int extraIgnitions = Config.EXTRA_IGNITIONS.get();
		LightningTweaks.log("Invoking " + IGNITE_FIRE + " with " + extraIgnitions + " at " + entity.getPosition(),
				world);
		IGNITE_FIRE.invoke(entity, extraIgnitions);
	}

	private void disableFire(Entity entity) throws IllegalAccessException {
		LightningTweaks.log("Enabling " + EFFECT_ONLY + " at " + entity.getPosition(), world);
		EFFECT_ONLY.set(entity, true);
	}

	private double getLeastResistance(List<BlockPos> poses, double least, BlockPos pos) {
		double resistance = getResistance(pos);
		if (resistance < least) {
			least = resistance;
			poses.clear();
		}
		if (resistance == least)
			poses.add(pos);
		return least;
	}

	private BlockPos getPathOfLeastResistance(Entity entity) {
		Chunk chunk = world.getChunkAt(entity.getPosition());
		Heightmap heightmap = chunk.getHeightmap(Type.MOTION_BLOCKING);
		ChunkPos chunkPos = chunk.getPos();

		List<BlockPos> poses = new ArrayList<>();
		double least = Double.MAX_VALUE;
		for (int x = 0; x < 16; x++)
			for (int z = 0; z < 16; z++)
				least = getLeastResistance(poses, least, chunkPos.getBlock(x, heightmap.getHeight(x, z) - 1, z));
		for (ClassInheritanceMultiMap<Entity> entities : chunk.getEntityLists())
			for (Entity entity1 : entities)
				if (entity1.onGround)
					least = getLeastResistance(poses, least, new BlockPos(entity1.getEyePosition(1)));

		return poses.get(RANDOM.nextInt(poses.size())).up();
	}

	private double getResistance(BlockPos pos) {
		// At max build height pos.getY() would return two below
		// world.getActualHeight().
		double resistance = Resistivity.BLOCKS.get(Blocks.AIR) * (world.getActualHeight() - pos.getY() + 2);
		int seaLevel = world.getSeaLevel();
		while (pos.getY() > seaLevel) {
			BlockState state = world.getBlockState(pos);
			resistance += Resistivity.BLOCKS.get(state.getBlock()) * getVolume(state, pos);
			pos = pos.down();
		}
		return resistance;
	}

	private double getVolume(BlockState state, BlockPos pos) {
		MutableDouble volume = new MutableDouble();
		state.getRenderShape(world, pos)
				.forEachBox((x1, y1, z1, x2, y2, z2) -> volume.add((x2 - x1) * (y2 - y1) * (z2 - z1)));
		return volume.doubleValue();
	}

	private void moveLightning(Entity entity) {
		BlockPos pos = getPathOfLeastResistance(entity);
		LightningTweaks.log("Moving lightning from " + entity.getPosition() + " to " + pos, world);
		entity.setPosition(pos.getX(), pos.getY(), pos.getZ());
	}

	private void removeFire(Entity entity) {
		BlockPos origin = entity.getPosition();
		for (int x = -1; x <= 1; x++)
			for (int y = -1; y <= 1; y++)
				for (int z = -1; z <= 1; z++) {
					BlockPos pos = origin.add(x, y, z);
					if (world.getBlockState(pos).getBlock() == Blocks.FIRE)
						world.removeBlock(pos, false);
				}
	}
}
