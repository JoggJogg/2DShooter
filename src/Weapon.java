import nl.han.ica.oopg.alarm.Alarm;
import nl.han.ica.oopg.alarm.IAlarmListener;
import nl.han.ica.oopg.objects.AnimatedSpriteObject;
import nl.han.ica.oopg.objects.Sprite;
import nl.han.ica.oopg.sound.Sound;

public abstract class Weapon extends AnimatedSpriteObject implements IAlarmListener {
    protected ShooterApp world;

    // Player owner is de speler die het wapen 'vast' heeft
    // Het wapen kan een projectiel afvuren wat gespawned wordt op de huidige locatie van de speler
    private Player owner;
    protected String particlefn;
    protected Sound weaponSound;

    protected int[] firingDirection = new int[2]; // [x, y]
    protected boolean canFire = true;
    protected boolean autoFire;
    protected double fireDelay;
    protected int magSize;
    protected int damage;
    protected boolean shootingDelayPassed = true;

    protected int particleSpeed;
    protected float particleSpawnLocationX;
    protected float particleSpawnLocationY;
    private float weaponSpawnLocationX;
    private float weaponSpawnLocationY;
    private float weaponZ;


    protected float particleOffsetX;
    protected float particleOffsetY;
    protected float weaponOffsetX;
    protected float weaponOffsetY;

    private int currentFrame;

    /** Maakt nieuw wapen aan zonder sprite
     * @param world huidige wereld
     * @param owner eigenaar wapen
     */
    public Weapon(ShooterApp world, Player owner) {
        super(new Sprite("media/empty.png"), 2);
        this.world = world;
        this.owner = owner;
    }

    /** Maakt nieuw wapen aan mét sprite
     * @param world huidige wereld
     * @param owner eigenaar wapen
     * @param weaponfn filename wapensprite
     */
    public Weapon(ShooterApp world, Player owner, String weaponfn) {
        super(new Sprite(weaponfn), 2);
        this.world = world;
        this.owner = owner;
    }

    @Override
    public void update() {
        updateFiringDirection();
        updateWeaponPosition();

        setCurrentFrameIndex(currentFrame);
    }

    /**
     * update constant de positie van het wapen gebaseerd op de positie van de speler
     */
    private void updateWeaponPosition() {

        // wapen naar links
        if(firingDirection[0] == -1) {
            currentFrame = 1;
            weaponZ = owner.getZ() -1;
            particleSpawnLocationX = owner.getX() - particleOffsetX/4;
            particleSpawnLocationY = owner.getY() + particleOffsetY;
            weaponSpawnLocationX = owner.getX() - weaponOffsetX/4;
            weaponSpawnLocationY = owner.getY() + weaponOffsetY;

        }

        // wapen naar rechts of andere richtingen
        else  {
            currentFrame = 0;
            weaponZ = owner.getZ() +1;
            particleSpawnLocationX = owner.getX() + particleOffsetX;
            particleSpawnLocationY = owner.getY() + particleOffsetY;
            weaponSpawnLocationX = owner.getX() + weaponOffsetX;
            weaponSpawnLocationY = owner.getY() + weaponOffsetY;
        }

        setX(weaponSpawnLocationX);
        setY(weaponSpawnLocationY);
        setZ(weaponZ);
    }

    /**
     * update constant de schietrichting van het wapen gebaseerd op de looprichting van de speler.
     */
    public void updateFiringDirection() {
        if (!owner.isWalking()) {
            if (owner.getFacingDirection()[0] == -1) {
                // Speler kijkt naar links
                firingDirection[0] = -1;
                firingDirection[1] = 0;
            } else {
                // Speler kijkt niet naar links, dus schiet naar rechts
                firingDirection[0] = 1;
                firingDirection[1] = 0;
            }
        } else {
            firingDirection[0] = owner.getFacingDirection()[0];
            firingDirection[1] = owner.getFacingDirection()[1];
        }
    }

    /**
     * Standaard fire()-functie: moet overschreven worden bij het afvuren van meerdere Particles.
     */
    public void fire() {
        if (autoFire && canFire) {
            world.addGameObject(new Particle(world, this, particlefn, particleSpawnLocationX, particleSpawnLocationY, firingDirection, particleSpeed, particleSpeed));
            addParticleAlarm();
            weaponSound.rewind();
            weaponSound.play();
            canFire = false;
        } else if (!autoFire && canFire && shootingDelayPassed) {
            world.addGameObject(new Particle(world, this, particlefn, particleSpawnLocationX, particleSpawnLocationY, firingDirection, particleSpeed, particleSpeed));
            addParticleAlarm();
            weaponSound.rewind();
            weaponSound.play();
            canFire = false;
            shootingDelayPassed = false;
        }

    }

    public void addParticleAlarm() {
        Alarm nextParticle = new Alarm("Next particle", fireDelay);
        nextParticle.addTarget(this);
        nextParticle.start();
    }

    @Override
    public void triggerAlarm(String s) {
        shootingDelayPassed = true;
        if (autoFire) {
            canFire = true;
        }
    }

    /** stel in of wapen momenteel kan vuren
     * @param val true / false
     */
    public void setCanFire(boolean val) {
        canFire = val;
    }

    /**
     * @return of het wapen autoFire ondersteunt
     */
    public boolean getAutoFire() {
        return autoFire;
    }

    /**
     * @return de hoeveelheid damage die een wapen doet
     */
    public int getDamage() {
        return damage;
    }
}
