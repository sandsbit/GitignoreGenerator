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

package me.nikitaserba.GitignoreGenerator.api;

import me.nikitaserba.GitignoreGenerator.api.exceptions.GitignoreSourceLoadException;
import me.nikitaserba.GitignoreGenerator.api.exceptions.GitignoreSourceNotFoundException;
import me.nikitaserba.GitignoreGenerator.api.exceptions.TemplateParsingException;
import me.nikitaserba.GitignoreGenerator.api.sources.GitignoreSource;
import me.nikitaserba.GitignoreGenerator.api.sources.Source;
import me.nikitaserba.GitignoreGenerator.api.templates.GitignoreTemplate;
import me.nikitaserba.GitignoreGenerator.api.templates.TemplateType;
import org.reflections.Reflections;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Main class of GitignoreGenerator API.
 *
 * Used for configuring and building final .gitignore file.
 *
 * This class is final to allow java use some optimizations. This
 * tag will be removed if it's needed to inherit any class from
 * this class.
 *
 */
final public class GitignoreGenerator {

    protected String customHeader;

    protected String header;
    protected String headerDescription;

    protected boolean useCustomHeader;

    protected String anotherGitignoreData;

    protected static final String NOTICE_RESOURCE_FILENAME = "text/app_using_notice.txt";
    protected String appUsageNotice;

    /**
     * Load app usage notice from resources.
     *
     * Should be called from all constructors.
     *
     * @throws IOException if usage notice file cannot be found or read.
     */
    protected void loadAppUsageNotice() throws IOException {
        ClassLoader classLoader = GitignoreGenerator.class.getClassLoader();

        URL noticeFileResource = classLoader.getResource(NOTICE_RESOURCE_FILENAME);
        if (noticeFileResource == null)
            throw new FileNotFoundException("Missing app usage notice file in resources.");
        File noticeFile = new File(noticeFileResource.getFile());

        appUsageNotice = new String(Files.readAllBytes(noticeFile.toPath()));
    }

    protected GitignoreSource[] gitignoreSources;

