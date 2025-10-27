public class Paddle extends MovableObject {
    private double speed;
    private int screenWidth;

    public Paddle(int posX, int posY, int width, int height, double speed, int screenWidth) {
        super(posX, posY, width, height, 0, 0);
        this.speed = speed;
        this.screenWidth = screenWidth;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    public void MoveLeft() {
        velX = -speed;
    }

    public void MoveRight() {
        velX = speed;
    }

    public void Stop() {
        velX = 0.0;
    }

    @Override
    public void update() {
        move();
        if (posX < 0) {
            posX = 0;
        }
        if (posX + width > screenWidth) {
            posX = screenWidth - width;
        }
    }
}
