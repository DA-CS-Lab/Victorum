package io.github.victorum.entity;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Spatial;
import io.github.victorum.world.World;

import java.util.Random;

public class EntityCapybara extends EntityAnimal{
    private static final Random capybaraCutenessRandomizer = new Random();

    private static Spatial createOstrich(AssetManager assetManager){
        Spatial cuteOstrich = assetManager.loadModel("capybara.obj");
        cuteOstrich.setLocalScale(0.25f + capybaraCutenessRandomizer.nextFloat()*.5f);
        return cuteOstrich;
    }

    public EntityCapybara(World world, AssetManager assetManager){
        super(world, createOstrich(assetManager), true);
    }

}
