package phoupraw.mcmod.trifleclient.jade;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Vec2f;
import snownee.jade.api.ui.Element;

public class SpriteElement extends Element {
    private final Sprite sprite;
    private final int width;
    private final int height;
    private final Vec2f size;
    public SpriteElement(Sprite sprite, int width, int height) {
        this.sprite = sprite;
        this.width = width;
        this.height = height;
        size = new Vec2f(width, height);
    }
    @Override
    public Vec2f getSize() {
        return size;
    }
    @Override
    public void render(DrawContext drawContext, float x, float y, float maxX, float maxY) {
        drawContext.drawSprite((int) x, (int) y, 0, width, height, sprite);
    }
}
