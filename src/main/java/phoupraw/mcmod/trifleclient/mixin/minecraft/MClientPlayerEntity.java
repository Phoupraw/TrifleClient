package phoupraw.mcmod.trifleclient.mixin.minecraft;

import com.mojang.authlib.GameProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import phoupraw.mcmod.trifleclient.events.OnClientPlayerMove;

@Environment(EnvType.CLIENT)
@Mixin(value = ClientPlayerEntity.class)
abstract class MClientPlayerEntity extends AbstractClientPlayerEntity {
    public MClientPlayerEntity(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }
    //@ModifyVariable(method = "move", at = @At("HEAD"), argsOnly = true)
    //private Vec3d speedSpeed(Vec3d movement) {
    //
    //    return SpeedSpeed.onClientPlayerMove((ClientPlayerEntity) (Object) this, movement);
    //}
    @ModifyVariable(method = "move", at = @At("HEAD"), argsOnly = true)
    private Vec3d onClientPlayerMove(Vec3d movement) {
        return OnClientPlayerMove.EVENT.invoker().onClientPlayerMove((ClientPlayerEntity) (Object) this, movement);
    }
}
