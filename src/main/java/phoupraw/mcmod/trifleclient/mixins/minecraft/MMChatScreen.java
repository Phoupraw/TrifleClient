package phoupraw.mcmod.trifleclient.mixins.minecraft;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.util.Util;
import org.jetbrains.annotations.ApiStatus;

import java.nio.file.Path;

@ApiStatus.Internal
@ApiStatus.NonExtendable
public interface MMChatScreen {
    static boolean rightClick(Style style) {
        if (style != null) {
            ClickEvent clickEvent = style.getClickEvent();
            if (clickEvent != null && clickEvent.getAction() == ClickEvent.Action.OPEN_FILE) {
                String value = clickEvent.getValue();
                Path path = Path.of(value);
                MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK.value(), 1, 0.1f));
                //if (Util.getOperatingSystem() == Util.OperatingSystem.WINDOWS) {
                //try {
                //    //Desktop.getDesktop().open(path.toFile());
                //    //Runtime.getRuntime().exec(new String[]{"explorer /select," + path.getFileName()},null,path.getParent().toFile());
                //    //Runtime.getRuntime().exec(new String[]{/*"rundll32",*/ "url.dll,FileProtocolHandler", "explorer /select," + path.getFileName()},null,path.getParent().toFile());
                //    //Runtime.getRuntime().exec(new String[]{"explorer /select," + path});
                //    //Process exec = Runtime.getRuntime().exec(new String[]{});
                //    //new ProcessBuilder("rundll32", "url.dll,FileProtocolHandler", "explorer d:\\ccc" ).start();
                //    @SuppressWarnings("removal") Process process = AccessController.doPrivileged((PrivilegedExceptionAction<? extends Process>)  () -> Runtime.getRuntime().exec(new String[]{"rundll32", "url.dll,FileProtocolHandler","explorer /select,"+path}));
                //    process.getInputStream().close();
                //    process.getErrorStream().close();
                //    process.getOutputStream().close();
                //    return true;
                //} catch (Exception e) {
                //    TrifleClient.LOGGER.throwing(e);
                //}
                //}
                Util.getOperatingSystem().open(path.getParent());
                return true;
            }
        }
        return false;
    }
}
