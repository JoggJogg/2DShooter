import nl.han.ica.oopg.objects.GameObject;
import processing.core.PGraphics;

public class Button extends GameObject {

    private Gamestate targetState;
    private String description;
    private int color;

    public Button(float x, float y, int width, int height, int color, String text, Gamestate targetState){
        setHeight(height);
        setWidth(width);
        setX(x);
        setY(y);
        this.color = color;
        this.description = text;
        this.targetState = targetState;
    }

    @Override
    public void update() {

    }

    @Override
    public void draw(PGraphics g) {
        g.rectMode(g.CORNER);
        g.stroke(color);
        g.fill(color);
        g.rect(getX(), getY(), getWidth(), getHeight());
        g.text(description, getX(), getY());
    }
}