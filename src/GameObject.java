import java.util.ArrayList;

public abstract class GameObject {
    protected double posX;
    protected double posY;

    protected double width;
    protected double height;

    /**
     * Construct a game object with position and size.
     *
     * @param posX   top-left X position
     * @param posY   top-left Y position
     * @param width  width (must be >= 0)
     * @param height height (must be >= 0)
     */
    public GameObject(double posX, double posY, double width, double height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("width/height must be >= 0");
        }
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
    }

    // --- Basic property getters / setters ---

    public double getPosX() {
        return posX;
    }

    public void setPosX(double posX) {
        this.posX = posX;
    }

    public double getPosY() {
        return posY;
    }

    public void setPosY(double posY) {
        this.posY = posY;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        if (width < 0) throw new IllegalArgumentException("width must be >= 0");
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        if (height < 0) throw new IllegalArgumentException("height must be >= 0");
        this.height = height;
    }

    /**
     * Update this object. Kept signature with ArrayList<GameObject> to match your existing code.
     * Subclasses override to implement behavior and collision handling.
     *
     * @param deltaTime  seconds since last frame
     * @param allObjects list of all game objects
     */
    public abstract void update(double deltaTime, ArrayList<GameObject> allObjects);

    // --- Utility methods helpful for collision and game logic ---

    /**
     * Simple AABB intersection check (non-inclusive on edges can be changed as needed).
     *
     * @param other other GameObject
     * @return true if bounding boxes overlap
     */
    public boolean intersects(GameObject other) {
        if (other == null) return false;
        return this.posX < other.posX + other.width
                && this.posX + this.width > other.posX
                && this.posY < other.posY + other.height
                && this.posY + this.height > other.posY;
    }

    /**
     * Checks if this object fully contains the other object's AABB.
     *
     * @param other other GameObject
     * @return true if other is fully inside this
     */
    public boolean contains(GameObject other) {
        if (other == null) return false;
        return other.posX >= this.posX
                && other.posY >= this.posY
                && other.posX + other.width <= this.posX + this.width
                && other.posY + other.height <= this.posY + this.height;
    }

    /**
     * Returns the center X coordinate of this object's bounding box.
     */
    public double centerX() {
        return posX + width * 0.5;
    }

    /**
     * Returns the center Y coordinate of this object's bounding box.
     */
    public double centerY() {
        return posY + height * 0.5;
    }

    @Override
    public String toString() {
        return String.format("GameObject[pos=(%.3f,%.3f), size=(%.3f,%.3f)]", posX, posY, width, height);
    }
}