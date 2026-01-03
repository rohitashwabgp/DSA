import java.util.*;
import java.util.stream.Collectors;



public enum OrderStatus {

    CREATED(1, "Order Created", ""),
    SHIPPED(2, "Order Shipped",""),
    DELIVERED(3, "Order Delivered","");

    private final int code;
    private final String description;
    private final String val;

    OrderStatus(int code, String description, String val) {
        this.code = code;
        this.description = description;
        this.val = val;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}

/* ===================== ENUM ===================== */

enum Game {
    FOOTBALL, CRICKET, LUDO, CS2
}

/* ===================== PLAYER ===================== */

class Player {
    private final String name;
    private String playerId;

    public Player(String name) {
        this.name = name;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player)) return false;
        Player player = (Player) o;
        return Objects.equals(playerId, player.playerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId);
    }
}

/* ===================== SESSION ===================== */

interface Session {
    String getSessionId();
    boolean join(Player player);
    boolean leave(Player player);
    boolean isActive();
    Game getRunningGame();
    int maxAllowed();
    Set<Player> getAllPlayers();
}

/* ===================== GAME SESSION ===================== */

class GameSession implements Session {

    private final int maxAllowed;
    private final String sessionId;
    private Game gameRunning;
    private final Set<Player> players = new HashSet<>();
    private boolean active;
    private long lastActivityTime;

    public GameSession(int maxAllowed, String sessionId, Game game) {
        this.maxAllowed = maxAllowed;
        this.sessionId = sessionId;
        this.gameRunning = game;
        this.active = true;
        this.lastActivityTime = System.currentTimeMillis();
    }

    private void touch() {
        lastActivityTime = System.currentTimeMillis();
    }

    public boolean isExpired(long ttlMillis) {
        return System.currentTimeMillis() - lastActivityTime > ttlMillis;
    }

    @Override
    public boolean join(Player player) {
        if (!active || players.size() == maxAllowed || players.contains(player)) {
            return false;
        }
        players.add(player);
        touch();
        return true;
    }

    @Override
    public boolean leave(Player player) {
        boolean removed = players.remove(player);
        if (removed) {
            touch();
        }
        return removed;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        active = false;
    }

    @Override
    public Game getRunningGame() {
        return gameRunning;
    }

    @Override
    public int maxAllowed() {
        return maxAllowed;
    }

    @Override
    public Set<Player> getAllPlayers() {
        return players;
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }
}

/* ===================== SESSION MANAGER ===================== */

interface SessionManager {
    Session createSession(Game game);
    boolean joinSession(String sessionId, String playerId);
    boolean leaveSession(String sessionId, String playerId);
    List<Session> getActiveSessions();
}

/* ===================== GAME SESSION MANAGER ===================== */

class GameSessionManager implements SessionManager {

    private static final long SESSION_TTL_MS = 5 * 60 * 1000; // 5 minutes
    private final Set<GameSession> sessions = new HashSet<>();

    /* ---------- Lazy Eviction ---------- */
    private void evictExpiredSessions() {
        sessions.removeIf(session -> session.isExpired(SESSION_TTL_MS));
    }

    @Override
    public Session createSession(Game game) {
        evictExpiredSessions();
        GameSession session = new GameSession(
                10,
                "session-" + UUID.randomUUID(),
                game
        );
        sessions.add(session);
        return session;
    }

    @Override
    public boolean joinSession(String sessionId, String playerId) {
        evictExpiredSessions();

        Optional<GameSession> sessionOpt = sessions.stream()
                .filter(s -> s.getSessionId().equals(sessionId))
                .findFirst();

        if (sessionOpt.isEmpty()) return false;
        if (isInOtherSession(sessionId, playerId)) return false;

        Player player = new Player("Mock");
        player.setPlayerId(playerId);

        return sessionOpt.get().join(player);
    }

    private boolean isInOtherSession(String sessionId, String playerId) {
        return sessions.stream().anyMatch(session ->
                !session.getSessionId().equals(sessionId) &&
                        session.getAllPlayers().stream()
                                .anyMatch(p -> p.getPlayerId().equals(playerId))
        );
    }

    @Override
    public boolean leaveSession(String sessionId, String playerId) {
        evictExpiredSessions();

        Optional<GameSession> sessionOpt = sessions.stream()
                .filter(s -> s.getSessionId().equals(sessionId))
                .findFirst();

        if (sessionOpt.isEmpty()) return false;

        GameSession session = sessionOpt.get();

        Optional<Player> playerOpt = session.getAllPlayers().stream()
                .filter(p -> p.getPlayerId().equals(playerId))
                .findFirst();

        return playerOpt.isPresent() && session.leave(playerOpt.get());
    }

    @Override
    public List<Session> getActiveSessions() {
        evictExpiredSessions();
        return sessions.stream()
                .filter(GameSession::isActive)
                .collect(Collectors.toList());
    }
}
