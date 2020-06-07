package lightningtweaks.common;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;

import lightningtweaks.LightningTweaks;
import lightningtweaks.common.ResistivityMap.Resistivity;
import lightningtweaks.common.event.EntityHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
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
public class GlobalEntitiesList extends ArrayList<Entity> {
	private static final SplittableRandom random = new SplittableRandom();

	/**
	 * TODO
	 *
	 * @param max   TODO
	 * @param poses TODO
	 * @param pos   TODO
	 * @return TODO
	 */
	private static double scoreBlockPos(double max, List<BlockPos> poses, BlockPos pos) {
		double score = pos.getY();
		if (score < max)
			return max;
		if (score > max)
			poses.clear();
		poses.add(pos);
		return score;
	}

	private final World world;

	/**
	 * Constructs an instance of {@link GlobalEntitiesList} on an instance of
	 * {@link World}. The given {@link World} should be the same instance of
	 * {@link World} that this {@link GlobalEntitiesList} is applied to.
	 * <hr>
	 * <b>Basically, the following statements should be true:</b>
	 * <ul>
	 * <li><code>world instanceof {@link ServerWorld}</code></li>
	 * <li><code>(({@link ServerWorld}) world).{@link ServerWorld#globalEntities globalEntities} == {@link GlobalEntitiesList this}</code></li>
	 * </ul>
	 *
	 * @param world the {@link World} that this {@link GlobalEntitiesList} is
	 *              applied to
	 */
	public GlobalEntitiesList(World world) {
		this.world = world;
	}

	/**
	 * {@inheritDoc}
	 * <hr>
	 * TODO
	 * <hr>
	 * <b>Mappings</b>
	 * <ul>
	 * <li><code>field_204810_e</code> = {@link LightningBoltEntity#caster}</li>
	 * </ul>
	 */
	@Override
	public boolean add(Entity entity) {
		if (entity.getType() == EntityType.LIGHTNING_BOLT) {
			if (ObfuscationReflectionHelper.getPrivateValue(LightningBoltEntity.class, (LightningBoltEntity) entity,
					"field_204810_e") == null
					&& world.getEntitiesWithinAABB(EntityType.SKELETON_HORSE, entity.getBoundingBox().grow(0, 1, 0),
							skelly -> true).isEmpty()) {
				if (LTConfig.doRealisticLightning())
					moveLightning(entity);

				if (!LTConfig.doSpawnFire() || getResistivity(entity.getPosition()) == Resistivity.CONDUCTOR)
					setEffectOnly(entity);
			}

			if (EntityHandler.removeDoFireTick(world))
				revertDoFireTick();

			Difficulty difficulty = world.getDifficulty();
			if (difficulty == Difficulty.NORMAL || difficulty == Difficulty.HARD)
				igniteBlocks(entity);
		}
		return super.add(entity);
	}

	/**
	 * Finds the highest {@link Block} or {@link Block}s in the {@link Chunk} of the
	 * given {@link Entity}. This is done by accessing the {@link Chunk}'s
	 * {@link Heightmap} of {@link Type#MOTION_BLOCKING}. All {@link Entity
	 * Entities} within the {@link Chunk} are also compared at their eye heights. If
	 * multiple {@link BlockPos}es have equal heights, one is chosen randomly using
	 * {@link SplittableRandom#nextInt(int)}.
	 *
	 * @param entity the {@link Entity} whose {@link Chunk} will be searched
	 * @return the {@link BlockPos} of one of the highest {@link Block}s or
	 *         {@link Entity Entities} in the given {@link Entity}'s {@link Chunk},
	 *         according to {@link Type#MOTION_BLOCKING}
	 */
	private BlockPos getBlockPos(Entity entity) {
		double max = 0;
		List<BlockPos> poses = new ArrayList<>();

		Chunk chunk = world.getChunkAt(entity.getPosition());
		Heightmap heightmap = chunk.getHeightmap(Type.MOTION_BLOCKING);
		ChunkPos chunkPos = chunk.getPos();
		for (int x = 0; x < 16; x++)
			for (int z = 0; z < 16; z++)
				max = scoreBlockPos(max, poses, chunkPos.getBlock(x, heightmap.getHeight(x, z), z));
		for (ClassInheritanceMultiMap<Entity> entities : chunk.getEntityLists())
			for (Entity entity1 : entities)
				if (entity1.onGround)
					max = scoreBlockPos(max, poses, new BlockPos(entity1.getEyePosition(1)).up());

		return poses.get(random.nextInt(poses.size()));
	}

	/**
	 * TODO
	 *
	 * @param pos TODO
	 * @return TODO
	 */
	private Resistivity getResistivity(BlockPos pos) {
		return ResistivityMap.getType(world.getBlockState(pos.func_177977_b()).getBlock().asItem());
	}

	/**
	 * TODO
	 * <hr>
	 * <b>Mappings</b>
	 * <ul>
	 * <li><code>func_195053_a</code> =
	 * {@link LightningBoltEntity#igniteBlocks(int)}</li>
	 * </ul>
	 *
	 * @param entity TODO
	 */
	private void igniteBlocks(Entity entity) {
		try {
			int extraIgnitions = LTConfig.getExtraIgnitions();
			LightningTweaks.log(
					"Invoking LightningBoltEntity#igniteBlocks(" + extraIgnitions + ") at " + entity.getPosition(),
					world);
			ObfuscationReflectionHelper.findMethod(LightningBoltEntity.class, "func_195053_a", int.class).invoke(entity,
					extraIgnitions);
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	/**
	 * TODO
	 *
	 * @param entity TODO
	 */
	private void moveLightning(Entity entity) {
		BlockPos pos = getBlockPos(entity);
		LightningTweaks.log("Moving lightning from " + entity.getPosition() + " to " + pos, world);
		entity.setPosition(pos.getX(), pos.getY(), pos.getZ());
	}

	/**
	 * TODO
	 */
	private void revertDoFireTick() {
		LightningTweaks.log("Reverting " + GameRules.DO_FIRE_TICK + " to true", world);
		EntityHandler.revertDoFireTick(world);
	}

	/**
	 * TODO
	 * <hr>
	 * <b>Mappings</b>
	 * <ul>
	 * <li><code>field_184529_d</code> = {@link LightningBoltEntity#effectOnly}</li>
	 * </ul>
	 *
	 * @param entity TODO
	 */
	private void setEffectOnly(Entity entity) {
		LightningTweaks.log("Setting LightningBoltEntity#effectOnly to true at " + entity.getPosition(), world);
		ObfuscationReflectionHelper.setPrivateValue(LightningBoltEntity.class, (LightningBoltEntity) entity, true,
				"field_184529_d");
	}
}
