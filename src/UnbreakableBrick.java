import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Unbreakable brick: a brick that cannot be destroyed (represented by very large HP).
 * Kept signature with ArrayList<GameObject> for compatibility.
 */
public class UnbreakableBrick extends Brick {
    private BufferedImage texture;

    /**
     * Construct an UnbreakableBrick. hitPoints is allowed but typically ignored because
     * this brick behaves as unbreakable (we can use Integer.MAX_VALUE as default).
     *
     * @param posX initial x (top-left)
     * @param posY initial y (top-left)
     * @param width brick width
     * @param height brick height
     * @param hitPoints initial hit points (optional; typically large)
     */
    public UnbreakableBrick(double posX, double posY, double width, double height, int screenWidth, int screenHeight, int hitPoints) {
        super(posX, posY, width, height, screenWidth, screenHeight, hitPoints);

        try {
            // Load texture image từ thư mục resources
            URL imageUrl = getClass().getResource("/textures/RedBrick.png");
            if (imageUrl == null) {
                System.err.println("⚠️ Không tìm thấy ảnh RedBrick.png!");
            } else {
                texture = ImageIO.read(imageUrl);
            }

            texture = ImageIO.read(getClass().getResource("/textures/RedBrick.png"));

        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
            texture = null; // fallback nếu load lỗi
        }
    }

    @Override
    public BufferedImage getTexture() {
        return texture;
    }

    @Override
    public void update(double deltaTime, ArrayList<GameObject> allObjects) {
        // No-op
    }
}