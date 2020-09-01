/*
 * GitignoreGenerator
 * https://github.com/sandsbit/GitignoreGenerator
 *
 * Copyright (C) 2020 Nikita Serba. All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.nikitaserba.GitignoreGenerator.api.sources;

import me.nikitaserba.GitignoreGenerator.api.exceptions.GitignoreSourceLoadException;
import me.nikitaserba.GitignoreGenerator.api.exceptions.TemplateParsingException;
import me.nikitaserba.GitignoreGenerator.api.templates.GitignoreTemplate;
import me.nikitaserba.GitignoreGenerator.api.templates.TemplateType;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;

/**
 * Gitignore source that parses templates from https://github.com/github/gitignore/.
 *
 * This class is final to allow java use some optimizations. This
 * tag will be removed if it's needed to inherit any class from
 * this class.
 *
 * @version 1.0
 */
public final class GithubRepoSource implements GitignoreSource {

    public static final String NAME = "Github Gitignore repo source";

    @Override
    public String getName() {
        return NAME;
    }

    public static final long REQUEST_TIMEOUT_SECONDS = 10;

    protected static final String GH_API_REPO_ROOT_CONTENT_URL
            = "https://api.github.com/repos/github/gitignore/contents/";

    protected HttpClient httpClient;

    public GithubRepoSource() {
        httpClient = HttpClient.newHttpClient();
    }

    /**
     * Make GET request and parse result as an JSON array.
     *
     * @param URL url that will be used in GET request.
     * @return parsed JSONArray
     * @throws URISyntaxException if URL is invalid.
     * @throws IOException if failed to make an request.
     * @throws InterruptedException if making an request was interrupted.
     */
    protected JSONArray parsePageByURL(String URL)
            throws URISyntaxException, IOException,InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(URL))
                .GET()
                .timeout(Duration.ofSeconds(REQUEST_TIMEOUT_SECONDS))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        return new JSONArray(response.body());
    }

    /**
     * Get recursively all files from all folders on github repo excluding
     * folders that begin with ".".
     *
     * @return set with all files on github .gitignore repo.
     * @throws URISyntaxException if URL is invalid.
     * @throws IOException if failed to make an request.
     * @throws InterruptedException if making an request was interrupted.
     * @throws GitignoreSourceLoadException in response from GitHub API in invalid.
     */
    protected Set<JSONObject> getAllTemplatesJSONFromGithub()  // TODO: encapsulate file, split into several methods
            throws URISyntaxException, IOException, InterruptedException, GitignoreSourceLoadException {
        Queue<String> paths = new ArrayDeque<>();
        paths.offer("");

        Set<JSONObject> templatesJSON = new HashSet<>();

        while (!paths.isEmpty()) {
            String path = paths.poll();
            JSONArray parsedPage = parsePageByURL(GH_API_REPO_ROOT_CONTENT_URL + path);
            for (Object fileRaw : parsedPage) {
                try {
                    JSONObject file = (JSONObject) fileRaw;
                    String type = file.getString("type");

                    switch (type) {
                        case "dir":
                            if (!file.getString("name").startsWith("."))
                                paths.offer(file.getString("path"));
                            break;
                        case "file":
                            templatesJSON.add(file);
                            break;
                        default:
                            throw new GitignoreSourceLoadException("Invalid type of file: " + type);
                    }
                } catch (ClassCastException | JSONException e) {
                    throw new GitignoreSourceLoadException("Invalid response from GitHub API", e);
                }
            }
        }

        return templatesJSON;
    }

    @Override
    public Set<Source> getAllSources() throws TemplateParsingException {
        return null;
    }

    @Override
    public Set<Source> getSourcesByType(TemplateType type) throws TemplateParsingException {
        return null;
    }

    @Override
    public GitignoreTemplate parse(Source source) throws TemplateParsingException {
        return null;
    }

    @Override
    public Set<GitignoreTemplate> parseAll() throws TemplateParsingException {
        return null;
    }
}
