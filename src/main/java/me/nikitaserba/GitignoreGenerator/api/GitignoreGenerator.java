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
import me.nikitaserba.GitignoreGenerator.api.sources.GitignoreSource;
import me.nikitaserba.GitignoreGenerator.api.sources.Source;
import me.nikitaserba.GitignoreGenerator.api.templates.TemplateType;
import org.reflections.Reflections;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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

    protected HashSet<Source> templateSources;  // store all templates that will be used to generate .gitignore

    public GitignoreGenerator() throws IOException, GitignoreSourceLoadException {
        loadAppUsageNotice();
        loadGitignoreSources();
    }

    public GitignoreGenerator(String customHeader) throws IOException, GitignoreSourceLoadException {
        loadAppUsageNotice();
        loadGitignoreSources();
        this.customHeader = customHeader;
        this.useCustomHeader = true;
    }

    public GitignoreGenerator(String header, String headerDescription)
            throws IOException, GitignoreSourceLoadException {
        loadAppUsageNotice();
        loadGitignoreSources();
        this.header = header;
        this.headerDescription = headerDescription;
        this.useCustomHeader = false;
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

    /**
     * Get all sources from all loaded .gitignore Sources
     *
     * @return array of all sources
     */
    public Source[] getAllSources() {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Get all sources with certain type from all loaded .gitignore Sources
     *
     * @return array of all sources with given type
     */
    public Source[] getAllSourcesByType(TemplateType type) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Add template source to list of all template sources after given position `pos`.
     * 
     * @param source source to be added to templates list.
     * @param pos position after whick source should be implaced.
     */
    public void loadTemplate(Source source, int pos) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Add template source to the end of list of all template sources.
     *
     * @param source source to be added to templates list.
     */
    public void loadTemplate(Source source) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Remove template source with given index `i`.
     * 
     * @param i index of template to be removed.
     */
    public void removeTemplateAt(int i) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Remove given template from list of template sources.
     * 
     * @param source template source to be removed.
     */
    public void removeTemplate(Source source) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Get all template sources from list of template sources.
     * 
     * @return set of all template sources.
     */
    public Set<Source[]> getAllTemplates() {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Get all template sources with certain type from list of template sources.
     *
     * @return set of all template sources with given type.
     */
    public Set<Source[]> getAllTemplatesByType(TemplateType type) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Check is template sources in list of template sources.
     *
     * @param source source to search for.
     * @return true if source is in list of template sources, false otherwise.
     */
    public boolean isTemplateInList(Source source) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Generate final .gitignore file from templates.
     *
     * @return content of generated .gitignore.
     */
    public String generateGitignoreFile() {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Generate .gitignore using `generateGitignoreFile` method and pass
     * result to given stream.
     *
     * @param stream stream to pass result in.
     */
    public void generateGitignoreFileToStream(OutputStream stream) {
        throw new UnsupportedOperationException("Not implemented");
    }

}
