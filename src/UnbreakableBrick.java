import java.util.ArrayList;

/**
 * Unbreakable brick: a brick that cannot be destroyed (represented by very large HP).
 * Kept signature with ArrayList<GameObject> for compatibility.
 */
public class UnbreakableBrick extends Brick {

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
    }


    @Override
    public void update(double deltaTime, ArrayList<GameObject> allObjects) {
        // No-op
    }
}