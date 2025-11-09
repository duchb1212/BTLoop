import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class StrongBrick extends BrickFactory {
    private BufferedImage texture;

    /**
     * Create a StrongBrick with given position, size and hit points.
     */
    public StrongBrick(double posX, double posY, double width, double height, int hitPoints) {
        super(posX, posY, width, height, hitPoints);

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

    /**
     * Factory method: create a new StrongBrick at the given position using
     * this brick's size and initial hitPoints.
     * NOTE: use the provided posX/posY parameters (previous version incorrectly used this.posX/posY).
     */
    @Override
    public BrickFactory createBrick(double posX, double posY) {
        // Use this instance's size and default HP (or its current HP) when creating the new brick.
        // Typical strong brick has 3 HP; use getHitPoints() if you want to preserve the prototype's HP.
        int hp = Math.max(1, this.getHitPoints()); // ensure at least 1 if somehow invalid
        return new StrongBrick(posX, posY, this.getWidth(), this.getHeight(), hp);
    }

    @Override
    public BufferedImage getTexture() {
        return texture;
    }

    @Override
    public void setTexture(BufferedImage texture) {
        this.texture = texture;
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