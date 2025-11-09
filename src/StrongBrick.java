import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class StrongBrick extends Brick {
    private BufferedImage texture;

    /**
     * Create a StrongBrick with given position, size and hit points.
     *
     * @param posX initial x (top-left)
     * @param posY initial y (top-left)
     * @param width brick width
     * @param height brick height
     * @param hitPoints initial hit points (>= 0) — typically 3 for strong bricks
     */
    public StrongBrick(double posX, double posY, double width, double height, int screenWidth, int screenHeight, int hitPoints) {
        super(posX, posY, width, height, screenWidth, screenHeight, hitPoints);

        try {
            // Load texture image từ thư mục resources
            URL imageUrl = getClass().getResource("/textures/BlueBrick.png");
            if (imageUrl == null) {
                System.err.println("⚠️ Không tìm thấy ảnh BlueBrick.png!");
            } else {
                texture = ImageIO.read(imageUrl);
            }

            texture = ImageIO.read(getClass().getResource("/textures/BlueBrick.png"));

        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
            texture = null; // fallback nếu load lỗi
        }
    }

    @Override
    public BufferedImage getTexture() {
        return texture;
    }

    /**
     * Bricks are static by default; override if behavior is needed.
     * Kept signature with ArrayList<GameObject> for compatibility.
     */
    @Override
    public void update(double deltaTime, ArrayList<GameObject> allObjects) {
        // No-op for static bricks
    }
}