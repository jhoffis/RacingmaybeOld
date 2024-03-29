package scenes.game.player_local;

import scenes.game.player_local.Car;

public class PlayerInfo {

	private Car car;
	private Bank bank;
	private Long discID;
	private String name;
	private byte ready;
	private long timeLapsedInRace;
	private byte host;
	private byte id;

	public PlayerInfo(String name, byte id, byte host) {
		this(name, host);
		this.id = id;
	}

	public PlayerInfo(String name, byte host) {
		this.name = name;
		this.host = host;
		this.car = null;
		bank = new Bank();
	}

	/**
	 * @return name#ready#host#points
	 */
	public String getLobbyInfo() {
		if (car != null)
			return name + "#" + ready + "#" + host + "#" + bank.getPoints();
		else
			return "Joining...";
	}

	public String getCarInfo() {
		String res = "";
		if (car != null)
			res = car.getRep().getInfo();
		return res;
	}

	public void newCar(String carname) {
		car = new Car(carname);
	}
	
	public void newCar(String[] ids, int i) {
		car = new Car(ids, i);
	}

	public void newRace() {
		finished = 0;
		timeLapsedInRace = 0;
		inTheRace = false;
	}

	/**
	 * fra og med input[3] input[3] finished input[4] timecurrently
	 * 
	 * @param input
	 * 
	 */
	public void updateRaceResults(byte finished, long time) {
		setFinished(finished);
		timeLapsedInRace = time;
	}

//	public String getRaceResults() {
//		return name + "#" finished + "#" + time + "#" + pointsGained or something.;
//	}

	/**
	 * @return name#ready#car#...
	 */
	public String getRaceInfo(boolean allFinished, boolean full) {
		String carName = null;
		if (car != null) {
			carName = car.getRep().getName();
		} else {
			carName = "NO_NAME";
		}

		int point = full ? bank.getPoints() : pointsAdded;

		if (allFinished == false)
			return name + "#" + finished + "#" + timeLapsedInRace + "#0#" + carName;
		else
			return name + "#" + finished + "#" + timeLapsedInRace + "#, " + (full ? "" : "+ ") + point + " points"
					+ ", +$" + moneyAdded + "#" + carName;
	}

	public void addPointsAndMoney(int amountPlayers, int place, int behindLeaderBy, int racesDone) {

		float inflation = (racesDone + 1f) / 2f;
		float stdPrice = 100f;
		pointsAdded = 0;

		if (!(amountPlayers == -1 || place == -1)) {
			pointsAdded = (amountPlayers - (place + 1)) + car.getRep().getPointParadise();
			moneyAdded = (int) (stdPrice * inflation);
			if (place > 0)
				moneyAdded += (int) (stdPrice * (0.30 * place / (amountPlayers - 1)) * inflation);
			else if (place == 0)
				moneyAdded += car.getRep().getMoneyMails();
		} else {
			moneyAdded = (int) (stdPrice * (3 / 2) * inflation);
		}

		bank.addPoints(pointsAdded);
		bank.addMoney(moneyAdded);
	}

	public int getFinished() {
		return finished;
	}

	public void setFinished(int i) {
		this.finished = (byte) i;
	}

	public int getPoints() {
		return bank.getPoints();
	}

	public void setPoints(int points) {
		bank.setPoints(points);
	}

	public int getMoney() {
		return bank.getMoney();
	}

	public void setMoney(int money) {
		bank.setMoney(money);
	}

	public String getName() {
		return name;
	}

	public String getNameID() {
		return id + "#" + name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCarName() {
		return car.getRep().getName();
	}

	public long getTime() {
		return timeLapsedInRace;
	}

	public void setTime(long time) {
		this.timeLapsedInRace = time;
	}

	public int getPointsAdded() {
		return pointsAdded;
	}

	public void setPointsAdded(int pointsAdded) {
		this.pointsAdded = pointsAdded;
	}

	public int getMoneyAdded() {
		return moneyAdded;
	}

	public void setMoneyAdded(int moneyAdded) {
		this.moneyAdded = moneyAdded;
	}

	public byte getID() {
		return id;
	}

	public void setIn(boolean in) {
		inTheRace = in;
	}

	public boolean isIn() {
		return inTheRace;
	}

	public Car getCar() {
		return car;
	}

	public void setCar(Car car) {
		this.car = car;
	}

	public Bank getBank() {
		return bank;
	}

	public void setBank(Bank bank) {
		this.bank = bank;
	}

	public byte getReady() {
		return ready;
	}

	public void setReady(byte ready) {
		this.ready = ready;
	}

	public Long getDisconnectID() {
		return discID;
	}

	public void setDiscID(long id2) {
		discID = id2;
	}

	public int getPodium() {
		return podium;
	}

	public void setPodium(int podium) {
		this.podium = podium;
	}

	public void setID(byte id2) {
		this.id = id2;
	}

	public byte getHost() {
		return host;
	}


}
