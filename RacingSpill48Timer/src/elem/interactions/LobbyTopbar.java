package elem.interactions;

import static org.lwjgl.glfw.GLFW.glfwIconifyWindow;
import static org.lwjgl.nuklear.Nuklear.NK_TEXT_ALIGN_LEFT;
import static org.lwjgl.nuklear.Nuklear.NK_WINDOW_NO_SCROLLBAR;
import static org.lwjgl.nuklear.Nuklear.nk_begin;
import static org.lwjgl.nuklear.Nuklear.nk_end;
import static org.lwjgl.nuklear.Nuklear.nk_label;
import static org.lwjgl.nuklear.Nuklear.nk_layout_row_dynamic;
import static org.lwjgl.nuklear.Nuklear.nk_rect;
import static org.lwjgl.nuklear.Nuklear.nk_style_pop_vec2;
import static org.lwjgl.nuklear.Nuklear.nk_style_push_vec2;

import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkRect;
import org.lwjgl.nuklear.NkVec2;
import org.lwjgl.system.MemoryStack;

import elem.ui.UIButton;
import elem.ui.UIObject;
import engine.io.Window;
import main.Main;
import scenes.adt.GlobalFeatures;
import scenes.game.player_local.Player;

/**
 * TODO
 * @author Jhoffis
 *
 */
public class LobbyTopbar  extends UIObject {

	private TopbarInteraction topbar;
	private String windowTitle;
	private String title;
	private NkRect rect;
	private UIButton minimizeBtn;
	private int options;
	
	private Player player;

	public LobbyTopbar(GlobalFeatures features, long window) {

		PressAction pressAction = (double X, double Y) -> {
			// Move window
			topbar.setX(X);
			topbar.setY(Y);
			topbar.setHeld(true);
		};

		int height = 100;
		
		topbar = new TopbarInteraction(window, height, pressAction);

		// Layout settings
		windowTitle = "lobbyTopbar";
		rect = NkRect.create();
		nk_rect(0, 0, Window.CURRENT_WIDTH, height, rect);
		options = NK_WINDOW_NO_SCROLLBAR;

		// Buttons
		minimizeBtn = new UIButton(features.getMinimizeText());

		minimizeBtn.setPressedAction(() -> glfwIconifyWindow(topbar.getWindow()));

	}

	public void setTitle(String title) {
		this.title = Main.GAME_NAME + " " + Main.GAME_VERSION + " - " + title;
	}

	public void layout(NkContext ctx) {
		if (nk_begin(ctx, windowTitle, rect, options)) {

			try (MemoryStack stack = MemoryStack.stackPush()) {
				// Set own custom styling
				NkVec2 spacing = NkVec2.mallocStack(stack);
				NkVec2 padding = NkVec2.mallocStack(stack);

				int height = getHeight() * 3 / 4;

				spacing.set(height, 0);
				padding.set(height, (int) (height / 3 * 0.65));

				nk_style_push_vec2(ctx, ctx.style().window().spacing(), spacing);
				nk_style_push_vec2(ctx, ctx.style().window().padding(), padding);

				// Layout
				nk_layout_row_dynamic(ctx, height, 3);

				nk_label(ctx, title, NK_TEXT_ALIGN_LEFT);

				// Empty space
				nk_label(ctx, "", NK_TEXT_ALIGN_LEFT);

				minimizeBtn.layout(ctx);

				// Reset styling
				nk_style_pop_vec2(ctx);
				nk_style_pop_vec2(ctx);
			}
		}
		nk_end(ctx);

//			
	}

	public void press(double x, double y) {
		topbar.press(x, y);
	}

	public void release() {
		topbar.release();
	}

	public void move(double x, double y) {
		topbar.move(x, y);
	}

	public int getHeight() {
		return topbar.getHeight();
	}

	public String getName() {
		return windowTitle;
	}

	public void unpress() {
		minimizeBtn.unpress();
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

}
