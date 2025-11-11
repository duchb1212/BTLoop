import java.util.ArrayList;

public class Buff extends MovableObject {
    private double speed;    // Tốc độ cơ bản (ví dụ: 300 pixel/giây)
    private double dirX;     // Hướng X (1 hoặc -1)
    private double dirY;     // Hướng Y (1 hoặc -1)
    private int screenWidth;
    private int screenHeight;
    private boolean markedForRemoval = false;

    public enum BuffType {
        Normal_Ball,
        Fire_Ball,
        Enlarged_Ball,
        Split_Ball,
        Gun_Ball,
        Heart_Ball;
    }
    private BuffType buff;

    public Buff(double posX, double posY, double width, double height, double velX, double velY, double speed,
                double dirX, double dirY, int screenWidth, int screenHeight, BuffType buff) {
        super(posX, posY, width, height, velX, velY);
        this.speed = speed;
        this.dirX = dirX;
        this.dirY = dirY;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.buff = buff;
    }

    public BuffType getBuffType() {
        return buff;
    }

    public void setBuffType(BuffType buff) {
        this.buff = buff;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public void setScreenHeight(int screenHeight) {
        this.screenHeight = screenHeight;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getDirX() {
        return dirX;
    }

    public void setDirX(double dirX) {
        this.dirX = dirX;
    }

    public double getDirY() {
        return dirY;
    }

    public void setDirY(double dirY) {
        this.dirY = dirY;
    }

    public boolean isMarkedForRemoval() {
        return markedForRemoval;
    }

    @Override
    public void update(double deltaTime, ArrayList<GameObject> objects) {
        move(deltaTime);
        if (this.screenHeight > 0 && this.posY + this.height >= this.screenHeight) {
            this.markedForRemoval = true;
            return;
        }
    }
}
