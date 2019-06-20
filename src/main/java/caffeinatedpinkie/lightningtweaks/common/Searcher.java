package caffeinatedpinkie.lightningtweaks.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.SplittableRandom;

import javax.annotation.Nullable;

import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Multimap;

import caffeinatedpinkie.lightningtweaks.ConfigLT;
import caffeinatedpinkie.lightningtweaks.ConfigLT.ConfigWeight;
import caffeinatedpinkie.lightningtweaks.LoggerLT;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

/**
 * Methods to search for the appropriate {@link BlockPos} to move the lightning
 * bolt to.
 *
 * @author CaffeinatedPinkie
 */
public class Searcher {
    public static SplittableRandom random = new SplittableRandom();

    /**
     * Gets a random {@link BlockPos} from the given {@link Collection}. The
     * {@link Double}s in the {@link Entry Entries} are used as the cumulative
     * probability of the {@link Entry Entries} being selected.
     *
     * @param probabilities              a {@link Collection} that contains the
     *                                   ascending cumulative probabilities and the
     *                                   {@link BlockPos}es that are represented by
     *                                   each
     * @param totalCumulativeProbability the total cumulative probability of all
     *                                   {@link BlockPos}es
     * @return a {@link BlockPos} chosen at random from the given {@link Multimap}
     */
    public static BlockPos getRandomBlock(Collection<Pair<Double, List<BlockPos>>> probabilities,
	    double totalCumulativeProbability) {
	double randomValue = random.nextDouble(totalCumulativeProbability);
	for (Entry<Double, List<BlockPos>> probability : probabilities)
	    if (randomValue < probability.getKey())
		return getRandomBlock(probability.getValue());
	return null;
    }

    /**
     * Gets a random {@link BlockPos} from the given {@link List} using
     * {@link Random#nextInt()}.
     *
     * @param poses the {@link List} of {@link BlockPos}es that a random one will be
     *              chosen from
     * @return a {@link BlockPos} chosen at random from the given {@link List}
     */
    public static BlockPos getRandomBlock(List<BlockPos> poses) {
	return poses.get(random.nextInt(poses.size()));
    }

    /**
     * Whether the given {@link EntityLivingBase} is holding a {@link Item} such
     * that {@link AttributeMetallic#isMetallic(ItemStack)} is true.
     *
     * @param entity the {@link EntityLivingBase} being tested
     * @return true if the {@link EntityLivingBase} is holding a metal {@link Item},
     *         false otherwise
     */
    public static boolean isEntityHoldingMetallicItem(EntityLivingBase entity) {
	return AttributeMetallic.isMetallic(entity.getHeldItemMainhand())
		|| AttributeMetallic.isMetallic(entity.getHeldItemOffhand());
    }

    /**
     * Whether the given {@link Entity} is wearing any {@link ItemArmor} such that
     * {@link AttributeMetallic#isMetallic(ItemStack)} is true.
     *
     * @param entity the {@link Entity} being tested
     * @return true if the {@link Entity} is wearing metal {@link ItemArmor}, false
     *         otherwise
     */
    public static boolean isEntityWearingMetallicArmor(Entity entity) {
	for (ItemStack itemStack : entity.getArmorInventoryList())
	    if (AttributeMetallic.isMetallic(itemStack))
		return true;
	return false;
    }

    public int blocks, maxHeight;
    public Map<BlockPos, Entity> heightMap;
    public Entity lightning;
    public World world;

    public Searcher(World world, Entity lightning) {
	heightMap = new HashMap<>();
	this.lightning = lightning;
	this.world = world;
	populateHeightMap();
    }

    /**
     * Determines whether the {@link Entity} is within {@link ConfigLT#radius}
     * blocks around {@link #lightning}.
     *
     * @param entity the {@link Entity} to test
     * @return true if the {@link Entity} is within the radius, false otherwise
     */
    public boolean entityWithinRadius(Entity entity) {
	return Math.sqrt(Math.pow(entity.posX - lightning.posX, 2)
		+ Math.pow(entity.posZ - lightning.posZ, 2)) <= ConfigLT.radius;
    }

    /**
     * Determines the best {@link BlockPos} to strike at in a predictable manner.
     * The highest scoring one will always be returned.
     *
     * @return a {@link BlockPos} representing the highest scoring one within the
     *         search radius, taken from {@link #heightMap}
     */
    public BlockPos getNonRandomPosition() {
	double greatestScore = 0;
	List<BlockPos> poses = new ArrayList<>();
	for (Entry<BlockPos, Entity> entry : heightMap.entrySet()) {
	    double score = score(entry.getKey(), entry.getValue());
	    if (score >= greatestScore) {
		if (score != greatestScore) {
		    greatestScore = score;
		    poses.clear();
		}
		poses.add(entry.getKey());
	    }
	}
	return getRandomBlock(poses);
    }

    public BlockPos getPosition(Entity entity) {
	return new BlockPos(entity.getPositionEyes(1));
    }

    /**
     * Determines the best {@link BlockPos} to strike at in a nondeterministic
     * manner. A cumulative probability function is produced using the scores of
     * each {@link BlockPos} and a random one is chosen.
     *
     * @return a {@link BlockPos} representing a randomly chosen one based on its
     *         relative score to all others, taken from {@link #heightMap}
     */
    public BlockPos getRandomPosition() {
	MutableDouble cumulativeProbability = new MutableDouble();
	Map<Double, Pair<Double, List<BlockPos>>> probabilities = new LinkedHashMap<>();
	heightMap.forEach((pos, entity) -> probabilities
		.computeIfAbsent(score(pos, entity),
			probability -> Pair.of(cumulativeProbability.addAndGet(probability), new ArrayList<>()))
		.getRight().add(pos));
	return getRandomBlock(probabilities.values(), cumulativeProbability.doubleValue());
    }

