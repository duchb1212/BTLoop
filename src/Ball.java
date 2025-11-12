import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

/**
 * Ball (Quả bóng) kế thừa từ MovableObject.
 * Quản lý chuyển động, va chạm, và buff.
 */
public class Ball extends MovableObject {
    private BufferedImage texture;
    private BufferedImage normalTexture;
    private BufferedImage fireTexture;
    private double speed;    // Tốc độ (pixel/giây)
    private double dirX;     // Hướng X
    private double dirY;     // Hướng Y
    public CollisionResult lastCollision = null;
    private HashMap<Buff.BuffType, Double> buffs;
    private boolean isLaunched = false;
    private int damage = 1;

    private int screenWidth;
    private int screenHeight;
    private final double baseWidth = 25;
    private final double baseHeight = 25;

    private static final double EPS = 1e-8;
    private static final double PUSH_OUT = 3.0;

    public Ball(double posX, double posY, double width, double height, double speed, double dirX, double dirY, int screenWidth, int screenHeight) {
        super(posX, posY, width, height, 0, 0);
        this.speed = speed;
        this.dirX = dirX;
        this.dirY = dirY;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.velX = speed * dirX;
        this.velY = speed * dirY;
        this.buffs = new HashMap<>();
        try {
            normalTexture = ImageIO.read(getClass().getResource("/textures/Ball.png"));
            fireTexture = ImageIO.read(getClass().getResource("/textures/FireBall.png"));
            texture = normalTexture;
        } catch (IOException e) {
            e.printStackTrace();
            texture = null;
        }
    }

    // Getters and Setters:
    public double getSpeed() { return speed; }
    public void setSpeed(double newSpeed) {
        this.speed = newSpeed;
        this.velX = this.speed * this.dirX;
        this.velY = this.speed * this.dirY;
    }
    public int getDamage() { return damage; }
    public void setDamage(int damage) { this.damage = damage; }
    public HashMap<Buff.BuffType, Double> getBuffs() { return buffs; }
    public void setBuffs(HashMap<Buff.BuffType, Double> buffs) { this.buffs = buffs; }
    public boolean isLaunched() { return isLaunched; }
    public void setLaunched(boolean launched) { isLaunched = launched; }
    public BufferedImage getTexture() { return texture; }

    public boolean checkWallCollision() {
        if (posX <= 0) {
            posX = 0;
            dirX = 1; velX = speed * dirX;
        }
        if (posX + width > screenWidth) {
            posX = screenWidth - width;
            dirX = -1; velX = speed * dirX;
        }
        if (posY <= 0) {
            posY = 0;
            dirY = 1; velY = speed * dirY;
        }
        if (posY + height > screenHeight) {
            return true; // rơi khỏi màn hình dưới
        }
        return false;
    }

    /**
     * Hàm update chính của bóng - Check va chạm, đổi hướng, đổi texture theo buff
     */
    @Override
    public void update(double deltaTime, ArrayList<GameObject> allObjects) {
        double remainingTime = 1.0;
        int maxIterations = 5;

        for (int iter = 0; iter < maxIterations && remainingTime > EPS; iter++) {
            double moveX = this.velX * deltaTime * remainingTime;
            double moveY = this.velY * deltaTime * remainingTime;
            CollisionResult bestCollision = null;

            for (GameObject other : allObjects) {
                if (other instanceof Ball || other instanceof Buff || other instanceof Paddle) continue;
                if (other instanceof Brick) {
                    Brick brick = (Brick) other;
                    if (brick.isDestroyed()) continue; // skip destroyed bricks
                    if (!(brick instanceof IceBrick) && this.getBuffs().containsKey(Buff.BuffType.Fire_Ball)) {
                        continue; // Fire Ball skips non-Ice bricks
                    }
                }

                CollisionResult currentCollision = CollisionUtils.sweptAABB(this, other, moveX, moveY);
                if (currentCollision != null && (bestCollision == null || currentCollision.t < bestCollision.t)) {
                    bestCollision = currentCollision;
                }
            }

            if (bestCollision != null && bestCollision.t < 1.0) {
                lastCollision = bestCollision;
                double t = Math.max(bestCollision.t, 0.0);
                this.posX += moveX * t;
                this.posY += moveY * t;

                if (bestCollision.normalX != 0.0) {
                    if (bestCollision.targetObject instanceof Paddle) {
                        this.dirX = -this.dirX;
                    } else if (!buffs.containsKey(Buff.BuffType.Fire_Ball)) {
                        this.dirX = -this.dirX;
                    }
                    this.velX = this.speed * this.dirX;
                }
                if (bestCollision.normalY != 0.0) {
                    if (bestCollision.targetObject instanceof Paddle) {
                        this.dirY = -this.dirY;
                    } else if (!buffs.containsKey(Buff.BuffType.Fire_Ball)) {
                        this.dirY = -this.dirY;
                    }
                    this.velY = this.speed * this.dirY;
                }

                remainingTime -= bestCollision.t;
                if (bestCollision.t < EPS) {
                    if (bestCollision.normalX > 0) this.posX += PUSH_OUT;
                    else if (bestCollision.normalX < 0) this.posX -= PUSH_OUT;
                    if (bestCollision.normalY > 0) this.posY += PUSH_OUT;
                    else if (bestCollision.normalY < 0) this.posY -= PUSH_OUT;
                    remainingTime = 0.0;
                    break;
                }
                if (remainingTime <= EPS) break;
            } else {
                this.posX += moveX;
                this.posY += moveY;
                remainingTime = 0.0;
                break;
            }
        }
        // Update texture for Fire Ball buff
        texture = buffs.containsKey(Buff.BuffType.Fire_Ball) ? fireTexture : normalTexture;
    }

    public Ball cloneAt(double posX, double posY, double velX, double velY) {
        double len = Math.sqrt(velX * velX + velY * velY);
        double newDirX = len > EPS ? velX / len : this.dirX;
        double newDirY = len > EPS ? velY / len : this.dirY;
        double newSpeed = len > EPS ? len : this.speed;
        Ball out = new Ball(posX, posY, this.width, this.height, newSpeed, newDirX, newDirY, screenWidth, screenHeight);
        out.setLaunched(true);
        out.getBuffs().putAll(this.buffs);
        return out;
    }

    public void reset(double posX, double posY) {
        this.posX = posX;
        this.posY = posY;
        this.setVelX(0.0);
        this.setVelY(0.0);
        this.isLaunched = false;
        this.lastCollision = null;
    }

    public void launch(double dirX, double dirY) {
        double len = Math.sqrt(dirX * dirX + dirY * dirY);
        if (len <= EPS) {
            dirX = 0.0; dirY = -1.0; len = 1.0;
        }
        dirX /= len; dirY /= len;
        double s = this.getSpeed();
        this.setVelX(s * dirX);
        this.setVelY(s * dirY);
        this.isLaunched = true;
    }

    public void setVelocity(double velX, double velY) {
        this.velX = velX;
        this.velY = velY;
        double len = Math.sqrt(velX * velX + velY * velY);
        if (len > EPS) {
            this.dirX = velX / len;
            this.dirY = velY / len;
            this.speed = len;
        }
    }

    public void incrementEnlarged() {
        this.width += 10;
        this.height += 10;
        this.damage++;
    }
}
