package elem;

import audio.RaceAudio;

public class Car implements Cloneable {

	private boolean gas;
	private boolean idle;
	private boolean brake;
	private boolean clutch;
	private boolean hasTurbo;
	private boolean hasNOS;
	private boolean gearTooHigh;
	private boolean NOSON;
	private boolean engineOn;
	private boolean changed;
	private long nosTimeLeft;
	private long nosTimeToGive;
	private int nosTimeToGiveStandard;
	private int nosAmountLeft;
	private int nosAmountLeftStandard;
	private double nosStrength;
	private double nosStrengthStandard;
	private double speedLinear;
	private double speedActual;
	private double hp;
	private double weightloss;
	private double totalWeight;
	private double currentWeight;
	private double spdinc;
	private double distance;
	private double topSpeed;
	private double resistance;
	private int gear;
	private int totalGear;
	private int totalRPM;
	private int rpm;
	private String carName;
	private RaceAudio audio;
	private int idleSpeed;
	private double gearsbalance;
	private boolean upgradedGears;
	private boolean audioActivated;
	private double maxValuePitch;
	private int highestSpeedAchived;
	private long gearBoostTime;
	private double gearBoost;
	private boolean gearBoostON;
	private double gearBoostSTD;
	private double top;
	private double bot;
	private Integer[] upgradeLVLs;
	private double drag;

	/**
	 * carName + "#" + hp + "#" + (totalWeight - weightloss) + "#" +
	 * nosStrengthStandard + "#" + totalGear + "#" + topSpeed + "#" +
	 * highestSpeedAchived;
	 */
	public Car(String[] cloneToServerString, int fromIndex) {
		updateServerClone(cloneToServerString, fromIndex);
	}

	public Car(String cartype, boolean audioActivated) {

		hasTurbo = false;
		hasNOS = false;
		gearTooHigh = false;
		this.audioActivated = audioActivated;

		speedLinear = 0f;
		nosTimeLeft = 0;
		nosTimeToGiveStandard = 1500;
		nosTimeToGive = nosTimeToGiveStandard;
		nosAmountLeft = 0;
		nosAmountLeftStandard = 0;
		nosStrength = 0;
		nosStrengthStandard = 0;
		topSpeed = 250;
		resistance = 1.0;
		gearsbalance = 1.0;
		idleSpeed = 1000;
		gearBoostSTD = 1;
		top = 24;
		bot = 4;
		drag = 1;
		setUpgradeLVLs(new Integer[Upgrades.UPGRADE_NAMES.length]);

		// Kanskje Lada der kj�relyden er hardbass.

		maxValuePitch = 2;

		switch (cartype) {
		case "M3":
			hp = 300;
			totalWeight = 1549;
			totalGear = 6;
			totalRPM = 8000;
			topSpeed = 249;
			break;
		case "Supra":
			hp = 220;
			totalWeight = 1400;
			totalGear = 5;
			totalRPM = 7800;
			topSpeed = 285;
			break;
		case "Mustang":
			hp = 310;
			totalWeight = 1607;
			totalRPM = 7500;
			totalGear = 5;
			topSpeed = 250;
			break;
		case "Bentley":
			// Bentley Blower No.1
			hp = 242;
			totalRPM = 3200;
			totalWeight = 1625;
			totalGear = 4;
			maxValuePitch = 3;
			topSpeed = 222;
			break;
		case "Skoda Fabia":
			hp = 64;
			totalWeight = 1090;
			totalRPM = 5500;
			totalGear = 5;
			maxValuePitch = 4;
			topSpeed = 162;
			break;
		case "Corolla":
			hp = 118;
			totalWeight = 998;
			totalRPM = 8000;
			totalGear = 5;
			topSpeed = 201;
			break;
		}
//		hp = 1600;
		setCarName(cartype.toLowerCase());
		if (audioActivated)
			audio = new RaceAudio(carName);
//		System.out.println("Weightcalc: " + weightcalc +", spdinc: " + spdinc);
	}

	public void updateVolume() {
		audio.updateVolume();
	}

	public boolean isHasTurbo() {
		return hasTurbo;
	}

	public void setHasTurbo(boolean hasTurbo) {
		this.hasTurbo = hasTurbo;
	}