    /**
     * Copy-and-paste of {@link World#getTopSolidOrLiquidBlock(BlockPos)} with
     * {@link Material#LEAVES} and foliage included.<br>
     * Finds the highest {@link BlockPos} on the x and z coordinate that is solid or
     * liquid, and returns its y coordinated.
     *
     * @param pos the {@link BlockPos} containing the x and z coordinates to look at
     * @return the {@link BlockPos} that represents the highest {@link Block} at the
     *         x and z
     */
    public BlockPos getTopSolidOrLiquidBlock(BlockPos pos) {
	Chunk chunk = world.getChunk(pos);
	BlockPos blockpos;
	BlockPos blockpos1;

	for (blockpos = new BlockPos(pos.getX(), chunk.getTopFilledSegment() + 16, pos.getZ()); blockpos
		.getY() >= 0; blockpos = blockpos1) {
	    blockpos1 = blockpos.down();
	    IBlockState state = chunk.getBlockState(blockpos1);

	    if (state.getMaterial().blocksMovement())
		break;
	}

	return blockpos;
    }

    /**
     * Populates {@link #heightMap} with the highest {@link BlockPos} for each x and
     * z coordinate within the search radius. Optionally stores an
     * {@link EntityLivingBase} at each {@link BlockPos}. {@link #maxHeight} is also
     * set.
     */
    public void populateHeightMap() {
	LoggerLT.log(() -> {
	    for (double x = lightning.posX - ConfigLT.radius; x <= lightning.posX + ConfigLT.radius; x++) {
		double zDifference = Math.sqrt(Math.pow(ConfigLT.radius, 2) - Math.pow(x - lightning.posX, 2));
		for (double z = lightning.posZ - zDifference; z <= lightning.posZ + zDifference; z++) {
		    BlockPos pos = getTopSolidOrLiquidBlock(new BlockPos(x, 0, z)).down();
		    if (pos.getY() >= 0) {
			heightMap.put(pos, null);
			maxHeight = Math.max(maxHeight, pos.getY());
		    }
		}
	    }
	    if (ConfigLT.weight.playerEntityRatio > 0)
		world.playerEntities.forEach(player -> {
		    if (entityWithinRadius(player))
			heightMap.put(getPosition(player), player);
		});
	    if (ConfigLT.weight.nonPlayerEntityRatio > 0)
		world.getLoadedEntityList().forEach(entity -> {
		    if (entity instanceof EntityLiving && entityWithinRadius(entity))
			heightMap.put(getPosition(entity), entity);
		});
	}, "Height map populated");
	LoggerLT.log("Processed " + heightMap.size() + " blocks.");
    }

    /**
     * Gives a weighted score for a given {@link BlockPos} based on its:<br>
     * - Height ({@link ConfigWeight#heightWeight})<br>
     * - Material ({@link ConfigWeight#materialWeight})<br>
     * - Shape ({@link ConfigWeight#shapeWeight})<br>
     * and whether the {@link Entity}'s:<br>
     * - wearing metal armor ({@link ConfigWeight#metalArmorRatio})<br>
     * - holding a metal item ({@link ConfigWeight#metalItemRatio})<br>
     * - an {@link EntityPlayer} ({@link ConfigWeight#playerEntityRatio})<br>
     * - not an {@link EntityPlayer} ({@link ConfigWeight#nonPlayerEntityRatio})<br>
     *
     * @param pos    the prospective {@link BlockPos} to score
     * @param entity an {@link Entity} at the {@link BlockPos} or null if there is
     *               none
     * @return a double representing the score of the given {@link BlockPos}
     */
    public double score(BlockPos pos, @Nullable Entity entity) {
	IBlockState state = world.getBlockState(pos);

	double score = ConfigLT.weight.heightWeight * pos.getY() / maxHeight;
	if (AttributeMetallic.isMetallic(state))
	    score += ConfigLT.weight.materialWeight;
	if (!world.isAirBlock(pos) && !state.isFullCube())
	    score += ConfigLT.weight.shapeWeight;
	if (entity != null) {
	    if (isEntityWearingMetallicArmor(entity))
		score *= ConfigLT.weight.metalArmorRatio;
	    if (isEntityHoldingMetallicItem((EntityLivingBase) entity))
		score *= ConfigLT.weight.metalItemRatio;
	    score *= entity instanceof EntityPlayer ? ConfigLT.weight.playerEntityRatio
		    : ConfigLT.weight.nonPlayerEntityRatio;
	}
	return score;
    }

    /**
     * Searches for the appropriate {@link BlockPos} based on {@link #world}, the
     * vanilla {@link BlockPos} of the {@link #lightning}, and
     * {@link ConfigLT#randomStrike}.
     *
     * @return the {@link BlockPos} that represents the highest scoring one or the
     *         randomly chosen one
     */
    public BlockPos search() {
	LoggerLT.log("Searching...");
	if (heightMap.isEmpty()) {
	    LoggerLT.log("No blocks found. Returning...");
	    return lightning.getPosition();
	}
	return (ConfigLT.randomStrike ? getRandomPosition() : getNonRandomPosition()).up();
    }
}
