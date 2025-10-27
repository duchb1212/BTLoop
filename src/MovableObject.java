public abstract class MovableObject extends GameObject {
    protected double velX;
    protected double velY;

    public MovableObject(double posX, double posY, int width, int height, double velX, double velY) {
        super(posX, posY, width, height);
        this.velX = velX;
        this.velY = velY;
    }

    public double getVelX() {
        return velX;
    }

    public double getVelY() {
        return velY;
    }

    public void setVelX(double velX) {
        this.velX = velX;
    }

    public void setVelY(double velY) {
        this.velY = velY;
    }

    public void SetVelocity(double velX, double velY) {
        this.velX = velX;
        this.velY = velY;
    }

    public void ReverseVelocity() {
        this.velX = -velX;
        this.velY = -velY;
    }

    public void ReverseX() {
        this.velX = -velX;
    }

    public void ReverseY() {
        this.velY = -velY;
    }

    public void move() {
        this.posX += velX;
        this.posY += velY;
    }

    @Override
    public void update() {
        move();
    }


}
