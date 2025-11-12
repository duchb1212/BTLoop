import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

/**
 * Lớp Ball (Quả bóng) kế thừa từ MovableObject.
 * Nó tự quản lý tốc độ, hướng và xử lý va chạm phức tạp (Swept AABB)
 * thông qua hàm update() của mình.
 */
public class Ball extends MovableObject {
    private BufferedImage texture;
    private BufferedImage normalTexture;
    private BufferedImage fireTexture;
    private double speed;    // Tốc độ cơ bản (ví dụ: 300 pixel/giây)
    private double dirX;     // Hướng X (1 hoặc -1)
    private double dirY;     // Hướng Y (1 hoặc -1)
    public CollisionResult lastCollision = null;
    private HashMap<Buff.BuffType, Double> buffs ;
    private boolean isLaunched = false;

    // Lưu trữ kích thước màn hình để kiểm tra va chạm tường
    private int screenWidth;
    private int screenHeight;

    private static final double EPS = 1e-8;
    private static final double PUSH_OUT = 3.0;

    /**
     * Hàm khởi tạo cho Ball
     */
    public Ball(double posX, double posY, double width, double height, double speed, double dirX, double dirY, int screenWidth, int screenHeight) {
        // Gọi hàm khởi tạo của MovableObject, vận tốc ban đầu sẽ được đặt ngay sau
        super(posX, posY, width, height, 0, 0);

        this.speed = speed;
        this.dirX = dirX;
        this.dirY = dirY;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        // Thiết lập vận tốc (velX, velY) ban đầu dựa trên tốc độ và hướng
        this.velX = speed * dirX;
        this.velY = speed * dirY;
        this.buffs = new HashMap<Buff.BuffType, Double>();

        try {
            normalTexture = ImageIO.read(getClass().getResource("/textures/Ball.png"));
            fireTexture = ImageIO.read(getClass().getResource("/textures/FireBall.png"));
            texture = normalTexture; // mặc định là bóng thường
        } catch (IOException e) {
            e.printStackTrace();
            texture = null;
        }

    }

    // --- Các hàm Getters / Setters (Bạn có thể thêm nếu cần) ---
    public double getSpeed() { return speed; }
    public void setSpeed(double newSpeed) {
        this.speed = newSpeed;
        // Cập nhật lại vận tốc ngay lập tức khi đổi tốc độ
        this.velX = this.speed * this.dirX;
        this.velY = this.speed * this.dirY;
    }

    public HashMap<Buff.BuffType, Double> getBuffs() {
        return buffs;
    }

    public void setBuffs(HashMap<Buff.BuffType, Double> powerUps) {
        this.buffs = buffs;
    }

    public boolean isLaunched() {
        return isLaunched;
    }

    public void setLaunched(boolean launched) {
        isLaunched = launched;
    }

    public BufferedImage getTexture() {
        return texture;
    }

    /**
     * Kiểm tra va chạm với 4 bức tường.
     * @return true nếu bóng chạm tường dưới (mất mạng), false nếu không.
     */
    public boolean checkWallCollision() {
        // Tường trái
        if (posX <= 0) {
            posX = 0; // Đặt lại vị trí
            dirX = 1; // Đổi hướng
            velX = speed * dirX; // Cập nhật vận tốc
        }
        // Tường phải
        if (posX + width > screenWidth) {
            posX = screenWidth - width; // Đặt lại vị trí
            dirX = -1; // Đổi hướng
            velX = speed * dirX; // Cập nhật vận tốc
        }
        // Tường trên
        if (posY <= 0) {
            posY = 0; // Đặt lại vị trí
            dirY = 1; // Đổi hướng
            velY = speed * dirY; // Cập nhật vận tốc
        }

        // Tường dưới (mất mạng)
        if (posY + height > screenHeight) {
            return true; // Báo cho vòng lặp game biết bóng đã rơi
        }

        return false; // Bóng vẫn trong cuộc
    }

