import java.util.ArrayList;

public class StrongBrick extends Brick {
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