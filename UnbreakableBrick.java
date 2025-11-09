import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class UnbreakableBrick extends BrickFactory {
    private BufferedImage texture;

    /**
     * Create an UnbreakableBrick with given position, size and hit points.
     */
    public UnbreakableBrick(double posX, double posY, double width, double height, int hitPoints) {
        super(posX, posY, width, height, hitPoints);

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

    /**
     * Factory method: create a new NormalBrick at the given position using
     * this brick's size and initial hitPoints.
     */
    @Override
    public BrickFactory createBrick(double posX, double posY) {
        return new NormalBrick(posX, posY, this.getWidth(), this.getHeight(), this.getHitPoints());
    }

    public BufferedImage getTexture() {
        return texture;
    }

    @Override
    public void update(double deltaTime, ArrayList<GameObject> allObjects) {
        // Gạch tĩnh -> không cần update
    }
}