    /**
     * Hàm UPDATE chính của Bóng - Sử dụng Swept AABB để va chạm chính xác.
     * @param deltaTime Thời gian (giây) trôi qua kể từ khung hình trước.
     * @param allObjects Danh sách TẤT CẢ các vật thể khác (Gạch, Paddle) để kiểm tra va chạm.
     */
    @Override
    public void update(double deltaTime, ArrayList<GameObject> allObjects) {

        // remainingTime: phần tỷ lệ của frame còn lại để xử lý (0..1)
        double remainingTime = 1.0;

        // Giới hạn số lần lặp xử lý va chạm trong 1 frame để tránh vòng lặp vô hạn.
        int maxIterations = 5;

        if (this.buffs.containsKey(Buff.BuffType.Enlarged_Ball)) {
            this.width = 30.0;
            this.height = 30.0;
        } else {
            this.width = 15.0;
            this.height = 15.0;
        }

        for (int iter = 0; iter < maxIterations && remainingTime > EPS; iter++) {
            double moveX = this.velX * deltaTime * remainingTime;
            double moveY = this.velY * deltaTime * remainingTime;

            CollisionResult bestCollision = new CollisionResult();

            for (GameObject other : allObjects) {
                if (other instanceof Ball) continue;
                if (other instanceof Buff) continue;
                if (other instanceof Brick) {
                    if (((Brick)other).isDestroyed()) continue;
                    if (this.getPowerUps().containsKey(PowerUpBall.PowerUpType.Fire_Ball)) {
                        continue;
                    }
                } 

                CollisionResult currentCollision = CollisionUtils.sweptAABB(this, other, moveX, moveY);
                if (currentCollision != null && currentCollision.t < bestCollision.t) {
                    bestCollision = currentCollision;
                }
            }

            if (bestCollision.t < 1.0) {
                this.lastCollision = bestCollision;

                double t = Math.max(bestCollision.t, 0.0);

                this.posX += moveX * t;
                this.posY += moveY * t;

                if (bestCollision.normalX != 0.0) {
                    if (bestCollision.targetObject instanceof Paddle) {
                        this.dirX = -this.dirX;
                    } else {
                        if (!this.buffs.containsKey(Buff.BuffType.Fire_Ball)) {
                            this.dirX = -this.dirX;
                        }
                    }
                    this.velX = this.speed * this.dirX;
                }
                if (bestCollision.normalY != 0.0) {
                    if (bestCollision.targetObject instanceof Paddle) {
                        this.dirY = -this.dirY;
                    } else {
                        if (!this.buffs.containsKey(Buff.BuffType.Fire_Ball)) {
                            this.dirY = -this.dirY;
                        }
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

        // --- Cập nhật texture theo buff ---
        if (buffs.containsKey(Buff.BuffType.Fire_Ball)) {
            texture = fireTexture;
        } else {
            texture = normalTexture;
        }

    }

    public Ball cloneAt(double posX, double posY, double velX, double velY) {
        double len = Math.sqrt(velX * velX + velY * velY);
        double newDirX,  newDirY, newSpeed;

        if (len > 1e-6) {
            newDirX = velX / len;
            newDirY = velY / len;
            newSpeed = len;
        } else {
            newDirY = this.dirY;
            newDirX = this.dirX;
            newSpeed = this.speed;
        }

        Ball out = new Ball(posX, posY, this.width, this.height, newSpeed, newDirX, newDirY, screenWidth, screenHeight);
        out.setLaunched(true);
        out.getBuffs().putAll(this.buffs);
        return out;
    }

    /**
     * Đặt lại vị trí và hướng của bóng (ví dụ: khi bắt đầu màn mới hoặc mất mạng).
     */
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
        if (len <= 1e-6) {
            dirX = 0.0;
            dirY = -1.0;
            len = 1.0;
        }
        dirX /= len;
        dirY /= len;
        double s = this.getSpeed();
        this.setVelX(s * dirX);
        this.setVelY(s * dirY);
        this.isLaunched = true;    
    }

    public void setVelocity(double velX, double velY) {
        this.velX = velX;
        this.velY = velY;
        double len = Math.sqrt(velX * velX + velY * velY);
        if (len > 1e-6) {
            this.dirX = velX / len;
            this.dirY = velY / len;
            this.speed = len;
        }
    }
}
