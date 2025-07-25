package GraphCreation;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Edge {
    private String from;
    private String to;
    private Set<String> contextHashes = new HashSet<>();

    public Edge(String from, String to) {
        this.from = from;
        this.to = to;
    }

    public void addHash(String hash) {
        contextHashes.add(hash);
    }

    public static String computeSHA256(List<String> tokens) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String input = String.join(" ", tokens);
            byte[] hashBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Set<String> getContextHashes() {
        return contextHashes;
    }

    public void setContextHashes(Set<String> contextHashes) {
        this.contextHashes = contextHashes;
    }
}