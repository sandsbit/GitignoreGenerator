/*
 * GitignoreGenerator
 * https://github.com/sandsbit/GitignoreGenerator
 *
 * Copyright (C) 2020 Nikita Serba. All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published
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

/**
 * General interface for all classes, that provide
 * .gitignore file templates.
 *
 * @version 1.0
 */
public interface GitignoreSource {

    /**
     * Store information about a source (url, file, etc.),
     * that will be used for parsing a template.
     *
     * Source data (file, url, etc.) is stored in field `data` and
     * template name is stored in field `name`.
     */
    final class Source {
        protected String name;
        protected String data;

        public Source(String name, String data) {
            this.name = name;
            this.data = data;
        }
    }

    /**
     * Return source name (e.g. "github/.gitignore parser" or "file template parser"
     *
     * @return full source name
     */
    String getName();

    /**
     * Get all available sources (URLs, file paths, etc.)
     *
     * @return list of `Source` objects which contain all available sources.
     */
    Source[] getAllSources();

    GitignoreTemplate parse(String source);
    GitignoreTemplate parse(Source source);
    GitignoreTemplate[] parseAll();

}
