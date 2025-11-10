class CollisionResult {
    /**
     * Thời điểm va chạm (0.0 .. 1.0). 1.0 = không va chạm trong khung hình này.
     */
    public double t = 1.0;

    /** Pháp tuyến X của bề mặt bị va chạm (-1, 0, 1) */
    public double normalX = 0.0;

    /** Pháp tuyến Y của bề mặt bị va chạm (-1, 0, 1) */
    public double normalY = 0.0;

    /** Vật thể mà chúng ta va chạm vào */
    public GameObject targetObject = null;

    /** Tọa độ điểm chạm ước tính (tính được khi cần) */
    public double contactX = Double.NaN;
    public double contactY = Double.NaN;

    public boolean hasCollision() {
        return t >= 0.0 && t < 1.0;
    }
}
public class CollisionUtils {



    private static final double EPS = 1e-9;



    private static boolean aabbOverlap(GameObject a, GameObject b) {

        return a.getPosX() < b.getPosX() + b.getWidth()

                && a.getPosX() + a.getWidth() > b.getPosX()

                && a.getPosY() < b.getPosY() + b.getHeight()

                && a.getPosY() + a.getHeight() > b.getPosY();

    }



    /**

     * Swept AABB continuous collision detection.

     */

    public static CollisionResult sweptAABB(GameObject movingObject, GameObject stationaryObject, double deltaX, double deltaY) {

        CollisionResult result = new CollisionResult();

        result.t = 1.0;



        if (movingObject == null || stationaryObject == null) {

            return result;

        }



        // If already overlapping, return immediate collision (t=0).

        if (aabbOverlap(movingObject, stationaryObject)) {

            result.t = 0.0;

            result.targetObject = stationaryObject;

            // Option: compute minimal push-out normal here.

            // For now we leave normals zero or you can set based on penetration direction.

            return result;

        }



        double xInvEntry, yInvEntry;

        double xInvExit, yInvExit;



        if (deltaX > 0.0) {

            xInvEntry = stationaryObject.getPosX() - (movingObject.getPosX() + movingObject.getWidth());

            xInvExit = (stationaryObject.getPosX() + stationaryObject.getWidth()) - movingObject.getPosX();

        } else {

            xInvEntry = (stationaryObject.getPosX() + stationaryObject.getWidth()) - movingObject.getPosX();

            xInvExit = stationaryObject.getPosX() - (movingObject.getPosX() + movingObject.getWidth());

        }



        if (deltaY > 0.0) {

            yInvEntry = stationaryObject.getPosY() - (movingObject.getPosY() + movingObject.getHeight());

            yInvExit = (stationaryObject.getPosY() + stationaryObject.getHeight()) - movingObject.getPosY();

        } else {

            yInvEntry = (stationaryObject.getPosY() + stationaryObject.getHeight()) - movingObject.getPosY();

            yInvExit = stationaryObject.getPosY() - (movingObject.getPosY() + movingObject.getHeight());

        }



        double xEntryTime, yEntryTime;

        double xExitTime, yExitTime;



        if (Math.abs(deltaX) < EPS) {

            xEntryTime = Double.NEGATIVE_INFINITY;

            xExitTime = Double.POSITIVE_INFINITY;

        } else {

            xEntryTime = xInvEntry / deltaX;

            xExitTime = xInvExit / deltaX;

        }



        if (Math.abs(deltaY) < EPS) {

            yEntryTime = Double.NEGATIVE_INFINITY;

            yExitTime = Double.POSITIVE_INFINITY;

        } else {

            yEntryTime = yInvEntry / deltaY;

            yExitTime = yInvExit / deltaY;

        }



        double entryTime = Math.max(xEntryTime, yEntryTime);

        double exitTime = Math.min(xExitTime, yExitTime);



        if (entryTime > exitTime || entryTime > 1.0 + EPS || entryTime < -EPS) {

            return result; // no collision

        }



        // There is a collision

        result.t = Math.max(0.0, Math.min(1.0, entryTime)); // clamp into [0,1]

        result.targetObject = stationaryObject;



        // Reset normals explicitly

        result.normalX = 0.0;

        result.normalY = 0.0;



        // If simultaneous (corner) collision, you may want to set both normals.

        if (Math.abs(xEntryTime - yEntryTime) <= EPS) {

            // Corner collision: set both normals according to movement direction.

            if (deltaX > 0) result.normalX = -1.0; else if (deltaX < 0) result.normalX = 1.0;

            if (deltaY > 0) result.normalY = -1.0; else if (deltaY < 0) result.normalY = 1.0;

        } else if (xEntryTime > yEntryTime) {

            // collision on X axis

            if (deltaX > 0) result.normalX = -1.0; else result.normalX = 1.0;

        } else {

            // collision on Y axis

            if (deltaY > 0) result.normalY = -1.0; else result.normalY = 1.0;

        }



        // Optionally compute contact point (approximate)

        double contactPosX = movingObject.getPosX() + deltaX * result.t;

        double contactPosY = movingObject.getPosY() + deltaY * result.t;

        result.contactX = contactPosX;

        result.contactY = contactPosY;



        return result;

    }

}