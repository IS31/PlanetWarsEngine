import java.util.*;
public class LessStupiderBot {
    // evolving values , don't adjust manually
    final static double
            SIZE_WEIGHT=1.1,
            NUM_PLAN_WEIGHT=1.1,
            GROWTH_WEIGHT=1.1,
            MAY_LOSE_PLANET_WEIGHT=1.1,
            NEUTRAL_WEIGHT=1.1;
    // end of evolving values

    final static int NEUTRAL_ID = 0;

  /*
	 * Function that gets called every turn. This is where to implement the strategies.
	 */

    public static void DoTurn(PlanetWars pw) {

        int my_id = pw.MyPlanets().get(0).Owner();
        int enemy_id = (my_id == 1) ? 2 : 1;

        Planet source = null;
        Planet dest = null;

        //   if (staleMate(pw)){
        //      handleStaleMate(pw);
        //} else {

        double possibleStateDesirability;

        double maxDesirability = -Double.MAX_VALUE;
        for (Planet p : pw.MyPlanets()) {
            for (Planet l : pw.Planets()) {
                if (l == p) continue;
                possibleStateDesirability = desirabilityOfNextTurn(my_id, enemy_id, p, l, pw);
                if (maxDesirability < possibleStateDesirability) {
                    maxDesirability = possibleStateDesirability;
                    source = p;
                    dest = l;
                }
            }
        }
        //    }

        if (source != null && dest != null) {
            System.err.println("ORDER " + source.PlanetID() + " " + dest.PlanetID());
            pw.IssueOrder(source, dest);
        }
    }

    private static boolean staleMate(PlanetWars pw) {
        return (
                pw.MyPlanets().size() == 1
                        && pw.EnemyPlanets().size() == 1
                        && largestPlanet(pw.MyPlanets()).NumShips() == 5
                        && largestPlanet(pw.EnemyPlanets()).NumShips() == 5
        );
    }

    private static double desirabilityOfNextTurn(int my_id, int enemy_id, Planet source, Planet dest,PlanetWars pw){

        int enemyGrowthRate = getTotalGrowthRate(pw.EnemyPlanets());
        int myGrowthRate = getTotalGrowthRate(pw.MyPlanets());

        int enemyTotalSize = getTotalSize(pw.EnemyPlanets());
        int myTotalSize = getTotalSize(pw.MyPlanets());

        int enemyNumPlanets = pw.EnemyPlanets().size();
        int myNumPlanets = pw.MyPlanets().size();
        int fleetSize = source.NumShips()/2;

        if (dest.Owner() == my_id) {

        } else if (dest.Owner() == enemy_id){
            if (dest.NumShips()>fleetSize) {
                myTotalSize -= fleetSize;
                enemyTotalSize -= fleetSize;
            } else {
                myTotalSize -= dest.NumShips();
                enemyTotalSize -= dest.NumShips();
                myGrowthRate += dest.GrowthRate();
                enemyGrowthRate -= dest.GrowthRate();
                myNumPlanets++;
                enemyNumPlanets--;
            }
        } else { // neutral
            if (dest.NumShips()>fleetSize) {
                myTotalSize -= fleetSize;
            } else {
                myTotalSize -= dest.NumShips();
                myGrowthRate += dest.GrowthRate();
                myNumPlanets++;
            }
        }

        if (enemyTotalSize < 1) return Double.MAX_VALUE;
        else {
            myTotalSize += myGrowthRate;
            enemyTotalSize += enemyGrowthRate;
            double sizeFactor = myTotalSize / enemyTotalSize;
            double numPlanetsFactor = myNumPlanets / enemyNumPlanets;
            double growthFactor = myGrowthRate / enemyGrowthRate;

            double score =
                    (
                            sizeFactor*SIZE_WEIGHT
                                    + numPlanetsFactor*NUM_PLAN_WEIGHT
                                    + growthFactor*GROWTH_WEIGHT
                    )   / 3;

            if (mayLosePlanet(pw,dest,source,fleetSize)) score *= MAY_LOSE_PLANET_WEIGHT;
            if (dest.Owner() == NEUTRAL_ID) score *= NEUTRAL_WEIGHT;
            return score;
        }
    }

    private static boolean mayLosePlanet(PlanetWars pw, Planet dest, Planet source, int fleetSize){
        return largestFleet(pw.EnemyPlanets(),dest,fleetSize) > Math.min(smallestPlanet(pw.MyPlanets()).NumShips(),(source.NumShips()/2)+source.GrowthRate());

    }

    private static int largestFleet(List<Planet> planets, Planet dest, int reduction) {
        int largestFleet = 0;
        int thisFleet;
        for (Planet p : planets){
            if (p==dest) thisFleet = ( dest.NumShips()-reduction+dest.GrowthRate() ) / 2;
            else thisFleet = (dest.NumShips()+dest.GrowthRate())/2;
            if (thisFleet>largestFleet) largestFleet = thisFleet;
        }
        return largestFleet;
    }


    private static int getTotalSize(List<Planet> planets) {
        int totalSize = 0;
        for (Planet p : planets) {
            totalSize += p.NumShips();
        }
        return totalSize;
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
            e.printStackTrace();
        }
    }
}
