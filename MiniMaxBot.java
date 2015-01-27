import java.io.PrintWriter;
import java.io.StringWriter;
public class MiniMaxBot {
	public static class Planets {
		Planet source = null;
		Planet dest = null;
	}
	public static void DoTurn(PlanetWars pw) {
		Planets planets = miniMax(createSimulation(pw), pw.NumPlanets() / 2);
		if (planets.source != null && planets.dest != null) {
			pw.IssueOrder(planets.source, planets.dest);
		}
	}
	public static Planets miniMax(SimulatedPlanetWars pw, int depth) {
		Planets planets = new Planets();
		double score = Double.MIN_VALUE;
		depth = (depth % 2 == 0) ? depth + 1 : depth;
		depth = (depth < 2) ? 2 : depth;
		for (Planet source: pw.MyPlanets()) {
			for (Planet dest: pw.NotMyPlanets()) {
				SimulatedPlanetWars simpw = simResult(pw, source, dest);
				double scoreMax = minimaxValue(simpw, depth - 1);
				if (scoreMax > score) {
					score = scoreMax;
					planets.source = source;
					planets.dest = dest;
				}
			}
		}
		return planets;
	}
	public static double minimaxValue(SimulatedPlanetWars pw, int depth) {
		if (depth == 0) {
			return evaluateState(pw);
		}
		double score;
		score = (depth % 2 == 0) ? Double.MIN_VALUE : Double.MAX_VALUE;
		for (Planet source: pw.MyPlanets()) {
			for (Planet dest: pw.NotMyPlanets()) {
				SimulatedPlanetWars simpw = simResult(pw, source, dest);
				if ((depth % 2) == 0) {
					score = Math.max(score, minimaxValue(simpw, depth - 1));
				} else {
					score = Math.min(score, minimaxValue(simpw, depth - 1));
				}
			}
		}
		return score;
	}
	public static SimulatedPlanetWars simResult(SimulatedPlanetWars pw, Planet source, Planet dest) {
		SimulatedPlanetWars simpw = new SimulatedPlanetWars(pw);
		simpw.simulateAttack(source, dest);
		simpw.simulateGrowth();
		return simpw;
	}
	public static double evaluateState(SimulatedPlanetWars pw) {
		double enemyShips = 0;
		double myShips = 0;
		for (Planet planet: pw.EnemyPlanets()) {
			enemyShips += planet.NumShips();
		}
		for (Planet planet: pw.MyPlanets()) {
			myShips += planet.NumShips();
		}
		return myShips - enemyShips;
	}
	public static void main(String[] args) {
		String line = "";
		String message = "";
		int c;
		try {
			while ((c = System. in .read()) >= 0) {
				switch (c) {
				case '\n':
					if (line.equals("go")) {
						PlanetWars pw = new PlanetWars(message);
						DoTurn(pw);
						pw.FinishTurn();
						message = "";
					} else {
						message += line + "\n";
					}
					line = "";
					break;
				default:
					line += (char) c;
					break;
				}
			}
		} catch (Exception e) {
			StringWriter writer = new StringWriter();
			e.printStackTrace(new PrintWriter(writer));
			String stackTrace = writer.toString();
			System.err.println(stackTrace);
			System.exit(1);
		}
	}
	public static SimulatedPlanetWars createSimulation(PlanetWars pw) {
		SimulatedPlanetWars result = new SimulatedPlanetWars(pw);
		return result;
	}
}