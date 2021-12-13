package text.concept;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import text.concept.data.SquareEntity;
import text.concept.data.InputState;
import text.concept.geometry.Direction;
import text.concept.input.Keyboard;

public class TextShower extends ApplicationAdapter {
	//GDX specific
	BitmapFont font;
	SpriteBatch batch;
	Texture playerTexture;
	Sprite playerSprite;

	public static class GameState {
		public int screenMaxWidth;
		public int screenMaxHeight;

		public GameState(int screenMaxWidth, int screenMaxHeight) {
			this.screenMaxWidth = screenMaxWidth;
			this.screenMaxHeight = screenMaxHeight;
		}

		public SquareEntity player =
				new SquareEntity(150, 150, 100, 0, 550);
	}

	//Program state specific
	GameState state;
	InputState inputState;

	@Override
	public void create () {
		state = new GameState(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		inputState = new InputState();

		batch = new SpriteBatch();
		playerTexture = new Texture("square.png");
		playerTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		playerSprite = new Sprite(playerTexture);

		font = new BitmapFont();


		Gdx.input.setInputProcessor(new InputAdapter() {

			@Override
			public boolean keyDown (int keyCode) {
				if (keyCode == Input.Keys.ESCAPE) {
					inputState.keyboardKeyState.put(Keyboard.ESCAPE, Boolean.TRUE);
				}
				else if (keyCode == Input.Keys.SPACE) {
					inputState.keyboardKeyState.put(Keyboard.SPACE, Boolean.TRUE);
				}
				return true;
			}

			@Override
			public boolean keyUp(int keyCode) {
				if (keyCode == Input.Keys.SPACE) {
					inputState.keyboardKeyState.put(Keyboard.SPACE, Boolean.FALSE);
				}
				return true;
			}
		});
	}

	public static void handleKeyboardInput(Input input, InputState inputState) {
		inputState.keyboardKeyState.put(Keyboard.W, Boolean.FALSE);
		inputState.keyboardKeyState.put(Keyboard.A, Boolean.FALSE);
		inputState.keyboardKeyState.put(Keyboard.S, Boolean.FALSE);
		inputState.keyboardKeyState.put(Keyboard.D, Boolean.FALSE);
		inputState.keyboardKeyState.put(Keyboard.UP, Boolean.FALSE);
		inputState.keyboardKeyState.put(Keyboard.DOWN, Boolean.FALSE);
		inputState.keyboardKeyState.put(Keyboard.LEFT, Boolean.FALSE);
		inputState.keyboardKeyState.put(Keyboard.RIGHT, Boolean.FALSE);

		if (input.isKeyPressed(Input.Keys.W)) {
			inputState.keyboardKeyState.put(Keyboard.UP, Boolean.TRUE);
		}
		else if (input.isKeyPressed(Input.Keys.S)) {
			inputState.keyboardKeyState.put(Keyboard.DOWN, Boolean.TRUE);
		}

		if (input.isKeyPressed(Input.Keys.A)) {
			inputState.keyboardKeyState.put(Keyboard.LEFT, Boolean.TRUE);
		}
		else if (input.isKeyPressed(Input.Keys.D)) {
			inputState.keyboardKeyState.put(Keyboard.RIGHT, Boolean.TRUE);
		}

		if (input.isKeyPressed(Input.Keys.LEFT)) {
			inputState.keyboardKeyState.put(Keyboard.LEFT, Boolean.TRUE);
		}
		else if (input.isKeyPressed(Input.Keys.RIGHT)) {
			inputState.keyboardKeyState.put(Keyboard.RIGHT, Boolean.TRUE);
		}

		if (input.isKeyPressed(Input.Keys.UP)) {
			inputState.keyboardKeyState.put(Keyboard.UP, Boolean.TRUE);
		}
		else if (input.isKeyPressed(Input.Keys.DOWN)) {
			inputState.keyboardKeyState.put(Keyboard.DOWN, Boolean.TRUE);
		}
	}

	public static void checkExit(InputState inputState) {
		if (inputState.keyboardKeyState.get(Keyboard.ESCAPE) == Boolean.TRUE) {
			Gdx.app.exit();
			System.exit(0);
		}
	}

	public static void applyPlayerInput(InputState inputState, GameState state, float delta) {
		if (inputState.keyboardKeyState.get(Keyboard.LEFT) != Boolean.TRUE
			&& inputState.keyboardKeyState.get(Keyboard.RIGHT) != Boolean.TRUE
			&& inputState.keyboardKeyState.get(Keyboard.UP) != Boolean.TRUE
			&& inputState.keyboardKeyState.get(Keyboard.DOWN) != Boolean.TRUE) {
			//no input
			return;
		}

		float x = 0;
		if (inputState.keyboardKeyState.get(Keyboard.LEFT) == Boolean.TRUE) {
			x = -1;
		}
		else if (inputState.keyboardKeyState.get(Keyboard.RIGHT) == Boolean.TRUE) {
			x = 1;
		}

		float y = 0;
		if (inputState.keyboardKeyState.get(Keyboard.UP) == Boolean.TRUE) {
			y = 1;
		}
		else if (inputState.keyboardKeyState.get(Keyboard.DOWN) == Boolean.TRUE) {
			y = -1;
		}

		Direction direction = Direction.getDirection(x, y);
		float rotation = direction.degreeAngle;


		float directionX = (float) Math.cos(Math.PI / 180 * rotation);
		float directionY = (float) Math.sin(Math.PI / 180 * rotation);

		float step = state.player.speed * delta;
		state.player.x += directionX * step;
		state.player.y += directionY * step;
		state.player.rotation = rotation;
		state.player.direction = direction;
	}

	//do not allow player move beyond screen
	public static void calculatePlayerCollisions(GameState state) {
		SquareEntity player = state.player;
		float halfSide = player.side/2;

		//against borders
		if (player.x > state.screenMaxWidth - halfSide) {
			player.x = state.screenMaxWidth - halfSide;
		} else if (player.x < halfSide) {
			player.x = halfSide;
		}

		if (player.y > state.screenMaxHeight - halfSide) {
			player.y = state.screenMaxHeight - halfSide;
		} else if (player.y < halfSide) {
			player.y = halfSide;
		}
	}

	public final int MAX_UPDATE_ITERATIONS = 3;
	public final float FIXED_TIMESTAMP = 1/60f;
	private float internalTimeTracker = 0;

	@Override
	public void render () {
		//input handling
		checkExit(inputState);
		handleKeyboardInput(Gdx.input, inputState);

		//fixed-timestamp logic handling
		float delta = Gdx.graphics.getDeltaTime();
		internalTimeTracker += delta;
		int iterations = 0;

		while(internalTimeTracker > FIXED_TIMESTAMP && iterations < MAX_UPDATE_ITERATIONS) {
			//apply input
			applyPlayerInput(inputState, state, FIXED_TIMESTAMP);

			//collision detection
			calculatePlayerCollisions(state);

			//time tracking logic
			internalTimeTracker -= FIXED_TIMESTAMP;
			iterations++;
		}

		//render
		Gdx.gl.glClearColor(0.25f, 0.45f, 0.75f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


		//this is where we actually draw player image
		batch.begin();

		SquareEntity player = state.player;
		playerSprite.setBounds(player.x-player.side/2, player.y-player.side/2, player.side , player.side);
		playerSprite.setOriginCenter();
		playerSprite.setRotation(player.rotation);
		playerSprite.draw(batch);

		font.draw(batch, "X, Y: (" + player.x+", "+player.y+")", 20, state.screenMaxHeight -20);

		batch.end();
	}

	//it should be called when we exit our program
	@Override
	public void dispose () {
		batch.dispose();
		playerTexture.dispose();
	}

}