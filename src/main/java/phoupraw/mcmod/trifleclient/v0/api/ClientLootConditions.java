package phoupraw.mcmod.trifleclient.v0.api;

import lombok.experimental.UtilityClass;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.loot.condition.LocationCheckLootCondition;
import net.minecraft.predicate.BlockPredicate;
import net.minecraft.predicate.FluidPredicate;
import net.minecraft.predicate.LightPredicate;
import net.minecraft.predicate.entity.*;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import phoupraw.mcmod.trifleclient.util.MCUtils;

@UtilityClass
public class ClientLootConditions {
    /**
     客户端一般不能获取到完整的NBT，须谨慎使用{@link EntityPredicate#nbt()}
     */
    public boolean test(EntityPredicate self, World world, Vec3d origin, Entity entity) {
        if (self.type().isPresent() && !self.type().get().matches(entity.getType())) {
            return false;
        }
        if (self.distance().isPresent() && !self.distance().get().test(origin.getX(), origin.getY(), origin.getZ(), entity.getX(), entity.getY(), entity.getZ())) {
            return false;
        }
        if (self.movement().isPresent()) {
            Vec3d mpt = entity.getMovement().multiply(20.0);
            if (!self.movement().get().test(mpt.getX(), mpt.getY(), mpt.getZ(), entity.fallDistance)) {
                return false;
            }
        }
        var location = self.location();
        if (location.located().isPresent() && !test(location.located().get(), world, entity.getPos())) {
            return false;
        }
        if (location.steppingOn().isPresent() && !test(location.steppingOn().get(), world, Vec3d.ofCenter(entity.getSteppingPos()))) {
            return false;
        }
        if (location.affectsMovement().isPresent() && !test(location.steppingOn().get(), world, Vec3d.ofCenter(entity.getVelocityAffectingPos()))) {
            return false;
        }
        if (self.effects().isPresent() && !self.effects().get().test(entity)) {
            return false;
        }
        if (self.nbt().isPresent() && !self.nbt().get().test(entity)) {
            return false;
        }
        if (self.flags().isPresent() && !self.flags().get().test(entity)) {
            return false;
        }
        if (self.equipment().isPresent() && !self.equipment().get().test(entity)) {
            return false;
        }
        if (self.typeSpecific().isPresent() && !test(self.typeSpecific().get(), origin, entity)) {
            return false;
        }
        if (self.periodicTick().isPresent() && entity.age % self.periodicTick().get() != 0) {
            return false;
        }
        if (self.team().isPresent()) {
            Team team = entity.getScoreboardTeam();
            if (team == null) {
                return false;
            }
            if (!self.team().get().equals(team.getName())) {
                return false;
            }
        }
        if (self.slots().isPresent() && !self.slots().get().matches(entity)) {
            return false;
        }
        return true;
    }
    public boolean test(FishingHookPredicate self, Entity entity) {
        return self.inOpenWater().isEmpty() || entity instanceof FishingBobberEntity bobber && self.inOpenWater().get() == bobber.isInOpenWater();
    }
    /**
     只支持{@link PlayerPredicate},{@link FishingHookPredicate},{@link SlimePredicate}
     */
    public boolean test(EntitySubPredicate self, Vec3d origin, Entity entity) {
        if (self instanceof FishingHookPredicate predicate) {
            return test(predicate, entity);
        }
        if (self instanceof PlayerPredicate predicate) {
            return test(predicate, origin, entity);
        }
        if (self instanceof SlimePredicate predicate) {
            return test(predicate, entity);
        }
        return true;
    }
    @Environment(EnvType.CLIENT)
    public boolean test(PlayerPredicate self, Vec3d origin, Entity entity) {
        if (!(entity instanceof ClientPlayerEntity player)) {
            return false;
        }
        if (!self.experienceLevel().test(player.experienceLevel)) {
            return false;
        }
        if (!self.gameMode().contains(MCUtils.getInteractor().getCurrentGameMode())) {
            return false;
        }
        for (var matcher : self.stats()) {
            if (!matcher.test(player.getStatHandler())) {
                return false;
            }
        }
        for (var entry : self.recipes().object2BooleanEntrySet()) {
            if (player.getRecipeBook().contains(entry.getKey()) != entry.getBooleanValue()) {
                return false;
            }
        }
        if (self.lookingAt().isPresent()) {
            HitResult target = MinecraftClient.getInstance().crosshairTarget;
            if (!(target instanceof EntityHitResult hitResult)) {
                return false;
            }
            if (!test(self.lookingAt().get(), hitResult.getEntity().getWorld(), origin, hitResult.getEntity())) {
                return false;
            }
        }
        return true;
    }
    public boolean test(SlimePredicate self, Entity entity) {
        if (!(entity instanceof SlimeEntity slime)) {
            return false;
        }
        if (!self.size().test(slime.getSize())) {
            return false;
        }
        return true;
    }
    public boolean test(LocationCheckLootCondition self, World world, Vec3d pos) {
        return self.predicate().isEmpty() || test(self.predicate().get(), world, pos.add(Vec3d.of(self.offset())));
    }
    public boolean test(LocationPredicate self, World world, Vec3d pos) {
        return test(self, world, pos.getX(), pos.getY(), pos.getZ());
    }
    /**
     忽略{@link LocationPredicate#structures()}
     */
    public boolean test(LocationPredicate self, World world, double x, double y, double z) {
        if (self.position().isPresent() && !self.position().get().test(x, y, z)) {
            return false;
        }
        BlockPos blockPos = BlockPos.ofFloored(x, y, z);
        if (self.biomes().isPresent() && !self.biomes().get().contains(world.getBiome(blockPos))) {
            return false;
        }
        if (self.dimension().isPresent() && !self.dimension().get().equals(world.getRegistryKey())) {
            return false;
        }
        if (self.smokey().isPresent() && self.smokey().get() != CampfireBlock.isLitCampfireInRange(world, blockPos)) {
            return false;
        }
        if (self.light().isPresent() && !test(self.light().get(), world, blockPos)) {
            return false;
        }
        if (self.block().isPresent() && !test(self.block().get(), world, blockPos)) {
            return false;
        }
        if (self.fluid().isPresent() && !test(self.fluid().get(), world, blockPos)) {
            return false;
        }
        if (self.canSeeSky().isPresent() && self.canSeeSky().get() != world.isSkyVisible(blockPos)) {
            return false;
        }
        return true;
    }
    public boolean test(LightPredicate self, World world, BlockPos pos) {
        return world.canSetBlock(pos) && self.range().test(world.getLightLevel(pos));
    }
    /**
     客户端一般不能获取到完整的NBT，须谨慎使用{@link BlockPredicate#nbt()}
     */
    public boolean test(BlockPredicate self, World world, BlockPos pos) {
        if (!world.canSetBlock(pos)) {
            return false;
        }
        BlockState state = world.getBlockState(pos);
        if (self.blocks().isPresent() && !state.isIn(self.blocks().get())) {
            return false;
        }
        if (self.state().isPresent() && !self.state().get().test(state)) {
            return false;
        }
        if (!self.nbt().isPresent()) return true;
        BlockEntity blockEntity = world.getBlockEntity(pos);
        return blockEntity != null && self.nbt().get().test(blockEntity.createNbtWithIdentifyingData(world.getRegistryManager()));
    }
    public boolean test(FluidPredicate self, World world, BlockPos pos) {
        if (!world.canSetBlock(pos)) {
            return false;
        }
        FluidState fluidState = world.getFluidState(pos);
        return (self.fluids().isEmpty() || fluidState.isIn(self.fluids().get())) && (!self.state().isPresent() || self.state().get().test(fluidState));
    }
}
