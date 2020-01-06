package main;

import game_modes.GameMode;
import game_modes.GolfLike;
import game_modes.PointRush;
import handlers.GameHandler;

public class Main {

	public static final boolean DEBUG = false;
	public static final String[] CAR_TYPES = { "Decentra", "Bentslie", "Fabula", "Tuffbred", "Tiềnmobile" };
	public static final String[] AI_NAMES = { "Jens", "Benz", "Razor", "The Boi", "The Viper", "The Biper",
			"èŠ‚å¥�å¤§å¸ˆ", "Knut", "Pepsi", "Cola", "Cherry", "Sprite", "Apex Legend", "The Law", "Anime Lover",
			"noobmaster69", "TeaBottle", "Racerdude", "BestRacer97", "Niki Lauda", "PÃ¥l the Racer" };
	public static final String[] DIFFICULTY_TYPES = { "Easy", "Normal", "Hard", "Godlike" };
	public static String[] AMOUNT_OF_AI;
	public static boolean[] AI_NAMES_TAKEN;
	public static final String GAME_NAME = "racingmaybe.jar";
	public static final String[] RACE_AMOUNT = { String.valueOf(18), String.valueOf(9), String.valueOf(2) };
	public static final GameMode[] GAME_MODES = { new GolfLike(), new PointRush() };
	public static final String UPGRADELVL_REGEX = "%";
	public static final String STANDARD_REGEX = "#";
	public static final String END_ALL_CLIENT_STRING = "!ENDALL!";
	public static final String GAME_VERSION = "1.8.4_FINAL";

	public static long DISCONNECTED_ID = -1;

	public static void main(String[] args) {
		AMOUNT_OF_AI = new String[9];
		for (int i = 0; i < AMOUNT_OF_AI.length; i++) {
			AMOUNT_OF_AI[i] = String.valueOf(i);
		}

		GameHandler game = new GameHandler();
		game.start("fixme");
	}

	
}