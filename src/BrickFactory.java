public abstract class BrickFactory extends GameObject {
    // Use private for encapsulation
    private int hitPoints;

    /**
     * Construct a Brick-like object (kept name BrickFactory for compatibility).
     *
     * @param posX initial X (top-left)
     * @param posY initial Y (top-left)
     * @param width width (>= 0)
     * @param height height (>= 0)
     * @param hitPoints initial hit points (>= 0)
     * @throws IllegalArgumentException if width/height/hitPoints are invalid
     */
    public BrickFactory(double posX, double posY, double width, double height, int hitPoints) {
        super(posX, posY, width, height);
        if (hitPoints < 0) throw new IllegalArgumentException("hitPoints must be >= 0");
        this.hitPoints = hitPoints;
    }

    /**
     * Return the current hit points.
     */
    public int getHitPoints() {
        return hitPoints;
    }

    /**
     * Set hit points (non-negative).
     */
    public void setHitPoints(int hitPoints) {
        if (hitPoints < 0) throw new IllegalArgumentException("hitPoints must be >= 0");
        this.hitPoints = hitPoints;
    }

    /**
     * Factory method to create a new brick instance of this concrete type at the given position.
     * Concrete subclasses should implement and return a new instance with the same properties
     * (size, initial HP, etc.) positioned at posX/posY.
     *
     * @param posX target X
     * @param posY target Y
     * @return a new BrickFactory instance (concrete subclass)
     */
    public abstract BrickFactory createBrick(double posX, double posY);

    /**
     * Apply damage to this brick. Returns true if the brick was destroyed by this damage.
     *
     * @param damage amount of damage to apply (>= 0)
     * @return true if brick transitioned to destroyed (hp <= 0) as a result
     */
    public boolean takeDamage(int damage) {
        if (damage < 0) throw new IllegalArgumentException("damage must be >= 0");
        if (isDestroyed()) return false; // already destroyed
        this.hitPoints -= damage;
        if (this.hitPoints <= 0) {
            this.hitPoints = 0;
            onDestroyed();
            return true;
        }
        return false;
    }

    /**
     * Hook called when the brick is destroyed. Subclasses may override to play effects,
     * drop items, increment score, etc. Default implementation does nothing.
     */
    protected void onDestroyed() {
        // default: no-op
    }

    /**
     * Check whether this brick is destroyed (hp == 0).
     */
    public boolean isDestroyed() {
        return hitPoints <= 0;
    }

    /**
     * Default update does nothing for static bricks. Kept signature with ArrayList for compatibility.
     * Subclasses that need behavior can override.
     *
     * @param deltaTime seconds since last frame
     * @param allObjects list of all game objects
     */
    @Override
    public void update(double deltaTime, java.util.ArrayList<GameObject> allObjects) {
        // Bricks are usually static; override if needed
    }
}