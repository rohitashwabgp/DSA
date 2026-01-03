import java.util.*;

public class CheapestFlightPath {
    static class Edge {
        String to;
        int cost;

        Edge(String to, int cost) {
            this.to = to;
            this.cost = cost;
        }
    }


    public static String findCheapestPath(List<String[]> flights, String source, String destination) {
        Map<String, List<Edge>> adjacent = new HashMap<>();
        for (String[] flight : flights) {
            adjacent.computeIfAbsent(flight[0], (k) -> new ArrayList<>()).add(new Edge(flight[1], Integer.parseInt(flight[2])));
        }
        PriorityQueue<Edge> que = new PriorityQueue<>(Comparator.comparingInt(edge -> edge.cost));
        que.offer(new Edge(source, 0));
        Map<String, String> parents = new HashMap<>();
        Map<String, Integer> dist = new HashMap<>();
        dist.put(source, 0);


        while (!que.isEmpty()) {
            Edge current = que.poll();
            if (current.cost > dist.get(current.to)) continue;
            if (current.to.equals(destination)) break;
            for (Edge edge : adjacent.get(current.to)) {
                int currentCost = current.cost + edge.cost;

                if (currentCost < dist.getOrDefault(edge.to, Integer.MAX_VALUE)) {
                    que.offer(new Edge(edge.to, currentCost));
                    dist.put(edge.to, currentCost);
                    parents.put(edge.to, current.to);
                }
            }
        }
        String target = destination;
        String out = "";
        while (true) {
            if (target.equals(destination)) {
                out = target.concat(out);
            } else {
                out = target.concat(":").concat(out);
            }
            if (target.equals(source)) break;
            target = parents.get(target);
        }
        return out;
    }

    public static void main(String[] args) {

        List<String[]> flights = List.of(new String[]{"A", "B", "100"}, new String[]{"A", "C", "300"}, new String[]{"B", "C", "100"}, new String[]{"C", "D", "200"}, new String[]{"B", "D", "400"});

        String path = CheapestFlightPath.findCheapestPath(flights, "A", "D");

        System.out.println("Path = " + path);
    }
}
