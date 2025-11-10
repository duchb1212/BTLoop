import java.awt.*;

public class Renderer {
    /**
     * Renders an axis-aligned rectangle for the given GameObject using the provided Graphics.
     * Keeps implementation simple and safe: null-checks and uses Graphics2D when available.
     */
    public void render(Graphics g, GameObject gameObject, java.awt.Color color) {
        if (g == null || gameObject == null) return;

        Graphics2D g2 = (g instanceof Graphics2D) ? (Graphics2D) g : null;

        // Lấy thông tin vị trí và kích thước (đảm bảo không bị giá trị âm)
        int x = (int) Math.round(gameObject.getPosX());
        int y = (int) Math.round(gameObject.getPosY());
        int w = Math.max(0, (int) Math.round(gameObject.getWidth()));
        int h = Math.max(0, (int) Math.round(gameObject.getHeight()));

        // Nếu có texture → vẽ ảnh
        if (gameObject.getTexture() != null) {
            if (g2 != null) {
                g2.drawImage(gameObject.getTexture(), x, y, w, h, null);
            } else {
                g.drawImage(gameObject.getTexture(), x, y, w, h, null);
            }
        }
        // Nếu không có texture → vẽ khối màu
        else if (color != null) {
            if (g2 != null) {
                g2.setColor(color);
                g2.fillRect(x, y, w, h);
            } else {
                g.setColor(color);
                g.fillRect(x, y, w, h);
            }
        }
    }

}