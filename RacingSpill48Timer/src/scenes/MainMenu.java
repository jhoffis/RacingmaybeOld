package scenes;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import adt.Scene;
import handlers.SceneHandler;
import handlers.ServerHandler;
import startup.Main;

public class MainMenu extends Scene {

	private JButton options;
	private JButton host;
	private JButton join;
	private JLabel title;
	private Thread thread;
	private ServerHandler serverHandler;
	private Lobby lobby;

	public MainMenu(Lobby lobby) {
		// Init variables
		serverHandler = new ServerHandler();
		options = new JButton("Options");
		host = new JButton("Host");
		join = new JButton("Join");
		this.lobby = lobby;
		title = new JLabel("A Smooth Cruise v.1.2_2");

		title.setPreferredSize(new Dimension(550, 20));

		// Eventlisteners
		options.addActionListener((ActionEvent e) -> SceneHandler.instance.changeScene(4));
		host.addActionListener((ActionEvent e) -> host());
		join.addActionListener((ActionEvent e) -> join());

		// Add to jpanel
		add(title);
		add(options);
		add(host);
		add(join);

	}

	private void host() {
		// init server and player and then go to lobby
		// Register your username
		String name = username();
		if (name == null)
			return;

		// Register your car
		String car = carSelection();
		if (car == null)
			return;

		serverHandler.createNew();

		SceneHandler.instance.changeScene(1);
		thread = new Thread(lobby);

		lobby.createNewLobby(name, 1, car, serverHandler, thread);

		thread.start();
	}

	private void join() {
		// init some shit like ip and player and then go to lobby
		// Register your username
		String name = username();
		if (name == null)
			return;

		// Register hosts ip
		String ip;
		do {
			ip = JOptionPane.showInputDialog("What's the ip?");

			try {
				if (ip.isEmpty())
					JOptionPane.showMessageDialog(null, "Write the ip please");
			} catch (NullPointerException e) {
				return;
			}

		} while (ip.isEmpty());

		// Register your car
		String car = carSelection();
		if (car == null)
			return;

		SceneHandler.instance.changeScene(1);
		thread = new Thread(lobby);

		lobby.joinNewLobby(name, 0, car, ip, thread);

		thread.start();
	}

	private String carSelection() {

		return (String) JOptionPane.showInputDialog(null, "Choose your car, mate", "Carznstuff",
				JOptionPane.PLAIN_MESSAGE, null, Main.CARTYPES, "Supra");
	}

	private String username() {
		String name = null;
		do {
			name = JOptionPane
					.showInputDialog("<html>What's your username?<br/> Don't use # and max 12 letters</html>");

			try {
				if (name.isEmpty() || name.contains("#") || name.length() > 12)
					JOptionPane.showMessageDialog(null, "Write your username properly please");
			} catch (NullPointerException e) {
				break;
			}

		} while (name.isEmpty() || name.contains("#") || name.length() > 12);
		return name;
	}

}