	public boolean isHasNOS() {
		return hasNOS;
	}

	public void setHasNOS(boolean hasNOS) {
		this.hasNOS = hasNOS;
	}

	public double getTopSpeed() {
		return topSpeed;
	}

	public void setTopSpeed(double topSpeed) {
		this.topSpeed = Math.round(topSpeed);
	}

	public void updateSpeed(double tickFactor) {

		//FIXME speedInc g�r mot 1000 per tick (* tickfactor nede) Ikke bra
		updateSpeedInc(tickFactor);
		double speedLinearChange = speedLinear;

		if (engineOn) {
			changed = false;

			// RPM
			double rpmChange = 0;

			if (resistance == 0) {
				// If clutch engaged
				int engineOnFactor = idleSpeed * (engineOn ? 1 : 0);
				double gearFactor = speedLinear / (gearMax() + 1);
				rpm = (int) ((totalRPM - engineOnFactor) * gearFactor + engineOnFactor);
			} else if (gas) {
				// Not engaged but throttle down
				if (rpm < totalRPM - 60)
					rpmChange = hp * ((double) totalRPM / 9000.0) * resistance;
				else
					rpm = totalRPM - 100;
			} else {
				// Not engaged and throttle not down
				if (rpm > idleSpeed)
					rpmChange = -(hp * 0.5 * resistance);
				else
					// Sets RPM to for instance 1000 rpm as standard.
					rpm = idleSpeed;
			}

			rpm = (int) (rpmChange * tickFactor) + rpm;

			// SOUND
			audio.motorPitch(rpm, totalRPM, maxValuePitch);
			audio.turbospoolPitch(rpm, totalRPM);
			audio.straightcutgearsPitch(speedLinear, topSpeed);

			// MOVEMENT
			if (!clutch && gear > 0 && idle && !gas) {
				setEngineOn(false);
			} else if (gas && !clutch && gearCheck()) {

				speedLinearChange += accelerateCar();
				idle = false;

				if (nosTimeLeft > System.currentTimeMillis()) {
					speedLinearChange += nosStrength;
					NOSON = true;
				} else {
					NOSON = false;
				}

				if (gearBoostTime > System.currentTimeMillis()) {
					speedLinearChange += gearBoost;
					gearBoostON = true;
				} else {
					gearBoostON = false;
				}
			} else {
				speedLinearChange += decelerateCar();
				checkIdle();
			}

		} else {
			if (!changed) {
				changed = true;
				resetBooleans();
			}

			speedLinearChange += decelerateCar();
		}

		if (brake) {
			speedLinearChange += brake();
			checkIdle();
		}
		System.out.println("slc" + speedLinearChange  + ", " +speedLinearChange * tickFactor + "tf:" + tickFactor);
		speedLinear += speedLinearChange * tickFactor;

		calculateActualSpeed();
		calculateDrag();
		calculateDistance(tickFactor);
	}

	private double brake() {
		double brake = 0;

		if (speedLinear > 0)
			brake = -spdinc;

		return brake;
	}

	public double decelerateCar() {
		double dec = 0;

		if (speedLinear > 0)
			dec = -0.5f;

		return dec;
	}

	private void calculateDrag() {
		drag = -Math.pow(speedActual / topSpeed, 5) + 1;
		if (drag < 0)
			drag = 0;

	}

	public void calculateActualSpeed() {
		speedActual = (-2 * Math.pow(speedLinear, 2) + 2000f * speedLinear) * (topSpeed / 500000f);
		if (speedActual > highestSpeedAchived)
			highestSpeedAchived = (int) speedActual;
	}

	public void calculateDistance(double tickFactor) {
		distance += (speedActual / 24) * tickFactor;
	}

	public double accelerateCar() {
		double inc = 0;

		if (speedLinear < ((gear - 1) * (500 / totalGear) - 35)) {
			//Shifted too early
			inc = spdinc / 6;
			gearTooHigh = true;

		} else {
			//Perfect shift
			inc = spdinc;
			gearTooHigh = false;
		}

		return inc;
	}

