package io.github.victorum;

import com.jme3.app.SimpleApplication;
import com.jme3.math.ColorRGBA;

import com.jme3.math.Vector3f;
import io.github.victorum.player.PlayerAppState;
import io.github.victorum.world.WorldAppState;

public class Victorum extends SimpleApplication{
    private final WorldAppState worldAppState = new WorldAppState();
    private final PlayerAppState playerAppState = new PlayerAppState();

    public static void main(String[] args){
        Victorum victorum = new Victorum();
        victorum.start();
    }

    @Override
    public void simpleInitApp(){
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        stateManager.attach(worldAppState);
        stateManager.attach(playerAppState);
        //flyCam.setDragToRotate(true);
        flyCam.setMoveSpeed(16f);
        cam.setLocation(new Vector3f(0, 145, 0));
        viewPort.setBackgroundColor(ColorRGBA.Blue);
    }

    @Override
    public void simpleUpdate(float tpf){

    }

    @Override
    public void stop(){
        super.stop();
        System.exit(0); //necessary because of background threads in io.github.victorum.util.ThreadingUtil
    }

    public WorldAppState getWorldAppState(){
        return worldAppState;
    }

}
