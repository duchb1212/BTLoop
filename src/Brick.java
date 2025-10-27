import java.awt.*;

public abstract class Brick extends GameObject {
    protected int hitPoint;
    protected String type;
    protected boolean destroyed;

    public Brick(double posX, double posY, int width, int height, int hitPoint, String type) {
        super(posX, posY, width, height);
        this.hitPoint = hitPoint;
        this.type = type;
    }

    public int getHitPoint() {
        return hitPoint;
    }

    public void setHitPoint(int hitPoint) {
        this.hitPoint = hitPoint;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
    }

    public void TakeHit() {
        if (!destroyed){
            hitPoint--;
            if (hitPoint <= 0){
                destroyed = true;
            }
        }
    }

    @Override
    public void update(){};

}