	private void checkIdle() {
		if (speedActual < 2 && !idle) {
			if (engineOn) {
				idle = true;
				audio.motorIdle();
			} else {
				rpm = 0;
				audio.stopAll();
			}
		}
	}

	public void tryGearBoost() {
		int rs = rightShift();
		if (rs == 2) {
			// Best boost
			gearBoostTime = System.currentTimeMillis() + 1000;
			gearBoost = gearBoostSTD;
		} else if (rs == 1) {
			// Good boost
			gearBoostTime = System.currentTimeMillis() + 1000;
			gearBoost = gearBoostSTD / 2;
		} else {
			// No boost
			gearBoostTime = 0;
		}
	}

	public double getSpeedLinear() {
		return speedLinear;
	}

	public void setSpeedLinear(double speedLinear) {
		this.speedLinear = speedLinear;
	}

	public double getSpeedActual() {
		return speedActual;
	}

	public void setSpeedActual(double speedActual) {
		this.speedActual = speedActual;
	}

	private float gearMax() {
		return gear * (500 / totalGear);
	}

	private boolean gearCheck() {
		if (isGearCorrect()) {
			return true;
		} else {
			audio.redline();
			return false;
		}
	}

	public boolean isGearCorrect() {
		return speedLinear < gearMax();
	}

	public boolean isTopGear() {
		return gear == totalGear;
	}

	public void acc() {
		if (!gas && engineOn) {
			gas = true;
			audio.motorAcc(hasTurbo);
		}

	}

	public void dcc() {
		if (gas && engineOn) {
			gas = false;
			if (hasTurbo)
				audio.turboSurge();
			audio.motorDcc();
		}
	}

	public void brakeOn() {
		if (!brake) {
			brake = true;
		}
	}

	public void brakeOff() {
		if (brake) {
			brake = false;
		}
	}

	public void clutchOn() {
		if (!clutch) {
			clutch = true;
			resistance = 1.0;
			if (gas) {
				if (hasTurbo)
					audio.turboSurge();
				audio.motorDcc();
			}
		}
	}

	public void clutchOff() {
		if (clutch) {
			clutch = false;
			if (gear > 0)
				resistance = 0.0;
			if (gas) {
				audio.motorAcc(hasTurbo);
			}
		}
	}

	public void shift(int gear) {
		if (gear <= totalGear && clutch) {
			if (this.gear == 1 && gear == 2) {

			}
			this.gear = gear;

			if (audioActivated)
				audio.gearSound();
		}
	}

	public boolean isAudioActivated() {
		return audioActivated;
	}

	public void setAudioActivated(boolean audioActivated) {
		this.audioActivated = audioActivated;
	}

	public void shiftUp() {
		shift(gear++);
	}

	public void shiftDown() {
		shift(gear--);
	}

	public void nos() {
		if (nosAmountLeft > 0) {
			nosTimeLeft = System.currentTimeMillis() + nosTimeToGive;
			audio.nos();
			nosAmountLeft--;
		}
	}

	public void reset() {
		brake = false;
		clutch = false;
		resetBooleans();
		engineOn = false;
		speedLinear = 0f;
		nosTimeLeft = 0;
		nosTimeToGive = nosTimeToGiveStandard;
		nosAmountLeft = nosAmountLeftStandard;
		nosStrength = nosStrengthStandard;
		speedActual = 0;
		distance = 0;
		gear = 0;
		rpm = 0;
		gearBoostTime = 0;
		gearBoost = 0;
		resistance = 1.0;
		drag = 1;
		if (audioActivated)
			audio.stopAll();
		updateSpeedInc(0);
	}

	private void resetBooleans() {
		idle = false;
		gas = false;
		NOSON = false;
	}

	public void updateSpeedInc(double tickFactor) {
		double w = (totalWeight - weightloss);
//		double weightcalc = (0.00000033 * Math.pow(w, 2) + 0.00019 * w + 0.3);
		double rpmCalc = (double) rpm / (double) totalRPM;
		spdinc = 6 * (hp * rpmCalc / w * gearsbalance) * drag * tickFactor;
	}

