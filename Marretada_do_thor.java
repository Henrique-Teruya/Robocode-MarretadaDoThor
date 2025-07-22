package MarretadaDoThor;
import robocode.*;
import java.awt.Color;

public class Marretada_do_thor extends AdvancedRobot {
	private int safeDirection = 1;
	private double enemyBearing;
	private double enemyDistance;
	private double enemyHeading;
	private double enemyVelocity;
	private boolean movingForward = true;

	public void run() {
		setColors(Color.red, Color.black, Color.yellow);
		setAdjustRadarForGunTurn(true);
		setAdjustGunForRobotTurn(true);

		while (true) {
			setTurnRadarRight(360);
			execute();
		}
	}

	public void onScannedRobot(ScannedRobotEvent e) {
		enemyBearing = e.getBearing();
		enemyDistance = e.getDistance();
		enemyHeading = e.getHeading();
		enemyVelocity = e.getVelocity();

		// Travar radar no inimigo
		double radarTurn = getHeading() + enemyBearing - getRadarHeading();
		setTurnRadarRight(normalizeBearing(radarTurn));

		// Apontar o canhão
		double absoluteBearing = getHeading() + enemyBearing;
		double gunTurn = normalizeBearing(absoluteBearing - getGunHeading());
		setTurnGunRight(gunTurn);

		// Manter 100 de distância
		double spacing = enemyDistance - 100;
		if (Math.abs(spacing) > 10) {
			if (spacing > 0) {
				setAhead(spacing);
				movingForward = true;
			} else {
				setBack(-spacing);
				movingForward = false;
			}
			setTurnRight(enemyBearing);
		} else {
			setAhead(0);
			setTurnRight(0);
		}

		// Atirar com força proporcional à distância
		if (enemyDistance < 100) {
			fire(3);
		} else if (enemyDistance < 200) {
			fire(2);
		} else {
			fire(1);
		}

		execute();
	}

	public void onHitWall(HitWallEvent e) {
		if (movingForward) {
			setBack(100);
			movingForward = false;
		} else {
			setAhead(100);
			movingForward = true;
		}
		execute();
	}

	public void onHitByBullet(HitByBulletEvent e) {
		// Move-se lateralmente ao ser atingido
		setTurnRight(normalizeBearing(enemyBearing + 90 * safeDirection));
		setAhead(50);
		movingForward = true;
	}

	public void onHitRobot(HitRobotEvent e) {
		// Inverte direção ao colidir com outro robô
		safeDirection *= -1;
		setTurnRight(normalizeBearing(e.getBearing() + 90 * safeDirection));
		setAhead(50);
		movingForward = true;
	}

	private double normalizeBearing(double angle) {
		while (angle > 180) angle -= 360;
		while (angle < -180) angle += 360;
		return angle;
	}
}