    /**
     * Create instances of all .gitignore sources.
     * All classes that implement `GitignoreSource` are loaded.
     *
     * Should be called from all constructors.
     *
     * @throws GitignoreSourceLoadException if cannot create .gitignore source instance
     */
    protected void loadGitignoreSources() throws GitignoreSourceLoadException {
        Reflections reflections = new Reflections("me.nikitaserba");
        Set<Class<? extends GitignoreSource>> gitignoreSourceClasses = reflections.getSubTypesOf(GitignoreSource.class);

        gitignoreSources = new GitignoreSource[gitignoreSources.length];
        int i = -1;
        for (Class<? extends GitignoreSource> sourceClass : gitignoreSourceClasses) {
            try {
                gitignoreSources[++i] = sourceClass.getDeclaredConstructor().newInstance();
            } catch (NoSuchMethodException e) {
                throw new GitignoreSourceLoadException(
                        "Invalid gitignore source " + sourceClass + ": constructor without arguments not found!",
                        e);
            } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
                throw new GitignoreSourceLoadException(
                        "Cannot create " + sourceClass + " instance: " + e,
                        e);
            }
        }
    }

    protected List<Source> templateSources;  // store all templates that will be used to generate .gitignore

    public GitignoreGenerator() throws IOException, GitignoreSourceLoadException {
        loadAppUsageNotice();
        loadGitignoreSources();
        this.templateSources = new ArrayList<>();
    }

    public GitignoreGenerator(String customHeader) throws IOException, GitignoreSourceLoadException {
        loadAppUsageNotice();
        loadGitignoreSources();
        this.customHeader = customHeader;
        this.useCustomHeader = true;
        this.templateSources = new ArrayList<>();
    }

    public GitignoreGenerator(String header, String headerDescription)
            throws IOException, GitignoreSourceLoadException {
        loadAppUsageNotice();
        loadGitignoreSources();
        this.header = header;
        this.headerDescription = headerDescription;
        this.useCustomHeader = false;
        this.templateSources = new ArrayList<>();
    }

    public String getCustomHeader() {
        return customHeader;
    }

    public void setCustomHeader(String customHeader) {
        this.customHeader = customHeader;
        this.useCustomHeader = true;
    }

    public void setHeader(String header, String headerDescription) {
        this.header = header;
        this.headerDescription = headerDescription;
        this.useCustomHeader = false;
    }

    public String getHeader() {
        return header;
    }

    public String getHeaderDescription() {
        return headerDescription;
    }

    public boolean isUseCustomHeader() {
        return useCustomHeader;
    }

    public void setUseCustomHeader(boolean useCustomHeader) {
        this.useCustomHeader = useCustomHeader;
    }

    public String getAnotherGitignoreData() {
        return anotherGitignoreData;
    }

    public void setAnotherGitignoreData(String anotherGitignoreData) {
        this.anotherGitignoreData = anotherGitignoreData;
    }

    private Set<Source> allSourcesCache;

    /**
     * Get all sources from all loaded .gitignore Sources.
     *
     * This method will try to use data from cache.
     *
     * @return unmodifiable set of all sources
     */
    public Set<Source> getAllSources() throws TemplateParsingException {
        if (allSourcesCache != null)
            return Collections.unmodifiableSet(allSourcesCache);

        allSourcesCache = new HashSet<>();
        for (GitignoreSource gitignoreSource : gitignoreSources) {
            allSourcesCache.addAll(gitignoreSource.getAllSources());
        }
        return Collections.unmodifiableSet(allSourcesCache);
    }

    private Map<TemplateType, Set<Source>> allSourcesByTypeCache;

    /**
     * Get all sources with certain type from all loaded .gitignore Sources
     *
     * This method will try to use data from cache.
     *
     * @return unmodifiable set of all sources with given type
     */
    public Set<Source> getAllSourcesByType(TemplateType type) throws TemplateParsingException {
        if (allSourcesByTypeCache != null && allSourcesByTypeCache.containsKey(type))
            return Collections.unmodifiableSet(allSourcesByTypeCache.get(type));

        if (allSourcesCache != null) {
            if (allSourcesByTypeCache == null)
                allSourcesByTypeCache = new EnumMap<>(TemplateType.class);

            Set<Source> sourcesByGivenType = allSourcesCache.stream()
                    .filter(src -> src.getType() == type)
                    .collect(Collectors.toSet());
            allSourcesByTypeCache.put(type, sourcesByGivenType);

            return Collections.unmodifiableSet(sourcesByGivenType);
        }

        HashSet<Source> sourcesByGivenType = new HashSet<>();

        for (GitignoreSource gitignoreSource : gitignoreSources) {
            sourcesByGivenType.addAll(gitignoreSource.getSourcesByType(type));
        }

        return Collections.unmodifiableSet(sourcesByGivenType);
    }

    /**
     * Add template source to list of all template sources after given position `pos`.
     * 
     * @param source source to be added to templates list.
     * @param pos position after whick source should be implaced.
     */
    public void loadTemplate(Source source, int pos) {
        templateSources.add(pos, source);
    }

    /**
     * Add template source to the end of list of all template sources.
     *
     * @param source source to be added to templates list.
     */
    public void loadTemplate(Source source) {
        templateSources.add(source);
    }

    /**
     * Remove template source with given index `i`.
     * 
     * @param i index of template to be removed.
     */
    public void removeTemplateAt(int i) {
        templateSources.remove(i);
    }

    /**
     * Remove given template from list of template sources.
     * 
     * @param source template source to be removed.
     */
    public void removeTemplate(Source source) {
        templateSources.remove(source);
    }

    /**
     * Get all template sources from list of template sources.
     * 
     * @return unmodifiable list of all template sources.
     */
    public List<Source> getAllTemplates() {
        return Collections.unmodifiableList(templateSources);
    }

    /**
     * Get all template sources with certain type from list of template sources.
     *
     * @return unmodifiable list all template sources with given type.
     */
    public List<Source> getAllTemplatesByType(TemplateType type) {
        return templateSources.stream()
                .filter(src -> src.getType() == type)
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Check is template sources in list of template sources.
     *
     * @param source source to search for.
     * @return true if source is in list of template sources, false otherwise.
     */
    public boolean isTemplateInList(Source source) {
        return templateSources.contains(source);
    }

    /**
     * Generate and add header to given StringBuilder.
     *
     * @param gitignore StringBuilder to emplace data to.
     */
    protected void generateHeader(StringBuilder gitignore) {
        if (useCustomHeader) {
            gitignore.append(customHeader).append('\n');
        } else {
            if (header != null && !header.trim().isEmpty()) {
                gitignore.append("## ").append(header.trim()).append('\n');
            }
            if (headerDescription != null && !headerDescription.trim().isEmpty()) {
                gitignore.append("## ").append(headerDescription.trim()).append('\n');
            }
            gitignore.append(appUsageNotice).append('\n');
        }
    }

    /**
     * Get template by its source using right GitignoreSource.
     *
     * @param source source to use to get template.
     * @return template by given source.
     */
    protected GitignoreTemplate getTemplateBySource(Source source) throws GitignoreSourceNotFoundException {
        for (GitignoreSource gitignoreSource : gitignoreSources) {
            if (gitignoreSource.getClass().equals(source.getGitignoreSource())) {
                return gitignoreSource.parse(source);
            }
        }
        throw new GitignoreSourceNotFoundException(source.getGitignoreSource().toString());
    }

    /**
     * Get templates by their sources and add them to StringBuilder.
     *
     * @param gitignore StringBuilder to emplace data to.
     */
    protected void emplaceTemplates(StringBuilder gitignore) throws GitignoreSourceNotFoundException {
        for (Source source : templateSources) {
            GitignoreTemplate template = getTemplateBySource(source);

            gitignore.append("\n# ").append(template.getName()).append('\n');
            gitignore.append("# ").append(template.getSourceData()).append("\n\n");
            gitignore.append(template.getContent()).append('\n');
        }
    }

    /**
     * Add custom .gitignore fields to StringBuilder.
     *
     * @param gitignore StringBuilder to emplace data to.
     */
    protected void emplaceAnotherGitignoreData(StringBuilder gitignore) throws GitignoreSourceNotFoundException {
        if (anotherGitignoreData != null && !anotherGitignoreData.trim().isEmpty()) {
            gitignore.append("\n# Other\n\n").append(anotherGitignoreData.trim());
        }
    }

    /**
     * Generate final .gitignore file from templates.
     *
     * @return content of generated .gitignore.
     */
    public String generateGitignoreFile() throws GitignoreSourceNotFoundException {
        if (useCustomHeader && customHeader == null)
            throw new NullPointerException("useCustomHeader is set to true, but no custom header provided");

        StringBuilder gitignore = new StringBuilder();

        generateHeader(gitignore);

        emplaceTemplates(gitignore);

        emplaceAnotherGitignoreData(gitignore);

        return gitignore.toString();
    }

    /**
     * Generate .gitignore using `generateGitignoreFile` method and pass
     * result to given stream.
     *
     * @param stream stream to pass result in.
     */
    public void generateGitignoreFileToStream(OutputStream stream)
            throws IOException, GitignoreSourceNotFoundException {
        String content = generateGitignoreFile();
        try (OutputStreamWriter streamWriter = new OutputStreamWriter(stream)) {
            streamWriter.write(content);
        }
    }

}
