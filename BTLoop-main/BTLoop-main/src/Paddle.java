import java.util.ArrayList;
public class Paddle extends MovableObject {
    protected double speed;
    protected double screenX;

    /**
     * @param posX    initial X position (top-left)
     * @param posY    initial Y position (top-left)
     * @param width   paddle width
     * @param height  paddle height
     * @param velX    initial velocity X
     * @param velY    initial velocity Y
     * @param speed   movement speed used when moving left/right
     * @param screenX horizontal screen size (used to clamp paddle)
     */
    public Paddle(double posX, double posY, double width, double height, double velX, double velY, double speed, double screenX) {
        super(posX, posY, width, height, velX, velY);
        if (screenX < 0) throw new IllegalArgumentException("screenX must be >= 0");
        this.speed = speed;
        this.screenX = screenX;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getScreenX() {
        return screenX;
    }

    public void setScreenX(double screenX) {
        if (screenX < 0) throw new IllegalArgumentException("screenX must be >= 0");
        this.screenX = screenX;
    }

    /** Start moving left at configured speed. */
    public void moveLeft() {
        this.velX = -Math.abs(speed);
    }

    /** Start moving right at configured speed. */
    public void moveRight() {
        this.velX = Math.abs(speed);
    }

    /** Stop horizontal movement. */
    public void stop() {
        this.velX = 0.0;
    }

    /**
     * Update paddle position and clamp within screen X bounds.
     * Kept ArrayList<GameObject> signature to match your codebase.
     */
    @Override
    public void update(double deltaTime, ArrayList<GameObject> allObjects) {
        // Move by current velocity (inherited move is protected)
        move(deltaTime);

        // Clamp within horizontal bounds [0, screenX - width]
        double maxX = Math.max(0.0, screenX - this.width);
        if (this.posX < 0.0) {
            this.posX = 0.0;
            this.velX = 0.0; // optional: stop on hitting edge
        } else if (this.posX > maxX) {
            this.posX = maxX;
            this.velX = 0.0; // optional: stop on hitting edge
        }

        // Note: allObjects parameter is currently unused; kept for compatibility
    }
}