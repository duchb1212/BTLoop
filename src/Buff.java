import java.util.ArrayList;
import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

public class Buff extends MovableObject {
    private double speed;    // Tốc độ cơ bản (ví dụ: 300 pixel/giây)
    private double dirX;     // Hướng X (1 hoặc -1)
    private double dirY;     // Hướng Y (1 hoặc -1)
    private int screenWidth;
    private int screenHeight;
    private boolean markedForRemoval = false;

    public enum BuffType {
        Normal_Ball(null),
        Fire_Ball("textures/Fire_Buff.png"),
        Enlarged_Ball("textures/Enlarged_Buff.png"),
        Split_Ball("textures/Split_Buff.png"),
        Heart_Ball("textures/Heart_Buff.png"),
        EnlargedPaddle_Ball("textures/Enlarged_Paddle_Buff.png");

        private final String texturePath;
        private Image texture;

        BuffType(String texturePath) {
            this.texturePath = texturePath;
        }

        public Image getTexture() {
            if (texturePath == null) return null;
            if (texture == null) {
                try {
                    texture = ImageIO.read(getClass().getClassLoader().getResource(texturePath));
                } catch (IOException | IllegalArgumentException e) {
                    System.err.println("Không thể load texture: " + texturePath);
                }
            }
            return texture;
        }
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
