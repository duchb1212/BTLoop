public class Ball extends MovableObject {
    private double speed;
    private double dirX;
    private double dirY;
    private int screenWidth;
    private int screenHeight;

    public Ball(int posX, int posY, int width, int height, double speed, double dirX, double dirY, int screenWidth, int screenHeight) {
        super(posX, posY, width, height, 0, 0);
        this.speed = speed;
        this.dirX = dirX;
        this.dirY = dirY;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        this.velX = speed * dirX;
        this.velY = speed * dirY;
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

    public boolean CheckCollision(GameObject object) {
        return this.Collision(object);
    }

    public void Bounce(GameObject object) {
        if (!CheckCollision(object)) {
            return;
        }
        double ballCenterX = posX + width / 2.0;
        double ballCenterY = posY + height / 2.0;

        double objCenterX = object.getPosX() + object.getWidth() / 2.0;
        double objCenterY = object.getPosY() + object.getHeight() / 2.0;

        double delX = ballCenterX - objCenterX;
        double delY = ballCenterY - objCenterY;

        double overLapX = (width / 2.0 + object.getWidth() / 2.0) - Math.abs(delX);
        double oveLapY = (height / 2.0 + object.getHeight() / 2.0) - Math.abs(delY);

        if (overLapX < oveLapY) {
            dirX = -dirX;
            velX = speed * dirX;
            if (delX > 0) {
                posX = object.getPosX() + object.getWidth();
            } else {
                posX = object.getPosX() - object.getWidth();
            }
        } else {
            dirY = -dirY;
            velY = speed * dirY;
            if (delY > 0) {
                posY = object.getPosY() + object.getHeight();
            } else {
                posY = object.getPosY() - object.getHeight();
            }
        }
    }

    public boolean CheckWallCollision() {
        if (posX <= 0) {
            posX = 0;
            dirX = -dirX;
            velX = speed * dirX;
        }
        if (posX + width > screenWidth) {
            posX = screenWidth - width;
            dirX = -dirX;
            velX = speed * dirX;
        }
        if (posY <= 0) {
            posY = 0;
            dirY = -dirY;
            velY = speed * dirY;
        }
        if (posY + height > screenHeight) {
            return true;
        }
        return false;
    }

    @Override
    public void update() {
        move();
        CheckWallCollision();
    }

    public void Reset(double posX, double posY) {
        this.posX = posX;
        this.posY = posY;
        this.dirX = -1;
        this.dirY = -1;
        this.velX = speed * dirX;
        this.velY = speed * dirY;
    }

}
