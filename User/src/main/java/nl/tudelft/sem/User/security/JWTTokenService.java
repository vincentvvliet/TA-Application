package nl.tudelft.sem.User.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.compression.GzipCompressionCodec;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static io.jsonwebtoken.impl.TextCodec.BASE64;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.substringBeforeLast;

/**
 * Creates and validates credentials.
 */
@Service
public class JWTTokenService implements Clock {
    private static final GzipCompressionCodec COMPRESSION_CODEC = new GzipCompressionCodec();

    String issuer;
    int expirationSec;
    int clockSkewSec;
    SecretKey secretKey;

    /**
     * Instantiates a new Jwt token service.
     *
     * @param issuer        the issuer
     * @param expirationSec the expiration sec
     * @param clockSkewSec  the clock skew sec
     * @param secret        the secret
     */
    JWTTokenService(@Value("${jwt.issuer:sem-group-17a}") final String issuer,
                    @Value("${jwt.expiration-sec:86400}") final int expirationSec,
                    @Value("${jwt.clock-skew-sec:300}") final int clockSkewSec,
                    @Value("${jwt.secret:secret}") final String secret) {
        super();
        this.issuer = requireNonNull(issuer);
        this.expirationSec = expirationSec;
        this.clockSkewSec = clockSkewSec;
        this.secretKey = Keys.secretKeyFor(HS256);
    }

    /**
     * Create a permanent token.
     *
     * @param attributes attributes of the token
     * @return token
     */
    public String permanent(final Map<String, String> attributes) {
        return newToken(attributes, 0);
    }

    /**
     * Create an expiring token.
     *
     * @param attributes attributes of the token
     * @return token
     */
    public String expiring(final Map<String, String> attributes) {
        return newToken(attributes, expirationSec);
    }

    /**
     * Create a new token.
     *
     * @param attributes   attributes of the token
     * @param expiresInSec time to expire
     * @return token
     */
    private String newToken(final Map<String, String> attributes, final int expiresInSec) {
        final DateTime now = DateTime.now();
        final Claims claims = Jwts
                .claims()
                .setIssuer(issuer)
                .setIssuedAt(now.toDate());

        if (expiresInSec > 0) {
            final DateTime expiresAt = now.plusSeconds(expiresInSec);
            claims.setExpiration(expiresAt.toDate());
        }
        claims.putAll(attributes);

//        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
//        Key key = Keys.secretKeyFor(HS256);

        return Jwts
                .builder()
                .setClaims(claims)
                .signWith(secretKey, HS256)
                .compressWith(COMPRESSION_CODEC)
                .compact();
    }

    /**
     * Checks the validity of the given credentials.
     *
     * @param token token
     * @return attributes if verified
     */
    public Map<String, String> verify(final String token) {
        final JwtParser parser = Jwts
                .parserBuilder().build()
                .requireIssuer(issuer)
                .setClock(this)
                .setAllowedClockSkewSeconds(clockSkewSec)
                .setSigningKey(secretKey);
        return parseClaims(() -> parser.parseClaimsJws(token).getBody());
    }

    /**
     * Checks the validity of the given credentials (without signing).
     *
     * @param token token
     * @return attributes if verified
     */
    public Map<String, String> untrusted(final String token) {
        final JwtParser parser = Jwts
                .parser()
                .requireIssuer(issuer)
                .setClock(this)
                .setAllowedClockSkewSeconds(clockSkewSec);

        final String withoutSignature = substringBeforeLast(token, ".") + ".";
        return parseClaims(() -> parser.parseClaimsJwt(withoutSignature).getBody());
    }

    /**
     * Parse the given claims.
     *
     * @param toClaims claims
     * @return token
     */
    private static Map<String, String> parseClaims(final Supplier<Claims> toClaims) {
        try {
            final Claims claims = toClaims.get();
            final Map<String, String> builder = new HashMap<>();
            for (final Map.Entry<String, Object> e : claims.entrySet()) {
                builder.put(e.getKey(), String.valueOf(e.getValue()));
            }
            return builder;
        } catch (final IllegalArgumentException | JwtException e) {
            return Map.of();
        }
    }

    /**
     * Get the current time.
     *
     * @return current time
     */
    @Override
    public Date now() {
        final DateTime now = DateTime.now();
        return now.toDate();
    }
}
