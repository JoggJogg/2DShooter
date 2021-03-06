import nl.han.ica.oopg.collision.ICollidableWithGameObjects;
import nl.han.ica.oopg.objects.AnimatedSpriteObject;
import nl.han.ica.oopg.objects.GameObject;
import nl.han.ica.oopg.objects.Sprite;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class Player extends AnimatedSpriteObject implements ICollidableWithGameObjects {
    private final ShooterApp world;

    private float[] previousLocation = new float[2]; // [x, y]
    private int[] facingDirection = new int[2]; // [x, y]
    private int currentFrame;
    private final ArrayList<Key> keys = new ArrayList<>();
    private ArrayList<Key> keysPressed = new ArrayList<>();
    private Weapon currentWeapon;

    private final int walkingSpeed;

    /** Maakt een player aan
     * @param world huidige wereld
     */
    public Player(ShooterApp world) {
        super(new Sprite("media/sprite_human.png"), 16);
        this.world = world;
        setFriction(0);

        walkingSpeed = 4;
        currentFrame = 0;
        facingDirection[0] = 1;
        facingDirection[1] = 0;

        currentWeapon = new Rock(world, this);
        world.addGameObject(currentWeapon);

        // Gebruikte toetsen
        keys.add(new Key('w'));
        keys.add(new Key('a'));
        keys.add(new Key('s'));
        keys.add(new Key('d'));
        keys.add(new Key(' '));
    }

    @Override
    public void update() {
        handleWorldBoundaries();
      
        for (Key key : keys) {
            if (key.isPressed() && !keysPressed.contains(key)) {
                keysPressed.add(key);
            } else if (!key.isPressed() && keysPressed.contains(key)) {
                keysPressed.remove(key);
            }
        }
        // update frames van player sprite
        setCurrentFrameIndex(currentFrame);

        if (isWalking()) {
            updateDirection();
            if (ShooterApp.dist(previousLocation[0], previousLocation[1], getX(), getY()) > walkingSpeed * 2) {
                if (facingDirection[0] == -1) {
                    loopFramesLeft();
                } else {
                    loopFramesRight();
                }
                previousLocation[0] = getX();
                previousLocation[1] = getY();
            }
            movePlayer();
        } else {
            if (facingDirection[0] == -1) {
                currentFrame = 15;
            } else {
                currentFrame = 0;
            }
            stopPlayer();
        }

        if (isShooting()) {
            currentWeapon.fire();
        } else if (!isShooting() && !currentWeapon.getAutoFire()) {
            currentWeapon.setCanFire(true);
        }
    }

    @Override
    public void keyPressed(int keyCode, char key) {
        for (Key keyp: keys) {
            if (key == keyp.getKeyCode()) {
                keyp.press();
            }
        }
    }

    public void keyReleased(int keyCode, char key) {
        for (Key keyr: keys) {
            if (key == keyr.getKeyCode()) {
                keyr.release();
            }
        }
    }

    /**
     * Update constant de richting waar de player heen loopt
     */
    private void updateDirection() {
        facingDirection[0] = 0;
        facingDirection[1] = 0;
        for (Key key: keysPressed) {
            if (key.getKeyCode() == 'a') {
                facingDirection[0] = -1;
            }
            if (key.getKeyCode() == 'w') {
                facingDirection[1] = -1;
            }
            if (key.getKeyCode() == 's') {
                facingDirection[1] = 1;
            }
            if (key.getKeyCode() == 'd') {
                facingDirection[0] = 1;
            }
        }
    }

    /**
     * controleert of de speler buiten de wereldgrenzen komt en zet dan de x- of y-speed op 0
     */
    private void handleWorldBoundaries () {
        int[] worldBoundaries = world.getWorldBoundaries();

        if (getX() <= worldBoundaries[0]) {
            setxSpeed(0);
            setX(worldBoundaries[0]);
        }
        if (getY() <= worldBoundaries[1]) {
            setySpeed(0);
            setY(worldBoundaries[1]);
        }
        if (getX() >= worldBoundaries[2] - getWidth()) {
            setxSpeed(0);
            setX(worldBoundaries[2] - getWidth());
        }
        if (getY() >= worldBoundaries[3] - getHeight()) {
            setySpeed(0);
            setY(worldBoundaries[3] - getHeight());
        }
    }

    /**
     * zet de snelheid van de speler gebaseerd op de richting waar de speler heen loopt
     * walkingSpeed is van te voren gedefineerd
     */
    private void movePlayer() {
        setxSpeed(facingDirection[0] * walkingSpeed);
        setySpeed(facingDirection[1] * walkingSpeed);
    }

    /**
     * zet de speler stil
     */
    private void stopPlayer() {
        setxSpeed(0);
        setySpeed(0);
    }

    /**
     * @return of de speler loopt (wasd ingedrukt)
     */
    public boolean isWalking() {
        for (Key key: keysPressed) {
            if (key.getKeyCode() == 'a' || key.getKeyCode() == 'w' || key.getKeyCode() == 's' || key.getKeyCode() == 'd') {
                return true;
            }
        }
        return false;
    }

    /**
     * @return of de speler aan het schieten is (spatie ingedrukt)
     */
    public boolean isShooting() {
        for (Key key: keysPressed) {
            if (key.getKeyCode() == ' ') {
                return true;
            }
        }
        return false;
    }

    /**
     * Loopt de frames wanneer speler naar rechts loopt
     */
    private void loopFramesRight() {
        if(currentFrame >= 7) {
            currentFrame = 0;
        } else {
            currentFrame++;
        }
    }

    /**
     * Loopt de frames wanneer de speler naar links loopt
     */
    private void loopFramesLeft() {
        if(currentFrame < 8) {
            currentFrame = 8;
        }
        if(currentFrame == 15 ) {
            currentFrame = 8;
        } else {
            currentFrame ++;
        }
    }

    /**
     * @return de huidige richting waar de speler heen loopt
     */
    public int[] getFacingDirection() {
        return facingDirection;
    }

    /** Update huidige wapen van de speler
     * @param weapon wapen
     */
    public void setWeapon(WeaponType weapon) {
        switch(weapon) {
            case ROCK:
                world.deleteGameObject(currentWeapon);
                currentWeapon = new Rock(world, this);
                world.addGameObject(currentWeapon);
                break;
            case PISTOL:
                world.deleteGameObject(currentWeapon);
                currentWeapon = new Pistol(world, this);
                world.addGameObject(currentWeapon);
                break;
            case SHOTGUN:
                world.deleteGameObject(currentWeapon);
                currentWeapon = new Shotgun(world, this);
                world.addGameObject(currentWeapon);
                break;
            case AUTOSHOTGUN:
                world.deleteGameObject(currentWeapon);
                currentWeapon = new AutoShotgun(world, this);
                world.addGameObject(currentWeapon);
                break;
        }
    }

    @Override
    public void gameObjectCollisionOccurred(List<GameObject> collidedGameObjects) {
        for (GameObject g : collidedGameObjects) {
            if (g instanceof Enemy) {
                // Enemy hit player
            }
        }
    }
}