	public String showStats(int prevLvl, int nextLvl) {
		return "From LVL " + prevLvl + " to LVL " + nextLvl + ": <br/>" + "HP: " + hp + "<br/>" + "Weight: "
				+ (totalWeight - weightloss) + "<br/>" + "NOS strength: " + nosStrengthStandard + "<br/>"
				+ "Amount of gears: " + totalGear + "<br/>" + "Topspeed: " + topSpeed + " km/h<br/>Tiregrip: "
				+ gearBoostSTD;

	}

	public String showStats() {
		return "<html>" + carName.toUpperCase() + ": <br/>" + "HP: " + hp + "<br/>" + "Weight: "
				+ (totalWeight - weightloss) + "<br/>" + "NOS strength: " + nosStrengthStandard + "<br/>"
				+ "Amount of gears: " + totalGear + "<br/>" + "Topspeed: " + topSpeed + " km/h<br/>Tiregrip: "
				+ gearBoostSTD;

	}

	public String cloneToServerString() {
		return carName + "#" + hp + "#" + (totalWeight - weightloss) + "#" + nosStrengthStandard + "#" + totalGear + "#"
				+ topSpeed + "#" + highestSpeedAchived + "#" + gearBoostSTD;
	}

	public void updateServerClone(String[] values, int fromIndex) {
		carName = values[fromIndex + 0];
		setHp(Double.valueOf(values[fromIndex + 1]));
		setCurrentWeight(Double.valueOf(values[fromIndex + 2]));
		setNosStrengthStandard(Double.valueOf(values[fromIndex + 3]));
		setTotalGear(Integer.valueOf(values[fromIndex + 4]));
		setTopSpeed(Double.valueOf(values[fromIndex + 5]));
		setHighestSpeedAchived(Integer.valueOf(values[fromIndex + 6]));
		setGearBoostSTD(Double.valueOf(values[fromIndex + 7]));
	}

	public int rightShift() {
		int res = 0;
		if ((this.gear == 1 || this.gear == 0) && speedLinear < 2) {
			double tr = totalRPM;

			if (top == -1 && rpm > totalRPM / 2 || (rpm < tr - tr / top && rpm > tr - tr / bot)) {
				res = 2;
			}
		}
		return res;
	}

	public void upgradeRightShift(double change) {
		top = top * change;
		bot = bot * (1 - Math.abs(1 - change));
	}

	public void guarenteeRightShift() {
		top = -1;
	}

	@Override
	public Car clone() throws CloneNotSupportedException {
		Car newCar = (Car) super.clone();
		Integer[] upgradeLVLs = new Integer[this.upgradeLVLs.length];
		for (int i = 0; i < this.upgradeLVLs.length; i++) {
			upgradeLVLs[i] = getUpgradeLVL(i);
		}
		newCar.setUpgradeLVLs(upgradeLVLs);
		return newCar;
	}

	public boolean isGas() {
		return gas;
	}

	public void setGas(boolean gas) {
		this.gas = gas;
	}

	public boolean isBrake() {
		return brake;
	}

	public void setBrake(boolean brake) {
		this.brake = brake;
	}

	public boolean isClutch() {
		return clutch;
	}

	public void setClutch(boolean clutch) {
		this.clutch = clutch;
	}

	public double getSpeed() {
		return speedLinear;
	}

	public void setSpeed(double speed) {
		this.speedLinear = speed;
	}

	public double getHp() {
		return hp;
	}

	public void setHp(double hp) {
		this.hp = hp;
	}

	public double getWeightloss() {
		return weightloss;
	}

	public void setWeightloss(double weightloss) {
		this.weightloss = Math.round(weightloss);
		setCurrentWeight();
	}

	public double getTotalWeight() {
		return totalWeight;
	}

	public void setTotalWeight(double totalWeight) {
		this.totalWeight = totalWeight;
	}

	public double getSpdinc() {
		return spdinc;
	}

	public void setSpdinc(double spdinc) {
		this.spdinc = spdinc;
	}

	public int getGear() {
		return gear;
	}

	public void setGear(int gear) {
		this.gear = gear;
	}

	public int getTotalGear() {
		return totalGear;
	}

	public void setTotalGear(int totalGear) {
		this.totalGear = totalGear;
	}

	public int getTotalRPM() {
		return totalRPM;
	}

