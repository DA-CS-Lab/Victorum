package io.github.victorum.entity;

import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

import io.github.victorum.inventory.block.BlockRegistry;
import io.github.victorum.inventory.block.BlockType;
import io.github.victorum.world.BlockCoordinates;
import io.github.victorum.world.ChunkCoordinates;
import io.github.victorum.world.World;

public abstract class Entity{
    private boolean isRemoved = false;
    private World world;
    private Spatial spatial;
    private Vector3f[] collisionVectors;
    private Vector3f forwardDirection = new Vector3f();
    private Vector3f leftDirection = new Vector3f();
    private boolean forward, backwards, left, right, isOnGround;
    private Vector3f velocity, airAcceleration;

    public Entity(World world, Spatial spatial, Vector3f... collisionVectors){
        this.world = world;
        this.spatial = spatial;
        this.collisionVectors = collisionVectors;
        velocity = new Vector3f();
        airAcceleration = new Vector3f();
        forward = backwards = left = right = isOnGround = false;
    }

    protected void updatePhysics(float tpf){
        Vector3f netDirection = new Vector3f(0, 0, 0);

        if(forward){
            netDirection.addLocal(forwardDirection);
        }

        if(backwards){
            netDirection.addLocal(forwardDirection.negate());
        }

        if(left){
            netDirection.addLocal(leftDirection);
        }

        if(right){
            netDirection.addLocal(leftDirection.negate());
        }

        netDirection.setY(0);
        netDirection.normalizeLocal();

        if(isOnGround){
            velocity.set(netDirection.mult(3));
            if(velocity.length() != 0){
                isOnGround = applyVelocitySeparately(velocity, tpf).isHitY();
            }
        }else{
            airAcceleration.set(0, -9.8f, 0);
            velocity.addLocal(airAcceleration.multLocal(tpf));
            isOnGround = applyVelocitySeparately(velocity, tpf).isHitY();
        }

        Vector3f location = spatial.getLocalTranslation();
        BlockCoordinates coordinates = new BlockCoordinates(location.getX(), location.getZ());
        ChunkCoordinates chunkCoordinates = new ChunkCoordinates(coordinates.getChunkX(), coordinates.getChunkZ());
        if(world.getChunkIfExists(chunkCoordinates) == null){
            remove();
            System.out.println("Removing entity");
        }
    }

    private HitData applyVelocitySeparately(Vector3f velocity, float tpf){
        HitData hitData = new HitData(false, false, false);
        float x = velocity.getX();
        float y = velocity.getY();
        float z = velocity.getZ();

        velocity.set(0, 0, 0);

        velocity.setX(x);
        if(applyVelocity(velocity, tpf)) hitData.hitX = true;
        velocity.setX(0);

        velocity.setY(y);
        if(applyVelocity(velocity, tpf)) hitData.hitY = true;
        velocity.setY(0);

        velocity.setZ(z);
        if(applyVelocity(velocity, tpf)) hitData.hitZ = true;
        velocity.setZ(0);

        velocity.set(x, y, z);

        if(hitData.hitX || hitData.hitZ) onCollision();

        return hitData;
    }

    private boolean applyVelocity(Vector3f velocity, float tpf){
        velocity = velocity.mult(tpf);
        int stepCount = (int)Math.ceil(velocity.length())*5;
        Vector3f step = velocity.divide(stepCount);
        Vector3f location = spatial.getLocalTranslation();
        boolean isCollided = false;

        for(int i=0;i<stepCount;++i){
            location.addLocal(step);

            for(Vector3f collisionVector : collisionVectors){
                Vector3f locationPrime = location.add(collisionVector);
                if(world.getBlockTypeAt(locationPrime.getX(), locationPrime.getY(), locationPrime.getZ()).isSolid()){
                    location.subtractLocal(step);
                    isCollided = true;
                    break;
                }
            }
        }

        spatial.setLocalTranslation(location);

        return isCollided;
    }

    public void jump(){
        if(isOnGround || isUnderwater()){
            isOnGround = false;
            velocity.set(velocity.x, 5f, velocity.z);
        }
    }

    public boolean isUnderwater(){
        int x = (int)getSpatial().getLocalTranslation().getX();
        int y = (int)getSpatial().getLocalTranslation().getY();
        int z = (int)getSpatial().getLocalTranslation().getZ();
        y+=2;
        return world.getBlockTypeAt(x, y, z).getBlockId() == BlockRegistry.BLOCK_TYPE_WATER.getBlockId();
    }

    public abstract void onCollision();

    public abstract void update(float tpf);

    public Vector3f getForwardDirection() {
        return forwardDirection;
    }

    public void setForwardDirection(Vector3f forwardDirection) {
        this.forwardDirection = forwardDirection;
    }

    public Vector3f getLeftDirection() {
        return leftDirection;
    }

    public void setLeftDirection(Vector3f leftDirection) {
        this.leftDirection = leftDirection;
    }

    public boolean isForward() {
        return forward;
    }

    public void setForward(boolean forward) {
        this.forward = forward;
    }

    public boolean isBackwards() {
        return backwards;
    }

    public void setBackwards(boolean backwards) {
        this.backwards = backwards;
    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public final Spatial getSpatial(){
        return spatial;
    }

    public World getWorld(){
        return world;
    }

    protected void remove(){
        isRemoved = true;
    }

    protected boolean isRemoved(){
        return isRemoved;
    }

    private static final class HitData{
        protected boolean hitX;
        protected boolean hitY;
        protected boolean hitZ;

        public HitData(boolean hitX, boolean hitY, boolean hitZ){
            this.hitX = hitX;
            this.hitY = hitY;
            this.hitZ = hitZ;
        }

        public boolean isHitX() {
            return hitX;
        }

        public boolean isHitY() {
            return hitY;
        }

        public boolean isHitZ() {
            return hitZ;
        }
    }



}
