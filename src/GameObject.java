import java.awt.*;

public abstract class GameObject {
    protected double posX;
    protected double posY;

    protected int width;
    protected int height;

    public GameObject(double posX, double posY, int width, int height) {
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
    }

    public double getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public double getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public abstract void update();

    /**
     * Get the hitbox of the object.
     *
     * @return A rectangle representing the hitbox of the object.
     */
    public Rectangle GetHitBox() {
        return new Rectangle((int)posX, (int)posY, width, height);
    }

    public boolean Collision(GameObject otherObject) {
        return GetHitBox().intersects(otherObject.GetHitBox());
    }
}