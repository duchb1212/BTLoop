public class IceBrickFactory extends BrickFactory {
    @Override
    public Brick createBrick(double posX, double posY, double width, double height, int screenWidth, int screenHeight) {
        return new IceBrick(posX, posY, width, height, screenWidth, screenHeight, 1);
    }
}
