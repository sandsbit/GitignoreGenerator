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

import me.nikitaserba.GitignoreGenerator.api.exceptions.TemplateParsingException;
import me.nikitaserba.GitignoreGenerator.api.templates.GitignoreTemplate;
import me.nikitaserba.GitignoreGenerator.api.templates.TemplateType;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * .gitignore source that uses templates from "templates" folder in resources.
 *
 * Gitignore file templates that begin with "_test" will be ignored in release version.
 * They're used in unittests.
 *
 * This class is final to allow java use some optimizations. This
 * tag will be removed if it's needed to inherit any class from
 * this class.
 *
 * @version 1.0
 */
public final class FileTemplateSource implements GitignoreSource {

    public static final String NAME = "File template source";

    protected static final String TEMPLATE_RESOURCES_FOLDER = "templates";  // do not include "/" at the end!

    // empty constructor is needed for auto source finding using reflection
    public FileTemplateSource() {
        cache = new HashMap<>();
    }

    @Override
    public String getName() {
        return NAME;
    }

    Map<String, Map.Entry<Source, String>> cache;

    /**
     * This method finds comment line in given content of .gitignore template,
     * parses its type from this line and deleted it.
     *
     * e.g. it will return TemplateType.IDE for content that has "# !type IDE" line.
     * Spaces will be ignored and line will be returned.
     *
     * @param content content of .gitignore template to examine.
     * @return type parsed from content
     */
    protected TemplateType getTypeFromContentAndRemoveTypeNotice(StringBuilder content) {
        String[] lines = content.toString().split("\\n");
        for (String line : lines) {
            if (line.trim().startsWith("#") && line.contains("!type")) {
                int typeEnd = line.indexOf("!type") + 6;
                String typeStr = line.substring(typeEnd).trim();

                content.replace(content.indexOf(line), line.length() + 1, "");  // + 1 to delete \n too

                return TemplateType.valueOf(typeStr);
            }
        }
        return TemplateType.OTHER;
    }

    /**
     * Parse template source from given path to template in resources.
     * Type will be parsed from file's !type comment.
     *
     * Content of parsed template will be saved in cache.
     *
     * @param filepath path to template in resources.
     * @param name name of template, usually filename without extension.
     * @return parses Source class instance for given template.
     * @throws IOException if there was errors while reading template content.
     */
    protected Source parseTemplateFromFileAndLoadContentToCache(String filepath, String name) throws IOException {
        if (cache.containsKey(filepath))
            return cache.get(filepath).getKey();
        else {
            Source src = new Source(name, filepath, TemplateType.OTHER, FileTemplateSource.class);
            ClassLoader classLoader = FileTemplateSource.class.getClassLoader();

            URL template = classLoader.getResource(filepath);
            if (template == null)
                throw new FileNotFoundException("Could not find template in resources: " + filepath);
            File templateFile = new File(template.getFile());
            String contentStr = new String(Files.readAllBytes(templateFile.toPath()));
            StringBuilder content = new StringBuilder(contentStr);

            src.type = getTypeFromContentAndRemoveTypeNotice(content);

            cache.put(filepath, Map.entry(src, content.toString()));

            return src;
        }
    }

    protected static final String templateFileRegex = "\\/([a-zA-Z0-9\\s_\\\\.\\-\\(\\):])+(.gitignore)$";

    /**
     * Load all templates from resources to cache and return set of their sources.
     *
     * @return set of Source class instances for all templates in resources.
     * @throws IOException if there was errors while reading template contents.
     */
    protected Set<Source> parseAllSourcesFromResources() throws IOException {
        assert !TEMPLATE_RESOURCES_FOLDER.endsWith("/") : "path to resource folder with templates should not ";

        Reflections reflections = new Reflections("me.nikitaserba", new ResourcesScanner());
        Pattern templateFilePattern = Pattern.compile(TEMPLATE_RESOURCES_FOLDER + templateFileRegex);
        Set<String> templates = reflections.getResources(templateFilePattern);

        Set<Source> sources = new HashSet<>();
        for (String template : templates) {
            sources.add(parseTemplateFromFileAndLoadContentToCache(
                    TEMPLATE_RESOURCES_FOLDER + "/" + template,
                    templateFilePattern.matcher(template).group(0)));
        }

        return sources;
    }

    Set<Source> allSourcesCache;

    /**
     * Get all Source class instances for all templates in resources.
     *
     * Templates content will be stored in cache.
     *
     * @return unmodifiable set of all sources for all templates in resources.
     * @throws TemplateParsingException if there was IOException while loading templates.
     */
    @Override
    public Set<Source> getAllSources() throws TemplateParsingException {
        if (allSourcesCache != null)
            return Collections.unmodifiableSet(allSourcesCache);

        try {
            allSourcesCache = parseAllSourcesFromResources();
            return Collections.unmodifiableSet(allSourcesCache);
        } catch (IOException e) {
            throw new TemplateParsingException("Could not load template: " + e, e);
        }
    }

    Map<TemplateType, Set<Source>> sourcesByTypeCache;

    /**
     * Get all Source class instances for all templates with certain type in resources.
     *
     * Templates content will be stored in cache.
     *
     * @param type type which be used as search criteria.
     * @return unmodifiable set of all sources for all templates with given type in resources.
     * @throws TemplateParsingException if there was TemplateParsingException while loading all sources.
     */
    @Override
    public Set<Source> getSourcesByType(TemplateType type) throws TemplateParsingException {
        if (sourcesByTypeCache != null && sourcesByTypeCache.containsKey(type))
            return Collections.unmodifiableSet(sourcesByTypeCache.get(type));

        if (allSourcesCache == null)
            getAllSources();  // load them to cache

        if (sourcesByTypeCache == null)
            sourcesByTypeCache = new EnumMap<>(TemplateType.class);

        Set<Source> sourcesByGivenType = allSourcesCache.stream()
                .filter(source -> source.getType() == type)
                .collect(Collectors.toSet());
        sourcesByTypeCache.put(type, sourcesByGivenType);

        return Collections.unmodifiableSet(sourcesByGivenType);
    }

    protected static GitignoreTemplate buildTemplateByContent(Source source, String content) {
        return new GitignoreTemplate(source.name, source.type, source.data, content);
    }

    @Override
    public GitignoreTemplate parse(Source source) throws TemplateParsingException {
        if (allSourcesCache == null || !cache.containsKey(source.data)) {
            try {
                parseTemplateFromFileAndLoadContentToCache(source.data, source.name);
            } catch (IOException e) {
                throw new TemplateParsingException(e.getMessage(), e);
            }
        }

        assert cache.containsKey(source.data) : "loadContentOfTemplateToCache did not loaded content to cache" +
                " but exited normaly";

        Map.Entry<Source, String> dataFromCache = cache.get(source.data);
        return buildTemplateByContent(dataFromCache.getKey(), dataFromCache.getValue());
    }

    /**
     * Parse all templates from resources using parse() method.
     *
     * @return Unmodifiable set with all templates from resources
     * @throws TemplateParsingException if there was errors while parsing templates
     */
    @Override
    public Set<GitignoreTemplate> parseAll() throws TemplateParsingException {
        if (cache == null)
            getAllSources(); // load all to cache
        
        Set<GitignoreTemplate> templates = new HashSet<>();
        for (Map.Entry<String, Map.Entry<Source, String>> contentInCache : cache.entrySet()) {
            templates.add(parse(contentInCache.getValue().getKey()));
        }

        return Collections.unmodifiableSet(templates);
    }
}
