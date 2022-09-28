package de.ronnywalter.eve.service;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;
import de.ronnywalter.eve.config.EveOnlineApi20;
import de.ronnywalter.eve.exception.TokenNotFoundException;
import de.ronnywalter.eve.model.Token;
import de.ronnywalter.eve.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {

    private final TokenRepository tokenRepository;
    private final EveOnlineApi20 eveOnlineApi20;

    public Token getToken(int characterId) {
        return tokenRepository.findById(characterId).orElse(null);
    }

    public Token saveToken(Token token) {
        return tokenRepository.save(token);
    }

    public String getAccessToken(int characterId) throws TokenNotFoundException {
        Token t = tokenRepository.findById(characterId).orElse(null);
        if(t == null) {
            throw new TokenNotFoundException("Token for " + characterId + " not found.");
        }
        return refreshAndSaveToken(t);
    }

    private String refreshAndSaveToken(Token token) {
        if(token != null) {
            LocalDateTime expiryDate = token.getExpiryDate();
            LocalDateTime now = LocalDateTime.now();
            if (now.isAfter(expiryDate)) {
                try {
                    OAuth20Service service = new ServiceBuilder(token.getClientId())
                            .apiSecret(token.getClientSecret())
                            //.callback(eveEsiProperties.getRedirectUrl())
                            //.defaultScope(sb)
                            .build(eveOnlineApi20);

                    OAuth2AccessToken accessToken = service.refreshAccessToken(token.getRefreshToken());
                    /*DecodedJWT decodedJWT = JWT.decode(accessToken.getAccessToken());
                    JwkProvider provider = new UrlJwkProvider(URI.create("https://login.eveonline.com/oauth/jwks").toURL());
                    Jwk jwk = provider.get(decodedJWT.getKeyId());
                    Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);
                    algorithm.verify(decodedJWT);
                    */

                    token.setAccessToken(accessToken.getAccessToken());
                    token.setRefreshToken(accessToken.getRefreshToken());
                    token.setExpiryDate(LocalDateTime.now().plusSeconds(accessToken.getExpiresIn()));
                    tokenRepository.save(token);
                } catch (IOException e) {
                    log.error("Error refreshing Token: " + e.getMessage(), e);
                } catch (InterruptedException e) {
                    log.error("Error refreshing Token: " + e.getMessage(), e);
                } catch (ExecutionException e) {
                    log.error("Error refreshing Token: " + e.getMessage(), e);
                }
            }

            return token.getAccessToken();
        } else {
            return null;
        }
    }
}
