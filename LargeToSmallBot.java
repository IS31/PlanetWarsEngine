import java.util.*;

/*
 RandomBot - an example bot that picks up one of his planets and send half of the ships 
 from that planet to a random target planet.

 Not a very clever bot, but showcases the functions that can be used.
 Overcommented for educational purposes.
 */
public class LargeToSmallBot {

  /*
	 * Function that gets called every turn. This is where to implement the strategies.
	 */

	public static void DoTurn(PlanetWars pw) {

		Planet source, dest;

		source = largestPlanet(pw.MyPlanets());
		dest = smallestPlanet(pw.EnemyPlanets());

		if (source != null && dest != null) {
			System.err.println("ORDER " + source.PlanetID() + " " + dest.PlanetID());
			pw.IssueOrder(source, dest);
		}
	}

	private static Planet largestPlanet(List<Planet> planets) {
		Planet largestPlanet = planets.get(0);
		for (Planet p : planets) {
			if (p.NumShips() > largestPlanet.NumShips()) {
				largestPlanet = p;
			}
		}
		return largestPlanet;
	}

	private static Planet smallestPlanet(List<Planet> planets) {
		Planet smallestPlanet = planets.get(0);
		for (Planet p : planets) {
			if (p.NumShips() < smallestPlanet.NumShips()) {
				smallestPlanet = p;
			}
		}
		return smallestPlanet;
	}

	public static void main(String[] args) {
		String line = "";
		String message = "";
		int c;
		try {
			while ((c = System.in.read()) >= 0) {
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
		}
	}
}
