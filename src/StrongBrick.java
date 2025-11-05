import java.util.ArrayList;

public class StrongBrick extends BrickFactory {
    /**
     * Create a StrongBrick with given position, size and hit points.
     *
     * @param posX initial x (top-left)
     * @param posY initial y (top-left)
     * @param width brick width
     * @param height brick height
     * @param hitPoints initial hit points (>= 0) — typically 3 for strong bricks
     */
    public StrongBrick(double posX, double posY, double width, double height, int hitPoints) {
        super(posX, posY, width, height, hitPoints);
    }

    /**
     * Factory method: create a new StrongBrick at the given position using
     * this brick's size and initial hitPoints.
     *
     * NOTE: use the provided posX/posY parameters (previous version incorrectly used this.posX/posY).
     */
    @Override
    public BrickFactory createBrick(double posX, double posY) {
        // Use this instance's size and default HP (or its current HP) when creating the new brick.
        // Typical strong brick has 3 HP; use getHitPoints() if you want to preserve the prototype's HP.
        int hp = Math.max(1, this.getHitPoints()); // ensure at least 1 if somehow invalid
        return new StrongBrick(posX, posY, this.getWidth(), this.getHeight(), hp);
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