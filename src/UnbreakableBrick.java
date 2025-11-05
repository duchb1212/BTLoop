import java.util.ArrayList;

/**
 * Unbreakable brick: a brick that cannot be destroyed (represented by very large HP).
 * Kept signature with ArrayList<GameObject> for compatibility.
 */
public class UnbreakableBrick extends BrickFactory {

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
    public UnbreakableBrick(double posX, double posY, double width, double height, int hitPoints) {
        super(posX, posY, width, height, hitPoints);
    }

    /**
     * Factory method: create a new UnbreakableBrick at the given position.
     * Uses this instance's width/height and sets HP to Integer.MAX_VALUE to model unbreakable behavior.
     */
    @Override
    public BrickFactory createBrick(double posX, double posY) {
        return new UnbreakableBrick(posX, posY, this.getWidth(), this.getHeight(), Integer.MAX_VALUE);
    }

    /**
     * Unbreakable bricks are static by default; no per-frame behavior.
     */
    @Override
    public void update(double deltaTime, ArrayList<GameObject> allObjects) {
        // No-op
    }
}