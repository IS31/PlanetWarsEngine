
import java.util.*;



public class SimulatedPlanetWars {
	
	List<Planet> planets = new ArrayList<Planet>();

    public SimulatedPlanetWars() {

    }

    public SimulatedPlanetWars(PlanetWars pw) {

        for (Planet planet: pw.Planets()){
            planets.add(planet);
        }
    }

    public SimulatedPlanetWars(SimulatedPlanetWars pw) {

        for (Planet planet: pw.Planets()){
            planets.add(planet);
        }
    }
    
	public void simulateGrowth() {
		for (Planet p: planets) {
			if (p.Owner() == 0) continue;
			Planet newp = new Planet(p.PlanetID(), p.Owner(), p.NumShips() + p.GrowthRate(), p.GrowthRate(), p.X(), p.Y());
			planets.set(p.PlanetID(), newp);
		}
	}
	public void simulateAttack(int player, Planet source, Planet dest) {
		if (source.Owner() != player) {
			return;
		}
		if (source != null && dest != null) {
			Planet newSource = new Planet(source.PlanetID(), source.Owner(), source.NumShips() / 2, source.GrowthRate(), source.X(), source.Y());
			Planet newDest = new Planet(dest.PlanetID(), dest.Owner(), Math.abs(dest.NumShips() - source.NumShips() / 2), dest.GrowthRate(), dest.X(), dest.Y());
			if (dest.NumShips() < source.NumShips() / 2) {
				newDest.Owner(player);
			}
			planets.set(source.PlanetID(), newSource);
			planets.set(dest.PlanetID(), newDest);
		}
	}
	public void simulateAttack(Planet source, Planet dest) {
		simulateAttack(1, source, dest);
	}
	public List < Planet > Planets() {
		return planets;
	}
	public int NumPlanets() {
		return planets.size();
	}
	public Planet GetPlanet(int planetID) {
		return planets.get(planetID);
	}
	public List < Planet > MyPlanets() {
		List < Planet > r = new ArrayList < Planet > ();
		for (Planet p: planets) {
			if (p.Owner() == 1) {
				r.add(p);
			}
		}
		return r;
	}
	public List < Planet > NeutralPlanets() {
		List < Planet > r = new ArrayList < Planet > ();
		for (Planet p: planets) {
			if (p.Owner() == 0) {
				r.add(p);
			}
		}
		return r;
	}
	public List < Planet > EnemyPlanets() {
		List < Planet > r = new ArrayList < Planet > ();
		for (Planet p: planets) {
			if (p.Owner() >= 2) {
				r.add(p);
			}
		}
		return r;
	}
/*	public class PlanetKey {
		Integer key;
		Planet planet;
		PlanetKey(Integer key, Planet planet) {
			this.key = key;
			this.planet = planet;
		}
	}

	public class MyPlanetKeyComparable implements Comparator < PlanetKey > {
		public int compare(PlanetKey o1, PlanetKey o2) {
			return (o1.key > o2.key ? -1 : (o1.key == o2.key ? 0 : 1));
		}
	}
	// Return a list of n planets that are not owned near a 
	// owned one. This includes all enemy planets and neutral planets.
	public List < Planet > NeighborsPlanets(Planet planet, int number) {
		List < Planet > notMyPlanets = NotMyPlanets();
		if (number > notMyPlanets.size()) return notMyPlanets;
		List < PlanetKey > planetKey = new ArrayList < PlanetKey > ();
		List < Planet > result = new ArrayList < Planet > ();
		List < Integer > distances = new ArrayList < Integer > ();
		for (Planet p: notMyPlanets) {
			PlanetKey aux = new PlanetKey(Distance(planet, p), planet);
			planetKey.add(aux);
		}
		List < Integer > distancesKeys = new ArrayList < Integer > (distances);
		Collections.sort(planetKey, new MyPlanetKeyComparable());
		for (int i = 0; i <= number; i++) {
			result.add(planetKey.get(i).planet);
		}
		return result;
	}*/
	public List < Planet > NotMyPlanets() {
		List < Planet > r = new ArrayList < Planet > ();
		for (Planet p: planets) {
			if (p.Owner() != 1) {
				r.add(p);
			}
		}
		return r;
	}
	public List < Planet > NotEnemyPlanets() {
		List < Planet > r = new ArrayList < Planet > ();
		for (Planet p: planets) {
			if (p.Owner() != 2) {
				r.add(p);
			}
		}
		return r;
	}
	public int Distance(int sourcePlanet, int destinationPlanet) {
		Planet source = planets.get(sourcePlanet);
		Planet destination = planets.get(destinationPlanet);
		double dx = source.X() - destination.X();
		double dy = source.Y() - destination.Y();
		return (int) Math.ceil(Math.sqrt(dx * dx + dy * dy));
	}
	public int Winner() {
		Set < Integer > remainingPlayers = new TreeSet < Integer > ();
		for (Planet p: planets) {
			remainingPlayers.add(p.Owner());
		}
		switch (remainingPlayers.size()) {
		case 0:
			return 0;
		case 1:
			return ((Integer) remainingPlayers.toArray()[0]).intValue();
		default:
			return -1;
		}
	}
	public int NumShips(int playerID) {
		int numShips = 0;
		for (Planet p: planets) {
			if (p.Owner() == playerID) {
				numShips += p.NumShips();
			}
		}
		return numShips;
	}
	public void IssueOrder(Planet source, Planet dest) {
		simulateAttack(source, dest);
	}
}