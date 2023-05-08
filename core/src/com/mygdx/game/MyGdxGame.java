package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import java.util.Random;

public class MyGdxGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture gameover;
	Music music;
	Sound soundFlap;
	Sound soundCoin;
	Texture[] characters;
	Texture coin;
	Texture emerald;
	Texture bread;
	Texture poison;
	int flapState = 0;
	float characterY = 0;
	float velocity = 0;
	Circle characterCircle;
	int score = 0;
	int coins = 0;
	int scoringTube = 0;
	BitmapFont font;
	int gameState = 0;
	float gravity = 2;
	Texture topTube;
	Texture bottomTube;
	float gap = 400;
	float maxTubeOffset;
	Random randomGenerator;
	float tubeVelocity = 4;
	int numberOfTubes = 4;
	float[] tubeX = new float[numberOfTubes];
	float[] tubeOffset = new float[numberOfTubes];
	float distanceBetweenTheTube;
	int newSpeed = 2;
	Rectangle[] coinsRectangle;
	Rectangle[] emeraldsRectangle;
	Rectangle[] poisonRectangle;
	Rectangle[] topTubeRectangles;
	Rectangle[] bottomTubeRectangles;

	@Override
	public void create() {
		batch = new SpriteBatch();

		music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
		music.setLooping(true);
		music.setVolume(0.3f);
		music.play();

		soundFlap = Gdx.audio.newSound(Gdx.files.internal("sfx_wing.ogg"));
		soundCoin = Gdx.audio.newSound(Gdx.files.internal("coin.ogg"));

		background = new Texture("background.png");
		gameover = new Texture("gameover.png");
		characterCircle = new Circle();

		characters = new Texture[2];
		characters[0] = new Texture("ufo.png");
		characters[1] = new Texture("ufo2.png");

		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(5);

		topTube = new Texture("toptube.png");
		bottomTube = new Texture("bottomtube.png");

		coin = new Texture("coin.png");
		emerald = new Texture("emerald.png");
		bread = new Texture("bread.png");

		poison = new Texture("poison.png");

		maxTubeOffset = Gdx.graphics.getHeight() / 2 - gap / 2 - 50;
		randomGenerator = new Random();
		distanceBetweenTheTube = Gdx.graphics.getWidth() * 3 / 4;
		topTubeRectangles = new Rectangle[numberOfTubes];
		bottomTubeRectangles = new Rectangle[numberOfTubes];

		coinsRectangle = new Rectangle[numberOfTubes];
		emeraldsRectangle = new Rectangle[numberOfTubes];
		poisonRectangle = new Rectangle[numberOfTubes];

		startGame();
	}

	public void startGame()
	{
		characterY = Gdx.graphics.getHeight() / 2 - characters[flapState].getHeight() / 2;
		for (int i = 0; i < numberOfTubes; i++) {
			tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);

			tubeX[i] = Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2 + Gdx.graphics.getWidth() + i * distanceBetweenTheTube;

			topTubeRectangles[i] = new Rectangle();
			bottomTubeRectangles[i] = new Rectangle();

			coinsRectangle[i] = new Rectangle();
			emeraldsRectangle[i] = new Rectangle();
			poisonRectangle[i] = new Rectangle();
		}
	}

	@Override
	public void render() {
		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if (gameState == 1) {
			if (tubeX[scoringTube] < Gdx.graphics.getWidth() / 2) {
				score++;
				if (scoringTube < numberOfTubes - 1) {
					scoringTube++;
				} else {
					scoringTube = 0;
				}
			}

			if (score == newSpeed) {
				distanceBetweenTheTube -= 700 / (newSpeed / 2);
				newSpeed += 2;
				gravity++;
			}

			if (Gdx.input.justTouched()) {
				velocity = -20;
				soundFlap.play();
			}

			for (int i = 0; i < numberOfTubes; i++) {
				if (tubeX[i] < -topTube.getWidth()) {
					tubeX[i] += numberOfTubes * distanceBetweenTheTube;
					tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
				} else {
					tubeX[i] = tubeX[i] - tubeVelocity;
				}

				batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
				batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i]);

				topTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
				bottomTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());

				batch.draw(coin, (float) (Gdx.graphics.getHeight() / 2 + tubeX[i] + i * 750), (float) (Gdx.graphics.getWidth() / 2 - 400 - i * 250));
				coinsRectangle[i] = new Rectangle((float) (Gdx.graphics.getHeight() / 2 + tubeX[i] + i * 750), (float) (Gdx.graphics.getWidth() / 2 - 400 - i * 250), coin.getWidth() / 2, coin.getHeight() / 2);

				batch.draw(emerald, (float) (Gdx.graphics.getHeight() / 2 + tubeX[i] + i+1 * 280), (float) (Gdx.graphics.getWidth() / 2 - 400 - i * 850));
				emeraldsRectangle[i] = new Rectangle((float) (Gdx.graphics.getHeight() / 2 + tubeX[i] + i+1 * 280), (float) (Gdx.graphics.getWidth() / 2 - 400 - i * 850), coin.getWidth() / 2, coin.getHeight() / 2);
			}

			for (int i = 0; i < numberOfTubes - 2; i++) {
				batch.draw(poison, Gdx.graphics.getHeight() / 2 + tubeX[i] + tubeOffset[i] * 3, Gdx.graphics.getWidth() / 2 - 400 + tubeOffset[i]);
				poisonRectangle[i] = new Rectangle(Gdx.graphics.getHeight() / 2 + tubeX[i] + tubeOffset[i] * 3, Gdx.graphics.getWidth() / 2 - 400 + tubeOffset[i], poison.getWidth() / 2, poison.getHeight() / 2);
			}

			if (characterY > 0) {
				velocity = velocity + gravity;
				characterY -= velocity;
			} else {
				gameState = 2;
			}

		} else if (gameState == 0) {
			if (Gdx.input.justTouched()) {
				gameState = 1;
			}

		} else if(gameState == 2) {
			batch.draw(gameover ,Gdx.graphics.getWidth()/2 - gameover.getWidth()/2,Gdx.graphics.getHeight()/2 - gameover.getHeight());

			int highScore = 0;
			FileHandle file = Gdx.files.local("/score.txt");
			if (file.length() == 0L) {
				file.writeString(Integer.toString(0), false);
			}
			int fileScore = Integer.parseInt(file.readString());
			if (score > fileScore) {
				highScore = score;
				file.writeString(Integer.toString(highScore), false);
			} else {
				highScore = fileScore;
			}

			font.draw(batch , "High score: " + String.valueOf(highScore) , Gdx.graphics.getWidth()/2 - gameover.getWidth()/2 + 50, Gdx.graphics.getHeight()/2 - gameover.getHeight());


			if (Gdx.input.justTouched()) {
				gameState = 1;
				startGame();
				score = 0;
				coins = 0;
				gravity = 2;
				scoringTube= 0;
				velocity = 0;
				newSpeed = 2;
			}
		}

		if (flapState == 0) {
			flapState = 1;
		} else {
			flapState = 0;
		}

		batch.draw(characters[flapState], Gdx.graphics.getWidth() / 2 - characters[flapState].getWidth() / 2, characterY);

		font.draw(batch , "Score: " + String.valueOf(score) , 100 , 200);
		font.draw(batch , "Coins: " + String.valueOf(coins) , 100 , 100);

		characterCircle.set(Gdx.graphics.getWidth() / 2, characterY + characters[flapState].getHeight() / 2, characters[flapState].getWidth() / 2);

		for (int i = 0; i < numberOfTubes; i++) {

			if (Intersector.overlaps(characterCircle, topTubeRectangles[i]) || Intersector.overlaps(characterCircle, bottomTubeRectangles[i])) {
				gameState = 2;
			}

			if (Intersector.overlaps(characterCircle, poisonRectangle[i])) {
				gameState = 2;
			}

			if (Intersector.overlaps(characterCircle, coinsRectangle[i])) {
				soundCoin.play();
				coins++;
			}

			if (Intersector.overlaps(characterCircle, emeraldsRectangle[i])) {
				soundCoin.play();
				coins += 5;
			}
		}
		batch.end();
	}

	public void dispose() {
		super.dispose();
		music.dispose();
		soundFlap.dispose();
		soundCoin.dispose();
	}
}
