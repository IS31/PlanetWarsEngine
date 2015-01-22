import java.util.*;

/*
 RandomBot - an example bot that picks up one of his planets and send half of the ships 
 from that planet to a random target planet.

 Not a very clever bot, but showcases the functions that can be used.
 Overcommented for educational purposes.
 */
public class EmptyBot {

	/*
	 * Function that gets called every turn. This is where to implement the strategies.
	 */

	public static void DoTurn(PlanetWars pw) {

		Planet source = null;
		Planet dest = null;

		//find strongest enemy planet
		double sourceScore = Double.MIN_VALUE;
		for (Planet myPlanet : pw.MyPlanets()) {
			//This score is one way of defining how 'good' my planet is. 
			double score = (double) myPlanet.NumShips() / (1 + myPlanet.GrowthRate());
			if (score > sourceScore) {
				//we want to maximize the score, so store the planet with the best score
				sourceScore = score;
				source = myPlanet;
			}
		}
		
		//determine the weakest enemy planet
		double destScore = Double.MIN_VALUE;
		
		for (Planet planetOfEnemy : pw.EnemyPlanets()) {
			System.out.println("test" + destScore + "ah yea :)");
			double score = (double) (1 + planetOfEnemy.GrowthRate()) / planetOfEnemy.NumShips();
			if (score > destScore) {
				destScore = score;
				dest = planetOfEnemy;
			}
		}
		

		//determine my source planet which is just strong enough to kill this planet
		
		/*
		double lowestAmount = Double.MAX_VALUE;

		for (Planet myPlanet : pw.MyPlanets()) {
			if (myPlanet.NumShips() > dest.NumShips() && myPlanet.NumShips() < lowestAmount){
				lowestAmount = myPlanet.NumShips();
				source = myPlanet;
			}
		}
		*/

		// (3) Send half the ships from source to destination
		if (source != null && dest != null) {
			pw.IssueOrder(source, dest);
		}
		
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
