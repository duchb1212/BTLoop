import java.awt.*;

public class Renderer {
    /**
     * Renders an axis-aligned rectangle for the given GameObject using the provided Graphics.
     * Keeps implementation simple and safe: null-checks and uses Graphics2D when available.
     */
    public void render(Graphics g, GameObject gameObject, java.awt.Color color) {
        if (g == null || gameObject == null || color == null) return;

        Graphics2D g2 = (g instanceof Graphics2D) ? (Graphics2D) g : null;
        if (g2 != null) {
            g2.setColor(color);
            // Use round-to-int to avoid negative-zero issues and improve visual placement
            int x = (int) Math.round(gameObject.getPosX());
            int y = (int) Math.round(gameObject.getPosY());
            int w = Math.max(0, (int) Math.round(gameObject.getWidth()));
            int h = Math.max(0, (int) Math.round(gameObject.getHeight()));
            g2.fillRect(x, y, w, h);
        } else {
            g.setColor(color);
            g.fillRect((int) Math.round(gameObject.getPosX()),
                    (int) Math.round(gameObject.getPosY()),
                    Math.max(0, (int) Math.round(gameObject.getWidth())),
                    Math.max(0, (int) Math.round(gameObject.getHeight())));
        }
    }
}