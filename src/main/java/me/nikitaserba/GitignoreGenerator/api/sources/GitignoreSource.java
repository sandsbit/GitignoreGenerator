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

import me.nikitaserba.GitignoreGenerator.api.templates.GitignoreTemplate;
import me.nikitaserba.GitignoreGenerator.api.templates.TemplateType;

import java.util.Set;

/**
 * General interface for all classes, that provide
 * .gitignore file templates.
 *
 * @version 1.0
 */
public interface GitignoreSource {

    /**
     * Return source name (e.g. "github/.gitignore parser" or "file template parser".
     *
     * @return full source name.
     */
    String getName();

    /**
     * Get all available sources (URLs, file paths, etc.)
     *
     * @return set of `Source` objects which contain all available sources.
     */
    Set<Source> getAllSources();

    /**
     * Get all available sources (URLs, file paths, etc.) by given type.
     *
     * @param type type which be used as search criteria.
     * @return set with all sources of given type.
     */
    Set<Source> getSourcesByType(TemplateType type);

    GitignoreTemplate parse(String source);
    GitignoreTemplate parse(Source source);
    GitignoreTemplate[] parseAll();

}
