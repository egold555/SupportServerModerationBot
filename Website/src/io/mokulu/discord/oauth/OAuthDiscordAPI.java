package io.mokulu.discord.oauth;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.jsoup.Jsoup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.mokulu.discord.oauth.model.OAuthConnection;
import io.mokulu.discord.oauth.model.OAuthGuild;
import io.mokulu.discord.oauth.model.OAuthUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class OAuthDiscordAPI
{
    public static final String BASE_URI = "https://discord.com/api";
    private static final Gson gson = new GsonBuilder().serializeNulls().enableComplexMapKeySerialization().create();
    private final String accessToken;

    private static <T> T toObject(String str, Class<T> clazz)
    {
        return gson.fromJson(str, clazz);
    }

    private void setHeaders(org.jsoup.Connection request) throws IOException
    {
        request.header("Authorization", "Bearer " + accessToken);
        request.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0");
    }

    private String handleGet(String path) throws IOException
    {
        org.jsoup.Connection request = Jsoup.connect(BASE_URI + path).ignoreContentType(true);
        setHeaders(request);

        return request.get().body().text();
    }

    public OAuthUser fetchUser() throws IOException
    {
        return toObject(handleGet("/users/@me"), OAuthUser.class);
    }

    public List<OAuthGuild> fetchGuilds() throws IOException
    {
        return Arrays.asList(toObject(handleGet("/users/@me/guilds"), OAuthGuild[].class));
    }

    public List<OAuthConnection> fetchConnections() throws IOException
    {
        return Arrays.asList(toObject(handleGet("/users/@me/connections"), OAuthConnection[].class));
    }
}
