public class UnbreakableBrickFactory extends BrickFactory {
    @Override
    public Brick createBrick(double posX, double posY, double width, double height, int screenWidth, int screenHeight) {
        return new UnbreakableBrick(posX, posY, width, height, screenWidth, screenHeight, Integer.MAX_VALUE);
    }
}
