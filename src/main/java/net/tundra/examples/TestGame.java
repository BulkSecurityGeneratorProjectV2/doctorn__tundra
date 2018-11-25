package net.tundra.examples;

import net.tundra.core.Game;
import net.tundra.core.TundraException;
import net.tundra.core.graphics.Graphics;
import net.tundra.core.resources.models.Model;
import net.tundra.core.resources.sprites.Animation;
import net.tundra.core.resources.sprites.Font;
import net.tundra.core.resources.sprites.SpriteSheet;
import net.tundra.core.scene.Camera;
import net.tundra.core.scene.FixedLight;
import net.tundra.core.scene.OrbitalCamera;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class TestGame extends Game {
  private Animation android;
  private Camera camera, camera2, active;
  private Font font;
  float angle;

  public TestGame() {
    super(1920, 1080, "tundra", true);
  }

  @Override
  public void init() throws TundraException {
    camera = new OrbitalCamera(new Vector3f(0, 0, -1), 10f);
    camera2 = new FPSCamera(new Vector3f(0, -3f, 0));
    addCamera(camera);
    addCamera(camera2);
    active = camera2;

    FixedLight main = new FixedLight(19, 3, 19, 1, 1, 1);
    addLight(new FixedLight(4, 0, 0, 0, 0, 1));
    addLight(new FixedLight(-4, 0, 0, 1, 0, 0));
    addLight(main);
    android = new Animation(new SpriteSheet("res/android.png", 24, 24), 0, 3, 5, 3, true, 10);
    android.start();

    SpriteSheet fontSheet = new SpriteSheet("res/font.png", 20, 22);
    font = new Font(fontSheet);
    getInput().setMouseGrabbed(true);

    Camera shadow = new ShadowCamera();
    shadow.setPosition(new Vector3f(19, 3, 19));
    shadow.setTarget(new Vector3f(0, 0, 0));
    enableShadowMapping(shadow, main);
    addCamera(shadow);
  }

  @Override
  public void update(int delta) throws TundraException {
    android.update(delta);
    angle += 0.001f * delta;

    if (getInput().isKeyPressed(org.lwjgl.input.Keyboard.KEY_C)) {
      if (active == camera) active = camera2;
      else active = camera;
    }

    if (getInput().isKeyPressed(org.lwjgl.input.Keyboard.KEY_V)) active.togglePerspective();
    if (getInput().isKeyPressed(org.lwjgl.input.Keyboard.KEY_T)) toggleDebug();
    if (getInput().isKeyPressed(org.lwjgl.input.Keyboard.KEY_L)) toggleLighting();

    if (getInput().isMouseButtonPressed(0))
      addObject(
          new Bullet(
              this, camera2.getPosition().add(camera2.getLook().mul(0.5f)), camera2.getLook()));
  }

  @Override
  public void render(Graphics g) throws TundraException {
    g.use(active);
    for (int i = -5; i < 5; i++) {
      Vector3f position = new Vector3f(i, -3.5f, -2);
      Matrix4f transform =
          new Matrix4f()
              .translate(position)
              .scale(0.5f)
              .lookAlong(active.getLookAlong(), active.getUp());
      g.drawModel(Model.PLANE, android.currentFrame(), transform);
    }

    g.drawModel(
        Model.CUBE,
        new Matrix4f().translate(5, -3, 5).rotate((float) Math.PI / 6f, new Vector3f(0, 1, 0)));
    g.drawModel(
        Model.CUBE,
        new Matrix4f()
            .translate(-7, 0, 3)
            .scale(((float) Math.sin(angle) + 2) / 3f)
            .rotate(angle, new Vector3f(0, 1, 0))
            .rotate(angle, new Vector3f(1, 0, 0)));

    g.drawModel(
        Model.PLANE,
        new Matrix4f()
            .translate(0, -4f, 0f)
            .scale(20f)
            .rotate(-(float) Math.PI / 2f, new Vector3f(1, 0, 0)));

    g.drawModel(
        Model.PLANE,
        new Matrix4f()
            .translate(0, 4f, 0f)
            .scale(20f)
            .rotate(-(float) Math.PI * 3f / 2f, new Vector3f(1, 0, 0)));

    for (int i = 0; i < 4; i++) {
      g.drawModel(
          Model.PLANE,
          new Matrix4f()
              .rotate((float) Math.PI * i / 2f, new Vector3f(0, 1, 0))
              .translate(0, 0f, 20f)
              .scale(20f, 4f, 1f)
              .rotate(-(float) Math.PI, new Vector3f(1, 0, 0)));
    }

    g.drawString(getFPS() + " FPS", font, 10, 10);
    g.drawString(getLights().size() + " LIGHTS", font, 10, 35);
  }

  public static void main(String args[]) throws TundraException {
    TestGame test = new TestGame();
    test.start();
  }
}
