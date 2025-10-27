import java.awt.*;

public class Renderer {
    public void render(Graphics g, GameObject gameObject, Color color) {
        g.setColor(color);
        g.fillRect((int) gameObject.getPosX(), (int) gameObject.getPosY()
                , (int) gameObject.getWidth(), (int) gameObject.getHeight());;
    }
}
