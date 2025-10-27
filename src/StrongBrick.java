public class StrongBrick extends Brick{
    public StrongBrick(double x, double y, int width, int height) {
        super(x, y, width, height, 3, "STRONG");
    }
    public StrongBrick(int posX, int posY, int width, int height, int hitPoint) {
        super(posX,posY,width,height,hitPoint, "STRONG");
    }
}