	public void setTotalRPM(int totalRPM) {
		this.totalRPM = totalRPM;
	}

	public String getCarName() {
		return carName;
	}

	public void setCarName(String carName) {
		this.carName = carName;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public double getNosStrengthStandard() {
		return nosStrengthStandard;
	}

	public void setNosStrengthStandard(double nosStrengthStandard) {
		this.nosStrengthStandard = nosStrengthStandard;
		if (nosStrengthStandard > 0)
			setHasNOS(true);
	}

	public int getNosAmountLeftStandard() {
		return nosAmountLeftStandard;
	}

	public void setNosAmountLeftStandard(int nosAmountLeftStandard) {
		this.nosAmountLeftStandard = nosAmountLeftStandard;
	}

	/**
	 * @return radian that represents rpm from -180 to ca. 35 - 40 idk yet
	 */
	public double getTachometer() {
		return 235 * ((double) (rpm + 1) / (double) totalRPM) - 203;
	}

	public boolean isGearTooHigh() {
		return gearTooHigh;
	}

	public void setGearTooHigh(boolean gearTooHigh) {
		this.gearTooHigh = gearTooHigh;
	}

	public int getNosAmountLeft() {
		return nosAmountLeft;
	}

	public void setNosAmountLeft(int nosAmountLeft) {
		this.nosAmountLeft = nosAmountLeft;
	}

	public boolean isNOSON() {
		return NOSON;
	}

	public void setNOSON(boolean nOSON) {
		NOSON = nOSON;
	}

	public int getRpm() {
		return rpm;
	}

	public void setRpm(int rpm) {
		this.rpm = rpm;
	}

	public boolean isEngineOn() {
		return engineOn;
	}

	public void setEngineOn(boolean engineOn) {
		this.engineOn = engineOn;
		if (engineOn) {
			audio.openLines(hasTurbo, upgradedGears);
		} else {
			rpm = 0;
			audio.stopMotor();
			checkIdle();
		}
	}

	public boolean isIdle() {
		return idle;
	}

	public void setIdle(boolean idle) {
		this.idle = idle;
	}

	public double getResistance() {
		return resistance;
	}

	public void setResistance(double resistance) {
		this.resistance = resistance;
	}

	public double getGearsbalance() {
		return gearsbalance;
	}

	public void setGearsbalance(double gearsbalance) {
		this.gearsbalance = gearsbalance;
	}

	public boolean isUpgradedGears() {
		return upgradedGears;
	}

	public void setUpgradedGears(boolean upgradedGears) {
		this.upgradedGears = upgradedGears;
	}

	public double getCurrentWeight() {
		return currentWeight;
	}

	public void setCurrentWeight(double currentWeight) {
		this.currentWeight = currentWeight;
		weightloss = 0;
		totalWeight = currentWeight;
	}

	public void setCurrentWeight() {
		this.currentWeight = totalWeight - weightloss;
		weightloss = 0;
		totalWeight = currentWeight;
	}

	public int getHighestSpeedAchived() {
		return highestSpeedAchived;
	}

	public void setHighestSpeedAchived(int highestSpeedAchived) {
		this.highestSpeedAchived = highestSpeedAchived;
	}

	public boolean isGearBoostON() {
		return gearBoostON;
	}

	public void setGearBoostON(boolean gearBoostON) {
		this.gearBoostON = gearBoostON;
	}

	public double getGearBoostSTD() {
		return gearBoostSTD;
	}

	public void setGearBoostSTD(double gearBoostSTD) {
		this.gearBoostSTD = gearBoostSTD;
	}

	public Integer getUpgradeLVL(Integer LVL) {
		if (upgradeLVLs[LVL] == null)
			upgradeLVLs[LVL] = 0;
		return upgradeLVLs[LVL];
	}

	public void setUpgradeLVLs(Integer[] upgradeLVLs) {
		this.upgradeLVLs = upgradeLVLs;
	}

	public void iterateUpgradeLVL(Integer LVL) {
		if (upgradeLVLs[LVL] == null)
			upgradeLVLs[LVL] = 0;
		upgradeLVLs[LVL]++;
	}

	public Integer[] getUpgradeLVLs() {
		return upgradeLVLs;
	}

}
