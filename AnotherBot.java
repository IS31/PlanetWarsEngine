import java.util.*;

public class AnotherBot {

    public static void DoTurn(PlanetWars pw) {
        Planet source, dest;

        int enemyGrowthRate = getTotalGrowthRate(pw.EnemyPlanets());
        int myGrowthRate = getTotalGrowthRate(pw.MyPlanets());
        double proportion = myGrowthRate/enemyGrowthRate;

        source = largestPlanet(pw.MyPlanets());

        List<Planet> destCandidates;
        if (proportion > 1.5 && pw.NeutralPlanets().size()>0) destCandidates = pw.NeutralPlanets();
        else destCandidates = pw.EnemyPlanets();
        List<Planet> temp = immediatelyConquerable(source.NumShips()/2, destCandidates);
        if (temp.size() > 0) destCandidates = temp;
        else destCandidates = pw.EnemyPlanets();
        dest = highestGrowthRate(destCandidates);

        if (source != null && dest != null) {
            System.err.println("ORDER " + source.PlanetID() + " " + dest.PlanetID());
            pw.IssueOrder(source, dest);
        }
    }

    private static List<Planet> immediatelyConquerable(int fleetSize, List<Planet> destCandidates) {
        List<Planet> temp = new LinkedList<Planet>();
        for (Planet p : temp) {
            if (p.NumShips()<fleetSize) temp.add(p);
        }
        return temp;
    }

    private static int getTotalGrowthRate(List<Planet> planets) {
        int totalGrowthRate = 0;
        for (Planet p : planets) {
            totalGrowthRate += p.GrowthRate();
        }
        return totalGrowthRate;
    }

    private static Planet highestGrowthRate(List<Planet> planets) {
        Planet highestGrowthRate = planets.get(0);
        for (Planet p : planets) {
            if (p.NumShips() > highestGrowthRate.GrowthRate()) {
                highestGrowthRate = p;
            }
        }
        return highestGrowthRate;
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
            System.err.println("AnotherBot crashed");
            e.printStackTrace();
        }
    }
}
