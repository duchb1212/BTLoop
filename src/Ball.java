import java.util.ArrayList;

/**
 * Lớp Ball (Quả bóng) kế thừa từ MovableObject.
 * Nó tự quản lý tốc độ, hướng và xử lý va chạm phức tạp (Swept AABB)
 * thông qua hàm update() của mình.
 */
public class Ball extends MovableObject {

    private double speed;    // Tốc độ cơ bản (ví dụ: 300 pixel/giây)
    private double dirX;     // Hướng X (1 hoặc -1)
    private double dirY;     // Hướng Y (1 hoặc -1)
    public CollisionResult lastCollision = null;

    // Lưu trữ kích thước màn hình để kiểm tra va chạm tường
    private int screenWidth;
    private int screenHeight;

    private static final double EPS = 1e-8;

    /**
     * Hàm khởi tạo cho Ball.
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
    }

    // --- Các hàm Getters / Setters (Bạn có thể thêm nếu cần) ---
    public double getSpeed() { return speed; }
    public void setSpeed(double newSpeed) {
        this.speed = newSpeed;
        // Cập nhật lại vận tốc ngay lập tức khi đổi tốc độ
        this.velX = this.speed * this.dirX;
        this.velY = this.speed * this.dirY;
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

        for (int iter = 0; iter < maxIterations && remainingTime > EPS; iter++) {

            // Tính quãng đường sẽ di chuyển trong phần còn lại của frame
            double moveX = this.velX * deltaTime * remainingTime;
            double moveY = this.velY * deltaTime * remainingTime;

            // Tìm va chạm sớm nhất trong phần move (theo t: 0..1 relative to this move)
            CollisionResult bestCollision = new CollisionResult(); // t default = 1.0 (no collision)

            for (GameObject other : allObjects) {
                if (other == this) continue; // Bỏ qua, không tự va chạm với mình

                // Bỏ qua vật thể vô hình/hỏng nếu cần (tùy codebase)
                if (other instanceof BrickFactory && ((BrickFactory)other).isDestroyed()) continue;

                // Gọi Swept AABB với quãng đường hiện tại (moveX, moveY)
                CollisionResult currentCollision = CollisionUtils.sweptAABB(this, other, moveX, moveY);

                // Chọn va chạm có t nhỏ nhất
                if (currentCollision != null && currentCollision.t < bestCollision.t) {
                    bestCollision = currentCollision;
                }
            }

            if (bestCollision.t < 1.0) {
                this.lastCollision = bestCollision;
                // Nếu t quá nhỏ (gần 0), advance một epsilon để tránh stuck
                double t = bestCollision.t;
                if (t < EPS) {
                    t = 0.0;
                }

                // Di chuyển bóng tới điểm va chạm (theo t của moveX/moveY)
                this.posX += moveX * t;
                this.posY += moveY * t;

                // Xử lý nảy bóng dựa trên pháp tuyến (normal)
                if (bestCollision.normalX != 0.0) { // Va chạm ngang
                    this.dirX = -this.dirX;
                    this.velX = this.speed * this.dirX;
                }
                if (bestCollision.normalY != 0.0) { // Va chạm dọc
                    this.dirY = -this.dirY;
                    this.velY = this.speed * this.dirY;
                }

                // Xử lý logic game (ví dụ: phá gạch)
                // if (bestCollision.targetObject instanceof Brick) {
                //     ((Brick) bestCollision.targetObject).destroy();
                // }
                if (bestCollision.targetObject instanceof BrickFactory) {
                    BrickFactory brickFactory = (BrickFactory) bestCollision.targetObject;
                    brickFactory.takeDamage(1);
                }

                // Cập nhật remainingTime: giảm theo t đã sử dụng
                // Lưu ý: t là tỷ lệ trên moveX/moveY hiện tại, nên trừ trực tiếp
                remainingTime -= bestCollision.t;

                // Nếu bestCollision.t == 0 (hoặc rất nhỏ), tránh lặp vô hạn:
                if (bestCollision.t <= EPS) {
                    // Đẩy nhẹ quả bóng ra khỏi va chạm theo normal (simple push-out)
                    if (bestCollision.normalX > 0) this.posX += EPS;
                    else if (bestCollision.normalX < 0) this.posX -= EPS;
                    if (bestCollision.normalY > 0) this.posY += EPS;
                    else if (bestCollision.normalY < 0) this.posY -= EPS;

                    // Đồng thời giảm remainingTime một lượng nhỏ để tiến triển
                    remainingTime -= EPS;
                }

                // Clamp remainingTime
                if (remainingTime <= EPS) {
                    break;
                }

                // Sau khi phản xạ, vòng lặp sẽ tiếp tục với remainingTime đã cập nhật.
                // Lưu ý: ở đầu vòng tiếp theo, moveX/moveY sẽ được tính lại theo velX/velY mới.
            } else {
                // Không có va chạm -> di chuyển nốt phần còn lại và thoát
                this.posX += moveX;
                this.posY += moveY;
                remainingTime = 0.0;
                break;
            }
        } // end for iterations

        // Sau khi xử lý va chạm với các object, kiểm tra va chạm tường
    }

    /**
     * Đặt lại vị trí và hướng của bóng (ví dụ: khi bắt đầu màn mới hoặc mất mạng).
     */
    public void reset(double posX, double posY) {
        this.posX = posX;
        this.posY = posY;

        // Đặt hướng ngẫu nhiên (hoặc cố định)
        this.dirX = 1;
        this.dirY = -1; // Bay lên trên

        // Cập nhật lại vận tốc
        this.velX = speed * dirX;
        this.velY = speed * dirY;
    }
}