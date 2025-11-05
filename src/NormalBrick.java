import java.util.ArrayList;

public class NormalBrick extends BrickFactory {
    /**
     * Create a NormalBrick with given position, size and hit points.
     *
     * @param posX initial x (top-left)
     * @param posY initial y (top-left)
     * @param width brick width
     * @param height brick height
     * @param hitPoints initial hit points (>= 0)
     */
    public NormalBrick(double posX, double posY, double width, double height, int hitPoints) {
        super(posX, posY, width, height, hitPoints);
    }

    /**
     * Factory method: create a new NormalBrick at the given position using
     * this brick's size and initial hitPoints.
     *
     * Note: use the provided posX/posY parameters (previous version used this.posX/posY by mistake).
     */
    @Override
    public BrickFactory createBrick(double posX, double posY) {
        return new NormalBrick(posX, posY, this.getWidth(), this.getHeight(), this.getHitPoints());
    }

    /**
     * Bricks are usually static; override if you need behavior.
     * Kept signature with ArrayList<GameObject> for compatibility.
     */
    @Override
    public void update(double deltaTime, ArrayList<GameObject> allObjects) {
        // No-op for static bricks
    }
}